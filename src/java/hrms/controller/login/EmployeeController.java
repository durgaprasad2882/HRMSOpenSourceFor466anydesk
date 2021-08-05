/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.controller.login;

import hrms.dao.employee.EmployeeDAO;
import hrms.dao.master.DepartmentDAO;
import hrms.dao.master.OfficeDAO;
import hrms.model.empinfo.EmployeeMessage;
import hrms.model.empinfo.SearchEmployee;
import hrms.model.employee.EmployeePayProfile;
import hrms.model.login.LoginUserBean;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Manas
 */
@Controller
@SessionAttributes("LoginUserBean")
public class EmployeeController {

    @Autowired
    EmployeeDAO employeeDAO;

    @Autowired
    DepartmentDAO departmentDao;

    @Autowired
    OfficeDAO officeDao;

    @ResponseBody
    @RequestMapping(value = "getEmployeePayProfile", method = RequestMethod.GET)
    public void getEmployeeWiseADList(HttpServletRequest request, @ModelAttribute("LoginUserBean") LoginUserBean lub, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = null;
        EmployeePayProfile empPayProfile = employeeDAO.getEmployeePayProfile(lub.getEmpid());
        JSONObject jsonObject = new JSONObject(empPayProfile);
        out = response.getWriter();
        out.write(jsonObject.toString());
    }

    @RequestMapping(value = "SearchEmployeeInfo")
    public ModelAndView SearchEmployeeInfo(@Valid @ModelAttribute("searchEmployee") SearchEmployee empinfo, ModelMap model) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("empinfo/SearchEmployee");
        mv.addObject("searchEmployee", empinfo);
        mv.addObject("departmentList", departmentDao.getDepartmentList());
        mv.addObject("officeList", officeDao.getTotalOfficeList(empinfo.getDeptName()));
        mv.addObject("employeeList", employeeDAO.SearchEmployee(empinfo));

        return mv;
    }

    @RequestMapping(value = "EmployeeBasicProfile")
    public ModelAndView EmployeeBasicProfile(@RequestParam("empid") String empid) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("empinfo/EmployeeBasicProfile");
        mv.addObject("EmployeeProfile", employeeDAO.getEmployeeProfile(empid));
        return mv;
    }

    @RequestMapping(value = "newMessage")
    public ModelAndView newMessage(@ModelAttribute("employeeMessage") EmployeeMessage employeeMessage) {
        ModelAndView mv = new ModelAndView();
        mv.addObject("employeeMessage", employeeMessage);
        mv.setViewName("empinfo/messageDetail");

        return mv;
    }

    /* 
     */
    @RequestMapping(value = "saveEmployeeMessage")
    public ModelAndView saveEmployeeMessage(@ModelAttribute("employeeMessage") EmployeeMessage employeeMessage) throws SQLException {
        ModelAndView mv = new ModelAndView();

        int messageId = employeeDAO.saveEmployeeMessage(employeeMessage);
        if (!employeeMessage.getUploadedFile().isEmpty()) {
            employeeDAO.uploadAttachedFile(messageId, employeeMessage.getUploadedFile());
        }

        return mv;
    }

    @RequestMapping(value = "getSentMessageList")
    public ModelAndView SentMessage() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("empinfo/SentMessage");
        mv.addObject("employeemessagelist", employeeDAO.getSentMessageList());
        return mv;
    }

}
