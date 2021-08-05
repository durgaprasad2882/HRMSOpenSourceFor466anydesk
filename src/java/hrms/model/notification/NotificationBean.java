/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.model.notification;

import java.util.Date;

/**
 *
 * @author Surendra
 */
public class NotificationBean {
    
    private String notid="";
    private String nottype="";
    private String empId="";
    private Date dateofEntry=null;
    private String ifAssumed="";
    private String ordno="";
    private Date ordDate=null;
    private String deptCode="";
    private String offCode="";
    private String spc="";
    private String note="";
    private String ifVisible="";
    private String entryDeptCode="";
    private String entryOffCode="";
    private String entryAuthCode="";
    private String sancDeptCode="";
    private String sancOffCode="";
    private String sancAuthCode="";

    public String getNotid() {
        return notid;
    }

    public void setNotid(String notid) {
        this.notid = notid;
    }

    public String getNottype() {
        return nottype;
    }

    public void setNottype(String nottype) {
        this.nottype = nottype;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public Date getDateofEntry() {
        return dateofEntry;
    }

    public void setDateofEntry(Date dateofEntry) {
        this.dateofEntry = dateofEntry;
    }

    public String getIfAssumed() {
        return ifAssumed;
    }

    public void setIfAssumed(String ifAssumed) {
        this.ifAssumed = ifAssumed;
    }

    public String getOrdno() {
        return ordno;
    }

    public void setOrdno(String ordno) {
        this.ordno = ordno;
    }

    public Date getOrdDate() {
        return ordDate;
    }

    public void setOrdDate(Date ordDate) {
        this.ordDate = ordDate;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public String getOffCode() {
        return offCode;
    }

    public void setOffCode(String offCode) {
        this.offCode = offCode;
    }

    public String getSpc() {
        return spc;
    }

    public void setSpc(String spc) {
        this.spc = spc;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getIfVisible() {
        return ifVisible;
    }

    public void setIfVisible(String ifVisible) {
        this.ifVisible = ifVisible;
    }

    public String getEntryDeptCode() {
        return entryDeptCode;
    }

    public void setEntryDeptCode(String entryDeptCode) {
        this.entryDeptCode = entryDeptCode;
    }

    public String getEntryOffCode() {
        return entryOffCode;
    }

    public void setEntryOffCode(String entryOffCode) {
        this.entryOffCode = entryOffCode;
    }

    public String getEntryAuthCode() {
        return entryAuthCode;
    }

    public void setEntryAuthCode(String entryAuthCode) {
        this.entryAuthCode = entryAuthCode;
    }

    public String getSancDeptCode() {
        return sancDeptCode;
    }

    public void setSancDeptCode(String sancDeptCode) {
        this.sancDeptCode = sancDeptCode;
    }

    public String getSancOffCode() {
        return sancOffCode;
    }

    public void setSancOffCode(String sancOffCode) {
        this.sancOffCode = sancOffCode;
    }

    public String getSancAuthCode() {
        return sancAuthCode;
    }

    public void setSancAuthCode(String sancAuthCode) {
        this.sancAuthCode = sancAuthCode;
    }
    
    
    
}
