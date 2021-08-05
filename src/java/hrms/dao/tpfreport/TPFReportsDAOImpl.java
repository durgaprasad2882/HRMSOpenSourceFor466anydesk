package hrms.dao.tpfreport;

import hrms.SelectOption;
import hrms.common.CommonFunctions;
import hrms.common.DataBaseFunctions;
import hrms.model.tpfreport.TPFReportBean;
import hrms.model.tpfreport.TPFReportList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;

public class TPFReportsDAOImpl implements TPFReportsDAO{
    
    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    @Override
    public List getTPFEmployeeList(int year, int month, String billNo) {
        
        Connection con = null;
        
        PreparedStatement pst = null;
        ResultSet rs = null;
        
        TPFReportBean tbean = null;
        
        List li = new ArrayList();
        try{
            con = this.dataSource.getConnection();
            
            String sql = "SELECT A.bill_no,B.aqsl_no,B.gpf_acc_no,B.emp_name,C.ad_amt FROM bill_mast A" +
                         " INNER JOIN aq_mast B ON A.bill_no=B.bill_no" +
                         " INNER JOIN aq_dtls C ON B.aqsl_no=C.aqsl_no WHERE A.bill_no=? and A.aq_month=? and A.aq_year=? and B.acct_type='TPF' and (C.ad_code='TPF' OR C.ad_code='TPFGA') and C.ad_amt > 0";
            pst = con.prepareStatement(sql);
            pst.setString(1,billNo);
            pst.setInt(2,month);
            pst.setInt(3,year);
            rs = pst.executeQuery();
            while(rs.next()){
                
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            DataBaseFunctions.closeSqlObjects(con);
        }
       return li; 
    }

    @Override
    public List getDDOList(String trCode) {
        
        Connection con = null;
        
        PreparedStatement pst = null;
        ResultSet rs = null;
        
        SelectOption so = null;
        
        List li = new ArrayList();
        try{
            con = this.dataSource.getConnection();
            
            String sql = "SELECT OFF_CODE,DDO_CODE FROM G_OFFICE WHERE IS_DDO='Y' AND TR_CODE=? ORDER BY DDO_CODE ASC";
            pst = con.prepareStatement(sql);
            pst.setString(1,trCode);
            rs = pst.executeQuery();
            while(rs.next()){
                so = new SelectOption();
                so.setValue(rs.getString("OFF_CODE"));
                so.setLabel(rs.getString("DDO_CODE"));
                li.add(so);
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            DataBaseFunctions.closeSqlObjects(con);
        }
      return li;  
    }

    @Override
    public List getBillAmt(String ddocode, int month, int year) {
        
        Connection con = null;
        
        PreparedStatement pst = null;
        ResultSet rs = null;
        
        SelectOption so = null;
        
        List li = new ArrayList();
        try{
            con = this.dataSource.getConnection();
            /*System.out.println("select bill_no, SUM(ad_amt) AS total_deduction from aq_mast AM INNER JOIN aq_dtls AD ON AM.aqsl_no = AD.aqsl_no" +
                            " where off_code='"+ddocode+"' and AM.aq_year='"+year+"' and AM.aq_month='"+month+"' and acct_type='TPF'" +
                            " group by bill_no");*/
            String sql = "select bill_no, SUM(ad_amt) AS total_deduction from aq_mast AM INNER JOIN aq_dtls AD ON AM.aqsl_no = AD.aqsl_no" +
                         " where off_code=? and AM.aq_year=? and AM.aq_month=? and acct_type=? and (AD.schedule=? OR AD.schedule=? or AD.ad_code = ? or AD.ad_code = ?)" +
                         " group by bill_no";
            pst = con.prepareStatement(sql);
            pst.setString(1,ddocode);
            pst.setInt(2,year);
            pst.setInt(3,month);
            pst.setString(4,"TPF");
			pst.setString(5,"TPF");
			pst.setString(6,"TPFGA");
			pst.setString(7,"GPDD");
			pst.setString(8,"GPIR");
            rs = pst.executeQuery();
            while(rs.next()){
                so = new SelectOption();
                so.setValue(rs.getString("bill_no"));
                so.setLabel(rs.getString("total_deduction"));
                li.add(so);
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            DataBaseFunctions.closeSqlObjects(con);
        }
      return li;  
    }

    @Override
    public List getYearList() {
        
        SelectOption so = null;
        List li = new ArrayList();
        try{
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            for(int i = 5; i > 0; i--){
                so = new SelectOption();
                so.setValue(year+"");
                so.setLabel(year+"");
                li.add(so);
                year = year - 1;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
      return li;  
    }
    
    @Override
    public List getPaymentList(String billNo) {
        
        Connection con = null;
        
        PreparedStatement pst = null;
        ResultSet rs = null;
        
        List li = new ArrayList();
        
        TPFReportList tlist = null;
        int i = 0;
        try{
            con = this.dataSource.getConnection();
            
            String sql = "select emp_name,cur_desg,mon_basic,gpf_type,gpf_acc_no,p_org_amt,ORDNO,ORDDT from (select emp_code,emp_name,cur_desg,mon_basic,gpf_type,gpf_acc_no from aq_mast where bill_no=?)aq_mast" +
                         " left outer join (select emp_id,not_id,p_org_amt from emp_loan_sanc where loan_tp=?)emp_loan_sanc on aq_mast.emp_code=emp_loan_sanc.emp_id" +
                         " inner join emp_notification on emp_loan_sanc.not_id=emp_notification.not_id order by emp_name asc";
            pst = con.prepareStatement(sql);
            pst.setInt(1,Integer.parseInt(billNo));
            pst.setString(2,"TPF");
            rs = pst.executeQuery();
            while(rs.next()){
                i = i + 1;
                tlist = new TPFReportList();
                tlist.setSlno(i);
                tlist.setEmpName(rs.getString("emp_name"));
                tlist.setEmpDesg(rs.getString("cur_desg"));
                tlist.setBasic(rs.getString("mon_basic"));
                tlist.setGpfseries(rs.getString("gpf_type"));
                tlist.setGpfNo(rs.getString("gpf_acc_no"));
                tlist.setOrdNo(rs.getString("ORDNO"));
                tlist.setOrdDt(CommonFunctions.getFormattedOutputDate1(rs.getDate("ORDDT")));
                tlist.setAmount(rs.getInt("p_org_amt"));
                li.add(tlist);
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            DataBaseFunctions.closeSqlObjects(con);
        }
      return li;  
    }

    @Override
    public TPFReportBean getBillDtls(String billNo) {
        Connection con = null;
        
        PreparedStatement pst = null;
        ResultSet rs = null;
        
        TPFReportBean tbean = new TPFReportBean();
        try{
            con = this.dataSource.getConnection();
            
            String sql = "select department_name,g_office.ddo_code,token_no,token_date,vch_no,vch_date,bill_mast.bill_date,aq_mast.off_code,bill_desc from aq_mast" +
                         " inner join bill_mast on aq_mast.bill_no=bill_mast.bill_no" +
                         " inner join g_office on aq_mast.off_code=g_office.off_code" +
                         " inner join g_department on g_office.department_code=g_department.department_code where aq_mast.bill_no=?";
            pst = con.prepareStatement(sql);
            pst.setInt(1,Integer.parseInt(billNo));
            rs = pst.executeQuery();
            if(rs.next()){
                tbean.setDeptName(rs.getString("department_name"));
                tbean.setDdoCode(rs.getString("ddo_code"));
                tbean.setTokenNo(rs.getString("token_no"));
                tbean.setTokenDate(CommonFunctions.getFormattedOutputDate1(rs.getDate("token_no")));
                tbean.setBillDesc(rs.getString("bill_desc"));
                tbean.setVoucherNo(rs.getString("vch_no"));
                tbean.setVoucherDate(CommonFunctions.getFormattedOutputDate1(rs.getDate("vch_date")));
                tbean.setBillDate(CommonFunctions.getFormattedOutputDate1(rs.getDate("bill_date")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            DataBaseFunctions.closeSqlObjects(con);
        }
      return tbean;  
    }
}
