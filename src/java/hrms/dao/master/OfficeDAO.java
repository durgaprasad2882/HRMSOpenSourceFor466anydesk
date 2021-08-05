/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.master;

import hrms.model.master.Office;
import java.util.List;

/**
 *
 * @author Durga
 */
public interface OfficeDAO {

    public List getOfficeList(String deptcode, String offSearch, int page, int rows);

    public List getTotalOfficeList(String deptcode);

    public int getOfficeListCount(String deptcode, String offSearch);

    public List getFieldOffList(String offCode);

    public List getDistrictWiseOfficeList(String distCode);

    public Office getOfficeDetails(String offCode);

    public boolean saveOfficeDetails(Office office);

    public boolean updateOfficeDetails(Office office);

    public List getOfficeListFilter(String offcode);
}
