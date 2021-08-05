/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.repatrition;

import hrms.model.repatrition.Repatrition;
import java.util.List;

/**
 *
 * @author Surendra
 */
public interface RepatritionDAO {
    
    public int insertRepatritionData(Repatrition repat);
    
    public int updateRepatritionData(Repatrition repat);
    
    public int deleteRepatrition(String repatritionId);
    
    public Repatrition editRepatrition(String repatritionId);
    
    public List findAllRepatrition(String empId);
    
}
