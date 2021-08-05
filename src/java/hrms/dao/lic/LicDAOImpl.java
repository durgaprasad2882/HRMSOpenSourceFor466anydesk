/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.lic;

import hrms.common.CalendarCommonMethods;
import hrms.common.DataBaseFunctions;
import hrms.model.lic.Lic;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 *
 * @author Manas Jena
 */
public class LicDAOImpl implements LicDAO {

    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List getLicList(String empId) {
        List<Lic> licEmpList = new ArrayList();
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        try {
            conn = dataSource.getConnection();
            String licListQuery = "SELECT * FROM EMP_LIC INNER JOIN G_LIC_TYPE ON EMP_LIC.INSURANCE_TYPE = G_LIC_TYPE.LIC_TYPE WHERE STATUS='SANCTIONED' AND EMP_ID=?";
            pstmt = conn.prepareStatement(licListQuery);
            pstmt.setString(1, empId);
            rs = pstmt.executeQuery();
            String status = "";
            int i = 0;
            while (rs.next()) {
                Lic licBean = new Lic();
                licBean.setSlno(i);
                licBean.setElId(rs.getBigDecimal("EL_ID"));
                licBean.setInsuranceType(rs.getString("INSURANCE_TYPE"));
                licBean.setEmpid(empId);
                licBean.setPolicyNo(rs.getString("POLICY_NO"));
                licBean.setSubAmount(rs.getInt("SUB_AMT"));
                licBean.setWef(rs.getDate("WEF"));
                if (rs.getString("is_valid").equals("Y")) {
                    status = "Active";
                } else {
                    status = "Stopped";
                }                
                licBean.setStatus(status);
                licBean.setTrDataType(rs.getString("TR_DATA_TYPE"));
                licBean.setBtid(rs.getString("BT_ID"));
                licBean.setMonth(rs.getString("month"));
                licBean.setYear(rs.getString("year"));
                licEmpList.add(licBean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pstmt, rs, conn);
        }
        return licEmpList;
    }

    @Override
    public Lic editLicData(String empId, BigDecimal elId) {
        Lic lic = new Lic();
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        try {
            conn = dataSource.getConnection();
            String licEditQuery = "select * from EMP_LIC WHERE EMP_ID=? and EL_ID=?";
            pstmt = conn.prepareStatement(licEditQuery);
            pstmt.setString(1, empId);
            pstmt.setBigDecimal(2, elId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                lic.setElId(elId);
                lic.setPolicyNo(rs.getString("POLICY_NO"));
                lic.setSubAmount(rs.getInt("SUB_AMT"));
                lic.setNote(rs.getString("NOTE"));
                lic.setInsuranceType(rs.getString("INSURANCE_TYPE"));
                if (rs.getString("WEF") != null && !rs.getString("WEF").equals("")) {
                    lic.setWef(rs.getDate("WEF"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pstmt, rs, conn);
        }
        return lic;
    }

    @Override
    public int saveLicData(Lic licBean) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        Statement stmt = null;
        int result = 0;
        BigDecimal mcode = null;
        try {
            conn = dataSource.getConnection();

            if (licBean.getElId() != null && !licBean.getElId().equals("")) {
                pstmt = conn.prepareStatement("UPDATE EMP_LIC SET POLICY_NO=?,SUB_AMT=?,WEF=?,NOTE=?,INSURANCE_TYPE=? WHERE EL_ID=?");
                pstmt.setString(1, licBean.getPolicyNo());
                pstmt.setInt(2, licBean.getSubAmount());
                pstmt.setDate(3, new java.sql.Date(licBean.getWef().getTime()));
                pstmt.setString(4, licBean.getNote());
                pstmt.setString(5, licBean.getInsuranceType());
                pstmt.setBigDecimal(6, licBean.getElId());
                result = pstmt.executeUpdate();
            } else {
                String insertQry = "INSERT INTO EMP_LIC (EL_ID,EMP_ID,POLICY_NO,SUB_AMT,WEF,NOTE,STATUS,INSURANCE_TYPE) "
                        + "VALUES (?,?,?,?,?,?,?,?)";
                pstmt = conn.prepareStatement(insertQry);
                stmt = conn.createStatement();
                //----- Fixed values is entered in Life insurance -------------
                rs = stmt.executeQuery("select max(EL_id)+1 maxid from EMP_LIC");
                if (rs.next()) {
                    mcode = rs.getBigDecimal("maxid");
                }

                pstmt.setBigDecimal(1, mcode);
                pstmt.setString(2, licBean.getEmpid());
                pstmt.setString(3, licBean.getPolicyNo());
                pstmt.setInt(4, licBean.getSubAmount());
                pstmt.setDate(5, new java.sql.Date(licBean.getWef().getTime()));
                pstmt.setString(6, licBean.getNote());
                pstmt.setString(7, "SANCTIONED");
                pstmt.setString(8, licBean.getInsuranceType());

                result = pstmt.executeUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(stmt);
            DataBaseFunctions.closeSqlObjects(pstmt, rs, conn);
        }
        return result;
    }

    @Override
    public boolean deleteLicData(String elId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean delResult = false;
        try {
            conn = dataSource.getConnection();
            pstmt = conn.prepareStatement("DELETE from EMP_LIC where EL_ID = ? ");
            pstmt.setString(1, elId);
            int res = pstmt.executeUpdate();
            if (res == 1) {
                delResult = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pstmt);
        }
        return delResult;
    }

}
