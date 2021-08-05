/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.controller.payroll.billbrowser;

import hrms.dao.payroll.arrear.ArrmastDAO;
import hrms.dao.payroll.billbrowser.AqReportDAO;
import hrms.dao.payroll.schedule.PayBillDMPDAO;
import hrms.model.common.CommonReportParamBean;
import hrms.model.login.LoginUserBean;
import hrms.model.payroll.arrear.ArrAqDtlsModel;
import hrms.model.payroll.arrear.ArrAqMastModel;
import hrms.model.payroll.arrear.PayRevisionOption;
import hrms.model.payroll.billbrowser.AcquaintanceBean;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Manas Jena
 */
@Controller
@SessionAttributes("LoginUserBean")
public class AquitanceBrowserController {

    @Autowired
    AqReportDAO aqReportDao;

    @Autowired
    public PayBillDMPDAO paybillDmpDao;

    @Autowired
    public ArrmastDAO arrmastDAO;

    @RequestMapping(value = "browseAquitance", method = RequestMethod.GET)
    public ModelAndView browseAquitance(ModelMap model, @ModelAttribute("LoginUserBean") LoginUserBean lub, @RequestParam("billNo") String billNo, HttpServletResponse response) {//
        ModelAndView mv = new ModelAndView("/payroll/BrowseAquitance");
        
        List aquitanceList = aqReportDao.getAcquaintance(billNo);
        mv.addObject("aquitanceList", aquitanceList);
        mv.addObject("billNo", billNo);
        return mv;
    }

    @RequestMapping(value = "browseAquitanceData", method = RequestMethod.GET)
    public ModelAndView browseAquitanceData(ModelMap model, @ModelAttribute("LoginUserBean") LoginUserBean lub, @RequestParam("aqslno") String aqslno, @RequestParam("billNo") String billNo, HttpServletResponse response) {
        int basic = 0;
        int totded = 0;
        int totall = 0;
        double gross = 0;
        double net = 0;
        double totAllowance = 0.0;
        double totDeduction = 0.0;
        ModelAndView mv = new ModelAndView("/payroll/BrowseAquitanceData");
        String tableName = aqReportDao.getAqDtlsTableName(billNo);
        AcquaintanceBean aqReportBean = aqReportDao.getAqMastDtl(aqslno);
        ArrayList deductionobjList = aqReportDao.getAcquaintanceDtlDed(aqslno, tableName);
        ArrayList allowanceObjList = aqReportDao.getAcquaintanceDtlAll(aqslno, tableName);
        //totAllowance = aqReportDao.getTotalAllowance(aqslno);
        //totDeduction = aqReportDao.getTotalDeduction(aqslno);
        gross = totAllowance + aqReportBean.getCurbasic();
        net = gross - totDeduction;
        mv.addObject("totAll", totAllowance);
        mv.addObject("totDed", totDeduction);
        mv.addObject("gross", gross);
        mv.addObject("net", net);
        mv.addObject("aqSlNo", aqslno);
        mv.addObject("billNo", billNo);
        mv.addObject("aqReportBean", aqReportBean);
        mv.addObject("deductionobjList", deductionobjList);
        mv.addObject("allowanceObjList", allowanceObjList);
        return mv;
    }

    @RequestMapping(value = "backToBillListPage")
    public ModelAndView backToBrowseAquitanceArr(ModelMap model, @ModelAttribute("ArrAqDtlsModel") ArrAqDtlsModel arrDtlsBean, @RequestParam("billNo") int billNo, HttpServletResponse response) {

        ModelAndView mv = new ModelAndView("redirect:/browseAquitanceArr.htm?billNo=" + billNo);

        return mv;
    }

