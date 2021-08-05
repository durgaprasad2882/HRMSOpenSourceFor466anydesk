package hrms.dao.performanceappraisal;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import hrms.model.common.FileAttribute;
import hrms.model.login.Users;
import hrms.model.parmast.ParAbsenteeBean;
import hrms.model.parmast.ParAchievement;
import hrms.model.parmast.ParDetail;
import hrms.model.parmast.ParMaster;
import hrms.model.parmast.ParOtherDetails;
import hrms.model.parmast.ParSubmitForm;
import java.text.ParseException;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

public interface PARBrowserDAO {

    public List getPARList(String fiscalyear, String empid);

    public List getPARList(String empid);

    public int savePAR(int pageno, ParMaster parmaster, ParOtherDetails parOtherDetails, String filepath);

    public List getAbsenteeList(String empid, int parid);

    public void saveAbsentee(ParAbsenteeBean parabsentee);

    public List getAchievementList(String empid, int parid);

    public void saveAchievement(String empid, ParAchievement parachievement, String filepath);

    public ParMaster getAppraiseInfo(String empid, int parid);

    public ParOtherDetails getOtherDetails(String empid, int parid);

    public List getAbsenceCauseList();

    public List getLeaveTypeList();

    public List getTrainingypeList();

    public String sendPar(ParSubmitForm pf) throws Exception;

    public int getmaxhierachy(int parid, int parstatus);

    public ParAbsenteeBean getAbsenteeInfo(String empid, int pabid);

    public ParAchievement getAchievementInfo(String empid, int pacid);

    public void deleteAbsentee(String empid, int pabid);

    public void deleteAchievement(String empid, int pacid, String fiscalyear, String filepath);

    public int deleteAchievementAttachment(String empid, int pacid, int attid, String fiscalyear, String filepath);

    public boolean isDuplicatePARPeriod(String empid, String parfrmdt, String partodt, String fiscalyear, String parid);

    public ParDetail getPARDetails(String empid, int parid, int taskid, String authType);

    public int getTaskid(String empid, int parid);

    //public Font getDesired_PDF_Font(int fontsize,boolean isBold,boolean isUnderline) throws Exception;
    public List getPARGradeList();

    public void saveAndForwardPAR(ParDetail pdetail, String empid, String forwardbtn);

    public void viewPDFfunc(Document document, ParDetail paf, String empid, String filepath) throws Exception;

    public int getParid(String empid, int taskid);

    public int saveAcceptingAuthRemarksFromCombo(String empid, int parid, int taskid, String remarks);

    public List getNRCReasonList();

    public String getNRCAttachedFileName(String empid, int parid);

    public ParDetail getNRCDetails(String empid, int parid);

    public void viewNRCPDFfunc(Document document, ParDetail paf, String empid);

    public FileAttribute downloadachievementattachment(String filepath, int attid, String fiscalyear) throws Exception;

    public FileAttribute downloadNRCAttachment(int parid, String fiscalyear, String filepath) throws Exception;

    public String revertPAR(String loginempid, ParDetail parDetail);

    public ParSubmitForm getAuthorityInfo(int parid);

    public String[] getRevertReason(int parid, int taskid);

    public String isFiscalYearClosed(String fyear);
	
	public String isAuthRemarksClosed(String fyear);
	
    public void deleteNRC(int parid, String empId);

    public int deletePAR(String parid, String empid);

    public String isAchievementDataPresent(int parid);

    public String isOtherDetailsPresent(int parid);
    
    public boolean isPARReverted(int parid,String empid);
	
	public List getPARReport(String fiscalyear);
	
	public boolean isAuthorizedtoDownloadPAR(String loggedinEmpid,int parid);
}
