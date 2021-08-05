
package hrms.dao.resignation;

import hrms.common.CommonFunctions;
import hrms.common.DataBaseFunctions;
import hrms.model.resignation.Resignation;
import hrms.model.retirement.Retirement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.annotation.Resource;
import javax.sql.DataSource;

public class ResignationDAOImpl implements ResignationDAO{
     @Resource(name = "dataSource")
    protected DataSource dataSource;
    public DataSource getDataSource() {
        return dataSource;
    }
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
     public int insertResignationtData(Resignation resig){
          int n = 0;
        PreparedStatement pst = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            String mcode = CommonFunctions.getMaxCode("EMP_NOTIFICATION", "NOT_ID", con);
            pst = con.prepareStatement("INSERT INTO EMP_NOTIFICATION(NOT_ID,NOT_TYPE,EMP_ID,DOE,TOE,IF_ASSUMED,ORDNO,ORDDT,DEPT_CODE,OFF_CODE,AUTH,NOTE,IF_VISIBLE,ENT_DEPT,ENT_OFF,ENT_AUTH,ACS,ASCS) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            pst.setString(1, mcode);
            pst.setString(2, "RESIGNATION");
            pst.setString(3, resig.getEmpid());
            pst.setString(4, resig.getDoe());
            pst.setString(5, resig.getToe());
            pst.setString(6, resig.getIfAssumed());
            pst.setString(7, resig.getOrdno());
            pst.setString(8, resig.getOrdDate());
            pst.setString(9, resig.getSltDept());
            pst.setString(10, resig.getSltOffice());
            pst.setString(11, resig.getSltAuth());
            pst.setString(12, resig.getNote());
            pst.setString(13, resig.getIfVisible());
            pst.setString(14, resig.getEntDept());
            pst.setString(15, resig.getEntOffice());
            pst.setString(16, resig.getEntAuth());
            pst.setString(17, resig.getCadreStatus());
            pst.setString(18, resig.getSubStatus());
            n = pst.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return n;
     }
    
    public int updateResignationData(Resignation resig){
        int n = 0;
        PreparedStatement pst = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("UPDATE  EMP_NOTIFICATION SET NOT_TYPE=?,EMP_ID=?,DOE=?,TOE=?,IF_ASSUMED=?,ORDNO=?,ORDDT=?,DEPT_CODE=?,OFF_CODE=?,AUTH,NOTE=?,IF_VISIBLE=?,ENT_DEPT=?,ENT_OFF=?,ENT_AUTH=?,ACS=?,ASCS=? WHERE NOT_ID=?");
            pst.setString(1, "RESIGNATION");
            pst.setString(2, resig.getEmpid());
            pst.setString(3, resig.getDoe());
            pst.setString(4, resig.getToe());
            pst.setString(5, resig.getIfAssumed());
            pst.setString(6, resig.getOrdno());
            pst.setString(7, resig.getOrdDate());
            pst.setString(8, resig.getSltDept());
            pst.setString(9, resig.getSltOffice());
            pst.setString(10, resig.getSltAuth());
            pst.setString(11, resig.getNote());
            pst.setString(12, resig.getIfVisible());
            pst.setString(13, resig.getEntDept());
            pst.setString(14, resig.getEntOffice());
            pst.setString(15, resig.getEntAuth());
            pst.setString(16, resig.getCadreStatus());
            pst.setString(17, resig.getSubStatus());
            pst.setString(18, resig.getNotId());
            n = pst.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return n;
    }
    
    public int deleteResignation(String resigId){
        int n = 0;
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("DELETE FROM EMP_NOTIFICATION WHERE NOT_ID=?");
            pst.setString(1, resigId);
            pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return n;
    }
    
    public Resignation editResignation(String resigId){
        Resignation resig = null;
        ResultSet rs = null;
        PreparedStatement pst = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("SELECT NOT_TYPE,EMP_ID,DOE,TOE,IF_ASSUMED,ORDNO,ORDDT,DEPT_CODE,OFF_CODE,AUTH,NOTE,IF_VISIBLE,ENT_DEPT,ENT_OFF,ENT_AUTH,ACS,ASCS FROM EMP_NOTIFICATION WHERE NOT_ID=?");
            pst.setString(1, resigId);
            rs = pst.executeQuery();
            if (rs.next()) {
                resig = new Resignation();
                resig.setNotType("RESIGNATION");
                resig.setEmpid(rs.getString("EMP_ID"));
                resig.setDoe(rs.getString("DOE"));
                resig.setToe(rs.getString("TOE"));
                resig.setIfAssumed(rs.getString("IF_ASSUMED"));
                resig.setOrdno(rs.getString("ORDNO"));
                resig.setOrdDate(rs.getString("ORDDT"));
                resig.setSltDept(rs.getString("DEPT_CODE"));
                resig.setSltOffice(rs.getString("OFF_CODE"));
                resig.setSltAuth(rs.getString("AUTH"));
                resig.setNote(rs.getString("NOTE"));
                resig.setIfVisible(rs.getString("IF_VISIBLE"));
                resig.setEntDept(rs.getString("ENT_DEPT"));
                resig.setEntOffice(rs.getString("ENT_OFF"));
                resig.setEntAuth(rs.getString("ENT_AUTH"));
                resig.setCadreStatus(rs.getString("ACS"));
                resig.setSubStatus(rs.getString("ASCS"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return resig;
    }
}
