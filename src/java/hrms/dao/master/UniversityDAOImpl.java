/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.master;

import hrms.common.DataBaseFunctions;
import hrms.model.master.University;

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
public class UniversityDAOImpl implements UniversityDAO{
    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public ArrayList getUniversityList(){
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement st = null;
        ArrayList universityList = new ArrayList();
        University university = null;
        try {
            con = dataSource.getConnection();
            st = con.prepareStatement("SELECT BOARD FROM G_BOARD_UNIVERSITY ORDER BY BOARD");
            rs = st.executeQuery();
            while (rs.next()) {
                university = new University();
                university.setBoardCode(rs.getString("BOARD"));
                university.setBoardName(rs.getString("BOARD"));
                universityList.add(university);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, st);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return universityList;
    } 
    
}
