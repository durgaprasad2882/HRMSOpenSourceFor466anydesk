/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.reinstatement;

import hrms.common.CommonFunctions;
import hrms.common.DataBaseFunctions;
import hrms.model.reinstatement.Reinstatement;
import hrms.model.reinstatement.ReinstatementList;
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
public class ReinstatementDAOImpl implements ReinstatementDAO{
    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
     
    
    public int insertReinstatementData(Reinstatement reinstate){
         int n = 0;
        PreparedStatement pst = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            String mcode = CommonFunctions.getMaxCode("EMP_REINSTATEMENT", "SP_ID", con);
            pst = con.prepareStatement("INSERT INTO EMP_REINSTATEMENT(SP_ID,EMP_ID,DOE,TOE,IF_ASSUMED,ORDNO,ORDDT,AUTH_DEPT,AUTH_OFF,AUTH,NOTE,IF_VISIBLE,ENT_DEPT,ENT_OFF,ENT_AUTH) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            pst.setString(1, mcode);
            pst.setString(2, reinstate.getEmpid());
            pst.setString(3, reinstate.getDoe());
            pst.setString(4, reinstate.getToe());
            pst.setString(5, reinstate.getIfAssumed());
            pst.setString(6, reinstate.getOrdno());
            pst.setString(7, reinstate.getOrdDate());
            pst.setString(8, reinstate.getSltDept());
            pst.setString(9, reinstate.getSltOffice());
            pst.setString(10, reinstate.getSltAuth());
            pst.setString(11, reinstate.getNote());
            pst.setString(12, reinstate.getIfVisible());
            pst.setString(13, reinstate.getEntDept());
            pst.setString(14, reinstate.getEntOffice());
            pst.setString(15, reinstate.getEntAuth());
            n = pst.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return n;
    }
    
    public int updateReinstatementData(Reinstatement reinstate){
        int n = 0;
        PreparedStatement pst = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("UPDATE  EMP_REINSTATEMENT SET EMP_ID=?,DOE=?,TOE=?,IF_ASSUMED=?,ORDNO=?,ORDDT=?,AUTH_DEPT=?,AUTH_OFF=?,AUTH,NOTE=?,IF_VISIBLE=?,ENT_DEPT=?,ENT_OFF=?,ENT_AUTH=? WHERE SP_ID=?");
            pst.setString(1, reinstate.getEmpid());
            pst.setString(2, reinstate.getDoe());
            pst.setString(3, reinstate.getToe());
            pst.setString(4, reinstate.getIfAssumed());
            pst.setString(5, reinstate.getOrdno());
            pst.setString(6, reinstate.getOrdDate());
            pst.setString(7, reinstate.getSltDept());
            pst.setString(8, reinstate.getSltOffice());
            pst.setString(9, reinstate.getSltAuth());
            pst.setString(10, reinstate.getNote());
            pst.setString(11, reinstate.getIfVisible());
            pst.setString(12, reinstate.getEntDept());
            pst.setString(13, reinstate.getEntOffice());
            pst.setString(14, reinstate.getEntAuth());
            pst.setString(15, reinstate.getSpId());
            n = pst.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return n; 
    }
    
    public int deleteReinstatement(String reinstateId){
        int n = 0;
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("DELETE FROM EMP_REINSTATEMENT WHERE SP_ID=?");
            pst.setString(1, reinstateId);
            pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return n;
    }
    
    public Reinstatement editReinstatement(String reinstateId){
       Reinstatement reinstate = null;
        ResultSet rs = null;
        PreparedStatement pst = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("SELECT EMP_ID,DOE,TOE,IF_ASSUMED,ORDNO,ORDDT,AUTH_DEPT,AUTH_OFF,AUTH,NOTE,IF_VISIBLE,ENT_DEPT,ENT_OFF,ENT_AUTH FROM EMP_REINSTATEMENT WHERE SP_ID=?");
            pst.setString(1, reinstateId);
            rs = pst.executeQuery();
            if (rs.next()) {
                reinstate = new Reinstatement();
               
                reinstate.setEmpid(rs.getString("EMP_ID"));
                reinstate.setDoe(rs.getString("DOE"));
                reinstate.setToe(rs.getString("TOE"));
                reinstate.setIfAssumed(rs.getString("IF_ASSUMED"));
                reinstate.setOrdno(rs.getString("ORDNO"));
                reinstate.setOrdDate(rs.getString("ORDDT"));
                reinstate.setSltDept(rs.getString("AUTH_DEPT"));
                reinstate.setSltOffice(rs.getString("AUTH_OFF"));
                reinstate.setSltAuth(rs.getString("AUTH"));
                reinstate.setNote(rs.getString("NOTE"));
                reinstate.setIfVisible(rs.getString("IF_VISIBLE"));
                reinstate.setEntDept(rs.getString("ENT_DEPT"));
                reinstate.setEntOffice(rs.getString("ENT_OFF"));
                reinstate.setEntAuth(rs.getString("ENT_AUTH"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return reinstate;   
    }
    public List findAllReinstatement(String empId){
         ReinstatementList reinstList=null;
         List list = null;
        Connection con=null;
        PreparedStatement pst=null;
        ResultSet rs=null;
        list=new ArrayList();
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("SELECT sus.emp_id, SUS.SP_ID AS SUSPID,SUS.DOE AS SDOE,SUS.ORDNO AS SORDNO,SUS.ORDDT AS SORDDT,SUS.AUTH AS SAUTH,SUS.WEFD AS SWEFD,SUS.WEFT AS SWEFT,RE.SP_ID AS RSPID,RE.DOE AS RDOE,RE.ORDNO AS RORDNO,RE.ORDDT AS RORDDT,RE.AUTH_DEPT AS RDEPT,RE.AUTH_OFF AS ROFFICE,RE.AUTH AS RAUTH,RE.SV_ID AS RSVID FROM (SELECT * FROM EMP_SUSPENSION WHERE EMP_ID=? AND HQ_FIX IS NULL OR HQ_FIX = '' OR HQ_FIX='N' ) SUS LEFT OUTER JOIN EMP_REINSTATEMENT RE ON SUS.SP_ID=RE.SP_ID");
            pst.setString(1, empId);
            rs = pst.executeQuery();
            while (rs.next()) {
                reinstList = new ReinstatementList();
                reinstList.setSusdoe(rs.getString("SDOE"));
                reinstList.setSusordno(rs.getString("SORDNO"));
                reinstList.setSusordDate(rs.getString("SORDDT"));
                reinstList.setWefd(rs.getString("SWEFD"));
                list.add(reinstList);
            }
        } catch (Exception e) {
                e.printStackTrace();
        } finally {
             DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return list;
     }
}
