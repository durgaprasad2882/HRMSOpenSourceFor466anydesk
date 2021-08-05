/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.payroll.arrear;

import hrms.common.CalendarCommonMethods;
import hrms.common.DataBaseFunctions;
import hrms.model.payroll.arrear.ArrAqDtlsChildModel;
import hrms.model.payroll.arrear.ArrAqDtlsModel;
import hrms.model.payroll.arrear.ArrAqList;
import hrms.model.payroll.arrear.ArrAqMastModel;
import hrms.model.payroll.arrear.PayRevisionIncrement;
import hrms.model.payroll.arrear.PayRevisionOption;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.annotation.Resource;
import javax.sql.DataSource;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.NumberFormats;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.Number;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Manas
 */
public class ArrmastDAOImpl implements ArrmastDAO {

    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public String saveArrmastdata(ArrAqMastModel arrAqMastModel) {
        Connection con = null;
        PreparedStatement pstmt = null;
        String aqslNo = "";
        try {
            con = dataSource.getConnection();
            aqslNo = arrAqMastModel.getBillNo() + "_" + arrAqMastModel.getPayMonth() + "_" + arrAqMastModel.getPayYear() + "_" + arrAqMastModel.getSlno();
            pstmt = con.prepareStatement("INSERT INTO ARR_MAST (AQSL_NO,EMP_ID,OFF_CODE,BILL_NO,ARR_TYPE,CHOICE_DATE,EMP_NAME,AQ_GROUP,AQ_GROUP_DESC,P_MONTH,P_YEAR,CUR_DESG,CUR_BASIC,EMP_TYPE) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
            pstmt.setString(1, aqslNo);
            pstmt.setString(2, arrAqMastModel.getEmpCode());
            pstmt.setString(3, arrAqMastModel.getOffCode());
            pstmt.setInt(4, arrAqMastModel.getBillNo());
            pstmt.setString(5, "PAY");
            pstmt.setDate(6, new java.sql.Date(arrAqMastModel.getChoiceDate().getTime()));
            pstmt.setString(7, arrAqMastModel.getEmpName());
            pstmt.setBigDecimal(8, arrAqMastModel.getAqGroup());
            pstmt.setString(9, arrAqMastModel.getAqGroupDesc());
            pstmt.setInt(10, arrAqMastModel.getPayMonth());
            pstmt.setInt(11, arrAqMastModel.getPayYear());
            pstmt.setString(12, arrAqMastModel.getCurDesg());
            pstmt.setInt(13, arrAqMastModel.getCurBasic());
            pstmt.setString(14, arrAqMastModel.getEmpType());
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return aqslNo;
    }

    @Override
    public String addEmployeeToBill(ArrAqMastModel arrAqMastModel) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String aqslNo = "";
        try {
            int dataExist = 0;
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT * FROM ARR_MAST WHERE BILL_NO=?");
            pstmt.setInt(1, arrAqMastModel.getBillNo());
            rs = pstmt.executeQuery();
            if (rs.next()) {
                dataExist = 1;
            }
            if (dataExist == 1) {
                pstmt = con.prepareStatement("SELECT MAX(CAST(split_part(AQSL_NO,'_',4) as INTEGER))+1  AS slno FROM ARR_MAST WHERE BILL_NO=?");
                pstmt.setInt(1, arrAqMastModel.getBillNo());
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    arrAqMastModel.setSlno(rs.getInt("slno"));
                }
            } else {
                arrAqMastModel.setSlno(1);
            }
            pstmt = con.prepareStatement("SELECT * FROM BILL_MAST WHERE BILL_NO=?");
            pstmt.setInt(1, arrAqMastModel.getBillNo());
            rs = pstmt.executeQuery();
            if (rs.next()) {
                arrAqMastModel.setOffCode(rs.getString("OFF_CODE"));
                arrAqMastModel.setAqGroup(rs.getBigDecimal("BILL_GROUP_ID"));
                arrAqMastModel.setPayMonth(rs.getInt("AQ_MONTH"));
                arrAqMastModel.setPayYear(rs.getInt("AQ_YEAR"));
            }
            pstmt = con.prepareStatement("SELECT F_NAME,M_NAME,L_NAME, POST, CUR_BASIC_SALARY FROM EMP_MAST "
                    + " INNER JOIN G_SPC ON EMP_MAST.CUR_SPC = G_SPC.SPC "
                    + " INNER JOIN G_POST ON G_SPC.GPC=G_POST.POST_CODE WHERE EMP_ID=? ");
            pstmt.setString(1, arrAqMastModel.getEmpCode());
            rs = pstmt.executeQuery();
            if (rs.next()) {
                arrAqMastModel.setCurBasic(rs.getInt("CUR_BASIC_SALARY"));
                arrAqMastModel.setCurDesg(rs.getString("POST"));
                arrAqMastModel.setEmpName((StringUtils.defaultString(rs.getString("F_NAME")) + " " + StringUtils.defaultString(rs.getString("M_NAME")) + " " + StringUtils.defaultString(rs.getString("L_NAME"))).replaceAll("\\s+", " "));
            }
            System.out.println("arrAqMastModel.getAqGroup()" + arrAqMastModel.getAqGroup() + "    arrAqMastModel.getPayMonth()" + arrAqMastModel.getPayMonth());
            arrAqMastModel.setChoiceDate(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(arrAqMastModel.getInputChoiceDate()));
            System.out.println("&&&&&" + arrAqMastModel.getChoiceDate().getTime());
            aqslNo = arrAqMastModel.getAqGroup() + "_" + arrAqMastModel.getPayMonth() + "_" + arrAqMastModel.getPayYear() + "_" + arrAqMastModel.getSlno();
            System.out.println("aqslNo:" + aqslNo);
            pstmt = con.prepareStatement("INSERT INTO ARR_MAST (AQSL_NO,EMP_ID,OFF_CODE,BILL_NO,ARR_TYPE,CHOICE_DATE,EMP_NAME,AQ_GROUP,AQ_GROUP_DESC,P_MONTH,P_YEAR,CUR_DESG,CUR_BASIC) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?) ");
            pstmt.setString(1, aqslNo);
            pstmt.setString(2, arrAqMastModel.getEmpCode());
            pstmt.setString(3, arrAqMastModel.getOffCode());
            pstmt.setInt(4, arrAqMastModel.getBillNo());
            pstmt.setString(5, "PAY");
            pstmt.setDate(6, new java.sql.Date(arrAqMastModel.getChoiceDate().getTime()));
            pstmt.setString(7, arrAqMastModel.getEmpName());
            pstmt.setBigDecimal(8, arrAqMastModel.getAqGroup());
            pstmt.setString(9, arrAqMastModel.getAqGroupDesc());
            pstmt.setInt(10, arrAqMastModel.getPayMonth());
            pstmt.setInt(11, arrAqMastModel.getPayYear());
            pstmt.setString(12, arrAqMastModel.getCurDesg());
            pstmt.setInt(13, arrAqMastModel.getCurBasic());
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return aqslNo;
    }

    @Override
    public int getCalcUniqueNo(String aqslno) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int calcUniqueNo = 0;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("select max(calc_unique_no) as calc_unique_no from arr_dtls WHERE aqsl_no =?");
            pstmt.setString(1, aqslno);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                calcUniqueNo = rs.getInt("calc_unique_no");
            }
            calcUniqueNo++;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return calcUniqueNo;
    }

    @Override
    public void saveArrdtlsdata(ArrAqDtlsModel[] arrAqDtlsModels, int calc_unique_no) {
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("INSERT INTO ARR_DTLS (AQSL_NO,P_MONTH,P_YEAR,AD_TYPE,ALREADY_PAID, TO_BE_PAID, DRAWN_VIDE_BILLNO, REF_AQSL_NO, CALC_UNIQUE_NO)VALUES (?,?,?,?,?,?,?,?,?) ");
            for (int i = 0; i < arrAqDtlsModels.length; i++) {
                ArrAqDtlsModel arrAqDtlsModel = arrAqDtlsModels[i];
                pstmt.setString(1, arrAqDtlsModel.getAqslno());
                pstmt.setInt(2, arrAqDtlsModel.getPayMonth());
                pstmt.setInt(3, arrAqDtlsModel.getPayYear());
                pstmt.setString(4, arrAqDtlsModel.getAdType());
                pstmt.setInt(5, arrAqDtlsModel.getDrawnAMt());
                pstmt.setInt(6, arrAqDtlsModel.getDueAmt());
                pstmt.setString(7, arrAqDtlsModel.getDrawnBillNo());
                pstmt.setString(8, arrAqDtlsModel.getRefaqslno());
                pstmt.setInt(9, calc_unique_no);
                pstmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    public void deleteArrdtlsdata(String aqslno) {
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("DELETE FROM ARR_DTLS WHERE AQSL_NO=?");
            pstmt.setString(1, aqslno);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    @Override
    public List getArrearBillDtls(int billno, String aqMonth, String aqYear) {

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List arrEmpList = new ArrayList();
        ArrAqMastModel arrAqBean = null;
        int slNo = 1;

        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT AQSL_NO, EMP_NAME,CUR_DESG, FULL_ARREAR_PAY, ARREAR_PAY,CPF_HEAD,INCTAX,PT,REMARK,GPF_NO FROM ARR_MAST "
                    + "INNER JOIN EMP_MAST ON ARR_MAST.EMP_ID = EMP_MAST.EMP_ID WHERE BILL_NO = ? order by EMP_NAME");
            pstmt.setInt(1, billno);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                arrAqBean = new ArrAqMastModel();
                arrAqBean.setSlno(slNo);
                arrAqBean.setAqSlNo(rs.getString("AQSL_NO"));
                arrAqBean.setEmpName(rs.getString("EMP_NAME"));
                arrAqBean.setCurDesg(rs.getString("CUR_DESG"));
                arrAqBean.setGrandTotArr100(rs.getInt("FULL_ARREAR_PAY"));
                arrAqBean.setGrandTotArr40(rs.getInt("ARREAR_PAY"));
                arrAqBean.setCpfHead(rs.getInt("CPF_HEAD") + "");
                arrAqBean.setIncomeTaxAmt(rs.getInt("INCTAX") + "");
                arrAqBean.setProfessionalTax(rs.getInt("PT"));
                arrAqBean.setRemark(rs.getString("REMARK"));
                arrAqBean.setGpfAccNo(rs.getString("GPF_NO"));
                arrEmpList.add(arrAqBean);
                slNo++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return arrEmpList;
    }

    @Override
    public List getArrearAcquaintance(int billNo) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet res = null;
        List arrAqList = new ArrayList();

        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("select emp_id, emp_name, cur_desg, aqsl_no, cur_basic, inctax, cpf_head, arrear_pay, pt from arr_mast "
                    + "where bill_no = ? order by emp_name");
            pstmt.setInt(1, billNo);
            res = pstmt.executeQuery();
            while (res.next()) {

                ArrAqMastModel aqBean = new ArrAqMastModel();

                aqBean.setEmpCode(res.getString("EMP_ID"));
                aqBean.setEmpName(res.getString("EMP_NAME"));
                aqBean.setCurDesg(res.getString("CUR_DESG"));
                aqBean.setAqSlNo(res.getString("aqsl_no"));
                aqBean.setArrearpay(res.getInt("arrear_pay"));
                aqBean.setCurBasic(res.getInt("cur_basic"));
                aqBean.setIncomeTaxAmt(res.getString("inctax"));
                aqBean.setCpfHead(res.getString("cpf_head"));
                aqBean.setPt(res.getString("pt"));

                arrAqList.add(aqBean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(res, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return arrAqList;
    }

    @Override
    public List getArrearAcquaintanceDtls(String aqSlNo) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet res = null;
        List arrAqDtlsList = new ArrayList();

        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT p_month, p_year, ad_type, to_be_paid, already_paid, drawn_vide_billno FROM arr_dtls WHERE aqsl_no =? order by p_year,p_month,ad_type");
            pstmt.setString(1, aqSlNo);
            res = pstmt.executeQuery();
            int i = 0;
            ArrAqDtlsModel arrAqDtlsBean = new ArrAqDtlsModel();
            while (res.next()) {
                i++;

                int pmonth = res.getInt("p_month");
                int pyear = res.getInt("p_year");

                arrAqDtlsBean.setAqslno(aqSlNo);
                arrAqDtlsBean.setPayMonth(pmonth);
                arrAqDtlsBean.setPayMonthName(new CalendarCommonMethods().getMonthAsString(pmonth));
                arrAqDtlsBean.setPayYear(pyear);

                if (res.getString("ad_type").equals("PAY")) {
                    arrAqDtlsBean.setDuePayAmt(res.getInt("to_be_paid"));
                    arrAqDtlsBean.setDrawnPayAmt(res.getInt("already_paid"));

                } else if (res.getString("ad_type").equals("GP")) {
                    arrAqDtlsBean.setDueGpAmt(res.getInt("to_be_paid"));
                    arrAqDtlsBean.setDrawnGpAmt(res.getInt("already_paid"));

                } else if (res.getString("ad_type").equals("DA")) {
                    arrAqDtlsBean.setDueDaAmt(res.getInt("to_be_paid"));
                    arrAqDtlsBean.setDrawnDaAmt(res.getInt("already_paid"));

                }
                arrAqDtlsBean.setDrawnBillNo(res.getString("drawn_vide_billno"));
                int totDueAmt = arrAqDtlsBean.getDuePayAmt() + arrAqDtlsBean.getDueGpAmt() + arrAqDtlsBean.getDueDaAmt();
                arrAqDtlsBean.setDueTotalAmt(totDueAmt);

                int totDrawnAmt = arrAqDtlsBean.getDrawnPayAmt() + arrAqDtlsBean.getDrawnGpAmt() + arrAqDtlsBean.getDrawnDaAmt();
                arrAqDtlsBean.setDrawnTotalAmt(totDrawnAmt);

                int arrear100 = totDueAmt - totDrawnAmt;
                arrAqDtlsBean.setArrear100(arrear100);

                double arrear40 = Math.ceil((arrear100 * 0.4));
                arrAqDtlsBean.setArrear40(arrear40);

                double arrear60 = Math.ceil((arrear100 * 0.6));
                arrAqDtlsBean.setArrear60(arrear60);

                if (i == 3) {
                    arrAqDtlsList.add(arrAqDtlsBean);
                    arrAqDtlsBean = new ArrAqDtlsModel();
                    i = 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(res, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return arrAqDtlsList;
    }

    @Override
    public ArrAqMastModel getArrearAcquaintanceData(String aqSlNo) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet res = null;
        int grandTotArr100 = 0;
        ArrAqMastModel arrAqMastBean = new ArrAqMastModel();

        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("select emp_id, emp_name, CUR_DESG, aqsl_no, p_month, p_year, inctax, cpf_head, pt, bill_no from arr_mast  where aqsl_no = ? ");
            pstmt.setString(1, aqSlNo);
            res = pstmt.executeQuery();
            if (res.next()) {

                arrAqMastBean.setEmpCode(res.getString("emp_id"));
                arrAqMastBean.setEmpName(res.getString("emp_name"));
                arrAqMastBean.setCurDesg(res.getString("cur_desg"));
                arrAqMastBean.setIncomeTaxAmt(res.getString("inctax"));
                arrAqMastBean.setCpfHead(res.getString("cpf_head"));
                arrAqMastBean.setPt(res.getString("pt"));
                arrAqMastBean.setBillNo(res.getInt("bill_no"));
            }
            List arrAqDtlsList = getArrearAcquaintanceDtls(aqSlNo);

            ArrAqDtlsModel obj = null;
            if (arrAqDtlsList != null && arrAqDtlsList.size() > 0) {
                obj = new ArrAqDtlsModel();
                for (int i = 0; i < arrAqDtlsList.size(); i++) {
                    obj = (ArrAqDtlsModel) arrAqDtlsList.get(i);

                    grandTotArr100 = grandTotArr100 + obj.getArrear100();
                    arrAqMastBean.setAqSlNo(aqSlNo);
                }
            }
            arrAqMastBean.setGrandTotArr100(grandTotArr100);
            arrAqMastBean.setGrandTotArr40(Math.round(grandTotArr100 * 0.4));
            arrAqMastBean.setGrandTotArr60(grandTotArr100 - Math.round(grandTotArr100 * 0.4));

            arrAqMastBean.setArrDetails(arrAqDtlsList);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(res, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return arrAqMastBean;
    }

    @Override
    public List getArrearAqHeaderData(int billNo) {
        Connection con = null;
        Statement stmt = null;
        ResultSet res = null;
        List arrAqList = new ArrayList();

        try {
            con = dataSource.getConnection();
            stmt = con.createStatement();

            String arrHdrQry = "select bill_desc,from_month, from_year, to_month, to_year from bill_mast where bill_no = '" + billNo + "'";
            res = stmt.executeQuery(arrHdrQry);
            while (res.next()) {

                ArrAqMastModel aqBean = new ArrAqMastModel();

                aqBean.setBillDesc(res.getString("bill_desc"));
                aqBean.setFromMonth(res.getString("from_month"));
                aqBean.setFromYear(res.getString("from_year"));
                aqBean.setToMonth(res.getString("to_month"));
                aqBean.setToYear(res.getString("to_year"));

                arrAqList.add(aqBean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(res, stmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return arrAqList;
    }

    @Override
    public int updateArrMastItData(int billNo, String aqSlNo, int incTax) {
        Connection con = null;
        PreparedStatement pstmt = null;
        int res = 0;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("update arr_mast set inctax=? where aqsl_no=? and bill_no=?");

            pstmt.setInt(1, incTax);
            pstmt.setString(2, aqSlNo);
            pstmt.setInt(3, billNo);

            res = pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return res;
    }

    @Override
    public int updateArrMastCpfData(int billNo, String aqSlNo, int cpfAmt) {
        Connection con = null;
        PreparedStatement pstmt = null;
        int res = 0;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("update arr_mast set cpf_head=? where aqsl_no=? and bill_no=?");

            pstmt.setInt(1, cpfAmt);
            pstmt.setString(2, aqSlNo);
            pstmt.setInt(3, billNo);

            res = pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return res;
    }

    @Override
    public int updateArrMastPtData(int billNo, String aqSlNo, int ptAmt) {
        Connection con = null;
        PreparedStatement pstmt = null;
        int res = 0;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("update arr_mast set pt=? where aqsl_no=? and bill_no=?");

            pstmt.setInt(1, ptAmt);
            pstmt.setString(2, aqSlNo);
            pstmt.setInt(3, billNo);

            res = pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return res;
    }

    @Override
    public int reCalculateArrMast(int billNo) {
        Connection con = null;
        PreparedStatement pstmt1 = null;
        int res = 0;
        try {
            con = dataSource.getConnection();
            pstmt1 = con.prepareStatement("update arr_mast set full_arrear_pay = getgross_arrear(aqsl_no) WHERE bill_no = ? ");
            pstmt1.setInt(1, billNo);
            pstmt1.executeUpdate();

            pstmt1 = con.prepareStatement("update arr_mast set arrear_pay = round(full_arrear_pay*0.4) WHERE bill_no = ? ");
            pstmt1.setInt(1, billNo);
            pstmt1.executeUpdate();

            pstmt1 = con.prepareStatement("UPDATE BILL_MAST SET GROSS_AMT = getbilltotgross_arrear(BILL_NO) WHERE BILL_NO = ? ");
            pstmt1.setInt(1, billNo);
            pstmt1.executeUpdate();

            pstmt1 = con.prepareStatement("UPDATE BILL_MAST SET DED_AMT = getbilltotded_arrear(BILL_NO) WHERE BILL_NO = ? ");
            pstmt1.setInt(1, billNo);
            res = pstmt1.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pstmt1);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return res;
    }

    @Override
    public int updateArrDtlsData(ArrAqDtlsModel arrAqDtlsModel) {
        Connection con = null;
        PreparedStatement pstmt = null;
        int res = 0;
        try {

            con = dataSource.getConnection();
            pstmt = con.prepareStatement("update arr_dtls set already_paid = ? ,to_be_paid = ?, drawn_vide_billno=? where aqsl_no = ? and p_month = ? and p_year = ? and ad_type=?");

            pstmt.setInt(1, arrAqDtlsModel.getDrawnPayAmt());
            pstmt.setInt(2, arrAqDtlsModel.getDuePayAmt());
            pstmt.setString(3, arrAqDtlsModel.getDrawnBillNo());
            pstmt.setString(4, arrAqDtlsModel.getAqslno());
            pstmt.setInt(5, arrAqDtlsModel.getPayMonth());
            pstmt.setInt(6, arrAqDtlsModel.getPayYear());
            pstmt.setString(7, "PAY");
            res = pstmt.executeUpdate();

            pstmt = con.prepareStatement("update arr_dtls set already_paid = ? ,to_be_paid = ?, drawn_vide_billno=? where aqsl_no = ? and p_month = ? and p_year = ? and ad_type=?");

            pstmt.setInt(1, arrAqDtlsModel.getDrawnGpAmt());
            pstmt.setInt(2, arrAqDtlsModel.getDueGpAmt());
            pstmt.setString(3, arrAqDtlsModel.getDrawnBillNo());
            pstmt.setString(4, arrAqDtlsModel.getAqslno());
            pstmt.setInt(5, arrAqDtlsModel.getPayMonth());
            pstmt.setInt(6, arrAqDtlsModel.getPayYear());
            pstmt.setString(7, "GP");
            res = pstmt.executeUpdate();

            pstmt = con.prepareStatement("update arr_dtls set already_paid = ? ,to_be_paid = ?, drawn_vide_billno=? where aqsl_no = ? and p_month = ? and p_year = ? and ad_type=?");

            pstmt.setInt(1, arrAqDtlsModel.getDrawnDaAmt());
            pstmt.setInt(2, arrAqDtlsModel.getDueDaAmt());
            pstmt.setString(3, arrAqDtlsModel.getDrawnBillNo());
            pstmt.setString(4, arrAqDtlsModel.getAqslno());
            pstmt.setInt(5, arrAqDtlsModel.getPayMonth());
            pstmt.setInt(6, arrAqDtlsModel.getPayYear());
            pstmt.setString(7, "DA");
            res = pstmt.executeUpdate();

            pstmt = con.prepareStatement("update arr_mast set full_arrear_pay = getgross_arrear(aqsl_no) WHERE aqsl_no=?");
            pstmt.setString(1, arrAqDtlsModel.getAqslno());
            pstmt.executeUpdate();
            pstmt = con.prepareStatement("update arr_mast set arrear_pay = round(full_arrear_pay*0.4) WHERE aqsl_no=?");
            pstmt.setString(1, arrAqDtlsModel.getAqslno());
            pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return res;
    }

    @Override
    public int deleteArrDtlsData(ArrAqDtlsModel arrDtlsBean) {
        Connection con = null;
        PreparedStatement pstmt = null;
        int deleted = 0;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("DELETE FROM arr_dtls WHERE aqsl_no = ? and p_month = ? and p_year = ?");
            pstmt.setString(1, arrDtlsBean.getAqslno());
            pstmt.setInt(2, arrDtlsBean.getPayMonth());
            pstmt.setInt(3, arrDtlsBean.getPayYear());
            System.out.println("aqslno:" + arrDtlsBean.getAqslno() + "      paymonth:" + arrDtlsBean.getPayMonth() + "    payyear:" + arrDtlsBean.getPayYear());
            deleted = pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return deleted;
    }

    @Override
    public ArrAqDtlsModel getArrearAcquaintanceDataList(String aqSlNo, int aqMonth, int aqYear) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet res = null;
        ArrAqDtlsModel arrAqDtlsBean = null;

        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("select * from arr_dtls where aqsl_no = ? and p_month= ? and p_year= ?");
            pstmt.setString(1, aqSlNo);
            pstmt.setInt(2, aqMonth);
            pstmt.setInt(3, aqYear);
            res = pstmt.executeQuery();
            int i = 0;
            arrAqDtlsBean = new ArrAqDtlsModel();
            while (res.next()) {

                int pmonth = res.getInt("p_month");
                int pyear = res.getInt("p_year");

                arrAqDtlsBean.setAqslno(aqSlNo);
                arrAqDtlsBean.setPayMonth(pmonth);
                //arrAqDtlsBean.setPayMonthName(new CalendarCommonMethods().getMonthAsString(pmonth));
                arrAqDtlsBean.setPayYear(pyear);

                if (res.getString("ad_type").equals("PAY")) {
                    arrAqDtlsBean.setDuePayAmt(res.getInt("to_be_paid"));
                    arrAqDtlsBean.setDrawnPayAmt(res.getInt("already_paid"));

                } else if (res.getString("ad_type").equals("GP")) {
                    arrAqDtlsBean.setDueGpAmt(res.getInt("to_be_paid"));
                    arrAqDtlsBean.setDrawnGpAmt(res.getInt("already_paid"));

                } else if (res.getString("ad_type").equals("DA")) {
                    arrAqDtlsBean.setDueDaAmt(res.getInt("to_be_paid"));
                    arrAqDtlsBean.setDrawnDaAmt(res.getInt("already_paid"));

                }
                arrAqDtlsBean.setDrawnBillNo(res.getString("drawn_vide_billno"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(res, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return arrAqDtlsBean;
    }

    @Override
    public int updateArrAqMastItData(int billNo, String aqSlNo, int incTax) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List getArrearAcqEmpDet(String billNo) {
        List list = null;
        List outlist = new ArrayList();
        ArrAqDtlsModel arrAqDtlsBean = null;
        ArrAqList arrempList = null;
        ResultSet rs = null;
        PreparedStatement pst = null;

        ResultSet rs1 = null;
        PreparedStatement pst1 = null;

        Connection con = null;
        String aqSlNo = null;
        try {

            con = dataSource.getConnection();
            pst1 = con.prepareStatement("SELECT DISTINCT ( a.aqsl_no),emp_name FROM arr_dtls b,arr_mast a WHERE a.aqsl_no=b.aqsl_no  AND a.bill_no = '" + billNo + "'  ");
            rs1 = pst1.executeQuery();
            while (rs1.next()) {
                list = new ArrayList();
                aqSlNo = rs1.getString("aqsl_no");

                pst = con.prepareStatement("SELECT p_month,p_year FROM arr_dtls WHERE  aqsl_no = '" + aqSlNo + "' GROUP BY p_year,p_month ORDER BY p_year,p_month ");
                rs = pst.executeQuery();
                while (rs.next()) {
                    arrAqDtlsBean = new ArrAqDtlsModel();
                    // arrAqDtlsBean.setPayMonth(rs.getInt("p_month"));
                    arrAqDtlsBean.setPayMonthName(new CalendarCommonMethods().getMonthAsString(rs.getInt("p_month")));
                    arrAqDtlsBean.setPayYear(rs.getInt("p_year"));
                    arrAqDtlsBean.setPayList(getArrearAcqEmpPayDetails(aqSlNo, rs.getInt("p_month"), rs.getInt("p_year")));
                    list.add(arrAqDtlsBean);
                    //System.out.println(rs.getInt("p_year"));
                }
                arrempList = new ArrAqList();
                arrempList.setEmpName(rs1.getString("emp_name"));
                arrempList.setEmpList(list);

                outlist.add(arrempList);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return outlist;
    }

    public List<ArrAqDtlsModel> getArrearAcqEmpPayDetails(String aqSlNo, int month, int year) {
        List listPay = null;
        ArrAqDtlsChildModel arrAqDtlsBean = null;
        ResultSet rs = null;
        PreparedStatement pst = null;
        Connection con = null;

        try {
            listPay = new ArrayList();
            con = dataSource.getConnection();
            //   System.out.println("SELECT ad_type,already_paid,to_be_paid FROM arr_dtls WHERE  aqsl_no = '" + aqSlNo + "' AND p_month='" + month + "' AND  p_year='" + year + "'  ORDER BY p_year,p_month");
            pst = con.prepareStatement("SELECT ad_type,already_paid,to_be_paid FROM arr_dtls WHERE  aqsl_no = '" + aqSlNo + "' AND p_month='" + month + "' AND  p_year='" + year + "'  ORDER BY p_year,p_month ");
            rs = pst.executeQuery();
            while (rs.next()) {
                arrAqDtlsBean = new ArrAqDtlsChildModel();
                arrAqDtlsBean.setAdType(rs.getString("ad_type"));
                arrAqDtlsBean.setAlreadyPaid(rs.getInt("already_paid"));
                arrAqDtlsBean.setToBePaid(rs.getInt("to_be_paid"));
                arrAqDtlsBean.setAqSlNo(aqSlNo);
                listPay.add(arrAqDtlsBean);
                // System.out.println(rs.getString("ad_type"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return listPay;
    }

    @Override
    public void downloadArrearAcqEmpExcel(OutputStream out, String offcode, WritableWorkbook workbook, String billNo) throws Exception {

        Connection con = null;
        List list = null;
        List outlist = new ArrayList();
        PreparedStatement pst = null;
        ResultSet rs = null;

        ResultSet rs1 = null;
        PreparedStatement pst1 = null;

        ArrAqDtlsModel arrAqDtlsBean = null;
        ArrAqDtlsChildModel arrChildBean = null;
        String aqSlNo = null;

        int row = 0;
        try {
            con = dataSource.getConnection();

            WritableSheet sheet = workbook.createSheet(offcode, 0);
            WritableFont headformat = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
            WritableFont textformat = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD);

            WritableCellFormat headcell3 = new WritableCellFormat(headformat);
            headcell3.setAlignment(Alignment.CENTRE);
            headcell3.setVerticalAlignment(VerticalAlignment.CENTRE);
            headcell3.setWrap(true);

            WritableCellFormat textcell = new WritableCellFormat(textformat);
            textcell.setAlignment(Alignment.CENTRE);
            textcell.setVerticalAlignment(VerticalAlignment.CENTRE);
            textcell.setWrap(true);
            textcell.setBorder(Border.ALL, BorderLineStyle.DOUBLE);

            WritableCellFormat headcell = new WritableCellFormat(headformat);
            headcell.setAlignment(Alignment.CENTRE);
            headcell.setVerticalAlignment(VerticalAlignment.CENTRE);
            headcell.setWrap(true);
            headcell.setBorder(Border.ALL, BorderLineStyle.DOUBLE);

            WritableCellFormat headcel2 = new WritableCellFormat(headformat);
            headcel2.setAlignment(Alignment.LEFT);
            headcel2.setVerticalAlignment(VerticalAlignment.CENTRE);
            headcel2.setWrap(true);
            headcel2.setBorder(Border.ALL, BorderLineStyle.DOUBLE);

            WritableFont cellformat = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD);

            WritableCellFormat innercell = new WritableCellFormat(NumberFormats.INTEGER);
            innercell.setAlignment(Alignment.CENTRE);
            innercell.setVerticalAlignment(VerticalAlignment.CENTRE);
            innercell.setWrap(true);

            row = row + 1;

            Label label = new Label(1, row, "ARREAR REPORT", headcell3);//column,row
            sheet.addCell(label);
            sheet.mergeCells(1, row, 15, row);

            Label childlabel = null;
            con = dataSource.getConnection();
            pst1 = con.prepareStatement("SELECT DISTINCT ( a.aqsl_no),emp_name FROM arr_dtls b,arr_mast a WHERE a.aqsl_no=b.aqsl_no  AND a.bill_no = '" + billNo + "'  ");
            rs1 = pst1.executeQuery();
            int cnt = 0;
            while (rs1.next()) {
                cnt++;
                list = new ArrayList();
                aqSlNo = rs1.getString("aqsl_no");

                pst = con.prepareStatement("SELECT p_month,p_year FROM arr_dtls WHERE aqsl_no = ? GROUP BY p_year,p_month ORDER BY p_year,p_month ");
                pst.setString(1, aqSlNo);
                rs = pst.executeQuery();

                int firstcol = 1;
                int lastcol = 3;
                row = cnt * 4;
                int printCol = 1;
                int empCount = 0;
                while (rs.next()) {
                    empCount = empCount + 1;
                    int headingCol = firstcol;
                    arrAqDtlsBean = new ArrAqDtlsModel();
                    // arrAqDtlsBean.setPayMonth(rs.getInt("p_month"));
                    arrAqDtlsBean.setPayMonthName(new CalendarCommonMethods().getMonthAsString(rs.getInt("p_month")));
                    arrAqDtlsBean.setPayYear(rs.getInt("p_year"));
                    arrAqDtlsBean.setPayList(getArrearAcqEmpPayDetails(aqSlNo, rs.getInt("p_month"), rs.getInt("p_year")));

                    //System.out.println("arrAqDtlsBean.getPayMonthName() is: " + arrAqDtlsBean.getPayMonthName());
                    if (cnt == 1) {
                        label = new Label(firstcol, row - 1, arrAqDtlsBean.getPayMonthName() + "-" + arrAqDtlsBean.getPayYear(), headcell3);//column,row
                        sheet.addCell(label);
                        sheet.mergeCells(firstcol, row - 1, lastcol, row - 1);
                        // row = row + 1;

                        childlabel = new Label(headingCol, row, "Type", headcell);//column,row
                        sheet.addCell(childlabel);

                        headingCol += 1;
                        childlabel = new Label(headingCol, row, "Already paid", headcell);//column,row
                        sheet.addCell(childlabel);

                        headingCol += 1;

                        childlabel = new Label(headingCol, row, "To Be paid", headcell);//column,row
                        sheet.addCell(childlabel);

                    }
                    row = row + 1;
                    if (empCount == 1) {
                        label = new Label(firstcol, row, rs1.getString("emp_name"), headcel2);//column,row
                        sheet.addCell(label);
                        sheet.mergeCells(firstcol, row, 60, row);
                    }

                    int childCol = printCol;
                    Number num = null;
                    row += 1;
                    List childArrList = arrAqDtlsBean.getPayList();

                    /*  childlabel = new Label(childCol, row,"Type", headcell);//column,row
                     sheet.addCell(childlabel);

                     childCol += 1;
                     childlabel = new Label(childCol, row,"ALready paid", headcell);//column,row
                     sheet.addCell(childlabel);

                     childCol += 1;

                     childlabel = new Label(childCol, row,"To Be paid", headcell);//column,row
                     sheet.addCell(childlabel);

                     childCol += 1;
                     row += 1;*/
                    if (childArrList != null && childArrList.size() > 0) {
                        for (int i = 0; i < arrAqDtlsBean.getPayList().size(); i++) {

                            arrChildBean = (ArrAqDtlsChildModel) childArrList.get(i);

                            childlabel = new Label(childCol, row, arrChildBean.getAdType(), textcell);//column,row
                            sheet.addCell(childlabel);

                            childCol += 1;

                            num = new Number(childCol, row, arrChildBean.getAlreadyPaid(), textcell);
                            sheet.addCell(num);

                            childCol += 1;

                            num = new Number(childCol, row, arrChildBean.getToBePaid(), textcell);//column,row
                            sheet.addCell(num);

                            childCol = printCol;

                            row += 1;
                        }
                    }
                    firstcol += 3;
                    lastcol += 3;

                    row = (cnt * 4);

                    printCol = printCol + 3;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    @Override
    public int deleteArrMastData(String aqSlNo, int billNo) {
        Connection con = null;
        PreparedStatement pstmt = null;
        int res = 0;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("delete from arr_mast where aqsl_no = ? and bill_no = ?");

            pstmt.setString(1, aqSlNo);
            pstmt.setInt(2, billNo);

            res = pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return res;
    }

    @Override
    public PayRevisionOption searchEmployee(String searchemp) {
        PayRevisionOption payRevisionOption = new PayRevisionOption();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet res = null;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT * FROM ARR_MAST WHERE EMP_ID=?");
            pstmt.setString(1, searchemp);
            res = pstmt.executeQuery();
            if (res.next()) {
                payRevisionOption.setMsgcode(1);
                payRevisionOption.setMessage("Employee Already Exist in another bill");
            }
            if (payRevisionOption.getMsgcode() == 0) {
                pstmt = con.prepareStatement("SELECT entered_date,PAYREV_FITTED_AMOUNT,is_approved FROM pay_revision_option WHERE emp_id = ?");
                pstmt.setString(1, searchemp);
                res = pstmt.executeQuery();
                if (res.next()) {
                    payRevisionOption.setChoiceDate(res.getDate("entered_date"));
                    payRevisionOption.setPayrevisionbasic(res.getInt("PAYREV_FITTED_AMOUNT"));
                    payRevisionOption.setIsapproved(res.getString("is_approved"));
                } else {
                    payRevisionOption.setMsgcode(2);
                    payRevisionOption.setMessage("Pay Fixation of this Employee is not Done in HRMS");
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(res);
            DataBaseFunctions.closeSqlObjects(pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return payRevisionOption;
    }

    @Override
    public void reprocessArrAqMast(String aqslno) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet res = null;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("delete from arr_dtls where aqsl_no=?");
            pstmt.setString(1, aqslno);
            pstmt.executeUpdate();

            ArrAqMastModel arrAqMastModel = getArrearAcquaintanceData(aqslno);
            PayRevisionOption po = getChoiceDate(arrAqMastModel.getEmpCode());
            if (po.getChoiceDate() != null) {
                Calendar choiceCal = Calendar.getInstance();
                choiceCal.setTime(po.getChoiceDate());
                int choiceYear = choiceCal.get(Calendar.YEAR);
                int choiceMonth = choiceCal.get(Calendar.MONTH);

                int year = choiceYear;
                choiceMonth = choiceMonth - 1;
                int calc_unique_no = 0;
                for (int month = choiceMonth; (month <= 11 && year == 2016) || (month <= 7 && year == 2017); month++) {
                    int dapercentage = 0;
                    calc_unique_no++;
                    if (month <= 5 && year == 2016) {
                        dapercentage = 0;
                    } else if (month > 5 && month <= 11 && year == 2016) {
                        dapercentage = 2;
                    } else if (month <= 5 && year == 2017) {
                        dapercentage = 4;
                    } else if (month > 5 && month <= 11 && year == 2017) {
                        dapercentage = 5;
                    }

                    System.out.println("month=" + month + "    year=" + year + "    dapercentage=" + dapercentage);

                    ArrAqDtlsModel[] arrAqDtlsModels = getAqDtlsModelFromAllowanceList(po, month, year, arrAqMastModel.getEmpCode(), dapercentage, aqslno);
                    if (month == 11) {
                        month = -1;
                        year = 2017;
                    }
                    saveArrdtlsdata(arrAqDtlsModels, calc_unique_no);
                }
                pstmt = con.prepareStatement("update arr_mast set full_arrear_pay = getgross_arrear(aqsl_no) WHERE aqsl_no=?");
                pstmt.setString(1, aqslno);
                pstmt.executeUpdate();
                pstmt = con.prepareStatement("update arr_mast set arrear_pay = round(full_arrear_pay*0.4) WHERE aqsl_no=?");
                pstmt.setString(1, aqslno);
                pstmt.executeUpdate();

            }
        } catch (SQLException e) {
            //status = "F";
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    @Override
    public PayRevisionOption getChoiceDate(String empCode) {
        PayRevisionOption po = new PayRevisionOption();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet res = null;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT entered_date,PAYREV_FITTED_AMOUNT FROM pay_revision_option WHERE emp_id = ? AND is_approved = 'Y'");
            pstmt.setString(1, empCode);
            res = pstmt.executeQuery();
            if (res.next()) {
                po.setChoiceDate(res.getDate("entered_date"));
                po.setPayrevisionbasic(res.getInt("PAYREV_FITTED_AMOUNT"));
            }
            pstmt = con.prepareStatement("SELECT incr_date,revised_basic FROM emp_pay_revised_increment_2016 WHERE emp_id=? order by incr_date");
            pstmt.setString(1, empCode);
            res = pstmt.executeQuery();
            List payRevisionIncrements = new ArrayList();
            while (res.next()) {
                PayRevisionIncrement princ = new PayRevisionIncrement();
                princ.setIncrementDate(res.getDate("incr_date"));
                princ.setIncrementedbasic(res.getInt("revised_basic"));
                payRevisionIncrements.add(princ);
            }
            po.setPayRevisionIncrements(payRevisionIncrements);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(res);
            DataBaseFunctions.closeSqlObjects(pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return po;
    }

    public int getRevisedPay(Date choiceDate, int month, int year, int[] contractualMatrix) {
        Calendar incCal = Calendar.getInstance();
        int revisedPay = 0;
        if (choiceDate != null) {
            incCal.setTime(choiceDate);
            int startYear = incCal.get(Calendar.YEAR);
            int startMonth = incCal.get(Calendar.MONTH);

            int endMonth = startMonth;
            int endYear = startYear + 1;
            if ((month >= startMonth && year == startYear) || (month <= endMonth && year == endYear)) {
                revisedPay = contractualMatrix[0];
            }
        }
        return revisedPay;
    }

    @Override
    public int[] getPayMatrix(int gp) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet res = null;
        int[] contractualMatrix = new int[6];
        try {
            System.out.println("gp:"+gp);
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT AMT FROM PAY_MATRIX_CONT_2017 WHERE GP=? ORDER BY YEAR");
            pstmt.setInt(1, gp);
            res = pstmt.executeQuery();
            int i = 0;
            while (res.next()) {
                contractualMatrix[i] = res.getInt("AMT");
                i++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(res);
            DataBaseFunctions.closeSqlObjects(pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return contractualMatrix;
    }

    @Override
    public ArrAqDtlsModel[] getAqDtlsModelFromAllowanceListForContractual(Date choiceDate, int month, int year, String empCode, int dapercentage, String aqslno, int[] contractualMatrix, int gp) {
        ArrayList<ArrAqDtlsModel> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet res = null;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT MON_BASIC,BILL_DESC,AQSL_NO FROM AQ_MAST INNER JOIN BILL_MAST ON AQ_MAST.BILL_NO = BILL_MAST.BILL_NO WHERE AQ_MAST.AQ_MONTH=? AND AQ_MAST.AQ_YEAR=? AND EMP_CODE=?");
            pstmt.setInt(1, month);
            pstmt.setInt(2, year);
            pstmt.setString(3, empCode);
            res = pstmt.executeQuery();

            String refaqslno = "";
            String billdesc = "";
            int revisedPay = getRevisedPay(choiceDate, month, year, contractualMatrix);
            int dbbasic = 0;
            if (res.next()) {
                ArrAqDtlsModel arrAqDtls = new ArrAqDtlsModel();
                arrAqDtls.setAqslno(aqslno);
                arrAqDtls.setPayMonth(month);
                arrAqDtls.setPayYear(year);
                arrAqDtls.setDrawnAMt(res.getInt("MON_BASIC"));
                arrAqDtls.setAdType("PAY");
                arrAqDtls.setRefaqslno(res.getString("AQSL_NO"));
                arrAqDtls.setDrawnBillNo(res.getString("BILL_DESC"));
                arrAqDtls.setDueAmt(revisedPay);
                dbbasic = arrAqDtls.getDrawnAMt();
                refaqslno = arrAqDtls.getRefaqslno();
                billdesc = arrAqDtls.getDrawnBillNo();
                list.add(arrAqDtls);
                System.out.println("Drawn gp:"+gp);
                arrAqDtls = new ArrAqDtlsModel();
                arrAqDtls.setAqslno(aqslno);
                arrAqDtls.setPayMonth(month);
                arrAqDtls.setPayYear(year);
                arrAqDtls.setDrawnAMt(gp);
                arrAqDtls.setAdType("GP");
                arrAqDtls.setRefaqslno(res.getString("AQSL_NO"));
                arrAqDtls.setDrawnBillNo(billdesc);
                arrAqDtls.setDueAmt(gp);
                list.add(arrAqDtls);

                arrAqDtls = new ArrAqDtlsModel();
                arrAqDtls.setAqslno(aqslno);
                arrAqDtls.setPayMonth(month);
                arrAqDtls.setPayYear(year);
                arrAqDtls.setDrawnAMt(0);
                arrAqDtls.setAdType("DA");
                arrAqDtls.setRefaqslno(res.getString("AQSL_NO"));
                arrAqDtls.setDrawnBillNo(billdesc);
                arrAqDtls.setDueAmt(0);
                list.add(arrAqDtls);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(res);
            DataBaseFunctions.closeSqlObjects(pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        ArrAqDtlsModel arrAqDtlsModels[] = list.toArray(new ArrAqDtlsModel[list.size()]);
        return arrAqDtlsModels;
    }

    @Override
    public ArrAqDtlsModel[] getAqDtlsModelFromAllowanceList(PayRevisionOption po, int month, int year, String empCode, double dapercentage, String aqslno) {
        ArrayList<ArrAqDtlsModel> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet res = null;
        try {

            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT MON_BASIC,BILL_DESC,AQSL_NO FROM AQ_MAST INNER JOIN BILL_MAST ON AQ_MAST.BILL_NO = BILL_MAST.BILL_NO WHERE AQ_MAST.AQ_MONTH=? AND AQ_MAST.AQ_YEAR=? AND EMP_CODE=?");
            pstmt.setInt(1, month);
            pstmt.setInt(2, year);
            pstmt.setString(3, empCode);
            res = pstmt.executeQuery();

            String refaqslno = "";
            String billdesc = "";
            int revisedPay = getRevisedPay(po, month, year);
            int dbbasic = 0;
            if (res.next()) {
                ArrAqDtlsModel arrAqDtls = new ArrAqDtlsModel();
                arrAqDtls.setAqslno(aqslno);
                arrAqDtls.setPayMonth(month);
                arrAqDtls.setPayYear(year);
                arrAqDtls.setDrawnAMt(res.getInt("MON_BASIC"));
                arrAqDtls.setAdType("PAY");
                arrAqDtls.setRefaqslno(res.getString("AQSL_NO"));
                arrAqDtls.setDrawnBillNo(res.getString("BILL_DESC"));
                arrAqDtls.setDueAmt(revisedPay);
                dbbasic = arrAqDtls.getDrawnAMt();
                refaqslno = arrAqDtls.getRefaqslno();
                billdesc = arrAqDtls.getDrawnBillNo();
                list.add(arrAqDtls);
            } else {
                ArrAqDtlsModel arrAqDtls = new ArrAqDtlsModel();
                arrAqDtls.setAqslno(aqslno);
                arrAqDtls.setPayMonth(month);
                arrAqDtls.setPayYear(year);
                arrAqDtls.setDrawnAMt(0);
                arrAqDtls.setAdType("PAY");
                refaqslno = "ADJUSTED";
                arrAqDtls.setRefaqslno(refaqslno);
                arrAqDtls.setDrawnBillNo(null);
                arrAqDtls.setDrawnAMt(0);
                arrAqDtls.setDueAmt(revisedPay);
                list.add(arrAqDtls);
            }
            String aqDTLS = "AQ_DTLS";

            if (year < 2020) {
                aqDTLS = "hrmis.AQ_DTLS1";
            }

            pstmt = con.prepareStatement("SELECT AQSL_NO,AD_CODE,AD_AMT FROM " + aqDTLS + " WHERE AQSL_NO=? AND AD_CODE='GP' ");
            pstmt.setString(1, refaqslno);
            res = pstmt.executeQuery();
            int gp = 0;

            if (res.next()) {
                ArrAqDtlsModel arrAqDtls = new ArrAqDtlsModel();
                arrAqDtls.setAqslno(aqslno);
                arrAqDtls.setPayMonth(month);
                arrAqDtls.setPayYear(year);
                arrAqDtls.setDrawnAMt(res.getInt("AD_AMT"));
                arrAqDtls.setAdType(res.getString("AD_CODE"));
                arrAqDtls.setRefaqslno(refaqslno);
                arrAqDtls.setDrawnBillNo(billdesc);
                arrAqDtls.setDueAmt(0);
                gp = arrAqDtls.getDrawnAMt();
                list.add(arrAqDtls);
            } else {

                ArrAqDtlsModel arrAqDtls = new ArrAqDtlsModel();
                arrAqDtls.setAqslno(aqslno);
                arrAqDtls.setPayMonth(month);
                arrAqDtls.setPayYear(year);
                arrAqDtls.setDrawnAMt(0);
                arrAqDtls.setAdType("GP");
                arrAqDtls.setRefaqslno(refaqslno);
                arrAqDtls.setDrawnBillNo(null);
                arrAqDtls.setDrawnAMt(0);
                arrAqDtls.setDueAmt(0);
                list.add(arrAqDtls);
            }

            pstmt = con.prepareStatement("SELECT AQSL_NO,AD_CODE,AD_AMT FROM " + aqDTLS + " WHERE AQSL_NO=? AND AD_CODE='DA'");
            pstmt.setString(1, refaqslno);
            res = pstmt.executeQuery();
            if (res.next()) {

                ArrAqDtlsModel arrAqDtls = new ArrAqDtlsModel();
                arrAqDtls.setAqslno(aqslno);
                arrAqDtls.setPayMonth(month);
                arrAqDtls.setPayYear(year);
                arrAqDtls.setDrawnAMt(res.getInt("AD_AMT"));
                arrAqDtls.setAdType(res.getString("AD_CODE"));
                arrAqDtls.setRefaqslno(res.getString("AQSL_NO"));
                arrAqDtls.setDrawnBillNo(billdesc);

                /**/
                int correctDA = getPrevDA(arrAqDtls.getDrawnAMt(), dbbasic + gp, month, year);
                arrAqDtls.setDrawnAMt(correctDA);
                /**/
                
                arrAqDtls.setDueAmt(new Long(Math.round((revisedPay / 100) * dapercentage)).intValue());

                list.add(arrAqDtls);
            } else {
                ArrAqDtlsModel arrAqDtls = new ArrAqDtlsModel();
                arrAqDtls.setAqslno(aqslno);
                arrAqDtls.setPayMonth(month);
                arrAqDtls.setPayYear(year);
                arrAqDtls.setDrawnAMt(0);
                arrAqDtls.setAdType("DA");
                arrAqDtls.setRefaqslno(refaqslno);
                arrAqDtls.setDrawnBillNo(null);
                arrAqDtls.setDrawnAMt(0);
                arrAqDtls.setDueAmt(0);
                list.add(arrAqDtls);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(res);
            DataBaseFunctions.closeSqlObjects(pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        ArrAqDtlsModel arrAqDtlsModels[] = list.toArray(new ArrAqDtlsModel[list.size()]);
        return arrAqDtlsModels;
    }

    public int getPrevDA(int currentda, int dbbasic, int month, int year) {
        int correctDa = 0;
        if (month >= 0 && month <= 5 && year == 2016) {
            int tcorrectDa = new Double(Math.ceil(dbbasic * 1.25)).intValue();
            if (tcorrectDa > currentda) {
                correctDa = tcorrectDa;
            } else {
                correctDa = currentda;
            }
        } else if ((month > 5 && month <= 11 && year == 2016) || (month >= 0 && month <= 7 && year == 2017)) {
            int tcorrectDa = new Double(Math.ceil(dbbasic * 1.32)).intValue();
            if (tcorrectDa > currentda) {
                correctDa = tcorrectDa;
            } else {
                correctDa = currentda;
            }
        }
        return correctDa;
    }

    public int getRevisedPay(PayRevisionOption po, int month, int year) {
        int revisedPay = po.getPayrevisionbasic();
        List payRevisionIncrements = po.getPayRevisionIncrements();
        for (int i = 0; i < payRevisionIncrements.size(); i++) {
            PayRevisionIncrement princ = (PayRevisionIncrement) payRevisionIncrements.get(i);
            Date incrementDate = princ.getIncrementDate();
            Calendar incCal = Calendar.getInstance();
            if (incrementDate != null) {
                incCal.setTime(incrementDate);
                int incYear = incCal.get(Calendar.YEAR);
                int incMonth = incCal.get(Calendar.MONTH);
                if ((month >= incMonth && year >= incYear) || (month < incMonth && year > incYear)) {
                    revisedPay = princ.getIncrementedbasic();
                }
            }
            /*if(month >= incMonth && year >= incYear){
             revisedPay = princ.getIncrementedbasic();
             }*/

        }
        return revisedPay;
    }

    @Override
    public void insertIntoPayRevisionOption(String inputChoiceDate, int payrevisionbasic, String empid) {
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = dataSource.getConnection();
            Date choiceDate = (new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(inputChoiceDate));
            pstmt = con.prepareStatement("INSERT INTO pay_revision_option (entered_date,PAYREV_FITTED_AMOUNT,is_approved,emp_id) VALUES (?,?,?,?) ");
            pstmt.setDate(1, new java.sql.Date(choiceDate.getTime()));
            pstmt.setInt(2, payrevisionbasic);
            pstmt.setString(3, "Y");
            pstmt.setString(4, empid);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

}
