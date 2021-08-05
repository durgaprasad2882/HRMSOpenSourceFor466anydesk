/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.incrementProposal;

import hrms.SelectOption;
import hrms.common.CommonFunctions;
import hrms.common.DataBaseFunctions;
import hrms.common.GetUserAttribute;
import hrms.model.incrementProposal.IncrementProposal;
import hrms.model.incrementProposal.ProposalAttr;
import hrms.model.login.Users;
import hrms.model.master.SubstantivePost;
import hrms.model.task.TaskBean;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 *
 * @author Surendra
 */
public class IncrementProposalDAOImpl implements IncrementProposalDAO {

    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void saveProposal(IncrementProposal incr) {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    @Override
    public void addProposedEmployee(String emp[], int year, int month, int propMastId) {
        Connection con = null;
        PreparedStatement ps2 = null;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.DATE, 1);
        cal.set(Calendar.MONTH, month);

        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        String startTime = dateFormat.format(cal.getTime());
        try {
            Date outputDate = dateFormat.parse(startTime);
            con = this.dataSource.getConnection();
            if (emp != null) {
                ps2 = con.prepareStatement("UPDATE EMP_MAST SET DATE_OF_NINCR=?, PAY_DATE=? WHERE EMP_ID=?");
                for (int i = 0; i < emp.length; i++) {
                    ps2.setTimestamp(1, new Timestamp(outputDate.getTime()));
                    ps2.setTimestamp(2, new Timestamp(outputDate.getTime()));
                    ps2.setString(3, emp[i]);
                    ps2.execute();
                }
            }
            if (propMastId > 0) {
                saveProposalDetailList(emp, propMastId, year, month);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(ps2);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    public List getEmployeeList(String offCode, int year, int month) {
        Connection con = null;
        PreparedStatement ps2 = null;
        ResultSet rs = null;
        List emplist = new ArrayList();
        try {
            con = this.dataSource.getConnection();
            ps2 = con.prepareStatement("SELECT EMP_ID,GPF_NO,POST,DATE_OF_NINCR, ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME,L_NAME], ' ') EMPNAME,F_NAME,PAY_DATE FROM EMP_MAST EMP "
                    + "   INNER JOIN G_SPC GSPC ON EMP.CUR_SPC=GSPC.SPC "
                    + "   INNER JOIN G_POST GPOST ON GSPC.GPC=GPOST.POST_CODE AND CUR_OFF_CODE=? "
                    + "   AND (EXTRACT(YEAR FROM DATE_OF_NINCR)!=? or DATE_OF_NINCR is null) AND (EXTRACT(MONTH FROM DATE_OF_NINCR)!=? or DATE_OF_NINCR is null) ORDER BY F_NAME");

            ps2.setString(1, offCode);
            ps2.setInt(2, year);
            ps2.setInt(3, month);
            rs = ps2.executeQuery();
            Users emp = null;
            SubstantivePost sup = null;
            while (rs.next()) {
                emp = new Users();
                emp.setEmpId(rs.getString("EMP_ID"));
                emp.setFullName(rs.getString("EMPNAME"));
                emp.setGpfno(rs.getString("GPF_NO"));
                emp.setDateOfnincr(rs.getDate("DATE_OF_NINCR"));
                emp.setPaydate(rs.getDate("PAY_DATE"));
                sup = new SubstantivePost();
                sup.setSpn(rs.getString("POST"));
                emp.setSubstantivePost(sup);
                emplist.add(emp);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(ps2);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return emplist;
    }

    @Override
    public List proposalList(String offCode, int page, int rows) {
        Connection con = null;
        PreparedStatement ps2 = null;
        ResultSet rs = null;
        List li = new ArrayList();
        IncrementProposal incrprop = null;
        String[] monthName = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        int firstpage = (page > 1) ? ((page - 1) * rows) + 1 : 0;
        try {
            con = this.dataSource.getConnection();
            String sql = "SELECT PROPOSAL_ID,OFF_CODE,PROPOSAL_FOR_MONTH,PROPOSAL_FOR_YEAR,approved_order_no,approved_order_date,TASK_ID,STATUS_NAME,last_updated_on FROM ("
                    + " SELECT PROPOSAL_ID,OFF_CODE,PROPOSAL_FOR_MONTH,PROPOSAL_FOR_YEAR,approved_order_no,approved_order_date,TASK_MASTER.TASK_ID,STATUS_ID,last_updated_on FROM ( "
                    + " SELECT PROPOSAL_ID,OFF_CODE,PROPOSAL_FOR_MONTH,PROPOSAL_FOR_YEAR,approved_order_no,approved_order_date,TASK_ID,last_updated_on FROM HW_INCREMENT_PROPOSAL_MASTER WHERE OFF_CODE=?) HW_INCREMENT_PROPOSAL_MASTER"
                    + " LEFT OUTER JOIN TASK_MASTER ON HW_INCREMENT_PROPOSAL_MASTER.TASK_ID=TASK_MASTER.TASK_ID) HW_INCREMENT_PROPOSAL_MASTER "
                    + " LEFT OUTER JOIN G_PROCESS_STATUS ON HW_INCREMENT_PROPOSAL_MASTER.STATUS_ID=G_PROCESS_STATUS.STATUS_ID ORDER BY last_updated_on DESC limit ? offset ? ";
            
            ps2 = con.prepareStatement(sql);

            ps2.setString(1, offCode);
            ps2.setInt(2, rows);
            ps2.setInt(3, firstpage);
            rs = ps2.executeQuery();
            while (rs.next()) {
                incrprop = new IncrementProposal();
                incrprop.setProposalId(rs.getInt("PROPOSAL_ID"));
                incrprop.setProposalMonth(rs.getInt("PROPOSAL_FOR_MONTH"));
                incrprop.setProposalYear(rs.getInt("PROPOSAL_FOR_YEAR"));
                TaskBean tbean = new TaskBean();
                tbean.setTaskid(rs.getInt("TASK_ID"));
                incrprop.setTask(tbean);
                incrprop.setOrderDate(rs.getDate("approved_order_date"));
                incrprop.setOrderno(rs.getString("approved_order_no"));
                incrprop.setLastUpdatedOn(rs.getDate("last_updated_on"));
                incrprop.setProcessStatusName(rs.getString("STATUS_NAME"));
                li.add(incrprop);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(ps2);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return li;
    }

    public static String getFutureAmt(double basic) {

        double incramt = basic *.03;

        double total = incramt;
        DecimalFormat df = new DecimalFormat("0");
        String formate = df.format(total);
        double finalValue = Double.parseDouble(formate);

        String finalval = "";

        int lastdigit = Integer.parseInt(formate.substring(formate.length() - 1));

        if (lastdigit > 0) {
            String str = formate.substring(0, formate.length() - 1);
            str = str + 9;
            finalval = CommonFunctions.getNextString(str);
        }
        if (finalval == null || finalval.equals("")) {
            finalval = "0";
        }
        return finalval;
    }

    @Override
    public List getBenificiaryList(String offcode, int year) {
        List li = null;
        Connection con = null;
        PreparedStatement ps = null;
        String sql = "SELECT EMP_ID, FROM EMP_MAST WHERE CUR_OFF_CODE ='" + offcode + "' AND EMP_ID NOT IN (SELECT EMP_ID FROM (SELECT PROPOSAL_ID FROM HW_INCREMENT_PROPOSAL_MASTER WHERE OFF_CODE='" + offcode + "' AND PROPOSAL_FOR_YEAR=" + year + ")  HW_INCREMENT_PROPOSAL_MASTER "
                + "INNER JOIN HW_INCREMENT_PROPOSAL_DETAIL ON HW_INCREMENT_PROPOSAL_MASTER.PROPOSAL_ID=HW_INCREMENT_PROPOSAL_DETAIL.PROPOSAL_ID)";
        try {
            con = this.dataSource.getConnection();
            ps = con.prepareStatement(sql);
            ps.execute();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(ps);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return li;
    }

    @Override
    public int saveProposalList(String offCode, String loggedinEmpId, String spc, int month, int year) {
        Connection con = null;
        PreparedStatement ps = null;
        int maxCode_MASTER = 0;

        Calendar cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        String startTime = dateFormat.format(cal.getTime());
        try {

            Date outputDate = dateFormat.parse(startTime);

            con = this.dataSource.getConnection();

            maxCode_MASTER = CommonFunctions.getMaxCode(con, "HW_INCREMENT_PROPOSAL_MASTER", "PROPOSAL_ID");
            ps = con.prepareStatement("INSERT INTO HW_INCREMENT_PROPOSAL_MASTER (PROPOSAL_ID,OFF_CODE,PROPOSAL_FOR_MONTH,PROPOSAL_FOR_YEAR,CREATED_BY,CREATED_BY_SPC,LAST_UPDATED_ON) VALUES (?,?,?,?,?,?,?)");
            ps.setInt(1, maxCode_MASTER);
            ps.setString(2, offCode);
            ps.setInt(3, (month+1));
            ps.setInt(4, year);
            ps.setString(5, loggedinEmpId);
            ps.setString(6, spc);
            ps.setTimestamp(7, new Timestamp(outputDate.getTime()));
            ps.execute();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(ps);
            DataBaseFunctions.closeSqlObjects(con);

        }
        return maxCode_MASTER;
    }

    @Override
    public int saveProposalDetailList(String emp[], int maxCode_MASTER, int year, int month) {
        Connection con = null;
        PreparedStatement ps = null;
        PreparedStatement ps2 = null;
        PreparedStatement ps3 = null;
        ResultSet rs = null;
        int maxCode = 0;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.DATE, 1);
        cal.set(Calendar.MONTH, month);

        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        String startTime = dateFormat.format(cal.getTime());
        try {
            Date outputDate = dateFormat.parse(startTime);
            con = this.dataSource.getConnection();
            if (emp != null) {

                ps = con.prepareStatement("INSERT INTO HW_INCREMENT_PROPOSAL_DETAIL (PROPOSAL_DETAIL_ID,PROPOSAL_ID,EMP_ID,CURRENT_BASIC,INCREMENT_AMT,INCREMENTED_BASIC,date_of_present_increment,date_of_present_pay) VALUES(?,?,?,?,?,?,?,?)");
                ps2 = con.prepareStatement("UPDATE EMP_MAST SET DATE_OF_NINCR=? WHERE EMP_ID=?");
                ps3 = con.prepareStatement("SELECT PAY_DATE,DATE_OF_NINCR,CUR_BASIC_SALARY,GP FROM EMP_MAST WHERE EMP_ID=?");

                for (int i = 0; i < emp.length; i++) {
                    ps3.setString(1, emp[i]);
                    rs = ps3.executeQuery();
                    if (rs.next()) {

                        maxCode = CommonFunctions.getMaxCode(con, "HW_INCREMENT_PROPOSAL_DETAIL", "PROPOSAL_DETAIL_ID");
                        String finalincr = getFutureAmt(rs.getDouble("CUR_BASIC_SALARY"));
                        String futurebasic = (Integer.parseInt(finalincr) + rs.getInt("CUR_BASIC_SALARY")) + "";
                        ps.setInt(1, maxCode);
                        ps.setInt(2, maxCode_MASTER);
                        ps.setString(3, emp[i]);
                        ps.setInt(4, rs.getInt("CUR_BASIC_SALARY"));
                        ps.setInt(5, Integer.parseInt(finalincr));
                        ps.setInt(6, Integer.parseInt(futurebasic));
                        ps.setTimestamp(7, rs.getTimestamp("DATE_OF_NINCR"));
                        ps.setTimestamp(8, rs.getTimestamp("PAY_DATE"));
                        ps.execute();

                        ps2.setTimestamp(1, new Timestamp(outputDate.getTime()));
                        ps2.setString(2, emp[i]);
                        ps2.execute();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(ps, ps2);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return maxCode_MASTER;
    }

    public List getEmployeeWisePostList(String offcode) {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        List li = new ArrayList();
        SubstantivePost post = null;
        try {
            con = this.dataSource.getConnection();

            ps = con.prepareStatement("SELECT SPC,ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME,L_NAME], ' ') EMPNAME,POST FROM ( SELECT SPC,POST FROM (SELECT SPC,GPC FROM G_SPC WHERE OFF_CODE=?) G_SPC INNER JOIN G_POST ON G_SPC.GPC=G_POST.POST_CODE) G_SPC"
                    + " INNER JOIN EMP_MAST ON G_SPC.SPC=EMP_MAST.CUR_SPC ");
            ps.setString(1, offcode);
            rs = ps.executeQuery();
            while (rs.next()) {
                post = new SubstantivePost();
                post.setSpc(rs.getString("SPC"));
                post.setEmpname(rs.getString("EMPNAME"));
                post.setPostname(rs.getString("POST"));
                li.add(post);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(ps);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return li;
    }

    @Override
    public void deleteSelectedProposalFromList(int proposaldetailId, String empId) {
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = this.dataSource.getConnection();

            ps = con.prepareStatement("DELETE FROM HW_INCREMENT_PROPOSAL_DETAIL WHERE PROPOSAL_DETAIL_ID=?");
            ps.setInt(1, proposaldetailId);
            ps.execute();

            ps = con.prepareStatement("UPDATE EMP_MAST SET DATE_OF_NINCR =null WHERE EMP_ID=?");
            ps.setString(1, empId);
            ps.execute();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(ps);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    @Override
    public List getProposedEmployeeList(String offcode, int year, int month) {
        List li = new ArrayList();
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        String sql = null;
        try {
            con = this.dataSource.getConnection();
            sql = "SELECT C.EMP_ID,C.GPF_NO,C.EMPNAME,C.CUR_BASIC_SALARY,C.CUR_SALARY,C.GP,C.PAY_DATE,C.DATE_OF_NINCR,C.CUR_SPC,F_NAME,D.GPC,E.POST FROM "
                    + "                     (SELECT EMP_ID,GPF_NO,ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME,M_NAME,L_NAME], ' ') EMPNAME,F_NAME,CUR_BASIC_SALARY,CUR_SALARY,GP,PAY_DATE,DATE_OF_NINCR,CUR_SPC FROM EMP_MAST  WHERE CUR_OFF_CODE='" + offcode + "' AND "
                    + "                     extract(month from DATE_OF_NINCR) =" + month + " and extract(year from DATE_OF_NINCR) =" + year + "  AND EMP_ID NOT IN ( "
                    + "                     SELECT B.EMP_ID FROM HW_INCREMENT_PROPOSAL_MASTER A, HW_INCREMENT_PROPOSAL_DETAIL B "
                    + "                     WHERE A.PROPOSAL_ID=B.PROPOSAL_ID  "
                    + "                     AND A.OFF_CODE='" + offcode + "' AND A.PROPOSAL_FOR_MONTH =" + month + " AND A.PROPOSAL_FOR_YEAR=" + year
                    + "                    ))  C, G_SPC D, G_POST E WHERE C.CUR_SPC=D.SPC AND D.GPC=E.POST_CODE ORDER BY C.F_NAME";
            st = con.createStatement();
            rs = st.executeQuery(sql);
            ProposalAttr pro = null;
            while (rs.next()) {
                pro = new ProposalAttr();
                pro.setProposaldetailId(0);
                pro.setEmpId(rs.getString("EMP_ID"));
                pro.setEmpname(rs.getString("EMPNAME"));
                pro.setGpfno(rs.getString("GPF_NO"));
                pro.setPost(rs.getString("POST"));
                pro.setNextincr(CommonFunctions.getFormattedOutputDate3(rs.getDate("DATE_OF_NINCR")));
                pro.setPayscale(rs.getString("CUR_SALARY"));
                pro.setPresentpay(((Double) Math.ceil(rs.getDouble("CUR_BASIC_SALARY"))).intValue()+"");
                String incramt = getFutureAmt(rs.getDouble("CUR_BASIC_SALARY"));
                int futurebasic = ((Double) Math.ceil(rs.getDouble("CUR_BASIC_SALARY") + Integer.parseInt(incramt))).intValue();
                pro.setFuturepay(futurebasic + "");
                pro.setPresentpaydate(CommonFunctions.getFormattedOutputDate3(rs.getDate("PAY_DATE")));
                li.add(pro);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return li;
    }

    @Override
    public List getFinalProposedList(int proposalId) {
        List li = new ArrayList();
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        String sql = "";
        try {
            sql = "SELECT PROPOSAL_DETAIL_ID,EMP_ID,GPF_NO,EMPNAME,CUR_SALARY,CURRENT_BASIC,INCREMENT_AMT,INCREMENTED_BASIC,GP,PAY_DATE,DATE_OF_NINCR,F_NAME,POST,date_of_present_increment,date_of_present_pay FROM ( "
                    + "               SELECT PROPOSAL_DETAIL_ID,EMP_ID,GPF_NO,EMPNAME,CURRENT_BASIC,CUR_SALARY,INCREMENT_AMT,INCREMENTED_BASIC,HW_INCREMENT_PROPOSAL_DETAIL.GP,PAY_DATE,DATE_OF_NINCR,F_NAME,CUR_SPC,GPC,date_of_present_increment,date_of_present_pay FROM ( "
                    + "               SELECT EMP_MAST.EMP_ID,GPF_NO,CUR_SALARY, "
                    + "               ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME,L_NAME], ' ') EMPNAME, "
                    + "               F_NAME,CUR_SPC, "
                    + "               GP,PAY_DATE,DATE_OF_NINCR,INCREMENTED_BASIC,CURRENT_BASIC,INCREMENT_AMT,PROPOSAL_DETAIL_ID,date_of_present_increment,date_of_present_pay FROM ("
                    + "               SELECT PROPOSAL_DETAIL_ID,EMP_ID,CURRENT_BASIC,INCREMENT_AMT,INCREMENTED_BASIC,date_of_present_increment,date_of_present_pay FROM HW_INCREMENT_PROPOSAL_DETAIL WHERE PROPOSAL_ID=" + proposalId + ") HW_INCREMENT_PROPOSAL_DETAIL INNER JOIN EMP_MAST ON HW_INCREMENT_PROPOSAL_DETAIL.EMP_ID=EMP_MAST.EMP_ID) HW_INCREMENT_PROPOSAL_DETAIL "
                    + "               LEFT OUTER JOIN G_SPC ON HW_INCREMENT_PROPOSAL_DETAIL.CUR_SPC=G_SPC.SPC) HW_INCREMENT_PROPOSAL_DETAIL"
                    + "               LEFT OUTER JOIN G_POST ON HW_INCREMENT_PROPOSAL_DETAIL.GPC=G_POST.POST_CODE ORDER BY F_NAME ";

            System.out.println(sql);
            con = this.dataSource.getConnection();
            st = con.createStatement();
            rs = st.executeQuery(sql);

            ProposalAttr pro = null;
            while (rs.next()) {
                pro = new ProposalAttr();
                pro.setProposaldetailId(rs.getInt("PROPOSAL_DETAIL_ID"));
                pro.setEmpId(rs.getString("EMP_ID"));
                pro.setGpfno(rs.getString("GPF_NO"));
                pro.setEmpname(rs.getString("EMPNAME"));
                pro.setNextincr(CommonFunctions.getFormattedOutputDate3(rs.getDate("date_of_present_increment")));
                pro.setPresentpay(rs.getString("CURRENT_BASIC"));
                pro.setPayscale(rs.getString("CUR_SALARY"));
                pro.setPresentpaydate(CommonFunctions.getFormattedOutputDate3(rs.getDate("date_of_present_pay")));
                pro.setFuturepay(rs.getString("INCREMENTED_BASIC"));
                pro.setPost(rs.getString("POST"));
                li.add(pro);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, st);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return li;
    }

    @Override
    public void deleteProposalDetail(int propmastId) {
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = this.dataSource.getConnection();
            ps = con.prepareStatement("DELETE FROM HW_INCREMENT_PROPOSAL_DETAIL WHERE PROPOSAL_ID=?");
            ps.setInt(1, propmastId);
            ps.execute();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(ps);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }    

    @Override
    public void submitProposal(int propmastId, String loggedempid, String loggedspc, String authSpc) {

        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = this.dataSource.getConnection();

            Calendar cal = Calendar.getInstance();
            DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
            String startTime = dateFormat.format(cal.getTime());
            Date initiatedDt = dateFormat.parse(startTime);

            int mcode = CommonFunctions.getMaxCode(con, "TASK_MASTER", "TASK_ID");

            pst = con.prepareStatement("INSERT INTO TASK_MASTER(TASK_ID, PROCESS_ID, INITIATED_BY, INITIATED_ON, STATUS_ID, PENDING_AT,APPLY_TO,INITIATED_SPC,PENDING_SPC) Values (?,?,?,?,?,?,?,?,?)");
            pst.setInt(1, mcode);
            pst.setInt(2, 4);
            pst.setString(3, loggedempid);
            pst.setTimestamp(4, new Timestamp(initiatedDt.getTime()));
            pst.setInt(5, 10);
            pst.setString(6, GetUserAttribute.getEmpId(con, authSpc));
            pst.setString(7, GetUserAttribute.getEmpId(con, authSpc));
            pst.setString(8, loggedspc);
            pst.setString(9, authSpc);
            pst.executeUpdate();

            pst = con.prepareStatement("UPDATE HW_INCREMENT_PROPOSAL_MASTER SET TASK_ID=? WHERE PROPOSAL_ID=?");
            pst.setInt(1, mcode);
            pst.setInt(2, propmastId);
            pst.execute();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    @Override
    public void authorityAction(int propmastId, int statusid, String note, String authspc) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int taskid = 0;
        Calendar cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");

        try {
            String startTime = dateFormat.format(cal.getTime());
            Date actionDate = dateFormat.parse(startTime);

            con = this.dataSource.getConnection();
            ps = con.prepareStatement("SELECT TASK_ID FROM HW_INCREMENT_PROPOSAL_MASTER WHERE PROPOSAL_ID=?");
            ps.setInt(1, propmastId);
            rs = ps.executeQuery();
            if (rs.next()) {
                taskid = rs.getInt("TASK_ID");
            }

            ps = con.prepareStatement("UPDATE HW_INCREMENT_PROPOSAL_MASTER SET APPROVED_AUTHORITY=?, APPROVE_DATE=? WHERE PROPOSAL_ID=? ");
            ps.setString(1, authspc);
            ps.setTimestamp(2, new Timestamp(actionDate.getTime()));
            ps.setInt(3, propmastId);
            ps.execute();

            ps = con.prepareStatement("UPDATE TASK_MASTER SET STATUS_ID=?, NOTE=? WHERE TASK_ID=? ");
            ps.setInt(1, statusid);
            ps.setString(2, note);
            ps.setInt(3, taskid);
            ps.execute();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs);
            DataBaseFunctions.closeSqlObjects(ps);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    @Override
    public String getOfficeName(String offcode) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String offname = "";

        try {

            con = this.dataSource.getConnection();
            ps = con.prepareStatement("SELECT OFF_EN FROM G_OFFICE WHERE OFF_CODE=?");
            ps.setString(1, offcode);
            rs = ps.executeQuery();
            if (rs.next()) {
                offname = rs.getString("OFF_EN");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(ps);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return offname;
    }

    @Override
    public void updateOrderInfo(int propmastId, String ordno, String orderDate) {
        Calendar cal = Calendar.getInstance();
        Connection con = null;
        PreparedStatement ps = null;
        Statement st = null;
        ResultSet rs = null;
        String sqlQuery = "SELECT a.proposal_for_month, a.proposal_for_year, b.emp_id,b.date_of_present_increment from "
                + "	 HW_INCREMENT_PROPOSAL_MASTER a, HW_INCREMENT_PROPOSAL_DETAIL b WHERE a.PROPOSAL_ID=b.PROPOSAL_ID and a.PROPOSAL_ID=" + propmastId;
        try {

            con = this.dataSource.getConnection();
            ps = con.prepareStatement("UPDATE HW_INCREMENT_PROPOSAL_MASTER SET approved_order_no=?, approved_order_date=?, is_sb_updated='N' WHERE PROPOSAL_ID=?");
            ps.setString(1, ordno);
            ps.setTimestamp(2, new Timestamp(new Date(orderDate).getTime()));
            ps.setInt(3, propmastId);
            ps.execute();

            ps = con.prepareStatement("UPDATE EMP_MAST SET PAY_DATE=?, DATE_OF_NINCR=? WHERE EMP_ID=?");

            st = con.createStatement();
            rs = st.executeQuery(sqlQuery);
            while (rs.next()) {
                cal.set((rs.getInt("proposal_for_year") + 1), rs.getInt("proposal_for_month"), 1);
                Timestamp time = new Timestamp(cal.getTimeInMillis());
                ps.setTimestamp(1, rs.getTimestamp("date_of_present_increment"));
                ps.setTimestamp(2, new Timestamp(time.getTime()));
                ps.setString(3, rs.getString("emp_id"));
                ps.execute();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(ps);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    @Override
    public ProposalAttr getordnoSpcEtc(int proposalMastId) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ProposalAttr pi = new ProposalAttr();
        try {

            con = this.dataSource.getConnection();
            ps = con.prepareStatement("select post,approved_order_no,approved_order_date from ( "
                    + "select gpc,approved_order_no,approved_order_date from ( select approved_order_no,approved_order_date,approved_authority from HW_INCREMENT_PROPOSAL_MASTER where proposal_id=?) HW_INCREMENT_PROPOSAL_MASTER\n"
                    + "left outer join g_spc on HW_INCREMENT_PROPOSAL_MASTER.approved_authority=g_spc.spc) incrprop "
                    + "left outer join g_post on incrprop.gpc=g_post.post_code");

            ps.setInt(1, proposalMastId);
            rs = ps.executeQuery();
            if (rs.next()) {
                pi.setOrdno(rs.getString("approved_order_no"));
                pi.setOrderDate(CommonFunctions.getFormattedOutputDate3(rs.getDate("approved_order_date")));
                pi.setPost(rs.getString("post"));

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return pi;
    }

    @Override
    public int getProposalMasterId(int taskId) {
        int proposalId = 0;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = this.dataSource.getConnection();
            ps = con.prepareStatement("SELECT PROPOSAL_ID FROM HW_INCREMENT_PROPOSAL_MASTER WHERE TASK_ID=?");
            ps.setInt(1, taskId);
            rs = ps.executeQuery();
            if (rs.next()) {
                proposalId = rs.getInt("PROPOSAL_ID");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs);
            DataBaseFunctions.closeSqlObjects(ps);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return proposalId;
    }

}
