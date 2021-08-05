/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.master;

import hrms.common.DataBaseFunctions;
import hrms.model.master.Bank;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 *
 * @author lenovo pc
 */
public class BankDAOImpl implements BankDAO{
    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public ArrayList getBankList() {
        Connection con = null;
        ResultSet rs = null;
        Statement st = null;
        ArrayList bankList = new ArrayList();
        Bank bank=null;
        try {
            con = dataSource.getConnection();
            st = con.createStatement();
            rs = st.executeQuery("SELECT BANK_CODE,BANK_NAME FROM G_BANK  ORDER BY BANK_NAME");
            while(rs.next()){
                bank=new Bank();
                bank.setBankcode(rs.getString("BANK_CODE"));
                bank.setBankname(rs.getString("BANK_NAME"));
                bankList.add(bank);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, st);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return bankList;
    }
}
