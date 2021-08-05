/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.model.privilege;

/**
 *
 * @author Manas Jena
 */
public class Privilege {

    private int privmapid;
    private String rolename;
    private String modulegroup;
    private String modulename;
    
    

    public int getPrivmapid() {
        return privmapid;
    }

    public void setPrivmapid(int privmapid) {
        this.privmapid = privmapid;
    }

    public String getRolename() {
        return rolename;
    }

    public void setRolename(String rolename) {
        this.rolename = rolename;
    }

    public String getModulegroup() {
        return modulegroup;
    }

    public void setModulegroup(String modulegroup) {
        this.modulegroup = modulegroup;
    }

    public String getModulename() {
        return modulename;
    }

    public void setModulename(String modulename) {
        this.modulename = modulename;
    }    

}
