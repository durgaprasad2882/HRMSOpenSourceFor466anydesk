/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hrms.controller.payroll.schedule;

import hrms.dao.payroll.schedule.ScheduleDAO;
import hrms.model.payroll.schedule.OtcFormBean;
import javax.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class OTCForm52Controller implements ServletContextAware{
    
    @Autowired
    public ScheduleDAO comonScheduleDao;
    
    private ServletContext context;

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.context = servletContext;
    }
    
    @RequestMapping(value = "OTCForm52HTML",method = {RequestMethod.POST,RequestMethod.GET})
    public ModelAndView OTCForm52HTML(@RequestParam("billNo") String billNo){
    
        ModelAndView mav = null;
        OtcFormBean otcBean = new OtcFormBean();
        try{
            mav = new ModelAndView();
            otcBean = comonScheduleDao.getOTCForm52ScheduleDetails(billNo);
            
            mav.addObject("OTC52Header",otcBean);
            mav.setViewName("/payroll/schedule/OTC52");
            
        }catch(Exception e){
            e.printStackTrace();
        } 
        return mav;  
    }
    
}
