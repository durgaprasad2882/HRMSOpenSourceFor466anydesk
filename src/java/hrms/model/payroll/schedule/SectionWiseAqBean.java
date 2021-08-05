/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.model.payroll.schedule;

import java.util.ArrayList;

/**
 *
 * @author Manas
 */
public class SectionWiseAqBean {

    private String sectionname;
    private ArrayList aqlistSectionWise = null;

    public SectionWiseAqBean() {
    }

    public void setAqlistSectionWise(ArrayList aqlistSectionWise) {
        this.aqlistSectionWise = aqlistSectionWise;
    }

    public ArrayList getAqlistSectionWise() {
        return aqlistSectionWise;
    }

    public void setSectionname(String sectionname) {
        this.sectionname = sectionname;
    }

    public String getSectionname() {
        return sectionname;
    }
}
