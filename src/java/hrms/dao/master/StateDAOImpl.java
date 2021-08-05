/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.master;

import hrms.common.DataBaseFunctions;
import hrms.model.master.State;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;
/**
 *
 * @author Manas Jena
 */
public class StateDAOImpl implements StateDAO {

    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List getStateList() {
        Connection con = null;

        Statement stmt = null;
        ResultSet rs = null;

        List statelist = new ArrayList();
        try {
            con = dataSource.getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT * FROM g_state");
            while (rs.next()) {
                State state = new State();                
                state.setStatecode(rs.getString("state_code"));
                state.setStatename(rs.getString("state_name"));
                statelist.add(state);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return statelist;
    }
@Override
    public State getStateDetails(String stateCode) {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement st = null;
        State state = new State();
        try {
            con = dataSource.getConnection();
            st = con.prepareStatement("SELECT STATE_CODE,STATE_NAME FROM G_STATE WHERE STATE_CODE=? ");
            st.setString(1, stateCode);
            rs = st.executeQuery();
            if (rs.next()) {
                state.setStatecode(rs.getString("state_code"));
                state.setStatename(rs.getString("state_name"));

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, st);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return state;
    }
}


