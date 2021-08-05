/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.controller.qtrallotment;

import hrms.dao.qtrallotment.QuarterAllotmentDAO;
import hrms.model.login.LoginUserBean;
import hrms.model.login.Users;
import hrms.model.qtrallotment.QuarterAllotment;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 *
 * @author lenovo
 */
@Controller
@SessionAttributes({"Users", "SelectedEmpObj"})
public class QuarterAllotmentController {
    @Autowired
    QuarterAllotmentDAO quarterAllotmentDAO;
    @RequestMapping(value = "QuarterAllotment")
    public String QuarterAllotmentList(@ModelAttribute("SelectedEmpObj") Users lub) {
        return "/quarterAllotment/QuarterAllotmentList";
    }
    
    @RequestMapping(value = "GetQuarterAllotmentListJSON")
    public void GetQuarterAllotmentListJSON(HttpServletResponse response, @ModelAttribute("SelectedEmpObj") Users selectedEmpObj, @RequestParam("page") int page) {

        response.setContentType("application/json");
        JSONObject json = new JSONObject();
        PrintWriter out = null;
        List quarterqllotmentlist = null;
        int quarterqllotmentlistcnt = 0;
        int maxlimit = 10;
        int minlimit = maxlimit * (page - 1);
        try {
            quarterqllotmentlist = quarterAllotmentDAO.QuaterAllotSt(selectedEmpObj.getEmpId());
            json.put("total", quarterqllotmentlist);
            out = response.getWriter();
            out.write(json.toString());
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
        }
    }
     @RequestMapping(value = "saveQuaterAllotmentData")
    public void saveQuaterAllotmentData(HttpServletResponse response, @ModelAttribute("LoginUserBean") LoginUserBean lub1, @ModelAttribute("SelectedEmpObj") Users selectedEmpObj, @ModelAttribute("additionChargeForm") QuarterAllotment quarterAllotmentForm) throws ParseException {

        response.setContentType("application/json");
        PrintWriter out = null;
        int quarterqllotment = 0;

        try {
            quarterAllotmentForm.setEmpid(selectedEmpObj.getEmpId());
            quarterqllotment = quarterAllotmentDAO.saveQuaterAllotmentRecord(quarterAllotmentForm);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
     @RequestMapping(value = "saveQuaterSurrenderData")
    public void saveQuaterSurrenderData(HttpServletResponse response, @ModelAttribute("LoginUserBean") LoginUserBean lub1, @ModelAttribute("SelectedEmpObj") Users selectedEmpObj, @ModelAttribute("additionChargeForm") QuarterAllotment quarterAllotmentForm) throws ParseException {

        response.setContentType("application/json");
        PrintWriter out = null;
        int quartersurrender = 0;

        try {
            quarterAllotmentForm.setEmpid(selectedEmpObj.getEmpId());
            quartersurrender = quarterAllotmentDAO.saveQuaterSurrenderRecord(quarterAllotmentForm);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
     @RequestMapping(value = "editquarterAllotment")
    public void editquarterAllotment(HttpServletResponse response, @ModelAttribute("SelectedEmpObj") Users selectedEmpObj, @RequestParam Map<String, String> requestParams) throws IOException {
        QuarterAllotment quarterqllotmentform = null;
        try {
            String empid = selectedEmpObj.getEmpId();
            String quarterqllotmentId = requestParams.get("qtrAllotId");
            quarterqllotmentform = quarterAllotmentDAO.editQuarterAllotment(quarterqllotmentId);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
     @RequestMapping(value = "editgetSurrender")
    public void editgetSurrender(HttpServletResponse response, @ModelAttribute("SelectedEmpObj") Users selectedEmpObj, @RequestParam Map<String, String> requestParams) throws IOException {
        QuarterAllotment quarterqllotmentform = null;
        try {
            String empid = selectedEmpObj.getEmpId();
            String quarterqllotmentId = requestParams.get("qtrSurId");
            quarterqllotmentform = quarterAllotmentDAO.getSurrenderEditRecords(quarterqllotmentId);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
     @RequestMapping(value = "deleteqtrAllot")
    public String deleteqtrAllot(@RequestParam("qtrid") String qtrid,@ModelAttribute("SelectedEmpObj") Users selectedEmpObj) {
         String empid = selectedEmpObj.getEmpId();
        quarterAllotmentDAO.deleteQtrAllot(qtrid,empid);
        // System.out.println("loan press save page"+lub.getEmpid());
         return "/quarterAllotment/QuarterAllotmentList";
    }
     @RequestMapping(value = "deleteqtrsurrendRecords")
    public String deleteqtrsurrendRecords(@RequestParam("qtrid") String qtrid,@RequestParam("surid") String surid,@ModelAttribute("SelectedEmpObj") Users selectedEmpObj) {
         String empid = selectedEmpObj.getEmpId();
        quarterAllotmentDAO.deleteQtrSurrendRecords(qtrid,surid,empid);
        // System.out.println("loan press save page"+lub.getEmpid());
         return "/quarterAllotment/QuarterAllotmentList";
    }
}
