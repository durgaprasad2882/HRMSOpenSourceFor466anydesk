/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.login;

import hrms.model.lamembers.LAMembers;
import hrms.model.login.AdminUsers;
import hrms.model.login.UserDetails;
import hrms.model.login.UserExpertise;
import hrms.model.login.Users;
import hrms.model.master.Module;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Surendra
 */
public interface LoginDAO {

    Users findByUserName(String username);

    public Users getEmployeeProfileInfo(String hrmsid);

    public LAMembers getMiniSterialProfileInfo(String lmid);

    public UserDetails checkLogin(String userid, String pwd);

    public List getProposedEmployeeList(String offCode, int year, int month);

    public List getEmployeeList(String offCode);

    public AdminUsers getAdminUsersProfileInfo(String userid);

    public int requestPassword(Map<String, String> params);

    public Users getInstituteInfo(String hrmsId);

    public void updateCadreStatus(String empId, String cadreStat, String subCadreStat);

    public AdminUsers getDeptUsersProfileInfo(String userid);

    public UserExpertise getUserInfo(String hrmsid);

    public boolean countExpertise(String empid);

    public Module[] getUserPrivileageList(String username);

    public boolean getEligibility(String postCode);

    public String getCurrentTrainingProgram();

}
