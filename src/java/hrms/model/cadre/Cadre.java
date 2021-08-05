

package hrms.model.cadre;

import java.io.Serializable;

public class Cadre implements Serializable{
    
    private String cadreCode;
    
    private String cadreName;
    
    private String deptCode = null;
    
    public String getCadreCode() {
        return cadreCode;
    }

    public void setCadreCode(String cadreCode) {
        this.cadreCode = cadreCode;
    }

    public String getCadreName() {
        return cadreName;
    }

    public void setCadreName(String cadreName) {
        this.cadreName = cadreName;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }
    
}
