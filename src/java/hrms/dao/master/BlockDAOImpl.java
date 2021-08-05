package hrms.dao.master;

import hrms.common.DataBaseFunctions;
import hrms.model.master.Bank;
import hrms.model.master.Block;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import javax.annotation.Resource;
import javax.sql.DataSource;

public class BlockDAOImpl implements BlockDAO {

    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public ArrayList getBlockList(String distCode) {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement st = null;
        ArrayList blockList = new ArrayList();
        Block block = null;
        try {
            con = dataSource.getConnection();
            st = con.prepareStatement("SELECT BL_CODE,BL_NAME FROM G_BLOCK WHERE DIST_CODE=? ORDER BY BL_NAME");
            st.setString(1, distCode);
            rs = st.executeQuery();
            while (rs.next()) {
                block = new Block();
                block.setBlockCode(rs.getString("BL_CODE"));
                block.setBlockName(rs.getString("BL_NAME"));
                blockList.add(block);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, st);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return blockList;
    }

    @Override
    public Block getBlockDetails(String blockCode) {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement st = null;
        Block block = new Block();
        try {
            con = dataSource.getConnection();
            st = con.prepareStatement("SELECT BL_CODE,BL_NAME FROM G_BLOCK WHERE BL_CODE=? ");
            st.setString(1, blockCode);
            rs = st.executeQuery();
            if (rs.next()) {
                block.setBlockCode(rs.getString("BL_CODE"));
                block.setBlockName(rs.getString("BL_NAME"));

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, st);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return block;
    }

}
