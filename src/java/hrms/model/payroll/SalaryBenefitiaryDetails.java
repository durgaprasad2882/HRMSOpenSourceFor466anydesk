package hrms.model.payroll;

public class SalaryBenefitiaryDetails {
    private String hrmsgeneratedRefno=null;
    private String hrmsgeneratedRefdate=null;
    private String hrmsid=null;
    private String benefitiaryName=null;
    private String benefitiaryAdd=null;
    private String benefitiaryAcct=null;
    private String receiverBankCode=null;
    private double tranAmt=0;
    private String emailId=null;
    private String mobileno=null;
    private String micrcode=null;
    
    public void setHrmsgeneratedRefno(String hrmsgeneratedRefno) {
        this.hrmsgeneratedRefno = hrmsgeneratedRefno;
    }

    public String getHrmsgeneratedRefno() {
        return hrmsgeneratedRefno;
    }

    public void setHrmsgeneratedRefdate(String hrmsgeneratedRefdate) {
        this.hrmsgeneratedRefdate = hrmsgeneratedRefdate;
    }

    public String getHrmsgeneratedRefdate() {
        return hrmsgeneratedRefdate;
    }

    public void setBenefitiaryName(String benefitiaryName) {
        this.benefitiaryName = benefitiaryName;
    }

    public String getBenefitiaryName() {
        if(benefitiaryName == null){
            benefitiaryName = "";
        }
        return benefitiaryName;
    }

    public void setBenefitiaryAdd(String benefitiaryAdd) {
        this.benefitiaryAdd = benefitiaryAdd;
    }

    public String getBenefitiaryAdd() {
        if(benefitiaryAdd == null){
            benefitiaryAdd = "";
        }
        return benefitiaryAdd;
    }

    public void setBenefitiaryAcct(String benefitiaryAcct) {
        this.benefitiaryAcct = benefitiaryAcct;
    }

    public String getBenefitiaryAcct() {
        if(benefitiaryAcct == null){
            benefitiaryAcct = "";
        }
        return benefitiaryAcct;
    }

    public void setReceiverBankCode(String receiverBankCode) {
        this.receiverBankCode = receiverBankCode;
    }

    public String getReceiverBankCode() {
        if(receiverBankCode == null){
            receiverBankCode = "";
        }
        return receiverBankCode;
    }

    public void setTranAmt(double tranAmt) {
        this.tranAmt = tranAmt;
    }

    public double getTranAmt() {
        return tranAmt;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getEmailId() {
        if(emailId == null){
            emailId = "";
        }
        return emailId;
    }

    public void setMobileno(String mobileno) {
        this.mobileno = mobileno;
    }

    public String getMobileno() {
        if(mobileno == null){
            mobileno = "";
        }
        return mobileno;
    }

    public void setMicrcode(String micrcode) {
        this.micrcode = micrcode;
    }

    public String getMicrcode() {
        if(micrcode == null){
            micrcode = "";
        }
        return micrcode;
    }

    public void setHrmsid(String hrmsid) {
        this.hrmsid = hrmsid;
    }

    public String getHrmsid() {
        return hrmsid;
    }
}
