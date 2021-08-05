package hrms.dao.incrementsanction;

import hrms.SelectOption;
import hrms.common.CommonFunctions;
import hrms.common.DataBaseFunctions;
import hrms.common.Message;
import hrms.dao.empinfo.EmployeeInformationDAO;
import hrms.dao.emppayrecord.EmpPayRecordDAO;
import hrms.dao.notification.NotificationDAO;
import hrms.model.empinfo.EmployeeInformation;
import hrms.model.emppayrecord.EmpPayRecordForm;
import hrms.model.incrementsanction.IncrementBean;
import hrms.model.incrementsanction.IncrementForm;
import hrms.model.notification.NotificationBean;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.apache.commons.lang.StringUtils;

public class IncrementSanctionDAOImpl implements IncrementSanctionDAO {

    @Resource(name = "dataSource")
    protected DataSource dataSource;

    protected MaxIncrementIdDAO maxIncrementIdDao;

    public NotificationDAO notificationDao;

    protected EmpPayRecordDAO emppayrecordDAO;

    protected EmployeeInformationDAO empinfoDAO;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setMaxIncrementIdDao(MaxIncrementIdDAO maxIncrementIdDao) {
        this.maxIncrementIdDao = maxIncrementIdDao;
    }

    public void setEmppayrecordDAO(EmpPayRecordDAO emppayrecordDAO) {
        this.emppayrecordDAO = emppayrecordDAO;
    }

    public void setNotificationDao(NotificationDAO notificationDao) {
        this.notificationDao = notificationDao;
    }

    public void setEmpinfoDAO(EmployeeInformationDAO empinfoDAO) {
        this.empinfoDAO = empinfoDAO;
    }
    
