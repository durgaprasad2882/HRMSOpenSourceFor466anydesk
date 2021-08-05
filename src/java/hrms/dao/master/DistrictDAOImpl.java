/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.master;

import hrms.common.DataBaseFunctions;
import hrms.model.master.District;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 *
 * @author Manas Jena
 */
public class DistrictDAOImpl implements DistrictDAO{
    @Resource(name = "dataSource")
    protected DataSource dataSource;
    
    public DataSource getDataSource() {
        return dataSource;
    }
    
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    @Override
    public ArrayList getDistrictList() {
        Connection con = null;
        ResultSet rs = null;
        Statement st = null;
        ArrayList districtList = new ArrayList();
        try {            
            con = dataSource.getConnection();
            st = con.createStatement();
            rs = st.executeQuery("select * from g_district where state_code='21' order by dist_name");
            while (rs.next()) {
                District district = new District();
                district.setDistCode(rs.getString("dist_code"));
                district.setDistName(rs.getString("dist_name"));
                districtList.add(district);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs,st);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return districtList;
    }
    
}
