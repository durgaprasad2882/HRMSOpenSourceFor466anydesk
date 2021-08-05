/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.lic;
import java.util.List;
import hrms.model.lic.Lic;
import java.math.BigDecimal;
/**
 *
 * @author Manas Jena
 */
public interface LicDAO {
    // For Page Load and Cancel 
    public List getLicList(String empId);
    
  // For Edit
    public Lic editLicData(String empId, BigDecimal elId);
    
  // For Save
    public int saveLicData(Lic llicBean);
  
  // For Delete
    public boolean deleteLicData(String elId);
}
