/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.controller.payroll.billbrowser;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import hrms.SelectOption;
import hrms.common.CalendarCommonMethods;
import hrms.dao.billvouchingTreasury.VouchingServicesDAO;
import hrms.dao.master.TreasuryDAO;
import hrms.dao.payroll.billbrowser.BillBrowserDAO;
import hrms.dao.payroll.billbrowser.BillGroupDAO;
import hrms.dao.payroll.schedule.PayBillDMPDAO;
import hrms.dao.payroll.schedule.ScheduleDAO;
import hrms.model.billvouchingTreasury.BillDetail;
import hrms.model.billvouchingTreasury.ObjectBreakup;
import hrms.model.common.CommonReportParamBean;
import hrms.model.login.LoginUserBean;
import hrms.model.payroll.BytransferDetails;
import hrms.model.payroll.GpfTpfDetails;
import hrms.model.payroll.NPSDetails;
import hrms.model.payroll.SalaryBenefitiaryDetails;
import hrms.model.payroll.billbrowser.BillAttr;
import hrms.model.payroll.billbrowser.BillBean;
import hrms.model.payroll.billbrowser.BillBrowserbean;
import hrms.model.payroll.billbrowser.GetBillStatusBean;
import hrms.model.payroll.billbrowser.GlobalBillStatus;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Manas Jena
 */
@Controller
@SessionAttributes("LoginUserBean")
public class BillBrowserController implements ServletContextAware {

    public static final String FILE_SEPARATOR = System.getProperty("file.separator");
    public static final String GLOBAL_VARIABLE_NAME = "STOP_BILL_PROCESS";

    private ServletContext servletContext;

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Autowired
    PayBillDMPDAO paybillDmpDao;

    @Autowired
    ScheduleDAO comonScheduleDao;

    @Autowired
    BillBrowserDAO billBrowserDao;

    @Autowired
    VouchingServicesDAO vouchingServicesDAOImpl;

    @Autowired
    BillGroupDAO billGroupDAO;

    @Autowired
    TreasuryDAO treasuryDao;

    @RequestMapping(value = "billBrowserAction", method = RequestMethod.GET)
    public ModelAndView billBrowserAction(ModelMap model, @ModelAttribute("BillBrowserbean") BillBrowserbean bbbean, @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response) {//       
        String path = "/payroll/BillBrowser";
        ArrayList billYears = billBrowserDao.getBillPrepareYear(lub.getOffcode());
        ModelAndView mav = new ModelAndView(path, "command", bbbean);
        mav.addObject("billYears", billYears);
        mav.setViewName(path);
        return mav;
    }

    @RequestMapping(value = "newBill", method = RequestMethod.POST)
    public ModelAndView newBill(ModelMap model, @ModelAttribute("BillBrowserbean") BillBrowserbean bbbean, @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response) {
        String path = "/payroll/BillBrowser";
        GlobalBillStatus gbs = billBrowserDao.getBillProcessStatus();
        ModelAndView mav = null;
        if (gbs.getGlobalVarValue().equalsIgnoreCase("Y")) {
            path = "/payroll/ErrorMessage";
            mav.addObject("gbs", gbs);
            mav = new ModelAndView(path, "command", bbbean);
        } else {
            int billYear = billBrowserDao.getNewBillYear(lub.getOffcode());
            int billMonth = billBrowserDao.getNewBillMonth(lub.getOffcode(), billYear);
            String monthName = billBrowserDao.getMonthName(billMonth);
            if (billMonth == 11) {
                billYear = billYear + 1;
                billMonth = 0;
            } else {
                billMonth = billMonth + 1;
            }
            bbbean.setSltMonth(billMonth);
            bbbean.setSltYear(billYear);
            path = "/payroll/PrepareBill";
            mav = new ModelAndView(path, "command", bbbean);
            SelectOption year = new SelectOption();
            year.setLabel(billYear + "");
            year.setValue(billYear + "");
            ArrayList billYears = new ArrayList();
            billYears.add(year);
            SelectOption month = new SelectOption();
            month.setValue(billMonth + "");
            month.setLabel(CalendarCommonMethods.getFullMonthAsString(billMonth));
            ArrayList billMonths = new ArrayList();
            billMonths.add(month);
            mav.addObject("billMonths", billMonths);
            mav.addObject("billYears", billYears);
            //mav.addObject("billYear", billYear);
            //mav.addObject("billMonth", billMonth);
            //mav.addObject("monthName", monthName);

        }

        mav.setViewName(path);
        return mav;
    }

    @RequestMapping(value = "newArrearBill", method = RequestMethod.POST)
    public ModelAndView newArrearBill(ModelMap model, @ModelAttribute("BillBrowserbean") BillBrowserbean bbbean, @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response) {
        String path = "/payroll/BillBrowser";
        GlobalBillStatus gbs = billBrowserDao.getBillProcessStatus();
        ModelAndView mav = null;
        if (gbs.getGlobalVarValue().equalsIgnoreCase("Y")) {
            path = "/payroll/ErrorMessage";
            mav.addObject("gbs", gbs);
            mav = new ModelAndView(path, "command", bbbean);
        } else {
            int billYear = billBrowserDao.getNewBillYear(lub.getOffcode());
            int billMonth = billBrowserDao.getNewBillMonth(lub.getOffcode(), billYear);
            String monthName = billBrowserDao.getMonthName(billMonth);
            if (billMonth == 11) {
                billYear = billYear + 1;
                billMonth = 0;
            } else {
                billMonth = billMonth + 1;
            }
            System.out.println("billMonth:" + billMonth + "  billYear:" + billYear);
            bbbean.setSltMonth(billMonth);
            bbbean.setSltYear(billYear);
            path = "/payroll/PrepareArrearBill";
            mav = new ModelAndView(path, "BillBrowserbean", bbbean);
            SelectOption year = new SelectOption();
            year.setLabel(billYear + "");
            year.setValue(billYear + "");
            ArrayList billYears = new ArrayList();
            billYears.add(year);
            SelectOption month = new SelectOption();
            month.setValue(billMonth + "");
            month.setLabel(CalendarCommonMethods.getFullMonthAsString(billMonth));
            ArrayList billMonths = new ArrayList();
            billMonths.add(month);
            mav.addObject("billMonths", billMonths);
            mav.addObject("billYears", billYears);

        }

        mav.setViewName(path);
        return mav;
    }

    @RequestMapping(value = "prepareNewBillform", method = RequestMethod.POST, params = "action=Ok")
    public ModelAndView prepareNewBillform(ModelMap model, @ModelAttribute("BillBrowserbean") BillBrowserbean bbbean, @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView("/payroll/PrepareBill", "command", bbbean);
        SelectOption year = new SelectOption();
        year.setLabel(bbbean.getSltYear() + "");
        year.setValue(bbbean.getSltYear() + "");
        ArrayList billYears = new ArrayList();
        billYears.add(year);
        SelectOption month = new SelectOption();
        month.setValue(bbbean.getSltMonth() + "");
        month.setLabel(CalendarCommonMethods.getFullMonthAsString(bbbean.getSltMonth()));
        ArrayList billMonths = new ArrayList();
        billMonths.add(month);
        mav.addObject("billGroupList", billBrowserDao.getBillGroupList(lub.getOffcode(), lub.getSpc()));
        mav.addObject("billMonths", billMonths);
        mav.addObject("billYears", billYears);
        return mav;
    }

    @RequestMapping(value = "prepareNewBillform", method = RequestMethod.POST, params = "action=Back")
    public String ReturnNewBillform(ModelMap model, @ModelAttribute("BillBrowserbean") BillBrowserbean bbbean, @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response) {
        System.out.println("");
        return "redirect:/billBrowserAction.htm";
    }

    @RequestMapping(value = "prepareNewBillform", method = RequestMethod.POST, params = "action=Process")
    public ModelAndView processNewBillform(ModelMap model, @ModelAttribute("BillBrowserbean") BillBrowserbean bbbean, @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView("/payroll/ShowBillProcessStatus", "command", bbbean);
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        if ((year == bbbean.getSltYear() + 1) || (year == bbbean.getSltYear() && bbbean.getSltMonth() <= month)) {
            int priority = billBrowserDao.getBillPriority(lub.getOffcode());
            BillAttr[] billAttr = billBrowserDao.createBillFromBillGroup(bbbean.getSltMonth(), bbbean.getSltYear(), bbbean.getBillgroupId(), bbbean.getProcessDate(), priority, bbbean.getTxtbilltype(), lub.getOffcode());
            mav.addObject("billAttr", billAttr);
            mav.addObject("msg", "Bill is Under Process. Check After 1 hour");
        } else {
            mav.addObject("msg", "Advance Month Salary Cannot Be prepared");
        }
        return mav;
    }

    @RequestMapping(value = "prepareNewArrearBillform", method = RequestMethod.POST, params = "action=Ok")
    public ModelAndView prepareNewArrearBillform(ModelMap model, @ModelAttribute("BillBrowserbean") BillBrowserbean bbbean, @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView("/payroll/PrepareArrearBill", "command", bbbean);
        SelectOption year = new SelectOption();
        year.setLabel(bbbean.getSltYear() + "");
        year.setValue(bbbean.getSltYear() + "");
        ArrayList billYears = new ArrayList();
        billYears.add(year);
        SelectOption month = new SelectOption();
        month.setValue(bbbean.getSltMonth() + "");
        month.setLabel(CalendarCommonMethods.getFullMonthAsString(bbbean.getSltMonth()));
        ArrayList billMonths = new ArrayList();
        billMonths.add(month);
        mav.addObject("billGroupList", billBrowserDao.getBillGroupList(lub.getOffcode(), lub.getSpc()));
        mav.addObject("billMonths", billMonths);
        mav.addObject("billYears", billYears);
        return mav;
    }

