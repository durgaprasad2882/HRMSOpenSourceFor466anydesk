package hrms.dao.deputation;

import hrms.SelectOption;
import hrms.common.CommonFunctions;
import hrms.common.DataBaseFunctions;
import hrms.common.Message;
import hrms.model.deputation.DeputationDataForm;
import hrms.model.deputation.DeputationListForm;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;

public class DeputationDAOImpl implements DeputationDAO {

    @Resource(name = "dataSource")
    protected DataSource dataSource;

    protected MaxDeputationIdDAO maxDeputationIdDAO;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setMaxDeputationIdDAO(MaxDeputationIdDAO maxDeputationIdDAO) {
        this.maxDeputationIdDAO = maxDeputationIdDAO;
    }

    @Override
    public List getCadreStatusList(String type) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        SelectOption so = null;

        List cslist = new ArrayList();
        try {
            con = this.dataSource.getConnection();

            String sql = "SELECT DISTINCT ACS,CADRE_STAT FROM G_CADRE_STAT WHERE TYPE=?";
            pst = con.prepareStatement(sql);
            pst.setString(1, type);
            rs = pst.executeQuery();
            while (rs.next()) {
                so = new SelectOption();
                so.setValue(rs.getString("ACS"));
                so.setLabel(rs.getString("CADRE_STAT"));
                cslist.add(so);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return cslist;
    }

    @Override
    public List getSubCadreStatusList(String cadrestat) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        SelectOption so = null;

        List cslist = new ArrayList();
        try {
            con = this.dataSource.getConnection();

            String sql = "SELECT DISTINCT ASCS,SUB_CADRE_STAT FROM G_CADRE_STAT WHERE ACS=?";
            pst = con.prepareStatement(sql);
            pst.setString(1, cadrestat);
            rs = pst.executeQuery();
            while (rs.next()) {
                so = new SelectOption();
                so.setValue(rs.getString("ASCS"));
                so.setLabel(rs.getString("SUB_CADRE_STAT"));
                cslist.add(so);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return cslist;
    }

    @Override
    public Message saveDeputation(DeputationDataForm deputationForm, String notid) {

        Connection con = null;

        PreparedStatement pst = null;

        Message msg = new Message();
        msg.setStatus("Success");
        try {
            con = this.dataSource.getConnection();

            String sql = "INSERT INTO EMP_DEPUTATION (DEP_ID, NOT_TYPE, NOT_ID, EMP_ID, IF_EXTENSION, WEF_DATE, WEF_TIME, TILL_DATE, TILL_TIME) VALUES(?,?,?,?,?,?,?,?,?)";
            pst = con.prepareStatement(sql);
            pst.setString(1, maxDeputationIdDAO.getMaxDeputationId());
            pst.setString(2, "DEPUTATION");
            pst.setString(3, notid);
            pst.setString(4, deputationForm.getEmpid());
            pst.setString(5, deputationForm.getChkExtnDptnPrd());
            pst.setTimestamp(6, new Timestamp(new SimpleDateFormat("dd-MMM-yyyy").parse(deputationForm.getTxtWEFrmDt()).getTime()));
            pst.setString(7, deputationForm.getSltWEFrmTime());
            pst.setTimestamp(8, new Timestamp(new SimpleDateFormat("dd-MMM-yyyy").parse(deputationForm.getTxtTillDt()).getTime()));
            pst.setString(9, deputationForm.getSltTillTime());
            pst.executeUpdate();

            //CommonFunctions.updateCadreStatus(deputationForm.getEmpid(), deputationForm.getSltCadreStatus(), deputationForm.getSltSubCadreStatus(), con);
        } catch (Exception e) {
            msg.setStatus("Error");
            msg.setMessage(e.getMessage());
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return msg;
    }

    @Override
    public Message updateDeputation(DeputationDataForm deputationForm) {
        
        Connection con = null;

        PreparedStatement pst = null;

        Message msg = new Message();
        msg.setStatus("Success");

        try {
            con = this.dataSource.getConnection();

            if (deputationForm.getDepId() != null && !deputationForm.getDepId().equals("")) {
                String sql = "UPDATE EMP_DEPUTATION SET IF_EXTENSION=?,WEF_DATE=?,WEF_TIME=?,TILL_DATE=?,TILL_TIME=? WHERE DEP_ID=? AND EMP_ID=?";
                pst = con.prepareStatement(sql);
                pst.setString(1, deputationForm.getChkExtnDptnPrd());
                pst.setTimestamp(2, new Timestamp(new SimpleDateFormat("dd-MMM-yyyy").parse(deputationForm.getTxtWEFrmDt()).getTime()));
                pst.setString(3, deputationForm.getSltWEFrmTime());
                pst.setTimestamp(4, new Timestamp(new SimpleDateFormat("dd-MMM-yyyy").parse(deputationForm.getTxtTillDt()).getTime()));
                pst.setString(5, deputationForm.getSltTillTime());
                pst.setString(6, deputationForm.getDepId());
                pst.setString(7, deputationForm.getEmpid());
                pst.executeUpdate();
                
                //CommonFunctions.updateCadreStatus(deputationForm.getEmpid(), deputationForm.getSltCadreStatus(), deputationForm.getSltSubCadreStatus(), con);
            }
        } catch (Exception e) {
            msg.setStatus("Error");
            msg.setMessage(e.getMessage());
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
      return msg;  
    }

    @Override
    public List getDeputationList(String empid, int minlimit, int maxlimit) {
        
        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        List deputationlist = new ArrayList();
        DeputationListForm dptBean = null;
        
        try{
            con = dataSource.getConnection();
            
            String sql = "select dep_id,doe,emp_notification.not_id,emp_deputation.not_type,ordno,orddt from" +
                         " (select emp_id,doe,not_id,not_type,ordno,orddt from emp_notification where emp_id=?" +
                         " and not_type=?)emp_notification" +
                         " inner join emp_deputation on emp_notification.not_id=emp_deputation.not_id";
            pst = con.prepareStatement(sql);
            pst.setString(1,empid);
            pst.setString(2,"DEPUTATION");
            rs = pst.executeQuery();
            while (rs.next()) {
                dptBean = new DeputationListForm();
                dptBean.setDeptId(rs.getString("dep_id"));
                dptBean.setNotId(rs.getString("not_id"));
                dptBean.setNotType(rs.getString("not_type"));
                dptBean.setDoe(CommonFunctions.getFormattedOutputDate1(rs.getDate("doe")));
                dptBean.setNotOrdNo(rs.getString("ordno"));
                dptBean.setNotOrdDt(CommonFunctions.getFormattedOutputDate1(rs.getDate("orddt")));
                deputationlist.add(dptBean);
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            DataBaseFunctions.closeSqlObjects(con);
        }
      return deputationlist;  
    }

    @Override
    public int getDeputationListCount(String empid) {
        
        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        int count = 0;
        try {
            con = dataSource.getConnection();

            String sql = "select count(*) cnt from (select emp_id,doe,not_id,not_type,ordno,orddt from emp_notification where emp_id=?"
                    + " and not_type=?)temp";
            pst = con.prepareStatement(sql);
            pst.setString(1,empid);
            pst.setString(2,"DEPUTATION");
            rs = pst.executeQuery();
            if (rs.next()) {
                count = rs.getInt("cnt");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return count;
    }

    @Override
    public DeputationDataForm getEmpDeputationData(String empid, String notId) {
        
        Connection con = null;
        
        PreparedStatement pst = null;
        ResultSet rs = null;
        
        DeputationDataForm deptnBean = new DeputationDataForm();
        try{
            con = this.dataSource.getConnection();
            
            String sql = "select spc1.spn notSpn,spc2.spn transSpn,ACS,ASCS,emp_notification.not_id,emp_notification.not_type,ordno,orddt,emp_notification.dept_code notDeptCode," +
                         " emp_notification.off_code notOffCode,auth,note,dep_id,if_extension,wef_date,wef_time,till_date,till_time,tr_id,emp_transfer.dept_code transDeptCode," +
                         " emp_transfer.off_code transOffCode,emp_transfer.next_spc transSPC,emp_transfer.field_off_code fieldOffCode from" +
                         " (SELECT emp_id,not_id,dep_id,if_extension,wef_date,wef_time,till_date,till_time FROM emp_deputation WHERE EMP_ID=?" +
                         " AND NOT_ID=?)emp_deputation" +
                         " inner join (select not_id,not_type,ordno,orddt,dept_code,off_code,auth,note from emp_notification where EMP_ID=?" +
                         " AND NOT_ID=?)emp_notification" +
                         " on emp_deputation.not_id=emp_notification.not_id" +
                         " inner join emp_transfer on emp_notification.not_id=emp_transfer.not_id inner join emp_mast on emp_deputation.emp_id=emp_mast.emp_id" +
                         " left outer join g_spc spc1 on emp_notification.auth=spc1.spc" +
                         " left outer join g_spc spc2 on emp_transfer.next_spc=spc2.spc" +
                         " where emp_transfer.not_type='DEPUTATION'";
            pst = con.prepareStatement(sql);
            pst.setString(1, empid);
            pst.setString(2, notId);
            pst.setString(3, empid);
            pst.setString(4, notId);
            rs = pst.executeQuery();
            if(rs.next()){
                deptnBean.setHidNotId(rs.getString("not_id"));
                deptnBean.setHidNotifyingDeptCode(rs.getString("notDeptCode"));
                deptnBean.setHidNotifyingOffCode(rs.getString("notOffCode"));
                deptnBean.setNotifyingSpc(rs.getString("auth"));
                deptnBean.setNotifyingSpn(rs.getString("notSpn"));
                deptnBean.setTxtNotOrdNo(rs.getString("ordno"));
                deptnBean.setTxtNotOrdDt(CommonFunctions.getFormattedOutputDate1(rs.getDate("orddt")));
                deptnBean.setNote(rs.getString("note"));
                
                deptnBean.setDepId(notId);
                
                deptnBean.setHidTransferId(rs.getString("tr_id"));
                deptnBean.setHidPostedDeptCode(rs.getString("transDeptCode"));
                deptnBean.setHidPostedOffCode(rs.getString("transOffCode"));
                deptnBean.setPostedSpc(rs.getString("transSPC"));
                deptnBean.setPostedSpn(rs.getString("transSpn"));
                //deptnBean.setSltFieldOffice(rs.getString("fieldOffCode"));
                deptnBean.setHidFieldOffice(rs.getString("fieldOffCode"));
                
                deptnBean.setSltCadreStatus(rs.getString("ACS"));
                //deptnBean.setSltSubCadreStatus(rs.getString("ASCS"));
                deptnBean.setHidSubCadreStatus(rs.getString("ASCS"));
                deptnBean.setChkExtnDptnPrd(rs.getString("if_extension"));
                deptnBean.setTxtWEFrmDt(CommonFunctions.getFormattedOutputDate1(rs.getDate("wef_date")));
                deptnBean.setSltWEFrmTime(rs.getString("wef_time"));
                deptnBean.setTxtTillDt(CommonFunctions.getFormattedOutputDate1(rs.getDate("till_date")));
                deptnBean.setSltTillTime(rs.getString("till_time"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            DataBaseFunctions.closeSqlObjects(con);
        }
      return deptnBean;
    }
}
