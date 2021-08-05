package hrms.dao.emppayrecord;

import hrms.model.emppayrecord.EmpPayRecordForm;

public interface EmpPayRecordDAO {
    
    public void saveEmpPayRecordData(EmpPayRecordForm eprform);
    
    public void updateEmpPayRecordData(EmpPayRecordForm eprform);
    
    public void deleteEmpPayRecordData(EmpPayRecordForm eprform);
}
