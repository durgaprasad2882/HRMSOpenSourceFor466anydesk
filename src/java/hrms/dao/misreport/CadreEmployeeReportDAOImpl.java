/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.misreport;

import hrms.common.DataBaseFunctions;
import hrms.model.employee.Employee;
import hrms.model.misreport.IncumbancyChart;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import javax.annotation.Resource;
import javax.sql.DataSource;
import static org.apache.commons.httpclient.util.DateUtil.formatDate;
//import static org.apache.http.client.utils.DateUtils.formatDate;


/**
 *
 * @author Manas Jena
 */
public class CadreEmployeeReportDAOImpl implements CadreEmployeeReportDAO {

    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public ArrayList getEmployeeList(String cadreCode) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList employees = new ArrayList();
        try {
            con = this.dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT emp_id,f_name,m_name,l_name,cadreid,rec_source,cur_spc,allotment_year,SPN,cur_salary,G_OFFICE.OFF_ADDRESS FROM emp_mast "
                    + "left outer join G_SPC ON emp_mast.cur_spc = G_SPC.SPC "
                    + "left outer join G_OFFICE ON emp_mast.cur_off_code = G_OFFICE.OFF_CODE "
                    + "WHERE emp_mast.CUR_CADRE_CODE=? and dep_code != '09' order by allotment_year");
            pstmt.setString(1, cadreCode);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Employee employee = new Employee();
                employee.setEmpid(rs.getString("emp_id"));
                employee.setFname(rs.getString("f_name"));
                employee.setMname(rs.getString("m_name"));
                employee.setLname(rs.getString("l_name"));
                employee.setIdmark(rs.getString("cadreid"));
                employee.setRecsource(rs.getString("rec_source"));
                employee.setCardeallotmentyear(rs.getString("allotment_year"));
                employee.setSpn(rs.getString("SPN"));
                employee.setStation(rs.getString("OFF_ADDRESS"));
                employee.setAddlCharge("");
                employee.setRemark("");
                employee.setPayScale(rs.getString("cur_salary"));
                employees.add(employee);
            }
        } catch (Exception exe) {
            exe.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return employees;
    }

    @Override
    public Employee getEmployeeData(String empid) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Employee employee = new Employee();
        try {
            con = this.dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT emp_id,f_name,m_name,l_name,id_number,allotment_year,domicile,prm_dist_code,rec_source,jointime_of_goo,SPN,prm_address,curr_post_doj,cur_salary FROM emp_mast_cadre "
                    + "left outer join G_SPC ON emp_mast_cadre.cur_spc = G_SPC.SPC WHERE emp_id=?");
            pstmt.setString(1, empid);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                employee.setEmpid(rs.getString("emp_id"));
                employee.setFname(rs.getString("f_name"));
                employee.setMname(rs.getString("m_name"));
                employee.setLname(rs.getString("l_name"));
                employee.setIdmark(rs.getString("id_number"));
                employee.setCardeallotmentyear(rs.getString("allotment_year"));
                employee.setRecsource(rs.getString("rec_source"));
                employee.setDomicile(rs.getString("domicile"));
                employee.setPermanentdist(rs.getString("prm_dist_code"));
                employee.setPermanentaddr(rs.getString("prm_address"));
                employee.setRecsource(rs.getString("rec_source"));
                employee.setJoindategoo(formatDate(rs.getDate("jointime_of_goo")));
                employee.setSpn(rs.getString("SPN"));
                employee.setDateOfCurPosting(rs.getDate("curr_post_doj"));
                employee.setPayScale(rs.getString("cur_salary"));
            }
        } catch (Exception exe) {
            exe.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return employee;
    }

    public void saveEmployeeData(Employee employee) {
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = this.dataSource.getConnection();
            pstmt = con.prepareStatement("INSERT INTO emp_mast_cadre (emp_id,f_name,m_name,l_name,dob,gender)");

        } catch (Exception exe) {
            exe.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    public static String getEmpID() throws Exception {
        Connection con = null;
        ResultSet rs = null;
        String maxID = null;
        String strID = null;
        PreparedStatement pstmt = null;
        try {

            pstmt = con.prepareStatement("SELECT MAX(EMP_ID) EMP_ID FROM emp_mast_cadre WHERE EMP_ID LIKE '91%'");
            rs = pstmt.executeQuery();
            int empCtr = 0;
            if (rs != null) {
                while (rs.next()) {
                    maxID = rs.getString("EMP_ID");
                    if (maxID != null && !maxID.equals("")) {
                        empCtr = Integer.parseInt(maxID);
                        empCtr++;
                        strID = Integer.toString(empCtr);
                    } else {
                        strID = "91000001";
                    }
                }
            } else {
                strID = "91000001";
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs);
            DataBaseFunctions.closeSqlObjects(con);
        }

        return strID;

    }

    @Override
    public ArrayList getIncumbancyChart(String spc) {
        ArrayList incumbancychart = new ArrayList();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = this.dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT EMP_JOIN.EMP_ID,EMP_JOIN.NOT_TYPE,JOIN_DATE,RLV_DATE,F_NAME,M_NAME,L_NAME FROM EMP_JOIN "
                    + "LEFT OUTER JOIN EMP_RELIEVE ON  EMP_JOIN.JOIN_ID = EMP_RELIEVE.JOIN_ID "
                    + "INNER JOIN EMP_MAST ON EMP_MAST.EMP_ID = EMP_JOIN.emp_id where EMP_JOIN.spc = ?");
            pstmt.setString(1, spc);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                IncumbancyChart ichart = new IncumbancyChart();
                ichart.setEmpid(rs.getString("EMP_ID"));
                ichart.setFname(rs.getString("F_NAME"));
                ichart.setMname(rs.getString("M_NAME"));
                ichart.setLname(rs.getString("L_NAME"));
                ichart.setJointype(rs.getString("NOT_TYPE"));
                ichart.setJoindate(rs.getDate("JOIN_DATE"));
                ichart.setRelievedate(rs.getDate("RLV_DATE"));
                incumbancychart.add(ichart);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return incumbancychart;
    }

    @Override
    public ArrayList getEmployeeIncumbancyChart(String empid) {
        ArrayList incumbancychart = new ArrayList();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = this.dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT EMP_JOIN.EMP_ID,EMP_JOIN.NOT_TYPE,JOIN_DATE,RLV_DATE,F_NAME,M_NAME,L_NAME FROM EMP_JOIN "
                    + "LEFT OUTER JOIN EMP_RELIEVE ON  EMP_JOIN.JOIN_ID = EMP_RELIEVE.JOIN_ID "
                    + "INNER JOIN EMP_MAST ON EMP_MAST.EMP_ID = EMP_JOIN.emp_id where EMP_JOIN.emp_id = ?");
            pstmt.setString(1, empid);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                IncumbancyChart ichart = new IncumbancyChart();
                ichart.setEmpid(rs.getString("EMP_ID"));
                ichart.setFname(rs.getString("F_NAME"));
                ichart.setMname(rs.getString("M_NAME"));
                ichart.setLname(rs.getString("L_NAME"));
                ichart.setJointype(rs.getString("NOT_TYPE"));
                ichart.setJoindate(rs.getDate("JOIN_DATE"));
                ichart.setRelievedate(rs.getDate("RLV_DATE"));
                incumbancychart.add(ichart);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return incumbancychart;
    }
}
