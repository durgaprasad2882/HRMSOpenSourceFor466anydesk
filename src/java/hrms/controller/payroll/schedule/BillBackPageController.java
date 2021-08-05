/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.controller.payroll.schedule;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import hrms.common.CommonFunctions;
import hrms.common.Numtowordconvertion;
import hrms.dao.payroll.billbrowser.AqReportDAOImpl;
import hrms.dao.payroll.schedule.PayBillDMPDAO;
import hrms.dao.payroll.schedule.ScheduleDAO;
import hrms.model.common.CommonReportParamBean;
import hrms.model.payroll.billbrowser.BillChartOfAccount;
import hrms.model.payroll.billbrowser.BillObjectHead;
import hrms.model.payroll.schedule.BillBackPageBean;
import hrms.model.payroll.schedule.Schedule;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class BillBackPageController implements ServletContextAware {

    @Autowired
    public ScheduleDAO comonScheduleDao;
    @Autowired
    AqReportDAOImpl AqReportDAO;
    @Autowired
    public PayBillDMPDAO paybillDmpDao;

    private ServletContext context;

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.context = servletContext;
    }

    @RequestMapping(value = "BillBackPgHTML", method = {RequestMethod.POST, RequestMethod.GET})
    public ModelAndView BillBackPgHTML(ModelMap model, @RequestParam("billNo") String billNo) {
        CommonReportParamBean crb = null;
        BillChartOfAccount billChartOfAccount = null;
        ModelAndView mav = null;
        List empList = null;
        double deductAmt = 0;
        String totPaise = "";
        long total = 0;
        double netAmt = 0;
        double cpfAmt = 0.0;
        double itAmt = 0.0;
        double ptAmt = 0.0;
        String netTotaUnder = "";
        String arrAll="";
        String netTotalinWordUnder = "";
        String netTotal = "";
        String netTotalinWord = "";
        BillBackPageBean backPageBean = new BillBackPageBean();
        BillBackPageBean obj = null;
        BillObjectHead boha = null;
        Schedule obj1 = null;
        double oaAmt = 0.0;
        try {
            mav = new ModelAndView();
            crb = paybillDmpDao.getCommonReportParameter(billNo);

            if (crb.getBillType() != null && crb.getBillType().equals("43")) {
                billChartOfAccount = AqReportDAO.getBillChartOfAccount(billNo);
                boha = AqReportDAO.getArrBillObjectHeadAndAmt(billNo);

                obj1 = AqReportDAO.getArrScheduleListWithADCode(billNo);
                if (obj1.getCpfAmt() != null && !obj1.getCpfAmt().equals("")) {
                    cpfAmt = Double.parseDouble(obj1.getCpfAmt());
                }
                if (obj1.getPtAmt() != null && !obj1.getPtAmt().equals("")) {
                    ptAmt = Double.parseDouble(obj1.getPtAmt());
                }
                if (obj1.getItAmt() != null && !obj1.getItAmt().equals("")) {
                    itAmt = Double.parseDouble(obj1.getItAmt());
                }

                deductAmt = cpfAmt + ptAmt + itAmt;
                oaAmt = boha.getPayamt();
                arrAll= Double.valueOf(oaAmt + "").longValue() + "";
                String totOaAmt[] = CommonFunctions.getRupessAndPaise(String.valueOf(oaAmt));
                String totDeductAmt[] = CommonFunctions.getRupessAndPaise(String.valueOf(deductAmt));

                netAmt = oaAmt - deductAmt;
                String netAmount[] = CommonFunctions.getRupessAndPaise(String.valueOf(netAmt));
                netTotalinWord = Numtowordconvertion.convertNumber((int) Double.parseDouble(netAmount[0]));
                netTotal = Double.valueOf(netAmt + "").longValue() + "";
                netTotalinWord = Numtowordconvertion.convertNumber(Integer.parseInt(netTotal));
                netTotaUnder = Double.valueOf((netAmt+1) + "").longValue() + "";
                netTotalinWordUnder = Numtowordconvertion.convertNumber(Integer.parseInt(netTotaUnder));

                model.addAttribute("billobjectheadamt", boha);

                mav.addObject("BPGHeader", backPageBean);
                mav.addObject("BPGList", empList);
                model.addAttribute("cpfAmt", obj1.getCpfAmt());
                model.addAttribute("ptAmt", obj1.getPtAmt());
                model.addAttribute("itAmt", obj1.getItAmt());
                mav.addObject("TotDedut", totDeductAmt[0]);
                mav.addObject("NetTotal", netTotal);
                mav.addObject("NetTotalWord", netTotalinWord);
                mav.addObject("NetTotalUnder", netTotaUnder);
                mav.addObject("NetTotalWordUnder", netTotalinWordUnder);
                mav.addObject("payAmt", arrAll);
                mav.setViewName("/payroll/arrear/ArrBillBackPage");

            } else {

                backPageBean = comonScheduleDao.getBillBackPgScheduleHeaderDetails(billNo, crb.getAqyear(), crb.getAqmonth());
                empList = comonScheduleDao.getBillBackPgScheduleEmpList(billNo, crb.getAqyear(), crb.getAqmonth());

                if (empList != null && empList.size() > 0) {
                    obj = new BillBackPageBean();
                    for (int i = 0; i < empList.size(); i++) {
                        obj = (BillBackPageBean) empList.get(i);
                        deductAmt = deductAmt + Long.parseLong(obj.getSchAmount());
                    }
                }
                total = Long.parseLong(backPageBean.getTotalPaise());

                netAmt = total - deductAmt;
                netTotal = Double.valueOf(netAmt + "").longValue() + "";
                netTotalinWord = Numtowordconvertion.convertNumber((int) Double.parseDouble(netTotal));

                netTotaUnder = Double.valueOf(netAmt + "").longValue() + "";
                netTotalinWordUnder = Numtowordconvertion.convertNumber(Integer.parseInt(netTotaUnder));

                mav.addObject("BPGHeader", backPageBean);
                mav.addObject("BPGList", empList);
                mav.addObject("TotDedut", deductAmt);
                mav.addObject("NetTotal", netTotal);
                mav.addObject("NetTotalWord", netTotalinWord);
                mav.addObject("NetTotalUnder", netTotaUnder);
                mav.addObject("NetTotalWordUnder", netTotalinWordUnder);
                mav.setViewName("/payroll/schedule/BillBackPage");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mav;
    }

    @RequestMapping(value = "billBackPagePDF")
    public void BillBackPagePDF(HttpServletResponse response, @RequestParam("billNo") String billNo) {

        response.setContentType("application/pdf");
        Document document = new Document(PageSize.A4);

        try {
            response.setHeader("Content-Disposition", "attachment; filename=BillBackPage_" + billNo + ".pdf");
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            CommonReportParamBean crb = paybillDmpDao.getCommonReportParameter(billNo);
            BillBackPageBean backPageBean = comonScheduleDao.getBillBackPgScheduleHeaderDetails(billNo, crb.getAqyear(), crb.getAqmonth());
            List empList = comonScheduleDao.getBillBackPgScheduleEmpList(billNo, crb.getAqyear(), crb.getAqmonth());

            comonScheduleDao.billBackPagePDF(document, billNo, backPageBean, empList);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
    }
}
