package hrms.controller.empprofile;

import hrms.common.CommonFunctions;
import hrms.dao.employee.EmployeeDAO;
import hrms.dao.employee.EmployeeDAOImpl;
import hrms.model.employee.Employee;
import hrms.model.loan.Loan;
import hrms.model.login.LoginUserBean;
import hrms.model.login.Users;
import java.io.IOException;
import java.io.PrintWriter;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

@Controller
@SessionAttributes({"LoginUserBean", "SelectedEmpObj"})
public class EmpProfileController {

    @Autowired
    public EmployeeDAO employeeDAO;

    @RequestMapping(value = "profile.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public String profileData(@ModelAttribute("SelectedEmpObj") Users user, @ModelAttribute("emp") Employee emp, Map<String, Object> model) {
        try {
            
            System.out.println("-------------" + user.getFullName());
            emp.setEmpName(user.getFullName());
            model.put("emp", emp);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return "/employee/EmployeeProfile";
    }

    @RequestMapping(value = "personalinfo.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public String personalInfoData(@ModelAttribute("SelectedEmpObj") Users user, @ModelAttribute("emp") Employee emp, BindingResult result, Map<String, Object> model, HttpServletResponse response) {
        try {
            emp.setEmpName(user.getFullName());
            emp.setGpfno(user.getGpfno());
            System.out.println("-----empid--" +CommonFunctions.decodedTxt(user.getEmpId()));
            emp.setEmpid(CommonFunctions.decodedTxt(user.getEmpId()));
            emp.setGisNo(user.getGisNo());
             System.out.println("-----gissssssssssss+--" +user.getGisType());
            emp.setGisType(user.getGisType());
            System.out.println("-----gender+--" +user.getGender());
            emp.setGender(user.getGender());
            System.out.println("-----mar+--" +user.getMarital());
            emp.setMarital(user.getMarital());
             System.out.println("-----bldddddddd+--" +user.getBloodGrp());
            emp.setBloodgrp(user.getBloodGrp());
           
            emp.setCategory(user.getCategory());
             System.out.println("-----goo+--========"+emp.getCategory());
           // emp.setDor(user.get);
             System.out.println("-----goo------+--" +user.getFormattedDoegov());
            emp.setJoindategoo(user.getFormattedDoegov());
            emp.setMobile(user.getMobile());
            emp.setDob(CommonFunctions.getFormattedOutputDate3(user.getDob()));
            model.put("emp", emp);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return "/employee/PersonalInfo";
    }

    @RequestMapping(value = "address.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public String addressData(@ModelAttribute("emp") Employee emp, BindingResult result, Map<String, Object> model, HttpServletResponse response) {
        try {
            System.out.println("-------------");
            model.put("emp", emp);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return "/employee/Address";
    }

    @RequestMapping(value = "identity.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public String identityData(@ModelAttribute("emp") Employee emp, BindingResult result, Map<String, Object> model, HttpServletResponse response) {
        try {
            System.out.println("-------------");
            model.put("emp", emp);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return "/employee/Identity";
    }

    @RequestMapping(value = "education.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public String educationData(@ModelAttribute("emp") Employee emp, BindingResult result, Map<String, Object> model, HttpServletResponse response) {
        try {
            System.out.println("-------------");
            model.put("emp", emp);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return "/employee/Education";
    }

    @RequestMapping(value = "family.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public String familyData(@ModelAttribute("emp") Employee emp, BindingResult result, Map<String, Object> model, HttpServletResponse response) {
        try {
            System.out.println("-------------");
            model.put("emp", emp);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return "/employee/Family";
    }

    @RequestMapping(value = "language.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public String languageData(@ModelAttribute("emp") Employee emp, BindingResult result, Map<String, Object> model, HttpServletResponse response) {
        try {
            System.out.println("-------------");
            model.put("emp", emp);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return "/employee/Language";
    }
    /* @RequestMapping(value = "saveProfileAction.htm", method = RequestMethod.POST)
    public void SaveAllowanceDeductionAction(@ModelAttribute("ChoosenEmployee") LoginUserBean lub, ModelMap model,@ModelAttribute("emp") Employee emp, BindingResult result, HttpServletResponse response) throws IOException {
         System.out.println("====inside ===save===");
    }*/
    @RequestMapping(value="/saveProfileAction.htm",method = RequestMethod.POST)  
    public ModelAndView saveProfile(@ModelAttribute("emp") Employee emp){  
         System.out.println("====inside ===saveeeeeeeeeeeeeeeeeeeeeee===");
        employeeDAO.saveProfile(emp);  
        return new ModelAndView("/employee/PersonalInfo");//will redirect to viewemp request mapping  
    }  
    
}
