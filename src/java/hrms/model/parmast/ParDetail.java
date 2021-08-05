package hrms.model.parmast;

import java.util.ArrayList;

public class ParDetail {

    private int parid;
    private int taskid;
    private int refid;
    private int parstatus;
    private String applicantempid = null;
    private String applicant = null;
    private String apprisespn = null;
    private String apprisespc = null;
    private String loginId = null;
    private String authNote = null;
    private ArrayList actionTypeArrList = null;
    private String txtSancAuthority = null;
    private String sltActionType = null;
    private String fiscalYear = null;
    private String parPeriodFrom = null;
    private String parPeriodTo = null;
    private ArrayList leaveAbsentee = null;
    private ArrayList achivementList = null;
    private String selfappraisal = null;
    private String specialcontribution = null;
    private String reason = null;
    private String place = null;
    private String dob = null;
    private String submitted_on = null;
    private String empService = null;
    private String empGroup = null;
    private String empDesg = null;
    private String empOffice = null;
    private String isreportingcompleted = null;
    private String reportingempid = null;
    private String reviewingNote = null;
    private String isreviewingcompleted = null;
    private String acceptingNote = null;
    private String isacceptingcompleted = null;
    private String urlName = null;
    private String gradingNote = null;
    private ArrayList gradingArr = null;
    private String sltHeadQuarter = null;

    private int ratingattitude;
    private int ratingcoordination;
    private int ratingresponsibility;
    private int teamworkrating;
    private int ratingcomskill;
    private int ratingitskill;
    private int ratingleadership;
    private int ratinginitiative;
    private int ratingdecisionmaking;
    private int ratequalityofwork;

    private String inadequaciesNote = null;
    private String integrityNote = null;
    private String sltGrading = null;
    private String sltGradingName = null;
    private String reportinggradename = null;
    private String sltReviewGrading = null;
    private ArrayList reportingauth = null;
    private ArrayList reviewingauth = null;
    private ArrayList acceptingauth = null;

    private ArrayList reportingdata = null;
    private ArrayList reviewingdata = null;
    private ArrayList acceptingdata = null;

    private String rdEmpIdReverted = null;
    private String revertremarks = null;
    private String revertdone = null;

    private String sltAcceptingAuthorityRemarks = null;
    private int grading = 0;

    private String auth = "";

    private String isClosedFiscalYearAuthority = null;
    
    private String nrcreason = null;
    private String nrcremarks = null;
    private String nrcattchname = null;
    
    public int getParid() {
        return parid;
    }

    public void setParid(int parid) {
        this.parid = parid;
    }

    public int getTaskid() {
        return taskid;
    }

    public void setTaskid(int taskid) {
        this.taskid = taskid;
    }

    public int getRefid() {
        return refid;
    }

    public void setRefid(int refid) {
        this.refid = refid;
    }

    public int getParstatus() {
        return parstatus;
    }

    public void setParstatus(int parstatus) {
        this.parstatus = parstatus;
    }

    public String getApplicantempid() {
        return applicantempid;
    }

    public void setApplicantempid(String applicantempid) {
        this.applicantempid = applicantempid;
    }

    public String getApplicant() {
        return applicant;
    }

    public void setApplicant(String applicant) {
        this.applicant = applicant;
    }

    public String getApprisespn() {
        return apprisespn;
    }

    public void setApprisespn(String apprisespn) {
        this.apprisespn = apprisespn;
    }

    public String getApprisespc() {
        return apprisespc;
    }

