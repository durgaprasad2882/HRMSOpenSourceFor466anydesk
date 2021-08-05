/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.notification;

import hrms.model.notification.NotificationBean;

/**
 *
 * @author Surendra
 */
public interface NotificationDAO {
    public String insertNotificationData(NotificationBean nfb);
    public int modifyNotificationData(NotificationBean nfb);
    public NotificationBean dispalyNotificationData(String notid, String nottype);    
    public int deleteNotificationData(String notid, String nottype);
}
