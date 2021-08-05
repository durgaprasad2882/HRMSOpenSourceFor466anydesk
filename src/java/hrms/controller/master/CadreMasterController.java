package hrms.controller.master;

import hrms.dao.master.CadreDAO;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CadreMasterController {
    
    @Autowired
    CadreDAO cadreDAO;
    
    @ResponseBody
    @RequestMapping(value = "getCadreListJSON")
    public void getcadrelist(HttpServletRequest request, HttpServletResponse response,@RequestParam("deptcode") String deptcode) {
        response.setContentType("application/json");
        PrintWriter out = null;
        JSONArray json = null;
        try{
            List cadrelist = cadreDAO.getCadreList(deptcode);
            json = new JSONArray(cadrelist);
            out = response.getWriter();
            out.write(json.toString());
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            out.flush();
            out.close();
        }       
    }
    
}
