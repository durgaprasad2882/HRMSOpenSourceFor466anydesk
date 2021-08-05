package hrms.dao.payroll.payslip;

import com.itextpdf.text.Document;
import hrms.model.payroll.payslip.ADDetails;
import hrms.model.payroll.payslip.PaySlipDetailBean;
import java.util.ArrayList;
import java.util.List;

public interface PaySlipDAO {

    public List getPaySlip(String billNo, String empid, int year, int month);

    public String getAQSLNo(String empid, int year, int month);

    public PaySlipDetailBean getEmployeeData(String aqslno, int year, int month);

    //public List getAllowanceDeductionList(String aqslno, String adType, int year, int month);

    public List getPrivateDedeuctionList(String aqslno, int year, int month);

    public List getLoanList(String aqslno, int year, int month);

    public void getPaySlipPDF(Document document, PaySlipDetailBean pbeandetail, ArrayList allownacelist, ArrayList deductionlist, ArrayList privatedeductionlist, ArrayList loanlist);

    public String getTokenGeneratedBillNo(String empid, int year, int month);
    
    public ADDetails[] getAllowanceDeductionList(String aqslno, String adType, int year, int month);
}
