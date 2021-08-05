package hrms.dao.payroll.tpschedule;

import hrms.common.DataBaseFunctions;
import hrms.common.Numtowordconvertion;
import hrms.common.PayrollCommonFunction;
import hrms.model.common.CommonReportParamBean;
import hrms.model.payroll.tpfschedule.TPFEmployeeScheduleBean;
import hrms.model.payroll.tpfschedule.TPFScheduleBean;
import hrms.model.payroll.tpfschedule.TpfTypeBean;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;

public class TPFScheduleDAOImpl implements TPFScheduleDAO {

    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List getEmployeeWiseTPFList(String billNo) {

        Connection con = null;

        Statement stmt = null;
        ResultSet rs = null;

        ArrayList tpfList = new ArrayList();

        TpfTypeBean tpfBean = null;

        try {
            con = dataSource.getConnection();

            stmt = con.createStatement();
            String sql = "SELECT GPF_TYPE FROM AQ_MAST WHERE BILL_NO ='" + billNo + "' AND ACCT_TYPE='TPF' AND GPF_TYPE IS NOT NULL GROUP BY GPF_TYPE ORDER BY GPF_TYPE";
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                tpfBean = new TpfTypeBean();
                tpfBean.setGpfType(rs.getString("GPF_TYPE"));
                tpfBean.setEmpGpfList(getEmpTPFDetails(tpfBean.getGpfType(), billNo, con));
                tpfBean.setEmpno(tpfBean.getEmpGpfList().size());
                tpfList.add(tpfBean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs,stmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return tpfList;
    }

    public ArrayList getEmpTPFDetails(String gpfType, String billNo, Connection con) throws SQLException {

        Statement stmt = null;
        ResultSet rs = null;
        Statement stmt1 = null;
        ResultSet rs1 = null;
        Statement stmt2 = null;
        ResultSet rs2 = null;
        Statement stmt3 = null;
        ResultSet rs3 = null;

        TPFEmployeeScheduleBean empSchBean = null;
        ArrayList empGpfList = new ArrayList();

        String noofinst = "";
        int releasedAmount = 0;
        int total = 0;
        String dob = null;
        String dob1 = null;
        String dob2 = null;
        String dob3 = null;
        String dob4 = null;
        String dos = null;
        String dos1 = null;
        String dos2 = null;
        String dos3 = null;
        String dos4 = null;
        String doe = null;
        String doe1 = null;
        String doe2 = null;
        String doe3 = null;
        String doe4 = null;

        String aqdtlsTblName = getAqDtlsTableName(billNo);
        try {
            stmt = con.createStatement();

            /*String query = "SELECT EMP_MAST.GPF_NO,EMP_MAST.DOE_GOV,EMP_MAST.DOB,EMP_MAST.DOS,AQ_MAST.EMP_CODE,AQ_MAST.EMP_NAME,AQ_MAST.CUR_DESG,AQ_MAST.BANK_ACC_NO,AQ_MAST.CUR_BASIC,AQ_MAST.PAY_SCALE,AQ_MAST.AQSL_NO,POST_SL_NO "
                    + "from (SELECT EMP_CODE,EMP_NAME,CUR_DESG,BANK_ACC_NO,CUR_BASIC,PAY_SCALE,AQSL_NO,POST_SL_NO from AQ_MAST WHERE GPF_TYPE = '" + gpfType + "' AND BILL_NO = '" + billNo + "' AND ACCT_TYPE='TPF') AQ_MAST left outer join EMP_MAST on AQ_MAST.EMP_CODE = EMP_MAST.EMP_ID ORDER BY SUBSTR(EMP_MAST.GPF_NO,LENGTH(getgpfseries(EMP_MAST.GPF_NO))+1)";*/
            String query = "SELECT * FROM (" +
                           " SELECT GPF_ACC_NO,regexp_replace(EMP_MAST.GPF_NO, '\\d', '', 'g') SERIES,EMP_MAST.DOE_GOV,EMP_MAST.DOB,EMP_MAST.DOS,AQ_MAST.EMP_CODE,AQ_MAST.EMP_NAME," +
                           " AQ_MAST.CUR_DESG,AQ_MAST.BANK_ACC_NO,AQ_MAST.CUR_BASIC,AQ_MAST.PAY_SCALE,AQ_MAST.AQSL_NO,POST_SL_NO" +
                           "  from (SELECT EMP_CODE,EMP_NAME,CUR_DESG,BANK_ACC_NO,CUR_BASIC,PAY_SCALE,AQSL_NO,POST_SL_NO,GPF_ACC_NO from AQ_MAST WHERE GPF_TYPE = '" + gpfType + "' AND BILL_NO = '" + billNo + "' AND ACCT_TYPE='TPF') AQ_MAST" +
                           "  left outer join EMP_MAST on AQ_MAST.EMP_CODE =EMP_MAST.EMP_ID ) TPF_DETAILS ORDER BY SERIES,EMP_MAST.F_NAME";
            //System.out.println("Query is: "+query);
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                empSchBean = new TPFEmployeeScheduleBean();
                if (rs.getString("EMP_NAME") != null && !rs.getString("EMP_NAME").equals("")) {

                    empSchBean.setName(rs.getString("EMP_NAME"));
                    empSchBean.setDesignation(rs.getString("CUR_DESG"));
                    empSchBean.setAccNo(rs.getString("GPF_ACC_NO"));
                    empSchBean.setBasicPay(rs.getString("CUR_BASIC"));
                    empSchBean.setScaleOfPay(rs.getString("PAY_SCALE"));
                    noofinst = getNoOfInst(rs.getString("AQSL_NO"), aqdtlsTblName, con);
                    empSchBean.setNoOfInst(noofinst);
                    if (rs.getString("DOE_GOV") != null && !rs.getString("DOE_GOV").trim().equals("")) {
                        doe = rs.getString("DOE_GOV");
                        doe1 = doe.substring(0, 4);
                        doe2 = doe.substring(5, 7);
                        doe3 = doe.substring(8, 10);
                        doe4 = doe3 + "/" + doe2 + "/" + doe1;
                        empSchBean.setDateOfEntry(doe4);
                    }
                    if (rs.getString("DOB") != null && !rs.getString("DOB").trim().equals("")) {
                        dob = rs.getString("DOB");

                        dob1 = dob.substring(0, 4);
                        dob2 = dob.substring(5, 7);
                        dob3 = dob.substring(8, 10);
                        dob4 = dob3 + "/" + dob2 + "/" + dob1;
                        empSchBean.setDob(dob4);
                    }
                    if (rs.getString("DOS") != null && !rs.getString("DOS").trim().equals("")) {
                        dos = rs.getString("DOS");
                        dos1 = dos.substring(0, 4);
                        dos2 = dos.substring(5, 7);
                        dos3 = dos.substring(8, 10);
                        dos4 = dos3 + "/" + dos2 + "/" + dos1;
                        empSchBean.setDor(dos4);
                    }
                    
                    stmt1 = con.createStatement();
                    query = "SELECT " + aqdtlsTblName + ".AD_AMT MONTHLYSUB FROM " + aqdtlsTblName + " WHERE " + aqdtlsTblName + ".AD_TYPE = 'D' AND EMP_CODE='" + rs.getString("EMP_CODE") + "' AND " + aqdtlsTblName + ".DED_TYPE = 'S' AND " + aqdtlsTblName + ".SCHEDULE = 'TPF' AND AQSL_NO = '" + rs.getString("AQSL_NO") + "'";
                    rs1 = stmt1.executeQuery(query);
                    if (rs1.next()) {
                        empSchBean.setMonthlySub(rs1.getInt("MONTHLYSUB"));
                    }
                    
                    stmt2 = con.createStatement();
                    query = "SELECT " + aqdtlsTblName + ".AD_AMT TOWARDSLOAN FROM " + aqdtlsTblName + " WHERE " + aqdtlsTblName + ".AD_TYPE = 'D' AND EMP_CODE='" + rs.getString("EMP_CODE") + "' AND " + aqdtlsTblName + ".DED_TYPE = 'L' AND " + aqdtlsTblName + ".SCHEDULE = 'TPFGA' AND AQSL_NO = '" + rs.getString("AQSL_NO") + "'";
                    rs2 = stmt2.executeQuery(query);
                    if (rs2.next()) {
                        empSchBean.setTowardsLoan(rs2.getInt("TOWARDSLOAN"));
                    }
                    
                    stmt3 = con.createStatement();
                    query = "SELECT SUM(" + aqdtlsTblName + ".AD_AMT) TOWARDSOTHER FROM " + aqdtlsTblName + " WHERE " + aqdtlsTblName + ".AD_TYPE = 'D' AND EMP_CODE='" + rs.getString("EMP_CODE") + "' AND (" + aqdtlsTblName + ".AD_CODE = 'GPDD' OR " + aqdtlsTblName + ".AD_CODE = 'GPIR') AND AQSL_NO = '" + rs.getString("AQSL_NO") + "'";
                    rs3 = stmt3.executeQuery(query);
                    if (rs3.next()) {
                        empSchBean.setOtherdeposits(rs3.getInt("TOWARDSOTHER"));
                    }

                    releasedAmount = empSchBean.getMonthlySub() + empSchBean.getTowardsLoan() + empSchBean.getOtherdeposits();

                    empSchBean.setTotalReleased(releasedAmount);

                    total += releasedAmount;

                    empSchBean.setCarryForward(total + "");
                    //if (total > 0) {
                        empGpfList.add(empSchBean);
                    //}
                }
                if (total > 0) {
                    empSchBean.setAmountInWords(Numtowordconvertion.convertNumber((int) total).toUpperCase());
                }
            }
            //System.out.println("Total is: " + total);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs1,rs2,rs3);
            DataBaseFunctions.closeSqlObjects(rs, stmt);
        }
        return empGpfList;
    }

    public static String getNoOfInst(String aqslno, String aqdtlsTblName, Connection con) throws Exception {

        Statement stmt = null;
        ResultSet rs = null;

        String inst = "";
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT REF_DESC FROM " + aqdtlsTblName + " WHERE AQSL_NO='" + aqslno + "' AND SCHEDULE='TPFGA'");
            if (rs.next()) {
                inst = rs.getString("REF_DESC");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, stmt);
        }
        return inst;
    }

