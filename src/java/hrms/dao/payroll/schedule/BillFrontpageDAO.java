/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.payroll.schedule;

import java.util.ArrayList;

/**
 *
 * @author Surendra
 */
public interface BillFrontpageDAO {
    
    public String getDPHead(String billno,int aqMonth, int aqYear);
    
    public ArrayList getScheduleListWithADCode(String billNo,int aqMonth, int aqYear);
    
}
