/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.model.login;

import hrms.common.CommonFunctions;
import hrms.model.cadre.Cadre;
import hrms.model.master.Office;
import hrms.model.master.SubstantivePost;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Surendra
 */
public class Users implements Serializable {

    private String empId;

    private Date paydate;

    private String userName;

    private String userPassword;

    private String hrmsEncId;

    private boolean enabled;

    private Set<UserRole> userRole = new HashSet<UserRole>(0);

    private String hasPrivilages;

    private String hasmyCadreTab;

    private String hasmyDeptTab;

    private String hasmyDistTab;

    private String hasmyHodTab;

    private String hasmyOfficeTab;

    private String hasparadminTab;

    private String haspoliceDGTab;

    private String hasPayRevisionAuth;

    private String hascheckingAuth;

    private String hasverifyingAuth;

    private String hasCommandandAuthPriv;

    private String newpassword;

    private String confirmpassword;

    private String initials;

    private String fname;

    private String mname;

    private String lname;

    private String usertype;

    private Date doegov;

    private Date dob;

    private String mobile;

    private String depcode;

    private String depstatus;

    private String postgrp;

    private String acctType;

    private String gpfno;

    private Date dateOfnincr;

    private double gradepay = 0;

    private String payscale;

    private Double curBasic;

    private Cadre cadre;

    private String cadrecode = "";

    private String cadrename = "";

    private String offname = "";

    private String offcode = "";

    private String fullName;

    private Office office;

    private SubstantivePost substantivePost;

    private String curspc;

    private String spn = null;

    private String aadharno;

    private String postname = null;

    private String captcha;//new line

    private String deptcode;
    private String gpc = null;
    private String deptName;

    private String postCode;

    private String gisNo;
    private String gisType;
    private String gender;
    private String marital;
    private String bloodGrp;
    private String category;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMarital() {
        return marital;
    }

    public void setMarital(String marital) {
        this.marital = marital;
    }

    public String getBloodGrp() {
        return bloodGrp;
    }

