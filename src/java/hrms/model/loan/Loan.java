package hrms.model.loan;

import java.util.Date;

public class Loan {

    private String empid = null;
    private String loanid = null;
    private Date doe;
    private String hidEmpId = null;
    private String orderno = null;
    private Date orderdate = null;
    private String sltloan = null;
    private double txtamount = 0;
    private String authority = null;
    private String dept = null;
    private String office = null;
    private String spc = null;
    private String note = null;
    private String accountNo = null;
    private String bank = null;
    private String branch = null;
    private String voucherNo = null;
    private Date voucherDate = null;
    private String treasuryname = null;
    private String nowDeduct = null;
    private Date deductionStartDate = null;
    private int originalAmt = 0;
    private int totalNoOfInsl = 0;
    private int instalmentAmount = 0;
    private int lastPaidInstalNo = 0;
    private int monthlyinstlno = 0;
    private int cumulativeAmtPaid = 0;
    private int completedRecovery;
    private String txtpvtloan = null;
    private String btid = null;
    private String notid = null;
    private String hidNotId = null;
    private String notType = null;
    private String hidAuthEmpId = null;
    private String hidSpcAuthCode = null;
    private String hidAuthDeptCode = null;
    private String hidAuthOffCode = null;

    private int repcol = 0;
    private int rowno = 0;
    private String haveInt = null;
    private String nowdedn = null;
    private String loanName = null;

    private String empName = null;
    private String gpfNo = null;
    
    private String refDesc = null;
    private int cummrecovered;
    private int refCount;

    public int getCummrecovered() {
        return cummrecovered;
    }

    public void setCummrecovered(int cummrecovered) {
        this.cummrecovered = cummrecovered;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getGpfNo() {
        return gpfNo;
    }

    public void setGpfNo(String gpfNo) {
        this.gpfNo = gpfNo;
    }

    public int getRepcol() {
        return repcol;
    }

    public void setRepcol(int repcol) {
        this.repcol = repcol;
    }

    public int getRowno() {
        return rowno;
    }

    public void setRowno(int rowno) {
        this.rowno = rowno;
    }

    public String getHaveInt() {
        return haveInt;
    }

    public void setHaveInt(String haveInt) {
        this.haveInt = haveInt;
    }

    public String getNowdedn() {
        return nowdedn;
    }

    public void setNowdedn(String nowdedn) {
        this.nowdedn = nowdedn;
    }

    public String getLoanName() {
        return loanName;
    }

    public void setLoanName(String loanName) {
        this.loanName = loanName;
    }

    public String getHidEmpId() {
        return hidEmpId;
    }

    public void setHidEmpId(String hidEmpId) {
        this.hidEmpId = hidEmpId;
    }

    public String getHidNotId() {
        return hidNotId;
    }

    public void setHidNotId(String hidNotId) {
        this.hidNotId = hidNotId;
    }

    public int getCompletedRecovery() {
        return completedRecovery;
    }

    public void setCompletedRecovery(int completedRecovery) {
        this.completedRecovery = completedRecovery;
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

    public String getHidAuthEmpId() {
        return hidAuthEmpId;
    }

    public void setHidAuthEmpId(String hidAuthEmpId) {
        this.hidAuthEmpId = hidAuthEmpId;
    }

    public String getHidSpcAuthCode() {
        return hidSpcAuthCode;
    }

    public void setHidSpcAuthCode(String hidSpcAuthCode) {
        this.hidSpcAuthCode = hidSpcAuthCode;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public String getNotid() {
        return notid;
    }

    public void setNotid(String notid) {
        this.notid = notid;
    }

    public String getNotType() {
        return notType;
    }

    public void setNotType(String notType) {
        this.notType = notType;
    }

    public String getEmpid() {
        return empid;
    }

    public void setEmpid(String empid) {
        this.empid = empid;
    }

    public String getLoanid() {
        return loanid;
    }

    public void setLoanid(String loanid) {
        this.loanid = loanid;
    }

    public Date getDoe() {
        return doe;
    }

    public void setDoe(Date doe) {
        this.doe = doe;
    }

    public String getOrderno() {
        return orderno;
    }

    public void setOrderno(String orderno) {
        this.orderno = orderno;
    }

    public Date getOrderdate() {
        return orderdate;
    }

    public void setOrderdate(Date orderdate) {
        this.orderdate = orderdate;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public String getSpc() {
        return spc;
    }

    public void setSpc(String spc) {
        this.spc = spc;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getNowDeduct() {
        return nowDeduct;
    }

    public void setNowDeduct(String nowDeduct) {
        this.nowDeduct = nowDeduct;
    }

    public Date getDeductionStartDate() {
        return deductionStartDate;
    }

    public void setDeductionStartDate(Date deductionStartDate) {
        this.deductionStartDate = deductionStartDate;
    }

    public int getOriginalAmt() {
        return originalAmt;
    }

    public void setOriginalAmt(int originalAmt) {
        this.originalAmt = originalAmt;
    }

    public int getTotalNoOfInsl() {
        return totalNoOfInsl;
    }

    public void setTotalNoOfInsl(int totalNoOfInsl) {
        this.totalNoOfInsl = totalNoOfInsl;
    }

    public int getInstalmentAmount() {
        return instalmentAmount;
    }

    public void setInstalmentAmount(int instalmentAmount) {
        this.instalmentAmount = instalmentAmount;
    }

    public int getLastPaidInstalNo() {
        return lastPaidInstalNo;
    }

    public void setLastPaidInstalNo(int lastPaidInstalNo) {
        this.lastPaidInstalNo = lastPaidInstalNo;
    }

    public int getMonthlyinstlno() {
        return monthlyinstlno;
    }

    public void setMonthlyinstlno(int monthlyinstlno) {
        this.monthlyinstlno = monthlyinstlno;
    }

    public int getCumulativeAmtPaid() {
        return cumulativeAmtPaid;
    }

    public void setCumulativeAmtPaid(int cumulativeAmtPaid) {
        this.cumulativeAmtPaid = cumulativeAmtPaid;
    }

    public String getTxtpvtloan() {
        return txtpvtloan;
    }

    public void setTxtpvtloan(String txtpvtloan) {
        this.txtpvtloan = txtpvtloan;
    }

    public String getBtid() {
        return btid;
    }

    public void setBtid(String btid) {
        this.btid = btid;
    }

    public String getSltloan() {
        return sltloan;
    }

    public void setSltloan(String sltloan) {
        this.sltloan = sltloan;
    }

    public double getTxtamount() {
        return txtamount;
    }

    public void setTxtamount(double txtamount) {
        this.txtamount = txtamount;
    }

    public String getVoucherNo() {
        return voucherNo;
    }

    public void setVoucherNo(String voucherNo) {
        this.voucherNo = voucherNo;
    }

    public Date getVoucherDate() {
        return voucherDate;
    }

    public void setVoucherDate(Date voucherDate) {
        this.voucherDate = voucherDate;
    }

    public String getTreasuryname() {
        return treasuryname;
    }

    public void setTreasuryname(String treasuryname) {
        this.treasuryname = treasuryname;
    }

    public String getRefDesc() {
        return refDesc;
    }

    public void setRefDesc(String refDesc) {
        this.refDesc = refDesc;
    }

    public int getRefCount() {
        return refCount;
    }

    public void setRefCount(int refCount) {
        this.refCount = refCount;
    }

}
