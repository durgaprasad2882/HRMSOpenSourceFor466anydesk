package hrms.dao.preferauthority;

import java.util.List;

public interface SactionedAuthorityDAO {

    public List getSanctionedAuthorityList(String spc, int processid, String fiscalyear, String empid);

    //public List getDeptList();
    public List getOfficeList(String deptcode);

    public List getPostList(String deptcode, String offcode);

    public List getPostListWithoutAuthority(String offcode);

    public String getAssignedDepartment(String lmid);

    public void addAuthoritySPC(String[] authSPCCode, String applicantspc, int processid, String deptcode, String offcode);

    public List getGPCListOfficeWise(String deptcode);

    public List getPostListGPCWiseAuthority(String gpc, String offcode);
}
