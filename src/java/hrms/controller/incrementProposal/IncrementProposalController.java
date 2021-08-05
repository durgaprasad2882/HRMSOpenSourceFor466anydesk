/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.controller.incrementProposal;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import hrms.SelectOption;
import hrms.common.CommonFunctions;
import hrms.common.HeaderFooterPageEvent;
import hrms.dao.incrementProposal.IncrementProposalDAO;
import hrms.dao.login.LoginDAOImpl;
import hrms.dao.master.DepartmentDAO;
import hrms.dao.master.OfficeDAO;
import hrms.dao.master.ProcessStatusDAO;
import hrms.dao.notification.MaxNotificationIdDAO;
import hrms.dao.notification.NotificationDAO;
import hrms.dao.master.SubStantivePostDAO;
import hrms.model.master.Department;
import hrms.model.incrementProposal.IncrementProposal;
import hrms.model.incrementProposal.ProposalAttr;
import hrms.model.incrementProposal.ProposalMaster;
import hrms.model.login.LoginUserBean;
import hrms.model.login.Users;
import hrms.model.master.Office;
import hrms.model.master.SubstantivePost;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Surendra
 */
@Controller
@SessionAttributes("LoginUserBean")
public class IncrementProposalController {

    @Autowired
    public LoginDAOImpl loginDAO;

    @Autowired
    public DepartmentDAO departmentDao;

    @Autowired
    public OfficeDAO offDAO;

    @Autowired
    public SubStantivePostDAO substantivePostDAO;

    @Autowired
    public IncrementProposalDAO incrementProposalDao;

    @Autowired
    public NotificationDAO NotificationDAO;

    @Autowired
    public MaxNotificationIdDAO maxnotiidDao;

    @Autowired
    ProcessStatusDAO processStatusDAO;

    @RequestMapping(value = "displayProposalListpage", method = RequestMethod.GET)
    public String ProposalOrderList(ModelMap model, @ModelAttribute("IncrementProposal") IncrementProposal incr, @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response) {

        String path = "/incrementProposal/IncrementProposalList";
        //NotificationBean nfb=new NotificationBean();
        //nfb.setNotid(maxnotiidDao.getMaxNotId());
        //nfb.setOrdDate(null);
        //NotificationDAO.insertNotificationData(nfb);
        return path;
    }

    @ResponseBody
    @RequestMapping(value = "proposalMasterListAction", method = RequestMethod.POST)
    public void getProposalList(@ModelAttribute("LoginUserBean") LoginUserBean lub, HttpServletRequest request, HttpServletResponse response, @RequestParam("page") int page, @RequestParam("rows") int rows) {
        response.setContentType("application/json");
        JSONObject json = new JSONObject();
        PrintWriter out = null;
        String[] monthName = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        try {

            List<IncrementProposal> li = incrementProposalDao.proposalList(lub.getOffcode(), page, rows);
            List jsonlist = new ArrayList();
            ProposalMaster propmast = null;
            IncrementProposal incrprop = null;
            int j = 0;
            while (j < li.size()) {
                incrprop = li.get(j);
                propmast = new ProposalMaster();
                propmast.setProposalId(incrprop.getProposalId());
                propmast.setSubmitproposalId(incrprop.getProposalId());
                propmast.setExportproposalId(incrprop.getProposalId());
                propmast.setOrdno(incrprop.getOrderno());
                propmast.setOrddate(CommonFunctions.getFormattedOutputDate3(incrprop.getOrderDate()));
                propmast.setPropMonth(monthName[incrprop.getProposalMonth() - 1]);
                propmast.setPropYear(incrprop.getProposalYear());
                propmast.setStatus(incrprop.getProcessStatusName());
                if (incrprop.getTask() != null) {
                    propmast.setTaskid(incrprop.getTask().getTaskid());
                } else {
                    propmast.setTaskid(0);
                }
                propmast.setLastUpdated(CommonFunctions.getFormattedOutputDate3(incrprop.getLastUpdatedOn()));
                jsonlist.add(propmast);
                j++;
            }
            json.put("total", jsonlist.size());
            json.put("rows", jsonlist);
            out = response.getWriter();
            out.write(json.toString());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
        }
    }

