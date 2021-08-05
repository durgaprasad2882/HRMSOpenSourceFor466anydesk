package hrms.dao.relieve;

import hrms.SelectOption;
import hrms.common.CommonFunctions;
import hrms.common.DataBaseFunctions;
import hrms.model.relieve.RelieveForm;
import hrms.model.relieve.RelieveBean;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;

public class RelieveDAOImpl implements RelieveDAO {

    @Resource(name = "dataSource")
    protected DataSource dataSource;

    protected MaxRelieveIdDAOImpl maxRelieveIdDao;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setMaxRelieveIdDao(MaxRelieveIdDAOImpl maxRelieveIdDao) {
        this.maxRelieveIdDao = maxRelieveIdDao;
    }

    @Override
    public List getRelieveList(String empid) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        RelieveBean rform = null;
        ArrayList alist = new ArrayList();
        try {
            con = this.dataSource.getConnection();

            String sql = "select * from (select TEMP.* from (SELECT NOTI.NOT_TYPE AS NOTIFYTYPE, NOTI.NOT_ID AS NOTIFYID,NOTI.DOE AS NOTIYDOE,NOTI.ORDNO"
                    + " AS NOTIYORDNO,NOTI.ORDDT"
                    + " AS NOTIYORDDT,NOTI.DEPT_CODE AS NOTIYCODE,NOTI.OFF_CODE AS NOTIOFFCODE,NOTI.AUTH AS NOTIYAUTH ,RE.RELIEVE_ID AS RLID,RE.DOE AS"
                    + " RLDOE,RE.MEMO_NO AS RLMEMONO,RE.MEMO_DATE AS RLMEMODT,RE.RLV_DATE AS RLDATE,RE.RLV_TIME AS RLTIME,RE.SPC AS RLAUTH,RE.DUE_DOJ AS"
                    + " RLDUEDATE,RE.DUE_TOJ AS RLDUETIME,RE.IF_RLNQ AS RLNQ,RE.TR_DATA_TYPE TRTYPE,RE.SV_ID FROM (SELECT * FROM EMP_NOTIFICATION WHERE"
                    + " EMP_ID=?) NOTI"
                    + " LEFT OUTER JOIN (SELECT EMP_RELIEVE.RELIEVE_ID,EMP_RELIEVE.NOT_TYPE,EMP_RELIEVE.NOT_ID,EMP_RELIEVE.EMP_ID,EMP_RELIEVE.DOE,"
                    + " EMP_RELIEVE.MEMO_NO,EMP_RELIEVE.MEMO_DATE,EMP_RELIEVE.RLV_DATE,EMP_RELIEVE.RLV_TIME,EMP_RELIEVE.SPC,EMP_RELIEVE.DUE_DOJ,"
                    + " EMP_RELIEVE.DUE_TOJ, EMP_RELIEVE.NOTE, EMP_RELIEVE.JOIN_ID,EMP_RELIEVE.IF_RLNQ,EMP_RELIEVE.TR_DATA_TYPE,EMP_RELIEVE.SV_ID"
                    + " FROM (SELECT * FROM EMP_RELIEVE WHERE"
                    + " EMP_ID=? AND (JOIN_ID IS NULL OR (JOIN_ID NOT IN(SELECT JOIN_ID FROM EMP_JOIN WHERE EMP_ID=? AND IF_AD_CHARGE='Y'))))"
                    + " EMP_RELIEVE LEFT OUTER JOIN (SELECT * FROM EMP_JOIN WHERE EMP_ID=?) EMP_JOIN ON"
                    + " (EMP_RELIEVE.JOIN_ID=EMP_JOIN.JOIN_ID) AND"
                    + " if_ad_charge='N') RE ON NOTI.NOT_ID=RE.NOT_ID WHERE NOTI.EMP_ID=? and (NOTI.not_type ='REHABILITATION' or"
                    + " NOTI.not_type='ABSORPTION' or NOTI.not_type='REDEPLOYMENT' or NOTI.not_type='VALIDATION' or NOTI.not_type='LT_TRAINING' or"
                    + " NOTI.not_type='DEPUTATION' or NOTI.not_type ='TRANSFER' or NOTI.not_type ='PROMOTION' or NOTI.not_type ='REDESIGNATION' or"
                    + " NOTI.not_type ='RESIGNATION' or NOTI.not_type ='DECEASED' or NOTI.not_type ='RETIREMENT' or"
                    + " NOTI.not_type='ALLOT_CADRE' or (NOTI.not_type='LEAVE' AND NOTI.NOT_ID IN (SELECT NOT_ID FROM EMP_LEAVE WHERE EMP_ID=?"
                    + " AND IF_LONGTERM='Y'))) and (NOTI.not_id not in (select not_id from emp_cadre where emp_cadre.not_id=NOTI.not_id and"
                    + " joined_assuch='Y' and EMP_ID=?)) ORDER BY RLDATE DESC)TEMP)relievetemp";
            System.out.println("SQL for Relieve List is: " + sql);
            pst = con.prepareStatement(sql);
            pst.setString(1,empid);
            pst.setString(2,empid);
            pst.setString(3,empid);
            pst.setString(4,empid);
            pst.setString(5,empid);
            pst.setString(6,empid);
            pst.setString(7,empid);
            rs = pst.executeQuery();
            while (rs.next()) {
                rform = new RelieveBean();
                rform.setRlvid(rs.getString("RLID"));
                rform.setNotid(rs.getString("NOTIFYID"));
                rform.setNotType(rs.getString("NOTIFYTYPE"));
                if (rs.getDate("NOTIYDOE") != null && !rs.getDate("NOTIYDOE").equals("")) {
                    rform.setNotdoe(CommonFunctions.getFormattedOutputDate1(rs.getDate("NOTIYDOE")));
                }
                rform.setNotordno(rs.getString("NOTIYORDNO"));
                if (rs.getDate("NOTIYORDDT") != null && !rs.getDate("NOTIYORDDT").equals("")) {
                    rform.setNotorddt(CommonFunctions.getFormattedOutputDate1(rs.getDate("NOTIYORDDT")));
                }
                rform.setRlvordno(rs.getString("RLMEMONO"));
                if (rs.getDate("RLMEMODT") != null && !rs.getDate("RLMEMODT").equals("")) {
                    rform.setRlvorddt(CommonFunctions.getFormattedOutputDate1(rs.getDate("RLMEMODT")));
                }
                if (rs.getDate("RLDATE") != null && !rs.getDate("RLDATE").equals("")) {
                    rform.setRlvondt(CommonFunctions.getFormattedOutputDate1(rs.getDate("RLDATE")));
                }
                rform.setRlvontime(rs.getString("RLTIME"));
                if (rs.getDate("RLDUEDATE") != null && !rs.getDate("RLDUEDATE").equals("")) {
                    rform.setRlvduedt(CommonFunctions.getFormattedOutputDate1(rs.getDate("RLDUEDATE")));
                }
                rform.setRlvduetime(rs.getString("RLDUETIME"));
                alist.add(rform);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs,pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return alist;
    }

    @Override
    public int getRelieveListCount(String empid) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        int count = 0;
        try {
            con = this.dataSource.getConnection();

            String sql = "select count(*) cnt from (select * from (select TEMP.* from (SELECT NOTI.NOT_TYPE AS NOTIFYTYPE, NOTI.NOT_ID AS NOTIFYID,NOTI.DOE AS NOTIYDOE,NOTI.ORDNO"
                    + " AS NOTIYORDNO,NOTI.ORDDT"
                    + " AS NOTIYORDDT,NOTI.DEPT_CODE AS NOTIYCODE,NOTI.OFF_CODE AS NOTIOFFCODE,NOTI.AUTH AS NOTIYAUTH ,RE.RELIEVE_ID AS RLID,RE.DOE AS"
                    + " RLDOE,RE.MEMO_NO AS RLMEMONO,RE.MEMO_DATE AS RLMEMODT,RE.RLV_DATE AS RLDATE,RE.RLV_TIME AS RLTIME,RE.SPC AS RLAUTH,RE.DUE_DOJ AS"
                    + " RLDUEDATE,RE.DUE_TOJ AS RLDUETIME,RE.IF_RLNQ AS RLNQ,RE.TR_DATA_TYPE TRTYPE,RE.SV_ID FROM (SELECT * FROM EMP_NOTIFICATION WHERE"
                    + " EMP_ID=?) NOTI"
                    + " LEFT OUTER JOIN (SELECT EMP_RELIEVE.RELIEVE_ID,EMP_RELIEVE.NOT_TYPE,EMP_RELIEVE.NOT_ID,EMP_RELIEVE.EMP_ID,EMP_RELIEVE.DOE,"
                    + " EMP_RELIEVE.MEMO_NO,EMP_RELIEVE.MEMO_DATE,EMP_RELIEVE.RLV_DATE,EMP_RELIEVE.RLV_TIME,EMP_RELIEVE.SPC,EMP_RELIEVE.DUE_DOJ,"
                    + " EMP_RELIEVE.DUE_TOJ, EMP_RELIEVE.NOTE, EMP_RELIEVE.JOIN_ID,EMP_RELIEVE.IF_RLNQ,EMP_RELIEVE.TR_DATA_TYPE,EMP_RELIEVE.SV_ID"
                    + " FROM (SELECT * FROM EMP_RELIEVE WHERE"
                    + " EMP_ID=? AND (JOIN_ID IS NULL OR (JOIN_ID NOT IN(SELECT JOIN_ID FROM EMP_JOIN WHERE EMP_ID=? AND IF_AD_CHARGE='Y'))))"
                    + " EMP_RELIEVE LEFT OUTER JOIN (SELECT * FROM EMP_JOIN WHERE EMP_ID=?) EMP_JOIN ON"
                    + " (EMP_RELIEVE.JOIN_ID=EMP_JOIN.JOIN_ID) AND"
                    + " if_ad_charge='N') RE ON NOTI.NOT_ID=RE.NOT_ID WHERE NOTI.EMP_ID=? and (NOTI.not_type ='REHABILITATION' or"
                    + " NOTI.not_type='ABSORPTION' or NOTI.not_type='REDEPLOYMENT' or NOTI.not_type='VALIDATION' or NOTI.not_type='LT_TRAINING' or"
                    + " NOTI.not_type='DEPUTATION' or NOTI.not_type ='TRANSFER' or NOTI.not_type ='PROMOTION' or NOTI.not_type ='REDESIGNATION' or"
                    + " NOTI.not_type ='RESIGNATION' or NOTI.not_type ='DECEASED' or NOTI.not_type ='RETIREMENT' or"
                    + " NOTI.not_type='ALLOT_CADRE' or (NOTI.not_type='LEAVE' AND NOTI.NOT_ID IN (SELECT NOT_ID FROM EMP_LEAVE WHERE EMP_ID=?"
                    + " AND IF_LONGTERM='Y'))) and (NOTI.not_id not in (select not_id from emp_cadre where emp_cadre.not_id=NOTI.not_id and"
                    + " joined_assuch='Y' and EMP_ID=?)) ORDER BY RLDATE DESC)TEMP)relievetemp)temp";
            pst = con.prepareStatement(sql);
            pst.setString(1,empid);
            pst.setString(2,empid);
            pst.setString(3,empid);
            pst.setString(4,empid);
            pst.setString(5,empid);
            pst.setString(6,empid);
            pst.setString(7,empid);
            rs = pst.executeQuery();
            if (rs.next()) {
                count = rs.getInt("cnt");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs,pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return count;
    }

    @Override
    public RelieveForm getRelieveData(String empid, String notid, String rlvid) {
        
        Connection con = null;
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        RelieveForm el = null;
        try {
            con = this.dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT EMP_NOTIFICATION.AUTH,EMP_NOTIFICATION.OFF_CODE,EMP_NOTIFICATION.DEPT_CODE,NOT_ID,NOT_TYPE,ORDNO,ORDDT,DEPARTMENT_NAME,OFF_EN,SPN FROM EMP_NOTIFICATION "
                    + "LEFT OUTER JOIN G_DEPARTMENT ON EMP_NOTIFICATION.DEPT_CODE =  G_DEPARTMENT.DEPARTMENT_CODE "
                    + "LEFT OUTER JOIN G_OFFICE ON EMP_NOTIFICATION.OFF_CODE =  G_OFFICE.OFF_CODE "
                    + "LEFT OUTER JOIN G_SPC ON EMP_NOTIFICATION.AUTH =  G_SPC.SPC WHERE NOT_ID=? AND EMP_ID=?");
            pstmt.setString(1, notid);
            pstmt.setString(2, empid);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                el = new RelieveForm();
                el.setEmpid(empid);
                el.setHidNotId(rs.getString("NOT_ID").toUpperCase());
                el.setHidNotType(rs.getString("NOT_TYPE").toUpperCase());
                el.setOrdNo(rs.getString("ORDNO"));
                if (rs.getDate("ORDDT") != null && !rs.getDate("ORDDT").equals("")) {
                    el.setOrdDt(CommonFunctions.getFormattedOutputDate1(rs.getDate("ORDDT")).toUpperCase());
                }
                if (rs.getString("DEPT_CODE") != null && !rs.getString("DEPT_CODE").trim().equals("")) {
                    el.setDeptname(rs.getString("DEPARTMENT_NAME"));
                }
                if (rs.getString("OFF_CODE") != null && !rs.getString("OFF_CODE").trim().equals("")) {
                    el.setOffname(rs.getString("OFF_EN"));
                }
                if (rs.getString("AUTH") != null && !rs.getString("AUTH").trim().equals("")) {
                    el.setPostname(rs.getString("SPN"));
                }
            }

            pstmt = con.prepareStatement("SELECT * FROM EMP_RELIEVE WHERE RELIEVE_ID=? AND EMP_ID=?");
            pstmt.setString(1, rlvid);
            pstmt.setString(2, empid);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                el.setRlvId(rlvid);
                el.setTxtRlvOrdNo(rs.getString("MEMO_NO"));
                if (rs.getDate("MEMO_DATE") != null && !rs.getDate("MEMO_DATE").equals("")) {
                    el.setTxtRlvOrdDt(CommonFunctions.getFormattedOutputDate1(rs.getDate("MEMO_DATE")).toUpperCase());
                }
                if (rs.getDate("RLV_DATE") != null && !rs.getDate("RLV_DATE").equals("")) {
                    el.setTxtRlvDt(CommonFunctions.getFormattedOutputDate1(rs.getDate("RLV_DATE")).toUpperCase());
                }
                el.setSltRlvTime(rs.getString("RLV_TIME"));
                el.setChkSBPrint(rs.getString("IF_VISIBLE"));
                if (rs.getDate("DUE_DOJ") != null && !rs.getDate("DUE_DOJ").equals("")) {
                    el.setTxtJoinDt(CommonFunctions.getFormattedOutputDate1(rs.getDate("DUE_DOJ")).toUpperCase());
                }
                el.setSltJoinTime(rs.getString("DUE_TOJ"));
                el.setRdRqRl(rs.getString("if_rlnq"));
                if (rs.getString("JOIN_ID") == null || rs.getString("JOIN_ID").equals("")) {
                    el.setSltRlvPost(rs.getString("SPC"));
                }
            }

            pstmt = con.prepareStatement("SELECT LAST_AQMONTH,LAST_AQYEAR FROM EMP_MAST WHERE EMP_ID=?");
            pstmt.setString(1, empid);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                el.setHidAqMonth(rs.getString("LAST_AQMONTH"));
                el.setHidAqYear(rs.getString("LAST_AQYEAR"));
            }
            //System.out.println("Hid AqYear is: " + el.getHidAqYear());
            //System.out.println("Hid AqMonth is: " + el.getHidAqMonth());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return el;
    }

    @Override
    public List getRelievedPostList(String empid) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        List li = new ArrayList();
        SelectOption so = null;
        try {
            con = this.dataSource.getConnection();

            String sql = "SELECT SPC,JOIN_ID FROM EMP_JOIN WHERE emp_id=? and if_ad_charge='N' and JOIN_ID NOT IN"
                    + " (select join_id from emp_relieve where emp_id=? and join_id is not null)"
                    + " UNION"
                    + " SELECT CUR_SPC,'NA' FROM AQ_MAST WHERE AQ_YEAR = (SELECT LAST_AQYEAR FROM EMP_MAST WHERE EMP_ID=?) AND"
                    + " AQ_MONTH = (SELECT LAST_AQMONTH FROM EMP_MAST WHERE EMP_ID=?) AND EMP_CODE=?";
            pst = con.prepareStatement(sql);
            pst.setString(1, empid);
            pst.setString(2, empid);
            pst.setString(3, empid);
            pst.setString(4, empid);
            pst.setString(5, empid);
            rs = pst.executeQuery();
            while (rs.next()) {
                so = new SelectOption();
                String spn = CommonFunctions.getSPN(con, rs.getString("SPC"));
                if (spn != null && !spn.equals("")) {
                    so.setLabel(spn);
                    if (rs.getString("JOIN_ID").equals("NA")) {
                        so.setValue("NA" + "-" + rs.getString("SPC"));
                    } else {
                        so.setValue(rs.getString("JOIN_ID"));
                    }
                }
                li.add(so);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return li;
    }

    @Override
    public void saveRelieve(RelieveForm relieve, String entDeptCode, String entOffCode, String entSpc) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        PreparedStatement pst1 = null;

        boolean updatePost = false;
        int retVal = 0;
        String spc = "";
        String joinid = "";
        try {
            con = this.dataSource.getConnection();

            //String next = CommonFunctions.getNextToe(relieve.getEmployeeid(), relieve.getDoe(), con);
            if (relieve.getSltRlvPost() != null && !relieve.getSltRlvPost().equals("")) {
                pst = con.prepareStatement("SELECT SPC FROM EMP_JOIN WHERE JOIN_ID=? and if_ad_charge='N'");
                pst.setString(1, relieve.getSltRlvPost());
                rs = pst.executeQuery();
                if (rs.next()) {
                    spc = rs.getString("SPC");
                }

                if (relieve.getSltRlvPost().substring(0, 3).equals("NA-")) {
                    joinid = "NA-";
                } else {
                    joinid = relieve.getSltRlvPost();
                }
            } else if (relieve.getSltRlvPost() == null || relieve.getSltRlvPost().equals("")) {
                if (relieve.getAuthSpc() != null && !relieve.getAuthSpc().equals("")) {
                    spc = relieve.getAuthSpc();
                }
            }
            /*if (relieve.getSltRlvMA() != null && relieve.getSltRlvMA().equals("AC")) {
             String[] spcsub = relieve.getSltAddlCharge().split("-");
             spc = spcsub[1];
             joinid = spcsub[0];
             }else if (relieve.getSltRlvPost() == null || relieve.getSltRlvPost().equals("")) {
             if (relieve.getAuthSpc() != null && !relieve.getAuthSpc().equals("")) {
             spc = relieve.getAuthSpc();
             }
             }*/

            if (relieve.getRlvId() != null && !relieve.getRlvId().equals("")) {
                pst = con.prepareStatement("UPDATE emp_relieve SET memo_no=?,memo_date=?,rlv_date=?,rlv_time=?,spc=?,due_doj=?,due_toj=?,note=?,join_id=?,if_rlnq=?,toe=?,if_assumed=?,if_visible=?,ENT_DEPT=?,ENT_OFF=?,ENT_AUTH=? WHERE relieve_id=? AND EMP_ID=?");
                pst.setString(1, relieve.getTxtRlvOrdNo());
                if (relieve.getTxtRlvOrdDt() != null && !relieve.getTxtRlvOrdDt().equals("")) {
                    pst.setTimestamp(2, new Timestamp(new SimpleDateFormat("dd-MMM-yyyy").parse(relieve.getTxtRlvOrdDt()).getTime()));
                } else {
                    pst.setTimestamp(2, null);
                }
                if (relieve.getTxtRlvDt() != null && !relieve.getTxtRlvDt().equals("")) {
                    pst.setTimestamp(3, new Timestamp(new SimpleDateFormat("dd-MMM-yyyy").parse(relieve.getTxtRlvDt()).getTime()));
                } else {
                    pst.setTimestamp(3, null);
                }
                pst.setString(4, relieve.getSltRlvTime());
                //pst.setString(5, relieve.getSpc1());
                pst.setString(5, spc);
                if (relieve.getTxtJoinDt() != null && !relieve.getTxtJoinDt().equals("")) {
                    pst.setTimestamp(6, new Timestamp(new SimpleDateFormat("dd-MMM-yyyy").parse(relieve.getTxtJoinDt()).getTime()));
                } else {
                    pst.setTimestamp(6, null);
                }
                pst.setString(7, relieve.getSltJoinTime());
                pst.setString(8, relieve.getNote());
                pst.setString(9, joinid);
                pst.setString(10, relieve.getRdRqRl());
                //pst.setString(11, next);
                pst.setString(11, "");
                pst.setString(12, "");
                pst.setString(13, relieve.getChkSBPrint());
                pst.setString(14, entDeptCode);
                pst.setString(15, entOffCode);
                pst.setString(16, entSpc);
                pst.setString(17,relieve.getRlvId());
                pst.setString(18,relieve.getEmpid());
                retVal = pst.executeUpdate();
            } else {
                String startTime = "";
                Calendar cal = Calendar.getInstance();
                DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");
                startTime = dateFormat.format(cal.getTime());

                pst = con.prepareStatement("insert into emp_relieve(RELIEVE_ID, NOT_TYPE, NOT_ID, EMP_ID, DOE, MEMO_NO, MEMO_DATE, RLV_DATE, RLV_TIME, SPC, DUE_DOJ, DUE_TOJ, NOTE, JOIN_ID, IF_RLNQ, TOE, IF_ASSUMED,IF_VISIBLE,ENT_DEPT,ENT_OFF,ENT_AUTH) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                pst.setString(1, maxRelieveIdDao.getMaxRelieveId());
                pst.setString(2, relieve.getHidNotType());
                pst.setString(3, relieve.getHidNotId());
                pst.setString(4, relieve.getEmpid());
                pst.setTimestamp(5, new Timestamp(new SimpleDateFormat("dd-MMM-yyyy").parse(startTime).getTime()));
                pst.setString(6, relieve.getTxtRlvOrdNo());
                if (relieve.getTxtRlvOrdDt() != null && !relieve.getTxtRlvOrdDt().equals("")) {
                    pst.setTimestamp(7, new Timestamp(new SimpleDateFormat("dd-MMM-yyyy").parse(relieve.getTxtRlvOrdDt()).getTime()));
                } else {
                    pst.setTimestamp(7, null);
                }
                if (relieve.getTxtRlvDt() != null && !relieve.getTxtRlvDt().equals("")) {
                    pst.setTimestamp(8, new Timestamp(new SimpleDateFormat("dd-MMM-yyyy").parse(relieve.getTxtRlvDt()).getTime()));
                } else {
                    pst.setTimestamp(8, null);
                }
                pst.setString(9, relieve.getSltRlvTime());
                pst.setString(10, spc);
                if (relieve.getTxtJoinDt() != null && !relieve.getTxtJoinDt().equals("")) {
                    pst.setTimestamp(11, new Timestamp(new SimpleDateFormat("dd-MMM-yyyy").parse(relieve.getTxtJoinDt()).getTime()));
                } else {
                    pst.setTimestamp(11, null);
                }
                pst.setString(12, relieve.getSltJoinTime());
                pst.setString(13, relieve.getNote());
                pst.setString(14, joinid);
                pst.setString(15, relieve.getRdRqRl());
                //pst.setString(16, next);
                pst.setString(16, "");
                pst.setString(17, "");
                pst.setString(18, relieve.getChkSBPrint());
                pst.setString(19, entDeptCode);
                pst.setString(20, entOffCode);
                pst.setString(21, entSpc);
                retVal = pst.executeUpdate();
            }

            //if (relieve.getSltRlvMA() != null && relieve.getSltRlvMA().equals("MC")) {
                if (retVal > 0) {
                    updatePost = CommonFunctions.isupdatePayOrPostingInfo(relieve.getEmpid(), relieve.getTxtRlvDt(), relieve.getTxtRlvOrdDt(), "POSTING", con);
                }
                if (updatePost == true) {
                    pst = con.prepareStatement("UPDATE EMP_MAST SET CUR_SPC=?, POST_ORDER_DATE=?, CURR_POST_DOJ=? WHERE EMP_ID=?");
                    pst.setString(1, null);
                    if(relieve.getTxtRlvDt() != null && !relieve.getTxtRlvDt().equals("")){
                        pst.setTimestamp(2, new Timestamp(new SimpleDateFormat("dd-MMM-yyyy").parse(relieve.getTxtRlvDt()).getTime()));
                    }else{
                        pst.setTimestamp(2, null);
                    }
                    if(relieve.getTxtRlvOrdDt() != null && !relieve.getTxtRlvOrdDt().equals("")){
                        pst.setTimestamp(3, new Timestamp(new SimpleDateFormat("dd-MMM-yyyy").parse(relieve.getTxtRlvOrdDt()).getTime()));
                    }else{
                        pst.setTimestamp(3, null);
                    }
                    pst.setString(4, relieve.getEmpid());
                    pst.executeUpdate();

                    if (relieve.getHidNotType() != null && relieve.getHidNotType().equals("LEAVE")) {
                        CommonFunctions.modifyEmpCurStatus(relieve.getEmpid(), "ON LEAVE", con);
                    } else if (relieve.getHidNotType() != null && relieve.getHidNotType().equals("RETIREMENT")) {
                        CommonFunctions.modifyEmpCurStatus(relieve.getEmpid(), "SUPERANNUATED", con);
                    } else {
                        CommonFunctions.modifyEmpCurStatus(relieve.getEmpid(), "ON TRANSIT", con);
                    }
                }

                pst = con.prepareStatement("select OFF_CODE from emp_transfer where not_id=? AND EMP_ID=?");
                pst.setString(1, relieve.getHidNotId());
                pst.setString(2, relieve.getEmpid());
                rs = pst.executeQuery();
                if (rs.next()) {
                    String off_code = rs.getString("OFF_CODE");
                    if (off_code != null && !off_code.equals("")) {
                        pst1 = con.prepareStatement("UPDATE EMP_MAST SET NEXT_OFFICE_CODE=? WHERE EMP_ID=?");
                        pst1.setString(1, off_code);
                        pst1.setString(2, relieve.getEmpid());
                        pst1.executeUpdate();
                    }
                }
            //}
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(pst1);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    @Override
    public void deleteRelieve(String empid, RelieveForm erelieve) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        String joiningId = "";
        String joining_offCode = "";
        String joining_spc = "";

        try {
            con = this.dataSource.getConnection();

            pst = con.prepareStatement("SELECT OFF_CODE,EMP_JOIN.SPC,EMP_RELIEVE.JOIN_ID FROM (SELECT JOIN_ID FROM EMP_RELIEVE WHERE RELIEVE_ID=?)EMP_RELIEVE"
                    + " INNER JOIN EMP_JOIN ON EMP_RELIEVE.JOIN_ID=EMP_JOIN.JOIN_ID"
                    + " INNER JOIN G_SPC ON EMP_JOIN.SPC=G_SPC.SPC");
            pst.setString(1, erelieve.getRlvId());
            rs = pst.executeQuery();
            if (rs.next()) {
                joiningId = rs.getString("JOIN_ID");
                joining_offCode = rs.getString("OFF_CODE");
                joining_spc = rs.getString("SPC");
            }

            if (joiningId != null && !joiningId.equals("")) {
                pst = con.prepareStatement("UPDATE EMP_MAST SET CUR_OFF_CODE=?,CUR_SPC=? WHERE EMP_ID=?");
                pst.setString(1, joining_offCode);
                pst.setString(2, joining_spc);
                pst.setString(3, empid);
                pst.executeUpdate();
            }

            pst = con.prepareStatement("DELETE FROM EMP_RELIEVE WHERE relieve_id=?");
            pst.setString(1, erelieve.getRlvId());
            pst.executeUpdate();

            CommonFunctions.modifyEmpCurStatus(erelieve.getEmpid(), "ON DUTY", con);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs,pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    @Override
    public List getAddlChargeList(String empid) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        List li = new ArrayList();
        SelectOption so = null;
        try {
            con = this.dataSource.getConnection();

            //String sql = "SELECT SPC,JOIN_ID FROM EMP_JOIN WHERE emp_id=? and if_ad_charge='Y' and JOIN_ID NOT IN (select join_id from emp_relieve where emp_id=? and join_id is not null)";
            String sql = "SELECT EMP_JOIN.SPC,SPN,JOIN_ID FROM (SELECT SPC,JOIN_ID FROM EMP_JOIN WHERE emp_id=? and if_ad_charge='Y' and JOIN_ID NOT IN"
                    + " (select join_id from emp_relieve where emp_id=? and join_id is not null))emp_join"
                    + " left outer join g_spc on emp_join.spc=g_spc.spn";
            pst = con.prepareStatement(sql);
            pst.setString(1, empid);
            pst.setString(2, empid);
            rs = pst.executeQuery();
            while (rs.next()) {
                so = new SelectOption();
                so.setValue(rs.getString("JOIN_ID") + "-" + rs.getString("SPC"));
                so.setLabel(rs.getString("SPN"));
                li.add(so);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return li;
    }
}
