/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.service;

import hrms.common.DataBaseFunctions;
import hrms.dao.employee.EmployeeDAO;
import hrms.dao.empqtrallotment.EmpQtrAllotmentDAO;
import hrms.dao.lic.LicDAO;
import hrms.dao.loansanction.LoanSancDAO;
import hrms.dao.payroll.allowancededcution.AllowanceDeductionDAO;
import hrms.dao.payroll.aqdtls.AqDtlsDAO;
import hrms.dao.payroll.aqmast.AqmastDAO;
import hrms.dao.payroll.billbrowser.BillGroupDAO;
import hrms.dao.payroll.billbrowser.SectionDefinationDAO;
import hrms.dao.payroll.billmast.BillMastDAO;
import hrms.model.employee.PayComponent;
import hrms.model.employee.QuaterAllotment;
import hrms.model.lic.Lic;
import hrms.model.loan.Loan;
import hrms.model.payroll.allowancededcution.AllowanceDeduction;
import hrms.model.payroll.aqdtls.AqDtlsModel;
import hrms.model.payroll.aqmast.AqmastModel;
import hrms.model.payroll.billbrowser.BillGroup;
import hrms.model.payroll.billbrowser.SectionDefinition;
import hrms.model.payroll.billbrowser.SectionDtlSPCWiseEmp;
import hrms.model.payroll.billmast.BillMastModel;
import hrms.model.payroll.paybilltask.PaybillTask;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Manas Jena
 */
@Service
public class ProcessPayBillService {

    @Resource(name = "dataSource")
    protected DataSource dataSource;

    @Autowired
    BillGroupDAO billGroupDAO;
    @Autowired
    SectionDefinationDAO sectionDefinationDAO;
    @Autowired
    AqmastDAO aqmastDAO;
    @Autowired
    BillMastDAO billMastDAO;
    @Autowired
    EmployeeDAO employeeDAO;
    @Autowired
    AqDtlsDAO aqDtlsDAO;
    @Autowired
    AllowanceDeductionDAO allowanceDeductionDAO;
    @Autowired
    LoanSancDAO loanSancDAO;
    @Autowired
    LicDAO licDAO;
    @Autowired
    EmpQtrAllotmentDAO empQtrAllotmentDAO;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setBillMastDAO(BillMastDAO billMastDAO) {
        this.billMastDAO = billMastDAO;
    }

    public void setBillGroupDAO(BillGroupDAO billGroupDAO) {
        this.billGroupDAO = billGroupDAO;
    }

    public void setSectionDefinationDAO(SectionDefinationDAO sectionDefinationDAO) {
        this.sectionDefinationDAO = sectionDefinationDAO;
    }

    public void setAqmastDAO(AqmastDAO aqmastDAO) {
        this.aqmastDAO = aqmastDAO;
    }

    public void setEmployeeDAO(EmployeeDAO employeeDAO) {
        this.employeeDAO = employeeDAO;
    }

    public void setAqDtlsDAO(AqDtlsDAO aqDtlsDAO) {
        this.aqDtlsDAO = aqDtlsDAO;
    }

    public void setAllowanceDeductionDAO(AllowanceDeductionDAO allowanceDeductionDAO) {
        this.allowanceDeductionDAO = allowanceDeductionDAO;
    }

    public void setLoanSancDAO(LoanSancDAO loanSancDAO) {
        this.loanSancDAO = loanSancDAO;
    }

    public void setLicDAO(LicDAO licDAO) {
        this.licDAO = licDAO;
    }

    public EmpQtrAllotmentDAO getEmpQtrAllotmentDAO() {
        return empQtrAllotmentDAO;
    }

    public void setEmpQtrAllotmentDAO(EmpQtrAllotmentDAO empQtrAllotmentDAO) {
        this.empQtrAllotmentDAO = empQtrAllotmentDAO;
    }
    private int slno = 1;

