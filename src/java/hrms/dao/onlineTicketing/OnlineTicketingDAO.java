/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.onlineTicketing;

import hrms.model.onlineTicketing.OnlineTicketing;
import java.util.List;

/**
 *
 * @author Surendra
 */
public interface OnlineTicketingDAO {
    
    public void addTicket(OnlineTicketing ticket);
    
    public List getTicketList(String userId);
    
    public OnlineTicketing editTicket(int ticketId);
    
    public void closeTicket(int ticketId);
    
   
    
    
    
    
}
