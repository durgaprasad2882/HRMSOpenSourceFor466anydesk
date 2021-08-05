/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.model.master;

import java.io.Serializable;

/**
 *
 * @author Surendra
 */
public class GPost implements Serializable {

    private String postcode;

    private String deptcode;

    private String post;

    private String ifael;

    private String vacation;

    private String postgrpType;

    private String isauth;

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getDeptcode() {
        return deptcode;
    }

    public void setDeptcode(String deptcode) {
        this.deptcode = deptcode;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getIfael() {
        return ifael;
    }

    public void setIfael(String ifael) {
        this.ifael = ifael;
    }

    public String getVacation() {
        return vacation;
    }

    public void setVacation(String vacation) {
        this.vacation = vacation;
    }

    public String getPostgrpType() {
        return postgrpType;
    }

    public void setPostgrpType(String postgrpType) {
        this.postgrpType = postgrpType;
    }

    public String getIsauth() {
        return isauth;
    }

    public void setIsauth(String isauth) {
        this.isauth = isauth;
    }

}
