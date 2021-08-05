package hrms.model.parmast;

import java.util.Date;
import org.springframework.web.multipart.MultipartFile;

import hrms.model.master.SubstantivePost;

public class ParMaster {

    private int parid;

    private String empid;

    private String fiscalyear;

    private Date periodfrom = null;

    private Date periodto = null;

    private int parstatus;

    private int taskid = 0;

    private String postGrp;

    private String isauthSet;

    private int refid;

    private String headqtr;

    private String remarks;

    private String nrcReason;

    private String isadverse;

    private int pageno = 0;

    private String cadreCode = null;

    private String cadreName = null;

    private String hidparid = null;

    private String spc = null;

    private String spn = null;

    private String offCode = null;

    private String offname = null;

    private MultipartFile nrcattchfile;

    private String isClosed = null;

    private String authRemarksClosed = null;

    private SubstantivePost substantivePost;

    public int getParid() {
        return parid;
    }

    public void setParid(int parid) {
        this.parid = parid;
    }

    public String getEmpid() {
        return empid;
    }

    public void setEmpid(String empid) {
        this.empid = empid;
    }

    public String getFiscalyear() {
        return fiscalyear;
    }

    public void setFiscalyear(String fiscalyear) {
        this.fiscalyear = fiscalyear;
    }

    public Date getPeriodfrom() {
        return periodfrom;
    }

    public void setPeriodfrom(Date periodfrom) {
        this.periodfrom = periodfrom;
    }

    public Date getPeriodto() {
        return periodto;
    }

    public void setPeriodto(Date periodto) {
        this.periodto = periodto;
    }

    public int getParstatus() {
        return parstatus;
    }

    public void setParstatus(int parstatus) {
        this.parstatus = parstatus;
    }

    public int getTaskid() {
        return taskid;
    }

    public void setTaskid(int taskid) {
        this.taskid = taskid;
    }

    public String getPostGrp() {
        return postGrp;
    }

    public void setPostGrp(String postGrp) {
        this.postGrp = postGrp;
    }

    public String getIsauthSet() {
        return isauthSet;
    }

    public void setIsauthSet(String isauthSet) {
        this.isauthSet = isauthSet;
    }

    public int getRefid() {
        return refid;
    }

    public void setRefid(int refid) {
        this.refid = refid;
    }

    public String getHeadqtr() {
        return headqtr;
    }

    public void setHeadqtr(String headqtr) {
        this.headqtr = headqtr;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getNrcReason() {
        return nrcReason;
    }

    public void setNrcReason(String nrcReason) {
        this.nrcReason = nrcReason;
    }

    public String getIsadverse() {
        return isadverse;
    }

    public void setIsadverse(String isadverse) {
        this.isadverse = isadverse;
    }

    public int getPageno() {
        return pageno;
    }

    public void setPageno(int pageno) {
        this.pageno = pageno;
    }

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

    public String getHidparid() {
        return hidparid;
    }

    public void setHidparid(String hidparid) {
        this.hidparid = hidparid;
    }

    public String getSpc() {
        return spc;
    }

    public void setSpc(String spc) {
        this.spc = spc;
    }

    public String getSpn() {
        return spn;
    }

    public void setSpn(String spn) {
        this.spn = spn;
    }

    public String getOffCode() {
        return offCode;
    }

    public void setOffCode(String offCode) {
        this.offCode = offCode;
    }

    public String getOffname() {
        return offname;
    }

    public void setOffname(String offname) {
        this.offname = offname;
    }

    public MultipartFile getNrcattchfile() {
        return nrcattchfile;
    }

    public void setNrcattchfile(MultipartFile nrcattchfile) {
        this.nrcattchfile = nrcattchfile;
    }

    public String getIsClosed() {
        return isClosed;
    }

    public void setIsClosed(String isClosed) {
        this.isClosed = isClosed;
    }

    public String getAuthRemarksClosed() {
        return authRemarksClosed;
    }

    public void setAuthRemarksClosed(String authRemarksClosed) {
        this.authRemarksClosed = authRemarksClosed;
    }

    public SubstantivePost getSubstantivePost() {
        return substantivePost;
    }

    public void setSubstantivePost(SubstantivePost substantivePost) {
        this.substantivePost = substantivePost;
    }
}
