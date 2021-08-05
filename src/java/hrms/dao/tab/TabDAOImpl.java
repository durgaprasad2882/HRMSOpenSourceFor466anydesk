/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.tab;

import hrms.common.CommonFunctions;
import hrms.common.DataBaseFunctions;
import hrms.model.login.UserExpertise;
import hrms.model.tab.EmployeeTree;
import hrms.model.tab.OfficeHelper;
import hrms.model.tab.OfficeTreeAttr;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Surendra
 */
public class TabDAOImpl implements TabDAO {

    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List getOfficeListXML(String empid) {
        Connection con = null;
        List li = new ArrayList();
        OfficeTreeAttr ofattr = null;
        OfficeHelper ofh = null;
        Statement st = null;
        ResultSet rs = null;
        String officeName = "";
        try {
            con = this.dataSource.getConnection();
            st = con.createStatement();
            ArrayList currentOffices = getCurrentOffice(con, empid);

            for (int l = 0; l < currentOffices.size(); l++) {
                ArrayList regList = new ArrayList();

                ofattr = new OfficeTreeAttr();
                ofh = (OfficeHelper) currentOffices.get(l);

                EmployeeTree regular = new EmployeeTree();
                regular.setId("REG" + CommonFunctions.encodedTxt(ofh.getPostCode()));
                regular.setText("Regular");
                regular.setState("open");
                regular.setIconCls("office.png");
                regular.setChildren(getEmployeeList(ofh.getOfficeCode(), empid));

                regList.add(regular);

                ArrayList al = getContractualEmployeeList(ofh.getOfficeCode(), empid);
                if (al.size() > 0) {

                    EmployeeTree contractual = new EmployeeTree();
                    contractual.setId("CON" + CommonFunctions.encodedTxt(ofh.getPostCode()));
                    contractual.setText("Contractual");
                    contractual.setState("open");
                    contractual.setIconCls("office.png");
                    contractual.setChildren(al);

                    regList.add(contractual);
                }

                officeName = ofh.getOfficeName();
                ofattr.setId("PA" + CommonFunctions.encodedTxt(ofh.getPostCode()));
                ofattr.setText(officeName);
                ofattr.setState("open");
                ofattr.setIconCls("office.png");
                ofattr.setChildren(regList);

                li.add(ofattr);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }

        return li;

    }

    @Override
    public ArrayList getSubOfficeListXML(String empid) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public ArrayList getContractualEmployeeList(String parentOfficeCode, String empId) {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        ArrayList templist = new ArrayList();
        EmployeeTree empTree = new EmployeeTree();
        try {
            con = this.dataSource.getConnection();
            ps = con.prepareStatement("SELECT EMP_ID,(F_NAME || ' ' || M_NAME || ' ' || L_NAME) FULL_NAME,F_NAME,POST_NOMENCLATURE FROM EMP_MAST WHERE CUR_OFF_CODE=? AND IS_REGULAR ='N' ORDER BY F_NAME");
            ps.setString(1, parentOfficeCode);
            rs = ps.executeQuery();
            while (rs.next()) {
                empTree = new EmployeeTree();
                empTree.setId(CommonFunctions.encodedTxt(rs.getString("EMP_ID")));
                if (rs.getString("FULL_NAME") != null && !rs.getString("FULL_NAME").equals("")) {
                    empTree.setText(rs.getString("FULL_NAME").toUpperCase() + " , " + StringUtils.defaultString(rs.getString("POST_NOMENCLATURE")).replaceAll("&", " AND "));
                } else {
                    empTree.setText(rs.getString("FULL_NAME") + " , " + StringUtils.defaultString(rs.getString("POST_NOMENCLATURE")).replaceAll("&", " AND "));
                }
                empTree.setIconCls("icon-onDuty");
                empTree.setState("close");
                templist.add(empTree);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return templist;
    }

    public ArrayList getEmployeeList(String parentOfficeCode, String empId) throws Exception {
        Connection con = null;
        ArrayList al = new ArrayList();
        try {
            con = this.dataSource.getConnection();

            Statement stamt = con.createStatement();
            Statement stamt2 = con.createStatement();

            ResultSet rs2 = null;
            String biigrpId = "";
            String sqlQuery = "";

            boolean separateBillMapped = false;
            stamt2 = con.createStatement();

            rs2 = stamt2.executeQuery("SELECT BILL_GRP_ID FROM BILL_GROUP_PRIVILAGE WHERE SPC=(SELECT CUR_SPC FROM EMP_MAST WHERE EMP_ID='" + empId + "')");
            while (rs2.next()) {
                separateBillMapped = true;
                biigrpId = rs2.getString("BILL_GRP_ID");

                if (biigrpId != null && !biigrpId.equals("")) {
                    sqlQuery = "SELECT * FROM ( "
                            + "SELECT SPC,POST_LEVEL,SPN,DEP_CODE,ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME,L_NAME], ' ') FULLNAME,F_NAME,EMP_ID,POST,BRASS_NO FROM ( "
                            + "SELECT G_SPC.SPC,POST_LEVEL,SPN,GPC FROM ( "
                            + "SELECT SPC FROM  ( "
                            + "SELECT SECTION_ID FROM BILL_SECTION_MAPPING  WHERE BILL_GROUP_ID ='" + biigrpId + "') BILL_SECTION_MAPPING "
                            + "INNER JOIN SECTION_POST_MAPPING ON BILL_SECTION_MAPPING.SECTION_ID=SECTION_POST_MAPPING.SECTION_ID) SECTION_POST_MAPPING "
                            + "INNER JOIN (SELECT SPC,POST_LEVEL,SPN,GPC FROM G_SPC WHERE OFF_CODE = '" + parentOfficeCode + "' AND (IFUCLEAN!='Y' OR IFUCLEAN IS NULL) )G_SPC ON SECTION_POST_MAPPING.SPC=G_SPC.SPC ) G_SPC "
                            + "LEFT OUTER JOIN EMP_MAST ON G_SPC.SPC = EMP_MAST.CUR_SPC  "
                            + "LEFT OUTER JOIN G_POST ON G_SPC.GPC = G_POST.POST_CODE "
                            + "UNION "
                            + "SELECT '' SPC,0 POST_LEVEL,'' SPN,DEP_CODE,ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME,L_NAME], ' ') FULLNAME,F_NAME,EMP_ID,'' POST,BRASS_NO FROM EMP_MAST "
                            + "WHERE (CUR_OFF_CODE = '" + parentOfficeCode + "'  OR NEXT_OFFICE_CODE='" + parentOfficeCode + "') AND CUR_SPC IS NULL AND IF_RETIRED IS NULL  "
                            + ") EMP_MAST ORDER BY F_NAME";

                    ResultSet res = stamt.executeQuery(sqlQuery);
                    EmployeeTree empTree = null;
                    while (res.next()) {
                        empTree = new EmployeeTree();
                        empTree.setId(CommonFunctions.encodedTxt(res.getString("EMP_ID")));
                        if (res.getString("BRASS_NO") != null && !res.getString("BRASS_NO").equals("")) {
                            empTree.setText(res.getString("BRASS_NO") + ", " + res.getString("FULLNAME").toUpperCase() + " , " + StringUtils.defaultString(res.getString("POST")).replaceAll("&", " AND "));
                        } else {
                            empTree.setText(res.getString("FULLNAME").toUpperCase() + " , " + StringUtils.defaultString(res.getString("POST")).replaceAll("&", " AND "));
                        }

                        if (res.getString("DEP_CODE") != null && !res.getString("DEP_CODE").equals("")) {
                            if (res.getString("DEP_CODE").equalsIgnoreCase("00")) { // TERMINATED
                                empTree.setIconCls("icon-terminate");
                            } else if (res.getString("DEP_CODE").equalsIgnoreCase("01")) { // LONG TERM LEAVE
                                empTree.setIconCls("icon-onltLeave");
                            } else if (res.getString("DEP_CODE").equalsIgnoreCase("02")) {// ON DUTY
                                empTree.setIconCls("icon-onDuty");
                            } else if (res.getString("DEP_CODE").equalsIgnoreCase("03")) {// WAITING FOR POSTING
                                empTree.setIconCls("icon-watingforpost");
                            } else if (res.getString("DEP_CODE").equalsIgnoreCase("04")) {// ON TRAINING
                                empTree.setIconCls("icon-lttrain");
                            } else if (res.getString("DEP_CODE").equalsIgnoreCase("05")) {// UNDER SUSPENSION
                                empTree.setIconCls("icon-suspension");
                            } else if (res.getString("DEP_CODE").equalsIgnoreCase("06")) {// ON TRANSIT
                                empTree.setIconCls("icon-ontransit");
                            } else if (res.getString("DEP_CODE").equalsIgnoreCase("07")) {// ON DEPUTATION
                                empTree.setIconCls("icon-ondeputation");
                            } else if (res.getString("DEP_CODE").equalsIgnoreCase("08")) {// RESIGNED
                                empTree.setIconCls("icon-resigned");
                            } else if (res.getString("DEP_CODE").equalsIgnoreCase("09")) {// SUPERANNUATED
                                empTree.setIconCls("icon-retired");
                            } else if (res.getString("DEP_CODE").equalsIgnoreCase("10")) {// DECEASED
                                empTree.setIconCls("icon-retired");
                            } else if (res.getString("DEP_CODE").equalsIgnoreCase("11")) {// PAY HELDUP 
                                empTree.setIconCls("icon-suspension");
                            } else if (res.getString("DEP_CODE").equalsIgnoreCase("12")) {// ON REDEPLOYMENT
                                empTree.setIconCls("icon-onDuty");
                            }

                        } else {
                            empTree.setIconCls("icon-watingforpost");
                        }
                        empTree.setState("close");
                        al.add(empTree);
                    }
                }

            }
            if (separateBillMapped == false) {
                sqlQuery = " SELECT SPC,POST_LEVEL,SPN,DEP_CODE,ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME,L_NAME], ' ') FULLNAME,F_NAME,EMP_ID,IS_REGULAR,BRASS_NO,POST FROM(SELECT SPC,POST_LEVEL,SPN,GPC FROM G_SPC WHERE OFF_CODE = '" + parentOfficeCode + "' AND (IFUCLEAN!='Y' OR IFUCLEAN IS NULL) )G_SPC "
                        + "  LEFT OUTER JOIN EMP_MAST ON G_SPC.SPC = EMP_MAST.CUR_SPC "
                        + "  LEFT OUTER JOIN G_POST ON G_SPC.GPC = G_POST.POST_CODE WHERE EMP_ID IS NOT NULL"
                        + "  UNION "
                        + "  SELECT * FROM (SELECT '' SPC,0 POST_LEVEL,'' SPN,DEP_CODE,ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME,L_NAME], ' ') FULLNAME,F_NAME,EMP_ID,IS_REGULAR,BRASS_NO,'' POST FROM EMP_MAST WHERE (CUR_OFF_CODE = '" + parentOfficeCode + "'  OR NEXT_OFFICE_CODE='" + parentOfficeCode + "') AND IS_REGULAR!='N' AND CUR_SPC IS NULL AND IF_RETIRED IS NULL ) EMP_MAST ORDER BY F_NAME";

                ResultSet res = stamt.executeQuery(sqlQuery);
                EmployeeTree empTree = null;
                while (res.next()) {
                    empTree = new EmployeeTree();
                    empTree.setId(CommonFunctions.encodedTxt(res.getString("EMP_ID")));
                    if (res.getString("BRASS_NO") != null && !res.getString("BRASS_NO").equals("")) {
                        empTree.setText(res.getString("BRASS_NO") + ", " + StringUtils.defaultString(res.getString("FULLNAME")).toUpperCase() + " " + StringUtils.defaultString(res.getString("POST")).replaceAll("&", " AND "));
                    } else {
                        empTree.setText(StringUtils.defaultString(res.getString("FULLNAME")).toUpperCase() + " , " + StringUtils.defaultString(res.getString("POST")).replaceAll("&", " AND "));
                    }
                    if (res.getString("DEP_CODE") != null && !res.getString("DEP_CODE").equals("")) {
                        if (res.getString("DEP_CODE").equalsIgnoreCase("00")) { // TERMINATED
                            empTree.setIconCls("icon-terminate");
                        } else if (res.getString("DEP_CODE").equalsIgnoreCase("01")) { // LONG TERM LEAVE
                            empTree.setIconCls("icon-onltLeave");
                        } else if (res.getString("DEP_CODE").equalsIgnoreCase("02")) {// ON DUTY
                            empTree.setIconCls("icon-onDuty");
                        } else if (res.getString("DEP_CODE").equalsIgnoreCase("03")) {// WAITING FOR POSTING
                            empTree.setIconCls("icon-watingforpost");
                        } else if (res.getString("DEP_CODE").equalsIgnoreCase("04")) {// ON TRAINING
                            empTree.setIconCls("icon-lttrain");
                        } else if (res.getString("DEP_CODE").equalsIgnoreCase("05")) {// UNDER SUSPENSION
                            empTree.setIconCls("icon-suspension");
                        } else if (res.getString("DEP_CODE").equalsIgnoreCase("06")) {// ON TRANSIT
                            empTree.setIconCls("icon-ontransit");
                        } else if (res.getString("DEP_CODE").equalsIgnoreCase("07")) {// ON DEPUTATION
                            empTree.setIconCls("icon-ondeputation");
                        } else if (res.getString("DEP_CODE").equalsIgnoreCase("08")) {// RESIGNED
                            empTree.setIconCls("icon-resigned");
                        } else if (res.getString("DEP_CODE").equalsIgnoreCase("09")) {// SUPERANNUATED
                            empTree.setIconCls("icon-retired");
                        } else if (res.getString("DEP_CODE").equalsIgnoreCase("10")) {// DECEASED
                            empTree.setIconCls("icon-retired");
                        } else if (res.getString("DEP_CODE").equalsIgnoreCase("11")) {// PAY HELDUP 
                            empTree.setIconCls("icon-suspension");
                        } else if (res.getString("DEP_CODE").equalsIgnoreCase("12")) {// ON REDEPLOYMENT
                            empTree.setIconCls("icon-onDuty");
                        }
                    } else {
                        empTree.setIconCls("icon-watingforpost");
                    }
                    empTree.setState("close");
                    al.add(empTree);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return al;
    }

    public static ArrayList getCurrentOffice(Connection con, String empid) throws Exception {
        PreparedStatement pstamt = con.prepareStatement("SELECT CUR_SPC,CUR_OFF_CODE,OFF_EN FROM ( "
                + "SELECT 1 SLNO,CUR_SPC,CUR_OFF_CODE,OFF_EN FROM(SELECT CUR_SPC,CUR_OFF_CODE FROM EMP_MAST WHERE EMP_ID=? ) "
                + "EMP_MAST LEFT OUTER JOIN G_OFFICE ON EMP_MAST.CUR_OFF_CODE=G_OFFICE.OFF_CODE "
                + "UNION "
                + "SELECT 2 SLNO,SPC CUR_SPC, AUTH_OFF CUR_OFF_CODE,OFF_EN FROM ( "
                + "select AUTH_OFF, SPC from emp_join where  EMP_ID=?  AND IF_AD_CHARGE='Y' "
                + "AND JOIN_ID NOT IN (SELECT EMP_ID FROM EMP_RELIEVE WHERE EMP_ID=?)) EMP_JOIN "
                + "LEFT OUTER JOIN G_OFFICE ON EMP_JOIN.AUTH_OFF=G_OFFICE.OFF_CODE) office_tree ORDER BY SLNO,OFF_EN");
        pstamt.setString(1, empid);
        pstamt.setString(2, empid);
        pstamt.setString(3, empid);
        ResultSet res = pstamt.executeQuery();
        OfficeHelper ofh = null;
        ArrayList offlist = new ArrayList();
        while (res.next()) {
            ofh = new OfficeHelper();
            ofh.setPostCode(res.getString("CUR_SPC"));
            ofh.setOfficeCode(res.getString("CUR_OFF_CODE"));
            if (res.getString("OFF_EN") != null && !res.getString("OFF_EN").equals("")) {
                ofh.setOfficeName(res.getString("OFF_EN").replaceAll("&", " AND "));
            }
            offlist.add(ofh);

        }
        return offlist;
    }

    @Override
    public String saveExpertise(UserExpertise ue) {
        Connection con = null;

        PreparedStatement pst = null;
        Statement st = null;
        ResultSet rs = null;
        Calendar cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");
        String startTime = dateFormat.format(cal.getTime());

        String msg = "Success";
        try {
            con = this.dataSource.getConnection();
            st = con.createStatement();
            pst = con.prepareStatement("INSERT INTO g_emp_expertise(emp_id, emp_name, designation, grade, department"
                    + ", current_place_of_posting, office_stationed, area_of_expertise, area_of_interest, willingness_to_serve"
                    + ", mobile, landline_number, office_phone_number, email_id, date_updated)"
                    + " Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            pst.setString(1, ue.getEmpid());
            pst.setString(2, ue.getName());
            pst.setString(3, ue.getDesignation());
            pst.setString(4, ue.getGrade());
            pst.setString(5, ue.getDeptname());
            pst.setString(6, ue.getPostingPlace());
            pst.setString(7, ue.getCurofficename());
            pst.setString(8, ue.getAreaOfExpertise());
            pst.setString(9, ue.getAreaOfInterest());
            pst.setString(10, ue.getVolWillingness());
            pst.setString(11, ue.getMobile());
            pst.setString(12, ue.getLandline());
            pst.setString(13, ue.getOfficePhone());
            pst.setString(14, ue.getEmailid());
            pst.setTimestamp(15, new Timestamp(dateFormat.parse(startTime).getTime()));

            pst.executeUpdate();
        } catch (Exception e) {
            msg = "Error";
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, st);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return msg;
    }

    @Override
    public OfficeTreeAttr getHODOfficeListXML(String parentOfficeCode) {
        Connection con = null;
        ArrayList childlist = new ArrayList();
        PreparedStatement pstmt = null;
        ResultSet res = null;
        OfficeTreeAttr ofattr = new OfficeTreeAttr();
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT OFF_CODE,OFF_EN FROM G_OFFICE WHERE OFF_CODE=?");
            pstmt.setString(1, parentOfficeCode);
            res = pstmt.executeQuery();
            if (res.next()) {
                ofattr.setId(CommonFunctions.encodedTxt(res.getString("OFF_CODE")));
                ofattr.setText(res.getString("OFF_EN"));
                ofattr.setState("open");
                ofattr.setIconCls("icon-office");
            }
            pstmt = con.prepareStatement("SELECT OFF_CODE,OFF_EN FROM G_OFFICE WHERE P_OFF_CODE=? ORDER BY OFF_EN");
            pstmt.setString(1, parentOfficeCode);
            res = pstmt.executeQuery();
            while (res.next()) {
                OfficeTreeAttr childOfattr = new OfficeTreeAttr();
                childOfattr.setId(res.getString("OFF_CODE"));
                childOfattr.setText(res.getString("OFF_EN"));
                childOfattr.setIconCls("icon-office");
                childlist.add(childOfattr);
            }
            ofattr.setChildren(childlist);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return ofattr;
    }

}
