/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.controller.redesignation;

import hrms.dao.redesignation.RedesignationDAO;
import hrms.model.login.LoginUserBean;
import hrms.model.login.Users;
import hrms.model.redesignation.Redesignation;
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
public class RedesignationController {
     @Autowired
    public RedesignationDAO redesignationDAO;
     @RequestMapping(value = "Redesignation")
    public String RedesignationList(@ModelAttribute("SelectedEmpObj") Users lub) {
        return "/redesignation/Redesignation";
        
    }
    
     @RequestMapping(value = "GetRedesignationListJSON")
    public void GetRedesignationListJSON(HttpServletResponse response, @ModelAttribute("SelectedEmpObj") Users selectedEmpObj, @RequestParam("page") int page) throws IOException {

        response.setContentType("application/json");
        JSONObject json = new JSONObject();
        PrintWriter out = null;
        List redesignationList = null;
        int redesignationlistCnt = 0;
        int maxlimit = 10;
        int minlimit = maxlimit * (page - 1);
        try {
             redesignationList = redesignationDAO.findAllRedesignation(selectedEmpObj.getEmpId());
            json.put("total", redesignationList);
            out = response.getWriter();
            out.write(json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
        }
    }
      @RequestMapping(value = "saveRedesignationData")
    public void saveRedesignationData(HttpServletResponse response, @ModelAttribute("LoginUserBean") LoginUserBean lub1, @ModelAttribute("SelectedEmpObj") Users selectedEmpObj, @ModelAttribute("redesignationForm") Redesignation redesignationForm) throws ParseException {

        response.setContentType("application/json");
        PrintWriter out = null;
        int redesignation = 0;

        try {
            redesignationForm.setEmpid(selectedEmpObj.getEmpId());
            redesignation = redesignationDAO.insertRedesignationData(redesignationForm);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
     @RequestMapping(value = "editRedeignation")
    public void editRedeignation(HttpServletResponse response, @ModelAttribute("SelectedEmpObj") Users selectedEmpObj, @RequestParam Map<String, String> requestParams) throws IOException {
        Redesignation redesignationform = null;
        try {
            String empid = selectedEmpObj.getEmpId();
            String redesignationformId = requestParams.get("notId");
            redesignationform = redesignationDAO.editRedesignation(redesignationformId);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
     @RequestMapping(value = "deleteRedeignation")
    public String deleteRedeignation(@RequestParam("notId") String notId) {
        int deletestatus = 0;
        deletestatus = redesignationDAO.deleteRedesignation(notId);
        // System.out.println("loan press save page"+lub.getEmpid());
         return "/redesignation/Redesignation";
    }
    
    @RequestMapping(value = "updateRedeignation")
    public void updateRedeignationData(HttpServletResponse response, @ModelAttribute("LoginUserBean") LoginUserBean lub1, @ModelAttribute("SelectedEmpObj") Users selectedEmpObj, @ModelAttribute("redesignationForm") Redesignation redesignationForm) throws ParseException {
        int redes = 0;
        try {
            redesignationForm.setEmpid(selectedEmpObj.getEmpId());
            redes = redesignationDAO.updateRedesignationData(redesignationForm);
           // System.out.println("Hi");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    
   
    
    
}
