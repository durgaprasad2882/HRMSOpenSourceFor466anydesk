package hrms.controller.payroll.aqreport;

import hrms.common.Numtowordconvertion;
import hrms.dao.payroll.billbrowser.AqReportDAO;
import hrms.model.common.CommonReportParamBean;
import hrms.model.login.LoginUserBean;
import hrms.model.payroll.aqreport.AqreportBean;
import hrms.model.payroll.billbrowser.BillConfigObj;
import hrms.model.payroll.schedule.SectionWiseAqBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class AqBillController {

    @Autowired
    public AqReportDAO aqReportDAO;

    @RequestMapping(value = "aqbillreport.htm", method = RequestMethod.GET)
    //,@RequestParam("billNo") String billNo
    public String aqBillReportHTML(ModelMap model, @ModelAttribute("LoginUserBean") LoginUserBean lub, @ModelAttribute("CommonReportParamBean") CommonReportParamBean crb, @ModelAttribute("AqreportBean") AqreportBean aqreportFormBean, BindingResult result, HttpServletRequest request) {

        ArrayList aqlist = new ArrayList();
        String jndiStr = null;
        String year = "";
        String month = "";
        String billdesc = "";
        String billdate = "";
        String empType = "";
        String billNo = aqreportFormBean.getBillNo();
        BillConfigObj objBill = null;
        SectionWiseAqBean sectionwiseAq = null;
        //String format=aqreportFormBean.getFormat();
        String format = "f1";
//        if(request.getParameter("format") != null){
//            format = request.getParameter("format");
//        }else{
//            format=aqreportFormBean.getFormat();
//        }

        //Statement stmt = null;
        // ResultSet rs = null;
        String column9NameList = null;
        String column10NameList = null;
        String column11NameList = null;
        String column12NameList = null;
        String column13NameList = null;
        String column14NameList = null;
        String column15NameList = null;
        String column16NameList = null;
        String column17NameList = "";
        String column18NameList = null;
        try {
            crb = aqReportDAO.getBillDetails(billNo);
            year = crb.getAqyear() + "";
            month = crb.getAqmonth() + "";
            billdesc = crb.getBilldesc();
            billdate = crb.getBilldate();
            empType = aqReportDAO.getEmpType(billNo, month, year);
            BillConfigObj billConfig = aqReportDAO.getBillConfig(billNo);
            
            Map<String, List> colNameList = aqReportDAO.getAllColumnNameList(billNo, month, year);

            if (colNameList.containsKey("9")) {

                Iterator itr = colNameList.get("9").iterator();
                while (itr.hasNext()) {
                    if (column9NameList != null && !column9NameList.equals("")) {
                        column9NameList = column9NameList + "/</br>" + (String) itr.next();
                    } else {
                        column9NameList = (String) itr.next();
                    }
                }
            } else {
                column9NameList = "LIC/<br/>PLI";
            }

            if (colNameList.containsKey("10")) {

                Iterator itr = colNameList.get("10").iterator();
                while (itr.hasNext()) {
                    if (column10NameList != null && !column10NameList.equals("")) {
                        column10NameList = column10NameList + "/</br>" + (String) itr.next();
                    } else {
                        column10NameList = (String) itr.next();
                    }
                }
            } else {
                column10NameList = "GPF/CPF/TPF<br/>DA-GPF<br/>RECOVERY";
            }

            if (colNameList.containsKey("11")) {

                Iterator itr = colNameList.get("11").iterator();
                while (itr.hasNext()) {
                    if (column11NameList != null && !column11NameList.equals("")) {
                        column11NameList = column11NameList + "/</br>" + (String) itr.next();
                    } else {
                        column11NameList = (String) itr.next();
                    }
                }
            } else {
                column11NameList = "P.TAX<br/>I.TAX";
            }

            if (colNameList.containsKey("12")) {

                Iterator itr = colNameList.get("12").iterator();
                while (itr.hasNext()) {
                    if (column12NameList != null && !column12NameList.equals("")) {
                        column12NameList = column12NameList + "/</br>" + (String) itr.next();
                    } else {
                        column12NameList = (String) itr.next();
                    }
                }
            } else {
                column12NameList = "HRR<br/>WATER TAX<br/>SWG<br/>HIRE CHG";
            }

            if (colNameList.containsKey("13")) {

                Iterator itr = colNameList.get("13").iterator();
                while (itr.hasNext()) {
                    if (column13NameList != null && !column13NameList.equals("")) {
                        column13NameList = column13NameList + "/</br>" + (String) itr.next();
                    } else {
                        column13NameList = (String) itr.next();
                    }
                }
            } else {
                column13NameList = "HB<br/> INT HB<br/>SPL HB<br/>INT SPL HB <br />";
            }

            if (colNameList.containsKey("14")) {

                Iterator itr = colNameList.get("14").iterator();
                while (itr.hasNext()) {
                    if (column14NameList != null && !column14NameList.equals("")) {
                        column14NameList = column14NameList + "/ </br>" + (String) itr.next();
                    } else {
                        column14NameList = (String) itr.next();
                    }
                }
            } else {
                column14NameList = "MC<br/>INT MC<br/>MC/MOP ADV<br/>INT MC/MOPED";
            }

            if (colNameList.containsKey("15")) {

                Iterator itr = colNameList.get("15").iterator();
                while (itr.hasNext()) {
                    if (column15NameList != null && !column15NameList.equals("")) {
                        column15NameList = column15NameList + "/ </br>" + (String) itr.next();
                    } else {
                        column15NameList = (String) itr.next();
                    }
                }
            } else {
                column15NameList = "CAR ADV<br/>INT CAR<br/>BI-CYCLE<br />INT CYCL";
            }

            if (colNameList.containsKey("16")) {

                Iterator itr = colNameList.get("16").iterator();
                while (itr.hasNext()) {
                    if (column16NameList != null && !column16NameList.equals("")) {
                        column16NameList = column16NameList + "/ </br>" + (String) itr.next();
                    } else {
                        column16NameList = (String) itr.next();
                    }
                }
            } else {
                column16NameList = "PAY ADV<br />MED ADV<br/>TRADE ADV<br/>OVDL";
            }

            if (colNameList.containsKey("17")) {

                Iterator itr = colNameList.get("17").iterator();
                while (itr.hasNext()) {
                    if (column17NameList != null && !column17NameList.equals("")) {
                        column17NameList = column17NameList + "/ </br>" + (String) itr.next();
                    } else {
                        column17NameList = (String) itr.next();
                    }
                }
            } else {
                column17NameList = "FEST<br/>NPS ARR.<br/>EX. PAY<br />RTI<br />AUDR";
            }

            if (colNameList.containsKey("18")) {

                Iterator itr = colNameList.get("18").iterator();
                while (itr.hasNext()) {
                    if (column18NameList != null && !column18NameList.equals("")) {
                        column18NameList = column18NameList + "/ </br>" + (String) itr.next();
                    } else {
                        column18NameList = (String) itr.next();
                    }
                }
            } else {
                column18NameList = "OTHER<br/>RECOVERY </br> GIS ADV </br>AIS GIS</br>COMP ADV";
            }

            List dedAbstractList = aqReportDAO.getDeductionGrandAbstract(billNo, month, year);
            
            if (format != null && format.equals("f1")) {
                aqlist = aqReportDAO.getSectionWiseBillDtls(billNo, month, year, "f1", billConfig, empType, column9NameList, column10NameList, column11NameList, column12NameList, column13NameList, column14NameList, column15NameList, column16NameList, column17NameList, column18NameList);
            } else if (format != null && format.equals("f2")) {
                aqlist = aqReportDAO.getSectionWiseBillDtls(billNo, month, year, "f2", billConfig, empType, column9NameList, column10NameList, column11NameList, column12NameList, column13NameList, column14NameList, column15NameList, column16NameList, column17NameList, column18NameList);
            }
           
            aqreportFormBean.setAqlist(aqlist);

            aqreportFormBean.setOffen(crb.getOfficeen());
            aqreportFormBean.setDept(crb.getDeptname());
            aqreportFormBean.setDistrict(crb.getDistrict());
            aqreportFormBean.setState(crb.getStatename());
            aqreportFormBean.setMonth(aqReportDAO.getMonth(Integer.parseInt(month)));
            aqreportFormBean.setYear(year);
            aqreportFormBean.setBilldesc(billdesc);
            aqreportFormBean.setBilldate(billdate);
            int col3Tot = aqReportDAO.getColGrandTotal(aqlist, "col3", "BASIC", null) + aqReportDAO.getColGrandTotal(aqlist, "col3", "SP", null) + aqReportDAO.getColGrandTotal(aqlist, "col3", "GP", null);
            int col4Tot = aqReportDAO.getColGrandTotal(aqlist, "col4", "GP", null) + aqReportDAO.getColGrandTotal(aqlist, "col4", "PPAY", null);
            int col5Tot = (aqReportDAO.getColGrandTotal(aqlist, "col5", "DA", null) + aqReportDAO.getColGrandTotal(aqlist, "col5", "IR", null));
            int col6Tot = aqReportDAO.getColGrandTotal(aqlist, "col6", "HRA", null) + aqReportDAO.getColGrandTotal(aqlist, "col6", "LFQ", null);
            int col7Tot = aqReportDAO.getColGrandTotal(aqlist, "col7", 0, null) + aqReportDAO.getColGrandTotal(aqlist, "col7", 1, null) + aqReportDAO.getColGrandTotal(aqlist, "col7", 2, null) + aqReportDAO.getColGrandTotal(aqlist, "col7", 3, null) + aqReportDAO.getColGrandTotal(aqlist, "col7", 4, null) + aqReportDAO.getColGrandTotal(aqlist, "col7", 5, null) + aqReportDAO.getColGrandTotal(aqlist, "col7", 6, null) + aqReportDAO.getColGrandTotal(aqlist, "col7", 7, null);
            int col8Tot = aqReportDAO.getColGrandTotal(aqlist, "col8", "GROSS PAY", null);
            int col9Tot = aqReportDAO.getColGrandTotal(aqlist, "col9", "LIC", null) + aqReportDAO.getColGrandTotal(aqlist, "col9", "PLI", null);
            int col10Tot = aqReportDAO.getColGrandTotal(aqlist, "col10", "CPF", null) + aqReportDAO.getColGrandTotal(aqlist, "col10", "GPF", null) + aqReportDAO.getColGrandTotal(aqlist, "col10", "TPF", null) + aqReportDAO.getColGrandTotal(aqlist, "col10", "GA", null) + aqReportDAO.getColGrandTotal(aqlist, "col10", "TPFGA", null) + aqReportDAO.getColGrandTotal(aqlist, "col10", "GPDD", null) + aqReportDAO.getColGrandTotal(aqlist, "col10", "GPIR", null);
            int col11Tot = aqReportDAO.getColGrandTotal(aqlist, "col11", "PT", null) + aqReportDAO.getColGrandTotal(aqlist, "col11", "IT", null);
            int col12Tot = aqReportDAO.getColGrandTotal(aqlist, "col12", "HRR", null) + aqReportDAO.getColGrandTotal(aqlist, "col12", "WRR", null) + aqReportDAO.getColGrandTotal(aqlist, "col12", "SWR", null) + aqReportDAO.getColGrandTotal(aqlist, "col12", "HC", null);
            int col13Tot = aqReportDAO.getColGrandTotal(aqlist, "col13", "HBA", "P") + aqReportDAO.getColGrandTotal(aqlist, "col13", "HBA", "I") + aqReportDAO.getColGrandTotal(aqlist, "col13", "SHBA", "P") + aqReportDAO.getColGrandTotal(aqlist, "col13", "SHBA", "I");
            int col14Tot = aqReportDAO.getColGrandTotal(aqlist, "col14", "MCA", "P") + aqReportDAO.getColGrandTotal(aqlist, "col14", "MCA", "I") + aqReportDAO.getColGrandTotal(aqlist, "col14", "MOPA", "P") + aqReportDAO.getColGrandTotal(aqlist, "col14", "MOPA", "I");
            int col15Tot = aqReportDAO.getColGrandTotal(aqlist, "col15", "VE", "P") + aqReportDAO.getColGrandTotal(aqlist, "col15", "VE", "I") + aqReportDAO.getColGrandTotal(aqlist, "col15", "BI", "P") + aqReportDAO.getColGrandTotal(aqlist, "col15", "BI", "I");
            int col16Tot = aqReportDAO.getColGrandTotal(aqlist, "col16", "PAY", null) + aqReportDAO.getColGrandTotal(aqlist, "col16", "MED", null) + aqReportDAO.getColGrandTotal(aqlist, "col16", "TRADE", null);
            int col17Tot = aqReportDAO.getColGrandTotal(aqlist, "col17", "FA", null);
            int col18Tot = aqReportDAO.getColGrandTotal(aqlist, "col18", "OR", null) + aqReportDAO.getColGrandTotal(aqlist, "col18", "GISA", "P") + aqReportDAO.getColGrandTotal(aqlist, "col18", "GIS", null) + aqReportDAO.getColGrandTotal(aqlist, "col18", "CMPA", null);
            int col19Tot = aqReportDAO.getColGrandTotal(aqlist, "col19", "TOTDEN", null);
            int col20Tot = aqReportDAO.getColGrandTotal(aqlist, "col20", "NETPAY", null);
            String netPay = Numtowordconvertion.convertNumber(aqReportDAO.getColGrandTotal(aqlist, "col20", "NETPAY", null));
            aqreportFormBean.setCol3Tot(col3Tot);
            aqreportFormBean.setCol4Tot(col4Tot);
            aqreportFormBean.setCol5Tot(col5Tot);
            aqreportFormBean.setCol6Tot(col6Tot);
            aqreportFormBean.setCol7Tot(col7Tot);
            aqreportFormBean.setCol8Tot(col8Tot);
            aqreportFormBean.setCol9Tot(col9Tot);
            aqreportFormBean.setCol10Tot(col10Tot);
            aqreportFormBean.setCol11Tot(col11Tot);
            aqreportFormBean.setCol12Tot(col12Tot);
            aqreportFormBean.setCol13Tot(col13Tot);
            aqreportFormBean.setCol14Tot(col14Tot);
            aqreportFormBean.setCol15Tot(col15Tot);
            aqreportFormBean.setCol16Tot(col16Tot);
            aqreportFormBean.setCol17Tot(col17Tot);
            aqreportFormBean.setCol18Tot(col18Tot);
            aqreportFormBean.setCol19Tot(col19Tot);
            aqreportFormBean.setNetPay(netPay);
            aqreportFormBean.setCol20Tot(col20Tot);
            int totHrr=aqReportDAO.getColGrandTotal(aqlist,"col12","HRR",null);
            int totHbaPri=aqReportDAO.getColGrandTotal(aqlist,"col13","HBA","P");
            int totShbaPri=aqReportDAO.getColGrandTotal(aqlist,"col13","SHBA","P");
            int totMcaPri=aqReportDAO.getColGrandTotal(aqlist,"col14","MCA","P")+aqReportDAO.getColGrandTotal(aqlist,"col14","MOPA","P");
            int totShbaInt=aqReportDAO.getColGrandTotal(aqlist,"col13","SHBA","I");
            int totBicycPri=aqReportDAO.getColGrandTotal(aqlist,"col15","BI","P");
            int totPt=aqReportDAO.getColGrandTotal(aqlist,"col11","PT",null);
            int totGisaPri=aqReportDAO.getColGrandTotal(aqlist,"col18","GISA","P");
            int totIt=aqReportDAO.getColGrandTotal(aqlist,"col11","IT",null);
            int totGpf=aqReportDAO.getColGrandTotal(aqlist,"col10","GPF",null)+aqReportDAO.getColGrandTotal(aqlist,"col10","GA",null)+aqReportDAO.getColGrandTotal(aqlist,"col10","GPDD",null)+aqReportDAO.getColGrandTotal(aqlist,"col10","GPIR",null);
            int totMopInt=aqReportDAO.getColGrandTotal(aqlist,"col14","MCA","I")+aqReportDAO.getColGrandTotal(aqlist,"col14","MOPA","I");
            int totHc=aqReportDAO.getColGrandTotal(aqlist,"col12","HC",null);
            int totHbaInt=aqReportDAO.getColGrandTotal(aqlist,"col13","HBA","I");
            int totGis=aqReportDAO.getColGrandTotal(aqlist,"col18","GIS",null);
            int totVehicleInt=aqReportDAO.getColGrandTotal(aqlist,"col15","VE","I");
            int totCpf=aqReportDAO.getColGrandTotal(aqlist,"col10","CPF",null);
            int totPa=aqReportDAO.getColGrandTotal(aqlist,"col16","PA",null);
            int totCgegis=aqReportDAO.getColGrandTotal(aqlist,"col18","CGEGIS",null);
            int totVehiclePri=aqReportDAO.getColGrandTotal(aqlist,"col15","VE","P");
            int totTpf=aqReportDAO.getColGrandTotal(aqlist,"col10","TPF",null);
            int totTfga=aqReportDAO.getColGrandTotal(aqlist,"col10","TPFGA",null);
            int totTlci=aqReportDAO.getColGrandTotal(aqlist,"col9","TLIC",null);
            int totWrr=aqReportDAO.getColGrandTotal(aqlist,"col12","WRR",null);
             int totSwr=aqReportDAO.getColGrandTotal(aqlist,"col12","SWR",null);
            int totCc= aqReportDAO.getColGrandTotal(aqlist,"col18","CC",null);
             int totCmpa=aqReportDAO.getColGrandTotal(aqlist,"col18","CMPA",null);
            aqreportFormBean.setTotHrr(totHrr);
             aqreportFormBean.setTotHbaPri(totHbaPri);
             aqreportFormBean.setTotShbaPri(totShbaPri);
             aqreportFormBean.setTotMcaPri(totMcaPri);
             aqreportFormBean.setTotShbaInt(totShbaInt);
             aqreportFormBean.setTotBicycPri(totBicycPri);
             aqreportFormBean.setTotPt(totPt);
             aqreportFormBean.setTotGisaPri(totGisaPri);
             aqreportFormBean.setTotIt(totIt);
             aqreportFormBean.setTotGpf(totGpf);
             aqreportFormBean.setTotMopInt(totMopInt);
             aqreportFormBean.setTotHc(totHc);
             aqreportFormBean.setTotHbaInt(totHbaInt);
             aqreportFormBean.setTotGis(totGis);
             aqreportFormBean.setTotVehicleInt(totVehicleInt);
             aqreportFormBean.setTotCpf(totCpf);
             aqreportFormBean.setTotPa(totPa);
             aqreportFormBean.setTotCgegis(totCgegis);
             aqreportFormBean.setTotVehiclePri(totVehiclePri);
             aqreportFormBean.setTotTpf(totTpf);
             aqreportFormBean.setTotTfga(totTfga);
             aqreportFormBean.setTotTlci(totTlci);
             aqreportFormBean.setTotWrr(totWrr);
             aqreportFormBean.setTotSwr(totSwr);
             aqreportFormBean.setTotCc(totCc);
             aqreportFormBean.setTotCmpa(totCmpa);
            HashMap payAbstract = aqReportDAO.getPayAbstract(aqlist);
            String pay = payAbstract.get("pay").toString();
            String dp = payAbstract.get("dp").toString();
            String da = payAbstract.get("da").toString();
            String hra = payAbstract.get("hra").toString();
            String oa = payAbstract.get("oa").toString();
            String offname = aqreportFormBean.getOffen();
            String deptname = aqreportFormBean.getDept();
            String distname = aqreportFormBean.getDistrict();
            String statename = aqreportFormBean.getState();
            request.setAttribute("offname", offname);
            request.setAttribute("deptname", deptname);
            request.setAttribute("distname", distname);
            request.setAttribute("statename", statename);
            request.setAttribute("billdesc", billdesc);
            request.setAttribute("billdate", billdate);
           aqreportFormBean.setPay(pay);
           aqreportFormBean.setDp(dp);
           aqreportFormBean.setDa(da);
           aqreportFormBean.setHra(hra);
           aqreportFormBean.setOa(oa);
           int totAbstract=Integer.parseInt(pay)+Integer.parseInt(dp)+Integer.parseInt(da)+Integer.parseInt(hra)+Integer.parseInt(oa);
            aqreportFormBean.setTotAbstract(totAbstract);
            //oa=aqReportDAO.getOtherAllowance(billNo, month, year);
            //request.setAttribute("oa",oa+"");

            request.setAttribute("aqBillReport", aqreportFormBean);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        String path = "";
        if (empType.equals("R")) {
            if (format != null && format.equals("f2")) {
                path = "success";
            } else {
                path = "regular";
            }

        } else if (empType.equals("C")) {
            path = "contractual";
        }
        return "payroll/AQReport/AQ_Bill";
    }
}
