/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.payroll.billbrowser;

import hrms.SelectOption;
import hrms.common.CommonFunctions;
import hrms.common.DataBaseFunctions;
import hrms.model.payroll.billbrowser.AssignPostList;
import hrms.model.payroll.billbrowser.SectionDefinition;
import hrms.model.payroll.billbrowser.SectionDtlSPCWiseEmp;
import hrms.model.payroll.grouppayfixation.GroupPayFixation;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Manas Jena
 */
public class SectionDefinationDAOImpl implements SectionDefinationDAO {

    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public ArrayList getSectionList(String offcode) {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        ArrayList al = new ArrayList();
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("select g_section.section_id,section_name,BILL_GROUP_MASTER.DESCRIPTION,(SELECT COUNT(*) FROM SECTION_POST_MAPPING WHERE SECTION_ID=g_section.section_id) NOOFPOST, "
                    + "(SELECT COUNT(*) FROM SECTION_POST_MAPPING INNER JOIN EMP_MAST ON SECTION_POST_MAPPING.SPC=EMP_MAST.CUR_SPC WHERE SECTION_ID=g_section.section_id)MANINPOSITION from g_section "
                    + "LEFT OUTER JOIN BILL_SECTION_MAPPING ON g_section.SECTION_ID = BILL_SECTION_MAPPING.SECTION_ID "
                    + "LEFT OUTER JOIN BILL_GROUP_MASTER ON BILL_GROUP_MASTER.BILL_GROUP_ID=BILL_SECTION_MAPPING.BILL_GROUP_ID "
                    + "where g_section.off_code=?");
            pstmt.setString(1, offcode);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                SectionDefinition secDef = new SectionDefinition();
                secDef.setSectionId(rs.getInt("section_id"));
                secDef.setSection(rs.getString("section_name"));
                secDef.setBillgroup(rs.getString("description"));
                secDef.setNofpost(rs.getInt("noofpost"));
                secDef.setMenInPos(rs.getInt("maninposition"));
                al.add(secDef);
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
    public ArrayList getBillGroupWiseSectionList(BigDecimal billgroupid) {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        ArrayList al = new ArrayList();
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT BILL_SECTION_MAPPING.SECTION_ID,SECTION_NAME,SEC_SL_NO,G_SECTION.BILL_TYPE FROM BILL_SECTION_MAPPING INNER JOIN G_SECTION ON BILL_SECTION_MAPPING.SECTION_ID=G_SECTION.SECTION_ID WHERE BILL_GROUP_ID=?");
            pstmt.setBigDecimal(1, billgroupid);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                SectionDefinition secDef = new SectionDefinition();
                secDef.setSectionId(rs.getInt("SECTION_ID"));
                secDef.setSection(rs.getString("SECTION_NAME"));
                secDef.setSecslno(rs.getInt("SEC_SL_NO"));
                secDef.setBillType(rs.getString("BILL_TYPE"));

                al.add(secDef);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return al;
    }

    public ArrayList getSPCWiseCont6RegularEmpInSection(int sectionid) {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        ArrayList emplist = new ArrayList();
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT section_post_mapping.spc,POST,EMP_ID,POST_SL_NO,ORDER_NO,ORDER_DATE,PAY_SCALE,"
                    + "CUR_BASIC_SALARY,EMP_MAST.GP,PREV_BASIC_SALARY,PREV_GP,PAY_DATE, "
                    + "F_NAME,M_NAME,L_NAME,DEP_CODE,EMP_MAST.BANK_CODE, EMP_MAST.BRANCH_CODE,BANK_ACC_NO,IFSC_CODE,G_SPC.OFF_CODE,DEP_CODE,ACCT_TYPE,GPF_NO,IS_GAZETTED FROM section_post_mapping "
                    + " INNER JOIN G_SPC ON section_post_mapping.SPC = G_SPC.SPC "
                    + " INNER JOIN G_POST ON G_SPC.GPC=G_POST.POST_CODE "
                    + " LEFT OUTER JOIN EMP_MAST ON section_post_mapping.spc = EMP_MAST.CUR_SPC "
                    + " left outer JOIN G_BRANCH ON EMP_MAST.BRANCH_CODE=G_BRANCH.BRANCH_CODE "
                    + " WHERE section_id=?");
            pstmt.setInt(1, sectionid);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                SectionDtlSPCWiseEmp sdswe = new SectionDtlSPCWiseEmp();
                sdswe.setSpc(rs.getString("spc"));
                sdswe.setOffcode(rs.getString("OFF_CODE"));
                sdswe.setPostname(rs.getString("POST"));
                sdswe.setEmpid(rs.getString("EMP_ID"));
                sdswe.setEmpname((StringUtils.defaultString(rs.getString("F_NAME")) + " " + StringUtils.defaultString(rs.getString("M_NAME")) + " " + StringUtils.defaultString(rs.getString("L_NAME"))).replaceAll("\\s+", " "));
                sdswe.setGpfaccno(rs.getString("GPF_NO"));
                sdswe.setAcctype(rs.getString("ACCT_TYPE"));
                sdswe.setPostslno(rs.getInt("POST_SL_NO"));
                sdswe.setOrderno(rs.getString("ORDER_NO"));
                sdswe.setOrderdate(rs.getDate("ORDER_DATE"));
                sdswe.setPayscale(rs.getString("PAY_SCALE"));
                sdswe.setBankcode(rs.getString("BANK_CODE"));
                sdswe.setBankaccno(rs.getString("BANK_ACC_NO"));
                sdswe.setBranchcode(rs.getString("BRANCH_CODE"));
                sdswe.setIfscCode(rs.getString("IFSC_CODE"));
                if (rs.getDate("PAY_DATE") != null) {
                    sdswe.setPayDate(new Date(rs.getDate("PAY_DATE").getTime()));
                }
                sdswe.setCurBasicSalary(rs.getInt("CUR_BASIC_SALARY"));
                sdswe.setGp(rs.getInt("GP"));
                sdswe.setPrevBasicSalary(rs.getInt("PREV_BASIC_SALARY"));
                //sdswe.setGp(rs.getInt("PREV_GP"));
                sdswe.setIsgazetted(rs.getString("IS_GAZETTED"));
                sdswe.setFname(rs.getString("F_NAME"));
                sdswe.setMname(rs.getString("M_NAME"));
                sdswe.setLname(rs.getString("L_NAME"));
                sdswe.setDepcode(rs.getString("DEP_CODE"));
                emplist.add(sdswe);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return emplist;
    }

