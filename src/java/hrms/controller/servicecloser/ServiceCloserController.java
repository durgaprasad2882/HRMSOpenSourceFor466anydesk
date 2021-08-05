/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hrms.controller.servicecloser;

import hrms.dao.employee.EmployeeDAO;
import hrms.dao.servicecloser.ServiceCloserDAO;
import hrms.model.employee.Employee;
import hrms.model.login.Users;
import hrms.model.servicecloser.EmployeeDeceased;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Manas
 */
@Controller
@SessionAttributes({"Users", "SelectedEmpObj"})
public class ServiceCloserController {
    @Autowired
    ServiceCloserDAO serviceCloserDAO;
    @Autowired
    EmployeeDAO employeeDAO;
    
    @RequestMapping(value = "ShowDeceasedForm")
    public ModelAndView RedeploymentList(@ModelAttribute("SelectedEmpObj") Users selectedEmpObj) {
        ModelAndView mv = new ModelAndView();
        Employee employee = employeeDAO.getEmployeeProfile(selectedEmpObj.getEmpId());
        EmployeeDeceased employeeDeceased = serviceCloserDAO.getEmployeeDeceasedData(selectedEmpObj.getEmpId());
        employeeDeceased.setEmpId(employee.getEmpid());
        employeeDeceased.setCurspc(employee.getSpc());
        mv.addObject("employee", employee);
        mv.addObject("EmployeeDeceased", employeeDeceased);
        mv.setViewName("/servicecloser/EmpDeceased");
        return mv;        
    }
    
    @RequestMapping(value = "SaveEmployeeDeceased")
    public ModelAndView SaveEmployeeDeceased(@ModelAttribute("SelectedEmpObj") Users selectedEmpObj,@ModelAttribute("EmployeeDeceased") EmployeeDeceased employeeDeceased){
        ModelAndView mv = new ModelAndView();        
        serviceCloserDAO.saveEmployeeDeceased(employeeDeceased);
        Employee employee = employeeDAO.getEmployeeProfile(selectedEmpObj.getEmpId());
        mv.addObject("employee", employee);
        mv.addObject("EmployeeDeceased", serviceCloserDAO.getEmployeeDeceasedData(selectedEmpObj.getEmpId()));
        mv.setViewName("/servicecloser/EmpDeceased");
        return mv; 
    }
}
