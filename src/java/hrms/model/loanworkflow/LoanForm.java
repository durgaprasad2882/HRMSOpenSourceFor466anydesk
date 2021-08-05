/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.model.loanworkflow;

import java.util.ArrayList;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author lenovo
 */
public class LoanForm {

    private String designation = null;
    private String basicsalary = null;
    private String netsalary = null;
    private String hidOffCode = null;
    private String hidOffName = null;
    private String hidSPC = null;
    private String antprice = null;
    private String purtype = null;
    private String amountadv = null;
    private int instalments = 0;
    private String previousAvail = null;
    private String preAdvPur = null;
    private String amounpretadv = null;
    private String dateofdrawal = null;
    private String intpaidfull = null;
    private String amountstanding = null;
    private String officerleave = null;
    private String datecommleave = null;
    private String dateexpireleave = null;
    private MultipartFile file_att = null;
    private String forwardto = null;
    private String EmpName = null;
    private String offaddress = null;
    private String name=null;
    private String JobType = null;
    private String Empdob = null;
    private String loancomments=null;
    private String Superannuation = null;
    private int loan_status = 0;
    
    private String empSPC = null;
    private String forwardtoHrmsid = null;
    private String loanstatus=null;
    private int taskid=0;
    private int loanId=0;
    private String approvedBy=null;
    private String approvedSpc=null;
    private String gpfno=null;
    private String gp=null;
    private String diskFileName=null;
    private String loanapplyfor=null;
    private String letterNo=null;
    private String letterDate=null;
    private String memoNo=null;
    private String memoDate=null;
    private String letterformName=null;
    private String letterformdesignation=null;
    private String letterto=null; 
    private String fileView = null;
    private String empID=null;
    private int statusId=0;
    private String notes=null;

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
    

    public int getLoanId() {
        return loanId;
    }

    public void setLoanId(int loanId) {
        this.loanId = loanId;
    }

    public String getLoancomments() {
        return loancomments;
    }

    public void setLoancomments(String loancomments) {
        this.loancomments = loancomments;
    }
  
    

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getBasicsalary() {
        return basicsalary;
    }

    public void setBasicsalary(String basicsalary) {
        this.basicsalary = basicsalary;
    }

    public String getNetsalary() {
        return netsalary;
    }

    public void setNetsalary(String netsalary) {
        this.netsalary = netsalary;
    }

    public String getHidOffCode() {
        return hidOffCode;
    }

    public void setHidOffCode(String hidOffCode) {
        this.hidOffCode = hidOffCode;
    }

    public String getHidOffName() {
        return hidOffName;
    }

    public void setHidOffName(String hidOffName) {
        this.hidOffName = hidOffName;
    }

    public String getHidSPC() {
        return hidSPC;
    }

    public void setHidSPC(String hidSPC) {
        this.hidSPC = hidSPC;
    }

    public String getAntprice() {
        return antprice;
    }

    public void setAntprice(String antprice) {
        this.antprice = antprice;
    }

    public String getPurtype() {
        return purtype;
    }

    public void setPurtype(String purtype) {
        this.purtype = purtype;
    }

    public String getAmountadv() {
        return amountadv;
    }

    public void setAmountadv(String amountadv) {
        this.amountadv = amountadv;
    }

    public int getInstalments() {
        return instalments;
    }

    public void setInstalments(int instalments) {
        this.instalments = instalments;
    }

    public String getPreviousAvail() {
        return previousAvail;
    }

    public void setPreviousAvail(String previousAvail) {
        this.previousAvail = previousAvail;
    }

    public String getPreAdvPur() {
        return preAdvPur;
    }

    public void setPreAdvPur(String preAdvPur) {
        this.preAdvPur = preAdvPur;
    }

    public String getAmounpretadv() {
        return amounpretadv;
    }

    public void setAmounpretadv(String amounpretadv) {
        this.amounpretadv = amounpretadv;
    }

    public String getDateofdrawal() {
        return dateofdrawal;
    }

    public void setDateofdrawal(String dateofdrawal) {
        this.dateofdrawal = dateofdrawal;
    }

    public String getIntpaidfull() {
        return intpaidfull;
    }

    public void setIntpaidfull(String intpaidfull) {
        this.intpaidfull = intpaidfull;
    }

    public String getAmountstanding() {
        return amountstanding;
    }

    public void setAmountstanding(String amountstanding) {
        this.amountstanding = amountstanding;
    }

