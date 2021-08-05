/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.controller.absorption;

import hrms.dao.absorption.AbsorptionDAO;
import hrms.model.absorption.AbsorptionModel;
import hrms.model.login.LoginUserBean;
import hrms.model.login.Users;
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
public class AbsorptionController {
    @Autowired
    public AbsorptionDAO absorptionDAO;
     @RequestMapping(value = "Absorption")
    public String AbsorptionList(@ModelAttribute("SelectedEmpObj") Users lub) {
        return "/absorption/Absorption";
        
    }
    
     @RequestMapping(value = "GetAbsorptionListJSON")
    public void GetAbsorptionListJSON(HttpServletResponse response, @ModelAttribute("SelectedEmpObj") Users selectedEmpObj, @RequestParam("page") int page) throws IOException {

        response.setContentType("application/json");
        JSONObject json = new JSONObject();
        PrintWriter out = null;
        List absorptionlist = null;
        int additionalchargelistCnt = 0;
        int maxlimit = 10;
        int minlimit = maxlimit * (page - 1);
        try {
            absorptionlist = absorptionDAO.findAllAbsorption(selectedEmpObj.getEmpId());
            json.put("total", absorptionlist);
            out = response.getWriter();
            out.write(json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
        }
    }
    
    @RequestMapping(value = "saveAbsorptionData")
    public void saveAbsorptionData(HttpServletResponse response, @ModelAttribute("LoginUserBean") LoginUserBean lub1, @ModelAttribute("SelectedEmpObj") Users selectedEmpObj, @ModelAttribute("absorptionForm") AbsorptionModel absorptionForm) throws ParseException {

        response.setContentType("application/json");
        PrintWriter out = null;
        int Absorptiondata = 0;

        try {
            absorptionForm.setEmpid(selectedEmpObj.getEmpId());
            Absorptiondata = absorptionDAO.insertAbsorptionData(absorptionForm);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
     @RequestMapping(value = "editAbsorption")
    public void editAbsorption(HttpServletResponse response, @ModelAttribute("SelectedEmpObj") Users selectedEmpObj, @RequestParam Map<String, String> requestParams) throws IOException {
        AbsorptionModel absorptionform = null;
        try {
            String empid = selectedEmpObj.getEmpId();
            String absorptionformId = requestParams.get("notId");
            absorptionform = absorptionDAO.editAbsorptionData(absorptionformId);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
      @RequestMapping(value = "deleteAbsorption")
    public String deleteAbsorption(@RequestParam("notId") String notId) {
        int deletestatus = 0;
        deletestatus = absorptionDAO.deleteAbsorptionData(notId);
        // System.out.println("loan press save page"+lub.getEmpid());
         return "/absorption/Absorption";
    }
    
    @RequestMapping(value = "updateAbsorptionData")
    public void updateRecruitmentData(HttpServletResponse response, @ModelAttribute("LoginUserBean") LoginUserBean lub1, @ModelAttribute("SelectedEmpObj") Users selectedEmpObj, @ModelAttribute("absorptionForm") AbsorptionModel absorptionForm) throws ParseException {
        int recruitmentFormdata = 0;
        try {
            absorptionForm.setEmpid(selectedEmpObj.getEmpId());
            recruitmentFormdata = absorptionDAO.updateAbsorptionData(absorptionForm);
           // System.out.println("Hi");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
