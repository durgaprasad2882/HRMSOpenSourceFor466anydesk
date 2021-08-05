package hrms.dao.joining;

import hrms.common.CommonFunctions;
import hrms.common.DataBaseFunctions;
import hrms.common.Message;
import hrms.model.joining.JoiningForm;
import hrms.model.joining.JoiningList;
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
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.apache.commons.lang.StringUtils;

public class JoiningDAOImpl implements JoiningDAO {

    @Resource(name = "dataSource")
    protected DataSource dataSource;

    protected MaxJoiningIdDAO maxJoiningIdDAO;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setMaxJoiningIdDAO(MaxJoiningIdDAO maxJoiningIdDao) {
        this.maxJoiningIdDAO = maxJoiningIdDao;
    }

    @Override
    public List getJoiningList(String empid) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        List jlist = new ArrayList();
        JoiningList jbean = null;
        try {
            con = this.dataSource.getConnection();
            String sql = "select * from (select TEMP.* from (select temp.sv_id,temp.ntype tntype,temp.notid tnid,temp.nordno tnordno,temp.nordt"
                    + " tnordt,temp.nauth tnauth,temp.notnote tnnote,"
                    + " temp.ndeptcode tndeptcode,temp.noffcode tnoffcode,temp.rid trid,temp.rmemono trmemono,"
                    + " temp.rmemodt trmemodt,temp.rdate trdate,temp.rtime trtime,temp.rdoj trdoj,temp.rtoj trtoj,joi.join_id joinid,joi.not_type jnotype,"
                    + " joi.not_id jnotid,joi.doe jdoe,joi.memo_no jmemono,joi.memo_date jmemodt,joi.join_date joindt,joi.join_time jointime,joi.spc jspc,"
                    + " joi.note jnote,joi.if_ad_charge jaddcharge,joi.lcr_id lcrid,joi.tr_data_type jointrtype,joi.sv_id svid from (SELECT noti.sv_id,"
                    + " noti.not_id notid,noti.not_type ntype,noti.emp_id nempid,"
                    + " noti.doe ndoe,noti.ordno nordno,noti.orddt nordt,noti.dept_code ndeptcode,noti.off_code noffcode,noti.auth nauth,noti.note notnote,"
                    + " reli.relieve_id rid,reli.doe rdoe,reli.memo_no rmemono,reli.memo_date rmemodt,reli.rlv_date rdate,reli.rlv_time rtime,"
                    + " reli.spc rspc,reli.due_doj rdoj,reli.due_toj rtoj FROM (SELECT * FROM emp_notification WHERE EMP_ID='" + empid + "') noti"
                    + " left outer join (Select * from emp_relieve where emp_id='" + empid + "') reli on noti.emp_id=reli.emp_id and"
                    + " noti.not_id=reli.not_id and reli.join_id not in (Select Join_id from emp_join where EMP_ID='" + empid + "' AND if_ad_charge='Y'))"
                    + " temp left outer join emp_join  joi on temp.nempid=joi.emp_id and temp.notid=joi.not_id WHERE temp.nempid='" + empid + "' and"
                    + " (temp.ntype='FIRST_APPOINTMENT' or temp.ntype='REHABILITATION' or temp.ntype='ABSORPTION' or temp.ntype='REDEPLOYMENT' or"
                    + " temp.ntype='VALIDATION' or temp.ntype='LT_TRAINING' or temp.ntype='DEPUTATION' or temp.ntype='POSTING' or temp.ntype='TRANSFER'"
                    + " or temp.ntype='PROMOTION' or temp.ntype='ADDITIONAL_CHARGE' or temp.ntype='ALLOT_CADRE' or temp.ntype='REDESIGNATION' or"
                    + " temp.ntype='CHNG_STRUCTURE') and"
                    + " (temp.notid not in (select not_id from emp_cadre where emp_cadre.not_id=temp.notid and joined_assuch='Y' and EMP_ID='" + empid + "'))"
                    + " order by joindt desc) TEMP)jointemp";
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                jbean = new JoiningList();
                jbean.setNotid(rs.getString("tnid"));
                jbean.setNotType(rs.getString("tntype"));
                jbean.setNotOrdNo(rs.getString("tnordno"));
                if (rs.getDate("tnordt") != null && !rs.getDate("tnordt").equals("")) {
                    jbean.setNotOrdDt(CommonFunctions.getFormattedOutputDate1(rs.getDate("tnordt")));
                }
                if(rs.getString("trid") != null && !rs.getString("trid").equals("")){
                    jbean.setRlvid(rs.getString("trid"));
                }else{
                    jbean.setRlvid("");
                }
                if(rs.getString("joinid") != null && !rs.getString("joinid").equals("")){
                    jbean.setJoinid(rs.getString("joinid"));
                }else{
                    jbean.setJoinid("");
                }
                //System.out.println("Join Id is inside list: "+jbean.getJoinid());
                if (rs.getDate("trdoj") != null && !rs.getDate("trdoj").equals("")) {
                    jbean.setJoiningDueDt(CommonFunctions.getFormattedOutputDate1(rs.getDate("trdoj")));
                }
                jbean.setJoiningDueTime(rs.getString("trtoj"));
                jbean.setJoiningOrdNo(rs.getString("jmemono"));
                if (rs.getDate("jmemodt") != null && !rs.getDate("jmemodt").equals("")) {
                    jbean.setJoiningOrdDt(CommonFunctions.getFormattedOutputDate1(rs.getDate("jmemodt")));
                }
                if (rs.getDate("joindt") != null && !rs.getDate("joindt").equals("")) {
                    jbean.setJoiningDt(CommonFunctions.getFormattedOutputDate1(rs.getDate("joindt")));
                }
                jbean.setJoiningTime(rs.getString("jointime"));
                if(rs.getString("lcrid") != null && !rs.getString("lcrid").equals("")){
                    jbean.setLcrid(rs.getString("lcrid"));
                }else{
                    jbean.setLcrid("");
                }
                if(rs.getString("jaddcharge") != null && !rs.getString("jaddcharge").equals("")){
                    jbean.setAdditionalCharge(rs.getString("jaddcharge"));
                }else{
                    jbean.setAdditionalCharge("");
                }
                jlist.add(jbean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return jlist;
    }

    @Override
    public int getJoiningListCount(String empid) {

        Connection con = null;

        Statement stmt = null;
        ResultSet rs = null;

        int count = 0;
        try {
            con = this.dataSource.getConnection();

            String sql = "select count(*) cnt from (select * from (select TEMP.* from (select temp.sv_id,temp.ntype tntype,temp.notid tnid,temp.nordno tnordno,temp.nordt"
                    + " tnordt,temp.nauth tnauth,temp.notnote tnnote,"
                    + " temp.ndeptcode tndeptcode,temp.noffcode tnoffcode,temp.rid trid,temp.rmemono trmemono,"
                    + " temp.rmemodt trmemodt,temp.rdate trdate,temp.rtime trtime,temp.rdoj trdoj,temp.rtoj trtoj,joi.join_id joinid,joi.not_type jnotype,"
                    + " joi.not_id jnotid,joi.doe jdoe,joi.memo_no jmemono,joi.memo_date jmemodt,joi.join_date joindt,joi.join_time jointime,joi.spc jspc,"
                    + " joi.note jnote,joi.if_ad_charge jaddcharge,joi.lcr_id lcrid,joi.tr_data_type jointrtype,joi.sv_id svid from (SELECT noti.sv_id,"
                    + " noti.not_id notid,noti.not_type ntype,noti.emp_id nempid,"
                    + " noti.doe ndoe,noti.ordno nordno,noti.orddt nordt,noti.dept_code ndeptcode,noti.off_code noffcode,noti.auth nauth,noti.note notnote,"
                    + " reli.relieve_id rid,reli.doe rdoe,reli.memo_no rmemono,reli.memo_date rmemodt,reli.rlv_date rdate,reli.rlv_time rtime,"
                    + " reli.spc rspc,reli.due_doj rdoj,reli.due_toj rtoj FROM (SELECT * FROM emp_notification WHERE EMP_ID='" + empid + "') noti"
                    + " left outer join (Select * from emp_relieve where emp_id='" + empid + "') reli on noti.emp_id=reli.emp_id and"
                    + " noti.not_id=reli.not_id and reli.join_id not in (Select Join_id from emp_join where EMP_ID='" + empid + "' AND if_ad_charge='Y'))"
                    + " temp left outer join emp_join  joi on temp.nempid=joi.emp_id and temp.notid=joi.not_id WHERE temp.nempid='" + empid + "' and"
                    + " (temp.ntype='FIRST_APPOINTMENT' or temp.ntype='REHABILITATION' or temp.ntype='ABSORPTION' or temp.ntype='REDEPLOYMENT' or"
                    + " temp.ntype='VALIDATION' or temp.ntype='LT_TRAINING' or temp.ntype='DEPUTATION' or temp.ntype='POSTING' or temp.ntype='TRANSFER'"
                    + " or temp.ntype='PROMOTION' or temp.ntype='ADDITIONAL_CHARGE' or temp.ntype='ALLOT_CADRE' or temp.ntype='REDESIGNATION' or"
                    + " temp.ntype='CHNG_STRUCTURE') and"
                    + " (temp.notid not in (select not_id from emp_cadre where emp_cadre.not_id=temp.notid and joined_assuch='Y' and EMP_ID='" + empid + "'))"
                    + " order by joindt desc) TEMP)jointemp)temp";
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                count = rs.getInt("cnt");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return count;
    }

    @Override
    public JoiningForm getJoiningData(String empid, String notid, String rlvid, String leaveid, String addl,String jid) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        String sql = "";

        JoiningForm jform = new JoiningForm();
        try {
            con = this.dataSource.getConnection();

            if (notid != null && !notid.equals("")) {
                //sql = "SELECT NOT_ID,NOT_TYPE,ORDNO,EMP_ID,ORDDT,DEPT_CODE,NOTE,OFF_CODE,AUTH FROM EMP_NOTIFICATION WHERE NOT_ID=? AND EMP_ID=?";
                sql = "SELECT NOT_ID,NOT_TYPE,ORDNO,EMP_ID,ORDDT,EMP_NOTIFICATION.DEPT_CODE,NOTE,EMP_NOTIFICATION.OFF_CODE,AUTH,DEPARTMENT_NAME,OFF_EN,SPN FROM"
                        + " (SELECT NOT_ID,NOT_TYPE,ORDNO,EMP_ID,ORDDT,DEPT_CODE,NOTE,OFF_CODE,AUTH FROM EMP_NOTIFICATION WHERE"
                        + " NOT_ID=? AND EMP_ID=?)EMP_NOTIFICATION"
                        + " LEFT OUTER JOIN G_DEPARTMENT ON EMP_NOTIFICATION.DEPT_CODE=G_DEPARTMENT.DEPARTMENT_CODE"
                        + " LEFT OUTER JOIN G_OFFICE ON EMP_NOTIFICATION.OFF_CODE=G_OFFICE.OFF_CODE"
                        + " LEFT OUTER JOIN G_SPC ON EMP_NOTIFICATION.AUTH=G_SPC.SPC";
                pst = con.prepareStatement(sql);
                pst.setString(1, notid);
                pst.setString(2, empid);
                rs = pst.executeQuery();
                if (rs.next()) {
                    jform.setJempid(empid);
                    jform.setNotId(rs.getString("NOT_ID"));
                    jform.setNotType(rs.getString("NOT_TYPE"));
                    jform.setNotOrdNo(rs.getString("ORDNO"));
                    if (rs.getDate("ORDDT") != null && !rs.getDate("ORDDT").equals("")) {
                        jform.setNotOrdDt(CommonFunctions.getFormattedOutputDate1(rs.getDate("ORDDT")));
                    }
                    jform.setNotiDeptName(rs.getString("DEPARTMENT_NAME"));
                    jform.setNotiOffName(rs.getString("OFF_EN"));
                    jform.setNotiSpn(rs.getString("SPN"));
                    System.out.println("DEPT NAME is: "+jform.getNotiDeptName());
                }
            }

            if (rlvid != null && !rlvid.equals("")) {
                sql = "SELECT RELIEVE_ID,MEMO_NO,MEMO_DATE,RLV_DATE,RLV_TIME,DUE_DOJ,DUE_TOJ FROM EMP_RELIEVE WHERE RELIEVE_ID=? AND EMP_ID=?";
                pst = con.prepareStatement(sql);
                pst.setString(1, rlvid);
                pst.setString(2, empid);
                rs = pst.executeQuery();
                if (rs.next()) {
                    jform.setRlvId(rs.getString("RELIEVE_ID"));
                    jform.setRlvOrdNo(rs.getString("MEMO_NO"));
                    if (rs.getDate("MEMO_DATE") != null && !rs.getDate("MEMO_DATE").equals("")) {
                        jform.setRlvOrdDt(CommonFunctions.getFormattedOutputDate1(rs.getDate("MEMO_DATE")));
                    }
                    if (rs.getDate("RLV_DATE") != null && !rs.getDate("RLV_DATE").equals("")) {
                        jform.setRlvDt(CommonFunctions.getFormattedOutputDate1(rs.getDate("RLV_DATE")));
                    }
                    jform.setRlvTime(rs.getString("RLV_TIME"));
                    if (rs.getDate("DUE_DOJ") != null && !rs.getDate("DUE_DOJ").equals("")) {
                        jform.setJoiningDueDt(CommonFunctions.getFormattedOutputDate1(rs.getDate("DUE_DOJ")));
                    }
                    jform.setJoiningDueTime(rs.getString("DUE_TOJ"));
                }
            }

            if (jid != null && !jid.equals("")) {
                sql = "SELECT JOIN_ID,DOE,MEMO_NO,MEMO_DATE,JOIN_DATE,JOIN_TIME,EMP_JOIN.SPC,NOTE,IF_AD_CHARGE,AUTH_DEPT,AUTH_OFF,LCR_ID,IF_ASSUMED,IF_VISIBLE,ENT_OFF,ENT_DEPT,ENT_AUTH,GPC,field_off_code,SPN FROM EMP_JOIN LEFT OUTER JOIN G_SPC ON EMP_JOIN.SPC=G_SPC.SPC WHERE JOIN_ID=? AND EMP_ID=?";
                pst = con.prepareStatement(sql);
                pst.setString(1, jid);
                pst.setString(2, empid);
                rs = pst.executeQuery();
                if (rs.next()) {
                    jform.setJoinId(rs.getString("JOIN_ID"));
                    jform.setJoiningOrdNo(rs.getString("MEMO_NO"));
                    if (rs.getDate("MEMO_DATE") != null && !rs.getDate("MEMO_DATE").equals("")) {
                        jform.setJoiningOrdDt(CommonFunctions.getFormattedOutputDate1(rs.getDate("MEMO_DATE")));
                    }
                    if (rs.getDate("JOIN_DATE") != null && !rs.getDate("JOIN_DATE").equals("")) {
                        jform.setJoiningDt(CommonFunctions.getFormattedOutputDate1(rs.getDate("JOIN_DATE")));
                    }
                    jform.setSltJoiningTime(rs.getString("JOIN_TIME"));
                    jform.setChkSBVisivle(rs.getString("IF_VISIBLE"));
                    //jform.setChkujt(rs.getString("IF_VISIBLE"));
                    jform.setHidDeptCode(rs.getString("AUTH_DEPT"));
                    jform.setHidOffCode(rs.getString("AUTH_OFF"));
                    jform.setHidTempOffCode(rs.getString("AUTH_OFF"));
                    jform.setHidPostCode(rs.getString("GPC"));
                    jform.setHidTempPostCode(rs.getString("GPC"));
                    jform.setJspc(rs.getString("SPC"));
                    jform.setSpn(rs.getString("SPN"));
                    jform.setHidTempSpc(rs.getString("SPC"));
                    jform.setHidLcrId(rs.getString("lcr_id"));
                    jform.setSltFieldOffice(rs.getString("field_off_code"));
                    jform.setHidTempFieldOffCode(rs.getString("field_off_code"));
                    jform.setHidAddition(addl);
                    jform.setNote(rs.getString("NOTE"));
                }
                sql = "SELECT SP_FROM,SP_TO FROM EMP_LEAVE_CR WHERE LCR_ID='" + jform.getHidLcrId() + "'";
                pst = con.prepareStatement(sql);
                rs = pst.executeQuery();
                if (rs.next()) {
                    if (rs.getDate("SP_FROM") != null && !rs.getDate("SP_FROM").equals("")) {
                        jform.setUjtFrmDt(CommonFunctions.getFormattedOutputDate1(rs.getDate("SP_FROM")));
                    }
                    if (rs.getDate("SP_TO") != null && !rs.getDate("SP_TO").equals("")) {
                        jform.setUjtToDt(CommonFunctions.getFormattedOutputDate1(rs.getDate("SP_TO")));
                    }
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return jform;
    }

    @Override
    public void saveJoining(JoiningForm jform, String entDeptCode, String entOffCode, String entSpc) {

        Connection con = null;

        PreparedStatement pst = null;

        boolean ret = false;
        
        try {
            con = this.dataSource.getConnection();

            String curDate = "";
            Calendar cal = Calendar.getInstance();
            DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");
            curDate = dateFormat.format(cal.getTime());

            String nexttoe = CommonFunctions.getNextToe(jform.getJempid(), curDate, con);

            if (jform.getJoinId() != null && !jform.getJoinId().equals("")) {
                String sql = "UPDATE EMP_JOIN SET MEMO_NO=?,MEMO_DATE=?,JOIN_DATE=?,JOIN_TIME=?,NOTE=?,NOT_ID=?,NOT_TYPE=?,IF_AD_CHARGE=?,AUTH_DEPT=?,AUTH_OFF=?,SPC=?,IF_ASSUMED=?,IF_VISIBLE=?,ENT_DEPT=?,ENT_OFF=?,ENT_AUTH=?,field_off_code=? WHERE JOIN_ID=? AND EMP_ID=?";
                pst = con.prepareStatement(sql);
                pst.setString(1, jform.getJoiningOrdNo());
                if(jform.getJoiningOrdDt() != null && !jform.getJoiningOrdDt().equals("")){
                    pst.setTimestamp(2, new Timestamp(new SimpleDateFormat("dd-MMM-yyyy").parse(jform.getJoiningOrdDt()).getTime()));
                }else{
                    pst.setTimestamp(2,null);
                }
                if(jform.getJoiningDt() != null && !jform.getJoiningDt().equals("")){
                    pst.setTimestamp(3, new Timestamp(new SimpleDateFormat("dd-MMM-yyyy").parse(jform.getJoiningDt()).getTime()));
                }else{
                    pst.setTimestamp(3,null);
                }
                pst.setString(4, jform.getSltJoiningTime());
                pst.setString(5, jform.getNote());
                pst.setString(6, jform.getNotId());
                pst.setString(7, jform.getNotType());
                pst.setString(8, jform.getHidAddition());
                pst.setString(9, jform.getHidDeptCode());
                pst.setString(10, jform.getHidOffCode());
                pst.setString(11, jform.getJspc());
                pst.setString(12, "");
                pst.setString(13, jform.getChkSBVisivle());
                pst.setString(14, entDeptCode);
                pst.setString(15, entOffCode);
                pst.setString(16, entSpc);
                pst.setString(17, jform.getSltFieldOffice());
                pst.setString(18, jform.getJoinId());
                pst.setString(19, jform.getJempid());
                pst.executeUpdate();

                if (jform.getHidLcrId() != null && !jform.getHidLcrId().equals("")) {
                    if ((jform.getUjtFrmDt() != null && !jform.getUjtFrmDt().equalsIgnoreCase("")) || (jform.getUjtToDt() != null && !jform.getUjtToDt().equalsIgnoreCase(""))) {
                        pst = con.prepareStatement("UPDATE EMP_LEAVE_CR SET tol_id=?,cr_type=?,sp_from=?,sp_to=? WHERE emp_id=? and lcr_id=?");
                        pst.setString(1, "EL");
                        pst.setString(2, "U");
                        if (jform.getUjtFrmDt() != null && !jform.getUjtFrmDt().equalsIgnoreCase("")) {
                            pst.setTimestamp(3, new Timestamp(new SimpleDateFormat("dd-MMM-yyyy").parse(jform.getUjtFrmDt()).getTime()));
                        } else {
                            pst.setTimestamp(3, null);
                        }
                        if (jform.getUjtToDt() != null && !jform.getUjtToDt().equals("")) {
                            pst.setTimestamp(4, new Timestamp(new SimpleDateFormat("dd-MMM-yyyy").parse(jform.getUjtToDt()).getTime()));
                        } else {
                            pst.setTimestamp(4, null);
                        }
                        pst.setString(5, jform.getJempid());
                        pst.setString(6, jform.getHidLcrId());
                        pst.executeUpdate();
                    } else {
                        pst = con.prepareStatement("delete from emp_leave_cr where emp_id=? and cr_type='U' and lcr_id=?");
                        pst.setString(1, jform.getJempid());
                        pst.setString(2, jform.getHidLcrId());
                        pst.executeUpdate();

                        pst = con.prepareStatement("UPDATE EMP_JOIN SET LCR_ID=? WHERE JOIN_ID=? AND EMP_ID=?");
                        pst.setString(1, null);
                        pst.setString(2, jform.getJoinId());
                        pst.setString(3, jform.getJempid());
                        pst.executeUpdate();
                    }
                } else {
                    if ((jform.getUjtFrmDt() != null && !jform.getUjtFrmDt().equalsIgnoreCase("")) || (jform.getUjtToDt() != null && !jform.getUjtToDt().equalsIgnoreCase(""))) {
                        String lcrCode = CommonFunctions.getMaxCode("EMP_LEAVE_CR", "LCR_ID", con);
                        pst = con.prepareStatement("INSERT INTO EMP_LEAVE_CR(LCR_ID,EMP_ID,TOL_ID,CR_TYPE,SP_FROM,SP_TO) values (?,?,?,?,?,?)");
                        pst.setString(1, lcrCode);
                        pst.setString(2, jform.getJempid());
                        pst.setString(3, "EL");
                        pst.setString(4, "U");
                        if (jform.getUjtFrmDt() != null && !jform.getUjtFrmDt().equals("")) {
                            pst.setTimestamp(5, new Timestamp(new SimpleDateFormat("dd-MMM-yyyy").parse(jform.getUjtFrmDt()).getTime()));
                        } else {
                            pst.setTimestamp(5, null);
                        }
                        if (jform.getUjtToDt() != null && !jform.getUjtToDt().equals("")) {
                            pst.setTimestamp(6, new Timestamp(new SimpleDateFormat("dd-MMM-yyyy").parse(jform.getUjtToDt()).getTime()));
                        } else {
                            pst.setTimestamp(6, null);
                        }
                        pst.executeUpdate();

                        pst = con.prepareStatement("UPDATE EMP_JOIN SET LCR_ID=? WHERE JOIN_ID=? AND EMP_ID=?");
                        pst.setString(1, lcrCode);
                        pst.setString(2, jform.getJoinId());
                        pst.setString(3, jform.getJempid());
                        pst.executeUpdate();
                    }
                }
            } else {
                String sql = "INSERT INTO EMP_JOIN(join_id, not_type, not_id, emp_id, doe,memo_no, memo_date, join_date, join_time, auth_dept, auth_off, spc,if_ad_charge, note, LCR_ID, TOE, IF_ASSUMED,IF_VISIBLE,ENT_DEPT,ENT_OFF,ENT_AUTH,field_off_code) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                pst = con.prepareStatement(sql);
                pst.setString(1, maxJoiningIdDAO.getMaxJoiningId());
                pst.setString(2, jform.getNotType());
                pst.setString(3, jform.getNotId());
                pst.setString(4, jform.getJempid());
                pst.setTimestamp(5, new Timestamp(new SimpleDateFormat("dd-MMM-yyyy").parse(curDate).getTime()));
                pst.setString(6, jform.getJoiningOrdNo());
                if(jform.getJoiningOrdDt() != null && !jform.getJoiningOrdDt().equals("")){
                    pst.setTimestamp(7, new Timestamp(new SimpleDateFormat("dd-MMM-yyyy").parse(jform.getJoiningOrdDt()).getTime()));
                }else{
                    pst.setTimestamp(7,null);
                }
                if(jform.getJoiningDt() != null && !jform.getJoiningDt().equals("")){
                    pst.setTimestamp(8, new Timestamp(new SimpleDateFormat("dd-MMM-yyyy").parse(jform.getJoiningDt()).getTime()));
                }else{
                    pst.setTimestamp(8,null);
                }
                pst.setString(9, jform.getSltJoiningTime());
                pst.setString(10, jform.getHidDeptCode());
                pst.setString(11, jform.getHidOffCode());
                pst.setString(12, jform.getJspc());
                pst.setString(13, jform.getHidAddition());
                pst.setString(14, jform.getNote());
                pst.setString(15, jform.getHidLcrId());
                pst.setString(16, nexttoe);
                pst.setString(17, "");
                pst.setString(18, jform.getChkSBVisivle());
                pst.setString(19, entDeptCode);
                pst.setString(20, entOffCode);
                pst.setString(21, entSpc);
                pst.setString(22, jform.getSltFieldOffice());
                pst.executeUpdate();

                if ((jform.getUjtFrmDt() != null && !jform.getUjtFrmDt().equals(""))
                        || (jform.getUjtToDt() != null && !jform.getUjtToDt().equals(""))) {
                    String lcrCode = CommonFunctions.getMaxCode("EMP_LEAVE_CR", "LCR_ID", con);
                    pst = con.prepareStatement("INSERT INTO EMP_LEAVE_CR(LCR_ID,EMP_ID,TOL_ID,CR_TYPE,SP_FROM,SP_TO) values (?,?,?,?,?,?)");
                    pst.setString(1, lcrCode);
                    pst.setString(2, jform.getJempid());
                    pst.setString(3, "EL");
                    pst.setString(4, "U");
                    if (jform.getUjtFrmDt() != null && !jform.getUjtFrmDt().equals("")) {
                        pst.setTimestamp(5, new Timestamp(new SimpleDateFormat("dd-MMM-yyyy").parse(jform.getUjtFrmDt()).getTime()));
                    } else {
                        pst.setTimestamp(5, null);
                    }
                    if (jform.getUjtToDt() != null && !jform.getUjtToDt().equals("")) {
                        pst.setTimestamp(6, new Timestamp(new SimpleDateFormat("dd-MMM-yyyy").parse(jform.getUjtToDt()).getTime()));
                    } else {
                        pst.setTimestamp(6, null);
                    }
                    pst.executeUpdate();
                }
            }

            if (curDate != null && !curDate.trim().equals("")) {
                boolean updatepay = CommonFunctions.isupdatePayOrPostingInfo(jform.getJempid(), jform.getJoiningDt(), jform.getJoiningOrdDt(), "POSTING", con);
                if (updatepay == true) {
                    ret = updatepay;
                    CommonFunctions.updateEmpPostingInfoOnDate(jform.getJempid(), curDate, con);
                    pst = con.prepareStatement("UPDATE EMP_MAST SET NEXT_OFFICE_CODE=? WHERE EMP_ID=?");
                    pst.setString(1, "");
                    pst.setString(2, jform.getJempid());
                    pst.execute();
                } else {
                    ret = false;
                }
            }

            if (ret == true) {
                CommonFunctions.modifyEmpCurStatus(jform.getJempid(), "ON DUTY", con);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    @Override
    public void deleteJoining(JoiningForm jform) {
        
        Connection con = null;
        
        PreparedStatement pst = null;
        
        try{
            con = this.dataSource.getConnection();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            DataBaseFunctions.closeSqlObjects(con);
        }
    }
}
