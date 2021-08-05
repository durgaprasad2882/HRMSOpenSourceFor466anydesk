/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hrms.dao.master;

import hrms.model.trainingadmin.InstituteForm;
import java.util.List;

/**
 *
 * @author Manoj PC
 */
public interface InstituteDAO {
    public List getInstituteList(String empId, int page, int rows);
    public void updateInstitute(InstituteForm instForm, String empId);

    public void saveInstitute(InstituteForm instForm, String empId);    
}
