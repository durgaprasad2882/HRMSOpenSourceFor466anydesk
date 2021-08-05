
package hrms.dao.payfixation;

import hrms.common.CommonFunctions;
import hrms.common.DataBaseFunctions;
import hrms.model.payfixation.PayFixation;
import hrms.model.payfixation.PayFixationList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;

public class PayFixationDAOImpl implements PayFixationDAO{
     @Resource(name = "dataSource")
    protected DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    public int insertPayFixationData(PayFixation payfix){
         int n = 0;
        PreparedStatement pst = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            String mcode = CommonFunctions.getMaxCode("EMP_NOTIFICATION", "NOT_ID", con);
            pst = con.prepareStatement("INSERT INTO EMP_NOTIFICATION(NOT_ID,NOT_TYPE,EMP_ID,DOE,TOE,IF_ASSUMED,ORDNO,ORDDT,DEPT_CODE,OFF_CODE,AUTH,NOTE,IF_VISIBLE,ENT_DEPT,ENT_OFF,ENT_AUTH,ACS,ASCS) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            pst.setString(1, mcode);
            pst.setString(2, "PAYFIXATION");
            pst.setString(3, payfix.getEmpid());
            pst.setString(4, payfix.getDoe());
            pst.setString(5, payfix.getToe());
            pst.setString(6, payfix.getIfAssumed());
            pst.setString(7, payfix.getOrdno());
            pst.setString(8, payfix.getOrdDate());
            pst.setString(9, payfix.getSltDept());
            pst.setString(10, payfix.getSltOffice());
            pst.setString(11, payfix.getSltAuth());
            pst.setString(12, payfix.getNote());
            pst.setString(13, payfix.getIfVisible());
            pst.setString(14, payfix.getEntDept());
            pst.setString(15, payfix.getEntOffice());
            pst.setString(16, payfix.getEntAuth());
            pst.setString(17, payfix.getCadreStatus());
            pst.setString(18, payfix.getSubStatus());
            n = pst.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return n;
    }
    
    public int updatePayFixationData(PayFixation payfix){
       int n = 0;
        PreparedStatement pst = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("UPDATE  EMP_NOTIFICATION SET NOT_TYPE=?,EMP_ID=?,DOE=?,TOE=?,IF_ASSUMED=?,ORDNO=?,ORDDT=?,DEPT_CODE=?,OFF_CODE=?,AUTH,NOTE=?,IF_VISIBLE=?,ENT_DEPT=?,ENT_OFF=?,ENT_AUTH=?,ACS=?,ASCS=? WHERE NOT_ID=?");
            pst.setString(1, "PAYFIXATION");
            pst.setString(2, payfix.getEmpid());
            pst.setString(3, payfix.getDoe());
            pst.setString(4, payfix.getToe());
            pst.setString(5, payfix.getIfAssumed());
            pst.setString(6, payfix.getOrdno());
            pst.setString(7, payfix.getOrdDate());
            pst.setString(8, payfix.getSltDept());
            pst.setString(9, payfix.getSltOffice());
            pst.setString(10, payfix.getSltAuth());
            pst.setString(11, payfix.getNote());
            pst.setString(12, payfix.getIfVisible());
            pst.setString(13, payfix.getEntDept());
            pst.setString(14, payfix.getEntOffice());
            pst.setString(15, payfix.getEntAuth());
            pst.setString(16, payfix.getCadreStatus());
            pst.setString(17, payfix.getSubStatus());
            pst.setString(18, payfix.getNotId());
            n = pst.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return n; 
    }
    
    public int deletePayFixation(String payfixId){
        int n = 0;
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("DELETE FROM EMP_NOTIFICATION WHERE NOT_ID=?");
            pst.setString(1, payfixId);
            pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return n;
    }
    
    public PayFixation editPayFixation(String payfixId){
       PayFixation payfix = null;
        ResultSet rs = null;
        PreparedStatement pst = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("SELECT NOT_TYPE,EMP_ID,DOE,TOE,IF_ASSUMED,ORDNO,ORDDT,DEPT_CODE,OFF_CODE,AUTH,NOTE,IF_VISIBLE,ENT_DEPT,ENT_OFF,ENT_AUTH,ACS,ASCS FROM EMP_NOTIFICATION WHERE NOT_ID=?");
            pst.setString(1, payfixId);
            rs = pst.executeQuery();
            if (rs.next()) {
                payfix = new PayFixation();
                payfix.setNotType("PAYFIXATION");
                payfix.setEmpid(rs.getString("EMP_ID"));
                payfix.setDoe(rs.getString("DOE"));
                payfix.setToe(rs.getString("TOE"));
                payfix.setIfAssumed(rs.getString("IF_ASSUMED"));
                payfix.setOrdno(rs.getString("ORDNO"));
                payfix.setOrdDate(rs.getString("ORDDT"));
                payfix.setSltDept(rs.getString("DEPT_CODE"));
                payfix.setSltOffice(rs.getString("OFF_CODE"));
                payfix.setSltAuth(rs.getString("AUTH"));
                payfix.setNote(rs.getString("NOTE"));
                payfix.setIfVisible(rs.getString("IF_VISIBLE"));
                payfix.setEntDept(rs.getString("ENT_DEPT"));
                payfix.setEntOffice(rs.getString("ENT_OFF"));
                payfix.setEntAuth(rs.getString("ENT_AUTH"));
                payfix.setCadreStatus(rs.getString("ACS"));
                payfix.setSubStatus(rs.getString("ASCS"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return payfix;  
    }
    
    public List findAllPayFixation(String empId){
          List list = null;
        Connection con=null;
        PreparedStatement pst=null;
        ResultSet rs=null;
        PayFixationList payFixList=null;
        list=new ArrayList();
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("SELECT NOT_ID,NOT_TYPE,DOE,ORDNO,ORDDT FROM EMP_NOTIFICATION WHERE EMP_ID=? AND NOT_TYPE='PAYFIXATION'");
            pst.setString(1, empId);
            rs = pst.executeQuery();
            while (rs.next()) {
                payFixList = new PayFixationList();
                payFixList.setNotId(rs.getString("NOT_ID"));
                payFixList.setNotType(rs.getString("NOT_TYPE"));
                payFixList.setDoe(rs.getString("DOE"));
                payFixList.setOrdno(rs.getString("ORDNO"));
                payFixList.setOrdDate(rs.getString("ORDDT"));
                list.add(payFixList);
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
