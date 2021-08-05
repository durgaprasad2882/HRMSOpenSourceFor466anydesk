/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.retirement;

import hrms.model.retirement.Retirement;
import hrms.model.termination.Termination;

/**
 *
 * @author Surendra
 */
public interface RetirementDAO {
    
    public int insertRetirementData(Retirement retire);
    
    public int updateRetirementData(Retirement retire);
    
    public int deleteRetirement(String retireId);
    
    public Retirement editRetirement(String retireId);

    public int insertRetirementData(Termination terminationForm);
    
}
