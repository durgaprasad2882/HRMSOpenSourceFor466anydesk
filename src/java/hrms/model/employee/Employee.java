/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.model.employee;

import java.util.ArrayList;
import java.util.Date;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author manas jena
 */
public class Employee {

    private String empid;
    private String fname;
    private String mname;
    private String lname;
    private String gender;
    private String marital;
    private String category;
    private String domicile;

    private String recsource;//Requirement Source
    private int height;
    private String gpfno;
    private int basic;
    private String dob;
    private String joindategoo;
    private String dor;
    private String department;
    private String deptcode;
    private String post;
    private String postcode;
    private String cardeallotmentyear;
    private String spn;
    private String spc;
    private String office;
    private String officecode;
    private String bloodgrp;
    private String religion;
    private String permanentaddr;
    private String permanentdist;
    private String permanentps;
    private String phyhandicapt;
    private String idmark;
    private String mobile;
    private String prmTelNo;
    private Date dateOfCurPosting;
    private int gp;
    private String payScale;
    private String cadreCode;
    private String postGrpType;
    private String email;

    private String fieldOffCode;

    private String chest;
    private String weight;
    private String leftvision;
    private String rightvision;
    private String brassno;

    private String station;
    private String addlCharge;
    private String remark;

    private String cadreGrade;
    private String aadhaarno;

    private String entryGovDateText = null;
    private String dobText;
    private String joinDateText;
    private String ifReservation;
    private String ifRehabiltation;
    private String doeGov = null;
    private String homeTown = null;
    private String empName;
    private String empCadre = null;
    private String empAllotmentYear = null;
    private String ifpPan = null;
    private String serverDate = null;
    private String residenceAdd = null;
    private ArrayList educationList = null;
    private String intitals;
    private String fullname;

    private String gisNo;
    private String gisType;
    private String domicil = null;
    private String timeOfEntryGoo = null;
    private String sltBank = null;
    private String sltbranch = null;
    private String bankaccno = null;

    public String getBankaccno() {
        return bankaccno;
    }

    public void setBankaccno(String bankaccno) {
        this.bankaccno = bankaccno;
    }
    
    

    public String getDomicil() {
        return domicil;
    }

    public void setDomicil(String domicil) {
        this.domicil = domicil;
    }

    public String getTimeOfEntryGoo() {
        return timeOfEntryGoo;
    }

    public void setTimeOfEntryGoo(String timeOfEntryGoo) {
        this.timeOfEntryGoo = timeOfEntryGoo;
    }

    public String getSltBank() {
        return sltBank;
    }

    public void setSltBank(String sltBank) {
        this.sltBank = sltBank;
    }

    public String getSltbranch() {
        return sltbranch;
    }

