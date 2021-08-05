package hrms.controller.changemobile;

import hrms.common.Message;
import hrms.dao.changemobile.ChangeMobileDAO;
import hrms.model.login.LoginUserBean;
import java.sql.SQLException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@SessionAttributes("LoginUserBean")
public class ChangeMobileController {

    @Autowired
    public ChangeMobileDAO changeMobileDAO;

    @RequestMapping(value = "ChangeMobile")
    public String changemobile(HttpServletResponse response, ModelMap model, @ModelAttribute("LoginUserBean") LoginUserBean lub, @RequestParam("empId") String empId, @RequestParam("newmobile") String newmobile) throws SQLException {

        String path = "/tab/ChangeMobile";
        Message msg = null;
        if (newmobile != null && !newmobile.equals("")) {
            msg = changeMobileDAO.changeMobile(empId, newmobile);
            System.out.println("msg is: "+msg.getMessage());
            model.addAttribute("message", msg.getMessage());
        }
        model.addAttribute("empId", lub.getEmpid());
        return path;
    }
}
