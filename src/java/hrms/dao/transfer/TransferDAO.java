package hrms.dao.transfer;

import hrms.common.Message;
import hrms.model.login.LoginUserBean;
import hrms.model.transfer.TransferForm;
import java.sql.SQLException;
import java.util.List;

public interface TransferDAO {
    
    public void saveTransfer(TransferForm transferForm,String notid);
    
    public void updateTransfer(TransferForm transferForm);
    
    public void deleteTransfer(TransferForm transferForm);
    
    public int getTransferListCount(String empid);
    
    public List getTransferList(String empid);
    
    public TransferForm getEmpTransferData(TransferForm trform,String notificationId) throws SQLException;
    
    public List getPostList(String deptCode,String offCode);
    
    public int getPostListCount(String deptCode,String offCode);
    
    public String getCadreCode(String empid);
    
    public LoginUserBean[] getTransferListOfficeWise(String offcode,int year,int month);
}
