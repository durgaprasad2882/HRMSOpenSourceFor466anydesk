/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.notification;

import hrms.common.DataBaseFunctions;
import hrms.model.notification.NotificationBean;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 *
 * @author Surendra
 */
public class NotificationDAOImpl implements NotificationDAO {

    @Resource(name = "dataSource")
    protected DataSource dataSource;
    protected MaxNotificationIdDAO maxnotiidDao;

    public MaxNotificationIdDAO getMaxnotiidDao() {
        return maxnotiidDao;
    }

    public void setMaxnotiidDao(MaxNotificationIdDAO maxnotiidDao) {
        this.maxnotiidDao = maxnotiidDao;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public String insertNotificationData(NotificationBean nfb) {
        //   pst=con.prepareStatement("insert into emp_notification(NOT_ID,NOT_TYPE,EMP_ID,DOE,ORDNO,ORDDT,DEPT_CODE,OFF_CODE,AUTH,NOTE,TOE,IF_ASSUMED,IF_VISIBLE,ENT_DEPT,ENT_OFF,ENT_AUTH) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        // pst=con.prepareStatement("INSERT INTO EMP_NOTIFICATION(NOT_ID,NOT_TYPE,EMP_ID,DOE,TOE,IF_ASSUMED,ORDNO,ORDDT,DEPT_CODE,OFF_CODE,AUTH,NOTE,IF_VISIBLE,ENT_DEPT,ENT_OFF,ENT_AUTH,ACS,ASCS) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

        Connection con = null;
        PreparedStatement pst = null;
        try {            
            con = this.dataSource.getConnection();
            String loanId = maxnotiidDao.getMaxNotId();
            System.out.println("loanId::::::::"+loanId);
            nfb.setNotid(loanId);
            pst = con.prepareStatement("insert into emp_notification(NOT_ID,NOT_TYPE,EMP_ID,DOE,ORDNO,ORDDT,DEPT_CODE,OFF_CODE,AUTH,NOTE,TOE,IF_ASSUMED,IF_VISIBLE,ENT_DEPT,ENT_OFF,ENT_AUTH,ACS,ASCS) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            pst.setString(1, nfb.getNotid());
            pst.setString(2, nfb.getNottype());
            pst.setString(3, nfb.getEmpId());
            if(nfb.getDateofEntry() != null){
                pst.setTimestamp(4, new Timestamp(nfb.getDateofEntry().getTime()));
            }else{
                pst.setTimestamp(6,null);
            }
            pst.setString(5, nfb.getOrdno());
            if(nfb.getOrdDate() != null){
                pst.setTimestamp(6, new Timestamp(nfb.getOrdDate().getTime()));
            }else{
                pst.setTimestamp(6,null);
            }
            pst.setString(7, nfb.getSancDeptCode());
            pst.setString(8, nfb.getSancOffCode());
            pst.setString(9, nfb.getSancAuthCode());
            pst.setString(10, nfb.getNote());

            pst.setString(11, null);
            pst.setString(12, null);
            pst.setString(13, "Y");

            pst.setString(14, nfb.getEntryDeptCode());
            pst.setString(15, nfb.getEntryOffCode());
            pst.setString(16, nfb.getEntryAuthCode());
            pst.setString(17, null);
            pst.setString(18, null);
            pst.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return nfb.getNotid();
    }

    @Override
    public int modifyNotificationData(NotificationBean nfb) {

        Connection con = null;
        PreparedStatement pst = null;
        int returnUpdate = 0;
        try {
            con = this.dataSource.getConnection();
            pst = con.prepareStatement("UPDATE emp_notification set ORDNO=? ,ORDDT=?, DEPT_CODE=?, OFF_CODE=?, AUTH=?, NOTE=?, IF_ASSUMED=?, IF_VISIBLE=?, ENT_DEPT=?, ENT_OFF=?, ENT_AUTH=? WHERE NOT_ID=?");
            pst.setString(1, nfb.getOrdno());
            if(nfb.getOrdDate() != null){
                pst.setTimestamp(2, new Timestamp(nfb.getOrdDate().getTime()));
            }else{
                pst.setTimestamp(2,null);
            }
            pst.setString(3, nfb.getSancDeptCode());
            pst.setString(4, nfb.getSancOffCode());
            pst.setString(5, nfb.getSancAuthCode());
            pst.setString(6, nfb.getNote());

            pst.setString(7, "N");
            pst.setString(8, "Y");

            pst.setString(9, nfb.getEntryDeptCode());
            pst.setString(10, nfb.getEntryOffCode());
            pst.setString(11, nfb.getEntryAuthCode());

            pst.setString(12, nfb.getNotid());
            returnUpdate = pst.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return returnUpdate;
    }

    @Override
    public NotificationBean dispalyNotificationData(String notid, String nottype) {

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = null;

        NotificationBean nfb = new NotificationBean();
        try {
            sql = "SELECT NOT_ID,NOT_TYPE,EMP_ID,DOE,ORDNO,ORDDT,DEPT_CODE,OFF_CODE,AUTH,NOTE,TOE,IF_ASSUMED,IF_VISIBLE,ENT_DEPT,ENT_OFF,ENT_AUTH,ACS,ASCS FROM EMP_NOTIFICATION WHERE NOT_ID=? AND NOT_TYPE=?";
            con = this.dataSource.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, notid);
            ps.setString(2, nottype);
            ps.executeQuery();
            if (rs.next()) {
                nfb.setNotid(notid);
                nfb.setDateofEntry(rs.getDate("DOE"));
                nfb.setDeptCode(rs.getString("DEPT_CODE"));
                nfb.setEmpId(rs.getString("EMP_ID"));
                nfb.setEntryAuthCode(rs.getString("ENT_AUTH"));
                nfb.setEntryDeptCode(rs.getString("ENT_DEPT"));
                nfb.setEntryOffCode(rs.getString("ENT_OFF"));
                nfb.setIfAssumed(rs.getString("IF_ASSUMED"));
                nfb.setIfVisible(rs.getString("IF_VISIBLE"));
                nfb.setNote(rs.getString("NOTE"));
                nfb.setNottype(rs.getString("NOT_TYPE"));
                nfb.setOffCode(rs.getString("OFF_CODE"));
                nfb.setOrdDate(rs.getDate("ORDDT"));
                nfb.setOrdno(rs.getString("ORDNO"));
                nfb.setSancAuthCode(rs.getString("AUTH"));
                nfb.setSancDeptCode(rs.getString("DEPT_CODE"));
                nfb.setSancOffCode(rs.getString("OFF_CODE"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs,ps);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return nfb;
    }

    @Override
    public int deleteNotificationData(String notid, String nottype) {

        Connection con = null;
        PreparedStatement ps = null;
        int returnsuccess = 0;
        try {
            con = this.dataSource.getConnection();
            ps = con.prepareStatement("DELETE FROM EMP_NOTIFICATION WHERE NOT_ID=? AND NOT_TYPE=?");
            ps.setString(1, notid);
            ps.setString(2, nottype);
            returnsuccess = ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(ps);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return returnsuccess;
    }

}
