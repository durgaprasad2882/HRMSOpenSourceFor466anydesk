/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.controller.payroll.billbrowser;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import hrms.common.CommonFunctions;
import hrms.dao.payroll.billbrowser.AqReportDAOImpl;
import hrms.dao.payroll.schedule.PayBillDMPDAO;
import hrms.dao.payroll.schedule.ScheduleDAO;
import hrms.dao.payroll.schedule.ScheduleDAOImpl;
import hrms.model.common.CommonReportParamBean;
import hrms.model.login.LoginUserBean;
import hrms.model.payroll.billbrowser.BillBrowserbean;
import hrms.model.payroll.billbrowser.BillChartOfAccount;
import hrms.model.payroll.billbrowser.BillObjectHead;
import hrms.model.payroll.schedule.Schedule;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Manas Jena
 */
@Controller
public class BillFrontPageController {

    @Autowired
    AqReportDAOImpl AqReportDAO;

    @Autowired
    ScheduleDAO comonScheduleDao;

    @Autowired
    public PayBillDMPDAO paybillDmpDao;

    @RequestMapping(value = "billfrontPageHTML", method = RequestMethod.GET)
    public String billfrontPageHTML(ModelMap model, @ModelAttribute("BillBrowserbean") BillBrowserbean bbBean,
            @ModelAttribute("LoginUserBean") LoginUserBean lub, @RequestParam("billNo") String billNo, BindingResult result, HttpServletResponse response) {
        CommonReportParamBean crb = null;
        BillChartOfAccount billChartOfAccount = null;
        BillObjectHead boha = null;
        ArrayList scheduleList = new ArrayList();
        Schedule obj = null;
        String path = "";
        double deductAmt = 0.0;
        String netTotaUnder="";
        double oaAmt = 0.0;
        double cpfAmt = 0.0;
        double itAmt = 0.0;
        double ptAmt = 0.0;
        String arrAll="";
         crb = paybillDmpDao.getCommonReportParameter(billNo);
        if ( crb.getBillType() != null && crb.getBillType().equals("43")) {
            path = "/payroll/arrear/ArrBillFrontPage";
           
            billChartOfAccount = AqReportDAO.getBillChartOfAccount(billNo);
            boha = AqReportDAO.getArrBillObjectHeadAndAmt(billNo);

            obj = AqReportDAO.getArrScheduleListWithADCode(billNo);
            if (obj.getCpfAmt() != null && !obj.getCpfAmt().equals("")) {
                cpfAmt = Double.parseDouble(obj.getCpfAmt());
            }
            if (obj.getPtAmt() != null && !obj.getPtAmt().equals("")) {
                ptAmt = Double.parseDouble(obj.getPtAmt());
            }
            if (obj.getItAmt() != null && !obj.getItAmt().equals("")) {
                itAmt = Double.parseDouble(obj.getItAmt());
            }

            deductAmt = cpfAmt + ptAmt + itAmt;
            oaAmt=boha.getPayamt();
             arrAll= Double.valueOf(oaAmt + "").longValue() + "";
            String totOaAmt[] = CommonFunctions.getRupessAndPaise(String.valueOf(oaAmt));
            String totDeductAmt[] = CommonFunctions.getRupessAndPaise(String.valueOf(deductAmt));

            double netAmt = oaAmt - deductAmt;
             netTotaUnder = Double.valueOf(netAmt + "").longValue() + "";
            String netAmount[] = CommonFunctions.getRupessAndPaise(String.valueOf(netAmt));

            model.addAttribute("billChartOfAccount", billChartOfAccount);
            //model.addAttribute("scheduleList", scheduleList);
            // model.addAttribute("OAList", oaList);
            model.addAttribute("cpfAmt", obj.getCpfAmt());
            model.addAttribute("ptAmt", obj.getPtAmt());
            model.addAttribute("itAmt", obj.getItAmt());
            model.addAttribute("itObjHead", obj.getItObjHead());
            model.addAttribute("cpfObjHead", obj.getCpfObjHead());
            model.addAttribute("ptfObjHead", obj.getPtObjHead());
            model.addAttribute("TotOaAmt", arrAll);
            //model.addAttribute("TotOaAmtPaise", arrAll);

            model.addAttribute("TotDeductAmt", totDeductAmt[0]);
            model.addAttribute("TotDeductAmtPaise", totDeductAmt[1]);

            model.addAttribute("TotNetAmt",netTotaUnder);
           // model.addAttribute("TotNetAmtPaise", netAmount[1]);

            model.addAttribute("payHead", boha.getPayhead());
            model.addAttribute("payAmt",arrAll);
            model.addAttribute("billobjectheadamt", boha);
            System.out.println("payAmt"+arrAll);
        } else {
            path = "/payroll/FrontPage";
            crb = paybillDmpDao.getCommonReportParameter(bbBean.getBillNo());
            billChartOfAccount = AqReportDAO.getBillChartOfAccount(bbBean.getBillNo());
            boha = AqReportDAO.getBillObjectHeadAndAmt(bbBean.getBillNo(), crb.getAqyear(), crb.getAqmonth());
            scheduleList = AqReportDAO.getScheduleListWithADCode(bbBean.getBillNo(), crb.getAqyear(), crb.getAqmonth());
            if (scheduleList != null && scheduleList.size() > 0) {
                for (int i = 0; i < scheduleList.size(); i++) {
                    obj = (Schedule) scheduleList.get(i);
                    double schAmt = Double.parseDouble(obj.getSchAmount());
                    deductAmt = deductAmt + schAmt;
                }
            }

            List oaList = AqReportDAO.getAllowanceDetails(bbBean.getBillNo(), crb.getAqyear(), crb.getAqmonth());
            Schedule obj1 = null;
            if (oaList != null && oaList.size() > 0) {
                for (int i = 0; i < oaList.size(); i++) {
                    obj1 = (Schedule) oaList.get(i);
                    oaAmt = obj1.getAlowanceTotal();
                }
            }

            String totOaAmt[] = CommonFunctions.getRupessAndPaise(String.valueOf(oaAmt));
            String totDeductAmt[] = CommonFunctions.getRupessAndPaise(String.valueOf(deductAmt));

            double netAmt = oaAmt - deductAmt;
             
            String netAmount[] = CommonFunctions.getRupessAndPaise(String.valueOf(netAmt));

            model.addAttribute("billChartOfAccount", billChartOfAccount);
            model.addAttribute("scheduleList", scheduleList);
            model.addAttribute("OAList", oaList);

            model.addAttribute("TotOaAmt", totOaAmt[0]);
            model.addAttribute("TotOaAmtPaise", totOaAmt[1]);

            model.addAttribute("TotDeductAmt", totDeductAmt[0]);
            model.addAttribute("TotDeductAmtPaise", totDeductAmt[1]);

            model.addAttribute("TotNetAmt", netAmount[0]);
            model.addAttribute("TotNetAmtPaise", netAmount[1]);

            model.addAttribute("billobjectheadamt", boha);
        }
        return path;
    }

    @RequestMapping(value = "billFrontPagePDF")
    public void BillFrontPagePDF(HttpServletResponse response, @ModelAttribute("BillBrowserbean") BillBrowserbean bbBean, @ModelAttribute("LoginUserBean") LoginUserBean lub) {

        response.setContentType("application/pdf");
        Document document = new Document(PageSize.A4);

        try {
            response.setHeader("Content-Disposition", "attachment; filename=BillFrontPage_" + bbBean.getBillNo() + ".pdf");

            PdfWriter.getInstance(document, response.getOutputStream());

            document.open();

            CommonReportParamBean crb = paybillDmpDao.getCommonReportParameter(bbBean.getBillNo());
            BillChartOfAccount billChartOfAccount = AqReportDAO.getBillChartOfAccount(bbBean.getBillNo());
            List allowanceList = AqReportDAO.getAllowanceDetails(bbBean.getBillNo(), crb.getAqyear(), crb.getAqmonth());
            ArrayList deductionList = AqReportDAO.getScheduleListWithADCode(bbBean.getBillNo(), crb.getAqyear(), crb.getAqmonth());

            comonScheduleDao.billFrontPagePDF(document, bbBean.getBillNo(), billChartOfAccount, allowanceList, deductionList);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
    }

}