    public void setApprisespc(String apprisespc) {
        this.apprisespc = apprisespc;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getAuthNote() {
        return authNote;
    }

    public void setAuthNote(String authNote) {
        this.authNote = authNote;
    }

    public ArrayList getActionTypeArrList() {
        return actionTypeArrList;
    }

    public void setActionTypeArrList(ArrayList actionTypeArrList) {
        this.actionTypeArrList = actionTypeArrList;
    }

    public String getTxtSancAuthority() {
        return txtSancAuthority;
    }

    public void setTxtSancAuthority(String txtSancAuthority) {
        this.txtSancAuthority = txtSancAuthority;
    }

    public String getSltActionType() {
        return sltActionType;
    }

    public void setSltActionType(String sltActionType) {
        this.sltActionType = sltActionType;
    }

    public String getFiscalYear() {
        return fiscalYear;
    }

    public void setFiscalYear(String fiscalYear) {
        this.fiscalYear = fiscalYear;
    }

    public String getParPeriodFrom() {
        return parPeriodFrom;
    }

    public void setParPeriodFrom(String parPeriodFrom) {
        this.parPeriodFrom = parPeriodFrom;
    }

    public String getParPeriodTo() {
        return parPeriodTo;
    }

    public void setParPeriodTo(String parPeriodTo) {
        this.parPeriodTo = parPeriodTo;
    }

    public ArrayList getLeaveAbsentee() {
        return leaveAbsentee;
    }

    public void setLeaveAbsentee(ArrayList leaveAbsentee) {
        this.leaveAbsentee = leaveAbsentee;
    }

    public ArrayList getAchivementList() {
        return achivementList;
    }

    public void setAchivementList(ArrayList achivementList) {
        this.achivementList = achivementList;
    }

    public String getSelfappraisal() {
        return selfappraisal;
    }

    public void setSelfappraisal(String selfappraisal) {
        this.selfappraisal = selfappraisal;
    }

    public String getSpecialcontribution() {
        return specialcontribution;
    }

    public void setSpecialcontribution(String specialcontribution) {
        this.specialcontribution = specialcontribution;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getSubmitted_on() {
        return submitted_on;
    }

    public void setSubmitted_on(String submitted_on) {
        this.submitted_on = submitted_on;
    }

    public String getEmpService() {
        return empService;
    }

    public void setEmpService(String empService) {
        this.empService = empService;
    }

    public String getEmpGroup() {
        return empGroup;
    }

    public void setEmpGroup(String empGroup) {
        this.empGroup = empGroup;
    }

    public String getEmpDesg() {
        return empDesg;
    }

    public void setEmpDesg(String empDesg) {
        this.empDesg = empDesg;
    }

    public String getEmpOffice() {
        return empOffice;
    }

    public void setEmpOffice(String empOffice) {
        this.empOffice = empOffice;
    }

    public String getIsreportingcompleted() {
        return isreportingcompleted;
    }

    public void setIsreportingcompleted(String isreportingcompleted) {
        this.isreportingcompleted = isreportingcompleted;
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

    public String getIsreviewingcompleted() {
        return isreviewingcompleted;
    }

    public void setIsreviewingcompleted(String isreviewingcompleted) {
        this.isreviewingcompleted = isreviewingcompleted;
    }

    public String getAcceptingNote() {
        return acceptingNote;
    }

    public void setAcceptingNote(String acceptingNote) {
        this.acceptingNote = acceptingNote;
    }

    public String getIsacceptingcompleted() {
        return isacceptingcompleted;
    }

    public void setIsacceptingcompleted(String isacceptingcompleted) {
        this.isacceptingcompleted = isacceptingcompleted;
    }

    public String getUrlName() {
        return urlName;
    }

    public void setUrlName(String urlName) {
        this.urlName = urlName;
    }

    public String getGradingNote() {
        return gradingNote;
    }

    public void setGradingNote(String gradingNote) {
        this.gradingNote = gradingNote;
    }

    public ArrayList getGradingArr() {
        return gradingArr;
    }

    public void setGradingArr(ArrayList gradingArr) {
        this.gradingArr = gradingArr;
    }

    public String getSltHeadQuarter() {
        return sltHeadQuarter;
    }

    public void setSltHeadQuarter(String sltHeadQuarter) {
        this.sltHeadQuarter = sltHeadQuarter;
    }

    public int getRatingattitude() {
        return ratingattitude;
    }

    public void setRatingattitude(int ratingattitude) {
        this.ratingattitude = ratingattitude;
    }

    public int getRatingcoordination() {
        return ratingcoordination;
    }

    public void setRatingcoordination(int ratingcoordination) {
        this.ratingcoordination = ratingcoordination;
    }

    public int getRatingresponsibility() {
        return ratingresponsibility;
    }

    public void setRatingresponsibility(int ratingresponsibility) {
        this.ratingresponsibility = ratingresponsibility;
    }

    public int getTeamworkrating() {
        return teamworkrating;
    }

    public void setTeamworkrating(int teamworkrating) {
        this.teamworkrating = teamworkrating;
    }

    public int getRatingcomskill() {
        return ratingcomskill;
    }

    public void setRatingcomskill(int ratingcomskill) {
        this.ratingcomskill = ratingcomskill;
    }

    public int getRatingitskill() {
        return ratingitskill;
    }

    public void setRatingitskill(int ratingitskill) {
        this.ratingitskill = ratingitskill;
    }

    public int getRatingleadership() {
        return ratingleadership;
    }

    public void setRatingleadership(int ratingleadership) {
        this.ratingleadership = ratingleadership;
    }

    public int getRatinginitiative() {
        return ratinginitiative;
    }

    public void setRatinginitiative(int ratinginitiative) {
        this.ratinginitiative = ratinginitiative;
    }

    public int getRatingdecisionmaking() {
        return ratingdecisionmaking;
    }

    public void setRatingdecisionmaking(int ratingdecisionmaking) {
        this.ratingdecisionmaking = ratingdecisionmaking;
    }

    public int getRatequalityofwork() {
        return ratequalityofwork;
    }

    public void setRatequalityofwork(int ratequalityofwork) {
        this.ratequalityofwork = ratequalityofwork;
    }

    public String getInadequaciesNote() {
        return inadequaciesNote;
    }

    public void setInadequaciesNote(String inadequaciesNote) {
        this.inadequaciesNote = inadequaciesNote;
    }

    public String getIntegrityNote() {
        return integrityNote;
    }

    public void setIntegrityNote(String integrityNote) {
        this.integrityNote = integrityNote;
    }

    public String getSltGrading() {
        return sltGrading;
    }

    public void setSltGrading(String sltGrading) {
        this.sltGrading = sltGrading;
    }

    public String getSltGradingName() {
        return sltGradingName;
    }

    public void setSltGradingName(String sltGradingName) {
        this.sltGradingName = sltGradingName;
    }

    public String getReportinggradename() {
        return reportinggradename;
    }

    public void setReportinggradename(String reportinggradename) {
        this.reportinggradename = reportinggradename;
    }

    public String getSltReviewGrading() {
        return sltReviewGrading;
    }

    public void setSltReviewGrading(String sltReviewGrading) {
        this.sltReviewGrading = sltReviewGrading;
    }

    public ArrayList getReportingauth() {
        return reportingauth;
    }

    public void setReportingauth(ArrayList reportingauth) {
        this.reportingauth = reportingauth;
    }

    public ArrayList getReviewingauth() {
        return reviewingauth;
    }

    public void setReviewingauth(ArrayList reviewingauth) {
        this.reviewingauth = reviewingauth;
    }

    public ArrayList getAcceptingauth() {
        return acceptingauth;
    }

    public void setAcceptingauth(ArrayList acceptingauth) {
        this.acceptingauth = acceptingauth;
    }

    public ArrayList getReportingdata() {
        return reportingdata;
    }

    public void setReportingdata(ArrayList reportingdata) {
        this.reportingdata = reportingdata;
    }

    public ArrayList getReviewingdata() {
        return reviewingdata;
    }

    public void setReviewingdata(ArrayList reviewingdata) {
        this.reviewingdata = reviewingdata;
    }

    public ArrayList getAcceptingdata() {
        return acceptingdata;
    }

    public void setAcceptingdata(ArrayList acceptingdata) {
        this.acceptingdata = acceptingdata;
    }

    public String getRdEmpIdReverted() {
        return rdEmpIdReverted;
    }

    public void setRdEmpIdReverted(String rdEmpIdReverted) {
        this.rdEmpIdReverted = rdEmpIdReverted;
    }

    public String getRevertremarks() {
        return revertremarks;
    }

    public void setRevertremarks(String revertremarks) {
        this.revertremarks = revertremarks;
    }

    public String getRevertdone() {
        return revertdone;
    }

    public void setRevertdone(String revertdone) {
        this.revertdone = revertdone;
    }

    public String getSltAcceptingAuthorityRemarks() {
        return sltAcceptingAuthorityRemarks;
    }

    public void setSltAcceptingAuthorityRemarks(String sltAcceptingAuthorityRemarks) {
        this.sltAcceptingAuthorityRemarks = sltAcceptingAuthorityRemarks;
    }

    public int getGrading() {
        return grading;
    }

    public void setGrading(int grading) {
        this.grading = grading;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getIsClosedFiscalYearAuthority() {
        return isClosedFiscalYearAuthority;
    }

    public void setIsClosedFiscalYearAuthority(String isClosedFiscalYearAuthority) {
        this.isClosedFiscalYearAuthority = isClosedFiscalYearAuthority;
    }
    
    public String getIshideremark() {
        String ishideremark = "Y";
        if (this.applicantempid != null && this.applicantempid.equals(this.loginId)) {
            //ishideremark = "Y";
            if (this.reportingempid != null && this.applicantempid.equals(this.reportingempid)) {
                ishideremark = "N";
            } else {
                ishideremark = "Y";
            }
        } else {
            System.out.println("Parstatus is: "+this.parstatus);
            if(this.parstatus == 9){
                ishideremark = "N";
            }else{
                ishideremark = "N";
            }
        }
        System.out.println("Hide Remark is: "+ishideremark);
        return ishideremark;
    }

    public String getNrcreason() {
        return nrcreason;
    }

    public void setNrcreason(String nrcreason) {
        this.nrcreason = nrcreason;
    }

    public String getNrcremarks() {
        return nrcremarks;
    }

    public void setNrcremarks(String nrcremarks) {
        this.nrcremarks = nrcremarks;
    }

    public String getNrcattchname() {
        return nrcattchname;
    }

    public void setNrcattchname(String nrcattchname) {
        this.nrcattchname = nrcattchname;
    }
}
