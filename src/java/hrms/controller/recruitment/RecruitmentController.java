/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.controller.recruitment;

import hrms.dao.recruitment.RecruitmentDAO;
import hrms.model.login.LoginUserBean;
import hrms.model.login.Users;
import hrms.model.recruitment.Recruitment;
import hrms.model.recruitment.RecruitmentModel;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 *
 * @author lenovo
 */
@Controller
@SessionAttributes({"Users", "SelectedEmpObj"})
public class RecruitmentController {
    @Autowired
    public RecruitmentDAO recruitmentDAO;

    @RequestMapping(value = "Recruitment")
    public String RecruitmentList(@ModelAttribute("SelectedEmpObj") Users lub) {
        return "/recruitment/recruitment";
    }
    
     @RequestMapping(value = "GetRecruitmentListJSON")
    public void GetRecruitmentListJSON(HttpServletResponse response, @ModelAttribute("SelectedEmpObj") Users selectedEmpObj, @RequestParam("page") int page) {

        response.setContentType("application/json");
        JSONObject json = new JSONObject();
        PrintWriter out = null;
        List recruitmentlist = null;
        int additionalchargelistCnt = 0;
        int maxlimit = 10;
        int minlimit = maxlimit * (page - 1);
        try {
            recruitmentlist = recruitmentDAO.findAllRecruitment(selectedEmpObj.getEmpId());
            json.put("total", recruitmentlist);
            out = response.getWriter();
            out.write(json.toString());
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
        }
    }
    
    @RequestMapping(value = "saveRecruitmentData")
    public void saveRecruitmentData(HttpServletResponse response, @ModelAttribute("LoginUserBean") LoginUserBean lub1, @ModelAttribute("SelectedEmpObj") Users selectedEmpObj, @ModelAttribute("recruitmentForm") RecruitmentModel recruitmentForm) throws ParseException {

        response.setContentType("application/json");
        PrintWriter out = null;
        int Recruitmentdata = 0;

        try {
            recruitmentForm.setEmpid(selectedEmpObj.getEmpId());
            Recruitmentdata = recruitmentDAO.insertRecruitmentData(recruitmentForm);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
     @RequestMapping(value = "editRecruitment")
    public void editRecruitment(HttpServletResponse response, @ModelAttribute("SelectedEmpObj") Users selectedEmpObj, @RequestParam Map<String, String> requestParams) throws IOException {
        RecruitmentModel recruitmentform = null;
        try {
            String empid = selectedEmpObj.getEmpId();
            String recruitmentId = requestParams.get("notId");
            recruitmentform = recruitmentDAO.editRecruitment(recruitmentId);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
     @RequestMapping(value = "deleteRecruitment")
    public String deleteRecruitment(@RequestParam("notId") String notId) {
        int deletestatus = 0;
        deletestatus = recruitmentDAO.deleteRecruitment(notId);
        // System.out.println("loan press save page"+lub.getEmpid());
        return "/recruitment/recruitment";
    }
    
    @RequestMapping(value = "updateRecruitmentData")
    public void updateRecruitmentData(HttpServletResponse response, @ModelAttribute("LoginUserBean") LoginUserBean lub1, @ModelAttribute("SelectedEmpObj") Users selectedEmpObj, @ModelAttribute("recruitmentForm") RecruitmentModel recruitmentForm) throws ParseException {
        int recruitmentFormdata = 0;
        try {
            recruitmentForm.setEmpid(selectedEmpObj.getEmpId());
            recruitmentFormdata = recruitmentDAO.updateRecruitmentData(recruitmentForm);
           // System.out.println("Hi");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
