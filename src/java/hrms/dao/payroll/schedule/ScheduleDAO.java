/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.payroll.schedule;

import com.itextpdf.text.Document;
import hrms.SelectOption;
import hrms.model.common.CommonReportParamBean;
import hrms.model.payroll.QuaterRent;
import hrms.model.payroll.billbrowser.BillChartOfAccount;
import hrms.model.payroll.schedule.AbsenteeStatementScheduleBean;
import hrms.model.payroll.schedule.AuditRecoveryBean;
import hrms.model.payroll.schedule.BankAcountScheduleBean;
import hrms.model.payroll.schedule.BillBackPageBean;
import hrms.model.payroll.schedule.BillContributionRepotBean;
import hrms.model.payroll.schedule.ComputerTokenReportBean;
import hrms.model.payroll.schedule.ExcessPayBean;
import hrms.model.payroll.schedule.GPFScheduleBean;
import hrms.model.payroll.schedule.GisAndFaScheduleBean;
import hrms.model.payroll.schedule.ItScheduleBean;
import hrms.model.payroll.schedule.LicScheduleBean;
import hrms.model.payroll.schedule.LoanAdvanceScheduleBean;
import hrms.model.payroll.schedule.OTC84Bean;
import hrms.model.payroll.schedule.OtcForm82Bean;
import hrms.model.payroll.schedule.OtcFormBean;
import hrms.model.payroll.schedule.OtcPlanForm40Bean;
import hrms.model.payroll.schedule.PLIScheduleBean;
import hrms.model.payroll.schedule.PrivateLoanScheduleBean;
import hrms.model.payroll.schedule.PtScheduleBean;
import hrms.model.payroll.schedule.SecondScheduleBean;
import hrms.model.payroll.schedule.VacancyStatementScheduleBean;
import hrms.model.payroll.schedule.VehicleScheduleBean;
import hrms.model.payroll.schedule.VoucherSlipBean;
import hrms.model.payroll.schedule.WrrScheduleBean;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface ScheduleDAO {

    public List getDisplayReportList(String billNo, String billType);

    //Codeing for All / Common Properties Schedule   
    public CommonReportParamBean getCommonReportParameter(String billNo);

    //Codeing for GPF Schedule
    public GPFScheduleBean getGPFScheduleHeaderDetails(String billno);

    public List getGPFScheduleTypeList(String billno, int aqmonth, int aqyear);

    public List getGPFScheduleAbstractList(String billno, int aqmonth, int aqyear);

    //Codeing for WRR Schedule
    public List getWRRScheduleEmployeeList(String billno, String schedule, int aqmonth, int aqyear, String docType);

    public WrrScheduleBean getWRRScheduleHeaderDetails(String billno, String schedule);

    //Codeing for HIRE CHARGE VEHICLE / IT / AIS GROUP INSURANCE SCHEME(Sl-21) Schedule   
    public List getITScheduleEmployeeList(String billno, String schedule, int aqMonth, int aqYear);

    public ItScheduleBean getITScheduleHeaderDetails(String billno, String schedule);

    //Codeing for PT Schedule     
    public List getPTScheduleEmployeeList(String billno, int aqMonth, int aqYear);

    public PtScheduleBean getPTScheduleHeaderDetails(String billno);

    //Sl No-12-15 *#*Codeing for HBA And MOTOR CAR ADVANCE And RECOVERY FOR(HOUSE BUILDING ADVANCE FOR CYCLONE/FLOOD OF OCT 1999 
    //And MOTOR CYCLE ADVANCE And MOPED ADVANCE) Schedule     
    public List getLoanAdvanceSchedulePrincipalList(String schedule, String billno, int aqMonth, int aqYear);

    public List getLoanAdvanceScheduleInterestList(String schedule, String billno, int aqMonth, int aqYear);

    public LoanAdvanceScheduleBean getLoanAdvanceScheduleHeaderDetails(String billno, String schedule);

    //Codeing for FASTIVAL & GIS ADVANCE Schedule      
    public GisAndFaScheduleBean getGisAndFaScheduleHeaderDetails(String schedule, String billno);

    public List getGISandFAScheduleEmpList(String schedule, String billno, int aqYear, int aqMonth);

    /* TO BE DEPLOY ON 17-NOV FROM HERE */
    //Codeing for LIC PREMIUM Schedule      
    public LicScheduleBean getLICScheduleHeaderDetails(String billno);

    public List getLICScheduleEmpList(String billno, int aqMonth, int aqYear);

    //Codeing for PLI PREMIUM Schedule      
    public PLIScheduleBean getPLIScheduleHeaderDetails(String billno);

    public List getPLIScheduleEmpList(String billno);
    /* TO BE DEPLOY ON 17-NOV TO HERE */

    //Codeing for VACANCY STATEMENT  Schedule         
    public AbsenteeStatementScheduleBean getAbsntStmtScheduleHeaderDetails(String billno);

    public List getAbsntStmtScheduleEmpList(String billno);

    //Codeing for BANK SCHEDULE  Schedule         
    public BankAcountScheduleBean getBankAcountScheduleHeaderDetails(String billno);

    public List getBankAcountScheduleEmpList(String billno, int year, int month);

    public List getBankNameScheduleList(String billno, int year, int month);

    //Codeing for BILL BACK PAGE  Schedule       
    public BillBackPageBean getBillBackPgScheduleHeaderDetails(String billno, int aqYear, int aqMonth);

    public List getBillBackPgScheduleEmpList(String billno, int aqYear, int aqMonth);

    //Codeing for ANNEXURE-I to IV NPS Schedule       
    public BillContributionRepotBean getBillContributionRepotScheduleHeaderDetails(String annexure, String billno);

    public List getBillContributionRepotScheduleEmpList(String annexure, String billno, int year, int month);

// Codeing for TPF Schedule
    public List getEmployeeWiseTPFList(String billno);

    public List getTPFAbstractList(String billno);

    //Codeing for Periodic Absentee Statement Schedule       
    public List getPeriodicAbsenteeStatementScheduleEmpList(String billno);

    //Codeing for Periodic Absentee Statement Schedule       
    public VoucherSlipBean getVoucherSlipScheduleDetails(String billno, int aqYear, int aqMonth);

    //Codeing for OTC52 Schedule       
    public OtcForm82Bean getOTCForm82ScheduleDetails(String billno);

    //Codeing for OTC52 Schedule       
    public OtcFormBean getOTCForm52ScheduleDetails(String billno);

    //Codeing for OTC40 Schedule       
    public OtcPlanForm40Bean getOTCForm40ScheduleDetails(String billno, int year, int month);

    public List getOTCForm40ScheduleEmpList(String billno, int year, int month);

    //Codeing for Computer Token Report Schedule       
    public ComputerTokenReportBean getCompTokenRepotScheduleDetails(String billno, int year, int month);

    //Codeing for Excess Pay Schedule Schedule       
    public ExcessPayBean getExcessPayScheduleHeaderDetails(String billno);

    public List getExcessPayScheduleEmpDetails(String billno, int year, int month);

    //Codeing for OTC 84 Schedule       
    public OTC84Bean getOTC84ScheduleDetails(String billno, int year, int month);

    //Codeing for AUDIT RECOVERY Schedule       
    public AuditRecoveryBean getAuditRecoveryScheduleDetails(String billno);

    public List getAuditRecoveryScheduleEmpDetails(String billno, int year, int month);

    // Codeing for PRIVATE LOAN / DEDUCTION SCHEDULE   
    public PrivateLoanScheduleBean getPrivateLoanScheduleDetails(String billno);

    public List getPrivateLoanScheduleEmpDetails(String billno, int aqMonth, int aqYear);

    public QuaterRent[] getRentData(int month, int year);

    public VacancyStatementScheduleBean getVacancyStmtScheduleHeaderDetails(String billno);

    public List getVacancyStmtScheduleEmpList(String billno);

    public void thirdSchedulePDF(Document document, String empid);

    public SecondScheduleBean getSecondScheduleData(String empid);

    public void secondSchedulePDF(Document document, String empid);

    public void secondScheduleIASPDF(Document document, String empid);

    public String isDuplicatePayRevisionOption(String empid);

    public void insertPayRevisionData(String empid, String offcode, String selectedOption, String postcode, String payscale, int gp, String txtDate, String hasUserChanged, String hasDDOChanged);

    public void billFrontPagePDF(Document document, String billNo, BillChartOfAccount billChartOfAccount, List allowanceList, ArrayList deductionList);

    public void billBackPagePDF(Document document, String billNo, BillBackPageBean backPageBean, List empList);
    
    public void WRRSchedulePagePDF(Document document, String schedule, String billNo, WrrScheduleBean wrrBean, List empList);
    
    public void ITSchedulePagePDF(Document document, String schedule, String billNo, ItScheduleBean itBean, List empList);
    
    public void PTSchedulePagePDF(Document document, String billNo, PtScheduleBean ptBean, List empList, int aqMonth, int aqYear);
            
    public int getBasicAmount(String billNo);

    public int getAllowanceAndDeductionAmount(String billNo, String adType, int month, int year);
    
    public List getHCScheduleEmpList(String billNo,int year,int month);
    
    public List getLTCScheduleEmpList(String billNo,int year,int month);
    
    public String getChartOfAccount(String billNo);
    
    public VehicleScheduleBean getVehicleScheduleList(String billNo,int year,int month);
    
    public void LoanSchedulePagePDF(Document document, String schedule, String billNo, LoanAdvanceScheduleBean laBean, List priList, List intList);
    
    public SelectOption getBTPIHeaderClassification(String loantp);
    
    public void generateGISSchedulePDFReportsForAG(Document document, String schedule, ArrayList billList);
    
    public void generateSchedulePDFReportsForAG(Document document,String schedule,ArrayList billList);
    
    public void generateGPFSchedulePDFReportsForAG(Document document, String schedule, ArrayList billList);
}
