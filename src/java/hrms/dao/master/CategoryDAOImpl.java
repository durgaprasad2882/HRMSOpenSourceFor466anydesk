
package hrms.dao.master;

import hrms.common.DataBaseFunctions;
import hrms.model.master.Category;
import hrms.model.master.MaritalStatus;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import javax.annotation.Resource;
import javax.sql.DataSource;

public class CategoryDAOImpl implements CategoryDAO{
     @Resource(name = "dataSource")
    protected DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public ArrayList getCategoryList(){
   Connection con = null;
        ResultSet rs = null;
        PreparedStatement st = null;
        ArrayList categoryList = new ArrayList();
        Category category = null;
        try {
            con = dataSource.getConnection();
            st = con.prepareStatement("SELECT CATEGORY FROM G_CATEGORY ORDER BY CATEGORY");
            rs = st.executeQuery();
            while (rs.next()) {
                category = new Category();
                System.out.println("-------------cat------------"+rs.getString("CATEGORY"));
                category.setCategoryid(rs.getString("CATEGORY"));
                category.setCategoryName(rs.getString("CATEGORY"));
                categoryList.add(category);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, st);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return categoryList;
    }   
}
