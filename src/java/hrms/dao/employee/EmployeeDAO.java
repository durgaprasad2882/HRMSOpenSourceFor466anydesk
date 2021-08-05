/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.employee;

import hrms.model.empinfo.EmployeeMessage;
import hrms.model.empinfo.SearchEmployee;
import hrms.model.employee.Address;
import hrms.model.employee.Education;
import hrms.model.employee.Employee;
import hrms.model.employee.EmployeePayProfile;
import hrms.model.employee.FamilyRelation;
import hrms.model.employee.IdentityInfo;
import hrms.model.employee.Language;
import hrms.model.employee.Pensioner;
import hrms.model.employee.Punishment;
import hrms.model.employee.Reward;
import hrms.model.employee.Training;
import hrms.model.employee.TransferJoining;
import hrms.model.loan.Loan;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Manas Jena
 */
public interface EmployeeDAO {

    public Employee getEmployee(String spc);

    public Education[] getEmployeeEducation(String empid, String inputdate);

    public FamilyRelation[] getEmployeeFamily(String empid);

    public Employee getEmployeeProfile(String empid);

    public Punishment[] getEmployeePunishment(String empid, String inputdate);

    public Reward[] getEmployeeReward(String empid, String inputdate);

    public Training[] getEmployeeTraining(String empid, String inputdate);

    public TransferJoining[] getEmployeeTransferAndJoining(String empid, String input);

    public Language[] getLanguageKnown(String empid);

    public String[] getUpdatedEmpList(String input, String off_code);

    public Pensioner getPensionerDetailsThroughAccNo(String gpfno);

    public Loan[] getEmployeeLoan(String empid);

    public IdentityInfo[] getEmployeeIdInformation(String empid);

    public Pensioner getPensionerDetailsThroughHRMSID(String empid);

    public ArrayList getOfficeWiseEmployeeList(String offCode);

    public EmployeePayProfile getEmployeePayProfile(String empid);

    //  public int addEmployee(Employee emp);
    public List SearchEmployee(SearchEmployee searchEmployee);

    public Address[] getAddress(String empid);

    public IdentityInfo[] getIdentity(String empid);

    public int saveEmployeeMessage(EmployeeMessage employeemessage);

    public List getSentMessageList();

    public List getEmployeeMessageList(String empid);

    public void uploadAttachedFile(int messageId, MultipartFile file)  throws SQLException;
    
    public List getOffWiseEmpList(String offCode);
    
    public List getDeptWiseEmpList(String deptCode);
    public void saveProfile(Employee emp);
    
   

}