    @Override
    public Message saveIncrement(IncrementForm incfb, String entdept, String entoff, String entauth) {

        Connection con = null;

        PreparedStatement pst = null;

        NotificationBean nb = new NotificationBean();
        EmpPayRecordForm epayrecordform = new EmpPayRecordForm();
        EmployeeInformation ei = new EmployeeInformation();
        
        Message msg = new Message();
        msg.setStatus("Success");

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            con = dataSource.getConnection();

            if (incfb.getEmpid() != null && !incfb.getEmpid().equals("")) {

                nb.setNottype("INCREMENT");
                nb.setEmpId(incfb.getEmpid());
                nb.setDateofEntry(new Date());
                nb.setOrdno(incfb.getTxtSanctionOrderNo());
                nb.setOrdDate(sdf.parse(incfb.getTxtSanctionOrderDt()));
                nb.setSancDeptCode(incfb.getDeptCode());
                nb.setSancOffCode(incfb.getHidOffCode());
                nb.setSancAuthCode(incfb.getHidSpc());
                nb.setEntryDeptCode(entdept);
                nb.setEntryOffCode(entoff);
                nb.setEntryAuthCode(entauth);
                nb.setNote(incfb.getTxtIncrNote());
                String notid = notificationDao.insertNotificationData(nb);

                //pst = con.prepareStatement("UPDATE EMP_MAST SET ST_DATE_OF_CUR_SALARY=DATE_OF_NINCR,DATE_OF_NINCR=(SELECT ADD_MONTHS(EMP_MAST.DATE_OF_NINCR,12) FROM EMP_MAST WHERE EMP_ID='" + incfb.getEmpid() + "') WHERE EMP_ID=?");
                pst = con.prepareStatement("UPDATE EMP_MAST SET ST_DATE_OF_CUR_SALARY=DATE_OF_NINCR,DATE_OF_NINCR=(SELECT DATE_OF_NINCR + CAST('12 MONTHS' AS INTERVAL) FROM EMP_MAST WHERE EMP_ID=?) WHERE EMP_ID=?");
                pst.setString(1, incfb.getEmpid());
                pst.setString(2, incfb.getEmpid());
                pst.executeUpdate();

                pst = con.prepareStatement("insert into emp_incr (incrid, prid, not_id, not_type, emp_id, incr, first_incr, second_incr, third_incr, fourth_incr, incr_type) values(?,?,?,?,?,?,?,?,?,?,?)");
                pst.setString(1, maxIncrementIdDao.getMaxIncrementId());
                pst.setString(2, null);
                pst.setString(3, notid);
                pst.setString(4, "INCREMENT");
                pst.setString(5, incfb.getEmpid());
                if (incfb.getTxtIncrAmt() != null && !incfb.getTxtIncrAmt().equalsIgnoreCase("")) {
                    pst.setDouble(6, Double.parseDouble(incfb.getTxtIncrAmt()));
                } else {
                    pst.setDouble(6, 0);
                }
                if (incfb.getIncrementLvl() != null && incfb.getIncrementLvl().equals("1")) {
                    pst.setString(7, incfb.getIncrementLvl());
                } else {
                    pst.setString(7, "");
                }
                if (incfb.getIncrementLvl()!= null && incfb.getIncrementLvl().equals("2")) {
                    pst.setString(8, incfb.getIncrementLvl());
                } else {
                    pst.setString(8, "");
                }
                if (incfb.getIncrementLvl() != null && incfb.getIncrementLvl().equals("3")) {
                    pst.setString(9, incfb.getIncrementLvl());
                } else {
                    pst.setString(9, "");
                }
                if (incfb.getIncrementLvl() != null && incfb.getIncrementLvl().equals("4")) {
                    pst.setString(10, incfb.getIncrementLvl());
                } else {
                    pst.setString(10, "");
                }
                if (incfb.getIncrementType() != null && !incfb.getIncrementType().equals("")) {
                    pst.setString(11, incfb.getIncrementType());
                } else {
                    pst.setString(11, "");
                }
                pst.executeUpdate();

                epayrecordform.setEmpid(incfb.getEmpid());
                epayrecordform.setNot_id(notid);
                epayrecordform.setNot_type("INCREMENT");
                epayrecordform.setPayscale(incfb.getSltPayScale());
                epayrecordform.setBasic(incfb.getTxtNewBasic());
                epayrecordform.setGp(incfb.getTxtGradePay());
                epayrecordform.setS_pay(incfb.getTxtSPay());
                epayrecordform.setP_pay(incfb.getTxtP_pay());
                epayrecordform.setOth_pay(incfb.getTxtOthPay());
                epayrecordform.setOth_desc(incfb.getTxtDescOth());
                epayrecordform.setWefDt(incfb.getTxtWEFDt());
                epayrecordform.setWefTime(incfb.getTxtWEFTime());
                emppayrecordDAO.saveEmpPayRecordData(epayrecordform);

                String serverDate = sdf.format(new Date());
                if (serverDate != null && !serverDate.trim().equals("")) {
                    boolean updatepay = empinfoDAO.isupdatePayOrPostingInfo(incfb.getEmpid(), incfb.getTxtWEFDt(), incfb.getTxtSanctionOrderDt(), "PAY");
                    if (updatepay == true) {
                        ei.setPaydate(incfb.getTxtWEFDt());
                        ei.setPayscale(incfb.getSltPayScale());
                        
                        if(incfb.getTxtNewBasic()!=null && !incfb.getTxtNewBasic().equals("")){
                            ei.setBasic(Double.parseDouble(incfb.getTxtNewBasic()));
                        }
                        
                        if(incfb.getTxtGradePay()!=null && !incfb.getTxtGradePay().equals("")){
                            ei.setGp(Double.parseDouble(incfb.getTxtGradePay()));
                        }
                        
                        if(incfb.getTxtSPay()!=null && !incfb.getTxtSPay().equals("")){
                            ei.setSpay(Integer.parseInt(incfb.getTxtSPay()));
                        }
                        
                        if(incfb.getTxtP_pay()!=null && !incfb.getTxtP_pay().equals("")){
                            ei.setPpay(Integer.parseInt(incfb.getTxtP_pay()));
                        }
                        
                        if(incfb.getTxtOthPay()!=null && !incfb.getTxtOthPay().equals("")){
                            ei.setOthpay(Integer.parseInt(incfb.getTxtOthPay()));
                        }
                        
                        
                        empinfoDAO.updateEmpPayInfoOnDate(ei, incfb.getEmpid());
                    }
                }
            }
        } catch (Exception e) {
            msg.setStatus("Error");
            msg.setMessage(e.getMessage());
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return msg;
    }

