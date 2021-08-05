/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.master;

import hrms.common.DataBaseFunctions;
import hrms.model.master.TicketTopic;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 *
 * @author lenovo pc
 */
public class OnlineTicketTopicDAOImpl implements OnlineTicketTopicDAO{
   
    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
       
   
     @Override
    public List getTicketTopicList() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List topiclist = new ArrayList();
         TicketTopic ticketTopic = null;
        try {
            con = dataSource.getConnection();
            System.out.println("SELECT topic_id,topic FROM g_ticket_topic");
            ps = con.prepareStatement("SELECT topic_id,topic FROM g_ticket_topic");
            rs = ps.executeQuery();
            while (rs.next()) {
                ticketTopic=new TicketTopic();
                ticketTopic.setTopicId(rs.getInt("topic_id"));
                ticketTopic.setTopic(rs.getString("topic"));
                topiclist.add(ticketTopic);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
            DataBaseFunctions.closeSqlObjects(ps);
        }
        return topiclist;
    }
    
}
