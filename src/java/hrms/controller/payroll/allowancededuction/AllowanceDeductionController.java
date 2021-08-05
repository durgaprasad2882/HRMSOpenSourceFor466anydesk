/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.controller.payroll.allowancededuction;

import hrms.common.CommonFunctions;
import hrms.common.Message;
import hrms.dao.payroll.allowancededcution.AllowanceDeductionDAO;
import hrms.model.login.LoginUserBean;
import hrms.model.login.Users;
import hrms.model.payroll.allowancededcution.AllowanceDeduction;
import hrms.model.payroll.billbrowser.BillBrowserbean;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Manas Jena
 */
@Controller
@SessionAttributes({"LoginUserBean", "SelectedEmpObj"})
public class AllowanceDeductionController {

    @Autowired
    AllowanceDeductionDAO allowanceDeductionDAO;

    @RequestMapping(value = "employeeWiseUpdatedADAction")
    public ModelAndView EmployeeWiseUpdatedADAction(ModelMap model, @ModelAttribute("AllowanceDeductionbean") AllowanceDeduction albean, @ModelAttribute("SelectedEmpObj") Users lub, BindingResult result, HttpServletResponse response) {        
        ArrayList adlist = new ArrayList();
        albean.setWhereupdated("E");        
        albean.setUpdationRefCode(CommonFunctions.decodedTxt(lub.getEmpId()));
        ModelAndView mav = new ModelAndView("/allowancededuction/EmploeeSpecificAD", "command", albean);
        if (albean.getAdtype() != null) {
            if (albean.getAdtype().equals("A")) {
                adlist = allowanceDeductionDAO.getEmployeeWiseAllowance(albean.getUpdationRefCode());
            } else if (albean.getAdtype().equals("D")) {
                adlist = allowanceDeductionDAO.getEmployeeWiseDeduction(albean.getUpdationRefCode());
            } else if (albean.getAdtype().equals("P")) {
                adlist = allowanceDeductionDAO.getEmployeeWisePvtDeduction(albean.getUpdationRefCode());
            }
            mav.addObject("adlist", adlist);
        }
        return mav;
    }
    @RequestMapping(value = "allowanceAndDeductionEdit")
    public ModelAndView AllowanceAndDeductionEdit(ModelMap model, @ModelAttribute("AllowanceDeductionbean") AllowanceDeduction albean, @ModelAttribute("SelectedEmpObj") Users lub, BindingResult result, HttpServletResponse response) {                
        AllowanceDeduction talbean = allowanceDeductionDAO.getAllowanceDeductionDetail(albean.getAdcode());
        albean.setAddesc(talbean.getAddesc());
        albean.setAdamttype(talbean.getAdamttype());
        albean.setAdtype(talbean.getAdtype());
        albean.setUpdationRefCode(CommonFunctions.decodedTxt(lub.getEmpId()));
        albean = allowanceDeductionDAO.getUpdatedAllowanceDeduction(albean);
        ModelAndView mav = new ModelAndView("/allowancededuction/EmployeeSpecificADEdit", "command", albean);
        return mav;
    }
    @RequestMapping(value = "saveAllowanceAndDeduction",params = "Save")
    public ModelAndView SaveAllowanceAndDeduction(ModelMap model, @ModelAttribute("AllowanceDeductionbean") AllowanceDeduction albean, @ModelAttribute("SelectedEmpObj") Users lub, BindingResult result, HttpServletResponse response) {        
        ModelAndView mav = new ModelAndView("/allowancededuction/EmploeeSpecificAD", "command", albean);
        ArrayList adlist = new ArrayList();
        albean.setWhereupdated("E");
        albean.setUpdationRefCode(CommonFunctions.decodedTxt(lub.getEmpId()));
        allowanceDeductionDAO.saveAllowanceDeductionDetail(albean);
        if (albean.getAdtype() != null) {
            if (albean.getAdtype().equals("A")) {
                adlist = allowanceDeductionDAO.getEmployeeWiseAllowance(albean.getUpdationRefCode());
            } else if (albean.getAdtype().equals("D")) {
                adlist = allowanceDeductionDAO.getEmployeeWiseDeduction(albean.getUpdationRefCode());
            } else if (albean.getAdtype().equals("P")) {
                adlist = allowanceDeductionDAO.getEmployeeWisePvtDeduction(albean.getUpdationRefCode());
            }
            mav.addObject("adlist", adlist);
        }
        return mav;
    }
    @RequestMapping(value = "saveAllowanceAndDeduction",params = "Cancel")
    public ModelAndView CancelAllowanceAndDeductionEdit(ModelMap model, @ModelAttribute("AllowanceDeductionbean") AllowanceDeduction albean, @ModelAttribute("SelectedEmpObj") Users lub, BindingResult result, HttpServletResponse response) {        
        ModelAndView mav = new ModelAndView("/allowancededuction/EmploeeSpecificAD", "command", albean);
        ArrayList adlist = new ArrayList();
        albean.setWhereupdated("E");
        albean.setUpdationRefCode(CommonFunctions.decodedTxt(lub.getEmpId()));
        allowanceDeductionDAO.saveAllowanceDeductionDetail(albean);
        if (albean.getAdtype() != null) {
            if (albean.getAdtype().equals("A")) {
                adlist = allowanceDeductionDAO.getEmployeeWiseAllowance(albean.getUpdationRefCode());
            } else if (albean.getAdtype().equals("D")) {
                adlist = allowanceDeductionDAO.getEmployeeWiseDeduction(albean.getUpdationRefCode());
            } else if (albean.getAdtype().equals("P")) {
                adlist = allowanceDeductionDAO.getEmployeeWisePvtDeduction(albean.getUpdationRefCode());
            }
            mav.addObject("adlist", adlist);
        }
        return mav;
    }
    @RequestMapping(value = "officeWiseUpdatedADAction", method = RequestMethod.GET)
    public String OfficeWiseUpdatedADAction(ModelMap model, @ModelAttribute("BillBrowserbean") BillBrowserbean bbbean, @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response) {//
        String path = "/allowancededuction/OfficeSpecificAD";

        return path;
    }

