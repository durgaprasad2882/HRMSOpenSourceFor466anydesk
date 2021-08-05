/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.model.payroll.allowancededcution;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 *
 * @author Manas Jena
 */
public class AllowanceDeduction {

    private String refadcode = null;
    private String adcode = null;
    private String addesc = null;
    private String adcodename = null;
    private String adtype = null;
    private String formula = null;
    private String head = null;
    private int advalue = 0;
    private String whereupdated = null;
    private int isfixed = 0;
    private String updationRefCode=null;    
    private String adamttype=null;
    
    
    private String dedType = null;
    private String altUnit = null;
    private int rownum = 0;
    private int repCol = 0;
    private String accNo = null;
    private String schedule = null;

    public String getDedType() {
        return dedType;
    }

    public void setDedType(String dedType) {
        this.dedType = dedType;
    }

    public String getAltUnit() {
        return altUnit;
    }

    public void setAltUnit(String altUnit) {
        this.altUnit = altUnit;
    }

    public int getRownum() {
        return rownum;
    }

    public void setRownum(int rownum) {
        this.rownum = rownum;
    }

    public int getRepCol() {
        return repCol;
    }

    public void setRepCol(int repCol) {
        this.repCol = repCol;
    }

    public String getAccNo() {
        return accNo;
    }

    public void setAccNo(String accNo) {
        this.accNo = accNo;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }
    
    

    public String getRefadcode() {
        return refadcode;
    }

    public void setRefadcode(String refadcode) {
        this.refadcode = refadcode;
    }

    public String getAdcode() {
        return adcode;
    }

    public void setAdcode(String adcode) {
        this.adcode = adcode;
    }

    public String getAddesc() {
        return addesc;
    }

    public void setAddesc(String addesc) {
        this.addesc = addesc;
    }

    public String getAdcodename() {
        return adcodename;
    }

    public void setAdcodename(String adcodename) {
        this.adcodename = adcodename;
    }

    public String getAdtype() {
        return adtype;
    }

    public void setAdtype(String adtype) {
        this.adtype = adtype;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public int getAdvalue() {
        return advalue;
    }

    public void setAdvalue(int advalue) {
        this.advalue = advalue;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getWhereupdated() {
        return whereupdated;
    }

    public void setWhereupdated(String whereupdated) {
        this.whereupdated = whereupdated;
    }

    public int getIsfixed() {
        return isfixed;
    }

    public void setIsfixed(int isfixed) {
        this.isfixed = isfixed;
    }

    public String getUpdationRefCode() {
        return updationRefCode;
    }

    public void setUpdationRefCode(String updationRefCode) {
        this.updationRefCode = updationRefCode;
    }

    public String getAdamttype() {
        return adamttype;
    }

    public void setAdamttype(String adamttype) {
        this.adamttype = adamttype;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
