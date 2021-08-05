package hrms.dao.leaveapply;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import hrms.SelectOption;
import hrms.common.CommonFunctions;
import hrms.common.DataBaseFunctions;
import hrms.common.SMSThread;
import hrms.model.leave.AttachmentHelperBean;
import hrms.model.leave.Leave;
import hrms.model.leave.LeaveApply;
import hrms.model.leave.LeaveEntrytakenBean;
import hrms.model.leave.LeaveFlowDtls;
import hrms.model.leave.LeaveListDtlsBean;
import hrms.model.leave.LeaveSancBean;
import hrms.model.leave.LeaveWsBean;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;

public class LeaveApplyDAOImpl implements LeaveApplyDAO {

    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    protected MaxLeaveIdDAOImpl maxleaveidDao;

    public MaxLeaveIdDAOImpl getMaxleaveidDao() {
        return maxleaveidDao;
    }

    public void setMaxleaveidDao(MaxLeaveIdDAOImpl maxleaveidDao) {
        this.maxleaveidDao = maxleaveidDao;
    }

    @Override
    public List getLeaveApplyList(String empId) {
        List a1 = new ArrayList();
        ResultSet rs = null;
        Statement st = null;
        LeaveListDtlsBean lldb = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            st = con.createStatement();
            rs = st.executeQuery("SELECT  (DATE_PART('year', APPROVE_TDATE::date) - DATE_PART('year', APPROVE_FDATE::date)) * 12 +   (DATE_PART('month', APPROVE_TDATE::date) - DATE_PART('month', APPROVE_FDATE::date))    "
                    + "AS DIFFDATE,G_PROCESS_STATUS.STATUS_ID,TAB.TASK_ID TASK_ID,LEAVEID,EMP_ID,FDATE,TDATE,APPROVE_FDATE,APPROVE_TDATE,TOL_ID,SUBMITEDTO,SUBMIT_AUTH,STATUS_NAME,INITIATED_ON,IS_SEEN,IS_EXTENDED,REF_ID FROM(SELECT WORKFLOW.TASK_ID,LEAVEID,EMP_ID,FDATE,TDATE,APPROVE_FDATE,APPROVE_TDATE,TOL_ID,SUBMITEDTO,SUBMIT_AUTH,STATUS_ID,INITIATED_ON,IS_SEEN,IS_EXTENDED,REF_ID FROM "
                    + "(SELECT TASK_ID,LEAVEID,EMP_ID,FDATE,TDATE,APPROVE_FDATE,APPROVE_TDATE,TOL_ID,SUBMITEDTO,SUBMIT_AUTH,IS_EXTENDED,REF_ID FROM HW_EMP_LEAVE WHERE EMP_ID='" + empId + "')WORKFLOW  "
                    + "INNER JOIN TASK_MASTER ON TASK_MASTER.TASK_ID=WORKFLOW.TASK_ID  )TAB INNER JOIN G_PROCESS_STATUS ON G_PROCESS_STATUS.STATUS_ID=TAB.STATUS_ID  ORDER BY INITIATED_ON DESC");
            while (rs.next()) {
                lldb = new LeaveListDtlsBean();
                lldb.setDateOfInitiation(CommonFunctions.getFormattedOutputDate3(rs.getDate("INITIATED_ON")));
                if (rs.getString("DIFFDATE") != null) {
                    int days = Integer.parseInt(rs.getString("DIFFDATE"));
                    lldb.setApproveDaysDiff("" + (days + 1));
                } else {
                    lldb.setApproveDaysDiff("");
                }
                lldb.setLeaveType(getLeaveType(rs.getString("TOL_ID")));
                lldb.setFromDate(CommonFunctions.getFormattedOutputDate3(rs.getDate("FDATE")));
                lldb.setToDate(CommonFunctions.getFormattedOutputDate3(rs.getDate("TDATE")));
                if (rs.getDate("APPROVE_FDATE") != null) {
                    lldb.setTxtApproveFrom(CommonFunctions.getFormattedOutputDate3(rs.getDate("APPROVE_FDATE")));
                } else {
                    lldb.setTxtApproveFrom("");
                }
                if (rs.getDate("APPROVE_TDATE") != null) {
                    lldb.setTxtApproveTo(CommonFunctions.getFormattedOutputDate3(rs.getDate("APPROVE_TDATE")));
                } else {
                    lldb.setTxtApproveTo("");
                }
                lldb.setSubmittedTo(getNameWithPost(rs.getString("SUBMIT_AUTH")));
                lldb.setLeaveId(rs.getString("LEAVEID"));
                lldb.setTaskId(rs.getString("TASK_ID"));
                lldb.setStatus(rs.getString("STATUS_NAME"));
                lldb.setStatusId(rs.getString("STATUS_ID"));
                if (lldb.getStatusId().equalsIgnoreCase("5")) {
                    lldb.setEnableJoingRpt("Y");
                    lldb.setLeaveExtensionReq("Y");
                } else {
                    lldb.setEnableJoingRpt("N");
                    lldb.setLeaveExtensionReq("N");
                }

                lldb.setIsSeen(rs.getString("IS_SEEN"));
                lldb.setHidAuthEmpId(rs.getString("SUBMITEDTO"));
                lldb.setIsExtended(rs.getString("IS_EXTENDED"));
                if (lldb.getIsExtended().equalsIgnoreCase("Y")) {
                    lldb.setEnableJoingRpt("N");
                    lldb.setLeaveExtensionReq("N");
                }
                a1.add(lldb);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }

        return a1;
    }

