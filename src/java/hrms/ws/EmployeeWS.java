/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.ws;

import hrms.dao.employee.EmployeeDAO;
import hrms.dao.leaveapply.LeaveApplyDAO;
import hrms.dao.payroll.payslip.PaySlipDAO;
import hrms.dao.transfer.TransferDAO;
import hrms.model.employee.Education;
import hrms.model.employee.Employee;
import hrms.model.employee.FamilyRelation;
import hrms.model.employee.IdentityInfo;
import hrms.model.employee.Language;
import hrms.model.employee.Pensioner;
import hrms.model.employee.Punishment;
import hrms.model.employee.Reward;
import hrms.model.employee.Training;
import hrms.model.employee.TransferJoining;
import hrms.model.leave.LeaveWsBean;
import hrms.model.login.LoginUserBean;
import hrms.model.payroll.payslip.PaySlipDetailBean;
import java.util.ArrayList;
import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Manas Jena
 */
@WebService
public class EmployeeWS {

    EmployeeDAO employeeDAO;
    LeaveApplyDAO leaveApplyDAO;
    PaySlipDAO paySlipDAO;
    TransferDAO transferDao;

    @WebMethod(exclude = true)
    public void setEmployeeDAO(EmployeeDAO employeeDAO) {
        this.employeeDAO = employeeDAO;
    }

    @WebMethod(exclude = true)
    public void setLeaveApplyDAO(LeaveApplyDAO leaveApplyDAO) {
        this.leaveApplyDAO = leaveApplyDAO;
    }

    @WebMethod(exclude = true)
    public void setPaySlipDAO(PaySlipDAO paySlipDAO) {
        this.paySlipDAO = paySlipDAO;
    }
    @WebMethod(exclude = true)
    public void setTransferDao(TransferDAO transferDao) {
        this.transferDao = transferDao;
    }

    @WebMethod(operationName = "getEmployeeProfile")
    public Employee getEmployeeProfile(@WebParam(name = "empid") String empid) {
        return employeeDAO.getEmployeeProfile(empid);
    }

    @WebMethod(operationName = "getEmployee")
    public Employee getEmployee(@WebParam(name = "spc") String spc) {
        return employeeDAO.getEmployee(spc);
    }

    @WebMethod(operationName = "getEmployeeEducation")
    public Education[] getEmployeeEducation(@WebParam(name = "empid") String empid, @WebParam(name = "inputdate") String inputdate) {
        return employeeDAO.getEmployeeEducation(empid, inputdate);
    }

    @WebMethod(operationName = "getEmployeeFamily")
    public FamilyRelation[] getEmployeeFamily(@WebParam(name = "empid") String empid) {
        return employeeDAO.getEmployeeFamily(empid);
    }

    @WebMethod(operationName = "getEmployeePunishment")
    public Punishment[] getEmployeePunishment(@WebParam(name = "empid") String empid, @WebParam(name = "inputdate") String inputdate) {
        return employeeDAO.getEmployeePunishment(empid, inputdate);
    }

    @WebMethod(operationName = "getEmployeeReward")
    public Reward[] getEmployeeReward(@WebParam(name = "empid") String empid, @WebParam(name = "inputdate") String inputdate) {
        return employeeDAO.getEmployeeReward(empid, inputdate);
    }

    @WebMethod(operationName = "getEmployeeTraining")
    public Training[] getEmployeeTraining(@WebParam(name = "empid") String empid, @WebParam(name = "inputdate") String inputdate) {
        return employeeDAO.getEmployeeTraining(empid, inputdate);
    }

    @WebMethod(operationName = "getEmployeeTransferAndJoining")
    public TransferJoining[] getEmployeeTransferAndJoining(@WebParam(name = "empid") String empid, @WebParam(name = "inputdate") String input) {
        return employeeDAO.getEmployeeTransferAndJoining(empid, input);
    }

    @WebMethod(operationName = "getLanguageKnown")
    public Language[] getLanguageKnown(@WebParam(name = "empid") String empid) {
        return employeeDAO.getLanguageKnown(empid);
    }

    @WebMethod(operationName = "getUpdatedEmpList")
    public String[] getUpdatedEmpList(@WebParam(name = "input") String input, @WebParam(name = "off_code") String off_code) {
        return employeeDAO.getUpdatedEmpList(input, off_code);
    }

    @WebMethod(operationName = "getEmployeeLeave")
    public LeaveWsBean[] getEmployeeLeave(@WebParam(name = "empid") String empid, @WebParam(name = "inputdate") String input) {
        return leaveApplyDAO.getEmployeeLeave(empid, input);
    }

    @WebMethod(operationName = "getPensionerDetailsThroughAccNo")
    public Pensioner getPensionerDetailsThroughAccNo(@WebParam(name = "gpfno") String gpfno) {
        return employeeDAO.getPensionerDetailsThroughAccNo(gpfno);

    }

    @WebMethod(operationName = "getEmployeeIdInformation")
    public IdentityInfo[] getEmployeeIdInformation(@WebParam(name = "empid") String empid) {
        return employeeDAO.getEmployeeIdInformation(empid);
    }

    @WebMethod(operationName = "getPensionerDetailsThroughHRMSID")
    public Pensioner getPensionerDetailsThroughHRMSID(@WebParam(name = "empid") String empid) {
        return employeeDAO.getPensionerDetailsThroughHRMSID(empid);
    }

    @WebMethod(operationName = "getEmployeePayslip")
    public PaySlipDetailBean getEmployeePayslip(@WebParam(name = "empid")String empid, @WebParam(name = "year")int year, @WebParam(name = "month")int month){
        String aqslno = paySlipDAO.getAQSLNo(empid, year, month);
        System.out.println("aqslno:"+aqslno);
        PaySlipDetailBean paySlipDetailBean = paySlipDAO.getEmployeeData(aqslno,year, month);
        paySlipDetailBean.setAllowList(paySlipDAO.getAllowanceDeductionList(aqslno,"A",year, month));
        paySlipDetailBean.setDeductList(paySlipDAO.getAllowanceDeductionList(aqslno,"D",year, month));
        return paySlipDetailBean;
    }

    @WebMethod(operationName = "getTransferListOfficeWise")
    public LoginUserBean[] getTransferListOfficeWise(@WebParam(name = "offcode") String offcode, @WebParam(name = "year") int year, @WebParam(name = "month") int month) {
        return transferDao.getTransferListOfficeWise(offcode,year,month);
    }
    @WebMethod(operationName = "getOfficeWiseEmployeeList")
    public Employee[] getOfficeWiseEmployeeList(@WebParam(name = "offCode")String offCode, @WebParam(name = "inputdate")String inputdate){
        List<Employee> employeelist = employeeDAO.getOfficeWiseEmployeeList(offCode);
        Employee employees[] = employeelist.toArray(new Employee[employeelist.size()]);        
        return employees;
    }
    
}
