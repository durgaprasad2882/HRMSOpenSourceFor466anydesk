
package hrms.dao.retirement;

import hrms.common.CommonFunctions;
import hrms.common.DataBaseFunctions;
import hrms.model.repatrition.Repatrition;
import hrms.model.retirement.Retirement;
import hrms.model.termination.Termination;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.annotation.Resource;
import javax.sql.DataSource;

public class RetirementDAOImpl implements RetirementDAO{
     @Resource(name = "dataSource")
    protected DataSource dataSource;
    public DataSource getDataSource() {
        return dataSource;
    }
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
     public int insertRetirementData(Retirement retire){
          int n = 0;
        PreparedStatement pst = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            String mcode = CommonFunctions.getMaxCode("EMP_NOTIFICATION", "NOT_ID", con);
            pst = con.prepareStatement("INSERT INTO EMP_NOTIFICATION(NOT_ID,NOT_TYPE,EMP_ID,DOE,TOE,IF_ASSUMED,ORDNO,ORDDT,DEPT_CODE,OFF_CODE,AUTH,NOTE,IF_VISIBLE,ENT_DEPT,ENT_OFF,ENT_AUTH,ACS,ASCS) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            pst.setString(1, mcode);
            pst.setString(2, "RETIREMENT");
            pst.setString(3, retire.getEmpid());
            pst.setString(4, retire.getDoe());
            pst.setString(5, retire.getToe());
            pst.setString(6, retire.getIfAssumed());
            pst.setString(7, retire.getOrdno());
            pst.setString(8, retire.getOrdDate());
            pst.setString(9, retire.getSltDept());
            pst.setString(10, retire.getSltOffice());
            pst.setString(11, retire.getSltAuth());
            pst.setString(12, retire.getNote());
            pst.setString(13, retire.getIfVisible());
            pst.setString(14, retire.getEntDept());
            pst.setString(15, retire.getEntOffice());
            pst.setString(16, retire.getEntAuth());
            pst.setString(17, retire.getCadreStatus());
            pst.setString(18, retire.getSubStatus());
            n = pst.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return n;
     }
    
    public int updateRetirementData(Retirement retire){
         int n = 0;
        PreparedStatement pst = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("UPDATE  EMP_NOTIFICATION SET NOT_TYPE=?,EMP_ID=?,DOE=?,TOE=?,IF_ASSUMED=?,ORDNO=?,ORDDT=?,DEPT_CODE=?,OFF_CODE=?,AUTH,NOTE=?,IF_VISIBLE=?,ENT_DEPT=?,ENT_OFF=?,ENT_AUTH=?,ACS=?,ASCS=? WHERE NOT_ID=?");
            pst.setString(1, "RETIREMENT");
            pst.setString(2, retire.getEmpid());
            pst.setString(3, retire.getDoe());
            pst.setString(4, retire.getToe());
            pst.setString(5, retire.getIfAssumed());
            pst.setString(6, retire.getOrdno());
            pst.setString(7, retire.getOrdDate());
            pst.setString(8, retire.getSltDept());
            pst.setString(9, retire.getSltOffice());
            pst.setString(10, retire.getSltAuth());
            pst.setString(11, retire.getNote());
            pst.setString(12, retire.getIfVisible());
            pst.setString(13, retire.getEntDept());
            pst.setString(14, retire.getEntOffice());
            pst.setString(15, retire.getEntAuth());
            pst.setString(16, retire.getCadreStatus());
            pst.setString(17, retire.getSubStatus());
            pst.setString(18, retire.getNotId());
            n = pst.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return n;
    }
    
    public int deleteRetirement(String retireId){
        int n = 0;
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("DELETE FROM EMP_NOTIFICATION WHERE NOT_ID=?");
            pst.setString(1, retireId);
            pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return n;
    }
    
    public Retirement editRetirement(String retireId){
        Retirement retire = null;
        ResultSet rs = null;
        PreparedStatement pst = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("SELECT NOT_TYPE,EMP_ID,DOE,TOE,IF_ASSUMED,ORDNO,ORDDT,DEPT_CODE,OFF_CODE,AUTH,NOTE,IF_VISIBLE,ENT_DEPT,ENT_OFF,ENT_AUTH,ACS,ASCS FROM EMP_NOTIFICATION WHERE NOT_ID=?");
            pst.setString(1, retireId);
            rs = pst.executeQuery();
            if (rs.next()) {
                retire = new Retirement();
                retire.setNotType("RETIREMENT");
                retire.setEmpid(rs.getString("EMP_ID"));
                retire.setDoe(rs.getString("DOE"));
                retire.setToe(rs.getString("TOE"));
                retire.setIfAssumed(rs.getString("IF_ASSUMED"));
                retire.setOrdno(rs.getString("ORDNO"));
                retire.setOrdDate(rs.getString("ORDDT"));
                retire.setSltDept(rs.getString("DEPT_CODE"));
                retire.setSltOffice(rs.getString("OFF_CODE"));
                retire.setSltAuth(rs.getString("AUTH"));
                retire.setNote(rs.getString("NOTE"));
                retire.setIfVisible(rs.getString("IF_VISIBLE"));
                retire.setEntDept(rs.getString("ENT_DEPT"));
                retire.setEntOffice(rs.getString("ENT_OFF"));
                retire.setEntAuth(rs.getString("ENT_AUTH"));
                retire.setCadreStatus(rs.getString("ACS"));
                retire.setSubStatus(rs.getString("ASCS"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return retire;
    }

    @Override
    public int insertRetirementData(Termination terminationForm) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
