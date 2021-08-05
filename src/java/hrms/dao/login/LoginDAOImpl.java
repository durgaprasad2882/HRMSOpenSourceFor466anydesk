/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.login;

import hrms.common.CommonFunctions;
import hrms.common.DataBaseFunctions;
import hrms.common.SMSThread;
import hrms.model.lamembers.LAMembers;
import hrms.model.login.AdminUsers;
import hrms.model.login.UserDetails;
import hrms.model.login.UserExpertise;
import hrms.model.login.Users;
import hrms.model.master.Module;
import java.sql.Connection;
import java.sql.DriverManager;
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
import java.util.Map;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.IntegerType;

/**
 *
 * @author Surendra
 */
public class LoginDAOImpl implements LoginDAO {

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

    @SuppressWarnings("unchecked")
    public Users findByUserName(String username) {
        Session session = this.sessionFactory.openSession();
        List<Users> users = new ArrayList<Users>();

        users = session
                .createQuery("from Login where username=?")
                .setParameter(0, username)
                .list();

        if (users.size() > 0) {
            return users.get(0);
        } else {
            return null;
        }

    }

    @Override
    public UserDetails checkLogin(String username, String pwd) {
        Connection con = null;
        PreparedStatement ps2 = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        UserDetails ud = new UserDetails();
        try {
            con = this.dataSource.getConnection();
            String sql = "SELECT username, password, enable, accountnonexpired, accountnonlocked, credentialsnonexpired, userid , usertype , linkid, password, last_login_failed, no_login_failed, last_login "
                    + "FROM user_details where username=? and password=?";
            ps2 = con.prepareStatement(sql);
            ps2.setString(1, username);
			ps2.setString(2, pwd);
            rs = ps2.executeQuery();
            if (rs.next()) {
                ud.setPassword(rs.getString("password"));
                ud.setLinkid(rs.getString("linkid"));
                ud.setUsertype(rs.getString("usertype"));
                ud.setAccountnonexpired(rs.getInt("accountnonexpired"));
                ud.setAccountnonlocked(rs.getInt("accountnonlocked"));
                ud.setCredentialsnonexpired(rs.getInt("credentialsnonexpired"));
                ud.setNoofloginfailed(rs.getInt("no_login_failed"));
                ud.setLastloginfailed(rs.getTimestamp("last_login_failed"));
                ud.setLastlogin(rs.getTimestamp("last_login"));
            }else{
                ud.setLinkid("F");
            }
            
                if (!ud.getLinkid().equals("F")) {
                    /*If login is sucessfull then last login is saved in database.*/
                    pstmt = con.prepareStatement("UPDATE user_details SET last_login = ?,no_login_failed=0 , last_login_failed=null where linkid=?");
                    pstmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                    pstmt.setString(2, ud.getLinkid());
                    pstmt.executeUpdate();
                } else {
                    /*If login fails. It will calculate the last failed attempt.*/
                    if (ud.getLastloginfailed() == null) {
                        ud.setLastloginfailed(new Timestamp(System.currentTimeMillis()));
                    }
                    long diff = System.currentTimeMillis() - ud.getLastloginfailed().getTime();
                    long hour = diff / (1000 * 60 * 60);
                    /*If last attempt is within 24 hrs. then it will increase the failed attempt.*/
                    if (hour < 24) {
                        if (ud.getNoofloginfailed() == 0) {
                            pstmt = con.prepareStatement("UPDATE user_details SET no_login_failed = ?, last_login_failed=? where linkid=?");
                            pstmt.setInt(1, 1);
                            pstmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                            pstmt.setString(3, ud.getLinkid());
                            pstmt.executeUpdate();
                        } else if (ud.getNoofloginfailed() > 0 && ud.getNoofloginfailed() < 4) {
                            pstmt = con.prepareStatement("UPDATE user_details SET no_login_failed = ? where linkid=?");
                            pstmt.setInt(1, ud.getNoofloginfailed() + 1);
                            pstmt.setString(2, ud.getLinkid());
                            pstmt.executeUpdate();
                        } else {
                            pstmt = con.prepareStatement("UPDATE user_details SET no_login_failed = ?, accountnonlocked = 0, last_login_failed=? where linkid=?");
                            pstmt.setInt(1, ud.getNoofloginfailed() + 1);
                            pstmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                            pstmt.setString(3, ud.getLinkid());
                            pstmt.executeUpdate();
                        }
                    } else {
                        /*If last attempt is more than 24 hrs. then it will count as 1st attempt.*/
                        pstmt = con.prepareStatement("UPDATE user_details SET no_login_failed = ?, last_login_failed=? where linkid=?");
                        pstmt.setInt(1, 1);
                        pstmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                        pstmt.setString(3, ud.getLinkid());
                        pstmt.executeUpdate();
                    }
                    ud = null;
                }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }

        return ud;
    }

