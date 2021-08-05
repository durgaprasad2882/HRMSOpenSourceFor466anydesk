package hrms.model.leave;

import java.util.ArrayList;

public class EmpLeaveAccountPropeties {

    private String empCadre = null;//stores the cadre
    private String empAllotmentYear = null;//stores the year of allotment
    private String duration = null; // stores the period(From-To) in which the employee wants to produce the Leave Account.
    private String leaveType = null; // stores type of Leave for which the Account is to be Shown.
    private String leaveTypeId = null;// Stores the TOL_ID of a particular leave
    private String leaveOBalDate = null;//stores the date of opening balance of particular leave of an employee.
    private String leaveOBal = null; // stores the opening balance of a particular type of leave of a particular employee.
    private ArrayList creditLvList = null;//stores the list of Leave Credited to a particular employee.
    private ArrayList servHisList = null;//stores the Service History data to a particular employee.
    private ArrayList empServiceRecordList = null;
    private ArrayList empSRPayList = null;
    private String post = null;
    private String empId = null; // stores Employee Id of the employee.
    private String empGpf = null;

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getEmpGpf() {
        return empGpf;
    }

    public void setEmpGpf(String empGpf) {
        this.empGpf = empGpf;
    }

    public String getEmpCadre() {
        return empCadre;
    }

    public void setEmpCadre(String empCadre) {
        this.empCadre = empCadre;
    }

    public String getEmpAllotmentYear() {
        return empAllotmentYear;
    }

    public void setEmpAllotmentYear(String empAllotmentYear) {
        this.empAllotmentYear = empAllotmentYear;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public String getLeaveTypeId() {
        return leaveTypeId;
    }

    public void setLeaveTypeId(String leaveTypeId) {
        this.leaveTypeId = leaveTypeId;
    }

    public String getLeaveOBalDate() {
        return leaveOBalDate;
    }

    public void setLeaveOBalDate(String leaveOBalDate) {
        this.leaveOBalDate = leaveOBalDate;
    }

    public String getLeaveOBal() {
        return leaveOBal;
    }

    public void setLeaveOBal(String leaveOBal) {
        this.leaveOBal = leaveOBal;
    }

    public ArrayList getCreditLvList() {
        return creditLvList;
    }

    public void setCreditLvList(ArrayList creditLvList) {
        this.creditLvList = creditLvList;
    }

    public ArrayList getServHisList() {
        return servHisList;
    }

    public void setServHisList(ArrayList servHisList) {
        this.servHisList = servHisList;
    }

    public ArrayList getEmpServiceRecordList() {
        return empServiceRecordList;
    }

    public void setEmpServiceRecordList(ArrayList empServiceRecordList) {
        this.empServiceRecordList = empServiceRecordList;
    }

    public ArrayList getEmpSRPayList() {
        return empSRPayList;
    }

    public void setEmpSRPayList(ArrayList empSRPayList) {
        this.empSRPayList = empSRPayList;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

}
