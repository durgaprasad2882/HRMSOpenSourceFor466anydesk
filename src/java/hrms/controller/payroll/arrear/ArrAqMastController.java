/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.controller.payroll.arrear;

import hrms.dao.payroll.arrear.ArrmastDAO;
import hrms.dao.payroll.billbrowser.AqReportDAO;
import hrms.dao.payroll.schedule.PayBillDMPDAO;
import hrms.model.common.CommonReportParamBean;
import hrms.model.login.LoginUserBean;
import hrms.model.payroll.arrear.ArrAqList;
import hrms.model.payroll.arrear.ArrAqMastModel;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jxl.Workbook;
import jxl.write.WritableWorkbook;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author prashant
 */
@Controller
@SessionAttributes("LoginUserBean")
public class ArrAqMastController {

    @Autowired
    public ArrmastDAO arrmastDAO;
    
    @Autowired
    public PayBillDMPDAO paybillDmpDao;
    
    @Autowired
    public AqReportDAO aqReportDAO;

    @RequestMapping(value = "arrAqMastReport.htm", method = RequestMethod.GET)
    public ModelAndView arrAqMastReportHTML(@RequestParam("billNo") int billNo, @ModelAttribute("LoginUserBean") LoginUserBean lub,
            @ModelAttribute("CommonReportParamBean") CommonReportParamBean crb, @ModelAttribute("ArrAqMastModel") ArrAqMastModel arrAqMastBean,
            BindingResult result, HttpServletRequest request) {

        ModelAndView mav = null;
        List arrAqList = new ArrayList();
        String year = "";
        String month = "";

        try {

            mav = new ModelAndView();
            crb = aqReportDAO.getBillDetails(billNo + "");
            year = crb.getAqyear() + "";
            month = crb.getAqmonth() + "";

            arrAqList = arrmastDAO.getArrearBillDtls(billNo, month, year);

            mav.addObject("ArrEmpList", arrAqList);
            mav.setViewName("/payroll/arrear/ArrAqMast");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return mav;
    }

    @ResponseBody
    @RequestMapping(value = "saveItArrMast")
    public void SaveItArrMast(@RequestParam("billNo") int billNo, @RequestParam("aqslno") String aqSlNo, HttpServletResponse response,
            @ModelAttribute("ArrAqMastModel") ArrAqMastModel arrAqMastBean, @RequestParam("taxAmt") int taxAmt) {

        response.setContentType("application/json");
        PrintWriter out = null;
        try {
            int updSts = arrmastDAO.updateArrMastItData(billNo, aqSlNo, taxAmt);
            JSONObject json = new JSONObject('Y');
            out = response.getWriter();
            out.write(json.toString());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
        }
    }

    @ResponseBody
    @RequestMapping(value = "saveCpfArrMast")
    public void SaveCpfArrMast(@RequestParam("billNo") int billNo, @RequestParam("aqslno") String aqSlNo, HttpServletResponse response,
            @ModelAttribute("ArrAqMastModel") ArrAqMastModel arrAqMastBean, @RequestParam("cpfAmt") int cpfAmt) {

        response.setContentType("application/json");
        PrintWriter out = null;
        try {
            int updSts = arrmastDAO.updateArrMastCpfData(billNo, aqSlNo, cpfAmt);
            JSONObject json = new JSONObject('Y');
            out = response.getWriter();
            out.write(json.toString());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
        }
    }

    @ResponseBody
    @RequestMapping(value = "savePtArrMast")
    public void SavePtArrMast(@RequestParam("billNo") int billNo, @RequestParam("aqslno") String aqSlNo, HttpServletResponse response,
            @ModelAttribute("ArrAqMastModel") ArrAqMastModel arrAqMastBean, @RequestParam("ptAmt") int ptAmt) {

        response.setContentType("application/json");
        PrintWriter out = null;
        try {
            int updSts = arrmastDAO.updateArrMastPtData(billNo, aqSlNo, ptAmt);

            JSONObject json = new JSONObject('Y');
            out = response.getWriter();
            out.write(json.toString());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
        }
    }

   @RequestMapping(value = "getArrearAcqEmpDet", method = {RequestMethod.GET, RequestMethod.POST})
    public String getArrearAcqEmpDet(ModelMap model, @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response, @RequestParam("billNo") String billNo) {
        List li=arrmastDAO.getArrearAcqEmpDet( billNo);
        ArrAqList arrempList=new ArrAqList();
        List hlist=new ArrayList();
	model.addAttribute("arrAcqList", li);
        
        if(li.size()>0){
         arrempList = (ArrAqList) li.get(0);
            model.addAttribute("arrheaderList", arrempList.getEmpList()); 
        }else{
            arrempList.setEmpList(hlist);
            arrempList.setEmpName("");
            model.addAttribute("arrheaderList", arrempList); 
        }
        return "/payroll/arrear/ArrAqEmpDetails";

    }

    @RequestMapping(value = "downloadArrearAcqEmpExcel")
    public String downloadArrearAcqEmpExcel(@ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response, @RequestParam("billNo") String billNo) {
	
        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        try{
            String fileName = "OFFICE_DATA_" + lub.getOffcode() + ".xls";
            out = new BufferedOutputStream(response.getOutputStream());
            
            WritableWorkbook workbook = Workbook.createWorkbook(out);
            
            arrmastDAO.downloadArrearAcqEmpExcel(out,lub.getOffcode(),workbook,billNo);
            
            response.setHeader("Content-Disposition", "attachment; filename="+fileName);
            workbook.write();
            workbook.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(value = "deleteArrMast")
    public ModelAndView delArrearAqMast(@ModelAttribute("LoginUserBean") LoginUserBean lub, HttpServletResponse response, @ModelAttribute("ArrAqMastModel") ArrAqMastModel arrAqMastBean) {
        ModelAndView mv = new ModelAndView("/payroll/arrear/BrowseArrAquitance");
        
        CommonReportParamBean crb = paybillDmpDao.getCommonReportParameter(arrAqMastBean.getBillNo() + "");
        if(crb.getBillStatusId() == 5 || crb.getBillStatusId() == 7 || crb.getBillStatusId() == 3){
            
        }else if(crb.getBillStatusId() < 2 || crb.getBillStatusId() == 4 || crb.getBillStatusId() == 8){
            arrmastDAO.deleteArrMastData(arrAqMastBean.getAqSlNo(), arrAqMastBean.getBillNo());
            List arrAqList = arrmastDAO.getArrearAcquaintance(arrAqMastBean.getBillNo());
            mv.addObject("AqArrList", arrAqList);
            mv.addObject("billNo", arrAqMastBean.getBillNo());
        }
        return mv;
    }
    
    @ResponseBody
    @RequestMapping(value = "reCalArrMast")
    public void ReCalculateArrMast(@RequestParam("billNo") int billNo, HttpServletResponse response ) {

        response.setContentType("application/json");
        PrintWriter out = null;
        try {
            int updSts = arrmastDAO.reCalculateArrMast(billNo);

            JSONObject json = new JSONObject('Y');
            out = response.getWriter();
            out.write(json.toString());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
        }
    }

}
