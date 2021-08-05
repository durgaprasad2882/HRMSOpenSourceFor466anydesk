package hrms.controller.relieve;

import hrms.common.CommonFunctions;
import hrms.dao.master.DepartmentDAO;
import hrms.dao.master.OfficeDAO;
import hrms.dao.master.PayScaleDAO;
import hrms.dao.master.SubStantivePostDAO;
import hrms.dao.notification.NotificationDAO;
import hrms.dao.relieve.RelieveDAO;
import hrms.model.login.LoginUserBean;
import hrms.model.login.Users;
import hrms.model.relieve.RelieveForm;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
public class RelieveController {

    @Autowired
    public DepartmentDAO deptDAO;

    @Autowired
    public OfficeDAO offDAO;

    @Autowired
    public SubStantivePostDAO substantivePostDAO;

    @Autowired
    public PayScaleDAO payscaleDAO;

    @Autowired
    public RelieveDAO reliveDao;

    @Autowired
    public NotificationDAO notificationDao;

    @RequestMapping(value = "RelieveList")
    public ModelAndView RelieveListPage(@ModelAttribute("SelectedEmpObj") Users lub,@ModelAttribute("rlvForm") RelieveForm rlvForm) {
        
        List relievelist = null;
        
        ModelAndView mav = null;
        try{
            System.out.println("EMP ID inside Relieve List is: "+lub.getEmpId());
            rlvForm.setEmpid(lub.getEmpId());
            relievelist = reliveDao.getRelieveList(rlvForm.getEmpid());
            
            mav = new ModelAndView("/relieve/RelieveList", "rlvForm", rlvForm);
            mav.addObject("relievelist", relievelist);
        }catch(Exception e){
            e.printStackTrace();
        }
        return mav;
   }

    @RequestMapping(value = "entryRelieve")
    public ModelAndView entryRelieve(@ModelAttribute("SelectedEmpObj") Users lub, @RequestParam("notId") String notId, @RequestParam("rlvId") String rlvId) throws IOException {

        ModelAndView mav = null;
        
        RelieveForm rlvForm = null;
        try {
            
            String empid = CommonFunctions.decodedTxt(lub.getEmpId());
            System.out.println("EMP ID inside Relieve Edit is: "+empid);
            rlvForm = reliveDao.getRelieveData(empid, notId, rlvId);
            List relievedPostlist = reliveDao.getRelievedPostList(empid);
            List addlChargeList = reliveDao.getAddlChargeList(empid);
            
            mav = new ModelAndView("/relieve/AddRelieve", "rlvForm", rlvForm);
            mav.addObject("relievedPostlist", relievedPostlist);
            mav.addObject("addlChargeList", addlChargeList);
            
            List deptlist = deptDAO.getDepartmentList();
            mav.addObject("deptlist", deptlist);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mav;
    }

    @RequestMapping(value = "relieveFromPostListJSON")
    public void relieveFromPostListJSON(HttpServletResponse response, @ModelAttribute("SelectedEmpObj") Users lub) {

        response.setContentType("application/json");
        PrintWriter out = null;

        List postlist = null;
        try {
            postlist = reliveDao.getRelievedPostList(lub.getEmpId());

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
    
    @RequestMapping(value = "relieveAddlChargeListJSON")
    public void relieveAddlChargeListJSON(HttpServletResponse response, @ModelAttribute("SelectedEmpObj") Users lub) {

        response.setContentType("application/json");
        PrintWriter out = null;

        List addlChrgelist = null;
        try {
            addlChrgelist = reliveDao.getAddlChargeList(lub.getEmpId());

            JSONArray json = new JSONArray(addlChrgelist);
            out = response.getWriter();
            out.write(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
        }
    }

    @RequestMapping(value = "saveRelieve")
    public ModelAndView saveRelieve(HttpServletResponse response, @ModelAttribute("LoginUserBean") LoginUserBean lub1, @ModelAttribute("rlvForm") RelieveForm rlvForm) {
        
        ModelAndView mav = null;
        List relievelist = null;
        try {
            //System.out.println("Relieve, Department Code is: "+rlvForm.getAuthHidDeptCode());
            //System.out.println("Relieve, Off Code is: "+rlvForm.getAuthHidOffCode());
            //System.out.println("Relieve, SPC is: "+rlvForm.getAuthSpc());
            reliveDao.saveRelieve(rlvForm, lub1.getDeptcode(), lub1.getOffcode(), lub1.getSpc());
            
            relievelist = reliveDao.getRelieveList(rlvForm.getEmpid());
            
            //mav = new ModelAndView("/relieve/RelieveList", "rlvForm", rlvForm);
            //mav.addObject("relievelist", relievelist);
            
            return new ModelAndView("redirect:/TransferList.htm");
        } catch (Exception e) {
            e.printStackTrace();
        }
      return mav;  
    }

    @RequestMapping(value = "deleteRelieve")
    public void deleteRelieve(HttpServletResponse response, @ModelAttribute("SelectedEmpObj") Users lub, @ModelAttribute("relieveForm") RelieveForm er) {

        try {
            //er.setEmployeeid(lub.getEmpid());
            reliveDao.deleteRelieve(lub.getEmpId(),er);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
