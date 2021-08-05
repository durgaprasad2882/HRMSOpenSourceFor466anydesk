/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.master;

import hrms.common.DataBaseFunctions;
import hrms.model.master.GPost;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Manas Jena
 */
public class PostDAOImpl implements PostDAO {

    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public ArrayList getPostList(String departmentCode) {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        ArrayList postList = new ArrayList();
        GPost gp = null;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("select * from g_post  where department_code=? order by post ");
            pstmt.setString(1, departmentCode);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                gp = new GPost();
                gp.setPostcode(rs.getString("POST_CODE"));
                gp.setPost(rs.getString("POST"));
                postList.add(gp);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return postList;
    }

    @Override
    public ArrayList getCadrewisePostList(String departmentCode, String cadreCode) {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        ArrayList postList = new ArrayList();
        GPost gp = null;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT g_cadre2post.post_code,POST from g_post  inner join g_cadre2post on g_post.POST_CODE = g_cadre2post.post_code where department_code=? and cadre_code=? order by post");
            pstmt.setString(1, departmentCode);
            pstmt.setString(2, cadreCode);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                gp = new GPost();
                gp.setPostcode(rs.getString("POST_CODE"));
                gp.setPost(rs.getString("POST"));
                postList.add(gp);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return postList;
    }

    @Override
    public ArrayList getPostList(String departmentCode, String offcode) {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        ArrayList postList = new ArrayList();
        GPost gp = null;
        try {
            con = dataSource.getConnection();
            ps = con.prepareStatement("select distinct post_code, post  from g_post a \n"
                    + "                    left outer join g_spc b on a.post_code=b.gpc where a.department_code=? and b.off_code=? and is_sanctioned='Y' order by post ");
            ps.setString(1, departmentCode);
            ps.setString(2, offcode);
            rs = ps.executeQuery();
            while (rs.next()) {
                gp = new GPost();
                gp.setPostcode(rs.getString("POST_CODE"));
                gp.setPost(rs.getString("POST"));
                postList.add(gp);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(ps);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return postList;
    }

    @Override
    public String getPostName(String postcode) {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        String postname = "";
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT POST from g_post   where post_code=?");
            pstmt.setString(1, postcode);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                postname = rs.getString("POST");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return postname;
    }

    @Override
    public String savePost(GPost post) {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        String postCode = "";
        try {
            con = dataSource.getConnection();
            if (post.getPostcode() == null || post.getPostcode().equals("")) {
                pstmt = con.prepareStatement("select max(post_code) as postCode from g_post where department_code=?");
                pstmt.setString(1, post.getDeptcode());
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    postCode = rs.getString("postCode");
                    postCode = StringUtils.right("00" + (Integer.parseInt(postCode) + 1), 6);
                }
                pstmt = con.prepareStatement("INSERT INTO G_POST (department_code,post_code,post) VALUES (?,?,?)");
                pstmt.setString(1, post.getDeptcode());
                pstmt.setString(2, postCode);
                pstmt.setString(3, post.getPost());
                pstmt.executeUpdate();
            }else{
                pstmt = con.prepareStatement("UPDATE G_POST SET post=? WHERE post_code = ?");
                pstmt.setString(1, post.getPost());
                pstmt.setString(2, post.getPostcode());
                pstmt.executeUpdate();
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return postCode;
    }

    @Override
    public GPost getPostDetail(String postcode) {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        GPost post = new GPost();
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT * FROM G_POST WHERE POST_CODE=?");
            pstmt.setString(1, postcode);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                post.setPostcode(rs.getString("post_code"));
                post.setPost(rs.getString("post"));
                post.setDeptcode(rs.getString("department_code"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return post;
    }
}
