/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hrms.controller.payroll.schedule;

import hrms.dao.payroll.schedule.PayBillDMPDAO;
import hrms.dao.payroll.schedule.ScheduleDAOImpl;
import hrms.model.common.CommonReportParamBean;
import hrms.model.payroll.schedule.OtcPlanForm40Bean;
import hrms.model.payroll.schedule.ScheduleHelper;
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
public class OTCForm40Controller implements ServletContextAware{
    
    @Autowired
    public ScheduleDAOImpl comonScheduleDao;
    
    @Autowired
    public PayBillDMPDAO paybillDmpDao;
    
    private ServletContext context;

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.context = servletContext;
    }
    
    @RequestMapping(value = "OTCForm40HTML",method = {RequestMethod.POST,RequestMethod.GET})
    public ModelAndView OTCForm40HTML(@RequestParam("billNo") String billNo){
    
        ModelAndView mav = null;
        List empList = null;
        List alowanceList = null;
        List deductList = null;
        OtcPlanForm40Bean otcBean=null;
        String pageBreak = null;
        try{
            mav = new ModelAndView();
            
            CommonReportParamBean crb=paybillDmpDao.getCommonReportParameter(billNo);
            otcBean = comonScheduleDao.getOTCForm40ScheduleDetails(billNo, crb.getAqyear(), crb.getAqmonth());
            empList = comonScheduleDao.getOTCForm40ScheduleEmpList(billNo, crb.getAqyear(), crb.getAqmonth());
            
            ScheduleHelper obj = null;
            if(empList != null && empList.size() > 0){
                obj = new ScheduleHelper();
                for(int i = 0; i < empList.size(); i++){
                    obj = (ScheduleHelper)empList.get(i);
                    
                    alowanceList = obj.getAllowanceList();
                    deductList = obj.getDeductionList();
                    pageBreak = obj.getPagebreakOTC();
                }
            }
            
            mav.addObject("Otc40Header",otcBean);
            
            mav.addObject("AlowanceList",alowanceList);
            mav.addObject("DeductList",deductList);
            mav.addObject("pBreak",pageBreak);
            mav.setViewName("/payroll/schedule/OTC40");
            
        }catch(Exception e){
            e.printStackTrace();
        } 
        return mav;  
    }
    
}
