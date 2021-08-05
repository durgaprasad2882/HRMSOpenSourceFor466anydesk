/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.payroll.billbrowser;

import hrms.model.payroll.billbrowser.BillGroup;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 *
 * @author Manas Jena
 */
public interface BillGroupDAO {
    public ArrayList getBillGroupList(String offcode);
    public ArrayList getActiveBillGroupList(String offcode);
    public ArrayList getPlanStatusList();
    public ArrayList getSectorList();
    public ArrayList getPostClassList();
    public ArrayList getBillTypeList();
    public ArrayList getDemandList();
    public ArrayList getMajorHeadList(String demandno);
    public ArrayList getSubMajorHeadList(String demandNo,String majorhead);
    public ArrayList getMinorHeadList(String majorHead,String submajorhead);
    public ArrayList getSubMinorHeadList(String subMajorHead,String minorhead);
    public ArrayList getDetailHeadList(String minorhead,String subminorhead);
    public ArrayList getChargedVotedList(String detailhead,String subminorhead);
    public BillGroup getBillGroupDetails(BigDecimal billGroupId);
    public ArrayList getSectionList(String billGroupId);
    public String getConfigurationLvl(BigDecimal billGroupId);
    public void saveGroupSection(String offCode, BillGroup BG);
    public int deleteGroupData(String ocode,String groupId);
    public BillGroup editGroupList(String ocode,String groupId);
    public void updateGroupSection(String offCode, BillGroup bg);


}
