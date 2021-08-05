/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.task;

import hrms.SelectOption;
import hrms.common.CommonFunctions;
import hrms.common.DataBaseFunctions;
import hrms.model.login.Users;
import hrms.model.parmast.ParMaster;
import hrms.model.task.TaskBean;
import hrms.model.task.TaskListHelperBean;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 *
 * @author Surendra
 */
public class TaskDAOImpl implements TaskDAO {

    @Resource(name = "sessionFactory")
    protected SessionFactory sessionFactory;

    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    protected Session getSession() {
        return sessionFactory.openSession();
    }

    public String getApplicantName(Connection con, String applicant, String prid, int taskid) throws Exception {
        ResultSet rs = null;
        SelectOption so = null;
        Statement st = null;
        String authority = null;
        String sql = "";

        try {
            st = con.createStatement();

            if (prid.equals("3")) {
                sql = "SELECT EMPNAME,POST FROM(SELECT GPC,EMPNAME FROM(SELECT CUR_SPC,ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME,L_NAME], ' ') EMPNAME,EMP_ID FROM EMP_MAST WHERE EMP_ID='" + applicant + "')EMPMAST LEFT OUTER JOIN (SELECT EMP_ID,SPC FROM PAR_MASTER WHERE TASK_ID='" + taskid + "')PAR_MASTER ON EMPMAST.EMP_ID="
                        + " PAR_MASTER.EMP_ID INNER JOIN G_SPC ON G_SPC.SPC=PAR_MASTER.SPC)TAB LEFT OUTER JOIN"
                        + " G_POST ON G_POST.POST_CODE=TAB.GPC";
                rs = st.executeQuery(sql);
                if (rs.next()) {
                    if (rs.getString("POST") != null && !rs.getString("POST").equals("")) {
                        authority = rs.getString("EMPNAME") + "," + rs.getString("POST");
                    } else {
                        authority = rs.getString("EMPNAME");
                    }
                }
            } else {
                sql = "SELECT EMPNAME,POST FROM(SELECT GPC,EMPNAME FROM(SELECT CUR_SPC,ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME,L_NAME], ' ') EMPNAME,EMP_ID FROM EMP_MAST WHERE EMP_ID='" + applicant + "')EMPMAST  "
                        + " INNER JOIN G_SPC ON G_SPC.SPC=EMPMAST.CUR_SPC)TAB LEFT OUTER JOIN"
                        + " G_POST ON G_POST.POST_CODE=TAB.GPC";
                rs = st.executeQuery(sql);
                if (rs.next()) {
                    if (rs.getString("POST") != null && !rs.getString("POST").equals("")) {
                        authority = rs.getString("EMPNAME") + "," + rs.getString("POST");
                    } else {
                        authority = rs.getString("EMPNAME");
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs);
            DataBaseFunctions.closeSqlObjects(st);
        }
        return authority;
    }

    @Override
    public List getMyTaskList(String empId, String logedinUserSpc, String parstatus, int page, int rows, String empname, String gpfno, int processId) {
        Connection con = null;
        ResultSet rs = null;
        Statement st = null;
        ArrayList a1 = new ArrayList();

        //int firstpage = (page > 1) ? ((page - 1) * rows) + 1 : 1;
        int minlimit = rows * (page - 1);
        int maxlimit = rows;
        TaskListHelperBean lldb = null;
        String emp_mast_index = "";
        String task_master_index = "";
        try {
            con = this.dataSource.getConnection();
            st = con.createStatement();
            String sql = "";

            if (empname != null && !empname.equals("")) {
                emp_mast_index = "WHERE F_NAME LIKE '%" + empname.toUpperCase() + "%' OR M_NAME LIKE '%" + empname.toUpperCase() + "%' OR L_NAME LIKE '%" + empname.toUpperCase() + "%' OR ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME,L_NAME], ' ') LIKE '%" + empname.toUpperCase() + "%'";
            } else if (gpfno != null && !gpfno.equals("")) {
                emp_mast_index = "WHERE GPF_NO='" + gpfno + "'";
            }

            if (processId > 0) {
                task_master_index = " AND PROCESS_ID='" + processId + "'";
            }

            if (parstatus != null && !parstatus.equals("")) {
                if (!parstatus.equals("9")) {
                    sql = "SELECT PROCESS_NAME,TASK_MASTER.PROCESS_ID PROCESS_ID,F_NAME,M_NAME,L_NAME,STATUS_NAME,INITIATED_ON,SPN,INITIATED_BY,TASK_MASTER.TASK_ID,TASK_MASTER.STATUS_ID,G_PROCESS_STATUS.IS_COMPLETED FROM (SELECT * FROM (SELECT TASK_ID,STATUS_ID,PROCESS_ID,INITIATED_BY,INITIATED_SPC,INITIATED_ON FROM TASK_MASTER WHERE PENDING_AT='" + empId + "'" + task_master_index + " AND STATUS_ID = '" + parstatus + "')"
                            + " TASK_MASTER LIMIT " + maxlimit + " OFFSET " + minlimit + ")TASK_MASTER"
                            + " LEFT OUTER JOIN G_WORKFLOW_PROCESS ON  TASK_MASTER.PROCESS_ID=G_WORKFLOW_PROCESS.PROCESS_ID"
                            + " INNER JOIN G_PROCESS_STATUS ON TASK_MASTER.STATUS_ID = G_PROCESS_STATUS.STATUS_ID"
                            + " INNER JOIN G_SPC ON TASK_MASTER.INITIATED_SPC = G_SPC.SPC"
                            + " INNER JOIN EMP_MAST ON EMP_MAST.EMP_ID = TASK_MASTER.INITIATED_BY"
                            + " INNER JOIN PAR_MASTER ON TASK_MASTER.TASK_ID=PAR_MASTER.TASK_ID"
                            + " INNER JOIN (SELECT FY,auth_remarks_closed FROM FINANCIAL_YEAR WHERE AUTH_REMARKS_CLOSED='N')FINANCIAL_YEAR ON PAR_MASTER.FISCAL_YEAR=FINANCIAL_YEAR.FY ORDER BY TASK_ID ASC";
                    rs = st.executeQuery(sql);
                    int i = 0;
                    while (rs.next()) {
                        i++;
                        lldb = new TaskListHelperBean();
                        lldb.setCount(i);
                        lldb.setDateOfInitiation(CommonFunctions.getFormattedOutputDate(rs.getDate("INITIATED_ON")));
                        lldb.setDateOfInitiationAsString(CommonFunctions.getFormattedOutputDate1(rs.getDate("INITIATED_ON")));
                        lldb.setProcessname(rs.getString("PROCESS_NAME"));
                        lldb.setApplicant(getApplicantName(con, rs.getString("INITIATED_BY"), rs.getString("PROCESS_ID"), rs.getInt("TASK_ID")));
                        lldb.setTaskId(rs.getInt("TASK_ID"));
                        lldb.setStatusId(rs.getInt("STATUS_ID"));
                        if (lldb.getStatusId() > 0) {
                            if (lldb.getStatusId() == 6) {
                                lldb.setStatus("PENDING AS REPORTING AUTHORITY");
                            } else if (lldb.getStatusId() == 7) {
                                lldb.setStatus("PENDING AS REVIEWING AUTHORITY");
                            } else if (lldb.getStatusId() == 8) {
                                lldb.setStatus("PENDING AS ACCEPTING AUTHORITY");
                            }
                        }
                        lldb.setAppEmpCode(rs.getString("INITIATED_BY"));
                        lldb.setProcessId(rs.getInt("PROCESS_ID"));
                        lldb.setIstaskcompleted(rs.getString("IS_COMPLETED"));
                        /*if(rs.getString("STATUS_ID") != null && rs.getString("STATUS_ID").equals("8")){
                         lldb.setGrading(rs.getString("OVERALLGRADING"));    
                         }*/
                        lldb.setAuth("");
                        a1.add(lldb);
                        Collections.sort(a1);
                    }
                } else if (parstatus.equals("9")) {
                    String reportsql = "SELECT SUBMITTED_ON,ACTIONURL,PAR_MASTER.PARID,PROCESS_NAME,TASK_MASTER.PROCESS_ID PROCESS_ID,F_NAME,M_NAME,L_NAME,STATUS_NAME,INITIATED_ON,"
                            + " SPN,INITIATED_BY,TASK_MASTER.TASK_ID,TASK_MASTER.STATUS_ID,PAR_REPORTING_TRAN.IS_COMPLETED FROM"
                            + " (SELECT PAR_ID,REPORTING_EMP_ID,REPORTING_CUR_SPC,SUBMITTED_ON,IS_COMPLETED FROM PAR_REPORTING_TRAN"
                            + " WHERE REPORTING_EMP_ID='" + empId + "' AND (IS_COMPLETED='Y' OR IS_COMPLETED='F'))PAR_REPORTING_TRAN"
                            + " INNER JOIN PAR_MASTER ON PAR_REPORTING_TRAN.PAR_ID=PAR_MASTER.PARID"
                            + " INNER JOIN TASK_MASTER ON PAR_MASTER.TASK_ID=TASK_MASTER.TASK_ID"
                            + " LEFT OUTER JOIN G_WORKFLOW_PROCESS ON  TASK_MASTER.PROCESS_ID=G_WORKFLOW_PROCESS.PROCESS_ID"
                            + " INNER JOIN G_PROCESS_STATUS ON TASK_MASTER.STATUS_ID = G_PROCESS_STATUS.STATUS_ID"
                            + " INNER JOIN G_SPC ON TASK_MASTER.INITIATED_SPC = G_SPC.SPC"
                            + " INNER JOIN EMP_MAST ON EMP_MAST.EMP_ID = TASK_MASTER.INITIATED_BY ORDER BY TASK_ID";
                    rs = st.executeQuery(reportsql);
                    int i = 0;
                    String actionurl = "";
                    while (rs.next()) {
                        actionurl = rs.getString("ACTIONURL");
                        actionurl = actionurl.substring(1);
                        i++;
                        lldb = new TaskListHelperBean();
                        lldb.setCount(i);
                        lldb.setSubmitted_on(CommonFunctions.getFormattedOutputDate1(rs.getDate("SUBMITTED_ON")));
                        lldb.setDateOfInitiation(CommonFunctions.getFormattedOutputDate(rs.getDate("INITIATED_ON")));
                        lldb.setDateOfInitiationAsString(CommonFunctions.getFormattedOutputDate1(rs.getDate("INITIATED_ON")));
                        lldb.setProcessname(rs.getString("PROCESS_NAME"));
                        lldb.setApplicant(getApplicantName(con, rs.getString("INITIATED_BY"), rs.getString("PROCESS_ID"), rs.getInt("TASK_ID")));
                        //lldb.setStatus(rs.getString("STATUS_NAME"));
                        if ((rs.getString("IS_COMPLETED") != null && rs.getString("IS_COMPLETED").equals("Y")) && (rs.getString("STATUS_ID") != null && rs.getString("STATUS_ID").equals("21"))) {
                            lldb.setStatus("PAR ADVERSED BY CUSTODIAN");
                        } else if (rs.getString("IS_COMPLETED") != null && rs.getString("IS_COMPLETED").equals("Y")) {
                            lldb.setStatus("COMPLETED AS REPORTING AUTHORITY");
                        } else if (rs.getString("IS_COMPLETED") != null && rs.getString("IS_COMPLETED").equals("F")) {
                            lldb.setStatus("FORCE FORWARDED BY SYSTEM");
                        }
                        lldb.setTaskId(rs.getInt("TASK_ID"));
                        lldb.setStatusId(9);
                        lldb.setAppEmpCode(rs.getString("INITIATED_BY"));
                        lldb.setProcessId(rs.getInt("PROCESS_ID"));
                        lldb.setIstaskcompleted("<a href='" + actionurl + rs.getString("TASK_ID") + "&auth=REPORTING' target='_blank'>View</a>");
                        lldb.setAuth("REPORTING");
                        a1.add(lldb);
                        Collections.sort(a1);
                    }
                    String reviewsql = "SELECT SUBMITTED_ON,ACTIONURL,PAR_MASTER.PARID,PROCESS_NAME,TASK_MASTER.PROCESS_ID PROCESS_ID,F_NAME,M_NAME,L_NAME,STATUS_NAME,INITIATED_ON,"
                            + " SPN,INITIATED_BY,TASK_MASTER.TASK_ID,TASK_MASTER.STATUS_ID,PAR_REVIEWING_TRAN.IS_COMPLETED FROM"
                            + " (SELECT PAR_ID,REVIEWING_EMP_ID,REVIEWING_CUR_SPC,SUBMITTED_ON,IS_COMPLETED FROM PAR_REVIEWING_TRAN"
                            + " WHERE REVIEWING_EMP_ID='" + empId + "' AND (IS_COMPLETED='Y' OR IS_COMPLETED='F'))PAR_REVIEWING_TRAN"
                            + " INNER JOIN PAR_MASTER ON PAR_REVIEWING_TRAN.PAR_ID=PAR_MASTER.PARID"
                            + " INNER JOIN TASK_MASTER ON PAR_MASTER.TASK_ID=TASK_MASTER.TASK_ID"
                            + " LEFT OUTER JOIN G_WORKFLOW_PROCESS ON TASK_MASTER.PROCESS_ID=G_WORKFLOW_PROCESS.PROCESS_ID"
                            + " INNER JOIN G_PROCESS_STATUS ON TASK_MASTER.STATUS_ID = G_PROCESS_STATUS.STATUS_ID"
                            + " INNER JOIN G_SPC ON TASK_MASTER.INITIATED_SPC = G_SPC.SPC"
                            + " INNER JOIN EMP_MAST ON EMP_MAST.EMP_ID = TASK_MASTER.INITIATED_BY ORDER BY TASK_ID";
                    rs = st.executeQuery(reviewsql);
                    while (rs.next()) {
                        actionurl = rs.getString("ACTIONURL");
                        actionurl = actionurl.substring(1);
                        i++;
                        lldb = new TaskListHelperBean();
                        lldb.setCount(i);
                        lldb.setSubmitted_on(CommonFunctions.getFormattedOutputDate1(rs.getDate("SUBMITTED_ON")));
                        lldb.setDateOfInitiation(CommonFunctions.getFormattedOutputDate(rs.getDate("INITIATED_ON")));
                        lldb.setDateOfInitiationAsString(CommonFunctions.getFormattedOutputDate1(rs.getDate("INITIATED_ON")));
                        lldb.setProcessname(rs.getString("PROCESS_NAME"));
                        lldb.setApplicant(getApplicantName(con, rs.getString("INITIATED_BY"), rs.getString("PROCESS_ID"), rs.getInt("TASK_ID")));
                        //lldb.setStatus(rs.getString("STATUS_NAME"));
                        if ((rs.getString("IS_COMPLETED") != null && rs.getString("IS_COMPLETED").equals("Y")) && (rs.getString("STATUS_ID") != null && rs.getString("STATUS_ID").equals("21"))) {
                            lldb.setStatus("PAR ADVERSED BY CUSTODIAN");
                        } else if (rs.getString("IS_COMPLETED") != null && rs.getString("IS_COMPLETED").equals("Y")) {
                            lldb.setStatus("COMPLETED AS REVIEWING AUTHORITY");
                        } else if (rs.getString("IS_COMPLETED") != null && rs.getString("IS_COMPLETED").equals("F")) {
                            lldb.setStatus("FORCE FORWARDED BY SYSTEM");
                        }
                        lldb.setTaskId(rs.getInt("TASK_ID"));
                        lldb.setStatusId(9);
                        lldb.setAppEmpCode(rs.getString("INITIATED_BY"));
                        lldb.setProcessId(rs.getInt("PROCESS_ID"));
                        lldb.setIstaskcompleted("<a href='" + actionurl + rs.getString("TASK_ID") + "&auth=REVIEWING' target='_blank'>View</a>");
                        lldb.setAuth("REVIEWING");
                        a1.add(lldb);
                        Collections.sort(a1);
                    }
                    String acceptsql = "SELECT SUBMITTED_ON,ACTIONURL,PAR_MASTER.PARID,PROCESS_NAME,TASK_MASTER.PROCESS_ID PROCESS_ID,F_NAME,M_NAME,L_NAME,STATUS_NAME,INITIATED_ON,"
                            + " SPN,INITIATED_BY,TASK_MASTER.TASK_ID,TASK_MASTER.STATUS_ID,PAR_ACCEPTING_TRAN.IS_COMPLETED FROM"
                            + " (SELECT PAR_ID,ACCEPTING_EMP_ID,ACCEPTING_CUR_SPC,SUBMITTED_ON,IS_COMPLETED FROM PAR_ACCEPTING_TRAN"
                            + " WHERE ACCEPTING_EMP_ID='" + empId + "' AND (IS_COMPLETED='Y' OR IS_COMPLETED='F'))PAR_ACCEPTING_TRAN"
                            + " INNER JOIN PAR_MASTER ON PAR_ACCEPTING_TRAN.PAR_ID=PAR_MASTER.PARID"
                            + " INNER JOIN TASK_MASTER ON PAR_MASTER.TASK_ID=TASK_MASTER.TASK_ID"
                            + " LEFT OUTER JOIN G_WORKFLOW_PROCESS ON TASK_MASTER.PROCESS_ID=G_WORKFLOW_PROCESS.PROCESS_ID"
                            + " INNER JOIN G_PROCESS_STATUS ON TASK_MASTER.STATUS_ID = G_PROCESS_STATUS.STATUS_ID"
                            + " INNER JOIN G_SPC ON TASK_MASTER.INITIATED_SPC = G_SPC.SPC"
                            + " INNER JOIN EMP_MAST ON EMP_MAST.EMP_ID = TASK_MASTER.INITIATED_BY ORDER BY TASK_ID";
                    rs = st.executeQuery(acceptsql);
                    while (rs.next()) {
                        actionurl = rs.getString("ACTIONURL");
                        actionurl = actionurl.substring(1);
                        i++;
                        lldb = new TaskListHelperBean();
                        lldb.setCount(i);
                        lldb.setSubmitted_on(CommonFunctions.getFormattedOutputDate1(rs.getDate("SUBMITTED_ON")));
                        lldb.setDateOfInitiation(CommonFunctions.getFormattedOutputDate(rs.getDate("INITIATED_ON")));
                        lldb.setDateOfInitiationAsString(CommonFunctions.getFormattedOutputDate1(rs.getDate("INITIATED_ON")));
                        lldb.setProcessname(rs.getString("PROCESS_NAME"));
                        lldb.setApplicant(getApplicantName(con, rs.getString("INITIATED_BY"), rs.getString("PROCESS_ID"), rs.getInt("TASK_ID")));
                        //lldb.setStatus(rs.getString("STATUS_NAME"));
                        if ((rs.getString("IS_COMPLETED") != null && rs.getString("IS_COMPLETED").equals("Y")) && (rs.getString("STATUS_ID") != null && rs.getString("STATUS_ID").equals("21"))) {
                            lldb.setStatus("PAR ADVERSED BY CUSTODIAN");
                        } else if (rs.getString("IS_COMPLETED") != null && rs.getString("IS_COMPLETED").equals("Y")) {
                            lldb.setStatus("COMPLETED AS ACCEPTING AUTHORITY");
                        } else if (rs.getString("IS_COMPLETED") != null && rs.getString("IS_COMPLETED").equals("F")) {
                            lldb.setStatus("FORCE FORWARDED BY SYSTEM");
                        }
                        lldb.setTaskId(rs.getInt("TASK_ID"));
                        lldb.setStatusId(9);
                        lldb.setAppEmpCode(rs.getString("INITIATED_BY"));
                        lldb.setProcessId(rs.getInt("PROCESS_ID"));
                        lldb.setIstaskcompleted("<a href='" + actionurl + rs.getString("TASK_ID") + "&auth=ACCEPTING' target='_blank'>View</a>");
                        lldb.setAuth("ACCEPTING");
                        a1.add(lldb);
                        Collections.sort(a1);
                    }
                }
            } else {
                if (rows == 0) {
                    sql = "SELECT RECIEV_AUTH_TYPE,PROCESS_NAME,TASK_MASTER.PROCESS_ID PROCESS_ID,F_NAME,M_NAME,L_NAME,STATUS_NAME,INITIATED_ON,SPN,INITIATED_BY,TASK_MASTER.TASK_ID,TASK_MASTER.STATUS_ID,IS_COMPLETED FROM (SELECT * FROM (SELECT TASK_ID,STATUS_ID,PROCESS_ID,INITIATED_BY,INITIATED_SPC,INITIATED_ON FROM TASK_MASTER WHERE PENDING_AT='" + empId + "' AND STATUS_ID != '16')"
                            + " TASK_MASTER)TASK_MASTER"
                            + " LEFT OUTER JOIN G_WORKFLOW_PROCESS ON  TASK_MASTER.PROCESS_ID=G_WORKFLOW_PROCESS.PROCESS_ID"
                            + " INNER JOIN G_PROCESS_STATUS ON TASK_MASTER.STATUS_ID = G_PROCESS_STATUS.STATUS_ID"
                            + " INNER JOIN G_SPC ON TASK_MASTER.INITIATED_SPC = G_SPC.SPC"
                            + " INNER JOIN (SELECT EMP_ID,F_NAME,M_NAME,L_NAME FROM EMP_MAST)EMP_MAST ON EMP_MAST.EMP_ID = TASK_MASTER.INITIATED_BY"
                            + " LEFT OUTER JOIN PAR_ADVERSE_COMM ON TASK_MASTER.TASK_ID=PAR_ADVERSE_COMM.TASK_ID"
                            + " LEFT OUTER JOIN PAR_MASTER ON TASK_MASTER.TASK_ID=PAR_MASTER.TASK_ID"
                            + " LEFT OUTER JOIN (SELECT FY,IS_CLOSED FROM FINANCIAL_YEAR WHERE IS_CLOSED='N')FINANCIAL_YEAR ON PAR_MASTER.FISCAL_YEAR=FINANCIAL_YEAR.FY ORDER BY TASK_ID";
                    rs = st.executeQuery(sql);
                    int i = 0;
                    while (rs.next()) {
                        i++;
                        lldb = new TaskListHelperBean();
                        lldb.setCount(i);
                        lldb.setDateOfInitiation(CommonFunctions.getFormattedOutputDate(rs.getDate("INITIATED_ON")));
                        lldb.setDateOfInitiationAsString(CommonFunctions.getFormattedOutputDate1(rs.getDate("INITIATED_ON")));
                        lldb.setProcessname(rs.getString("PROCESS_NAME"));
                        lldb.setApplicant(getApplicantName(con, rs.getString("INITIATED_BY"), rs.getString("PROCESS_ID"), rs.getInt("TASK_ID")));
                        lldb.setTaskId(rs.getInt("TASK_ID"));
                        lldb.setStatusId(rs.getInt("STATUS_ID"));
                        if (lldb.getStatusId() > 0) {
                            if (lldb.getStatusId() == 6) {
                                lldb.setStatus("PENDING AS REPORTING AUTHORITY");
                            } else if (lldb.getStatusId() == 7) {
                                lldb.setStatus("PENDING AS REVIEWING AUTHORITY");
                            } else if (lldb.getStatusId() == 8) {
                                lldb.setStatus("PENDING AS ACCEPTING AUTHORITY");
                            } else if (lldb.getStatusId() == 21) {
                                lldb.setApplicant(getPARApplicant(con, rs.getString("INITIATED_BY"), "21"));
                                lldb.setStatus("PAR IS ADVERSED BY CUSTODIAN");
                            } else {
                                lldb.setStatus(rs.getString("STATUS_NAME"));
                            }
                        }
                        lldb.setAppEmpCode(rs.getString("INITIATED_BY"));
                        lldb.setProcessId(rs.getInt("PROCESS_ID"));
                        lldb.setIstaskcompleted(rs.getString("IS_COMPLETED"));
                        /*if(rs.getString("STATUS_ID") != null && rs.getString("STATUS_ID").equals("8")){
                         lldb.setGrading(rs.getString("OVERALLGRADING"));    
                         }*/
                        lldb.setAuth("");
                        a1.add(lldb);
                        Collections.sort(a1);
                    }
                } else {
                    /*sql = "SELECT IS_CLOSED,RECIEV_AUTH_TYPE,PROCESS_NAME,TASK_MASTER.PROCESS_ID PROCESS_ID,F_NAME,M_NAME,L_NAME,STATUS_NAME,INITIATED_ON,SPN,INITIATED_BY,TASK_MASTER.TASK_ID,G_PROCESS_STATUS.STATUS_ID,IS_COMPLETED FROM ("
                     + "SELECT * FROM (SELECT TASK_ID,STATUS_ID,PROCESS_ID,INITIATED_BY,INITIATED_SPC,INITIATED_ON FROM TASK_MASTER WHERE PENDING_AT='" + empId + "'" + task_master_index + " AND (STATUS_ID != '16') ORDER BY INITIATED_ON DESC)"
                     + " TASK_MASTER)TASK_MASTER"
                     + " LEFT OUTER JOIN G_WORKFLOW_PROCESS ON TASK_MASTER.PROCESS_ID=G_WORKFLOW_PROCESS.PROCESS_ID"
                     + " INNER JOIN G_PROCESS_STATUS ON TASK_MASTER.STATUS_ID = G_PROCESS_STATUS.STATUS_ID"
                     + " INNER JOIN G_SPC ON TASK_MASTER.INITIATED_SPC = G_SPC.SPC"
                     + " INNER JOIN (SELECT EMP_ID,F_NAME,M_NAME,L_NAME FROM EMP_MAST " + emp_mast_index + ")EMP_MAST ON EMP_MAST.EMP_ID = TASK_MASTER.INITIATED_BY"
                     + " LEFT OUTER JOIN PAR_ADVERSE_COMM ON TASK_MASTER.TASK_ID=PAR_ADVERSE_COMM.TASK_ID"
                     + " LEFT OUTER JOIN PAR_MASTER ON TASK_MASTER.TASK_ID=PAR_MASTER.TASK_ID"
                     + " LEFT OUTER JOIN FINANCIAL_YEAR ON PAR_MASTER.FISCAL_YEAR=FINANCIAL_YEAR.FY ORDER BY TASK_ID";*/
                    if (task_master_index != null && !task_master_index.equals("")) {
                        if (processId != 3) {
                            sql = "SELECT PROCESS_NAME,TASK_MASTER.PROCESS_ID PROCESS_ID,F_NAME,M_NAME,L_NAME,STATUS_NAME,INITIATED_ON,SPN,INITIATED_BY,TASK_MASTER.TASK_ID,G_PROCESS_STATUS.STATUS_ID,IS_COMPLETED FROM ("
                                    + " SELECT * FROM (SELECT TASK_ID,STATUS_ID,PROCESS_ID,INITIATED_BY,INITIATED_SPC,INITIATED_ON FROM TASK_MASTER WHERE PENDING_AT='" + empId + "'" + task_master_index + " AND (STATUS_ID != '16') ORDER BY INITIATED_ON DESC)"
                                    + " TASK_MASTER)TASK_MASTER"
                                    + " LEFT OUTER JOIN G_WORKFLOW_PROCESS ON TASK_MASTER.PROCESS_ID=G_WORKFLOW_PROCESS.PROCESS_ID"
                                    + " INNER JOIN G_PROCESS_STATUS ON TASK_MASTER.STATUS_ID = G_PROCESS_STATUS.STATUS_ID"
                                    + " INNER JOIN G_SPC ON TASK_MASTER.INITIATED_SPC = G_SPC.SPC"
                                    + " INNER JOIN (SELECT EMP_ID,F_NAME,M_NAME,L_NAME FROM EMP_MAST " + emp_mast_index + ")EMP_MAST ON EMP_MAST.EMP_ID = TASK_MASTER.INITIATED_BY"
                                    + " LEFT OUTER JOIN PAR_ADVERSE_COMM ON TASK_MASTER.TASK_ID=PAR_ADVERSE_COMM.TASK_ID ORDER BY TASK_ID DESC";
                        } else {
                            sql = "SELECT PROCESS_NAME,TASK_MASTER.PROCESS_ID PROCESS_ID,F_NAME,M_NAME,L_NAME,STATUS_NAME,INITIATED_ON,SPN,INITIATED_BY,TASK_MASTER.TASK_ID,G_PROCESS_STATUS.STATUS_ID,IS_COMPLETED FROM ("
                                    + " SELECT * FROM (SELECT TASK_ID,STATUS_ID,PROCESS_ID,INITIATED_BY,INITIATED_SPC,INITIATED_ON FROM TASK_MASTER WHERE PENDING_AT='" + empId + "'" + task_master_index + " AND (STATUS_ID != '16') ORDER BY INITIATED_ON DESC)"
                                    + " TASK_MASTER)TASK_MASTER"
                                    + " LEFT OUTER JOIN G_WORKFLOW_PROCESS ON TASK_MASTER.PROCESS_ID=G_WORKFLOW_PROCESS.PROCESS_ID"
                                    + " INNER JOIN G_PROCESS_STATUS ON TASK_MASTER.STATUS_ID = G_PROCESS_STATUS.STATUS_ID"
                                    + " INNER JOIN G_SPC ON TASK_MASTER.INITIATED_SPC = G_SPC.SPC"
                                    + " INNER JOIN (SELECT EMP_ID,F_NAME,M_NAME,L_NAME FROM EMP_MAST " + emp_mast_index + ")EMP_MAST ON EMP_MAST.EMP_ID = TASK_MASTER.INITIATED_BY"
                                    + " LEFT OUTER JOIN PAR_ADVERSE_COMM ON TASK_MASTER.TASK_ID=PAR_ADVERSE_COMM.TASK_ID"
                                    + " INNER JOIN PAR_MASTER ON TASK_MASTER.TASK_ID=PAR_MASTER.TASK_ID"
                                    + " INNER JOIN (SELECT FY,auth_remarks_closed FROM FINANCIAL_YEAR WHERE AUTH_REMARKS_CLOSED='N')FINANCIAL_YEAR ON PAR_MASTER.FISCAL_YEAR=FINANCIAL_YEAR.FY ORDER BY TASK_ID DESC";
                        }
                    } else {
                        sql = "SELECT '' auth_remarks_closed,'' RECIEV_AUTH_TYPE,PROCESS_NAME,TASK_MASTER.PROCESS_ID PROCESS_ID,F_NAME,M_NAME,L_NAME,STATUS_NAME,"
                                + " INITIATED_ON,SPN,INITIATED_BY,TASK_MASTER.TASK_ID,G_PROCESS_STATUS.STATUS_ID,IS_COMPLETED FROM ("
                                + " SELECT * FROM (SELECT TASK_ID,STATUS_ID,PROCESS_ID,INITIATED_BY,INITIATED_SPC,INITIATED_ON FROM TASK_MASTER WHERE"
                                + " PENDING_AT='" + empId + "' AND PROCESS_ID != 3 AND (STATUS_ID != '16') ORDER BY INITIATED_ON DESC)"
                                + " TASK_MASTER)TASK_MASTER"
                                + " LEFT OUTER JOIN G_WORKFLOW_PROCESS ON TASK_MASTER.PROCESS_ID=G_WORKFLOW_PROCESS.PROCESS_ID"
                                + " INNER JOIN G_PROCESS_STATUS ON TASK_MASTER.STATUS_ID = G_PROCESS_STATUS.STATUS_ID"
                                + " INNER JOIN G_SPC ON TASK_MASTER.INITIATED_SPC = G_SPC.SPC"
                                + " INNER JOIN (SELECT EMP_ID,F_NAME,M_NAME,L_NAME FROM EMP_MAST)EMP_MAST ON EMP_MAST.EMP_ID = "
                                + " TASK_MASTER.INITIATED_BY"
                                + " UNION"
                                + " SELECT auth_remarks_closed,RECIEV_AUTH_TYPE,PROCESS_NAME,TASK_MASTER.PROCESS_ID PROCESS_ID,F_NAME,M_NAME,L_NAME,STATUS_NAME,"
                                + " INITIATED_ON,SPN,INITIATED_BY,TASK_MASTER.TASK_ID,G_PROCESS_STATUS.STATUS_ID,IS_COMPLETED FROM ("
                                + " SELECT * FROM (SELECT TASK_ID,STATUS_ID,PROCESS_ID,INITIATED_BY,INITIATED_SPC,INITIATED_ON FROM TASK_MASTER WHERE"
                                + " PENDING_AT='" + empId + "' AND PROCESS_ID = 3 AND (STATUS_ID != '16') ORDER BY INITIATED_ON DESC)"
                                + " TASK_MASTER)TASK_MASTER"
                                + " LEFT OUTER JOIN G_WORKFLOW_PROCESS ON TASK_MASTER.PROCESS_ID=G_WORKFLOW_PROCESS.PROCESS_ID"
                                + " INNER JOIN G_PROCESS_STATUS ON TASK_MASTER.STATUS_ID = G_PROCESS_STATUS.STATUS_ID"
                                + " INNER JOIN G_SPC ON TASK_MASTER.INITIATED_SPC = G_SPC.SPC"
                                + " INNER JOIN (SELECT EMP_ID,F_NAME,M_NAME,L_NAME FROM EMP_MAST)EMP_MAST ON EMP_MAST.EMP_ID = "
                                + " TASK_MASTER.INITIATED_BY"
                                + " LEFT OUTER JOIN PAR_ADVERSE_COMM ON TASK_MASTER.TASK_ID=PAR_ADVERSE_COMM.TASK_ID"
                                + " INNER JOIN PAR_MASTER ON TASK_MASTER.TASK_ID=PAR_MASTER.TASK_ID"
                                + " INNER JOIN (SELECT FY,auth_remarks_closed FROM FINANCIAL_YEAR WHERE AUTH_REMARKS_CLOSED='N')FINANCIAL_YEAR ON"
                                + " PAR_MASTER.FISCAL_YEAR=FINANCIAL_YEAR.FY ORDER BY TASK_ID DESC";
                    }
                    sql = sql + " LIMIT " + maxlimit + " OFFSET " + minlimit;
                    System.out.println("Query for List is: " + sql);
                    rs = st.executeQuery(sql);
                    int i = 0;
                    while (rs.next()) {

                        i++;
                        lldb = new TaskListHelperBean();
                        lldb.setCount(i);
                        lldb.setDateOfInitiation(CommonFunctions.getFormattedOutputDate(rs.getDate("INITIATED_ON")));
                        lldb.setDateOfInitiationAsString(CommonFunctions.getFormattedOutputDate1(rs.getDate("INITIATED_ON")));
                        lldb.setProcessname(rs.getString("PROCESS_NAME"));
                        lldb.setApplicant(getApplicantName(con, rs.getString("INITIATED_BY"), rs.getString("PROCESS_ID"), rs.getInt("TASK_ID")));
                        lldb.setTaskId(rs.getInt("TASK_ID"));
                        lldb.setStatusId(rs.getInt("STATUS_ID"));
                        if (lldb.getStatusId() >= 0) {
                            if (lldb.getStatusId() == 6) {
                                lldb.setStatus("PENDING AS REPORTING AUTHORITY");
                            } else if (lldb.getStatusId() == 7) {
                                lldb.setStatus("PENDING AS REVIEWING AUTHORITY");
                            } else if (lldb.getStatusId() == 8) {
                                lldb.setStatus("PENDING AS ACCEPTING AUTHORITY");
                            } else if (lldb.getStatusId() == 21) {
                                lldb.setApplicant(getPARApplicant(con, rs.getString("INITIATED_BY"), "21"));
                                lldb.setStatus("PAR IS ADVERSED BY CUSTODIAN");
                            } else {
                                lldb.setStatus(rs.getString("STATUS_NAME"));
                            }
                        }
                        lldb.setAppEmpCode(rs.getString("INITIATED_BY"));
                        lldb.setProcessId(rs.getInt("PROCESS_ID"));
                        lldb.setIstaskcompleted(rs.getString("IS_COMPLETED"));
                        /*if(rs.getString("STATUS_ID") != null && rs.getString("STATUS_ID").equals("8")){
                         lldb.setGrading(rs.getString("OVERALLGRADING"));    
                         }*/
                        lldb.setAuth("");
                        a1.add(lldb);
                        Collections.sort(a1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return a1;
    }

    @Override
    public int getTaskListTotalCnt(String empId, String parstatus) {

        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        int totalCnt = 0;
        try {
            con = this.dataSource.getConnection();
            stmt = con.createStatement();

            String sql = "";
            if (parstatus != null && !parstatus.equals("")) {
                sql = "SELECT COUNT(*) CNT FROM (SELECT PROCESS_NAME,TASK_MASTER.PROCESS_ID PROCESS_ID,F_NAME,M_NAME,L_NAME,STATUS_NAME,INITIATED_ON,SPN,INITIATED_BY,TASK_MASTER.TASK_ID,TASK_MASTER.STATUS_ID,IS_COMPLETED FROM(SELECT TASK_ID,STATUS_ID,PROCESS_ID,INITIATED_BY,INITIATED_SPC,INITIATED_ON FROM TASK_MASTER WHERE PENDING_AT='" + empId + "' AND STATUS_ID = '" + parstatus + "')"
                        + " TASK_MASTER"
                        + " LEFT OUTER JOIN G_WORKFLOW_PROCESS ON TASK_MASTER.PROCESS_ID=G_WORKFLOW_PROCESS.PROCESS_ID"
                        + " INNER JOIN G_PROCESS_STATUS ON TASK_MASTER.STATUS_ID = G_PROCESS_STATUS.STATUS_ID"
                        + " INNER JOIN G_SPC ON TASK_MASTER.INITIATED_SPC = G_SPC.SPC"
                        + " INNER JOIN EMP_MAST ON EMP_MAST.EMP_ID = TASK_MASTER.INITIATED_BY"
                        + " INNER JOIN PAR_MASTER ON TASK_MASTER.TASK_ID=PAR_MASTER.TASK_ID"
                        + " INNER JOIN (SELECT FY,auth_remarks_closed FROM FINANCIAL_YEAR WHERE AUTH_REMARKS_CLOSED='N')FINANCIAL_YEAR ON"
                        + " PAR_MASTER.FISCAL_YEAR=FINANCIAL_YEAR.FY)TEMP";
                rs = stmt.executeQuery(sql);
                if (rs.next()) {
                    totalCnt = rs.getInt("CNT");
                }
            } else {
                sql = "SELECT COUNT(*) CNT FROM (SELECT PROCESS_NAME,TASK_MASTER.PROCESS_ID PROCESS_ID,F_NAME,M_NAME,L_NAME,STATUS_NAME,INITIATED_ON,SPN,INITIATED_BY,TASK_ID,TASK_MASTER.STATUS_ID,IS_COMPLETED FROM(SELECT TASK_ID,STATUS_ID,PROCESS_ID,INITIATED_BY,INITIATED_SPC,INITIATED_ON FROM TASK_MASTER WHERE PENDING_AT='" + empId + "' AND PROCESS_ID != '3' AND STATUS_ID != '16')"
                        + " TASK_MASTER"
                        + " LEFT OUTER JOIN G_WORKFLOW_PROCESS ON  TASK_MASTER.PROCESS_ID=G_WORKFLOW_PROCESS.PROCESS_ID"
                        + " INNER JOIN G_PROCESS_STATUS ON TASK_MASTER.STATUS_ID = G_PROCESS_STATUS.STATUS_ID"
                        + " INNER JOIN G_SPC ON TASK_MASTER.INITIATED_SPC = G_SPC.SPC"
                        + " INNER JOIN EMP_MAST ON EMP_MAST.EMP_ID = TASK_MASTER.INITIATED_BY ORDER BY INITIATED_ON, TASK_ID DESC)TEMP";
                rs = stmt.executeQuery(sql);
                if (rs.next()) {
                    totalCnt = rs.getInt("CNT");
                }

                sql = "SELECT COUNT(*) CNT FROM (SELECT TASK_MASTER.* FROM (SELECT TASK_ID,STATUS_ID,PROCESS_ID,INITIATED_BY,INITIATED_SPC,INITIATED_ON FROM TASK_MASTER WHERE PENDING_AT='" + empId + "' AND PROCESS_ID = '3' AND STATUS_ID != '16')"
                        + " TASK_MASTER"
                        + " LEFT OUTER JOIN G_WORKFLOW_PROCESS ON TASK_MASTER.PROCESS_ID=G_WORKFLOW_PROCESS.PROCESS_ID"
                        + " INNER JOIN G_PROCESS_STATUS ON TASK_MASTER.STATUS_ID = G_PROCESS_STATUS.STATUS_ID"
                        + " INNER JOIN G_SPC ON TASK_MASTER.INITIATED_SPC = G_SPC.SPC"
                        + " INNER JOIN EMP_MAST ON EMP_MAST.EMP_ID = TASK_MASTER.INITIATED_BY"
                        + " INNER JOIN PAR_MASTER ON TASK_MASTER.TASK_ID=PAR_MASTER.TASK_ID"
                        + " INNER JOIN (SELECT FY,auth_remarks_closed FROM FINANCIAL_YEAR WHERE AUTH_REMARKS_CLOSED='N')FINANCIAL_YEAR ON"
                        + " PAR_MASTER.FISCAL_YEAR=FINANCIAL_YEAR.FY)TEMP";
                rs = stmt.executeQuery(sql);
                if (rs.next()) {
                    totalCnt = totalCnt + rs.getInt("CNT");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return totalCnt;
    }

    public String getPARApplicant(Connection con, String leaveApplicant, String prid) throws Exception {
        ResultSet rs = null;
        SelectOption so = null;
        Statement st = null;
        String authority = null;
        String sql = "";

        try {
            st = con.createStatement();
            if (prid.equals("")) {
                sql = "SELECT EMPNAME,POST FROM(SELECT GPC,EMPNAME FROM(SELECT CUR_SPC,ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME,L_NAME], ' ') EMPNAME,EMP_ID FROM EMP_MAST WHERE EMP_ID='" + leaveApplicant + "')EMPMAST LEFT OUTER JOIN PAR_MASTER ON EMPMAST.EMP_ID="
                        + " PAR_MASTER.EMP_ID INNER JOIN G_SPC ON G_SPC.SPC=PAR_MASTER.SPC)TAB LEFT OUTER JOIN"
                        + " G_POST ON G_POST.POST_CODE=TAB.GPC";
            } else if (prid.equals("21")) {
                sql = "SELECT EMPNAME,POST FROM(SELECT GPC,EMPNAME FROM(SELECT CUR_SPC,ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME,L_NAME], ' ') EMPNAME,EMP_ID FROM EMP_MAST WHERE EMP_ID='" + leaveApplicant + "')EMPMAST LEFT OUTER JOIN PAR_ADVERSE_COMM ON EMPMAST.EMP_ID="
                        + " PAR_ADVERSE_COMM.SENDER_ID INNER JOIN G_SPC ON G_SPC.SPC=PAR_ADVERSE_COMM.SENDER_SPC)TAB LEFT OUTER JOIN"
                        + " G_POST ON G_POST.POST_CODE=TAB.GPC";
            }
            rs = st.executeQuery(sql);
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
            DataBaseFunctions.closeSqlObjects(rs);
        }
        return authority;
    }

    @Override
    public String getDispatcherString(int taskid) {

        Connection con = null;

        Statement stmt = null;
        ResultSet rs = null;

        String actionurl = "";
        try {
            con = dataSource.getConnection();
            stmt = con.createStatement();
            String sql = "SELECT TASK_ID,PROCESS_NAME,ACTIONURL,APPLY_TO FROM (SELECT * FROM TASK_MASTER WHERE TASK_ID='" + taskid + "')TASK_MASTER INNER JOIN G_WORKFLOW_PROCESS ON G_WORKFLOW_PROCESS.PROCESS_ID = TASK_MASTER.PROCESS_ID";
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                actionurl = rs.getString("ACTIONURL");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, stmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return actionurl;
    }

    @Override
    public List getProcessList() {

        Connection con = null;

        Statement stmt = null;
        ResultSet rs = null;

        List processlist = new ArrayList();
        SelectOption so = null;
        try {
            con = dataSource.getConnection();

            so = new SelectOption();
            so.setLabel("ALL");
            so.setValue("");
            processlist.add(so);

            stmt = con.createStatement();
            String sql = "SELECT PROCESS_ID,PROCESS_NAME FROM G_WORKFLOW_PROCESS WHERE ACTIVE='Y' ORDER BY PROCESS_NAME";
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                so = new SelectOption();
                so.setLabel(rs.getString("PROCESS_NAME") + " ");
                so.setValue(rs.getString("PROCESS_ID"));
                processlist.add(so);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, stmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return processlist;
    }

    @Override
    public List getMyTaskList(String empid) {
        Connection con = null;
        ResultSet result = null;
        PreparedStatement statement = null;
        ArrayList tasklist = new ArrayList();

        try {
            con = dataSource.getConnection();
            String SQL = "SELECT PROCESS_NAME,TASK_MASTER.PROCESS_ID PROCESS_ID,F_NAME,M_NAME,L_NAME,STATUS_NAME,INITIATED_ON,SPN,INITIATED_BY,TASK_ID,G_PROCESS_STATUS.STATUS_ID,IS_COMPLETED FROM (SELECT * FROM (SELECT TASK_ID,STATUS_ID,PROCESS_ID,INITIATED_BY,INITIATED_SPC,INITIATED_ON,ROW_NUMBER() OVER (ORDER BY TASK_ID) Row_Num FROM TASK_MASTER WHERE PENDING_AT=? AND STATUS_ID != '16')"
                    + " TASK_MASTER)TASK_MASTER"
                    + " LEFT OUTER JOIN G_WORKFLOW_PROCESS ON  TASK_MASTER.PROCESS_ID=G_WORKFLOW_PROCESS.PROCESS_ID"
                    + " INNER JOIN G_PROCESS_STATUS ON TASK_MASTER.STATUS_ID = G_PROCESS_STATUS.STATUS_ID"
                    + " INNER JOIN G_SPC ON TASK_MASTER.INITIATED_SPC = G_SPC.SPC"
                    + " INNER JOIN EMP_MAST ON EMP_MAST.EMP_ID = TASK_MASTER.INITIATED_BY ORDER BY TASK_ID";
            statement = con.prepareStatement(SQL);
            statement.setString(1, empid);
            result = statement.executeQuery();
            while (result.next()) {
                TaskListHelperBean tlhb = new TaskListHelperBean();
                tlhb.setDateOfInitiation(result.getDate("INITIATED_ON"));
                tlhb.setProcessname(result.getString("PROCESS_NAME"));
                tlhb.setTaskId(result.getInt("TASK_ID"));
                tlhb.setStatusId(result.getInt("STATUS_ID"));

                if (tlhb.getStatusId() != 0) {

                    if (tlhb.getStatusId() == 6) {
                        tlhb.setStatus("PENDING AS REPORTING AUTHORITY");
                    } else if (tlhb.getStatusId() == 7) {
                        tlhb.setStatus("PENDING AS REVIEWING AUTHORITY");
                    } else if (tlhb.getStatusId() == 8) {
                        tlhb.setStatus("PENDING AS ACCEPTING AUTHORITY");
                    } else if (tlhb.getStatusId() == 1) {
                        tlhb.setStatus("APPROVED");
                    } else if (tlhb.getStatusId() == 2) {
                        tlhb.setStatus("FORWARD");
                    } else if (tlhb.getStatusId() == 3) {
                        tlhb.setStatus("PENDING");
                    } else if (tlhb.getStatusId() == 4) {
                        tlhb.setStatus("SANCTIONED");
                    } else if (tlhb.getStatusId() == 5) {
                        tlhb.setStatus("ALLOWED");
                    } else if (tlhb.getStatusId() == 41) {
                        tlhb.setStatus("JOINED");
                    } else if (tlhb.getStatusId() == 42) {
                        tlhb.setStatus("DECLINE");
                    }
                }
                tlhb.setAppEmpCode(result.getString("INITIATED_BY"));
                tlhb.setProcessId(result.getInt("PROCESS_ID"));
                tlhb.setIstaskcompleted(result.getString("IS_COMPLETED"));
                tasklist.add(tlhb);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(result, statement);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return tasklist;

    }

    @Override
    public String getLoggedInEmpNameForCompletedTask(String empid) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        String empname = "";
        try {
            con = this.dataSource.getConnection();

            String sql = "SELECT ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME, L_NAME], ' ') EMPNAME FROM EMP_MAST WHERE EMP_ID=?";
            pst = con.prepareStatement(sql);
            pst.setString(1, empid);
            rs = pst.executeQuery();
            if (rs.next()) {
                empname = rs.getString("EMPNAME");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return empname;
    }

}
