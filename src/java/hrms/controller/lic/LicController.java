/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.controller.lic;

import hrms.dao.lic.LicDAO;
import hrms.model.lic.Lic;
import hrms.model.login.LoginUserBean;
import hrms.model.login.Users;
import hrms.model.payroll.allowancededcution.AllowanceDeduction;
import hrms.model.payroll.billbrowser.BillBrowserbean;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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

/**
 *
 * @author Manas Jena
 */
@Controller
@SessionAttributes({"LoginUserBean","SelectedEmpObj"})
public class LicController {
    @Autowired
    LicDAO licDAO;
    @RequestMapping(value = "employeeLicAction", method = RequestMethod.GET)
    public String employeeLicAction(ModelMap model, @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response) {//
        String path = "/lic/EmployeeLIC";

        return path;
    }
    @ResponseBody
    @RequestMapping(value = "getEmployeeWiseLICList")
    public void getEmployeeWiseLICList(HttpServletRequest request, @ModelAttribute("LoginUserBean") LoginUserBean lub, @ModelAttribute("SelectedEmpObj") Users selectedEmpObj, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = null;
        List adlist = licDAO.getLicList(selectedEmpObj.getEmpId());
        JSONArray json = new JSONArray(adlist);
        out = response.getWriter();
        out.write(json.toString());
    }
    @RequestMapping(value = "saveEmployeeLic", method = RequestMethod.POST)
    public void saveEmployeeLic(@ModelAttribute("PropertyStatement") Lic lic, @ModelAttribute("LoginUserBean") LoginUserBean lub, @ModelAttribute("SelectedEmpObj") Users selectedEmpObj, HttpServletResponse response) throws IOException{        
        lic.setEmpid(selectedEmpObj.getEmpId());              
        licDAO.saveLicData(lic);
    }
    @RequestMapping(value = "editEmployeeLic", method = RequestMethod.GET)
    public void editEmployeeLic(ModelMap model, @RequestParam("elId") BigDecimal elId, @ModelAttribute("LoginUserBean") LoginUserBean lub,@ModelAttribute("SelectedEmpObj") Users selectedEmpObj, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = null;
        JSONObject job = new JSONObject(licDAO.editLicData(selectedEmpObj.getEmpId(), elId));
        out = response.getWriter();
        out.write(job.toString());
    }
}
