/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hrms.dao.payroll.billmast;

import hrms.model.payroll.billmast.BillMastModel;
import java.util.List;

/**
 *
 * @author Durga
 */
public interface BillMastDAO {
    public int saveBillMast(BillMastModel model);    
    public BillMastModel getBillMastDetails(int billid);
    public List getBillList(int year,int month,String offcode,String billType,String spc);    
    public int deleteBill(int billid);
    public int getBillStatus(int billid);
    public void updateBillStatus(int billid, int billStatusId);
    public void markBillAsPrepared(int billid);
    public void updateBillTotaling(int billid);
}
