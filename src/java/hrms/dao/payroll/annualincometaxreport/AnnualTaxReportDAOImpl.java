package hrms.dao.payroll.annualincometaxreport;

import hrms.SelectOption;
import hrms.common.CalendarCommonMethods;
import hrms.common.DataBaseFunctions;
import hrms.model.payroll.annualincometaxreport.EmpAttr;
import java.io.OutputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.NumberFormats;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class AnnualTaxReportDAOImpl implements AnnualTaxReportDAO {

    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List getBillList(String offCode) {

        Connection con = null;

        Statement stmt = null;
        ResultSet rs = null;

        SelectOption so = null;

        ArrayList li = new ArrayList();
        try {
            con = dataSource.getConnection();

            stmt = con.createStatement();
            String sql = "SELECT BILL_GROUP_ID, DESCRIPTION, PLAN, SECTOR, MAJOR_HEAD,SUB_MAJOR_HEAD,MINOR_HEAD,SUB_MINOR_HEAD1,SUB_MINOR_HEAD2,SUB_MINOR_HEAD3,POST_CLASS,DEMAND_NO from BILL_GROUP_MASTER "
                    + "WHERE OFF_CODE='" + offCode + "' AND (IS_DELETED IS  NULL OR IS_DELETED='N') ORDER BY DESCRIPTION";
            System.out.println("SQL for Bill List is: " + sql);
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                so = new SelectOption();
                so.setLabel(rs.getString("DESCRIPTION"));
                so.setValue(rs.getString("BILL_GROUP_ID"));
                li.add(so);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
             DataBaseFunctions.closeSqlObjects(rs,stmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return li;
    }

    @Override
    public List getFinyearList() {

        ArrayList list = new ArrayList();
        SelectOption so = null;
        try {
            int year = Calendar.getInstance().get(Calendar.YEAR);
            int month = Calendar.getInstance().get(Calendar.MONTH) + 1;

            if (month < 3) {
                so = new SelectOption();
                so.setLabel(year - 1 + "-" + year);
                so.setValue(year - 1 + "-" + year);
                list.add(so);
                year--;
                so = new SelectOption();
                so.setLabel(year - 1 + "-" + year);
                so.setValue(year - 1 + "-" + year);
                list.add(so);
            } else {
                so = new SelectOption();
                so.setLabel(year - 1 + "-" + year);
                so.setValue(year - 1 + "-" + year);
                list.add(so);
                year--;
                so = new SelectOption();
                so.setLabel(year - 1 + "-" + year);
                so.setValue(year - 1 + "-" + year);
                list.add(so);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return list;
    }

    @Override
    public List getEmployeeList(String billgroupid, String finyear) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        String finyearArr[] = finyear.split("-");

        EmpAttr ep = null;
        ArrayList list = new ArrayList();
        try {
            con = dataSource.getConnection();

            String sql = "SELECT DISTINCT EMP_CODE,GPF_ACC_NO,ACCT_TYPE,EMP_NAME FROM ("
                    + " SELECT DISTINCT EMP_CODE,GPF_ACC_NO,ACCT_TYPE,EMP_NAME,CUR_DESG,AQ_YEAR,AQ_MONTH,POST_SL_NO FROM AQ_MAST WHERE AQ_GROUP='" + billgroupid + "' AND AQ_YEAR=" + finyearArr[1] + " AND AQ_MONTH BETWEEN 0 AND 2 AND EMP_CODE IS NOT NULL"
                    + " UNION"
                    + " SELECT DISTINCT EMP_CODE,GPF_ACC_NO,ACCT_TYPE,EMP_NAME,CUR_DESG,AQ_YEAR,AQ_MONTH,POST_SL_NO FROM AQ_MAST WHERE AQ_GROUP='" + billgroupid + "' AND AQ_YEAR=" + finyearArr[0] + " AND AQ_MONTH BETWEEN 3 AND 11 AND EMP_CODE IS NOT NULL)TEMP group by EMP_CODE,GPF_ACC_NO,ACCT_TYPE,EMP_NAME ORDER BY EMP_NAME";
            System.out.println("Employee List query is: " + sql);
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                ep = new EmpAttr();
                ep.setEmpId(rs.getString("EMP_CODE"));
                ep.setGpfno(rs.getString("GPF_ACC_NO"));
                ep.setName(rs.getString("EMP_NAME"));
                ep.setAccType(rs.getString("ACCT_TYPE"));
                list.add(ep);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs,pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return list;
    }

    @Override
    public void downloadExcel(HttpServletResponse response, String empId, String offcode, String bgrId, String fiscalYear) {

        Connection con = null;

        PreparedStatement pst = null;
        Statement stmt = null;
        Statement st3 = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        ResultSet rs3 = null;

        String year1 = "";
        String year2 = "";
        if (fiscalYear != null) {
            String str[] = fiscalYear.split("-");
            year1 = str[1];
            year2 = str[0];
        }

        int row = 0;
        int totbasic = 0;
        int totgp = 0;
        int totir = 0;
        int totoa = 0;
        int totda = 0;
        int tothra = 0;
        int totgpf = 0;
        int totot = 0;
        int totgross = 0;
        int totpt = 0;
        int totit = 0;
        int totlic = 0;
        int tothbap = 0;
        int tothbai = 0;
        int tothrr = 0;
        int totwrr = 0;
        int tothc = 0;
        int totcompadv = 0;
        int totfa = 0;
        int totgis = 0;
        int totmopap = 0;
        int totmopai = 0;
        int totmcap = 0;
        int totmcai = 0;
        int totded = 0;
        int totnet = 0;
        int totloan = 0;
        int totga = 0;
        try {
            con = dataSource.getConnection();

            stmt = con.createStatement();
            st3 = con.createStatement();
            String fileName = "OFFICE_DATA_" + offcode + ".xls";

            OutputStream out = response.getOutputStream();
            WritableWorkbook workbook = Workbook.createWorkbook(out);
            WritableSheet sheet = workbook.createSheet(offcode, 0);
            WritableFont headformat = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
            WritableCellFormat headcell3 = new WritableCellFormat(headformat);
            headcell3.setAlignment(Alignment.CENTRE);
            headcell3.setVerticalAlignment(VerticalAlignment.CENTRE);
            headcell3.setWrap(true);

            WritableCellFormat headcell = new WritableCellFormat(headformat);
            headcell.setAlignment(Alignment.CENTRE);
            headcell.setVerticalAlignment(VerticalAlignment.CENTRE);
            headcell.setWrap(true);
            headcell.setBorder(Border.ALL, BorderLineStyle.DOUBLE);

            WritableCellFormat headcell2 = new WritableCellFormat(headformat);
            headcell2.setAlignment(Alignment.LEFT);
            headcell2.setWrap(true);

            WritableFont cellformat = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD);
            // WritableCellFormat innercell = new WritableCellFormat(cellformat);

            WritableCellFormat innercell = new WritableCellFormat(NumberFormats.INTEGER);

            Label label1 = new Label(1, row++, "ANNUAL INCOME TAX REPORT", headcell3);//column,row
            sheet.addCell(label1);
            sheet.mergeCells(1, 0, 15, 0);
            Label label2 = new Label(1, row++, "For the Year " + year2 + "-" + year1, headcell3);//column,row
            sheet.addCell(label2);
            sheet.mergeCells(1, 1, 15, 1);
            String sql = "";

            Number num = null;

            Label label = null;
            bgrId = "";
            if (bgrId != null && !bgrId.equals("")) {
                sql = "SELECT EMP_ID,GPF_NO,EMP_NAME,F_NAME,SPN,PANCARD FROM ( SELECT EMP_ID,GPF_NO, ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME,L_NAME], ' ') EMP_NAME,F_NAME, CUR_SPC,GETPANNUMBER(EMP_MAST.EMP_ID,'PAN') PANCARD FROM("
                        + " SELECT SPC FROM ( SELECT SECTION_ID,BILL_GROUP_ID FROM BILL_SECTION_MAPPING WHERE BILL_GROUP_ID='" + bgrId + "') BILL_SECTION_MAPPING"
                        + " INNER JOIN SECTION_POST_MAPPING ON BILL_SECTION_MAPPING.SECTION_ID=SECTION_POST_MAPPING.SECTION_ID) SECTION_POST_MAPPING"
                        + " INNER JOIN EMP_MAST ON SECTION_POST_MAPPING.SPC=EMP_MAST.CUR_SPC ORDER BY F_NAME ) EMP_MAST INNER JOIN G_SPC ON  EMP_MAST.CUR_SPC=G_SPC.SPC";
            } else if (empId != null && !empId.equals("")) {
                sql = "SELECT EMP_ID,GPF_NO,EMP_NAME,F_NAME,SPN,PANCARD FROM ( SELECT EMP_ID,GPF_NO, ARRAY_TO_STRING(ARRAY[INITIALS, F_NAME, M_NAME,L_NAME], ' ') EMP_NAME,F_NAME, CUR_SPC,GETPANNUMBER(EMP_MAST.EMP_ID,'PAN') PANCARD FROM emp_mast where EMP_ID='" + empId + "' ORDER BY F_NAME) EMP_MAST INNER JOIN G_SPC ON EMP_MAST.CUR_SPC=G_SPC.SPC";
            }
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();

            while (rs.next()) {
                label = new Label(1, row, "(1)  Account Number  :", headcell2);//column,row
                sheet.addCell(label);
                sheet.mergeCells(1, row, 3, row);

                label = new Label(4, row, rs.getString("GPF_NO"), headcell2);//column,row
                sheet.addCell(label);
                sheet.mergeCells(4, row, 15, row);

                row++;
                label = new Label(1, row, "(2)  Name            :", headcell2);//column,row
                sheet.addCell(label);
                sheet.mergeCells(1, row, 3, row);

                label = new Label(4, row, rs.getString("EMP_NAME"), headcell2);//column,row
                sheet.addCell(label);
                sheet.mergeCells(4, row, 15, row);

                row++;
                label = new Label(1, row, "(3)  Designation     :", headcell2);//column,row
                sheet.addCell(label);
                sheet.mergeCells(1, row, 3, row);

                label = new Label(4, row, rs.getString("SPN"), headcell2);//column,row
                sheet.addCell(label);
                sheet.mergeCells(4, row, 15, row);

                row++;
                label = new Label(1, row, "(4)  Pan card Number :", headcell2);//column,row
                sheet.addCell(label);
                sheet.mergeCells(1, row, 3, row);

                label = new Label(4, row, rs.getString("PANCARD"), headcell2);//column,row
                sheet.addCell(label);
                sheet.mergeCells(4, row, 15, row);

                row++;
                label = new Label(1, row, "Month", headcell);//column,row
                sheet.addCell(label);
                sheet.setColumnView(1, 16);

                label = new Label(2, row, "Bill No", headcell);//column,row
                sheet.addCell(label);
                sheet.setColumnView(2, 16);

                label = new Label(3, row, "Basic Pay", headcell);//column,row
                sheet.addCell(label);
                sheet.setColumnView(3, 11);

                label = new Label(4, row, "DP/GP", headcell);//column,row
                sheet.addCell(label);
                sheet.setColumnView(4, 8);

                label = new Label(5, row, "IR", headcell);//column,row
                sheet.addCell(label);
                sheet.setColumnView(5, 8);

                label = new Label(6, row, "DA", headcell);
                sheet.addCell(label);
                sheet.setColumnView(6, 9);

                label = new Label(7, row, "HRA", headcell);
                sheet.addCell(label);
                sheet.setColumnView(7, 8);

                label = new Label(8, row, "OA", headcell);//column,row
                sheet.addCell(label);
                sheet.setColumnView(8, 8);
                
                label = new Label(9, row, "OT", headcell);//column,row
                sheet.addCell(label);
                sheet.setColumnView(9, 9);
                
                label = new Label(10, row, "GROSS TOTAL", headcell);
                sheet.addCell(label);
                sheet.setColumnView(10, 10);

                label = new Label(11, row, "GPF/TPF /CPF", headcell);
                sheet.addCell(label);
                sheet.setColumnView(11, 9);

                label = new Label(12, row, "PT", headcell);
                sheet.addCell(label);
                sheet.setColumnView(12, 8);

                label = new Label(13, row, "IT", headcell);
                sheet.addCell(label);
                sheet.setColumnView(13, 8);

                label = new Label(14, row, "LIC", headcell);
                sheet.addCell(label);
                sheet.setColumnView(14, 8);

                label = new Label(15, row, "HRR", headcell);
                sheet.addCell(label);
                sheet.setColumnView(15, 8);

                label = new Label(16, row, "WRR", headcell);
                sheet.addCell(label);
                sheet.setColumnView(16, 8);

                label = new Label(17, row, "MOPA(P)", headcell);
                sheet.addCell(label);
                sheet.setColumnView(17, 8);

                label = new Label(18, row, "MOPA(I)", headcell);
                sheet.addCell(label);
                sheet.setColumnView(18, 8);

                label = new Label(19, row, "MCY(P)", headcell);
                sheet.addCell(label);
                sheet.setColumnView(19, 8);

                label = new Label(20, row, "MCY(I)", headcell);
                sheet.addCell(label);
                sheet.setColumnView(20, 8);

                label = new Label(21, row, "Hiring Charges", headcell);
                sheet.addCell(label);
                sheet.setColumnView(21, 8);

                label = new Label(22, row, "Computer Advance", headcell);
                sheet.addCell(label);
                sheet.setColumnView(22, 8);

                label = new Label(23, row, "GIS", headcell);
                sheet.addCell(label);
                sheet.setColumnView(23, 8);

                label = new Label(24, row, "GPF Advance", headcell);
                sheet.addCell(label);
                sheet.setColumnView(24, 8);

                label = new Label(25, row, "FA", headcell);
                sheet.addCell(label);
                sheet.setColumnView(25, 8);

                label = new Label(26, row, "HBA(P)", headcell);
                sheet.addCell(label);
                sheet.setColumnView(26, 8);

                label = new Label(27, row, "HBA(I)", headcell);
                sheet.addCell(label);
                sheet.setColumnView(27, 8);

                label = new Label(28, row, "Loan", headcell);
                sheet.addCell(label);
                sheet.setColumnView(28, 8);

                label = new Label(29, row, "Total Deduction", headcell);
                sheet.addCell(label);
                sheet.setColumnView(29, 10);

                label = new Label(30, row, "Net", headcell);
                sheet.addCell(label);
                sheet.setColumnView(30, 10);
                row++;

                label = new Label(1, row, "1", headcell);//column,row
                sheet.addCell(label);
                label = new Label(2, row, "2", headcell);//column,row
                sheet.addCell(label);
                label = new Label(3, row, "3", headcell);//column,row
                sheet.addCell(label);
                label = new Label(4, row, "4", headcell);//column,row
                sheet.addCell(label);
                label = new Label(5, row, "5", headcell);
                sheet.addCell(label);
                label = new Label(6, row, "6", headcell);
                sheet.addCell(label);
                label = new Label(7, row, "7", headcell);//column,row
                sheet.addCell(label);
                label = new Label(8, row, "8", headcell);
                sheet.addCell(label);
                label = new Label(9, row, "9", headcell);
                sheet.addCell(label);
                label = new Label(10, row, "10", headcell);
                sheet.addCell(label);
                label = new Label(11, row, "11", headcell);
                sheet.addCell(label);
                label = new Label(12, row, "12", headcell);
                sheet.addCell(label);
                label = new Label(13, row, "13", headcell);
                sheet.addCell(label);
                label = new Label(14, row, "14", headcell);
                sheet.addCell(label);
                label = new Label(15, row, "15", headcell);
                sheet.addCell(label);
                label = new Label(16, row, "16", headcell);
                sheet.addCell(label);
                label = new Label(17, row, "17", headcell);
                sheet.addCell(label);
                label = new Label(18, row, "18", headcell);
                sheet.addCell(label);
                label = new Label(19, row, "19", headcell);
                sheet.addCell(label);
                label = new Label(20, row, "20", headcell);
                sheet.addCell(label);
                label = new Label(21, row, "21", headcell);
                sheet.addCell(label);
                label = new Label(22, row, "22", headcell);
                sheet.addCell(label);
                label = new Label(23, row, "23", headcell);
                sheet.addCell(label);
                label = new Label(24, row, "24", headcell);
                sheet.addCell(label);
                label = new Label(25, row, "25", headcell);
                sheet.addCell(label);
                label = new Label(26, row, "26", headcell);
                sheet.addCell(label);
                label = new Label(27, row, "27", headcell);
                sheet.addCell(label);
                label = new Label(28, row, "28", headcell);
                sheet.addCell(label);
                label = new Label(29, row, "29", headcell);
                sheet.addCell(label);
                label = new Label(30, row, "30", headcell);
                sheet.addCell(label);

                row++;
                con.setAutoCommit(false);
                CallableStatement proc = con.prepareCall("{ ? = call process_incometax_report1(?, ?, ?) }");
                proc.registerOutParameter(1, Types.OTHER);
                proc.setString(2, rs.getString("EMP_ID"));
                proc.setInt(3, Integer.parseInt(year1));
                proc.setInt(4, Integer.parseInt(year2));
                proc.execute();
                rs2 = (ResultSet) proc.getObject(1);
                /*String sql1 = "SELECT * FROM process_incometax_report("+rs.getString("EMP_ID")+","+year1+","+year2+")";
                 rs2 = stmt.executeQuery(sql1);*/
                while (rs2.next()) {
                    int basic = rs2.getInt("CUR_BASIC");
                    int da = 0;
                    int gp = 0;
                    int dp = 0;
                    int ir = 0;
                    int hra = 0;
                    int oa = 0;
                    int ot = 0;
                    int gross = 0;
                    int gpf = 0;
                    int pt = 0;
                    int it = 0;
                    int lic = 0;
                    int hbap = 0;
                    int hbai = 0;
                    int hrr = 0;
                    int wrr = 0;
                    int fa = 0;
                    int hc = 0;
                    int gis = 0;
                    int ga = 0;
                    int cmpa = 0;
                    int mcap = 0;
                    int mcai = 0;
                    int mopap = 0;
                    int mopai = 0;
                    int loan = 0;
                    int totdeduct = 0;
                    int net = 0;
                    //System.out.println("EMP ID is: " + rs2.getString("EMP_CODE"));
                    //System.out.println("AQSL_NO is: " + rs2.getString("AQSL_NO"));
                    String aqdtlsname = getAqDtlsTableName(rs2.getString("BILL_NO"));
                    //System.out.println("Table name is: "+aqdtlsname);
                    rs3 = st3.executeQuery("SELECT GETADAMT_FROM_AQDTLS('" + rs2.getString("EMP_CODE") + "','" + rs2.getString("AQSL_NO") + "','GP','','"+aqdtlsname+"') GP,GETADAMT_FROM_AQDTLS('" + rs2.getString("EMP_CODE") + "','" + rs2.getString("AQSL_NO") + "','DP','','"+aqdtlsname+"') DP,GETADAMT_FROM_AQDTLS('" + rs2.getString("EMP_CODE") + "','" + rs2.getString("AQSL_NO") + "','DA','','"+aqdtlsname+"') DA, "
                            + "GETADAMT_FROM_AQDTLS('" + rs2.getString("EMP_CODE") + "','" + rs2.getString("AQSL_NO") + "','OA','','"+aqdtlsname+"') OA,GETADAMT_FROM_AQDTLS('" + rs2.getString("EMP_CODE") + "','" + rs2.getString("AQSL_NO") + "','HRA','','"+aqdtlsname+"') HRA,GETADAMT_FROM_AQDTLS('" + rs2.getString("EMP_CODE") + "','" + rs2.getString("AQSL_NO") + "','IR','','"+aqdtlsname+"') IR ,"
                            + "GETADAMT_FROM_AQDTLS('" + rs2.getString("EMP_CODE") + "','" + rs2.getString("AQSL_NO") + "','PT','','"+aqdtlsname+"') PT ,GETADAMT_FROM_AQDTLS('" + rs2.getString("EMP_CODE") + "','" + rs2.getString("AQSL_NO") + "','IT','','"+aqdtlsname+"') IT,GETADAMT_FROM_AQDTLS('" + rs2.getString("EMP_CODE") + "','" + rs2.getString("AQSL_NO") + "','LIC','','"+aqdtlsname+"') LIC,"
                            + "GETADAMT_FROM_AQDTLS('" + rs2.getString("EMP_CODE") + "','" + rs2.getString("AQSL_NO") + "','HBA','P','"+aqdtlsname+"') HBAP,GETADAMT_FROM_AQDTLS('" + rs2.getString("EMP_CODE") + "','" + rs2.getString("AQSL_NO") + "','HBA','I','"+aqdtlsname+"') HBAI,GETADAMT_FROM_AQDTLS('" + rs2.getString("EMP_CODE") + "','" + rs2.getString("AQSL_NO") + "','GPF','','"+aqdtlsname+"')GPF, "
                            + "GETADAMT_FROM_AQDTLS('" + rs2.getString("EMP_CODE") + "','" + rs2.getString("AQSL_NO") + "','HRR','','"+aqdtlsname+"')HRR ,GETADAMT_FROM_AQDTLS('" + rs2.getString("EMP_CODE") + "','" + rs2.getString("AQSL_NO") + "','WRR','','"+aqdtlsname+"')WRR ,"
                            + "GETADAMT_FROM_AQDTLS('" + rs2.getString("EMP_CODE") + "','" + rs2.getString("AQSL_NO") + "','MOPA','P','"+aqdtlsname+"')MOPAP ,GETADAMT_FROM_AQDTLS('" + rs2.getString("EMP_CODE") + "','" + rs2.getString("AQSL_NO") + "','MOPA','I','"+aqdtlsname+"')MOPAI ,"
                            + "GETADAMT_FROM_AQDTLS('" + rs2.getString("EMP_CODE") + "','" + rs2.getString("AQSL_NO") + "','MCA','P','"+aqdtlsname+"')MCAP ,GETADAMT_FROM_AQDTLS('" + rs2.getString("EMP_CODE") + "','" + rs2.getString("AQSL_NO") + "','MCA','I','"+aqdtlsname+"')MCAI ,"
                            + "GETADAMT_FROM_AQDTLS('" + rs2.getString("EMP_CODE") + "','" + rs2.getString("AQSL_NO") + "','GIS','','"+aqdtlsname+"')GIS ,GETADAMT_FROM_AQDTLS('" + rs2.getString("EMP_CODE") + "','" + rs2.getString("AQSL_NO") + "','FA','','"+aqdtlsname+"')FA ,GETADAMT_FROM_AQDTLS('" + rs2.getString("EMP_CODE") + "','" + rs2.getString("AQSL_NO") + "','LOAN','','"+aqdtlsname+"')LOAN,"
                            + "GETADAMT_FROM_AQDTLS('" + rs2.getString("EMP_CODE") + "','" + rs2.getString("AQSL_NO") + "','CMPA','','"+aqdtlsname+"')COMPADV ,GETADAMT_FROM_AQDTLS('" + rs2.getString("EMP_CODE") + "','" + rs2.getString("AQSL_NO") + "','HC','','"+aqdtlsname+"')HC,GETADAMT_FROM_AQDTLS('" + rs2.getString("EMP_CODE") + "','" + rs2.getString("AQSL_NO") + "','GA','','"+aqdtlsname+"')GA,"
                            + "GETADAMT_FROM_AQDTLS('" + rs2.getString("EMP_CODE") + "','" + rs2.getString("AQSL_NO") + "','OT','','"+aqdtlsname+"')OT");
                    if (rs3.next()) {
                        da = rs3.getInt("DA");
                        if (rs3.getInt("GP") > 0) {
                            gp = rs3.getInt("GP");
                        } else if (rs3.getInt("DP") > 0) {
                            gp = rs3.getInt("DP");
                        }
                        ir = rs3.getInt("IR");
                        oa = rs3.getInt("OA");
                        hra = rs3.getInt("HRA");
                        ot = rs3.getInt("OT");
                        
                        pt = rs3.getInt("PT");
                        it = rs3.getInt("IT");
                        lic = rs3.getInt("LIC");
                        gpf = rs3.getInt("GPF");
                        hbap = rs3.getInt("HBAP");
                        hbai = rs3.getInt("HBAI");
                        hrr = rs3.getInt("HRR");
                        wrr = rs3.getInt("WRR");
                        hc = rs3.getInt("HC");
                        cmpa = rs3.getInt("COMPADV");
                        gis = rs3.getInt("GIS");
                        mopap = rs3.getInt("MOPAP");
                        mopai = rs3.getInt("MOPAI");
                        mcap = rs3.getInt("MCAP");
                        mcai = rs3.getInt("MCAI");
                        fa = rs3.getInt("FA");
                        ga = rs3.getInt("GA");
                        loan = rs3.getInt("LOAN");
                    }

                    gross = basic + da + ir + oa + hra + gp + ot;
                    //loan=hbap+hbai+mopap+mopai+mcap+mcai+gis+fa+cmpa;
                    totdeduct = gpf + pt + it + lic + hrr + wrr + hc + hbap + hbai + mopap + mopai + mcap + mcai + gis + fa + cmpa;
                    net = gross - totdeduct;
                    label = new Label(1, row, CalendarCommonMethods.getFullNameMonthAsString(rs2.getInt("AQ_MONTH")) + "-" + rs2.getString("AQ_YEAR"), innercell);//column,row
                    sheet.addCell(label);

                    label = new Label(2, row, rs2.getString("BILL_DESC"), innercell);
                    sheet.addCell(label);

                    num = new Number(3, row, basic, innercell) {
                    };//column,row
                    sheet.addCell(num);
                    num = new Number(4, row, gp, innercell);//column,row
                    sheet.addCell(num);
                    num = new Number(5, row, ir, innercell);//column,row
                    sheet.addCell(num);
                    num = new Number(6, row, da, innercell);
                    sheet.addCell(num);
                    num = new Number(7, row, hra, innercell);
                    sheet.addCell(num);
                    num = new Number(8, row, oa, innercell);//column,row
                    sheet.addCell(num);
                    num = new Number(9, row, ot, innercell);//column,row
                    sheet.addCell(num);
                    num = new Number(10, row, gross, innercell);
                    sheet.addCell(num);
                    System.out.println("GPF Amt is: " + gpf);
                    num = new Number(11, row, gpf, innercell);
                    sheet.addCell(num);
                    num = new Number(12, row, pt, innercell);
                    sheet.addCell(num);
                    num = new Number(13, row, it, innercell);
                    sheet.addCell(num);
                    num = new Number(14, row, lic, innercell);
                    sheet.addCell(num);
                    num = new Number(15, row, hrr, innercell);
                    sheet.addCell(num);
                    num = new Number(16, row, wrr, innercell);
                    sheet.addCell(num);
                    num = new Number(17, row, mopap, innercell);
                    sheet.addCell(num);
                    num = new Number(18, row, mopai, innercell);
                    sheet.addCell(num);
                    num = new Number(19, row, mcap, innercell);
                    sheet.addCell(num);
                    num = new Number(20, row, mcai, innercell);
                    sheet.addCell(num);
                    num = new Number(21, row, hc, innercell);
                    sheet.addCell(num);
                    num = new Number(22, row, cmpa, innercell);
                    sheet.addCell(num);
                    num = new Number(23, row, gis, innercell);
                    sheet.addCell(num);
                    num = new Number(24, row, ga, innercell);
                    sheet.addCell(num);
                    num = new Number(25, row, fa, innercell);
                    sheet.addCell(num);
                    num = new Number(26, row, hbap, innercell);
                    sheet.addCell(num);
                    num = new Number(27, row, hbai, innercell);
                    sheet.addCell(num);
                    num = new Number(28, row, loan, innercell);
                    sheet.addCell(num);
                    num = new Number(29, row, totdeduct, innercell);
                    sheet.addCell(num);
                    num = new Number(30, row, net, innercell);
                    sheet.addCell(num);
                    totbasic = totbasic + basic;
                    totgp = totgp + gp;
                    totir = totir + ir;
                    totda = totda + da;
                    tothra = tothra + hra;
                    totoa = totoa + oa;
                    totot = totot + ot;
                    totgross = totgross + gross;
                    totgpf = totgpf + gpf;
                    totpt = totpt + pt;
                    totit = totit + it;
                    totlic = totlic + lic;
                    tothbap = tothbap + hbap;
                    tothbai = tothbai + hbai;
                    totmcap = totmcap + mcap;
                    totmcai = totmcai + mcai;
                    totmopap = totmopap + mopap;
                    totmopai = totmopai + mopai;
                    totfa = totfa + fa;
                    totgis = totgis + gis;
                    totga = totga + ga;
                    tothc = tothc + hc;
                    totcompadv = totcompadv + cmpa;
                    tothrr = tothrr + hrr;
                    totwrr = totwrr + wrr;
                    totloan = totloan + loan;
                    totded = totded + totdeduct;
                    totnet = totnet + net;
                    row++;
                }
            }
            label = new Label(1, row, "Total", innercell);//column,row
            sheet.addCell(label);
            num = new Number(3, row, totbasic, innercell);//column,row
            sheet.addCell(num);
            num = new Number(4, row, totgp, innercell);//column,row
            sheet.addCell(num);
            num = new Number(5, row, totir, innercell);//column,row
            sheet.addCell(num);
            num = new Number(6, row, totda, innercell);
            sheet.addCell(num);
            num = new Number(7, row, tothra, innercell);
            sheet.addCell(num);
            num = new Number(8, row, totoa, innercell);//column,row
            sheet.addCell(num);
            num = new Number(9, row, totot, innercell);//column,row
            sheet.addCell(num);
            num = new Number(10, row, totgross, innercell);
            sheet.addCell(num);
            num = new Number(11, row, totgpf, innercell);
            sheet.addCell(num);
            num = new Number(12, row, totpt, innercell);
            sheet.addCell(num);
            num = new Number(13, row, totit, innercell);
            sheet.addCell(num);
            num = new Number(14, row, totlic, innercell);
            sheet.addCell(num);
            num = new Number(15, row, tothrr, innercell);
            sheet.addCell(num);
            num = new Number(16, row, totwrr, innercell);
            sheet.addCell(num);
            num = new Number(17, row, totmopap, innercell);
            sheet.addCell(num);
            num = new Number(18, row, totmopai, innercell);
            sheet.addCell(num);
            num = new Number(19, row, totmcap, innercell);
            sheet.addCell(num);
            num = new Number(20, row, totmcai, innercell);
            sheet.addCell(num);
            num = new Number(21, row, tothc, innercell);
            sheet.addCell(num);
            num = new Number(22, row, totcompadv, innercell);
            sheet.addCell(num);
            num = new Number(23, row, totgis, innercell);
            sheet.addCell(num);
            num = new Number(24, row, totga, innercell);
            sheet.addCell(num);
            num = new Number(25, row, totfa, innercell);
            sheet.addCell(num);
            num = new Number(26, row, tothbap, innercell);
            sheet.addCell(num);
            num = new Number(27, row, tothbai, innercell);
            sheet.addCell(num);
            num = new Number(28, row, totloan, innercell);
            sheet.addCell(num);
            num = new Number(29, row, totded, innercell);
            sheet.addCell(num);
            num = new Number(30, row, totnet, innercell);
            sheet.addCell(num);
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs,stmt);
            DataBaseFunctions.closeSqlObjects(rs3,st3);
            DataBaseFunctions.closeSqlObjects(rs2,rs3);
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
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

            if (aqYear < 2020) {
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
}
