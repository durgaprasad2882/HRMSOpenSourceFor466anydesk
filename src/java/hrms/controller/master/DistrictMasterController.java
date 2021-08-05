/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.controller.master;

import hrms.dao.master.DistrictDAO;
import hrms.dao.master.PostDAO;
import hrms.model.login.LoginUserBean;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 *
 * @author Manas Jena
 */
@Controller
@SessionAttributes("LoginUserBean")
public class DistrictMasterController {
    @Autowired
    DistrictDAO districtDAO;
    
    
    
    @ResponseBody
    @RequestMapping(value = "getDistrictList", method = RequestMethod.POST)
    public void getDistrictList(HttpServletRequest request, @ModelAttribute("LoginUserBean") LoginUserBean lub, HttpServletResponse response) throws IOException{
        response.setContentType("application/json");
        PrintWriter out = null;        
        ArrayList districtList = districtDAO.getDistrictList();
        JSONArray json = new JSONArray(districtList);
        out = response.getWriter();
        out.write(json.toString());
    }
    
    
}
