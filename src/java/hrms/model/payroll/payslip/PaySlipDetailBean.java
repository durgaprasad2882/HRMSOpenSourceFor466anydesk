package hrms.model.payroll.payslip;

import java.util.ArrayList;

public class PaySlipDetailBean {

    private String billID = null;
    private String empId = null;
    private String empCode = null;
    private String empBasic = null;
    private String sltYear = null;
    private String sltMonth = null;
    private ArrayList empList = null;
    private String aqSlNo = null;
    private String offName = null;
    private String empName = null;
    private String curDesig = null;
    private String gpfno = null;
    private String scalePay = null;
    private String curBasic = null;
    private String forMonth = null;
    private String forYear = null;
    private String daysWork = null;
    private String monthPay = null;
    private String billNo = null;
    private String bankAcno = null;
    private String bank = null;
    private int totdeductAmt = 0;
    private int totallowAmt = 0;
    private int totpvtdedAmt = 0;
    private int grossAmount = 0;
    private int netAmount = 0;
    private String amountWords = null;
    private String brassno = null;
    private String vchno = null;
    private String vchdate = null;
    private String billdate = null;

    private ADDetails[] deductList = null;
    private ADDetails[] allowList = null;
    private ArrayList pvtdeductList = null;
    private ArrayList loanList = null;

    public String getBillID() {
        return billID;
    }

    public void setBillID(String billID) {
        this.billID = billID;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getEmpCode() {
        return empCode;
    }

    public void setEmpCode(String empCode) {
        this.empCode = empCode;
    }

    public String getEmpBasic() {
        return empBasic;
    }

    public void setEmpBasic(String empBasic) {
        this.empBasic = empBasic;
    }

    public String getSltYear() {
        return sltYear;
    }

    public void setSltYear(String sltYear) {
        this.sltYear = sltYear;
    }

    public String getSltMonth() {
        return sltMonth;
    }

    public void setSltMonth(String sltMonth) {
        this.sltMonth = sltMonth;
    }

    public ArrayList getEmpList() {
        return empList;
    }

    public void setEmpList(ArrayList empList) {
        this.empList = empList;
    }

    public String getAqSlNo() {
        return aqSlNo;
    }

    public void setAqSlNo(String aqSlNo) {
        this.aqSlNo = aqSlNo;
    }

    public String getOffName() {
        return offName;
    }

    public void setOffName(String offName) {
        this.offName = offName;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getCurDesig() {
        return curDesig;
    }

    public void setCurDesig(String curDesig) {
        this.curDesig = curDesig;
    }

    public String getGpfno() {
        return gpfno;
    }

    public void setGpfno(String gpfno) {
        this.gpfno = gpfno;
    }

    public String getScalePay() {
        return scalePay;
    }

    public void setScalePay(String scalePay) {
        this.scalePay = scalePay;
    }

    public String getCurBasic() {
        return curBasic;
    }

    public void setCurBasic(String curBasic) {
        this.curBasic = curBasic;
    }

    public String getForMonth() {
        return forMonth;
    }

    public void setForMonth(String forMonth) {
        this.forMonth = forMonth;
    }

    public String getForYear() {
        return forYear;
    }

    public void setForYear(String forYear) {
        this.forYear = forYear;
    }

    public String getDaysWork() {
        return daysWork;
    }

    public void setDaysWork(String daysWork) {
        this.daysWork = daysWork;
    }

    public String getMonthPay() {
        return monthPay;
    }

    public void setMonthPay(String monthPay) {
        this.monthPay = monthPay;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getBankAcno() {
        return bankAcno;
    }

    public void setBankAcno(String bankAcno) {
        this.bankAcno = bankAcno;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public int getTotdeductAmt() {
        return totdeductAmt;
    }

    public void setTotdeductAmt(int totdeductAmt) {
        this.totdeductAmt = totdeductAmt;
    }

    public int getTotallowAmt() {
        return totallowAmt;
    }

    public void setTotallowAmt(int totallowAmt) {
        this.totallowAmt = totallowAmt;
    }

    public int getTotpvtdedAmt() {
        return totpvtdedAmt;
    }

    public void setTotpvtdedAmt(int totpvtdedAmt) {
        this.totpvtdedAmt = totpvtdedAmt;
    }

    public int getGrossAmount() {
        return grossAmount;
    }

    public void setGrossAmount(int grossAmount) {
        this.grossAmount = grossAmount;
    }

    public int getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(int netAmount) {
        this.netAmount = netAmount;
    }

    public String getAmountWords() {
        return amountWords;
    }

    public void setAmountWords(String amountWords) {
        this.amountWords = amountWords;
    }

    public String getBrassno() {
        return brassno;
    }

    public void setBrassno(String brassno) {
        this.brassno = brassno;
    }

    public String getVchno() {
        return vchno;
    }

    public void setVchno(String vchno) {
        this.vchno = vchno;
    }

    public String getVchdate() {
        return vchdate;
    }

    public void setVchdate(String vchdate) {
        this.vchdate = vchdate;
    }

    public String getBilldate() {
        return billdate;
    }

    public void setBilldate(String billdate) {
        this.billdate = billdate;
    }

    public ADDetails[] getDeductList() {
        return deductList;
    }

    public void setDeductList(ADDetails[] deductList) {
        this.deductList = deductList;
    }

    public ADDetails[] getAllowList() {
        return allowList;
    }

    public void setAllowList(ADDetails[] allowList) {
        this.allowList = allowList;
    }

    public ArrayList getPvtdeductList() {
        return pvtdeductList;
    }

    public void setPvtdeductList(ArrayList pvtdeductList) {
        this.pvtdeductList = pvtdeductList;
    }

    public ArrayList getLoanList() {
        return loanList;
    }

    public void setLoanList(ArrayList loanList) {
        this.loanList = loanList;
    }
}
