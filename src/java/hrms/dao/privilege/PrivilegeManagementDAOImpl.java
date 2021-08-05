/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.privilege;

import hrms.SelectOption;
import hrms.common.DataBaseFunctions;
import hrms.model.master.Module;
import hrms.model.privilege.Privilege;
import hrms.model.privilege.RoleMapping;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Surendra
 */
public class PrivilegeManagementDAOImpl implements PrivilegeManagementDAO {

    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public ArrayList getAssignedPrivilageList(String spc) {
        ArrayList assignedPrivilageList = new ArrayList();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet resultset = null;
        try {
            con = this.dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT PRIV_MAP_ID,ROLE_NAME,MOD_GRP_NAME,MOD_NAME FROM g_privilege_map "
                    + "INNER JOIN G_ROLE_MASTER ON g_privilege_map.ROLE_ID =  G_ROLE_MASTER.ROLE_ID "
                    + "INNER JOIN g_module_group ON g_privilege_map.MOD_GRP_ID = g_module_group.MOD_GRP_ID "
                    + "INNER JOIN g_module_link ON g_privilege_map.mod_id = g_module_link.mod_id "
                    + "WHERE g_privilege_map.SPC=? ");
            pstmt.setString(1, spc);
            resultset = pstmt.executeQuery();
            while (resultset.next()) {
                Privilege privilege = new Privilege();
                privilege.setPrivmapid(resultset.getInt("PRIV_MAP_ID"));
                privilege.setRolename(resultset.getString("ROLE_NAME"));
                privilege.setModulegroup(resultset.getString("MOD_GRP_NAME"));
                privilege.setModulename(resultset.getString("MOD_NAME"));
                assignedPrivilageList.add(privilege);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return assignedPrivilageList;
    }

    @Override
    public ArrayList getRoleList() {
        Statement st = null;
        ArrayList al = new ArrayList();
        SelectOption so = null;
        ResultSet rs = null;
        Connection con = null;
        try {
            con = this.dataSource.getConnection();
            st = con.createStatement();
            rs = st.executeQuery("SELECT role_id,role_name FROM g_role_master");
            while (rs.next()) {
                so = new SelectOption();
                so.setValue(rs.getString("role_id"));
                so.setLabel(rs.getString("role_name"));
                al.add(so);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return al;
    }

    @Override
    public ArrayList getModuleGroupList(String roleId) {
        Statement st = null;
        ArrayList al = new ArrayList();
        ResultSet rs = null;
        String sqlQuery = null;
        RoleMapping rm = null;
        Connection con = null;
        try {
            con = this.dataSource.getConnection();
            st = con.createStatement();
            if (roleId != null && !roleId.equals("")) {
                sqlQuery = "SELECT G_MODULE_GROUP.MOD_GRP_ID,G_MODULE_GROUP.MOD_GRP_NAME ,G_GROUP_MAP.MOD_GRP_ID  MAPID FROM G_MODULE_GROUP "
                        + "INNER JOIN (SELECT * FROM G_GROUP_MAP WHERE G_GROUP_MAP.ROLE_ID = '" + roleId + "')  G_GROUP_MAP "
                        + "ON G_MODULE_GROUP.MOD_GRP_ID = G_GROUP_MAP.MOD_GRP_ID ORDER BY MOD_GRP_NAME";
            } else {
                sqlQuery = "SELECT MOD_GRP_ID,MOD_GRP_NAME FROM G_MODULE_GROUP ORDER BY MOD_GRP_NAME";
            }
            System.out.println("sqlQuery:"+sqlQuery);
            rs = st.executeQuery(sqlQuery);

            while (rs.next()) {
                rm = new RoleMapping();
                rm.setModId(rs.getString("MOD_GRP_ID"));
                rm.setModGrpName(rs.getString("MOD_GRP_NAME"));
                if (roleId != null && !roleId.equals("")) {
                    rm.setModGrpMap(rs.getString("MAPID"));
                } else {
                    rm.setModGrpMap("");
                }
                al.add(rm);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return al;
    }

    @Override
    public String assignPrivilege(Module module, String spc) {
        Connection con = null;
        PreparedStatement pst = null;
        Statement st = null;
        ResultSet rs = null;
        int mCode = 1;
        String status = "Y";
        try {
            boolean alreadyAssigned = false;
            con = this.dataSource.getConnection();
            pst = con.prepareStatement("SELECT * FROM g_privilege_map WHERE spc=? and mod_id=?");
            pst.setString(1, spc);
            pst.setInt(2, module.getModid());
            rs = pst.executeQuery();
            if (rs.next()) {
                alreadyAssigned = true;
                status = "E";
            }
            if (alreadyAssigned == false) {
                st = con.createStatement();
                rs = st.executeQuery("select max(priv_map_id) priv_map_id from g_privilege_map");
                while (rs.next()) {
                    if (rs != null) {
                        mCode = rs.getInt("priv_map_id") + 1;
                    }
                }

                pst = con.prepareStatement("insert into g_privilege_map(priv_map_id,spc,role_id,mod_grp_id,mod_id) values(?,?,?,?,?)");
                pst.setInt(1, mCode);
                pst.setString(2, spc);
                pst.setString(3, module.getRoleid());
                pst.setString(4, module.getModgrpid());
                pst.setInt(5, module.getModid());
                pst.executeUpdate();
            }
        } catch (Exception e) {
            status = "N";
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return status;

    }

    public String revokePrivilege(String spc, int privmapid) {
        Connection con = null;
        PreparedStatement pst = null;
        String status = "Y";
        try {
            con = this.dataSource.getConnection();
            pst = con.prepareStatement("DELETE FROM g_privilege_map WHERE spc=? and priv_map_id=?");
            pst.setString(1, spc);
            pst.setInt(2, privmapid);
            pst.execute();
        } catch (Exception e) {
            status = "N";
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return status;
    }
    @Override
    public List getUserList(String userType) {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        List li = new ArrayList();
        try {
            con = this.dataSource.getConnection();
            pst = con.prepareStatement("SELECT USERNAME FROM USER_DETAILS WHERE USERTYPE=? ORDER BY USERTYPE");
            pst.setString(1, userType);
            rs = pst.executeQuery();
            while (rs.next()) {
                li.add(rs.getString("USERNAME"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return li;
    }

    @Override
    public List getAssignedPrivilageUserNameSpecificList(String userName) {
        List assignedPrivilageList = new ArrayList();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet resultset = null;
        try {
            con = this.dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT g_user_privilege_map.mod_id,mod_name,url,PRIV_MAP_ID FROM g_user_privilege_map "
                    + "INNER JOIN g_module_link ON g_user_privilege_map.mod_id = g_module_link.mod_id WHERE username=? ");
            pstmt.setString(1, userName);
            resultset = pstmt.executeQuery();
            while (resultset.next()) {
                Privilege privilege = new Privilege();
                privilege.setPrivmapid(resultset.getInt("PRIV_MAP_ID"));
                //privilege.setModulegroup(resultset.getString("MOD_GRP_NAME"));
                privilege.setModulename(resultset.getString("MOD_NAME"));
                assignedPrivilageList.add(privilege);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return assignedPrivilageList;
    }

    @Override
    public List getModuleListUserNameSpecific(String userType) {

        PreparedStatement st = null;
        ArrayList al = new ArrayList();
        SelectOption so = null;
        ResultSet rs = null;
        Connection con = null;
        try {
            con = this.dataSource.getConnection();
            st = con.prepareStatement("SELECT MOD_ID,MOD_NAME FROM G_MODULE_LINK WHERE module_type=? ORDER BY MOD_NAME");
            st.setString(1, userType);
            rs=st.executeQuery();
            while (rs.next()) {
                so = new SelectOption();
                so.setValue(rs.getString("MOD_ID"));
                so.setLabel(rs.getString("MOD_NAME"));
                al.add(so);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return al;
    }
    
    @Override
    public String assignPrivilegeUserNameSpecific(Module module, String username){
        Connection con = null;
        PreparedStatement pst = null;
        Statement st = null;
        ResultSet rs = null;
        int mCode = 1;
        String status = "Y";
        try {
            boolean alreadyAssigned = false;
            con = this.dataSource.getConnection();
            pst = con.prepareStatement("SELECT * FROM g_user_privilege_map WHERE username=? and mod_id=?");
            pst.setString(1, username);
            pst.setInt(2, module.getModid());
            rs = pst.executeQuery();
            if (rs.next()) {
                alreadyAssigned = true;
                status = "E";
            }
            if (alreadyAssigned == false) {
                st = con.createStatement();
                rs = st.executeQuery("select max(priv_map_id) priv_map_id from g_user_privilege_map");
                
                while (rs.next()) {
                    if (rs != null) {
                        mCode = rs.getInt("priv_map_id") + 1;
                    }
                }

                pst = con.prepareStatement("insert into g_user_privilege_map(priv_map_id,username,mod_grp_id,mod_id) values(?,?,?,?)");
                pst.setInt(1, mCode);
                pst.setString(2, username);
                pst.setString(3, module.getModgrpid());
                pst.setInt(4, module.getModid());
                pst.executeUpdate();
            }
        } catch (Exception e) {
            status = "N";
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return status;

    }
    @Override 
    public String revokePrivilageUserNameSpecific(String username,int privmapid){
        Connection con = null;
        PreparedStatement pst = null;
        String status = "Y";
        try {
            con = this.dataSource.getConnection();
            pst = con.prepareStatement("DELETE FROM g_user_privilege_map WHERE username=? and priv_map_id=?");
            pst.setString(1, username);
            pst.setInt(2, privmapid);
            pst.execute();
        } catch (Exception e) {
            status = "N";
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return status;
    }
}