    @RequestMapping(value = "prepareNewArrearBillform", method = RequestMethod.POST, params = "action=Back")
    public String ReturnNewBillArrearform(ModelMap model, @ModelAttribute("BillBrowserbean") BillBrowserbean bbbean, @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response) {
        System.out.println("");
        return "redirect:/billBrowserAction.htm";
    }

    @RequestMapping(value = "prepareNewArrearBillform", method = RequestMethod.POST, params = "action=Process")
    public ModelAndView ProcessNewArrearBillform(ModelMap model, @ModelAttribute("BillBrowserbean") BillBrowserbean bbbean, @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView("/payroll/ShowBillProcessStatus", "command", bbbean);

        int priority = billBrowserDao.getBillPriority(lub.getOffcode());
        BillAttr[] billAttr = billBrowserDao.createBillFromBillGroupForArrear(bbbean.getSltFromMonth(), bbbean.getSltFromYear(), bbbean.getSltToMonth(), bbbean.getSltToYear(), bbbean.getBillgroupId(), bbbean.getProcessDate(), priority, bbbean.getTxtbilltype(), lub.getOffcode());
        mav.addObject("billAttr", billAttr);
        mav.addObject("msg", "Bill is Under Process. Check After 1 hour");

        return mav;
    }

    @RequestMapping(value = "processArrearIndividualBill", method = RequestMethod.GET)
    public ModelAndView processArrearIndividualBill(ModelMap model, @ModelAttribute("BillBrowserbean") BillBrowserbean bbbean, @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response) {
        bbbean.setOffCode(lub.getOffcode());
        if (bbbean.getTxtbilltype() != null && !bbbean.getTxtbilltype().equals("") && bbbean.getTxtbilltype().equalsIgnoreCase("ARREAR")) {
            BillBrowserbean arrearData = billBrowserDao.getArrearBillPeriod(lub.getOffcode(), bbbean.getSltMonth(), bbbean.getSltYear());
            bbbean.setSltFromMonth(arrearData.getSltFromMonth());
            bbbean.setSltFromYear(arrearData.getSltFromYear());
            bbbean.setSltToMonth(arrearData.getSltToMonth());
            bbbean.setSltToYear(arrearData.getSltToYear());

        }
        ModelAndView mav = new ModelAndView("/payroll/ReprocessArrearBill", "command", bbbean);

        return mav;
    }

    @RequestMapping(value = "payBillReportAction", method = RequestMethod.GET)
    public String payBillReportAction(ModelMap model, @ModelAttribute("BillBrowserbean") BillBrowserbean bbbean, @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response) {
        String path = "/payroll/PayBillReport";
        List reportList = comonScheduleDao.getDisplayReportList(bbbean.getBillNo(), bbbean.getTxtbilltype());

        model.addAttribute("reportList", reportList);
        model.addAttribute("bbbean", bbbean);
        return path;
    }

    @RequestMapping(value = "processIndividualBill", method = RequestMethod.GET)
    public ModelAndView processIndividualBill(ModelMap model, @ModelAttribute("BillBrowserbean") BillBrowserbean bbbean, @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response) {
        bbbean.setOffCode(lub.getOffcode());
        ModelAndView mav = new ModelAndView("/payroll/ReprocessBill", "command", bbbean);

        return mav;
    }

    @RequestMapping(value = "showUploadBillStatus", method = RequestMethod.GET)
    public ModelAndView uploadBillStatus(ModelMap model, @ModelAttribute("GetBillStatusBean") GetBillStatusBean statusBill, @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response, @RequestParam("billNo") int billNo) {

        statusBill = billBrowserDao.getUploadBillStatus(billNo);

        ModelAndView mav = new ModelAndView("/payroll/GetUploadBillStatus", "command", statusBill);
        return mav;
    }

    /*
     @RequestMapping(value = "processBillform", method = RequestMethod.POST)
     public ModelAndView processBillform(ModelMap model, @ModelAttribute("BillBrowserbean") BillBrowserbean bbbean, @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response) {//
     String path = "/payroll/ErrorMessage";
     GlobalBillStatus gbs = billBrowserDao.getBillProcessStatus();
     ModelAndView mav = null;
     if (gbs.getGlobalVarValue().equalsIgnoreCase("Y")) {
     path = "/payroll/ErrorMessage";
     mav = new ModelAndView(path, "command", bbbean);
     mav.addObject("gbs", gbs);
     }else{
            
     }
     return mav;
     }    
     */
    @ResponseBody
    @RequestMapping(value = "getBillMonthYearWise", method = RequestMethod.POST)
    public void getBillMonthYearWise(HttpServletRequest request, @ModelAttribute("BillBrowserbean") BillBrowserbean bbbean, @ModelAttribute("LoginUserBean") LoginUserBean lub, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = null;
        ArrayList billMonths = billBrowserDao.getMonthFromSelectedYear(lub.getOffcode(), bbbean.getSltYear());
        JSONArray json = new JSONArray(billMonths);
        out = response.getWriter();
        out.write(json.toString());
    }

    @RequestMapping(value = "getPayBillList")
    public ModelAndView getPayBillList(HttpServletRequest request, @ModelAttribute("BillBrowserbean") BillBrowserbean bbbean, @ModelAttribute("LoginUserBean") LoginUserBean lub, HttpServletResponse response) throws IOException {
        String path = "/payroll/BillBrowser";

        ArrayList billYears = billBrowserDao.getBillPrepareYear(lub.getOffcode());
        ArrayList billMonths = billBrowserDao.getMonthFromSelectedYear(lub.getOffcode(), bbbean.getSltYear());
        ArrayList<BillBean> billList = billBrowserDao.getPayBillList(bbbean.getSltYear(), bbbean.getSltMonth(), lub.getOffcode(), bbbean.getTxtbilltype(), lub.getSpc());
        if (bbbean.getTxtbilltype() != null && !bbbean.getTxtbilltype().equals("") && bbbean.getTxtbilltype().equalsIgnoreCase("ARREAR")) {
            BillBrowserbean arrearData = billBrowserDao.getArrearBillPeriod(lub.getOffcode(), bbbean.getSltMonth(), bbbean.getSltYear());
            bbbean.setSltFromMonth(arrearData.getSltFromMonth());
            bbbean.setSltFromYear(arrearData.getSltFromYear());
            bbbean.setSltToMonth(arrearData.getSltToMonth());
            bbbean.setSltToYear(arrearData.getSltToYear());
        }
        bbbean.setTxtbilltype(bbbean.getTxtbilltype());
        ModelAndView mav = new ModelAndView(path, "command", bbbean);
        mav.addObject("billYears", billYears);
        mav.addObject("billMonths", billMonths);
        mav.addObject("billList", billList);
        mav.setViewName(path);
        return mav;
    }

