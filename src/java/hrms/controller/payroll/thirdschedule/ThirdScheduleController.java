package hrms.controller.payroll.thirdschedule;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import hrms.common.Message;
import hrms.dao.master.DepartmentDAO;
import hrms.dao.master.PostDAO;
import hrms.dao.payroll.thirdschedule.ThirdScheduleDAO;
import hrms.model.login.LoginUserBean;
import hrms.model.payroll.thirdschedule.ThirdScheduleBean;
import hrms.model.payroll.thirdschedule.ThirdScheduleForm;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

@Controller
@SessionAttributes("LoginUserBean")
public class ThirdScheduleController {

    @Autowired
    DepartmentDAO departmentDao;

    @Autowired
    public PostDAO postDAO;

    @Autowired
    public ThirdScheduleDAO thirdSchedleDAO;

    @RequestMapping(value = "ThirdScheduleEmpList")
    public String thirdScheduleEmpList() {

        return "/payroll/thirdschedule/ThirdShceduleList";

    }

    @RequestMapping(value = "getThirdScheduleEmpListJSON")
    public void getThirdScheduleEmpListJSON(HttpServletResponse response, @ModelAttribute("LoginUserBean") LoginUserBean lub, @RequestParam("rows") int rows, @RequestParam("page") int page) {

        response.setContentType("application/json");

        JSONObject json = new JSONObject();
        PrintWriter out = null;

        int total = 0;

        try {
            List empList = thirdSchedleDAO.getEmployeeList(lub.getEmpid(), rows, page);
            total = thirdSchedleDAO.getEmployeeListCount(lub.getEmpid());

            json.put("rows", empList);
            json.put("total", total);

            out = response.getWriter();
            out.write(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "viewThirdSchedule")
    public String ViewThirdSchedule(Model model, @RequestParam("empid") String empid) {

        ThirdScheduleForm tform = null;
        String path = "";
        
        List postlist2nd = null;
        try {
            tform = thirdSchedleDAO.getThirdScheduleData(empid);
            
            if(tform != null && tform.getIsIASCadre().equals("Y")){
                path = "/payroll/thirdschedule/ThirdScheduleViewIAS";
                postlist2nd = postDAO.getPostList(tform.getDeptCodeIAS());
            }else{
                path = "/payroll/thirdschedule/ThirdScheduleView";
                postlist2nd = postDAO.getPostList(tform.getDeptCode());
            }
            
            List deptlist = departmentDao.getDepartmentList();
            List postlist6th = postDAO.getPostList(tform.getEntryDeptCode());

            model.addAttribute("deptlist", deptlist);
            model.addAttribute("PostList2nd", postlist2nd);
            model.addAttribute("PostList6th", postlist6th);
            model.addAttribute("tform", tform);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    @RequestMapping(value = "ThirdschedulePDF")
    public void thirdschedulePDF(HttpServletResponse response, @RequestParam("empid") String empid) {

        response.setContentType("application/pdf");
        Document document = new Document(PageSize.A4);
        
        ThirdScheduleForm tform = null;
        PdfWriter writer = null;
        try {
            String offName = thirdSchedleDAO.getEmployeeOfficeName(empid);
            
            response.setHeader("Content-Disposition", "attachment; filename=Third_Schedule_" + empid + ".pdf");
            
            writer = PdfWriter.getInstance(document, response.getOutputStream());
            writer.setPageEvent(new ThirdScheduleHeaderFooter(offName,empid));
            
            document.open();
            
            tform = thirdSchedleDAO.getThirdScheduleData(empid);
            
            if(tform != null && tform.getIsIASCadre().equals("Y")){
                thirdSchedleDAO.thirdScheduleIASPDF(document, tform);
            }else{
                thirdSchedleDAO.thirdSchedulePDF(document, empid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
    }

    @ResponseBody
    @RequestMapping(value = "saveThirdScheduleData")
    public void SaveThirdScheduleData(HttpServletResponse response, @ModelAttribute("thirdScheduleForm") ThirdScheduleForm tform, @RequestParam("approve") String approve) {

        response.setContentType("application/json");
        PrintWriter out = null;

        Message msg = null;

        try {
            msg = thirdSchedleDAO.saveThirdScheduleData(tform, approve);

            JSONObject job = new JSONObject(msg);
            out = response.getWriter();
            out.write(job.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "saveCheckingAuthData")
    public void SaveCheckingAuthData(HttpServletResponse response, @RequestParam Map<String, String> requestParams) {

        response.setContentType("application/json");
        PrintWriter out = null;

        Message msg = null;

        try {
            String chkEmp = (String) requestParams.get("chkEmp");
            String authEmp = (String) requestParams.get("authEmp");

            System.out.println("chkAuth is: " + chkEmp);
            System.out.println("authEmp is: " + authEmp);

            msg = thirdSchedleDAO.saveCheckingAuthData(chkEmp, authEmp);

            JSONObject job = new JSONObject(msg);
            out = response.getWriter();
            out.write(job.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @RequestMapping(value = "revertPayFixationEmp")
    public void RevertPayFixationEmp(HttpServletResponse response, @RequestParam Map<String, String> requestParams) {

        response.setContentType("application/json");
        PrintWriter out = null;

        Message msg = null;

        try {
            String chkEmp = (String) requestParams.get("chkEmp");

            System.out.println("chkAuth is: " + chkEmp);

            msg = thirdSchedleDAO.revertPayFixationAuthData(chkEmp);

            JSONObject job = new JSONObject(msg);
            out = response.getWriter();
            out.write(job.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
