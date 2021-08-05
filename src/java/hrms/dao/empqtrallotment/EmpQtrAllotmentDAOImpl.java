/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.empqtrallotment;

import hrms.common.DataBaseFunctions;
import hrms.model.employee.QuaterAllotment;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 *
 * @author Manas
 */
public class EmpQtrAllotmentDAOImpl implements EmpQtrAllotmentDAO {

    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public QuaterAllotment[] getQuaterAllotmentDetail(String empId) {
        QuaterAllotment qtrAllot = null;
        Connection con = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;
        ResultSet res = null;
        ResultSet res1 = null;
        ArrayList<QuaterAllotment> list = new ArrayList<>();
        try {
            con = dataSource.getConnection();

            String isGetHRA = "N";
            String isQNotAvailed = "N";
            /*Fixed Data HRR*/
            String qadCode = null;
            String qadDesc = null;
            String qadcodename = null;
            String qadtype = null;
            String qalunit = null;
            String qdedtype = null;
            String qschedule = null;
            int qrepCol = 0;
            int qrowNo = 0;
            /*Fixed Data HRR*/
            /*Fixed Data WRR*/
            String wadCode = null;
            String wadDesc = null;
            String wadcodename = null;
            String wadtype = null;
            String walunit = null;
            String wdedtype = null;
            String wschedule = null;
            int wrepCol = 0;
            int wrowNo = 0;
            String wbtid = null;
            /*Fixed Data WRR*/

            /*Fixed Data SWR*/
            String sadCode = null;
            String sadDesc = null;
            String sadcodename = null;
            String sadtype = null;
            String salunit = null;
            String sdedtype = null;
            String sschedule = null;
            int srepCol = 0;
            int srowNo = 0;
            String sbtid = null;
            /*Fixed Data SWR*/

            /*HRR*/
            pstmt = con.prepareStatement("SELECT AD_CODE,AD_DESC,AD_CODE_NAME,AD_TYPE,ALT_UNIT,DED_TYPE,SCHEDULE,REP_COL,ROW_NO FROM G_AD_LIST WHERE AD_TYPE='D' AND SCHEDULE = 'HRR'");
            res = pstmt.executeQuery();
            if (res.next()) {
                qadCode = res.getString("AD_CODE");
                qadDesc = res.getString("AD_DESC");
                qadcodename = res.getString("AD_CODE_NAME");
                qadtype = res.getString("AD_TYPE");
                qalunit = res.getString("ALT_UNIT");
                qdedtype = res.getString("DED_TYPE");
                qschedule = res.getString("SCHEDULE");
                qrepCol = res.getInt("REP_COL");
                qrowNo = res.getInt("ROW_NO");
            }
            /*WRR*/
            pstmt = con.prepareStatement("SELECT AD_CODE,AD_DESC,AD_CODE_NAME,AD_TYPE,ALT_UNIT,DED_TYPE,SCHEDULE,REP_COL,ROW_NO,BT_ID  FROM G_AD_LIST WHERE AD_TYPE='D' AND SCHEDULE = 'WRR'");
            res = pstmt.executeQuery();
            if (res.next()) {
                wadCode = res.getString("AD_CODE");
                wadDesc = res.getString("AD_DESC");
                wadcodename = res.getString("AD_CODE_NAME");
                wadtype = res.getString("AD_TYPE");
                walunit = res.getString("ALT_UNIT");
                wdedtype = res.getString("DED_TYPE");
                wschedule = res.getString("SCHEDULE");
                wrepCol = res.getInt("REP_COL");
                wrowNo = res.getInt("ROW_NO");
                wbtid = res.getString("BT_ID");
            }
            /*SWR*/
            qtrAllot = new QuaterAllotment();
            pstmt = con.prepareStatement("SELECT AD_CODE,AD_DESC,AD_CODE_NAME,AD_TYPE,ALT_UNIT,DED_TYPE,SCHEDULE,REP_COL,ROW_NO,BT_ID  FROM G_AD_LIST WHERE AD_TYPE='D' AND SCHEDULE = 'SWR'");
            res = pstmt.executeQuery();
            if (res.next()) {
                sadCode = res.getString("AD_CODE");
                sadDesc = res.getString("AD_DESC");
                sadcodename = res.getString("AD_CODE_NAME");
                sadtype = res.getString("AD_TYPE");
                salunit = res.getString("ALT_UNIT");
                sdedtype = res.getString("DED_TYPE");
                sschedule = res.getString("SCHEDULE");
                srepCol = res.getInt("REP_COL");
                srowNo = res.getInt("ROW_NO");
                sbtid = res.getString("BT_ID");
            }

            pstmt = con.prepareStatement("SELECT QS_ID,QUARTER_RENT,WATER_RENT,SEWERAGE_RENT,BT_ID,IS_GET_HRA,IF_SURRENDERED,EMP_QTR_ALLOT.QA_ID FROM "
                    + "                    (SELECT * FROM EMP_QTR_ALLOT WHERE EMP_QTR_ALLOT.EMP_ID = ? and ALLOTMENT_DATE IS NOT NULL AND (if_surrendered = 'N' OR if_surrendered IS NULL OR if_surrendered = ''))EMP_QTR_ALLOT "
                    + "                    LEFT OUTER JOIN EMP_QTR_SURRENDER ON EMP_QTR_ALLOT.QA_ID = EMP_QTR_SURRENDER.QA_ID LEFT OUTER JOIN G_QTR_POOL ON EMP_QTR_ALLOT.Q_ID = G_QTR_POOL.Q_ID");
            pstmt.setString(1, empId);
            //pstmt.setString(2, empId);
            res = pstmt.executeQuery();
            boolean insideLoop = false;
            while (res.next()) {
                int hrr = 0;
                int wrr = 0;
                int swr = 0;
                int qaid = 0;
                qtrAllot = new QuaterAllotment();
                /**/
                
                qtrAllot.setAdcode(qadCode);
                qtrAllot.setAddesc(qadDesc);
                qtrAllot.setAdcodename(qadcodename);
                qtrAllot.setAdtype(qadtype);
                qtrAllot.setAlunit(qalunit);
                qtrAllot.setDedtype(qdedtype);
                qtrAllot.setSchedule(qschedule);
                qtrAllot.setRepcol(qrepCol);
                qtrAllot.setRowno(qrowNo);
                
                /**/
                insideLoop = true;
                isQNotAvailed = res.getString("IF_SURRENDERED");//IF SURRENDER IS Y MEANS HE IS NOT AVAILED THE QUATER
                qtrAllot.setQsid(res.getInt("QS_ID"));
                if (isQNotAvailed != null && isQNotAvailed.equals("Y")) {
                    qtrAllot.setAmt(0);
                } else {
                    qtrAllot.setAmt(res.getInt("QUARTER_RENT"));
                    wrr = res.getInt("WATER_RENT");
                    swr = res.getInt("SEWERAGE_RENT");
                    qaid = res.getInt("QA_ID");
                    System.out.println("hrr:" + qtrAllot.getAmt() + " wrr:" + wrr + " swr:" + swr);
                    isQNotAvailed = "N";
                }
                
                qtrAllot.setQaid(qaid);
                qtrAllot.setBtid(res.getString("BT_ID"));
                qtrAllot.setIsgethra(res.getString("IS_GET_HRA"));
                isGetHRA = qtrAllot.getIsgethra();
                list.add(qtrAllot);

                /*WRR*/
                qtrAllot = new QuaterAllotment();
                qtrAllot.setAdcode(wadCode);
                qtrAllot.setAddesc(wadDesc);
                qtrAllot.setAdcodename(wadcodename);
                qtrAllot.setAdtype(wadtype);
                qtrAllot.setAlunit(walunit);
                qtrAllot.setDedtype(wdedtype);
                qtrAllot.setSchedule(wschedule);
                qtrAllot.setRepcol(wrepCol);
                qtrAllot.setRowno(wrowNo);
                qtrAllot.setBtid(wbtid);
                qtrAllot.setAmt(wrr);
                qtrAllot.setQaid(qaid);
                list.add(qtrAllot);

                /*SWR*/
                qtrAllot = new QuaterAllotment();
                qtrAllot.setAdcode(sadCode);
                qtrAllot.setAddesc(sadDesc);
                qtrAllot.setAdcodename(sadcodename);
                qtrAllot.setAdtype(sadtype);
                qtrAllot.setAlunit(salunit);
                qtrAllot.setDedtype(sdedtype);
                qtrAllot.setSchedule(sschedule);
                qtrAllot.setRepcol(srepCol);
                qtrAllot.setRowno(srowNo);
                qtrAllot.setBtid(sbtid);
                qtrAllot.setAmt(swr);
                qtrAllot.setQaid(qaid);
                list.add(qtrAllot);
            }
            if (insideLoop == false) {
                isQNotAvailed = "Y";
            }

            //qtrData[0] = qtrAllot;
            /*HRR*/
            /*HRA*/
            qtrAllot = new QuaterAllotment();
            pstmt = con.prepareStatement("SELECT G_AD_LIST.AD_CODE,AD_DESC,AD_CODE_NAME,AD_TYPE,ALT_UNIT,DED_TYPE,SCHEDULE,REP_COL,ROW_NO,OBJECT_HEAD,UPDATE_AD_INFO.FIXEDVALUE,UPDATE_AD_INFO.IS_FIXED FROM G_AD_LIST "
                    + "LEFT OUTER JOIN (SELECT REF_AD_CODE,FIXEDVALUE,IS_FIXED FROM UPDATE_AD_INFO WHERE UPDATION_REF_CODE = ? ) AS UPDATE_AD_INFO ON G_AD_LIST.AD_CODE = UPDATE_AD_INFO.REF_AD_CODE "
                    + "WHERE AD_TYPE='A' AND AD_CODE_NAME = 'HRA'");
            pstmt.setString(1, empId);
            res = pstmt.executeQuery();
            if (res.next()) {
                qtrAllot.setAdcode(res.getString("AD_CODE"));
                qtrAllot.setAddesc(res.getString("AD_DESC"));
                qtrAllot.setAdcodename(res.getString("AD_CODE_NAME"));
                qtrAllot.setAdtype(res.getString("AD_TYPE"));
                qtrAllot.setAlunit(res.getString("ALT_UNIT"));
                qtrAllot.setDedtype(res.getString("DED_TYPE"));
                qtrAllot.setSchedule(res.getString("SCHEDULE"));
                qtrAllot.setRepcol(res.getInt("REP_COL"));
                qtrAllot.setRowno(res.getInt("ROW_NO"));
                qtrAllot.setBtid(res.getString("OBJECT_HEAD"));
                System.out.println("isQAvailed:" + isQNotAvailed + " isGetHRA:" + isGetHRA + "   empId:" + empId);
                if (isQNotAvailed == null || isQNotAvailed.equals("Y")) {
                    int hra = 0;
                    if (res.getInt("IS_FIXED") == 1) {
                        hra = res.getInt("FIXEDVALUE");
                    } else {
                        hra = getPrevHRA(empId);
                    }
                    qtrAllot.setAmt(hra);
                } else {
                    if (isGetHRA.equalsIgnoreCase("Y")) {
                        int hra = 0;
                        if (res.getInt("IS_FIXED") == 1) {
                            hra = res.getInt("FIXEDVALUE");
                        } else {
                            hra = getPrevHRA(empId);
                        }
                        qtrAllot.setAmt(hra);
                    } else {
                        qtrAllot.setAmt(0);
                    }
                }
                /*
                 if (isGetHRA == null || isGetHRA.equalsIgnoreCase("N")) {
                 qtrAllot.setAmt(0);
                 } else if (isGetHRA.equalsIgnoreCase("Y")) {
                 qtrAllot.setAmt(getPrevHRA(empId));
                 }
                 */
            }
            list.add(qtrAllot);
            //qtrData[3] = qtrAllot;
            /*HRA*/

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(res, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        System.out.println("size:" + list.size());
        QuaterAllotment qtrData[] = list.toArray(new QuaterAllotment[list.size()]);
        return qtrData;
    }
    
    public int getPrevHRA(String empId) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet res = null;
        int hra = 0;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT sum(AD_AMT) AS AD_AMT FROM HRMIS.AQ_DTLS1 INNER JOIN "
                    + "(SELECT AQSL_NO FROM AQ_MAST WHERE AQ_MONTH=7 AND AQ_YEAR=2017 AND EMP_CODE=?) AQ_MAST ON  HRMIS.AQ_DTLS1.AQSL_NO=AQ_MAST.AQSL_NO "
                    + " AND AD_CODE='HRA'");
            pstmt.setString(1, empId);
            res = pstmt.executeQuery();
            if (res.next()) {
                hra = res.getInt("AD_AMT");
            }

            if (hra == 0) {
                pstmt = con.prepareStatement("SELECT sum(AD_AMT) AS AD_AMT FROM HRMIS.AQ_DTLS1 INNER JOIN "
                        + "(SELECT AQSL_NO FROM AQ_MAST WHERE AQ_MONTH=1 AND AQ_YEAR=2018 AND EMP_CODE=?) AQ_MAST ON  HRMIS.AQ_DTLS1.AQSL_NO=AQ_MAST.AQSL_NO "
                        + " AND AD_CODE='HRA'");
                pstmt.setString(1, empId);
                res = pstmt.executeQuery();
                if (res.next()) {
                    hra = res.getInt("AD_AMT");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(res, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return hra;
    }
}
