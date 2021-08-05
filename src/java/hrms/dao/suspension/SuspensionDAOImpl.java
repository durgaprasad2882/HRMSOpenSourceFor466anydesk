/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.suspension;

import hrms.common.CommonFunctions;
import hrms.common.DataBaseFunctions;
import hrms.model.suspension.Suspension;
import hrms.model.suspension.SuspensionList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 *
 * @author lenovo pc
 */
public class SuspensionDAOImpl implements SuspensionDAO{
     @Resource(name = "dataSource")
    protected DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
     public List findAllSuspension(String empId){
         List list = null;
        Connection con=null;
        PreparedStatement pst=null;
        ResultSet rs=null;
        SuspensionList susList=null;
        list=new ArrayList();
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("SELECT sp_id, emp_id, doe,ordno, orddt,wefd FROM emp_suspension WHERE emp_id=? and and HQ_FIX IS NULL");
            pst.setString(1, empId);
            rs = pst.executeQuery();
            while (rs.next()) {
                susList = new SuspensionList();
                susList.setSpId(rs.getString("sp_id"));
                susList.setDoe(rs.getString("DOE"));
                susList.setOrdno(rs.getString("ORDNO"));
                susList.setOrdDate(rs.getString("ORDDT"));
                susList.setWefdate(rs.getString("wefd"));
                list.add(susList);
            }
        } catch (Exception e) {
                e.printStackTrace();
        } finally {
             DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return list;
     }
    
    public int insertSuspensionData(Suspension suspend){
        int n = 0;
        PreparedStatement pst = null;
        Connection con = null;
        String susid="";
        try {
            con = dataSource.getConnection();
            susid = CommonFunctions.getMaxCode("emp_suspension","sp_id",con);
            pst = con.prepareStatement("insert into emp_suspension (sp_id, emp_id, doe, TOE, IF_ASSUMED, ordno, orddt, auth_dept, auth_off, auth, wefd, weft, SUSP_ALLOW, HQ_OFF_CODE, note, IF_VISIBLE,ent_dept,ent_off,ent_auth,HQ_FIX,ACS) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
             pst.setString(1, susid);
            pst.setString(2, suspend.getEmpid());
            pst.setString(3, suspend.getDoe());
            pst.setString(4, suspend.getToe());
            pst.setString(5, suspend.getIfAssumed());
            pst.setString(6, suspend.getOrdno());
            pst.setString(7, suspend.getOrdDate());
            pst.setString(8, suspend.getSltDept());
            pst.setString(9, suspend.getSltOffice());
            pst.setString(10,suspend.getSltAuth());
            pst.setString(11,suspend.getWefdate());
            pst.setString(12,suspend.getSltweftime());
            pst.setString(13,suspend.getTxtallowance());
            pst.setString(14,suspend.getSlthqoffice());
            pst.setString(15,suspend.getNote());
            pst.setString(16, suspend.getIfVisible());
            pst.setString(17, suspend.getEntDept());
            pst.setString(18, suspend.getEntOffice());
            pst.setString(19, suspend.getEntAuth());
            pst.setString(20,null);
            pst.setString(21, suspend.getCadreStatus()); 
              }catch(Exception e){
            e.printStackTrace();
        }
         return n; 
    }
    
    public int updateSuspensionData(Suspension suspend){
        int n = 0;
        PreparedStatement pst = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("UPDATE  emp_suspension SET emp_id=?, doe=?, TOE=?, IF_ASSUMED=?, ordno=?, orddt=?, auth_dept=?, auth_off=?, auth=?, wefd=?, weft=?, SUSP_ALLOW=?, HQ_OFF_CODE=?, note=?, IF_VISIBLE=?,ent_dept=?,ent_off=?,ent_auth=?,HQ_FIX=?,ACS=? WHERE sp_id=?");
            pst.setString(1, suspend.getEmpid());
            pst.setString(2, suspend.getDoe());
            pst.setString(3, suspend.getToe());
            pst.setString(4, suspend.getIfAssumed());
            pst.setString(5, suspend.getOrdno());
            pst.setString(6, suspend.getOrdDate());
            pst.setString(7, suspend.getSltDept());
            pst.setString(8, suspend.getSltOffice());
            pst.setString(9,suspend.getSltAuth());
            pst.setString(10,suspend.getWefdate());
            pst.setString(11,suspend.getSltweftime());
            pst.setString(12,suspend.getTxtallowance());
            pst.setString(13,suspend.getSlthqoffice());
            pst.setString(14,suspend.getNote());
            pst.setString(15, suspend.getIfVisible());
            pst.setString(16, suspend.getEntDept());
            pst.setString(17, suspend.getEntOffice());
            pst.setString(18, suspend.getEntAuth());
            pst.setString(19,null);
            pst.setString(20, suspend.getCadreStatus());
            n = pst.executeUpdate();
        }catch(Exception e){
            e.printStackTrace();
        }
         return n; 
    }
    
    public int deleteSuspension(String suspendId){
         int n = 0;
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = dataSource.getConnection();
            pst=con.prepareStatement("DELETE FROM EMP_SUSPENSION WHERE SP_ID =?");
            pst.setString(1, suspendId);
            pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
             DataBaseFunctions.closeSqlObjects(con);
        }
        return n; 
    }
    
    public Suspension editSuspension(String suspendId){
        Suspension suspend = null;
        ResultSet rs = null;
        PreparedStatement pst = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("SELECT emp_id, doe, TOE, IF_ASSUMED, ordno, orddt, auth_dept, auth_off, auth, wefd, weft, SUSP_ALLOW, HQ_OFF_CODE, note, IF_VISIBLE,ent_dept,ent_off,ent_auth,HQ_FIX,ACS FROM emp_suspension WHERE sp_id=?");
            pst.setString(1, suspendId);
            rs = pst.executeQuery();
            if (rs.next()) {
                suspend = new Suspension();
                suspend.setEmpid(rs.getString("EMP_ID"));
                suspend.setDoe(rs.getString("DOE"));
                suspend.setToe(rs.getString("TOE"));
                suspend.setIfAssumed(rs.getString("IF_ASSUMED"));
                suspend.setOrdno(rs.getString("ORDNO"));
                suspend.setOrdDate(rs.getString("ORDDT"));
                suspend.setSltDept(rs.getString("DEPT_CODE"));
                suspend.setSltOffice(rs.getString("OFF_CODE"));
                suspend.setSltAuth(rs.getString("AUTH"));
                suspend.setNote(rs.getString("NOTE"));
                suspend.setIfVisible(rs.getString("IF_VISIBLE"));
                suspend.setEntDept(rs.getString("ENT_DEPT"));
                suspend.setEntOffice(rs.getString("ENT_OFF"));
                suspend.setEntAuth(rs.getString("ENT_AUTH"));
                suspend.setCadreStatus(rs.getString("ACS"));
            }
                 } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return suspend; 
    }
    
}
