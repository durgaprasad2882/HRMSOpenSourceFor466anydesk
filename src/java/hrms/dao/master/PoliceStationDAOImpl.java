/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.master;

import hrms.common.DataBaseFunctions;
import hrms.model.master.PoliceStation;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 *
 * @author lenovo pc
 */
public class PoliceStationDAOImpl implements PoliceStationDAO {

    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public ArrayList getPoliceStationList(String distCode) {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement st = null;
        ArrayList poStList = new ArrayList();
        PoliceStation ps = null;
        try {
            con = dataSource.getConnection();
            st = con.prepareStatement("SELECT PS_CODE,PS_NAME FROM G_PS WHERE DIST_CODE=? ORDER BY PS_NAME");
            st.setString(1, distCode);
            rs = st.executeQuery();
            while (rs.next()) {
                ps = new PoliceStation();
                ps.setPsCode(rs.getString("PS_CODE"));
                ps.setPsName(rs.getString("PS_NAME"));
                poStList.add(ps);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, st);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return poStList;
    }

}
