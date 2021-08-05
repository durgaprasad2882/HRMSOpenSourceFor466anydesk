package hrms.controller.unlockuser;

import hrms.dao.unlockuser.UnlockuserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UnlockUserController {
    
    @Autowired
    UnlockuserDAO unlockuserDAO;
    
    @RequestMapping(value = "unlockuserpage")
    public String unlockuserpage(){
        
        return "/unlockuser/UnlockUser";
        
    }
    
    @RequestMapping(value = "unlockuser")
    public String unlockuser(Model model,@RequestParam("sltEmpidGpf") String sltEmpidGpf,@RequestParam("txtEmpidGpf") String txtEmpidGpf){
        
        int status = 0;
        try{
            status = unlockuserDAO.unlockuser(sltEmpidGpf, txtEmpidGpf);
            model.addAttribute("status", status);
        }catch(Exception e){
            e.printStackTrace();
        }
      return "/unlockuser/UnlockUser";  
    }
}
