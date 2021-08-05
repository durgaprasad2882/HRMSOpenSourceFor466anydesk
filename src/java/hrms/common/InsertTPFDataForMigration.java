package hrms.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class InsertTPFDataForMigration {

    public static void main(String args[]) {

        Connection con = null;

        PreparedStatement pst = null;

        InputStream inputStream = null;
        Workbook workbook = null;

        int counter = 0;

        String billNo = "";
        String gpfSeries = "";
        String gpfAccNo = "";
        String gpfNo = "";
        String empName = "";
        Date salFromDateDt = new Date();
        Date salToDateDt = new Date();
        String salFromDate = "";
        String salToDate = "";
        double tpfAmt = 0;
        String curInstlNo = "";
        String totalInstlNo = "";
        double subMonth = 0;
        double subYear = 0;
        Date voucherDateDt = new Date();
        String voucherDate = "";
        String importMonth = "JAN";
        try {
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection("jdbc:postgresql://172.16.1.16/hrmis", "hrmis2", "cmgi");
            //con = DriverManager.getConnection("jdbc:postgresql://192.168.1.19/hrmis", "hrmis2", "cmgi");

            String sql = "insert into tpf_data_migration(bill_no,gpf_series,gpf_acc_no,gpf_no,emp_name,sal_from_date,sal_to_date,tpf_amt,cur_instl_no,total_instl_no,subs_month,subs_year,voucher_date,import_month) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pst = con.prepareStatement(sql);

            File beneficiaryFile = new File("C:\\TPF_Excel\\JAN_DATA.xlsx");
            inputStream = new FileInputStream(beneficiaryFile);

            //workbook = new HSSFWorkbook(inputStream);
            workbook = new XSSFWorkbook(inputStream);

            Sheet firstSheet = workbook.getSheetAt(0);
            int noofRows = firstSheet.getLastRowNum();
            for (int r = 1; r <= noofRows; r++) {
                counter += 1;
                Row row = firstSheet.getRow(r);
                int index = 0;
                if (row != null) {
                    for (int count = 0; count < row.getLastCellNum(); count++) {
                        Cell cell = row.getCell(count, Row.RETURN_BLANK_AS_NULL);
                        if (cell == null || cell.equals("")) {
                            continue;
                        }
                        index++;
                        /*if (index == 6) {
                            salFromDateDt = cell.getDateCellValue();
                        }
                        if (index == 7) {
                            salToDateDt = cell.getDateCellValue();
                        }*/
                        if (index == 13) {
                            voucherDateDt = cell.getDateCellValue();
                        }
                        switch (cell.getCellType()) {
                            case Cell.CELL_TYPE_STRING:
                                if (index == 2) {
                                    billNo = cell.getStringCellValue();
                                }
                                if (index == 3) {
                                    gpfSeries = cell.getStringCellValue();
                                }
                                if (index == 4) {
                                    gpfAccNo = cell.getStringCellValue();
                                }
                                if (index == 5) {
                                    empName = cell.getStringCellValue();
                                }
                                if (index == 6) {
                                    //salFromDate = cell.getStringCellValue();
                                }
                                if (index == 7) {
                                    //salToDate = cell.getStringCellValue();
                                }
                                if (index == 8) {
                                    //tpfAmt = cell.getStringCellValue();
                                }
                                if (index == 9) {
                                    curInstlNo = cell.getStringCellValue();
                                }
                                if (index == 10) {
                                    totalInstlNo = cell.getStringCellValue();
                                }
                                if (index == 11) {
                                    //subMonth = cell.getStringCellValue();
                                }
                                if (index == 12) {
                                    //subYear = cell.getStringCellValue();
                                }
                                if (index == 13) {
                                    //voucherDateDt = cell.getDateCellValue();
                                }
                                break;
                            case Cell.CELL_TYPE_NUMERIC:
                                if (index == 8) {
                                    tpfAmt = cell.getNumericCellValue();
                                }
                                if (index == 11) {
                                    subMonth = cell.getNumericCellValue();
                                }
                                if (index == 12) {
                                    subYear = cell.getNumericCellValue();
                                }
                                break;
                            case Cell.CELL_TYPE_BLANK:
                                break;
                        }
                    }
                    empName = empName.trim();
                    gpfNo = gpfSeries + gpfAccNo;
                    //salFromDate = new SimpleDateFormat("dd-MM-yyyy").format(salFromDateDt);
                    //salToDate = new SimpleDateFormat("dd-MM-yyyy").format(salToDateDt);
                    voucherDate = new SimpleDateFormat("dd-MM-yyyy").format(voucherDateDt);
                    
                    System.out.println(counter + " Bill No: "+billNo+" and Name: " + empName + " and GPF Acc No: " + gpfAccNo);
                    
                    if (billNo != null && !billNo.equals("")) {
                        pst.setInt(1, Integer.parseInt(billNo));
                    } else {
                        pst.setInt(1, 0);
                    }
                    pst.setString(2, gpfSeries);
                    pst.setString(3, gpfAccNo);
                    pst.setString(4, gpfNo);
                    pst.setString(5, empName);
                    if (salFromDate != null && !salFromDate.equals("") && !salFromDate.equals("0")) {
                        pst.setTimestamp(6, new Timestamp(new SimpleDateFormat("dd-MM-yyyy").parse(salFromDate).getTime()));
                    } else {
                        pst.setTimestamp(6, null);
                    }
                    if (salToDate != null && !salToDate.equals("") && !salToDate.equals("0")) {
                        pst.setTimestamp(7, new Timestamp(new SimpleDateFormat("dd-MM-yyyy").parse(salToDate).getTime()));
                    } else {
                        pst.setTimestamp(7, null);
                    }
                    pst.setInt(8, (int) tpfAmt);
                    pst.setInt(9, Integer.parseInt(curInstlNo));
                    pst.setInt(10, Integer.parseInt(totalInstlNo));
                    pst.setInt(11, (int) subMonth);
                    pst.setInt(12, (int) subYear);
                    if (voucherDate != null && !voucherDate.equals("")) {
                        pst.setTimestamp(13, new Timestamp(new SimpleDateFormat("dd-MM-yyyy").parse(voucherDate).getTime()));
                    } else {
                        pst.setTimestamp(13, null);
                    }
                    pst.setString(14, importMonth);
                    pst.executeUpdate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException ie) {
                ie.printStackTrace();
            }
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }

    }

}
