package hrms.dao.incrementsanction;

import hrms.common.Message;
import hrms.model.incrementsanction.IncrementForm;
import java.util.List;

public interface IncrementSanctionDAO {

    public Message saveIncrement(IncrementForm incrementForm,String entdept,String entoff,String entauth);
    
    public Message updateIncrement(IncrementForm incrementForm,String entdept,String entoff,String entauth) throws Exception;
    
    public int getIncrementListCount(String empid);
    
    public List getIncrementList(String empid,int minlimit,int maxlimit);
    
    public IncrementForm getEmpIncRowData(String empid,String incrId);
    
    public boolean deleteIncrement(IncrementForm incrementForm);
}
