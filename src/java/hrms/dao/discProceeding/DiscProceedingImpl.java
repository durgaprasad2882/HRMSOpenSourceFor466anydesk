/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.discProceeding;

import hrms.SelectOption;
import hrms.common.CommonFunctions;
import hrms.common.DataBaseFunctions;
import hrms.model.discProceeding.ChargeWitnesBean;
import hrms.model.discProceeding.FileAttachBean;
import hrms.model.discProceeding.DiscProceedingBean;
import hrms.model.discProceeding.DpViewBean;
import hrms.model.discProceeding.Rule15ChargeBean;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public class DiscProceedingImpl implements DiscProceedingDAO {

    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    
    @Override
    public List getPostWithEmpList(String offcode) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List li = new ArrayList();
        SelectOption so = null;
        
        try {
            con = this.dataSource.getConnection();
            String postQry = "select spc,post,ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME,L_NAME], ' ') EMPNAME,emp_id,POST_CODE,CUR_SPC from ("
                    + "select spc,gpc from g_spc where off_code='"+ offcode +"' AND (IFUCLEAN!='Y' OR IFUCLEAN IS NULL)) g_spc "
                    + "inner join g_post on g_spc.GPC = g_post.POST_CODE left outer join EMP_MAST ON G_SPC.SPC=EMP_MAST.CUR_SPC ORDER BY POST";
            ps = con.prepareStatement(postQry);
            rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getString("EMPNAME") != null && !rs.getString("EMPNAME").equals("")) {
                    so = new SelectOption();
                    
                    so.setLabel(rs.getString("post") +" (" +rs.getString("EMPNAME")+ ")");
                    so.setValue(rs.getString("CUR_SPC"));
                    li.add(so);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return li;
    }
    
    @Override
    public int saveRule15ForwardDP(String authHrmsId, String authEmpSpc, Rule15ChargeBean chargeBean){
        
        Connection con = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;
        PreparedStatement pstmt2 = null;
        int logid = 0;
        int tmRes = 0;
        int wfRes = 0;
        int res = 0;
        try {
            con = dataSource.getConnection();
            Calendar cal = Calendar.getInstance();
            DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");
            String startTime = dateFormat.format(cal.getTime());
            
            int mcode = CommonFunctions.getMaxCodeInteger("TASK_MASTER", "TASK_ID", con);
            String taskMastInsQry="INSERT INTO TASK_MASTER(TASK_ID, PROCESS_ID, INITIATED_BY, INITIATED_ON, STATUS_ID, PENDING_AT,APPLY_TO,"
                                + "INITIATED_SPC,PENDING_SPC) Values (?,?,?,?,?,?,?,?,?)";
            pstmt1 = con.prepareStatement(taskMastInsQry);
            pstmt1.setInt(1, mcode);
            pstmt1.setInt(2, 11);  //SAME AS (PROCESS_ID) OF TBALE G_WORKFLOW_PROCESS
            pstmt1.setString(3, authHrmsId);
            pstmt1.setTimestamp(4, new Timestamp(dateFormat.parse(startTime).getTime()));
        //TASK_ID, PROCESS_ID, INITIATED_BY, INITIATED_ON,    
            
            pstmt1.setInt(5, 56); //SAME AS (STATUS_ID) OF TBALE G_PROCESS_STATUS - for forward
            pstmt1.setString(6, chargeBean.getHidForwardHrmsId());
            pstmt1.setString(7, chargeBean.getHidForwardHrmsId());
            pstmt1.setString(8, authEmpSpc);
            pstmt1.setString(9, chargeBean.getHidPostCode());
        //STATUS_ID, PENDING_AT,APPLY_TO, INITIATED_SPC,PENDING_SPC    
            tmRes = pstmt1.executeUpdate();
                    
                    
            String insQry = "INSERT INTO WORKFLOW_LOG(LOG_ID,REF_ID,ACTION_TAKEN_BY,FORWARDED_SPC,FORWARD_TO,NOTE,SPC_ONTIME,TASK_ACTION_DATE,"
                    + "TASK_ID,TASK_STATUS_ID,WORKFLOW_TYPE,AUTHORITY_TYPE) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmt = con.prepareStatement(insQry);
            logid = CommonFunctions.getMaxCodeInteger("WORKFLOW_LOG", "LOG_ID", con);
            pstmt.setInt(1, logid);
            pstmt.setInt(2, Integer.parseInt(chargeBean.getHidFowardDaId()));
            pstmt.setString(3, authHrmsId);
            pstmt.setString(4, chargeBean.getHidPostCode());
            pstmt.setString(5, chargeBean.getHidForwardHrmsId());
        //LOG_ID,REF_ID,ACTION_TAKEN_BY,FORWARDED_SPC,FORWARD_TO
            
            pstmt.setString(6, "NA");
            pstmt.setString(7, authEmpSpc);
            pstmt.setTimestamp(8, new Timestamp(dateFormat.parse(startTime).getTime()));
        //NOTE,SPC_ONTIME,TASK_ACTION_DATE
            
            pstmt.setInt(9, mcode);
            pstmt.setInt(10, 56); //SAME AS (STATUS_ID) OF TBALE G_PROCESS_STATUS - for forward
            pstmt.setString(11, "DISCIPLINARY PROCEEDING");
            pstmt.setString(12, "NA");
        //TASK_ID,TASK_STATUS_ID,WORKFLOW_TYPE,AUTHORITY_TYPE
            
            wfRes = pstmt.executeUpdate();
            
            if(tmRes == 1 && wfRes == 1){
                pstmt2 = con.prepareStatement("UPDATE disciplinary_authority set dp_status=?,task_id=? WHERE daid=?");
                pstmt2.setString(1, "56");
                pstmt2.setInt(2, mcode);
                pstmt2.setInt(3, Integer.parseInt(chargeBean.getHidFowardDaId()));
                
                res= pstmt2.executeUpdate();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return res;
    }
    
    @Override
    public int saveRule15MemoDetails(DiscProceedingBean dpBean) {

        Connection con = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;
        ResultSet rs = null;
        int res = 0;
        String doCurSpc = null;
        try {
            con = dataSource.getConnection();
            String startTime = "";
            Calendar cal = Calendar.getInstance();
            DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");
            startTime = dateFormat.format(cal.getTime());

            String getSpcQry = "select emp_id, cur_spc from emp_mast WHERE emp_id=?";
            pstmt1 = con.prepareStatement(getSpcQry);
            pstmt1.setString(1, dpBean.getAuthHrmsId());
            rs = pstmt1.executeQuery();
            while (rs.next()) {
                doCurSpc = rs.getString("cur_spc");
            }

            String rule15MemoQry = "INSERT INTO disciplinary_authority (daid, auth_hrmsid, auth_spc, do_hrmsid, do_spc, dis_initiated_date ,"
                    + "dis_order_no, dis_order_date, dis_type, under_rule, isactive, closedate, irre_details,dp_status,task_id) "
                    + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmt = con.prepareStatement(rule15MemoQry);
            pstmt.setInt(1, getMaxDaId());
            pstmt.setString(2, dpBean.getAuthHrmsId());
            pstmt.setString(3, dpBean.getAuthEmpCurDegn());
            pstmt.setString(4, dpBean.getHidDoHrmsId());
            pstmt.setString(5, doCurSpc);
            pstmt.setTimestamp(6, new Timestamp(dateFormat.parse(startTime).getTime()));
            pstmt.setString(7, dpBean.getRule15MemoNo());
            pstmt.setTimestamp(8, new Timestamp(new SimpleDateFormat("dd-MMM-yyyy").parse(dpBean.getRule15MemoDate()).getTime()));
            pstmt.setString(9, "DIS");
            pstmt.setString(10, "Rule15");
            pstmt.setString(11, "Y");
            pstmt.setTimestamp(12, new Timestamp(new SimpleDateFormat("dd-MMM-yyyy").parse("11-NOV-1111").getTime()));
            pstmt.setString(13, dpBean.getAnnex1Charge());
            pstmt.setString(14, "55"); // 55 = Initiated 
            pstmt.setInt(15, 0);
            res = pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return res;
    }

    @Override
    public String getChargeDetails(int daId) {

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String showBtn="";
        try {
            con = dataSource.getConnection();
            String chargeQry = "select daid from disciplinary_act_charge where daid=?";
            pstmt = con.prepareStatement(chargeQry);
            pstmt.setInt(1, daId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                showBtn="Y";
            }else{
                showBtn="";
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return showBtn;
    }
    
    @Override
    public DiscProceedingBean getRule15MemoDetails(String offCode, String doHrmsId) {

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        PreparedStatement pstmt1 = null;
        ResultSet rs1 = null;
        DiscProceedingBean dpBean = new DiscProceedingBean();

        try {
            con = dataSource.getConnection();
            String deptNameQry = "SELECT department_name FROM g_office OFFICE INNER JOIN g_department DEPT ON "
                    + "OFFICE.department_code=DEPT.department_code WHERE off_code=?";

            pstmt = con.prepareStatement(deptNameQry);
            pstmt.setString(1, offCode);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                dpBean.setDeptName(rs.getString("department_name"));
                dpBean.setHidOffCode(offCode);
            }

            String empQry = "SELECT emp_id,ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME, L_NAME], ' ') EMPNAME FROM emp_mast WHERE emp_id=?";
            pstmt1 = con.prepareStatement(empQry);
            pstmt1.setString(1, doHrmsId);
            rs1 = pstmt1.executeQuery();
            while (rs1.next()) {
                dpBean.setDoHrmsId(rs1.getString("emp_id"));
                dpBean.setDoEmpName(rs1.getString("EMPNAME"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return dpBean;
    }
    
    @Override
    public List getDeptWiseEmpList(String deptCode) {

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        ArrayList empList = new ArrayList();
        DiscProceedingBean dpBean = null;

        try {
            con = dataSource.getConnection();
            String empListQry = "select emp_id,ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME, L_NAME], ' ') EMPNAME,post "
                    + "from g_office GE INNER JOIN emp_mast EM ON GE.off_code=EM.cur_off_code "
                    + "LEFT OUTER JOIN g_spc GS ON EM.cur_spc=GS.spc "
                    + "LEFT OUTER JOIN g_post GP ON GP.post_code= GS.gpc where GE.department_code=? ORDER BY F_NAME";
            
            pstmt = con.prepareStatement(empListQry);
            pstmt.setString(1, deptCode);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                dpBean = new DiscProceedingBean();

                dpBean.setDoHrmsId(rs.getString("emp_id"));
                dpBean.setDoEmpName(rs.getString("EMPNAME"));
                dpBean.setDoEmpCurDegn(rs.getString("post"));

                empList.add(dpBean);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return empList;
    }
    
    @Override
    public List getDPForwadrEmpList(String offCode) {

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        ArrayList empList = new ArrayList();
        DiscProceedingBean dpBean = null;

        try {
            con = dataSource.getConnection();
            String empListQry = "select emp_id,ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME, L_NAME], ' ') EMPNAME,post "
                    + "from g_office GE INNER JOIN emp_mast EM ON GE.off_code=EM.cur_off_code "
                    + "LEFT OUTER JOIN g_spc GS ON EM.cur_spc=GS.spc "
                    + "LEFT OUTER JOIN g_post GP ON GP.post_code= GS.gpc where GE.department_code=? ORDER BY F_NAME";
            
            pstmt = con.prepareStatement(empListQry);
            pstmt.setString(1, offCode);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                dpBean = new DiscProceedingBean();

                dpBean.setDoHrmsId(rs.getString("emp_id"));
                dpBean.setDoEmpName(rs.getString("EMPNAME"));
                dpBean.setDoEmpCurDegn(rs.getString("post"));

                empList.add(dpBean);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return empList;
    }
    
    @Override
    public List getOffWiseEmpList(String offCode) {

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        ArrayList empList = new ArrayList();
        DiscProceedingBean dpBean = null;

        try {
            con = dataSource.getConnection();
            String empListQry = "select emp_id,ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME, L_NAME], ' ') EMPNAME,post from emp_mast EM "
                    + "LEFT OUTER JOIN g_spc GS ON EM.cur_spc=GS.spc "
                    + "LEFT OUTER JOIN g_post GP ON GP.post_code= GS.gpc where cur_off_code=? ORDER BY F_NAME";
            pstmt = con.prepareStatement(empListQry);
            pstmt.setString(1, offCode);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                dpBean = new DiscProceedingBean();

                dpBean.setDoHrmsId(rs.getString("emp_id"));
                dpBean.setDoEmpName(rs.getString("EMPNAME"));
                dpBean.setDoEmpCurDegn(rs.getString("post"));

                empList.add(dpBean);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return empList;
    }
    
    
    @Override
    public List getPostWiseEmpList(String curSpc) {

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        ArrayList empList = new ArrayList();
        DiscProceedingBean dpBean = null;

        try {
            con = dataSource.getConnection();
            String empListQry = "select emp_id,ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME, L_NAME], ' ') EMPNAME from emp_mast where cur_spc=?";
            pstmt = con.prepareStatement(empListQry);
            pstmt.setString(1, curSpc);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                dpBean = new DiscProceedingBean();

                dpBean.setDoHrmsId(rs.getString("emp_id"));
                dpBean.setDoEmpName(rs.getString("EMPNAME"));

                empList.add(dpBean);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return empList;
    }
    
    
    @Override
    public List getOffWiseEmpWitnessList(String offCode, String doHrmsId, int dacId, String mode) {

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        ArrayList empList = new ArrayList();
        DiscProceedingBean dpBean = null;
        String empListQry = "";

        try {
            con = dataSource.getConnection();
            if (mode.equals("E")) {
                empListQry = "select ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME, L_NAME], ' ') EMPNAME,emp_id,post,dacid from emp_mast emp "
                        + "LEFT OUTER JOIN disciplinary_act_charge_witness dw ON emp.emp_id=dw.witness_hrmsid "
                        + "LEFT OUTER JOIN g_spc GS ON emp.cur_spc=GS.spc "
                        + "LEFT OUTER JOIN g_post GP ON GP.post_code= GS.gpc "
                        + "where cur_off_code=? and emp.emp_id!=? ORDER BY F_NAME";
                pstmt = con.prepareStatement(empListQry);
                pstmt.setString(1, offCode);
                pstmt.setString(2, doHrmsId);

                rs = pstmt.executeQuery();
                while (rs.next()) {
                    dpBean = new DiscProceedingBean();

                    String emHrmsId = rs.getString("emp_id");
                    if (rs.getInt("dacid") == dacId) {
                        dpBean.setChkVal(1);
                    }

                    dpBean.setDoHrmsId(emHrmsId);
                    dpBean.setDoEmpName(rs.getString("EMPNAME"));
                    dpBean.setDoEmpCurDegn(rs.getString("post"));

                    empList.add(dpBean);
                }

            } else {
                empListQry = "select emp_id,ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME, L_NAME], ' ') EMPNAME,post from emp_mast EM "
                        + "LEFT OUTER JOIN g_spc GS ON EM.cur_spc=GS.spc LEFT OUTER JOIN g_post GP ON GP.post_code= GS.gpc "
                        + "where cur_off_code=? and emp_id!=? ORDER BY F_NAME";
                pstmt = con.prepareStatement(empListQry);
                pstmt.setString(1, offCode);
                pstmt.setString(2, doHrmsId);

                rs = pstmt.executeQuery();
                while (rs.next()) {
                    dpBean = new DiscProceedingBean();

                    dpBean.setDoHrmsId(rs.getString("emp_id"));
                    dpBean.setChkVal(0);
                    dpBean.setDoEmpName(rs.getString("EMPNAME"));
                    dpBean.setDoEmpCurDegn(rs.getString("post"));

                    empList.add(dpBean);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return empList;
    }
    
           
            
    @Override
    public List getDiscProcedingList(String empId, int page, int rows) {

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        ArrayList empList = new ArrayList();
        DiscProceedingBean dpBean = null;
        String dType = "";
        String nameAndDegn="";
        String forwardNameDegn="";
        try {
            String eol = System.getProperty("line.separator");
            con = dataSource.getConnection();
            int minlimit = rows * (page - 1);
            int maxlimit = rows;
        
            String doListQry = "select daid,ARRAY_TO_STRING(ARRAY[EM1.INITIALS, EM1.F_NAME, EM1.M_NAME, EM1.L_NAME], ' ') FromEmp,GP1.post FromSpc, "
                    + "dis_initiated_date,dis_type,under_rule,dp_status,initiated_on,ARRAY_TO_STRING(ARRAY[EM2.INITIALS, EM2.F_NAME, EM2.M_NAME, "
                    + "EM2.L_NAME], ' ') ToEmp,GP2.post ToSpc from disciplinary_authority DA INNER JOIN emp_mast EM1 ON EM1.emp_id=DA.do_hrmsid "
                    + "LEFT OUTER JOIN g_spc GS1 ON EM1.cur_spc=GS1.spc LEFT OUTER JOIN g_post GP1 ON GP1.post_code= GS1.gpc "
                    + "LEFT OUTER JOIN task_master TM ON TM.task_id=DA.task_id LEFT OUTER JOIN emp_mast EM2 ON EM2.emp_id=TM.APPLY_TO "
                    + "LEFT OUTER JOIN g_spc GS2 ON EM2.cur_spc=GS2.spc LEFT OUTER JOIN g_post GP2 ON GP2.post_code= GS2.gpc "
                    + "where auth_hrmsid=? AND isactive='Y' ORDER BY dis_initiated_date DESC LIMIT "+maxlimit+" OFFSET "+minlimit+"";
            pstmt = con.prepareStatement(doListQry);
            pstmt.setString(1, empId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                dpBean = new DiscProceedingBean();

                dpBean.setDaid(rs.getInt("daid"));
                String doEmp= rs.getString("FromEmp");
                String doDegn= rs.getString("FromSpc");
                
                if (doDegn != null && !doDegn.equals("")) {
                    nameAndDegn = doEmp + ", "+ eol + doDegn;
                } else {
                    nameAndDegn = doEmp + " " + doDegn;
                }
                dpBean.setDoEmpNameAndDesg(nameAndDegn);
                
                //dpBean.setStartDate(CommonFunctions.getFormattedOutputDate1(rs.getDate("dis_initiated_date")));
                dType = rs.getString("dis_type");
                if (dType.equals("DIS")) {
                    dpBean.setDisType("Disciplinary");
                } else {
                    dpBean.setDisType("Suspension");
                }
                dpBean.setUnderRule(rs.getString("under_rule"));
                dpBean.setDpStatus(rs.getString("dp_status"));
                
                dpBean.setForwardDate(CommonFunctions.getFormattedOutputDate1(rs.getDate("initiated_on")));
                String applyTo= StringUtils.defaultString(rs.getString("ToEmp"));
                String applySpc= StringUtils.defaultString(rs.getString("ToSpc"));
                        
                if (applySpc != null && !applySpc.equals("")) {
                    forwardNameDegn = applyTo + ", "+ eol + applySpc;
                } else {
                    forwardNameDegn = applyTo + " " + applySpc;
                }
                dpBean.setForwardNameAndDegn(StringUtils.defaultString(forwardNameDegn));
                 
                empList.add(dpBean);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return empList;
    }
    
    @Override
    public int getDiscProceedTotalCount(String empId) {

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int totCount = 0;
        
        try {
            con = dataSource.getConnection();
            String countQry = "select count(*) totalCount from disciplinary_authority DA INNER JOIN emp_mast EM1 ON EM1.emp_id=DA.do_hrmsid "
                    + "LEFT OUTER JOIN g_spc GS1 ON EM1.cur_spc=GS1.spc LEFT OUTER JOIN g_post GP1 ON GP1.post_code= GS1.gpc "
                    + "LEFT OUTER JOIN task_master TM ON TM.task_id=DA.task_id LEFT OUTER JOIN emp_mast EM2 ON EM2.emp_id=TM.APPLY_TO "
                    + "LEFT OUTER JOIN g_spc GS2 ON EM2.cur_spc=GS2.spc LEFT OUTER JOIN g_post GP2 ON GP2.post_code= GS2.gpc "
                    + "where auth_hrmsid=? AND isactive='Y'";
            pstmt = con.prepareStatement(countQry);
            pstmt.setString(1, empId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                totCount = rs.getInt("totalCount");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return totCount;
    }
    
    @Override
    public int getMaxDaId() {

        Connection con = null;
        int maxId = 0;
        try {
            con = dataSource.getConnection();
            maxId = CommonFunctions.getMaxCode(con, "disciplinary_authority", "daid");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return maxId;
    }

    private int getMaxDacId() {

        Connection con = null;
        int maxId = 0;
        try {
            con = dataSource.getConnection();
            maxId = CommonFunctions.getMaxCode(con, "disciplinary_act_charge", "dacid");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return maxId;
    }

    @Override
    public ArrayList getEmpComboDtls(String offCode, String doHrmsId) {
        ArrayList al = new ArrayList();
        ResultSet rs = null;
        SelectOption so = null;
        PreparedStatement pstmt = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            String empListQry = "select emp_id,ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME, L_NAME], ' ') EMPNAME,post,cur_spc from emp_mast EM "
                    + "LEFT OUTER JOIN g_spc GS ON EM.cur_spc=GS.spc LEFT OUTER JOIN g_post GP ON GP.post_code= GS.gpc"
                    + " where cur_off_code=? and emp_id!=?";

            pstmt = con.prepareStatement(empListQry);
            pstmt.setString(1, offCode);
            pstmt.setString(2, doHrmsId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                so = new SelectOption();
                so.setLabel(rs.getString("EMPNAME") + "," + rs.getString("post"));
                so.setValue(rs.getString("emp_id") + "~" + rs.getString("cur_spc"));
                al.add(so);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return al;
    }

    @Override
    public int saveRule15AddCharges(Rule15ChargeBean chargeBean, String filePath) {

        Connection con = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;
        int result = 0;
        MultipartFile docFile = chargeBean.getRule15Document();
        MultipartFile docFile1 = chargeBean.getRule15SubDoc();
        try {
            InputStream inputStream = null;
            OutputStream outputStream = null;
            
            String dirpath = filePath + "DP" + "/";
            File newfile = new File(dirpath);
            if (!newfile.exists()) {
                newfile.mkdirs();
            }
            
            con = dataSource.getConnection();
            String chargesQry = "INSERT INTO disciplinary_act_charge (dacid, daid, charge, charge_details) VALUES(?,?,?,?)";
            pstmt = con.prepareStatement(chargesQry);
            
            int dacId= CommonFunctions.getMaxCode(con, "disciplinary_act_charge", "dacid");
            pstmt.setInt(1, dacId);
            pstmt.setInt(2, chargeBean.getHidChargeDaid());
            pstmt.setString(3, chargeBean.getRule15Articles());
            pstmt.setString(4, chargeBean.getRule15ChargDtls());
            result = pstmt.executeUpdate();
            
            String filename = "";
            long time = System.currentTimeMillis();
                        
            String insDocQry="INSERT INTO disciplinary_charge_document (dacid,do_hrmsid,disk_file_name,org_file_name,file_type,doc_type) VALUES(?,?,?,?,?,?)";
            
            
            if (docFile != null && !docFile.isEmpty()) {
                pstmt1 = con.prepareStatement(insDocQry);
                pstmt1.setInt(1, dacId);
                String dohrmsId=chargeBean.getHidChargeDoHrmsId();
                pstmt1.setString(2, dohrmsId);
            
                filename = dohrmsId + "_" + dacId + "_" + time;
                pstmt1.setString(3, filename);
                pstmt1.setString(4, docFile.getOriginalFilename());
                pstmt1.setString(5, docFile.getContentType());
                pstmt1.setString(6, "D");
                int onlyDocIns=pstmt1.executeUpdate();
                
                outputStream = new FileOutputStream(dirpath + filename);
                int read = 0;
                byte[] bytes = new byte[1024];
                inputStream = docFile.getInputStream();
                while ((read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
                
            }else if (docFile1 != null && !docFile1.isEmpty()) {
                pstmt1 = con.prepareStatement(insDocQry);
                pstmt1.setInt(1, dacId);
                String dohrmsId=chargeBean.getHidChargeDoHrmsId();
                pstmt1.setString(2, dohrmsId);
            
                filename = dohrmsId + "_" + dacId + "_" + time;
                pstmt1.setString(3, filename);
                pstmt1.setString(4, docFile1.getOriginalFilename());
                pstmt1.setString(5, docFile1.getContentType());
                pstmt1.setString(6, "S");
                int onlySdocIns=pstmt1.executeUpdate();
                
                outputStream = new FileOutputStream(dirpath + filename);
                int read = 0;
                byte[] bytes = new byte[1024];
                inputStream = docFile1.getInputStream();
                while ((read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
            }
            
            
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return result;
    }

    @Override
    public int saveRule15AddWitness(Rule15ChargeBean chargeBean) {

        Connection con = null;
        PreparedStatement pstmt = null;
        int result = 0;
        String witnessId = "";
        try {
            con = dataSource.getConnection();
            String witnessQry = "INSERT INTO disciplinary_act_charge_witness (dacwid, dacid, witness_hrmsid, witness_spc) VALUES(?,?,?,?)";
            pstmt = con.prepareStatement(witnessQry);

            String witnessHrmsIds = chargeBean.getHidWitnessIds();
            StringTokenizer stringTokenizer = new StringTokenizer(witnessHrmsIds, ",");
            while (stringTokenizer.hasMoreTokens()) {
                witnessId = stringTokenizer.nextToken().trim();
                pstmt.setInt(1, CommonFunctions.getMaxCode(con, "disciplinary_act_charge_witness", "dacwid"));
                pstmt.setInt(2, chargeBean.getHidWitnessDacId());
                pstmt.setString(3, witnessId);
                pstmt.setString(4, getCurSpc(witnessId));
                result = pstmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return result;
    }

    private String getCurSpc(String witnessId) {

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String curSpc = "";
        try {
            con = dataSource.getConnection();
            String curSpcQry = "select cur_spc from emp_mast where emp_id=?";
            pstmt = con.prepareStatement(curSpcQry);
            pstmt.setString(1, witnessId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                curSpc = rs.getString("cur_spc");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return curSpc;
    }

    @Override
    public List getRule15ChargeList(int doDaId, int page, int rows) {

        Connection con = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;
        ResultSet rs = null;
        ResultSet rs1 = null;

        ArrayList empList = new ArrayList();
        Rule15ChargeBean chargeBean = null;

        try {
            
            int minlimit = rows * (page - 1);
            int maxlimit = rows;
            
            String eol = System.getProperty("line.separator");
            con = dataSource.getConnection();
            String chargeListQry = "Select daid,DAC.dacid dId,charge, org_file_name from disciplinary_act_charge DAC "
                    + "INNER JOIN disciplinary_charge_document DCD ON DAC.dacid=DCD.dacid where daid=? LIMIT "+maxlimit+" OFFSET "+minlimit+"";
            
            String witnessQry = "select ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME, L_NAME], ' ') EMPNAME from emp_mast EM "
                    + "INNER JOIN disciplinary_act_charge_witness DACW ON EM.emp_id=DACW.witness_hrmsid where dacid=?";
            pstmt = con.prepareStatement(chargeListQry);
            pstmt1 = con.prepareStatement(witnessQry);

            pstmt.setInt(1, doDaId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                chargeBean = new Rule15ChargeBean();

                chargeBean.setChargeDaId(rs.getInt("daid"));
                int dacId = rs.getInt("dId");
                chargeBean.setChargeDacId(dacId);
                chargeBean.setRule15Articles(rs.getString("charge"));
                chargeBean.setRule15DocumentName(rs.getString("org_file_name"));
                        
                pstmt1.setInt(1, dacId);
                rs1 = pstmt1.executeQuery();
                String witness = "";
                while (rs1.next()) {
                    if (witness.equals("")) {
                        witness = rs1.getString("EMPNAME");
                    } else {
                        witness = witness + "," + eol + rs1.getString("EMPNAME");
                    }
                }
                chargeBean.setWitnessName(StringUtils.defaultString(witness));
                empList.add(chargeBean);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return empList;
    }
    
    @Override
    public int getRule15ChargeTotalCount(int doDaId) {

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int totalCount = 0;
        try {
            
            con = dataSource.getConnection();
            String chargeListQry = "Select count(*)totalCount from disciplinary_act_charge DAC "
                    + "INNER JOIN disciplinary_charge_document DCD ON DAC.dacid=DCD.dacid where daid=?";
            pstmt = con.prepareStatement(chargeListQry);
            pstmt.setInt(1, doDaId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                totalCount = rs.getInt("totalCount");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return totalCount;
    }
    
    @Override
    public Rule15ChargeBean EditRule15ChargeData(Rule15ChargeBean chargeBean, int dacId) {

        Connection con = null;
        PreparedStatement pstmt = null;
        Statement stmt = null;
        ResultSet rs = null;
        List al=new ArrayList();
        List updAl=new ArrayList();
        
        try {
            con = dataSource.getConnection();
            stmt = con.createStatement();
            String chargeQry = "select charge, charge_details from disciplinary_act_charge where dacid=?";
            pstmt = con.prepareStatement(chargeQry);
            pstmt.setInt(1, dacId);
            rs = pstmt.executeQuery();
            if (rs.next()) {

                chargeBean.setRule15Articles(rs.getString("charge"));
                chargeBean.setRule15ChargDtls(rs.getString("charge_details"));
                al=getDPAttachedFileName(stmt, dacId);
                
                FileAttachBean faBean = null;
                //ChargeWitnesBean cwBean = null;
//                for(int i=0; i< al.size(); i++){
//                    cwBean = (ChargeWitnesBean)al.get(i);
//                    String fName=cwBean.getFname();
//                    String dType=cwBean.getDtype();
//                    System.out.println("the val of fName is===111===="+fName+"=====docType==="+dType);
//                    
//                    faBean = new FileAttachBean();
//                    faBean.setOrgFileName(fName);
//                    faBean.setDocType(dType);
//                    updAl.add(faBean);
//                    
//                    System.out.println("the val of fName is===2222===="+faBean.getOrgFileName()+"=====docType==="+faBean.getDocType());
//                    chargeBean.setDocumentList(al);
//                    
//                }
                Iterator itr=al.iterator();
                while(itr.hasNext()){
                    
                    faBean = (FileAttachBean)itr.next();
                    String fName=faBean.getOrgFileName();
                    String dType=faBean.getDocType();
                    
                    faBean.setOrgFileName(fName);
                    faBean.setDocType(dType);
                    updAl.add(faBean);
                    
                    chargeBean.setDocumentList(updAl);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return chargeBean;
    }
    
    
    public List getDPAttachedFileName(Statement stmt, int dacId){

        ResultSet rs = null;
        FileAttachBean faBean;
        List fileName=new ArrayList();
        try {
            String sql = "SELECT ORG_FILE_NAME, doc_type FROM disciplinary_charge_document WHERE dacid='"+dacId+"'";
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                faBean= new FileAttachBean();
                
                faBean.setOrgFileName(rs.getString("ORG_FILE_NAME"));
                faBean.setDocType(rs.getString("doc_type"));
                
                fileName.add(faBean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } 
        return fileName;
    } 
    
    
    @Override
    public int updateRule15Charges(Rule15ChargeBean chargeBean) {

        Connection con = null;
        PreparedStatement pstmt = null;
        int result = 0;
        try {
            con = dataSource.getConnection();
            String updChargeQry = "update disciplinary_act_charge set charge=?, charge_details=? where dacid=?";
            pstmt = con.prepareStatement(updChargeQry);
            pstmt.setString(1, chargeBean.getRule15Articles());
            pstmt.setString(2, chargeBean.getRule15ChargDtls());
            pstmt.setInt(3, chargeBean.getHidChargeDacId());

            result = pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return result;
    }
    
    @Override
    public int rule15UpdateWitness(Rule15ChargeBean chargeBean) {

        Connection con = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;
        int delRes = 0;
        int insRes = 0;
        String witnessId = "";
        try {
            con = dataSource.getConnection();
            int dacId=chargeBean.getHidWitnessDacId();
            
            String delWitnessQry="delete from disciplinary_act_charge_witness where dacid=?";        
            pstmt1 = con.prepareStatement(delWitnessQry);
            pstmt1.setInt(1, dacId);
            delRes = pstmt1.executeUpdate();
            
            String witnessQry = "INSERT INTO disciplinary_act_charge_witness (dacwid, dacid, witness_hrmsid, witness_spc) VALUES(?,?,?,?)";
            pstmt = con.prepareStatement(witnessQry);
            
            String witnessHrmsIds = chargeBean.getHidWitnessIds();
            StringTokenizer stringTokenizer = new StringTokenizer(witnessHrmsIds, ",");
            while (stringTokenizer.hasMoreTokens()) {
                
                witnessId = stringTokenizer.nextToken().trim();
                pstmt.setInt(1, CommonFunctions.getMaxCode(con, "disciplinary_act_charge_witness", "dacwid"));
                pstmt.setInt(2, dacId);
                pstmt.setString(3, witnessId);
                pstmt.setString(4, getCurSpc(witnessId));
                insRes = pstmt.executeUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return insRes;
    }
    
    
    @Override
    public Rule15ChargeBean EditRule15WitnessData(Rule15ChargeBean chargeBean, int dacId) {

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = dataSource.getConnection();
            String chargeQry = "select charge, charge_details from disciplinary_act_charge where dacid=?";
            pstmt = con.prepareStatement(chargeQry);
            pstmt.setInt(1, dacId);
            rs = pstmt.executeQuery();
            while (rs.next()) {

                chargeBean.setRule15Articles(rs.getString("charge"));
                chargeBean.setRule15ChargDtls(rs.getString("charge_details"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return chargeBean;
    }

    @Override
    public DpViewBean viewRule15DiscProceeding(String offCode, String daId) {

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        PreparedStatement pstmt1 = null;
        ResultSet rs1 = null;
        PreparedStatement pstmt2 = null;
        ResultSet rs2 = null;
        PreparedStatement pstmt3 = null;
        ResultSet rs3 = null;
        
        DpViewBean dpViewBean = new DpViewBean();

        try {
            con = dataSource.getConnection();
            String deptNameQry = "SELECT department_name FROM g_office OFFICE INNER JOIN g_department DEPT ON "
                    + "OFFICE.department_code=DEPT.department_code WHERE off_code=?";

            pstmt = con.prepareStatement(deptNameQry);
            pstmt.setString(1, offCode);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                dpViewBean.setDeptName(rs.getString("department_name"));
            }

            String dpQry = "select do_hrmsid,ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME, L_NAME], ' ') EMPNAME,post,dis_order_no,dis_order_date, "
                    + "irre_details from disciplinary_authority DP INNER JOIN emp_mast EM ON DP.do_hrmsid=EM.emp_id "
                    + "LEFT OUTER JOIN g_spc GS ON DP.do_spc=GS.spc LEFT OUTER JOIN g_post GP ON GP.post_code= GS.gpc where daid=?";
            pstmt1 = con.prepareStatement(dpQry);
            pstmt1.setInt(1, Integer.parseInt(daId));
            rs1 = pstmt1.executeQuery();
            while (rs1.next()) {
                dpViewBean.setDoEmpHrmsId(rs1.getString("do_hrmsid"));
                dpViewBean.setDoEmpName(rs1.getString("EMPNAME"));
                dpViewBean.setDoEmpCurDegn(rs1.getString("post"));
                dpViewBean.setRule15OrderNo(rs1.getString("dis_order_no"));
                dpViewBean.setRule15OrderDate(CommonFunctions.getFormattedOutputDate1(rs1.getDate("dis_order_date")));
                dpViewBean.setIrrgularDetails(rs1.getString("irre_details"));
            }

            ChargeWitnesBean tBean = null;
            List chargeListOnly = new ArrayList();
            List chargeDtlsList = new ArrayList();
            
            String chargeListQry = "select charge, charge_details from disciplinary_act_charge where daid=? order by charge asc";
            pstmt2 = con.prepareStatement(chargeListQry);
            pstmt2.setInt(1, Integer.parseInt(daId));
            rs2 = pstmt2.executeQuery();
            while (rs2.next()) {
                tBean = new ChargeWitnesBean();
                tBean.setField(rs2.getString("charge"));
                chargeListOnly.add(tBean);
                        
                tBean = new ChargeWitnesBean();
                tBean.setField(rs2.getString("charge_details"));
                chargeDtlsList.add(tBean);
            }
            dpViewBean.setChargeListOnly(chargeListOnly);
            dpViewBean.setChargeDtlsList(chargeDtlsList);
            
            
            int dacId = 0;
            List chargeList = new ArrayList();
            String fileAndWitnessQry="select DAC.dacid dacId,charge,org_file_name from disciplinary_act_charge DAC "
                    + "INNER JOIN disciplinary_charge_document DCD ON DAC.dacid=DCD.dacid where daid=? order by charge asc";
            pstmt3 = con.prepareStatement(fileAndWitnessQry);
            pstmt3.setInt(1, Integer.parseInt(daId));
            rs3 = pstmt3.executeQuery();
            while (rs3.next()) {
                tBean = new ChargeWitnesBean();
                
                dacId = rs3.getInt("dacId");
                
                tBean.setCharge(rs3.getString("charge"));
                tBean.setOrgFileName(rs3.getString("org_file_name"));
                tBean.setWitnessName(getWitnessNames(con, dacId));
                
                chargeList.add(tBean);
            }
            dpViewBean.setChargeList(chargeList);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return dpViewBean;
    }

    private String getWitnessNames(Connection con, int dacId) {

        PreparedStatement pstmt3 = null;
        ResultSet rs3 = null;
        String witness = "";
        
        try {
            String witnessQry = "select ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME, L_NAME], ' ') EMPNAME, spn from emp_mast EM "
                    + "INNER JOIN disciplinary_act_charge_witness DACW ON EM.emp_id=DACW.witness_hrmsid "
                    + "LEFT OUTER JOIN g_spc GS ON DACW.witness_spc=GS.spc where dacid=?";
            pstmt3 = con.prepareStatement(witnessQry);
            pstmt3.setInt(1, dacId);
            rs3 = pstmt3.executeQuery();
            while (rs3.next()) {
                
                String spn = rs3.getString("spn"); // spn means emp desgn with office address
                if(spn == null || spn.equals("null")){
                    spn="";
                }
                witness = witness + rs3.getString("EMPNAME") + " " + spn;
                witness = witness + "<br/>";
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return witness;
    }

    @Override
    public int deleteChargeAndWitness(int dacId) {

        Connection con = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;
        int delResult = 0;
        try {
            con = dataSource.getConnection();
            String delWitnessQry = "delete from disciplinary_act_charge_witness where dacid=?";
            pstmt = con.prepareStatement(delWitnessQry);
            pstmt.setInt(1, dacId);
            int wRes = pstmt.executeUpdate();

            String delChargeQry = "delete from disciplinary_act_charge where dacid=?";
            pstmt1 = con.prepareStatement(delChargeQry);
            pstmt1.setInt(1, dacId);
            int cRes = pstmt1.executeUpdate();

            if (wRes == 1 && cRes == 1) {
                delResult = 1;
            } else {
                delResult = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return delResult;
    }
    
    @Override
    public int deleteWitnessOnly(int dacId) {

        Connection con = null;
        PreparedStatement pstmt = null;
        int delResult = 0;
        try {
            con = dataSource.getConnection();
            String delWitnessQry = "delete from disciplinary_act_charge_witness where dacid=?";
            pstmt = con.prepareStatement(delWitnessQry);
            pstmt.setInt(1, dacId);
            int wRes = pstmt.executeUpdate();

            if (wRes == 1) {
                delResult = 1;
            } else {
                delResult = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return delResult;
    }
    
//    @Override
//    public DpViewBean forwardDiscProced(String offCode, String daId) {
//
//        Connection con = null;
//        PreparedStatement pstmt = null;
//        ResultSet rs = null;
//        DpViewBean dpViewBean = new DpViewBean();
//
//        try {
//            con = dataSource.getConnection();
//            String deptNameQry = "SELECT department_name FROM g_office OFFICE INNER JOIN g_department DEPT ON "
//                    + "OFFICE.department_code=DEPT.department_code WHERE off_code=?";
//
//            pstmt = con.prepareStatement(deptNameQry);
//            pstmt.setString(1, offCode);
//            rs = pstmt.executeQuery();
//            while (rs.next()) {
//                dpViewBean.setDeptName(rs.getString("department_name"));
//            }
//
//            
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            DataBaseFunctions.closeSqlObjects(con);
//        }
//        return dpViewBean;
//    }
    
}
