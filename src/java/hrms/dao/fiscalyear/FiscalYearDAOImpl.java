package hrms.dao.fiscalyear;

import hrms.common.DataBaseFunctions;
import hrms.model.fiscalyear.FiscalYear;
import hrms.model.parmast.ParMaster;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public class FiscalYearDAOImpl implements FiscalYearDAO {

    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public String getDefaultFiscalYear() {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        String defaultFiscalYear = null;
        try {
            con = this.dataSource.getConnection();

            String sql = "SELECT FY FROM FINANCIAL_YEAR WHERE IS_CLOSED='N'";
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            if (rs.next()) {
                defaultFiscalYear = rs.getString("FY");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return defaultFiscalYear;
    }

    @Override
    public List getFiscalYearList() {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        List<FiscalYear> fiscalyearlist = new ArrayList();
        FiscalYear fiscalyr = null;
        try {
            con = this.dataSource.getConnection();

            String sql = "SELECT FY FROM FINANCIAL_YEAR ORDER BY FY DESC";
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                fiscalyr = new FiscalYear();
                fiscalyr.setFy(rs.getString("FY"));
                fiscalyearlist.add(fiscalyr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return fiscalyearlist;
    }

    @Override
    public List getPFiscalYearList() {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        List<FiscalYear> fiscalyearlist = new ArrayList();
        FiscalYear fiscalyr = null;
        try {
            con = this.dataSource.getConnection();
            String sql = "SELECT FY FROM FINANCIAL_YEAR WHERE property_active='Y' ORDER BY FY DESC";
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                fiscalyr = new FiscalYear();
                fiscalyr.setFy(rs.getString("FY"));
                fiscalyearlist.add(fiscalyr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return fiscalyearlist;
    }
}
