/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.controller.termination;

import hrms.dao.retirement.RetirementDAO;
import hrms.dao.termination.TerminationDAO;
import hrms.model.login.LoginUserBean;
import hrms.model.login.Users;
import hrms.model.termination.Termination;
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
public class TerminationController {

    @Autowired
    public TerminationDAO terminationDAO;

    @RequestMapping(value = "Termination")
    public String TerminationList(@ModelAttribute("SelectedEmpObj") Users lub) {
        return "/termination/Termination";

    }

    @RequestMapping(value = "saveTerminationData")
    public void saveTerminationData(HttpServletResponse response, @ModelAttribute("LoginUserBean") LoginUserBean lub1, @ModelAttribute("SelectedEmpObj") Users selectedEmpObj, @ModelAttribute("terminationForm") Termination terminationForm) throws ParseException {

        response.setContentType("application/json");
        PrintWriter out = null;
        int Terminationdata = 0;

        try {
            terminationForm.setEmpid(selectedEmpObj.getEmpId());
            Terminationdata = terminationDAO.insertPayrevisionData(terminationForm);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
     @RequestMapping(value = "editTermination")
    public void editTermination(HttpServletResponse response, @ModelAttribute("SelectedEmpObj") Users selectedEmpObj, @RequestParam Map<String, String> requestParams) throws IOException {
        Termination terminationform = null;
        try {
            String empid = selectedEmpObj.getEmpId();
            String terminationformId = requestParams.get("notId");
            terminationform = terminationDAO.editTermination(terminationformId);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
      @RequestMapping(value = "deleteTermination")
    public String deleteTermination(@RequestParam("notId") String notId) {
        int deletestatus = 0;
        deletestatus = terminationDAO.deleteTermination(notId);
        // System.out.println("loan press save page"+lub.getEmpid());
          return "/termination/Termination";
    }
    
    @RequestMapping(value = "updateTermination")
    public void updateTerminationData(HttpServletResponse response, @ModelAttribute("LoginUserBean") LoginUserBean lub1, @ModelAttribute("SelectedEmpObj") Users selectedEmpObj, @ModelAttribute("terminationForm") Termination terminationForm) throws ParseException {
        int terminationdata = 0;
        try {
            terminationForm.setEmpid(selectedEmpObj.getEmpId());
            terminationdata = terminationDAO.updateTerminationData(terminationForm);
           // System.out.println("Hi");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
