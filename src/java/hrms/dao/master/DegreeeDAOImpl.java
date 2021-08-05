/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.master;

import hrms.common.DataBaseFunctions;
import hrms.model.master.Degree;
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
public class DegreeeDAOImpl implements DegreeeDAO{
     @Resource(name = "dataSource")
    protected DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public ArrayList getDegreeList(){
         Connection con = null;
        ResultSet rs = null;
        PreparedStatement st = null;
        ArrayList degreeList = new ArrayList();
        Degree degree = null;
        try {
            con = dataSource.getConnection();
            st = con.prepareStatement("SELECT DEGREE FROM G_DEGREE ORDER BY DEGREE");
            rs = st.executeQuery();
            while (rs.next()) {
                degree = new Degree();
                degree.setDegreeCode(rs.getString("DEGREE"));
                degree.setDegreeName(rs.getString("DEGREE"));
                degreeList.add(degree);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, st);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return degreeList;
    }
    
}
