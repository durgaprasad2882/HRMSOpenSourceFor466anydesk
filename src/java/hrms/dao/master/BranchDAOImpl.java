/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.master;

import hrms.SelectOption;
import hrms.common.DataBaseFunctions;
import hrms.model.master.Branch;
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
public class BranchDAOImpl implements BranchDAO{
     @Resource(name = "dataSource")
    protected DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    @Override
    public ArrayList getBranchList(String bankCode)  {
        ArrayList branchList = new ArrayList();
        ResultSet rs = null;
        Statement st = null;
        Connection con = null;
        Branch branch=null;
        try {
            con = dataSource.getConnection();
            st = con.createStatement();
            rs = st.executeQuery("SELECT BRANCH_CODE,BRANCH_NAME,IFSC_CODE,MICR_CODE,STATE_CODE,DIST_CODE FROM G_BRANCH WHERE BANK_CODE='" + bankCode + "' ORDER BY BRANCH_NAME");
            while (rs.next()) {
                branch=new Branch();
                branch.setBranchcode(rs.getString("BRANCH_CODE"));
                branch.setBranchname(rs.getString("BRANCH_NAME"));
                branch.setIfsccode(rs.getString("IFSC_CODE"));
                branch.setMicrcode(rs.getString("MICR_CODE"));
                branch.setStatecode(rs.getString("STATE_CODE"));
                branch.setDistcode(rs.getString("DIST_CODE"));
                branchList.add(branch);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return branchList;
    } 
}
