/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.payroll.schedule;

import hrms.common.DataBaseFunctions;
import hrms.model.payroll.schedule.Schedule;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Surendra
 */
public class BillFrontpageDAOImpl implements BillFrontpageDAO {

    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    
    @Override
    public String getDPHead(String billNo,int aqMonth, int aqYear){
        
        Statement st = null;
        ResultSet rs = null;
        String dphead = "";
        Connection con=null;
        try{
            con=dataSource.getConnection();
            st = con.createStatement();
            
            String sql = "Select BT_ID from( (Select AQ_MAST.AQSL_NO from AQ_MAST where AQ_MAST.BILL_NO = '"+billNo+"' AND AQ_MAST.aq_month="+aqMonth+" AND AQ_MAST.aq_year="+aqYear+" )" + 
                         " AQ_MAST inner join" + 
                         " (Select AQ_DTLS.AQSL_NO,AQ_DTLS.AD_AMT,BT_ID from AQ_DTLS where  AD_CODE ='DP' AND AQ_DTLS.aq_month="+aqMonth+" AND AQ_DTLS.aq_year="+aqYear+" AND AD_AMT > 0) AQ_DTLS" + 
                         " on AQ_MAST.AQSL_NO = AQ_DTLS.AQSL_NO ) GROUP BY BT_ID";
            rs = st.executeQuery(sql);
            if(rs.next()){
                dphead = rs.getString("BT_ID");
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            DataBaseFunctions.closeSqlObjects(rs,st);
            DataBaseFunctions.closeSqlObjects(con);
        }
      return dphead;   
    }

    
    public ArrayList getScheduleListWithADCode(String billNo,int aqMonth, int aqYear)  {

        Connection con = null;
        ResultSet rs = null;
        ArrayList al = new ArrayList();
        Schedule sc = null;
        Statement st = null;
        try {
            con = dataSource.getConnection();
            st = con.createStatement();

            rs = st.executeQuery(" SELECT AQ_DTLS.SCHEDULE,NOW_DEDN,AD_AMT,BT_ID "
                    + "FROM (SELECT AD_CODE,SCHEDULE,DED_TYPE,NOW_DEDN,SUM(AD_AMT) AD_AMT,BT_ID FROM AQ_DTLS INNER JOIN  ("
                    + "SELECT AQ_MAST.AQSL_NO FROM AQ_MAST WHERE  AQ_MAST.BILL_NO='"+billNo+"' AND AQ_MAST.aq_month="+aqMonth+" AND aq_year="+aqYear+")AQ_MAST "
                    + " ON AQ_DTLS.AQSL_NO = AQ_MAST.AQSL_NO WHERE AD_TYPE='D' and SCHEDULE != 'PVTL' and schedule != 'PVTD'"
                    + " GROUP BY AQ_DTLS.SCHEDULE,DED_TYPE,NOW_DEDN,AD_CODE,BT_ID ORDER BY AQ_DTLS.SCHEDULE)AQ_DTLS WHERE AD_AMT >0");

            while (rs.next()) {
                String schedule = rs.getString("SCHEDULE");

                if (schedule.equals("GPF")) {
                    sc = new Schedule();
                    sc.setScheduleName("GPF");
                    sc.setObjectHead(StringUtils.defaultString(rs.getString("BT_ID")));
                    sc.setSchAmount(rs.getString("AD_AMT"));
                }else if (schedule.equals("GA")) {
                    sc = new Schedule();
                    sc.setScheduleName("GA");
                    sc.setObjectHead(StringUtils.defaultString(rs.getString("BT_ID")));
                    sc.setSchAmount(rs.getString("AD_AMT"));
                }else if (schedule.equals("TPF") || schedule.equals("TPFGA")) {
                    sc = new Schedule();
                    sc.setScheduleName("TPF");
                    sc.setObjectHead(StringUtils.defaultString(rs.getString("BT_ID")));
                    sc.addSchAmount(rs.getInt("AD_AMT"));
                }else if (schedule.equals("CPF") || schedule.equals("NPSL")) {
                    sc = new Schedule();
                    sc.setScheduleName("CPF");
                    sc.setObjectHead(StringUtils.defaultString(rs.getString("BT_ID")));
                    sc.addSchAmount(rs.getInt("AD_AMT"));
                }else {
                    sc = new Schedule();
                    sc.setObjectHead(StringUtils.defaultString(rs.getString("BT_ID")));
                    if (rs.getString("NOW_DEDN") != null && !rs.getString("NOW_DEDN").equals("")) {
                        sc.setScheduleName(rs.getString("SCHEDULE") + " (" + rs.getString("NOW_DEDN") + ")");
                    } else {
                        sc.setScheduleName(rs.getString("SCHEDULE"));
                    }
                    sc.setSchAmount(rs.getString("AD_AMT"));
                }
                al.add(sc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs,st);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return al;
    }

}
