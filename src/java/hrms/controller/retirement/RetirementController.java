/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.controller.retirement;

import hrms.dao.retirement.RetirementDAO;
import hrms.model.login.LoginUserBean;
import hrms.model.login.Users;
import hrms.model.retirement.Retirement;
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
public class RetirementController {
     @Autowired
    public RetirementDAO retirementDAO;
     @RequestMapping(value = "Retirement")
    public String RedeploymentList(@ModelAttribute("SelectedEmpObj") Users lub) {
        return "/retirement/Retirement";
        
    }
     @RequestMapping(value = "saveRetirementData")
    public void saveRetirementData(HttpServletResponse response, @ModelAttribute("LoginUserBean") LoginUserBean lub1, @ModelAttribute("SelectedEmpObj") Users selectedEmpObj, @ModelAttribute("retirementForm") Retirement retirementForm) throws ParseException {

        response.setContentType("application/json");
        PrintWriter out = null;
        int Retirementdata = 0;

        try {
            retirementForm.setEmpid(selectedEmpObj.getEmpId());
           Retirementdata = retirementDAO.insertRetirementData(retirementForm);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
     @RequestMapping(value = "editRetirement")
    public void editRetirement(HttpServletResponse response, @ModelAttribute("SelectedEmpObj") Users selectedEmpObj, @RequestParam Map<String, String> requestParams) throws IOException {
        Retirement retirementform = null;
        try {
            String empid = selectedEmpObj.getEmpId();
            String retirementformId = requestParams.get("notId");
            retirementform = retirementDAO.editRetirement(retirementformId);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
      @RequestMapping(value = "deleteRetirement")
    public String deleteRetirement(@RequestParam("notId") String notId) {
        int deletestatus = 0;
        deletestatus = retirementDAO.deleteRetirement(notId);
        // System.out.println("loan press save page"+lub.getEmpid());
          return "/retirement/Retirement";
    }
    
    @RequestMapping(value = "updateRetirement")
    public void updateRedeploymentData(HttpServletResponse response, @ModelAttribute("LoginUserBean") LoginUserBean lub1, @ModelAttribute("SelectedEmpObj") Users selectedEmpObj, @ModelAttribute("retirementForm") Retirement retirementForm) throws ParseException {
        int Retirementdata = 0;
        try {
            retirementForm.setEmpid(selectedEmpObj.getEmpId());
            Retirementdata = retirementDAO.updateRetirementData(retirementForm);
           // System.out.println("Hi");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
}