    @RequestMapping(value = "submitToIFMS", method = RequestMethod.GET)
    public ModelAndView submitToIFMS(HttpServletRequest request, @ModelAttribute("BillBrowserbean") BillBrowserbean bbbean, @ModelAttribute("LoginUserBean") LoginUserBean lub, HttpServletResponse response, @RequestParam("billNo") int billno) throws IOException {
        String path = "/payroll/BillVerificationData";

        long startTime = System.currentTimeMillis();

        String billNo = null;
        String mode = null;
        String year = null;
        String month = null;
        String pageno = null;
        String jndiStr = null;

        double allowAmt = 0;
        double dedAmt = 0;
        String officeCode = "";

        int monthasNumber = 0;
        boolean billdetailNullFound = false;
        boolean objectbreakupNullFound = false;
        boolean bytransferNullFound = false;
        boolean gpfNullFound = false;
        boolean npsNullFound = false;
        boolean headofAcct = true;
        int year2 = 0;
        double grossAmt = 0;
        double objBrkSum = 0;
        double netAmt = 0;
        double btSum = 0;
        double gpfSum8690 = 0;
        double gpfSum8692 = 0;
        double npsSum = 0;
        double gpfBTsum8690 = 0;
        double gpfBTsum8692 = 0;
        double npsBTsum = 0;

        String DEMAND_NUMBER = "";
        String MAJOR_HEAD = "";
        String SUBMAJOR_HEAD = "";
        String MINOR_HEAD = "";
        String SUB_HEAD = "";
        String DETAIL_HEAD = "";
        String PLAN_STATUS = "";
        String CHARGED_VOTED = "";
        String SECTOR_CODE = "";

        String periodTo = "";
        String periodFrom = "";
        Calendar cal = Calendar.getInstance();
        String treasuryCode = "";
        int restrictDate = 0;
        int closingDate = 0;
        int closingMonth = 0;
        ModelAndView mav = null;

        BillDetail bill = new BillDetail();

        try {

            List errorList = new ArrayList();

            bill = billBrowserDao.getBillDetails(billno);

            bbbean.setBillNo(billno + "");
            if (bill != null) {
                //grossAmt=vserv.getGrossAmt(con,billNo);
                //netAmt= grossAmt-vserv.getdeductAmt(con,billNo);
                DEMAND_NUMBER = bill.getDemandNumber();
                MAJOR_HEAD = bill.getMajorHead();
                SUBMAJOR_HEAD = bill.getSubMajorHead();
                MINOR_HEAD = bill.getMinorHead();
                SUB_HEAD = bill.getSubHead();
                DETAIL_HEAD = bill.getDetailHead();
                PLAN_STATUS = bill.getPlanStatus();
                CHARGED_VOTED = bill.getChargedVoted();
                SECTOR_CODE = bill.getSectorCode();
                if (bill.getBillmonth() >= 0) {
                    monthasNumber = bill.getBillmonth() + 1;
                    month = CalendarCommonMethods.getFullNameMonthAsString((bill.getBillmonth()));

                    cal.set(Calendar.YEAR, bill.getBillyear());
                    cal.set(Calendar.MONTH, bill.getBillmonth());
                    cal.set(Calendar.DATE, 1);
                }
                SimpleDateFormat date_format = new SimpleDateFormat("dd-MMM-yyyy");
                periodFrom = date_format.format(cal.getTime());
                cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                periodTo = date_format.format(cal.getTime());
                treasuryCode = bill.getTreasuryCode();
                if (StringUtils.isEmpty(bill.getBillnumber())) {
                    billdetailNullFound = true;
                    errorList.add("Bill Number cannot be Empty");
                }
                if (StringUtils.isEmpty(bill.getBillDate())) {
                    billdetailNullFound = true;
                    errorList.add("Bill Date cannot be Empty");
                }
                if (StringUtils.isEmpty(bill.getBilldesc())) {
                    billdetailNullFound = true;
                    errorList.add("Bill Description cannot be Empty");
                }
                if (StringUtils.isEmpty(bill.getBillType())) {
                    billdetailNullFound = true;
                    errorList.add("Bill Type cannot be Empty");
                }

                monthasNumber = bill.getBillmonth() + 1;

                if (StringUtils.isEmpty(bill.getDemandNumber())) {
                    billdetailNullFound = true;
                    errorList.add("Demand Number cannot be Empty");
                }
                if (StringUtils.isEmpty(bill.getMajorHead())) {
                    billdetailNullFound = true;
                    errorList.add("Bill Major Head cannot be Empty");
                }
                if (StringUtils.isEmpty(bill.getSubMajorHead())) {
                    billdetailNullFound = true;
                    errorList.add("Bill Sub Major Head cannot be Empty");
                }
                if (StringUtils.isEmpty(bill.getMinorHead())) {
                    billdetailNullFound = true;
                    errorList.add("Bill Minor Head cannot be Empty");
                }
                if (StringUtils.isEmpty(bill.getSubHead())) {
                    billdetailNullFound = true;
                    errorList.add("Bill Sub Head cannot be Empty");
                }
                if (StringUtils.isEmpty(bill.getDetailHead())) {
                    billdetailNullFound = true;
                    errorList.add("Bill Detail Head cannot be Empty");
                }
                if (StringUtils.isEmpty(bill.getPlanStatus())) {
                    billdetailNullFound = true;
                    errorList.add("Plan cannot be Empty");
                }
                if (StringUtils.isEmpty(bill.getChargedVoted())) {
                    billdetailNullFound = true;
                    errorList.add("Charge/Voted cannot be Empty");
                }
                if (StringUtils.isEmpty(bill.getSectorCode())) {
                    billdetailNullFound = true;
                    errorList.add("Sector code cannot be Empty");
                }
                if (StringUtils.isEmpty(bill.getTreasuryCode())) {
                    billdetailNullFound = true;
                    errorList.add("Treasury Code cannot be Empty");
                }
                if (StringUtils.isEmpty(bill.getBeneficiaryrefno())) {
                    billdetailNullFound = true;
                    errorList.add("Beneficiary Refernce Number cannot be Empty");
                }
            }

            List objlist = billBrowserDao.getOBJXMLData(billno, bill.getTreasuryCode(), 0, bill.getBillDate(), bill.getTypeofBillString());

            //List objlist=billFunc.getOBJXMLData(con, billNo,treasuryCode, basicPay,billdate);        
            Iterator objItrList = objlist.iterator();
            ObjectBreakup object = null;
            while (objItrList.hasNext()) {
                object = (ObjectBreakup) objItrList.next();

                objBrkSum = objBrkSum + object.getObjectHeadwiseAmount();

                if (StringUtils.isEmpty(object.getObjectHead())) {
                    objectbreakupNullFound = true;
                    errorList.add("Object Head Found blank.");
                }
                if (StringUtils.isEmpty(object.getObjectHeadwiseAmount() + "")) {
                    objectbreakupNullFound = true;
                    errorList.add("Object Head Amount Found blank.");
                }
            }

            // (billno, bill.getTreasuryCode(), 0, bill.getBillDate(), bill.getTypeofBillString())
            List bytranlist = billBrowserDao.getBTXMLData(billno, bill.getTreasuryCode(), bill.getBillDate(), bill.getTypeofBillString());
            Iterator byItrList = bytranlist.iterator();
            BytransferDetails bytransfer = null;
            while (byItrList.hasNext()) {
                bytransfer = (BytransferDetails) byItrList.next();
                btSum = btSum + bytransfer.getAmount();

                if (StringUtils.isEmpty(bytransfer.getBtserialno())) {
                    bytransferNullFound = true;
                } else {
                    if (bytransfer.getBtserialno().equals("55545")) {
                        gpfBTsum8690 = gpfBTsum8690 + bytransfer.getAmount();
                    } else if (bytransfer.getBtserialno().equals("57649")) {
                        gpfBTsum8692 = gpfBTsum8692 + bytransfer.getAmount();
                    } else if (bytransfer.getBtserialno().equals("9871") || bytransfer.getBtserialno().equals("32149")) {
                        npsBTsum = npsBTsum + bytransfer.getAmount();
                    }

                }
                if (StringUtils.isEmpty(bytransfer.getAmount() + "")) {
                    bytransferNullFound = true;
                }
            }

            List npslist = billBrowserDao.getNPSXMLData(billno, bill.getBillDate(), bill.getBillmonth(), bill.getBillyear(), bill.getTypeofBillString());

            Iterator itrListNPS = npslist.iterator();
            NPSDetails nps = null;

            while (itrListNPS.hasNext()) {
                nps = (NPSDetails) itrListNPS.next();
                if (StringUtils.isEmpty(nps.getDdoRegno())) {
                    npsNullFound = true;
                }
                if (StringUtils.isEmpty(nps.getHrmsgeneratedRefno() + "")) {
                    npsNullFound = true;
                }
                if (StringUtils.isEmpty(nps.getBasic() + "")) {
                    npsNullFound = true;
                }
                if (StringUtils.isEmpty(nps.getNameofSubscrib())) {
                    npsNullFound = true;
                }
                if (StringUtils.isEmpty(nps.getPran())) {
                    npsNullFound = true;
                }

                if (StringUtils.isEmpty(nps.getSc())) {
                    npsNullFound = true;

                } else {
                    npsSum = npsSum + Double.parseDouble(nps.getSc());
                }

            }

            CommonReportParamBean crb = paybillDmpDao.getCommonReportParameter(bbbean.getBillNo());
            bbbean.setSltMonth(crb.getAqmonth());
            bbbean.setSltYear(crb.getAqyear());
            bbbean.setTxtbilltype(crb.getTypeofBill());

            BillDetail billDtls = billBrowserDao.getBillDetails(Integer.parseInt(bbbean.getBillNo()));
            bbbean.setBilldesc(billDtls.getBillnumber());
            bbbean.setBenificiaryNumber(billDtls.getBeneficiaryrefno());
            bbbean.setVchDt(billDtls.getVchDt());
            bbbean.setVchNo(billDtls.getVchNo());
            bbbean.setBillDate(billDtls.getBillDate());
            bbbean.setTreasury(billDtls.getTreasuryCode());
            mav = new ModelAndView(path, "command", bbbean);

            bbbean.setStatus(billDtls.getBillStatusId());

            List treasuryList = treasuryDao.getTreasuryList();
            mav.addObject("allowanceList", billBrowserDao.getAllowanceList(Integer.parseInt(bbbean.getBillNo())));
            mav.addObject("deductionList", billBrowserDao.getDeductionList(Integer.parseInt(bbbean.getBillNo())));
            mav.addObject("pvtloanList", billBrowserDao.getPvtLoanList(Integer.parseInt(bbbean.getBillNo())));
            mav.addObject("treasuryList", treasuryList);
            mav.addObject("billDtls", billDtls);
            mav.addObject("errorList", errorList);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

        return mav;
    }

    @RequestMapping(value = "saveBill", params = "action=Upload", method = RequestMethod.POST)
    public ModelAndView upload(HttpServletRequest request, @ModelAttribute("BillBrowserbean") BillBrowserbean bbbean,
            @ModelAttribute("LoginUserBean") LoginUserBean lub, HttpServletResponse response) {
        String billDetailFile = "BILL_DETAILS";
        String folderPath = servletContext.getInitParameter("payBillXMLDOC");
        Calendar cal = Calendar.getInstance();
        BillDetail bill = new BillDetail();

        String path = "redirect:/getPayBillList.htm";
        ModelAndView mav = null;
        try {

            BillDetail billDtls = billBrowserDao.getBillDetails(Integer.parseInt(bbbean.getBillNo()));
            bbbean.setSltYear(billDtls.getBillyear());
            //bbbean.setSltMonth(billDtls.getBillmonth());
            //bbbean.setTxtbilltype(billDtls.getBillType());

            mav = new ModelAndView(path, "command", bbbean);

            DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
            String submissionDate = dateFormat.format(cal.getTime());
            int billno = Integer.parseInt(bbbean.getBillNo());

            boolean upverify = true;
            if (upverify) {
                bill = billBrowserDao.getBillDetails(billno);

                billDetailFile = billDetailFile + "_" + bill.getDdoccode() + "_" + bill.getBillmonth() + bill.getBillyear() + ".xml";
                createXMLBillDetails(folderPath + BillBrowserController.FILE_SEPARATOR + bill.getHrmsgeneratedRefno(), billDetailFile, bill);

                /*
                 * Object Breakup data is collected here
                 * */
                ArrayList objlist = billBrowserDao.getOBJXMLData(billno, bill.getTreasuryCode(), 0, bill.getBillDate(), bill.getTypeofBillString());
                billDetailFile = "OBJ_BREAKUP" + "_" + bill.getDdoccode() + "_" + bill.getBillmonth() + bill.getBillyear() + ".xml";
                createXMLObjectBreakUp(folderPath + BillBrowserController.FILE_SEPARATOR + bill.getHrmsgeneratedRefno(), billDetailFile, objlist);

                /*
                 * Bytransfer Data is collected here
                 * */
                ArrayList bytranlist = billBrowserDao.getBTXMLData(billno, bill.getTreasuryCode(), bill.getBillDate(), bill.getTypeofBillString());
                billDetailFile = "BT_DETAILS" + "_" + bill.getDdoccode() + "_" + bill.getBillmonth() + bill.getBillyear() + ".xml";
                createXMLBT(folderPath + BillBrowserController.FILE_SEPARATOR + bill.getHrmsgeneratedRefno(), billDetailFile, bytranlist);

                /*
                 * Data for NPS
                 * */
                ArrayList npslist = billBrowserDao.getNPSXMLData(billno, bill.getBillDate(), bill.getBillmonth(), bill.getBillyear(), bill.getTypeofBillString());
                billDetailFile = "NPS_DETAILS" + "_" + bill.getDdoccode() + "_" + bill.getBillmonth() + bill.getBillyear() + ".xml";
                createXMLCPF(folderPath + BillBrowserController.FILE_SEPARATOR + billno, billDetailFile, npslist);

                /*
                 * Zip file is created and uploaded to server
                 * */
                int count = 0;
                count = billBrowserDao.getbillsubmissionCount(billno);
                String zipFilename = "";
                if (count > 0) {
                    zipFilename = bill.getDdoccode() + "-" + CalendarCommonMethods.getFullMonthAsString(bill.getBillmonth() - 1) + "-" + bill.getBillyear() + "-" + billno + "_" + count + ".zip";
                } else {
                    zipFilename = bill.getDdoccode() + "-" + CalendarCommonMethods.getFullMonthAsString(bill.getBillmonth() - 1) + "-" + bill.getBillyear() + "-" + billno + ".zip";

                }

                File directory = new File(folderPath + BillBrowserController.FILE_SEPARATOR + billno);
                String[] files = directory.list();
                byte[] zip = zipFiles(directory, files);
                FileOutputStream fout = new FileOutputStream(new File(folderPath + BillBrowserController.FILE_SEPARATOR + zipFilename));
                fout.write(zip);
                fout.close();
                fout.flush();
                //ftpUpload(zip, zipFilename);
                billBrowserDao.updateBillStatus(billno, 3);
                billBrowserDao.updateBillHistory(billno, submissionDate);

            }

            mav.addObject("sltYear", bbbean.getSltYear());
            mav.addObject("sltMonth", bbbean.getSltMonth());
            mav.addObject("txtbilltype", bbbean.getTxtbilltype());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mav;
    }

    @RequestMapping(value = "saveBill", params = "action=Download", method = RequestMethod.POST)
    public ModelAndView downloadXML(HttpServletRequest request, @ModelAttribute("BillBrowserbean") BillBrowserbean bbbean,
            @ModelAttribute("LoginUserBean") LoginUserBean lub, HttpServletResponse response) {
        String billDetailFile = "BILL_DETAILS";
        String folderPath = servletContext.getInitParameter("payBillXMLDOC");
        Calendar cal = Calendar.getInstance();
        BillDetail bill = new BillDetail();

        String path = "redirect:/getPayBillList.htm";
        ModelAndView mav = null;
        try {

            BillDetail billDtls = billBrowserDao.getBillDetails(Integer.parseInt(bbbean.getBillNo()));
            bbbean.setSltYear(billDtls.getBillyear());
            //bbbean.setSltMonth(billDtls.getBillmonth());
            //bbbean.setTxtbilltype(billDtls.getBillType());

            mav = new ModelAndView(path, "command", bbbean);

            DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
            String submissionDate = dateFormat.format(cal.getTime());
            int billno = Integer.parseInt(bbbean.getBillNo());

            boolean upverify = true;
            if (upverify) {
                bill = billBrowserDao.getBillDetails(billno);

                billDetailFile = billDetailFile + "_" + bill.getDdoccode() + "_" + bill.getBillmonth() + bill.getBillyear() + ".xml";
                createXMLBillDetails(folderPath + BillBrowserController.FILE_SEPARATOR + bill.getHrmsgeneratedRefno(), billDetailFile, bill);

                /*
                 * Object Breakup data is collected here
                 * */
                ArrayList objlist = billBrowserDao.getOBJXMLData(billno, bill.getTreasuryCode(), 0, bill.getBillDate(), bill.getTypeofBillString());
                billDetailFile = "OBJ_BREAKUP" + "_" + bill.getDdoccode() + "_" + bill.getBillmonth() + bill.getBillyear() + ".xml";
                createXMLObjectBreakUp(folderPath + BillBrowserController.FILE_SEPARATOR + bill.getHrmsgeneratedRefno(), billDetailFile, objlist);

                /*
                 * Bytransfer Data is collected here
                 * */
                ArrayList bytranlist = billBrowserDao.getBTXMLData(billno, bill.getTreasuryCode(), bill.getBillDate(), bill.getTypeofBillString());
                billDetailFile = "BT_DETAILS" + "_" + bill.getDdoccode() + "_" + bill.getBillmonth() + bill.getBillyear() + ".xml";
                createXMLBT(folderPath + BillBrowserController.FILE_SEPARATOR + bill.getHrmsgeneratedRefno(), billDetailFile, bytranlist);

                /*
                 * Data for NPS
                 * */
                ArrayList npslist = billBrowserDao.getNPSXMLData(billno, bill.getBillDate(), bill.getBillmonth(), bill.getBillyear(), bill.getTypeofBillString());
                billDetailFile = "NPS_DETAILS" + "_" + bill.getDdoccode() + "_" + bill.getBillmonth() + bill.getBillyear() + ".xml";
                createXMLCPF(folderPath + BillBrowserController.FILE_SEPARATOR + billno, billDetailFile, npslist);

                /*
                 * Zip file is created and uploaded to server
                 * */
                int count = 0;
                count = billBrowserDao.getbillsubmissionCount(billno);
                String zipFilename = "";
                if (count > 0) {
                    zipFilename = bill.getDdoccode() + "-" + bill.getBillmonth() + "-" + bill.getBillyear() + "-" + billno + "_" + count + ".zip";
                } else {
                    zipFilename = bill.getDdoccode() + "-" + bill.getBillmonth() + "-" + bill.getBillyear() + "-" + billno + ".zip";

                }

                File directory = new File(folderPath + BillBrowserController.FILE_SEPARATOR + billno);
                String[] files = directory.list();
                byte[] zip = zipFiles(directory, files);
                OutputStream out = response.getOutputStream();
                response.setContentType("application/zip");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + zipFilename + "\"");
                out.write(zip);
                out.flush();
                //ftpUpload(zip, zipFilename);
                //billBrowserDao.updateBillStatus(billno, 3);
                //billBrowserDao.updateBillHistory(billno, submissionDate);

            }

            mav.addObject("sltYear", bbbean.getSltYear());
            mav.addObject("sltMonth", bbbean.getSltMonth());
            mav.addObject("txtbilltype", bbbean.getTxtbilltype());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mav;
    }

    private byte[] zipFiles(File directory, String[] files) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        byte bytes[] = new byte[2048];
        System.out.println("files.length" + files.length);
        for (int i = 0; i < files.length; i++) {
            String fileName = files[i];
            FileInputStream fis = new FileInputStream(directory.getPath() + BillBrowserController.FILE_SEPARATOR + fileName);
            BufferedInputStream bis = new BufferedInputStream(fis);
            zos.putNextEntry(new ZipEntry(fileName));
            int bytesRead;
            while ((bytesRead = bis.read(bytes)) != -1) {
                zos.write(bytes, 0, bytesRead);
            }
            zos.closeEntry();
            bis.close();
            fis.close();
        }
        zos.flush();
        baos.flush();
        zos.close();
        baos.close();
        return baos.toByteArray();
    }

    public void ftpUpload(byte[] file, String fileName) {
        String SFTPHOST = "bpel.hrmsorissa.gov.in";
        int SFTPPORT = 22;
        String SFTPUSER = "root";
        String SFTPPASS = "cmgi#2012#";
        String SFTPWORKINGDIR = "/home/paybillxml";
        /*String SFTPHOST = "117.240.239.72";
         int    SFTPPORT = 21;
         String SFTPUSER = "hrms";
         String SFTPPASS = "hrmsiotms@321";
         String SFTPWORKINGDIR = "/shared/hrms/upload/"; */
        Session session = null;
        Channel channel = null;
        ChannelSftp channelSftp = null;
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
            session.setPassword(SFTPPASS);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            channel = session.openChannel("sftp");
            channel.connect();
            channelSftp = (ChannelSftp) channel;
            channelSftp.cd(SFTPWORKINGDIR);
            channelSftp.put(new ByteArrayInputStream(file), fileName);
            channelSftp.disconnect();
            session.disconnect();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void createXMLBillDetails(String folderPath, String filename, BillDetail bill) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("BILL_DETAILS");
            doc.appendChild(rootElement);
            // ROW elements
            Element row = doc.createElement("ROW");
            rootElement.appendChild(row);
            // firstname elements
            Element element = doc.createElement("HRMS_NO");
            element.appendChild(doc.createTextNode(bill.getHrmsgeneratedRefno()));
            row.appendChild(element);
            element = doc.createElement("HRMS_DATE");
            element.appendChild(doc.createTextNode(bill.getHrmsgeneratedRefdate()));
            row.appendChild(element);
            element = doc.createElement("BILL_TYPE");
            element.appendChild(doc.createTextNode(bill.getBillType()));
            row.appendChild(element);
            element = doc.createElement("BILL_NUMBER");
            element.appendChild(doc.createTextNode(bill.getBillnumber()));
            row.appendChild(element);
            element = doc.createElement("BILL_DATE");
            element.appendChild(doc.createTextNode(bill.getBillDate()));
            row.appendChild(element);
            element = doc.createElement("AG_BILL_TYPE_ID");
            element.appendChild(doc.createTextNode(bill.getAgbillTypeId()));
            row.appendChild(element);
            element = doc.createElement("SAL_FROM_DATE");
            element.appendChild(doc.createTextNode(bill.getSalFromdate()));
            row.appendChild(element);
            element = doc.createElement("SAL_TO_DATE");
            element.appendChild(doc.createTextNode(bill.getSalTodate()));
            row.appendChild(element);
            element = doc.createElement("DDO_CODE");
            element.appendChild(doc.createTextNode(bill.getDdoccode()));
            row.appendChild(element);
            element = doc.createElement("DEMAND_NUMBER");
            element.appendChild(doc.createTextNode(bill.getDemandNumber()));
            row.appendChild(element);
            element = doc.createElement("MAJOR_HEAD");
            element.appendChild(doc.createTextNode(bill.getMajorHead()));
            row.appendChild(element);
            element = doc.createElement("SUB_MAJOR_HEAD");
            element.appendChild(doc.createTextNode(bill.getSubMajorHead()));
            row.appendChild(element);
            element = doc.createElement("MINOR_HEAD");
            element.appendChild(doc.createTextNode(bill.getMinorHead()));
            row.appendChild(element);
            element = doc.createElement("SUB_HEAD");
            element.appendChild(doc.createTextNode(bill.getSubHead()));
            row.appendChild(element);
            element = doc.createElement("DETAILS_HEAD");
            element.appendChild(doc.createTextNode(bill.getDetailHead()));
            row.appendChild(element);
            element = doc.createElement("PLAN_STATUS");
            element.appendChild(doc.createTextNode(bill.getPlanStatus()));
            row.appendChild(element);
            element = doc.createElement("CHARGED_VOTED");
            element.appendChild(doc.createTextNode(bill.getChargedVoted()));
            row.appendChild(element);
            element = doc.createElement("SECTOR_CODE");
            element.appendChild(doc.createTextNode(bill.getSectorCode()));
            row.appendChild(element);
            element = doc.createElement("GROSS_AMOUNT");
            element.appendChild(doc.createTextNode(bill.getGrossAmount() + ""));
            row.appendChild(element);
            element = doc.createElement("NET_AMOUNT");
            element.appendChild(doc.createTextNode(bill.getNetAmount() + ""));
            row.appendChild(element);
            element = doc.createElement("PREVIOUS_TOKEN_NUMBER");
            element.appendChild(doc.createTextNode(bill.getPrevTokenNumber()));
            row.appendChild(element);
            element = doc.createElement("PREVIOUS_TOKEN_DATE");
            element.appendChild(doc.createTextNode(bill.getPrevTokendate()));
            row.appendChild(element);
            element = doc.createElement("TREASURY_CODE");
            element.appendChild(doc.createTextNode(bill.getTreasuryCode()));
            row.appendChild(element);
            element = doc.createElement("MOBILE_NO");
            element.appendChild(doc.createTextNode(bill.getDdomobileno()));
            row.appendChild(element);
            element = doc.createElement("BEN_REF_NO");
            element.appendChild(doc.createTextNode(StringUtils.defaultString(bill.getBeneficiaryrefno())));
            row.appendChild(element);
            // write the content into xml file
            boolean success = new File(folderPath).mkdirs();

            File f = new File(folderPath, filename);
            if (f.exists()) {
                f.delete();
                f.createNewFile();
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(f);
            transformer.transform(source, result);
            if (result.getOutputStream() != null) {
                result.getOutputStream().close();
            }

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        } catch (IOException iox) {
            iox.printStackTrace();
        }
    }

    public void createXMLObjectBreakUp(String folderPath, String filename, ArrayList objlist) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            // root elements
            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("OBJ_BREAKUP");
            doc.appendChild(rootElement);
            for (int i = 0; i < objlist.size(); i++) {
                ObjectBreakup object = (ObjectBreakup) objlist.get(i);
                Element row = doc.createElement("ROW");
                Element element = doc.createElement("HRMS_NO");
                element.appendChild(doc.createTextNode(object.getHrmsgeneratedRefno() + ""));
                row.appendChild(element);
                element = doc.createElement("HRMS_DATE");
                element.appendChild(doc.createTextNode(object.getHrmsgeneratedRefdate()));
                row.appendChild(element);
                element = doc.createElement("OBJECT_HEAD");
                element.appendChild(doc.createTextNode(object.getObjectHead()));
                row.appendChild(element);
                element = doc.createElement("AMOUNT");
                element.appendChild(doc.createTextNode(object.getObjectHeadwiseAmount() + ""));
                row.appendChild(element);
                element = doc.createElement("TREASURY_CODE");
                element.appendChild(doc.createTextNode(object.getTreasuryCode()));
                row.appendChild(element);

                rootElement.appendChild(row);
            }
            // write the content into xml file
            boolean success = new File(folderPath).mkdirs();

            File f = new File(folderPath, filename);
            if (f.exists()) {
                f.delete();
                f.createNewFile();
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(f);
            transformer.transform(source, result);
            if (result.getOutputStream() != null) {
                result.getOutputStream().close();
            }
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        } catch (IOException iox) {
            iox.printStackTrace();
        }
    }

