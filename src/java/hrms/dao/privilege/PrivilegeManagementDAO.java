/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.privilege;

import hrms.model.master.Module;
import hrms.model.privilege.ModuleGroup;
import hrms.model.privilege.Privilege;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Surendra
 */
public interface PrivilegeManagementDAO {

    public ArrayList getRoleList();

    public ArrayList getModuleGroupList(String roleId);

    public ArrayList getAssignedPrivilageList(String spc);

    public String assignPrivilege(Module module, String spc);

    public String revokePrivilege(String spc, int privmapid);

    public List getUserList(String userType);

    public List getAssignedPrivilageUserNameSpecificList(String userName);

    public List getModuleListUserNameSpecific(String userType);

    public String assignPrivilegeUserNameSpecific(Module module, String username);

    public String revokePrivilageUserNameSpecific(String username, int privmapid);

    

    

}
