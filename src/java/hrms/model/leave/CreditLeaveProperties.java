

package hrms.model.leave;

import java.util.ArrayList;

public class CreditLeaveProperties {
  	private String fromDate = null;
	private String toDate = null;
	private String compMonths = null;
	private String leaveCredited = null;
	private String totEOLNumber = null;
	private String leaveDeduct = null;
	private String creditBal = null;
	private String creditBalShow=null;				/*Added to show the cumulative balance leave*/
	private String creditType = null;
	private ArrayList availedLeave = null;
	private String ifaddEL = null; 

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getCompMonths() {
        return compMonths;
    }

    public void setCompMonths(String compMonths) {
        this.compMonths = compMonths;
    }

    public String getLeaveCredited() {
        return leaveCredited;
    }

    public void setLeaveCredited(String leaveCredited) {
        this.leaveCredited = leaveCredited;
    }

    public String getTotEOLNumber() {
        return totEOLNumber;
    }

    public void setTotEOLNumber(String totEOLNumber) {
        this.totEOLNumber = totEOLNumber;
    }

    public String getLeaveDeduct() {
        return leaveDeduct;
    }

    public void setLeaveDeduct(String leaveDeduct) {
        this.leaveDeduct = leaveDeduct;
    }

    public String getCreditBal() {
        return creditBal;
    }

    public void setCreditBal(String creditBal) {
        this.creditBal = creditBal;
    }

    public String getCreditBalShow() {
        return creditBalShow;
    }

    public void setCreditBalShow(String creditBalShow) {
        this.creditBalShow = creditBalShow;
    }

    public String getCreditType() {
        return creditType;
    }

    public void setCreditType(String creditType) {
        this.creditType = creditType;
    }

    public ArrayList getAvailedLeave() {
        return availedLeave;
    }

    public void setAvailedLeave(ArrayList availedLeave) {
        this.availedLeave = availedLeave;
    }

    public String getIfaddEL() {
        return ifaddEL;
    }

    public void setIfaddEL(String ifaddEL) {
        this.ifaddEL = ifaddEL;
    }
        
}
