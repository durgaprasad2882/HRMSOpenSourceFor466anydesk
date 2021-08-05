package hrms.dao.deputation;

import hrms.common.Message;
import hrms.model.deputation.DeputationDataForm;
import java.util.List;

public interface DeputationDAO {
    
    public List getCadreStatusList(String type);
    
    public List getSubCadreStatusList(String cadrestat);
    
    public Message saveDeputation(DeputationDataForm deputationForm,String notid);
    
    public Message updateDeputation(DeputationDataForm deputationForm);
    
    public List getDeputationList(String empid, int minlimit, int maxlimit);
    
    public int getDeputationListCount(String empid);
    
    public DeputationDataForm getEmpDeputationData(String empid, String notId);
}