    @Override
    public Users getEmployeeProfileInfo(String hrmsid) {

        Users emp = new Users();
        Connection con = null;
        ResultSet rs2 = null;
        Statement stmt2 = null;
        PreparedStatement ps = null;

        String sql = "";
        try {
            con = this.dataSource.getConnection();
            /*
             emp = (Users) session.get(Users.class, hrmsid);
             emp.setHrmsEncId(CommonFunctions.encodedTxt(hrmsid));
             emp.setDob(emp.getDob());
             emp.setDoegov(emp.getDoegov());
             emp.setUsertype("G");
             Hibernate.initialize(emp.getOffice());
             Hibernate.initialize(emp.getSubstantivePost());
             Hibernate.initialize(emp.getCadre());
            
             */
            sql = "SELECT G.deploy_type,A.EMP_ID,ARRAY_TO_STRING(ARRAY[A.INITIALS, A.F_NAME, A.M_NAME, A.L_NAME], ' ') EMPNAME,A.USERID,A.PWD,A.USERTYPE,A.DOE_GOV,A.DOB,A.MOBILE,A.POST_GRP_TYPE,A.ACCT_TYPE,A.GPF_NO,A.DATE_OF_NINCR,A.GP,A.CUR_SALARY,A.CUR_BASIC_SALARY, "
                    + "	D.CADRE_NAME, D.CADRE_CODE,E.OFF_EN, E.OFF_CODE, B.SPC, B.SPN, B.GPC, C.POST,C.POST_CODE,C.DEPARTMENT_CODE,GD.department_name FROM   EMP_MAST A "
                    + "	left outer join G_CADRE D on A.CUR_CADRE_CODE=D.CADRE_CODE "
                    + "	LEFT outer join G_SPC B on A.CUR_SPC=B.SPC "
                    + " left outer join g_deploy_type G ON A.dep_code=G.deploy_code"
                    + "	left outer join g_post C on B.gpc=C.post_code "
                    + " LEFT OUTER JOIN g_department GD ON C.department_code = GD.department_code, "
                    + "	G_OFFICE E "
                    + "	WHERE A.CUR_OFF_CODE=E.OFF_CODE AND A.EMP_ID=? ";

            //System.out.println(sql);
            ps = con.prepareStatement(sql);
            ps.setString(1, hrmsid);

            rs2 = ps.executeQuery();
            if (rs2.next()) {
                emp.setHrmsEncId(CommonFunctions.encodedTxt(hrmsid));
                emp.setEmpId(hrmsid);
                emp.setFullName(rs2.getString("EMPNAME"));
                emp.setDob(rs2.getDate("DOB"));
                emp.setDoegov(rs2.getDate("DOE_GOV"));
                emp.setMobile(rs2.getString("MOBILE"));
                emp.setPostgrp(rs2.getString("POST_GRP_TYPE"));
                emp.setAcctType(rs2.getString("ACCT_TYPE"));
                emp.setGpfno(rs2.getString("GPF_NO"));
                emp.setDateOfnincr(rs2.getDate("DATE_OF_NINCR"));
                emp.setGradepay(rs2.getInt("GP"));
                emp.setPayscale(rs2.getString("CUR_SALARY"));
                emp.setCurBasic(rs2.getDouble("CUR_BASIC_SALARY"));
                emp.setCadrecode(rs2.getString("CADRE_CODE"));
                emp.setCadrename(rs2.getString("CADRE_NAME"));
                emp.setOffname(rs2.getString("OFF_EN"));
                emp.setOffcode(rs2.getString("OFF_CODE"));
                emp.setCurspc(rs2.getString("SPC"));
                emp.setSpn(rs2.getString("SPN"));
                emp.setGpc(rs2.getString("GPC"));
                emp.setPostname(rs2.getString("POST"));
                emp.setDeptcode(rs2.getString("DEPARTMENT_CODE"));
                emp.setDepstatus(rs2.getString("deploy_type"));
                emp.setDeptName(rs2.getString("department_name"));
                emp.setUsertype("G");
                emp.setPostCode(rs2.getString("POST_CODE"));
            }

            stmt2 = con.createStatement();
            rs2 = stmt2.executeQuery("SELECT id_no FROM EMP_ID_DOC WHERE EMP_ID='" + hrmsid + "' AND ID_DESCRIPTION='AADHAAR'");
            if (rs2.next()) {
                emp.setAadharno(rs2.getString("id_no"));
            }
            DataBaseFunctions.closeSqlObjects(rs2, stmt2);

            if (emp.getCurspc() != null) {

                stmt2 = con.createStatement();
                rs2 = stmt2.executeQuery("SELECT ROLE_ID FROM G_PRIVILEGE_MAP WHERE SPC='" + emp.getCurspc() + "' GROUP BY ROLE_ID ");
                while (rs2.next()) {
                    emp.setHasPrivilages("Y");
                    if (rs2.getString("ROLE_ID").equals("01")) {
                        emp.setHasmyCadreTab("Y");
                    } else if (rs2.getString("ROLE_ID").equals("02")) {
                        emp.setHasmyDeptTab("Y");
                    } else if (rs2.getString("ROLE_ID").equals("03")) {
                        emp.setHasmyDistTab("Y");
                    } else if (rs2.getString("ROLE_ID").equals("04")) {
                        emp.setHasmyHodTab("Y");
                    } else if (rs2.getString("ROLE_ID").equals("05")) {
                        emp.setHasmyOfficeTab("Y");
                    } else if (rs2.getString("ROLE_ID").equals("10")) {
                        emp.setHasparadminTab("Y");
                    } else if (rs2.getString("ROLE_ID").equals("11")) {
                        emp.setHaspoliceDGTab("Y");
                    }else if (rs2.getString("ROLE_ID").equals("13")) {
                        emp.setHasCommandandAuthPriv("Y");
                    }
                }

            }
            DataBaseFunctions.closeSqlObjects(rs2, stmt2);
            stmt2 = con.createStatement();
            rs2 = stmt2.executeQuery("SELECT revisioning_auth_emp_id FROM pay_revision_option WHERE revisioning_auth_emp_id='" + hrmsid + "' ");
            if (rs2.next()) {
                if (rs2.getString("revisioning_auth_emp_id") != null && !rs2.getString("revisioning_auth_emp_id").equals("")) {
                    emp.setHasPayRevisionAuth("Y");
                }
            }
            
            DataBaseFunctions.closeSqlObjects(rs2, stmt2);
            stmt2 = con.createStatement();
            rs2 = stmt2.executeQuery("SELECT checking_auth_emp_id FROM pay_revision_option WHERE checking_auth_emp_id='"+hrmsid+"' ");
            if (rs2.next()) {
                if (rs2.getString("checking_auth_emp_id") != null && !rs2.getString("checking_auth_emp_id").equals("")) {
                    emp.setHascheckingAuth("Y");
                }
            }
            
            DataBaseFunctions.closeSqlObjects(rs2, stmt2);
            stmt2 = con.createStatement();
            rs2 = stmt2.executeQuery("SELECT verifying_auth_emp_id FROM pay_revision_option WHERE verifying_auth_emp_id='"+hrmsid+"' ");
            if (rs2.next()) {
                if (rs2.getString("verifying_auth_emp_id") != null && !rs2.getString("verifying_auth_emp_id").equals("")) {
                    emp.setHasverifyingAuth("Y");
                }
            }
            
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }

        return emp;
    }