    public String getAqDtlsTableName(String billNo) {

        Connection con = null;

        Statement stmt = null;
        ResultSet res = null;

        String aqDTLS = "AQ_DTLS";
        try {
            con = dataSource.getConnection();

            stmt = con.createStatement();
            res = stmt.executeQuery("SELECT aq_month,aq_year FROM BILL_MAST WHERE bill_no=" + billNo);
            int aqMonth = 0;
            int aqYear = 0;
            if (res.next()) {
                aqMonth = res.getInt("aq_month");
                aqYear = res.getInt("aq_year");
            }

            if ((aqYear == 2017 && aqMonth < 2) || aqYear < 2017) {
                aqDTLS = "hrmis.AQ_DTLS1";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(res, stmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return aqDTLS;
    }

    @Override
    public List getTPFAbstract(String billNo) {

        Connection con = null;

        Statement stmt = null;
        ResultSet rs = null;

        ArrayList tpfabstractList = new ArrayList();

        TPFScheduleBean tpfTypeBean = null;

        double sal = 0.0;
        String empCode = "";
        double amt = 0.0;
        double amt1 = 0.0;
        String tot = null;
        String tot1 = null;

        String aqdtlsTblName = getAqDtlsTableName(billNo);
        try {
            con = dataSource.getConnection();

            stmt = con.createStatement();
            String sql = "SELECT GPF_TYPE,SUM(AD_AMT) AMT FROM (SELECT AQ_MAST.AQSL_NO,GPF_TYPES.GPF_TYPE,AD_AMT FROM (SELECT AQ_MAST.AQSL_NO,GPF_TYPE FROM AQ_MAST where BILL_NO = '" + billNo + "')AQ_MAST"
                    + " INNER JOIN (SELECT GPF_TYPE from AQ_MAST where BILL_NO = '" + billNo + "' group by GPF_TYPE) GPF_TYPES ON GPF_TYPES.GPF_TYPE = AQ_MAST.GPF_TYPE"
                    + " INNER JOIN (SELECT AQSL_NO,AD_AMT FROM " + aqdtlsTblName + " WHERE (AD_CODE = 'TPF' and DED_TYPE ='S') or (AD_CODE='TPFGA' AND DED_TYPE='L') or (AD_CODE='GPDD' AND DED_TYPE='S') or (AD_CODE='GPIR' AND DED_TYPE='S'))AQ_DTLS ON AQ_MAST.AQSL_NO = AQ_DTLS.AQSL_NO)TEMP GROUP BY GPF_TYPE ORDER BY GPF_TYPE";
            //System.out.println("SQL is: "+sql);
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                tpfTypeBean = new TPFScheduleBean();
                tpfTypeBean.setPfcode(rs.getString("GPF_TYPE"));
                tpfTypeBean.setTotalamt(rs.getString("AMT"));
                sal = rs.getDouble("AMT");
                amt = amt + sal;
                amt1 = amt + 1;
                tpfTypeBean.setCarryForward((int) amt + "");
                tpfTypeBean.setCarryForward1((int) amt1 + "");
                tot = Numtowordconvertion.convertNumber((int) amt);
                tpfTypeBean.setAmountInWords(tot.toUpperCase());
                tot1 = Numtowordconvertion.convertNumber((int) amt1);
                tpfTypeBean.setAmountInWords1(tot1.toUpperCase());
                if (sal > 0) {
                    tpfabstractList.add(tpfTypeBean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs,stmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return tpfabstractList;
    }

    @Override
    public CommonReportParamBean getCommonReportParameter(String billNo) {

        Connection con = null;

        CommonReportParamBean commonBean = new CommonReportParamBean();
        
        PayrollCommonFunction prcf = new PayrollCommonFunction();
        try {
            con = dataSource.getConnection();
            
            commonBean = prcf.getCommonReportParameter(con, billNo);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
      return commonBean;  
    }
}
