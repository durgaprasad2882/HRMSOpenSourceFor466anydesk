/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hrms.model.loan;

import java.util.ArrayList;

/**
 *
 * @author Manas
 */
public class EmployeeLoan {
    private String loanamt = null;
    private String loandate = null;
    private String iscompleted = null;
    private String now_ded = null;    
    private ArrayList<EmployeeLoanAccBean> loanAccDetails = new ArrayList();

    public String getLoanamt() {
        return loanamt;
    }

    public void setLoanamt(String loanamt) {
        this.loanamt = loanamt;
    }

    public String getLoandate() {
        return loandate;
    }

    public void setLoandate(String loandate) {
        this.loandate = loandate;
    }

    public String getIscompleted() {
        return iscompleted;
    }

    public void setIscompleted(String iscompleted) {
        this.iscompleted = iscompleted;
    }

    public String getNow_ded() {
        return now_ded;
    }

    public void setNow_ded(String now_ded) {
        this.now_ded = now_ded;
    }

    public ArrayList<EmployeeLoanAccBean> getLoanAccDetails() {
        return loanAccDetails;
    }

    public void setLoanAccDetails(ArrayList<EmployeeLoanAccBean> loanAccDetails) {
        this.loanAccDetails = loanAccDetails;
    }
    
    
}
