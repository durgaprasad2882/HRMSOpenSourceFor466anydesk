/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.model.employee;

/**
 *
 * @author Manas Jena
 */
public class PayComponent {
    private int basic;
    private int gp;
    private String ispayrevised;

    public int getBasic() {
        return basic;
    }

    public void setBasic(int basic) {
        this.basic = basic;
    }

    public int getGp() {
        return gp;
    }

    public void setGp(int gp) {
        this.gp = gp;
    }

    public String getIspayrevised() {
        return ispayrevised;
    }

    public void setIspayrevised(String ispayrevised) {
        this.ispayrevised = ispayrevised;
    }
    
}
