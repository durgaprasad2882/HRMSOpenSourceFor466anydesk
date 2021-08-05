package hrms.controller.deputation;

import hrms.common.Message;
import hrms.dao.deputation.DeputationDAO;
import hrms.dao.login.LoginDAOImpl;
import hrms.dao.master.OfficeDAO;
import hrms.dao.notification.NotificationDAO;
import hrms.dao.transfer.TransferDAO;
import hrms.model.deputation.DeputationDataForm;
import hrms.model.login.LoginUserBean;
import hrms.model.notification.NotificationBean;
import hrms.model.transfer.TransferForm;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@SessionAttributes({"LoginUserBean", "SelectedEmpObj"})
public class DeputationController {

    @Autowired
    public OfficeDAO offDAO;

    @Autowired
    public DeputationDAO deputationDAO;

    @Autowired
    public NotificationDAO notificationDao;

    @Autowired
    public TransferDAO transferDao;
    
    @Autowired
    public LoginDAOImpl loginDao;
    
    @RequestMapping(value = "Deputation")
    public String DeputationList(@ModelAttribute("SelectedEmpObj") LoginUserBean lub) {
        System.out.println("EMPID inside Deputation is: " + lub.getEmpid());
        return "/deputation/Deputation";
    }

    @RequestMapping(value = "GetDeputationListJSON")
    public void GetDeputationListJSON(HttpServletResponse response, @ModelAttribute("SelectedEmpObj") LoginUserBean lub, @RequestParam("page") int page) {

        response.setContentType("application/json");
        JSONObject json = new JSONObject();
        PrintWriter out = null;

        List deputationlist = null;
        int deputationlistCnt = 0;

        int maxlimit = 10;
        int minlimit = maxlimit * (page - 1);
        try {
            deputationlist = deputationDAO.getDeputationList(lub.getEmpid(), minlimit, maxlimit);
            deputationlistCnt = deputationDAO.getDeputationListCount(lub.getEmpid());

            json.put("total", deputationlistCnt);
            json.put("rows", deputationlist);
            out = response.getWriter();
            out.write(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
        }
    }

    @RequestMapping(value = "saveDeputation")
    public void saveDeputation(HttpServletResponse response, @ModelAttribute("LoginUserBean") LoginUserBean lub1, @ModelAttribute("SelectedEmpObj") LoginUserBean lub, @ModelAttribute("deputationForm") DeputationDataForm deputationForm) throws ParseException {

        response.setContentType("application/json");
        PrintWriter out = null;

        NotificationBean nb = new NotificationBean();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");

        Message msg = null;

        TransferForm transferForm = new TransferForm();
        try {
            deputationForm.setEmpid(lub.getEmpid());

            System.out.println("EMP ID for Save Deputation is: " + lub.getEmpid());

            nb.setNottype("DEPUTATION");
            nb.setEmpId(deputationForm.getEmpid());
            nb.setDateofEntry(new Date());
            nb.setOrdno(deputationForm.getTxtNotOrdNo());
            nb.setOrdDate(sdf.parse(deputationForm.getTxtNotOrdDt()));
            nb.setSancDeptCode(deputationForm.getHidNotifyingDeptCode());
            nb.setSancOffCode(deputationForm.getHidNotifyingOffCode());
            nb.setSancAuthCode(deputationForm.getNotifyingSpc());
            nb.setEntryDeptCode(lub1.getDeptcode());
            nb.setEntryOffCode(lub1.getOffcode());
            nb.setEntryAuthCode(lub1.getSpc());
            nb.setNote(deputationForm.getNote());
            if (deputationForm.getHidNotId() != null && !deputationForm.getHidNotId().trim().equals("")) {
                nb.setNotid(deputationForm.getHidNotId());
                notificationDao.modifyNotificationData(nb);
                msg = deputationDAO.updateDeputation(deputationForm);

                if (deputationForm.getHidTransferId() != null && !deputationForm.getHidTransferId().equals("")) {
                    transferForm.setTransferId(deputationForm.getHidTransferId());
                    transferForm.setHnotType("DEPUTATION");
                    transferForm.setEmpid(deputationForm.getEmpid());
                    transferForm.setHidPostedDeptCode(deputationForm.getHidPostedDeptCode());
                    transferForm.setHidPostedOffCode(deputationForm.getHidPostedOffCode());
                    transferForm.setPostedspc(deputationForm.getPostedSpc());
                    transferForm.setSltPostedFieldOff(deputationForm.getSltFieldOffice());
                    transferDao.updateTransfer(transferForm);
                }
            } else {
                String notid = notificationDao.insertNotificationData(nb);
                msg = deputationDAO.saveDeputation(deputationForm, notid);

                transferForm.setHnotType("DEPUTATION");
                transferForm.setEmpid(deputationForm.getEmpid());
                transferForm.setHidPostedDeptCode(deputationForm.getHidPostedDeptCode());
                transferForm.setHidPostedOffCode(deputationForm.getHidPostedOffCode());
                transferForm.setPostedspc(deputationForm.getPostedSpc());
                transferForm.setSltPostedFieldOff(deputationForm.getSltFieldOffice());
                transferDao.saveTransfer(transferForm, notid);
            }
            
            loginDao.updateCadreStatus(deputationForm.getEmpid(), deputationForm.getSltCadreStatus(), deputationForm.getSltSubCadreStatus());
            
            JSONObject job = new JSONObject(msg);
            out = response.getWriter();
            out.write(job.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "editDeputation")
    public void editDeputation(HttpServletResponse response, @ModelAttribute("SelectedEmpObj") LoginUserBean lub, @RequestParam Map<String, String> requestParams) throws IOException {

        response.setContentType("application/json");
        PrintWriter out = null;
        DeputationDataForm dptnform = null;
        try {
            String empid = lub.getEmpid();
            System.out.println("EMP ID inside Deputation edit is: " + lub.getEmpid());
            String notificationId = requestParams.get("notId");

            dptnform = deputationDAO.getEmpDeputationData(empid, notificationId);

        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject job = new JSONObject(dptnform);
        out = response.getWriter();
        out.write(job.toString());
    }

    @ResponseBody
    @RequestMapping(value = "deputationGetFieldOffListJSON", method = RequestMethod.POST)
    public void GetFieldOffListJSON(HttpServletResponse response, @RequestParam("offcode") String offcode) {

        response.setContentType("application/json");
        PrintWriter out = null;

        List fieldofflist = null;
        try {
            if (offcode != null && !offcode.equals("")) {
                System.out.println("Off Code is: " + offcode);
                fieldofflist = offDAO.getFieldOffList(offcode);
            }

            JSONArray json = new JSONArray(fieldofflist);
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
    @RequestMapping(value = "deputationGetCadreStatusListJSON", method = RequestMethod.POST)
    public void GetCadreStatusListJSON(HttpServletResponse response) {

        response.setContentType("application/json");
        PrintWriter out = null;

        List cadrestatuslist = null;
        try {
            cadrestatuslist = deputationDAO.getCadreStatusList("DEP");

            JSONArray json = new JSONArray(cadrestatuslist);
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
    @RequestMapping(value = "deputationGetSubCadreStatusListJSON", method = RequestMethod.POST)
    public void GetSubCadreStatusListJSON(HttpServletResponse response, @RequestParam("cadrestat") String cadrestat) {

        response.setContentType("application/json");
        PrintWriter out = null;

        List cadrestatuslist = null;
        try {
            cadrestatuslist = deputationDAO.getSubCadreStatusList(cadrestat);

            JSONArray json = new JSONArray(cadrestatuslist);
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
