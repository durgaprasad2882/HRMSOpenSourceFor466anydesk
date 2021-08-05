package hrms.model.parmast;

public class ParMasterBean {

    private int parid;
    private int parstatus;
    private String periodfrom = null;
    private String periodto = null;
    private String designation = null;
    private String isClosed = null;
    private String authRemarksClosed = null;

    public int getParid() {
        return parid;
    }

    public void setParid(int parid) {
        this.parid = parid;
    }

    public String getPeriodfrom() {
        return periodfrom;
    }

    public void setPeriodfrom(String periodfrom) {
        this.periodfrom = periodfrom;
    }

    public String getPeriodto() {
        return periodto;
    }

    public void setPeriodto(String periodto) {
        this.periodto = periodto;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public int getParstatus() {
        return parstatus;
    }

    public void setParstatus(int parstatus) {
        this.parstatus = parstatus;
    }

    public String getIsClosed() {
        return isClosed;
    }

    public void setIsClosed(String isClosed) {
        this.isClosed = isClosed;
    }

    public String getAuthRemarksClosed() {
        return authRemarksClosed;
    }

    public void setAuthRemarksClosed(String authRemarksClosed) {
        this.authRemarksClosed = authRemarksClosed;
    }

}
