package hrms.dao.report.officewisesanctionedstrength;

import hrms.common.DataBaseFunctions;
import hrms.model.report.officewisesanctionedstrength.OfficeWiseSanctionedStrengthBean;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;

public class OfficeWiseSanctionedStrengthDAOImpl implements OfficeWiseSanctionedStrengthDAO {

    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List getSanctionedStrengthList(String offCode) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        List datalist = new ArrayList();

        OfficeWiseSanctionedStrengthBean bean = null;
        int total = 0;
        try {
            con = this.dataSource.getConnection();

            String sql = "select aer_id,fy,sancnosgrp_a,sancnosgrp_b,sancnosgrp_c,sancnosgrp_d,grant_in_aid from aer_report_submit where off_code=? order by fy desc";
            pst = con.prepareStatement(sql);
            pst.setString(1, offCode);
            rs = pst.executeQuery();
            while (rs.next()) {
                bean = new OfficeWiseSanctionedStrengthBean();
                bean.setAerId(rs.getString("aer_id"));
                bean.setFinancialYear(rs.getString("fy"));
                bean.setGroupAData(rs.getString("sancnosgrp_a"));
                bean.setGroupBData(rs.getString("sancnosgrp_b"));
                bean.setGroupCData(rs.getString("sancnosgrp_c"));
                bean.setGroupDData(rs.getString("sancnosgrp_d"));
                bean.setGrantInAid(rs.getString("grant_in_aid"));
                total = rs.getInt("sancnosgrp_a") + rs.getInt("sancnosgrp_b") + rs.getInt("sancnosgrp_c") + rs.getInt("sancnosgrp_d") + rs.getInt("grant_in_aid");
                bean.setTotal(total);
                datalist.add(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return datalist;
    }

    @Override
    public void saveSanctionedPostData(String offCode, OfficeWiseSanctionedStrengthBean bean) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        boolean found = false;
        try {
            con = this.dataSource.getConnection();

            if (bean.getAerId() != null && !bean.getAerId().equals("")) {
                String sql = "UPDATE aer_report_submit SET fy=?,sancnosgrp_a=?,sancnosgrp_b=?,sancnosgrp_c=?,sancnosgrp_d=?,grant_in_aid=? WHERE aer_id=?";
                pst = con.prepareStatement(sql);
                pst.setString(1, bean.getFinancialYear());
                if(bean.getGroupAData() != null && !bean.getGroupAData().equals("")){
                    pst.setInt(2, Integer.parseInt(bean.getGroupAData()));
                }else{
                    pst.setInt(2, 0);
                }
                if(bean.getGroupBData() != null && !bean.getGroupBData().equals("")){
                    pst.setInt(3, Integer.parseInt(bean.getGroupBData()));
                }else{
                    pst.setInt(3, 0);
                }
                if(bean.getGroupCData() != null && !bean.getGroupCData().equals("")){
                    pst.setInt(4, Integer.parseInt(bean.getGroupCData()));
                }else{
                    pst.setInt(4, 0);
                }
                if(bean.getGroupDData() != null && !bean.getGroupDData().equals("")){
                    pst.setInt(5, Integer.parseInt(bean.getGroupDData()));
                }else{
                    pst.setInt(5, 0);
                }
                if(bean.getGrantInAid() != null && !bean.getGrantInAid().equals("")){
                    pst.setInt(6, Integer.parseInt(bean.getGrantInAid()));
                }else{
                    pst.setInt(6, 0);
                }
                pst.setInt(7, Integer.parseInt(bean.getAerId()));
                pst.executeUpdate();
            } else {
                String sql = "INSERT INTO aer_report_submit(off_code,controlling_spc,file_name,status,fy,sancnosgrp_a,sancnosgrp_b ,sancnosgrp_c ,sancnosgrp_d,grant_in_aid) values(?,?,?,?,?,?,?,?,?,?)";
                pst = con.prepareStatement(sql);
                pst.setString(1, offCode);
                pst.setString(2, "");
                pst.setString(3, "");
                pst.setString(4, "");
                pst.setString(5, bean.getFinancialYear());
                if(bean.getGroupAData() != null && !bean.getGroupAData().equals("")){
                    pst.setInt(6, Integer.parseInt(bean.getGroupAData()));
                }else{
                    pst.setInt(6, 0);
                }
                if(bean.getGroupBData() != null && !bean.getGroupBData().equals("")){
                    pst.setInt(7, Integer.parseInt(bean.getGroupBData()));
                }else{
                    pst.setInt(7, 0);
                }
                if(bean.getGroupCData() != null && !bean.getGroupCData().equals("")){
                    pst.setInt(8, Integer.parseInt(bean.getGroupCData()));
                }else{
                    pst.setInt(8, 0);
                }
                if(bean.getGroupDData() != null && !bean.getGroupDData().equals("")){
                    pst.setInt(9, Integer.parseInt(bean.getGroupDData()));
                }else{
                    pst.setInt(9, 0);
                }
                if(bean.getGrantInAid() != null && !bean.getGrantInAid().equals("")){
                    pst.setInt(10, Integer.parseInt(bean.getGrantInAid()));
                }else{
                    pst.setInt(10, 0);
                }
                pst.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    @Override
    public String verifyDuplicate(String offCode, String financialYear) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        String isDuplicate = "N";
        try {
            con = this.dataSource.getConnection();

            String sql = "SELECT * FROM aer_report_submit WHERE OFF_CODE=? AND FY=?";
            pst = con.prepareStatement(sql);
            pst.setString(1, offCode);
            pst.setString(2, financialYear);
            rs = pst.executeQuery();
            if (rs.next()) {
                isDuplicate = "Y";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return isDuplicate;
    }

    @Override
    public OfficeWiseSanctionedStrengthBean getSanctionedStrengthData(OfficeWiseSanctionedStrengthBean bean) {
        
        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;
        
        try{
            con = this.dataSource.getConnection();
            
            String sql = "select aer_id,fy,sancnosgrp_a,sancnosgrp_b,sancnosgrp_c,sancnosgrp_d,grant_in_aid from aer_report_submit where aer_id=?";
            
            pst = con.prepareStatement(sql);
            pst.setInt(1,Integer.parseInt(bean.getAerId()));
            rs = pst.executeQuery();
            if(rs.next()){
                bean.setFinancialYear(rs.getString("fy"));
                bean.setGroupAData(rs.getString("sancnosgrp_a"));
                bean.setGroupBData(rs.getString("sancnosgrp_b"));
                bean.setGroupCData(rs.getString("sancnosgrp_c"));
                bean.setGroupDData(rs.getString("sancnosgrp_d"));
                bean.setGrantInAid(rs.getString("grant_in_aid"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
      return bean;  
    }

    @Override
    public HashMap<String, String> getFinancialYearList() {
        
        Connection con = null;

        PreparedStatement ps = null;
        ResultSet rs = null;

        HashMap<String, String> list = new HashMap<String, String>();
        try {
            con = dataSource.getConnection();

            String sql = "SELECT fy FROM financial_year WHERE active='Y' ORDER BY fy";

            ps = con.prepareStatement(sql);

            rs = ps.executeQuery();
            while (rs.next()) {
                list.put(rs.getString("fy"), rs.getString("fy"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, ps);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return list;

    }

}
