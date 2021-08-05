
package hrms.dao.redesignation;

import hrms.common.CommonFunctions;
import hrms.common.DataBaseFunctions;
import hrms.model.redeployment.Redeployment;
import hrms.model.redesignation.Redesignation;
import hrms.model.redesignation.RedesignationList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;

public class RedesignationDAOImpl implements RedesignationDAO{
      @Resource(name = "dataSource")
    protected DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
     public int insertRedesignationData(Redesignation redesig){
        int n = 0;
        PreparedStatement pst = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            String mcode = CommonFunctions.getMaxCode("EMP_NOTIFICATION", "NOT_ID", con);
            pst = con.prepareStatement("INSERT INTO EMP_NOTIFICATION(NOT_ID,NOT_TYPE,EMP_ID,DOE,TOE,IF_ASSUMED,ORDNO,ORDDT,DEPT_CODE,OFF_CODE,AUTH,NOTE,IF_VISIBLE,ENT_DEPT,ENT_OFF,ENT_AUTH,ACS,ASCS) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            pst.setString(1, mcode);
            pst.setString(2, "REDESIGNATION");
            pst.setString(3, redesig.getEmpid());
            pst.setString(4, redesig.getDoe());
            pst.setString(5, redesig.getToe());
            pst.setString(6, redesig.getIfAssumed());
            pst.setString(7, redesig.getOrdno());
            pst.setString(8, redesig.getOrdDate());
            pst.setString(9, redesig.getSltDept());
            pst.setString(10, redesig.getSltOffice());
            pst.setString(11, redesig.getSltAuth());
            pst.setString(12, redesig.getNote());
            pst.setString(13, redesig.getIfVisible());
            pst.setString(14, redesig.getEntDept());
            pst.setString(15, redesig.getEntOffice());
            pst.setString(16, redesig.getEntAuth());
            pst.setString(17, redesig.getCadreStatus());
            pst.setString(18, redesig.getSubStatus());
            n = pst.executeUpdate();
            String trCode = CommonFunctions.getMaxCode("EMP_TRANSFER", "TR_ID", con);
            pst = con.prepareStatement("INSERT INTO EMP_TRANSFER(TR_ID,NOT_ID,NOT_TYPE,EMP_ID,DEPT_CODE,OFF_CODE,NEXT_SPC)VALUES(?,?,?,?,?,?,?)");
            pst.setString(1, trCode);
            pst.setString(2, mcode);
            pst.setString(3,"REDESIGNATION");
            pst.setString(4,redesig.getEmpid());
            pst.setString(5,redesig.getSltDept());
            pst.setString(6,redesig.getSltOffice());
            pst.setString(7,redesig.getSltAuth());
            n = pst.executeUpdate(); 
              
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return n; 
     }
    
    public int updateRedesignationData(Redesignation redesig){
         int n = 0;
        PreparedStatement pst = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("UPDATE  EMP_NOTIFICATION SET NOT_TYPE=?,EMP_ID=?,DOE=?,TOE=?,IF_ASSUMED=?,ORDNO=?,ORDDT=?,DEPT_CODE=?,OFF_CODE=?,AUTH,NOTE=?,IF_VISIBLE=?,ENT_DEPT=?,ENT_OFF=?,ENT_AUTH=?,ACS=?,ASCS=? WHERE NOT_ID=?");
            pst.setString(1, "REDESIGNATION");
            pst.setString(2, redesig.getEmpid());
            pst.setString(3, redesig.getDoe());
            pst.setString(4, redesig.getToe());
            pst.setString(5, redesig.getIfAssumed());
            pst.setString(6, redesig.getOrdno());
            pst.setString(7, redesig.getOrdDate());
            pst.setString(8, redesig.getSltDept());
            pst.setString(9, redesig.getSltOffice());
            pst.setString(10, redesig.getSltAuth());
            pst.setString(11, redesig.getNote());
            pst.setString(12, redesig.getIfVisible());
            pst.setString(13, redesig.getEntDept());
            pst.setString(14, redesig.getEntOffice());
            pst.setString(15, redesig.getEntAuth());
            pst.setString(16, redesig.getCadreStatus());
            pst.setString(17, redesig.getSubStatus());
            pst.setString(18, redesig.getNotId());
            n = pst.executeUpdate();
            pst = con.prepareStatement("UPDATE EMP_TRANSFER SET TR_ID=?,NOT_ID=?,NOT_TYPE=?,EMP_ID=?,DEPT_CODE=?,OFF_CODE=?,NEXT_SPC=? WHERE TR_ID=?");
            pst.setString(1, redesig.getNotId());
            pst.setString(2,"REDESIGNATION");
            pst.setString(3,redesig.getEmpid());
            pst.setString(4,redesig.getSltDept());
            pst.setString(5,redesig.getSltOffice());
            pst.setString(6,redesig.getSltAuth());
             pst.setString(7, redesig.getTrId());
            n = pst.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return n;
    }
    
    public int deleteRedesignation(String notId){
         int n = 0;
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("DELETE FROM EMP_NOTIFICATION WHERE NOT_ID=?");
            pst.setString(1, notId);
            pst.executeUpdate();
            pst = con.prepareStatement("DELETE FROM EMP_TRANSFER WHERE NOT_ID=?");
            pst.setString(1, notId);
            pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
             DataBaseFunctions.closeSqlObjects(con);
        }
        return n;
    }
    
