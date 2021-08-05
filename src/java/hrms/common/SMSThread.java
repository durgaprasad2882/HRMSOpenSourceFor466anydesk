package hrms.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import oracle.ldap.util.jndi.ConnectionUtil;
import oracle.ldap.util.Util;

public class SMSThread implements Runnable {

    private String empid = null;
    private String mobile = null;
    private String msgType = null;

    boolean running = true;

    public SMSThread(String empId, String mobile, String type) {
        this.empid = empId;
        this.mobile = mobile;
        this.msgType = type;
    }

    @Override
    public void run() {
        Connection conpsql = null;
        Connection conora = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        String deliverymsg = "";
        String msg = "";

        String ipaddress = null;
        String port = null;
        String userName = null;
        String pwd = null;
        DirContext ctx = null;
        ipaddress = "172.16.1.21";
        port = "13060";
        userName = "cn=orcladmin";
        pwd = "cmgi2011";
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conora = DriverManager.getConnection("jdbc:oracle:thin:@172.16.1.12:1522:orcl", "hrmis", "hrmis");
           // conora = DriverManager.getConnection("jdbc:oracle:thin:@192.168.1.121:1521:orcl", "hrmis", "hrmis");

            Class.forName("org.postgresql.Driver");
            conpsql = DriverManager.getConnection("jdbc:postgresql://172.16.1.14/hrmis", "hrmis2", "cmgi");
           // conpsql = DriverManager.getConnection("jdbc:postgresql://192.168.1.19/hrmis", "hrmis2", "hrmis2");

            if (msgType.equals("L")) {
                msg = "LEAVE PENDING";
                SMSHttpPostClient smhttp = new SMSHttpPostClient(mobile, msg);
                deliverymsg = smhttp.send_sms();
                insertSMSData(conpsql, empid, msg, mobile, deliverymsg, "LEAVE_PENDING");
            } else if (msgType.equals("S")) {
                msg = "LEAVE IS SANCTIONED";
                SMSHttpPostClient smhttp = new SMSHttpPostClient(mobile, msg);
                deliverymsg = smhttp.send_sms();
                insertSMSData(conpsql, empid, msg, mobile, deliverymsg, "LEAVE_SANCTIONED");
            } else if (msgType.equals("FP")) {
                ctx = ConnectionUtil.getDefaultDirCtx(ipaddress, port, userName, pwd);
                System.out.println("empid is: " + empid + " and mobile is: " + mobile);
                updateoidandDB(ctx, conora, conpsql, empid, mobile);

                msg = getMsgforSMS(conpsql, empid);

                //SMSHttpPostClient smhttp = new SMSHttpPostClient(mobile, msg);
                //deliverymsg = smhttp.send_sms();
                SendSMS sm = new SendSMS(mobile, msg);
                deliverymsg = sm.send();

                insertSMSData(conpsql, empid, msg, mobile, deliverymsg, "USERID/PASSWORD/FROM_THREAD");
            }
            System.out.println("deliverymsg is: " + deliverymsg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(conpsql);
            DataBaseFunctions.closeSqlObjects(conora);
        }
    }

