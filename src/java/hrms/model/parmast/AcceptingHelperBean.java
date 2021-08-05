package hrms.model.parmast;

public class AcceptingHelperBean {

    private int pactid;
    private String isacceptingcompleted;
    private String iscurrentaccepting;
    private String acceptingempid;
    private String acceptingNote;

    private String acceptingauthName = null;

    private String submittedon = null;

    public int getPactid() {
        return pactid;
    }

    public void setPactid(int pactid) {
        this.pactid = pactid;
    }

    public String getIsacceptingcompleted() {
        return isacceptingcompleted;
    }

    public void setIsacceptingcompleted(String isacceptingcompleted) {
        this.isacceptingcompleted = isacceptingcompleted;
    }

    public String getIscurrentaccepting() {
        return iscurrentaccepting;
    }

    public void setIscurrentaccepting(String iscurrentaccepting) {
        this.iscurrentaccepting = iscurrentaccepting;
    }

    public String getAcceptingempid() {
        return acceptingempid;
    }

    public void setAcceptingempid(String acceptingempid) {
        this.acceptingempid = acceptingempid;
    }

    public String getAcceptingNote() {
        return acceptingNote;
    }

    public void setAcceptingNote(String acceptingNote) {
        this.acceptingNote = acceptingNote;
    }

    public String getAcceptingauthName() {
        return acceptingauthName;
    }

    public void setAcceptingauthName(String acceptingauthName) {
        this.acceptingauthName = acceptingauthName;
    }

    public String getSubmittedon() {
        return submittedon;
    }

    public void setSubmittedon(String submittedon) {
        this.submittedon = submittedon;
    }

}
