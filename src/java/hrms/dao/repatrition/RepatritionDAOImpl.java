
package hrms.dao.repatrition;

import hrms.common.CommonFunctions;
import hrms.common.DataBaseFunctions;


import hrms.model.repatrition.Repatrition;
import hrms.model.repatrition.RepatritionList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;

public class RepatritionDAOImpl implements RepatritionDAO{
     @Resource(name = "dataSource")
    protected DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
      public int insertRepatritionData(Repatrition repat){
           int n = 0;
        PreparedStatement pst = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            String mcode = CommonFunctions.getMaxCode("EMP_NOTIFICATION", "NOT_ID", con);
            pst = con.prepareStatement("INSERT INTO EMP_NOTIFICATION(NOT_ID,NOT_TYPE,EMP_ID,DOE,TOE,IF_ASSUMED,ORDNO,ORDDT,DEPT_CODE,OFF_CODE,AUTH,NOTE,IF_VISIBLE,ENT_DEPT,ENT_OFF,ENT_AUTH,ACS,ASCS) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            pst.setString(1, mcode);
            pst.setString(2, "REPATRIATION");
            pst.setString(3, repat.getEmpid());
            pst.setString(4, repat.getDoe());
            pst.setString(5, repat.getToe());
            pst.setString(6, repat.getIfAssumed());
            pst.setString(7, repat.getOrdno());
            pst.setString(8, repat.getOrdDate());
            pst.setString(9, repat.getSltDept());
            pst.setString(10, repat.getSltOffice());
            pst.setString(11, repat.getSltAuth());
            pst.setString(12, repat.getNote());
            pst.setString(13, repat.getIfVisible());
            pst.setString(14, repat.getEntDept());
            pst.setString(15, repat.getEntOffice());
            pst.setString(16, repat.getEntAuth());
            pst.setString(17, repat.getCadreStatus());
            pst.setString(18, repat.getSubStatus());
            n = pst.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return n; 
      }
    
    public int updateRepatritionData(Repatrition repat){
          int n = 0;
        PreparedStatement pst = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("UPDATE  EMP_NOTIFICATION SET NOT_TYPE=?,EMP_ID=?,DOE=?,TOE=?,IF_ASSUMED=?,ORDNO=?,ORDDT=?,DEPT_CODE=?,OFF_CODE=?,AUTH,NOTE=?,IF_VISIBLE=?,ENT_DEPT=?,ENT_OFF=?,ENT_AUTH=?,ACS=?,ASCS=? WHERE NOT_ID=?");
            pst.setString(1, "REPATRIATION");
            pst.setString(2, repat.getEmpid());
            pst.setString(3, repat.getDoe());
            pst.setString(4, repat.getToe());
            pst.setString(5, repat.getIfAssumed());
            pst.setString(6, repat.getOrdno());
            pst.setString(7, repat.getOrdDate());
            pst.setString(8, repat.getSltDept());
            pst.setString(9, repat.getSltOffice());
            pst.setString(10, repat.getSltAuth());
            pst.setString(11, repat.getNote());
            pst.setString(12, repat.getIfVisible());
            pst.setString(13, repat.getEntDept());
            pst.setString(14, repat.getEntOffice());
            pst.setString(15, repat.getEntAuth());
            pst.setString(16, repat.getCadreStatus());
            pst.setString(17, repat.getSubStatus());
            pst.setString(18, repat.getNotId());
            n = pst.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return n;
    }
    
    public int deleteRepatrition(String repatritionId){
        int n = 0;
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("DELETE FROM EMP_NOTIFICATION WHERE NOT_ID=?");
            pst.setString(1, repatritionId);
            pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return n;
    }
    
    public Repatrition editRepatrition(String repatritionId){
         Repatrition repat = null;
        ResultSet rs = null;
        PreparedStatement pst = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("SELECT NOT_TYPE,EMP_ID,DOE,TOE,IF_ASSUMED,ORDNO,ORDDT,DEPT_CODE,OFF_CODE,AUTH,NOTE,IF_VISIBLE,ENT_DEPT,ENT_OFF,ENT_AUTH,ACS,ASCS FROM EMP_NOTIFICATION WHERE NOT_ID=?");
            pst.setString(1, repatritionId);
            rs = pst.executeQuery();
            if (rs.next()) {
                repat = new Repatrition();
                repat.setNotType("REPATRIATION");
                repat.setEmpid(rs.getString("EMP_ID"));
                repat.setDoe(rs.getString("DOE"));
                repat.setToe(rs.getString("TOE"));
                repat.setIfAssumed(rs.getString("IF_ASSUMED"));
                repat.setOrdno(rs.getString("ORDNO"));
                repat.setOrdDate(rs.getString("ORDDT"));
                repat.setSltDept(rs.getString("DEPT_CODE"));
                repat.setSltOffice(rs.getString("OFF_CODE"));
                repat.setSltAuth(rs.getString("AUTH"));
                repat.setNote(rs.getString("NOTE"));
                repat.setIfVisible(rs.getString("IF_VISIBLE"));
                repat.setEntDept(rs.getString("ENT_DEPT"));
                repat.setEntOffice(rs.getString("ENT_OFF"));
                repat.setEntAuth(rs.getString("ENT_AUTH"));
                repat.setCadreStatus(rs.getString("ACS"));
                repat.setSubStatus(rs.getString("ASCS"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return repat;
    }
    
    public List findAllRepatrition(String empId){
        List list = null;
        Connection con=null;
        PreparedStatement pst=null;
        ResultSet rs=null;
        RepatritionList repatList=null;
        list=new ArrayList();
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("SELECT NOT_ID,DOE,ORDNO,ORDDT FROM EMP_NOTIFICATION WHERE EMP_ID=? AND NOT_TYPE='REPATRIATION'");
            pst.setString(1, empId);
            rs = pst.executeQuery();
            while (rs.next()) {
                repatList = new RepatritionList();
                repatList.setNotId(rs.getString("NOT_ID"));
                repatList.setNotType("REPATRIATION");
                repatList.setDoe(rs.getString("DOE"));
                repatList.setOrdno(rs.getString("ORDNO"));
                repatList.setOrdDate(rs.getString("ORDDT"));
                list.add(repatList);
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
