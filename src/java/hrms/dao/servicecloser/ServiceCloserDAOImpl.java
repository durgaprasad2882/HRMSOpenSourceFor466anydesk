/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.servicecloser;

import hrms.common.CommonFunctions;
import hrms.common.DataBaseFunctions;
import hrms.model.servicecloser.EmployeeDeceased;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 *
 * @author Manas
 */
public class ServiceCloserDAOImpl implements ServiceCloserDAO {

    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public EmployeeDeceased getEmployeeDeceasedData(String empId) {
        EmployeeDeceased empDeceased = new EmployeeDeceased();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = this.dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT EMP_ID,DATE_OF_DECEASED,DECEASED_TIME,DECEASED_NOTE,DECEASED_TYPE FROM EMP_DECEASED WHERE EMP_ID=?");
            pstmt.setString(1, empId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                empDeceased.setDeceasedDate(CommonFunctions.getFormattedOutputDate1(rs.getDate("DATE_OF_DECEASED")));
                empDeceased.setDeceasednote(rs.getString("DECEASED_NOTE"));
                empDeceased.setDeceasedTime(rs.getString("DECEASED_TIME"));
                empDeceased.setEmpId(rs.getString("EMP_ID"));
                empDeceased.setDeceasedType(rs.getString("DECEASED_TYPE"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return empDeceased;
    }

    @Override
    public void saveEmployeeDeceased(EmployeeDeceased employeeDeceased) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = this.dataSource.getConnection();
            pstmt = con.prepareStatement("INSERT INTO EMP_DECEASED (EMP_ID,SPC,DATE_OF_DECEASED,DECEASED_TIME,DECEASED_NOTE,DECEASED_TYPE) VALUES (?,?,?,?,?,?)");
            pstmt.setString(1, employeeDeceased.getEmpId());
            pstmt.setString(2, employeeDeceased.getCurspc());
            pstmt.setDate(3, new java.sql.Date(CommonFunctions.getDateFromString(employeeDeceased.getDeceasedDate(), "dd-MMM-yyyy").getTime()));
            pstmt.setString(4, employeeDeceased.getDeceasedTime());
            pstmt.setString(5, employeeDeceased.getDeceasednote());
            pstmt.setString(6, employeeDeceased.getDeceasedType());
            pstmt.execute();
            
            pstmt = con.prepareStatement("UPDATE EMP_MAST SET DEP_CODE='10',CUR_SPC=null WHERE EMP_ID =?");
            pstmt.setString(1, employeeDeceased.getEmpId());
            pstmt.execute();

            String rid = null;
            pstmt = con.prepareStatement("INSERT INTO EMP_RELIEVE(RELIEVE_ID, NOT_TYPE, NOT_ID, EMP_ID, RLV_DATE, RLV_TIME, SPC, IF_VISIBLE) VALUES (?,?,?,?,?,?,?,?)");
            rid = CommonFunctions.getMaxCode("emp_relieve", "RELIEVE_ID", con);
            pstmt.setString(1, rid);
            pstmt.setString(2, "DECEASED");
            pstmt.setString(3, "");
            pstmt.setString(4, employeeDeceased.getEmpId());
            pstmt.setDate(5, new java.sql.Date(CommonFunctions.getDateFromString(employeeDeceased.getDeceasedDate(), "dd-MMM-yyyy").getTime()));
            pstmt.setString(6, "AN");
            pstmt.setString(7, employeeDeceased.getCurspc());
            pstmt.setString(8, "Y");
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }
}
