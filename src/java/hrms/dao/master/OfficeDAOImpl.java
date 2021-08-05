/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.master;

import hrms.SelectOption;
import hrms.common.DataBaseFunctions;
import hrms.model.master.Office;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 *
 * @author Durga
 */
public class OfficeDAOImpl implements OfficeDAO {

    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List getOfficeList(String deptcode, String offSearch, int page, int rows) {
        Connection con = null;

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        List officelist = new ArrayList();
        SelectOption so = null;

        int minlimit = rows * (page - 1);
        int maxlimit = rows;
        //System.out.println("officeName is: "+offSearch);
        try {
            con = dataSource.getConnection();

            if (offSearch != null && !offSearch.equals("")) {
                pstmt = con.prepareStatement("SELECT off_code,off_en from g_office where department_code=? and off_en like ? order by off_en LIMIT ? OFFSET ?");
                pstmt.setString(1, deptcode);
                pstmt.setString(2, "%" + offSearch.toUpperCase() + "%");
                pstmt.setInt(3, maxlimit);
                pstmt.setInt(4, minlimit);
            } else {
                pstmt = con.prepareStatement("SELECT off_code,off_en from g_office where department_code=? order by off_en LIMIT ? OFFSET ?");
                pstmt.setString(1, deptcode);
                pstmt.setInt(2, maxlimit);
                pstmt.setInt(3, minlimit);
            }
            rs = pstmt.executeQuery();
            while (rs.next()) {
                so = new SelectOption();
                so.setValue(rs.getString("off_code"));
                so.setLabel(rs.getString("off_en"));
                officelist.add(so);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return officelist;
    }

    @Override
    public List getFieldOffList(String offCode) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        SelectOption so = null;

        ArrayList alist = new ArrayList();
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("select off_code,off_name from g_office where p_off_code=? order by off_en");
            pstmt.setString(1, offCode);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                so = new SelectOption();
                so.setLabel(rs.getString("off_name"));
                so.setValue(rs.getString("off_code"));
                alist.add(so);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return alist;
    }

    @Override
    public int getOfficeListCount(String deptcode, String offSearch) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        int cnt = 0;

        try {
            con = this.dataSource.getConnection();

            String sql = "";
            if (offSearch != null && !offSearch.equals("")) {
                sql = "SELECT COUNT(*) CNT FROM G_OFFICE WHERE DEPARTMENT_CODE=? AND OFF_EN LIKE ?";
                pst = con.prepareStatement(sql);
                pst.setString(1, deptcode);
                pst.setString(2, "%" + offSearch.toUpperCase() + "%");
            } else {
                sql = "SELECT COUNT(*) CNT FROM G_OFFICE WHERE DEPARTMENT_CODE=?";
                pst = con.prepareStatement(sql);
                pst.setString(1, deptcode);
            }
            rs = pst.executeQuery();
            if (rs.next()) {
                cnt = rs.getInt("CNT");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return cnt;
    }

    @Override
    public List getTotalOfficeList(String deptcode) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List officelist = new ArrayList();
        Office office = null;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT off_code,off_en,ddo_code from g_office where department_code=? order by off_en asc");
            pstmt.setString(1, deptcode);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                office = new Office();
                office.setOffCode(rs.getString("off_code"));
                office.setOffName(rs.getString("off_en") + "(" + rs.getString("ddo_code") + ")");
                office.setDdoCode(rs.getString("ddo_code"));
                officelist.add(office);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return officelist;
    }

    @Override
    public List getDistrictWiseOfficeList(String distCode) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List officelist = new ArrayList();
        Office office = null;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT off_code,off_en from g_office where dist_code=? order by off_en");
            pstmt.setString(1, distCode);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                office = new Office();
                office.setOffCode(rs.getString("off_code"));
                office.setOffName(rs.getString("off_en"));
                officelist.add(office);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return officelist;
    }

