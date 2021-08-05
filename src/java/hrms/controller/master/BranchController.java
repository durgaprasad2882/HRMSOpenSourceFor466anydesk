/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.controller.master;

import hrms.dao.master.BranchDAO;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author lenovo pc
 */
@Controller
public class BranchController {
    @Autowired
    BranchDAO branchDao;
    @ResponseBody
    @RequestMapping(value = "getBranchListJSON.htm", method = {RequestMethod.GET,RequestMethod.POST})
    public void getBranchListJSON(HttpServletRequest request, HttpServletResponse response, @RequestParam("bankCode") String bankCode) {
        response.setContentType("application/json");
        PrintWriter out = null;
        JSONArray json = null;
        
        try {
            System.out.println("***********"+bankCode);
            List branchlist = branchDao.getBranchList(bankCode);
            json = new JSONArray(branchlist);
            out = response.getWriter();
            out.write(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
        }
    }
}
