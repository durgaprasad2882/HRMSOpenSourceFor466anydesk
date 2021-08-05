/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.report.annualestablishment;

import hrms.common.CommonFunctions;
import hrms.common.DataBaseFunctions;
import hrms.model.report.annualestablishmentreport.AnnualEstablishment;
import hrms.model.report.annualestablishmentreport.DepartmentWiseAerStatus;
import hrms.model.report.annualestablishmentreport.ScheduleIIBean;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 *
 * @author Surendra
 */
public class AnnuaiEstablishmentReportDAOImpl implements AnnuaiEstablishmentReportDAO {

    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public AnnualEstablishment submittedforCurrentFinancialYear(String offcode, AnnualEstablishment ae) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String status = "";
        try {
            con = this.dataSource.getConnection();
            ps = con.prepareStatement("SELECT status,taskid FROM aer_report_submit WHERE fy=? AND off_code=?");
            ps.setString(1, ae.getFy());
            ps.setString(2, offcode);
            rs = ps.executeQuery();
            if (rs.next()) {
                status = rs.getString("status");
                ae.setStatus(status);
                ae.setTaskid(rs.getInt("taskid"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, ps);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return ae;
    }

    @Override
    public List getAerReportList(String offcode) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        List<AnnualEstablishment> li = new ArrayList<AnnualEstablishment>();
        AnnualEstablishment ae = null;
        int i = 0;
        try {
            con = this.dataSource.getConnection();

            ps = con.prepareStatement("SELECT aer_id, a.off_code, spn controlling_spc, file_name,  fy, submitted_on, taskid, d.status_name \n"
                    + "FROM aer_report_submit a, G_SPC b, task_master c, g_process_status d WHERE a.taskid=c.task_id and  a.controlling_spc=b.spc and c.status_id=d.status_id and  a.off_code=?");
            ps.setString(1, offcode);
            rs = ps.executeQuery();
            while (rs.next()) {
                i++;
                ae = new AnnualEstablishment();
                ae.setSerialno(i);
                ae.setOffCode(rs.getString("off_code"));
                ae.setControllingSpc(rs.getString("controlling_spc"));
                ae.setFileName(rs.getString("file_name"));
                ae.setStatus(rs.getString("status_name"));
                ae.setFy(rs.getString("fy"));
                ae.setTaskid(rs.getInt("taskid"));
                ae.setSubmittedDate(CommonFunctions.getFormattedOutputDate1(rs.getDate("submitted_on")));

                li.add(ae);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return li;
    }

    @Override
    public List<AnnualEstablishment> getAnnualEstablistmentReportListFromAuthLogin(String taskId) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        List<AnnualEstablishment> li = new ArrayList<AnnualEstablishment>();
        AnnualEstablishment ae = null;
        int i = 0;
        try {
            con = this.dataSource.getConnection();

            ps = con.prepareStatement("select meninposition(gpc,pay_scale, post_grp, pay_scale_7th_pay , gp , pay_gp_level , off_code ) meninPosition, "
                    + "	post, sanction_strength, gpc, pay_scale, post_grp, pay_scale_7th_pay, GP, pay_gp_level, off_code from ( "
                    + " select COUNT(*) sanction_strength,gpc, pay_scale, post_grp,pay_scale_7th_pay,GP,pay_gp_level, a.off_code  from g_spc a, aer_report_submit b  "
                    + " where a.off_code=b.off_code and taskid=? and   a.is_sanctioned='Y' group by gpc, pay_scale, post_grp,pay_scale_7th_pay,GP,pay_gp_level, a.off_code) g_spc "
                    + " inner join g_post on g_spc.gpc=g_post.post_code order by post ");
            System.out.println("taskId==" + taskId);
            ps.setInt(1, Integer.parseInt(taskId));
            rs = ps.executeQuery();
            while (rs.next()) {
                i++;
                ae = new AnnualEstablishment();
                ae.setSerialno(i);
                ae.setGpc(rs.getString("gpc"));
                ae.setOffCode(rs.getString("off_code"));
                ae.setPostname(rs.getString("post"));
                ae.setGroup(rs.getString("post_grp"));
                ae.setScaleofPay(rs.getString("pay_scale"));
                ae.setGp(rs.getString("GP"));
                ae.setScaleofPay7th(rs.getString("pay_scale_7th_pay"));
                ae.setLevel(rs.getString("pay_gp_level"));
                ae.setSanctionedStrength(rs.getInt("sanction_strength"));
                int meninPosition = rs.getInt("meninPosition");
                int vacant = ae.getSanctionedStrength() - meninPosition;
                ae.setMeninPosition(meninPosition);
                ae.setVacancyPosition(vacant);

                li.add(ae);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return li;
    }

    @Override
    public List<AnnualEstablishment> getAnnualEstablistmentReportList(String offcode) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        List<AnnualEstablishment> li = new ArrayList<AnnualEstablishment>();
        AnnualEstablishment ae = null;
        int i = 0;
        try {
            con = this.dataSource.getConnection();

            ps = con.prepareStatement("select meninposition(gpc,pay_scale, post_grp, pay_scale_7th_pay , gp , pay_gp_level , off_code ) meninPosition, post, sanction_strength, gpc, pay_scale, post_grp, pay_scale_7th_pay, GP, pay_gp_level, off_code from ( \n"
                    + "SELECT COUNT(*) sanction_strength,gpc, pay_scale, post_grp,pay_scale_7th_pay,GP,pay_gp_level, off_code \n"
                    + "from g_spc where  off_code=? and is_sanctioned='Y'  group by gpc, pay_scale, post_grp,pay_scale_7th_pay,GP,pay_gp_level, off_code) g_spc\n"
                    + "inner join g_post on g_spc.gpc=g_post.post_code order by post");

            ps.setString(1, offcode);
            rs = ps.executeQuery();
            while (rs.next()) {
                i++;
                ae = new AnnualEstablishment();
                ae.setSerialno(i);
                ae.setGpc(rs.getString("gpc"));
                ae.setOffCode(rs.getString("off_code"));
                ae.setPostname(rs.getString("post"));
                ae.setGroup(rs.getString("post_grp"));
                ae.setScaleofPay(rs.getString("pay_scale"));
                ae.setGp(rs.getString("GP"));
                ae.setScaleofPay7th(rs.getString("pay_scale_7th_pay"));
                ae.setLevel(rs.getString("pay_gp_level"));
                ae.setSanctionedStrength(rs.getInt("sanction_strength"));
                int meninPosition = rs.getInt("meninPosition");
                int vacant = ae.getSanctionedStrength() - meninPosition;
                ae.setMeninPosition(meninPosition);
                ae.setVacancyPosition(vacant);

                li.add(ae);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return li;
    }

    @Override
    public void addAERMaster(AnnualEstablishment ae, String empId) {
        Connection con = null;
        PreparedStatement ps2 = null;
        PreparedStatement ps4 = null;
        ResultSet rs2 = null;
        int mcode = 0;
        try {

            con = this.dataSource.getConnection();

            String startTime = "";
            Calendar cal = Calendar.getInstance();
            DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");
            startTime = dateFormat.format(cal.getTime());
            String authEmpId = "";
            ps4 = con.prepareStatement("SELECT EMP_ID FROM emp_mast  WHERE cur_spc=?");
            ps4.setString(1, ae.getControllingSpc());
            rs2 = ps4.executeQuery();
            if (rs2.next()) {
                authEmpId = rs2.getString("EMP_ID");
            }

            if (ae.getTaskid() > 0) {
                mcode = ae.getTaskid();
                ps2 = con.prepareStatement("UPDATE TASK_MASTER SET INITIATED_BY=?, INITIATED_ON=?, STATUS_ID=?, PENDING_AT=?,APPLY_TO=?,INITIATED_SPC=?,PENDING_SPC=? WHERE TASK_ID=? AND PROCESS_ID=?");
                ps2.setString(1, empId);
                ps2.setTimestamp(2, new Timestamp(dateFormat.parse(startTime).getTime()));
                ps2.setInt(3, 66);//SUBMITTED TO REPORTING AUTHORITY
                ps2.setString(4, authEmpId);
                ps2.setString(5, authEmpId);
                ps2.setString(6, ae.getControllingSpc());
                ps2.setString(7, ae.getControllingSpc());
                ps2.setInt(8, ae.getTaskid());
                ps2.setInt(9, 13);
                ps2.execute();
            } else {

                mcode = CommonFunctions.getMaxCodeInteger("TASK_MASTER", "TASK_ID", con);
                ps2 = con.prepareStatement("INSERT INTO TASK_MASTER(TASK_ID, PROCESS_ID, INITIATED_BY, INITIATED_ON, STATUS_ID, PENDING_AT,APPLY_TO,INITIATED_SPC,PENDING_SPC) Values (?,?,?,?,?,?,?,?,?)");
                ps2.setInt(1, mcode);
                ps2.setInt(2, 13);
                ps2.setString(3, empId);
                ps2.setTimestamp(4, new Timestamp(dateFormat.parse(startTime).getTime()));
                ps2.setInt(5, 66);//SUBMITTED TO REPORTING AUTHORITY
                ps2.setString(6, authEmpId);
                ps2.setString(7, authEmpId);
                ps2.setString(8, ae.getControllingSpc());
                ps2.setString(9, ae.getControllingSpc());
                ps2.execute();
            }
            ps2 = con.prepareStatement("update aer_report_submit set controlling_spc=?, file_name=?, status=?, taskid=?, submitted_on=? where off_code=? and fy=?");
            ps2.setString(1, ae.getControllingSpc());
            ps2.setString(2, "AER_" + ae.getOffCode() + ".pdf");
            ps2.setString(3, "YES");
            ps2.setInt(4, mcode);
            ps2.setTimestamp(5, new Timestamp(dateFormat.parse(startTime).getTime()));
            ps2.setString(6, ae.getOffCode());
            ps2.setString(7, ae.getFy());
            ps2.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs2);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    @Override
    public void addAEReportData(AnnualEstablishment ae, String empId, String fy) {
        Connection con = null;
        PreparedStatement ps4 = null;
        ResultSet rs2 = null;
        try {
            con = this.dataSource.getConnection();

            ps4 = con.prepareStatement("INSERT INTO annual_establish_report (off_code ,gpc, postname, post_group, pay_scale_7th_pay ,"
                    + "pay_scale_6th_pay ,level ,sanction_strength ,menin_position ,vacancy_position ,financial_year ,submit_status ,gp) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?) ");

            ps4.setString(1, ae.getOffCode());
            ps4.setString(2, ae.getGpc());
            ps4.setString(3, ae.getPostname());
            ps4.setString(4, ae.getGroup());
            ps4.setString(5, ae.getScaleofPay7th());
            ps4.setString(6, ae.getScaleofPay());
            ps4.setString(7, ae.getLevel());
            ps4.setInt(8, ae.getSanctionedStrength());
            ps4.setInt(9, ae.getMeninPosition());
            ps4.setInt(10, ae.getVacancyPosition());
            ps4.setString(11, fy);
            ps4.setString(12, "Y");
            ps4.setString(13, ae.getGp());
            ps4.execute();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs2);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    @Override
    public void updateGspcForAERReport(String offcode) {
        Connection con = null;
        PreparedStatement ps2 = null;
        PreparedStatement ps4 = null;
        PreparedStatement ps6 = null;
        PreparedStatement ps8 = null;
        ResultSet rs2 = null;
        ResultSet rs4 = null;
        ResultSet rs8 = null;
        String sql = "";
        String sql2 = "";
        try {
            con = this.dataSource.getConnection();
            sql = "SELECT cur_spc, emp_id FROM emp_mast WHERE cur_off_code=? ";
            ps2 = con.prepareStatement(sql);
            ps2.setString(1, offcode);

            sql2 = "SELECT previous_pay_scale,previous_gp,payrev_fitted_level,PAYSCALE FROM PAY_REVISION_OPTION \n"
                    + "LEFT OUTER JOIN (SELECT DISTINCT PAYSCALE, GP_LEVEL FROM PAY_MATRIX_2017 )  PAY_MATRIX_2017  ON PAY_REVISION_OPTION.payrev_fitted_level=PAY_MATRIX_2017.GP_LEVEL::text\n"
                    + "WHERE emp_id=? and IS_APPROVED_CHECKING_AUTH='Y'";
            ps4 = con.prepareStatement(sql2);

            ps6 = con.prepareStatement(" UPDATE G_SPC SET pay_gp_level=?, pay_scale=?, GP=?, pay_scale_7th_pay=? WHERE spc=? ");

            ps8 = con.prepareStatement("select  min(amt) entry_level from PAY_MATRIX_2017 where PAYSCALE=? and gp=? and GP_LEVEL=? \n"
                    + "UNION\n"
                    + "select  max(amt) entry_level from PAY_MATRIX_2017 where PAYSCALE=? and gp=? and GP_LEVEL=?  ");

            rs2 = ps2.executeQuery();
            while (rs2.next()) {
                ps4.setString(1, rs2.getString("emp_id"));
                rs4 = ps4.executeQuery();
                if (rs4.next()) {

                    String payScale7th = "";

                    ps8.setString(1, rs4.getString("PAYSCALE"));
                    ps8.setInt(2, rs4.getInt("previous_gp"));
                    if (rs4.getString("payrev_fitted_level") != null && !rs4.getString("payrev_fitted_level").equals("")) {
                        ps8.setInt(3, Integer.parseInt(rs4.getString("payrev_fitted_level")));
                    } else {
                        ps8.setInt(3, 0);
                    }

                    ps8.setString(4, rs4.getString("PAYSCALE"));
                    ps8.setInt(5, rs4.getInt("previous_gp"));
                    if (rs4.getString("payrev_fitted_level") != null && !rs4.getString("payrev_fitted_level").equals("")) {
                        ps8.setInt(6, Integer.parseInt(rs4.getString("payrev_fitted_level")));
                    } else {
                        ps8.setInt(6, 0);
                    }

                    rs8 = ps8.executeQuery();
                    while (rs8.next()) {

                        if (payScale7th != null && !payScale7th.equals("")) {
                            payScale7th = payScale7th + "-" + rs8.getString("entry_level");
                        } else {
                            payScale7th = rs8.getString("entry_level");
                        }

                    }

                    ps6.setString(1, rs4.getString("payrev_fitted_level"));
                    ps6.setString(2, rs4.getString("previous_pay_scale"));
                    ps6.setInt(3, rs4.getInt("previous_gp"));
                    ps6.setString(4, payScale7th);
                    ps6.setString(5, rs2.getString("cur_spc"));
                    ps6.execute();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    @Override
    public List<AnnualEstablishment> getSubmittedReportList(String offcode, String fy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<AnnualEstablishment> li = new ArrayList<AnnualEstablishment>();
        AnnualEstablishment ae = null;
        try {
            con = this.dataSource.getConnection();
            ps = con.prepareStatement("SELECT off_code ,gpc ,postname ,post_group ,pay_scale_7th_pay ,gp,"
                    + "pay_scale_6th_pay ,level ,sanction_strength ,menin_position ,vacancy_position ,financial_year ,submit_status ,file_path from annual_establish_report where off_code=? and financial_year=?");

            ps.setString(1, offcode);
            ps.setString(2, fy);
            rs = ps.executeQuery();
            int i = 0;
            while (rs.next()) {
                i++;
                ae = new AnnualEstablishment();
                ae.setSerialno(i);
                ae.setGpc(rs.getString("gpc"));
                ae.setOffCode(rs.getString("off_code"));
                ae.setPostname(rs.getString("postname"));
                ae.setGroup(rs.getString("post_group"));
                ae.setScaleofPay(rs.getString("pay_scale_6th_pay"));
                ae.setGp(rs.getString("GP"));
                ae.setScaleofPay7th(rs.getString("pay_scale_7th_pay"));
                ae.setLevel(rs.getString("level"));
                ae.setSanctionedStrength(rs.getInt("sanction_strength"));
                ae.setMeninPosition(rs.getInt("menin_position"));
                ae.setVacancyPosition(rs.getInt("vacancy_position"));

                li.add(ae);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return li;
    }

    @Override
    public Map<String, String> getAuthorityList(String offcode) {
        Map<String, String> map = new HashMap<String, String>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = this.dataSource.getConnection();
            ps = con.prepareStatement(" SELECT b.spc,d.post,ARRAY_TO_STRING(ARRAY[c.INITIALS, c.F_NAME, c.M_NAME, c.L_NAME], ' ') EMPNAME from g_office a, g_spc b, emp_mast c, g_post d "
                    + "  where a.co_ddo_code=b.off_code and b.spc=c.cur_spc and b.gpc=d.post_code and a.off_code=? and d.isauthority='Y'");
            ps.setString(1, offcode);
            rs = ps.executeQuery();
            while (rs.next()) {
                map.put(rs.getString("spc"), rs.getString("post") + ", (" + rs.getString("EMPNAME") + ")");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, ps);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return map;
    }

    @Override
    public void approvedAER(int taskId) {
        Connection con = null;
        PreparedStatement pst1 = null;
        String sql = "";
        try {
            con = this.dataSource.getConnection();

            pst1 = con.prepareStatement("UPDATE TASK_MASTER SET STATUS_ID=?, APPLY_TO=? WHERE TASK_ID=?");

            pst1.setInt(1, 67);//Completed
            pst1.setString(2, null);
            pst1.setInt(3, taskId);
            pst1.executeUpdate();
            DataBaseFunctions.closeSqlObjects(pst1);

            sql = "UPDATE aer_report_submit SET status=? WHERE taskid=?";
            pst1 = con.prepareStatement(sql);
            pst1.setString(1, "COMP");
            pst1.setInt(2, taskId);
            pst1.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst1);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    @Override
    public String getAERStatus(int taskId) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        String submitted = "N";
        try {
            con = this.dataSource.getConnection();

            String sql = "SELECT STATUS_ID FROM TASK_MASTER WHERE TASK_ID=?";
            pst = con.prepareStatement(sql);
            pst.setInt(1, taskId);
            rs = pst.executeQuery();
            if (rs.next()) {
                if (rs.getInt("STATUS_ID") == 68) {
                    submitted = "R";
                } else if (rs.getInt("STATUS_ID") == 67) {
                    submitted = "Y";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return submitted;
    }

    @Override
    public void revertAER(int taskId, String serverfilePath, String fileName) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        String offCode = "";
        String fy = "";
        try {
            con = this.dataSource.getConnection();

            String sql = "SELECT off_code,fy FROM aer_report_submit WHERE taskid=?";
            pst = con.prepareStatement(sql);
            pst.setInt(1, taskId);
            rs = pst.executeQuery();
            if (rs.next()) {
                offCode = rs.getString("off_code");
                fy = rs.getString("fy");
            }

            DataBaseFunctions.closeSqlObjects(pst);

            sql = "UPDATE TASK_MASTER SET STATUS_ID=?, PENDING_AT=?,APPLY_TO=?,PENDING_SPC=? WHERE TASK_ID=?";
            pst = con.prepareStatement(sql);
            pst.setInt(1, 68);
            pst.setString(2, null);
            pst.setString(3, null);
            pst.setString(4, null);
            pst.setInt(5, taskId);
            pst.executeUpdate();

            DataBaseFunctions.closeSqlObjects(pst);

            sql = "UPDATE aer_report_submit SET status=? WHERE taskid=?";
            pst = con.prepareStatement(sql);
            pst.setString(1, "REV");
            pst.setInt(2, taskId);
            pst.executeUpdate();

            DataBaseFunctions.closeSqlObjects(pst);

            System.out.println("Off Code is: " + offCode);
            System.out.println("Financial Year is: " + fy);

            sql = "DELETE FROM annual_establish_report WHERE off_code=? and financial_year=?";
            pst = con.prepareStatement(sql);
            pst.setString(1, offCode);
            pst.setString(2, fy);
            pst.executeUpdate();

            Path file = Paths.get(serverfilePath, fileName);
            if (Files.exists(file)) {
                Files.deleteIfExists(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }

    }

    @Override
    public List getScheduleIIData(String offCode) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        List data = new ArrayList();

        ScheduleIIBean scbean = null;
        try {
            con = this.dataSource.getConnection();

            String sql = "select PAY_SCALE from g_spc where off_code=? and PAY_SCALE is not null GROUP BY PAY_SCALE order by PAY_SCALE asc";
            pst = con.prepareStatement(sql);
            pst.setString(1, offCode);
            rs = pst.executeQuery();
            while (rs.next()) {
                if (rs.getString("PAY_SCALE") != null && !rs.getString("PAY_SCALE").equals("")) {
                    scbean = new ScheduleIIBean();
                    scbean.setPayscale(rs.getString("PAY_SCALE"));
                    scbean.setTeacherSanctionedStrengthPlan(getSanctionedStrength(offCode, rs.getString("PAY_SCALE"), "T", "P"));
                    scbean.setTeacherSanctionedStrengthNonPlan(getSanctionedStrength(offCode, rs.getString("PAY_SCALE"), "T", "NP"));
                    scbean.setTeacherSanctionedStrengthTotal(scbean.getTeacherSanctionedStrengthPlan() + scbean.getTeacherSanctionedStrengthNonPlan());
                    scbean.setOthersSanctionedStrengthPlan(getSanctionedStrength(offCode, rs.getString("PAY_SCALE"), "NT", "P"));
                    scbean.setOthersSanctionedStrengthNonPlan(getSanctionedStrength(offCode, rs.getString("PAY_SCALE"), "NT", "NP"));
                    scbean.setOthersSanctionedStrengthTotal(scbean.getOthersSanctionedStrengthPlan() + scbean.getOthersSanctionedStrengthNonPlan());
                    scbean.setTotalPlan(scbean.getTeacherSanctionedStrengthPlan() + scbean.getOthersSanctionedStrengthPlan());
                    scbean.setTotalNonPlan(scbean.getTeacherSanctionedStrengthNonPlan() + scbean.getOthersSanctionedStrengthNonPlan());
                    scbean.setTotalSanctionedStrength(scbean.getTeacherSanctionedStrengthTotal() + scbean.getOthersSanctionedStrengthTotal());
                    data.add(scbean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return data;
    }

    private int getSanctionedStrength(String offCode, String payscale, String isTeacher, String isPlan) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        int count = 0;
        try {
            con = this.dataSource.getConnection();

            String sql = "select count(*) cnt from g_spc where off_code=? and pay_scale=? and (IFUCLEAN!='Y' OR IFUCLEAN IS NULL)"
                    + " AND is_sanctioned='Y' and is_teaching_post=? and is_plan=?";
            pst = con.prepareStatement(sql);
            pst.setString(1, offCode);
            pst.setString(2, payscale);
            pst.setString(3, isTeacher);
            pst.setString(4, isPlan);
            rs = pst.executeQuery();
            if (rs.next()) {
                count = rs.getInt("cnt");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return count;
    }

    public List departmentWiseAerStatus(String finYear) {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        DepartmentWiseAerStatus dwas = null;
        List deptWiseAerStatus = new ArrayList();
        String submittedAer = "";
        String approvedAer = "";
        String noOfDdo = "";
        try {
            con = this.dataSource.getConnection();
            String sql = "SELECT * FROM G_DEPARTMENT WHERE IF_ACTIVE='Y'";
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                dwas = new DepartmentWiseAerStatus();
                dwas.setDeptName(rs.getString("DEPARTMENT_NAME"));
                dwas.setDeptCode(rs.getString("DEPARTMENT_CODE"));
                noOfDdo = getNoOfDdo(rs.getString("DEPARTMENT_CODE"));
                submittedAer = getSubmittedAER(rs.getString("DEPARTMENT_CODE"), finYear);
                approvedAer = getApprovedAER(rs.getString("DEPARTMENT_CODE"), finYear);

                if (noOfDdo != null && !noOfDdo.equals("")) {
                    dwas.setNoOfDDO(getNoOfDdo(rs.getString("DEPARTMENT_CODE")));;
                } else {
                    dwas.setNoOfDDO("0");
                }
                if (submittedAer != null && !submittedAer.equals("")) {
                    dwas.setNoAerSubmitted(getSubmittedAER(rs.getString("DEPARTMENT_CODE"), finYear));
                } else {
                    dwas.setNoAerSubmitted("0");
                }
                if (approvedAer != null && !approvedAer.equals("")) {
                    dwas.setNoOfAerAproved(getApprovedAER(rs.getString("DEPARTMENT_CODE"), finYear));
                } else {
                    dwas.setNoOfAerAproved("0");
                }
                deptWiseAerStatus.add(dwas);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return deptWiseAerStatus;

    }

    public String getSubmittedAER(String deptCode, String finYear) {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        String noOfAreSubmitted = "";
        try {

            con = this.dataSource.getConnection();
            String sql = "SELECT AERSUBMIT.OFF_CODE,PENDING FROM(SELECT * FROM G_OFFICE WHERE DEPARTMENT_CODE='" + deptCode + "' ) DEPT INNER JOIN (SELECT A.OFF_CODE as OFF_CODE, \n"
                    + "       COUNT(case when STATUS = 'YES' then A.OFF_CODE end) as PENDING\n"
                    + "FROM AER_REPORT_SUBMIT A WHERE A.FY='" + finYear + "' GROUP BY A.OFF_CODE)AERSUBMIT ON AERSUBMIT.OFF_CODE=DEPT.OFF_CODE";
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            if (rs.next()) {
                noOfAreSubmitted = rs.getString("PENDING");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return noOfAreSubmitted;
    }

    public String getApprovedAER(String deptCode, String finYear) {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        String noOfApprovedAer = "";
        try {

            con = this.dataSource.getConnection();
            String sql = "SELECT AERSUBMIT.OFF_CODE,APPROVED FROM(SELECT * FROM G_OFFICE WHERE DEPARTMENT_CODE='" + deptCode + "' ) DEPT INNER JOIN (SELECT A.OFF_CODE as OFF_CODE, \n"
                    + "       COUNT(case when STATUS = 'COMP' then A.OFF_CODE end)APPROVED\n"
                    + "FROM AER_REPORT_SUBMIT A WHERE A.FY='" + finYear + "' GROUP BY A.OFF_CODE)AERSUBMIT ON AERSUBMIT.OFF_CODE=DEPT.OFF_CODE";
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            if (rs.next()) {
                noOfApprovedAer = rs.getString("APPROVED");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return noOfApprovedAer;
    }

    public String getNoOfDdo(String deptCode) {
        String noOfDdo = "";
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = this.dataSource.getConnection();
            String sql = "SELECT COUNT(*)NOOFDDO FROM G_OFFICE WHERE DEPARTMENT_CODE='" + deptCode + "' AND IS_DDO='Y'  GROUP BY DEPARTMENT_CODE";
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            if (rs.next()) {
                noOfDdo = rs.getString("NOOFDDO");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return noOfDdo;
    }

    public List aerSubmittedOfficeList(String deptCode) {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        DepartmentWiseAerStatus dwas = null;
        List offList = new ArrayList();
        try {
            con = this.dataSource.getConnection();
            String sql = "SELECT OFF_NAME FROM(SELECT OFF_CODE,OFF_NAME FROM G_OFFICE WHERE DEPARTMENT_CODE='"+deptCode+"' ) OFFICE INNER JOIN \n"
                    + "(SELECT DISTINCT OFF_CODE FROM AER_REPORT_SUBMIT WHERE FY='2016-17')AERREPORT ON OFFICE.OFF_CODE=AERREPORT.OFF_CODE";
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                dwas=new DepartmentWiseAerStatus();
                dwas.setOffName(rs.getString("OFF_NAME"));
                offList.add(dwas);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return offList;
    }

    @Override
    public List getFinancialYearList() {
        List li = new ArrayList();
        try {
            int fystartyear = 0;
            String currentFinancialYear = "";
            int year = Calendar.getInstance().get(Calendar.YEAR);

            int month = Calendar.getInstance().get(Calendar.MONTH) + 1;

            if (month <= 3) {
                currentFinancialYear = (year - 1) + "-" + Integer.toString(year).substring(2, 4);
                fystartyear = (year - 1);
            } else {
                currentFinancialYear = year + "-" + Integer.toString(year + 1).substring(2, 4);
                fystartyear = year;
            }
            li.add(currentFinancialYear);
            currentFinancialYear = (fystartyear - 1) + "-" + Integer.toString(fystartyear).substring(2, 4);
            li.add(currentFinancialYear);

            currentFinancialYear = (fystartyear - 2) + "-" + Integer.toString(fystartyear - 1).substring(2, 4);
            li.add(currentFinancialYear);

            currentFinancialYear = (fystartyear - 3) + "-" + Integer.toString(fystartyear - 2).substring(2, 4);
            li.add(currentFinancialYear);

            currentFinancialYear = (fystartyear - 4) + "-" + Integer.toString(fystartyear - 3).substring(2, 4);
            li.add(currentFinancialYear);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return li;
    }

}