    @ResponseBody
    @RequestMapping(value = "getEmployeeWiseADList", method = RequestMethod.GET)
    public void getEmployeeWiseADList(HttpServletRequest request, @ModelAttribute("AllowanceDeductionbean") AllowanceDeduction albean, @ModelAttribute("LoginUserBean") LoginUserBean lub, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = null;
        ArrayList adlist = new ArrayList();
        if (albean.getAdtype().equals("A")) {
            adlist = allowanceDeductionDAO.getEmployeeWiseAllowance("08000437");
        } else if (albean.getAdtype().equals("D")) {
            adlist = allowanceDeductionDAO.getEmployeeWiseDeduction("08000437");
        } else if (albean.getAdtype().equals("P")) {
            adlist = allowanceDeductionDAO.getEmployeeWisePvtDeduction("08000437");
        }

        JSONArray json = new JSONArray(adlist);
        out = response.getWriter();
        out.write(json.toString());
    }

    @ResponseBody
    @RequestMapping(value = "getOfficeWiseADList")
    public void getOfficeWiseADList(HttpServletRequest request, @ModelAttribute("AllowanceDeductionbean") AllowanceDeduction albean, @ModelAttribute("LoginUserBean") LoginUserBean lub, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = null;
        ArrayList adlist = new ArrayList();
        if (albean.getAdtype().equals("A")) {
            adlist = allowanceDeductionDAO.getOfficeWiseAllowance(lub.getOffcode());
        } else if (albean.getAdtype().equals("D")) {
            adlist = allowanceDeductionDAO.getOfficeWiseDeduction(lub.getOffcode());
        } else if (albean.getAdtype().equals("P")) {
            adlist = allowanceDeductionDAO.getOfficeWisePvtDeduction(lub.getOffcode());
        }

        JSONArray json = new JSONArray(adlist);
        out = response.getWriter();
        out.write(json.toString());
    }

    @RequestMapping(value = "editAllowanceDeductionAction", method = RequestMethod.GET)
    public void EditAllowanceDeductionAction(ModelMap model, @ModelAttribute("AllowanceDeduction") AllowanceDeduction adbean, @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = null;
        AllowanceDeduction ad = new AllowanceDeduction();
        if (adbean.getWhereupdated().equals("G")) {
            ad = allowanceDeductionDAO.getAllowanceDeductionDetail(adbean.getAdcode());
        }else if (adbean.getWhereupdated().equals("O")) {
            ad = allowanceDeductionDAO.getUpdatedAllowanceDeductionDetail(adbean.getAdcode());
        }
        JSONObject job = new JSONObject(ad);
        out = response.getWriter();
        out.write(job.toString());
    }

    @RequestMapping(value = "saveAllowanceDeductionAction", method = RequestMethod.POST)
    public void SaveAllowanceDeductionAction(ModelMap model, @ModelAttribute("AllowanceDeduction") AllowanceDeduction adbean, @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = null;
        Message msg = null;
        if (adbean.getWhereupdated().equals("G")) {            
            adbean.setUpdationRefCode(lub.getOffcode());
            msg = allowanceDeductionDAO.saveAllowanceDeductionDetail(adbean);
        }else if (adbean.getWhereupdated().equals("O")) {
            
        }
        System.out.println(adbean);
        JSONObject job = new JSONObject(msg);
        out = response.getWriter();
        out.write(job.toString());
    }
}
