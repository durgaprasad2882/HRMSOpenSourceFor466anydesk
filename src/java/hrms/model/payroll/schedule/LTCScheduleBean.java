package hrms.model.payroll.schedule;

public class LTCScheduleBean {
    
    private String empName;
    private String empDesg;
    private String gpfNo;
    
    private int adAmt;
    private int principalAmt;
    private int totRecAmt;
    private String refDesc;
    private String accNo;
    
    private String vchNo;

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getEmpDesg() {
        return empDesg;
    }

    public void setEmpDesg(String empDesg) {
        this.empDesg = empDesg;
    }

    public String getGpfNo() {
        return gpfNo;
    }

    public void setGpfNo(String gpfNo) {
        this.gpfNo = gpfNo;
    }

    public int getAdAmt() {
        return adAmt;
    }

    public void setAdAmt(int adAmt) {
        this.adAmt = adAmt;
    }

    public int getPrincipalAmt() {
        return principalAmt;
    }

    public void setPrincipalAmt(int principalAmt) {
        this.principalAmt = principalAmt;
    }

    public int getTotRecAmt() {
        return totRecAmt;
    }

    public void setTotRecAmt(int totRecAmt) {
        this.totRecAmt = totRecAmt;
    }

    public String getRefDesc() {
        return refDesc;
    }

    public void setRefDesc(String refDesc) {
        this.refDesc = refDesc;
    }

    public String getAccNo() {
        return accNo;
    }

    public void setAccNo(String accNo) {
        this.accNo = accNo;
    }

    public String getVchNo() {
        return vchNo;
    }

    public void setVchNo(String vchNo) {
        this.vchNo = vchNo;
    }
    
}
