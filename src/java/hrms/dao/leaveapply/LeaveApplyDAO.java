package hrms.dao.leaveapply;

import com.itextpdf.text.Document;
import hrms.model.leave.Leave;
import hrms.model.leave.LeaveBalanceBean;
import hrms.model.leave.LeaveEntrytakenBean;
import hrms.model.leave.LeaveSancBean;
import hrms.model.leave.LeaveWsBean;
import hrms.model.parmast.ParDetail;
import hrms.model.leave.LeaveApply;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public interface LeaveApplyDAO {

    public List getLeaveApplyList(String empId) ;

    public String getLeaveType(String tolId);

    public String getAuthOffice(String authSpc) ;

    public boolean saveLeave(LeaveApply leaveForm);

    public String getNameWithPost(String leaveAuth);
   
    public Leave getLeaveData(String leaveId, String loginEmpId, String loggedinSpc) ;

    public ArrayList getFileName(String taskId,String attFlag);

    public String getAuthorityEmpCode(String taskId) ;

    public void updateTaskList(Leave leaveForm);

    public String getApplicant(String taskId);

    public ArrayList getLeaveWorkFlowDtls(String taskId);

    public String getStatusId(String taskId);
  //  public LeaveBalanceBean getLeaveBalanceInfo(String empId)throws Exception;
     public String getLeaveBalanceInfo(String empCode, String tolId, String year);
    public boolean getIfLeaveRecordExist(String empid,String leaveType,String fromDate,String toDate);
    public boolean ifEmpExist(String empid);
    public boolean ifMaxSurviveChild(String tolId,String empCode);
    public void getLeaveOpeningBalance(String empid,String tolId,String currDate);
    public void updateEmpleave(Leave leaveForm,String notId);
   // public void updateLeaveBalance(String empCode, String tolId, int noOfDays);
    public void updateClLeaveBalance(String empCode, String tolId, int noOfDays);
    public void updateElLeaveBalance(String empCode, String tolId, int noOfDays);
    public void updateHplLeaveBalance(String empCode, String tolId, int noOfDays);
    public void updateCommutedLeaveBalance(String empCode, String tolId, int noOfDays);
    public void updateMaternityLeaveBalance(String empCode, String tolId, int noOfDays);
     public void updatePaternityLeaveBalance(String empCode, String tolId, int noOfDays);      
    public void updateLeaveOrder(Leave leaveForm);
    public int calculateDateDiff(String fromdate,String toDate,String empId,String tolid);
    public void updateApproveDate(Leave leave);
    //public boolean getmaxLeaveAvailable(String leaveType,String periodFrom,String periodTo);
     public void viewPDFfunc(Document document,Leave leaveForm,String empid);
     public void viewAllowedPDFfunc(Document document,Leave leaveForm,String empid);
     public LeaveEntrytakenBean getEntryTaken(String empId) ;
     public LeaveSancBean getLeaveSancInfo(String taskId) ;
     public boolean maxPeriodCount(LeaveApply leaveForm);
     public int maxLeavePeriodCount(String tolId);
     public LeaveWsBean[] getEmployeeLeave(String empid, String inputdate);
     public void saveJoinData(Leave leaveForm);
    
    

}