    public void deletePayBillTask(int billId) {
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            //con = dataSource.getConnection();
            con = this.getDBConnection();
            pstmt = con.prepareStatement("DELETE FROM PAYBILL_TASK WHERE BILL_ID=?");
            pstmt.setInt(1, billId);
            pstmt.executeUpdate();
        } catch (SQLException exe) {
            exe.printStackTrace();
        } finally {
            try {
                pstmt.close();
                con.close();
            } catch (SQLException exe) {
                exe.printStackTrace();
            }
        }
    }

    public ArrayList getPayBillTaskList(String billType) {
        ArrayList tasklist = new ArrayList();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {

            //con = dataSource.getConnection();
            con = this.getDBConnection();

            // priority::30=SELECT * FROM paybill_task WHERE BILL_TYPE=? AND AQ_MONTH < 4 AND AQ_YEAR=2018 order by task_id
            //pstmt = con.prepareStatement("SELECT * FROM paybill_task WHERE BILL_TYPE=? AND off_code = 'BBSHOM0010000'  AND AQ_YEAR=2018 ORDER BY AQ_MONTH");//ORDER BY priority desc LIMIT 2 bill_id = 41189096
            //  pstmt = con.prepareStatement("SELECT * FROM paybill_task WHERE BILL_TYPE=? AND AQ_MONTH < 4 AND AQ_YEAR=2018 order by task_id");//ORDER BY priority desc LIMIT 2 bill_id = 41189096
            pstmt = con.prepareStatement("SELECT * FROM paybill_task WHERE BILL_TYPE=? and priority=4 order by task_id ");//ORDER BY priority desc LIMIT 2 bill_id = 41189096
            pstmt.setString(1, billType);
            rs = pstmt.executeQuery();
            while (rs.next()) {

                int billId = rs.getInt("bill_id");
                System.out.println("billId:" + billId);
                if (billMastDAO.getBillStatus(billId) == 2) {
                    deletePayBillTask(billId);
                } else {
                    PaybillTask paybillTask = new PaybillTask();
                    paybillTask.setTaskid(rs.getInt("task_id"));
                    paybillTask.setOffcode(rs.getString("off_code"));
                    paybillTask.setAqmonth(rs.getInt("aq_month"));
                    paybillTask.setAqyear(rs.getInt("aq_year"));
                    paybillTask.setBillid(billId);
                    paybillTask.setBillgroupid(rs.getBigDecimal("bill_group_id"));
                    tasklist.add(paybillTask);
                }
            }
        } catch (SQLException exe) {
            exe.printStackTrace();
        } finally {
            try {
                rs.close();
                pstmt.close();
                con.close();
            } catch (SQLException exe) {
                exe.printStackTrace();
            }
        }
        return tasklist;
    }
    //private int gross_amt = 0;

    public void regularBillProcess(SectionDefinition secDef, BillGroup billGroup, PaybillTask paybillTask, BillMastModel billMastModel) {

        ArrayList spcwiseemplist = sectionDefinationDAO.getSPCWiseEmpInSection(secDef.getSectionId(), billMastModel.getOffCode(), billMastModel.getAqYear(), billMastModel.getAqMonth() + 1);
        Calendar myCalendar = new GregorianCalendar(billMastModel.getAqYear(), billMastModel.getAqMonth(), 1);
        Date startDate = myCalendar.getTime();
        //1st date of the month
        myCalendar.set(Calendar.DAY_OF_MONTH, myCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        int daysInMonth = myCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        Date endDate = myCalendar.getTime();
        //BillMastModel billMastModel = billMastDAO.getBillMastDetails(paybillTask.getBillid());
        String configLvl = billGroupDAO.getConfigurationLvl(new BigDecimal(billMastModel.getBillGroupId()));
        int totalEmp = spcwiseemplist.size();
        for (int k = 0; k < spcwiseemplist.size(); k++) {
            slno++;
            System.out.println(k + "/" + totalEmp);
            SectionDtlSPCWiseEmp sdswe = (SectionDtlSPCWiseEmp) spcwiseemplist.get(k);
            //this.gross_amt = 0;
            System.out.println("****###" + sdswe.getEmpid());
            if (sdswe.getEmpid() == null) {
                AqmastModel aqmast = new AqmastModel();
                aqmast.setSlno(slno);
                aqmast.setAqGroup(billGroup.getBillgroupid());
                aqmast.setAqGroupDesc(billGroup.getBillgroupdesc());
                aqmast.setPayMonth(paybillTask.getAqmonth());
                aqmast.setPayYear(paybillTask.getAqyear());
                aqmast.setAqMonth(paybillTask.getAqmonth());
                aqmast.setAqYear(paybillTask.getAqyear());
                aqmast.setPayType("42");
                aqmast.setAqType("42");
                aqmast.setAqDate(billMastModel.getBillDate());
                aqmast.setPlan(billGroup.getPlan());
                aqmast.setSector(billGroup.getSector());
                aqmast.setMajorHead(billGroup.getMajorHead());
                aqmast.setMajorHeadDesc(billGroup.getMajorHeadDesc());
                aqmast.setSubMajorHead(billGroup.getSubMajorHead());
                aqmast.setSubMajorHeadDesc(billGroup.getSubMajorHeadDesc());
                aqmast.setMinorHead(billGroup.getMinorHead());
                aqmast.setMinorHeadDesc(billGroup.getMinorHeadDesc());
                aqmast.setSubMinorHead1(billGroup.getSubMinorHead1());
                aqmast.setSubMinorHeadDesc1(billGroup.getSubMinorHeadDesc1());
                aqmast.setSubMinorHead2(billGroup.getSubMinorHead2());
                aqmast.setSubMinorHeadDesc2(billGroup.getSubMinorHeadDesc2());
                aqmast.setSubMinorHead3(billGroup.getSubMinorHead3());
                aqmast.setSubMinorHeadDesc3(billGroup.getSubMinorHeadDesc3());
                aqmast.setPlan(billGroup.getPlan());
                aqmast.setSector(billGroup.getSector());
                aqmast.setOffCode(billMastModel.getOffCode());
                aqmast.setOffDdo(billMastModel.getOffDDO());
                aqmast.setSecSlNo(secDef.getSecslno());
                aqmast.setSection(secDef.getSection());
                aqmast.setPostSlNo(sdswe.getPostslno());
                aqmast.setCurDesg(sdswe.getPostname());
                aqmast.setBillNo(billMastModel.getBillNo());
                aqmast.setSpcOrdNo(sdswe.getOrderno());
                aqmast.setSpcOrdDate(sdswe.getOrderdate());
                aqmast.setPayScale(sdswe.getPayscale());
                aqmast.setCurSpc(sdswe.getSpc());
                aqmast.setEmpType("R");
                if (aqmast.getAqDate() != null) {
                    aqmastDAO.saveAqmastdata(aqmast);
                }
            } else {

                boolean stopSalary = aqmastDAO.stopSalaryForPayHeldUp(sdswe.getEmpid());
                
                if (stopSalary==false) {

                    /*Get Basic and GP of the Employee (If increment is in this month then also auto calculate)*/
                    PayComponent payComponent = getEmployeePayComponent(sdswe, startDate, endDate, daysInMonth);
                    /*Get Basic and GP of the Employee (If increment is in this month then also auto calculate)*/
                    HashMap<String, Integer> payworkday = getPayWorkDays(sdswe, startDate, endDate, daysInMonth);
                    int monBasic = payComponent.getBasic();
                    int curBasic = 0;

                    if (sdswe.getDeptcode() != null && sdswe.getDeptcode().equals("05")) {
                        monBasic = monBasic / 2;
                    }
                    System.out.println(monBasic + ":monBasic - daysInMonth:" + daysInMonth + " payday:" + payworkday.get("payday"));

                    if (payworkday.get("payday") == daysInMonth) {
                        curBasic = monBasic;
                    } else {
                        double value = (((double) monBasic) / daysInMonth) * payworkday.get("payday");
                        Long temp = Math.round(value);
                        curBasic = temp.intValue();
                        payComponent.setBasic(curBasic);
                    }
                    System.out.println("curBasic:" + curBasic);

                    /*if the employee is under suspension*/
                    int defaultBank = 1;
                    if (sdswe.getBankcode() == null) {
                        defaultBank = 0;
                    }
                    
                    //long newBasic = payComponent.getBasic();
                /*if (payComponent.getIspayrevised() == null || payComponent.getIspayrevised().equalsIgnoreCase("N")) {
                     newBasic = payComponent.getBasic() + payComponent.getGp();
                     } else {
                     newBasic = payComponent.getBasic();
                     }*/
                    String gpfSeries = getGPFSeries(sdswe.getGpfaccno());
                    //this.gross_amt = this.gross_amt + curBasic;
                    
                    AqmastModel aqmast = new AqmastModel();
                    aqmast.setGrossAmt(curBasic);
                    aqmast.setSlno(slno);
                    aqmast.setEmpCode(sdswe.getEmpid());
                    aqmast.setEmpName(sdswe.getEmpname());
                    aqmast.setGpfAccNo(sdswe.getGpfaccno());
                    aqmast.setGpfType(gpfSeries);
                    aqmast.setGazetted(sdswe.getIsgazetted());
                    aqmast.setBankAccNo(sdswe.getBankaccno());
                    aqmast.setIfscCode(sdswe.getIfscCode());
                    aqmast.setBranchName(sdswe.getBranchcode());
                    aqmast.setBankName(sdswe.getBankcode());

                    aqmast.setRefDate(sdswe.getPayDate());
                    aqmast.setAqGroup(billGroup.getBillgroupid());
                    aqmast.setAqGroupDesc(billGroup.getBillgroupdesc());
                    aqmast.setPayMonth(paybillTask.getAqmonth());
                    aqmast.setPayYear(paybillTask.getAqyear());
                    aqmast.setAqMonth(paybillTask.getAqmonth());
                    aqmast.setAqYear(paybillTask.getAqyear());
                    aqmast.setAqDay(daysInMonth);
                    aqmast.setPayDay(payworkday.get("payday"));
                    aqmast.setMonBasic(monBasic);
                    
                    if (sdswe.getPayheldup().equals("N")) {
                        aqmast.setCurBasic(curBasic);
                    } else {
                        aqmast.setCurBasic(0);
                    }
                    aqmast.setPayType("42");
                    aqmast.setAqType("42");
                    aqmast.setAcctType(sdswe.getAcctype());
                    aqmast.setAqDate(billMastModel.getBillDate());
                    aqmast.setDemandNumber(billMastModel.getDemandNo());
                    aqmast.setMajorHead(billGroup.getMajorHead());
                    aqmast.setMajorHeadDesc(billGroup.getMajorHeadDesc());
                    aqmast.setSubMajorHead(billGroup.getSubMajorHead());
                    aqmast.setSubMajorHeadDesc(billGroup.getSubMajorHeadDesc());
                    aqmast.setMinorHead(billGroup.getMinorHead());
                    aqmast.setMinorHeadDesc(billGroup.getMinorHeadDesc());
                    aqmast.setSubMinorHead1(billGroup.getSubMinorHead1());
                    aqmast.setSubMinorHeadDesc1(billGroup.getSubMinorHeadDesc1());
                    aqmast.setSubMinorHead2(billGroup.getSubMinorHead2());
                    aqmast.setSubMinorHeadDesc2(billGroup.getSubMinorHeadDesc2());
                    aqmast.setSubMinorHead3(billGroup.getSubMinorHead3());
                    aqmast.setSubMinorHeadDesc3(billGroup.getSubMinorHeadDesc3());
                    aqmast.setPlan(billGroup.getPlan());
                    aqmast.setSector(billGroup.getSector());
                    aqmast.setOffCode(billMastModel.getOffCode());
                    aqmast.setOffDdo(billMastModel.getOffDDO());
                    aqmast.setSecSlNo(secDef.getSecslno());
                    aqmast.setSection(secDef.getSection());
                    aqmast.setPostSlNo(sdswe.getPostslno());
                    aqmast.setCurDesg(sdswe.getPostname());
                    aqmast.setBillNo(billMastModel.getBillNo());
                    aqmast.setSpcOrdNo(sdswe.getOrderno());
                    aqmast.setSpcOrdDate(sdswe.getOrderdate());
                    aqmast.setPayScale(sdswe.getPayscale());
                    aqmast.setCurSpc(sdswe.getSpc());
                    aqmast.setEmpType("R");
                    aqmast.setDeptCode(sdswe.getDepcode());
                    if (aqmast.getAqDate() != null) {
                        String aqslno = aqmastDAO.saveAqmastdata(aqmast);

                        aqmast.setAqSlNo(aqslno);
                        if (sdswe.getPayheldup().equals("N")) {
                            ArrayList allowanceList = allowanceDeductionDAO.getAllowanceList(sdswe, configLvl, aqmast.getAqGroup(), billMastModel.getOffCode(), daysInMonth, payworkday.get("payday"), payworkday.get("payday"), 0, payComponent, billMastModel.getBillType(), aqmast.getDeptCode(), aqmast.getAqMonth(), aqmast.getAqYear());
                            AqDtlsModel[] AqDtlsModelFromAllowanceList = getAqDtlsModelFromAllowanceList(allowanceList, aqmast, payComponent, aqmast.getEmpType(), aqmast.getAqMonth());
                            if (paybillTask.getAqyear() < 2020) {
                                aqDtlsDAO.saveAqdtls1Data(AqDtlsModelFromAllowanceList, false);
                            } else {
                                aqDtlsDAO.saveAqdtlsData(AqDtlsModelFromAllowanceList, false);
                            }
                            ArrayList deductionList = allowanceDeductionDAO.getDeductionList(sdswe, configLvl, aqmast, billMastModel, daysInMonth, payworkday.get("payday"), payworkday.get("payday"), 0, payComponent, gpfSeries);
                            //ArrayList deductionList = allowanceDeductionDAO.getDeductionList(sdswe.getSpc(), sdswe.getEmpid(), configLvl, billGroup.getBillgroupid(), billMastModel.getOffCode(), daysInMonth, payworkday.get("payday"), payworkday.get("payday"), 0, payComponent, billMastModel.getBillType(), aqmast.getDeptCode(), gpfSeries);
                            List principalLoanList = loanSancDAO.getPrincipalLoanListForBill(sdswe.getEmpid(), aqmast.getPayDay(), sdswe.getDepcode());
                            List interestLoanList = loanSancDAO.getInterestLoanListForBill(sdswe.getEmpid(), aqmast.getPayDay(), sdswe.getDepcode());
                            List licList = licDAO.getLicList(sdswe.getEmpid());
                            QuaterAllotment[] qtrallotmentList = empQtrAllotmentDAO.getQuaterAllotmentDetail(sdswe.getEmpid());
                            //AqDtlsModel[] AqDtlsModelFromAllowanceList = allowanceDeductionDAO.getDA(sdswe.getEmpid(),billGroup.getBillgroupid(),payComponent,gross,aqmast);

                            AqDtlsModel[] AqDtlsModelFromDeductionList = getAqDtlsModelFromDeductionList(deductionList, aqmast, payComponent);
                            AqDtlsModel[] AqDtlsModelFromPLoanList = getAqDtlsModelFromPLoanList(principalLoanList, aqmast);
                            AqDtlsModel[] AqDtlsModelFromILoanList = getAqDtlsModelFromILoanList(interestLoanList, aqmast);
                            AqDtlsModel[] AqDtlsModelFromLICList = getAqDtlsModelFromLICList(licList, aqmast);
                            AqDtlsModel[] AqDtlsModelFromQTRList = getAqDtlsModelFromQtrAllotment(qtrallotmentList, aqmast);
                            
                            AqDtlsModel[] AqDtlsModelFromPT = getAqDtlsModelFromPT(aqmast.getGrossAmt(), aqmast);
                            AqDtlsModel[] AqDtlsModelFromCPF = null;

                            boolean stopMonthlySubscrip = false;
                            System.out.println("account type===" + aqmast.getAcctType());
                            if (aqmast.getAcctType() != null && !aqmast.getAcctType().equals("") && (aqmast.getAcctType().equalsIgnoreCase("GPF") || aqmast.getAcctType().equalsIgnoreCase("TPF"))) {
                                stopMonthlySubscrip = aqmastDAO.stopGpfDeduction(aqmast.getEmpCode(), aqmast.getAqMonth(), aqmast.getAqYear());
                            } else if (aqmast.getAcctType() != null && !aqmast.getAcctType().equals("") && aqmast.getAcctType().equalsIgnoreCase("PRAN")) {
                                stopMonthlySubscrip = aqmastDAO.stopNpsDeduction(aqmast.getEmpCode(), aqmast.getAqMonth(), aqmast.getAqYear());
                            }
                            System.out.println("stop subscription==" + stopMonthlySubscrip);

                            if (sdswe.getAcctype().equals("PRAN")) {
                                AqDtlsModelFromCPF = getAqDtlsModelFromCPF(payComponent, aqmast);
                            }
                            if (paybillTask.getAqyear() < 2020) {
                                aqDtlsDAO.saveAqdtls1Data(AqDtlsModelFromDeductionList, stopMonthlySubscrip);
                                aqDtlsDAO.saveAqdtls1Data(AqDtlsModelFromILoanList, stopMonthlySubscrip);
                                aqDtlsDAO.saveAqdtls1Data(AqDtlsModelFromPLoanList, stopMonthlySubscrip);
                                aqDtlsDAO.saveAqdtls1Data(AqDtlsModelFromLICList, stopMonthlySubscrip);
                                aqDtlsDAO.saveAqdtls1Data(AqDtlsModelFromQTRList, stopMonthlySubscrip);
                                aqDtlsDAO.saveAqdtls1Data(AqDtlsModelFromPT, stopMonthlySubscrip);
                                if (sdswe.getAcctype().equals("PRAN")) {
                                    aqDtlsDAO.saveAqdtls1Data(AqDtlsModelFromCPF, stopMonthlySubscrip);
                                }
                            } else {
                                aqDtlsDAO.saveAqdtlsData(AqDtlsModelFromDeductionList, stopMonthlySubscrip);
                                aqDtlsDAO.saveAqdtlsData(AqDtlsModelFromILoanList, stopMonthlySubscrip);
                                aqDtlsDAO.saveAqdtlsData(AqDtlsModelFromPLoanList, stopMonthlySubscrip);
                                aqDtlsDAO.saveAqdtlsData(AqDtlsModelFromLICList, stopMonthlySubscrip);
                                aqDtlsDAO.saveAqdtlsData(AqDtlsModelFromQTRList, stopMonthlySubscrip);
                                aqDtlsDAO.saveAqdtlsData(AqDtlsModelFromPT, stopMonthlySubscrip);
                                if (sdswe.getAcctype().equals("PRAN")) {
                                    aqDtlsDAO.saveAqdtlsData(AqDtlsModelFromCPF, stopMonthlySubscrip);
                                }
                            }

                        }
                    }
                }
            }

        }
    }

    public void cont6regularBillProcess(SectionDefinition secDef, BillGroup billGroup, PaybillTask paybillTask, BillMastModel billMastModel) {

        ArrayList spcwiseemplist = sectionDefinationDAO.getSPCWiseCont6RegularEmpInSection(secDef.getSectionId());
        Calendar myCalendar = new GregorianCalendar(paybillTask.getAqyear(), paybillTask.getAqmonth(), 1);
        Date startDate = myCalendar.getTime();
        //1st date of the month
        myCalendar.set(Calendar.DAY_OF_MONTH, myCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        int daysInMonth = myCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        Date endDate = myCalendar.getTime();
        String configLvl = billGroupDAO.getConfigurationLvl(paybillTask.getBillgroupid());
        int totalEmp = spcwiseemplist.size();
        for (int k = 0; k < spcwiseemplist.size(); k++) {
            slno++;
            System.out.println(k + "/" + totalEmp);
            SectionDtlSPCWiseEmp sdswe = (SectionDtlSPCWiseEmp) spcwiseemplist.get(k);
            
            if (sdswe.getEmpid() == null) {
                AqmastModel aqmast = new AqmastModel();
                aqmast.setSlno(slno);
                aqmast.setAqGroup(billGroup.getBillgroupid());
                aqmast.setAqGroupDesc(billGroup.getBillgroupdesc());
                aqmast.setPayMonth(paybillTask.getAqmonth());
                aqmast.setPayYear(paybillTask.getAqyear());
                aqmast.setAqMonth(paybillTask.getAqmonth());
                aqmast.setAqYear(paybillTask.getAqyear());
                aqmast.setPayType("42");
                aqmast.setAqType("42");
                aqmast.setAqDate(billMastModel.getBillDate());
                aqmast.setPlan(billGroup.getPlan());
                aqmast.setSector(billGroup.getSector());
                aqmast.setMajorHead(billGroup.getMajorHead());
                aqmast.setMajorHeadDesc(billGroup.getMajorHeadDesc());
                aqmast.setSubMajorHead(billGroup.getSubMajorHead());
                aqmast.setSubMajorHeadDesc(billGroup.getSubMajorHeadDesc());
                aqmast.setMinorHead(billGroup.getMinorHead());
                aqmast.setMinorHeadDesc(billGroup.getMinorHeadDesc());
                aqmast.setSubMinorHead1(billGroup.getSubMinorHead1());
                aqmast.setSubMinorHeadDesc1(billGroup.getSubMinorHeadDesc1());
                aqmast.setSubMinorHead2(billGroup.getSubMinorHead2());
                aqmast.setSubMinorHeadDesc2(billGroup.getSubMinorHeadDesc2());
                aqmast.setSubMinorHead3(billGroup.getSubMinorHead3());
                aqmast.setSubMinorHeadDesc3(billGroup.getSubMinorHeadDesc3());
                aqmast.setPlan(billGroup.getPlan());
                aqmast.setSector(billGroup.getSector());
                aqmast.setOffCode(billMastModel.getOffCode());
                aqmast.setOffDdo(billMastModel.getOffDDO());
                aqmast.setSecSlNo(secDef.getSecslno());
                aqmast.setSection(secDef.getSection());
                aqmast.setPostSlNo(sdswe.getPostslno());
                aqmast.setCurDesg(sdswe.getPostname());
                aqmast.setBillNo(billMastModel.getBillNo());
                aqmast.setSpcOrdNo(sdswe.getOrderno());
                aqmast.setSpcOrdDate(sdswe.getOrderdate());
                aqmast.setPayScale(sdswe.getPayscale());
                aqmast.setCurSpc(sdswe.getSpc());
                aqmast.setEmpType("S");
                if (aqmast.getAqDate() != null) {
                    aqmastDAO.saveAqmastdata(aqmast);
                }
            } else {
                PayComponent payComponent = new PayComponent();
                payComponent.setBasic(sdswe.getCurBasicSalary());
                /*Get Basic and GP of the Employee (If increment is in this month then also auto calculate)*/
                HashMap<String, Integer> payworkday = getPayWorkDays(sdswe, startDate, endDate, daysInMonth);

                int monBasic = payComponent.getBasic();
                /*if the employee is under suspension*/
                if (sdswe.getDeptcode() != null && sdswe.getDeptcode().equals("05")) {
                    monBasic = monBasic / 2;
                }
                int curBasic = (monBasic / daysInMonth) * payworkday.get("payday");
                
                
                int defaultBank = 1;
                if (sdswe.getBankcode() == null) {
                    defaultBank = 0;
                }
                
                long newBasic = payComponent.getBasic();
                /*if (payComponent.getIspayrevised() == null || payComponent.getIspayrevised().equalsIgnoreCase("N")) {
                 newBasic = payComponent.getBasic() + payComponent.getGp();
                 } else {
                 newBasic = payComponent.getBasic();
                 }*/
                String gpfSeries = getGPFSeries(sdswe.getGpfaccno());
                
                AqmastModel aqmast = new AqmastModel();
                aqmast.setGrossAmt( new BigDecimal(newBasic).intValueExact());
                aqmast.setSlno(slno);
                aqmast.setEmpCode(sdswe.getEmpid());
                aqmast.setEmpName(sdswe.getEmpname());
                aqmast.setGpfAccNo(sdswe.getGpfaccno());
                aqmast.setGpfType(gpfSeries);
                aqmast.setGazetted(sdswe.getIsgazetted());
                aqmast.setBankAccNo(sdswe.getBankaccno());
                aqmast.setBranchName(sdswe.getBranchcode());
                aqmast.setBankName(sdswe.getBankcode());
                aqmast.setIfscCode(sdswe.getIfscCode());
                aqmast.setRefDate(sdswe.getPayDate());
                aqmast.setAqGroup(billGroup.getBillgroupid());
                aqmast.setAqGroupDesc(billGroup.getBillgroupdesc());
                aqmast.setPayMonth(paybillTask.getAqmonth());
                aqmast.setPayYear(paybillTask.getAqyear());
                aqmast.setAqMonth(paybillTask.getAqmonth());
                aqmast.setAqYear(paybillTask.getAqyear());
                aqmast.setAqDay(daysInMonth);
                aqmast.setPayDay(payworkday.get("payday"));
                aqmast.setMonBasic(newBasic);
                aqmast.setCurBasic(newBasic);
                aqmast.setPayType("42");
                aqmast.setAqType("42");
                aqmast.setAcctType(sdswe.getAcctype());
                aqmast.setAqDate(billMastModel.getBillDate());
                aqmast.setDemandNumber(billMastModel.getDemandNo());
                aqmast.setMajorHead(billGroup.getMajorHead());
                aqmast.setMajorHeadDesc(billGroup.getMajorHeadDesc());
                aqmast.setSubMajorHead(billGroup.getSubMajorHead());
                aqmast.setSubMajorHeadDesc(billGroup.getSubMajorHeadDesc());
                aqmast.setMinorHead(billGroup.getMinorHead());
                aqmast.setMinorHeadDesc(billGroup.getMinorHeadDesc());
                aqmast.setSubMinorHead1(billGroup.getSubMinorHead1());
                aqmast.setSubMinorHeadDesc1(billGroup.getSubMinorHeadDesc1());
                aqmast.setSubMinorHead2(billGroup.getSubMinorHead2());
                aqmast.setSubMinorHeadDesc2(billGroup.getSubMinorHeadDesc2());
                aqmast.setSubMinorHead3(billGroup.getSubMinorHead3());
                aqmast.setSubMinorHeadDesc3(billGroup.getSubMinorHeadDesc3());
                aqmast.setPlan(billGroup.getPlan());
                aqmast.setSector(billGroup.getSector());
                aqmast.setOffCode(billMastModel.getOffCode());
                aqmast.setOffDdo(billMastModel.getOffDDO());
                aqmast.setSecSlNo(secDef.getSecslno());
                aqmast.setSection(secDef.getSection());
                aqmast.setPostSlNo(sdswe.getPostslno());
                aqmast.setCurDesg(sdswe.getPostname());
                aqmast.setBillNo(billMastModel.getBillNo());
                aqmast.setSpcOrdNo(sdswe.getOrderno());
                aqmast.setSpcOrdDate(sdswe.getOrderdate());
                aqmast.setPayScale(sdswe.getPayscale());
                aqmast.setCurSpc(sdswe.getSpc());
                aqmast.setEmpType("S");
                aqmast.setDeptCode(sdswe.getDepcode());
                if (aqmast.getAqDate() != null) {
                    String aqslno = aqmastDAO.saveAqmastdata(aqmast);

                    aqmast.setAqSlNo(aqslno);

                    //ArrayList allowanceList = allowanceDeductionDAO.getAllowanceList(sdswe.getSpc(), sdswe.getEmpid(), configLvl, billGroup.getBillgroupid(), billMastModel.getOffCode(), daysInMonth, payworkday.get("payday"), payworkday.get("payday"), 0, payComponent, billMastModel.getBillType(), aqmast.getDeptCode());
                    ArrayList deductionList = allowanceDeductionDAO.getDeductionList(sdswe, configLvl, billGroup.getBillgroupid(), billMastModel.getOffCode(), daysInMonth, payworkday.get("payday"), payworkday.get("payday"), 0, payComponent, billMastModel.getBillType(), aqmast.getDeptCode(), gpfSeries);
                    List principalLoanList = loanSancDAO.getPrincipalLoanListForBill(sdswe.getEmpid(), aqmast.getPayDay(), sdswe.getDepcode());
                    List interestLoanList = loanSancDAO.getInterestLoanListForBill(sdswe.getEmpid(), aqmast.getPayDay(), sdswe.getDepcode());
                    List licList = licDAO.getLicList(sdswe.getEmpid());
                    QuaterAllotment[] qtrallotmentList = empQtrAllotmentDAO.getQuaterAllotmentDetail(sdswe.getEmpid());
                    //AqDtlsModel[] AqDtlsModelFromAllowanceList = allowanceDeductionDAO.getDA(sdswe.getEmpid(),billGroup.getBillgroupid(),payComponent,gross,aqmast);

                    //AqDtlsModel[] AqDtlsModelFromAllowanceList = getAqDtlsModelForContractualFromAllowanceList(allowanceList, aqmast, payComponent, aqmast.getEmpType(), paybillTask.getAqmonth());
                    //aqDtlsDAO.saveAqdtlsData(AqDtlsModelFromAllowanceList);
                    AqDtlsModel[] AqDtlsModelFromDeductionList = getAqDtlsModelFromDeductionList(deductionList, aqmast, payComponent);
                    AqDtlsModel[] AqDtlsModelFromPLoanList = getAqDtlsModelFromPLoanList(principalLoanList, aqmast);
                    AqDtlsModel[] AqDtlsModelFromILoanList = getAqDtlsModelFromILoanList(interestLoanList, aqmast);
                    AqDtlsModel[] AqDtlsModelFromLICList = getAqDtlsModelFromLICList(licList, aqmast);
                    AqDtlsModel[] AqDtlsModelFromQTRList = getAqDtlsModelFromQtrAllotment(qtrallotmentList, aqmast);
                    AqDtlsModel[] AqDtlsModelFromPT = getAqDtlsModelFromPT(aqmast.getGrossAmt(), aqmast);
                    AqDtlsModel[] AqDtlsModelFromCPF = null;
                    if (sdswe.getAcctype().equals("PRAN")) {
                        AqDtlsModelFromCPF = getAqDtlsModelFromCPF(payComponent, aqmast);
                    }
                    if (paybillTask.getAqyear() < 2020) {
                        aqDtlsDAO.saveAqdtls1Data(AqDtlsModelFromDeductionList, false);
                        aqDtlsDAO.saveAqdtls1Data(AqDtlsModelFromILoanList, false);
                        aqDtlsDAO.saveAqdtls1Data(AqDtlsModelFromPLoanList, false);
                        aqDtlsDAO.saveAqdtls1Data(AqDtlsModelFromLICList, false);
                        aqDtlsDAO.saveAqdtls1Data(AqDtlsModelFromQTRList, false);
                        aqDtlsDAO.saveAqdtls1Data(AqDtlsModelFromPT, false);
                        if (sdswe.getAcctype().equals("PRAN")) {
                            aqDtlsDAO.saveAqdtls1Data(AqDtlsModelFromCPF, false);
                        }
                    } else {
                        aqDtlsDAO.saveAqdtlsData(AqDtlsModelFromDeductionList, false);
                        aqDtlsDAO.saveAqdtlsData(AqDtlsModelFromILoanList, false);
                        aqDtlsDAO.saveAqdtlsData(AqDtlsModelFromPLoanList, false);
                        aqDtlsDAO.saveAqdtlsData(AqDtlsModelFromLICList, false);
                        aqDtlsDAO.saveAqdtlsData(AqDtlsModelFromQTRList, false);
                        aqDtlsDAO.saveAqdtlsData(AqDtlsModelFromPT, false);
                        if (sdswe.getAcctype().equals("PRAN")) {
                            aqDtlsDAO.saveAqdtlsData(AqDtlsModelFromCPF, false);
                        }
                    }

                }
            }

        }
    }

    public int getPayDaysContractual(SectionDtlSPCWiseEmp sdswe, int month, int year, int daysInMonth) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int v_payday = daysInMonth;
        if (sdswe.getJobtypeid() == 1) {
            v_payday = 0;
            try {
                System.out.println("sdswe.getEmpid():" + sdswe.getEmpid() + " month:" + month + "  year:" + year);
                //con = dataSource.getConnection();
                con = this.getDBConnection();
                pstmt = con.prepareStatement("SELECT DAYS_WORKED FROM EMP_ATTENDANCE WHERE EMP_ID=? AND V_MONTH=? AND AT_YEAR=?");
                pstmt.setString(1, sdswe.getEmpid());
                pstmt.setInt(2, month);
                pstmt.setInt(3, year);
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    v_payday = rs.getInt("DAYS_WORKED");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                DataBaseFunctions.closeSqlObjects(rs, pstmt);
                DataBaseFunctions.closeSqlObjects(con);
            }
        }
        return v_payday;
    }

    public void contractualBillProcess(SectionDefinition secDef, BillGroup billGroup, PaybillTask paybillTask, BillMastModel billMastModel) {
        slno++;
        ArrayList spcwiseemplist = sectionDefinationDAO.getSPCWiseContEmpInSection(secDef.getSectionId());
        Calendar myCalendar = new GregorianCalendar(paybillTask.getAqyear(), paybillTask.getAqmonth(), 1);
        Date startDate = myCalendar.getTime();
        //1st date of the month
        myCalendar.set(Calendar.DAY_OF_MONTH, myCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        int daysInMonth = myCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        Date endDate = myCalendar.getTime();
        for (int k = 0; k < spcwiseemplist.size(); k++) {
            slno++;
            long gross = 0;
            SectionDtlSPCWiseEmp sdswe = (SectionDtlSPCWiseEmp) spcwiseemplist.get(k);
            //PayComponent payComponent = getContractualEmployeePayComponent(sdswe.getEmpid(),paybillTask.getAqmonth(),paybillTask.getAqyear());
            HashMap<String, Integer> payworkday = getPayWorkDays(sdswe, startDate, endDate, daysInMonth);
            PayComponent payComponent = new PayComponent();
            payComponent.setBasic(sdswe.getCurBasicSalary());
            payComponent.setGp(sdswe.getGp());
            gross = gross + sdswe.getCurBasicSalary() + sdswe.getGp();
            int payday = 0;
            if (sdswe.getJobtypeid() == 1) {
                payday = getPayDaysContractual(sdswe, billMastModel.getAqMonth() + 1, billMastModel.getAqYear(), daysInMonth);
            } else {
                payday = payworkday.get("payday");
            }
            //int payday = getPayDaysContractual(sdswe, paybillTask.getAqmonth() + 1, paybillTask.getAqyear(), daysInMonth);
            System.out.println("daysInMonth:" + daysInMonth + "daysInMonthsdswe.getEmpid():" + sdswe.getEmpid() + " sdswe.getEmpname()" + sdswe.getEmpname() + "  payday:" + payday + " GPF ACC NO " + sdswe.getGpfaccno() + "sdswe.getAcctype()" + sdswe.getAcctype());
            AqmastModel aqmast = new AqmastModel();
            aqmast.setSlno(slno);
            aqmast.setEmpCode(sdswe.getEmpid());
            aqmast.setEmpName(sdswe.getEmpname());
            aqmast.setGpfAccNo(sdswe.getGpfaccno());
            aqmast.setGpfType(getGPFSeries(sdswe.getGpfaccno()));

            aqmast.setGazetted(sdswe.getIsgazetted());
            aqmast.setBankAccNo(sdswe.getBankaccno());
            aqmast.setBranchName(sdswe.getBranchcode());
            aqmast.setBankName(sdswe.getBankcode());
            aqmast.setIfscCode(sdswe.getIfscCode());
            aqmast.setRefDate(sdswe.getPayDate());
            aqmast.setAqGroup(billGroup.getBillgroupid());
            aqmast.setAqGroupDesc(billGroup.getBillgroupdesc());
            aqmast.setPayMonth(paybillTask.getAqmonth());
            aqmast.setPayYear(paybillTask.getAqyear());
            aqmast.setAqMonth(paybillTask.getAqmonth());
            aqmast.setAqYear(paybillTask.getAqyear());
            aqmast.setAqDay(daysInMonth);
            aqmast.setPayDay(payday);
            aqmast.setMonBasic(sdswe.getCurBasicSalary());
            aqmast.setCurBasic(sdswe.getCurBasicSalary());
            aqmast.setPayType("42");
            aqmast.setAqType("42");
            aqmast.setAcctType(sdswe.getAcctype());
            aqmast.setAqDate(billMastModel.getBillDate());
            aqmast.setDemandNumber(billMastModel.getDemandNo());
            aqmast.setMajorHead(billGroup.getMajorHead());
            aqmast.setMajorHeadDesc(billGroup.getMajorHeadDesc());
            aqmast.setSubMajorHead(billGroup.getSubMajorHead());
            aqmast.setSubMajorHeadDesc(billGroup.getSubMajorHeadDesc());
            aqmast.setMinorHead(billGroup.getMinorHead());
            aqmast.setMinorHeadDesc(billGroup.getMinorHeadDesc());
            aqmast.setSubMinorHead1(billGroup.getSubMinorHead1());
            aqmast.setSubMinorHeadDesc1(billGroup.getSubMinorHeadDesc1());
            aqmast.setSubMinorHead2(billGroup.getSubMinorHead2());
            aqmast.setSubMinorHeadDesc2(billGroup.getSubMinorHeadDesc2());
            aqmast.setSubMinorHead3(billGroup.getSubMinorHead3());
            aqmast.setSubMinorHeadDesc3(billGroup.getSubMinorHeadDesc3());
            aqmast.setPlan(billGroup.getPlan());
            aqmast.setSector(billGroup.getSector());
            aqmast.setOffCode(billMastModel.getOffCode());
            aqmast.setOffDdo(billMastModel.getOffDDO());
            aqmast.setSecSlNo(secDef.getSecslno());
            aqmast.setSection(secDef.getSection());
            aqmast.setPostSlNo(sdswe.getPostslno());
            aqmast.setCurDesg(sdswe.getPostname());
            aqmast.setBillNo(billMastModel.getBillNo());
            aqmast.setSpcOrdNo(sdswe.getOrderno());
            aqmast.setSpcOrdDate(sdswe.getOrderdate());
            aqmast.setPayScale(sdswe.getPayscale());
            aqmast.setCurSpc(sdswe.getSpc());
            aqmast.setEmpType("C");
            aqmast.setDeptCode(sdswe.getDepcode());
            String aqslno = aqmastDAO.saveAqmastdata(aqmast);

            aqmast.setAqSlNo(aqslno);
            sdswe.setDepcode("02");
            ArrayList allowanceList = allowanceDeductionDAO.getAllowanceList(sdswe, billMastModel.getOffCode(), daysInMonth, payday, payday, 0, payComponent);
            ArrayList deductionList = allowanceDeductionDAO.getDeductionList(sdswe, null, billGroup.getBillgroupid(), billMastModel.getOffCode(), daysInMonth, payday, payday, 0, payComponent, billMastModel.getBillType(), sdswe.getDepcode(), "");
            List principalLoanList = loanSancDAO.getPrincipalLoanListForBill(sdswe.getEmpid(), aqmast.getPayDay(), sdswe.getDepcode());
            List interestLoanList = loanSancDAO.getInterestLoanListForBill(sdswe.getEmpid(), aqmast.getPayDay(), sdswe.getDepcode());
            List licList = licDAO.getLicList(sdswe.getEmpid());
            QuaterAllotment[] qtrallotmentList = empQtrAllotmentDAO.getQuaterAllotmentDetail(sdswe.getEmpid());

            AqDtlsModel[] AqDtlsModelFromAllowanceList = getAqDtlsModelForContractualFromAllowanceList(allowanceList, aqmast, payComponent, aqmast.getEmpType(), paybillTask.getAqmonth());
            AqDtlsModel[] AqDtlsModelFromPT = getAqDtlsModelFromPT(gross, aqmast);
            AqDtlsModel[] AqDtlsModelFromPLoanList = getAqDtlsModelFromPLoanList(principalLoanList, aqmast);
            AqDtlsModel[] AqDtlsModelFromILoanList = getAqDtlsModelFromILoanList(interestLoanList, aqmast);
            AqDtlsModel[] AqDtlsModelFromLICList = getAqDtlsModelFromLICList(licList, aqmast);
            AqDtlsModel[] AqDtlsModelFromDeductionList = getAqDtlsModelFromDeductionList(deductionList, aqmast, payComponent);
            AqDtlsModel[] AqDtlsModelFromQTRList = getAqDtlsModelFromQtrAllotment(qtrallotmentList, aqmast);
            AqDtlsModel[] AqDtlsModelFromCPF = null;
            if (sdswe.getAcctype().equals("PRAN")) {
                AqDtlsModelFromCPF = getAqDtlsModelFromCPF(payComponent, aqmast);
            }

            if (paybillTask.getAqyear() < 2020) {

                aqDtlsDAO.saveAqdtls1Data(AqDtlsModelFromAllowanceList, false);
                aqDtlsDAO.saveAqdtls1Data(AqDtlsModelFromDeductionList, false);
                aqDtlsDAO.saveAqdtls1Data(AqDtlsModelFromPT, false);
                aqDtlsDAO.saveAqdtls1Data(AqDtlsModelFromILoanList, false);
                aqDtlsDAO.saveAqdtls1Data(AqDtlsModelFromPLoanList, false);
                aqDtlsDAO.saveAqdtls1Data(AqDtlsModelFromLICList, false);
                aqDtlsDAO.saveAqdtls1Data(AqDtlsModelFromQTRList, false);
                if (sdswe.getAcctype().equals("PRAN")) {
                    aqDtlsDAO.saveAqdtls1Data(AqDtlsModelFromCPF, false);
                }
            } else {
                aqDtlsDAO.saveAqdtlsData(AqDtlsModelFromAllowanceList, false);
                aqDtlsDAO.saveAqdtlsData(AqDtlsModelFromDeductionList, false);
                aqDtlsDAO.saveAqdtlsData(AqDtlsModelFromPT, false);
                aqDtlsDAO.saveAqdtlsData(AqDtlsModelFromILoanList, false);
                aqDtlsDAO.saveAqdtlsData(AqDtlsModelFromPLoanList, false);
                aqDtlsDAO.saveAqdtlsData(AqDtlsModelFromLICList, false);
                aqDtlsDAO.saveAqdtlsData(AqDtlsModelFromQTRList, false);
                if (sdswe.getAcctype().equals("PRAN")) {
                    aqDtlsDAO.saveAqdtlsData(AqDtlsModelFromCPF, false);
                }
            }

        }
    }

    public PayComponent getContractualEmployeePayComponent(String empId, int aqMonth, int aqYear) {
        PayComponent payComponent = new PayComponent();

        return payComponent;
    }

    public String processBill(PaybillTask paybillTask) {
        String status = "S";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean reprocess = true;
        try {
            //con = dataSource.getConnection();
            con = this.getDBConnection();

            pstmt = con.prepareStatement("select bill_status_id from bill_mast where bill_no=?");
            pstmt.setInt(1, paybillTask.getBillid());
            rs = pstmt.executeQuery();
            if (rs.next()) {
                if (rs.getInt("bill_status_id") == 5 || rs.getInt("bill_status_id") == 7) {
                    reprocess = false;
                }
            }

            if (reprocess) {

                if (paybillTask.getAqyear() < 2020) {
                    pstmt = con.prepareStatement("delete from hrmis.aq_dtls1 B using aq_mast C "
                            + "where B.AQSL_NO = C.AQSL_NO AND C.bill_no=?");
                    pstmt.setInt(1, paybillTask.getBillid());
                    pstmt.executeUpdate();
                }

                pstmt = con.prepareStatement("delete from aq_mast where aq_month=? and aq_year=? and bill_no=? ");
                pstmt.setInt(1, paybillTask.getAqmonth());
                pstmt.setInt(2, paybillTask.getAqyear());
                pstmt.setInt(3, paybillTask.getBillid());
                pstmt.executeUpdate();
                /*Get 20 nos of task from paybill task list*/

                //PaybillTask paybillTask = (PaybillTask) paybilltasks.get(i);
                //1st date of the month, 
                System.out.println(paybillTask.getBillgroupid() + ")()()()()()()()()()()()(" + paybillTask.getOffcode());
                Calendar myCalendar = new GregorianCalendar(paybillTask.getAqyear(), paybillTask.getAqmonth() + 1, 1);
                Date startDate = myCalendar.getTime();
                //1st date of the month
                myCalendar.set(Calendar.DAY_OF_MONTH, myCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                int daysInMonth = myCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                Date endDate = myCalendar.getTime();

                //End Date of Month
                BillMastModel billMastModel = billMastDAO.getBillMastDetails(paybillTask.getBillid());
                BillGroup billGroup = billGroupDAO.getBillGroupDetails(new BigDecimal(billMastModel.getBillGroupId()));

                ArrayList sections = sectionDefinationDAO.getBillGroupWiseSectionList(new BigDecimal(billMastModel.getBillGroupId()));
                slno = 1;
                for (int j = 0; j < sections.size(); j++) {
                    SectionDefinition secDef = (SectionDefinition) sections.get(j);
                    System.out.println("bill type==="+secDef.getBillType());
                    if (secDef.getBillType().equals("REGULAR")) {
                        System.out.println("regular");
                        regularBillProcess(secDef, billGroup, paybillTask, billMastModel);
                    } else if (secDef.getBillType().equals("CONTRACTUAL")) {
                        contractualBillProcess(secDef, billGroup, paybillTask, billMastModel);
                    } else if (secDef.getBillType().equals("CONT6_REG")) {
                        cont6regularBillProcess(secDef, billGroup, paybillTask, billMastModel);
                    }
                }
            }
            billMastDAO.markBillAsPrepared(paybillTask.getBillid());
            deletePayBillTask(paybillTask.getBillid());
        } catch (SQLException e) {
            status = "F";
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return status;
    }

    public String getGPFSeries(String gpfaccno) {
        String gpfseries = "";
        if (gpfaccno != null) {
            gpfseries = gpfaccno.replaceAll("[0-9]", "");
        }
        if (gpfseries.lastIndexOf("-") > 0) {
            gpfseries = gpfseries.substring(0, gpfseries.length() - 1);
        }/*
         if (gpfaccno != null) {
         gpfseries = gpfaccno.replaceAll("[^A-Z]", "");
         }*/

        return gpfseries;
    }

    public int getDAAmount(String aqslno) {
        int da = 0;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            //con = dataSource.getConnection();
            con = this.getDBConnection();
            pstmt = con.prepareStatement("select ad_amt from aq_dtls where aqsl_no=? and ad_code='DA'");
            pstmt.setString(1, aqslno);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                da = rs.getInt("ad_amt");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return da;
    }

    public AqDtlsModel[] getAqDtlsModelFromCPF(PayComponent payComponent, AqmastModel aqmast) {
        Connection con = null;
        PreparedStatement pstmt = null;
        Statement stmt = null;
        ResultSet res = null;
        ResultSet res2 = null;
        ArrayList<AqDtlsModel> list = new ArrayList<>();
        try {
            //con = dataSource.getConnection();
            con = this.getDBConnection();
            int da = getDAAmount(aqmast.getAqSlNo());
            pstmt = con.prepareStatement("SELECT FIXEDVALUE FROM UPDATE_AD_INFO WHERE WHERE_UPDATED = 'E' AND UPDATION_REF_CODE = ? AND REF_AD_CODE='76' ");
            pstmt.setString(1, aqmast.getEmpCode());
            res = pstmt.executeQuery();
            int fixedcpf = 0;
            if (res.next()) {
                fixedcpf = res.getInt("FIXEDVALUE");
            } else {
                fixedcpf = new Long(Math.round((payComponent.getBasic() + payComponent.getGp() + da) * 0.1)).intValue();
            }
            pstmt = con.prepareStatement("SELECT AD_CODE,AD_DESC,AD_CODE_NAME,AD_TYPE,ALT_UNIT,DED_TYPE,SCHEDULE,REP_COL,ROW_NO,BT_ID FROM G_AD_LIST WHERE AD_TYPE='D' AND SCHEDULE = 'CPF'");
            res = pstmt.executeQuery();
            if (res.next()) {

                AqDtlsModel aqModel = new AqDtlsModel();
                aqModel.setAqGroup(aqmast.getAqGroup());
                aqModel.setAqSlNo(aqmast.getAqSlNo());
                aqModel.setDdoOff("");
                aqModel.setEmpCode(aqmast.getEmpCode());
                aqModel.setPayMon(aqmast.getPayMonth());
                aqModel.setPayYear(aqmast.getPayYear());
                aqModel.setAqDate(aqmast.getAqDate());
                aqModel.setAqMonth(aqmast.getAqMonth());
                aqModel.setAqYear(aqmast.getAqYear());
                aqModel.setAqType(aqmast.getAqType());
                aqModel.setRefOrderNo(aqmast.getRefOrder());
                aqModel.setRefOrderDate(aqmast.getRefDate());
                aqModel.setSlNo(res.getInt("ROW_NO"));
                aqModel.setAdCode(res.getString("AD_CODE_NAME"));
                aqModel.setAdDesc(res.getString("AD_DESC"));
                aqModel.setAdType(res.getString("AD_TYPE"));
                aqModel.setAltUnit(res.getString("ALT_UNIT"));
                aqModel.setDedType(res.getString("DED_TYPE"));
                aqModel.setAdAmt(fixedcpf);
                aqModel.setAccNo(null);
                aqModel.setRefDesc(null);
                aqModel.setRefCount(0);
                aqModel.setSchedule(res.getString("SCHEDULE"));
                aqModel.setNowDedn(null);
                aqModel.setTotRecAmt(0);
                aqModel.setRepCol(res.getInt("REP_COL"));
                aqModel.setAdRefId(null);
                aqModel.setBtId(res.getString("BT_ID"));
                aqModel.setInstalCount(1);
                list.add(aqModel);
            }
        } catch (SQLException exe) {
            exe.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(res, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        AqDtlsModel aqDtlsModels[] = list.toArray(new AqDtlsModel[list.size()]);
        return aqDtlsModels;
    }

    public AqDtlsModel[] getAqDtlsModelFromPT(double gross, AqmastModel aqmast) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet res = null;
        ArrayList<AqDtlsModel> list = new ArrayList<>();
        try {
            //con = dataSource.getConnection();
            con = this.getDBConnection();
            pstmt = con.prepareStatement("SELECT AD_CODE,AD_DESC,AD_CODE_NAME,AD_TYPE,ALT_UNIT,DED_TYPE,SCHEDULE,REP_COL,ROW_NO,BT_ID FROM G_AD_LIST WHERE AD_TYPE='D' AND SCHEDULE = 'PT'");
            res = pstmt.executeQuery();
            if (res.next()) {
                double annualGross = gross * 12;
                int adamt = 0;
                if (annualGross <= 160000) {
                    adamt = 0;
                } else if (annualGross >= 160001 && annualGross <= 300000) {
                    adamt = 125;
                } else if (annualGross > 300000) {
                    if (aqmast.getAqMonth() + 1 != 2) {
                        adamt = 200;
                    } else {
                        adamt = 300;
                    }
                }

                AqDtlsModel aqModel = new AqDtlsModel();
                aqModel.setAqGroup(aqmast.getAqGroup());
                aqModel.setAqSlNo(aqmast.getAqSlNo());
                aqModel.setDdoOff("");
                aqModel.setEmpCode(aqmast.getEmpCode());
                aqModel.setPayMon(aqmast.getPayMonth());
                aqModel.setPayYear(aqmast.getPayYear());
                aqModel.setAqDate(aqmast.getAqDate());
                aqModel.setAqMonth(aqmast.getAqMonth());
                aqModel.setAqYear(aqmast.getAqYear());
                aqModel.setAqType(aqmast.getAqType());
                aqModel.setRefOrderNo(aqmast.getRefOrder());
                aqModel.setRefOrderDate(aqmast.getRefDate());
                aqModel.setSlNo(res.getInt("ROW_NO"));
                aqModel.setAdCode(res.getString("AD_CODE_NAME"));
                aqModel.setAdDesc(res.getString("AD_DESC"));
                aqModel.setAdType(res.getString("AD_TYPE"));
                aqModel.setAltUnit(res.getString("ALT_UNIT"));
                aqModel.setDedType(res.getString("DED_TYPE"));
                aqModel.setAdAmt(adamt);
                aqModel.setAccNo(null);
                aqModel.setRefDesc(null);
                aqModel.setRefCount(0);
                aqModel.setSchedule(res.getString("SCHEDULE"));
                aqModel.setNowDedn(null);
                aqModel.setTotRecAmt(0);
                aqModel.setRepCol(res.getInt("REP_COL"));
                aqModel.setAdRefId(null);
                aqModel.setBtId(res.getString("BT_ID"));
                aqModel.setInstalCount(1);
                list.add(aqModel);
            }
        } catch (SQLException exe) {
            exe.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        AqDtlsModel aqDtlsModels[] = list.toArray(new AqDtlsModel[list.size()]);
        return aqDtlsModels;
    }

    public AqDtlsModel[] getAqDtlsModelFromQtrAllotment(QuaterAllotment[] qtrallotmentList, AqmastModel aqmast) {
        ArrayList<AqDtlsModel> list = new ArrayList<>();
        int grossAmt = 0;
        for (int i = 0; i < qtrallotmentList.length; i++) {
            QuaterAllotment qtrallotment = qtrallotmentList[i];
            AqDtlsModel aqModel = new AqDtlsModel();
            aqModel.setAqGroup(aqmast.getAqGroup());
            aqModel.setAqSlNo(aqmast.getAqSlNo());
            aqModel.setDdoOff("");
            aqModel.setEmpCode(aqmast.getEmpCode());
            aqModel.setPayMon(aqmast.getPayMonth());
            aqModel.setPayYear(aqmast.getPayYear());
            aqModel.setAqDate(aqmast.getAqDate());
            aqModel.setAqMonth(aqmast.getAqMonth());
            aqModel.setAqYear(aqmast.getAqYear());
            aqModel.setAqType(aqmast.getAqType());
            aqModel.setRefOrderNo(aqmast.getRefOrder());
            aqModel.setRefOrderDate(aqmast.getRefDate());
            aqModel.setSlNo(qtrallotment.getRowno());
            aqModel.setAdCode(qtrallotment.getAdcodename());
            aqModel.setAdDesc(qtrallotment.getAddesc());
            aqModel.setAdType(qtrallotment.getAdtype());
            aqModel.setAltUnit(qtrallotment.getAlunit());
            aqModel.setDedType(qtrallotment.getDedtype());
            aqModel.setAdAmt(qtrallotment.getAmt());
            if (qtrallotment.getAdtype() != null & qtrallotment.getAdtype().equalsIgnoreCase("A")) {
                grossAmt = grossAmt + qtrallotment.getAmt();
                aqmast.setGrossAmt(grossAmt);
            }
            aqModel.setAccNo(null);
            aqModel.setRefDesc(qtrallotment.getQaid() + "");
            aqModel.setRefCount(0);
            aqModel.setSchedule(qtrallotment.getSchedule());
            aqModel.setNowDedn(null);
            aqModel.setTotRecAmt(0);
            aqModel.setRepCol(qtrallotment.getRepcol());
            aqModel.setAdRefId(null);
            aqModel.setBtId(qtrallotment.getBtid());
            aqModel.setInstalCount(1);
            list.add(aqModel);
        }

        AqDtlsModel aqDtlsModels[] = list.toArray(new AqDtlsModel[list.size()]);
        return aqDtlsModels;
    }

    public AqDtlsModel[] getAqDtlsModelFromLICList(List licList, AqmastModel aqmast) {
        ArrayList<AqDtlsModel> list = new ArrayList<>();
        for (int i = 0; i < licList.size(); i++) {
            Lic licdata = (Lic) licList.get(i);
            if (licdata.getStatus().equalsIgnoreCase("Active")) {
                AqDtlsModel aqModel = new AqDtlsModel();
                aqModel.setAqGroup(aqmast.getAqGroup());
                aqModel.setAqSlNo(aqmast.getAqSlNo());
                aqModel.setDdoOff("");
                aqModel.setEmpCode(aqmast.getEmpCode());
                aqModel.setPayMon(aqmast.getPayMonth());
                aqModel.setPayYear(aqmast.getPayYear());
                aqModel.setAqDate(aqmast.getAqDate());
                aqModel.setAqMonth(aqmast.getAqMonth());
                aqModel.setAqYear(aqmast.getAqYear());
                aqModel.setAqType(aqmast.getAqType());
                aqModel.setRefOrderNo(aqmast.getRefOrder());
                aqModel.setRefOrderDate(aqmast.getRefDate());
                aqModel.setSlNo(1);
                aqModel.setAdCode(licdata.getInsuranceType());
                aqModel.setAdDesc(licdata.getInsuranceType());
                aqModel.setAdType("D");
                aqModel.setAltUnit(null);
                aqModel.setDedType("V");
                aqModel.setAdAmt(licdata.getSubAmount());
                aqModel.setAccNo(licdata.getPolicyNo());
                aqModel.setRefDesc(null);
                aqModel.setRefCount(0);
                aqModel.setSchedule(licdata.getInsuranceType());
                aqModel.setNowDedn(null);
                aqModel.setTotRecAmt(0);
                aqModel.setRepCol(9);
                aqModel.setAdRefId(licdata.getElId() + "");
                aqModel.setBtId(licdata.getBtid());
                aqModel.setInstalCount(1);
                list.add(aqModel);
            }
        }
        AqDtlsModel aqDtlsModels[] = list.toArray(new AqDtlsModel[list.size()]);
        return aqDtlsModels;
    }

    public AqDtlsModel[] getAqDtlsModelFromPLoanList(List principalLoanList, AqmastModel aqmast) {
        ArrayList<AqDtlsModel> list = new ArrayList<>();
        for (int i = 0; i < principalLoanList.size(); i++) {
            Loan loanForm = (Loan) principalLoanList.get(i);
            AqDtlsModel aqModel = new AqDtlsModel();
            aqModel.setAqGroup(aqmast.getAqGroup());
            aqModel.setAqSlNo(aqmast.getAqSlNo());
            aqModel.setDdoOff("");
            aqModel.setEmpCode(aqmast.getEmpCode());
            aqModel.setPayMon(aqmast.getPayMonth());
            aqModel.setPayYear(aqmast.getPayYear());
            aqModel.setAqDate(aqmast.getAqDate());
            aqModel.setAqMonth(aqmast.getAqMonth());
            aqModel.setAqYear(aqmast.getAqYear());
            aqModel.setAqType(aqmast.getAqType());
            aqModel.setRefOrderNo(aqmast.getRefOrder());
            aqModel.setRefOrderDate(aqmast.getRefDate());
            aqModel.setSlNo(loanForm.getRowno());
            aqModel.setAdCode(loanForm.getSltloan());
            aqModel.setAdDesc(loanForm.getLoanName());
            aqModel.setAdType("D");
            aqModel.setAltUnit(null);
            aqModel.setDedType("L");
            aqModel.setAdAmt(loanForm.getInstalmentAmount());
            aqModel.setAccNo(null);
            aqModel.setRefDesc(loanForm.getRefDesc());
            aqModel.setRefCount(loanForm.getRefCount());
            aqModel.setSchedule(loanForm.getSltloan());
            aqModel.setNowDedn(loanForm.getNowdedn());
            aqModel.setTotRecAmt(loanForm.getCummrecovered());
            aqModel.setRepCol(loanForm.getRepcol());
            aqModel.setAdRefId(loanForm.getLoanid());
            if (loanForm.getSltloan().equals("FA")) {
                aqModel.setBtId(loanSancDAO.getFABTId(aqmast.getDemandNumber()));
            } else if (loanForm.getSltloan().equals("GA")) {
                aqModel.setBtId(loanSancDAO.getGPFBTId(aqmast.getGpfType()));
            } else {
                aqModel.setBtId(loanForm.getBtid());
            }
            aqModel.setInstalCount(1);
            list.add(aqModel);
        }
        AqDtlsModel aqDtlsModels[] = list.toArray(new AqDtlsModel[list.size()]);
        return aqDtlsModels;
    }

    public AqDtlsModel[] getAqDtlsModelFromILoanList(List interestLoanList, AqmastModel aqmast) {
        ArrayList<AqDtlsModel> list = new ArrayList<>();
        for (int i = 0; i < interestLoanList.size(); i++) {
            Loan loanForm = (Loan) interestLoanList.get(i);
            AqDtlsModel aqModel = new AqDtlsModel();
            aqModel.setAqGroup(aqmast.getAqGroup());
            aqModel.setAqSlNo(aqmast.getAqSlNo());
            aqModel.setDdoOff("");
            aqModel.setEmpCode(aqmast.getEmpCode());
            aqModel.setPayMon(aqmast.getPayMonth());
            aqModel.setPayYear(aqmast.getPayYear());
            aqModel.setAqDate(aqmast.getAqDate());
            aqModel.setAqMonth(aqmast.getAqMonth());
            aqModel.setAqYear(aqmast.getAqYear());
            aqModel.setAqType(aqmast.getAqType());
            aqModel.setRefOrderNo(aqmast.getRefOrder());
            aqModel.setRefOrderDate(aqmast.getRefDate());
            aqModel.setSlNo(loanForm.getRowno());
            aqModel.setAdCode(loanForm.getSltloan());
            aqModel.setAdDesc(loanForm.getLoanName());
            aqModel.setAdType("D");
            aqModel.setAltUnit(null);
            aqModel.setDedType("L");
            aqModel.setAdAmt(loanForm.getInstalmentAmount());
            aqModel.setAccNo(null);
            aqModel.setRefDesc(loanForm.getRefDesc());
            aqModel.setRefCount(0);
            aqModel.setSchedule(loanForm.getSltloan());
            aqModel.setNowDedn(loanForm.getNowdedn());

            aqModel.setTotRecAmt(loanForm.getCummrecovered());
            aqModel.setRepCol(loanForm.getRepcol());
            aqModel.setAdRefId(loanForm.getLoanid());
            aqModel.setBtId(loanForm.getBtid());
            aqModel.setInstalCount(1);
            list.add(aqModel);
        }
        AqDtlsModel aqDtlsModels[] = list.toArray(new AqDtlsModel[list.size()]);
        return aqDtlsModels;
    }

    public AqDtlsModel[] getAqDtlsModelFromDeductionList(ArrayList allowanceList, AqmastModel aqmast, PayComponent payComponent) {
        ArrayList<AqDtlsModel> list = new ArrayList<>();

        for (int i = 0; i < allowanceList.size(); i++) {
            AllowanceDeduction alwded = (AllowanceDeduction) allowanceList.get(i);
            long adAmt = alwded.getAdvalue();
            if (aqmast.getAcctType().equals("PRAN") && alwded.getAdcodename().equals("GPF")) {
                adAmt = 0;
            }
            AqDtlsModel aqModel = new AqDtlsModel();
            aqModel.setAqGroup(aqmast.getAqGroup());
            aqModel.setAqSlNo(aqmast.getAqSlNo());
            aqModel.setDdoOff("");
            aqModel.setEmpCode(aqmast.getEmpCode());
            aqModel.setPayMon(aqmast.getPayMonth());
            aqModel.setPayYear(aqmast.getPayYear());
            aqModel.setAqDate(aqmast.getAqDate());
            aqModel.setAqMonth(aqmast.getAqMonth());
            aqModel.setAqYear(aqmast.getAqYear());
            aqModel.setAqType(aqmast.getAqType());
            aqModel.setRefOrderNo(aqmast.getRefOrder());
            aqModel.setRefOrderDate(aqmast.getRefDate());
            aqModel.setSlNo(alwded.getRownum());
            aqModel.setAdCode(alwded.getAdcodename());
            aqModel.setAdDesc(alwded.getAddesc());
            aqModel.setAdType(alwded.getAdamttype());
            aqModel.setDedType(alwded.getDedType());
            aqModel.setAltUnit(alwded.getAltUnit());
            aqModel.setDedType(alwded.getDedType());
            aqModel.setAdAmt(adAmt);
            aqModel.setAccNo(null);
            aqModel.setRefDesc(null);
            aqModel.setRefCount(0);
            aqModel.setSchedule(alwded.getSchedule());
            aqModel.setNowDedn(null);
            aqModel.setTotRecAmt(0);
            aqModel.setRepCol(alwded.getRepCol());
            aqModel.setAdRefId(null);
            aqModel.setBtId(alwded.getHead());
            aqModel.setInstalCount(0);
            list.add(aqModel);
        }
        AqDtlsModel aqDtlsModels[] = list.toArray(new AqDtlsModel[list.size()]);
        return aqDtlsModels;
    }

    public AqDtlsModel[] getAqDtlsModelForContractualFromAllowanceList(ArrayList allowanceList, AqmastModel aqmast, PayComponent payComponent, String empType, int aqmonth) {
        ArrayList<AqDtlsModel> list = new ArrayList<>();
        long da = 0;
        int grossAmt = 0;
        if (aqmast.getEmpType().equals("R")) {
            if (payComponent.getIspayrevised() == null || payComponent.getIspayrevised().equalsIgnoreCase("N")) {
                if (aqmonth == 3) {
                    da = Math.round(((payComponent.getBasic() + payComponent.getGp()) * 1.42));
                } else {
                    da = Math.round(((payComponent.getBasic() + payComponent.getGp()) * 1.39));
                }
            } else {
                if (aqmonth == 3) {
                    da = Math.round(payComponent.getBasic() * 0.07);
                } else {
                    da = Math.round(payComponent.getBasic() * 0.05);
                }
            }
        }
        for (int i = 0; i < allowanceList.size(); i++) {
            AllowanceDeduction alwded = (AllowanceDeduction) allowanceList.get(i);
            int adAmt = alwded.getAdvalue();
            /*if (alwded.getAdcodename().equalsIgnoreCase("GP")) {
             adAmt = 0;
             }*/
            if (alwded.getAdcodename().equalsIgnoreCase("DA")) {
                adAmt = new BigDecimal(da).intValueExact();
            }
            /*if (alwded.getAdcodename().equalsIgnoreCase("CPF")) {
             adAmt = (int) ((aqmast.getCurBasic() + da) * 0.1);
             }*/
            grossAmt = grossAmt + adAmt;
            AqDtlsModel aqModel = new AqDtlsModel();
            aqModel.setAqGroup(aqmast.getAqGroup());
            aqModel.setAqSlNo(aqmast.getAqSlNo());
            aqModel.setDdoOff("");
            aqModel.setEmpCode(aqmast.getEmpCode());
            aqModel.setPayMon(aqmast.getPayMonth());
            aqModel.setPayYear(aqmast.getPayYear());
            aqModel.setAqDate(aqmast.getAqDate());
            aqModel.setAqMonth(aqmast.getAqMonth());
            aqModel.setAqYear(aqmast.getAqYear());
            aqModel.setAqType(aqmast.getAqType());
            aqModel.setRefOrderNo(aqmast.getRefOrder());
            aqModel.setRefOrderDate(aqmast.getRefDate());
            aqModel.setSlNo(alwded.getRownum());
            aqModel.setAdCode(alwded.getAdcodename());
            aqModel.setAdDesc(alwded.getAddesc());
            aqModel.setAdType(alwded.getAdamttype());
            aqModel.setAltUnit(alwded.getAltUnit());
            aqModel.setDedType(alwded.getDedType());
            aqModel.setAdAmt(adAmt);
            aqModel.setAccNo(null);
            aqModel.setRefDesc(null);
            aqModel.setRefCount(0);
            aqModel.setSchedule(alwded.getSchedule());
            aqModel.setNowDedn(null);
            aqModel.setTotRecAmt(0);
            aqModel.setRepCol(alwded.getRepCol());
            aqModel.setAdRefId(null);
            //////if (empType.equals("R")) {
            aqModel.setBtId(alwded.getHead());
            //} else {
            //aqModel.setBtId("000");
            //}
            aqModel.setInstalCount(0);
            list.add(aqModel);
        }
        aqmast.setGrossAmt(grossAmt);
        AqDtlsModel aqDtlsModels[] = list.toArray(new AqDtlsModel[list.size()]);
        return aqDtlsModels;
    }

    public AqDtlsModel[] getAqDtlsModelFromAllowanceList(ArrayList allowanceList, AqmastModel aqmast, PayComponent payComponent, String empType, int aqmonth) {
        ArrayList<AqDtlsModel> list = new ArrayList<>();
        int grossAmt = 0;
        for (int i = 0; i < allowanceList.size(); i++) {
            AllowanceDeduction alwded = (AllowanceDeduction) allowanceList.get(i);
            int adAmt = alwded.getAdvalue();
            /*if (alwded.getAdcodename().equalsIgnoreCase("GP")) {
             adAmt = 0;
             }*/
            if (alwded.getAdcodename().equalsIgnoreCase("DA")) {
                if (!aqmast.getEmpType().equals("R")) {
                    adAmt = 0;
                }
            }
            /*if (alwded.getAdcodename().equalsIgnoreCase("CPF")) {
             adAmt = (int) ((aqmast.getCurBasic() + da) * 0.1);
             }*/

            grossAmt = grossAmt + adAmt;
            AqDtlsModel aqModel = new AqDtlsModel();
            aqModel.setAqGroup(aqmast.getAqGroup());
            aqModel.setAqSlNo(aqmast.getAqSlNo());
            aqModel.setDdoOff("");
            aqModel.setEmpCode(aqmast.getEmpCode());
            aqModel.setPayMon(aqmast.getPayMonth());
            aqModel.setPayYear(aqmast.getPayYear());
            aqModel.setAqDate(aqmast.getAqDate());
            aqModel.setAqMonth(aqmast.getAqMonth());
            aqModel.setAqYear(aqmast.getAqYear());
            aqModel.setAqType(aqmast.getAqType());
            aqModel.setRefOrderNo(aqmast.getRefOrder());
            aqModel.setRefOrderDate(aqmast.getRefDate());
            aqModel.setSlNo(alwded.getRownum());
            aqModel.setAdCode(alwded.getAdcodename());
            aqModel.setAdDesc(alwded.getAddesc());
            aqModel.setAdType(alwded.getAdamttype());
            aqModel.setAltUnit(alwded.getAltUnit());
            aqModel.setDedType(alwded.getDedType());
            aqModel.setAdAmt(adAmt);
            aqModel.setAccNo(null);
            aqModel.setRefDesc(null);
            aqModel.setRefCount(0);
            aqModel.setSchedule(alwded.getSchedule());
            aqModel.setNowDedn(null);
            aqModel.setTotRecAmt(0);
            aqModel.setRepCol(alwded.getRepCol());
            aqModel.setAdRefId(null);
            if (empType.equals("R")) {
                aqModel.setBtId(alwded.getHead());
            } else {
                aqModel.setBtId("000");
            }
            aqModel.setInstalCount(0);
            
            list.add(aqModel);
        }
        aqmast.setGrossAmt(grossAmt);
        AqDtlsModel aqDtlsModels[] = list.toArray(new AqDtlsModel[list.size()]);
        return aqDtlsModels;
    }

    public HashMap getPayWorkDays(SectionDtlSPCWiseEmp sdswe, Date monstartDate, Date monendDate, int v_aqday) {
        HashMap<String, Integer> payworkday = new HashMap<String, Integer>();
        int v_payday = 0;
        int v_workday = 0;
        payworkday.put("payday", v_payday);
        payworkday.put("workday", v_workday);
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        System.out.println(sdswe.getSpc() + "*****" + sdswe.getEmpid());
        try {

            /*Get month and year from date*/
            Calendar tcal = Calendar.getInstance();
            tcal.setTime(monstartDate);
            int year = tcal.get(Calendar.YEAR);
            int month = tcal.get(Calendar.MONTH);
            /*Get month and year from date*/

            int jday = 0;
            int v_rday = 0;
            int totdays = 0;
            int v_fday = 0;
            int v_jday = 0;
            int v_tday = 0;
            int v_fday1 = 0;
            int v_tday1 = 0;
            Calendar cal = Calendar.getInstance();
            Date v_ifjoinedthismonth = null;
            String v_jtime = null;
            Date v_ifrelievedthismonth = null;
            String v_rtime = null;

            long diff = monendDate.getTime() - monstartDate.getTime();
            long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            totdays = (int) days + 1;
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT JOIN_DATE, JOIN_TIME FROM EMP_JOIN WHERE SPC=? AND  EMP_ID=? AND date_part('year', JOIN_DATE) = ? and date_part('month', JOIN_DATE)=?");
            pstmt.setString(1, sdswe.getSpc());
            pstmt.setString(2, sdswe.getEmpid());
            pstmt.setInt(3, year);
            pstmt.setInt(4, month + 1);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                if (rs.getDate("JOIN_DATE") != null) {
                    v_ifjoinedthismonth = rs.getDate("JOIN_DATE");
                    v_jtime = rs.getString("JOIN_TIME");
                    cal.setTime(v_ifjoinedthismonth);
                    v_jday = cal.get(Calendar.DAY_OF_MONTH);
                }
            }
            System.out.println("year:" + year + "  month:" + month);
            System.out.println("spc:" + sdswe.getSpc() + "   empid:" + sdswe.getEmpid());
            pstmt = con.prepareStatement("SELECT RLV_DATE,RLV_TIME FROM EMP_RELIEVE WHERE SPC = ? AND EMP_ID=? AND date_part('year', RLV_DATE) = ? and date_part('month', RLV_DATE)=?");
            pstmt.setString(1, sdswe.getSpc());
            pstmt.setString(2, sdswe.getEmpid());
            pstmt.setInt(3, year);
            pstmt.setInt(4, month);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                if (rs.getDate("RLV_DATE") != null) {
                    v_ifrelievedthismonth = rs.getDate("RLV_DATE");
                    v_rtime = rs.getString("RLV_TIME");
                    cal.setTime(v_ifrelievedthismonth);
                    v_rday = cal.get(Calendar.DAY_OF_MONTH);
                }
            }
            System.out.println("v_rday:" + v_rday);
            /**
             * ***********************IF JOINED THIS MONTH AND RELIEVED THIS
             * MONTH FROM SAME POST**************
             */
            if (v_ifjoinedthismonth != null && v_ifrelievedthismonth != null) {
                if (v_rday > v_jday) {
                    v_fday = v_jday;
                    if (v_rtime.equalsIgnoreCase("FN")) {
                        totdays = v_rday - v_jday;
                        v_tday = v_rday - 1;
                    } else {
                        totdays = v_rday - v_jday + 1;
                        v_tday = v_rday;
                    }
                } else {
                    v_fday = 1;
                    v_fday1 = v_jday;
                    v_tday1 = v_aqday;
                    if (v_rtime.equalsIgnoreCase("FN")) {
                        totdays = (v_aqday - v_jday) + v_rday;
                        v_tday = v_rday - 1;
                    } else {
                        totdays = (v_aqday - v_jday) + 1 + v_rday;
                        v_tday = v_rday;
                    }
                }
            }
            /**
             * *IF JOINED THIS MONTH AND RELIEVED THIS MONTH FROM SAME POST**
             */
            /**
             * *IF JOINED THIS MONTH AND NOT RELIEVED THIS MONTH FROM SAME
             * POST*
             */
            if (v_ifjoinedthismonth != null && v_ifrelievedthismonth == null) {
                totdays = (v_aqday - v_jday) + 1;
                v_fday = v_jday;
                v_tday = v_aqday;
            }

            /**
             * IF JOINED THIS MONTH AND NOT RELIEVED THIS MONTH FROM SAME POST*
             */
            /**
             * ***********************IF RELIEVED THIS MONTH AND NOT JOINED
             * THIS MONTH IN SAME POST**************
             */
            if (v_ifjoinedthismonth == null && v_ifrelievedthismonth != null) {
                v_fday = 1;
                if (v_rtime.equalsIgnoreCase("FN")) {
                    totdays = v_rday - 1;
                    v_tday = v_rday - 1;
                } else {
                    totdays = v_rday;
                    v_tday = v_rday;
                }
            }
            /**
             * ****IF RELIEVED THIS MONTH AND NOT JOINED THIS MONTH IN SAME
             * POST***
             */
            /**
             * NOT JOINED THIS MONTH AND NOT RELIEVED THIS MONTH
             */
            if (v_ifjoinedthismonth == null && v_ifrelievedthismonth != null) {
                totdays = v_aqday;
                v_fday = 1;
                v_tday = v_aqday;
            }

            /**
             * NOT JOINED THIS MONTH AND NOT RELIEVED THIS MONTH
             */
            /**
             * CALCULATING TOTAL DAYS ABSENT*
             */
            int v_totalAbsent = 0;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String strmonstartDate = simpleDateFormat.format(monstartDate);
            String strmonendDate = simpleDateFormat.format(monendDate);
            pstmt = con.prepareStatement("SELECT ABS_FROM,ABS_TO FROM EMP_ABSENTEE where EMP_ID = ? and (ABS_FROM BETWEEN to_date(?, 'YYYY-MM-DD') AND to_date(?, 'YYYY-MM-DD') or ABS_TO BETWEEN to_date(?, 'YYYY-MM-DD') AND to_date(?, 'YYYY-MM-DD'))");
            pstmt.setString(1, sdswe.getEmpid());
            pstmt.setString(2, strmonstartDate);
            pstmt.setString(3, strmonendDate);
            pstmt.setString(4, strmonstartDate);
            pstmt.setString(5, strmonendDate);
            rs = pstmt.executeQuery();
            Date tmonstartDate = new SimpleDateFormat("yyyy-MM-dd").parse(strmonstartDate);
            Date tmonendDate = new SimpleDateFormat("yyyy-MM-dd").parse(strmonendDate);
            while (rs.next()) {
                Date absfrom = rs.getDate("ABS_FROM");
                Date absto = rs.getDate("ABS_TO");
                if (absfrom.compareTo(tmonstartDate) >= 0 && absto.compareTo(tmonendDate) <= 0) {//Absent from and to with in this month
                    diff = absto.getTime() - absfrom.getTime();
                    days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                    v_totalAbsent = v_totalAbsent + (int) days;
                    v_totalAbsent++;
                } else if (absfrom.compareTo(tmonstartDate) < 0 && absto.compareTo(tmonendDate) <= 0) {//absent from beftore this month but absent to in this month
                    diff = absto.getTime() - monstartDate.getTime();
                    days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                    v_totalAbsent = v_totalAbsent + (int) days;
                } else if (absfrom.compareTo(tmonstartDate) >= 0 && absto.compareTo(tmonendDate) > 0) {//absent from in this month but absent to after this month
                    diff = monendDate.getTime() - absfrom.getTime();
                    days = TimeUnit.DAYS.convert(diff, TimeUnit.SECONDS);
                    v_totalAbsent = v_totalAbsent + (int) days;
                } else if (absfrom.compareTo(tmonstartDate) < 0 && absto.compareTo(tmonendDate) > 0) {//absent from beftore this month but absent to after this month
                    diff = monstartDate.getTime() - monendDate.getTime();
                    days = TimeUnit.DAYS.convert(diff, TimeUnit.SECONDS);
                    v_totalAbsent = v_totalAbsent + (int) days;
                }
            }
            /**
             * CALCULATING TOTAL DAYS ABSENT*
             */
            /**
             * CALCULATING TOTAL DAYS SANCTIONED*
             */

            int v_sanctionavailed = 0;
            /*
             pstmt = con.prepareStatement("SELECT FDATE,TDATE FROM(SELECT FDATE,TDATE FROM EMP_LEAVE WHERE EMP_ID = ? and IF_LONGTERM != 'Y' and AND LSOT_ID='01' and (ABS_FROM BETWEEN ? AND ? or ABS_TO BETWEEN ? AND ?))EMP_LEAVE INNER JOIN "
             + "(SELECT * FROM EMP_NOTIFICATION WHERE EMP_ID = ? AND NOT_TYPE='LEAVE' AND EMP_NOTIFICATION.LINK_ID IS NULL AND STATUS='SANCTIONED AND AVAILED') EMP_NOTIFICATION ON EMP_LEAVE.NOT_ID=EMP_NOTIFICATION.NOT_ID)");
             pstmt.setString(1, sdswe.getEmpid());
             pstmt.setDate(2, new java.sql.Date(monstartDate.getTime()));
             pstmt.setDate(3, new java.sql.Date(monendDate.getTime()));
             pstmt.setDate(4, new java.sql.Date(monstartDate.getTime()));
             pstmt.setDate(5, new java.sql.Date(monendDate.getTime()));
             pstmt.setString(6, sdswe.getEmpid());
             rs = pstmt.executeQuery();
             while (rs.next()) {
             Date sancfrom = rs.getDate("FDATE");
             Date sancto = rs.getDate("TDATE");
             if (sancfrom.compareTo(monstartDate) >= 0 && sancto.compareTo(monendDate) <= 0) {//Absent from and to with in this month
             long diff = sancto.getTime() - sancfrom.getTime();
             long days = TimeUnit.DAYS.convert(diff, TimeUnit.SECONDS);
             v_sanctionavailed = v_sanctionavailed + (int) days;
             } else if (sancfrom.compareTo(monstartDate) < 0 && sancto.compareTo(monendDate) <= 0) {//absent from beftore this month but absent to in this month
             long diff = sancto.getTime() - monstartDate.getTime();
             long days = TimeUnit.DAYS.convert(diff, TimeUnit.SECONDS);
             v_sanctionavailed = v_sanctionavailed + (int) days;
             } else if (sancfrom.compareTo(monstartDate) >= 0 && sancto.compareTo(monendDate) > 0) {//absent from in this month but absent to after this month
             long diff = monendDate.getTime() - sancfrom.getTime();
             long days = TimeUnit.DAYS.convert(diff, TimeUnit.SECONDS);
             v_sanctionavailed = v_sanctionavailed + (int) days;
             } else if (sancfrom.compareTo(monstartDate) < 0 && sancto.compareTo(monendDate) > 0) {//absent from beftore this month but absent to after this month
             long diff = monstartDate.getTime() - monendDate.getTime();
             long days = TimeUnit.DAYS.convert(diff, TimeUnit.SECONDS);
             v_sanctionavailed = v_sanctionavailed + (int) days;
             }
             }*/
            /**
             * CALCULATING TOTAL DAYS SANCTIONED*
             */
            /* CALCULATING JT TIME PERIOD */
            int v_jtavailed = 0;
            pstmt = con.prepareStatement("SELECT JOIN_TIME_FROM,JOIN_TIME_TO FROM EMP_LEAVE WHERE EMP_ID = ? AND (JOIN_TIME_FROM <= ? AND JOIN_TIME_TO >= ?)");
            pstmt.setString(1, sdswe.getEmpid());
            pstmt.setDate(2, new java.sql.Date(monstartDate.getTime()));
            pstmt.setDate(3, new java.sql.Date(monendDate.getTime()));
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Date joiningfrom = rs.getDate("FDATE");
                Date joiningto = rs.getDate("TDATE");
                if (joiningfrom.compareTo(monstartDate) >= 0 && joiningto.compareTo(monendDate) <= 0) {//Absent from and to with in this month
                    diff = joiningto.getTime() - joiningfrom.getTime();
                    days = TimeUnit.DAYS.convert(diff, TimeUnit.SECONDS);
                    v_jtavailed = v_jtavailed + (int) days;
                } else if (joiningfrom.compareTo(monstartDate) < 0 && joiningto.compareTo(monendDate) <= 0) {//absent from beftore this month but absent to in this month
                    diff = joiningto.getTime() - monstartDate.getTime();
                    days = TimeUnit.DAYS.convert(diff, TimeUnit.SECONDS);
                    v_jtavailed = v_jtavailed + (int) days;
                } else if (joiningfrom.compareTo(monstartDate) >= 0 && joiningto.compareTo(monendDate) > 0) {//absent from in this month but absent to after this month
                    diff = monendDate.getTime() - joiningfrom.getTime();
                    days = TimeUnit.DAYS.convert(diff, TimeUnit.SECONDS);
                    v_jtavailed = v_jtavailed + (int) days;
                } else if (joiningfrom.compareTo(monstartDate) < 0 && joiningto.compareTo(monendDate) > 0) {//absent from beftore this month but absent to after this month
                    diff = monstartDate.getTime() - monendDate.getTime();
                    days = TimeUnit.DAYS.convert(diff, TimeUnit.SECONDS);
                    v_jtavailed = v_jtavailed + (int) days;
                }
            }
            /* CALCULATING JT TIME PERIOD */
            if (v_rday > 0) {
                v_payday = v_rday - (v_totalAbsent - v_sanctionavailed);
            } else {
                v_payday = totdays - (v_totalAbsent - v_sanctionavailed);
            }
            v_workday = totdays - v_totalAbsent;
            if (v_payday < 0) {
                v_payday = 0;
            }
            if (v_workday < 0) {
                v_workday = 0;
            }
            /*payworkday.put("payday", v_payday);
             payworkday.put("workday", v_workday);
             */
            payworkday.put("payday", v_payday);
            payworkday.put("workday", v_workday);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return payworkday;
    }

    public HashMap getPayWorkDays(AqmastModel aqMastModel, SectionDtlSPCWiseEmp sdswe, Date monstartDate, Date monendDate, int v_aqday) {
        HashMap<String, Integer> payworkday = new HashMap<String, Integer>();
        int v_payday = 0;
        int v_workday = 0;
        payworkday.put("payday", v_payday);
        payworkday.put("workday", v_workday);
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        if (sdswe.getSpc().equals("OLSAGR00100000105880004")) {
            System.out.println("____________________________________________________________________________");
        }

        try {

            /*Get month and year from date*/
            Calendar tcal = Calendar.getInstance();
            tcal.setTime(monstartDate);
            int year = tcal.get(Calendar.YEAR);
            int month = tcal.get(Calendar.MONTH);
            /*Get month and year from date*/

            int jday = 0;
            int v_rday = 0;
            int totdays = 0;
            int v_fday = 0;
            int v_jday = 0;
            int v_tday = 0;
            int v_fday1 = 0;
            int v_tday1 = 0;
            Calendar cal = Calendar.getInstance();
            Date v_ifjoinedthismonth = null;
            String v_jtime = null;
            Date v_ifrelievedthismonth = null;
            String v_rtime = null;

            long diff = monendDate.getTime() - monstartDate.getTime();
            long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            totdays = (int) days + 1;
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT JOIN_DATE, JOIN_TIME FROM EMP_JOIN WHERE SPC=? AND  EMP_ID=? AND date_part('year', JOIN_DATE) = ? and date_part('month', JOIN_DATE)=?");
            pstmt.setString(1, aqMastModel.getCurSpc());
            pstmt.setString(2, aqMastModel.getEmpCode());
            pstmt.setInt(3, year);
            pstmt.setInt(4, month + 1);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                if (rs.getDate("JOIN_DATE") != null) {
                    v_ifjoinedthismonth = rs.getDate("JOIN_DATE");
                    v_jtime = rs.getString("JOIN_TIME");
                    cal.setTime(v_ifjoinedthismonth);
                    v_jday = cal.get(Calendar.DAY_OF_MONTH);
                }
            }
            System.out.println("year:" + year + "  month:" + month);
            System.out.println("spc:" + aqMastModel.getCurSpc() + "   empid:" + aqMastModel.getEmpCode());
            pstmt = con.prepareStatement("SELECT RLV_DATE,RLV_TIME FROM EMP_RELIEVE WHERE SPC = ? AND EMP_ID=? AND date_part('year', RLV_DATE) = ? and date_part('month', RLV_DATE)=?");
            pstmt.setString(1, aqMastModel.getCurSpc());
            pstmt.setString(2, aqMastModel.getEmpCode());
            pstmt.setInt(3, year);
            pstmt.setInt(4, month + 1);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                if (rs.getDate("RLV_DATE") != null) {
                    v_ifrelievedthismonth = rs.getDate("RLV_DATE");
                    v_rtime = rs.getString("RLV_TIME");
                    cal.setTime(v_ifrelievedthismonth);
                    v_rday = cal.get(Calendar.DAY_OF_MONTH);
                }
            }
            System.out.println("v_rday:" + v_rday);
            /**
             * ***********************IF JOINED THIS MONTH AND RELIEVED THIS
             * MONTH FROM SAME POST**************
             */
            if (v_ifjoinedthismonth != null && v_ifrelievedthismonth != null) {
                if (v_rday > v_jday) {
                    v_fday = v_jday;
                    if (v_rtime.equalsIgnoreCase("FN")) {
                        totdays = v_rday - v_jday;
                        v_tday = v_rday - 1;
                    } else {
                        totdays = v_rday - v_jday + 1;
                        v_tday = v_rday;
                    }
                } else {
                    v_fday = 1;
                    v_fday1 = v_jday;
                    v_tday1 = v_aqday;
                    if (v_rtime.equalsIgnoreCase("FN")) {
                        totdays = (v_aqday - v_jday) + v_rday;
                        v_tday = v_rday - 1;
                    } else {
                        totdays = (v_aqday - v_jday) + 1 + v_rday;
                        v_tday = v_rday;
                    }
                }
            }
            /**
             * *IF JOINED THIS MONTH AND RELIEVED THIS MONTH FROM SAME POST**
             */
            /**
             * *IF JOINED THIS MONTH AND NOT RELIEVED THIS MONTH FROM SAME
             * POST*
             */
            if (v_ifjoinedthismonth != null && v_ifrelievedthismonth == null) {
                totdays = (v_aqday - v_jday) + 1;
                v_fday = v_jday;
                v_tday = v_aqday;
            }

            /**
             * IF JOINED THIS MONTH AND NOT RELIEVED THIS MONTH FROM SAME POST*
             */
            /**
             * ***********************IF RELIEVED THIS MONTH AND NOT JOINED
             * THIS MONTH IN SAME POST**************
             */
            if (v_ifjoinedthismonth == null && v_ifrelievedthismonth != null) {
                v_fday = 1;
                if (v_rtime.equalsIgnoreCase("FN")) {
                    totdays = v_rday - 1;
                    v_tday = v_rday - 1;
                } else {
                    totdays = v_rday;
                    v_tday = v_rday;
                }
            }
            /**
             * ****IF RELIEVED THIS MONTH AND NOT JOINED THIS MONTH IN SAME
             * POST***
             */
            /**
             * NOT JOINED THIS MONTH AND NOT RELIEVED THIS MONTH
             */
            if (v_ifjoinedthismonth == null && v_ifrelievedthismonth != null) {
                totdays = v_aqday;
                v_fday = 1;
                v_tday = v_aqday;
            }

            /**
             * NOT JOINED THIS MONTH AND NOT RELIEVED THIS MONTH
             */
            /**
             * CALCULATING TOTAL DAYS ABSENT*
             */
            int v_totalAbsent = 0;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String strmonstartDate = simpleDateFormat.format(monstartDate);
            String strmonendDate = simpleDateFormat.format(monendDate);
            pstmt = con.prepareStatement("SELECT ABS_FROM,ABS_TO FROM EMP_ABSENTEE where EMP_ID = ? and (ABS_FROM BETWEEN to_date(?, 'YYYY-MM-DD') AND to_date(?, 'YYYY-MM-DD') or ABS_TO BETWEEN to_date(?, 'YYYY-MM-DD') AND to_date(?, 'YYYY-MM-DD'))");
            pstmt.setString(1, aqMastModel.getEmpCode());
            pstmt.setString(2, strmonstartDate);
            pstmt.setString(3, strmonendDate);
            pstmt.setString(4, strmonstartDate);
            pstmt.setString(5, strmonendDate);
            rs = pstmt.executeQuery();
            Date tmonstartDate = new SimpleDateFormat("yyyy-MM-dd").parse(strmonstartDate);
            Date tmonendDate = new SimpleDateFormat("yyyy-MM-dd").parse(strmonendDate);
            while (rs.next()) {
                Date absfrom = rs.getDate("ABS_FROM");
                Date absto = rs.getDate("ABS_TO");
                if (absfrom.compareTo(tmonstartDate) >= 0 && absto.compareTo(tmonendDate) <= 0) {//Absent from and to with in this month
                    diff = absto.getTime() - absfrom.getTime();
                    days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                    v_totalAbsent = v_totalAbsent + (int) days;
                    v_totalAbsent++;
                } else if (absfrom.compareTo(tmonstartDate) < 0 && absto.compareTo(tmonendDate) <= 0) {//absent from beftore this month but absent to in this month
                    diff = absto.getTime() - monstartDate.getTime();
                    days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                    v_totalAbsent = v_totalAbsent + (int) days;
                } else if (absfrom.compareTo(tmonstartDate) >= 0 && absto.compareTo(tmonendDate) > 0) {//absent from in this month but absent to after this month
                    diff = monendDate.getTime() - absfrom.getTime();
                    days = TimeUnit.DAYS.convert(diff, TimeUnit.SECONDS);
                    v_totalAbsent = v_totalAbsent + (int) days;
                } else if (absfrom.compareTo(tmonstartDate) < 0 && absto.compareTo(tmonendDate) > 0) {//absent from beftore this month but absent to after this month
                    diff = monstartDate.getTime() - monendDate.getTime();
                    days = TimeUnit.DAYS.convert(diff, TimeUnit.SECONDS);
                    v_totalAbsent = v_totalAbsent + (int) days;
                }
            }
            /**
             * CALCULATING TOTAL DAYS ABSENT*
             */
            /**
             * CALCULATING TOTAL DAYS SANCTIONED*
             */

            int v_sanctionavailed = 0;
            /*
             pstmt = con.prepareStatement("SELECT FDATE,TDATE FROM(SELECT FDATE,TDATE FROM EMP_LEAVE WHERE EMP_ID = ? and IF_LONGTERM != 'Y' and AND LSOT_ID='01' and (ABS_FROM BETWEEN ? AND ? or ABS_TO BETWEEN ? AND ?))EMP_LEAVE INNER JOIN "
             + "(SELECT * FROM EMP_NOTIFICATION WHERE EMP_ID = ? AND NOT_TYPE='LEAVE' AND EMP_NOTIFICATION.LINK_ID IS NULL AND STATUS='SANCTIONED AND AVAILED') EMP_NOTIFICATION ON EMP_LEAVE.NOT_ID=EMP_NOTIFICATION.NOT_ID)");
             pstmt.setString(1, sdswe.getEmpid());
             pstmt.setDate(2, new java.sql.Date(monstartDate.getTime()));
             pstmt.setDate(3, new java.sql.Date(monendDate.getTime()));
             pstmt.setDate(4, new java.sql.Date(monstartDate.getTime()));
             pstmt.setDate(5, new java.sql.Date(monendDate.getTime()));
             pstmt.setString(6, sdswe.getEmpid());
             rs = pstmt.executeQuery();
             while (rs.next()) {
             Date sancfrom = rs.getDate("FDATE");
             Date sancto = rs.getDate("TDATE");
             if (sancfrom.compareTo(monstartDate) >= 0 && sancto.compareTo(monendDate) <= 0) {//Absent from and to with in this month
             long diff = sancto.getTime() - sancfrom.getTime();
             long days = TimeUnit.DAYS.convert(diff, TimeUnit.SECONDS);
             v_sanctionavailed = v_sanctionavailed + (int) days;
             } else if (sancfrom.compareTo(monstartDate) < 0 && sancto.compareTo(monendDate) <= 0) {//absent from beftore this month but absent to in this month
             long diff = sancto.getTime() - monstartDate.getTime();
             long days = TimeUnit.DAYS.convert(diff, TimeUnit.SECONDS);
             v_sanctionavailed = v_sanctionavailed + (int) days;
             } else if (sancfrom.compareTo(monstartDate) >= 0 && sancto.compareTo(monendDate) > 0) {//absent from in this month but absent to after this month
             long diff = monendDate.getTime() - sancfrom.getTime();
             long days = TimeUnit.DAYS.convert(diff, TimeUnit.SECONDS);
             v_sanctionavailed = v_sanctionavailed + (int) days;
             } else if (sancfrom.compareTo(monstartDate) < 0 && sancto.compareTo(monendDate) > 0) {//absent from beftore this month but absent to after this month
             long diff = monstartDate.getTime() - monendDate.getTime();
             long days = TimeUnit.DAYS.convert(diff, TimeUnit.SECONDS);
             v_sanctionavailed = v_sanctionavailed + (int) days;
             }
             }*/
            /**
             * CALCULATING TOTAL DAYS SANCTIONED*
             */
            /* CALCULATING JT TIME PERIOD */
            int v_jtavailed = 0;
            pstmt = con.prepareStatement("SELECT JOIN_TIME_FROM,JOIN_TIME_TO FROM EMP_LEAVE WHERE EMP_ID = ? AND (JOIN_TIME_FROM <= ? AND JOIN_TIME_TO >= ?)");
            pstmt.setString(1, aqMastModel.getEmpCode());
            pstmt.setDate(2, new java.sql.Date(monstartDate.getTime()));
            pstmt.setDate(3, new java.sql.Date(monendDate.getTime()));
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Date joiningfrom = rs.getDate("FDATE");
                Date joiningto = rs.getDate("TDATE");
                if (joiningfrom.compareTo(monstartDate) >= 0 && joiningto.compareTo(monendDate) <= 0) {//Absent from and to with in this month
                    diff = joiningto.getTime() - joiningfrom.getTime();
                    days = TimeUnit.DAYS.convert(diff, TimeUnit.SECONDS);
                    v_jtavailed = v_jtavailed + (int) days;
                } else if (joiningfrom.compareTo(monstartDate) < 0 && joiningto.compareTo(monendDate) <= 0) {//absent from beftore this month but absent to in this month
                    diff = joiningto.getTime() - monstartDate.getTime();
                    days = TimeUnit.DAYS.convert(diff, TimeUnit.SECONDS);
                    v_jtavailed = v_jtavailed + (int) days;
                } else if (joiningfrom.compareTo(monstartDate) >= 0 && joiningto.compareTo(monendDate) > 0) {//absent from in this month but absent to after this month
                    diff = monendDate.getTime() - joiningfrom.getTime();
                    days = TimeUnit.DAYS.convert(diff, TimeUnit.SECONDS);
                    v_jtavailed = v_jtavailed + (int) days;
                } else if (joiningfrom.compareTo(monstartDate) < 0 && joiningto.compareTo(monendDate) > 0) {//absent from beftore this month but absent to after this month
                    diff = monstartDate.getTime() - monendDate.getTime();
                    days = TimeUnit.DAYS.convert(diff, TimeUnit.SECONDS);
                    v_jtavailed = v_jtavailed + (int) days;
                }
            }
            /* CALCULATING JT TIME PERIOD */
            if (v_rday > 0) {
                v_payday = v_rday - (v_totalAbsent - v_sanctionavailed);
            } else {
                v_payday = totdays - (v_totalAbsent - v_sanctionavailed);
            }
            v_workday = totdays - v_totalAbsent;
            if (v_payday < 0) {
                v_payday = 0;
            }
            if (v_workday < 0) {
                v_workday = 0;
            }
            /*payworkday.put("payday", v_payday);
             payworkday.put("workday", v_workday);
             */
            payworkday.put("payday", v_payday);
            payworkday.put("workday", v_workday);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        if (sdswe.getSpc().equals("OLSAGR00100000105880004")) {
            System.out.println("____________________________________________________________________________");
        }
        return payworkday;
    }

    public PayComponent getEmployeePayComponent(SectionDtlSPCWiseEmp sdswe, Date startDate, Date endDate, int daysInMonth) {
        Connection con = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        PayComponent paycomp = null;
        try {
            paycomp = new PayComponent();
            paycomp.setBasic(sdswe.getCurBasicSalary());
            paycomp.setGp(sdswe.getGp());
            //con = dataSource.getConnection();
            con = this.getDBConnection();
            int v_temp_basic = 0;
            int basicinEmpMast = 0;
            int gpinEmpMast = 0;

            pstmt = con.prepareStatement("SELECT CUR_BASIC_SALARY,GP FROM EMP_MAST WHERE EMP_ID=?");
            pstmt.setString(1, sdswe.getEmpid());
            rs = pstmt.executeQuery();

            if (rs.next()) {
                basicinEmpMast = rs.getInt("CUR_BASIC_SALARY");
                gpinEmpMast = rs.getInt("GP");
            }
            String sDate1 = "01/01/2016";
            Date payRevDate = new SimpleDateFormat("dd/MM/yyyy").parse(sDate1);
            if (sdswe.getDoegov() != null && sdswe.getDoegov().compareTo(payRevDate) > 0) {
                paycomp.setBasic(basicinEmpMast);
                paycomp.setGp(gpinEmpMast);
                paycomp.setIspayrevised("Y");
            } else {
                pstmt = con.prepareStatement("SELECT MAX(REVISED_BASIC) AS v_temp_basic FROM emp_pay_revised_increment_2016 WHERE EMP_ID=?");
                pstmt.setString(1, sdswe.getEmpid());
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    v_temp_basic = rs.getInt("v_temp_basic");
                }
                pstmt = con.prepareStatement("SELECT IS_APPROVED_CHECKING_AUTH,payrev_fitted_amount FROM PAY_REVISION_OPTION WHERE EMP_ID=?");
                pstmt.setString(1, sdswe.getEmpid());
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    String isapproved = rs.getString("IS_APPROVED_CHECKING_AUTH");
                    if (isapproved != null && isapproved.equalsIgnoreCase("Y")) {
                        String revisedBasic = rs.getString("payrev_fitted_amount");
                        if (isStringInt(revisedBasic)) {
                            paycomp.setBasic(Integer.parseInt(revisedBasic));
                            if (Integer.parseInt(revisedBasic) < v_temp_basic) {
                                paycomp.setBasic(v_temp_basic);
                            }
                            if (basicinEmpMast > paycomp.getBasic()) {
                                paycomp.setBasic(basicinEmpMast);
                            }
                            paycomp.setGp(0);
                            paycomp.setIspayrevised("Y");
                        } else {
                            paycomp.setIspayrevised("N");
                        }
                    } else {
                        paycomp.setIspayrevised("N");
                    }
                } else {
                    paycomp.setBasic(basicinEmpMast);
                    paycomp.setGp(gpinEmpMast);
                    paycomp.setIspayrevised("N");
                }
                Date payDate = null;
                int prevBasicSalary = 0;
                int prevGP = 0;

                payDate = sdswe.getPayDate();

                prevBasicSalary = sdswe.getPrevBasicSalary();
                prevGP = sdswe.getPrevGp();
            }
            System.out.println("******" + paycomp.getBasic());
            /*Calculate Is Employee get his increment in this month*/
            /*
             if (payDate != null && payDate.compareTo(startDate) > 0 && payDate.compareTo(endDate) < 0) {
             long diff = payDate.getTime() - startDate.getTime();
             int prevPayDays = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
             if (prevBasicSalary != 0 && paycomp.getBasic() != prevBasicSalary) {
             int tPrevBasic = (prevBasicSalary / daysInMonth) * prevPayDays;
             int tCurBasic = ((paycomp.getBasic() / daysInMonth) * (daysInMonth - prevPayDays)) + tPrevBasic;
             paycomp.setBasic(tCurBasic);
             }
             if (prevGP != 0 && paycomp.getGp() != prevGP) {
             int tPrevGp = (prevGP / daysInMonth) * prevPayDays;
             int tCurGp = ((paycomp.getGp() / daysInMonth) * (daysInMonth - prevPayDays)) + tPrevGp;
             paycomp.setGp(tCurGp);
             }
             }*/
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(rs1, pstmt1);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return paycomp;
    }

    public boolean isStringInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private Connection getDBConnection() {
        Connection con = null;

        try {
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection("jdbc:postgresql://172.16.1.16/hrmis", "hrmis2", "cmgi");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }

    public boolean stopSalaryForPayHeldUp(String empId) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean stopSalary = false;
        try {
            con = dataSource.getConnection();
            ps = con.prepareStatement("select * from emp_pay_heldup where emp_id=? and to_date is null ");
            ps.setString(1, empId);
            rs = ps.executeQuery();
            if (rs.next()) {
                stopSalary = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, ps);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return stopSalary;
    }
}
