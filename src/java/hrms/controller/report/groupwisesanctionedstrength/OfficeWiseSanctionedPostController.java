package hrms.controller.report.groupwisesanctionedstrength;

import hrms.dao.report.annualestablishment.AnnuaiEstablishmentReportDAO;
import hrms.dao.report.officewisesanctionedstrength.OfficeWiseSanctionedStrengthDAO;
import hrms.model.login.LoginUserBean;
import hrms.model.report.officewisesanctionedstrength.OfficeWiseSanctionedStrengthBean;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

@Controller
@SessionAttributes("LoginUserBean")
public class OfficeWiseSanctionedPostController {

    @Autowired
    OfficeWiseSanctionedStrengthDAO officeWiseSanctionedStrengthDAO;

    @Autowired
    AnnuaiEstablishmentReportDAO annuaiEstablishmentDao;

    @RequestMapping(value = "getOfficeWiseSanctionedStrength")
    public String GetOfficeWiseSanctionedStrength(Model model,@ModelAttribute("LoginUserBean") LoginUserBean lub) {
        //ModelAndView mav = new ModelAndView();
        List data = officeWiseSanctionedStrengthDAO.getSanctionedStrengthList(lub.getOffcode());
        model.addAttribute("data", data);
        model.addAttribute("offName", lub.getOffname());
        //System.out.println("Office Name is: "+lub.getOffname());
        //mav = new ModelAndView("report/OfficeWiseSanctionedStrength");
        //mav.setViewName("report/OfficeWiseSanctionedStrength");
        return "report/OfficeWiseSanctionedStrength";
    }

    @RequestMapping(value = "addOfficeWiseSanctionedStrength")
    public ModelAndView AddOfficeWiseSanctionedStrength(@ModelAttribute("LoginUserBean") LoginUserBean lub, @ModelAttribute("officebean") OfficeWiseSanctionedStrengthBean officebean) {

        ModelAndView mav = null;
        mav = new ModelAndView("report/AddOfficeWiseSanctionedStrength", "officebean", officebean);
        if (officebean.getAerId() != null && !officebean.getAerId().equals("")) {
            officeWiseSanctionedStrengthDAO.getSanctionedStrengthData(officebean);
        }
        Map<String, String> fylist = officeWiseSanctionedStrengthDAO.getFinancialYearList();
        mav.addObject("fylist", fylist);
        mav.addObject("offName", lub.getOffname());
        return mav;
    }

    /*@RequestMapping(value = "saveOfficeWiseSanctionedStrength")
    public String SaveOfficeWiseSanctionedStrength(Model model, @ModelAttribute("LoginUserBean") LoginUserBean lub, @ModelAttribute("officebean") OfficeWiseSanctionedStrengthBean officebean, @RequestParam("btnAdd") String btnAdd) {
        
        if (btnAdd.equals("Save")) {
            officeWiseSanctionedStrengthDAO.saveSanctionedPostData(lub.getOffcode(), officebean);
            List data = officeWiseSanctionedStrengthDAO.getSanctionedStrengthList(lub.getOffcode());
            model.addAttribute("data", data);
            model.addAttribute("offName", lub.getOffname());
        } else if (btnAdd.equals("Back")) {

        }
       return "report/OfficeWiseSanctionedStrength"; 
    }*/
    
    @RequestMapping(value = "saveOfficeWiseSanctionedStrength", params = {"action=Save"})
    public ModelAndView Save(@ModelAttribute("LoginUserBean") LoginUserBean lub,@ModelAttribute("officebean") OfficeWiseSanctionedStrengthBean officebean) {
        //System.out.println("Inside Save");
        officeWiseSanctionedStrengthDAO.saveSanctionedPostData(lub.getOffcode(), officebean);
        return new ModelAndView("redirect:getOfficeWiseSanctionedStrength.htm");
    }
    
    @RequestMapping(value = "saveOfficeWiseSanctionedStrength", params = {"action=Back"})
    public ModelAndView Back(@ModelAttribute("officebean") OfficeWiseSanctionedStrengthBean officebean) {

        return new ModelAndView("redirect:getOfficeWiseSanctionedStrength.htm");
    }

    @RequestMapping(value = "duplicateAERFinancialYear")
    public void getdeptlist(HttpServletResponse response, @ModelAttribute("LoginUserBean") LoginUserBean lub, @RequestParam("fy") String fy) {
        //response.setContentType("application/json");
        PrintWriter out = null;
        //JSONArray json = null;
        try {
            String isDuplicate = officeWiseSanctionedStrengthDAO.verifyDuplicate(lub.getOffcode(), fy);
            out = response.getWriter();
            out.write(isDuplicate.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
        }
    }
}
