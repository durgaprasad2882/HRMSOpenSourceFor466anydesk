/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.model.onlineTicketing;

import java.io.File;
import java.util.Date;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Surendra
 */
public class OnlineTicketing {
    private String username=null;
    private int ticketId;
    private String userId=null;
    private Date createdDateTime=null;
    private String message=null;
    private Date closedDateTime=null;
    private String status=null;
    private Date overDueDateTime=null;
    private double durationForReply;
    private Date reopenDateTime=null;
    private String assignedToUserId=null;
    private String topicId;
     private String topicName;
    private String offname="";
    private MultipartFile file; 
     private int attachmentId;
    private String ofileName=null;
    private String dfileName =null;
    private int refId ;
    private String refType =null;
    private String filePath =null;
    private String fileType =null;

    public int getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(int attachmentId) {
        this.attachmentId = attachmentId;
    }

    public String getOfileName() {
        return ofileName;
    }

    public void setOfileName(String ofileName) {
        this.ofileName = ofileName;
    }

    public String getDfileName() {
        return dfileName;
    }

    public void setDfileName(String dfileName) {
        this.dfileName = dfileName;
    }

    public int getRefId() {
        return refId;
    }

    public void setRefId(int refId) {
        this.refId = refId;
    }

    public String getRefType() {
        return refType;
    }

    public void setRefType(String refType) {
        this.refType = refType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
    
    

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    
    
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    
    
    
    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    
    
    

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }
    
    

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(Date createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getClosedDateTime() {
        return closedDateTime;
    }

    public void setClosedDateTime(Date closedDateTime) {
        this.closedDateTime = closedDateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getOverDueDateTime() {
        return overDueDateTime;
    }

    public void setOverDueDateTime(Date overDueDateTime) {
        this.overDueDateTime = overDueDateTime;
    }

    public double getDurationForReply() {
        return durationForReply;
    }

    public void setDurationForReply(double durationForReply) {
        this.durationForReply = durationForReply;
    }


    public Date getReopenDateTime() {
        return reopenDateTime;
    }

    public void setReopenDateTime(Date reopenDateTime) {
        this.reopenDateTime = reopenDateTime;
    }

    public String getAssignedToUserId() {
        return assignedToUserId;
    }

    public void setAssignedToUserId(String assignedToUserId) {
        this.assignedToUserId = assignedToUserId;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getOffname() {
        return offname;
    }

    public void setOffname(String offname) {
        this.offname = offname;
    }

    
    
    
    
    

}
