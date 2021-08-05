package hrms.controller.leaveapply;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import hrms.dao.employee.EmployeeDAO;
import hrms.dao.leaveapply.LeaveApplyDAO;
import hrms.dao.leaveapply.LeaveApplyDAOImpl;
import hrms.dao.login.LoginDAOImpl;
import hrms.dao.master.LeaveTypeDAOImpl;
import hrms.dao.notification.NotificationDAOImpl;
import hrms.dao.workflowrouting.WorkflowRoutingDAO;
import hrms.model.leave.Leave;
import hrms.model.leave.LeaveApply;
import hrms.model.leave.LeaveEntrytakenBean;
import hrms.model.leave.LeaveSancBean;
import hrms.model.login.LoginUserBean;
import hrms.model.login.Users;
import hrms.model.notification.NotificationBean;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
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

@Controller
@SessionAttributes("LoginUserBean")
public class LeaveApplyController {

    @Autowired
    public LeaveApplyDAO leaveApplyDAO;
    @Autowired
    public LoginDAOImpl loginDAO;
    @Autowired
    NotificationDAOImpl notificationDao;
    @Autowired
    WorkflowRoutingDAO workflowRoutingDao;
    @Autowired
    EmployeeDAO employeeDAO;

    public static String getServerDoe() {
        String currDate;
        Format formatter;
        formatter = new SimpleDateFormat("dd-MMM-yyyy");
        currDate = formatter.format(new Date());
        return currDate;
    }

