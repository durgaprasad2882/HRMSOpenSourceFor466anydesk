/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.brassallot;

import hrms.model.brassallot.BrassAllot;
import java.util.List;

/**
 *
 * @author Surendra
 */
public interface BrassAllotDAO {
    
    public int insertBrassAllotData(BrassAllot brass);
    
    public int updateBrassAllotData(BrassAllot brass);
    
    public int deleteBrassAllotData(String brassId);
    
    public BrassAllot editBrassAllotData(String brassId);
    
    public List findAllBrassAllot(String empId);
    
}
