/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hrms.controller.payroll.schedule;

import hrms.common.Numtowordconvertion;
import hrms.dao.payroll.schedule.PayBillDMPDAO;
import hrms.dao.payroll.schedule.ScheduleDAO;
import hrms.model.common.CommonReportParamBean;
import hrms.model.payroll.schedule.BillContributionRepotBean;
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
public class BillContributionController implements ServletContextAware{
    
    @Autowired
    public ScheduleDAO comonScheduleDao;
    
    @Autowired
    public PayBillDMPDAO paybillDmpDao;
    
    private ServletContext context;

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.context = servletContext;
    }
    
    @RequestMapping(value = "BillContributionHTML",method = {RequestMethod.POST,RequestMethod.GET})
    public ModelAndView BillContributionHTML(@RequestParam("annexure") String annexure, @RequestParam("billNo") String billNo){
    
        ModelAndView mav = null;
        List empDataList = null;
        String cpfTotal="";
        String gcpfTotal="";
        String cpfTotalFig="";
        String gcpfTotalFig="";
        String gTotal="";
        BillContributionRepotBean billContBean = new BillContributionRepotBean();
        try{
            mav = new ModelAndView();
            
            CommonReportParamBean crb=paybillDmpDao.getCommonReportParameter(billNo);
            billContBean = comonScheduleDao.getBillContributionRepotScheduleHeaderDetails(annexure, billNo);
            empDataList = comonScheduleDao.getBillContributionRepotScheduleEmpList(annexure, billNo, crb.getAqyear(), crb.getAqmonth());
            
            BillContributionRepotBean obj = null;
            if(empDataList != null && empDataList.size() > 0){
                obj = new BillContributionRepotBean();
                for(int i = 0; i < empDataList.size(); i++){
                    obj = (BillContributionRepotBean)empDataList.get(i);
                    
                    cpfTotal= obj.getTotCpf();
                    gcpfTotal=obj.getTotGcpf();
                    gTotal=obj.getGrandTotal();
                    cpfTotalFig=Numtowordconvertion.convertNumber((int)Integer.parseInt(cpfTotal));
                    gcpfTotalFig=Numtowordconvertion.convertNumber((int)Integer.parseInt(gcpfTotal));
                }
            }
            mav.addObject("NpsEmpList",empDataList);
            mav.addObject("NPSHeader",billContBean);
            
            mav.addObject("TotCpf",cpfTotal);
            mav.addObject("TotCpfFig",cpfTotalFig);
            mav.addObject("TotGcpf",gcpfTotal);
            mav.addObject("TotGcpfFig",gcpfTotalFig);
            mav.addObject("GrandTot",gTotal);
            
            if(annexure.equalsIgnoreCase("annexure1")){
                mav.setViewName("/payroll/schedule/BillContributionA1"); //annexure1
                
            }else if(annexure.equalsIgnoreCase("annexure2")){
                mav.setViewName("/payroll/schedule/BillContributionA2"); //annexure2 
                
            }else if(annexure.equalsIgnoreCase("annexure3")){
                mav.setViewName("/payroll/schedule/BillContributionA3"); //annexure3
                
            }else if(annexure.equalsIgnoreCase("annexure4")){
                mav.setViewName("/payroll/schedule/BillContributionA4"); //annexure4 
            }
            
        }catch(Exception e){
            e.printStackTrace();
        } 
        return mav;  
    }
    
}
