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
import hrms.dao.payroll.schedule.PayBillDMPDAO;
import hrms.dao.payroll.schedule.ScheduleDAO;
import hrms.model.common.CommonReportParamBean;
import hrms.model.payroll.schedule.PtScheduleBean;
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
public class PTSchduleController implements ServletContextAware{
    
    @Autowired
    public ScheduleDAO comonScheduleDao;
    
    @Autowired
    public PayBillDMPDAO paybillDmpDao;
    
    private ServletContext context;

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.context = servletContext;
    }
    
    @RequestMapping(value = "PTScheduleHTML",method = {RequestMethod.POST,RequestMethod.GET})
    public ModelAndView PTScheduleHTML(@RequestParam("billNo") String billNo){

        ModelAndView mav = null;
        
        List ptEmpList = null;
        PtScheduleBean ptHeaderDtls=null;
        String totalTaxFig=null;
        String totalFig=null;
		String totalTax="";
        int totalGross=0;
        try{
            mav = new ModelAndView();
            
            ptHeaderDtls = comonScheduleDao.getPTScheduleHeaderDetails(billNo);
            CommonReportParamBean crb=paybillDmpDao.getCommonReportParameter(billNo);
            ptEmpList = comonScheduleDao.getPTScheduleEmployeeList(billNo,crb.getAqmonth(),crb.getAqyear());
                      
            PtScheduleBean obj = null;
            if(ptEmpList != null && ptEmpList.size() > 0){
                obj = new PtScheduleBean();
                for(int i = 0; i < ptEmpList.size(); i++){
                    obj = (PtScheduleBean)ptEmpList.get(i);
                    totalTax=obj.getTotalTax();
                    totalGross=Integer.parseInt(obj.getTotalGross());
                    totalTaxFig=Numtowordconvertion.convertNumber(Integer.parseInt(totalTax)).toUpperCase();
                }
            }
            
            mav.addObject("PTEmpList",ptEmpList);
            mav.addObject("PTHeader",ptHeaderDtls);
            mav.addObject("TotTax",totalTax);
            mav.addObject("TotGros",totalGross);
            mav.addObject("TotalFig",totalTaxFig);
            mav.setViewName("/payroll/schedule/PTSchedule");
            
        }catch(Exception e){
            e.printStackTrace();
        } 
        return mav;  
    }
    
    @RequestMapping(value = "PTSchedulePDF")
    public void PTSchedulePagePDF(HttpServletResponse response, @RequestParam("billNo") String billNo) {
        response.setContentType("application/pdf");
        Document document = new Document(PageSize.A4);
        try {
            
            response.setHeader("Content-Disposition", "attachment; filename=PTSchedule_"+billNo+".pdf");
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            CommonReportParamBean crb=paybillDmpDao.getCommonReportParameter(billNo);
            PtScheduleBean ptBean = comonScheduleDao.getPTScheduleHeaderDetails(billNo);
            List empList = comonScheduleDao.getPTScheduleEmployeeList(billNo, crb.getAqmonth(),crb.getAqyear());
            
            comonScheduleDao.PTSchedulePagePDF(document, billNo, ptBean, empList, crb.getAqmonth(),crb.getAqyear());
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
    }
    
    
}
