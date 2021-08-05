/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hrms.model.trainingadmin;

/**
 *
 * @author Manoj PC
 */
public class InstituteForm {
    private String InstituteName = null;
    private String location = null;
    private String website = null;
    private String email = null;
    private String phone = null;
    private String contactPerson = null;
    private int instituteId = 0;
    private String outsideTerritory = null;
    private String username = null;
    private String password = null;
    private String opt = null;

    public String getOpt() {
        return opt;
    }

    public void setOpt(String opt) {
        this.opt = opt;
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOutsideTerritory() {
        return outsideTerritory;
    }

    public void setOutsideTerritory(String outsideTerritory) {
        this.outsideTerritory = outsideTerritory;
    }

    public int getInstituteId() {
        return instituteId;
    }

    public void setInstituteId(int instituteId) {
        this.instituteId = instituteId;
    }

    public String getInstituteName() {
        return InstituteName;
    }

    public void setInstituteName(String InstituteName) {
        this.InstituteName = InstituteName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }


}
