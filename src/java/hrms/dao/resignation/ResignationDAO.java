/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.resignation;

import hrms.model.resignation.Resignation;

/**
 *
 * @author Surendra
 */
public interface ResignationDAO {
    
    public int insertResignationtData(Resignation resig);
    
    public int updateResignationData(Resignation resig);
    
    public int deleteResignation(String resigId);
    
    public Resignation editResignation(String resigId);
}
