/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.master;

import hrms.common.DataBaseFunctions;
import hrms.model.master.Treasury;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.springframework.stereotype.Component;

/**
 *
 * @author Manas Jena
 */
@Component
public class TreasuryDAOImpl implements TreasuryDAO {

    @Resource(name = "dataSource")
    protected DataSource dataSource;
    
    public DataSource getDataSource() {
        return dataSource;
    }
    
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    @Override
    public ArrayList getTreasuryList() {
        Connection con = null;
        ResultSet rs = null;
        Statement st = null;
        ArrayList treasuryList = new ArrayList();
        try {            
           // con = dataSource.getConnection();
            con=this.getDBConnection();
            st = con.createStatement();
            rs = st.executeQuery("select * from g_treasury order by tr_name asc");
            while (rs.next()) {
                Treasury treasury = new Treasury();
                treasury.setTreasuryCode(rs.getString("tr_code"));
                treasury.setTreasuryName(rs.getString("tr_name"));
                treasury.setOfficeCode(rs.getString("off_code"));
                treasury.setAgtreasuryCode(rs.getString("ag_treasury_code"));
                treasuryList.add(treasury);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs,st);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return treasuryList;
    }
    @Override
    public ArrayList getAGTreasuryList() {
        Connection con = null;
        ResultSet rs = null;
        Statement st = null;
        ArrayList treasuryList = new ArrayList();
        try {            
            con = dataSource.getConnection();
            //con=this.getDBConnection();
            st = con.createStatement();
            rs = st.executeQuery("select distinct ag_treasury_code from g_treasury where ag_treasury_code is not null order by ag_treasury_code asc ");
            while (rs.next()) {
                Treasury treasury = new Treasury();
                treasury.setAgtreasuryCode(rs.getString("ag_treasury_code"));
                treasury.setTreasuryName(rs.getString("ag_treasury_code"));
                treasuryList.add(treasury);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs,st);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return treasuryList;
    }
    private Connection getDBConnection(){
        Connection con = null;
        
        try {
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection("jdbc:postgresql://172.16.1.16/hrmis", "hrmis2", "cmgi");
        }catch (Exception e) {
            e.printStackTrace();
        }
       return con; 
    }    
}
