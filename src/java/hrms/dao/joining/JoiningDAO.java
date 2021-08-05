package hrms.dao.joining;

import hrms.common.Message;
import hrms.model.joining.JoiningForm;
import java.util.List;

public interface JoiningDAO {

    public List getJoiningList(String empid);

    public int getJoiningListCount(String empid);

    public JoiningForm getJoiningData(String empid, String notid, String rlvid,String leaveid,String addl,String jid);

    public void saveJoining(JoiningForm jform,String entDeptCode,String entOffCode,String entSpc);
    
    public void deleteJoining(JoiningForm jform);
    
}
