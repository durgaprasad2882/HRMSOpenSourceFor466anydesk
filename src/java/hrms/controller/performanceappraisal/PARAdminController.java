/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.controller.performanceappraisal;

import hrms.dao.performanceappraisal.PARAdminDAO;
import hrms.model.login.LoginUserBean;
import hrms.model.parmast.PARSearchResult;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author DurgaPrasad
 */
@Controller
@SessionAttributes("LoginUserBean")
public class PARAdminController {

    @Autowired
    PARAdminDAO parAdminDAO;

    @RequestMapping(value = "viewPARAdmin.htm", method = RequestMethod.GET)
    public ModelAndView viewPARAdmin(ModelMap model, HttpServletRequest request, @ModelAttribute("LoginUserBean") LoginUserBean lub) {
        ModelAndView mv = new ModelAndView("/par/ParAdmin");
        return mv;
    }

    @RequestMapping(value = "getSearchPARList.htm", method = RequestMethod.POST)
    public void getSearchPARList(ModelMap model, HttpServletRequest request, @ModelAttribute("LoginUserBean") LoginUserBean lub, HttpServletResponse response, @RequestParam("fiscalyear") String fiscalyear, @RequestParam("page") int page, @RequestParam("rows") int rows) throws IOException,JSONException {
        response.setContentType("application/json");
        PrintWriter out = null;
        PARSearchResult propertyStatementList = parAdminDAO.getPARList(fiscalyear, rows, page);
        JSONObject obj = new JSONObject();
        obj.put("total", propertyStatementList.getTotalPARFound());
        obj.put("rows", propertyStatementList.getParlist());
        out = response.getWriter();
        out.write(obj.toString());
    }
}
