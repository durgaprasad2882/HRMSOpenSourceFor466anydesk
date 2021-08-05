package hrms.controller.payroll.payslip;

import hrms.common.Numtowordconvertion;
import hrms.dao.payroll.payslip.PaySlipDAOImpl;
import hrms.model.login.LoginUserBean;
import hrms.model.payroll.payslip.ADDetails;
import hrms.model.payroll.payslip.PaySlipDetailBean;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

@Controller
@SessionAttributes("LoginUserBean")
public class PaySlipController {

    @Autowired
    public PaySlipDAOImpl payslipDao;

    @RequestMapping(value = "PaySlipList", method = RequestMethod.GET)
    public String PaySlipList(Model model, @ModelAttribute("LoginUserBean") LoginUserBean lub) {

        
        model.addAttribute("empid", lub.getEmpid());
        return "/payroll/payslip/PaySlipList";

    }

    @ResponseBody
    @RequestMapping(value = "GetPaySlipListJSON", method = {RequestMethod.GET, RequestMethod.POST})
    public void GetPaySlipListJSON(HttpServletResponse response, @RequestParam Map<String, String> requestParams) {

        response.setContentType("application/json");

        JSONObject json = new JSONObject();
        PrintWriter out = null;

        List emppayslip = null;
        try {
            String empid = requestParams.get("empid");
            if ((requestParams.get("year") != null && !requestParams.get("year").equals(""))
                    && (requestParams.get("month") != null && !requestParams.get("month").equals(""))) {

                int year = Integer.parseInt(requestParams.get("year"));
                int month = Integer.parseInt(requestParams.get("month"));

                String billNo = payslipDao.getTokenGeneratedBillNo(empid, year, month);

                if (billNo != null && !billNo.equals("")) {
                    emppayslip = payslipDao.getPaySlip(billNo, empid, year, month);
                }
            }
            json.put("total", 1);
            json.put("rows", emppayslip);
            out = response.getWriter();
            out.write(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
        }
    }

    @RequestMapping(value = "GetPaySlip", method = RequestMethod.POST)
    public ModelAndView getPaySlip(Model model, @RequestParam Map<String, String> requestParams) {

        String empid = "";
        String year = "";
        String month = "";
        try {
            empid = requestParams.get("empid");
            year = requestParams.get("sltYear");
            month = requestParams.get("sltMonth");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ModelAndView("redirect:/GetPaySlipListJSON.htm?empid=" + empid + "&year=" + year + "&month=" + month);
    }

    @RequestMapping(value = "PaySlipDetail", method = RequestMethod.GET)
    public String PaySlipDetail(Model model, @RequestParam Map<String, String> requestParams) {

        String empid = requestParams.get("empid");
        String aqslno = requestParams.get("aqlsno");
        int year = Integer.parseInt(requestParams.get("sltYear"));
        int month = Integer.parseInt(requestParams.get("sltMonth"));

        String path = "";

        PaySlipDetailBean pbeandetail = null;
        ADDetails ad = null;

        ADDetails[] allowancelist = null;
        ADDetails[] deductionlist = null;
        List privateDeductionlist = null;
        List loanList = null;

        double totalAllowance = 0;
        double totalDeduction = 0;
        double totalPrivateDeduction = 0;
        double totalLoan = 0;

        int gross = 0;
        int net = 0;
        int netded = 0;
        try {
            pbeandetail = payslipDao.getEmployeeData(aqslno, year, month);

            allowancelist = payslipDao.getAllowanceDeductionList(aqslno, "A", year, month);
            for (int i = 0; i < allowancelist.length; i++) {
                ad = (ADDetails) allowancelist[i];
                totalAllowance = ad.getAdAmount();
            }

            deductionlist = payslipDao.getAllowanceDeductionList(aqslno, "D", year, month);
            for (int i = 0; i < deductionlist.length; i++) {
                ad = (ADDetails) deductionlist[i];
                totalDeduction = ad.getAdAmount();
            }

            privateDeductionlist = payslipDao.getPrivateDedeuctionList(aqslno, year, month);
            for (int i = 0; i < privateDeductionlist.size(); i++) {
                ad = (ADDetails) privateDeductionlist.get(i);
                totalPrivateDeduction = ad.getAdAmount();
            }

            loanList = payslipDao.getLoanList(aqslno, year, month);
            for (int i = 0; i < loanList.size(); i++) {
                ad = (ADDetails) loanList.get(i);
                totalLoan = ad.getAdAmount();
            }

            model.addAttribute("empdata", pbeandetail);
            model.addAttribute("allowancelist", allowancelist);
            model.addAttribute("deductionlist", deductionlist);
            model.addAttribute("privateDeductionlist", privateDeductionlist);
            model.addAttribute("loanList", loanList);

            model.addAttribute("totalAllowance", totalAllowance);
            model.addAttribute("totalDeduction", totalDeduction + totalLoan);
            model.addAttribute("totalPrivateDeduction", totalPrivateDeduction);
            model.addAttribute("totalLoan", totalLoan);

            if (pbeandetail.getCurBasic() != null && !pbeandetail.getCurBasic().equals("")) {
                gross = Integer.parseInt(pbeandetail.getCurBasic()) + (int) totalAllowance;
                net = gross - ((int) totalDeduction + (int) totalLoan);
                netded = net - (int) totalPrivateDeduction;
            }

            model.addAttribute("gross", gross);
            model.addAttribute("netded", netded);
            model.addAttribute("netAmtWords", Numtowordconvertion.convertNumber(netded));

            path = "/payroll/payslip/PaySlipDetail";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    @RequestMapping(value = "PaySlipPDF", method = RequestMethod.GET)
    public void PaySlipPDF(@RequestParam Map<String, String> requestParams) {

        String empid = requestParams.get("empid");
        String aqslno = requestParams.get("aqlsno");
        int year = Integer.parseInt(requestParams.get("sltYear"));
        int month = Integer.parseInt(requestParams.get("sltMonth"));

        String path = "";

        PaySlipDetailBean pbeandetail = null;

        ADDetails ad = null;

        ADDetails[] allowancelist = null;
        ADDetails[] deductionlist = null;
        List privateDeductionlist = null;
        List loanList = null;

        try {
            pbeandetail = payslipDao.getEmployeeData(aqslno, year, month);

            allowancelist = payslipDao.getAllowanceDeductionList(aqslno, "A", year, month);
            for (int i = 0; i < allowancelist.length; i++) {
                ad = (ADDetails) allowancelist[i];
                //totalAllowance = totalAllowance + Integer.parseInt(ad.getAdAmount());
            }

            deductionlist = payslipDao.getAllowanceDeductionList(aqslno, "D", year, month);
            for (int i = 0; i < deductionlist.length; i++) {
                ad = (ADDetails) deductionlist[i];
                //totalDeduction = totalDeduction + Integer.parseInt(ad.getAdAmount());
            }

            privateDeductionlist = payslipDao.getPrivateDedeuctionList(aqslno, year, month);
            for (int i = 0; i < privateDeductionlist.size(); i++) {
                ad = (ADDetails) privateDeductionlist.get(i);
                //totalPrivateDeduction = totalPrivateDeduction + Integer.parseInt(ad.getAdAmount());
            }

            loanList = payslipDao.getLoanList(aqslno, year, month);
            for (int i = 0; i < loanList.size(); i++) {
                ad = (ADDetails) loanList.get(i);
                //totalLoan = totalLoan + Integer.parseInt(ad.getAdAmount());
            }

            //payslipDao.getPaySlipPDF(pbeandetail,allowancelist,deductionlist,privateDeductionlist,loanList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