    public void createXMLTPF(String folderPath, String filename, ArrayList objlist) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("TPF_DETAILS");
            doc.appendChild(rootElement);
            for (int i = 0; i < objlist.size(); i++) {
                GpfTpfDetails object = (GpfTpfDetails) objlist.get(i);
                Element row = doc.createElement("ROW");
                Element element = doc.createElement("HRMS_NO");
                element.appendChild(doc.createTextNode(object.getHrmsgeneratedRefno()));
                row.appendChild(element);
                element = doc.createElement("HRMS_DATE");
                element.appendChild(doc.createTextNode(object.getHrmsgeneratedRefdate()));
                row.appendChild(element);
                element = doc.createElement("BT_SERIAL_NUMBER");
                element.appendChild(doc.createTextNode(object.getBtserialno()));
                row.appendChild(element);
                element = doc.createElement("GPF_SERIES");
                element.appendChild(doc.createTextNode(object.getGpfSeries()));
                row.appendChild(element);
                element = doc.createElement("GPF_NUMBER");
                element.appendChild(doc.createTextNode(object.getGpfnumber()));
                row.appendChild(element);
                element = doc.createElement("SUBSCRIBER_NAME");
                element.appendChild(doc.createTextNode(object.getSubscribName()));
                row.appendChild(element);
                element = doc.createElement("DESIGNATION");
                element.appendChild(doc.createTextNode(object.getDesig()));
                row.appendChild(element);
                element = doc.createElement("DOB");
                element.appendChild(doc.createTextNode(object.getDob()));
                row.appendChild(element);
                element = doc.createElement("DOS");
                element.appendChild(doc.createTextNode(object.getDos()));
                row.appendChild(element);
                element = doc.createElement("SUBSCRIPTION");
                element.appendChild(doc.createTextNode(object.getMonthlySubscrip()));
                row.appendChild(element);
                element = doc.createElement("PERIOD_FROM");
                element.appendChild(doc.createTextNode(object.getPeriodFrom()));
                row.appendChild(element);
                element = doc.createElement("PERIOD_TO");
                element.appendChild(doc.createTextNode(object.getPeriodTo()));
                row.appendChild(element);
                element = doc.createElement("REFUND_OF_WITHDRAWL");
                element.appendChild(doc.createTextNode(object.getRefundWithdrawl()));
                row.appendChild(element);
                element = doc.createElement("INSTAL_NUMBER");
                element.appendChild(doc.createTextNode(object.getInstNumber()));
                row.appendChild(element);
                element = doc.createElement("OTH_DEPOSITS");
                element.appendChild(doc.createTextNode(object.getOtherDeposit() + ""));
                row.appendChild(element);
                element = doc.createElement("TOTAL_REALISED");
                element.appendChild(doc.createTextNode(object.getTotRealised() + ""));
                row.appendChild(element);
                element = doc.createElement("REMARKS");
                element.appendChild(doc.createTextNode(object.getRemarks()));
                row.appendChild(element);

                rootElement.appendChild(row);
            }
            // write the content into xml file
            boolean success = new File(folderPath).mkdirs();

