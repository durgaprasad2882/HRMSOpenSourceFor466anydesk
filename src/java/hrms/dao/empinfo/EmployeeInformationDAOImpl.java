package hrms.dao.empinfo;

import hrms.common.CommonFunctions;
import hrms.common.DataBaseFunctions;
import hrms.model.empinfo.EmployeeInformation;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Resource;
import javax.sql.DataSource;

public class EmployeeInformationDAOImpl implements EmployeeInformationDAO {

    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public EmployeeInformation getEmployeeData(String empid, String gpf) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        EmployeeInformation empinfo = new EmployeeInformation();
        String param = "";
        try {
            con = dataSource.getConnection();
            String sql = "SELECT EMP_ID,GPF_NO,FULL_NAME,MOBILE,SPN,CADRE_NAME,CUR_CADRE_CODE FROM"
                    + " (SELECT EMP_ID,ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME,L_NAME], ' ') FULL_NAME,MOBILE,GPF_NO,CUR_SPC,CUR_CADRE_CODE FROM EMP_MAST WHERE";
            if (empid != null && !empid.equals("")) {
                sql = sql + " EMP_ID=?";
            } else if (gpf != null && !gpf.equals("")) {
                sql = sql + " GPF_NO=?";
            }
            sql = sql + ")EMP_MAST LEFT OUTER JOIN G_SPC ON EMP_MAST.CUR_SPC=G_SPC.SPC"
                    + " LEFT OUTER JOIN G_CADRE ON EMP_MAST.CUR_CADRE_CODE=G_CADRE.CADRE_CODE";
            //System.out.println("SQL is: " + sql);
            pst = con.prepareStatement(sql);
            if (empid != null && !empid.equals("")) {
                param = empid;
            } else if (gpf != null && !gpf.equals("")) {
                param = gpf;
            }
            pst.setString(1, param);
            rs = pst.executeQuery();
            if (rs.next()) {
                empinfo.setEmpid(rs.getString("EMP_ID"));
                empinfo.setGpfno(rs.getString("GPF_NO"));
                empinfo.setEmpname(rs.getString("FULL_NAME"));
                empinfo.setEmpdesg(rs.getString("SPN"));
                empinfo.setCadreCode(rs.getString("CUR_CADRE_CODE"));
                empinfo.setCadreName(rs.getString("CADRE_NAME"));
                empinfo.setMobile(rs.getString("MOBILE"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return empinfo;
    }

    @Override
    public void updateEmployeeData(EmployeeInformation empinfo) {

        Connection conpsql = null;
        Connection conora = null;

        PreparedStatement pst = null;
        String sql = "";
        try {
            conpsql = this.dataSource.getConnection();

            if (empinfo.getCadreCode() != null && !empinfo.getCadreCode().equals("")) {
                sql = "UPDATE EMP_MAST SET CUR_CADRE_CODE=? WHERE EMP_ID=?";
                pst = conpsql.prepareStatement(sql);
                pst.setString(1, empinfo.getCadreCode());
                pst.setString(2, empinfo.getEmpid());
                pst.executeUpdate();
            }
            if (empinfo.getMobile() != null && !empinfo.getMobile().equals("")) {
                sql = "UPDATE EMP_MAST SET MOBILE=? WHERE EMP_ID=?";
                pst = conpsql.prepareStatement(sql);
                pst.setString(1, empinfo.getMobile());
                pst.setString(2, empinfo.getEmpid());
                pst.executeUpdate();
            }
            
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conora = DriverManager.getConnection("jdbc:oracle:thin:@172.16.1.12:1522:orcl", "hrmis", "hrmis");
            
            if (empinfo.getCadreCode() != null && !empinfo.getCadreCode().equals("")) {
                sql = "UPDATE EMP_MAST SET CUR_CADRE_CODE=? WHERE EMP_ID=?";
                pst = conora.prepareStatement(sql);
                pst.setString(1, empinfo.getCadreCode());
                pst.setString(2, empinfo.getEmpid());
                pst.executeUpdate();
            }
            if (empinfo.getMobile() != null && !empinfo.getMobile().equals("")) {
                sql = "UPDATE EMP_MAST SET MOBILE=? WHERE EMP_ID=?";
                pst = conora.prepareStatement(sql);
                pst.setString(1, empinfo.getMobile());
                pst.setString(2, empinfo.getEmpid());
                pst.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(conpsql);
            DataBaseFunctions.closeSqlObjects(conora);
        }
    }
    
    @Override
    public boolean isupdatePayOrPostingInfo(String empId, String wefDate, String ordDate, String updateType) {
        
        Connection conpsql = null;
        
        boolean update = false;
        Statement st = null;
        ResultSet rs = null;
        Date wefCdate = null;
        Date empWefDatepayCdate = null;
        Date empWefdate = null;
        Date ordCdate = null;
        Date empOrdDate = null;
        Date sysDate = null;
        String dateform = "";
        Date dt = new Date();
        DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            conpsql = this.dataSource.getConnection();
            
            st = conpsql.createStatement();
            if (updateType != null && !updateType.equals("")) {
                if (updateType.equalsIgnoreCase("PAY")) {
                    String empmastOrddate = "";
                    String empmastWefDate = "";
                    rs = st.executeQuery("SELECT PAY_DATE, ST_DATE_OF_CUR_SALARY FROM EMP_MAST WHERE EMP_ID='" + empId + "'");
                    if (rs.next()) {
                        if (rs.getString("ST_DATE_OF_CUR_SALARY") != null && !rs.getString("ST_DATE_OF_CUR_SALARY").equals("")) {
                            empmastOrddate = CommonFunctions.getFormattedOutputDate1(rs.getDate("ST_DATE_OF_CUR_SALARY"));
                            if ((ordDate != null && !ordDate.equals("")) && (wefDate != null && !wefDate.equals(""))) {
                                ordCdate = (Date) formatter.parse(ordDate);
                                if (empmastOrddate != null && !empmastOrddate.equals("")) {
                                    empOrdDate = (Date) formatter.parse(empmastOrddate);
                                }
                                if (dt != null) {
                                    dateform = formatter.format(dt);
                                    sysDate = (Date) formatter.parse(dateform);
                                }
                                if (rs.getString("PAY_DATE") != null && !rs.getString("PAY_DATE").equals("")) {
                                    empmastWefDate = CommonFunctions.getFormattedOutputDate1(rs.getDate("PAY_DATE"));
                                    if (wefDate != null && !wefDate.equals("")) {
                                        wefCdate = (Date) formatter.parse(wefDate);
                                    }
                                    if (empmastWefDate != null && !empmastWefDate.equals("")) {
                                        empWefDatepayCdate = (Date) formatter.parse(empmastWefDate);
                                    }

                                    if (ordCdate.compareTo(empOrdDate) < 0 && wefCdate.compareTo(empWefDatepayCdate) < 0) {
                                        update = false; // both order date and wef date are smaller
                                    } else if (ordCdate.compareTo(empOrdDate) <= 0 && wefCdate.compareTo(empWefDatepayCdate) >= 0 && sysDate.compareTo(wefCdate) >= 0) {
                                        update = true; // order date is smaller and wef date is greater and system date is greater than wef date
                                    } else if (ordCdate.compareTo(empOrdDate) >= 0 && wefCdate.compareTo(empWefDatepayCdate) <= 0) {
                                        update = true; // order date is greater and wef date is smaller 
                                    } else if (ordCdate.compareTo(empOrdDate) >= 0 && wefCdate.compareTo(empWefDatepayCdate) >= 0) {
                                        //&& sysDate.compareTo(wefCdate)>=0
                                        update = true; // order date is greater and wef date is greater but not wef date is greater than sysdate
                                    }

                                } else {
                                    update = true;
                                }
                            } else {
                                update = false;
                            }
                        } else {
                            update = true;
                        }
                    } else {
                        update = true;
                        //no record found inside emp_mast table
                    }
                } else if (updateType.equalsIgnoreCase("POSTING")) {
                    String empmastOrdDate = "";
                    String empmastWefdate = "";
                    rs = st.executeQuery("SELECT POST_ORDER_DATE, CURR_POST_DOJ FROM EMP_MAST WHERE EMP_ID='" + empId + "'");
                    if (rs.next()) {
                        if (rs.getString("CURR_POST_DOJ") != null && !rs.getString("CURR_POST_DOJ").equals("")) {
                            empmastOrdDate = CommonFunctions.getFormattedOutputDate1(rs.getDate("CURR_POST_DOJ"));
                            if ((ordDate != null && !ordDate.equals("")) && (wefDate != null && !wefDate.equals(""))) {
                                ordCdate = (Date) formatter.parse(ordDate);
                                if (empmastOrdDate != null && !empmastOrdDate.equals("")) {
                                    empOrdDate = (Date) formatter.parse(empmastOrdDate);
                                }
                                if (dt != null) {
                                    dateform = formatter.format(dt);
                                    sysDate = (Date) formatter.parse(dateform);
                                }
                                if (rs.getString("POST_ORDER_DATE") != null && !rs.getString("POST_ORDER_DATE").equals("")) {
                                    empmastWefdate = CommonFunctions.getFormattedOutputDate1(rs.getDate("POST_ORDER_DATE"));
                                    if (wefDate != null && !wefDate.equals("")) {
                                        wefCdate = (Date) formatter.parse(wefDate);
                                    }
                                    if (empmastWefdate != null && !empmastWefdate.equals("")) {
                                        empWefdate = (Date) formatter.parse(empmastWefdate);
                                    }

                                    if (ordCdate.compareTo(empOrdDate) < 0 && wefCdate.compareTo(empWefdate) < 0) {
                                        update = false; // both order date and wef date are smaller
                                    } else if (ordCdate.compareTo(empOrdDate) <= 0 && wefCdate.compareTo(empWefdate) >= 0 && sysDate.compareTo(wefCdate) >= 0) {
                                        update = true; // order date is smaller and wef date is greater and system date is greater than wef date
                                    } else if (ordCdate.compareTo(empOrdDate) >= 0 && wefCdate.compareTo(empWefdate) <= 0) {
                                        update = true; // order date is greater and wef date is smaller 
                                    } else if (ordCdate.compareTo(empOrdDate) >= 0 && wefCdate.compareTo(empWefdate) >= 0) {
                                        //&& sysDate.compareTo(wefCdate)>=0
                                        update = true; // order date is greater and wef date is greater but not wef date is greater than sysdate
                                    }
                                } else {
                                    update = true;
                                }
                            } else {
                                update = false;
                            }
                        } else {
                            update = true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, st);
            DataBaseFunctions.closeSqlObjects(conpsql);
        }
        return update;
        
    }

    @Override
    public void updateEmpPayInfoOnDate(EmployeeInformation ei,String empid) {
        
        Connection conpsql = null;
        
        PreparedStatement pst = null;
        
        try{
            conpsql = this.dataSource.getConnection();
            //System.out.println("CUR_SALARY is: "+ei.getPaydate());
            String sql = "UPDATE EMP_MAST SET PAY_DATE=?,CUR_SALARY=?,CUR_BASIC_SALARY=?,GP=?,SPAY=?,PPAY=?,OTHPAY=? WHERE EMP_ID=?";
            pst = conpsql.prepareStatement(sql);
            pst.setTimestamp(1, new Timestamp(new SimpleDateFormat("dd-MMM-yyyy").parse(ei.getPaydate()).getTime()));
            pst.setString(2,ei.getPayscale());
            pst.setDouble(3,ei.getBasic());
            pst.setDouble(4,ei.getGp());
            pst.setInt(5,ei.getSpay());
            pst.setInt(6,ei.getPpay());
            pst.setInt(7,ei.getOthpay());
            pst.setString(8,empid);
            pst.executeUpdate();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(conpsql);
        }
    }
}
