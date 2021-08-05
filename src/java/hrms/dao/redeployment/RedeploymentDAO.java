
package hrms.dao.redeployment;

import hrms.model.redeployment.Redeployment;
import java.util.List;
public interface RedeploymentDAO {
    
    public int insertRedeploymentData(Redeployment redeploy);
    
    public int updateRedeploymentData(Redeployment redeploy);
    
    public int deleteRedeployment(String redeploymentId);
    
    public Redeployment editRedeployment(String redeploymentId);
    
    public List findAllRedeployment(String empId);
}
