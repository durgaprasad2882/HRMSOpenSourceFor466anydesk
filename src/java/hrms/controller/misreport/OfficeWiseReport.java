/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.controller.misreport;

import hrms.dao.employee.EmployeeDAO;
import hrms.model.login.LoginUserBean;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Manas Jena
 * 
 */
@Controller
@SessionAttributes({"LoginUserBean","SelectedEmpOffice"})
public class OfficeWiseReport {
    @Autowired
    EmployeeDAO employeeDAO;
    
    @RequestMapping(value = "officewiseEmployee")
    public ModelAndView officewiseEmployee(ModelMap model, HttpServletRequest request,@ModelAttribute("LoginUserBean") LoginUserBean lub) {
        ModelAndView mav = new ModelAndView();
        String path = "misreport/OfficeWiseEmployee";        
        ArrayList employees = employeeDAO.getOfficeWiseEmployeeList(lub.getOffcode());

        mav.addObject("employees", employees);
        mav.setViewName(path);
        return mav;
    }
}
