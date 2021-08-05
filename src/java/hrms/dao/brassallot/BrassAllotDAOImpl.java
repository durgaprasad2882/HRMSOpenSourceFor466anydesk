
package hrms.dao.brassallot;

import hrms.common.CommonFunctions;
import hrms.common.DataBaseFunctions;
import hrms.model.brassallot.BrassAllot;
import hrms.model.brassallot.BrassAllotList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;

public class BrassAllotDAOImpl implements BrassAllotDAO{
      @Resource(name = "dataSource")
    protected DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
      public int insertBrassAllotData(BrassAllot brass){
           int n = 0;
        PreparedStatement pst = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            String mcode = CommonFunctions.getMaxCode("EMP_NOTIFICATION", "NOT_ID", con);
            pst = con.prepareStatement("INSERT INTO EMP_NOTIFICATION(NOT_ID,NOT_TYPE,EMP_ID,DOE,TOE,IF_ASSUMED,ORDNO,ORDDT,DEPT_CODE,OFF_CODE,AUTH,NOTE,IF_VISIBLE,ENT_DEPT,ENT_OFF,ENT_AUTH,ACS,ASCS) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            pst.setString(1, mcode);
            pst.setString(2, "BRASS_ALLOT");
            pst.setString(3, brass.getEmpid());
            pst.setString(4, brass.getDoe());
            pst.setString(5, brass.getToe());
            pst.setString(6, brass.getIfAssumed());
            pst.setString(7, brass.getOrdno());
            pst.setString(8, brass.getOrdDate());
            pst.setString(9, brass.getSltDept());
            pst.setString(10, brass.getSltOffice());
            pst.setString(11, brass.getSltAuth());
            pst.setString(12, brass.getNote());
            pst.setString(13, brass.getIfVisible());
            pst.setString(14, brass.getEntDept());
            pst.setString(15, brass.getEntOffice());
            pst.setString(16, brass.getEntAuth());
            pst.setString(17, brass.getCadreStatus());
            pst.setString(18, brass.getSubStatus());
            n = pst.executeUpdate();
            String brCode = CommonFunctions.getMaxCode("EMP_BRASSALLOT", "BR_ID", con);
            pst = con.prepareStatement("INSERT INTO EMP_BRASSALLOT(BR_ID,NOT_ID,NOT_TYPE,EMP_ID,DIST_CODE,WEFDT,WEFTIME)VALUES(?,?,?,?,?,?,?)");
            pst.setString(1, brCode);
            pst.setString(2, mcode);
            pst.setString(3,"BRASS_ALLOT");
            pst.setString(4,brass.getEmpid());
            pst.setString(5,brass.getDistCode());
            pst.setString(6,brass.getWefDt());
            pst.setString(7,brass.getWefTime());
            n = pst.executeUpdate(); 
              
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return n; 
      }
    
    public int updateBrassAllotData(BrassAllot brass){
         int n = 0;
        PreparedStatement pst = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("UPDATE  EMP_NOTIFICATION SET NOT_TYPE=?,EMP_ID=?,DOE=?,TOE=?,IF_ASSUMED=?,ORDNO=?,ORDDT=?,DEPT_CODE=?,OFF_CODE=?,AUTH,NOTE=?,IF_VISIBLE=?,ENT_DEPT=?,ENT_OFF=?,ENT_AUTH=?,ACS=?,ASCS=? WHERE NOT_ID=?");
            pst.setString(1, "BRASS_ALLOT");
            pst.setString(2, brass.getEmpid());
            pst.setString(3, brass.getDoe());
            pst.setString(4, brass.getToe());
            pst.setString(5, brass.getIfAssumed());
            pst.setString(6, brass.getOrdno());
            pst.setString(7, brass.getOrdDate());
            pst.setString(8, brass.getSltDept());
            pst.setString(9, brass.getSltOffice());
            pst.setString(10, brass.getSltAuth());
            pst.setString(11, brass.getNote());
            pst.setString(12, brass.getIfVisible());
            pst.setString(13, brass.getEntDept());
            pst.setString(14, brass.getEntOffice());
            pst.setString(15, brass.getEntAuth());
            pst.setString(16, brass.getCadreStatus());
            pst.setString(17, brass.getSubStatus());
            pst.setString(18, brass.getNotId());
            n = pst.executeUpdate();
            pst = con.prepareStatement("UPDATE EMP_BRASSALLOT SET NOT_ID=?,NOT_TYPE=?,EMP_ID=?,DEPT_CODE=?,OFF_CODE=?,NEXT_SPC=? WHERE BR_ID=?");
            pst.setString(1, brass.getNotId());
            pst.setString(2,"BRASS_ALLOT");
            pst.setString(3,brass.getEmpid());
            pst.setString(4,brass.getDistCode());
            pst.setString(5,brass.getWefDt());
            pst.setString(6,brass.getWefTime());
             pst.setString(7, brass.getBrId());
            n = pst.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return n;
    }
    
    public int deleteBrassAllotData(String brassId){
         int n = 0;
        Connection con = null;
        PreparedStatement pst = null;
        Statement st=null;
        ResultSet rs=null;
        String notId=null; 
        try {
            con = dataSource.getConnection();
            st=con.createStatement();
            rs=st.executeQuery("SELECT NOT_ID FROM EMP_BRASSALLOT WHERE BR_ID='"+brassId+"'");
            if(rs.next()){
                notId=rs.getString("NOT_ID");
            }
            pst = con.prepareStatement("DELETE FROM EMP_NOTIFICATION WHERE NOT_ID=?");
            pst.setString(1, notId);
            pst.executeUpdate();
            pst = con.prepareStatement("DELETE FROM EMP_BRASSALLOT WHERE BR_ID=?");
            pst.setString(1, brassId);
            pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(st);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return n;
    }
    
    public BrassAllot editBrassAllotData(String brassId){
        BrassAllot brass = null;
        ResultSet rs = null;
        PreparedStatement pst = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("SELECT NOT_TYPE,EMP_ID,DOE,TOE,IF_ASSUMED,ORDNO,ORDDT,DEPT_CODE,OFF_CODE,AUTH,NOTE,IF_VISIBLE,ENT_DEPT,ENT_OFF,ENT_AUTH,ACS,ASCS FROM EMP_NOTIFICATION WHERE NOT_ID=?");
            pst.setString(1, brassId);
            rs = pst.executeQuery();
            if (rs.next()) {
                brass = new BrassAllot();
                brass.setNotType("BRASS_ALLOT");
                brass.setEmpid(rs.getString("EMP_ID"));
                brass.setDoe(rs.getString("DOE"));
                brass.setToe(rs.getString("TOE"));
                brass.setIfAssumed(rs.getString("IF_ASSUMED"));
                brass.setOrdno(rs.getString("ORDNO"));
                brass.setOrdDate(rs.getString("ORDDT"));
                brass.setSltDept(rs.getString("DEPT_CODE"));
                brass.setSltOffice(rs.getString("OFF_CODE"));
                brass.setSltAuth(rs.getString("AUTH"));
                brass.setNote(rs.getString("NOTE"));
                brass.setIfVisible(rs.getString("IF_VISIBLE"));
                brass.setEntDept(rs.getString("ENT_DEPT"));
                brass.setEntOffice(rs.getString("ENT_OFF"));
                brass.setEntAuth(rs.getString("ENT_AUTH"));
                brass.setCadreStatus(rs.getString("ACS"));
                brass.setSubStatus(rs.getString("ASCS"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return brass;
    }
    
    public List findAllBrassAllot(String empId){
         List list = null;
        Connection con=null;
        PreparedStatement pst=null;
        ResultSet rs=null;
        BrassAllotList brassList=null;
        list=new ArrayList();
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("SELECT NOT_ID,DOE,ORDNO,ORDDT FROM EMP_NOTIFICATION WHERE EMP_ID=? AND NOT_TYPE='BRASS_ALLOT'");
            pst.setString(1, empId);
            rs = pst.executeQuery();
            while (rs.next()) {
                brassList = new BrassAllotList();
                brassList.setNotId(rs.getString("NOT_ID"));
                brassList.setNotType("BRASS_ALLOT");
                brassList.setDoe(rs.getString("DOE"));
                brassList.setOrdno(rs.getString("ORDNO"));
                brassList.setOrdDate(rs.getString("ORDDT"));
                list.add(brassList);
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
