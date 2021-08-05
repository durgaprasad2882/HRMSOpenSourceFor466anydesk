package hrms.controller.empinfo;

import hrms.dao.empinfo.EmployeeInformationDAO;
import hrms.model.empinfo.EmployeeInformation;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class EmployeeInformationController {

    @Autowired
    public EmployeeInformationDAO empinfoDAO;

    @RequestMapping(value = "GetEmployeeInformationPage")
    public String GetEmployeeInformation() {
        return "/empinfo/EmployeeInformation";
    }

    @RequestMapping(value = "GetEmployeeInformation")
    public String GetEmployeeInformation(@Valid @ModelAttribute("employeeInformation") EmployeeInformation empinfo, ModelMap model) {

        EmployeeInformation empinfo1 = null;

        try {
            if ((empinfo.getCadreCode() != null && !empinfo.getCadreCode().equals("")
                    || (empinfo.getMobile() != null && !empinfo.getMobile().equals("")))) {
                empinfoDAO.updateEmployeeData(empinfo);
            }

            empinfo1 = empinfoDAO.getEmployeeData(empinfo.getEmpid(), empinfo.getGpfno());

            model.addAttribute("empinfo", empinfo1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "/empinfo/EmployeeInformation";
    }

    
}
