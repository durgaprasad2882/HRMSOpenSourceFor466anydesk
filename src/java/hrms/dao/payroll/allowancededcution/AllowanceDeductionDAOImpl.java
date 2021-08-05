/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.payroll.allowancededcution;

import hrms.common.DataBaseFunctions;
import hrms.common.Message;
import hrms.model.employee.PayComponent;
import hrms.model.payroll.allowancededcution.AllowanceDeduction;
import hrms.model.payroll.aqdtls.AqDtlsModel;
import hrms.model.payroll.aqmast.AqmastModel;
import hrms.model.payroll.billbrowser.SectionDtlSPCWiseEmp;
import hrms.model.payroll.billmast.BillMastModel;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 *
 * @author Manas Jena
 */
public class AllowanceDeductionDAOImpl implements AllowanceDeductionDAO {

    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public class ADOBJECT {

        private int adamt;
        private String btid;

        public int getAdamt() {
            return adamt;
        }

        public void setAdamt(int adamt) {
            this.adamt = adamt;
        }

        public String getBtid() {
            return btid;
        }

        public void setBtid(String btid) {
            this.btid = btid;
        }
    }

    @Override
    public ArrayList getEmployeeWiseAllowance(String empId) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList allowanceList = new ArrayList();
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT G_AD_LIST.ad_code,ad_desc,G_AD_LIST.ad_formula as O_Formula,UPDATE_AD_INFO.ad_formula as U_Formula,G_AD_LIST.fixedvalue as O_VALUE,UPDATE_AD_INFO.fixedvalue as U_VALUE,AD_CODE_NAME FROM G_AD_LIST "
                    + "LEFT OUTER JOIN (SELECT * FROM UPDATE_AD_INFO WHERE where_updated = 'E' AND UPDATION_REF_CODE=?)UPDATE_AD_INFO ON G_AD_LIST.AD_CODE=UPDATE_AD_INFO.REF_AD_CODE "
                    + "WHERE AD_TYPE='A'");
            pstmt.setString(1, empId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                AllowanceDeduction allowance = new AllowanceDeduction();
                allowance.setAdcode(rs.getString("ad_code"));
                allowance.setAddesc(rs.getString("ad_desc"));
                allowance.setAdcodename(rs.getString("AD_CODE_NAME"));
                if (rs.getString("U_Formula") != null) {
                    allowance.setFormula(rs.getString("U_Formula"));
                } else {
                    allowance.setFormula(rs.getString("O_Formula"));
                }
                if (rs.getString("U_VALUE") != null) {
                    allowance.setAdvalue(rs.getInt("U_VALUE"));
                } else {
                    allowance.setAdvalue(rs.getInt("O_VALUE"));
                }
                allowanceList.add(allowance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return allowanceList;
    }

    @Override
    public ArrayList getEmployeeWiseDeduction(String empId) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList deductionList = new ArrayList();
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT G_AD_LIST.ad_code,ad_desc,G_AD_LIST.ad_formula as O_Formula,UPDATE_AD_INFO.ad_formula as U_Formula,G_AD_LIST.fixedvalue as O_VALUE,UPDATE_AD_INFO.fixedvalue as U_VALUE,AD_CODE_NAME FROM G_AD_LIST "
                    + "LEFT OUTER JOIN (SELECT * FROM UPDATE_AD_INFO WHERE where_updated = 'E' AND UPDATION_REF_CODE=?)UPDATE_AD_INFO ON G_AD_LIST.AD_CODE=UPDATE_AD_INFO.REF_AD_CODE "
                    + "WHERE AD_TYPE='D' AND IS_PRIVATE_DEDN='N' ");
            pstmt.setString(1, empId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                AllowanceDeduction deduction = new AllowanceDeduction();
                deduction.setAdcode(rs.getString("ad_code"));
                deduction.setAddesc(rs.getString("ad_desc"));
                deduction.setAdcodename(rs.getString("AD_CODE_NAME"));
                if (rs.getString("U_Formula") != null) {
                    deduction.setFormula(rs.getString("U_Formula"));
                } else {
                    deduction.setFormula(rs.getString("O_Formula"));
                }
                if (rs.getString("U_VALUE") != null) {
                    deduction.setAdvalue(rs.getInt("U_VALUE"));
                } else {
                    deduction.setAdvalue(rs.getInt("O_VALUE"));
                }
                deductionList.add(deduction);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return deductionList;
    }

    @Override
    public ArrayList getEmployeeWisePvtDeduction(String empId) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList pvtDeductionList = new ArrayList();
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT G_AD_LIST.ad_code,ad_desc,G_AD_LIST.ad_formula as O_Formula,UPDATE_AD_INFO.ad_formula as U_Formula,G_AD_LIST.fixedvalue as O_VALUE,UPDATE_AD_INFO.fixedvalue as U_VALUE,AD_CODE_NAME FROM G_AD_LIST "
                    + "LEFT OUTER JOIN (SELECT * FROM UPDATE_AD_INFO WHERE where_updated = 'E' AND UPDATION_REF_CODE=?)UPDATE_AD_INFO ON G_AD_LIST.AD_CODE=UPDATE_AD_INFO.REF_AD_CODE "
                    + "WHERE AD_TYPE='D' AND IS_PRIVATE_DEDN='Y' ");
            pstmt.setString(1, empId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                AllowanceDeduction pvtdeduction = new AllowanceDeduction();

                pvtdeduction.setAdcode(rs.getString("ad_code"));
                pvtdeduction.setAddesc(rs.getString("ad_desc"));
                pvtdeduction.setAdcodename(rs.getString("AD_CODE_NAME"));
                if (rs.getString("U_Formula") != null) {
                    pvtdeduction.setFormula(rs.getString("U_Formula"));
                } else {
                    pvtdeduction.setFormula(rs.getString("O_Formula"));
                }
                if (rs.getString("U_VALUE") != null) {
                    pvtdeduction.setAdvalue(rs.getInt("U_VALUE"));
                } else {
                    pvtdeduction.setAdvalue(rs.getInt("O_VALUE"));
                }

                pvtDeductionList.add(pvtdeduction);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return pvtDeductionList;
    }

    @Override
    public ArrayList getOfficeWiseAllowance(String empId) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList allowanceList = new ArrayList();
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT G_AD_LIST.ad_code as ref_ad_code,UPDATE_AD_INFO.ad_code,ad_desc,G_AD_LIST.ad_formula as O_Formula,UPDATE_AD_INFO.ad_formula as U_Formula,G_AD_LIST.fixedvalue as O_VALUE,UPDATE_AD_INFO.fixedvalue as U_VALUE,AD_CODE_NAME FROM G_AD_LIST "
                    + "LEFT OUTER JOIN (SELECT * FROM UPDATE_AD_INFO WHERE where_updated = 'O' AND UPDATION_REF_CODE=?)UPDATE_AD_INFO ON G_AD_LIST.AD_CODE=UPDATE_AD_INFO.REF_AD_CODE "
                    + "WHERE AD_TYPE='A'");
            pstmt.setString(1, empId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                AllowanceDeduction allowance = new AllowanceDeduction();
                allowance.setAdcode(rs.getString("ad_code"));
                allowance.setRefadcode(rs.getString("ref_ad_code"));
                allowance.setAddesc(rs.getString("ad_desc"));
                allowance.setAdcodename(rs.getString("AD_CODE_NAME"));
                if (rs.getString("U_Formula") != null) {
                    allowance.setFormula(rs.getString("U_Formula"));
                } else {
                    allowance.setFormula(rs.getString("O_Formula"));
                }
                if (rs.getString("U_VALUE") != null) {
                    allowance.setAdvalue(rs.getInt("U_VALUE"));
                } else {
                    allowance.setAdvalue(rs.getInt("O_VALUE"));
                }
                allowanceList.add(allowance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return allowanceList;
    }

    @Override
    public ArrayList getOfficeWiseDeduction(String empId) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList deductionList = new ArrayList();
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT UPDATE_AD_INFO.ad_code,ad_desc,G_AD_LIST.ad_formula as O_Formula,UPDATE_AD_INFO.ad_formula as U_Formula,G_AD_LIST.fixedvalue as O_VALUE,UPDATE_AD_INFO.fixedvalue as U_VALUE,AD_CODE_NAME FROM G_AD_LIST "
                    + "LEFT OUTER JOIN (SELECT * FROM UPDATE_AD_INFO WHERE where_updated = 'O' AND UPDATION_REF_CODE=?)UPDATE_AD_INFO ON G_AD_LIST.AD_CODE=UPDATE_AD_INFO.REF_AD_CODE "
                    + "WHERE AD_TYPE='D' AND IS_PRIVATE_DEDN='N' ");
            pstmt.setString(1, empId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                AllowanceDeduction deduction = new AllowanceDeduction();
                deduction.setAdcode(rs.getString("ad_code"));
                deduction.setAddesc(rs.getString("ad_desc"));
                deduction.setAdcodename(rs.getString("AD_CODE_NAME"));
                if (rs.getString("U_Formula") != null) {
                    deduction.setFormula(rs.getString("U_Formula"));
                } else {
                    deduction.setFormula(rs.getString("O_Formula"));
                }
                if (rs.getString("U_VALUE") != null) {
                    deduction.setAdvalue(rs.getInt("U_VALUE"));
                } else {
                    deduction.setAdvalue(rs.getInt("O_VALUE"));
                }
                deductionList.add(deduction);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return deductionList;
    }

    @Override
    public ArrayList getOfficeWisePvtDeduction(String empId) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList pvtDeductionList = new ArrayList();
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT UPDATE_AD_INFO.ad_code,ad_desc,G_AD_LIST.ad_formula as O_Formula,UPDATE_AD_INFO.ad_formula as U_Formula,G_AD_LIST.fixedvalue as O_VALUE,UPDATE_AD_INFO.fixedvalue as U_VALUE,AD_CODE_NAME FROM G_AD_LIST "
                    + "LEFT OUTER JOIN (SELECT * FROM UPDATE_AD_INFO WHERE where_updated = 'O' AND UPDATION_REF_CODE=?)UPDATE_AD_INFO ON G_AD_LIST.AD_CODE=UPDATE_AD_INFO.REF_AD_CODE "
                    + "WHERE AD_TYPE='D' AND IS_PRIVATE_DEDN='Y' ");
            pstmt.setString(1, empId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                AllowanceDeduction pvtdeduction = new AllowanceDeduction();

                pvtdeduction.setAdcode(rs.getString("ad_code"));
                pvtdeduction.setAddesc(rs.getString("ad_desc"));
                pvtdeduction.setAdcodename(rs.getString("AD_CODE_NAME"));
                if (rs.getString("U_Formula") != null) {
                    pvtdeduction.setFormula(rs.getString("U_Formula"));
                } else {
                    pvtdeduction.setFormula(rs.getString("O_Formula"));
                }
                if (rs.getString("U_VALUE") != null) {
                    pvtdeduction.setAdvalue(rs.getInt("U_VALUE"));
                } else {
                    pvtdeduction.setAdvalue(rs.getInt("O_VALUE"));
                }

                pvtDeductionList.add(pvtdeduction);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return pvtDeductionList;
    }

    @Override
    public AllowanceDeduction getAllowanceDeductionDetail(String adcode) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        AllowanceDeduction ad = new AllowanceDeduction();
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT ad_code,AD_CODE_NAME,AD_DESC,is_fixed,bt_id,OBJECT_HEAD,BT_ID,AD_TYPE FROM g_ad_list WHERE ad_code=?");
            pstmt.setString(1, adcode);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                ad.setAdcode(rs.getString("ad_code"));
                ad.setAddesc(rs.getString("AD_DESC"));
                ad.setAdcodename(rs.getString("AD_CODE_NAME"));
                ad.setIsfixed(rs.getInt("is_fixed"));
                if (rs.getString("AD_TYPE").equals("A")) {
                    ad.setHead(rs.getString("OBJECT_HEAD"));
                } else {
                    ad.setHead(rs.getString("BT_ID"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return ad;
    }

    @Override
    public Message saveAllowanceDeductionDetail(AllowanceDeduction adbean) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String nCode = null;
        Message msg = new Message();
        msg.setStatus("Success");
        try {
            con = dataSource.getConnection();
            if (adbean.getWhereupdated().equals("G")) {
                con = dataSource.getConnection();
                pstmt = con.prepareStatement("SELECT MAX(CAST(AD_CODE as Integer)) AD_CODE FROM UPDATE_AD_INFO");
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    nCode = rs.getString("AD_CODE");
                    if (nCode != null) {
                        nCode = "" + (Integer.parseInt(nCode) + 1);
                        if (nCode.length() < 2) {
                            nCode = "0" + nCode;
                        }
                    } else {
                        nCode = "01";
                    }
                }
                pstmt = con.prepareStatement("INSERT INTO UPDATE_AD_INFO (REF_AD_CODE, AD_CODE, AD_FORMULA, IS_FIXED,FIXEDVALUE,AD_ORGFORMULA,WHERE_UPDATED,UPDATION_REF_CODE,BT_ID) values (?,?,?,?,?,?,?,?,?)");
                pstmt.setString(1, adbean.getAdcode());
                pstmt.setString(2, nCode);
                if (adbean.getIsfixed() == 1) {
                    pstmt.setString(3, null);
                    pstmt.setInt(4, 1);
                    pstmt.setInt(5, adbean.getAdvalue());
                    pstmt.setString(6, null);
                } else {
                    String str[] = adbean.getFormula().split(",");
                    pstmt.setString(3, str[1]);
                    pstmt.setInt(4, 0);
                    pstmt.setString(5, null);
                    pstmt.setString(6, str[0]);
                }
                pstmt.setString(7, "O");
                pstmt.setString(8, adbean.getUpdationRefCode());
                pstmt.setString(9, adbean.getHead());
                pstmt.executeUpdate();
            } else if (adbean.getWhereupdated().equals("O")) {

            } else if (adbean.getWhereupdated().equals("E")) {
                pstmt = con.prepareStatement("SELECT * FROM UPDATE_AD_INFO WHERE REF_AD_CODE=? AND WHERE_UPDATED=? AND UPDATION_REF_CODE=?");
                pstmt.setString(1, adbean.getAdcode());
                pstmt.setString(2, adbean.getWhereupdated());
                pstmt.setString(3, adbean.getUpdationRefCode());
                rs = pstmt.executeQuery();
                boolean recordFound = false;
                if (rs.next()) {
                    recordFound = true;
                }
                if (recordFound == false) {
                    pstmt = con.prepareStatement("INSERT INTO UPDATE_AD_INFO (REF_AD_CODE, AD_FORMULA, IS_FIXED,FIXEDVALUE,AD_ORGFORMULA,WHERE_UPDATED,UPDATION_REF_CODE,BT_ID) values (?,?,?,?,?,?,?,?)");
                    pstmt.setString(1, adbean.getAdcode());
                    if (adbean.getAdamttype().equals("1")) {
                        pstmt.setString(2, null);
                        pstmt.setInt(3, 1);
                        pstmt.setInt(4, adbean.getAdvalue());
                        pstmt.setString(5, null);
                    } else if (adbean.getAdamttype().equals("0")) {
                        String str[] = adbean.getFormula().split(",");
                        pstmt.setString(2, adbean.getFormula());
                        pstmt.setInt(3, 0);
                        pstmt.setString(4, null);
                        pstmt.setString(5, adbean.getFormula());
                    }
                    pstmt.setString(6, adbean.getWhereupdated());
                    pstmt.setString(7, adbean.getUpdationRefCode());
                    pstmt.setString(8, adbean.getHead());
                    pstmt.executeUpdate();
                } else if (recordFound == true) {
                    pstmt = con.prepareStatement("UPDATE UPDATE_AD_INFO SET FIXEDVALUE=? WHERE REF_AD_CODE=? AND WHERE_UPDATED=? AND UPDATION_REF_CODE=?");
                    pstmt.setInt(1, adbean.getAdvalue());
                    pstmt.setString(2, adbean.getAdcode());
                    pstmt.setString(3, adbean.getWhereupdated());
                    pstmt.setString(4, adbean.getUpdationRefCode());
                    pstmt.executeUpdate();
                }
            }
        } catch (Exception e) {
            msg.setStatus("Error");
            msg.setMessage(e.getMessage());
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return msg;
    }

    @Override
    public AllowanceDeduction getUpdatedAllowanceDeductionDetail(String adcode) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        AllowanceDeduction ad = new AllowanceDeduction();
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT UPDATE_AD_INFO.ad_code,AD_DESC,AD_CODE_NAME,UPDATE_AD_INFO.fixedvalue,UPDATE_AD_INFO.is_fixed,AD_TYPE,OBJECT_HEAD,UPDATE_AD_INFO.BT_ID FROM (SELECT * FROM UPDATE_AD_INFO WHERE where_updated = 'O' AND ad_code=?)UPDATE_AD_INFO INNER JOIN g_ad_list ON UPDATE_AD_INFO.ref_ad_code = g_ad_list.ad_code");
            pstmt.setString(1, adcode);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                ad.setAdcode(rs.getString("ad_code"));
                ad.setAddesc(rs.getString("AD_DESC"));
                ad.setAdcodename(rs.getString("AD_CODE_NAME"));
                ad.setAdvalue(rs.getInt("fixedvalue"));
                ad.setIsfixed(rs.getInt("is_fixed"));
                if (rs.getString("AD_TYPE").equals("A")) {
                    ad.setHead(rs.getString("OBJECT_HEAD"));
                } else {
                    ad.setHead(rs.getString("BT_ID"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return ad;
    }

    @Override
    public AllowanceDeduction getUpdatedAllowanceDeduction(AllowanceDeduction adBean) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT fixedvalue,IS_FIXED FROM UPDATE_AD_INFO WHERE REF_AD_CODE=? AND WHERE_UPDATED='E' AND UPDATION_REF_CODE=?");
            pstmt.setString(1, adBean.getAdcode());
            pstmt.setString(2, adBean.getUpdationRefCode());
            rs = pstmt.executeQuery();
            if (rs.next()) {
                adBean.setAdvalue(rs.getInt("FIXEDVALUE"));
                adBean.setAdamttype(rs.getString("IS_FIXED"));
            } else {
                adBean.setAdvalue(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return adBean;
    }

    public ADOBJECT getAdAmt(String curspc, String empCode, String adcode, String offCode, int aqday, int payday, int workday, int traing_days, PayComponent payComponent) {
        int v_finaladAmt = 0;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String v_adconfigured = "N";
        int v_adamt = 0;
        int v_fixed = 0;
        String v_ad_orgformula = "";
        String v_btId = "";
        int v_reccnt = 0;
        String v_gpc = "";
        try {
            con = dataSource.getConnection();
            if (v_adconfigured.equals("N")) {
                /* Allowance or Deduction amount is updated at the Employee End */
                pstmt = con.prepareStatement("SELECT FIXEDVALUE,IS_FIXED,AD_ORGFORMULA,BT_ID FROM UPDATE_AD_INFO WHERE UPDATION_REF_CODE = ? AND "
                        + "WHERE_UPDATED = 'E'  AND REF_AD_CODE = ?");
                pstmt.setString(1, empCode);
                pstmt.setString(2, adcode);
                rs = pstmt.executeQuery();

                if (rs.next()) {
                    v_reccnt = 1;
                    v_adamt = rs.getInt("FIXEDVALUE");
                    v_fixed = rs.getInt("IS_FIXED");
                    v_ad_orgformula = rs.getString("AD_ORGFORMULA");
                    v_btId = rs.getString("BT_ID");
                    if (v_fixed == 1) {
                        v_finaladAmt = v_adamt;
                    } else {
                        v_adconfigured = "Y";
                        //System.out.println("employee-v_ad_orgformula:" + v_ad_orgformula);
                    }
                }
                pstmt.close();
                pstmt = null;
            }

            /* Allowance or Deduction amount is updated at the POST End */
            if (v_finaladAmt == 0 && v_adconfigured.equals("N") && curspc != null) {
                pstmt = con.prepareStatement("SELECT GPC FROM G_SPC WHERE SPC=?");
                pstmt.setString(1, curspc);
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    v_gpc = rs.getString("GPC");
                }

                pstmt = con.prepareStatement("SELECT FIXEDVALUE,IS_FIXED,AD_ORGFORMULA FROM UPDATE_AD_INFO WHERE "
                        + "SUBSTR(UPDATION_REF_CODE,0,14) = ? AND SUBSTR(UPDATION_REF_CODE,15) =? AND WHERE_UPDATED = 'P'  AND"
                        + " REF_AD_CODE = ?");
                pstmt.setString(1, offCode);
                pstmt.setString(2, v_gpc);
                pstmt.setString(3, adcode);
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    v_reccnt = 1;
                    v_adamt = rs.getInt("FIXEDVALUE");
                    v_fixed = rs.getInt("IS_FIXED");
                    v_ad_orgformula = rs.getString("AD_ORGFORMULA");
                    if (v_fixed == 1) {
                        v_finaladAmt = v_adamt;
                    } else {
                        v_adconfigured = "Y";
                    }
                }
                pstmt.close();
                pstmt = null;
            }
            /* Allowance or Deduction amount is updated at the Office End */
            if (v_finaladAmt == 0 && v_adconfigured.equals("N")) {
                pstmt = con.prepareStatement("SELECT FIXEDVALUE,IS_FIXED,AD_ORGFORMULA,BT_ID FROM UPDATE_AD_INFO WHERE UPDATION_REF_CODE = ? AND WHERE_UPDATED = 'O' AND REF_AD_CODE = ?");
                pstmt.setString(1, offCode);
                pstmt.setString(2, adcode);
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    v_reccnt = 1;
                    v_adamt = rs.getInt("FIXEDVALUE");
                    v_fixed = rs.getInt("IS_FIXED");
                    v_ad_orgformula = rs.getString("AD_ORGFORMULA");
                    v_btId = rs.getString("BT_ID");

                    if (v_fixed == 1) {
                        v_finaladAmt = v_adamt;
                    } else {
                        v_adconfigured = "Y";

                    }
                }
                pstmt.close();
                pstmt = null;
            }

            /* Allowance or Deduction amount is Calculated From Global Formula */
            if (v_reccnt == 0) {
                if (v_finaladAmt == 0 && v_adconfigured.equals("N")) {
                    pstmt = con.prepareStatement("SELECT FIXEDVALUE,IS_FIXED,AD_ORGFORMULA,BT_ID,SCHEDULE,OBJECT_HEAD,AD_TYPE FROM G_AD_LIST WHERE AD_CODE = ?");
                    pstmt.setString(1, adcode);
                    rs = pstmt.executeQuery();
                    if (rs.next()) {
                        v_reccnt = 1;
                        v_adamt = rs.getInt("FIXEDVALUE");
                        v_fixed = rs.getInt("IS_FIXED");
                        v_ad_orgformula = rs.getString("AD_ORGFORMULA");

                        if (rs.getString("AD_TYPE") != null && rs.getString("AD_TYPE").equals("A")) {
                            v_btId = rs.getString("OBJECT_HEAD");
                        } else {
                            v_btId = rs.getString("BT_ID");
                        }

                        if (v_fixed == 1) {
                            v_finaladAmt = v_adamt;
                        } else {
                            v_adconfigured = "Y";
                            //System.out.println("global-v_ad_orgformula:" + v_ad_orgformula);
                        }
                    }

                    pstmt.close();
                    pstmt = null;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        ADOBJECT adObject = new ADOBJECT();
        adObject.setAdamt(v_finaladAmt);
        adObject.setBtid(v_btId);
        return adObject;
    }

    @Override
    public ArrayList getAllowanceList(SectionDtlSPCWiseEmp sdswe, String offCode, int aqday, int payday, int workday, int traing_days, PayComponent payComponent) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList allowanceList = new ArrayList();
        try {
            con = dataSource.getConnection();
            if (sdswe.getJobtypeid() == 1) {
                pstmt = con.prepareStatement("SELECT AD_CODE,AD_DESC,AD_CODE_NAME,AD_TYPE,ALT_UNIT,DED_TYPE,SCHEDULE,REP_COL,ROW_NO,OBJECT_HEAD FROM G_AD_LIST "
                        + "                        WHERE AD_TYPE='A' AND (ad_code_name='DCLUPA' OR ad_code_name='WASHAL')");
                rs = pstmt.executeQuery();
                int v_adamt = 0;
                System.out.println("workday:" + workday);
                while (rs.next()) {
                    if (!rs.getString("AD_CODE_NAME").equalsIgnoreCase("HRA")) {
                        AllowanceDeduction alwded = new AllowanceDeduction();
                        alwded.setAdcode(rs.getString("AD_CODE"));
                        alwded.setAddesc(rs.getString("AD_DESC"));
                        alwded.setAdcodename(rs.getString("AD_CODE_NAME"));
                        alwded.setAdamttype(rs.getString("AD_TYPE"));
                        alwded.setAltUnit(rs.getString("ALT_UNIT"));
                        alwded.setDedType(rs.getString("DED_TYPE"));
                        alwded.setSchedule(rs.getString("SCHEDULE"));
                        alwded.setRownum(rs.getInt("ROW_NO"));
                        alwded.setRepCol(rs.getInt("REP_COL"));
                        alwded.setHead(rs.getString("OBJECT_HEAD"));
                        String v_adcodename = alwded.getAdcodename();
                        if (v_adcodename.equalsIgnoreCase("DCLUPA")) {
                            v_adamt = workday * 300;
                        } else if (v_adcodename.equalsIgnoreCase("WASHAL")) {
                            float washamt = new Float(0.83);
                            v_adamt = Math.round(workday * washamt);

                        }
                        alwded.setAdvalue(v_adamt);
                        allowanceList.add(alwded);
                    }

                }
            } else {
                pstmt = con.prepareStatement("SELECT AD_CODE,AD_DESC,AD_CODE_NAME,AD_TYPE,ALT_UNIT,DED_TYPE,SCHEDULE,REP_COL,ROW_NO,OBJECT_HEAD "
                        + "FROM (SELECT AD_CODE,AD_DESC,AD_CODE_NAME,AD_TYPE,ALT_UNIT,DED_TYPE,SCHEDULE,REP_COL,ROW_NO,OBJECT_HEAD FROM G_AD_LIST "
                        + "WHERE AD_TYPE='A')G_AD_LIST");
                rs = pstmt.executeQuery();
                int v_adamt = 0;
                while (rs.next()) {
                    if (!rs.getString("AD_CODE_NAME").equalsIgnoreCase("HRA")) {
                        AllowanceDeduction alwded = new AllowanceDeduction();
                        alwded.setAdcode(rs.getString("AD_CODE"));
                        alwded.setAddesc(rs.getString("AD_DESC"));
                        alwded.setAdcodename(rs.getString("AD_CODE_NAME"));
                        alwded.setAdamttype(rs.getString("AD_TYPE"));
                        alwded.setAltUnit(rs.getString("ALT_UNIT"));
                        alwded.setDedType(rs.getString("DED_TYPE"));
                        alwded.setSchedule(rs.getString("SCHEDULE"));
                        alwded.setRownum(rs.getInt("ROW_NO"));
                        alwded.setRepCol(rs.getInt("REP_COL"));
                        alwded.setHead(rs.getString("OBJECT_HEAD"));
                        String v_adcodename = alwded.getAdcodename();
                        String v_schedule = alwded.getSchedule();
                        ADOBJECT v_adobject = getAdAmt(sdswe.getSpc(), sdswe.getEmpid(), alwded.getAdcode(), offCode, aqday, payday, workday, traing_days, payComponent);
                        String v_btid = "";
                        if (v_adobject.btid != null) {
                            v_btid = v_adobject.btid;
                        }
                        v_adamt = v_adobject.adamt;

                        if (aqday > workday) {
                            if (v_schedule.equals("OA") && (v_adcodename != "GCA" && v_adcodename != "SP" && v_adcodename != "GAAL")) {
                                v_adamt = (v_adamt / aqday) * workday;
                            }
                            if (v_adamt > 0) {
                                v_adamt = (v_adamt / aqday) * payday;
                            }
                        }
                        alwded.setAdvalue(v_adamt);
                        if (v_adcodename.equalsIgnoreCase("GP")) {
                            alwded.setAdvalue(payComponent.getGp());
                        }

                        if (v_adcodename.equalsIgnoreCase("SP")) {
                            alwded.setAdvalue(0);
                        }

                        allowanceList.add(alwded);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }

        return allowanceList;
    }

    @Override
    public ArrayList getAllowanceList(SectionDtlSPCWiseEmp sdswe, String configuredlvl, BigDecimal billGroupId, String offCode, int aqday, int payday, int workday, int traing_days, PayComponent payComponent, String v_bill_type, String v_depcode, int aqmonth, int aqyear) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList allowanceList = new ArrayList();
        try {
            con = dataSource.getConnection();
            System.out.println("configuredlvl:" + configuredlvl);
            if (configuredlvl == null || configuredlvl.equals("B")) {
                pstmt = con.prepareStatement(" SELECT G_AD_LIST.AD_CODE,AD_DESC,AD_CODE_NAME,AD_TYPE,ALT_UNIT,DED_TYPE,SCHEDULE,REP_COL,ROW_NO,COL_NUMBER,ROW_NUMBER,OBJECT_HEAD FROM "
                        + "(SELECT AD_CODE,AD_DESC,AD_CODE_NAME,AD_TYPE,ALT_UNIT,DED_TYPE,SCHEDULE,REP_COL,ROW_NO,OBJECT_HEAD FROM G_AD_LIST WHERE AD_TYPE='A')G_AD_LIST "
                        + "LEFT OUTER JOIN (select AD_CODE,COL_NUMBER,ROW_NUMBER from bill_configuration where bill_group_id = ?)bill_configuration ON "
                        + "G_AD_LIST.AD_CODE = bill_configuration.AD_CODE");
                pstmt.setBigDecimal(1, billGroupId);
                rs = pstmt.executeQuery();
                int v_adamt_temp = 0;
                int v_adamt = 0;
                while (rs.next()) {
                    if (!rs.getString("AD_CODE_NAME").equalsIgnoreCase("HRA")) {
                        int confrepcol = rs.getInt("COL_NUMBER");
                        int confreprow = rs.getInt("ROW_NUMBER");
                        AllowanceDeduction alwded = new AllowanceDeduction();
                        alwded.setAdcode(rs.getString("AD_CODE"));
                        alwded.setAddesc(rs.getString("AD_DESC"));
                        alwded.setAdcodename(rs.getString("AD_CODE_NAME"));
                        alwded.setAdamttype(rs.getString("AD_TYPE"));
                        alwded.setAltUnit(rs.getString("ALT_UNIT"));
                        alwded.setDedType(rs.getString("DED_TYPE"));
                        alwded.setSchedule(rs.getString("SCHEDULE"));
                        alwded.setRownum(rs.getInt("ROW_NO"));
                        alwded.setRepCol(rs.getInt("REP_COL"));

                        String v_btid = rs.getString("OBJECT_HEAD");
                        ADOBJECT v_adobject = new ADOBJECT();
                        String v_adcodename = alwded.getAdcodename();
                        if (!v_adcodename.equalsIgnoreCase("HRA")) {
                            v_adobject = getAdAmt(sdswe, alwded.getAdcode(), offCode, aqday, payday, workday, traing_days, payComponent, aqmonth, aqyear);
                            if (v_adobject.getBtid() != null && v_adcodename.equalsIgnoreCase("DP") && !v_adobject.getBtid().equals("")) {
                                v_btid = v_adobject.btid;
                            }
                            if (v_adobject.getBtid() != null && v_adobject.getBtid().equals("")) {
                                if (v_bill_type.equals("21") && (v_adcodename.equalsIgnoreCase("DA") || v_adcodename.equalsIgnoreCase("DP") || alwded.getAdcodename().equalsIgnoreCase("GP") || alwded.getAdcodename().equalsIgnoreCase("SP") || alwded.getSchedule().equalsIgnoreCase("OA"))) {
                                    v_btid = "921";
                                } else {
                                    if (alwded.getAdcodename().equalsIgnoreCase("DP")) {
                                        v_btid = v_adobject.btid;
                                    }
                                    v_btid = v_adobject.btid;
                                }

                            }

                        } else {
                            if (v_bill_type.equals("21") && (alwded.getAdcodename().equalsIgnoreCase("DA") || alwded.getAdcodename().equalsIgnoreCase("DP") || alwded.getAdcodename().equalsIgnoreCase("GP") || alwded.getAdcodename().equalsIgnoreCase("SP") || alwded.getSchedule().equalsIgnoreCase("OA"))) {
                                v_btid = "921";
                            }
                        }
                        v_adamt_temp = v_adobject.getAdamt();
                        v_adamt = v_adamt_temp;
                        if (v_adamt_temp > 0) {
                            if (v_depcode.equals("05")) {
                                if (v_adcodename.equals("KMA") || v_adcodename.equals("BTLA") || v_adcodename.equals("SPLDIT") || v_adcodename.equals("RAL") || v_adcodename.equals("CLAL") || v_adcodename.equals("HA")) {
                                    v_adamt = 0;
                                } else if (!v_adcodename.equals("GCA") && !v_adcodename.equals("LFQ")) {
                                    v_adamt = v_adamt_temp / 2;
                                }
                            }
                        }
                        alwded.setAdvalue(v_adamt);
                        if (v_adcodename.equalsIgnoreCase("GP")) {
                            alwded.setAdvalue(payComponent.getGp());
                        }

                        if (v_adcodename.equalsIgnoreCase("SP")) {
                            alwded.setAdvalue(0);
                        }
                        if (v_adcodename.equalsIgnoreCase("PPAY")) {
                            alwded.setAdvalue(v_adamt_temp);
                        }
                        /*
                         IF v_adcodename = 'DA' THEN            
                         v_da := v_adamt;           
                         END IF;
                         */
                        if (confrepcol != 0) {
                            alwded.setRepCol(confrepcol);

                        }
                        if (confreprow != 0) {
                            alwded.setRownum(confrepcol);
                        }
                        alwded.setHead(v_btid);
                        allowanceList.add(alwded);
                    }
                }
            } else {
                pstmt = con.prepareStatement("SELECT G_AD_LIST.AD_CODE,AD_DESC,AD_CODE_NAME,AD_TYPE,ALT_UNIT,DED_TYPE,SCHEDULE,REP_COL,ROW_NO,COL_NUMBER,ROW_NUMBER,OBJECT_HEAD FROM (SELECT AD_CODE,AD_DESC,AD_CODE_NAME,AD_TYPE,ALT_UNIT,DED_TYPE,SCHEDULE,REP_COL,ROW_NO,OBJECT_HEAD FROM G_AD_LIST WHERE AD_TYPE='A')G_AD_LIST "
                        + "LEFT OUTER JOIN (select AD_CODE,COL_NUMBER,ROW_NUMBER from bill_configuration where OFF_CODE = ? and bill_group_id is null)bill_configuration ON "
                        + "G_AD_LIST.AD_CODE = bill_configuration.AD_CODE");
                pstmt.setString(1, offCode);
                rs = pstmt.executeQuery();
                int v_adamt_temp = 0;
                int v_adamt = 0;
                String v_btid = null;
                ADOBJECT v_adobject = null;

                while (rs.next()) {
                    v_btid = rs.getString("OBJECT_HEAD");
                    int confrepcol = rs.getInt("COL_NUMBER");
                    int confreprow = rs.getInt("ROW_NUMBER");
                    AllowanceDeduction alwded = new AllowanceDeduction();
                    alwded.setAdcode(rs.getString("AD_CODE"));
                    alwded.setAddesc(rs.getString("AD_DESC"));
                    alwded.setAdcodename(rs.getString("AD_CODE_NAME"));
                    alwded.setAdamttype(rs.getString("AD_TYPE"));
                    alwded.setAltUnit(rs.getString("ALT_UNIT"));
                    alwded.setDedType(rs.getString("DED_TYPE"));
                    alwded.setSchedule(rs.getString("SCHEDULE"));
                    alwded.setRownum(rs.getInt("ROW_NO"));
                    alwded.setRepCol(rs.getInt("REP_COL"));
                    alwded.setHead(rs.getString("OBJECT_HEAD"));
                    String v_adcodename = alwded.getAdcodename();
                    if (!rs.getString("AD_CODE_NAME").equalsIgnoreCase("HRA")) {
                        v_adobject = getAdAmt(sdswe, alwded.getAdcode(), offCode, aqday, payday, workday, traing_days, payComponent, aqmonth, aqyear);
                        if (v_adobject.getBtid() != null && !v_adobject.getBtid().equals("")) {
                            if (v_bill_type.equals("21") && (alwded.getAdcodename().equalsIgnoreCase("DA") || alwded.getAdcodename().equalsIgnoreCase("DP") || alwded.getAdcodename().equalsIgnoreCase("GP") || alwded.getAdcodename().equalsIgnoreCase("SP") || alwded.getSchedule().equalsIgnoreCase("OA"))) {
                                v_btid = "921";
                            } else {
                                v_btid = v_adobject.btid;
                            }
                        } else {
                            if (v_bill_type.equals("21") && (alwded.getAdcodename().equalsIgnoreCase("DA") || alwded.getAdcodename().equalsIgnoreCase("DP") || alwded.getAdcodename().equalsIgnoreCase("GP") || alwded.getAdcodename().equalsIgnoreCase("SP") || alwded.getSchedule().equalsIgnoreCase("OA"))) {
                                v_btid = "921";
                            }
                        }
                        v_adamt_temp = v_adobject.getAdamt();
                        v_adamt = v_adamt_temp;
                        if (v_adamt_temp > 0) {
                            if (v_depcode.equals("05")) {
                                if (v_adcodename.equals("KMA") || v_adcodename.equals("BTLA") || v_adcodename.equals("SPLDIT") || v_adcodename.equals("RAL") || v_adcodename.equals("CLAL") || v_adcodename.equals("HA")) {
                                    v_adamt = 0;
                                } else if (!v_adcodename.equals("GCA") && !v_adcodename.equals("LFQ")) {
                                    v_adamt = v_adamt_temp / 2;
                                }
                            }
                        }
                        alwded.setAdvalue(v_adamt);
                        if (v_adcodename.equalsIgnoreCase("GP")) {
                            alwded.setAdvalue(payComponent.getGp());
                        }

                        if (v_adcodename.equalsIgnoreCase("SP")) {
                            alwded.setAdvalue(0);
                        }
                        if (v_adcodename.equalsIgnoreCase("PPAY")) {
                            alwded.setAdvalue(v_adamt_temp);
                        }
                        /*
                         IF v_adcodename = 'DA' THEN            
                         v_da := v_adamt;           
                         END IF;
                         */
                        if (confrepcol != 0) {
                            alwded.setRepCol(confrepcol);

                        }
                        if (confreprow != 0) {
                            alwded.setRownum(confrepcol);
                        }
                        alwded.setHead(v_btid);
                        allowanceList.add(alwded);

                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return allowanceList;
    }

    @Override
    public ArrayList getAllowanceList(String curspc, String empCode, String configuredlvl, BigDecimal billGroupId, String offCode, int aqday, int payday, int workday, int traing_days, PayComponent payComponent, String v_bill_type, String v_depcode) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList allowanceList = new ArrayList();
        try {
            con = dataSource.getConnection();
            if (configuredlvl == null || configuredlvl.equals("B")) {
                pstmt = con.prepareStatement(" SELECT G_AD_LIST.AD_CODE,AD_DESC,AD_CODE_NAME,AD_TYPE,ALT_UNIT,DED_TYPE,SCHEDULE,REP_COL,ROW_NO,COL_NUMBER,ROW_NUMBER,OBJECT_HEAD FROM "
                        + "(SELECT AD_CODE,AD_DESC,AD_CODE_NAME,AD_TYPE,ALT_UNIT,DED_TYPE,SCHEDULE,REP_COL,ROW_NO,OBJECT_HEAD FROM G_AD_LIST WHERE AD_TYPE='A')G_AD_LIST "
                        + "LEFT OUTER JOIN (select AD_CODE,COL_NUMBER,ROW_NUMBER from bill_configuration where bill_group_id = ?)bill_configuration ON "
                        + "G_AD_LIST.AD_CODE = bill_configuration.AD_CODE");
                pstmt.setBigDecimal(1, billGroupId);
                rs = pstmt.executeQuery();
                int v_adamt_temp = 0;
                int v_adamt = 0;
                while (rs.next()) {
                    if (!rs.getString("AD_CODE_NAME").equalsIgnoreCase("HRA")) {
                        int confrepcol = rs.getInt("COL_NUMBER");
                        int confreprow = rs.getInt("ROW_NUMBER");
                        AllowanceDeduction alwded = new AllowanceDeduction();
                        alwded.setAdcode(rs.getString("AD_CODE"));
                        alwded.setAddesc(rs.getString("AD_DESC"));
                        alwded.setAdcodename(rs.getString("AD_CODE_NAME"));
                        alwded.setAdamttype(rs.getString("AD_TYPE"));
                        alwded.setAltUnit(rs.getString("ALT_UNIT"));
                        alwded.setDedType(rs.getString("DED_TYPE"));
                        alwded.setSchedule(rs.getString("SCHEDULE"));
                        alwded.setRownum(rs.getInt("ROW_NO"));
                        alwded.setRepCol(rs.getInt("REP_COL"));

                        String v_btid = rs.getString("OBJECT_HEAD");
                        ADOBJECT v_adobject = new ADOBJECT();
                        String v_adcodename = alwded.getAdcodename();
                        if (!v_adcodename.equalsIgnoreCase("HRA")) {
                            v_adobject = getAdAmt(curspc, empCode, alwded.getAdcode(), offCode, aqday, payday, workday, traing_days, payComponent);
                            if (v_adobject.getBtid() != null && v_adcodename.equalsIgnoreCase("DP") && !v_adobject.getBtid().equals("")) {
                                v_btid = v_adobject.btid;
                            }
                            if (v_adobject.getBtid() != null && !v_adobject.getBtid().equals("")) {
                                if (v_bill_type.equals("21") && (v_adcodename.equalsIgnoreCase("DA") || v_adcodename.equalsIgnoreCase("DP") || alwded.getAdcodename().equalsIgnoreCase("GP") || alwded.getAdcodename().equalsIgnoreCase("SP") || alwded.getSchedule().equalsIgnoreCase("OA"))) {
                                    v_btid = "921";
                                } else {
                                    if (alwded.getAdcodename().equalsIgnoreCase("DP")) {
                                        v_btid = v_adobject.btid;
                                    }
                                    v_btid = v_adobject.btid;
                                }

                            }

                        } else {
                            if (v_bill_type.equals("21") && (alwded.getAdcodename().equalsIgnoreCase("DA") || alwded.getAdcodename().equalsIgnoreCase("DP") || alwded.getAdcodename().equalsIgnoreCase("GP") || alwded.getAdcodename().equalsIgnoreCase("SP") || alwded.getSchedule().equalsIgnoreCase("OA"))) {
                                v_btid = "921";
                            }
                        }
                        v_adamt_temp = v_adobject.getAdamt();
                        v_adamt = v_adamt_temp;
                        if (v_adamt_temp > 0) {
                            if (v_depcode.equals("05")) {
                                if (v_adcodename.equals("KMA") || v_adcodename.equals("BTLA") || v_adcodename.equals("SPLDIT") || v_adcodename.equals("RAL") || v_adcodename.equals("CLAL") || v_adcodename.equals("HA")) {
                                    v_adamt = 0;
                                } else if (!v_adcodename.equals("GCA") && !v_adcodename.equals("LFQ")) {
                                    v_adamt = v_adamt_temp / 2;
                                }
                            }
                        }
                        alwded.setAdvalue(v_adamt);
                        if (v_adcodename.equalsIgnoreCase("GP")) {
                            alwded.setAdvalue(payComponent.getGp());
                        }

                        if (v_adcodename.equalsIgnoreCase("SP")) {
                            alwded.setAdvalue(0);
                        }
                        if (v_adcodename.equalsIgnoreCase("PPAY")) {
                            alwded.setAdvalue(v_adamt_temp);
                        }
                        /*
                         IF v_adcodename = 'DA' THEN            
                         v_da := v_adamt;           
                         END IF;
                         */
                        if (confrepcol != 0) {
                            alwded.setRepCol(confrepcol);

                        }
                        if (confreprow != 0) {
                            alwded.setRownum(confrepcol);
                        }
                        alwded.setHead(v_btid);
                        allowanceList.add(alwded);
                    }
                }
            } else {
                pstmt = con.prepareStatement("SELECT G_AD_LIST.AD_CODE,AD_DESC,AD_CODE_NAME,AD_TYPE,ALT_UNIT,DED_TYPE,SCHEDULE,REP_COL,ROW_NO,COL_NUMBER,ROW_NUMBER,OBJECT_HEAD FROM (SELECT AD_CODE,AD_DESC,AD_CODE_NAME,AD_TYPE,ALT_UNIT,DED_TYPE,SCHEDULE,REP_COL,ROW_NO,OBJECT_HEAD FROM G_AD_LIST WHERE AD_TYPE='A')G_AD_LIST "
                        + "LEFT OUTER JOIN (select AD_CODE,COL_NUMBER,ROW_NUMBER from bill_configuration where OFF_CODE = ? AND BILL_GROUP_ID IS NULL)bill_configuration ON "
                        + "G_AD_LIST.AD_CODE = bill_configuration.AD_CODE");
                pstmt.setString(1, offCode);
                rs = pstmt.executeQuery();
                int v_adamt_temp = 0;
                int v_adamt = 0;
                String v_btid = null;
                ADOBJECT v_adobject = null;

                while (rs.next()) {
                    int confrepcol = rs.getInt("COL_NUMBER");
                    int confreprow = rs.getInt("ROW_NUMBER");
                    AllowanceDeduction alwded = new AllowanceDeduction();
                    alwded.setAdcode(rs.getString("AD_CODE"));
                    alwded.setAddesc(rs.getString("AD_DESC"));
                    alwded.setAdcodename(rs.getString("AD_CODE_NAME"));
                    alwded.setAdamttype(rs.getString("AD_TYPE"));
                    alwded.setAltUnit(rs.getString("ALT_UNIT"));
                    alwded.setDedType(rs.getString("DED_TYPE"));
                    alwded.setSchedule(rs.getString("SCHEDULE"));
                    alwded.setRownum(rs.getInt("ROW_NO"));
                    alwded.setRepCol(rs.getInt("REP_COL"));
                    alwded.setHead(rs.getString("OBJECT_HEAD"));
                    String v_adcodename = alwded.getAdcodename();
                    if (!rs.getString("AD_CODE_NAME").equalsIgnoreCase("HRA")) {
                        v_adobject = getAdAmt(curspc, empCode, alwded.getAdcode(), offCode, aqday, payday, workday, traing_days, payComponent);
                        if (v_adobject.getBtid() != null && !v_adobject.getBtid().equals("")) {
                            if (v_bill_type.equals("21") && (alwded.getAdcodename().equalsIgnoreCase("DA") || alwded.getAdcodename().equalsIgnoreCase("DP") || alwded.getAdcodename().equalsIgnoreCase("GP") || alwded.getAdcodename().equalsIgnoreCase("SP") || alwded.getSchedule().equalsIgnoreCase("OA"))) {
                                v_btid = "921";
                            } else {
                                v_btid = v_adobject.btid;
                            }
                        } else {
                            if (v_bill_type.equals("21") && (alwded.getAdcodename().equalsIgnoreCase("DA") || alwded.getAdcodename().equalsIgnoreCase("DP") || alwded.getAdcodename().equalsIgnoreCase("GP") || alwded.getAdcodename().equalsIgnoreCase("SP") || alwded.getSchedule().equalsIgnoreCase("OA"))) {
                                v_btid = "921";
                            }
                        }
                        v_adamt = v_adobject.adamt;
                        if (v_adamt_temp > 0) {
                            if (v_depcode.equals("05")) {
                                if (v_adcodename.equals("KMA") || v_adcodename.equals("BTLA") || v_adcodename.equals("SPLDIT") || v_adcodename.equals("RAL") || v_adcodename.equals("CLAL") || v_adcodename.equals("HA")) {
                                    v_adamt = 0;
                                } else if (!v_adcodename.equals("GCA") && !v_adcodename.equals("LFQ")) {
                                    v_adamt = v_adamt_temp / 2;
                                }
                            }
                        }
                        alwded.setAdvalue(v_adamt);
                        if (v_adcodename.equalsIgnoreCase("GP")) {
                            alwded.setAdvalue(payComponent.getGp());
                        }

                        if (v_adcodename.equalsIgnoreCase("SP")) {
                            alwded.setAdvalue(0);
                        }
                        if (v_adcodename.equalsIgnoreCase("PPAY")) {
                            alwded.setAdvalue(v_adamt_temp);
                        }
                        /*
                         IF v_adcodename = 'DA' THEN            
                         v_da := v_adamt;           
                         END IF;
                         */
                        if (confrepcol != 0) {
                            alwded.setRepCol(confrepcol);

                        }
                        if (confreprow != 0) {
                            alwded.setRownum(confrepcol);
                        }
                        alwded.setHead(v_btid);
                        allowanceList.add(alwded);

                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return allowanceList;
    }

    public AqDtlsModel[] getDA(String empCode, BigDecimal billGroupId, PayComponent payComponent, long gross, AqmastModel aqmast) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList<AqDtlsModel> allowanceList = new ArrayList<>();
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT G_AD_LIST.AD_CODE,AD_DESC,AD_CODE_NAME,AD_TYPE,ALT_UNIT,DED_TYPE,SCHEDULE,REP_COL,ROW_NO,rep_col,row_no,OBJECT_HEAD from g_ad_list where ad_code_NAME='DA'");
            rs = pstmt.executeQuery();
            if (rs.next()) {

                long da;
                if (payComponent.getIspayrevised() == null || payComponent.getIspayrevised().equalsIgnoreCase("N")) {
                    da = Math.round(((payComponent.getBasic() + payComponent.getGp()) * 1.39));
                } else {
                    da = Math.round(payComponent.getBasic() * 0.05);
                }

                /*if (alwded.getAdcodename().equalsIgnoreCase("GP")) {
                 adAmt = 0;
                 }*/
                long adAmt = da;

                /*if (alwded.getAdcodename().equalsIgnoreCase("CPF")) {
                 adAmt = (int) ((aqmast.getCurBasic() + da) * 0.1);
                 }*/
                //gross = gross + adAmt;
                AqDtlsModel aqModel = new AqDtlsModel();
                aqModel.setAqGroup(aqmast.getAqGroup());
                aqModel.setAqSlNo(aqmast.getAqSlNo());
                aqModel.setDdoOff("");
                aqModel.setEmpCode(aqmast.getEmpCode());
                aqModel.setPayMon(aqmast.getPayMonth());
                aqModel.setPayYear(aqmast.getPayYear());
                aqModel.setAqDate(aqmast.getAqDate());
                aqModel.setAqMonth(aqmast.getAqMonth());
                aqModel.setAqYear(aqmast.getAqYear());
                aqModel.setAqType(aqmast.getAqType());
                aqModel.setRefOrderNo(aqmast.getRefOrder());
                aqModel.setRefOrderDate(aqmast.getRefDate());
                aqModel.setSlNo(rs.getInt("ROW_NO"));
                aqModel.setAdCode(rs.getString("AD_CODE_NAME"));
                aqModel.setAdDesc(rs.getString("AD_DESC"));
                aqModel.setAdType(rs.getString("AD_TYPE"));
                aqModel.setAltUnit(rs.getString("ALT_UNIT"));
                aqModel.setDedType(rs.getString("DED_TYPE"));
                aqModel.setAdAmt(adAmt);
                aqModel.setAccNo(null);
                aqModel.setRefDesc(null);
                aqModel.setRefCount(0);
                aqModel.setSchedule(rs.getString("SCHEDULE"));
                aqModel.setNowDedn(null);
                aqModel.setTotRecAmt(0);
                aqModel.setRepCol(rs.getInt("REP_COL"));
                aqModel.setAdRefId(null);
                aqModel.setBtId(rs.getString("OBJECT_HEAD"));
                aqModel.setInstalCount(0);
                allowanceList.add(aqModel);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }

        AqDtlsModel aqDtlsModels[] = allowanceList.toArray(new AqDtlsModel[allowanceList.size()]);
        return aqDtlsModels;
    }

    @Override
    public ArrayList getAllowanceList(String configuredlvl, BigDecimal billGroupId, String offcode, String empId, PayComponent payComponent
    ) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList allowanceList = new ArrayList();
        try {
            con = dataSource.getConnection();
            if (configuredlvl == null || configuredlvl.equals("B")) {
                pstmt = con.prepareStatement("SELECT G_AD_LIST.AD_CODE,AD_DESC,AD_CODE_NAME,AD_TYPE,ALT_UNIT,DED_TYPE,SCHEDULE,REP_COL,ROW_NO,COL_NUMBER,ROW_NUMBER,OBJECT_HEAD, update_ad_info.FIXEDVALUE FROM "
                        + "(SELECT AD_CODE,AD_DESC,AD_CODE_NAME, AD_TYPE,ALT_UNIT,DED_TYPE,SCHEDULE,REP_COL,ROW_NO,OBJECT_HEAD FROM G_AD_LIST WHERE AD_TYPE='A')G_AD_LIST "
                        + "LEFT OUTER JOIN (select AD_CODE,COL_NUMBER,ROW_NUMBER from bill_configuration where bill_group_id = ?)bill_configuration ON G_AD_LIST.AD_CODE = bill_configuration.AD_CODE "
                        + "LEFT OUTER JOIN (SELECT * FROM update_ad_info WHERE updation_ref_code = ? AND where_updated = 'E')update_ad_info ON G_AD_LIST.AD_CODE = update_ad_info.REF_AD_CODE");
                pstmt.setBigDecimal(1, billGroupId);
                pstmt.setString(2, empId);
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    if (!rs.getString("AD_CODE_NAME").equalsIgnoreCase("HRA")) {
                        AllowanceDeduction alwded = new AllowanceDeduction();
                        alwded.setAdcode(rs.getString("AD_CODE"));
                        alwded.setAddesc(rs.getString("AD_DESC"));
                        alwded.setAdcodename(rs.getString("AD_CODE_NAME"));
                        alwded.setAdamttype(rs.getString("AD_TYPE"));
                        alwded.setHead(rs.getString("OBJECT_HEAD"));
                        if (rs.getString("AD_CODE_NAME").equalsIgnoreCase("GP")) {
                            alwded.setAdvalue(payComponent.getGp());
                        } else {
                            alwded.setAdvalue(rs.getInt("FIXEDVALUE"));
                        }

                        alwded.setRownum(rs.getInt("ROW_NO"));
                        alwded.setRepCol(rs.getInt("REP_COL"));
                        alwded.setAltUnit(rs.getString("ALT_UNIT"));
                        alwded.setSchedule(rs.getString("SCHEDULE"));
                        allowanceList.add(alwded);
                    }
                }
            } else {
                pstmt = con.prepareStatement("SELECT G_AD_LIST.AD_CODE,AD_DESC,AD_CODE_NAME,AD_TYPE,ALT_UNIT,DED_TYPE,SCHEDULE,REP_COL,ROW_NO,COL_NUMBER,ROW_NUMBER,OBJECT_HEAD, update_ad_info.FIXEDVALUE FROM "
                        + "(SELECT AD_CODE,AD_DESC,AD_CODE_NAME,AD_TYPE,ALT_UNIT,DED_TYPE,SCHEDULE,REP_COL,ROW_NO,OBJECT_HEAD FROM G_AD_LIST WHERE AD_TYPE='A')G_AD_LIST "
                        + "LEFT OUTER JOIN (select AD_CODE,COL_NUMBER,ROW_NUMBER from bill_configuration where OFF_CODE = ?)bill_configuration ON G_AD_LIST.AD_CODE = bill_configuration.AD_CODE "
                        + "LEFT OUTER JOIN (SELECT * FROM update_ad_info WHERE updation_ref_code = ? AND where_updated = 'E')update_ad_info ON G_AD_LIST.AD_CODE = update_ad_info.REF_AD_CODE");
                pstmt.setString(1, offcode);
                pstmt.setString(2, empId);
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    if (!rs.getString("AD_CODE_NAME").equalsIgnoreCase("HRA")) {
                        AllowanceDeduction alwded = new AllowanceDeduction();
                        alwded.setAdcode(rs.getString("AD_CODE"));
                        alwded.setAddesc(rs.getString("AD_DESC"));
                        alwded.setAdcodename(rs.getString("AD_CODE_NAME"));
                        alwded.setAdamttype(rs.getString("AD_TYPE"));
                        alwded.setHead(rs.getString("OBJECT_HEAD"));
                        if (rs.getString("AD_CODE_NAME").equalsIgnoreCase("GP")) {
                            alwded.setAdvalue(payComponent.getGp());
                        } else {
                            alwded.setAdvalue(rs.getInt("FIXEDVALUE"));
                        }
                        alwded.setRownum(rs.getInt("ROW_NO"));
                        alwded.setRepCol(rs.getInt("REP_COL"));
                        alwded.setAltUnit(rs.getString("ALT_UNIT"));
                        alwded.setSchedule(rs.getString("SCHEDULE"));
                        allowanceList.add(alwded);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return allowanceList;
    }

    public String getGPFSeries(String gpfSeries) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String gpfbtid = "";
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT BT_ID FROM G_GPF_TYPE WHERE GPF_TYPE = ?");
            pstmt.setString(1, gpfSeries);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                gpfbtid = rs.getString("BT_ID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return gpfbtid;
    }

    @Override
    public ArrayList getDeductionList(SectionDtlSPCWiseEmp sdswe, String configuredlvl, BigDecimal billGroupId, String offCode, int aqday, int payday, int workday, int traing_days, PayComponent payComponent, String bill_type, String v_depcode, String gpfSeries) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList deductionList = new ArrayList();
        try {
            //if (sdswe.getJobtypeid() != 1) {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT AD_CODE,AD_DESC,AD_CODE_NAME,AD_TYPE,ALT_UNIT,DED_TYPE,SCHEDULE,REP_COL,ROW_NO,BT_ID FROM G_AD_LIST WHERE AD_TYPE='D' AND SHOW_IN_ADLIST='Y' ORDER BY  CAST (AD_CODE AS INTEGER )");
            rs = pstmt.executeQuery();
            String v_btid = null;
            String v_gpfbtid = getGPFSeries(gpfSeries);
            while (rs.next()) {
                AllowanceDeduction alwded = new AllowanceDeduction();
                alwded.setAdcode(rs.getString("AD_CODE"));
                String v_schedule = rs.getString("SCHEDULE");
                v_btid = rs.getString("BT_ID");
                if (v_schedule != null && !v_schedule.equalsIgnoreCase("HRR") && !v_schedule.equalsIgnoreCase("WRR") && !v_schedule.equalsIgnoreCase("LIC") && !v_schedule.equalsIgnoreCase("CPF") && !v_schedule.equalsIgnoreCase("PT")) {
                    String v_adcodename = rs.getString("AD_CODE_NAME");
                    int v_adamt = 0;
                    if (payday < 15 || (v_depcode != null && v_depcode.equals("05"))) {
                        if (!v_adcodename.equals("CGEGIS")) {
                            v_adamt = 0;
                        }
                    } else {
                        ADOBJECT v_adobject = getAdAmt(sdswe.getSpc(), sdswe.getEmpid(), alwded.getAdcode(), offCode, aqday, payday, workday, traing_days, payComponent);
                        if (v_schedule.equals("GPF") || v_schedule.equals("GPDD") || v_schedule.equals("GPIR")) {
                            v_btid = v_gpfbtid;
                        }
                        v_adamt = v_adobject.adamt;

                    }

                    alwded.setAddesc(rs.getString("AD_DESC"));
                    alwded.setAdcodename(v_adcodename);
                    alwded.setAdamttype(rs.getString("AD_TYPE"));
                    alwded.setDedType(rs.getString("DED_TYPE"));
                    alwded.setHead(v_btid);
                    alwded.setAdvalue(v_adamt);
                    alwded.setRownum(rs.getInt("ROW_NO"));
                    alwded.setRepCol(rs.getInt("REP_COL"));
                    alwded.setAltUnit(rs.getString("ALT_UNIT"));
                    alwded.setSchedule(rs.getString("SCHEDULE"));
                    deductionList.add(alwded);
                }
            }
            //}
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }

        return deductionList;
    }

    @Override
    public ArrayList getDeductionList(SectionDtlSPCWiseEmp sdswe, String configuredlvl, AqmastModel aqMastModel, BillMastModel billMastModel, int aqday, int payday, int workday, int traing_days, PayComponent payComponent, String gpfSeries) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList deductionList = new ArrayList();
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT AD_CODE,AD_DESC,AD_CODE_NAME,AD_TYPE,ALT_UNIT,DED_TYPE,SCHEDULE,REP_COL,ROW_NO,BT_ID FROM G_AD_LIST WHERE AD_TYPE='D' AND SHOW_IN_ADLIST='Y' ORDER BY  CAST (AD_CODE AS INTEGER )");
            rs = pstmt.executeQuery();
            String v_btid = null;
            String v_gpfbtid = getGPFSeries(gpfSeries);
            while (rs.next()) {
                AllowanceDeduction alwded = new AllowanceDeduction();
                alwded.setAdcode(rs.getString("AD_CODE"));
                String v_schedule = rs.getString("SCHEDULE");
                v_btid = rs.getString("BT_ID");
                if (v_schedule != null && !v_schedule.equalsIgnoreCase("HRR") && !v_schedule.equalsIgnoreCase("WRR") && !v_schedule.equalsIgnoreCase("LIC") && !v_schedule.equalsIgnoreCase("CPF") && !v_schedule.equalsIgnoreCase("PT")) {
                    String v_adcodename = rs.getString("AD_CODE_NAME");
                    int v_adamt = 0;
                    if (payday < 15 || (aqMastModel.getDeptCode() != null && aqMastModel.getDeptCode().equals("05"))) {
                        if (!v_adcodename.equals("CGEGIS")) {
                            v_adamt = 0;
                        }
                    } else {
                        ADOBJECT v_adobject = getAdAmt(sdswe, alwded.getAdcode(), billMastModel.getOffCode(), aqday, payday, workday, traing_days, payComponent, aqMastModel.getAqMonth(), aqMastModel.getAqYear());

                        if (v_adobject.getBtid() != null && !v_adobject.getBtid().equals("")) {
                            v_btid = v_adobject.btid;
                            if (v_schedule.equals("GPF") || v_schedule.equals("GPDD") || v_schedule.equals("GPIR")) {
                                v_btid = v_gpfbtid;
                            }
                        }
                        v_adamt = v_adobject.adamt;

                    }

                    alwded.setAddesc(rs.getString("AD_DESC"));
                    alwded.setAdcodename(v_adcodename);
                    alwded.setAdamttype(rs.getString("AD_TYPE"));
                    alwded.setDedType(rs.getString("DED_TYPE"));
                    alwded.setHead(v_btid);
                    alwded.setAdvalue(v_adamt);
                    alwded.setRownum(rs.getInt("ROW_NO"));
                    alwded.setRepCol(rs.getInt("REP_COL"));
                    alwded.setAltUnit(rs.getString("ALT_UNIT"));
                    alwded.setSchedule(rs.getString("SCHEDULE"));
                    deductionList.add(alwded);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return deductionList;
    }

    @Override
    public ArrayList getDeductionList(String empId) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList deductionList = new ArrayList();
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT G_AD_LIST.AD_CODE,AD_DESC,AD_CODE_NAME,AD_TYPE,ALT_UNIT,DED_TYPE,SCHEDULE,REP_COL,ROW_NO,G_AD_LIST.BT_ID,update_ad_info.FIXEDVALUE FROM G_AD_LIST "
                    + "LEFT OUTER JOIN (SELECT * FROM update_ad_info WHERE updation_ref_code = ? AND where_updated = 'E')update_ad_info ON G_AD_LIST.AD_CODE = update_ad_info.REF_AD_CODE "
                    + "WHERE AD_TYPE='D' AND SHOW_IN_ADLIST='Y' ORDER BY  AD_CODE");
            pstmt.setString(1, empId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String v_schedule = rs.getString("SCHEDULE");
                if (v_schedule != null && !v_schedule.equalsIgnoreCase("HRR") && !v_schedule.equalsIgnoreCase("WRR") && !v_schedule.equalsIgnoreCase("LIC") && !v_schedule.equalsIgnoreCase("CPF") && !v_schedule.equalsIgnoreCase("PT")) {
                    AllowanceDeduction alwded = new AllowanceDeduction();
                    alwded.setAdcode(rs.getString("AD_CODE"));
                    alwded.setAddesc(rs.getString("AD_DESC"));
                    alwded.setAdcodename(rs.getString("AD_CODE_NAME"));
                    alwded.setAdamttype(rs.getString("AD_TYPE"));
                    alwded.setDedType(rs.getString("DED_TYPE"));
                    alwded.setHead(rs.getString("BT_ID"));
                    alwded.setAdvalue(rs.getInt("FIXEDVALUE"));
                    alwded.setRownum(rs.getInt("ROW_NO"));
                    alwded.setRepCol(rs.getInt("REP_COL"));
                    alwded.setAltUnit(rs.getString("ALT_UNIT"));
                    alwded.setSchedule(rs.getString("SCHEDULE"));
                    deductionList.add(alwded);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return deductionList;
    }

    public ADOBJECT getAdAmt(SectionDtlSPCWiseEmp sdswe, String adcode, String offCode, int aqday, int payday, int workday, int traing_days, PayComponent payComponent, int aqmonth, int aqyear) {
        int v_finaladAmt = 0;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String v_adconfigured = "N";
        int v_adamt = 0;
        int v_fixed = 0;
        String v_ad_orgformula = "";
        String v_btId = "";
        int v_reccnt = 0;
        String v_gpc = "";
        try {
            con = dataSource.getConnection();
            if (v_adconfigured.equals("N")) {
                /* Allowance or Deduction amount is updated at the Employee End */
                pstmt = con.prepareStatement("SELECT FIXEDVALUE,IS_FIXED,AD_ORGFORMULA,BT_ID FROM UPDATE_AD_INFO WHERE UPDATION_REF_CODE = ? AND "
                        + "WHERE_UPDATED = 'E'  AND REF_AD_CODE = ?");
                pstmt.setString(1, sdswe.getEmpid());
                pstmt.setString(2, adcode);
                rs = pstmt.executeQuery();

                if (rs.next()) {
                    v_reccnt = 1;
                    v_adamt = rs.getInt("FIXEDVALUE");
                    v_fixed = rs.getInt("IS_FIXED");
                    v_ad_orgformula = rs.getString("AD_ORGFORMULA");
                    v_btId = rs.getString("BT_ID");
                    if (v_fixed == 1) {
                        v_finaladAmt = v_adamt;
                    } else {
                        v_finaladAmt = calculate(v_ad_orgformula, sdswe.getEmpid(), aqmonth, aqyear);
                    }
                    v_adconfigured = "Y";
                }
                pstmt.close();
                pstmt = null;
            }

            /* Allowance or Deduction amount is updated at the POST End */
            if (v_finaladAmt == 0 && v_adconfigured.equals("N") && sdswe.getSpc() != null) {
                pstmt = con.prepareStatement("SELECT GPC FROM G_SPC WHERE SPC=?");
                pstmt.setString(1, sdswe.getSpc());
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    v_gpc = rs.getString("GPC");
                }

                pstmt = con.prepareStatement("SELECT FIXEDVALUE,IS_FIXED,AD_ORGFORMULA FROM UPDATE_AD_INFO WHERE "
                        + "SUBSTR(UPDATION_REF_CODE,0,14) = ? AND SUBSTR(UPDATION_REF_CODE,15) =? AND WHERE_UPDATED = 'P'  AND"
                        + " REF_AD_CODE = ?");
                pstmt.setString(1, offCode);
                pstmt.setString(2, v_gpc);
                pstmt.setString(3, adcode);
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    v_reccnt = 1;
                    v_adamt = rs.getInt("FIXEDVALUE");
                    v_fixed = rs.getInt("IS_FIXED");
                    v_ad_orgformula = rs.getString("AD_ORGFORMULA");
                    if (v_fixed == 1) {
                        v_finaladAmt = v_adamt;
                    } else {

                    }
                    v_adconfigured = "Y";
                }
                pstmt.close();
                pstmt = null;
            }
            /* Allowance or Deduction amount is updated at the Office End */
            if (v_finaladAmt == 0 && v_adconfigured.equals("N")) {
                pstmt = con.prepareStatement("SELECT FIXEDVALUE,IS_FIXED,AD_ORGFORMULA,BT_ID FROM UPDATE_AD_INFO WHERE UPDATION_REF_CODE = ? AND WHERE_UPDATED = 'O' AND REF_AD_CODE = ?");
                pstmt.setString(1, offCode);
                pstmt.setString(2, adcode);
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    v_reccnt = 1;
                    v_adamt = rs.getInt("FIXEDVALUE");
                    v_fixed = rs.getInt("IS_FIXED");
                    v_ad_orgformula = rs.getString("AD_ORGFORMULA");
                    v_btId = rs.getString("BT_ID");

                    if (v_fixed == 1) {
                        v_finaladAmt = v_adamt;
                    } else {

                    }
                    v_adconfigured = "Y";
                }
                pstmt.close();
                pstmt = null;
            }

            /* Allowance or Deduction amount is Calculated From Global Formula */
            if (v_reccnt == 0) {
                if (v_finaladAmt == 0 && v_adconfigured.equals("N")) {

                    pstmt = con.prepareStatement("SELECT FIXEDVALUE,IS_FIXED,AD_ORGFORMULA,BT_ID,SCHEDULE,OBJECT_HEAD,AD_TYPE FROM G_AD_LIST WHERE AD_CODE = ?");
                    pstmt.setString(1, adcode);
                    rs = pstmt.executeQuery();
                    if (rs.next()) {
                        v_reccnt = 1;
                        v_adamt = rs.getInt("FIXEDVALUE");
                        v_fixed = rs.getInt("IS_FIXED");
                        v_ad_orgformula = rs.getString("AD_ORGFORMULA");
                        if (rs.getString("AD_TYPE") != null && rs.getString("AD_TYPE").equals("A")) {
                            v_btId = rs.getString("OBJECT_HEAD");
                        } else {
                            v_btId = rs.getString("BT_ID");
                        }
                        if (adcode.equals("52")) {
                            if (payComponent.getIspayrevised() == null || payComponent.getIspayrevised().equalsIgnoreCase("N")) {
                                if ((aqmonth >= 6 && aqyear == 2018) || (aqmonth >= 0 && aqyear >= 2019)) {
                                    v_finaladAmt = new Long(Math.round(((payComponent.getBasic() + payComponent.getGp()) * 1.48))).intValue();
                                } else if (aqmonth >= 3 && aqmonth <= 7 && aqyear < 2019) {
                                    v_finaladAmt = new Long(Math.round(((payComponent.getBasic() + payComponent.getGp()) * 1.42))).intValue();
                                } else {
                                    v_finaladAmt = new Long(Math.round(((payComponent.getBasic() + payComponent.getGp()) * 1.39))).intValue();
                                }
                            } else {
                                  if ((aqmonth >= 6 && aqmonth <= 11 && aqyear == 2019) || (aqmonth >= 0 && aqyear >= 2020 )) {
                                    v_finaladAmt = new Long(Math.round(payComponent.getBasic() * 0.17)).intValue();
                                }else if (aqmonth >= 2 && aqmonth <= 5 && aqyear == 2019) {
                                    v_finaladAmt = new Long(Math.round(payComponent.getBasic() * 0.12)).intValue();
                                }else if ((aqmonth > 7 && aqmonth <= 11 && aqyear == 2018) || (aqmonth < 2 && aqyear == 2019)) {
                                    v_finaladAmt = new Long(Math.round(payComponent.getBasic() * 0.09)).intValue();
                                } else if (aqmonth >= 3 && aqmonth <= 7 && aqyear == 2018) {
                                    v_finaladAmt = new Long(Math.round(payComponent.getBasic() * 0.07)).intValue();
                                } else {
                                    v_finaladAmt = new Long(Math.round(payComponent.getBasic() * 0.05)).intValue();
                                }
                            }
                        } else {
                            if (v_fixed == 1) {
                                v_finaladAmt = v_adamt;
                            } else {

                            }
                        }
                        v_adconfigured = "Y";
                    }

                    pstmt.close();
                    pstmt = null;

                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }

        ADOBJECT adObject = new ADOBJECT();
        adObject.setAdamt(v_finaladAmt);
        adObject.setBtid(v_btId);
        return adObject;
    }

    private int calculate(String oformula, String empid, int month, int year) {
        Connection con = null;
        Statement stmt = null;
        ResultSet res = null;
        int advalue = 0;
        try {
            con = dataSource.getConnection();
            if (oformula != null) {
                String sql
                        = sql = oformula.replaceAll("@empid", empid);
                sql = sql.replaceAll("@month", String.valueOf(month));
                sql = sql.replaceAll("@year", String.valueOf(year));
                stmt = con.createStatement();
                res = stmt.executeQuery(sql);
                if (res.next()) {
                    advalue = new Double(Math.round(res.getDouble("ADVALUE"))).intValue();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(res, stmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return advalue;
    }
}
