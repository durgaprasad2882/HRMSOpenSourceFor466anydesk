/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.employee;

import hrms.common.CommonFunctions;
import hrms.common.DataBaseFunctions;
import hrms.common.Numtowordconvertion;
import hrms.model.empinfo.EmployeeMessage;
import hrms.model.empinfo.SearchEmployee;
import hrms.model.employee.Address;
import hrms.model.employee.Education;
import hrms.model.employee.Employee;
import hrms.model.employee.EmployeePayProfile;
import hrms.model.employee.FamilyRelation;
import hrms.model.employee.IdentityInfo;
import hrms.model.employee.Language;
import hrms.model.employee.Pensioner;
import hrms.model.employee.Punishment;
import hrms.model.employee.Reward;
import hrms.model.employee.Training;
import hrms.model.employee.TransferJoining;
import hrms.model.loan.Loan;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Manas Jena
 */
public class EmployeeDAOImpl implements EmployeeDAO {

    @Resource(name = "dataSource")
    protected DataSource dataSource;
    
    protected String uploadPath;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }

    private String formatDate(Date input) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = "";
        try {
            formattedDate = sdf.format(input);
        } catch (Exception exp) {
            formattedDate = "";
        }
        return formattedDate;
    }

    public Training[] getEmployeeTraining(String empid, String inputdate) {
        List<Training> traininglist = new ArrayList<>();
        String SQL = "SELECT TITLE,S_DATE,C_DATE,PLACE,NOTE FROM EMP_TRAIN WHERE EMP_ID=? AND DOE > ?";
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            conn = dataSource.getConnection();
            statement = conn.prepareStatement(SQL);
            statement.setString(1, empid);
            statement.setDate(2, new java.sql.Date(CommonFunctions.getDateFromString(inputdate, "yyyy").getTime()));
            result = statement.executeQuery();
            while (result.next()) {
                Training training = new Training();
                training.setTitle(result.getString("TITLE"));
                training.setSdate(result.getString("S_DATE"));
                training.setCdate(result.getString("C_DATE"));
                training.setPlace(result.getString("PLACE"));
                training.setNote(result.getString("NOTE"));
                traininglist.add(training);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(result, statement);
            DataBaseFunctions.closeSqlObjects(conn);
        }
        Training trainingarray[] = traininglist.toArray(new Training[traininglist.size()]);
        return trainingarray;
    }

    public Punishment[] getEmployeePunishment(String empid, String inputdate) {
        List<Punishment> punlist = new ArrayList<>();
        String SQL = "select DUR_FROM,DUR_TO,DAYS,REASON,IF_PAY_HELDUP,ORDNO,ORDDT,NOTE FROM "
                + "(SELECT * FROM EMP_NOTIFICATION WHERE EMP_ID = ? AND NOT_TYPE='ADM_ACTION' AND  DOE > ?) EMP_NOTIFICATION "
                + "INNER JOIN EMP_AD_ACTION ON EMP_AD_ACTION.NOT_ID=EMP_NOTIFICATION.NOT_ID";
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            conn = dataSource.getConnection();
            statement = conn.prepareStatement(SQL);
            statement.setString(1, empid);
            statement.setDate(2, new java.sql.Date(CommonFunctions.getDateFromString(inputdate, "yyyy").getTime()));
            result = statement.executeQuery();
            while (result.next()) {
                Punishment punishment = new Punishment();
                punishment.setNote(result.getString("note"));
                punishment.setOrdno(result.getString("ORDNO"));
                punishment.setOrddate(result.getString("ORDDT"));
                punishment.setDays(result.getString("DAYS"));
                punishment.setDurfrom(result.getString("DUR_FROM"));
                punishment.setDurto(result.getString("DUR_TO"));
                punishment.setIfpayheldup(result.getString("IF_PAY_HELDUP"));
                punishment.setReason(result.getString("REASON"));
                punlist.add(punishment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(result, statement);
            DataBaseFunctions.closeSqlObjects(conn);
        }
        Punishment punarray[] = punlist.toArray(new Punishment[punlist.size()]);
        return punarray;
    }

    public Education[] getEmployeeEducation(String empid, String inputdate) {
        List<Education> edulist = new ArrayList<>();
        String SQL = "SELECT QUALIFICATION,FACULTY,YOP,DEGREE,SUBJECTS,INSTITUTE, BOARD_UNIV, QFN_ID FROM EMP_QUALIFICATION WHERE EMP_ID=? AND DOE > ? ORDER BY QFN_ID;";
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            conn = dataSource.getConnection();
            statement = conn.prepareStatement(SQL);
            statement.setString(1, empid);
            statement.setDate(2, new java.sql.Date(CommonFunctions.getDateFromString(inputdate, "yyyy").getTime()));
            result = statement.executeQuery();
            while (result.next()) {
                Education education = new Education();
                education.setQualification(result.getString("QUALIFICATION"));
                education.setFaculty(result.getString("FACULTY"));
                education.setYearofpass(result.getString("YOP"));
                education.setDegree(result.getString("DEGREE"));
                education.setSubject(result.getString("SUBJECTS"));
                education.setInstitute(result.getString("INSTITUTE"));
                education.setBoard(result.getString("BOARD_UNIV"));
                education.setQfn_id(result.getInt("QFN_ID"));
                edulist.add(education);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(result, statement);
            DataBaseFunctions.closeSqlObjects(conn);
        }
        Education eduarray[] = edulist.toArray(new Education[edulist.size()]);
        return eduarray;
    }

    public Language[] getLanguageKnown(String empid) {
        List<Language> languagelist = new ArrayList<>();
        String SQL = "SELECT LANGUAGE, IF_READ, IF_WRITE, IF_SPEAK, IF_MLANG FROM EMP_LANGUAGE WHERE EMP_ID=?";
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            conn = dataSource.getConnection();
            statement = conn.prepareStatement(SQL);
            statement.setString(1, empid);
            result = statement.executeQuery();
            while (result.next()) {
                Language language = new Language();
                language.setLanguage(result.getString("LANGUAGE"));
                language.setIfread(result.getString("IF_READ"));
                language.setIfspeak(result.getString("IF_SPEAK"));
                language.setIfwrite(result.getString("IF_WRITE"));
                language.setIfmlang(result.getString("IF_MLANG"));
                languagelist.add(language);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(result, statement);
            DataBaseFunctions.closeSqlObjects(conn);
        }
        Language languagearray[] = languagelist.toArray(new Language[languagelist.size()]);
        return languagearray;
    }

    public FamilyRelation[] getEmployeeFamily(String empid) {
        String fatherName = "";
        List<FamilyRelation> frlist = new ArrayList<>();
        String SQL = "SELECT int_rel_id,EMPR.relation, IF_ALIVE,ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME,L_NAME], ' ') EMPNAME, INITIALS, F_NAME, M_NAME, L_NAME, EMPLOYEE_EMP_ID,gender,dob,mobile,EMPR.marital_status,int_marital_status_id,"
                + "                    pension_date,share_pct,ifsc_code,bank_acc_no,branch_code,is_pwd,pwd_type,is_minor,minor_guardian, "
                + "                              remarks,address,priority_lvl,is_nominee FROM EMP_RELATION EMPR "
                + "                              LEFT OUTER JOIN g_marital gmarital ON EMPR.marital_status=gmarital.m_status "
                + "                              LEFT OUTER JOIN g_relation rel ON EMPR.relation=rel.relation "
                + "                              WHERE EMP_ID=? ";
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            conn = dataSource.getConnection();
            statement = conn.prepareStatement(SQL);
            statement.setString(1, empid);
            result = statement.executeQuery();
            while (result.next()) {
                FamilyRelation familyRelation = new FamilyRelation();
                familyRelation.setRelation(result.getString("RELATION"));
                familyRelation.setIfalive(result.getString("IF_ALIVE"));
                familyRelation.setInitials(result.getString("INITIALS"));
                familyRelation.setFname(result.getString("F_NAME"));
                familyRelation.setMname(result.getString("M_NAME"));
                familyRelation.setLname(result.getString("L_NAME"));
                familyRelation.setRelname(result.getString("EMPNAME"));
                familyRelation.setEmployeeempid(result.getString("EMPLOYEE_EMP_ID"));
                familyRelation.setGender(result.getString("gender"));
                familyRelation.setDob(formatDate(result.getDate("dob")));
                familyRelation.setMobile(result.getString("mobile"));
                familyRelation.setMarital_statuS(result.getString("int_marital_status_id"));
                familyRelation.setPension_date(formatDate(result.getDate("pension_date")));
                familyRelation.setShare_pct(result.getString("share_pct"));
                familyRelation.setIfsc_code(result.getString("ifsc_code"));
                familyRelation.setBank_acc_no(result.getString("bank_acc_no"));
                familyRelation.setBranch_code(result.getString("branch_code"));
                familyRelation.setIs_pwd(result.getString("is_pwd"));
                familyRelation.setPwd_type(result.getString("pwd_type"));
                familyRelation.setIs_minor(result.getString("is_minor"));
                familyRelation.setMinor_guardian(result.getString("minor_guardian"));
                familyRelation.setRemarks(result.getString("remarks"));
                familyRelation.setAddress(result.getString("address"));
                familyRelation.setPriority_lvl(result.getString("priority_lvl"));
                familyRelation.setIs_Nominee(result.getString("is_nominee"));

                if (result.getString("INITIALS") != null && !result.getString("INITIALS").equals("")) {
                    fatherName = fatherName + " " + result.getString("INITIALS");
                }
                if (result.getString("F_NAME") != null && !result.getString("F_NAME").equals("")) {
                    fatherName = fatherName + " " + result.getString("F_NAME");
                }
                if (result.getString("M_NAME") != null && !result.getString("M_NAME").equals("")) {
                    fatherName = fatherName + " " + result.getString("M_NAME");
                }
                if (result.getString("L_NAME") != null && !result.getString("L_NAME").equals("")) {
                    fatherName = fatherName + " " + result.getString("L_NAME");
                }

                familyRelation.setFatherName(fatherName);
                familyRelation.setEmployeeempid(result.getString("EMPLOYEE_EMP_ID"));
                frlist.add(familyRelation);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(result, statement);
            DataBaseFunctions.closeSqlObjects(conn);
        }
        FamilyRelation frarray[] = frlist.toArray(new FamilyRelation[frlist.size()]);
        return frarray;
    }

    @Override
    public Loan[] getEmployeeLoan(String empid) {
        List<Loan> frlist = new ArrayList<>();
        String SQL = "SELECT loanid,loan_tp,amount,lr_type,tr_code,vch_no,vch_date,p_org_amt,  "
                + "p_cum_recovered,p_recovered,i_org_amt,i_tot_no_inst,i_instl_amt,i_last_instl_no,i_last_pmt_mon,i_cum_recovered,i_recovered, "
                + "new_dedn,now_dedn,p_instl_no,i_instl_no,stop_loan FROM EMP_LOAN_SANC WHERE EMP_ID=? AND P_RECOVERED='' and I_RECOVERED=''";
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            conn = dataSource.getConnection();
            statement = conn.prepareStatement(SQL);
            statement.setString(1, empid);
            result = statement.executeQuery();
            Loan l = null;
            while (result.next()) {
                l = new Loan();
                l.setTreasuryname(result.getString("tr_code"));
                l.setVoucherNo(result.getString("vch_no"));
                l.setVoucherDate(result.getDate("vch_date"));
                l.setTxtamount(result.getInt("p_org_amt"));
                l.setSltloan(result.getString("loan_tp"));
                frlist.add(l);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(result, statement);
            DataBaseFunctions.closeSqlObjects(conn);
        }
        Loan laoanarray[] = frlist.toArray(new Loan[frlist.size()]);
        return laoanarray;
    }

    public Reward[] getEmployeeReward(String empid, String inputdate) {
        List<Reward> rwlist = new ArrayList<>();
        String SQL = "SELECT rwd_lvl,note,ORDNO,ORDDT,OFF_CODE FROM "
                + "(SELECT NOTE,NOT_ID,DOE,ORDNO,ORDDT,OFF_CODE FROM EMP_NOTIFICATION WHERE EMP_ID=? AND NOT_TYPE='REWARD' AND DOE > ?)EMP_NOTIFICATION "
                + "INNER JOIN EMP_REWARD ON EMP_REWARD.NOT_ID=EMP_NOTIFICATION.NOT_ID";
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            conn = dataSource.getConnection();
            statement = conn.prepareStatement(SQL);
            statement.setString(1, empid);
            statement.setDate(2, new java.sql.Date(CommonFunctions.getDateFromString(inputdate, "yyyy").getTime()));
            result = statement.executeQuery();
            while (result.next()) {
                Reward reward = new Reward();
                reward.setRwdlvl(result.getString("rwd_lvl"));
                reward.setNote(result.getString("note"));
                reward.setOrdno(result.getString("ORDNO"));
                reward.setOrddate(result.getString("ORDDT"));
                reward.setOffcode(result.getString("OFF_CODE"));
                rwlist.add(reward);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(result, statement);
            DataBaseFunctions.closeSqlObjects(conn);
        }
        Reward rwarray[] = rwlist.toArray(new Reward[rwlist.size()]);
        return rwarray;
    }

    public String[] getUpdatedEmpList(String input, String off_code) {
        List<String> emplist = new ArrayList<>();
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        String SQL = "SELECT EMP_ID FROM EMP_MAST WHERE CUR_OFF_CODE = ? AND IS_REGULAR='Y' AND DEP_CODE = '02' AND LAST_UPDATED_DATE > ?";
        try {
            conn = dataSource.getConnection();
            statement = conn.prepareStatement(SQL);
            statement.setString(1, off_code);
            statement.setDate(2, new java.sql.Date(CommonFunctions.getDateFromString(input, "yyyy").getTime()));
            result = statement.executeQuery();
            while (result.next()) {
                emplist.add(result.getString("EMP_ID"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(result, statement);
            DataBaseFunctions.closeSqlObjects(conn);
        }
        String employees[] = emplist.toArray(new String[emplist.size()]);
        return employees;
    }

    public TransferJoining[] getEmployeeTransferAndJoining(String empid, String input) {
        List<TransferJoining> rjlist = new ArrayList<>();
        String SQL = "SELECT EJ.DOE, EJ.SPC, EJ.NOT_TYPE, EJ.JOIN_DATE, EJ.SPN, EMP_NOTIFICATION.ORDNO,EMP_NOTIFICATION.ORDDT,EMP_TRANSFER.OFF_CODE,RLV_DATE,EMP_RELIEVE.SPC AS RLV_FROM_SPC,GETSPN(EMP_RELIEVE.SPC) AS RLV_FROM_SPN FROM "
                + "(SELECT NOT_ID,DOE,SPC,NOT_TYPE,JOIN_DATE,GETSPN(SPC) SPN,EMP_ID FROM EMP_JOIN WHERE EMP_ID=? AND DOE > ?)EJ "
                + "INNER JOIN EMP_NOTIFICATION ON EJ.NOT_ID = EMP_NOTIFICATION.NOT_ID "
                + "LEFT OUTER JOIN EMP_RELIEVE ON EMP_NOTIFICATION.NOT_ID = EMP_RELIEVE.NOT_ID "
                + "LEFT OUTER JOIN EMP_TRANSFER ON EMP_NOTIFICATION.NOT_ID = EMP_TRANSFER.NOT_ID ORDER BY JOIN_DATE";

        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            conn = dataSource.getConnection();
            statement = conn.prepareStatement(SQL);
            statement.setString(1, empid);
            statement.setDate(2, new java.sql.Date(CommonFunctions.getDateFromString(input, "yyyy").getTime()));
            result = statement.executeQuery();
            while (result.next()) {
                TransferJoining tj = new TransferJoining();
                tj.setDateofentry(result.getString("DOE"));
                tj.setTranstype(result.getString("NOT_TYPE"));
                tj.setJoinspc(result.getString("SPC"));
                tj.setJoinspn(result.getString("SPN"));
                tj.setJoindate(result.getString("JOIN_DATE"));
                tj.setOrderno(result.getString("ORDNO"));
                tj.setOrderdate(result.getString("ORDDT"));
                tj.setJoinofficecode(result.getString("OFF_CODE"));
                tj.setRelievedate(result.getString("RLV_DATE"));
                tj.setRelievefromspc(result.getString("RLV_FROM_SPC"));
                tj.setRelievefromspn(result.getString("RLV_FROM_SPN"));
                rjlist.add(tj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(result, statement);
            DataBaseFunctions.closeSqlObjects(conn);
        }
        TransferJoining rjarray[] = rjlist.toArray(new TransferJoining[rjlist.size()]);
        return rjarray;
    }

    @Override
    public Employee getEmployeeProfile(String empid) {
        Employee employee = new Employee();
        String fullName = "";
        employee.setEmpid(empid);

        String SQL = "SELECT GPF_NO,F_NAME,M_NAME,L_NAME,GENDER,M_STATUS,EMP_MAST.CATEGORY,CUR_BASIC_SALARY,HEIGHT,DOB,JOINDATE_OF_GOO,DOS,CUR_SPC, G_SPC.SPN,DEPARTMENT_NAME, G_SPC.DEPT_CODE,GPC,POST,CUR_OFF_CODE,OFF_NAME,BL_GRP,RELIGION,PRM_DIST_CODE,PRM_PS_CODE,PH_CODE, ID_MARK, MOBILE,PRM_TPHONE, CURR_POST_DOJ, EMP_MAST.GP, CUR_SALARY, CUR_CADRE_CODE, POST_GRP_TYPE, FIELD_OFF_CODE, EMAIL_ID, CHEST, WEIGHT, LEFT_VISION, RIGHT_VISION, BRASS_NO,DOE_GOV,HOME_TOWN,if_employed_res,if_employed_rehab FROM "
                + "(SELECT GPF_NO,F_NAME,M_NAME,L_NAME,GENDER,M_STATUS,CATEGORY,CUR_BASIC_SALARY,HEIGHT,DOB,JOINDATE_OF_GOO,DOS,CUR_SPC,CUR_OFF_CODE,BL_GRP,RELIGION,PRM_DIST_CODE,PRM_PS_CODE,PH_CODE, ID_MARK, MOBILE,PRM_TPHONE, CURR_POST_DOJ, GP, CUR_SALARY, CUR_CADRE_CODE, POST_GRP_TYPE, FIELD_OFF_CODE, EMAIL_ID, CHEST, WEIGHT, LEFT_VISION, RIGHT_VISION, BRASS_NO,DOE_GOV,HOME_TOWN,if_employed_res,if_employed_rehab FROM EMP_MAST WHERE EMP_ID=?)EMP_MAST "
                + "LEFT OUTER JOIN G_OFFICE ON EMP_MAST.CUR_OFF_CODE = G_OFFICE.OFF_CODE "
                + "LEFT OUTER JOIN G_SPC ON EMP_MAST.CUR_SPC = G_SPC.SPC "
                + "LEFT OUTER JOIN G_POST ON G_SPC.GPC = G_POST.POST_CODE "
                + "LEFT OUTER JOIN G_DEPARTMENT ON G_DEPARTMENT.DEPARTMENT_CODE = G_SPC.DEPT_CODE ";
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            conn = dataSource.getConnection();
            statement = conn.prepareStatement(SQL);
            statement.setString(1, empid);
            result = statement.executeQuery();
            if (result.next()) {
                if (result.getString("F_NAME") != null && !result.getString("F_NAME").equals("")) {
                    fullName = fullName + " " + result.getString("F_NAME");
                }
                if (result.getString("M_NAME") != null && !result.getString("M_NAME").equals("")) {
                    fullName = fullName + " " + result.getString("M_NAME");
                }
                if (result.getString("L_NAME") != null && !result.getString("L_NAME").equals("")) {
                    fullName = fullName + " " + result.getString("L_NAME");
                }
                employee.setEmpName(fullName);
                employee.setFname(result.getString("F_NAME"));
                employee.setMname(result.getString("M_NAME"));
                employee.setLname(result.getString("L_NAME"));
                employee.setGender(result.getString("GENDER"));
                employee.setMarital(result.getString("M_STATUS"));
                employee.setCategory(result.getString("CATEGORY"));
                employee.setHeight(result.getInt("HEIGHT"));
                employee.setGpfno(result.getString("GPF_NO"));
                employee.setBasic(result.getInt("CUR_BASIC_SALARY"));
                employee.setDob(formatDate(result.getDate("DOB")));
                int ymd[] = Numtowordconvertion.getDateParts(result.getDate("DOB").toString());
                employee.setDobText(Numtowordconvertion.getFormattedDOB(ymd[0], ymd[1], ymd[2]));
                int yjoindata[] = Numtowordconvertion.getDateParts(result.getDate("JOINDATE_OF_GOO").toString());
                employee.setJoinDateText(Numtowordconvertion.getFormattedDOB(yjoindata[0], yjoindata[1], yjoindata[2]));
                employee.setJoindategoo(formatDate(result.getDate("JOINDATE_OF_GOO")));
                employee.setDor(formatDate(result.getDate("DOS")));
                employee.setDepartment(result.getString("DEPARTMENT_NAME"));
                employee.setDeptcode(result.getString("DEPT_CODE"));
                employee.setPost(result.getString("POST"));
                employee.setPostcode(result.getString("GPC"));
                employee.setSpn(result.getString("SPN"));
                employee.setSpc(result.getString("CUR_SPC"));
                employee.setOffice(result.getString("OFF_NAME"));
                employee.setOfficecode(result.getString("CUR_OFF_CODE"));
                employee.setBloodgrp(result.getString("BL_GRP"));
                employee.setReligion(result.getString("RELIGION"));
                employee.setPermanentdist(result.getString("PRM_DIST_CODE"));
                employee.setPermanentps(result.getString("PRM_PS_CODE"));
                employee.setPhyhandicapt(result.getString("PH_CODE"));
                employee.setIdmark(result.getString("ID_MARK"));
                employee.setMobile(result.getString("MOBILE"));
                employee.setPrmTelNo(result.getString("PRM_TPHONE"));
                employee.setDateOfCurPosting(result.getDate("CURR_POST_DOJ"));
                employee.setGp(result.getInt("GP"));
                employee.setPayScale(result.getString("CUR_SALARY"));
                employee.setCadreCode(result.getString("CUR_CADRE_CODE"));
                employee.setPostGrpType(result.getString("POST_GRP_TYPE"));
                employee.setFieldOffCode(result.getString("FIELD_OFF_CODE"));
                employee.setEmail(result.getString("EMAIL_ID"));
                employee.setChest(result.getString("CHEST"));
                employee.setWeight(result.getString("WEIGHT"));
                employee.setLeftvision(result.getString("LEFT_VISION"));
                employee.setRightvision(result.getString("RIGHT_VISION"));
                employee.setBrassno(result.getString("BRASS_NO"));
                employee.setDoeGov(formatDate(result.getDate("DOE_GOV")));
                int ydoe[] = Numtowordconvertion.getDateParts(result.getDate("DOE_GOV").toString());
                employee.setEntryGovDateText(Numtowordconvertion.getFormattedDOB(ydoe[0], ydoe[1], ydoe[2]));
                employee.setHomeTown(result.getString("HOME_TOWN"));
                employee.setIfReservation(result.getString("if_employed_res"));
                employee.setIfRehabiltation(result.getString("if_employed_rehab"));
                return employee;
            }
        } catch (Exception e) {
            employee.setFname(e.getMessage());
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(result, statement);
            DataBaseFunctions.closeSqlObjects(conn);
        }
        return employee;
    }

    public Employee getEmployee(String spc) {
        Employee employee = new Employee();
        employee.setSpc(spc);

        String SQL = "SELECT EMP_ID,GPF_NO,F_NAME,M_NAME,L_NAME,GENDER,M_STATUS,EMP_MAST.CATEGORY,CUR_BASIC_SALARY,HEIGHT,DOB,JOINDATE_OF_GOO,DOS,CUR_SPC, G_SPC.SPN,DEPARTMENT_NAME, G_SPC.DEPT_CODE,GPC,POST,CUR_OFF_CODE,OFF_NAME,BL_GRP,RELIGION,PRM_DIST_CODE,PRM_PS_CODE,PH_CODE, ID_MARK, MOBILE,PRM_TPHONE, CURR_POST_DOJ, EMP_MAST.GP, CUR_SALARY, CUR_CADRE_CODE, POST_GRP_TYPE, FIELD_OFF_CODE, EMAIL_ID FROM "
                + "(SELECT EMP_ID,GPF_NO,F_NAME,M_NAME,L_NAME,GENDER,M_STATUS,CATEGORY,CUR_BASIC_SALARY,HEIGHT,DOB,JOINDATE_OF_GOO,DOS,CUR_SPC,CUR_OFF_CODE,BL_GRP,RELIGION,PRM_DIST_CODE,PRM_PS_CODE,PH_CODE, ID_MARK, MOBILE,PRM_TPHONE, CURR_POST_DOJ, GP, CUR_SALARY, CUR_CADRE_CODE, POST_GRP_TYPE, FIELD_OFF_CODE, EMAIL_ID FROM EMP_MAST WHERE CUR_SPC=?)EMP_MAST "
                + "LEFT OUTER JOIN G_OFFICE ON EMP_MAST.CUR_OFF_CODE = G_OFFICE.OFF_CODE "
                + "LEFT OUTER JOIN G_SPC ON EMP_MAST.CUR_SPC = G_SPC.SPC "
                + "LEFT OUTER JOIN G_POST ON G_SPC.GPC = G_POST.POST_CODE "
                + "LEFT OUTER JOIN G_DEPARTMENT ON G_DEPARTMENT.DEPARTMENT_CODE = G_SPC.DEPT_CODE ";
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            conn = dataSource.getConnection();
            statement = conn.prepareStatement(SQL);
            statement.setString(1, spc);
            result = statement.executeQuery();
            if (result.next()) {
                employee.setEmpid(result.getString("EMP_ID"));
                employee.setFname(result.getString("F_NAME"));
                employee.setMname(result.getString("M_NAME"));
                employee.setLname(result.getString("L_NAME"));
                employee.setGender(result.getString("GENDER"));
                employee.setMarital(result.getString("M_STATUS"));
                employee.setCategory(result.getString("CATEGORY"));
                employee.setHeight(result.getInt("HEIGHT"));
                employee.setGpfno(result.getString("GPF_NO"));
                employee.setBasic(result.getInt("CUR_BASIC_SALARY"));
                employee.setDob(formatDate(result.getDate("DOB")));
                employee.setJoindategoo(formatDate(result.getDate("JOINDATE_OF_GOO")));
                employee.setDor(formatDate(result.getDate("DOS")));
                employee.setDepartment(result.getString("DEPARTMENT_NAME"));
                employee.setDeptcode(result.getString("DEPT_CODE"));
                employee.setPost(result.getString("POST"));
                employee.setPostcode(result.getString("GPC"));
                employee.setSpn(result.getString("SPN"));
                employee.setSpc(result.getString("CUR_SPC"));
                employee.setOffice(result.getString("OFF_NAME"));
                employee.setOfficecode(result.getString("CUR_OFF_CODE"));
                employee.setBloodgrp(result.getString("BL_GRP"));
                employee.setReligion(result.getString("RELIGION"));
                employee.setPermanentdist(result.getString("PRM_DIST_CODE"));
                employee.setPermanentps(result.getString("PRM_PS_CODE"));
                employee.setPhyhandicapt(result.getString("PH_CODE"));
                employee.setIdmark(result.getString("ID_MARK"));
                employee.setMobile(result.getString("MOBILE"));
                employee.setPrmTelNo(result.getString("PRM_TPHONE"));
                employee.setDateOfCurPosting(result.getDate("CURR_POST_DOJ"));
                employee.setGp(result.getInt("GP"));
                employee.setPayScale(result.getString("CUR_SALARY"));
                employee.setCadreCode(result.getString("CUR_CADRE_CODE"));
                employee.setPostGrpType(result.getString("POST_GRP_TYPE"));
                employee.setFieldOffCode(result.getString("FIELD_OFF_CODE"));
                employee.setEmail(result.getString("EMAIL_ID"));
            }
        } catch (Exception e) {
            employee.setFname(e.getMessage());
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(result, statement);
            DataBaseFunctions.closeSqlObjects(conn);
        }
        return employee;
    }

    @Override
    public Pensioner getPensionerDetailsThroughAccNo(String gpfno) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Pensioner p = new Pensioner();
        String sql = "";
        try {
            con = dataSource.getConnection();
            sql = "SELECT EMP_ID,GPF_NO,ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME,L_NAME], ' ') EMPNAME,GENDER,EMP.M_STATUS,int_marital_status_id,EMP.CATEGORY,CUR_BASIC_SALARY,HEIGHT,"
                    + "         DOB,EXTRACT(YEAR from AGE(NOW(), dob)) as age,JOINDATE_OF_GOO,DOS,CUR_SPC,CUR_OFF_CODE,BL_GRP,EMP.RELIGION,int_religion_id, "
                    + "         PH_CODE, ID_MARK, MOBILE,PRM_TPHONE, CURR_POST_DOJ, EMP.GP, CUR_SALARY,ppay, CUR_CADRE_CODE, POST_GRP_TYPE, FIELD_OFF_CODE, EMAIL_ID, "
                    + "         CHEST, WEIGHT, LEFT_VISION, RIGHT_VISION, BRASS_NO,bank_acc_no,emp.branch_code,branch_name,ifsc_code,OFF_EN,GOFFICE.off_address,GOFFICE.DEPARTMENT_CODE,department_name,dept_abbr,GOFFICE.DIST_CODE,INT_DISTRICT_ID,GOFFICE.TR_CODE,INT_TREASURY_ID,"
                    + "         getVillageName(prm_vill_code) prm_vill_code,getBlockName(prm_bl_code) prm_bl_code,getPoliceStationName(prm_ps_code) prm_ps,prm_pin,getTreasuryDistCode(prm_dist_code) prm_dist,prm_state_code,getVillageName(res_vill_code) res_vill_code ,getBlockName(res_bl_code) res_bl_code ,getPoliceStationName(res_ps_code) res_ps_code , "
                    + "		res_pin,getTreasuryDistCode(res_dist_code) res_dist_code,res_state_code, "
                    + "         GOFFICE.DDO_CODE,GOFFICE.int_ddo_id,DDO_SPC,GPC,POST FROM EMP_MAST EMP "
                    + "         LEFT OUTER JOIN G_OFFICE GOFFICE ON EMP.CUR_OFF_CODE=GOFFICE.OFF_CODE "
                    + "		LEFT OUTER JOIN G_SPC GSPC ON EMP.CUR_SPC=GSPC.SPC "
                    + "		LEFT OUTER JOIN G_POST GPOST ON GSPC.GPC = GPOST.POST_CODE "
                    + "		LEFT OUTER JOIN G_DEPARTMENT DEPT ON GOFFICE.DEPARTMENT_CODE=DEPT.DEPARTMENT_CODE "
                    + "         LEFT OUTER JOIN g_branch gbranch ON emp.branch_code=gbranch.branch_code"
                    + "         LEFT OUTER JOIN g_district gdistrict ON GOFFICE.dist_code=gdistrict.dist_code"
                    + "         LEFT OUTER JOIN g_marital gmarital ON EMP.M_STATUS=gmarital.m_status"
                    + "         LEFT OUTER JOIN g_religion greligion ON EMP.religion=greligion.religion"
                    + "         LEFT OUTER JOIN g_treasury gtreasury ON GOFFICE.TR_CODE=gtreasury.tr_code"
                    + "         WHERE GPF_NO=?";

            ps = con.prepareStatement(sql);
            ps.setString(1, gpfno);
            rs = ps.executeQuery();
            if (rs.next()) {
                String accno = rs.getString("gpf_no").replaceAll("[^0-9]", "");
                String gpfseries = rs.getString("gpf_no").replaceAll("[^A-Z ]", "");
                p.setGpfseries(gpfseries);
                p.setGpfno(accno);

                p.setEmpName(rs.getString("EMPNAME"));
                p.setHrmsEmpId(rs.getString("EMP_ID"));
                p.setDob(formatDate(rs.getDate("DOB")));
                p.setDor(formatDate(rs.getDate("DOS")));
                p.setGender(rs.getString("GENDER"));
                p.setMaritalStatus(rs.getString("int_marital_status_id"));
                p.setReligion(rs.getString("int_religion_id"));
                p.setNationality("1");
                p.setMobileno(rs.getString("MOBILE"));
                p.setEmailId(rs.getString("EMAIL_ID"));
                p.setIdmark(rs.getString("ID_MARK"));
                p.setHeight(rs.getString("HEIGHT"));
                p.setIfscCode(rs.getString("ifsc_code"));
                p.setBranchname(rs.getString("branch_name"));
                p.setBankAccNo(rs.getString("bank_acc_no"));

                p.setPayableTreasuryCode(rs.getString("INT_TREASURY_ID"));
                p.setDdoCode(rs.getString("int_ddo_id"));
                p.setDistrictName(rs.getString("INT_DISTRICT_ID"));
                p.setPensionerDesignation(StringUtils.replace(rs.getString("post"), "&", "AND"));
                p.setDeptcode(rs.getString("dept_abbr"));
                p.setDepartmentName(rs.getString("department_name"));
                p.setOfficeName(StringUtils.replace(rs.getString("OFF_EN"), "&", "AND"));
                p.setOffAddress(rs.getString("off_address"));
                p.setAgeonNextDOB((rs.getInt("age") + 1) + "");
                p.setPostGrpType(rs.getString("POST_GRP_TYPE"));

                p.setPermanentCity(rs.getString("prm_bl_code"));
                p.setPermanentTown(rs.getString("prm_vill_code"));
                p.setPermanentps(rs.getString("prm_ps"));
                p.setPermanentdistName(rs.getString("prm_dist"));
                p.setPermanentAddressPin(rs.getString("prm_pin"));
                p.setPermanentState(rs.getString("prm_state_code"));

                p.setPresentCity(rs.getString("res_bl_code"));
                p.setPresentTown(rs.getString("res_vill_code"));
                p.setPresentps(rs.getString("res_ps_code"));
                p.setPresentdistName(rs.getString("res_dist_code"));
                p.setPresentAddressPin(rs.getString("res_pin"));
                p.setPresentState(rs.getString("res_state_code"));

                p.setDateOfAppointment(formatDate(rs.getDate("JOINDATE_OF_GOO")));
                p.setBasic(rs.getString("CUR_BASIC_SALARY"));
                p.setGp(rs.getString("gp"));
                p.setPayScale(rs.getString("CUR_SALARY"));
                p.setPersonalPay(rs.getString("ppay"));

            }

            DataBaseFunctions.closeSqlObjects(rs);
            ps = con.prepareStatement("SELECT date_of_deceased,deceased_time FROM emp_deceased WHERE emp_id=?");
            ps.setString(1, p.getHrmsEmpId());
            rs = ps.executeQuery();
            if (rs.next()) {
                p.setDeceasedDate(formatDate(rs.getDate("date_of_deceased")));
            }

            DataBaseFunctions.closeSqlObjects(rs);
            ps = con.prepareStatement("SELECT id_description,id_no,id_doi,id_doe,id_poi FROM emp_id_doc WHERE EMP_ID=? AND id_description='PAN' ");
            ps.setString(1, p.getHrmsEmpId());
            rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getString("id_description").equalsIgnoreCase("PAN")) {
                    p.setPanNo(rs.getString("id_no"));
                }
            }

            DataBaseFunctions.closeSqlObjects(rs);
            ps = con.prepareStatement("SELECT ret_type FROM emp_ret_res WHERE emp_id=?");
            ps.setString(1, p.getHrmsEmpId());
            rs = ps.executeQuery();
            if (rs.next()) {
                p.setRetirementType(rs.getString("ret_type"));
            }

            DataBaseFunctions.closeSqlObjects(rs);
            ps = con.prepareStatement("select aqmast.aqsl_no,ad_amt,ad_code from (select aqsl_no from aq_mast aqmast where emp_code=? order by aq_year,aq_month desc limit 1) aqmast "
                    + "			     inner join aq_dtls dtls on aqmast.aqsl_no=dtls.aqsl_no where ad_code='DA'");
            ps.setString(1, p.getHrmsEmpId());
            rs = ps.executeQuery();
            if (rs.next()) {
                p.setDa(rs.getString("ad_amt"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return p;
    }

    @Override
    public IdentityInfo[] getEmployeeIdInformation(String empid) {
        List<IdentityInfo> idlist = new ArrayList<>();
        String SQL = "SELECT id_description,id_no,id_doi,id_doe,id_poi FROM emp_id_doc WHERE EMP_ID=?";
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            conn = dataSource.getConnection();
            statement = conn.prepareStatement(SQL);
            statement.setString(1, empid);
            result = statement.executeQuery();
            System.out.println(SQL);
            while (result.next()) {
                IdentityInfo id = new IdentityInfo();
                id.setIdentityDocNo(result.getString("id_no"));
                if (result.getString("id_description") != null && !result.getString("id_description").equals("")) {
                    if (result.getString("id_description").equalsIgnoreCase("AADHAAR")) {
                        id.setIdentityDocType("1");
                        idlist.add(id);
                    } else if (result.getString("id_description").equalsIgnoreCase("ELECTION ID CARD")) {
                        id.setIdentityDocType("2");
                        idlist.add(id);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(result, statement);
            DataBaseFunctions.closeSqlObjects(conn);
        }
        IdentityInfo idarray[] = idlist.toArray(new IdentityInfo[idlist.size()]);
        return idarray;
    }

    @Override
    public Pensioner getPensionerDetailsThroughHRMSID(String empid) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Pensioner p = new Pensioner();
        String sql = "";
        try {
            con = dataSource.getConnection();
            sql = "SELECT EMP_ID,GPF_NO,ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME,L_NAME], ' ') EMPNAME,GENDER,EMP.M_STATUS,int_marital_status_id,EMP.CATEGORY,CUR_BASIC_SALARY,HEIGHT,"
                    + "         DOB,EXTRACT(YEAR from AGE(NOW(), dob)) as age,JOINDATE_OF_GOO,DOS,CUR_SPC,CUR_OFF_CODE,BL_GRP,EMP.RELIGION,int_religion_id, "
                    + "         PH_CODE, ID_MARK, MOBILE,PRM_TPHONE, CURR_POST_DOJ, EMP.GP, CUR_SALARY,ppay, CUR_CADRE_CODE, POST_GRP_TYPE, FIELD_OFF_CODE, EMAIL_ID, "
                    + "         CHEST, WEIGHT, LEFT_VISION, RIGHT_VISION, BRASS_NO,bank_acc_no,emp.branch_code,branch_name,ifsc_code,OFF_EN,GOFFICE.off_address,GOFFICE.DEPARTMENT_CODE,department_name,dept_abbr,GOFFICE.DIST_CODE,INT_DISTRICT_ID,GOFFICE.TR_CODE,INT_TREASURY_ID,"
                    + "         getVillageName(prm_vill_code) prm_vill_code,getBlockName(prm_bl_code) prm_bl_code,getPoliceStationName(prm_ps_code) prm_ps,prm_pin,getTreasuryDistCode(prm_dist_code) prm_dist,prm_state_code,getVillageName(res_vill_code) res_vill_code ,getBlockName(res_bl_code) res_bl_code ,getPoliceStationName(res_ps_code) res_ps_code , "
                    + "		res_pin,getTreasuryDistCode(res_dist_code) res_dist_code,res_state_code, "
                    + "         GOFFICE.DDO_CODE,GOFFICE.int_ddo_id,DDO_SPC,GPC,POST FROM EMP_MAST EMP "
                    + "         LEFT OUTER JOIN G_OFFICE GOFFICE ON EMP.CUR_OFF_CODE=GOFFICE.OFF_CODE "
                    + "		LEFT OUTER JOIN G_SPC GSPC ON EMP.CUR_SPC=GSPC.SPC "
                    + "		LEFT OUTER JOIN G_POST GPOST ON GSPC.GPC = GPOST.POST_CODE "
                    + "		LEFT OUTER JOIN G_DEPARTMENT DEPT ON GOFFICE.DEPARTMENT_CODE=DEPT.DEPARTMENT_CODE "
                    + "         LEFT OUTER JOIN g_branch gbranch ON emp.branch_code=gbranch.branch_code"
                    + "         LEFT OUTER JOIN g_district gdistrict ON GOFFICE.dist_code=gdistrict.dist_code"
                    + "         LEFT OUTER JOIN g_marital gmarital ON EMP.M_STATUS=gmarital.m_status"
                    + "         LEFT OUTER JOIN g_religion greligion ON EMP.religion=greligion.religion"
                    + "         LEFT OUTER JOIN g_treasury gtreasury ON GOFFICE.TR_CODE=gtreasury.tr_code"
                    + "         WHERE EMP_ID=?";

            ps = con.prepareStatement(sql);
            ps.setString(1, empid);
            rs = ps.executeQuery();
            if (rs.next()) {
                String accno = rs.getString("gpf_no").replaceAll("[^0-9]", "");
                String gpfseries = rs.getString("gpf_no").replaceAll("[^A-Z ]", "");
                p.setGpfseries(gpfseries);
                p.setGpfno(accno);

                p.setEmpName(rs.getString("EMPNAME"));
                p.setHrmsEmpId(rs.getString("EMP_ID"));
                p.setDob(formatDate(rs.getDate("DOB")));
                p.setDor(formatDate(rs.getDate("DOS")));
                p.setGender(rs.getString("GENDER"));
                p.setMaritalStatus(rs.getString("int_marital_status_id"));
                p.setReligion(rs.getString("int_religion_id"));
                p.setNationality("1");
                p.setMobileno(rs.getString("MOBILE"));
                p.setEmailId(rs.getString("EMAIL_ID"));
                p.setIdmark(rs.getString("ID_MARK"));
                p.setHeight(rs.getString("HEIGHT"));
                p.setIfscCode(rs.getString("ifsc_code"));
                p.setBranchname(rs.getString("branch_name"));
                p.setBankAccNo(rs.getString("bank_acc_no"));

                p.setPayableTreasuryCode(rs.getString("INT_TREASURY_ID"));
                p.setDdoCode(rs.getString("int_ddo_id"));
                p.setDistrictName(rs.getString("INT_DISTRICT_ID"));
                p.setPensionerDesignation(StringUtils.replace(rs.getString("post"), "&", "AND"));
                p.setDeptcode(rs.getString("dept_abbr"));
                p.setDepartmentName(rs.getString("department_name"));
                p.setOfficeName(StringUtils.replace(rs.getString("OFF_EN"), "&", "AND"));
                p.setOffAddress(rs.getString("off_address"));
                p.setAgeonNextDOB((rs.getInt("age") + 1) + "");
                p.setPostGrpType(rs.getString("POST_GRP_TYPE"));

                p.setPermanentCity(rs.getString("prm_bl_code"));
                p.setPermanentTown(rs.getString("prm_vill_code"));
                p.setPermanentps(rs.getString("prm_ps"));
                p.setPermanentdistName(rs.getString("prm_dist"));
                p.setPermanentAddressPin(rs.getString("prm_pin"));
                p.setPermanentState(rs.getString("prm_state_code"));

                p.setPresentCity(rs.getString("res_bl_code"));
                p.setPresentTown(rs.getString("res_vill_code"));
                p.setPresentps(rs.getString("res_ps_code"));
                p.setPresentdistName(rs.getString("res_dist_code"));
                p.setPresentAddressPin(rs.getString("res_pin"));
                p.setPresentState(rs.getString("res_state_code"));

                p.setDateOfAppointment(formatDate(rs.getDate("JOINDATE_OF_GOO")));
                p.setBasic(rs.getString("CUR_BASIC_SALARY"));
                p.setGp(rs.getString("gp"));
                p.setPayScale(rs.getString("CUR_SALARY"));
                p.setPersonalPay(rs.getString("ppay"));

            }

            DataBaseFunctions.closeSqlObjects(rs);
            ps = con.prepareStatement("SELECT date_of_deceased,deceased_time FROM emp_deceased WHERE emp_id=?");
            ps.setString(1, p.getHrmsEmpId());
            rs = ps.executeQuery();
            if (rs.next()) {
                p.setDeceasedDate(formatDate(rs.getDate("date_of_deceased")));
            }

            DataBaseFunctions.closeSqlObjects(rs);
            ps = con.prepareStatement("SELECT id_description,id_no,id_doi,id_doe,id_poi FROM emp_id_doc WHERE EMP_ID=? AND id_description='PAN' ");
            ps.setString(1, p.getHrmsEmpId());
            rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getString("id_description").equalsIgnoreCase("PAN")) {
                    p.setPanNo(rs.getString("id_no"));
                }
            }

            DataBaseFunctions.closeSqlObjects(rs);
            ps = con.prepareStatement("SELECT ret_type FROM emp_ret_res WHERE emp_id=?");
            ps.setString(1, p.getHrmsEmpId());
            rs = ps.executeQuery();
            if (rs.next()) {
                p.setRetirementType(rs.getString("ret_type"));
            }

            DataBaseFunctions.closeSqlObjects(rs);
            ps = con.prepareStatement("select aqmast.aqsl_no,ad_amt,ad_code from (select aqsl_no from aq_mast aqmast where emp_code=? order by aq_year,aq_month desc limit 1) aqmast "
                    + "			     inner join aq_dtls dtls on aqmast.aqsl_no=dtls.aqsl_no where ad_code='DA'");
            ps.setString(1, p.getHrmsEmpId());
            rs = ps.executeQuery();
            if (rs.next()) {
                p.setDa(rs.getString("ad_amt"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return p;
    }

    @Override
    public ArrayList getOfficeWiseEmployeeList(String offCode) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList empList = new ArrayList();
        try {
            con = dataSource.getConnection();
            ps = con.prepareStatement("SELECT EMP_MAST.EMP_ID,GPF_NO,F_NAME,M_NAME,L_NAME,CUR_BASIC_SALARY,EMP_MAST.GP,G_CADRE.CADRE_NAME,CUR_CADRE_GRADE,DOB,DOS,joindate_of_goo,home_town,POST,ID_NO FROM EMP_MAST "
                    + "LEFT OUTER JOIN G_CADRE ON EMP_MAST.CUR_CADRE_CODE = G_CADRE.CADRE_CODE "
                    + "LEFT OUTER JOIN G_SPC ON EMP_MAST.CUR_SPC = G_SPC.SPC "
                    + "LEFT OUTER JOIN G_POST ON G_SPC.GPC = G_POST.POST_CODE "
                    + "LEFT OUTER JOIN (SELECT * FROM EMP_ID_DOC WHERE ID_DESCRIPTION = 'AADHAAR')EMP_ID_DOC ON EMP_MAST.EMP_ID = EMP_ID_DOC.EMP_ID "
                    + "WHERE CUR_OFF_CODE=? AND DEP_CODE='02' ORDER BY F_NAME,M_NAME,L_NAME");
            ps.setString(1, offCode);
            rs = ps.executeQuery();
            while (rs.next()) {
                Employee employee = new Employee();
                employee.setEmpid(rs.getString("EMP_ID"));
                employee.setFname(rs.getString("F_NAME"));
                employee.setMname(rs.getString("M_NAME"));
                employee.setLname(rs.getString("L_NAME"));
                employee.setGpfno(rs.getString("GPF_NO"));
                employee.setPost(rs.getString("POST"));
                employee.setCadreCode(rs.getString("CADRE_NAME"));
                employee.setCadreGrade(rs.getString("CUR_CADRE_GRADE"));
                employee.setDob(formatDate(rs.getDate("DOB")));
                employee.setDor(formatDate(rs.getDate("DOS")));
                employee.setJoindategoo(formatDate(rs.getDate("joindate_of_goo")));
                employee.setBasic(rs.getInt("CUR_BASIC_SALARY"));
                employee.setPermanentdist(rs.getString("home_town"));
                employee.setGp(rs.getInt("GP"));
                employee.setAadhaarno(rs.getString("ID_NO"));
                empList.add(employee);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return empList;
    }

    @Override
    public EmployeePayProfile getEmployeePayProfile(String empid) {
        EmployeePayProfile empPay = new EmployeePayProfile();
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            con = dataSource.getConnection();
            statement = con.prepareStatement("SELECT CUR_BASIC_SALARY,GP,CUR_SALARY FROM EMP_MAST WHERE EMP_ID=?");
            statement.setString(1, empid);
            result = statement.executeQuery();
            if (result.next()) {
                empPay.setBasic(result.getInt("CUR_BASIC_SALARY"));
                empPay.setGp(result.getInt("GP"));
                empPay.setPayScale(result.getString("CUR_SALARY"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return empPay;
    }

    @Override
    public List SearchEmployee(SearchEmployee searchEmployee) {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement st = null;
        ArrayList employeeList = new ArrayList();
        Employee employee = null;
        try {
            con = dataSource.getConnection();
            //System.out.println(searchEmployee.getSearchString() + "****" + searchEmployee.getCriteria());
            if (searchEmployee.getCriteria() == null) {
                st = con.prepareStatement("SELECT emp_id, f_name, m_name,l_name FROM emp_mast WHERE cur_off_code = ? ");
                st.setString(1, searchEmployee.getOffcode());
                //  System.out.println("()()()(()");
            } else if (searchEmployee.getCriteria().equals("GPFNO")) {
                st = con.prepareStatement("SELECT emp_id, f_name, m_name,l_name FROM emp_mast WHERE gpf_no=?");
                st.setString(1, searchEmployee.getSearchString());
            } else if (searchEmployee.getCriteria().equals("HRMSID")) {
                st = con.prepareStatement("SELECT emp_id, f_name, m_name,l_name FROM emp_mast WHERE emp_id=?");
                st.setString(1, searchEmployee.getSearchString());
            } else if (searchEmployee.getCriteria().equals("FNAME")) {
                st = con.prepareStatement("SELECT emp_id, f_name, m_name,l_name FROM emp_mast WHERE F_name LIKE  ? ");
                st.setString(1, searchEmployee.getSearchString() + "%");
            } else {
                st = con.prepareStatement("SELECT emp_id, f_name, m_name,l_name FROM emp_mast WHERE cur_off_code = ? ");
                st.setString(1, searchEmployee.getOffcode());
                // System.out.println("#####");
            }
            rs = st.executeQuery();
            while (rs.next()) {
                employee = new Employee();
                employee.setEmpid(rs.getString("EMP_ID"));
                employee.setFname(rs.getString("F_NAME"));
                employee.setMname(rs.getString("M_NAME"));
                employee.setLname(rs.getString("L_NAME"));
                employeeList.add(employee);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, st);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return employeeList;
    }

    @Override
    public IdentityInfo[] getIdentity(String empid) {
        List<IdentityInfo> idInfoList = new ArrayList<>();
        String SQL = "SELECT ID_DESCRIPTION,ID_NO,ID_DOI,ID_DOE,ID_POI FROM EMP_ID_DOC WHERE EMP_ID=?";
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            conn = dataSource.getConnection();
            statement = conn.prepareStatement(SQL);
            statement.setString(1, empid);
            result = statement.executeQuery();
            while (result.next()) {
                IdentityInfo idInfo = new IdentityInfo();
                idInfo.setIdentityNo(result.getString("ID_NO"));
                idInfo.setIdentityDesc(result.getString("ID_DESCRIPTION"));
                idInfo.setIssueDate(formatDate(result.getDate("ID_DOI")));
                idInfo.setExpiryDate(formatDate(result.getDate("ID_DOE")));
                idInfo.setPlaceOfIssue(result.getString("ID_POI"));
                idInfoList.add(idInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(result, statement);
            DataBaseFunctions.closeSqlObjects(conn);
        }
        IdentityInfo idInfoarray[] = idInfoList.toArray(new IdentityInfo[idInfoList.size()]);
        return idInfoarray;
    }

    @Override
    public Address[] getAddress(String empid) {
        List<Address> addressList = new ArrayList<>();

        String SQL = "SELECT ADDRESS_TYPE,ADDRESS,BL_CODE,VILL_CODE,PO_CODE,PS_CODE,PIN,STATE_CODE,DIST_CODE,STD_CODE,TPHONE from emp_address WHERE EMP_ID=?";
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            conn = dataSource.getConnection();
            statement = conn.prepareStatement(SQL);
            statement.setString(1, empid);
            result = statement.executeQuery();
            while (result.next()) {
                Address addr = new Address();
                addr.setAddressType(result.getString("ADDRESS_TYPE"));
                addr.setAddress(result.getString("ADDRESS"));
                addr.setBlockCode(result.getString("BL_CODE"));
                addr.setVillageCode(result.getString("VILL_CODE"));
                addr.setPostCode(result.getString("PO_CODE"));
                addr.setPsCode(result.getString("PS_CODE"));
                addr.setDistCode(result.getString("DIST_CODE"));
                addr.setStateCode(result.getString("STATE_CODE"));
                addr.setPin(result.getString("PIN"));
                addr.setStdCode(result.getString("STD_CODE"));
                addr.setTelephone(result.getString("TPHONE"));
                addressList.add(addr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(result, statement);
            DataBaseFunctions.closeSqlObjects(conn);
        }
        Address addrarray[] = addressList.toArray(new Address[addressList.size()]);
        return addrarray;
    }

    @Override
    public int saveEmployeeMessage(EmployeeMessage employeemessage) {
        int messageId = 0;
        boolean flag = false;
        PreparedStatement pst = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("INSERT INTO employee_message(emp_id,Message,Message_on_date,off_code) VALUES(?, ?, ?, ? )", Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, employeemessage.getEmpid());
            pst.setString(2, employeemessage.getMessage());
            pst.setDate(3, new java.sql.Date(new Date().getTime()));
            pst.setString(4, employeemessage.getOffcode());
            pst.executeUpdate();
            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                messageId = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return messageId;
    }

    @Override
    public List getSentMessageList() {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement st = null;
        ArrayList EmployeeMessageList = new ArrayList();
        Employee employee = null;
        try {
            con = dataSource.getConnection();
            st = con.prepareStatement("SELECT emp_mast.emp_id, f_name, m_name,l_name, message, message_on_date, viewed_on_date, off_code, is_viewed from employee_message "
                    + "INNER JOIN emp_mast ON employee_message.emp_id = emp_mast.emp_id ");

            rs = st.executeQuery();
            while (rs.next()) {
                EmployeeMessage message = new EmployeeMessage();

                message.setEmpid(rs.getString("emp_id"));
                message.setEmpname(rs.getString("f_name") + " " + StringUtils.defaultString(rs.getString("m_name")) + " " + rs.getString("l_name"));
                message.setMessage(rs.getString("message"));
                message.setOffcode(rs.getString("off_code"));
                message.setIsviewed(rs.getString("is_viewed"));
                message.setMessageondate(rs.getString("message_on_date"));
                message.setViewondate(rs.getString("viewed_on_date"));

                EmployeeMessageList.add(message);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, st);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return EmployeeMessageList;
    }

    @Override
    public void uploadAttachedFile(int messageId, MultipartFile file) throws SQLException {
        
        String diskfileName = new Date().getTime() + "";
        PreparedStatement pst = null;
        Connection con = null;
        if (!file.isEmpty()) {
            try {

                byte[] bytes = file.getBytes();
                File dir = new File(this.uploadPath + File.separator + "tmpFiles");
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File serverFile = new File(dir.getAbsolutePath() + File.separator + diskfileName);
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
                stream.write(bytes);
                stream.close();

                con = dataSource.getConnection();
                pst = con.prepareStatement("INSERT INTO employee_attachment(o_file_name,d_file_name,ref_id,ref_type,file_path,file_type) VALUES(?, ?, ?, ?, ?, ? )");
                pst.setString(1, file.getOriginalFilename());
                pst.setString(2, diskfileName);
                pst.setInt(3, messageId);
                pst.setString(4, "MESSAGE");
                pst.setString(5, dir.getAbsolutePath() + File.separator + diskfileName);
                pst.setString(6, file.getContentType());
                pst.executeUpdate();
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            } finally {
                DataBaseFunctions.closeSqlObjects(pst);
                DataBaseFunctions.closeSqlObjects(con);
            }
        }
    }

    @Override
    public List getEmployeeMessageList(String empid) {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement st = null;
        ArrayList EmployeeMessageList = new ArrayList();
        Employee employee = null;
        try {
            con = dataSource.getConnection();
            st = con.prepareStatement("SELECT  message, message_on_date, viewed_on_date, off_code, is_viewed from employee_message where emp_id= ?");

            rs = st.executeQuery();
            while (rs.next()) {
                EmployeeMessage message = new EmployeeMessage();

                message.setMessage(rs.getString("message"));
                message.setOffcode(rs.getString("off_code"));
                message.setIsviewed(rs.getString("is_viewed"));
                message.setMessageondate(rs.getString("message_on_date"));
                message.setViewondate(rs.getString("viewed_on_date"));

                EmployeeMessageList.add(message);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, st);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return EmployeeMessageList;
    }
    @Override
    public List getOffWiseEmpList(String offCode) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        ArrayList empList = new ArrayList();
        Employee employee = null;
        try {
            con = dataSource.getConnection();
            String empListQry = "select emp_id,INITIALS, F_NAME, M_NAME, L_NAME,POST from emp_mast EM "
                    + "LEFT OUTER JOIN g_spc GS ON EM.cur_spc=GS.spc "
                    + "LEFT OUTER JOIN g_post GP ON GP.post_code= GS.gpc where cur_off_code=? ORDER BY F_NAME";
            pstmt = con.prepareStatement(empListQry);
            pstmt.setString(1, offCode);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                employee = new Employee();
                employee.setEmpid(rs.getString("EMP_ID"));
                employee.setIntitals(rs.getString("INITIALS"));
                employee.setFname(rs.getString("F_NAME"));
                employee.setMname(rs.getString("M_NAME"));
                employee.setLname(rs.getString("L_NAME"));
                employee.setPost(rs.getString("POST"));
                empList.add(employee);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return empList;
    }

    @Override
    public List getDeptWiseEmpList(String deptCode) {

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        ArrayList empList = new ArrayList();
        Employee employee = null;

        try {
            con = dataSource.getConnection();
            String empListQry = "select emp_id,ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME, L_NAME], ' ') EMPNAME,post "
                    + "from g_office GE INNER JOIN emp_mast EM ON GE.off_code=EM.cur_off_code "
                    + "LEFT OUTER JOIN g_spc GS ON EM.cur_spc=GS.spc "
                    + "LEFT OUTER JOIN g_post GP ON GP.post_code= GS.gpc where GE.department_code=? ORDER BY F_NAME";

            pstmt = con.prepareStatement(empListQry);
            pstmt.setString(1, deptCode);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                employee = new Employee();
                employee.setEmpid(rs.getString("EMP_ID"));
                employee.setIntitals(rs.getString("INITIALS"));
                employee.setFname(rs.getString("F_NAME"));
                employee.setMname(rs.getString("M_NAME"));
                employee.setLname(rs.getString("L_NAME"));
                employee.setPost(rs.getString("POST"));
                empList.add(employee);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return empList;
    }
    @Override
    public void saveProfile(Employee emp) {
        PreparedStatement pst = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            pst = con.prepareStatement("update emp_mast set gistype=?,gisno=?,dos=?,m_status=?,category=?,height=?,bl_grp=?,home_town=?,religion=?,domicile=?,id_mark=?,mobile=?,joindate_of_goo=?,doe_gov=?,jointime_of_goo=?,bank_code=?,branch_code=?,bank_acc_no=? where emp_id=?");
            pst.setString(1, emp.getGisType());
            pst.setString(2, emp.getGisNo());
            pst.setString(3, emp.getDor());
            pst.setString(4, emp.getMarital());
            pst.setString(5, emp.getCategory());
            pst.setInt(6, emp.getHeight());
            pst.setString(7, emp.getBloodgrp());
            pst.setString(8, emp.getHomeTown());
            pst.setString(9, emp.getReligion());
            pst.setString(10, emp.getDomicil());
            pst.setString(11, emp.getIdmark());
            pst.setString(12, emp.getMobile());
            pst.setString(13, emp.getJoindategoo());
            pst.setString(14, emp.getDoeGov());
            pst.setString(15, emp.getTimeOfEntryGoo());
            pst.setString(16, emp.getSltBank());
            pst.setString(17, emp.getSltbranch());
            pst.setString(18, emp.getBankaccno());
            pst.setString(19, emp.getSltbranch());
            pst.setString(20, emp.getEmpid());

            pst.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

}


