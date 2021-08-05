package hrms.dao.tpfreport;

import hrms.model.tpfreport.TPFReportBean;
import java.util.List;

public interface TPFReportsDAO {
    
    public List getDDOList(String trCode);
    
    public List getBillAmt(String ddocode,int month,int year);
    
    public List getTPFEmployeeList(int year,int month,String trCode);
    
    public List getYearList();
    
    public List getPaymentList(String billNo);
    
    public TPFReportBean getBillDtls(String billNo);
}
