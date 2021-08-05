package hrms.dao.changemobile;

import hrms.common.DataBaseFunctions;
import hrms.common.Message;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.annotation.Resource;
import javax.sql.DataSource;

public class ChangeMobileDAOImpl implements ChangeMobileDAO {

    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Message changeMobile(String empid, String newmobile) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        boolean duplicatemobile = false;
        int retVal = 0;

        Message msg = new Message();
        
        Connection oracon = null;
        try {
            con = this.dataSource.getConnection();

            String sql = "SELECT MOBILE FROM EMP_MAST WHERE MOBILE=? AND EMP_ID != ?";
            pst = con.prepareStatement(sql);
            pst.setString(1, newmobile);
            pst.setString(2, empid);
            rs = pst.executeQuery();
            if (rs.next()) {
                duplicatemobile = true;
            }

            if (duplicatemobile == false) {
                pst = con.prepareStatement("UPDATE EMP_MAST SET MOBILE=? WHERE EMP_ID=?");
                pst.setString(1, newmobile);
                pst.setString(2, empid);
                retVal = pst.executeUpdate();

                Class.forName("oracle.jdbc.driver.OracleDriver");
              //  oracon = DriverManager.getConnection("jdbc:oracle:thin:@192.168.1.121:1521:orcl", "hrmis", "hrmis");
                oracon = DriverManager.getConnection("jdbc:oracle:thin:@172.16.1.12:1522:orcl", "hrmis", "hrmis");
                pst = oracon.prepareStatement("UPDATE EMP_MAST SET MOBILE=? WHERE EMP_ID=?");
                pst.setString(1, newmobile);
                pst.setString(2, empid);
                retVal = pst.executeUpdate();
            }

            if (retVal > 0) {
                msg.setMessage("CHANGED");
            } else {
                if (duplicatemobile == true) {
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
