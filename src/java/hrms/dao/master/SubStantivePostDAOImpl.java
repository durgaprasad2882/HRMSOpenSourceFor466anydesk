/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.master;

import hrms.SelectOption;
import hrms.common.DataBaseFunctions;
import hrms.model.master.SubstantivePost;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 *
 * @author Durga
 */
public class SubStantivePostDAOImpl implements SubStantivePostDAO {

    @Resource(name = "dataSource")
    protected DataSource dataSource;

    @Resource(name = "oradataSource")
    protected DataSource oradataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setOradataSource(DataSource oradataSource) {
        this.oradataSource = oradataSource;
    }

    @Override
    public List getEmployeeWithSPCList(String offcode) {
        Connection con = null;

        PreparedStatement ps = null;
        ResultSet rs = null;

        List li = new ArrayList();

        try {
            con = this.dataSource.getConnection();
            ps = con.prepareStatement("select spc,post,ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME,L_NAME], ' ') EMPNAME,emp_id,spn from ("
                    + "select spc,gpc,spn from g_spc where off_code=? AND (IFUCLEAN!='Y' OR IFUCLEAN IS NULL)) g_spc "
                    + "inner join g_post on g_spc.GPC = g_post.POST_CODE "
                    + "left outer join EMP_MAST ON G_SPC.SPC=EMP_MAST.CUR_SPC ORDER BY POST");

            ps.setString(1, offcode);
            rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getString("EMPNAME") != null && !rs.getString("EMPNAME").equals("")) {
                    SubstantivePost spc = new SubstantivePost();
                    spc.setPostname(rs.getString("post"));
                    spc.setEmpname(rs.getString("EMPNAME"));
                    spc.setSpc(rs.getString("spc"));
                    spc.setSpn(rs.getString("spn"));
                    spc.setEmpid(rs.getString("emp_id"));
                    String spcHrmsId = rs.getString("spc") + "|" + rs.getString("emp_id") + "|" + rs.getString("post") + "|" + rs.getString("EMPNAME");
                    spc.setSpcHrmsId(spcHrmsId);
                    li.add(spc);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return li;
    }

    @Override
    public List getSPCList(String offcode) {
        Connection con = null;

        PreparedStatement ps = null;
        ResultSet rs = null;

        List li = new ArrayList();
        SelectOption so = null;
        try {
            con = this.dataSource.getConnection();
            ps = con.prepareStatement("select spc,post,ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME,L_NAME], ' ') EMPNAME,emp_id from ("
                    + "select spc,gpc from g_spc where off_code='" + offcode + "' AND (IFUCLEAN!='Y' OR IFUCLEAN IS NULL)) g_spc "
                    + "inner join g_post on g_spc.GPC = g_post.POST_CODE "
                    + "left outer join EMP_MAST ON G_SPC.SPC=EMP_MAST.CUR_SPC ORDER BY POST");
            rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getString("EMPNAME") != null && !rs.getString("EMPNAME").equals("")) {
                    so = new SelectOption();
                    so.setLabel(rs.getString("post"));
                    so.setDesc(rs.getString("EMPNAME"));
                    String SpcHrmsId = rs.getString("spc") + "|" + rs.getString("emp_id");
                    so.setValue(SpcHrmsId);
                    li.add(so);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return li;
    }

    @Override
    public List getGenericPostList(String offcode) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        PreparedStatement pst1 = null;
        ResultSet rs1 = null;

        List li = new ArrayList();

        SelectOption so = null;
        try {
            con = this.dataSource.getConnection();

            pst1 = con.prepareStatement("SELECT POST_CODE,POST FROM G_POST WHERE POST_CODE=?");
            pst = con.prepareStatement("SELECT DISTINCT GPC FROM G_SPC WHERE OFF_CODE=? AND SPN is not null AND (IFUCLEAN!='Y' OR IFUCLEAN IS NULL)");
            pst.setString(1, offcode);
            rs = pst.executeQuery();
            while (rs.next()) {

                pst1.setString(1, rs.getString("GPC"));
                rs1 = pst1.executeQuery();
                while (rs1.next()) {
                    so = new SelectOption();
                    so.setValue(rs1.getString("POST_CODE"));
                    so.setLabel(rs1.getString("POST"));
                    li.add(so);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return li;
    }

    @Override
    public List getGPCWiseSPCList(String empid, String offcode, String gpc) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        PreparedStatement pst1 = null;
        ResultSet rs1 = null;

        List li = new ArrayList();

        SelectOption so = null;

        String empspc = "";
        String empname = "";
        try {
            con = this.dataSource.getConnection();

            pst = con.prepareStatement("SELECT ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME,L_NAME], ' ') FULL_NAME,CUR_SPC FROM EMP_MAST WHERE EMP_ID=?");
            pst.setString(1, empid);
            rs = pst.executeQuery();
            if (rs.next()) {
                empspc = rs.getString("CUR_SPC");
                empname = rs.getString("FULL_NAME");
            }

            pst1 = con.prepareStatement("SELECT CUR_SPC FROM EMP_MAST WHERE CUR_SPC=?");
            pst = con.prepareStatement("SELECT SPC,SPN FROM G_SPC WHERE OFF_CODE=? AND GPC=? and spn is not null AND (IFUCLEAN!='Y' OR IFUCLEAN IS NULL) ORDER BY SPN");
            pst.setString(1, offcode);
            pst.setString(2, gpc);
            rs = pst.executeQuery();
            while (rs.next()) {
                so = new SelectOption();
                pst1.setString(1, rs.getString("SPC"));
                rs1 = pst1.executeQuery();
                if (rs1.next()) {
                    if (rs1.getString("CUR_SPC") == null || rs1.getString("CUR_SPC").equals("")) {
                        so.setValue(rs.getString("SPC"));
                        so.setLabel(rs.getString("SPN"));
                    }
                }
                if (empspc != null && empspc.equals(rs.getString("SPC"))) {
                    so.setValue(rs.getString("SPC"));
                    so.setLabel(rs.getString("SPN") + " " + empname);
                }
            }
            li.add(so);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return li;
    }

    @Override
    public ArrayList getCadreWiseOfficeWiseSPC(String cadreCode, String offCode) {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        ArrayList spcList = new ArrayList();
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("select SPC,SPN from g_cadre2post INNER JOIN G_SPC ON g_cadre2post.POST_CODE = G_SPC.gpc where g_cadre2post.cadre_code=? "
                    + "AND G_SPC.OFF_CODE=? AND (IFUCLEAN!='Y' OR IFUCLEAN IS NULL)");
            pstmt.setString(1, cadreCode);
            pstmt.setString(2, offCode);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                SubstantivePost spc = new SubstantivePost();
                spc.setSpn(rs.getString("SPN"));
                spc.setSpc(rs.getString("SPC"));
                spcList.add(spc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return spcList;
    }

    @Override
    public List getPostListPaging(String offcode, String postToSearch, int page, int rows) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        List postlist = new ArrayList();
        SubstantivePost sp = null;

        int minlimit = rows * (page - 1);
        int maxlimit = rows;
        try {
            con = dataSource.getConnection();

            String sql = "";
            if (postToSearch != null && !postToSearch.equals("")) {
                sql = "SELECT SPC,SPN FROM G_SPC WHERE OFF_CODE=? AND SPN LIKE ? ORDER BY SPN LIMIT ? OFFSET ?";
                pst = con.prepareStatement(sql);
                pst.setString(1, offcode);
                pst.setString(2, "%" + postToSearch.toUpperCase() + "%");
                pst.setInt(3, maxlimit);
                pst.setInt(4, minlimit);
            } else {
                //sql = "SELECT SPC,SPN FROM G_SPC WHERE OFF_CODE=? ORDER BY SPN LIMIT ? OFFSET ?";
                sql = "SELECT POST,POST_CODE FROM G_POST"
                        + " INNER JOIN G_OFFICE ON G_POST.DEPARTMENT_CODE=G_OFFICE.DEPARTMENT_CODE WHERE OFF_CODE=?"
                        + " ORDER BY POST ASC LIMIT ? OFFSET ?";
                pst = con.prepareStatement(sql);
                pst.setString(1, offcode);
                pst.setInt(2, maxlimit);
                pst.setInt(3, minlimit);
            }

            rs = pst.executeQuery();
            while (rs.next()) {
                sp = new SubstantivePost();
                sp.setSpc(rs.getString("POST_CODE"));
                sp.setSpn(rs.getString("POST"));
                postlist.add(sp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return postlist;
    }

    @Override
    public int getPostListCountPaging(String offcode, String postToSearch) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        int cnt = 0;

        try {
            con = this.dataSource.getConnection();

            String sql = "";

            if (postToSearch != null && !postToSearch.equals("")) {
                sql = "SELECT COUNT(*) CNT FROM G_SPC WHERE OFF_CODE=? AND SPN LIKE ?";
                pst = con.prepareStatement(sql);
                pst.setString(1, offcode);
                pst.setString(2, "%" + postToSearch.toUpperCase() + "%");
            } else {
                //sql = "SELECT COUNT(*) CNT FROM G_SPC WHERE OFF_CODE=?";
                sql = "SELECT count(*) cnt FROM G_POST"
                        + " INNER JOIN G_OFFICE ON G_POST.DEPARTMENT_CODE=G_OFFICE.DEPARTMENT_CODE WHERE OFF_CODE=?";
                pst = con.prepareStatement(sql);
                pst.setString(1, offcode);
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
    public List getGPCWiseEmployeeList(String postcode, String offcode) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        List emplist = new ArrayList();

        SelectOption so = null;
        try {
            con = this.dataSource.getConnection();

            String sql = "select EMP_ID,ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME,L_NAME], ' ')"
                    + "EMPNAME,SPC from (select spc,off_code,gpc from g_spc where off_code=? and gpc=? AND (IFUCLEAN!='Y' OR IFUCLEAN IS NULL))g_spc inner join emp_mast on g_spc.spc=emp_mast.cur_spc order by f_name";
            pst = con.prepareStatement(sql);
            pst.setString(1, offcode);
            pst.setString(2, postcode);
            rs = pst.executeQuery();
            while (rs.next()) {
                so = new SelectOption();
                so.setValue(rs.getString("EMP_ID") + "-" + rs.getString("SPC"));
                so.setLabel(rs.getString("EMPNAME") + "(" + rs.getString("EMP_ID") + ")");
                emplist.add(so);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return emplist;
    }

    @Override
    public List getGPCWiseEmployeeListOnlySPC(String postcode) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        List emplist = new ArrayList();

        SelectOption so = null;
        try {
            con = this.dataSource.getConnection();

            String sql = "select EMP_ID,ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME,L_NAME], ' ')"
                    + "EMPNAME,SPC from (select spc,off_code,gpc from g_spc where gpc=? AND (IFUCLEAN!='Y' OR IFUCLEAN IS NULL))g_spc inner join emp_mast on g_spc.spc=emp_mast.cur_spc order by f_name";
            pst = con.prepareStatement(sql);
            pst.setString(1, postcode);
            rs = pst.executeQuery();
            while (rs.next()) {
                so = new SelectOption();
                so.setValue(rs.getString("SPC"));
                so.setLabel(rs.getString("EMPNAME") + "(" + rs.getString("EMP_ID") + ")");
                emplist.add(so);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return emplist;
    }

    @Override
    public List getOfficeWithSPCList(String offcode) {
        Connection con = null;

        PreparedStatement ps = null;
        ResultSet rs = null;

        List li = new ArrayList();

        try {
            con = this.dataSource.getConnection();
            ps = con.prepareStatement("select spc,post,ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME,L_NAME], ' ') EMPNAME,emp_id,spn from ("
                    + "select spc,gpc,spn from g_spc where off_code=? AND (IFUCLEAN!='Y' OR IFUCLEAN IS NULL)) g_spc "
                    + "inner join g_post on g_spc.GPC = g_post.POST_CODE "
                    + "left outer join EMP_MAST ON G_SPC.SPC=EMP_MAST.CUR_SPC ORDER BY POST");

            ps.setString(1, offcode);
            rs = ps.executeQuery();
            while (rs.next()) {

                SubstantivePost spc = new SubstantivePost();
                spc.setPostname(rs.getString("post"));
                spc.setEmpname(rs.getString("EMPNAME"));
                spc.setSpc(rs.getString("spc"));
                spc.setSpn(rs.getString("spn"));
                spc.setEmpid(rs.getString("emp_id"));
                String spcHrmsId = rs.getString("spc") + "|" + rs.getString("emp_id") + "|" + rs.getString("post") + "|" + rs.getString("EMPNAME");
                spc.setSpcHrmsId(spcHrmsId);
                li.add(spc);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return li;
    }

    @Override
    public List getCadreWisePostList(String offcode, String cadrecode) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        List spclist = new ArrayList();

        SubstantivePost sp = null;
        String post = "";
        try {
            con = this.dataSource.getConnection();

            if (cadrecode != null && (cadrecode.equals("1101") || cadrecode.equals("1103") || cadrecode.equals("2668"))) {
                String sql = "SELECT TAB1.SPC SPC,TAB1.POST POST,ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME,L_NAME], ' ') EMPNAME FROM"
                        + " (SELECT TAB.POST,SPC FROM"
                        + " (SELECT G_POST.POST_CODE,G_POST.POST FROM (SELECT POST_CODE FROM G_CADRE2POST WHERE CADRE_CODE=?)CADRE2POST"
                        + " INNER JOIN G_POST ON G_POST.POST_CODE=CADRE2POST.POST_CODE)TAB INNER JOIN (SELECT SPC,GPC FROM G_SPC WHERE OFF_CODE=?) G_SPC ON G_SPC.GPC= TAB.POST_CODE)TAB1 LEFT OUTER JOIN"
                        + " EMP_MAST ON EMP_MAST.CUR_SPC=TAB1.SPC";
                pst = con.prepareStatement(sql);
                pst.setString(1, cadrecode);
                pst.setString(2, offcode);
            } else {
                String sql = "SELECT POST,SPC,ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME,L_NAME], ' ') EMPNAME FROM("
                        + " SELECT SPC,POST FROM ("
                        + " SELECT DISTINCT GPC,SPC FROM G_SPC WHERE OFF_CODE=?) G_SPC"
                        + " INNER JOIN G_POST ON G_SPC.GPC=G_POST.POST_CODE) G_POST"
                        + " LEFT OUTER JOIN EMP_MAST ON G_POST.SPC=EMP_MAST.CUR_SPC ORDER BY POST";
                pst = con.prepareStatement(sql);
                pst.setString(1, offcode);
            }
            rs = pst.executeQuery();
            while (rs.next()) {
                post = rs.getString("POST");
                if (rs.getString("EMPNAME") != null && !rs.getString("EMPNAME").equals("")) {
                    post = post + ", " + rs.getString("EMPNAME");
                }
                sp = new SubstantivePost();
                sp.setPostname(post);
                sp.setSpc(rs.getString("SPC"));
                spclist.add(sp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return spclist;
    }

    @Override
    public List getSanctioningSPCOfficeWiseList(String offcode) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        List spclist = new ArrayList();

        String post = "";

        SubstantivePost sp = null;
        try {
            con = this.dataSource.getConnection();

            String sql = "select spc,post,GPF_NO,ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME,L_NAME], ' ') EMPNAME from "
                    + "(select spc,gpc from g_spc where off_code=? AND IS_SANCTIONING_SPC = 'Y' AND (IFUCLEAN!='Y' OR IFUCLEAN IS NULL)) g_spc "
                    + "inner join g_post on g_spc.GPC = g_post.POST_CODE left outer join EMP_MAST ON G_SPC.SPC=EMP_MAST.CUR_SPC ORDER BY POST";
            pst = con.prepareStatement(sql);
            pst.setString(1, offcode);
            rs = pst.executeQuery();
            while (rs.next()) {
                post = rs.getString("post");
                if (rs.getString("EMPNAME") != null && !rs.getString("EMPNAME").equals("")) {
                    post = post + ", " + rs.getString("EMPNAME") + "(" + rs.getString("GPF_NO") + ")";
                }
                sp = new SubstantivePost();
                sp.setPostname(post);
                sp.setSpc(rs.getString("SPC"));
                spclist.add(sp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return spclist;
    }

    @Override
    public SubstantivePost getSpcDetail(String spc) {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement st = null;
        SubstantivePost substantivePost = new SubstantivePost();
        try {
            con = dataSource.getConnection();
            st = con.prepareStatement("SELECT spc,gpc, spn,post FROM g_spc inner join g_post on g_spc.gpc = g_post.post_code WHERE spc = ? ");
            st.setString(1, spc);
            rs = st.executeQuery();
            if (rs.next()) {
                substantivePost.setSpc(rs.getString("spc"));
                substantivePost.setGpc(rs.getString("gpc"));
                substantivePost.setSpn(rs.getString("spn"));
                substantivePost.setPostname(rs.getString("post"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, st);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return substantivePost;
    }

    @Override
    public List getSPCListWithEmployeeName(String offCode, String postCode) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        SubstantivePost spost = null;

        List spnlist = new ArrayList();
        try {
            con = this.dataSource.getConnection();
            //System.out.println("Off Code is: "+offCode);
            //System.out.println("Post Code is: "+postCode);
            String sql = "select emp_id,ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME,L_NAME], ' ') EMP_NAME,F_NAME,spc,SPN,g_spc.pay_scale,pay_scale_7th_pay,g_spc.post_grp,pay_gp_level,g_spc.gp,g_spc.cadre_code,G_DEPARTMENT.DEPARTMENT_CODE from g_spc"
                    + " left outer join emp_mast on g_spc.spc=emp_mast.cur_spc LEFT OUTER JOIN G_CADRE ON G_SPC.CADRE_CODE=G_CADRE.CADRE_CODE"
                    + " LEFT OUTER JOIN G_DEPARTMENT ON G_CADRE.DEPARTMENT_CODE=G_DEPARTMENT.DEPARTMENT_CODE where gpc=? and off_code=? AND is_sanctioned='Y' ORDER BY SPN,F_NAME ASC";
            pst = con.prepareStatement(sql);
            pst.setString(1, postCode);
            pst.setString(2, offCode);
            rs = pst.executeQuery();
            while (rs.next()) {
                spost = new SubstantivePost();
                spost.setEmpname(rs.getString("EMP_NAME"));
                spost.setSpc(rs.getString("SPC"));
                spost.setSpn(rs.getString("SPN"));
                spost.setPayscale(rs.getString("pay_scale"));
                spost.setPayscale_7th(rs.getString("pay_scale_7th_pay"));
                spost.setPostgrp(rs.getString("post_grp"));
                spost.setPaylevel(rs.getString("pay_gp_level"));
                spost.setGp(rs.getString("gp"));
                spost.setCadre(rs.getString("cadre_code"));
                spost.setDept(rs.getString("DEPARTMENT_CODE"));
                spnlist.add(spost);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return spnlist;
    }

    @Override
    public List getPostCodeOfficeWise(String offCode) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        List postcodelist = new ArrayList();

        SubstantivePost spost = null;

        try {
            con = this.dataSource.getConnection();

            String sql = "SELECT POST_CODE,POST FROM G_POST"
                    + " INNER JOIN G_SPC ON G_POST.POST_CODE=G_SPC.GPC WHERE OFF_CODE=? AND SPN is not null AND (IFUCLEAN!='Y' OR IFUCLEAN IS NULL) GROUP BY POST_CODE,POST ORDER BY POST ASC";
            pst = con.prepareStatement(sql);
            pst.setString(1, offCode);
            rs = pst.executeQuery();
            while (rs.next()) {
                spost = new SubstantivePost();
                spost.setPostCode(rs.getString("POST_CODE"));
                spost.setPostname(rs.getString("POST"));
                postcodelist.add(spost);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return postcodelist;
    }

    @Override
    public int updateSubstantivePost(String offCode, String gpc, String spc, String payscale_6th, String payscale_7th, String postgrp, String paylevel, String gp, String cadreCode, String chkGrantInAid, String teachingPost, String planOrNonPlan) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        int retVal = 0;
        try {
            con = this.dataSource.getConnection();
            System.out.println("GPC is: " + gpc);
            System.out.println("SPC is: " + spc);
            if (spc != null && !spc.equals("")) {
                String sql = "UPDATE G_SPC SET pay_scale=?,pay_scale_7th_pay=?,post_grp=?,PAY_GP_LEVEL=?,gp=?,cadre_code=?,is_teaching_post=?,is_plan=? WHERE OFF_CODE=? AND GPC=? AND SPC=?";
                pst = con.prepareStatement(sql);
                pst.setString(1, payscale_6th);
                pst.setString(2, payscale_7th);
                if (chkGrantInAid != null && !chkGrantInAid.equals("")) {
                    pst.setString(3, "G");
                } else {
                    pst.setString(3, postgrp);
                }
                pst.setString(4, paylevel);
                if (gp != null && !gp.equals("")) {
                    pst.setInt(5, Integer.parseInt(gp));
                } else {
                    pst.setInt(5, 0);
                }
                pst.setString(6, cadreCode);
                if (teachingPost != null && !teachingPost.equals("")) {
                    pst.setString(7, teachingPost);
                } else {
                    pst.setString(7, "NT");
                }
                if (planOrNonPlan != null && !planOrNonPlan.equals("")) {
                    pst.setString(8, planOrNonPlan);
                } else {
                    pst.setString(8, "NP");
                }
                pst.setString(9, offCode);
                pst.setString(10, gpc);
                pst.setString(11, spc);

                retVal = pst.executeUpdate();
            } else {
                String sql = "UPDATE G_SPC SET pay_scale=?,pay_scale_7th_pay=?,post_grp=?,PAY_GP_LEVEL=?,gp=?,cadre_code=?,is_teaching_post=?,is_plan=? WHERE OFF_CODE=? AND GPC=?";
                pst = con.prepareStatement(sql);
                pst.setString(1, payscale_6th);
                pst.setString(2, payscale_7th);
                pst.setString(3, postgrp);
                pst.setString(4, paylevel);
                if (gp != null && !gp.equals("")) {
                    pst.setInt(5, Integer.parseInt(gp));
                } else {
                    pst.setInt(5, 0);
                }
                pst.setString(6, cadreCode);
                if (teachingPost != null && !teachingPost.equals("")) {
                    pst.setString(7, teachingPost);
                } else {
                    pst.setString(7, "NT");
                }
                if (planOrNonPlan != null && !planOrNonPlan.equals("")) {
                    pst.setString(8, planOrNonPlan);
                } else {
                    pst.setString(8, "NP");
                }
                pst.setString(9, offCode);
                pst.setString(10, gpc);
                retVal = pst.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return retVal;
    }

    @Override
    public int removeSubstantivePost(String offCode, String gpc, String spc) {

        Connection con = null;
        Connection conora = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        int retVal = 0;

        try {
            con = this.dataSource.getConnection();
            //conora = this.oradataSource.getConnection();

            String sql = "UPDATE G_SPC SET is_sanctioned='N',ifuclean='Y' where off_code=? and gpc=? and spc=?";
            pst = con.prepareStatement(sql);
            pst.setString(1, offCode);
            pst.setString(2, gpc);
            pst.setString(3, spc);
            retVal = pst.executeUpdate();

            /*DataBaseFunctions.closeSqlObjects(rs, pst);
            
             sql = "UPDATE G_SPC SET is_sanctioned='N',ifuclean='Y' where off_code=? and gpc=? and spc=?";
             pst = conora.prepareStatement(sql);
             pst.setString(1,offCode);
             pst.setString(2,gpc);
             pst.setString(3,spc);
             retVal = pst.executeUpdate();*/
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
            //DataBaseFunctions.closeSqlObjects(conora);
        }
        return retVal;
    }

    @Override
    public int addSubstantivePost(String deptCode, String offCode, SubstantivePost substantivePost) {

        Connection con = null;
        //Connection conora = null;

        PreparedStatement pst1 = null;
        PreparedStatement pst2 = null;
        ResultSet rs = null;

        PreparedStatement updatepst = null;
        PreparedStatement insertpst = null;

        int postAvailable = 0;
        int retVal = 0;

        String gpc = substantivePost.getPostCode();
        int noOfPost = Integer.parseInt(substantivePost.getTxtNoOfPost());
        try {
            con = this.dataSource.getConnection();

            pst2 = con.prepareStatement("SELECT SPC FROM G_SPC WHERE OFF_CODE=? AND GPC=? AND is_sanctioned='N'");

            String updateSql = "UPDATE G_SPC SET is_sanctioned='Y',IFUCLEAN='N' WHERE SPC=?";
            updatepst = con.prepareStatement(updateSql);

            String insertSql = "insert into G_SPC (Dept_code, Off_Code, SPC, GPC, if_abolished, sub_id, territory, post_level, post_sl,is_gazetted,ORDER_NO,IFUCLEAN,PAY_SCALE,is_sanctioned,SPN,if_inoid) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            insertpst = con.prepareStatement(insertSql);

            String sql = "SELECT COUNT(*) CNT FROM G_SPC WHERE OFF_CODE=? AND GPC=? AND is_sanctioned='N'";
            pst1 = con.prepareStatement(sql);
            pst1.setString(1, offCode);
            pst1.setString(2, gpc);
            rs = pst1.executeQuery();
            if (rs.next()) {
                postAvailable = rs.getInt("CNT");
            }

            if (postAvailable > noOfPost) {
                postAvailable = noOfPost;
            }
            for (int i = 0; i < postAvailable; i++) {
                //System.out.println("Post Availabe is: "+postAvailable);
                pst2.setString(1, offCode);
                pst2.setString(2, gpc);
                rs = pst2.executeQuery();
                if (rs.next()) {
                    updatepst.setString(1, rs.getString("SPC"));
                    retVal = updatepst.executeUpdate();
                }
            }
            for (int i = 1; i <= noOfPost - postAvailable; i++) {
                generateSPN(offCode, gpc, substantivePost);
                insertpst.setString(1, deptCode);
                insertpst.setString(2, offCode);
                insertpst.setString(3, generateSpc(offCode, gpc));
                insertpst.setString(4, gpc);
                insertpst.setString(5, "");
                insertpst.setString(6, substantivePost.getSubid());
                insertpst.setString(7, substantivePost.getTerritory());
                insertpst.setInt(8, 0);
                insertpst.setInt(9, 0);
                insertpst.setString(10, "");
                insertpst.setString(11, "");
                insertpst.setString(12, "N");
                insertpst.setString(13, "");
                insertpst.setString(14, "Y");
                insertpst.setString(15, substantivePost.getSpn());
                insertpst.setString(16, "P");
                insertpst.executeUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst1);
            DataBaseFunctions.closeSqlObjects(pst2);
            DataBaseFunctions.closeSqlObjects(updatepst);
            DataBaseFunctions.closeSqlObjects(insertpst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return retVal;
    }

    private String generateSpc(String offCode, String gpc) {

        Connection conora = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        String mcode = "";
        try {
            conora = this.dataSource.getConnection();

            String sql = "select max(spc) mcode from G_SPC where off_code=? and gpc=?";
            pst = conora.prepareStatement(sql);
            pst.setString(1, offCode);
            pst.setString(2, gpc);
            rs = pst.executeQuery();
            if (rs.next()) {
                mcode = rs.getString("mcode");
            }
            if (mcode != null && !mcode.equals("")) {
                mcode = getNextString(mcode);
            } else {
                mcode = offCode + gpc + "0001";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(conora);
        }
        return mcode;
    }

    private SubstantivePost generateSPN(String offCode, String gpc, SubstantivePost substantivePost) {

        Connection conora = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        String spn = "";
        try {
            conora = this.dataSource.getConnection();

            String sql = "SELECT CATEGORY,SUFFIX FROM G_OFFICE WHERE OFF_CODE=?";
            pst = conora.prepareStatement(sql);
            pst.setString(1, offCode);
            rs = pst.executeQuery();
            if (rs.next()) {
                substantivePost.setSubid(rs.getString("CATEGORY"));
                substantivePost.setTerritory(rs.getString("SUFFIX"));
            }

            DataBaseFunctions.closeSqlObjects(rs, pst);

            sql = "SELECT POST FROM G_POST WHERE POST_CODE=?";
            pst = conora.prepareStatement(sql);
            pst.setString(1, gpc);
            rs = pst.executeQuery();
            if (rs.next()) {
                spn = rs.getString("POST") + "," + substantivePost.getSubid() + "," + substantivePost.getTerritory();
                substantivePost.setSpn(spn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(conora);
        }
        return substantivePost;
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

    public static boolean isNumericChar(String inStr) {
        boolean result = false;
        if (inStr.equals("0") || inStr.equals("1") || inStr.equals("2") || inStr.equals("3") || inStr.equals("4") || inStr.equals("5") || inStr.equals("6") || inStr.equals("7") || inStr.equals("8") || inStr.equals("9")) {
            result = true;
        }
        return result;
    }

    @Override
    public List listSubPost(SubstantivePost substantivePost) {
        List list = null;
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        SubstantivePost pformdata = null;
        list = new ArrayList();
        String dcode = substantivePost.getDeptCode();
        String ocode = substantivePost.getOffCode();
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("SELECT PS.POST,PS.post_code, (SELECT COUNT(*) as cnt FROM g_spc WHERE gpc = PS.post_code AND off_code ='" + ocode + "' AND dept_code = '" + dcode + "') FROM (SELECT post, post_code FROM g_post GP WHERE GP.department_code='" + dcode + "' ORDER BY post) AS PS");
            rs = pst.executeQuery();
            while (rs.next()) {
                pformdata = new SubstantivePost();
                pformdata.setPostname(rs.getString("post"));
                pformdata.setTotalPost(rs.getString("cnt"));
                pformdata.setDeptCode(dcode);
                pformdata.setOffCode(ocode);
                pformdata.setPostCode(rs.getString("post_code"));

                list.add(pformdata);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return list;
    }

    @Override
    public int savePostdataDetails(SubstantivePost substantivePost) {
        int n = 0;
        PreparedStatement insertpst = null;
        Connection con = null;
        ResultSet rs = null;
        String offCode = substantivePost.getOffCode();
        String deptcode = substantivePost.getDeptCode();
        String gpc = substantivePost.getPostCode();
        int noofPost = Integer.parseInt(substantivePost.getTxtNoOfPost());
        String orderdate = substantivePost.getOrderDate();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
        
        try {
            con = dataSource.getConnection();
            Date outputDate = dateFormat.parse(orderdate);
            insertpst = con.prepareStatement("SELECT ((SELECT (max(cast(REPLACE(spc, (OFF_CODE || GPC), '') as integer))) AS new_code FROM g_spc WHERE off_code='" + offCode + "' AND gpc='" + gpc + "' group by cast(REPLACE(spc, (OFF_CODE || GPC), '') as integer) ORDER BY cast(REPLACE(spc, (OFF_CODE || GPC), '') as integer) DESC LIMIT 1)) AS new_code FROM g_spc  WHERE off_code='" + offCode + "' AND gpc='" + gpc + "' LIMIT 1");
            int cnt = 0;
            rs = insertpst.executeQuery();
            while (rs.next()) {
                cnt = rs.getInt("new_code");
            }
            insertpst = con.prepareStatement("INSERT INTO G_SPC (DEPT_CODE, OFF_CODE, SPC, GPC, IF_ABOLISHED, "
                    + "SUB_ID, TERRITORY, POST_LEVEL, POST_SL,IS_GAZETTED,ORDER_NO,ORDER_DATE, IFUCLEAN,PAY_SCALE,IS_SANCTIONED,SPN,IF_INOID) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            for (int x = 1; x <= noofPost; x++) {
                int newcnt = cnt + x;
                String newcntpad = "0000" + newcnt;
                newcntpad = newcntpad.substring(newcntpad.length() - 4);
                String newCode = offCode + gpc + newcntpad;                                
                generateSPN(offCode, gpc, substantivePost);
                insertpst.setString(1, deptcode);
                insertpst.setString(2, offCode);
                insertpst.setString(3, newCode);
                insertpst.setString(4, gpc);
                insertpst.setString(5, "");
                insertpst.setString(6, substantivePost.getSubid());
                insertpst.setString(7, substantivePost.getTerritory());
                insertpst.setInt(8, 0);
                insertpst.setInt(9, 0);
                insertpst.setString(10, "");
                insertpst.setString(11, substantivePost.getOrderNo());
                insertpst.setTimestamp(12, new Timestamp(outputDate.getTime()));
                insertpst.setString(13, "N");
                insertpst.setString(14, "");
                insertpst.setString(15, "Y");
                insertpst.setString(16, substantivePost.getSpn());
                insertpst.setString(17, "P");
                insertpst.executeUpdate();
            }
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(insertpst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return n;
    }
}
