/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.payroll.allowancededcution;

import hrms.common.Message;
import hrms.model.employee.PayComponent;
import hrms.model.payroll.allowancededcution.AllowanceDeduction;
import hrms.model.payroll.aqdtls.AqDtlsModel;
import hrms.model.payroll.aqmast.AqmastModel;
import hrms.model.payroll.billbrowser.SectionDtlSPCWiseEmp;
import hrms.model.payroll.billmast.BillMastModel;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 *
 * @author Manas Jena
 */
public interface AllowanceDeductionDAO {

    public ArrayList getEmployeeWiseAllowance(String empId);

    public ArrayList getEmployeeWiseDeduction(String empId);

    public ArrayList getEmployeeWisePvtDeduction(String empId);

    public ArrayList getOfficeWiseAllowance(String empId);

    public ArrayList getOfficeWiseDeduction(String empId);

    public ArrayList getOfficeWisePvtDeduction(String empId);

    public AllowanceDeduction getAllowanceDeductionDetail(String adcode);

    public AllowanceDeduction getUpdatedAllowanceDeduction(AllowanceDeduction adBean);

    public AllowanceDeduction getUpdatedAllowanceDeductionDetail(String adcode);

    public Message saveAllowanceDeductionDetail(AllowanceDeduction adbean);

    public AqDtlsModel[] getDA(String empCode, BigDecimal billGroupId, PayComponent payComponent, long gross, AqmastModel aqmast);

    public ArrayList getAllowanceList(SectionDtlSPCWiseEmp sdswe, String offCode, int aqday, int payday, int workday, int traing_days, PayComponent payComponent);

    public ArrayList getAllowanceList(String curspc, String empCode, String configuredlvl, BigDecimal billGroupId, String offCode, int aqday, int payday, int workday, int traing_days, PayComponent payComponent, String bill_type, String v_depcode);

    public ArrayList getAllowanceList(SectionDtlSPCWiseEmp sdswe, String configuredlvl, BigDecimal billGroupId, String offCode, int aqday, int payday, int workday, int traing_days, PayComponent payComponent, String bill_type, String v_depcode, int aqmonth, int aqyear);

    public ArrayList getDeductionList(SectionDtlSPCWiseEmp sdswe, String configuredlvl, BigDecimal billGroupId, String offCode, int aqday, int payday, int workday, int traing_days, PayComponent payComponent, String bill_type, String v_depcode, String gpfSeries);

    public ArrayList getDeductionList(SectionDtlSPCWiseEmp sdswe, String configuredlvl, AqmastModel aqMastModel, BillMastModel billMastModel, int aqday, int payday, int workday, int traing_days, PayComponent payComponent, String gpfSeries);

    public ArrayList getAllowanceList(String configuredlvl, BigDecimal billGroupId, String offcode, String empId, PayComponent payComponent);

    public ArrayList getDeductionList(String empId);
}
