/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.qtrallotment;

import hrms.common.CommonFunctions;
import hrms.common.DataBaseFunctions;
import hrms.model.qtrallotment.QuarterAllotment;
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
 * @author lenovo pc
 */
public class QuarterAllotmentDAOImpl implements QuarterAllotmentDAO {

    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List QuaterAllotSt(String empId) {
        List list = null;
        ResultSet rs = null;
        Statement st = null;
        Connection con = null;
        QuarterAllotment qas = new QuarterAllotment();
        list = new ArrayList();
        try {
            con = dataSource.getConnection();
            st = con.createStatement();
            rs = st.executeQuery("SELECT QA_ID,NOT_ID,EMP_ID,QUARTER_NO,ALLOTMENT_DATE,POSSESSION_DATE,QUARTER_RENT,ADDRESS,IF_SURRENDERED,WATER_RENT,CONSUMER_NO,SEWERAGE_RENT,POOL_NAME,EMP_QTR_ALLOT.Q_ID,IS_GET_HRA FROM (SELECT QA_ID,NOT_ID,EMP_ID,QUARTER_NO,ALLOTMENT_DATE,POSSESSION_DATE,QUARTER_RENT,ADDRESS,IF_SURRENDERED,WATER_RENT,CONSUMER_NO,SEWERAGE_RENT,Q_ID,IS_GET_HRA "
                    + " FROM EMP_QTR_ALLOT where EMP_ID='" + empId + "') EMP_QTR_ALLOT "
                    + " LEFT OUTER JOIN G_QTR_POOL ON EMP_QTR_ALLOT.Q_ID=G_QTR_POOL.Q_ID");
            while (rs.next()) {
                qas.setQtrId(rs.getString("QA_ID"));
                qas.setNotId(rs.getString("NOT_ID"));
                qas.setQtrNo(rs.getString("QUARTER_NO"));
                if (rs.getDate("ALLOTMENT_DATE") != null) {
                    qas.setQtrAllotdate(CommonFunctions.getFormattedOutputDate3(rs.getDate("ALLOTMENT_DATE")));
                }
                if (rs.getDate("POSSESSION_DATE") != null) {
                    qas.setPossesdate(CommonFunctions.getFormattedOutputDate3(rs.getDate("POSSESSION_DATE")));
                }
                qas.setQtrRent(rs.getString("QUARTER_RENT"));
                qas.setWtrRent(rs.getString("WATER_RENT"));
                qas.setConsumerNo(rs.getString("CONSUMER_NO"));
                qas.setSewerageRent(rs.getString("SEWERAGE_RENT"));
                qas.setAddress(rs.getString("ADDRESS"));
                qas.setIfSurrendered(rs.getString("IF_SURRENDERED"));
                qas.setQtrIdforAllotFrom(rs.getString("Q_ID"));
                qas.setQtrForAllotName(rs.getString("POOL_NAME"));
                qas.setIsgetHra(rs.getString("IS_GET_HRA"));
                list.add(qas);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return list;
    }

    public QuarterAllotment editQuarterAllotment(String qtrAllotId) {
        ResultSet rs = null;
        Statement st = null;
        Connection con = null;
        QuarterAllotment qas = new QuarterAllotment();
        try {
            con = dataSource.getConnection();
            st = con.createStatement();
            rs = st.executeQuery("SELECT QA_ID,NOT_ID,EMP_ID,QUARTER_NO,ALLOTMENT_DATE,POSSESSION_DATE,QUARTER_RENT,ADDRESS,IF_SURRENDERED,WATER_RENT,CONSUMER_NO,SEWERAGE_RENT,POOL_NAME,EMP_QTR_ALLOT.Q_ID,IS_GET_HRA FROM (SELECT QA_ID,NOT_ID,EMP_ID,QUARTER_NO,ALLOTMENT_DATE,POSSESSION_DATE,QUARTER_RENT,ADDRESS,IF_SURRENDERED,WATER_RENT,CONSUMER_NO,SEWERAGE_RENT,Q_ID,IS_GET_HRA "
                    + " FROM EMP_QTR_ALLOT where QA_ID=" + qtrAllotId + ") EMP_QTR_ALLOT "
                    + " LEFT OUTER JOIN G_QTR_POOL ON EMP_QTR_ALLOT.Q_ID=G_QTR_POOL.Q_ID");
            while (rs.next()) {
                qas.setQtrId(rs.getString("QA_ID"));
                qas.setNotId(rs.getString("NOT_ID"));
                qas.setQtrNo(rs.getString("QUARTER_NO"));
                if (rs.getDate("ALLOTMENT_DATE") != null) {
                    qas.setQtrAllotdate(CommonFunctions.getFormattedOutputDate3(rs.getDate("ALLOTMENT_DATE")));
                }
                if (rs.getDate("POSSESSION_DATE") != null) {
                    qas.setPossesdate(CommonFunctions.getFormattedOutputDate3(rs.getDate("POSSESSION_DATE")));
                }
                qas.setQtrRent(rs.getString("QUARTER_RENT"));
                qas.setWtrRent(rs.getString("WATER_RENT"));
                qas.setConsumerNo(rs.getString("CONSUMER_NO"));
                qas.setSewerageRent(rs.getString("SEWERAGE_RENT"));
                qas.setAddress(rs.getString("ADDRESS"));
                qas.setIfSurrendered(rs.getString("IF_SURRENDERED"));
                qas.setQtrIdforAllotFrom(rs.getString("Q_ID"));
                qas.setQtrForAllotName(rs.getString("POOL_NAME"));
                qas.setIsgetHra(rs.getString("IS_GET_HRA"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return qas;
    }

    public int saveQuaterAllotmentRecord(QuarterAllotment quarterAllot) {
        PreparedStatement pst = null;
        Statement st = null;
        int mcode = 0;
        Connection con = null;
        int n = 0;
        try {
            con = dataSource.getConnection();
            st = con.createStatement();
            if (quarterAllot.getNotId() != null && !quarterAllot.getNotId().equals("")) {
                st.executeUpdate("delete from EMP_QTR_ALLOT where not_id='" + quarterAllot.getNotId() + "'");
            }
            mcode = CommonFunctions.getMaxCodeInteger("EMP_QTR_ALLOT", "QA_ID", con);
            pst = con.prepareStatement("INSERT INTO EMP_QTR_ALLOT(QA_ID,NOT_ID,NOT_TYPE,EMP_ID,QUARTER_NO,ALLOTMENT_DATE,POSSESSION_DATE,QUARTER_RENT,SEWERAGE_RENT,ADDRESS,IF_SURRENDERED,WATER_RENT,Q_ID,CONSUMER_NO,IS_GET_HRA) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            pst.setInt(1, mcode);
            pst.setString(2, quarterAllot.getNotId());
            pst.setString(3, quarterAllot.getNotType());
            pst.setString(4, quarterAllot.getEmpId());
            pst.setString(5, quarterAllot.getQtrNo());
            pst.setString(6, quarterAllot.getQtrAllotdate());
            pst.setString(7, quarterAllot.getPossesdate());
            pst.setString(8, quarterAllot.getQtrRent());
            pst.setString(9, quarterAllot.getSewerageRent());
            pst.setString(10, quarterAllot.getAddress());
            pst.setString(11, null);
            pst.setString(12, quarterAllot.getWtrRent());
            pst.setString(13, quarterAllot.getQtrIdforAllotFrom());
            pst.setString(14, quarterAllot.getConsumerNo());
            pst.setString(15, quarterAllot.getIsgetHra());
            n = pst.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst, st);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return n;
    }

    public int saveQuaterSurrenderRecord(QuarterAllotment quarterAllot) {
        PreparedStatement pst = null;
        Statement st = null;
        Statement st1 = null;
        int mcode = 0;
        Connection con = null;
        int n = 0;
        try {
            con = dataSource.getConnection();
            st1 = con.createStatement();
            if (quarterAllot.getNotId() != null && !quarterAllot.getNotId().equals("")) {
                st1.executeUpdate("delete from EMP_QTR_SURRENDER where not_id='" + quarterAllot.getNotId() + "'");
            }
            mcode = CommonFunctions.getMaxCodeInteger("EMP_QTR_SURRENDER", "QS_ID", con);
            pst = con.prepareStatement("INSERT INTO EMP_QTR_SURRENDER(QS_ID,QA_ID,NOT_ID,NOT_TYPE,EMP_ID,SURRENDER_DATE) VALUES(?,?,?,?,?,?)");
            pst.setInt(1, mcode);
            pst.setInt(2, Integer.parseInt(quarterAllot.getQtrId()));
            pst.setString(3, quarterAllot.getNotId());
            pst.setString(4, quarterAllot.getNotType());
            pst.setString(5, quarterAllot.getEmpId());
            pst.setString(6, quarterAllot.getSurrendate());
            n = pst.executeUpdate();
            st.executeUpdate("UPDATE EMP_QTR_ALLOT SET IF_SURRENDERED='Y' WHERE QA_ID=" + quarterAllot.getQtrId());
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst, st, st1);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return n;
    }

    public QuarterAllotment getSurrenderEditRecords(String qtrSurId) {
        QuarterAllotment qas = new QuarterAllotment();
        String surdate = null;
        ResultSet rs = null;
        Statement st = null;
        Statement st1 = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            st1 = con.createStatement();
            rs = st1.executeQuery("SELECT * FROM (SELECT * FROM EMP_QTR_SURRENDER WHERE QS_ID=" + qtrSurId + ") EMP_QTR_SURRENDER INNER JOIN EMP_QTR_ALLOT ON EMP_QTR_SURRENDER.QA_ID = EMP_QTR_ALLOT.QA_ID");
            while (rs.next()) {
                qas.setQtrId(rs.getString("QA_ID"));
                qas.setQtrNo(rs.getString("QUARTER_NO"));
                if (rs.getDate("ALLOTMENT_DATE") != null) {
                    qas.setQtrAllotdate(CommonFunctions.getFormattedOutputDate3(rs.getDate("ALLOTMENT_DATE")));
                }
                if (rs.getDate("POSSESSION_DATE") != null) {
                    qas.setPossesdate(CommonFunctions.getFormattedOutputDate3(rs.getDate("POSSESSION_DATE")));
                }
                qas.setQtrRent(rs.getString("QUARTER_RENT"));
                qas.setAddress(rs.getString("ADDRESS"));
                if (rs.getDate("SURRENDER_DATE") != null) {
                    qas.setSurrendate(CommonFunctions.getFormattedOutputDate3(rs.getDate("SURRENDER_DATE")));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return qas;
    }

    public void deleteQtrAllot(String qtrid, String empid) {
        Statement st = null;
        Statement st1 = null;
        ResultSet rs = null;
        Statement st2 = null;
        String qtrNo = null;
        String ifsuendered = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            st = con.createStatement();
            st1 = con.createStatement();
            st2 = con.createStatement();
            st.executeUpdate("Delete EMP_QTR_ALLOT where QA_ID='" + qtrid + "'");
            rs = st1.executeQuery("select QUARTER_NO,IF_SURRENDERED from EMP_QTR_ALLOT where emp_id='" + empid + "' order by QA_ID DESC");
            if (rs.next()) {
                qtrNo = rs.getString("QUARTER_NO");
                ifsuendered = rs.getString("IF_SURRENDERED");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(st, st1, st2);
            DataBaseFunctions.closeSqlObjects(rs);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    public void deleteQtrSurrendRecords(String qtrid, String surid, String empid) {
        Statement st = null;
        Statement st1 = null;
        ResultSet rs = null;
        String qtrNo = null;
        String ifsuendered = null;
        Connection con = null;
        try {
            con = dataSource.getConnection();
            st = con.createStatement();
            st1 = con.createStatement();
            st1.executeUpdate("Delete from EMP_QTR_SURRENDER where qs_id='" + surid + "'");
            st.executeUpdate("UPDATE EMP_QTR_ALLOT SET IF_SURRENDERED=" + null + " where QA_ID='" + qtrid + "'");
            rs = st1.executeQuery("select QUARTER_NO,IF_SURRENDERED from EMP_QTR_ALLOT where emp_id='" + empid + "' order by QA_ID DESC");
            if (rs.next()) {
                qtrNo = rs.getString("QUARTER_NO");
                ifsuendered = rs.getString("IF_SURRENDERED");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(st, st1);
            DataBaseFunctions.closeSqlObjects(rs);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

}
