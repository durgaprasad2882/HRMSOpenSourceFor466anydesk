/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hrms.model.payroll.schedule;

public class OtcForm82Bean extends ScheduleHelper{
    
    private String billDesc;
    private String year;
    private String treasuryName;
    private String month;
    private String netPay=null;
    private int toDdoAccount;
    private String noofEmp=null;
    
    
    public String getBillDesc() {
        return billDesc;
    }

    public void setBillDesc(String billDesc) {
        this.billDesc = billDesc;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getTreasuryName() {
        return treasuryName;
    }

    public void setTreasuryName(String treasuryName) {
        this.treasuryName = treasuryName;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getNetPay() {
        return netPay;
    }

    public void setNetPay(String netPay) {
        this.netPay = netPay;
    }

    public String getNoofEmp() {
        return noofEmp;
    }

    public void setNoofEmp(String noofEmp) {
        this.noofEmp = noofEmp;
    }

    public int getToDdoAccount() {
        return toDdoAccount;
    }

    public void setToDdoAccount(int toDdoAccount) {
        this.toDdoAccount = toDdoAccount;
    }
    
    
    
    
    
    
}