    @Override
    public ArrayList getSPCWiseContEmpInSection(int sectionid) {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        ArrayList empList = new ArrayList();
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT section_post_mapping.spc,post_nomenclature,EMP_ID,POST_SL_NO,CUR_BASIC_SALARY,EMP_MAST.GP,PREV_BASIC_SALARY,PREV_GP,PAY_DATE, "
                    + "F_NAME,M_NAME,L_NAME,DEP_CODE,EMP_MAST.BANK_CODE, EMP_MAST.BRANCH_CODE,BANK_ACC_NO,IFSC_CODE,BRASS_NO,ACCT_TYPE,GPF_NO,JOB_TYPE_ID FROM section_post_mapping "
                    + " INNER JOIN EMP_MAST ON section_post_mapping.SPC = EMP_MAST.EMP_ID "
                    + " LEFT OUTER JOIN g_temp_jobtype ON EMP_MAST.JOB_TYPE_ID=g_temp_jobtype.jobtype_code "
                    + " left outer JOIN G_BRANCH ON EMP_MAST.BRANCH_CODE=G_BRANCH.BRANCH_CODE "
                    + " WHERE section_id=? ORDER BY post_sl_no");

            pstmt.setInt(1, sectionid);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                SectionDtlSPCWiseEmp sdswe = new SectionDtlSPCWiseEmp();
                sdswe.setPostname(rs.getString("post_nomenclature"));
                sdswe.setAcctype(rs.getString("ACCT_TYPE"));
                sdswe.setGpfaccno(rs.getString("GPF_NO"));
                sdswe.setEmpid(rs.getString("EMP_ID"));
                sdswe.setCurBasicSalary(rs.getInt("CUR_BASIC_SALARY"));
                sdswe.setGp(rs.getInt("GP"));
                sdswe.setEmpname((StringUtils.defaultString(rs.getString("F_NAME")) + " " + StringUtils.defaultString(rs.getString("M_NAME")) + " " + StringUtils.defaultString(rs.getString("L_NAME"))).replaceAll("\\s+", " "));
                sdswe.setBankcode(rs.getString("BANK_CODE"));
                sdswe.setBankaccno(rs.getString("BANK_ACC_NO"));
                sdswe.setBranchcode(rs.getString("BRANCH_CODE"));
                sdswe.setIfscCode(rs.getString("IFSC_CODE"));
                sdswe.setJobtypeid(rs.getInt("JOB_TYPE_ID"));
                empList.add(sdswe);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }

        return empList;
    }

    @Override
    public SectionDtlSPCWiseEmp getSPCEmpSection(String empId) {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        SectionDtlSPCWiseEmp sdswe = new SectionDtlSPCWiseEmp();
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT cur_spc,POST,EMP_ID,CUR_BASIC_SALARY,EMP_MAST.GP,PREV_BASIC_SALARY,PREV_GP,PAY_DATE, F_NAME,M_NAME,L_NAME,DEP_CODE,BANK_CODE, BRANCH_CODE,"
                    + "BANK_ACC_NO,G_SPC.OFF_CODE,DEP_CODE,ACCT_TYPE,GPF_NO,IS_GAZETTED FROM EMP_MAST "
                    + "INNER JOIN G_SPC ON EMP_MAST.CUR_SPC = G_SPC.SPC "
                    + "INNER JOIN G_POST ON G_SPC.GPC=G_POST.POST_CODE "
                    + "WHERE EMP_MAST.EMP_ID=");
            pstmt.setString(1, empId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                sdswe.setSpc(rs.getString("cur_spc"));
                sdswe.setOffcode(rs.getString("OFF_CODE"));
                sdswe.setPostname(rs.getString("POST"));
                sdswe.setEmpid(rs.getString("EMP_ID"));
                sdswe.setEmpname((StringUtils.defaultString(rs.getString("F_NAME")) + " " + StringUtils.defaultString(rs.getString("M_NAME")) + " " + StringUtils.defaultString(rs.getString("L_NAME"))).replaceAll("\\s+", " "));
                sdswe.setGpfaccno(rs.getString("GPF_NO"));
                sdswe.setAcctype(rs.getString("ACCT_TYPE"));
                sdswe.setBankcode(rs.getString("BANK_CODE"));
                sdswe.setBankaccno(rs.getString("BANK_ACC_NO"));
                sdswe.setBranchcode(rs.getString("BRANCH_CODE"));
                if (rs.getDate("PAY_DATE") != null) {
                    sdswe.setPayDate(new Date(rs.getDate("PAY_DATE").getTime()));
                }
                sdswe.setCurBasicSalary(rs.getInt("CUR_BASIC_SALARY"));
                sdswe.setGp(rs.getInt("GP"));
                sdswe.setPrevBasicSalary(rs.getInt("PREV_BASIC_SALARY"));
                //sdswe.setGp(rs.getInt("PREV_GP"));
                sdswe.setIsgazetted(rs.getString("IS_GAZETTED"));
                sdswe.setFname(rs.getString("F_NAME"));
                sdswe.setMname(rs.getString("M_NAME"));
                sdswe.setLname(rs.getString("L_NAME"));
                sdswe.setDepcode(rs.getString("DEP_CODE"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return sdswe;
    }

    @Override
    public ArrayList getSPCWiseEmpInSection(int sectionid) {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        ArrayList al = new ArrayList();
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT section_post_mapping.spc,POST,EMP_ID,POST_SL_NO,ORDER_NO,ORDER_DATE,PAY_SCALE,"
                    + "CUR_BASIC_SALARY,EMP_MAST.GP,PREV_BASIC_SALARY,PREV_GP,PAY_DATE, "
                    + "F_NAME,M_NAME,L_NAME,DEP_CODE,BANK_CODE, BRANCH_CODE,BANK_ACC_NO,G_SPC.OFF_CODE,DEP_CODE,ACCT_TYPE,GPF_NO,IS_GAZETTED,DOE_GOV FROM section_post_mapping "
                    + " INNER JOIN G_SPC ON section_post_mapping.SPC = G_SPC.SPC "
                    + " INNER JOIN G_POST ON G_SPC.GPC=G_POST.POST_CODE "
                    + " LEFT OUTER JOIN EMP_MAST ON section_post_mapping.spc = EMP_MAST.CUR_SPC WHERE section_id=?");
            pstmt.setInt(1, sectionid);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                SectionDtlSPCWiseEmp sdswe = new SectionDtlSPCWiseEmp();
                sdswe.setSpc(rs.getString("spc"));
                sdswe.setOffcode(rs.getString("OFF_CODE"));
                sdswe.setPostname(rs.getString("POST"));
                sdswe.setEmpid(rs.getString("EMP_ID"));
                sdswe.setDoegov(rs.getDate("DOE_GOV"));
                sdswe.setEmpname((StringUtils.defaultString(rs.getString("F_NAME")) + " " + StringUtils.defaultString(rs.getString("M_NAME")) + " " + StringUtils.defaultString(rs.getString("L_NAME"))).replaceAll("\\s+", " "));
                sdswe.setGpfaccno(rs.getString("GPF_NO"));
                sdswe.setAcctype(rs.getString("ACCT_TYPE"));
                sdswe.setPostslno(rs.getInt("POST_SL_NO"));
                sdswe.setOrderno(rs.getString("ORDER_NO"));
                sdswe.setOrderdate(rs.getDate("ORDER_DATE"));
                sdswe.setPayscale(rs.getString("PAY_SCALE"));
                sdswe.setBankcode(rs.getString("BANK_CODE"));
                sdswe.setBankaccno(rs.getString("BANK_ACC_NO"));
                sdswe.setBranchcode(rs.getString("BRANCH_CODE"));
                sdswe.setIfscCode(rs.getString("IFSC_CODE"));
                if (rs.getDate("PAY_DATE") != null) {
                    sdswe.setPayDate(new Date(rs.getDate("PAY_DATE").getTime()));
                }
                sdswe.setCurBasicSalary(rs.getInt("CUR_BASIC_SALARY"));
                sdswe.setGp(rs.getInt("GP"));
                sdswe.setPrevBasicSalary(rs.getInt("PREV_BASIC_SALARY"));
                //sdswe.setGp(rs.getInt("PREV_GP"));
                sdswe.setIsgazetted(rs.getString("IS_GAZETTED"));
                sdswe.setFname(rs.getString("F_NAME"));
                sdswe.setMname(rs.getString("M_NAME"));
                sdswe.setLname(rs.getString("L_NAME"));
                sdswe.setDepcode(rs.getString("DEP_CODE"));
                al.add(sdswe);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return al;
    }

    @Override
    public ArrayList getSPCWiseEmpInSection(int sectionid, String offcode, int pyear, int pmonth) {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        ArrayList al = new ArrayList();
        try {
           
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT SPC, POST,EMP_ID,POST_SL_NO,ORDER_NO,ORDER_DATE,PAY_SCALE,CUR_BASIC_SALARY,GP,PREV_BASIC_SALARY,PREV_GP,PAY_DATE,F_NAME,M_NAME,L_NAME,DEP_CODE,G_BRANCH.BANK_CODE,G_BRANCH.BRANCH_CODE,BANK_ACC_NO,IFSC_CODE,OFF_CODE,ACCT_TYPE,GPF_NO,IS_GAZETTED,DOE_GOV,FROM_DATE, RLV_DATE FROM ( "
                    + " SELECT section_post_mapping.spc,POST,EMP_MAST.EMP_ID,POST_SL_NO,ORDER_NO,ORDER_DATE,PAY_SCALE, "
                    + " CUR_BASIC_SALARY,EMP_MAST.GP,PREV_BASIC_SALARY,PREV_GP,PAY_DATE, "
                    + " F_NAME,M_NAME,L_NAME,DEP_CODE,BANK_CODE, BRANCH_CODE,BANK_ACC_NO,G_SPC.OFF_CODE,ACCT_TYPE,GPF_NO,IS_GAZETTED,DOE_GOV, FROM_DATE,CAST( NULL AS timestamp) AS RLV_DATE FROM section_post_mapping "
                    + " INNER JOIN G_SPC ON section_post_mapping.SPC = G_SPC.SPC "
                    + " INNER JOIN G_POST ON G_SPC.GPC=G_POST.POST_CODE "
                    + " LEFT OUTER JOIN EMP_MAST ON section_post_mapping.spc = EMP_MAST.CUR_SPC "
                    + " LEFT OUTER JOIN (SELECT * FROM EMP_PAY_HELDUP WHERE OFF_CODE=? AND TO_DATE IS NULL) AS EMP_PAY_HELDUP ON EMP_MAST.EMP_ID=EMP_PAY_HELDUP.EMP_ID "
                    + " WHERE section_id = ? "
                    + " UNION "
                    + " SELECT emp_relieve.SPC,POST,EMP_MAST.EMP_ID,POST_SL_NO,ORDER_NO,ORDER_DATE,PAY_SCALE, "
                    + " CUR_BASIC_SALARY,EMP_MAST.GP,PREV_BASIC_SALARY,PREV_GP,PAY_DATE, "
                    + " F_NAME,M_NAME,L_NAME,DEP_CODE,BANK_CODE, BRANCH_CODE,BANK_ACC_NO,G_SPC.OFF_CODE,ACCT_TYPE,GPF_NO,IS_GAZETTED,DOE_GOV,CAST( NULL AS timestamp),RLV_DATE FROM "
                    + " (SELECT EMP_ID,spc,RLV_DATE from emp_relieve where date_part('year', rlv_date) = ? and date_part('month', rlv_date)=? AND SUBSTR(SPC,0,14) = ?)emp_relieve  "
                    + " INNER JOIN G_SPC ON emp_relieve.SPC = G_SPC.SPC "
                    + " INNER JOIN G_POST ON G_SPC.GPC=G_POST.POST_CODE "
                    + " INNER JOIN (SELECT * FROM section_post_mapping WHERE section_id=?)section_post_mapping ON section_post_mapping.SPC = emp_relieve.SPC "
                    + " LEFT OUTER JOIN EMP_MAST ON emp_relieve.EMP_ID = EMP_MAST.EMP_ID)T3"
                    + " LEFT OUTER JOIN G_BRANCH ON T3.BRANCH_CODE=G_BRANCH.BRANCH_CODE");
            
            pstmt.setString(1, offcode);
            pstmt.setInt(2, sectionid);
            pstmt.setInt(3, pyear);
            pstmt.setInt(4, pmonth);
            pstmt.setString(5, offcode);
            pstmt.setInt(6, sectionid);
            /*
            pstmt = con.prepareStatement("SELECT section_post_mapping.spc,POST,EMP_MAST.EMP_ID,POST_SL_NO,ORDER_NO,ORDER_DATE,PAY_SCALE,"
                    + "CUR_BASIC_SALARY,EMP_MAST.GP,PREV_BASIC_SALARY,PREV_GP,PAY_DATE, "
                    + "F_NAME,M_NAME,L_NAME,DEP_CODE,BANK_CODE, BRANCH_CODE,BANK_ACC_NO,G_SPC.OFF_CODE,DEP_CODE,ACCT_TYPE,GPF_NO,IS_GAZETTED,DOE_GOV, FROM_DATE FROM section_post_mapping "
                    + " INNER JOIN G_SPC ON section_post_mapping.SPC = G_SPC.SPC "
                    + " INNER JOIN G_POST ON G_SPC.GPC=G_POST.POST_CODE "
                    + " LEFT OUTER JOIN EMP_MAST ON section_post_mapping.spc = EMP_MAST.CUR_SPC "
                    + "LEFT OUTER JOIN (SELECT * FROM EMP_PAY_HELDUP WHERE OFF_CODE=? AND TO_DATE IS NULL) AS EMP_PAY_HELDUP ON EMP_MAST.EMP_ID=EMP_PAY_HELDUP.EMP_ID "
                    + "WHERE section_id=?");
            pstmt.setInt(2, sectionid);
            pstmt.setString(1, offcode);
            */
            rs = pstmt.executeQuery();
            while (rs.next()) {

                SectionDtlSPCWiseEmp sdswe = new SectionDtlSPCWiseEmp();
                sdswe.setSpc(rs.getString("spc"));
                sdswe.setOffcode(rs.getString("OFF_CODE"));
                sdswe.setPostname(rs.getString("POST"));
                sdswe.setEmpid(rs.getString("EMP_ID"));
                sdswe.setDoegov(rs.getDate("DOE_GOV"));
                sdswe.setEmpname((StringUtils.defaultString(rs.getString("F_NAME")) + " " + StringUtils.defaultString(rs.getString("M_NAME")) + " " + StringUtils.defaultString(rs.getString("L_NAME"))).replaceAll("\\s+", " "));
                sdswe.setGpfaccno(rs.getString("GPF_NO"));
                sdswe.setAcctype(rs.getString("ACCT_TYPE"));
                sdswe.setPostslno(rs.getInt("POST_SL_NO"));
                sdswe.setOrderno(rs.getString("ORDER_NO"));
                sdswe.setOrderdate(rs.getDate("ORDER_DATE"));
                sdswe.setPayscale(rs.getString("PAY_SCALE"));
                sdswe.setBankcode(rs.getString("BANK_CODE"));
                sdswe.setBankaccno(rs.getString("BANK_ACC_NO"));
                sdswe.setBranchcode(rs.getString("BRANCH_CODE"));
                sdswe.setIfscCode(rs.getString("IFSC_CODE"));
                if (rs.getDate("PAY_DATE") != null) {
                    sdswe.setPayDate(new Date(rs.getDate("PAY_DATE").getTime()));
                }
                sdswe.setCurBasicSalary(rs.getInt("CUR_BASIC_SALARY"));
                sdswe.setGp(rs.getInt("GP"));
                sdswe.setPrevBasicSalary(rs.getInt("PREV_BASIC_SALARY"));
                //sdswe.setGp(rs.getInt("PREV_GP"));
                sdswe.setIsgazetted(rs.getString("IS_GAZETTED"));
                sdswe.setFname(rs.getString("F_NAME"));
                sdswe.setMname(rs.getString("M_NAME"));
                sdswe.setLname(rs.getString("L_NAME"));
                sdswe.setDepcode(rs.getString("DEP_CODE"));
                if (rs.getDate("FROM_DATE") == null) {
                    sdswe.setPayheldup("N");
                } else {
                    sdswe.setPayheldup("Y");
                }
                al.add(sdswe);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return al;
    }

    @Override
    public ArrayList getSPCWiseEmpOnlyInSection(int sectionid) {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        ArrayList al = new ArrayList();
        try {
            con = dataSource.getConnection();
            String billType = "";
            pstmt = con.prepareStatement("SELECT BILL_TYPE FROM G_SECTION WHERE SECTION_ID=?");
            pstmt.setInt(1, sectionid);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                billType = rs.getString("BILL_TYPE");
            }

            if (billType.equals("CONTRACTUAL")) {
                pstmt = con.prepareStatement("SELECT EMP_ID,POST_SL_NO, CUR_BASIC_SALARY,EMP_MAST.GP,PREV_BASIC_SALARY,PREV_GP,PAY_DATE, "
                        + "F_NAME,M_NAME,L_NAME,DEP_CODE,BANK_CODE, BRANCH_CODE,BANK_ACC_NO,DEP_CODE,ACCT_TYPE,GPF_NO, POST_NOMENCLATURE,DOE_GOV, PRE_GP FROM section_post_mapping "
                        + "INNER JOIN EMP_MAST ON section_post_mapping.SPC = EMP_MAST.EMP_ID WHERE section_id=? AND EMP_ID != ''");
                pstmt.setInt(1, sectionid);
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    SectionDtlSPCWiseEmp sdswe = new SectionDtlSPCWiseEmp();
                    sdswe.setEmpid(rs.getString("EMP_ID"));
                    sdswe.setPostname(rs.getString("POST_NOMENCLATURE"));
                    sdswe.setEmpname((StringUtils.defaultString(rs.getString("F_NAME")) + " " + StringUtils.defaultString(rs.getString("M_NAME")) + " " + StringUtils.defaultString(rs.getString("L_NAME"))).replaceAll("\\s+", " "));
                    sdswe.setOrderdate(rs.getDate("DOE_GOV"));
                    sdswe.setGp(rs.getInt("PRE_GP"));
                    al.add(sdswe);
                }
            } else {
                pstmt = con.prepareStatement("SELECT section_post_mapping.spc,POST,EMP_ID,POST_SL_NO,ORDER_NO,ORDER_DATE,PAY_SCALE,"
                        + "CUR_BASIC_SALARY,EMP_MAST.GP,PREV_BASIC_SALARY,PREV_GP,PAY_DATE, "
                        + "F_NAME,M_NAME,L_NAME,DEP_CODE,BANK_CODE, BRANCH_CODE,BANK_ACC_NO,G_SPC.OFF_CODE,DEP_CODE,ACCT_TYPE,GPF_NO,IS_GAZETTED FROM section_post_mapping "
                        + " INNER JOIN G_SPC ON section_post_mapping.SPC = G_SPC.SPC "
                        + " INNER JOIN G_POST ON G_SPC.GPC=G_POST.POST_CODE "
                        + " LEFT OUTER JOIN EMP_MAST ON section_post_mapping.spc = EMP_MAST.CUR_SPC WHERE section_id=? AND EMP_ID != ''");
                pstmt.setInt(1, sectionid);
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    SectionDtlSPCWiseEmp sdswe = new SectionDtlSPCWiseEmp();
                    sdswe.setSpc(rs.getString("SPC"));
                    sdswe.setOffcode(rs.getString("OFF_CODE"));
                    sdswe.setPostname(rs.getString("POST"));
                    sdswe.setEmpid(rs.getString("EMP_ID"));
                    sdswe.setEmpname((StringUtils.defaultString(rs.getString("F_NAME")) + " " + StringUtils.defaultString(rs.getString("M_NAME")) + " " + StringUtils.defaultString(rs.getString("L_NAME"))).replaceAll("\\s+", " "));
                    sdswe.setGpfaccno(rs.getString("GPF_NO"));
                    sdswe.setAcctype(rs.getString("ACCT_TYPE"));
                    sdswe.setPostslno(rs.getInt("POST_SL_NO"));
                    sdswe.setOrderno(rs.getString("ORDER_NO"));
                    sdswe.setOrderdate(rs.getDate("ORDER_DATE"));
                    sdswe.setPayscale(rs.getString("PAY_SCALE"));
                    sdswe.setBankcode(rs.getString("BANK_CODE"));
                    sdswe.setBankaccno(rs.getString("BANK_ACC_NO"));
                    sdswe.setBranchcode(rs.getString("BRANCH_CODE"));
                    if (rs.getDate("PAY_DATE") != null) {
                        sdswe.setPayDate(new Date(rs.getDate("PAY_DATE").getTime()));
                    }
                    sdswe.setCurBasicSalary(rs.getInt("CUR_BASIC_SALARY"));
                    sdswe.setGp(rs.getInt("GP"));
                    sdswe.setPrevBasicSalary(rs.getInt("PREV_BASIC_SALARY"));
                    //sdswe.setGp(rs.getInt("PREV_GP"));
                    sdswe.setIsgazetted(rs.getString("IS_GAZETTED"));
                    sdswe.setFname(rs.getString("F_NAME"));
                    sdswe.setMname(rs.getString("M_NAME"));
                    sdswe.setLname(rs.getString("L_NAME"));
                    sdswe.setDepcode(rs.getString("DEP_CODE"));
                    al.add(sdswe);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return al;
    }

    @Override
    public String getBillType(int sectionid) {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        String billtype = null;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT BILL_TYPE FROM G_SECTION WHERE SECTION_ID = ?");
            pstmt.setInt(1, sectionid);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                billtype = rs.getString("BILL_TYPE");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return billtype;
    }

    @Override
    public ArrayList getTotalAvailableEmp(String offcode) {
        Connection con = null;
        ArrayList availableEmpList = new ArrayList();
        ResultSet rs = null;
        SelectOption so = null;
        PreparedStatement pstmt = null;
        String ordno = null;
        String orddate = null;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT * FROM (select  POST_LEVEL,SPC,SPN, ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME, L_NAME,'(',GPF_NO,')'], ' ')  fullname,"
                    + "ORDER_NO,ORDER_DATE,PAY_SCALE from (select CUR_SPC,EMP_ID,INITIALS,F_NAME,M_NAME,L_NAME,GPF_NO from emp_mast where emp_mast.CUR_OFF_CODE = ? AND IS_REGULAR='Y')tab1 "
                    + "RIGHT OUTER JOIN (SELECT G_SPC.SPC,SPN,POST_LEVEL,ORDER_NO,ORDER_DATE,SECTION_ID,PAY_SCALE from(SELECT SPC,SPN,POST_LEVEL,ORDER_NO,ORDER_DATE,PAY_SCALE FROM G_SPC WHERE OFF_CODE = ? AND "
                    + "(IFUCLEAN!='Y' OR IFUCLEAN IS NULL))G_SPC left outer join SECTION_POST_MAPPING on G_SPC.spc = SECTION_POST_MAPPING.spc where SECTION_ID is null)G_SPC on G_SPC.SPC=tab1.CUR_SPC ORDER BY SPN)tbl2");
            pstmt.setString(1, offcode);
            pstmt.setString(2, offcode);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                so = new SelectOption();
                so.setValue(rs.getString("SPC"));
                ordno = rs.getString("ORDER_NO");
                orddate = CommonFunctions.getFormattedOutputDate1(rs.getDate("ORDER_DATE"));
                if (ordno != null && (orddate != null && !orddate.equals("")) && rs.getString("fullname") != null && !rs.getString("fullname").equals("")) {
                    so.setLabel(rs.getString("SPN") + " (" + ordno + "," + orddate + ", " + StringUtils.defaultString(rs.getString("PAY_SCALE")) + ")" + " (" + rs.getString("fullname") + ")");
                } else if (rs.getString("fullname") != null && !rs.getString("fullname").equals("") && ordno != null && !ordno.equals("")) {
                    so.setLabel(rs.getString("SPN") + " (" + ordno + ", " + StringUtils.defaultString(rs.getString("PAY_SCALE")) + ")" + " (" + rs.getString("fullname") + ")");
                } else if (rs.getString("fullname") != null && !rs.getString("fullname").equals("") && orddate != null && !orddate.equals("")) {
                    so.setLabel(rs.getString("SPN") + " (" + orddate + ", " + StringUtils.defaultString(rs.getString("PAY_SCALE")) + ")" + " (" + rs.getString("fullname") + ")");
                } else if (orddate != null && !orddate.equals("") && ordno != null && !ordno.equals("")) {
                    so.setLabel(rs.getString("SPN") + " (" + ordno + "," + orddate + ", " + StringUtils.defaultString(rs.getString("PAY_SCALE")) + ")");
                } else if (ordno != null && !ordno.equals("")) {
                    so.setLabel(rs.getString("SPN") + " (" + ordno + ", " + StringUtils.defaultString(rs.getString("PAY_SCALE")) + ")");
                } else if (orddate != null && !orddate.equals("")) {
                    so.setLabel(rs.getString("SPN") + " (" + orddate + ", " + StringUtils.defaultString(rs.getString("PAY_SCALE")) + ")");
                } else if (rs.getString("fullname") != null && !rs.getString("fullname").equals("")) {
                    so.setLabel(rs.getString("SPN") + " (" + rs.getString("fullname") + ")");
                } else {
                    if (rs.getString("PAY_SCALE") != null && !rs.getString("PAY_SCALE").equals("")) {
                        so.setLabel(rs.getString("SPN") + "( " + rs.getString("PAY_SCALE") + " )");
                    } else {
                        so.setLabel(rs.getString("SPN"));
                    }
                }
                availableEmpList.add(so);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return availableEmpList;
    }

    @Override
    public ArrayList getTotalAssignEmp(String offcode, int sectionid) {
        Connection con = null;
        ArrayList assignEmpList = new ArrayList();
        ResultSet rs = null;
        SelectOption so = null;
        PreparedStatement pstmt = null;
        String ordno = null;
        String orddate = null;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT SPC,SPN, ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME, L_NAME,'(',GPF_NO,')'], ' ')  fullname,"
                    + "POST_SL_NO,ORDER_NO,ORDER_DATE,PAY_SCALE  FROM ( SELECT SPN,G_SPC.SPC,EMP_ID,INITIALS,F_NAME,M_NAME,L_NAME,POST_SL_NO,ORDER_NO,ORDER_DATE,PAY_SCALE,GPF_NO FROM (SELECT SPC,POST_SL_NO FROM SECTION_POST_MAPPING "
                    + "WHERE SECTION_ID=? ORDER BY POST_SL_NO)SECTION_POST_MAPPING INNER JOIN (SELECT SPN,SPC,ORDER_NO,ORDER_DATE,PAY_SCALE FROM G_SPC WHERE OFF_CODE=? AND (IFUCLEAN!='Y' OR IFUCLEAN IS NULL) )G_SPC ON SECTION_POST_MAPPING.SPC=G_SPC.SPC "
                    + "LEFT OUTER JOIN (SELECT EMP_ID,initials,f_name,m_name,l_name,CUR_SPC,GPF_NO FROM EMP_MAST WHERE CUR_OFF_CODE=? AND (DEP_CODE='02' OR DEP_CODE='05'))EMP_MAST ON SECTION_POST_MAPPING.SPC=EMP_MAST.CUR_SPC) SECTION_MAPPING_LIST ORDER BY POST_SL_NO");
            pstmt.setInt(1, sectionid);
            pstmt.setString(2, offcode);
            pstmt.setString(3, offcode);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                AssignPostList asp = new AssignPostList();
                asp.setSpc(rs.getString("SPC"));
                asp.setSectionId(sectionid);
                ordno = rs.getString("ORDER_NO");
                orddate = CommonFunctions.getFormattedOutputDate1(rs.getDate("ORDER_DATE"));

                if (ordno != null && (orddate != null && !orddate.equals("")) && rs.getString("fullname") != null && !rs.getString("fullname").equals("")) {
                    asp.setSpn(rs.getString("SPN") + " (" + ordno + "," + orddate + ", " + StringUtils.defaultString(rs.getString("PAY_SCALE")) + ")" + " (" + rs.getString("fullname") + ")");
                } else if (rs.getString("fullname") != null && !rs.getString("fullname").equals("") && ordno != null && !ordno.equals("")) {
                    asp.setSpn(rs.getString("SPN") + " (" + ordno + ", " + StringUtils.defaultString(rs.getString("PAY_SCALE")) + ")" + " (" + rs.getString("fullname") + ")");
                } else if (rs.getString("fullname") != null && !rs.getString("fullname").equals("") && orddate != null && !orddate.equals("")) {
                    asp.setSpn(rs.getString("SPN") + " (" + orddate + ", " + StringUtils.defaultString(rs.getString("PAY_SCALE")) + ")" + " (" + rs.getString("fullname") + ")");
                } else if (orddate != null && !orddate.equals("") && ordno != null && !ordno.equals("")) {
                    asp.setSpn(rs.getString("SPN") + " (" + ordno + "," + orddate + ", " + StringUtils.defaultString(rs.getString("PAY_SCALE")) + ")");
                } else if (ordno != null && !ordno.equals("")) {
                    asp.setSpn(rs.getString("SPN") + " (" + ordno + ", " + StringUtils.defaultString(rs.getString("PAY_SCALE")) + ")");
                } else if (orddate != null && !orddate.equals("")) {
                    asp.setSpn(rs.getString("SPN") + " (" + orddate + ", " + StringUtils.defaultString(rs.getString("PAY_SCALE")) + ")");
                } else if (rs.getString("fullname") != null && !rs.getString("fullname").equals("")) {
                    asp.setSpn(rs.getString("SPN") + " (" + rs.getString("fullname") + ")");
                } else {
                    if (rs.getString("PAY_SCALE") != null && !rs.getString("PAY_SCALE").equals("")) {
                        asp.setSpn(rs.getString("SPN") + "( " + rs.getString("PAY_SCALE") + " )");
                    } else {
                        asp.setSpn(rs.getString("SPN"));
                    }
                }

                asp.setSerialNo(rs.getString("POST_SL_NO"));
                assignEmpList.add(asp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return assignEmpList;
    }

    @Override
    public ArrayList getTotalAvailableContractEmp(String offcode) {
        Connection con = null;
        ArrayList availableEmpList = new ArrayList();
        ResultSet rs = null;
        SelectOption so = null;
        PreparedStatement pstmt = null;
        String usertype = null;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT EMP_ID,USERTYPE,F_NAME || ' ' || M_NAME || ' ' || L_NAME FULL_NAME,POST_NOMENCLATURE FROM EMP_MAST WHERE CUR_OFF_CODE=? AND IS_REGULAR='N' AND EMP_ID NOT IN (SELECT SPC FROM SECTION_POST_MAPPING) ORDER BY F_NAME");
            pstmt.setString(1, offcode);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                so = new SelectOption();
                so.setValue(rs.getString("EMP_ID"));
                if (rs.getString("USERTYPE") != null && !rs.getString("USERTYPE").equals("")) {
                    usertype = " (" + rs.getString("USERTYPE").toUpperCase() + ")";
                } else {
                    usertype = "";
                }
                so.setLabel(rs.getString("POST_NOMENCLATURE") + " (" + rs.getString("FULL_NAME") + ")" + usertype);
                availableEmpList.add(so);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return availableEmpList;
    }

    @Override
    public ArrayList getTotalAssignContractEmp(String offcode, int sectionid) {
        Connection con = null;
        ArrayList assignEmpList = new ArrayList();
        ResultSet rs = null;
        AssignPostList asp = null;
        PreparedStatement pstmt = null;
        String usertype = null;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT F_NAME || ' ' || M_NAME || ' ' || L_NAME FULL_NAME,SECTION_ID,USERTYPE,EMP_MAST.EMP_ID,POST_SL_NO,POST_NOMENCLATURE FROM( "
                    + "SELECT SECTION_ID,SPC,POST_SL_NO FROM SECTION_POST_MAPPING WHERE SECTION_ID=?) SECTION_POST_MAPPING  "
                    + "INNER JOIN EMP_MAST ON SECTION_POST_MAPPING.SPC=EMP_MAST.EMP_ID ORDER BY POST_SL_NO");
            pstmt.setInt(1, sectionid);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                asp = new AssignPostList();
                asp.setSpc(rs.getString("EMP_ID"));
                asp.setSectionId(sectionid);
                if (rs.getString("USERTYPE") != null && !rs.getString("USERTYPE").equals("")) {
                    usertype = " (" + rs.getString("USERTYPE").toUpperCase() + ")";
                } else {
                    usertype = "";
                }
                asp.setSpn(rs.getString("POST_NOMENCLATURE") + " (" + rs.getString("FULL_NAME") + ")" + usertype);

                asp.setSerialNo(rs.getString("POST_SL_NO"));
                assignEmpList.add(asp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return assignEmpList;
    }

    @Override
    public String getSectionName(int sectionid) {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        String secName = null;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT SECTION_NAME FROM G_SECTION WHERE SECTION_ID=?");
            pstmt.setInt(1, sectionid);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                secName = rs.getString("SECTION_NAME").toUpperCase();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return secName;
    }

    @Override
    public void mapPost(int sectionid, String spc) {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        String billtype = null;
        int maxcode = 0;
        int postserial = 1;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT BILL_TYPE FROM G_SECTION WHERE SECTION_ID = ?");
            pstmt.setInt(1, sectionid);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                billtype = rs.getString("BILL_TYPE");
            }
            if (billtype == null || billtype.equals("") || billtype.equals("REGULAR")) {
                maxcode = getMaxNumericCode("SECTION_POST_MAPPING", "MAPPING_ID");
                pstmt = con.prepareStatement("SELECT MAX(POST_SL_NO) POST_SL_NO FROM SECTION_POST_MAPPING WHERE SECTION_ID=?");//GET HRMSID
                pstmt.setInt(1, sectionid);
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    postserial = rs.getInt("POST_SL_NO") + 1;
                } else {
                    postserial = 1;
                }

                pstmt = con.prepareStatement("INSERT INTO SECTION_POST_MAPPING (MAPPING_ID,SECTION_ID,SPC,POST_SL_NO) VALUES (?,?,?,?)");
                pstmt.setInt(1, maxcode);
                pstmt.setInt(2, sectionid);
                pstmt.setString(3, spc);
                pstmt.setInt(4, postserial);
                pstmt.execute();
            } else if (billtype != null && billtype.equals("CONTRACTUAL")) {
                maxcode = CommonFunctions.getMaxCode(con, "SECTION_POST_MAPPING", "MAPPING_ID");
                pstmt = con.prepareStatement("SELECT MAX(POST_SL_NO) POST_SL_NO FROM SECTION_POST_MAPPING WHERE SECTION_ID=?");//GET HRMSID
                pstmt.setInt(1, sectionid);
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    postserial = rs.getInt("POST_SL_NO") + 1;
                } else {
                    postserial = 1;
                }

                pstmt = con.prepareStatement("INSERT INTO SECTION_POST_MAPPING (MAPPING_ID,SECTION_ID,SPC,POST_SL_NO) VALUES (?,?,?,?)");
                pstmt.setInt(1, maxcode);
                pstmt.setInt(2, sectionid);
                pstmt.setString(3, spc); //GET HRMSID
                pstmt.setInt(4, postserial);
                pstmt.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    @Override
    public void removePost(String spc) {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("DELETE FROM  SECTION_POST_MAPPING WHERE SPC=?");
            pstmt.setString(1, spc);
            pstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    public int getMaxNumericCode(String tblName, String fieldName) {
        Connection con = null;
        String maxQueryService = "SELECT MAX(" + fieldName + ")+1 as MaxId FROM " + tblName;
        Statement stamt = null;
        ResultSet resultset = null;
        String temp = "";
        int maxId = 1;
        try {
            con = dataSource.getConnection();
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
        } finally {
            DataBaseFunctions.closeSqlObjects(resultset, stamt);
            DataBaseFunctions.closeSqlObjects(con);
        }

        return maxId;
    }

    @Override
    public ArrayList getSPCList(String sectionId) {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        ArrayList al = new ArrayList();
        GroupPayFixation groupPayFix = null;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT EMP.EMP_ID,GPF_NO,SPC,ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME,L_NAME], ' ') EMPNAME,mon_basic,REVISED.GP,existing_pay_scale,revised_basic,G_POST.POST FROM SECTION_POST_MAPPING MAPPING"
                    + " INNER JOIN EMP_MAST EMP ON MAPPING.SPC=EMP.CUR_SPC LEFT OUTER JOIN EMP_PAY_REVISED_2016 REVISED ON EMP.EMP_ID=REVISED.EMP_ID LEFT OUTER JOIN G_POST ON REVISED.POST=G_POST.POST_CODE"
                    + "  WHERE MAPPING.SECTION_ID=?");
            pstmt.setInt(1, Integer.parseInt(sectionId));
            rs = pstmt.executeQuery();
            while (rs.next()) {
                groupPayFix = new GroupPayFixation();
                groupPayFix.setEmpId(rs.getString("EMP_ID"));
                groupPayFix.setEmpName(rs.getString("EMPNAME"));
                groupPayFix.setGpfNo(rs.getString("GPF_NO"));
                groupPayFix.setPost(rs.getString("POST"));
                groupPayFix.setCurPayScale(rs.getString("existing_pay_scale"));
                groupPayFix.setPreviousBasic(rs.getString("mon_basic"));
                groupPayFix.setPreviousGp(rs.getString("GP"));
                groupPayFix.setRevisedBasic(rs.getString("revised_basic"));
                al.add(groupPayFix);
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return al;
    }

    @Override
    public void saveBillSection(String offCode, SectionDefinition SD) {
        int n = 0;
        PreparedStatement pst = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            String maxCode = CommonFunctions.getMaxCode("g_section", "section_id", con);
            pst = con.prepareStatement("INSERT INTO g_section(section_id,off_code,section_name,no_of_emp,bill_type,is_locked) VALUES(?, ?, ?, ?, ?, ?)");
            pst.setInt(1, Integer.parseInt(maxCode));
            pst.setString(2, offCode);
            pst.setString(3, SD.getSection());
            pst.setInt(4, SD.getNofpost());
            pst.setString(5, SD.getBillType());
            System.out.println("No:" + SD.getNofpost());
            pst.setString(6, "N");

            n = pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    public SectionDefinition getBillSection(int billSectionId) {
        SectionDefinition SD = null;
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            con = this.dataSource.getConnection();
            st = con.createStatement();

            rs = st.executeQuery("SELECT * FROM g_section WHERE section_id = " + billSectionId);

            while (rs.next()) {
                SD = new SectionDefinition();
                SD.setBillType(rs.getString("bill_type"));
                SD.setNofpost(rs.getInt("no_of_emp"));
                SD.setSection(rs.getString("section_name"));
                SD.setSectionId(billSectionId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, st);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return SD;
    }

    @Override
    public void updateBillSection(SectionDefinition SD) {
        int n = 0;
        PreparedStatement pst = null;
        Connection con = null;
        int sectionId = SD.getSectionId();
        String sectionName = SD.getSection();
        String billType = SD.getBillType();
        int nofPost = SD.getNofpost();
        try {
            con = dataSource.getConnection();

            pst = con.prepareStatement("UPDATE g_section SET section_name = ?, no_of_emp = ?, bill_type =? WHERE section_id = ?");
            pst.setString(1, sectionName);
            pst.setInt(2, nofPost);
            pst.setString(3, billType);
            pst.setInt(4, sectionId);

            n = pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    @Override
    public SectionDtlSPCWiseEmp getEmpoyeeDataInTransit(SectionDtlSPCWiseEmp sdswe, int year, int month) {
        PreparedStatement pstmt = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("select emp_relieve.spc,POST,EMP_MAST.EMP_ID,POST_SL_NO,ORDER_NO,ORDER_DATE,PAY_SCALE,"
                    + "                    CUR_BASIC_SALARY,EMP_MAST.GP,PREV_BASIC_SALARY,PREV_GP,PAY_DATE, "
                    + "                    F_NAME,M_NAME,L_NAME,DEP_CODE,BANK_CODE, BRANCH_CODE,BANK_ACC_NO,G_SPC.OFF_CODE,DEP_CODE,ACCT_TYPE,GPF_NO,IS_GAZETTED,DOE_GOV "
                    + " from (select EMP_ID,spc from emp_relieve where emp_relieve.spc= 'NPRREV85100002601930001' and date_part('year', rlv_date) = 2018 and date_part('month', rlv_date)=6)emp_relieve "
                    + "INNER JOIN section_post_mapping ON emp_relieve.SPC = section_post_mapping.SPC "
                    + "INNER JOIN G_SPC ON emp_relieve.SPC = G_SPC.SPC "
                    + "INNER JOIN G_POST ON G_SPC.GPC=G_POST.POST_CODE "
                    + "LEFT OUTER JOIN EMP_MAST ON emp_relieve.EMP_ID = EMP_MAST.EMP_ID ");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return sdswe;
    }
}