            File f = new File(folderPath, filename);
            if (f.exists()) {
                f.delete();
                f.createNewFile();
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(f);
            transformer.transform(source, result);
            if (result.getOutputStream() != null) {
                result.getOutputStream().close();
            }
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        } catch (IOException iox) {
            iox.printStackTrace();
        }
    }

    public void createXMLCPF(String folderPath, String filename, ArrayList objlist) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("NPS_DETAILS");
            doc.appendChild(rootElement);
            for (int i = 0; i < objlist.size(); i++) {
                NPSDetails object = (NPSDetails) objlist.get(i);
                Element row = doc.createElement("ROW");
                Element element = doc.createElement("HRMS_NO");
                element.appendChild(doc.createTextNode(object.getHrmsgeneratedRefno() + ""));
                row.appendChild(element);
                element = doc.createElement("HRMS_DATE");
                element.appendChild(doc.createTextNode(object.getHrmsgeneratedRefdate()));
                row.appendChild(element);
                element = doc.createElement("BT_SERIAL_NUMBER");
                element.appendChild(doc.createTextNode(object.getBtserialno()));
                row.appendChild(element);
                element = doc.createElement("DDO_REG_NUMBER");
                element.appendChild(doc.createTextNode(object.getDdoRegno()));
                row.appendChild(element);
                element = doc.createElement("PRAN");
                element.appendChild(doc.createTextNode(object.getPran()));
                row.appendChild(element);
                element = doc.createElement("SUBSCRIBER_NAME");
                element.appendChild(doc.createTextNode(object.getNameofSubscrib()));
                row.appendChild(element);
                element = doc.createElement("BASIC_GP");
                element.appendChild(doc.createTextNode((object.getGp() + object.getBasic() + object.getPpay()) + ""));
                row.appendChild(element);
                element = doc.createElement("DA");
                element.appendChild(doc.createTextNode(object.getDa() + ""));
                row.appendChild(element);
                element = doc.createElement("SC");
                element.appendChild(doc.createTextNode(object.getSc() + ""));
                row.appendChild(element);
                element = doc.createElement("GC");
                element.appendChild(doc.createTextNode(object.getGc()));
                row.appendChild(element);
                element = doc.createElement("INSTALLMENT");
                element.appendChild(doc.createTextNode(object.getInstAmt() + ""));
                row.appendChild(element);
                element = doc.createElement("PAY_MONTH");
                element.appendChild(doc.createTextNode(object.getPaymonth()));
                row.appendChild(element);
                element = doc.createElement("PAY_YEAR");
                element.appendChild(doc.createTextNode(object.getPayYear()));
                row.appendChild(element);
                element = doc.createElement("CONTRIBUTION_TYPE");
                element.appendChild(doc.createTextNode(object.getContType()));
                row.appendChild(element);
                element = doc.createElement("REMARKS");
                element.appendChild(doc.createTextNode(object.getRemarks()));
                row.appendChild(element);

                rootElement.appendChild(row);
            }
            // write the content into xml file
            boolean success = new File(folderPath).mkdirs();

            File f = new File(folderPath, filename);
            if (f.exists()) {
                f.delete();
                f.createNewFile();
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(f);
            transformer.transform(source, result);
            if (result.getOutputStream() != null) {
                result.getOutputStream().close();
            }
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        } catch (IOException iox) {
            iox.printStackTrace();
        }
    }

    public void createXMLGPF(String folderPath, String filename, ArrayList objlist) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("GPF_DETAILS");
            doc.appendChild(rootElement);
            for (int i = 0; i < objlist.size(); i++) {
                GpfTpfDetails object = (GpfTpfDetails) objlist.get(i);
                Element row = doc.createElement("ROW");
                Element element = doc.createElement("HRMS_NO");
                element.appendChild(doc.createTextNode(object.getHrmsgeneratedRefno()));
                row.appendChild(element);
                element = doc.createElement("HRMS_DATE");
                element.appendChild(doc.createTextNode(object.getHrmsgeneratedRefdate()));
                row.appendChild(element);
                element = doc.createElement("BT_SERIAL_NUMBER");
                element.appendChild(doc.createTextNode(object.getBtserialno()));
                row.appendChild(element);
                element = doc.createElement("GPF_SERIES");
                element.appendChild(doc.createTextNode(object.getGpfSeries()));
                row.appendChild(element);
                element = doc.createElement("GPF_NUMBER");
                element.appendChild(doc.createTextNode(object.getGpfnumber()));
                row.appendChild(element);
                element = doc.createElement("SUBSCRIBER_NAME");
                element.appendChild(doc.createTextNode(object.getSubscribName()));
                row.appendChild(element);
                element = doc.createElement("DESIGNATION");
                element.appendChild(doc.createTextNode(object.getDesig()));
                row.appendChild(element);
                element = doc.createElement("DOB");
                element.appendChild(doc.createTextNode(object.getDob()));
                row.appendChild(element);
                element = doc.createElement("DOS");
                element.appendChild(doc.createTextNode(object.getDos()));
                row.appendChild(element);
                element = doc.createElement("SUBSCRIPTION");
                element.appendChild(doc.createTextNode(object.getMonthlySubscrip()));
                row.appendChild(element);
                element = doc.createElement("PERIOD_FROM");
                element.appendChild(doc.createTextNode(object.getPeriodFrom()));
                row.appendChild(element);
                element = doc.createElement("PERIOD_TO");
                element.appendChild(doc.createTextNode(object.getPeriodTo()));
                row.appendChild(element);
                element = doc.createElement("REFUND_OF_WITHDRAWL");
                element.appendChild(doc.createTextNode(object.getRefundWithdrawl()));
                row.appendChild(element);
                element = doc.createElement("INSTAL_NUMBER");
                element.appendChild(doc.createTextNode(object.getInstNumber()));
                row.appendChild(element);
                element = doc.createElement("OTH_DEPOSITS");
                element.appendChild(doc.createTextNode(object.getOtherDeposit() + ""));
                row.appendChild(element);
                element = doc.createElement("TOTAL_REALISED");
                element.appendChild(doc.createTextNode(object.getTotRealised() + ""));
                row.appendChild(element);
                element = doc.createElement("REMARKS");
                element.appendChild(doc.createTextNode(object.getRemarks()));
                row.appendChild(element);

                rootElement.appendChild(row);
            }
            // write the content into xml file
            boolean success = new File(folderPath).mkdirs();

            File f = new File(folderPath, filename);
            if (f.exists()) {
                f.delete();
                f.createNewFile();
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(f);
            transformer.transform(source, result);
            if (result.getOutputStream() != null) {
                result.getOutputStream().close();
            }
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        } catch (IOException iox) {
            iox.printStackTrace();
        }
    }
    /*
     * Function to create Salary Benefitiary Detail XML Document
     * */

    public void createXMLBeneficiary(String folderPath, String filename, ArrayList objlist) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("BENEFICIARY_DETAILS");
            doc.appendChild(rootElement);
            for (int i = 0; i < objlist.size(); i++) {
                SalaryBenefitiaryDetails object = (SalaryBenefitiaryDetails) objlist.get(i);
                Element row = doc.createElement("ROW");
                Element element = doc.createElement("HRMS_NO");
                element.appendChild(doc.createTextNode(object.getHrmsgeneratedRefno()));
                row.appendChild(element);
                element = doc.createElement("HRMS_DATE");
                element.appendChild(doc.createTextNode(object.getHrmsgeneratedRefdate()));
                row.appendChild(element);
                element = doc.createElement("BENEFICIARY_NAME");
                element.appendChild(doc.createTextNode(object.getBenefitiaryName()));
                row.appendChild(element);
                element = doc.createElement("BENEFICIARY_ADDRESS");
                element.appendChild(doc.createTextNode(object.getBenefitiaryAdd()));
                row.appendChild(element);
                element = doc.createElement("BENEFICIARY_ACCTNO");
                element.appendChild(doc.createTextNode(object.getBenefitiaryAcct()));
                row.appendChild(element);
                element = doc.createElement("BANK_IFSC_CODE");
                element.appendChild(doc.createTextNode(object.getReceiverBankCode()));
                row.appendChild(element);
                element = doc.createElement("BANK_MICR_CODE");
                element.appendChild(doc.createTextNode(object.getMicrcode()));
                row.appendChild(element);
                element = doc.createElement("TRAN_AMOUNT");
                element.appendChild(doc.createTextNode(object.getTranAmt() + ""));
                row.appendChild(element);
                element = doc.createElement("EMAILID");
                element.appendChild(doc.createTextNode(object.getEmailId()));
                row.appendChild(element);
                element = doc.createElement("MOBILE_NO");
                element.appendChild(doc.createTextNode(object.getMobileno()));
                row.appendChild(element);
                rootElement.appendChild(row);
                // write the content into xml file
                boolean success = new File(folderPath).mkdirs();

                File f = new File(folderPath, filename);
                if (f.exists()) {
                    f.delete();
                    f.createNewFile();
                }
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(f);
                transformer.transform(source, result);
                if (result.getOutputStream() != null) {
                    result.getOutputStream().close();
                }
            }
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        } catch (IOException iox) {
            iox.printStackTrace();
        }

    }

    public void createXMLBT(String folderPath, String filename, ArrayList objlist) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("BT_DETAILS");
            doc.appendChild(rootElement);
            for (int i = 0; i < objlist.size(); i++) {
                BytransferDetails object = (BytransferDetails) objlist.get(i);
                if (object.getAmount() > 0) {

                    Element row = doc.createElement("ROW");
                    Element element = doc.createElement("HRMS_NO");
                    element.appendChild(doc.createTextNode(object.getHrmsgeneratedRefno() + ""));
                    row.appendChild(element);
                    element = doc.createElement("HRMS_DATE");
                    element.appendChild(doc.createTextNode(object.getHrmsgeneratedRefdate()));
                    row.appendChild(element);
                    element = doc.createElement("BT_SERIAL_NUMBER");
                    element.appendChild(doc.createTextNode(object.getBtserialno()));
                    row.appendChild(element);
                    element = doc.createElement("AMOUNT");
                    element.appendChild(doc.createTextNode(object.getAmount() + ""));
                    row.appendChild(element);
                    element = doc.createElement("TREASURY_CODE");
                    element.appendChild(doc.createTextNode(object.getTreasuryCode()));
                    row.appendChild(element);

                    rootElement.appendChild(row);
                }
            }
            // write the content into xml file
            boolean success = new File(folderPath).mkdirs();

            File f = new File(folderPath, filename);
            if (f.exists()) {
                f.delete();
                f.createNewFile();
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(f);
            transformer.transform(source, result);
            if (result.getOutputStream() != null) {
                result.getOutputStream().close();
            }
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        } catch (IOException iox) {
            iox.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "getMajorHeadListTreasuryWise", method = {RequestMethod.GET, RequestMethod.POST})
    public void getMajorHeadListTreasuryWise(HttpServletRequest request, @RequestParam("aqyear") int aqyear, @RequestParam("aqmonth") int aqmonth, @RequestParam("trcode") String trcode, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = null;
        ArrayList mjhcdlist = billBrowserDao.getMajorHeadListTreasuryWise(trcode, aqyear, aqmonth);
        JSONArray json = new JSONArray(mjhcdlist);
        out = response.getWriter();
        out.write(json.toString());
    }

    @ResponseBody
    @RequestMapping(value = "getVoucherListMajorHeadWise", method = {RequestMethod.GET, RequestMethod.POST})
    public void getVoucherListMajorHeadWise(HttpServletRequest request, @RequestParam("majorhead") String majorhead, @RequestParam("aqyear") int aqyear, @RequestParam("aqmonth") int aqmonth, @RequestParam("trcode") String trcode, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = null;
        ArrayList vchlist = billBrowserDao.getVoucherListTreasuryWise(trcode, aqyear, aqmonth, majorhead);
        JSONArray json = new JSONArray(vchlist);
        out = response.getWriter();
        out.write(json.toString());
    }

    @RequestMapping(value = "lockBill", method = RequestMethod.GET)
    public ModelAndView lockBill(HttpServletRequest request, @ModelAttribute("BillBrowserbean") BillBrowserbean bbbean, @ModelAttribute("LoginUserBean") LoginUserBean lub, HttpServletResponse response) throws IOException {
        String path = "/payroll/BillBrowser";

        billBrowserDao.updateBillStatus(Integer.parseInt(bbbean.getBillNo()), 2);
        ArrayList billYears = billBrowserDao.getBillPrepareYear(lub.getOffcode());
        ArrayList billMonths = billBrowserDao.getMonthFromSelectedYear(lub.getOffcode(), bbbean.getSltYear());
        ArrayList<BillBean> billList = billBrowserDao.getPayBillList(bbbean.getSltYear(), bbbean.getSltMonth(), lub.getOffcode(), bbbean.getTxtbilltype(), lub.getSpc());
        ModelAndView mav = new ModelAndView(path, "command", bbbean);
        mav.addObject("billYears", billYears);
        mav.addObject("billMonths", billMonths);
        mav.addObject("billList", billList);
        mav.setViewName(path);
        return mav;
    }

    @RequestMapping(value = "editBill", method = RequestMethod.GET)
    public ModelAndView editBill(ModelMap model, @ModelAttribute("BillBrowserbean") BillBrowserbean bbbean, @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response) {
        String path = "/payroll/BillBrowserData";
        CommonReportParamBean crb = paybillDmpDao.getCommonReportParameter(bbbean.getBillNo());
        bbbean.setSltMonth(crb.getAqmonth());
        bbbean.setSltYear(crb.getAqyear());
        bbbean.setTxtbilltype(crb.getTypeofBill());
        bbbean.setChartofAcct(billBrowserDao.getBillChartofAccount(Integer.parseInt(bbbean.getBillNo())));

        BillDetail billDtls = billBrowserDao.getBillDetails(Integer.parseInt(bbbean.getBillNo()));

        bbbean.setBilldesc(billDtls.getBillnumber());
        bbbean.setBenificiaryNumber(billDtls.getBeneficiaryrefno());
        bbbean.setVchDt(billDtls.getVchDt());
        bbbean.setVchNo(billDtls.getVchNo());
        bbbean.setBillDate(billDtls.getBillDate());
        bbbean.setTreasury(billDtls.getTreasuryCode());
        ModelAndView mav = new ModelAndView(path, "command", bbbean);

        bbbean.setStatus(billDtls.getBillStatusId());

        List treasuryList = treasuryDao.getTreasuryList();
        mav.addObject("allowanceList", billBrowserDao.getAllowanceList(Integer.parseInt(bbbean.getBillNo())));
        mav.addObject("deductionList", billBrowserDao.getDeductionList(Integer.parseInt(bbbean.getBillNo())));
        mav.addObject("pvtloanList", billBrowserDao.getPvtLoanList(Integer.parseInt(bbbean.getBillNo())));
        mav.addObject("treasuryList", treasuryList);
        mav.addObject("billDtls", billDtls);
        return mav;
    }

    @RequestMapping(value = "editBillArrear", method = RequestMethod.GET)
    public ModelAndView editBillArrear(ModelMap model, @ModelAttribute("BillBrowserbean") BillBrowserbean bbbean, @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response) {
        String path = "/payroll/BillBrowserArrearData";
        CommonReportParamBean crb = paybillDmpDao.getCommonReportParameter(bbbean.getBillNo());
        bbbean.setSltMonth(crb.getAqmonth());
        bbbean.setSltYear(crb.getAqyear());
        bbbean.setTxtbilltype(crb.getTypeofBill());

        BillDetail billDtls = billBrowserDao.getBillDetails(Integer.parseInt(bbbean.getBillNo()));
        bbbean.setChartofAcct(billBrowserDao.getBillChartofAccount(Integer.parseInt(bbbean.getBillNo())));
        bbbean.setBilldesc(billDtls.getBillnumber());
        bbbean.setBenificiaryNumber(billDtls.getBeneficiaryrefno());
        bbbean.setVchDt(billDtls.getVchDt());
        bbbean.setVchNo(billDtls.getVchNo());
        bbbean.setBillDate(billDtls.getBillDate());
        bbbean.setTreasury(billDtls.getTreasuryCode());
        ModelAndView mav = new ModelAndView(path, "command", bbbean);

        bbbean.setStatus(billDtls.getBillStatusId());

        List treasuryList = treasuryDao.getTreasuryList();
        mav.addObject("allowanceList", billBrowserDao.getAllowanceList(Integer.parseInt(bbbean.getBillNo())));
        mav.addObject("deductionList", billBrowserDao.getDeductionList(Integer.parseInt(bbbean.getBillNo())));
        mav.addObject("pvtloanList", billBrowserDao.getPvtLoanList(Integer.parseInt(bbbean.getBillNo())));
        mav.addObject("treasuryList", treasuryList);
        mav.addObject("billDtls", billDtls);
        return mav;
    }

    @RequestMapping(value = "saveBill", params = "action=Cancel", method = RequestMethod.POST)
    public ModelAndView returnBill(ModelMap model, @ModelAttribute("BillBrowserbean") BillBrowserbean bbbean, @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response) {
        String path = "redirect:/getPayBillList.htm";
        BillDetail billDtls = billBrowserDao.getBillDetails(Integer.parseInt(bbbean.getBillNo()));
        bbbean.setSltYear(billDtls.getBillyear());

        ModelAndView mav = new ModelAndView(path, "command", bbbean);
        mav.addObject("sltYear", bbbean.getSltYear());
        mav.addObject("sltMonth", bbbean.getSltMonth());
        mav.addObject("txtbilltype", bbbean.getTxtbilltype());

        mav.setViewName(path);
        return mav;
    }

    @RequestMapping(value = "saveBill", params = "action=ChangeChartofAccount", method = RequestMethod.POST)
    public ModelAndView changeBillChartofAccount(ModelMap model, @ModelAttribute("BillBrowserbean") BillBrowserbean bbbean, @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response) {
        String path = "payroll/ChangeChartofAccount";
        BillDetail billDtls = billBrowserDao.getBillDetails(Integer.parseInt(bbbean.getBillNo()));
        bbbean.setSltYear(billDtls.getBillyear());

        bbbean.setChartofAcct(billBrowserDao.getBillChartofAccount(Integer.parseInt(bbbean.getBillNo())));
        System.out.println("====" + billDtls.getDemandNumber());
        bbbean.setTxtDemandno(billDtls.getDemandNumber());
        bbbean.setTxtmajcode(billDtls.getMajorHead());
        System.out.println("bbbean==" + bbbean.getTxtDemandno());
        bbbean.setTxtmincode(billDtls.getMinorHead());
        bbbean.setSubmajcode(billDtls.getSubMajorHead());
        bbbean.setSubmincode1(billDtls.getSubHead());
        bbbean.setSubmincode2(billDtls.getDetailHead());
        bbbean.setSubmincode3(billDtls.getChargedVoted());
        bbbean.setPlanCode(billDtls.getPlanStatus());
        bbbean.setSectorCode(billDtls.getSectorCode());

        ModelAndView mav = new ModelAndView(path, "command", bbbean);
        mav.addObject("txtDemandno", bbbean.getTxtDemandno());
        mav.addObject("sltYear", bbbean.getSltYear());
        mav.addObject("sltMonth", bbbean.getSltMonth());
        mav.addObject("txtbilltype", bbbean.getTxtbilltype());

        mav.setViewName(path);
        return mav;
    }

    @RequestMapping(value = "saveBillArrear", params = "action=ChangeChartofAccount", method = RequestMethod.POST)
    public ModelAndView changeArrearBillChartofAccount(ModelMap model, @ModelAttribute("BillBrowserbean") BillBrowserbean bbbean, @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response) {
        String path = "payroll/ChangeChartofAccount";
        BillDetail billDtls = billBrowserDao.getBillDetails(Integer.parseInt(bbbean.getBillNo()));
        bbbean.setSltYear(billDtls.getBillyear());

        bbbean.setChartofAcct(billBrowserDao.getBillChartofAccount(Integer.parseInt(bbbean.getBillNo())));
        System.out.println("====" + billDtls.getDemandNumber());
        bbbean.setTxtDemandno(billDtls.getDemandNumber());
        bbbean.setTxtmajcode(billDtls.getMajorHead());
        System.out.println("bbbean==" + bbbean.getTxtDemandno());
        bbbean.setTxtmincode(billDtls.getMinorHead());
        bbbean.setSubmajcode(billDtls.getSubMajorHead());
        bbbean.setSubmincode1(billDtls.getSubHead());
        bbbean.setSubmincode2(billDtls.getDetailHead());
        bbbean.setSubmincode3(billDtls.getChargedVoted());
        bbbean.setPlanCode(billDtls.getPlanStatus());
        bbbean.setSectorCode(billDtls.getSectorCode());
        ModelAndView mav = new ModelAndView(path, "command", bbbean);
        mav.addObject("sltYear", bbbean.getSltYear());
        mav.addObject("sltMonth", bbbean.getSltMonth());
        mav.addObject("txtbilltype", bbbean.getTxtbilltype());

        mav.setViewName(path);
        return mav;
    }

    @RequestMapping(value = "saveBill", params = "action=Update", method = RequestMethod.POST)
    public ModelAndView updateBillChartofAccount(ModelMap model, @ModelAttribute("BillBrowserbean") BillBrowserbean bbbean, @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response) {
        String path = "payroll/ChangeChartofAccount";
        BillDetail billDtls = billBrowserDao.getBillDetails(Integer.parseInt(bbbean.getBillNo()));
        bbbean.setSltYear(billDtls.getBillyear());
        System.out.println("billno==" + bbbean.getBillNo());
        billBrowserDao.updateBillChartofAccount(Integer.parseInt(bbbean.getBillNo()), bbbean);
        bbbean.setChartofAcct(billBrowserDao.getBillChartofAccount(Integer.parseInt(bbbean.getBillNo())));

        ModelAndView mav = new ModelAndView(path, "command", bbbean);
        mav.addObject("sltYear", bbbean.getSltYear());
        mav.addObject("sltMonth", bbbean.getSltMonth());
        mav.addObject("txtbilltype", bbbean.getTxtbilltype());

        mav.setViewName(path);
        return mav;
    }

    @RequestMapping(value = "saveBillArrear", params = "action=Update", method = RequestMethod.POST)
    public ModelAndView updateArrearBillChartofAccount(ModelMap model, @ModelAttribute("BillBrowserbean") BillBrowserbean bbbean, @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response) {
        String path = "payroll/ChangeChartofAccount";
        BillDetail billDtls = billBrowserDao.getBillDetails(Integer.parseInt(bbbean.getBillNo()));
        bbbean.setSltYear(billDtls.getBillyear());

        billBrowserDao.updateBillChartofAccount(Integer.parseInt(bbbean.getBillNo()), bbbean);
        bbbean.setChartofAcct(billBrowserDao.getBillChartofAccount(Integer.parseInt(bbbean.getBillNo())));

        ModelAndView mav = new ModelAndView(path, "command", bbbean);
        mav.addObject("sltYear", bbbean.getSltYear());
        mav.addObject("sltMonth", bbbean.getSltMonth());
        mav.addObject("txtbilltype", bbbean.getTxtbilltype());

        mav.setViewName(path);
        return mav;
    }

    @RequestMapping(value = "saveBillArrear", params = "action=Cancel", method = RequestMethod.POST)
    public ModelAndView returnBack(ModelMap model, @ModelAttribute("BillBrowserbean") BillBrowserbean bbbean, @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response) {
        String path = "redirect:/getPayBillList.htm";
        BillDetail billDtls = billBrowserDao.getBillDetails(Integer.parseInt(bbbean.getBillNo()));
        bbbean.setSltYear(billDtls.getBillyear());
        //bbbean.setSltMonth(billDtls.getBillmonth());
        //bbbean.setTxtbilltype(billDtls.getBillType());

        ArrayList billYears = billBrowserDao.getBillPrepareYear(lub.getOffcode());
        ArrayList billMonths = billBrowserDao.getMonthFromSelectedYear(lub.getOffcode(), bbbean.getSltYear());
        ArrayList<BillBean> billList = billBrowserDao.getPayBillList(bbbean.getSltYear(), bbbean.getSltMonth(), lub.getOffcode(), bbbean.getTxtbilltype(), lub.getSpc());
        ModelAndView mav = new ModelAndView(path, "command", bbbean);
        mav.addObject("sltYear", bbbean.getSltYear());
        mav.addObject("sltMonth", bbbean.getSltMonth());
        mav.addObject("txtbilltype", bbbean.getTxtbilltype());

        mav.setViewName(path);
        return mav;
    }

    @RequestMapping(value = "saveBillArrear", params = "action=Reprocess")
    public ModelAndView reprocessBillArrear(ModelMap model, @ModelAttribute("BillBrowserbean") BillBrowserbean bbbean, @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response) {
        String path = "/payroll/BillBrowser";
        BillDetail billDtls = billBrowserDao.getBillDetails(Integer.parseInt(bbbean.getBillNo()));
        bbbean.setSltYear(billDtls.getBillyear());
        //bbbean.setSltMonth(billDtls.getBillmonth());
        //bbbean.setTxtbilltype(billDtls.getBillType());
        bbbean.setTxtbilltype("ARREAR");
        ArrayList billYears = billBrowserDao.getBillPrepareYear(lub.getOffcode());
        ArrayList billMonths = billBrowserDao.getMonthFromSelectedYear(lub.getOffcode(), bbbean.getSltYear());
        ArrayList<BillBean> billList = billBrowserDao.getPayBillList(bbbean.getSltYear(), bbbean.getSltMonth(), lub.getOffcode(), bbbean.getTxtbilltype(), lub.getSpc());
        int priority = billBrowserDao.getBillPriority(lub.getOffcode());
        bbbean.setOffCode(lub.getOffcode());
        bbbean.setBgid(billDtls.getBillgroupId());
        bbbean.setPriority(priority);

        billBrowserDao.reprocessSingleBill(bbbean);
        ModelAndView mav = new ModelAndView(path, "command", bbbean);
        mav.addObject("billYears", billYears);
        mav.addObject("billMonths", billMonths);
        mav.addObject("billList", billList);
        mav.setViewName(path);
        return mav;
    }

    @RequestMapping(value = "saveBill", params = "action=Reprocess")
    public ModelAndView reprocessBill(ModelMap model, @ModelAttribute("BillBrowserbean") BillBrowserbean bbbean, @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response) {
        String path = "/payroll/BillBrowser";
        BillDetail billDtls = billBrowserDao.getBillDetails(Integer.parseInt(bbbean.getBillNo()));
        bbbean.setSltYear(billDtls.getBillyear());
        //bbbean.setSltMonth(billDtls.getBillmonth());
        //bbbean.setTxtbilltype(billDtls.getBillType());
        bbbean.setTxtbilltype("PAY");
        ArrayList billYears = billBrowserDao.getBillPrepareYear(lub.getOffcode());
        ArrayList billMonths = billBrowserDao.getMonthFromSelectedYear(lub.getOffcode(), bbbean.getSltYear());
        ArrayList<BillBean> billList = billBrowserDao.getPayBillList(bbbean.getSltYear(), bbbean.getSltMonth(), lub.getOffcode(), bbbean.getTxtbilltype(), lub.getSpc());
        int priority = billBrowserDao.getBillPriority(lub.getOffcode());
        bbbean.setOffCode(lub.getOffcode());
        bbbean.setBgid(billDtls.getBillgroupId());
        bbbean.setPriority(priority);
        billBrowserDao.reprocessSingleBill(bbbean);
        ModelAndView mav = new ModelAndView(path, "command", bbbean);
        mav.addObject("billYears", billYears);
        mav.addObject("billMonths", billMonths);
        mav.addObject("billList", billList);
        mav.setViewName(path);
        return mav;
    }

    @RequestMapping(value = "saveBill", params = "action=Save", method = RequestMethod.POST)
    public ModelAndView saveBill(ModelMap model, @ModelAttribute("BillBrowserbean") BillBrowserbean bbbean, @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response) {
        String path = "redirect:/getPayBillList.htm";
        BillDetail billDtls = billBrowserDao.getBillDetails(Integer.parseInt(bbbean.getBillNo()));
        bbbean.setSltYear(billDtls.getBillyear());

        billBrowserDao.updateBillData(bbbean);

        ModelAndView mav = new ModelAndView(path, "command", bbbean);

        mav.addObject("sltYear", bbbean.getSltYear());
        mav.addObject("sltMonth", bbbean.getSltMonth());
        mav.addObject("txtbilltype", bbbean.getTxtbilltype());
        mav.setViewName(path);
        return mav;
    }

    @RequestMapping(value = "saveBillArrear", params = "action=Save", method = RequestMethod.POST)
    public ModelAndView saveBillArrear(ModelMap model, @ModelAttribute("BillBrowserbean") BillBrowserbean bbbean, @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, HttpServletResponse response) {
        String path = "redirect:/getPayBillList.htm";
        BillDetail billDtls = billBrowserDao.getBillDetails(Integer.parseInt(bbbean.getBillNo()));
        bbbean.setSltYear(billDtls.getBillyear());

        billBrowserDao.updateBillData(bbbean);

        ModelAndView mav = new ModelAndView(path, "command", bbbean);

        mav.addObject("sltYear", bbbean.getSltYear());
        mav.addObject("sltMonth", bbbean.getSltMonth());
        mav.addObject("txtbilltype", bbbean.getTxtbilltype());
        mav.setViewName(path);
        return mav;
    }

    /*@RequestMapping(value = "unlockbill")
    public ModelAndView getBillData(@ModelAttribute("billDetail") BillDetail billDetail) {
        ModelAndView mv = new ModelAndView();
        if (billDetail.getOffcode() != null && !billDetail.getOffcode().equals("")) {
            List data = billBrowserDao.getBillDetails(billDetail.getOffcode(), billDetail.getBillmonth(), billDetail.getBillyear());
            mv.addObject("data", data);
        } else if (billDetail.getBillnumber() != null && !billDetail.getBillnumber().equals("")) {
            ArrayList data = new ArrayList();
            data.add(billBrowserDao.getBillDetails(Integer.parseInt(billDetail.getBillnumber())));
            mv.addObject("data", data);
        }
        mv.setViewName("/payroll/UnlockBill");
        return mv;
    }*/
    
    @RequestMapping("unlockbill")
    public String getBillData(Model model, @ModelAttribute("billDetail") BillDetail billDetail) {
        List data = billBrowserDao.getBillData(billDetail);
        model.addAttribute("data", data);
        return ("/payroll/UnlockBill");
    }  
    @RequestMapping(value="unlockbilldata")
    public ModelAndView unlockbill(Model model, @ModelAttribute("billDetail") BillDetail billDetail) {
       billBrowserDao.unlockBill(billDetail);
        return new ModelAndView ("redirect:/unlockbill.htm");
    }
    @RequestMapping(value="unlockbilltoResubmit")
    public ModelAndView unlockBillToResubmit(Model model, @ModelAttribute("billDetail") BillDetail billDetail) {
       billBrowserDao.unlockBillToResubmit(billDetail);
        return new ModelAndView ("redirect:/unlockbill.htm");
    }
    
    @RequestMapping(value = "unlockbill", params = {"action=ObjectBill"})    
    public ModelAndView objectBill(Model model,@ModelAttribute("billDetail") BillDetail billDetail) {        
        billBrowserDao.objectBill(billDetail);
        return new ModelAndView("redirect:/unlockbill.htm");
    }

}
