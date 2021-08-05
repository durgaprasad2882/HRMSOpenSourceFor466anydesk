/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.model.billvouchingTreasury;

/**
 *
 * @author Surendra
 */
public class BillDetail {

    private String hrmsgeneratedRefno = null;
    private String hrmsgeneratedRefdate = null;
    private String billid;
    private String billType = null;
    private String typeofBillString = null;
    private String billnumber = null;
    private String billdesc = null;
    private String billDate = null;
    private String agbillTypeId = null;
    private String salFromdate = null;
    private String salTodate = null;
    private String ddoccode = null;
    private String offcode = null;
    private String officename = null;
    private String demandNumber = null;
    private String majorHead = null;
    private String subMajorHead = null;
    private String minorHead = null;
    private String subHead = null;
    private String detailHead = null;
    private String planStatus = null;
    private String chargedVoted = null;
    private String sectorCode = null;
    private String grossAmount = null;
    private String netAmount = null;
    private double totdeduction = 0;
    private String tokenNumber = null;
    private String tokendate = null;
    private String prevTokenNumber = null;
    private String prevTokendate = null;
    private String treasuryCode = null;
    private String ddomobileno = null;
    private String beneficiaryrefno = null;
    private String vchNo = null;
    private String vchDt = null;
    int billmonth;
    int billyear;

    private int billStatusId = 0;
    private String aq_month;
    private String aq_year;
    private String billStatus = null;
    private String billgroupId = null;
    private String billgrpname = null;
    private String objectbill;
    private String off_code;

    public String getBillgrpname() {
        return billgrpname;
    }

    public void setBillgrpname(String billgrpname) {
        this.billgrpname = billgrpname;
    }

    public String getBillStatus() {
        return billStatus;
    }

    public void setBillStatus(String billStatus) {
        this.billStatus = billStatus;
    }

    public String getBillgroupId() {
        return billgroupId;
    }

    public void setBillgroupId(String billgroupId) {
        this.billgroupId = billgroupId;
    }

    public int getBillStatusId() {
        return billStatusId;
    }

    public void setBillStatusId(int billStatusId) {
        this.billStatusId = billStatusId;
    }

    public void setHrmsgeneratedRefno(String hrmsgeneratedRefno) {
        this.hrmsgeneratedRefno = hrmsgeneratedRefno;
    }

    public String getHrmsgeneratedRefno() {
        return hrmsgeneratedRefno;
    }

    public void setHrmsgeneratedRefdate(String hrmsgeneratedRefdate) {
        this.hrmsgeneratedRefdate = hrmsgeneratedRefdate;
    }

    public String getHrmsgeneratedRefdate() {
        return hrmsgeneratedRefdate;
    }

    public void setBillType(String billType) {
        this.billType = billType;
    }

    public String getBillType() {
        return billType;
    }

    public String getTypeofBillString() {
        return typeofBillString;
    }

    public void setTypeofBillString(String typeofBillString) {
        this.typeofBillString = typeofBillString;
    }

    public void setBillnumber(String billnumber) {
        this.billnumber = billnumber;
    }

    public String getBillnumber() {
        if (billnumber == null) {
            billnumber = "";
        }
        return billnumber;
    }

    public void setBillDate(String billDate) {
        this.billDate = billDate;
    }

    public String getBillDate() {
        return billDate;
    }

    public void setAgbillTypeId(String agbillTypeId) {
        this.agbillTypeId = agbillTypeId;
    }

    public String getAgbillTypeId() {
        return agbillTypeId;
    }

    public void setSalFromdate(String salFromdate) {
        this.salFromdate = salFromdate;
    }

    public String getSalFromdate() {
        return salFromdate;
    }

    public void setSalTodate(String salTodate) {
        this.salTodate = salTodate;
    }

    public String getSalTodate() {
        return salTodate;
    }

    public String getOffcode() {
        return offcode;
    }

    public void setOffcode(String offcode) {
        this.offcode = offcode;
    }

    public void setDdoccode(String ddoccode) {
        this.ddoccode = ddoccode;
    }

    public String getDdoccode() {
        if (ddoccode == null) {
            ddoccode = "";
        }
        return ddoccode;
    }

    public void setDemandNumber(String demandNumber) {
        this.demandNumber = demandNumber;
    }

    public String getDemandNumber() {
        if (demandNumber == null) {
            demandNumber = "";
        }
        return demandNumber;
    }

    public void setMajorHead(String majorHead) {
        this.majorHead = majorHead;
    }

    public String getMajorHead() {
        if (majorHead == null) {
            majorHead = "";
        }
        return majorHead;
    }

    public void setSubMajorHead(String subMajorHead) {
        this.subMajorHead = subMajorHead;
    }

    public String getSubMajorHead() {
        if (subMajorHead == null) {
            subMajorHead = "";
        }
        return subMajorHead;
    }

