/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.master;

import hrms.common.DataBaseFunctions;
import hrms.model.master.PostOffice;
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
public class PostOfficeDAOImpl implements PostOfficeDAO {
       @Resource(name = "dataSource")
    protected DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
        @Override
     public ArrayList getPostOfficeList(String distCode){
         Connection con = null;
        ResultSet rs = null;
        PreparedStatement st = null;
        ArrayList poList = new ArrayList();
        PostOffice po=null;
        try {
            con = dataSource.getConnection();
            st = con.prepareStatement("SELECT PO_CODE,PO_NAME FROM G_PO WHERE DIST_CODE=? ORDER BY PO_NAME");
            st.setString(1, distCode);
            rs = st.executeQuery();
            while(rs.next()){
                po=new PostOffice();
                po.setPostOfficeCode(rs.getString("PO_CODE"));
                po.setPostOfficeName(rs.getString("PO_NAME"));
                poList.add(po);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, st);
            DataBaseFunctions.closeSqlObjects(con);
        }
       return poList;
    }
     
}
