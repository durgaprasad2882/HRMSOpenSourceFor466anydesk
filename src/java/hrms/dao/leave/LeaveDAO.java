/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.leave;

import hrms.model.leave.Leave;
import java.util.List;

/**
 *
 * @author Surendra
 */
public interface LeaveDAO {
    
    public int addLeaveData(Leave leave);
    
    public int updateLeaveData(Leave leave);
    
    public int deleteLeaveData(int leaveId);
    
    public Leave editLeaveData(int leaveId);
    
    public List getAbsenteeList(String empId, String leaveType);
    
}