    public void setMinorHead(String minorHead) {
        this.minorHead = minorHead;
    }

    public String getMinorHead() {
        if (minorHead == null) {
            minorHead = "";
        }
        return minorHead;
    }

    public void setSubHead(String subHead) {
        this.subHead = subHead;
    }

    public String getSubHead() {
        if (subHead == null) {
            subHead = "";
        }
        return subHead;
    }

    public void setDetailHead(String detailHead) {
        this.detailHead = detailHead;
    }

    public String getDetailHead() {
        if (detailHead == null) {
            detailHead = "";
        }
        return detailHead;
    }

    public void setPlanStatus(String planStatus) {
        this.planStatus = planStatus;
    }

    public String getPlanStatus() {
        if (planStatus == null) {
            planStatus = "";
        }
        return planStatus;
    }

    public void setChargedVoted(String chargedVoted) {
        this.chargedVoted = chargedVoted;
    }

    public String getChargedVoted() {
        if (chargedVoted == null) {
            chargedVoted = "";
        }
        return chargedVoted;
    }

    public void setSectorCode(String sectorCode) {
        this.sectorCode = sectorCode;
    }

    public String getSectorCode() {
        if (sectorCode == null) {
            sectorCode = "";
        }
        return sectorCode;
    }

    public String getTokenNumber() {
        return tokenNumber;
    }

    public void setTokenNumber(String tokenNumber) {
        this.tokenNumber = tokenNumber;
    }

    public String getTokendate() {
        return tokendate;
    }

    public void setTokendate(String tokendate) {
        this.tokendate = tokendate;
    }

    public void setPrevTokenNumber(String prevTokenNumber) {
        this.prevTokenNumber = prevTokenNumber;
    }

    public String getPrevTokenNumber() {
        if (prevTokenNumber == null) {
            prevTokenNumber = "";
        }
        return prevTokenNumber;
    }

    public void setPrevTokendate(String prevTokendate) {
        this.prevTokendate = prevTokendate;
    }

    public String getPrevTokendate() {
        if (prevTokendate == null) {
            prevTokendate = "";
        }
        return prevTokendate;
    }

    public void setTreasuryCode(String treasuryCode) {
        this.treasuryCode = treasuryCode;
    }

    public String getTreasuryCode() {
        if (treasuryCode == null) {
            treasuryCode = "";
        }
        return treasuryCode;
    }

    public void setOfficename(String officename) {
        this.officename = officename;
    }

    public String getOfficename() {
        return officename;
    }

    public void setBilldesc(String billdesc) {
        this.billdesc = billdesc;
    }

    public String getBilldesc() {
        if (billdesc == null) {
            billdesc = "";
        }
        return billdesc;
    }

    public void setTotdeduction(double totdeduction) {
        this.totdeduction = totdeduction;
    }

    public double getTotdeduction() {
        return totdeduction;
    }

    public void setDdomobileno(String ddomobileno) {
        this.ddomobileno = ddomobileno;
    }

    public String getDdomobileno() {
        if (ddomobileno == null) {
            ddomobileno = "";
        }
        return ddomobileno;
    }

    public void setBeneficiaryrefno(String beneficiaryrefno) {
        this.beneficiaryrefno = beneficiaryrefno;
    }

    public String getBeneficiaryrefno() {
        return beneficiaryrefno;
    }

    public void setGrossAmount(String grossAmount) {
        this.grossAmount = grossAmount;
    }

    public String getGrossAmount() {
        return grossAmount;
    }

    public void setNetAmount(String netAmount) {
        this.netAmount = netAmount;
    }

    public String getNetAmount() {
        return netAmount;
    }

    public int getBillmonth() {
        return billmonth;
    }

    public void setBillmonth(int billmonth) {
        this.billmonth = billmonth;
    }

    public int getBillyear() {
        return billyear;
    }

    public void setBillyear(int billyear) {
        this.billyear = billyear;
    }

    public String getVchNo() {
        return vchNo;
    }

    public void setVchNo(String vchNo) {
        this.vchNo = vchNo;
    }

    public String getVchDt() {
        return vchDt;
    }

    public void setVchDt(String vchDt) {
        this.vchDt = vchDt;
    }

    public String getObjectbill() {
        return objectbill;
    }

    public void setObjectbill(String objectbill) {
        this.objectbill = objectbill;
    }

    public String getOff_code() {
        return off_code;
    }

    public void setOff_code(String off_code) {
        this.off_code = off_code;
    }

    public String getBillid() {
        return billid;
    }

    public void setBillid(String billid) {
        this.billid = billid;
    }

    public String getAq_month() {
        return aq_month;
    }

    public void setAq_month(String aq_month) {
        this.aq_month = aq_month;
    }

    public String getAq_year() {
        return aq_year;
    }

    public void setAq_year(String aq_year) {
        this.aq_year = aq_year;
    }

}
