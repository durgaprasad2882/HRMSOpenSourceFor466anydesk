package hrms.dao.transfer;

import hrms.SelectOption;
import hrms.common.CommonFunctions;
import hrms.common.DataBaseFunctions;
import hrms.common.Message;
import hrms.dao.emppayrecord.EmpPayRecordDAO;
import hrms.dao.emppayrecord.EmpPayRecordDAOImpl;
import hrms.model.emppayrecord.EmpPayRecordForm;
import hrms.model.login.LoginUserBean;
import hrms.model.notification.OtherSPCBean;
import hrms.model.transfer.TransferBean;
import hrms.model.transfer.TransferForm;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;

public class TransferDAOImpl implements TransferDAO {

    @Resource(name = "dataSource")
    protected DataSource dataSource;

    protected MaxTransferIdDAOImpl maxTransferIdDao;

    protected EmpPayRecordDAO emppayrecordDAO;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setMaxTransferIdDao(MaxTransferIdDAOImpl maxTransferIdDao) {
        this.maxTransferIdDao = maxTransferIdDao;
    }

    public void setEmppayrecordDAO(EmpPayRecordDAOImpl emppayrecordDAO) {
        this.emppayrecordDAO = emppayrecordDAO;
    }

    @Override
    public void saveTransfer(TransferForm transferForm, String notid) {

        Connection con = null;

        PreparedStatement pst = null;

        EmpPayRecordForm epayrecordform = new EmpPayRecordForm();

        try {
            con = dataSource.getConnection();

            String sql = "INSERT INTO EMP_TRANSFER (TR_ID, NOT_ID, NOT_TYPE, EMP_ID,DEPT_CODE,OFF_CODE, NEXT_SPC,IF_DEPLOYED,FIELD_OFF_CODE) VALUES(?,?,?,?,?,?,?,?,?)";
            pst = con.prepareStatement(sql);
            pst.setString(1, maxTransferIdDao.getMaxTransferId()); //CommonFunctions.getMaxCode("EMP_TRANSFER", "TR_ID", con));
            pst.setString(2, notid);
            pst.setString(3, "TRANSFER");
            pst.setString(4, transferForm.getEmpid());
            pst.setString(5, transferForm.getHidPostedDeptCode());
            pst.setString(6, transferForm.getHidPostedOffCode());
            System.out.println("Posted SPC is: " + transferForm.getPostedspc());
            pst.setString(7, transferForm.getPostedspc());
            pst.setString(8, "");
            pst.setString(9, transferForm.getSltPostedFieldOff());
            pst.executeUpdate();

            sql = "UPDATE EMP_MAST SET NEXT_OFFICE_CODE=? WHERE EMP_ID=?";
            pst = con.prepareStatement(sql);
            pst.setString(1, transferForm.getHidPostedOffCode());
            pst.setString(2, transferForm.getEmpid());
            pst.executeUpdate();

            epayrecordform.setEmpid(transferForm.getEmpid());
            epayrecordform.setNot_id(notid);
            epayrecordform.setNot_type("TRANSFER");
            epayrecordform.setPayscale(transferForm.getSltPayScale());
            epayrecordform.setBasic(transferForm.getTxtBasic());
            epayrecordform.setGp(transferForm.getTxtGP());
            epayrecordform.setS_pay(transferForm.getTxtSP());
            epayrecordform.setP_pay(transferForm.getTxtPP());
            epayrecordform.setOth_pay(transferForm.getTxtOP());
            epayrecordform.setOth_desc(transferForm.getTxtDescOP());
            epayrecordform.setWefDt(transferForm.getTxtWEFDt());
            epayrecordform.setWefTime(transferForm.getSltWEFTime());
            emppayrecordDAO.saveEmpPayRecordData(epayrecordform);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    @Override
    public void updateTransfer(TransferForm transferForm) {

        Connection con = null;

        PreparedStatement pst = null;

        EmpPayRecordForm epayrecordform = new EmpPayRecordForm();
        try {
            con = dataSource.getConnection();

            String sql = "UPDATE EMP_TRANSFER SET DEPT_CODE=?,OFF_CODE=?, NEXT_SPC=?,IF_DEPLOYED=?,FIELD_OFF_CODE=? WHERE EMP_ID=? AND TR_ID=?";
            pst = con.prepareStatement(sql);
            pst.setString(1, transferForm.getHidPostedDeptCode());
            pst.setString(2, transferForm.getHidPostedOffCode());
            pst.setString(3, transferForm.getPostedspc());
            pst.setString(4, "");
            pst.setString(5, transferForm.getSltPostedFieldOff());
            pst.setString(6, transferForm.getEmpid());
            pst.setString(7, transferForm.getTransferId());
            pst.executeUpdate();

            sql = "UPDATE EMP_MAST SET NEXT_OFFICE_CODE=? WHERE EMP_ID=?";
            pst = con.prepareStatement(sql);
            pst.setString(1, transferForm.getHidPostedOffCode());
            pst.setString(2, transferForm.getEmpid());
            pst.executeUpdate();

            epayrecordform.setPayid(transferForm.getHpayid());
            epayrecordform.setEmpid(transferForm.getEmpid());
            epayrecordform.setPayscale(transferForm.getSltPayScale());
            epayrecordform.setBasic(transferForm.getTxtBasic());
            epayrecordform.setGp(transferForm.getTxtGP());
            epayrecordform.setS_pay(transferForm.getTxtSP());
            epayrecordform.setP_pay(transferForm.getTxtPP());
            epayrecordform.setOth_pay(transferForm.getTxtOP());
            epayrecordform.setOth_desc(transferForm.getTxtDescOP());
            epayrecordform.setWefDt(transferForm.getTxtWEFDt());
            epayrecordform.setWefTime(transferForm.getSltWEFTime());
            emppayrecordDAO.updateEmpPayRecordData(epayrecordform);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    @Override
    public void deleteTransfer(TransferForm transferForm) {

        Connection con = null;

        PreparedStatement pst = null;

        EmpPayRecordForm epayrecordform = new EmpPayRecordForm();
        
        try {
            con = dataSource.getConnection();
            System.out.println("Delete Emp id is: " + transferForm.getEmpid());
            System.out.println("Delete TR_ID id is: " + transferForm.getTransferId());
            System.out.println("Delete PAY Id is: " + transferForm.getHpayid());
            String sql = "DELETE FROM EMP_TRANSFER WHERE EMP_ID=? AND TR_ID=?";
            pst = con.prepareStatement(sql);
            pst.setString(1, transferForm.getEmpid());
            pst.setString(2, transferForm.getTransferId());
            pst.executeUpdate();

            epayrecordform.setEmpid(transferForm.getEmpid());
            epayrecordform.setPayid(transferForm.getHpayid());
            emppayrecordDAO.deleteEmpPayRecordData(epayrecordform);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    @Override
    public int getTransferListCount(String empid) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        int count = 0;
        try {
            con = dataSource.getConnection();

            String sql = "select count(*) cnt from (select emp_id,doe,not_id,not_type,ordno,orddt from emp_notification where emp_id=?"
                    + " and not_type='TRANSFER')temp";
            pst = con.prepareStatement(sql);
            pst.setString(1, empid);
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
    public List getTransferList(String empid) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        List transferlist = new ArrayList();
        TransferBean tbean = null;
        try {
            con = dataSource.getConnection();

            String sql = "select tr_id,emp_notification.doe,emp_notification.not_id,emp_transfer.not_type,ordno,orddt,off_en,relieve_id from"
                    + " (select emp_id,doe,not_id,not_type,ordno,orddt from emp_notification where emp_id=? and not_type='TRANSFER')emp_notification"
                    + " left outer join emp_transfer on emp_notification.not_id=emp_transfer.not_id"
                    + " left outer join emp_relieve on emp_notification.not_id=emp_relieve.not_id"
                    + " left outer join g_office on emp_transfer.off_code=g_office.off_code";
            System.out.println("SQL for Transfer List is: " + sql);
            pst = con.prepareStatement(sql);
            pst.setString(1, empid);
            rs = pst.executeQuery();
            while (rs.next()) {
                tbean = new TransferBean();
                tbean.setTransferId(rs.getString("tr_id"));
                tbean.setHnotid(rs.getString("not_id"));
                tbean.setNotType(rs.getString("not_type"));
                tbean.setDoe(CommonFunctions.getFormattedOutputDate1(rs.getDate("DOE")));
                tbean.setOrdno(rs.getString("ordno"));
                tbean.setOrdt(CommonFunctions.getFormattedOutputDate1(rs.getDate("orddt")));
                tbean.setTransferToOffice(rs.getString("off_en"));
                tbean.setHrlvid(rs.getString("relieve_id"));
                transferlist.add(tbean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return transferlist;
    }

    @Override
    public TransferForm getEmpTransferData(TransferForm trform, String notificationId) throws SQLException {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            con = dataSource.getConnection();

            String sql = "select"
                    + " emp_notification.not_id,ordno,orddt,emp_notification.dept_code notDeptCode,emp_notification.off_code notOffCode,"
                    + " auth,note,tr_id,emp_transfer.dept_code transDeptCode,emp_transfer.off_code transOffCode,next_spc,field_off_code,pay_id,wef,weft,emp_pay_record.pay_scale,"
                    + " pay,s_pay,p_pay,oth_pay,emp_pay_record.gp,oth_desc,spn from"
                    + " (SELECT not_id,tr_id,dept_code,off_code,next_spc,field_off_code FROM EMP_TRANSFER WHERE EMP_ID=? AND NOT_ID=?)emp_transfer"
                    + " inner join (select not_id,ordno,orddt,dept_code,off_code,auth,note from emp_notification where EMP_ID=? AND NOT_ID=?)emp_notification"
                    + " on emp_transfer.not_id=emp_notification.not_id"
                    + " left outer join (select pay_id,not_id,wef,weft,pay_scale,pay,s_pay,p_pay,oth_pay,gp,oth_desc from emp_pay_record where EMP_ID=? AND NOT_ID=?)emp_pay_record"
                    + " on emp_notification.not_id=emp_pay_record.not_id"
                    + " left outer join g_spc on emp_transfer.next_spc=g_spc.spc";
            System.out.println("SQL for Edit Transfer is: " + sql);
            pst = con.prepareStatement(sql);
            pst.setString(1, trform.getEmpid());
            pst.setString(2, notificationId);
            pst.setString(3, trform.getEmpid());
            pst.setString(4, notificationId);
            pst.setString(5, trform.getEmpid());
            pst.setString(6, notificationId);
            rs = pst.executeQuery();
            if (rs.next()) {
                trform.setTransferId(rs.getString("tr_id"));
                trform.setTxtNotOrdNo(rs.getString("ordno"));
                trform.setTxtNotOrdDt(CommonFunctions.getFormattedOutputDate1(rs.getDate("orddt")));
                trform.setHidAuthDeptCode(rs.getString("notDeptCode"));
                trform.setHidAuthOffCode(rs.getString("notOffCode"));
                trform.setHidTempAuthOffCode(rs.getString("notOffCode"));
                trform.setAuthSpc(rs.getString("auth"));
                trform.setHidTempAuthPost(rs.getString("auth"));
                trform.setAuthPostName(CommonFunctions.getSPN(con, rs.getString("auth")));

                trform.setRdAuthType("GOO");

                trform.setHidPostedDeptCode(rs.getString("transDeptCode"));
                trform.setHidPostedOffCode(rs.getString("transOffCode"));
                trform.setHidTempPostedOffCode(rs.getString("transOffCode"));
                //System.out.println("HidOffCode: "+trform.getHidPostedOffCode());
                trform.setPostedspc(rs.getString("next_spc"));
                trform.setHidTempPostedPost(rs.getString("next_spc"));
                trform.setPostedPostName(rs.getString("SPN"));
                trform.setSltPostedFieldOff(rs.getString("field_off_code"));
                trform.setHidTempPostedFieldOffCode(rs.getString("field_off_code"));
                
                trform.setRdPostedAuthType("GOO");

                trform.setHpayid(rs.getString("pay_id"));
                trform.setSltPayScale(rs.getString("pay_scale"));
                trform.setTxtGP(rs.getString("gp"));
                trform.setTxtBasic(rs.getString("pay"));
                trform.setTxtSP(rs.getString("s_pay"));
                trform.setTxtPP(rs.getString("p_pay"));
                trform.setTxtOP(rs.getString("oth_pay"));
                trform.setTxtDescOP(rs.getString("oth_desc"));
                trform.setTxtWEFDt(CommonFunctions.getFormattedOutputDate1(rs.getDate("wef")));
                trform.setSltWEFTime(rs.getString("weft"));
                trform.setNote(rs.getString("note"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return trform;
    }

    public OtherSPCBean getOthSPCData(String spc) throws SQLException {

        Connection con = null;

        Statement stmt = null;
        ResultSet rs = null;

        OtherSPCBean othbean = new OtherSPCBean();
        try {
            con = dataSource.getConnection();

            if (spc != null && !spc.equals("") && !(spc.length() < 3)) {
                if (spc.substring(0, 3).equalsIgnoreCase("GOI") || spc.substring(0, 3).equalsIgnoreCase("OSG") || spc.substring(0, 3).equalsIgnoreCase("FRB") || spc.substring(0, 3).equalsIgnoreCase("ORG")) {
                    /*check if exist in G_SPC*/
                    boolean bln = false;
                    rs = stmt.executeQuery("Select * from G_SPC where SPC='" + spc + "' and (ifuclean='N' or ifuclean is null) order by SPN ");
                    if (rs.next()) {
                        bln = true;
                        othbean.setOthDeptCode(rs.getString("DEPT_CODE"));
                        othbean.setOthOffCode(rs.getString("OFF_CODE"));
                        othbean.setOthPostCode(rs.getString("SPC"));
                    }
                    /*check if exist in G_SPC*/

                    if (bln == false) {
                        rs = null;
                        rs = stmt.executeQuery("SELECT * FROM G_OTH_SPC WHERE OTH_SPC='" + spc + "' order by AUTH_NAME");
                        if (rs.next()) {
                            if (rs.getString("DEPT_NAME") != null && !rs.getString("DEPT_NAME").equalsIgnoreCase("")) {
                                othbean.setOthDeptCode(rs.getString("DEPT_NAME"));
                                othbean.setOthDeptName(rs.getString("DEPT_NAME"));
                            }
                            if (rs.getString("OFF_EN") != null && !rs.getString("OFF_EN").equalsIgnoreCase("")) {
                                othbean.setOthOffCode(rs.getString("OFF_EN"));
                                othbean.setOthOffName(rs.getString("OFF_EN"));
                            }
                            if (rs.getString("AUTH_NAME") != null && !rs.getString("AUTH_NAME").equalsIgnoreCase("")) {
                                othbean.setOthPostName(rs.getString("AUTH_NAME"));
                            }
                            String othspn = null;
                            if ((rs.getString("AUTH_NAME") == null || rs.getString("AUTH_NAME").equalsIgnoreCase(""))
                                    && (rs.getString("OFF_EN") != null && !rs.getString("OFF_EN").equalsIgnoreCase(""))
                                    && (rs.getString("DEPT_NAME") != null && !rs.getString("DEPT_NAME").equalsIgnoreCase(""))) {
                                othspn = rs.getString("OFF_EN") + ", " + rs.getString("DEPT_NAME");
                            }
                            if ((rs.getString("AUTH_NAME") == null || rs.getString("AUTH_NAME").equalsIgnoreCase(""))
                                    && (rs.getString("OFF_EN") != null && !rs.getString("OFF_EN").equalsIgnoreCase(""))
                                    && (rs.getString("DEPT_NAME") == null || rs.getString("DEPT_NAME").equalsIgnoreCase(""))) {
                                othspn = rs.getString("OFF_EN");
                            }
                            if ((rs.getString("AUTH_NAME") == null || rs.getString("AUTH_NAME").equalsIgnoreCase(""))
                                    && (rs.getString("OFF_EN") == null || rs.getString("OFF_EN").equalsIgnoreCase(""))
                                    && (rs.getString("DEPT_NAME") != null && !rs.getString("DEPT_NAME").equalsIgnoreCase(""))) {
                                othspn = rs.getString("DEPT_NAME");
                            }
                            if ((rs.getString("AUTH_NAME") != null && !rs.getString("AUTH_NAME").equalsIgnoreCase(""))
                                    && (rs.getString("OFF_EN") == null || rs.getString("OFF_EN").equalsIgnoreCase(""))
                                    && (rs.getString("DEPT_NAME") == null || rs.getString("DEPT_NAME").equalsIgnoreCase(""))) {
                                othspn = rs.getString("AUTH_NAME");
                            } else if ((rs.getString("AUTH_NAME") != null && !rs.getString("AUTH_NAME").equalsIgnoreCase(""))
                                    && (rs.getString("OFF_EN") == null || rs.getString("OFF_EN").equalsIgnoreCase(""))
                                    && (rs.getString("DEPT_NAME") != null && !rs.getString("DEPT_NAME").equalsIgnoreCase(""))) {
                                othspn = rs.getString("AUTH_NAME") + ", " + rs.getString("DEPT_NAME");
                            } else if ((rs.getString("AUTH_NAME") != null && !rs.getString("AUTH_NAME").equalsIgnoreCase(""))
                                    && (rs.getString("OFF_EN") != null && !rs.getString("OFF_EN").equalsIgnoreCase(""))
                                    && (rs.getString("DEPT_NAME") == null || rs.getString("DEPT_NAME").equalsIgnoreCase(""))) {
                                othspn = rs.getString("AUTH_NAME") + ", " + rs.getString("OFF_EN");
                            } else if ((rs.getString("AUTH_NAME") != null && !rs.getString("AUTH_NAME").equalsIgnoreCase(""))
                                    && (rs.getString("OFF_EN") != null && !rs.getString("OFF_EN").equalsIgnoreCase(""))
                                    && (rs.getString("DEPT_NAME") != null && !rs.getString("DEPT_NAME").equalsIgnoreCase(""))) {
                                othspn = rs.getString("AUTH_NAME") + ", " + rs.getString("OFF_EN") + ", " + rs.getString("DEPT_NAME");
                            }
                        }
                    }
                } else {
                    rs = stmt.executeQuery("Select * from G_SPC where SPC='" + spc + "' and (ifuclean='N' or ifuclean is null) order by SPN ");
                    while (rs.next()) {
                        othbean.setOthDeptCode(rs.getString("DEPT_CODE"));
                        othbean.setOthOffCode(rs.getString("OFF_CODE"));
                        othbean.setOthPostCode(rs.getString("SPC"));
                        othbean.setOthPostName(rs.getString("SPN"));
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return othbean;
    }

    @Override
    public List getPostList(String deptCode, String offCode) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        SelectOption so = null;
        List plist = new ArrayList();
        try {
            con = this.dataSource.getConnection();

            String sql = "SELECT SPC,SPN FROM G_SPC WHERE DEPT_CODE=? AND OFF_CODE=? ORDER BY SPN ASC";
            pst = con.prepareStatement(sql);
            pst.setString(1,deptCode);
            pst.setString(2,offCode);
            rs = pst.executeQuery();
            while (rs.next()) {
                so = new SelectOption();
                so.setLabel(rs.getString("SPN"));
                so.setValue(rs.getString("SPC"));
                plist.add(so);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return plist;
    }

    @Override
    public int getPostListCount(String deptCode, String offCode) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        int count = 0;
        try {
            con = this.dataSource.getConnection();

            String sql = "SELECT count(SPC) cnt FROM G_SPC WHERE DEPT_CODE=? AND OFF_CODE=?";
            pst = con.prepareStatement(sql);
            pst.setString(1,deptCode);
            pst.setString(2,offCode);
            rs = pst.executeQuery();
            if (rs.next()) {
                count = rs.getInt("cnt");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs,pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return count;
    }

    @Override
    public String getCadreCode(String empid) {
        
        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;
        
        String cadreCode = "";
        try{
            con = this.dataSource.getConnection();
            
            String sql = "SELECT CUR_CADRE_CODE FROM EMP_MAST WHERE EMP_ID=?";
            pst = con.prepareStatement(sql);
            pst.setString(1,empid);
            rs = pst.executeQuery();
            if(rs.next()){
                cadreCode = rs.getString("CUR_CADRE_CODE");
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            DataBaseFunctions.closeSqlObjects(rs,pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
      return cadreCode;  
    }
    
    @Override
    public LoginUserBean[] getTransferListOfficeWise(String offcode,int year,int month){
        Connection con=null;
        PreparedStatement ps=null;
        List<LoginUserBean> li=new ArrayList<>();
        try{
            String sql="select EMP.EMP_ID,CUR_OFF_CODE from emp_relieve RELV " +
            "INNER JOIN EMP_JOIN EMPJOIN ON RELV.JOIN_ID=EMPJOIN.JOIN_ID " +
            "inner join EMP_MAST EMP ON RELV.EMP_ID=EMP.EMP_ID " +
            "where RELV.spc like '"+offcode+"%' AND EXTRACT(YEAR FROM RLV_DATE)=? AND EXTRACT(MONTH FROM RLV_DATE)=? AND EMP.CUR_OFF_CODE<>'"+offcode+"'";
            con=dataSource.getConnection();
            ps=con.prepareStatement(sql);
            ps.setInt(1, year);
            ps.setInt(2, month);
            ResultSet rs=ps.executeQuery();
            LoginUserBean us=null;
            while(rs.next()){
                us=new LoginUserBean();
                us.setEmpid(rs.getString("emp_id"));
                us.setOffcode(rs.getString("CUR_OFF_CODE"));
                li.add(us);
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            DataBaseFunctions.closeSqlObjects(con);
        }
        LoginUserBean frarray[] = li.toArray(new LoginUserBean[li.size()]);
        return frarray;
    }
}