    public void setSltbranch(String sltbranch) {
        this.sltbranch = sltbranch;
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

    public String getIntitals() {
        return intitals;
    }

    public void setIntitals(String Intitals) {
        this.intitals = Intitals;
    }

    public String getFullname() {
        return StringUtils.defaultString(this.intitals) + " " + this.fname + " " + StringUtils.defaultString(this.mname) + " " + this.lname;
    }

    public String getIfpPan() {
        return ifpPan;
    }

    public void setIfpPan(String ifpPan) {
        this.ifpPan = ifpPan;
    }

    public String getServerDate() {
        return serverDate;
    }

    public void setServerDate(String serverDate) {
        this.serverDate = serverDate;
    }

    public String getResidenceAdd() {
        return residenceAdd;
    }

    public void setResidenceAdd(String residenceAdd) {
        this.residenceAdd = residenceAdd;
    }

    public ArrayList getEducationList() {
        return educationList;
    }

    public void setEducationList(ArrayList educationList) {
        this.educationList = educationList;
    }

    public String getEmpAllotmentYear() {
        return empAllotmentYear;
    }

    public void setEmpAllotmentYear(String empAllotmentYear) {
        this.empAllotmentYear = empAllotmentYear;
    }

    public String getEmpCadre() {
        return empCadre;
    }

    public void setEmpCadre(String empCadre) {
        this.empCadre = empCadre;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getHomeTown() {
        return homeTown;
    }

    public void setHomeTown(String homeTown) {
        this.homeTown = homeTown;
    }

    public String getDoeGov() {
        return doeGov;
    }

    public void setDoeGov(String doeGov) {
        this.doeGov = doeGov;
    }

    public String getEntryGovDateText() {
        return entryGovDateText;
    }

    public void setEntryGovDateText(String entryGovDateText) {
        this.entryGovDateText = entryGovDateText;
    }

    public String getDobText() {
        return dobText;
    }

    public void setDobText(String dobText) {
        this.dobText = dobText;
    }

    public String getJoinDateText() {
        return joinDateText;
    }

    public void setJoinDateText(String joinDateText) {
        this.joinDateText = joinDateText;
    }

    public String getIfReservation() {
        return ifReservation;
    }

    public void setIfReservation(String ifReservation) {
        this.ifReservation = ifReservation;
    }

    public String getIfRehabiltation() {
        return ifRehabiltation;
    }

    public void setIfRehabiltation(String ifRehabiltation) {
        this.ifRehabiltation = ifRehabiltation;
    }

    public String getCadreGrade() {
        return cadreGrade;
    }

    public void setCadreGrade(String cadreGrade) {
        this.cadreGrade = cadreGrade;
    }

    public String getAadhaarno() {
        return aadhaarno;
    }

    public void setAadhaarno(String aadhaarno) {
        this.aadhaarno = aadhaarno;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public String getAddlCharge() {
        return addlCharge;
    }

    public void setAddlCharge(String addlCharge) {
        this.addlCharge = addlCharge;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPermanentaddr() {
        return permanentaddr;
    }

    public void setPermanentaddr(String permanentaddr) {
        this.permanentaddr = permanentaddr;
    }

    public String getRecsource() {
        return recsource;
    }

    public void setRecsource(String recsource) {
        this.recsource = recsource;
    }

    public String getCardeallotmentyear() {
        return cardeallotmentyear;
    }

    public void setCardeallotmentyear(String cardeallotmentyear) {
        this.cardeallotmentyear = cardeallotmentyear;
    }

    public String getEmpid() {
        return empid;
    }

    public void setEmpid(String empid) {
        this.empid = empid;
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

    public String getGpfno() {
        return gpfno;
    }

    public void setGpfno(String gpfno) {
        this.gpfno = gpfno;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getSpn() {
        return spn;
    }

    public void setSpn(String spn) {
        this.spn = spn;
    }

    public String getSpc() {
        return spc;
    }

    public void setSpc(String spc) {
        this.spc = spc;
    }

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public String getOfficecode() {
        return officecode;
    }

    public void setOfficecode(String officecode) {
        this.officecode = officecode;
    }

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getBasic() {
        return basic;
    }

    public void setBasic(int basic) {
        this.basic = basic;
    }

    public String getDor() {
        return dor;
    }

    public void setDor(String dor) {
        this.dor = dor;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDeptcode() {
        return deptcode;
    }

    public void setDeptcode(String deptcode) {
        this.deptcode = deptcode;
    }

    public String getBloodgrp() {
        return bloodgrp;
    }

    public void setBloodgrp(String bloodgrp) {
        this.bloodgrp = bloodgrp;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public String getPermanentdist() {
        return permanentdist;
    }

    public void setPermanentdist(String permanentdist) {
        this.permanentdist = permanentdist;
    }

    public String getPermanentps() {
        return permanentps;
    }

    public void setPermanentps(String permanentps) {
        this.permanentps = permanentps;
    }

    public String getPhyhandicapt() {
        return phyhandicapt;
    }

    public void setPhyhandicapt(String phyhandicapt) {
        this.phyhandicapt = phyhandicapt;
    }

    public String getIdmark() {
        return idmark;
    }

    public void setIdmark(String idmark) {
        this.idmark = idmark;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPrmTelNo() {
        return prmTelNo;
    }

    public void setPrmTelNo(String prmTelNo) {
        this.prmTelNo = prmTelNo;
    }

    public Date getDateOfCurPosting() {
        return dateOfCurPosting;
    }

    public void setDateOfCurPosting(Date dateOfCurPosting) {
        this.dateOfCurPosting = dateOfCurPosting;
    }

    public int getGp() {
        return gp;
    }

    public void setGp(int gp) {
        this.gp = gp;
    }

    public String getPayScale() {
        return payScale;
    }

    public void setPayScale(String payScale) {
        this.payScale = payScale;
    }

    public String getCadreCode() {
        return cadreCode;
    }

    public void setCadreCode(String cadreCode) {
        this.cadreCode = cadreCode;
    }

    public String getPostGrpType() {
        return postGrpType;
    }

    public void setPostGrpType(String postGrpType) {
        this.postGrpType = postGrpType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFieldOffCode() {
        return fieldOffCode;
    }

    public void setFieldOffCode(String fieldOffCode) {
        this.fieldOffCode = fieldOffCode;
    }

    public String getChest() {
        return chest;
    }

    public void setChest(String chest) {
        this.chest = chest;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getLeftvision() {
        return leftvision;
    }

    public void setLeftvision(String leftvision) {
        this.leftvision = leftvision;
    }

    public String getRightvision() {
        return rightvision;
    }

    public void setRightvision(String rightvision) {
        this.rightvision = rightvision;
    }

    public String getBrassno() {
        return brassno;
    }

    public void setBrassno(String brassno) {
        this.brassno = brassno;
    }

    public String getDomicile() {
        return domicile;
    }

    public void setDomicile(String domicile) {
        this.domicile = domicile;
    }

    public String getJoindategoo() {
        return joindategoo;
    }

    public void setJoindategoo(String joindategoo) {
        this.joindategoo = joindategoo;
    }

}
