/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.controller.additionalCharge;

import hrms.dao.additionalCharge.AdditionalChargeDAO;
import hrms.model.additionalCharge.AdditionalCharge;
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
public class AdditionalChargeController {

    @Autowired
    public AdditionalChargeDAO additionalChargeDAO;

    @RequestMapping(value = "AdditionalCharge")
    public String AdditionalChargeList(@ModelAttribute("SelectedEmpObj") Users lub) {
        return "/additionalCharge/AdditionalCharge";
    }

    @RequestMapping(value = "GetAdditionalChargeListJSON")
    public void GetAdditionalChargeListJSON(HttpServletResponse response, @ModelAttribute("SelectedEmpObj") Users selectedEmpObj, @RequestParam("page") int page) {

        response.setContentType("application/json");
        JSONObject json = new JSONObject();
        PrintWriter out = null;
        List additionalchargelist = null;
        int additionalchargelistCnt = 0;
        int maxlimit = 10;
        int minlimit = maxlimit * (page - 1);
        try {
            additionalchargelist = additionalChargeDAO.findAllAdditionalCharge(selectedEmpObj.getEmpId());
            json.put("total", additionalchargelist);
            out = response.getWriter();
            out.write(json.toString());
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
        }
    }

    @RequestMapping(value = "saveAdditionalChargeData")
    public void saveAdditionalChargeData(HttpServletResponse response, @ModelAttribute("LoginUserBean") LoginUserBean lub1, @ModelAttribute("SelectedEmpObj") Users selectedEmpObj, @ModelAttribute("additionChargeForm") AdditionalCharge additionChargeForm) throws ParseException {

        response.setContentType("application/json");
        PrintWriter out = null;
        int additionalcharge = 0;

        try {
            additionChargeForm.setEmpid(selectedEmpObj.getEmpId());
            additionalcharge = additionalChargeDAO.insertAdditionalChargeData(additionChargeForm);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "editadditionalCharge")
    public void editadditionalCharge(HttpServletResponse response, @ModelAttribute("SelectedEmpObj") Users selectedEmpObj, @RequestParam Map<String, String> requestParams) throws IOException {
        AdditionalCharge additionalform = null;
        try {
            String empid = selectedEmpObj.getEmpId();
            String addchargeIdId = requestParams.get("notId");
            additionalform = additionalChargeDAO.editAdditionalCharge(addchargeIdId);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @RequestMapping(value = "deleteaditionalCharge")
    public String deleteAditionalCharge(@RequestParam("notId") String notId) {
        int deletestatus = 0;
        deletestatus = additionalChargeDAO.deleteAdditionalCharge(notId);
        // System.out.println("loan press save page"+lub.getEmpid());
        return "/additionalCharge/AdditionalCharge";
    }

    @RequestMapping(value = "updateAdditionalChargeData")
    public void updateAdditionalChargeData(HttpServletResponse response, @ModelAttribute("LoginUserBean") LoginUserBean lub1, @ModelAttribute("SelectedEmpObj") Users selectedEmpObj, @ModelAttribute("additionChargeForm") AdditionalCharge additionChargeForm) throws ParseException {
        int additionalcharge = 0;
        try {
            additionChargeForm.setEmpid(selectedEmpObj.getEmpId());
            additionalcharge = additionalChargeDAO.updateAdditionalChargeData(additionChargeForm);
           // System.out.println("Hi");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
