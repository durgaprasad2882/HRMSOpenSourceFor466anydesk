/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.controller.propertystatement;

import hrms.dao.fiscalyear.FiscalYearDAO;
import hrms.dao.propertystatement.PropertyStatementDAO;
import hrms.model.login.LoginUserBean;
import hrms.model.propertystatement.PropertyDetail;
import hrms.model.propertystatement.PropertyStatement;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

@Controller
@SessionAttributes("LoginUserBean")
public class PropertyStatementController {

    @Autowired
    PropertyStatementDAO propertyStatementDAO;
    @Autowired
    public FiscalYearDAO fiscalDAO;

    @RequestMapping(value = "viewpropertystatement.htm", method = RequestMethod.GET)
    public ModelAndView viewPropertyStatement(ModelMap model, HttpServletRequest request, @ModelAttribute("LoginUserBean") LoginUserBean lub, @RequestParam("yearlyPropId") BigDecimal yearlyPropId) {
        PropertyStatement propertyStatement = propertyStatementDAO.getPropertyStmt(yearlyPropId);
        ArrayList movablePropertyDetailList = propertyStatementDAO.getMovablePropertyDetailList(yearlyPropId);
        ArrayList immovablePropertyDetailList = propertyStatementDAO.getImmovablePropertyDetailList(yearlyPropId);
        //model.addAttribute("propertyStatement", propertyStatement);
        ModelAndView mv = new ModelAndView("/propertystatement/ViewPropertyStatement");
        mv.addObject("propertyStatement", propertyStatement);
        mv.addObject("movablePropertyDetailList", movablePropertyDetailList);
        mv.addObject("immovablePropertyDetailList", immovablePropertyDetailList);
        return mv;
    }

    @RequestMapping(value = "viewpropertystatementlist.htm", method = RequestMethod.GET)
    public String viewPropertyStatementList() {
        return "/propertystatement/PropertyStatementList";
    }

    @ResponseBody
    @RequestMapping(value = "getPropertStatementList", method = RequestMethod.POST)
    public void getPropertStatementList(HttpServletRequest request, @ModelAttribute("LoginUserBean") LoginUserBean lub, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = null;
        ArrayList propertyStatementList = propertyStatementDAO.getPropertyList(lub.getEmpid());
        JSONArray json = new JSONArray(propertyStatementList);
        out = response.getWriter();
        out.write(json.toString());
    }
    @ResponseBody
    @RequestMapping(value = "savePropertStatement", method = RequestMethod.POST)
    public void savePropertStatement(@ModelAttribute("PropertyStatement") PropertyStatement propertyStatement, @ModelAttribute("LoginUserBean") LoginUserBean lub, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = null;
        propertyStatement.setEmpid(lub.getEmpid());
        propertyStatement.setCurspc(lub.getSpc());
        propertyStatement.setCurcadrecode(lub.getCadrecode());
        boolean isDuplicatePeriod = propertyStatementDAO.isDuplicatePropertyPeriod(propertyStatement);
        Map result = new HashMap();        
        if (isDuplicatePeriod == false) {
            propertyStatementDAO.savePropertyStmt(propertyStatement);
            result.put("msg", "Sucessfully Saved");
        }else{
            result.put("msg", "Duplicate Period");
        }
        
        JSONObject jobj = new JSONObject(result);
        out = response.getWriter();
        out.write(jobj.toString());
    }

    @ResponseBody
    @RequestMapping(value = "getPropertyStmt", method = RequestMethod.GET)
    public void getPropertyStmt(ModelMap model, @RequestParam("yearlyPropId") BigDecimal yearlyPropId, @ModelAttribute("LoginUserBean") LoginUserBean lub, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = null;
        PropertyStatement propertyStatement = propertyStatementDAO.getPropertyStmt(yearlyPropId);
        JSONObject obj = new JSONObject(propertyStatement);
        out = response.getWriter();
        out.write(obj.toString());
    }
    @ResponseBody
    @RequestMapping(value = "deletePropertyStmt", method = RequestMethod.GET)
    public void deletePropertyStmt(ModelMap model, @RequestParam("yearlyPropId") BigDecimal yearlyPropId, @ModelAttribute("LoginUserBean") LoginUserBean lub, HttpServletResponse response) throws IOException, JSONException {
        response.setContentType("application/json");
        PrintWriter out = null;
        boolean isDeleted = propertyStatementDAO.deletePropertyStmt(yearlyPropId,lub.getEmpid());
        JSONObject obj = new JSONObject();
        obj.append("isDeleted", isDeleted);
        out = response.getWriter();
        out.write(obj.toString());
    }
    @ResponseBody
    @RequestMapping(value = "getMovablePropertyDetailList", method = RequestMethod.POST)
    public void getMovablePropertyDetailList(ModelMap model, @RequestParam("yearlyPropId") BigDecimal yearlyPropId, @ModelAttribute("LoginUserBean") LoginUserBean lub, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = null;
        ArrayList propertyDetailList = propertyStatementDAO.getMovablePropertyDetailList(yearlyPropId);
        JSONArray json = new JSONArray(propertyDetailList);
        out = response.getWriter();
        out.write(json.toString());
    }

