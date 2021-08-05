/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.controller.discProceeding;

import hrms.dao.discProceeding.DiscProceedingDAO;
import hrms.model.discProceeding.DiscProceedingBean;
import hrms.model.discProceeding.DpViewBean;
import hrms.model.discProceeding.Rule15ChargeBean;
import hrms.model.login.LoginUserBean;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;

@Controller
@SessionAttributes({"LoginUserBean", "DoHrmsId"})
public class DiscProceedingController implements ServletContextAware {

    @Autowired
    public DiscProceedingDAO discProcedDAO;

    private ServletContext context;

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.context = servletContext;
    }

    @RequestMapping(value = "DiscProcedingList.htm", method = {RequestMethod.POST, RequestMethod.GET})
    public ModelAndView getDiscProcedingList() {

        ModelAndView mav = null;
        try {
            mav = new ModelAndView();
            return new ModelAndView("/discProceeding/discProcedList");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mav;
    }
    
    @ResponseBody
    @RequestMapping(value = "getRule15PostListJSON")
    public void getPostListJSON(@RequestParam("offcode") String offcode, HttpServletResponse response ) {
        response.setContentType("application/json");
        PrintWriter out = null;
        JSONArray json = null;
        try {
            List postlist = discProcedDAO.getPostWithEmpList(offcode);
            json = new JSONArray(postlist);
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
    @RequestMapping(value = "GetDoListJSON", method = {RequestMethod.POST})
    public void discProcedingList(@ModelAttribute("LoginUserBean") LoginUserBean lub,@RequestParam Map<String,String> requestParams, HttpServletResponse response) {
        response.setContentType("application/json");
        JSONObject json = new JSONObject();
        PrintWriter out = null;
        List dpEmpList = null;
        try {
            
            int page = Integer.parseInt(requestParams.get("page"));
            int rows = Integer.parseInt(requestParams.get("rows"));
            
            dpEmpList = discProcedDAO.getDiscProcedingList(lub.getEmpid(), page, rows);
            int totalCount = discProcedDAO.getDiscProceedTotalCount(lub.getEmpid()); 
            
            json.put("total", totalCount);
            json.put("rows", dpEmpList);
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
    @RequestMapping(value = "GetDPEmpListJSON", method = {RequestMethod.POST})
    public void DPForwardEmployeeList(@ModelAttribute("LoginUserBean") LoginUserBean lub, HttpServletResponse response) {
        response.setContentType("application/json");
        JSONObject json = new JSONObject();
        PrintWriter out = null;
        List dpEmpList = null;
        try {
            dpEmpList = discProcedDAO.getDPForwadrEmpList(lub.getOffcode());

            json.put("total", 50);
            json.put("rows", dpEmpList);
            out = response.getWriter();
            out.write(json.toString());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
        }
    }
    
  
    @RequestMapping(value = "DiscProceding1", method = {RequestMethod.POST, RequestMethod.GET})
    public ModelAndView getEmployeeList(@ModelAttribute("LoginUserBean") LoginUserBean lub) {

        ModelAndView mav = null;
        String path = null;
        try {
            mav = new ModelAndView();
            path = "/discProceeding/OfficeEmployeeList";
            mav.addObject("deptCode", lub.getDeptcode());
            mav.setViewName(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mav;
    }
    
    @RequestMapping(value = "/DiscProceding", method = RequestMethod.POST)
    public ModelAndView getEmployee(@ModelAttribute("LoginUserBean") LoginUserBean lub, @RequestParam("rule15ShowEmp") String btnParam,
            @ModelAttribute("DiscProceedingBean") DiscProceedingBean dpBean) {
        ModelAndView mav = null;
        String path = null;
        int saveRes = 0;
        try {
            mav = new ModelAndView();
            
            if (btnParam.equalsIgnoreCase("Show")) {
                path = "/discProceeding/OfficeEmployeeList";
                mav.addObject("deptCode", lub.getDeptcode());
                mav.addObject("Dcode", dpBean.getHidDeptCode());
                mav.addObject("Ocode", dpBean.getHidOffCode());
                
            } else{
                path = "/discProceeding/OfficeEmployeeList";
                mav.addObject("deptCode", lub.getDeptcode());
            }
            mav.setViewName(path);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mav;
    }
    
    
    @RequestMapping(value = "/saveRule15Memo", method = RequestMethod.POST)
    public ModelAndView saveRule15Memorandum(@ModelAttribute("LoginUserBean") LoginUserBean lub, @RequestParam("btnAnnexture1") String btnParam,
            @ModelAttribute("DiscProceedingBean") DiscProceedingBean dpBean) {
        ModelAndView mav = null;
        int saveRes = 0;
        try {
            mav = new ModelAndView();
            dpBean.setAuthHrmsId(lub.getEmpid());
            dpBean.setAuthEmpCurDegn(lub.getSpc());
            String offCode=dpBean.getHidOffCode();
            
            if (btnParam.equalsIgnoreCase("Previous")) {
                mav.setViewName("redirect:/DiscProceding1.htm");
                
            } else if (btnParam.equalsIgnoreCase("CHARGES And WITNESS")) {
                int maxId = discProcedDAO.getMaxDaId();
                saveRes = discProcedDAO.saveRule15MemoDetails(dpBean);
                
                if (saveRes == 1) {
                    mav.addObject("DoHrmsId", dpBean.getHidDoHrmsId());
                    return new ModelAndView("redirect:/openAnnexure1.htm?DOHRMSID="+dpBean.getHidDoHrmsId()+"&DAID="+maxId+"&offCode="+offCode);
                } else {
                    mav.setViewName("/discProceeding/rule15Memo");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mav;
    }
    
    @ResponseBody
    @RequestMapping(value = "GetEMPListJSON")
    public void OffWiseEmployeeList(@ModelAttribute("LoginUserBean") LoginUserBean lub, HttpServletResponse response, @RequestParam Map<String, String> requestParams) {

        response.setContentType("application/json");
        JSONObject json = new JSONObject();
        PrintWriter out = null;
        List empList = null;
        try {
                    
            String deptCode = requestParams.get("deptCode");
            String offCode = requestParams.get("offCode");
            if(deptCode != null && !deptCode.equals("")){
                if(offCode.equals("")){
                    empList = discProcedDAO.getDeptWiseEmpList(deptCode);
                }else if(offCode !=null){
                    empList = discProcedDAO.getOffWiseEmpList(offCode);
                }
            }
            json.put("total", 50);
            json.put("rows", empList);
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
    @RequestMapping(value = "GetDpForwardEmpJSON")
    public void PostWiseEmployeeList(@ModelAttribute("LoginUserBean") LoginUserBean lub, HttpServletResponse response, @RequestParam Map<String, String> requestParams) {

        response.setContentType("application/json");
        JSONObject json = new JSONObject();
        PrintWriter out = null;
        List empList = null;
        try {
                    
            String deptCode = requestParams.get("deptCode");
            String offCode = requestParams.get("offCode");
            String curSpc = requestParams.get("pCode");
            empList = discProcedDAO.getPostWiseEmpList(curSpc);
               
            //json.put("total", 50);
            json.put("rows", empList);
            out = response.getWriter();
            out.write(json.toString());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
        }
    }
    
    @RequestMapping(value = "forwardDiscProced.htm", method = RequestMethod.POST)
    public ModelAndView ForwardRule15DP(@ModelAttribute("LoginUserBean") LoginUserBean lub, @RequestParam("rule15forwardDp") String btnParam, 
                                            @ModelAttribute("Rule15ChargeBean") Rule15ChargeBean chargeBean) {
        ModelAndView mav = null;
        try {
            
            mav = new ModelAndView();
            
            if (btnParam.equalsIgnoreCase("Back")) {
                return new ModelAndView("redirect:/DiscProcedingList.htm");
            
            } else if (btnParam.equalsIgnoreCase("Forward")) {
                int insRes=discProcedDAO.saveRule15ForwardDP(lub.getEmpid(), lub.getSpc(), chargeBean);
                return new ModelAndView("redirect:/DiscProcedingList.htm");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mav;
    }
    
    @ResponseBody
    @RequestMapping(value = "GetEMPWitnessListJSON", method = {RequestMethod.POST})
    public void EmpWitnessList(@ModelAttribute("LoginUserBean") LoginUserBean lub, @RequestParam Map<String, String> requestParams, HttpServletResponse response) {

        response.setContentType("application/json");
        JSONObject json = new JSONObject();
        PrintWriter out = null;
        List empList = null;
        try {
            String doHrmsId = requestParams.get("DOHRMSId");
            int dacId = Integer.parseInt(requestParams.get("DACID"));
            String mode = requestParams.get("MODE");
            String offCode= requestParams.get("offCode");
            
            empList = discProcedDAO.getOffWiseEmpWitnessList(offCode, doHrmsId, dacId, mode);

            json.put("rows", empList);
            out = response.getWriter();
            out.write(json.toString());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
        }
    }

    
    
    @RequestMapping(value = "rule15Controller", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView showRule15(@ModelAttribute("LoginUserBean") LoginUserBean lub, @RequestParam Map<String, String> requestParams) {

        ModelAndView mav = null;
        DiscProceedingBean dpBean = null;
        try {
            mav = new ModelAndView();

            String daId = requestParams.get("DAID");
            String doHrmsId = requestParams.get("DOHRMSID");
            String offCode = requestParams.get("offCode");
            dpBean = discProcedDAO.getRule15MemoDetails(offCode, doHrmsId);
            
            mav.addObject("Rule15Memo", dpBean);
            mav.setViewName("/discProceeding/rule15Memo");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mav;
    }

    @RequestMapping(value = "openAnnexure1.htm", method = {RequestMethod.POST, RequestMethod.GET})
    public ModelAndView loadRule15Annexure1(@RequestParam Map<String, String> requestParams, @ModelAttribute("DiscProceedingBean") DiscProceedingBean dpBean) {

        ModelAndView mav = null;
        try {
            mav = new ModelAndView();
            String doHrmsId = requestParams.get("DOHRMSID");
            int daId = Integer.parseInt(requestParams.get("DAID"));
            String offCode = requestParams.get("offCode");
            dpBean.setDoHrmsId(doHrmsId);
            dpBean.setDaid(daId);
            dpBean.setHidOffCode1(offCode);
            
            String saveExitBtn = discProcedDAO.getChargeDetails(daId);

            mav.addObject("Annex1Value", dpBean);
            mav.addObject("ShowBtn", saveExitBtn);
            mav.setViewName("/discProceeding/rule15Annex1");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mav;
    }

    @ResponseBody
    @RequestMapping(value = "ChargeListJSON")
    public void Rule15ChargeList(@RequestParam Map<String, String> requestParams, HttpServletResponse response) {

        response.setContentType("application/json");
        JSONObject json = new JSONObject();
        PrintWriter out = null;
        List empList = null;
        try {
            
            int page = Integer.parseInt(requestParams.get("page"));
            int rows = Integer.parseInt(requestParams.get("rows"));
            
            String doDaId = requestParams.get("DAID");
            empList = discProcedDAO.getRule15ChargeList(Integer.parseInt(doDaId), page, rows);
            int totalCount = discProcedDAO.getRule15ChargeTotalCount(Integer.parseInt(doDaId));
            json.put("total", totalCount);
            json.put("rows", empList);
            out = response.getWriter();
            out.write(json.toString());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
        }
    }

    

    @RequestMapping(value = "addNewCharge.htm")
    public ModelAndView loadRule15AddCharges(@RequestParam Map<String, String> requestParams,
            @ModelAttribute("DiscProceedingBean") DiscProceedingBean dpBean) {
        ModelAndView mav = null;
        try {
            mav = new ModelAndView();
            dpBean.setDoHrmsId(requestParams.get("DOHRMSID"));
            dpBean.setDaid(Integer.parseInt(requestParams.get("DAID")));
            dpBean.setHidOffCode1(requestParams.get("offCode"));    
            
            mav.addObject("newChrgValue", dpBean);
            mav.setViewName("/discProceeding/rule15AddCharge");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mav;
    }

    @RequestMapping(value = "editCharge.htm")
    public ModelAndView EditRule15Charges(@RequestParam Map<String,String> requestParams,@ModelAttribute("DiscProceedingBean") DiscProceedingBean dpBean){
        ModelAndView mav = null;
        Rule15ChargeBean chargeBean = new Rule15ChargeBean();
        try {
            mav = new ModelAndView();
            int dacId = Integer.parseInt(requestParams.get("DACID"));
            dpBean.setDoHrmsId(requestParams.get("DOHRMSID"));
            dpBean.setDaid(Integer.parseInt(requestParams.get("DAID")));
            dpBean.setDacId(dacId);
            dpBean.setHidOffCode1(requestParams.get("offCode"));

            chargeBean = discProcedDAO.EditRule15ChargeData(chargeBean, dacId);
            
            String showBtn = "Y";
            mav.addObject("newChrgValue", dpBean);
            mav.addObject("editChrgValue", chargeBean);
            mav.addObject("UpdBtn", showBtn);
            mav.setViewName("/discProceeding/rule15AddCharge");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mav;
    }

    @RequestMapping(value = "addNewWitness.htm")
    public ModelAndView loadRule15AddWitness(@RequestParam Map<String, String> requestParams,
            @ModelAttribute("DiscProceedingBean") DiscProceedingBean dpBean) {
        ModelAndView mav = null;
        try {
            mav = new ModelAndView();
            dpBean.setDoHrmsId(requestParams.get("DOHRMSID"));
            dpBean.setDaid(Integer.parseInt(requestParams.get("DAID")));
            dpBean.setDacId(Integer.parseInt(requestParams.get("DACID")));
            dpBean.setMode(requestParams.get("MODE"));
            dpBean.setHidOffCode(requestParams.get("offCode"));
            
            mav.addObject("WitnesValue", dpBean);
            mav.setViewName("/discProceeding/rule15AddWitness");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mav;
    }

    @RequestMapping(value = "editWitness.htm")
    public ModelAndView EditRule15Witness(@RequestParam Map<String,String> requestParams,@ModelAttribute("DiscProceedingBean") DiscProceedingBean dpBean){
        ModelAndView mav = null;
        Rule15ChargeBean chargeBean = new Rule15ChargeBean();
        try {
            mav = new ModelAndView();
            int dacId = Integer.parseInt(requestParams.get("DACID"));
            dpBean.setDoHrmsId(requestParams.get("DOHRMSID"));
            dpBean.setDaid(Integer.parseInt(requestParams.get("DAID")));
            dpBean.setDacId(dacId);
            dpBean.setMode(requestParams.get("MODE"));
            dpBean.setHidOffCode(requestParams.get("offCode"));
            
            String showBtn = "Y";
            mav.addObject("WitnesValue", dpBean);
            mav.addObject("editWitnsValue", chargeBean);
            mav.addObject("UpdBtn", showBtn);

            mav.setViewName("/discProceeding/rule15AddWitness");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mav;
    }

    @RequestMapping(value = "/saveRule15Anex1", method = RequestMethod.POST)
    public ModelAndView saveRule15Annexure1(@RequestParam("rule15Over") String btnParam, @ModelAttribute("DiscProceedingBean") DiscProceedingBean dpBean) {
        ModelAndView mav = null;
        try {
            mav = new ModelAndView();
            if (btnParam.equalsIgnoreCase("Save And Exit")) {
                return new ModelAndView("redirect:/DiscProcedingList.htm");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mav;
    }

    @RequestMapping(value = "saveRule15Charges.htm", method = RequestMethod.POST)
    public ModelAndView UpdateRule15AddCharge(@RequestParam("rule15AddCharge") String btnParam, @ModelAttribute("Rule15ChargeBean") Rule15ChargeBean chargeBean) {
        ModelAndView mav = null;
        try {
            
            mav = new ModelAndView();
            String doHrmsId = chargeBean.getHidChargeDoHrmsId();
            int daId = chargeBean.getHidChargeDaid();
            String offCode= chargeBean.getHidOffCode2();
            
            if (btnParam.equalsIgnoreCase("Back")) {
                return new ModelAndView("redirect:/openAnnexure1.htm?DOHRMSID="+doHrmsId +"&DAID="+daId+"&offCode="+offCode);
                
            } else if (btnParam.equalsIgnoreCase("Update")) {
                
                discProcedDAO.updateRule15Charges(chargeBean);
                return new ModelAndView("redirect:/openAnnexure1.htm?DOHRMSID="+doHrmsId +"&DAID="+daId +"&offCode="+offCode);
            
            } else if (btnParam.equalsIgnoreCase("Save")) {
                String filepath = context.getInitParameter("DPPath");
                discProcedDAO.saveRule15AddCharges(chargeBean,filepath);
                return new ModelAndView("redirect:/openAnnexure1.htm?DOHRMSID="+doHrmsId +"&DAID="+daId +"&offCode="+offCode);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mav;
    }
   
    @RequestMapping(value = "saveRule15Witness.htm", method = RequestMethod.POST)
    public ModelAndView UpdateRule15Witness(@RequestParam("rule15AddWitness") String btnParam, 
                                            @ModelAttribute("Rule15ChargeBean") Rule15ChargeBean chargeBean) {
        ModelAndView mav = null;
        try {
            
            mav = new ModelAndView();
            String doHrmsId = chargeBean.getHidWitnessDoHrmsId();
            int daid = chargeBean.getHidWitnesDaid();
            String offCode= chargeBean.getHidOffCode2();
            
            if (btnParam.equalsIgnoreCase("Back")) {
                
                return new ModelAndView("redirect:/openAnnexure1.htm?DOHRMSID="+doHrmsId+"&DAID="+daid+"&offCode="+offCode);
            
            } else if (btnParam.equalsIgnoreCase("Update")) {
                
                discProcedDAO.rule15UpdateWitness(chargeBean);
                return new ModelAndView("redirect:/openAnnexure1.htm?DOHRMSID="+doHrmsId+"&DAID="+daid+"&offCode="+offCode);
            
            } else if (btnParam.equalsIgnoreCase("Save")) {
                
                discProcedDAO.saveRule15AddWitness(chargeBean);
                return new ModelAndView("redirect:/openAnnexure1.htm?DOHRMSID="+doHrmsId+"&DAID="+daid+"&offCode="+offCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mav;
    }
    
    @RequestMapping(value = "getEmpList.htm", method = RequestMethod.POST)
    public @ResponseBody
    String getEmplist(@ModelAttribute("LoginUserBean") LoginUserBean lub, @ModelAttribute("DoHrmsId") String doHrmsId) {

        JSONArray json = null;
        try {
            ArrayList empList = discProcedDAO.getEmpComboDtls(lub.getOffcode(), doHrmsId);
            json = new JSONArray(empList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    @RequestMapping(value = "viewDiscProced", method = {RequestMethod.POST, RequestMethod.GET})
    public ModelAndView viewDiscProceding(@ModelAttribute("LoginUserBean") LoginUserBean lub, @RequestParam("DAID") String daId) {

        ModelAndView mav = null;
        DpViewBean dpViewBean = null;
        try {
            mav = new ModelAndView();
            dpViewBean = discProcedDAO.viewRule15DiscProceeding(lub.getOffcode(), daId);

            mav.addObject("viewDP", dpViewBean);
            mav.setViewName("/discProceeding/viewRule15DiscProceding");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mav;
    }

    @RequestMapping(value = "deleteChargeWitness", method = {RequestMethod.POST, RequestMethod.GET})
    public ModelAndView DeleteChargeAndWitness(@ModelAttribute("LoginUserBean") LoginUserBean lub, @RequestParam Map<String, String> requestParams) {

        ModelAndView mav = null;
        try {
            mav = new ModelAndView();
            String doHrmsId = requestParams.get("DOHRMSID");
            String daid = requestParams.get("DAID");
            String dacId = requestParams.get("DACID");
            String offCode= requestParams.get("offCode");

            discProcedDAO.deleteChargeAndWitness(Integer.parseInt(dacId));
            return new ModelAndView("redirect:/openAnnexure1.htm?DOHRMSID="+doHrmsId+"&DAID="+daid+"&offCode="+offCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mav;
    }
    
    @RequestMapping(value = "deleteWitness", method = {RequestMethod.POST, RequestMethod.GET})
    public void DeleteWitnessOnly(HttpServletResponse response, @ModelAttribute("LoginUserBean") LoginUserBean lub, 
                            @RequestParam Map<String, String> requestParams) throws JSONException,IOException {

        PrintWriter out = response.getWriter();
        JSONObject obj = new JSONObject();
        String msgType = "";
        String msg = "";
        
        try {
            String doHrmsId = requestParams.get("DOHRMSID");
            String daid = requestParams.get("DAID");
            String dacId = requestParams.get("DACID");
            
            discProcedDAO.deleteWitnessOnly(Integer.parseInt(dacId));
            msg = "Witness Deleted";
            msgType = "Info";
        } catch (Exception e) {
            e.printStackTrace();
        }
        obj.put("msg", msg);
        obj.put("msgType", msgType);
        out.write(obj.toString());
        out.flush();
        out.close();
    }
    
    
    
    @RequestMapping(value = "forwardDiscProced", method = {RequestMethod.POST, RequestMethod.GET})
    public ModelAndView forwardDiscProced(@ModelAttribute("LoginUserBean") LoginUserBean lub, @RequestParam Map<String, String> requestParams) {

        ModelAndView mav = null;
        try {
            mav = new ModelAndView();
            String daid = requestParams.get("DAID");
            mav.addObject("hidDaid", daid);
            mav.setViewName("/discProceeding/forwardRule15DiscProceding");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mav;
    }        
            
            
}
