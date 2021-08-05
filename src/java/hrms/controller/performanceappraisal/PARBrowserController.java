package hrms.controller.performanceappraisal;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import hrms.common.CommonFunctions;
import hrms.dao.fiscalyear.FiscalYearDAOImpl;
import hrms.dao.login.LoginDAOImpl;
import hrms.dao.performanceappraisal.PARBrowserDAOImpl;
import hrms.model.login.LoginUserBean;
import hrms.model.common.FileAttribute;
import hrms.model.login.Users;
import hrms.model.parmast.ParAbsenteeBean;
import hrms.model.parmast.ParAchievement;
import hrms.model.parmast.ParDetail;
import hrms.model.parmast.ParMaster;
import hrms.model.parmast.ParMasterBean;
import hrms.model.parmast.ParOtherDetails;
import hrms.model.parmast.ParSubmitForm;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.ModelAndView;
import hrms.model.parmast.PARReportBean;
import org.springframework.ui.Model;

@Controller
@SessionAttributes("LoginUserBean")
public class PARBrowserController implements ServletContextAware {

    @Autowired
    public LoginDAOImpl loginDao;

    @Autowired
    public PARBrowserDAOImpl parbrowserDao;

    @Autowired
    public FiscalYearDAOImpl fiscalDAO;

    private ServletContext context;

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.context = servletContext;
    }

    @ResponseBody
    @RequestMapping(value = "GetPARListJSON", method = RequestMethod.POST)
    public void getparlistJSON(@Valid @ModelAttribute("ParMastForm") ParMaster parMastForm, @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, Map<String, Object> model, HttpServletResponse response) {

        response.setContentType("application/json");
        JSONObject json = new JSONObject();
        PrintWriter out = null;

        ParMaster pmast = null;
        ParMasterBean pbean = null;
        ArrayList parlist = new ArrayList();
        try {
            String year = "";
            if (parMastForm.getFiscalyear() != null && !parMastForm.getFiscalyear().equals("")) {
                year = parMastForm.getFiscalyear();
            } else {
                year = fiscalDAO.getDefaultFiscalYear();
            }
            context.setAttribute("selectedyear", year);
            List tempparlist = parbrowserDao.getPARList(year, lub.getEmpid());

            for (int i = 0; i < tempparlist.size(); i++) {
                pmast = (ParMaster) tempparlist.get(i);
                pbean = new ParMasterBean();
                pbean.setParid(pmast.getParid());
                pbean.setParstatus(pmast.getParstatus());
                pbean.setPeriodfrom(CommonFunctions.getFormattedOutputDate1(pmast.getPeriodfrom()));
                pbean.setPeriodto(CommonFunctions.getFormattedOutputDate1(pmast.getPeriodto()));
                //pbean.setDesignation(pmast.getSubstantivePost().getSpn());
                pbean.setDesignation(pmast.getSpn());
                pbean.setIsClosed(pmast.getIsClosed());
                pbean.setAuthRemarksClosed(pmast.getAuthRemarksClosed());
                parlist.add(pbean);
            }

            json.put("total", 10);
            json.put("rows", parlist);
            out = response.getWriter();
            out.write(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
        }
    }

    @RequestMapping(value = "GetPARList", method = RequestMethod.GET)
    public ModelAndView getparlist(@Valid @ModelAttribute("ParMastForm") ParMaster parMastForm, @ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, Map<String, Object> model, HttpServletResponse response) {

        ModelAndView mav = new ModelAndView();
        String path = "/par/PARList";
        String year = "";
        try {
            year = fiscalDAO.getDefaultFiscalYear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mav.addObject("defaultfiscalyear", year);
        mav.setViewName(path);
        return mav;
    }

    @RequestMapping(value = "addPAR", method = RequestMethod.POST)
    public ModelAndView newPar(@RequestParam("newPar") String newPar, @ModelAttribute("ParMastForm") ParMaster parMastForm, @ModelAttribute("LoginUserBean") LoginUserBean lub, @ModelAttribute("parAbsentee") ParAbsenteeBean parabsenteeForm, @ModelAttribute("parAchievement") ParAchievement parAchievementForm, @ModelAttribute("parOtherDetails") ParOtherDetails parOtherDetails) {

        ModelAndView mav = new ModelAndView();
        String path = "";

        int pageNo = 0;
        Users emp = null;

        String parfrmdt = "";
        String partodt = "";
        String year = "";
        try {
            parMastForm.setEmpid(lub.getEmpid());

            pageNo = parMastForm.getPageno();

            if (newPar != null && (newPar.equals("Previous") || newPar.equals("Back") || newPar.equals("Next") || newPar.equals("Update") || newPar.equals("Create PAR") || newPar.equals("Request For NRC"))) {
                if (newPar.equals("Back") || newPar.equals("Previous")) {
                    if (pageNo == 1) {
                        year = fiscalDAO.getDefaultFiscalYear();
                        path = "/par/PARList";
                        mav.addObject("defaultfiscalyear", year);
                    } else if (pageNo == 2) {
                        parMastForm = parbrowserDao.getAppraiseInfo(lub.getEmpid(), parabsenteeForm.getHidparid());
                        emp = loginDao.getEmployeeProfileInfo(lub.getEmpid());
                        path = "/par/PersonalInformation";
                    } else if (pageNo == 3) {
                        parMastForm.setParid(parAchievementForm.getHidparid());
                        parfrmdt = parAchievementForm.getHidparfrmdt();
                        partodt = parAchievementForm.getHidpartodt();
                        path = "/par/AbsenteeList";
                    } else if (pageNo == 4) {
                        parMastForm.setParid(parOtherDetails.getHidparid());
                        path = "/par/AchievementList";
                    }
                } else {
                    if (pageNo == 0) {
                        emp = loginDao.getEmployeeProfileInfo(lub.getEmpid());
                        String isClosed = parbrowserDao.isFiscalYearClosed(parMastForm.getFiscalyear());
                        mav.addObject("isClosed", isClosed);
                        if (newPar.equals("Request For NRC")) {
                            path = "/par/CreateNRC";
                        } else {
                            path = "/par/PersonalInformation";
                        }
                    } else if (pageNo == 1) {
                        parfrmdt = CommonFunctions.getFormattedOutputDate1(parMastForm.getPeriodfrom());
                        partodt = CommonFunctions.getFormattedOutputDate1(parMastForm.getPeriodto());
                        boolean isDuplicatePeriod = parbrowserDao.isDuplicatePARPeriod(lub.getEmpid(), parfrmdt, partodt, parMastForm.getFiscalyear(), parMastForm.getHidparid());
                        int diffInDays = (int) ((parMastForm.getPeriodto().getTime() - parMastForm.getPeriodfrom().getTime()) / (1000 * 60 * 60 * 24));
                        String cadreCode = parMastForm.getCadreCode();
                        if (isDuplicatePeriod == false) {
                            if (!parMastForm.getHidparid().equals("0")) {
                                parMastForm.setParid(Integer.parseInt(parMastForm.getHidparid()));
                                parMastForm = parbrowserDao.getAppraiseInfo(lub.getEmpid(), Integer.parseInt(parMastForm.getHidparid()));
                            }
                            System.out.println("PAR SPN is: " + parMastForm.getSpc());
                            parfrmdt = parAchievementForm.getHidparfrmdt();
                            partodt = parAchievementForm.getHidpartodt();
                            emp = loginDao.getEmployeeProfileInfo(lub.getEmpid());
                            parMastForm.setSpn(emp.getSpn());
                            parMastForm.setOffname(emp.getOffname());
                            mav.addObject("parerrmsg", "Duplicate Period");
                            path = "/par/PersonalInformation";
                        } else if (diffInDays < 120) {
                            if (parMastForm.getHidparid() != null && !parMastForm.getHidparid().equals("0")) {
                                parMastForm = parbrowserDao.getAppraiseInfo(lub.getEmpid(), Integer.parseInt(parMastForm.getHidparid()));
                            }
                            emp = loginDao.getEmployeeProfileInfo(lub.getEmpid());
                            mav.addObject("parerrmsg", "The minimum period for recording remark is four months (120 days)");
                            path = "/par/PersonalInformation";
                        } else if (cadreCode == null || cadreCode.equals("")) {
                            if (parMastForm.getHidparid() != null && !parMastForm.getHidparid().equals("0")) {
                                parMastForm = parbrowserDao.getAppraiseInfo(lub.getEmpid(), Integer.parseInt(parMastForm.getHidparid()));
                            }
                            emp = loginDao.getEmployeeProfileInfo(lub.getEmpid());
                            mav.addObject("parerrmsg", "Service to which the officer belongs cannot be blank.");
                            path = "/par/PersonalInformation";
                        } else {
                            parMastForm.setParid(parbrowserDao.savePAR(pageNo, parMastForm, null, null));
                            parMastForm.setFiscalyear(parMastForm.getFiscalyear());
                            if (newPar.equals("Update")) {
                                path = "/par/PARList";
                            } else {
                                path = "/par/AbsenteeList";
                            }
                        }
                    } else if (pageNo == 2) {
                        parfrmdt = parabsenteeForm.getHidparfrmdt();
                        partodt = parabsenteeForm.getHidpartodt();
                        parMastForm.setParid(parabsenteeForm.getHidparid());
                        parMastForm.setFiscalyear(parabsenteeForm.getFiscalyear());
                        path = "/par/AchievementList";
                    } else if (pageNo == 3) {
                        parMastForm.setParid(parAchievementForm.getHidparid());
                        parOtherDetails = parbrowserDao.getOtherDetails(lub.getEmpid(), Integer.parseInt(parMastForm.getHidparid()));
                        if (parOtherDetails != null && !parOtherDetails.equals("")) {
                            parOtherDetails.setHidpaptid(parOtherDetails.getPaptid() + "");
                        }
                        emp = loginDao.getEmployeeProfileInfo(lub.getEmpid());
                        path = "/par/OtherDetails";
                    } else if (pageNo == 4) {
                        //parbrowserDao.savePAR(pageNo, parMastForm,parOtherDetails);
                        //path = "/par/PARList";
                    }
                }
            } else if (newPar != null && !newPar.equals("Next")) {
                if (newPar.equals("Add New")) {
                    if (parabsenteeForm.getMode() != null && parabsenteeForm.getMode().equals("absentee")) {
                        parabsenteeForm.setHidparid(parabsenteeForm.getHidparid());
                        parfrmdt = parabsenteeForm.getHidparfrmdt();
                        partodt = parabsenteeForm.getHidpartodt();
                        path = "/par/NewAbsentee";
                    } else if (parAchievementForm.getMode() != null && parAchievementForm.getMode().equals("achievement")) {
                        parMastForm.setParid(parAchievementForm.getHidparid());
                        parfrmdt = parAchievementForm.getHidparfrmdt();
                        partodt = parAchievementForm.getHidpartodt();
                        path = "/par/NewAchievement";
                    }
                } else if (newPar.equals("Save")) {
                    if (parabsenteeForm.getMode() != null && parabsenteeForm.getMode().equals("absentee")) {
                        parMastForm.setParid(parabsenteeForm.getHidparid());
                        parfrmdt = parabsenteeForm.getHidparfrmdt();
                        partodt = parabsenteeForm.getHidpartodt();
                        parbrowserDao.saveAbsentee(parabsenteeForm);
                        path = "/par/AbsenteeList";
                    } else if (parAchievementForm.getMode() != null && parAchievementForm.getMode().equals("achievement")) {
                        parMastForm.setParid(parAchievementForm.getHidparid());
                        String filepath = context.getInitParameter("ParPath");
                        parbrowserDao.saveAchievement(lub.getEmpid(), parAchievementForm, filepath);
                        parfrmdt = parAchievementForm.getHidparfrmdt();
                        partodt = parAchievementForm.getHidpartodt();
                        path = "/par/AchievementList";
                    } else {
                        parbrowserDao.savePAR(pageNo, parMastForm, parOtherDetails, null);
                        path = "/par/PARList";
                    }
                } else if (newPar.equals("Cancel")) {
                    if (parabsenteeForm.getMode() != null && parabsenteeForm.getMode().equals("absentee")) {
                        parMastForm.setParid(parabsenteeForm.getHidparid());
                        parfrmdt = parabsenteeForm.getHidparfrmdt();
                        partodt = parabsenteeForm.getHidpartodt();
                        path = "/par/AbsenteeList";
                    } else if (parAchievementForm.getMode() != null && parAchievementForm.getMode().equals("achievement")) {
                        parMastForm.setParid(parAchievementForm.getHidparid());
                        parfrmdt = parAchievementForm.getHidparfrmdt();
                        partodt = parAchievementForm.getHidpartodt();
                        path = "/par/AchievementList";
                    }
                } else if (newPar.equals("Delete")) {
					boolean isReverted = parbrowserDao.isPARReverted(Integer.parseInt(parMastForm.getHidparid()), lub.getEmpid());
                    if(isReverted == false){
						parbrowserDao.deletePAR(parMastForm.getHidparid(), lub.getEmpid());
						path = "/par/PARList";
					}else{
                        emp = loginDao.getEmployeeProfileInfo(lub.getEmpid());
                        parMastForm = parbrowserDao.getAppraiseInfo(lub.getEmpid(), Integer.parseInt(parMastForm.getHidparid()));
                        mav.addObject("parerrmsg", "Reverted PAR cannot be deleted.");
                        path = "/par/PersonalInformation";
                    }
                }
                mav.addObject("parAbsentee", parabsenteeForm);
            }
            mav.addObject("users", emp);
            mav.addObject("parMastForm", parMastForm);
            mav.addObject("parOtherDetails", parOtherDetails);
            mav.addObject("parfrmdt", parfrmdt);
            mav.addObject("partodt", partodt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mav.setViewName(path);
        return mav;
    }

    @RequestMapping(value = "saveAbsenteeorAchievement", method = RequestMethod.POST)
    public String saveAbsenteeorAchievement() {

        String path = "";

        try {
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return path;
    }

    @RequestMapping(value = "editPAR", method = RequestMethod.GET)
    public ModelAndView editPar(@Valid @ModelAttribute("ParMastForm") ParMaster parMastForm, @ModelAttribute("LoginUserBean") LoginUserBean lub, @RequestParam("parid") int parid) {

        ModelAndView mav = new ModelAndView();
        String path = "";

        Users emp = null;
        try {

            //System.out.println("Inside Edit, PAR ID is: "+parid);
            parMastForm = parbrowserDao.getAppraiseInfo(lub.getEmpid(), parid);
            //parMastForm.setHidparid(parMastForm.getParid()+"");
            emp = loginDao.getEmployeeProfileInfo(lub.getEmpid());
			
			String isClosed = parbrowserDao.isFiscalYearClosed(parMastForm.getFiscalyear());
            mav.addObject("isClosed", isClosed);
			
            //System.out.println("Fiscal Year inside edit is: "+parMastForm.getFiscalyear());
            mav.addObject("parMastForm", parMastForm);
            mav.addObject("users", emp);

            path = "/par/PersonalInformation";
        } catch (Exception e) {
            e.printStackTrace();
        }
        mav.setViewName(path);
        return mav;
    }

    @RequestMapping(value = "editAbsenteeorAchievement", method = RequestMethod.GET)
    public ModelAndView editAbsenteeorAchievement(@ModelAttribute("LoginUserBean") LoginUserBean lub, @RequestParam Map<String, String> requestParams) {

        String path = "";

        ParAbsenteeBean pabs = null;
        ParAchievement pach = null;
        ParMaster parMast = new ParMaster();

        ModelAndView mav = new ModelAndView();
        try {
            String mode = (String) requestParams.get("mode");
            int id = Integer.parseInt(requestParams.get("id"));
            String fiscalyear = (String) requestParams.get("fiscalyear");
            String parfrmdt = (String) requestParams.get("parfrmdt");
            String partodt = (String) requestParams.get("partodt");

            if (mode != null && mode.equals("absentee")) {
                pabs = parbrowserDao.getAbsenteeInfo(lub.getEmpid(), id);
                parMast.setParid(pabs.getHidparid());
                mav.addObject("parAbsentee", pabs);
                mav.addObject("parfrmdt", parfrmdt);
                mav.addObject("partodt", partodt);
                path = "/par/NewAbsentee";
            } else if (mode != null && mode.equals("achievement")) {
                pach = parbrowserDao.getAchievementInfo(lub.getEmpid(), id);
                pach.setHidpacid(id);
                parMast.setParid(pach.getHidparid());
                mav.addObject("parAchievement", pach);
                path = "/par/NewAchievement";
            }
            parMast.setFiscalyear(fiscalyear);
            mav.addObject("parMastForm", parMast);
            mav.setViewName(path);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return mav;
    }

    @RequestMapping(value = "deleteAbsenteeorAchievement", method = {RequestMethod.GET, RequestMethod.POST})
    public void deleteAbsenteeorAchievement(@ModelAttribute("LoginUserBean") LoginUserBean lub, HttpServletResponse response, @RequestParam Map<String, String> requestParams) throws JSONException, IOException {

        String msgType = "";
        String msg = "";

        PrintWriter out = response.getWriter();
        JSONObject obj = new JSONObject();
        String mode = "";
        int id = 0;
        String fiscalyear = "";
        try {
            mode = requestParams.get("mode");
            id = Integer.parseInt(requestParams.get("id"));
            fiscalyear = requestParams.get("fsclyr");

            if (mode != null && mode.equals("absentee")) {
                System.out.println("Inside Absentee Delete and id is:" + id);
                parbrowserDao.deleteAbsentee(lub.getEmpid(), id);
                msg = "Absentee Deleted";
            } else if (mode != null && mode.equals("achievement")) {
                System.out.println("Inside Achievement Delete and id is:" + id);
                String filepath = context.getInitParameter("ParPath");
                parbrowserDao.deleteAchievement(lub.getEmpid(), id, fiscalyear, filepath);
                msg = "Achievement Deleted";
            }
            msgType = "Info";
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        obj.put("msg", msg);
        obj.put("msgType", msgType);
        out.write(obj.toString());
        out.flush();
        out.close();
    }

    @RequestMapping(value = "DeleteAchievementAttachment.htm", method = RequestMethod.POST)
    public void DeleteAchievementAttachment(@ModelAttribute("LoginUserBean") LoginUserBean lub, HttpServletResponse response, @RequestParam Map<String, String> requestParams) throws JSONException, IOException {

        int pacid = Integer.parseInt(requestParams.get("achId"));
        int attid = Integer.parseInt(requestParams.get("attachmentid"));
        String fiscalyear = requestParams.get("fiscalyr");

        String msgType = "";
        String msg = "";

        PrintWriter out = response.getWriter();
        JSONObject obj = new JSONObject();

        int retVal = 0;
        try {
            String filepath = context.getInitParameter("ParPath");
            retVal = parbrowserDao.deleteAchievementAttachment(lub.getEmpid(), pacid, attid, fiscalyear, filepath);
            msgType = "Info";
            if (retVal > 0) {
                msg = "Achievement Attachment Deleted Successful";
            } else {
                msg = "Achievement Attachment Deletetion Failed";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        obj.put("msg", msg);
        obj.put("msgType", msgType);
        out.write(obj.toString());
        out.flush();
        out.close();
    }

    @RequestMapping(value = "submitPAR", method = RequestMethod.GET)
    public ModelAndView submitPAR(@Valid @ModelAttribute("ParMastForm") ParMaster parMastForm, @ModelAttribute("LoginUserBean") LoginUserBean lub, @RequestParam Map<String, String> requestParams) {

        ModelAndView mav = new ModelAndView();
        String path = "";

        String parfrmdt = "";
        String partodt = "";
        int hierarchyno = 0;
        ParSubmitForm psubfrm = null;
        try {
            int parid = Integer.parseInt(requestParams.get("parid"));
            //System.out.println("Inside Submit, PAR ID is: "+parid);

            parMastForm = parbrowserDao.getAppraiseInfo(lub.getEmpid(), parid);
            psubfrm = parbrowserDao.getAuthorityInfo(parid);
            String isAchievementPresent = parbrowserDao.isAchievementDataPresent(parid);
            String isOtherDetailsPresent = parbrowserDao.isOtherDetailsPresent(parid);

            parfrmdt = CommonFunctions.getFormattedOutputDate1(parMastForm.getPeriodfrom());
            partodt = CommonFunctions.getFormattedOutputDate1(parMastForm.getPeriodto());
            hierarchyno = parbrowserDao.getmaxhierachy(parMastForm.getParid(), parMastForm.getParstatus());
            //System.out.println("Max Hierachy no is: "+hierarchyno);
            mav.addObject("parMastForm", parMastForm);
            mav.addObject("ParSubmitForm", psubfrm);
            mav.addObject("parfrmdt", parfrmdt);
            mav.addObject("partodt", partodt);
            mav.addObject("hierarchyno", hierarchyno);
            mav.addObject("isAchievementPresent", isAchievementPresent);
            mav.addObject("isOtherDetailsPresent", isOtherDetailsPresent);

            path = "/par/PARSubmit";
        } catch (Exception e) {
            e.printStackTrace();
        }
        mav.setViewName(path);
        return mav;
    }

    @RequestMapping(value = "sendPAR", method = RequestMethod.POST)
    public ModelAndView sendPAR(@ModelAttribute("LoginUserBean") LoginUserBean lub, @ModelAttribute("ParSubmit") ParSubmitForm pf) {

        String path = "/par/PARList";
        String year = "";
        ModelAndView mav = new ModelAndView();

        ParMaster parMastForm = null;
        ParSubmitForm psubfrm = null;

        String parfrmdt = "";
        String partodt = "";
        int hierarchyno = 0;
        String isClosed = "N";
        try {
            int parid = pf.getHidparid();
            if (pf.getHidparstatus() == 0) {
                //boolean isReverted = parbrowserDao.isPARReverted(parid, lub.getEmpid());
                //if (isReverted == false) {
                    isClosed = parbrowserDao.isFiscalYearClosed(pf.getHidfiscalyear());
                //}
            }
            if (isClosed != null && !isClosed.equals("")) {
                if (isClosed.equals("N")) {
                    String isInvalidPrd = parbrowserDao.sendPar(pf);

                    if (isInvalidPrd.equals("N")) {
                        //parbrowserDao.sendPar(pf);
                        year = fiscalDAO.getDefaultFiscalYear();
                        path = "/par/PARList";
                        mav.addObject("defaultfiscalyear", year);
                    } else {
                        mav.addObject("invalidperiod", isInvalidPrd);

                        parMastForm = parbrowserDao.getAppraiseInfo(lub.getEmpid(), parid);
                        psubfrm = parbrowserDao.getAuthorityInfo(parid);

                        parfrmdt = CommonFunctions.getFormattedOutputDate1(parMastForm.getPeriodfrom());
                        partodt = CommonFunctions.getFormattedOutputDate1(parMastForm.getPeriodto());
                        hierarchyno = parbrowserDao.getmaxhierachy(parMastForm.getParid(), parMastForm.getParstatus());
                        //System.out.println("Max Hierachy no is: "+hierarchyno);
                        mav.addObject("parMastForm", parMastForm);
                        mav.addObject("ParSubmitForm", psubfrm);
                        mav.addObject("parfrmdt", parfrmdt);
                        mav.addObject("partodt", partodt);
                        mav.addObject("hierarchyno", hierarchyno);

                        path = "/par/PARSubmit";
                    }
                } else if (isClosed.equals("Y")) {
                    parMastForm = parbrowserDao.getAppraiseInfo(lub.getEmpid(), parid);
                    psubfrm = parbrowserDao.getAuthorityInfo(parid);

                    parfrmdt = CommonFunctions.getFormattedOutputDate1(parMastForm.getPeriodfrom());
                    partodt = CommonFunctions.getFormattedOutputDate1(parMastForm.getPeriodto());
                    hierarchyno = parbrowserDao.getmaxhierachy(parMastForm.getParid(), parMastForm.getParstatus());
                    //System.out.println("Max Hierachy no is: "+hierarchyno);
                    mav.addObject("parMastForm", parMastForm);
                    mav.addObject("ParSubmitForm", psubfrm);
                    mav.addObject("parfrmdt", parfrmdt);
                    mav.addObject("partodt", partodt);
                    mav.addObject("hierarchyno", hierarchyno);
                    mav.addObject("isClosed", isClosed);

                    path = "/par/PARSubmit";

                }
            }
            mav.setViewName(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mav;
    }

    /*@RequestMapping(value = "addPAR",params = "newAbsentee",method = RequestMethod.POST)
     public ModelAndView newAbsentee(@RequestParam String newAbsentee,@Valid @ModelAttribute("parAbsentee") ParAbsentee parabsenteeForm, @ModelAttribute("LoginUserBean") LoginUserBean lub){
     //return new ModelAndView("NewPAR", "command", new ParMaster());
        
     ModelAndView mav = new ModelAndView();
     String path = "";
        
     try{
     System.out.println("Inside New Absentee");
     if(newAbsentee.equals("Save")){
     parbrowserDao.saveAbsentee(parabsenteeForm);
     path = "/par/AbsenteeList";
     }else{
     path = "/par/NewAbsentee";
     }
     }catch(Exception e){
     e.printStackTrace();
     }
     mav.setViewName(path);
     return mav;
     }*/
    @ResponseBody
    @RequestMapping(value = "GetAbsenteeListJSON", method = RequestMethod.POST)
    public void getabsenteelistJSON(@ModelAttribute("LoginUserBean") LoginUserBean lub, HttpServletResponse response, @RequestParam("parid") int parid
    ) {

        response.setContentType("application/json");
        JSONObject json = new JSONObject();
        PrintWriter out = null;

        try {
            List absenteelist = parbrowserDao.getAbsenteeList(lub.getEmpid(), parid);
            json.put("total", 10);
            json.put("rows", absenteelist);
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
    @RequestMapping(value = "GetAchievementListJSON", method = RequestMethod.POST)
    public void getachievementlistJSON(@ModelAttribute("LoginUserBean") LoginUserBean lub, HttpServletResponse response, @RequestParam("parid") int parid
    ) {

        response.setContentType("application/json");
        JSONObject json = new JSONObject();
        PrintWriter out = null;
        try {
            List achievementlist = parbrowserDao.getAchievementList(lub.getEmpid(), parid);
            json.put("total", 10);
            json.put("rows", achievementlist);
            out = response.getWriter();
            out.write(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
        }
    }

    @RequestMapping(value = "GetAbsenceCauseListJSON", method = RequestMethod.POST)
    public @ResponseBody
    String getAbsenceCauseListJSON() {

        JSONArray json = null;
        try {
            List absencecauselist = parbrowserDao.getAbsenceCauseList();
            json = new JSONArray(absencecauselist);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return json.toString();
    }

    @RequestMapping(value = "GetLeaveTypeListJSON", method = RequestMethod.POST)
    public @ResponseBody
    String getLeaveTypeListJSON() {

        JSONArray json = null;
        try {
            List absencecauselist = parbrowserDao.getLeaveTypeList();
            json = new JSONArray(absencecauselist);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return json.toString();
    }

    @RequestMapping(value = "getLeaveorTrainingListJSON", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody
    String getLeaveorTrainingListJSON(@RequestParam("leavecause") String leavecause
    ) {

        List leaveortraininglist = null;
        JSONArray json = null;
        try {
            if (leavecause != null && !leavecause.equals("")) {
                if (leavecause.equals("L")) {
                    leaveortraininglist = parbrowserDao.getLeaveTypeList();
                } else if (leavecause.equals("T")) {
                    leaveortraininglist = parbrowserDao.getTrainingypeList();
                }
            }
            json = new JSONArray(leaveortraininglist);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return json.toString();
    }

    @RequestMapping(value = "ChangePost", method = RequestMethod.GET)
    public String ChangePost() {

        String path = "/par/PostChange";
        return path;
    }

    @RequestMapping(value = "GetFiscalYearListJSON", method = RequestMethod.POST)
    public @ResponseBody void getFiscalYearListJSON(HttpServletResponse response) {
        response.setContentType("application/json");
        JSONArray json = null;
        PrintWriter out = null;
        try {
            List fiscalyearlist = fiscalDAO.getFiscalYearList();
            json = new JSONArray(fiscalyearlist);
            out = response.getWriter();
            out.write(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
        }        
    }
	
    @InitBinder
    public void initBinder(WebDataBinder binder
    ) {
        DateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        //df.setLenient(false);
        binder.registerCustomEditor(Date.class, "periodfrom", new CustomDateEditor(df, true));
        binder.registerCustomEditor(Date.class, "periodto", new CustomDateEditor(df, true));
        //binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @RequestMapping(value = "viewPAR", method = RequestMethod.GET)
    public void viewPARPdf(@ModelAttribute("LoginUserBean") LoginUserBean lub, HttpServletResponse response, @RequestParam("parid") int parid
    ) {

        response.setContentType("application/pdf");
        Document document = null;

        ParDetail paf = null;

        PdfWriter writer = null;

        String htmlContent = new String();
        String str = null;
        try {
            document = new Document(PageSize.A4);

            response.setHeader("Content-Disposition", "attachment; filename=PAR_" + lub.getEmpid() + ".pdf");
            writer = PdfWriter.getInstance(document, response.getOutputStream());
			writer.setPageEvent(new HeaderFooter(lub.getEmpid()));
            document.open();

            URL pdfurl = new URL("https://par.hrmsodisha.gov.in/PARDetailPDF.htm?parid=" + parid);

            String sessionid = RequestContextHolder.getRequestAttributes().getSessionId();
            System.out.println("Session id is: " + sessionid);

            HttpURLConnection urlConn = null;
            urlConn = (HttpURLConnection) pdfurl.openConnection();
            urlConn.setDoOutput(true);
            urlConn.setDoInput(true);
            urlConn.setUseCaches(false);
            urlConn.setRequestProperty("Cookie", "JSESSIONID=" + sessionid);

            InputStreamReader fis = new InputStreamReader(urlConn.getInputStream());

            BufferedReader d = new BufferedReader(fis);

            while ((str = d.readLine()) != null) {
                htmlContent += str.trim();
            }
            //System.out.println("htmlContent is: " + htmlContent);
            StringReader strReader = new StringReader(StringUtils.defaultString(htmlContent));

            /*XMLWorkerFontProvider fontImp = new XMLWorkerFontProvider(XMLWorkerFontProvider.DONTLOOKFORFONTS);
             fontImp.register(FONT);*/
            XMLWorkerHelper worker = XMLWorkerHelper.getInstance();
            worker.parseXHtml(writer, document, strReader);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
    }

    @RequestMapping(value = "PARDetailPDF", method = RequestMethod.GET)
    public ModelAndView PARDetailPDF(@ModelAttribute("LoginUserBean") LoginUserBean lub, @RequestParam("parid") int parid
    ) {

        String path = "";
        ModelAndView mav = new ModelAndView();
        ParDetail paf = null;
        try {
			boolean viewPAR = parbrowserDao.isAuthorizedtoDownloadPAR(lub.getEmpid(),parid);
            if(viewPAR == true){
				paf = parbrowserDao.getPARDetails(lub.getEmpid(), parid, 0, "");
				paf.setLoginId(lub.getEmpid());
			}
            mav.addObject("pardetail", paf);

            path = "/par/PARDetailPDF";
            mav.setViewName(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mav;
    }

    @RequestMapping(value = "PARDetailView", method = RequestMethod.GET)
    public ModelAndView PARDetailView(@ModelAttribute("LoginUserBean") LoginUserBean lub, @RequestParam("parid") int parid, @RequestParam("taskid") int taskid, @RequestParam("auth") String auth
    ) {

        String path = "";
        ModelAndView mav = new ModelAndView();
        ParDetail paf = null;
        try {
			if(parid == 0){
                parid = parbrowserDao.getParid(lub.getEmpid(), taskid);
            }
			boolean viewPAR = parbrowserDao.isAuthorizedtoDownloadPAR(lub.getEmpid(),parid);
            if(viewPAR == true){
				paf = parbrowserDao.getPARDetails(lub.getEmpid(), parid, taskid, auth);
				paf.setLoginId(lub.getEmpid());
			}
            mav.addObject("pardetail", paf);

            path = "/par/PARDetailView";
            mav.setViewName(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mav;
    }

    @RequestMapping(value = "forwardPAR", method = RequestMethod.POST)
    public ModelAndView forwardPAR(@ModelAttribute("parDetail") ParDetail parDetail, @ModelAttribute("LoginUserBean") LoginUserBean lub, @RequestParam("forwardpar") String forwardpar
    ) {

        String path = "";
        ParDetail paf = null;
        ModelAndView mav = new ModelAndView();
		String isClosed = "N";
        try {
			isClosed = parbrowserDao.isAuthRemarksClosed(parDetail.getFiscalYear());
			
			if (isClosed != null && !isClosed.equals("")) {
                if (isClosed.equals("N")) {
					parbrowserDao.saveAndForwardPAR(parDetail, lub.getEmpid(), forwardpar);

					if (forwardpar.equals("Submit")) {
						path = "/par/PARRemarksDone";
					} else {
						paf = parbrowserDao.getPARDetails(lub.getEmpid(), parDetail.getParid(), parDetail.getTaskid(), null);
						mav.addObject("pardetail", paf);
						path = "/par/PARDetailView";
					}
				} else if (isClosed.equals("Y")) {
                    paf = parbrowserDao.getPARDetails(lub.getEmpid(), parDetail.getParid(), parDetail.getTaskid(), null);
                    mav.addObject("pardetail", paf);
                    path = "/par/PARDetailView";
                    mav.addObject("isClosed", isClosed);
                }
			}
            mav.setViewName(path);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return mav;
    }

    @RequestMapping(value = "GetPARGradeListJSON", method = RequestMethod.POST)
    public @ResponseBody
    String GetPARGradeListJSON() {

        JSONArray json = null;
        try {
            List gradelist = parbrowserDao.getPARGradeList();
            json = new JSONArray(gradelist);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return json.toString();
    }

    @RequestMapping(value = "AcceptingAuthRemarksPage", method = RequestMethod.GET)
    public ModelAndView AcceptingAuthRemarksPage(@ModelAttribute("LoginUserBean") LoginUserBean lub, @RequestParam("taskid") int taskid
    ) {

        String path = "/par/AcceptingAuthRemarks";
        ModelAndView mav = null;

        try {
            mav = new ModelAndView();

            int parid = parbrowserDao.getParid(lub.getEmpid(), taskid);
            System.out.println("PAR ID inside Accepting Authority Remarks page is: " + parid);
            mav.addObject("parid", parid);
            mav.addObject("taskid", taskid);
            mav.setViewName(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mav;
    }

    @RequestMapping(value = "AcceptingAuthRemarksSave", method = {RequestMethod.GET, RequestMethod.POST})
    public void AcceptingAuthRemarksSave(HttpServletResponse response, @ModelAttribute("LoginUserBean") LoginUserBean lub, @RequestParam Map<String, String> requestParams) throws JSONException, IOException {

        int parid = Integer.parseInt(requestParams.get("parid"));
        int taskid = Integer.parseInt(requestParams.get("taskid"));
        String acceptingAuthRemarks = requestParams.get("acceptingAuthRemarks");

        String msgType = "";
        String msg = "";
        try {
            int status = parbrowserDao.saveAcceptingAuthRemarksFromCombo(lub.getEmpid(), parid, taskid, acceptingAuthRemarks);

            if (status > 0) {
                msgType = "Info";
                msg = "Accepted";
            } else {
                msgType = "Error";
                msg = "Error Occured!";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        JSONObject obj = new JSONObject();
        obj.put("msg", msg);
        obj.put("msgType", msgType);
        PrintWriter out = response.getWriter();
        out.write(obj.toString());
        out.flush();
        out.close();
    }

    @RequestMapping(value = "GetNRCReasonListJSON", method = RequestMethod.POST)
    public @ResponseBody
    String getNRCReasonListJSON() {

        JSONArray json = null;
        try {
            List nrcreasonlist = parbrowserDao.getNRCReasonList();
            json = new JSONArray(nrcreasonlist);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return json.toString();
    }

    @RequestMapping(value = "addNRC", method = RequestMethod.POST)
    public ModelAndView saveNRC(@ModelAttribute("LoginUserBean") LoginUserBean lub, @ModelAttribute("ParMastForm") ParMaster parMastForm, @RequestParam("newNRC") String newNRC
    ) {

        String path = "";
        ModelAndView mav = null;

        boolean duplPeriod = false;
        Users emp = null;
        try {
            mav = new ModelAndView();

            if (!newNRC.equals("Submit for NRC")) {
                if (newNRC.equals("Back")) {
                    path = "/par/PARList";
                } else if (newNRC.equals("Delete")) {
                    String isClosed = parbrowserDao.isFiscalYearClosed(parMastForm.getFiscalyear());
                    mav.addObject("isClosed", isClosed);
                    if (isClosed.equalsIgnoreCase("N")) {
                        parbrowserDao.deleteNRC(Integer.parseInt(parMastForm.getHidparid()), parMastForm.getEmpid());
                    }
                    path = "/par/PARList";
                }
            } else {

                String parfrmdt = CommonFunctions.getFormattedOutputDate1(parMastForm.getPeriodfrom());
                String partodt = CommonFunctions.getFormattedOutputDate1(parMastForm.getPeriodto());
                duplPeriod = parbrowserDao.isDuplicatePARPeriod(lub.getEmpid(), parfrmdt, partodt, parMastForm.getFiscalyear(), parMastForm.getHidparid());
                //System.out.println("Duplicate Period is: "+duplPeriod);
                String cadreCode = parMastForm.getCadreCode();
                if (duplPeriod == false) {
                    emp = loginDao.getEmployeeProfileInfo(lub.getEmpid());
                    mav.addObject("users", emp);
                    mav.addObject("NRCError", "NRC Period is duplicate.");
                    path = "/par/CreateNRC";
                    parMastForm.setFiscalyear(parMastForm.getFiscalyear());
                } else if (cadreCode == null || cadreCode.equals("")) {
                    emp = loginDao.getEmployeeProfileInfo(lub.getEmpid());
                    mav.addObject("users", emp);
                    mav.addObject("NRCError", "Service to which the officer belongs cannot be blank.");
                    path = "/par/CreateNRC";
                } else {
                    String filepath = context.getInitParameter("ParPath");
                    parMastForm.setParstatus(17);
                    parbrowserDao.savePAR(1, parMastForm, null, filepath);
                    mav.addObject("defaultfiscalyear", parMastForm.getFiscalyear());
                    path = "/par/PARList";
                }
            }
            //String year = (String)context.getAttribute("selectedyear");
            //System.out.println("Selected Fiscal Year is: "+year);
            mav.addObject("parMastForm", parMastForm);
            mav.setViewName(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mav;
    }

    @RequestMapping(value = "NRCDetailView", method = {RequestMethod.POST, RequestMethod.GET})
    public ModelAndView editNRC(@ModelAttribute("LoginUserBean") LoginUserBean lub, @RequestParam("parid") int parid
    ) {

        ModelAndView mav = null;

        ParMaster parMastForm = null;
        Users emp = null;

        String path = "/par/CreateNRC";
        try {
            mav = new ModelAndView();

            parMastForm = parbrowserDao.getAppraiseInfo(lub.getEmpid(), parid);
            String nrcattch = parbrowserDao.getNRCAttachedFileName(lub.getEmpid(), parid);
            emp = loginDao.getEmployeeProfileInfo(lub.getEmpid());
            String isClosed = parbrowserDao.isFiscalYearClosed(parMastForm.getFiscalyear());
            mav.addObject("isClosed", isClosed);
            mav.addObject("parMastForm", parMastForm);
            mav.addObject("users", emp);
            mav.addObject("nrcattch", nrcattch);
            mav.setViewName(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mav;
    }

    @RequestMapping(value = "ViewNRCPDF", method = {RequestMethod.POST, RequestMethod.GET})
    public void viewNRCPDF(HttpServletResponse response, @ModelAttribute("LoginUserBean") LoginUserBean lub, @RequestParam("parid") int parid
    ) {

        response.setContentType("application/pdf");
        Document document = new Document(PageSize.A4);

        ParDetail paf = null;
        try {
            PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            paf = parbrowserDao.getNRCDetails(lub.getEmpid(), parid);
            parbrowserDao.viewNRCPDFfunc(document, paf, lub.getEmpid());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
    }

    @RequestMapping(value = "DownloadAchievementAttachment", method = RequestMethod.GET)
    public void downloadachievementattachment(HttpServletResponse response, @RequestParam("attId") int attId, @RequestParam("fiscalyr") String fiscalyr
    ) {

        FileAttribute fa = null;

        FileInputStream inputStream = null;
        OutputStream outStream = null;

        try {
            outStream = response.getOutputStream();

            String filepath = context.getInitParameter("ParPath");
            fa = parbrowserDao.downloadachievementattachment(filepath, attId, fiscalyr);

            response.setContentLength((int) fa.getUploadAttachment().length());
            response.setContentType(fa.getFileType());
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fa.getOriginalFileName() + "\"");

            byte[] buffer = new byte[4096];
            int bytesRead = -1;

            // write bytes read from the input stream into the output stream
            inputStream = new FileInputStream(fa.getUploadAttachment());
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @RequestMapping(value = "DownloadNRCAttch", method = RequestMethod.GET)
    public void downloadNRCAttachment(HttpServletResponse response, @RequestParam Map<String, String> requestParams
    ) {

        int parid = Integer.parseInt(requestParams.get("parId"));
        String fiscalyear = requestParams.get("fyear");

        FileAttribute fa = null;

        FileInputStream inputStream = null;
        OutputStream outStream = null;
        try {
            outStream = response.getOutputStream();

            String filepath = context.getInitParameter("ParPath");
            fa = parbrowserDao.downloadNRCAttachment(parid, fiscalyear, filepath);

            response.setContentLength((int) fa.getUploadAttachment().length());
            response.setContentType(fa.getFileType());
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fa.getOriginalFileName() + "\"");

            byte[] buffer = new byte[4096];
            int bytesRead = -1;

            // write bytes read from the input stream into the output stream
            inputStream = new FileInputStream(fa.getUploadAttachment());
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @RequestMapping(value = "RevertPAR", method = {RequestMethod.POST, RequestMethod.GET})
    public ModelAndView revertPage(@ModelAttribute("LoginUserBean") LoginUserBean lub, @RequestParam Map<String, String> requestParams
    ) {

        ModelAndView mav = null;
        String path = "/par/RevertPAR";

        ParDetail pdtl = new ParDetail();

        int parid = Integer.parseInt(requestParams.get("parId"));
        int parstatus = Integer.parseInt(requestParams.get("parStatus"));
        int taskid = Integer.parseInt(requestParams.get("taskId"));
        String isreportingcompleted = requestParams.get("isreportingcompleted");
		String fiscalyear = requestParams.get("fiscalyr");
        try {
            mav = new ModelAndView();
            pdtl.setParid(parid);
            pdtl.setParstatus(parstatus);
            pdtl.setTaskid(taskid);
            pdtl.setIsreportingcompleted(isreportingcompleted);
			pdtl.setFiscalYear(fiscalyear);

            mav.addObject("ParDetail", pdtl);

            mav.setViewName(path);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return mav;
    }

    @RequestMapping(value = "RevertSave", method = RequestMethod.POST)
    public ModelAndView revertSave(@ModelAttribute("LoginUserBean") LoginUserBean lub, @ModelAttribute("parDetail") ParDetail parDetail
    ) {

        ModelAndView mav = null;
        String path = "/par/RevertPAR";

        String isReverted = "N";
		String isClosed = "N";
        try {
            mav = new ModelAndView();
			isClosed = parbrowserDao.isAuthRemarksClosed(parDetail.getFiscalYear());
			
			if (isClosed != null && !isClosed.equals("")) {
				if(isClosed.equals("N")){
					isReverted = parbrowserDao.revertPAR(lub.getEmpid(), parDetail);
					mav.addObject("revertstatus", isReverted);
				}else if(isClosed.equals("Y")){
                    mav.addObject("isClosed", isClosed);
                }
			}
            mav.setViewName(path);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return mav;
    }

    @RequestMapping(value = "RevertReason", method = RequestMethod.GET)
    public ModelAndView RevertReason(@ModelAttribute("LoginUserBean") LoginUserBean lub, @RequestParam("parid") int parid
    ) {

        ModelAndView mav = null;

        try {
            mav = new ModelAndView();

            int taskid = parbrowserDao.getTaskid(lub.getEmpid(), parid);

            String[] revertreason = parbrowserDao.getRevertReason(parid, taskid);

            mav.addObject("authorityType", revertreason[0]);
            mav.addObject("revertRemaeks", revertreason[1]);
            mav.addObject("authorityName", revertreason[2]);
            mav.setViewName("/par/RevertReason");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return mav;
    }

    @RequestMapping(value = "IsClosedFinYr", method = RequestMethod.GET)
    public void IsClosedFinYr(HttpServletResponse response, @RequestParam("fslyr") String fyear
    ) {

        response.setContentType("text/html");
        try {
            String isClosed = parbrowserDao.isFiscalYearClosed(fyear);

            PrintWriter out = response.getWriter();
            out = response.getWriter();
            out.write(isClosed.toString());
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    @RequestMapping(value = "AddCadre", method = RequestMethod.GET)
    public String AddCadre() {

        String path = "/par/AddCadre";
        return path;
    }
	
	@RequestMapping(value = "ParReport")
    public String parReport(Model model,@RequestParam("fiscalyear") String fiscalyear){
        
        List parreportlist = null;
        PARReportBean prbean = null;
        
        int totalpar = 0;
        int totalparwithoutspc = 0;
        int totalreportingpendingpar = 0;
        int totalreviewingpendingpar = 0;
        int totalacceptingpendingpar = 0;
        int totalcompleted = 0;
        try{
            if(fiscalyear != null && !fiscalyear.equals("")){
                parreportlist = parbrowserDao.getPARReport(fiscalyear);
            }
            
            if(parreportlist != null && parreportlist.size() > 0){
                for(int i = 0;i < parreportlist.size();i++){
                    prbean = (PARReportBean)parreportlist.get(i);
                    totalpar = totalpar + prbean.getParapplied();
                    totalreportingpendingpar = totalreportingpendingpar + prbean.getReportingpending();
                    totalreviewingpendingpar = totalreviewingpendingpar + prbean.getReviewingpending();
                    totalacceptingpendingpar = totalacceptingpendingpar + prbean.getAcceptingpending();
                    totalcompleted = totalcompleted + prbean.getCompleted();
                }
            }
            model.addAttribute("fiscalyear", fiscalyear);
            model.addAttribute("PARReportList", parreportlist);
            model.addAttribute("NOOFPAR", totalpar);
            model.addAttribute("REPORTINGPENDING", totalreportingpendingpar);
            model.addAttribute("REVIEWINGPENDING", totalreviewingpendingpar);
            model.addAttribute("ACCEPTINGPENDING", totalacceptingpendingpar);
            model.addAttribute("COMPLETED", totalcompleted);
        }catch(Exception e){
            e.printStackTrace();
        }
       return "/par/PARStatusReport";
    }
}
