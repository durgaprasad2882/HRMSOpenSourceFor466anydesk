package hrms.model.leave;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Leave implements Serializable {

    private String leaveId = null;
    private String notType = null;
    private String empId = null;
    //@DateTimeFormat(pattern = "yyyy-MMM-dd")
    private String txtperiodFrom = null;
    private String txtperiodTo = null;
    private String txtsuffixFrom = null;
    private String txtsuffixTo = null;
    private String txtprefixFrom = null;
    private String txtprefixTo = null;
    private String txtconaddress = null;
    private String txtphoneNo = null;
    private String ifmedical;
    private String ifcommuted;
    private String hidSpcCode = null;
    private String chkLongTerm = null;
    private String txtnote = null;
    private String tollid;
    private String sltleaveType = null;
    private String[] attachmentid = null;
    private String submittedTo = null;
    private int taskId;
    private String applicantName = null;
    private int empid;
    private String offname = null;
    private String hidAuthEmpId = null;
    private String hidSpcAuthCode = null;
    private String hidempId = null;
    private ArrayList fileArrList = null;
    private String txtApplyFor = null;
    private String passString = null;

    private String hidTaskId = null;
    private String sltActionType = null;
    private String txtauthnote = null;
    private ArrayList leaveFlowList = null;
    private String statusId = null;
    private String txtApproveFrom=null;
    private String TxtApproveTo=null;
    private String txtOrdDate=null;
    private String txtOrdNo=null;
    private String authPost=null;
    private String hidAuthDeptCode=null;
    private String hidAuthOffCode=null;
    private String hqperrad=null;
    private String extendedFromDate=null;
    private String pendingPostWthName=null;
    private String txtSancAuthority=null;
    private String joiningDate=null;
    private String joiningNote=null;
     private ArrayList joinFileArrList = null;
     private String loginUser=null;
     private String sltChild=null;

    public String getSltChild() {
        return sltChild;
    }

    public void setSltChild(String sltChild) {
        this.sltChild = sltChild;
    }

    public String getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(String loginUser) {
        this.loginUser = loginUser;
    }

   

    public ArrayList getJoinFileArrList() {
        return joinFileArrList;
    }

    public void setJoinFileArrList(ArrayList joinFileArrList) {
        this.joinFileArrList = joinFileArrList;
    }

    public String getJoiningNote() {
        return joiningNote;
    }

    public void setJoiningNote(String joiningNote) {
        this.joiningNote = joiningNote;
    }
    

    public String getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(String joiningDate) {
        this.joiningDate = joiningDate;
    }
    
    
    public String getTxtSancAuthority() {
        return txtSancAuthority;
    }

    public void setTxtSancAuthority(String txtSancAuthority) {
        this.txtSancAuthority = txtSancAuthority;
    }

    public String getPendingPostWthName() {
        return pendingPostWthName;
    }

    public void setPendingPostWthName(String pendingPostWthName) {
        this.pendingPostWthName = pendingPostWthName;
    }

    public String getExtendedFromDate() {
        return extendedFromDate;
    }

    public void setExtendedFromDate(String extendedFromDate) {
        this.extendedFromDate = extendedFromDate;
    }

    public String getHqperrad() {
        return hqperrad;
    }

    public void setHqperrad(String hqperrad) {
        this.hqperrad = hqperrad;
    }
    
    public String getHidAuthDeptCode() {
        return hidAuthDeptCode;
    }

    public void setHidAuthDeptCode(String hidAuthDeptCode) {
        this.hidAuthDeptCode = hidAuthDeptCode;
    }

    public String getHidAuthOffCode() {
        return hidAuthOffCode;
    }

    public void setHidAuthOffCode(String hidAuthOffCode) {
        this.hidAuthOffCode = hidAuthOffCode;
    }

    public String getAuthPost() {
        return authPost;
    }

    public void setAuthPost(String authPost) {
        this.authPost = authPost;
    }

    public String getTxtOrdDate() {
        return txtOrdDate;
    }

    public void setTxtOrdDate(String txtOrdDate) {
        this.txtOrdDate = txtOrdDate;
    }

    public String getTxtOrdNo() {
        return txtOrdNo;
    }

    public void setTxtOrdNo(String txtOrdNo) {
        this.txtOrdNo = txtOrdNo;
    }

    public String getTxtApproveFrom() {
        return txtApproveFrom;
    }

    public void setTxtApproveFrom(String txtApproveFrom) {
        this.txtApproveFrom = txtApproveFrom;
    }

    public String getTxtApproveTo() {
        return TxtApproveTo;
    }

    public void setTxtApproveTo(String TxtApproveTo) {
        this.TxtApproveTo = TxtApproveTo;
    }
    

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public ArrayList getLeaveFlowList() {
        return leaveFlowList;
    }

    public void setLeaveFlowList(ArrayList leaveFlowList) {
        this.leaveFlowList = leaveFlowList;
    }

    public String getHidTaskId() {
        return hidTaskId;
    }

    public void setHidTaskId(String hidTaskId) {
        this.hidTaskId = hidTaskId;
    }

    public String getSltActionType() {
        return sltActionType;
    }

    public void setSltActionType(String sltActionType) {
        this.sltActionType = sltActionType;
    }

    public String getTxtauthnote() {
        return txtauthnote;
    }

    public void setTxtauthnote(String txtauthnote) {
        this.txtauthnote = txtauthnote;
    }

    public String getPassString() {
        return passString;
    }

    public void setPassString(String passString) {
        this.passString = passString;
    }

    public String getTxtApplyFor() {
        return txtApplyFor;
    }

    public void setTxtApplyFor(String txtApplyFor) {
        this.txtApplyFor = txtApplyFor;
    }

    public ArrayList getFileArrList() {
        return fileArrList;
    }

    public void setFileArrList(ArrayList fileArrList) {
        this.fileArrList = fileArrList;
    }

    public String getLeaveId() {
        return leaveId;
    }

    public void setLeaveId(String leaveId) {
        this.leaveId = leaveId;
    }

    public String getNotType() {
        return notType;
    }

    public void setNotType(String notType) {
        this.notType = notType;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getTollid() {
        return tollid;
    }

    public void setTollid(String tollid) {
        this.tollid = tollid;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getEmpid() {
        return empid;
    }

    public void setEmpid(int empid) {
        this.empid = empid;
    }

    public String getHidAuthEmpId() {
        return hidAuthEmpId;
    }

    public void setHidAuthEmpId(String hidAuthEmpId) {
        this.hidAuthEmpId = hidAuthEmpId;
    }

    public String getTxtsuffixFrom() {
        return txtsuffixFrom;
    }

    public void setTxtsuffixFrom(String txtsuffixFrom) {
        this.txtsuffixFrom = txtsuffixFrom;
    }

    public String getTxtsuffixTo() {
        return txtsuffixTo;
    }

    public void setTxtsuffixTo(String txtsuffixTo) {
        this.txtsuffixTo = txtsuffixTo;
    }

    public String getTxtprefixFrom() {
        return txtprefixFrom;
    }

    public void setTxtprefixFrom(String txtprefixFrom) {
        this.txtprefixFrom = txtprefixFrom;
    }

    public String getTxtprefixTo() {
        return txtprefixTo;
    }

    public void setTxtprefixTo(String txtprefixTo) {
        this.txtprefixTo = txtprefixTo;
    }

    public String getHidempId() {
        return hidempId;
    }

    public void setHidempId(String hidempId) {
        this.hidempId = hidempId;
    }

    public String getTxtconaddress() {
        return txtconaddress;
    }

    public void setTxtconaddress(String txtconaddress) {
        this.txtconaddress = txtconaddress;
    }

    public String getTxtphoneNo() {
        return txtphoneNo;
    }

    public void setTxtphoneNo(String txtphoneNo) {
        this.txtphoneNo = txtphoneNo;
    }

    public String getIfmedical() {
        return ifmedical;
    }

    public void setIfmedical(String ifmedical) {
        this.ifmedical = ifmedical;
    }

    public String getIfcommuted() {
        return ifcommuted;
    }

    public void setIfcommuted(String ifcommuted) {
        this.ifcommuted = ifcommuted;
    }

    public String getHidSpcCode() {
        return hidSpcCode;
    }

    public void setHidSpcCode(String hidSpcCode) {
        this.hidSpcCode = hidSpcCode;
    }

    public String getChkLongTerm() {
        return chkLongTerm;
    }

    public void setChkLongTerm(String chkLongTerm) {
        this.chkLongTerm = chkLongTerm;
    }

    public String getSltleaveType() {
        return sltleaveType;
    }

    public void setSltleaveType(String sltleaveType) {
        this.sltleaveType = sltleaveType;
    }

    public String[] getAttachmentid() {
        return attachmentid;
    }

    public void setAttachmentid(String[] attachmentid) {
        this.attachmentid = attachmentid;
    }

    public String getTxtperiodFrom() {
        return txtperiodFrom;
    }

    public void setTxtperiodFrom(String txtperiodFrom) {
        this.txtperiodFrom = txtperiodFrom;
    }

    public String getTxtperiodTo() {
        return txtperiodTo;
    }

    public void setTxtperiodTo(String txtperiodTo) {
        this.txtperiodTo = txtperiodTo;
    }

    public String getHidSpcAuthCode() {
        return hidSpcAuthCode;
    }

    public void setHidSpcAuthCode(String hidSpcAuthCode) {
        this.hidSpcAuthCode = hidSpcAuthCode;
    }

    public String getSubmittedTo() {
        return submittedTo;
    }

    public void setSubmittedTo(String submittedTo) {
        this.submittedTo = submittedTo;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getOffname() {
        return offname;
    }

    public void setOffname(String offname) {
        this.offname = offname;
    }

    public String getTxtnote() {
        return txtnote;
    }

    public void setTxtnote(String txtnote) {
        this.txtnote = txtnote;
    }

}
