/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.onlineTicketing;

import hrms.common.CommonFunctions;
import hrms.common.DataBaseFunctions;
import hrms.model.onlineTicketing.OnlineTicketing;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 *
 * @author Surendra
 */
public class OnlineTicketingDAOImpl implements OnlineTicketingDAO {

    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void addTicket(OnlineTicketing ticket) {

        Connection con = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        PreparedStatement pst = null;
        String createdDateTime = "";
        String overDueDateTime = "";
        try {
            con = dataSource.getConnection();
            /*pst = con.prepareStatement("select * from user_details where linkid='59001114'");
             rs=pst.executeQuery();
             if(){
                
             }
             */
            Calendar cal = Calendar.getInstance();
            DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");
            createdDateTime = dateFormat.format(cal.getTime());
            cal.add(Calendar.DAY_OF_YEAR, 1);
            overDueDateTime = dateFormat.format(cal.getTime());
            int tktId = CommonFunctions.getMaxCode(con, "ticket", "ticket_id");
            ps = con.prepareStatement("INSERT INTO ticket (ticket_id,user_id,created_date_time,message,closed_date_time,"
                    + "status,over_due_date_time,duration_for_reply,reopen_date_time,assigned_to_user_id,topic_id ) VALUES (?,?,?,?,?,?,?,?,?,?,?) ");

            ps.setInt(1, tktId);
            ps.setString(2, ticket.getUserId());
            ps.setTimestamp(3, new Timestamp(dateFormat.parse(createdDateTime).getTime()));
            ps.setString(4, ticket.getMessage());
            ps.setTimestamp(5, null);
            ps.setString(6, ticket.getStatus());
            ps.setTimestamp(7, new Timestamp(dateFormat.parse(overDueDateTime).getTime()));
            ps.setDouble(8, 0);
            ps.setTimestamp(9, null);
            ps.setString(10, ticket.getAssignedToUserId());
            ps.setInt(11, Integer.parseInt(ticket.getTopicId()));
            ps.execute();
            if (ticket.getOfileName() != null && !ticket.getOfileName().equals("")) {
                pst = con.prepareStatement("INSERT INTO ticket_attachment (attachment_id,o_file_name,d_file_name,ref_id,ref_type,file_path,file_type) VALUES (?,?,?,?,?,?,?)");
                pst.setInt(1, CommonFunctions.getMaxCode(con, "ticket_attachment", "attachment_id"));
                pst.setString(2, ticket.getOfileName());
                pst.setString(3, ticket.getDfileName());
                pst.setInt(4, tktId);
                pst.setString(5, ticket.getRefType());
                pst.setString(6, ticket.getFilePath());
                pst.setString(7, ticket.getFileType());
                pst.execute();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
            DataBaseFunctions.closeSqlObjects(ps, pst);
        }
    }

    @Override
    public List getTicketList(String userId) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List li = new ArrayList();
        OnlineTicketing ticket = null;
        try {
            con = dataSource.getConnection();
            ps = con.prepareStatement("SELECT ticket_id,user_id,created_date_time,message,closed_date_time, "
                    + "status,over_due_date_time,duration_for_reply,reopen_date_time,assigned_to_user_id,topic,ticket.topic_id,off_en FROM ticket  "
                    + "LEFT OUTER JOIN user_details userdet ON ticket.user_id=userdet.username "
                    + "LEFT OUTER JOIN emp_mast ON userdet.linkid=emp_mast.emp_id "
                    + "LEFT OUTER JOIN g_office off ON emp_mast.cur_off_code=off.off_code LEFT OUTER JOIN g_ticket_topic on g_ticket_topic.topic_id=ticket.topic_id  WHERE user_id=?");
            ps.setString(1, userId);
            rs = ps.executeQuery();
            while (rs.next()) {
                ticket = new OnlineTicketing();
                ticket.setTopicName(rs.getString("topic"));
                ticket.setUserId(rs.getString("user_id"));
                ticket.setTicketId(rs.getInt("ticket_id"));
                if (rs.getTimestamp("created_date_time") != null) {
                    ticket.setCreatedDateTime(new Date(rs.getTimestamp("created_date_time").getTime()));
                } else {
                    ticket.setCreatedDateTime(null);
                }

                ticket.setMessage(rs.getString("message"));

                if (rs.getTimestamp("closed_date_time") != null) {
                    ticket.setClosedDateTime(new Date(rs.getTimestamp("closed_date_time").getTime()));
                } else {
                    ticket.setClosedDateTime(null);
                }
                ticket.setStatus(rs.getString("status"));
                if (rs.getTimestamp("over_due_date_time") != null) {
                    ticket.setOverDueDateTime(new Date(rs.getTimestamp("over_due_date_time").getTime()));
                } else {
                    ticket.setOverDueDateTime(null);
                }
                ticket.setDurationForReply(rs.getDouble("duration_for_reply"));
                if (rs.getTimestamp("reopen_date_time") != null) {
                    ticket.setReopenDateTime(new Date(rs.getTimestamp("reopen_date_time").getTime()));
                } else {
                    ticket.setReopenDateTime(null);
                }
                ticket.setAssignedToUserId(rs.getString("assigned_to_user_id"));
                ticket.setTopicId(rs.getInt("topic_id") + "");
                ticket.setOffname(rs.getString("off_en"));
                li.add(ticket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return li;
    }

    @Override
    public OnlineTicketing editTicket(int ticketId) {
        Connection con = null;

        try {

            con = dataSource.getConnection();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return null;
    }

    @Override
    public void closeTicket(int ticketId) {
        Connection con = null;
        PreparedStatement ps = null;
        String closedDate = "";
        try {
            con = dataSource.getConnection();
            Calendar cal = Calendar.getInstance();
            DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");
            closedDate = dateFormat.format(cal.getTime());

            ps = con.prepareStatement("UPDATE ticket SET closed_date_time=? WHERE ticket_id=?");
            ps.setTimestamp(1, new Timestamp(dateFormat.parse(closedDate).getTime()));
            ps.setInt(2, ticketId);
            ps.execute();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

}
