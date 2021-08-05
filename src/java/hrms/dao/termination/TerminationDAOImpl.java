
package hrms.dao.termination;

import hrms.common.CommonFunctions;
import hrms.common.DataBaseFunctions;
import hrms.model.termination.Termination;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.annotation.Resource;
import javax.sql.DataSource;

public class TerminationDAOImpl implements TerminationDAO{
     @Resource(name = "dataSource")
    protected DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    public int insertPayrevisionData(Termination term){
         int n = 0;
        PreparedStatement pst = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            String mcode = CommonFunctions.getMaxCode("EMP_NOTIFICATION", "NOT_ID", con);
            pst = con.prepareStatement("INSERT INTO EMP_NOTIFICATION(NOT_ID,NOT_TYPE,EMP_ID,DOE,TOE,IF_ASSUMED,ORDNO,ORDDT,DEPT_CODE,OFF_CODE,AUTH,NOTE,IF_VISIBLE,ENT_DEPT,ENT_OFF,ENT_AUTH,ACS,ASCS) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            pst.setString(1, mcode);
            pst.setString(2, "TERMINATION");
            pst.setString(3, term.getEmpid());
            pst.setString(4, term.getDoe());
            pst.setString(5, term.getToe());
            pst.setString(6, term.getIfAssumed());
            pst.setString(7, term.getOrdno());
            pst.setString(8, term.getOrdDate());
            pst.setString(9, term.getSltDept());
            pst.setString(10, term.getSltOffice());
            pst.setString(11, term.getSltAuth());
            pst.setString(12, term.getNote());
            pst.setString(13, term.getIfVisible());
            pst.setString(14, term.getEntDept());
            pst.setString(15, term.getEntOffice());
            pst.setString(16, term.getEntAuth());
            pst.setString(17, term.getCadreStatus());
            pst.setString(18, term.getSubStatus());
            n = pst.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return n; 
    }
    
    public int updateTerminationData(Termination term){
         int n = 0;
        PreparedStatement pst = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("UPDATE  EMP_NOTIFICATION SET NOT_TYPE=?,EMP_ID=?,DOE=?,TOE=?,IF_ASSUMED=?,ORDNO=?,ORDDT=?,DEPT_CODE=?,OFF_CODE=?,AUTH,NOTE=?,IF_VISIBLE=?,ENT_DEPT=?,ENT_OFF=?,ENT_AUTH=?,ACS=?,ASCS=? WHERE NOT_ID=?");
            pst.setString(1, "TERMINATION");
            pst.setString(2, term.getEmpid());
            pst.setString(3, term.getDoe());
            pst.setString(4, term.getToe());
            pst.setString(5, term.getIfAssumed());
            pst.setString(6, term.getOrdno());
            pst.setString(7, term.getOrdDate());
            pst.setString(8, term.getSltDept());
            pst.setString(9, term.getSltOffice());
            pst.setString(10, term.getSltAuth());
            pst.setString(11, term.getNote());
            pst.setString(12, term.getIfVisible());
            pst.setString(13, term.getEntDept());
            pst.setString(14, term.getEntOffice());
            pst.setString(15, term.getEntAuth());
            pst.setString(16, term.getCadreStatus());
            pst.setString(17, term.getSubStatus());
            pst.setString(18, term.getNotId());
            n = pst.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return n;
    }
    
    public int deleteTermination(String terminationId){
        int n = 0;
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("DELETE FROM EMP_NOTIFICATION WHERE NOT_ID=?");
            pst.setString(1, terminationId);
            pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return n;
    }
    
    public Termination editTermination(String terminationId){
        Termination term = null;
        ResultSet rs = null;
        PreparedStatement pst = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("SELECT NOT_TYPE,EMP_ID,DOE,TOE,IF_ASSUMED,ORDNO,ORDDT,DEPT_CODE,OFF_CODE,AUTH,NOTE,IF_VISIBLE,ENT_DEPT,ENT_OFF,ENT_AUTH,ACS,ASCS FROM EMP_NOTIFICATION WHERE NOT_ID=?");
            pst.setString(1, terminationId);
            rs = pst.executeQuery();
            if (rs.next()) {
                term = new Termination();
                term.setNotType("TERMINATION");
                term.setEmpid(rs.getString("EMP_ID"));
                term.setDoe(rs.getString("DOE"));
                term.setToe(rs.getString("TOE"));
                term.setIfAssumed(rs.getString("IF_ASSUMED"));
                term.setOrdno(rs.getString("ORDNO"));
                term.setOrdDate(rs.getString("ORDDT"));
                term.setSltDept(rs.getString("DEPT_CODE"));
                term.setSltOffice(rs.getString("OFF_CODE"));
                term.setSltAuth(rs.getString("AUTH"));
                term.setNote(rs.getString("NOTE"));
                term.setIfVisible(rs.getString("IF_VISIBLE"));
                term.setEntDept(rs.getString("ENT_DEPT"));
                term.setEntOffice(rs.getString("ENT_OFF"));
                term.setEntAuth(rs.getString("ENT_AUTH"));
                term.setCadreStatus(rs.getString("ACS"));
                term.setSubStatus(rs.getString("ASCS"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return term;
    }
}