    public void updateoidandDB(DirContext ctx, Connection conora, Connection conpsql, String empId, String mobile) {

        PreparedStatement ps = null;
        PreparedStatement ps1 = null;
        // System.out.println("inside updateoidandDB method");

        ResultSet rs = null;
        ResultSet rs1 = null;
        try {
            String userDN = "cn=" + empId + ",cn=Users,dc=hrmsorissa,dc=gov,dc=in";
            //String newpwd = RandomStringUtils.randomAlphanumeric(5).toLowerCase() + empId;

            //newpwd = newpwd.replaceAll("[^a-zA-Z0-9]+", "");
            int noOfCAPSAlpha = 1;
            int noOfDigits = 1;
            int noOfSplChars = 0;
            int minLen = 8;
            int maxLen = 8;

            char[] pswd = RandomPasswordGenerator.generatePswd(minLen, maxLen,
                    noOfCAPSAlpha, noOfDigits, noOfSplChars);

            String newpwd = new String(pswd);

            String newUserId = "";
            String usertype = "";

            ps1 = conpsql.prepareStatement("SELECT * FROM USER_DETAILS WHERE LINKID=?");
            ps1.setString(1, empId);
            rs = ps1.executeQuery();
            boolean updateEmpMast = false;
            if (rs.next()) {
                if (rs.getString("USERNAME") == null || rs.getString("USERNAME").equals("")) {
                    ps = conpsql.prepareStatement("SELECT USERID,USERTYPE,F_NAME,L_NAME FROM EMP_MAST WHERE EMP_ID=? AND MOBILE=?");
                    ps.setString(1, empId);
                    ps.setString(2, mobile);
                    rs1 = ps.executeQuery();
                    if (rs1.next()) {
                        newUserId = rs1.getString("USERID");
                        if (newUserId != null && !newUserId.equals("")) {
                            if (Character.isDigit(newUserId.charAt(0))) {
                                newUserId = rs1.getString("F_NAME").trim().toLowerCase() + "." + rs1.getString("L_NAME").trim().toLowerCase();
                                updateEmpMast = true;
                            }
                        } else {
                            newUserId = rs1.getString("F_NAME").trim().toLowerCase() + "." + rs1.getString("L_NAME").trim().toLowerCase();
                            updateEmpMast = true;
                        }
                        usertype = rs1.getString("USERTYPE");
                    }
                    String verifiedUserId = verifyUserID(conpsql, newUserId);
                    updateUserId(conora, conpsql, empId, verifiedUserId, ctx, userDN, usertype, updateEmpMast);
                }
            } else {
                ps = conpsql.prepareStatement("SELECT USERID,USERTYPE,F_NAME,L_NAME FROM EMP_MAST WHERE EMP_ID=? AND MOBILE=?");
                ps.setString(1, empId);
                ps.setString(2, mobile);
                rs1 = ps.executeQuery();
                if (rs1.next()) {
                    newUserId = rs1.getString("USERID");
                    if (newUserId != null && !newUserId.equals("")) {
                        if (Character.isDigit(newUserId.charAt(0))) {
                            newUserId = rs1.getString("F_NAME").trim().toLowerCase() + "." + rs1.getString("L_NAME").trim().toLowerCase();
                            updateEmpMast = true;
                        }
                    } else {
                        newUserId = rs1.getString("F_NAME").trim().toLowerCase() + "." + rs1.getString("L_NAME").trim().toLowerCase();
                        updateEmpMast = true;
                    }
                    usertype = rs1.getString("USERTYPE");
                }
                System.out.println("Usertype is: " + usertype);
                String verifiedUserId = verifyUserID(conpsql, newUserId);

                insertUserId(conora, conpsql, empId, verifiedUserId, ctx, userDN, usertype, updateEmpMast);

            }

            ps1 = conpsql.prepareStatement("UPDATE EMP_MAST SET PWD=?,MOBILE=? WHERE EMP_ID=?");
            ps1.setString(1, newpwd);
            ps1.setString(2, mobile);
            ps1.setString(3, empId);
            ps1.executeUpdate();
            System.out.println(" update in db psql(emp_mast)");

            ps1 = conpsql.prepareStatement("UPDATE USER_DETAILS SET PASSWORD=?,ACCOUNTNONLOCKED=? WHERE LINKID=?");
            ps1.setString(1, newpwd);
            ps1.setInt(2, 1);
            ps1.setString(3, empId);
            ps1.executeUpdate();
            System.out.println(" update in db psql(user_details)");

            ps = conora.prepareStatement("UPDATE EMP_MAST SET PWD=? WHERE EMP_ID=?");
            ps.setString(1, newpwd);
            ps.setString(2, empId);
            ps.executeUpdate();
            System.out.println(" update in db oracle");

            if (Util.dnExists(ctx, userDN)) {
                modifyOIDPassword(ctx, userDN, newpwd);
            }

            System.out.println(" update in oid ");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, ps);
            DataBaseFunctions.closeSqlObjects(rs1, ps1);
        }
    }

    public static void modifyOIDPassword(DirContext ctx, String User, String newPassword) throws Exception {
        ModificationItem[] mods = new ModificationItem[1];
        //System.out.println("Inside modify,userid is: "+User);
        //System.out.println("Inside modify,new password is: "+newPassword);
        mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("userpassword", newPassword));
        ctx.modifyAttributes(User, mods);
    }

    public static void modifyOIDUserId(DirContext ctx, String User, String newUid) throws Exception {
        ModificationItem[] mods = new ModificationItem[1];
        //System.out.println("Inside modify,userid is: "+User);
        //System.out.println("Inside modify,new password is: "+newPassword);
        mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("uid", newUid));
        ctx.modifyAttributes(User, mods);
    }

    public String getMsgforSMS(Connection conpsql, String empid) {

        PreparedStatement pst = null;
        ResultSet rs = null;

        String msg = "";
        try {
            String eol = System.getProperty("line.separator");
            String sql = "SELECT USERNAME,PASSWORD FROM USER_DETAILS WHERE LINKID=?";
            pst = conpsql.prepareStatement(sql);
            pst.setString(1, empid);
            rs = pst.executeQuery();
            if (rs.next()) {
                if (rs.getString("USERNAME") != null && !rs.getString("USERNAME").equals("")) {
                    msg = "User Id: " + rs.getString("USERNAME") + eol + "Password: " + rs.getString("PASSWORD");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
        }
        return msg;
    }

    public void insertSMSData(Connection conpsql, String empid, String msg, String mobile, String deliverymsg, String msgtype) {

        PreparedStatement pst = null;

        String startTime = "";
        try {
            Calendar cal = Calendar.getInstance();
            DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");
            startTime = dateFormat.format(cal.getTime());

            pst = conpsql.prepareStatement("INSERT INTO SMS_LOG(MSG_ID,EMP_ID,MESSAGE_TEXT,MESSAGE_TYPE,MOBILE,SENT_ON,STATUS) VALUES(?,?,?,?,?,?,?)");
            pst.setInt(1, CommonFunctions.getMaxCode(conpsql, "SMS_LOG", "MSG_ID"));
            pst.setString(2, empid);
            pst.setString(3, msg);
            pst.setString(4, msgtype);
            pst.setString(5, mobile);
            pst.setTimestamp(6, new Timestamp(dateFormat.parse(startTime).getTime()));
            pst.setString(7, deliverymsg);
            pst.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
        }
    }

    private static String verifyUserID(Connection conpsql, String userid) {

        PreparedStatement pst = null;
        ResultSet rs = null;

        String username = "";
        String regex = "[^\\d]+";
        ArrayList numList = new ArrayList();
        int greaterInt = 0;
        try {
            regex = "[^A-Za-z. ]+";
            String[] uidArr = userid.split(regex);

            String sql = "SELECT USERNAME FROM USER_DETAILS WHERE USERNAME LIKE '" + uidArr[0] + "%'";
            System.out.println(sql);
            pst = conpsql.prepareStatement(sql);
            rs = pst.executeQuery();
            regex = "[^\\d]+";
            String uid = "";
            while (rs.next()) {
                uid = rs.getString("USERNAME");
                String[] str = uid.split(regex);
                //System.out.println("uid is: " + uid);
                //System.out.println("Inside verifyUserID str[1] is: " + str[1]);
                if (str.length > 0) {
                    System.out.println("Inside verifyUserID str[1] is: " + str[1]);
                    numList.add(str[1]);
                }
            }
            if (numList != null && numList.size() > 0) {
                for (int i = 0; i < numList.size(); i++) {
                    int numVal = Integer.parseInt(numList.get(i) + "");
                    if (numVal > greaterInt) {
                        greaterInt = numVal;
                    }
                }
            }
            if (greaterInt > 0) {
                greaterInt = greaterInt + 1;
            }
            System.out.println("Inside verifyUserID greaterInt: " + greaterInt);

            //regex = "[^A-Za-z. ]+";
            //uidArr = userid.split(regex);
            //System.out.println("Split Username: " + uidArr[0]);
            if (uidArr.length > 0) {
                username = uidArr[0];
            } else {
                username = userid;
            }
            if (greaterInt > 0) {
                username = username + greaterInt;
            } else if (uid != null && !uid.equals("")) {
                username = username + 1;
            }
            System.out.println("Inside verifyUserID New Username: " + username);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
        }
        return username;
    }

    public void insertUserId(Connection conora, Connection conpsql, String empId, String newuserid, DirContext ctx, String userDN, String usertype, boolean updateEmpMast) {

        PreparedStatement pstInsert = null;

        try {
            pstInsert = conpsql.prepareStatement("INSERT INTO user_details(username, password, enable, accountnonexpired, accountnonlocked,credentialsnonexpired, usertype, linkid) VALUES(?,?,?,?,?,?,?,?)");
            pstInsert.setString(1, newuserid.toLowerCase());
            pstInsert.setString(2, "");
            pstInsert.setInt(3, 1);
            pstInsert.setInt(4, 1);
            pstInsert.setInt(5, 1);
            pstInsert.setInt(6, 1);
            pstInsert.setString(7, "G");
            pstInsert.setString(8, empId);
            pstInsert.executeUpdate();

            pstInsert = conpsql.prepareStatement("UPDATE EMP_MAST SET USERID=? WHERE EMP_ID=?");
            pstInsert.setString(1, newuserid.toLowerCase());
            pstInsert.setString(2, empId);
            pstInsert.executeUpdate();

            pstInsert = conora.prepareStatement("UPDATE EMP_MAST SET USERID=? WHERE EMP_ID=?");
            pstInsert.setString(1, newuserid.toLowerCase());
            pstInsert.setString(2, empId);
            pstInsert.executeUpdate();

            if (Util.dnExists(ctx, userDN)) {
                modifyOIDUserId(ctx, userDN, newuserid);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pstInsert);
        }
    }

    public void updateUserId(Connection conora, Connection conpsql, String empId, String newuserid, DirContext ctx, String userDN, String usertype, boolean updateEmpMast) {

        PreparedStatement pstUpdate = null;
        System.out.println("User Id inside Update is: " + newuserid);
        try {
            pstUpdate = conpsql.prepareStatement("UPDATE user_details SET username=? where linkid=?");
            pstUpdate.setString(1, newuserid.toLowerCase());
            pstUpdate.setString(2, empId);
            pstUpdate.executeUpdate();

            pstUpdate = conpsql.prepareStatement("UPDATE EMP_MAST SET USERID=? WHERE EMP_ID=?");
            pstUpdate.setString(1, newuserid.toLowerCase());
            pstUpdate.setString(2, empId);
            pstUpdate.executeUpdate();

            pstUpdate = conora.prepareStatement("UPDATE EMP_MAST SET USERID=? WHERE EMP_ID=?");
            pstUpdate.setString(1, newuserid.toLowerCase());
            pstUpdate.setString(2, empId);
            pstUpdate.executeUpdate();

            if (Util.dnExists(ctx, userDN)) {
                modifyOIDUserId(ctx, userDN, newuserid);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            DataBaseFunctions.closeSqlObjects(pstUpdate);
        }
    }
}
