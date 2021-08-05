/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.tab;

import hrms.common.DataBaseFunctions;
import hrms.model.tab.ModuleGroup;
import hrms.model.tab.ModuleProperty;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 *
 * @author Surendra
 */
public class ServicePanelDAOImpl implements ServicePanelDAO {

    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public ArrayList getRollWiseGrpInfo(String rolid, String spc, String ifEmpClicked, boolean isddo) throws Exception {
        Connection con = null;
        ArrayList roleListarr = new ArrayList();
        ArrayList moduleList = new ArrayList();
        ModuleGroup mg = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            con = this.dataSource.getConnection();
            st = con.createStatement();
            String sqlStr = "";
            if (rolid != null && (rolid.equals("06") || rolid.equals("09"))) {
                sqlStr = "SELECT ROLE_ID,GRPMOD.MOD_GRP_ID,ROLLMODGRP.MOD_GRP_NAME MODGRPNAME,GRPMOD.MOD_ID,GRPMOD.MOD_NAME,URL,HELP_URL,EMP_SPECIFIC,SERIAL,CONVERT_URL,MOD_SERIAL,NEWWINDOW  FROM (SELECT G_GROUP_MAP.ROLE_ID,G_MODULE_GROUP.MOD_GRP_ID,G_MODULE_GROUP.MOD_GRP_NAME,SERIAL FROM G_GROUP_MAP INNER JOIN G_MODULE_GROUP ON G_GROUP_MAP.MOD_GRP_ID = G_MODULE_GROUP.MOD_GRP_ID WHERE ROLE_ID='" + rolid + "')  ROLLMODGRP  INNER JOIN  (SELECT distinct G_MODULE_MAP.MOD_GRP_ID,G_MODULE_LINK.MOD_ID,MOD_NAME,URL,HELP_URL,EMP_SPECIFIC,CONVERT_URL,MOD_SERIAL,NEWWINDOW FROM G_MODULE_MAP INNER JOIN (SELECT G_MODULE_LINK.* FROM G_MODULE_LINK INNER JOIN G_PRIVILEGE_MAP ON G_MODULE_LINK.MOD_ID=G_PRIVILEGE_MAP.MOD_ID WHERE G_PRIVILEGE_MAP.SPC='" + spc + "' AND ROLE_ID='" + rolid + "' AND EMP_SPECIFIC='N' AND HIDE_URL='N')  G_MODULE_LINK ON G_MODULE_MAP.MOD_ID=G_MODULE_LINK.MOD_ID) GRPMOD  ON ROLLMODGRP.MOD_GRP_ID=GRPMOD.MOD_GRP_ID  ORDER BY SERIAL,MODGRPNAME,MOD_SERIAL,MOD_NAME";
            } else if (rolid != null && rolid.equals("08")) {
                sqlStr = "SELECT MODULE_MAP.MOD_GRP_ID, MOD_GRP_NAME MODGRPNAME, MOD_ID, MOD_NAME, URL,HELP_URL, EMP_SPECIFIC, SERIAL, CONVERT_URL, MOD_SERIAL, NEWWINDOW   FROM (SELECT  G_MODULE.MOD_ID, MOD_GRP_ID, MOD_NAME, URL,HELP_URL, EMP_SPECIFIC, CONVERT_URL, MOD_SERIAL, NEWWINDOW FROM ( "
                        + "SELECT MOD_ID, MOD_NAME, URL,HELP_URL, EMP_SPECIFIC, CONVERT_URL, MOD_SERIAL, NEWWINDOW FROM G_MODULE_LINK WHERE EMP_SPECIFIC='Y' AND HIDE_URL='N') G_MODULE "
                        + "INNER JOIN G_MODULE_MAP ON G_MODULE.MOD_ID=G_MODULE_MAP.MOD_ID) MODULE_MAP "
                        + "INNER JOIN G_MODULE_GROUP ON MODULE_MAP.MOD_GRP_ID=G_MODULE_GROUP.MOD_GRP_ID ORDER BY SERIAL,MODGRPNAME,MOD_SERIAL,MOD_NAME";
            } else if ((rolid != null && rolid.equals("03") || rolid.equals("02")) && (spc == null || spc.equals(""))) {
                sqlStr = "SELECT MODULE_MAP.MOD_GRP_ID, MOD_GRP_NAME MODGRPNAME, MOD_ID, MOD_NAME, URL,HELP_URL, EMP_SPECIFIC, SERIAL, CONVERT_URL, MOD_SERIAL, NEWWINDOW  "
                        + "FROM (SELECT  G_MODULE.MOD_ID, MOD_GRP_ID, MOD_NAME, URL,HELP_URL, EMP_SPECIFIC, CONVERT_URL, MOD_SERIAL, NEWWINDOW FROM ( "
                        + "SELECT MOD_ID, MOD_NAME, URL,HELP_URL, EMP_SPECIFIC, CONVERT_URL, MOD_SERIAL, NEWWINDOW FROM G_MODULE_LINK WHERE MOD_ROLE_ID='" + rolid + "' AND HIDE_URL='N') G_MODULE "
                        + "INNER JOIN G_MODULE_MAP ON G_MODULE.MOD_ID=G_MODULE_MAP.MOD_ID) MODULE_MAP  "
                        + "INNER JOIN G_MODULE_GROUP ON MODULE_MAP.MOD_GRP_ID=G_MODULE_GROUP.MOD_GRP_ID ORDER BY SERIAL,MODGRPNAME,MOD_SERIAL,MOD_NAME";
            } else {
                if (isddo == true) {
                    if (ifEmpClicked.equalsIgnoreCase("Y")) {
                        sqlStr = "SELECT ROLE_ID,GRPMOD.MOD_GRP_ID,ROLLMODGRP.MOD_GRP_NAME MODGRPNAME,GRPMOD.MOD_ID,GRPMOD.MOD_NAME,URL,HELP_URL,EMP_SPECIFIC,SERIAL,CONVERT_URL,MOD_SERIAL,NEWWINDOW  FROM (SELECT G_GROUP_MAP.ROLE_ID,G_MODULE_GROUP.MOD_GRP_ID,G_MODULE_GROUP.MOD_GRP_NAME,SERIAL FROM G_GROUP_MAP INNER JOIN G_MODULE_GROUP ON G_GROUP_MAP.MOD_GRP_ID = G_MODULE_GROUP.MOD_GRP_ID WHERE ROLE_ID='" + rolid + "')  ROLLMODGRP  INNER JOIN  (SELECT distinct G_MODULE_MAP.MOD_GRP_ID,G_MODULE_LINK.MOD_ID,MOD_NAME,URL,HELP_URL,EMP_SPECIFIC,CONVERT_URL,MOD_SERIAL,NEWWINDOW FROM G_MODULE_MAP INNER JOIN (SELECT G_MODULE_LINK.* FROM G_MODULE_LINK INNER JOIN G_PRIVILEGE_MAP ON G_MODULE_LINK.MOD_ID=G_PRIVILEGE_MAP.MOD_ID WHERE G_PRIVILEGE_MAP.SPC='" + spc + "' AND ROLE_ID='" + rolid + "' AND EMP_SPECIFIC='Y' AND HIDE_URL='N')  G_MODULE_LINK ON G_MODULE_MAP.MOD_ID=G_MODULE_LINK.MOD_ID) GRPMOD  ON ROLLMODGRP.MOD_GRP_ID=GRPMOD.MOD_GRP_ID  ORDER BY SERIAL,MODGRPNAME,MOD_SERIAL,MOD_NAME";
                    } else {
                        sqlStr = "SELECT ROLE_ID,GRPMOD.MOD_GRP_ID,ROLLMODGRP.MOD_GRP_NAME MODGRPNAME,GRPMOD.MOD_ID,GRPMOD.MOD_NAME,URL,HELP_URL,EMP_SPECIFIC,SERIAL, "
                                + "CONVERT_URL,MOD_SERIAL,NEWWINDOW  FROM ( "
                                + "SELECT G_GROUP_MAP.ROLE_ID,G_MODULE_GROUP.MOD_GRP_ID,G_MODULE_GROUP.MOD_GRP_NAME,SERIAL FROM G_GROUP_MAP "
                                + "INNER JOIN G_MODULE_GROUP ON G_GROUP_MAP.MOD_GRP_ID = G_MODULE_GROUP.MOD_GRP_ID WHERE ROLE_ID='" + rolid + "')  ROLLMODGRP  "
                                + "INNER JOIN  ( "
                                + "  SELECT distinct G_MODULE_MAP.MOD_GRP_ID,G_MODULE.MOD_ID,MOD_NAME,URL,HELP_URL,EMP_SPECIFIC,CONVERT_URL,MOD_SERIAL,NEWWINDOW FROM G_MODULE_MAP "
                                + "    INNER JOIN ( "
                                + "      SELECT * FROM ( "
                                + "SELECT G_MODULE_LINK.MOD_ID,MOD_NAME,URL,EMP_SPECIFIC,CONVERT_URL,MOD_SERIAL,NEWWINDOW,HELP_URL,MODULE_TYPE,IS_DIRECTLINK,MOD_ROLE_ID,EMPTYPE, "
                                + "PRIV_MAP_ID,SPC,ROLE_ID,MOD_GRP_ID FROM G_MODULE_LINK WHERE HIDE_URL='N' "
                                + "INNER JOIN G_PRIVILEGE_MAP ON G_MODULE_LINK.MOD_ID=G_PRIVILEGE_MAP.MOD_ID "
                                + "WHERE G_PRIVILEGE_MAP.SPC='" + spc + "' AND ROLE_ID='" + rolid + "' AND EMP_SPECIFIC='N' "
                                + "UNION "
                                + "SELECT G_MODULE_LINK.MOD_ID,MOD_NAME,URL,EMP_SPECIFIC,CONVERT_URL,MOD_SERIAL,NEWWINDOW, "
                                + " HELP_URL, MODULE_TYPE, IS_DIRECTLINK,'' MOD_ROLE_ID, EMPTYPE,0 PRIV_MAP_ID,'" + spc + "' SPC,'" + rolid + "' ROLE_ID,'017' MOD_GRP_ID "
                                + " FROM G_MODULE_LINK WHERE MOD_ID='205' ) G_MODULE_LINK "
                                + "    )  G_MODULE_LINK ON G_MODULE_MAP.MOD_ID=G_MODULE_LINK.MOD_ID) "
                                + "GRPMOD  ON ROLLMODGRP.MOD_GRP_ID=GRPMOD.MOD_GRP_ID  "
                                + "ORDER BY SERIAL,MODGRPNAME,MOD_SERIAL,MOD_NAME";
                    }
                } else {
                    if (ifEmpClicked.equalsIgnoreCase("Y")) {
                        sqlStr = "SELECT ROLE_ID,GRPMOD.MOD_GRP_ID,ROLLMODGRP.MOD_GRP_NAME MODGRPNAME,GRPMOD.MOD_ID,GRPMOD.MOD_NAME,URL,HELP_URL,EMP_SPECIFIC,SERIAL,CONVERT_URL,MOD_SERIAL,NEWWINDOW  FROM (SELECT G_GROUP_MAP.ROLE_ID,G_MODULE_GROUP.MOD_GRP_ID,G_MODULE_GROUP.MOD_GRP_NAME,SERIAL FROM G_GROUP_MAP INNER JOIN G_MODULE_GROUP ON G_GROUP_MAP.MOD_GRP_ID = G_MODULE_GROUP.MOD_GRP_ID WHERE ROLE_ID='" + rolid + "')  ROLLMODGRP  INNER JOIN  (SELECT distinct G_MODULE_MAP.MOD_GRP_ID,G_MODULE_LINK.MOD_ID,MOD_NAME,URL,HELP_URL,EMP_SPECIFIC,CONVERT_URL,MOD_SERIAL,NEWWINDOW FROM G_MODULE_MAP INNER JOIN (SELECT G_MODULE_LINK.* FROM G_MODULE_LINK INNER JOIN G_PRIVILEGE_MAP ON G_MODULE_LINK.MOD_ID=G_PRIVILEGE_MAP.MOD_ID WHERE G_PRIVILEGE_MAP.SPC='" + spc + "' AND ROLE_ID='" + rolid + "' AND EMP_SPECIFIC='Y' AND HIDE_URL='N')  G_MODULE_LINK ON G_MODULE_MAP.MOD_ID=G_MODULE_LINK.MOD_ID) GRPMOD  ON ROLLMODGRP.MOD_GRP_ID=GRPMOD.MOD_GRP_ID  ORDER BY SERIAL,MODGRPNAME,MOD_SERIAL,MOD_NAME";
                    } else {
                        sqlStr = "SELECT ROLE_ID,GRPMOD.MOD_GRP_ID,ROLLMODGRP.MOD_GRP_NAME MODGRPNAME,GRPMOD.MOD_ID,GRPMOD.MOD_NAME,URL,HELP_URL,EMP_SPECIFIC,SERIAL,CONVERT_URL,MOD_SERIAL,NEWWINDOW  FROM (SELECT G_GROUP_MAP.ROLE_ID,G_MODULE_GROUP.MOD_GRP_ID,G_MODULE_GROUP.MOD_GRP_NAME,SERIAL FROM G_GROUP_MAP INNER JOIN G_MODULE_GROUP ON G_GROUP_MAP.MOD_GRP_ID = G_MODULE_GROUP.MOD_GRP_ID WHERE ROLE_ID='" + rolid + "')  ROLLMODGRP  INNER JOIN  (SELECT distinct G_MODULE_MAP.MOD_GRP_ID,G_MODULE_LINK.MOD_ID,MOD_NAME,URL,HELP_URL,EMP_SPECIFIC,CONVERT_URL,MOD_SERIAL,NEWWINDOW FROM G_MODULE_MAP INNER JOIN (SELECT G_MODULE_LINK.* FROM G_MODULE_LINK INNER JOIN G_PRIVILEGE_MAP ON G_MODULE_LINK.MOD_ID=G_PRIVILEGE_MAP.MOD_ID WHERE G_PRIVILEGE_MAP.SPC='" + spc + "' AND ROLE_ID='" + rolid + "' AND EMP_SPECIFIC='N' AND HIDE_URL='N')  G_MODULE_LINK ON G_MODULE_MAP.MOD_ID=G_MODULE_LINK.MOD_ID) GRPMOD  ON ROLLMODGRP.MOD_GRP_ID=GRPMOD.MOD_GRP_ID  ORDER BY SERIAL,MODGRPNAME,MOD_SERIAL,MOD_NAME";
                    }
                }
                System.out.println("");
            }

            rs = st.executeQuery(sqlStr);
            String prvGrp = null;
            ModuleProperty mp = null;
            while (rs.next()) {
                if (prvGrp == null) {
                    mg = new ModuleGroup();
                    mg.setModGrpId(rs.getString("MOD_GRP_ID"));
                    mg.setModGrpName(rs.getString("MODGRPNAME"));
                }
                mp = new ModuleProperty();
                mp.setModuleId(rs.getString("MOD_ID"));
                mp.setModuleName(rs.getString("MOD_NAME"));
                mp.setLinkurl(rs.getString("URL"));
                mp.setHelpUrl(rs.getString("HELP_URL"));
                mp.setIfspecific(rs.getString("EMP_SPECIFIC"));
                mp.setConvertUrl(rs.getString("CONVERT_URL"));
                if (rs.getString("NEWWINDOW") == null || rs.getString("NEWWINDOW").equals("")) {
                    mp.setNewWindow("N");
                } else {
                    mp.setNewWindow(rs.getString("NEWWINDOW"));
                }

                if (prvGrp != null && !prvGrp.equals(rs.getString("MOD_GRP_ID"))) {
                    roleListarr.add(mg);
                    mg = new ModuleGroup();
                    moduleList = new ArrayList();
                    mg.setModGrpId(rs.getString("MOD_GRP_ID"));
                    mg.setModGrpName(rs.getString("MODGRPNAME"));

                }
                moduleList.add(mp);
                mg.setModuleListArr(moduleList);
                prvGrp = rs.getString("MOD_GRP_ID");
            }
            if (mg != null) {
                roleListarr.add(mg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }

        return roleListarr;
    }

}
