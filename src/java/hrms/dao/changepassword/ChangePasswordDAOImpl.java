/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.changepassword;

import hrms.common.DataBaseFunctions;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Resource;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.sql.DataSource;
import oracle.ldap.util.jndi.ConnectionUtil;
import org.hibernate.SessionFactory;

/**
 *
 * @author Surendra
 */
public class ChangePasswordDAOImpl implements ChangePasswordDAO {

    @Resource(name = "sessionFactory")
    protected SessionFactory sessionFactory;

    @Resource(name = "dataSource")
    protected DataSource dataSource;
    private String pwd_policy = "((?=.*[a-zA-Z]).(?=.*[@#$%]).(?=.*[@#$%]).{6,})";
    private Pattern pattern;
    private Matcher matcher;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public int modifyUserPassword(String empId, String usertype, String oldpwd, String newpwd) throws SQLException {

        Connection conpostgres = null;
        Connection conora = null;
        PreparedStatement pst = null;
        PreparedStatement pst1 = null;
        int modify = 0;
        int modifications = 0;

        String ipaddress = null;
        String port = null;
        String userName = null;
        String pwd = null;
        DirContext ctx = null;

        ipaddress = "192.168.1.121";
        port = "389";
        userName = "cn=orcladmin";
        pwd = "welcome1";
        try {
            //For PostGres            
            conpostgres = this.dataSource.getConnection();
            //con.setAutoCommit(false);
            pattern = Pattern.compile(pwd_policy);
            matcher = pattern.matcher(newpwd);
            if (matcher.matches()) {                
               
                pst = conpostgres.prepareStatement("UPDATE user_details SET password=? WHERE linkid=? AND password=?");
                pst.setString(1, newpwd);
                pst.setString(2, empId);
                pst.setString(3, oldpwd);
                modifications = pst.executeUpdate();

                if (modifications > 0) {
                    modify = 1;
                } else {
                    modify = 0;
                }

                DataBaseFunctions.closeSqlObjects(pst, pst1);
                DataBaseFunctions.closeSqlObjects(conpostgres);

                //For Oracle
                Class.forName("oracle.jdbc.driver.OracleDriver");
                conora = DriverManager.getConnection("jdbc:oracle:thin:@172.16.1.12:1522:orcl", "hrmis", "hrmis");
                //System.out.println("CON Oracle is: "+con);
                pst = conora.prepareStatement("UPDATE emp_mast SET pwd=? WHERE emp_id=? AND pwd=?");
                pst.setString(1, newpwd);
                pst.setString(2, empId);
                pst.setString(3, oldpwd);
                modifications = pst.executeUpdate();

                if (modifications > 0) {
                    modify = 1;
                } else {
                    modify = 0;
                }
                if(usertype!=null && usertype.equalsIgnoreCase("G")){
                    ctx = ConnectionUtil.getDefaultDirCtx(ipaddress, port, userName, pwd);
                    String userDN = "cn=" + empId + ",cn=Users,dc=hrmsorissa,dc=gov,dc=in";
                    modify(ctx, userDN, newpwd);
                }
            }else{
                modify = 2;//New Password Doesnot 
                
                
                
            }
        } catch (Exception e) {
            //con.rollback();
            e.printStackTrace();
        } finally {
            //con.commit();
            DataBaseFunctions.closeSqlObjects(conora);
        }
        return modify;
    }

    public static void modify(DirContext ctx, String User, String newPassword) throws Exception {
        ModificationItem[] mods = new ModificationItem[1];
        //System.out.println("Inside modify,userid is: "+User);
        //System.out.println("Inside modify,new password is: "+newPassword);
        mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("userpassword", newPassword));
        ctx.modifyAttributes(User, mods);
    }
}
