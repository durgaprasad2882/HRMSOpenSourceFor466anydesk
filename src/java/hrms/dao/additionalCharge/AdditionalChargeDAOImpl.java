
package hrms.dao.additionalCharge;

import hrms.common.CommonFunctions;
import hrms.common.DataBaseFunctions;
import hrms.model.additionalCharge.AdditionalCharge;
import hrms.model.additionalCharge.AdditionalChargeList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;


public class AdditionalChargeDAOImpl implements AdditionalChargeDAO {
    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    public int insertAdditionalChargeData(AdditionalCharge addchage) {
         int n = 0;
        PreparedStatement pst = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            String mcode = CommonFunctions.getMaxCode("EMP_NOTIFICATION", "NOT_ID", con);
            pst = con.prepareStatement("INSERT INTO EMP_NOTIFICATION(NOT_ID,NOT_TYPE,EMP_ID,DOE,TOE,IF_ASSUMED,ORDNO,ORDDT,DEPT_CODE,OFF_CODE,AUTH,NOTE,IF_VISIBLE,ENT_DEPT,ENT_OFF,ENT_AUTH,ACS,ASCS) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            pst.setString(1, mcode);
            pst.setString(2, "ADDITIONAL_CHARGE");
            pst.setString(3, addchage.getEmpid());
            pst.setString(4, addchage.getDoe());
            pst.setString(5, addchage.getToe());
            pst.setString(6, addchage.getIfAssumed());
            pst.setString(7, addchage.getOrdno());
            pst.setString(8, addchage.getOrdDate());
            pst.setString(9, addchage.getSltDept());
            pst.setString(10, addchage.getSltOffice());
            pst.setString(11, addchage.getSltAuth());
            pst.setString(12, addchage.getNote());
            pst.setString(13, addchage.getIfVisible());
            pst.setString(14, addchage.getEntDept());
            pst.setString(15, addchage.getEntOffice());
            pst.setString(16, addchage.getEntAuth());
            pst.setString(17, addchage.getCadreStatus());
            pst.setString(18, addchage.getSubStatus());
            n = pst.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return n;
    }

    public int updateAdditionalChargeData(AdditionalCharge addchage) {
        int n = 0;
        PreparedStatement pst = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("UPDATE  EMP_NOTIFICATION SET NOT_TYPE=?,EMP_ID=?,DOE=?,TOE=?,IF_ASSUMED=?,ORDNO=?,ORDDT=?,DEPT_CODE=?,OFF_CODE=?,AUTH,NOTE=?,IF_VISIBLE=?,ENT_DEPT=?,ENT_OFF=?,ENT_AUTH=?,ACS=?,ASCS=? WHERE NOT_ID='" + addchage.getNotId() + "'");
            pst.setString(1, "ADDITIONAL_CHARGE");
            pst.setString(2, addchage.getEmpid());
            pst.setString(3, addchage.getDoe());
            pst.setString(4, addchage.getToe());
            pst.setString(5, addchage.getIfAssumed());
            pst.setString(6, addchage.getOrdno());
            pst.setString(7, addchage.getOrdDate());
            pst.setString(8, addchage.getSltDept());
            pst.setString(9, addchage.getSltOffice());
            pst.setString(10, addchage.getSltAuth());
            pst.setString(11, addchage.getNote());
            pst.setString(12, addchage.getIfVisible());
            pst.setString(13, addchage.getEntDept());
            pst.setString(14, addchage.getEntOffice());
            pst.setString(15, addchage.getEntAuth());
            pst.setString(16, addchage.getCadreStatus());
            pst.setString(17, addchage.getSubStatus());
            pst.setString(18, addchage.getNotId());
            n = pst.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return n;
    }

    public int deleteAdditionalCharge(String addchageId) {
         int n = 0;
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("DELETE FROM EMP_NOTIFICATION WHERE NOT_ID=?");
            pst.setString(1, addchageId);
            pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return n;
    }

    public AdditionalCharge editAdditionalCharge(String addchageId) {
        AdditionalCharge addchage = null;
        ResultSet rs = null;
        PreparedStatement pst = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("SELECT NOT_TYPE,EMP_ID,DOE,TOE,IF_ASSUMED,ORDNO,ORDDT,DEPT_CODE,OFF_CODE,AUTH,NOTE,IF_VISIBLE,ENT_DEPT,ENT_OFF,ENT_AUTH,ACS,ASCS FROM EMP_NOTIFICATION WHERE NOT_ID='" + addchageId + "'");
            pst.setString(1, addchageId);
            rs = pst.executeQuery();
            if (rs.next()) {
                addchage = new AdditionalCharge();
                addchage.setNotType("ADDITIONAL_CHARGE");
                addchage.setEmpid(rs.getString("EMP_ID"));
                addchage.setDoe(rs.getString("DOE"));
                addchage.setToe(rs.getString("TOE"));
                addchage.setIfAssumed(rs.getString("IF_ASSUMED"));
                addchage.setOrdno(rs.getString("ORDNO"));
                addchage.setOrdDate(rs.getString("ORDDT"));
                addchage.setSltDept(rs.getString("DEPT_CODE"));
                addchage.setSltOffice(rs.getString("OFF_CODE"));
                addchage.setSltAuth(rs.getString("AUTH"));
                addchage.setNote(rs.getString("NOTE"));
                addchage.setIfVisible(rs.getString("IF_VISIBLE"));
                addchage.setEntDept(rs.getString("ENT_DEPT"));
                addchage.setEntOffice(rs.getString("ENT_OFF"));
                addchage.setEntAuth(rs.getString("ENT_AUTH"));
                addchage.setCadreStatus(rs.getString("ACS"));
                addchage.setSubStatus(rs.getString("ASCS"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return addchage;
    }

    public List findAllAdditionalCharge(String empId) {
        List list=null;
         Connection con=null;
        PreparedStatement pst=null;
        ResultSet rs=null;
        AdditionalChargeList addList=null;
        list=new ArrayList();
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("SELECT NOT_ID,DOE,ORDNO,ORDDT FROM EMP_NOTIFICATION WHERE EMP_ID=? AND NOT_TYPE=?");
           // System.out.println("pst");
            pst.setString(1, empId);
            pst.setString(2, "ADDITIONAL_CHARGE");
            rs = pst.executeQuery();
            while (rs.next()) {
                addList = new AdditionalChargeList();
                addList.setNotId(rs.getString("NOT_ID"));
                addList.setNotType("ABSORPTION");
                addList.setDoe(rs.getString("DOE"));
                addList.setOrdno(rs.getString("ORDNO"));
                addList.setOrdDate(rs.getString("ORDDT"));
                list.add(addList);
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
