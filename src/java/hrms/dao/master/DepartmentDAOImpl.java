/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hrms.dao.master;

import hrms.SelectOption;
import hrms.common.DataBaseFunctions;
import hrms.model.master.Department;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 *
 * @author Durga
 */
public class DepartmentDAOImpl implements DepartmentDAO{
    
    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    @Override
    public List getDepartmentList() {
        Connection con = null;

        Statement stmt = null;
        ResultSet rs = null;

        List deptlist = new ArrayList();        
        try {
            con = dataSource.getConnection();
            String sql = "SELECT department_code,department_name from g_department where if_active='Y' order by department_name";
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Department department = new Department();                
                department.setDeptCode(rs.getString("department_code"));
                department.setDeptName(rs.getString("department_name"));
                deptlist.add(department);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return deptlist;
    }
    
    @Override
    public List getDeptList() {
        Connection con = null;

        Statement stmt = null;
        ResultSet rs = null;

        List deptlist = new ArrayList();        
        try {
            con = dataSource.getConnection();
            String sql = "SELECT department_code,department_name from g_department where if_active='Y' order by department_name";
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                SelectOption so = new SelectOption();              
                so.setValue(rs.getString("department_code"));
                so.setLabel(rs.getString("department_name"));
                deptlist.add(so);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return deptlist;
    }
    
}
