/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.controller.tab;

import hrms.common.CommonFunctions;
import hrms.dao.login.LoginDAO;
import hrms.dao.tab.ServicePanelDAO;
import hrms.model.login.LoginUserBean;
import hrms.model.tab.RollwiseGroupInfoBean;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Surendra
 */
@Controller

@SessionAttributes({"LoginUserBean", "SelectedEmpObj","SelectedEmpOffice"})
public class TabServicePanelController {

    @Autowired
    public ServicePanelDAO servicePanelDAO;
    @Autowired
    public LoginDAO loginDao;

    @RequestMapping(value = "getRollWiseLink", method = RequestMethod.GET)
    public ModelAndView getServicePanel(@ModelAttribute("RollwiseGroupInfoBean") RollwiseGroupInfoBean rgi, @ModelAttribute("LoginUserBean") LoginUserBean lub,
            BindingResult result, @RequestParam("rollId") String rollId, @RequestParam("nodeID") String nodeId) {
        ModelAndView mav = new ModelAndView();
        String path = "index";
        String ifEmpSpecific = "";
        ArrayList servicesList = new ArrayList();
        try {
            String roll = CommonFunctions.decodedTxt(rollId);
            if (roll != null && (roll.equals("05"))) {
                /*
                 Below code of Block is written for My office Tab 
                 Where Roll Id =05 for My Office Tab
                 */
                //rgi.setUserType("governmentemployee");
                if (nodeId != null && !nodeId.equals("")) {
                    if (nodeId.substring(0, 2).equals("PA")) { /* This block will be execute when user will press Parent Node */

                        ifEmpSpecific = "N";
                        servicesList = servicePanelDAO.getRollWiseGrpInfo(roll, lub.getSpc(), ifEmpSpecific, false);
                    } else if (nodeId != null && nodeId.substring(0, 2).equals("RE")) { /* This block will be execute when user will press Regular Employee Node */

                        ifEmpSpecific = "N";
                        servicesList = servicePanelDAO.getRollWiseGrpInfo(roll, lub.getSpc(), ifEmpSpecific, false);
                    } else if (nodeId != null && nodeId.substring(0, 2).equals("CO")) { /* This block will be execute when user will press Contractual Employee Node */

                        ifEmpSpecific = "N";
                        servicesList = servicePanelDAO.getRollWiseGrpInfo(roll, lub.getSpc(), ifEmpSpecific, false);
                    } else { /* This block will be execute when user will press Employee Node */

                        ifEmpSpecific = "Y";
                        nodeId = CommonFunctions.decodedTxt(nodeId);
                        /*When User clicked the employee the clicked Employee is set in the session*/
                        hrms.model.login.Users u = loginDao.getEmployeeProfileInfo(nodeId);
                        mav.addObject("SelectedEmpObj", u);
                        /*When User clicked the employee the clicked Employee is set in the session*/
                        servicesList = servicePanelDAO.getRollWiseGrpInfo(roll, lub.getSpc(), ifEmpSpecific, false);
                    }
                } else {
                    ifEmpSpecific = "N";
                    servicesList = servicePanelDAO.getRollWiseGrpInfo(roll, lub.getSpc(), ifEmpSpecific, false);

                }

            } else if (roll != null && (roll.equals("04"))) {
                if (nodeId.substring(0, 2).equals("PA")) { /* This block will be execute when user will press Parent Node */

                    ifEmpSpecific = "N";
                    mav.addObject("SelectedEmpOffice", nodeId);
                    servicesList = servicePanelDAO.getRollWiseGrpInfo(roll, lub.getSpc(), ifEmpSpecific, false);
                }else{
                    mav.addObject("SelectedEmpOffice", nodeId);
                    ifEmpSpecific = "N";
                    servicesList = servicePanelDAO.getRollWiseGrpInfo(roll, lub.getSpc(), ifEmpSpecific, false);
                }
            }
            rgi.setGrpList(servicesList);
            rgi.setIsAccessible("Yes");

            mav.addObject("RollwiseGroupInfoBean", rgi);
            path = "/tab/Services";

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

        mav.setViewName(path);
        return mav;
    }
}
