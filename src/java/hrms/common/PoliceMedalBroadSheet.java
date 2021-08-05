package hrms.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.apache.commons.lang.StringUtils;

public class PoliceMedalBroadSheet {

    public static void main(String args[]) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        OutputStream out = null;
        int row = 0;
        try {
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection("jdbc:postgresql://172.16.1.14:6432/hrmis", "hrmis2", "cmgi");
            //con = DriverManager.getConnection("jdbc:postgresql://192.168.1.19/hrmis", "hrmis2", "hrmis2");

            out = new FileOutputStream(new File("/home/cmgi/AGLTA/PresidentPoliceMedalBroadSheet.xls"));

            WritableWorkbook workbook = Workbook.createWorkbook(out);

            WritableSheet sheet = workbook.createSheet("Broad Sheet", 0);

            WritableFont headformat = new WritableFont(WritableFont.ARIAL, 15, WritableFont.BOLD);
            WritableCellFormat headcell = new WritableCellFormat(headformat);
            headcell.setAlignment(Alignment.CENTRE);
            headcell.setVerticalAlignment(VerticalAlignment.CENTRE);
            headcell.setWrap(true);
            headcell.setBorder(Border.ALL, BorderLineStyle.THIN);

            WritableFont headformat2 = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);
            WritableCellFormat innerheadcell = new WritableCellFormat(headformat2);
            innerheadcell.setAlignment(Alignment.CENTRE);
            innerheadcell.setVerticalAlignment(VerticalAlignment.CENTRE);
            innerheadcell.setWrap(true);
            innerheadcell.setBorder(Border.ALL, BorderLineStyle.THIN);

            WritableFont innerformat = new WritableFont(WritableFont.ARIAL, 12, WritableFont.NO_BOLD);

            WritableCellFormat innercell = new WritableCellFormat(innerformat);
            innercell.setWrap(true);
            innercell.setBorder(Border.ALL, BorderLineStyle.THIN);

            WritableCellFormat headcellvertical = new WritableCellFormat(headformat);
            headcellvertical.setWrap(true);
            headcellvertical.setBorder(Border.ALL, BorderLineStyle.THIN);
            headcellvertical.setOrientation(jxl.format.Orientation.PLUS_90);

            WritableCellFormat innercellvertical = new WritableCellFormat(innerformat);
            innercellvertical.setWrap(true);
            innercellvertical.setBorder(Border.ALL, BorderLineStyle.THIN);
            innercellvertical.setOrientation(jxl.format.Orientation.PLUS_90);

            Label label = new Label(0, row, "PRESIDENT'S POLICE MEDAL FOR DISTINGUISHED SERVICE/POLICE MEDALS FOR MERITORIOS SERVICE ON THE OCCASION OF INDEPENDENCE DAY,2021", headcell);//column,row
            sheet.setRowView(row, 30 * 30);
            sheet.addCell(label);
            sheet.mergeCells(0, row, 20, row);

            row = row + 1;

            sheet.mergeCells(0, row, 0, row + 1);
            label = new Label(0, row, "SL No", innerheadcell);
            sheet.setRowView(row, 60 * 30);
            sheet.addCell(label);
            sheet.mergeCells(1, row, 1, row + 1);
            label = new Label(1, row, "Name", innerheadcell);
            sheet.setColumnView(1, 30);
            sheet.addCell(label);
            sheet.mergeCells(2, row, 2, row + 1);
            label = new Label(2, row, "Designation", innerheadcell);
            sheet.setColumnView(2, 30);
            sheet.addCell(label);
            sheet.mergeCells(3, row, 3, row + 1);
            label = new Label(3, row, "D.O.B", innerheadcell);
            sheet.setColumnView(3, 20);
            sheet.addCell(label);
            sheet.mergeCells(4, row, 4, row + 1);
            label = new Label(4, row, "Age as on\n15.08.2021\n(Y-M-D)", innerheadcell);
            sheet.setColumnView(4, 20);
            sheet.addCell(label);
            sheet.mergeCells(5, row, 5, row + 1);
            label = new Label(5, row, "Year of Joining", innerheadcell);
            sheet.setColumnView(5, 20);
            sheet.addCell(label);
            sheet.mergeCells(6, row, 6, row + 1);
            label = new Label(6, row, "Service as on\n15.08.2021\n(Y-M-D)", innerheadcell);
            sheet.setColumnView(6, 20);
            sheet.addCell(label);
            sheet.mergeCells(7, row, 7, row + 1);
            label = new Label(7, row, "If Police Medal for Meritorius Service awarded (Year/Occasion)", headcellvertical);
            sheet.setColumnView(7, 15);
            sheet.addCell(label);
            sheet.mergeCells(8, row, 14, row);
            label = new Label(8, row, "Rewards", innerheadcell);
            sheet.setColumnView(8, 100);
            sheet.addCell(label);
            sheet.mergeCells(15, row, 17, row);
            label = new Label(15, row, "Punishment", innerheadcell);
            sheet.setColumnView(15, 20);
            sheet.addCell(label);
            sheet.mergeCells(18, row, 27, row);
            label = new Label(18, row, "ACR Grading of preceeding 10 Years", innerheadcell);
            sheet.setColumnView(18, 50);
            sheet.addCell(label);
            sheet.mergeCells(28, row, 28, row + 1);
            label = new Label(28, row, "Remarks", headcellvertical);
            sheet.setColumnView(28, 20);
            sheet.addCell(label);
            sheet.mergeCells(29, row, 29, row + 1);
            label = new Label(29, row, "Date of Recommendation by Range office /Heads of the organisation ", headcellvertical);
            sheet.setColumnView(29, 20);
            sheet.addCell(label);
            sheet.mergeCells(30, row, 30, row + 1);
            label = new Label(30, row, " Hard copy Page No.", headcellvertical);
            sheet.setColumnView(30, 20);
            sheet.addCell(label);

