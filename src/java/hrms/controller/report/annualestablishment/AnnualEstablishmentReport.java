/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.controller.report.annualestablishment;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import hrms.common.ValueComparator;
import hrms.dao.report.annualestablishment.AnnuaiEstablishmentReportDAO;
import hrms.model.login.LoginUserBean;
import hrms.model.report.annualestablishmentreport.AnnualEstablishment;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Surendra
 */
@SessionAttributes("LoginUserBean")
@Controller
public class AnnualEstablishmentReport implements ServletContextAware {

    private ServletContext context;

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.context = servletContext;
    }

    @Autowired
    AnnuaiEstablishmentReportDAO annuaiEstablishmentDao;

    @RequestMapping(value = "aerstatuslist.htm")
    public ModelAndView deptWiseAerStatus(HttpServletResponse response, @ModelAttribute("LoginUserBean") LoginUserBean lub, @ModelAttribute("command") AnnualEstablishment ae) {
        ModelAndView mav = null;
        mav = new ModelAndView("report/DepartmentWiseAerStatus", "aerstatuslist", ae);
        List fylist = annuaiEstablishmentDao.getFinancialYearList();
        mav.addObject("financialYearList", fylist);
        if (ae.getFy() != null && !ae.getFy().equals("")) {
            List aerStatusList = annuaiEstablishmentDao.departmentWiseAerStatus(ae.getFy());
            mav.addObject("aerStatusList", aerStatusList);
        }

        return mav;

    }

    @RequestMapping(value = "aerreportsubmittedofflist.htm")
    public ModelAndView aerSubmittedOfficeList(HttpServletResponse response, @ModelAttribute("LoginUserBean") LoginUserBean lub, @RequestParam("deptCode") String deptCode) {
        ModelAndView mav = null;
        mav = new ModelAndView("report/AerSubmittedOfficeList");
        List offList = annuaiEstablishmentDao.aerSubmittedOfficeList(deptCode);
        mav.addObject("offList", offList);
        return mav;

    }

    @RequestMapping(value = "displayReportList")
    public ModelAndView displayReportList(HttpServletResponse response, @ModelAttribute("LoginUserBean") LoginUserBean lub, @ModelAttribute("command") AnnualEstablishment ae, @RequestParam Map<String, String> parameters) {
        ModelAndView mav = null;
        mav = new ModelAndView("report/AnnualEstablishmentReportList", "command", ae);
        List<AnnualEstablishment> submitList = annuaiEstablishmentDao.getAerReportList(lub.getOffcode());
        annuaiEstablishmentDao.submittedforCurrentFinancialYear(lub.getOffcode(), ae);

        String status = ae.getStatus();
        String stat = "";
        System.out.println("Status is: " + status);
        if (status != null && !status.equals("")) {
            mav = new ModelAndView("report/AnnualEstablishmentReportList", "command", ae);
            if (status.equals("YES")) {
                stat = "YES";
            } else if (status.equals("REV")) {
                stat = "REV";
            }
            mav.addObject("visible", stat);
            mav.addObject("OffName", lub.getOffname());
            mav.addObject("MasterList", submitList);
        } else {
            stat = "NO";
            mav = new ModelAndView("redirect:/displayEstablishmentReport.htm?fy=" + ae.getFy(), "command", ae);
        }
        return mav;
    }

    @RequestMapping(value = "displayEstablishmentReport")
    public ModelAndView displayEstablishmentReport(HttpServletResponse response, @ModelAttribute("LoginUserBean") LoginUserBean lub, @ModelAttribute("command") AnnualEstablishment ae, @RequestParam Map<String, String> parameters) {
        ModelAndView mav = null;
        mav = new ModelAndView("report/AnnualEstablishmentReport", "command", ae);
        List<AnnualEstablishment> submitList = annuaiEstablishmentDao.getSubmittedReportList(lub.getOffcode(), ae.getFy());

        Map<String, String> map = annuaiEstablishmentDao.getAuthorityList(lub.getOffcode());

        Map<String, String> sortedMap = new TreeMap<String, String>(new ValueComparator(map));
        sortedMap.putAll(map);
        //System.out.println("submitList size is: "+submitList.size());
        if (submitList.size() > 0) {
            mav.addObject("EstablishmentList", submitList);
            mav.addObject("submitted", "Y");

        } else {
            List<AnnualEstablishment> li = annuaiEstablishmentDao.getAnnualEstablistmentReportList(lub.getOffcode());
            mav.addObject("EstablishmentList", li);
            mav.addObject("AuthListArray", sortedMap);
            mav.addObject("submitted", "N");
        }
        annuaiEstablishmentDao.submittedforCurrentFinancialYear(lub.getOffcode(), ae);
        /*System.out.println("Status is: "+ae.getStatus());
         if (ae.getStatus() != null && ae.getStatus().equals("YES")) {
         mav = new ModelAndView("redirect:/displayReportList.htm", "command", ae);
         }*/
        mav.addObject("OffName", lub.getOffname());
        return mav;
    }

    @RequestMapping(value = "submitEstablishmentReport", params = {"btnAer=Submit"})
    public ModelAndView submitEstablishmentReport(HttpServletResponse response, @ModelAttribute("LoginUserBean") LoginUserBean lub, @ModelAttribute("command") AnnualEstablishment ae, @RequestParam Map<String, String> C) {
        ModelAndView mav = null;
        Document document = new Document();
        String fy = "";
        try {
            List<AnnualEstablishment> li = annuaiEstablishmentDao.getAnnualEstablistmentReportList(lub.getOffcode());
            String serverfilePath = context.getInitParameter("AERPath");
            String filePath = serverfilePath + "AER_" + lub.getOffcode() + ".pdf";

            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.setPageSize(PageSize.A4.rotate());
            document.open();

            PdfPTable table = new PdfPTable(11);
            table.setWidths(new float[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1});
            table.setWidthPercentage(100);
            int i = 1;

            System.out.println("Office Name is: " + lub.getOffname());

            PdfPCell cell = new PdfPCell(new Phrase(lub.getOffname()));
            cell.setColspan(11);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("ANNUAL ESTABLISHMENT REVIEW REPORT"));
            cell.setColspan(11);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            table.addCell("Sl No");
            table.addCell("Posts");
            table.addCell("Group");
            table.addCell("Scale of Pay (6th Pay)");
            table.addCell("Grade Pay");
            table.addCell("Scale of Pay (7th Pay) ");
            table.addCell("Level in the Pay Matrix as per ORSP Rules, 2017");
            table.addCell("Sanctioned Strength ");
            table.addCell("Men in Position");
            table.addCell("Vacancy Position");
            table.addCell("Remarks");

            table.addCell("1");
            table.addCell("2");
            table.addCell("3");
            table.addCell("4");
            table.addCell("5");
            table.addCell("6");
            table.addCell("7");
            table.addCell("8");
            table.addCell("9");
            table.addCell("10");
            table.addCell("11");
            ae.setOffCode(lub.getOffcode());
            fy = ae.getFy();
            annuaiEstablishmentDao.addAERMaster(ae, lub.getEmpid());
            for (AnnualEstablishment aer : li) {
                annuaiEstablishmentDao.addAEReportData(aer, lub.getEmpid(), fy);

                table.addCell(i + "");
                table.addCell(aer.getPostname());
                table.addCell(aer.getGroup());
                table.addCell(aer.getScaleofPay());
                table.addCell(aer.getGp());
                table.addCell(aer.getScaleofPay7th());
                table.addCell(aer.getLevel());
                table.addCell(aer.getSanctionedStrength() + "");
                table.addCell(aer.getMeninPosition() + "");
                table.addCell(aer.getVacancyPosition() + "");
                table.addCell("");

                i++;

            }

            document.add(table);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            document.close();
        }

        mav = new ModelAndView("redirect:/displayReportList.htm?fy=" + fy, "command", ae);

        return mav;

    }

    @RequestMapping(value = "downloadEstablishmentReport")
    public void downloadReport(HttpServletRequest request, HttpServletResponse response, @ModelAttribute("LoginUserBean") LoginUserBean lub) {

        String serverfilePath = context.getInitParameter("AERPath");

        String fileName = "AER_" + lub.getOffcode() + ".pdf";
        Path file = Paths.get(serverfilePath, fileName);
        if (Files.exists(file)) {
            response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
            try {
                Files.copy(file, response.getOutputStream());
                response.getOutputStream().flush();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @RequestMapping(value = "submitEstablishmentReport", params = {"btnAer=Return"})
    public ModelAndView returnReport(HttpServletRequest request, HttpServletResponse response, @ModelAttribute("LoginUserBean") LoginUserBean lub, @ModelAttribute("command") AnnualEstablishment ae) {

        return new ModelAndView("redirect:/displayReportList.htm", "command", ae);
    }

    @RequestMapping(value = "authApprovedAER")
    public ModelAndView approve(HttpServletRequest request, HttpServletResponse response, @ModelAttribute("LoginUserBean") LoginUserBean lub, @ModelAttribute("command") AnnualEstablishment ae, @RequestParam Map<String, String> parameters) {

        String taskId = parameters.get("taskId");
        ae.setTaskid(Integer.parseInt(taskId));
        String submitted = annuaiEstablishmentDao.getAERStatus(Integer.parseInt(taskId));
        ModelAndView mav = new ModelAndView("/report/AuthReportViewPage", "command", ae);
        List<AnnualEstablishment> li = annuaiEstablishmentDao.getAnnualEstablistmentReportListFromAuthLogin(taskId);
        mav.addObject("EstablishmentList", li);
        mav.addObject("submitted", submitted);
        return mav;
    }

    @RequestMapping(value = "approvedAERByAuth", params = {"btnAer=Approve"})
    public ModelAndView approvedAERByAuth(HttpServletRequest request, HttpServletResponse response, @ModelAttribute("LoginUserBean") LoginUserBean lub, @ModelAttribute("command") AnnualEstablishment ae) {

        annuaiEstablishmentDao.approvedAER(ae.getTaskid());

        //ModelAndView mav = new ModelAndView("/report/AuthReportViewPage?taskId="+ae.getTaskid(), "command", ae);
        ModelAndView mav = new ModelAndView();
        mav.addObject("taskId", ae.getTaskid());
        return new ModelAndView("redirect:/authApprovedAER.htm?taskId=" + ae.getTaskid(), "command", ae);

        //return mav;
    }

    @RequestMapping(value = "approvedAERByAuth", params = {"btnAer=Revert"})
    public ModelAndView Revert(HttpServletRequest request, HttpServletResponse response, @ModelAttribute("LoginUserBean") LoginUserBean lub, @ModelAttribute("command") AnnualEstablishment ae) {

        String serverfilePath = context.getInitParameter("AERPath");
        String fileName = "AER_" + lub.getOffcode() + ".pdf";

        annuaiEstablishmentDao.revertAER(ae.getTaskid(), serverfilePath, fileName);

        ModelAndView mav = new ModelAndView();
        mav.addObject("taskId", ae.getTaskid());
        return new ModelAndView("redirect:/authApprovedAER.htm?taskId=" + ae.getTaskid(), "command", ae);
    }

    @RequestMapping(value = "scheduleIIReport")
    public String ScheduleIIReport(Model model, @ModelAttribute("LoginUserBean") LoginUserBean lub) {

        List listdata = annuaiEstablishmentDao.getScheduleIIData(lub.getOffcode());
        model.addAttribute("data", listdata);
        return "report/AERReportScheduleII";

    }

}
