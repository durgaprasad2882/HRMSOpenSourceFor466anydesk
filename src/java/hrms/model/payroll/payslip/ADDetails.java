package hrms.model.payroll.payslip;

public class ADDetails {

    private String adCode = null;
    private double adAmount = 0;
    private String adDesc = null;

    public String getAdCode() {
        return adCode;
    }

    public void setAdCode(String adCode) {
        this.adCode = adCode;
    }

    public double getAdAmount() {
        return adAmount;
    }

    public void setAdAmount(double adAmount) {
        this.adAmount = adAmount;
    }

    

    public String getAdDesc() {
        return adDesc;
    }

    public void setAdDesc(String adDesc) {
        this.adDesc = adDesc;
    }
}
