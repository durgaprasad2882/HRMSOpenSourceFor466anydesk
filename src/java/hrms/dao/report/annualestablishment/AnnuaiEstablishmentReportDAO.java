/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.report.annualestablishment;

import hrms.model.report.annualestablishmentreport.AnnualEstablishment;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Surendra
 */
public interface AnnuaiEstablishmentReportDAO {

    public AnnualEstablishment submittedforCurrentFinancialYear(String offcode, AnnualEstablishment ae);

    public List<AnnualEstablishment> getAnnualEstablistmentReportListFromAuthLogin(String taskId);

    public List<AnnualEstablishment> getAnnualEstablistmentReportList(String offcode);

    public void addAEReportData(AnnualEstablishment ae, String empId, String fy);

    public void updateGspcForAERReport(String offcode);

    public List<AnnualEstablishment> getSubmittedReportList(String offcode, String fy);

    public List getAerReportList(String offcode);

    public Map<String, String> getAuthorityList(String offcode);

    public void addAERMaster(AnnualEstablishment ae, String empId);

    public void approvedAER(int taskId);

    public String getAERStatus(int taskId);

    public void revertAER(int taskId, String serverfilePath, String fileName);

    public List getScheduleIIData(String offCode);

    public List departmentWiseAerStatus(String finYear);

    public List aerSubmittedOfficeList(String deptCode);
     public List getFinancialYearList();

}