            row = row + 1;

            label = new Label(8, row, "Cash rewards in Rs", headcellvertical);
            sheet.setColumnView(8, 10);
            sheet.addCell(label);
            label = new Label(9, row, "Cash Rewards", headcellvertical);
            sheet.setColumnView(9, 10);
            sheet.addCell(label);
            label = new Label(10, row, "Commendation", headcellvertical);
            sheet.setColumnView(10, 10);
            sheet.addCell(label);
            label = new Label(11, row, "Appreciation", headcellvertical);
            sheet.setColumnView(11, 10);
            sheet.addCell(label);
            label = new Label(12, row, "Good Services", headcellvertical);
            sheet.setColumnView(12, 10);
            sheet.addCell(label);
            label = new Label(13, row, "Others", headcellvertical);
            sheet.setColumnView(13, 10);
            sheet.addCell(label);
            label = new Label(14, row, "Total", headcellvertical);
            sheet.setColumnView(14, 10);
            sheet.addCell(label);
            label = new Label(15, row, "Major", headcellvertical);
            sheet.setColumnView(15, 10);
            sheet.addCell(label);
            label = new Label(16, row, "Minor", headcellvertical);
            sheet.setColumnView(16, 10);
            sheet.addCell(label);
            label = new Label(17, row, "Total", headcellvertical);
            sheet.setColumnView(17, 10);
            sheet.addCell(label);
            label = new Label(18, row, "2010-11", headcellvertical);
            sheet.setColumnView(18, 10);
            sheet.addCell(label);
            label = new Label(19, row, "2011-12", headcellvertical);
            sheet.setColumnView(19, 10);
            sheet.addCell(label);
            label = new Label(20, row, "2012-13", headcellvertical);
            sheet.setColumnView(20, 10);
            sheet.addCell(label);
            label = new Label(21, row, "2013-14", headcellvertical);
            sheet.setColumnView(21, 10);
            sheet.addCell(label);
            label = new Label(22, row, "2014-15", headcellvertical);
            sheet.setColumnView(22, 10);
            sheet.addCell(label);
            label = new Label(23, row, "2015-16", headcellvertical);
            sheet.setColumnView(23, 10);
            sheet.addCell(label);
            label = new Label(24, row, "2016-17", headcellvertical);
            sheet.setColumnView(24, 10);
            sheet.addCell(label);
            label = new Label(25, row, "2017-18", headcellvertical);
            sheet.setColumnView(25, 10);
            sheet.addCell(label);
            label = new Label(26, row, "2018-19", headcellvertical);
            sheet.setColumnView(26, 10);
            sheet.addCell(label);
            label = new Label(27, row, "2019-20", headcellvertical);
            sheet.setColumnView(27, 10);
            sheet.addCell(label);

            int i = 0;

            SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");