    public void setBloodGrp(String bloodGrp) {
        this.bloodGrp = bloodGrp;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    
    

    public String getGisType() {
        return gisType;
    }

    public void setGisType(String gisType) {
        this.gisType = gisType;
    }

    public String getGisNo() {
        return gisNo;
    }

    public void setGisNo(String gisNo) {
        this.gisNo = gisNo;
    }

    public String getGpc() {
        return gpc;
    }

    public void setGpc(String gpc) {
        this.gpc = gpc;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getDeptcode() {
        return deptcode;
    }

    public void setDeptcode(String deptcode) {
        this.deptcode = deptcode;
    }

    public String getPostname() {
        return postname;
    }

    public void setPostname(String postname) {
        this.postname = postname;
    }

    public String getSpn() {
        return spn;
    }

    public void setSpn(String spn) {
        this.spn = spn;
    }

    public String getCadrename() {
        return cadrename;
    }

    public void setCadrename(String cadrename) {
        this.cadrename = cadrename;
    }

    public String getOffname() {
        return offname;
    }

    public void setOffname(String offname) {
        this.offname = offname;
    }

    public String getOffcode() {
        return offcode;
    }

    public void setOffcode(String offcode) {
        this.offcode = offcode;
    }

    public String getCurspc() {
        return curspc;
    }

    public void setCurspc(String curspc) {
        this.curspc = curspc;
    }

    public String getAadharno() {
        return aadharno;
    }

    public void setAadharno(String aadharno) {
        this.aadharno = aadharno;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public String getHrmsEncId() {
        return hrmsEncId;
    }

    public void setHrmsEncId(String hrmsEncId) {
        this.hrmsEncId = hrmsEncId;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<UserRole> getUserRole() {
        return userRole;
    }

    public void setUserRole(Set<UserRole> userRole) {
        this.userRole = userRole;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getHasPrivilages() {
        return hasPrivilages;
    }

    public void setHasPrivilages(String hasPrivilages) {
        this.hasPrivilages = hasPrivilages;
    }

    public String getHasmyCadreTab() {
        return hasmyCadreTab;
    }

    public void setHasmyCadreTab(String hasmyCadreTab) {
        this.hasmyCadreTab = hasmyCadreTab;
    }

    public String getHasmyDeptTab() {
        return hasmyDeptTab;
    }

    public void setHasmyDeptTab(String hasmyDeptTab) {
        this.hasmyDeptTab = hasmyDeptTab;
    }

    public String getHasmyDistTab() {
        return hasmyDistTab;
    }

    public void setHasmyDistTab(String hasmyDistTab) {
        this.hasmyDistTab = hasmyDistTab;
    }

    public String getHasmyHodTab() {
        return hasmyHodTab;
    }

    public void setHasmyHodTab(String hasmyHodTab) {
        this.hasmyHodTab = hasmyHodTab;
    }

    public String getHasmyOfficeTab() {
        return hasmyOfficeTab;
    }

    public void setHasmyOfficeTab(String hasmyOfficeTab) {
        this.hasmyOfficeTab = hasmyOfficeTab;
    }

    public String getHasparadminTab() {
        return hasparadminTab;
    }

    public void setHasparadminTab(String hasparadminTab) {
        this.hasparadminTab = hasparadminTab;
    }

    public String getHaspoliceDGTab() {
        return haspoliceDGTab;
    }

    public void setHaspoliceDGTab(String haspoliceDGTab) {
        this.haspoliceDGTab = haspoliceDGTab;
    }

    public String getHasPayRevisionAuth() {
        return hasPayRevisionAuth;
    }

    public void setHasPayRevisionAuth(String hasPayRevisionAuth) {
        this.hasPayRevisionAuth = hasPayRevisionAuth;
    }

    public String getHascheckingAuth() {
        return hascheckingAuth;
    }

    public void setHascheckingAuth(String hascheckingAuth) {
        this.hascheckingAuth = hascheckingAuth;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getNewpassword() {
        return newpassword;
    }

    public void setNewpassword(String newpassword) {
        this.newpassword = newpassword;
    }

    public String getConfirmpassword() {
        return confirmpassword;
    }

    public void setConfirmpassword(String confirmpassword) {
        this.confirmpassword = confirmpassword;
    }

    public String getInitials() {
        return initials;
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getMname() {
        return mname;
    }

    public void setMname(String mname) {
        this.mname = mname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAcctType() {
        return acctType;
    }

    public void setAcctType(String acctType) {
        this.acctType = acctType;
    }

    public String getGpfno() {
        return gpfno;
    }

    public void setGpfno(String gpfno) {
        this.gpfno = gpfno;
    }

    public Office getOffice() {
        return office;
    }

    public void setOffice(Office office) {
        this.office = office;
    }

    public SubstantivePost getSubstantivePost() {
        return substantivePost;
    }

    public void setSubstantivePost(SubstantivePost substantivePost) {
        this.substantivePost = substantivePost;
    }

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public Date getDoegov() {
        return doegov;
    }

    public String getFormattedDoegov() {
        return CommonFunctions.getFormattedOutputDate3(doegov);
    }

    public void setDoegov(Date doegov) {
        this.doegov = doegov;
    }

    public Date getDob() {
        return dob;
    }

    public String getFormattedDob() {
        return CommonFunctions.getFormattedOutputDate3(dob);
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Cadre getCadre() {
        return cadre;
    }

    public void setCadre(Cadre cadre) {
        this.cadre = cadre;
    }

    public Date getDateOfnincr() {
        return dateOfnincr;
    }

    public void setDateOfnincr(Date dateOfnincr) {
        this.dateOfnincr = dateOfnincr;
    }

    public String getPostgrp() {
        return postgrp;
    }

    public void setPostgrp(String postgrp) {
        this.postgrp = postgrp;
    }

    public double getGradepay() {
        return gradepay;
    }

    public void setGradepay(double gradepay) {
        this.gradepay = gradepay;
    }

    public String getPayscale() {
        return payscale;
    }

    public void setPayscale(String payscale) {
        this.payscale = payscale;
    }

    public Double getCurBasic() {
        return curBasic;
    }

    public void setCurBasic(Double curBasic) {
        this.curBasic = curBasic;
    }

    public Date getPaydate() {
        return paydate;
    }

    public void setPaydate(Date paydate) {
        this.paydate = paydate;
    }

    public String getDepcode() {
        return depcode;
    }

    public void setDepcode(String depcode) {
        this.depcode = depcode;
    }

    public String getDepstatus() {
        return depstatus;
    }

    public void setDepstatus(String depstatus) {
        this.depstatus = depstatus;
    }

    public String getCadrecode() {
        return cadrecode;
    }

    public void setCadrecode(String cadrecode) {
        this.cadrecode = cadrecode;
    }

    public String getHasverifyingAuth() {
        return hasverifyingAuth;
    }

    public void setHasverifyingAuth(String hasverifyingAuth) {
        this.hasverifyingAuth = hasverifyingAuth;
    }

    public String getHasCommandandAuthPriv() {
        return hasCommandandAuthPriv;
    }

    public void setHasCommandandAuthPriv(String hasCommandandAuthPriv) {
        this.hasCommandandAuthPriv = hasCommandandAuthPriv;
    }

}
