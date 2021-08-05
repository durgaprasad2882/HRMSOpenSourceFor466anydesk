/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.model.report.annualestablishmentreport;

/**
 *
 * @author lenovo pc
 */
public class DepartmentWiseAerStatus {
    String deptCode=null;
    String deptName=null;
    String noAerSubmitted=null;
    String noOfAerAproved=null;
    String noOfDDO=null;
    String offName=null;

    public String getOffName() {
        return offName;
    }

    public void setOffName(String offName) {
        this.offName = offName;
    }
    

    public String getNoOfDDO() {
        return noOfDDO;
    }

    public void setNoOfDDO(String noOfDDO) {
        this.noOfDDO = noOfDDO;
    }
    

    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getNoAerSubmitted() {
        return noAerSubmitted;
    }

    public void setNoAerSubmitted(String noAerSubmitted) {
        this.noAerSubmitted = noAerSubmitted;
    }

    public String getNoOfAerAproved() {
        return noOfAerAproved;
    }

    public void setNoOfAerAproved(String noOfAerAproved) {
        this.noOfAerAproved = noOfAerAproved;
    }
    
    
}