    @ResponseBody
    @RequestMapping(value = "submitPropertyStatement", method = RequestMethod.POST)
    public void submitPropertyStatement(ModelMap model, @RequestParam("yearlyPropId") BigDecimal yearlyPropId, @ModelAttribute("LoginUserBean") LoginUserBean lub, HttpServletResponse response) throws Exception {
        response.setContentType("application/json");
        PrintWriter out = null;
        boolean isSubmitted = propertyStatementDAO.submitPropertyStatement(yearlyPropId, lub.getEmpid());
        JSONObject obj = new JSONObject();
        obj.append("isSubmitted", isSubmitted);
        out = response.getWriter();
        out.write(obj.toString());
    }

    @ResponseBody
    @RequestMapping(value = "getImmovablePropertyDetailList", method = RequestMethod.POST)
    public void getImmovablePropertyDetailList(ModelMap model, @RequestParam("yearlyPropId") BigDecimal yearlyPropId, @ModelAttribute("LoginUserBean") LoginUserBean lub, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = null;
        ArrayList propertyDetailList = propertyStatementDAO.getImmovablePropertyDetailList(yearlyPropId);
        JSONArray json = new JSONArray(propertyDetailList);
        out = response.getWriter();
        out.write(json.toString());
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        dateFormat.setLenient(false);        
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    @RequestMapping(value = "saveImmovablePropertyDetail", method = {RequestMethod.GET, RequestMethod.POST})
    public void saveImmovablePropertyDetail(ModelMap model, @ModelAttribute("LoginUserBean") LoginUserBean lub,  @ModelAttribute("PropertyDetail") PropertyDetail prtdtl, HttpServletResponse response) throws IOException {
        int mCode = propertyStatementDAO.saveImmovableProperty(prtdtl);
        PrintWriter out = null;
        Map result = new HashMap();
        result.put("mCode", mCode);
        JSONObject job = new JSONObject(result);
        out = response.getWriter();
        out.write(job.toString());
    }

    @RequestMapping(value = "saveMovablePropertyDetail", method = RequestMethod.POST)
    public void saveMovablePropertyDetail(ModelMap model, @ModelAttribute("LoginUserBean") LoginUserBean lub, @ModelAttribute("PropertyDetail") PropertyDetail prtdtl, HttpServletResponse response) throws IOException {
        int mCode = propertyStatementDAO.saveMovableProperty(prtdtl);
       // System.out.println("mCode:" + mCode);
        PrintWriter out = null;
        Map result = new HashMap();
        result.put("mCode", mCode);
        JSONObject job = new JSONObject(result);
        out = response.getWriter();
        out.write(job.toString());
    }

    @ResponseBody
    @RequestMapping(value = "getImmovableProperty", method = RequestMethod.GET)
    public void getImmovableProperty(ModelMap model, @RequestParam("propertyDtlsId") BigDecimal propertyDtlsId, @ModelAttribute("LoginUserBean") LoginUserBean lub, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = null;
        PropertyDetail propertyDetail = propertyStatementDAO.getImmovableProperty(propertyDtlsId);
        JSONObject obj = new JSONObject(propertyDetail);
        out = response.getWriter();
        out.write(obj.toString());
    }

    @ResponseBody
    @RequestMapping(value = "deleteImmovableProperty", method = RequestMethod.POST)
    public void deleteImmovableProperty(ModelMap model, @RequestParam("propertyDtlsId") BigDecimal propertyDtlsId, @ModelAttribute("LoginUserBean") LoginUserBean lub, HttpServletResponse response) throws Exception {
        response.setContentType("application/json");
        PrintWriter out = null;
        boolean isDeleted = propertyStatementDAO.deleteImmovableProperty(propertyDtlsId, lub.getEmpid());
        JSONObject obj = new JSONObject();
        obj.append("isDeleted", isDeleted);
        out = response.getWriter();
        out.write(obj.toString());
    }

    @ResponseBody
    @RequestMapping(value = "getMovableProperty", method = RequestMethod.GET)
    public void getMovableProperty(ModelMap model, @RequestParam("propertyDtlsId") BigDecimal propertyDtlsId, @ModelAttribute("LoginUserBean") LoginUserBean lub, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = null;
      //  System.out.println("propertyDtlsId is: "+propertyDtlsId);
        PropertyDetail propertyDetail = propertyStatementDAO.getMovableProperty(propertyDtlsId);
        JSONObject obj = new JSONObject(propertyDetail);
        out = response.getWriter();
        out.write(obj.toString());
    }

    @ResponseBody
    @RequestMapping(value = "deleteMovableProperty", method = RequestMethod.POST)
    public void deleteMovableProperty(ModelMap model, @RequestParam("propertyDtlsId") BigDecimal propertyDtlsId, @ModelAttribute("LoginUserBean") LoginUserBean lub, HttpServletResponse response) throws Exception {
        response.setContentType("application/json");
        PrintWriter out = null;
        boolean isDeleted = propertyStatementDAO.deleteMovableProperty(propertyDtlsId, lub.getEmpid());
        JSONObject obj = new JSONObject();
        obj.append("isDeleted", isDeleted);
        out = response.getWriter();
        out.write(obj.toString());
    }

    @RequestMapping(value = "GetPFiscalYearListJSON", method = RequestMethod.POST)
    public @ResponseBody
    String getPFiscalYearListJSON() {

        JSONArray json = null;
        try {
            List fiscalyearlist = fiscalDAO.getPFiscalYearList();
            json = new JSONArray(fiscalyearlist);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return json.toString();
    }
}
