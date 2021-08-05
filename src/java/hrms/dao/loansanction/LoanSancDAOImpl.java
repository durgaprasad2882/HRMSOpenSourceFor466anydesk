package hrms.dao.loansanction;

import hrms.common.CalendarCommonMethods;
import hrms.common.CommonFunctions;
import hrms.common.DataBaseFunctions;
import hrms.model.loan.EmployeeLoan;
import hrms.model.loan.EmployeeLoanAccBean;
import hrms.model.loan.Loan;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.apache.commons.lang.builder.ToStringBuilder;

public class LoanSancDAOImpl implements LoanSancDAO {

    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    protected MaxLoanIdDAOImpl maxloanidDao;

    public MaxLoanIdDAOImpl getMaxloanidDao() {
        return maxloanidDao;
    }

    public void setMaxloanidDao(MaxLoanIdDAOImpl maxloanidDao) {
        this.maxloanidDao = maxloanidDao;
    }

    @Override
    public List getLoanSancList(String empId) {
        List loanlist = new ArrayList();
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        Loan loanForm = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("select LOANID,lnnotid,LNNOTYPE,LNTP,ACC_NO,LNAMNT,LOAN_NAME,P_RECOVERED from(  select LOANID,NOT_ID,LOAN_TP,emp_loan_sanc.loanid lnid, emp_loan_sanc.not_id lnnotid, "
                    + "emp_loan_sanc.not_type lnnotype,emp_loan_sanc.loan_tp lntp,emp_loan_sanc.amount lnamnt,ACC_NO,P_RECOVERED,I_RECOVERED from emp_loan_sanc where emp_loan_sanc.emp_id=? and "
                    + "emp_loan_sanc.not_type='LOAN_SANC') temploan left outer join g_loan on temploan.loan_tp=g_loan.loan_tp order by P_RECOVERED");
            pstmt.setString(1, empId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                loanForm = new Loan();
                loanForm.setCompletedRecovery(rs.getInt("P_RECOVERED"));
                loanForm.setNotid(rs.getString("lnnotid"));
                loanForm.setLoanid(rs.getString("LOANID"));
                loanForm.setSltloan(rs.getString("LOAN_NAME"));
                loanForm.setTxtamount(rs.getDouble("LNAMNT"));
                loanForm.setAccountNo(rs.getString("ACC_NO"));
                loanlist.add(loanForm);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return loanlist;
    }

    @Override
    public void saveLoanDetail(Loan lfb, String notId) {
        Connection con = null;
        PreparedStatement pstmt = null;
        String loanId = "";
        try {
            con = dataSource.getConnection();
            loanId = maxloanidDao.getLoanId();
            ToStringBuilder.reflectionToString(lfb);
            pstmt = con.prepareStatement("insert into emp_loan_sanc(loanid, not_id, not_type, emp_id, loan_tp, amount,ACC_NO,VCH_NO,VCH_DATE,TR_CODE,NOW_DEDN,DED_ST_DATE,P_ORG_AMT,P_TOT_NO_INSTL,P_INSTL_AMT,P_LAST_INSTL_NO,P_INSTL_NO,P_CUM_RECOVERED,P_RECOVERED,BANK_CODE,BRANCH_CODE) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            pstmt.setString(1, loanId);
            pstmt.setString(2, notId);
            pstmt.setString(3, "LOAN_SANC");
            pstmt.setString(4, lfb.getEmpid());
            pstmt.setString(5, lfb.getSltloan());
            pstmt.setDouble(6, lfb.getTxtamount());
            if (lfb.getAccountNo() != null && !lfb.getAccountNo().equalsIgnoreCase("")) {
                pstmt.setString(7, lfb.getAccountNo());
            } else {
                pstmt.setString(7, null);
            }
            if (lfb.getVoucherNo() != null && !lfb.getVoucherNo().equalsIgnoreCase("")) {
                pstmt.setString(8, lfb.getVoucherNo());
            } else {
                pstmt.setString(8, null);
            }
            if (lfb.getVoucherDate() != null) {
                pstmt.setDate(9, new java.sql.Date(lfb.getVoucherDate().getTime()));
            } else {
                pstmt.setDate(9, null);
            }
            if (lfb.getTreasuryname() != null && !lfb.getTreasuryname().equalsIgnoreCase("")) {
                pstmt.setString(10, lfb.getTreasuryname());
            } else {
                pstmt.setString(10, null);
            }

            pstmt.setString(11, lfb.getNowDeduct());
            if (lfb.getDeductionStartDate() != null) {
                pstmt.setDate(12, new java.sql.Date(lfb.getDeductionStartDate().getTime()));
            } else {
                pstmt.setDate(12, null);
            }

            pstmt.setInt(13, lfb.getOriginalAmt());
            pstmt.setInt(14, lfb.getTotalNoOfInsl());
            pstmt.setInt(15, lfb.getInstalmentAmount());
            pstmt.setInt(16, lfb.getLastPaidInstalNo());
            pstmt.setInt(17, lfb.getMonthlyinstlno());
            pstmt.setInt(18, lfb.getCumulativeAmtPaid());
            pstmt.setInt(19, lfb.getCompletedRecovery());
            if (lfb.getBank() != null && !lfb.getBank().equals("")) {
                pstmt.setString(20, lfb.getBank());
            } else {
                pstmt.setString(20, null);
            }
            if (lfb.getBranch() != null && !lfb.getBranch().equals("")) {
                pstmt.setString(21, lfb.getBranch());
            } else {
                pstmt.setString(21, null);
            }

            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    @Override
    public void updateLoanDetail(Loan lfb) {
        Connection con = null;
        PreparedStatement pstmt = null;
        //String loanId = "";
        try {
            con = dataSource.getConnection();
            // loanId = maxloanidDao.getLoanId();
            pstmt = con.prepareStatement("update emp_loan_sanc set  loan_tp=?, amount=?,ACC_NO=?,VCH_NO=?,VCH_DATE=?,TR_CODE=?,NOW_DEDN=?,DED_ST_DATE=?,P_ORG_AMT=?,P_TOT_NO_INSTL=?,P_INSTL_AMT=?,P_LAST_INSTL_NO=?,P_INSTL_NO=?,P_CUM_RECOVERED=?,P_RECOVERED=?,BANK_CODE=?,BRANCH_CODE=? where loanid=?");
            pstmt.setString(1, lfb.getSltloan());
            pstmt.setDouble(2, lfb.getTxtamount());
            if (lfb.getAccountNo() != null && !lfb.getAccountNo().equalsIgnoreCase("")) {
                pstmt.setString(3, lfb.getAccountNo());
            } else {
                pstmt.setString(3, null);
            }
            if (lfb.getVoucherNo() != null && !lfb.getVoucherNo().equalsIgnoreCase("")) {
                pstmt.setString(4, lfb.getVoucherNo());
            } else {
                pstmt.setString(4, null);
            }
            if (lfb.getVoucherDate() != null) {
                pstmt.setDate(5, new java.sql.Date(lfb.getVoucherDate().getTime()));
            } else {
                pstmt.setDate(5, null);
            }
            if (lfb.getTreasuryname() != null && !lfb.getTreasuryname().equalsIgnoreCase("")) {
                pstmt.setString(6, lfb.getTreasuryname());
            } else {
                pstmt.setString(6, null);
            }

            if (lfb.getNowDeduct() != null && !lfb.getNowDeduct().equalsIgnoreCase("")) {
                pstmt.setString(7, lfb.getNowDeduct());
            } else {
                pstmt.setString(7, null);
            }
            if (lfb.getDeductionStartDate() != null) {
                pstmt.setDate(8, new java.sql.Date(lfb.getDeductionStartDate().getTime()));
            } else {
                pstmt.setDate(8, null);
            }

            pstmt.setInt(9, lfb.getOriginalAmt());
            pstmt.setInt(10, lfb.getTotalNoOfInsl());
            pstmt.setInt(11, lfb.getInstalmentAmount());
            pstmt.setInt(12, lfb.getLastPaidInstalNo());
            pstmt.setInt(13, lfb.getMonthlyinstlno());
            pstmt.setInt(14, lfb.getCumulativeAmtPaid());
            pstmt.setInt(15, lfb.getCompletedRecovery());
            if (lfb.getBank() != null && !lfb.getBank().equals("")) {
                pstmt.setString(16, lfb.getBank());
            } else {
                pstmt.setString(16, null);
            }
            if (lfb.getBranch() != null && !lfb.getBranch().equals("")) {
                pstmt.setString(17, lfb.getBranch());
            } else {
                pstmt.setString(17, null);
            }
            pstmt.setString(18, lfb.getLoanid());
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    @Override
    public Loan editLoanData(String loanId) {
        Connection con = null;
        PreparedStatement pstmt = null;
        Loan lfb = null;
        ResultSet rs = null;
        String input = "2013-09-14";
        // DateFormat format = DateFormat.getInstance();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-mm-dd");
        SimpleDateFormat format2 = new SimpleDateFormat("dd-MMM-yy");
        Date date;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("select ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME,L_NAME], ' ') EMPNAME,post,empnoti.not_id as notid,note,empnoti.not_type as nottype, empnoti.emp_id as empid, loan_tp, amount,ACC_NO,VCH_NO,VCH_DATE,emploan.TR_CODE trcode,emploan.NOW_DEDN as nowdedn, "
                    + " DED_ST_DATE,P_ORG_AMT,P_TOT_NO_INSTL, P_INSTL_AMT,P_LAST_INSTL_NO,P_LAST_PMT_MON,P_CUM_RECOVERED,I_ORG_AMT,I_TOT_NO_INST, "
                    + " I_INSTL_AMT,I_LAST_INSTL_NO,I_LAST_PMT_MON, I_CUM_RECOVERED,P_RECOVERED,I_RECOVERED,PVT_DESC,emploan.BANK_CODE as bankcode,emploan.BRANCH_CODE as branchcode, "
                    + " BT_ID_P,BT_ID_I,P_INSTL_NO,auth,ordno,orddt,empnoti.dept_code deptcode,empnoti.off_code as offcode from emp_loan_sanc emploan  "
                    + " inner join emp_notification empnoti on emploan.not_id=empnoti.not_id "
                    + " left outer join emp_mast empmast on empnoti.auth=empmast.cur_spc "
                    + " left outer join g_spc gspc on empmast.cur_spc=gspc.spc "
                    + " left outer join g_post gpost on gspc.gpc=gpost.post_code "
                    + " where emploan.loanid='" + loanId + "'");

            rs = pstmt.executeQuery();
            if (rs.next()) {
                lfb = new Loan();
                lfb.setLoanid(loanId);
                lfb.setNotid(rs.getString("notid"));
                lfb.setOrderno(rs.getString("ordno"));
                lfb.setOrderdate(rs.getDate("orddt"));
                lfb.setNotType(rs.getString("nottype"));
                lfb.setEmpid(rs.getString("empid"));
                lfb.setSltloan(rs.getString("loan_tp"));
                lfb.setTxtamount(rs.getInt("amount"));
                if (rs.getString("empname") != null && rs.getString("post") != null) {
                    lfb.setAuthority(rs.getString("empname") + "," + rs.getString("post"));
                } else {
                    lfb.setAuthority("");
                }
                lfb.setHidAuthDeptCode(rs.getString("deptcode"));
                lfb.setHidAuthOffCode(rs.getString("offcode"));
                lfb.setHidSpcAuthCode(rs.getString("auth"));
                lfb.setAccountNo(rs.getString("ACC_NO"));
                lfb.setVoucherNo(rs.getString("VCH_NO"));
                lfb.setVoucherDate(rs.getDate("VCH_DATE"));
                lfb.setTreasuryname(rs.getString("trcode"));
                lfb.setNowDeduct(rs.getString("nowdedn"));
                lfb.setDeductionStartDate(rs.getDate("DED_ST_DATE"));
                lfb.setOriginalAmt(rs.getInt("P_ORG_AMT"));
                lfb.setTotalNoOfInsl(rs.getInt("P_TOT_NO_INSTL"));
                lfb.setInstalmentAmount(rs.getInt("P_INSTL_AMT"));
                lfb.setLastPaidInstalNo(rs.getInt("P_LAST_INSTL_NO"));
                lfb.setMonthlyinstlno(rs.getInt("P_INSTL_NO"));
                lfb.setCumulativeAmtPaid(rs.getInt("P_CUM_RECOVERED"));
                lfb.setCompletedRecovery(rs.getInt("P_RECOVERED"));
                lfb.setTxtpvtloan(rs.getString("PVT_DESC"));
                lfb.setBank(rs.getString("bankcode"));
                lfb.setBranch(rs.getString("branchcode"));
                lfb.setNote(rs.getString("note"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return lfb;
    }

    @Override
    public void removeLoanData(String loanId, String notid) {
        Connection con = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmtt = null;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("delete from emp_loan_sanc where loanid=?");
            pstmt.setString(1, loanId);
            pstmt.executeUpdate();
            pstmtt = con.prepareStatement("delete from emp_notification where not_id=?");
            pstmtt.setString(1, notid);
            pstmtt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pstmt);
            DataBaseFunctions.closeSqlObjects(pstmtt);
            DataBaseFunctions.closeSqlObjects(con);
        }

    }

    public class LoanRefernce {

        private int noofinstalmentRecovered;
        private int cummAmtRecovered;

        public int getNoofinstalmentRecovered() {
            return noofinstalmentRecovered;
        }

        public void setNoofinstalmentRecovered(int noofinstalmentRecovered) {
            this.noofinstalmentRecovered = noofinstalmentRecovered;
        }

        public int getCummAmtRecovered() {
            return cummAmtRecovered;
        }

        public void setCummAmtRecovered(int cummAmtRecovered) {
            this.cummAmtRecovered = cummAmtRecovered;
        }

    }

    public LoanRefernce getRefCount(String loanId, String nowdedn, String empId, int loanemi) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet res = null;
        int cummAmtRecovered = 0;
        int noofinstalmentRecovered = 0;
        LoanRefernce loanRefernce = new LoanRefernce();
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT sum(INSTALMENT_COUNT) as INSTALMENT_COUNT,sum(AD_AMT) as AD_AMT FROM AQ_DTLS WHERE AD_REF_ID = ? AND NOW_DEDN=? AND AD_AMT != 0");
            pstmt.setString(1, loanId);
            pstmt.setString(2, nowdedn);
            res = pstmt.executeQuery();
            while (res.next()) {
                cummAmtRecovered = cummAmtRecovered + res.getInt("AD_AMT");
                noofinstalmentRecovered = noofinstalmentRecovered + res.getInt("INSTALMENT_COUNT");
            }

            pstmt = con.prepareStatement("SELECT sum(INSTALMENT_COUNT) as INSTALMENT_COUNT,sum(AD_AMT) as AD_AMT FROM HRMIS.AQ_DTLS1 WHERE EMP_CODE=? AND AD_REF_ID = ? AND NOW_DEDN=? AND AD_AMT != 0");
            pstmt.setString(1, empId);
            pstmt.setString(2, loanId);
            pstmt.setString(3, nowdedn);
            res = pstmt.executeQuery();
            if (res.next()) {
                int emi = res.getInt("AD_AMT");
                int emicnt = res.getInt("INSTALMENT_COUNT");

                cummAmtRecovered = cummAmtRecovered + emi;
                noofinstalmentRecovered = noofinstalmentRecovered + emicnt;
            }

            pstmt = con.prepareStatement("SELECT AD_AMT,INSTALMENT_COUNT FROM LOAN_ADJUSTMENT WHERE AD_REF_ID = ? AND DED_TYPE=? AND AD_AMT != 0");
            pstmt.setString(1, loanId);
            pstmt.setString(2, nowdedn);
            res = pstmt.executeQuery();
            while (res.next()) {
                cummAmtRecovered = cummAmtRecovered + res.getInt("AD_AMT");
                noofinstalmentRecovered = noofinstalmentRecovered + res.getInt("INSTALMENT_COUNT");
            }
            loanRefernce.setCummAmtRecovered(cummAmtRecovered);
            loanRefernce.setNoofinstalmentRecovered(noofinstalmentRecovered);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(res, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return loanRefernce;
    }

    @Override
    public List getPrincipalLoanListForBill(String empId, int payday, String depcode) {
        List loanlist = new ArrayList();
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;
        Loan loanForm = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT LOANID,G_LOAN.LOAN_TP,LOAN_NAME,DISB_DATE,P_TOT_NO_INSTL,P_INSTL_AMT,P_LAST_INSTL_NO,ACC_NO, ORDNO ,G_LOAN.REP_COL,G_LOAN.ROW_NO,HAVE_INT,NOW_DEDN,P_ORG_AMT,"
                    + "BT_CODE_PRINCIPAL,BT_CODE_INTEREST,BT_ID_P,P_CUM_RECOVERED FROM (SELECT LOANID,LOAN_TP,DISB_DATE,P_TOT_NO_INSTL,P_INSTL_AMT,P_LAST_INSTL_NO,ACC_NO,GETORDNO(NOT_ID) ORDNO,NOW_DEDN,P_ORG_AMT,BT_ID_P,P_CUM_RECOVERED FROM EMP_LOAN_SANC "
                    + "where (P_RECOVERED IS NULL OR P_RECOVERED = '0') AND (P_INSTL_AMT IS NOT NULL OR P_INSTL_AMT > 0) AND NOW_DEDN IS NOT NULL AND STOP_LOAN IS NULL AND  EMP_ID = ? "
                    + "UNION "
                    + "SELECT LOANID,LOAN_TP,DISB_DATE,P_TOT_NO_INSTL,P_INSTL_AMT,P_LAST_INSTL_NO,ACC_NO,GETORDNO(NOT_ID) ORDNO,NOW_DEDN,P_ORG_AMT,BT_ID_P,P_CUM_RECOVERED FROM EMP_LOAN_SANC WHERE EMP_ID = ? AND NOW_DEDN = 'I' AND "
                    + "(I_RECOVERED IS NULL OR I_RECOVERED = '0'))EMP_LOAN_SANC1 RIGHT OUTER JOIN G_LOAN ON EMP_LOAN_SANC1.LOAN_TP = G_LOAN.LOAN_TP");
            pstmt.setString(1, empId);
            pstmt.setString(2, empId);
            rs = pstmt.executeQuery();
            System.out.println("empId:"+empId);
            while (rs.next()) {
                loanForm = new Loan();
                loanForm.setLoanid(rs.getString("LOANID"));
                loanForm.setSltloan(rs.getString("LOAN_TP"));
                loanForm.setLoanName(rs.getString("LOAN_NAME"));
                loanForm.setOrderdate(rs.getDate("DISB_DATE"));
                loanForm.setTotalNoOfInsl(rs.getInt("P_TOT_NO_INSTL"));
                loanForm.setInstalmentAmount(rs.getInt("P_INSTL_AMT"));
                loanForm.setLastPaidInstalNo(rs.getInt("P_LAST_INSTL_NO"));
                loanForm.setAccountNo(rs.getString("ACC_NO"));
                loanForm.setOrderno(rs.getString("ORDNO"));
                loanForm.setRepcol(rs.getInt("REP_COL"));
                loanForm.setRowno(rs.getInt("ROW_NO"));
                loanForm.setHaveInt(rs.getString("HAVE_INT"));
                loanForm.setNowdedn(rs.getString("NOW_DEDN"));
                loanForm.setOriginalAmt(rs.getInt("P_ORG_AMT"));
                if(rs.getString("BT_ID_P")!=null && !rs.getString("BT_ID_P").equals("")){
                    loanForm.setBtid(rs.getString("BT_ID_P"));
                }else{
                    loanForm.setBtid(rs.getString("BT_CODE_PRINCIPAL"));
                }
                loanForm.setCumulativeAmtPaid(rs.getInt("P_CUM_RECOVERED"));
                int adamt = loanForm.getInstalmentAmount();
                int cumamtrec = 0;
                int refcount = 0;
                String refdesc = "";
                
                if (loanForm.getLoanid() != null && loanForm.getNowdedn().equals("P") && payday >= 15 && !depcode.equals("05")) {
                    LoanRefernce loanRefernce = getRefCount(loanForm.getLoanid(), loanForm.getNowdedn(), empId, adamt);
                    //LoanRefernce loanRefernce = new LoanRefernce();
                    cumamtrec = loanRefernce.getCummAmtRecovered()+ loanForm.getCumulativeAmtPaid();
                    int calamt = cumamtrec + adamt;
                    refcount = loanRefernce.getNoofinstalmentRecovered() + loanForm.getLastPaidInstalNo() + 1;
                    if (refcount == loanForm.getTotalNoOfInsl()) {
                        refdesc = refcount + "/" + loanForm.getTotalNoOfInsl();
                        if (calamt > loanForm.getOriginalAmt() && loanForm.getOriginalAmt() > 0) {
                            int extra_amt = calamt - loanForm.getOriginalAmt();
                            adamt = adamt - extra_amt;
                            cumamtrec = cumamtrec + adamt;
                        } else if (calamt < loanForm.getOriginalAmt() && loanForm.getOriginalAmt() > 0) {
                            int extra_amt = loanForm.getOriginalAmt() - calamt;
                            adamt = adamt + extra_amt;
                            cumamtrec = cumamtrec + adamt;
                        } else {
                            cumamtrec = cumamtrec + adamt;
                        }
                    } else if (refcount < loanForm.getTotalNoOfInsl()) {
                        cumamtrec = loanRefernce.getCummAmtRecovered() + loanForm.getCumulativeAmtPaid() + adamt;
                        refdesc = refcount + "/" + loanForm.getTotalNoOfInsl();
                    } else {
                        pstmt1 = con.prepareStatement("UPDATE EMP_LOAN_SANC SET P_RECOVERED = 1 WHERE LOANID=?");
                        pstmt1.setString(1, loanForm.getLoanid());
                        pstmt1.executeUpdate();
                        refcount = 0;
                        refdesc = "";
                        adamt = 0;
                        cumamtrec = 0;
                    }
                } else {
                    loanForm.setNowdedn("P");
                    adamt = 0;
                    cumamtrec = 0;
                    refdesc = "";
                    refcount = 0;
                }
                loanForm.setInstalmentAmount(adamt);
                loanForm.setCummrecovered(cumamtrec);
                loanForm.setRefCount(refcount);
                loanForm.setRefDesc(refdesc);
                loanlist.add(loanForm);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(pstmt1);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return loanlist;
    }

    @Override
    public List getInterestLoanListForBill(String empId, int payday, String depcode) {
        List loanlist = new ArrayList();
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;
        Loan loanForm = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT LOANID,G_LOAN.LOAN_TP,LOAN_NAME,DISB_DATE,I_TOT_NO_INST,I_INSTL_AMT,I_LAST_INSTL_NO,ACC_NO, ORDNO ,G_LOAN.REP_COL,G_LOAN.ROW_NO,NOW_DEDN,I_ORG_AMT,BT_CODE_INTEREST, I_CUM_RECOVERED "
                    + "FROM (SELECT LOANID,LOAN_TP,DISB_DATE,I_TOT_NO_INST,I_INSTL_AMT,I_LAST_INSTL_NO,ACC_NO,GETORDNO(NOT_ID) ORDNO,NOW_DEDN,I_ORG_AMT, I_CUM_RECOVERED FROM EMP_LOAN_SANC where (I_RECOVERED IS NULL OR I_RECOVERED = 0) AND "
                    + "(NOW_DEDN='I' OR NOW_DEDN='B') AND STOP_LOAN IS NULL AND EMP_ID = ?)EMP_LOAN_SANC1 INNER JOIN G_LOAN ON EMP_LOAN_SANC1.LOAN_TP = G_LOAN.LOAN_TP");
            pstmt.setString(1, empId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                loanForm = new Loan();
                loanForm.setLoanid(rs.getString("LOANID"));
                loanForm.setSltloan(rs.getString("LOAN_TP"));
                loanForm.setLoanName(rs.getString("LOAN_NAME"));
                loanForm.setOrderdate(rs.getDate("DISB_DATE"));
                loanForm.setTotalNoOfInsl(rs.getInt("I_TOT_NO_INST"));
                loanForm.setInstalmentAmount(rs.getInt("I_INSTL_AMT"));
                loanForm.setLastPaidInstalNo(rs.getInt("I_LAST_INSTL_NO"));
                loanForm.setAccountNo(rs.getString("ACC_NO"));
                loanForm.setOrderno(rs.getString("ORDNO"));
                loanForm.setRepcol(rs.getInt("REP_COL"));
                loanForm.setRowno(rs.getInt("ROW_NO"));
                loanForm.setNowdedn(rs.getString("NOW_DEDN"));
                loanForm.setOriginalAmt(rs.getInt("I_ORG_AMT"));
                loanForm.setBtid(rs.getString("BT_CODE_INTEREST"));
                loanForm.setCumulativeAmtPaid(rs.getInt("I_CUM_RECOVERED"));

                int adamt = loanForm.getInstalmentAmount();
                int cumamtrec = 0;
                int refcount = 0;
                String refdesc = "";
                if (loanForm.getLoanid() != null && loanForm.getNowdedn().equals("I") && payday >= 15 && !depcode.equals("05")) {
                    LoanRefernce loanRefernce = getRefCount(loanForm.getLoanid(), loanForm.getNowdedn(), empId, adamt);
                    //LoanRefernce loanRefernce = new LoanRefernce();
                    cumamtrec = loanRefernce.getCummAmtRecovered() + loanForm.getCumulativeAmtPaid();
                    int calamt = cumamtrec + adamt;
                    refcount = loanRefernce.getNoofinstalmentRecovered() + loanForm.getLastPaidInstalNo() + 1;
                    if (refcount == loanForm.getTotalNoOfInsl()) {
                        refdesc = refcount + "/" + loanForm.getTotalNoOfInsl();
                        if (calamt > loanForm.getOriginalAmt() && loanForm.getOriginalAmt() > 0) {
                            int extra_amt = calamt - loanForm.getOriginalAmt();
                            adamt = adamt - extra_amt;
                            cumamtrec = cumamtrec + adamt;
                        } else if (calamt < loanForm.getOriginalAmt() && loanForm.getOriginalAmt() > 0) {
                            int extra_amt = loanForm.getOriginalAmt() - calamt;
                            adamt = adamt + extra_amt;
                            cumamtrec = cumamtrec + adamt;
                        } else {
                            cumamtrec = cumamtrec + adamt;
                        }
                    } else if (refcount < loanForm.getTotalNoOfInsl()) {
                        cumamtrec = loanRefernce.getCummAmtRecovered() + loanForm.getCumulativeAmtPaid() + adamt;
                        refdesc = refcount + "/" + loanForm.getTotalNoOfInsl();
                    } else {
                        pstmt1 = con.prepareStatement("UPDATE EMP_LOAN_SANC SET I_RECOVERED = 1 WHERE LOANID=?");
                        pstmt1.setString(1, loanForm.getLoanid());
                        pstmt1.executeUpdate();
                        refcount = 0;
                        refdesc = "";
                        adamt = 0;
                        cumamtrec = 0;
                    }
                } else {
                    loanForm.setNowdedn("I");
                    adamt = 0;
                    cumamtrec = 0;
                    refdesc = "";
                    refcount = 0;
                }
                loanForm.setInstalmentAmount(adamt);
                loanForm.setCummrecovered(cumamtrec);
                loanForm.setRefCount(refcount);
                loanForm.setRefDesc(refdesc);
                loanlist.add(loanForm);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(pstmt1);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return loanlist;
    }

    @Override
    public List getLoanDeductionDeailEmpWise(String empId, String loanType) {
        List loanDetailList = new ArrayList();
        Connection con = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        Loan loan = null;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("select NOW_DEDN,LOANID,P_RECOVERED,I_RECOVERED,AMOUNT,DISB_DATE from emp_loan_sanc where emp_id=? and loan_tp=? GROUP BY LOANID,P_RECOVERED,I_RECOVERED,AMOUNT,DISB_DATE,NOW_DEDN ORDER BY P_RECOVERED DESC");
            pstmt1 = con.prepareStatement("select VCH_NO,VCH_DATE,aq_mast.bill_no,BILL_DESC,aq_mast.bill_date,aq_dtls.* from (select aq_year,aq_month,REF_COUNT,AD_AMT,AQSL_NO,TOT_REC_AMT from aq_dtls where ad_ref_id=? and emp_code=? and AD_AMT>0)aq_dtls"
                    + " left outer join (select AQSL_NO,bill_no,emp_code,bill_date from aq_mast where emp_code=?)aq_mast on aq_dtls.AQSL_NO=aq_mast.AQSL_NO"
                    + " left outer join bill_mast on aq_mast.bill_no=bill_mast.bill_no order by aq_dtls.aq_year,aq_dtls.aq_month asc");

            pstmt.setString(1, empId);
            pstmt.setString(2, loanType);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                EmployeeLoan employeeLoan = new EmployeeLoan();
                employeeLoan.setLoanamt(rs.getString("AMOUNT"));
                employeeLoan.setLoandate(rs.getString("DISB_DATE"));
                employeeLoan.setNow_ded(rs.getString("NOW_DEDN"));
                if (employeeLoan.getNow_ded() != null && employeeLoan.getNow_ded().equals("P")) {
                    employeeLoan.setIscompleted(rs.getString("P_RECOVERED"));
                } else if (employeeLoan.getNow_ded() != null && employeeLoan.getNow_ded().equals("I")) {
                    employeeLoan.setIscompleted(rs.getString("I_RECOVERED"));
                }
                String adRefId = rs.getString("LOANID");
                ArrayList loanaccList = new ArrayList();
                pstmt1.setString(1, adRefId);
                pstmt1.setString(2, empId);
                pstmt1.setString(3, empId);
                rs1 = pstmt1.executeQuery();
                while (rs1.next()) {
                    EmployeeLoanAccBean ebean = new EmployeeLoanAccBean();
                    ebean.setLoanid(rs.getString("LOANID"));
                    ebean.setDedmonth(CalendarCommonMethods.getMonthAsString(rs1.getInt("aq_month")));
                    ebean.setDedyear(rs1.getString("aq_year"));
                    ebean.setInslno(rs1.getString("REF_COUNT"));
                    ebean.setDedamt(rs1.getString("AD_AMT"));
                    int bal = Integer.parseInt(employeeLoan.getLoanamt()) - rs1.getInt("TOT_REC_AMT");
                    ebean.setBal(bal + "");
                    ebean.setBillNo(rs1.getString("BILL_DESC"));
                    ebean.setBilldate(CommonFunctions.getFormattedOutputDate1(rs1.getDate("BILL_DATE")));
                    ebean.setVchno(rs1.getString("VCH_NO"));
                    ebean.setVchdate(CommonFunctions.getFormattedOutputDate1(rs1.getDate("VCH_DATE")));
                    loanaccList.add(ebean);
                }
                employeeLoan.setLoanAccDetails(loanaccList);
                if (employeeLoan.getLoanAccDetails() != null && employeeLoan.getLoanAccDetails().size() > 0) {
                    loanDetailList.add(employeeLoan);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(rs1, pstmt1);
            DataBaseFunctions.closeSqlObjects(con);
        }

        return loanDetailList;
    }

    @Override
    public String getFABTId(String demandNumber) {
        String btId = "";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = dataSource.getConnection();

            pstmt = con.prepareStatement("SELECT fa_bt_id FROM g_demand_no where demand_no=? ");
            pstmt.setString(1, demandNumber);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                btId = rs.getString("fa_bt_id");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return btId;
    }

    @Override
    public String getGPFBTId(String gpfType) {
        String btId = "";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = dataSource.getConnection();

            pstmt = con.prepareStatement("SELECT BT_ID  FROM G_GPF_TYPE WHERE GPF_TYPE = ?");
            pstmt.setString(1, gpfType);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                btId = rs.getString("BT_ID");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return btId;
    }
}
