package hrms.controller.loansanction;

import hrms.common.CommonFunctions;
import hrms.dao.leaveapply.LeaveApplyDAOImpl;
import hrms.dao.loansanction.LoanSancDAO;
import hrms.dao.master.BankDAOImpl;
import hrms.dao.master.LoanTypeDAO;
import hrms.dao.master.TreasuryDAO;
import hrms.dao.notification.NotificationDAOImpl;
import hrms.model.loan.Loan;
import hrms.model.login.LoginUserBean;
import hrms.model.login.Users;
import hrms.model.notification.NotificationBean;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
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

@Controller
@SessionAttributes({"LoginUserBean", "SelectedEmpObj"})
public class LoanSanctionController {

    @Autowired
    public LoanSancDAO loanSancDAO;
    @Autowired
    NotificationDAOImpl notificationDao;
    @Autowired
    public LeaveApplyDAOImpl leaveapplyDAO;
    @Autowired
    public BankDAOImpl bankDAO;
    @Autowired
    public TreasuryDAO treasuryDao;
    @Autowired
    public LoanTypeDAO loanTypeDAO;
    
    public static String getServerDoe() {
        String currDate;
        Format formatter;
        formatter = new SimpleDateFormat("dd-MMM-yyyy");
        currDate = formatter.format(new Date());
        return currDate;
    }

