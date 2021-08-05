/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.master;

import hrms.common.DataBaseFunctions;
import hrms.model.master.Village;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 *
 * @author lenovo pc
 */
public class VillageDAOImpl implements VillageDAO {

    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override

    public ArrayList getVillageList(String distCode, String psCode) {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement st = null;
        ArrayList villList = new ArrayList();
        Village vill = null;
        try {
            con = dataSource.getConnection();
            st = con.prepareStatement("SELECT VILL_CODE,VILLAGE_NAME FROM G_VILLAGE WHERE DIST_CODE=? AND PS_CODE=? ORDER BY VILLAGE_NAME");
            st.setString(1, distCode);
            st.setString(2, psCode);
            rs = st.executeQuery();
            while (rs.next()) {
                vill = new Village();
                vill.setVillageCode(rs.getString("VILL_CODE"));
                vill.setVillageName(rs.getString("VILLAGE_NAME"));
                villList.add(vill);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, st);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return villList;
    }
}
