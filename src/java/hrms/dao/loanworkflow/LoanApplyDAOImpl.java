/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.loanworkflow;

//import com.sun.xml.ws.mex.MetadataConstants;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import hrms.SelectOption;
import hrms.common.CommonFunctions;
import hrms.common.DataBaseFunctions;
import hrms.model.loanworkflow.LoanForm;
import hrms.model.loanworkflow.LoanGPFForm;
import hrms.model.loanworkflow.LoanHBAForm;
import hrms.model.loanworkflow.LoanList;
import hrms.model.loanworkflow.LoanTempGPFForm;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author lenovo
 */
public class LoanApplyDAOImpl implements LoanApplyDAO {

    @Resource(name = "dataSource")
    protected DataSource dataSource;
    private Object DateTimeFormatter;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public LoanForm displayEmpDetails(String hrmsid) {
        LoanForm loanvalue = new LoanForm();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Calendar now = Calendar.getInstance();
        int cmonth = now.get(Calendar.MONTH);
        int cyear = now.get(Calendar.YEAR);
        try {
            con = this.dataSource.getConnection();
            ps = con.prepareStatement("SELECT a.is_regular,a.emp_id,ARRAY_TO_STRING(ARRAY[INITIALS, a.F_NAME, a.M_NAME,a.L_NAME], ' ') fullname,a.cur_basic_salary,to_char(a.dob, 'DD-Mon-YYYY') as dob,to_char(a.dos, 'DD-Mon-YYYY') as dos,b.spn,b.spc,c.off_name  FROM emp_mast a,g_spc b,g_office c WHERE a.cur_spc=b.spc AND a.cur_off_code=c.off_code AND   emp_id=?");
            ps.setString(1, hrmsid);
            rs = ps.executeQuery();
            if (rs.next()) {
                loanvalue.setEmpName(rs.getString("fullname"));
                loanvalue.setBasicsalary(rs.getString("cur_basic_salary"));
                loanvalue.setDesignation(rs.getString("spn"));
                loanvalue.setEmpSPC(rs.getString("spc"));
                // des = StringUtils.strip(rs.getString("spn"), ",");
                //  loanvalue.setDesignation(rs.getString("des"));
                // System.out.println(des);
                loanvalue.setOffaddress(rs.getString("off_name"));
                loanvalue.setNetsalary(getNetPay(hrmsid, cmonth, cyear) + "");
                if (rs.getString("is_regular").equals("Y")) {
                    loanvalue.setJobType("Permanent");
                } else {
                    loanvalue.setJobType("Temporary");
                }
                loanvalue.setEmpdob(rs.getString("dob"));
                loanvalue.setSuperannuation(rs.getString("dos"));

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return loanvalue;
    }

    public double getNetPay(String hrmsid, int monthValue, int yearValue) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        int basic = 0;
        int allowance = 0;
        int deduction = 0;
        int gross = 0;
        int net = 0;
        try {
            con = this.dataSource.getConnection();
            //System.out.println("EMP_CODE is: " + hrmsid + " Year is: " + yearValue + " and month is: " + monthValue);
            String sql = "SELECT CUR_BASIC FROM AQ_MAST WHERE EMP_CODE=? AND AQ_YEAR=? AND AQ_MONTH=?";
            pst = con.prepareStatement(sql);
            pst.setString(1, hrmsid);
            pst.setInt(2, yearValue);
            pst.setInt(3, monthValue - 1);
            rs = pst.executeQuery();
            if (rs.next()) {
                basic = rs.getInt("CUR_BASIC");
            }

            sql = "SELECT AD_AMT FROM AQ_DTLS WHERE EMP_CODE=? AND AQ_YEAR=? AND AQ_MONTH=? AND AD_TYPE='A' AND AD_AMT > 0";
            pst = con.prepareStatement(sql);
            pst.setString(1, hrmsid);
            pst.setInt(2, yearValue);
            pst.setInt(3, monthValue - 1);
            rs = pst.executeQuery();
            while (rs.next()) {
                allowance = allowance + rs.getInt("AD_AMT");
            }
            gross = basic + allowance;

            sql = "SELECT AD_AMT FROM AQ_DTLS WHERE EMP_CODE=? AND AQ_YEAR=? AND AQ_MONTH=? AND AD_TYPE='D' AND AD_AMT > 0";
            pst = con.prepareStatement(sql);
            pst.setString(1, hrmsid);
            pst.setInt(2, yearValue);
            pst.setInt(3, monthValue - 1);
            rs = pst.executeQuery();
            while (rs.next()) {
                deduction = deduction + rs.getInt("AD_AMT");
            }
            net = gross - deduction;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return net;
    }

    @Override
    public void saveLoanData(LoanForm lform, String empid, String filepath) {
        Connection con = null;
        Calendar now = Calendar.getInstance();
        Date loanapplyDate = now.getTime();

        PreparedStatement ps = null;
        ResultSet rs = null;
        Date dt1 = null;
        Date dt2 = null;
        Date dt3 = null;

        DateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");

        MultipartFile loanAttch = lform.getFile_att();
        try {
            con = this.dataSource.getConnection();
            String forwardId = lform.getForwardtoHrmsid();
            String hidSpc = lform.getHidSPC();
            //System.out.println(forwardId + "-- " + hidSpc );
            double antprice = Double.parseDouble(lform.getAntprice());
            String session_empId = empid;
            String designation = lform.getDesignation();
            double basicsalary = Double.parseDouble(lform.getBasicsalary());
            double netsalary = Double.parseDouble(lform.getNetsalary());
            String hidOffCode = lform.getHidOffCode();
            String hidOffName = lform.getHidOffName();

            String purtype = lform.getPurtype();
            String amountadv = lform.getAmountadv();

            int instalments = lform.getInstalments();
            String previousAvail = lform.getPreviousAvail();
            String PreAdvPur = lform.getPreAdvPur();
            String amounpretadv = lform.getAmounpretadv();

            String dateofdrawal = lform.getDateofdrawal();
            String intpaidfull = lform.getIntpaidfull();
            String amountstanding = lform.getAmountstanding();

            String officerleave = lform.getOfficerleave();
            String datecommleave = lform.getDatecommleave();
            String dateexpireleave = lform.getDateexpireleave();
            String forwardto = lform.getForwardto();
            int loanvalue = 25;
            String initiatedSpc = lform.getEmpSPC();
            String loanapplyfor = lform.getLoanapplyfor();

            /**
             * **********************************************************************************
             */
            int mcode = CommonFunctions.getMaxCodeInteger("TASK_MASTER", "TASK_ID", con);
            String strTaskmaster = "INSERT INTO task_master (task_id "
                    + ",process_id "
                    + ",initiated_by "
                    + ",status_id "
                    + ",pending_at "
                    + ",apply_to "
                    + ",initiated_spc "
                    + ",pending_spc "
                    + ",apply_to_spc "
                    + ",initiated_on "
                    + ") values(?,?,?,?,?,?,?,?,?,?)";
            ps = con.prepareStatement(strTaskmaster);
            ps.setInt(1, mcode);
            ps.setInt(2, 6);
            ps.setString(3, session_empId);
            ps.setInt(4, loanvalue);
            ps.setString(5, forwardId);
            ps.setString(6, forwardId);
            ps.setString(7, initiatedSpc);
            ps.setString(8, hidSpc);
            ps.setString(9, hidSpc);
            ps.setTimestamp(10, new Timestamp(loanapplyDate.getTime()));

            int stsTask = ps.executeUpdate();

            /**
             * *************************************************************************************
             */
            //  dt2 = new Date(datecommleave);
            // dt3 = new Date(dateexpireleave);
            // System.out.println(designation + "-- " + basicsalary + "--" + netsalary + " " + hidOffCode + "--" + hidOffName + "--" + hidSpc);
            if (stsTask == 1) {

                String str = "INSERT INTO hw_emp_loan(emp_id "
                        + ",basic_salary "
                        + ",net_salary"
                        + ",ant_price"
                        + ",purchase_type"
                        + ",amount_adv"
                        + ",no_installment"
                        + ",loan_availed"
                        + ",date_drawal_adv"
                        + ",availed_amt_adv"
                        + ",principal_paid"
                        + ",loan_type"
                        + ",motorcar_cycle_moped"
                        + ",amount_standing"
                        + ",officer_leave"
                        + ",date_commencement"
                        + ",date_expire"
                        + ",forward_to"
                        + ",loan_status,loan_apply_date,taskId) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

                ps = con.prepareStatement(str);
                ps.setString(1, session_empId);
                ps.setDouble(2, basicsalary);
                ps.setDouble(3, netsalary);
                ps.setDouble(4, antprice);
                ps.setString(5, purtype);

                if (amountadv != null && !amountadv.equals("")) {
                    ps.setDouble(6, Double.parseDouble(amountadv));
                } else {
                    ps.setDouble(6, 0);
                }
                ps.setInt(7, instalments);
                ps.setString(8, previousAvail);
                if (lform.getDateofdrawal() != null && !lform.getDateofdrawal().equals("")) {
                    ps.setTimestamp(9, new Timestamp(sdf.parse(lform.getDateofdrawal()).getTime()));
                } else {
                    ps.setTimestamp(9, null);
                }
                if (amounpretadv != null && !amounpretadv.equals("")) {
                    ps.setDouble(10, Double.parseDouble(amounpretadv));
                } else {
                    ps.setDouble(10, 0);
                }
                ps.setString(11, intpaidfull);
                ps.setString(12, loanapplyfor);
                ps.setString(13, PreAdvPur);

                if (amountstanding != null && !amountstanding.equals("")) {
                    ps.setDouble(14, Double.parseDouble(amountstanding));
                } else {
                    ps.setDouble(14, 0);
                }
                ps.setString(15, officerleave);
                if (lform.getDatecommleave() != null && !lform.getDatecommleave().equals("")) {
                    ps.setTimestamp(16, new Timestamp(sdf.parse(lform.getDatecommleave()).getTime()));
                } else {
                    ps.setTimestamp(16, null);
                }
                if (lform.getDateexpireleave() != null && !lform.getDateexpireleave().equals("")) {
                    ps.setTimestamp(17, new Timestamp(sdf.parse(lform.getDateexpireleave()).getTime()));
                } else {
                    ps.setTimestamp(17, null);
                }
                ps.setString(18, hidSpc);
                ps.setInt(19, loanvalue);
                ps.setTimestamp(20, new Timestamp(loanapplyDate.getTime()));
                ps.setInt(21, mcode);
                int sts = ps.executeUpdate();

                if (sts > 0) {
                    InputStream inputStream = null;
                    OutputStream outputStream = null;
                    String filename = "";
                    if (loanAttch != null && !loanAttch.isEmpty()) {
                        long time = System.currentTimeMillis();
                        filename = session_empId + "_" + mcode + "_" + time;

                        ps = con.prepareStatement("UPDATE hw_emp_loan SET original_filename=?,disk_file_name=?,file_type=? WHERE TASKID=?");
                        ps.setString(1, loanAttch.getOriginalFilename());
                        ps.setString(2, filename);
                        ps.setString(3, loanAttch.getContentType());
                        ps.setInt(4, mcode);
                        ps.executeUpdate();

                        String dirpath = filepath + "/";
                        File newfile = new File(dirpath);
                        if (!newfile.exists()) {
                            newfile.mkdirs();
                        }

                        outputStream = new FileOutputStream(dirpath + filename);
                        int read = 0;
                        byte[] bytes = new byte[1024];
                        inputStream = loanAttch.getInputStream();
                        while ((read = inputStream.read(bytes)) != -1) {
                            outputStream.write(bytes, 0, read);
                        }
                    }
                }
            }

            /* ps = con.prepareStatement("SELECT a.emp_id,a.f_name  FROM emp_mast a,g_spc b WHERE a.cur_spc=b.spc AND b.spc=?");
             ps.setString(1, hidSpc);
             rs = ps.executeQuery();
             String applyToHrmsid=rs.getString("emp_id");*/
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    @Override
    public List getLoanList(String empid) {

        Connection con = null;

        Statement stmt = null;
        ResultSet rs = null;

        LoanList loan = null;
        ArrayList alist = new ArrayList();
        try {
            con = this.dataSource.getConnection();

            String sql = "SELECT a.*,to_char(a.loan_apply_date, 'DD-Mon-YYYY') as loan_apply_date_format FROM hw_emp_loan a WHERE emp_id='" + empid + "'";
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                loan = new LoanList();
                loan.setLoanid(rs.getString("loan_code"));
                loan.setLoanType(rs.getString("loan_type"));
                loan.setLoanDate(rs.getString("loan_apply_date_format"));
                loan.setPurType(rs.getString("purchase_type"));
                String loanStatus = loanStatus(rs.getInt("loan_status"), 6);
                loan.setLoanStatus(loanStatus);
                loan.setLoanstatusid(rs.getInt("loan_status"));
                loan.setTaskid(rs.getInt("taskid"));

                alist.add(loan);
            }

            String sqlgpf = "SELECT a.taskid,a.loan_status,a.loan_gpf_id as loan_code,a.gpftype as loan_type ,to_char(a.loan_apply_date, 'DD-Mon-YYYY') as loan_apply_date_format FROM hw_emp_gpf_loan a WHERE emp_id='" + empid + "'";
            stmt = con.createStatement();
            rs = stmt.executeQuery(sqlgpf);
            while (rs.next()) {
                loan = new LoanList();
                loan.setLoanid(rs.getString("loan_code"));
                String loanType = "GPF " + rs.getString("loan_type");
                loan.setLoanType(loanType);
                loan.setLoanDate(rs.getString("loan_apply_date_format"));

                String loanStatus = loanStatus(rs.getInt("loan_status"), 8);
                loan.setLoanStatus(loanStatus);
                loan.setLoanstatusid(rs.getInt("loan_status"));
                loan.setTaskid(rs.getInt("taskid"));

                alist.add(loan);
            }

            String sqltempgpf = "SELECT a.taskid,a.loan_status,a.loan_temp_gpf_id as loan_code,a.gpftype as loan_type ,to_char(a.loan_apply_date, 'DD-Mon-YYYY') as loan_apply_date_format FROM hw_emp_temp_gpf_loan a WHERE emp_id='" + empid + "'";
            stmt = con.createStatement();
            rs = stmt.executeQuery(sqltempgpf);
            while (rs.next()) {
                loan = new LoanList();
                loan.setLoanid(rs.getString("loan_code"));
                String loanType = rs.getString("loan_type");
                loan.setLoanType(loanType);
                loan.setLoanDate(rs.getString("loan_apply_date_format"));

                String loanStatus = loanStatus(rs.getInt("loan_status"), 10);
                loan.setLoanStatus(loanStatus);
                loan.setLoanstatusid(rs.getInt("loan_status"));
                loan.setTaskid(rs.getInt("taskid"));

                alist.add(loan);
            }

            String sqlhba = "SELECT a.taskid,a.loan_status,a.loan_hba_id as loan_code ,to_char(a.loan_apply_date, 'DD-Mon-YYYY') as loan_apply_date_format FROM hw_emp_hba_loan a WHERE emp_id='" + empid + "'";
            stmt = con.createStatement();
            rs = stmt.executeQuery(sqlhba);
            while (rs.next()) {
                loan = new LoanList();
                loan.setLoanid(rs.getString("loan_code"));
                String loanType = "Housing Building Advance ";
                loan.setLoanType(loanType);
                loan.setLoanDate(rs.getString("loan_apply_date_format"));

                String loanStatus = loanStatus(rs.getInt("loan_status"), 9);
                loan.setLoanStatus(loanStatus);
                loan.setLoanstatusid(rs.getInt("loan_status"));
                loan.setTaskid(rs.getInt("taskid"));

                alist.add(loan);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return alist;
    }

    @Override
    public String loanStatus(int statusid, int processid) {

        Connection con = null;

        Statement stmt = null;
        ResultSet rs = null;
        String loanstatus = null;
        try {
            con = this.dataSource.getConnection();

            String sql = "SELECT status_name FROM g_process_status  WHERE status_id='" + statusid + "' AND process_id='" + processid + "'";
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                loanstatus = rs.getString("status_name");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return loanstatus;

    }

    @Override
    public LoanForm getLoanDetails(int taskid, String hrmsid) {

        LoanForm loanvalue = new LoanForm();
        Connection con = null;
        PreparedStatement ps = null;
        PreparedStatement pstask = null;
        PreparedStatement psapply = null;
        ResultSet rs = null;
        ResultSet rstask = null;
        ResultSet rsapply = null;
        Calendar now = Calendar.getInstance();
        int cmonth = now.get(Calendar.MONTH);
        int cyear = now.get(Calendar.YEAR);
        String apply_hrmsid = null;

        try {
            con = this.dataSource.getConnection();
            pstask = con.prepareStatement("SELECT a.*,to_char(a.date_drawal_adv, 'DD-Mon-YYYY') as date_drawal_format,to_char(a.date_commencement, 'DD-Mon-YYYY') as date_commencement_format,to_char(a.date_expire, 'DD-Mon-YYYY') as date_expire_format,b.* FROM hw_emp_loan a,task_master b WHERE a.taskid=b.task_id AND a.taskid='" + taskid + "' ");
            rstask = pstask.executeQuery();
            if (rstask.next()) {
                apply_hrmsid = rstask.getString("emp_id");
                loanvalue.setEmpID(apply_hrmsid);
                loanvalue.setAntprice(rstask.getString("ant_price"));
                loanvalue.setPurtype(rstask.getString("purchase_type"));
                loanvalue.setAmountadv(rstask.getString("amount_adv"));
                loanvalue.setInstalments(rstask.getInt("no_installment"));
                loanvalue.setPreviousAvail(rstask.getString("loan_availed"));
                loanvalue.setPreAdvPur(rstask.getString("motorcar_cycle_moped"));
                loanvalue.setAmounpretadv(rstask.getString("availed_amt_adv"));
                loanvalue.setDateofdrawal(rstask.getString("date_drawal_format"));
                loanvalue.setIntpaidfull(rstask.getString("principal_paid"));
                loanvalue.setAmountstanding(rstask.getString("amount_standing"));
                loanvalue.setOfficerleave(rstask.getString("officer_leave"));
                loanvalue.setDatecommleave(rstask.getString("date_commencement_format"));
                loanvalue.setDateexpireleave(rstask.getString("date_expire_format"));
                loanvalue.setLoanId(rstask.getInt("loan_code"));
                loanvalue.setTaskid(rstask.getInt("taskid"));
                loanvalue.setStatusId(rstask.getInt("status_id"));
                loanvalue.setApprovedBy(rstask.getString("apply_to"));
                loanvalue.setApprovedSpc(rstask.getString("pending_spc"));
                loanvalue.setNotes(rstask.getString("note"));
                if (rstask.getString("disk_file_name") != null && !rstask.getString("disk_file_name").equals("")) {
                    loanvalue.setDiskFileName(rstask.getString("disk_file_name"));
                } else {
                    loanvalue.setDiskFileName("");
                }
                loanvalue.setLoancomments(rstask.getString("comments"));
                //System.out.println(" DFN "+loanvalue.getDiskFileName());
                loanvalue.setLoanapplyfor(rstask.getString("loan_type"));

            }
            /**
             * ********************************** Apply user Details
             * *********************************
             */
            psapply = con.prepareStatement("SELECT ARRAY_TO_STRING(ARRAY[INITIALS, a.F_NAME, a.M_NAME,a.L_NAME], ' ') fullname,a.cur_basic_salary,to_char(a.dob, 'DD-Mon-YYYY') as dob,to_char(a.dos, 'DD-Mon-YYYY') as dos,b.spn,b.spc,c.off_name  FROM emp_mast a,g_spc b,g_office c WHERE a.cur_spc=b.spc AND a.cur_off_code=c.off_code AND   emp_id=?");
            psapply.setString(1, rstask.getString("apply_to"));
            rsapply = psapply.executeQuery();
            if (rsapply.next()) {
                String apply_name = rsapply.getString("fullname");
                String apply_spn = rsapply.getString("spn");
                String apply_emp = apply_name + " (" + apply_spn + ")";
                loanvalue.setForwardtoHrmsid(apply_emp);
            }

            ps = con.prepareStatement("SELECT a.is_regular,a.emp_id,ARRAY_TO_STRING(ARRAY[INITIALS, a.F_NAME, a.M_NAME,a.L_NAME], ' ') fullname,a.cur_basic_salary,to_char(a.dob, 'DD-Mon-YYYY') as dob,to_char(a.dos, 'DD-Mon-YYYY') as dos,b.spn,b.spc,c.off_name  FROM emp_mast a,g_spc b,g_office c WHERE a.cur_spc=b.spc AND a.cur_off_code=c.off_code AND   emp_id=?");
            ps.setString(1, rstask.getString("emp_id"));
            rs = ps.executeQuery();

            if (rs.next()) {
                loanvalue.setEmpName(rs.getString("fullname"));
                loanvalue.setBasicsalary(rs.getString("cur_basic_salary"));
                loanvalue.setDesignation(rs.getString("spn"));
                loanvalue.setEmpSPC(rs.getString("spc"));
                // des = StringUtils.strip(rs.getString("spn"), ",");
                //  loanvalue.setDesignation(rs.getString("des"));
                // System.out.println(des);
                loanvalue.setOffaddress(rs.getString("off_name"));
                loanvalue.setNetsalary(getNetPay(apply_hrmsid, cmonth, cyear) + "");
                if (rs.getString("is_regular").equals("Y")) {
                    loanvalue.setJobType("Permanent");
                } else {
                    loanvalue.setJobType("Temporary");
                }
                loanvalue.setEmpdob(rs.getString("dob"));
                loanvalue.setSuperannuation(rs.getString("dos"));

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return loanvalue;

    }

    @Override
    public List getPostList(String offcode) {

        Connection con = null;

        PreparedStatement ps = null;
        ResultSet rs = null;

        List li = new ArrayList();
        SelectOption so = null;
        try {
            con = this.dataSource.getConnection();
            ps = con.prepareStatement("select spc,post,ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME,L_NAME], ' ') EMPNAME,emp_id from ("
                    + "select spc,gpc from g_spc where off_code='" + offcode + "' AND (IFUCLEAN!='Y' OR IFUCLEAN IS NULL)) g_spc "
                    + "inner join g_post on g_spc.GPC = g_post.POST_CODE "
                    + "left outer join EMP_MAST ON G_SPC.SPC=EMP_MAST.CUR_SPC ORDER BY POST");
            rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getString("EMPNAME") != null && !rs.getString("EMPNAME").equals("")) {
                    so = new SelectOption();
                    so.setLabel(rs.getString("post"));
                    so.setDesc(rs.getString("EMPNAME"));
                    String SpcHrmsId = rs.getString("spc") + "|" + rs.getString("emp_id");
                    so.setValue(SpcHrmsId);
                    li.add(so);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return li;
    }

    @Override
    public List getprocessList(String processid) {

        Connection con = null;

        PreparedStatement ps = null;
        ResultSet rs = null;

        List li = new ArrayList();
        SelectOption so = null;
        try {
            con = this.dataSource.getConnection();
            ps = con.prepareStatement(" SELECT * FROM g_process_status WHERE status_name!='PENDING' AND process_id='" + processid + "'");
            rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getString("status_name") != null && !rs.getString("status_name").equals("")) {
                    so = new SelectOption();
                    so.setLabel(rs.getString("status_name"));

                    so.setValue(rs.getString("status_id"));
                    li.add(so);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return li;
    }

    public void saveApproveLoanData(LoanForm lform, String empid) {
        Connection con = null;
        Calendar now = Calendar.getInstance();
        Date loanapplyDate = now.getTime();

        PreparedStatement ps = null;
        ResultSet rs = null;
        Date dt1 = null;
        String pending_at = null;
        String pending_spc = null;
        int taskid = lform.getTaskid();
        int loanid = lform.getLoanId();
        int loanstatus = lform.getLoan_status();
        String Comments = lform.getLoancomments();
        String approvedby = lform.getApprovedBy();
        String approvedspc = lform.getApprovedSpc();

        DateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");

        MultipartFile loanAttch = lform.getFile_att();
        try {
            con = this.dataSource.getConnection();
            if (loanstatus == 29 || loanstatus == 27) {
                String forwardId = lform.getForwardtoHrmsid();
                String hidSpc = lform.getHidSPC();
                ps = con.prepareStatement(" SELECT * FROM task_master WHERE  task_id='" + taskid + "'");
                rs = ps.executeQuery();
                while (rs.next()) {
                    pending_at = (rs.getString("pending_at"));
                    pending_spc = (rs.getString("pending_spc"));
                }
                ps = con.prepareStatement("UPDATE task_master SET status_id=?,pending_at=?,note=?,apply_to=?,pending_spc=?,apply_to_spc=? WHERE task_id=?");
                ps.setInt(1, loanstatus);
                ps.setString(2, forwardId);
                ps.setString(3, Comments);
                ps.setString(4, forwardId);
                ps.setString(5, hidSpc);
                ps.setString(6, hidSpc);
                ps.setInt(7, taskid);
                int stsTask = ps.executeUpdate();
                if (stsTask == 1) {
                    String str = "INSERT INTO workflow_log(task_id "
                            + ",task_action_date "
                            + ",action_taken_by"
                            + ",spc_ontime"
                            + ",task_status_id"
                            + ",note"
                            + ",forward_to"
                            + ",forwarded_spc"
                            + ",log_id"
                            + ",ref_id"
                            + ") values(?,?,?,?,?,?,?,?,?,?)";
                    ps = con.prepareStatement(str);
                    ps.setInt(1, taskid);
                    ps.setTimestamp(2, new Timestamp(loanapplyDate.getTime()));
                    ps.setString(3, pending_at);
                    ps.setString(4, pending_spc);
                    ps.setInt(5, loanstatus);
                    ps.setString(6, Comments);
                    ps.setString(7, forwardId);
                    ps.setString(8, hidSpc);
                    ps.setInt(10, loanid);
                    int wfcode = CommonFunctions.getMaxCodeInteger("workflow_log", "log_id", con);
                    ps.setInt(9, wfcode);
                    ps.executeUpdate();

                    ps = con.prepareStatement("UPDATE hw_emp_loan SET loan_status=? WHERE taskid=? AND loan_code=?");
                    ps.setInt(1, loanstatus);
                    ps.setInt(2, taskid);
                    ps.setInt(3, loanid);
                    ps.executeUpdate();

                }

            } else {
                ps = con.prepareStatement("UPDATE hw_emp_loan SET loan_status=?,approved_by=?,approved_spc=?,comments=?,loan_sanction_date=? WHERE taskid=? AND loan_code=?");
                ps.setInt(1, loanstatus);
                ps.setString(2, approvedby);
                ps.setString(3, approvedspc);
                ps.setString(4, Comments);
                ps.setTimestamp(5, new Timestamp(loanapplyDate.getTime()));
                ps.setInt(6, taskid);
                ps.setInt(7, loanid);
                int stsTask = ps.executeUpdate();

                if (stsTask == 1) {
                    ps = con.prepareStatement("UPDATE task_master SET status_id=?,pending_at=?,note=?,apply_to=?,pending_spc=?,apply_to_spc=? WHERE task_id=?");
                    ps.setInt(1, loanstatus);
                    ps.setString(2, null);
                    ps.setString(3, Comments);
                    ps.setString(4, null);
                    ps.setString(5, null);
                    ps.setString(6, null);
                    ps.setInt(7, taskid);
                    ps.executeUpdate();
                }    //  System.out.println( taskid);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    @Override
    public LoanForm ReplyLoan(String option, String hrmsid, int loanid) {

        LoanForm loanvalue = new LoanForm();
        Connection con = null;
        PreparedStatement ps = null;
        PreparedStatement psapply = null;
        ResultSet rs = null;
        ResultSet rsapply = null;
        Calendar now = Calendar.getInstance();
        int cmonth = now.get(Calendar.MONTH);
        int cyear = now.get(Calendar.YEAR);
        try {
            con = this.dataSource.getConnection();
            ps = con.prepareStatement("SELECT a.is_regular,a.emp_id,ARRAY_TO_STRING(ARRAY[INITIALS, a.F_NAME, a.M_NAME,a.L_NAME], ' ') fullname,a.cur_basic_salary,to_char(a.dob, 'DD-Mon-YYYY') as dob,to_char(a.dos, 'DD-Mon-YYYY') as dos,b.spn,b.spc,c.off_name  FROM emp_mast a,g_spc b,g_office c WHERE a.cur_spc=b.spc AND a.cur_off_code=c.off_code AND   emp_id=?");
            ps.setString(1, hrmsid);
            rs = ps.executeQuery();
            if (rs.next()) {
                loanvalue.setEmpName(rs.getString("fullname"));
                loanvalue.setBasicsalary(rs.getString("cur_basic_salary"));
                loanvalue.setDesignation(rs.getString("spn"));
                loanvalue.setEmpSPC(rs.getString("spc"));
                // des = StringUtils.strip(rs.getString("spn"), ",");
                //  loanvalue.setDesignation(rs.getString("des"));
                // System.out.println(des);
                loanvalue.setOffaddress(rs.getString("off_name"));
                loanvalue.setNetsalary(getNetPay(hrmsid, cmonth, cyear) + "");
                if (rs.getString("is_regular").equals("Y")) {
                    loanvalue.setJobType("Permanent");
                } else {
                    loanvalue.setJobType("Temporary");
                }
                loanvalue.setEmpdob(rs.getString("dob"));
                loanvalue.setSuperannuation(rs.getString("dos"));

            }
            ps = con.prepareStatement("SELECT a.*,b.*,to_char(a.date_drawal_adv, 'DD-Mon-YYYY') as date_drawal_format,to_char(a.date_commencement, 'DD-Mon-YYYY') as date_commencement_format,to_char(a.date_expire, 'DD-Mon-YYYY') as date_expire_format FROM hw_emp_loan a,task_master b WHERE a.taskid=b.task_id AND  a.loan_code=?");
            ps.setInt(1, loanid);
            rs = ps.executeQuery();
            if (rs.next()) {
                loanvalue.setLoanId(loanid);
                loanvalue.setInstalments(rs.getInt("no_installment"));
                loanvalue.setAntprice(rs.getString("ant_price"));

                // String Ptype=rs.getString("purchase_type");                  
                loanvalue.setPurtype(rs.getString("purchase_type"));

                loanvalue.setAmountadv(rs.getDouble("amount_adv") + "");
                loanvalue.setPreviousAvail(rs.getString("loan_availed"));
                loanvalue.setPreAdvPur(rs.getString("motorcar_cycle_moped"));
                loanvalue.setAmounpretadv(rs.getDouble("availed_amt_adv") + "");
                loanvalue.setDateofdrawal(rs.getString("date_drawal_format"));
                loanvalue.setIntpaidfull(rs.getString("principal_paid"));
                loanvalue.setAmountstanding(rs.getDouble("amount_standing") + "");
                loanvalue.setOfficerleave(rs.getString("officer_leave"));
                loanvalue.setDatecommleave(rs.getString("date_commencement_format"));
                loanvalue.setDateexpireleave(rs.getString("date_expire_format"));
                loanvalue.setFileView(rs.getString("original_filename"));
                loanvalue.setTaskid(rs.getInt("taskid"));
                loanvalue.setLoancomments(rs.getString("note"));
                loanvalue.setDiskFileName(rs.getString("disk_file_name"));
                loanvalue.setLoanapplyfor(rs.getString("loan_type"));

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return loanvalue;
    }

    @Override
    public LoanForm SactionLoanOrder(int taskid, String hrmsid, int loanid) {

        LoanForm loanvalue = new LoanForm();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        loanvalue.setTaskid(taskid);
        loanvalue.setLoanId(loanid);
        try {
            con = this.dataSource.getConnection();
            // ps = con.prepareStatement("SELECT a.is_regular,gpf_no,a.gp,a.emp_id,ARRAY_TO_STRING(ARRAY[INITIALS, a.F_NAME, a.M_NAME,a.L_NAME], ' ') fullname,a.cur_basic_salary,to_char(a.dob, 'DD-Mon-YYYY') as dob,to_char(a.dos, 'DD-Mon-YYYY') as dos,b.spn,b.spc,b.gpc,c.post  FROM emp_mast a,g_spc b,g_post c WHERE  a.cur_spc=b.spc AND b.gpc=c.post_code   AND     emp_id=?");
            ps = con.prepareStatement("SELECT a.is_regular,a.gpf_no,a.gp,a.emp_id,ARRAY_TO_STRING(ARRAY[INITIALS, a.F_NAME, a.M_NAME,"
                    + "a.L_NAME], ' ') fullname,to_char(a.dob, 'DD-Mon-YYYY') as dob,a.cur_basic_salary,to_char(a.JOINDATE_OF_GOO, 'DD-Mon-YYYY') as doj,"
                    + "to_char(a.dos, 'DD-Mon-YYYY') as dos,b.spn,b.spc,b.gpc,c.post,d.department_name,"
                    + "EXTRACT(YEAR from AGE(NOW(), a.JOINDATE_OF_GOO)) as age,EXTRACT(YEAR from AGE(a.dos,NOW())) as remainingage,e.ddo_code,e.off_en  FROM emp_mast a,g_spc b,g_post c,"
                    + "g_department d,g_office e WHERE  a.cur_spc=b.spc AND b.dept_code=d.department_code AND  "
                    + " b.gpc=c.post_code AND a.cur_off_code=e.off_code AND emp_id=?");

            ps.setString(1, hrmsid);
            rs = ps.executeQuery();
            if (rs.next()) {
                String Loanee_name = rs.getString("fullname");

                String Loanee_des = rs.getString("post");
                String loanee_name_des = Loanee_name + "<br/>" + Loanee_des;
                loanvalue.setEmpName(loanee_name_des);

                String loanee_basicsalary = rs.getString("cur_basic_salary");
                String loanee_gp = rs.getString("gp");

                String basic_gp = "RS " + loanee_basicsalary + "/-" + "<br/>" + "+" + "<br/>" + "RS " + loanee_gp + "/-";
                loanvalue.setBasicsalary(basic_gp);
                if (rs.getString("is_regular").equals("Y")) {
                    loanvalue.setJobType("Perm.");
                } else {
                    loanvalue.setJobType("Temp.");
                }

                loanvalue.setGpfno(rs.getString("gpf_no"));

                loanvalue.setEmpdob(rs.getString("dob"));
                loanvalue.setOffaddress(rs.getString("department_name"));
                loanvalue.setName(Loanee_name);

            }
            ps = con.prepareStatement("SELECT a.* FROM hw_emp_loan a WHERE loan_code=?");
            ps.setInt(1, loanid);
            rs = ps.executeQuery();
            if (rs.next()) {
                loanvalue.setInstalments(rs.getInt("no_installment"));

                Double amount_adv = rs.getDouble("amount_adv");
                loanvalue.setAmounpretadv(Math.round(amount_adv) + "/-");
                Double amount_inst = amount_adv / rs.getInt("no_installment");

                String amt = "RS " + Math.round(amount_inst) + "/-";
                loanvalue.setAmountadv(amt);
                loanvalue.setPreAdvPur(rs.getString("motorcar_cycle_moped"));
                loanvalue.setInstalments(rs.getInt("no_installment"));
                loanvalue.setLetterNo(rs.getString("letterno"));
                loanvalue.setLetterDate(rs.getString("letterdate"));
                loanvalue.setLetterformName(rs.getString("letterfrom"));
                loanvalue.setLetterformdesignation(rs.getString("fromdesignation"));
                loanvalue.setLetterto(rs.getString("letterto"));
                loanvalue.setMemoNo(rs.getString("memono"));
                loanvalue.setMemoDate(rs.getString("memodate"));

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }

        return loanvalue;
    }

    @Override
    public LoanForm PreviewSactionOrder(int taskid, String hrmsid, int loanid) {

        LoanForm loanvalue = new LoanForm();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        loanvalue.setTaskid(taskid);
        loanvalue.setLoanId(loanid);
        try {
            con = this.dataSource.getConnection();
            // ps = con.prepareStatement("SELECT a.is_regular,gpf_no,a.gp,a.emp_id,ARRAY_TO_STRING(ARRAY[INITIALS, a.F_NAME, a.M_NAME,a.L_NAME], ' ') fullname,a.cur_basic_salary,to_char(a.dob, 'DD-Mon-YYYY') as dob,to_char(a.dos, 'DD-Mon-YYYY') as dos,b.spn,b.spc,b.gpc,c.post  FROM emp_mast a,g_spc b,g_post c WHERE  a.cur_spc=b.spc AND b.gpc=c.post_code   AND     emp_id=?");
            ps = con.prepareStatement("SELECT a.is_regular,a.gpf_no,a.gp,a.emp_id,ARRAY_TO_STRING(ARRAY[INITIALS, a.F_NAME, a.M_NAME,"
                    + "a.L_NAME], ' ') fullname,to_char(a.dob, 'DD-Mon-YYYY') as dob,a.cur_basic_salary,to_char(a.JOINDATE_OF_GOO, 'DD-Mon-YYYY') as doj,"
                    + "to_char(a.dos, 'DD-Mon-YYYY') as dos,b.spn,b.spc,b.gpc,c.post,d.department_name,"
                    + "EXTRACT(YEAR from AGE(NOW(), a.JOINDATE_OF_GOO)) as age,EXTRACT(YEAR from AGE(a.dos,NOW())) as remainingage,e.ddo_code,e.off_en  FROM emp_mast a,g_spc b,g_post c,"
                    + "g_department d,g_office e WHERE  a.cur_spc=b.spc AND b.dept_code=d.department_code AND  "
                    + " b.gpc=c.post_code AND a.cur_off_code=e.off_code AND emp_id=?");

            ps.setString(1, hrmsid);
            rs = ps.executeQuery();
            if (rs.next()) {
                String Loanee_name = rs.getString("fullname");

                String Loanee_des = rs.getString("post");
                String loanee_name_des = Loanee_name + "<br/>" + Loanee_des;
                loanvalue.setEmpName(loanee_name_des);

                String loanee_basicsalary = rs.getString("cur_basic_salary");
                String loanee_gp = rs.getString("gp");

                String basic_gp = "RS " + loanee_basicsalary + "/-" + "<br/>" + "+" + "<br/>" + "RS " + loanee_gp + "/-";
                loanvalue.setBasicsalary(basic_gp);
                if (rs.getString("is_regular").equals("Y")) {
                    loanvalue.setJobType("Perm.");
                } else {
                    loanvalue.setJobType("Temp.");
                }

                loanvalue.setGpfno(rs.getString("gpf_no"));

                loanvalue.setEmpdob(rs.getString("dob"));
                loanvalue.setOffaddress(rs.getString("department_name"));
                loanvalue.setName(Loanee_name);

            }
            ps = con.prepareStatement("SELECT a.* FROM hw_emp_loan a WHERE loan_code=?");
            ps.setInt(1, loanid);
            rs = ps.executeQuery();
            if (rs.next()) {
                loanvalue.setInstalments(rs.getInt("no_installment"));

                Double amount_adv = rs.getDouble("amount_adv");
                loanvalue.setAmounpretadv(Math.round(amount_adv) + "/-");
                Double amount_inst = amount_adv / rs.getInt("no_installment");

                String amt = "RS " + Math.round(amount_inst) + "/-";
                loanvalue.setAmountadv(amt);
                loanvalue.setPreAdvPur(rs.getString("motorcar_cycle_moped"));
                loanvalue.setInstalments(rs.getInt("no_installment"));
                loanvalue.setLetterNo(rs.getString("letterno"));
                loanvalue.setLetterDate(rs.getString("letterdate"));
                loanvalue.setLetterformName(rs.getString("letterfrom"));
                loanvalue.setLetterformdesignation(rs.getString("fromdesignation"));
                loanvalue.setLetterto(rs.getString("letterto"));
                loanvalue.setMemoNo(rs.getString("memono"));
                loanvalue.setMemoDate(rs.getString("memodate"));

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }

        return loanvalue;
    }

    @Override
    public void saveLoansaction(LoanForm lform) {
        Connection con = null;
        Calendar now = Calendar.getInstance();
        Date loanapplyDate = now.getTime();

        PreparedStatement ps = null;
        ResultSet rs = null;
        Date dt1 = null;
        String pending_at = null;
        String pending_spc = null;
        int taskid = lform.getTaskid();
        int loanid = lform.getLoanId();

        String letterno = lform.getLetterNo();
        String letterdate = lform.getLetterDate();
        String letterform = lform.getLetterformName();
        String letterto = lform.getLetterto();
        String letterdesi = lform.getLetterformdesignation();
        String memono = lform.getMemoNo();
        String memodate = lform.getMemoDate();

        MultipartFile loanAttch = lform.getFile_att();
        try {
            con = this.dataSource.getConnection();
            ps = con.prepareStatement("UPDATE hw_emp_loan SET letterno=?,letterdate=?,letterfrom=?,fromdesignation=?,letterto=?,memono=?,memodate=? WHERE taskid=? AND loan_code=?");
            ps.setString(1, letterno);
            ps.setString(2, letterdate);
            ps.setString(3, letterform);
            ps.setString(4, letterdesi);
            ps.setString(5, letterto);
            ps.setString(6, memono);
            ps.setString(7, memodate);
            ps.setInt(8, taskid);
            ps.setInt(9, loanid);
            int stsTask = ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    @Override
    public void downloadLoanAttachment(HttpServletResponse response, String filepath, String loanid) throws IOException {

        Connection con = null;

        Statement stmt = null;
        ResultSet rs = null;
        int BUFFER_LENGTH = 4096;
        OutputStream out = response.getOutputStream();
        try {
            con = dataSource.getConnection();

            stmt = con.createStatement();
            String sql = "SELECT * FROM HW_EMP_LOAN WHERE LOAN_CODE='" + loanid + "'";
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                File f = null;
                String dirpath = filepath + "/" + rs.getString("DISK_FILE_NAME");
                System.out.println("dirpath is: " + dirpath);
                f = new File(dirpath);
                if (f.exists()) {
                    String originalFilename = rs.getString("ORIGINAL_FILENAME");
                    String filetype = rs.getString("FILE_TYPE");

                    response.setContentLength((int) f.length());
                    FileInputStream is = new FileInputStream(f);

                    response.setContentType(filetype);
                    response.setHeader("Content-Disposition", "attachment; filename=\"" + originalFilename + "\"");

                    byte[] bytes = new byte[BUFFER_LENGTH];
                    int read = 0;
                    while ((read = is.read(bytes, 0, BUFFER_LENGTH)) != -1) {
                        out.write(bytes, 0, read);
                    }
                    is.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, stmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    public void savereapplyLoanData(LoanForm lform, String empid, String filepath) {
        Connection con = null;
        Calendar now = Calendar.getInstance();
        Date loanapplyDate = now.getTime();

        PreparedStatement ps = null;
        ResultSet rs = null;
        Date dt1 = null;
        Date dt2 = null;
        Date dt3 = null;

        DateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        MultipartFile loanAttch = lform.getFile_att();
        try {
            con = this.dataSource.getConnection();
            String forwardId = lform.getForwardtoHrmsid();
            String hidSpc = lform.getHidSPC();
            //System.out.println(forwardId + "-- " + hidSpc );
            double antprice = Double.parseDouble(lform.getAntprice());
            String session_empId = empid;
            String designation = lform.getDesignation();
            double basicsalary = Double.parseDouble(lform.getBasicsalary());
            double netsalary = Double.parseDouble(lform.getNetsalary());
            String hidOffCode = lform.getHidOffCode();
            String hidOffName = lform.getHidOffName();

            String purtype = lform.getPurtype();
            String amountadv = lform.getAmountadv();

            int instalments = lform.getInstalments();
            String previousAvail = lform.getPreviousAvail();
            String PreAdvPur = lform.getPreAdvPur();
            String amounpretadv = lform.getAmounpretadv();

            String dateofdrawal = lform.getDateofdrawal();
            String intpaidfull = lform.getIntpaidfull();
            String amountstanding = lform.getAmountstanding();

            String officerleave = lform.getOfficerleave();
            String datecommleave = lform.getDatecommleave();
            String dateexpireleave = lform.getDateexpireleave();
            String forwardto = lform.getForwardto();
            String loanapplyfor = lform.getLoanapplyfor();
            int loanvalue = 25;
            String initiatedSpc = lform.getEmpSPC();
            int taskid = lform.getTaskid();

            /**
             * **********************************************************************************
             */
            String strTaskmaster = "UPDATE   task_master SET status_id=?,pending_at=?,apply_to=?,pending_spc=?,apply_to_spc=?,initiated_on=?,note=? WHERE task_id=?";

            ps = con.prepareStatement(strTaskmaster);

            ps.setInt(1, loanvalue);
            ps.setString(2, forwardId);
            ps.setString(3, forwardId);

            ps.setString(4, hidSpc);
            ps.setString(5, hidSpc);
            ps.setTimestamp(6, new Timestamp(loanapplyDate.getTime()));
            ps.setString(7, "");
            ps.setInt(8, taskid);

            int stsTask = ps.executeUpdate();

            /**
             * *************************************************************************************
             */
            //  dt2 = new Date(datecommleave);
            // dt3 = new Date(dateexpireleave);
            // System.out.println(designation + "-- " + basicsalary + "--" + netsalary + " " + hidOffCode + "--" + hidOffName + "--" + hidSpc);
            if (stsTask == 1) {

                String str = "UPDATE hw_emp_loan SET basic_salary=?,net_salary=?,ant_price=?,purchase_type=?,amount_adv=?,no_installment=?,loan_availed=?  ";
                str = str + ",date_drawal_adv=?,availed_amt_adv=?,principal_paid=?,loan_type=?,motorcar_cycle_moped=?,amount_standing=?,officer_leave=?,date_commencement=?,date_expire=?,forward_to=?";
                str = str + ",loan_status=?,loan_apply_date=?,comments=? WHERE loan_code=? ";
                ps = con.prepareStatement(str);

                ps.setDouble(1, basicsalary);
                ps.setDouble(2, netsalary);
                ps.setDouble(3, antprice);
                ps.setString(4, purtype);

                if (amountadv != null && !amountadv.equals("")) {
                    ps.setDouble(5, Double.parseDouble(amountadv));
                } else {
                    ps.setDouble(5, 0);
                }
                ps.setInt(6, instalments);
                ps.setString(7, previousAvail);
                if (lform.getDateofdrawal() != null && !lform.getDateofdrawal().equals("")) {
                    ps.setTimestamp(8, new Timestamp(sdf.parse(lform.getDateofdrawal()).getTime()));
                } else {
                    ps.setTimestamp(8, null);
                }
                if (amounpretadv != null && !amounpretadv.equals("")) {
                    ps.setDouble(9, Double.parseDouble(amounpretadv));
                } else {
                    ps.setDouble(9, 0);
                }
                ps.setString(10, intpaidfull);
                ps.setString(11, loanapplyfor);
                ps.setString(12, PreAdvPur);

                if (amountstanding != null && !amountstanding.equals("")) {
                    ps.setDouble(13, Double.parseDouble(amountstanding));
                } else {
                    ps.setDouble(13, 0);
                }
                ps.setString(14, officerleave);
                if (lform.getDatecommleave() != null && !lform.getDatecommleave().equals("")) {
                    ps.setTimestamp(15, new Timestamp(sdf.parse(lform.getDatecommleave()).getTime()));
                } else {
                    ps.setTimestamp(15, null);
                }
                if (lform.getDateexpireleave() != null && !lform.getDateexpireleave().equals("")) {
                    ps.setTimestamp(16, new Timestamp(sdf.parse(lform.getDateexpireleave()).getTime()));
                } else {
                    ps.setTimestamp(16, null);
                }
                ps.setString(17, hidSpc);
                ps.setInt(18, loanvalue);
                ps.setTimestamp(19, new Timestamp(loanapplyDate.getTime()));
                ps.setString(20, "");
                int loanid = lform.getLoanId();
                ps.setInt(21, loanid);
                int sts = ps.executeUpdate();

                if (sts > 0) {
                    InputStream inputStream = null;
                    OutputStream outputStream = null;
                    String filename = "";
                    if (loanAttch != null && !loanAttch.isEmpty()) {
                        long time = System.currentTimeMillis();
                        filename = session_empId + "_" + taskid + "_" + time;

                        ps = con.prepareStatement("UPDATE hw_emp_loan SET original_filename=?,disk_file_name=?,file_type=? WHERE TASKID=?");
                        ps.setString(1, loanAttch.getOriginalFilename());
                        ps.setString(2, filename);
                        ps.setString(3, loanAttch.getContentType());
                        ps.setInt(4, taskid);
                        ps.executeUpdate();

                        String dirpath = filepath + "/";
                        File newfile = new File(dirpath);
                        if (!newfile.exists()) {
                            newfile.mkdirs();
                        }

                        outputStream = new FileOutputStream(dirpath + filename);
                        int read = 0;
                        byte[] bytes = new byte[1024];
                        inputStream = loanAttch.getInputStream();
                        while ((read = inputStream.read(bytes)) != -1) {
                            outputStream.write(bytes, 0, read);
                        }
                    }
                }
            }

            /* ps = con.prepareStatement("SELECT a.emp_id,a.f_name  FROM emp_mast a,g_spc b WHERE a.cur_spc=b.spc AND b.spc=?");
             ps.setString(1, hidSpc);
             rs = ps.executeQuery();
             String applyToHrmsid=rs.getString("emp_id");*/
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    @Override
    public void deleteLoanAttch(int loanid, String filepath) {

        Connection con = null;

        Statement stmt = null;
        ResultSet rs = null;

        PreparedStatement pst = null;

        boolean isDeleted = false;
        try {
            con = dataSource.getConnection();
            stmt = con.createStatement();
            String sql = "SELECT original_filename,disk_file_name FROM HW_EMP_LOAN WHERE LOAN_CODE='" + loanid + "'";
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                File f = null;
                String dirpath = filepath + "/" + rs.getString("DISK_FILE_NAME");
                System.out.println("dirpath to delete is: " + dirpath);
                f = new File(dirpath);
                if (f.exists()) {
                    System.out.println("Inside Delete if");
                    isDeleted = f.delete();
                }
            }
            if (isDeleted) {
                pst = con.prepareStatement("UPDATE HW_EMP_LOAN SET original_filename='',disk_file_name='',file_type='' WHERE LOAN_CODE='" + loanid + "'");
                pst.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, stmt);
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        //return retval2;
    }

    @Override
    public LoanGPFForm GPFEmpDetails(String hrmsid) {
        LoanGPFForm loanvalue = new LoanGPFForm();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Calendar now = Calendar.getInstance();
        int cmonth = now.get(Calendar.MONTH);
        int cyear = now.get(Calendar.YEAR);
        int previousYear = cyear;
        try {
            con = this.dataSource.getConnection();
            ps = con.prepareStatement("SELECT a.gpf_no,a.gp,a.emp_id,ARRAY_TO_STRING(ARRAY[INITIALS, a.F_NAME, a.M_NAME,"
                    + "a.L_NAME], ' ') fullname,a.cur_basic_salary,to_char(a.JOINDATE_OF_GOO, 'DD-Mon-YYYY') as doj,"
                    + "to_char(a.dos, 'DD-Mon-YYYY') as dos,b.spn,b.spc,b.gpc,c.post,d.department_name,"
                    + "EXTRACT(YEAR from AGE(NOW(), a.JOINDATE_OF_GOO)) as age,EXTRACT(YEAR from AGE(a.dos,NOW())) as remainingage,e.ddo_code,e.off_en  FROM emp_mast a,g_spc b,g_post c,"
                    + "g_department d,g_office e WHERE  a.cur_spc=b.spc AND b.dept_code=d.department_code AND  "
                    + " b.gpc=c.post_code AND a.cur_off_code=e.off_code AND emp_id=?");
            ps.setString(1, hrmsid);
            rs = ps.executeQuery();
            if (rs.next()) {
                loanvalue.setEmpName(rs.getString("fullname"));
                String gpfno = rs.getString("gpf_no");
                loanvalue.setEmpSPC(rs.getString("spc"));
                String deptname = rs.getString("department_name");
                String accountNo = gpfno + " " + deptname;
                loanvalue.setGpfno(accountNo);
                int serviceYear = rs.getInt("age");
                int remainingAge = rs.getInt("remainingage");
                String gpftypestatus = null;
                if (serviceYear >= 20 || remainingAge < 10) {
                    gpftypestatus = "NON-REFUNDABLE";
                } else {
                    gpftypestatus = "REFUNDABLE";
                }
                loanvalue.setGpftype(gpftypestatus);
                String basicsalary_gp = rs.getString("cur_basic_salary") + "+ GP " + rs.getString("gp");
                loanvalue.setDesignation(rs.getString("post"));
                loanvalue.setPay(basicsalary_gp);
                loanvalue.setDoj(rs.getString("doj"));
                loanvalue.setSupperannuation(rs.getString("dos"));
                //System.out.println("cmonth---"+cmonth);
                if (cmonth <= 3) {
                    previousYear = cyear - 1;
                    //cyear = previousYear;

                }
                if (cmonth == 0) {
                    cmonth = 12;
                }
                loanvalue.setCyear(cyear);

                loanvalue.setPreviousYear(previousYear);
                loanvalue.setCmonth(cmonth);
                String ddo_code = rs.getString("ddo_code");
                String ddo_office = rs.getString("off_en");
                String ddooffice = ddo_office + "(" + ddo_code + ")";
                loanvalue.setAccountOfficer(ddooffice);
                loanvalue.setDdocode(ddo_code);
                //  System.out.println("remainingage" + rs.getInt("remainingage") + "ge" + serviceYear);

            }
            ps = con.prepareStatement("SELECT SUM(ad_amt) as total_subscription FROM aq_dtls WHERE ad_code='GPF' AND  emp_code='" + hrmsid + "' AND (p_month BETWEEN 4 AND '" + cmonth + "' AND (p_year  BETWEEN  '" + previousYear + "' AND  '" + cyear + "')) ");
            //  System.out.println("SELECT SUM(ad_amt) as total_subscription FROM aq_dtls WHERE ad_code='GPF' AND  emp_code='" + hrmsid + "' AND (p_month BETWEEN 4 AND '" + cmonth + "' AND (p_year  BETWEEN  '" + previousYear + "' AND  '" + cyear + "')) ");
            rs = ps.executeQuery();
            String total_subscription = null;
            if (rs.next()) {
                total_subscription = rs.getString("total_subscription");
            }
            loanvalue.setCreditAmount(total_subscription);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return loanvalue;
    }

    public void savegpfLoanData(LoanGPFForm lform, String empid, String filepath) {
        Connection con = null;
        Calendar now = Calendar.getInstance();
        Date loanapplyDate = now.getTime();

        PreparedStatement ps = null;
        ResultSet rs = null;
        Date dt1 = null;
        Date dt2 = null;
        Date dt3 = null;

        DateFormat cdate = new SimpleDateFormat("dd-MMM-yyyy");
        MultipartFile loanAttch = lform.getFile_att();
        try {
            con = this.dataSource.getConnection();
            String forwardId = lform.getForwardtoHrmsid();
            String hidSpc = lform.getHidSPC();
            int loanvalue = 35;
            String initiatedSpc = lform.getEmpSPC();
            // String loanapplyfor=lform.getLoanapplyfor();
            String session_empId = empid;

            String cbalnace = lform.getClosingbalance();
            String camount = lform.getCreditAmount();
            String cform = lform.getCreditForm();
            String cto = lform.getCreditTo();
            String ramount = lform.getRefund();
            String wfrom = lform.getWithdrawfrom();
            String wto = lform.getWithdrawto();
            String wamount = lform.getWithdrawalAmount();
            String nbalance = lform.getNetbalance();
            String amountrequired = lform.getWithdrawalreq();
            String purpose = lform.getPurpose();
            String rulegpf_request = lform.getRequestcovered();
            String ddo = lform.getAccountOfficer();
            String gppftype = lform.getGpftype();
            String gppftype_value = gppftype.replace(",", "");
            String samepurpose = lform.getWithdrawaltaken();
            String bcredit = lform.getBalanceCredit();

            int mcode = CommonFunctions.getMaxCodeInteger("TASK_MASTER", "TASK_ID", con);
            String strTaskmaster = "INSERT INTO task_master (task_id "
                    + ",process_id "
                    + ",initiated_by "
                    + ",status_id "
                    + ",pending_at "
                    + ",apply_to "
                    + ",initiated_spc "
                    + ",pending_spc "
                    + ",apply_to_spc "
                    + ",initiated_on "
                    + ") values(?,?,?,?,?,?,?,?,?,?)";
            ps = con.prepareStatement(strTaskmaster);

            ps.setInt(1, mcode);
            ps.setInt(2, 8);
            ps.setString(3, session_empId);
            ps.setInt(4, loanvalue);
            ps.setString(5, forwardId);
            ps.setString(6, forwardId);
            ps.setString(7, initiatedSpc);
            ps.setString(8, hidSpc);
            ps.setString(9, hidSpc);
            ps.setTimestamp(10, new Timestamp(loanapplyDate.getTime()));

            int stsTask = ps.executeUpdate();

            if (stsTask == 1) {
                String str = "INSERT INTO hw_emp_gpf_loan(taskid "
                        + ",emp_id "
                        + ",closing_balance"
                        + ",credit_amount"
                        + ",credit_from"
                        + ",credit_to"
                        + ",refund_amount"
                        + ",withdrawal_from"
                        + ",withdrawal_to"
                        + ",withdrawal_amount"
                        + ",net_balance"
                        + ",amount_required"
                        + ",purpose"
                        + ",rule_gpf"
                        + ",same_purpse_details"
                        + ",ddo_code"
                        + ",forward_to"
                        + ",loan_apply_date"
                        + ",loan_status,gpftype,balancecredit) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                ps = con.prepareStatement(str);
                ps.setInt(1, mcode);
                ps.setString(2, session_empId);
                if (cbalnace != null && !cbalnace.equals("")) {
                    ps.setDouble(3, Double.parseDouble(cbalnace));
                } else {
                    ps.setDouble(3, 0);
                }
                if (camount != null && !camount.equals("")) {
                    ps.setDouble(4, Double.parseDouble(camount));
                } else {
                    ps.setDouble(4, 0);
                }
                ps.setString(5, cform);
                ps.setString(6, cto);
                if (ramount != null && !ramount.equals("")) {
                    ps.setDouble(7, Double.parseDouble(ramount));
                } else {
                    ps.setDouble(7, 0);
                }
                ps.setString(8, wfrom);
                ps.setString(9, wto);
                if (wamount != null && !wamount.equals("")) {
                    ps.setDouble(10, Double.parseDouble(wamount));
                } else {
                    ps.setDouble(10, 0);
                }
                if (nbalance != null && !nbalance.equals("")) {
                    ps.setDouble(11, Double.parseDouble(nbalance));
                } else {
                    ps.setDouble(11, 0);
                }
                if (amountrequired != null && !amountrequired.equals("")) {
                    ps.setDouble(12, Double.parseDouble(amountrequired));
                } else {
                    ps.setDouble(12, 0);
                }
                ps.setString(13, purpose);
                ps.setString(14, rulegpf_request);
                ps.setString(15, samepurpose);
                ps.setString(16, ddo);
                ps.setString(17, hidSpc);
                ps.setTimestamp(18, new Timestamp(loanapplyDate.getTime()));
                ps.setInt(19, loanvalue);
                ps.setString(20, gppftype_value);
                if (bcredit != null && !bcredit.equals("")) {
                    ps.setDouble(21, Double.parseDouble(bcredit));
                } else {
                    ps.setDouble(21, 0);
                }
                int sts = ps.executeUpdate();
                if (sts > 0) {
                    InputStream inputStream = null;
                    OutputStream outputStream = null;
                    String filename = "";
                    if (loanAttch != null && !loanAttch.isEmpty()) {
                        long time = System.currentTimeMillis();
                        filename = session_empId + "_" + mcode + "_" + time;

                        ps = con.prepareStatement("UPDATE hw_emp_gpf_loan SET original_filename=?,disk_file_name=?,file_type=? WHERE TASKID=?");
                        ps.setString(1, loanAttch.getOriginalFilename());
                        ps.setString(2, filename);
                        ps.setString(3, loanAttch.getContentType());
                        ps.setInt(4, mcode);
                        ps.executeUpdate();

                        String dirpath = filepath + "/";
                        File newfile = new File(dirpath);
                        if (!newfile.exists()) {
                            newfile.mkdirs();
                        }

                        outputStream = new FileOutputStream(dirpath + filename);
                        int read = 0;
                        byte[] bytes = new byte[1024];
                        inputStream = loanAttch.getInputStream();
                        while ((read = inputStream.read(bytes)) != -1) {
                            outputStream.write(bytes, 0, read);
                        }
                    }

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    @Override
    public LoanGPFForm getgpfLoanDetails(int taskid, String hrmsid) {

        LoanGPFForm loanvalue = new LoanGPFForm();
        Connection con = null;
        PreparedStatement ps = null;
        PreparedStatement pstask = null;
        PreparedStatement psapply = null;
        ResultSet rs = null;
        ResultSet rstask = null;
        ResultSet rsapply = null;
        Calendar now = Calendar.getInstance();
        int cmonth = now.get(Calendar.MONTH);
        int cyear = now.get(Calendar.YEAR);
        int previousYear = cyear;
        String empId = null;

        try {
            con = this.dataSource.getConnection();
            pstask = con.prepareStatement("SELECT a.*,b.* FROM hw_emp_gpf_loan a,task_master b WHERE a.taskid=b.task_id AND a.taskid='" + taskid + "' ");
            rstask = pstask.executeQuery();
            if (rstask.next()) {
                empId = rstask.getString("emp_id");
                loanvalue.setEmpId(rstask.getString("emp_id"));
                loanvalue.setClosingbalance(rstask.getString("closing_balance"));
                loanvalue.setBalanceCredit(rstask.getString("balancecredit"));
                loanvalue.setCreditForm(rstask.getString("credit_from"));
                loanvalue.setCreditTo(rstask.getString("credit_to"));
                loanvalue.setCreditAmount(rstask.getString("credit_amount"));
                loanvalue.setRefund(rstask.getString("refund_amount"));
                loanvalue.setWithdrawfrom(rstask.getString("withdrawal_from"));
                loanvalue.setWithdrawto(rstask.getString("withdrawal_to"));
                loanvalue.setWithdrawalAmount(rstask.getString("withdrawal_amount"));
                loanvalue.setNetbalance(rstask.getString("net_balance"));
                loanvalue.setWithdrawalreq(rstask.getString("amount_required"));
                loanvalue.setPurpose(rstask.getString("purpose"));
                loanvalue.setRequestcovered(rstask.getString("rule_gpf"));
                loanvalue.setWithdrawaltaken(rstask.getString("same_purpse_details"));
                loanvalue.setApprovedBy(rstask.getString("apply_to"));
                loanvalue.setApprovedSpc(rstask.getString("pending_spc"));
                loanvalue.setLoanId(rstask.getInt("loan_gpf_id"));
                loanvalue.setTaskid(rstask.getInt("taskid"));
                loanvalue.setLoancomments(rstask.getString("comments"));
                loanvalue.setStatusId(rstask.getInt("status_id"));

            }

            ps = con.prepareStatement("SELECT a.gpf_no,a.gp,a.emp_id,ARRAY_TO_STRING(ARRAY[INITIALS, a.F_NAME, a.M_NAME,"
                    + "a.L_NAME], ' ') fullname,a.cur_basic_salary,to_char(a.JOINDATE_OF_GOO, 'DD-Mon-YYYY') as doj,"
                    + "to_char(a.dos, 'DD-Mon-YYYY') as dos,b.spn,b.spc,b.gpc,c.post,d.department_name,"
                    + "EXTRACT(YEAR from AGE(NOW(), a.JOINDATE_OF_GOO)) as age,EXTRACT(YEAR from AGE(a.dos,NOW())) as remainingage,e.ddo_code,e.off_en  FROM emp_mast a,g_spc b,g_post c,"
                    + "g_department d,g_office e WHERE  a.cur_spc=b.spc AND b.dept_code=d.department_code AND  "
                    + " b.gpc=c.post_code AND a.cur_off_code=e.off_code AND emp_id=?");
            ps.setString(1, empId);
            rs = ps.executeQuery();
            if (rs.next()) {
                loanvalue.setEmpName(rs.getString("fullname"));
                String gpfno = rs.getString("gpf_no");
                loanvalue.setEmpSPC(rs.getString("spc"));
                String deptname = rs.getString("department_name");
                String accountNo = gpfno + " (" + deptname + ")";
                loanvalue.setGpfno(accountNo);
                int serviceYear = rs.getInt("age");
                int remainingAge = rs.getInt("remainingage");
                String gpftypestatus = null;
                if (serviceYear >= 20 || remainingAge < 10) {
                    gpftypestatus = "NON-REFUNDABLE";
                } else {
                    gpftypestatus = "REFUNDABLE";
                }
                loanvalue.setGpftype(gpftypestatus);
                String basicsalary_gp = rs.getString("cur_basic_salary") + "+ GP " + rs.getString("gp");
                loanvalue.setDesignation(rs.getString("post"));
                loanvalue.setPay(basicsalary_gp);
                loanvalue.setDoj(rs.getString("doj"));
                loanvalue.setSupperannuation(rs.getString("dos"));

                if (cmonth < 4) {
                    previousYear = cyear - 1;
                    cyear = previousYear;

                }
                if (cmonth == 0) {
                    cmonth = 12;
                }
                loanvalue.setCyear(cyear);

                loanvalue.setPreviousYear(previousYear);
                loanvalue.setCmonth(cmonth);
                String ddo_code = rs.getString("ddo_code");
                String ddo_office = rs.getString("off_en");
                String ddooffice = ddo_office + "(" + ddo_code + ")";
                loanvalue.setAccountOfficer(ddooffice);
                loanvalue.setDdocode(ddo_code);
                if (rstask.getString("disk_file_name") != null && !rstask.getString("disk_file_name").equals("")) {
                    loanvalue.setDiskFileName(rstask.getString("disk_file_name"));
                } else {
                    loanvalue.setDiskFileName("");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return loanvalue;

    }

    public void savegpfApproveLoanData(LoanGPFForm lform, String empid) {
        Connection con = null;
        Calendar now = Calendar.getInstance();
        Date loanapplyDate = now.getTime();

        PreparedStatement ps = null;
        ResultSet rs = null;
        Date dt1 = null;
        String pending_at = null;
        String pending_spc = null;
        int taskid = lform.getTaskid();
        int loanid = lform.getLoanId();
        int loanstatus = lform.getLoan_status();
        String Comments = lform.getLoancomments();
        String approvedby = lform.getApprovedBy();
        String approvedspc = lform.getApprovedSpc();

        DateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");

        MultipartFile loanAttch = lform.getFile_att();
        try {
            con = this.dataSource.getConnection();
            if (loanstatus == 39 || loanstatus == 37) {
                String forwardId = lform.getForwardtoHrmsid();
                String hidSpc = lform.getHidSPC();
                ps = con.prepareStatement(" SELECT * FROM task_master WHERE  task_id='" + taskid + "'");
                rs = ps.executeQuery();
                while (rs.next()) {
                    pending_at = (rs.getString("pending_at"));
                    pending_spc = (rs.getString("pending_spc"));
                }

                ps = con.prepareStatement("UPDATE task_master SET status_id=?,pending_at=?,note=?,apply_to=?,pending_spc=?,apply_to_spc=? WHERE task_id=?");
                ps.setInt(1, loanstatus);
                ps.setString(2, forwardId);
                ps.setString(3, Comments);
                ps.setString(4, forwardId);
                ps.setString(5, hidSpc);
                ps.setString(6, hidSpc);
                ps.setInt(7, taskid);
                int stsTask = ps.executeUpdate();
                if (stsTask == 1) {
                    String str = "INSERT INTO workflow_log(task_id "
                            + ",task_action_date "
                            + ",action_taken_by"
                            + ",spc_ontime"
                            + ",task_status_id"
                            + ",note"
                            + ",forward_to"
                            + ",forwarded_spc"
                            + ",log_id"
                            + ",ref_id"
                            + ") values(?,?,?,?,?,?,?,?,?,?)";
                    ps = con.prepareStatement(str);
                    ps.setInt(1, taskid);
                    ps.setTimestamp(2, new Timestamp(loanapplyDate.getTime()));
                    ps.setString(3, pending_at);
                    ps.setString(4, pending_spc);
                    ps.setInt(5, loanstatus);
                    ps.setString(6, Comments);
                    ps.setString(7, forwardId);
                    ps.setString(8, hidSpc);
                    ps.setInt(10, loanid);
                    int wfcode = CommonFunctions.getMaxCodeInteger("workflow_log", "log_id", con);
                    ps.setInt(9, wfcode);
                    ps.executeUpdate();

                    ps = con.prepareStatement("UPDATE hw_emp_gpf_loan SET loan_status=?,comments=? WHERE taskid=? AND loan_gpf_id=?");
                    ps.setInt(1, loanstatus);
                    ps.setString(2, Comments);
                    ps.setInt(3, taskid);
                    ps.setInt(4, loanid);
                    ps.executeUpdate();

                }

            } else {
                ps = con.prepareStatement("UPDATE hw_emp_gpf_loan SET loan_status=?,approved_by=?,approved_spc=?,comments=?,loan_sanction_date=? WHERE taskid=? AND loan_gpf_id=?");
                ps.setInt(1, loanstatus);
                ps.setString(2, approvedby);
                ps.setString(3, approvedspc);
                ps.setString(4, Comments);
                ps.setTimestamp(5, new Timestamp(loanapplyDate.getTime()));
                ps.setInt(6, taskid);
                ps.setInt(7, loanid);
                int stsTask = ps.executeUpdate();

                if (stsTask == 1) {
                    ps = con.prepareStatement("UPDATE task_master SET status_id=?,pending_at=?,note=?,apply_to=?,pending_spc=?,apply_to_spc=? WHERE task_id=?");
                    ps.setInt(1, loanstatus);
                    ps.setString(2, null);
                    ps.setString(3, Comments);
                    ps.setString(4, null);
                    ps.setString(5, null);
                    ps.setString(6, null);
                    ps.setInt(7, taskid);
                    ps.executeUpdate();
                }    //  System.out.println( taskid);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    public LoanGPFForm ReplygpfLoan(String option, String hrmsid, int loanid) {

        LoanGPFForm loanvalue = new LoanGPFForm();
        Connection con = null;
        PreparedStatement ps = null;
        PreparedStatement pstask = null;
        PreparedStatement psapply = null;
        ResultSet rs = null;
        ResultSet rstask = null;
        ResultSet rsapply = null;
        Calendar now = Calendar.getInstance();
        int cmonth = now.get(Calendar.MONTH);
        int cyear = now.get(Calendar.YEAR);
        int previousYear = cyear;

        try {
            con = this.dataSource.getConnection();
            pstask = con.prepareStatement("SELECT a.*,b.* FROM hw_emp_gpf_loan a,task_master b WHERE a.taskid=b.task_id AND a.loan_gpf_id='" + loanid + "' ");
            rstask = pstask.executeQuery();
            if (rstask.next()) {
                loanvalue.setClosingbalance(rstask.getString("closing_balance"));
                loanvalue.setBalanceCredit(rstask.getString("balancecredit"));
                loanvalue.setCreditForm(rstask.getString("credit_from"));
                loanvalue.setCreditTo(rstask.getString("credit_to"));
                loanvalue.setCreditAmount(rstask.getString("credit_amount"));
                loanvalue.setRefund(rstask.getString("refund_amount"));
                loanvalue.setWithdrawfrom(rstask.getString("withdrawal_from"));
                loanvalue.setWithdrawto(rstask.getString("withdrawal_to"));
                loanvalue.setWithdrawalAmount(rstask.getString("withdrawal_amount"));
                loanvalue.setNetbalance(rstask.getString("net_balance"));
                loanvalue.setWithdrawalreq(rstask.getString("amount_required"));
                loanvalue.setPurpose(rstask.getString("purpose"));
                loanvalue.setRequestcovered(rstask.getString("rule_gpf"));
                loanvalue.setWithdrawaltaken(rstask.getString("same_purpse_details"));
                loanvalue.setApprovedBy(rstask.getString("apply_to"));
                loanvalue.setApprovedSpc(rstask.getString("pending_spc"));
                loanvalue.setLoanId(rstask.getInt("loan_gpf_id"));
                loanvalue.setTaskid(rstask.getInt("taskid"));
                loanvalue.setLoancomments(rstask.getString("comments"));

            }

            ps = con.prepareStatement("SELECT a.gpf_no,a.gp,a.emp_id,ARRAY_TO_STRING(ARRAY[INITIALS, a.F_NAME, a.M_NAME,"
                    + "a.L_NAME], ' ') fullname,a.cur_basic_salary,to_char(a.JOINDATE_OF_GOO, 'DD-Mon-YYYY') as doj,"
                    + "to_char(a.dos, 'DD-Mon-YYYY') as dos,b.spn,b.spc,b.gpc,c.post,d.department_name,"
                    + "EXTRACT(YEAR from AGE(NOW(), a.JOINDATE_OF_GOO)) as age,EXTRACT(YEAR from AGE(a.dos,NOW())) as remainingage,e.ddo_code,e.off_en  FROM emp_mast a,g_spc b,g_post c,"
                    + "g_department d,g_office e WHERE  a.cur_spc=b.spc AND b.dept_code=d.department_code AND  "
                    + " b.gpc=c.post_code AND a.cur_off_code=e.off_code AND emp_id=?");
            ps.setString(1, hrmsid);
            rs = ps.executeQuery();
            if (rs.next()) {
                loanvalue.setEmpName(rs.getString("fullname"));
                String gpfno = rs.getString("gpf_no");
                loanvalue.setEmpSPC(rs.getString("spc"));
                String deptname = rs.getString("department_name");
                String accountNo = gpfno + " (" + deptname + ")";
                loanvalue.setGpfno(accountNo);
                int serviceYear = rs.getInt("age");
                int remainingAge = rs.getInt("remainingage");
                String gpftypestatus = null;
                if (serviceYear >= 20 || remainingAge < 10) {
                    gpftypestatus = "NON-REFUNDABLE";
                } else {
                    gpftypestatus = "REFUNDABLE";
                }
                loanvalue.setGpftype(gpftypestatus);
                String basicsalary_gp = rs.getString("cur_basic_salary") + "+ GP " + rs.getString("gp");
                loanvalue.setDesignation(rs.getString("post"));
                loanvalue.setPay(basicsalary_gp);
                loanvalue.setDoj(rs.getString("doj"));
                loanvalue.setSupperannuation(rs.getString("dos"));

                if (cmonth < 4) {
                    previousYear = cyear - 1;
                    cyear = previousYear;

                }
                if (cmonth == 0) {
                    cmonth = 12;
                }
                loanvalue.setCyear(cyear);

                loanvalue.setPreviousYear(previousYear);
                loanvalue.setCmonth(cmonth);
                String ddo_code = rs.getString("ddo_code");
                String ddo_office = rs.getString("off_en");
                String ddooffice = ddo_office + "(" + ddo_code + ")";
                loanvalue.setAccountOfficer(ddooffice);
                loanvalue.setDdocode(ddo_code);
                if (rstask.getString("disk_file_name") != null && !rstask.getString("disk_file_name").equals("")) {
                    loanvalue.setDiskFileName(rstask.getString("disk_file_name"));
                } else {
                    loanvalue.setDiskFileName("");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return loanvalue;

    }

    @Override
    public LoanGPFForm SactionGPFOrder(int taskid, int loanid) {

        LoanGPFForm loanvalue = new LoanGPFForm();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        loanvalue.setTaskid(taskid);
        loanvalue.setLoanId(loanid);
        String empId = null;
        try {
            con = this.dataSource.getConnection();
            ps = con.prepareStatement("SELECT a.* FROM hw_emp_gpf_loan a WHERE loan_gpf_id=?");
            ps.setInt(1, loanid);
            rs = ps.executeQuery();
            if (rs.next()) {
                loanvalue.setLetterNo(rs.getString("letterno"));
                loanvalue.setLetterDate(rs.getString("letterdate"));
                loanvalue.setLetterformName(rs.getString("letterfrom"));
                loanvalue.setLetterformdesignation(rs.getString("fromdesignation"));
                loanvalue.setLetterto(rs.getString("letterto"));
                loanvalue.setMemoNo(rs.getString("memono"));
                loanvalue.setMemoDate(rs.getString("memodate"));
                loanvalue.setLetterto(rs.getString("letterto"));
                loanvalue.setMemoNo(rs.getString("memono"));
                loanvalue.setMemoDate(rs.getString("memodate"));
                empId = rs.getString("emp_id");

                loanvalue.setClosingbalance(rs.getString("closing_balance"));
                loanvalue.setBalanceCredit(rs.getString("balancecredit"));
                loanvalue.setCreditForm(rs.getString("credit_from"));
                loanvalue.setCreditTo(rs.getString("credit_to"));
                loanvalue.setCreditAmount(rs.getString("credit_amount"));
                loanvalue.setRefund(rs.getString("refund_amount"));
                loanvalue.setWithdrawfrom(rs.getString("withdrawal_from"));
                loanvalue.setWithdrawto(rs.getString("withdrawal_to"));
                loanvalue.setWithdrawalAmount(rs.getString("withdrawal_amount"));
                loanvalue.setNetbalance(rs.getString("net_balance"));
                loanvalue.setWithdrawalreq(rs.getString("amount_required"));
                loanvalue.setPurpose(rs.getString("purpose"));
                loanvalue.setRequestcovered(rs.getString("rule_gpf"));
                loanvalue.setWithdrawaltaken(rs.getString("same_purpse_details"));
                loanvalue.setGpftype(rs.getString("gpftype"));

            }
            ps = con.prepareStatement("SELECT a.is_regular,a.gpf_no,a.gp,a.emp_id,ARRAY_TO_STRING(ARRAY[INITIALS, a.F_NAME, a.M_NAME,"
                    + "a.L_NAME], ' ') fullname,to_char(a.dob, 'DD-Mon-YYYY') as dob,a.cur_basic_salary,to_char(a.JOINDATE_OF_GOO, 'DD-Mon-YYYY') as doj,"
                    + "to_char(a.dos, 'DD-Mon-YYYY') as dos,b.spn,b.spc,b.gpc,c.post,d.department_name,"
                    + "EXTRACT(YEAR from AGE(NOW(), a.JOINDATE_OF_GOO)) as age,EXTRACT(YEAR from AGE(a.dos,NOW())) as remainingage,e.ddo_code,e.off_en  FROM emp_mast a,g_spc b,g_post c,"
                    + "g_department d,g_office e WHERE  a.cur_spc=b.spc AND b.dept_code=d.department_code AND  "
                    + " b.gpc=c.post_code AND a.cur_off_code=e.off_code AND emp_id=?");

            ps.setString(1, empId);
            rs = ps.executeQuery();
            if (rs.next()) {
                String Loanee_name = rs.getString("fullname");
                loanvalue.setEmpName(Loanee_name);

                String Loanee_des = rs.getString("post");
                loanvalue.setDesignation(Loanee_des);

                String loanee_basicsalary = rs.getString("cur_basic_salary");
                String loanee_gp = rs.getString("gp");

                String basic_gp = "RS " + loanee_basicsalary + "/-" + " " + "+" + "" + "RS " + loanee_gp + "/-" + " (G.P)";
                loanvalue.setPay(basic_gp);

                loanvalue.setGpfno(rs.getString("gpf_no"));
                loanvalue.setOfficeAddress(rs.getString("department_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }

        return loanvalue;
    }

    public void savegpfreapplyLoan(LoanGPFForm lform, String empid, String filepath) {
        Connection con = null;
        Calendar now = Calendar.getInstance();
        Date loanapplyDate = now.getTime();

        PreparedStatement ps = null;
        ResultSet rs = null;
        Date dt1 = null;
        Date dt2 = null;
        Date dt3 = null;

        DateFormat cdate = new SimpleDateFormat("dd-MMM-yyyy");
        MultipartFile loanAttch = lform.getFile_att();
        try {
            con = this.dataSource.getConnection();
            String forwardId = lform.getForwardtoHrmsid();
            String hidSpc = lform.getHidSPC();
            int loanvalue = 35;
            String initiatedSpc = lform.getEmpSPC();
            // String loanapplyfor=lform.getLoanapplyfor();
            String session_empId = empid;
            String gppftype = lform.getGpftype();
            String gppftype_value = gppftype.replace(",", "");

            String cbalnace = lform.getClosingbalance();
            String camount = lform.getCreditAmount();
            String ramount = lform.getRefund();
            String wamount = lform.getWithdrawalAmount();

            String nbalance = lform.getNetbalance();

            String amountrequired = lform.getWithdrawalreq();
            String purpose = lform.getPurpose();
            String rulegpf_request = lform.getRequestcovered();

            String samepurpose = lform.getWithdrawaltaken();
            String bcredit = lform.getBalanceCredit();
            int taskid = lform.getTaskid();
            int loanid = lform.getLoanId();
            String Comments = lform.getLoancomments();

            String strTaskmaster = "UPDATE   task_master SET status_id=?,pending_at=?,apply_to=?,pending_spc=?,apply_to_spc=?,initiated_on=?,note=? WHERE task_id=?";

            ps = con.prepareStatement(strTaskmaster);

            ps.setInt(1, loanvalue);
            ps.setString(2, forwardId);
            ps.setString(3, forwardId);

            ps.setString(4, hidSpc);
            ps.setString(5, hidSpc);
            ps.setTimestamp(6, new Timestamp(loanapplyDate.getTime()));
            ps.setString(7, "");
            ps.setInt(8, taskid);
            int stsTask = ps.executeUpdate();
            if (stsTask == 1) {

                String str = "UPDATE hw_emp_gpf_loan SET closing_balance=?,credit_amount=?,refund_amount=?,withdrawal_amount=?,net_balance=?,amount_required=?,purpose=?  ";
                str = str + ",rule_gpf=?,same_purpse_details=?,forward_to=?,loan_apply_date=?,loan_status=?,gpftype=?,balancecredit=?";
                str = str + ",comments=? WHERE loan_gpf_id=? ";
                ps = con.prepareStatement(str);
                if (cbalnace != null && !cbalnace.equals("")) {
                    ps.setDouble(1, Double.parseDouble(cbalnace));
                } else {
                    ps.setDouble(1, 0);
                }
                if (camount != null && !camount.equals("")) {
                    ps.setDouble(2, Double.parseDouble(camount));
                } else {
                    ps.setDouble(2, 0);
                }
                if (ramount != null && !ramount.equals("")) {
                    ps.setDouble(3, Double.parseDouble(ramount));
                } else {
                    ps.setDouble(3, 0);
                }
                if (wamount != null && !wamount.equals("")) {
                    ps.setDouble(4, Double.parseDouble(wamount));
                } else {
                    ps.setDouble(4, 0);
                }
                if (nbalance != null && !nbalance.equals("")) {
                    ps.setDouble(5, Double.parseDouble(nbalance));
                } else {
                    ps.setDouble(5, 0);
                }
                if (amountrequired != null && !amountrequired.equals("")) {
                    ps.setDouble(6, Double.parseDouble(amountrequired));
                } else {
                    ps.setDouble(6, 0);
                }
                ps.setString(7, purpose);
                ps.setString(8, rulegpf_request);
                ps.setString(9, samepurpose);
                ps.setString(10, hidSpc);
                ps.setTimestamp(11, new Timestamp(loanapplyDate.getTime()));
                ps.setInt(12, loanvalue);
                ps.setString(13, gppftype_value);
                if (bcredit != null && !bcredit.equals("")) {
                    ps.setDouble(14, Double.parseDouble(bcredit));
                } else {
                    ps.setDouble(14, 0);
                }
                ps.setString(15, Comments);
                ps.setInt(16, loanid);
                int sts = ps.executeUpdate();
                if (sts > 0) {
                    InputStream inputStream = null;
                    OutputStream outputStream = null;
                    String filename = "";
                    if (loanAttch != null && !loanAttch.isEmpty()) {
                        long time = System.currentTimeMillis();
                        filename = session_empId + "_" + taskid + "_" + time;

                        ps = con.prepareStatement("UPDATE hw_emp_gpf_loan SET original_filename=?,disk_file_name=?,file_type=? WHERE TASKID=?");
                        ps.setString(1, loanAttch.getOriginalFilename());
                        ps.setString(2, filename);
                        ps.setString(3, loanAttch.getContentType());
                        ps.setInt(4, taskid);
                        ps.executeUpdate();

                        String dirpath = filepath + "/";
                        File newfile = new File(dirpath);
                        if (!newfile.exists()) {
                            newfile.mkdirs();
                        }

                        outputStream = new FileOutputStream(dirpath + filename);
                        int read = 0;
                        byte[] bytes = new byte[1024];
                        inputStream = loanAttch.getInputStream();
                        while ((read = inputStream.read(bytes)) != -1) {
                            outputStream.write(bytes, 0, read);
                        }
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }

    }

    @Override
    public LoanHBAForm HBAEmpDetails(String hrmsid) {
        LoanHBAForm loanvalue = new LoanHBAForm();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Calendar now = Calendar.getInstance();
        int cmonth = now.get(Calendar.MONTH);
        int cyear = now.get(Calendar.YEAR);
        int previousYear = cyear;
        try {
            con = this.dataSource.getConnection();
            ps = con.prepareStatement("SELECT a.gpf_no,a.gp,a.emp_id,ARRAY_TO_STRING(ARRAY[INITIALS, a.F_NAME, a.M_NAME,"
                    + "a.L_NAME], ' ') fullname,a.cur_basic_salary,to_char(a.dob, 'DD-Mon-YYYY') as dob, to_char(a.dob+ cast('1 Year' as interval),'DD-Mon-YYYY')  as nextdob,a.cur_salary,to_char(a.JOINDATE_OF_GOO, 'DD-Mon-YYYY') as doj,"
                    + "to_char(a.dos, 'DD-Mon-YYYY') as dos,b.spn,b.spc,b.gpc,c.post,d.department_name,"
                    + "EXTRACT(YEAR from AGE(NOW(), a.JOINDATE_OF_GOO)) as age,EXTRACT(YEAR from AGE(a.dos,NOW())) as remainingage,e.ddo_code,e.off_en  FROM emp_mast a,g_spc b,g_post c,"
                    + "g_department d,g_office e WHERE  a.cur_spc=b.spc AND b.dept_code=d.department_code AND  "
                    + " b.gpc=c.post_code AND a.cur_off_code=e.off_code AND emp_id=?");
            ps.setString(1, hrmsid);
            rs = ps.executeQuery();
            if (rs.next()) {
                loanvalue.setEmpName(rs.getString("fullname"));
                String basicsalary_gp = rs.getString("cur_basic_salary") + "+ GP " + rs.getString("gp");
                loanvalue.setDesignation(rs.getString("post"));

                loanvalue.setDoj(rs.getString("doj"));
                loanvalue.setStationposted(rs.getString("off_en"));
                loanvalue.setDepartment(rs.getString("department_name"));
                loanvalue.setCursalary(rs.getString("cur_salary"));
                loanvalue.setCurbasicsalary(rs.getString("cur_basic_salary"));
                loanvalue.setEmpSPC(rs.getString("spc"));
                loanvalue.setNetPay(getNetPay(hrmsid, cmonth, cyear) + "");
                loanvalue.setDob(rs.getString("dob"));
                loanvalue.setSuperannuation(rs.getString("dos"));
                loanvalue.setNdob(rs.getString("nextdob"));

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return loanvalue;
    }

    @Override
    public void saveHBALoanData(LoanHBAForm lform, String empid, String filepath) {
        Connection con = null;
        Calendar now = Calendar.getInstance();
        Date loanapplyDate = now.getTime();

        PreparedStatement ps = null;
        ResultSet rs = null;
        Date dt1 = null;
        Date dt2 = null;
        Date dt3 = null;

        DateFormat cdate = new SimpleDateFormat("dd-MMM-yyyy");
        MultipartFile loanAttch = lform.getFile_att();
        MultipartFile loanAttch1 = lform.getFile_att1();
        try {
            con = this.dataSource.getConnection();
            String forwardId = lform.getForwardtoHrmsid();
            String hidSpc = lform.getHidSPC();
            int loanvalue = 43;
            String initiatedSpc = lform.getEmpSPC();
            String session_empId = empid;
            String jobtype = lform.getJobtype();
            String per_post = lform.getPer_post();
            String per_appointment = lform.getPer_appointment();
            String other_govt_ser = lform.getOther_govt_ser();
            String address = lform.getAddress();
            String floor_area = lform.getFloor_area();
            String approx_valuation = lform.getApprox_valuation();
            String reason = lform.getReason();
            String constructed_area = lform.getConstructed_area();
            String cost_land = lform.getCost_land();
            String cost_building = lform.getCost_building();
            String total_amount = lform.getTotal_amount();
            String amount_adv = lform.getAmount_adv();
            String noofYear = lform.getNoofYear();
            String city_name = lform.getCity_name();
            String settle_retir = lform.getSettle_retir();
            String area_plot = lform.getArea_plot();
            String localauthority = lform.getLocalauthority();
            String propose_acquire = lform.getPropose_acquire();
            String no_rooms = lform.getNo_rooms();
            String total_floor = lform.getTotal_floor();
            String additional_storey = lform.getAdditional_storey();
            String addition_room = lform.getAddition_room();
            String addition_farea = lform.getAddition_farea();
            String est_cost = lform.getEst_cost();
            String amount_desired = lform.getAmount_desired();
            String year_repaid = lform.getYear_repaid();
            String exact_location = lform.getExact_location();
            String exact_floor_area = lform.getExact_floor_area();
            String plinth_area = lform.getPlinth_area();
            String total_land_cost = lform.getTotal_land_cost();
            String parties_name_address = lform.getParties_name_address();
            String repay_adv_amount = lform.getRepay_adv_amount();
            String repay_year = lform.getRepay_year();
            String readymade_exact_loc = lform.getReadymade_exact_loc();
            String readymade_floor_area = lform.getReadymade_floor_area();
            String readymade_plinth_area = lform.getReadymade_plinth_area();
            String house_age = lform.getHouse_age();
            String valuation_price = lform.getValuation_price();
            String owner_name = lform.getOwner_name();
            String readymade_appro_amount = lform.getReadymade_appro_amount();
            String readymade_adv_amount = lform.getReadymade_adv_amount();
            String readymade_year = lform.getReadymade_year();
            String propose_amount = lform.getPropose_amount();
            String propose_adv = lform.getPropose_adv();
            String propose_repaid = lform.getPropose_repaid();
            String term_lease = lform.getTerm_lease();
            String term_expired = lform.getTerm_expired();
            String lease_condition = lform.getLease_condition();
            String premimum_paid = lform.getPremimum_paid();
            String annual_rent = lform.getAnnual_rent();
            String encumb_status = lform.getEncumb_status();
            String loanpurpose = lform.getLoanpurpose();

            int mcode = CommonFunctions.getMaxCodeInteger("TASK_MASTER", "TASK_ID", con);
            String strTaskmaster = "INSERT INTO task_master (task_id "
                    + ",process_id "
                    + ",initiated_by "
                    + ",status_id "
                    + ",pending_at "
                    + ",apply_to "
                    + ",initiated_spc "
                    + ",pending_spc "
                    + ",apply_to_spc "
                    + ",initiated_on "
                    + ") values(?,?,?,?,?,?,?,?,?,?)";
            ps = con.prepareStatement(strTaskmaster);

            ps.setInt(1, mcode);
            ps.setInt(2, 9);
            ps.setString(3, session_empId);
            ps.setInt(4, loanvalue);
            ps.setString(5, forwardId);
            ps.setString(6, forwardId);
            ps.setString(7, initiatedSpc);
            ps.setString(8, hidSpc);
            ps.setString(9, hidSpc);
            ps.setTimestamp(10, new Timestamp(loanapplyDate.getTime()));

            int stsTask = ps.executeUpdate();

            if (stsTask == 1) {
                String str = "INSERT INTO hw_emp_hba_loan(taskid "
                        + ",emp_id "
                        + ",jobtype"
                        + ",per_post"
                        + ",per_appointment"
                        + ",other_govt_ser"
                        + ",address"
                        + ",floor_area"
                        + ",approx_valuation"
                        + ",reason"
                        + ",constructed_area"
                        + ",cost_land"
                        + ",cost_building"
                        + ",total_amount"
                        + ",amount_adv"
                        + ",noofyear"
                        + ",city_name"
                        + ",settle_retir"
                        + ",area_plot "
                        + ",localauthority"
                        + ",propose_acquire"
                        + ",no_rooms"
                        + ",total_floor"
                        + ",additional_storey"
                        + ",addition_room"
                        + ",addition_farea"
                        + ",est_cost"
                        + ",amount_desired"
                        + ",year_repaid"
                        + ",exact_location"
                        + ",exact_floor_area"
                        + ",plinth_area"
                        + ",total_land_cost"
                        + ",parties_name_address"
                        + ",repay_adv_amount"
                        + ",repay_year "
                        + ",readymade_exact_loc"
                        + ",readymade_floor_area"
                        + ",readymade_plinth_area"
                        + ",house_age"
                        + ",valuation_price"
                        + ",owner_name"
                        + ",readymade_appro_amount"
                        + ",readymade_adv_amount"
                        + ",readymade_year"
                        + ",propose_amount"
                        + ",propose_adv"
                        + ",propose_repaid"
                        + ",term_lease"
                        + ",term_expired"
                        + ",lease_condition"
                        + ",premimum_paid"
                        + ",annual_rent"
                        + ",encumb_status"
                        + ",forward_to"
                        + ",loan_status,loan_apply_date,purpose) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                ps = con.prepareStatement(str);
                ps.setInt(1, mcode);
                ps.setString(2, session_empId);
                ps.setString(3, jobtype);
                ps.setString(4, per_post);
                ps.setString(5, per_appointment);

                ps.setString(6, other_govt_ser);
                ps.setString(7, address);
                ps.setString(8, floor_area);
                ps.setString(9, approx_valuation);
                ps.setString(10, reason);
                ps.setString(11, constructed_area);
                ps.setString(12, cost_land);
                ps.setString(13, cost_building);
                ps.setString(14, total_amount);
                ps.setString(15, amount_adv);
                ps.setString(16, noofYear);
                ps.setString(17, city_name);
                ps.setString(18, settle_retir);
                ps.setString(19, area_plot);
                ps.setString(20, localauthority);
                ps.setString(21, propose_acquire);
                ps.setString(22, no_rooms);
                ps.setString(23, total_floor);
                ps.setString(24, additional_storey);
                ps.setString(25, addition_room);
                ps.setString(26, addition_farea);
                ps.setString(27, est_cost);
                ps.setString(28, amount_desired);
                ps.setString(29, year_repaid);
                ps.setString(30, exact_location);
                ps.setString(31, exact_floor_area);
                ps.setString(32, plinth_area);
                ps.setString(33, total_land_cost);
                ps.setString(34, parties_name_address);
                ps.setString(35, repay_adv_amount);
                ps.setString(36, repay_year);
                ps.setString(37, readymade_exact_loc);
                ps.setString(38, readymade_floor_area);
                ps.setString(39, readymade_plinth_area);
                ps.setString(40, house_age);
                ps.setString(41, valuation_price);
                ps.setString(42, owner_name);
                ps.setString(43, readymade_appro_amount);
                ps.setString(44, readymade_adv_amount);
                ps.setString(45, readymade_year);
                ps.setString(46, propose_amount);
                ps.setString(47, propose_adv);
                ps.setString(48, propose_repaid);
                ps.setString(49, term_lease);
                ps.setString(50, term_expired);
                ps.setString(51, lease_condition);
                ps.setString(52, premimum_paid);
                ps.setString(53, annual_rent);
                ps.setString(54, encumb_status);
                ps.setString(55, hidSpc);
                ps.setInt(56, loanvalue);
                ps.setTimestamp(57, new Timestamp(loanapplyDate.getTime()));
                ps.setString(58, loanpurpose);
                int sts = ps.executeUpdate();
                if (sts > 0) {
                    InputStream inputStream = null;
                    OutputStream outputStream = null;
                    String filename = "";
                    if (loanAttch != null && !loanAttch.isEmpty()) {
                        long time = System.currentTimeMillis();
                        filename = session_empId + "_1_" + mcode + "_" + time;

                        ps = con.prepareStatement("UPDATE hw_emp_hba_loan SET original_filename=?,disk_file_name=?,file_type=? WHERE TASKID=?");
                        ps.setString(1, loanAttch.getOriginalFilename());
                        ps.setString(2, filename);
                        ps.setString(3, loanAttch.getContentType());
                        ps.setInt(4, mcode);
                        ps.executeUpdate();

                        String dirpath = filepath + "/attachment/";
                        File newfile = new File(dirpath);
                        if (!newfile.exists()) {
                            newfile.mkdirs();
                        }

                        outputStream = new FileOutputStream(dirpath + filename);
                        int read = 0;
                        byte[] bytes = new byte[1024];
                        inputStream = loanAttch.getInputStream();
                        while ((read = inputStream.read(bytes)) != -1) {
                            outputStream.write(bytes, 0, read);
                        }
                    }

                    InputStream inputStream1 = null;
                    OutputStream outputStream1 = null;
                    String filename1 = "";
                    if (loanAttch1 != null && !loanAttch1.isEmpty()) {
                        long time = System.currentTimeMillis();
                        filename1 = session_empId + "_2_" + mcode + "_" + time;

                        ps = con.prepareStatement("UPDATE hw_emp_hba_loan SET original_filename1=?,disk_file_name1=?,file_type1=? WHERE TASKID=?");
                        ps.setString(1, loanAttch1.getOriginalFilename());
                        ps.setString(2, filename1);
                        ps.setString(3, loanAttch1.getContentType());
                        ps.setInt(4, mcode);
                        ps.executeUpdate();

                        String dirpath = filepath + "/attachment1/";
                        File newfile = new File(dirpath);
                        if (!newfile.exists()) {
                            newfile.mkdirs();
                        }

                        outputStream1 = new FileOutputStream(dirpath + filename1);
                        int read = 0;
                        byte[] bytes = new byte[1024];
                        inputStream1 = loanAttch.getInputStream();
                        while ((read = inputStream.read(bytes)) != -1) {
                            outputStream1.write(bytes, 0, read);
                        }
                    }

                }

            }

            //int mcode = CommonFunctions.getMaxCodeInteger("TASK_MASTER", "TASK_ID", con);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    @Override
    public LoanHBAForm gethbaLoanDetails(int taskid) {

        LoanHBAForm loanvalue = new LoanHBAForm();
        Connection con = null;
        PreparedStatement ps = null;
        PreparedStatement pstask = null;
        PreparedStatement psapply = null;
        ResultSet rs = null;
        ResultSet rstask = null;
        ResultSet rsapply = null;
        Calendar now = Calendar.getInstance();
        int cmonth = now.get(Calendar.MONTH);
        int cyear = now.get(Calendar.YEAR);
        int previousYear = cyear;
        String hrmsid = null;

        try {
            con = this.dataSource.getConnection();
            pstask = con.prepareStatement("SELECT a.*,b.* FROM hw_emp_hba_loan a,task_master b WHERE a.taskid=b.task_id AND a.taskid='" + taskid + "' ");
            rstask = pstask.executeQuery();
            if (rstask.next()) {
                hrmsid = rstask.getString("emp_id");
                loanvalue.setApprovedBy(rstask.getString("apply_to"));
                loanvalue.setApprovedSpc(rstask.getString("pending_spc"));
                loanvalue.setLoanId(rstask.getInt("loan_hba_id"));
                loanvalue.setTaskid(rstask.getInt("taskid"));
                loanvalue.setLoancomments(rstask.getString("comments"));
                loanvalue.setJobtype(rstask.getString("jobtype"));
                loanvalue.setPer_post(rstask.getString("per_post"));
                loanvalue.setPer_appointment(rstask.getString("per_appointment"));
                loanvalue.setOther_govt_ser(rstask.getString("other_govt_ser"));
                loanvalue.setAddress(rstask.getString("address"));
                loanvalue.setFloor_area(rstask.getString("floor_area"));
                loanvalue.setApprox_valuation(rstask.getString("approx_valuation"));
                loanvalue.setReason(rstask.getString("reason"));
                loanvalue.setConstructed_area(rstask.getString("constructed_area"));
                loanvalue.setCost_land(rstask.getString("cost_land"));
                loanvalue.setCost_building(rstask.getString("cost_building"));
                loanvalue.setTotal_amount(rstask.getString("total_amount"));

                loanvalue.setAmount_adv(rstask.getString("amount_adv"));
                loanvalue.setNoofYear(rstask.getString("noofyear"));
                loanvalue.setCity_name(rstask.getString("city_name"));
                loanvalue.setSettle_retir(rstask.getString("settle_retir"));
                loanvalue.setArea_plot(rstask.getString("area_plot"));
                loanvalue.setLocalauthority(rstask.getString("localauthority"));
                loanvalue.setPropose_acquire(rstask.getString("propose_acquire"));
                loanvalue.setNo_rooms(rstask.getString("no_rooms"));
                loanvalue.setTotal_floor(rstask.getString("total_floor"));
                loanvalue.setAdditional_storey(rstask.getString("additional_storey"));

                loanvalue.setAddition_room(rstask.getString("addition_room"));
                loanvalue.setAddition_farea(rstask.getString("addition_farea"));
                loanvalue.setEst_cost(rstask.getString("est_cost"));
                loanvalue.setAmount_desired(rstask.getString("amount_desired"));
                loanvalue.setYear_repaid(rstask.getString("year_repaid"));
                loanvalue.setExact_location(rstask.getString("exact_location"));
                loanvalue.setExact_floor_area(rstask.getString("exact_floor_area"));
                loanvalue.setPlinth_area(rstask.getString("plinth_area"));
                loanvalue.setTotal_land_cost(rstask.getString("total_land_cost"));
                loanvalue.setParties_name_address(rstask.getString("parties_name_address"));

                loanvalue.setRepay_adv_amount(rstask.getString("repay_adv_amount"));
                loanvalue.setRepay_year(rstask.getString("repay_year"));
                loanvalue.setReadymade_exact_loc(rstask.getString("readymade_exact_loc"));
                loanvalue.setReadymade_floor_area(rstask.getString("readymade_floor_area"));
                loanvalue.setReadymade_plinth_area(rstask.getString("readymade_plinth_area"));
                loanvalue.setHouse_age(rstask.getString("house_age"));
                loanvalue.setValuation_price(rstask.getString("valuation_price"));
                loanvalue.setOwner_name(rstask.getString("owner_name"));
                loanvalue.setReadymade_appro_amount(rstask.getString("readymade_appro_amount"));
                loanvalue.setReadymade_adv_amount(rstask.getString("readymade_adv_amount"));

                loanvalue.setReadymade_year(rstask.getString("readymade_year"));
                loanvalue.setPropose_amount(rstask.getString("propose_amount"));
                loanvalue.setPropose_adv(rstask.getString("propose_adv"));
                loanvalue.setPropose_repaid(rstask.getString("propose_repaid"));
                loanvalue.setTerm_lease(rstask.getString("term_lease"));
                loanvalue.setTerm_expired(rstask.getString("term_expired"));
                loanvalue.setLease_condition(rstask.getString("lease_condition"));
                loanvalue.setPremimum_paid(rstask.getString("premimum_paid"));
                loanvalue.setAnnual_rent(rstask.getString("annual_rent"));
                loanvalue.setEncumb_status(rstask.getString("encumb_status"));
                loanvalue.setDiskFileName(rstask.getString("disk_file_name"));
                loanvalue.setDiskFileName1(rstask.getString("disk_file_name1"));
                loanvalue.setStatusId(rstask.getInt("status_id"));

            }
            ps = con.prepareStatement("SELECT a.gpf_no,a.gp,a.emp_id,ARRAY_TO_STRING(ARRAY[INITIALS, a.F_NAME, a.M_NAME,"
                    + "a.L_NAME], ' ') fullname,a.cur_basic_salary,to_char(a.dob, 'DD-Mon-YYYY') as dob, to_char(a.dob+ cast('1 Year' as interval),'DD-Mon-YYYY')  as nextdob,a.cur_salary,to_char(a.JOINDATE_OF_GOO, 'DD-Mon-YYYY') as doj,"
                    + "to_char(a.dos, 'DD-Mon-YYYY') as dos,b.spn,b.spc,b.gpc,c.post,d.department_name,"
                    + "EXTRACT(YEAR from AGE(NOW(), a.JOINDATE_OF_GOO)) as age,EXTRACT(YEAR from AGE(a.dos,NOW())) as remainingage,e.ddo_code,e.off_en  FROM emp_mast a,g_spc b,g_post c,"
                    + "g_department d,g_office e WHERE  a.cur_spc=b.spc AND b.dept_code=d.department_code AND  "
                    + " b.gpc=c.post_code AND a.cur_off_code=e.off_code AND emp_id=?");
            ps.setString(1, hrmsid);
            rs = ps.executeQuery();
            if (rs.next()) {
                loanvalue.setEmpName(rs.getString("fullname"));
                String basicsalary_gp = rs.getString("cur_basic_salary") + "+ GP " + rs.getString("gp");
                loanvalue.setDesignation(rs.getString("post"));

                loanvalue.setDoj(rs.getString("doj"));
                loanvalue.setStationposted(rs.getString("off_en"));
                loanvalue.setDepartment(rs.getString("department_name"));
                loanvalue.setCursalary(rs.getString("cur_salary"));
                loanvalue.setCurbasicsalary(rs.getString("cur_basic_salary"));
                loanvalue.setEmpSPC(rs.getString("spc"));
                loanvalue.setNetPay(getNetPay(hrmsid, cmonth, cyear) + "");
                loanvalue.setDob(rs.getString("dob"));
                loanvalue.setSuperannuation(rs.getString("dos"));
                loanvalue.setNdob(rs.getString("nextdob"));

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return loanvalue;

    }

    @Override
    public void downloadHBALoanAttachment(HttpServletResponse response, String filepath, String loanid, String attchmentId) throws IOException {

        Connection con = null;

        Statement stmt = null;
        ResultSet rs = null;
        int BUFFER_LENGTH = 4096;
        OutputStream out = response.getOutputStream();
        try {
            con = dataSource.getConnection();

            stmt = con.createStatement();
            String sql = "SELECT * FROM hw_emp_hba_loan WHERE loan_hba_id='" + loanid + "'";
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                File f = null;
                String dirpath = null;
                System.out.println("dirpath is: " + attchmentId + "dfsd");
                if (attchmentId.equals("1")) {
                    dirpath = filepath + "/attachment/" + rs.getString("DISK_FILE_NAME");
                    System.out.println("dirpath is: " + dirpath);
                } else {
                    dirpath = filepath + "/attachment1/" + rs.getString("DISK_FILE_NAME1");
                    System.out.println("dirpath is: " + dirpath);
                }

                f = new File(dirpath);
                if (f.exists()) {
                    String originalFilename = null;
                    String filetype = null;
                    if (attchmentId.equals("1")) {
                        originalFilename = rs.getString("ORIGINAL_FILENAME");
                        filetype = rs.getString("FILE_TYPE");
                    } else {
                        originalFilename = rs.getString("ORIGINAL_FILENAME1");
                        filetype = rs.getString("FILE_TYPE1");
                    }

                    response.setContentLength((int) f.length());
                    FileInputStream is = new FileInputStream(f);

                    response.setContentType(filetype);
                    response.setHeader("Content-Disposition", "attachment; filename=\"" + originalFilename + "\"");

                    byte[] bytes = new byte[BUFFER_LENGTH];
                    int read = 0;
                    while ((read = is.read(bytes, 0, BUFFER_LENGTH)) != -1) {
                        out.write(bytes, 0, read);
                    }
                    is.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, stmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    public void savehbaApproveLoanData(LoanHBAForm lform, String empid) {
        Connection con = null;
        Calendar now = Calendar.getInstance();
        Date loanapplyDate = now.getTime();

        PreparedStatement ps = null;
        ResultSet rs = null;
        Date dt1 = null;
        String pending_at = null;
        String pending_spc = null;
        int taskid = lform.getTaskid();
        int loanid = lform.getLoanId();
        int loanstatus = lform.getLoan_status();
        String Comments = lform.getLoancomments();
        String approvedby = lform.getApprovedBy();
        String approvedspc = lform.getApprovedSpc();

        DateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");

        MultipartFile loanAttch = lform.getFile_att();
        try {
            con = this.dataSource.getConnection();
            if (loanstatus == 45 || loanstatus == 47) {
                String forwardId = lform.getForwardtoHrmsid();
                String hidSpc = lform.getHidSPC();
                ps = con.prepareStatement(" SELECT * FROM task_master WHERE  task_id='" + taskid + "'");
                rs = ps.executeQuery();
                while (rs.next()) {
                    pending_at = (rs.getString("pending_at"));
                    pending_spc = (rs.getString("pending_spc"));
                }

                ps = con.prepareStatement("UPDATE task_master SET status_id=?,pending_at=?,note=?,apply_to=?,pending_spc=?,apply_to_spc=? WHERE task_id=?");
                ps.setInt(1, loanstatus);
                ps.setString(2, forwardId);
                ps.setString(3, Comments);
                ps.setString(4, forwardId);
                ps.setString(5, hidSpc);
                ps.setString(6, hidSpc);
                ps.setInt(7, taskid);
                int stsTask = ps.executeUpdate();
                if (stsTask == 1) {
                    String str = "INSERT INTO workflow_log(task_id "
                            + ",task_action_date "
                            + ",action_taken_by"
                            + ",spc_ontime"
                            + ",task_status_id"
                            + ",note"
                            + ",forward_to"
                            + ",forwarded_spc"
                            + ",log_id"
                            + ",ref_id"
                            + ") values(?,?,?,?,?,?,?,?,?,?)";
                    ps = con.prepareStatement(str);
                    ps.setInt(1, taskid);
                    ps.setTimestamp(2, new Timestamp(loanapplyDate.getTime()));
                    ps.setString(3, pending_at);
                    ps.setString(4, pending_spc);
                    ps.setInt(5, loanstatus);
                    ps.setString(6, Comments);
                    ps.setString(7, forwardId);
                    ps.setString(8, hidSpc);
                    ps.setInt(10, loanid);
                    int wfcode = CommonFunctions.getMaxCodeInteger("workflow_log", "log_id", con);
                    ps.setInt(9, wfcode);
                    ps.executeUpdate();

                    ps = con.prepareStatement("UPDATE hw_emp_hba_loan SET loan_status=?,comments=? WHERE taskid=? AND loan_hba_id=?");
                    ps.setInt(1, loanstatus);
                    ps.setString(2, Comments);
                    ps.setInt(3, taskid);
                    ps.setInt(4, loanid);
                    ps.executeUpdate();

                }

            } else {
                ps = con.prepareStatement("UPDATE hw_emp_hba_loan SET loan_status=?,approved_by=?,approved_spc=?,comments=?,loan_sanction_date=? WHERE taskid=? AND loan_hba_id=?");
                ps.setInt(1, loanstatus);
                ps.setString(2, approvedby);
                ps.setString(3, approvedspc);
                ps.setString(4, Comments);
                ps.setTimestamp(5, new Timestamp(loanapplyDate.getTime()));
                ps.setInt(6, taskid);
                ps.setInt(7, loanid);
                int stsTask = ps.executeUpdate();

                if (stsTask == 1) {
                    ps = con.prepareStatement("UPDATE task_master SET status_id=?,pending_at=?,note=?,apply_to=?,pending_spc=?,apply_to_spc=? WHERE task_id=?");
                    ps.setInt(1, loanstatus);
                    ps.setString(2, null);
                    ps.setString(3, Comments);
                    ps.setString(4, null);
                    ps.setString(5, null);
                    ps.setString(6, null);
                    ps.setInt(7, taskid);
                    ps.executeUpdate();
                }    //  System.out.println( taskid);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    public LoanHBAForm ReplyhbaLoan(String option, String hrmsid, int loanid) {

        LoanHBAForm loanvalue = new LoanHBAForm();
        Connection con = null;
        PreparedStatement ps = null;
        PreparedStatement pstask = null;
        PreparedStatement psapply = null;
        ResultSet rs = null;
        ResultSet rstask = null;
        ResultSet rsapply = null;
        Calendar now = Calendar.getInstance();
        int cmonth = now.get(Calendar.MONTH);
        int cyear = now.get(Calendar.YEAR);
        int previousYear = cyear;

        try {
            con = this.dataSource.getConnection();
            ps = con.prepareStatement("SELECT a.gpf_no,a.gp,a.emp_id,ARRAY_TO_STRING(ARRAY[INITIALS, a.F_NAME, a.M_NAME,"
                    + "a.L_NAME], ' ') fullname,a.cur_basic_salary,to_char(a.dob, 'DD-Mon-YYYY') as dob, to_char(a.dob+ cast('1 Year' as interval),'DD-Mon-YYYY')  as nextdob,a.cur_salary,to_char(a.JOINDATE_OF_GOO, 'DD-Mon-YYYY') as doj,"
                    + "to_char(a.dos, 'DD-Mon-YYYY') as dos,b.spn,b.spc,b.gpc,c.post,d.department_name,"
                    + "EXTRACT(YEAR from AGE(NOW(), a.JOINDATE_OF_GOO)) as age,EXTRACT(YEAR from AGE(a.dos,NOW())) as remainingage,e.ddo_code,e.off_en  FROM emp_mast a,g_spc b,g_post c,"
                    + "g_department d,g_office e WHERE  a.cur_spc=b.spc AND b.dept_code=d.department_code AND  "
                    + " b.gpc=c.post_code AND a.cur_off_code=e.off_code AND emp_id=?");
            ps.setString(1, hrmsid);
            rs = ps.executeQuery();
            if (rs.next()) {
                loanvalue.setEmpName(rs.getString("fullname"));
                String basicsalary_gp = rs.getString("cur_basic_salary") + "+ GP " + rs.getString("gp");
                loanvalue.setDesignation(rs.getString("post"));

                loanvalue.setDoj(rs.getString("doj"));
                loanvalue.setStationposted(rs.getString("off_en"));
                loanvalue.setDepartment(rs.getString("department_name"));
                loanvalue.setCursalary(rs.getString("cur_salary"));
                loanvalue.setCurbasicsalary(rs.getString("cur_basic_salary"));
                loanvalue.setEmpSPC(rs.getString("spc"));
                loanvalue.setNetPay(getNetPay(hrmsid, cmonth, cyear) + "");
                loanvalue.setDob(rs.getString("dob"));
                loanvalue.setSuperannuation(rs.getString("dos"));
                loanvalue.setNdob(rs.getString("nextdob"));

            }
            pstask = con.prepareStatement("SELECT a.*,b.* FROM hw_emp_hba_loan a,task_master b WHERE a.taskid=b.task_id AND a.loan_hba_id='" + loanid + "' ");
            rstask = pstask.executeQuery();
            if (rstask.next()) {
                loanvalue.setApprovedBy(rstask.getString("apply_to"));
                loanvalue.setApprovedSpc(rstask.getString("pending_spc"));
                loanvalue.setLoanId(rstask.getInt("loan_hba_id"));
                loanvalue.setTaskid(rstask.getInt("taskid"));
                loanvalue.setLoancomments(rstask.getString("comments"));
                loanvalue.setJobtype(rstask.getString("jobtype"));
                loanvalue.setPer_post(rstask.getString("per_post"));
                loanvalue.setPer_appointment(rstask.getString("per_appointment"));
                loanvalue.setOther_govt_ser(rstask.getString("other_govt_ser"));
                loanvalue.setAddress(rstask.getString("address"));
                loanvalue.setFloor_area(rstask.getString("floor_area"));
                loanvalue.setApprox_valuation(rstask.getString("approx_valuation"));
                loanvalue.setReason(rstask.getString("reason"));
                loanvalue.setConstructed_area(rstask.getString("constructed_area"));
                loanvalue.setCost_land(rstask.getString("cost_land"));
                loanvalue.setCost_building(rstask.getString("cost_building"));
                loanvalue.setTotal_amount(rstask.getString("total_amount"));

                loanvalue.setAmount_adv(rstask.getString("amount_adv"));
                loanvalue.setNoofYear(rstask.getString("noofyear"));
                loanvalue.setCity_name(rstask.getString("city_name"));
                loanvalue.setSettle_retir(rstask.getString("settle_retir"));
                loanvalue.setArea_plot(rstask.getString("area_plot"));
                loanvalue.setLocalauthority(rstask.getString("localauthority"));
                loanvalue.setPropose_acquire(rstask.getString("propose_acquire"));
                loanvalue.setNo_rooms(rstask.getString("no_rooms"));
                loanvalue.setTotal_floor(rstask.getString("total_floor"));
                loanvalue.setAdditional_storey(rstask.getString("additional_storey"));

                loanvalue.setAddition_room(rstask.getString("addition_room"));
                loanvalue.setAddition_farea(rstask.getString("addition_farea"));
                loanvalue.setEst_cost(rstask.getString("est_cost"));
                loanvalue.setAmount_desired(rstask.getString("amount_desired"));
                loanvalue.setYear_repaid(rstask.getString("year_repaid"));
                loanvalue.setExact_location(rstask.getString("exact_location"));
                loanvalue.setExact_floor_area(rstask.getString("exact_floor_area"));
                loanvalue.setPlinth_area(rstask.getString("plinth_area"));
                loanvalue.setTotal_land_cost(rstask.getString("total_land_cost"));
                loanvalue.setParties_name_address(rstask.getString("parties_name_address"));

                loanvalue.setRepay_adv_amount(rstask.getString("repay_adv_amount"));
                loanvalue.setRepay_year(rstask.getString("repay_year"));
                loanvalue.setReadymade_exact_loc(rstask.getString("readymade_exact_loc"));
                loanvalue.setReadymade_floor_area(rstask.getString("readymade_floor_area"));
                loanvalue.setReadymade_plinth_area(rstask.getString("readymade_plinth_area"));
                loanvalue.setHouse_age(rstask.getString("house_age"));
                loanvalue.setValuation_price(rstask.getString("valuation_price"));
                loanvalue.setOwner_name(rstask.getString("owner_name"));
                loanvalue.setReadymade_appro_amount(rstask.getString("readymade_appro_amount"));
                loanvalue.setReadymade_adv_amount(rstask.getString("readymade_adv_amount"));

                loanvalue.setReadymade_year(rstask.getString("readymade_year"));
                loanvalue.setPropose_amount(rstask.getString("propose_amount"));
                loanvalue.setPropose_adv(rstask.getString("propose_adv"));
                loanvalue.setPropose_repaid(rstask.getString("propose_repaid"));
                loanvalue.setTerm_lease(rstask.getString("term_lease"));
                loanvalue.setTerm_expired(rstask.getString("term_expired"));
                loanvalue.setLease_condition(rstask.getString("lease_condition"));
                loanvalue.setPremimum_paid(rstask.getString("premimum_paid"));
                loanvalue.setAnnual_rent(rstask.getString("annual_rent"));
                loanvalue.setEncumb_status(rstask.getString("encumb_status"));
                loanvalue.setDiskFileName(rstask.getString("disk_file_name"));
                loanvalue.setDiskFileName1(rstask.getString("disk_file_name1"));

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return loanvalue;

    }

    public void saveHBAreapplyLoan(LoanHBAForm lform, String empid, String filepath) {
        Connection con = null;
        Calendar now = Calendar.getInstance();
        Date loanapplyDate = now.getTime();

        PreparedStatement ps = null;
        ResultSet rs = null;
        Date dt1 = null;
        Date dt2 = null;
        Date dt3 = null;

        DateFormat cdate = new SimpleDateFormat("dd-MMM-yyyy");
        MultipartFile loanAttch = lform.getFile_att();
        MultipartFile loanAttch1 = lform.getFile_att1();
        try {
            con = this.dataSource.getConnection();
            String forwardId = lform.getForwardtoHrmsid();
            String hidSpc = lform.getHidSPC();
            int loanvalue = 43;
            String initiatedSpc = lform.getEmpSPC();
            // String loanapplyfor=lform.getLoanapplyfor();
            String session_empId = empid;
            int taskid = lform.getTaskid();
            int loanid = lform.getLoanId();
            String Comments = lform.getLoancomments();
            String jobtype = lform.getJobtype();
            String per_post = lform.getPer_post();
            String per_appointment = lform.getPer_appointment();
            String other_govt_ser = lform.getOther_govt_ser();
            String address = lform.getAddress();
            String floor_area = lform.getFloor_area();
            String approx_valuation = lform.getApprox_valuation();
            String reason = lform.getReason();
            String constructed_area = lform.getConstructed_area();
            String cost_land = lform.getCost_land();
            String cost_building = lform.getCost_building();
            String total_amount = lform.getTotal_amount();
            String amount_adv = lform.getAmount_adv();
            String noofYear = lform.getNoofYear();
            String city_name = lform.getCity_name();
            String settle_retir = lform.getSettle_retir();
            String area_plot = lform.getArea_plot();
            String localauthority = lform.getLocalauthority();
            String propose_acquire = lform.getPropose_acquire();
            String no_rooms = lform.getNo_rooms();
            String total_floor = lform.getTotal_floor();
            String additional_storey = lform.getAdditional_storey();
            String addition_room = lform.getAddition_room();
            String addition_farea = lform.getAddition_farea();
            String est_cost = lform.getEst_cost();
            String amount_desired = lform.getAmount_desired();
            String year_repaid = lform.getYear_repaid();
            String exact_location = lform.getExact_location();
            String exact_floor_area = lform.getExact_floor_area();
            String plinth_area = lform.getPlinth_area();
            String total_land_cost = lform.getTotal_land_cost();
            String parties_name_address = lform.getParties_name_address();
            String repay_adv_amount = lform.getRepay_adv_amount();
            String repay_year = lform.getRepay_year();
            String readymade_exact_loc = lform.getReadymade_exact_loc();
            String readymade_floor_area = lform.getReadymade_floor_area();
            String readymade_plinth_area = lform.getReadymade_plinth_area();
            String house_age = lform.getHouse_age();
            String valuation_price = lform.getValuation_price();
            String owner_name = lform.getOwner_name();
            String readymade_appro_amount = lform.getReadymade_appro_amount();
            String readymade_adv_amount = lform.getReadymade_adv_amount();
            String readymade_year = lform.getReadymade_year();
            String propose_amount = lform.getPropose_amount();
            String propose_adv = lform.getPropose_adv();
            String propose_repaid = lform.getPropose_repaid();
            String term_lease = lform.getTerm_lease();
            String term_expired = lform.getTerm_expired();
            String lease_condition = lform.getLease_condition();
            String premimum_paid = lform.getPremimum_paid();
            String annual_rent = lform.getAnnual_rent();
            String encumb_status = lform.getEncumb_status();

            String strTaskmaster = "UPDATE   task_master SET status_id=?,pending_at=?,apply_to=?,pending_spc=?,apply_to_spc=?,initiated_on=?,note=? WHERE task_id=?";

            ps = con.prepareStatement(strTaskmaster);

            ps.setInt(1, loanvalue);
            ps.setString(2, forwardId);
            ps.setString(3, forwardId);

            ps.setString(4, hidSpc);
            ps.setString(5, hidSpc);
            ps.setTimestamp(6, new Timestamp(loanapplyDate.getTime()));
            ps.setString(7, "");
            ps.setInt(8, taskid);
            int stsTask = ps.executeUpdate();
            if (stsTask == 1) {
                String str = "UPDATE hw_emp_hba_loan SET taskid =?,emp_id=?,jobtype=?,per_post=?,per_appointment=?,other_govt_ser=?,address=?,floor_area=?,approx_valuation=?  ";
                str = str + ",reason=?,constructed_area=?,cost_land=?,cost_building=?,total_amount=?,amount_adv=?,noofyear=?";
                str = str + ",city_name=?,settle_retir=?,area_plot=?,localauthority=?,propose_acquire=?,no_rooms=?,total_floor=?";
                str = str + ",additional_storey=?,addition_room=?,addition_farea=?,est_cost=?,amount_desired=?,year_repaid=?,exact_location=?";
                str = str + ",exact_floor_area=?,plinth_area=?,total_land_cost=?,parties_name_address=?,repay_adv_amount=?,repay_year=?,readymade_exact_loc=?";
                str = str + ",readymade_floor_area=?,readymade_plinth_area=?,house_age=?,valuation_price=?,owner_name=?,readymade_appro_amount=?,readymade_adv_amount=?";
                str = str + ",readymade_year=?,propose_amount=?,propose_adv=?,propose_repaid=?,term_lease=?,term_expired=?,lease_condition=?";
                str = str + ",premimum_paid=?,annual_rent=?,encumb_status=?,forward_to=?,loan_status=?";
                str = str + ",comments=? WHERE loan_hba_id =? ";
                ps = con.prepareStatement(str);
                ps.setInt(1, taskid);
                ps.setString(2, session_empId);
                ps.setString(3, jobtype);
                ps.setString(4, per_post);
                ps.setString(5, per_appointment);

                ps.setString(6, other_govt_ser);
                ps.setString(7, address);
                ps.setString(8, floor_area);
                ps.setString(9, approx_valuation);
                ps.setString(10, reason);
                ps.setString(11, constructed_area);
                ps.setString(12, cost_land);
                ps.setString(13, cost_building);
                ps.setString(14, total_amount);
                ps.setString(15, amount_adv);
                ps.setString(16, noofYear);
                ps.setString(17, city_name);
                ps.setString(18, settle_retir);
                ps.setString(19, area_plot);
                ps.setString(20, localauthority);
                ps.setString(21, propose_acquire);
                ps.setString(22, no_rooms);
                ps.setString(23, total_floor);
                ps.setString(24, additional_storey);
                ps.setString(25, addition_room);
                ps.setString(26, addition_farea);
                ps.setString(27, est_cost);
                ps.setString(28, amount_desired);
                ps.setString(29, year_repaid);
                ps.setString(30, exact_location);
                ps.setString(31, exact_floor_area);
                ps.setString(32, plinth_area);
                ps.setString(33, total_land_cost);
                ps.setString(34, parties_name_address);
                ps.setString(35, repay_adv_amount);
                ps.setString(36, repay_year);
                ps.setString(37, readymade_exact_loc);
                ps.setString(38, readymade_floor_area);
                ps.setString(39, readymade_plinth_area);
                ps.setString(40, house_age);
                ps.setString(41, valuation_price);
                ps.setString(42, owner_name);
                ps.setString(43, readymade_appro_amount);
                ps.setString(44, readymade_adv_amount);
                ps.setString(45, readymade_year);
                ps.setString(46, propose_amount);
                ps.setString(47, propose_adv);
                ps.setString(48, propose_repaid);
                ps.setString(49, term_lease);
                ps.setString(50, term_expired);
                ps.setString(51, lease_condition);
                ps.setString(52, premimum_paid);
                ps.setString(53, annual_rent);
                ps.setString(54, encumb_status);
                ps.setString(55, hidSpc);
                ps.setInt(56, loanvalue);

                ps.setString(57, Comments);
                ps.setInt(58, loanid);
                int sts = ps.executeUpdate();
                if (sts > 0) {
                    InputStream inputStream = null;
                    OutputStream outputStream = null;
                    String filename = "";
                    if (loanAttch != null && !loanAttch.isEmpty()) {
                        long time = System.currentTimeMillis();
                        filename = session_empId + "_1_" + taskid + "_" + time;

                        ps = con.prepareStatement("UPDATE hw_emp_hba_loan SET original_filename=?,disk_file_name=?,file_type=? WHERE TASKID=?");
                        ps.setString(1, loanAttch.getOriginalFilename());
                        ps.setString(2, filename);
                        ps.setString(3, loanAttch.getContentType());
                        ps.setInt(4, taskid);
                        ps.executeUpdate();

                        String dirpath = filepath + "/attachment/";
                        File newfile = new File(dirpath);
                        if (!newfile.exists()) {
                            newfile.mkdirs();
                        }

                        outputStream = new FileOutputStream(dirpath + filename);
                        int read = 0;
                        byte[] bytes = new byte[1024];
                        inputStream = loanAttch.getInputStream();
                        while ((read = inputStream.read(bytes)) != -1) {
                            outputStream.write(bytes, 0, read);
                        }
                    }

                    InputStream inputStream1 = null;
                    OutputStream outputStream1 = null;
                    String filename1 = "";
                    if (loanAttch1 != null && !loanAttch1.isEmpty()) {
                        long time = System.currentTimeMillis();
                        filename1 = session_empId + "_2_" + taskid + "_" + time;

                        ps = con.prepareStatement("UPDATE hw_emp_hba_loan SET original_filename1=?,disk_file_name1=?,file_type1=? WHERE TASKID=?");
                        ps.setString(1, loanAttch1.getOriginalFilename());
                        ps.setString(2, filename1);
                        ps.setString(3, loanAttch1.getContentType());
                        ps.setInt(4, taskid);
                        ps.executeUpdate();

                        String dirpath = filepath + "/attachment1/";
                        File newfile = new File(dirpath);
                        if (!newfile.exists()) {
                            newfile.mkdirs();
                        }

                        outputStream1 = new FileOutputStream(dirpath + filename1);
                        int read = 0;
                        byte[] bytes = new byte[1024];
                        inputStream1 = loanAttch.getInputStream();
                        while ((read = inputStream.read(bytes)) != -1) {
                            outputStream1.write(bytes, 0, read);
                        }
                    }

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }

    }

    @Override
    public void viewPDFfunc(Document document, int loanid) {

        try {

            Font f1 = new Font();
            f1.setSize(10);
            f1.setFamily("Times New Roman");

            PdfPTable table = null;

            table = new PdfPTable(1);
            table.setWidths(new int[]{5});
            table.setWidthPercentage(100);

            PdfPCell cell = null;

            cell = new PdfPCell(new Phrase("GOVERNMENT OF ODISHA", f1));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("GENERAL ADMINISTRATION DEPARTMENT", f1));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("No. GAD- OE1-ADV-0484-2016-17100/Gen., Bhubaneswar 05/08/2016", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            document.add(table);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void saveLoanGPFsaction(LoanGPFForm lform) {
        Connection con = null;
        Calendar now = Calendar.getInstance();
        Date loanapplyDate = now.getTime();

        PreparedStatement ps = null;
        ResultSet rs = null;
        Date dt1 = null;
        String pending_at = null;
        String pending_spc = null;
        int taskid = lform.getTaskid();
        int loanid = lform.getLoanId();

        String letterno = lform.getLetterNo();
        String letterdate = lform.getLetterDate();
        String letterform = lform.getLetterformName();
        String letterto = lform.getLetterto();
        String letterdesi = lform.getLetterformdesignation();
        String memono = lform.getMemoNo();
        String memodate = lform.getMemoDate();

        MultipartFile loanAttch = lform.getFile_att();
        try {
            con = this.dataSource.getConnection();
            ps = con.prepareStatement("UPDATE hw_emp_gpf_loan SET letterno=?,letterdate=?,letterfrom=?,fromdesignation=?,letterto=?,memono=?,memodate=? WHERE taskid=? AND loan_gpf_id=?");
            ps.setString(1, letterno);
            ps.setString(2, letterdate);
            ps.setString(3, letterform);
            ps.setString(4, letterdesi);
            ps.setString(5, letterto);
            ps.setString(6, memono);
            ps.setString(7, memodate);
            ps.setInt(8, taskid);
            ps.setInt(9, loanid);
            int stsTask = ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    /**
     * ******************************* Temp GPF LOAN APPLY PANEL
     * *******************
     */
    @Override
    public LoanTempGPFForm TempGPFEmpDetails(String hrmsid) {
        LoanTempGPFForm loanvalue = new LoanTempGPFForm();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Calendar now = Calendar.getInstance();
        int cmonth = now.get(Calendar.MONTH);
        int cyear = now.get(Calendar.YEAR);
        int previousYear = cyear;
        try {
            con = this.dataSource.getConnection();
            ps = con.prepareStatement("SELECT a.gpf_no,a.gp,a.emp_id,ARRAY_TO_STRING(ARRAY[INITIALS, a.F_NAME, a.M_NAME,"
                    + "a.L_NAME], ' ') fullname,a.cur_basic_salary,to_char(a.JOINDATE_OF_GOO, 'DD-Mon-YYYY') as doj,"
                    + "to_char(a.dos, 'DD-Mon-YYYY') as dos,b.spn,b.spc,b.gpc,c.post,d.department_name,"
                    + "EXTRACT(YEAR from AGE(NOW(), a.JOINDATE_OF_GOO)) as age,EXTRACT(YEAR from AGE(a.dos,NOW())) as remainingage,e.ddo_code,e.off_en  FROM emp_mast a,g_spc b,g_post c,"
                    + "g_department d,g_office e WHERE  a.cur_spc=b.spc AND b.dept_code=d.department_code AND  "
                    + " b.gpc=c.post_code AND a.cur_off_code=e.off_code AND emp_id=?");
            ps.setString(1, hrmsid);
            rs = ps.executeQuery();
            if (rs.next()) {
                loanvalue.setEmpName(rs.getString("fullname"));
                // System.out.println(rs.getString("fullname"));
                String gpfno = rs.getString("gpf_no");
                loanvalue.setEmpSPC(rs.getString("spc"));
                String deptname = rs.getString("department_name");
                String accountNo = gpfno + " " + deptname;
                loanvalue.setGpfno(accountNo);

                String basicsalary_gp = rs.getString("cur_basic_salary") + "+ GP " + rs.getString("gp");
                loanvalue.setDesignation(rs.getString("post"));
                loanvalue.setPay(basicsalary_gp);
                loanvalue.setDoj(rs.getString("doj"));

                if (cmonth < 4) {
                    previousYear = cyear - 1;
                    cyear = previousYear;

                }
                if (cmonth == 0) {
                    cmonth = 12;
                }

                String ddo_code = rs.getString("ddo_code");
                String ddo_office = rs.getString("off_en");
                String ddooffice = ddo_office + "(" + ddo_code + ")";

                loanvalue.setDdocode(ddo_code);
                //  System.out.println("remainingage" + rs.getInt("remainingage") + "ge" + serviceYear);

            }
            /*  ps = con.prepareStatement("SELECT SUM(ad_amt) as total_subscription FROM aq_dtls WHERE ad_code='GPF' AND  emp_code='" + hrmsid + "' AND (p_month BETWEEN 4 AND '" + cmonth + "' AND (p_year  BETWEEN  '" + previousYear + "' AND  '" + cyear + "')) ");
             //  System.out.println("SELECT SUM(ad_amt) as total_subscription FROM aq_dtls WHERE ad_code='GPF' AND  emp_code='" + hrmsid + "' AND (p_month BETWEEN 4 AND '" + cmonth + "' AND (p_year  BETWEEN  '" + previousYear + "' AND  '" + cyear + "')) ");
             rs = ps.executeQuery();
             String total_subscription = null;
             if (rs.next()) {
             total_subscription = rs.getString("total_subscription");
             }
             */

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return loanvalue;
    }

    public void savetempgpfLoanData(LoanTempGPFForm lform, String empid, String filepath) {
        Connection con = null;
        Calendar now = Calendar.getInstance();
        Date loanapplyDate = now.getTime();

        PreparedStatement ps = null;
        ResultSet rs = null;
        Date dt1 = null;
        Date dt2 = null;
        Date dt3 = null;

        DateFormat cdate = new SimpleDateFormat("dd-MMM-yyyy");
        MultipartFile loanAttch = lform.getFile_att();
        try {
            con = this.dataSource.getConnection();
            String forwardId = lform.getForwardtoHrmsid();
            String hidSpc = lform.getHidSPC();
            int loanvalue = 49;
            String initiatedSpc = lform.getEmpSPC();
            // String loanapplyfor=lform.getLoanapplyfor();
            String session_empId = empid;

            String amount_credit = lform.getAmount_credit();
            String amount_subscription = lform.getAmount_subscription();
            String deduct_adv = lform.getDeduct_adv();
            String advance_taken = lform.getAdvance_taken();
            String amount_drawal = lform.getAmount_drawal();
            String date_drawal = lform.getDate_drawal();
            String purpose_drawal = lform.getPurpose_drawal();
            String date_repaid = lform.getDate_repaid();
            String date_drawal_sanction = lform.getDate_drawal_sanction();
            String date_drawal_cadvance = lform.getDate_drawal_cadvance();
            String balance_outstanding = lform.getBalance_outstanding();
            String rate_recovery = lform.getRate_recovery();

            String final_payment = lform.getFinal_payment();
            String amount_adv = lform.getAmount_adv();
            String purpose = lform.getPurpose();
            String total_advance = lform.getTotal_advance();
            String noofinst = lform.getNoofinst();
            String gpfno = lform.getGpfno();
            String gppftype = "TEMP GPF Advance";

            String ddo = lform.getAccountOfficer();

            int mcode = CommonFunctions.getMaxCodeInteger("TASK_MASTER", "TASK_ID", con);
            String strTaskmaster = "INSERT INTO task_master (task_id "
                    + ",process_id "
                    + ",initiated_by "
                    + ",status_id "
                    + ",pending_at "
                    + ",apply_to "
                    + ",initiated_spc "
                    + ",pending_spc "
                    + ",apply_to_spc "
                    + ",initiated_on "
                    + ") values(?,?,?,?,?,?,?,?,?,?)";
            ps = con.prepareStatement(strTaskmaster);

            ps.setInt(1, mcode);
            ps.setInt(2, 10);
            ps.setString(3, session_empId);
            ps.setInt(4, loanvalue);
            ps.setString(5, forwardId);
            ps.setString(6, forwardId);
            ps.setString(7, initiatedSpc);
            ps.setString(8, hidSpc);
            ps.setString(9, hidSpc);
            ps.setTimestamp(10, new Timestamp(loanapplyDate.getTime()));

            int stsTask = ps.executeUpdate();

            if (stsTask == 1) {
                String str = "INSERT INTO hw_emp_temp_gpf_loan(taskid "
                        + ",emp_id "
                        + ",amount_credit"
                        + ",amount_subscription"
                        + ",deduct_adv"
                        + ",advance_taken"
                        + ",amount_drawal"
                        + ",date_drawal"
                        + ",purpose_drawal"
                        + ",date_repaid"
                        + ",date_drawal_sanction"
                        + ",date_drawal_cadvance"
                        + ",balance_outstanding"
                        + ",rate_recovery"
                        + ",final_payment"
                        + ",amount_adv"
                        + ",purpose"
                        + ",total_advance"
                        + ",noofinst"
                        + ",ddo_code"
                        + ",forward_to"
                        + ",loan_apply_date"
                        + ",loan_status,gpftype) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                ps = con.prepareStatement(str);
                ps.setInt(1, mcode);
                ps.setString(2, session_empId);
                if (amount_credit != null && !amount_credit.equals("")) {
                    ps.setDouble(3, Double.parseDouble(amount_credit));
                } else {
                    ps.setDouble(3, 0);
                }
                if (amount_subscription != null && !amount_subscription.equals("")) {
                    ps.setDouble(4, Double.parseDouble(amount_subscription));
                } else {
                    ps.setDouble(4, 0);
                }

                if (deduct_adv != null && !deduct_adv.equals("")) {
                    ps.setDouble(5, Double.parseDouble(deduct_adv));
                } else {
                    ps.setDouble(5, 0);
                }
                ps.setString(6, advance_taken);
                if (amount_drawal != null && !amount_drawal.equals("")) {
                    ps.setDouble(7, Double.parseDouble(amount_drawal));
                } else {
                    ps.setDouble(7, 0);
                }

                ps.setString(8, date_drawal);
                ps.setString(9, purpose_drawal);
                ps.setString(10, date_repaid);
                ps.setString(11, date_drawal_sanction);

                ps.setString(12, date_drawal_cadvance);
                ps.setString(13, balance_outstanding);
                ps.setString(14, rate_recovery);
                ps.setString(15, final_payment);
                if (amount_adv != null && !amount_adv.equals("")) {
                    ps.setDouble(16, Double.parseDouble(amount_adv));
                } else {
                    ps.setDouble(16, 0);
                }
                ps.setString(17, purpose);

                if (total_advance != null && !total_advance.equals("")) {
                    ps.setDouble(18, Double.parseDouble(total_advance));
                } else {
                    ps.setDouble(18, 0);
                }
                ps.setString(19, noofinst);
                ps.setString(20, ddo);

                ps.setString(21, hidSpc);
                ps.setTimestamp(22, new Timestamp(loanapplyDate.getTime()));
                ps.setInt(23, loanvalue);
                ps.setString(24, gppftype);

                int sts = ps.executeUpdate();
                if (sts > 0) {
                    InputStream inputStream = null;
                    OutputStream outputStream = null;
                    String filename = "";
                    if (loanAttch != null && !loanAttch.isEmpty()) {
                        long time = System.currentTimeMillis();
                        filename = session_empId + "_" + mcode + "_" + time;

                        ps = con.prepareStatement("UPDATE hw_emp_temp_gpf_loan SET original_filename=?,disk_file_name=?,file_type=? WHERE TASKID=?");
                        ps.setString(1, loanAttch.getOriginalFilename());
                        ps.setString(2, filename);
                        ps.setString(3, loanAttch.getContentType());
                        ps.setInt(4, mcode);
                        ps.executeUpdate();

                        String dirpath = filepath + "/";
                        File newfile = new File(dirpath);
                        if (!newfile.exists()) {
                            newfile.mkdirs();
                        }

                        outputStream = new FileOutputStream(dirpath + filename);
                        int read = 0;
                        byte[] bytes = new byte[1024];
                        inputStream = loanAttch.getInputStream();
                        while ((read = inputStream.read(bytes)) != -1) {
                            outputStream.write(bytes, 0, read);
                        }
                    }

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    @Override
    public LoanTempGPFForm tempgpfLoanDetails(int taskid) {

        LoanTempGPFForm loanvalue = new LoanTempGPFForm();
        Connection con = null;
        PreparedStatement ps = null;
        PreparedStatement pstask = null;
        PreparedStatement psapply = null;
        ResultSet rs = null;
        ResultSet rstask = null;
        ResultSet rsapply = null;
        Calendar now = Calendar.getInstance();
        int cmonth = now.get(Calendar.MONTH);
        int cyear = now.get(Calendar.YEAR);
        int previousYear = cyear;
        String empId = null;

        try {
            con = this.dataSource.getConnection();
            pstask = con.prepareStatement("SELECT a.*,b.* FROM hw_emp_temp_gpf_loan a,task_master b WHERE a.taskid=b.task_id AND a.taskid='" + taskid + "' ");
            //  System.out.println("SELECT a.*,b.* FROM hw_emp_temp_gpf_loan a,task_master b WHERE a.taskid=b.task_id AND a.taskid='" + taskid + "' ");
            rstask = pstask.executeQuery();

            if (rstask.next()) {
                empId = rstask.getString("emp_id");

                loanvalue.setEmpId(rstask.getString("emp_id"));

                loanvalue.setAmount_credit(rstask.getString("amount_credit"));
                loanvalue.setAmount_subscription(rstask.getString("amount_subscription"));
                loanvalue.setDeduct_adv(rstask.getString("deduct_adv"));
                loanvalue.setAdvance_taken(rstask.getString("advance_taken"));
                loanvalue.setAmount_drawal(rstask.getString("amount_drawal"));
                loanvalue.setDate_drawal(rstask.getString("date_drawal"));
                loanvalue.setPurpose_drawal(rstask.getString("purpose_drawal"));
                loanvalue.setDate_repaid(rstask.getString("date_repaid"));
                loanvalue.setDate_drawal_sanction(rstask.getString("date_drawal_sanction"));
                loanvalue.setDate_drawal_cadvance(rstask.getString("date_drawal_cadvance"));
                loanvalue.setBalance_outstanding(rstask.getString("balance_outstanding"));
                loanvalue.setRate_recovery(rstask.getString("rate_recovery"));
                loanvalue.setFinal_payment(rstask.getString("final_payment"));
                loanvalue.setAmount_adv(rstask.getString("amount_adv"));
                loanvalue.setPurpose(rstask.getString("purpose"));
                loanvalue.setTotal_advance(rstask.getString("total_advance"));

                loanvalue.setNoofinst(rstask.getString("noofinst"));

                loanvalue.setApprovedBy(rstask.getString("apply_to"));
                loanvalue.setApprovedSpc(rstask.getString("pending_spc"));
                loanvalue.setLoanId(rstask.getInt("loan_temp_gpf_id"));
                loanvalue.setTaskid(rstask.getInt("taskid"));
                loanvalue.setLoancomments(rstask.getString("comments"));
                loanvalue.setStatusId(rstask.getInt("status_id"));

            }

            ps = con.prepareStatement("SELECT a.gpf_no,a.gp,a.emp_id,ARRAY_TO_STRING(ARRAY[INITIALS, a.F_NAME, a.M_NAME,"
                    + "a.L_NAME], ' ') fullname,a.cur_basic_salary,to_char(a.JOINDATE_OF_GOO, 'DD-Mon-YYYY') as doj,"
                    + "to_char(a.dos, 'DD-Mon-YYYY') as dos,b.spn,b.spc,b.gpc,c.post,d.department_name,"
                    + "EXTRACT(YEAR from AGE(NOW(), a.JOINDATE_OF_GOO)) as age,EXTRACT(YEAR from AGE(a.dos,NOW())) as remainingage,e.ddo_code,e.off_en  FROM emp_mast a,g_spc b,g_post c,"
                    + "g_department d,g_office e WHERE  a.cur_spc=b.spc AND b.dept_code=d.department_code AND  "
                    + " b.gpc=c.post_code AND a.cur_off_code=e.off_code AND emp_id=?");
            ps.setString(1, empId);

            rs = ps.executeQuery();
            if (rs.next()) {
                loanvalue.setEmpName(rs.getString("fullname"));

                String gpfno = rs.getString("gpf_no");
                loanvalue.setEmpSPC(rs.getString("spc"));
                String deptname = rs.getString("department_name");
                String accountNo = gpfno + " " + deptname;
                loanvalue.setGpfno(accountNo);

                String basicsalary_gp = rs.getString("cur_basic_salary") + "+ GP " + rs.getString("gp");
                loanvalue.setDesignation(rs.getString("post"));
                loanvalue.setPay(basicsalary_gp);
                loanvalue.setDoj(rs.getString("doj"));

                if (cmonth < 4) {
                    previousYear = cyear - 1;
                    cyear = previousYear;

                }
                if (cmonth == 0) {
                    cmonth = 12;
                }

                String ddo_code = rs.getString("ddo_code");
                String ddo_office = rs.getString("off_en");
                String ddooffice = ddo_office + "(" + ddo_code + ")";

                loanvalue.setDdocode(ddo_code);
                if (rstask.getString("disk_file_name") != null && !rstask.getString("disk_file_name").equals("")) {
                    loanvalue.setDiskFileName(rstask.getString("disk_file_name"));
                } else {
                    loanvalue.setDiskFileName("");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return loanvalue;

    }

    @Override
    public void DownloadtempgpfLoanAttch(HttpServletResponse response, String filepath, String loanid) throws IOException {

        Connection con = null;

        Statement stmt = null;
        ResultSet rs = null;
        int BUFFER_LENGTH = 4096;
        OutputStream out = response.getOutputStream();
        try {
            con = dataSource.getConnection();

            stmt = con.createStatement();
            String sql = "SELECT * FROM hw_emp_temp_gpf_loan WHERE loan_temp_gpf_id='" + loanid + "'";
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                File f = null;
                String dirpath = filepath + "/" + rs.getString("DISK_FILE_NAME");
                System.out.println("dirpath is: " + dirpath);
                f = new File(dirpath);
                if (f.exists()) {
                    String originalFilename = rs.getString("ORIGINAL_FILENAME");
                    String filetype = rs.getString("FILE_TYPE");

                    response.setContentLength((int) f.length());
                    FileInputStream is = new FileInputStream(f);

                    response.setContentType(filetype);
                    response.setHeader("Content-Disposition", "attachment; filename=\"" + originalFilename + "\"");

                    byte[] bytes = new byte[BUFFER_LENGTH];
                    int read = 0;
                    while ((read = is.read(bytes, 0, BUFFER_LENGTH)) != -1) {
                        out.write(bytes, 0, read);
                    }
                    is.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, stmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    public void savetempfpfApproveData(LoanTempGPFForm lform) {
        Connection con = null;
        Calendar now = Calendar.getInstance();
        Date loanapplyDate = now.getTime();

        PreparedStatement ps = null;
        ResultSet rs = null;
        Date dt1 = null;
        String pending_at = null;
        String pending_spc = null;
        int taskid = lform.getTaskid();
        int loanid = lform.getLoanId();
        int loanstatus = lform.getLoan_status();
        String Comments = lform.getLoancomments();
        String approvedby = lform.getApprovedBy();
        String approvedspc = lform.getApprovedSpc();

        DateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");

        MultipartFile loanAttch = lform.getFile_att();
        try {
            con = this.dataSource.getConnection();
            if (loanstatus == 51 || loanstatus == 53) {
                String forwardId = lform.getForwardtoHrmsid();
                String hidSpc = lform.getHidSPC();
                ps = con.prepareStatement(" SELECT * FROM task_master WHERE  task_id='" + taskid + "'");
                rs = ps.executeQuery();
                while (rs.next()) {
                    pending_at = (rs.getString("pending_at"));
                    pending_spc = (rs.getString("pending_spc"));
                }

                ps = con.prepareStatement("UPDATE task_master SET status_id=?,pending_at=?,note=?,apply_to=?,pending_spc=?,apply_to_spc=? WHERE task_id=?");
                ps.setInt(1, loanstatus);
                ps.setString(2, forwardId);
                ps.setString(3, Comments);
                ps.setString(4, forwardId);
                ps.setString(5, hidSpc);
                ps.setString(6, hidSpc);
                ps.setInt(7, taskid);
                int stsTask = ps.executeUpdate();
                if (stsTask == 1) {
                    String str = "INSERT INTO workflow_log(task_id "
                            + ",task_action_date "
                            + ",action_taken_by"
                            + ",spc_ontime"
                            + ",task_status_id"
                            + ",note"
                            + ",forward_to"
                            + ",forwarded_spc"
                            + ",log_id"
                            + ",ref_id"
                            + ") values(?,?,?,?,?,?,?,?,?,?)";
                    ps = con.prepareStatement(str);
                    ps.setInt(1, taskid);
                    ps.setTimestamp(2, new Timestamp(loanapplyDate.getTime()));
                    ps.setString(3, pending_at);
                    ps.setString(4, pending_spc);
                    ps.setInt(5, loanstatus);
                    ps.setString(6, Comments);
                    ps.setString(7, forwardId);
                    ps.setString(8, hidSpc);
                    ps.setInt(10, loanid);
                    int wfcode = CommonFunctions.getMaxCodeInteger("workflow_log", "log_id", con);
                    ps.setInt(9, wfcode);
                    ps.executeUpdate();

                    ps = con.prepareStatement("UPDATE hw_emp_temp_gpf_loan SET loan_status=?,comments=? WHERE taskid=? AND loan_temp_gpf_id=?");
                    ps.setInt(1, loanstatus);
                    ps.setString(2, Comments);
                    ps.setInt(3, taskid);
                    ps.setInt(4, loanid);
                    ps.executeUpdate();

                }

            } else {
                ps = con.prepareStatement("UPDATE hw_emp_temp_gpf_loan SET loan_status=?,approved_by=?,approved_spc=?,comments=?,loan_sanction_date=? WHERE taskid=? AND loan_temp_gpf_id=?");
                ps.setInt(1, loanstatus);
                ps.setString(2, approvedby);
                ps.setString(3, approvedspc);
                ps.setString(4, Comments);
                ps.setTimestamp(5, new Timestamp(loanapplyDate.getTime()));
                ps.setInt(6, taskid);
                ps.setInt(7, loanid);
                int stsTask = ps.executeUpdate();

                if (stsTask == 1) {
                    ps = con.prepareStatement("UPDATE task_master SET status_id=?,pending_at=?,note=?,apply_to=?,pending_spc=?,apply_to_spc=? WHERE task_id=?");
                    ps.setInt(1, loanstatus);
                    ps.setString(2, null);
                    ps.setString(3, Comments);
                    ps.setString(4, null);
                    ps.setString(5, null);
                    ps.setString(6, null);
                    ps.setInt(7, taskid);
                    ps.executeUpdate();
                }    //  System.out.println( taskid);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    public LoanTempGPFForm Reapplytempgpf(String option, String hrmsid, int loanid) {

        LoanTempGPFForm loanvalue = new LoanTempGPFForm();
        Connection con = null;
        PreparedStatement ps = null;
        PreparedStatement pstask = null;
        PreparedStatement psapply = null;
        ResultSet rs = null;
        ResultSet rstask = null;
        ResultSet rsapply = null;
        Calendar now = Calendar.getInstance();
        int cmonth = now.get(Calendar.MONTH);
        int cyear = now.get(Calendar.YEAR);
        int previousYear = cyear;

        try {
            con = this.dataSource.getConnection();
            ps = con.prepareStatement("SELECT a.gpf_no,a.gp,a.emp_id,ARRAY_TO_STRING(ARRAY[INITIALS, a.F_NAME, a.M_NAME,"
                    + "a.L_NAME], ' ') fullname,a.cur_basic_salary,to_char(a.JOINDATE_OF_GOO, 'DD-Mon-YYYY') as doj,"
                    + "to_char(a.dos, 'DD-Mon-YYYY') as dos,b.spn,b.spc,b.gpc,c.post,d.department_name,"
                    + "EXTRACT(YEAR from AGE(NOW(), a.JOINDATE_OF_GOO)) as age,EXTRACT(YEAR from AGE(a.dos,NOW())) as remainingage,e.ddo_code,e.off_en  FROM emp_mast a,g_spc b,g_post c,"
                    + "g_department d,g_office e WHERE  a.cur_spc=b.spc AND b.dept_code=d.department_code AND  "
                    + " b.gpc=c.post_code AND a.cur_off_code=e.off_code AND emp_id=?");
            ps.setString(1, hrmsid);

            rs = ps.executeQuery();
            if (rs.next()) {
                loanvalue.setEmpName(rs.getString("fullname"));

                String gpfno = rs.getString("gpf_no");
                loanvalue.setEmpSPC(rs.getString("spc"));
                String deptname = rs.getString("department_name");
                String accountNo = gpfno + " " + deptname;
                loanvalue.setGpfno(accountNo);

                String basicsalary_gp = rs.getString("cur_basic_salary") + "+ GP " + rs.getString("gp");
                loanvalue.setDesignation(rs.getString("post"));
                loanvalue.setPay(basicsalary_gp);
                loanvalue.setDoj(rs.getString("doj"));

                if (cmonth < 4) {
                    previousYear = cyear - 1;
                    cyear = previousYear;

                }
                if (cmonth == 0) {
                    cmonth = 12;
                }

                String ddo_code = rs.getString("ddo_code");
                String ddo_office = rs.getString("off_en");
                String ddooffice = ddo_office + "(" + ddo_code + ")";

                loanvalue.setDdocode(ddo_code);
               
            }
            pstask = con.prepareStatement("SELECT a.*,b.* FROM hw_emp_temp_gpf_loan a,task_master b WHERE a.taskid=b.task_id AND a.loan_temp_gpf_id='" + loanid + "' ");
       System.out.println("SELECT a.*,b.* FROM hw_emp_temp_gpf_loan a,task_master b WHERE a.taskid=b.task_id AND a.loan_temp_gpf_id='" + loanid + "' ");
            rstask = pstask.executeQuery();

            if (rstask.next()) {

                loanvalue.setEmpId(rstask.getString("emp_id"));
                loanvalue.setApprovedBy(rstask.getString("apply_to"));
                loanvalue.setApprovedSpc(rstask.getString("pending_spc"));
                loanvalue.setLoanId(rstask.getInt("loan_temp_gpf_id"));
                loanvalue.setTaskid(rstask.getInt("taskid"));
                loanvalue.setLoancomments(rstask.getString("comments"));

                loanvalue.setAmount_credit(rstask.getString("amount_credit"));
                loanvalue.setAmount_subscription(rstask.getString("amount_subscription"));
                loanvalue.setDeduct_adv(rstask.getString("deduct_adv"));
                loanvalue.setAdvance_taken(rstask.getString("advance_taken"));
                loanvalue.setAmount_drawal(rstask.getString("amount_drawal"));
                loanvalue.setDate_drawal(rstask.getString("date_drawal"));
                loanvalue.setPurpose_drawal(rstask.getString("purpose_drawal"));
                loanvalue.setDate_repaid(rstask.getString("date_repaid"));
                loanvalue.setDate_drawal_sanction(rstask.getString("date_drawal_sanction"));
                loanvalue.setDate_drawal_cadvance(rstask.getString("date_drawal_cadvance"));
                loanvalue.setBalance_outstanding(rstask.getString("balance_outstanding"));
                loanvalue.setRate_recovery(rstask.getString("rate_recovery"));
                loanvalue.setFinal_payment(rstask.getString("final_payment"));
                loanvalue.setAmount_adv(rstask.getString("amount_adv"));
                loanvalue.setPurpose(rstask.getString("purpose"));
                loanvalue.setTotal_advance(rstask.getString("total_advance"));

                loanvalue.setNoofinst(rstask.getString("noofinst"));

                loanvalue.setApprovedBy(rstask.getString("apply_to"));
                loanvalue.setApprovedSpc(rstask.getString("pending_spc"));
                loanvalue.setLoanId(rstask.getInt("loan_temp_gpf_id"));
                loanvalue.setTaskid(rstask.getInt("taskid"));
                loanvalue.setLoancomments(rstask.getString("comments"));
                loanvalue.setStatusId(rstask.getInt("status_id"));
                 if (rstask.getString("disk_file_name") != null && !rstask.getString("disk_file_name").equals("")) {
                    loanvalue.setDiskFileName(rstask.getString("disk_file_name"));
                } else {
                    loanvalue.setDiskFileName("");
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return loanvalue;

    }
     public void savetempgpfreapply(LoanTempGPFForm lform, String empid, String filepath) {
        Connection con = null;
        Calendar now = Calendar.getInstance();
        Date loanapplyDate = now.getTime();

        PreparedStatement ps = null;
        ResultSet rs = null;
        Date dt1 = null;
        Date dt2 = null;
        Date dt3 = null;

        DateFormat cdate = new SimpleDateFormat("dd-MMM-yyyy");
        MultipartFile loanAttch = lform.getFile_att();
       
        try {
            con = this.dataSource.getConnection();
            String forwardId = lform.getForwardtoHrmsid();
            String hidSpc = lform.getHidSPC();
            int loanvalue = 49;
            String initiatedSpc = lform.getEmpSPC();
            // String loanapplyfor=lform.getLoanapplyfor();
            String session_empId = empid;
            int taskid = lform.getTaskid();
            int loanid = lform.getLoanId();
            String Comments = lform.getLoancomments();
            String amount_credit = lform.getAmount_credit();
            String amount_subscription = lform.getAmount_subscription();
            String deduct_adv = lform.getDeduct_adv();
            String advance_taken = lform.getAdvance_taken();
            String amount_drawal = lform.getAmount_drawal();
            String date_drawal = lform.getDate_drawal();
            String purpose_drawal = lform.getPurpose_drawal();
            String date_repaid = lform.getDate_repaid();
            String date_drawal_sanction = lform.getDate_drawal_sanction();
            String date_drawal_cadvance = lform.getDate_drawal_cadvance();
            String balance_outstanding = lform.getBalance_outstanding();
            String rate_recovery = lform.getRate_recovery();

            String final_payment = lform.getFinal_payment();
            String amount_adv = lform.getAmount_adv();
            String purpose = lform.getPurpose();
            String total_advance = lform.getTotal_advance();
            String noofinst = lform.getNoofinst();
            String gpfno = lform.getGpfno();
            String gppftype = "TEMP GPF Advance";

            String strTaskmaster = "UPDATE   task_master SET status_id=?,pending_at=?,apply_to=?,pending_spc=?,apply_to_spc=?,initiated_on=?,note=? WHERE task_id=?";

            ps = con.prepareStatement(strTaskmaster);

            ps.setInt(1, loanvalue);
            ps.setString(2, forwardId);
            ps.setString(3, forwardId);

            ps.setString(4, hidSpc);
            ps.setString(5, hidSpc);
            ps.setTimestamp(6, new Timestamp(loanapplyDate.getTime()));
            ps.setString(7, "");
            ps.setInt(8, taskid);
            int stsTask = ps.executeUpdate();
            if (stsTask == 1) {
                String str = "UPDATE hw_emp_temp_gpf_loan SET amount_credit =?,amount_subscription=?,deduct_adv=?,advance_taken=?,amount_drawal=?,date_drawal=?,purpose_drawal=?,date_repaid=?,date_drawal_sanction=?  ";
                str = str + ",date_drawal_cadvance=?,balance_outstanding=?,rate_recovery=?,final_payment=?,amount_adv=?,purpose=?,total_advance=?";
                str = str + ",noofinst=?,forward_to=?,loan_status=?,comments=?";
             
              
                ps = con.prepareStatement(str);
              
             if (amount_credit != null && !amount_credit.equals("")) {
                    ps.setDouble(1, Double.parseDouble(amount_credit));
                } else {
                    ps.setDouble(1, 0);
                }
                if (amount_subscription != null && !amount_subscription.equals("")) {
                    ps.setDouble(2, Double.parseDouble(amount_subscription));
                } else {
                    ps.setDouble(2, 0);
                }

                if (deduct_adv != null && !deduct_adv.equals("")) {
                    ps.setDouble(3, Double.parseDouble(deduct_adv));
                } else {
                    ps.setDouble(3, 0);
                }
                ps.setString(4, advance_taken);
                if (amount_drawal != null && !amount_drawal.equals("")) {
                    ps.setDouble(5, Double.parseDouble(amount_drawal));
                } else {
                    ps.setDouble(5, 0);
                }

                ps.setString(6, date_drawal);
                ps.setString(7, purpose_drawal);
                ps.setString(8, date_repaid);
                ps.setString(9, date_drawal_sanction);

                ps.setString(10, date_drawal_cadvance);
                ps.setString(11, balance_outstanding);
                ps.setString(12, rate_recovery);
                ps.setString(13, final_payment);
                if (amount_adv != null && !amount_adv.equals("")) {
                    ps.setDouble(14, Double.parseDouble(amount_adv));
                } else {
                    ps.setDouble(14, 0);
                }
                ps.setString(15, purpose);

                if (total_advance != null && !total_advance.equals("")) {
                    ps.setDouble(16, Double.parseDouble(total_advance));
                } else {
                    ps.setDouble(16, 0);
                }
                ps.setString(17, noofinst);
                ps.setString(18, hidSpc);
                ps.setInt(19, loanvalue);

                ps.setString(20, Comments);
             
                int sts = ps.executeUpdate();
                if (sts > 0) {
                    InputStream inputStream = null;
                    OutputStream outputStream = null;
                    String filename = "";
                    if (loanAttch != null && !loanAttch.isEmpty()) {
			long time = System.currentTimeMillis();
                        filename = session_empId + "_" + taskid + "_" + time;

                        ps = con.prepareStatement("UPDATE hw_emp_temp_gpf_loan SET original_filename=?,disk_file_name=?,file_type=? WHERE TASKID=?");
                        ps.setString(1, loanAttch.getOriginalFilename());
                        ps.setString(2, filename);
                        ps.setString(3, loanAttch.getContentType());
                        ps.setInt(4, taskid);
                        ps.executeUpdate();

                        String dirpath = filepath + "/";
                        File newfile = new File(dirpath);
                        if (!newfile.exists()) {
                            newfile.mkdirs();
                        }

                        outputStream = new FileOutputStream(dirpath + filename);
                        int read = 0;
                        byte[] bytes = new byte[1024];
                        inputStream = loanAttch.getInputStream();
                        while ((read = inputStream.read(bytes)) != -1) {
                            outputStream.write(bytes, 0, read);
                        }
                    }

                 

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }

    }


}
