/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.controller.master;

import hrms.dao.employee.EmployeeDAO;
import hrms.dao.master.BankDAO;
import hrms.dao.master.BlockDAO;
import hrms.dao.master.BranchDAO;
import hrms.dao.master.DepartmentDAO;
import hrms.dao.master.DistrictDAO;
import hrms.dao.master.OfficeDAO;
import hrms.dao.master.StateDAO;
import hrms.dao.master.SubDivisionDAO;
import hrms.dao.master.SubStantivePostDAO;
import hrms.dao.master.TreasuryDAO;
import hrms.model.employee.Employee;
import hrms.model.master.Block;
import hrms.model.master.Office;
import hrms.model.master.State;
import hrms.model.master.SubDivision;
import hrms.model.master.SubstantivePost;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Durga
 */
@Controller
public class OfficeMasterController {

    @Autowired
    OfficeDAO officeDao;
    @Autowired
    DepartmentDAO departmentDao;
    @Autowired
    BankDAO bankDAO;
    @Autowired
    BranchDAO branchDAO;
    @Autowired
    TreasuryDAO treasuryDao;
    @Autowired
    BlockDAO blockDAO;
    @Autowired
    StateDAO stateDAO;
    @Autowired
    EmployeeDAO employeeDAO;
    @Autowired
    SubDivisionDAO subDivisionDAO;
    @Autowired
    SubStantivePostDAO substantivePostDAO;
    @Autowired
    DistrictDAO districtDAO;

    @RequestMapping(value = "getOfficeList")
    public String getOfficeList(@RequestParam("deptcode") String deptcode) {
        return "/office/Office";
    }

    @ResponseBody
    @RequestMapping(value = "getOfficeListJSON")
    public void getofficelist(HttpServletResponse response, @RequestParam("deptcode") String deptCode) {
        response.setContentType("application/json");
        PrintWriter out = null;
        JSONArray json = null;

        try {
            List officelist = officeDao.getTotalOfficeList(deptCode);
            System.out.println("--------------");
            json = new JSONArray(officelist);
            out = response.getWriter();
            out.write(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
        }
    }

    @RequestMapping(value = "getOfficeListJSONPaging")
    public void getOfficeListJSONPaging(HttpServletResponse response, @RequestParam Map<String, String> requestParams) {

        response.setContentType("application/json");
        PrintWriter out = null;
        JSONObject json = new JSONObject();
        String deptCode = null;
        int total = 0;
        try {
            int page = Integer.parseInt(requestParams.get("page"));
            int rows = Integer.parseInt(requestParams.get("rows"));
            String offToSearch = requestParams.get("offToSearch");

            List officelist = officeDao.getOfficeList(deptCode, offToSearch, page, rows);
            total = officeDao.getOfficeListCount(deptCode, offToSearch);

            json.put("rows", officelist);
            json.put("total", total);
            out = response.getWriter();
            out.write(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
        }
    }

    @RequestMapping(value = "officeList")
    public ModelAndView officeList(@ModelAttribute("Office") Office office) {
        ModelAndView mv = new ModelAndView();
        mv.addObject("departmentList", departmentDao.getDepartmentList());
        mv.setViewName("/master/OfficeList");
        return mv;
    }

    @RequestMapping(value = "getDeptWiseOfficeList")
    public ModelAndView getDeptWiseOfficeList(@ModelAttribute("Office") Office office) {
        ModelAndView mv = new ModelAndView();
        mv.addObject("Office", office);
        mv.addObject("departmentList", departmentDao.getDepartmentList());
        mv.addObject("officeList", officeDao.getTotalOfficeList(office.getDeptCode()));
        mv.setViewName("/master/OfficeList");
        return mv;
    }

    @RequestMapping(value = "getOfficeDetail.htm")
    public ModelAndView getOfficeDetail(@ModelAttribute("officeModel") Office office) {
        Office officeDtls = null;
        System.out.println("========Hello============" + office.getOffCode());
          System.out.println("--+++++--"+office.getDistCode()+"------------"+office.getTrCode()+"-----------");
        if (office.getOffCode() != null) {
            officeDtls = officeDao.getOfficeDetails(office.getOffCode());
            Block block = blockDAO.getBlockDetails(officeDtls.getBlockCode());
            officeDtls.setBlockName(block.getBlockName());
            State state = stateDAO.getStateDetails(officeDtls.getStateCode());
            officeDtls.setStateName(state.getStatename());
            Employee employee = employeeDAO.getEmployeeProfile(officeDtls.getDdoHrmsid());
            officeDtls.setDdoName(employee.getFname());
            SubDivision subDivision = subDivisionDAO.getSubDivisionDetail(officeDtls.getSubDivisionCode());
            officeDtls.setSubDivisionName(subDivision.getSubDivisionName());
            SubstantivePost spc = substantivePostDAO.getSpcDetail(officeDtls.getDdoSpc());
            officeDtls.setDdoPost(spc.getPostname());
            ModelAndView mv = new ModelAndView("/master/OfficeDetail", "officeModel", officeDtls);
           
            mv.addObject("bankList", bankDAO.getBankList());
            mv.addObject("branchList", branchDAO.getBranchList(officeDtls.getBankCode()));
            mv.addObject("treasuryList", treasuryDao.getTreasuryList());
            mv.addObject("subdivisionList", subDivisionDAO.getSubDivisionList());
            mv.addObject("districtList", districtDAO.getDistrictList());
            mv.addObject("departmentList", departmentDao.getDepartmentList());
            System.out.println("*****======*********"+office.getDeptCode());
             mv.addObject("parentOffList", officeDao.getTotalOfficeList(officeDtls.getDeptCode()));
            return mv;
        } else {
            ModelAndView mv = new ModelAndView();
            System.out.println("----"+office.getDistCode());
             System.out.println("----"+office.getTrCode());
            //ModelAndView mv = new ModelAndView("/master/OfficeDetail", "officeModel", officeDtls);
            mv.addObject("bankList", bankDAO.getBankList());
            mv.addObject("branchList", branchDAO.getBranchList(office.getBankCode()));
            mv.addObject("treasuryList", treasuryDao.getTreasuryList());
            mv.addObject("subdivisionList", subDivisionDAO.getSubDivisionList());
            mv.addObject("districtList", districtDAO.getDistrictList());
            mv.addObject("departmentList", departmentDao.getDepartmentList());
            mv.addObject("blockList", blockDAO.getBlockList(office.getDistCode()));
            mv.setViewName("/master/OfficeDetail");
            return mv;
        }

    }

    @RequestMapping(value = "getOfficeDetail.htm", params = "save")
    public ModelAndView saveOfficeDetail(@ModelAttribute("officeModel") Office office) {
        System.out.println("#################"+office.getOffCode());
        if(office.getOffCode()!=null && !office.getOffCode().equals("")){
             officeDao.updateOfficeDetails(office);
        }else{
           officeDao.saveOfficeDetails(office);
        }
        ModelAndView mv = new ModelAndView("/master/OfficeList", "Office", office);
         mv.addObject("departmentList", departmentDao.getDepartmentList());
          mv.addObject("officeList", officeDao.getTotalOfficeList(office.getDeptCode()));
        return mv;
    }
}
