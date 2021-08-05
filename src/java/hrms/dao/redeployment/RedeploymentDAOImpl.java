
package hrms.dao.redeployment;

import hrms.common.CommonFunctions;
import hrms.common.DataBaseFunctions;
import hrms.model.redeployment.RedeplomentList;


import hrms.model.redeployment.Redeployment;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;

public class RedeploymentDAOImpl implements RedeploymentDAO{
     @Resource(name = "dataSource")
    protected DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
      public int insertRedeploymentData(Redeployment redeploy){
        int n = 0;
        PreparedStatement pst = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            String mcode = CommonFunctions.getMaxCode("EMP_NOTIFICATION", "NOT_ID", con);
            pst = con.prepareStatement("INSERT INTO EMP_NOTIFICATION(NOT_ID,NOT_TYPE,EMP_ID,DOE,TOE,IF_ASSUMED,ORDNO,ORDDT,DEPT_CODE,OFF_CODE,AUTH,NOTE,IF_VISIBLE,ENT_DEPT,ENT_OFF,ENT_AUTH,ACS,ASCS) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            pst.setString(1, mcode);
            pst.setString(2, "REDEPLOYMENT");
            pst.setString(3, redeploy.getEmpid());
            pst.setString(4, redeploy.getDoe());
            pst.setString(5, redeploy.getToe());
            pst.setString(6, redeploy.getIfAssumed());
            pst.setString(7, redeploy.getOrdno());
            pst.setString(8, redeploy.getOrdDate());
            pst.setString(9, redeploy.getSltDept());
            pst.setString(10, redeploy.getSltOffice());
            pst.setString(11, redeploy.getSltAuth());
            pst.setString(12, redeploy.getNote());
            pst.setString(13, redeploy.getIfVisible());
            pst.setString(14, redeploy.getEntDept());
            pst.setString(15, redeploy.getEntOffice());
            pst.setString(16, redeploy.getEntAuth());
            pst.setString(17, redeploy.getCadreStatus());
            pst.setString(18, redeploy.getSubStatus());
            n = pst.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return n; 
      }
    
    public int updateRedeploymentData(Redeployment redeploy){
        int n = 0;
        PreparedStatement pst = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("UPDATE  EMP_NOTIFICATION SET NOT_TYPE=?,EMP_ID=?,DOE=?,TOE=?,IF_ASSUMED=?,ORDNO=?,ORDDT=?,DEPT_CODE=?,OFF_CODE=?,AUTH,NOTE=?,IF_VISIBLE=?,ENT_DEPT=?,ENT_OFF=?,ENT_AUTH=?,ACS=?,ASCS=? WHERE NOT_ID=?");
            pst.setString(1, "REDEPLOYMENT");
            pst.setString(2, redeploy.getEmpid());
            pst.setString(3, redeploy.getDoe());
            pst.setString(4, redeploy.getToe());
            pst.setString(5, redeploy.getIfAssumed());
            pst.setString(6, redeploy.getOrdno());
            pst.setString(7, redeploy.getOrdDate());
            pst.setString(8, redeploy.getSltDept());
            pst.setString(9, redeploy.getSltOffice());
            pst.setString(10, redeploy.getSltAuth());
            pst.setString(11, redeploy.getNote());
            pst.setString(12, redeploy.getIfVisible());
            pst.setString(13, redeploy.getEntDept());
            pst.setString(14, redeploy.getEntOffice());
            pst.setString(15, redeploy.getEntAuth());
            pst.setString(16, redeploy.getCadreStatus());
            pst.setString(17, redeploy.getSubStatus());
            pst.setString(18, redeploy.getNotId());
            n = pst.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return n;
    }
    
    public int deleteRedeployment(String redeploymentId){
         int n = 0;
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("DELETE FROM EMP_NOTIFICATION WHERE NOT_ID=?");
            pst.setString(1, redeploymentId);
            pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return n;
    }
    
    public Redeployment editRedeployment(String redeploymentId){
         Redeployment redep = null;
        ResultSet rs = null;
        PreparedStatement pst = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("SELECT NOT_TYPE,EMP_ID,DOE,TOE,IF_ASSUMED,ORDNO,ORDDT,DEPT_CODE,OFF_CODE,AUTH,NOTE,IF_VISIBLE,ENT_DEPT,ENT_OFF,ENT_AUTH,ACS,ASCS FROM EMP_NOTIFICATION WHERE NOT_ID=?");
            pst.setString(1, redeploymentId);
            rs = pst.executeQuery();
            if (rs.next()) {
                redep = new Redeployment();
                redep.setNotType("REDEPLOYMENT");
                redep.setEmpid(rs.getString("EMP_ID"));
                redep.setDoe(rs.getString("DOE"));
                redep.setToe(rs.getString("TOE"));
                redep.setIfAssumed(rs.getString("IF_ASSUMED"));
                redep.setOrdno(rs.getString("ORDNO"));
                redep.setOrdDate(rs.getString("ORDDT"));
                redep.setSltDept(rs.getString("DEPT_CODE"));
                redep.setSltOffice(rs.getString("OFF_CODE"));
                redep.setSltAuth(rs.getString("AUTH"));
                redep.setNote(rs.getString("NOTE"));
                redep.setIfVisible(rs.getString("IF_VISIBLE"));
                redep.setEntDept(rs.getString("ENT_DEPT"));
                redep.setEntOffice(rs.getString("ENT_OFF"));
                redep.setEntAuth(rs.getString("ENT_AUTH"));
                redep.setCadreStatus(rs.getString("ACS"));
                redep.setSubStatus(rs.getString("ASCS"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return redep;
    }
    
    public List findAllRedeployment(String empId){
         List list = null;
        Connection con=null;
        PreparedStatement pst=null;
        ResultSet rs=null;
        RedeplomentList redepList=null;
        list=new ArrayList();
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("SELECT NOT_ID,DOE,ORDNO,ORDDT FROM EMP_NOTIFICATION WHERE EMP_ID=? AND NOT_TYPE='REDEPLOYMENT'");
            pst.setString(1, empId);
            rs = pst.executeQuery();
            while (rs.next()) {
                redepList = new RedeplomentList();
                redepList.setNotId(rs.getString("NOT_ID"));
                redepList.setNotType("ABSORPTION");
                redepList.setDoe(rs.getString("DOE"));
                redepList.setOrdno(rs.getString("ORDNO"));
                redepList.setOrdDate(rs.getString("ORDDT"));
                list.add(redepList);
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
