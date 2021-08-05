/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.payroll.schedule;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import hrms.SelectOption;
import hrms.common.CalendarCommonMethods;
import static hrms.common.CalendarCommonMethods.getMonthAsString;
import hrms.common.CommonFunctions;
import hrms.common.DataBaseFunctions;
import hrms.common.Numtowordconvertion;
import hrms.common.PayrollCommonFunction;
import hrms.model.common.CommonReportParamBean;
import hrms.model.payroll.QuaterRent;
import hrms.model.payroll.aqreport.AqFunctionalities;
import hrms.model.payroll.aqreport.BillAmtDetails;
import hrms.model.payroll.billbrowser.AllowDeductDetails;
import hrms.model.payroll.billbrowser.AquitanceDataAGBean;
import hrms.model.payroll.billbrowser.BillBean;
import hrms.model.payroll.billbrowser.BillChartOfAccount;
import hrms.model.payroll.billbrowser.MajorHeadAttribute;
import hrms.model.payroll.billbrowser.ReportList;
import hrms.model.payroll.schedule.AbsenteeStatementScheduleBean;
import hrms.model.payroll.schedule.AuditRecoveryBean;
import hrms.model.payroll.schedule.BankAcountScheduleBean;
import hrms.model.payroll.schedule.BillBackPageBean;
import hrms.model.payroll.schedule.BillContributionRepotBean;
import hrms.model.payroll.schedule.BillFrontPageBean;
import hrms.model.payroll.schedule.CommonScheduleMethods;
import hrms.model.payroll.schedule.ComputerTokenReportBean;
import hrms.model.payroll.schedule.ExcessPayBean;
import hrms.model.payroll.schedule.GPFScheduleBean;
import hrms.model.payroll.schedule.GisAndFaScheduleBean;
import hrms.model.payroll.schedule.HCScheduleBean;
import hrms.model.payroll.schedule.ItScheduleBean;
import hrms.model.payroll.schedule.LTCScheduleBean;
import hrms.model.payroll.schedule.LicScheduleBean;
import hrms.model.payroll.schedule.LicSchedulePolicyBean;
import hrms.model.payroll.schedule.LoanAdvanceScheduleBean;
import hrms.model.payroll.schedule.OTC84Bean;
import hrms.model.payroll.schedule.OtcForm82Bean;
import hrms.model.payroll.schedule.OtcFormBean;
import hrms.model.payroll.schedule.OtcPlanForm40Bean;
import hrms.model.payroll.schedule.PLIScheduleBean;
import hrms.model.payroll.schedule.PeriodicAbsenteeStmtBean;
import hrms.model.payroll.schedule.PrivateLoanScheduleBean;
import hrms.model.payroll.schedule.PtScheduleBean;
import hrms.model.payroll.schedule.Schedule;
import hrms.model.payroll.schedule.ScheduleHelper;
import hrms.model.payroll.schedule.SecondScheduleBean;
import hrms.model.payroll.schedule.TPFEmployeeScheduleBean;
import hrms.model.payroll.schedule.ThirdScheduleBean;
import hrms.model.payroll.schedule.VacancyStatementScheduleBean;
import hrms.model.payroll.schedule.VehicleScheduleBean;
import hrms.model.payroll.schedule.VoucherSlipBean;
import hrms.model.payroll.schedule.WrrScheduleBean;
import hrms.model.payroll.tpfschedule.TPFScheduleBean;
import hrms.model.payroll.tpfschedule.TpfTypeBean;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.apache.commons.lang.StringUtils;

public class ScheduleDAOImpl implements ScheduleDAO {

    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List getDisplayReportList(String billNo, String billType) {
        List list = new ArrayList();
        ResultSet rs = null;
        String sqlQuery = "";
        Connection con = null;
        Statement st = null;

        sqlQuery = "Select * from G_SCHEDULE  where HAVEREPORT='Y' AND BILL_TYPE='" + billType + "' AND BILL_FOR='R' order by sl_no";

        try {
            con = dataSource.getConnection();
            st = con.createStatement();
            rs = st.executeQuery(sqlQuery);
            ReportList rlhb = null;
            while (rs.next()) {
                rlhb = new ReportList();
                rlhb.setSlNo(rs.getString("SL_NO"));
                rlhb.setReportName(rs.getString("SCHEDULE_DESC"));
                rlhb.setActionPath(rs.getString("REPORT_PATH") + "billNo=" + billNo);
                if (rs.getString("PDF_PATH") != null && !rs.getString("PDF_PATH").equals("")) {
                    rlhb.setPdfLink(rs.getString("PDF_PATH") + "billNo=" + billNo);
                } else {
                    rlhb.setPdfLink("");
                }
                rlhb.setHidePrintOption(rs.getString("HIDE_PRINT_LINK"));
                list.add(rlhb);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, st);
            DataBaseFunctions.closeSqlObjects(con);
        }

        return list;
    }

    @Override
    public QuaterRent[] getRentData(int month, int year) {
        List<QuaterRent> rentdata = new ArrayList();
        Connection conn = null;
        ResultSet result = null;
        PreparedStatement statement = null;
        try {
            conn = dataSource.getConnection();
            String SQL = "select hrmsid,gpfno,ddocode,hra,wtax,swtax,dt_realn,tvno,tvdate,HRMS_EQUATER_DATA.dos,f_name,m_name,l_name,mobile,dob,quarter_no from HRMS_EQUATER_DATA INNER JOIN EMP_MAST ON HRMS_EQUATER_DATA.hrmsid= EMP_MAST.emp_id where aq_month=? and aq_year=?";
            statement = conn.prepareStatement(SQL);
            statement.setInt(1, month - 1);
            statement.setInt(2, year);
            result = statement.executeQuery();
            while (result.next()) {
                QuaterRent qRent = new QuaterRent();
                qRent.setHrmsid(result.getString("hrmsid"));
                qRent.setGpfno(result.getString("gpfno"));
                qRent.setDdocode(result.getString("ddocode"));
                qRent.setHra(result.getInt("hra"));
                qRent.setWtax(result.getInt("wtax"));
                qRent.setSwtax(result.getInt("swtax"));
                qRent.setDateOfRealn(result.getString("dt_realn"));
                qRent.setTvno(result.getString("tvno"));
                qRent.setTvdate(result.getString("tvdate"));
                qRent.setDos(result.getString("dos"));
                qRent.setDob(result.getString("dob"));
                qRent.setFname(result.getString("f_name"));
                qRent.setMname(result.getString("m_name"));
                qRent.setLname(result.getString("l_name"));
                qRent.setMobile(result.getString("mobile"));
                qRent.setQuarterNo(result.getString("quarter_no"));
                rentdata.add(qRent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            DataBaseFunctions.closeSqlObjects(result, statement);
            DataBaseFunctions.closeSqlObjects(conn);

        }

        QuaterRent quaterRent[] = rentdata.toArray(new QuaterRent[rentdata.size()]);
        return quaterRent;
    }

    @Override
    public GPFScheduleBean getGPFScheduleHeaderDetails(String billno) {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        GPFScheduleBean gpfBean = new GPFScheduleBean();
        int month = 0;
        String year = "";
        try {
            con = dataSource.getConnection();
            stmt = con.createStatement();
            PayrollCommonFunction prcf = new PayrollCommonFunction();
            CommonReportParamBean bean = prcf.getCommonReportParameter(con, billno);

            gpfBean.setOfficeName(bean.getOfficeen());
            gpfBean.setDdoDesg(bean.getDdoname());
            gpfBean.setBillDesc(bean.getBilldesc());
            gpfBean.setBillNo(billno);

            rs = stmt.executeQuery("SELECT AQ_MONTH, AQ_YEAR FROM BILL_MAST WHERE BILL_NO = '" + billno + "'");
            if (rs.next()) {
                month = rs.getInt("AQ_MONTH");
                gpfBean.setBillMonth(CalendarCommonMethods.getFullMonthAsString(month));
                gpfBean.setBillYear(rs.getString("AQ_YEAR"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, stmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return gpfBean;
    }

    @Override
    public List getGPFScheduleAbstractList(String billno, int aqmonth, int aqyear) {

        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        ArrayList gpfAbstractList = new ArrayList();
        GPFScheduleBean gpfBean = null;
        String aqDtlsTbl = "";

        try {
            con = dataSource.getConnection();
            stmt = con.createStatement();

            aqDtlsTbl = getAqDtlsTableName(billno);
            String gpfAbstQry1 = "SELECT gpf_type, sum(ad_amt) amt FROM AQ_MAST AM INNER JOIN " + aqDtlsTbl + " AD on AM.aqsl_no = AD.aqsl_no WHERE BILL_NO='" + billno + "' "
                    + " AND AM.aq_month=" + aqmonth + " AND AM.aq_year=" + aqyear + " AND ((AD_CODE='GPF' and DED_TYPE='S') or (AD_CODE='GA' AND DED_TYPE='L') or (AD_CODE='GPDD' AND DED_TYPE='S') or "
                    + " (AD_CODE='GPIR' AND DED_TYPE='S')) group by gpf_type order by gpf_type";

            rs = stmt.executeQuery(gpfAbstQry1);
            while (rs.next()) {
                gpfBean = new GPFScheduleBean();

                gpfBean.setGpfType(rs.getString("GPF_TYPE"));
                gpfBean.setTotalAmount(rs.getString("amt"));

                gpfAbstractList.add(gpfBean);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, stmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return gpfAbstractList;
    }

    @Override
    public List getGPFScheduleTypeList(String billno, int aqmonth, int aqyear) {

        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        ArrayList gpfTypeList = new ArrayList();
        ScheduleHelper scHelperBean = null;
        int pageno = 0;
        try {
            con = dataSource.getConnection();

            int releaseTot = 0;
            String releaseTotFig = null;
            String gpfQry = "SELECT GPF_TYPE FROM AQ_MAST WHERE BILL_NO=? AND GPF_TYPE IS NOT NULL  AND aq_month=? AND aq_year=? GROUP BY GPF_TYPE ORDER BY GPF_TYPE";
            pst = con.prepareStatement(gpfQry);
            pst.setInt(1, Integer.parseInt(billno));
            pst.setInt(2, aqmonth);
            pst.setInt(3, aqyear);
            rs = pst.executeQuery();

            while (rs.next()) {
                pageno += 1;
                releaseTot = 0;
                scHelperBean = new ScheduleHelper();
                String gpfType = rs.getString("GPF_TYPE");
                scHelperBean.setGpfType(gpfType);
                scHelperBean.setPageheaderparent(reportPageHeader(con, "GPF", gpfType, billno, null) + "");
                ArrayList al = getEmpGpfDetails(gpfType, billno, con, scHelperBean);
                scHelperBean.setHelperList(al);

                GPFScheduleBean obj1 = null;
                if (al != null && al.size() > 0) {
                    obj1 = new GPFScheduleBean();
                    for (int i = 0; i < al.size(); i++) {
                        obj1 = (GPFScheduleBean) al.get(i);
                        releaseTot = releaseTot + obj1.getTotalReleased();
                        releaseTotFig = Numtowordconvertion.convertNumber((int) releaseTot);
                    }
                }
                scHelperBean.setReleaseTot(releaseTot);
                scHelperBean.setReleaseTotFig(releaseTotFig);
                scHelperBean.setEmpNo(scHelperBean.getHelperList().size());
                scHelperBean.setPagebreakparent("<input type=\"button\" name=\"pagebreak1\" style=\"page-break-before: always;width: 0;height: 0\"/>");
                gpfTypeList.add(scHelperBean);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return gpfTypeList;
    }

    @Override
    public PrivateLoanScheduleBean getPrivateLoanScheduleDetails(String billno) {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        PrivateLoanScheduleBean loanBean = new PrivateLoanScheduleBean();

        String year = "";
        String month = "";
        String billdesc = "";
        String billdate = "";
        String ddoacctno = "";

        try {
            con = dataSource.getConnection();
            stmt = con.createStatement();
            PayrollCommonFunction prcf = new PayrollCommonFunction();
            CommonReportParamBean bean = prcf.getCommonReportParameter(con, billno);

            String loanQry = "SELECT AQ_MONTH,AQ_YEAR,BILL_DESC,BILL_DATE,DDO_CUR_ACC_NO FROM(SELECT AQ_MONTH,AQ_YEAR,BILL_DESC,BILL_DATE,OFF_CODE FROM "
                    + "BILL_MAST WHERE BILL_NO='" + billno + "')BILLMAST INNER JOIN G_OFFICE ON BILLMAST.OFF_CODE = G_OFFICE.OFF_CODE";

            rs = stmt.executeQuery(loanQry);
            if (rs.next()) {
                year = rs.getString("AQ_YEAR");
                month = rs.getString("AQ_MONTH");
                billdesc = rs.getString("BILL_DESC");
                ddoacctno = rs.getString("DDO_CUR_ACC_NO");
                if (rs.getString("BILL_DATE") != null && !rs.getString("BILL_DATE").equals("")) {
                    billdate = CommonFunctions.getFormattedOutputDate1(rs.getDate("BILL_DATE"));
                }
            }
            loanBean.setOfficeName(bean.getOfficename());
            loanBean.setBillDesc(billdesc);
            loanBean.setYear(bean.getAqyear());
            loanBean.setMonth(new CalendarCommonMethods().getMonthAsString(bean.getAqmonth()));
            loanBean.setDdoAccountNo(ddoacctno);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, stmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return loanBean;
    }

    @Override
    public List getPrivateLoanScheduleEmpDetails(String billno, int aqMonth, int aqYear) {

        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        Statement stmt1 = null;
        ResultSet rs1 = null;

        ArrayList ploanScheduleList = new ArrayList();
        ScheduleHelper scHelperBean = null;
        PrivateLoanScheduleBean loanBean = null;
        ArrayList amtList = null;
        int slno = 1;
        int deductAmt = 0;
        String empcode = "";
        String aqslno = "";
        String aqDTLS = "AQ_DTLS";
        try {
            con = dataSource.getConnection();
            stmt = con.createStatement();
            stmt1 = con.createStatement();

            if ((aqYear == 2017 && aqMonth < 2) || aqYear < 2017) {
                aqDTLS = "hrmis.AQ_DTLS1";
            }
            /*String pLoanQry = "SELECT AQ_MAST.AQSL_NO,AQ_DTLS.EMP_CODE,AQ_MAST.EMP_NAME,AQ_MAST.CUR_DESG FROM "
             + "(SELECT * FROM AQ_MAST WHERE AQ_MAST.BILL_NO='" + billno + "')AQ_MAST INNER JOIN "
             + "(SELECT AQ_DTLS.EMP_CODE FROM " + aqDTLS + " WHERE (AQ_DTLS.SCHEDULE='PVTL' OR AQ_DTLS.SCHEDULE='PVTD') "
             + "AND AQ_DTLS.AD_TYPE='D' AND AQ_DTLS.AQ_MONTH=" + aqMonth + " AND AQ_DTLS.AQ_YEAR=" + aqYear + " group by EMP_CODE)AQ_DTLS "
             + "ON AQ_DTLS.EMP_CODE=AQ_MAST.EMP_CODE ORDER BY POST_SL_NO";*/
            String pLoanQry = "select aq_mast.EMP_CODE,aq_mast.AQSL_NO,EMP_NAME,CUR_DESG from aq_mast"
                    + " inner join " + aqDTLS + " on aq_mast.emp_code=" + aqDTLS + ".emp_code where BILL_NO='" + billno + "' and (" + aqDTLS + ".SCHEDULE='PVTL' OR " + aqDTLS + ".SCHEDULE='PVTD') AND " + aqDTLS + ".AD_TYPE='D' group by aq_mast.EMP_CODE,aq_mast.AQSL_NO,EMP_NAME,CUR_DESG ORDER BY POST_SL_NO";
            rs = stmt.executeQuery(pLoanQry);
            while (rs.next()) {
                if (rs.getString("EMP_NAME") != null && !rs.getString("EMP_NAME").equals("")) {
                    amtList = new ArrayList();
                    scHelperBean = new ScheduleHelper();

                    scHelperBean.setSlno(slno);
                    slno = slno + 1;
                    scHelperBean.setEmpname(rs.getString("EMP_NAME"));
                    scHelperBean.setEmpdesg(rs.getString("CUR_DESG"));
                    empcode = rs.getString("EMP_CODE");
                    aqslno = rs.getString("AQSL_NO");
                    scHelperBean.setAqslNo(aqslno);
                    String ploanQry1 = "SELECT AD_AMT,AD_DESC FROM " + aqDTLS + " WHERE (SCHEDULE='PVTL' OR SCHEDULE='PVTD') "
                            + "AND EMP_CODE='" + empcode + "' AND AQSL_NO = '" + aqslno + "' AND AQ_MONTH=" + aqMonth + " "
                            + "AND AQ_YEAR=" + aqYear + " AND AD_AMT>0";
                    rs1 = stmt1.executeQuery(ploanQry1);
                    while (rs1.next()) {
                        loanBean = new PrivateLoanScheduleBean();
                        int dedAmt = rs1.getInt("AD_AMT");
                        deductAmt = deductAmt + dedAmt;
                        scHelperBean.setPageTotalPLS(String.valueOf(deductAmt));
                        loanBean.setDeductedAmt(dedAmt);
                        loanBean.setDeductedAmtDesc(rs1.getString("AD_DESC"));
                        amtList.add(loanBean);
                    }
                    scHelperBean.setHelperList(amtList);
                    scHelperBean.setDeductAmt(deductAmt);

                    if (slno % 20 == 0) {
                        scHelperBean.setPagebreakPLS("<input type=\"button\" name=\"pagebreak1\" style=\"page-break-before: always;width: 0;height: 0\"/>");
                        scHelperBean.setPageHeaderPLS(reportPageHeader(con, "PLS", null, billno, "") + "");
                    } else {
                        scHelperBean.setPagebreakPLS("");
                        scHelperBean.setPageHeaderPLS("");
                    }
                    ploanScheduleList.add(scHelperBean);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, stmt);
            DataBaseFunctions.closeSqlObjects(rs1, stmt1);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return ploanScheduleList;
    }

    @Override
    public AuditRecoveryBean getAuditRecoveryScheduleDetails(String billno) {
        Connection con = null;
        AuditRecoveryBean auditBean = new AuditRecoveryBean();
        try {
            con = dataSource.getConnection();
            PayrollCommonFunction prcf = new PayrollCommonFunction();
            CommonReportParamBean bean = prcf.getCommonReportParameter(con, billno);

            auditBean.setAqMonth(CalendarCommonMethods.getFullMonthAsString(bean.getAqmonth()) + "");
            auditBean.setOfficeName(bean.getOfficename());
            auditBean.setBillDesc(bean.getBilldesc());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return auditBean;
    }

    @Override
    public List getAuditRecoveryScheduleEmpDetails(String billno, int aqYear, int aqMonth) {

        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        ArrayList auditRepotScheduleList = new ArrayList();
        AuditRecoveryBean auditBean = null;
        int slno = 0;
        String aqDtlsTbl = "";
        int cfDedAmt = 0;
        try {
            con = dataSource.getConnection();
            stmt = con.createStatement();
            aqDtlsTbl = getAqDtlsTableName(billno);
            String auditQry = "SELECT * from (SELECT AQ_MAST.EMP_NAME,AQ_MAST.CUR_DESG,AQSL_NO from AQ_MAST where AQ_MAST.BILL_NO='" + billno + "') "
                    + "AQ_MAST INNER JOIN (SELECT EMP_CODE,AD_CODE,SCHEDULE,AQSL_NO,AD_AMT,TOT_REC_AMT,REF_DESC,AD_REF_ID from " + aqDtlsTbl + " where "
                    + "AD_CODE = 'AUDR' AND AQ_MONTH =" + aqMonth + " AND AQ_YEAR=" + aqYear + " AND AD_AMT > 0) AQ_DTLS ON AQ_MAST.AQSL_NO = AQ_DTLS.AQSL_NO LEFT OUTER JOIN "
                    + "(SELECT LOANID,P_ORG_AMT,LOAN_TP,EMP_ID FROM EMP_LOAN_SANC)EMP_LOAN_SANC ON AQ_DTLS.AD_REF_ID = EMP_LOAN_SANC.LOANID";

            rs = stmt.executeQuery(auditQry);
            while (rs.next()) {
                slno++;
                auditBean = new AuditRecoveryBean();

                auditBean.setSlno(slno);
                auditBean.setEmpname(rs.getString("EMP_NAME"));
                auditBean.setEmpdesg(rs.getString("CUR_DESG"));
                if (rs.getString("TOT_REC_AMT") != null && !rs.getString("TOT_REC_AMT").equals("")) {
                    auditBean.setAmtRec(rs.getString("TOT_REC_AMT"));
                } else {
                    auditBean.setAmtRec("&nbsp;");
                }

                int dedAmt = Integer.parseInt(rs.getString("AD_AMT"));
                auditBean.setAmtDeduct(String.valueOf(dedAmt));
                cfDedAmt = cfDedAmt + dedAmt;
                auditBean.setCarryFordAmt(String.valueOf(cfDedAmt));

                auditBean.setNoofInstallment(rs.getString("REF_DESC"));
                if (rs.getString("P_ORG_AMT") != null && rs.getString("TOT_REC_AMT") != null) {
                    auditBean.setBalance(Integer.parseInt(rs.getString("P_ORG_AMT")) - Integer.parseInt(rs.getString("TOT_REC_AMT")));
                }
                auditBean.setAuditReport("");
                auditBean.setHod("");
                auditBean.setRemarks("");

                auditRepotScheduleList.add(auditBean);

                if (slno % 19 == 0) {
                    auditBean.setPagebreakAR("<input type=\"button\" name=\"pagebreak1\" style=\"page-break-before: always;width: 0;height: 0\"/>");
                    auditBean.setPageHeaderAR(reportPageHeader(con, "AR", null, billno, "") + "");
                } else {
                    auditBean.setPagebreakAR("");
                    auditBean.setPageHeaderAR("");
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, stmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return auditRepotScheduleList;
    }

    @Override
    public List getEmployeeWiseTPFList(String billNo) {

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList tpfList = new ArrayList();
        TpfTypeBean tpfBean = null;

        try {
            con = dataSource.getConnection();
            String tpfQuery = "SELECT GPF_TYPE FROM AQ_MAST WHERE BILL_NO ='" + billNo + "' AND ACCT_TYPE='TPF' AND GPF_TYPE IS NOT NULL "
                    + "GROUP BY GPF_TYPE ORDER BY GPF_TYPE";
            pstmt = con.prepareStatement(tpfQuery);
            rs = pstmt.executeQuery();
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
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return tpfList;
    }

    @Override
    public List getTPFAbstractList(String billNo) {

        Connection con = null;
        PreparedStatement pstmt = null;
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
            String tpfQuery = "SELECT GPF_TYPE,SUM(AD_AMT) AMT FROM (SELECT AQ_MAST.AQSL_NO,GPF_TYPES.GPF_TYPE,AD_AMT FROM (SELECT AQ_MAST.AQSL_NO,GPF_TYPE FROM AQ_MAST where BILL_NO = '" + billNo + "')AQ_MAST"
                    + " INNER JOIN (SELECT GPF_TYPE from AQ_MAST where BILL_NO ='" + billNo + "' group by GPF_TYPE) GPF_TYPES ON GPF_TYPES.GPF_TYPE = AQ_MAST.GPF_TYPE"
                    + " INNER JOIN (SELECT AQSL_NO,AD_AMT FROM " + aqdtlsTblName + " WHERE (AD_CODE = 'TPF' and DED_TYPE ='S') or (AD_CODE='TPFGA' AND DED_TYPE='L') or (AD_CODE='GPDD' AND DED_TYPE='S') or (AD_CODE='GPIR' AND DED_TYPE='S'))AQ_DTLS ON AQ_MAST.AQSL_NO = AQ_DTLS.AQSL_NO)TEMP GROUP BY GPF_TYPE ORDER BY GPF_TYPE";
            pstmt = con.prepareStatement(tpfQuery);
            rs = pstmt.executeQuery();
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
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return tpfabstractList;
    }

    public ArrayList getEmpTPFDetails(String gpfType, String billNo, Connection con) {

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
            String query = "SELECT * FROM (SELECT GPF_ACC_NO,regexp_replace(EMP_MAST.GPF_NO, '\\d', '', 'g') SERIES,EMP_MAST.DOE_GOV,EMP_MAST.DOB,"
                    + "EMP_MAST.DOS,AQ_MAST.EMP_CODE,AQ_MAST.EMP_NAME,AQ_MAST.CUR_DESG,AQ_MAST.BANK_ACC_NO,AQ_MAST.CUR_BASIC,AQ_MAST.PAY_SCALE,"
                    + "AQ_MAST.AQSL_NO,POST_SL_NO,F_NAME from (SELECT EMP_CODE,EMP_NAME,CUR_DESG,BANK_ACC_NO,CUR_BASIC,PAY_SCALE,AQSL_NO,"
                    + "POST_SL_NO,GPF_ACC_NO from AQ_MAST WHERE GPF_TYPE ='" + gpfType + "' AND BILL_NO ='" + billNo + "' AND ACCT_TYPE='TPF') AQ_MAST "
                    + "left outer join EMP_MAST on AQ_MAST.EMP_CODE =EMP_MAST.EMP_ID ) TPF_DETAILS ORDER BY SERIES,F_NAME";
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                empSchBean = new TPFEmployeeScheduleBean();
                if (rs.getString("EMP_NAME") != null && !rs.getString("EMP_NAME").equals("")) {

                    empSchBean.setName(rs.getString("EMP_NAME"));
                    empSchBean.setDesignation(rs.getString("CUR_DESG"));
                    empSchBean.setAccNo(rs.getString("GPF_ACC_NO"));
                    empSchBean.setBasicPay(rs.getString("CUR_BASIC"));
                    empSchBean.setScaleOfPay(rs.getString("PAY_SCALE"));
                    noofinst = CommonScheduleMethods.getNoOfInst2(rs.getString("AQSL_NO"), aqdtlsTblName, con);
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
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs1, rs2, rs3);
            DataBaseFunctions.closeSqlObjects(rs, stmt);
        }
        return empGpfList;
    }

    @Override
    public OTC84Bean getOTC84ScheduleDetails(String billno, int aqYear, int aqMonth) {

        Connection con = null;
        OTC84Bean otc84Bean = new OTC84Bean();
        Statement stmt = null;
        ResultSet rs = null;
        String month = "";
        String aqDTLS = "AQ_DTLS";

        try {
            con = dataSource.getConnection();
            stmt = con.createStatement();
            if ((aqYear == 2017 && aqMonth < 2) || aqYear < 2017) {
                aqDTLS = "hrmis.AQ_DTLS1";
            }

            PayrollCommonFunction prcf = new PayrollCommonFunction();
            CommonReportParamBean bean = prcf.getCommonReportParameter(con, billno);

            String arr1[] = new CommonScheduleMethods().getTotalAmount(con, billno, aqDTLS, aqYear, aqMonth);
            String arr[] = new CommonScheduleMethods().getGrossTotalAmount(con, billno);
            otc84Bean.setNetAmount(arr1[0]);
            otc84Bean.setGross(Double.valueOf(arr[0]).longValue() + "");
            otc84Bean.setGrossAmountWord(Numtowordconvertion.convertNumber((int) Double.parseDouble(otc84Bean.getGross())));

            otc84Bean.setBillDesc(bean.getBilldesc());
            otc84Bean.setOffName(bean.getOfficename());
            otc84Bean.setBillDate(bean.getBilldate());
            otc84Bean.setBillMonth("" + bean.getAqmonth());
            month = new CalendarCommonMethods().getMonthAsString(bean.getAqmonth());
            otc84Bean.setBillMonth(month);
            otc84Bean.setBillYear(bean.getAqyear() + "");
            otc84Bean.setTreasuryOffice(bean.getTreasuryname());
            otc84Bean.setBranchManager(bean.getBranchmanager());
            otc84Bean.setBranchName(bean.getBranchname());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, stmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return otc84Bean;
    }

    @Override
    public ExcessPayBean getExcessPayScheduleHeaderDetails(String billno) {

        Connection con = null;
        ExcessPayBean excessBean = new ExcessPayBean();
        Statement stmt = null;
        ResultSet rs = null;

        String yearVal = "";
        int monthVal = 0;
        String monthStr = "";
        String result = "";
        try {
            con = dataSource.getConnection();
            stmt = con.createStatement();

            PayrollCommonFunction prcf = new PayrollCommonFunction();
            CommonReportParamBean bean = prcf.getCommonReportParameter(con, billno);

            String excessQry = "SELECT AQ_MONTH,AQ_YEAR FROM BILL_MAST WHERE BILL_NO='" + billno + "'";
            rs = stmt.executeQuery(excessQry);
            while (rs.next()) {
                yearVal = rs.getString("AQ_YEAR");
                monthVal = rs.getInt("AQ_MONTH");
                monthStr = CalendarCommonMethods.getFullMonthAsString(monthVal);
                result = monthStr + "-" + yearVal;
            }
            excessBean.setMonthYear(result);
            excessBean.setBillNo(bean.getBilldesc());
            excessBean.setDeptName(bean.getDeptname());
            excessBean.setOffName(bean.getOfficename());
            excessBean.setDdoDegn(bean.getDdoname());
            excessBean.setBillDesc(bean.getBilldesc());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, stmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return excessBean;
    }

    @Override
    public List getExcessPayScheduleEmpDetails(String billno, int aqYear, int aqMonth) {

        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        Statement stmt1 = null;
        ResultSet rs1 = null;

        ArrayList excessPayScheduleList = new ArrayList();
        ExcessPayBean excessBean = null;
        int carryForwardTax = 0;
        int totalGross = 0;
        int basicSal = 0;
        String empCode = "";

        String gross = "";
        int slNo = 0;
        String aqDtlsTbl = "";
        try {
            con = dataSource.getConnection();
            stmt = con.createStatement();
            stmt1 = con.createStatement();

            PayrollCommonFunction prcf = new PayrollCommonFunction();
            CommonReportParamBean bean = prcf.getCommonReportParameter(con, billno);
            aqDtlsTbl = getAqDtlsTableName(billno);

            String excessQry = "select c.EMP_CODE, c.EMP_NAME, c.CUR_DESG, c.CUR_BASIC, a.AD_AMT from " + aqDtlsTbl + " a, BILL_MAST b, AQ_MAST c where "
                    + "b.bill_no = '" + billno + "' and a.aqsl_no = c.aqsl_no and a.aq_year = c.aq_year and a.aq_month = c.aq_month and "
                    + "b.bill_no = c.bill_no and SCHEDULE='EP' AND AD_TYPE='D' AND AD_AMT >0 and a.aq_month = " + aqMonth + " and "
                    + "a.aq_year = " + aqYear + " order by POST_SL_NO";

            rs = stmt.executeQuery(excessQry);
            while (rs.next()) {
                slNo++;
                excessBean = new ExcessPayBean();
                excessBean.setSlno(slNo);

                excessBean.setEmpName(rs.getString("EMP_NAME"));
                excessBean.setEmpDegn(rs.getString("CUR_DESG"));
                if (rs.getString("AD_AMT") != null && !rs.getString("AD_AMT").equals("")) {
                    excessBean.setEmpTaxOnProffesion(rs.getString("AD_AMT"));
                } else {
                    excessBean.setEmpTaxOnProffesion("0");
                }
                carryForwardTax = carryForwardTax + rs.getInt("AD_AMT");
                excessBean.setTotalTax(carryForwardTax + "");
                empCode = rs.getString("EMP_CODE");
                basicSal = rs.getInt("CUR_BASIC");

                String excessQry1 = "select AD_AMT,AD_DESC from " + aqDtlsTbl + " a, AQ_MAST b where b.bill_no ='" + billno + "' and b.EMP_CODE ='" + empCode + "' "
                        + "and a.aqsl_no = b.aqsl_no and a.aq_year = b.aq_year and a.aq_month = b.aq_month and AD_TYPE='A' and "
                        + "a.aq_month = " + aqMonth + " and a.aq_year = " + aqYear + "";

                rs1 = stmt1.executeQuery(excessQry1);
                int totalAllowance = 0;
                while (rs1.next()) {
                    totalAllowance = totalAllowance + rs1.getInt("AD_AMT");
                }
                gross = basicSal + totalAllowance + "";
                totalGross = totalGross + Integer.parseInt(gross);

                excessBean.setTotalGross(totalGross + "");
                excessBean.setEmpGrossSal(gross);

                if (slNo % 20 == 0) {
                    excessBean.setPagebreakEP("<input type=\"button\" name=\"pagebreak1\" style=\"page-break-before: always;width: 0;height: 0\"/>");
                    excessBean.setPageHeaderEP(reportPageHeader(con, "EP", null, billno, "") + "");
                } else {
                    excessBean.setPagebreakEP("");
                    excessBean.setPageHeaderEP("");
                }
                excessPayScheduleList.add(excessBean);

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, stmt);
            DataBaseFunctions.closeSqlObjects(rs1, stmt1);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return excessPayScheduleList;
    }

    @Override
    public ComputerTokenReportBean getCompTokenRepotScheduleDetails(String billno, int aqYear, int aqMonth) {

        Connection con = null;
        ComputerTokenReportBean tokenBean = new ComputerTokenReportBean();
        Statement stmt = null;
        ResultSet rs = null;
        Statement stmt1 = null;
        ResultSet rs1 = null;
        Statement stmt2 = null;
        ResultSet rs2 = null;
        String aqDTLS = "AQ_DTLS";
        String acct_type = null;
        double basicPay = 0;
        try {
            con = dataSource.getConnection();
            stmt = con.createStatement();
            stmt1 = con.createStatement();
            stmt2 = con.createStatement();
            if ((aqYear == 2017 && aqMonth < 2) || aqYear < 2017) {
                aqDTLS = "hrmis.AQ_DTLS1";
            }

            PayrollCommonFunction prcf = new PayrollCommonFunction();
            CommonReportParamBean bean = prcf.getCommonReportParameter(con, billno);
            String tokenQry1 = "SELECT TAB.*,POST,TR_NAME FROM(SELECT BILL_MAST.*,SECTOR_DESC,POST_TYPE,DDO_POST FROM (SELECT BILL_DATE,BILL_TYPE,"
                    + "BILL_DESC,DDO_CODE,DEMAND_NO,MAJOR_HEAD,SUB_MAJOR_HEAD,MINOR_HEAD,SUB_MINOR_HEAD1,SUB_MINOR_HEAD2,PLAN,SUB_MINOR_HEAD3,SECTOR,"
                    + "TR_CODE,BEN_REF_NO,TOKEN_NO FROM BILL_MAST WHERE BILL_NO='" + billno + "') BILL_MAST LEFT OUTER JOIN G_SECTOR ON BILL_MAST.SECTOR="
                    + "G_SECTOR.SECTOR_CODE LEFT OUTER JOIN G_POST_TYPE ON BILL_MAST.PLAN=G_POST_TYPE.POST_CODE INNER JOIN G_OFFICE ON "
                    + "BILL_MAST.DDO_CODE=G_OFFICE.DDO_CODE)TAB LEFT OUTER JOIN G_POST ON TAB.DDO_POST=G_POST.POST_CODE LEFT OUTER JOIN "
                    + "G_TREASURY ON TAB.TR_CODE=G_TREASURY.TR_CODE";
            rs = stmt.executeQuery(tokenQry1);
            if (rs.next()) {
                tokenBean.setBillDate(CommonFunctions.getFormattedOutputDate1(rs.getDate("BILL_DATE")));
                tokenBean.setBillType(rs.getString("BILL_TYPE"));
                tokenBean.setBillDesc(rs.getString("BILL_DESC"));
                tokenBean.setDdoCode(rs.getString("DDO_CODE"));
                tokenBean.setDdoName(rs.getString("POST"));
                tokenBean.setDemandNo(rs.getString("DEMAND_NO"));
                tokenBean.setMajorHead(rs.getString("MAJOR_HEAD"));
                tokenBean.setSubMajorHead(rs.getString("SUB_MAJOR_HEAD"));
                tokenBean.setMinor(rs.getString("MINOR_HEAD"));
                tokenBean.setSub(rs.getString("SUB_MINOR_HEAD1"));

                tokenBean.setDetail(rs.getString("SUB_MINOR_HEAD2"));
                tokenBean.setPlanOrNonPlan(rs.getString("PLAN"));
                tokenBean.setChargedOrVoted(rs.getString("SUB_MINOR_HEAD3"));
                tokenBean.setSector(rs.getString("SECTOR"));
                tokenBean.setSectorDesc(rs.getString("SECTOR_DESC"));
                tokenBean.setTreasuryName(rs.getString("TR_NAME"));
                tokenBean.setBenRefNo(rs.getString("BEN_REF_NO"));
                tokenBean.setTokenNo(rs.getString("TOKEN_NO"));
            }
            BillAmtDetails billAmtDetail = AqFunctionalities.BillAmt(con, billno, aqDTLS, aqYear, aqMonth);
            tokenBean.setGrossAmt(Double.valueOf(billAmtDetail.getBillGross() + "").longValue() + "");
            tokenBean.setNetAmt(Double.valueOf(billAmtDetail.getBillNet() + "").longValue() + "");
            tokenBean.setByTransferAmt(billAmtDetail.getBillDeduction());

            String tokenQry2 = "select SUM(CUR_BASIC) AMT from AQ_MAST where bill_no=" + billno;
            rs1 = stmt1.executeQuery(tokenQry2);
            if (rs1.next()) {
                basicPay = rs1.getDouble("AMT");
            }

            rs2 = stmt2.executeQuery("SELECT DISTINCT ACCT_TYPE FROM AQ_MAST WHERE BILL_NO='" + billno + "'");
            if (rs2.next()) {
                acct_type = rs2.getString("ACCT_TYPE");
            }

            tokenBean.setAllowanceList(getAllowanceList(con, "A", billno, basicPay, acct_type));// Object List
            tokenBean.setDeductionList(getDeductionList(con, "D", billno, acct_type)); // Deduction List

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, stmt);
            DataBaseFunctions.closeSqlObjects(rs1, stmt1);
            DataBaseFunctions.closeSqlObjects(rs2, stmt2);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return tokenBean;
    }

    @Override
    public OtcForm82Bean getOTCForm82ScheduleDetails(String billno) {

        Connection con = null;
        OtcForm82Bean otcBean = new OtcForm82Bean();
        Statement stmt = null;
        ResultSet rs = null;
        Statement stmt1 = null;
        ResultSet rs1 = null;
        Statement stmt2 = null;
        ResultSet rs2 = null;
        Statement stmt3 = null;
        ResultSet rs3 = null;
        Statement stmt4 = null;
        ResultSet rs4 = null;
        Statement stmt5 = null;
        ResultSet rs5 = null;
        String aqDtlsTbl = "";
        int curbasic = 0;
        int sum = 0;
        int sum1 = 0;
        int gross = 0;
        int net = 0;

        try {
            con = dataSource.getConnection();
            stmt = con.createStatement();
            stmt1 = con.createStatement();
            stmt2 = con.createStatement();
            stmt3 = con.createStatement();
            stmt4 = con.createStatement();
            stmt5 = con.createStatement();

            PayrollCommonFunction prcf = new PayrollCommonFunction();
            CommonReportParamBean bean = prcf.getCommonReportParameter(con, billno);
            aqDtlsTbl = getAqDtlsTableName(billno);
            String otcQry1 = "SELECT AQ_YEAR,AQ_MONTH,BILL_DESC,TR_NAME FROM (SELECT AQ_YEAR,AQ_MONTH,BILL_DESC,TR_CODE FROM BILL_MAST WHERE "
                    + "BILL_NO='" + billno + "') BILL_MAST LEFT OUTER JOIN G_TREASURY ON BILL_MAST.TR_CODE=G_TREASURY.TR_CODE";
            rs = stmt.executeQuery(otcQry1);
            if (rs.next()) {
                otcBean.setBillDesc(StringUtils.defaultString(rs.getString("BILL_DESC")));
                otcBean.setYear(StringUtils.defaultString(rs.getString("AQ_YEAR")));
                otcBean.setTreasuryName(StringUtils.defaultString(rs.getString("TR_NAME")));
                otcBean.setMonth(rs.getInt("AQ_MONTH") + "");
            }

            String otcQry2 = "SELECT SUM(CUR_BASIC) CUR_BASIC FROM AQ_MAST WHERE AQ_MAST.BILL_NO='" + billno + "'";
            rs1 = stmt1.executeQuery(otcQry2);
            if (rs1.next()) {
                curbasic = rs1.getInt("CUR_BASIC");
            }

            String otcQry3 = "SELECT SUM(AD_AMT) AD_AMT FROM (SELECT AQSL_NO FROM AQ_MAST WHERE BILL_NO='" + billno + "' and aq_year and aq_month) AQ_MAST INNER JOIN "
                    + "(SELECT AD_AMT,AQSL_NO FROM " + aqDtlsTbl + " WHERE AD_TYPE='A' AND AQ_MONTH=" + otcBean.getMonth() + " AND "
                    + "AQ_YEAR=" + otcBean.getYear() + ") AQ_DTLS ON AQ_MAST.AQSL_NO=AQ_DTLS.AQSL_NO";
            rs2 = stmt2.executeQuery(otcQry3);
            if (rs2.next()) {
                sum = rs2.getInt("AD_AMT");
            }

            String otcQry4 = "SELECT SUM(AD_AMT) AD_AMT FROM (SELECT AQSL_NO FROM AQ_MAST WHERE BILL_NO='" + billno + "'and aq_year and aq_month) AQ_MAST INNER JOIN "
                    + "(SELECT AD_AMT,AQSL_NO FROM " + aqDtlsTbl + " WHERE AD_TYPE='D' AND AQ_MONTH=" + otcBean.getMonth() + " AND AQ_YEAR=" + otcBean.getYear() + ") "
                    + "AQ_DTLS ON AQ_MAST.AQSL_NO=AQ_DTLS.AQSL_NO";
            rs3 = stmt3.executeQuery(otcQry4);
            if (rs3.next()) {
                sum1 += rs3.getInt("AD_AMT");
            }

            String otcQry5 = "SELECT SUM(AD_AMT) AD_AMT FROM (SELECT AQSL_NO FROM AQ_MAST WHERE BILL_NO='" + billno + "' and aq_year and aq_month) AQ_MAST INNER JOIN "
                    + "(SELECT AD_AMT,AQSL_NO FROM " + aqDtlsTbl + " WHERE AD_TYPE='D' AND (SCHEDULE ='PVTD' OR SCHEDULE ='PVTL') AND AQ_MONTH=" + otcBean.getMonth() + " "
                    + "AND AQ_YEAR=" + otcBean.getYear() + ") AQ_DTLS ON AQ_MAST.AQSL_NO=AQ_DTLS.AQSL_NO";
            rs4 = stmt4.executeQuery(otcQry5);
            int toDDOAccount = 0;
            if (rs4.next()) {
                toDDOAccount = rs4.getInt("AD_AMT");
            }
            gross = curbasic + sum;
            net = gross - sum1;
            otcBean.setNetPay("" + net);
            otcBean.setToDdoAccount(toDDOAccount);

            String otcQry6 = "SELECT COUNT(*) NOOFEMP FROM AQ_MAST WHERE BILL_NO=" + billno + " AND EMP_CODE IS NOT NULL";
            rs5 = stmt5.executeQuery(otcQry6);
            if (rs5.next()) {
                otcBean.setNoofEmp(rs5.getString("NOOFEMP"));
            }
            if (otcBean.getMonth() != null && !otcBean.getMonth().equals("")) {
                otcBean.setMonth(getMonthAsString(Integer.parseInt(otcBean.getMonth())));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, stmt);
            DataBaseFunctions.closeSqlObjects(rs1, stmt1);
            DataBaseFunctions.closeSqlObjects(rs2, stmt2);
            DataBaseFunctions.closeSqlObjects(rs3, stmt3);
            DataBaseFunctions.closeSqlObjects(rs4, stmt4);
            DataBaseFunctions.closeSqlObjects(rs5, stmt5);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return otcBean;
    }

    @Override
    public OtcPlanForm40Bean getOTCForm40ScheduleDetails(String billno, int aqYear, int aqMonth) {

        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        Statement stmt1 = null;
        ResultSet rs1 = null;
        OtcPlanForm40Bean otcBean = new OtcPlanForm40Bean();
        int netunder = 0;
        int basic = 0;
        String empcnt = "";
        int totda = 0;
        int totgp = 0;
        int totgpf = 0;
        int totga = 0;
        int grandtotgpf = 0;
        String aqDTLS = "AQ_DTLS";

        try {
            con = dataSource.getConnection();
            stmt = con.createStatement();
            stmt1 = con.createStatement();

            if ((aqYear == 2017 && aqMonth < 2) || aqYear < 2017) {
                aqDTLS = "hrmis.AQ_DTLS1";
            }

            PayrollCommonFunction prcf = new PayrollCommonFunction();
            CommonReportParamBean bean = prcf.getCommonReportParameter(con, billno);
            String otcQuery = "SELECT * FROM(select G_TREASURY.TR_NAME,G_BANK.BANK_NAME,G_BRANCH.BRANCH_NAME,(BILL_MAST.GROSS_AMT - BILL_MAST.DED_AMT) "
                    + "NET_AMOUNT,BILL_MAST.REC_BY,BILL_MAST.DESG,G_POST.POST,BILL_MAST.GROSS_AMT,DEMAND_NO,MAJOR_HEAD,SUB_MAJOR_HEAD,MINOR_HEAD,"
                    + "SUB_MINOR_HEAD1,SUB_MINOR_HEAD2,SUB_MINOR_HEAD3,BEN_REF_NO,TOKEN_NO,PLAN,BILL_MAST.DDO_CODE from (select * from bill_mast WHERE"
                    + " BILL_NO='" + billno + "') bill_mast LEFT OUTER join G_POST on bill_mast.OFF_DDO=G_POST.POST_CODE LEFT OUTER JOIN G_BANK ON "
                    + "bill_mast.BANK_CODE=G_BANK.BANK_CODE LEFT OUTER join G_BRANCH ON bill_mast.BRANCH_CODE=G_BRANCH.BRANCH_CODE LEFT OUTER join "
                    + "G_TREASURY ON bill_mast.TR_CODE=G_TREASURY.TR_CODE)TAB LEFT OUTER JOIN G_PLAN_STATUS ON TAB.PLAN=G_PLAN_STATUS.DESCRIPTION";
            rs = stmt.executeQuery(otcQuery);
            while (rs.next()) {
                if (rs.getString("BEN_REF_NO") != null && !rs.getString("BEN_REF_NO").equals("")) {
                    otcBean.setBenRefNo(rs.getString("BEN_REF_NO"));
                }
                otcBean.setOtcCode(rs.getString("PLAN_STATUS"));
                otcBean.setOtcStatus(rs.getString("PLAN_DESCPN"));

                otcBean.setTreasuryName(rs.getString("TR_NAME"));
                otcBean.setDemandNo(rs.getString("DEMAND_NO"));
                otcBean.setMajorHead(rs.getString("MAJOR_HEAD"));
                otcBean.setSubMajorHead(rs.getString("SUB_MAJOR_HEAD"));
                otcBean.setMinorHead(rs.getString("MINOR_HEAD"));
                otcBean.setSubMinorHead(rs.getString("SUB_MINOR_HEAD1"));
                otcBean.setSubMinorHead2(rs.getString("SUB_MINOR_HEAD2"));
                otcBean.setSubMinorHead3(rs.getString("SUB_MINOR_HEAD3"));
                otcBean.setDdoCode(rs.getString("DDO_CODE"));
                otcBean.setToken(rs.getString("TOKEN_NO"));
            }

            rs1 = stmt1.executeQuery("select sum(AQ_MAST.CUR_BASIC) basic,count(AQ_MAST.EMP_CODE) employees from AQ_MAST where BILL_NO='" + billno + "'");
            while (rs1.next()) {
                if (rs1.getString("basic") != null && !rs1.getString("basic").equals("")) {
                    basic = Integer.parseInt(rs1.getString("basic"));
                }
                empcnt = rs1.getString("employees");
            }

            BillAmtDetails billAmtDetail = AqFunctionalities.BillAmt(con, billno, aqDTLS, aqYear, aqMonth);
            otcBean.setNetAmount("" + (int) billAmtDetail.getBillNet());
            otcBean.setDeductTot("" + (int) billAmtDetail.getBillDeduction());
            otcBean.setGrossTot("" + (int) billAmtDetail.getBillGross());
            netunder = (int) billAmtDetail.getBillNet() + 1;
            otcBean.setNetAmtUnder("" + netunder);
            if (otcBean.getNetAmount() != null && !otcBean.getNetAmount().equals("")) {
                otcBean.setNetAmountWord(Numtowordconvertion.convertNumber(Integer.parseInt(otcBean.getNetAmount())).toUpperCase());
            }
            otcBean.setNetAmtUnderWord(Numtowordconvertion.convertNumber(netunder).toUpperCase());
            otcBean.setBasicPlusGp(Integer.toString(basic + totgp));
            otcBean.setTotalDa(Integer.toString(totda));
            grandtotgpf = totgpf + totga;
            otcBean.setTotalGpf(Integer.toString(grandtotgpf));
            otcBean.setGrandTotinWord(Numtowordconvertion.convertNumber((int) billAmtDetail.getBillGross()).toUpperCase());

            if (bean.getBilldesc() != null && !bean.getBilldesc().equals("")) {
                otcBean.setBillDesc(bean.getBilldesc());
            }
            if (bean.getBilldate() != null && !bean.getBilldate().equals("")) {
                otcBean.setBillDate(bean.getBilldate());
            }
            if (bean.getDdoname() != null && !bean.getDdoname().equals("")) {
                otcBean.setDdoName(bean.getDdoname());
            }
            otcBean.setBillMonth(CalendarCommonMethods.getMonthAsString(bean.getAqmonth()));
            otcBean.setBillYear("" + bean.getAqyear());
            otcBean.setOfficeName(bean.getOfficename());
            otcBean.setTanNo(bean.getTanno());
            otcBean.setPayhead(new CommonScheduleMethods().getADCodeHead(con, billno, "GP", aqDTLS, bean.getAqmonth(), bean.getAqyear()));
            otcBean.setDahead(new CommonScheduleMethods().getADCodeHead(con, billno, "DA", aqDTLS, bean.getAqmonth(), bean.getAqyear()));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, stmt);
            DataBaseFunctions.closeSqlObjects(rs1, stmt1);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return otcBean;
    }

    @Override
    public List getOTCForm40ScheduleEmpList(String billno, int aqYear, int aqMonth) {

        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        ArrayList otc40ScheduleList = new ArrayList();
        ScheduleHelper otcBean = new ScheduleHelper();
        int totgp = 0;
        String aqDTLS = "AQ_DTLS";

        try {
            con = dataSource.getConnection();
            stmt = con.createStatement();
            if ((aqYear == 2017 && aqMonth < 2) || aqYear < 2017) {
                aqDTLS = "hrmis.AQ_DTLS1";
            }

            PayrollCommonFunction prcf = new PayrollCommonFunction();
            CommonReportParamBean bean = prcf.getCommonReportParameter(con, billno);

            otcBean.setDeductionList(new CommonScheduleMethods().allowanceDeductionList(con, bean.getAqmonth(), bean.getAqyear() + "", "D", billno, aqDTLS));
            otcBean.setAllowanceList(new CommonScheduleMethods().allowanceDeductionList(con, bean.getAqmonth(), bean.getAqyear() + "", "A", billno, aqDTLS));

            List alList = otcBean.getAllowanceList();
            OtcPlanForm40Bean otc40Obj = null;
            for (int i = 0; i < alList.size(); i++) {
                otc40Obj = (OtcPlanForm40Bean) alList.get(i);
                if (otc40Obj.getGpAmt() > 0) {
                    totgp = totgp + otc40Obj.getGpAmt();
                }
            }
            otcBean.setPagebreakOTC("<input type=\"button\" name=\"pagebreak1\" style=\"page-break-before: always;width: 0;height: 0\"/>");
            otc40ScheduleList.add(otcBean);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, stmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return otc40ScheduleList;
    }

    @Override
    public OtcFormBean getOTCForm52ScheduleDetails(String billno) {

        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        Statement stmt1 = null;
        ResultSet rs1 = null;
        Statement stmt2 = null;
        OtcFormBean otcBean = new OtcFormBean();

        int basic = 0;
        String empcnt = "";
        String sqlAllowance = "";
        String sqlDeduction = "";
        int allowance = 0;
        int deduction = 0;
        String net = "";
        String gross = "";
        String aqDtlsTbl = "";
        try {
            con = dataSource.getConnection();
            stmt = con.createStatement();
            stmt1 = con.createStatement();
            stmt2 = con.createStatement();

            PayrollCommonFunction prcf = new PayrollCommonFunction();
            CommonReportParamBean bean = prcf.getCommonReportParameter(con, billno);
            aqDtlsTbl = getAqDtlsTableName(billno);
            String otcQry1 = "SELECT BILL_DESC,BILL_DATE,AQ_YEAR,AQ_MONTH,G_OFFICE.OFF_EN,DIST_NAME,STATE_NAME, G_POST.POST,G_OFFICE.OFF_EN,"
                    + "G_OFFICE.DDO_REG_NO,G_DEPARTMENT.DEPARTMENT_NAME,TAN_NO FROM (SELECT OFF_CODE,BILL_DESC,AQ_YEAR,AQ_MONTH,BILL_DATE FROM "
                    + "BILL_MAST WHERE BILL_NO='" + billno + "') BILL_NO INNER JOIN G_OFFICE ON BILL_NO.OFF_CODE = G_OFFICE.OFF_CODE LEFT OUTER JOIN G_POST ON "
                    + "G_OFFICE.DDO_POST=G_POST.POST_CODE LEFT OUTER JOIN G_DEPARTMENT ON G_OFFICE.DEPARTMENT_CODE =G_DEPARTMENT.DEPARTMENT_CODE LEFT OUTER "
                    + "JOIN G_DISTRICT ON G_OFFICE.DIST_CODE=G_DISTRICT.DIST_CODE LEFT OUTER JOIN G_STATE ON G_OFFICE.STATE_CODE=G_STATE.STATE_CODE";

            rs = stmt.executeQuery(otcQry1);
            if (rs.next()) {
                otcBean.setBillDesc(rs.getString("BILL_DESC"));
                otcBean.setBillDate(CommonFunctions.getFormattedOutputDate1(rs.getDate("BILL_DATE")));
            }
            DataBaseFunctions.closeSqlObjects(rs, stmt);

            stmt = con.createStatement();
            String otcQry2 = "select G_TREASURY.TR_NAME,G_BANK.BANK_NAME,G_BRANCH.BRANCH_NAME,(BILL_MAST.GROSS_AMT - BILL_MAST.DED_AMT) NET_AMOUNT,"
                    + "BILL_MAST.REC_BY,BILL_MAST.DESG,G_POST.POST,BILL_MAST.GROSS_AMT from (select * from bill_mast where BILL_NO='" + billno + "') "
                    + "bill_mast LEFT OUTER join G_POST on bill_mast.OFF_DDO=G_POST.POST_CODE LEFT OUTER join G_BANK ON bill_mast.BANK_CODE="
                    + "G_BANK.BANK_CODE LEFT OUTER join G_BRANCH ON bill_mast.BRANCH_CODE=G_BRANCH.BRANCH_CODE LEFT OUTER join G_TREASURY ON "
                    + "bill_mast.TR_CODE=G_TREASURY.TR_CODE";

            rs = stmt.executeQuery(otcQry2);
            while (rs.next()) {
                otcBean.setTreasuryOffice(rs.getString("TR_NAME"));
                otcBean.setBranchManager(rs.getString("BANK_NAME"));
                otcBean.setBranchName(rs.getString("BRANCH_NAME"));
                otcBean.setNetAmount(rs.getString("NET_AMOUNT"));

                if (otcBean.getNetAmount() != null && !otcBean.getNetAmount().equals("")) {
                    otcBean.setNetAmountWord(Numtowordconvertion.convertNumber(Integer.parseInt(otcBean.getNetAmount())).toUpperCase());
                } else {

                    stmt1 = con.createStatement();
                    String otcQry3 = "select sum(AQ_MAST.CUR_BASIC) basic,count(AQ_MAST.EMP_CODE) employees from AQ_MAST where BILL_NO='" + billno + "'";

                    rs1 = stmt1.executeQuery(otcQry3);
                    while (rs1.next()) {
                        if (rs1.getString("basic") != null && !rs1.getString("basic").equals("")) {
                            basic = Integer.parseInt(rs1.getString("basic"));
                        }
                        empcnt = rs1.getString("employees");
                    }
                    sqlAllowance = "select sum(AQ_DTLS.AD_AMT) allowance from (select * from AQ_MAST where BILL_NO='" + billno + "') AQ_MAST "
                            + "left outer join (select * from " + aqDtlsTbl + " where AQ_DTLS.AD_TYPE='A') AQ_DTLS on AQ_MAST.AQSL_NO=AQ_DTLS.AQSL_NO";

                    sqlDeduction = "select sum(AQ_DTLS.AD_AMT) allowance from (select * from AQ_MAST where BILL_NO='" + billno + "') AQ_MAST "
                            + "left outer join (select * from " + aqDtlsTbl + " where AQ_DTLS.AD_TYPE='D') AQ_DTLS on AQ_MAST.AQSL_NO=AQ_DTLS.AQSL_NO";

                    stmt2 = con.createStatement();
                    allowance = new CommonScheduleMethods().getAllowanceOrDeduction(stmt2, sqlAllowance);
                    deduction = new CommonScheduleMethods().getAllowanceOrDeduction(stmt2, sqlDeduction);
                    net = Integer.toString(basic + allowance - deduction);
                    gross = Integer.toString(basic + allowance);
                    otcBean.setNetAmount(net);
                    otcBean.setNetAmountWord(Numtowordconvertion.convertNumber(Integer.parseInt(otcBean.getNetAmount())).toUpperCase());
                }
                otcBean.setDdoSignature(rs.getString("POST"));
                otcBean.setBillNo(billno);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            DataBaseFunctions.closeSqlObjects(rs1, stmt2);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return otcBean;
    }

    @Override
    public VoucherSlipBean getVoucherSlipScheduleDetails(String billno, int aqYear, int aqMonth) {

        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        VoucherSlipBean voucherBean = new VoucherSlipBean();

        int month = 0;
        String year = "";
        String billdesc = "";
        String demandNo = "";
        String majorHead = "";
        String minorHeadDesc = "";
        String minorHead = "";
        String smHead1Desc = "";
        String postType = "";
        String smHead2Desc = "";
        String smHead3 = "";
        String treasuryCode = "";
        double billGrossAmt = 0.0;
        double billDeductionAmt = 0.0;
        double billNetAmt = 0.0;
        String aqDTLS = "AQ_DTLS";

        try {
            con = dataSource.getConnection();
            stmt = con.createStatement();
            if ((aqYear == 2017 && aqMonth < 2) || aqYear < 2017) {
                aqDTLS = "hrmis.AQ_DTLS1";
            }
            PayrollCommonFunction prcf = new PayrollCommonFunction();
            CommonReportParamBean bean = prcf.getCommonReportParameter(con, billno);

            voucherBean.setDdoName(bean.getDdoname());
            voucherBean.setOfficeName(bean.getOfficename());

            stmt = con.createStatement();
            String voucherQry1 = "SELECT G_TREASURY.TR_NAME,Bill_mast.BILL_DATE,Bill_mast.BILL_DESC,Bill_mast.AQ_MONTH,Bill_mast.AQ_YEAR from "
                    + "(select * from Bill_mast where bill_no='" + billno + "') Bill_mast LEFT OUTER JOIN G_TREASURY on "
                    + "Bill_mast.TR_CODE=G_TREASURY.TR_CODE";

            rs = stmt.executeQuery(voucherQry1);
            if (rs.next()) {
                month = Integer.parseInt(rs.getString("AQ_MONTH"));
                year = rs.getString("AQ_YEAR");
                billdesc = rs.getString("BILL_DESC");
            }
            voucherBean.setMonth((month + 1) + "");
            voucherBean.setYear(year);
            voucherBean.setBillDesc(billdesc);

            DataBaseFunctions.closeSqlObjects(rs, stmt);

            stmt = con.createStatement();
            String voucherQry2 = "SELECT gross_amt,ded_amt,DEMAND_NO,MAJOR_HEAD,MINOR_HEAD_DESC,MINOR_HEAD,SUB_MINOR_HEAD1_DESC,POST_TYPE,SUB_MINOR_HEAD2_DESC,"
                    + "SUB_MINOR_HEAD3,TR_CODE FROM (SELECT gross_amt,ded_amt,DEMAND_NO,MAJOR_HEAD,MINOR_HEAD_DESC,MINOR_HEAD,SUB_MINOR_HEAD1_DESC,PLAN,"
                    + "SUB_MINOR_HEAD2_DESC,SUB_MINOR_HEAD3,TR_CODE FROM BILL_MAST WHERE BILL_NO='" + billno + "')billmast left outer join G_POST_TYPE "
                    + "on G_POST_TYPE.POST_CODE=billmast.PLAN";

            rs = stmt.executeQuery(voucherQry2);
            if (rs.next()) {
                if (rs.getString("DEMAND_NO") != null && !rs.getString("DEMAND_NO").equals("")) {
                    demandNo = rs.getString("DEMAND_NO");
                } else {
                    demandNo = "";
                }
                if (rs.getString("MAJOR_HEAD") != null && !rs.getString("MAJOR_HEAD").equals("")) {
                    majorHead = rs.getString("MAJOR_HEAD");
                } else {
                    majorHead = "";
                }
                if (rs.getString("MINOR_HEAD_DESC") != null && !rs.getString("MINOR_HEAD_DESC").equals("")) {
                    minorHeadDesc = rs.getString("MINOR_HEAD_DESC");
                } else {
                    minorHeadDesc = "";
                }
                if (rs.getString("MINOR_HEAD") != null && !rs.getString("MINOR_HEAD").equals("")) {
                    minorHead = rs.getString("MINOR_HEAD");
                } else {
                    minorHead = "";
                }
                if (rs.getString("SUB_MINOR_HEAD1_DESC") != null && !rs.getString("SUB_MINOR_HEAD1_DESC").equals("")) {
                    smHead1Desc = rs.getString("SUB_MINOR_HEAD1_DESC");
                } else {
                    smHead1Desc = "";
                }
                if (rs.getString("POST_TYPE") != null && !rs.getString("POST_TYPE").equals("")) {
                    postType = rs.getString("POST_TYPE");
                } else {
                    postType = "";
                }
                if (rs.getString("SUB_MINOR_HEAD2_DESC") != null && !rs.getString("SUB_MINOR_HEAD2_DESC").equals("")) {
                    smHead2Desc = rs.getString("SUB_MINOR_HEAD2_DESC");
                } else {
                    smHead2Desc = "";
                }
                if (rs.getString("SUB_MINOR_HEAD3") != null && !rs.getString("SUB_MINOR_HEAD3").equals("")) {
                    smHead3 = rs.getString("SUB_MINOR_HEAD3");
                } else {
                    smHead3 = "";
                }
                if (rs.getString("TR_CODE") != null && !rs.getString("TR_CODE").equals("")) {
                    treasuryCode = rs.getString("TR_CODE");
                }

                billGrossAmt = rs.getInt("gross_amt");
                billDeductionAmt = rs.getInt("ded_amt");
                billNetAmt = billGrossAmt - billDeductionAmt;
                voucherBean.setGrossAmount(Double.valueOf(billGrossAmt + "").longValue() + "");
                voucherBean.setNetAmount(Double.valueOf(billNetAmt + "").longValue() + "");
            }
            voucherBean.setDemandno(demandNo);
            voucherBean.setMajorhead(majorHead);
            voucherBean.setMinorheaddesc(minorHeadDesc);
            voucherBean.setMinorhead(minorHead);
            voucherBean.setSubminorhead1desc(smHead1Desc);
            voucherBean.setPostType(postType);
            voucherBean.setSubminorhead2desc(smHead2Desc);
            voucherBean.setSubminorhead3(smHead3);
            voucherBean.setTreasuryCode(treasuryCode);

            voucherBean.setMonth((month + 1) + "");
            voucherBean.setYear(year);
            voucherBean.setBillDesc(billdesc);

            DataBaseFunctions.closeSqlObjects(rs, stmt);

            stmt = con.createStatement();
            String trName = "";
            if (!treasuryCode.equals("") && treasuryCode != null) {
                String selQry1 = "SELECT TR_NAME FROM G_TREASURY WHERE TR_CODE='" + treasuryCode + "'";
                rs = stmt.executeQuery(selQry1);
                if (rs.next()) {
                    if (rs.getString("TR_NAME") != null && !rs.getString("TR_NAME").equals("")) {
                        trName = rs.getString("TR_NAME");
                    } else {
                        trName = " ";
                    }
                }
            } else {
                trName = " ";
            }
            voucherBean.setTreasuryName(trName);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return voucherBean;
    }

    @Override
    public List getPeriodicAbsenteeStatementScheduleEmpList(String billno) {

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        PreparedStatement pstmt1 = null;
        ResultSet rs1 = null;
        List absenteeStmtList = new ArrayList();

        ScheduleHelper sHelperBean = null;
        PeriodicAbsenteeStmtBean absentStmtBean = null;
        int mnth = 0;
        int cnt = 0;
        String perd = "";
        try {
            con = dataSource.getConnection();

            String absentQry = "select EMP_CODE,EMP_NAME,GPF_ACC_NO,AQ_MONTH,AQ_YEAR,CUR_BASIC,AQ_DAY,CUR_DESG from aq_mast where "
                    + "bill_no='" + billno + "' AND EMP_CODE IS NOT NULL ORDER BY SEC_SL_NO";
            pstmt = con.prepareStatement(absentQry);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                sHelperBean = new ScheduleHelper();

                mnth = rs.getInt("AQ_MONTH");
                String absentQry1 = "SELECT ABS_FROM,ABS_TO FROM EMP_ABSENTEE WHERE EMP_ID='" + rs.getString("EMP_CODE") + "' AND MONTH='" + mnth + "' "
                        + "AND YEAR='" + rs.getString("AQ_YEAR") + "' order by ABS_FROM";
                pstmt1 = con.prepareStatement(absentQry1);
                rs1 = pstmt1.executeQuery();
                ArrayList localList = new ArrayList();
                cnt = 0;

                while (rs1.next()) {
                    cnt++;
                    absentStmtBean = new PeriodicAbsenteeStmtBean();

                    absentStmtBean.setNameofAbsentee(rs.getString("EMP_NAME"));
                    absentStmtBean.setGpfNo(rs.getString("GPF_ACC_NO"));

                    if (rs.getString("CUR_BASIC") != null && !rs.getString("CUR_BASIC").equals("")) {
                        absentStmtBean.setRateofPay("" + rs.getInt("CUR_BASIC") / rs.getInt("AQ_DAY"));
                    }
                    absentStmtBean.setEmpname(rs.getString("EMP_NAME"));
                    absentStmtBean.setSubPost(rs.getString("CUR_DESG"));

                    if (rs1.getString("ABS_FROM") != null && !rs1.getString("ABS_FROM").equals("")) {
                        perd = CommonFunctions.getFormattedOutputDate1(rs1.getDate("ABS_FROM"));
                    }
                    if (rs1.getString("ABS_TO") != null && !rs1.getString("ABS_TO").equals("")) {
                        perd = perd + " to " + CommonFunctions.getFormattedOutputDate1(rs1.getDate("ABS_TO"));
                    }
                    absentStmtBean.setPeriod(perd);
                    localList.add(absentStmtBean);
                }
                sHelperBean.setRowspan("" + cnt);
                sHelperBean.setHelperList(localList);
            }
            absenteeStmtList.add(sHelperBean);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(rs1, pstmt1);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return absenteeStmtList;
    }

    @Override
    public BillContributionRepotBean getBillContributionRepotScheduleHeaderDetails(String annexure, String billno) {

        Connection con = null;
        int billmonth = 0;
        Statement stmt = null;
        ResultSet rs = null;
        BillContributionRepotBean billContBean = new BillContributionRepotBean();
        try {
            con = dataSource.getConnection();
            stmt = con.createStatement();
            PayrollCommonFunction prcf = new PayrollCommonFunction();
            CommonReportParamBean bean = prcf.getCommonReportParameter(con, billno);

            if (bean.getBilldesc() != null && !bean.getBilldesc().equals("")) {
                billContBean.setBillDesc(bean.getBilldesc());
            }
            if (bean.getBilldate() != null && !bean.getBilldate().equals("")) {
                billContBean.setBillDate(bean.getBilldate());
            }
            if (bean.getDdoname() != null && !bean.getDdoname().equals("")) {
                billContBean.setDdoName(bean.getDdoname());
            }
            if (bean.getDdoregno() != null && !bean.getDdoregno().equals("")) {
                billContBean.setDdoRegdNo(bean.getDdoregno());
            }
            if (bean.getDtoregno() != null && !bean.getDtoregno().equals("")) {
                billContBean.setDtoRegdNo(bean.getDtoregno());
            }
            billmonth = bean.getAqmonth();
            billContBean.setAqMonth(String.valueOf(billmonth++));
            billContBean.setBillMonth("" + billmonth);
            billContBean.setBillYear("" + bean.getAqyear());
            billContBean.setOffName(bean.getOfficename());
            billContBean.setBillNo(billno);
            billContBean.setAnnexure(annexure);

            String trNameQry = "SELECT TR_NAME FROM (SELECT TR_CODE FROM BILL_MAST WHERE BILL_NO='" + billno + "') BILL_MAST INNER JOIN G_TREASURY ON "
                    + "BILL_MAST.TR_CODE=G_TREASURY.TR_CODE";
            rs = stmt.executeQuery(trNameQry);
            if (rs.next()) {
                billContBean.setTreasuryName(rs.getString("TR_NAME"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return billContBean;
    }

    @Override
    public List getBillContributionRepotScheduleEmpList(String annexure, String billno, int aqYear, int aqMonth) {

        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        ArrayList billContScheduleList = new ArrayList();
        BillContributionRepotBean billContBean = null;
        String arrearAmt = null;
        String arrearInst = null;
        int gtotal = 0;
        int slno = 0;
        int cpfPlusGcpf = 0;
        int totCpfPlusGcpf = 0;
        int annexeTotalGcpf = 0;
        int annexeTotalCpf = 0;
        double basic = 0.0;
        double gp = 0.0;
        double da = 0.0;
        double cpf = 0.0;
        double cp = 0.0;
        double gcpf = 0.0;
        double total = 0.0;
        double totalannex3 = 0.0;
        double grandtotal = 0;
        String aqDTLS = "AQ_DTLS";
        int cpfCaryFrd = 0;
        int totCaryFrd = 0;
        double anx3CaryFrd = 0;

        try {
            con = dataSource.getConnection();
            stmt = con.createStatement();
            if ((aqYear == 2017 && aqMonth < 2) || aqYear < 2017) {
                aqDTLS = "hrmis.AQ_DTLS1";
            }

            String query = "SELECT TAB1.*,G_POST.POST  FROM(SELECT AQ_MAST.*,G_SPC.GPC  FROM (SELECT CUR_SPC, GPF_ACC_NO,AQ_MAST.EMP_CODE,AQ_MAST.EMP_NAME,"
                    + "AQ_MAST.CUR_DESG,AQ_MAST.BANK_ACC_NO,AQ_MAST.CUR_BASIC,AQ_MAST.AQSL_NO,POST_SL_NO FROM AQ_MAST WHERE BILL_NO='" + billno + "' "
                    + "AND ACCT_TYPE='PRAN' ORDER BY EMP_NAME)AQ_MAST left outer JOIN G_SPC ON G_SPC.SPC=AQ_MAST.CUR_SPC)TAB1 LEFT OUTER JOIN G_POST "
                    + "ON G_POST.POST_CODE=TAB1.GPC";
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                billContBean = new BillContributionRepotBean();
                slno++;
                billContBean.setSlno(slno);
                billContBean.setEmpname(rs.getString("EMP_NAME"));

                if (rs.getString("POST") != null && !rs.getString("POST").equals("")) {
                    billContBean.setEmpdesg(rs.getString("POST"));
                }
                billContBean.setGpfNo(rs.getString("GPF_ACC_NO"));

                billContBean.setEmpBasicSal(rs.getString("CUR_BASIC"));
                billContBean.setEmpGradepay(new CommonScheduleMethods().getGradePay(rs.getString("AQSL_NO"), con, aqDTLS, aqYear, aqMonth));
                billContBean.setEmpPersonalpay(new CommonScheduleMethods().getPpay(rs.getString("AQSL_NO"), con, aqDTLS, aqYear, aqMonth));
                billContBean.setEmpDearnespay(new CommonScheduleMethods().getDearnessAllowence(rs.getString("AQSL_NO"), con, aqDTLS, aqYear, aqMonth));

                String cpfCF = new CommonScheduleMethods().getCpf(rs.getString("AQSL_NO"), con, aqDTLS, aqYear, aqMonth);
                if (cpfCF != null && !cpfCF.equals("")) {
                    cpfCaryFrd = cpfCaryFrd + Integer.parseInt(cpfCF);
                } else {
                    cpfCaryFrd = cpfCaryFrd;
                }
                billContBean.setCpfCaryFrd(String.valueOf(cpfCaryFrd));
                billContBean.setEmpCpf(cpfCF);

                arrearAmt = new CommonScheduleMethods().getNpsl(rs.getString("AQSL_NO"), con, aqDTLS, aqYear, aqMonth);
                arrearInst = new CommonScheduleMethods().getArrInstalment(rs.getString("AQSL_NO"), con, aqDTLS, aqYear, aqMonth);
                if (billContBean.getEmpCpf() != null) {
                    if (arrearAmt != null) {
                        gtotal = Integer.parseInt(billContBean.getEmpCpf()) + Integer.parseInt(arrearAmt);
                    } else {
                        gtotal = Integer.parseInt(billContBean.getEmpCpf());
                    }
                }
                totCaryFrd = totCaryFrd + gtotal;
                billContBean.setTotCaryFrd(String.valueOf(totCaryFrd));
                billContBean.setEmpGcpf("" + gtotal);

                if (billContBean.getEmpCpf() != null && billContBean.getEmpGcpf() != null) {
                    cpfPlusGcpf = Integer.parseInt(billContBean.getEmpCpf()) + Integer.parseInt(billContBean.getEmpGcpf());
                    billContBean.setCpfPlusGcpf(cpfPlusGcpf + "");
                    totCpfPlusGcpf = totCpfPlusGcpf + cpfPlusGcpf;
                }
                billContBean.setCpfPlusGcpf(cpfPlusGcpf + "");
                billContBean.setTotCpfPlusGcpf(totCpfPlusGcpf + "");
                cpfPlusGcpf = 0;
                gtotal = 0;

                if (billContBean.getEmpGcpf() != null) {
                    annexeTotalGcpf = annexeTotalGcpf + Integer.parseInt(billContBean.getEmpGcpf());
                }
                billContBean.setTotGcpf("" + annexeTotalGcpf);

                if (annexeTotalGcpf > 0) {
                    billContBean.setTotGcpfWord(Numtowordconvertion.convertNumber(annexeTotalGcpf).toUpperCase());
                }
                if (billContBean.getEmpCpf() != null) {
                    annexeTotalCpf = annexeTotalCpf + Integer.parseInt(billContBean.getEmpCpf());
                }
                billContBean.setTotCpf("" + annexeTotalCpf);

                if (annexeTotalCpf > 0) {
                    billContBean.setTotCpfWord(Numtowordconvertion.convertNumber(annexeTotalCpf).toUpperCase());
                }
                if (billContBean.getEmpBasicSal() != null && !billContBean.getEmpBasicSal().equals("")) {
                    basic = Integer.parseInt(billContBean.getEmpBasicSal());
                }
                if (billContBean.getEmpGradepay() != null && !billContBean.getEmpGradepay().equals("")) {
                    gp = Integer.parseInt(billContBean.getEmpGradepay());
                }
                if (billContBean.getEmpDearnespay() != null && !billContBean.getEmpDearnespay().equals("")) {
                    da = Integer.parseInt(billContBean.getEmpDearnespay());
                }
                if (billContBean.getEmpCpf() != null) {
                    cpf = Integer.parseInt(billContBean.getEmpCpf());
                } else {
                    billContBean.setEmpCpf("" + cp);
                }
                if (billContBean.getEmpGcpf() != null) {
                    gcpf = Integer.parseInt(billContBean.getEmpGcpf());
                } else {
                    billContBean.setEmpGcpf("" + cp);
                }
                total = basic + gp + da;
                billContBean.setTotal("" + total);

                if (arrearAmt != null) {
                    totalannex3 = cpf + gcpf + Integer.parseInt(arrearAmt);
                } else {
                    totalannex3 = cpf + gcpf;
                }
                if (totalannex3 != 0.0) {
                    billContBean.setTotalAnnexure3("" + totalannex3);
                } else {
                    billContBean.setTotalAnnexure3("" + 0.0);
                }
                anx3CaryFrd = anx3CaryFrd + totalannex3;
                billContBean.setAnx3CaryFrd("" + anx3CaryFrd);
                billContBean.setTotalAnnexure3("" + totalannex3);
                cpf = 0;
                if (arrearInst != null) {
                    billContBean.setArrInstalment(arrearInst);
                } else {
                    billContBean.setArrInstalment("0");
                }
                if (arrearAmt != null) {
                    billContBean.setArrearAmt(arrearAmt);
                } else {
                    billContBean.setArrearAmt("0");
                }
                if (billContBean.getTotCpf() != null) {
                    billContBean.setGrandTotal("" + Integer.parseInt(billContBean.getTotCpf()));
                }
                if (billContBean.getTotGcpf() != null) {
                    grandtotal = grandtotal + totalannex3;
                    billContBean.setGrandTotal("" + grandtotal);
                }

                if (slno % 13 == 0) {
                    billContBean.setPagebreakAnx("<input type=\"button\" name=\"pagebreak1\" style=\"page-break-before: always;width: 0;height: 0\"/>");
                    billContBean.setPageHeaderAnx(reportPageHeader(con, annexure, null, billno, "") + "");
                } else {
                    billContBean.setPagebreakAnx("");
                    billContBean.setPageHeaderAnx("");
                }

                billContScheduleList.add(billContBean);
                gp = 0.0;
                da = 0.0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return billContScheduleList;
    }

    @Override
    public BillBackPageBean getBillBackPgScheduleHeaderDetails(String billno, int aqYear, int aqMonth) {

        Connection con = null;
        ResultSet rs = null;
        Statement stmt = null;
        long tot = 0;
        BillBackPageBean backPageBean = new BillBackPageBean();
        try {
            con = dataSource.getConnection();
            stmt = con.createStatement();

            Map<String, Integer> map = new HashMap<String, Integer>();
            map = getAllowanceSum(stmt, billno, aqYear, aqMonth);
            Iterator<Map.Entry<String, Integer>> entries = map.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<String, Integer> entry = entries.next();
                String keySchdule = entry.getKey();
                int valueSumAMt = entry.getValue();

                if (keySchdule.equals("DA")) {
                    tot = tot + valueSumAMt;

                } else if (keySchdule.equals("GP")) {
                    tot = tot + valueSumAMt;

                } else if (keySchdule.equals("PPAY")) {
                    tot = tot + valueSumAMt;

                } else if (keySchdule.equals("HRA")) {
                    tot = tot + valueSumAMt;

                } else if (keySchdule.equals("OA")) {
                    tot = tot + valueSumAMt;
                }
            }

            backPageBean.setTxtPay(new CommonScheduleMethods().getPayAmt(con, billno));
            backPageBean.setTxtDearnessPay(new CommonScheduleMethods().getDearnessPay(con, billno));

            if (backPageBean.getTxtPay() != null && !backPageBean.getTxtPay().equals("")) {
                tot = tot + Long.parseLong(backPageBean.getTxtPay());
            }
            if (backPageBean.getTxtDearnessPay() != null && !backPageBean.getTxtDearnessPay().equals("")) {
                tot = tot + Long.parseLong(backPageBean.getTxtDearnessPay());
            }
            backPageBean.setTotalPaise(tot + "");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return backPageBean;
    }

    public Map getAllowanceSum(Statement stmt, String billno, int aqYear, int aqMonth) {

        String aqDtlsTbl = "AQ_DTLS";
        Map<String, Integer> map = new HashMap<String, Integer>();
        try {
            if ((aqYear == 2017 && aqMonth < 2) || aqYear < 2017) {
                aqDtlsTbl = "hrmis.AQ_DTLS1";
            }
            String qry = "select schedule, sum(ad_amt) sumAmt from " + aqDtlsTbl + " a, aq_mast b where a.aqsl_no=b.aqsl_no and a.aq_year=b.aq_year and "
                    + "a.aq_month=b.aq_month and a.aq_month= '" + aqMonth + "' and a.aq_year= '" + aqYear + "' and b.bill_no='" + billno + "' group by schedule";
            ResultSet rs = stmt.executeQuery(qry);
            while (rs.next()) {
                map.put(rs.getString("schedule"), rs.getInt("sumAmt"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public List getBillBackPgScheduleEmpList(String billno, int aqYear, int aqMonth) {

        Connection con = null;
        ResultSet rs = null;
        Statement stmt = null;
        ArrayList backPageScheduleList = new ArrayList();
        BillBackPageBean backPageBean = null;
        String aqDtlsTbl = "AQ_DTLS";
        try {
            con = dataSource.getConnection();
            stmt = con.createStatement();
            if ((aqYear == 2017 && aqMonth < 2) || aqYear < 2017) {
                aqDtlsTbl = "hrmis.AQ_DTLS1";
            }
            String billBackPgQry = "select schedule, now_dedn,sum(ad_amt) totSum, bt_id from " + aqDtlsTbl + " a, aq_mast b "
                    + "where a.aqsl_no=b.aqsl_no and a.aq_year=b.aq_year and a.aq_month=b.aq_month "
                    + "and a.aq_month='" + aqMonth + "' and a.aq_year='" + aqYear + "' and b.bill_no='" + billno + "' and ad_type='D' and schedule != 'PVTL' "
                    + "and schedule!='PVTD' and ad_amt >0 GROUP BY a.schedule, ded_type, now_dedn, ad_code, bt_id";
            rs = stmt.executeQuery(billBackPgQry);
            while (rs.next()) {
                String schedule = rs.getString("SCHEDULE");
                if (schedule.equals("GPF")) {
                    backPageBean = new BillBackPageBean();
                    backPageBean.setScheduleName("GPF");
                    backPageBean.setObjectHead(StringUtils.defaultString(rs.getString("BT_ID")));
                    backPageBean.setSchAmount(rs.getString("totSum"));

                } else if (schedule.equals("GA")) {
                    backPageBean = new BillBackPageBean();
                    backPageBean.setScheduleName("GA");
                    backPageBean.setObjectHead(StringUtils.defaultString(rs.getString("BT_ID")));
                    backPageBean.setSchAmount(rs.getString("totSum"));

                } else if (schedule.equals("TPF") || schedule.equals("TPFGA")) {
                    backPageBean = new BillBackPageBean();
                    backPageBean.setScheduleName("TPF");
                    backPageBean.setObjectHead(StringUtils.defaultString(rs.getString("BT_ID")));
                    backPageBean.addSchAmount(rs.getInt("totSum"));

                } else if (schedule.equals("CPF") || schedule.equals("NPSL")) {
                    backPageBean = new BillBackPageBean();
                    backPageBean.setScheduleName("CPF");
                    backPageBean.setObjectHead(StringUtils.defaultString(rs.getString("BT_ID")));
                    backPageBean.addSchAmount(rs.getInt("totSum"));

                } else {
                    backPageBean = new BillBackPageBean();
                    backPageBean.setObjectHead(StringUtils.defaultString(rs.getString("BT_ID")));
                    if (rs.getString("NOW_DEDN") != null && !rs.getString("NOW_DEDN").equals("")) {
                        backPageBean.setScheduleName(rs.getString("SCHEDULE") + " (" + rs.getString("NOW_DEDN") + ")");
                    } else {
                        backPageBean.setScheduleName(rs.getString("SCHEDULE"));
                    }
                    backPageBean.setSchAmount(rs.getString("totSum"));
                }
                backPageScheduleList.add(backPageBean);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, stmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return backPageScheduleList;
    }

    @Override
    public BankAcountScheduleBean getBankAcountScheduleHeaderDetails(String billno) {

        Connection con = null;
        BankAcountScheduleBean bankAcountBean = new BankAcountScheduleBean();
        try {
            con = dataSource.getConnection();

            PayrollCommonFunction prcf = new PayrollCommonFunction();
            CommonReportParamBean bean = prcf.getCommonReportParameter(con, billno);

            bankAcountBean.setBilldesc(bean.getBilldesc());
            bankAcountBean.setDdoDesg(bean.getDdoname());
            bankAcountBean.setOffName(bean.getOfficename());
            bankAcountBean.setMonth(CalendarCommonMethods.getFullMonthAsString(bean.getAqmonth()));
            bankAcountBean.setYear(bean.getAqyear() + "");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return bankAcountBean;
    }

    @Override
    public List getBankAcountScheduleEmpList(String billno, int aqYear, int aqMonth) {

        Connection con = null;
        ResultSet rs = null;
        Statement stmt = null;
        ArrayList bankAccScheduleList = new ArrayList();
        BankAcountScheduleBean bankAcountBean = null;
        int pagetotal = 0;
        int pagetotalddo = 0;
        int slno = 0;
        int netCfTot = 0;
        String aqDTLS = "AQ_DTLS";
        try {
            con = dataSource.getConnection();
            stmt = con.createStatement();

            if ((aqYear == 2017 && aqMonth < 2) || aqYear < 2017) {
                aqDTLS = "hrmis.AQ_DTLS1";
            }

            String bankQuery = "SELECT AQSL_NO,EMP_NAME,GPF_ACC_NO,CUR_DESG,PAY_SCALE,BANK_ACC_NO,CUR_BASIC,SPC_ORD_NO,SPC_ORD_DATE,"
                    + "GPF_ACC_NO FROM AQ_MAST WHERE BILL_NO='" + billno + "' AND EMP_NAME IS NOT NULL order by post_sl_no";
            rs = stmt.executeQuery(bankQuery);
            while (rs.next()) {
                bankAcountBean = new BankAcountScheduleBean();
                slno++;
                bankAcountBean.setSlno(slno);
                bankAcountBean.setDesignation(rs.getString("CUR_DESG"));
                bankAcountBean.setAccountNo(rs.getString("BANK_ACC_NO"));
                bankAcountBean.setEmpname(rs.getString("EMP_NAME"));
                bankAcountBean.setGpfNo(rs.getString("GPF_ACC_NO"));

                int privatededuction = new CommonScheduleMethods().getPrivateDeductionLoanForEmp(con, rs.getString("AQSL_NO"), aqDTLS, aqYear, aqMonth);
                int privateloan = new CommonScheduleMethods().getPrivateLoan(con, rs.getString("AQSL_NO"), aqDTLS, aqYear, aqMonth);
                int grosspay = new CommonScheduleMethods().getGrossPay(con, rs.getString("AQSL_NO"), aqDTLS, aqYear, aqMonth);
                int totaldedn = new CommonScheduleMethods().getTotalDedn(con, rs.getString("AQSL_NO"), aqDTLS, aqYear, aqMonth);

                int netPay = grosspay - totaldedn;
                pagetotal = (pagetotal + netPay) - (privatededuction + privateloan);
                pagetotalddo = pagetotalddo + privatededuction + privateloan;

                bankAcountBean.setNetAmount(netPay + "");
                netCfTot = netCfTot + netPay;
                bankAcountBean.setNetCfTot(String.valueOf(netCfTot));

                bankAcountBean.setTowardsLoan(privatededuction + privateloan);
                bankAcountBean.setTotalReleased(grosspay - (totaldedn + privatededuction + privateloan));
                bankAcountBean.setOtherDeposits(privatededuction + privateloan);

                bankAcountBean.setCarryForward(pagetotal + "");
                bankAcountBean.setCarryForwardDDO(pagetotalddo + "");

                bankAccScheduleList.add(bankAcountBean);

                if (slno % 13 == 0) {
                    bankAcountBean.setPagebreakBS("<input type=\"button\" name=\"pagebreak1\" style=\"page-break-before: always;width: 0;height: 0\"/>");
                    bankAcountBean.setPageHeaderBS(reportPageHeader(con, "BS", null, billno, "") + "");
                } else {
                    bankAcountBean.setPagebreakBS("");
                    bankAcountBean.setPageHeaderBS("");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, stmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return bankAccScheduleList;
    }

    @Override
    public List getBankNameScheduleList(String billno, int aqYear, int aqMonth) {

        Connection con = null;
        ResultSet rs = null;
        Statement stmt = null;
        ArrayList bankNameScheduleList = new ArrayList();
        BankAcountScheduleBean bankAcountBean = null;
        String totamt = "0";
        String aqDTLS = "AQ_DTLS";

        try {
            con = dataSource.getConnection();
            stmt = con.createStatement();
            if ((aqYear == 2017 && aqMonth < 2) || aqYear < 2017) {
                aqDTLS = "hrmis.AQ_DTLS1";
            }

            String bankQry = "SELECT BANK_NAME from AQ_MAST WHERE BILL_NO='" + billno + "' and DEFAULT_BANK= 1 OR DEFAULT_BANK = 0 GROUP BY BANK_NAME";
            rs = stmt.executeQuery(bankQry);
            while (rs.next()) {
                bankAcountBean = new BankAcountScheduleBean();

                if (rs.getString("BANK_NAME") != null && !rs.getString("BANK_NAME").equals("")) {
                    bankAcountBean.setBankName(rs.getString("BANK_NAME"));
                    totamt = new CommonScheduleMethods().getEmpSalDetails(con, rs.getString("BANK_NAME"), billno, true, aqDTLS, aqYear, aqMonth);
                } else {
                    bankAcountBean.setBankName("DDO Current");
                    totamt = new CommonScheduleMethods().getEmpSalDetails(con, null, billno, false, aqDTLS, aqYear, aqMonth);
                }
                bankAcountBean.setNetAmt(totamt);
                bankNameScheduleList.add(bankAcountBean);
            }
            String arr[] = new CommonScheduleMethods().getTotalAmount(con, billno, aqDTLS, aqYear, aqMonth);
            bankAcountBean.setNetAmtNumbers(arr[0]);
            bankAcountBean.setAmountInWords(Numtowordconvertion.convertNumber((int) Double.parseDouble(bankAcountBean.getNetAmtNumbers())));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, stmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return bankNameScheduleList;
    }

    @Override
    public AbsenteeStatementScheduleBean getAbsntStmtScheduleHeaderDetails(String billno) {

        Connection con = null;
        AbsenteeStatementScheduleBean absStmtBean = new AbsenteeStatementScheduleBean();
        String btid = "";
        try {
            con = dataSource.getConnection();

            PayrollCommonFunction prcf = new PayrollCommonFunction();
            CommonReportParamBean bean = prcf.getCommonReportParameter(con, billno);

            absStmtBean.setBillNo(billno);
            absStmtBean.setOffName(bean.getOfficename());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return absStmtBean;
    }

    @Override
    public List getAbsntStmtScheduleEmpList(String billno) {

        Connection con = null;
        ResultSet rs = null;
        Statement stmt = null;
        ArrayList absStmtScheduleList = new ArrayList();
        AbsenteeStatementScheduleBean absStmtBean = null;
        int cnt = 0;
        int cnt1 = 0;

        try {
            con = dataSource.getConnection();
            stmt = con.createStatement();
            String absStmtQuery = "SELECT OFF_CODE,CUR_DESG,COUNT(*) CNT,PAY_SCALE,OFF_CODE FROM AQ_MAST WHERE BILL_NO='" + billno + "' AND EMP_CODE IS NULL "
                    + "GROUP BY CUR_DESG,PAY_SCALE,OFF_CODE";
            rs = stmt.executeQuery(absStmtQuery);
            while (rs.next()) {
                absStmtBean = new AbsenteeStatementScheduleBean();

                String desg = rs.getString("CUR_DESG");
                StringTokenizer stringTokenizer = new StringTokenizer(desg, ",");
                if (stringTokenizer.hasMoreTokens()) {
                    desg = stringTokenizer.nextToken().trim();
                }
                absStmtBean.setDesignation(desg);

                cnt = rs.getInt("CNT");
                cnt1 = cnt1 + cnt;
                absStmtBean.setPostno(cnt);
                if (rs.getString("PAY_SCALE") != null && !rs.getString("PAY_SCALE").equals("")) {
                    absStmtBean.setPayscale(rs.getString("PAY_SCALE"));
                } else {
                    absStmtBean.setPayscale("--");
                }
                absStmtBean.setGrandTotal(cnt1);

                absStmtScheduleList.add(absStmtBean);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, stmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return absStmtScheduleList;
    }

    @Override
    public LicScheduleBean getLICScheduleHeaderDetails(String billno) {

        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        Statement stmt1 = null;
        LicScheduleBean licBean = new LicScheduleBean();

        try {
            con = dataSource.getConnection();
            stmt = con.createStatement();
            stmt1 = con.createStatement();

            PayrollCommonFunction prcf = new PayrollCommonFunction();
            CommonReportParamBean bean = prcf.getCommonReportParameter(con, billno);

            licBean.setBilldesc(bean.getBilldesc());
            licBean.setOffName(bean.getOfficename());
            licBean.setAqYear(bean.getAqyear() + "");
            licBean.setDdoDesg(new CommonScheduleMethods().getDDODesignationList(stmt1, billno));
            licBean.setMonthYear(new CommonScheduleMethods().getMonthAndYear(con, billno));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, stmt);
            DataBaseFunctions.closeSqlObjects(stmt1);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return licBean;
    }

    @Override
    public List getLICScheduleEmpList(String billno, int aqMonth, int aqYear) {

        Connection con = null;
        ResultSet rs = null;
        Statement stmt = null;
        ResultSet rs1 = null;
        Statement stmt1 = null;

        ArrayList licScheduleList = new ArrayList();

        String query1 = "";
        LicScheduleBean licBean = null;
        LicSchedulePolicyBean licPolicyBean = null;
        int totAmount = 0;
        int carryForwardTotal = 0;
        String gross = "";
        String aqDtlsTbl = "";
        int slno = 0;
        try {
            con = dataSource.getConnection();
            stmt = con.createStatement();
            aqDtlsTbl = getAqDtlsTableName(billno);

            String licQuery = "SELECT DISTINCT MAST.AQSL_NO,MAST.EMP_CODE,EMP_NAME,CUR_DESG,MAST.POST_SL_NO FROM AQ_MAST MAST "
                    + "INNER JOIN " + aqDtlsTbl + " DTLS ON MAST.AQSL_NO=DTLS.AQSL_NO AND DTLS.SCHEDULE='LIC' AND DTLS.AD_TYPE='D' "
                    + "WHERE BILL_NO=" + billno + " and DTLS.aq_month=" + aqMonth + " and DTLS.aq_year=" + aqYear + " ORDER BY MAST.POST_SL_NO";
            rs = stmt.executeQuery(licQuery);
            while (rs.next()) {
                slno++;
                licBean = new LicScheduleBean();

                licBean.setSlno(slno);
                String aqslno = rs.getString("AQSL_NO");
                licBean.setAqSlno(aqslno);
                String empCode = rs.getString("EMP_CODE");
                licBean.setEmpcode(empCode);
                licBean.setEmpname(rs.getString("EMP_NAME"));
                licBean.setEmpdesg(rs.getString("CUR_DESG"));
                String postSlNo = rs.getString("POST_SL_NO");

                ArrayList licSchedulePoicyList = new ArrayList();
                totAmount = 0;
                query1 = "SELECT ACC_NO,REF_DESC,AD_AMT FROM " + aqDtlsTbl + " WHERE SCHEDULE='LIC' AND EMP_CODE='" + empCode + "' AND AQSL_NO='" + aqslno + "' AND AQ_MONTH=" + aqMonth + " AND AQ_YEAR=" + aqYear;
                stmt1 = con.createStatement();
                rs1 = stmt1.executeQuery(query1);
                while (rs1.next()) {

                    licPolicyBean = new LicSchedulePolicyBean();

                    licPolicyBean.setPolicyNo(rs1.getString("ACC_NO"));
                    licPolicyBean.setRecoveryMonth(rs1.getString("REF_DESC"));
                    licPolicyBean.setAmount(rs1.getString("AD_AMT"));
                    totAmount = totAmount + rs1.getInt("AD_AMT");

                    carryForwardTotal = carryForwardTotal + rs1.getInt("AD_AMT");
                    licSchedulePoicyList.add(licPolicyBean);
                    licBean.setPremiumDetails(licSchedulePoicyList);
                }
                licBean.setTotal(totAmount + "");
                licBean.setCarryForward(carryForwardTotal + "");

                licScheduleList.add(licBean);

                if (slno % 15 == 0) {
                    licBean.setPagebreakLIC("<input type=\"button\" name=\"pagebreak1\" style=\"page-break-before: always;width: 0;height: 0\"/>");
                    licBean.setPageHeaderLIC(reportPageHeader(con, "LIC", null, billno, "") + "");
                } else {
                    licBean.setPagebreakLIC("");
                    licBean.setPageHeaderLIC("");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, stmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return licScheduleList;
    }

    @Override
    public PLIScheduleBean getPLIScheduleHeaderDetails(String billno) {

        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        PLIScheduleBean pliBean = new PLIScheduleBean();

        try {
            con = dataSource.getConnection();
            stmt = con.createStatement();
            PayrollCommonFunction prcf = new PayrollCommonFunction();
            CommonReportParamBean bean = prcf.getCommonReportParameter(con, billno);

            pliBean.setBilldesc(bean.getBilldesc());
            pliBean.setOfficeName(bean.getOfficename());
            pliBean.setMonthYear(new CommonScheduleMethods().getMonthAndYear(con, billno));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, stmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return pliBean;
    }

    @Override
    public List getPLIScheduleEmpList(String billno) {

        Connection con = null;
        ResultSet rs = null;
        Statement stmt = null;
        Statement stmt1 = null;
        ArrayList pliScheduleList = new ArrayList();
        PLIScheduleBean pliBean = null;
        String aqDtlsTbl = "";
        int slNo = 0;
        double amt = 0.0;
        double total = 0.0;
        int i = 1;
        int j = 0;
        double carryForward = 0.0;
        String test = "";
        try {
            con = dataSource.getConnection();
            stmt = con.createStatement();
            stmt1 = con.createStatement();

            aqDtlsTbl = getAqDtlsTableName(billno);
            String pliQuery = "SELECT  MAST.AQSL_NO,MAST.EMP_CODE,CUR_DESG,EMP_NAME,DTLS.ACC_NO,DTLS.AD_AMT,DTLS.REF_DESC FROM AQ_MAST MAST "
                    + "INNER JOIN " + aqDtlsTbl + " DTLS ON MAST.AQSL_NO=DTLS.AQSL_NO AND DTLS.SCHEDULE='PLI' AND DTLS.AD_TYPE='D' "
                    + "WHERE BILL_NO='" + billno + "' ORDER BY MAST.POST_SL_NO";
            rs = stmt.executeQuery(pliQuery);
            while (rs.next()) {
                slNo++;
                pliBean = new PLIScheduleBean();

                pliBean.setSlno(slNo);
                pliBean.setEmpname(rs.getString("EMP_NAME"));
                pliBean.setEmpdesg(rs.getString("CUR_DESG"));
                String policyNo = rs.getString("ACC_NO");
                pliBean.setPolicyNo(policyNo);
                pliBean.setRecMonth(rs.getString("REF_DESC"));
                pliBean.setAmount(rs.getString("AD_AMT"));

                amt = rs.getDouble("AD_AMT");
                carryForward = carryForward + amt;
                pliBean.setCarryForward(carryForward + "");
                test = Numtowordconvertion.convertNumber((int) carryForward);
                pliBean.setTotFig(test);
                total = new CommonScheduleMethods().getTotalValue(stmt1, policyNo, rs.getString("EMP_CODE"), "PLI");
                pliBean.setTotal(total + "");

                pliScheduleList.add(pliBean);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, stmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return pliScheduleList;
    }

    @Override
    public GisAndFaScheduleBean getGisAndFaScheduleHeaderDetails(String schedule, String billno) {

        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        GisAndFaScheduleBean gisAndFaBean = new GisAndFaScheduleBean();

        try {
            con = dataSource.getConnection();
            stmt = con.createStatement();

            PayrollCommonFunction prcf = new PayrollCommonFunction();
            CommonReportParamBean bean = prcf.getCommonReportParameter(con, billno);

            gisAndFaBean.setReportName(prcf.getReportName(con, schedule));
            gisAndFaBean.setDeptName(bean.getDeptname());
            gisAndFaBean.setOffName(bean.getOfficename());
            gisAndFaBean.setBilldesc(bean.getBilldesc());

            gisAndFaBean.setDdoName(bean.getDdoname());
            gisAndFaBean.setSchedule(schedule);

            String gisAndFaQryHdr = "SELECT * FROM (select TR_CODE,AQ_MONTH,AQ_YEAR,OFF_DDO from BILL_MAST where BILL_NO='" + billno + "') BILL_MAST left outer join"
                    + " (SELECT POST_CODE,POST FROM G_POST) G_POST on BILL_MAST.OFF_DDO=G_POST.POST_CODE left outer join "
                    + "(SELECT TR_CODE,TR_NAME FROM G_TREASURY) G_TREASURY on BILL_MAST.TR_CODE=G_TREASURY.TR_CODE ";
            rs = stmt.executeQuery(gisAndFaQryHdr);
            while (rs.next()) {
                gisAndFaBean.setTreasuryName(rs.getString("TR_NAME"));
                gisAndFaBean.setDdoDesg(rs.getString("POST"));
                gisAndFaBean.setRecYear(rs.getString("AQ_YEAR"));
                String month = rs.getString("AQ_MONTH");
                gisAndFaBean.setRecMonth(month);
                if (month != null && !month.equals("")) {
                    gisAndFaBean.setRecMonth(CalendarCommonMethods.getFullMonthAsString(Integer.parseInt(month)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, stmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return gisAndFaBean;
    }

    @Override
    public List getGISandFAScheduleEmpList(String schedule, String billno, int aqYear, int aqMonth) {

        Connection con = null;
        ResultSet rs = null;
        Statement stmt = null;
        ArrayList gisAndFaScheduleList = new ArrayList();
        GisAndFaScheduleBean gisAndFaBean = null;
        int slNo = 1;
        int amt1 = 0;
        int amt2 = 0;
        int tot = 0;
        String aqDTLS = "AQ_DTLS";
        int cfAmt = 0;
        try {
            con = dataSource.getConnection();
            stmt = con.createStatement();
            if ((aqYear == 2017 && aqMonth < 2) || aqYear < 2017) {
                aqDTLS = "hrmis.AQ_DTLS1";
            }
            String gisAndFaQry = "SELECT A.EMP_NAME empname,A.CUR_DESG designation,A.BANK_ACC_NO ACCOUNTNO,A.AQSL_NO,POST_SL_NO,GPF_ACC_NO,"
                    + "A.EMP_CODE, B.REF_ORD,AD_CODE,SCHEDULE,B.ad_amt deductedamt,TOT_REC_AMT,B.acc_no, B.ref_desc, B.ad_ref_id,"
                    + "LOANID,VCH_NO,VCH_DATE,DED_ST_DATE,P_ORG_AMT,I_ORG_AMT, P_INSTL_AMT,I_INSTL_AMT,C.I_LAST_INSTL_NO,C.P_LAST_INSTL_NO,LOAN_TP,"
                    + "P_CUM_RECOVERED,I_CUM_RECOVERED FROM AQ_MAST A, " + aqDTLS + " B, EMP_LOAN_SANC C WHERE A.AQSL_NO=B.AQSL_NO AND A.AQ_MONTH=B.AQ_MONTH "
                    + "AND A.AQ_YEAR=B.AQ_YEAR AND B.aq_month = " + aqMonth + " and B.aq_year = " + aqYear + " AND B.AD_REF_ID = C.LOANID AND "
                    + "A.BILL_NO = '" + billno + "' AND B.AD_AMT>0 AND B.SCHEDULE = '" + schedule + "' ORDER BY POST_SL_NO";

            rs = stmt.executeQuery(gisAndFaQry);
            while (rs.next()) {
                gisAndFaBean = new GisAndFaScheduleBean();

                gisAndFaBean.setSlno(Integer.parseInt(slNo + ""));
                gisAndFaBean.setEmpname(rs.getString("empname"));
                gisAndFaBean.setCurDesg(rs.getString("designation"));
                gisAndFaBean.setRemark(rs.getString("GPF_ACC_NO"));
                gisAndFaBean.setTreasuryVoucherNo(rs.getString("VCH_NO"));
                gisAndFaBean.setTreasuryVoucherDate("");
                gisAndFaBean.setAccountno(rs.getString("ACC_NO"));

                if (rs.getString("I_ORG_AMT") != null && !rs.getString("I_ORG_AMT").equals("")) {
                    gisAndFaBean.setOriginalAmount(rs.getString("I_ORG_AMT"));
                }
                if (rs.getString("P_ORG_AMT") != null && !rs.getString("P_ORG_AMT").equals("")) {
                    gisAndFaBean.setOriginalAmount(rs.getString("P_ORG_AMT"));
                }
                if (rs.getString("I_LAST_INSTL_NO") != null && !rs.getString("I_LAST_INSTL_NO").equals("")) {
                    gisAndFaBean.setNoofInstallment(rs.getString("I_LAST_INSTL_NO"));
                }
                int instalment_of_recovery = rs.getInt("P_LAST_INSTL_NO") + 1;
                if (rs.getString("P_LAST_INSTL_NO") != null && !rs.getString("P_LAST_INSTL_NO").equals("")) {
                    gisAndFaBean.setNoofInstallment(rs.getString("REF_DESC"));
                }
                String dedAmt = rs.getString("deductedamt");
                gisAndFaBean.setDeductedAmount(dedAmt);
                cfAmt = cfAmt + Integer.parseInt(dedAmt);
                gisAndFaBean.setCarryFrdAmt(cfAmt);

                int cummulative_recovery = 0;
                if (rs.getString("TOT_REC_AMT") != null && !rs.getString("TOT_REC_AMT").equals("")) {
                    cummulative_recovery = rs.getInt("TOT_REC_AMT");
                    gisAndFaBean.setRecoveryUptoMonth(cummulative_recovery + "");
                }
                if (rs.getString("P_ORG_AMT") != null && !rs.getString("P_ORG_AMT").equals("")) {
                    amt1 = Integer.parseInt(rs.getString("P_ORG_AMT"));
                }
                if (rs.getString("I_ORG_AMT") != null && !rs.getString("I_ORG_AMT").equals("")) {
                    amt1 = Integer.parseInt(rs.getString("I_ORG_AMT"));
                }

                if (rs.getString("P_CUM_RECOVERED") != null && !rs.getString("P_CUM_RECOVERED").equals("")) {
                    amt2 = Integer.parseInt(rs.getString("P_CUM_RECOVERED"));
                }
                if (rs.getString("I_CUM_RECOVERED") != null && !rs.getString("I_CUM_RECOVERED").equals("")) {
                    amt2 = Integer.parseInt(rs.getString("I_CUM_RECOVERED"));
                }

                int bal = amt1 - cummulative_recovery;
                gisAndFaBean.setBalance(bal + "");

                if (rs.getString("DED_ST_DATE") != null && !rs.getString("DED_ST_DATE").equals("")) {
                    gisAndFaBean.setMonthdrawn(CommonFunctions.getFormattedOutputDate1(rs.getDate("DED_ST_DATE")));
                } else if (rs.getString("VCH_DATE") != null && !rs.getString("VCH_DATE").equals("")) {
                    gisAndFaBean.setMonthdrawn(CommonFunctions.getFormattedOutputDate1(rs.getDate("VCH_DATE")));
                }

                gisAndFaScheduleList.add(gisAndFaBean);
                slNo++;

                if (schedule.equals("FA") || schedule.equals("OR") || schedule.equals("ADVPAY")) {

                    if (slNo % 12 == 0) {
                        gisAndFaBean.setPagebreakFA("<input type=\"button\" name=\"pagebreak1\" style=\"page-break-before: always;width: 0;height: 0\"/>");
                        gisAndFaBean.setPageHeaderFA(reportPageHeader(con, schedule, null, billno, "") + "");
                    } else {
                        gisAndFaBean.setPagebreakFA("");
                        gisAndFaBean.setPageHeaderFA("");
                    }

                } else if (schedule.equals("GISA")) {

                    if (slNo % 7 == 0) {
                        gisAndFaBean.setPagebreakFA("<input type=\"button\" name=\"pagebreak1\" style=\"page-break-before: always;width: 0;height: 0\"/>");
                        gisAndFaBean.setPageHeaderFA(reportPageHeader(con, schedule, null, billno, "") + "");
                    } else {
                        gisAndFaBean.setPagebreakFA("");
                        gisAndFaBean.setPageHeaderFA("");
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, stmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return gisAndFaScheduleList;
    }

    @Override
    public List getLoanAdvanceSchedulePrincipalList(String schedule, String billno, int aqMonth, int aqYear) {

        Connection con = null;
        ResultSet rs = null;
        Statement stmt = null;
        ArrayList hbaSchldList = new ArrayList();
        LoanAdvanceScheduleBean loanBean = null;
        String empNameDesc = "";
        int sno = 1;
        double tot = 0.0;
        double amt1 = 0.0;
        double amt2 = 0.0;
        String drawingmonth = "";
        String drawingmonth1 = "";
        String aqDtlsTbl = "";
        try {
            con = dataSource.getConnection();
            stmt = con.createStatement();
            aqDtlsTbl = getAqDtlsTableName(billno);
            String loanQuery = "SELECT AQ_MAST.GPF_ACC_NO,AQ_MAST.EMP_NAME,AQ_MAST.CUR_DESG,AQ_DTLS.ACC_NO,AQ_DTLS.REF_DESC,AQ_DTLS.AD_AMT,"
                    + "EMP_LOAN_SANC.DED_ST_DATE,EMP_LOAN_SANC.I_LAST_INSTL_NO,EMP_LOAN_SANC.P_LAST_INSTL_NO,EMP_LOAN_SANC.P_CUM_RECOVERED,"
                    + "EMP_LOAN_SANC.I_CUM_RECOVERED,EMP_LOAN_SANC.VCH_NO,EMP_LOAN_SANC.VCH_DATE,EMP_LOAN_SANC.P_ORG_AMT,EMP_LOAN_SANC.I_ORG_AMT,"
                    + "EMP_LOAN_SANC.P_INSTL_AMT,EMP_LOAN_SANC.I_INSTL_AMT,(EMP_LOAN_SANC.P_ORG_AMT-AQ_DTLS.TOT_REC_AMT) BALANCE,POST_SL_NO,TOT_REC_AMT "
                    + "from (Select EMP_NAME,CUR_DESG,AQSL_NO,POST_SL_NO,GPF_ACC_NO from AQ_MAST where AQ_MAST.BILL_NO='" + billno + "'  AND aq_month=" + aqMonth + " AND aq_year=" + aqYear + ") AQ_MAST inner join "
                    + "(select ACC_NO,REF_DESC,AD_AMT,TOT_REC_AMT,AQSL_NO,AD_CODE,EMP_CODE,REF_ORD,REF_DATE,AD_REF_ID from " + aqDtlsTbl + " where "
                    + "SCHEDULE='" + schedule + "'  AND aq_month=" + aqMonth + " AND aq_year=" + aqYear + " and AD_TYPE='D' and DED_TYPE='L' AND AD_AMT >0 AND NOW_DEDN='P') AQ_DTLS on "
                    + "AQ_MAST.AQSL_NO=AQ_DTLS.AQSL_NO inner join EMP_LOAN_SANC on AQ_DTLS.AD_REF_ID=EMP_LOAN_SANC.LOANID  ORDER BY POST_SL_NO";

            rs = stmt.executeQuery(loanQuery);
            while (rs.next()) {
                loanBean = new LoanAdvanceScheduleBean();

                if ((rs.getString("EMP_NAME") + "/\n" + rs.getString("CUR_DESG")).equals(empNameDesc)) {
                    loanBean.setEmpNameDesg("");
                } else {
                    empNameDesc = rs.getString("EMP_NAME") + "/\n" + rs.getString("CUR_DESG");
                    loanBean.setEmpNameDesg(empNameDesc);
                    loanBean.setSlno(sno);
                    sno++;
                }

                if (rs.getString("VCH_NO") != null && !rs.getString("VCH_NO").equals("")) {
                    loanBean.setVchNo(rs.getString("VCH_NO"));
                } else {
                    loanBean.setVchNo("");
                }

                if (rs.getString("VCH_DATE") != null && !rs.getString("VCH_DATE").equals("")) {
                    loanBean.setVchDate(CommonFunctions.getFormattedOutputDate1(rs.getDate("VCH_DATE")));
                } else {
                    loanBean.setVchDate("");
                }

                loanBean.setAccNo(rs.getString("ACC_NO"));
                loanBean.setEmpdesg(rs.getString("CUR_DESG"));

                if (rs.getString("P_ORG_AMT") != null && !rs.getString("P_ORG_AMT").equals("")) {
                    loanBean.setOriginalAmt(rs.getDouble("P_ORG_AMT"));
                }
                //piadv.setInstalmentRec(rs2.getString("REF_DESC"));
                if (rs.getString("I_LAST_INSTL_NO") != null && !rs.getString("I_LAST_INSTL_NO").equals("")) {
                    loanBean.setInstalmentRec(rs.getString("REF_DESC"));
                }
                if (rs.getString("REF_DESC") != null && !rs.getString("REF_DESC").equals("")) {
                    loanBean.setInstalmentRec(rs.getString("REF_DESC"));
                }
                loanBean.setDecutedAmt(rs.getDouble("AD_AMT"));
                tot = tot + loanBean.getDecutedAmt();
                loanBean.setTotal(tot);

                if (rs.getString("TOT_REC_AMT") != null && !rs.getString("TOT_REC_AMT").equals("")) {
                    loanBean.setRecAmt(rs.getDouble("TOT_REC_AMT"));
                }
                // piadv.setBalOutstanding(rs2.getString("BALANCE"));
                if (rs.getString("P_ORG_AMT") != null && !rs.getString("P_ORG_AMT").equals("")) {
                    amt1 = Integer.parseInt(rs.getString("P_ORG_AMT"));
                }
                if (rs.getString("TOT_REC_AMT") != null && !rs.getString("TOT_REC_AMT").equals("")) {
                    amt2 = Integer.parseInt(rs.getString("TOT_REC_AMT"));
                }
                if (rs.getString("GPF_ACC_NO") != null && !rs.getString("GPF_ACC_NO").equals("")) {
                    loanBean.setGpfNo(rs.getString("GPF_ACC_NO"));
                }

                drawingmonth = "";
                drawingmonth1 = "";
                if (rs.getDate("DED_ST_DATE") != null && !rs.getDate("DED_ST_DATE").equals("")) {
                    drawingmonth = CommonFunctions.getFormattedOutputDate1(rs.getDate("DED_ST_DATE"));
                    drawingmonth1 = drawingmonth.substring(3, drawingmonth.length());
                    loanBean.setDeductionStdate(drawingmonth);
                } else if (rs.getDate("VCH_DATE") != null && !rs.getDate("VCH_DATE").equals("")) {
                    drawingmonth1 = CommonFunctions.getFormattedOutputDate1(rs.getDate("VCH_DATE"));
                    loanBean.setVchDate(drawingmonth1);
                }

                loanBean.setDrawingmonth(drawingmonth1);
                double bal = amt1 - amt2;
                if (bal < 0) {
                    bal = 0;
                }
                loanBean.setBalOutstanding(bal);
                if (sno % 10 == 0) {
                    loanBean.setPagebreakLA("<input type=\"button\" name=\"pagebreak1\" style=\"page-break-before: always;width: 0;height: 0\"/>");
                    loanBean.setPageHeaderLA(reportPageHeader(con, schedule, null, billno, "P") + "");
                } else {
                    loanBean.setPagebreakLA("");
                    loanBean.setPageHeaderLA("");
                }
                hbaSchldList.add(loanBean);

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, stmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return hbaSchldList;
    }

    @Override
    public List getLoanAdvanceScheduleInterestList(String schedule, String billno, int aqMonth, int aqYear) {

        Connection con = null;
        ResultSet rs = null;
        Statement stmt = null;
        ArrayList hbaSchldIntList = new ArrayList();
        LoanAdvanceScheduleBean loanBean = null;
        String empNmaeDesc = "";
        String aqDtlsTbl = "";
        int slNo = 1;
        double tot = 0.0;
        double amt1 = 0;
        double amt2 = 0;

        String drawingmonth = "";
        String drawingmonth1 = "";

        try {
            con = dataSource.getConnection();
            stmt = con.createStatement();
            aqDtlsTbl = getAqDtlsTableName(billno);

            String interestQry = "SELECT AQ_MAST.GPF_ACC_NO,AQ_MAST.EMP_NAME,AQ_MAST.CUR_DESG,AQ_DTLS.ACC_NO,AQ_DTLS.REF_DESC,AQ_DTLS.AD_AMT,"
                    + "EMP_LOAN_SANC.DED_ST_DATE,EMP_LOAN_SANC.I_LAST_INSTL_NO,EMP_LOAN_SANC.P_LAST_INSTL_NO,EMP_LOAN_SANC.P_CUM_RECOVERED,"
                    + "EMP_LOAN_SANC.I_CUM_RECOVERED,EMP_LOAN_SANC.VCH_NO,EMP_LOAN_SANC.VCH_DATE,EMP_LOAN_SANC.P_ORG_AMT,EMP_LOAN_SANC.I_ORG_AMT,"
                    + "EMP_LOAN_SANC.P_INSTL_AMT,EMP_LOAN_SANC.I_INSTL_AMT,(EMP_LOAN_SANC.I_ORG_AMT-AQ_DTLS.TOT_REC_AMT) BALANCE,POST_SL_NO,TOT_REC_AMT "
                    + "from (Select EMP_NAME,CUR_DESG,AQSL_NO,POST_SL_NO,GPF_ACC_NO from AQ_MAST where AQ_MAST.BILL_NO='" + billno + "' AND "
                    + "aq_month=" + aqMonth + " AND aq_year=" + aqYear + " ) AQ_MAST inner join (select ACC_NO,REF_DESC,AD_AMT,TOT_REC_AMT,AQSL_NO,"
                    + "AD_CODE,EMP_CODE,REF_ORD,REF_DATE,AD_REF_ID from " + aqDtlsTbl + " where SCHEDULE ='" + schedule + "' and "
                    + "AD_TYPE='D' and DED_TYPE='L' AND AD_AMT >0 AND NOW_DEDN='I') AQ_DTLS on AQ_MAST.AQSL_NO=AQ_DTLS.AQSL_NO inner join EMP_LOAN_SANC "
                    + "on AQ_DTLS.AD_REF_ID=EMP_LOAN_SANC.LOANID ORDER BY POST_SL_NO";

            stmt = con.createStatement();
            rs = stmt.executeQuery(interestQry);
            empNmaeDesc = "";
            while (rs.next()) {

                loanBean = new LoanAdvanceScheduleBean();
                //loanBean.setEmpName(rs.getString("EMP_NAME"));

                if ((rs.getString("EMP_NAME") + "/\n" + rs.getString("CUR_DESG")).equals(empNmaeDesc)) {
                    loanBean.setEmpNameDesg("");
                } else {
                    empNmaeDesc = rs.getString("EMP_NAME") + "/\n" + rs.getString("CUR_DESG");
                    loanBean.setSlno(slNo);
                    loanBean.setEmpNameDesg(empNmaeDesc);
                    slNo++;
                }
                if (rs.getString("VCH_NO") != null && !rs.getString("VCH_NO").equals("")) {
                    loanBean.setVchNo(rs.getString("VCH_NO"));
                } else {
                    loanBean.setVchNo("");
                }

                if (rs.getString("VCH_DATE") != null && !rs.getString("VCH_DATE").equals("")) {
                    loanBean.setVchDate(CommonFunctions.getFormattedOutputDate1(rs.getDate("VCH_DATE")));
                } else {
                    loanBean.setVchDate("");
                }

                loanBean.setAccNo(rs.getString("ACC_NO"));
                if (rs.getString("I_ORG_AMT") != null && !rs.getString("I_ORG_AMT").equals("")) {
                    loanBean.setOriginalAmt(rs.getDouble("I_ORG_AMT"));
                }
                if (rs.getString("REF_DESC") != null && !rs.getString("REF_DESC").equals("")) {
                    loanBean.setInstalmentRec(rs.getString("REF_DESC"));
                }
                loanBean.setDecutedAmt(rs.getDouble("AD_AMT"));
                tot = tot + loanBean.getDecutedAmt();
                loanBean.setTotal(tot);

                if (rs.getString("TOT_REC_AMT") != null && !rs.getString("TOT_REC_AMT").equals("")) {
                    loanBean.setRecAmt(rs.getDouble("TOT_REC_AMT"));
                }
                if (rs.getString("I_ORG_AMT") != null && !rs.getString("I_ORG_AMT").equals("")) {
                    amt1 = Integer.parseInt(rs.getString("I_ORG_AMT"));
                }

                if (rs.getString("TOT_REC_AMT") != null && !rs.getString("TOT_REC_AMT").equals("")) {
                    amt2 = Integer.parseInt(rs.getString("TOT_REC_AMT"));
                }

                if (rs.getString("GPF_ACC_NO") != null && !rs.getString("GPF_ACC_NO").equals("")) {
                    loanBean.setGpfNo(rs.getString("GPF_ACC_NO"));
                }

                drawingmonth = "";
                drawingmonth1 = "";
                if (rs.getDate("DED_ST_DATE") != null && !rs.getDate("DED_ST_DATE").equals("")) {
                    drawingmonth = CommonFunctions.getFormattedOutputDate1(rs.getDate("DED_ST_DATE"));
                    drawingmonth1 = drawingmonth.substring(3, drawingmonth.length());
                } else if (rs.getDate("VCH_DATE") != null && !rs.getDate("VCH_DATE").equals("")) {
                    drawingmonth1 = CommonFunctions.getFormattedOutputDate1(rs.getDate("VCH_DATE"));
                }
                loanBean.setDrawingmonth(drawingmonth1);

                double bal = amt1 - amt2;
                if (bal < 0) {
                    bal = 0;
                }
                loanBean.setBalOutstanding(bal);
                if (slNo % 13 == 0) {
                    loanBean.setPagebreakLA("<input type=\"button\" name=\"pagebreak1\" style=\"page-break-before: always;width: 0;height: 0\"/>");
                    loanBean.setPageHeaderLA(reportPageHeader(con, schedule, null, billno, "I") + "");
                }
                hbaSchldIntList.add(loanBean);

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, stmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return hbaSchldIntList;
    }

    @Override
    public LoanAdvanceScheduleBean getLoanAdvanceScheduleHeaderDetails(String schedule, String billno) {

        Connection con = null;
        Statement stmt1 = null;
        ResultSet rs1 = null;
        Statement stmt2 = null;
        ResultSet rs2 = null;
        LoanAdvanceScheduleBean loanBean = new LoanAdvanceScheduleBean();

        try {
            con = dataSource.getConnection();
            stmt1 = con.createStatement();
            stmt2 = con.createStatement();

            PayrollCommonFunction prcf = new PayrollCommonFunction();
            CommonReportParamBean bean = prcf.getCommonReportParameter(con, billno);

            loanBean.setDeptName(bean.getDeptname());
            loanBean.setOffName(bean.getOfficename());
            loanBean.setBilldesc(bean.getBilldesc());
            loanBean.setBillNo(billno);
            loanBean.setDdoName(bean.getDdoname());
            loanBean.setScheduleName(schedule);

            String loanQry1 = "SELECT SCHEDULE,SCHEDULE_DESC,DEMAND_NO from G_SCHEDULE where SCHEDULE='" + schedule + "'";
            rs1 = stmt1.executeQuery(loanQry1);
            if (rs1.next()) {
                loanBean.setScheduleOfRecovery(rs1.getString("SCHEDULE_DESC"));
                if (rs1.getString("DEMAND_NO") != null && !rs1.getString("DEMAND_NO").equals("")) {
                    loanBean.setDemandNo(rs1.getString("DEMAND_NO"));
                } else {
                    loanBean.setDemandNo("");
                }
            }

            String loanQuery2 = "select G_TREASURY.TR_NAME,Bill_mast.AQ_MONTH,Bill_mast.AQ_YEAR from (select * from Bill_mast where bill_no='" + billno + "') Bill_mast "
                    + "LEFT OUTER JOIN G_TREASURY on Bill_mast.TR_CODE=G_TREASURY.TR_CODE";
            rs2 = stmt2.executeQuery(loanQuery2);
            if (rs2.next()) {
                loanBean.setMonth(CalendarCommonMethods.getFullMonthAsString(rs2.getInt("AQ_MONTH")));
                loanBean.setYear(rs2.getString("AQ_YEAR"));
                loanBean.setTrName(rs2.getString("TR_NAME"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs1, stmt1);
            DataBaseFunctions.closeSqlObjects(rs2, stmt2);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return loanBean;
    }

    @Override
    public WrrScheduleBean getWRRScheduleHeaderDetails(String billno, String schedule) {

        Connection con = null;
        Statement stmt = null;
        Statement stmt1 = null;
        ResultSet rs = null;
        WrrScheduleBean wrrBean = new WrrScheduleBean();
        Statement stQuaterPool = null;
        ResultSet resultsetQuaterPool = null;
        ArrayList wrrHeaderList = new ArrayList();
        PayrollCommonFunction prcf = new PayrollCommonFunction();
        String poolname = "";
        String demandno = "";

        try {
            con = dataSource.getConnection();

            CommonReportParamBean bean = prcf.getCommonReportParameter(con, billno);
            stmt = con.createStatement();
            stmt1 = con.createStatement();
            rs = stmt.executeQuery("SELECT * FROM G_AD_LIST WHERE AD_CODE_NAME='WRR' OR AD_CODE_NAME='SWR'");

            String wrrbtid = null;
            String swrbtid = null;
            String wrr_swr_btid = null;
            while (rs.next()) {
                if (rs.getString("AD_CODE_NAME").equals("WRR")) {
                    wrrbtid = rs.getString("BT_ID");
                    wrr_swr_btid = wrrbtid;
                } else if (rs.getString("AD_CODE_NAME").equals("SWR")) {
                    swrbtid = rs.getString("BT_ID");
                    wrr_swr_btid = swrbtid;
                }
            }

            String qtrPoolQuery = "";
            if (schedule != null && schedule.equals("HRR")) {
                qtrPoolQuery = "SELECT * FROM G_QTR_POOL WHERE BT_ID = '" + wrr_swr_btid + "'";
            } else if (schedule != null) {
                qtrPoolQuery = "SELECT * FROM G_QTR_POOL WHERE IS_DEFAULT='Y'";
            }

            stQuaterPool = con.createStatement();
            resultsetQuaterPool = stQuaterPool.executeQuery(qtrPoolQuery);
            if (resultsetQuaterPool.next()) {
                poolname = resultsetQuaterPool.getString("POOL_NAME");
                demandno = resultsetQuaterPool.getString("DEMAND_NO_STRING");
            }
            wrrBean.setPoolName(poolname);

            wrrBean.setOfficeName(bean.getOfficename());
            wrrBean.setDeptName(bean.getDeptname());
            wrrBean.setDdoDegn(bean.getDdoname());
            wrrBean.setBillDesc(bean.getBilldesc());
            wrrBean.setReportName(prcf.getReportName(con, schedule));
            wrrBean.setDemandNo(prcf.getDemandName(schedule, con));
            prcf.getDate(con, billno, wrrBean);

            ArrayList despList = getList(billno, schedule);
            wrrBean.setDespListSize(String.valueOf(despList.size()));
            wrrBean.setEmplist(despList);

            wrrHeaderList.add(wrrBean);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return wrrBean;
    }

    @Override
    public List getPTScheduleEmployeeList(String billno, int aqMonth, int aqYear) {

        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        Statement stmt1 = null;
        ResultSet rs1 = null;
        PtScheduleBean ptBean = null;
        ArrayList ptSchldList = new ArrayList();
        int carryForwardTax = 0;
        int totalGross = 0;
        int basicSal = 0;
        String empCode = "";
        int sno = 1;
        String aqDtlsTbl = "";

        try {
            con = dataSource.getConnection();
            stmt = con.createStatement();
            aqDtlsTbl = getAqDtlsTableName(billno);
            String ptQuery = "SELECT DTL.EMP_CODE,DTL.EMP_NAME,DTL.CUR_DESG,DTL.CUR_BASIC, DTL.AD_AMT FROM((SELECT * FROM BILL_MAST WHERE "
                    + "BILL_MAST.BILL_NO='" + billno + "')BILL_MAST LEFT OUTER JOIN (SELECT AQ_MAST.EMP_CODE,AQ_MAST.BILL_NO,AQ_MAST.CUR_DESG,"
                    + "AQ_MAST.EMP_NAME,AQ_MAST.CUR_BASIC,AQ_MAST.POST_SL_NO, AQ_DTLS.AD_AMT FROM((SELECT * FROM AQ_MAST WHERE "
                    + "AQ_MAST.BILL_NO='" + billno + "' AND AQ_MAST.AQ_MONTH=" + aqMonth + " AND"
                    + " AQ_MAST.AQ_YEAR=" + aqYear + "  )AQ_MAST INNER JOIN (SELECT * FROM " + aqDtlsTbl + " WHERE "
                    + "SCHEDULE='PT' AND AD_TYPE='D' AND AQ_MONTH=" + aqMonth + " AND AQ_YEAR=" + aqYear + " AND AD_AMT >0 )AQ_DTLS ON AQ_DTLS.AQSL_NO=AQ_MAST.AQSL_NO))DTL "
                    + "ON BILL_MAST.BILL_NO=DTL.BILL_NO)order by POST_SL_NO";
            rs = stmt.executeQuery(ptQuery);
            while (rs.next()) {

                ptBean = new PtScheduleBean();

                ptBean.setSlno(sno);
                ptBean.setEmpname(rs.getString("EMP_NAME"));
                ptBean.setEmpdesg(rs.getString("CUR_DESG"));

                if (rs.getString("AD_AMT") != null && !rs.getString("AD_AMT").equals("")) {
                    ptBean.setEmpTaxOnProffesion(rs.getString("AD_AMT"));
                } else {
                    ptBean.setEmpTaxOnProffesion("0");
                }
                carryForwardTax = carryForwardTax + rs.getInt("AD_AMT");

                ptBean.setTotalTax(carryForwardTax + "");
                empCode = rs.getString("EMP_CODE");
                basicSal = rs.getInt("CUR_BASIC");
                ptBean.setBasicSal(basicSal);

                stmt1 = con.createStatement();
                String query1 = "SELECT AD_AMT,AD_DESC FROM(select AQSL_NO from AQ_MAST where EMP_CODE ='" + empCode + "' and BILL_NO='" + billno + "')AQ_MAST "
                        + "INNER JOIN (SELECT AQSL_NO,AD_DESC,AD_AMT FROM AQ_DTLS WHERE AD_TYPE='A' )AQ_DTLS on AQ_DTLS.AQSL_NO=AQ_MAST.AQSL_NO";
                rs1 = stmt1.executeQuery(query1);
                int totalAllowance = 0;
                while (rs1.next()) {
                    totalAllowance = totalAllowance + rs1.getInt("AD_AMT");
                }
                String gross = basicSal + totalAllowance + "";
                totalGross = totalGross + Integer.parseInt(gross);
                ptBean.setTotalGross(totalGross + "");
                ptBean.setEmpGrossSal(gross);

                ptSchldList.add(ptBean);
                sno++;

                if (sno % 13 == 0) {
                    ptBean.setPagebreakPT("<input type=\"button\" name=\"pagebreak1\" style=\"page-break-before: always;width: 0;height: 0\"/>");
                    ptBean.setPageHeaderPT(reportPageHeader(con, "PT", null, billno, "") + "");
                } else {
                    ptBean.setPagebreakPT("");
                    ptBean.setPageHeaderPT("");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, stmt);
            DataBaseFunctions.closeSqlObjects(rs1, stmt1);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return ptSchldList;
    }

    @Override
    public List getWRRScheduleEmployeeList(String billno, String schedule, int aqMonth, int aqYear, String dType) {

        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        WrrScheduleBean wrrBean = null;
        ArrayList wrrList = new ArrayList();
        String aqDTLS = "AQ_DTLS";
        int slno = 1;
        int carryForward = 0;
        try {
            con = dataSource.getConnection();
            stmt = con.createStatement();
            if ((aqYear == 2017 && aqMonth < 2) || aqYear < 2017) {
                aqDTLS = "hrmis.AQ_DTLS1";
            }

            String sql = "SELECT GPF_ACC_NO,EMP_QTR_ALLOT.QUARTER_NO,EMP_QTR_ALLOT.ADDRESS,AQ_MAST.EMP_NAME,AQ_MAST.EMP_CODE,AQ_MAST.CUR_DESG,"
                    + "AQ_DTLS.ACC_NO,AQ_DTLS.REF_DESC,AQ_DTLS.AD_AMT,POST_SL_NO from ( select GPF_ACC_NO,aq_mast.EMP_CODE,aq_mast.EMP_NAME,"
                    + "aq_mast.CUR_DESG,aq_mast.AQSL_NO,POST_SL_NO from AQ_MAST where BILL_NO='" + billno + "') AQ_MAST "
                    + "INNER JOIN (select AQ_DTLS.AQSL_NO,AQ_DTLS.SCHEDULE,AQ_DTLS.AD_AMT,AQ_DTLS.AD_TYPE,AQ_DTLS.ACC_NO,AQ_DTLS.REF_DESC "
                    + "FROM " + aqDTLS + "  where AQ_DTLS.SCHEDULE='" + schedule + "' and AQ_DTLS.aq_month='" + aqMonth + "' and AQ_DTLS.aq_year='" + aqYear + "' "
                    + "AND AQ_DTLS.AD_TYPE='D' AND AD_AMT >0 ) AQ_DTLS on AQ_DTLS.AQSL_NO=AQ_MAST.AQSL_NO inner join "
                    + "(select quarter_no,EMP_ID,address from emp_qtr_allot WHERE if_surrendered IS NULL OR if_surrendered != 'Y')emp_qtr_allot "
                    + "on emp_qtr_allot.EMP_ID=AQ_MAST.EMP_CODE ORDER BY POST_SL_NO";
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                wrrBean = new WrrScheduleBean();

                wrrBean.setSlNo(slno);
                slno++;
                wrrBean.setQuarterNo(rs.getString("QUARTER_NO"));
                if (rs.getString("ADDRESS") != null) {
                    wrrBean.setAddress(rs.getString("ADDRESS"));
                } else {
                    wrrBean.setAddress("");
                }
                wrrBean.setGpfNo(rs.getString("GPF_ACC_NO"));
                wrrBean.setEmpname(rs.getString("EMP_NAME"));
                wrrBean.setEmpdesg(rs.getString("CUR_DESG"));
                wrrBean.setRecMonth(rs.getString("REF_DESC"));

                int amt = rs.getInt("AD_AMT");
                wrrBean.setAmount(amt + "");

                carryForward = carryForward + amt;
                wrrBean.setCarryForward(carryForward + "");

                if (dType.equals("HTML")) {
                    if (slno % 10 == 0) {
                        wrrBean.setPagebreakWR("<input type=\"button\" name=\"pagebreak1\" style=\"page-break-before: always;width: 0;height: 0\"/>");
                        wrrBean.setPageHeaderWR(reportPageHeader(con, schedule, null, billno, "") + "");
                    } else {
                        wrrBean.setPagebreakWR("");
                        wrrBean.setPageHeaderWR("");
                    }
                }

                wrrList.add(wrrBean);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return wrrList;
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

    @Override
    public List getITScheduleEmployeeList(String billno, String schedule, int aqMonth, int aqYear) {

        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        ItScheduleBean itBean = null;
        ArrayList itSchList = new ArrayList();
        String empCode = "";
        int sno = 1;
        String aqDtlsTbl = "";
        int carryForward = 0;
        try {
            con = dataSource.getConnection();
            stmt = con.createStatement();
            aqDtlsTbl = getAqDtlsTableName(billno);

            String itQuery = "SELECT DTL.EMP_CODE,DTL.EMP_NAME,DTL.CUR_DESG,DTL.CUR_BASIC, DTL.AD_AMT,ID_NO,AQSL_NO FROM("
                    + "(SELECT * FROM BILL_MAST WHERE BILL_MAST.BILL_NO='" + billno + "')BILL_MAST "
                    + "LEFT OUTER JOIN (SELECT AQ_MAST.EMP_CODE,"
                    + "AQ_MAST.BILL_NO,AQ_MAST.CUR_DESG,AQ_MAST.EMP_NAME,AQ_MAST.CUR_BASIC,AQ_MAST.POST_SL_NO, AQ_DTLS.AD_AMT,AQ_DTLS.AQSL_NO FROM"
                    + "((SELECT * FROM AQ_MAST WHERE AQ_MAST.BILL_NO='" + billno + "' AND AQ_MAST.aq_month = " + aqMonth + " AND AQ_MAST.aq_year = " + aqYear + ")AQ_MAST "
                    + "INNER JOIN (SELECT * FROM " + aqDtlsTbl + " WHERE "
                    + "AQ_DTLS.SCHEDULE='" + schedule + "' AND AQ_DTLS.AD_TYPE='D' AND AD_AMT >0 )AQ_DTLS ON AQ_DTLS.AQSL_NO=AQ_MAST.AQSL_NO))DTL "
                    + "ON BILL_MAST.BILL_NO=DTL.BILL_NO left outer join (select ID_NO,EMP_ID from EMP_ID_DOC where ID_DESCRIPTION='PAN')EMP_ID_DOC "
                    + "on DTL.emp_code=EMP_ID_DOC.emp_id)order by POST_SL_NO";
            rs = stmt.executeQuery(itQuery);
            while (rs.next()) {
                itBean = new ItScheduleBean();

                itBean.setSlno(sno);
                itBean.setEmpname(rs.getString("EMP_NAME"));
                itBean.setEmpdesg(rs.getString("CUR_DESG"));
                if (rs.getString("AQSL_NO") != null && !rs.getString("AQSL_NO").equals("")) {
                    itBean.setEmpBasicSal(new CommonScheduleMethods().getGrossPay(con, rs.getString("AQSL_NO"), aqDtlsTbl, aqYear, aqMonth) + "");
                } else {
                    itBean.setEmpBasicSal("");
                }
                //itobjclass.setEmpBasicSal(rs.getString("CUR_BASIC"));
                if (rs.getString("AD_AMT") != null && !rs.getString("AD_AMT").equalsIgnoreCase("")) {
                    int dedAmt = rs.getInt("AD_AMT");
                    carryForward = carryForward + dedAmt;
                    itBean.setEmpDedutAmount(String.valueOf(dedAmt));
                    itBean.setCarryForward(String.valueOf(carryForward));
                } else {
                    itBean.setEmpDedutAmount("0");
                }
                if (rs.getString("ID_NO") != null && !rs.getString("ID_NO").equalsIgnoreCase("")) {
                    itBean.setEmpPanNo(rs.getString("ID_NO"));
                } else {
                    itBean.setEmpPanNo("");
                }
                empCode = rs.getString("EMP_CODE");

                if (sno % 20 == 0) {
                    itBean.setPagebreakIT("<input type=\"button\" name=\"pagebreak1\" style=\"page-break-before: always;width: 0;height: 0\"/>");
                    itBean.setPageHeaderIT(reportPageHeader(con, schedule, null, billno, "") + "");
                } else {
                    itBean.setPagebreakIT("");
                    itBean.setPageHeaderIT("");
                }

                itSchList.add(itBean);
                sno++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, stmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return itSchList;
    }

    @Override
    public ItScheduleBean getITScheduleHeaderDetails(String billno, String schedule) {

        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        ItScheduleBean itBean = new ItScheduleBean();
        ArrayList itHeaderList = new ArrayList();
        PayrollCommonFunction prcf = new PayrollCommonFunction();
        String btId = "";

        try {
            con = dataSource.getConnection();

            CommonReportParamBean bean = prcf.getCommonReportParameter(con, billno);
            //itBean.setITScheduleDtls(getITScheduleEmployeeList(billno, schedule, month, year));
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT BT_ID FROM G_AD_LIST WHERE SCHEDULE='" + schedule + "'");
            if (rs.next()) {
                btId = rs.getString("BT_ID");
            }
            itBean.setBtId(btId);

            itBean.setOfficeName(bean.getOfficename());
            itBean.setMontYear(prcf.getMonthAndYear(con, billno));
            itBean.setScheduleName(prcf.getReportName(con, schedule));
            itBean.setBillNo(bean.getBilldesc());
            itBean.setAqmonth(CalendarCommonMethods.getMonthAsString(bean.getAqmonth()));
            itBean.setAqyear(bean.getAqyear() + "");
            itBean.setTanno(bean.getTanno());
            itBean.setDeptName(bean.getDeptname());
            itBean.setDdoDegn(bean.getDdoname());
            itBean.setBillDesc(bean.getBilldesc());
            itBean.setReportName(prcf.getReportName(con, schedule));

            itHeaderList.add(itBean);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, stmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return itBean;
    }

    @Override
    public PtScheduleBean getPTScheduleHeaderDetails(String billno) {

        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        PtScheduleBean ptBean = new PtScheduleBean();
        ArrayList ptHeaderList = new ArrayList();
        PayrollCommonFunction prcf = new PayrollCommonFunction();

        try {
            con = dataSource.getConnection();

            CommonReportParamBean bean = prcf.getCommonReportParameter(con, billno);
            ptBean.setPTScheduleDtls(getPTScheduleEmployeeList(billno, bean.getAqmonth(), bean.getAqyear()));

            ptBean.setDeptName(bean.getDeptname());
            ptBean.setOfficeName(bean.getOfficename());
            ptBean.setDdoDegn(bean.getDdoname());
            ptBean.setBillDesc(bean.getBilldesc());
            ptBean.setMonthYear(prcf.getMonthAndYear(con, billno));
            ptBean.setBillNo(billno);

            ptHeaderList.add(ptBean);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, stmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return ptBean;
    }

    public ArrayList getList(String billno, String schedule) throws Exception {
        Connection con = null;
        ResultSet rs = null;
        Statement st = null;
        WrrScheduleBean wrrBean = null;
        ArrayList displist = new ArrayList();
        int i = 0;
        int j = 0;
        double carryForward = 0.0;
        double amt = 0.0;
        String test = null;

        try {
            con = dataSource.getConnection();
            st = con.createStatement();
            rs = st.executeQuery("select GPF_ACC_NO,EMP_QTR_ALLOT.QUARTER_NO,EMP_QTR_ALLOT.ADDRESS,AQ_MAST.EMP_NAME,AQ_MAST.EMP_CODE,AQ_MAST.CUR_DESG,AQ_DTLS.ACC_NO,AQ_DTLS.REF_DESC,AQ_DTLS.AD_AMT,POST_SL_NO from ( select GPF_ACC_NO,aq_mast.EMP_CODE,aq_mast.EMP_NAME,aq_mast.CUR_DESG,aq_mast.AQSL_NO,POST_SL_NO from AQ_MAST where BILL_NO='" + billno + "') AQ_MAST INNER JOIN (select AQ_DTLS.AQSL_NO,AQ_DTLS.SCHEDULE,AQ_DTLS.AD_AMT,AQ_DTLS.AD_TYPE,AQ_DTLS.ACC_NO,AQ_DTLS.REF_DESC from AQ_DTLS  where AQ_DTLS.SCHEDULE='" + schedule + "' AND AQ_DTLS.AD_TYPE='D' AND AD_AMT >0 ) AQ_DTLS on AQ_DTLS.AQSL_NO=AQ_MAST.AQSL_NO inner join (select quarter_no,EMP_ID,address from emp_qtr_allot WHERE if_surrendered IS NULL OR if_surrendered != 'Y')emp_qtr_allot on emp_qtr_allot.EMP_ID=AQ_MAST.EMP_CODE ORDER BY POST_SL_NO");
            while (rs.next()) {
                wrrBean = new WrrScheduleBean();

                wrrBean.setQuarterNo(rs.getString("QUARTER_NO"));
                wrrBean.setAddress(rs.getString("ADDRESS"));
                wrrBean.setEmpcode(rs.getString("GPF_ACC_NO"));
                wrrBean.setEmpname(rs.getString("EMP_NAME"));
                wrrBean.setEmpdesg(rs.getString("CUR_DESG"));
                wrrBean.setRecMonth(rs.getString("REF_DESC"));
                wrrBean.setAmount(rs.getString("AD_AMT"));

                amt = rs.getDouble("AD_AMT");
                carryForward = carryForward + amt;
                wrrBean.setCarryForward(carryForward + "");
                test = Numtowordconvertion.convertNumber((int) carryForward);
                wrrBean.setTotFig(test);
                displist.add(wrrBean);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, st);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return displist;
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

    public ArrayList getEmpGpfDetails(String gpfType, String billNo, Connection con) throws Exception {
        ArrayList empGpfList = new ArrayList();
        ResultSet rs = null;
        Statement stmt = null;
        ResultSet rs2 = null;
        Statement stmt2 = null;
        int slno = 0;
        GPFScheduleBean gpfBean = null;

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
        int gpfinstl = 0;
        String aqDtlsTbl = "";
        try {
            stmt = con.createStatement();
            stmt2 = con.createStatement();
            aqDtlsTbl = getAqDtlsTableName(billNo);
            String gpfQuery = "SELECT EMP_MAST.GPF_NO,EMP_MAST.DOE_GOV,EMP_MAST.DOB,EMP_MAST.DOS,AQ_MAST.EMP_CODE,AQ_MAST.EMP_NAME,AQ_MAST.CUR_DESG,"
                    + "AQ_MAST.BANK_ACC_NO,AQ_MAST.CUR_BASIC,GP,AQ_MAST.PAY_SCALE,AQ_MAST.AQSL_NO,POST_SL_NO from (SELECT EMP_CODE,EMP_NAME,CUR_DESG,"
                    + "BANK_ACC_NO,CUR_BASIC,PAY_SCALE,AQSL_NO,POST_SL_NO from AQ_MAST WHERE GPF_TYPE='" + gpfType + "' AND BILL_NO='" + billNo + "') AQ_MAST "
                    + "left outer join EMP_MAST on AQ_MAST.EMP_CODE=EMP_MAST.EMP_ID ORDER BY SUBSTR(EMP_MAST.GPF_NO,"
                    + "LENGTH(getgpfseries(EMP_MAST.GPF_NO))+1)";
            rs = stmt.executeQuery(gpfQuery);
            while (rs.next()) {
                releasedAmount = 0;
                gpfinstl = 0;
                gpfBean = new GPFScheduleBean();
                if (rs.getString("EMP_NAME") != null && !rs.getString("EMP_NAME").equals("")) {

                    gpfBean.setSlno(slno);
                    slno++;
                    gpfBean.setEmpName(rs.getString("EMP_NAME"));
                    gpfBean.setDesignation(rs.getString("CUR_DESG"));
                    gpfBean.setAccountNo(rs.getString("GPF_NO"));
                    gpfBean.setBasicPay(rs.getString("CUR_BASIC"));
                    gpfBean.setGradePay(rs.getString("GP"));
                    gpfBean.setScaleOfPay(rs.getString("PAY_SCALE"));
                    noofinst = CommonScheduleMethods.getNoOfInst2(rs.getString("AQSL_NO"), aqDtlsTbl, con);
                    gpfBean.setNoOfInstalment(noofinst);

                    if (rs.getString("DOE_GOV") != null && !rs.getString("DOE_GOV").trim().equals("")) {
                        doe = rs.getString("DOE_GOV");
                        doe1 = doe.substring(0, 4);
                        doe2 = doe.substring(5, 7);
                        doe3 = doe.substring(8, 10);
                        doe4 = doe3 + "/" + doe2 + "/" + doe1;
                        gpfBean.setDateOfEntry(doe4);
                    }
                    if (rs.getString("DOB") != null && !rs.getString("DOB").trim().equals("")) {
                        dob = rs.getString("DOB");
                        dob1 = dob.substring(0, 4);
                        dob2 = dob.substring(5, 7);
                        dob3 = dob.substring(8, 10);
                        dob4 = dob3 + "/" + dob2 + "/" + dob1;
                        gpfBean.setDob(dob4);
                    }
                    if (rs.getString("DOS") != null && !rs.getString("DOS").trim().equals("")) {
                        dos = rs.getString("DOS");
                        dos1 = dos.substring(0, 4);
                        dos2 = dos.substring(5, 7);
                        dos3 = dos.substring(8, 10);
                        dos4 = dos3 + "/" + dos2 + "/" + dos1;
                        gpfBean.setDor(dos4);
                    }

                    String qryString1 = "SELECT AD_AMT MONTHLYSUB FROM " + aqDtlsTbl + " WHERE AD_TYPE='D' AND EMP_CODE='" + rs.getString("EMP_CODE") + "' AND "
                            + "DED_TYPE='S' AND SCHEDULE='GPF' AND AQSL_NO='" + rs.getString("AQSL_NO") + "'";
                    rs2 = stmt2.executeQuery(qryString1);
                    if (rs2.next()) {
                        gpfBean.setMonthlySub(rs2.getInt("MONTHLYSUB"));
                    }
                    DataBaseFunctions.closeSqlObjects(rs2, stmt2);

                    String qryString2 = "SELECT AD_AMT TOWARDSLOAN FROM " + aqDtlsTbl + " WHERE AD_TYPE='D' AND EMP_CODE='" + rs.getString("EMP_CODE") + "' "
                            + "AND DED_TYPE='L' AND SCHEDULE='GA' AND AQSL_NO='" + rs.getString("AQSL_NO") + "'";
                    stmt2 = con.createStatement();
                    rs2 = stmt2.executeQuery(qryString2);
                    while (rs2.next()) {
                        gpfinstl = gpfinstl + rs2.getInt("TOWARDSLOAN");
                    }
                    gpfBean.setTowardsLoan(gpfinstl);
                    DataBaseFunctions.closeSqlObjects(rs2, stmt2);

                    String qryString3 = "SELECT SUM(AD_AMT) TOWARDSOTHER FROM " + aqDtlsTbl + " WHERE AD_TYPE='D' AND EMP_CODE='" + rs.getString("EMP_CODE") + "' "
                            + "AND (AD_CODE='GPDD' OR AD_CODE='GPIR') AND AQSL_NO='" + rs.getString("AQSL_NO") + "'";
                    stmt2 = con.createStatement();
                    rs2 = stmt2.executeQuery(qryString3);
                    if (rs2.next()) {
                        gpfBean.setOtherDeposits(rs2.getInt("TOWARDSOTHER"));
                    }

                    releasedAmount = gpfBean.getMonthlySub() + gpfBean.getTowardsLoan() + gpfBean.getOtherDeposits();
                    gpfBean.setTotalReleased(releasedAmount);
                    total += releasedAmount;
                    gpfBean.setCarryForward(total + "");
                    if (total > 0) {
                        empGpfList.add(gpfBean);
                    }
                }
                if (total > 0) {
                    gpfBean.setAmountInWords(Numtowordconvertion.convertNumber((int) total).toUpperCase());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, stmt);
            DataBaseFunctions.closeSqlObjects(rs2, stmt2);
        }
        return empGpfList;
    }

    @Override
    public VacancyStatementScheduleBean getVacancyStmtScheduleHeaderDetails(String billno) {

        Connection con = null;
        VacancyStatementScheduleBean absStmtBean = new VacancyStatementScheduleBean();
        String btid = "";
        try {
            con = dataSource.getConnection();

            PayrollCommonFunction prcf = new PayrollCommonFunction();
            CommonReportParamBean bean = prcf.getCommonReportParameter(con, billno);

            absStmtBean.setBillNo(billno);
            absStmtBean.setOffName(bean.getOfficename());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return absStmtBean;
    }

    @Override
    public List getVacancyStmtScheduleEmpList(String billno) {

        Connection con = null;
        ResultSet rs = null;
        Statement stmt = null;
        ArrayList absStmtScheduleList = new ArrayList();
        VacancyStatementScheduleBean absStmtBean = null;
        int total = 0;
        int gTotal = 0;
        int slno = 0;
        try {
            con = dataSource.getConnection();
            stmt = con.createStatement();
            String absStmtQuery = "SELECT OFF_CODE,CUR_DESG,COUNT(*) CNT,PAY_SCALE,OFF_CODE FROM AQ_MAST WHERE BILL_NO='" + billno + "' AND EMP_CODE IS NULL "
                    + "GROUP BY CUR_DESG,PAY_SCALE,OFF_CODE";
            rs = stmt.executeQuery(absStmtQuery);
            while (rs.next()) {
                absStmtBean = new VacancyStatementScheduleBean();
                slno++;
                absStmtBean.setSlno(slno);

                String desg = rs.getString("CUR_DESG");
                StringTokenizer stringTokenizer = new StringTokenizer(desg, ",");
                if (stringTokenizer.hasMoreTokens()) {
                    desg = stringTokenizer.nextToken().trim();
                }
                absStmtBean.setDesignation(desg);

                total = rs.getInt("CNT");
                absStmtBean.setPostno(total);

                if (rs.getString("PAY_SCALE") != null && !rs.getString("PAY_SCALE").equals("")) {
                    absStmtBean.setPayscale(rs.getString("PAY_SCALE"));
                } else {
                    absStmtBean.setPayscale("--");
                }

                gTotal = gTotal + total;
                absStmtBean.setGrandTotal(gTotal);

                absStmtScheduleList.add(absStmtBean);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, stmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return absStmtScheduleList;
    }

    public List getAllowanceList(Connection con, String adtype, String billno, double basicPay, String accType) {
        AllowDeductDetails allowdeduct = null;
        List al = new ArrayList();
        ResultSet rs = null;
        Statement stmt = null;
        boolean ispayadded = false;
        String gphead = "";
        String aqDtlsTbl = "";
        try {
            stmt = con.createStatement();
            aqDtlsTbl = getAqDtlsTableName(billno);
            String alowanceQry = "SELECT AD_CODE,BT_ID,SUM(AD_AMT) AD_AMT FROM " + aqDtlsTbl + " INNER JOIN (SELECT AQ_MAST.AQSL_NO FROM AQ_MAST WHERE "
                    + "AQ_MAST.BILL_NO='" + billno + "')AQ_MAST ON AQ_DTLS.AQSL_NO = AQ_MAST.AQSL_NO WHERE AD_TYPE='" + adtype + "' AND AD_AMT>0 GROUP BY "
                    + "AQ_DTLS.AD_CODE,BT_ID,NOW_DEDN ORDER BY BT_ID";
            stmt = con.createStatement();
            rs = stmt.executeQuery(alowanceQry);
            while (rs.next()) {
                allowdeduct = new AllowDeductDetails();

                allowdeduct.setAdname(rs.getString("AD_CODE"));
                if (accType != null && accType.equalsIgnoreCase("TPF") && allowdeduct.getAdname().equalsIgnoreCase("LIC")) {
                    allowdeduct.setObjecthead("7129");
                } else if (accType != null && accType.equalsIgnoreCase("TPF") && (allowdeduct.getAdname().equalsIgnoreCase("GPF") || allowdeduct.getAdname().equalsIgnoreCase("GA"))) {
                    allowdeduct.setObjecthead("7058");
                } else {
                    if (allowdeduct.getAdname().equalsIgnoreCase("GP")) {
                        gphead = rs.getString("BT_ID");
                    }
                    allowdeduct.setObjecthead(rs.getString("BT_ID"));
                }

                if (allowdeduct.getObjecthead() != null && !allowdeduct.getObjecthead().equals("")) {
                    if (allowdeduct.getObjecthead().equals("136")) {
                        ispayadded = true;
                        allowdeduct.setAdname("PAY + " + allowdeduct.getAdname());
                        allowdeduct.setAdamount(Double.valueOf(basicPay + rs.getDouble("AD_AMT") + "").longValue() + "");
                    } else {
                        allowdeduct.setAdamount(Double.valueOf(rs.getDouble("AD_AMT") + "").longValue() + "");
                    }
                } else {
                    allowdeduct.setAdamount(Double.valueOf(rs.getDouble("AD_AMT") + "").longValue() + "");
                }
                al.add(allowdeduct);
            }
            if (ispayadded == false) {
                allowdeduct = new AllowDeductDetails();
                allowdeduct.setAdname("PAY  ");
                allowdeduct.setObjecthead(gphead);
                allowdeduct.setAdamount(Double.valueOf(basicPay + "").longValue() + "");
                al.add(allowdeduct);
            }
            Collections.sort(al);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, stmt);
        }
        return al;
    }

    public ArrayList getDeductionList(Connection con, String adtype, String billno, String accType) {

        Statement stmt = null;
        ResultSet rs = null;
        AllowDeductDetails allowdeduct = null;
        ArrayList dList = new ArrayList();
        String aqDtlsTbl = "";
        try {
            stmt = con.createStatement();
            aqDtlsTbl = getAqDtlsTableName(billno);
            String deductQry = "SELECT AQ_DTLS.AD_CODE,AQ_DTLS.BT_ID,sum(AQ_DTLS.AD_AMT) AD_AMT from( (Select AQ_MAST.AQSL_NO FROM AQ_MAST where "
                    + "AQ_MAST.BILL_NO = '" + billno + "')AQ_MAST inner join (SELECT AQSL_NO,AD_AMT,AD_CODE,BT_ID "
                    + "from " + aqDtlsTbl + " where AD_TYPE ='" + adtype + "' AND AD_AMT>0 AND SCHEDULE != 'PVTL' and SCHEDULE != 'PVTD') AQ_DTLS ON "
                    + "AQ_MAST.AQSL_NO = AQ_DTLS.AQSL_NO) GROUP BY AQ_DTLS.AD_CODE,BT_ID ORDER BY BT_ID";
            stmt = con.createStatement();
            rs = stmt.executeQuery(deductQry);
            while (rs.next()) {
                allowdeduct = new AllowDeductDetails();

                allowdeduct.setAdname(rs.getString("AD_CODE"));
                if (accType != null && accType.equalsIgnoreCase("TPF") && allowdeduct.getAdname().equalsIgnoreCase("LIC")) {
                    allowdeduct.setObjecthead("7129");
                } else if (accType != null && accType.equalsIgnoreCase("TPF") && (allowdeduct.getAdname().equalsIgnoreCase("GPF") || allowdeduct.getAdname().equalsIgnoreCase("GA"))) {
                    allowdeduct.setObjecthead("7058");
                } else {
                    allowdeduct.setObjecthead(rs.getString("BT_ID"));
                }

                allowdeduct.setAdamount(rs.getDouble("AD_AMT") + "");
                dList.add(allowdeduct);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, stmt);
        }
        return dList;
    }

    @Override
    public void thirdSchedulePDF(Document document, String empid) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        PreparedStatement pst2 = null;
        ResultSet rs2 = null;

        int fixedheight = 40;

        Date incrDt = null;
        int revisedBasic = 0;
        try {
            con = this.dataSource.getConnection();

            Font f1 = new Font();
            f1.setSize(10);
            f1.setFamily("Times New Roman");

            PdfPTable table = null;
            PdfPCell cell = null;

            PdfPTable innertable = null;
            PdfPCell innercell = null;

            table = new PdfPTable(4);
            table.setWidths(new float[]{0.3f, 3, 0.5f, 3});
            table.setWidthPercentage(80);

            innertable = new PdfPTable(2);
            innertable.setWidths(new float[]{0.05f, 0.5f});
            innertable.setWidthPercentage(100);

            cell = new PdfPCell(new Phrase("THIRD SCHEDULE", getDesired_PDF_Font(13, true, true)));
            cell.setColspan(4);
            cell.setFixedHeight(fixedheight);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Form for fixation of pay under the Orissa Revised Scales", getDesired_PDF_Font(13, true, false)));
            cell.setColspan(4);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("of Pay Rules,2016", getDesired_PDF_Font(13, true, false)));
            cell.setColspan(4);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("(See rule-7)", getDesired_PDF_Font(12, false, false)));
            cell.setColspan(4);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            String sql = "SELECT emp_pay_revised_2016.EMP_ID,ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME,L_NAME], ' ') FULL_NAME,G_POST.POST,existing_pay_scale,date_option_exercised,"
                    + " mon_basic,DP,DA,total_amount,revised_basic,emp_pay_revised_2016.GP FROM emp_pay_revised_2016"
                    + " INNER JOIN EMP_MAST ON emp_pay_revised_2016.EMP_ID=EMP_MAST.EMP_ID"
                    + " LEFT OUTER JOIN G_POST ON emp_pay_revised_2016.POST=G_POST.POST_CODE WHERE emp_pay_revised_2016.EMP_ID=?";
            pst = con.prepareStatement(sql);
            pst.setString(1, empid);
            rs = pst.executeQuery();
            if (rs.next()) {

                cell = new PdfPCell();
                cell.setColspan(4);
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("1.", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("Name of the Employee", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(":", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(rs.getString("FULL_NAME"), f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("2.", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("Designation of the post in which pay is to be fixed as on January 1,2016.", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(":", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(rs.getString("POST"), f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("3.", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("Status(substantive/officiating)", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(":", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("Substantive", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("4.", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("Name of the Head of the Office (Designation only)", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(":", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("5.", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("Existing Scale of Pay", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(":", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(rs.getString("existing_pay_scale"), f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("6.", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("Revised pay band in the pay structure as per the Fitment Table attached at Annexure-I", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(":", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("7.", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("Date from which option exercised", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(":", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(CommonFunctions.getFormattedOutputDate1(rs.getDate("date_option_exercised")), f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("8.", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("Emoulments in the existing Scale of pay on the Date from which Revised Scale is opted", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(":", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                cell = new PdfPCell();
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                innercell = new PdfPCell(new Phrase("(a)", f1));
                innercell.setBorder(Rectangle.NO_BORDER);
                innertable.addCell(innercell);
                innercell = new PdfPCell(new Phrase("Basic Pay(including R.P.P)", f1));
                innercell.setBorder(Rectangle.NO_BORDER);
                innertable.addCell(innercell);
                cell = new PdfPCell(innertable);
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(":", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(rs.getString("mon_basic"), f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                cell = new PdfPCell();
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                innertable = new PdfPTable(2);
                innertable.setWidths(new float[]{0.05f, 0.5f});
                innertable.setWidthPercentage(100);

                innercell = new PdfPCell(new Phrase("(b)", f1));
                innercell.setBorder(Rectangle.NO_BORDER);
                innertable.addCell(innercell);
                innercell = new PdfPCell(new Phrase("D.P. if any", f1));
                innercell.setBorder(Rectangle.NO_BORDER);
                innertable.addCell(innercell);
                cell = new PdfPCell(innertable);
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(":", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(rs.getString("DP"), f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                cell = new PdfPCell();
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                innertable = new PdfPTable(2);
                innertable.setWidths(new float[]{0.05f, 0.5f});
                innertable.setWidthPercentage(100);

                innercell = new PdfPCell(new Phrase("(c)", f1));
                innercell.setBorder(Rectangle.NO_BORDER);
                innertable.addCell(innercell);
                innercell = new PdfPCell(new Phrase("D.A as on 01.01.2016 4%", f1));
                innercell.setBorder(Rectangle.NO_BORDER);
                innertable.addCell(innercell);
                cell = new PdfPCell(innertable);
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(":", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(rs.getString("DA"), f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                cell = new PdfPCell();
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                innertable = new PdfPTable(2);
                innertable.setWidths(new float[]{0.05f, 0.5f});
                innertable.setWidthPercentage(100);

                innercell = new PdfPCell(new Phrase("(d)", f1));
                innercell.setBorder(Rectangle.NO_BORDER);
                innertable.addCell(innercell);
                innercell = new PdfPCell(new Phrase("Total emoulments (a to c)", f1));
                innercell.setBorder(Rectangle.NO_BORDER);
                innertable.addCell(innercell);
                cell = new PdfPCell(innertable);
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(":", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(rs.getString("total_amount"), f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("9.", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("Pay fixed in the Revised Scale of pay", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(":", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell();
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                cell = new PdfPCell();
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                innertable = new PdfPTable(2);
                innertable.setWidths(new float[]{0.05f, 0.5f});
                innertable.setWidthPercentage(100);

                innercell = new PdfPCell(new Phrase("(a)", f1));
                innercell.setBorder(Rectangle.NO_BORDER);
                innertable.addCell(innercell);
                innercell = new PdfPCell(new Phrase("Pay in revised pay band/scale in which pay is to be fixed as per the fitment Table attached at Annexure-I", f1));
                innercell.setBorder(Rectangle.NO_BORDER);
                innertable.addCell(innercell);
                cell = new PdfPCell(innertable);
                cell.setFixedHeight(50);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(":", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("10.", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("Stepped up pay with reference to the Revised pay of junior, if applicable [Notes 2 & 4 of Rule 7(1) of ORSP Rules,2008].Name and pay of the Junior also to be indicated distinctly", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(":", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("11.", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("Revised pay with reference to the Substantive Pay in cases where the pay fixed into the officiating post is lower than the pay fixed in the substantive post if applicable [See Rule(1) of Rule 7]", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(":", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("12.", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("Personal Pay, if any [Notes 1 and 3 of Sub Rule (1) of Rule 7]", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(":", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("13.", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("Revised emoulments after fixation", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(":", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                cell = new PdfPCell();
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                innertable = new PdfPTable(2);
                innertable.setWidths(new float[]{0.05f, 0.5f});
                innertable.setWidthPercentage(100);

                innercell = new PdfPCell(new Phrase("(a)", f1));
                innercell.setBorder(Rectangle.NO_BORDER);
                innertable.addCell(innercell);
                innercell = new PdfPCell(new Phrase("Pay in the Revised Pay Band/Pay Scale", f1));
                innercell.setBorder(Rectangle.NO_BORDER);
                innertable.addCell(innercell);
                cell = new PdfPCell(innertable);
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(":", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(rs.getString("revised_basic"), f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                cell = new PdfPCell();
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                innertable = new PdfPTable(2);
                innertable.setWidths(new float[]{0.05f, 0.5f});
                innertable.setWidthPercentage(100);

                innercell = new PdfPCell(new Phrase("(c)", f1));
                innercell.setBorder(Rectangle.NO_BORDER);
                innertable.addCell(innercell);
                innercell = new PdfPCell(new Phrase("Personal Pay, if admissible", f1));
                innercell.setBorder(Rectangle.NO_BORDER);
                innertable.addCell(innercell);
                cell = new PdfPCell(innertable);
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(":", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                /*java.util.Date utilDate = new java.util.Date("1-JAN-2016");
                 java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
                 ArrayList revisedList = getNoOfIncrement(con, rs.getString("EMP_ID"), sqlDate, rs.getInt("revised_basic"), totalgp, rs.getString("existing_pay_scale"));*/
                // Start Revised Table
                ArrayList revisedList = getNoOfIncrement(con, rs.getString("EMP_ID"));
                ThirdScheduleBean tbean = null;
                if (revisedList.size() > 0) {
                    tbean = (ThirdScheduleBean) revisedList.get(0);
                    incrDt = tbean.getIncrDt();
                    revisedBasic = tbean.getRevisedbasic();
                }

                cell = new PdfPCell(new Phrase("14.", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("Date of next increment (Rules 10) and pay after grant of increment", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(":", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(CommonFunctions.getFormattedOutputDate1(incrDt) + "\n" + revisedBasic, f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                cell = new PdfPCell();
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("Date of Increment", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(":", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(CommonFunctions.getFormattedOutputDate1(incrDt), f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                document.add(table);

                table = new PdfPTable(3);
                table.setWidths(new float[]{1, 1.5f, 0.5f});
                table.setWidthPercentage(100);

                cell = new PdfPCell();
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("Pay after Increment", getDesired_PDF_Font(13, false, true)));
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
                cell = new PdfPCell();
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                document.add(table);

                table = new PdfPTable(3);
                table.setWidths(new int[]{1, 2, 2});
                table.setWidthPercentage(80);

                cell = new PdfPCell();
                cell.setColspan(3);
                cell.setFixedHeight(20);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                cell = new PdfPCell();
                cell.setFixedHeight(20);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("Date of Increment/Promotion", f1));
                cell.setFixedHeight(20);
                //cell.setBorder(Rectangle.NO_BORDER);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("Pay in the Pay Band/Scale", f1));
                cell.setFixedHeight(20);
                //cell.setBorder(Rectangle.NO_BORDER);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                document.add(table);

                table = new PdfPTable(3);
                table.setWidths(new int[]{1, 2, 2});
                table.setWidthPercentage(80);
                tbean = null;
                for (int i = 0; i < revisedList.size(); i++) {
                    tbean = (ThirdScheduleBean) revisedList.get(i);

                    cell = new PdfPCell();
                    cell.setFixedHeight(20);
                    cell.setBorder(Rectangle.NO_BORDER);
                    table.addCell(cell);
                    cell = new PdfPCell(new Phrase(CommonFunctions.getFormattedOutputDate1(tbean.getIncrDt()), f1));
                    cell.setFixedHeight(20);
                    //cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                    cell = new PdfPCell(new Phrase(tbean.getRevisedbasic() + "", f1));
                    cell.setFixedHeight(20);
                    //cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                }
                document.add(table);
                // End Revised Table

                table = new PdfPTable(4);
                table.setWidths(new float[]{0.3f, 3, 0.5f, 3});
                table.setWidthPercentage(80);

                cell = new PdfPCell();
                cell.setColspan(4);
                cell.setFixedHeight(20);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("15", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("Any other relevant information", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(":", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                cell = new PdfPCell();
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("  Date", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("Signature & Designation of Head  of Office/Competent Authority", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                cell = new PdfPCell();
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("  Office:G.A.Department", f1));
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell();
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("", f1));
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
            }
            document.add(table);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    private Font getDesired_PDF_Font(int fontsize, boolean isBold, boolean isUnderline) throws Exception {
        Font f = null;

        try {
            if (isBold == false && isUnderline == false) {
                f = new Font(Font.FontFamily.TIMES_ROMAN, fontsize, Font.NORMAL);
            }
            if (isBold == true && isUnderline == false) {
                f = new Font(Font.FontFamily.TIMES_ROMAN, fontsize, Font.BOLD);
            }
            if (isBold == true && isUnderline == true) {
                f = new Font(Font.FontFamily.TIMES_ROMAN, fontsize, Font.BOLD | Font.UNDERLINE);
            }
            if (isBold == false && isUnderline == true) {
                f = new Font(Font.FontFamily.TIMES_ROMAN, fontsize, Font.UNDERLINE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return f;
    }

    private ArrayList getNoOfIncrement(Connection con, String empId) {

        PreparedStatement pst = null;
        ResultSet rs = null;

        ThirdScheduleBean tbean = null;
        ArrayList revisedPayList = new ArrayList();
        try {
            pst = con.prepareStatement("SELECT INCR_DATE,REVISED_BASIC FROM emp_pay_revised_increment_2016 WHERE EMP_ID=? ORDER BY INCR_DATE ASC");
            pst.setString(1, empId);
            rs = pst.executeQuery();
            while (rs.next()) {
                tbean = new ThirdScheduleBean();
                tbean.setIncrDt(rs.getDate("INCR_DATE"));
                tbean.setRevisedbasic(rs.getInt("REVISED_BASIC"));
                revisedPayList.add(tbean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
        }
        return revisedPayList;
    }

    @Override
    public SecondScheduleBean getSecondScheduleData(String empid) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        SecondScheduleBean secondSchlBean = new SecondScheduleBean();
        try {
            con = this.dataSource.getConnection();

            /*String sql = "SELECT ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME,L_NAME], ' ') FULL_NAME,G_POST.POST,EMP_PAY_REVISED_2016.GP,EXISTING_PAY_SCALE FROM EMP_MAST"
             + " LEFT OUTER JOIN EMP_PAY_REVISED_2016 ON EMP_MAST.EMP_ID=EMP_PAY_REVISED_2016.EMP_ID"
             + " LEFT OUTER JOIN G_POST ON EMP_PAY_REVISED_2016.POST=G_POST.POST_CODE"
             + " WHERE EMP_MAST.EMP_ID=? AND AQ_YEAR=2016 AND AQ_MONTH=0";*/
            String sql = "SELECT ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME,L_NAME], ' ') FULL_NAME,G_POST.POST,EMP_PAY_REVISED_2016.GP,EMP_PAY_REVISED_2016.EXISTING_PAY_SCALE FROM EMP_MAST"
                    + " LEFT OUTER JOIN (SELECT EMP_ID,GP,EXISTING_PAY_SCALE,POST FROM EMP_PAY_REVISED_2016 WHERE AQ_YEAR=2016 AND AQ_MONTH=0)EMP_PAY_REVISED_2016 ON EMP_MAST.EMP_ID=EMP_PAY_REVISED_2016.EMP_ID"
                    + " LEFT OUTER JOIN G_POST ON EMP_PAY_REVISED_2016.POST=G_POST.POST_CODE"
                    + " WHERE EMP_MAST.EMP_ID=?";
            pst = con.prepareStatement(sql);
            pst.setString(1, empid);
            rs = pst.executeQuery();
            if (rs.next()) {
                secondSchlBean.setEmpid(empid);
                secondSchlBean.setEmpname(rs.getString("FULL_NAME"));
                secondSchlBean.setPost(rs.getString("POST"));
                secondSchlBean.setGp(rs.getString("GP"));
                secondSchlBean.setPayscale(rs.getString("EXISTING_PAY_SCALE"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return secondSchlBean;
    }

    @Override
    public void secondSchedulePDF(Document document, String empid) {

        Connection con = null;

        int fixedheight = 50;

        SecondScheduleBean secondSchlBean = new SecondScheduleBean();
        try {

            secondSchlBean = getPayRevisionOptionData(empid);

            Font f1 = new Font();
            f1.setSize(10);
            f1.setFamily("Times New Roman");

            PdfPTable table = null;
            PdfPCell cell = null;

            table = new PdfPTable(2);
            table.setWidths(new float[]{0.3f, 3});
            table.setWidthPercentage(80);

            cell = new PdfPCell(new Phrase("SECOND SCHEDULE", getDesired_PDF_Font(13, true, true)));
            cell.setColspan(2);
            cell.setFixedHeight(fixedheight);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Application Form for exercising option to come over to the", getDesired_PDF_Font(13, true, false)));
            cell.setColspan(2);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Odisha Revised Scales of Pay Rules,2017", getDesired_PDF_Font(13, true, false)));
            cell.setColspan(2);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("(See rule-6)", getDesired_PDF_Font(12, false, false)));
            cell.setColspan(2);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell();
            cell.setColspan(2);
            cell.setFixedHeight(30);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            if (secondSchlBean.getOptionChosen().equals("1")) {

                Chunk c1 = new Chunk("I ", getDesired_PDF_Font(10, false, false));
                Chunk c2 = new Chunk(secondSchlBean.getEmpname(), getDesired_PDF_Font(10, true, false));
                Chunk c3 = new Chunk(" holding the post of ", getDesired_PDF_Font(10, false, false));
                Chunk c4 = new Chunk(StringUtils.defaultString(secondSchlBean.getPost()), getDesired_PDF_Font(10, true, false));
                Chunk c5 = new Chunk(" and drawing pay in the Pay Band and Grade Pay of ", getDesired_PDF_Font(10, false, false));
                Chunk c6 = new Chunk(StringUtils.defaultString(secondSchlBean.getPayscale()) + "(" + StringUtils.defaultString(secondSchlBean.getGp() + "") + ")", getDesired_PDF_Font(10, true, false));
                Chunk c7 = new Chunk(" do hereby elect the revised pay structure with effect from 1st day of January,2016 ", getDesired_PDF_Font(10, false, false));
                Phrase p1 = new Phrase();
                p1.add(c1);
                p1.add(c2);
                p1.add(c3);
                p1.add(c4);
                p1.add(c5);
                p1.add(c6);
                p1.add(c7);

                cell = new PdfPCell(new Phrase("1.", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                //cell = new PdfPCell(new Phrase("I "+secondSchlBean.getEmpname()+" holding the post of "+secondSchlBean.getPost()+" and drawing pay in the Pay Band and Grade Pay of "+secondSchlBean.getPayscale()+"("+ secondSchlBean.getGp()+") do hereby elect the revised pay structure with effect from 1st day of January,2016",f1));
                cell = new PdfPCell(p1);
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
            } else if (secondSchlBean.getOptionChosen().equals("2")) {

                Chunk c1 = new Chunk("I ", getDesired_PDF_Font(10, false, false));
                Chunk c2 = new Chunk(secondSchlBean.getEmpname(), getDesired_PDF_Font(10, true, false));
                Chunk c3 = new Chunk(" holding the post of ", getDesired_PDF_Font(10, false, false));
                Chunk c4 = new Chunk(StringUtils.defaultString(secondSchlBean.getPost()), getDesired_PDF_Font(10, true, false));
                Chunk c5 = new Chunk(" and drawing pay in the Pay Band and Grade Pay of ", getDesired_PDF_Font(10, false, false));
                Chunk c6 = new Chunk(StringUtils.defaultString(secondSchlBean.getPayscale()) + "(" + StringUtils.defaultString(secondSchlBean.getGp() + "") + ")", getDesired_PDF_Font(10, true, false));
                Chunk c7 = new Chunk(" do hereby elect to continue on the existing Pay Band and Grade Pay until the date ", getDesired_PDF_Font(10, false, false));
                Chunk c8 = new Chunk(StringUtils.defaultString(secondSchlBean.getEnteredDate()), getDesired_PDF_Font(10, true, false));
                Chunk c9 = new Chunk(" (i.e the date of my next increment/promotion or up-gradation of the post/vacate or cease to draw pay in the existing pay structure).", getDesired_PDF_Font(10, false, false));
                Phrase p1 = new Phrase();
                p1.add(c1);
                p1.add(c2);
                p1.add(c3);
                p1.add(c4);
                p1.add(c5);
                p1.add(c6);
                p1.add(c7);
                p1.add(c8);
                p1.add(c9);

                cell = new PdfPCell(new Phrase("1.", f1));
                cell.setFixedHeight(70);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                //cell = new PdfPCell(new Phrase("I "+secondSchlBean.getEmpname()+" holding the post of "+secondSchlBean.getPost()+" and drawing pay in the Pay Band and Grade Pay of "+secondSchlBean.getPayscale()+"("+ secondSchlBean.getGp()+") do hereby elect to continue on the existing Pay Band and Grade Pay until the date "+txtDate+" (i.e the date of my next increment/promotion or up-gradation of the post/vacate or cease to draw pay in the existing pay structure).",f1));
                cell = new PdfPCell(p1);
                cell.setFixedHeight(70);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
            }

            cell = new PdfPCell();
            cell.setColspan(2);
            cell.setFixedHeight(20);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("2.", f1));
            cell.setFixedHeight(30);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("The option hereby exercised is final and will not be modified at any subsequent date.", f1));
            cell.setFixedHeight(30);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell();
            cell.setColspan(2);
            cell.setFixedHeight(20);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Date:", f1));
            cell.setColspan(2);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            document.add(table);

            table = new PdfPTable(2);
            table.setWidths(new float[]{2.5f, 2f});
            table.setWidthPercentage(80);

            cell = new PdfPCell();
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(StringUtils.repeat("", 120) + "Signature:", f1));
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell();
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(StringUtils.repeat("", 120) + "Designation:", f1));
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell();
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(StringUtils.repeat("", 120) + "Office:", f1));
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell();
            cell.setColspan(2);
            cell.setFixedHeight(20);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell();
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(StringUtils.repeat("", 120) + "Signed before me", f1));
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell();
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(StringUtils.repeat("", 120) + "Head of Office/Any other Gazetted Officer with designated recieved the above declaration.", f1));
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell();
            cell.setColspan(2);
            cell.setFixedHeight(20);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell();
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(StringUtils.repeat("", 120) + "Signature:", f1));
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell();
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(StringUtils.repeat("", 120) + "Head of Office/Competent Authority:", f1));
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell();
            cell.setColspan(2);
            cell.setFixedHeight(20);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Date:", f1));
            cell.setColspan(2);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("N.B: Delete whichever is not applicable at Para-1", f1));
            cell.setColspan(2);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            document.add(table);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    @Override
    public String isDuplicatePayRevisionOption(String empid) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        String exists = "N";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        String isIAS = "N";
        try {
            Date payRevDt = sdf.parse("2016-01-01");

            con = this.dataSource.getConnection();

            String sql = "select doe_gov,pay_revision_option.emp_id,cur_cadre_code from emp_mast"
                    + " left outer join pay_revision_option on emp_mast.emp_id=pay_revision_option.emp_id where emp_mast.emp_id=?";
            pst = con.prepareStatement(sql);
            pst.setString(1, empid);
            rs = pst.executeQuery();
            if (rs.next()) {
                if (rs.getString("cur_cadre_code") != null && !rs.getString("cur_cadre_code").equals("")) {
                    if (rs.getString("cur_cadre_code").equals("1101") || rs.getString("cur_cadre_code").equals("9103")
                            || rs.getString("cur_cadre_code").equals("5801") || rs.getString("cur_cadre_code").equals("1165")
                            || rs.getString("cur_cadre_code").equals("1007") || rs.getString("cur_cadre_code").equals("9105")
                            || rs.getString("cur_cadre_code").equals("1166") || rs.getString("cur_cadre_code").equals("9106")) {
                        isIAS = "Y";
                        if (rs.getString("emp_id") != null && !rs.getString("emp_id").equals("")) {
                            exists = "IASE";
                        } else {
                            exists = "IAS";
                        }
                    }
                }
                if (isIAS.equals("N")) {
                    if (rs.getString("doe_gov") != null && !rs.getString("doe_gov").equals("")) {
                        Date doj = sdf.parse(rs.getString("doe_gov"));

                        if (doj.compareTo(payRevDt) > 0) {
                            exists = "NE";
                        } else {
                            if (rs.getString("emp_id") != null && !rs.getString("emp_id").equals("")) {
                                exists = "Y";
                            }
                        }
                    }
                }
                //exists = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return exists;
    }

    private SecondScheduleBean getPayRevisionOptionData(String empid) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        SecondScheduleBean secondSchlBean = new SecondScheduleBean();
        try {
            con = this.dataSource.getConnection();

            String sql = "SELECT ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME,L_NAME], ' ') FULL_NAME,G_POST.POST,pay_revision_option.GP,PAY_SCALE,option_chosen,entered_date FROM EMP_MAST"
                    + " LEFT OUTER JOIN pay_revision_option ON EMP_MAST.EMP_ID=pay_revision_option.EMP_ID"
                    + " LEFT OUTER JOIN G_POST ON pay_revision_option.GPC=G_POST.POST_CODE"
                    + " WHERE EMP_MAST.EMP_ID=?";
            pst = con.prepareStatement(sql);
            pst.setString(1, empid);
            rs = pst.executeQuery();
            if (rs.next()) {
                secondSchlBean.setEmpid(empid);
                secondSchlBean.setEmpname(rs.getString("FULL_NAME"));
                secondSchlBean.setPost(rs.getString("POST"));
                secondSchlBean.setGp(rs.getString("GP"));
                secondSchlBean.setPayscale(rs.getString("PAY_SCALE"));
                secondSchlBean.setOptionChosen(rs.getString("option_chosen"));
                secondSchlBean.setEnteredDate(CommonFunctions.getFormattedOutputDate1(rs.getDate("entered_date")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return secondSchlBean;
    }

    @Override
    public void insertPayRevisionData(String empid, String offcode, String selectedOption, String postcode, String payscale, int gp, String txtDate, String hasUserChanged, String hasDDOChanged) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        SecondScheduleBean secondSchlBean = new SecondScheduleBean();

        String startTime = "";
        try {
            Calendar cal = Calendar.getInstance();
            DateFormat dateFormat1 = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");
            startTime = dateFormat1.format(cal.getTime());

            DateFormat dateFormat2 = new SimpleDateFormat("dd-MMM-yyyy");

            con = this.dataSource.getConnection();

            String sql = "SELECT ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME,L_NAME], ' ') FULL_NAME,POST,EMP_PAY_REVISED_2016.GP,EMP_PAY_REVISED_2016.EXISTING_PAY_SCALE FROM EMP_MAST"
                    + " LEFT OUTER JOIN EMP_PAY_REVISED_2016 ON EMP_MAST.EMP_ID=EMP_PAY_REVISED_2016.EMP_ID"
                    + " WHERE EMP_MAST.EMP_ID=?";
            pst = con.prepareStatement(sql);
            pst.setString(1, empid);
            rs = pst.executeQuery();
            if (rs.next()) {
                secondSchlBean.setEmpid(empid);
                secondSchlBean.setEmpname(rs.getString("FULL_NAME"));
                if (rs.getString("POST") != null && !rs.getString("POST").equals("")) {
                    secondSchlBean.setPostcode(rs.getString("POST"));
                }
                //secondSchlBean.setPostcode(rs.getString("POST_CODE"));
                secondSchlBean.setSpc(getSPCFromGPC(con, rs.getString("POST")));
                secondSchlBean.setGp(rs.getString("GP"));
                secondSchlBean.setPayscale(rs.getString("EXISTING_PAY_SCALE"));
            }

            if (secondSchlBean.getPayscale() != null && !secondSchlBean.getPayscale().equals("")) {
                if (!secondSchlBean.getPayscale().equals(payscale)) {
                    hasUserChanged = "Y";
                }
            } else if (secondSchlBean.getPayscale() == null || secondSchlBean.getPayscale().equals("")) {
                hasUserChanged = "Y";
            }

            if (secondSchlBean.getGp() != null && !secondSchlBean.getGp().equals("")) {
                if (!secondSchlBean.getGp().equals(gp + "")) {
                    hasUserChanged = "Y";
                }
            } else if (secondSchlBean.getGp() == null || secondSchlBean.getGp().equals("")) {
                hasUserChanged = "Y";
            }

            if (postcode != null && !postcode.equals("")) {
                secondSchlBean.setSpc("");
            } else if (postcode == null || postcode.equals("")) {
                postcode = secondSchlBean.getPostcode();
            }

            sql = "INSERT INTO pay_revision_option(emp_id,date_of_submission,option_chosen,pay_scale,GP,entered_date,off_code,spc,gpc,has_employee_changed,has_ddo_changed) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
            pst = con.prepareStatement(sql);
            pst.setString(1, empid);
            pst.setTimestamp(2, new Timestamp(dateFormat1.parse(startTime).getTime()));
            pst.setString(3, selectedOption);
            pst.setString(4, payscale);
            pst.setInt(5, gp);
            if (txtDate != null && !txtDate.equals("")) {
                pst.setTimestamp(6, new Timestamp(dateFormat2.parse(txtDate).getTime()));
            } else {
                pst.setTimestamp(6, null);
            }
            pst.setString(7, offcode);
            pst.setString(8, secondSchlBean.getSpc());
            pst.setString(9, postcode);
            pst.setString(10, hasUserChanged);
            pst.setString(11, hasDDOChanged);
            int retVal = pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    private String getSPCFromGPC(Connection con, String gpc) {

        PreparedStatement pst = null;
        ResultSet rs = null;

        String spc = "";

        try {
            String sql = "SELECT SPC FROM G_SPC WHERE GPC=?";

            pst = con.prepareStatement(sql);
            pst.setString(1, gpc);
            rs = pst.executeQuery();
            if (rs.next()) {
                spc = rs.getString("SPC");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
        }
        return spc;
    }

    @Override
    public void secondScheduleIASPDF(Document document, String empid) {

        Connection con = null;

        int fixedheight = 50;

        SecondScheduleBean secondSchlBean = new SecondScheduleBean();
        try {

            secondSchlBean = getPayRevisionOptionData(empid);

            Font f1 = new Font();
            f1.setSize(10);
            f1.setFamily("Times New Roman");

            PdfPTable table = null;
            PdfPCell cell = null;

            table = new PdfPTable(2);
            table.setWidths(new float[]{0.3f, 3});
            table.setWidthPercentage(80);

            cell = new PdfPCell(new Phrase("FORM OF OPTION", getDesired_PDF_Font(13, true, true)));
            cell.setColspan(2);
            cell.setFixedHeight(fixedheight);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("[See Rule 6]", getDesired_PDF_Font(13, true, false)));
            cell.setColspan(2);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell();
            cell.setColspan(2);
            cell.setFixedHeight(30);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            if (secondSchlBean.getOptionChosen().equals("1")) {

                Chunk c1 = new Chunk("I ", getDesired_PDF_Font(10, false, false));
                Chunk c2 = new Chunk(secondSchlBean.getEmpname(), getDesired_PDF_Font(10, true, false));
                Chunk c3 = new Chunk(" hereby elect the revised pay structure with effect from 1st January,2016. ", getDesired_PDF_Font(10, false, false));

                Phrase p1 = new Phrase();
                p1.add(c1);
                p1.add(c2);
                p1.add(c3);

                cell = new PdfPCell(new Phrase("1.", f1));
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(p1);
                cell.setFixedHeight(fixedheight);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
            } else if (secondSchlBean.getOptionChosen().equals("2")) {

                Chunk c1 = new Chunk("I ", getDesired_PDF_Font(10, false, false));
                Chunk c2 = new Chunk(secondSchlBean.getEmpname(), getDesired_PDF_Font(10, true, false));
                Chunk c3 = new Chunk(" hereby elect to continue on Pay Band and Grade Pay on my substantive/officiating post mentioned below unit. ", getDesired_PDF_Font(10, false, false));

                Phrase p1 = new Phrase();
                p1.add(c1);
                p1.add(c2);
                p1.add(c3);

                cell = new PdfPCell(new Phrase("1.", f1));
                cell.setFixedHeight(30);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(p1);
                cell.setFixedHeight(30);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                c1 = new Chunk("The date of my next increment/the date of my subsequent increment raising my pay to promotion/up gradation to the post of ", getDesired_PDF_Font(10, false, false));
                c2 = new Chunk(StringUtils.defaultString(secondSchlBean.getPost()), getDesired_PDF_Font(10, true, false));

                p1 = new Phrase();
                p1.add(c1);
                p1.add(c2);

                cell = new PdfPCell(new Phrase("."));
                cell.setFixedHeight(30);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cell);
                cell = new PdfPCell(p1);
                cell.setFixedHeight(30);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                c1 = new Chunk(" Existing Pay Band and Grade Pay ", getDesired_PDF_Font(10, false, false));
                c2 = new Chunk(StringUtils.defaultString(secondSchlBean.getPayscale()) + "(" + StringUtils.defaultString(secondSchlBean.getGp() + "") + ")", getDesired_PDF_Font(10, true, false));

                p1 = new Phrase();
                p1.add(c1);
                p1.add(c2);

                cell = new PdfPCell();
                cell.setFixedHeight(30);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(p1);
                cell.setFixedHeight(30);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
            }

            cell = new PdfPCell();
            cell.setColspan(2);
            cell.setFixedHeight(20);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(StringUtils.repeat(" ", 60) + "Signature", f1));
            cell.setColspan(2);
            cell.setFixedHeight(30);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(StringUtils.repeat(" ", 60) + "Name", f1));
            cell.setColspan(2);
            cell.setFixedHeight(30);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(StringUtils.repeat(" ", 60) + "Designation", f1));
            cell.setColspan(2);
            cell.setFixedHeight(30);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(StringUtils.repeat(" ", 60) + "Office in which employed", f1));
            cell.setColspan(2);
            cell.setFixedHeight(30);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell();
            cell.setColspan(2);
            cell.setFixedHeight(30);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell();
            cell.setColspan(2);
            cell.setFixedHeight(30);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("UNDERTAKING", getDesired_PDF_Font(10, true, false)));
            cell.setColspan(2);
            cell.setFixedHeight(30);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("I hereby undertake that in the event of my pay havng been fixed in a manner contrary to the provisions contained in these Rules, as detect subsequently, any excess payment so made shall be refunded by me to the Government either by adjustment againest futire payments due to me or otherwise.", f1));
            cell.setColspan(2);
            cell.setFixedHeight(60);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell();
            cell.setColspan(2);
            cell.setFixedHeight(30);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(StringUtils.repeat(" ", 60) + "Signature", f1));
            cell.setColspan(2);
            cell.setFixedHeight(30);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(StringUtils.repeat(" ", 60) + "Name", f1));
            cell.setColspan(2);
            cell.setFixedHeight(30);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(StringUtils.repeat(" ", 60) + "Designation", f1));
            cell.setColspan(2);
            cell.setFixedHeight(30);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell();
            cell.setColspan(2);
            cell.setFixedHeight(30);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Date:", f1));
            cell.setColspan(2);
            cell.setFixedHeight(30);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Place:", f1));
            cell.setColspan(2);
            cell.setFixedHeight(30);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            document.add(table);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    private ArrayList getEmpGpfDetails(String gpfType, String billNo, Connection con, ScheduleHelper scHelperBean) throws Exception {

        ArrayList empGpfList = new ArrayList();

        PreparedStatement pst = null;
        ResultSet rs = null;

        PreparedStatement pstmonthlySub = null;
        ResultSet rsmonthlySub = null;

        PreparedStatement pstgpfinst = null;
        ResultSet rsgpfinst = null;

        PreparedStatement pstotherdeposits = null;
        ResultSet rsotherdeposits = null;

        int slno = 0;
        GPFScheduleBean gpfBean = null;

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
        int gpfinstl = 0;
        String aqDtlsTbl = "";

        //int pageno = scHelperBean.getPageno();
        try {
            aqDtlsTbl = getAqDtlsTableName(billNo);

            String monthlySubQuery = "SELECT AD_AMT MONTHLYSUB FROM " + aqDtlsTbl + " WHERE AD_TYPE='D' AND EMP_CODE=? AND"
                    + " DED_TYPE='S' AND SCHEDULE='GPF' AND AQSL_NO=? AND AQ_YEAR=? AND AQ_MONTH=?";
            pstmonthlySub = con.prepareStatement(monthlySubQuery);

            String gpfinstQuery = "SELECT AD_AMT TOWARDSLOAN FROM " + aqDtlsTbl + " WHERE AD_TYPE='D' AND EMP_CODE=?"
                    + " AND DED_TYPE='L' AND SCHEDULE='GA' AND AQSL_NO=? AND AQ_YEAR=? AND AQ_MONTH=?";
            pstgpfinst = con.prepareStatement(gpfinstQuery);

            String otherdepositsQuery = "SELECT SUM(AD_AMT) TOWARDSOTHER FROM " + aqDtlsTbl + " WHERE AD_TYPE='D' AND EMP_CODE=?"
                    + " AND (AD_CODE='GPDD' OR AD_CODE='GPIR') AND AQSL_NO=? AND AQ_YEAR=? AND AQ_MONTH=?";
            pstotherdeposits = con.prepareStatement(otherdepositsQuery);

            String gpfQuery = "SELECT EMP_MAST.GPF_NO,EMP_MAST.DOE_GOV,EMP_MAST.DOB,EMP_MAST.DOS,AQ_MAST.EMP_CODE,AQ_MAST.EMP_NAME,AQ_MAST.CUR_DESG,"
                    + " AQ_MAST.BANK_ACC_NO,AQ_MAST.CUR_BASIC,GP,AQ_MAST.PAY_SCALE,AQ_MAST.AQSL_NO,POST_SL_NO,AQ_YEAR,AQ_MONTH from AQ_MAST"
                    + " LEFT OUTER JOIN EMP_MAST ON AQ_MAST.EMP_CODE=EMP_MAST.EMP_ID WHERE GPF_TYPE=? AND BILL_NO=? ORDER BY SUBSTR(EMP_MAST.GPF_NO,"
                    + " LENGTH(getgpfseries(EMP_MAST.GPF_NO))+1)";
            pst = con.prepareStatement(gpfQuery);
            pst.setString(1, gpfType);
            pst.setInt(2, Integer.parseInt(billNo));
            rs = pst.executeQuery();
            while (rs.next()) {
                releasedAmount = 0;
                gpfinstl = 0;
                gpfBean = new GPFScheduleBean();
                if (rs.getString("EMP_NAME") != null && !rs.getString("EMP_NAME").equals("")) {
                    slno++;

                    gpfBean.setSlno(slno);
                    gpfBean.setEmpName(rs.getString("EMP_NAME"));
                    gpfBean.setDesignation(rs.getString("CUR_DESG"));
                    gpfBean.setAccountNo(rs.getString("GPF_NO"));
                    gpfBean.setBasicPay(rs.getString("CUR_BASIC"));
                    gpfBean.setGradePay(rs.getString("GP"));
                    gpfBean.setScaleOfPay(rs.getString("PAY_SCALE"));
                    noofinst = CommonScheduleMethods.getNoOfInst(rs.getString("AQSL_NO"), rs.getInt("AQ_YEAR"), rs.getInt("AQ_MONTH"), aqDtlsTbl, con, "GA");
                    gpfBean.setNoOfInstalment(noofinst);

                    if (rs.getString("DOE_GOV") != null && !rs.getString("DOE_GOV").trim().equals("")) {
                        doe = rs.getString("DOE_GOV");
                        doe1 = doe.substring(0, 4);
                        doe2 = doe.substring(5, 7);
                        doe3 = doe.substring(8, 10);
                        doe4 = doe3 + "/" + doe2 + "/" + doe1;
                        gpfBean.setDateOfEntry(doe4);
                    }
                    if (rs.getString("DOB") != null && !rs.getString("DOB").trim().equals("")) {
                        dob = rs.getString("DOB");
                        dob1 = dob.substring(0, 4);
                        dob2 = dob.substring(5, 7);
                        dob3 = dob.substring(8, 10);
                        dob4 = dob3 + "/" + dob2 + "/" + dob1;
                        gpfBean.setDob(dob4);
                    }
                    if (rs.getString("DOS") != null && !rs.getString("DOS").trim().equals("")) {
                        dos = rs.getString("DOS");
                        dos1 = dos.substring(0, 4);
                        dos2 = dos.substring(5, 7);
                        dos3 = dos.substring(8, 10);
                        dos4 = dos3 + "/" + dos2 + "/" + dos1;
                        gpfBean.setDor(dos4);
                    }

                    pstmonthlySub.setString(1, rs.getString("EMP_CODE"));
                    pstmonthlySub.setString(2, rs.getString("AQSL_NO"));
                    pstmonthlySub.setInt(3, rs.getInt("AQ_YEAR"));
                    pstmonthlySub.setInt(4, rs.getInt("AQ_MONTH"));
                    rsmonthlySub = pstmonthlySub.executeQuery();
                    if (rsmonthlySub.next()) {
                        gpfBean.setMonthlySub(rsmonthlySub.getInt("MONTHLYSUB"));
                    }

                    pstgpfinst.setString(1, rs.getString("EMP_CODE"));
                    pstgpfinst.setString(2, rs.getString("AQSL_NO"));
                    pstgpfinst.setInt(3, rs.getInt("AQ_YEAR"));
                    pstgpfinst.setInt(4, rs.getInt("AQ_MONTH"));
                    rsgpfinst = pstgpfinst.executeQuery();
                    while (rsgpfinst.next()) {
                        gpfinstl = gpfinstl + rsgpfinst.getInt("TOWARDSLOAN");
                    }
                    gpfBean.setTowardsLoan(gpfinstl);

                    pstotherdeposits.setString(1, rs.getString("EMP_CODE"));
                    pstotherdeposits.setString(2, rs.getString("AQSL_NO"));
                    pstotherdeposits.setInt(3, rs.getInt("AQ_YEAR"));
                    pstotherdeposits.setInt(4, rs.getInt("AQ_MONTH"));
                    rsotherdeposits = pstotherdeposits.executeQuery();
                    if (rsotherdeposits.next()) {
                        gpfBean.setOtherDeposits(rsotherdeposits.getInt("TOWARDSOTHER"));
                    }

                    releasedAmount = gpfBean.getMonthlySub() + gpfBean.getTowardsLoan() + gpfBean.getOtherDeposits();
                    gpfBean.setTotalReleased(releasedAmount);
                    total += releasedAmount;

                    if (slno % 8 == 0) {
                        gpfBean.setCarryForward(reportCarryForward(total, "GPF") + "");

                        gpfBean.setPagebreakchild("<input type=\"button\" name=\"pagebreak1\" style=\"page-break-before: always;width: 0;height: 0\"/>");
                        gpfBean.setPageHeaderTable(reportTableHeader("GPF") + "");
                        gpfBean.setBroughtForward(reportBroughtForward(total, "GPF") + "");
                    } else {
                        gpfBean.setCarryForward("");
                        gpfBean.setPagebreakchild("");
                        gpfBean.setPageHeaderTable("");
                        gpfBean.setBroughtForward("");
                    }
                    //if (total > 0) {
                    empGpfList.add(gpfBean);
                    //}
                }
                if (total > 0) {
                    gpfBean.setAmountInWords(Numtowordconvertion.convertNumber((int) total).toUpperCase());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rsmonthlySub, rsgpfinst, rsotherdeposits);
            DataBaseFunctions.closeSqlObjects(pstmonthlySub, pstgpfinst, pstotherdeposits);
        }
        return empGpfList;
    }

    private StringBuffer reportBroughtForward(int pagetotal, String schedule) {

        StringBuffer broughtforward = null;

        try {
            if (schedule.equals("GPF")) {
                broughtforward = new StringBuffer("<tr style=\"height:30px\">"
                        + "<td colspan=\"8\" style=\"text-align:right;\" class=\"txtf\">"
                        + "Brought Forward  " + pagetotal + ""
                        + "</td>"
                        + "</tr>");
            } else if (schedule.equals("WRR") || schedule.equals("SWR") || schedule.equals("HRR")) {
                broughtforward = new StringBuffer("<tr style=\"height:30px\">"
                        + "<td colspan=\"5\" style=\"text-align:right;\" class=\"txtf\">"
                        + "Brought Forward  " + pagetotal + ""
                        + "</td>"
                        + "</tr>");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return broughtforward;
    }

    private StringBuffer reportTableHeader(String schedule) {

        StringBuffer tblHeader = null;

        try {
            if (schedule.equals("GPF")) {
                tblHeader = new StringBuffer("<table border=\"0\" width=\"100%\" cellspacing=\"0\" style=\"font-size:11px; font-family:verdana;\">"
                        + "<tr class=\"tblHeader\" height=\"45px\">"
                        + "<td width=\"3%\" class=\"tblHeader\">Sl No</td>"
                        + "<td width=\"15%\" align=\"center\" class=\"tblHeader\">ACCOUNT NO./<br/>DATE OF ENTRY <br/>INTO GOVT. SERVICE</td>"
                        + "<td width=\"25%\" class=\"tblHeader\">NAME OF THE SUBSCRIBER/<br/>DESIGNATION</td>"
                        + "<td width=\"12%\" class=\"tblHeader\">BASIC PAY/ GRADE <br/>PAY / SCALE OF PAY</td>"
                        + "<td width=\"12%\" class=\"tblHeader\">MONTHLY SUBSCRIPTION</td>"
                        + "<td width=\"15%\" class=\"tblHeader\">REFUND OF WITHDRAWLS </br> AMT / NO. OF INST.</td>"
                        + "<td width=\"8%\" class=\"tblHeader\">TOTAL RELEASED</td>"
                        + "<td width=\"15%\" class=\"tblHeader\">REMARKS <br/>D.O.B and D.O.R.</td>"
                        + "</tr>");
                tblHeader.append("<tr class=\"tblHeader\" height=\"20px\">"
                        + "<td style=\"text-align:center;border-bottom:1px solid;\" class=\"printData\">&nbsp;1</td>"
                        + "<td style=\"text-align:center;border-bottom:1px solid;\" class=\"printData\">&nbsp;2</td>"
                        + "<td style=\"text-align:center;border-bottom:1px solid;\" class=\"printData\">&nbsp;3</td>"
                        + "<td style=\"text-align:center;border-bottom:1px solid;\" class=\"printData\">&nbsp;4</td>"
                        + "<td style=\"text-align:center;border-bottom:1px solid;\" class=\"printData\">&nbsp;5</td>"
                        + "<td style=\"text-align:center;border-bottom:1px solid;\" class=\"printData\">&nbsp;6</td>"
                        + "<td style=\"text-align:center;border-bottom:1px solid;\" class=\"printData\">&nbsp;7</td>"
                        + "<td style=\"text-align:center;border-bottom:1px solid;\" class=\"printData\">&nbsp;8</td>"
                        + "</tr>");
            } else if (schedule.equals("WRR")) {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tblHeader;
    }

    private StringBuffer reportPageHeader(Connection con, String schedule, String gpfType, String billNo, String dednType) {

        PreparedStatement pst = null;
        ResultSet rs = null;

        String billmonth = "";
        String billyear = "";

        StringBuffer header = null;
        try {
            PayrollCommonFunction prcf = new PayrollCommonFunction();
            CommonReportParamBean bean = prcf.getCommonReportParameter(con, StringUtils.defaultString(billNo));

            String sql = "SELECT AQ_MONTH, AQ_YEAR FROM BILL_MAST WHERE BILL_NO = ?";
            pst = con.prepareStatement(sql);
            pst.setInt(1, Integer.parseInt(billNo));
            rs = pst.executeQuery();
            if (rs.next()) {
                int month = rs.getInt("AQ_MONTH");
                billmonth = CalendarCommonMethods.getFullMonthAsString(month);
                billyear = rs.getString("AQ_YEAR");
            }

            if (schedule.equals("GPF")) {
                header = new StringBuffer("<div style=\"width:90%;margin: 0 auto;\">"
                        + "<table width=\"100%\" border=\"0\">"
                        + "<tr>"
                        + "<td width=\"100%\" style=\"text-align:center\">"
                        + "<b>GENERAL PROVIDENT FUND</b>"
                        + "</td>"
                        + "</tr>"
                        + "<tr>"
                        + "<td width=\"100%\" style=\"text-align:center\">"
                        + "<b>BILL NO:</b>" + bean.getBilldesc() + ""
                        + "</td>"
                        + "</tr>"
                        + "<tr>"
                        + "<td width=\"100%\" style=\"text-align:center\">"
                        + "SCHEDULE OF <b>" + gpfType + "</b>"
                        + "</td>"
                        + "</tr>"
                        + "<tr>"
                        + "<td width=\"100%\" style=\"text-align:center\">"
                        + "Demand No-\"8009/_____________________- State/Centre G.P.F Withdrawals\" (Strike out which is not applicable)"
                        + "</td>"
                        + "</tr>"
                        + "</table>"
                        + "</div>");
                header.append("<div style=\"width:99%;margin: 0 auto;\">"
                        + "<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-size:13px;\">"
                        + "<thead></thead>"
                        + "<tr>"
                        + "<td style=\"text-align:left;\">"
                        + "1. Arrange the A/C Nos in serial order. Accounts Nos may be written very clearly."
                        + "Separate Schedules should be prepared  for each group."
                        + "</td>"
                        + "</tr>"
                        + "<tr>"
                        + "<td style=\"text-align:left;\">2. The names of the subscribers should be written in full.</td>"
                        + "</tr>"
                        + "<tr>"
                        + "<td style=\"text-align:left;\">3. If interest is paid on advance, mention it in remarks column.</td>"
                        + "</tr>"
                        + "<tr>"
                        + "<td style=\"text-align:left;\">4. Figures in columns 3,4,5 and 7 should be rounded to whole rupees.</td>"
                        + "</tr>"
                        + "<tr>"
                        + "<td style=\"text-align:left;\">5. Use similar form, if names are few. But do not write subscribers name and account numbers very close to each other.</td>"
                        + "</tr>"
                        + "<tr>"
                        + "<td style=\"text-align:left;\">6. The total of schedules also should be written both in figures and words.</td>"
                        + "</tr>"
                        + "<tr>"
                        + "<td style=\"text-align:left;\">7. This form should not be used for transactions of General Provident Fund for which form No. O.T.C. 76 has been provided.</td>"
                        + "</tr>"
                        + "<tr>"
                        + "<td style=\"text-align:left;\">8. In Col. 1 quote account number unfailingly. The guide letters e.g. I.C.S. (ICS Provident Fund) etc. should be invariably  prefixed to Account Nos.</td>"
                        + "</tr>"
                        + "<tr>"
                        + "<td style=\"text-align:left;\">9. In the remarks column write description against every new name such as 'New Subscriber' came on transfer from Office District resumed subscription.</td>"
                        + "</tr>"
                        + "<tr>"
                        + "<td style=\"text-align:left;\">10. Separate schedule should be prepared in respect of persons whose account are kept by different Accountant General.</td>"
                        + "</tr>"
                        + "<tr>"
                        + "<td>&nbsp;</td>"
                        + "</tr>"
                        + "<tr>"
                        + "<td style=\"text-align:center;\"><b>" + bean.getOfficeen() + "</b>"
                        + "</br>DEDUCTION MADE FROM THE SALARY FOR <b>" + billmonth + "</b>&ensp;<b>" + billyear + "</b>"
                        + "</td>"
                        + "</tr>"
                        + "<tr>"
                        + "<td>&nbsp;</td>"
                        + "</tr>"
                        + "</table>"
                        + "</div>");
                header.append(reportTableHeader("GPF"));

            } else if (schedule.equals("WRR") || schedule.equals("SWR") || schedule.equals("HRR")) {
                WrrScheduleBean wrrBean = getWRRScheduleHeaderDetails(billNo, schedule);

                header = new StringBuffer("<tr style=\"height: 30px\">\n"
                        + "<th width=\"8%\" style=\"text-align: center;border-bottom:1px solid #000000;border-top:1px solid #000000;border-right:1px solid #000000;border-left:1px solid #000000;\">\n"
                        + "Sl. No.\n"
                        + "</th>\n"
                        + "<th width=\"15%\" style=\"text-align: center;border-bottom:1px solid #000000;border-top:1px solid #000000;border-right:1px solid #000000;border-left:1px solid #000000;\">\n"
                        + "GPF No.\n"
                        + "</th>\n"
                        + "<th width=\"25%\" style=\"text-align: center;border-bottom:1px solid #000000;border-top:1px solid #000000;border-right:1px solid #000000;border-left:1px solid #000000;\">\n"
                        + "Name of the Employee/Designation\n"
                        + "</th>\n"
                        + "<th width=\"12%\" style=\"text-align: center;border-bottom:1px solid #000000;border-top:1px solid #000000;border-right:1px solid #000000;border-left:1px solid #000000;\">\n"
                        + "Amount Recovered\n"
                        + "</th>\n"
                        + "<th width=\"17%\" style=\"text-align: center;border-bottom:1px solid #000000;border-top:1px solid #000000;border-right:1px solid #000000;border-left:1px solid #000000;\">\n"
                        + "Quarter No. & Address\n"
                        + "</th>\n"
                        + "</tr>");

            } else if (schedule.equals("HBA") || schedule.equals("VE") || schedule.equals("SHBA") || schedule.equals("MCA") || schedule.equals("MOPA")) {
                if (dednType.equals("P")) {
                    header = new StringBuffer("<tr class=\"tblHeader\">\n"
                            + "<td width=\"4%\" rowspan=\"2\" class=\"printData\" style=\"border: 1px solid black;\">Sl. No.</td>\n"
                            + "<td width=\"20%\" rowspan=\"2\" class=\"printData\" style=\"border: 1px solid black;\">Name of the Employee/<br>Designation</td>\n"
                            + "<td width=\"10%\" rowspan=\"2\" class=\"printData\" style=\"border: 1px solid black;\">Month in which Original Advance was Drawn</td>\n"
                            + "<td width=\"5%\" colspan=\"6\" style=\"text-align:center;border: 1px solid black;\" class=\"printData\">PRINCIPAL</td>\n"
                            + "</tr>");
                } else if (dednType.equals("I")) {
                    header = new StringBuffer("<tr class=\"tblHeader\">\n"
                            + "<td width=\"4%\" rowspan=\"2\" class=\"printData\" style=\"border: 1px solid black;\">Sl. No.</td>\n"
                            + "<td width=\"20%\" rowspan=\"2\" class=\"printData\" style=\"border: 1px solid black;\">Name of the Employee/<br>Designation</td>\n"
                            + "<td width=\"10%\" rowspan=\"2\" class=\"printData\" style=\"border: 1px solid black;\">T.V. No. & Date in<br>which original adv<br>drawn with<br>Treasury Name</td>\n"
                            + "<td width=\"10%\" rowspan=\"2\" class=\"printData\" style=\"border: 1px solid black;\">Account No</td>\n"
                            + "<td width=\"5%\" colspan=\"6\" style=\"text-align:center;border: 1px solid black;\" class=\"printData\">INTEREST</td>\n"
                            + "</tr>");
                }
                header.append("<tr class=\"tblHeader\">\n"
                        + "<td width=\"8%\" align=\"center\" class=\"printData\" style=\"border: 1px solid black;\">Amount of<br>Original<br>Advance</td>\n"
                        + "<td width=\"8%\" align=\"center\" class=\"printData\" style=\"border: 1px solid black;\">No of<br>Installment of<br>Recovery</td>\n"
                        + "<td width=\"8%\" align=\"center\" class=\"printData\" style=\"border: 1px solid black;\">Amount<br>Deducted in<br>the Bill</td>\n"
                        + "<td width=\"8%\" align=\"center\" class=\"printData\" style=\"border: 1px solid black;\">Recovery<br>Upto the<br>Month</td>\n"
                        + "<td width=\"8%\" align=\"center\" class=\"printData\" style=\"border: 1px solid black;\">Balance<br>Outstanding</td>\n"
                        + "<td width=\"8%\" align=\"center\" class=\"printData\" style=\"border: 1px solid black;\">Remarks</td>\n"
                        + "</tr>");

            } else if (schedule.equals("IT") || schedule.equals("HC") || schedule.equals("GIS") || schedule.equals("CGEGIS")) {
                if (schedule.equals("IT")) {
                    header = new StringBuffer("<tr style=\"height: 30px\">\n"
                            + "<th width=\"5%\" style=\"text-align: center;border-bottom:1px solid #000000;border-top:1px solid #000000;border-right:1px solid #000000;border-left:1px solid #000000;\">Sl. No.</th>\n"
                            + "<th width=\"20%\" style=\"text-align: center;border-bottom:1px solid #000000;border-top:1px solid #000000;border-right:1px solid #000000;border-left:1px solid #000000;\">Name and Designation of Employee</th>\n"
                            + "<th width=\"10%\" style=\"text-align: center;border-bottom:1px solid #000000;border-top:1px solid #000000;border-right:1px solid #000000;border-left:1px solid #000000;\">PAN No</th>\n"
                            + "<th width=\"10%\" style=\"text-align: center;border-bottom:1px solid #000000;border-top:1px solid #000000;border-right:1px solid #000000;border-left:1px solid #000000;\">Gross Salary</th>\n"
                            + "<th width=\"10%\" style=\"text-align: center;border-bottom:1px solid #000000;border-top:1px solid #000000;border-right:1px solid #000000;border-left:1px solid #000000;\">Deduction</th>\n"
                            + "</tr>");
                } else if (schedule.equals("HC")) {
                    header = new StringBuffer("<tr style=\"height: 30px\">\n"
                            + "<th width=\"5%\" style=\"text-align: center;border-bottom:1px solid #000000;border-top:1px solid #000000;border-right:1px solid #000000;border-left:1px solid #000000;\">Sl. No.</th>\n"
                            + "<th width=\"20%\" colspan=\"2\" style=\"text-align: center;border-bottom:1px solid #000000;border-top:1px solid #000000;border-right:1px solid #000000;border-left:1px solid #000000;\">Name and Designation of Employee</th>\n"
                            + "<th width=\"10%\" style=\"text-align: center;border-bottom:1px solid #000000;border-top:1px solid #000000;border-right:1px solid #000000;border-left:1px solid #000000;\">Gross Salary</th>\n"
                            + "<th width=\"10%\" style=\"text-align: center;border-bottom:1px solid #000000;border-top:1px solid #000000;border-right:1px solid #000000;border-left:1px solid #000000;\">Deduction</th>\n"
                            + "</tr>");
                } else if (schedule.equals("GIS")) {
                    header = new StringBuffer("<tr style=\"height: 30px\">\n"
                            + "<th width=\"5%\" style=\"text-align: center;border-bottom:1px solid #000000;border-top:1px solid #000000;border-right:1px solid #000000;border-left:1px solid #000000;\">Sl. No.</th>\n"
                            + "<th width=\"20%\" colspan=\"2\" style=\"text-align: center;border-bottom:1px solid #000000;border-top:1px solid #000000;border-right:1px solid #000000;border-left:1px solid #000000;\">Name and Designation of Employee</th>\n"
                            + "<th width=\"10%\" style=\"text-align: center;border-bottom:1px solid #000000;border-top:1px solid #000000;border-right:1px solid #000000;border-left:1px solid #000000;\">Gross Salary</th>\n"
                            + "<th width=\"10%\" style=\"text-align: center;border-bottom:1px solid #000000;border-top:1px solid #000000;border-right:1px solid #000000;border-left:1px solid #000000;\">Deduction</th>\n"
                            + "</tr>");
                } else if (schedule.equals("CGEGIS")) {
                    header = new StringBuffer("<tr style=\"height: 30px\">\n"
                            + "<th width=\"5%\" style=\"text-align: center;border-bottom:1px solid #000000;border-top:1px solid #000000;border-right:1px solid #000000;border-left:1px solid #000000;\">Sl. No.</th>\n"
                            + "<th width=\"20%\" colspan=\"2\" style=\"text-align: center;border-bottom:1px solid #000000;border-top:1px solid #000000;border-right:1px solid #000000;border-left:1px solid #000000;\">Name and Designation of Employee</th>\n"
                            + "<th width=\"10%\" style=\"text-align: center;border-bottom:1px solid #000000;border-top:1px solid #000000;border-right:1px solid #000000;border-left:1px solid #000000;\">Gross Salary</th>\n"
                            + "<th width=\"10%\" style=\"text-align: center;border-bottom:1px solid #000000;border-top:1px solid #000000;border-right:1px solid #000000;border-left:1px solid #000000;\">Deduction</th>\n"
                            + "</tr>");
                }
            } else if (schedule.equals("EP")) {

                header = new StringBuffer("<tr style=\"height:40px;\">"
                        + "<td width=\"4%\" class=\"tblHeader\">Sl No</td>"
                        + "<td width=\"25%\" class=\"tblHeader\">Employee Name</td>"
                        + "<td width=\"17%\" class=\"tblHeader\">Designation</td>"
                        + "<td width=\"10%\" class=\"tblHeader\">Gross Salary</td>"
                        + "<td width=\"10%\" class=\"tblHeader\">Excess Pay</td>"
                        + "<td width=\"10%\" class=\"tblHeader\">Remark</td>"
                        + "</tr>");

            } else if (schedule.equals("PLS")) {

                header = new StringBuffer("<tr style=\"height:40px;\">"
                        + "<td width=\"4%\" class=\"printData\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>Sl No</b></td>"
                        + "<td width=\"30%\" class=\"printData\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>NAME AND DESIGNATION</b></td>"
                        + "<td width=\"20%\" class=\"printData\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>AMOUNT(PVT LOAN /DED DESCRIPTION)</b></td>"
                        + "</tr>");
            } else if (schedule.equals("annexure1")) {

                header = new StringBuffer("<tr style=\"height:40px;\">"
                        + "<td width=\"4%\" rowspan=\"3\" class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>Sl No</b></td>"
                        + "<td width=\"10%\" rowspan=\"3\" class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>PRAN</b></td>"
                        + "<td width=\"25%\" rowspan=\"3\" class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>Employee Name</b></td>"
                        + "<td width=\"17%\" rowspan=\"3\" class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>Designation</b></td>"
                        + "<td width=\"8%\" rowspan=\"3\" class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>Basic Pay<br> + GP (Rs.)</b></td>"
                        + "<td width=\"5%\" rowspan=\"3\" class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>D.A<br>(Rs.)</b></td>"
                        + "<td width=\"8%\" rowspan=\"3\" class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>Total <br>(Rs.)</b></td>"
                        + "<td width=\"25%\" colspan=\"4\" class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>Employees Contribution</b></td>"
                        + "</tr>"
                        + "<tr>"
                        + "<td width=\"7%\" rowspan=\"2\" class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>Current (Rs.)</b></td>"
                        + "<td width=\"18%\" colspan=\"3\" class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>Arrears</b></td>"
                        + "</tr>"
                        + "<tr>"
                        + "<td width=\"5%\" class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>Installment</b></td>"
                        + "<td width=\"6%\" class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>Amount <br>(Rs.)</b></td>"
                        + "<td width=\"7%\" class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>Total <br>(Rs.)</b></td>"
                        + "</tr>"
                        + "<tr>"
                        + "<td class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>1</b></td>"
                        + "<td class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>2</b></td>"
                        + "<td class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>3</b></td>"
                        + "<td class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>4</b></td>"
                        + "<td class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>5</b></td>"
                        + "<td class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>6</b></td>"
                        + "<td class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>7</b></td>"
                        + "<td class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>8</b></td>"
                        + "<td class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>9</b></td>"
                        + "<td class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>10</b></td>"
                        + "<td class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>11</b></td>"
                        + "</tr>");
            } else if (schedule.equals("annexure2")) {

                header = new StringBuffer("<tr style=\"height:40px;\">"
                        + "<td width=\"4%\" class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>Sl No</b></td>"
                        + "<td width=\"12%\" class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>PRAN</b></td>"
                        + "<td width=\"22%\" class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>Employee Name</b></td>"
                        + "<td width=\"20%\" class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>Designation</b></td>"
                        + "<td width=\"10%\" class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>Basic Pay<br> + GP (Rs.)</b></td>"
                        + "<td width=\"8%\" class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>D.A<br>(Rs.)</b></td>"
                        + "<td width=\"15%\" class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>Govt's Contribution</b></td>"
                        + "<td width=\"14%\" class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>Remarks</b></td>"
                        + "</tr>"
                        + "<tr>"
                        + "<td class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>1</b></td>"
                        + "<td class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>2</b></td>"
                        + "<td class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>3</b></td>"
                        + "<td class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>4</b></td>"
                        + "<td class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>5</b></td>"
                        + "<td class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>6</b></td>"
                        + "<td class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>7</b></td>"
                        + "<td class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>8</b></td>"
                        + "</tr>");
            } else if (schedule.equals("annexure3")) {

                header = new StringBuffer("<tr style=\"height:40px;\">"
                        + "<td width=\"4%\" class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>Sl No</b></td>"
                        + "<td width=\"10%\" class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>PRAN</b></td>"
                        + "<td width=\"25%\" class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>Employee Name</b></td>"
                        + "<td width=\"17%\" class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>Designation</b></td>"
                        + "<td width=\"10%\" class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>Bill No and Date of Employees Contribution</b></td>"
                        + "<td width=\"10%\" class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>Amount of Employees Contribution</b></td>"
                        + "<td width=\"10%\" class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>Bill No & Date of Govt. Contribution</b></td>"
                        + "<td width=\"10%\" class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>Amount of Govt. Contribution</b></td>"
                        + "<td width=\"10%\" class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>Total in Rs.</b></td>"
                        + "</tr>"
                        + "<tr>"
                        + "<td class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>1</b></td>"
                        + "<td class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>2</b></td>"
                        + "<td class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>3</b></td>"
                        + "<td class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>4</b></td>"
                        + "<td class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>5</b></td>"
                        + "<td class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>6</b></td>"
                        + "<td class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>7</b></td>"
                        + "<td class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>8</b></td>"
                        + "<td class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>9</b></td>"
                        + "</tr>");
            } else if (schedule.equals("annexure4")) {

                header = new StringBuffer("<tr style=\"height:40px;\">"
                        + "<td width=\"4%\" class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>Sl No</b></td>"
                        + "<td width=\"10%\" class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>Treasury Officers <br/>Regd No(issued by CRA)</b></td>"
                        + "<td width=\"17%\" class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>PRAN</b></td>"
                        + "<td width=\"25%\" class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>Employee Name</b></td>"
                        + "<td width=\"10%\" class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>Basic Pay + GP + DA Rs.</b></td>"
                        + "<td width=\"10%\" class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>Employees Contribution under Tier-I Rs.</b></td>"
                        + "<td width=\"10%\" class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>Government Contribution under Tier-I Rs.</b></td>"
                        + "<td width=\"10%\" class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>Total</b></td>"
                        + "<td width=\"10%\" class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>Remark</b></td>"
                        + "</tr>"
                        + "<tr>"
                        + "<td class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>1</b></td>"
                        + "<td class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>2</b></td>"
                        + "<td class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>3</b></td>"
                        + "<td class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>4</b></td>"
                        + "<td class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>5</b></td>"
                        + "<td class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>6</b></td>"
                        + "<td class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>7</b></td>"
                        + "<td class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>8</b></td>"
                        + "<td class=\"tblHeader\" style=\"font-family:verdana;font-size:12px;text-align:center;border:1px solid black;\"><b>9</b></td>"
                        + "</tr>");
            } else if (schedule.equals("PT")) {

                header = new StringBuffer("<tr style=\"height:40px;\">"
                        + "<td width=\"5%\" class=\"tblHeader\" style=\"text-align:center;\">Sl No</td>"
                        + "<td width=\"25%\" class=\"tblHeader\" style=\"text-align:center;\">Employee Name</td>"
                        + "<td width=\"35%\" class=\"tblHeader\" style=\"text-align:center;\">Designation</td>"
                        + "<td width=\"15%\" class=\"tblHeader\" style=\"text-align:center;\">Gross Salary</td>"
                        + "<td width=\"10%\" class=\"tblHeader\" style=\"text-align:center;\">Tax on Profession</td>"
                        + "<td width=\"10%\" class=\"tblHeader\" style=\"text-align:center;\">Remark</td>"
                        + "</tr>");
            } else if (schedule.equals("GISA") || schedule.equals("FA") || schedule.equals("OR") || schedule.equals("ADVPAY")) {

                header = new StringBuffer("<tr style=\"height:40px;\">"
                        + "<td width=\"3%\" class=\"tblHeader\" style=\"text-align:center;\">Sl No</td>"
                        + "<td width=\"20%\" class=\"tblHeader\" style=\"text-align:center;\">NAME OF THE EMPLOYEE/ DESIGNATION</td>"
                        + "<td width=\"15%\" class=\"tblHeader\" style=\"text-align:center;\">T.V. NO. IN WHICH ORIGINAL ADV DRAWN WITH TREASURY NAME</td>"
                        + "<td width=\"8%\" class=\"tblHeader\" style=\"text-align:center;\">ACCOUNT NO</td>"
                        + "<td width=\"8%\" class=\"tblHeader\" style=\"text-align:center;\">AMOUNT OF ORIGINAL ADVANCE</td>"
                        + "<td width=\"8%\" class=\"tblHeader\" style=\"text-align:center;\">NO OF INSTALLMENT OF RECOVERY</td>"
                        + "<td width=\"8%\" class=\"tblHeader\" style=\"text-align:center;\">AMOUNT DEDUCTED IN THE BILL</td>"
                        + "<td width=\"8%\" class=\"tblHeader\" style=\"text-align:center;\">RECOVERY UPTO THE MONTH</td>"
                        + "<td width=\"8%\" class=\"tblHeader\" style=\"text-align:center;\">BALANCE OUTSTANDING</td>"
                        + "<td width=\"8%\" class=\"tblHeader\" style=\"text-align:center;\">REMARKS</td>"
                        + "</tr>");
            } else if (schedule.equals("BS")) {

                header = new StringBuffer("<tr style=\"height:40px;\">"
                        + "<td width=\"3%\" class=\"tblHeader\" style=\"text-align:center;\">Sl No</td>"
                        + "<td width=\"18%\" class=\"tblHeader\" style=\"text-align:center;\">SB A/C No. /  <br> Name of the Bank</td>"
                        + "<td width=\"30%\" class=\"tblHeader\" style=\"text-align:center;\">Name/<br> Designation</td>"
                        + "<td width=\"12%\" class=\"tblHeader\" style=\"text-align:center;\">(PF No) <br> Net Amount <br> (in Rs)</td>"
                        + "<td width=\"12%\" class=\"tblHeader\" style=\"text-align:center;\">Loan/ Advance <br> Liability <br> Amount (in Rs)</td>"
                        + "<td width=\"12%\" class=\"tblHeader\" style=\"text-align:center;\">Amt Credited to <br> SB A/C of the <br> employee (in Rs)</td>"
                        + "<td width=\"18%\" class=\"tblHeader\" style=\"text-align:center;\">Amt Credited to <br> CA A/C of the <br> DDO (in Rs)</td>"
                        + "</tr>");
            } else if (schedule.equals("LIC")) {

                header = new StringBuffer("<tr class=\"tblHeader\" style=\"height:40px;\">"
                        + "<td width=\"4%\" class=\"printData\" style=\"text-align:center;border:1px solid black;\">Sl No</td>"
                        + "<td width=\"20%\" class=\"printData\" style=\"text-align:center;border:1px solid black;\">Name of the Employee</td>"
                        + "<td width=\"20%\" class=\"printData\" style=\"text-align:center;border:1px solid black;\">Designation</td>"
                        + "<td width=\"10%\" class=\"printData\" style=\"text-align:center;border:1px solid black;\">Policy No.</td>"
                        + "<td width=\"10%\" class=\"printData\" style=\"text-align:center;border:1px solid black;\">Recovery Month</td>"
                        + "<td width=\"10%\" class=\"printData\" style=\"text-align:center;border:1px solid black;\">Amount</td>"
                        + "<td width=\"10%\" class=\"printData\" style=\"text-align:center;border:1px solid black;\">Total</td>"
                        + "<td width=\"10%\" class=\"printData\" style=\"text-align:center;border:1px solid black;\">Remark</td>"
                        + "</tr>");
            } else if (schedule.equals("AR")) {

                header = new StringBuffer("<tr class=\"tblHeader\" style=\"height:40px;\">"
                        + "<td width=\"4%\" class=\"tblHeader\" style=\"text-align:center;border:1px solid black;\">Sl No</td>"
                        + "<td width=\"22%\" class=\"tblHeader\" style=\"text-align:center;border:1px solid black;\">Name of the Incumbents with Designation</td>"
                        + "<td width=\"10%\" class=\"tblHeader\" style=\"text-align:center;border:1px solid black;\">Amount of Recoveries</td>"
                        + "<td width=\"10%\" class=\"tblHeader\" style=\"text-align:center;border:1px solid black;\">Amount to be deducted now</td>"
                        + "<td width=\"10%\" class=\"tblHeader\" style=\"text-align:center;border:1px solid black;\">No. of instalmenth</td>"
                        + "<td width=\"10%\" class=\"tblHeader\" style=\"text-align:center;border:1px solid black;\">Balance</td>"
                        + "<td width=\"10%\" class=\"tblHeader\" style=\"text-align:center;border:1px solid black;\">Audit report No.& para</td>"
                        + "<td width=\"10%\" class=\"tblHeader\" style=\"text-align:center;border:1px solid black;\">Head of Account to be Credited</td>"
                        + "<td width=\"8%\" class=\"tblHeader\" style=\"text-align:center;border:1px solid black;\">REMARKS</td>"
                        + "</tr>");
            }
            //next code here

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
        }
        return header;
    }

    private StringBuffer reportCarryForward(int pagetotal, String schedule) {

        StringBuffer carryforward = null;

        try {
            if (schedule.equals("GPF")) {
                carryforward = new StringBuffer("<table width=\"100%\" border=\"0\" id=\"tblcarryforward\" style=\"left: 18px;\" style=\"font-size:11px;\" cellpadding=\"0\" cellspacing=\"0\">"
                        + "<tr style=\"height:30px\">"
                        + "<td colspan=\"8\" style=\"text-align:right;\" class=\"txtf\">"
                        + "Carry Forward  " + pagetotal + ""
                        + "</td>"
                        + "</tr>"
                        + "</table>");
            } else if (schedule.equals("WRR") || schedule.equals("SWR") || schedule.equals("HRR")) {
                carryforward = new StringBuffer("<table width=\"100%\" border=\"0\" id=\"tblcarryforward\" style=\"left: 18px;\" style=\"font-size:11px;\" cellpadding=\"0\" cellspacing=\"0\">"
                        + "<tr style=\"height:30px\">"
                        + "<td colspan=\"5\" style=\"text-align:right;\" class=\"txtf\">"
                        + "Carry Forward  " + pagetotal + ""
                        + "</td>"
                        + "</tr>"
                        + "</table>");
            } else if (schedule.equals("IT") || schedule.equals("HC") || schedule.equals("GIS") || schedule.equals("CGEGIS")) {
                carryforward = new StringBuffer("<tr style=\"height: 30px\">"
                        + "<td style=\"text-align: center\">Grand Total</td>\n"
                        + "<td>&nbsp;</td>\n"
                        + "<td>&nbsp;</td>\n"
                        + "<td>&nbsp;</td>\n"
                        + "<td style=\"text-align: right\">" + pagetotal + "</td>"
                        + "</tr>");
                carryforward.append("<tr style=\"height: 30px\">"
                        + "<td align=\"center\" colspan=\"5\">In Words(RUPEES" + Numtowordconvertion.convertNumber(pagetotal) + ") ONLY</td>"
                        + "</tr>");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return carryforward;
    }

    @Override
    public void billFrontPagePDF(Document document, String billNo, BillChartOfAccount billChartOfAccount, List allowanceList, ArrayList deductionList) {

        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        BillFrontPageBean bfpbean = new BillFrontPageBean();
        double totAllowanceAmt = 0.0;
        double totDeductAmt = 0.0;
        Schedule obj = null;
        double netAmt = 0.0;

        try {
            con = this.dataSource.getConnection();
            getBenRefNo(bfpbean, billNo);
            getDescriptionDetails(bfpbean, billNo);

            if (allowanceList != null && allowanceList.size() > 0) {
                for (int i = 0; i < allowanceList.size(); i++) {
                    obj = (Schedule) allowanceList.get(i);
                    totAllowanceAmt = obj.getAlowanceTotal();
                }
            }

            if (deductionList != null && deductionList.size() > 0) {
                for (int i = 0; i < deductionList.size(); i++) {
                    obj = (Schedule) deductionList.get(i);
                    double schAmt = Double.parseDouble(obj.getSchAmount());
                    totDeductAmt = totDeductAmt + schAmt;
                }
            }

            netAmt = totAllowanceAmt - totDeductAmt;

            Font f1 = new Font();
            f1.setSize(8.5f);
            f1.setFamily("Times New Roman");

            //start of first table
            PdfPTable maintable = new PdfPTable(4);
            maintable.setWidths(new int[]{3, 2, 2, 1});
            maintable.setWidthPercentage(100);

            PdfPCell maincell = null;

            //START OF FIRST MAIN ROW
            maincell = new PdfPCell(new Phrase("Schedule LIII - Form No. 188", new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.BOLD)));
            maincell.setBorder(Rectangle.NO_BORDER);
            maintable.addCell(maincell);
            maincell = new PdfPCell(new Phrase(StringUtils.defaultString(billChartOfAccount.getDdoName()), f1));
            //maincell.setBorder(Rectangle.NO_BORDER);
            maincell.setHorizontalAlignment(Element.ALIGN_CENTER);
            maintable.addCell(maincell);
            maincell = new PdfPCell(new Phrase("Bill No: " + StringUtils.defaultString(billChartOfAccount.getBilldesc()), new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.BOLD)));
            maincell.setBorder(Rectangle.NO_BORDER);
            maincell.setHorizontalAlignment(Element.ALIGN_CENTER);
            maintable.addCell(maincell);
            maincell = new PdfPCell(new Phrase("P", new Font(Font.FontFamily.TIMES_ROMAN, 30, Font.NORMAL)));
            maincell.setRowspan(3);
            maincell.setBorder(Rectangle.NO_BORDER);
            maintable.addCell(maincell);

            maincell = new PdfPCell(new Phrase("Detailed Pay Bill of Permanent/Temporary Establishment of the " + StringUtils.defaultString(billChartOfAccount.getOffName()), new Font(Font.FontFamily.TIMES_ROMAN, 7, Font.NORMAL)));
            maincell.setColspan(2);
            maincell.setBorder(Rectangle.NO_BORDER);
            maintable.addCell(maincell);
            maincell = new PdfPCell(new Phrase("(O.T.C.Form No.22)", new Font(Font.FontFamily.TIMES_ROMAN, 7, Font.BOLD)));
            maincell.setBorder(Rectangle.NO_BORDER);
            maincell.setHorizontalAlignment(Element.ALIGN_CENTER);
            maintable.addCell(maincell);
            /*maincell = new PdfPCell(new Phrase("",f1));
             maincell.setBorder(Rectangle.NO_BORDER);
             maintable.addCell(maincell);*/

            maincell = new PdfPCell(new Phrase("for the month of " + StringUtils.defaultString(billChartOfAccount.getBillMonth()), new Font(Font.FontFamily.TIMES_ROMAN, 7, Font.NORMAL)));
            maincell.setBorder(Rectangle.NO_BORDER);
            maintable.addCell(maincell);
            maincell = new PdfPCell(new Phrase("", f1));
            maincell.setBorder(Rectangle.NO_BORDER);
            maintable.addCell(maincell);
            maincell = new PdfPCell(new Phrase("District : " + StringUtils.defaultString(billChartOfAccount.getDistrict()), new Font(Font.FontFamily.TIMES_ROMAN, 6, Font.NORMAL)));
            maincell.setColspan(2);
            maincell.setBorder(Rectangle.NO_BORDER);
            maintable.addCell(maincell);

            maincell = new PdfPCell();
            maincell.setBorder(Rectangle.NO_BORDER);
            maintable.addCell(maincell);
            maincell = new PdfPCell();
            maincell.setBorder(Rectangle.NO_BORDER);
            maintable.addCell(maincell);
            maincell = new PdfPCell(new Phrase("Ben Ref No : " + StringUtils.defaultString(billChartOfAccount.getBenRefNo()), new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
            maincell.setColspan(2);
            maincell.setBorder(Rectangle.NO_BORDER);
            maintable.addCell(maincell);

            maincell = new PdfPCell();
            maincell.setBorder(Rectangle.NO_BORDER);
            maintable.addCell(maincell);
            if (billChartOfAccount.getBillType() != null && !billChartOfAccount.getBillType().equals("22")) {
                if (billChartOfAccount.getTokenNo() == null || billChartOfAccount.getTokenNo().equals("")) {
                    maincell = new PdfPCell(new Phrase("DRAFT", new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD)));
                    maincell.setBorder(Rectangle.NO_BORDER);
                    maintable.addCell(maincell);
                } else {
                    maincell = new PdfPCell();
                    maincell.setBorder(Rectangle.NO_BORDER);
                    maintable.addCell(maincell);
                }
            } else {
                maincell = new PdfPCell();
                maincell.setBorder(Rectangle.NO_BORDER);
                maintable.addCell(maincell);
            }
            maincell = new PdfPCell(new Phrase("Token No : " + StringUtils.defaultString(billChartOfAccount.getTokenNo()), new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
            maincell.setColspan(2);
            maincell.setBorder(Rectangle.NO_BORDER);
            maintable.addCell(maincell);
            //end of first table
            document.add(maintable);
            //END OF FIRST MAIN ROW

            //START OF SECOND MAIN ROW
            //start of second table
            maintable = new PdfPTable(2);
            maintable.setWidths(new float[]{6.5f, 4.5f});
            maintable.setWidthPercentage(100);

            //PdfPCell maincell = null;
            //start of Creating Space
            maincell = new PdfPCell();
            maincell.setColspan(2);
            maincell.setFixedHeight(20);
            maincell.setBorder(Rectangle.NO_BORDER);
            maintable.addCell(maincell);
            //end of Creating Space

            //start of first row under SECOND MAIN ROW
            maincell = new PdfPCell(new Phrase("Space for classification stamp of manuscript entries of classification to be filled in by Drawing Officer,"
                    + " Name of detailed heads and corresponding amounts should be recorded by him in adjacent column.", f1));
            maincell.setBorder(Rectangle.TOP | Rectangle.RIGHT);
            maintable.addCell(maincell);
            maincell = new PdfPCell(new Phrase(StringUtils.leftPad("VOUCHER\n", 50) + "of" + StringUtils.repeat(" ", 80) + "list\n" + "for", f1));
            maincell.setBorder(Rectangle.TOP);
            maincell.setHorizontalAlignment(Rectangle.ALIGN_LEFT);
            maintable.addCell(maincell);
            //end of first row under SECOND MAIN ROW
            //start of Creating Space
            maincell = new PdfPCell();
            maincell.setFixedHeight(10);
            maincell.setBorder(Rectangle.RIGHT);
            maintable.addCell(maincell);
            maincell = new PdfPCell();
            maincell.setBorder(Rectangle.BOTTOM);
            maintable.addCell(maincell);
            //end of Creating Space

            //start of second row under SECOND MAIN ROW
            //start of 1st inner table under second table for column 1
            PdfPTable innertable1 = new PdfPTable(2);
            innertable1.setWidths(new int[]{2, 2});
            innertable1.setWidthPercentage(100);

            PdfPCell innercell = null;

            innercell = new PdfPCell(new Phrase("Demand No", f1));
            innercell.setBorder(Rectangle.NO_BORDER);
            innertable1.addCell(innercell);
            innercell = new PdfPCell(new Phrase("- " + StringUtils.defaultString(billChartOfAccount.getDemandNo()), f1));
            innercell.setBorder(Rectangle.RIGHT);
            innertable1.addCell(innercell);

            innercell = new PdfPCell(new Phrase("Major Head", f1));
            innercell.setBorder(Rectangle.NO_BORDER);
            innertable1.addCell(innercell);
            innercell = new PdfPCell(new Phrase("- " + StringUtils.defaultString(billChartOfAccount.getMajorHead()), f1));
            innercell.setBorder(Rectangle.RIGHT);
            innertable1.addCell(innercell);

            innercell = new PdfPCell(new Phrase("Sub Major Head", f1));
            innercell.setBorder(Rectangle.NO_BORDER);
            innertable1.addCell(innercell);
            innercell = new PdfPCell(new Phrase("- " + StringUtils.defaultString(billChartOfAccount.getSubMajorHead()), f1));
            innercell.setBorder(Rectangle.RIGHT);
            innertable1.addCell(innercell);

            innercell = new PdfPCell(new Phrase("Minor Head", f1));
            innercell.setBorder(Rectangle.NO_BORDER);
            innertable1.addCell(innercell);
            innercell = new PdfPCell(new Phrase("- " + StringUtils.defaultString(billChartOfAccount.getMinorHead()), f1));
            innercell.setBorder(Rectangle.RIGHT);
            innertable1.addCell(innercell);

            innercell = new PdfPCell(new Phrase("Sub head", f1));
            innercell.setBorder(Rectangle.NO_BORDER);
            innertable1.addCell(innercell);
            innercell = new PdfPCell(new Phrase("- " + StringUtils.defaultString(billChartOfAccount.getSubMinorHead1()), f1));
            innercell.setBorder(Rectangle.RIGHT);
            innertable1.addCell(innercell);

            innercell = new PdfPCell(new Phrase("Detail Head", f1));
            innercell.setBorder(Rectangle.NO_BORDER);
            innertable1.addCell(innercell);
            innercell = new PdfPCell(new Phrase("- " + StringUtils.defaultString(billChartOfAccount.getSubMinorHead2()), f1));
            innercell.setBorder(Rectangle.RIGHT);
            innertable1.addCell(innercell);

            innercell = new PdfPCell(new Phrase("Plan Status", f1));
            innercell.setBorder(Rectangle.NO_BORDER);
            innertable1.addCell(innercell);
            innercell = new PdfPCell(new Phrase("- " + StringUtils.defaultString(billChartOfAccount.getPlanName()), f1));
            innercell.setBorder(Rectangle.RIGHT);
            innertable1.addCell(innercell);

            innercell = new PdfPCell(new Phrase("Charge/Voted", f1));
            innercell.setBorder(Rectangle.NO_BORDER);
            innertable1.addCell(innercell);
            innercell = new PdfPCell(new Phrase("- " + StringUtils.defaultString(billChartOfAccount.getSubMinorHead3()), f1));
            innercell.setBorder(Rectangle.RIGHT);
            innertable1.addCell(innercell);

            innercell = new PdfPCell(new Phrase("Sector", f1));
            innercell.setBorder(Rectangle.NO_BORDER);
            innertable1.addCell(innercell);
            innercell = new PdfPCell(new Phrase("- " + StringUtils.defaultString(billChartOfAccount.getSectorName()), f1));
            innercell.setBorder(Rectangle.RIGHT);
            innertable1.addCell(innercell);
            //end of 1st inner table under second table for column1    
            maincell = new PdfPCell(innertable1);
            maincell.setBorder(Rectangle.NO_BORDER);
            maintable.addCell(maincell);

            //start of second inner table under second table for column 2
            innertable1 = new PdfPTable(3);
            innertable1.setWidths(new float[]{3, 1.5f, 0.5f});
            innertable1.setWidthPercentage(100);

            //PdfPCell innercell = null;
            innercell = new PdfPCell();
            innercell.setBorder(Rectangle.TOP);
            innertable1.addCell(innercell);
            innercell = new PdfPCell(new Phrase("Rs", f1));
            innercell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.LEFT);
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innertable1.addCell(innercell);
            innercell = new PdfPCell(new Phrase("P", f1));
            innercell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.LEFT);
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innertable1.addCell(innercell);

            innercell = new PdfPCell(new Phrase("Pay of Permanent Establishment", f1));
            innercell.setBorder(Rectangle.RIGHT);
            innertable1.addCell(innercell);
            innercell = new PdfPCell();
            innercell.setBorder(Rectangle.RIGHT);
            innertable1.addCell(innercell);
            innercell = new PdfPCell();
            innercell.setBorder(Rectangle.NO_BORDER);
            innertable1.addCell(innercell);

            innercell = new PdfPCell(new Phrase("Pay of temporary Establishment", f1));
            innercell.setBorder(Rectangle.RIGHT);
            innertable1.addCell(innercell);
            innercell = new PdfPCell();
            innercell.setBorder(Rectangle.RIGHT);
            innertable1.addCell(innercell);
            innercell = new PdfPCell();
            innercell.setBorder(Rectangle.NO_BORDER);
            innertable1.addCell(innercell);

            Font f2 = new Font();
            f2.setSize(9);

            if (allowanceList != null && allowanceList.size() > 0) {
                for (int i = 0; i < allowanceList.size(); i++) {
                    obj = (Schedule) allowanceList.get(i);
                    innercell = new PdfPCell(new Phrase(obj.getObjectHead() + StringUtils.repeat(" ", 30) + StringUtils.defaultString(obj.getScheduleName()), f2));
                    innercell.setBorder(Rectangle.RIGHT);
                    innertable1.addCell(innercell);
                    innercell = new PdfPCell(new Phrase(obj.getSchAmount(), f2));
                    innercell.setBorder(Rectangle.RIGHT);
                    innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    innertable1.addCell(innercell);
                    innercell = new PdfPCell();
                    innercell.setBorder(Rectangle.NO_BORDER);
                    innertable1.addCell(innercell);
                }
            }

            innercell = new PdfPCell(new Phrase("Total", f1));
            innercell.setBorder(Rectangle.TOP | Rectangle.RIGHT);
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innertable1.addCell(innercell);
            innercell = new PdfPCell(new Phrase(StringUtils.defaultString(totAllowanceAmt + ""), f1));
            innercell.setBorder(Rectangle.TOP | Rectangle.RIGHT | Rectangle.BOTTOM);
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innertable1.addCell(innercell);
            //innercell = new PdfPCell(new Phrase(StringUtils.defaultString(billFrForm.getTotalPaice()), f1));
            innercell = new PdfPCell();
            innercell.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innertable1.addCell(innercell);

            maincell = new PdfPCell(innertable1);
            maincell.setBorder(Rectangle.NO_BORDER);
            maintable.addCell(maincell);
            //end of second row under SECOND MAIN ROW

            //start of third row under SECOND MAIN ROW
            maincell = new PdfPCell(new Phrase("\nN.B", f1));
            maincell.setBorder(Rectangle.RIGHT);
            maintable.addCell(maincell);

            //start of inner table
            innertable1 = new PdfPTable(3);
            innertable1.setWidths(new float[]{3, 1.5f, 0.5f});
            innertable1.setWidthPercentage(100);

            innercell = new PdfPCell(new Phrase("\nDeduct-", f1));
            innercell.setBorder(Rectangle.RIGHT);
            innertable1.addCell(innercell);

            innercell = new PdfPCell();
            innercell.setBorder(Rectangle.RIGHT);
            innertable1.addCell(innercell);

            innercell = new PdfPCell();
            innercell.setBorder(Rectangle.NO_BORDER);
            innertable1.addCell(innercell);
            //end of inner table
            maincell = new PdfPCell(innertable1);
            maincell.setBorder(Rectangle.NO_BORDER);
            maintable.addCell(maincell);
                        //start of third row under SECOND MAIN ROW

            //start of fourth row under SECOND MAIN ROW
            maincell = new PdfPCell(new Phrase(" 1.Hold over amounts should be entered in red ink in the appropriate col. 3,4,5 and 6 as the"
                    + " case may be and ignored in totalling. Leave salary the amount of which is not known, should Similarly"
                    + " be entered in red in col. 4 at the same rate as pay if he has remained on duty (S.T.R. 55).\n  2. In the Remarks column 15 should be recorded all unusal permanent events such as death,"
                    + "retirements, transfers and first appointments which find no place increment certificates or absentee"
                    + "statement.\n  3. When the increment claimed operates to carry a Government Servant Govt. to efficiency bar"
                    + "it should be supported by a declaration that the Government servants in question is fit to pass the bar"
                    + " (S.T.R. 6).\n  4. Names of Government servants in inferior services as well as those mentioned [S.T.R.55(3)]"
                    + " may be omitted from pay bill (S.T.R.55).\n  5. A red line should be drawn right across the sheet after each section of the punishment and"
                    + " under it is totals of columns 4,5,6 and 7 and 8 of the section should be shown in red ink.\n  6. In cases where the amount of leave salary is based on average pay separate statement"
                    + " showing the calculation of average pay duly attested by Drawing Officer should be attachment to this"
                    + " bill vide [S.T.R.55 (3)].\n  7. The names of men holding post substantively should be entered in order of Seniority as"
                    + " measured by substantive pay drawn and below those will be shown the parts left vacant and the men"
                    + " officiating in the vacancies.\n  8. Officiating pay should be record in the section of the bill appropriate to that in which the"
                    + " Government servant officiates and transit pay should be recorded in the same section as that in which"
                    + " the duty pay of the Government servant after transfer is recorded."
                    + "\n  9. The following abbreviations should be use in this and in all other document, submitted with"
                    + " pay bill.", f1));
            maincell.setBorder(Rectangle.RIGHT);
            maintable.addCell(maincell);

            //start of third inner table under second inner table under second table for column 2
            innertable1 = new PdfPTable(4);
            innertable1.setWidths(new float[]{2, 1, 1.5f, 0.5f});
            innertable1.setWidthPercentage(100);
            //ArrayList 
            if (deductionList != null && deductionList.size() > 0) {
                Iterator itr = deductionList.iterator();
                while (itr.hasNext()) {
                    obj = (Schedule) itr.next();
                    innercell = new PdfPCell(new Phrase(StringUtils.defaultString(obj.getObjectHead()), f2));
                    innercell.setBorder(Rectangle.NO_BORDER);
                    innertable1.addCell(innercell);
                    innercell = new PdfPCell(new Phrase(StringUtils.defaultString(obj.getScheduleName()), f2));
                    innercell.setBorder(Rectangle.RIGHT);
                    innercell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    innertable1.addCell(innercell);
                    innercell = new PdfPCell(new Phrase(StringUtils.defaultString(obj.getSchAmount()), f2));
                    innercell.setBorder(Rectangle.RIGHT);
                    innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    innertable1.addCell(innercell);
                    innercell = new PdfPCell();
                    innercell.setBorder(Rectangle.NO_BORDER);
                    innertable1.addCell(innercell);
                }
            }
            //end of third inner table under second inner table under second table for column 2
            maincell = new PdfPCell(innertable1);
            maincell.setBorder(Rectangle.NO_BORDER);
            maintable.addCell(maincell);
                    //end of fourth row under SECOND MAIN ROW

            //start of fifth row under SECOND MAIN ROW
            //start of inner table for fifth row under SECOND MAIN ROW
            innertable1 = new PdfPTable(3);
            innertable1.setWidths(new float[]{2.5f, 2, 1});
            innertable1.setWidthPercentage(100);

            innercell = new PdfPCell(new Phrase("Leave on average pay", f1));
            innercell.setBorder(Rectangle.NO_BORDER);
            innertable1.addCell(innercell);
            innercell = new PdfPCell(new Phrase("- LAP Under suspension", f1));
            innercell.setBorder(Rectangle.NO_BORDER);
            innertable1.addCell(innercell);
            innercell = new PdfPCell(new Phrase("- SP", f1));
            innercell.setBorder(Rectangle.NO_BORDER);
            innertable1.addCell(innercell);

            innercell = new PdfPCell(new Phrase("Leave on quarter average pay", f1));
            innercell.setBorder(Rectangle.NO_BORDER);
            innertable1.addCell(innercell);
            innercell = new PdfPCell(new Phrase("- LIP Vacant", f1));
            innercell.setBorder(Rectangle.NO_BORDER);
            innertable1.addCell(innercell);
            innercell = new PdfPCell(new Phrase("- A", f1));
            innercell.setBorder(Rectangle.NO_BORDER);
            innertable1.addCell(innercell);

            innercell = new PdfPCell(new Phrase("On other duty", f1));
            innercell.setBorder(Rectangle.NO_BORDER);
            innertable1.addCell(innercell);
            innercell = new PdfPCell(new Phrase("- OD Post Life Insurance", f1));
            innercell.setBorder(Rectangle.NO_BORDER);
            innertable1.addCell(innercell);
            innercell = new PdfPCell(new Phrase("- I", f1));
            innercell.setBorder(Rectangle.NO_BORDER);
            innertable1.addCell(innercell);

            innercell = new PdfPCell(new Phrase("Leave Salary", f1));
            innercell.setBorder(Rectangle.NO_BORDER);
            innertable1.addCell(innercell);
            innercell = new PdfPCell(new Phrase("- LS Last Pay Certificate", f1));
            innercell.setBorder(Rectangle.NO_BORDER);
            innertable1.addCell(innercell);
            innercell = new PdfPCell(new Phrase("- LP", f1));
            innercell.setBorder(Rectangle.NO_BORDER);
            innertable1.addCell(innercell);

            innercell = new PdfPCell(new Phrase("Conveyance allowance", f1));
            innercell.setBorder(Rectangle.NO_BORDER);
            innertable1.addCell(innercell);
            innercell = new PdfPCell(new Phrase("- CA Subsistence grant", f1));
            innercell.setBorder(Rectangle.NO_BORDER);
            innertable1.addCell(innercell);
            innercell = new PdfPCell(new Phrase("- Sub grant", f1));
            innercell.setBorder(Rectangle.NO_BORDER);
            innertable1.addCell(innercell);

            innercell = new PdfPCell(new Phrase("Transit pay", f1));
            innercell.setBorder(Rectangle.NO_BORDER);
            innertable1.addCell(innercell);
            innercell = new PdfPCell(new Phrase("- TP", f1));
            innercell.setBorder(Rectangle.NO_BORDER);
            innertable1.addCell(innercell);
            innercell = new PdfPCell(new Phrase("", f1));
            innercell.setBorder(Rectangle.NO_BORDER);
            innertable1.addCell(innercell);
            //end of inner table for fifth row under SECOND MAIN ROW
            maincell = new PdfPCell(innertable1);
            maincell.setBorder(Rectangle.RIGHT);
            maintable.addCell(maincell);

            //start of inner table for column 2 for fifth row under SECOND MAIN ROW
            innertable1 = new PdfPTable(3);
            innertable1.setWidths(new float[]{3, 1.5f, 0.5f});
            innertable1.setWidthPercentage(100);

            innercell = new PdfPCell(new Phrase("\n\nTotal deductions", f1));
            innercell.setBorder(Rectangle.RIGHT);
            innertable1.addCell(innercell);

            innercell = new PdfPCell(new Phrase("\n\n" + StringUtils.defaultString(totDeductAmt + ""), f1));
            innercell.setBorder(Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innertable1.addCell(innercell);
            //innercell = new PdfPCell(new Phrase("\n\n" + StringUtils.defaultString(billFrForm.getDeductPaice()), f1));
            innercell = new PdfPCell();
            innercell.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innertable1.addCell(innercell);

            innercell = new PdfPCell(new Phrase("\nNet Total", f1));
            innercell.setBorder(Rectangle.RIGHT);
            innertable1.addCell(innercell);
            innercell = new PdfPCell(new Phrase("\n" + StringUtils.defaultString(netAmt + ""), f1));
            innercell.setBorder(Rectangle.RIGHT | Rectangle.TOP);
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innertable1.addCell(innercell);
            //innercell = new PdfPCell(new Phrase("\n" + StringUtils.defaultString(billFrForm.getNetPaice()), f1));
            innercell = new PdfPCell();
            innercell.setBorder(Rectangle.TOP);
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innertable1.addCell(innercell);
            //end of inner table for column 2 for fifth row under SECOND MAIN ROW
            maincell = new PdfPCell(innertable1);
            maincell.setBorder(Rectangle.NO_BORDER);
            maintable.addCell(maincell);
            //end of fifth row under SECOND MAIN ROW

            //start of sixth row under SECOND MAIN ROW
            maincell = new PdfPCell(new Phrase("\n  10. In cases where any fast one sesiocladeo in pay bill, a separate schepe showing the"
                    + " particulars of deduction relating to each fund should accompany the bill.", f1));
            maincell.setBorder(Rectangle.RIGHT | Rectangle.BOTTOM);
            maintable.addCell(maincell);
            maincell = new PdfPCell();
            maincell.setBorder(Rectangle.BOTTOM);
            maintable.addCell(maincell);
            //end of sixth row under SECOND MAIN ROW
            //END OF SECOND MAIN ROW

            //START OF THIRD MAIN ROW
            maincell = new PdfPCell(new Phrase("FOR THE USE OF THE ACCOUNT GENERAL'S OFFICE", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD)));
            maincell.setColspan(2);
            maincell.setBorder(Rectangle.NO_BORDER);
            maincell.setHorizontalAlignment(Element.ALIGN_CENTER);
            maintable.addCell(maincell);

            maincell = new PdfPCell(new Phrase("Admitted Rs.", new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.BOLD)));
            maincell.setColspan(2);
            maincell.setBorder(Rectangle.NO_BORDER);
            maincell.setHorizontalAlignment(Element.ALIGN_LEFT);
            maintable.addCell(maincell);

            maincell = new PdfPCell(new Phrase("Object Rs.", new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.BOLD)));
            maincell.setColspan(2);
            maincell.setBorder(Rectangle.NO_BORDER);
            maincell.setHorizontalAlignment(Element.ALIGN_LEFT);
            maintable.addCell(maincell);

            maincell = new PdfPCell(new Phrase("Auditor" + StringUtils.repeat(" ", 60) + "Superitendent", new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.BOLD)));
            maincell.setBorder(Rectangle.NO_BORDER);
            maincell.setHorizontalAlignment(Element.ALIGN_LEFT);
            maintable.addCell(maincell);
            maincell = new PdfPCell(new Phrase("Gazetted Officer", new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.BOLD)));
            maincell.setBorder(Rectangle.NO_BORDER);
            maincell.setHorizontalAlignment(Element.ALIGN_LEFT);
            maintable.addCell(maincell);

            maincell = new PdfPCell();
            maincell.setColspan(2);
            maincell.setFixedHeight(20);
            maincell.setBorder(Rectangle.BOTTOM);
            maintable.addCell(maincell);

            maincell = new PdfPCell(new Phrase("S.T.R. means Subsidiary Rules under the Orissa Treasury Rules.\nThe deduct entries relating to the Provident fund should be posted separately for the Sterling and Ordinary Brand as.", f1));
            maincell.setColspan(2);
            maincell.setBorder(Rectangle.NO_BORDER);
            maintable.addCell(maincell);
            //END OF THIRD MAIN ROW
            document.add(maintable);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    private BillFrontPageBean getBenRefNo(BillFrontPageBean bfpbean, String billNo) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            con = this.dataSource.getConnection();

            String sql = "SELECT BEN_REF_NO,TOKEN_NO,BILL_TYPE FROM BILL_MAST WHERE BILL_NO=?";
            pst = con.prepareStatement(sql);
            pst.setInt(1, Integer.parseInt(billNo));
            rs = pst.executeQuery();
            if (rs.next()) {
                bfpbean.setRefno(rs.getString("BEN_REF_NO"));
                bfpbean.setTokenNo(rs.getString("TOKEN_NO"));
                bfpbean.setBillType(rs.getString("BILL_TYPE"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return bfpbean;
    }

    private BillFrontPageBean getDescriptionDetails(BillFrontPageBean bfpbean, String billNo) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            con = this.dataSource.getConnection();

            String sql = "SELECT OFF_EN,DIST_NAME,DDO_CODE,BILL_MAST.MAJOR_HEAD, BILL_MAST.MAJOR_HEAD_DESC, BILL_MAST.PLAN, POST_TYPE, BILL_MAST.SECTOR,SECTOR_DESC, BILL_MAST.SUB_MAJOR_HEAD,"
                    + " BILL_MAST.SUB_MAJOR_HEAD_DESC, BILL_MAST.MINOR_HEAD, BILL_MAST.MINOR_HEAD_DESC, BILL_MAST.SUB_MINOR_HEAD1, BILL_MAST.SUB_MINOR_HEAD1_DESC,"
                    + " BILL_MAST.SUB_MINOR_HEAD2, BILL_MAST.SUB_MINOR_HEAD2_DESC, BILL_MAST.SUB_MINOR_HEAD3, BILL_MAST.BILL_TYPE,DEMAND_NO FROM("
                    + " SELECT G_OFFICE.OFF_EN,G_OFFICE.DIST_CODE,BILL_MAST.MAJOR_HEAD, BILL_MAST.MAJOR_HEAD_DESC, BILL_MAST.PLAN, BILL_MAST.SECTOR, BILL_MAST.SUB_MAJOR_HEAD,"
                    + " BILL_MAST.SUB_MAJOR_HEAD_DESC, BILL_MAST.MINOR_HEAD, BILL_MAST.MINOR_HEAD_DESC, BILL_MAST.SUB_MINOR_HEAD1, BILL_MAST.SUB_MINOR_HEAD1_DESC,"
                    + " BILL_MAST.SUB_MINOR_HEAD2, BILL_MAST.SUB_MINOR_HEAD2_DESC, BILL_MAST.SUB_MINOR_HEAD3, BILL_MAST.BILL_TYPE,G_OFFICE.DDO_CODE,DEMAND_NO FROM ("
                    + " SELECT BILL_MAST.MAJOR_HEAD, BILL_MAST.MAJOR_HEAD_DESC, BILL_MAST.PLAN, BILL_MAST.SECTOR, BILL_MAST.SUB_MAJOR_HEAD,"
                    + " BILL_MAST.SUB_MAJOR_HEAD_DESC, BILL_MAST.MINOR_HEAD, BILL_MAST.MINOR_HEAD_DESC, BILL_MAST.SUB_MINOR_HEAD1, BILL_MAST.SUB_MINOR_HEAD1_DESC,"
                    + " BILL_MAST.SUB_MINOR_HEAD2, BILL_MAST.SUB_MINOR_HEAD2_DESC, BILL_MAST.SUB_MINOR_HEAD3, BILL_MAST.BILL_TYPE,DDO_CODE,DEMAND_NO,OFF_CODE FROM BILL_MAST WHERE BILL_NO=?) BILL_MAST"
                    + " LEFT OUTER JOIN G_OFFICE ON BILL_MAST.OFF_CODE=G_OFFICE.OFF_CODE) BILL_MAST"
                    + " LEFT OUTER JOIN G_DISTRICT ON BILL_MAST.DIST_CODE=G_DISTRICT.DIST_CODE"
                    + " LEFT OUTER JOIN G_SECTOR ON BILL_MAST.SECTOR=G_SECTOR.SECTOR_CODE"
                    + " LEFT OUTER JOIN G_POST_TYPE ON BILL_MAST.PLAN=G_POST_TYPE.POST_CODE";
            pst = con.prepareStatement(sql);
            pst.setInt(1, Integer.parseInt(billNo));
            rs = pst.executeQuery();
            if (rs.next()) {
                bfpbean.setDdoName(rs.getString("DDO_CODE"));
                bfpbean.setOffName(rs.getString("OFF_EN"));
                bfpbean.setDistrictName(rs.getString("DIST_NAME"));
                bfpbean.setMajorHead(rs.getString("MAJOR_HEAD"));
                bfpbean.setMajorHeadDesc(rs.getString("MAJOR_HEAD_DESC"));
                bfpbean.setPlan(rs.getString("PLAN"));
                bfpbean.setSector(rs.getString("SECTOR"));
                bfpbean.setPlanName(rs.getString("POST_TYPE"));
                bfpbean.setSectorName(rs.getString("SECTOR_DESC"));
                bfpbean.setSubMajorHead(rs.getString("SUB_MAJOR_HEAD"));
                bfpbean.setSubMajorHeadDesc(rs.getString("SUB_MAJOR_HEAD_DESC"));
                bfpbean.setMinorHead(rs.getString("MINOR_HEAD"));
                bfpbean.setMinorHeadDesc(rs.getString("MINOR_HEAD_DESC"));
                bfpbean.setSubMinorHead1(rs.getString("SUB_MINOR_HEAD1"));
                bfpbean.setSubMinorHeadDesc1(rs.getString("SUB_MINOR_HEAD1_DESC"));
                bfpbean.setSubMinorHead2(rs.getString("SUB_MINOR_HEAD2"));
                bfpbean.setSubMinorHeadDesc2(rs.getString("SUB_MINOR_HEAD2_DESC"));
                if (rs.getString("SUB_MINOR_HEAD3") != null && !rs.getString("SUB_MINOR_HEAD3").equals("")) {
                    if (rs.getString("SUB_MINOR_HEAD3").equals("2")) {
                        bfpbean.setSubMinorHead3("Charge");
                    } else {
                        bfpbean.setSubMinorHead3("Voted");
                    }
                } else {
                    bfpbean.setSubMinorHead3("");
                }

                bfpbean.setBillType(rs.getString("BILL_TYPE"));
                bfpbean.setDemandNo(rs.getString("DEMAND_NO"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return bfpbean;
    }

    @Override
    public void billBackPagePDF(Document document, String billNo, BillBackPageBean backPageBean, List empList) {

        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        BillBackPageBean bbpBean = new BillBackPageBean();
        Schedule sc = null;
        long deductAmt = 0;

        try {
            con = this.dataSource.getConnection();

            BillBackPageBean bBean = null;
            if (empList != null && empList.size() > 0) {
                bBean = new BillBackPageBean();
                for (int i = 0; i < empList.size(); i++) {
                    bBean = (BillBackPageBean) empList.get(i);
                    deductAmt = deductAmt + Long.parseLong(bBean.getSchAmount());
                }
            }
            long total = Long.parseLong(backPageBean.getTotalPaise());

            long netAmt = total - deductAmt;
            String netTotal = Double.valueOf(netAmt + "").longValue() + "";
            String netTotalinWord = Numtowordconvertion.convertNumber((int) Double.parseDouble(netTotal));

            long netTotaUnder = Double.valueOf(netAmt + "").longValue() + 1;
            String netTotalinWordUnder = Numtowordconvertion.convertNumber((int) netTotaUnder);

            Font textFont = new Font(Font.FontFamily.TIMES_ROMAN, 8.4f, Font.NORMAL, BaseColor.BLACK);
            Font boldTextFont = new Font(Font.FontFamily.TIMES_ROMAN, 8.6f, Font.BOLD, BaseColor.BLACK);

            //1st parent table
            PdfPTable table = null;

            table = new PdfPTable(2);
            table.setWidths(new int[]{5, 7});
            table.setWidthPercentage(100);

            PdfPCell cell = null;

            //1st inner table under table1 for column1
            PdfPTable innertable = new PdfPTable(3);
            innertable.setWidths(new float[]{1.5f, 1.5f, 1});
            innertable.setWidthPercentage(100);

            PdfPCell innercell = null;

            innercell = new PdfPCell(new Phrase("Total column 8", textFont));
            innercell.setBorder(Rectangle.LEFT | Rectangle.TOP);
            innertable.addCell(innercell);
            innercell = new PdfPCell(new Phrase("", textFont));
            innercell.setBorder(Rectangle.NO_BORDER);
            innertable.addCell(innercell);

            innercell = new PdfPCell(new Phrase(backPageBean.getTotalPaise(), textFont));
            innercell.setBorder(Rectangle.LEFT | Rectangle.TOP | Rectangle.RIGHT);
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innertable.addCell(innercell);

            innercell = new PdfPCell(new Phrase("Deduct:Undisturbed pay as detailed below -", textFont));
            innercell.setColspan(2);
            innercell.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
            innercell.setBorder(Rectangle.BOTTOM);
            innertable.addCell(innercell);

            innercell = new PdfPCell(new Phrase("", textFont));
            innercell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
            innertable.addCell(innercell);

            innercell = new PdfPCell(new Phrase("", textFont));
            innercell.setColspan(2);
            innercell.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
            //innercell.setFixedHeight(10);
            innertable.addCell(innercell);

            innercell = new PdfPCell(new Phrase("", textFont));
            innercell.setBorder(Rectangle.NO_BORDER);
            innertable.addCell(innercell);

            if (empList != null && empList.size() > 0) {
                Iterator itr = empList.iterator();
                while (itr.hasNext()) {
                    sc = (Schedule) itr.next();
                    innercell = new PdfPCell(new Phrase(sc.getScheduleName(), boldTextFont));
                    innercell.setBorder(Rectangle.LEFT);
                    innertable.addCell(innercell);
                    innercell = new PdfPCell(new Phrase("", boldTextFont));
                    innercell.setBorder(Rectangle.RIGHT);
                    innertable.addCell(innercell);
                    innercell = new PdfPCell(new Phrase(sc.getSchAmount(), boldTextFont));
                    innercell.setBorder(Rectangle.RIGHT);
                    innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    innertable.addCell(innercell);
                }
            }

            innercell = new PdfPCell(new Phrase("In respect of subscribers to the sterling Branch it"
                    + " should be noted on the bill and in the Fund Schedule"
                    + " attached to the bill that the recoveries related to the"
                    + " Sterling Brancehes.", textFont));
            innercell.setColspan(2);
            innercell.setBorder(Rectangle.TOP | Rectangle.LEFT | Rectangle.RIGHT);
            innertable.addCell(innercell);
            innercell = new PdfPCell(new Phrase("", textFont));
            innercell.setBorder(Rectangle.TOP | Rectangle.RIGHT);
            innertable.addCell(innercell);

            innercell = new PdfPCell(new Phrase("Total deduction:", textFont));
            innercell.setColspan(2);
            innercell.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innertable.addCell(innercell);
            innercell = new PdfPCell(new Phrase(deductAmt + "", textFont));
            innercell.setBorder(Rectangle.BOTTOM);
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innertable.addCell(innercell);

            innercell = new PdfPCell(new Phrase("The amount required for payment (in words)", textFont));
            innercell.setColspan(2);
            innercell.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
            innertable.addCell(innercell);
            innercell = new PdfPCell(new Phrase(netTotal + "", boldTextFont));
            innercell.setBorder(Rectangle.BOTTOM);
            innercell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            innertable.addCell(innercell);

            innercell = new PdfPCell(new Phrase(netTotalinWord, boldTextFont));
            innercell.setColspan(3);
            innercell.setFixedHeight(20);
            innercell.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innertable.addCell(innercell);

            innercell = new PdfPCell(new Phrase("DETAIL OF PAY OF ABSENTEES REFUNDED", textFont));
            innercell.setColspan(3);
            innercell.setBorder(Rectangle.LEFT | Rectangle.BOTTOM);
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innertable.addCell(innercell);

            //2nd inner table under 1st inner table under table1 for column1
            PdfPTable innertable2 = new PdfPTable(4);
            innertable2.setWidths(new float[]{1.5f, 1.5f, 1, 1});
            innertable2.setWidthPercentage(100);

            PdfPCell innercell2 = null;

            innercell2 = new PdfPCell(new Phrase("Traction of\nEstablishment", textFont));
            innercell2.setBorder(Rectangle.LEFT | Rectangle.BOTTOM);
            innertable2.addCell(innercell2);
            innercell2 = new PdfPCell(new Phrase("Name of\nincumbent", textFont));
            innercell2.setBorder(Rectangle.LEFT | Rectangle.BOTTOM);
            innertable2.addCell(innercell2);
            innercell2 = new PdfPCell(new Phrase("Period", textFont));
            innercell2.setBorder(Rectangle.LEFT | Rectangle.BOTTOM);
            innertable2.addCell(innercell2);
            innercell2 = new PdfPCell(new Phrase("Amount", textFont));
            innercell2.setBorder(Rectangle.LEFT | Rectangle.BOTTOM);
            innertable2.addCell(innercell2);
            //end of 2nd inner table under 1st inner table under table1 for column1

            innercell = new PdfPCell(innertable2);
            innercell.setColspan(3);
            innertable.addCell(innercell);

            innercell = new PdfPCell(new Phrase("", textFont));
            innercell.setBorder(Rectangle.NO_BORDER);
            innertable.addCell(innercell);
            innercell = new PdfPCell(new Phrase("", textFont));
            innercell.setBorder(Rectangle.NO_BORDER);
            innertable.addCell(innercell);
            innercell = new PdfPCell(new Phrase("", textFont));
            innercell.setBorder(Rectangle.NO_BORDER);
            innertable.addCell(innercell);
            //end of 1st inner table under table1 for column1

            innercell = new PdfPCell(new Phrase("2ND DISCHARGE FOR\nRBI, PAD, BHUBANESWAR", textFont));
            innercell.setColspan(3);
            innercell.setBorder(Rectangle.NO_BORDER);
            innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innertable.addCell(innercell);

            cell = new PdfPCell(innertable);
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            //1st inner table under table1 for column2
            PdfPTable col2table = new PdfPTable(1);
            col2table.setWidths(new int[]{2});
            col2table.setWidthPercentage(100);

            PdfPCell col2cell = new PdfPCell();

            col2cell = new PdfPCell(new Phrase("1.Received contents and certificated that I have satisfied myself 1 month"
                    + " 2month / 3month that all emoluments included in bills drawn previous to this"
                    + " date which the exception of those detailed below (of which the total has been"
                    + " refunded by deduction from this bill) have been taken and filled in my office"
                    + " with receipt stamps duly cancelled for every payment in excess of Rs. 20."
                    + " ++One line to be used and the other scored out.\n\n2.Certificated that no person"
                    + " in superior service has been absent either on other duty or suspension or "
                    + " with or without leave (except on casual leave) during the month.\n\n", textFont));
            col2cell.setBorder(Rectangle.NO_BORDER);
            col2table.addCell(col2cell);

            col2cell = new PdfPCell(new Phrase("RECEIVED CONTENTS", textFont));
            col2cell.setBorder(Rectangle.NO_BORDER);
            col2table.addCell(col2cell);

            col2cell = new PdfPCell(new Phrase("\nNote-When an absentee statement accompanies the bill the second certificate should be struck out.", textFont));
            col2cell.setBorder(Rectangle.NO_BORDER);
            col2table.addCell(col2cell);

            col2cell = new PdfPCell(new Phrase("RECEIVED PAYMENTS", textFont));
            col2cell.setBorder(Rectangle.NO_BORDER);
            col2cell.setHorizontalAlignment(Rectangle.ALIGN_RIGHT);
            col2table.addCell(col2cell);

            col2cell = new PdfPCell(new Phrase("3.Certificated that no leave has been granted until by reference to the"
                    + " applicant's service book leave accounts and to the leave rule, applicable to"
                    + " him. I have satisfied myself that it was admissible, and that all grants of"
                    + " leave and departures on, and returns from leave and all periods of"
                    + " suspension and other duty and other events which are required under the"
                    + " rules to be so recorded have been recorded in the service books and leave"
                    + " ccounts under my attestation.", textFont));
            col2cell.setBorder(Rectangle.NO_BORDER);
            col2table.addCell(col2cell);

            col2cell = new PdfPCell(new Phrase("SIGNATURE ATTESTED", textFont));
            col2cell.setBorder(Rectangle.NO_BORDER);
            col2cell.setHorizontalAlignment(Rectangle.ALIGN_RIGHT);
            col2table.addCell(col2cell);

            col2cell = new PdfPCell(new Phrase("4.Certified that all appointments and substantive promotion and such of the"
                    + " officiating promotions as have to be entered in the service books as per"
                    + " columns in the standard from No. FR-10 have been entered in the service"
                    + " book of the persons concerned under my attestation.\n\n5.Certified that all Government servants whose names are omitted from but"
                    + " for whom pay has been drawn in the bill has actually been entertained during"
                    + " the month (S.R.35 Bihar and Orissa Account Code).\n\n6.Certified that no persons for whom house rent allowance has been drawn"
                    + " in this bill has been in occupation of rent - free Government quarters during"
                    + " the period for which the allowances has been drawn.\n\n7.Certified that except in the case of Government servant whose names"
                    + " appear in the appended list and in whose case the appropriate certificate"
                    + " required under S.T.R.55 (3) has been furnished on leave salary for any"
                    + " Government servants drawn in the bill is equal to his actual pay.\n\n8.Certified that no leave salary for end Government servants (except the"
                    + " following in whose service book noted regarding allocation has been"
                    + " recorded) drawn in this bill form is debitable to any Government. Etc. other"
                    + " than the Government of Orissa.\n1     4\n2     5\n3     6\n\n9. Certified that in respect of fixed traveling allowance claims drawn in the"
                    + " previous month, quarter, half-year or full year as the case may be the"
                    + " necessary journals have been examined to see that the Government"
                    + " servants concerned made the requisite tours and that in case where the"
                    + " requisite tours have not been made the necessary recoveries have been"
                    + " effected. The particulars of recoveries made or yet to be made are furnished"
                    + " below:-", textFont));
            col2cell.setBorder(Rectangle.NO_BORDER);
            col2table.addCell(col2cell);

            //2nd inner table under 1st inner table under table1 for column2
            PdfPTable col2innertable = new PdfPTable(3);
            col2innertable.setWidths(new float[]{0.7f, 0.7f, 3.2f});
            col2innertable.setWidthPercentage(100);
            PdfPCell col2innercell = null;
            col2innercell = new PdfPCell(new Phrase("Under Rs.", textFont));
            col2innercell.setBorder(Rectangle.NO_BORDER);
            col2innertable.addCell(col2innercell);
            col2innercell = new PdfPCell(new Phrase(netTotaUnder + "", boldTextFont));
            col2innercell.setBorder(Rectangle.NO_BORDER);
            col2innertable.addCell(col2innercell);
            col2innercell = new PdfPCell(new Phrase(netTotalinWordUnder, boldTextFont));
            col2innercell.setBorder(Rectangle.NO_BORDER);
            col2innertable.addCell(col2innercell);
            //end of 2nd inner table under 1st inner table under table1 for column2
            col2cell = new PdfPCell(col2innertable);
            col2table.addCell(col2cell);
            //end of 1st inner table under table1 for column2

            cell = new PdfPCell(col2table);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("", textFont));
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("", textFont));
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            document.add(table);

            //2nd parent table
            table = new PdfPTable(2);
            table.setWidths(new int[]{5, 7});
            table.setWidthPercentage(100);

            cell = new PdfPCell(new Phrase("Pay ___________________ electronically to the account of the beneficiaries as per the "
                    + "list enclosed or to his Current Account, Suspense Head in case of unsuccessful payments.", textFont));
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            //2nd inner table under 2nd parent table
            PdfPTable table2 = new PdfPTable(10);
            table2.setWidths(new float[]{0.5f, 1.2f, 1.2f, 2, 1, 1, 1.2f, 1, 1, 1.2f});
            table2.setWidthPercentage(100);

            PdfPCell cell2 = new PdfPCell(new Phrase("", textFont));
            cell2.setBorder(Rectangle.RIGHT);
            table2.addCell(cell2);
            cell2 = new PdfPCell(new Phrase("Name\nof the\nofficer", textFont));
            cell2.setBorder(Rectangle.RIGHT);
            table2.addCell(cell2);
            cell2 = new PdfPCell(new Phrase("Desig\nnation", textFont));
            cell2.setBorder(Rectangle.RIGHT);
            table2.addCell(cell2);
            cell2 = new PdfPCell(new Phrase("Period for\nwhich\nminimum tour\nis prescribed\nmonth,quarter,\nhalfyear, year", textFont));
            cell2.setBorder(Rectangle.RIGHT);
            table2.addCell(cell2);
            cell2 = new PdfPCell(new Phrase("Minimum\ntour\nrequired", textFont));
            cell2.setBorder(Rectangle.RIGHT);
            table2.addCell(cell2);
            cell2 = new PdfPCell(new Phrase("Short\nage\nin tour", textFont));
            cell2.setBorder(Rectangle.RIGHT);
            table2.addCell(cell2);
            cell2 = new PdfPCell(new Phrase("Amou\nnt\nrecov\nered", textFont));
            cell2.setBorder(Rectangle.RIGHT);
            table2.addCell(cell2);
            cell2 = new PdfPCell(new Phrase("Amou\nnt yet\ndue", textFont));
            cell2.setBorder(Rectangle.RIGHT);
            table2.addCell(cell2);
            cell2 = new PdfPCell(new Phrase("Date\nof\nrecov\nery", textFont));
            cell2.setBorder(Rectangle.RIGHT);
            table2.addCell(cell2);
            cell2 = new PdfPCell(new Phrase("Remar\nks", textFont));
            cell2.setBorder(Rectangle.RIGHT);
            table2.addCell(cell2);

            cell2 = new PdfPCell(new Phrase("", textFont));
            cell2.setFixedHeight(50);
            table2.addCell(cell2);
            cell2 = new PdfPCell(new Phrase("", textFont));
            table2.addCell(cell2);
            cell2 = new PdfPCell(new Phrase("", textFont));
            table2.addCell(cell2);
            cell2 = new PdfPCell(new Phrase("", textFont));
            table2.addCell(cell2);
            cell2 = new PdfPCell(new Phrase("", textFont));
            table2.addCell(cell2);
            cell2 = new PdfPCell(new Phrase("", textFont));
            table2.addCell(cell2);
            cell2 = new PdfPCell(new Phrase("", textFont));
            table2.addCell(cell2);
            cell2 = new PdfPCell(new Phrase("", textFont));
            //cell2.setBorder(Rectangle.RIGHT);
            table2.addCell(cell2);
            cell2 = new PdfPCell(new Phrase("", textFont));
            table2.addCell(cell2);
            cell2 = new PdfPCell(new Phrase("", textFont));
            table2.addCell(cell2);
            //end of 2nd inner table under 2nd parent table

            cell = new PdfPCell(table2);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("", textFont));
            cell.setColspan(2);
            cell.setFixedHeight(10);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            /*cell = new PdfPCell(new Phrase("Pay ___________________ electronically to the account of the beneficiaries as per the list enclosed or to his Current Account, Suspense Head in case of unsuccessful payments.",f1));
             cell.setBorder(Rectangle.NO_BORDER);
             table.addCell(cell);
             cell = new PdfPCell(new Phrase("",f1));
             cell.setBorder(Rectangle.NO_BORDER);
             table.addCell(cell);*/
            cell = new PdfPCell(new Phrase("Station Dated_____________20", textFont));
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("", textFont));
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("", textFont));
            cell.setColspan(2);
            cell.setFixedHeight(10);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Signature", textFont));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("", textFont));
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Designation of Drawing Officer", textFont));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("", textFont));
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("", textFont));
            cell.setColspan(2);
            cell.setFixedHeight(10);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Pay Rs.__________________________ Rupees_______________________________________________________________ as follows", textFont));
            cell.setColspan(2);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("In cash Rs__________________________", textFont));
            cell.setColspan(2);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Deduct - By transfer credit to personal deposits Rs __________________________", textFont));
            cell.setColspan(2);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("P.L.I Premia Rs __________________________", textFont));
            cell.setColspan(2);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("IV - Taxes on income Rs.__________________________", textFont));
            cell.setColspan(2);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("XXXIX - Civil Works Rs.__________________________", textFont));
            cell.setColspan(2);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            /*cell = new PdfPCell(new Phrase("",f1));
             cell.setColspan(2);
             cell.setFixedHeight(10);
             cell.setBorder(Rectangle.NO_BORDER);
             cell.setHorizontalAlignment(Element.ALIGN_CENTER);
             table.addCell(cell);*/
            cell = new PdfPCell(new Phrase("Examined and entered" + StringUtils.repeat(" ", 39) + "Dated...........................20" + StringUtils.repeat(" ", 45) + "Treasury Officer", textFont));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setColspan(2);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Treasury Accountant", textFont));
            cell.setColspan(2);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);

            document.add(table);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    @Override
    public int getBasicAmount(String billNo) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        int amt = 0;

        try {
            con = this.dataSource.getConnection();

            String sql = "SELECT SUM(CUR_BASIC) CUR_BASIC FROM AQ_MAST WHERE AQ_MAST.BILL_NO=?";
            pst = con.prepareStatement(sql);
            pst.setInt(1, Integer.parseInt(billNo));
            rs = pst.executeQuery();
            if (rs.next()) {
                amt = rs.getInt("CUR_BASIC");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return amt;
    }

    @Override
    public int getAllowanceAndDeductionAmount(String billNo, String adType, int month, int year) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        int amt = 0;

        try {
            con = this.dataSource.getConnection();
            if (adType.equals("A")) {
                String sql = "SELECT SUM(AD_AMT) AD_AMT FROM (SELECT AQSL_NO FROM AQ_MAST WHERE BILL_NO=?) AQ_MAST"
                        + " INNER JOIN (SELECT AD_AMT,AQSL_NO FROM AQ_DTLS WHERE AD_TYPE=? AND AQ_MONTH=? AND AQ_YEAR=?) AQ_DTLS ON AQ_MAST.AQSL_NO=AQ_DTLS.AQSL_NO";
                pst = con.prepareStatement(sql);
                pst.setInt(1, Integer.parseInt(billNo));
                pst.setString(2, adType);
                pst.setInt(3, month);
                pst.setInt(4, year);
            } else if (adType.equals("D")) {
                String sql = "SELECT SUM(AD_AMT) AD_AMT FROM (SELECT AQSL_NO FROM AQ_MAST WHERE BILL_NO=?) AQ_MAST"
                        + " INNER JOIN (SELECT AD_AMT,AQSL_NO FROM AQ_DTLS WHERE AD_TYPE=? AND SCHEDULE!='PVTL' AND SCHEDULE !='PVTD' AND AQ_MONTH=? AND AQ_YEAR=?) AQ_DTLS ON AQ_MAST.AQSL_NO=AQ_DTLS.AQSL_NO";
                pst = con.prepareStatement(sql);
                pst.setInt(1, Integer.parseInt(billNo));
                pst.setString(2, adType);
                pst.setInt(3, month);
                pst.setInt(4, year);
            }
            rs = pst.executeQuery();
            if (rs.next()) {
                amt = rs.getInt("AD_AMT");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return amt;
    }

    @Override
    public void WRRSchedulePagePDF(Document document, String schedule, String billNo, WrrScheduleBean wrrBean, List empList) {

        double totalAmt = 0.0;
        double amt = 0.0;
        String totalFig = null;
        int slno = 0;
        int pageNo = 1;

        try {

            WrrScheduleBean obj = null;
            if (empList != null && empList.size() > 0) {
                obj = new WrrScheduleBean();
                for (int i = 0; i < empList.size(); i++) {
                    obj = (WrrScheduleBean) empList.get(i);
                    amt = Double.parseDouble(obj.getAmount());
                    totalAmt = totalAmt + amt;
                    totalFig = Numtowordconvertion.convertNumber((int) totalAmt);
                }
            }

            Font textFont = new Font(Font.FontFamily.TIMES_ROMAN, 8.4f, Font.NORMAL, BaseColor.BLACK);
            Font bigTextFont = new Font(Font.FontFamily.TIMES_ROMAN, 8.6f, Font.NORMAL, BaseColor.BLACK);
            Font boldTextFont = new Font(Font.FontFamily.TIMES_ROMAN, 8.6f, Font.BOLD, BaseColor.BLACK);
            Font hdrTextFont = new Font(Font.FontFamily.TIMES_ROMAN, 10f, Font.BOLD, BaseColor.BLACK);

            PdfPTable footerTable = new PdfPTable(5);
            footerTable.setWidths(new int[]{5, 10, 20, 10, 15});
            footerTable.setWidthPercentage(100);

            PdfPCell footerCell = null;

            PdfPTable signTable = new PdfPTable(5);
            signTable.setWidths(new int[]{5, 10, 20, 10, 15});
            signTable.setWidthPercentage(100);

            PdfPCell signCell = null;

            // CREATING A BLANK TABLE WITH A BLANK CELL    
            PdfPTable blankTable = null;
            blankTable = new PdfPTable(1);
            blankTable.setWidthPercentage(100);

            PdfPCell blankCell = null;
            blankCell = new PdfPCell(new Phrase(" ", textFont));
            blankCell.setBorder(Rectangle.NO_BORDER);
            blankTable.addCell(blankCell);

            //1st parent table
            PdfPTable hdrTable = null;
            hdrTable = new PdfPTable(1);
            hdrTable.setWidthPercentage(100);

            PdfPCell hdrCell = null;
            hdrCell = new PdfPCell(new Phrase("Page No :" + pageNo, hdrTextFont));
            hdrCell.setBorder(Rectangle.NO_BORDER);
            hdrCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            hdrTable.addCell(hdrCell);

            hdrCell = new PdfPCell(new Phrase(wrrBean.getReportName(), hdrTextFont));
            hdrCell.setBorder(Rectangle.NO_BORDER);
            hdrCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            hdrTable.addCell(hdrCell);

            hdrCell = new PdfPCell(new Phrase(" 0215-WATER SUPPLY AND SANITATION-01-WATER SUPPLY-103-RECEIPTS FROM ", hdrTextFont));
            hdrCell.setBorder(Rectangle.NO_BORDER);
            hdrCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            hdrTable.addCell(hdrCell);

            hdrCell = new PdfPCell(new Phrase(" URBAN WATER SUPPLY SCHEMES-0175-Water Rate / Cess-02171-Water Supply for ", hdrTextFont));
            hdrCell.setBorder(Rectangle.NO_BORDER);
            hdrCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            hdrTable.addCell(hdrCell);

            hdrCell = new PdfPCell(new Phrase(" supply of drinking water " + wrrBean.getPoolName(), hdrTextFont));
            hdrCell.setBorder(Rectangle.NO_BORDER);
            hdrCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            hdrTable.addCell(hdrCell);

            hdrCell = new PdfPCell(new Phrase(" FOR THE MONTH OF " + wrrBean.getTxtMonth() + " - " + wrrBean.getTxtYear(), hdrTextFont));
            hdrCell.setBorder(Rectangle.NO_BORDER);
            hdrCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            hdrTable.addCell(hdrCell);

            hdrCell = new PdfPCell(new Phrase(wrrBean.getDemandNo(), bigTextFont));
            hdrCell.setBorder(Rectangle.NO_BORDER);
            hdrCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            hdrTable.addCell(hdrCell);

            hdrCell = new PdfPCell(new Phrase(" ********** = - = ********** ", bigTextFont));
            hdrCell.setBorder(Rectangle.NO_BORDER);
            hdrCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            hdrTable.addCell(hdrCell);

            document.add(hdrTable);// 1st Top Header Created 
            document.add(blankTable); // To add 1 empty line

            PdfPTable dataTable = null;
            dataTable = new PdfPTable(5);
            dataTable.setWidths(new int[]{5, 10, 20, 10, 15});
            dataTable.setWidthPercentage(100);

            PdfPCell dataCell = null;
            dataCell = new PdfPCell(new Phrase("Sl. No. ", boldTextFont));
            dataCell.setBorder(Rectangle.BOX);
            dataCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            dataTable.addCell(dataCell);

            dataCell = new PdfPCell(new Phrase(" GPF No.", boldTextFont));
            dataCell.setBorder(Rectangle.BOX);
            dataCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            dataTable.addCell(dataCell);

            dataCell = new PdfPCell(new Phrase(" Name of the Employee / Designation ", boldTextFont));
            dataCell.setBorder(Rectangle.BOX);
            dataCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            dataTable.addCell(dataCell);

            dataCell = new PdfPCell(new Phrase(" Amount Recovered ", boldTextFont));
            dataCell.setBorder(Rectangle.BOX);
            dataCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            dataTable.addCell(dataCell);

            dataCell = new PdfPCell(new Phrase(" Quarter No. & Address ", boldTextFont));
            dataCell.setBorder(Rectangle.BOX);
            dataCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            dataTable.addCell(dataCell);

            if (empList != null && empList.size() > 0) {
                Iterator itr = empList.iterator();
                WrrScheduleBean obj1 = null;
                while (itr.hasNext()) {
                    obj1 = (WrrScheduleBean) itr.next();
                    slno++;
                    if (pageNo == 1) {
                        pageNo++;
                        printBillDescription(wrrBean, document);
                        document.add(blankTable); // To add 1 empty line
                    }

                    dataCell = new PdfPCell(new Phrase(slno + "", textFont));
                    dataCell.setBorder(Rectangle.BOTTOM);
                    dataCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    dataTable.addCell(dataCell);

                    dataCell = new PdfPCell(new Phrase(obj1.getGpfNo(), textFont));
                    dataCell.setBorder(Rectangle.BOTTOM);
                    dataCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    dataTable.addCell(dataCell);

                    dataCell = new PdfPCell(new Phrase(obj1.getEmpname() + " \n " + obj1.getEmpdesg(), textFont));
                    dataCell.setBorder(Rectangle.BOTTOM);
                    dataCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    dataTable.addCell(dataCell);

                    dataCell = new PdfPCell(new Phrase(obj1.getAmount(), textFont));
                    dataCell.setBorder(Rectangle.BOTTOM);
                    dataCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    dataTable.addCell(dataCell);

                    dataCell = new PdfPCell(new Phrase(obj1.getQuarterNo() + " \n " + obj1.getAddress(), textFont));
                    dataCell.setBorder(Rectangle.BOTTOM);
                    dataCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    dataTable.addCell(dataCell);

                    if (slno % 25 == 0) {
                        printPageTotal(dataTable, dataCell, boldTextFont, obj1.getCarryForward());
                        document.add(dataTable);
                        document.newPage();

                        dataTable = new PdfPTable(5);
                        dataTable.setWidths(new int[]{5, 10, 20, 10, 15});
                        dataTable.setWidthPercentage(100);

                        printPageNo(pageNo, document);
                        printBillDescription(wrrBean, document);
                        printCarryForward(obj1.getCarryForward(), dataTable, dataCell, pageNo - 1, boldTextFont);
                        document.add(blankTable); // To add 1 empty line
                        pageNo++;
                    }
                    if (slno == empList.size()) {
                        printPageFooter(wrrBean, footerTable, footerCell, boldTextFont, totalAmt);
                        printPageSign(wrrBean, signTable, signCell, boldTextFont);
                    }
                }

            } else {

                printBillDescription(wrrBean, document);
                document.add(blankTable); // To add 1 empty line

                dataCell = new PdfPCell(new Phrase(" There is no record ", bigTextFont));
                dataCell.setBorder(Rectangle.BOTTOM);
                dataCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                dataCell.setColspan(5);
                dataTable.addCell(dataCell);
            }

            document.add(dataTable); // 3rd Table with data Created
            document.add(footerTable);
            document.add(blankTable); // To add 1 empty line
            document.add(signTable);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printPageSign(WrrScheduleBean wrrBean, PdfPTable signTable, PdfPCell signCell, Font f1) throws Exception {

        signCell = new PdfPCell(new Phrase("", f1));
        signCell.setBorder(Rectangle.NO_BORDER);
        signCell.setColspan(5);
        signTable.addCell(signCell);

        signCell = new PdfPCell(new Phrase("", f1));
        signCell.setBorder(Rectangle.NO_BORDER);
        signCell.setColspan(3);
        signTable.addCell(signCell);

        signCell = new PdfPCell(new Phrase(" Signature of D.D.O " + "\n" + wrrBean.getOfficeName(), f1));
        signCell.setBorder(Rectangle.NO_BORDER);
        signCell.setColspan(2);
        signCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        signTable.addCell(signCell);

        signCell = new PdfPCell(new Phrase("", f1));
        signCell.setBorder(Rectangle.NO_BORDER);
        signCell.setColspan(3);
        signTable.addCell(signCell);

        signCell = new PdfPCell(new Phrase("Date", f1));
        signCell.setBorder(Rectangle.NO_BORDER);
        signCell.setColspan(2);
        signCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        signTable.addCell(signCell);

    }

    private void printPageFooter(WrrScheduleBean wrrBean, PdfPTable dataTable, PdfPCell dataCell, Font f1, double totAmt) throws Exception {

        dataCell = new PdfPCell(new Phrase("", f1));
        dataCell.setColspan(5);
        dataCell.setBorder(Rectangle.BOTTOM);
        dataTable.addCell(dataCell);

        dataCell = new PdfPCell(new Phrase("TOTAL FOR THE MONTH OF " + wrrBean.getTxtMonth() + " - " + wrrBean.getTxtYear(), f1));
        dataCell.setBorder(Rectangle.BOTTOM);
        dataCell.setColspan(3);
        dataCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        dataTable.addCell(dataCell);

        dataCell = new PdfPCell(new Phrase("     " + totAmt + "\n" + "Rupees ( " + Numtowordconvertion.convertNumber((int) totAmt) + ") Only", f1));
        dataCell.setBorder(Rectangle.BOTTOM);
        dataCell.setColspan(2);
        dataCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        dataTable.addCell(dataCell);

        dataCell = new PdfPCell(new Phrase("", f1));
        dataCell.setBorder(Rectangle.NO_BORDER);
        dataCell.setColspan(3);
        dataTable.addCell(dataCell);

    }

    private void printBillDescription(WrrScheduleBean wrrBean, Document document) throws Exception {

        Font bigTextFont = new Font(Font.FontFamily.TIMES_ROMAN, 8.6f, Font.NORMAL, BaseColor.BLACK);
        Font boldTextFont = new Font(Font.FontFamily.TIMES_ROMAN, 8.6f, Font.BOLD, BaseColor.BLACK);

        PdfPTable hdr2Table = null;
        hdr2Table = new PdfPTable(3);
        hdr2Table.setWidths(new int[]{8, 20, 10});
        hdr2Table.setWidthPercentage(100);

        PdfPCell hdr2Cell = null;
        hdr2Cell = new PdfPCell(new Phrase("Name of the Department: ", bigTextFont));
        hdr2Cell.setBorder(Rectangle.NO_BORDER);
        hdr2Cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        hdr2Table.addCell(hdr2Cell);

        hdr2Cell = new PdfPCell(new Phrase(wrrBean.getDeptName(), boldTextFont));
        hdr2Cell.setBorder(Rectangle.NO_BORDER);
        hdr2Cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        hdr2Table.addCell(hdr2Cell);

        hdr2Cell = new PdfPCell(new Phrase(" ", bigTextFont));
        hdr2Cell.setBorder(Rectangle.NO_BORDER);
        hdr2Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        hdr2Table.addCell(hdr2Cell);

        hdr2Cell = new PdfPCell(new Phrase("Name of the Office: ", bigTextFont));
        hdr2Cell.setBorder(Rectangle.NO_BORDER);
        hdr2Cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        hdr2Table.addCell(hdr2Cell);

        hdr2Cell = new PdfPCell(new Phrase(wrrBean.getOfficeName(), boldTextFont));
        hdr2Cell.setBorder(Rectangle.NO_BORDER);
        hdr2Cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        hdr2Table.addCell(hdr2Cell);

        hdr2Cell = new PdfPCell(new Phrase(" ", bigTextFont));
        hdr2Cell.setBorder(Rectangle.NO_BORDER);
        hdr2Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        hdr2Table.addCell(hdr2Cell);

        hdr2Cell = new PdfPCell(new Phrase("Designation of DDO: ", bigTextFont));
        hdr2Cell.setBorder(Rectangle.NO_BORDER);
        hdr2Cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        hdr2Table.addCell(hdr2Cell);

        hdr2Cell = new PdfPCell(new Phrase(wrrBean.getDdoDegn(), boldTextFont));
        hdr2Cell.setBorder(Rectangle.NO_BORDER);
        hdr2Cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        hdr2Table.addCell(hdr2Cell);

        hdr2Cell = new PdfPCell(new Phrase(" ", bigTextFont));
        hdr2Cell.setBorder(Rectangle.NO_BORDER);
        hdr2Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        hdr2Table.addCell(hdr2Cell);

        hdr2Cell = new PdfPCell(new Phrase("Bill No: ", bigTextFont));
        hdr2Cell.setBorder(Rectangle.NO_BORDER);
        hdr2Cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        hdr2Table.addCell(hdr2Cell);

        hdr2Cell = new PdfPCell(new Phrase(wrrBean.getBillDesc(), boldTextFont));
        hdr2Cell.setBorder(Rectangle.NO_BORDER);
        hdr2Cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        hdr2Table.addCell(hdr2Cell);

        hdr2Cell = new PdfPCell(new Phrase(" ", bigTextFont));
        hdr2Cell.setBorder(Rectangle.NO_BORDER);
        hdr2Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        hdr2Table.addCell(hdr2Cell);

        document.add(hdr2Table);
    }

    private void printPageTotal(PdfPTable table, PdfPCell cell, Font f1, String pageTotal) throws Exception {

        cell = new PdfPCell(new Phrase(" * Page Total * :", f1));
        cell.setColspan(3);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(pageTotal + "", f1));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("", f1));
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(StringUtils.repeat("-", 181), f1));
        cell.setColspan(5);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        //cell = new PdfPCell(new Phrase("In Words (Rupees "+StringUtils.upperCase(Numtowordconvertion.convertNumber((int)pageTotal)+" ) Only"),f1));
        cell = new PdfPCell(new Phrase("", f1));
        cell.setColspan(5);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cell);

    }

    private void printCarryForward(String pagetotal, PdfPTable table, PdfPCell cell, int pageno, Font f1) throws Exception {

        cell = new PdfPCell(new Phrase(StringUtils.repeat("-", 181), f1));
        cell.setColspan(5);
        cell.setBorder(Rectangle.NO_BORDER);

        cell = new PdfPCell(new Phrase("CARRIED FROM PAGE : " + pageno, f1));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(3);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(pagetotal + "", f1));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("", f1));
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(StringUtils.repeat("-", 181), f1));
        cell.setColspan(5);
        cell.setBorder(Rectangle.NO_BORDER);

        table.addCell(cell);
    }

    private void printPageNo(int pageNo, Document document) throws Exception {

        Font boldTextFont = new Font(Font.FontFamily.TIMES_ROMAN, 8.6f, Font.BOLD, BaseColor.BLACK);

        PdfPTable pageNoTable = null;
        pageNoTable = new PdfPTable(1);
        pageNoTable.setWidthPercentage(100);

        PdfPCell pageNoCell = null;
        pageNoCell = new PdfPCell(new Phrase("Page No :" + pageNo, boldTextFont));
        pageNoCell.setBorder(Rectangle.NO_BORDER);
        pageNoCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        pageNoTable.addCell(pageNoCell);

        document.add(pageNoTable);

    }

    @Override
    public void ITSchedulePagePDF(Document document, String schedule, String billNo, ItScheduleBean itBean, List empList) {

        double dedutAmt = 0.0;
        double totDedutAmt = 0.0;
        String totalFig = null;
        int slno = 0;
        int pageNo = 0;
        int total = 0;
        String empValue = "NA";
        try {

            ItScheduleBean obj = null;
            if (empList != null && empList.size() > 0) {
                obj = new ItScheduleBean();
                for (int i = 0; i < empList.size(); i++) {
                    obj = (ItScheduleBean) empList.get(i);
                    dedutAmt = Double.parseDouble(obj.getEmpDedutAmount());
                    totDedutAmt = totDedutAmt + dedutAmt;
                    totalFig = Numtowordconvertion.convertNumber((int) totDedutAmt);
                }
            }

            Font textFont = new Font(Font.FontFamily.TIMES_ROMAN, 8.4f, Font.NORMAL, BaseColor.BLACK);
            Font bigTextFont = new Font(Font.FontFamily.TIMES_ROMAN, 8.6f, Font.NORMAL, BaseColor.BLACK);
            Font boldTextFont = new Font(Font.FontFamily.TIMES_ROMAN, 8.6f, Font.BOLD, BaseColor.BLACK);
            Font hdrTextFont = new Font(Font.FontFamily.TIMES_ROMAN, 10f, Font.BOLD, BaseColor.BLACK);

            PdfPTable footerTable = new PdfPTable(5);
            footerTable.setWidths(new int[]{5, 10, 20, 10, 15});
            footerTable.setWidthPercentage(100);
            PdfPCell footerCell = null;

            // CREATING A BLANK TABLE WITH A BLANK CELL    
            PdfPTable blankTable = null;
            blankTable = new PdfPTable(1);
            blankTable.setWidthPercentage(100);

            PdfPCell blankCell = null;
            blankCell = new PdfPCell(new Phrase(" ", textFont));
            blankCell.setBorder(Rectangle.NO_BORDER);
            blankTable.addCell(blankCell);

            PdfPTable table = null;
            table = new PdfPTable(2);
            table.setWidths(new int[]{2, 5});
            table.setWidthPercentage(100);

            PdfPCell cell = null;
            cell = new PdfPCell(new Phrase(StringUtils.defaultString(itBean.getOfficeName()), hdrTextFont));
            cell.setColspan(2);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(StringUtils.defaultString(itBean.getScheduleName()), hdrTextFont));
            cell.setColspan(2);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("MEMORANDUM INDICATING THE AMOUNTS CREDITABLE", hdrTextFont));
            cell.setColspan(2);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("TO CENTRAL AS SHOWN BELOW", hdrTextFont));
            cell.setColspan(2);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("FOR THE MONTH OF : " + itBean.getAqmonth() + "-" + itBean.getAqyear(), hdrTextFont));
            cell.setColspan(2);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("", hdrTextFont));
            cell.setColspan(2);
            cell.setFixedHeight(30);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Bill No:   " + StringUtils.defaultString(itBean.getBillNo()), hdrTextFont));
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(StringUtils.repeat(" ", 15) + "TAN No:  " + StringUtils.defaultString(itBean.getTanno()), hdrTextFont));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            document.add(table);
            if (empList != null && empList.size() > 0) {

                table = new PdfPTable(5);
                table.setWidths(new float[]{0.5f, 4, 1, 1, 1});
                table.setWidthPercentage(100);

                Iterator itr = empList.iterator();
                ItScheduleBean itsbean = null;
                while (itr.hasNext()) {
                    itsbean = (ItScheduleBean) itr.next();

                    empValue = itsbean.getEmpname();

                    if (empValue != null && !empValue.equals("")) {
                        slno++;
                        if (pageNo == 0) {
                            pageNo++;
                            printHeader(table, cell, pageNo, hdrTextFont, schedule, itBean.getBtId());
                        }
                        total = total + Integer.parseInt(itsbean.getEmpDedutAmount());

                        //1st row inside while
                        cell = new PdfPCell(new Phrase(slno + "", textFont));
                        cell.setBorder(Rectangle.NO_BORDER);
                        table.addCell(cell);

                        cell = new PdfPCell(new Phrase(itsbean.getEmpname(), textFont));
                        cell.setBorder(Rectangle.NO_BORDER);
                        table.addCell(cell);

                        if (schedule.equalsIgnoreCase("IT")) {
                            cell = new PdfPCell(new Phrase(itsbean.getEmpPanNo(), textFont));
                            cell.setBorder(Rectangle.NO_BORDER);
                            table.addCell(cell);
                        } else {
                            cell = new PdfPCell();
                            cell.setBorder(Rectangle.NO_BORDER);
                            table.addCell(cell);
                        }
                        cell = new PdfPCell(new Phrase(itsbean.getEmpBasicSal() + StringUtils.repeat(" ", 10), textFont));
                        cell.setBorder(Rectangle.NO_BORDER);
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        table.addCell(cell);

                        cell = new PdfPCell(new Phrase(itsbean.getEmpDedutAmount() + StringUtils.repeat(" ", 10), textFont));
                        cell.setBorder(Rectangle.NO_BORDER);
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        table.addCell(cell);

                        //2nd row inside while
                        cell = new PdfPCell(new Phrase("", textFont));
                        cell.setBorder(Rectangle.BOTTOM);
                        table.addCell(cell);

                        if (schedule.equalsIgnoreCase("IT")) {
                            cell = new PdfPCell(new Phrase(itsbean.getEmpdesg(), textFont));
                            cell.setColspan(4);
                            cell.setBorder(Rectangle.BOTTOM);
                            table.addCell(cell);
                        } else {
                            cell = new PdfPCell(new Phrase(itsbean.getEmpdesg(), textFont));
                            cell.setColspan(3);
                            cell.setBorder(Rectangle.BOTTOM);
                            table.addCell(cell);

                            cell = new PdfPCell();
                            cell.setBorder(Rectangle.BOTTOM);
                            table.addCell(cell);
                        }

                        if (slno % 17 == 0) {

                            printTotal(table, cell, total, boldTextFont, schedule, itBean.getDdoDegn(), pageNo);
                            document.add(table);
                            document.newPage();

                            table = new PdfPTable(5);
                            table.setWidths(new float[]{0.5f, 4f, 1, 1, 1});
                            table.setWidthPercentage(100);

                            pageNo++;
                            printHeader(table, cell, pageNo, hdrTextFont, schedule, itBean.getBtId());

                        }
                        if (slno == empList.size()) {
                            printTotal(table, cell, total, boldTextFont, schedule, itBean.getDdoDegn(), pageNo);
                            document.add(table);
                        }

                    } else {

                        printHeader(table, cell, pageNo, hdrTextFont, schedule, itBean.getBtId());
                        cell = new PdfPCell(new Phrase(" There is no record ", bigTextFont));
                        cell.setBorder(Rectangle.BOTTOM);
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setColspan(5);
                        table.addCell(cell);
                        document.add(table);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printHeader(PdfPTable table, PdfPCell cell, int pageNo, Font f1, String schedule, String btId) throws Exception {

        if (pageNo == 1) {
            if (schedule.equalsIgnoreCase("IT")) {
                cell = new PdfPCell(new Phrase(StringUtils.defaultString(btId) + " - INCOME TAX DEDUCTION", f1));
                cell.setColspan(5);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

            } else if (schedule.equalsIgnoreCase("CGEGIS")) {
                cell = new PdfPCell(new Phrase(StringUtils.defaultString(btId) + " - CGEGIS TAX DEDUCTION", f1));
                cell.setColspan(5);
                cell.setBorder(Rectangle.NO_BORDER);
                //cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

            } else if (schedule.equalsIgnoreCase("GIS")) {
                cell = new PdfPCell(new Phrase(StringUtils.defaultString(btId) + " - GIS TAX DEDUCTION", f1));
                cell.setColspan(5);
                cell.setBorder(Rectangle.NO_BORDER);
                //cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }
        }

        if (schedule.equalsIgnoreCase("IT")) {
            cell = new PdfPCell();
            cell.setColspan(5);
            cell.setFixedHeight(20);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Sl No", f1));
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Name and Designation of Employee", f1));
            cell.setBorder(Rectangle.BOX);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("PAN NO", f1));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.BOX);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Gross Salary", f1));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Deduction", f1));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

        } else {
            cell = new PdfPCell();
            cell.setColspan(5);
            cell.setFixedHeight(20);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Sl No", f1));
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Name and Designation of Employee", f1));
            cell.setBorder(Rectangle.BOX);
            cell.setColspan(2);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Gross Salary", f1));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Deduction", f1));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

        }

        cell = new PdfPCell();
        cell.setColspan(5);
        cell.setFixedHeight(10);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);
    }

    private void printTotal(PdfPTable table, PdfPCell cell, int total, Font f1, String schedule, String ddoname, int pageno) {

        cell = new PdfPCell(new Phrase(StringUtils.repeat("-", 181), f1));
        cell.setColspan(5);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("* Grand Total * :", f1));
        cell.setColspan(4);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);
        cell = new PdfPCell(new Phrase(total + "", f1));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(StringUtils.repeat("-", 181), f1));
        cell.setColspan(5);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        if (total > 0) {
            cell = new PdfPCell(new Phrase("In Words(RUPEES " + StringUtils.upperCase(Numtowordconvertion.convertNumber(total) + ") ONLY"), f1));
            cell.setColspan(5);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell();
            cell.setColspan(5);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        cell = new PdfPCell();
        cell.setColspan(5);
        cell.setFixedHeight(20);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell();
        cell.setColspan(2);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);
        cell = new PdfPCell(new Phrase("Designation of the Drawing Officer:", f1));
        cell.setColspan(3);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell();
        cell.setColspan(2);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);
        cell = new PdfPCell(new Phrase(StringUtils.defaultString(ddoname), f1));
        cell.setColspan(3);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell();
        cell.setColspan(5);
        cell.setFixedHeight(10);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Page: " + StringUtils.defaultString(pageno + ""), f1));
        cell.setColspan(5);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cell);

    }

    @Override
    public void PTSchedulePagePDF(Document document, String billNo, PtScheduleBean ptBean, List empList, int aqMonth, int aqYear) {

        int slNo = 0;
        int pageNo = 0;
        int pageTotal = 0;

        try {

            Font textFont = new Font(Font.FontFamily.TIMES_ROMAN, 8.4f, Font.NORMAL, BaseColor.BLACK);
            Font bigTextFont = new Font(Font.FontFamily.TIMES_ROMAN, 8.6f, Font.NORMAL, BaseColor.BLACK);
            Font boldTextFont = new Font(Font.FontFamily.TIMES_ROMAN, 8.6f, Font.BOLD, BaseColor.BLACK);
            Font hdrTextFont = new Font(Font.FontFamily.TIMES_ROMAN, 10f, Font.BOLD, BaseColor.BLACK);

            // CREATING A BLANK TABLE WITH A BLANK CELL    
            PdfPTable blankTable = null;
            blankTable = new PdfPTable(1);
            blankTable.setWidthPercentage(100);

            PdfPCell blankCell = null;
            blankCell = new PdfPCell(new Phrase(" ", textFont));
            blankCell.setBorder(Rectangle.NO_BORDER);
            blankTable.addCell(blankCell);

            PdfPTable table = null;
            table = new PdfPTable(6);
            table.setWidths(new int[]{4, 15, 15, 9, 9, 8});
            table.setWidthPercentage(100);

            PdfPCell cell = null;

            if (empList != null && empList.size() > 0) {
                Iterator itr = empList.iterator();
                PtScheduleBean ptbean = null;
                while (itr.hasNext()) {
                    ptbean = (PtScheduleBean) itr.next();
                    slNo++;
                    if (pageNo == 0) {
                        table = new PdfPTable(6);
                        table.setWidths(new int[]{4, 18, 15, 8, 8, 7});
                        table.setWidthPercentage(100);
                        pageNo++;
                        printPTHeader(ptBean, table, cell, pageNo, hdrTextFont);
                        printPtBillDescription(ptBean, table, cell);
                    }
                    pageTotal = pageTotal + Integer.parseInt(ptbean.getEmpTaxOnProffesion());
                    String totGross = ptbean.getTotalGross();

                    //1st row inside while
                    cell = new PdfPCell(new Paragraph(slNo + "", textFont));
                    cell.setBorder(Rectangle.NO_BORDER);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase(ptbean.getEmpname(), textFont));
                    cell.setBorder(Rectangle.NO_BORDER);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase(ptbean.getEmpdesg(), textFont));
                    cell.setBorder(Rectangle.NO_BORDER);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase(ptbean.getEmpGrossSal(), textFont));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase(ptbean.getEmpTaxOnProffesion(), textFont));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase("", textFont));
                    cell.setBorder(Rectangle.NO_BORDER);
                    table.addCell(cell);

                    if (slNo % 30 == 0) {
                        printPageTotalPT(table, cell, boldTextFont, String.valueOf(pageTotal), totGross);
                        document.add(table);
                        document.newPage();

                        table = new PdfPTable(6);
                        table.setWidths(new int[]{4, 18, 15, 8, 8, 7});
                        table.setWidthPercentage(100);

                        pageNo++;
                        printPageNo(pageNo, document);
                        printPtBillDescription(ptBean, table, cell);
                    }
                    if (slNo == empList.size()) {
                        printPageFooterPT(ptBean, table, cell, pageTotal, totGross);
                        document.add(table);
                    }
                }
            }
            if (empList.size() == 0 || empList == null) {
                printPTHeader(ptBean, table, cell, pageNo, hdrTextFont);
                document.add(table);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printPageTotalPT(PdfPTable table, PdfPCell cell, Font f1, String pageTotal, String pageGross) throws Exception {

        cell = new PdfPCell(new Phrase(StringUtils.repeat("-", 180), f1));
        cell.setColspan(6);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(" * Page Total * :", f1));
        cell.setColspan(3);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(pageGross, f1));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(pageTotal + "", f1));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("", f1));
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(StringUtils.repeat("-", 180), f1));
        cell.setColspan(6);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

    }

    private void printPTHeader(PtScheduleBean ptbean, PdfPTable table, PdfPCell cell, int pageno, Font f1) throws Exception {

        cell = new PdfPCell(new Paragraph("Page : " + pageno, f1));
        cell.setColspan(6);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph("", f1));
        cell.setColspan(6);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph(" SCHEDULE OF RECOVERY OF TAX ON PROFESSION ", f1));
        cell.setColspan(6);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph(" FOR THE MONTH OF " + ptbean.getMonthYear(), f1));
        cell.setColspan(6);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph(" DEMAND NO.- 0028-OTHER TAXES ON INCOME AND EXPENDITURE - 107 - TAXES ON ", f1));
        cell.setColspan(6);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph(" PROFESSION TRADES,CALLING AND EMPLOYMENT-9913780-TAXES ON PROFESSION ", f1));
        cell.setColspan(6);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph(" **********-*-********** ", f1));
        cell.setColspan(6);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

    }

    private void printPtBillDescription(PtScheduleBean ptBean, PdfPTable hdr2Table, PdfPCell hdr2Cell) throws Exception {

        Font bigTextFont = new Font(Font.FontFamily.TIMES_ROMAN, 8.6f, Font.NORMAL, BaseColor.BLACK);
        Font boldTextFont = new Font(Font.FontFamily.TIMES_ROMAN, 8.6f, Font.BOLD, BaseColor.BLACK);

        hdr2Cell = new PdfPCell(new Phrase("Name of the Department: ", bigTextFont));
        hdr2Cell.setColspan(2);
        hdr2Cell.setBorder(Rectangle.NO_BORDER);
        hdr2Cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        hdr2Table.addCell(hdr2Cell);

        hdr2Cell = new PdfPCell(new Phrase(ptBean.getDeptName(), boldTextFont));
        hdr2Cell.setColspan(3);
        hdr2Cell.setBorder(Rectangle.NO_BORDER);
        hdr2Cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        hdr2Table.addCell(hdr2Cell);

        hdr2Cell = new PdfPCell(new Phrase(" ", bigTextFont));
        hdr2Cell.setBorder(Rectangle.NO_BORDER);
        hdr2Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        hdr2Table.addCell(hdr2Cell);

        hdr2Cell = new PdfPCell(new Phrase("Name of the Office: ", bigTextFont));
        hdr2Cell.setColspan(2);
        hdr2Cell.setBorder(Rectangle.NO_BORDER);
        hdr2Cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        hdr2Table.addCell(hdr2Cell);

        hdr2Cell = new PdfPCell(new Phrase(ptBean.getOfficeName(), boldTextFont));
        hdr2Cell.setColspan(3);
        hdr2Cell.setBorder(Rectangle.NO_BORDER);
        hdr2Cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        hdr2Table.addCell(hdr2Cell);

        hdr2Cell = new PdfPCell(new Phrase(" ", bigTextFont));
        hdr2Cell.setBorder(Rectangle.NO_BORDER);
        hdr2Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        hdr2Table.addCell(hdr2Cell);

        hdr2Cell = new PdfPCell(new Phrase("Designation of DDO: ", bigTextFont));
        hdr2Cell.setColspan(2);
        hdr2Cell.setBorder(Rectangle.NO_BORDER);
        hdr2Cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        hdr2Table.addCell(hdr2Cell);

        hdr2Cell = new PdfPCell(new Phrase(ptBean.getDdoDegn(), boldTextFont));
        hdr2Cell.setColspan(3);
        hdr2Cell.setBorder(Rectangle.NO_BORDER);
        hdr2Cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        hdr2Table.addCell(hdr2Cell);

        hdr2Cell = new PdfPCell(new Phrase(" ", bigTextFont));
        hdr2Cell.setBorder(Rectangle.NO_BORDER);
        hdr2Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        hdr2Table.addCell(hdr2Cell);

        hdr2Cell = new PdfPCell(new Phrase("Bill No: ", bigTextFont));
        hdr2Cell.setColspan(2);
        hdr2Cell.setBorder(Rectangle.NO_BORDER);
        hdr2Cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        hdr2Table.addCell(hdr2Cell);

        hdr2Cell = new PdfPCell(new Phrase(ptBean.getBillDesc(), boldTextFont));
        hdr2Cell.setColspan(3);
        hdr2Cell.setBorder(Rectangle.NO_BORDER);
        hdr2Cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        hdr2Table.addCell(hdr2Cell);

        hdr2Cell = new PdfPCell(new Phrase(" ", bigTextFont));
        hdr2Cell.setBorder(Rectangle.NO_BORDER);
        hdr2Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        hdr2Table.addCell(hdr2Cell);

        hdr2Cell = new PdfPCell(new Phrase(" ", bigTextFont));
        hdr2Cell.setColspan(6);
        hdr2Cell.setBorder(Rectangle.NO_BORDER);
        hdr2Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        hdr2Table.addCell(hdr2Cell);

        hdr2Cell = new PdfPCell(new Phrase(StringUtils.repeat("-", 180), bigTextFont));
        hdr2Cell.setColspan(6);
        hdr2Cell.setBorder(Rectangle.NO_BORDER);
        hdr2Cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        hdr2Table.addCell(hdr2Cell);

        hdr2Cell = new PdfPCell(new Phrase("Sl No", boldTextFont));
        hdr2Cell.setBorder(Rectangle.NO_BORDER);
        hdr2Cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        hdr2Table.addCell(hdr2Cell);

        hdr2Cell = new PdfPCell(new Phrase("Name of the Employee", boldTextFont));
        hdr2Cell.setBorder(Rectangle.NO_BORDER);
        hdr2Cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        hdr2Table.addCell(hdr2Cell);

        hdr2Cell = new PdfPCell(new Phrase("Designation", boldTextFont));
        hdr2Cell.setBorder(Rectangle.NO_BORDER);
        hdr2Cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        hdr2Table.addCell(hdr2Cell);

        hdr2Cell = new PdfPCell(new Phrase("Gross Salary", boldTextFont));
        hdr2Cell.setBorder(Rectangle.NO_BORDER);
        hdr2Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        hdr2Table.addCell(hdr2Cell);

        hdr2Cell = new PdfPCell(new Phrase("Tax on Profession", boldTextFont));
        hdr2Cell.setBorder(Rectangle.NO_BORDER);
        hdr2Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        hdr2Table.addCell(hdr2Cell);

        hdr2Cell = new PdfPCell(new Phrase("Remark", boldTextFont));
        hdr2Cell.setBorder(Rectangle.NO_BORDER);
        hdr2Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        hdr2Table.addCell(hdr2Cell);

        hdr2Cell = new PdfPCell(new Phrase(StringUtils.repeat("-", 180), bigTextFont));
        hdr2Cell.setColspan(6);
        hdr2Cell.setBorder(Rectangle.NO_BORDER);
        hdr2Table.addCell(hdr2Cell);
    }

    private void printPageFooterPT(PtScheduleBean ptbean, PdfPTable dataTable, PdfPCell dataCell, double totAmt, String totGross) throws Exception {

        Font textFont = new Font(Font.FontFamily.TIMES_ROMAN, 8.4f, Font.NORMAL, BaseColor.BLACK);
        Font bigTextFont = new Font(Font.FontFamily.TIMES_ROMAN, 8.6f, Font.NORMAL, BaseColor.BLACK);
        Font boldTextFont = new Font(Font.FontFamily.TIMES_ROMAN, 8.6f, Font.BOLD, BaseColor.BLACK);

        dataCell = new PdfPCell(new Phrase("", textFont));
        dataCell.setColspan(6);
        dataCell.setBorder(Rectangle.BOTTOM);
        dataTable.addCell(dataCell);

        dataCell = new PdfPCell(new Phrase("TOTAL FOR THE MONTH OF " + ptbean.getMonthYear(), textFont));
        dataCell.setBorder(Rectangle.BOTTOM);
        dataCell.setColspan(3);
        dataCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        dataTable.addCell(dataCell);

        dataCell = new PdfPCell(new Phrase(totGross, boldTextFont));
        dataCell.setBorder(Rectangle.BOTTOM);
        dataCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        dataTable.addCell(dataCell);

        dataCell = new PdfPCell(new Phrase("     " + totAmt + "\n" + "Rupees ( " + Numtowordconvertion.convertNumber((int) totAmt) + ") Only", boldTextFont));
        dataCell.setBorder(Rectangle.BOTTOM);
        dataCell.setColspan(2);
        dataCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        dataTable.addCell(dataCell);

        dataCell = new PdfPCell(new Phrase(" ", textFont));
        dataCell.setColspan(6);
        dataCell.setBorder(Rectangle.NO_BORDER);
        dataCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        dataTable.addCell(dataCell);

        dataCell = new PdfPCell(new Phrase("", bigTextFont));
        dataCell.setColspan(3);
        dataCell.setBorder(Rectangle.NO_BORDER);
        dataCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        dataTable.addCell(dataCell);

        dataCell = new PdfPCell(new Phrase("Signature of D.D.O.", bigTextFont));
        dataCell.setColspan(3);
        dataCell.setBorder(Rectangle.NO_BORDER);
        dataCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        dataTable.addCell(dataCell);

        dataCell = new PdfPCell(new Phrase("", bigTextFont));
        dataCell.setColspan(2);
        dataCell.setBorder(Rectangle.NO_BORDER);
        dataTable.addCell(dataCell);

        dataCell = new PdfPCell(new Phrase(ptbean.getOfficeName(), textFont));
        dataCell.setColspan(4);
        dataCell.setBorder(Rectangle.NO_BORDER);
        dataCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        dataTable.addCell(dataCell);

        dataCell = new PdfPCell(new Phrase("", bigTextFont));
        dataCell.setColspan(4);
        dataCell.setBorder(Rectangle.NO_BORDER);
        dataCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        dataTable.addCell(dataCell);

        dataCell = new PdfPCell(new Phrase("Date :", textFont));
        dataCell.setColspan(2);
        dataCell.setBorder(Rectangle.NO_BORDER);
        dataCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        dataTable.addCell(dataCell);

        dataCell = new PdfPCell(new Phrase("To be filled by the Treasury Officer/ Sub Treasury Officer/ Special Treasury Officer", textFont));
        dataCell.setColspan(6);
        dataCell.setBorder(Rectangle.NO_BORDER);
        dataCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        dataTable.addCell(dataCell);

        dataCell = new PdfPCell(new Phrase("a. 	T.V. No. _____________ and Date ____________ of encashment of Bill", textFont));
        dataCell.setColspan(6);
        dataCell.setBorder(Rectangle.NO_BORDER);
        dataCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        dataTable.addCell(dataCell);

        dataCell = new PdfPCell(new Phrase("b. 	Sl. No. _____________ and Date ____________ of the receipt Schedule in which accounted by Transfer Credit.", textFont));
        dataCell.setColspan(6);
        dataCell.setBorder(Rectangle.NO_BORDER);
        dataCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        dataTable.addCell(dataCell);

    }

    @Override
    public List getHCScheduleEmpList(String billNo, int year, int month) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        List emplist = new ArrayList();

        HCScheduleBean bean = null;
        try {
            con = this.dataSource.getConnection();

            String sql = "select aq_mast.emp_code,emp_name,cur_desg,cur_basic,ad_amt,id_no,aq_mast.aqsl_no from aq_mast"
                    + " inner join aq_dtls on aq_mast.aqsl_no=aq_dtls.aqsl_no and aq_mast.aq_year=aq_dtls.aq_year and aq_mast.aq_month=aq_dtls.aq_year"
                    + " inner join bill_mast on aq_mast.bill_no=bill_mast.bill_no"
                    + " left outer join (select ID_NO,EMP_ID from EMP_ID_DOC where ID_DESCRIPTION='PAN')EMP_ID_DOC on aq_mast.emp_code=EMP_ID_DOC.emp_id where bill_mast.bill_no=? and AQ_DTLS.SCHEDULE='HC' AND AQ_DTLS.AD_TYPE='D' AND AD_AMT > 0";
            pst = con.prepareStatement(sql);
            pst.setInt(1, Integer.parseInt(billNo));
            rs = pst.executeQuery();
            while (rs.next()) {
                bean = new HCScheduleBean();
                bean.setEmpCode(rs.getString("emp_code"));
                bean.setEmpName(rs.getString("emp_name"));
                bean.setDesg(rs.getString("cur_desg"));
                bean.setBasic(rs.getString("cur_basic"));
                bean.setAmt(rs.getString("ad_amt"));
                bean.setIdNumber(rs.getString("id_no"));
                emplist.add(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return emplist;
    }

    @Override
    public void LoanSchedulePagePDF(Document document, String schedule, String billNo, LoanAdvanceScheduleBean laBean, List priList, List intList) {

        int slno = 0;
        int pageNo = 0;

        double totalP = 0.0;
        double totalI = 0.0;
        String totalIFig = null;
        LoanAdvanceScheduleBean laBeanP = null;
        try {

            Font textFont = new Font(Font.FontFamily.TIMES_ROMAN, 8.4f, Font.NORMAL, BaseColor.BLACK);
            Font bigTextFont = new Font(Font.FontFamily.TIMES_ROMAN, 8.6f, Font.NORMAL, BaseColor.BLACK);
            Font boldTextFont = new Font(Font.FontFamily.TIMES_ROMAN, 8.6f, Font.BOLD, BaseColor.BLACK);

            // CREATING A BLANK TABLE WITH A BLANK CELL    
            PdfPTable blankTable = null;
            blankTable = new PdfPTable(1);
            blankTable.setWidthPercentage(100);

            PdfPCell blankCell = null;
            blankCell = new PdfPCell(new Phrase(" ", textFont));
            blankCell.setBorder(Rectangle.NO_BORDER);
            blankTable.addCell(blankCell);

            PdfPTable table = null;
            table = new PdfPTable(9);
            table.setWidths(new int[]{3, 15, 8, 6, 8, 6, 6, 7, 6});
            table.setWidthPercentage(100);

            PdfPCell cell = null;

            if (priList != null && priList.size() > 0) {

                table = new PdfPTable(9);
                table.setWidths(new int[]{3, 15, 8, 6, 8, 6, 6, 7, 6});
                table.setWidthPercentage(100);

                Iterator itr = priList.iterator();
                while (itr.hasNext()) {
                    laBeanP = (LoanAdvanceScheduleBean) itr.next();

                    slno++;
                    if (pageNo == 0) {
                        pageNo++;

                        printHeaderLoanSch(table, cell, schedule, laBean, pageNo);
                        printBillDescLoanSch(table, cell, laBean, "PRINCIPAL");
                    }
                    totalP = laBeanP.getTotal();

                    //1st row inside while
                    if (laBeanP.getSlno() > 0) {
                        cell = new PdfPCell(new Phrase(laBeanP.getSlno() + "", textFont));
                    } else {
                        cell = new PdfPCell(new Phrase("", textFont));
                    }
                    cell.setBorder(Rectangle.NO_BORDER);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase(laBeanP.getEmpNameDesg(), textFont));
                    cell.setBorder(Rectangle.NO_BORDER);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase(laBeanP.getVchNo() + " / " + laBeanP.getVchDate(), textFont));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBorder(Rectangle.NO_BORDER);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase(laBeanP.getOriginalAmt() + "", textFont));
                    cell.setBorder(Rectangle.NO_BORDER);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase(laBeanP.getInstalmentRec() + "", textFont));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBorder(Rectangle.NO_BORDER);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase(laBeanP.getDecutedAmt() + "", textFont));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBorder(Rectangle.NO_BORDER);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase(laBeanP.getRecAmt() + "", textFont));
                    cell.setBorder(Rectangle.NO_BORDER);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase(laBeanP.getBalOutstanding() + "", textFont));
                    cell.setBorder(Rectangle.NO_BORDER);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase(laBeanP.getGpfNo() + "", textFont));
                    cell.setBorder(Rectangle.NO_BORDER);
                    table.addCell(cell);

                    if (slno % 18 == 0) {

                        printTotalLoan(table, cell, totalP, schedule, laBean, pageNo);
                        document.add(table);
                        document.newPage();

                        table = new PdfPTable(9);
                        table.setWidths(new int[]{3, 15, 8, 6, 8, 6, 6, 7, 6});
                        table.setWidthPercentage(100);

                        pageNo++;
                        printPageNo(pageNo, document);
                        printBillDescLoanSch(table, cell, laBean, "PRINCIPAL");

                    }
                    if (slno == priList.size()) {
                        printTotalLoan(table, cell, totalP, schedule, laBean, pageNo);
                        printPageSignLoan(table, cell, totalP, schedule, laBean);
                        document.add(table);
                        document.newPage();
                    }

                }
            } else {
                pageNo++;
                printHeaderLoanSch(table, cell, schedule, laBean, pageNo);
                printBillDescLoanSch(table, cell, laBean, "PRINCIPAL");

                cell = new PdfPCell(new Phrase(" There is no record ", bigTextFont));
                cell.setBorder(Rectangle.BOTTOM);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setColspan(9);
                table.addCell(cell);

                document.newPage();
                //document.add(table);
            }

            if (intList != null && intList.size() > 0) {
                slno = 0;
                int intPageNo = pageNo;
                int intPageNo1 = 0;

                table = new PdfPTable(9);
                table.setWidths(new int[]{3, 15, 8, 6, 8, 6, 6, 7, 6});
                table.setWidthPercentage(100);

                Iterator itr = intList.iterator();
                while (itr.hasNext()) {
                    laBeanP = (LoanAdvanceScheduleBean) itr.next();

                    slno++;
                    if (intPageNo1 == 0) {
                        intPageNo1++;

                        intPageNo++;
                        printHeaderLoanSch(table, cell, schedule, laBean, intPageNo);
                        printBillDescLoanSch(table, cell, laBean, "INTEREST");
                    }
                    totalP = laBeanP.getTotal();

                    //1st row inside while
                    if (laBeanP.getSlno() > 0) {
                        cell = new PdfPCell(new Phrase(laBeanP.getSlno() + "", textFont));
                    } else {
                        cell = new PdfPCell(new Phrase("", textFont));
                    }
                    cell.setBorder(Rectangle.NO_BORDER);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase(laBeanP.getEmpNameDesg(), textFont));
                    cell.setBorder(Rectangle.NO_BORDER);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase(laBeanP.getVchNo() + " / " + laBeanP.getVchDate(), textFont));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBorder(Rectangle.NO_BORDER);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase(laBeanP.getOriginalAmt() + "", textFont));
                    cell.setBorder(Rectangle.NO_BORDER);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase(laBeanP.getInstalmentRec() + "", textFont));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBorder(Rectangle.NO_BORDER);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase(laBeanP.getDecutedAmt() + "", textFont));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBorder(Rectangle.NO_BORDER);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase(laBeanP.getRecAmt() + "", textFont));
                    cell.setBorder(Rectangle.NO_BORDER);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase(laBeanP.getBalOutstanding() + "", textFont));
                    cell.setBorder(Rectangle.NO_BORDER);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase(laBeanP.getGpfNo() + "", textFont));
                    cell.setBorder(Rectangle.NO_BORDER);
                    table.addCell(cell);

                    if (slno % 18 == 0) {

                        printTotalLoan(table, cell, totalP, schedule, laBean, intPageNo);
                        document.add(table);
                        document.newPage();

                        table = new PdfPTable(9);
                        table.setWidths(new int[]{3, 15, 8, 6, 8, 6, 6, 7, 6});
                        table.setWidthPercentage(100);

                        intPageNo++;
                        printPageNo(intPageNo, document);
                        printBillDescLoanSch(table, cell, laBean, "INTEREST");

                    }
                    if (slno == intList.size()) {
                        printTotalLoan(table, cell, totalP, schedule, laBean, intPageNo);
                        printPageSignLoan(table, cell, totalP, schedule, laBean);
                        document.add(table);
                    }

                }
            } else {
                pageNo++;
                printHeaderLoanSch(table, cell, schedule, laBean, pageNo);
                printBillDescLoanSch(table, cell, laBean, "INTEREST");

                cell = new PdfPCell(new Phrase(" There is no record ", bigTextFont));
                cell.setBorder(Rectangle.BOTTOM);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setColspan(9);
                table.addCell(cell);

                document.add(table);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printPageSignLoan(PdfPTable table, PdfPCell cell, double total, String schedule, LoanAdvanceScheduleBean laBean) {

        Font textFont = new Font(Font.FontFamily.TIMES_ROMAN, 8.4f, Font.NORMAL, BaseColor.BLACK);
        Font boldTextFont = new Font(Font.FontFamily.TIMES_ROMAN, 8.6f, Font.BOLD, BaseColor.BLACK);

        cell = new PdfPCell(new Phrase(" ", boldTextFont));
        cell.setColspan(9);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(" ", boldTextFont));
        cell.setColspan(9);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(" ", boldTextFont));
        cell.setColspan(6);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Signature of D.D.O.", textFont));
        cell.setColspan(3);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(" ", boldTextFont));
        cell.setColspan(6);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(laBean.getDdoName(), textFont));
        cell.setColspan(3);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(" ", boldTextFont));
        cell.setColspan(6);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Date : ", textFont));
        cell.setColspan(3);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(StringUtils.repeat("-", 181), textFont));
        cell.setColspan(9);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

    }

    private void printTotalLoan(PdfPTable table, PdfPCell cell, double total, String schedule, LoanAdvanceScheduleBean laBean, int pageno) {

        Font bigTextFont = new Font(Font.FontFamily.TIMES_ROMAN, 8.6f, Font.NORMAL, BaseColor.BLACK);
        Font boldTextFont = new Font(Font.FontFamily.TIMES_ROMAN, 8.6f, Font.BOLD, BaseColor.BLACK);

        cell = new PdfPCell(new Phrase(StringUtils.repeat("-", 181), bigTextFont));
        cell.setColspan(9);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(" RECOVERY FOR THE MONTH OF :" + laBean.getMonth() + "-" + laBean.getYear(), boldTextFont));
        cell.setColspan(5);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(total + "", boldTextFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(" ", boldTextFont));
        cell.setColspan(3);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(" ", boldTextFont));
        cell.setColspan(4);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(" RUPEES " + Numtowordconvertion.convertNumber((int) total).toUpperCase() + " Only", boldTextFont));
        cell.setColspan(5);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(StringUtils.repeat("-", 181), bigTextFont));
        cell.setColspan(9);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

    }

    private void printHeaderLoanSch(PdfPTable table, PdfPCell cell, String schedule, LoanAdvanceScheduleBean laBean, int pageNo) throws Exception {

        Font hdrTextFont = new Font(Font.FontFamily.TIMES_ROMAN, 10f, Font.BOLD, BaseColor.BLACK);

        cell = new PdfPCell(new Phrase("Page No : ", hdrTextFont));
        cell.setColspan(8);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(pageNo + " ", hdrTextFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(StringUtils.defaultString(laBean.getScheduleOfRecovery()), hdrTextFont));
        cell.setColspan(9);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("FOR THE MONTH OF : " + laBean.getMonth() + "-" + laBean.getYear(), hdrTextFont));
        cell.setColspan(9);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Demand No : " + laBean.getDemandNo() + "-" + laBean.getScheduleName(), hdrTextFont));
        cell.setColspan(9);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(" **********-*-********** ", hdrTextFont));
        cell.setColspan(9);
        cell.setFixedHeight(20);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

    }

    private void printBillDescLoanSch(PdfPTable table, PdfPCell cell, LoanAdvanceScheduleBean laBean, String hdrString) throws Exception {

        Font bigTextFont = new Font(Font.FontFamily.TIMES_ROMAN, 8.6f, Font.NORMAL, BaseColor.BLACK);
        Font boldTextFont = new Font(Font.FontFamily.TIMES_ROMAN, 8.6f, Font.BOLD, BaseColor.BLACK);

        cell = new PdfPCell(new Phrase("Name of the Department : ", boldTextFont));
        cell.setColspan(2);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(laBean.getDeptName(), bigTextFont));
        cell.setColspan(7);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Name of the Office : ", boldTextFont));
        cell.setColspan(2);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(laBean.getOffName(), bigTextFont));
        cell.setColspan(7);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Designation of DDO : ", boldTextFont));
        cell.setColspan(2);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(laBean.getDdoName(), bigTextFont));
        cell.setColspan(7);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Name of Treasury : ", boldTextFont));
        cell.setColspan(2);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(laBean.getTrName(), bigTextFont));
        cell.setColspan(7);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Bill No : ", boldTextFont));
        cell.setColspan(2);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(laBean.getBilldesc(), bigTextFont));
        cell.setColspan(7);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(" ", bigTextFont));
        cell.setColspan(9);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Sl. No.", boldTextFont));
        cell.setRowspan(2);
        cell.setBorder(Rectangle.BOX);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Name of the Employee / Designation", boldTextFont));
        cell.setRowspan(2);
        cell.setBorder(Rectangle.BOX);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Month in which Original Advance was Drawn", boldTextFont));
        cell.setRowspan(2);
        cell.setBorder(Rectangle.BOX);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(hdrString, boldTextFont));
        cell.setColspan(6);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(Rectangle.BOX);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Amount of Original Advance", boldTextFont));
        cell.setBorder(Rectangle.BOX);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("No of Installment of Recovery", boldTextFont));
        cell.setBorder(Rectangle.BOX);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Amount Deducted in the Bill", boldTextFont));
        cell.setBorder(Rectangle.BOX);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Recovery Upto the Month", boldTextFont));
        cell.setBorder(Rectangle.BOX);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Balance Outstanding", boldTextFont));
        cell.setBorder(Rectangle.BOX);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Remarks", boldTextFont));
        cell.setBorder(Rectangle.BOX);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

    }

    @Override
    public List getLTCScheduleEmpList(String billNo, int year, int month) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        List emplist = new ArrayList();

        LTCScheduleBean ltcBean = null;

        String aqDtlsTbl = "AQ_DTLS";
        try {
            con = this.dataSource.getConnection();

            if ((year == 2017 && month < 2) || year < 2017) {
                aqDtlsTbl = "hrmis.AQ_DTLS1";
            }

            String sql = "SELECT BILL_MAST.VCH_NO,BILL_MAST.VCH_DATE,DTL.EMP_CODE,DTL.EMP_NAME,DTL.GPF_ACC_NO,DTL.CUR_DESG,DTL.CUR_BASIC,DTL.ACC_NO,DTL.AD_AMT,"
                    + " DTL.AQSL_NO,P_ORG_AMT,REF_DESC,TOT_REC_AMT FROM ("
                    + " (SELECT * FROM BILL_MAST WHERE BILL_MAST.BILL_NO=?)BILL_MAST LEFT OUTER JOIN"
                    + " (SELECT AQ_MAST.EMP_CODE,AQ_MAST.GPF_ACC_NO,AQ_MAST.BILL_NO,AQ_MAST.CUR_DESG,AQ_MAST.EMP_NAME,"
                    + " AQ_MAST.CUR_BASIC,AQ_MAST.POST_SL_NO,AQ_DTLS.TOT_REC_AMT,AQ_DTLS.REF_DESC,AQ_DTLS.AD_REF_ID,"
                    + " AQ_DTLS.ACC_NO,AQ_DTLS.AD_AMT,AQ_DTLS.AQSL_NO"
                    + " FROM((SELECT * FROM AQ_MAST WHERE AQ_MAST.BILL_NO=?)AQ_MAST INNER JOIN (SELECT *"
                    + " FROM " + aqDtlsTbl + " WHERE " + aqDtlsTbl + ".SCHEDULE='LTCA' AND " + aqDtlsTbl + ".AD_TYPE='D' AND AD_AMT >0)AQ_DTLS ON"
                    + " AQ_DTLS.AQSL_NO=AQ_MAST.AQSL_NO))DTL ON BILL_MAST.BILL_NO=DTL.BILL_NO"
                    + " left outer join emp_loan_sanc on dtl.AD_REF_ID=emp_loan_sanc.loanid) order by POST_SL_NO";
            pst = con.prepareStatement(sql);
            pst.setInt(1, Integer.parseInt(billNo));
            pst.setInt(2, Integer.parseInt(billNo));
            rs = pst.executeQuery();
            while (rs.next()) {
                ltcBean = new LTCScheduleBean();
                ltcBean.setEmpName(rs.getString("EMP_NAME"));
                ltcBean.setEmpDesg(rs.getString("CUR_DESG"));
                ltcBean.setGpfNo(rs.getString("GPF_ACC_NO"));

                ltcBean.setAdAmt(rs.getInt("AD_AMT"));
                ltcBean.setPrincipalAmt(rs.getInt("P_ORG_AMT"));
                ltcBean.setTotRecAmt(rs.getInt("TOT_REC_AMT"));
                ltcBean.setRefDesc(rs.getString("REF_DESC"));
                ltcBean.setAccNo(rs.getString("ACC_NO"));

                ltcBean.setVchNo(rs.getString("VCH_NO"));
                emplist.add(ltcBean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return emplist;
    }

    @Override
    public String getChartOfAccount(String billNo) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        String chartOfAccount = "";
        try {
            con = this.dataSource.getConnection();

            String sql = "SELECT DEMAND_NO,MAJOR_HEAD,SUB_MAJOR_HEAD,MINOR_HEAD,SUB_MINOR_HEAD1,SUB_MINOR_HEAD2,SUB_MINOR_HEAD3,PLAN,SECTOR FROM BILL_MAST WHERE BILL_NO=?";
            pst = con.prepareStatement(sql);
            pst.setInt(1, Integer.parseInt(billNo));
            rs = pst.executeQuery();
            if (rs.next()) {
                chartOfAccount = StringUtils.defaultString(rs.getString("DEMAND_NO")) + "-" + StringUtils.defaultString(rs.getString("MAJOR_HEAD")) + "-" + StringUtils.defaultString(rs.getString("SUB_MAJOR_HEAD")) + "-" + StringUtils.defaultString(rs.getString("MINOR_HEAD")) + "-" + StringUtils.defaultString(rs.getString("SUB_MINOR_HEAD1")) + "-" + StringUtils.defaultString(rs.getString("SUB_MINOR_HEAD2")) + "-" + StringUtils.defaultString(rs.getString("PLAN")) + "-" + StringUtils.defaultString(rs.getString("SUB_MINOR_HEAD3")) + "-" + StringUtils.defaultString(rs.getString("SECTOR"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return chartOfAccount;
    }

    @Override
    public VehicleScheduleBean getVehicleScheduleList(String billNo, int year, int month) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        List vehiclelist = new ArrayList();

        VehicleScheduleBean vBean = new VehicleScheduleBean();

        int totMCAPri = 0;
        int totMCAInt = 0;

        int totMOPAPri = 0;
        int totMOPAInt = 0;

        int totVEPri = 0;
        int totVEInt = 0;
        try {
            con = this.dataSource.getConnection();

            String sql = "SELECT AD_AMT,AD_CODE,NOW_DEDN FROM(SELECT AD_AMT,AD_CODE,NOW_DEDN FROM (SELECT AQSL_NO from AQ_MAST WHERE BILL_NO = ?)AQMAST INNER JOIN (SELECT AQSL_NO,AD_AMT,AD_CODE,NOW_DEDN FROM AQ_DTLS WHERE AD_AMT>0)AQDTLS ON AQDTLS.AQSL_NO=AQMAST.AQSL_NO)TAB INNER JOIN G_LOAN ON G_LOAN.LOAN_TP=TAB.AD_CODE AND G_LOAN.IS_VEHICLE_LOAN='Y'";
            pst = con.prepareStatement(sql);
            pst.setInt(1, Integer.parseInt(billNo));
            rs = pst.executeQuery();
            while (rs.next()) {
                if (rs.getString("AD_CODE").equals("MOPA") && rs.getString("NOW_DEDN").equals("P")) {
                    totMCAPri += rs.getInt("AD_AMT");
                }
                if (rs.getString("AD_CODE").equals("MOPA") && rs.getString("NOW_DEDN").equals("P")) {
                    totMOPAPri += rs.getInt("AD_AMT");;
                }
                if (rs.getString("AD_CODE").equals("VE") && rs.getString("NOW_DEDN").equals("P")) {
                    totVEPri += rs.getInt("AD_AMT");
                }
                if (rs.getString("AD_CODE").equals("MCA") && rs.getString("NOW_DEDN").equals("I")) {
                    totMCAInt += rs.getInt("AD_AMT");
                }
                if (rs.getString("AD_CODE").equals("MOPA") && rs.getString("NOW_DEDN").equals("I")) {
                    totMOPAInt += rs.getInt("AD_AMT");
                }
                if (rs.getString("AD_CODE").equals("VE") && rs.getString("NOW_DEDN").equals("I")) {
                    totVEInt += rs.getInt("AD_AMT");
                }
            }
            vBean.setTotMCAPri(totMCAPri);
            vBean.setTotMCAInt(totMCAInt);
            vBean.setTotMopaPri(totMOPAPri);
            vBean.setTotMopaInt(totMOPAInt);
            vBean.setTotVEPri(totVEPri);
            vBean.setTotVEInt(totVEInt);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return vBean;
    }

    @Override
    public SelectOption getBTPIHeaderClassification(String loantp) {
        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        SelectOption so = new SelectOption();
        try {
            con = this.dataSource.getConnection();
            //con = this.getDBConnection();

            String sql = "select bt_head_principal_classification,bt_head_interest_classification from g_loan where loan_tp=?";
            pst = con.prepareStatement(sql);
            pst.setString(1, loantp);
            rs = pst.executeQuery();
            if (rs.next()) {
                so.setLabel(rs.getString("bt_head_principal_classification"));
                so.setValue(rs.getString("bt_head_interest_classification"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return so;
    }

    @Override
    public void generateGISSchedulePDFReportsForAG(Document document, String schedule, ArrayList billList) {

        String majorhead = "";

        String loanName = "";

        String nowDedn = "";
        //String[] nowDednArr = {"P"};
        String nowDednLabel = "Principal";

        ArrayList majorheadPriList = new ArrayList();

        MajorHeadAttribute mha = null;
        ArrayList vchdataPriList = new ArrayList();

        List empdatalist = null;
        try {
            Font f1 = new Font();
            f1.setSize(10);
            f1.setFamily("Times New Roman");

            PdfPTable table = null;
            PdfPTable innertable = null;

            PdfPCell cell = null;
            PdfPCell innercell = null;

            if (schedule.equals("CMPA")) {
                loanName = "COMPUTER";
            } else if (schedule.equals("HBA")) {
                loanName = "HOUSE BUILDING ADVANCE";
            } else if (schedule.equals("MCA")) {
                loanName = "MOTOR CYCLE ADVANCE";
            } else if (schedule.equals("GA")) {
                loanName = "GPF ADVANCE";
            } else if (schedule.equals("GIS")) {
                loanName = "AISGIS";
            } else if (schedule.equals("CGEGIS")) {
                loanName = "CGEGIS";
            }

            double pTotAmt = 0;

            if (billList != null && billList.size() > 0) {
                for (int i = 0; i < billList.size(); i++) {
                    //for (int i = 0; i < 1; i++) {
                    BillBean bb = (BillBean) billList.get(i);
                    //System.out.println("Major Head is: " + bb.getMajorhead());

                    innertable = new PdfPTable(5);
                    innertable.setWidths(new float[]{0.5f, 3f, 1.7f, 1.7f, 1});
                    innertable.setWidthPercentage(100);

                    empdatalist = getEmployeeAquitanceDetailsForAG(bb.getBillno(), bb.getBillYear(), bb.getBillMonth(), schedule, nowDedn);
                    String billmonth = (bb.getBillMonth() + 1) + "-" + bb.getBillYear();
                    if (empdatalist != null && empdatalist.size() > 0) {
                        //System.out.println("empdatalist size is: " + empdatalist.size());
                        table = new PdfPTable(4);
                        table.setWidths(new int[]{2, 2, 2, 2});
                        table.setWidthPercentage(100);
                        if (majorhead.equals("")) {
                            majorhead = bb.getMajorhead();

                            mha = new MajorHeadAttribute();
                            mha.setMajorhead(majorhead);

                            printGISHeader(table, cell, f1, majorhead, bb.getTreasurycode(), loanName, nowDednLabel);
                        }
                        if (!majorhead.equals(bb.getMajorhead())) {
                            majorhead = bb.getMajorhead();

                            mha.setVchPriList(vchdataPriList);
                            majorheadPriList.add(mha);

                            mha = new MajorHeadAttribute();
                            mha.setMajorhead(majorhead);

                            vchdataPriList = new ArrayList();
                            printGISHeader(table, cell, f1, majorhead, bb.getTreasurycode(), loanName, nowDednLabel);
                        }

                        printGISSubHeader(innertable, innercell, f1, bb.getMajorhead() + "/" + bb.getVoucherno(), bb.getVoucherdate(), bb.getDdocode(),bb.getDdoname());

                        cell = new PdfPCell();
                        cell.setColspan(4);
                        cell.setFixedHeight(20);
                        cell.setBorder(Rectangle.NO_BORDER);
                        table.addCell(cell);

                        document.add(table);
                        int billrecovamt = 0;
                        AquitanceDataAGBean agbean = null;
                        for (int j = 0; j < empdatalist.size(); j++) {
                            agbean = (AquitanceDataAGBean) empdatalist.get(j);

                            innercell = new PdfPCell(new Phrase((j + 1) + "", f1));
                            //innercell.setBorder(Rectangle.NO_BORDER);
                            innertable.addCell(innercell);
                            innercell = new PdfPCell(new Phrase(agbean.getEmpname() + "\n" + agbean.getDesg(), f1));
                            //innercell.setBorder(Rectangle.NO_BORDER);
                            innertable.addCell(innercell);
                            innercell = new PdfPCell(new Phrase(agbean.getAdAmt(), f1));
                            billrecovamt = billrecovamt + Integer.parseInt(agbean.getAdAmt());
                            //innercell.setBorder(Rectangle.NO_BORDER);
                            innertable.addCell(innercell);
                            innercell = new PdfPCell(new Phrase(agbean.getGpfNo(), f1));
                            //innercell.setBorder(Rectangle.NO_BORDER);
                            innertable.addCell(innercell);

                            double pTemp = Double.parseDouble(agbean.getAdAmt());
                            pTotAmt = pTotAmt + pTemp;

                            innercell = new PdfPCell(new Phrase(billmonth, f1));
                            //innercell.setBorder(Rectangle.NO_BORDER);
                            innertable.addCell(innercell);
                        }

                        innercell = new PdfPCell(new Phrase(" ", f1));
                        innercell.setBorder(Rectangle.NO_BORDER);
                        innertable.addCell(innercell);
                        innercell = new PdfPCell(new Phrase(" ", f1));
                        innercell.setBorder(Rectangle.NO_BORDER);
                        innertable.addCell(innercell);
                        innercell = new PdfPCell(new Phrase(billrecovamt + "", f1));
                        innercell.setBorder(Rectangle.NO_BORDER);
                        innertable.addCell(innercell);
                        innercell = new PdfPCell(new Phrase(" ", f1));
                        innercell.setBorder(Rectangle.NO_BORDER);
                        innertable.addCell(innercell);
                        innercell = new PdfPCell(new Phrase(" ", f1));
                        innercell.setBorder(Rectangle.NO_BORDER);
                        innertable.addCell(innercell);

                        SelectOption so = new SelectOption();
                        so.setLabel(bb.getVoucherno());
                        so.setValue(pTotAmt + "");
                        vchdataPriList.add(so);

                        pTotAmt = 0;

                        innercell = new PdfPCell();
                        innercell.setColspan(5);
                        innercell.setFixedHeight(20);
                        innercell.setBorder(Rectangle.NO_BORDER);
                        innertable.addCell(innercell);
                    }
                    /*innercell = new PdfPCell();
                     innercell.setColspan(9);
                     innercell.setFixedHeight(20);
                     innercell.setBorder(Rectangle.NO_BORDER);
                     innertable.addCell(innercell);*/
                    if (empdatalist != null && empdatalist.size() > 0) {
                        document.add(innertable);
                    }
                }
                mha = new MajorHeadAttribute();
                mha.setMajorhead(majorhead);
                mha.setVchPriList(vchdataPriList);
                majorheadPriList.add(mha);
            }
            if (empdatalist != null && empdatalist.size() > 0) {
                document.add(table);
                document.newPage();
            }
            System.out.println("majorheadPriList size is:============================================================== " + majorheadPriList.size());
            if (majorheadPriList != null && majorheadPriList.size() > 0) {

                table = new PdfPTable(2);
                table.setWidths(new int[]{3, 2});
                table.setWidthPercentage(70);

                cell = new PdfPCell(new Phrase("SUMMARY", getDesired_PDF_Font(11, true, true)));
                cell.setColspan(2);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell();
                cell.setColspan(2);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setFixedHeight(10);
                table.addCell(cell);

                mha = null;
                for (int i = 0; i < majorheadPriList.size(); i++) {
                    mha = (MajorHeadAttribute) majorheadPriList.get(i);

                    cell = new PdfPCell(new Phrase("Major Head:" + StringUtils.repeat(" ", 50) + mha.getMajorhead(), getDesired_PDF_Font(11, true, false)));
                    cell.setColspan(2);
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                    innertable = new PdfPTable(2);
                    innertable.setWidths(new int[]{1, 1});
                    innertable.setWidthPercentage(70);

                    double tvPTot = 0;

                    for (int j = 0; j < mha.getVchPriList().size(); j++) {
                        SelectOption so = (SelectOption) mha.getVchPriList().get(j);

                        innercell = new PdfPCell(new Phrase(so.getLabel(), f1));
                        innercell.setBorder(Rectangle.NO_BORDER);
                        innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        innertable.addCell(innercell);
                        innercell = new PdfPCell(new Phrase(so.getValue(), f1));
                        innercell.setBorder(Rectangle.NO_BORDER);
                        innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        innertable.addCell(innercell);

                        double tvPTotTemp = Double.parseDouble(so.getValue());
                        tvPTot = tvPTot + tvPTotTemp;
                    }

                    innercell = new PdfPCell(new Phrase("Total", getDesired_PDF_Font(11, true, false)));
                    innercell.setBorder(Rectangle.NO_BORDER);
                    innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    innertable.addCell(innercell);
                    innercell = new PdfPCell(new Phrase(tvPTot + "", getDesired_PDF_Font(11, true, false)));
                    innercell.setBorder(Rectangle.NO_BORDER);
                    innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    innertable.addCell(innercell);

                    cell = new PdfPCell(innertable);
                    cell.setColspan(2);
                    cell.setBorder(Rectangle.NO_BORDER);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase(StringUtils.repeat("_", 52), f1));
                    cell.setColspan(2);
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                }
                document.add(table);
            }
            //document.newPage();

            //document.add(table);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

    }

    @Override
    public void generateSchedulePDFReportsForAG(Document document, String schedule, ArrayList billList) {

        String majorhead = "";

        String majorheadData = "";

        String loanName = "";

        String nowDedn = "";
        String[] nowDednArr = {"P", "I"};
        String nowDednLabel = "Principal";

        ArrayList majorheadPriList = new ArrayList();
        ArrayList majorheadIntList = new ArrayList();

        MajorHeadAttribute mha = null;
        ArrayList vchdataPriList = new ArrayList();
        ArrayList vchdataIntList = new ArrayList();

        List empdatalist = null;

        try {
            Font f1 = new Font();
            f1.setSize(10);
            f1.setFamily("Times New Roman");

            PdfPTable table = null;
            PdfPTable innertable = null;

            PdfPCell cell = null;
            PdfPCell innercell = null;

            if (schedule.equals("CMPA")) {
                loanName = "COMPUTER";
            } else if (schedule.equals("HBA")) {
                loanName = "HOUSE BUILDING ADVANCE";
            } else if (schedule.equals("MCA")) {
                loanName = "MOTOR CYCLE ADVANCE";
            } else if (schedule.equals("GA")) {
                loanName = "GPF ADVANCE";
            } else if (schedule.equals("GIS")) {
                loanName = "GIS";
            }

            double pTotAmt = 0;
            double iTotAmt = 0;
            /*if (nowDedn.equals("I")) {
             nowDednLabel = "Interest";
             }*/
            int index = 0;

            if (billList != null && billList.size() > 0) {
                while (index <= 1) {
                    nowDedn = nowDednArr[index];
                    if (nowDedn.equals("I")) {
                        nowDednLabel = "Interest";
                    }
                    majorhead = "";
                    for (int i = 0; i < billList.size(); i++) {
                        //for (int i = 0; i < 1; i++) {
                        BillBean bb = (BillBean) billList.get(i);
                        //System.out.println("Major Head is: " + bb.getMajorhead());

                        innertable = new PdfPTable(10);
                        innertable.setWidths(new float[]{0.5f, 2.6f, 1.4f, 1, 1.1f, 1, 1.2f, 1.7f, 0.7f, 1});
                        innertable.setWidthPercentage(100);

                        empdatalist = getEmployeeAquitanceDetailsForAG(bb.getBillno(), bb.getBillYear(), bb.getBillMonth(), schedule, nowDedn);
                        String billmonth = (bb.getBillMonth() + 1) + "-" + bb.getBillYear();
                        if (empdatalist != null && empdatalist.size() > 0) {
                            //System.out.println("empdatalist size is: " + empdatalist.size());
                            table = new PdfPTable(4);
                            table.setWidths(new int[]{2, 2, 2, 2});
                            table.setWidthPercentage(100);
                            if (majorhead.equals("")) {
                                majorhead = bb.getMajorhead();

                                mha = new MajorHeadAttribute();
                                mha.setMajorhead(majorhead);

                                printHeader(table, cell, f1, majorhead, bb.getTreasurycode(), loanName, nowDednLabel);
                            }
                            if (!majorhead.equals(bb.getMajorhead())) {
                                majorhead = bb.getMajorhead();

                                if (nowDedn.equals("P")) {
                                    mha.setVchPriList(vchdataPriList);
                                    majorheadPriList.add(mha);
                                } else if (nowDedn.equals("I")) {
                                    mha.setVchIntList(vchdataIntList);
                                    majorheadIntList.add(mha);
                                }

                                mha = new MajorHeadAttribute();
                                mha.setMajorhead(majorhead);

                                vchdataPriList = new ArrayList();
                                vchdataIntList = new ArrayList();
                                printHeader(table, cell, f1, majorhead, bb.getTreasurycode(), loanName, nowDednLabel);
                            }

                            printSubHeader(innertable, innercell, f1, bb.getMajorhead() + "/" + bb.getVoucherno(), bb.getVoucherdate(), bb.getDdocode());

                            cell = new PdfPCell();
                            cell.setColspan(4);
                            cell.setFixedHeight(20);
                            cell.setBorder(Rectangle.NO_BORDER);
                            table.addCell(cell);

                            document.add(table);
                            int billrecovamt = 0;
                            AquitanceDataAGBean agbean = null;
                            for (int j = 0; j < empdatalist.size(); j++) {
                                agbean = (AquitanceDataAGBean) empdatalist.get(j);

                                innercell = new PdfPCell(new Phrase((j + 1) + "", f1));
                                //innercell.setBorder(Rectangle.NO_BORDER);
                                innertable.addCell(innercell);
                                innercell = new PdfPCell(new Phrase(agbean.getEmpname() + "\n" + agbean.getDesg(), f1));
                                //innercell.setBorder(Rectangle.NO_BORDER);
                                innertable.addCell(innercell);
                                innercell = new PdfPCell(new Phrase(agbean.getOrgAmount(), f1));
                                //innercell.setBorder(Rectangle.NO_BORDER);
                                innertable.addCell(innercell);
                                innercell = new PdfPCell(new Phrase(agbean.getRefCount(), f1));
                                //innercell.setBorder(Rectangle.NO_BORDER);
                                innertable.addCell(innercell);
                                innercell = new PdfPCell(new Phrase(agbean.getAdAmt(), f1));
                                billrecovamt = billrecovamt + Integer.parseInt(agbean.getAdAmt());
                                //innercell.setBorder(Rectangle.NO_BORDER);
                                innertable.addCell(innercell);
                                innercell = new PdfPCell(new Phrase(agbean.getTotRecAmt(), f1));
                                //innercell.setBorder(Rectangle.NO_BORDER);
                                innertable.addCell(innercell);
                                innercell = new PdfPCell(new Phrase(agbean.getBalance(), f1));
                                //innercell.setBorder(Rectangle.NO_BORDER);
                                innertable.addCell(innercell);
                                innercell = new PdfPCell(new Phrase(agbean.getGpfNo(), f1));
                                //innercell.setBorder(Rectangle.NO_BORDER);
                                innertable.addCell(innercell);
                                String nowDednforTable = "";
                                if (nowDedn.equals("P")) {
                                    nowDednforTable = "PRI";
                                    double pTemp = Double.parseDouble(agbean.getAdAmt());
                                    pTotAmt = pTotAmt + pTemp;
                                } else if (nowDedn.equals("I")) {
                                    nowDednforTable = "INT";
                                    double iTemp = Double.parseDouble(agbean.getAdAmt());
                                    iTotAmt = iTotAmt + iTemp;
                                }
                                innercell = new PdfPCell(new Phrase(nowDednforTable, f1));
                                //innercell.setBorder(Rectangle.NO_BORDER);
                                innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                innertable.addCell(innercell);
                                innercell = new PdfPCell(new Phrase(billmonth, f1));
                                //innercell.setBorder(Rectangle.NO_BORDER);
                                innertable.addCell(innercell);
                            }

                            innercell = new PdfPCell(new Phrase(" ", f1));
                            innercell.setBorder(Rectangle.NO_BORDER);
                            innertable.addCell(innercell);
                            innercell = new PdfPCell(new Phrase(" ", f1));
                            innercell.setBorder(Rectangle.NO_BORDER);
                            innertable.addCell(innercell);
                            innercell = new PdfPCell(new Phrase(" ", f1));
                            innercell.setBorder(Rectangle.NO_BORDER);
                            innertable.addCell(innercell);
                            innercell = new PdfPCell(new Phrase(" ", f1));
                            innercell.setBorder(Rectangle.NO_BORDER);
                            innertable.addCell(innercell);
                            innercell = new PdfPCell(new Phrase(billrecovamt + "", f1));
                            innercell.setBorder(Rectangle.NO_BORDER);
                            innertable.addCell(innercell);
                            innercell = new PdfPCell(new Phrase(" ", f1));
                            innercell.setBorder(Rectangle.NO_BORDER);
                            innertable.addCell(innercell);
                            innercell = new PdfPCell(new Phrase(" ", f1));
                            innercell.setBorder(Rectangle.NO_BORDER);
                            innertable.addCell(innercell);
                            innercell = new PdfPCell(new Phrase(" ", f1));
                            innercell.setBorder(Rectangle.NO_BORDER);
                            innertable.addCell(innercell);
                            innercell = new PdfPCell(new Phrase(" ", f1));
                            innercell.setBorder(Rectangle.NO_BORDER);
                            innertable.addCell(innercell);
                            innercell = new PdfPCell(new Phrase(" ", f1));
                            innercell.setBorder(Rectangle.NO_BORDER);
                            innertable.addCell(innercell);

                            if (nowDedn.equals("P")) {
                                SelectOption so = new SelectOption();
                                so.setLabel(bb.getVoucherno());
                                so.setValue(pTotAmt + "");
                                vchdataPriList.add(so);
                            } else if (nowDedn.equals("I")) {
                                SelectOption so = new SelectOption();
                                so.setLabel(bb.getVoucherno());
                                so.setValue(iTotAmt + "");
                                vchdataIntList.add(so);
                            }

                            pTotAmt = 0;
                            iTotAmt = 0;

                            innercell = new PdfPCell();
                            innercell.setColspan(10);
                            innercell.setFixedHeight(20);
                            innercell.setBorder(Rectangle.NO_BORDER);
                            innertable.addCell(innercell);
                        }
                        /*innercell = new PdfPCell();
                         innercell.setColspan(9);
                         innercell.setFixedHeight(20);
                         innercell.setBorder(Rectangle.NO_BORDER);
                         innertable.addCell(innercell);*/
                        if (empdatalist != null && empdatalist.size() > 0) {
                            document.add(innertable);
                        }
                    }

                    mha = new MajorHeadAttribute();
                    mha.setMajorhead(majorhead);
                    if (nowDedn.equals("P")) {
                        mha.setVchPriList(vchdataPriList);
                        majorheadPriList.add(mha);
                    } else if (nowDedn.equals("I")) {
                        mha.setVchIntList(vchdataIntList);
                        majorheadIntList.add(mha);
                    }

                    index++;
                }
            }

            if (empdatalist != null && empdatalist.size() > 0) {
                document.add(table);
                document.newPage();
            }
            System.out.println("majorheadPriList size is:============================================================== " + majorheadPriList.size());
            if (majorheadPriList != null && majorheadPriList.size() > 0) {

                table = new PdfPTable(2);
                table.setWidths(new int[]{3, 2});
                table.setWidthPercentage(70);

                if (schedule.equals("GA")) {
                    cell = new PdfPCell(new Phrase("SUMMARY", getDesired_PDF_Font(11, true, true)));
                    cell.setColspan(2);
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                } else {
                    cell = new PdfPCell(new Phrase("PRINCIPAL", getDesired_PDF_Font(11, true, true)));
                    cell.setColspan(2);
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                }
                cell = new PdfPCell();
                cell.setColspan(2);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setFixedHeight(10);
                table.addCell(cell);

                mha = null;
                for (int i = 0; i < majorheadPriList.size(); i++) {
                    mha = (MajorHeadAttribute) majorheadPriList.get(i);

                    cell = new PdfPCell(new Phrase("Major Head:" + StringUtils.repeat(" ", 50) + mha.getMajorhead(), getDesired_PDF_Font(11, true, false)));
                    cell.setColspan(2);
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                    innertable = new PdfPTable(2);
                    innertable.setWidths(new int[]{1, 1});
                    innertable.setWidthPercentage(70);

                    double tvPTot = 0;

                    for (int j = 0; j < mha.getVchPriList().size(); j++) {
                        SelectOption so = (SelectOption) mha.getVchPriList().get(j);

                        innercell = new PdfPCell(new Phrase(so.getLabel(), f1));
                        innercell.setBorder(Rectangle.NO_BORDER);
                        innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        innertable.addCell(innercell);
                        innercell = new PdfPCell(new Phrase(so.getValue(), f1));
                        innercell.setBorder(Rectangle.NO_BORDER);
                        innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        innertable.addCell(innercell);

                        double tvPTotTemp = Double.parseDouble(so.getValue());
                        tvPTot = tvPTot + tvPTotTemp;
                    }

                    innercell = new PdfPCell(new Phrase("Total", getDesired_PDF_Font(11, true, false)));
                    innercell.setBorder(Rectangle.NO_BORDER);
                    innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    innertable.addCell(innercell);
                    innercell = new PdfPCell(new Phrase(tvPTot + "", getDesired_PDF_Font(11, true, false)));
                    innercell.setBorder(Rectangle.NO_BORDER);
                    innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    innertable.addCell(innercell);

                    cell = new PdfPCell(innertable);
                    cell.setColspan(2);
                    cell.setBorder(Rectangle.NO_BORDER);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase(StringUtils.repeat("_", 52), f1));
                    cell.setColspan(2);
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                }
                document.add(table);
            }
            document.newPage();
            System.out.println("majorheadIntList size is: ================================" + majorheadIntList.size());
            if (majorheadIntList != null && majorheadIntList.size() > 0) {
                table = new PdfPTable(2);
                table.setWidths(new int[]{3, 2});
                table.setWidthPercentage(70);

                cell = new PdfPCell();
                cell.setColspan(2);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setFixedHeight(20);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("INTEREST", getDesired_PDF_Font(11, true, true)));
                cell.setColspan(2);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell();
                cell.setColspan(2);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setFixedHeight(10);
                table.addCell(cell);

                mha = null;
                for (int i = 0; i < majorheadIntList.size(); i++) {
                    mha = (MajorHeadAttribute) majorheadIntList.get(i);

                    cell = new PdfPCell(new Phrase("Major Head:" + StringUtils.repeat(" ", 50) + mha.getMajorhead(), getDesired_PDF_Font(11, true, false)));
                    cell.setColspan(2);
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                    innertable = new PdfPTable(2);
                    innertable.setWidths(new int[]{1, 1});
                    innertable.setWidthPercentage(70);

                    double tvPTot = 0;

                    for (int j = 0; j < mha.getVchIntList().size(); j++) {
                        SelectOption so = (SelectOption) mha.getVchIntList().get(j);

                        innercell = new PdfPCell(new Phrase(so.getLabel(), f1));
                        innercell.setBorder(Rectangle.NO_BORDER);
                        innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        innertable.addCell(innercell);
                        innercell = new PdfPCell(new Phrase(so.getValue(), f1));
                        innercell.setBorder(Rectangle.NO_BORDER);
                        innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        innertable.addCell(innercell);

                        double tvPTotTemp = Double.parseDouble(so.getValue());
                        tvPTot = tvPTot + tvPTotTemp;
                    }

                    innercell = new PdfPCell(new Phrase("Total", getDesired_PDF_Font(11, true, false)));
                    innercell.setBorder(Rectangle.NO_BORDER);
                    innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    innertable.addCell(innercell);
                    innercell = new PdfPCell(new Phrase(tvPTot + "", getDesired_PDF_Font(11, true, false)));
                    innercell.setBorder(Rectangle.NO_BORDER);
                    innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    innertable.addCell(innercell);

                    cell = new PdfPCell(innertable);
                    cell.setColspan(2);
                    cell.setBorder(Rectangle.NO_BORDER);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase(StringUtils.repeat("_", 52), f1));
                    cell.setColspan(2);
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                }
                document.add(table);
            }

            //document.add(table);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    private List getEmployeeAquitanceDetailsForAG(String billNo, int year, int month, String adcode, String nowDedn) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        List datalist = new ArrayList();
        AquitanceDataAGBean agbean = null;

        String sql = "";
        String adCodeStr = "";
        try {
            con = this.dataSource.getConnection();
            //con =  this.getDBConnection();
            String aqdtlsTable = hrms.common.AqFunctionalities.getAQBillDtlsTable(month, year);

            if (!adcode.equals("GIS") && !adcode.equals("CGEGIS")) {
                if (adcode.equals("MCA")) {
                    adCodeStr = "(ad_code='MCA' or ad_code='VE' or ad_code='MOPA')";
                } else if (adcode.equals("GA")) {
                    adCodeStr = "(ad_code='GPF' or ad_code='GA')";
                } else {
                    adCodeStr = "ad_code='" + adcode + "'";
                }
                //System.out.println("adCode is: "+adcode);
                //System.out.println("adCodeStr is: "+adCodeStr);
                if (adcode.equals("GA")) {
                    sql = "select aq_mast.emp_code,emp_name,cur_desg,p_org_amt,ref_desc,ref_count,ded_type,getgpfmonthlysubscription(aq_mast.aqsl_no) gpfSubAmt,ad_amt gaAmt,tot_rec_amt,(p_org_amt - tot_rec_amt) balance,gpf_acc_no from aq_mast"
                            + " inner join " + aqdtlsTable + " aq_dtls on aq_mast.aqsl_no=aq_dtls.aqsl_no"
                            + " left outer join emp_loan_sanc on aq_dtls.ad_ref_id=emp_loan_sanc.loanid where aq_mast.bill_no=? and aq_mast.aq_year=? and aq_mast.aq_month=? and ad_code='GA'";
                    pst = con.prepareStatement(sql);
                    pst.setInt(1, Integer.parseInt(billNo));
                    pst.setInt(2, year);
                    pst.setInt(3, month);
                } else {
                    /*System.out.println("select aq_mast.emp_code,emp_name,cur_desg,p_org_amt,ref_desc,ref_count,ad_amt,tot_rec_amt,(p_org_amt - tot_rec_amt) balance,gpf_acc_no from aq_mast"
                            + " inner join " + aqdtlsTable + " aq_dtls on aq_mast.aqsl_no=aq_dtls.aqsl_no"
                            + " left outer join emp_loan_sanc on aq_dtls.ad_ref_id=emp_loan_sanc.loanid where aq_mast.bill_no="+billNo+" and aq_mast.aq_year="+year+" and aq_mast.aq_month="+month+" and " + adCodeStr + " and aq_dtls.now_dedn='"+nowDedn+"' and ad_amt > 0");*/
                    sql = "select aq_mast.emp_code,emp_name,cur_desg,p_org_amt,ref_desc,ref_count,ad_amt,tot_rec_amt,(p_org_amt - tot_rec_amt) balance,gpf_acc_no from aq_mast"
                            + " inner join " + aqdtlsTable + " aq_dtls on aq_mast.aqsl_no=aq_dtls.aqsl_no"
                            + " left outer join emp_loan_sanc on aq_dtls.ad_ref_id=emp_loan_sanc.loanid where aq_mast.bill_no=? and aq_mast.aq_year=? and aq_mast.aq_month=? and " + adCodeStr + " and aq_dtls.now_dedn=? and ad_amt > 0";
                    pst = con.prepareStatement(sql);
                    pst.setInt(1, Integer.parseInt(billNo));
                    pst.setInt(2, year);
                    pst.setInt(3, month);
                    //pst.setString(4, adcode);
                    pst.setString(4, nowDedn);
                }
            } else if (adcode.equals("GIS")) {
                adCodeStr = "ad_code='" + adcode + "'";
                /*sql = "select aq_mast.emp_code,emp_name,cur_desg,p_org_amt,ref_desc,ref_count,ad_amt,tot_rec_amt,(p_org_amt - tot_rec_amt) balance,gpf_acc_no from aq_mast"
                 + " inner join aq_dtls on aq_mast.aqsl_no=aq_dtls.aqsl_no"
                 + " left outer join emp_loan_sanc on aq_dtls.ad_ref_id=emp_loan_sanc.loanid where aq_mast.bill_no=? and aq_mast.aq_year=? and aq_mast.aq_month=? and " + adCodeStr + " and ad_amt > 0";*/
                sql = "select aq_mast.emp_code,emp_name,cur_desg,p_org_amt,ref_desc,ref_count,ad_amt,tot_rec_amt,(p_org_amt - tot_rec_amt) balance,gpf_acc_no from aq_mast"
                        + " inner join " + aqdtlsTable + " aq_dtls on aq_mast.aqsl_no=aq_dtls.aqsl_no"
                        + " inner join emp_mast on aq_mast.emp_code=emp_mast.emp_id"
                        + " inner join g_cadre on emp_mast.cur_cadre_code=g_cadre.cadre_code"
                        + " left outer join emp_loan_sanc on aq_dtls.ad_ref_id=emp_loan_sanc.loanid where aq_mast.bill_no=? and aq_mast.aq_year=? and aq_mast.aq_month=? and " + adCodeStr + " and ad_amt > 0 and (cadre_type='AIS' or cadre_type='IRS')";
                pst = con.prepareStatement(sql);
                pst.setInt(1, Integer.parseInt(billNo));
                pst.setInt(2, year);
                pst.setInt(3, month);
                //pst.setString(4, adcode);
            } else if (adcode.equals("CGEGIS")) {
                adCodeStr = "ad_code='GIS'";
                /*sql = "select aq_mast.emp_code,emp_name,cur_desg,p_org_amt,ref_desc,ref_count,ad_amt,tot_rec_amt,(p_org_amt - tot_rec_amt) balance,gpf_acc_no from aq_mast"
                        + " inner join " + aqdtlsTable + " aq_dtls on aq_mast.aqsl_no=aq_dtls.aqsl_no"
                        + " left outer join emp_loan_sanc on aq_dtls.ad_ref_id=emp_loan_sanc.loanid where aq_mast.bill_no=? and aq_mast.aq_year=? and aq_mast.aq_month=? and " + adCodeStr + " and ad_amt > 0 and (gpf_type='DAO' or cadre_type='DAO')";*/
                sql = "select aq_mast.emp_code,emp_name,cur_desg,p_org_amt,ref_desc,ref_count,ad_amt,tot_rec_amt,(p_org_amt - tot_rec_amt) balance,gpf_acc_no from aq_mast"
                        + " inner join " + aqdtlsTable + " aq_dtls on aq_mast.aqsl_no=aq_dtls.aqsl_no"
                        + " left outer join emp_loan_sanc on aq_dtls.ad_ref_id=emp_loan_sanc.loanid"
                        + " inner join emp_mast on aq_mast.emp_code=emp_mast.emp_id"
                        + " left outer join g_cadre on emp_mast.cur_cadre_code=g_cadre.cadre_code"
                        + " where aq_mast.bill_no=? and aq_mast.aq_year=? and aq_mast.aq_month=? and " + adCodeStr + " and ad_amt > 0 and (gpf_type='DAO' or cadre_type='DAO')";
                pst = con.prepareStatement(sql);
                pst.setInt(1, Integer.parseInt(billNo));
                pst.setInt(2, year);
                pst.setInt(3, month);
                //pst.setString(4, adcode);
            }
            //System.out.println("LTA PDF Query is: " + sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                agbean = new AquitanceDataAGBean();
                agbean.setEmpid(rs.getString("emp_code"));
                agbean.setGpfNo(rs.getString("gpf_acc_no"));
                agbean.setEmpname(rs.getString("emp_name"));
                agbean.setDesg(rs.getString("cur_desg"));
                agbean.setOrgAmount(rs.getInt("p_org_amt") + "");
                if (rs.getString("ref_desc") != null && !rs.getString("ref_desc").equals("")) {
                    String[] refdesc = rs.getString("ref_desc").split("/");
                    //agbean.setRefCount(refdesc[0]);
                    agbean.setRefCount(rs.getString("ref_desc"));
                } else {
                    agbean.setRefCount(rs.getString("ref_count"));
                }
                agbean.setAdAmt(rs.getInt("ad_amt") + "");
                agbean.setTotRecAmt(rs.getInt("tot_rec_amt") + "");
                agbean.setBalance(rs.getInt("balance") + "");
                datalist.add(agbean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return datalist;
    }

    /*private List getEmployeeGPFAquitanceDetailsForAG(String billNo, int year, int month, String adcode, String nowDedn) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        List datalist = new ArrayList();
        AquitanceDataAGBean agbean = null;

        String sql = "";
        String adCodeStr = "";
        try {
            con = this.dataSource.getConnection();
            //con =  this.getDBConnection();
            String aqdtlsTable = hrms.common.AqFunctionalities.getAQBillDtlsTable(month, year);

            if (adcode.equals("GA")) {
                adCodeStr = "(ad_code='GPF' or ad_code='GA')";
            }
            //System.out.println("adCode is: "+adcode);
            //System.out.println("adCodeStr is: "+adCodeStr);
            if (adcode.equals("GA")) {
                sql = "select aq_mast.emp_code,emp_name,cur_desg,p_org_amt,ref_desc,ref_count,ded_type,getgpfmonthlysubscription(aq_mast.aqsl_no,'" + aqdtlsTable + "') gpfSubAmt,ad_amt gaAmt,tot_rec_amt,(p_org_amt - tot_rec_amt) balance,gpf_acc_no from aq_mast"
                        + " inner join " + aqdtlsTable + " aq_dtls on aq_mast.aqsl_no=aq_dtls.aqsl_no"
                        + " left outer join emp_loan_sanc on aq_dtls.ad_ref_id=emp_loan_sanc.loanid where aq_mast.bill_no=? and aq_mast.aq_year=? and aq_mast.aq_month=? and "+adCodeStr;
                pst = con.prepareStatement(sql);
                pst.setInt(1, Integer.parseInt(billNo));
                pst.setInt(2, year);
                pst.setInt(3, month);
            }

            //System.out.println("LTA PDF Query is: " + sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                agbean = new AquitanceDataAGBean();
                agbean.setEmpid(rs.getString("emp_code"));
                agbean.setGpfNo(rs.getString("gpf_acc_no"));
                agbean.setEmpname(rs.getString("emp_name"));
                agbean.setDesg(rs.getString("cur_desg"));

                int gpfSubAmt = rs.getInt("gpfSubAmt");
                int gaAmt = rs.getInt("gaAmt");
                int totalAmt = gpfSubAmt + gaAmt;
                agbean.setGpfSubscriptionAmt(gpfSubAmt + "");
                agbean.setGaAmt(gaAmt + "");

                if (gaAmt > 0) {
                    agbean.setOrgAmount(rs.getString("p_org_amt"));
                    if (rs.getString("ref_desc") != null && !rs.getString("ref_desc").equals("")) {
                        String[] refdesc = rs.getString("ref_desc").split("/");
                        //agbean.setRefCount(refdesc[0]);
                        agbean.setRefCount(rs.getString("ref_desc"));
                    } else {
                        agbean.setRefCount(rs.getString("ref_count"));
                    }

                    agbean.setTotRecAmt(rs.getInt("tot_rec_amt") + "");
                    agbean.setBalance(rs.getInt("balance") + "");
                } else {
                    agbean.setOrgAmount("0");
                    agbean.setTotRecAmt("0");
                    agbean.setBalance("0");
                    agbean.setRefCount("0");
                }
                //agbean.setAdAmt(rs.getString("ad_amt"));
                agbean.setAdAmt(totalAmt + "");
                if (totalAmt > 0) {
                    datalist.add(agbean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return datalist;
    }*/
    
    private List getEmployeeGPFAquitanceDetailsForAG(String billNo, int year, int month, String adcode, String nowDedn) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        PreparedStatement pst1 = null;
        ResultSet rs1 = null;

        PreparedStatement pst2 = null;
        ResultSet rs2 = null;

        List datalist = new ArrayList();
        AquitanceDataAGBean agbean = null;

        String sql = "";
        String adCodeStr = "";
        try {
            con = this.dataSource.getConnection();
            //con =  this.getDBConnection();
            String aqdtlsTable = hrms.common.AqFunctionalities.getAQBillDtlsTable(month, year);

            if (adcode.equals("GA")) {
                adCodeStr = "(ad_code='GPF' or ad_code='GA')";
            }
            if (adcode.equals("GA")) {
                sql = "select aqsl_no,aq_mast.emp_code,emp_name,cur_desg,gpf_acc_no from aq_mast"
                        + " where aq_mast.bill_no=? and aq_mast.aq_year=? and aq_mast.aq_month=? order by emp_name";
                pst = con.prepareStatement(sql);
                pst.setInt(1, Integer.parseInt(billNo));
                pst.setInt(2, year);
                pst.setInt(3, month);
            }

            rs = pst.executeQuery();
            while (rs.next()) {
                agbean = new AquitanceDataAGBean();

                agbean.setEmpid(rs.getString("emp_code"));
                agbean.setGpfNo(rs.getString("gpf_acc_no"));
                agbean.setEmpname(rs.getString("emp_name"));
                agbean.setDesg(rs.getString("cur_desg"));

                int gpfSubAmt = 0;
                int gaAmt = 0;

                if (rs.getString("aqsl_no") != null && !rs.getString("aqsl_no").equals("")) {
                    String empAqslNo = rs.getString("aqsl_no");

                    String Sql1 = "SELECT AD_AMT FROM " + aqdtlsTable + " WHERE AD_TYPE = 'D' AND AQSL_NO= '" + empAqslNo + "' AND DED_TYPE = 'S' and ad_code ='GPF'";
                    pst1 = con.prepareStatement(Sql1);
                    rs1 = pst1.executeQuery();
                    if (rs1.next()) {
                        gpfSubAmt = rs1.getInt("AD_AMT");
                    }

                    String Sql2 = "SELECT p_org_amt,AD_AMT,ref_desc,tot_rec_amt,ref_count,(p_org_amt - tot_rec_amt) balance FROM AQ_DTLS" +
                                 " left outer join emp_loan_sanc on aq_dtls.ad_ref_id=emp_loan_sanc.loanid" +
                                 " WHERE AD_TYPE = 'D' AND DED_TYPE = 'L' and ad_code = 'GA' AND AQSL_NO = '" + empAqslNo + "'";
                    pst2 = con.prepareStatement(Sql2);
                    rs2 = pst2.executeQuery();
                    if (rs2.next()) {
                        gaAmt = rs2.getInt("AD_AMT");
                        if (gaAmt > 0) {
                            agbean.setOrgAmount(rs2.getString("p_org_amt"));
                            if (rs2.getString("ref_desc") != null && !rs2.getString("ref_desc").equals("")) {
                                String[] refdesc = rs2.getString("ref_desc").split("/");
                                //agbean.setRefCount(refdesc[0]);
                                agbean.setRefCount(rs2.getString("ref_desc"));
                            } else {
                                agbean.setRefCount(rs2.getString("ref_count"));
                            }

                            agbean.setTotRecAmt(rs2.getInt("tot_rec_amt") + "");
                            agbean.setBalance(rs2.getInt("balance") + "");
                        } else {
                            agbean.setOrgAmount("0");
                            agbean.setTotRecAmt("0");
                            agbean.setBalance("0");
                            agbean.setRefCount("0");
                        }
                        
                    }
                }

                int totalAmt = gpfSubAmt + gaAmt;
                agbean.setGpfSubscriptionAmt(gpfSubAmt + "");
                agbean.setGaAmt(gaAmt + "");
               
                agbean.setAdAmt(totalAmt + "");
                if (totalAmt > 0) {
                    datalist.add(agbean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return datalist;
    }

    private void printHeader(PdfPTable table, PdfPCell cell, Font f1, String majorhead, String treasuryCode, String loanName, String nowDedn) {

        try {

            cell = new PdfPCell(new Phrase("MAJOR HEAD", f1));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("TREASURY CODE", f1));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("LOAN TYPE", f1));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("DEDUCTION TYPE", f1));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(majorhead, f1));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(treasuryCode, f1));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(loanName, f1));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(nowDedn, f1));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printSubHeader(PdfPTable table, PdfPCell cell, Font f1, String vchNo, String vchDate, String ddocode) {

        try {
            cell = new PdfPCell(new Phrase("TV No: " + vchNo, f1));
            cell.setColspan(2);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("TV Date: " + vchDate, f1));
            cell.setColspan(3);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("DDO Code: " + ddocode, f1));
            cell.setColspan(5);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Sl No", f1));
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Name and Designation", f1));
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Amt of Org. Adv.", f1));
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("No of Instl. of Rec.", f1));
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Amt Deducted in the Bill", f1));
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Rec. up to the Month", f1));
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Bal Out-\nstanding", f1));
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Loanee Id", f1));
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("PRI/\nINT", f1));
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Salary Month", f1));
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printGISHeader(PdfPTable table, PdfPCell cell, Font f1, String majorhead, String treasuryCode, String loanName, String nowDedn) {

        try {

            cell = new PdfPCell(new Phrase("MAJOR HEAD", f1));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("TREASURY CODE", f1));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("SUBSCRIPTION TYPE", f1));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell();
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(majorhead, f1));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(treasuryCode, f1));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(loanName, f1));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell();
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printGISSubHeader(PdfPTable table, PdfPCell cell, Font f1, String vchNo, String vchDate, String ddocode,String ddoname) {

        try {
            cell = new PdfPCell(new Phrase("TV No: " + vchNo + "     TV Date: " + vchDate, f1));
            cell.setColspan(2);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("DDO Code: " + ddocode + "("+ddoname+")", f1));
            cell.setColspan(3);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Sl No", f1));
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Name and Designation", f1));
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Amt Deducted in the Bill", f1));
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Subscriber Id", f1));
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Salary Month", f1));
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void generateGPFSchedulePDFReportsForAG(Document document, String schedule, ArrayList billList) {

        String majorhead = "";

        String loanName = "";

        String nowDedn = "";
        String[] nowDednArr = {"P"};
        String nowDednLabel = "Principal";

        ArrayList majorheadPriList = new ArrayList();
        ArrayList majorheadIntList = new ArrayList();

        MajorHeadAttribute mha = null;
        ArrayList vchdataPriList = new ArrayList();
        ArrayList vchdataIntList = new ArrayList();

        List empdatalist = null;

        try {
            Font f1 = new Font();
            f1.setSize(10);
            f1.setFamily("Times New Roman");

            PdfPTable table = null;
            PdfPTable innertable = null;

            PdfPCell cell = null;
            PdfPCell innercell = null;

            if (schedule.equals("GA")) {
                loanName = "GPF";
            }

            double pTotAmt = 0;
            double iTotAmt = 0;
            /*if (nowDedn.equals("I")) {
             nowDednLabel = "Interest";
             }*/
            int index = 0;

            if (billList != null && billList.size() > 0) {

                nowDedn = nowDednArr[index];

                for (int i = 0; i < billList.size(); i++) {
                    //for (int i = 0; i < 1; i++) {
                    BillBean bb = (BillBean) billList.get(i);
                    //System.out.println("Major Head is: " + bb.getMajorhead());

                    innertable = new PdfPTable(11);
                    //innertable.setWidths(new float[]{0.5f, 3, 1.2f, 1.1f, 0.7f, 1.1f, 1, 1.2f, 1.5f, 0.9f, 1});
                    innertable.setWidths(new float[]{0.7f, 2.6f, 1.7f, 1.1f, 1, 1.1f, 1, 1.2f, 1.1f, 1.2f, 1});
                    innertable.setWidthPercentage(100);

                    String billmonth = "";
                    String billperiod = "";
                    //empdatalist = getEmployeeAquitanceDetailsForAG(bb.getBillno(), bb.getBillYear(), bb.getBillMonth(), schedule, nowDedn);
                    if (bb.getBilltype().equals("PAY")) {
                        empdatalist = getEmployeeGPFAquitanceDetailsForAG(bb.getBillno(), bb.getBillYear(), bb.getBillMonth(), schedule, nowDedn);
                        billmonth = (bb.getBillMonth() + 1) + "-" + bb.getBillYear();
                    } else if (bb.getBilltype().contains("ARREAR")) {
                        empdatalist = getEmployeeGPFArrearAquitanceDetailsForAG(bb.getBillno(), bb.getBillYear(), bb.getBillMonth(), schedule);
                        billperiod = getArrearBillPeriod(bb.getBillno());
                    }
                    if (empdatalist != null && empdatalist.size() > 0) {
                        //System.out.println("empdatalist size is: " + empdatalist.size());
                        table = new PdfPTable(4);
                        table.setWidths(new int[]{2, 2, 2, 2});
                        table.setWidthPercentage(100);
                        if (majorhead.equals("")) {
                            majorhead = bb.getMajorhead();

                            mha = new MajorHeadAttribute();
                            mha.setMajorhead(majorhead);
                            if (bb.getBilltype().equals("PAY")) {
                                printGPFHeader(table, cell, f1, majorhead, bb.getTreasurycode(), loanName, nowDednLabel);
                            } else if (bb.getBilltype().contains("ARREAR")) {
                                printGPFArrearHeader(table, cell, f1, majorhead, bb.getTreasurycode(), loanName, nowDednLabel);
                            }
                        }
                        if (!majorhead.equals(bb.getMajorhead())) {
                            majorhead = bb.getMajorhead();

                            if (nowDedn.equals("P")) {
                                mha.setVchPriList(vchdataPriList);
                                majorheadPriList.add(mha);
                            } else if (nowDedn.equals("I")) {
                                mha.setVchIntList(vchdataIntList);
                                majorheadIntList.add(mha);
                            }

                            mha = new MajorHeadAttribute();
                            mha.setMajorhead(majorhead);

                            vchdataPriList = new ArrayList();
                            vchdataIntList = new ArrayList();
                            if (bb.getBilltype().equals("PAY")) {
                                printGPFHeader(table, cell, f1, majorhead, bb.getTreasurycode(), loanName, nowDednLabel);
                            } else if (bb.getBilltype().contains("ARREAR")) {
                                printGPFArrearHeader(table, cell, f1, majorhead, bb.getTreasurycode(), loanName, nowDednLabel);
                            }
                        }
                        if (bb.getBilltype().equals("PAY")) {
                            printGPFSubHeader(innertable, innercell, f1, bb.getMajorhead() + "/" + bb.getVoucherno(), bb.getVoucherdate(), bb.getDdocode());
                        } else if (bb.getBilltype().contains("ARREAR")) {
                            printGPFArrearSubHeader(innertable, innercell, f1, bb.getMajorhead() + "/" + bb.getVoucherno(), bb.getVoucherdate(), bb.getDdocode());
                        }
                        cell = new PdfPCell();
                        cell.setColspan(4);
                        cell.setFixedHeight(20);
                        cell.setBorder(Rectangle.NO_BORDER);
                        table.addCell(cell);

                        document.add(table);
                        int billrecovamt = 0;
                        AquitanceDataAGBean agbean = null;
                        for (int j = 0; j < empdatalist.size(); j++) {
                            agbean = (AquitanceDataAGBean) empdatalist.get(j);

                            innercell = new PdfPCell(new Phrase((j + 1) + "", f1));
                            //innercell.setBorder(Rectangle.NO_BORDER);
                            innertable.addCell(innercell);
                            innercell = new PdfPCell(new Phrase(agbean.getEmpname() + "\n" + agbean.getDesg(), f1));
                            //innercell.setBorder(Rectangle.NO_BORDER);
                            innertable.addCell(innercell);
                            innercell = new PdfPCell(new Phrase(agbean.getGpfNo(), f1));
                            //innercell.setBorder(Rectangle.NO_BORDER);
                            innertable.addCell(innercell);
                            innercell = new PdfPCell(new Phrase(agbean.getGpfSubscriptionAmt(), f1));
                            //innercell.setBorder(Rectangle.NO_BORDER);
                            innertable.addCell(innercell);
                            innercell = new PdfPCell(new Phrase(agbean.getGaAmt(), f1));
                            //innercell.setBorder(Rectangle.NO_BORDER);
                            innertable.addCell(innercell);
                            innercell = new PdfPCell(new Phrase(agbean.getAdAmt(), f1));
                            billrecovamt = billrecovamt + Integer.parseInt(agbean.getAdAmt());
                            //innercell.setBorder(Rectangle.NO_BORDER);
                            innertable.addCell(innercell);
                            innercell = new PdfPCell(new Phrase(agbean.getRefCount(), f1));
                            //innercell.setBorder(Rectangle.NO_BORDER);
                            innertable.addCell(innercell);
                            innercell = new PdfPCell(new Phrase(agbean.getOrgAmount(), f1));
                            //innercell.setBorder(Rectangle.NO_BORDER);
                            innertable.addCell(innercell);
                            innercell = new PdfPCell(new Phrase(agbean.getTotRecAmt(), f1));
                            //innercell.setBorder(Rectangle.NO_BORDER);
                            innertable.addCell(innercell);
                            innercell = new PdfPCell(new Phrase(agbean.getBalance(), f1));
                            //innercell.setBorder(Rectangle.NO_BORDER);
                            innertable.addCell(innercell);
                            if (bb.getBilltype().equals("PAY")) {
                                innercell = new PdfPCell(new Phrase(billmonth, f1));
                                //innercell.setBorder(Rectangle.NO_BORDER);
                                innertable.addCell(innercell);
                            } else if (bb.getBilltype().contains("ARREAR")) {
                                innercell = new PdfPCell(new Phrase(billperiod, f1));
                                //innercell.setBorder(Rectangle.NO_BORDER);
                                innertable.addCell(innercell);
                            }
                            double pTemp = Double.parseDouble(agbean.getAdAmt());
                            pTotAmt = pTotAmt + pTemp;
                        }

                        innercell = new PdfPCell(new Phrase(" ", f1));
                        innercell.setBorder(Rectangle.NO_BORDER);
                        innertable.addCell(innercell);
                        innercell = new PdfPCell(new Phrase(" ", f1));
                        innercell.setBorder(Rectangle.NO_BORDER);
                        innertable.addCell(innercell);
                        innercell = new PdfPCell(new Phrase(" ", f1));
                        innercell.setBorder(Rectangle.NO_BORDER);
                        innertable.addCell(innercell);
                        innercell = new PdfPCell(new Phrase(" ", f1));
                        innercell.setBorder(Rectangle.NO_BORDER);
                        innertable.addCell(innercell);
                        innercell = new PdfPCell(new Phrase(" ", f1));
                        innercell.setBorder(Rectangle.NO_BORDER);
                        innertable.addCell(innercell);
                        innercell = new PdfPCell(new Phrase(billrecovamt + "", f1));
                        innercell.setColspan(2);
                        innercell.setBorder(Rectangle.NO_BORDER);
                        innertable.addCell(innercell);
                        innercell = new PdfPCell(new Phrase(" ", f1));
                        innercell.setBorder(Rectangle.NO_BORDER);
                        innertable.addCell(innercell);
                        innercell = new PdfPCell(new Phrase(" ", f1));
                        innercell.setBorder(Rectangle.NO_BORDER);
                        innertable.addCell(innercell);
                        innercell = new PdfPCell(new Phrase(" ", f1));
                        innercell.setBorder(Rectangle.NO_BORDER);
                        innertable.addCell(innercell);
                        innercell = new PdfPCell(new Phrase(" ", f1));
                        innercell.setBorder(Rectangle.NO_BORDER);
                        innertable.addCell(innercell);

                        if (nowDedn.equals("P")) {
                            SelectOption so = new SelectOption();
                            so.setLabel(bb.getVoucherno());
                            so.setValue(pTotAmt + "");
                            vchdataPriList.add(so);
                        } else if (nowDedn.equals("I")) {
                            SelectOption so = new SelectOption();
                            so.setLabel(bb.getVoucherno());
                            so.setValue(iTotAmt + "");
                            vchdataIntList.add(so);
                        }

                        pTotAmt = 0;
                        iTotAmt = 0;

                        innercell = new PdfPCell();
                        innercell.setColspan(11);
                        innercell.setFixedHeight(20);
                        innercell.setBorder(Rectangle.NO_BORDER);
                        innertable.addCell(innercell);
                    }
                    /*innercell = new PdfPCell();
                     innercell.setColspan(9);
                     innercell.setFixedHeight(20);
                     innercell.setBorder(Rectangle.NO_BORDER);
                     innertable.addCell(innercell);*/
                    if (empdatalist != null && empdatalist.size() > 0) {
                        document.add(innertable);
                    }
                }

                mha = new MajorHeadAttribute();
                mha.setMajorhead(majorhead);
                if (nowDedn.equals("P")) {
                    mha.setVchPriList(vchdataPriList);
                    majorheadPriList.add(mha);
                } else if (nowDedn.equals("I")) {
                    mha.setVchIntList(vchdataIntList);
                    majorheadIntList.add(mha);
                }

                index++;

            }

            if (empdatalist != null && empdatalist.size() > 0) {
                document.add(table);
                document.newPage();
            }
            System.out.println("majorheadPriList size is:============================================================== " + majorheadPriList.size());
            if (majorheadPriList != null && majorheadPriList.size() > 0) {

                table = new PdfPTable(2);
                table.setWidths(new int[]{3, 2});
                table.setWidthPercentage(70);

                if (schedule.equals("GA")) {
                    cell = new PdfPCell(new Phrase("SUMMARY", getDesired_PDF_Font(11, true, true)));
                    cell.setColspan(2);
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                } else {
                    cell = new PdfPCell(new Phrase("PRINCIPAL", getDesired_PDF_Font(11, true, true)));
                    cell.setColspan(2);
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                }
                cell = new PdfPCell();
                cell.setColspan(2);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setFixedHeight(10);
                table.addCell(cell);

                mha = null;
                for (int i = 0; i < majorheadPriList.size(); i++) {
                    mha = (MajorHeadAttribute) majorheadPriList.get(i);

                    cell = new PdfPCell(new Phrase("Major Head:" + StringUtils.repeat(" ", 50) + mha.getMajorhead(), getDesired_PDF_Font(11, true, false)));
                    cell.setColspan(2);
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                    innertable = new PdfPTable(2);
                    innertable.setWidths(new int[]{1, 1});
                    innertable.setWidthPercentage(70);

                    double tvPTot = 0;

                    for (int j = 0; j < mha.getVchPriList().size(); j++) {
                        SelectOption so = (SelectOption) mha.getVchPriList().get(j);

                        innercell = new PdfPCell(new Phrase(so.getLabel(), f1));
                        innercell.setBorder(Rectangle.NO_BORDER);
                        innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        innertable.addCell(innercell);
                        innercell = new PdfPCell(new Phrase(so.getValue(), f1));
                        innercell.setBorder(Rectangle.NO_BORDER);
                        innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        innertable.addCell(innercell);

                        double tvPTotTemp = Double.parseDouble(so.getValue());
                        tvPTot = tvPTot + tvPTotTemp;
                    }

                    innercell = new PdfPCell(new Phrase("Total", getDesired_PDF_Font(11, true, false)));
                    innercell.setBorder(Rectangle.NO_BORDER);
                    innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    innertable.addCell(innercell);
                    innercell = new PdfPCell(new Phrase(tvPTot + "", getDesired_PDF_Font(11, true, false)));
                    innercell.setBorder(Rectangle.NO_BORDER);
                    innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    innertable.addCell(innercell);

                    cell = new PdfPCell(innertable);
                    cell.setColspan(2);
                    cell.setBorder(Rectangle.NO_BORDER);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase(StringUtils.repeat("_", 52), f1));
                    cell.setColspan(2);
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                }
                document.add(table);
            }
            document.newPage();
            System.out.println("majorheadIntList size is: ================================" + majorheadIntList.size());
            if (majorheadIntList != null && majorheadIntList.size() > 0) {
                table = new PdfPTable(2);
                table.setWidths(new int[]{3, 2});
                table.setWidthPercentage(70);

                cell = new PdfPCell();
                cell.setColspan(2);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setFixedHeight(20);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("INTEREST", getDesired_PDF_Font(11, true, true)));
                cell.setColspan(2);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell();
                cell.setColspan(2);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setFixedHeight(10);
                table.addCell(cell);

                mha = null;
                for (int i = 0; i < majorheadIntList.size(); i++) {
                    mha = (MajorHeadAttribute) majorheadIntList.get(i);

                    cell = new PdfPCell(new Phrase("Major Head:" + StringUtils.repeat(" ", 50) + mha.getMajorhead(), getDesired_PDF_Font(11, true, false)));
                    cell.setColspan(2);
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                    innertable = new PdfPTable(2);
                    innertable.setWidths(new int[]{1, 1});
                    innertable.setWidthPercentage(70);

                    double tvPTot = 0;

                    for (int j = 0; j < mha.getVchIntList().size(); j++) {
                        SelectOption so = (SelectOption) mha.getVchIntList().get(j);

                        innercell = new PdfPCell(new Phrase(so.getLabel(), f1));
                        innercell.setBorder(Rectangle.NO_BORDER);
                        innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        innertable.addCell(innercell);
                        innercell = new PdfPCell(new Phrase(so.getValue(), f1));
                        innercell.setBorder(Rectangle.NO_BORDER);
                        innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        innertable.addCell(innercell);

                        double tvPTotTemp = Double.parseDouble(so.getValue());
                        tvPTot = tvPTot + tvPTotTemp;
                    }

                    innercell = new PdfPCell(new Phrase("Total", getDesired_PDF_Font(11, true, false)));
                    innercell.setBorder(Rectangle.NO_BORDER);
                    innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    innertable.addCell(innercell);
                    innercell = new PdfPCell(new Phrase(tvPTot + "", getDesired_PDF_Font(11, true, false)));
                    innercell.setBorder(Rectangle.NO_BORDER);
                    innercell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    innertable.addCell(innercell);

                    cell = new PdfPCell(innertable);
                    cell.setColspan(2);
                    cell.setBorder(Rectangle.NO_BORDER);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase(StringUtils.repeat("_", 52), f1));
                    cell.setColspan(2);
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                }
                document.add(table);
            }

            //document.add(table);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    private void printGPFHeader(PdfPTable table, PdfPCell cell, Font f1, String majorhead, String treasuryCode, String loanName, String nowDedn) {

        try {

            cell = new PdfPCell(new Phrase("SALARY HEAD", f1));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("TREASURY CODE", f1));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("RECOVERY", f1));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell();
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(majorhead, f1));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(treasuryCode, f1));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(loanName, f1));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell();
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printGPFSubHeader(PdfPTable table, PdfPCell cell, Font f1, String vchNo, String vchDate, String ddocode) {

        try {
            cell = new PdfPCell(new Phrase("TV No: " + vchNo, f1));
            cell.setColspan(2);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("TV Date: " + vchDate, f1));
            cell.setColspan(3);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("DDO Code: " + ddocode, f1));
            cell.setColspan(6);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Sl No", f1));
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Name and Designation", f1));
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("GPF No", f1));
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Sub-\nscription\nAmt", f1));
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Refund\nof\nAdv", f1));
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Amt Deducted in the Bill", f1));
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("No of Instl. of Rec.", f1));
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Amt of Org. Adv.", f1));
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Rec. up to\nthe Month", f1));
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Bal Out-\nstanding", f1));
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Salary Month", f1));
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Connection getDBConnection() {
        Connection con = null;

        try {
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection("jdbc:postgresql://172.16.1.16/hrmis", "hrmis2", "cmgi");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }

    private List getEmployeeGPFArrearAquitanceDetailsForAG(String billNo, int year, int month, String adcode) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        List datalist = new ArrayList();
        AquitanceDataAGBean agbean = null;

        String sql = "";
        try {
            con = this.dataSource.getConnection();

            sql = "select * from arr_mast where bill_no=? and acct_type='GPF' and cpf_head > 0";
            pst = con.prepareStatement(sql);
            pst.setInt(1, Integer.parseInt(billNo));
            rs = pst.executeQuery();
            while (rs.next()) {
                agbean = new AquitanceDataAGBean();
                agbean.setEmpid(rs.getString("emp_id"));
                agbean.setGpfNo(rs.getString("gpf_acc_no"));
                agbean.setEmpname(rs.getString("emp_name"));
                agbean.setDesg(rs.getString("cur_desg"));

                int gpfSubAmt = rs.getInt("cpf_head");
                int totalAmt = gpfSubAmt;
                agbean.setGpfSubscriptionAmt(gpfSubAmt + "");

                agbean.setGaAmt("0");
                agbean.setOrgAmount("0");
                agbean.setTotRecAmt("0");
                agbean.setBalance("0");
                agbean.setRefCount("0");

                //agbean.setAdAmt(rs.getString("ad_amt"));
                agbean.setAdAmt(totalAmt + "");
                if (totalAmt > 0) {
                    datalist.add(agbean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return datalist;
    }

    private String getArrearBillPeriod(String billno) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        String billperiod = "";
        try {
            con = this.dataSource.getConnection();

            if (billno != null && !billno.equals("")) {
                String sql = "select from_month,from_year,to_month,to_year from bill_mast where bill_no=?";
                pst = con.prepareStatement(sql);
                pst.setInt(1, Integer.parseInt(billno));
                rs = pst.executeQuery();
                if (rs.next()) {
                    if (rs.getString("from_month") != null && !rs.getString("from_month").equals("")) {
                        billperiod = CommonFunctions.getMonthAsString(rs.getInt("from_month") - 1);
                    }
                    if (rs.getString("from_year") != null && !rs.getString("from_year").equals("")) {
                        billperiod = billperiod + "-" + rs.getInt("from_year");
                    }
                    if (rs.getString("to_month") != null && !rs.getString("to_month").equals("")) {
                        billperiod = billperiod + "\nto\n" + CommonFunctions.getMonthAsString(rs.getInt("to_month") - 1);
                    }
                    if (rs.getString("to_year") != null && !rs.getString("to_year").equals("")) {
                        billperiod = billperiod + "-" + rs.getInt("to_year");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return billperiod;
    }

    private void printGPFArrearHeader(PdfPTable table, PdfPCell cell, Font f1, String majorhead, String treasuryCode, String loanName, String nowDedn) {

        try {

            cell = new PdfPCell(new Phrase("SALARY HEAD", f1));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("TREASURY CODE", f1));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("RECOVERY", f1));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell();
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(majorhead, f1));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(treasuryCode, f1));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(loanName, f1));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell();
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printGPFArrearSubHeader(PdfPTable table, PdfPCell cell, Font f1, String vchNo, String vchDate, String ddocode) {

        try {
            cell = new PdfPCell(new Phrase("TV No: " + vchNo, f1));
            cell.setColspan(2);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("TV Date: " + vchDate, f1));
            cell.setColspan(3);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("DDO Code: " + ddocode, f1));
            cell.setColspan(6);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Sl No", f1));
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Name and Designation", f1));
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("GPF No", f1));
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Sub-\nscription\nAmt", f1));
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Refund\nof\nAdv", f1));
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Amt Deducted in the Bill", f1));
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("No of Instl. of Rec.", f1));
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Amt of Org. Adv.", f1));
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Rec. up to\nthe Month", f1));
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Bal Out-\nstanding", f1));
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Bill Period", f1));
            //cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
