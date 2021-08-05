/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.misreport;

import hrms.model.employee.Employee;
import java.util.ArrayList;

/**
 *
 * @author Manas Jena
 */
public interface CadreEmployeeReportDAO {
    public ArrayList getEmployeeList(String cadreCode);
    public Employee getEmployeeData(String empid);
    public void saveEmployeeData(Employee employee);
    public ArrayList getIncumbancyChart(String spc);
    public ArrayList getEmployeeIncumbancyChart(String empid);
}