    @RequestMapping(value = "newProposalMaster", method = RequestMethod.GET)
    public ModelAndView createNewProposal(@ModelAttribute("LoginUserBean") LoginUserBean lub, @ModelAttribute("incrementForm") IncrementProposal incrementForm,
            BindingResult result, @RequestParam("proposalId") int proposalId) {
        ModelAndView mav = new ModelAndView();

        String path = "index";
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        String[] monthName = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        String crrmonth = monthName[month];
        crrmonth = crrmonth + "-" + year;
        try {
            incrementForm.setProposalYear(year);
            incrementForm.setProposalMonth(month);
            incrementForm.setMonthasString(crrmonth);
            incrementForm.setProposalId(proposalId);
            mav.addObject("incrementForm", incrementForm);
            path = "/incrementProposal/AddIncrementMasterData";

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

        mav.setViewName(path);
        return mav;
    }

    @ResponseBody
    @RequestMapping(value = "addProposalEmployeeListAction", method = RequestMethod.GET)
    public void addProposalEmployeeListAction(@ModelAttribute("LoginUserBean") LoginUserBean lub, HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json");
        JSONObject json = new JSONObject();
        PrintWriter out = null;
        List jsonlist = new ArrayList();
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        List<Users> li = null;
        ProposalAttr pro = null;
        try {

            li = incrementProposalDao.getEmployeeList(lub.getOffcode(), year, month);

            Users emp = null;
            int j = 0;
            while (j < li.size()) {
                emp = li.get(j);
                pro = new ProposalAttr();
                pro.setProposaldetailId(0);
                pro.setEmpId(emp.getEmpId());
                pro.setEmpname(emp.getFullName());
                pro.setGpfno(emp.getGpfno());
                pro.setPost(emp.getSubstantivePost().getSpn());
                pro.setPresentpaydate(CommonFunctions.getFormattedOutputDate3(emp.getPaydate()));
                pro.setNextincr(CommonFunctions.getFormattedOutputDate3(emp.getDateOfnincr()));

                jsonlist.add(pro);
                j++;
            }
            json.put("total", 50);
            json.put("rows", jsonlist);
            out = response.getWriter();
            out.write(json.toString());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
        }
    }

    @ResponseBody
    @RequestMapping(value = "proposalListAction", method = {RequestMethod.GET, RequestMethod.POST})
    public void getProposalDetail(@ModelAttribute("LoginUserBean") LoginUserBean lub, HttpServletRequest request, HttpServletResponse response, @RequestParam("proposalId") int proposalId) {
        response.setContentType("application/json");
        JSONObject json = new JSONObject();
        PrintWriter out = null;
        String[] monthName = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        try {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            month = month + 1;
            List jsonlist = new ArrayList();

            List<Users> li = null;

            ProposalAttr pro = null;

            /*
             This If block is for edit Proposal final List which is Present At HW_INCREMENT_PROPOSAL_DETAIL Table
            
             and 
            
             else block is for create new proposal list from EMP_MAST Table
             */
            if (proposalId > 0) {
                jsonlist = incrementProposalDao.getFinalProposedList(proposalId);
            } else {
                jsonlist = incrementProposalDao.getProposedEmployeeList(lub.getOffcode(), year, month);
            }
            json.put("total", 50);
            json.put("rows", jsonlist);
            out = response.getWriter();
            out.write(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
        }

    }

    @RequestMapping(value = "removeProposalList", method = {RequestMethod.GET, RequestMethod.POST})
    public void removeproposalList(ModelMap model, @ModelAttribute("IncrementProposal") IncrementProposal incr,
            @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response,
            @RequestParam("empId") String empId, @RequestParam("proposaldetailId") int proposaldetailId) {

        incrementProposalDao.deleteSelectedProposalFromList(proposaldetailId, empId);
    }

    @RequestMapping(value = "addProposedEmployee", method = RequestMethod.POST)
    public void addProposedEmployee(ModelMap model, @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response,
            @RequestParam("emplist") String emplist, @RequestParam("month") int month, @RequestParam("year") int year, @RequestParam("proposlMastId") int proposlMastId) {
        String empArr[] = emplist.split(",");
        incrementProposalDao.addProposedEmployee(empArr, year, month, proposlMastId);
    }

    @RequestMapping(value = "saveProposalList", method = RequestMethod.POST)
    public String saveProposalList(ModelMap model, @ModelAttribute("IncrementProposal") IncrementProposal incr,
            @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response,
            @RequestParam("emplist") String emplist, @RequestParam("month") int month, @RequestParam("year") int year, @RequestParam("proposalId") int proposalId) {

        String path = "/incrementProposal/IncrementProposalList";
        try {
            if (proposalId > 0) {
                incrementProposalDao.deleteProposalDetail(proposalId);
                String empArr[] = emplist.split(",");
                incrementProposalDao.saveProposalDetailList(empArr, proposalId, year, month);
            } else {
                String empArr[] = emplist.split(",");
                proposalId = incrementProposalDao.saveProposalList(lub.getOffcode(), lub.getEmpid(), lub.getSpc(), month, year);
                incrementProposalDao.saveProposalDetailList(empArr, proposalId, year, month);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return path;
    }
// http://par.hrmsodisha.gov.in/submitProposal.htm?proposalId=11

    @RequestMapping(value = "submitProposal", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView submitProposal(@ModelAttribute("IncrementProposal") IncrementProposal incr,
            @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response,
            @RequestParam("proposalId") int proposalId) {
        String path = "/incrementProposal/SubmitIncrementProposal";
        ModelAndView mav = new ModelAndView();
        try {
            incr.setProposalId(proposalId);
            mav.addObject("incrementForm", incr);
            mav.setViewName(path);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return mav;

    }

    @RequestMapping(value = "submitProposalTask", method = {RequestMethod.GET, RequestMethod.POST})
    public String submitToAuthority(@ModelAttribute("IncrementProposal") IncrementProposal incr,
            @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response) {
        String path = "/incrementProposal/IncrementProposalList";
        try {
            incrementProposalDao.submitProposal(incr.getProposalId(), lub.getEmpid(), lub.getSpc(), incr.getAuthspc());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return path;

    }

    @ResponseBody
    @RequestMapping(value = "getDepartmentListAction", method = RequestMethod.POST)
    public void getDepartmentlList(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json");
        PrintWriter out = null;
        SelectOption so = null;
        ArrayList al = new ArrayList();
        Department dept = null;
        try {
            List li = departmentDao.getDepartmentList();
            JSONArray json = new JSONArray(li);
            out = response.getWriter();
            out.write(json.toString());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            out.flush();
            out.close();
        }
    }

    @ResponseBody
    @RequestMapping(value = "getStatusList", method = RequestMethod.POST)
    public void getStatusList(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json");
        PrintWriter out = null;
        try {
            List li = processStatusDAO.getProcessStatusList();
            JSONArray json = new JSONArray(li);
            out = response.getWriter();
            out.write(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
        }

    }

    @ResponseBody
    @RequestMapping(value = "getPostListAction", method = RequestMethod.POST)
    public void getPostList(HttpServletRequest request, HttpServletResponse response,
            @RequestParam String offcode) {
        response.setContentType("application/json");
        PrintWriter out = null;
        SelectOption so = null;
        ArrayList al = new ArrayList();
        SubstantivePost post = null;
        try {

            List li = incrementProposalDao.getEmployeeWisePostList(offcode);
            Iterator<SubstantivePost> itr = li.iterator();

            while (itr.hasNext()) {
                post = (SubstantivePost) itr.next();
                so = new SelectOption();
                so.setLabel(post.getEmpname() + ", " + post.getPostname());
                so.setValue(post.getSpc());
                al.add(so);
            }
            JSONArray json = new JSONArray(al);
            out = response.getWriter();
            out.write(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
        }
    }

    @RequestMapping(value = "authorityViewProposalTask", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView authorityView(@ModelAttribute("IncrementProposal") IncrementProposal incr,
            @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response, @RequestParam("taskId") int taskId) {
        String path = "/incrementProposal/AuthorityViewPage";

        ModelAndView mav = new ModelAndView();
        try {
            int proposalId = incrementProposalDao.getProposalMasterId(taskId);
            incr.setProposalId(proposalId);
            mav.addObject("incrementForm", incr);
            mav.setViewName(path);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return mav;

    }

    @ResponseBody
    @RequestMapping(value = "authorityViewData", method = {RequestMethod.GET, RequestMethod.POST})
    public void authorityViewData(@ModelAttribute("IncrementProposal") IncrementProposal incr,
            @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response, @RequestParam("proposalId") int proposalId) {
        response.setContentType("application/json");
        JSONObject json = new JSONObject();
        PrintWriter out = null;
        try {
            List jsonlist = new ArrayList();

            ProposalAttr pro = null;

            jsonlist = incrementProposalDao.getFinalProposedList(proposalId);

            json.put("total", 50);
            json.put("rows", jsonlist);
            out = response.getWriter();
            out.write(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
        }

    }

    @RequestMapping(value = "authorityApproved", method = {RequestMethod.GET, RequestMethod.POST})
    public void authorityApprovedTask(@ModelAttribute("IncrementProposal") IncrementProposal incr,
            @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response,
            @RequestParam("propmastId") int propmastId, @RequestParam("status") int status, @RequestParam("note") String note) {
        try {
            incrementProposalDao.authorityAction(propmastId, status, note, lub.getSpc());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    @RequestMapping(value = "updateorderInfo", method = {RequestMethod.GET, RequestMethod.POST})
    public void updateorderInfo(@ModelAttribute("IncrementProposal") IncrementProposal incr,
            @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response,
            @RequestParam("propmastId") int propmastId, @RequestParam("ordno") String ordno, @RequestParam("ordDate") String ordDate) {
        try {
            incrementProposalDao.updateOrderInfo(propmastId, ordno, ordDate);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    @RequestMapping(value = "generatepdf", method = RequestMethod.GET)
    public void generatePDF(ModelMap model, @ModelAttribute("IncrementProposal") IncrementProposal incr,
            @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response, @RequestParam("proposalId") int proposalId) {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=PERIODICAL_INCREMENT.pdf");
        Document document = new Document(PageSize.A4.rotate());
        final Font titlefontSmall = FontFactory.getFont("Arial", 15, Font.BOLD | Font.UNDERLINE);
        final Font headingRule = FontFactory.getFont("Arial", 15, Font.BOLD);
        String offname = "";
        try {
            HeaderFooterPageEvent event = new HeaderFooterPageEvent();
            PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
            Rectangle rect = new Rectangle(150, 30, 550, 800);
            writer.setBoxSize("art", rect);
            writer.setPageEvent(event);
            document.open();
            /*
             rs = st.executeQuery("SELECT APPROVED_ORDER_NO, APPROVED_ORDER_DATE,APPROVED_AUTHORITY FROM HW_INCREMENT_PROPOSAL_MASTER WHERE PROPOSAL_ID=" + ifBean.getProposalMasterId());
             if (rs.next()) {
             ordno = rs.getString("APPROVED_ORDER_NO");
             ordDate = CommonFunctions.getFormattedOutputDate(rs.getDate("APPROVED_ORDER_DATE"));
             sanctionAuth=GetUserAttribute.getPost(con, rs.getString("APPROVED_AUTHORITY"));
             }
            
             */
            List li = incrementProposalDao.getFinalProposedList(proposalId);

            offname = incrementProposalDao.getOfficeName(lub.getOffcode());
            ProposalAttr pi = incrementProposalDao.getordnoSpcEtc(proposalId);
            Iterator itr = li.iterator();
            ProposalAttr emp = null;
            while (itr.hasNext()) {

                emp = (ProposalAttr) itr.next();

                Paragraph rule = new Paragraph("[See Subsidiary Rule 225]", headingRule);
                rule.setAlignment(Element.ALIGN_CENTER);
                document.add(rule);

                Paragraph heading = new Paragraph("PERIODICAL INCREMENT CERTIFICATE", titlefontSmall);
                heading.setAlignment(Element.ALIGN_CENTER);
                document.add(heading);

                String pointStr = "1. Certified that the Government Servants named below have earned the Prescribed Periodical increment from"
                        + " the date cited in column 6 having been the incumbent of the posts specified for not less than 1 (one) years from"
                        + " the date in column 5 after deducting period from misconduct, etc. and absence on leave without pay and in the case"
                        + " of those holding posts in officiating capacity all other kinds of leave.";
                Paragraph point1 = new Paragraph(pointStr);
                document.add(point1);

                String pointStr2 = "2. Certified that the Government Servants named below have earned/will earn periodical increments from"
                        + " the date cited reasons stated in the explanatory Memo. Attached hereto,";
                Paragraph point2 = new Paragraph(pointStr2);
                document.add(point2);

                Paragraph blank = new Paragraph(" ");
                document.add(blank);

                PdfPTable table = new PdfPTable(11);
                table.setWidths(new int[]{9, 4, 4, 3, 4, 4, 3, 4, 4, 4, 4});
                table.setWidthPercentage(100);

                PdfPCell cell = null;

                cell = new PdfPCell(new Phrase("Name of incumbents", new Font(FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
                //cell.setBorder(Rectangle.);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setRowspan(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("Substantive/ Officiating", new Font(FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
                //cell.setBorder(Rectangle.);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setRowspan(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("Scale of Pay of posts", new Font(FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
                //cell.setBorder(Rectangle.);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setRowspan(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("Present pay in Rs.", new Font(FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
                //cell.setBorder(Rectangle.);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setRowspan(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("Date from which present pay is drawn", new Font(FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
                //cell.setBorder(Rectangle.);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setRowspan(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("Date of present increment", new Font(FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
                //cell.setBorder(Rectangle.);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setRowspan(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("Future pay", new Font(FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
                //cell.setBorder(Rectangle.);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setRowspan(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("Suspension for misconduct and such other absence as does not count for increment", new Font(FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
                //cell.setBorder(Rectangle.);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setColspan(2);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("Leave without pay and in the case of those holding the posts in officiating capacity all other kinds of leave.", new Font(FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
                //cell.setBorder(Rectangle.);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setColspan(2);
                table.addCell(cell);

                // 2nd row
                cell = new PdfPCell(new Phrase("From", new Font(FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
                //cell.setBorder(Rectangle.);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("To", new Font(FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
                //cell.setBorder(Rectangle.);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("From", new Font(FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
                //cell.setBorder(Rectangle.);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("To", new Font(FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
                //cell.setBorder(Rectangle.);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                // 3rd row
                cell = new PdfPCell(new Phrase("1", new Font(FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
                //cell.setBorder(Rectangle.);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("2", new Font(FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
                //cell.setBorder(Rectangle.);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("3", new Font(FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
                //cell.setBorder(Rectangle.);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("4", new Font(FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
                //cell.setBorder(Rectangle.);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("5", new Font(FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
                //cell.setBorder(Rectangle.);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("6", new Font(FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
                //cell.setBorder(Rectangle.);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("7", new Font(FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
                //cell.setBorder(Rectangle.);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("8", new Font(FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
                //cell.setBorder(Rectangle.);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("9", new Font(FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
                //cell.setBorder(Rectangle.);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("10", new Font(FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
                //cell.setBorder(Rectangle.);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("11", new Font(FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
                //cell.setBorder(Rectangle.);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                //Chunk chunk1 = new Chunk(emp.getEmpname());
                //Chunk chunk2 = new Chunk(emp.getPost());
                cell = new PdfPCell(new Phrase(emp.getEmpname() + "\n" + StringUtils.defaultString(emp.getPost()), new Font(FontFamily.TIMES_ROMAN, 10, Font.BOLD)));
                //cell.setBorder(Rectangle.);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("Officiating", new Font(FontFamily.TIMES_ROMAN, 10, Font.BOLD)));
                //cell.setBorder(Rectangle.);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(emp.getPayscale(), new Font(FontFamily.TIMES_ROMAN, 10, Font.BOLD)));
                //cell.setBorder(Rectangle.);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(emp.getPresentpay(), new Font(FontFamily.TIMES_ROMAN, 10, Font.BOLD)));
                //cell.setBorder(Rectangle.);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(emp.getPresentpaydate(), new Font(FontFamily.TIMES_ROMAN, 10, Font.BOLD)));
                //cell.setBorder(Rectangle.);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(emp.getNextincr(), new Font(FontFamily.TIMES_ROMAN, 10, Font.BOLD)));
                //cell.setBorder(Rectangle.);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(emp.getFuturepay(), new Font(FontFamily.TIMES_ROMAN, 10, Font.BOLD)));
                //cell.setBorder(Rectangle.);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(" ", new Font(FontFamily.TIMES_ROMAN, 10, Font.BOLD)));
                //cell.setBorder(Rectangle.);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(" ", new Font(FontFamily.TIMES_ROMAN, 10, Font.BOLD)));
                //cell.setBorder(Rectangle.);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(" ", new Font(FontFamily.TIMES_ROMAN, 10, Font.BOLD)));
                //cell.setBorder(Rectangle.);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(" ", new Font(FontFamily.TIMES_ROMAN, 10, Font.BOLD)));
                //cell.setBorder(Rectangle.);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                document.add(table);
                /*
                 Paragraph gov = new Paragraph("Government of Odisha");
                 gov.setAlignment(Element.ALIGN_CENTER);
                 document.add(gov);
                 */
                Paragraph off = new Paragraph(offname);
                off.setAlignment(Element.ALIGN_CENTER);
                document.add(off);

                Paragraph star = new Paragraph("*******");
                star.setAlignment(Element.ALIGN_CENTER);
                document.add(star);

                Paragraph orderno = new Paragraph("Order No. " + StringUtils.defaultString(pi.getOrdno()) + "        , dated- " + pi.getOrderDate());
                orderno.setAlignment(Element.ALIGN_LEFT);
                document.add(orderno);

                Paragraph incrsanc = new Paragraph("The Increment is sanctioned.");
                incrsanc.setAlignment(Element.ALIGN_LEFT);
                document.add(incrsanc);

                PdfPTable table2 = new PdfPTable(1);
                table2.setWidths(new int[]{10});
                table2.setWidthPercentage(100);

                PdfPCell cell2 = null;

                cell2 = new PdfPCell(new Phrase(pi.getPost(), new Font(FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
                cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell2.setBorder(Rectangle.NO_BORDER);
                table2.addCell(cell2);

                document.add(table2);

                Paragraph memo = new Paragraph("Memo No.                        ,  date-                ");
                memo.setAlignment(Element.ALIGN_LEFT);
                document.add(memo);

                Paragraph cpy = new Paragraph("Copy forwarded to Accounts Section (in duplicate) / Person concerned for information and necessary action.");
                cpy.setAlignment(Element.ALIGN_LEFT);
                document.add(cpy);

                PdfPTable table3 = new PdfPTable(1);
                table3.setWidths(new int[]{10});
                table3.setWidthPercentage(100);

                PdfPCell cell3 = null;

                cell3 = new PdfPCell(new Phrase(pi.getPost(), new Font(FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
                cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell3.setBorder(Rectangle.NO_BORDER);
                table3.addCell(cell3);

                document.add(table3);

                document.newPage();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            document.close();
        }

    }

    public void onEndPage(PdfWriter writer, Document document) {
        Font ffont = new Font(Font.FontFamily.UNDEFINED, 5, Font.ITALIC);
        PdfContentByte cb = writer.getDirectContent();
        Phrase footer = new Phrase("this is a footer", ffont);

        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                footer,
                (document.right() - document.left()) / 2 + document.leftMargin(),
                document.bottom() - 10, 0);
    }

}
