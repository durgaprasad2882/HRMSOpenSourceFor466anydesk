/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.controller.thread;


import hrms.SelectOption;
import hrms.common.LogMessage;
import hrms.dao.master.TreasuryDAO;
import hrms.model.login.Users;
import hrms.model.payroll.billbrowser.BillBrowserbean;
import hrms.thread.ag.LTAAQReportPDFThread;
import hrms.thread.ag.LTAPDFThread;
import hrms.thread.ag.LTAThread;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Manas Jena
 */
@Controller
public class LTAThreadController {
    
    @Autowired
    public LTAThread ltaThread;
    
    @Autowired
    public LTAPDFThread ltaPDFThread;
    
    @Autowired
    public LTAAQReportPDFThread ltaAquitancePDFThread;
    
    @Autowired
    public TreasuryDAO treasuryDao;
    
    @RequestMapping(value = "createXMLFile")
    public ModelAndView createXMLFile(ModelMap model, @ModelAttribute("billBrowserForm") BillBrowserbean bbbean,BindingResult result, HttpServletResponse response){
        LogMessage.setMessage("XML Creation called");
        if(ltaThread.getThreadStatus() == 0){
            LogMessage.setMessage("Inside the thread");
            LogMessage.setMessage("bbbean.getSltMonth():"+bbbean.getSltMonth()+"bbbean.getSltYear()"+bbbean.getSltYear()+" HBA");
            ltaThread.setThreadValue(bbbean.getSltMonth(),bbbean.getSltYear(),"HBA");
            Thread t = new Thread(ltaThread);
            t.start();
        }        
        
        ModelAndView mav = new ModelAndView();        
        mav.setViewName("payroll/LTAScheduleForAG");
        return mav;
                
    }
    
    @RequestMapping(value = "showXMLFileCreation", method = RequestMethod.GET)
    public ModelAndView showXMLFileCreation(ModelMap model, @ModelAttribute("loginForm") Users loginForm,BindingResult result, HttpServletResponse response){
        ModelAndView mav = new ModelAndView();
        
        mav.setViewName("payroll/LTAScheduleForAG");
        return mav;
    }
    
    @RequestMapping(value = "viewLogAction", method = RequestMethod.GET)
    public void viewLogAction(HttpServletRequest request, HttpServletResponse response){
        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.write(LogMessage.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
        }
    }
    
    @RequestMapping(value = "LTAScheduleBulkPDFforAG")
    public ModelAndView LTAScheduleBulkPDFforAG(@ModelAttribute("billBrowserForm") BillBrowserbean bbbean){
        
        ModelAndView mav = new ModelAndView();

        SelectOption so = new SelectOption();
        List yearlist = new ArrayList();
        try {

            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);

            so.setLabel(year + "");
            so.setValue(year + "");
            yearlist.add(so);

            for (int i = 0; i < 4; i++) {
                int tempyear = year - 1;
                year = tempyear;
                so = new SelectOption();
                so.setLabel(year + "");
                so.setValue(year + "");
                yearlist.add(so);
            }
            mav.addObject("yearlist", yearlist);
            mav.setViewName("payroll/CreateLoanBulkPDFAG");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mav;
    }

    @RequestMapping(value = "createLTABulkPDFforAG")
    public ModelAndView createLTABulkPDFforAG(ModelMap model, @ModelAttribute("billBrowserForm") BillBrowserbean bbbean, BindingResult result, HttpServletResponse response) {

        try {
            if (ltaPDFThread.getThreadStatus() == 0) {
                LogMessage.setMessage("Inside the Bulk PDF thread");
                LogMessage.setMessage("bbbean.getSltMonth(): " + bbbean.getSltMonth() + "bbbean.getSltYear(): " + bbbean.getSltYear() + " and bbbean.getSltLoan(): " +bbbean.getSltLoan());
                ltaPDFThread.setThreadValue(bbbean.getSltMonth(), bbbean.getSltYear(), bbbean.getSltLoan());
                Thread t = new Thread(ltaPDFThread);
                t.start();
            }
            //ltaService.createLTAScheduleBulkPDFFileForAG(bbbean.getSltMonth(), bbbean.getSltYear(), bbbean.getSltLoan());
        } catch (Exception e) {
            e.printStackTrace();
        }
        ModelAndView mav = new ModelAndView("redirect:/LTAScheduleBulkPDFforAG.htm");
        return mav;
    }
    
    @RequestMapping(value = "LTAAQuitancePDFforAG")
    public ModelAndView LTAAQuitancePDFforAG(@ModelAttribute("billBrowserForm") BillBrowserbean bbbean) {

        ModelAndView mav = new ModelAndView();

        SelectOption so = new SelectOption();
        List yearlist = new ArrayList();
        try {
            List trlist = treasuryDao.getAGTreasuryList();
            mav.addObject("trlist", trlist);

            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);

            so.setLabel(year + "");
            so.setValue(year + "");
            yearlist.add(so);

            for (int i = 0; i < 4; i++) {
                int tempyear = year - 1;
                year = tempyear;
                so = new SelectOption();
                so.setLabel(year + "");
                so.setValue(year + "");
                yearlist.add(so);
            }
            mav.addObject("yearlist", yearlist);
            mav.setViewName("payroll/CreateAquitancePDFAG");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mav;
    }

    @RequestMapping(value = "createLTAAquitancePDFforAG")
    public ModelAndView createLTAAquitancePDFforAG(ModelMap model, @ModelAttribute("billBrowserForm") BillBrowserbean bbbean, BindingResult result, HttpServletResponse response) {

        try {
            if (ltaAquitancePDFThread.getThreadStatus() == 0) {
                ltaAquitancePDFThread.setThreadValue(bbbean.getSltMonth(), bbbean.getSltYear(), bbbean.getSltTrCode());
                Thread t = new Thread(ltaAquitancePDFThread);
                t.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ModelAndView mav = new ModelAndView("redirect:/LTAAQuitancePDFforAG.htm?sltTrCode="+bbbean.getSltTrCode());
        return mav;
    }
}
