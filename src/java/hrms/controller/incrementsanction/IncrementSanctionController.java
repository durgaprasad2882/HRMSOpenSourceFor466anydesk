package hrms.controller.incrementsanction;

import hrms.common.CommonFunctions;
import hrms.common.Message;
import hrms.dao.incrementsanction.IncrementSanctionDAO;
import hrms.dao.master.DepartmentDAO;
import hrms.dao.master.PayScaleDAO;
import hrms.dao.notification.NotificationDAO;
import hrms.model.incrementsanction.IncrementForm;
import hrms.model.login.LoginUserBean;
import hrms.model.login.Users;
import hrms.model.notification.NotificationBean;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONException;
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
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;

@Controller
//@SessionAttributes("LoginUserBean")
@SessionAttributes({"LoginUserBean", "SelectedEmpObj"})
public class IncrementSanctionController implements ServletContextAware {

    private ServletContext context;

    @Autowired
    public DepartmentDAO departmentDao;

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.context = servletContext;
    }

    @Autowired
    public PayScaleDAO payscaleDAO;

    @Autowired
    public IncrementSanctionDAO incrementsancDAO;

    @RequestMapping(value = "IncrementSanctionList")
    public ModelAndView incrementlist(@ModelAttribute("incrementForm") IncrementForm inf) {
        ModelAndView mav = new ModelAndView("IncrementSanction/IncrementSanction");
        List li = departmentDao.getDeptList();
        mav.addObject("DepartmentList", li);
        mav.addObject("incrementForm", new IncrementForm());
        return mav;
    }

    @ResponseBody
    @RequestMapping(value = "GetIncrementSanctionListJSON")
    public void GetIncrementSanctionListJSON(HttpServletResponse response, @ModelAttribute("SelectedEmpObj") Users lub, @RequestParam("page") int page, @RequestParam("rows") int rows) {

        response.setContentType("application/json");
        JSONObject json = new JSONObject();
        PrintWriter out = null;

        List incrlist = null;
        int incrlistCnt = 0;

        int minlimit = rows * (page - 1);
        int maxlimit = rows;
        try {

            incrlist = incrementsancDAO.getIncrementList(lub.getEmpId(), minlimit, maxlimit);
            incrlistCnt = incrementsancDAO.getIncrementListCount(lub.getEmpId());

            json.put("total", incrlistCnt);
            json.put("rows", incrlist);
            out = response.getWriter();
            out.write(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
        }
    }

    @RequestMapping(value = "incrementGetPayscaleListJSON")
    public void GetPayscaleListJSON(HttpServletResponse response) {

        response.setContentType("application/json");
        PrintWriter out = null;

        List payscalelist = null;
        JSONArray json = null;
        try {
            payscalelist = payscaleDAO.getPayScaleList();

            json = new JSONArray(payscalelist);
            out = response.getWriter();
            out.write(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
        }
    }

    /*@RequestMapping(value = "addIncrement")
     public void addIncrement(@ModelAttribute("incrementForm") IncrementForm incrementform, @RequestParam Map<String, String> requestParams) {

     IncrementForm incfb = new IncrementForm();
     Format sdf = new SimpleDateFormat("dd-MMM-yyyy");
     try {
     if (requestParams.get("empid") != null && !requestParams.get("empid").equals("")) {
     incfb.setEmpid(requestParams.get("empid"));
     } else {
     incfb.setEmpid(incrementform.getEmpid());
     }
     } catch (Exception e) {
     e.printStackTrace();
     }
     }*/
    @RequestMapping(value = "saveIncrement")
    public void saveIncrement(HttpServletResponse response, @ModelAttribute("LoginUserBean") LoginUserBean lub, @ModelAttribute("SelectedEmpObj") Users lub1, @ModelAttribute("incrementForm") IncrementForm incrementform) {

        response.setContentType("application/json");
        PrintWriter out = null;

        Message msg = null;

        try {
            incrementform.setEmpid(lub1.getEmpId());

            if (incrementform.getHnotid() != null && !incrementform.getHnotid().trim().equals("")) {
                if (incrementform.getHidIncrId() != null && !incrementform.getHidIncrId().equalsIgnoreCase("")) {
                    msg = incrementsancDAO.updateIncrement(incrementform, lub.getDeptcode(), lub.getOffcode(), lub.getSpc());
                } else {
                    msg = incrementsancDAO.saveIncrement(incrementform, lub.getDeptcode(), lub.getOffcode(), lub.getSpc());
                }
            } else {
                msg = incrementsancDAO.saveIncrement(incrementform, lub.getDeptcode(), lub.getOffcode(), lub.getSpc());
            }
            JSONObject job = new JSONObject(msg);
            out = response.getWriter();
            out.write(job.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
        }
    }

    @RequestMapping(value = "editIncrement")
    public void editIncrement(HttpServletResponse response, @ModelAttribute("SelectedEmpObj") Users lub, @RequestParam Map<String, String> requestParams) {

        response.setContentType("application/json");
        PrintWriter out = null;

        IncrementForm inf = null;

        JSONObject job = null;
        try {
            String empid = lub.getEmpId();
            String incrementId = requestParams.get("incrId");
            String notificationId = requestParams.get("notId");

            inf = incrementsancDAO.getEmpIncRowData(empid, incrementId);
            inf.setHidIncrId(incrementId);
            inf.setHnotid(notificationId);

            job = new JSONObject(inf);
            out = response.getWriter();
            out.write(job.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
        }
    }

    @ResponseBody
    @RequestMapping(value = "deleteIncrement", method = RequestMethod.GET)
    public void deleteIncrement(HttpServletResponse response, @ModelAttribute("SelectedEmpObj") Users lub, @RequestParam Map<String, String> requestParams) {
        response.setContentType("application/json");
        PrintWriter out = null;
        try {
            
            IncrementForm inf = new IncrementForm();
            inf.setEmpid(lub.getEmpId());
            inf.setHidIncrId(requestParams.get("incrId"));
            inf.setHnotid(requestParams.get("notId"));
            boolean isDeleted = incrementsancDAO.deleteIncrement(inf);
            JSONObject obj = new JSONObject();
            obj.append("isDeleted", isDeleted);
            out = response.getWriter();
            out.write(obj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            out.close();
            out.flush();
        }
    }

}
