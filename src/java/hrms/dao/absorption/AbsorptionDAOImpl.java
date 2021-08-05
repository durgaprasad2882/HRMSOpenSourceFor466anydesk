package hrms.dao.absorption;


import hrms.common.CommonFunctions;
import hrms.common.DataBaseFunctions;
import hrms.model.absorption.Absorption;
import hrms.model.absorption.AbsorptionModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;

public class AbsorptionDAOImpl implements AbsorptionDAO {

    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public int insertAbsorptionData(AbsorptionModel absorp) {
        int n = 0;
        PreparedStatement pst = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            String mcode = CommonFunctions.getMaxCode("EMP_NOTIFICATION", "NOT_ID", con);
            pst = con.prepareStatement("INSERT INTO EMP_NOTIFICATION(NOT_ID,NOT_TYPE,EMP_ID,DOE,TOE,IF_ASSUMED,ORDNO,ORDDT,DEPT_CODE,OFF_CODE,AUTH,NOTE,IF_VISIBLE,ENT_DEPT,ENT_OFF,ENT_AUTH,ACS,ASCS) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            pst.setString(1, mcode);
            pst.setString(2, "ABSORPTION");
            pst.setString(3, absorp.getEmpid());
            pst.setString(4, absorp.getDoe());
            pst.setString(5, absorp.getToe());
            pst.setString(6, absorp.getIfAssumed());
            pst.setString(7, absorp.getOrdno());
            pst.setString(8, absorp.getOrdDate());
            pst.setString(9, absorp.getSltDept());
            pst.setString(10, absorp.getSltOffice());
            pst.setString(11, absorp.getSltAuth());
            pst.setString(12, absorp.getNote());
            pst.setString(13, absorp.getIfVisible());
            pst.setString(14, absorp.getEntDept());
            pst.setString(15, absorp.getEntOffice());
            pst.setString(16, absorp.getEntAuth());
            pst.setString(17, absorp.getCadreStatus());
            pst.setString(18, absorp.getSubStatus());
            n = pst.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return n;
    }

    public int updateAbsorptionData(AbsorptionModel absorp) {
        int n = 0;
        PreparedStatement pst = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("UPDATE  EMP_NOTIFICATION SET NOT_TYPE=?,EMP_ID=?,DOE=?,TOE=?,IF_ASSUMED=?,ORDNO=?,ORDDT=?,DEPT_CODE=?,OFF_CODE=?,AUTH,NOTE=?,IF_VISIBLE=?,ENT_DEPT=?,ENT_OFF=?,ENT_AUTH=?,ACS=?,ASCS=? WHERE NOT_ID=?");
            pst.setString(1, "ABSORPTION");
            pst.setString(2, absorp.getEmpid());
            pst.setString(3, absorp.getDoe());
            pst.setString(4, absorp.getToe());
            pst.setString(5, absorp.getIfAssumed());
            pst.setString(6, absorp.getOrdno());
            pst.setString(7, absorp.getOrdDate());
            pst.setString(8, absorp.getSltDept());
            pst.setString(9, absorp.getSltOffice());
            pst.setString(10, absorp.getSltAuth());
            pst.setString(11, absorp.getNote());
            pst.setString(12, absorp.getIfVisible());
            pst.setString(13, absorp.getEntDept());
            pst.setString(14, absorp.getEntOffice());
            pst.setString(15, absorp.getEntAuth());
            pst.setString(16, absorp.getCadreStatus());
            pst.setString(17, absorp.getSubStatus());
            pst.setString(18, absorp.getNotId());
            n = pst.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return n;
    }

    public int deleteAbsorptionData(String absorpId) {
        int n = 0;
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("DELETE FROM EMP_NOTIFICATION WHERE NOT_ID=?");
            pst.setString(1, absorpId);
            pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return n;
    }

    public AbsorptionModel editAbsorptionData(String absorpId) {
        AbsorptionModel absorp = null;
        ResultSet rs = null;
        PreparedStatement pst = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("SELECT NOT_TYPE,EMP_ID,DOE,TOE,IF_ASSUMED,ORDNO,ORDDT,DEPT_CODE,OFF_CODE,AUTH,NOTE,IF_VISIBLE,ENT_DEPT,ENT_OFF,ENT_AUTH,ACS,ASCS FROM EMP_NOTIFICATION WHERE NOT_ID=?");
            pst.setString(1, absorpId);
            rs = pst.executeQuery();
            if (rs.next()) {
                absorp = new AbsorptionModel();
                absorp.setNotType("ABSORPTION");
                absorp.setEmpid(rs.getString("EMP_ID"));
                absorp.setDoe(rs.getString("DOE"));
                absorp.setToe(rs.getString("TOE"));
                absorp.setIfAssumed(rs.getString("IF_ASSUMED"));
                absorp.setOrdno(rs.getString("ORDNO"));
                absorp.setOrdDate(rs.getString("ORDDT"));
                absorp.setSltDept(rs.getString("DEPT_CODE"));
                absorp.setSltOffice(rs.getString("OFF_CODE"));
                absorp.setSltAuth(rs.getString("AUTH"));
                absorp.setNote(rs.getString("NOTE"));
                absorp.setIfVisible(rs.getString("IF_VISIBLE"));
                absorp.setEntDept(rs.getString("ENT_DEPT"));
                absorp.setEntOffice(rs.getString("ENT_OFF"));
                absorp.setEntAuth(rs.getString("ENT_AUTH"));
                absorp.setCadreStatus(rs.getString("ACS"));
                absorp.setSubStatus(rs.getString("ASCS"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return absorp;
    }

    public List findAllAbsorption(String empId) {
        List list = null;
        Connection con=null;
        PreparedStatement pst=null;
        ResultSet rs=null;
        Absorption absorpBean=null;
        list=new ArrayList();
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("SELECT NOT_ID,DOE,ORDNO,ORDDT FROM EMP_NOTIFICATION WHERE EMP_ID=? AND NOT_TYPE='ABSORPTION'");
            pst.setString(1, empId);
            rs = pst.executeQuery();
            while (rs.next()) {
                absorpBean = new Absorption();
                absorpBean.setNotId(rs.getString("NOT_ID"));
                absorpBean.setNotType("ABSORPTION");
                absorpBean.setDoe(rs.getString("DOE"));
                absorpBean.setOrdno(rs.getString("ORDNO"));
                absorpBean.setOrdDate(rs.getString("ORDDT"));
                list.add(absorpBean);
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
