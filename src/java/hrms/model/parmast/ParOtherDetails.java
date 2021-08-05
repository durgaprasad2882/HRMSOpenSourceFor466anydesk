package hrms.model.parmast;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.GenericGenerator;

public class ParOtherDetails {
    
    private int paptid;
    
    private String spc;
    
    private String empid;
    
    private String spcinperiod;
    
    private Date submittedon;
    
    private String place;
    
    private String selfappraisal;
    
    private String hinderreason;
    
    private String specialcontribution;
    
    private int pageno = 0;
    
    private int hidparid;
    
    private String hidpaptid;
    
    public int getPaptid() {
        return paptid;
    }

    public void setPaptid(int paptid) {
        this.paptid = paptid;
    }

    public String getSpc() {
        return spc;
    }

    public void setSpc(String spc) {
        this.spc = spc;
    }

    public String getEmpid() {
        return empid;
    }

    public void setEmpid(String empid) {
        this.empid = empid;
    }

    public String getSpcinperiod() {
        return spcinperiod;
    }

    public void setSpcinperiod(String spcinperiod) {
        this.spcinperiod = spcinperiod;
    }

    public Date getSubmittedon() {
        return submittedon;
    }

    public void setSubmittedon(Date submittedon) {
        this.submittedon = submittedon;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getSelfappraisal() {
        return selfappraisal;
    }

    public void setSelfappraisal(String selfappraisal) {
        this.selfappraisal = selfappraisal;
    }

    public String getHinderreason() {
        return hinderreason;
    }

    public void setHinderreason(String hinderreason) {
        this.hinderreason = hinderreason;
    }

    public String getSpecialcontribution() {
        return specialcontribution;
    }

    public void setSpecialcontribution(String specialcontribution) {
        this.specialcontribution = specialcontribution;
    }

    public int getPageno() {
        return pageno;
    }

    public void setPageno(int pageno) {
        this.pageno = pageno;
    }

    public String getHidpaptid() {
        return hidpaptid;
    }

    public void setHidpaptid(String hidpaptid) {
        this.hidpaptid = hidpaptid;
    }

    public int getHidparid() {
        return hidparid;
    }

    public void setHidparid(int hidparid) {
        this.hidparid = hidparid;
    }
}   
