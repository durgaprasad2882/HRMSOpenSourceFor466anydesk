package hrms.dao.payroll.payslip;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import hrms.common.CalendarCommonMethods;
import hrms.common.CommonFunctions;
import hrms.common.DataBaseFunctions;
import hrms.common.Numtowordconvertion;
import hrms.common.PayrollCommonFunction;
import hrms.model.common.CommonReportParamBean;
import hrms.model.payroll.payslip.ADDetails;
import hrms.model.payroll.payslip.PaySlipDetailBean;
import hrms.model.payroll.payslip.PaySlipListBean;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class PaySlipDAOImpl implements PaySlipDAO {

    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List getPaySlip(String billNo,String empid, int year, int month) {

        Connection con = null;

        Statement stmt = null;
        ResultSet rs = null;
        PaySlipListBean pbean = null;

        double basic = 0.0;
        double totallow = 0.0;
        double grosspay = 0.0;
        double totdeduct = 0.0;
        double netpay = 0.0;

        ArrayList list = new ArrayList();
        try {
            con = dataSource.getConnection();
            
            String aqdtlsTbl = getAqDtlsTableName(billNo);
            
            stmt = con.createStatement();
            String sql = "select * from ("
                    + " SELECT AQSL_NO,AQ_MONTH,AQ_YEAR,CUR_BASIC,TOTALALLOWANCE,TOTALDEDUCTION FROM ("
                    + " select AQSL_NO,aq_mast.AQ_MONTH,aq_mast.AQ_YEAR,CUR_BASIC,TOTALALLOWANCE,TOTALDEDUCTION FROM ("
                    + " select BILL_NO,AQSL_NO,AQ_MONTH,AQ_YEAR,CUR_BASIC,"
                    + " GETADTOTAL_NEW(AQSL_NO,'A','"+aqdtlsTbl+"','"+year+"','"+month+"')TOTALALLOWANCE,GETADTOTAL_NEW(AQSL_NO,'D','"+aqdtlsTbl+"','"+year+"','"+month+"')TOTALDEDUCTION from AQ_MAST where"
                    + " AQ_MAST.EMP_CODE='" + empid + "' AND AQ_YEAR=" + year + " AND AQ_MONTH=" + month + ")AQ_MAST"
                    + " inner join"
                    + " bill_mast on aq_mast.bill_no=bill_mast.bill_no where bill_status_id >= 5 ORDER BY AQ_YEAR DESC, AQ_MONTH DESC)tab)tab2";
            //System.out.println("SQL for Pay Slip List is: " + sql);
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                pbean = new PaySlipListBean();
                pbean.setEmpId(empid);
                pbean.setAqslno(rs.getString("AQSL_NO"));
                pbean.setMonth(CalendarCommonMethods.getFullNameMonthAsString(rs.getInt("AQ_MONTH")));
                pbean.setYear(rs.getString("AQ_YEAR"));
                pbean.setMonth_year(pbean.getMonth() + "-" + pbean.getYear());
                pbean.setBasic(rs.getString("CUR_BASIC"));
                pbean.setTotallowance(rs.getString("TOTALALLOWANCE"));
                pbean.setTotdeduction(rs.getString("TOTALDEDUCTION"));

                basic = rs.getDouble("CUR_BASIC");
                totallow = rs.getDouble("TOTALALLOWANCE");
                grosspay = basic + totallow;
                pbean.setGross(grosspay + "");

                totdeduct = rs.getDouble("TOTALDEDUCTION");
                netpay = grosspay - totdeduct;
                pbean.setNetpay(netpay + "");

                list.add(pbean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            
            DataBaseFunctions.closeSqlObjects(rs,stmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return list;
    }
	@Override
    public String getAQSLNo(String empid, int year, int month) {
        Connection con = null;
        String aqslno = "";
        PreparedStatement pst = null;
        ResultSet rs = null;
        try{
            con = dataSource.getConnection();
            pst=con.prepareStatement("SELECT AQSL_NO FROM AQ_MAST WHERE EMP_CODE=? AND AQ_MONTH=? AND AQ_YEAR=?");
            pst.setString(1, empid);
            pst.setInt(2, month);
            pst.setInt(3, year);
            rs=pst.executeQuery();
            
            if(rs.next()){
                aqslno=rs.getString("aqsl_no");
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            DataBaseFunctions.closeSqlObjects(rs,pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return aqslno;
    }
    @Override
    public PaySlipDetailBean getEmployeeData(String aqslno,int year,int month) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        PaySlipDetailBean pbeandetail = null;
        
        PayrollCommonFunction prcf = new PayrollCommonFunction();
        CommonReportParamBean bean = null;

        try {
            con = dataSource.getConnection();

            String sql = "SELECT BILL_DESC,G_BANK.BANK_NAME,EMP_CODE,EMP_NAME,CUR_DESG,GPF_ACC_NO,PAY_SCALE,"
                    + " CUR_BASIC,AQ_MAST.AQ_MONTH,AQ_MAST.AQ_YEAR,PAY_DAY,AQ_MAST.BILL_NO,EMP_MAST.BANK_ACC_NO,BRASS_NO,VCH_NO,VCH_DATE,BILL_MAST.BILL_DATE FROM"
                    + " (select EMP_CODE,EMP_NAME,CUR_DESG,GPF_ACC_NO,PAY_SCALE,"
                    + " CUR_BASIC,AQ_MONTH,AQ_YEAR,PAY_DAY,BILL_NO,BANK_ACC_NO,BANK_NAME from aq_mast where"
                    + " aqsl_no='" + aqslno + "')AQ_MAST"
                    + " LEFT OUTER JOIN G_BANK ON AQ_MAST.BANK_NAME=G_BANK.BANK_CODE"
                    + " LEFT OUTER JOIN EMP_MAST ON AQ_MAST.EMP_CODE=EMP_MAST.EMP_ID"
                    + " LEFT OUTER JOIN BILL_MAST ON AQ_MAST.BILL_NO=BILL_MAST.BILL_NO";
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            if (rs.next()) {
                pbeandetail = new PaySlipDetailBean();
                if (rs.getString("EMP_NAME") != null && !rs.getString("EMP_NAME").equals("")) {
                    if (rs.getString("BRASS_NO") != null && !rs.getString("BRASS_NO").equals("")) {
                        pbeandetail.setEmpName(rs.getString("BRASS_NO") + " " + rs.getString("EMP_NAME"));
                    } else {
                        pbeandetail.setEmpName(rs.getString("EMP_NAME"));
                    }
                }
                if (rs.getString("CUR_DESG") != null && !rs.getString("CUR_DESG").equals("")) {
                    pbeandetail.setCurDesig(rs.getString("CUR_DESG"));
                }
                if (rs.getString("GPF_ACC_NO") != null && !rs.getString("GPF_ACC_NO").equals("")) {
                    pbeandetail.setGpfno(rs.getString("GPF_ACC_NO"));
                }
                if (rs.getString("PAY_SCALE") != null && !rs.getString("PAY_SCALE").equals("")) {
                    pbeandetail.setScalePay(rs.getString("PAY_SCALE"));
                }
                if (rs.getString("CUR_BASIC") != null && !rs.getString("CUR_BASIC").equals("")) {
                    pbeandetail.setCurBasic(rs.getString("CUR_BASIC"));
                }
                if (rs.getString("AQ_MONTH") != null && !rs.getString("AQ_MONTH").equals("")) {
                    pbeandetail.setForMonth(CalendarCommonMethods.getFullNameMonthAsString((rs.getInt("AQ_MONTH"))));
                }
                if (rs.getString("AQ_YEAR") != null && !rs.getString("AQ_YEAR").equals("")) {
                    pbeandetail.setForYear(rs.getString("AQ_YEAR"));
                }
                if (rs.getString("PAY_DAY") != null && !rs.getString("PAY_DAY").equals("")) {
                    pbeandetail.setDaysWork(rs.getString("PAY_DAY"));
                }
                if (rs.getString("BILL_DESC") != null && !rs.getString("BILL_DESC").equals("")) {
                    pbeandetail.setBillNo(rs.getString("BILL_DESC"));
                }
                if (rs.getString("BANK_ACC_NO") != null && !rs.getString("BANK_ACC_NO").equals("")) {
                    pbeandetail.setBankAcno(rs.getString("BANK_ACC_NO"));
                }
                if (rs.getString("BANK_NAME") != null && !rs.getString("BANK_NAME").equals("")) {
                    pbeandetail.setBank(rs.getString("BANK_NAME"));
                }
                if (rs.getString("VCH_NO") != null && !rs.getString("VCH_NO").equals("")) {
                    pbeandetail.setVchno(rs.getString("VCH_NO"));
                }
                if (rs.getString("VCH_DATE") != null && !rs.getString("VCH_DATE").equals("")) {
                    pbeandetail.setVchdate(CommonFunctions.getFormattedOutputDate1(rs.getDate("VCH_DATE")));
                }
                if (rs.getString("BILL_DATE") != null && !rs.getString("BILL_DATE").equals("")) {
                    pbeandetail.setBilldate(CommonFunctions.getFormattedOutputDate1(rs.getDate("BILL_DATE")));
                }
            }
            bean = prcf.getCommonReportParameter(con, getBillNo(aqslno,year,month));
            pbeandetail.setOffName(bean.getOfficename());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs,pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return pbeandetail;
    }

    @Override
    public ADDetails[] getAllowanceDeductionList(String aqslno, String adType,int year,int month) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        String sql = "";
        ADDetails ad = null;

        int sum = 0;
        int sum1 = 0;

        ArrayList<ADDetails> list = new ArrayList();

        try {
            con = dataSource.getConnection();
            
            String aqdtlsTbl = getAqDtlsTableName(getBillNo(aqslno,year,month));
            
            if (adType.equals("A")) {
                sql = "SELECT G_AD_LIST.AD_DESC,AD_AMT,REF_DESC,G_AD_LIST.AD_CODE,AD_ABBR FROM (SELECT AD_DESC,AD_AMT,REF_DESC,AD_CODE FROM "+aqdtlsTbl+" WHERE AD_TYPE='A' AND AD_AMT > 0 AND AQSL_NO= '" + aqslno + "' AND AQ_YEAR='"+year+"' AND AQ_MONTH='"+month+"' ORDER BY REP_COL,SL_NO )AQ_DTLS "
                        + " LEFT OUTER JOIN G_AD_LIST ON AQ_DTLS.AD_CODE=G_AD_LIST.AD_CODE_NAME";
            } else {
                sql = "SELECT AQ_DTLS.AD_DESC,AD_AMT,REF_DESC,AQ_DTLS.AD_CODE,NOW_DEDN,AD_ABBR,ACC_NO FROM (SELECT ACC_NO,AD_DESC,AD_AMT,REF_DESC,AD_CODE,NOW_DEDN"
                        + " FROM "+aqdtlsTbl+" WHERE AD_TYPE='D' AND DED_TYPE != 'L' AND AD_AMT > 0 AND AQSL_NO='" + aqslno + "' AND SCHEDULE !='PVTL' AND SCHEDULE !='PVTD' AND AQ_YEAR='"+year+"' AND AQ_MONTH='"+month+"')AQ_DTLS"
                        + " LEFT OUTER JOIN G_AD_LIST ON AQ_DTLS.AD_CODE=G_AD_LIST.AD_CODE_NAME";
            }
            System.out.println("SQL is:"+sql);
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                ad = new ADDetails();
                if ((rs.getString("AD_CODE").equals("LIC") || rs.getString("AD_CODE").equals("TLIC")) && (rs.getString("ACC_NO") != null && !rs.getString("ACC_NO").equals(""))) {
                    ad.setAdCode(rs.getString("AD_ABBR") + " (" + rs.getString("ACC_NO") + ")");
                } else {
                    ad.setAdCode(rs.getString("AD_ABBR"));
                }
                if ((rs.getString("AD_CODE").equals("LIC") || rs.getString("AD_CODE").equals("TLIC"))) {
                    ad.setAdDesc(rs.getString("AD_AMT"));
                } else if (rs.getString("REF_DESC") != null) {
                    ad.setAdDesc(rs.getString("AD_AMT") + " (" + rs.getString("REF_DESC") + ")");
                } else {
                    ad.setAdDesc(rs.getString("AD_AMT"));
                }
                if (adType.equalsIgnoreCase("A") && rs.getString("AD_AMT") != null) {

                    sum += Integer.parseInt(rs.getString("AD_AMT"));
                    ad.setAdAmount(sum);
                }
                if (adType.equalsIgnoreCase("D") && rs.getString("AD_AMT") != null) {

                    sum1 += Integer.parseInt(rs.getString("AD_AMT"));
                    ad.setAdAmount(sum1);
                }
                list.add(ad);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs,pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        ADDetails idarray[] = list.toArray(new ADDetails[list.size()]);
        return idarray;
    }

    @Override
    public List getPrivateDedeuctionList(String aqslno,int year,int month) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet pvtdedRs = null;

        ADDetails ad = null;

        int sum = 0;
        int sum1 = 0;

        ArrayList list = new ArrayList();
        try {
            con = dataSource.getConnection();
            
            String aqdtlsTbl = getAqDtlsTableName(getBillNo(aqslno,year,month));
            
            String sql = "SELECT PVT_DESC,AD_DESC,AD_CODE,AD_AMT,REF_DESC,AQ_DTLS.NOW_DEDN,AD_REF_ID,TOT_REC_AMT,P_ORG_AMT,I_ORG_AMT,LOAN_ABBR FROM"
                    + " (SELECT PVT_DESC,AD_DESC,AD_CODE,AD_AMT,REF_DESC,AQ_DTLS.NOW_DEDN,AD_REF_ID,TOT_REC_AMT,P_ORG_AMT,I_ORG_AMT FROM(SELECT AD_DESC,AD_CODE,AD_AMT,"
                    + " REF_DESC,NOW_DEDN,AD_REF_ID,TOT_REC_AMT FROM "+aqdtlsTbl+" WHERE AD_TYPE='D' AND AD_AMT > 0 AND (SCHEDULE ='PVTL' OR SCHEDULE"
                    + " ='PVTD') AND AQSL_NO='" + aqslno + "' AND AQ_YEAR='"+year+"' AND AQ_MONTH='"+month+"')AQ_DTLS"
                    + " LEFT OUTER JOIN (SELECT LOANID,NOW_DEDN,P_ORG_AMT,I_ORG_AMT,PVT_DESC FROM EMP_LOAN_SANC)EMP_LOAN_SANC ON AQ_DTLS.AD_REF_ID ="
                    + " EMP_LOAN_SANC.LOANID )AQ_DTLS"
                    + " LEFT OUTER JOIN G_LOAN ON AQ_DTLS.AD_CODE=G_LOAN.LOAN_TP";
            pst = con.prepareStatement(sql);
            pvtdedRs = pst.executeQuery();
            while (pvtdedRs.next()) {
                ad = new ADDetails();
                if (pvtdedRs.getString("LOAN_ABBR") != null && !pvtdedRs.getString("LOAN_ABBR").equals("")) {
                    ad.setAdCode(pvtdedRs.getString("LOAN_ABBR"));
                } else if (pvtdedRs.getString("PVT_DESC") != null && !pvtdedRs.getString("PVT_DESC").equals("")) {
                    ad.setAdCode(pvtdedRs.getString("PVT_DESC").toUpperCase());
                } else if (pvtdedRs.getString("AD_DESC") != null && !pvtdedRs.getString("AD_DESC").equals("")) {
                    ad.setAdCode(pvtdedRs.getString("AD_DESC").toUpperCase());
                }
                if (pvtdedRs.getString("REF_DESC") != null) {
                    ad.setAdDesc(pvtdedRs.getString("AD_AMT") + " (" + pvtdedRs.getString("REF_DESC") + ")");
                } else {
                    ad.setAdDesc(pvtdedRs.getString("AD_AMT"));
                }
                sum1 += Integer.parseInt(pvtdedRs.getString("AD_AMT"));
                ad.setAdAmount(sum1);
                list.add(ad);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pvtdedRs,pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return list;
    }

    @Override
    public List getLoanList(String aqslno,int year,int month) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet loanRs = null;

        ADDetails ad = null;

        ArrayList list = new ArrayList();

        int sum = 0;
        int sum1 = 0;
        try {
            con = dataSource.getConnection();
            
            String aqdtlsTbl = getAqDtlsTableName(getBillNo(aqslno,year,month));
            
            String sql = "SELECT PVT_DESC,AD_DESC,AD_CODE,AD_AMT,REF_DESC,AQ_DTLS.NOW_DEDN,AD_REF_ID,TOT_REC_AMT,P_ORG_AMT,I_ORG_AMT,LOAN_ABBR FROM (SELECT AD_DESC,AD_CODE,AD_AMT,REF_DESC,AQ_DTLS.NOW_DEDN,AD_REF_ID,TOT_REC_AMT,P_ORG_AMT,I_ORG_AMT,PVT_DESC FROM(SELECT AD_DESC,AD_CODE,AD_AMT,REF_DESC,NOW_DEDN,AD_REF_ID,TOT_REC_AMT FROM "+aqdtlsTbl+" WHERE AD_TYPE='D' AND DED_TYPE = 'L' AND AD_AMT > 0 AND AQSL_NO='" + aqslno + "' AND SCHEDULE !='PVTL' AND SCHEDULE !='PVTD' AND AQ_YEAR='"+year+"' AND AQ_MONTH='"+month+"')AQ_DTLS"
                    + " INNER JOIN (SELECT LOANID,NOW_DEDN,P_ORG_AMT,I_ORG_AMT,PVT_DESC FROM EMP_LOAN_SANC)EMP_LOAN_SANC ON AQ_DTLS.AD_REF_ID = EMP_LOAN_SANC.LOANID )AQ_DTLS"
                    + " LEFT OUTER JOIN G_LOAN ON AQ_DTLS.AD_CODE=G_LOAN.LOAN_TP";

            pst = con.prepareStatement(sql);
            loanRs = pst.executeQuery();
            while (loanRs.next()) {
                ad = new ADDetails();
                if (loanRs.getString("LOAN_ABBR") != null && !loanRs.getString("LOAN_ABBR").equals("")) {
                    ad.setAdCode(loanRs.getString("LOAN_ABBR"));
                } else {
                    ad.setAdCode(loanRs.getString("PVT_DESC").toUpperCase());
                }
                if (loanRs.getString("REF_DESC") != null) {
                    ad.setAdDesc(loanRs.getString("AD_AMT") + " (" + loanRs.getString("REF_DESC") + ")");
                } else {
                    ad.setAdDesc(loanRs.getString("AD_AMT"));
                }
                sum1 += loanRs.getDouble("AD_AMT");
                ad.setAdAmount(sum1);
                list.add(ad);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(loanRs,pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return list;
    }

    @Override
    public void getPaySlipPDF(Document document, PaySlipDetailBean pbeandetail, ArrayList allownacelist, ArrayList deductionlist, ArrayList privatedeductionlist, ArrayList loanlist) {

        try {
            Font f1 = new Font();
            f1.setSize(10);
            f1.setFamily("Times New Roman");

            PdfPTable table = null;
            PdfPTable innertable = null;
            
            PdfPCell cell = null;
            PdfPCell innercell = null;

            table = new PdfPTable(6);
            table.setWidths(new int[]{2, 2, 2, 2, 2, 2});
            table.setWidthPercentage(100);

            cell = new PdfPCell(new Phrase("PAY SLIP",f1));
            cell.setColspan(6);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell();
            cell.setColspan(6);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("("+pbeandetail.getOffName()+")",f1));
            cell.setColspan(6);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            cell = new PdfPCell();
            cell.setColspan(6);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            
            cell = new PdfPCell(new Phrase("Name and Designation of the Incumbent: "+pbeandetail.getEmpName()+","+pbeandetail.getCurDesig(),f1));
            cell.setColspan(6);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            
            cell = new PdfPCell(new Phrase(StringUtils.repeat("-", 150)));
            cell.setColspan(6);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            
            cell = new PdfPCell(new Phrase("GPF Ac No:",f1));
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(pbeandetail.getGpfno(),f1));
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("For the Month of:",f1));
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(pbeandetail.getForMonth()+"-"+pbeandetail.getForYear(),f1));
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Bank:",f1));
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(pbeandetail.getBank(),f1));
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            
            cell = new PdfPCell(new Phrase("Scale of Pay:",f1));
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(pbeandetail.getScalePay(),f1));
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("No of days worked:",f1));
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(pbeandetail.getDaysWork(),f1));
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Bank A/c No:",f1));
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(pbeandetail.getBankAcno(),f1));
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            
            cell = new PdfPCell(new Phrase("Current Basic:",f1));
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(pbeandetail.getCurBasic(),f1));
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Pay of the Month:",f1));
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(pbeandetail.getCurBasic(),f1));
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Bill No:",f1));
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(pbeandetail.getBillNo(),f1));
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            
            cell = new PdfPCell(new Phrase("TV No:",f1));
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(pbeandetail.getVchno(),f1));
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("TV Date:",f1));
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(pbeandetail.getVchdate(),f1));
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Bill Date:",f1));
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(pbeandetail.getBilldate(),f1));
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            
            innertable = new PdfPTable(3);
            innertable.setWidths(new int[]{2, 2, 2});
            innertable.setWidthPercentage(100);
            
            innercell = new PdfPCell(new Phrase("SL",f1));
            cell.setColspan(2);
            cell.setBorder(Rectangle.NO_BORDER);
            innertable.addCell(cell);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    public String getBillNo(String aqslno,int year,int month) throws Exception {
        
        Connection con = null;
        
        Statement stmt = null;
        ResultSet rs = null;
        
        String billNO = "";
        try {
            con = dataSource.getConnection();
            
            stmt = con.createStatement();
            rs = stmt.executeQuery("select AQ_MAST.BILL_NO from AQ_MAST where aqsl_no='" + aqslno + "' AND AQ_YEAR='"+year+"' AND AQ_MONTH='"+month+"'");
            if (rs.next()) {
                billNO = rs.getString("BILL_NO");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs,stmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return billNO;
    }
    
    public String getBillNo1(String empid,int year,int month) throws Exception {
        
        Connection con = null;
        
        Statement stmt = null;
        ResultSet rs = null;
        
        String billNO = "";
        try{
            con = dataSource.getConnection();
            
            stmt = con.createStatement();
            String sql = "select bill_no from aq_mast where emp_code='"+empid+"' and aq_year='"+year+"' and aq_month='"+month+"'";
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                billNO = rs.getString("bill_no");
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            DataBaseFunctions.closeSqlObjects(rs,stmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
      return billNO;  
    }
    
    public String getAqDtlsTableName(String billNo) {

        Connection con = null;

        Statement stmt = null;
        ResultSet res = null;

        String aqDTLS = "AQ_DTLS";
        try {
            con = dataSource.getConnection();

            stmt = con.createStatement();
            res = stmt.executeQuery("SELECT aq_month,aq_year FROM BILL_MAST WHERE bill_no=" + billNo);
            int aqMonth = 0;
            int aqYear = 0;
            if (res.next()) {
                aqMonth = res.getInt("aq_month");
                aqYear = res.getInt("aq_year");
            }

            if ( aqYear < 2020) {
                aqDTLS = "hrmis.AQ_DTLS1";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(res, stmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return aqDTLS;
    }
	
	@Override
    public String getTokenGeneratedBillNo(String empid, int year, int month) {
        
        Connection con = null;

        Statement stmt = null;
        ResultSet rs = null;

        String billNO = "";
        try {
            con = dataSource.getConnection();

            stmt = con.createStatement();
            String sql = "select bill_mast.bill_no from (select bill_no from aq_mast where emp_code='"+empid+"' and aq_year="+year+" and aq_month="+month+")aq_mast" +
                         " inner join (select bill_no,bill_status_id from bill_mast where bill_status_id >= 5)bill_mast on aq_mast.bill_no=bill_mast.bill_no";
            System.out.println("Query for bill no is: "+sql);
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                billNO = rs.getString("bill_no");
            }
            //System.out.println("Bill No is: "+billNO);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
             DataBaseFunctions.closeSqlObjects(rs,stmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return billNO;
    }
    
    /*
    @Override
    public ADDetails[] getAllowanceDeductionList(String aqslno, String adType, int year, int month) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        String sql = "";
        ADDetails ad = null;

        int sum = 0;
        int sum1 = 0;

        ArrayList<ADDetails> list = new ArrayList();
        try {
            con = dataSource.getConnection();

            String aqdtlsTbl = getAqDtlsTableName(getBillNo(aqslno,year,month));

            if (adType.equals("A")) {
                sql = "SELECT G_AD_LIST.AD_DESC,AD_AMT,REF_DESC,G_AD_LIST.AD_CODE,AD_ABBR FROM (SELECT AD_DESC,AD_AMT,REF_DESC,AD_CODE FROM " + aqdtlsTbl + " WHERE AD_TYPE='A' AND AD_AMT > 0 AND AQSL_NO= '" + aqslno + "' ORDER BY REP_COL,SL_NO )AQ_DTLS "
                        + " LEFT OUTER JOIN G_AD_LIST ON AQ_DTLS.AD_CODE=G_AD_LIST.AD_CODE_NAME";
            } else {
                sql = "SELECT AQ_DTLS.AD_DESC,AD_AMT,REF_DESC,AQ_DTLS.AD_CODE,NOW_DEDN,AD_ABBR,ACC_NO FROM (SELECT ACC_NO,AD_DESC,AD_AMT,REF_DESC,AD_CODE,NOW_DEDN"
                        + " FROM " + aqdtlsTbl + " WHERE AD_TYPE='D' AND DED_TYPE != 'L' AND AD_AMT > 0 AND AQSL_NO='" + aqslno + "' AND SCHEDULE !='PVTL' AND SCHEDULE !='PVTD')AQ_DTLS"
                        + " LEFT OUTER JOIN G_AD_LIST ON AQ_DTLS.AD_CODE=G_AD_LIST.AD_CODE_NAME";
            }
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                ad = new ADDetails();
                if ((rs.getString("AD_CODE").equals("LIC") || rs.getString("AD_CODE").equals("TLIC")) && (rs.getString("ACC_NO") != null && !rs.getString("ACC_NO").equals(""))) {
                    ad.setAdCode(rs.getString("AD_ABBR") + " (" + rs.getString("ACC_NO") + ")");
                } else {
                    ad.setAdCode(rs.getString("AD_ABBR"));
                }
                if ((rs.getString("AD_CODE").equals("LIC") || rs.getString("AD_CODE").equals("TLIC"))) {
                    ad.setAdDesc(rs.getString("AD_AMT"));
                } else if (rs.getString("REF_DESC") != null) {
                    ad.setAdDesc(rs.getString("AD_AMT") + " (" + rs.getString("REF_DESC") + ")");
                } else {
                    ad.setAdDesc(rs.getString("AD_AMT"));
                }
                if (adType.equalsIgnoreCase("A") && rs.getString("AD_AMT") != null) {

                    sum += Integer.parseInt(rs.getString("AD_AMT"));
                    ad.setAdAmount(sum);
                }
                if (adType.equalsIgnoreCase("D") && rs.getString("AD_AMT") != null) {

                    sum1 += Integer.parseInt(rs.getString("AD_AMT"));
                    ad.setAdAmount(sum1);
                }
                list.add(ad);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        ADDetails idarray[] = list.toArray(new ADDetails[list.size()]);
        return idarray;
    }
    
    */
}
