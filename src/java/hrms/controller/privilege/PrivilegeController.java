/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.controller.privilege;

import hrms.dao.master.DepartmentDAO;
import hrms.dao.master.OfficeDAO;
import hrms.dao.privilege.PrivilegeManagementDAO;
import hrms.model.login.LoginUserBean;
import hrms.model.master.Module;
import hrms.model.privilege.Privilege;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static org.joda.time.DateTimeFieldType.year;
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
 * @author Surendra
 */
@Controller
@SessionAttributes("LoginUserBean")
public class PrivilegeController {

    @Autowired
    public PrivilegeManagementDAO privilegeManagementDAO;

    @Autowired
    public DepartmentDAO departmentDao;

    @Autowired
    public OfficeDAO offDAO;

    @RequestMapping(value = "displayAssignPrivilegepage", method = {RequestMethod.GET, RequestMethod.POST})
    public String DisplayPage(ModelMap model, @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response) {
        String path = "/privilege/AssignPrivilege";
        model.addAttribute("deptList", departmentDao.getDepartmentList());
        return path;
    }

    @ResponseBody
    @RequestMapping(value = "getAssignedPrivilege", method = {RequestMethod.GET, RequestMethod.POST})
    public void getAssignedPrivilege(HttpServletRequest request, HttpServletResponse response, @RequestParam("spc") String spc) {
        response.setContentType("application/json");
        PrintWriter out = null;
        try {
            List li = privilegeManagementDAO.getAssignedPrivilageList(spc);
            JSONArray json = new JSONArray(li);
            out = response.getWriter();
            out.write(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            out.flush();
            out.close();
        }
    }
    
    @ResponseBody
    @RequestMapping(value = "getRoleList", method = {RequestMethod.GET, RequestMethod.POST})
    public void getRoleList(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json");
        PrintWriter out = null;
        try {
            List li = privilegeManagementDAO.getRoleList();
            JSONArray json = new JSONArray(li);
            out = response.getWriter();
            out.write(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            out.flush();
            out.close();
        }
    }

    @ResponseBody
    @RequestMapping(value = "getModuleGroupList", method = {RequestMethod.GET, RequestMethod.POST})
    public void getModuleGroupList(HttpServletRequest request, HttpServletResponse response, @RequestParam("role") String role) {
        response.setContentType("application/json");
        PrintWriter out = null;
        try {
            List li = privilegeManagementDAO.getModuleGroupList(role);
            JSONArray json = new JSONArray(li);
            out = response.getWriter();
            out.write(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            out.flush();
            out.close();
        }
    }
    @ResponseBody
    @RequestMapping(value = "revokePrivilage", method = {RequestMethod.GET, RequestMethod.POST})
    public void revokePrivilage(ModelMap model, @RequestParam("privmapid") int privmapid, @RequestParam("spc") String spc,@ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response) {
        PrintWriter out = null;
        try {
            String status = privilegeManagementDAO.revokePrivilege(spc,privmapid);
            out = response.getWriter();
            out.write(status);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            out.flush();
            out.close();
        }
    }

    @ResponseBody
    @RequestMapping(value = "assignPrivilege", method = {RequestMethod.GET, RequestMethod.POST})  
    public void assignPrivilege(@RequestParam("roleid") String roleid,@RequestParam("modgrpid") String modgrpid,@RequestParam("modid") int modid, @RequestParam("spc") String spc, HttpServletRequest request, HttpServletResponse response) {        
        PrintWriter out = null;
        try {
            Module module = new Module();
            module.setRoleid(roleid);
            module.setModgrpid(modgrpid);
            module.setModid(modid);
            String status = privilegeManagementDAO.assignPrivilege(module, spc);
            out = response.getWriter();
            out.write(status);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            out.flush();
            out.close();
        }
    }
    @RequestMapping(value = "displayAssignPrivilegepageUserNameSpecific", method = {RequestMethod.GET, RequestMethod.POST})
    public String DisplayPageUsernameSpecific(ModelMap model, @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response) {
        String path = "/privilege/AssignPrivilegeUserNameSpecific";
        model.addAttribute("userList", privilegeManagementDAO.getUserList("A"));
        return path;
    }

    @ResponseBody
    @RequestMapping(value = "getAssignedPrivilegeUserNameSpecific", method = {RequestMethod.GET, RequestMethod.POST})
    public void getAssignedPrivilegeUserNameSpecific(HttpServletRequest request, HttpServletResponse response, @RequestParam("username") String userName) {
        response.setContentType("application/json");
        PrintWriter out = null;
        try {
            List li = privilegeManagementDAO.getAssignedPrivilageUserNameSpecificList(userName);
            JSONArray json = new JSONArray(li);
            out = response.getWriter();
            out.write(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            out.flush();
            out.close();
        }
    }
    
    @ResponseBody
    @RequestMapping(value = "getModuleListUserNameSpecific", method = {RequestMethod.GET, RequestMethod.POST})
    public void getModuleListUserNameSpecific(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json");
        PrintWriter out = null;
        try {
            List li = privilegeManagementDAO.getModuleListUserNameSpecific("U");
            JSONArray json = new JSONArray(li);
            out = response.getWriter();
            out.write(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            out.flush();
            out.close();
        }
    }
    
    
    @ResponseBody
    @RequestMapping(value = "assignPrivilegeUserNameSpecific", method = {RequestMethod.GET, RequestMethod.POST})  
    public void assignPrivilegeUserNameSpecific(@RequestParam("username") String username,@RequestParam("modid") int modid,HttpServletRequest request, HttpServletResponse response) {        
        PrintWriter out = null;
        try {
            Module module = new Module();
            module.setModid(modid);
            String status = privilegeManagementDAO.assignPrivilegeUserNameSpecific(module, username);
            out = response.getWriter();
            out.write(status);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            out.flush();
            out.close();
        }
    }
    
    @ResponseBody
    @RequestMapping(value = "revokePrivilageUserNameSpecific", method = {RequestMethod.GET, RequestMethod.POST})
    public void revokePrivilageUserNameSpecific(ModelMap model, @RequestParam("privmapid") int privmapid, @RequestParam("username") String username,@ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response) {
        PrintWriter out = null;
        try {
            String status = privilegeManagementDAO.revokePrivilageUserNameSpecific(username,privmapid);
            out = response.getWriter();
            out.write(status);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            out.flush();
            out.close();
        }
    }
    
     

}