    @ResponseBody
    @RequestMapping(value = "leaveapplylist.htm", method = RequestMethod.POST)
    public void viewLeaveList(@ModelAttribute("LoginUserBean") LoginUserBean lub, BindingResult result, Map<String, Object> model, HttpServletResponse response) {
        response.setContentType("application/json");
        JSONObject json = new JSONObject();
        PrintWriter out = null;
        try {
            List leavelist = leaveApplyDAO.getLeaveApplyList(lub.getEmpid());
            json.put("total", 50);
            json.put("rows", leavelist);
            out = response.getWriter();
            out.write(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
        }
    }

    @RequestMapping(value = "leaveapply.htm", method = RequestMethod.GET)
    public String viewLeave(@ModelAttribute("LoginUserBean") LoginUserBean lub, @ModelAttribute("leaveForm") Leave leave, Map<String, Object> model) {
        model.put("curdate", getServerDoe());
        // lub.setEmpid("75000935");
        // lub.setEmpid("43003363");
        // leaveapplyDAO.updateLeaveBalance(lub.getEmpid(),);
        leaveApplyDAO.getLeaveOpeningBalance(lub.getEmpid(), "CL", getServerDoe());
        leaveApplyDAO.getLeaveOpeningBalance(lub.getEmpid(), "EL", getServerDoe());
        leaveApplyDAO.getLeaveOpeningBalance(lub.getEmpid(), "HPL", getServerDoe());
        leaveApplyDAO.getLeaveOpeningBalance(lub.getEmpid(), "COL", getServerDoe());
        //LeaveBalanceBean balanceBean = leaveapplyDAO.getLeaveBalanceInfo(lub.getEmpid());
        model.put("elBalance", leaveApplyDAO.getLeaveBalanceInfo(lub.getEmpid(), "EL", "2016"));
        model.put("clBalance", leaveApplyDAO.getLeaveBalanceInfo(lub.getEmpid(), "CL", "2016"));
        model.put("hplBalance", leaveApplyDAO.getLeaveBalanceInfo(lub.getEmpid(), "HPL", "2016"));
        model.put("colBalance", leaveApplyDAO.getLeaveBalanceInfo(lub.getEmpid(), "COL", "2016"));
//           model.put("elBalance",balanceBean.getElBalance());
//          model.put("hplBalance",balanceBean.getHplBalance());
//          model.put("clBalance",balanceBean.getClBalance());
        model.put("leaveForm", leave);
        return "/leaveapply/LeaveApplyList";
    }

    @RequestMapping(value = "addleaveapply.htm", method = RequestMethod.GET)
    public ModelAndView addLeave(@ModelAttribute("LoginUserBean") LoginUserBean lub, @ModelAttribute("leaveForm") Leave leave, Map<String, Object> model, @RequestParam("empId") String empid) {
        // lub.setEmpid("59001138");
        ModelAndView mav = new ModelAndView();
        System.out.println("------------------->" + empid);
        Users user = loginDAO.getEmployeeProfileInfo(empid);
        System.out.println("--------++----------->" + user.getCurspc());
        if (empid != null && !empid.equals("")) {
            leave.setHidempId(empid);
            leave.setHidSpcCode(user.getCurspc());
        } else {
            leave.setHidempId(lub.getEmpid());
            leave.setHidSpcCode(lub.getSpc());
        }
        leave.setApplicantName(user.getFullName());
        mav.addObject("empList", workflowRoutingDao.getWorkFlowRoutingList(1, lub.getGpc(), lub.getOffcode()));
        mav.addObject("leaveForm", leave);
        mav.setViewName("leaveapply/LeaveApplyEdit");
        return mav;
    }

    @RequestMapping(value = "leaveextension.htm", method = RequestMethod.GET)
    public String leaveExtension(@RequestParam("taskId") String taskId, @ModelAttribute("LoginUserBean") LoginUserBean lub, @ModelAttribute("leaveForm") Leave leave, Map<String, Object> model) {

        leave = leaveApplyDAO.getLeaveData(taskId, lub.getEmpid(), lub.getSpc());
        leave.setHidempId(lub.getEmpid());
        leave.setHidSpcCode(lub.getSpc());
        model.put("leaveForm", leave);
        return "leaveapply/LeaveApplyEdit";
    }

    @RequestMapping(params = "ApplyFor", value = "leaveapply.htm", method = RequestMethod.POST)
    public String applyForOther(@ModelAttribute("LoginUserBean") LoginUserBean lub, @ModelAttribute("leaveForm") Leave leave, Map<String, Object> model) {
        // lub.setEmpid("59001138");
        leave.setHidempId(lub.getEmpid());
        leave.setHidSpcCode(lub.getSpc());
        model.put("leaveForm", leave);
        return "leaveapply/LeaveApplyFor";
    }

    @RequestMapping(value = "leaveapplyforemp.htm", method = {RequestMethod.POST, RequestMethod.GET})
    public ModelAndView LeaveApplyForOther(@ModelAttribute("LoginUserBean") LoginUserBean lub, @ModelAttribute("leaveForm") Leave leave, Map<String, Object> model) {
        // lub.setEmpid("59001138");
        ModelAndView mav = new ModelAndView();
        leave.setHidempId(lub.getEmpid());
        leave.setHidSpcCode(lub.getSpc());
        // mav.addObject("empList", workflowRoutingDao.getWorkFlowRoutingList(1, lub.getGpc(), lub.getOffcode()));
        mav.addObject("empList", employeeDAO.getOffWiseEmpList(lub.getOffcode()));

        mav.addObject("leaveForm", leave);
        mav.setViewName("leaveapply/LeaveApplyForEmployees");
        return mav;
        //  return "leaveapply/LeaveApplyForEmployees";
    }

    @RequestMapping(value = "leaveapplyedit.htm", method = RequestMethod.POST, params = "Save")
    public String submitLeave(@ModelAttribute("LoginUserBean") LoginUserBean lub, @ModelAttribute("leaveForm") LeaveApply leave, Map<String, Object> model) {
        String path = "";
        try {
            boolean ifMaxChild = leaveApplyDAO.ifMaxSurviveChild(leave.getSltleaveType(), leave.getHidempId());
            boolean ifMoreThanMaxPeriod = leaveApplyDAO.maxPeriodCount(leave);
            int maxPeriodCnt = leaveApplyDAO.maxLeavePeriodCount(leave.getSltleaveType());
            String leaveType = leaveApplyDAO.getLeaveType(leave.getSltleaveType());
            boolean ifEmpExist = leaveApplyDAO.ifEmpExist(leave.getHidempId());
            boolean ifexist = leaveApplyDAO.getIfLeaveRecordExist(leave.getHidempId(), leave.getSltleaveType(), leave.getTxtperiodFrom(), leave.getTxtperiodTo());
            // boolean maxLeaveExceeds = leaveapplyDAO.getmaxLeaveAvailable(leave.getSltleaveType(), leave.getTxtperiodFrom(), leave.getTxtperiodTo());
            // System.out.println(maxLeaveExceeds);
            model.put("curdate", getServerDoe());
            if (ifEmpExist == false) {
                leaveApplyDAO.saveLeave(leave);
                leaveApplyDAO.getLeaveOpeningBalance(lub.getEmpid(), "EL", getServerDoe());
                leaveApplyDAO.getLeaveOpeningBalance(lub.getEmpid(), "CL", getServerDoe());
                leaveApplyDAO.getLeaveOpeningBalance(lub.getEmpid(), "HPL", getServerDoe());
                //LeaveBalanceBean balanceBean = leaveapplyDAO.getLeaveBalanceInfo(lub.getEmpid());
                model.put("elBalance", leaveApplyDAO.getLeaveBalanceInfo(lub.getEmpid(), "EL", "2016"));
                model.put("clBalance", leaveApplyDAO.getLeaveBalanceInfo(lub.getEmpid(), "CL", "2016"));
                model.put("hplBalance", leaveApplyDAO.getLeaveBalanceInfo(lub.getEmpid(), "HPL", "2016"));
                model.put("colBalance", leaveApplyDAO.getLeaveBalanceInfo(lub.getEmpid(), "COL", "2016"));
                path = "/leaveapply/LeaveApplyList";
            } else {
                ifexist = leaveApplyDAO.getIfLeaveRecordExist(leave.getHidempId(), leave.getSltleaveType(), leave.getTxtperiodFrom(), leave.getTxtperiodTo());
                if (ifexist == false) {
                    model.put("errors", "Leave already applied for this period");
                    path = "/leaveapply/LeaveApplyEdit";
                } else if (ifMoreThanMaxPeriod == false) {
                    model.put("errors1", "MAXIMUM " + leaveType + " MAY BE GRANTED AT A TIME TO A GOVERNMENT EMPLOYEE CAN NOT BE MORE THAN " + maxPeriodCnt + " DAYS");
                    path = "/leaveapply/LeaveApplyEdit";
                } else if (ifMaxChild == false) {
                    model.put("errors2", "MAXIMUM TWO SURVIVING CHILDREN PARENT CAN APPLY FOR MATERNITY/PATERNITY LEAVE");
                    path = "/leaveapply/LeaveApplyEdit";
                } else {
                    leaveApplyDAO.saveLeave(leave);
                    //    empleave.addLeave(lb,con);
                    leaveApplyDAO.getLeaveOpeningBalance(lub.getEmpid(), "EL", getServerDoe());
                    leaveApplyDAO.getLeaveOpeningBalance(lub.getEmpid(), "CL", getServerDoe());
                    leaveApplyDAO.getLeaveOpeningBalance(lub.getEmpid(), "HPL", getServerDoe());
                    //LeaveBalanceBean balanceBean = leaveapplyDAO.getLeaveBalanceInfo(lub.getEmpid());
                    model.put("elBalance", leaveApplyDAO.getLeaveBalanceInfo(lub.getEmpid(), "EL", "2016"));
                    model.put("clBalance", leaveApplyDAO.getLeaveBalanceInfo(lub.getEmpid(), "CL", "2016"));
                    model.put("hplBalance", leaveApplyDAO.getLeaveBalanceInfo(lub.getEmpid(), "HPL", "2016"));
                    model.put("colBalance", leaveApplyDAO.getLeaveBalanceInfo(lub.getEmpid(), "COL", "2016"));
                    path = "/leaveapply/LeaveApplyList";
                }
            }
//         if (ifMoreThanMaxPeriod == false) {
//             System.out.println("*************"+ifMoreThanMaxPeriod);
//                model.put("errors1"," You can not apply more than ");
//                path = "/leaveapply/LeaveApplyEdit";
//            }
            model.put("leaveForm", leave);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    @RequestMapping(value = "leaveViewData.htm")
    public ModelAndView getLeaveData(@ModelAttribute("LoginUserBean") LoginUserBean lub, @RequestParam("taskId") String taskId, @ModelAttribute("leaveForm") Leave leave, Map<String, Object> model) throws Exception {
        String authorityEmpCode = null;
        ModelAndView mav = new ModelAndView();
        ArrayList workFlowDtls = null;
        if (taskId != null && !taskId.equals("")) {
            leave.setHidTaskId(taskId);
        } else {
            leave.setHidTaskId(leave.getHidTaskId());
        }
        if (taskId != null && !taskId.equals("")) {
            leave = leaveApplyDAO.getLeaveData(taskId, lub.getEmpid(), lub.getSpc());
            authorityEmpCode = leaveApplyDAO.getAuthorityEmpCode(taskId);
            //leave.setLeaveFlowList(leaveApplyDAO.getLeaveWorkFlowDtls(taskId));
            workFlowDtls = leaveApplyDAO.getLeaveWorkFlowDtls(taskId);
        }
        if (leave.getHidTaskId() != null && !leave.getHidTaskId().equals("")) {
            leave = leaveApplyDAO.getLeaveData(taskId, lub.getEmpid(), lub.getSpc());
            authorityEmpCode = leaveApplyDAO.getAuthorityEmpCode(taskId);
        }
        if (lub.getEmpid().equals(authorityEmpCode)) {
            leave.setPassString("Task");
        } else {
            leave.setPassString("Own");
        }
        leave.setStatusId(leaveApplyDAO.getStatusId(taskId));
        leave.setFileArrList(leaveApplyDAO.getFileName(taskId, "M"));
        leave.setJoinFileArrList(leaveApplyDAO.getFileName(taskId, "N"));
        model.put("elBalance", leaveApplyDAO.getLeaveBalanceInfo(leave.getHidempId(), "EL", "2016"));
        model.put("clBalance", leaveApplyDAO.getLeaveBalanceInfo(leave.getHidempId(), "CL", "2016"));
        model.put("hplBalance", leaveApplyDAO.getLeaveBalanceInfo(leave.getHidempId(), "HPL", "2016"));
        model.put("colBalance", leaveApplyDAO.getLeaveBalanceInfo(leave.getHidempId(), "COL", "2016"));
        model.put("curdate", getServerDoe());
        mav.addObject("leaveForm", leave);
        mav.addObject("work", workFlowDtls);
        mav.setViewName("leaveapply/LeaveApplyView");
        return mav;
    }

    @RequestMapping(value = "leaveViewData.htm", method = RequestMethod.POST, params = "Back")
    public String back(@ModelAttribute("LoginUserBean") LoginUserBean lub, @ModelAttribute("leaveForm") Leave leave, Map<String, Object> model
    ) {
        return "redirect:leaveapply.htm";
    }

    @RequestMapping(value = "leaveapplyedit.htm", method = RequestMethod.POST, params = "Back")
    public String backButton(@ModelAttribute("LoginUserBean") LoginUserBean lub, @ModelAttribute("leaveForm") Leave leave, Map<String, Object> model
    ) {
        return "redirect:leaveapply.htm";
    }

    @RequestMapping(value = "leaveViewData.htm", method = RequestMethod.POST, params = "TakeAction")
    public ModelAndView takeAction(@ModelAttribute("LoginUserBean") LoginUserBean lub, @ModelAttribute("leaveForm") Leave leave, Map<String, Object> model) {
        ModelAndView mav = new ModelAndView();
        try {
            leave.setHidempId(lub.getEmpid());
            leave.setHidSpcCode(lub.getSpc());
            if (leave.getSltActionType().equals("1")) {
                leaveApplyDAO.updateApproveDate(leave);
            }
            if (!leave.getSltActionType().equals("1")) {
                leaveApplyDAO.updateTaskList(leave);
            }
            String appEmpName = leaveApplyDAO.getApplicant(leave.getHidTaskId());
            mav.addObject("empName", appEmpName);

            if (leave.getSltActionType().equals("2") || leave.getSltActionType().equals("0") || leave.getSltActionType().equals("4")) {
                mav.setViewName("leaveapply/LeaveViewAction");
            }
            if (leave.getSltActionType().equals("1") || leave.getSltActionType().equals("5")) {
                mav.setViewName("leaveapply/GenerateLeaveOrdEdit");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mav;
    }

    @RequestMapping(value = "leaveViewData.htm", method = RequestMethod.POST, params = "Submit")
    public ModelAndView submitLeaveDoc(@ModelAttribute("LoginUserBean") LoginUserBean lub, @ModelAttribute("leaveForm") Leave leave, Map<String, Object> model) {
        ModelAndView mav = new ModelAndView();
        NotificationBean nb = new NotificationBean();
        LeaveEntrytakenBean leb = new LeaveEntrytakenBean();
        LeaveSancBean lsb = new LeaveSancBean();
        DateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        Date orddate = null;
        int noOfDays = 0;
        String notId = "";
        try {
            leave.setLoginUser(lub.getEmpid());
            leave.setHidSpcCode(lub.getSpc());
            if (leave.getSltActionType() != null && !leave.getSltActionType().equals("")) {
                leave.setStatusId(leave.getSltActionType());
            } else {
                leave.setStatusId(leave.getStatusId());
            }
            if (leave.getTxtOrdDate() != null && !leave.getTxtOrdDate().equals("")) {
                orddate = df.parse(leave.getTxtOrdDate());
            }

            Date servdate = df.parse(getServerDoe());
//             
            nb.setNottype("LEAVE");
//            nb.setEmpId(lub.getEmpid());
            nb.setDateofEntry(servdate);
            nb.setOrdno(leave.getTxtOrdNo());
            nb.setOrdDate(orddate);

            leb = leaveApplyDAO.getEntryTaken(lub.getEmpid());
            nb.setEntryDeptCode(leb.getDeptCode());
            nb.setEntryOffCode(leb.getOffcode());
            nb.setEntryAuthCode(lub.getSpc());

            lsb = leaveApplyDAO.getLeaveSancInfo(leave.getHidTaskId());
            nb.setSancDeptCode(lsb.getDeptCode());
            nb.setSancOffCode(lsb.getOffCode());
            nb.setSancAuthCode(lsb.getAuthCode());
            nb.setEmpId(lsb.getInitiatedEmpId());
//            System.out.println("----" + lub.getDeptcode());
            if (leave.getStatusId().equals("4")) {
                notId = notificationDao.insertNotificationData(nb);
            }
            leave.setEmpId(lsb.getInitiatedEmpId());
            leave.setTollid(lsb.getTolId());
            leave.setTxtApproveFrom(lsb.getFromDate());
            leave.setTxtApproveTo(lsb.getToDate());
            leave.setTxtprefixFrom(lsb.getPrefixFrom());
            leave.setTxtprefixTo(lsb.getPrefixTo());
            leave.setTxtsuffixFrom(lsb.getSuffixeFrom());
            leave.setTxtsuffixTo(lsb.getSuffixeTo());
            leave.setAuthPost(nb.getSancAuthCode());
            if (leave.getStatusId().equals("4")) {
                leaveApplyDAO.updateEmpleave(leave, notId);
            }

            leaveApplyDAO.updateLeaveOrder(leave);

            if (leave.getStatusId().equals("41")) {
                noOfDays = leaveApplyDAO.calculateDateDiff(leave.getTxtApproveFrom(), leave.getTxtApproveTo(), leave.getEmpId(), leave.getTollid());
                if (leave.getTollid().equals("CL")) {
                    leaveApplyDAO.updateClLeaveBalance(leave.getEmpId(), leave.getTollid(), noOfDays);
                }
                if (leave.getTollid().equals("EL")) {
                    leaveApplyDAO.updateElLeaveBalance(leave.getEmpId(), leave.getTollid(), noOfDays);
                }
                if (leave.getTollid().equals("HPL")) {
                    leaveApplyDAO.updateHplLeaveBalance(leave.getEmpId(), leave.getTollid(), noOfDays);
                }
                if (leave.getTollid().equals("COL")) {
                    leaveApplyDAO.updateCommutedLeaveBalance(leave.getEmpId(), leave.getTollid(), noOfDays);
                }
                if (leave.getTollid().equals("ML")) {
                    leaveApplyDAO.updateMaternityLeaveBalance(leave.getEmpId(), leave.getTollid(), noOfDays);
                }
                if (leave.getTollid().equals("PL")) {
                    leaveApplyDAO.updatePaternityLeaveBalance(leave.getEmpId(), leave.getTollid(), noOfDays);
                }
            }
            mav.addObject("leaveForm", leave);
            mav.setViewName("redirect:leaveViewData.htm?taskId=" + leave.getHidTaskId());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mav;
    }

    @RequestMapping(value = "leaveViewData.htm", method = RequestMethod.POST, params = "Print")
    public void viewLeavePdf(@ModelAttribute("LoginUserBean") LoginUserBean lub, HttpServletResponse response, @ModelAttribute("leaveForm") Leave leave) {

        response.setContentType("application/pdf");
        Document document = new Document(PageSize.A4);
        try {
            response.setHeader("Content-Disposition", "attachment; filename=Leave_" + lub.getEmpid() + ".pdf");
            leave = leaveApplyDAO.getLeaveData(leave.getHidTaskId(), lub.getEmpid(), lub.getSpc());
            PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
            Rectangle rect = new Rectangle(30, 30, 500, 800);
            writer.setBoxSize("art", rect);
            document.open();
            if (leave.getStatusId().equals("4")) {
                leaveApplyDAO.viewPDFfunc(document, leave, lub.getEmpid());
            }
            if (leave.getStatusId().equals("5")) {
                leaveApplyDAO.viewAllowedPDFfunc(document, leave, lub.getEmpid());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
    }

    @RequestMapping(value = "joiningData.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public void joiningData(@RequestParam("taskId") String taskId, @ModelAttribute("LoginUserBean") LoginUserBean lub, @ModelAttribute("leaveForm") Leave leave, Map<String, Object> model, HttpServletResponse response) {
        response.setContentType("application/json");
        PrintWriter out = null;
        try {

            leave = leaveApplyDAO.getLeaveData(taskId, lub.getEmpid(), lub.getSpc());
            leave.setTxtSancAuthority(leave.getPendingPostWthName());
            leave.setFileArrList(leaveApplyDAO.getFileName(taskId, "N"));
            JSONObject job = new JSONObject(leave);
            out = response.getWriter();
            out.write(job.toString());
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "saveJoiningData.htm", method = RequestMethod.POST)
    public void saveJoiningData(@ModelAttribute("LoginUserBean") LoginUserBean lub, @ModelAttribute("leaveForm") Leave leave, BindingResult result, HttpServletResponse response) throws IOException {
        try {
            response.setContentType("application/json");
            PrintWriter out = null;
            leaveApplyDAO.saveJoinData(leave);
            JSONObject job = new JSONObject();
            out = response.getWriter();
            out.write(job.toString());
        } catch (Exception e) {
            e.printStackTrace();;
        } finally {

        }
    }

}
