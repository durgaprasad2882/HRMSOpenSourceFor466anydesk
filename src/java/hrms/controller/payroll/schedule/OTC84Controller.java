/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hrms.controller.payroll.schedule;

import hrms.dao.payroll.schedule.PayBillDMPDAO;
import hrms.dao.payroll.schedule.ScheduleDAO;
import hrms.model.common.CommonReportParamBean;
import hrms.model.payroll.schedule.OTC84Bean;
import java.util.List;
import javax.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class OTC84Controller implements ServletContextAware{
    
    @Autowired
    public ScheduleDAO comonScheduleDao;
    
    @Autowired
    public PayBillDMPDAO paybillDmpDao;
    
    private ServletContext context;

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.context = servletContext;
    }
    
    @RequestMapping(value = "OTCForm84HTML",method = {RequestMethod.POST,RequestMethod.GET})
    public ModelAndView OTCForm84HTML(@RequestParam("billNo") String billNo){
    
        ModelAndView mav = null;
        List empList = null;
        OTC84Bean otc84Bean = new OTC84Bean();
        try{
            mav = new ModelAndView();
            
            CommonReportParamBean crb = paybillDmpDao.getCommonReportParameter(billNo);
            otc84Bean = comonScheduleDao.getOTC84ScheduleDetails(billNo, crb.getAqyear(), crb.getAqmonth());
            
            mav.addObject("OTC84Header", otc84Bean);     
            mav.setViewName("/payroll/schedule/OTC84");
            
        }catch(Exception e){
            e.printStackTrace();
        } 
        return mav;  
    }
    
}
