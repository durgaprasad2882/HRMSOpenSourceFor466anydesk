/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hrms.controller.payroll.schedule;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import hrms.common.Numtowordconvertion;
import hrms.common.PayrollCommonFunction;
import hrms.dao.payroll.schedule.PayBillDMPDAO;
import hrms.dao.payroll.schedule.ScheduleDAO;
import hrms.model.common.CommonReportParamBean;
import hrms.model.payroll.schedule.WrrScheduleBean;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class WRRSchduleController implements ServletContextAware{
    
    @Autowired
    public ScheduleDAO comonScheduleDao;
    
    @Autowired
    public PayBillDMPDAO paybillDmpDao;
    
    private ServletContext context;

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.context = servletContext;
    }
    
    @RequestMapping(value = "WRRScheduleHTML",method = {RequestMethod.POST,RequestMethod.GET})
    public ModelAndView WRRScheduleHTML(@RequestParam("schedule") String schedule, @RequestParam("billNo") String billNo ){

        ModelAndView mav = null;
        
        List wrrEmpList = null;
        WrrScheduleBean wrrHeaderDtls=null;
        double totalAmt=0.0;
        double amt=0.0;
        String totalFig=null;
        
        try{
            mav = new ModelAndView();
            PayrollCommonFunction prcf = new PayrollCommonFunction();
            
            CommonReportParamBean crb=paybillDmpDao.getCommonReportParameter(billNo);
            wrrHeaderDtls = comonScheduleDao.getWRRScheduleHeaderDetails(billNo,schedule);
            wrrEmpList = comonScheduleDao.getWRRScheduleEmployeeList(billNo, schedule, crb.getAqmonth(), crb.getAqyear(), "HTML");
            
            WrrScheduleBean obj = null;
            if(wrrEmpList != null && wrrEmpList.size() > 0){
                obj = new WrrScheduleBean();
                for(int i = 0; i < wrrEmpList.size(); i++){
                    obj = (WrrScheduleBean)wrrEmpList.get(i);
                        amt = Double.parseDouble(obj.getAmount());
                        totalAmt=totalAmt+amt;
                        totalFig=Numtowordconvertion.convertNumber((int)totalAmt);
                }
            }

            mav.addObject("offName",comonScheduleDao.getCommonReportParameter(billNo).getOfficeen());
            mav.addObject("WRREmpList",wrrEmpList);
            mav.addObject("Amount",totalAmt);
            mav.addObject("TotalFig",totalFig);
            mav.addObject("WRRHeader",wrrHeaderDtls);
            
            if(schedule.equals("WRR") || schedule.equals("HRR")){
                mav.setViewName("/payroll/schedule/WRRSchedule");
                
            }else if(schedule.equals("SWR")){
                mav.setViewName("/payroll/schedule/SwerageSchedule");
            }
            
        }catch(Exception e){
            e.printStackTrace();
        } 
        return mav;  
    }
    
    @RequestMapping(value = "WRRSchedulePDF")
    public void WRRSchedulePagePDF(HttpServletResponse response, @RequestParam("schedule") String schedule, @RequestParam("billNo") String billNo) {

        response.setContentType("application/pdf");
        Document document = new Document(PageSize.A4);
        try {
            
            if(schedule.equals("WRR")){
                response.setHeader("Content-Disposition", "attachment; filename=WRRSchedule_"+billNo+".pdf");
            }else if(schedule.equals("HRR")){
                response.setHeader("Content-Disposition", "attachment; filename=HRRSchedule_"+billNo+".pdf");
            }else if(schedule.equals("SWR")){
                response.setHeader("Content-Disposition", "attachment; filename=SwerageSchedule_"+billNo+".pdf");
            }
            
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            CommonReportParamBean crb=paybillDmpDao.getCommonReportParameter(billNo);
            WrrScheduleBean wrrBean = comonScheduleDao.getWRRScheduleHeaderDetails(billNo,schedule);
            List wrrEmpList = comonScheduleDao.getWRRScheduleEmployeeList(billNo, schedule, crb.getAqmonth(), crb.getAqyear(), "PDF");
            
            comonScheduleDao.WRRSchedulePagePDF(document, schedule, billNo, wrrBean, wrrEmpList);
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
    }
    
}
