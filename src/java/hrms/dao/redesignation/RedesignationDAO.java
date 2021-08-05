/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.redesignation;

import hrms.model.redesignation.Redesignation;
import java.util.List;

/**
 *
 * @author Surendra
 */
public interface RedesignationDAO {
    
    public int insertRedesignationData(Redesignation redesig);
    
    public int updateRedesignationData(Redesignation redesig);
    
    public int deleteRedesignation(String redesignationId);
    
    public Redesignation editRedesignation(String redesignationId);
    
    public List findAllRedesignation(String empId);
}
