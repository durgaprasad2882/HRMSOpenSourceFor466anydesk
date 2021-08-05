/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.controller.absenteestmt;

import hrms.dao.absenteestmt.EmpAbsenteeDAO;
import hrms.model.absentee.Absentee;
import hrms.model.login.LoginUserBean;
import hrms.model.login.Users;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Manas Jena
 */
@Controller
@SessionAttributes({"LoginUserBean","SelectedEmpObj"})
public class EmpAbsenteeStmtController {
    @Autowired
    EmpAbsenteeDAO empAbsenteeDAO;
    @RequestMapping(value = "employeeAbsenteeAction", method = RequestMethod.GET)
    public String employeeAbsenteeAction(ModelMap model, @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response) {//
        String path = "/absentee/EmployeeAbsentee";
        return path;
    }
    @ResponseBody
    @RequestMapping(value = "getAbseneteeYear")
    public void getAbseneteeYear(HttpServletRequest request, @ModelAttribute("LoginUserBean") LoginUserBean lub, @ModelAttribute("SelectedEmpObj") Users selectedEmpObj, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = null;
        List adlist = empAbsenteeDAO.getAbseneteeYear(selectedEmpObj.getEmpId());
        JSONArray json = new JSONArray(adlist);
        out = response.getWriter();
        out.write(json.toString());
    }
    @ResponseBody
    @RequestMapping(value = "getAbseneteeList")
    public void getAbseneteeList(HttpServletRequest request, @RequestParam("sltyear") String sltyear, @RequestParam("sltmonth") Integer sltmonth, @ModelAttribute("LoginUserBean") LoginUserBean lub, @ModelAttribute("SelectedEmpObj") Users selectedEmpObj, HttpServletResponse response) throws IOException{
        response.setContentType("application/json");
        PrintWriter out = null;
        List adlist = empAbsenteeDAO.getAbseneteeList(selectedEmpObj.getEmpId(),sltyear,sltmonth);
        
        JSONArray json = new JSONArray(adlist);
        out = response.getWriter();
        out.write(json.toString());
    }
    @RequestMapping(value = "saveEmployeeAbsentee", method = RequestMethod.POST)
    public  void  saveEmployeeAbsentee(@ModelAttribute("Absentee") Absentee absentee, @ModelAttribute("LoginUserBean") LoginUserBean lub, @ModelAttribute("SelectedEmpObj") Users selectedEmpObj, HttpServletResponse response) throws IOException{        
       response.setContentType("application/json");
        PrintWriter out = null;
        absentee.setEmpId(selectedEmpObj.getEmpId());
        absentee.setEntempId(lub.getEmpid());
       
        empAbsenteeDAO.saveAbsenteeData(absentee);
        
        List adlist = empAbsenteeDAO.getAbseneteeList(selectedEmpObj.getEmpId(),absentee.getSltyear()+"",absentee.getSltmonth());
        
        JSONArray json = new JSONArray(adlist);
        out = response.getWriter();
        out.write(json.toString());
       
    }
}
