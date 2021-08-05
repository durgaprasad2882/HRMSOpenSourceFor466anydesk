/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hrms.dao.task;

import java.util.List;

/**
 *
 * @author Surendra
 */
public interface TaskDAO {
	
    public List getMyTaskList(String empid,String loggedinSpc, String parstatus,int page,int rows,String empname,String gpfno,int process);
    
    public int getTaskListTotalCnt(String empId,String parstatus);
 
    public String getDispatcherString(int taskid);
	
	public List getProcessList();
        public List getMyTaskList(String empid);
        
        public String getLoggedInEmpNameForCompletedTask(String empid);
}
