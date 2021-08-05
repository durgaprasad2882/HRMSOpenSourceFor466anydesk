package hrms.dao.master;

import hrms.SelectOption;
import hrms.common.DataBaseFunctions;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;

public class CadreDAOImpl implements CadreDAO{
    
    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    @Override
    public List getCadreList(String deptcode) {
        Connection con = null;

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        List cadrelist = new ArrayList();
        SelectOption so = null;
        try {
            con = dataSource.getConnection();            

            pstmt = con.prepareStatement("SELECT cadre_code,cadre_name from g_cadre where department_code=? order by cadre_name asc");
            pstmt.setString(1, deptcode);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                so = new SelectOption();
                so.setValue(rs.getString("cadre_code"));
                so.setLabel(rs.getString("cadre_name"));
                cadrelist.add(so);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs,pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return cadrelist;
    }
    
}
