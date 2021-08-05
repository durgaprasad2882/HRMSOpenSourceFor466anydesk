/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.payroll.billbrowser;

import hrms.SelectOption;
import hrms.common.CalendarCommonMethods;
import hrms.common.CommonFunctions;
import hrms.common.DataBaseFunctions;
import hrms.dao.billvouchingTreasury.VouchingServicesDAOImpl;
import hrms.model.billvouchingTreasury.BillDetail;
import hrms.model.billvouchingTreasury.ObjectBreakup;
import hrms.model.payroll.BytransferDetails;
import hrms.model.payroll.LtaLoanList;
import hrms.model.payroll.NPSDetails;
import hrms.model.payroll.billbrowser.AllowDeductDetails;
import hrms.model.payroll.billbrowser.BillAttr;
import hrms.model.payroll.billbrowser.BillBean;
import hrms.model.payroll.billbrowser.BillBrowserbean;
import hrms.model.payroll.billbrowser.BillHistoryAttr;
import hrms.model.payroll.billbrowser.GetBillStatusBean;
import hrms.model.payroll.billbrowser.GlobalBillStatus;
import java.math.BigDecimal;
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
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Manas Jena
 */
public class BillBrowserDAOImpl implements BillBrowserDAO {

    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public BillBrowserbean getArrearBillPeriod(String offcode, int prepareMonth, int preparedYear) {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        BillBrowserbean bb = new BillBrowserbean();
        try {

            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT from_month, from_year, to_month, to_year  FROM BILL_MAST WHERE OFF_CODE=? AND AQ_MONTH=? AND AQ_YEAR=? AND TYPE_OF_BILL=?");
            pstmt.setString(1, offcode);
            pstmt.setInt(2, prepareMonth);
            pstmt.setInt(3, preparedYear);
            pstmt.setString(4, "ARREAR");
            pstmt.executeQuery();
            rs = pstmt.executeQuery();
            if (rs.next()) {
                bb.setSltFromMonth(rs.getInt("from_month"));
                bb.setSltFromYear(rs.getInt("from_year"));
                bb.setSltToMonth(rs.getInt("to_month"));
                bb.setSltToYear(rs.getInt("to_year"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return bb;
    }

    @Override
    public ArrayList getBillPrepareYear(String offCode) {
        Connection con = null;
        ResultSet rs = null;
        ArrayList al = new ArrayList();
        SelectOption so;
        PreparedStatement pstmt = null;
        try {

            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT DISTINCT AQ_YEAR FROM BILL_MAST WHERE OFF_CODE=?  ORDER  BY AQ_YEAR DESC");
            pstmt.setString(1, offCode);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                so = new SelectOption();
                so.setLabel(rs.getString("AQ_YEAR"));
                so.setValue(rs.getString("AQ_YEAR"));
                al.add(so);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return al;

    }

    @Override
    public ArrayList getMajorHeadListTreasuryWise(String trcode, int aqyear, int aqmonth) {
        Connection con = null;
        ResultSet rs = null;
        ArrayList al = new ArrayList();
        SelectOption so;
        PreparedStatement pstmt = null;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("select distinct major_head from bill_mast where extract (year from vch_date)=? AND extract (month from vch_date)=? and tr_code in (select tr_code from g_treasury where ag_treasury_code=?) order by major_head");
            pstmt.setInt(1, aqyear);
            pstmt.setInt(2, aqmonth + 1);
            pstmt.setString(3, trcode);
            pstmt.execute();
            rs = pstmt.executeQuery();
            while (rs.next()) {
                so = new SelectOption();
                so.setLabel(rs.getString("major_head"));
                so.setValue(rs.getString("major_head"));
                al.add(so);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return al;

    }

    @Override
    public ArrayList getVoucherListTreasuryWise(String trcode, int aqyear, int aqmonth, String majorhead) {
        Connection con = null;
        ResultSet rs = null;
        ArrayList al = new ArrayList();
        PreparedStatement pstmt = null;
        LtaLoanList vchlist = null;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("select bill_no,vch_no from bill_mast where major_head=? and  extract (year from vch_date)=? and extract (month from vch_date)=? and tr_code in (select tr_code from g_treasury where ag_treasury_code=?) order by vch_no::integer");
            pstmt.setString(1, majorhead);
            pstmt.setInt(2, aqyear);
            pstmt.setInt(3, aqmonth);
            pstmt.setString(4, trcode);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                vchlist = new LtaLoanList();
                vchlist.setBillid(rs.getInt("bill_no"));
                vchlist.setHbabillId(rs.getInt("bill_no"));
                vchlist.setMcaBillId(rs.getInt("bill_no"));
                vchlist.setCompaBillId(rs.getInt("bill_no"));
                vchlist.setVeBillId(rs.getInt("bill_no"));
                vchlist.setGpfBillId(rs.getInt("bill_no"));
                String tvtcno = "";
                if (StringUtils.contains(rs.getString("vch_no"), "/")) {
                    tvtcno = StringUtils.leftPad(StringUtils.substring(rs.getString("vch_no"), (StringUtils.indexOf(rs.getString("vch_no"), "/") + 1)), 4, "0");
                } else if (StringUtils.contains(rs.getString("vch_no"), "-")) {
                    tvtcno = StringUtils.leftPad(StringUtils.substring(rs.getString("vch_no"), (StringUtils.indexOf(rs.getString("vch_no"), "-") + 1)), 4, "0");
                } else {
                    if (rs.getString("vch_no").length() > 4) {
                        tvtcno = StringUtils.leftPad(StringUtils.substring(rs.getString("vch_no"), 4), 4, "0");
                    } else {
                        tvtcno = StringUtils.leftPad(rs.getString("vch_no"), 4, "0");
                    }
                }
                vchlist.setVchno(tvtcno);
                al.add(vchlist);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }

        return al;
    }

    @Override
    public ArrayList getMonthFromSelectedYear(String offCode, int year) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList month = new ArrayList();
        SelectOption so;
        try {

            if (year != 0) {
                con = dataSource.getConnection();
                pstmt = con.prepareStatement("SELECT DISTINCT AQ_MONTH FROM BILL_MAST WHERE OFF_CODE=? AND AQ_YEAR=? ORDER BY AQ_MONTH DESC");
                pstmt.setString(1, offCode);
                pstmt.setInt(2, year);
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    so = new SelectOption();
                    so.setValue(rs.getString("AQ_MONTH"));
                    so.setLabel(CalendarCommonMethods.getFullMonthAsString(rs.getInt("AQ_MONTH")));
                    month.add(so);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return month;

    }

    @Override
    public ArrayList getPayBillList(int year, int month, String offCode, String billType, String spc) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList<BillBean> billList = new ArrayList<>();

        try {
            //if (billType != null && billType.equalsIgnoreCase("PAY")) {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT BILL_GRP_ID FROM BILL_GROUP_PRIVILAGE WHERE SPC=?");
            pstmt.setString(1, spc);
            rs = pstmt.executeQuery();
            boolean separateBillMapped = false;
            if (rs.next()) {
                separateBillMapped = true;
                pstmt = con.prepareStatement("SELECT BILL_NO, BILL_DESC, BILL_DATE, BILL_TYPE,TYPE_OF_BILL, BILL_GROUP_DESC DESCRIPTION, BILL_STATUS_ID, ONLINE_BILL_SUBMISSION, IS_BILL_PREPARED, BILL_GROUP_ID FROM ( "
                        + "SELECT BILL_NO,BILL_DESC,BILL_DATE,BILL_TYPE,TYPE_OF_BILL,BILL_GROUP_DESC,BILL_STATUS_ID,ONLINE_BILL_SUBMISSION, IS_BILL_PREPARED, BILL_GROUP_ID  FROM ( "
                        + "SELECT BILL_NO,BILL_DESC,BILL_DATE,BILL_TYPE,TYPE_OF_BILL,BILL_GROUP_DESC,BILL_STATUS_ID,OFF_CODE, IS_BILL_PREPARED, BILL_GROUP_ID FROM ( "
                        + "SELECT BILL_GRP_ID FROM BILL_GROUP_PRIVILAGE WHERE SPC=? ) BILL_GROUP_PRIVILAGE "
                        + "LEFT OUTER JOIN ( "
                        + "SELECT BILL_NO,BILL_DESC,BILL_DATE,BILL_TYPE,TYPE_OF_BILL,BILL_GROUP_DESC,BILL_STATUS_ID,OFF_CODE, IS_BILL_PREPARED, BILL_GROUP_ID FROM BILL_MAST WHERE OFF_CODE=? AND AQ_MONTH=? AND AQ_YEAR=? AND TYPE_OF_BILL=? ORDER BY BILL_GROUP_DESC)BILL_MAST "
                        + "ON BILL_GROUP_PRIVILAGE.BILL_GRP_ID = BILL_MAST.BILL_GROUP_ID "
                        + ") BILL_MAST "
                        + "LEFT OUTER JOIN G_OFFICE ON BILL_MAST.OFF_CODE=G_OFFICE.OFF_CODE ORDER BY BILL_DESC) BILL_MAST");
                pstmt.setString(1, spc);
                pstmt.setString(2, offCode);
                pstmt.setInt(3, month);
                pstmt.setInt(4, year);
                pstmt.setString(5, billType);
                System.out.println("****billType:" + billType + "  month:" + month);
            } else {
                pstmt = con.prepareStatement("SELECT BILL_NO,BILL_DATE,BILL_TYPE,TYPE_OF_BILL,BILL_STATUS_ID,ONLINE_BILL_SUBMISSION , IS_BILL_PREPARED, BILL_GROUP_ID,DESCRIPTION, BILL_DESC,IS_DELETED FROM ( "
                        + "SELECT BILL_NO,BILL_DATE,BILL_TYPE,TYPE_OF_BILL,BILL_STATUS_ID,ONLINE_BILL_SUBMISSION , IS_BILL_PREPARED, BILL_GROUP_ID,DESCRIPTION, BILL_DESC,IS_DELETED FROM ( "
                        + "SELECT BILL_NO,BILL_DATE,BILL_TYPE,TYPE_OF_BILL,BILL_STATUS_ID,ONLINE_BILL_SUBMISSION , IS_BILL_PREPARED, BILL_GROUP_MASTER.BILL_GROUP_ID,DESCRIPTION, BILL_DESC,IS_DELETED FROM ( "
                        + "SELECT BILL_GROUP_ID, DESCRIPTION,IS_DELETED FROM BILL_GROUP_MASTER WHERE OFF_CODE=?) BILL_GROUP_MASTER "
                        + "LEFT OUTER JOIN "
                        + "(SELECT BILL_NO,BILL_DATE,BILL_TYPE,TYPE_OF_BILL,BILL_STATUS_ID,ONLINE_BILL_SUBMISSION , IS_BILL_PREPARED, BILL_GROUP_ID, BILL_DESC FROM ( "
                        + "SELECT BILL_NO,BILL_DATE,BILL_TYPE,TYPE_OF_BILL,BILL_STATUS_ID,IS_BILL_PREPARED, BILL_GROUP_ID,OFF_CODE, BILL_DESC FROM BILL_MAST WHERE OFF_CODE=? AND AQ_MONTH=? AND AQ_YEAR=? AND TYPE_OF_BILL=?) BILL_MAST "
                        + "INNER JOIN G_OFFICE ON BILL_MAST.OFF_CODE=G_OFFICE.OFF_CODE) BILL_MAST "
                        + "ON BILL_GROUP_MASTER.BILL_GROUP_ID=BILL_MAST.BILL_GROUP_ID  WHERE (IS_DELETED='N' OR IS_DELETED IS NULL) OR (IS_DELETED='Y' AND IS_BILL_PREPARED ='Y') ORDER BY DESCRIPTION) BILL_MAST ) BILL_MAST");
                pstmt.setString(1, offCode);
                pstmt.setString(2, offCode);
                pstmt.setInt(3, month);
                pstmt.setInt(4, year);
                pstmt.setString(5, billType);
                System.out.println("billType:" + billType + "  month:" + month);
            }
            rs = pstmt.executeQuery();

            while (rs.next()) {
                BillBean bb = new BillBean();
                bb.setBillgroupId(rs.getString("BILL_GROUP_ID"));
                bb.setBillno(rs.getString("BILL_NO"));
                bb.setBilldesc(rs.getString("BILL_DESC"));
                bb.setBilldate(CommonFunctions.getFormattedOutputDate1(rs.getDate("BILL_DATE")));
                bb.setBilltype(rs.getString("TYPE_OF_BILL"));
                bb.setBillGroupDesc(rs.getString("DESCRIPTION"));
                if (rs.getInt("BILL_STATUS_ID") == 0) {
                    bb.setShowLink("");
                } else if (rs.getInt("BILL_STATUS_ID") == 2 || rs.getInt("BILL_STATUS_ID") == 4 || rs.getInt("BILL_STATUS_ID") == 8) {
                    bb.setShowLink("Y");
                } else if (rs.getInt("BILL_STATUS_ID") > 2 && rs.getInt("BILL_STATUS_ID") != 4 && rs.getInt("BILL_STATUS_ID") != 8) {
                    bb.setShowLink("N");
                } else {
                    bb.setShowLink("");
                }
                bb.setLockBill(rs.getInt("BILL_STATUS_ID"));
                bb.setOnlinebillapproved(rs.getString("ONLINE_BILL_SUBMISSION"));
                if (rs.getString("IS_BILL_PREPARED") != null && !rs.getString("IS_BILL_PREPARED").equals("")) {
                    bb.setIsbillPrepared(rs.getString("IS_BILL_PREPARED"));
                } else {
                    bb.setIsbillPrepared("N");
                }
                billList.add(bb);
            }

            //}
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }

        return billList;
    }

    public ArrayList getBillDetails(String offcode, int month, int year) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList billList = new ArrayList();
        try {
            con = this.dataSource.getConnection();
            pstmt = con.prepareStatement("select billmast.bill_no,billmast.bill_desc,billmast.bill_date,billmast.aq_month,billmast.aq_year,billmast.off_code, billmast.bill_group_desc, "
                    + "billmast.token_no,billmast.token_date,billmast.bill_status_id,billstatus.bill_status from "
                    + "(select * from bill_mast where off_code=? and aq_month=? and aq_year=?)billmast left outer join (select * from g_bill_status)billstatus on billmast.bill_status_id=billstatus.id");
            pstmt.setString(1, offcode);
            pstmt.setInt(2, month);
            pstmt.setInt(3, year);
            rs = pstmt.executeQuery();
            System.out.println("Bill Details");
            while (rs.next()) {
                BillDetail billDetail = new BillDetail();
                billDetail.setOffcode(rs.getString("off_code"));
                billDetail.setBillnumber(rs.getString("bill_no"));
                billDetail.setBilldesc(rs.getString("bill_desc"));
                billDetail.setBilldesc(rs.getString("bill_group_desc"));
                billDetail.setBillStatus(rs.getString("bill_status"));
                billDetail.setBillStatusId(rs.getInt("bill_status_id"));
                billDetail.setBillDate(CommonFunctions.getFormattedInputDate(rs.getDate("bill_date")));
                billDetail.setBillmonth(rs.getInt("aq_month"));
                billDetail.setBillyear(rs.getInt("aq_year"));
                billDetail.setTokenNumber(StringUtils.defaultString(rs.getString("token_no")));
                billDetail.setTokendate(CommonFunctions.getFormattedInputDate(rs.getDate("token_date")));
                billDetail.setPrevTokenNumber(StringUtils.defaultString(rs.getString("previous_token_no")));
                billDetail.setPrevTokendate(CommonFunctions.getFormattedInputDate(rs.getDate("previous_token_date")));
                billList.add(billDetail);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return billList;
    }

    @Override
    public BillDetail getBillDetails(int billno) {
        BillDetail bill = null;
        VouchingServicesDAOImpl vserv = new VouchingServicesDAOImpl();
        Calendar cal = Calendar.getInstance();

        String month = "";
        String billdate = "";
        String periodFrom = "";
        String periodTo = "";
        String ddocode = "";
        String treasuryCode = "";

        int year = 0;
        int billmonth = 0;

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "";
        try {
            con = this.dataSource.getConnection();

            sql = "SELECT OFF_NAME,OFF_DDO,BILL_MAST.DDO_CODE,BILL_NO,BILL_DATE,BILL_DESC,PLAN,SECTOR,MAJOR_HEAD,SUB_MAJOR_HEAD,MINOR_HEAD, "
                    + "            SUB_MINOR_HEAD1,SUB_MINOR_HEAD2,SUB_MINOR_HEAD3,BILL_TYPE,BILL_MAST.TR_CODE,AQ_MONTH,AQ_YEAR,DEMAND_NO,BEN_REF_NO,PREVIOUS_TOKEN_NO,PREVIOUS_TOKEN_DATE,IS_RESUBMITTED,type_of_bill,bill_status_id,bill_group_id, vch_no, vch_date, gross_amt ,ded_amt , pvt_ded_amt FROM ( "
                    + "            SELECT OFF_CODE,BILL_NO,BILL_DATE,BILL_DESC,OFF_DDO,DDO_CODE,PLAN,SECTOR,MAJOR_HEAD,SUB_MAJOR_HEAD,MINOR_HEAD, "
                    + "            SUB_MINOR_HEAD1,SUB_MINOR_HEAD2,SUB_MINOR_HEAD3,BILL_TYPE,TR_CODE,AQ_MONTH,AQ_YEAR,DEMAND_NO,BEN_REF_NO,PREVIOUS_TOKEN_NO,PREVIOUS_TOKEN_DATE,IS_RESUBMITTED, type_of_bill, bill_status_id, bill_group_id, vch_no, vch_date, gross_amt ,ded_amt , pvt_ded_amt  "
                    + "            FROM BILL_MAST WHERE BILL_NO=? ) BILL_MAST "
                    + "            INNER JOIN G_OFFICE ON BILL_MAST.OFF_CODE=G_OFFICE.OFF_CODE";

            ps = con.prepareStatement(sql);
            ps.setInt(1, billno);
            rs = ps.executeQuery();
            if (rs.next()) {

                bill = new BillDetail();
                bill.setBillgroupId(rs.getString("bill_group_id"));
                bill.setHrmsgeneratedRefno(rs.getString("BILL_NO"));
                billdate = CommonFunctions.getFormattedOutputDate3(rs.getDate("BILL_DATE"));
                bill.setHrmsgeneratedRefdate(billdate);
                bill.setBillType("N");
                bill.setBillnumber(rs.getString("BILL_NO"));
                bill.setBillDate(billdate);
                bill.setBilldesc(rs.getString("BILL_DESC"));
                year = rs.getInt("AQ_YEAR");
                billmonth = rs.getInt("AQ_MONTH");
                bill.setBillyear(year);
                bill.setBillmonth(billmonth + 1);
                if (rs.getString("AQ_MONTH") != null) {

                    month = CalendarCommonMethods.getFullMonthAsString((rs.getInt("AQ_MONTH")));
                    cal.set(Calendar.YEAR, year);
                    cal.set(Calendar.MONTH, rs.getInt("AQ_MONTH"));
                    cal.set(Calendar.DATE, 1);
                }
                SimpleDateFormat date_format = new SimpleDateFormat("dd-MMM-yyyy");

                bill.setAgbillTypeId(rs.getString("BILL_TYPE"));
                if (rs.getString("BILL_TYPE").equals("43")) {
                    bill.setSalFromdate("01-JAN-2016");
                } else {
                    bill.setSalFromdate(date_format.format(cal.getTime()));
                }

                periodFrom = bill.getSalFromdate();
                cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                if (rs.getString("BILL_TYPE").equals("43")) {
                    bill.setSalTodate("31-AUG-2017");
                } else {
                    bill.setSalTodate(date_format.format(cal.getTime()));
                }

                periodTo = bill.getSalTodate();
                ddocode = rs.getString("DDO_CODE");
                bill.setDdoccode(ddocode);
                bill.setDemandNumber(rs.getString("DEMAND_NO"));
                bill.setMajorHead(rs.getString("MAJOR_HEAD"));
                bill.setSubMajorHead(rs.getString("SUB_MAJOR_HEAD"));
                bill.setMinorHead(rs.getString("MINOR_HEAD"));
                bill.setSubHead(rs.getString("SUB_MINOR_HEAD1"));
                bill.setDetailHead(rs.getString("SUB_MINOR_HEAD2"));
                bill.setPlanStatus(rs.getString("PLAN"));
                bill.setChargedVoted(rs.getString("SUB_MINOR_HEAD3"));
                bill.setSectorCode(rs.getString("SECTOR"));
                bill.setTypeofBillString(rs.getString("type_of_bill"));
                bill.setVchDt(CommonFunctions.getFormattedOutputDate3(rs.getDate("vch_date")));
                bill.setVchNo(rs.getString("vch_no"));
                bill.setPrevTokenNumber(null);
                bill.setPrevTokendate(null);
                treasuryCode = rs.getString("TR_CODE");
                bill.setTreasuryCode(treasuryCode);
                bill.setBeneficiaryrefno(rs.getString("BEN_REF_NO"));
                if (rs.getString("IS_RESUBMITTED") != null && rs.getString("IS_RESUBMITTED").equals("Y")) {
                    bill.setBillType("R");
                    bill.setPrevTokenNumber(rs.getString("PREVIOUS_TOKEN_NO"));
                    bill.setPrevTokendate(CommonFunctions.getFormattedOutputDate3(rs.getDate("PREVIOUS_TOKEN_DATE")));
                }
                bill.setGrossAmount(Double.valueOf(rs.getInt("gross_amt") + "").longValue() + "");
                int net = rs.getInt("gross_amt") - (rs.getInt("ded_amt") + rs.getInt("pvt_ded_amt"));
                bill.setNetAmount(Double.valueOf(net + "").longValue() + "");
                bill.setBillStatusId(rs.getInt("bill_status_id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, ps);
            DataBaseFunctions.closeSqlObjects(con);
        }

        return bill;
    }

    @Override
    public ArrayList getOBJXMLData(int billId, String treasuryCode, double basicPay, String billdate, String typeofbill) {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        ArrayList objlist = new ArrayList();
        String billtype = "";
        try {
            con = this.dataSource.getConnection();
            billtype = BillBrowserDAOImpl.getBillType(con, billId);
            st = con.createStatement();
            String sql = "";
            if (typeofbill != null) {
                if (typeofbill.equalsIgnoreCase("PAY")) {
                    sql = "select BT_ID,sum(ad_amt) AMT from aq_dtls inner join (select * from AQ_MAST where bill_no=" + billId + ")AQ_MAST "
                            + " on AQ_MAST.aqsl_no= aq_dtls.aqsl_no where ad_type = 'A' and ad_amt > 0 GROUP BY BT_ID";

                    rs = st.executeQuery(sql);

                    boolean head136added = false;
                    ObjectBreakup object = null;

                    while (rs.next()) {
                        object = new ObjectBreakup();
                        object.setHrmsgeneratedRefno(billId);
                        object.setHrmsgeneratedRefdate(billdate);
                        object.setObjectHead(rs.getString("BT_ID"));
                        if (billtype.equals("21")) {
                            if (object.getObjectHead().equals("921")) {
                                head136added = true;
                                object.setObjectHeadwiseAmount(basicPay + rs.getDouble("AMT"));
                            } else {
                                object.setObjectHeadwiseAmount(rs.getDouble("AMT"));
                            }
                        } else {
                            if (object.getObjectHead().equals("136")) {
                                head136added = true;
                                object.setObjectHeadwiseAmount(basicPay + rs.getDouble("AMT"));
                            } else {
                                object.setObjectHeadwiseAmount(rs.getDouble("AMT"));
                            }
                        }

                        object.setTreasuryCode(treasuryCode);
                        objlist.add(object);
                    }
                    if (head136added == false) {
                        object = new ObjectBreakup();
                        object.setHrmsgeneratedRefno(billId);
                        object.setHrmsgeneratedRefdate(billdate);
                        if (billtype.equals("21")) {
                            object.setObjectHead("921");
                        } else {
                            object.setObjectHead("136");
                        }
                        object.setObjectHeadwiseAmount(basicPay);
                        object.setTreasuryCode(treasuryCode);
                        objlist.add(object);
                    }

                } else if (typeofbill.equalsIgnoreCase("OT")) {
                    sql = "SELECT sum(TOTAL) AMT,sum(DA) DA FROM AQ_MAST_OT WHERE bill_no=" + billId;
                    rs = st.executeQuery(sql);
                    ObjectBreakup object = null;

                    if (rs.next()) {
                        object = new ObjectBreakup();
                        object.setHrmsgeneratedRefno(billId);
                        object.setHrmsgeneratedRefdate(billdate);
                        object.setObjectHead("156");
                        object.setObjectHeadwiseAmount(rs.getDouble("DA"));
                        object.setTreasuryCode(treasuryCode);
                        objlist.add(object);

                        object = new ObjectBreakup();
                        object.setHrmsgeneratedRefno(billId);
                        object.setHrmsgeneratedRefdate(billdate);
                        object.setObjectHead("136");
                        object.setObjectHeadwiseAmount((rs.getInt("AMT") - rs.getDouble("DA")));
                        object.setTreasuryCode(treasuryCode);
                        objlist.add(object);
                    }

                } else if (typeofbill.equalsIgnoreCase("ARREAR")) {
                    sql = "select sum(arrear_pay) arrear_pay from arr_mast mast where bill_no=" + billId;
                    rs = st.executeQuery(sql);
                    ObjectBreakup object = null;

                    if (rs.next()) {
                        /*
                         object = new ObjectBreakup();
                         object.setHrmsgeneratedRefno(billId);
                         object.setHrmsgeneratedRefdate(billdate);
                         object.setObjectHead("156");
                         object.setObjectHeadwiseAmount(rs.getDouble("DA"));
                         object.setTreasuryCode(treasuryCode);
                         objlist.add(object);
                         */
                        object = new ObjectBreakup();
                        object.setHrmsgeneratedRefno(billId);
                        object.setHrmsgeneratedRefdate(billdate);
                        object.setObjectHead("855");

                        object.setObjectHeadwiseAmount(rs.getInt("arrear_pay"));
                        object.setTreasuryCode(treasuryCode);
                        objlist.add(object);
                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, st);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return objlist;
    }

    @Override
    public ArrayList getBTXMLData(int billId, String treasuryCode, String billdate, String typeofbill) {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        ArrayList bytranlist = new ArrayList();
        String billtype = "";
        try {
            con = this.dataSource.getConnection();
            billtype = BillBrowserDAOImpl.getBillType(con, billId);

            st = con.createStatement();
            if (typeofbill.equalsIgnoreCase("PAY")) {
                rs = st.executeQuery("SELECT BT_ID,SUM(AD_AMT) AD_AMT FROM (SELECT AQSL_NO FROM AQ_MAST WHERE BILL_NO=" + billId + ")AQ_MAST "
                        + "INNER JOIN (SELECT * FROM AQ_DTLS WHERE AD_TYPE='D'  AND AD_AMT >0 and SCHEDULE != 'PVTL' and SCHEDULE != 'PVTD')AQ_DTLS ON AQ_MAST.AQSL_NO = AQ_DTLS.AQSL_NO GROUP BY BT_ID");
                BytransferDetails bytran = null;
                while (rs.next()) {
                    bytran = new BytransferDetails();
                    bytran.setHrmsgeneratedRefno(billId);
                    bytran.setHrmsgeneratedRefdate(billdate);
                    bytran.setBtserialno(rs.getString("BT_ID"));
                    bytran.setBytransferType("Ag Bt");
                    bytran.setAmount(rs.getDouble("AD_AMT"));
                    bytran.setTreasuryCode(treasuryCode);

                    bytranlist.add(bytran);
                }
            } else if (typeofbill.equalsIgnoreCase("ARREAR")) {
                rs = st.executeQuery("select sum(inctax) inctax, sum(cpf_head) cpf_head, sum(pt) pt from arr_mast where bill_no=" + billId);

                BytransferDetails bytran = null;
                if (rs.next()) {
                    bytran = new BytransferDetails();
                    bytran.setHrmsgeneratedRefno(billId);
                    bytran.setHrmsgeneratedRefdate(billdate);
                    bytran.setBtserialno("58816");
                    bytran.setBytransferType("Ag Bt");
                    bytran.setAmount(rs.getInt("inctax"));
                    bytran.setTreasuryCode(treasuryCode);

                    bytranlist.add(bytran);

                    bytran = new BytransferDetails();
                    bytran.setHrmsgeneratedRefno(billId);
                    bytran.setHrmsgeneratedRefdate(billdate);
                    bytran.setBtserialno("57740");
                    bytran.setBytransferType("Ag Bt");
                    bytran.setAmount(rs.getInt("cpf_head"));
                    bytran.setTreasuryCode(treasuryCode);

                    bytranlist.add(bytran);

                    bytran = new BytransferDetails();
                    bytran.setHrmsgeneratedRefno(billId);
                    bytran.setHrmsgeneratedRefdate(billdate);
                    bytran.setBtserialno("3043");
                    bytran.setBytransferType("Ag Bt");
                    bytran.setAmount(rs.getInt("pt"));
                    bytran.setTreasuryCode(treasuryCode);

                    bytranlist.add(bytran);

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, st);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return bytranlist;
    }

    public String getBTIDforCPF() throws Exception {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        String acc = "";
        try {
            con = this.dataSource.getConnection();
            st = con.createStatement();
            rs = st.executeQuery("SELECT BT_ID FROM G_AD_LIST WHERE AD_CODE_NAME='CPF'");
            if (rs.next()) {
                acc = rs.getString("BT_ID");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, st);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return acc;
    }

    @Override
    public ArrayList getNPSXMLData(int billId, String billdate, int monthasNumber, int year, String typeofbill) {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        String empId = "";
        ArrayList npslist = new ArrayList();
        int tot = 0;
        int firstTime = 0;
        //VouchingServices vserv= new VouchingServices();    

        NPSDetails nps = null;
        try {
            String query = "SELECT DDO_REG_NO FROM G_OFFICE INNER JOIN (SELECT OFF_CODE FROM BILL_MAST WHERE BILL_NO=" + billId + ")BILL_MAST ON G_OFFICE.OFF_CODE=BILL_MAST.OFF_CODE";
            con = this.dataSource.getConnection();
            st = con.createStatement();
            String ddoregno = "";
            rs = st.executeQuery(query);
            if (rs.next()) {
                ddoregno = rs.getString("DDO_REG_NO");
            }

            st = con.createStatement();
            String btidforcpf = getBTIDforCPF();
            if (typeofbill.equalsIgnoreCase("PAY")) {

                rs = st.executeQuery("SELECT EMP_ID,CUR_BASIC,EMP_NAME,GPF_NO,DOB,DOS,REF_DESC,AD_AMT,DED_TYPE,AD_CODE FROM ( "
                        + "SELECT EMP_CODE,AQSL_NO,EMP_NAME,CUR_BASIC FROM AQ_MAST WHERE ACCT_TYPE='PRAN' AND BILL_NO=" + billId + ") AQ_MAST "
                        + "INNER JOIN (SELECT * FROM AQ_DTLS WHERE (AD_CODE='CPF' OR AD_CODE='NPSL' OR AD_CODE='DA' OR AD_CODE='GP' OR AD_CODE='PPAY') AND AQ_MONTH=" + (monthasNumber - 1) + " AND AQ_YEAR=" + year + ") AQ_DTLS ON AQ_MAST.AQSL_NO=AQ_DTLS.AQSL_NO "
                        + "INNER JOIN EMP_MAST ON AQ_MAST.EMP_CODE=EMP_MAST.EMP_ID ORDER BY EMP_ID,DED_TYPE");

                empId = "";
                tot = 0;

                boolean npsbeanAlreadyAdded = true;
                firstTime = 0;
                while (rs.next()) {
                    if (empId.equals(rs.getString("EMP_ID"))) {
                        String ded_type = rs.getString("DED_TYPE");
                        if (rs.getString("AD_CODE").equals("CPF")) {
                            nps.setSc(rs.getString("AD_AMT"));
                            nps.setGc(rs.getString("AD_AMT"));
                        } else if (rs.getString("AD_CODE").equals("DA")) {
                            nps.setDa(rs.getDouble("AD_AMT"));
                        } else if (rs.getString("AD_CODE").equals("GP")) {
                            nps.setGp(rs.getDouble("AD_AMT"));
                        } else if (rs.getString("AD_CODE").equals("PPAY")) {
                            nps.setPpay(rs.getDouble("AD_AMT"));
                        }
                        if (ded_type != null && !ded_type.equals("") && ded_type.equals("L")) {
                            nps.setInstAmt(rs.getDouble("AD_AMT"));
                        }
                    } else if (!empId.equals(rs.getString("EMP_ID"))) {
                        if (firstTime == 0) {
                            firstTime = 1;
                            nps = new NPSDetails();
                            npsbeanAlreadyAdded = false;
                        } else {
                            if (nps.getGc() != null && !nps.getGc().equals("")) {
                                if (Integer.parseInt(nps.getGc()) > 0 || nps.getInstAmt() > 0) {
                                    npslist.add(nps);
                                }
                            }
                            npsbeanAlreadyAdded = true;
                            nps = new NPSDetails();
                            tot = 0;
                            npsbeanAlreadyAdded = false;
                        }
                        nps.setContType("R");
                        empId = rs.getString("EMP_ID");
                        nps.setHrmsgeneratedRefno(billId);
                        nps.setHrmsgeneratedRefdate(billdate);
                        nps.setPran(StringUtils.trim(rs.getString("GPF_NO")));
                        nps.setNameofSubscrib(rs.getString("EMP_NAME"));
                        nps.setBasic(rs.getDouble("CUR_BASIC"));
                        nps.setBtserialno(btidforcpf);
                        nps.setPaymonth(monthasNumber + "");
                        nps.setPayYear(year + "");
                        nps.setDdoRegno(ddoregno);
                        if (rs.getString("AD_CODE").equals("CPF")) {
                            nps.setSc(rs.getString("AD_AMT"));
                            nps.setGc(rs.getString("AD_AMT"));
                        } else if (rs.getString("AD_CODE").equals("DA")) {
                            nps.setDa(rs.getDouble("AD_AMT"));
                        } else if (rs.getString("AD_CODE").equals("PPAY")) {
                            nps.setPpay(rs.getDouble("AD_AMT"));
                        } else if (rs.getString("AD_CODE").equals("GP")) {
                            nps.setGp(rs.getDouble("AD_AMT"));
                        } else if (rs.getString("DED_TYPE") != null && !rs.getString("DED_TYPE").equals("") && rs.getString("DED_TYPE").equals("L")) {
                            nps.setInstAmt(rs.getDouble("AD_AMT"));
                        }
                    }
                }
                if (npsbeanAlreadyAdded == false && (Integer.parseInt(nps.getGc()) > 0 || nps.getInstAmt() > 0)) {
                    npslist.add(nps);
                }
            } else if (typeofbill.equalsIgnoreCase("ARREAR")) {

                rs = st.executeQuery("select a.emp_id,EMP_NAME,GPF_NO,DOB,DOS,cpf_head, "
                        + "arrear_pay from arr_mast a, emp_mast b where a.emp_id=b.emp_id and bill_no=" + billId + " and cpf_head>0");
                while (rs.next()) {
                    nps = new NPSDetails();
                    nps.setContType("A");
                    empId = rs.getString("emp_id");
                    nps.setHrmsgeneratedRefno(billId);
                    nps.setHrmsgeneratedRefdate(billdate);
                    nps.setPran(StringUtils.trim(rs.getString("GPF_NO")));
                    nps.setNameofSubscrib(rs.getString("EMP_NAME"));
                    nps.setBasic(rs.getInt("arrear_pay"));
                    nps.setBtserialno(btidforcpf);
                    nps.setPaymonth(monthasNumber + "");
                    nps.setPayYear(year + "");
                    nps.setDdoRegno(ddoregno);
                    nps.setSc(rs.getString("cpf_head"));
                    nps.setGc(rs.getString("cpf_head"));

                    npslist.add(nps);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, st);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return npslist;

    }

    public static String getBillType(Connection con, int billId) throws Exception {
        Statement st = null;
        ResultSet rs = null;
        String billtype = "";
        try {
            st = con.createStatement();
            rs = st.executeQuery("SELECT BILL_TYPE FROM BILL_MAST WHERE BILL_NO=" + billId);
            if (rs.next()) {
                billtype = rs.getString("BILL_TYPE");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, st);
        }
        return billtype;
    }

    @Override
    public int getbillsubmissionCount(int billno) {
        int count = 0;
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            con = this.dataSource.getConnection();
            String sqlQuery = "SELECT COUNT(*) cnt FROM BILL_STATUS_HISTORY WHERE STATUS_ID=2 AND BILL_ID=" + billno;
            st = con.createStatement();
            rs = st.executeQuery(sqlQuery);
            if (rs.next()) {
                count = rs.getInt("cnt");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, st);
            DataBaseFunctions.closeSqlObjects(con);
        }

        return count;
    }

    @Override
    public void updateBillStatus(int billno, int billStatusId) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = this.dataSource.getConnection();
            ps = con.prepareStatement("UPDATE BILL_MAST SET GROSS_AMT = getbilltotgross_arrear(BILL_NO),DED_AMT = getbilltotded_arrear(BILL_NO) WHERE BILL_NO=?");
            ps.setInt(1, billno);
            ps.execute();
            ps = con.prepareStatement("UPDATE BILL_MAST SET BILL_STATUS_ID=? WHERE BILL_NO=?");
            ps.setInt(1, billStatusId);
            ps.setInt(2, billno);
            ps.execute();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(ps);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    @Override
    public void updateBillHistory(int billno, String submissionDate) {
        Connection con = null;
        PreparedStatement ps = null;
        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            Date outputDate = dateFormat.parse(submissionDate);

            con = this.dataSource.getConnection();
            ps = con.prepareStatement("INSERT INTO BILL_STATUS_HISTORY (BILL_ID,HISTORY_DATE,STATUS_ID,REMARK) VALUES (?,?,?,?)");
            ps.setInt(1, billno);
            ps.setTimestamp(2, new Timestamp(outputDate.getTime()));
            ps.setInt(3, 2);
            ps.setString(4, "ONLINE BILL SUBMITTED");
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(ps);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    @Override
    public ArrayList getPayBillList(int year, int month, String treasuryCode) {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        ArrayList billList = new ArrayList();

        try {
            con = dataSource.getConnection();

            pstmt = con.prepareStatement("SELECT aq_month,aq_year,BILL_NO,major_head,sub_major_head,minor_head,sub_minor_head1,sub_minor_head2,sub_minor_head3,VCH_NO,VCH_DATE,extract (year from vch_date) vch_year, EXTRACT(MONTH FROM vch_date) vch_month,EXTRACT(DAY FROM vch_date) vch_day,ag_treasury_code,DDO_CODE FROM BILL_MAST bill "
                    + "inner join g_treasury treasury on bill.tr_code=treasury.tr_code WHERE extract (year from vch_date)=? AND extract (month from vch_date)=? AND bill.TR_CODE=?  AND bill.BILL_STATUS_ID=7");
            pstmt.setInt(1, year);
            pstmt.setInt(2, (month + 1));
            pstmt.setString(3, treasuryCode);

            rs = pstmt.executeQuery();

            String financialYear = "";
            if (month > 2) {
                financialYear = year + "-" + (year + 1);
            } else {
                financialYear = (year - 1) + "-" + year;
            }
            SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
            while (rs.next()) {
                BillBean bb = new BillBean();
                bb.setBillMonth(rs.getInt("aq_month"));
                bb.setBillYear(rs.getInt("aq_year"));
                bb.setAdjYear(rs.getInt("vch_year"));
                bb.setBillno(rs.getString("BILL_NO"));
                bb.setVoucherno(rs.getString("VCH_NO"));
                if (rs.getDate("VCH_DATE") != null) {
                    bb.setVoucherdate(DATE_FORMAT.format(rs.getDate("VCH_DATE")));
                }
                bb.setVouchermonth(rs.getString("vch_month"));
                bb.setFinyear(financialYear);
                bb.setTreasurycode(rs.getString("ag_treasury_code"));
                bb.setDdocode(rs.getString("DDO_CODE"));
                bb.setMajorhead(rs.getString("major_head"));
                bb.setVoucherDay(rs.getInt("vch_day"));
                billList.add(bb);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return billList;
    }

    @Override
    public int getNewBillYear(String offCode) {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        int newYear = 0;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("select max(aq_year) maxyear,count(*) cnt from BILL_MAST WHERE OFF_CODE=?");
            pstmt.setString(1, offCode);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                if (rs.getInt("cnt") > 0) {
                    newYear = rs.getInt("maxyear");
                } else {
                    newYear = Calendar.getInstance().get(Calendar.YEAR);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return newYear;
    }

    @Override
    public int getNewBillMonth(String offCode, int year) {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        int newMonth = 0;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT MAX(AQ_MONTH) maxmonth FROM BILL_MAST WHERE OFF_CODE=? AND AQ_YEAR=?");
            pstmt.setString(1, offCode);
            pstmt.setInt(2, year);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                newMonth = rs.getInt("maxmonth");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return newMonth;
    }

    @Override
    public GlobalBillStatus getBillProcessStatus() {
        String GLOBAL_VARIABLE_NAME = "STOP_BILL_PROCESS";
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        GlobalBillStatus gbs = new GlobalBillStatus();
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT GLOBAL_VAR_VALUE,MESSAGE FROM HRMS_CONFIG WHERE GLOBAL_VAR_NAME=?");
            pstmt.setString(1, GLOBAL_VARIABLE_NAME);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                gbs.setGlobalVarValue(rs.getString("GLOBAL_VAR_VALUE"));
                gbs.setMessage(rs.getString("MESSAGE"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return gbs;
    }

    @Override
    public String getMonthName(int month) {
        String monthString = "";
        if (month == 0) {
            monthString = "January";
        } else if (month == 1) {
            monthString = "February";
        } else if (month == 2) {
            monthString = "March";
        } else if (month == 3) {
            monthString = "April";
        } else if (month == 4) {
            monthString = "May";
        } else if (month == 5) {
            monthString = "June";
        } else if (month == 6) {
            monthString = "July";
        } else if (month == 7) {
            monthString = "August";
        } else if (month == 8) {
            monthString = "September";
        } else if (month == 9) {
            monthString = "October";
        } else if (month == 10) {
            monthString = "November";
        } else if (month == 11) {
            monthString = "December";
        }
        return monthString;
    }

    @Override
    public ArrayList getBillGroupList(String offCode, String curSpc) {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        ArrayList empBillGrpList = new ArrayList();
        boolean privFound = false;
        try {
            System.out.println("curSpc:" + curSpc);
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT BILL_GROUP_ID, DESCRIPTION, PLAN, SECTOR, MAJOR_HEAD,SUB_MAJOR_HEAD,MINOR_HEAD,SUB_MINOR_HEAD1,SUB_MINOR_HEAD2,SUB_MINOR_HEAD3,POST_CLASS,DEMAND_NO from ("
                    + "SELECT BILL_GRP_ID FROM BILL_GROUP_PRIVILAGE WHERE SPC=?) BILL_GROUP_PRIVILAGE "
                    + "INNER JOIN ( "
                    + "SELECT BILL_GROUP_ID, DESCRIPTION, PLAN, SECTOR, MAJOR_HEAD,SUB_MAJOR_HEAD,MINOR_HEAD,SUB_MINOR_HEAD1,SUB_MINOR_HEAD2,SUB_MINOR_HEAD3,POST_CLASS,DEMAND_NO from BILL_GROUP_MASTER "
                    + "WHERE OFF_CODE=? AND (IS_DELETED IS  NULL OR IS_DELETED='N')) BILL_GROUP_MASTER ON BILL_GROUP_PRIVILAGE.BILL_GRP_ID = BILL_GROUP_MASTER.BILL_GROUP_ID ORDER BY DESCRIPTION");
            pstmt.setString(1, curSpc);
            pstmt.setString(2, offCode);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                BillAttr billattr = new BillAttr();
                billattr.setBillgroupId(rs.getString("BILL_GROUP_ID"));
                billattr.setBillDesc(rs.getString("DESCRIPTION"));
                billattr.setChartofAcc(StringUtils.defaultString(rs.getString("DEMAND_NO")) + "-" + StringUtils.defaultString(rs.getString("MAJOR_HEAD")) + "-" + StringUtils.defaultString(rs.getString("SUB_MAJOR_HEAD")) + "-" + StringUtils.defaultString(rs.getString("MINOR_HEAD")) + "-" + StringUtils.defaultString(rs.getString("SUB_MINOR_HEAD1")) + "-" + StringUtils.defaultString(rs.getString("SUB_MINOR_HEAD2")) + "-" + StringUtils.defaultString(rs.getString("PLAN")) + "-" + StringUtils.defaultString(rs.getString("SUB_MINOR_HEAD3")) + "-" + StringUtils.defaultString(rs.getString("SECTOR")));
                privFound = true;
                empBillGrpList.add(billattr);
            }
            if (privFound == false) {
                pstmt = con.prepareStatement("SELECT BILL_GROUP_ID, DESCRIPTION, PLAN, SECTOR, MAJOR_HEAD,SUB_MAJOR_HEAD,MINOR_HEAD,SUB_MINOR_HEAD1,SUB_MINOR_HEAD2,SUB_MINOR_HEAD3,POST_CLASS,DEMAND_NO from BILL_GROUP_MASTER "
                        + "WHERE OFF_CODE=? AND (IS_DELETED IS  NULL OR IS_DELETED='N') ORDER BY DESCRIPTION");
                pstmt.setString(1, offCode);
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    BillAttr billattr = new BillAttr();
                    billattr.setBillgroupId(rs.getString("BILL_GROUP_ID"));
                    billattr.setBillDesc(rs.getString("DESCRIPTION"));
                    billattr.setChartofAcc(StringUtils.defaultString(rs.getString("DEMAND_NO")) + "-" + StringUtils.defaultString(rs.getString("MAJOR_HEAD")) + "-" + StringUtils.defaultString(rs.getString("SUB_MAJOR_HEAD")) + "-" + StringUtils.defaultString(rs.getString("MINOR_HEAD")) + "-" + StringUtils.defaultString(rs.getString("SUB_MINOR_HEAD1")) + "-" + StringUtils.defaultString(rs.getString("SUB_MINOR_HEAD2")) + "-" + StringUtils.defaultString(rs.getString("PLAN")) + "-" + StringUtils.defaultString(rs.getString("SUB_MINOR_HEAD3")) + "-" + StringUtils.defaultString(rs.getString("SECTOR")));
                    empBillGrpList.add(billattr);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return empBillGrpList;
    }

    @Override
    public BillAttr[] createBillFromBillGroup(int mAqMonth, int mAqYear, String[] billGroupId, String processDt, int priority, String billType, String moffcode) {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt2 = null;
        Date processDate = null;
        Date fyStatrtDate = null;
        DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int task[] = null;
        int taskid = 0;
        String regularOrcontractualBill = "";
        BillAttr[] billStatus = null;
        ArrayList billStatusA = new ArrayList();
        try {
            con = dataSource.getConnection();
            System.out.println("mAqYear"+mAqYear);
             System.out.println("mAqMonth"+mAqMonth);
              System.out.println("year"+year);
               System.out.println("mAqMonth"+mAqMonth);
                System.out.println("billType"+billType);
            if ((year == mAqYear + 1) || (year == mAqYear && mAqMonth <= month)) {
                task = new int[billGroupId.length];
                System.out.println("billGroupId"+billGroupId);
                for (int i = 0; i < billGroupId.length; i++) {
                    BillAttr billAttr = new BillAttr();
                    billAttr.setBillgroupId(billGroupId[i]);
                    pstmt = con.prepareStatement("SELECT * FROM BILL_MAST WHERE BILL_GROUP_ID=? AND AQ_MONTH=? AND AQ_YEAR=? and type_of_bill=?");
                    pstmt.setBigDecimal(1, new BigDecimal(billGroupId[i]));
                    pstmt.setInt(2, mAqMonth);
                    pstmt.setInt(3, mAqYear);
                    pstmt.setString(4,billType);
                    rs = pstmt.executeQuery();
                    boolean isDuplicateProcess = true;
                    if (rs.next()) {
                        isDuplicateProcess = true;
                        //System.out.println("Trying to submit through Refresh Duplicate Entry "+bean.getBillgrpId()[i]);
                    } else {
                        isDuplicateProcess = false;
                    }

                    if (!isDuplicateProcess) {
                        System.out.println("^^^^^^^^");
                        processDate = (Date) formatter.parse(processDt);
                        fyStatrtDate = (Date) formatter.parse("1-APR-" + mAqYear);
                        pstmt = con.prepareStatement("SELECT BILL_TYPE FROM G_SECTION WHERE SECTION_ID IN (SELECT DISTINCT SECTION_ID FROM BILL_SECTION_MAPPING WHERE BILL_GROUP_ID=?)");
                        pstmt.setBigDecimal(1, new BigDecimal(billGroupId[i]));
                        rs = pstmt.executeQuery();
                        if (rs.next()) {
                            regularOrcontractualBill = rs.getString("BILL_TYPE");
                        }
                        taskid = isContainsKey(moffcode, mAqMonth, mAqYear, billGroupId[i], processDt, billType, 0, regularOrcontractualBill, priority);
                        task[i] = taskid;

                        if (taskid == 0) {
                            billAttr.setMsg("Bill Cannot be Processed. Because you have not assigned section to bill group list or you have not mapped any employee into section. ");
                        } else {
                            billAttr.setMsg("Bill is under Process");
                        }

                    }
                    billStatusA.add(billAttr);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return billStatus;
    }

    @Override
    public BillAttr[] createBillFromBillGroupForArrear(int fromMonth, int fromYear, int toMonth, int toYear, String[] billGroupId, String processDt, int priority, String billType, String moffcode) {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt2 = null;
        Date processDate = null;
        Date fyStatrtDate = null;
        DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int task[] = null;
        int taskid = 0;
        BillAttr[] billStatus = null;
        ArrayList billStatusA = new ArrayList();
        try {
            con = dataSource.getConnection();

            task = new int[billGroupId.length];
            for (int i = 0; i < billGroupId.length; i++) {
                BillAttr billAttr = new BillAttr();
                billAttr.setBillgroupId(billGroupId[i]);

                pstmt = con.prepareStatement("SELECT * FROM BILL_MAST WHERE BILL_GROUP_ID=? AND from_month=? AND from_year=? AND to_month=? AND to_year=?");
                pstmt.setBigDecimal(1, new BigDecimal(billGroupId[i]));
                pstmt.setInt(2, fromMonth);
                pstmt.setInt(3, fromYear);
                pstmt.setInt(4, toMonth);
                pstmt.setInt(5, toYear);
                rs = pstmt.executeQuery();
                boolean isDuplicateProcess = true;
                if (rs.next()) {
                    isDuplicateProcess = true;
                    //System.out.println("Trying to submit through Refresh Duplicate Entry "+bean.getBillgrpId()[i]);
                } else {
                    isDuplicateProcess = false;
                }

                if (!isDuplicateProcess) {

                    processDate = (Date) formatter.parse(processDt);

                    taskid = isContainsKeyForArrear(moffcode, billGroupId[i], processDt, billType, 0, priority, fromMonth, fromYear, toMonth, toYear);
                    task[i] = taskid;

                    if (taskid == 0) {
                        billAttr.setMsg("Bill Cannot be Processed. Because you have not assigned section to bill group list or you have not mapped any employee into section. ");
                    } else {
                        billAttr.setMsg("Bill is under Process");
                    }

                }
                billStatusA.add(billAttr);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return billStatus;
    }

    public int getBillPriority(String offCode) {
        int priority = 0;
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT PAYBILL_PRIORITY FROM G_OFFICE WHERE OFF_CODE=?");
            pstmt.setString(1, offCode);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                priority = rs.getInt("PAYBILL_PRIORITY");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return priority;
    }

    public int isContainsKeyForArrear(String moffcode, String bgId, String processDate, String billType, int billId, int priority, int fromMonth, int fromYear, int toMonth, int toYear) throws Exception {
        Connection con = null;
        PreparedStatement stamt = null;
        ResultSet res = null;
        ResultSet res1 = null;
        int containsKey = 0;
        int taskid = 0;
        try {
            Calendar c = Calendar.getInstance();
            int aqyear = c.get(Calendar.YEAR);
            int aqmonth = c.get(Calendar.MONTH);
            con = dataSource.getConnection();
            /**
             * ******************THIS QUERY RETRIEVES ALL EMPLOYEES OF AN
             * OFFICE INCLUDING EMPLOYEES RELIEVED IN CURRENT
             * MONTH******************
             */
            String query = "";

            query = "SELECT TASK_ID FROM PAYBILL_TASK where OFF_CODE=?  AND BILL_GROUP_ID=? AND bill_type=?";
            stamt = con.prepareStatement(query);
            stamt.setString(1, moffcode);
            stamt.setBigDecimal(2, new BigDecimal(bgId));
            stamt.setString(3, billType);

            res = stamt.executeQuery();
            if (res.next()) {
                taskid = res.getInt("TASK_ID");
                containsKey = taskid;
            } else {
                containsKey = 0;
            }

            if (containsKey == 0) {
                if (billId == 0) {
                    billId = createBillIdForArrear(moffcode, bgId, processDate, billType, aqmonth, aqyear, fromMonth, fromYear, toMonth, toYear);
                    System.out.println("billId===" + billId);
                }
                stamt = con.prepareStatement("select COALESCE(max(task_id),0)+1 taskid FROM paybill_task");
                res = stamt.executeQuery();
                taskid = 0;
                if (res.next()) {
                    taskid = res.getInt("TASKID");
                }

                stamt = con.prepareStatement("INSERT INTO PAYBILL_TASK (TASK_ID, OFF_CODE, BILL_ID, BILL_GROUP_ID, PRIORITY, bill_type, aq_month, aq_year) VALUES (?,?,?,?,?,?,?,?)");
                stamt.setInt(1, taskid);
                stamt.setString(2, moffcode);
                stamt.setInt(3, billId);
                stamt.setBigDecimal(4, new BigDecimal(bgId));
                stamt.setInt(5, priority);
                stamt.setString(6, billType);
                stamt.setInt(7, 0);
                stamt.setInt(8, 0);
                stamt.executeUpdate();

            }
        } catch (SQLException sqe) {
            sqe.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(res1);
            DataBaseFunctions.closeSqlObjects(res, stamt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return taskid;
    }

    public int isContainsKey(String moffcode, int aqMonth, int aqYear, String bgId, String processDate, String billType, int billId, String regularOrcontractualBill, int priority) throws Exception {
        Connection con = null;
        Statement stamt = null;
        ResultSet res = null;
        ResultSet res1 = null;
        int containsKey = 0;
        int taskid = 0;
        String employeeQuery = "";
        try {
            con = dataSource.getConnection();
            /**
             * ******************THIS QUERY RETRIEVES ALL EMPLOYEES OF AN
             * OFFICE INCLUDING EMPLOYEES RELIEVED IN CURRENT
             * MONTH******************
             */
            if (regularOrcontractualBill.equalsIgnoreCase("REGULAR")) {
                employeeQuery = "SELECT COUNT(*) CNT FROM( "
                        + "SELECT OFF_CODE, SECTION_POST_MAPPING.SPC,EMP_ID, NAME,SECTION_ID FROM ( "
                        + "SELECT OFF_CODE, SPC,EMP_ID, NAME    FROM "
                        + " (SELECT '" + moffcode + "'::TEXT OFF_CODE ,G_SPC.SPC,EMP_MAST.EMP_ID,ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME,L_NAME], ' ')||' / '||G_SPC.SPN AS NAME "
                        + " FROM (SELECT * FROM G_SPC WHERE OFF_CODE ='" + moffcode + "' AND (IFUCLEAN!='Y' OR IFUCLEAN IS NULL))G_SPC "
                        + " LEFT OUTER JOIN "
                        + " (SELECT EMP_ID,CUR_SPC,GPF_NO,INITIALS,F_NAME,M_NAME,L_NAME FROM EMP_MAST WHERE CUR_OFF_CODE ='" + moffcode + "' AND (DEP_CODE='02' OR DEP_CODE='05') AND CUR_SPC IS NOT NULL) EMP_MAST "
                        + " ON  G_SPC.SPC = EMP_MAST.CUR_SPC "
                        + " INNER JOIN SECTION_POST_MAPPING ON G_SPC.SPC = SECTION_POST_MAPPING.SPC ORDER BY F_NAME) AS TAB1"
                        + " UNION "
                        + " (SELECT '" + moffcode + "'::TEXT OFF_CODE ,G_SPC.SPC,EMP_MAST.EMP_ID,ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME,L_NAME], ' ')||' / '||G_SPC.SPN AS NAME "
                        + " FROM"
                        + " (SELECT SPC,EMP_ID FROM EMP_RELIEVE WHERE SUBSTR(SPC,0,13)='" + moffcode + "' AND TO_CHAR(RLV_DATE,'mm')='" + aqMonth + "' AND TO_CHAR(RLV_DATE,'yyyy')='" + aqYear + "' AND ((TO_CHAR(RLV_DATE,'dd') || RLV_TIME) != '01FN')) TMPRLV"
                        + " INNER JOIN "
                        + " (SELECT EMP_ID,CUR_SPC,GPF_NO,INITIALS,F_NAME,M_NAME,L_NAME FROM EMP_MAST) EMP_MAST "
                        + " ON TMPRLV.EMP_ID=EMP_MAST.EMP_ID"
                        + " LEFT OUTER JOIN"
                        + " (SELECT * FROM G_SPC WHERE OFF_CODE ='" + moffcode + "' AND (IFUCLEAN!='Y' OR IFUCLEAN IS NULL)) G_SPC "
                        + " ON TMPRLV.SPC = G_SPC.SPC) ) AS COMPLETE_QUERY "
                        + " LEFT OUTER JOIN SECTION_POST_MAPPING ON COMPLETE_QUERY.SPC=SECTION_POST_MAPPING.SPC ) TEMP "
                        + " INNER JOIN (SELECT * FROM BILL_SECTION_MAPPING WHERE BILL_GROUP_ID IN (" + bgId + ")) BILL_SECTION_MAPPING ON TEMP.SECTION_ID=BILL_SECTION_MAPPING.SECTION_ID ";
            } else if (regularOrcontractualBill.equalsIgnoreCase("CONTRACTUAL")) {
                employeeQuery = " SELECT COUNT(SPC)CNT FROM ( "
                        + " SELECT SECTION_ID,BILL_GROUP_ID FROM BILL_SECTION_MAPPING WHERE BILL_GROUP_ID IN (" + bgId + ")) AS TEMP "
                        + " INNER JOIN SECTION_POST_MAPPING ON TEMP.SECTION_ID=SECTION_POST_MAPPING.SECTION_ID";
            }

            stamt = con.createStatement();
            String query = "";

            query = "SELECT TASK_ID FROM PAYBILL_TASK where OFF_CODE='" + moffcode + "' AND AQ_MONTH=" + aqMonth + " AND AQ_YEAR=" + aqYear + " AND BILL_GROUP_ID='" + bgId + "'";

            res = stamt.executeQuery(query);
            if (res.next()) {
                taskid = res.getInt("TASK_ID");
                containsKey = taskid;
            } else {
                containsKey = 0;
            }
            if (containsKey == 0) {
                if (billId == 0) {
                    billId = createBillId(moffcode, bgId, processDate, billType, aqMonth, aqYear);
                }
                stamt = con.createStatement();

                res = stamt.executeQuery(employeeQuery);
                int totalNoOfEmployee = 0;
                if (res.next()) {
                    totalNoOfEmployee = res.getInt("CNT");
                }
                if (totalNoOfEmployee > 0) {
                    stamt = con.createStatement();
                    res = stamt.executeQuery("select COALESCE(max(task_id),0)+1 taskid FROM paybill_task");
                    taskid = 0;
                    if (res.next()) {
                        taskid = res.getInt("TASKID");
                    }
                    stamt = con.createStatement();
                    stamt.execute("INSERT INTO PAYBILL_TASK (TASK_ID,OFF_CODE,AQ_MONTH,AQ_YEAR,TOTAL_AQ,BILL_ID,BILL_GROUP_ID,PRIORITY, bill_type) VALUES (" + taskid + ",'" + moffcode + "'," + aqMonth + "," + aqYear + "," + totalNoOfEmployee + "," + billId + ",'" + bgId + "'," + priority + ", '" + billType + "')");
                } else {
                    taskid = 0;
                }
            }
        } catch (SQLException sqe) {
            sqe.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(res1);
            DataBaseFunctions.closeSqlObjects(res, stamt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return taskid;
    }

    public int createBillId(String offCode, String bgrId, String processDate, String billType, int aqmonth, int aqyear) throws Exception {
        Connection con = null;
        ResultSet rs2 = null;
        Statement st2 = null;
        PreparedStatement pst = null;
        int mBillNo = 0;

        try {
            DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
            con = dataSource.getConnection();
            st2 = con.createStatement();
            rs2 = st2.executeQuery("SELECT G_OFFICE.OFF_CODE,BILL_GROUP_ID, DESCRIPTION,DEMAND_NO,PLAN,SECTOR,MAJOR_HEAD, MAJOR_HEAD_DESC, SUB_MAJOR_HEAD, SUB_MAJOR_HEAD_DESC, MINOR_HEAD, MINOR_HEAD_DESC, SUB_MINOR_HEAD1, SUB_MINOR_HEAD1_DESC, SUB_MINOR_HEAD2, "
                    + "SUB_MINOR_HEAD2_DESC, SUB_MINOR_HEAD3, SUB_MINOR_HEAD3_DESC,DDO_CODE,DDO_POST,BANK_CODE,BRANCH_CODE,TR_CODE,BILL_TYPE FROM (SELECT * FROM BILL_GROUP_MASTER WHERE OFF_CODE='" + offCode + "' AND (IS_DELETED IS  NULL OR IS_DELETED='N') AND BILL_GROUP_ID ='" + bgrId + "' )BILL_GROUP_MASTER "
                    + "LEFT OUTER JOIN G_OFFICE ON BILL_GROUP_MASTER.OFF_CODE = G_OFFICE.OFF_CODE");

            if (rs2.next()) {

                mBillNo = CommonFunctions.getMaxCode(con, "BILL_MAST", "BILL_NO");
                pst = con.prepareStatement("INSERT INTO BILL_MAST (BILL_NO,BILL_DESC,BILL_DATE,BILL_TYPE,AQ_GROUP_DESC,AQ_MONTH,AQ_YEAR,OFF_CODE,DEMAND_NO,MAJOR_HEAD,MAJOR_HEAD_DESC,SUB_MAJOR_HEAD,SUB_MAJOR_HEAD_DESC,MINOR_HEAD,MINOR_HEAD_DESC,"
                        + "SUB_MINOR_HEAD1,SUB_MINOR_HEAD1_DESC,SUB_MINOR_HEAD2,SUB_MINOR_HEAD2_DESC,SUB_MINOR_HEAD3,BILL_GROUP_DESC,PLAN,SECTOR,DDO_CODE,BANK_CODE,BRANCH_CODE,TR_CODE,BILL_GROUP_ID,TYPE_OF_BILL) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                pst.setInt(1, mBillNo);
                pst.setString(2, null);
                pst.setTimestamp(3, new Timestamp(dateFormat.parse(processDate).getTime()));
                pst.setString(4, rs2.getString("BILL_TYPE"));
                pst.setString(5, null);
                pst.setInt(6, aqmonth);
                pst.setInt(7, aqyear);
                pst.setString(8, offCode);
                pst.setString(9, rs2.getString("DEMAND_NO"));
                pst.setString(10, rs2.getString("MAJOR_HEAD"));
                pst.setString(11, rs2.getString("MAJOR_HEAD_DESC"));
                pst.setString(12, rs2.getString("SUB_MAJOR_HEAD"));
                pst.setString(13, rs2.getString("SUB_MAJOR_HEAD_DESC"));
                pst.setString(14, rs2.getString("MINOR_HEAD"));
                pst.setString(15, rs2.getString("MINOR_HEAD_DESC"));
                pst.setString(16, rs2.getString("SUB_MINOR_HEAD1"));
                pst.setString(17, rs2.getString("SUB_MINOR_HEAD1_DESC"));
                pst.setString(18, rs2.getString("SUB_MINOR_HEAD2"));
                pst.setString(19, rs2.getString("SUB_MINOR_HEAD2_DESC"));
                pst.setString(20, rs2.getString("SUB_MINOR_HEAD3"));
                pst.setString(21, rs2.getString("DESCRIPTION"));
                pst.setString(22, rs2.getString("PLAN"));
                pst.setString(23, rs2.getString("SECTOR"));
                pst.setString(24, rs2.getString("DDO_CODE"));
                pst.setString(25, rs2.getString("BANK_CODE"));
                pst.setString(26, rs2.getString("BRANCH_CODE"));
                pst.setString(27, rs2.getString("TR_CODE"));
                pst.setBigDecimal(28, new BigDecimal(bgrId));
                pst.setString(29, billType);
                int ret = pst.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs2, pst);
            DataBaseFunctions.closeSqlObjects(st2);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return mBillNo;
    }

    public int createBillIdForArrear(String offCode, String bgrId, String processDate, String billType, int aqmonth, int aqyear, int fromMonth, int fromYear, int toMonth, int toYear) throws Exception {
        Connection con = null;
        ResultSet rs2 = null;
        Statement st2 = null;
        PreparedStatement pst = null;
        int mBillNo = 0;

        try {
            DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
            con = dataSource.getConnection();
            st2 = con.createStatement();
            rs2 = st2.executeQuery("SELECT G_OFFICE.OFF_CODE,BILL_GROUP_ID, DESCRIPTION,DEMAND_NO,PLAN,SECTOR,MAJOR_HEAD, MAJOR_HEAD_DESC, SUB_MAJOR_HEAD, SUB_MAJOR_HEAD_DESC, MINOR_HEAD, MINOR_HEAD_DESC, SUB_MINOR_HEAD1, SUB_MINOR_HEAD1_DESC, SUB_MINOR_HEAD2, "
                    + "SUB_MINOR_HEAD2_DESC, SUB_MINOR_HEAD3, SUB_MINOR_HEAD3_DESC,DDO_CODE,DDO_POST,BANK_CODE,BRANCH_CODE,TR_CODE,BILL_TYPE FROM (SELECT * FROM BILL_GROUP_MASTER WHERE OFF_CODE='" + offCode + "' AND (IS_DELETED IS  NULL OR IS_DELETED='N') AND BILL_GROUP_ID ='" + bgrId + "' )BILL_GROUP_MASTER "
                    + "LEFT OUTER JOIN G_OFFICE ON BILL_GROUP_MASTER.OFF_CODE = G_OFFICE.OFF_CODE");

            if (rs2.next()) {

                mBillNo = CommonFunctions.getMaxCode(con, "BILL_MAST", "BILL_NO");
                pst = con.prepareStatement("INSERT INTO BILL_MAST (BILL_NO,BILL_DESC,BILL_DATE,BILL_TYPE,AQ_GROUP_DESC,AQ_MONTH,AQ_YEAR,OFF_CODE,DEMAND_NO,MAJOR_HEAD,MAJOR_HEAD_DESC,SUB_MAJOR_HEAD,SUB_MAJOR_HEAD_DESC,MINOR_HEAD,MINOR_HEAD_DESC,"
                        + "SUB_MINOR_HEAD1,SUB_MINOR_HEAD1_DESC,SUB_MINOR_HEAD2,SUB_MINOR_HEAD2_DESC,SUB_MINOR_HEAD3,BILL_GROUP_DESC,PLAN,SECTOR,DDO_CODE,BANK_CODE,BRANCH_CODE,TR_CODE,BILL_GROUP_ID,TYPE_OF_BILL, from_month ,from_year ,to_month ,to_year ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                pst.setInt(1, mBillNo);
                pst.setString(2, null);
                pst.setTimestamp(3, new Timestamp(dateFormat.parse(processDate).getTime()));
                pst.setString(4, "43");
                pst.setString(5, null);
                pst.setInt(6, aqmonth);
                pst.setInt(7, aqyear);
                pst.setString(8, offCode);
                pst.setString(9, rs2.getString("DEMAND_NO"));
                pst.setString(10, rs2.getString("MAJOR_HEAD"));
                pst.setString(11, rs2.getString("MAJOR_HEAD_DESC"));
                pst.setString(12, rs2.getString("SUB_MAJOR_HEAD"));
                pst.setString(13, rs2.getString("SUB_MAJOR_HEAD_DESC"));
                pst.setString(14, rs2.getString("MINOR_HEAD"));
                pst.setString(15, rs2.getString("MINOR_HEAD_DESC"));
                pst.setString(16, rs2.getString("SUB_MINOR_HEAD1"));
                pst.setString(17, rs2.getString("SUB_MINOR_HEAD1_DESC"));
                pst.setString(18, rs2.getString("SUB_MINOR_HEAD2"));
                pst.setString(19, rs2.getString("SUB_MINOR_HEAD2_DESC"));
                pst.setString(20, rs2.getString("SUB_MINOR_HEAD3"));
                pst.setString(21, rs2.getString("DESCRIPTION"));
                pst.setString(22, rs2.getString("PLAN"));
                pst.setString(23, rs2.getString("SECTOR"));
                pst.setString(24, rs2.getString("DDO_CODE"));
                pst.setString(25, rs2.getString("BANK_CODE"));
                pst.setString(26, rs2.getString("BRANCH_CODE"));
                pst.setString(27, rs2.getString("TR_CODE"));
                pst.setBigDecimal(28, new BigDecimal(bgrId));
                pst.setString(29, billType);
                pst.setInt(30, fromMonth);
                pst.setInt(31, fromYear);
                pst.setInt(32, toMonth);
                pst.setInt(33, toYear);

                int ret = pst.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs2, pst);
            DataBaseFunctions.closeSqlObjects(st2);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return mBillNo;
    }

    @Override
    public ArrayList getAllowanceList(int billno) {
        AllowDeductDetails allowdeduct = null;
        ArrayList allowanceList = new ArrayList();
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pst = null;
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("SELECT AD_CODE,BT_ID,SUM(AD_AMT) AD_AMT FROM AQ_DTLS INNER JOIN  (SELECT AQ_MAST.AQSL_NO FROM AQ_MAST WHERE  AQ_MAST.BILL_NO=? )AQ_MAST ON AQ_DTLS.AQSL_NO = AQ_MAST.AQSL_NO "
                    + "WHERE AD_TYPE='A' AND AD_AMT > 0 GROUP BY AQ_DTLS.AD_CODE,BT_ID,NOW_DEDN ORDER BY AQ_DTLS.AD_CODE");
            pst.setInt(1, billno);
            rs = pst.executeQuery();
            while (rs.next()) {
                allowdeduct = new AllowDeductDetails();
                allowdeduct.setAdname(rs.getString("AD_CODE"));
                allowdeduct.setObjecthead(rs.getString("BT_ID"));
                allowdeduct.setAdamount(rs.getString("AD_AMT"));
                allowanceList.add(allowdeduct);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return allowanceList;
    }

    @Override
    public ArrayList getDeductionList(int billno) {
        AllowDeductDetails allowdeduct = null;
        ArrayList deductionList = new ArrayList();
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pst = null;
        String nowdedn = "";
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("SELECT AD_CODE,SUM(AD_AMT) AD_AMT,NOW_DEDN,BT_ID FROM AQ_DTLS INNER JOIN  (SELECT AQ_MAST.AQSL_NO FROM AQ_MAST WHERE  AQ_MAST.BILL_NO=?)AQ_MAST ON AQ_DTLS.AQSL_NO = AQ_MAST.AQSL_NO "
                    + "WHERE AD_TYPE='D' AND  (SCHEDULE !='PVTL' AND SCHEDULE!='PVTD')  AND AD_AMT>0 GROUP BY AQ_DTLS.AD_CODE,BT_ID,NOW_DEDN ORDER BY AQ_DTLS.AD_CODE");
            pst.setInt(1, billno);
            rs = pst.executeQuery();
            while (rs.next()) {
                if (rs.getString("AD_AMT") != null && !rs.getString("AD_AMT").equals("")) {
                    nowdedn = rs.getString("NOW_DEDN");
                    allowdeduct = new AllowDeductDetails();
                    if (nowdedn == null) {
                        allowdeduct.setAdname(rs.getString("AD_CODE"));
                    } else {
                        allowdeduct.setAdname(rs.getString("AD_CODE") + "-" + nowdedn);
                    }
                    allowdeduct.setObjecthead(rs.getString("BT_ID"));
                    allowdeduct.setAdamount(rs.getString("AD_AMT"));
                    deductionList.add(allowdeduct);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return deductionList;
    }

    @Override
    public ArrayList getPvtLoanList(int billno) {
        AllowDeductDetails allowdeduct = null;
        ArrayList pvtDeductionList = new ArrayList();
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pst = null;
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("SELECT AD_CODE,SUM(AD_AMT) AD_AMT FROM AQ_DTLS INNER JOIN  (SELECT AQ_MAST.AQSL_NO FROM AQ_MAST WHERE  AQ_MAST.BILL_NO=?)AQ_MAST ON AQ_DTLS.AQSL_NO = AQ_MAST.AQSL_NO "
                    + "WHERE (SCHEDULE ='PVTL' OR SCHEDULE ='PVTD') AND AD_AMT>0 GROUP BY AQ_DTLS.AD_CODE,BT_ID,NOW_DEDN ORDER BY AQ_DTLS.AD_CODE");
            pst.setInt(1, billno);
            rs = pst.executeQuery();
            while (rs.next()) {
                if (rs.getString("AD_AMT") != null && !rs.getString("AD_AMT").equals("")) {
                    allowdeduct = new AllowDeductDetails();
                    allowdeduct.setAdname(rs.getString("AD_CODE"));
                    allowdeduct.setAdamount(rs.getString("AD_AMT"));
                    pvtDeductionList.add(allowdeduct);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return pvtDeductionList;
    }

    @Override
    public void reprocessSingleBill(BillBrowserbean bbbean) {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        try {
            boolean ifexist = false;
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT TASK_ID FROM PAYBILL_TASK WHERE BILL_ID=?");
            pstmt.setInt(1, Integer.parseInt(bbbean.getBillNo()));
            rs = pstmt.executeQuery();
            if (rs.next()) {
                ifexist = true;
            }
            if (!ifexist) {
                pstmt = con.prepareStatement("select COALESCE(max(task_id),0)+1 taskid FROM paybill_task");
                rs = pstmt.executeQuery();
                int taskid = 0;
                if (rs.next()) {
                    taskid = rs.getInt("TASKID");
                }
                int totalNoOfEmployee = 0;
                pstmt = con.prepareStatement("INSERT INTO PAYBILL_TASK (TASK_ID,OFF_CODE,AQ_MONTH,AQ_YEAR,TOTAL_AQ,BILL_ID,BILL_GROUP_ID,PRIORITY,bill_type) VALUES (" + taskid + ",'" + bbbean.getOffCode() + "'," + bbbean.getSltMonth() + "," + bbbean.getSltYear() + "," + totalNoOfEmployee + "," + bbbean.getBillNo() + ",'" + bbbean.getBgid() + "'," + bbbean.getPriority() + ", '" + bbbean.getTxtbilltype() + "')");
                pstmt.executeUpdate();

                pstmt = con.prepareStatement("UPDATE BILL_MAST SET IS_BILL_PREPARED = 'N' WHERE BILL_NO=?");
                pstmt.setInt(1, Integer.parseInt(bbbean.getBillNo()));
                pstmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    @Override
    public void updateBillData(BillBrowserbean bbbean) {
        Connection con = null;
        PreparedStatement pst = null;
        int retVal = 1;
        boolean ret = false;
        String isError = "N";
        String status = "";
        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            Date outputDate = dateFormat.parse(bbbean.getBillDate());
            con = dataSource.getConnection();

            status = verifyBillNoandBenRefNo(bbbean.getOffCode(), bbbean.getBillNo(), bbbean.getBenificiaryNumber(), bbbean.getSltYear(), bbbean.getSltMonth());
            if (status != null && status.equals("Y")) {
                pst = con.prepareStatement("UPDATE BILL_MAST SET BILL_DESC=?,TR_CODE=?,BEN_REF_NO=?, BILL_DATE=?,VCH_NO=?,VCH_DATE=?,BILL_STATUS_ID=? WHERE BILL_NO=?");
                pst.setString(1, bbbean.getBilldesc());
                pst.setString(2, bbbean.getTreasury());
                pst.setString(3, bbbean.getBenificiaryNumber());
                pst.setTimestamp(4, new Timestamp(outputDate.getTime()));
                pst.setString(5, bbbean.getVchNo());
                if (bbbean.getVchDt() != null && !bbbean.getVchDt().equals("")) {
                    outputDate = dateFormat.parse(bbbean.getVchDt());
                    pst.setTimestamp(6, new Timestamp(outputDate.getTime()));
                } else {
                    pst.setTimestamp(6, null);
                }
                if (bbbean.getStatus() == 5) {
                    if (bbbean.getVchNo() != null && !bbbean.getVchNo().equals("")) {
                        pst.setInt(7, 7);
                    } else {
                        pst.setInt(7, bbbean.getStatus());
                    }
                } else {
                    pst.setInt(7, bbbean.getStatus());
                }
                pst.setInt(8, Integer.parseInt(bbbean.getBillNo()));
                retVal = pst.executeUpdate();
            } else if (status != null && status.equals("DBN")) {
                isError = "DBN";
            } else if (status != null && status.equals("DBR")) {
                isError = "DBR";
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    @Override
    public String verifyBillNoandBenRefNo(String offCode, String billId, String benrefNumber, int year, int month) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Connection con = null;
        String benref = benrefNumber.trim();
        String isSaving = "Y";
        try {
            con = dataSource.getConnection();
            String sql = "SELECT * FROM BILL_MAST WHERE AQ_YEAR=? AND AQ_MONTH=? AND OFF_CODE=?  AND BILL_NO!=?";

            stmt = con.prepareStatement(sql);
            stmt.setInt(1, year);
            stmt.setInt(2, month);
            stmt.setString(3, offCode);
            stmt.setInt(4, Integer.parseInt(billId));
            rs = stmt.executeQuery();
            if (rs.next()) {
                isSaving = "DBN";
            } else if (benref != null && !benref.equals("")) {

                sql = "SELECT * FROM BILL_MAST WHERE AQ_YEAR=? AND AQ_MONTH=? AND OFF_CODE=? AND BEN_REF_NO=? AND BILL_NO!=?";
                stmt = con.prepareStatement(sql);
                stmt.setInt(1, year);
                stmt.setInt(2, month);
                stmt.setString(3, offCode);
                stmt.setString(4, benrefNumber);
                stmt.setInt(5, Integer.parseInt(billId));
                rs = stmt.executeQuery();
                if (rs.next()) {
                    isSaving = "DBR";
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, stmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return isSaving;
    }

    @Override
    public void updateBillChartofAccount(int billno, BillBrowserbean bean) {
        String query = "";
        String chartAcc = "";
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        try {

            con = dataSource.getConnection();
            query = "UPDATE BILL_MAST SET DEMAND_NO=?, MAJOR_HEAD=?, SUB_MAJOR_HEAD=?, MINOR_HEAD=?, SUB_MINOR_HEAD1=?, SUB_MINOR_HEAD2=?, SUB_MINOR_HEAD3=?, PLAN=?, SECTOR=? WHERE BILL_NO=?";
            pstmt = con.prepareStatement(query);
            pstmt.setString(1, bean.getTxtDemandno());
            pstmt.setString(2, bean.getTxtmajcode());
            pstmt.setString(3, bean.getSubmajcode());
            pstmt.setString(4, bean.getTxtmincode());
            pstmt.setString(5, bean.getSubmincode1());
            pstmt.setString(6, bean.getSubmincode2());
            pstmt.setString(7, bean.getSubmincode3());
            pstmt.setString(8, bean.getPlanCode());
            pstmt.setString(9, bean.getSectorCode());
            pstmt.setInt(10, billno);
            pstmt.execute();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }

    }

    @Override
    public String getBillChartofAccount(int billno) {
        String query = "";
        String chartAcc = "";
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        try {
            con = dataSource.getConnection();
            query = "SELECT DEMAND_NO,MAJOR_HEAD,SUB_MAJOR_HEAD,MINOR_HEAD,SUB_MINOR_HEAD1,SUB_MINOR_HEAD2,SUB_MINOR_HEAD3,PLAN,SECTOR FROM BILL_MAST WHERE BILL_NO=?";
            pstmt = con.prepareStatement(query);
            pstmt.setInt(1, billno);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                chartAcc = StringUtils.defaultString(rs.getString("DEMAND_NO")) + "-" + StringUtils.defaultString(rs.getString("MAJOR_HEAD")) + "-" + StringUtils.defaultString(rs.getString("SUB_MAJOR_HEAD")) + "-" + StringUtils.defaultString(rs.getString("MINOR_HEAD")) + "-" + StringUtils.defaultString(rs.getString("SUB_MINOR_HEAD1")) + "-" + StringUtils.defaultString(rs.getString("SUB_MINOR_HEAD2")) + "-" + StringUtils.defaultString(rs.getString("PLAN")) + "-" + StringUtils.defaultString(rs.getString("SUB_MINOR_HEAD3")) + "-" + StringUtils.defaultString(rs.getString("SECTOR"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return chartAcc;
    }

    @Override
    public boolean verifyBenificiaryNumber(String benificiaryNumber, int year, int month) {
        boolean saveBenificiary = false;
        String benificiaryNo = benificiaryNumber;
        int benificiaryYear = Integer.parseInt(benificiaryNo.substring(0, 4));
        Calendar c = Calendar.getInstance();
        int cyear = c.get(Calendar.YEAR);
        int cmonth = c.get(Calendar.MONTH);
        if (month < 2 && benificiaryYear == (cyear - 1)) {
            saveBenificiary = true;
        } else if (month > 1 && year == benificiaryYear) {
            saveBenificiary = true;
        }
        return saveBenificiary;
    }

    @Override
    public GetBillStatusBean getUploadBillStatus(int billId) {
        Connection con = null;
        ResultSet rs2 = null;
        PreparedStatement pstmt = null;
        GetBillStatusBean gbillStatus = new GetBillStatusBean();
        int billStatusId = 0;
        ArrayList al = null;
        int prevBillId = 0;

        BillHistoryAttr billhistr = null;
        ArrayList billhistrs = new ArrayList();
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT BILL_STATUS_ID,BILL_DESC,BILL_DATE,PREVIOUS_BILL_NO FROM BILL_MAST WHERE BILL_NO=?");
            pstmt.setInt(1, billId);
            rs2 = pstmt.executeQuery();
            if (rs2.next()) {
                billStatusId = rs2.getInt("BILL_STATUS_ID");
                gbillStatus.setBillno(rs2.getString("BILL_DESC"));
                gbillStatus.setBilldate(CommonFunctions.getFormattedOutputDate3(rs2.getDate("BILL_DATE")));
                prevBillId = rs2.getInt("PREVIOUS_BILL_NO");
            }

            DataBaseFunctions.closeSqlObjects(rs2, pstmt);

            String query = "SELECT BILL_STATUS,REMARK,STATUS_ID,HISTORY_DATE FROM ( "
                    + "SELECT REMARK,STATUS_ID,HISTORY_DATE FROM BILL_STATUS_HISTORY WHERE BILL_ID IN (?, ? ) ORDER BY HISTORY_DATE DESC ) BILL_STATUS_HISTORY "
                    + "INNER JOIN G_BILL_STATUS ON BILL_STATUS_HISTORY.STATUS_ID=G_BILL_STATUS.ID ORDER BY HISTORY_DATE DESC";

            pstmt = con.prepareStatement(query);
            pstmt.setInt(1, billId);
            pstmt.setInt(2, prevBillId);
            rs2 = pstmt.executeQuery();
            while (rs2.next()) {

                Timestamp originalString = rs2.getTimestamp("HISTORY_DATE");
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");

                String newString = simpleDateFormat.format(originalString);
                billhistr = new BillHistoryAttr();
                billhistr.setBillStatus(rs2.getString("BILL_STATUS") + " (" + newString + ")");
                al = new ArrayList();
                if (rs2.getInt("STATUS_ID") == 4) {
                    String str[] = rs2.getString("REMARK").split("@");
                    if (str != null) {
                        for (int i = 0; i < str.length; i++) {
                            al.add(str[i]);
                        }
                    }
                } else {
                    al.add(rs2.getString("REMARK"));
                }
                billhistr.setMessage(al);
                billhistrs.add(billhistr);

            }
            gbillStatus.setErrMsg(billhistrs);

            DataBaseFunctions.closeSqlObjects(rs2, pstmt);

            String query2 = "SELECT * FROM (SELECT BILL_NO, BILL_DESC, BILL_DATE, VCH_NO, VCH_DATE, TOKEN_NO, TOKEN_DATE, BILL_STATUS_ID FROM BILL_MAST WHERE BILL_NO=?) BILL_MAST "
                    + "LEFT OUTER JOIN G_BILL_STATUS ON BILL_MAST.BILL_STATUS_ID = G_BILL_STATUS.ID";
            pstmt = con.prepareStatement(query2);
            pstmt.setInt(1, billId);
            rs2 = pstmt.executeQuery();
            if (rs2.next()) {
                gbillStatus.setBillno(rs2.getString("BILL_DESC"));
                gbillStatus.setBilldate(CommonFunctions.getFormattedOutputDate3(rs2.getDate("BILL_DATE")));
                gbillStatus.setTokenno(rs2.getString("TOKEN_NO"));
                gbillStatus.setTokendate(CommonFunctions.getFormattedOutputDate3(rs2.getDate("TOKEN_DATE")));
                gbillStatus.setVoucherno(rs2.getString("VCH_NO"));
                gbillStatus.setVoucherdate(CommonFunctions.getFormattedOutputDate3(rs2.getDate("VCH_DATE")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs2, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return gbillStatus;
    }

    @Override
    public void changeBillStatus(int billId, int statusId) {
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = this.dataSource.getConnection();
            System.out.println("Bill Unlocked ID");
            pstmt = con.prepareStatement("update bill_mast set bill_status_id=? where bill_no=?");//0
            pstmt.setInt(1, statusId);
            pstmt.setInt(2, billId);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    @Override
    public List getBillData(BillDetail billDetail) {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        ArrayList ar = new ArrayList();
        String sql = "";
        String sql1 = "";
        String sql2 = "";

        String offCode = billDetail.getOffcode();
        String bill_no = billDetail.getBillnumber();
        try {
            con = this.dataSource.getConnection();
            stmt = con.createStatement();
            System.out.println("Bill Data LIst");
            if (offCode != null && !offCode.equals("")) {
                sql1 = "select MAX(extract(year from bill_date)) \"year\" from bill_mast where off_code='" + billDetail.getOffcode() + "'";
                rs = stmt.executeQuery(sql1);
                if (rs.next()) {
                    billDetail = new BillDetail();
                    billDetail.setAq_year(rs.getString("year"));
                    System.out.println("Year:" + rs.getString("year"));
                }
                int aqYear = Integer.parseInt(billDetail.getAq_year());
                if (aqYear > 0) {
                    sql2 = "select MAX(extract(month from bill_date)) \"amonth\" from bill_mast where off_code='" + offCode + "' and aq_year='" + aqYear + "'";
                    rs = stmt.executeQuery(sql2);
                    if (rs.next()) {
                        billDetail = new BillDetail();
                        billDetail.setAq_month((rs.getString("amonth")));
                    }
                }
                int aqMonth = Integer.parseInt(billDetail.getAq_month()) - 1;
                System.out.println("Month:" + aqMonth);

                sql = "select billmast.bill_no,billmast.bill_desc,billmast.bill_date,billmast.aq_month,billmast.aq_year,billmast.off_code, billmast.bill_group_desc,\n"
                        + "billmast.token_no,billmast.token_date,billmast.previous_token_no,billmast.previous_token_date,billmast.bill_status_id,billstatus.bill_status from\n"
                        + "(select * from bill_mast where off_code='" + offCode + "' and aq_year='" + aqYear + "' and aq_month='" + aqMonth + "')billmast\n"
                        + "left outer join\n"
                        + "(select * from g_bill_status)billstatus\n"
                        + "on\n"
                        + "billmast.bill_status_id=billstatus.id";
                rs = stmt.executeQuery(sql);

                while (rs.next()) {
                    billDetail = new BillDetail();
                    billDetail.setOffcode(rs.getString("off_code"));
                    System.out.println("Off Code:" + rs.getString("off_code"));
                    billDetail.setBillnumber(rs.getString("bill_no"));                    
                    billDetail.setBilldesc(rs.getString("bill_desc"));
                    billDetail.setBilldesc(rs.getString("bill_group_desc"));
                    billDetail.setBillStatus(rs.getString("bill_status"));
                    billDetail.setBillStatusId(rs.getInt("bill_status_id"));                    
                    billDetail.setBillDate(CommonFunctions.getFormattedInputDate(rs.getDate("bill_date")));                    
                    billDetail.setAq_month(rs.getString("aq_month"));                    
                    billDetail.setAq_year(rs.getString("aq_year"));                    
                    billDetail.setTokenNumber(StringUtils.defaultString(rs.getString("token_no")));                    
                    billDetail.setTokendate(CommonFunctions.getFormattedInputDate(rs.getDate("token_date")));                    
                    billDetail.setPrevTokenNumber(StringUtils.defaultString(rs.getString("previous_token_no")));
                    billDetail.setPrevTokendate(CommonFunctions.getFormattedInputDate(rs.getDate("previous_token_date")));
                    ar.add(billDetail);
                }

            } else if (bill_no != null && !bill_no.equals("")) {
                System.out.println("Bill No:" + billDetail.getBillnumber());
                sql = "select billmast.bill_no,billmast.bill_desc,billmast.bill_date,billmast.aq_month,billmast.aq_year,billmast.off_code, billmast.bill_group_desc,\n"
                        + "billmast.token_no,billmast.token_date,billmast.previous_token_no,billmast.previous_token_date,billmast.bill_status_id,billstatus.bill_status from\n"
                        + "(select * from bill_mast where BILL_no='" + billDetail.getBillnumber() + "')billmast\n"
                        + "left outer join\n"
                        + "(select * from g_bill_status)billstatus\n"
                        + "on\n"
                        + "billmast.bill_status_id=billstatus.id";
                rs = stmt.executeQuery(sql);
                if (rs.next()) {
                    billDetail = new BillDetail();
                    billDetail.setOffcode(rs.getString("off_code"));
                    billDetail.setBillnumber(rs.getString("bill_no"));                    
                    billDetail.setBilldesc(rs.getString("bill_desc"));
                    billDetail.setBilldesc(rs.getString("bill_group_desc"));
                    billDetail.setBillStatus(rs.getString("bill_status"));
                    billDetail.setBillStatusId(rs.getInt("bill_status_id"));                    
                    billDetail.setBillDate(CommonFunctions.getFormattedInputDate(rs.getDate("bill_date")));                    
                    billDetail.setAq_month(rs.getString("aq_month"));
                    billDetail.setAq_year(rs.getString("aq_year"));
                    billDetail.setTokenNumber(StringUtils.defaultString(rs.getString("token_no")));
                    billDetail.setTokendate(CommonFunctions.getFormattedInputDate(rs.getDate("token_date")));
                    billDetail.setPrevTokenNumber(StringUtils.defaultString(rs.getString("previous_token_no")));
                    billDetail.setPrevTokendate(CommonFunctions.getFormattedInputDate(rs.getDate("previous_token_date")));
                    ar.add(billDetail);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return ar;
    }

    @Override
    public void unlockBill(BillDetail billDetail) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = this.dataSource.getConnection();            
            ps = con.prepareStatement("update bill_mast set bill_status_id=0 where bill_no=?");
            ps.setInt(1, Integer.parseInt(billDetail.getBillnumber()));
            ps.executeUpdate();            

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }        
    }

    @Override
    public BillDetail unlockBillToResubmit(BillDetail billDetail) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = this.dataSource.getConnection();            
            ps = con.prepareStatement("update bill_mast set bill_status_id=1 where bill_status_id=2 and bill_no=?");
            ps.setInt(1, Integer.parseInt(billDetail.getBillnumber()));
            ps.executeUpdate();                     

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public void objectBill(BillDetail billDetail) {
        Connection con = null;
        PreparedStatement ps = null;
        Statement stmt = null;
        ResultSet rs = null;
        String prvtoken_no = "";
        Date prvtoken_date = null;
        String tokenno = "";
        Date tokendate = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        String objBill = billDetail.getObjectbill();
        ArrayList ar = new ArrayList();
        try {
            con = this.dataSource.getConnection();
            stmt = con.createStatement();

            String sql = "UPDATE BILL_MAST SET BILL_STATUS_ID=8,IS_RESUBMITTED='Y',PREVIOUS_TOKEN_NO=?,PREVIOUS_TOKEN_DATE=?,TOKEN_NO='',TOKEN_DATE=NULL WHERE BILL_NO=?";
            ps = con.prepareStatement(sql);
            String[] objArr = objBill.split(",");

            for (int i = 0; i < objArr.length; i++) {
                String sql1 = "SELECT * FROM BILL_MAST WHERE BILL_NO='" + Integer.parseInt(objArr[i]) + "'";
                rs = stmt.executeQuery(sql1);
                if (rs.next()) {
                    prvtoken_no = rs.getString("TOKEN_NO");
                    prvtoken_date = rs.getDate("TOKEN_DATE");
                }
                ps.setString(1, prvtoken_no);
                ps.setTimestamp(2, new Timestamp(prvtoken_date.getTime()));
                ps.setInt(3, Integer.parseInt(objArr[i]));
                ps.executeUpdate();
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }        
    }
    
    @Override
    public ArrayList getVoucherListForAG(int year, int month, String parentTrCode) {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        ArrayList billList = new ArrayList();

        String allTreasury = "";
        try {
            con = dataSource.getConnection();
            //con = this.getDBConnection();

            pstmt = con.prepareStatement("select tr_code from g_treasury where ag_treasury_code=?");
            pstmt.setString(1, parentTrCode);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                if (allTreasury.equals("")) {
                    allTreasury = "'" + rs.getString("tr_code") + "'";
                } else {
                    allTreasury = allTreasury + ",'" + rs.getString("tr_code") + "'";
                }
            }
            //System.out.println("allTreasury is: "+allTreasury);

            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            
            pstmt = con.prepareStatement("SELECT type_of_bill,aq_month,aq_year,BILL_NO,major_head,sub_major_head,minor_head,sub_minor_head1,sub_minor_head2,sub_minor_head3,VCH_NO,VCH_DATE,extract (year from vch_date) vch_year, EXTRACT(MONTH FROM vch_date) vch_month,EXTRACT(DAY FROM vch_date) vch_day,ag_treasury_code,DDO_CODE FROM BILL_MAST bill "
                    + "inner join g_treasury treasury on bill.tr_code=treasury.tr_code WHERE extract (year from vch_date)=? AND extract (month from vch_date)=? AND bill.TR_CODE IN (" + allTreasury + ") AND bill.BILL_STATUS_ID=7 and length(VCH_NO) > 4 order by major_head,substring(VCH_NO,5)::integer");
            pstmt.setInt(1, year);
            pstmt.setInt(2, (month + 1));
            //pstmt.setString(3, allTreasury);

            rs = pstmt.executeQuery();

            String financialYear = "";
            if (month > 2) {
                financialYear = year + "-" + (year + 1);
            } else {
                financialYear = (year - 1) + "-" + year;
            }
            SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
            while (rs.next()) {
                BillBean bb = new BillBean();
                bb.setBilltype(rs.getString("type_of_bill"));
                bb.setBillMonth(rs.getInt("aq_month"));
                bb.setBillYear(rs.getInt("aq_year"));
                bb.setAdjYear(rs.getInt("vch_year"));
                bb.setBillno(rs.getString("BILL_NO"));
                if (rs.getString("VCH_NO").length() > 4) {
                    bb.setVoucherno(StringUtils.leftPad(StringUtils.substring(rs.getString("VCH_NO"), 4), 5, "0"));
                } else {
                    bb.setVoucherno(StringUtils.leftPad(rs.getString("VCH_NO"), 5, "0"));
                }

                if (rs.getDate("VCH_DATE") != null) {
                    bb.setVoucherdate(DATE_FORMAT.format(rs.getDate("VCH_DATE")));
                }
                bb.setVouchermonth(rs.getString("vch_month"));
                bb.setFinyear(financialYear);
                bb.setTreasurycode(rs.getString("ag_treasury_code"));
                bb.setDdocode(rs.getString("DDO_CODE"));
                bb.setMajorhead(rs.getString("major_head"));
                bb.setVoucherDay(rs.getInt("vch_day"));
                billList.add(bb);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return billList;
    }
    
    @Override
    public ArrayList getAquitanceVoucherListForAG(int year, int month, String parentTrCode) {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        ArrayList billList = new ArrayList();

        String allTreasury = "";
        try {
            con = dataSource.getConnection();

            pstmt = con.prepareStatement("select tr_code from g_treasury where ag_treasury_code=?");
            pstmt.setString(1, parentTrCode);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                if (allTreasury.equals("")) {
                    allTreasury = "'" + rs.getString("tr_code") + "'";
                } else {
                    allTreasury = allTreasury + ",'" + rs.getString("tr_code") + "'";
                }
            }
            System.out.println("Aquitance,ParentTrCode is: " + parentTrCode + " and allTreasury is: " + allTreasury);

            DataBaseFunctions.closeSqlObjects(rs, pstmt);

            pstmt = con.prepareStatement("SELECT aq_month,aq_year,BILL_NO,major_head,sub_major_head,minor_head,sub_minor_head1,sub_minor_head2,sub_minor_head3,VCH_NO,VCH_DATE,extract (year from vch_date) vch_year, EXTRACT(MONTH FROM vch_date) vch_month,EXTRACT(DAY FROM vch_date) vch_day,ag_treasury_code,DDO_CODE FROM BILL_MAST bill "
                    + "inner join g_treasury treasury on bill.tr_code=treasury.tr_code WHERE extract (year from vch_date)=? AND extract (month from vch_date)=? AND bill.TR_CODE IN (" + allTreasury + ") AND bill.BILL_STATUS_ID=7 and bill.type_of_bill='PAY' and length(VCH_NO) > 4 order by major_head,substring(VCH_NO,5)::integer");
            pstmt.setInt(1, year);
            pstmt.setInt(2, (month + 1));
            //pstmt.setString(3, allTreasury);

            rs = pstmt.executeQuery();

            String financialYear = "";
            if (month > 2) {
                financialYear = year + "-" + (year + 1);
            } else {
                financialYear = (year - 1) + "-" + year;
            }
            SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
            while (rs.next()) {
                BillBean bb = new BillBean();
                bb.setBillMonth(rs.getInt("aq_month"));
                bb.setBillYear(rs.getInt("aq_year"));
                bb.setAdjYear(rs.getInt("vch_year"));
                bb.setBillno(rs.getString("BILL_NO"));
                if (rs.getString("VCH_NO").length() > 4) {
                    bb.setVoucherno(StringUtils.leftPad(StringUtils.substring(rs.getString("VCH_NO"), 4), 5, "0"));
                } else {
                    bb.setVoucherno(StringUtils.leftPad(rs.getString("VCH_NO"), 5, "0"));
                }

                if (rs.getDate("VCH_DATE") != null) {
                    bb.setVoucherdate(DATE_FORMAT.format(rs.getDate("VCH_DATE")));
                }
                bb.setVouchermonth(rs.getString("vch_month"));
                bb.setFinyear(financialYear);
                bb.setTreasurycode(rs.getString("ag_treasury_code"));
                bb.setDdocode(rs.getString("DDO_CODE"));
                bb.setMajorhead(rs.getString("major_head"));
                bb.setVoucherDay(rs.getInt("vch_day"));
                billList.add(bb);
            }
            System.out.println("No of bills: "+billList.size());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return billList;
    }
    
    private Connection getDBConnection(){
        Connection con = null;
        
        try {
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection("jdbc:postgresql://172.16.1.16/hrmis", "hrmis2", "cmgi");
        }catch (Exception e) {
            e.printStackTrace();
        }
       return con; 
    }
    
    @Override
    public ArrayList getScheduleVoucherListForAG(int year, int month, String parentTrCode) {
        
        Connection con = null;
        
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        ArrayList billList = new ArrayList();

        String allTreasury = "";
        try {
            con = dataSource.getConnection();

            pstmt = con.prepareStatement("select tr_code from g_treasury where ag_treasury_code=?");
            pstmt.setString(1, parentTrCode);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                if (allTreasury.equals("")) {
                    allTreasury = "'" + rs.getString("tr_code") + "'";
                } else {
                    allTreasury = allTreasury + ",'" + rs.getString("tr_code") + "'";
                }
            }
            //System.out.println("allTreasury is: "+allTreasury);

            DataBaseFunctions.closeSqlObjects(rs, pstmt);

            /*pstmt = con.prepareStatement("SELECT type_of_bill,aq_month,aq_year,BILL_NO,major_head,sub_major_head,minor_head,sub_minor_head1,sub_minor_head2,sub_minor_head3,VCH_NO,VCH_DATE,extract (year from vch_date) vch_year, EXTRACT(MONTH FROM vch_date) vch_month,EXTRACT(DAY FROM vch_date) vch_day,ag_treasury_code,DDO_CODE,off_en FROM BILL_MAST bill "
                    + "inner join g_office on bill.off_code=g_office.off_code inner join g_treasury treasury on bill.tr_code=treasury.tr_code WHERE extract (year from vch_date)=? AND extract (month from vch_date)=? AND bill.TR_CODE IN (" + allTreasury + ") AND bill.BILL_STATUS_ID=7 order by major_head,substring(VCH_NO,5)::integer");*/
            pstmt = con.prepareStatement("SELECT type_of_bill,aq_month,aq_year,BILL_NO,major_head,sub_major_head,minor_head,sub_minor_head1,sub_minor_head2,sub_minor_head3,VCH_NO,VCH_DATE,extract (year from vch_date) vch_year, EXTRACT(MONTH FROM vch_date) vch_month," +
                                            " EXTRACT(DAY FROM vch_date) vch_day,ag_treasury_code,g_office.DDO_CODE,post FROM BILL_MAST bill" +
                                            " inner join g_treasury treasury on bill.tr_code=treasury.tr_code" +
                                            " inner join g_office on bill.off_code=g_office.off_code" +
                                            " inner join g_post on g_office.ddo_post=g_post.post_code" +
                                            " WHERE extract (year from vch_date)=? AND extract (month from vch_date)=?" +
                                            " AND bill.TR_CODE IN (" + allTreasury + ") AND bill.BILL_STATUS_ID=7 order by major_head,substring(VCH_NO,5)::integer");
            pstmt.setInt(1, year);
            pstmt.setInt(2, (month + 1));
            //pstmt.setString(3, allTreasury);

            rs = pstmt.executeQuery();

            String financialYear = "";
            if (month > 2) {
                financialYear = year + "-" + (year + 1);
            } else {
                financialYear = (year - 1) + "-" + year;
            }
            SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
            while (rs.next()) {
                BillBean bb = new BillBean();
                bb.setBilltype(rs.getString("type_of_bill"));
                bb.setBillMonth(rs.getInt("aq_month"));
                bb.setBillYear(rs.getInt("aq_year"));
                bb.setAdjYear(rs.getInt("vch_year"));
                bb.setBillno(rs.getString("BILL_NO"));
                if (rs.getString("VCH_NO").length() > 4) {
                    bb.setVoucherno(StringUtils.leftPad(StringUtils.substring(rs.getString("VCH_NO"), 4), 5, "0"));
                } else {
                    bb.setVoucherno(StringUtils.leftPad(rs.getString("VCH_NO"), 5, "0"));
                }

                if (rs.getDate("VCH_DATE") != null) {
                    bb.setVoucherdate(DATE_FORMAT.format(rs.getDate("VCH_DATE")));
                }
                bb.setVouchermonth(rs.getString("vch_month"));
                bb.setFinyear(financialYear);
                bb.setTreasurycode(rs.getString("ag_treasury_code"));
                bb.setDdocode(rs.getString("DDO_CODE"));
                bb.setDdoname(rs.getString("post"));
                bb.setMajorhead(rs.getString("major_head"));
                bb.setVoucherDay(rs.getInt("vch_day"));
                billList.add(bb);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return billList;
    }
}
