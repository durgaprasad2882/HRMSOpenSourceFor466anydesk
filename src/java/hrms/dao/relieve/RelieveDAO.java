package hrms.dao.relieve;

import hrms.common.Message;
import hrms.model.relieve.RelieveForm;
import java.util.List;

public interface RelieveDAO {
    
    public List getRelieveList(String empid);
    
    public int getRelieveListCount(String empid);
    
    public RelieveForm getRelieveData(String empid,String notid,String rlvid);
    
    public List getRelievedPostList(String empid);
    
    public List getAddlChargeList(String empid);
    
    public void saveRelieve(RelieveForm erelieve,String entDeptCode,String entOffCode,String entSpc);
    
    public void deleteRelieve(String empid,RelieveForm erelieve);
}
