/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.controller.resignation;

import hrms.dao.resignation.ResignationDAO;
import hrms.model.login.LoginUserBean;
import hrms.model.login.Users;
import hrms.model.resignation.Resignation;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
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
public class ResignationController {
    @Autowired
    public ResignationDAO resignationDAO;

    @RequestMapping(value = "Resignation")
    public String ResignationList(@ModelAttribute("SelectedEmpObj") Users lub) {
        return "/resignation/Resignation";

    }
    @RequestMapping(value = "saveResignationData")
    public void saveResignationData(HttpServletResponse response, @ModelAttribute("LoginUserBean") LoginUserBean lub1, @ModelAttribute("SelectedEmpObj") Users selectedEmpObj, @ModelAttribute("resignationForm") Resignation resignationForm) throws ParseException {

        response.setContentType("application/json");
        PrintWriter out = null;
        int resignationdata = 0;

        try {
            resignationForm.setEmpid(selectedEmpObj.getEmpId());
            resignationdata = resignationDAO.insertResignationtData(resignationForm);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
     @RequestMapping(value = "editResignation")
    public void editResignation(HttpServletResponse response, @ModelAttribute("SelectedEmpObj") Users selectedEmpObj, @RequestParam Map<String, String> requestParams) throws IOException {
       Resignation resignationform = null;
        try {
            String empid = selectedEmpObj.getEmpId();
            String resignationformId = requestParams.get("notId");
           resignationform = resignationDAO.editResignation(resignationformId);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
      @RequestMapping(value = "deleteResignation")
    public String deleteResignation(@RequestParam("notId") String notId) {
        int deletestatus = 0;
        deletestatus = resignationDAO.deleteResignation(notId);
        // System.out.println("loan press save page"+lub.getEmpid());
          return "/resignation/Resignation";
    }
    
    @RequestMapping(value = "updateResignation")
    public void updateResignationData(HttpServletResponse response, @ModelAttribute("LoginUserBean") LoginUserBean lub1, @ModelAttribute("SelectedEmpObj") Users selectedEmpObj, @ModelAttribute("resignationForm") Resignation resignationForm) throws ParseException {
        int resignationdata = 0;
        try {
            resignationForm.setEmpid(selectedEmpObj.getEmpId());
            resignationdata = resignationDAO.updateResignationData(resignationForm);
           // System.out.println("Hi");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
}
