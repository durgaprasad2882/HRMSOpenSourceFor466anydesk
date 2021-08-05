/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.controller.repatrition;

import hrms.dao.repatrition.RepatritionDAO;
import hrms.model.login.LoginUserBean;
import hrms.model.login.Users;
import hrms.model.repatrition.Repatrition;
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
public class RepatritionController {
       @Autowired
    public RepatritionDAO repatritionDAO;
     @RequestMapping(value = "Repatrition")
    public String RepatritionList(@ModelAttribute("SelectedEmpObj") Users lub) {
        return "/repatrition/Repatrition";
        
        
    }
    @RequestMapping(value = "GetRepatritionListJSON")
    public void GetRepatritionListJSON(HttpServletResponse response, @ModelAttribute("SelectedEmpObj") Users selectedEmpObj, @RequestParam("page") int page) throws IOException {

        response.setContentType("application/json");
        JSONObject json = new JSONObject();
        PrintWriter out = null;
        List repatritionList = null;
        int repatritionlistCnt = 0;
        int maxlimit = 10;
        int minlimit = maxlimit * (page - 1);
        try {
             repatritionList = repatritionDAO.findAllRepatrition(selectedEmpObj.getEmpId());
            json.put("total", repatritionList);
            out = response.getWriter();
            out.write(json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
        }
    }
    
     @RequestMapping(value = "saveRepatritionData")
    public void saveRepatritionData(HttpServletResponse response, @ModelAttribute("LoginUserBean") LoginUserBean lub1, @ModelAttribute("SelectedEmpObj") Users selectedEmpObj, @ModelAttribute("repatritionForm") Repatrition repatritionForm) throws ParseException {

        response.setContentType("application/json");
        PrintWriter out = null;
        int repatrition = 0;

        try {
            repatritionForm.setEmpid(selectedEmpObj.getEmpId());
            repatrition = repatritionDAO.insertRepatritionData(repatritionForm);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
     @RequestMapping(value = "editRepatrition")
    public void editRepatrition(HttpServletResponse response, @ModelAttribute("SelectedEmpObj") Users selectedEmpObj, @RequestParam Map<String, String> requestParams) throws IOException {
        Repatrition repatritionform = null;
        try {
            String empid = selectedEmpObj.getEmpId();
            String repatritionformId = requestParams.get("notId");
            repatritionform = repatritionDAO.editRepatrition(repatritionformId);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
     @RequestMapping(value = "deleteRepatrition")
    public String deleteRepatrition(@RequestParam("notId") String notId) {
        int deletestatus = 0;
        deletestatus = repatritionDAO.deleteRepatrition(notId);
        // System.out.println("loan press save page"+lub.getEmpid());
         return "/repatrition/Repatrition";
    }
    
    @RequestMapping(value = "updateRepatrition")
    public void updateRepatritionData(HttpServletResponse response, @ModelAttribute("LoginUserBean") LoginUserBean lub1, @ModelAttribute("SelectedEmpObj") Users selectedEmpObj, @ModelAttribute("repatritionForm") Repatrition repatritionForm) throws ParseException {
        int redes = 0;
        try {
            repatritionForm.setEmpid(selectedEmpObj.getEmpId());
            redes = repatritionDAO.updateRepatritionData(repatritionForm);
           // System.out.println("Hi");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}

