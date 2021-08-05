/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.controller.payroll.schedule;

import hrms.dao.payroll.schedule.ScheduleDAO;
import hrms.model.payroll.schedule.VacancyStatementScheduleBean;
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
public class VacancyStmtController implements ServletContextAware {

    @Autowired
    public ScheduleDAO comonScheduleDao;
    private ServletContext context;

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.context = servletContext;
    }

    @RequestMapping(value = "VacencyStmtHTML", method = {RequestMethod.POST, RequestMethod.GET})
    public ModelAndView VacencyStmtHTML(@RequestParam("billNo") String billNo) {

        ModelAndView mav = null;
        List vstmtList = null;
        VacancyStatementScheduleBean vsBean = null;
        int gTotal = 0;
        try {
            mav = new ModelAndView();
            vsBean = comonScheduleDao.getVacancyStmtScheduleHeaderDetails(billNo);
            vstmtList = comonScheduleDao.getVacancyStmtScheduleEmpList(billNo);
            
            VacancyStatementScheduleBean obj=null;
            if (vstmtList != null && vstmtList.size() > 0) {
                obj = new VacancyStatementScheduleBean();
                for (int i = 0; i < vstmtList.size(); i++) {
                    obj = (VacancyStatementScheduleBean) vstmtList.get(i);
                    gTotal = obj.getGrandTotal();
                }
            }
            mav.addObject("VSHeader", vsBean);
            mav.addObject("VEpeList", vstmtList);
            mav.addObject("Gtotal", gTotal);
            mav.setViewName("/payroll/schedule/VacancyStmtSchedule");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mav;
    }

}