            pst = con.prepareStatement("select off_name, full_name, designation, dob , to_char(dob, 'DD-Mon-YYYY') dateofbirth, to_char(doa, 'DD-Mon-YYYY') doa , to_char(doc_present_rank, 'DD-Mon-YYYY') doc_present_rank , doj_dist_est, money_reward, commendation,"
                    + "gs_mark, appreciation, aar, punishment_major, punishment_minor, award_medal_previous_year, award_medal_rank,"
                    + "award_medal_posting_place, previously_awarded, brief_note, recommend_by_dist, recommend_by_range,range_submitted_on,"
                    + "not_recommend_reason_by_range, dpcifany, disc_details, further_information_by_range,age_in_year, age_in_month,first_name,middle_name,last_name,father_first_name,father_second_name,father_last_name,category,"
                    + "initial_appoint_date,initial_appoint_service,initial_appoint_category,total_police_service_years,total_police_service_months,total_police_service_days,"
                    + "present_posting_designation,present_posting_address,present_posting_place,present_posting_pin,present_posting_date,"
                    + "if_deputation,deputation_doj,rewards_no,rewards_total_amt,rewards_cash_awards,rewards_commendation,rewards_appreciation,rewards_good_service,rewards_any_other,"
                    + "medal_meritorious_service_year,medal_meritorious_occassion,punishment_penalty_details,punishment_penalty_year,punishment_penalty_order_no,punishment_penalty_order_date,"
                    + "medical_category,pending_enquiry,dpc_year,dpc_nature,dpc_status,court_case_pending_year,court_case_pending_details,court_case_pending_status,"
                    + "acr_grading_1,acr_grading_2,acr_grading_3,acr_grading_4,acr_grading_5,acr_grading_6,acr_grading_7,acr_grading_8,acr_grading_9,acr_grading_10,acr_grading_11,"
                    + "acr_grading_12,acr_grading_13,acr_grading_14,acr_grading_os,acr_grading_vg,acr_grading_good,acr_grading_avg,acr_grading_nic,acr_grading_adv,acr_grading_ms,"
                    + "acr_grading_na,email,mobile,brief_description,pending_enquiry_note,rewards_cash_awards_amt,rewards_commendation_amt,rewards_appreciation_amt,rewards_good_service_amt,rewards_any_other_desc from police_award_form"
                    + " where award_medal_type_id='09' and award_medal_year=? and recommend_by_range='RECOMMENDED'"
                    + " order by designation, full_name ");
            pst.setInt(1, 2021);
            rs = pst.executeQuery();
            while (rs.next()) {
                
                row = row + 1;

                i = i + 1;
                
                System.out.println("SL No is:" + i);

                String totalService = StringUtils.defaultString(rs.getString("total_police_service_years")) + "-" + StringUtils.defaultString(rs.getString("total_police_service_months")) + "-" + StringUtils.defaultString(rs.getString("total_police_service_days"));

                String dob = CommonFunctions.getFormattedOutputDate1(rs.getTimestamp("dob"));
                Date date = formatter.parse(dob);
                //Converting obtained Date object to LocalDate object
                Instant instant = date.toInstant();
                ZonedDateTime zone = instant.atZone(ZoneId.systemDefault());
                LocalDate givenDate = zone.toLocalDate();

                Date date2 = formatter.parse("15-AUG-2021");

                //Converting obtained Date object to LocalDate object
                Instant instant2 = date2.toInstant();
                ZonedDateTime zone2 = instant2.atZone(ZoneId.systemDefault());
                LocalDate nowDate = zone2.toLocalDate();

                //Calculating the difference between given date to current date.
                Period period = Period.between(givenDate, nowDate);

                String employeeage = period.getYears() + "-" + period.getMonths() + "-" + period.getDays();

                sheet.mergeCells(0, row, 0, row);
                label = new Label(0, row, i + "", innercell);
                sheet.setRowView(row, 60 * 30);
                sheet.addCell(label);
                sheet.mergeCells(1, row, 1, row);
                label = new Label(1, row, StringUtils.defaultString(rs.getString("full_name")), innercell);
                sheet.setColumnView(1, 30);
                sheet.addCell(label);
                sheet.mergeCells(2, row, 2, row);
                label = new Label(2, row, StringUtils.defaultString(rs.getString("designation")), innercell);
                sheet.setColumnView(2, 30);
                sheet.addCell(label);
                sheet.mergeCells(3, row, 3, row);
                label = new Label(3, row, StringUtils.defaultString(rs.getString("dateofbirth")), innercell);
                sheet.setColumnView(3, 20);
                sheet.addCell(label);
                sheet.mergeCells(4, row, 4, row);
                label = new Label(4, row, employeeage, innercell);
                sheet.setColumnView(4, 20);
                sheet.addCell(label);
                sheet.mergeCells(5, row, 5, row);
                label = new Label(5, row, "", innercell); //Year of Joining
                sheet.setColumnView(5, 20);
                sheet.addCell(label);
                sheet.mergeCells(6, row, 6, row);
                label = new Label(6, row, totalService, innercell);
                sheet.setColumnView(6, 20);
                sheet.addCell(label);
                sheet.mergeCells(7, row, 7, row);
                label = new Label(7, row, StringUtils.defaultString(rs.getString("medal_meritorious_service_year")) + "/" + StringUtils.defaultString(rs.getString("medal_meritorious_occassion")), innercellvertical);
                sheet.setColumnView(7, 15);
                sheet.addCell(label);
                label = new Label(8, row, StringUtils.defaultString(rs.getString("rewards_total_amt")), innercellvertical);
                sheet.setColumnView(8, 10);
                sheet.addCell(label);
                label = new Label(9, row, StringUtils.defaultString(rs.getString("rewards_cash_awards")), innercellvertical);
                sheet.setColumnView(9, 10);
                sheet.addCell(label);
                label = new Label(10, row, StringUtils.defaultString(rs.getString("rewards_commendation")), innercellvertical);
                sheet.setColumnView(10, 10);
                sheet.addCell(label);
                label = new Label(11, row, StringUtils.defaultString(rs.getString("rewards_appreciation")), innercellvertical);
                sheet.setColumnView(11, 10);
                sheet.addCell(label);
                label = new Label(12, row, StringUtils.defaultString(rs.getString("rewards_good_service")), innercellvertical);
                sheet.setColumnView(12, 10);
                sheet.addCell(label);
                label = new Label(13, row, StringUtils.defaultString(rs.getString("rewards_any_other")), innercellvertical);
                sheet.setColumnView(13, 10);
                sheet.addCell(label);
                label = new Label(14, row, "", innercellvertical); //Total
                sheet.setColumnView(14, 10);
                sheet.addCell(label);
                label = new Label(15, row, "", innercellvertical); //Major
                sheet.setColumnView(15, 10);
                sheet.addCell(label);
                label = new Label(16, row, "", innercellvertical); //Minor
                sheet.setColumnView(16, 10);
                sheet.addCell(label);
                label = new Label(17, row, "", innercellvertical); //Total
                sheet.setColumnView(17, 10);
                sheet.addCell(label);
                label = new Label(18, row, StringUtils.defaultString(rs.getString("acr_grading_4")), innercellvertical);
                sheet.setColumnView(18, 10);
                sheet.addCell(label);
                label = new Label(19, row, StringUtils.defaultString(rs.getString("acr_grading_5")), innercellvertical);
                sheet.setColumnView(19, 10);
                sheet.addCell(label);
                label = new Label(20, row, StringUtils.defaultString(rs.getString("acr_grading_6")), innercellvertical);
                sheet.setColumnView(20, 10);
                sheet.addCell(label);
                label = new Label(21, row, StringUtils.defaultString(rs.getString("acr_grading_7")), innercellvertical);
                sheet.setColumnView(21, 10);
                sheet.addCell(label);
                label = new Label(22, row, StringUtils.defaultString(rs.getString("acr_grading_8")), innercellvertical);
                sheet.setColumnView(22, 10);
                sheet.addCell(label);
                label = new Label(23, row, StringUtils.defaultString(rs.getString("acr_grading_9")), innercellvertical);
                sheet.setColumnView(23, 10);
                sheet.addCell(label);
                label = new Label(24, row, StringUtils.defaultString(rs.getString("acr_grading_10")), innercellvertical);
                sheet.setColumnView(24, 10);
                sheet.addCell(label);
                label = new Label(25, row, StringUtils.defaultString(rs.getString("acr_grading_11")), innercellvertical);
                sheet.setColumnView(25, 10);
                sheet.addCell(label);
                label = new Label(26, row, StringUtils.defaultString(rs.getString("acr_grading_12")), innercellvertical);
                sheet.setColumnView(26, 10);
                sheet.addCell(label);
                label = new Label(27, row, StringUtils.defaultString(rs.getString("acr_grading_13")), innercellvertical);
                sheet.setColumnView(27, 10);
                sheet.addCell(label);
                label = new Label(28, row, "", innercellvertical);//StringUtils.defaultString(rs.getString("brief_description"))
                sheet.setColumnView(28, 20);
                sheet.addCell(label);
                sheet.mergeCells(29, row, 29, row);
                label = new Label(29, row, CommonFunctions.getFormattedOutputDate1(rs.getDate("range_submitted_on")), innercellvertical);
                sheet.setColumnView(29, 20);
                sheet.addCell(label);
                sheet.mergeCells(30, row, 30, row);
                label = new Label(30, row, "", innercellvertical); //Hard copy Page No.
                sheet.setColumnView(30, 20);
                sheet.addCell(label);
            }
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
        }
    }
}
