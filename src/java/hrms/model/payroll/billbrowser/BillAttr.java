/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.model.payroll.billbrowser;

/**
 *
 * @author Manas
 */
public class BillAttr {

    private String billgroupId = "";
    private String billDesc = "";
    private String chartofAcc = "";
    private String msg = null;

    public void setBillgroupId(String billgrpId) {
        this.billgroupId = billgrpId;
    }

    public String getBillgroupId() {
        return billgroupId;
    }

    public void setBillDesc(String billDesc) {
        this.billDesc = billDesc;
    }

    public String getBillDesc() {
        return billDesc;
    }

    public void setChartofAcc(String chartofAcc) {
        this.chartofAcc = chartofAcc;
    }

    public String getChartofAcc() {
        return chartofAcc;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
    
}
