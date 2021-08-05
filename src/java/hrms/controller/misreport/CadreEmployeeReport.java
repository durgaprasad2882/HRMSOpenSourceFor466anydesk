/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.controller.misreport;

import hrms.dao.misreport.CadreEmployeeReportDAO;
import hrms.model.employee.Employee;
import hrms.model.login.LoginUserBean;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
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
@SessionAttributes("LoginUserBean")
public class CadreEmployeeReport {
    @Autowired
    CadreEmployeeReportDAO cadreEmployeeReportDAO;
    
    @RequestMapping(value = "cadreEmployeeReportIAS")
     //public String moduleList(ModelMap model, @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response) {
    public ModelAndView cadreEmployeeReportIAS(ModelMap model, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView();
        String path = "misreport/CadreEmployeeReport";
        ArrayList employees = cadreEmployeeReportDAO.getEmployeeList("1101");        
        mav.addObject("employees", employees);
        mav.setViewName(path);
        return mav;
    }
    
    @RequestMapping(value = "servicesdashboard", method = {RequestMethod.GET})
    public ModelAndView servicesDashboard() {
        ModelAndView mav = new ModelAndView();
        String path = "/tab/ServiceConditionAdmin";
        mav.setViewName(path);
        return mav;
    }
    
    @ResponseBody
    @RequestMapping(value = "getCadreEmpData", method = RequestMethod.GET)
    public void getCadreEmpData(HttpServletRequest request, @ModelAttribute("LoginUserBean") LoginUserBean lub,@RequestParam("empid") String empid, HttpServletResponse response) throws IOException{
        response.setContentType("application/json");
        PrintWriter out = null;
        try {
            Employee employee = cadreEmployeeReportDAO.getEmployeeData(empid);
            JSONObject jobj = new JSONObject(employee);
            out = response.getWriter();
            out.print(jobj);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
        }
    }
    
    @RequestMapping(value = "postIncumbencyChart", method = RequestMethod.GET)
    public String getholidayList(){
        String path = "/misreport/PostIncumbencyChart";
        return path;
    }
    @ResponseBody
    @RequestMapping(value = "getIncumbancyChart", method = RequestMethod.GET)
    public void getIncumbancyChart(HttpServletRequest request, @ModelAttribute("LoginUserBean") LoginUserBean lub,@RequestParam("spc") String spc, HttpServletResponse response){
        response.setContentType("application/json");
        PrintWriter out = null;
        try {
            ArrayList incumbancyReport = cadreEmployeeReportDAO.getIncumbancyChart(spc);
            JSONArray jobj = new JSONArray(incumbancyReport);
            out = response.getWriter();
            out.print(jobj);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
        }
    }
    @ResponseBody
    @RequestMapping(value = "getEmployeeIncumbancyChart", method = RequestMethod.GET)
    public void getEmployeeIncumbancyChart(HttpServletRequest request, @ModelAttribute("LoginUserBean") LoginUserBean lub,@RequestParam("empid") String empid, HttpServletResponse response){
        response.setContentType("application/json");
        PrintWriter out = null;
        try {
            ArrayList incumbancyReport = cadreEmployeeReportDAO.getEmployeeIncumbancyChart(empid);
            JSONArray jobj = new JSONArray(incumbancyReport);
            out = response.getWriter();
            out.print(jobj);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
        }
    }
}
