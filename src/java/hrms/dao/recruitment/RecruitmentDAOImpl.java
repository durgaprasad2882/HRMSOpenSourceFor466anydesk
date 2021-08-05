package hrms.dao.recruitment;

import hrms.common.CommonFunctions;
import hrms.common.DataBaseFunctions;
import hrms.model.recruitment.Recruitment;
import hrms.model.recruitment.RecruitmentModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;

public class RecruitmentDAOImpl implements RecruitmentDAO{
    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
     public int insertRecruitmentData(RecruitmentModel recruit){
         int n = 0;
        PreparedStatement pst = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            String mcode = CommonFunctions.getMaxCode("EMP_NOTIFICATION", "NOT_ID", con);
            pst = con.prepareStatement("INSERT INTO EMP_NOTIFICATION(NOT_ID,NOT_TYPE,EMP_ID,DOE,TOE,IF_ASSUMED,ORDNO,ORDDT,DEPT_CODE,OFF_CODE,AUTH,NOTE,IF_VISIBLE,ENT_DEPT,ENT_OFF,ENT_AUTH,ACS,ASCS) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            pst.setString(1, mcode);
            pst.setString(2, "RECRUITMENT");
            pst.setString(3, recruit.getEmpid());
            pst.setString(4, recruit.getDoe());
            pst.setString(5, recruit.getToe());
            pst.setString(6, recruit.getIfAssumed());
            pst.setString(7, recruit.getOrdno());
            pst.setString(8, recruit.getOrdDate());
            pst.setString(9, recruit.getSltDept());
            pst.setString(10, recruit.getSltOffice());
            pst.setString(11, recruit.getSltAuth());
            pst.setString(12, recruit.getNote());
            pst.setString(13, recruit.getIfVisible());
            pst.setString(14, recruit.getEntDept());
            pst.setString(15, recruit.getEntOffice());
            pst.setString(16, recruit.getEntAuth());
            pst.setString(17, recruit.getCadreStatus());
            pst.setString(18, recruit.getSubStatus());
            n = pst.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return n; 
     }
    
    public int updateRecruitmentData(RecruitmentModel recruit){
        int n = 0;
        PreparedStatement pst = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("UPDATE  EMP_NOTIFICATION SET NOT_TYPE=?,EMP_ID=?,DOE=?,TOE=?,IF_ASSUMED=?,ORDNO=?,ORDDT=?,DEPT_CODE=?,OFF_CODE=?,AUTH,NOTE=?,IF_VISIBLE=?,ENT_DEPT=?,ENT_OFF=?,ENT_AUTH=?,ACS=?,ASCS=? WHERE NOT_ID=?");
            pst.setString(1, "RECRUITMENT");
            pst.setString(2, recruit.getEmpid());
            pst.setString(3, recruit.getDoe());
            pst.setString(4, recruit.getToe());
            pst.setString(5, recruit.getIfAssumed());
            pst.setString(6, recruit.getOrdno());
            pst.setString(7, recruit.getOrdDate());
            pst.setString(8, recruit.getSltDept());
            pst.setString(9, recruit.getSltOffice());
            pst.setString(10, recruit.getSltAuth());
            pst.setString(11, recruit.getNote());
            pst.setString(12, recruit.getIfVisible());
            pst.setString(13, recruit.getEntDept());
            pst.setString(14, recruit.getEntOffice());
            pst.setString(15, recruit.getEntAuth());
            pst.setString(16, recruit.getCadreStatus());
            pst.setString(17, recruit.getSubStatus());
            pst.setString(18, recruit.getNotId());
            n = pst.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return n;
    }
    
    public int deleteRecruitment(String recruitId){
        int n = 0;
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("DELETE FROM EMP_NOTIFICATION WHERE NOT_ID=?");
            pst.setString(1, recruitId);
            pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return n;
    }
    
    public RecruitmentModel editRecruitment(String recruitId){
         RecruitmentModel recruit = null;
        ResultSet rs = null;
        PreparedStatement pst = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("SELECT NOT_TYPE,EMP_ID,DOE,TOE,IF_ASSUMED,ORDNO,ORDDT,DEPT_CODE,OFF_CODE,AUTH,NOTE,IF_VISIBLE,ENT_DEPT,ENT_OFF,ENT_AUTH,ACS,ASCS FROM EMP_NOTIFICATION WHERE NOT_ID=?");
            pst.setString(1, recruitId);
            rs = pst.executeQuery();
            if (rs.next()) {
                recruit = new RecruitmentModel();
                recruit.setNotType("RECRUITMENT");
                recruit.setEmpid(rs.getString("EMP_ID"));
                recruit.setDoe(rs.getString("DOE"));
                recruit.setToe(rs.getString("TOE"));
                recruit.setIfAssumed(rs.getString("IF_ASSUMED"));
                recruit.setOrdno(rs.getString("ORDNO"));
                recruit.setOrdDate(rs.getString("ORDDT"));
                recruit.setSltDept(rs.getString("DEPT_CODE"));
                recruit.setSltOffice(rs.getString("OFF_CODE"));
                recruit.setSltAuth(rs.getString("AUTH"));
                recruit.setNote(rs.getString("NOTE"));
                recruit.setIfVisible(rs.getString("IF_VISIBLE"));
                recruit.setEntDept(rs.getString("ENT_DEPT"));
                recruit.setEntOffice(rs.getString("ENT_OFF"));
                recruit.setEntAuth(rs.getString("ENT_AUTH"));
                recruit.setCadreStatus(rs.getString("ACS"));
                recruit.setSubStatus(rs.getString("ASCS"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return recruit;
    }
    
    public List findAllRecruitment(String empId){
         List list = null;
        Connection con=null;
        PreparedStatement pst=null;
        ResultSet rs=null;
        Recruitment recruitment=null;
        list=new ArrayList();
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("SELECT NOT_ID,NOT_TYPE,DOE,ORDNO,ORDDT FROM EMP_NOTIFICATION WHERE EMP_ID=? AND NOT_TYPE='RECRUITMENT'");
            pst.setString(1, empId);
            rs = pst.executeQuery();
            while (rs.next()) {
                recruitment = new Recruitment();
                recruitment.setNotId(rs.getString("NOT_ID"));
                 recruitment.setNotType(rs.getString("NOT_TYPE"));
                recruitment.setDoe(rs.getString("DOE"));
                recruitment.setOrdno(rs.getString("ORDNO"));
                recruitment.setOrdDate(rs.getString("ORDDT"));
                list.add(recruitment);
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
    

