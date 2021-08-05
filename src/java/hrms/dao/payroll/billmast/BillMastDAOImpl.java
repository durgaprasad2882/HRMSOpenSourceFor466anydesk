/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.payroll.billmast;

import hrms.common.CommonFunctions;
import hrms.common.DataBaseFunctions;
import hrms.model.payroll.billmast.BillMastModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;

public class BillMastDAOImpl implements BillMastDAO {

    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public int saveBillMast(BillMastModel bmModel) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Calendar cal = Calendar.getInstance();
        int result = 0;
        int maxId = 0;

        try {
            con = dataSource.getConnection();

            if (bmModel.getBillNo() <= 0) {
                maxId = CommonFunctions.getMaxCode(con, "bill_mast", "bill_no");

                String insertQry = "INSERT INTO bill_mast(bill_no, bill_desc, bill_date, aq_group_desc, aq_month, aq_year, off_ddo, plan, sector,"//9
                        + "major_head, major_head_desc, sub_major_head, sub_major_head_desc, minor_head, minor_head_desc, sub_minor_head1,"//7
                        + "sub_minor_head1_desc, sub_minor_head2, sub_minor_head2_desc, sub_minor_head3, tr_code, tr_officer, vch_no, vch_date, mode,"//9
                        + "bank_code, branch_code,  amt_paid, rec_by, desg, notif_no, gross_amt, ded_amt, pvt_ded_amt, vouchered, off_code,"//12
                        + "bill_group_desc, demand_no, ddo_code, token_no, token_date, bill_status_id, ben_ref_no, bill_group_id, previous_token_no,"//9
                        + "previous_token_date, is_resubmitted, is_bill_prepared, previous_bill_no, bill_type, type_of_bill,reprocess_occurance,is_cleaned"
                        + "ddo_spc, ddo_emp_id)"//2
                        + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";//55
                pstmt = con.prepareStatement(insertQry);

                pstmt.setInt(1, maxId);
                pstmt.setString(2, bmModel.getBillDesc());
                pstmt.setTimestamp(3, new Timestamp(bmModel.getBillDate().getTime()));
                pstmt.setString(4, bmModel.getAqGroupDesc());
                pstmt.setInt(5, bmModel.getAqMonth());
                pstmt.setInt(6, bmModel.getAqYear());
                pstmt.setString(7, bmModel.getOffDDO());
                pstmt.setString(8, bmModel.getPlan());
                pstmt.setString(9, bmModel.getSector());
                //bill_no, bill_desc, bill_date, aq_group_desc, aq_month, aq_year, off_ddo, plan, sector,

                pstmt.setString(10, bmModel.getMajorHead());
                pstmt.setString(11, bmModel.getMajorHeadDesc());
                pstmt.setString(12, bmModel.getSubMajorHead());
                pstmt.setString(13, bmModel.getSubMajorHeadDesc());
                pstmt.setString(14, bmModel.getMinorHead());
                pstmt.setString(15, bmModel.getMinorHeadDesc());
                pstmt.setString(16, bmModel.getSubMinorHead1());
                //major_head, major_head_desc, sub_major_head, sub_major_head_desc, minor_head, minor_head_desc, sub_minor_head1,

                pstmt.setString(17, bmModel.getSubMinorHead1Desc());
                pstmt.setString(18, bmModel.getSubMinorHead2());
                pstmt.setString(19, bmModel.getSubMinorHead2Desc());
                pstmt.setString(20, bmModel.getSubMinorHead3());
                pstmt.setString(21, bmModel.getTrCode());
                pstmt.setString(22, bmModel.getTrOfficer());
                pstmt.setString(23, bmModel.getVchNo());
                pstmt.setTimestamp(24, new Timestamp(bmModel.getVchDate().getTime()));
                pstmt.setString(25, bmModel.getMode());
                //sub_minor_head1_desc, sub_minor_head2, sub_minor_head2_desc, sub_minor_head3, tr_code, tr_officer, vch_no, vch_date, mode,

                pstmt.setString(26, bmModel.getBankCode());
                pstmt.setString(27, bmModel.getBranchCode());

                pstmt.setInt(28, bmModel.getAmtPaid());
                pstmt.setString(29, bmModel.getRecBy());
                pstmt.setString(30, bmModel.getDesg());
                pstmt.setString(31, bmModel.getNotifNo());
                pstmt.setInt(32, bmModel.getGrossAmt());
                pstmt.setInt(33, bmModel.getDedAmt());
                pstmt.setInt(34, bmModel.getPvtDedAmt());
                pstmt.setString(35, bmModel.getVouchered());
                pstmt.setString(36, bmModel.getOffCode());
                //bank_code, branch_code, instr_no, amt_paid, rec_by, desg, notif_no, gross_amt, ded_amt, pvt_ded_amt, vouchered, off_code,

                pstmt.setString(37, bmModel.getBillGroupDesc());
                pstmt.setString(38, bmModel.getDemandNo());
                pstmt.setString(39, bmModel.getDdoCode());
                pstmt.setString(40, bmModel.getTokenNo());
                pstmt.setTimestamp(41, new Timestamp(bmModel.getTokenDate().getTime()));
                pstmt.setInt(42, bmModel.getBillStatusId());
                pstmt.setString(43, bmModel.getBenRefNo());
                pstmt.setLong(44, bmModel.getBillGroupId());
                pstmt.setString(45, bmModel.getPreviousTokenNo());
                //bill_group_desc, demand_no, ddo_code, token_no, token_date, bill_status_id, ben_ref_no, bill_group_id, previous_token_no,    

                pstmt.setTimestamp(46, new Timestamp(bmModel.getTokenDate().getTime()));
                pstmt.setString(47, bmModel.getIsResubmitted());
                pstmt.setString(48, bmModel.getIsBillPrepared());
                pstmt.setInt(49, bmModel.getPreviousBillNo());
                pstmt.setString(50, bmModel.getBillType());
                pstmt.setString(51, bmModel.getTypeOfBill());
                pstmt.setInt(52, bmModel.getReprocessOccurance());
                pstmt.setString(53, bmModel.getIsCleaned());
            //previous_token_date, is_resubmitted, is_bill_prepared, previous_bill_no, bill_type, type_of_bill,reprocess_occurance,is_cleaned

                pstmt.setString(54, bmModel.getDdoSpc());
                pstmt.setString(55, bmModel.getDdoEmpId());
                //ddo_spc, ddo_emp_id    
                result = pstmt.executeUpdate();

            } else if (bmModel.getBillNo() > 0) {

                String updQry = "UPDATE bill_mast SET bill_desc=?, bill_date=?, aq_group_desc=?, aq_month=?, aq_year=?, off_ddo=?, plan=?, sector=?,"
                        + "major_head=?, major_head_desc=?, sub_major_head=?, sub_major_head_desc=?, minor_head=?, minor_head_desc=?, sub_minor_head1=?,"
                        + "sub_minor_head1_desc=?, sub_minor_head2=?, sub_minor_head2_desc=?, sub_minor_head3=?, tr_code=?, tr_officer=?, vch_no=?, "
                        + "vch_date=?, mode=?,bank_code=?, branch_code=?, amt_paid=?, rec_by=?, desg=?, notif_no=?, gross_amt=?, ded_amt=?,"
                        + "pvt_ded_amt=?, vouchered=?, off_code=?,bill_group_desc=?, demand_no=?, ddo_code=?, token_no=?, token_date=?, bill_status_id=?,"
                        + "ben_ref_no=?, bill_group_id=?, previous_token_no=?,previous_token_date=?, is_resubmitted=?, is_bill_prepared=?, previous_bill_no=?,"
                        + "bill_type=?, type_of_bill=?,reprocess_occurance=?,is_cleaned=?,ddo_spc=?, ddo_emp_id=? WHERE bill_no=?";

                pstmt = con.prepareStatement(updQry);

                pstmt.setString(1, bmModel.getBillDesc());
                pstmt.setTimestamp(2, new Timestamp(bmModel.getBillDate().getTime()));
                pstmt.setString(3, bmModel.getAqGroupDesc());
                pstmt.setInt(4, bmModel.getAqMonth());
                pstmt.setInt(5, bmModel.getAqYear());
                pstmt.setString(6, bmModel.getOffDDO());
                pstmt.setString(7, bmModel.getPlan());
                pstmt.setString(8, bmModel.getSector());
                //bill_no, bill_desc, bill_date, aq_group_desc, aq_month, aq_year, off_ddo, plan, sector,

                pstmt.setString(9, bmModel.getMajorHead());
                pstmt.setString(10, bmModel.getMajorHeadDesc());
                pstmt.setString(11, bmModel.getSubMajorHead());
                pstmt.setString(12, bmModel.getSubMajorHeadDesc());
                pstmt.setString(13, bmModel.getMinorHead());
                pstmt.setString(14, bmModel.getMinorHeadDesc());
                pstmt.setString(15, bmModel.getSubMinorHead1());
                //major_head, major_head_desc, sub_major_head, sub_major_head_desc, minor_head, minor_head_desc, sub_minor_head1,

                pstmt.setString(16, bmModel.getSubMinorHead1Desc());
                pstmt.setString(17, bmModel.getSubMinorHead2());
                pstmt.setString(18, bmModel.getSubMinorHead2Desc());
                pstmt.setString(19, bmModel.getSubMinorHead3());
                pstmt.setString(20, bmModel.getTrCode());
                pstmt.setString(21, bmModel.getTrOfficer());
                pstmt.setString(22, bmModel.getVchNo());
                pstmt.setTimestamp(23, new Timestamp(bmModel.getVchDate().getTime()));
                pstmt.setString(24, bmModel.getMode());
                //sub_minor_head1_desc, sub_minor_head2, sub_minor_head2_desc, sub_minor_head3, tr_code, tr_officer, vch_no, vch_date, mode,

                pstmt.setString(25, bmModel.getBankCode());
                pstmt.setString(26, bmModel.getBranchCode());
                pstmt.setInt(27, bmModel.getAmtPaid());
                pstmt.setString(28, bmModel.getRecBy());
                pstmt.setString(29, bmModel.getDesg());
                pstmt.setString(30, bmModel.getNotifNo());
                pstmt.setInt(31, bmModel.getGrossAmt());
                pstmt.setInt(32, bmModel.getDedAmt());
                pstmt.setInt(33, bmModel.getPvtDedAmt());
                pstmt.setString(34, bmModel.getVouchered());
                pstmt.setString(35, bmModel.getOffCode());
                //bank_code, branch_code, amt_paid, rec_by, desg, notif_no, gross_amt, ded_amt, pvt_ded_amt, vouchered, off_code,

                pstmt.setString(36, bmModel.getBillGroupDesc());
                pstmt.setString(37, bmModel.getDemandNo());
                pstmt.setString(38, bmModel.getDdoCode());
                pstmt.setString(39, bmModel.getTokenNo());
                pstmt.setTimestamp(40, new Timestamp(bmModel.getTokenDate().getTime()));
                pstmt.setInt(41, bmModel.getBillStatusId());
                pstmt.setString(42, bmModel.getBenRefNo());
                pstmt.setLong(43, bmModel.getBillGroupId());
                pstmt.setString(44, bmModel.getPreviousTokenNo());
                //bill_group_desc, demand_no, ddo_code, token_no, token_date, bill_status_id, ben_ref_no, bill_group_id, previous_token_no,    

                pstmt.setTimestamp(45, new Timestamp(bmModel.getTokenDate().getTime()));
                pstmt.setString(46, bmModel.getIsResubmitted());
                pstmt.setString(47, bmModel.getIsBillPrepared());
                pstmt.setInt(48, bmModel.getPreviousBillNo());
                pstmt.setString(49, bmModel.getBillType());
                pstmt.setString(50, bmModel.getTypeOfBill());
                pstmt.setInt(51, bmModel.getReprocessOccurance());
                pstmt.setString(52, bmModel.getIsCleaned());
            //previous_token_date, is_resubmitted, is_bill_prepared, previous_bill_no, bill_type, type_of_bill,reprocess_occurance,is_cleaned

                pstmt.setString(53, bmModel.getDdoSpc());
                pstmt.setString(54, bmModel.getDdoEmpId());
            //ddo_spc=?, ddo_emp_id=?

                pstmt.setInt(55, bmModel.getBillNo());
                result = pstmt.executeUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return result;
    }

   @Override
    public BillMastModel getBillMastDetails(int billId) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        BillMastModel bmModel = new BillMastModel();

        try {
            con = dataSource.getConnection();
            String selectQry = "SELECT bill_no, bill_desc, bill_date, aq_group_desc, aq_month, aq_year, off_ddo, plan, sector, major_head, major_head_desc,"
                    + "sub_major_head, sub_major_head_desc, minor_head, minor_head_desc, sub_minor_head1,sub_minor_head1_desc, sub_minor_head2, "
                    + "sub_minor_head2_desc, sub_minor_head3, tr_code, tr_officer, vch_no, vch_date, mode,bank_code, branch_code, amt_paid, "
                    + "rec_by, desg, notif_no, gross_amt, ded_amt, pvt_ded_amt, vouchered, off_code,bill_group_desc, demand_no, ddo_code, token_no, "
                    + "token_date, bill_status_id, ben_ref_no, bill_group_id, previous_token_no,previous_token_date, is_resubmitted, is_bill_prepared, "
                    + "previous_bill_no, bill_type, type_of_bill,reprocess_occurance,is_cleaned,ddo_spc, ddo_empid FROM bill_mast WHERE bill_no='" + billId + "'";
            pstmt = con.prepareStatement(selectQry);
            rs = pstmt.executeQuery();
            while (rs.next()) {

                bmModel.setBillNo(billId);
                bmModel.setBillDesc(rs.getString("bill_desc"));
                bmModel.setBillDate(rs.getDate("bill_date"));
                bmModel.setAqGroupDesc(rs.getString("aq_group_desc"));
                bmModel.setAqMonth(rs.getInt("aq_month"));
                bmModel.setAqYear(rs.getInt("aq_year"));
                bmModel.setOffDDO(rs.getString("off_ddo"));
                bmModel.setPlan(rs.getString("plan"));
                bmModel.setSector(rs.getString("sector"));
                bmModel.setMajorHead(rs.getString("major_head"));
                bmModel.setMajorHeadDesc(rs.getString("major_head_desc"));
                bmModel.setSubMajorHead(rs.getString("sub_major_head"));
                bmModel.setSubMajorHeadDesc(rs.getString("sub_major_head_desc"));
                bmModel.setMinorHead(rs.getString("minor_head"));
                bmModel.setMinorHeadDesc(rs.getString("minor_head_desc"));
                bmModel.setSubMinorHead1(rs.getString("sub_minor_head1"));
                bmModel.setSubMinorHead1Desc(rs.getString("sub_minor_head1_desc"));
                bmModel.setSubMinorHead2(rs.getString("sub_minor_head2"));
                bmModel.setSubMinorHead2Desc(rs.getString("sub_minor_head2_desc"));
                bmModel.setSubMinorHead3(rs.getString("sub_minor_head3"));
                bmModel.setTrCode(rs.getString("tr_code"));
                bmModel.setTrOfficer(rs.getString("tr_officer"));
                bmModel.setVchNo(rs.getString("vch_no"));
                bmModel.setVchDate(rs.getDate("vch_date"));
                bmModel.setMode(rs.getString("mode"));
                bmModel.setBankCode(rs.getString("bank_code"));
                bmModel.setBranchCode(rs.getString("branch_code"));
                bmModel.setAmtPaid(rs.getInt("amt_paid"));
                bmModel.setRecBy(rs.getString("rec_by"));
                bmModel.setDesg(rs.getString("desg"));
                bmModel.setNotifNo(rs.getString("notif_no"));
                bmModel.setGrossAmt(rs.getInt("gross_amt"));
                bmModel.setDedAmt(rs.getInt("ded_amt"));
                bmModel.setPvtDedAmt(rs.getInt("pvt_ded_amt"));
                bmModel.setVouchered(rs.getString("vouchered"));
                bmModel.setOffCode(rs.getString("off_code"));
                bmModel.setBillGroupDesc(rs.getString("bill_group_desc"));
                bmModel.setDemandNo(rs.getString("demand_no"));
                bmModel.setDdoCode(rs.getString("ddo_code"));
                bmModel.setTokenNo(rs.getString("token_no"));
                bmModel.setTokenDate(rs.getDate("token_date"));
                bmModel.setBillStatusId(rs.getInt("bill_status_id"));
                bmModel.setBenRefNo(rs.getString("ben_ref_no"));
                bmModel.setBillGroupId(rs.getLong("bill_group_id"));
                bmModel.setPreviousTokenNo(rs.getString("previous_token_no"));
                bmModel.setTokenDate(rs.getDate("previous_token_date"));
                bmModel.setIsResubmitted(rs.getString("is_resubmitted"));
                bmModel.setIsBillPrepared(rs.getString("is_bill_prepared"));
                bmModel.setPreviousBillNo(rs.getInt("previous_bill_no"));
                bmModel.setBillType(rs.getString("bill_type"));
                bmModel.setTypeOfBill(rs.getString("type_of_bill"));
                bmModel.setReprocessOccurance(rs.getInt("reprocess_occurance"));
                bmModel.setIsCleaned(rs.getString("is_cleaned"));
                bmModel.setDdoSpc(rs.getString("ddo_spc"));
                bmModel.setDdoEmpId(rs.getString("ddo_empid"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return bmModel;
    }

    @Override
    public List getBillList(int year, int month, String offcode, String billType, String spc) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Calendar cal = Calendar.getInstance();
        List billList = new ArrayList();
        BillMastModel bmModel = null;

        try {
            con = dataSource.getConnection();
            String selectQry = "SELECT bill_no, bill_desc, bill_date, aq_group_desc, aq_month, aq_year, off_ddo, plan, sector, major_head, major_head_desc,"
                    + "sub_major_head, sub_major_head_desc, minor_head, minor_head_desc, sub_minor_head1,sub_minor_head1_desc, sub_minor_head2, "
                    + "sub_minor_head2_desc, sub_minor_head3, tr_code, tr_officer, vch_no, vch_date, mode,bank_code, branch_code, amt_paid, rec_by,"
                    + " desg, notif_no, gross_amt, ded_amt, pvt_ded_amt, vouchered, off_code,bill_group_desc, demand_no, ddo_code, token_no, "
                    + "token_date, bill_status_id, ben_ref_no, bill_group_id, previous_token_no,previous_token_date, is_resubmitted, is_bill_prepared, "
                    + "previous_bill_no, bill_type, type_of_bill, reprocess_occurance, is_cleaned, ddo_spc, ddo_emp_id FROM bill_mast WHERE off_code='" + offcode + "'"
                    + "bill_type='" + billType + "' AND aq_month='" + month + "' AND aq_year='" + year + "' AND ddo_spc='" + spc + "'";

            pstmt = con.prepareStatement(selectQry);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                bmModel = new BillMastModel();

                bmModel.setBillNo(rs.getInt("bill_no"));
                bmModel.setBillDesc(rs.getString("bill_desc"));
                bmModel.setBillDate(rs.getDate("bill_date"));
                bmModel.setAqGroupDesc(rs.getString("aq_group_desc"));
                bmModel.setAqMonth(rs.getInt("aq_month"));
                bmModel.setAqYear(rs.getInt("aq_year"));
                bmModel.setOffDDO(rs.getString("off_ddo"));
                bmModel.setPlan(rs.getString("plan"));
                bmModel.setSector(rs.getString("sector"));
                bmModel.setMajorHead(rs.getString("major_head"));
                bmModel.setMajorHeadDesc(rs.getString("major_head_desc"));
            //bill_no, bill_desc, bill_date, aq_group_desc, aq_month, aq_year, off_ddo, plan, sector, major_head, major_head_desc,    

                bmModel.setSubMajorHead(rs.getString("sub_major_head"));
                bmModel.setSubMajorHeadDesc(rs.getString("sub_major_head_desc"));
                bmModel.setMinorHead(rs.getString("minor_head"));
                bmModel.setMinorHeadDesc(rs.getString("minor_head_desc"));
                bmModel.setSubMinorHead1(rs.getString("sub_minor_head1"));
                bmModel.setSubMinorHead1Desc(rs.getString("sub_minor_head1_desc"));
                bmModel.setSubMinorHead2(rs.getString("sub_minor_head2"));
            // sub_major_head, sub_major_head_desc, minor_head, minor_head_desc, sub_minor_head1,sub_minor_head1_desc, sub_minor_head2,     

                bmModel.setSubMinorHead2Desc(rs.getString("sub_minor_head2_desc"));
                bmModel.setSubMinorHead3(rs.getString("sub_minor_head3"));
                bmModel.setTrCode(rs.getString("tr_code"));
                bmModel.setTrOfficer(rs.getString("tr_officer"));
                bmModel.setVchNo(rs.getString("vch_no"));
                bmModel.setVchDate(rs.getDate("vch_date"));
                bmModel.setMode(rs.getString("mode"));

                bmModel.setBankCode(rs.getString("bank_code"));
                bmModel.setBranchCode(rs.getString("branch_code"));
                bmModel.setAmtPaid(rs.getInt("amt_paid"));
            //sub_minor_head2_desc, sub_minor_head3, tr_code, tr_officer, vch_no, vch_date, mode,bank_code, branch_code, amt_paid,

                bmModel.setRecBy(rs.getString("rec_by"));
                bmModel.setDesg(rs.getString("desg"));
                bmModel.setNotifNo(rs.getString("notif_no"));
                bmModel.setGrossAmt(rs.getInt("gross_amt"));
                bmModel.setDedAmt(rs.getInt("ded_amt"));
                bmModel.setPvtDedAmt(rs.getInt("pvt_ded_amt"));
                bmModel.setVouchered(rs.getString("vouchered"));
                bmModel.setOffCode(rs.getString("off_code"));
                bmModel.setBillGroupDesc(rs.getString("bill_group_desc"));
                bmModel.setDemandNo(rs.getString("demand_no"));
                bmModel.setDdoCode(rs.getString("ddo_code"));
                bmModel.setTokenNo(rs.getString("token_no"));
            //rec_by, desg, notif_no, gross_amt, ded_amt, pvt_ded_amt, vouchered, off_code,bill_group_desc, demand_no, ddo_code, token_no,

                bmModel.setTokenDate(rs.getDate("token_date"));
                bmModel.setBillStatusId(rs.getInt("bill_status_id"));
                bmModel.setBenRefNo(rs.getString("ben_ref_no"));
                bmModel.setBillGroupId(rs.getInt("bill_group_id"));
                bmModel.setPreviousTokenNo(rs.getString("previous_token_no"));
                bmModel.setTokenDate(rs.getDate("previous_token_date"));
                bmModel.setIsResubmitted(rs.getString("is_resubmitted"));
                bmModel.setIsBillPrepared(rs.getString("is_bill_prepared"));
            //token_date, bill_status_id, ben_ref_no, bill_group_id, previous_token_no,previous_token_date, is_resubmitted, is_bill_prepared,

                bmModel.setPreviousBillNo(rs.getInt("previous_bill_no"));
                bmModel.setBillType(rs.getString("bill_type"));
                bmModel.setTypeOfBill(rs.getString("type_of_bill"));
                bmModel.setReprocessOccurance(rs.getInt("reprocess_occurance"));
                bmModel.setIsCleaned(rs.getString("is_cleaned"));
            //previous_bill_no, bill_type, type_of_bill,reprocess_occurance,is_cleaned

                bmModel.setDdoSpc(rs.getString("ddo_spc"));
                bmModel.setDdoEmpId(rs.getString("ddo_emp_id"));
            //ddo_spc, ddo_emp_id

                billList.add(bmModel);
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
    public int deleteBill(int billid) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int result = 0;

        try {
            con = dataSource.getConnection();
            String delQry = "DELETE FROM bill_mast WHERE bill_no=?";
            pstmt = con.prepareStatement(delQry);
            pstmt.setInt(1, billid);

            result = pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return result;

    }

    @Override
    public int getBillStatus(int billid) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int status = 0;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT BILL_STATUS_ID FROM BILL_MAST WHERE BILL_NO=?");
            pstmt.setInt(1, billid);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                status = rs.getInt("BILL_STATUS_ID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return status;
    }

    @Override
    public void updateBillStatus(int billid, int billStatusId) {
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("UPDATE BILL_MAST SET BILL_STATUS_ID=? WHERE BILL_NO=?");
            pstmt.setInt(1, billStatusId);
            pstmt.setInt(2, billid);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }

    }

    @Override
    public void markBillAsPrepared(int billid) {
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("UPDATE BILL_MAST SET IS_BILL_PREPARED = 'Y' WHERE BILL_NO=?");
            pstmt.setInt(1, billid);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }

    }
    @Override
    public void updateBillTotaling(int billid){
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("UPDATE AQ_MAST SET gross_amt = getempgrosstotal(AQSL_NO) WHERE BILL_NO=?");
            pstmt.setInt(1, billid);
            pstmt.executeUpdate();
            pstmt = con.prepareStatement("UPDATE AQ_MAST SET ded_amt = getempdedtotal(AQSL_NO) WHERE BILL_NO=?");
            pstmt.setInt(1, billid);
            pstmt.executeUpdate();
            pstmt = con.prepareStatement("UPDATE AQ_MAST SET pvt_ded_amt = getemppvtdedtotal(AQSL_NO) WHERE BILL_NO=?");
            pstmt.setInt(1, billid);
            pstmt.executeUpdate();
            pstmt = con.prepareStatement("UPDATE BILL_MAST SET gross_amt = (SELECT SUM(gross_amt) FROM AQ_MAST WHERE BILL_NO=?) WHERE BILL_NO=?");
            pstmt.setInt(1, billid);
            pstmt.setInt(2, billid);
            pstmt.executeUpdate();
            pstmt = con.prepareStatement("UPDATE BILL_MAST SET ded_amt = (SELECT SUM(ded_amt) FROM AQ_MAST WHERE BILL_NO=?) WHERE BILL_NO=?");
            pstmt.setInt(1, billid);
            pstmt.setInt(2, billid);
            pstmt.executeUpdate();
            pstmt = con.prepareStatement("UPDATE BILL_MAST SET pvt_ded_amt = (SELECT SUM(pvt_ded_amt) FROM AQ_MAST WHERE BILL_NO=?) WHERE BILL_NO=?");
            pstmt.setInt(1, billid);
            pstmt.setInt(2, billid);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }
}
