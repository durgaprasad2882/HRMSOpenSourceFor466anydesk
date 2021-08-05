package hrms.model.deputation;

public class DeputationListForm {
    
    private String deptId = null;
    private String notId = null;
    private String doe = null;
    private String notOrdNo = null;
    private String notOrdDt = null;
    private String notType = null;

    public String getDoe() {
        return doe;
    }

    public void setDoe(String doe) {
        this.doe = doe;
    }

    public String getNotOrdNo() {
        return notOrdNo;
    }

    public void setNotOrdNo(String notOrdNo) {
        this.notOrdNo = notOrdNo;
    }

    public String getNotOrdDt() {
        return notOrdDt;
    }

    public void setNotOrdDt(String notOrdDt) {
        this.notOrdDt = notOrdDt;
    }

    public String getNotType() {
        return notType;
    }

    public void setNotType(String notType) {
        this.notType = notType;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getNotId() {
        return notId;
    }

    public void setNotId(String notId) {
        this.notId = notId;
    }
}
