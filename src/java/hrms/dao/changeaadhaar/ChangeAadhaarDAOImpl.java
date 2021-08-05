package hrms.dao.changeaadhaar;

import hrms.common.DataBaseFunctions;
import hrms.common.Message;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.annotation.Resource;
import javax.sql.DataSource;

public class ChangeAadhaarDAOImpl implements ChangeAadhaarDAO {

    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Message changeAadhaar(String empid, String newaadhaar) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        boolean duplicateaadhaar = false;
        int retVal = 0;

        Message msg = new Message();
        Connection oracon = null;
        try {
            con = this.dataSource.getConnection();

            String sql = "SELECT * FROM emp_id_doc WHERE id_no=? AND EMP_ID != ? AND id_description=?";
            pst = con.prepareStatement(sql);
            pst.setString(1, newaadhaar);
            pst.setString(2, empid);
            pst.setString(3, "AADHAAR");
            rs = pst.executeQuery();
            if (rs.next()) {
                duplicateaadhaar = true;
            }

            if (duplicateaadhaar == false) {

                sql = "SELECT * FROM emp_id_doc WHERE EMP_ID = ? AND id_description=?";
                pst = con.prepareStatement(sql);
                pst.setString(1, empid);
                pst.setString(2, "AADHAAR");
                rs = pst.executeQuery();
                if (rs.next()) {
                    pst = con.prepareStatement("UPDATE emp_id_doc SET id_no=? WHERE EMP_ID=? AND id_description=?");
                    pst.setString(1, newaadhaar);
                    pst.setString(2, empid);
                    pst.setString(3, "AADHAAR");
                    retVal = pst.executeUpdate();
                } else {
                    pst = con.prepareStatement("INSERT INTO emp_id_doc(emp_id,id_description,id_no) values(?,?,?)");
                    pst.setString(1, empid);
                    pst.setString(2, "AADHAAR");
                    pst.setString(3, newaadhaar);
                    retVal = pst.executeUpdate();
                }
                
                Class.forName("oracle.jdbc.driver.OracleDriver");
                oracon = DriverManager.getConnection("jdbc:oracle:thin:@172.16.1.12:1522:orcl", "hrmis", "hrmis");
               // oracon = DriverManager.getConnection("jdbc:oracle:thin:@192.168.1.121:1521:orcl", "hrmis", "hrmis");
                
                sql = "SELECT * FROM emp_id_doc WHERE EMP_ID = ? AND id_description=?";
                pst = oracon.prepareStatement(sql);
                pst.setString(1, empid);
                pst.setString(2, "AADHAAR");
                rs = pst.executeQuery();
                if (rs.next()) {
                    pst = oracon.prepareStatement("UPDATE emp_id_doc SET id_no=? WHERE EMP_ID=? AND id_description=?");
                    pst.setString(1, newaadhaar);
                    pst.setString(2, empid);
                    pst.setString(3, "AADHAAR");
                    retVal = pst.executeUpdate();
                } else {
                    pst = oracon.prepareStatement("INSERT INTO emp_id_doc(emp_id,id_description,id_no) values(?,?,?)");
                    pst.setString(1, empid);
                    pst.setString(2, "AADHAAR");
                    pst.setString(3, newaadhaar);
                    retVal = pst.executeUpdate();
                }
            }

            if (retVal > 0) {
                msg.setMessage("CHANGED");
            } else {
                if (duplicateaadhaar == true) {
                    msg.setMessage("DUPLICATE");
                } else {
                    msg.setMessage("ERROR");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs,pst);
            DataBaseFunctions.closeSqlObjects(con);
            DataBaseFunctions.closeSqlObjects(oracon);
            
        }
        return msg;
    }
}
