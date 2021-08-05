/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.model.empinfo;

import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author manisha
 */
public class EmployeeMessage {
    private String empid;
    private String empname;
    private String message;
    private String offcode;
    private String isviewed;
    private String  messageondate;
    private String viewondate;
    private MultipartFile uploadedFile;

    public String getEmpid() {
        return empid;
    }

    public void setEmpid(String empid) {
        this.empid = empid;
    }

    public String getEmpname() {
        return empname;
    }

    public void setEmpname(String empname) {
        this.empname = empname;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOffcode() {
        return offcode;
    }

    public void setOffcode(String offcode) {
        this.offcode = offcode;
    }

    public String getIsviewed() {
        return isviewed;
    }

    public void setIsviewed(String isviewed) {
        this.isviewed = isviewed;
    }

    public String getMessageondate() {
        return messageondate;
    }

    public void setMessageondate(String messageondate) {
        this.messageondate = messageondate;
    }

    public String getViewondate() {
        return viewondate;
    }

    public void setViewondate(String viewondate) {
        this.viewondate = viewondate;
    }

   
    

    public MultipartFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(MultipartFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }
    
    
}
