/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.payfixation;

import hrms.model.payfixation.PayFixation;
import java.util.List;

/**
 *
 * @author Surendra
 */
public interface PayFixationDAO {
    
    public int insertPayFixationData(PayFixation payfix);
    
    public int updatePayFixationData(PayFixation payfix);
    
    public int deletePayFixation(String payfixId);
    
    public PayFixation editPayFixation(String payfixId);
    
    public List findAllPayFixation(String empId);
}
