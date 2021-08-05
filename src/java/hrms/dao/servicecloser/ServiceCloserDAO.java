/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hrms.dao.servicecloser;

import hrms.model.servicecloser.EmployeeDeceased;

/**
 *
 * @author Manas
 */
public interface ServiceCloserDAO {
    public EmployeeDeceased getEmployeeDeceasedData(String empId);
    public void saveEmployeeDeceased(EmployeeDeceased employeeDeceased);
}
