/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.tab;

import hrms.model.login.UserExpertise;
import hrms.model.tab.OfficeTreeAttr;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Surendra
 */
public interface TabDAO {

    public List getOfficeListXML(String empid);

    public ArrayList getSubOfficeListXML(String empid);

    public String saveExpertise(UserExpertise ue);

    public OfficeTreeAttr getHODOfficeListXML(String parentOfficeCode);

    
}
