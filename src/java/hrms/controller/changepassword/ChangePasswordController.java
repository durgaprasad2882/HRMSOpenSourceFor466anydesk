/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.controller.changepassword;

import hrms.dao.changepassword.ChangePasswordDAOImpl;
import hrms.model.login.LoginUserBean;
import hrms.model.login.Users;
import java.io.PrintWriter;
import java.util.HashMap;
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
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 *
 * @author Surendra
 */
@Controller
@SessionAttributes("LoginUserBean")
public class ChangePasswordController {

    //@Value("${msg}")
    @Autowired
    public ChangePasswordDAOImpl changepwdDao;

    @RequestMapping(value = "ChangePasswordAction", method = RequestMethod.POST)
    public void showchangepassword(ModelMap model, @ModelAttribute("loginForm") Users loginForm, @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response) throws Exception{
        PrintWriter out = null;    
        response.setContentType("application/json");
        out = response.getWriter();                
        int pwdstatus = 0;
        Map responseResult = new HashMap();
        if (loginForm.getUserPassword() != null && !loginForm.getUserPassword().equals("")) {
            if (loginForm.getNewpassword().equalsIgnoreCase(loginForm.getConfirmpassword())) {
                pwdstatus = changepwdDao.modifyUserPassword(lub.getEmpid(), lub.getUsertype(), loginForm.getUserPassword(), loginForm.getNewpassword());                
                if (pwdstatus == 1) {
                    responseResult.put("msg", "password changed successfully");                    
                } else if (pwdstatus == 0) {
                    responseResult.put("msg", "Invalid Password");                    
                }else if (pwdstatus == 2) {
                    responseResult.put("msg", "Password does not meet the policy");                    
                }
            } else {
                responseResult.put("msg", "New Password does not Match with Confirm Password");                
            }
        }
        JSONObject jobj = new JSONObject(responseResult);
        out = response.getWriter();
        out.write(jobj.toString());
        
    }

}
