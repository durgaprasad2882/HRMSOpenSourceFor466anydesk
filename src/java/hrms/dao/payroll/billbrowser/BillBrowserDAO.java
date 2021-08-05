/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.payroll.billbrowser;

import hrms.model.billvouchingTreasury.BillDetail;
import hrms.model.payroll.billbrowser.BillAttr;
import hrms.model.payroll.billbrowser.BillBrowserbean;
import hrms.model.payroll.billbrowser.GetBillStatusBean;
import hrms.model.payroll.billbrowser.GlobalBillStatus;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Manas Jena
 */
public interface BillBrowserDAO {
    
    public BillBrowserbean getArrearBillPeriod(String offcode, int prepareMonth, int preparedYear);
    
    public ArrayList getBillPrepareYear(String offCode);

    public ArrayList getMajorHeadListTreasuryWise(String trcode, int aqyear, int aqmonth);

    public ArrayList getVoucherListTreasuryWise(String trcode, int aqyear, int aqmonth, String majorhead);

    public ArrayList getMonthFromSelectedYear(String offcode, int year);

    public ArrayList getPayBillList(int year, int month, String offCode, String billType, String spc);

    public BillDetail getBillDetails(int billno);
    
    public List getBillDetails(String offcode, int month, int year);

    public ArrayList getOBJXMLData(int billId, String treasuryCode, double basicPay, String billdate, String typeofbill);
    
    public ArrayList getBTXMLData(int billId,String treasuryCode, String billdate, String typeofbill);
    
    public ArrayList getNPSXMLData(int billId,  String billdate, int monthasNumber, int year, String typeofBill);

    public int getbillsubmissionCount(int billno);

    public void updateBillStatus(int billno, int billStatusId);

    public void updateBillHistory(int billno, String submissionDate);

    public ArrayList getPayBillList(int year, int month, String treasuryCode);

    public int getNewBillYear(String offCode);

    public int getNewBillMonth(String offCode, int year);

    public GlobalBillStatus getBillProcessStatus();

    public String getMonthName(int month);
    
    public void updateBillChartofAccount(int billno, BillBrowserbean bean);

    public ArrayList getBillGroupList(String offCode, String curSpc);

    public BillAttr[] createBillFromBillGroup(int month, int year, String[] billGroupId, String processDate, int priority, String billType, String moffcode);
    
    public BillAttr[] createBillFromBillGroupForArrear(int fromMonth, int fromYear, int toMonth, int toYear, String[] billGroupId, String processDate, int priority, String billType, String moffcode);

    public int getBillPriority(String offCode);

    public ArrayList getAllowanceList(int billno);

    public ArrayList getDeductionList(int billno);

    public ArrayList getPvtLoanList(int billno);

    public void reprocessSingleBill(BillBrowserbean bbbean);
    
    public void updateBillData(BillBrowserbean bbbean);
    
    public String getBillChartofAccount(int billno);
    
    public boolean verifyBenificiaryNumber(String benificiaryNumber,int year,int month);
    
    public String verifyBillNoandBenRefNo(String offCode,  String billId, String benrefNumber, int year, int month);
    
    public GetBillStatusBean getUploadBillStatus(int billId);
    
    public void changeBillStatus(int billId, int statusId);  

    
    public List getBillData(BillDetail billDetail);
    
    public void unlockBill(BillDetail billDetail);
    
    public BillDetail unlockBillToResubmit(BillDetail billDetail);
    
    public void objectBill(BillDetail billDetail); 
    
    public ArrayList getVoucherListForAG(int year, int month, String parentTrCode);
    
    public ArrayList getAquitanceVoucherListForAG(int year, int month, String parentTrCode);
    
    public ArrayList getScheduleVoucherListForAG(int year, int month, String parentTrCode);
}
    

