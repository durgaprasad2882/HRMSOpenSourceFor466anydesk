/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.controller.redeployment;

import hrms.dao.redeployment.RedeploymentDAO;
import hrms.model.login.LoginUserBean;
import hrms.model.login.Users;
import hrms.model.redeployment.Redeployment;
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
public class RedeploymentController {
    @Autowired
    public RedeploymentDAO redeploymentDAO;
     @RequestMapping(value = "Redeployment")
    public String RedeploymentList(@ModelAttribute("SelectedEmpObj") Users lub) {
        return "/redeployment/Redeployment";
        
    }
    
     @RequestMapping(value = "GetRedeploymentListJSON")
    public void GetRedeploymentListJSON(HttpServletResponse response, @ModelAttribute("SelectedEmpObj") Users selectedEmpObj, @RequestParam("page") int page) throws IOException {

        response.setContentType("application/json");
        JSONObject json = new JSONObject();
        PrintWriter out = null;
        List redeploymentlist = null;
        int redeploymentlistCnt = 0;
        int maxlimit = 10;
        int minlimit = maxlimit * (page - 1);
        try {
            redeploymentlist = redeploymentDAO.findAllRedeployment(selectedEmpObj.getEmpId());
            json.put("total", redeploymentlist);
            out = response.getWriter();
            out.write(json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
        }
    }
     @RequestMapping(value = "saveRedeploymentData")
    public void saveRedeploymentData(HttpServletResponse response, @ModelAttribute("LoginUserBean") LoginUserBean lub1, @ModelAttribute("SelectedEmpObj") Users selectedEmpObj, @ModelAttribute("redeploymentForm") Redeployment redeploymentForm) throws ParseException {

        response.setContentType("application/json");
        PrintWriter out = null;
        int redeploymentdata = 0;

        try {
            redeploymentForm.setEmpid(selectedEmpObj.getEmpId());
            redeploymentdata = redeploymentDAO.insertRedeploymentData(redeploymentForm);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @RequestMapping(value = "editRedeployment")
    public void editRedeployment(HttpServletResponse response, @ModelAttribute("SelectedEmpObj") Users selectedEmpObj, @RequestParam Map<String, String> requestParams) throws IOException {
        Redeployment redeploymentform = null;
        try {
            String empid = selectedEmpObj.getEmpId();
            String redeploymentformId = requestParams.get("notId");
            redeploymentform = redeploymentDAO.editRedeployment(redeploymentformId);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
      @RequestMapping(value = "deleteRedeployment")
    public String deleteRedeployment(@RequestParam("notId") String notId) {
        int deletestatus = 0;
        deletestatus = redeploymentDAO.deleteRedeployment(notId);
        // System.out.println("loan press save page"+lub.getEmpid());
         return "/redeployment/Redeployment";
    }
    
    @RequestMapping(value = "updateRedeployment")
    public void updateRedeploymentData(HttpServletResponse response, @ModelAttribute("LoginUserBean") LoginUserBean lub1, @ModelAttribute("SelectedEmpObj") Users selectedEmpObj, @ModelAttribute("redeploymentForm") Redeployment redeploymentForm) throws ParseException {
        int redeploymentdata = 0;
        try {
            redeploymentForm.setEmpid(selectedEmpObj.getEmpId());
            redeploymentdata = redeploymentDAO.updateRedeploymentData(redeploymentForm);
           // System.out.println("Hi");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
     
    
    
}
