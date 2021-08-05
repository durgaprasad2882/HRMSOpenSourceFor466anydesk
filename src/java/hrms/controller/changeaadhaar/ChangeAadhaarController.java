/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hrms.controller.changeaadhaar;

import hrms.common.Message;
import hrms.dao.changeaadhaar.ChangeAadhaarDAO;
import hrms.model.login.LoginUserBean;
import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@SessionAttributes("LoginUserBean")
public class ChangeAadhaarController {
    
    @Autowired
    public ChangeAadhaarDAO changeAadhaarDAO;

    @RequestMapping(value = "ChangeAadhaar")
    public String changeaadhar(ModelMap model, @ModelAttribute("LoginUserBean") LoginUserBean lub, @RequestParam("empId") String empId, @RequestParam("newAadhaar") String newAadhaar) throws SQLException {

        String path = "/tab/ChageAadhaar";
        Message msg = null;
        if (newAadhaar != null && !newAadhaar.equals("")) {
            msg = changeAadhaarDAO.changeAadhaar(empId, newAadhaar);
            System.out.println("msg is: "+msg.getMessage());
            model.addAttribute("message", msg.getMessage());
        }
        model.addAttribute("empId", lub.getEmpid());
        return path;
    }
}
