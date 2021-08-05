/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.controller.payroll.billbrowser;

import hrms.dao.payroll.billbrowser.BillGroupDAO;
import hrms.model.login.LoginUserBean;
import hrms.model.payroll.billbrowser.BillGroup;
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
public class BillGroupController {

    @Autowired
    BillGroupDAO billGroupDAO;

    @RequestMapping(value = "billGroupAction", method = RequestMethod.GET)
    public ModelAndView billBrowserAction(ModelMap model, @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response) {//
        String path = "/payroll/BillGroupList";
        ArrayList billGroupList = billGroupDAO.getBillGroupList(lub.getOffcode());
        //System.out.println(lub.getOffcode());
        ModelAndView mav = new ModelAndView();
        mav.addObject("groupList", billGroupList);
        mav.setViewName(path);
        return mav;
        //return path;
    }

    @RequestMapping(value = "addGroupList")
    public ModelAndView addBillSection(ModelMap model, @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response) {//
        String path = "/payroll/AddGroupList";
        ModelAndView mav = new ModelAndView();
        ArrayList planStatusList = billGroupDAO.getPlanStatusList();
        ArrayList billSectorList = billGroupDAO.getSectorList();
        ArrayList billPostClassList = billGroupDAO.getPostClassList();
        ArrayList billTypeList = billGroupDAO.getBillTypeList();
        ArrayList billGroupList = billGroupDAO.getDemandList();
        mav.addObject("billGroupList", billGroupList);
        mav.addObject("billTypeList", billTypeList);
        mav.addObject("billPostClassList", billPostClassList);
        mav.addObject("billSectorList", billSectorList);
        mav.addObject("planStatusList", planStatusList);
        mav.setViewName(path);
        return mav;
    }

    @RequestMapping(value = "saveGroupSection", method = RequestMethod.POST)
    public ModelAndView billBrowserAction(ModelMap model, @ModelAttribute("LoginUserBean") LoginUserBean lub, @ModelAttribute("GroupSection") BillGroup BG, BindingResult result, HttpServletResponse response) {//
        billGroupDAO.saveGroupSection(lub.getOffcode(), BG);
        return new ModelAndView("redirect:/billGroupAction.htm");
    }

    @ResponseBody
    @RequestMapping(value = "getBillGroupList", method = RequestMethod.POST)
    public void getBillYearlList(HttpServletRequest request, @ModelAttribute("LoginUserBean") LoginUserBean lub, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = null;
        ArrayList billGroupList = billGroupDAO.getBillGroupList(lub.getOffcode());
        JSONArray json = new JSONArray(billGroupList);
        out = response.getWriter();
        out.write(json.toString());
    }

