/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hrms.dao.payroll.aqmast;

import hrms.model.payroll.aqmast.AqmastModel;
import java.util.List;

/**
 *
 * @author Durga
 */
public interface AqmastDAO {
    public String saveAqmastdata(AqmastModel aqmast);
    public AqmastModel getAqmastDetail(String aqslno);
    public int deleteAqmastData(String aqslno);
    public List getAqmastList(int billno);
    
    public boolean stopNpsDeduction(String empId, int billMonth, int billYear);
    
    public boolean stopGpfDeduction(String empId, int billMonth, int billYear);
    
    public boolean stopSalaryForPayHeldUp(String empId);
    
   
    
}