    public AdminUsers getAdminUsersProfileInfo(String userid) {
        Connection con = null;
        PreparedStatement ps2 = null;
        ResultSet rs = null;
        AdminUsers adm = new AdminUsers();
        String sql = "";
        try {
            con = this.dataSource.getConnection();
            sql = "SELECT USER_ID,FULL_NAME,district_code FROM USER_MASTER WHERE USER_ID=?";
            ps2 = con.prepareStatement(sql);
            ps2.setInt(1, Integer.parseInt(userid));
            rs = ps2.executeQuery();
            if (rs.next()) {
                adm.setEmpId(userid);
                adm.setFullName(rs.getString("FULL_NAME"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return adm;
    }

    public LAMembers getMiniSterialProfileInfo(String lmid) {
        Connection con = null;
        PreparedStatement ps2 = null;
        ResultSet rs = null;
        LAMembers lam = new LAMembers();
        try {
            con = this.dataSource.getConnection();
            String sql = "SELECT LMID,fullname,OFF_AS,OFF_NAME,GETMINISTERSDEPT(LMID) OFF_EN,MOBILE FROM ( "
                    + "SELECT LMID,ARRAY_TO_STRING(ARRAY[INITIALS, FNAME, MNAME,LNAME], ' ') fullname, OFF_AS,MOBILE FROM LA_MEMBERS WHERE LMID::TEXT=?) LA_MEMBERS "
                    + "INNER JOIN G_OFFICIATING ON LA_MEMBERS.OFF_AS=G_OFFICIATING.OFF_ID";
            ps2 = con.prepareStatement(sql);
            ps2.setString(1, lmid);
            rs = ps2.executeQuery();
            if (rs.next()) {
                lam.setEmpId(rs.getInt("lmid"));
                lam.setUsertype("M");
                lam.setFullName(rs.getString("fullname"));
                lam.setMobile(rs.getString("mobile"));
                lam.setDesignation(rs.getString("OFF_NAME"));
                lam.setOffName(rs.getString("OFF_EN"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return lam;
    }

    @Override
    public List getProposedEmployeeList(String offCode, int year, int month) {
        Session session = null;
        List emplist = null;
        try {
            session = this.sessionFactory.openSession();
            Criteria crit = session.createCriteria(Users.class, "emp");
            crit.createAlias("emp.substantivePost", "spc");
            crit.add(Restrictions.eq("office.offCode", offCode));
            crit.add(Restrictions.sqlRestriction(" extract(year from {alias}.DATE_OF_NINCR) = ? ", year, IntegerType.INSTANCE));
            crit.add(Restrictions.sqlRestriction(" extract(month from {alias}.DATE_OF_NINCR) = ? ", (month + 1), IntegerType.INSTANCE));
            crit.addOrder(Order.asc("fname"));

            emplist = crit.list();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.flush();
            session.close();
        }
        return emplist;
    }

    @Override
    public List getEmployeeList(String offCode) {
        Session session = null;
        List emplist = null;
        try {
            session = this.sessionFactory.openSession();
            Criteria crit = session.createCriteria(Users.class, "emp");
            crit.createAlias("emp.substantivePost", "spc");
            crit.add(Restrictions.eq("office.offCode", offCode));
            crit.addOrder(Order.asc("fname"));

            emplist = crit.list();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.flush();
            session.close();
        }
        return emplist;
    }

    @Override
    public int requestPassword(Map<String, String> params) {

        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        PreparedStatement pst = null;

        String mobileno = params.get("mobile");
        String dob = params.get("dob");
        String enteredcaptcha = params.get("cap1");
        String originalcaptcha = params.get("cap2");

        String sendingDate = "";
        int msg = 0;
        boolean existindb = false;
        String empid = "";

        boolean msgSent = false;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            
            con = dataSource.getConnection();

            stmt = con.createStatement();

            Calendar cal = Calendar.getInstance();
            DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
            sendingDate = dateFormat.format(cal.getTime());

            if (!enteredcaptcha.equals(originalcaptcha)) {
                msg = 5;
            } else {
                if (mobileno != null && !mobileno.equals("")) {

                    String sql = "SELECT EMP_ID,MOBILE,DOB,L_NAME,USERID FROM EMP_MAST WHERE MOBILE='" + mobileno + "'";
                    rs = stmt.executeQuery(sql);
                    if (rs.next()) {
                        empid = rs.getString("EMP_ID");
                        //System.out.println("Table Date of Birth is: "+CommonFunctions.getFormattedOutputDate(rs.getDate("DOB")));
                        if (rs.getDate("DOB") != null && CommonFunctions.getFormattedOutputDate1(rs.getDate("DOB")).equalsIgnoreCase(dob)) {

                            /*pst = con.prepareStatement("INSERT INTO MESSAGE_TRAN (MSG_TRAN_ID, EMP_ID, MOBILE_NO, MSG_SEND_DATE, IS_DELIVERED, IS_REGISTERED, MSG_TYPE) VALUES (?,?,?,?,?,?,?)");
                             pst.setInt(1, CommonFunctions.getMaxNumberIncludeMissingSeries(con, "MESSAGE_TRAN", "MSG_TRAN_ID", 0));
                             pst.setString(2, empid);
                             pst.setString(3, mobileno);
                             pst.setTimestamp(4, new Timestamp(dateFormat.parse(sendingDate).getTime()));
                             pst.setString(5, "N");
                             pst.setString(6, "N");
                             pst.setString(7, "2");
                             pst.execute();*/
                            int smsperday = getSMSCountPerDay(con, mobileno);
                            //System.out.println("SMS Count is: "+smsperday);
                            if (smsperday < 3) {
                                boolean requestsms = getSMSDelayInMins(con, mobileno);
                                if (requestsms == true) {
                                    SMSThread smsthread = new SMSThread(empid, mobileno, "FP");
                                    Thread t1 = new Thread(smsthread);
                                    t1.start();
                                    msg = 1;
                                    msgSent = true;
                                } else {
                                    msg = 6;
                                }
                            } else {
                                msg = 3;
                            }
                        } else {
                            msg = 4;
                        }
                    } 
                    //if (msgSent == false) {
                    //msg = 2;
                    //}
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, stmt);
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return msg;
    }

    public int getSMSCountPerDay(Connection con, String mobile) {

        PreparedStatement pst = null;
        ResultSet rs = null;

        int count = 0;
        try {
            String startTime = "";
            Calendar cal = Calendar.getInstance();
            //DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            startTime = dateFormat.format(cal.getTime());
            //System.out.println("Today's Date is: "+startTime);
            //pst = con.prepareStatement("SELECT count(*) cnt FROM sms_log WHERE sent_on > CURRENT_TIMESTAMP - INTERVAL '100 days' AND MOBILE=?");
            String sql = "SELECT count(*) cnt FROM sms_log WHERE sent_on::date = '" + startTime + "' AND MOBILE='" + mobile + "'";
            //System.out.println("SQL is: "+sql);
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            if (rs.next()) {
                count = rs.getInt("cnt");
            }
            //System.out.println("Count is: "+count);
        } catch (Exception e) {
            DataBaseFunctions.closeSqlObjects(rs, pst);
        }
        return count;
    }

    public boolean getSMSDelayInMins(Connection con, String mobile) {

        PreparedStatement pst = null;
        ResultSet rs = null;

        boolean requestSMS = true;
        try {
            String startTime = "";
            Calendar cal = Calendar.getInstance();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            startTime = dateFormat.format(cal.getTime());

            String sql = "select (extract(epoch from '" + startTime + "' - sent_on)/60) diff from sms_log where sent_on::date = '" + startTime + "' and mobile='" + mobile + "'";
            //System.out.println("SQL is: "+sql);
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                if (rs.getString("diff") != null && !rs.getString("diff").equals("")) {
                    if (rs.getDouble("diff") <= 5) {
                        requestSMS = false;
                    }
                }
            }
            //System.out.println("requestSMS is: "+requestSMS);
        } catch (Exception e) {
            DataBaseFunctions.closeSqlObjects(rs, pst);
        }
        return requestSMS;
    }

    @Override
    public Users getInstituteInfo(String linkid) {
        Connection con = null;
        PreparedStatement ps2 = null;
        ResultSet rs = null;
        Users us = new Users();
        try {
            con = this.dataSource.getConnection();
            String sql = "SELECT institution_code, institution_name FROM user_details"
                    + " UD INNER JOIN g_institutions GI ON UD.linkid = GI.institution_code::text"
                    + " WHERE UD.linkid = '" + linkid + "'";
            ps2 = con.prepareStatement(sql);
            rs = ps2.executeQuery();
            if (rs.next()) {
                us.setEmpId("" + rs.getInt("institution_code"));
                us.setUsertype("I");
                us.setFullName(rs.getString("institution_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return us;
    }

    @Override
    public void updateCadreStatus(String empId, String cadreStat, String subCadreStat) {

        Connection con = null;

        PreparedStatement pst = null;
        int retVal = 0;
        try {
            con = this.dataSource.getConnection();

            pst = con.prepareStatement("UPDATE EMP_MAST SET ACS=?,ASCS=? WHERE EMP_ID=?");
            pst.setString(1, cadreStat);
            pst.setString(2, subCadreStat);
            pst.setString(3, empId);
            retVal = pst.executeUpdate();

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    public AdminUsers getDeptUsersProfileInfo(String userid) {
        Connection con = null;
        PreparedStatement ps2 = null;
        ResultSet rs = null;
        AdminUsers adm = new AdminUsers();
        String sql = "";
        try {
            con = this.dataSource.getConnection();
            sql = "SELECT department_name from g_department where department_code=?";
            ps2 = con.prepareStatement(sql);
            ps2.setString(1, userid);
            rs = ps2.executeQuery();
            if (rs.next()) {
                adm.setEmpId(userid);
                adm.setFullName(rs.getString("department_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return adm;
    }

    @Override
    public UserExpertise getUserInfo(String hrmsid) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        UserExpertise ueObj = new UserExpertise();
        try {
            con = this.dataSource.getConnection();

            String sql = "SELECT ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME,L_NAME], ' ') FULL_NAME,POST,CADRE_GRADE,DEPARTMENT_NAME,CUR_OFF_CODE,MOBILE,EMAIL_ID,OFF_EN,SUFFIX FROM EMP_MAST"
                    + " LEFT OUTER JOIN G_SPC ON EMP_MAST.CUR_SPC=G_SPC.SPC"
                    + " LEFT OUTER JOIN G_POST ON G_SPC.GPC=G_POST.POST_CODE"
                    + " LEFT OUTER JOIN G_OFFICE ON EMP_MAST.CUR_OFF_CODE=G_OFFICE.OFF_CODE"
                    + " LEFT OUTER JOIN G_DEPARTMENT ON G_OFFICE.DEPARTMENT_CODE=G_DEPARTMENT.DEPARTMENT_CODE WHERE EMP_ID=?";
            pst = con.prepareStatement(sql);
            pst.setString(1, hrmsid);
            rs = pst.executeQuery();
            if (rs.next()) {
                ueObj.setName(rs.getString("FULL_NAME"));
                ueObj.setDesignation(rs.getString("POST"));
                ueObj.setGrade(rs.getString("CADRE_GRADE"));
                ueObj.setDeptname(rs.getString("DEPARTMENT_NAME"));
                ueObj.setPostingPlace(rs.getString("SUFFIX"));
                ueObj.setCurofficename(rs.getString("OFF_EN"));
                ueObj.setMobile(rs.getString("MOBILE"));
                ueObj.setEmailid(rs.getString("EMAIL_ID"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return ueObj;
    }

    @Override
    public boolean countExpertise(String empid) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        boolean isEmpPresent = false;
        String curCadreCode = null;
        String sql = null;
        try {
            con = this.dataSource.getConnection();
            sql = "SELECT CUR_CADRE_CODE FROM emp_mast WHERE EMP_ID=?";
            pst = con.prepareStatement(sql);
            pst.setString(1, empid);
            rs = pst.executeQuery();
            if (rs.next()) {
                curCadreCode = rs.getString("CUR_CADRE_CODE");
                if(curCadreCode != null && !curCadreCode.equals("")){
                    if (curCadreCode.equalsIgnoreCase("1101") || curCadreCode.equalsIgnoreCase("1103") || curCadreCode.equalsIgnoreCase("2668") || curCadreCode.equalsIgnoreCase("0723") || curCadreCode.equalsIgnoreCase("1165")) {
                        isEmpPresent = false;
                    } else {
                        isEmpPresent = true;
                    }
                }else{
                    isEmpPresent = true;
                }
            }
            if (!isEmpPresent) {
                sql = "SELECT COUNT(*) CNT FROM g_emp_expertise WHERE EMP_ID=?";
                pst = con.prepareStatement(sql);
                pst.setString(1, empid);
                rs = pst.executeQuery();
                if (rs.next()) {
                    if (rs.getInt("CNT") > 0) {
                        isEmpPresent = true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return isEmpPresent;
    }

    @Override
    public Module[] getUserPrivileageList(String username) {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet result = null;
        List<Module> modulelist = new ArrayList<>();
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("SELECT g_user_privilege_map.mod_id,mod_name,url FROM g_user_privilege_map INNER JOIN g_module_link ON g_user_privilege_map.mod_id = g_module_link.mod_id WHERE username=?");
            pst.setString(1, username);
            result = pst.executeQuery();
            while (result.next()) {
                Module module = new Module();
                module.setModid(result.getInt("mod_id"));
                module.setModname(result.getString("mod_name"));
                module.setModurl(result.getString("url"));
                modulelist.add(module);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(result, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        Module moduleArray[] = modulelist.toArray(new Module[modulelist.size()]);
        return moduleArray;
    }
     @Override
public String getCurrentTrainingProgram() {
        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        String trainingIds = null;
        String sql = null;
        try {
            con = this.dataSource.getConnection();
            sql = "SELECT training_program_code FROM g_training_programs WHERE is_published = 'Y' LIMIT 1";
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            if (rs.next()) {
                trainingIds = ""+rs.getInt("training_program_code");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return trainingIds;
    }    
    @Override
    public boolean getEligibility(String postCode) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        boolean isEligible = false;
        String sql = null;
        String trainingIds = getCurrentTrainingProgram();
        try {
            con = this.dataSource.getConnection();
                sql = "SELECT COUNT(*) CNT FROM g_training_postcodes WHERE training_id="+trainingIds+" AND post_code = '"+postCode+"'";
                System.out.println(sql);
                pst = con.prepareStatement(sql);
                rs = pst.executeQuery();
                if (rs.next()) {
                    if (rs.getInt("CNT") > 0) {
                        isEligible = true;
                    }
                }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return isEligible;
    }                              
}
