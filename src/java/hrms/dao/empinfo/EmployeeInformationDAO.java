package hrms.dao.empinfo;

import hrms.model.empinfo.EmployeeInformation;

public interface EmployeeInformationDAO {
    
    public EmployeeInformation getEmployeeData(String empid,String gpf);
    
    public void updateEmployeeData(EmployeeInformation empinfo);
    
    public boolean isupdatePayOrPostingInfo(String empid,String wefdt,String ordDt,String type);
    
    public void updateEmpPayInfoOnDate(EmployeeInformation e,String empid);
}
