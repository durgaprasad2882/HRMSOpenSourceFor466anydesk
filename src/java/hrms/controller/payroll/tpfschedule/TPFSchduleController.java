package hrms.controller.payroll.tpfschedule;

import hrms.common.CalendarCommonMethods;
import hrms.dao.payroll.tpschedule.TPFScheduleDAOImpl;
import hrms.model.payroll.tpfschedule.TpfTypeBean;
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
public class TPFSchduleController implements ServletContextAware {
    
    @Autowired
    public TPFScheduleDAOImpl tpfScheduleDao;
    
    private ServletContext context;

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.context = servletContext;
    }
    
    @RequestMapping(value = "TPFScheduleHTML",method = {RequestMethod.POST,RequestMethod.GET})
    public ModelAndView TPFScheduleHTML(@RequestParam("billNo") String billNo){
        
        ModelAndView mav = null;
        
        List tpfEmpList = null;
        List tpfAbstract = null;
        try{
            mav = new ModelAndView();
            
            tpfEmpList = tpfScheduleDao.getEmployeeWiseTPFList(billNo);
            tpfAbstract = tpfScheduleDao.getTPFAbstract(billNo);
            
            mav.addObject("billNo",billNo);
            mav.addObject("billDesc",tpfScheduleDao.getCommonReportParameter(billNo).getBilldesc());
            //System.out.println("Bill Desn is: "+tpfScheduleDao.getCommonReportParameter(billNo).getBilldesc());
            mav.addObject("billMonth",CalendarCommonMethods.getFullMonthAsString(tpfScheduleDao.getCommonReportParameter(billNo).getAqmonth()));
            mav.addObject("billYear",tpfScheduleDao.getCommonReportParameter(billNo).getAqyear());
            mav.addObject("ddodesg",tpfScheduleDao.getCommonReportParameter(billNo).getDdoname());
            mav.addObject("offname",tpfScheduleDao.getCommonReportParameter(billNo).getOfficeen());
            
            mav.addObject("TPFList",tpfEmpList);
            mav.addObject("TPFAbstract",tpfAbstract);
            mav.setViewName("/payroll/tpfschedule/TPFSchedule");
        }catch(Exception e){
            e.printStackTrace();
        }
      return mav;  
    }
}
