package hrms.controller.joining;

import hrms.SelectOption;
import hrms.common.Message;
import hrms.dao.joining.JoiningDAO;
import hrms.dao.master.DepartmentDAO;
import hrms.dao.master.OfficeDAO;
import hrms.dao.master.PayScaleDAO;
import hrms.dao.master.SubStantivePostDAO;
import hrms.model.joining.JoiningForm;
import hrms.model.login.LoginUserBean;
import hrms.model.login.Users;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
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

@Controller
@SessionAttributes({"LoginUserBean", "SelectedEmpObj"})
public class JoiningController {

    @Autowired
    public DepartmentDAO deptDAO;

    @Autowired
    public OfficeDAO offDAO;

    @Autowired
    public SubStantivePostDAO substantivePostDAO;

    @Autowired
    public PayScaleDAO payscaleDAO;

    @Autowired
    public JoiningDAO joiningDAO;

    @RequestMapping(value = "JoiningList")
    public ModelAndView JoiningListPage(@ModelAttribute("SelectedEmpObj") Users lub,@ModelAttribute("jForm") JoiningForm jForm) {

        List joininglist = null;
        
        ModelAndView mav = null;
        try{
            System.out.println("EMP ID inside Joining List is: "+lub.getEmpId());
            jForm.setJempid(lub.getEmpId());
            joininglist = joiningDAO.getJoiningList(jForm.getJempid());
            
            mav = new ModelAndView("/joining/JoiningList", "jForm", jForm);
            mav.addObject("joininglist", joininglist);
        }catch(Exception e){
            e.printStackTrace();
        }
        return mav;
    }

    @RequestMapping(value = "enterJoiningData")
    public ModelAndView enterJoiningData(@ModelAttribute("LoginUserBean") LoginUserBean lub1,@ModelAttribute("SelectedEmpObj") Users lub, @RequestParam("notId") String notId, @RequestParam("rlvId") String rlvId,@RequestParam("leaveId") String leaveid,@RequestParam("addl") String addl,@RequestParam("jId") String jId) throws IOException {

        ModelAndView mav = null;

        JoiningForm joinform = null;
        try {
            System.out.println("Entry SPC inside Edit is: "+lub1.getSpc());
            joinform = joiningDAO.getJoiningData(lub.getEmpId(),notId,rlvId,leaveid,addl,jId);
            
            mav = new ModelAndView("/joining/AddJoining", "jForm", joinform);
            
            List deptlist = deptDAO.getDepartmentList();
            mav.addObject("deptlist", deptlist);
            
            List fieldofflist = offDAO.getFieldOffList(lub.getOffcode());
            mav.addObject("fieldofflist", fieldofflist);
            mav.addObject("entpsc", lub1.getSpc());
        } catch (Exception e) {
            e.printStackTrace();
        }
      return mav;  
    }

    @RequestMapping(value = "saveEmployeeJoining")
    public ModelAndView saveEmployeeJoining(HttpServletResponse response, @ModelAttribute("LoginUserBean") LoginUserBean lub1, @ModelAttribute("jForm") JoiningForm joinform) {
        
        ModelAndView mav = null;
        
        List joininglist = null;

        try {
            System.out.println("Joined SPC inside save is: "+joinform.getJspc());
            System.out.println("Entry SPC inside Save is: "+lub1.getSpc());
            joiningDAO.saveJoining(joinform,lub1.getDeptcode(),lub1.getOffcode(),lub1.getSpc());
            
            joininglist = joiningDAO.getJoiningList(joinform.getJempid());
            
            mav = new ModelAndView("/joining/JoiningList", "jForm", joinform);
            mav.addObject("joininglist", joininglist);
        } catch (Exception e) {
            e.printStackTrace();
        }
      return mav;  
    }

    @RequestMapping(value = "deleteJoining")
    public void deleteJoining(HttpServletResponse response, @ModelAttribute("SelectedEmpObj") Users lub, @ModelAttribute("joiningForm") JoiningForm joinform) {

        try {
            joinform.setJempid(lub.getEmpId());
            joiningDAO.deleteJoining(joinform);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @ResponseBody
    @RequestMapping(value = "joiningGetFieldOffListJSON")
    public void GetFieldOffListJSON(HttpServletResponse response,@ModelAttribute("LoginUserBean") LoginUserBean lub,@RequestParam("offcode") String offcode) {

        response.setContentType("application/json");
        PrintWriter out = null;

        List fieldofflist = null;
        try {
            if(offcode != null && !offcode.equals("")){
                System.out.println("Off Code is: "+offcode);
                fieldofflist = offDAO.getFieldOffList(offcode);
            }else{
                System.out.println("Cur Off Code is: "+lub.getOffcode());
                fieldofflist = offDAO.getFieldOffList(lub.getOffcode());
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
    @RequestMapping(value = "joiningGetGenericPostListJSON")
    public void joiningGetGenericPostListJSON(HttpServletResponse response,@RequestParam("offcode") String offcode) {

        response.setContentType("application/json");
        PrintWriter out = null;

        List genericpostlist = null;
        try {
            
            genericpostlist = substantivePostDAO.getGenericPostList(offcode);
            
            JSONArray json = new JSONArray(genericpostlist);
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
    @RequestMapping(value = "joiningGetGPCWiseSPCListJSON")
    public void joiningGetGPCWiseSPCListJSON(HttpServletResponse response,@ModelAttribute("SelectedEmpObj") Users lub,@RequestParam("offcode") String offcode,@RequestParam("gpc") String gpc) {

        response.setContentType("application/json");
        PrintWriter out = null;

        List gpcwisespclist = null;
        try {
            System.out.println("Off Code is: "+offcode);
            System.out.println("GPC is: "+gpc);
            gpcwisespclist = substantivePostDAO.getGPCWiseSPCList(lub.getEmpId(), offcode, gpc);
            
            System.out.println("List size is: "+gpcwisespclist.size());
            
            JSONArray json = new JSONArray(gpcwisespclist);
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
