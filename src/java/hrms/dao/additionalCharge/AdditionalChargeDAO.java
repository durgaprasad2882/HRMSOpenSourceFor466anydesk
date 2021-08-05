/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.additionalCharge;


import hrms.model.additionalCharge.AdditionalCharge;
import java.util.List;

/**
 *
 * @author Surendra
 */
public interface AdditionalChargeDAO {

    //public static List findAllAdditionalCharge(String empid, int minlimit, int maxlimit);
    
    public int insertAdditionalChargeData(AdditionalCharge addchage);
    
    public int updateAdditionalChargeData(AdditionalCharge addchage);
    
    public int deleteAdditionalCharge(String addchageId);
    
    public AdditionalCharge editAdditionalCharge(String addchageId);
    
    public List findAllAdditionalCharge(String empId);
    
}