    public String getLeaveType(String tolId) {
        ResultSet rs = null;
        SelectOption so = null;
        Statement st = null;
        String leaveType = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            st = con.createStatement();
            rs = st.executeQuery("SELECT TOL FROM G_LEAVE WHERE TOL_ID='" + tolId + "'");
            if (rs.next()) {
                leaveType = rs.getString("TOL");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return leaveType;
    }

    public String getNameWithPost(String leaveAuth) {
        ResultSet rs = null;
        SelectOption so = null;
        Statement st = null;
        String authority = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            st = con.createStatement();
            rs = st.executeQuery("SELECT EMPNAME,POST FROM(SELECT GPC,EMPNAME FROM(SELECT CUR_SPC,concat_ws(' ', INITIALS ::text, F_NAME::text, M_NAME::text,L_NAME::text) AS EMPNAME FROM EMP_MAST WHERE CUR_SPC='" + leaveAuth + "')EMPMAST INNER JOIN G_SPC ON G_SPC.SPC=EMPMAST.CUR_SPC)TAB LEFT OUTER JOIN "
                    + "G_POST ON G_POST.POST_CODE=TAB.GPC");
            if (rs.next()) {
                if (rs.getString("POST") != null && !rs.getString("POST").equals("")) {
                    authority = rs.getString("EMPNAME") + "," + rs.getString("POST");
                } else {
                    authority = rs.getString("EMPNAME");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return authority;
    }

    public String getPost(String leaveAuth) {
        ResultSet rs = null;
        SelectOption so = null;
        Statement st = null;
        String authority = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            st = con.createStatement();
            rs = st.executeQuery("SELECT POST FROM(SELECT  GPC FROM G_SPC WHERE SPC='" + leaveAuth + "')TAB LEFT OUTER JOIN G_POST ON G_POST.POST_CODE=TAB.GPC");
            if (rs.next()) {
                if (rs.getString("POST") != null && !rs.getString("POST").equals("")) {
                    authority = rs.getString("POST");
                } else {
                    authority = "";
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return authority;
    }

    @Override
    public boolean saveLeave(LeaveApply lfb) {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        PreparedStatement pst = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmtt = null;
        String startTime = "";
        String leaveId = null;
        String getncode = null;
        Calendar cal = Calendar.getInstance();
        Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
        boolean isLeaveApplied=true;
        try {
            con = dataSource.getConnection();
            st = con.createStatement();
            int mcode = CommonFunctions.getMaxCodeInteger("TASK_MASTER", "TASK_ID", con);
            pst = con.prepareStatement("INSERT INTO TASK_MASTER(TASK_ID, PROCESS_ID, INITIATED_BY, INITIATED_ON, STATUS_ID, PENDING_AT,APPLY_TO,INITIATED_SPC,PENDING_SPC,APPLY_TO_SPC) Values (?,?,?,?,?,?,?,?,?,?)");
            pst.setInt(1, mcode);
            pst.setInt(2, 1);
            pst.setString(3, lfb.getHidempId());
            pst.setTimestamp(4, timestamp);
            pst.setInt(5, 3);
            String str = lfb.getTxtSancAuthority();
            String[] temp;
            String delimiter = "-";
            temp = str.split(delimiter);
            System.out.println(temp[0]);
            System.out.println(temp[1]);
            pst.setString(6, temp[0]);
            pst.setString(7, temp[0]);
            pst.setString(8, lfb.getHidSpcCode());
            pst.setString(9, temp[1]);
            pst.setString(10, temp[1]);
            pst.executeUpdate();
            leaveId = CommonFunctions.getMaxCode("HW_EMP_LEAVE", "LEAVEID", con);
            // getncode = CommonFunctions.getMaxNotIdCode(con);
            //  System.out.println("----getncode------"+getncode);
            pstmt = con.prepareStatement("INSERT INTO HW_EMP_LEAVE(LEAVEID,NOT_TYPE, EMP_ID, LSOT_ID, TOL_ID,FDATE,TDATE,PREFIX_DATE,PREFIX_TO,SUFFIX_FROM,SUFFIX_DATE,IF_MEDICAL,IF_COMMUTED,TASK_ID,SUBMIT_AUTH,SUBMITEDTO,IF_LONGTERM,APPLICANT_NOTE,ADDRESS,PHONENO,IF_HEADQUARTER,REF_ID,IS_EXTENDED,NO_OF_CHILD) Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            pstmt.setString(1, leaveId);
            //  pstmt.setString(2, getncode);
            pstmt.setString(2, "LEAVE");
            pstmt.setString(3, lfb.getHidempId());
            pstmt.setString(4, "01");
            pstmt.setString(5, lfb.getSltleaveType());
            Date fdate = formatter.parse(lfb.getTxtperiodFrom());
            java.sql.Date fdateSql = new java.sql.Date(fdate.getTime());
            pstmt.setDate(6, fdateSql);
            Date tdate = formatter.parse(lfb.getTxtperiodTo());
            java.sql.Date todateSql = new java.sql.Date(tdate.getTime());
            pstmt.setDate(7, todateSql);
            if (lfb.getTxtprefixFrom() != null && !lfb.getTxtprefixFrom().equals("")) {
                Date prefixFrom = formatter.parse(lfb.getTxtprefixFrom());
                java.sql.Date prefixFromSql = new java.sql.Date(prefixFrom.getTime());
                pstmt.setDate(8, prefixFromSql);
            } else {
                pstmt.setDate(8, null);
            }
            if (lfb.getTxtprefixTo() != null && !lfb.getTxtprefixTo().equals("")) {
                Date prefixTo = formatter.parse(lfb.getTxtprefixTo());
                java.sql.Date prefixToSql = new java.sql.Date(prefixTo.getTime());
                pstmt.setDate(9, prefixToSql);
            } else {
                pstmt.setDate(9, null);
            }
            if (lfb.getTxtsuffixFrom() != null && !lfb.getTxtsuffixFrom().equals("")) {
                Date sufixFrom = formatter.parse(lfb.getTxtsuffixFrom());
                java.sql.Date sufixFromSql = new java.sql.Date(sufixFrom.getTime());
                pstmt.setDate(10, sufixFromSql);
            } else {
                pstmt.setDate(10, null);
            }
            if (lfb.getTxtsuffixTo() != null && !lfb.getTxtsuffixTo().equals("")) {
                Date sufixTo = formatter.parse(lfb.getTxtsuffixTo());
                java.sql.Date sufixToSql = new java.sql.Date(sufixTo.getTime());
                pstmt.setDate(11, sufixToSql);
            } else {
                pstmt.setDate(11, null);
            }
            pstmt.setString(12, lfb.getIfmedical());
            pstmt.setString(13, lfb.getIfcommuted());
            pstmt.setInt(14, mcode);
            pstmt.setString(15, temp[1]);
            pstmt.setString(16, temp[0]);
            pstmt.setString(17, lfb.getChkLongTerm());
            pstmt.setString(18, lfb.getTxtnote());
            pstmt.setString(19, lfb.getTxtconaddress());
            // int n=Integer.parseInt(lfb.getTxtphoneNo());
            //  System.out.println("-------" + lfb.getTxtphoneNo());
            if (lfb.getTxtphoneNo() != null && !lfb.getTxtphoneNo().equals("")) {
                pstmt.setDouble(20, Double.parseDouble(lfb.getTxtphoneNo()));
            } else {
                pstmt.setDouble(20, 0);
            }
            pstmt.setString(21, lfb.getHqperrad());
            pstmt.setString(22, lfb.getLeaveId());
            pstmt.setString(23, "");
            if (lfb.getSltChild() != null && !lfb.getSltChild().equals("")) {
                pstmt.setInt(24, Integer.parseInt(lfb.getSltChild()));
            } else {
                pstmt.setInt(24, 0);
            }
            pstmt.executeUpdate();
            if (lfb.getLeaveId() != null) {
                ps = con.prepareStatement("UPDATE HW_EMP_LEAVE SET IS_EXTENDED='Y' WHERE LEAVEID='" + lfb.getLeaveId() + "'");
                ps.executeUpdate();
            }
            if (lfb.getAttachmentid() != null) {
                for (int i = 0; i < lfb.getAttachmentid().length; i++) {
                    pstmtt = con.prepareStatement("UPDATE HW_ATTACHMENTS set TASK_ID=?,ATTACH_FLAG=? WHERE ATT_ID=" + lfb.getAttachmentid()[i]);
                    pstmtt.setInt(1, mcode);
                    pstmtt.setString(2, "M");
                    pstmtt.executeUpdate();
                }
            }
            pst = con.prepareStatement("SELECT MOBILE FROM EMP_MAST WHERE EMP_ID=?");
            pst.setString(1, lfb.getHidAuthEmpId());
            rs = pst.executeQuery();
            if (rs.next()) {
                if (rs.getString("MOBILE") != null && !rs.getString("MOBILE").equals("")) {
                    SMSThread smsthread = new SMSThread(lfb.getHidAuthEmpId(), rs.getString("MOBILE"), "L");
                    Thread t1 = new Thread(smsthread);
                    t1.start();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
            DataBaseFunctions.closeSqlObjects(ps, pst, pstmtt);

        }
return isLeaveApplied;
    }

    @Override
    public String getAuthOffice(String authSpc)  {
        ResultSet rs = null;
        SelectOption so = null;
        Statement st = null;
        String authorityOff = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            st = con.createStatement();
            rs = st.executeQuery("SELECT OFF_EN FROM(SELECT CUR_OFF_CODE FROM EMP_MAST WHERE CUR_SPC='" + authSpc + "') "
                    + "EMPMAST INNER JOIN G_OFFICE ON G_OFFICE.OFF_CODE=EMPMAST.CUR_OFF_CODE");
            if (rs.next()) {
                authorityOff = rs.getString("OFF_EN");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return authorityOff;
    }

    @Override
    public Leave getLeaveData(String taskId, String loginEmpId, String loggedinSpc) {
        Leave leaveForm = null;
        Connection con = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        Statement st = null;
        Statement st1 = null;
        String empStatusStr = null;
        String initiatedBy = null;
        String initiatedSpc = null;
        try {
            con = dataSource.getConnection();
            st = con.createStatement();
            rs = st.executeQuery("SELECT INITIATED_BY,INITIATED_SPC,LEAVEID,FDATE,TDATE,TDATE + interval '1' day as EXTFDATE,APPROVE_FDATE,APPROVE_TDATE,SUFFIX_FROM,SUFFIX_DATE,PREFIX_DATE,PREFIX_TO,TOL_ID,EMP_ID,SUBMIT_AUTH,HWEMP.TASK_ID,APPLICANT_NOTE,ADDRESS,PHONENO,ORD_NO,ORD_DATE,PENDING_SPC ,PENDING_AT,IF_HEADQUARTER,STATUS_ID,JOINING_NOTE,JOIN_TIME_FROM FROM "
                    + "(SELECT LEAVEID,FDATE,TDATE,APPROVE_FDATE,APPROVE_TDATE,SUFFIX_FROM,SUFFIX_DATE,PREFIX_DATE,PREFIX_TO,TOL_ID,EMP_ID,SUBMIT_AUTH,TASK_ID,APPLICANT_NOTE,ADDRESS,PHONENO,ORD_NO,ORD_DATE,IF_HEADQUARTER,JOINING_NOTE,JOIN_TIME_FROM FROM HW_EMP_LEAVE WHERE TASK_ID='" + taskId + "')HWEMP  "
                    + "INNER JOIN TASK_MASTER ON HWEMP.TASK_ID=TASK_MASTER.TASK_ID");
//            System.out.println("SELECT INITIATED_BY,INITIATED_SPC,LEAVEID,FDATE,TDATE,TDATE + interval '1' day as EXTFDATE,APPROVE_FDATE,APPROVE_TDATE,SUFFIX_FROM,SUFFIX_DATE,PREFIX_DATE,PREFIX_TO,TOL_ID,EMP_ID,SUBMIT_AUTH,HWEMP.TASK_ID,APPLICANT_NOTE,ADDRESS,PHONENO,ORD_NO,ORD_DATE,PENDING_SPC ,IF_HEADQUARTER,STATUS_ID,JOINING_NOTE,JOIN_TIME_FROM FROM "
//                    + "(SELECT LEAVEID,FDATE,TDATE,APPROVE_FDATE,APPROVE_TDATE,SUFFIX_FROM,SUFFIX_DATE,PREFIX_DATE,PREFIX_TO,TOL_ID,EMP_ID,SUBMIT_AUTH,TASK_ID,APPLICANT_NOTE,ADDRESS,PHONENO,ORD_NO,ORD_DATE,IF_HEADQUARTER,JOINING_NOTE,JOIN_TIME_FROM FROM HW_EMP_LEAVE WHERE TASK_ID='" + taskId + "')HWEMP  "
//                    + "INNER JOIN TASK_MASTER ON HWEMP.TASK_ID=TASK_MASTER.TASK_ID");
            if (rs.next()) {
                leaveForm = new Leave();
                initiatedBy = rs.getString("INITIATED_BY");
                initiatedSpc = rs.getString("INITIATED_SPC");
                leaveForm.setHidempId(initiatedBy);
                leaveForm.setLeaveId(rs.getString("LEAVEID"));
                leaveForm.setStatusId(rs.getString("STATUS_ID"));
                leaveForm.setHidTaskId(rs.getString("TASK_ID"));
                leaveForm.setTollid(rs.getString("TOL_ID"));
                leaveForm.setTxtperiodFrom(CommonFunctions.getFormattedOutputDate3(rs.getDate("FDATE")));
                leaveForm.setTxtperiodTo(CommonFunctions.getFormattedOutputDate3(rs.getDate("TDATE")));
                leaveForm.setExtendedFromDate(CommonFunctions.getFormattedOutputDate3(rs.getDate("EXTFDATE")));
                if (rs.getDate("APPROVE_FDATE") != null && rs.getDate("APPROVE_TDATE") != null) {
                    leaveForm.setTxtApproveFrom(CommonFunctions.getFormattedOutputDate3(rs.getDate("APPROVE_FDATE")));
                    leaveForm.setTxtApproveTo(CommonFunctions.getFormattedOutputDate3(rs.getDate("APPROVE_TDATE")));
                } else {
                    leaveForm.setTxtApproveFrom(CommonFunctions.getFormattedOutputDate3(rs.getDate("FDATE")));
                    leaveForm.setTxtApproveTo(CommonFunctions.getFormattedOutputDate3(rs.getDate("TDATE")));
                }
                leaveForm.setTxtsuffixFrom(CommonFunctions.getFormattedOutputDate3(rs.getDate("SUFFIX_FROM")));
                leaveForm.setTxtsuffixTo(CommonFunctions.getFormattedOutputDate3(rs.getDate("SUFFIX_DATE")));
                leaveForm.setTxtprefixFrom(CommonFunctions.getFormattedOutputDate3(rs.getDate("PREFIX_DATE")));
                leaveForm.setTxtprefixTo(CommonFunctions.getFormattedOutputDate3(rs.getDate("PREFIX_TO")));
                if (rs.getString("APPLICANT_NOTE") != null && !rs.getString("APPLICANT_NOTE").equals("")) {
                    leaveForm.setTxtnote(rs.getString("APPLICANT_NOTE").toLowerCase());
                }
                if (rs.getString("JOINING_NOTE") != null && !rs.getString("JOINING_NOTE").equals("")) {
                    leaveForm.setJoiningNote(rs.getString("JOINING_NOTE").toLowerCase());
                }
                leaveForm.setJoiningDate(CommonFunctions.getFormattedOutputDate3(rs.getDate("JOIN_TIME_FROM")));
                leaveForm.setSubmittedTo(getNameWithPost(rs.getString("SUBMIT_AUTH")));
                leaveForm.setAuthPost(getPost(rs.getString("PENDING_SPC")));
                leaveForm.setPendingPostWthName(getNameWithPost(rs.getString("PENDING_SPC")));
                if (initiatedBy.equals(loginEmpId)) {
                    leaveForm.setApplicantName(getNameWithPost(loggedinSpc));
                } else {
                    leaveForm.setApplicantName(getNameWithPost(initiatedSpc));
                }

                leaveForm.setOffname(getAuthOffice(rs.getString("SUBMIT_AUTH")));
                leaveForm.setSltleaveType(getLeaveType(rs.getString("TOL_ID")));
                leaveForm.setTxtconaddress(rs.getString("ADDRESS"));
                if (rs.getDouble("PHONENO") > 0) {
                    leaveForm.setTxtphoneNo(rs.getString("PHONENO"));
                } else {
                    leaveForm.setTxtphoneNo("");
                }
                leaveForm.setTxtOrdNo(rs.getString("ORD_NO"));
                leaveForm.setTxtOrdDate(CommonFunctions.getFormattedOutputDate3(rs.getDate("ORD_DATE")));
                if (rs.getString("IF_HEADQUARTER") != null && rs.getString("IF_HEADQUARTER").equals("Y")) {
                    leaveForm.setHqperrad("YES");
                } else {
                    leaveForm.setHqperrad("NO");
                }
                leaveForm.setHidAuthEmpId(rs.getString("PENDING_AT"));
                leaveForm.setHidSpcAuthCode(rs.getString("PENDING_SPC"));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return leaveForm;
    }

    @Override
    public ArrayList getFileName(String taskId, String attFlag) {
        ResultSet rs = null;
        Connection con = null;
        Statement st = null;
        ArrayList attFileList = new ArrayList();
        AttachmentHelperBean ahb = null;
        try {
            con = dataSource.getConnection();
            st = con.createStatement();
            // System.out.println("SELECT * FROM HW_ATTACHMENTS WHERE ATTACH_FLAG='"+attFlag+"' AND TASK_ID='" + taskId + "'");
            rs = st.executeQuery("SELECT * FROM HW_ATTACHMENTS WHERE ATTACH_FLAG='" + attFlag + "' AND TASK_ID='" + taskId + "'");
            while (rs.next()) {
                ahb = new AttachmentHelperBean();
                ahb.setAttFileName(rs.getString("ORIGINAL_FILENAME"));
                ahb.setAttId(rs.getString("ATT_ID"));
                attFileList.add(ahb);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return attFileList;
    }

    @Override
    public String getAuthorityEmpCode(String taskId) {
        ResultSet rs = null;
        Connection con = null;
        Statement st = null;
        String authorityEmpCode = null;
        try {
            con = dataSource.getConnection();
            st = con.createStatement();
            rs = st.executeQuery("SELECT * FROM TASK_MASTER WHERE TASK_ID='" + taskId + "'");
            if (rs.next()) {
                authorityEmpCode = rs.getString("PENDING_AT");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return authorityEmpCode;
    }

    @Override
    public String getStatusId(String taskId) {
        ResultSet rs = null;
        Connection con = null;
        Statement st = null;
        String statusId = null;
        try {
            con = dataSource.getConnection();
            st = con.createStatement();
            rs = st.executeQuery("SELECT STATUS_ID FROM TASK_MASTER WHERE TASK_ID='" + taskId + "'");
            if (rs.next()) {
                statusId = rs.getString("STATUS_ID");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return statusId;
    }

    @Override
    public void updateApproveDate(Leave lfb) {
        PreparedStatement pstt = null;
        PreparedStatement pstmt = null;
        PreparedStatement pst = null;
        Statement st = null;
        ResultSet rs = null;
        String startTime = "";
        int mcode = 0;
        Calendar cal = Calendar.getInstance();
        Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
        Connection con = null;
        try {
            con = dataSource.getConnection();
            st = con.createStatement();
            pstt = con.prepareStatement("UPDATE HW_EMP_LEAVE SET APPROVE_FDATE=?,APPROVE_TDATE=? WHERE TASK_ID=?");
            Date fappdate = formatter.parse(lfb.getTxtApproveFrom());
            java.sql.Date fappdateSql = new java.sql.Date(fappdate.getTime());
            pstt.setDate(1, fappdateSql);
            Date apptodate = formatter.parse(lfb.getTxtApproveTo());
            java.sql.Date fapptodateSql = new java.sql.Date(apptodate.getTime());
            pstt.setDate(2, fapptodateSql);
            pstt.setInt(3, Integer.parseInt(lfb.getHidTaskId()));
            pstt.executeUpdate();

//            if (lfb.getSltActionType().equals("1")) {
//                mcode = CommonFunctions.getMaxCodeInteger("WORKFLOW_LOG", "LOG_ID", con);
//                pstmt = con.prepareStatement("INSERT INTO WORKFLOW_LOG(LOG_ID,TASK_ID, TASK_ACTION_DATE, ACTION_TAKEN_BY, SPC_ONTIME, TASK_STATUS_ID, NOTE,FORWARD_TO,FORWARDED_SPC) Values (?,?,?,?,?,?,?,?,?)");
//                pstmt.setInt(1, mcode);
//                pstmt.setInt(2, Integer.parseInt(lfb.getHidTaskId()));
//                pstmt.setTimestamp(3, timestamp);
//                pstmt.setString(4, lfb.getHidempId());
//                pstmt.setString(5, lfb.getHidSpcCode());
//                pstmt.setInt(6, Integer.parseInt(lfb.getSltActionType()));
//                pstmt.setString(7, lfb.getTxtauthnote());
//                pstmt.setString(8, null);
//                pstmt.setString(9, null);
//                pstmt.executeUpdate();
//                pst = con.prepareStatement("UPDATE TASK_MASTER SET STATUS_ID=? WHERE TASK_ID=?");
//                pst.setInt(1, Integer.parseInt(lfb.getSltActionType()));
//                pst.setInt(2, Integer.parseInt(lfb.getHidTaskId()));
//                pst.executeUpdate();
//            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pstt, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    @Override
    public void updateTaskList(Leave lfb)  {
        PreparedStatement pst = null;
        PreparedStatement pstt = null;
        PreparedStatement pst1 = null;
        PreparedStatement pstmt = null;
        String startTime = "";
        int mcode = 0;
        Calendar cal = Calendar.getInstance();

        Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
        Connection con = null;
        try {
            con = dataSource.getConnection();
            if (lfb.getSltActionType().equals("2")) {
                mcode = CommonFunctions.getMaxCodeInteger("WORKFLOW_LOG", "LOG_ID", con);
                pstmt = con.prepareStatement("INSERT INTO WORKFLOW_LOG(LOG_ID,TASK_ID, TASK_ACTION_DATE, ACTION_TAKEN_BY, SPC_ONTIME, TASK_STATUS_ID, NOTE,FORWARD_TO,FORWARDED_SPC) Values (?,?,?,?,?,?,?,?,?)");
                pstmt.setInt(1, mcode);
                pstmt.setInt(2, Integer.parseInt(lfb.getHidTaskId()));
                pstmt.setTimestamp(3, timestamp);
                pstmt.setString(4, lfb.getHidempId());
                pstmt.setString(5, lfb.getHidSpcCode());
                pstmt.setInt(6, Integer.parseInt(lfb.getSltActionType()));
                pstmt.setString(7, lfb.getTxtauthnote());
                pstmt.setString(8, lfb.getHidAuthEmpId());
                pstmt.setString(9, lfb.getHidSpcAuthCode());
                pstmt.executeUpdate();
            }
            if (!lfb.getSltActionType().equals("2")) {
                mcode = CommonFunctions.getMaxCodeInteger("WORKFLOW_LOG", "LOG_ID", con);
                pstmt = con.prepareStatement("INSERT INTO WORKFLOW_LOG(LOG_ID,TASK_ID, TASK_ACTION_DATE, ACTION_TAKEN_BY, SPC_ONTIME, TASK_STATUS_ID, NOTE,FORWARD_TO,FORWARDED_SPC) Values (?,?,?,?,?,?,?,?,?)");
                pstmt.setInt(1, mcode);
                pstmt.setInt(2, Integer.parseInt(lfb.getHidTaskId()));
                pstmt.setTimestamp(3, timestamp);
                pstmt.setString(4, lfb.getHidempId());
                pstmt.setString(5, lfb.getHidSpcCode());
                pstmt.setInt(6, Integer.parseInt(lfb.getSltActionType()));
                pstmt.setString(7, lfb.getTxtauthnote());
                pstmt.setString(8, null);
                pstmt.setString(9, null);
                pstmt.executeUpdate();
            }
            if (lfb.getSltActionType().equals("2")) {
                pst = con.prepareStatement("UPDATE TASK_MASTER SET PENDING_AT=?,PENDING_SPC=? WHERE TASK_ID=?");
                pst.setString(1, lfb.getHidAuthEmpId());
                pst.setString(2, lfb.getHidSpcAuthCode());
                pst.setInt(3, Integer.parseInt(lfb.getHidTaskId()));
                pst.executeUpdate();
                pstt = con.prepareStatement("UPDATE HW_EMP_LEAVE set SUBMITEDTO=?,SUBMIT_AUTH=? WHERE TASK_ID=?");
                pstt.setString(1, lfb.getHidAuthEmpId());
                pstt.setString(2, lfb.getHidSpcAuthCode());
                pstt.setInt(3, Integer.parseInt(lfb.getHidTaskId()));
                pstt.executeUpdate();

            } else {

                pst = con.prepareStatement("UPDATE TASK_MASTER SET STATUS_ID=? WHERE TASK_ID=?");

                pst.setInt(1, Integer.parseInt(lfb.getSltActionType()));

                pst.setInt(2, Integer.parseInt(lfb.getHidTaskId()));
                pst.executeUpdate();

            }

//            if(lfb.getAttachmentid()!=null){
//                for (int i = 0; i < lfb.getAttachmentid().length; i++) {
//                    pst1 = con.prepareStatement("UPDATE HW_ATTACHMENTS set TASK_ID=?,ATTACH_FLAG=? WHERE ATT_ID=" + lfb.getAttachmentid()[i]);
//                    pst1.setInt(1, mcode);
//                    pst1.setString(2, "L");
//                    pst1.executeUpdate();
//                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst, pstmt, pst1, pstt);
        }
    }

    @Override
    public String getApplicant(String taskId){
        ResultSet rs = null;
        Connection con = null;
        Statement st = null;
        String authorityEmpCode = null;
        String empSpc = "";
        String empName = "";

        try {
            con = dataSource.getConnection();
            st = con.createStatement();
            rs = st.executeQuery("SELECT * FROM TASK_MASTER WHERE TASK_ID='" + taskId + "'");
            if (rs.next()) {
                empSpc = rs.getString("INITIATED_SPC");
                empName = getNameWithPost(empSpc);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);

        }
        return empName;
    }

    public ArrayList getLeaveWorkFlowDtls(String taskId)  {

        ResultSet rs = null;
        Statement st = null;
        LeaveFlowDtls lfd = null;
        ArrayList leaveFlowList = new ArrayList();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy' 'HH:MM");
        Connection con = null;
        try {
            con = dataSource.getConnection();
            st = con.createStatement();
            rs = st.executeQuery("SELECT APPROVE_FDATE,APPROVE_TDATE,LOG_ID,WORKFLOWLOG.TASK_ID,TASK_ACTION_DATE,TASK_STATUS_ID,NOTE,FORWARDED_SPC,ACTION_TAKEN_BY, "
                    + " SPC_ONTIME FROM(SELECT LOG_ID,TASK_ID,TASK_ACTION_DATE,TASK_STATUS_ID,NOTE,FORWARDED_SPC,ACTION_TAKEN_BY,SPC_ONTIME FROM  WORKFLOW_LOG "
                    + "  WHERE TASK_ID='" + taskId + "' ORDER BY TASK_ACTION_DATE)WORKFLOWLOG INNER JOIN HW_EMP_LEAVE ON HW_EMP_LEAVE.TASK_ID=WORKFLOWLOG.TASK_ID ORDER BY LOG_ID DESC");
            while (rs.next()) {
                lfd = new LeaveFlowDtls();
                lfd.setTaskId(rs.getString("TASK_ID"));
                if (rs.getString("TASK_STATUS_ID") != null && rs.getString("TASK_STATUS_ID").equals("2")) {
                    lfd.setActionTakenBy(getNameWithPost(rs.getString("FORWARDED_SPC")));
                    lfd.setActionTaken(getNameWithPost(rs.getString("SPC_ONTIME")));
                } else {
                    lfd.setActionTaken(getNameWithPost(rs.getString("SPC_ONTIME")));
                    lfd.setActionTakenBy(getNameWithPost(rs.getString("SPC_ONTIME")));
                }
                if (rs.getString("TASK_STATUS_ID").equals("1")) {
                    lfd.setStatus("Approved By");
                }
                if (rs.getString("TASK_STATUS_ID").equals("0")) {
                    lfd.setStatus("Decline By");
                }
                if (rs.getString("TASK_STATUS_ID").equals("2")) {
                    lfd.setStatus("Forward To");
                }
                if (rs.getString("TASK_STATUS_ID").equals("3")) {
                    lfd.setStatus("Pending At");
                }

                lfd.setTaskdate(CommonFunctions.getFormattedOutputDatAndTime(rs.getTimestamp("TASK_ACTION_DATE")));
                if (rs.getString("TASK_STATUS_ID").equals("1")) {
                    lfd.setTxtApproveFrom(CommonFunctions.getFormattedOutputDate3(rs.getDate("APPROVE_FDATE")));
                    lfd.setTxtApproveTo(CommonFunctions.getFormattedOutputDate3(rs.getDate("APPROVE_TDATE")));
                }

                lfd.setStatusId(rs.getString("TASK_STATUS_ID"));
                lfd.setNote(rs.getString("NOTE"));
                lfd.setActionTakenId(rs.getString("ACTION_TAKEN_BY"));
                // lfd.setAttachments(getLogFileName(con, rs.getInt("LOG_ID")));
                leaveFlowList.add(lfd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);

        }
        return leaveFlowList;
    }

    public boolean getIfLeaveRecordExist(String empid, String leavecode, String fromdate, String todate) {
        ResultSet rs = null;
        SelectOption so = null;
        Connection con = null;
        Statement st = null;
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
        boolean duplPeriod = false;
        boolean status = true;
        String dbf1 = "";
        String dbt1 = "";
        String approveFrmdate = "";
        String approveTodate = "";
        String query = "";
        try {
            con = dataSource.getConnection();
            Date fd1 = formatter.parse(fromdate);
            Date td1 = formatter.parse(todate);
            st = con.createStatement();
            rs = st.executeQuery("SELECT * FROM(SELECT * FROM HW_EMP_LEAVE WHERE EMP_ID= '" + empid + "')HWEMP INNER JOIN TASK_MASTER ON HWEMP.TASK_ID=TASK_MASTER.TASK_ID WHERE STATUS_ID!='0'");

            while (rs.next()) {
                dbf1 = CommonFunctions.getFormattedOutputDate3(rs.getDate("FDATE"));
                dbt1 = CommonFunctions.getFormattedOutputDate3(rs.getDate("TDATE"));
                approveFrmdate = CommonFunctions.getFormattedOutputDate3(rs.getDate("APPROVE_FDATE"));
                approveTodate = CommonFunctions.getFormattedOutputDate3(rs.getDate("APPROVE_TDATE"));

                Date frs1 = formatter.parse(dbf1);
                Date trs1 = formatter.parse(dbt1);

                if ((approveFrmdate != null && !approveFrmdate.equals("")) && (approveTodate != null && !approveTodate.equals(""))) {
                    Date appfrs1 = formatter.parse(approveFrmdate);
                    Date apptrs1 = formatter.parse(approveTodate);
                    if (fd1.compareTo(appfrs1) > 0 && (fd1.compareTo(apptrs1) > 0 && td1.compareTo(apptrs1) > 0)) {
                        duplPeriod = true;

                    } else if ((fd1.compareTo(appfrs1) < 0) && (td1.compareTo(appfrs1) < 0 && td1.compareTo(apptrs1) < 0)) {
                        duplPeriod = true;

                    } else {
                        duplPeriod = false;

                    }
                } else {

                    if (fd1.compareTo(frs1) > 0 && (fd1.compareTo(trs1) > 0 && td1.compareTo(trs1) > 0)) {
                        duplPeriod = true;

                    } else if ((fd1.compareTo(frs1) < 0) && (td1.compareTo(frs1) < 0 && td1.compareTo(trs1) < 0)) {
                        duplPeriod = true;

                    } else {
                        duplPeriod = false;

                    }
                }
                if (duplPeriod == false) {
                    status = false;
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return status;
    }

    public boolean ifEmpExist(String empid) {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        boolean ifEmpExist = true;
        try {
            con = dataSource.getConnection();
            st = con.createStatement();
            rs = st.executeQuery("SELECT * FROM HW_EMP_LEAVE WHERE EMP_ID= '" + empid + "'");
            if (rs.next() == false) {
                ifEmpExist = true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return ifEmpExist;
    }

    public void updateLeaveOrder(Leave leave) {
        Connection con = null;
        PreparedStatement pst = null;
        PreparedStatement pstmt = null;
        PreparedStatement stmt = null;
        PreparedStatement pstt = null;
        ResultSet rs = null;
        int mcode = 0;
        Calendar cal = Calendar.getInstance();
        Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            con = dataSource.getConnection();
            if (leave.getStatusId().equals("41")) {
                stmt = con.prepareStatement("UPDATE HW_EMP_LEAVE SET ORD_NO=?,ORD_DATE=?,IS_SB_UPDATED=? WHERE LEAVEID=?");
                stmt.setString(1, leave.getTxtOrdNo());
                Date ordDate = formatter.parse(leave.getTxtOrdDate());
                java.sql.Date leaveOrdDt = new java.sql.Date(ordDate.getTime());
                stmt.setDate(2, leaveOrdDt);
                stmt.setString(3, "N");
                stmt.setString(4, leave.getLeaveId());
                stmt.executeUpdate();
            }
            //System.out.println("-----"+leave.getStatusId());
            pstmt = con.prepareStatement("UPDATE TASK_MASTER SET STATUS_ID=? WHERE TASK_ID=?");
            if (leave.getStatusId().equals("1")) {
                pstmt.setInt(1, 5);
            }
            if (leave.getStatusId().equals("41")) {
                pstmt.setInt(1, 4);
            }

            pstmt.setInt(2, Integer.parseInt(leave.getHidTaskId()));
            pstmt.executeUpdate();
            if (leave.getStatusId().equals("1")) {
                pstt = con.prepareStatement("UPDATE TASK_MASTER SET PENDING_AT=?,PENDING_SPC=? WHERE TASK_ID=?");
                pstt.setString(1, leave.getHidAuthEmpId());
                pstt.setString(2, leave.getHidSpcAuthCode());
                pstt.setInt(3, Integer.parseInt(leave.getHidTaskId()));
                pstt.executeUpdate();

                //  System.out.println("******** "+leave.getHidAuthEmpId()+"********* "+leave.getHidSpcAuthCode());
                // if ((leave.getHidAuthEmpId() != null && !leave.getHidAuthEmpId().equals("")) && (leave.getHidSpcAuthCode() != null && !lfb.getHidSpcAuthCode().equals(""))) {
                //   System.out.println("-------inside  update 5  -----");
                mcode = CommonFunctions.getMaxCodeInteger("WORKFLOW_LOG", "LOG_ID", con);
                pstmt = con.prepareStatement("INSERT INTO WORKFLOW_LOG(LOG_ID,TASK_ID, TASK_ACTION_DATE, ACTION_TAKEN_BY, SPC_ONTIME, TASK_STATUS_ID, NOTE,FORWARD_TO,FORWARDED_SPC) Values (?,?,?,?,?,?,?,?,?)");
                pstmt.setInt(1, mcode);
                pstmt.setInt(2, Integer.parseInt(leave.getHidTaskId()));
                pstmt.setTimestamp(3, timestamp);
                pstmt.setString(4, leave.getLoginUser());
                pstmt.setString(5, leave.getHidSpcCode());
                pstmt.setInt(6, Integer.parseInt(leave.getSltActionType()));
                pstmt.setString(7, leave.getTxtauthnote());
                pstmt.setString(8, leave.getHidAuthEmpId());
                pstmt.setString(9, leave.getHidSpcAuthCode());
                pstmt.executeUpdate();

            }
            pst = con.prepareStatement("SELECT MOBILE FROM EMP_MAST WHERE EMP_ID=?");
            pst.setString(1, leave.getHidempId());
            rs = pst.executeQuery();
            if (rs.next()) {
                if (rs.getString("MOBILE") != null && !rs.getString("MOBILE").equals("")) {
                    SMSThread smsthread = new SMSThread(leave.getHidempId(), rs.getString("MOBILE"), "S");
                    Thread t1 = new Thread(smsthread);
                    t1.start();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
            DataBaseFunctions.closeSqlObjects(pst, pstmt, stmt);
        }
    }

    @Override
    public void updateClLeaveBalance(String empCode, String tolId, int noOfDays) {
        Connection con = null;
        PreparedStatement stmt = null;
        Statement st = null;
        ResultSet rs = null;
        int available = 0;
        int currElAvailable = 0;
        int carryForward = 0;
        int currAvailable = 0;
        try {
            con = dataSource.getConnection();
            st = con.createStatement();
            //System.out.println("SELECT AVAILABLE,TOL_ID FROM EMP_LEAVE_BALANCE WHERE EMP_ID='" + empCode + "' AND TOL_ID='" + tolId + "'");
            rs = st.executeQuery("SELECT AVAILABLE,TOL_ID FROM EMP_LEAVE_BALANCE WHERE EMP_ID='" + empCode + "' AND TOL_ID='" + tolId + "'");
            if (rs.next()) {
                available = rs.getInt("AVAILABLE");
                currAvailable = available - noOfDays;

                if (available > 0 && currAvailable < 0) {
                    currAvailable = 0;
                    carryForward = noOfDays - available;

                } else if (available <= 0) {
                    currAvailable = 0;
                    carryForward = noOfDays - available;

                }
                if (carryForward > 0) {
                    rs = st.executeQuery("SELECT AVAILABLE,TOL_ID FROM EMP_LEAVE_BALANCE WHERE EMP_ID='" + empCode + "' AND TOL_ID='EL'");
                    if (rs.next()) {
                        available = rs.getInt("AVAILABLE");
                    }
                    currElAvailable = available - carryForward;
                    stmt = con.prepareStatement("UPDATE EMP_LEAVE_BALANCE SET AVAILABLE=? WHERE EMP_ID=? AND TOL_ID='EL'");
                    stmt.setInt(1, currElAvailable);
                    stmt.setString(2, empCode);
                    stmt.executeUpdate();

                }
                stmt = con.prepareStatement("UPDATE EMP_LEAVE_BALANCE SET AVAILABLE=? WHERE EMP_ID=? AND TOL_ID=?");
                stmt.setInt(1, currAvailable);
                stmt.setString(2, empCode);
                stmt.setString(3, tolId);
                stmt.executeUpdate();

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }

    }

    @Override
    public void updateElLeaveBalance(String empCode, String tolId, int noOfDays) {
        Connection con = null;
        PreparedStatement stmt = null;
        Statement st = null;
        ResultSet rs = null;
        int available = 0;
        int currAvailable = 0;
        try {
            con = dataSource.getConnection();
            st = con.createStatement();
            //System.out.println("SELECT AVAILABLE,TOL_ID FROM EMP_LEAVE_BALANCE WHERE EMP_ID='" + empCode + "' AND TOL_ID='" + tolId + "'");
            rs = st.executeQuery("SELECT AVAILABLE,TOL_ID FROM EMP_LEAVE_BALANCE WHERE EMP_ID='" + empCode + "' AND TOL_ID='" + tolId + "'");
            if (rs.next()) {
                available = rs.getInt("AVAILABLE");
                currAvailable = available - noOfDays;
            }
            stmt = con.prepareStatement("UPDATE EMP_LEAVE_BALANCE SET AVAILABLE=? WHERE EMP_ID=? AND TOL_ID=?");
            stmt.setInt(1, currAvailable);
            stmt.setString(2, empCode);
            stmt.setString(3, tolId);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }

    }

    @Override
    public void updateHplLeaveBalance(String empCode, String tolId, int noOfDays) {
        Connection con = null;
        PreparedStatement stmt = null;
        Statement st = null;
        ResultSet rs = null;
        int available = 0;
        int currAvailable = 0;
        long currColAvailable = 0;

        try {
            con = dataSource.getConnection();
            st = con.createStatement();
            //System.out.println("SELECT AVAILABLE,TOL_ID FROM EMP_LEAVE_BALANCE WHERE EMP_ID='" + empCode + "' AND TOL_ID='" + tolId + "'");
            rs = st.executeQuery("SELECT AVAILABLE,TOL_ID FROM EMP_LEAVE_BALANCE WHERE EMP_ID='" + empCode + "' AND TOL_ID='" + tolId + "'");
            if (rs.next()) {
                available = rs.getInt("AVAILABLE");
                currAvailable = available - noOfDays;
                rs = st.executeQuery("SELECT AVAILABLE,TOL_ID FROM EMP_LEAVE_BALANCE WHERE EMP_ID='" + empCode + "' AND TOL_ID='COL'");
                if (rs.next()) {
                    available = rs.getInt("AVAILABLE");
                }
                long colDed = noOfDays / 2;
                currColAvailable = available - colDed;
                stmt = con.prepareStatement("UPDATE EMP_LEAVE_BALANCE SET AVAILABLE=? WHERE EMP_ID=? AND TOL_ID='COL'");
                stmt.setLong(1, currColAvailable);
                stmt.setString(2, empCode);
                stmt.executeUpdate();
            }
            stmt = con.prepareStatement("UPDATE EMP_LEAVE_BALANCE SET AVAILABLE=? WHERE EMP_ID=? AND TOL_ID=?");
            stmt.setInt(1, currAvailable);
            stmt.setString(2, empCode);
            stmt.setString(3, tolId);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }

    }

    @Override
    public void updateCommutedLeaveBalance(String empCode, String tolId, int noOfDays) {
        Connection con = null;
        PreparedStatement stmt = null;
        Statement st = null;
        ResultSet rs = null;
        int available = 0;
        int currAvailable = 0;
        int currHplAvailable = 0;

        try {
            con = dataSource.getConnection();
            st = con.createStatement();
            //System.out.println("SELECT AVAILABLE,TOL_ID FROM EMP_LEAVE_BALANCE WHERE EMP_ID='" + empCode + "' AND TOL_ID='" + tolId + "'");
            rs = st.executeQuery("SELECT AVAILABLE,TOL_ID FROM EMP_LEAVE_BALANCE WHERE EMP_ID='" + empCode + "' AND TOL_ID='" + tolId + "'");
            if (rs.next()) {
                available = rs.getInt("AVAILABLE");
                currAvailable = available - noOfDays;

                rs = st.executeQuery("SELECT AVAILABLE,TOL_ID FROM EMP_LEAVE_BALANCE WHERE EMP_ID='" + empCode + "' AND TOL_ID='HPL'");
                if (rs.next()) {
                    available = rs.getInt("AVAILABLE");
                }
                currHplAvailable = available - (noOfDays * 2);
                stmt = con.prepareStatement("UPDATE EMP_LEAVE_BALANCE SET AVAILABLE=? WHERE EMP_ID=? AND TOL_ID='HPL'");
                stmt.setInt(1, currHplAvailable);
                stmt.setString(2, empCode);
                stmt.executeUpdate();
            }
            stmt = con.prepareStatement("UPDATE EMP_LEAVE_BALANCE SET AVAILABLE=? WHERE EMP_ID=? AND TOL_ID=?");
            stmt.setInt(1, currAvailable);
            stmt.setString(2, empCode);
            stmt.setString(3, tolId);
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }

    }

    @Override
    public void updateMaternityLeaveBalance(String empCode, String tolId, int noOfDays) {
        Connection con = null;
        PreparedStatement stmt = null;
        Statement st = null;
        ResultSet rs = null;
        int available = 0;
        int currAvailable = 0;
        try {
            con = dataSource.getConnection();
            st = con.createStatement();
            //System.out.println("SELECT AVAILABLE,TOL_ID FROM EMP_LEAVE_BALANCE WHERE EMP_ID='" + empCode + "' AND TOL_ID='" + tolId + "'");
            rs = st.executeQuery("SELECT AVAILABLE,TOL_ID FROM EMP_LEAVE_BALANCE WHERE EMP_ID='" + empCode + "' AND TOL_ID='" + tolId + "'");
            if (rs.next()) {
                available = rs.getInt("AVAILABLE");
                currAvailable = available - noOfDays;
            }
            stmt = con.prepareStatement("UPDATE EMP_LEAVE_BALANCE SET AVAILABLE=? WHERE EMP_ID=? AND TOL_ID=?");
            stmt.setInt(1, currAvailable);
            stmt.setString(2, empCode);
            stmt.setString(3, tolId);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }

    }

    @Override
    public void updatePaternityLeaveBalance(String empCode, String tolId, int noOfDays) {
        Connection con = null;
        PreparedStatement stmt = null;
        Statement st = null;
        ResultSet rs = null;
        int available = 0;
        int currAvailable = 0;
        try {
            con = dataSource.getConnection();
            st = con.createStatement();
            //System.out.println("SELECT AVAILABLE,TOL_ID FROM EMP_LEAVE_BALANCE WHERE EMP_ID='" + empCode + "' AND TOL_ID='" + tolId + "'");
            rs = st.executeQuery("SELECT AVAILABLE,TOL_ID FROM EMP_LEAVE_BALANCE WHERE EMP_ID='" + empCode + "' AND TOL_ID='" + tolId + "'");
            if (rs.next()) {
                available = rs.getInt("AVAILABLE");
                currAvailable = available - noOfDays;
            }
            stmt = con.prepareStatement("UPDATE EMP_LEAVE_BALANCE SET AVAILABLE=? WHERE EMP_ID=? AND TOL_ID=?");
            stmt.setInt(1, currAvailable);
            stmt.setString(2, empCode);
            stmt.setString(3, tolId);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }

    }

    /*@Override
     public void updateLeaveBalance(String empCode, String tolId, int noOfDays) {
     Connection con = null;
     PreparedStatement stmt = null;
     Statement st = null;
     ResultSet rs = null;
     int available = 0;
     int currElAvailable = 0;
     int carryForward = 0;
     int currAvailable = 0;
     long currColAvailable = 0;
     int currHplAvailable = 0;

     try {
     con = dataSource.getConnection();
     st = con.createStatement();
     //System.out.println("SELECT AVAILABLE,TOL_ID FROM EMP_LEAVE_BALANCE WHERE EMP_ID='" + empCode + "' AND TOL_ID='" + tolId + "'");
     rs = st.executeQuery("SELECT AVAILABLE,TOL_ID FROM EMP_LEAVE_BALANCE WHERE EMP_ID='" + empCode + "' AND TOL_ID='" + tolId + "'");
     if (rs.next()) {
     available = rs.getInt("AVAILABLE");
     currAvailable = available - noOfDays;
     if (tolId.equals("CL")) {
     if (available > 0 && currAvailable < 0) {
     currAvailable = 0;
     carryForward = noOfDays - available;

     } else if (available <= 0) {
     currAvailable = 0;
     carryForward = noOfDays - available;

     }
     if (carryForward > 0) {
     rs = st.executeQuery("SELECT AVAILABLE,TOL_ID FROM EMP_LEAVE_BALANCE WHERE EMP_ID='" + empCode + "' AND TOL_ID='EL'");
     if (rs.next()) {
     available = rs.getInt("AVAILABLE");
     }
     currElAvailable = available - carryForward;
     stmt = con.prepareStatement("UPDATE EMP_LEAVE_BALANCE SET AVAILABLE=? WHERE EMP_ID=? AND TOL_ID='EL'");
     stmt.setInt(1, currElAvailable);
     stmt.setString(2, empCode);
     stmt.executeUpdate();

     }
     }
     if (tolId.equals("EL")) {
     currAvailable = available - noOfDays;
     }
     if (tolId.equals("HPL")) {
     currAvailable = available - noOfDays;
     rs = st.executeQuery("SELECT AVAILABLE,TOL_ID FROM EMP_LEAVE_BALANCE WHERE EMP_ID='" + empCode + "' AND TOL_ID='COL'");
     if (rs.next()) {
     available = rs.getInt("AVAILABLE");
     }
     long colDed = noOfDays / 2;
     currColAvailable = available - colDed;
     stmt = con.prepareStatement("UPDATE EMP_LEAVE_BALANCE SET AVAILABLE=? WHERE EMP_ID=? AND TOL_ID='COL'");
     stmt.setLong(1, currColAvailable);
     stmt.setString(2, empCode);
     stmt.executeUpdate();
     }
     if (tolId.equals("COL")) {
     currAvailable = available - noOfDays;
     rs = st.executeQuery("SELECT AVAILABLE,TOL_ID FROM EMP_LEAVE_BALANCE WHERE EMP_ID='" + empCode + "' AND TOL_ID='HPL'");
     if (rs.next()) {
     available = rs.getInt("AVAILABLE");
     }
     currHplAvailable = available - (noOfDays * 2);
     stmt = con.prepareStatement("UPDATE EMP_LEAVE_BALANCE SET AVAILABLE=? WHERE EMP_ID=? AND TOL_ID='HPL'");
     stmt.setInt(1, currHplAvailable);
     stmt.setString(2, empCode);
     stmt.executeUpdate();
     }
     }
     //  System.out.println("-currAvailable----" + currAvailable);
     stmt = con.prepareStatement("UPDATE EMP_LEAVE_BALANCE SET AVAILABLE=? WHERE EMP_ID=? AND TOL_ID=?");
     stmt.setInt(1, currAvailable);
     stmt.setString(2, empCode);
     stmt.setString(3, tolId);
     stmt.executeUpdate();

     } catch (Exception e) {
     e.printStackTrace();
     } finally {
     DataBaseFunctions.closeSqlObjects(con);
     }
     }*/
    @Override
    public String getLeaveBalanceInfo(String empCode, String tolId, String year) {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        String availableLeave = "";
        String balance = "";
        String currBalance = "";
        String elLeaveString = "";
        String tolid = "";

        // ArrayList leaveAvail=new ArrayList();
        // LeaveBalanceBean lbb=null;
        try {
            con = dataSource.getConnection();
            st = con.createStatement();
            //System.out.println("SELECT TOL_ID,BALANCE,AVAILABLE,(AVAILABLE-BALANCE)CURRBAL FROM EMP_LEAVE_BALANCE WHERE EMP_ID='" + empCode + "' AND TOL_ID='" + tolId + "'");
            rs = st.executeQuery("SELECT TOL_ID,BALANCE,AVAILABLE,(AVAILABLE-BALANCE)CURRBAL FROM EMP_LEAVE_BALANCE WHERE EMP_ID='" + empCode + "' AND TOL_ID='" + tolId + "'");
            if (rs.next()) {
                tolid = rs.getString("TOL_ID");
                balance = rs.getString("BALANCE");
                availableLeave = rs.getString("AVAILABLE");
                currBalance = rs.getString("CURRBAL");
                if (tolId.equals("CL")) {
                    elLeaveString = availableLeave;
                }
                if (tolId.equals("EL") && Integer.parseInt(currBalance) >= 0) {
                    elLeaveString = balance + "  " + "(" + currBalance + ")";
                }
                if (tolId.equals("EL") && Integer.parseInt(currBalance) < 0) {
                    elLeaveString = availableLeave;
                }
                if (tolId.equals("HPL")) {
                    elLeaveString = rs.getString("AVAILABLE");;
                }
                if (tolId.equals("COL")) {
                    elLeaveString = rs.getString("AVAILABLE");;
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return elLeaveString;
    }

    @Override
    public void getLeaveOpeningBalance(String empid, String tolId, String currDate) {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        Statement st1 = null;
        ResultSet rs1 = null;
        Statement st2 = null;
        Statement st3 = null;
        Statement st4 = null;
        ResultSet rs2 = null;
        ResultSet rs3 = null;
        ResultSet rs4 = null;
        long crCount = 0;
        long totCrLeave = 0;
        PreparedStatement pstmt = null;
        int maximumLimit = 0;
        long debitCount = 0;
        long totdebitCount = 0;
        String currYear = null;
        long leaveAvailable = 0;
        int finalLeaveLimit = 0;
        long totCasualdebitCount = 0;
        long casualdebitCount = 0;
        String currYearStartDate = "";
        String currYearEndDate = "";
        long carryClCredit = 0;
        long totDebtCont = 0;
        long leaveBalance = 0;
        try {
            con = dataSource.getConnection();
            st = con.createStatement();
            st1 = con.createStatement();
            st2 = con.createStatement();
            st3 = con.createStatement();
            st4 = con.createStatement();
            currYear = currDate.substring(7, 11);
            currYearStartDate = "2014-" + "-01-" + "01";
            currYearEndDate = "2014-" + "-12-" + "31";
            rs3 = st3.executeQuery("SELECT * FROM EMP_LEAVE_BALANCE WHERE EMP_ID='" + empid + "'");
            if (rs3.next()) {

            }
            if (!rs3.next()) {
                rs = st.executeQuery("SELECT LCR_ID,EMP_ID,TOL_ID,SP_FROM,SP_TO,COM_MON,CR_COUNT,CR_TYPE,'1' SL FROM EMP_LEAVE_CR WHERE EMP_ID='" + empid + "' AND TOL_ID='" + tolId + "' AND CR_TYPE='G'  ORDER BY SP_FROM");
                if (!rs.next()) {
                    if (tolId.equals("CL")) {
                        leaveBalance = 0;
                        leaveAvailable = 0;
                    }
                    if (tolId.equals("HPL")) {
                        leaveBalance = 0;
                        leaveAvailable = 0;
                    }
                } else {
                    while (rs.next()) {
                        crCount = rs.getInt("CR_COUNT");
                        totCrLeave = totCrLeave + crCount;
                        maximumLimit = getMaxCreditLimitofLeave(rs.getString("TOL_ID"), CommonFunctions.getFormattedOutputDate3(rs.getDate("SP_FROM")), con);
                        finalLeaveLimit = maximumLimit + 15;
//                        System.out.println("SELECT LSOT_ID,FDATE,TDATE, DATE_PART('day', TDATE::timestamp - FDATE::timestamp)+1 DEBIT_COUNT  FROM EMP_LEAVE "
//                                + "WHERE (FDATE BETWEEN '" + rs.getDate("SP_FROM") + "' AND '" + rs.getDate("SP_TO") + "' "
//                                + "OR    TDATE   BETWEEN '" + rs.getDate("SP_FROM") + "' AND '" + rs.getDate("SP_TO") + "') AND EMP_ID='" + empid + "'  AND TOL_ID='" + tolId + "' AND FDATE >='" + rs.getDate("SP_FROM") + "'");
                        rs2 = st2.executeQuery("SELECT LSOT_ID,FDATE,TDATE, DATE_PART('day', TDATE::timestamp - FDATE::timestamp)+1 DEBIT_COUNT  FROM EMP_LEAVE "
                                + "WHERE (FDATE BETWEEN '" + rs.getDate("SP_FROM") + "' AND '" + rs.getDate("SP_TO") + "' "
                                + "OR    TDATE   BETWEEN '" + rs.getDate("SP_FROM") + "' AND '" + rs.getDate("SP_TO") + "') AND EMP_ID='" + empid + "' AND TOL_ID='" + tolId + "' AND FDATE >='" + rs.getDate("SP_FROM") + "'");
                        while (rs2.next()) {
                            debitCount = rs2.getInt("DEBIT_COUNT");
                            totdebitCount = totdebitCount + debitCount;
                            if (rs2.getDate("TDATE").after(rs.getDate("SP_TO"))) {
                                rs4 = st4.executeQuery("SELECT DATE_PART('day', '" + rs.getDate("SP_TO") + "'::timestamp - '" + rs2.getDate("FDATE") + "'::timestamp)+1 DEBIT_COUNT,DATE_PART('day', '" + rs2.getDate("TDATE") + "'::timestamp - '" + rs.getDate("SP_TO") + "'::timestamp) DEBIT_COUNT1");
                                if (rs4.next()) {
                                    totdebitCount = rs4.getLong("DEBIT_COUNT") + rs4.getLong("DEBIT_COUNT1");
                                }
                            }
                        }
                        totCrLeave = totCrLeave - totdebitCount;

                        if (tolId.equals("CL")) {
                            leaveBalance = 10;
                            leaveAvailable = totCrLeave + 10;
                        }
                        if (tolId.equals("EL")) {
                            if (totCrLeave >= maximumLimit) {
                                leaveBalance = maximumLimit;

                            } else {
                                leaveBalance = totCrLeave;
                            }
                            leaveAvailable = leaveBalance + 15;
                        }
                        if (tolId.equals("HPL")) {
                            leaveBalance = totCrLeave;
                            leaveAvailable = totCrLeave + 20;
                        }
                        if (tolId.equals("PL")) {
                            leaveBalance = totCrLeave;
                            leaveAvailable = totCrLeave + 15;
                        }
                        if (tolId.equals("ML")) {
                            leaveBalance = totCrLeave;
                            leaveAvailable = totCrLeave + 180;
                        }
                        //totCrLeave
                        totdebitCount = 0;

                    }
                }
            }
            currYear = currDate.substring(7, 11);
            st1 = con.createStatement();
            //System.out.println("SELECT * FROM EMP_LEAVE_BALANCE WHERE EMP_ID='" + empid + "' AND TOL_ID='" + tolId + "' AND YEAR='" + currYear + "'");
            rs1 = st1.executeQuery("SELECT * FROM EMP_LEAVE_BALANCE WHERE EMP_ID='" + empid + "' AND TOL_ID='" + tolId + "' AND YEAR='" + currYear + "'");

            if (!rs1.next()) {
                pstmt = con.prepareStatement("INSERT INTO EMP_LEAVE_BALANCE(YEAR,MONTH,EMP_ID,TOL_ID,BALANCE,AVAILABLE)VALUES(?,?,?,?,?,?)");
                pstmt.setString(1, currYear);
                pstmt.setString(2, "JAN");
                pstmt.setString(3, empid);
                pstmt.setString(4, tolId);
                pstmt.setLong(5, leaveBalance);
                pstmt.setLong(6, leaveAvailable);
                pstmt.executeUpdate();

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
            DataBaseFunctions.closeSqlObjects(rs, rs1, rs2, rs3);
            DataBaseFunctions.closeSqlObjects(st, st1, st2, st3);
            DataBaseFunctions.closeSqlObjects(pstmt);
        }

    }

    public static int getMaxCreditLimitofLeave(String leaveType, String onDate, Connection con) throws Exception {
        int maximumLimit = 0;
        ResultSet rs = null;
        //Connection con=null;
        Statement st = null;

        try {
            //con=dataSource.getConnection();
            st = con.createStatement();
            // System.out.println("SELECT DISTINCT MAX_CR FROM g_leave WHERE TOL_ID='" + leaveType + "' AND WEF=(SELECT MAX(WEF) FROM g_leave WHERE TOL_ID='" + leaveType + "' AND WEF<='" + onDate + "')");
            rs = st.executeQuery("SELECT DISTINCT MAX_CR FROM g_leave WHERE TOL_ID='" + leaveType + "' AND WEF=(SELECT MAX(WEF) FROM g_leave WHERE TOL_ID='" + leaveType + "' AND WEF<='" + onDate + "')");

            while (rs.next()) {
                if (rs.getString("MAX_CR") != null && !rs.getString("MAX_CR").equals("")) {
                    maximumLimit = Integer.parseInt(rs.getString("MAX_CR"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs);
        }
        return maximumLimit;
    }

    public static long getCarryOverFromCl(Connection con, String empid, String tolId, String currDate, String currYearStartDate, String currYearEndDate) throws Exception {

        Statement st = null;
        ResultSet rs = null;
        int maximumLimit = 0;
        long casualdebitCount = 0;
        long totCasualdebitCount = 0;
        long carryClCredit = 0;
        try {
            st = con.createStatement();
            maximumLimit = getMaxCreditLimitofLeave("CL", currDate, con);
            //  System.out.println("SELECT LSOT_ID,FDATE,TDATE, DATE_PART('day', TDATE::timestamp - FDATE::timestamp)+1 DEBIT_COUNT  FROM EMP_LEAVE WHERE EMP_ID='" + empid + "' AND (FDATE BETWEEN '" + currYearStartDate + "' AND '" + currYearEndDate + "' OR TDATE   BETWEEN '" + currYearStartDate + "' AND '" + currYearEndDate + "')  AND TOL_ID='CL' ORDER BY FDATE");
            rs = st.executeQuery("SELECT LSOT_ID,FDATE,TDATE, DATE_PART('day', TDATE::timestamp - FDATE::timestamp)+1 DEBIT_COUNT  FROM EMP_LEAVE WHERE EMP_ID='" + empid + "' AND (FDATE BETWEEN '" + currYearStartDate + "' AND '" + currYearEndDate + "' OR TDATE   BETWEEN '" + currYearStartDate + "' AND '" + currYearEndDate + "')  AND TOL_ID='CL' ORDER BY FDATE");
            while (rs.next()) {
                casualdebitCount = rs.getInt("DEBIT_COUNT");
                //    System.out.println("---casualdebitCount--" + casualdebitCount);
                totCasualdebitCount = totCasualdebitCount + casualdebitCount;
            }
            //  System.out.println("========CL======totdedcnt=====" + totCasualdebitCount);
            if (totCasualdebitCount >= 10 && totCasualdebitCount <= 15) {
                carryClCredit = totCasualdebitCount - 10;
                //  System.out.println("========CL111111======totdedcnt=====" + carryClCredit);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs);
        }
        return carryClCredit;
    }

    public void updateEmpleave(Leave lfb, String notId) {
        PreparedStatement pst = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmtt = null;
        Connection con = null;
        String leaveId = "";
        //String notId = "";
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
        try {
            con = dataSource.getConnection();
            leaveId = maxleaveidDao.getMaxLeaveId();
            pstmtt = con.prepareStatement("INSERT INTO EMP_LEAVE(LEAVEID, NOT_ID, NOT_TYPE, EMP_ID, LSOT_ID, TOL_ID, FDATE, TDATE,PREFIX_DATE,PREFIX_TO,SUFFIX_FROM,SUFFIX_DATE)VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");
            pstmtt.setString(1, leaveId);
            pstmtt.setString(2, notId);
            pstmtt.setString(3, "LEAVE");
            pstmtt.setString(4, lfb.getEmpId());
            pstmtt.setString(5, "01");
            pstmtt.setString(6, lfb.getTollid());
            if (lfb.getTxtApproveFrom() != null && !lfb.getTxtApproveFrom().equals("")) {
                Date periodFrom = formatter.parse(lfb.getTxtApproveFrom());
                java.sql.Date periodFromSql = new java.sql.Date(periodFrom.getTime());
                pstmtt.setDate(7, periodFromSql);
            } else {
                pstmtt.setDate(7, null);
            }
            if (lfb.getTxtApproveTo() != null && !lfb.getTxtApproveTo().equals("")) {
                Date periodTo = formatter.parse(lfb.getTxtApproveTo());
                java.sql.Date periodToSql = new java.sql.Date(periodTo.getTime());
                pstmtt.setDate(8, periodToSql);
            } else {
                pstmtt.setDate(8, null);
            }

            if (lfb.getTxtprefixFrom() != null && !lfb.getTxtprefixFrom().equals("")) {
                Date prefixFrom = formatter.parse(lfb.getTxtprefixFrom());
                java.sql.Date prefixFromSql = new java.sql.Date(prefixFrom.getTime());
                pstmtt.setDate(9, prefixFromSql);
            } else {
                pstmtt.setDate(9, null);
            }
            if (lfb.getTxtprefixTo() != null && !lfb.getTxtprefixTo().equals("")) {
                Date prefixTo = formatter.parse(lfb.getTxtprefixTo());
                java.sql.Date prefixToSql = new java.sql.Date(prefixTo.getTime());
                pstmtt.setDate(10, prefixToSql);
            } else {
                pstmtt.setDate(10, null);
            }
            if (lfb.getTxtsuffixFrom() != null && !lfb.getTxtsuffixFrom().equals("")) {
                Date sufixFrom = formatter.parse(lfb.getTxtsuffixFrom());
                java.sql.Date sufixFromSql = new java.sql.Date(sufixFrom.getTime());
                pstmtt.setDate(11, sufixFromSql);
            } else {
                pstmtt.setDate(11, null);
            }
            if (lfb.getTxtsuffixTo() != null && !lfb.getTxtsuffixTo().equals("")) {
                Date sufixTo = formatter.parse(lfb.getTxtsuffixTo());
                java.sql.Date sufixToSql = new java.sql.Date(sufixTo.getTime());
                pstmtt.setDate(12, sufixToSql);
            } else {
                pstmtt.setDate(12, null);
            }
            pstmtt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    @Override
    public int calculateDateDiff(String fromdate, String toDate, String empId, String tolid) {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        int dateDiff = 0;
        int holiDiff = 0;
        String query = "";
        String cId = "";
        ResultSet rs1 = null;
        Statement st1 = null;
        ResultSet rs2 = null;
        Statement st2 = null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date d1 = formatter.parse(fromdate);
            Date d2 = formatter.parse(toDate);
            con = dataSource.getConnection();
            st = con.createStatement();
            st1 = con.createStatement();
            // System.out.println("SELECT DATE_PART('day', '" + toDate + "'::timestamp - '" + fromdate + "'::timestamp)+1 DATECOUNT");
            rs = st.executeQuery("SELECT DATE_PART('day', '" + toDate + "'::timestamp - '" + fromdate + "'::timestamp)+1 DATECOUNT");
            if (rs.next()) {
                dateDiff = rs.getInt("DATECOUNT");
            }
            rs1 = st1.executeQuery("SELECT DATE_PART('day', tdate::timestamp - fdate::timestamp)+1 DATECOUNT  FROM G_HOLIDAY  WHERE (fdate, tdate) OVERLAPS ('" + fromdate + "'::DATE, '" + toDate + "'::DATE)");
            
            while (rs1.next()) {
                holiDiff = holiDiff + rs1.getInt("DATECOUNT");
            }
            Calendar c1 = Calendar.getInstance();
            c1.setTime(d1);

            Calendar c2 = Calendar.getInstance();
            c2.setTime(d2);

            int sundays = 0;
            int saturday = 0;
            while (!c1.after(c2)) {
                if ((c1.get(Calendar.DAY_OF_WEEK) == 7) && (c1.get(Calendar.DAY_OF_MONTH) > 7) && (c1.get(Calendar.DAY_OF_MONTH) < 15)) {
                    saturday++;
                }
                if (c1.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                    sundays++;
                }

                c1.add(Calendar.DATE, 1);
            }
           
            if (tolid.equals("CL")) {
                return dateDiff - (saturday + sundays + holiDiff);
            } else {
                return dateDiff;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
            DataBaseFunctions.closeSqlObjects(st, st1);
            DataBaseFunctions.closeSqlObjects(rs, rs1);
        }
        return dateDiff;
    }

    @Override
    public void viewPDFfunc(Document document, Leave leave, String empid)  {

        try {
            String toBeCapped = leave.getSltleaveType().toLowerCase();
            String[] arr = toBeCapped.split(" ");
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < arr.length; i++) {
                sb.append(Character.toUpperCase(arr[i].charAt(0)))
                        .append(arr[i].substring(1)).append(" ");
            }
            String leaveType = sb.toString();
            Font f1 = new Font();
            f1.setSize(8);
            f1.setFamily("Times New Roman");

            PdfPTable table = null;
            table = new PdfPTable(4);
            table.setWidths(new int[]{2, 3, 3, 3});
            table.setWidthPercentage(100);
            PdfPCell cell = null;
            cell = new PdfPCell(new Phrase("Government of Odisha", new Font(Font.FontFamily.TIMES_ROMAN, 13, Font.BOLD)));
            cell.setColspan(4);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(leave.getOffname(), new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.NORMAL)));
            cell.setColspan(4);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("* * *", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
            cell.setColspan(4);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell();
            cell.setColspan(4);
            cell.setFixedHeight(20);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("OFFICE ORDER", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.UNDERLINE)));
            cell.setColspan(4);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Order No. " + leave.getTxtOrdNo() + "," + " Dated " + leave.getTxtOrdDate().toLowerCase(), new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.BOLD)));
            cell.setColspan(4);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell();
            cell.setColspan(4);
            cell.setFixedHeight(20);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            Phrase phrs = new Phrase();
            Chunk c1 = new Chunk(leave.getApplicantName() + "," + leave.getOffname().toLowerCase() + " is granted ", f1);
            Chunk c2 = new Chunk(leaveType, new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.BOLD));
            Chunk c3 = new Chunk("for the period from ", f1);
            Chunk c4 = new Chunk(leave.getTxtApproveFrom(), new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.BOLD));
            Chunk c5 = new Chunk(" to ", f1);
            Chunk c6 = new Chunk(leave.getTxtApproveTo(), new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.BOLD));
            Chunk c7 = new Chunk(".", f1);
            phrs.add(c1);
            phrs.add(c2);
            phrs.add(c3);
            phrs.add(c4);
            phrs.add(c5);
            phrs.add(c6);
            phrs.add(c7);
            cell = new PdfPCell(phrs);
            cell.setColspan(4);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

//            cell = new PdfPCell(new Phrase("No. " + leave.getTxtOrdNo() + " " + leave.getApplicantName() + "," + leave.getOffname().toLowerCase() + " is granted " + p.add(leaveName) + " for the period from " + leave.getTxtApproveFrom() + " to " + leave.getTxtApproveTo() + ".", new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.NORMAL)));
//            cell.setColspan(4);
//            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
//            cell.setBorder(Rectangle.NO_BORDER);
//            table.addCell(cell);
//            cell = new PdfPCell(new Phrase(" to " + leave.getTxtApproveTo() + ".", new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.NORMAL)));
//            cell.setColspan(4);
//            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
//            cell.setBorder(Rectangle.NO_BORDER);
//            table.addCell(cell);
            cell = new PdfPCell();
            cell.setColspan(4);
            cell.setFixedHeight(10);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            cell = new PdfPCell(new Phrase("He is allowed to draw Pay, D.A & other allowances as admissible under the Rule for the above period leave.", new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.NORMAL)));
            cell.setColspan(4);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell();
            cell.setColspan(4);
            cell.setFixedHeight(10);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);

            cell = new PdfPCell(new Phrase("Certified that had " + leave.getApplicantName() + ", not gone on leave, he would have continued in the said post.", new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.NORMAL)));
            cell.setColspan(4);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell();
            cell.setColspan(4);
            cell.setFixedHeight(40);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(leave.getAuthPost(), new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.NORMAL)));
            cell.setColspan(4);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell();
            cell.setColspan(4);
            cell.setFixedHeight(30);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Memo No._____________________ ,/ Dt._______________________", new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.NORMAL)));
            cell.setColspan(4);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Copy forwarded to Accounts – II Branch (in duplicate) for information and necessary action.", new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.NORMAL)));
            cell.setColspan(4);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell();
            cell.setColspan(4);
            cell.setFixedHeight(40);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(leave.getAuthPost(), new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.NORMAL)));
            cell.setColspan(4);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Memo No._____________________ ,/ Dt._______________________", new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.NORMAL)));
            cell.setColspan(4);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Copy forwarded to the person concerned / Guard file for information.", new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.NORMAL)));
            cell.setColspan(4);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell();
            cell.setColspan(4);
            cell.setFixedHeight(40);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(leave.getAuthPost(), new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.NORMAL)));
            cell.setColspan(4);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            document.add(table);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    @Override
    public void viewAllowedPDFfunc(Document document, Leave leave, String empid) {

        try {
            String toBeCapped = leave.getSltleaveType().toLowerCase();
            String[] arr = toBeCapped.split(" ");
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < arr.length; i++) {
                sb.append(Character.toUpperCase(arr[i].charAt(0)))
                        .append(arr[i].substring(1)).append(" ");
            }
            String leaveType = sb.toString();
            Font f1 = new Font();
            f1.setSize(8);
            f1.setFamily("Times New Roman");

            PdfPTable table = null;
            table = new PdfPTable(4);
            table.setWidths(new int[]{2, 3, 3, 3});
            table.setWidthPercentage(100);
            PdfPCell cell = null;
            cell = new PdfPCell(new Phrase("Government of Odisha", new Font(Font.FontFamily.TIMES_ROMAN, 13, Font.BOLD)));
            cell.setColspan(4);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(leave.getOffname(), new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.NORMAL)));
            cell.setColspan(4);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("* * *", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
            cell.setColspan(4);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell();
            cell.setColspan(4);
            cell.setFixedHeight(20);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("OFFICE APPROVAL ORDER", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.UNDERLINE)));
            cell.setColspan(4);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell();
            cell.setColspan(4);
            cell.setFixedHeight(20);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            Phrase phrs = new Phrase();
            Chunk c1 = new Chunk(leave.getApplicantName() + "," + leave.getOffname().toLowerCase() + " is granted ", f1);
            Chunk c2 = new Chunk(leaveType, new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.BOLD));
            Chunk c3 = new Chunk("for the period from ", f1);
            Chunk c4 = new Chunk(leave.getTxtApproveFrom(), new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.BOLD));
            Chunk c5 = new Chunk(" to ", f1);
            Chunk c6 = new Chunk(leave.getTxtApproveTo(), new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.BOLD));
            Chunk c7 = new Chunk(".", f1);
            phrs.add(c1);
            phrs.add(c2);
            phrs.add(c3);
            phrs.add(c4);
            phrs.add(c5);
            phrs.add(c6);
            phrs.add(c7);
            cell = new PdfPCell(phrs);
            cell.setColspan(4);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

//            cell = new PdfPCell(new Phrase("No. " + leave.getTxtOrdNo() + " " + leave.getApplicantName() + "," + leave.getOffname().toLowerCase() + " is granted " + p.add(leaveName) + " for the period from " + leave.getTxtApproveFrom() + " to " + leave.getTxtApproveTo() + ".", new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.NORMAL)));
//            cell.setColspan(4);
//            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
//            cell.setBorder(Rectangle.NO_BORDER);
//            table.addCell(cell);
//            cell = new PdfPCell(new Phrase(" to " + leave.getTxtApproveTo() + ".", new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.NORMAL)));
//            cell.setColspan(4);
//            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
//            cell.setBorder(Rectangle.NO_BORDER);
//            table.addCell(cell);
            cell = new PdfPCell();
            cell.setColspan(4);
            cell.setFixedHeight(10);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            cell = new PdfPCell(new Phrase("He is allowed to draw Pay, D.A & other allowances as admissible under the Rule for the above period leave.", new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.NORMAL)));
            cell.setColspan(4);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell();
            cell.setColspan(4);
            cell.setFixedHeight(10);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);

            cell = new PdfPCell(new Phrase("Certified that had " + leave.getApplicantName() + ", not gone on leave, he would have continued in the said post.", new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.NORMAL)));
            cell.setColspan(4);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell();
            cell.setColspan(4);
            cell.setFixedHeight(40);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(leave.getAuthPost(), new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.NORMAL)));
            cell.setColspan(4);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell();
            cell.setColspan(4);
            cell.setFixedHeight(30);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Memo No._____________________ ,/ Dt._______________________", new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.NORMAL)));
            cell.setColspan(4);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Copy forwarded to Accounts – II Branch (in duplicate) for information and necessary action.", new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.NORMAL)));
            cell.setColspan(4);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell();
            cell.setColspan(4);
            cell.setFixedHeight(40);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(leave.getAuthPost(), new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.NORMAL)));
            cell.setColspan(4);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Memo No._____________________ ,/ Dt._______________________", new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.NORMAL)));
            cell.setColspan(4);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Copy forwarded to the person concerned / Guard file for information.", new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.NORMAL)));
            cell.setColspan(4);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell();
            cell.setColspan(4);
            cell.setFixedHeight(40);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(leave.getAuthPost(), new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.NORMAL)));
            cell.setColspan(4);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            document.add(table);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    @Override
    public LeaveEntrytakenBean getEntryTaken(String empId) {
        LeaveEntrytakenBean leb = null;
        Connection con = null;
        ResultSet rs = null;
        Statement st = null;
        try {
            con = dataSource.getConnection();
            st = con.createStatement();

            rs = st.executeQuery("SELECT CUR_OFF_CODE,DEPARTMENT_CODE,CUR_SPC FROM(SELECT CUR_OFF_CODE,CUR_SPC FROM EMP_MAST WHERE EMP_ID='" + empId + "')EMPMAST  "
                    + "INNER JOIN G_OFFICE ON G_OFFICE.OFF_CODE=EMPMAST.CUR_OFF_CODE");
            while (rs.next()) {
                leb = new LeaveEntrytakenBean();
                leb.setDeptCode(rs.getString("DEPARTMENT_CODE"));
                leb.setOffcode(rs.getString("CUR_OFF_CODE"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return leb;
    }

    @Override
    public LeaveSancBean getLeaveSancInfo(String taskId)  {
        LeaveSancBean lsb = null;
        Connection con = null;
        ResultSet rs = null;
        Statement st = null;
        try {
            con = dataSource.getConnection();
            st = con.createStatement();
            // System.out.println("SELECT DATE_PART('day', '" + toDate + "'::timestamp - '" + fromdate + "'::timestamp)+1 DATECOUNT");
            rs = st.executeQuery("SELECT EMP_ID,TOL_ID,APPROVE_FDATE,APPROVE_TDATE,SUFFIX_FROM,SUFFIX_DATE,PREFIX_DATE,PREFIX_TO,DEPARTMENT_CODE,CUR_OFF_CODE,SUBMIT_AUTH FROM "
                    + "(SELECT HWLEAVE.EMP_ID,TOL_ID,APPROVE_FDATE,APPROVE_TDATE,SUFFIX_FROM,SUFFIX_DATE,PREFIX_DATE,PREFIX_TO,CUR_OFF_CODE,SUBMIT_AUTH FROM(SELECT EMP_ID,TOL_ID, "
                    + "APPROVE_FDATE,APPROVE_TDATE,SUFFIX_FROM,SUFFIX_DATE,PREFIX_DATE,PREFIX_TO,SUBMITEDTO,SUBMIT_AUTH FROM HW_EMP_LEAVE WHERE TASK_ID='" + taskId + "')HWLEAVE INNER JOIN   "
                    + "EMP_MAST ON EMP_MAST.EMP_ID=HWLEAVE.SUBMITEDTO)TAB INNER JOIN G_OFFICE ON G_OFFICE.OFF_CODE=TAB.CUR_OFF_CODE");
            while (rs.next()) {
                lsb = new LeaveSancBean();
                lsb.setDeptCode(rs.getString("DEPARTMENT_CODE"));
                lsb.setOffCode(rs.getString("CUR_OFF_CODE"));
                lsb.setAuthCode(rs.getString("SUBMIT_AUTH"));
                lsb.setInitiatedEmpId(rs.getString("EMP_ID"));
                lsb.setTolId(rs.getString("TOL_ID"));
                lsb.setFromDate(rs.getString("APPROVE_FDATE"));
                lsb.setToDate(rs.getString("APPROVE_TDATE"));
                lsb.setPrefixFrom(rs.getString("PREFIX_DATE"));
                lsb.setPrefixTo(rs.getString("PREFIX_TO"));
                lsb.setSuffixeFrom(rs.getString("SUFFIX_FROM"));
                lsb.setSuffixeTo(rs.getString("SUFFIX_DATE"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
            DataBaseFunctions.closeSqlObjects(st);
            DataBaseFunctions.closeSqlObjects(rs);
        }
        return lsb;
    }

    @Override
    public boolean maxPeriodCount(LeaveApply leaveForm) {
        Connection con = null;
        Statement st = null;
        Statement stmt = null;
        ResultSet rs = null;
        ResultSet rset = null;
        int dayCnt = 0;
        int maxPeriod = 0;
        boolean leaveAvail = false;
        try {
            con = dataSource.getConnection();
            st = con.createStatement();
            stmt = con.createStatement();
            rs = st.executeQuery("SELECT  DATE_PART('day', '" + leaveForm.getTxtperiodTo() + "'::timestamp - '" + leaveForm.getTxtperiodFrom() + "'::timestamp)+1 DAY_COUNT");
            //  System.out.println("SELECT  DATE_PART('day', '" + leaveForm.getTxtperiodTo() + "'::timestamp - '" + leaveForm.getTxtperiodFrom() + "'::timestamp)+1 DAY_COUNT");
            if (rs.next()) {
                dayCnt = rs.getInt("DAY_COUNT");
            }

            rset = stmt.executeQuery("SELECT MAX( MAX_PERIOD)MAX_PERIOD FROM G_LEAVE WHERE TOL_ID='" + leaveForm.getSltleaveType() + "'");
            // System.out.println("SELECT MAX( MAX_PERIOD)MAX_PERIOD FROM G_LEAVE WHERE TOL_ID='" + leaveForm.getSltleaveType() + "'");
            if (rset.next()) {
                maxPeriod = rset.getInt("MAX_PERIOD");
            }

            if (dayCnt > maxPeriod) {
                leaveAvail = false;
            } else {
                leaveAvail = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
            DataBaseFunctions.closeSqlObjects(st);
            DataBaseFunctions.closeSqlObjects(stmt);
        }
        return leaveAvail;
    }
@Override
    public boolean ifMaxSurviveChild(String tolId,String empId) {
        Connection con = null;
        Statement st = null;
        Statement stmt = null;
        ResultSet rset = null;
        int maxChild = 0;
        boolean ret=false;
        try {
            con = dataSource.getConnection();
            stmt = con.createStatement();
            rset = stmt.executeQuery("SELECT MAX(NO_OF_CHILD)MAX_CHILD FROM HW_EMP_LEAVE WHERE TOL_ID='" + tolId + "' AND EMP_ID='"+empId+"'");
            if (rset.next()) {
                maxChild = rset.getInt("MAX_CHILD");
            }
            if(maxChild==2){
                 ret=false;
            }else{
                ret=true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return ret;
    }
    @Override
    public int maxLeavePeriodCount(String tolId) {
        Connection con = null;
        Statement st = null;
        Statement stmt = null;
        ResultSet rset = null;
        int maxPeriod = 0;

        try {
            con = dataSource.getConnection();
            stmt = con.createStatement();
            rset = stmt.executeQuery("SELECT MAX( MAX_PERIOD)MAX_PERIOD FROM G_LEAVE WHERE TOL_ID='" + tolId + "'");
            if (rset.next()) {
                maxPeriod = rset.getInt("MAX_PERIOD");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return maxPeriod;
    }

    @Override
    public LeaveWsBean[] getEmployeeLeave(String empid, String inputdate) {
        List<LeaveWsBean> lvlist = new ArrayList<>();

        String SQL = "SELECT LSOT, TOL_ID, FDATE, TDATE, SUFFIX_FROM, SUFFIX_DATE, PREFIX_DATE, PREFIX_TO, S_YEAR, T_YEAR, S_DAYS, ORDNO, ORDDT, NOTE FROM "
                + "(SELECT NOTE,NOT_ID,DOE, ORDNO, ORDDT FROM EMP_NOTIFICATION WHERE EMP_ID=? AND NOT_TYPE='LEAVE' AND DOE > ?)EMP_NOTIFICATION "
                + "INNER JOIN EMP_LEAVE ON EMP_NOTIFICATION.NOT_ID=EMP_LEAVE.NOT_ID "
                + "INNER JOIN G_LSOT ON EMP_LEAVE.LSOT_ID=G_LSOT.LSOT_ID";
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            conn = dataSource.getConnection();
            statement = conn.prepareStatement(SQL);
            statement.setString(1, empid);
            statement.setDate(2, new java.sql.Date(CommonFunctions.getDateFromString(inputdate, "yyyy").getTime()));
            result = statement.executeQuery();
            while (result.next()) {
                LeaveWsBean leave = new LeaveWsBean();
                leave.setLsot(result.getString("LSOT"));
                leave.setTol(result.getString("TOL_ID"));
                leave.setFdate(result.getString("FDATE"));
                leave.setTdate(result.getString("TDATE"));
                leave.setSuffixFrom(result.getString("SUFFIX_FROM"));
                leave.setSuffixDate(result.getString("SUFFIX_DATE"));
                leave.setPrefixDate(result.getString("PREFIX_DATE"));
                leave.setPrefixTo(result.getString("PREFIX_TO"));
                leave.setSyear(result.getString("S_YEAR"));
                leave.setTyear(result.getString("T_YEAR"));
                leave.setSdays(result.getString("S_DAYS"));
                leave.setOrdno(result.getString("ORDNO"));
                leave.setOrddate(result.getString("ORDDT"));
                leave.setNote(result.getString("NOTE"));
                lvlist.add(leave);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (result != null) {
                    result.close();
                    result = null;
                }
                if (statement != null) {
                    statement.close();
                    statement = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e) {
                System.out.println("Error while closing the DB connection");
            }
        }
        LeaveWsBean lvarray[] = lvlist.toArray(new LeaveWsBean[lvlist.size()]);
        return lvarray;
    }

    @Override
    public void saveJoinData(Leave leave) {
        Connection conn = null;
        PreparedStatement stmt = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstt = null;
        PreparedStatement pstmtt = null;
        Calendar cal = Calendar.getInstance();
        Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement("UPDATE HW_EMP_LEAVE SET JOIN_TIME_FROM=?,JOINING_NOTE=? WHERE LEAVEID=?");
            //stmt.setString(1, leave.getJoiningDate());
            Date joinDate = formatter.parse(leave.getJoiningDate());
            java.sql.Date joiningDate = new java.sql.Date(joinDate.getTime());
            stmt.setDate(1, joiningDate);
            stmt.setString(2, leave.getJoiningNote());
            stmt.setString(3, leave.getLeaveId());

            stmt.executeUpdate();
            pstmt = conn.prepareStatement("UPDATE TASK_MASTER SET STATUS_ID=? WHERE TASK_ID=?");
            if (leave.getStatusId().equals("1")) {
                pstmt.setInt(1, 5);
            }
            if (leave.getStatusId().equals("5")) {
                pstmt.setInt(1, 41);
            }

            pstmt.setInt(2, Integer.parseInt(leave.getHidTaskId()));
            pstmt.executeUpdate();
            if (leave.getStatusId().equals("5")) {
                pstt = conn.prepareStatement("UPDATE TASK_MASTER SET PENDING_AT=?,PENDING_SPC=? WHERE TASK_ID=?");
                pstt.setString(1, leave.getHidAuthEmpId());
                pstt.setString(2, leave.getHidSpcAuthCode());
                pstt.setInt(3, Integer.parseInt(leave.getHidTaskId()));
                pstt.executeUpdate();

                //  System.out.println("******** "+leave.getHidAuthEmpId()+"********* "+leave.getHidSpcAuthCode());
                // if ((leave.getHidAuthEmpId() != null && !leave.getHidAuthEmpId().equals("")) && (leave.getHidSpcAuthCode() != null && !lfb.getHidSpcAuthCode().equals(""))) {
                //   System.out.println("-------inside  update 5  -----");
//                mcode = CommonFunctions.getMaxCodeInteger("WORKFLOW_LOG", "LOG_ID", con);
//                pstmt = con.prepareStatement("INSERT INTO WORKFLOW_LOG(LOG_ID,TASK_ID, TASK_ACTION_DATE, ACTION_TAKEN_BY, SPC_ONTIME, TASK_STATUS_ID, NOTE,FORWARD_TO,FORWARDED_SPC) Values (?,?,?,?,?,?,?,?,?)");
//                pstmt.setInt(1, mcode);
//                pstmt.setInt(2, Integer.parseInt(leave.getHidTaskId()));
//                pstmt.setTimestamp(3, timestamp);
//                pstmt.setString(4, leave.getHidempId());
//                pstmt.setString(5, leave.getHidSpcCode());
//                pstmt.setInt(6, Integer.parseInt(leave.getSltActionType()));
//                pstmt.setString(7, leave.getTxtauthnote());
//                pstmt.setString(8, leave.getHidAuthEmpId());
//                pstmt.setString(9, leave.getHidSpcAuthCode());
//                pstmt.executeUpdate();
            }
            //int mcode = CommonFunctions.getMaxCodeInteger("TASK_MASTER", "TASK_ID", conn);
            if (leave.getAttachmentid() != null) {
                for (int i = 0; i < leave.getAttachmentid().length; i++) {
                    pstmtt = conn.prepareStatement("UPDATE HW_ATTACHMENTS set TASK_ID=?,ATTACH_FLAG=? WHERE ATT_ID=" + leave.getAttachmentid()[i]);
                    pstmtt.setInt(1, Integer.parseInt(leave.getHidTaskId()));
                    pstmtt.setString(2, "N");
                    pstmtt.executeUpdate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(conn);
        }
    }

//    public void clDayCount(String fromDate, String toDate) {
//        String holiday = "";
//        Connection con = null;
//        Statement st = null;
//        ResultSet rs = null;
//        int dayCnt = 0;
//        try {
//            con = dataSource.getConnection();
//            st = con.createStatement();
//            rs = st.executeQuery("SELECT  DATE_PART('day', '" + fromDate + "'::timestamp - '" + toDate + "'::timestamp)+1 DAY_COUNT");
//            //  System.out.println("SELECT  DATE_PART('day', '" + leaveForm.getTxtperiodTo() + "'::timestamp - '" + leaveForm.getTxtperiodFrom() + "'::timestamp)+1 DAY_COUNT");
//            if (rs.next()) {
//                dayCnt = rs.getInt("DAY_COUNT");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
    public static boolean ifWorkingDay(String empId, String frmDate, Connection con) throws Exception {
        boolean status = false;
        Statement st = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        Statement st1 = null;
        String cId = "";
        String curddmmmyyyy = "";
        String queryForSPC = "";
        String SPC = "";
        String year = frmDate.substring(frmDate.length() - 4, frmDate.length());
        String month = frmDate.substring(3, 6).trim();
        int mon = getMonthAsInteger(month);
        String query = "";
        try {
            st = con.createStatement();
            st1 = con.createStatement();
            DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
            java.util.Date dt = new java.util.Date();
            curddmmmyyyy = dateFormat.format(dt);

            queryForSPC = "SELECT CID FROM G_CAL_POST WHERE SPC='" + SPC + "'";

            rs = st.executeQuery(queryForSPC);
            while (rs.next()) {
                cId = rs.getString("CID");
            }
            query = "SELECT G_HOLIDAY.HID,G_HOLIDAY.FDATE, G_HOLIDAY.TDATE,(G_HOLIDAY.TDATE-G_HOLIDAY.FDATE)+1 LENGTH,G_HOLIDAY.HTYPE  FROM G_CAL_HOLIDAYS INNER JOIN G_HOLIDAY ON G_CAL_HOLIDAYS.HID=G_HOLIDAY.HID AND G_CAL_HOLIDAYS.CYEAR='" + year + "' AND TO_char(G_HOLIDAY.FDATE,'MM')=" + mon + " AND G_CAL_HOLIDAYS.CID='" + cId + "'  AND (G_HOLIDAY.HTYPE='G' OR (G_HOLIDAY.HID IN (SELECT HID FROM EMP_CALENDAR WHERE EMP_ID='" + empId + "' AND CID='" + cId + "'))) WHERE TO_DATE('" + frmDate + "') >= FDATE AND TO_DATE('" + frmDate + "')<=TDATE";
            //System.out.println("---------" + query);
            Calendar prefixFromdate = null;
            Calendar c = Calendar.getInstance();
            prefixFromdate = Calendar.getInstance();
            Date d = new Date(frmDate);
            prefixFromdate.setTime(d);
            int dayofWeek = prefixFromdate.get(prefixFromdate.DAY_OF_WEEK);
            c.setTime(d);
            c.set(Calendar.WEEK_OF_MONTH, 2);
            c.set(c.DAY_OF_WEEK, c.SATURDAY);
            if (d.equals(c.getTime())) {

                status = false;
            } else if (dayofWeek == 1) {

                status = false;
            } else {

                rs1 = st1.executeQuery(query);

                if (rs1.next()) {

                    status = false;
                } else {
                    status = true;
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, st);
        }
        return status;
    }

    public static int getMonthAsInteger(String month) {
        int mon = 0;
        if (month.equalsIgnoreCase("JAN")) {
            mon = 1;
        }
        if (month.equalsIgnoreCase("FEB")) {
            mon = 2;
        }
        if (month.equalsIgnoreCase("MAR")) {
            mon = 3;
        }
        if (month.equalsIgnoreCase("APR")) {
            mon = 4;
        }
        if (month.equalsIgnoreCase("MAY")) {
            mon = 5;
        }
        if (month.equalsIgnoreCase("JUN")) {
            mon = 6;
        }
        if (month.equalsIgnoreCase("JUL")) {
            mon = 7;
        }
        if (month.equalsIgnoreCase("AUG")) {
            mon = 8;
        }
        if (month.equalsIgnoreCase("SEP")) {
            mon = 9;
        }
        if (month.equalsIgnoreCase("OCT")) {
            mon = 10;
        }
        if (month.equalsIgnoreCase("NOV")) {
            mon = 11;
        }
        if (month.equalsIgnoreCase("DEC")) {
            mon = 12;
        }
        return mon;
    }
}
