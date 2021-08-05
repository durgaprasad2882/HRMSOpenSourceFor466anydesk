/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.controller.master;

import hrms.dao.master.DepartmentDAO;
import hrms.dao.master.PayScaleDAO;
import hrms.dao.master.SubStantivePostDAO;
import hrms.model.login.LoginUserBean;
import hrms.model.master.SubstantivePost;
import hrms.model.privilege.Privilege;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
 * @author Durga
 */
@Controller
@SessionAttributes("LoginUserBean")
public class SPCMasterController {

    @Autowired
    SubStantivePostDAO subStantivePostDao;

    @Autowired
    public PayScaleDAO payscaleDAO;

    @Autowired
    DepartmentDAO departmentDao;

    String offCode = "";

    @ResponseBody
    @RequestMapping(value = "getPostListLoanJSON", method = {RequestMethod.GET, RequestMethod.POST})
    public String getPostListMasterJSON(@RequestParam("offcode") String offcode) {
        JSONArray json = null;
        try {
            List postlist = subStantivePostDao.getSPCList(offcode);
            json = new JSONArray(postlist);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return json.toString();
    }

    @ResponseBody
    @RequestMapping(value = "getEmployeeWithSPCList", method = {RequestMethod.GET, RequestMethod.POST})
    public String getEmployeeWithSPCList(@RequestParam("offcode") String offcode) {
        JSONArray json = null;
        try {
            List postlist = subStantivePostDao.getEmployeeWithSPCList(offcode);
            json = new JSONArray(postlist);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return json.toString();
    }

    @ResponseBody
    @RequestMapping(value = "getTrainingPostListJSON", method = {RequestMethod.GET, RequestMethod.POST})
    public String getPostListJSON(@RequestParam("offcode") String offcode) {
        JSONArray json = null;
        try {
            List postlist = subStantivePostDao.getSPCList(offcode);
            json = new JSONArray(postlist);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return json.toString();
    }

    @ResponseBody
    @RequestMapping(value = "getCadreWiseOfficeWiseSPC", method = {RequestMethod.GET, RequestMethod.POST})
    public String getCadreWiseOfficeWiseSPC(@RequestParam("offcode") String offcode, @RequestParam("cadrecode") String cadrecode) {
        JSONArray json = null;
        try {
            List postlist = subStantivePostDao.getCadreWiseOfficeWiseSPC(cadrecode, offcode);
            json = new JSONArray(postlist);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return json.toString();
    }

    @RequestMapping(value = "getSPCList")
    public String getOfficeList(@RequestParam("offcode") String offcode) {

        this.offCode = offcode;
        return "/post/Post";

    }

    @ResponseBody
    @RequestMapping(value = "getSPCListJSONPaging")
    public String getSPCListJSONPaging(HttpServletResponse response, @RequestParam Map<String, String> requestParams) {

        JSONObject json = new JSONObject();
        int total = 0;
        try {
            int page = Integer.parseInt(requestParams.get("page"));
            int rows = Integer.parseInt(requestParams.get("rows"));
            String postToSearch = requestParams.get("postToSearch");

            List postlist = subStantivePostDao.getPostListPaging(offCode, postToSearch, page, rows);
            total = subStantivePostDao.getPostListCountPaging(offCode, postToSearch);

            json.put("rows", postlist);
            json.put("total", total);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    @ResponseBody
    @RequestMapping(value = "getPostCodeListJSON")
    public String getPostCodeListJSON(@RequestParam("offcode") String offcode) {
        JSONArray json = null;
        try {
            List postlist = subStantivePostDao.getGenericPostList(offcode);
            json = new JSONArray(postlist);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return json.toString();
    }

    @ResponseBody
    @RequestMapping(value = "getPostCodeWiseEmployeeListJSON")
    public String GetPostCodeWiseEmployeeListJSON(@RequestParam("postcode") String postcode, @RequestParam("offcode") String offcode) {
        JSONArray json = null;
        try {
            List emplist = subStantivePostDao.getGPCWiseEmployeeList(postcode, offcode);
            json = new JSONArray(emplist);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return json.toString();
    }

    @ResponseBody
    @RequestMapping(value = "getPostCodeWiseEmployeeListSPCJSON")
    public String GetPostCodeWiseEmployeeListSPCJSON(@RequestParam("postcode") String postcode) {
        JSONArray json = null;
        try {
            List emplist = subStantivePostDao.getGPCWiseEmployeeListOnlySPC(postcode);
            json = new JSONArray(emplist);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return json.toString();
    }

    @ResponseBody
    @RequestMapping(value = "getOfficeWithSPCList")
    public String getOfficeWithSPCList(@RequestParam("offcode") String offcode) {
        JSONArray json = null;
        try {
            List postlist = subStantivePostDao.getOfficeWithSPCList(offcode);
            json = new JSONArray(postlist);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return json.toString();
    }

    @ResponseBody
    @RequestMapping(value = "getSanctioningSPCOfficeWiseListJSON")
    public String GetSanctioningSPCOfficeWiseListJSON(@RequestParam("offcode") String offcode) {
        JSONArray json = null;
        try {
            List spclist = subStantivePostDao.getSanctioningSPCOfficeWiseList(offcode);
            json = new JSONArray(spclist);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    @RequestMapping(value = "GPCWiseSPCList")
    public String GPCWiseSPCList(Model model, @ModelAttribute("LoginUserBean") LoginUserBean lub, @ModelAttribute("substantivePost") SubstantivePost substantivePost) {

        List deptlist = null;
        List postlist = null;
        try {
            //System.out.println("Off Code is: "+lub.getOffcode());
            deptlist = departmentDao.getDepartmentList();
            model.addAttribute("deptlist", deptlist);

            postlist = subStantivePostDao.getPostCodeOfficeWise(lub.getOffcode());
            model.addAttribute("postlist", postlist);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "/master/SubstantivePostEdit";
    }

    @RequestMapping(value = "saveSubstantivePost")
    public String saveSubstantivePost(Model model, @ModelAttribute("LoginUserBean") LoginUserBean lub, @ModelAttribute("substantivePost") SubstantivePost substantivePost, @RequestParam("btnSpc") String btnSpc) {

        List deptlist = null;
        List postlist = null;
        List spnList = null;
        List payscaleList = null;
        int retVal = 0;
        try {
            deptlist = departmentDao.getDepartmentList();
            model.addAttribute("deptlist", deptlist);
            postlist = subStantivePostDao.getPostCodeOfficeWise(lub.getOffcode());
            model.addAttribute("postlist", postlist);
            if (btnSpc != null && btnSpc.equals("List")) {
                spnList = subStantivePostDao.getSPCListWithEmployeeName(lub.getOffcode(), substantivePost.getPostCode());
                model.addAttribute("spnList", spnList);

                payscaleList = payscaleDAO.getPayScaleList();
                model.addAttribute("payscaleList", payscaleList);
            } else if (btnSpc != null && btnSpc.equals("Update")) {
                if (substantivePost.getMode() != null && substantivePost.getMode().equals("single")) {
                    retVal = subStantivePostDao.updateSubstantivePost(lub.getOffcode(), substantivePost.getPostCode(), substantivePost.getSpc(), substantivePost.getPayscale(), substantivePost.getPayscale_7th(), substantivePost.getPostgrp(), substantivePost.getPaylevel(), substantivePost.getGp(), substantivePost.getCadre(),substantivePost.getChkGrantInAid(),substantivePost.getChkTeachingPost(),substantivePost.getChkPlanOrNonPlan());
                } else if (substantivePost.getMode() != null && substantivePost.getMode().equals("all")) {
                    retVal = subStantivePostDao.updateSubstantivePost(lub.getOffcode(), substantivePost.getPostCode(), null, substantivePost.getPayscale(), substantivePost.getPayscale_7th(), substantivePost.getPostgrp(), substantivePost.getPaylevel(), substantivePost.getGp(), substantivePost.getCadre(),substantivePost.getChkGrantInAid(),substantivePost.getChkTeachingPost(),substantivePost.getChkPlanOrNonPlan());
                }
                model.addAttribute("retVal", retVal);
            } else if (btnSpc != null && btnSpc.equals("Remove")) {
                retVal = subStantivePostDao.removeSubstantivePost(lub.getOffcode(), substantivePost.getPostCode(), substantivePost.getSpc());
            } else if (btnSpc != null && btnSpc.equals("AddPost")) {
                retVal = subStantivePostDao.addSubstantivePost(lub.getDeptcode(), lub.getOffcode(), substantivePost);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "/master/SubstantivePostEdit";
    }

    @RequestMapping(value = "substantivePost", method = {RequestMethod.GET, RequestMethod.POST})
    public String substansivePost(ModelMap model, @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response) {
        String path = "/master/SubstantivePost";
        model.addAttribute("deptList", departmentDao.getDepartmentList());
        return path;
    }

    @RequestMapping(value = "substantivePostDetails", method = {RequestMethod.GET, RequestMethod.POST})
    public String substantivePostDetails(ModelMap model, @ModelAttribute("substantivePost") SubstantivePost substantivePost, BindingResult result, HttpServletResponse response, @RequestParam("hiddenDeptName") String DeptName, @RequestParam("hiddenOfficeName") String officeName) {
        model.addAttribute("DeptName", DeptName);
        model.addAttribute("officeName", officeName);
        model.addAttribute("subList", subStantivePostDao.listSubPost(substantivePost));
        String path = "/master/SubstantivePostDetails";        
        return path;
    }

    @RequestMapping(value = "addPostdetails", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView addPostdetails(ModelMap model, @ModelAttribute("substantivePost") SubstantivePost substantivePost, BindingResult result, HttpServletResponse response, @RequestParam("deptCode") String deptCode, @RequestParam("officeName") String officeName, @RequestParam("officeCode") String officeCode, @RequestParam("postname") String postname, @RequestParam("postCode") String postCode) {
        model.addAttribute("deptCode", deptCode);
        String offname = URLEncoder.encode(officeName);
        model.addAttribute("officeName", offname);
        model.addAttribute("officeCode", officeCode);
        model.addAttribute("postName", postname);
        model.addAttribute("postCode", postCode);
        
        return new ModelAndView("/master/AddPostDetails");
    }

    @RequestMapping(value = "savePostdataDetails", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView savePostdataDetails(ModelMap model, @ModelAttribute("substantivePost") SubstantivePost substantivePost, BindingResult result, HttpServletResponse response) {

        subStantivePostDao.savePostdataDetails(substantivePost);
        model.addAttribute("deptList", departmentDao.getDepartmentList());
        // String path = "/privilege/SubstantivePost";
        return new ModelAndView("/master/SubstantivePost");
    }
}
