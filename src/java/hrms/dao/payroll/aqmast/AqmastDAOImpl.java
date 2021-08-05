package hrms.dao.payroll.aqmast;

import hrms.common.CommonFunctions;
import hrms.common.DataBaseFunctions;
import hrms.model.payroll.aqmast.AqMastBean;
import hrms.model.payroll.aqmast.AqmastModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.apache.commons.lang.StringUtils;

public class AqmastDAOImpl implements AqmastDAO {

    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    

    @Override
    public String saveAqmastdata(AqmastModel aqmast) {        
        Connection con = null;
        PreparedStatement pstmt = null;
        String aqslNo = "";
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
        try {
            con = dataSource.getConnection();            
            aqslNo = aqmast.getBillNo() + "_" + aqmast.getPayMonth() + "_" + aqmast.getPayYear() + "_" + aqmast.getSlno();
            //pstmt = con.prepareStatement("DELETE FROM aq_mast WHERE aqsl_no=?");
            //pstmt.setString(1, aqslNo);
            //pstmt.executeUpdate();
            pstmt = con.prepareStatement("insert into aq_mast(aqsl_no,aq_group,aq_group_desc ,p_month , p_year , pay_type ,aq_date ,aq_month ,aq_year ,aq_type ,ref_ord ,ref_date ,aq_day ,pay_day , "
                    + "major_head ,major_head_desc ,sub_major_head ,sub_major_head_desc ,minor_head ,minor_head_desc ,sub_minor_head1,sub_minor_head1_desc , "
                    + "sub_minor_head2 ,sub_minor_head2_desc ,sub_minor_head3 ,sub_minor_head3_desc ,plan ,sector ,alt_unit ,off_code ,off_ddo ,sec_sl_no , "
                    + " section ,post_sl_no ,cur_desg ,cur_grade ,cur_level ,gazetted ,pay_scale ,mon_basic ,emp_code ,emp_name ,gpf_type ,gpf_acc_no,cur_basic,bill_no, "
                    + " bill_date ,bank_name,branch_name ,bank_acc_no ,default_bank ,remark ,spc_ord_no ,spc_ord_date,acct_type ,cur_spc ,emp_type ,dep_code,ifsc_code) values(?,?,?,?,?,?,?,?,?,?,?,"
                    + "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            pstmt.setString(1, aqslNo);
            pstmt.setBigDecimal(2, aqmast.getAqGroup());
            pstmt.setString(3, aqmast.getAqGroupDesc());
            pstmt.setInt(4, aqmast.getPayMonth());
            pstmt.setInt(5, aqmast.getPayYear());
            pstmt.setString(6, aqmast.getPayType());
            pstmt.setDate(7, new java.sql.Date(aqmast.getAqDate().getTime()));
            pstmt.setInt(8, aqmast.getAqMonth());
            pstmt.setInt(9, aqmast.getAqYear());
            pstmt.setString(10, aqmast.getAqType());
            pstmt.setString(11, aqmast.getRefOrder());
            if (aqmast.getRefDate() != null) {
                pstmt.setDate(12, new java.sql.Date(aqmast.getRefDate().getTime()));
            } else {
                pstmt.setDate(12, null);
            }
            pstmt.setInt(13, aqmast.getAqDay());
            pstmt.setInt(14, aqmast.getPayDay());
            pstmt.setString(15, aqmast.getMajorHead());
            pstmt.setString(16, aqmast.getMajorHeadDesc());
            pstmt.setString(17, aqmast.getSubMajorHead());
            pstmt.setString(18, aqmast.getSubMajorHeadDesc());
            pstmt.setString(19, aqmast.getMinorHead());
            pstmt.setString(20, aqmast.getMinorHeadDesc());
            pstmt.setString(21, aqmast.getSubMinorHead1());
            pstmt.setString(22, aqmast.getSubMinorHeadDesc1());
            pstmt.setString(23, aqmast.getSubMinorHead2());
            pstmt.setString(24, aqmast.getSubMinorHeadDesc2());
            pstmt.setString(25, aqmast.getSubMinorHead3());
            pstmt.setString(26, aqmast.getSubMinorHeadDesc3());
            pstmt.setString(27, aqmast.getPlan());
            pstmt.setString(28, aqmast.getSector());
            pstmt.setString(29, aqmast.getAltUnit());
            pstmt.setString(30, aqmast.getOffCode());
            pstmt.setString(31, aqmast.getOffDdo());
            pstmt.setInt(32, aqmast.getSecSlNo());
            pstmt.setString(33, aqmast.getSection());
            pstmt.setInt(34, aqmast.getPostSlNo());
            pstmt.setString(35, aqmast.getCurDesg());
            pstmt.setString(36, aqmast.getCurGrade());
            pstmt.setString(37, aqmast.getCurLevel());
            pstmt.setString(38, aqmast.getGazetted());
            pstmt.setString(39, aqmast.getPayScale());
            pstmt.setLong(40, aqmast.getMonBasic());
            pstmt.setString(41, aqmast.getEmpCode());
            pstmt.setString(42, aqmast.getEmpName());
            pstmt.setString(43, aqmast.getGpfType());
            pstmt.setString(44, aqmast.getGpfAccNo());
            pstmt.setLong(45, aqmast.getCurBasic());
            pstmt.setInt(46, aqmast.getBillNo());
            if (aqmast.getBillDate() != null) {
                pstmt.setDate(47, new java.sql.Date(aqmast.getBillDate().getTime()));
            }else{
                pstmt.setDate(47,null);
            }
            pstmt.setString(48, aqmast.getBankName());
            pstmt.setString(49, aqmast.getBranchName());
            pstmt.setString(50, aqmast.getBankAccNo());
            pstmt.setInt(51, aqmast.getDefaultBank());
            pstmt.setString(52, aqmast.getRemark());
            pstmt.setString(53, aqmast.getSpcOrdNo());
            if (aqmast.getSpcOrdDate() != null) {
                pstmt.setDate(54, new java.sql.Date(aqmast.getSpcOrdDate().getTime()));
            }else{
                pstmt.setDate(54,null);
            }
            pstmt.setString(55, aqmast.getAcctType());
            pstmt.setString(56, aqmast.getCurSpc());
            pstmt.setString(57, aqmast.getEmpType());
            pstmt.setString(58, StringUtils.defaultIfEmpty(aqmast.getDeptCode(), "02"));
            pstmt.setString(59, aqmast.getIfscCode());
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            DataBaseFunctions.closeSqlObjects(pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return aqslNo;
    }

    @Override
    public AqmastModel getAqmastDetail(String aqslno) {
        AqmastModel aqmast = null;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("select aqsl_no,aq_group,aq_group_desc ,p_month , p_year , pay_type ,aq_date ,aq_month ,aq_year ,aq_type ,ref_ord ,ref_date ,aq_day ,pay_day , "
                    + "major_head ,major_head_desc ,sub_major_head ,sub_major_head_desc ,minor_head ,minor_head_desc ,sub_minor_head1,sub_minor_head1_desc , "
                    + "sub_minor_head2 ,sub_minor_head2_desc ,sub_minor_head3 ,sub_minor_head3_desc ,plan ,sector ,alt_unit ,off_code ,off_ddo ,sec_sl_no , "
                    + " section ,post_sl_no ,cur_desg ,cur_grade ,cur_level ,gazetted ,pay_scale ,mon_basic ,emp_code ,emp_name ,gpf_type ,gpf_acc_no,cur_basic,bill_no, "
                    + " bill_date ,bank_name,branch_name ,bank_acc_no ,default_bank ,remark ,spc_ord_no ,spc_ord_date,acct_type ,cur_spc ,emp_type ,dep_code from "
                    + "  aq_mast where aqsl_no=?");
            pstmt.setString(1, aqslno);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                aqmast = new AqmastModel();
                aqmast.setAqGroup(rs.getBigDecimal("aq_group"));
                aqmast.setAqGroupDesc(rs.getString("aq_group_desc"));
                aqmast.setPayMonth(rs.getInt("p_month"));
                aqmast.setPayYear(rs.getInt("p_year"));
                aqmast.setPayType(rs.getString("pay_type"));
                aqmast.setAqDate(rs.getDate("aq_date"));
                aqmast.setAqMonth(rs.getInt("aq_month"));
                aqmast.setAqYear(rs.getInt("aq_year"));
                aqmast.setAqType(rs.getString("aq_type"));
                aqmast.setRefOrder(rs.getString("ref_ord"));
                aqmast.setRefDate(rs.getDate("ref_date"));
                aqmast.setAqDay(rs.getInt("aq_day"));
                aqmast.setPayDay(rs.getInt("pay_day"));
                aqmast.setMajorHead(rs.getString("major_head"));
                aqmast.setMajorHeadDesc(rs.getString("major_head_desc"));
                aqmast.setSubMajorHead(rs.getString("sub_major_head"));
                aqmast.setSubMajorHeadDesc(rs.getString("sub_major_head_desc"));
                aqmast.setMinorHead(rs.getString("minor_head"));
                aqmast.setMinorHeadDesc(rs.getString("minor_head_desc"));
                aqmast.setSubMinorHead1(rs.getString("sub_minor_head1"));
                aqmast.setSubMinorHeadDesc1(rs.getString("sub_minor_head1_desc"));
                aqmast.setSubMinorHead2(rs.getString("sub_minor_head2"));
                aqmast.setSubMinorHeadDesc2(rs.getString("sub_minor_head2_desc"));
                aqmast.setSubMinorHead3(rs.getString("sub_minor_head3"));
                aqmast.setSubMinorHeadDesc3(rs.getString("sub_minor_head3_desc"));
                aqmast.setPlan(rs.getString("plan"));
                aqmast.setSector(rs.getString("sector"));
                aqmast.setAltUnit(rs.getString("alt_unit"));
                aqmast.setOffCode(rs.getString("off_code"));
                aqmast.setOffDdo(rs.getString("off_ddo"));
                aqmast.setSecSlNo(rs.getInt("sec_sl_no"));
                aqmast.setSection(rs.getString("section"));
                aqmast.setPostSlNo(rs.getInt("post_sl_no"));
                aqmast.setCurDesg(rs.getString("cur_desg"));
                aqmast.setCurGrade(rs.getString("cur_grade"));
                aqmast.setCurLevel(rs.getString("cur_level"));
                aqmast.setGazetted(rs.getString("gazetted"));
                aqmast.setPayScale(rs.getString("pay_scale"));
                aqmast.setMonBasic(rs.getInt("mon_basic"));
                aqmast.setEmpCode(rs.getString("emp_name"));
                aqmast.setGpfType(rs.getString("gpf_type"));
                aqmast.setGpfAccNo(rs.getString("gpf_acc_no"));
                aqmast.setCurBasic(rs.getInt("cur_basic"));
                aqmast.setBillNo(rs.getInt("bill_no"));
                aqmast.setBillDate(rs.getDate("bill_date"));
                aqmast.setBankName(rs.getString("bank_name"));
                aqmast.setBranchName(rs.getString("branch_name"));
                aqmast.setBankAccNo(rs.getString("bank_acc_no"));
                aqmast.setDefaultBank(rs.getInt("default_bank"));
                aqmast.setRemark(rs.getString("remark"));
                aqmast.setSpcOrdNo(rs.getString("spc_ord_no"));
                aqmast.setSpcOrdDate(rs.getDate("spc_ord_date"));
                aqmast.setAcctType(rs.getString("acct_type"));
                aqmast.setCurSpc(rs.getString("cur_spc"));
                aqmast.setEmpType(rs.getString("emp_type"));
                aqmast.setDeptCode(rs.getString("dep_code"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return aqmast;
    }

    @Override
    public int deleteAqmastData(String aqslno) {
        int n = 0;
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("delete from aq_mast where aqsl_no=?");
            pstmt.setString(1, aqslno);
            n = pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            DataBaseFunctions.closeSqlObjects(pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return n;
    }

    @Override
    public List getAqmastList(int billno) {
        List aqList = new ArrayList();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        AqMastBean aqmBean = null;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("select aqsl_no,aq_group,aq_group_desc ,p_month , p_year , pay_type ,aq_date ,aq_month ,aq_year ,aq_type ,ref_ord ,ref_date ,aq_day ,pay_day , "
                    + "major_head ,major_head_desc ,sub_major_head ,sub_major_head_desc ,minor_head ,minor_head_desc ,sub_minor_head1,sub_minor_head1_desc , "
                    + "sub_minor_head2 ,sub_minor_head2_desc ,sub_minor_head3 ,sub_minor_head3_desc ,plan ,sector ,alt_unit ,off_code ,off_ddo ,sec_sl_no , "
                    + " section ,post_sl_no ,cur_desg ,cur_grade ,cur_level ,gazetted ,pay_scale ,mon_basic ,emp_code ,emp_name ,gpf_type ,gpf_acc_no,cur_basic,bill_no, "
                    + " bill_date ,bank_name,branch_name ,bank_acc_no ,default_bank ,remark ,spc_ord_no ,spc_ord_date,acct_type ,cur_spc ,emp_type ,dep_code from "
                    + "  aq_mast where bill_no=?");
            pstmt.setInt(1, billno);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                aqmBean = new AqMastBean();
                aqmBean.setAqSlNo(rs.getString("aqsl_no"));
                aqmBean.setAqGroup(rs.getInt("aq_group"));
                aqmBean.setAqGroupDesc(rs.getString("aq_group_desc"));
                aqmBean.setPayMonth(rs.getInt("p_month"));
                aqmBean.setPayYear(rs.getInt("p_year"));
                aqmBean.setPayType(rs.getString("pay_type"));
                aqmBean.setAqDate(CommonFunctions.getFormattedOutputDate3(rs.getDate("aq_date")));
                aqmBean.setAqMonth(rs.getInt("aq_month"));
                aqmBean.setAqYear(rs.getInt("aq_year"));
                aqmBean.setAqType(rs.getString("aq_type"));
                aqmBean.setRefOrder(rs.getString("ref_ord"));
                aqmBean.setRefDate(CommonFunctions.getFormattedOutputDate3(rs.getDate("ref_date")));
                aqmBean.setAqDay(rs.getInt("aq_day"));
                aqmBean.setPayDay(rs.getInt("pay_day"));
                aqmBean.setMajorHead(rs.getString("major_head"));
                aqmBean.setMajorHeadDesc(rs.getString("major_head_desc"));
                aqmBean.setSubMajorHead(rs.getString("sub_major_head"));
                aqmBean.setSubMajorHeadDesc(rs.getString("sub_major_head_desc"));
                aqmBean.setMinorHead(rs.getString("minor_head"));
                aqmBean.setMinorHeadDesc(rs.getString("minor_head_desc"));
                aqmBean.setSubMinorHead1(rs.getString("sub_minor_head1"));
                aqmBean.setSubMinorHeadDesc1(rs.getString("sub_minor_head1_desc"));
                aqmBean.setSubMinorHead2(rs.getString("sub_minor_head2"));
                aqmBean.setSubMinorHeadDesc2(rs.getString("sub_minor_head2_desc"));
                aqmBean.setSubMinorHead3(rs.getString("sub_minor_head3"));
                aqmBean.setSubMinorHeadDesc3(rs.getString("sub_minor_head3_desc"));
                aqmBean.setPlan(rs.getString("plan"));
                aqmBean.setSector(rs.getString("sector"));
                aqmBean.setAltUnit(rs.getString("alt_unit"));
                aqmBean.setOffCode(rs.getString("off_code"));
                aqmBean.setOffDdo(rs.getString("off_ddo"));
                aqmBean.setSecSlNo(rs.getInt("sec_sl_no"));
                aqmBean.setSection(rs.getString("section"));
                aqmBean.setPostSlNo(rs.getInt("post_sl_no"));
                aqmBean.setCurDesg(rs.getString("cur_desg"));
                aqmBean.setCurGrade(rs.getString("cur_grade"));
                aqmBean.setCurLevel(rs.getString("cur_level"));
                aqmBean.setGazetted(rs.getString("gazetted"));
                aqmBean.setPayScale(rs.getString("pay_scale"));
                aqmBean.setMonBasic(rs.getInt("mon_basic"));
                aqmBean.setEmpCode(rs.getString("emp_name"));
                aqmBean.setGpfType(rs.getString("gpf_type"));
                aqmBean.setGpfAccNo(rs.getString("gpf_acc_no"));
                aqmBean.setCurBasic(rs.getInt("cur_basic"));
                aqmBean.setBillNo(rs.getInt("bill_no"));
                aqmBean.setBillDate(CommonFunctions.getFormattedOutputDate3(rs.getDate("bill_date")));
                aqmBean.setBankName(rs.getString("bank_name"));
                aqmBean.setBranchName(rs.getString("branch_name"));
                aqmBean.setBankAccNo(rs.getString("bank_acc_no"));
                aqmBean.setDefaultBank(rs.getInt("default_bank"));
                aqmBean.setRemark(rs.getString("remark"));
                aqmBean.setSpcOrdNo(rs.getString("spc_ord_no"));
                aqmBean.setSpcOrdDate(CommonFunctions.getFormattedOutputDate3(rs.getDate("spc_ord_date")));
                aqmBean.setAcctType(rs.getString("acct_type"));
                aqmBean.setCurSpc(rs.getString("cur_spc"));
                aqmBean.setEmpType(rs.getString("emp_type"));
                aqmBean.setDeptCode(rs.getString("dep_code"));
                aqList.add(aqmBean);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return aqList;
    }
    
    @Override
    public boolean stopNpsDeduction(String empId, int billMonth, int billYear) {
        boolean hastobeStop = false;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String str = "";

        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("select dos  from emp_mast where emp_id =? ");
            pstmt.setString(1, empId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                str = CommonFunctions.getFormattedOutputDate1(rs.getDate("dos"));
            }
            if (str != null) {
                Date dt = new Date(str);

                Calendar billcal1 = Calendar.getInstance();
                billcal1.set(Calendar.YEAR, billYear);
                billcal1.set(Calendar.MONTH, billMonth);

                Calendar dosCal = Calendar.getInstance();
                dosCal.setTime(dt);

                int diffYear = dosCal.get(Calendar.YEAR) - billcal1.get(Calendar.YEAR);
                int diffMonth = diffYear * 12 + dosCal.get(Calendar.MONTH) - billcal1.get(Calendar.MONTH);

                if ( diffMonth <=0) {
                    hastobeStop = true;
                }
                System.out.println("stop subscription==" + hastobeStop);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return hastobeStop;
    }

    @Override
    public boolean stopGpfDeduction(String empId, int billMonth, int billYear) {
        boolean hastobeStop = false;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String str = "";

        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("select dos  from emp_mast where emp_id =? ");
            pstmt.setString(1, empId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                str = CommonFunctions.getFormattedOutputDate1(rs.getDate("dos"));
            }
            if (str != null) {
                Date dt = new Date(str);

                Calendar billcal1 = Calendar.getInstance();
                billcal1.set(Calendar.YEAR, billYear);
                billcal1.set(Calendar.MONTH, billMonth);

                Calendar dosCal = Calendar.getInstance();
                dosCal.setTime(dt);

                int diffYear = dosCal.get(Calendar.YEAR) - billcal1.get(Calendar.YEAR);
                int diffMonth = diffYear * 12 + dosCal.get(Calendar.MONTH) - billcal1.get(Calendar.MONTH);

                if ( diffMonth <= 3) {
                    hastobeStop = true;
                }
                System.out.println("stop subscription==" + hastobeStop);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return hastobeStop;
    }
    /* pay held up code */
    @Override
    public boolean stopSalaryForPayHeldUp(String empId){
        Connection con=null;
        PreparedStatement ps=null;
        ResultSet rs=null;
        boolean stopSalary=false;
        try {
            con = dataSource.getConnection();
            ps = con.prepareStatement("select * from emp_pay_heldup where emp_id=? and to_date is null ");
            ps.setString(1, empId);
            rs = ps.executeQuery();
            if (rs.next()) {
                stopSalary=true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, ps);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return stopSalary;
    }
}