    @Override
    public Office getOfficeDetails(String offCode) {
        Office off = null;
        ResultSet rs = null;
        PreparedStatement pst = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("SELECT * FROM g_office WHERE off_code = ?");
            System.out.println("^^^^^^^offCode^^^^^^^^^^^^"+offCode);
            pst.setString(1, offCode);
            rs = pst.executeQuery();

            while (rs.next()) {
                off = new Office();
                off.setOffCode(rs.getString("off_code"));
                off.setOffEn(rs.getString("off_en"));
                off.setOffName(rs.getString("off_name"));
                off.setDeptCode(rs.getString("department_code"));
                off.setCategory(rs.getString("category"));
                off.setSuffix(rs.getString("suffix"));
                off.setLvl(rs.getString("lvl"));
                off.setIfGroup(rs.getString("if_group"));
                off.setOffAddress(rs.getString("off_address"));
                off.setStateCode(rs.getString("state_code"));
                off.setDistCode(rs.getString("dist_code"));
                off.setBlockCode(rs.getString("block_code"));
                off.setPsCode(rs.getString("ps_code"));
                off.setPoCode(rs.getString("po_code"));
                off.setVillCode(rs.getString("vill_code"));
                off.setPincode(rs.getString("pincode"));
                off.setTrCode(rs.getString("tr_code"));
                off.setDdoCode(rs.getString("ddo_code"));
                off.setDdoPost(rs.getString("ddo_post"));
                off.setTelStd(rs.getString("tel_std"));
                off.setTelNo(rs.getString("tel_no"));
                off.setFaxStd(rs.getString("fax_std"));
                off.setFaxNo(rs.getString("fax_no"));
                off.setOffEmail(rs.getString("off_email"));
                off.setOffIncharge(rs.getString("off_incharge"));
                System.out.println("!!!!!!!!!!!!!!!"+rs.getString("p_off_code"));
                off.setPOffCode(rs.getString("p_off_code"));
                off.setBankCode(rs.getString("bank_code"));
                off.setBranchCode(rs.getString("branch_code"));
                off.setRecBy(rs.getString("rec_by"));
                off.setDesg(rs.getString("desg"));
                off.setSalHead(rs.getString("sal_head"));
                off.setSalHeadDesc(rs.getString("sal_head_desc"));
                off.setOffStatus(rs.getString("off_status"));
                off.setDdoRegNo(rs.getString("ddo_reg_no"));
                off.setTanNo(rs.getString("tan_no"));
                off.setDtoRegNo(rs.getString("dto_reg_no"));
                off.setOnlineBillSubmission(rs.getString("online_bill_submission"));
                off.setPaCode(rs.getString("pa_code"));
                off.setDdoCurAccNo(rs.getString("ddo_cur_acc_no"));
                off.setIsDdo(rs.getString("is_ddo"));
                off.setOffBillStatus(rs.getString("off_bill_status"));
                off.setDdoSpc(rs.getString("ddo_spc"));
                off.setDdoHrmsid(rs.getString("ddo_hrmsid"));
                off.setOfficeCategoryId(rs.getString("office_category_id"));
                off.setHodOfficeCode(rs.getString("hod_office_code"));
                off.setSubDivisionCode(rs.getString("sub_division_code"));
                off.setPaybillPriority(rs.getInt("paybill_priority"));
                off.setPTypeId(rs.getString("p_type_id"));
                off.setExtFieldCode(rs.getString("ext_field_code"));
                off.setHaveSubOffice(rs.getString("have_sub_office"));
                off.setOffAbbrev(rs.getString("off_abbrev"));
                off.setIntDdoId(rs.getString("int_ddo_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return off;
    }

    @Override
    public boolean saveOfficeDetails(Office off) {
        int n = 0;
        boolean flag = false;
        PreparedStatement pst = null;
        PreparedStatement pst1 = null;
        ResultSet rs = null;
        Connection con = null;
        String offCode = "";
        String newOffCode = "";
        String maxCode = "";
        int mCode = 0;
        try {
            con = dataSource.getConnection();
            System.out.println("------^^^^-----" + off.getDdoCode());
            pst1 = con.prepareStatement("select max(cast(substring(off_code,10) as int)+1) offcode from g_office where ddo_code=?");
            pst1.setString(1, off.getDdoCode());
            rs = pst1.executeQuery();
            if (rs.next()) {
                if (rs.getInt("offcode") == 0) {
                    offCode = off.getDdoCode() + "0000";
                } else {
                    // mCode = rs.getInt("offcode") + 1;
                    maxCode = "0000" + "" + rs.getInt("offcode");
                    System.out.println("Last 4 char String: " + maxCode.substring(maxCode.length() - 4));
                    offCode = off.getDdoCode() + maxCode.substring(maxCode.length() - 4);
                }
            }

            pst = con.prepareStatement("INSERT INTO g_office(off_code,off_en,off_name,department_code,category,suffix,lvl,if_group,off_address,state_code,dist_code,block_code,ps_code,po_code,vill_code,pincode,tr_code,ddo_code,ddo_post,tel_std,tel_no,fax_std,fax_no,off_email,off_incharge,p_off_code,bank_code,branch_code,rec_by,desg,sal_head,sal_head_desc,off_status,ddo_reg_no,tan_no,dto_reg_no,online_bill_submission,pa_code,ddo_cur_acc_no,is_ddo,off_bill_status,ddo_spc,ddo_hrmsid,office_category_id,hod_office_code,sub_division_code,paybill_priority,p_type_id,ext_field_code,have_sub_office,off_abbrev,int_ddo_id) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

            pst.setString(1, offCode);
            pst.setString(2, off.getOffEn());
            pst.setString(3, off.getOffName());
            pst.setString(4, off.getDeptCode());
            pst.setString(5, off.getCategory());
            pst.setString(6, off.getSuffix());
            pst.setString(7, off.getLvl());
            pst.setString(8, off.getIfGroup());
            pst.setString(9, off.getOffAddress());
            pst.setString(10, off.getStateCode());
            pst.setString(11, off.getDistCode());
            pst.setString(12, off.getBlockCode());
            pst.setString(13, off.getPsCode());
            pst.setString(14, off.getPoCode());
            pst.setString(15, off.getVillCode());
            pst.setString(16, off.getPincode());
            pst.setString(17, off.getTrCode());
            pst.setString(18, off.getDdoCode());
            pst.setString(19, off.getDdoPost());
            pst.setString(20, off.getTelStd());
            pst.setString(21, off.getTelNo());
            pst.setString(22, off.getFaxStd());
            pst.setString(23, off.getFaxNo());
            pst.setString(24, off.getOffEmail());
            pst.setString(25, off.getOffIncharge());
            pst.setString(26, off.getPOffCode());
            pst.setString(27, off.getBankCode());
            pst.setString(28, off.getBranchCode());
            pst.setString(29, off.getRecBy());
            pst.setString(30, off.getDesg());
            pst.setString(31, off.getSalHead());
            pst.setString(32, off.getSalHeadDesc());
            pst.setString(33, off.getOffStatus());
            pst.setString(34, off.getDdoRegNo());
            pst.setString(35, off.getTanNo());
            pst.setString(36, off.getDtoRegNo());
            // pst.setString(37, off.getOnlineBillSubmission());
            pst.setString(37, "N");
            pst.setString(38, off.getPaCode());
            pst.setString(39, off.getDdoCurAccNo());
            pst.setString(40, off.getIsDdo());
            pst.setString(41, off.getOffBillStatus());
            pst.setString(42, off.getDdoSpc());
            pst.setString(43, off.getDdoHrmsid());
            pst.setString(44, off.getOfficeCategoryId());
            pst.setString(45, off.getHodOfficeCode());
            pst.setString(46, off.getSubDivisionCode());
            pst.setInt(47, off.getPaybillPriority());
            pst.setString(48, off.getPTypeId());
            pst.setString(49, off.getExtFieldCode());
            // pst.setString(50, off.getHaveSubOffice());
            if (off.getPOffCode() != null && !off.getPOffCode().equals("")) {
                pst.setString(50, "Y");
            } else {
                pst.setString(50, "N");
            }
            pst.setString(51, off.getOffAbbrev());
            pst.setString(52, off.getIntDdoId());

            n = pst.executeUpdate();
            if (n == 1) {
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return flag;
    }

    @Override
    public boolean updateOfficeDetails(Office off) {
        int n = 0;
        boolean flag = false;
        PreparedStatement pst = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("UPDATE g_office SET  off_en = ?, off_name = ?, department_code = ?, category = ?, suffix = ?, lvl = ?, if_group = ?, off_address = ?, state_code = ?, dist_code = ?, block_code = ?, ps_code = ?, po_code = ?, vill_code = ?, pincode = ?, tr_code = ?, ddo_code = ?, ddo_post = ?, tel_std = ?, tel_no = ?, fax_std = ?, fax_no = ?, off_email = ?, off_incharge = ?, p_off_code = ?, bank_code = ?, branch_code = ?, rec_by = ?, desg = ?, sal_head = ?, sal_head_desc = ?, off_status = ?, ddo_reg_no = ?, tan_no = ?, dto_reg_no = ?, online_bill_submission = ?, pa_code = ?, ddo_cur_acc_no = ?, is_ddo = ?, off_bill_status = ?, ddo_spc = ?, ddo_hrmsid = ?, office_category_id = ?, hod_office_code = ?, sub_division_code = ?, paybill_priority = ?, p_type_id = ?, ext_field_code = ?, have_sub_office = ?, off_abbrev = ?, int_ddo_id = ? WHERE off_code=?");

            pst.setString(1, off.getOffEn());
            pst.setString(2, off.getOffName());
            pst.setString(3, off.getDeptCode());
            pst.setString(4, off.getCategory());
            pst.setString(5, off.getSuffix());
            pst.setString(6, off.getLvl());
            pst.setString(7, off.getIfGroup());
            pst.setString(8, off.getOffAddress());
            pst.setString(9, off.getStateCode());
            pst.setString(10, off.getDistCode());
            pst.setString(11, off.getBlockCode());
            pst.setString(12, off.getPsCode());
            pst.setString(13, off.getPoCode());
            pst.setString(14, off.getVillCode());
            pst.setString(15, off.getPincode());
            pst.setString(16, off.getTrCode());
            pst.setString(17, off.getDdoCode());
            pst.setString(18, off.getDdoPost());
            pst.setString(19, off.getTelStd());
            pst.setString(20, off.getTelNo());
            pst.setString(21, off.getFaxStd());
            pst.setString(22, off.getFaxNo());
            pst.setString(23, off.getOffEmail());
            pst.setString(24, off.getOffIncharge());
            pst.setString(25, off.getPOffCode());
            pst.setString(26, off.getBankCode());
            pst.setString(27, off.getBranchCode());
            pst.setString(28, off.getRecBy());
            pst.setString(29, off.getDesg());
            pst.setString(30, off.getSalHead());
            pst.setString(31, off.getSalHeadDesc());
            pst.setString(32, off.getOffStatus());
            pst.setString(33, off.getDdoRegNo());
            pst.setString(34, off.getTanNo());
            pst.setString(35, off.getDtoRegNo());
            pst.setString(36, "N");
            pst.setString(37, off.getPaCode());
            pst.setString(38, off.getDdoCurAccNo());
            pst.setString(39, off.getIsDdo());
            pst.setString(40, off.getOffBillStatus());
            pst.setString(41, off.getDdoSpc());
            pst.setString(42, off.getDdoHrmsid());
            pst.setString(43, off.getOfficeCategoryId());
            pst.setString(44, off.getHodOfficeCode());
            pst.setString(45, off.getSubDivisionCode());
            pst.setInt(46, off.getPaybillPriority());
            pst.setString(47, off.getPTypeId());
            pst.setString(48, off.getExtFieldCode());
            if (off.getPOffCode() != null && !off.getPOffCode().equals("")) {
                pst.setString(49, "Y");
            } else {
                pst.setString(49, "N");
            }
            pst.setString(50, off.getOffAbbrev());
            pst.setString(51, off.getIntDdoId());
            pst.setString(52, off.getOffCode());

            n = pst.executeUpdate();
            if (n == 1) {
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return flag;
    }

    @Override
    public List getOfficeListFilter(String offcode) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List officelist = new ArrayList();
        SelectOption so = null;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT off_code,off_en,ddo_code from g_office where off_code=?");
            pstmt.setString(1, offcode);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                so = new SelectOption();
                so.setValue(rs.getString("off_code"));
                so.setLabel(rs.getString("off_en") + " (" + rs.getString("ddo_code") + ")");
                officelist.add(so);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return officelist;
    }
}
