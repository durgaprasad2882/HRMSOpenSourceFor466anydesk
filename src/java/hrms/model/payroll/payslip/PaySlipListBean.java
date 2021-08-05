package hrms.model.payroll.payslip;

public class PaySlipListBean {

    private String aqslno = null;
    private String month = null;
    private String year = null;
    private String month_year = null;
    private String basic = null;
    private String totallowance = null;
    private String gross = null;
    private String totdeduction = null;
    private String netpay = null;
    private String empId = null;

    public String getAqslno() {
        return aqslno;
    }

    public void setAqslno(String aqslno) {
        this.aqslno = aqslno;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getBasic() {
        return basic;
    }

    public void setBasic(String basic) {
        this.basic = basic;
    }

    public String getTotallowance() {
        return totallowance;
    }

    public void setTotallowance(String totallowance) {
        this.totallowance = totallowance;
    }

    public String getGross() {
        return gross;
    }

    public void setGross(String gross) {
        this.gross = gross;
    }

    public String getTotdeduction() {
        return totdeduction;
    }

    public void setTotdeduction(String totdeduction) {
        this.totdeduction = totdeduction;
    }

    public String getNetpay() {
        return netpay;
    }

    public void setNetpay(String netpay) {
        this.netpay = netpay;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getMonth_year() {
        return month_year;
    }

    public void setMonth_year(String month_year) {
        this.month_year = month_year;
    }

}
