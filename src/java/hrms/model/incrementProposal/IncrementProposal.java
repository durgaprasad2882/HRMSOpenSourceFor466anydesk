/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.model.incrementProposal;

import hrms.model.login.Users;
import hrms.model.master.Office;
import hrms.model.master.SubstantivePost;
import hrms.model.task.TaskBean;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Surendra
 */
public class IncrementProposal implements Serializable {

    private int proposalId;

    private String emplist;

    private Office office;

    private int proposalMonth;

    private String monthasString = null;

    private int proposalYear;

    private String orderno;

    private Date orderDate;

    private TaskBean task;

    private Users emp;

    private SubstantivePost createdBy;

    private Date lastUpdatedOn;

    private SubstantivePost approvedBy;

    private String authspc;

    private int processStatus;

    private String processStatusName;

    public int getProposalId() {
        return proposalId;
    }

    public void setProposalId(int proposalId) {
        this.proposalId = proposalId;
    }

    public String getEmplist() {
        return emplist;
    }

    public void setEmplist(String emplist) {
        this.emplist = emplist;
    }

    public Office getOffice() {
        return office;
    }

    public void setOffice(Office office) {
        this.office = office;
    }

    public int getProposalMonth() {
        return proposalMonth;
    }

    public void setProposalMonth(int proposalMonth) {
        this.proposalMonth = proposalMonth;
    }

    public String getMonthasString() {
        return monthasString;
    }

    public void setMonthasString(String monthasString) {
        this.monthasString = monthasString;
    }

    public int getProposalYear() {
        return proposalYear;
    }

    public void setProposalYear(int proposalYear) {
        this.proposalYear = proposalYear;
    }

    public String getOrderno() {
        return orderno;
    }

    public void setOrderno(String orderno) {
        this.orderno = orderno;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public TaskBean getTask() {
        return task;
    }

    public void setTask(TaskBean task) {
        this.task = task;
    }

    public Users getEmp() {
        return emp;
    }

    public void setEmp(Users emp) {
        this.emp = emp;
    }

    public SubstantivePost getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(SubstantivePost createdBy) {
        this.createdBy = createdBy;
    }

    public Date getLastUpdatedOn() {
        return lastUpdatedOn;
    }

    public void setLastUpdatedOn(Date lastUpdatedOn) {
        this.lastUpdatedOn = lastUpdatedOn;
    }

    public SubstantivePost getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(SubstantivePost approvedBy) {
        this.approvedBy = approvedBy;
    }

    public String getAuthspc() {
        return authspc;
    }

    public void setAuthspc(String authspc) {
        this.authspc = authspc;
    }

    public int getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(int processStatus) {
        this.processStatus = processStatus;
    }

    public String getProcessStatusName() {
        return processStatusName;
    }

    public void setProcessStatusName(String processStatusName) {
        this.processStatusName = processStatusName;
    }

}
