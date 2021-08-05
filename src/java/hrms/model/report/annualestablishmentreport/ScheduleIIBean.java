package hrms.model.report.annualestablishmentreport;

public class ScheduleIIBean {
    
    private String payscale;
    private int teacherSanctionedStrengthPlan;
    private int teacherSanctionedStrengthNonPlan;
    private int teacherSanctionedStrengthTotal;
    private int othersSanctionedStrengthPlan;
    private int othersSanctionedStrengthNonPlan;
    private int othersSanctionedStrengthTotal;
    private int totalPlan;
    private int totalNonPlan;
    private int totalSanctionedStrength;

    public String getPayscale() {
        return payscale;
    }

    public void setPayscale(String payscale) {
        this.payscale = payscale;
    }

    public int getTeacherSanctionedStrengthPlan() {
        return teacherSanctionedStrengthPlan;
    }

    public void setTeacherSanctionedStrengthPlan(int teacherSanctionedStrengthPlan) {
        this.teacherSanctionedStrengthPlan = teacherSanctionedStrengthPlan;
    }

    public int getTeacherSanctionedStrengthNonPlan() {
        return teacherSanctionedStrengthNonPlan;
    }

    public void setTeacherSanctionedStrengthNonPlan(int teacherSanctionedStrengthNonPlan) {
        this.teacherSanctionedStrengthNonPlan = teacherSanctionedStrengthNonPlan;
    }

    public int getOthersSanctionedStrengthPlan() {
        return othersSanctionedStrengthPlan;
    }

    public void setOthersSanctionedStrengthPlan(int othersSanctionedStrengthPlan) {
        this.othersSanctionedStrengthPlan = othersSanctionedStrengthPlan;
    }

    public int getOthersSanctionedStrengthNonPlan() {
        return othersSanctionedStrengthNonPlan;
    }

    public void setOthersSanctionedStrengthNonPlan(int othersSanctionedStrengthNonPlan) {
        this.othersSanctionedStrengthNonPlan = othersSanctionedStrengthNonPlan;
    }

    public int getTotalSanctionedStrength() {
        return totalSanctionedStrength;
    }

    public void setTotalSanctionedStrength(int totalSanctionedStrength) {
        this.totalSanctionedStrength = totalSanctionedStrength;
    }

    public int getTeacherSanctionedStrengthTotal() {
        return teacherSanctionedStrengthTotal;
    }

    public void setTeacherSanctionedStrengthTotal(int teacherSanctionedStrengthTotal) {
        this.teacherSanctionedStrengthTotal = teacherSanctionedStrengthTotal;
    }

    public int getOthersSanctionedStrengthTotal() {
        return othersSanctionedStrengthTotal;
    }

    public void setOthersSanctionedStrengthTotal(int othersSanctionedStrengthTotal) {
        this.othersSanctionedStrengthTotal = othersSanctionedStrengthTotal;
    }

    public int getTotalPlan() {
        return totalPlan;
    }

    public void setTotalPlan(int totalPlan) {
        this.totalPlan = totalPlan;
    }

    public int getTotalNonPlan() {
        return totalNonPlan;
    }

    public void setTotalNonPlan(int totalNonPlan) {
        this.totalNonPlan = totalNonPlan;
    }
}