    @ResponseBody
    @RequestMapping(value = "getPlanStatusList")
    public void getPlanStatusList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = null;
        ArrayList planStatusList = billGroupDAO.getPlanStatusList();
        JSONArray json = new JSONArray(planStatusList);
        out = response.getWriter();
        out.write(json.toString());
    }

    @ResponseBody
    @RequestMapping(value = "getSectorList")
    public void getSectorList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = null;
        ArrayList billSectorList = billGroupDAO.getSectorList();
        JSONArray json = new JSONArray(billSectorList);
        out = response.getWriter();
        out.write(json.toString());
    }

    @ResponseBody
    @RequestMapping(value = "getDemandList")
    public void getDemandList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = null;
        ArrayList billGroupList = billGroupDAO.getDemandList();
        JSONArray json = new JSONArray(billGroupList);
        out = response.getWriter();
        out.write(json.toString());
    }

    @ResponseBody
    @RequestMapping(value = "getMajorHeadList")
    public void getMajorHeadList(HttpServletRequest request, HttpServletResponse response, @RequestParam("demandNo") String demandNo) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = null;
        ArrayList majorHeadList = billGroupDAO.getMajorHeadList(demandNo);
        JSONArray json = new JSONArray(majorHeadList);
        out = response.getWriter();
        out.write(json.toString());
    }

    @ResponseBody
    @RequestMapping(value = "getSubMajorHeadList")
    public void getSubMajorHeadList(HttpServletRequest request, HttpServletResponse response, @RequestParam("demandNo") String demandNo, @RequestParam("majorhead") String majorhead) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = null;
        ArrayList majorHeadList = billGroupDAO.getSubMajorHeadList(demandNo, majorhead);
        JSONArray json = new JSONArray(majorHeadList);
        out = response.getWriter();
        out.write(json.toString());
    }

    @ResponseBody
    @RequestMapping(value = "getMinorHeadList")
    public void getMinorHeadList(HttpServletRequest request, HttpServletResponse response, @RequestParam("majorhead") String majorhead, @RequestParam("submajorhead") String submajorhead) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = null;
        ArrayList majorHeadList = billGroupDAO.getMinorHeadList(majorhead, submajorhead);
        JSONArray json = new JSONArray(majorHeadList);
        out = response.getWriter();
        out.write(json.toString());
    }

    @ResponseBody
    @RequestMapping(value = "getSubMinorHeadList")
    public void getSubMinorHeadList(HttpServletRequest request, HttpServletResponse response, @RequestParam("minorHead") String minorHead, @RequestParam("submajorhead") String submajorhead) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = null;
        ArrayList majorHeadList = billGroupDAO.getSubMinorHeadList(submajorhead, minorHead);
        JSONArray json = new JSONArray(majorHeadList);
        out = response.getWriter();
        out.write(json.toString());
    }

    @ResponseBody
    @RequestMapping(value = "getDetailHeadList")
    public void getDetailHeadList(HttpServletRequest request, HttpServletResponse response, @RequestParam("minorhead") String minorhead, @RequestParam("subhead") String subhead) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = null;
        ArrayList majorHeadList = billGroupDAO.getDetailHeadList(minorhead, subhead);
        JSONArray json = new JSONArray(majorHeadList);
        out = response.getWriter();
        out.write(json.toString());
    }

    @ResponseBody
    @RequestMapping(value = "getChargedVotedList")
    public void getChargedVotedList(HttpServletRequest request, HttpServletResponse response, @RequestParam("subminorhead") String subminorhead, @RequestParam("detailhead") String detailhead) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = null;
        ArrayList majorHeadList = billGroupDAO.getChargedVotedList(detailhead, subminorhead);
        JSONArray json = new JSONArray(majorHeadList);
        out = response.getWriter();
        out.write(json.toString());
    }

    @ResponseBody
    @RequestMapping(value = "getPostClassList")
    public void getPostClassList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = null;
        ArrayList billPostClassList = billGroupDAO.getPostClassList();
        JSONArray json = new JSONArray(billPostClassList);
        out = response.getWriter();
        out.write(json.toString());
    }

    @ResponseBody
    @RequestMapping(value = "getBillTypeList")
    public void getBillTypeList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = null;
        ArrayList billTypeList = billGroupDAO.getBillTypeList();
        JSONArray json = new JSONArray(billTypeList);
        out = response.getWriter();
        out.write(json.toString());
    }

    @RequestMapping(value = "deleteGroupData")
    public ModelAndView deleteGroupData(ModelMap model, @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response, @RequestParam("groupId") String groupId) {
        String ocode = lub.getOffcode();
        billGroupDAO.deleteGroupData(ocode, groupId);
        return new ModelAndView("redirect:/billGroupAction.htm");

    }
    @RequestMapping(value = "editGroupList")
public ModelAndView editGroupList(ModelMap model, @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response, @RequestParam("groupId") String groupId) {//
	String path = "/payroll/EditGroupList";
        ModelAndView mav = new ModelAndView();
	 String ocode = lub.getOffcode();
	 ArrayList planStatusList = billGroupDAO.getPlanStatusList();
	ArrayList billSectorList = billGroupDAO.getSectorList();
	ArrayList billPostClassList = billGroupDAO.getPostClassList();
	ArrayList billTypeList = billGroupDAO.getBillTypeList();
	ArrayList billGroupList = billGroupDAO.getDemandList();
	mav.addObject("billGroupList", billGroupList);
	mav.addObject("billTypeList", billTypeList);
	mav.addObject("billPostClassList", billPostClassList);
	mav.addObject("billSectorList", billSectorList);
	mav.addObject("planStatusList", planStatusList);
       BillGroup billGroup = new BillGroup();
	billGroup=billGroupDAO.editGroupList(ocode, groupId);
        mav.addObject("billGroup", billGroup);
	//ModelAndView mav = new ModelAndView();
	mav.setViewName(path);
	return mav;
}
@RequestMapping(value = "updateGroupSection", method = RequestMethod.POST)
public ModelAndView updateGroupSection(ModelMap model, @ModelAttribute("LoginUserBean") LoginUserBean lub, @ModelAttribute("GroupSection") BillGroup bg, BindingResult result, HttpServletResponse response) {//
	billGroupDAO.updateGroupSection(lub.getOffcode(), bg);
	return new ModelAndView("redirect:/billGroupAction.htm");
}


}
