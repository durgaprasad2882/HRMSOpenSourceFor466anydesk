/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.controller.tab;

import hrms.common.CommonFunctions;
import hrms.dao.login.LoginDAOImpl;
import hrms.dao.tab.TabDAOImpl;
import hrms.model.login.LoginUserBean;
import hrms.model.login.UserExpertise;
import hrms.model.login.Users;
import hrms.model.tab.OfficeTreeAttr;
import hrms.model.tab.TabForm;
import java.io.PrintWriter;
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
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Surendra
 */
@Controller
@SessionAttributes("LoginUserBean")
public class TabController {

    @Autowired
    public TabDAOImpl tabDAO;

    @Autowired
    public LoginDAOImpl loginDao;

    @RequestMapping(value = "tabController", method = RequestMethod.GET)
    public ModelAndView getTabPage(@ModelAttribute("LoginUserBean") LoginUserBean lub, @ModelAttribute("tform") TabForm tform,
            BindingResult result, @RequestParam("rollId") String rollId) {
        ModelAndView mav = new ModelAndView();

        String path = "index";
        try {
            String rollName = CommonFunctions.decodedTxt(rollId);
            String empId = lub.getEmpid();
            tform.setRollId(rollId);
            if (rollName.equals("01")) {
                path = "mycadre";
            } else if (rollName.equals("02")) {
                path = "mydepartment";
            } else if (rollName.equals("03")) {
                path = "mydistrict";
            } else if (rollName.equals("04")) {
                path = "tab/MyHod";
            } else if (rollName.equals("05")) {
                path = "tab/MyOffice";
                
            } else if (rollName.equals("10")) {
                path = "paradmin";
            }
            

            tform.setEmployeeId(empId);
            tform.setOffCode(lub.getOffcode());
            mav.addObject("tform", tform);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

        mav.setViewName(path);
        return mav;
    }

    @RequestMapping(value = "myPage", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView myPageTab(@ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, ModelMap model, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView();
        String path = "index";

        String empid = "";

        try {

            empid = lub.getEmpid();
            if (empid != null && !empid.equals("")) {

                Users emp = loginDao.getEmployeeProfileInfo(empid);

                mav.addObject("users", emp);
                path = "/tab/mypage";
            } else {

                model.addAttribute("errorMsg", "Invalid User or Password");
                path = "index";
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        mav.setViewName(path);
        return mav;
    }

    @ResponseBody
    @RequestMapping(value = "employeeTree")
    public void getEmployeeTree(@ModelAttribute("LoginUserBean") LoginUserBean lub, HttpServletRequest request, HttpServletResponse response, @RequestParam("empid") String empId, @RequestParam("id") String id) {

        PrintWriter out = null;

        try {
            out = response.getWriter();
            List li = tabDAO.getOfficeListXML(empId);

            JSONArray json = new JSONArray(li);
            out = response.getWriter();
            out.write(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.close();
            out.flush();

        }
    }

    @ResponseBody
    @RequestMapping(value = "saveExpertise", method = {RequestMethod.GET, RequestMethod.POST})
    public String myPageTab(@ModelAttribute("LoginUserBean") LoginUserBean lub, @ModelAttribute("frmExpertise") UserExpertise uExpertise, HttpServletRequest request) {
        uExpertise.setEmpid(lub.getEmpid());
        String result = tabDAO.saveExpertise(uExpertise);

        return result;
    }
    @ResponseBody
    @RequestMapping(value = "hodTree")
    public void getHodTree(@ModelAttribute("LoginUserBean") LoginUserBean lub, HttpServletRequest request, HttpServletResponse response) {
        PrintWriter out = null;
        try {
            out = response.getWriter();
            OfficeTreeAttr ofattr = tabDAO.getHODOfficeListXML(lub.getOffcode());            
            JSONObject jsonObj = new JSONObject(ofattr);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(jsonObj);
            out = response.getWriter();
            out.write(jsonArray.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.close();
            out.flush();

        }
    }

}
