package hrms.dao.master;

import hrms.common.DataBaseFunctions;
import hrms.model.master.Payscale;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;

public class PayScaleDAOImpl implements PayScaleDAO{
    
    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    @Override
    public List getPayScaleList() {
        
        Connection con = null;
        
        PreparedStatement pst= null;
        ResultSet rs = null;
        
        List payscalelist = new ArrayList();
        
        Payscale ps = null;
        try {
            con = this.dataSource.getConnection();
            
            String sql = "SELECT PAYSCALE FROM G_PAYSCALE ORDER BY PAYSCALE ASC";
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            while(rs.next()){
                ps = new Payscale();
                ps.setPayscale(rs.getString("PAYSCALE"));
                payscalelist.add(ps);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return payscalelist;
    }
    
}
