package hrms.controller.transfer;

import hrms.common.CommonFunctions;
import hrms.common.Message;
import hrms.dao.master.DepartmentDAO;
import hrms.dao.master.OfficeDAO;
import hrms.dao.master.PayScaleDAO;
import hrms.dao.master.SubStantivePostDAO;
import hrms.dao.notification.NotificationDAO;
import hrms.dao.transfer.TransferDAO;
import hrms.model.login.LoginUserBean;
import hrms.model.login.Users;
import hrms.model.notification.NotificationBean;
import hrms.model.transfer.TransferForm;
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
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

@Controller
//@SessionAttributes("LoginUserBean")
@SessionAttributes({"LoginUserBean", "SelectedEmpObj"})
public class TransferController {

    @Autowired
    public DepartmentDAO deptDAO;

    @Autowired
    public OfficeDAO offDAO;

    @Autowired
    public SubStantivePostDAO substantivePostDAO;

    @Autowired
    public PayScaleDAO payscaleDAO;

    @Autowired
    public TransferDAO transferDao;

    @Autowired
    public NotificationDAO notificationDao;

    @RequestMapping(value = "TransferList")
    public ModelAndView TransferList(@ModelAttribute("SelectedEmpObj") Users u, @ModelAttribute("transferForm") TransferForm transferForm) {

        List transferlist = null;

        ModelAndView mav = null;
        try {
            transferForm.setEmpid(CommonFunctions.decodedTxt(u.getEmpId()));
            System.out.println("EMP ID inside Transfer is: " + transferForm.getEmpid());
            transferlist = transferDao.getTransferList(transferForm.getEmpid());

            mav = new ModelAndView("/transfer/TransferList", "transferForm", transferForm);
            mav.addObject("transferlist", transferlist);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mav;
    }

    @RequestMapping(value = "newTransfer")
    public ModelAndView NewTransfer(HttpServletResponse response, @ModelAttribute("LoginUserBean") LoginUserBean lub, @ModelAttribute("transferForm") TransferForm transferForm) throws IOException {

        ModelAndView mav = null;

        try {
            System.out.println("EMP ID inside New Transfer is: " + transferForm.getEmpid());
            mav = new ModelAndView("/transfer/NewTransfer", "transferForm", transferForm);

            List deptlist = deptDAO.getDepartmentList();
            List payscalelist = payscaleDAO.getPayScaleList();
            mav.addObject("deptlist", deptlist);
            mav.addObject("payscalelist", payscalelist);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mav;
    }

    @RequestMapping(value = "saveTransfer")
    public ModelAndView saveTransfer(HttpServletResponse response, Model model, @ModelAttribute("transferForm") TransferForm transferform, @RequestParam("submit") String submit) throws ParseException {

        NotificationBean nb = new NotificationBean();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");

        ModelAndView mav = null;
        List transferlist = null;
        try {
            if (submit != null && submit.equals("Save")) {
                nb.setNottype("TRANSFER");
                nb.setEmpId(transferform.getEmpid());
                nb.setDateofEntry(new Date());
                nb.setOrdno(transferform.getTxtNotOrdNo());
                nb.setOrdDate(sdf.parse(transferform.getTxtNotOrdDt()));
                nb.setSancDeptCode(transferform.getHidAuthDeptCode());
                nb.setSancOffCode(transferform.getHidAuthOffCode());
                nb.setSancAuthCode(transferform.getAuthSpc());
                nb.setNote(transferform.getNote());
                System.out.println("TR Id is: " + transferform.getTransferId());
                if (transferform.getHnotid() != null && !transferform.getHnotid().trim().equals("")) {

                    nb.setNotid(transferform.getHnotid());
                    notificationDao.modifyNotificationData(nb);
                    transferDao.updateTransfer(transferform);
                } else {
                    String notid = notificationDao.insertNotificationData(nb);
                    transferDao.saveTransfer(transferform, notid);
                }
            } else if (submit != null && submit.equals("Delete")) {
                System.out.println("Inside Delete");
                int retVal = notificationDao.deleteNotificationData(transferform.getHnotid(), "TRANSFER");
                System.out.println("retval is: " + retVal);
                if (retVal > 0) {
                    transferDao.deleteTransfer(transferform);
                }
            }

            transferlist = transferDao.getTransferList(transferform.getEmpid());
            mav = new ModelAndView("/transfer/TransferList", "transferForm", transferform);
            mav.addObject("transferlist", transferlist);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mav;
    }

    @RequestMapping(value = "editTransfer")
    public ModelAndView editTransfer(HttpServletResponse response, @ModelAttribute("LoginUserBean") LoginUserBean lub, @ModelAttribute("SelectedEmpObj") Users u, Model model, @RequestParam Map<String, String> requestParams) throws IOException {

        TransferForm trform = new TransferForm();

        ModelAndView mav = null;

        try {
            trform.setEmpid(CommonFunctions.decodedTxt(u.getEmpId()));
            System.out.println("EMP ID inside edit is: " + trform.getEmpid());

            String transferId = requestParams.get("transferId");
            String notificationId = requestParams.get("notId");

            trform = transferDao.getEmpTransferData(trform, notificationId);

            trform.setEmpid(CommonFunctions.decodedTxt(u.getEmpId()));
            trform.setTransferId(transferId);
            trform.setHnotid(notificationId);

            mav = new ModelAndView("/transfer/NewTransfer", "transferForm", trform);

            List deptlist = deptDAO.getDepartmentList();
            List fieldofflist = offDAO.getFieldOffList(lub.getOffcode());
            List payscalelist = payscaleDAO.getPayScaleList();
            mav.addObject("deptlist", deptlist);
            mav.addObject("fieldofflist", fieldofflist);
            mav.addObject("payscalelist", payscalelist);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mav;
    }

    @RequestMapping(value = "TransferPostList")
    public String TransferPostList(ModelMap model, @RequestParam("type") String type, @RequestParam("deptCode") String deptCode, @RequestParam("offCode") String offCode) {
        try {
            model.addAttribute("type", type);
            model.addAttribute("deptCode", deptCode);
            model.addAttribute("offCode", offCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "/transfer/TransferPostList";
    }

    @ResponseBody
    @RequestMapping(value = "TransferPostListJSON")
    public void TransferPostListJSON(HttpServletResponse response, ModelMap model, @ModelAttribute("LoginUserBean") LoginUserBean lub) {

        response.setContentType("application/json");
        PrintWriter out = null;
        try {
            List postlist = transferDao.getPostList(lub.getDeptcode(), lub.getOffcode());

            JSONArray json = new JSONArray(postlist);
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
    @RequestMapping(value = "transferGetFieldOffListJSON")
    public void GetFieldOffListJSON(HttpServletResponse response, @RequestParam("offcode") String offcode) {

        response.setContentType("application/json");
        PrintWriter out = null;

        List fieldofflist = null;
        try {
            fieldofflist = offDAO.getFieldOffList(offcode);

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
    @RequestMapping(value = "transferGetPayscaleListJSON")
    public void GetPayscaleListJSON(HttpServletResponse response) {

        response.setContentType("application/json");
        PrintWriter out = null;

        List payscalelist = null;
        try {
            payscalelist = payscaleDAO.getPayScaleList();

            JSONArray json = new JSONArray(payscalelist);
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
    @RequestMapping(value = "getTransferCadreWisePostListJSON")
    public String GetTransferCadreWisePostListJSON(@ModelAttribute("SelectedEmpObj") Users u,@RequestParam("offcode") String offcode) {
        JSONArray json = null;
        try {
            String cadrecode = transferDao.getCadreCode(u.getEmpId());
            List spclist = substantivePostDAO.getCadreWisePostList(offcode,cadrecode);
            json = new JSONArray(spclist);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json.toString();
    }
}