    @ResponseBody
    @RequestMapping(value = "loansanctionlist.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public void viewLoanList(@ModelAttribute("SelectedEmpObj") Users lub, @ModelAttribute("loanForm") Loan loanForm, BindingResult result, Map<String, Object> model, HttpServletResponse response) {
        response.setContentType("application/json");
        JSONObject json = new JSONObject();
        PrintWriter out = null;
        int transferlistCnt = 0;

        try {
            List loanSancList = loanSancDAO.getLoanSancList(loanForm.getEmpid());
            json.put("total", 50);
            json.put("rows", loanSancList);
            out = response.getWriter();
            out.write(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
        }
    }

    @RequestMapping(value = "loansanction.htm", method = RequestMethod.GET)
    public ModelAndView LoanSanctionList(@ModelAttribute("SelectedEmpObj") Users lub, @ModelAttribute("loanForm") Loan loanForm, Map<String, Object> model) {
        ModelAndView mv = null;
        loanForm.setEmpid(CommonFunctions.decodedTxt(lub.getEmpId()));
        List loanSancList = loanSancDAO.getLoanSancList(loanForm.getEmpid());

        mv = new ModelAndView("/loansanction/LoanSanctionList", "command", loanForm);
        mv.addObject("loanSancList", loanSancList);
        //mv.setViewName("/loansanction/LoanSanctionList");
        return mv;
    }

    @RequestMapping(value = "saveloanSanction.htm", method = RequestMethod.POST)
    public ModelAndView saveloanSanction(@ModelAttribute("SelectedEmpObj") Users lub, ModelMap model, @ModelAttribute("loanForm") Loan loanForm, BindingResult result, HttpServletResponse response) throws IOException {
        String notId = "";
        ModelAndView mv = new ModelAndView("/loansanction/LoanSanctionList", "command", loanForm);

        NotificationBean nb = new NotificationBean();

        if (loanForm.getNotid() != null) {
            nb.setNotid(loanForm.getNotid());
        }
        nb.setNottype("LOAN_SANC");
        nb.setEmpId(loanForm.getEmpid());
        nb.setDateofEntry(new Date());
        nb.setOrdno(loanForm.getOrderno());
        nb.setOrdDate(loanForm.getOrderdate());
        nb.setNote(loanForm.getNote());
        nb.setSancDeptCode(loanForm.getHidAuthDeptCode());
        nb.setSancOffCode(loanForm.getHidAuthOffCode());
        nb.setSancAuthCode(loanForm.getHidSpcAuthCode());
        if (loanForm.getLoanid() != null && !loanForm.getLoanid().equals("")) {
            System.out.println("Inside update");
            loanSancDAO.updateLoanDetail(loanForm);
            notificationDao.modifyNotificationData(nb);
        } else {
            System.out.println("Inside insert");
            notId = notificationDao.insertNotificationData(nb);
            loanSancDAO.saveLoanDetail(loanForm, notId);
        }
        List loanSancList = loanSancDAO.getLoanSancList(loanForm.getEmpid());
        mv.addObject("loanSancList", loanSancList);

        return mv;
    }

    @RequestMapping(value = "newloanData.htm", method = RequestMethod.POST)
    public ModelAndView newLoanData(@ModelAttribute("SelectedEmpObj") Users lub, @ModelAttribute("loanForm") Loan loanForm, Map<String, Object> model) {
        ModelAndView mv = new ModelAndView("/loansanction/NewLoanSanction", "command", loanForm);
        List bankList = bankDAO.getBankList();
        List treasuryList = treasuryDao.getTreasuryList();
        List loanTypeList = loanTypeDAO.getLoanTypeList();
        mv.addObject("bankList", bankList);
        mv.addObject("treasuryList", treasuryList);
        mv.addObject("loanTypeList", loanTypeList);
        return mv;
    }
    @RequestMapping(value = "editloanData.htm", method = RequestMethod.GET)
    public ModelAndView editloanData(@ModelAttribute("SelectedEmpObj") Users lub, @ModelAttribute("loanForm") Loan loanForm, Map<String, Object> model) {        
        loanForm = loanSancDAO.editLoanData(loanForm.getLoanid());
        List bankList = bankDAO.getBankList();
        List treasuryList = treasuryDao.getTreasuryList();
        List loanTypeList = loanTypeDAO.getLoanTypeList();
        ModelAndView mv = new ModelAndView("/loansanction/NewLoanSanction", "command", loanForm);
        mv.addObject("bankList", bankList);
        mv.addObject("treasuryList", treasuryList);
        mv.addObject("loanTypeList", loanTypeList);
        return mv;
    }
    @RequestMapping(value = "editLoanSanc.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public void editloanData(@ModelAttribute("loanForm") Loan lfb, Map<String, Object> model, @RequestParam("loanId") String loanId, @RequestParam("notId") String notid, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = null;
        try {
            lfb = loanSancDAO.editLoanData(loanId);
            lfb.setLoanid(loanId);
            lfb.setNotid(notid);
            JSONObject job = new JSONObject(lfb);
            out = response.getWriter();
            out.write(job.toString());
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // response.sendRedirect("loansanction.htm");
        //return "/loansanction/LoanSanctionList";
    }
    /*
    @RequestMapping(value = "deleteLoan.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public void deleteLoanData(@ModelAttribute("loanForm") Loan lfb, Map<String, Object> model, @RequestParam("loanId") String loanId, @RequestParam("notId") String notid, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = null;
        try {
            loanSancDAO.removeLoanData(loanId, notid);
            JSONObject job = new JSONObject(lfb);
            out = response.getWriter();
            out.write(job.toString());
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }*/
    @RequestMapping(value = "employeeloanaccount.htm", method = RequestMethod.GET)
    public ModelAndView EmpLoanAccount(@ModelAttribute("LoginUserBean") LoginUserBean lub, ModelMap model, @ModelAttribute("loanacount") Loan loan) {        
        loan.setEmpName(lub.getLoginname());
        loan.setGpfNo(lub.getGpfno());        
        ModelAndView mv = new ModelAndView("/loansanction/EmployeeLoanAccount","loanacount",loan);
        mv.addObject("loanTypeList", loanTypeDAO.getLoanTypeList());
        return mv;
    }

    @RequestMapping(value = "loanaccount.htm", method = RequestMethod.POST)
    public ModelAndView getLoanEmpWise(@ModelAttribute("LoginUserBean") LoginUserBean lub, ModelMap model, @ModelAttribute("loanacount") Loan loan) {        
        loan.setEmpName(lub.getLoginname());
        loan.setGpfNo(lub.getGpfno());
        List empLoanDeatil = loanSancDAO.getLoanDeductionDeailEmpWise(lub.getEmpid(), loan.getSltloan());
        ModelAndView mv = new ModelAndView("/loansanction/EmployeeLoanAccount","loanacount",loan);
        mv.addObject("loanTypeList", loanTypeDAO.getLoanTypeList());
        mv.addObject("loanName", loanTypeDAO.getLoanTypeDetails(loan.getSltloan()));
        mv.addObject("empLoanDeatil", empLoanDeatil);
        return mv;
    }
}
