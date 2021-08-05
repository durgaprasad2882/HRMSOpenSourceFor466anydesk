/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.servicebook;

import hrms.model.servicebook.EmpServiceBook;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author lenovo pc
 */
public interface ServiceBookDAO {
    public EmpServiceBook getSHReport(String employeeCodes);
    
    public List getServiceBookAnnexureAData(String empid);
    
    public List getServiceBookAnnexureBData(String empid);
    
    public List getServiceBookAnnexureDData(String empid);
    
    public List getServiceBookAnnexureEData(String empid);
}