    public Redesignation editRedesignation(String redesignationId){
        Redesignation redesig = null;
        ResultSet rs = null;
        PreparedStatement pst = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("SELECT NOT_TYPE,EMP_ID,DOE,TOE,IF_ASSUMED,ORDNO,ORDDT,DEPT_CODE,OFF_CODE,AUTH,NOTE,IF_VISIBLE,ENT_DEPT,ENT_OFF,ENT_AUTH,ACS,ASCS FROM EMP_NOTIFICATION WHERE NOT_ID=?");
            pst.setString(1, redesignationId);
            rs = pst.executeQuery();
            if (rs.next()) {
                redesig = new Redesignation();
                redesig.setNotType("REDESIGNATION");
                redesig.setEmpid(rs.getString("EMP_ID"));
                redesig.setDoe(rs.getString("DOE"));
                redesig.setToe(rs.getString("TOE"));
                redesig.setIfAssumed(rs.getString("IF_ASSUMED"));
                redesig.setOrdno(rs.getString("ORDNO"));
                redesig.setOrdDate(rs.getString("ORDDT"));
                redesig.setSltDept(rs.getString("DEPT_CODE"));
                redesig.setSltOffice(rs.getString("OFF_CODE"));
                redesig.setSltAuth(rs.getString("AUTH"));
                redesig.setNote(rs.getString("NOTE"));
                redesig.setIfVisible(rs.getString("IF_VISIBLE"));
                redesig.setEntDept(rs.getString("ENT_DEPT"));
                redesig.setEntOffice(rs.getString("ENT_OFF"));
                redesig.setEntAuth(rs.getString("ENT_AUTH"));
                redesig.setCadreStatus(rs.getString("ACS"));
                redesig.setSubStatus(rs.getString("ASCS"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return redesig;
    }
    
    public List findAllRedesignation(String empId){
         List list = null;
        Connection con=null;
        PreparedStatement pst=null;
        ResultSet rs=null;
        RedesignationList redepList=null;
        list=new ArrayList();
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("SELECT NOT_ID,NOT_TYPE,DOE,ORDNO,ORDDT FROM EMP_NOTIFICATION WHERE EMP_ID=? AND NOT_TYPE='REDESIGNATION'");
            pst.setString(1, empId);
            rs = pst.executeQuery();
            while (rs.next()) {
                redepList = new RedesignationList();
                redepList.setNotId(rs.getString("NOT_ID"));
                 redepList.setNotType(rs.getString("NOT_TYPE"));
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

