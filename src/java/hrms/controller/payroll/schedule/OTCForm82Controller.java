/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hrms.controller.payroll.schedule;

import hrms.dao.payroll.schedule.ScheduleDAO;
import hrms.model.payroll.schedule.OtcForm82Bean;
import javax.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class OTCForm82Controller implements ServletContextAware{
    
    @Autowired
    public ScheduleDAO comonScheduleDao;
    
    private ServletContext context;

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.context = servletContext;
    }
    
    @RequestMapping(value = "OTCForm82HTML",method = {RequestMethod.POST,RequestMethod.GET})
    public ModelAndView OTCForm82HTMLController(@RequestParam("billNo") String billNo){

        ModelAndView mav = null;
        OtcForm82Bean otcBean = new OtcForm82Bean();
        try{
            mav = new ModelAndView();
            System.out.println("Inside the OTCForm82HTMLController ----------------------");
            otcBean = comonScheduleDao.getOTCForm82ScheduleDetails(billNo);
            
            mav.addObject("Otc82Header", otcBean);
            mav.setViewName("/payroll/schedule/OTC82");
            
        }catch(Exception e){
            e.printStackTrace();
        } 
        return mav;  
    }
}
