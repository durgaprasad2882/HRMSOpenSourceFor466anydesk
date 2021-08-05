/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.controller.payroll.billbrowser;

import hrms.dao.payroll.aqdtls.AqDtlsDAO;
import hrms.dao.payroll.billbrowser.AqReportDAO;
import hrms.model.login.LoginUserBean;
import hrms.model.payroll.billbrowser.AcquaintanceBean;
import hrms.model.payroll.billbrowser.AqDtlsDedBean;
import java.util.ArrayList;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

@Controller
@SessionAttributes("LoginUserBean")
public class AqDtlsController {

    @Autowired
    AqDtlsDAO aqDtlsDAO;
    @Autowired
    AqReportDAO aqReportDao;

    @RequestMapping(value = "getAqDtlsDedAction.htm", method = RequestMethod.GET)
    public ModelAndView getAqDtlsDedData(ModelMap model, @ModelAttribute("LoginUserBean") LoginUserBean lub, @RequestParam("aqslNo") String aqslNo, @RequestParam("adCode") String adCode, @RequestParam("billNo") String billNo, @RequestParam("nowDedn") String nowdedn) {//

        AqDtlsDedBean aqDtlsDed = new AqDtlsDedBean();
        //String tableName = aqReportDao.getAqDtlsTableName(billNo);

        aqDtlsDed = aqDtlsDAO.getAqDetailsDed(aqslNo, adCode, nowdedn);
        aqDtlsDed.setBillNo(billNo);
        aqDtlsDed.setAdCode(adCode);
        aqDtlsDed.setNowDedn(nowdedn);
        aqDtlsDed.setAqslNo(aqslNo);
        ModelAndView mv = new ModelAndView("/payroll/AqDtlsDedAmt", "command", aqDtlsDed);
        return mv;
    }

    @RequestMapping(value = "getAqDtlsAllAction.htm", method = RequestMethod.GET)
    public ModelAndView getAqDtlsAllData(ModelMap model, @ModelAttribute("LoginUserBean") LoginUserBean lub, @RequestParam("aqslNo") String aqslNo, @RequestParam("adCode") String adCode, @RequestParam("billNo") String billNo, @RequestParam("adType") String adType) {//

        AqDtlsDedBean aqDtlsDed = new AqDtlsDedBean();
        //String tableName = aqReportDao.getAqDtlsTableName(billNo);

        aqDtlsDed = aqDtlsDAO.getAqDetailsAllowance(aqslNo, adCode);
        aqDtlsDed.setBillNo(billNo);
        aqDtlsDed.setAdCode(adCode);
        aqDtlsDed.setAqslNo(aqslNo);
        aqDtlsDed.setAdType(adType);
        ModelAndView mv = new ModelAndView("/payroll/AqDtlsDedAmt", "command", aqDtlsDed);
        return mv;
    }

    @RequestMapping(value = "browserAqData.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView updateAqdtls(@ModelAttribute("AqDtlsDedBean") AqDtlsDedBean aqDtlsDedBean) {
        double gross = 0;
        double net = 0;
        double totAllowance = 0.0;
        double totDeduction = 0.0;
        aqDtlsDAO.updateAqDtlsData(aqDtlsDedBean);
        ModelAndView mv = new ModelAndView("/payroll/BrowseAquitanceData");
        String tableName = aqReportDao.getAqDtlsTableName(aqDtlsDedBean.getBillNo());
        AcquaintanceBean aqReportBean = aqReportDao.getAqMastDtl(aqDtlsDedBean.getAqslNo());
        ArrayList deductionobjList = aqReportDao.getAcquaintanceDtlDed(aqDtlsDedBean.getAqslNo(), tableName);
        ArrayList allowanceObjList = aqReportDao.getAcquaintanceDtlAll(aqDtlsDedBean.getAqslNo(), tableName);
        //totAllowance = aqReportDao.getTotalAllowance(aqDtlsDedBean.getAqslNo());
        //totDeduction = aqReportDao.getTotalDeduction(aqDtlsDedBean.getAqslNo());
        gross = totAllowance + aqReportBean.getCurbasic();
        net = gross - totDeduction;
        mv.addObject("totAll", totAllowance);
        mv.addObject("totDed", totDeduction);
        mv.addObject("gross", gross);
        mv.addObject("net", net);
        mv.addObject("aqSlNo", aqDtlsDedBean.getAqslNo());
        mv.addObject("billNo", aqDtlsDedBean.getBillNo());
        mv.addObject("aqReportBean", aqReportBean);
        mv.addObject("deductionobjList", deductionobjList);
        mv.addObject("allowanceObjList", allowanceObjList);

        return mv;
    }
}
