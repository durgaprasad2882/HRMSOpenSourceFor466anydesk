package hrms.controller.tpfreport;

import hrms.common.Numtowordconvertion;
import hrms.dao.tpfreport.TPFReportsDAO;
import hrms.model.login.LoginUserBean;
import hrms.model.tpfreport.TPFReportBean;
import hrms.model.tpfreport.TPFReportList;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TPFReportsController {
    
    @Autowired
    public TPFReportsDAO tpfreportDAO;
    
    @RequestMapping(value = "TreasuryReportTPF")
    public ModelAndView tpfreportpage(@RequestParam("offCode") String offCode){
        ModelAndView mav = new ModelAndView();        
        mav.setViewName("/tpfreport/TPFReport");
        return mav;
    }
    
    @ResponseBody
    @RequestMapping(value = "treasuryDDOListJSON")
    public void GetTreasuryDDOListJSON(HttpServletResponse response,@RequestParam("trcode") String trcode) {

        response.setContentType("application/json");
        PrintWriter out = null;

        List ddolist = null;
        try {
            ddolist = tpfreportDAO.getDDOList(trcode);

            JSONArray json = new JSONArray(ddolist);
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
    @RequestMapping(value = "treasuryGetBillAmtJSON")
    public void GetTreasuryGETBillAmtJSON(HttpServletResponse response,@RequestParam("ddocode") String ddocode,@RequestParam("year") int year,@RequestParam("month") int month) {

        response.setContentType("application/json");
        PrintWriter out = null;

        List amtlist = null;
        try {
            amtlist = tpfreportDAO.getBillAmt(ddocode,month,year);

            JSONArray json = new JSONArray(amtlist);
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
    @RequestMapping(value = "treasuryGetYearListJSON")
    public void GetTreasuryGetYearListJSON(HttpServletResponse response) {

        response.setContentType("application/json");
        PrintWriter out = null;

        List yearlist = null;
        try {
            yearlist = tpfreportDAO.getYearList();

            JSONArray json = new JSONArray(yearlist);
            out = response.getWriter();
            out.write(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
        }
    }
    
    @RequestMapping(value = "TPFSchedulePaymentReport")
    public String TPFSchedulePaymentReport(Model model,@RequestParam("billNo") String billNo){
        
        int totalAmt = 0;
        
        TPFReportBean tbean = null;
        TPFReportList tlist = null;
        try{
            tbean = tpfreportDAO.getBillDtls(billNo);
            
            model.addAttribute("billData", tbean);
            
            List paymentList = tpfreportDAO.getPaymentList(billNo);
            Iterator itr = paymentList.iterator();
            if(itr.hasNext()){
                tlist = (TPFReportList)itr.next();
                totalAmt = totalAmt + tlist.getAmount();
            }
            
            model.addAttribute("paymentList", paymentList);
            model.addAttribute("totalAmt", totalAmt);
            model.addAttribute("totalAmtinWords", Numtowordconvertion.convertNumber(totalAmt));
        }catch(Exception e){
            e.printStackTrace();
        }
       return "/tpfreport/TPFPaymentReport";
    }
}
