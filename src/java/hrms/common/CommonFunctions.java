/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.common;

import hrms.model.joining.JoiningData;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Surendra
 */
public class CommonFunctions implements Serializable {

    public static ArrayList maxObjPool = new ArrayList();

    private static String[] tensPlace = {"", "Ten", " Twenty", " Thirty", " Forty", " Fifty", " Sixty", "Seventy", "Eighty", "Ninety"};

    private static String[] unitPlace = {"",
        " One", " Two", " Three", " Four", " Five", " Six", " Seven", " Eight",
        " Nine", "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen",
        "Sixteen", "Seventeen", "Eighteen", "Nineteen"};

    public static Date getDateFromString(String inputdate, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date date = new Date();
        try {
            date = formatter.parse(inputdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String getFormattedOutputDatAndTime(Date dateInput) {
        if (dateInput != null) {
            DateFormat formatDate = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
            String outputDate = formatDate.format(dateInput);
            return outputDate.toUpperCase();
        } else {
            return "";
        }
    }

    public static String getNextToe(String empId, String doe, Connection con) throws Exception {

        Statement stmt = null;
        ResultSet rs = null;

        String nextToe = null;
        String queryInput = null;
        queryInput = "SELECT MAX(TOE) REC_CNT FROM(SELECT * FROM(SELECT TOE,NOT_ID ID FROM EMP_NOTIFICATION where emp_id='" + empId + "' AND DOE='" + doe + "' AND NOT_TYPE!='CHNG_STRUCTURE' AND (NOT_TYPE!='INCREMENT' OR (NOT_TYPE='INCREMENT' AND NOT_ID NOT IN (SELECT NOT_ID FROM EMP_INCR WHERE PRID IS NOT NULL AND EMP_ID='" + empId + "'))) ORDER BY ID )  TMPNT "
                + "UNION SELECT TOE,EX_ID ID FROM EMP_EXAM where emp_id='" + empId + "' AND DOE='" + doe + "'"
                + "UNION SELECT TOE,TRAINID ID FROM EMP_TRAIN where emp_id='" + empId + "' AND DOE='" + doe + "'"
                + "UNION SELECT TOE,SV_ID ID FROM EMP_SV where emp_id='" + empId + "' AND DOE='" + doe + "'"
                + "UNION SELECT TOE,NOT_ID ID FROM EMP_RELIEVE where emp_id='" + empId + "' AND DOE='" + doe + "' AND NOT_TYPE!='CHNG_STRUCTURE' AND NOT_TYPE!='SUSPENSION'"
                + "UNION SELECT TOE,NOT_ID ID FROM EMP_JOIN where emp_id='" + empId + "' AND DOE='" + doe + "' AND NOT_TYPE!='CHNG_STRUCTURE'"
                + "UNION SELECT TOE,SP_ID ID FROM EMP_REINSTATEMENT where emp_id='" + empId + "' AND DOE='" + doe + "'"
                + "UNION SELECT TOE,SP_ID ID FROM EMP_SUSPENSION where emp_id='" + empId + "' AND DOE='" + doe + "'"
                + "UNION SELECT TOE,PER_ID ID FROM EMP_PERMISSION where emp_id='" + empId + "' AND DOE='" + doe + "'"
                + "UNION SELECT TOE,QUALIFICATION ID FROM EMP_QUALIFICATION where emp_id='" + empId + "' AND DOE='" + doe + "' AND DOE IS NOT NULL "
                + "UNION SELECT TOE,GIS_ID ID FROM EMP_GIS where emp_id='" + empId + "' AND DDATE='" + doe + "'"
                + "UNION SELECT TOE,RET_ID ID FROM EMP_RET_RES where emp_id='" + empId + "' AND DOE='" + doe + "'"
                + "UNION SELECT TOE,MISC_ID ID FROM EMP_MISC where emp_id='" + empId + "' AND DOE='" + doe + "'"
                + "UNION SELECT TOE,SR_ID ID FROM EMP_SERVICERECORD where emp_id='" + empId + "' AND DOE='" + doe + "'"
                + "UNION SELECT TOE,SRP_ID ID FROM EMP_SR_PAY where emp_id='" + empId + "' AND DOE='" + doe + "')  tab1";
//		System.out.println(queryInput);
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(queryInput);
            while (rs.next()) {
                nextToe = rs.getString("REC_CNT");
                if (rs.getString("REC_CNT") != null && !rs.getString("REC_CNT").equals("")) {
                    nextToe = getNextString(rs.getString("REC_CNT"));
                } else {
                    nextToe = "01";
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, stmt);
        }
        return nextToe;
    }

    public static String getFormattedOutputDate2(Date dateInput) {
        String outputDate = "";
        if (dateInput != null) {
            try {
                DateFormat formatDate = new SimpleDateFormat("dd-MMM-yyyy");
                outputDate = formatDate.format(dateInput);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return outputDate;
    }

    // Added By Tushar
    public static Date getFormattedOutputDate(Date dateInput) throws ParseException {
        if (dateInput != null) {
            DateFormat formatDate = new SimpleDateFormat("dd-MMM-yyyy");
            String outputDate = formatDate.format(dateInput);
            //Date dt = new Date(outputDate);
            Date dt = formatDate.parse(outputDate);
            return dt;
        } else {
            return null;
        }
    }

    public static String getFormattedOutputDate1(Date dateInput) {
        if (dateInput != null) {
            DateFormat formatDate = new SimpleDateFormat("dd-MMM-yyyy");
            String outputDate = formatDate.format(dateInput);
            return outputDate.toUpperCase();
        } else {
            return null;
        }
    }
    // Added By Tushar

    public static String getFormattedOutputDate3(Date dateInput) {
        if (dateInput != null) {
            DateFormat formatDate = new SimpleDateFormat("dd-MMM-yyyy");
            String outputDate = formatDate.format(dateInput);
            return outputDate.toUpperCase();
        } else {
            return "";
        }
    }

    /* 
         
     Below Methods are used to Generate Parameter Encoding 
        
        
     */
    public static Cipher getCipher(
            String synchro1, String synchro2, String synchro3, String synchro4, boolean isEncryptMode)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        byte raw[] = (synchro1 + synchro2 + synchro3 + synchro4).getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        if (isEncryptMode) {
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        } else {
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        }
        return cipher;
    }

    public static byte[] hexToByte(String hex) {
        byte bts[] = new byte[hex.length() / 2];
        for (int i = 0; i < bts.length; i++) {
            bts[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }

        return bts;
    }

    public static String toHexString(byte bytes[]) {
        StringBuffer retString = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            retString.append(Integer.toHexString(256 + (bytes[i] & 0xff)).substring(1));
        }

        return retString.toString();
    }

    public static String encodedTxt(String text)
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        javax.crypto.Cipher cipher = getCipher("bb5", "1860", "17a74", "213f", true);
        return toHexString(cipher.doFinal(text.getBytes()));
    }

    public static String decodedTxt(String text) {
        String st = null;
        try {
            javax.crypto.Cipher cipher = getCipher("bb5", "1860", "17a74", "213f", false);
            st = new String(cipher.doFinal(hexToByte(text)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return st;
    }

    /* 
           
     above Methods are used to Generate Parameter Encoding 
        
        
     */
    public static int getMaxCode(Connection con, String tblName, String fieldName) {
        String maxQueryService = "SELECT MAX(CAST(" + fieldName + " as Integer))+1 as MaxId FROM " + tblName;
        Statement stamt = null;
        ResultSet resultset = null;
        String temp = "";
        int maxId = 1;
        try {
            stamt = con.createStatement();
            resultset = stamt.executeQuery(maxQueryService);

            if (resultset.next()) {
                temp = resultset.getString("MaxId");
                if (temp != null && !temp.equals("")) {
                    maxId = Integer.parseInt(temp);
                } else {
                    maxId = 1;
                }

            }

        } catch (SQLException sqe) {
            sqe.printStackTrace();
        }

        return maxId;
    }

    public synchronized static String getMaxCode(String tblname, String fieldname, Connection con) throws Exception {
        String mCode = null;
        Statement st = null;
        ResultSet rs = null;
        String fixedId = "21910000000000";
        try {
            st = con.createStatement();

            rs = st.executeQuery("SELECT MAX(" + fieldname + ") MCODE FROM " + tblname);
            while (rs.next()) {
                mCode = rs.getString("MCODE");

                if (mCode != null) {
                    // modified by Surendra for keep track of HRMS 2.0 transaction //
                    if (fixedId.compareTo(mCode) > 0) {
                        mCode = "21910000000001";
                    } else {
                        mCode = CommonFunctions.getNextString(mCode);
                    }
                } else {
                    //String setupDistrict = getMaxSetupCode(con);
                    // modified by Surendra for keep track of HRMS 2.0 transaction //
                    mCode = "21910000000001";

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (st != null) {
                st.close();
                st = null;
            }
        }
        mCode = getValidatedIdFromPool(tblname, fieldname, mCode);
        return mCode;
    }

    public static String getNextString(String previousString) {
        String nextString = "";
        long prevNum = 0;
        String zeroStr = "";
        if (previousString != null && !previousString.equals("")) {

            if (isNumeric(previousString) == true) {
                prevNum = Long.parseLong(previousString);
                prevNum++;
                nextString = String.valueOf(prevNum);
                if (nextString.length() < previousString.length()) {
                    for (int cnt = 0; cnt < (previousString.length() - nextString.length()); cnt++) {
                        zeroStr = zeroStr + "0";
                    }
                    nextString = zeroStr + nextString;
                }
            } else {
                if (isNumericChar(previousString.substring(0, 1)) == true) {

                    String tmp1 = previousString.substring(previousString.length() - 1, previousString.length());
                    char tmp = tmp1.charAt(0);
                    int asc = (int) tmp;
                    if (asc < 90) {
                        asc = asc + 1;
                        char[] newStr = {(char) asc};
                        //System.out.println("newStr is:"+newStr[0]);
                        String finalStr = new String(newStr);
                        //System.out.println("In If 1:"+previousString.substring(0,previousString.length()-1));
                        //System.out.println("In If 2:"+finalStr);
                        nextString = previousString.substring(0, previousString.length() - 1) + finalStr;
                    } else {
                        nextString = previousString + "A";
                    }

                } else {
                    int i = 2;
                    for (i = 0; i < previousString.length(); i++) {
                        if (isNumericChar(previousString.substring(i, i + 1)) == true) {
                            break;
                        }
                    }
                    prevNum = Long.parseLong(previousString.substring(i, previousString.length()));
                    prevNum++;
                    for (int cnt = 0; cnt < (previousString.length() - i) - String.valueOf(prevNum).length(); cnt++) {
                        zeroStr = zeroStr + "0";
                    }
                    nextString = zeroStr + nextString;
                    nextString = previousString.substring(0, i) + zeroStr + String.valueOf(prevNum);
                }
            }
        } else {
            nextString = "1";
        }
        return nextString;
    }

    public static boolean isNumericChar(String inStr) {
        boolean result = false;
        if (inStr.equals("0") || inStr.equals("1") || inStr.equals("2") || inStr.equals("3") || inStr.equals("4") || inStr.equals("5") || inStr.equals("6") || inStr.equals("7") || inStr.equals("8") || inStr.equals("9")) {
            result = true;
        }
        return result;
    }

    public static boolean isNumeric(String inStr) {
        boolean result = false;
        try {
            long num = Long.parseLong(inStr);
            result = true;
        } catch (NumberFormatException nfe) {
            //nfe.printStackTrace();
            result = false;
        }
        return result;
    }

    /**
     * Added by : Pravat Kumar Nayak Creation date: 21-08-2007
     */
    public static boolean isDouble(String inStr) {
        boolean result = false;
        try {
            double num = Double.parseDouble(inStr);
            result = true;
        } catch (NumberFormatException nfe) {
            result = false;
        }
        return result;
    }

    public static boolean isFloat(String inStr) {
        boolean result = false;
        try {
            float num = Float.parseFloat(inStr);
            result = true;
        } catch (NumberFormatException nfe) {
            result = false;
        }
        return result;
    }

    public static synchronized String getValidatedIdFromPool(String tblname, String fldname, String maxcode) {
        String id = null;
        removeOlderObjectsFromPool(tblname, fldname, maxcode);
        while (id == null) {
            if (ifMaxIdAvailableInPool(tblname, fldname, maxcode) == true) {
                maxcode = getNextString(maxcode);
            } else {
                id = maxcode;
                maxObjPool.add(new MaxObj(tblname, fldname, id));
            }
        }
        return id;
    }

    public static boolean ifMaxIdAvailableInPool(String tblname, String fldname, String maxcode) {
        boolean ifavailable = false;
        for (int i = 0; i < maxObjPool.size(); i++) {
            MaxObj mo = (MaxObj) maxObjPool.get(i);
            if (mo.getTblname().equalsIgnoreCase(tblname) && mo.getFldname().equalsIgnoreCase(fldname) && mo.getMaxcode().equalsIgnoreCase(maxcode)) {
                ifavailable = true;
                break;
            }
        }
        return ifavailable;
    }

    public static void removeOlderObjectsFromPool(String tblname, String fldname, String maxcode) {
        for (int i = 0; i < maxObjPool.size(); i++) {
            MaxObj mo = (MaxObj) maxObjPool.get(i);
            if (mo.getTblname().equalsIgnoreCase(tblname) && mo.getFldname().equalsIgnoreCase(fldname)) {
                if (Double.parseDouble(mo.getMaxcode()) < Double.parseDouble(maxcode)) {
                    maxObjPool.remove(i);
                }
            }
        }
    }

    public synchronized static int getMaxCodeInteger(String tblname, String fieldname, Connection con) throws Exception {
        int mCode = 0;
        Statement st = null;
        ResultSet rs = null;
        try {
            st = con.createStatement();

            rs = st.executeQuery("SELECT MAX(" + fieldname + ") MCODE FROM " + tblname);
            while (rs.next()) {
                mCode = rs.getInt("MCODE");

                if (mCode >= 0) {
                    mCode = Integer.parseInt(CommonFunctions.getNextString(Integer.toString(mCode)));
                } else {
                    mCode = 1;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (st != null) {
                st.close();
                st = null;
            }
        }
        mCode = Integer.parseInt(getValidatedIdFromPool(tblname, fieldname, Integer.toString(mCode)));
        return mCode;
    }

    public static int getMaxNumberIncludeMissingSeries(Connection con, String tableName, String colName, int startSeries) throws Exception {

        Statement st = null;
        ResultSet rs = null;
        String sql = "";
        int num = 1;
        try {
            /*
             *   Gap filled up Query
             *   
             */
            sql = "SELECT  * FROM    ( SELECT  " + colName + " + 1  MAXMISSING FROM    " + tableName + " mo WHERE " + colName + " > " + startSeries + " AND  NOT EXISTS ( SELECT  NULL  "
                    + " FROM    " + tableName + " mi  WHERE   mi." + colName + " = mo." + colName + " + 1 "
                    + " ) ORDER BY " + colName + " )TEMP LIMIT 1";

            st = con.createStatement();
            rs = st.executeQuery(sql);
            if (rs.next()) {
                num = rs.getInt("MAXMISSING");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, st);
        }
        return num;
    }

    public static String getSPN(Connection con, String spc) {

        PreparedStatement pst = null;
        ResultSet rs = null;

        String spn = "";
        try {
            String sql = "SELECT SPN FROM G_SPC WHERE SPC='" + spc + "'";
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            if (rs.next()) {
                spn = rs.getString("SPN");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
        }
        return spn;
    }

    public static boolean isupdatePayOrPostingInfo(String empId, String wefDate, String ordDate, String updateType, Connection con) throws Exception {
        boolean update = false;
        Statement st = null;
        ResultSet rs = null;
        Date wefCdate = null;
        Date empWefDatepayCdate = null;
        Date empWefdate = null;
        Date ordCdate = null;
        Date empOrdDate = null;
        Date sysDate = null;
        String dateform = "";
        Date dt = new Date();
        DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            st = con.createStatement();
            if (updateType != null && !updateType.equals("")) {
                if (updateType.equalsIgnoreCase("PAY")) {
                    String empmastOrddate = "";
                    String empmastWefDate = "";
                    rs = st.executeQuery("SELECT PAY_DATE, ST_DATE_OF_CUR_SALARY FROM EMP_MAST WHERE EMP_ID='" + empId + "'");
                    if (rs.next()) {
                        if (rs.getString("ST_DATE_OF_CUR_SALARY") != null && !rs.getString("ST_DATE_OF_CUR_SALARY").equals("")) {
                            empmastOrddate = CommonFunctions.getFormattedOutputDate1(rs.getDate("ST_DATE_OF_CUR_SALARY"));
                            if ((ordDate != null && !ordDate.equals("")) && (wefDate != null && !wefDate.equals(""))) {
                                ordCdate = (Date) formatter.parse(ordDate);
                                if (empmastOrddate != null && !empmastOrddate.equals("")) {
                                    empOrdDate = (Date) formatter.parse(empmastOrddate);
                                }
                                if (dt != null) {
                                    dateform = formatter.format(dt);
                                    sysDate = (Date) formatter.parse(dateform);
                                }
                                if (rs.getString("PAY_DATE") != null && !rs.getString("PAY_DATE").equals("")) {
                                    empmastWefDate = CommonFunctions.getFormattedOutputDate1(rs.getDate("PAY_DATE"));
                                    if (wefDate != null && !wefDate.equals("")) {
                                        wefCdate = (Date) formatter.parse(wefDate);
                                    }
                                    if (empmastWefDate != null && !empmastWefDate.equals("")) {
                                        empWefDatepayCdate = (Date) formatter.parse(empmastWefDate);
                                    }

                                    if (ordCdate.compareTo(empOrdDate) < 0 && wefCdate.compareTo(empWefDatepayCdate) < 0) {
                                        update = false; // both order date and wef date are smaller
                                    } else if (ordCdate.compareTo(empOrdDate) <= 0 && wefCdate.compareTo(empWefDatepayCdate) >= 0 && sysDate.compareTo(wefCdate) >= 0) {
                                        update = true; // order date is smaller and wef date is greater and system date is greater than wef date
                                    } else if (ordCdate.compareTo(empOrdDate) >= 0 && wefCdate.compareTo(empWefDatepayCdate) <= 0) {
                                        update = true; // order date is greater and wef date is smaller 
                                    } else if (ordCdate.compareTo(empOrdDate) >= 0 && wefCdate.compareTo(empWefDatepayCdate) >= 0) {
                                        //&& sysDate.compareTo(wefCdate)>=0
                                        update = true; // order date is greater and wef date is greater but not wef date is greater than sysdate
                                    }

                                } else {
                                    update = true;
                                }
                            } else {
                                update = false;
                            }
                        } else {
                            update = true;
                        }
                    } else {

                        update = true;
                    }
                } else if (updateType.equalsIgnoreCase("POSTING")) {
                    String empmastOrdDate = "";
                    String empmastWefdate = "";
                    rs = st.executeQuery("SELECT POST_ORDER_DATE, CURR_POST_DOJ FROM EMP_MAST WHERE EMP_ID='" + empId + "'");
                    if (rs.next()) {
                        if (rs.getString("CURR_POST_DOJ") != null && !rs.getString("CURR_POST_DOJ").equals("")) {
                            empmastOrdDate = CommonFunctions.getFormattedOutputDate1(rs.getDate("CURR_POST_DOJ"));
                            if ((ordDate != null && !ordDate.equals("")) && (wefDate != null && !wefDate.equals(""))) {
                                ordCdate = (Date) formatter.parse(ordDate);
                                if (empmastOrdDate != null && !empmastOrdDate.equals("")) {
                                    empOrdDate = (Date) formatter.parse(empmastOrdDate);
                                }
                                if (dt != null) {
                                    dateform = formatter.format(dt);
                                    sysDate = (Date) formatter.parse(dateform);
                                }
                                if (rs.getString("POST_ORDER_DATE") != null && !rs.getString("POST_ORDER_DATE").equals("")) {
                                    empmastWefdate = CommonFunctions.getFormattedOutputDate1(rs.getDate("POST_ORDER_DATE"));
                                    if (wefDate != null && !wefDate.equals("")) {
                                        wefCdate = (Date) formatter.parse(wefDate);
                                    }
                                    if (empmastWefdate != null && !empmastWefdate.equals("")) {
                                        empWefdate = (Date) formatter.parse(empmastWefdate);
                                    }

                                    if (ordCdate.compareTo(empOrdDate) < 0 && wefCdate.compareTo(empWefdate) < 0) {
                                        update = false; // both order date and wef date are smaller
                                    } else if (ordCdate.compareTo(empOrdDate) <= 0 && wefCdate.compareTo(empWefdate) >= 0 && sysDate.compareTo(wefCdate) >= 0) {
                                        update = true; // order date is smaller and wef date is greater and system date is greater than wef date
                                    } else if (ordCdate.compareTo(empOrdDate) >= 0 && wefCdate.compareTo(empWefdate) <= 0) {
                                        update = true; // order date is greater and wef date is smaller 
                                    } else if (ordCdate.compareTo(empOrdDate) >= 0 && wefCdate.compareTo(empWefdate) >= 0) {
                                        update = true; // order date is greater and wef date is greater but not wef date is greater than sysdate
                                    }
                                } else {
                                    update = true;
                                }
                            } else {
                                update = false;
                            }
                        } else {
                            update = true;
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, st);
        }
        return update;
    }

    public static void modifyEmpCurStatus(String empId, String deployStat, Connection con) throws SQLException {
        PreparedStatement pst = null;
        String deployCode = null;
        try {
            if (deployStat != null && deployStat.equalsIgnoreCase("TERMINATED")) {
                deployCode = "00";
            } else if (deployStat != null && deployStat.equalsIgnoreCase("ON DUTY")) {
                deployCode = "02";
            } else if (deployStat != null && deployStat.equalsIgnoreCase("ON TRAINING")) {
                deployCode = "04";
            } else if (deployStat != null && deployStat.equalsIgnoreCase("WAITING FOR POSTING")) {
                deployCode = "03";
            } else if (deployStat != null && deployStat.equalsIgnoreCase("ON DEPUTATION")) {
                deployCode = "07";
            } else if (deployStat != null && deployStat.equalsIgnoreCase("ON TRANSIT")) {
                deployCode = "06";
            } else if (deployStat != null && deployStat.equalsIgnoreCase("RESIGNED")) {
                deployCode = "08";
            } else if (deployStat != null && deployStat.equalsIgnoreCase("SUPERANNUATED")) {
                deployCode = "09";
            } else if (deployStat != null && deployStat.equalsIgnoreCase("UNDER SUSPENSION")) {
                deployCode = "05";
            }

            pst = con.prepareStatement("UPDATE emp_mast SET DEP_CODE=? WHERE EMP_ID=?");
            pst.setString(1, deployCode);
            pst.setString(2, empId);
            pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
        }
    }

    public static String getTreasuryName(Connection con, String trcode) {
        Statement trstmt = null;
        ResultSet trrs = null;
        String trname = "";
        try {
            trstmt = con.createStatement();
            String sql = "SELECT TR_NAME FROM G_TREASURY WHERE TR_CODE='" + trcode + "'";
            trrs = trstmt.executeQuery(sql);
            if (trrs.next()) {
                trname = trrs.getString("TR_NAME");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(trrs, trstmt);
        }
        return trname;
    }

    public static String getFormattedOutputDate4(Date dateInput) {
        if (dateInput != null) {
            DateFormat formatDate = new SimpleDateFormat("dd-MMM-yyyy");
            String outputDate = formatDate.format(dateInput);
            return outputDate.toUpperCase();
        } else {
            return "";
        }
    }

    public static String getFormattedInputDate(Date dateInput) {
        String inputDate = "";
        if (dateInput != null) {
            DateFormat formatDate = new SimpleDateFormat("dd-MMM-yyyy");
            inputDate = formatDate.format(dateInput).toUpperCase();
        }
        return inputDate;
    }

    public static String getMonthAsString(int month) {
        String monthString = null;
        switch (month) {
            case 0:
                monthString = "JAN";
                break;
            case 1:
                monthString = "FEB";
                break;
            case 2:
                monthString = "MAR";
                break;
            case 3:
                monthString = "APR";
                break;
            case 4:
                monthString = "MAY";
                break;
            case 5:
                monthString = "JUN";
                break;
            case 6:
                monthString = "JUL";
                break;
            case 7:
                monthString = "AUG";
                break;
            case 8:
                monthString = "SEP";
                break;
            case 9:
                monthString = "OCT";
                break;
            case 10:
                monthString = "NOV";
                break;
            case 11:
                monthString = "DEC";
                break;
        }
        return monthString;
    }

    public static String formatNumber(double d) {

        String pattern = null;
        String temp = new Double(d).toString();
        int indexOfDot = temp.indexOf(".");
        if (indexOfDot >= 0) {
            String str = temp.substring(indexOfDot + 1, temp.length());
            if (Integer.parseInt(str) == 0) {
                pattern = "0";
            } else {
                pattern = "0.00";
            }

        } else {
            pattern = "0.00";
        }
        NumberFormat formatter = new DecimalFormat(pattern);
        String s = formatter.format(d);
        return s;
    }

    public static String getOtherMaxSpcCode(String othType, Connection con) throws Exception {

        Statement stmt = null;
        ResultSet rs = null;

        String nextOthSpc = null;
        String setupDistrict = null;
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT * FROM G_SETUP");
            while (rs.next()) {
                setupDistrict = rs.getString("ACT_DIST");
            }
            DataBaseFunctions.closeSqlObjects(rs);

            if (setupDistrict != null) {
                if (othType.equalsIgnoreCase("GOI") || othType.equalsIgnoreCase("OSG") || othType.equalsIgnoreCase("FRB") || othType.equalsIgnoreCase("ORG")) {
                    rs = stmt.executeQuery("SELECT MAX(SUBSTR(OTH_SPC,4,23)) MAXSPC FROM G_OTH_SPC");
                    while (rs.next()) {
                        nextOthSpc = rs.getString("MAXSPC");
                    }
                    DataBaseFunctions.closeSqlObjects(rs);
                    if (nextOthSpc != null) {
                        nextOthSpc = othType.toUpperCase() + CommonFunctions.getNextString(nextOthSpc);
                    } else {
                        nextOthSpc = othType.toUpperCase() + setupDistrict.toUpperCase() + "0000000000000001";
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs);
        }
        return nextOthSpc;
    }

    public static String insertOthSpc(String othType, String deptName, String offName, String authName, Connection con) throws Exception {

        PreparedStatement pst = null;
        Statement st = null;

        boolean save = false;
        String mCode = "";

        try {
            System.out.println("othType is: " + othType);
            st = con.createStatement();
            pst = con.prepareStatement("INSERT INTO G_OTH_SPC (OTH_SPC,DEPT_NAME,OFF_EN,AUTH_NAME) VALUES(?,?,?,?)");
            mCode = getOtherMaxSpcCode(othType, con);
            pst.setString(1, mCode);
            if (deptName != null && !deptName.trim().equalsIgnoreCase("")) {
                pst.setString(2, deptName.toUpperCase());
            } else {
                pst.setString(2, null);
            }
            if (offName != null && !offName.trim().equalsIgnoreCase("")) {
                pst.setString(3, offName.toUpperCase());
            } else {
                pst.setString(3, null);
            }
            if (authName != null && !authName.trim().equalsIgnoreCase("")) {
                pst.setString(4, authName.toUpperCase());
            } else {
                pst.setString(4, null);
            }
            save = pst.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst, null, st);
        }
        return mCode;
    }

    public static boolean updateOthSpc(String othSpc, String authType, String deptName, String offName, String authName, Connection con) throws Exception {

        PreparedStatement pst = null;

        int retInteger = 0;
        boolean save = false;
        try {
            if (othSpc != null) {
                pst = con.prepareStatement("UPDATE G_OTH_SPC SET DEPT_NAME=?,OFF_EN=?,AUTH_NAME=?,OTH_SPC=? WHERE OTH_SPC='" + othSpc + "'");
                if (deptName != null && !deptName.trim().equalsIgnoreCase("")) {
                    pst.setString(1, deptName.toUpperCase());
                } else {
                    pst.setString(1, null);
                }
                if (offName != null && !offName.trim().equalsIgnoreCase("")) {
                    pst.setString(2, offName.toUpperCase());
                } else {
                    pst.setString(2, null);
                }
                if (authName != null && !authName.trim().equalsIgnoreCase("")) {
                    pst.setString(3, authName.toUpperCase());
                } else {
                    pst.setString(3, null);
                }
                if (othSpc.substring(0, 3).trim().equalsIgnoreCase(authType)) {
                    pst.setString(4, othSpc);
                } else {
                    pst.setString(4, (authType + othSpc.substring(3, othSpc.length())).toUpperCase());
                }
                retInteger = pst.executeUpdate();
            }
            if (retInteger > 0) {
                save = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
        }
        return save;
    }

    public static boolean deleteOthSpc(String othSpc, Connection con) throws Exception {

        PreparedStatement pst = null;

        int retInteger = 0;
        boolean delete = false;
        try {
            pst = con.prepareStatement("DELETE FROM G_OTH_SPC WHERE OTH_SPC='" + othSpc + "'");
            retInteger = pst.executeUpdate();
            if (retInteger > 0) {
                delete = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
        }
        return delete;
    }

    public synchronized static String getMaxNotIdCode(Connection con) throws Exception {
        String nCode = null;
        Statement st = null;
        ResultSet rs = null;
        String fixedId = "21910000000000";
        try {
            st = con.createStatement();
            rs = st.executeQuery("SELECT max(cast (not_id as bigint )) NCODE FROM emp_notification");
            if (rs.next()) {
                nCode = rs.getString("NCODE");
                //modified by Surendra for keep track of HRMS 2.0 transaction data//
                if (nCode != null) {
                    if (fixedId.compareTo(nCode) > 0) {
                        nCode = "21910000000001";
                    } else {
                        nCode = CommonFunctions.getNextString(nCode);
                    }
                } else {
                    // modified by Surendra for keep track of HRMS 2.0 transaction //
                    //String setupDistrict = getMaxSetupCode(con);
                    nCode = "21910000000001";
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (st != null) {
                st.close();
                st = null;
            }
        }
        nCode = getValidatedIdFromPool("EMP_NOTIFICATION", "NOT_ID", nCode);
        return nCode;
    }

    public static String getFormattedDate(String inputDate) {

        String formattedDate = "";

        DateFormat formatStringDate = new SimpleDateFormat("dd-MMM-yyyy");
        formattedDate = formatStringDate.format(inputDate);
        return formattedDate;
    }

    public static int getDaysInMonth(int date, int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, date);
        int days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        return days;
    }

    public static void updateEmpPostingInfoOnDate(String empId, String ddMMMyyyy, Connection con) throws Exception {

        JoiningData join = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        String subIdentity = "";
        String teriIdentity = "";
        String spcDeptCode = "";
        String spcOffCode = "";
        String spcGpc = "";
        String spcDDO = "";
        int retVal = 0;

        try {
            join = getJoinInfoOnDate(con, empId, ddMMMyyyy, "01");
            if (join != null && join.getJauth() != null && !join.getJauth().trim().equals("")) {
                pst = con.prepareStatement("SELECT GPC,DEPT_CODE,OFF_CODE,SUB_ID,TERRITORY FROM G_SPC WHERE SPC = ?");
                pst.setString(1, join.getJauth());
                rs = pst.executeQuery();
                if (rs.next()) {
                    spcGpc = rs.getString("GPC");
                    spcDeptCode = rs.getString("DEPT_CODE");
                    spcOffCode = rs.getString("OFF_CODE");
                    subIdentity = rs.getString("SUB_ID");
                    teriIdentity = rs.getString("TERRITORY");
                }

                if (spcOffCode != null && !spcOffCode.equals("")) {
                    if (!spcOffCode.substring(spcOffCode.length() - 3, spcOffCode.length()).equals("000")) {
                        spcDDO = spcOffCode.substring(0, spcOffCode.length() - 3) + "000";
                    } else {
                        spcDDO = spcOffCode;
                    }
                }
                /*pst = con.prepareStatement("UPDATE EMP_MAST SET POST_DATE=?,GEN_POST_NAME=?,SUB_IDENTITY=?,TERI_IDENTITY=?,POST_CLS_TYPE=?,"
                 + " APPOINTING_DEPT=?,CURR_POST_DOJ=?,CUR_OFF_CODE=?,HQ_OFF_CODE=?, CUR_SPC=? WHERE EMP_ID='" + empId + "'");*/
                pst = con.prepareStatement("UPDATE EMP_MAST SET GEN_POST_NAME=?,SUB_IDENTITY=?,TERI_IDENTITY=?,"
                        + " APPOINTING_DEPT=?,CURR_POST_DOJ=?,CUR_OFF_CODE=?,HQ_OFF_CODE=?, CUR_SPC=? WHERE EMP_ID=?");
                pst.setString(1, spcGpc);
                pst.setString(2, subIdentity);
                pst.setString(3, teriIdentity);
                pst.setString(4, spcDeptCode);
                if (join.getJoinon() != null && !join.getJoinon().equals("")) {
                    pst.setTimestamp(5, new Timestamp(new SimpleDateFormat("dd-MMM-yyyy").parse(join.getJoinon()).getTime()));
                } else {
                    pst.setTimestamp(5, null);
                }
                pst.setString(6, spcDDO);
                pst.setString(7, spcOffCode);
                pst.setString(8, join.getJauth());
                pst.setString(9, empId);
                retVal = pst.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs);
            DataBaseFunctions.closeSqlObjects(pst);
        }
    }

    public static JoiningData getJoinInfoOnDate(Connection con, String empId, String ddMMMyyyy, String toe) throws Exception {

        PreparedStatement pst = null;
        ResultSet rsRecset = null;
        JoiningData join = new JoiningData();
        try {
            pst = con.prepareStatement("SELECT SPC,NOT_ID,JOIN_DATE FROM EMP_JOIN WHERE NOT_TYPE!='ADDITIONAL_CHARGE' AND if_ad_charge='N' and emp_id='" + empId + "' and join_date<='" + ddMMMyyyy + "' and concat(doe,toe)<='" + ddMMMyyyy + toe + "' and join_date=(SELECT max(join_date) FROM emp_join WHERE NOT_TYPE!='ADDITIONAL_CHARGE' AND if_ad_charge='N' and emp_id='" + empId + "' and join_date<='" + ddMMMyyyy + "' and concat(doe,toe)<='" + ddMMMyyyy + toe + "') and join_id not in (select join_id from emp_relieve where concat(doe,toe)<='" + ddMMMyyyy + toe + "' and emp_id='" + empId + "' AND JOIN_ID != '')");
            rsRecset = pst.executeQuery();
            if (rsRecset.next()) {
                join.setNotificationid(rsRecset.getString("NOT_ID"));
                join.setJauth(rsRecset.getString("SPC"));
                join.setJoinon(rsRecset.getString("JOIN_DATE"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rsRecset);
        }
        return join;
    }

    public static String getPostName(Connection con, String postcode) {

        PreparedStatement pst = null;
        ResultSet rs = null;

        String postname = "";
        try {
            String sql = "SELECT POST FROM G_POST WHERE POST_CODE=?";
            pst = con.prepareStatement(sql);
            pst.setString(1, postcode);
            rs = pst.executeQuery();
            if (rs.next()) {
                postname = rs.getString("POST");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
        }
        return postname;
    }

    public static String[] getRupessAndPaise(String amount) {

        String[] st = StringUtils.split(amount, ".");
        String[] outputString = new String[2];
        if (st.length > 1) {
            String s2 = st[1];
            outputString[0] = st[0];
            if (s2.length() == 1) {
                st[1] = "0" + st[1];
            }
            if (s2.length() > 1) {
                st[1] = st[1];
            }
            outputString[1] = st[1];
        } else {
            outputString[0] = st[0];
            outputString[1] = "00";
        }
        return outputString;
    }

    public static String getFinancialYear(String date) {
        String financialYear = null;
        String[] dateParts = date.split("-");
        String year = dateParts[0];
        String month = dateParts[1];
        if (Integer.parseInt(month) > 3) {
            financialYear = year + "-" + (Integer.parseInt(year) + 1);
        } else {
            financialYear = (Integer.parseInt(year) - 1) + "-" + year;
        }
        return financialYear;
    }

    public static String convertNumber(int number) {
        String wordStr = "";
        if (number > 0) {
            String num = String.valueOf(number);
            int tenplace = 0;
            int hundredplace = 0;
            int thousandplace = 0;
            int lakhplace = 0;
            int croreplace = 0;

            if (number < 20) {
                if (number == 0) {
                    wordStr = "zero";
                } else {
                    wordStr = unitPlace[number];
                }
            } else {
                if (number < 100) {
                    if (number < 20) {
                        wordStr = wordStr + unitPlace[number];
                    } else {
                        tenplace = number / 10;
                        wordStr = wordStr + tensPlace[tenplace];
                        if ((number - tenplace * 10) > 0) {
                            wordStr = wordStr + unitPlace[(number - tenplace * 10)];
                        }
                    }
                } else if (number >= 100 && number < 1000) {
                    hundredplace = number / 100;
                    if (hundredplace > 0) {
                        wordStr = unitPlace[hundredplace] + " hundred ";
                    }
                    number = number - hundredplace * 100;
                    if (number < 20) {
                        wordStr = wordStr + unitPlace[number];
                    } else {
                        tenplace = number / 10;
                        wordStr = wordStr + tensPlace[tenplace];
                        if ((number - tenplace * 10) > 0) {
                            wordStr = wordStr + unitPlace[(number - tenplace * 10)];
                        }
                    }
                } else if (number >= 1000 && number < 100000) {
                    thousandplace = number / 1000;
                    if (thousandplace > 0) {
                        wordStr = convertNumber(thousandplace) + " thousand ";
                    }

                    number = number - thousandplace * 1000;
                    hundredplace = number / 100;
                    if (hundredplace > 0) {
                        wordStr = wordStr + unitPlace[hundredplace] + " hundred ";
                    }
                    number = number - hundredplace * 100;
                    if (number < 20) {
                        wordStr = wordStr + unitPlace[number];
                    } else {
                        tenplace = number / 10;
                        wordStr = wordStr + tensPlace[tenplace];
                        if ((number - tenplace * 10) > 0) {
                            wordStr = wordStr + unitPlace[(number - tenplace * 10)];
                        }
                    }
                } else if (number >= 100000 && number < 10000000) {
                    lakhplace = number / 100000;
                    if (lakhplace > 0) {
                        wordStr = convertNumber(lakhplace) + " lakh ";
                    }
                    number = number - lakhplace * 100000;
                    thousandplace = number / 1000;
                    if (thousandplace > 0) {
                        wordStr = wordStr + convertNumber(thousandplace) + " thousand ";
                    }
                    number = number - thousandplace * 1000;
                    hundredplace = number / 100;
                    if (hundredplace > 0) {
                        wordStr = wordStr + unitPlace[hundredplace] + " hundred ";
                    }
                    number = number - hundredplace * 100;
                    if (number < 20) {
                        wordStr = wordStr + unitPlace[number];
                    } else {
                        tenplace = number / 10;
                        wordStr = wordStr + tensPlace[tenplace];
                        if ((number - tenplace * 10) > 0) {
                            wordStr = wordStr + unitPlace[(number - tenplace * 10)];
                        }
                    }
                } else if (number > 10000000) {
                    croreplace = number / 10000000;
                    if (croreplace > 0) {
                        wordStr = convertNumber(croreplace) + " crore ";
                    }
                    number = number - croreplace * 10000000;
                    lakhplace = number / 100000;
                    if (lakhplace > 0) {
                        wordStr = wordStr + convertNumber(lakhplace) + " lakh ";
                    }
                    number = number - lakhplace * 100000;
                    thousandplace = number / 1000;
                    if (thousandplace > 0) {
                        wordStr = wordStr + convertNumber(thousandplace) + " thousand ";
                    }
                    number = number - thousandplace * 1000;
                    hundredplace = number / 100;
                    if (hundredplace > 0) {
                        wordStr = wordStr + unitPlace[hundredplace] + " hundred ";
                    }
                    number = number - hundredplace * 100;
                    if (number < 20) {
                        wordStr = wordStr + unitPlace[number];
                    } else {
                        tenplace = number / 10;
                        wordStr = wordStr + tensPlace[tenplace];
                        if ((number - tenplace * 10) > 0) {
                            wordStr = wordStr + unitPlace[(number - tenplace * 10)];
                        }
                    }

                }

            }
        }
        return wordStr;
    }
    
    private static Connection getDBConnection(){
        Connection con = null;
        
        try {
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection("jdbc:postgresql://172.16.1.16/hrmis", "hrmis2", "cmgi");
        }catch (Exception e) {
            e.printStackTrace();
        }
       return con; 
    }
}
