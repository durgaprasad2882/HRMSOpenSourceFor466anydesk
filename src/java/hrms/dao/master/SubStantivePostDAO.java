/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.master;

import hrms.model.master.SubstantivePost;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Durga
 */
public interface SubStantivePostDAO {

    public List getSPCList(String offcode);

    public List getEmployeeWithSPCList(String offcode);

    public List getGenericPostList(String offcode);

    public List getGPCWiseSPCList(String empid, String offcode, String gpc);

    public ArrayList getCadreWiseOfficeWiseSPC(String cadreCode, String offCode);

    public List getPostListPaging(String offcode, String postToSearch, int page, int rows);

    public int getPostListCountPaging(String offcode, String postToSearch);

    public List getGPCWiseEmployeeList(String postcode, String offcode);

    public List getOfficeWithSPCList(String offcode);

    public List getCadreWisePostList(String offcode, String cadrecode);

    public List getSanctioningSPCOfficeWiseList(String offcode);

    public SubstantivePost getSpcDetail(String spc);

    public List getGPCWiseEmployeeListOnlySPC(String postcode);

    public List getPostCodeOfficeWise(String offCode);

    public List getSPCListWithEmployeeName(String offCode, String postCode);

    public int updateSubstantivePost(String offCode, String gpc, String spc, String payscale, String payscale_7th, String postgrp, String paylevel, String gp, String cadre,String chkGrantInAid,String teachingPost,String planOrNonPlan);

    public int removeSubstantivePost(String offCode, String gpc, String spc);

    public int addSubstantivePost(String deptCode, String offCode, SubstantivePost substantivePost);

    public List listSubPost(SubstantivePost substantivePost);

    public int savePostdataDetails(SubstantivePost substantivePost);
}
