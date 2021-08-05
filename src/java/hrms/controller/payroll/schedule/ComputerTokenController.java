/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hrms.controller.payroll.schedule;

import hrms.dao.payroll.schedule.PayBillDMPDAO;
import hrms.dao.payroll.schedule.ScheduleDAO;
import hrms.model.common.CommonReportParamBean;
import hrms.model.payroll.schedule.ComputerTokenReportBean;
import javax.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ComputerTokenController implements ServletContextAware{
    
    @Autowired
    public ScheduleDAO comonScheduleDao;
    
    @Autowired
    public PayBillDMPDAO paybillDmpDao;
    
    private ServletContext context;

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.context = servletContext;
    }
    
    @RequestMapping(value = "ComputerTokenHTML",method = {RequestMethod.POST,RequestMethod.GET})
    public ModelAndView ComputerTokenHTML(@RequestParam("billNo") String billNo){
    
        ModelAndView mav = null;
        ComputerTokenReportBean tokenBean = new ComputerTokenReportBean();
        try{
            mav = new ModelAndView();
            CommonReportParamBean crb=paybillDmpDao.getCommonReportParameter(billNo);
            
            tokenBean = comonScheduleDao.getCompTokenRepotScheduleDetails(billNo, crb.getAqyear(), crb.getAqmonth());
            
            mav.addObject("CTokenHeader", tokenBean);
            mav.setViewName("/payroll/schedule/ComputerTokenReport");
            
        }catch(Exception e){
            e.printStackTrace();
        } 
        return mav;  
    }
    
}
