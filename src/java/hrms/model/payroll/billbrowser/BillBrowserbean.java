/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.model.payroll.billbrowser;

/**
 *
 * @author Manas Jena
 */
public class BillBrowserbean { 
    
    private String offCode;
    private String txtbilltype;
    private int sltMonth;
    private int sltYear;
    private int sltFromMonth;
    private int sltFromYear;
    private int sltToMonth;
    private int sltToYear;
    private String processDate;
    private String billNo;
    private String billdesc;
    private String billDate;
    private String treasury;
    private String instrNo=null;
    private String vchNo = null;
    private String vchDt = null;
    private String benificiaryNumber=null;
    private String[] billgroupId;
    private String bgid;
    private int priority;
    private int status;
    private String chartofAcct;
    
    
    private String txtDemandno=null;
    private String txtmajcode=null;

    private String submajcode=null;

    private String txtmincode=null;

    private String submincode1=null;

    private String submincode2=null;

    private String submincode3=null;

    private String sectorCode=null;
    
    private String planCode=null;
    
    private String sltTrCode;
    
    private String sltLoan;

    public int getSltFromMonth() {
        return sltFromMonth;
    }

    public void setSltFromMonth(int sltFromMonth) {
        this.sltFromMonth = sltFromMonth;
    }

    public int getSltFromYear() {
        return sltFromYear;
    }

    public void setSltFromYear(int sltFromYear) {
        this.sltFromYear = sltFromYear;
    }

    public int getSltToMonth() {
        return sltToMonth;
    }

    public void setSltToMonth(int sltToMonth) {
        this.sltToMonth = sltToMonth;
    }

    public int getSltToYear() {
        return sltToYear;
    }

    public void setSltToYear(int sltToYear) {
        this.sltToYear = sltToYear;
    }
    

    
    
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    

    public String getBgid() {
        return bgid;
    }

    public void setBgid(String bgid) {
        this.bgid = bgid;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
    

    public String getOffCode() {
        return offCode;
    }

    public void setOffCode(String offCode) {
        this.offCode = offCode;
    }    

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }
    public String getTxtbilltype() {
        return txtbilltype;
    }

    public void setTxtbilltype(String txtbilltype) {
        this.txtbilltype = txtbilltype;
    }

    public int getSltMonth() {
        return sltMonth;
    }

    public void setSltMonth(int sltMonth) {
        this.sltMonth = sltMonth;
    }

    public int getSltYear() {
        return sltYear;
    }

    public void setSltYear(int sltYear) {
        this.sltYear = sltYear;
    }

    public String getProcessDate() {
        return processDate;
    }

    public void setProcessDate(String processDate) {
        this.processDate = processDate;
    }

    public String[] getBillgroupId() {
        return billgroupId;
    }

    public void setBillgroupId(String[] billgroupId) {
        this.billgroupId = billgroupId;
    }

    public String getTreasury() {
        return treasury;
    }

    public void setTreasury(String treasury) {
        this.treasury = treasury;
    }

    public String getInstrNo() {
        return instrNo;
    }

    public void setInstrNo(String instrNo) {
        this.instrNo = instrNo;
    }

    public String getVchNo() {
        return vchNo;
    }

    public void setVchNo(String vchNo) {
        this.vchNo = vchNo;
    }

    public String getVchDt() {
        return vchDt;
    }

    public void setVchDt(String vchDt) {
        this.vchDt = vchDt;
    }

    public String getBenificiaryNumber() {
        return benificiaryNumber;
    }

    public void setBenificiaryNumber(String benificiaryNumber) {
        this.benificiaryNumber = benificiaryNumber;
    }

    public String getBilldesc() {
        return billdesc;
    }

    public void setBilldesc(String billdesc) {
        this.billdesc = billdesc;
    }

    public String getBillDate() {
        return billDate;
    }

    public void setBillDate(String billDate) {
        this.billDate = billDate;
    }

    public String getChartofAcct() {
        return chartofAcct;
    }

    public void setChartofAcct(String chartofAcct) {
        this.chartofAcct = chartofAcct;
    }

    public String getTxtDemandno() {
        return txtDemandno;
    }

    public void setTxtDemandno(String txtDemandno) {
        this.txtDemandno = txtDemandno;
    }

    public String getTxtmajcode() {
        return txtmajcode;
    }

    public void setTxtmajcode(String txtmajcode) {
        this.txtmajcode = txtmajcode;
    }

    public String getSubmajcode() {
        return submajcode;
    }

    public void setSubmajcode(String submajcode) {
        this.submajcode = submajcode;
    }

    public String getTxtmincode() {
        return txtmincode;
    }

    public void setTxtmincode(String txtmincode) {
        this.txtmincode = txtmincode;
    }

    public String getSubmincode1() {
        return submincode1;
    }

    public void setSubmincode1(String submincode1) {
        this.submincode1 = submincode1;
    }

    public String getSubmincode2() {
        return submincode2;
    }

    public void setSubmincode2(String submincode2) {
        this.submincode2 = submincode2;
    }

    public String getSubmincode3() {
        return submincode3;
    }

    public void setSubmincode3(String submincode3) {
        this.submincode3 = submincode3;
    }

    public String getSectorCode() {
        return sectorCode;
    }

    public void setSectorCode(String sectorCode) {
        this.sectorCode = sectorCode;
    }

    public String getPlanCode() {
        return planCode;
    }

    public void setPlanCode(String planCode) {
        this.planCode = planCode;
    }

    public String getSltTrCode() {
        return sltTrCode;
    }

    public void setSltTrCode(String sltTrCode) {
        this.sltTrCode = sltTrCode;
    }

    public String getSltLoan() {
        return sltLoan;
    }

    public void setSltLoan(String sltLoan) {
        this.sltLoan = sltLoan;
    }
}
