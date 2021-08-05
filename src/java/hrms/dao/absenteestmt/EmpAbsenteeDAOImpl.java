package hrms.dao.absenteestmt;

import hrms.SelectOption;
import hrms.common.CommonFunctions;
import hrms.common.DataBaseFunctions;
import hrms.dao.leaveapply.LeaveApplyDAOImpl;
import hrms.model.absentee.Absentee;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.annotation.Resource;
import javax.sql.DataSource;

public class EmpAbsenteeDAOImpl implements EmpAbsenteeDAO {

    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected LeaveApplyDAOImpl leaveapplyDAO;

    public LeaveApplyDAOImpl getLeaveapplyDAO() {
        return leaveapplyDAO;
    }

    public void setLeaveapplyDAO(LeaveApplyDAOImpl leaveapplyDAO) {
        this.leaveapplyDAO = leaveapplyDAO;
    }

    public static String getServerDoe() {
        String currDate;
        Format formatter;
        formatter = new SimpleDateFormat("dd-MMM-yyyy");
        currDate = formatter.format(new Date());
        return currDate;
    }

    @Override
    public ArrayList getAbseneteeList(String empid, String year, int month) {
        ArrayList emparr = new ArrayList();
        ResultSet rs = null;
        Absentee absForm = null;
        PreparedStatement pst = null;
        String sql = "";
        Connection con = null;
        try {
            con = dataSource.getConnection();
            sql = "SELECT ABS_ID,ABS_FROM,ABS_TO,ENT_EMP,ENT_DATE,TOT_DAY FROM EMP_ABSENTEE where EMP_ID=? and YEAR=? and MONTH=? order by ABS_FROM";
            pst = con.prepareStatement(sql);
            pst.setString(1, empid);
            pst.setString(2, year);
            pst.setInt(3, month);
            rs = pst.executeQuery();

            while (rs.next()) {
                absForm = new Absentee();
                absForm.setAbsid(rs.getString("ABS_ID"));
                absForm.setFromDate(rs.getDate("ABS_FROM"));
                absForm.setToDate(rs.getDate("ABS_TO"));
                absForm.setTotaldays(rs.getInt("TOT_DAY"));
                emparr.add(absForm);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs,pst);
             DataBaseFunctions.closeSqlObjects(con);
        }
        return emparr;
    }
    @Override
    public boolean saveAbsenteeData(Absentee abform) {
        PreparedStatement pst = null;        
        Connection con = null;
        int i;
         abform.setEntDate(new Date());
        try {

            if (getChkDuplicate(abform.getFromDate(), abform.getEmpId()) == false) {
                con = dataSource.getConnection();               
                pst = con.prepareStatement("insert into emp_absentee(ABS_ID,EMP_ID,YEAR,MONTH,ABS_FROM,ABS_TO,ENT_EMP,ENT_DATE,TOT_DAY) values(?,?,?,?,?,?,?,?,?)");
                pst.setBigDecimal(1, getMaxAbsenteeId());
                pst.setString(2, abform.getEmpId());
                pst.setInt(3, abform.getSltyear());
                pst.setInt(4, abform.getSltmonth());
                pst.setDate(5, new java.sql.Date(abform.getFromDate().getTime()));
                pst.setDate(6, new java.sql.Date(abform.getToDate().getTime()));
                pst.setString(7, abform.getEntempId());
                pst.setTimestamp(8, new Timestamp(abform.getEntDate().getTime()));
                pst.setInt(9, abform.getTotaldays());
                pst.executeUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return false;

    }

    public boolean updateAbsenteeData(Absentee abform) {
        PreparedStatement pst = null;
        String mCode = "";
        Connection con = null;
        int i;
        try {
            con = dataSource.getConnection();

            if (abform.getAbsid() != null && !abform.getAbsid().equals("")) {
                pst = con.prepareStatement("update EMP_ABSENTEE set EMP_ID=?, YEAR=?, MONTH=?,ABS_FROM=?,ABS_TO=?,ENT_EMP=?,ENT_DATE=?,TOT_DAY=? where ABS_ID=?");
                if (getChkDuplicate(abform.getFromDate(), abform.getEmpId()) == false) {
                    pst.setString(1, abform.getEmpId());
                    pst.setInt(2, abform.getSltyear());
                    pst.setInt(3, abform.getSltmonth());
                    pst.setDate(4, new java.sql.Date(abform.getFromDate().getTime()));
                    pst.setDate(4, new java.sql.Date(abform.getToDate().getTime()));
                    pst.setString(6, abform.getEntempId());
                    pst.setString(7, getServerDoe());
                    pst.setInt(8, abform.getTotaldays());
                    pst.setInt(9, Integer.parseInt(abform.getAbsid()));
                    pst.executeUpdate();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return false;
    }

    public boolean deleteAbsEmploye(String empId, String absid) {

        PreparedStatement pst = null;
        //PreparedStatement pstUpdateNotification = null;
        PreparedStatement pstgetDeleteDateRange = null;
        PreparedStatement pstChkDateRangeSubset = null;
        PreparedStatement pstChkDateRangeSuperset = null;
        PreparedStatement pstLeave = null;

        Statement st = null;
        ResultSet queryResult = null;
        ResultSet rs = null;
        Connection con = null;
        String notId = null;
        String leaveId = null;
        Date fdate = null;
        Date tdate = null;
        int ret = 0;
        try {
            con = dataSource.getConnection();
            st = con.createStatement();
            //  pstUpdateNotification = con.prepareStatement("UPDATE EMP_NOTIFICATION SET STATUS = ? WHERE NOT_ID = ?");
            pstLeave = con.prepareStatement("SELECT FDATE,TDATE FROM EMP_LEAVE WHERE LEAVEID=?");
            pstgetDeleteDateRange = con.prepareStatement("SELECT ABS_FROM,ABS_TO FROM EMP_ABSENTEE WHERE ABS_ID=?");
            pstgetDeleteDateRange.setString(1, absid);
            queryResult = pstgetDeleteDateRange.executeQuery();
            while (queryResult.next()) {
                fdate = CommonFunctions.getFormattedOutputDate(queryResult.getDate("ABS_FROM"));
                tdate = CommonFunctions.getFormattedOutputDate(queryResult.getDate("ABS_TO"));
            }

            // Check ABSENTEE_PERIOD IS A SUBSET OF LEAVE_PERIOD  or  ABSENTEE _PERIOD = = LEAVE _PERIOD
            pstChkDateRangeSubset = con.prepareStatement("SELECT LEAVEID,NOT_ID FROM EMP_LEAVE WHERE EMP_ID = ? AND LSOT_ID = '01' AND (FDATE <= TO_DATE(?) AND TDATE >= TO_DATE(?))");
            pstChkDateRangeSubset.setString(1, empId);
            pstChkDateRangeSubset.setDate(2, new java.sql.Date(fdate.getTime()));
            pstChkDateRangeSubset.setDate(3, new java.sql.Date(tdate.getTime()));
            queryResult = pstChkDateRangeSubset.executeQuery();
            while (queryResult.next()) {
                notId = queryResult.getString("NOT_ID");
                leaveId = queryResult.getString("LEAVEID");

//                pstUpdateNotification.setString(1, "SANCTIONED");
//                pstUpdateNotification.setString(2, notId);
//                pstUpdateNotification.execute();
                if (leaveId != null && !leaveId.equals("")) {
                    pstLeave.setString(1, leaveId);
                    rs = pstLeave.executeQuery();
                    if (rs.next()) {
                        PreparedStatement pstLeaveSDaysUpdate = null;
                        pstLeaveSDaysUpdate = con.prepareStatement("UPDATE EMP_LEAVE SET S_DAYS=? WHERE LEAVEID=?");
                        pstLeaveSDaysUpdate.setInt(1, calculateDateDiff(CommonFunctions.getFormattedOutputDate2(rs.getDate("FDATE")), CommonFunctions.getFormattedOutputDate2(rs.getDate("TDATE"))));
                        pstLeaveSDaysUpdate.setString(2, leaveId);
                        pstLeaveSDaysUpdate.executeUpdate();
                        DataBaseFunctions.closeSqlObjects(pstLeaveSDaysUpdate);
                    }
                }
            }

            // Check ABSENTEE _PERIOD IS SUPERSET OF LEAVE_PERIOD 
            pstChkDateRangeSuperset = con.prepareStatement("SELECT NOT_ID FROM EMP_LEAVE WHERE EMP_ID = ? AND LSOT_ID = '01' AND ((FDATE > TO_DATE(?) AND TDATE <= TO_DATE(?)) OR (FDATE >= TO_DATE(?) AND TDATE < TO_DATE(?)))");
            pstChkDateRangeSuperset.setString(1, empId);
            pstChkDateRangeSuperset.setDate(2, new java.sql.Date(fdate.getTime()));
            pstChkDateRangeSuperset.setDate(3, new java.sql.Date(tdate.getTime()));
            pstChkDateRangeSuperset.setDate(4, new java.sql.Date(fdate.getTime()));
            pstChkDateRangeSuperset.setDate(5, new java.sql.Date(tdate.getTime()));
            queryResult = pstChkDateRangeSuperset.executeQuery();
            while (queryResult.next()) {
                notId = queryResult.getString("NOT_ID");

//                pstUpdateNotification.setString(1, "SANCTIONED");
//                pstUpdateNotification.setString(2, notId);
//                pstUpdateNotification.execute();
            }

            pst = con.prepareStatement("delete from emp_absentee where abs_id=?");
            pst.setString(1, absid);
            ret = pst.executeUpdate();

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
            DataBaseFunctions.closeSqlObjects(pst, st);
            DataBaseFunctions.closeSqlObjects(rs, queryResult);
            DataBaseFunctions.closeSqlObjects(pstgetDeleteDateRange, pstChkDateRangeSuperset, pstChkDateRangeSubset);
            DataBaseFunctions.closeSqlObjects(pstLeave);
        }
        return true;
    }

    public boolean getChkDuplicate(Date frmdate, String empid) throws Exception {
        boolean ret = false;
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        Statement st = null;        
        try {
            con = dataSource.getConnection();
            st = con.createStatement();
            if (frmdate != null) {
                System.out.println("select ABS_FROM from emp_absentee where ABS_FROM='" + frmdate + "' and emp_id='" + empid + "'");
                rs = st.executeQuery("select ABS_FROM from emp_absentee where ABS_FROM='" + frmdate + "' and emp_id='" + empid + "'");
                if (rs.next()) {
                    ret = true;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, st);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return ret;
    }

    @Override
    public ArrayList getAbseneteeYear(String empid) {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        ArrayList absenteeYear = new ArrayList();
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("SELECT joindate_of_goo FROM EMP_MAST WHERE emp_id=?");
            pst.setString(1, empid);
            rs = pst.executeQuery();
            Date curDate = new Date();
            Date joindate_of_goo = new Date();
            if (rs.next()) {
                joindate_of_goo = rs.getDate("joindate_of_goo");
            }
            Calendar curcalendar = new GregorianCalendar();
            curcalendar.setTime(curDate);
            int curyear = curcalendar.get(Calendar.YEAR);
            Calendar dojCalendar = new GregorianCalendar();
            dojCalendar.setTime(joindate_of_goo);
            int dojyear = dojCalendar.get(Calendar.YEAR);
            for (int i = dojyear; i <= curyear; i++) {
                SelectOption so = new SelectOption();
                so.setLabel(i + "");
                so.setValue(i + "");
                absenteeYear.add(so);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return absenteeYear;
    }

    public BigDecimal getMaxAbsenteeId() {
        Statement st = null;
        ResultSet rs = null;        
        Connection con = null;
        BigDecimal mCode = null;
        try {
            con = this.dataSource.getConnection();
            st = con.createStatement();
            rs = st.executeQuery("SELECT MAX(cast ( ABS_ID as BIGINT ))+1 MCODE FROM EMP_ABSENTEE");
            if (rs.next()) {
                mCode = rs.getBigDecimal("MCODE");                
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, st);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return mCode;
    }
    private int calculateDateDiff(String fromdate, String toDate) {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        int dateDiff = 0;
        try {
            con = dataSource.getConnection();
            st = con.createStatement();
            // System.out.println("SELECT DATE_PART('day', '" + toDate + "'::timestamp - '" + fromdate + "'::timestamp)+1 DATECOUNT");
            rs = st.executeQuery("SELECT DATE_PART('day', '" + toDate + "'::timestamp - '" + fromdate + "'::timestamp)+1 DATECOUNT");
            if (rs.next()) {
                dateDiff = rs.getInt("DATECOUNT");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, st);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return dateDiff;
    }
}
