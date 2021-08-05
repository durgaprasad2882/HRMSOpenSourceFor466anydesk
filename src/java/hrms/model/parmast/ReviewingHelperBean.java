package hrms.model.parmast;

public class ReviewingHelperBean {

    private int prtid;
    private String isreviewingcompleted;
    private String reportingempid;
    private String reviewingNote;
    private int sltReviewGrading;
    private String reviewGrading;

    private String iscurrentreviewing = null;
    private String reviewingauthName = null;

    private String submittedon = null;

    public int getPrtid() {
        return prtid;
    }

    public void setPrtid(int prtid) {
        this.prtid = prtid;
    }

    public String getIsreviewingcompleted() {
        return isreviewingcompleted;
    }

    public void setIsreviewingcompleted(String isreviewingcompleted) {
        this.isreviewingcompleted = isreviewingcompleted;
    }

    public String getReportingempid() {
        return reportingempid;
    }

    public void setReportingempid(String reportingempid) {
        this.reportingempid = reportingempid;
    }

    public String getReviewingNote() {
        return reviewingNote;
    }

    public void setReviewingNote(String reviewingNote) {
        this.reviewingNote = reviewingNote;
    }

    public int getSltReviewGrading() {
        return sltReviewGrading;
    }

    public void setSltReviewGrading(int sltReviewGrading) {
        this.sltReviewGrading = sltReviewGrading;
    }

    public String getReviewGrading() {
        return reviewGrading;
    }

    public void setReviewGrading(String reviewGrading) {
        this.reviewGrading = reviewGrading;
    }

    public String getIscurrentreviewing() {
        return iscurrentreviewing;
    }

    public void setIscurrentreviewing(String iscurrentreviewing) {
        this.iscurrentreviewing = iscurrentreviewing;
    }

    public String getReviewingauthName() {
        return reviewingauthName;
    }

    public void setReviewingauthName(String reviewingauthName) {
        this.reviewingauthName = reviewingauthName;
    }

    public String getSubmittedon() {
        return submittedon;
    }

    public void setSubmittedon(String submittedon) {
        this.submittedon = submittedon;
    }

}