    public String getOfficerleave() {
        return officerleave;
    }

    public void setOfficerleave(String officerleave) {
        this.officerleave = officerleave;
    }

    public String getDatecommleave() {
        return datecommleave;
    }

    public void setDatecommleave(String datecommleave) {
        this.datecommleave = datecommleave;
    }

    public String getDateexpireleave() {
        return dateexpireleave;
    }

    public void setDateexpireleave(String dateexpireleave) {
        this.dateexpireleave = dateexpireleave;
    }

    public String getForwardto() {
        return forwardto;
    }

    public void setForwardto(String forwardto) {
        this.forwardto = forwardto;
    }

    public String getEmpName() {
        return EmpName;
    }

    public void setEmpName(String EmpName) {
        this.EmpName = EmpName;
    }

    

    public String getJobType() {
        return JobType;
    }

    public void setJobType(String JobType) {
        this.JobType = JobType;
    }

    public String getEmpdob() {
        return Empdob;
    }

    public void setEmpdob(String Empdob) {
        this.Empdob = Empdob;
    }

    public String getSuperannuation() {
        return Superannuation;
    }

    public void setSuperannuation(String Superannuation) {
        this.Superannuation = Superannuation;
    }

    public int getLoan_status() {
        return loan_status;
    }

    public void setLoan_status(int loan_status) {
        this.loan_status = loan_status;
    }

    public String getEmpSPC() {
        return empSPC;
    }

    public void setEmpSPC(String empSPC) {
        this.empSPC = empSPC;
    }

    public String getForwardtoHrmsid() {
        return forwardtoHrmsid;
    }

    public void setForwardtoHrmsid(String forwardtoHrmsid) {
        this.forwardtoHrmsid = forwardtoHrmsid;
    }

    public String getLoanstatus() {
        return loanstatus;
    }

    public void setLoanstatus(String loanstatus) {
        this.loanstatus = loanstatus;
    }

    public int getTaskid() {
        return taskid;
    }

    public void setTaskid(int taskid) {
        this.taskid = taskid;
    }

    public MultipartFile getFile_att() {
        return file_att;
    }

    public void setFile_att(MultipartFile file_att) {
        this.file_att = file_att;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public String getApprovedSpc() {
        return approvedSpc;
    }

    public void setApprovedSpc(String approvedSpc) {
        this.approvedSpc = approvedSpc;
    }

    public String getGpfno() {
        return gpfno;
    }

    public void setGpfno(String gpfno) {
        this.gpfno = gpfno;
    }

    public String getGp() {
        return gp;
    }

    public void setGp(String gp) {
        this.gp = gp;
    }

    public String getFileView() {
        return fileView;
    }

    public void setFileView(String fileView) {
        this.fileView = fileView;
    }

    public String getDiskFileName() {
        return diskFileName;
    }

    public void setDiskFileName(String diskFileName) {
        this.diskFileName = diskFileName;
    }

    public String getLoanapplyfor() {
        return loanapplyfor;
    }

    public void setLoanapplyfor(String loanapplyfor) {
        this.loanapplyfor = loanapplyfor;
    }

    public String getOffaddress() {
        return offaddress;
    }

    public void setOffaddress(String offaddress) {
        this.offaddress = offaddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLetterNo() {
        return letterNo;
    }

    public void setLetterNo(String letterNo) {
        this.letterNo = letterNo;
    }

    public String getLetterDate() {
        return letterDate;
    }

    public void setLetterDate(String letterDate) {
        this.letterDate = letterDate;
    }

    public String getMemoNo() {
        return memoNo;
    }

    public void setMemoNo(String memoNo) {
        this.memoNo = memoNo;
    }

    public String getMemoDate() {
        return memoDate;
    }

    public void setMemoDate(String memoDate) {
        this.memoDate = memoDate;
    }

    public String getLetterformName() {
        return letterformName;
    }

    public void setLetterformName(String letterformName) {
        this.letterformName = letterformName;
    }

    public String getLetterformdesignation() {
        return letterformdesignation;
    }

    public void setLetterformdesignation(String letterformdesignation) {
        this.letterformdesignation = letterformdesignation;
    }

    public String getLetterto() {
        return letterto;
    }

    public void setLetterto(String letterto) {
        this.letterto = letterto;
    }

    public String getEmpID() {
        return empID;
    }

    public void setEmpID(String empID) {
        this.empID = empID;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    

   
}
