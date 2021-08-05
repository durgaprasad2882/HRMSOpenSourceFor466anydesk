/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hrms.model.discProceeding;

import java.util.List;

public class DpViewBean {
    
    private int daId;
    private String deptName;
    private String rule15OrderNo;    
    private String rule15OrderDate;
    private String doEmpHrmsId;
    private String doEmpName;
    private String doEmpCurDegn;
    private String irrgularDetails;

    private List chargeListOnly;
    private List chargeList;
    private List chargeDtlsList;
    private List documentList;
    private List witnessList;
    
    
    
    
    public int getDaId() {
        return daId;
    }

    public void setDaId(int daId) {
        this.daId = daId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getRule15OrderNo() {
        return rule15OrderNo;
    }

    public void setRule15OrderNo(String rule15OrderNo) {
        this.rule15OrderNo = rule15OrderNo;
    }

    public String getRule15OrderDate() {
        return rule15OrderDate;
    }

    public void setRule15OrderDate(String rule15OrderDate) {
        this.rule15OrderDate = rule15OrderDate;
    }

    public String getDoEmpName() {
        return doEmpName;
    }

    public void setDoEmpName(String doEmpName) {
        this.doEmpName = doEmpName;
    }

    public String getDoEmpCurDegn() {
        return doEmpCurDegn;
    }

    public void setDoEmpCurDegn(String doEmpCurDegn) {
        this.doEmpCurDegn = doEmpCurDegn;
    }

    public String getDoEmpHrmsId() {
        return doEmpHrmsId;
    }

    public void setDoEmpHrmsId(String doEmpHrmsId) {
        this.doEmpHrmsId = doEmpHrmsId;
    }

    public List getChargeList() {
        return chargeList;
    }

    public void setChargeList(List chargeList) {
        this.chargeList = chargeList;
    }

    public List getWitnessList() {
        return witnessList;
    }

    public void setWitnessList(List witnessList) {
        this.witnessList = witnessList;
    }

    public String getIrrgularDetails() {
        return irrgularDetails;
    }

    public void setIrrgularDetails(String irrgularDetails) {
        this.irrgularDetails = irrgularDetails;
    }

    public List getChargeDtlsList() {
        return chargeDtlsList;
    }

    public void setChargeDtlsList(List chargeDtlsList) {
        this.chargeDtlsList = chargeDtlsList;
    }

    public List getDocumentList() {
        return documentList;
    }

    public void setDocumentList(List documentList) {
        this.documentList = documentList;
    }

    public List getChargeListOnly() {
        return chargeListOnly;
    }

    public void setChargeListOnly(List chargeListOnly) {
        this.chargeListOnly = chargeListOnly;
    }
    
    
    
    
    
    
}