    @ResponseBody
    @RequestMapping(value = "reprocessArrAqMast")
    public void reprocessArrAqMast(@ModelAttribute("LoginUserBean") LoginUserBean lub, @RequestParam("aqslno") String aqslno, @RequestParam("billNo") int billNo, HttpServletResponse response) {
        response.setContentType("application/json");
        PrintWriter out = null;
        HashMap<String, Integer> hmap = new HashMap<String, Integer>();
        try {
            out = response.getWriter();
            CommonReportParamBean crb = paybillDmpDao.getCommonReportParameter(billNo+ "");
            if(crb.getBillStatusId() == 5 || crb.getBillStatusId() == 7 || crb.getBillStatusId() == 3){

            }else if(crb.getBillStatusId() < 2 || crb.getBillStatusId() == 4 || crb.getBillStatusId() == 8){
                arrmastDAO.reprocessArrAqMast(aqslno);

                hmap.put("processed", 1);
                JSONObject jsonobj = new JSONObject(hmap);
                out.write(jsonobj.toString());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
        }
    }

    @RequestMapping(value = "browseAquitanceArr", method = RequestMethod.GET)
    public ModelAndView browseAquitanceArr(ModelMap model, @ModelAttribute("LoginUserBean") LoginUserBean lub, @RequestParam("billNo") int billNo, HttpServletResponse response) {//

        ModelAndView mv = new ModelAndView("/payroll/arrear/BrowseArrAquitance");
        CommonReportParamBean crb = paybillDmpDao.getCommonReportParameter(billNo + "");
        List arrAqList = arrmastDAO.getArrearAcquaintance(billNo);
        
        mv.addObject("BillSts", crb.getBillStatusId());
        mv.addObject("AqArrList", arrAqList);
        mv.addObject("billNo", billNo);

        return mv;
    }

    @RequestMapping(value = "browseAquitanceArrReport", method = RequestMethod.GET)
    public ModelAndView browseAquitanceArrReport(ModelMap model, @ModelAttribute("LoginUserBean") LoginUserBean lub, @RequestParam("billNo") int billNo, HttpServletResponse response) {//

        ModelAndView mv = new ModelAndView("/payroll/arrear/BrowseArrAquitanceReport");
        List arrAqList = arrmastDAO.getArrearAcquaintance(billNo);

        mv.addObject("AqArrList", arrAqList);
        mv.addObject("billNo", billNo);

        return mv;
    }

    @RequestMapping(value = "browseArrAqDataReport", method = RequestMethod.GET)
    public ModelAndView browseArrAquitanceDataReport(ModelMap model, @ModelAttribute("ArrAqDtlsModel") ArrAqDtlsModel arrMastBean, @RequestParam("aqslno") String aqslno, @RequestParam("billNo") int billNo, HttpServletResponse response) {

        ModelAndView mv = new ModelAndView("/payroll/arrear/BrowseArrAAquitanceDataReport");

        CommonReportParamBean crb = paybillDmpDao.getCommonReportParameter(billNo + "");
        List headerDataList = arrmastDAO.getArrearAqHeaderData(billNo);
        ArrAqMastModel arrAqMastBean = arrmastDAO.getArrearAcquaintanceData(aqslno);

        mv.addObject("OffName", crb.getOfficename());
        mv.addObject("DeptName", crb.getDeptname());
        mv.addObject("HeaderDataList", headerDataList);
        mv.addObject("arrAqMastBean", arrAqMastBean);
        
        return mv;
    }

    @RequestMapping(value = "browseArrAqData", method = RequestMethod.GET)
    public ModelAndView browseArrAquitanceData(ModelMap model, @ModelAttribute("ArrAqDtlsModel") ArrAqDtlsModel arrMastBean, @RequestParam("aqslno") String aqslno, @RequestParam("billNo") int billNo, HttpServletResponse response) {

        arrMastBean.setAqslno(aqslno);
        ModelAndView mv = new ModelAndView("/payroll/arrear/BrowseArrAAquitanceData", "command", arrMastBean);
        
        CommonReportParamBean crb = paybillDmpDao.getCommonReportParameter(billNo + "");
        List headerDataList = arrmastDAO.getArrearAqHeaderData(billNo);
        ArrAqMastModel arrAqMastBean = arrmastDAO.getArrearAcquaintanceData(aqslno);
        
        mv.addObject("OffName", crb.getOfficename());
        mv.addObject("DeptName", crb.getDeptname());
        mv.addObject("HeaderDataList", headerDataList);
        mv.addObject("arrAqMastBean", arrAqMastBean);
        mv.addObject("BillSts", crb.getBillStatusId());

        return mv;
    }

    @ResponseBody
    @RequestMapping(value = "editArrData")
    public void GetArrDtlsDataJSON(HttpServletResponse response, @ModelAttribute("LoginUserBean") LoginUserBean lub, @RequestParam("billNo") int billNo,
            @RequestParam("aqslno") String aqSlno, @RequestParam("pmonth") int pMonth, @RequestParam("pyear") int pYear) {

        response.setContentType("application/json");
        PrintWriter out = null;

        try {
            out = response.getWriter();
            CommonReportParamBean crb = paybillDmpDao.getCommonReportParameter(billNo+ "");
            
            if(crb.getBillStatusId() == 5 || crb.getBillStatusId() == 7 || crb.getBillStatusId() == 3){

            }else if(crb.getBillStatusId() < 2 || crb.getBillStatusId() == 4 || crb.getBillStatusId() == 8){
                ArrAqDtlsModel arrAqDtlsBean = arrmastDAO.getArrearAcquaintanceDataList(aqSlno, pMonth, pYear);
                JSONObject json = new JSONObject(arrAqDtlsBean);
                out.write(json.toString());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
        }
    }

    @ResponseBody
    @RequestMapping(value = "deleteArrAqDtls", method = RequestMethod.GET)
    public void deleteArrAqDtls(HttpServletResponse response, @ModelAttribute("LoginUserBean") LoginUserBean lub, 
            @ModelAttribute("ArrAqDtlsModel") ArrAqDtlsModel arrDtlsBean, @ModelAttribute("billNo") int billNo) {
        
        response.setContentType("application/json");
        PrintWriter out = null;
        
        try {
            
            out = response.getWriter();
            CommonReportParamBean crb = paybillDmpDao.getCommonReportParameter(billNo+ "");
            if(crb.getBillStatusId() == 5 || crb.getBillStatusId() == 7 || crb.getBillStatusId() == 3){

            }else if(crb.getBillStatusId() < 2 || crb.getBillStatusId() == 4 || crb.getBillStatusId() == 8){
                int deleted = arrmastDAO.deleteArrDtlsData(arrDtlsBean);
            
                HashMap<String, Integer> hmap = new HashMap<String, Integer>();
                hmap.put("deleted", deleted);
                JSONObject jsonobj = new JSONObject(hmap);
                out.write(jsonobj.toString());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
        }
    }

    @ResponseBody
    @RequestMapping(value = "updateArrDtls")
    public void UpdateArrDtlsData(HttpServletResponse response, @ModelAttribute() ArrAqDtlsModel arrDtlsBean) {
        response.setContentType("application/json");
        PrintWriter out = null;

        try {

            int updSts = arrmastDAO.updateArrDtlsData(arrDtlsBean);
            ArrAqDtlsModel arrAqDtlsBean = arrmastDAO.getArrearAcquaintanceDataList(arrDtlsBean.getAqslno(), arrDtlsBean.getPayMonth(), arrDtlsBean.getPayYear());
            JSONObject json = new JSONObject(arrAqDtlsBean);
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
    @RequestMapping(value = "searchEmpForArrMast")
    public void searchEmpForArrMast(HttpServletResponse response, @ModelAttribute("LoginUserBean") LoginUserBean lub, @RequestParam("searchemp") String searchemp) {
        response.setContentType("application/json");
        PrintWriter out = null;
        PayRevisionOption payRevisionOption = arrmastDAO.searchEmployee(searchemp);
        try {
            JSONObject json = new JSONObject(payRevisionOption);
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
    @RequestMapping(value = "addEmployeeToBill")
    public void addEmployeeToBill(HttpServletResponse response, @ModelAttribute("LoginUserBean") LoginUserBean lub, @ModelAttribute("arrAqMastModel") ArrAqMastModel arrAqMastModel) {
        PayRevisionOption payRevisionOption = arrmastDAO.searchEmployee(arrAqMastModel.getEmpCode());
        String aqslNo = "";
        if (payRevisionOption.getMsgcode() == 0) {
            aqslNo = arrmastDAO.addEmployeeToBill(arrAqMastModel);
        }else if(payRevisionOption.getMsgcode() == 2){            
            arrmastDAO.insertIntoPayRevisionOption(arrAqMastModel.getInputChoiceDate(),arrAqMastModel.getPayrevisionbasic(),arrAqMastModel.getEmpCode());
            aqslNo = arrmastDAO.addEmployeeToBill(arrAqMastModel);
        }
        response.setContentType("application/json");
        PrintWriter out = null;
        try {
            HashMap<String, String> hmap = new HashMap<String, String>();
            hmap.put("aqslNo", aqslNo);
            JSONObject jsonobj = new JSONObject(hmap);
            out = response.getWriter();
            out.write(jsonobj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
        }
    }

    @ResponseBody
    @RequestMapping(value = "saveArrData")
    public void SaveArrDtlsData(HttpServletResponse response, @ModelAttribute() ArrAqDtlsModel arrDtlsBean) {

        response.setContentType("application/json");
        PrintWriter out = null;

        try {

            ArrAqDtlsModel[] arrAqDtlsModels = new ArrAqDtlsModel[3];

            ArrAqDtlsModel arrAqDtlsModel = new ArrAqDtlsModel();

            arrAqDtlsModel.setAqslno(arrDtlsBean.getAqslno());
            arrAqDtlsModel.setPayMonth(arrDtlsBean.getPayMonth());
            arrAqDtlsModel.setPayYear(arrDtlsBean.getPayYear());
            arrAqDtlsModel.setAdType("PAY");
            arrAqDtlsModel.setDueAmt(arrDtlsBean.getDuePayAmt());
            arrAqDtlsModel.setDrawnAMt(arrDtlsBean.getDrawnPayAmt());
            arrAqDtlsModel.setDrawnBillNo(arrDtlsBean.getDrawnBillNo());
            arrAqDtlsModel.setRefaqslno("0");
            arrAqDtlsModels[0] = arrAqDtlsModel;

            arrAqDtlsModel = new ArrAqDtlsModel();
            arrAqDtlsModel.setAqslno(arrDtlsBean.getAqslno());
            arrAqDtlsModel.setPayMonth(arrDtlsBean.getPayMonth());
            arrAqDtlsModel.setPayYear(arrDtlsBean.getPayYear());
            arrAqDtlsModel.setAdType("GP");
            arrAqDtlsModel.setDueAmt(arrDtlsBean.getDueGpAmt());
            arrAqDtlsModel.setDrawnAMt(arrDtlsBean.getDrawnGpAmt());
            arrAqDtlsModel.setDrawnBillNo(arrDtlsBean.getDrawnBillNo());
            arrAqDtlsModel.setRefaqslno("0");
            arrAqDtlsModels[1] = arrAqDtlsModel;

            arrAqDtlsModel = new ArrAqDtlsModel();
            arrAqDtlsModel.setAqslno(arrDtlsBean.getAqslno());
            arrAqDtlsModel.setPayMonth(arrDtlsBean.getPayMonth());
            arrAqDtlsModel.setPayYear(arrDtlsBean.getPayYear());
            arrAqDtlsModel.setAdType("DA");
            arrAqDtlsModel.setDueAmt(arrDtlsBean.getDueDaAmt());
            arrAqDtlsModel.setDrawnAMt(arrDtlsBean.getDrawnDaAmt());
            arrAqDtlsModel.setDrawnBillNo(arrDtlsBean.getDrawnBillNo());
            arrAqDtlsModel.setRefaqslno("0");
            arrAqDtlsModels[2] = arrAqDtlsModel;
            int calcUniqueNo = arrmastDAO.getCalcUniqueNo(arrDtlsBean.getAqslno());
            arrmastDAO.saveArrdtlsdata(arrAqDtlsModels,calcUniqueNo);

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
