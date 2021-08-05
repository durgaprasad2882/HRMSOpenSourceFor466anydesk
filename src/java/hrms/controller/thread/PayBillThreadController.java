/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hrms.controller.thread;

import hrms.model.login.LoginUserBean;
import hrms.thread.paybill.CallableRegularPayBill;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Manas
 */
@Controller
public class PayBillThreadController {
    @Autowired
    public CallableRegularPayBill callableRegularPayBill;
    
    @RequestMapping(value = "runPayBillThread", method = RequestMethod.GET)
    public ModelAndView runPayBillThread(HttpServletRequest request, @ModelAttribute("LoginUserBean") LoginUserBean lub, HttpServletResponse response)throws IOException{        
        System.out.println("callableRegularPayBill.getThreadStatus() :"+callableRegularPayBill.getThreadStatus() );
        if(callableRegularPayBill.getThreadStatus() == 0){
            System.out.println("Trigger");
            Thread t = new Thread(callableRegularPayBill);
            t.start();
        }
        ModelAndView mv = new ModelAndView("redirect:/getThreadServiceList.htm");                
        return mv;
    }
    @ResponseBody
    @RequestMapping(value = "runPayBillThreadStatus")
    public void getThreadStatus(HttpServletRequest request, @ModelAttribute("LoginUserBean") LoginUserBean lub, HttpServletResponse response)throws Exception{
        response.setContentType("application/json");
        PrintWriter out = null;
        out = response.getWriter();
        JSONObject job = new JSONObject();
        job.append("ThreadStatus", callableRegularPayBill.threadStatus);
        out.write(job.toString());   
        out.close();
    }
}