    @Override
    public List getIncrementList(String empId, int minlimit, int maxlimit) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        List incrementlist = new ArrayList();
        IncrementBean inc = null;
        try {
            con = dataSource.getConnection();

            String sql = "SELECT * FROM (SELECT incr.* FROM (SELECT SV_ID,CANCEL_TYPE,LINK_ID,T.NOT_ID,INCRID,INCR,"
                    + " FIRST_INCR,SECOND_INCR,THIRD_INCR,FOURTH_INCR,INCR_TYPE,DOE, ORDNO,ORDDT,AUTH,OFF_CODE,DEPT_CODE,WEF,WEFT,"
                    + " pay_scale,s_pay, p_pay,oth_pay,oth_desc,pay,gp FROM (SELECT E.*,F.PRID,F.INCRID,F.INCR,F.FIRST_INCR,"
                    + " F.second_incr, F.third_incr,F.fourth_incr,F.incr_type FROM"
                    + " (SELECT * FROM EMP_INCR WHERE NOT_TYPE='INCREMENT' AND EMP_ID=? AND PRID IS NULL) F  LEFT OUTER JOIN"
                    + " (select N1.*,N2.NOT_TYPE CANCEL_TYPE FROM  ("
                    + " SELECT SV_ID,NOT_ID, NOT_TYPE, EMP_ID, DOE, TOE, IF_ASSUMED, ORDNO, ORDDT, DEPT_CODE, OFF_CODE, AUTH, ent_dept,"
                    + " ent_off, ent_auth, NOTE, ISCANCELED, LINK_ID  FROM EMP_NOTIFICATION WHERE EMP_ID=?"
                    + " and NOT_TYPE='INCREMENT') N1 LEFT OUTER JOIN (SELECT SV_ID,NOT_ID, NOT_TYPE, EMP_ID, DOE, TOE, IF_ASSUMED,"
                    + " ORDNO, ORDDT, DEPT_CODE, OFF_CODE, AUTH, ent_dept, ent_off, ent_auth, NOTE, ISCANCELED, LINK_ID FROM"
                    + " EMP_NOTIFICATION WHERE EMP_ID=?)  N2  ON  N1.LINK_ID=N2.NOT_ID)  E"
                    + " ON E.NOT_ID=F.NOT_ID ) T LEFT OUTER JOIN (SELECT * FROM EMP_PAY_RECORD WHERE NOT_TYPE='INCREMENT'"
                    + " AND EMP_ID=?)  R ON T.NOT_ID=R.NOT_ID  ORDER BY DOE DESC) incr)incrtemp LIMIT ? OFFSET ?";
            pst = con.prepareStatement(sql);
            pst.setString(1, empId);
            pst.setString(2, empId);
            pst.setString(3, empId);
            pst.setString(4, empId);
            pst.setInt(5, maxlimit);
            pst.setInt(6, minlimit);
            rs = pst.executeQuery();
            while (rs.next()) {
                inc = new IncrementBean();
                
                inc.setHidIncrId(StringUtils.defaultString(rs.getString("INCRID")));
                inc.setHnotid(StringUtils.defaultString(rs.getString("not_id")));
                if (rs.getDate("DOE") != null && !rs.getDate("DOE").equals("")) {
                    inc.setDoe(CommonFunctions.getFormattedOutputDate1(rs.getDate("DOE")));
                } else {
                    inc.setDoe("");
                }
                if (rs.getDate("WEF") != null && !rs.getDate("WEF").equals("")) {
                    inc.setEffDate(CommonFunctions.getFormattedOutputDate1(rs.getDate("WEF")));
                } else {
                    inc.setEffDate("");
                }
                inc.setEffTime(StringUtils.defaultString(rs.getString("WEFT")));
                inc.setIncrAmt(StringUtils.defaultString(rs.getString("INCR")));
                inc.setNewBasic(StringUtils.defaultString(rs.getString("pay")));
                inc.setGradePay(StringUtils.defaultString(rs.getString("gp")));
                incrementlist.add(inc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return incrementlist;
    }

    @Override
    public Message updateIncrement(IncrementForm incfb, String entdept, String entoff, String entauth) throws Exception {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        boolean dateincr = false;

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");

        Message msg = new Message();
        msg.setStatus("Success");

        NotificationBean nb = new NotificationBean();
        EmpPayRecordForm epayrecordform = new EmpPayRecordForm();
        EmployeeInformation ei = new EmployeeInformation();
        try {
            con = dataSource.getConnection();

            if (incfb.getEmpid() != null && !incfb.getEmpid().equals("")) {

                nb.setNottype("INCREMENT");
                nb.setEmpId(incfb.getEmpid());
                nb.setOrdno(incfb.getTxtSanctionOrderNo());
                nb.setOrdDate(sdf.parse(incfb.getTxtSanctionOrderDt()));
                nb.setSancDeptCode(incfb.getDeptCode());
                nb.setSancOffCode(incfb.getHidOffCode());
                nb.setSancAuthCode(incfb.getHidSpc());
                nb.setEntryDeptCode(entdept);
                nb.setEntryOffCode(entoff);
                nb.setEntryAuthCode(entauth);
                nb.setNote(incfb.getTxtIncrNote());
                notificationDao.modifyNotificationData(nb);

                dateincr = getCmpDate(con, "DATE_OF_NINCR", incfb.getEmpid());
                //pst = con.prepareStatement("UPDATE EMP_MAST SET DATE_OF_NINCR=(SELECT ADD_MONTHS(EMP_MAST.DATE_OF_NINCR,12) FROM EMP_MAST WHERE EMP_ID='" + incfb.getEmpid() + "') WHERE EMP_ID=?");
                pst = con.prepareStatement("UPDATE EMP_MAST SET ST_DATE_OF_CUR_SALARY=DATE_OF_NINCR,DATE_OF_NINCR=(SELECT DATE_OF_NINCR + INTERVAL '12' MONTH FROM EMP_MAST WHERE EMP_ID=?) WHERE EMP_ID=?");
                pst.setString(1, incfb.getEmpid());
                pst.setString(2, incfb.getEmpid());
                if (dateincr == true) {
                    pst.executeUpdate();
                }

                pst = con.prepareStatement("update emp_incr set prid=?,not_id=?,not_type=?,incr=?,first_incr=?,second_incr=?,third_incr=?,fourth_incr=?,incr_type=? where emp_id=? and incrid=?");
                pst.setString(1, null);
                pst.setString(2, incfb.getHnotid());
                pst.setString(3, "INCREMENT");
                if (incfb.getTxtIncrAmt() != null && !incfb.getTxtIncrAmt().equalsIgnoreCase("")) {
                    pst.setDouble(4, java.lang.Double.parseDouble(incfb.getTxtIncrAmt()));
                } else {
                    pst.setDouble(4, 0);
                }
                if (incfb.getIncrementLvl() != null && incfb.getIncrementLvl().equals("1")) {
                    pst.setString(5, incfb.getIncrementLvl());
                } else {
                    pst.setString(5, "N");
                }
                if (incfb.getIncrementLvl() != null && incfb.getIncrementLvl().equals("2")) {
                    pst.setString(6, incfb.getIncrementLvl());
                } else {
                    pst.setString(6, "N");
                }
                if (incfb.getIncrementLvl() != null && incfb.getIncrementLvl().equals("3")) {
                    pst.setString(7, incfb.getIncrementLvl());
                } else {
                    pst.setString(7, "N");
                }
                if (incfb.getIncrementLvl() != null && incfb.getIncrementLvl().equals("4")) {
                    pst.setString(8, incfb.getIncrementLvl());
                } else {
                    pst.setString(8, "N");
                }
                if (incfb.getIncrementType() != null && !incfb.getIncrementType().equals("")) {
                    pst.setString(9, incfb.getIncrementType());
                } else {
                    pst.setString(9, "N");
                }
                pst.setString(10, incfb.getEmpid());
                pst.setString(11, incfb.getHidIncrId());
                pst.executeUpdate();

                epayrecordform.setEmpid(incfb.getEmpid());
                epayrecordform.setNot_id(incfb.getHnotid());
                epayrecordform.setNot_type("INCREMENT");
                epayrecordform.setPayid(incfb.getHidPayId());
                epayrecordform.setPayscale(incfb.getSltPayScale());
                epayrecordform.setBasic(incfb.getTxtNewBasic());
                epayrecordform.setGp(incfb.getTxtGradePay());
                epayrecordform.setS_pay(incfb.getTxtSPay());
                epayrecordform.setP_pay(incfb.getTxtP_pay());
                epayrecordform.setOth_pay(incfb.getTxtOthPay());
                epayrecordform.setOth_desc(incfb.getTxtDescOth());
                epayrecordform.setWefDt(incfb.getTxtWEFDt());
                epayrecordform.setWefTime(incfb.getTxtWEFTime());
                emppayrecordDAO.updateEmpPayRecordData(epayrecordform);

                String serverDate = sdf.format(new Date());
                if (serverDate != null && !serverDate.trim().equals("")) {
                    boolean updatepay = empinfoDAO.isupdatePayOrPostingInfo(incfb.getEmpid(), incfb.getTxtWEFDt(), incfb.getTxtSanctionOrderDt(), "PAY");
                    if (updatepay == true) {
                        ei.setPaydate(incfb.getTxtWEFDt());
                        ei.setPayscale(incfb.getSltPayScale());
                        ei.setBasic(Double.parseDouble(incfb.getTxtNewBasic()));
                        ei.setGp(Double.parseDouble(incfb.getTxtGradePay()));
                        ei.setSpay(Integer.parseInt(incfb.getTxtSPay()));
                        ei.setPpay(Integer.parseInt(incfb.getTxtP_pay()));
                        ei.setOthpay(Integer.parseInt(incfb.getTxtOthPay()));
                        empinfoDAO.updateEmpPayInfoOnDate(ei, incfb.getEmpid());
                    }
                }
            }
        } catch (Exception e) {
            msg.setStatus("Error");
            msg.setMessage(e.getMessage());
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return msg;
    }

    private boolean getCmpDate(Connection con, String colName, String empid) throws Exception {

        Statement stmt = null;
        ResultSet rs = null;
        int curyear = 0;
        String curyear1 = "";
        boolean flag = false;
        try {
            Calendar cal = Calendar.getInstance();
            curyear = cal.get(Calendar.YEAR);
            curyear1 = curyear + "";
            stmt = con.createStatement();
            String sql = "SELECT " + colName + " FROM EMP_MAST WHERE EMP_ID='" + empid + "'";
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                if (rs.getString("DATE_OF_NINCR") != null && !rs.getString("DATE_OF_NINCR").equals("")) {
                    if (Integer.parseInt(CommonFunctions.getFormattedOutputDate1(rs.getDate("DATE_OF_NINCR")).substring(7)) > Integer.parseInt(curyear1)) {
                        flag = false;
                    } else if (Integer.parseInt(CommonFunctions.getFormattedOutputDate1(rs.getDate("DATE_OF_NINCR")).substring(7)) == Integer.parseInt(curyear1)) {
                        flag = true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, stmt);
        }
        return flag;
    }

    @Override
    public IncrementForm getEmpIncRowData(String empId, String incid) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        IncrementForm incfb = new IncrementForm();
        String incrLvl = "";
        try {
            con = dataSource.getConnection();

            String sql = "SELECT * FROM (SELECT E.*,F.INCRID,F.PRID,F.INCR,F.FIRST_INCR,F.second_incr,F.third_incr,F.fourth_incr,"
                    + " F.incr_type,G_SPC.SPN FROM EMP_NOTIFICATION E INNER JOIN EMP_INCR F ON E.NOT_ID=F.NOT_ID LEFT OUTER JOIN G_SPC ON E.AUTH=G_SPC.SPC"
                    + " WHERE E.NOT_TYPE='INCREMENT'"
                    + " AND E.EMP_ID=? AND F.INCRID=? AND F.PRID IS NULL) T LEFT OUTER JOIN ("
                    + " SELECT * FROM EMP_PAY_RECORD WHERE NOT_TYPE='INCREMENT' AND EMP_ID=?) R ON T.NOT_ID=R.NOT_ID";
            pst = con.prepareStatement(sql);
            pst.setString(1, empId);
            pst.setString(2, incid);
            pst.setString(3, empId);
            rs = pst.executeQuery();
            if (rs.next()) {

                incfb.setHidIncrId(incid);
                incfb.setHnotid(rs.getString("not_id"));
                incfb.setHidPayId(rs.getString("pay_id"));

                incfb.setTxtSanctionOrderNo(rs.getString("ordno"));
                if (rs.getDate("orddt") != null && !rs.getDate("orddt").equals("")) {
                    incfb.setTxtSanctionOrderDt(CommonFunctions.getFormattedOutputDate1(rs.getDate("orddt")));
                }
                if (rs.getString("dept_code") != null && !rs.getString("dept_code").equals("")) {
                    incfb.setDeptCode(rs.getString("dept_code"));
                }
                if (rs.getString("off_code") != null && !rs.getString("off_code").equals("")) {
                    incfb.setHidOffCode(rs.getString("off_code"));
                }
                if (rs.getString("auth") != null && !rs.getString("auth").equals("")) {
                    incfb.setHidSpc(rs.getString("auth"));
                }
                if (rs.getString("spn") != null && !rs.getString("spn").equals("")) {
                    incfb.setSancAuthPostName(rs.getString("spn"));
                }
                if (rs.getDate("wef") != null && !rs.getDate("wef").equals("")) {
                    incfb.setTxtWEFDt(CommonFunctions.getFormattedOutputDate1(rs.getDate("wef")));
                }
                incfb.setTxtWEFTime(rs.getString("weft"));
                incfb.setSltPayScale(rs.getString("pay_scale"));
                incfb.setTxtIncrAmt(rs.getString("incr"));
                incfb.setTxtP_pay(rs.getString("p_pay"));
                incfb.setTxtOthPay(rs.getString("oth_pay"));
                incfb.setTxtSPay(rs.getString("s_pay"));
                incfb.setTxtNewBasic(rs.getString("pay"));
                incfb.setTxtDescOth(rs.getString("oth_desc"));
                incfb.setTxtGradePay(rs.getString("gp"));
                if(rs.getString("first_incr") != null && rs.getString("first_incr").equals("1")){
                    incrLvl = rs.getString("first_incr");
                }else if(rs.getString("second_incr") != null && rs.getString("second_incr").equals("2")){
                    incrLvl = rs.getString("second_incr");
                }else if(rs.getString("third_incr") != null && rs.getString("third_incr").equals("3")){
                    incrLvl = rs.getString("third_incr");
                }else if(rs.getString("fourth_incr") != null && rs.getString("fourth_incr").equals("4")){
                    incrLvl = rs.getString("fourth_incr");
                }
                incfb.setIncrementLvl(incrLvl);
                incfb.setIncrementType(rs.getString("incr_type"));
                incfb.setTxtIncrNote(rs.getString("note"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return incfb;
    }

    @Override
    public int getIncrementListCount(String empId) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        int totalcnt = 0;
        try {
            con = dataSource.getConnection();

            String sql = "SELECT count(*) cnt FROM (SELECT incr.* FROM(SELECT SV_ID,CANCEL_TYPE,LINK_ID,T.NOT_ID,INCRID,INCR,"
                    + " FIRST_INCR,SECOND_INCR,THIRD_INCR,FOURTH_INCR,INCR_TYPE,DOE, ORDNO,ORDDT,AUTH,OFF_CODE,DEPT_CODE,WEF,WEFT,"
                    + " pay_scale,s_pay, p_pay,oth_pay,oth_desc,pay,gp FROM (SELECT E.*,F.PRID,F.INCRID,F.INCR,F.FIRST_INCR,"
                    + " F.second_incr, F.third_incr,F.fourth_incr,F.incr_type FROM"
                    + " (SELECT * FROM EMP_INCR WHERE NOT_TYPE='INCREMENT' AND EMP_ID=? AND PRID IS NULL) F  LEFT OUTER JOIN"
                    + " (select N1.*,N2.NOT_TYPE CANCEL_TYPE FROM  ("
                    + " SELECT SV_ID,NOT_ID, NOT_TYPE, EMP_ID, DOE, TOE, IF_ASSUMED, ORDNO, ORDDT, DEPT_CODE, OFF_CODE, AUTH, ent_dept,"
                    + " ent_off, ent_auth, NOTE, ISCANCELED, LINK_ID  FROM EMP_NOTIFICATION WHERE EMP_ID=?"
                    + " and NOT_TYPE='INCREMENT') N1 LEFT OUTER JOIN (SELECT SV_ID,NOT_ID, NOT_TYPE, EMP_ID, DOE, TOE, IF_ASSUMED,"
                    + " ORDNO, ORDDT, DEPT_CODE, OFF_CODE, AUTH, ent_dept, ent_off, ent_auth, NOTE, ISCANCELED, LINK_ID FROM"
                    + " EMP_NOTIFICATION WHERE EMP_ID=?)  N2  ON  N1.LINK_ID=N2.NOT_ID)  E"
                    + " ON E.NOT_ID=F.NOT_ID ) T LEFT OUTER JOIN (SELECT * FROM EMP_PAY_RECORD WHERE NOT_TYPE='INCREMENT'"
                    + " AND EMP_ID=?)  R ON T.NOT_ID=R.NOT_ID  ORDER BY DOE DESC) incr)incrtemp";
            pst = con.prepareStatement(sql);
            pst.setString(1, empId);
            pst.setString(2, empId);
            pst.setString(3, empId);
            pst.setString(4, empId);
            rs = pst.executeQuery();
            if (rs.next()) {
                totalcnt = rs.getInt("cnt");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return totalcnt;
    }

    @Override
    public boolean deleteIncrement(IncrementForm incrementForm) {

        Connection con = null;
        boolean deleted=false;
        Statement stmt = null;
        ResultSet rs = null;

        PreparedStatement pst = null;

        String linkid = null;
        int status = 0;

        Format sdf = new SimpleDateFormat("dd-MMM-yyyy");

        Message msg = new Message();
        msg.setStatus("Success");
        try {
            con = dataSource.getConnection();

            pst = con.prepareStatement("delete from emp_incr where incrid=? and emp_id=? and not_id=?");

            stmt = con.createStatement();
            String sql = "SELECT LINK_ID FROM EMP_NOTIFICATION WHERE EMP_ID='" + incrementForm.getEmpid() + "' AND NOT_ID='" + incrementForm.getHnotid() + "'";
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                linkid = rs.getString("LINK_ID");
            }

            if (linkid == null) {
                if (incrementForm.getEmpid() != null && incrementForm.getHidIncrId() != null) {
                    rs = stmt.executeQuery("SELECT NOT_ID FROM EMP_NOTIFICATION WHERE LINK_ID='" + incrementForm.getHnotid() + "'");
                    if (rs.next()) {
                        linkid = rs.getString("NOT_ID");
                    }
                    if (linkid != null && !linkid.equals("")) {
                        deleteSuperInc(incrementForm.getEmpid(), linkid, con);
                    }
                    pst.setString(1, incrementForm.getHidIncrId());
                    pst.setString(2, incrementForm.getEmpid());
                    pst.setString(3, incrementForm.getHnotid());
                    status = pst.executeUpdate();
                }
                DataBaseFunctions.closeSqlObjects(pst);
                pst = con.prepareStatement("delete from emp_pay_record where not_id='" + incrementForm.getHnotid() + "' and emp_id='" + incrementForm.getEmpid() + "' and not_type='INCREMENT'");
                status = pst.executeUpdate();

                DataBaseFunctions.closeSqlObjects(pst);
                pst = con.prepareStatement("delete from emp_notification where not_id='" + incrementForm.getHnotid() + "' and emp_id='" + incrementForm.getEmpid() + "' and not_type='INCREMENT'");
                status = pst.executeUpdate();

                //ChronologySerial.deletefromSBOutput(con, incrementForm.getEmpid(), "INCREMENT", incrementForm.getHnotid());
                //CommonFunctions.deleteOthSpc(incrementForm.getTxtothPostName(), con);
                String serverDate = sdf.format(new Date());

                if (serverDate != null && !serverDate.trim().equals("")) {
                    //StatusAsOn.updateEmpPayInfoOnDate(incrementForm.getEmpid(), serverDate, con);
                }
                deleted=true;
            }
        } catch (Exception e) {
            msg.setStatus("Error");
            msg.setMessage(e.getMessage());
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return deleted;
    }

    public boolean deleteSuperInc(String empid, String notid, Connection con) throws Exception {

        PreparedStatement pst = null;

        boolean delete = false;
        int conf = 0;

        try {

            if (notid != null || !notid.equals("")) {
                pst = con.prepareStatement("update emp_notification set ISCANCELED='N',LINK_ID=NULL where not_id='" + notid + "' and emp_id='" + empid + "' and not_type='INCREMENT'");
                conf = pst.executeUpdate();
            }

            if (conf > 0) {
                delete = true;
            } else {
                delete = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
        }
        return delete;
    }
}
