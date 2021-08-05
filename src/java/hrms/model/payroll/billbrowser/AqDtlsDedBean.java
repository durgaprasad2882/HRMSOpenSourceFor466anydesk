/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.model.payroll.billbrowser;

/**
 *
 * @author lenovo pc
 */
public class AqDtlsDedBean {
    private String adCode=null;
    private String dedInstalNo=null;
    private String dedAmt=null;
    private String nowDedn=null;
    private String billNo=null;
    private String aqslNo=null;
    private String adType=null;

    public String getAdCode() {
        return adCode;
    }

    public void setAdCode(String adCode) {
        this.adCode = adCode;
    }

    public String getAdType() {
        return adType;
    }

    public void setAdType(String adType) {
        this.adType = adType;
    }
    
    
    
    public String getNowDedn() {
        return nowDedn;
    }

    public void setNowDedn(String nowDedn) {
        this.nowDedn = nowDedn;
    }

    public String getAqslNo() {
        return aqslNo;
    }

    public void setAqslNo(String aqslNo) {
        this.aqslNo = aqslNo;
    }
    

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }
    

   

    public String getDedInstalNo() {
        return dedInstalNo;
    }

    public void setDedInstalNo(String dedInstalNo) {
        this.dedInstalNo = dedInstalNo;
    }

    public String getDedAmt() {
        return dedAmt;
    }

    public void setDedAmt(String dedAmt) {
        this.dedAmt = dedAmt;
    }
    
    
}
