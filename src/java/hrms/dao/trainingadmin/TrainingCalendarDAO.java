/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.trainingadmin;

import hrms.common.Message;
import hrms.model.trainingadmin.EmployeeSearch;
import hrms.model.trainingadmin.InstituteForm;
import hrms.model.trainingadmin.NISGTrainingBean;
import hrms.model.trainingadmin.TrainingFacultyForm;
import hrms.model.trainingadmin.TrainingProgramForm;
import hrms.model.trainingadmin.TrainingSponsorForm;
import hrms.model.trainingadmin.TrainingVenueForm;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Manoj PC
 */
public interface TrainingCalendarDAO {
    /* Method Declare */

    public int[] getGovtHolidaysList(int month, int year);

    public boolean isHolidayAdded(int days, int[] noOfDay);

    public boolean isDatePresentInList(int[] holidayList, int date);

    public List getPostListWithName(String offcode);

    public void SaveTrainingProgram(TrainingProgramForm trainingPrograms, String empID, String filePath);

    public String getTrainingProgramList(String programDate, String ownerID);

    public List getTrainingPrograms(String hrmsId, String date);

    public void deleteTrainingProgram(int hrmsId, String empId);

    public TrainingProgramForm getTrainingDetail(int trainingId);

    public void updateTrainingProgram(TrainingProgramForm trainingProgram, String EmpID, String filePath);

    public String getCadreList(String trainingId, String empId);

    public void assignCadreList(String trainingId, String cadreId, String grade, String status, String empId);

    public void addNewFaculty(TrainingFacultyForm trainingFaculty, String empID);

    public List getTrainingProgramList(String ownerId, int page, int rows);

    public List getFacultyList(String ownerId, int page, int rows);

    public void updateTrainingFaculty(TrainingFacultyForm trainingFaculty, String empID);

    public void deleteFaculty(int facultyCode);

    public List getSponsorList(String ownerId);

    public List getSelectboxSponsorList(String ownerId);

    public void addNewSponsor(TrainingSponsorForm trainingSponsor, String empID);

    public void updateTrainingSponsor(TrainingSponsorForm trainingSponsor, String empID);

    public void deleteSponsor(int sponsorCode);

    public List getVenueList(String ownerId);

    public void addNewVenue(TrainingVenueForm trainingVenue, String empID);

    public void updateTrainingVenue(TrainingVenueForm trainingVenue, String empID);

    public void deleteVenue(int venueCode);

    public void downloadDocument(HttpServletResponse response, String filePath, int trainingId);

    public String getAssignedFacultyList(String empId, String trainingId);

    public void assignFacultyList(String trainingId, int facultyCode, String status, String empId);

    public String getAssignedSponsorList(String empId, String trainingId);

    public void assignSponsorList(String trainingId, int sponsorCode, String status, String empId);

    public String getTrainingInstituteList();

    public String getUpcomingTrainingPrograms();

    public int deleteDocumentAttachment(int trainingId, String filepath);

    public String getLeftUpcomingPrograms();

    public InstituteForm getInstituteDetail(String instituteId);

    public int getTotalRowsCount(String tableName, String where);

    public void updateInstituteProfile(InstituteForm instForm, String empID);

    public List getApplyProgramList(String empId, int page, int rows, String type);

    public Message applyTraining(String empid, String trid, String spc, String mobile, String email, String tpids, String trainingOption);

    public TrainingProgramForm getApplyTrainingDetail(int taskId);

    public Message saveTrainingApprove(int taskId, int trainingStatus, String loginempid, String loginSpc, String forwardemp);

    public List getTrainingStatus();

    public List getParticipantList(String empId, String trainingId, int page, int rows);

    public int getDefaultVenue(String ownerId);

    public String getCadreCode(String empId);

    public List getManageTrainingProgramList(String ownerID, int page, int rows);

    public TrainingProgramForm getManageTrainingDetail(int trainingId);

    public List getAppliedParticipantList(String empId, String trainingId, int rows, int page);

    public void saveShortlisted(int empTrainingId);

    public void exportExcel(HttpServletResponse response, String trainingId, String trainingAuth);

    public List getArchiveTrainingProgramList(String ownerId, int page, int rows);

    public String getEmpMobile(String empID);

    public String getEmpEmail(String empID);

    public List getPreviousTrainingList(String empId);

    public boolean isTrainingExist(String empid);

    public String getEmpTrainingOptions(String empId, int trainingId);

    public void updateTrainingOption(int trainingId, String empId, String trainingOption);

    public boolean hasEmpPreviousTraining(String empId, int trainingId);

    public Message savePreviousTraining(String empId, String tpids);

    public Message withdrawTraining(int trainingId, String empId);

    public List getTrainingDeptList();

    public List getTrainingOfficeList(String deptcode);

    public List getPostListTraining(String deptcode, String offcode);

    public List getSearchEmp(EmployeeSearch empSearch);

    public void saveNISGTrainingDetails(NISGTrainingBean ntBean);

    public int getTrainingCount(String empId);

    public List GetNISGParticipants();

    public NISGTrainingBean getNISGParticipantDetail(String participantID);

    public String getActiveTraining();
    
    public List getOptionTrainingList();
    
    public String saveEmpPreviousTraining(String empId, String tpids);
}
