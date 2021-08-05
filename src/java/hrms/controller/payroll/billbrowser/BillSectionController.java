/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.controller.payroll.billbrowser;

import hrms.dao.payroll.billbrowser.SectionDefinationDAO;
import hrms.model.login.LoginUserBean;
import hrms.model.payroll.billbrowser.SectionDefinition;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
@SessionAttributes("LoginUserBean")
public class BillSectionController {

    @Autowired
    SectionDefinationDAO sectionDefinationDAO;

    @RequestMapping(value = "billSectionAction")
    public ModelAndView billBrowserAction(ModelMap model, @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response) {//
        String path = "/payroll/SectionDefination";
        ArrayList sectionList = sectionDefinationDAO.getSectionList(lub.getOffcode());
        ModelAndView mav = new ModelAndView();
        mav.addObject("sectionList", sectionList);
        mav.setViewName(path);
        return mav;
    }

    @RequestMapping(value = "sectionMapping", method = RequestMethod.GET)
    public ModelAndView sectionMapping(ModelMap model, @ModelAttribute("LoginUserBean") LoginUserBean lub, @RequestParam("sectionId") int sectionId, BindingResult result, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView();        
        String billtype = sectionDefinationDAO.getBillType(sectionId);
        String path = null;
        if (billtype == null || billtype.equals("") || billtype.equals("REGULAR")) {
            ArrayList availableEmpList = sectionDefinationDAO.getTotalAvailableEmp(lub.getOffcode());
            ArrayList assignEmpList = sectionDefinationDAO.getTotalAssignEmp(lub.getOffcode(), sectionId);
            mav.addObject("availableEmpList", availableEmpList);
            mav.addObject("assignEmpList", assignEmpList);
            mav.addObject("sectionId", sectionId);
            mav.addObject("sectionName", sectionDefinationDAO.getSectionName(sectionId));
            path = "/payroll/SectionDefinitionMapping";
            mav.setViewName(path);
        }
        if (billtype != null && billtype.equals("CONTRACTUAL")) {
            ArrayList availableEmpList = sectionDefinationDAO.getTotalAvailableContractEmp(lub.getOffcode());
            ArrayList assignEmpList = sectionDefinationDAO.getTotalAssignContractEmp(lub.getOffcode(), sectionId);
            mav.addObject("availableEmpList", availableEmpList);
            mav.addObject("assignEmpList", assignEmpList);
            mav.addObject("sectionId", sectionId);
            mav.addObject("sectionName", sectionDefinationDAO.getSectionName(sectionId));
            path="contractualmapping";
        }
        return mav;
    }

    @ResponseBody
    @RequestMapping(value = "getBillSectionList", method = RequestMethod.POST)
    public void getBillYearlList(HttpServletRequest request, @ModelAttribute("LoginUserBean") LoginUserBean lub, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = null;
        ArrayList sectionList = sectionDefinationDAO.getSectionList(lub.getOffcode());
        JSONArray json = new JSONArray(sectionList);
        out = response.getWriter();
        out.write(json.toString());
    }
    
    @ResponseBody
    @RequestMapping(value = "assignPostAction", method = RequestMethod.POST)
    public void assignPostAction(@ModelAttribute("LoginUserBean") LoginUserBean lub, @RequestParam("spc") String spc, @RequestParam("sectionId") int sectionId, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = null;
        sectionDefinationDAO.mapPost(sectionId,spc);
        out = response.getWriter();
        out.write("S");
    }
    
    @ResponseBody
    @RequestMapping(value = "removePostAction", method = RequestMethod.POST)
    public void removePostAction(@ModelAttribute("LoginUserBean") LoginUserBean lub, @RequestParam("spc") String spc, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = null;
        sectionDefinationDAO.removePost(spc);
        out = response.getWriter();
        out.write("S");
    }
    @RequestMapping(value = "addBillSection")
    public ModelAndView addBillSection(ModelMap model, @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response) {//
        String path = "/payroll/AddBillSection";
        ModelAndView mav = new ModelAndView();
        mav.addObject("sectionId", 0);
        mav.setViewName(path);
        return mav;
    }
@RequestMapping(value = "saveBillSection", method = RequestMethod.POST)
    public ModelAndView saveBillSection(HttpServletResponse response, @ModelAttribute("LoginUserBean") LoginUserBean lub, @ModelAttribute("billSection") SectionDefinition SD) {

        try {
            String offCode = lub.getOffcode();
            int sectionId = SD.getSectionId();

            if(sectionId > 0)
            {
                sectionDefinationDAO.updateBillSection(SD);
            }
                else
            {
                sectionDefinationDAO.saveBillSection(offCode, SD);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ModelAndView("redirect:/billSectionAction.htm?result=success");
    }    
        @RequestMapping(value = "editBillSection")
    public ModelAndView editBillSection(ModelMap model, @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response, @RequestParam("sectionId") int sectionId) {//
        
        String path = "/payroll/AddBillSection";
        SectionDefinition SD = sectionDefinationDAO.getBillSection(sectionId);
        ModelAndView mav = new ModelAndView();
        mav.addObject("section", SD.getSection());
        mav.addObject("billType", SD.getBillType());
        mav.addObject("nofEmp", SD.getNofpost());
        mav.addObject("sectionId", sectionId);
        mav.setViewName(path);
        return mav;
    }
}
