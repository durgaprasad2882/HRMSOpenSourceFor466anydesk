package hrms.dao.preferauthority;

import hrms.SelectOptionforLAMembers;
import hrms.common.DataBaseFunctions;
import hrms.model.master.Department;
import hrms.model.g_officating.G_Officiating;
import hrms.model.master.GPost;
import hrms.model.master.Office;
import hrms.model.preferauthority.WorkflowAuthority;
import hrms.model.master.SubstantivePost;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public class SactionedAuthorityDAOImpl implements SactionedAuthorityDAO {

    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /*@Override
     public List getSanctionedAuthorityList(String spc, int processid, String fiscalyear, String empid) {
     Session session = null;
     List sanctionedauthoritylist1 = null;
     List sanctionedauthoritylist2 = null;
        
     Connection con = null;
     try{
            
     SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yy");
            
     String[] yeartemp = fiscalyear.split("-");
     int year1 = Integer.parseInt(yeartemp[0]);
     int year2 = year1+1;

     String finyear1 = "01-APR-"+year1;
     String finyear2 = "31-MAR-"+year2;
            
     con = dataSource.getConnection();
     Criteria cr = session.createCriteria(WorkflowAuthority.class,"wf");
     cr.add(Restrictions.eq("wf.applicantSpc", spc));
     cr.add(Restrictions.eq("processid", processid));
     cr.add(Restrictions.ne("wf.authoritySpc.substantivePost.spc", spc));
     cr.createAlias("authoritySpc", "empmast").add(Restrictions.ne("empmast.empId", empid));
                    
     sanctionedauthoritylist1 = cr.list();
            
     cr = session.createCriteria(EmpRelieve.class)
     .setProjection(Projections.projectionList()
     .add(Projections.property("empmast.empId"))
     .add(Projections.property("wfspc.authoritySpc"))
     .add(Projections.property("empmast.fullName"))
     .add(Projections.property("wfspc.authoritySpc")));
     //cr.add(Restrictions.ge("rlvdate", formatter.parse(finyear1)));
     //cr.add(Restrictions.lt("rlvdate", formatter.parse(finyear2)));
     cr.add(Restrictions.between("rlvdate", (Date)formatter.parse(finyear1), (Date)formatter.parse(finyear2)));
     cr.createAlias("spc", "wfspc")
     .add(Restrictions.eq("wfspc.applicantSpc", spc))
     .add(Restrictions.eq("wfspc.processid", processid))
     .add(Restrictions.ne("wfspc.authoritySpc", spc));
     cr.createAlias("empid", "empmast")
     .add(Restrictions.ne("empmast.empId", empid));
     //cr.createAlias("spc", "gspc");
     sanctionedauthoritylist2 = cr.list();
            
     sanctionedauthoritylist1.add(sanctionedauthoritylist2);
     }catch(Exception e){
     e.printStackTrace();
     }finally{
     session.flush();
     session.close();
     }
     return sanctionedauthoritylist1;
     }*/
    public String getApplicantSPC(int parid) {

        Connection con = null;

        String spc = "";

        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = dataSource.getConnection();

            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT SPC FROM PAR_MASTER WHERE PARID=" + parid);
            if (rs.next()) {
                spc = rs.getString("SPC");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return spc;
    }

    @Override
    public List getSanctionedAuthorityList(String spc, int processid, String fiscalyear, String empid) {

        List sanctionedauthoritylist1 = new ArrayList();
        List sanctionedauthoritylist2 = null;

        Connection con = null;

        Statement stmt = null;
        ResultSet rs = null;
        WorkflowAuthority wa = null;
        try {

            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yy");

            String[] yeartemp = fiscalyear.split("-");
            int year1 = Integer.parseInt(yeartemp[0]);
            int year2 = year1 + 1;

            String finyear1 = "01-APR-" + year1;
            String finyear2 = "31-MAR-" + year2;

            con = dataSource.getConnection();

            stmt = con.createStatement();
            /*String sql = "SELECT EMPNAME,POST,AUTHORITY_SPC,EMP_ID,F_NAME FROM(SELECT GPC,EMPNAME,AUTHORITY_SPC,EMP_ID,F_NAME" +
             " FROM(SELECT EMP_ID,AUTHORITY_SPC,CASE WHEN TRIM(INITIALS) = NULL THEN '' ELSE TRIM(INITIALS)||' ' END ||" +
             " CASE WHEN TRIM(F_NAME) = NULL THEN '' ELSE TRIM(F_NAME)||' ' END || CASE WHEN TRIM(M_NAME) = NULL THEN '' ELSE TRIM(M_NAME)||' ' END" +
             " || CASE WHEN TRIM(L_NAME) = NULL THEN '' ELSE TRIM(L_NAME) END EMPNAME,F_NAME FROM (SELECT AUTHORITY_SPC FROM WORKFLOW_AUTHORITY WHERE APPLICANT_SPC ='"+ spc + "'" +
             " AND PROCESS_ID='"+ processid + "' AND AUTHORITY_SPC != '"+spc+"' )WFAUTH INNER JOIN EMP_MAST ON EMP_MAST.CUR_SPC=WFAUTH.AUTHORITY_SPC)TAB INNER JOIN" +
             " G_SPC ON G_SPC.SPC=TAB.AUTHORITY_SPC)TAB1 INNER JOIN G_POST ON G_POST.POST_CODE=TAB1.GPC WHERE ISAUTHORITY='Y' AND EMP_ID != '"+empid+"'";*/

            String sql = "SELECT DISTINCT LMID, EMPNAME,OFF_AS,OFF_NAME,FNAME FROM( "
                    + "SELECT LMID,ARRAY_TO_STRING(ARRAY[INITIALS, FNAME,MNAME,LNAME], ' ') EMPNAME,OFF_AS,FNAME FROM( "
                    + "SELECT AUTHORITY_SPC FROM WORKFLOW_AUTHORITY WHERE APPLICANT_SPC ='" + spc + "' AND PROCESS_ID='" + processid + "' AND AUTHORITY_TYPE='LM') WORKFLOW_AUTHORITY "
                    + "INNER JOIN LA_MEMBERS ON WORKFLOW_AUTHORITY.AUTHORITY_SPC = LA_MEMBERS.LMID::VARCHAR) LA_MEMBERS "
                    + "INNER JOIN G_OFFICIATING ON LA_MEMBERS.OFF_AS=G_OFFICIATING.OFF_ID WHERE LMID::VARCHAR !='" + empid + "' ORDER BY FNAME";
            System.out.println("SQL is: " + sql);
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                wa = new WorkflowAuthority();
                wa.setPost(rs.getString("OFF_NAME"));
                wa.setEmpid(rs.getString("LMID"));
                wa.setEmpname(rs.getString("EMPNAME"));
                wa.setAuthoritySpc(rs.getString("LMID"));
                wa.setCombination(rs.getString("LMID") + ":" + rs.getString("EMPNAME") + ":" + rs.getString("OFF_NAME") + ":" + rs.getString("LMID"));
                sanctionedauthoritylist1.add(wa);
            }
            sql = "SELECT EMPNAME,SPN,AUTHORITY_SPC,EMP_ID,F_NAME FROM"
                    + " (SELECT GPC,SPN,EMPNAME,AUTHORITY_SPC,EMP_ID,F_NAME FROM"
                    + " (SELECT EMP_ID,AUTHORITY_SPC,"
                    + " ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME,L_NAME], ' ') EMPNAME,F_NAME FROM (SELECT AUTHORITY_SPC FROM WORKFLOW_AUTHORITY WHERE"
                    + " APPLICANT_SPC ='" + spc + "' AND PROCESS_ID='" + processid + "' AND AUTHORITY_SPC != '" + spc + "' )WFAUTH INNER JOIN EMP_MAST"
                    + " ON EMP_MAST.CUR_SPC=WFAUTH.AUTHORITY_SPC)TAB INNER JOIN G_SPC ON G_SPC.SPC=TAB.AUTHORITY_SPC)TAB1 WHERE EMP_ID != '" + empid + "'"
                    + " UNION"
                    + " SELECT EMPNAME,SPN,AUTHORITY_SPC,EMP_RELIEVE.EMP_ID,F_NAME FROM (SELECT * FROM WORKFLOW_AUTHORITY WHERE"
                    + " APPLICANT_SPC='" + spc + "' AND PROCESS_ID='" + processid + "' AND AUTHORITY_SPC != '" + spc + "' )WORKFLOW_AUTHORITY"
                    + " INNER JOIN"
                    + " (SELECT SPC,EMP_ID FROM EMP_RELIEVE WHERE RLV_DATE BETWEEN '" + finyear1 + "' AND '" + finyear2 + "')EMP_RELIEVE ON"
                    + " WORKFLOW_AUTHORITY.AUTHORITY_SPC=EMP_RELIEVE.SPC"
                    + " LEFT OUTER JOIN"
                    + " (SELECT EMP_ID,ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME,L_NAME], ' ') EMPNAME,F_NAME FROM EMP_MAST WHERE EMP_ID != '" + empid + "')"
                    + " EMP_MAST ON EMP_RELIEVE.EMP_ID=EMP_MAST.EMP_ID"
                    + " LEFT OUTER JOIN G_SPC ON WORKFLOW_AUTHORITY.AUTHORITY_SPC=G_SPC.SPC"
                    + " UNION"
                    + " SELECT EMPNAME,SPN,AUTHORITY_SPC,EMP_JOIN.EMP_ID,F_NAME FROM (SELECT * FROM WORKFLOW_AUTHORITY WHERE"
                    + " APPLICANT_SPC='" + spc + "' AND PROCESS_ID='" + processid + "' AND AUTHORITY_SPC != '" + spc + "' )WORKFLOW_AUTHORITY"
                    + " INNER JOIN"
                    + " (SELECT EMP_JOIN.SPC,EMP_JOIN.EMP_ID FROM (SELECT JOIN_ID,SPC,EMP_ID FROM EMP_JOIN WHERE IF_AD_CHARGE='Y')EMP_JOIN LEFT OUTER JOIN EMP_RELIEVE ON EMP_JOIN.JOIN_ID=EMP_RELIEVE.JOIN_ID WHERE EMP_RELIEVE.JOIN_ID IS NULL)EMP_JOIN ON"
                    + " WORKFLOW_AUTHORITY.AUTHORITY_SPC=EMP_JOIN.SPC"
                    + " LEFT OUTER JOIN"
                    + " (SELECT EMP_ID,ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME,L_NAME], ' ') EMPNAME,F_NAME FROM EMP_MAST WHERE EMP_ID != '" + empid + "')"
                    + " EMP_MAST ON EMP_JOIN.EMP_ID=EMP_MAST.EMP_ID"
                    + " LEFT OUTER JOIN G_SPC ON WORKFLOW_AUTHORITY.AUTHORITY_SPC=G_SPC.SPC"
                    + " ORDER BY F_NAME";
            /*sql = "SELECT EMPNAME,POST,AUTHORITY_SPC,EMP_ID,F_NAME FROM(SELECT GPC,EMPNAME,AUTHORITY_SPC,EMP_ID,F_NAME"
             + " FROM(SELECT EMP_ID,AUTHORITY_SPC,ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME,L_NAME], ' ') EMPNAME,F_NAME FROM (SELECT AUTHORITY_SPC FROM WORKFLOW_AUTHORITY WHERE APPLICANT_SPC ='" + spc + "'"
             + " AND PROCESS_ID='" + processid + "' AND AUTHORITY_SPC != '" + spc + "' )WFAUTH INNER JOIN EMP_MAST ON EMP_MAST.CUR_SPC=WFAUTH.AUTHORITY_SPC)TAB INNER JOIN"
             + " G_SPC ON G_SPC.SPC=TAB.AUTHORITY_SPC)TAB1 INNER JOIN G_POST ON G_POST.POST_CODE=TAB1.GPC WHERE ISAUTHORITY='Y' AND EMP_ID != '" + empid + "'"
             + "\n"
             + " UNION"
             + "\n"
             + " SELECT EMPNAME,POST,AUTHORITY_SPC,EMP_RELIEVE.EMP_ID,F_NAME FROM (SELECT * FROM WORKFLOW_AUTHORITY WHERE"
             + " APPLICANT_SPC='" + spc + "' AND PROCESS_ID='" + processid + "' AND AUTHORITY_SPC != '" + spc + "' )WORKFLOW_AUTHORITY"
             + " INNER JOIN"
             + " (SELECT SPC,EMP_ID FROM EMP_RELIEVE WHERE RLV_DATE BETWEEN '" + finyear1 + "' AND '" + finyear2 + "')EMP_RELIEVE"
             + " ON WORKFLOW_AUTHORITY.AUTHORITY_SPC=EMP_RELIEVE.SPC"
             + " LEFT OUTER JOIN (SELECT EMP_ID,ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME,L_NAME], ' ') EMPNAME,F_NAME FROM EMP_MAST WHERE EMP_ID != '" + empid + "')EMP_MAST ON EMP_RELIEVE.EMP_ID=EMP_MAST.EMP_ID"
             + " LEFT OUTER JOIN G_SPC ON WORKFLOW_AUTHORITY.AUTHORITY_SPC=G_SPC.SPC"
             + " LEFT OUTER JOIN G_POST ON G_SPC.GPC=G_POST.POST_CODE"
             + " ORDER BY F_NAME";*/

            System.out.println("SQL is: " + sql);
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                wa = new WorkflowAuthority();
                wa.setEmpid(rs.getString("EMP_ID"));
                wa.setEmpname(rs.getString("EMPNAME"));
                wa.setPost(rs.getString("SPN"));
                wa.setAuthoritySpc(rs.getString("AUTHORITY_SPC"));
                wa.setCombination(rs.getString("EMP_ID") + ":" + rs.getString("EMPNAME") + ":" + rs.getString("SPN") + ":" + rs.getString("AUTHORITY_SPC"));
                sanctionedauthoritylist1.add(wa);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, stmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return sanctionedauthoritylist1;
    }

    /*public List getDeptList() {

     Session session = null;
     List deptlist = new ArrayList();
     SelectOptionforLAMembers so = null;
     try {
     session = this.sessionFactory.openSession();

     so = new SelectOptionforLAMembers();
     so.setDeptCode("LM");
     so.setDeptName("LEGISLATIVE MEMBERS");

     Criteria cr = session.createCriteria(Department.class);
     cr.add(Restrictions.eq("active", "Y"));
     deptlist = cr.list();
     deptlist.add(so);
     } catch (Exception e) {
     e.printStackTrace();
     } finally {
     session.flush();
     session.close();
     }
     return deptlist;
     }*/
    public List getOfficeList(String deptcode) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        List officelist = new ArrayList();
        Office off = null;
        try {
            con = this.dataSource.getConnection();

            if (deptcode != null && !deptcode.equals("")) {
                if (deptcode.equals("LM")) {
                    String sql = "SELECT off_id,off_name FROM g_officiating ORDER BY off_id ASC";
                    pst = con.prepareStatement(sql);
                    rs = pst.executeQuery();
                    while (rs.next()) {
                        off = new Office();
                        off.setOffCode(rs.getString("off_id"));
                        off.setOffName(rs.getString("off_name"));
                        officelist.add(off);
                    }
                } else {
                    String sql = "SELECT off_code,off_en,ddo_code from g_office where department_code=? order by off_en asc";
                    pst = con.prepareStatement(sql);
                    pst.setString(1,deptcode);
                    rs = pst.executeQuery();
                    while (rs.next()) {
                        off = new Office();
                        off.setOffCode(rs.getString("off_code"));
                        off.setOffName(rs.getString("off_en")+" ("+rs.getString("ddo_code")+")");
                        officelist.add(off);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return officelist;
    }

    @Override
    public List getPostList(String deptcode, String offcode) {

        List postlist = new ArrayList();

        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        WorkflowAuthority wa = null;
        try {
            /*Criteria cr = session.createCriteria(SubstantivePost.class);
             cr.add(Restrictions.eq("offcode.offCode",offcode));
             cr.createAlias("gPost", "gPost")
             .setProjection(Projections.projectionList()
             .add(Projections.property("gPost.postcode"))
             .add(Projections.property("gPost.post")))
             .add(Restrictions.eq("gPost.isauth", "Y"));
             cr.setProjection(Projections.projectionList()
             .add(Projections.groupProperty("gPost"))
             .add(Projections.groupProperty("gPost.post")));
             cr.addOrder(Order.asc("gPost.post"));
             postlist = cr.list();*/

            con = dataSource.getConnection();
            stmt = con.createStatement();
            String sql = "";
            if (deptcode != null && deptcode.equalsIgnoreCase("LM")) {
                sql = "SELECT LMID,ARRAY_TO_STRING(ARRAY[INITIALS, FNAME, MNAME,LNAME], ' ') EMPNAME,OFF_AS,RANK FROM LA_MEMBERS WHERE ACTIVE='Y' AND OFF_AS='" + offcode + "'";
                rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    wa = new WorkflowAuthority();
                    wa.setEmpname(rs.getString("EMPNAME"));
                    wa.setAuthoritySpc(rs.getString("LMID"));
                    wa.setPost(getAssignedDepartment(rs.getString("LMID")));
                    wa.setPostcode(rs.getString("LMID"));
                    postlist.add(wa);
                }
            } else {
                sql = "SELECT POST_CODE,POST FROM ("
                        + " SELECT GPC FROM G_SPC WHERE OFF_CODE='" + offcode + "' GROUP BY GPC)G_SPC"
                        + " INNER JOIN (SELECT POST_CODE,POST FROM G_POST WHERE ISAUTHORITY='Y')G_POST ON G_SPC.GPC=G_POST.POST_CODE ORDER BY POST";
                System.out.println("SQL for post list is: " + sql);
                rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    wa = new WorkflowAuthority();
                    wa.setPost(rs.getString("POST"));
                    wa.setPostcode(rs.getString("POST_CODE"));
                    postlist.add(wa);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, stmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return postlist;
    }

    @Override
    public String getAssignedDepartment(String lmid) {

        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        String desc = "";
        try {
            con = dataSource.getConnection();
            String sql = "SELECT DEPARTMENT_NAME FROM G_DEPARTMENT WHERE LMID='" + lmid + "' ORDER BY DEPARTMENT_NAME";
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                if (desc != null && !desc.equals("")) {
                    desc = desc + ", " + rs.getString("DEPARTMENT_NAME");
                } else {
                    desc = rs.getString("DEPARTMENT_NAME");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, stmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return desc;
    }

    @Override
    public void addAuthoritySPC(String[] authSPCCode, String applicantspc, int processid, String deptcode, String offcode) {

        Connection con = null;
        PreparedStatement pst = null;

        Statement stmt = null;
        ResultSet rs = null;
        Statement stmt1 = null;
        ResultSet rs1 = null;
        Statement stmt2 = null;
        ResultSet rs2 = null;

        String sql = "";
        String authSpc = "";
        try {
            System.out.println("Applicant SPC is: " + applicantspc + " and Dept Code is: " + deptcode);
            con = dataSource.getConnection();

            pst = con.prepareStatement("INSERT INTO WORKFLOW_AUTHORITY(APPLICANT_SPC,AUTHORITY_SPC,PROCESS_ID,AUTHORITY_TYPE) VALUES(?,?,?,?)");
            stmt = con.createStatement();
            stmt1 = con.createStatement();
            stmt2 = con.createStatement();
            if (authSPCCode != null) {
                for (int i = 0; i < authSPCCode.length; i++) {
                    /*sql = "SELECT SPC FROM G_SPC WHERE GPC='"+authSPCCode[i]+"' AND OFF_CODE='"+offcode+"' AND (IFUCLEAN='N' OR IFUCLEAN IS NULL)";
                     rs2 = stmt.executeQuery(sql);
                     if(rs.next()){
                     authSpc = rs.getString("SPC");
                     }
                     sql = "SELECT AUTHORITY_SPC FROM WORKFLOW_AUTHORITY WHERE APPLICANT_SPC='" + applicantspc + "' AND AUTHORITY_SPC='" + authSPCCode[i] + "' AND PROCESS_ID='" + processid + "'";
                     rs = stmt.executeQuery(sql);*/

                    if (deptcode != null && deptcode.equalsIgnoreCase("LM")) {
                        if (authSPCCode[i] != null) {
                            boolean isDuplicateLAMemberAsAuthority = isDuplicateInWorkflowAuthority(applicantspc, authSPCCode[i], deptcode);
                            if (isDuplicateLAMemberAsAuthority == false) {
                                pst.setString(1, applicantspc);
                                pst.setString(2, authSPCCode[i]);
                                pst.setInt(3, processid);
                                pst.setString(4, "LM");
                                pst.executeUpdate();
                            }
                        }
                    } else {
                        sql = "SELECT SPC FROM G_SPC WHERE GPC='" + authSPCCode[i] + "' AND OFF_CODE='" + offcode + "' AND (IFUCLEAN='N' OR IFUCLEAN IS NULL)";
                        rs1 = stmt1.executeQuery(sql);
                        while (rs1.next()) {
                            if (rs1.getString("SPC") != null && !rs1.getString("SPC").equals("")) {
                                boolean isDuplicateAuthority = isDuplicateInWorkflowAuthority(applicantspc, rs1.getString("SPC"), deptcode);
                                if (isDuplicateAuthority == false) {
                                    pst.setString(1, applicantspc);
                                    pst.setString(2, rs1.getString("SPC"));
                                    pst.setInt(3, processid);
                                    pst.setString(4, "");
                                    pst.executeUpdate();
                                }
                            }
                        }
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, stmt);
            DataBaseFunctions.closeSqlObjects(rs1, stmt1);
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    @Override
    public List getGPCListOfficeWise(String deptcode) {
        Connection con = null;

        PreparedStatement ps = null;
        ResultSet rs = null;
        List gpclist = new ArrayList();
        GPost gp = null;
        try {
            con = dataSource.getConnection();
            String sql = "select post_code,post from g_post where department_code=? order by post";
            ps = con.prepareStatement(sql);
            ps.setString(1, deptcode);
            rs = ps.executeQuery();
            while (rs.next()) {
                gp = new GPost();
                gp.setPostcode(rs.getString("post_code"));
                gp.setPost(rs.getString("post"));
                gpclist.add(gp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return gpclist;
    }

    @Override
    public List getPostListGPCWiseAuthority(String gpc, String offcode) {

        Connection con = null;

        PreparedStatement ps = null;
        ResultSet rs = null;
        List postlist = new ArrayList();
        SubstantivePost sp = null;
        try {
            con = dataSource.getConnection();
            String sql = "SELECT SPC,SPN FROM G_SPC WHERE gpc=? and off_code=? and ifuclean='N' ORDER BY SPN";
            ps = con.prepareStatement(sql);
            ps.setString(1, gpc);
            ps.setString(2, offcode);
            rs = ps.executeQuery();
            while (rs.next()) {
                sp = new SubstantivePost();
                sp.setSpc(rs.getString("SPC"));
                sp.setSpn(rs.getString("SPN"));
                postlist.add(sp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return postlist;
    }

    @Override
    public List getPostListWithoutAuthority(String offcode) {

        Connection con = null;

        Statement stmt = null;
        ResultSet rs = null;
        List postlist = new ArrayList();
        SubstantivePost sp = null;
        try {
            con = dataSource.getConnection();
            stmt = con.createStatement();
            String sql = "SELECT SPC,SPN FROM G_SPC WHERE OFF_CODE='" + offcode + "' ORDER BY SPN";
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                sp = new SubstantivePost();
                sp.setSpc(rs.getString("SPC"));
                sp.setSpn(rs.getString("SPN"));
                postlist.add(sp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, stmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return postlist;
    }

    private boolean isDuplicateInWorkflowAuthority(String applicantSpc, String authoritySpc, String deptCode) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        boolean isDuplicate = false;

        try {
            con = this.dataSource.getConnection();

            String sql = "";

            if (deptCode != null && deptCode.equals("LM")) {
                sql = "SELECT * FROM WORKFLOW_AUTHORITY WHERE PROCESS_ID=? AND APPLICANT_SPC=? AND AUTHORITY_SPC=? AND AUTHORITY_TYPE=?";
                pst = con.prepareStatement(sql);
                pst.setInt(1, 3);
                pst.setString(2, applicantSpc);
                pst.setString(3, authoritySpc);
                pst.setString(4, "LM");
            } else {
                sql = "SELECT * FROM WORKFLOW_AUTHORITY WHERE PROCESS_ID=? AND APPLICANT_SPC=? AND AUTHORITY_SPC=? AND AUTHORITY_TYPE!=?";
                pst = con.prepareStatement(sql);
                pst.setInt(1, 3);
                pst.setString(2, applicantSpc);
                pst.setString(3, authoritySpc);
                pst.setString(4, "LM");
            }
            rs = pst.executeQuery();
            if (rs.next()) {
                isDuplicate = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return isDuplicate;
    }

}
