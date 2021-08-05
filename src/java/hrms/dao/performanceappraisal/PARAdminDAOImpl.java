/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.performanceappraisal;

import hrms.common.DataBaseFunctions;
import hrms.model.parmast.PARSearchResult;
import hrms.model.parmast.ParAdminProperties;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 *
 * @author DurgaPrasad
 */
public class PARAdminDAOImpl implements PARAdminDAO {

    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public PARSearchResult getPARList(String fiscalyear, int limit, int offset) {//rows,page
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        ArrayList parList = new ArrayList();
        PARSearchResult parSearchResult = new PARSearchResult();
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("SELECT COUNT(*) CNT FROM PAR_MASTER WHERE FISCAL_YEAR=?");
            pst.setString(1, fiscalyear);
            rs = pst.executeQuery();
            if (rs.next()) {
                parSearchResult.setTotalPARFound(rs.getInt("CNT"));
            }

            pst = con.prepareStatement("SELECT EMP_ID FROM PAR_MASTER WHERE FISCAL_YEAR=? GROUP BY EMP_ID");
            pst.setString(1, fiscalyear);
            pst.setFetchSize(limit);
            pst.setMaxRows(limit * (offset - 1));
            pst.setFetchDirection(ResultSet.FETCH_FORWARD);
            rs = pst.executeQuery();
            while (rs.next()) {
                ParAdminProperties parAdminProperties = new ParAdminProperties();
                parAdminProperties.setEmpId(rs.getString("EMP_ID"));
                parList.add(parAdminProperties);
            }
            parSearchResult.setParlist(parList);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return parSearchResult;
    }

    @Override
    public PARSearchResult getPARList(String fiscalyear, String empid, int limit, int offset) {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        ArrayList par = new ArrayList();
        PARSearchResult parSearchResult = new PARSearchResult();
        try {
            con = dataSource.getConnection();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return parSearchResult;
    }

}
