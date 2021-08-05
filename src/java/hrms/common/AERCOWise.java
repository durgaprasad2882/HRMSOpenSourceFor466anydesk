/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class AERCOWise {

    public static void main(String args[]) {

        Connection con = null;

        PreparedStatement copst = null;
        ResultSet cors = null;

        PreparedStatement offpst = null;
        ResultSet offrs = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        OutputStream out = null;

        String[] postgrpArr = {"A", "B", "C", "D"};

        int sanctionedStrengthTotal = 0;
        int menInPositionTotal = 0;
        int vacancyTotal = 0;
        try {
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection("jdbc:postgresql://172.16.1.14:6432/hrmis", "hrmis2", "cmgi");
            //con = DriverManager.getConnection("jdbc:postgresql://192.168.1.19/hrmis", "hrmis2", "hrmis2");

            out = new FileOutputStream(new File("/home/cmgi/AGLTA/AER/PRD_AER.xls"));

            WritableWorkbook workbook = Workbook.createWorkbook(out);
            WritableSheet sheet = null;

            String cocodequery = "select co_off_code from ddo_to_co_mapping where co_off_code like '%PRD%' and co_off_code not like 'OLS%' group by co_off_code";
            copst = con.prepareStatement(cocodequery);
            cors = copst.executeQuery();
            while (cors.next()) {
                String coOffCode = cors.getString("co_off_code");

                sheet = workbook.createSheet(coOffCode, 0);

                sanctionedStrengthTotal = 0;
                menInPositionTotal = 0;
                vacancyTotal = 0;

                WritableFont headformat = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);

                WritableCellFormat headcell = new WritableCellFormat(headformat);
                headcell.setAlignment(Alignment.CENTRE);
                headcell.setVerticalAlignment(VerticalAlignment.CENTRE);
                headcell.setWrap(true);
                headcell.setBorder(Border.ALL, BorderLineStyle.THIN);

                WritableFont innerformat = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD);

                WritableCellFormat innercell = new WritableCellFormat(innerformat);
                innercell.setBorder(Border.ALL, BorderLineStyle.THIN);

                Number num = null;

                int row = 0;

                Label label = new Label(0, row, "PART-A(Regular Establishment)", headcell);//column,row
                sheet.addCell(label);
                sheet.mergeCells(0, row, 8, row);

                row = row + 1;

                label = new Label(0, row, "Category of employee", headcell);
                sheet.setColumnView(0, 10);
                sheet.addCell(label);
                label = new Label(1, row, "Description of the Posts", headcell);
                sheet.setColumnView(1, 35);
                sheet.addCell(label);
                label = new Label(2, row, "Pay Scale", headcell);
                sheet.addCell(label);
                sheet.mergeCells(2, row, 3, row);
                label = new Label(4, row, "As per 7th Pay", headcell);
                sheet.setColumnView(4, 10);
                sheet.addCell(label);
                label = new Label(5, row, "Sanctioned Strength", headcell);
                sheet.setColumnView(5, 11);
                sheet.addCell(label);
                label = new Label(6, row, "Person-in-Position", headcell);
                sheet.setColumnView(6, 10);
                sheet.addCell(label);
                label = new Label(7, row, "Vacancy Position", headcell);
                sheet.setColumnView(7, 10);
                sheet.addCell(label);
                label = new Label(8, row, "Remarks", headcell);
                sheet.setColumnView(8, 10);
                sheet.addCell(label);

                row = row + 1;

                label = new Label(0, row, "", headcell);
                sheet.addCell(label);
                label = new Label(1, row, "", headcell);
                sheet.addCell(label);
                label = new Label(2, row, "As per 6th Pay Commission", headcell);
                sheet.setColumnView(2, 30);
                sheet.addCell(label);
                label = new Label(3, row, "GP", headcell);
                sheet.addCell(label);
                label = new Label(4, row, "", headcell);
                sheet.addCell(label);
                label = new Label(5, row, "", headcell);
                sheet.addCell(label);
                label = new Label(6, row, "", headcell);
                sheet.addCell(label);
                label = new Label(7, row, "", headcell);
                sheet.addCell(label);
                label = new Label(8, row, "", headcell);
                sheet.addCell(label);

                String officecodes = "";

                String offsql = "select * from ddo_to_co_mapping where co_off_code=?";
                offpst = con.prepareStatement(offsql);
                offpst.setString(1, coOffCode);
                offrs = offpst.executeQuery();
                while (offrs.next()) {
                    if (officecodes.equals("")) {
                        officecodes = "'" + offrs.getString("ddo_off_code") + "'";
                    } else {
                        officecodes = officecodes + ",'" + offrs.getString("ddo_off_code") + "'";
                    }
                }
                System.out.println("officecodes is: " + officecodes);
                //String officecodes = "'ANGWOR0020000','BAMWOR0010000','BAMWOR0020000','BAMWOR0030000','BAMWOR0050000','BAMWOR0060000','BBSWOR0010000','BDHWOR0020000','BDKWOR0010000','BGRWOR0010000','BLGWOR0010000','BLGWOR0020000','BLGWOR0040000','BLSWOR0010000','BLSWOR0020000','BLSWOR0040000','BLSWOR0060000','CTCWOR0010000','CTCWOR0030000','CTCWOR0040000','CTCWOR0060000','CTSWOR0010000','CTSWOR0020000','DGRWOR0020000','DKLWOR0020000','DKLWOR0030000','GJMWOR0010000','GJPWOR0010000''JPRWOR0010000','JSDWOR0010000','JSDWOR0020000''JSPWOR0010000','JYRWOR0010000','JYRWOR0020000','KHDWOR0020000','KJRWOR0010000','KJRWOR0040000','KJRWOR0050000','KJRWOR0060000','KLDWOR0010000','KPDWOR0010000','KPDWOR0020000','KPTWOR0010000','KPTWOR0030000','KPTWOR0040000','KRDWOR0030000','KRDWOR0050000','KRDWOR0060000','KRDWOR0070000','KRDWOR0090000','KRDWOR0100000','KRDWOR0110000','KRDWOR0120000','KRDWOR0140000','KRDWOR0150000','KRDWOR0160000','KRDWOR0190000','KRDWOR0200000','KRDWOR0210000','KRDWOR0230000','MBJWOR0010000','MBJWOR0030000','MKGWOR0010000','NGRWOR0010000','NPRWOR0010000','NRGWOR0010000','PLBWOR0010000','PLBWOR0020000','PLBWOR0030000','PNPWOR0020000','PRIWOR0010000','RGDWOR0010000','RGDWOR0020000','RGDWOR0030000','RGDWOR0040000','SBPWOR0010000','SBPWOR0020000','SBPWOR0040000','SBPWOR0060000','SBPWOR0080000','SBPWOR0090000','SBPWOR0100000','SNGWOR0010000','SNPWOR0020000'";

                /*PART-A*/
                for (int i = 0; i < postgrpArr.length; i++) {

                    String postgroup = postgrpArr[i];

                    row = row + 1;

                    String sql = "";

                    int counter = 0;

                    sanctionedStrengthTotal = 0;
                    menInPositionTotal = 0;
                    vacancyTotal = 0;

                    if (postgroup.equals("A")) {

                        sql = "SELECT gpc,post, POST_GRP,level_7thpay,pay_scale,sanction_strength, GP FROM("
                                + " SELECT COUNT(*) sanction_strength,GPC,pay_scale,post_grp,level_7thpay, G_SPC.GP FROM bill_section_mapping"
                                + " inner join bill_group_master on bill_section_mapping.BILL_GROUP_ID=bill_group_master.BILL_GROUP_ID"
                                + " inner join g_section on bill_section_mapping.section_id=g_section.section_id"
                                + " INNER JOIN SECTION_POST_MAPPING ON BILL_SECTION_MAPPING.SECTION_ID = SECTION_POST_MAPPING.SECTION_ID"
                                + " INNER JOIN G_SPC ON SECTION_POST_MAPPING.SPC = G_SPC.SPC"
                                + " LEFT OUTER JOIN EMP_MAST ON SECTION_POST_MAPPING.SPC = EMP_MAST.CUR_SPC"
                                + " INNER JOIN G_CADRE ON EMP_MAST.CUR_CADRE_CODE=G_CADRE.CADRE_CODE"
                                + " WHERE bill_group_master.off_code in (" + officecodes + ")"
                                + " and g_section.bill_type='REGULAR' and bill_group_master.bill_type = '42' AND is_sanctioned='Y' and is_terminated <> 'Y' AND POST_GRP=? AND cadre_type='AIS'"
                                + " group by GPC,pay_scale,post_grp,level_7thpay, G_SPC.GP) G_SPC"
                                + " INNER JOIN G_POST ON G_SPC.GPC = G_POST.POST_CODE order by post_grp, level_7thpay desc,pay_scale desc, post";

                        pst = con.prepareStatement(sql);
                        pst.setString(1, postgroup);
                        rs = pst.executeQuery();
                        while (rs.next()) {

                            row = row + 1;

                            if (counter == 0) {
                                label = new Label(0, row, "Group " + postgroup, headcell);
                                sheet.addCell(label);
                                label = new Label(1, row, "All India Services", headcell);
                                sheet.addCell(label);
                                sheet.mergeCells(1, row, 8, row);

                                row = row + 1;
                            }

                            int meninposition = getMenInPosition(con, "REGULAR", rs.getString("level_7thpay"), "AIS", rs.getString("gpc"), rs.getString("pay_scale"), rs.getString("GP"), rs.getString("POST_GRP"));
                            int vacancy = rs.getInt("sanction_strength") - meninposition;

                            label = new Label(0, row, "", innercell);
                            sheet.addCell(label);
                            label = new Label(1, row, rs.getString("post"), innercell);
                            sheet.addCell(label);
                            label = new Label(2, row, rs.getString("pay_scale"), innercell);
                            sheet.addCell(label);
                            num = new Number(3, row, rs.getInt("GP"), innercell);
                            sheet.addCell(num);
                            label = new Label(4, row, rs.getString("level_7thpay"), innercell);
                            sheet.addCell(label);
                            num = new Number(5, row, rs.getInt("sanction_strength"), innercell);
                            sheet.addCell(num);
                            num = new Number(6, row, meninposition, innercell);
                            sheet.addCell(num);
                            num = new Number(7, row, vacancy, innercell);
                            sheet.addCell(num);
                            label = new Label(8, row, "", innercell);
                            sheet.addCell(label);

                            counter = counter + 1;

                            sanctionedStrengthTotal = sanctionedStrengthTotal + rs.getInt("sanction_strength");
                            menInPositionTotal = menInPositionTotal + meninposition;
                            vacancyTotal = vacancyTotal + vacancy;
                        }
                        DataBaseFunctions.closeSqlObjects(rs, pst);
                        counter = 0;
                        sql = "SELECT gpc,post, POST_GRP,level_7thpay,pay_scale,sanction_strength, GP FROM("
                                + " SELECT COUNT(*) sanction_strength,GPC,pay_scale,post_grp,level_7thpay, G_SPC.GP FROM bill_section_mapping"
                                + " inner join bill_group_master on bill_section_mapping.BILL_GROUP_ID=bill_group_master.BILL_GROUP_ID"
                                + " inner join g_section on bill_section_mapping.section_id=g_section.section_id"
                                + " INNER JOIN SECTION_POST_MAPPING ON BILL_SECTION_MAPPING.SECTION_ID = SECTION_POST_MAPPING.SECTION_ID"
                                + " INNER JOIN G_SPC ON SECTION_POST_MAPPING.SPC = G_SPC.SPC"
                                + " LEFT OUTER JOIN EMP_MAST ON SECTION_POST_MAPPING.SPC = EMP_MAST.CUR_SPC"
                                + " INNER JOIN G_CADRE ON EMP_MAST.CUR_CADRE_CODE=G_CADRE.CADRE_CODE"
                                + " WHERE bill_group_master.off_code in (" + officecodes + ")"
                                + " and g_section.bill_type='REGULAR' and bill_group_master.bill_type = '42' AND is_sanctioned='Y' and is_terminated <> 'Y' AND POST_GRP=? AND cadre_type='UGC'"
                                + " group by GPC,pay_scale,post_grp,level_7thpay, G_SPC.GP) G_SPC"
                                + " INNER JOIN G_POST ON G_SPC.GPC = G_POST.POST_CODE order by post_grp, level_7thpay desc,pay_scale desc, post";

                        pst = con.prepareStatement(sql);
                        pst.setString(1, postgroup);
                        rs = pst.executeQuery();
                        while (rs.next()) {

                            row = row + 1;

                            if (counter == 0) {
                                label = new Label(0, row, "Group " + postgroup, headcell);
                                sheet.addCell(label);
                                label = new Label(1, row, "UGC", headcell);
                                sheet.addCell(label);
                                sheet.mergeCells(1, row, 8, row);

                                row = row + 1;
                            }

                            int meninposition = getMenInPosition(con, "REGULAR", rs.getString("level_7thpay"), "UGC", rs.getString("gpc"), rs.getString("pay_scale"), rs.getString("GP"), rs.getString("POST_GRP"));
                            int vacancy = rs.getInt("sanction_strength") - meninposition;

                            label = new Label(0, row, "", innercell);
                            sheet.addCell(label);
                            label = new Label(1, row, rs.getString("post"), innercell);
                            sheet.addCell(label);
                            label = new Label(2, row, rs.getString("pay_scale"), innercell);
                            sheet.addCell(label);
                            num = new Number(3, row, rs.getInt("GP"), innercell);
                            sheet.addCell(num);
                            label = new Label(4, row, rs.getString("level_7thpay"), innercell);
                            sheet.addCell(label);
                            num = new Number(5, row, rs.getInt("sanction_strength"), innercell);
                            sheet.addCell(num);
                            num = new Number(6, row, meninposition, innercell);
                            sheet.addCell(num);
                            num = new Number(7, row, vacancy, innercell);
                            sheet.addCell(num);
                            label = new Label(8, row, "", innercell);
                            sheet.addCell(label);

                            counter = counter + 1;

                            sanctionedStrengthTotal = sanctionedStrengthTotal + rs.getInt("sanction_strength");
                            menInPositionTotal = menInPositionTotal + meninposition;
                            vacancyTotal = vacancyTotal + vacancy;
                        }
                        DataBaseFunctions.closeSqlObjects(rs, pst);
                        counter = 0;
                        sql = "SELECT gpc,post, POST_GRP,level_7thpay,pay_scale,sanction_strength, GP FROM("
                                + " SELECT COUNT(*) sanction_strength,GPC,pay_scale,post_grp,level_7thpay, G_SPC.GP FROM bill_section_mapping"
                                + " inner join bill_group_master on bill_section_mapping.BILL_GROUP_ID=bill_group_master.BILL_GROUP_ID"
                                + " inner join g_section on bill_section_mapping.section_id=g_section.section_id"
                                + " INNER JOIN SECTION_POST_MAPPING ON BILL_SECTION_MAPPING.SECTION_ID = SECTION_POST_MAPPING.SECTION_ID"
                                + " INNER JOIN G_SPC ON SECTION_POST_MAPPING.SPC = G_SPC.SPC"
                                + " LEFT OUTER JOIN EMP_MAST ON SECTION_POST_MAPPING.SPC = EMP_MAST.CUR_SPC"
                                + " INNER JOIN G_CADRE ON EMP_MAST.CUR_CADRE_CODE=G_CADRE.CADRE_CODE"
                                + " WHERE bill_group_master.off_code in (" + officecodes + ")"
                                + " and g_section.bill_type='REGULAR' and bill_group_master.bill_type = '42' AND is_sanctioned='Y' and is_terminated <> 'Y' AND POST_GRP=? AND cadre_type='OJS'"
                                + " group by GPC,pay_scale,post_grp,level_7thpay, G_SPC.GP) G_SPC"
                                + " INNER JOIN G_POST ON G_SPC.GPC = G_POST.POST_CODE order by post_grp, level_7thpay desc,pay_scale desc, post";

                        pst = con.prepareStatement(sql);
                        pst.setString(1, postgroup);
                        rs = pst.executeQuery();
                        while (rs.next()) {

                            row = row + 1;

                            if (counter == 0) {
                                label = new Label(0, row, "Group " + postgroup, headcell);
                                sheet.addCell(label);
                                label = new Label(1, row, "Judiciary", headcell);
                                sheet.addCell(label);
                                sheet.mergeCells(1, row, 8, row);

                                row = row + 1;
                            }

                            int meninposition = getMenInPosition(con, "REGULAR", rs.getString("level_7thpay"), "OJS", rs.getString("gpc"), rs.getString("pay_scale"), rs.getString("GP"), rs.getString("POST_GRP"));
                            int vacancy = rs.getInt("sanction_strength") - meninposition;

                            label = new Label(0, row, "", innercell);
                            sheet.addCell(label);
                            label = new Label(1, row, rs.getString("post"), innercell);
                            sheet.addCell(label);
                            label = new Label(2, row, rs.getString("pay_scale"), innercell);
                            sheet.addCell(label);
                            num = new Number(3, row, rs.getInt("GP"), innercell);
                            sheet.addCell(num);
                            label = new Label(4, row, rs.getString("level_7thpay"), innercell);
                            sheet.addCell(label);
                            num = new Number(5, row, rs.getInt("sanction_strength"), innercell);
                            sheet.addCell(num);
                            num = new Number(6, row, meninposition, innercell);
                            sheet.addCell(num);
                            num = new Number(7, row, vacancy, innercell);
                            sheet.addCell(num);
                            label = new Label(8, row, "", innercell);
                            sheet.addCell(label);

                            counter = counter + 1;

                            sanctionedStrengthTotal = sanctionedStrengthTotal + rs.getInt("sanction_strength");
                            menInPositionTotal = menInPositionTotal + meninposition;
                            vacancyTotal = vacancyTotal + vacancy;
                        }
                    }
                    DataBaseFunctions.closeSqlObjects(rs, pst);
                    counter = 0;

                    row = row + 1;

                    if (postgroup.equals("A")) {
                        label = new Label(0, row, "Group " + postgroup, headcell);
                        sheet.addCell(label);
                        label = new Label(1, row, "Other than UGC /Judiciary/ All India Services", headcell);
                        sheet.addCell(label);
                        sheet.mergeCells(1, row, 8, row);
                    } else {
                        label = new Label(0, row, "Group " + postgroup, headcell);
                        sheet.addCell(label);
                        label = new Label(1, row, "", headcell);
                        sheet.addCell(label);
                        sheet.mergeCells(1, row, 8, row);
                    }
                    if (!postgroup.equals("D")) {
                        sql = "SELECT gpc,post, POST_GRP,level_7thpay,pay_scale,sanction_strength,GP FROM("
                                + " SELECT COUNT(*) sanction_strength,GPC,pay_scale,post_grp,level_7thpay, G_SPC.GP FROM bill_section_mapping"
                                + " inner join bill_group_master on bill_section_mapping.BILL_GROUP_ID=bill_group_master.BILL_GROUP_ID"
                                + " inner join g_section on bill_section_mapping.section_id=g_section.section_id"
                                + " INNER JOIN SECTION_POST_MAPPING ON BILL_SECTION_MAPPING.SECTION_ID = SECTION_POST_MAPPING.SECTION_ID"
                                + " INNER JOIN G_SPC ON SECTION_POST_MAPPING.SPC = G_SPC.SPC"
                                + " LEFT OUTER JOIN EMP_MAST ON SECTION_POST_MAPPING.SPC = EMP_MAST.CUR_SPC"
                                + " LEFT OUTER JOIN G_CADRE ON EMP_MAST.CUR_CADRE_CODE=G_CADRE.CADRE_CODE"
                                + " WHERE bill_group_master.off_code in (" + officecodes + ") and (g_section.bill_type='REGULAR' or g_section.bill_type='CONT6_REG')"
                                + " and bill_group_master.bill_type = '42' AND is_sanctioned='Y' and is_terminated <> 'Y' AND POST_GRP=? AND"
                                + " (((cadre_type<>'AIS' AND cadre_type<>'UGC' AND cadre_type<>'OJS') or cadre_type is null) OR CUR_CADRE_CODE IS NULL)"
                                + " group by gpc, pay_scale, post_grp,level_7thpay,G_SPC.GP) G_SPC"
                                + " INNER JOIN G_POST ON G_SPC.GPC = G_POST.POST_CODE order by post_grp, level_7thpay desc,pay_scale desc, post";
                    } else {
                        sql = "SELECT gpc,post, POST_GRP,level_7thpay,pay_scale,sanction_strength,GP FROM("
                                + " SELECT COUNT(*) sanction_strength,GPC,pay_scale,post_grp,level_7thpay, G_SPC.GP FROM bill_section_mapping"
                                + " inner join bill_group_master on bill_section_mapping.BILL_GROUP_ID=bill_group_master.BILL_GROUP_ID"
                                + " inner join g_section on bill_section_mapping.section_id=g_section.section_id"
                                + " INNER JOIN SECTION_POST_MAPPING ON BILL_SECTION_MAPPING.SECTION_ID = SECTION_POST_MAPPING.SECTION_ID"
                                + " INNER JOIN G_SPC ON SECTION_POST_MAPPING.SPC = G_SPC.SPC"
                                + " LEFT OUTER JOIN EMP_MAST ON SECTION_POST_MAPPING.SPC = EMP_MAST.CUR_SPC"
                                + " LEFT OUTER JOIN G_CADRE ON EMP_MAST.CUR_CADRE_CODE=G_CADRE.CADRE_CODE"
                                + " WHERE bill_group_master.off_code in (" + officecodes + ") and (g_section.bill_type='REGULAR' or g_section.bill_type='CONT6_REG')"
                                + " and bill_group_master.bill_type = '42' AND is_sanctioned='Y' and is_terminated <> 'Y' AND (POST_GRP=? or POST_GRP is null) AND"
                                + " (((cadre_type<>'AIS' AND cadre_type<>'UGC' AND cadre_type<>'OJS') or cadre_type is null) OR CUR_CADRE_CODE IS NULL)"
                                + " group by gpc, pay_scale, post_grp,level_7thpay,G_SPC.GP) G_SPC"
                                + " INNER JOIN G_POST ON G_SPC.GPC = G_POST.POST_CODE order by post_grp, level_7thpay desc,pay_scale desc, post";
                    }
                    pst = con.prepareStatement(sql);
                    pst.setString(1, postgroup);
                    rs = pst.executeQuery();
                    while (rs.next()) {

                        row = row + 1;

                        int meninposition = getMenInPosition(con, "REGULAR", rs.getString("level_7thpay"), "", rs.getString("gpc"), rs.getString("pay_scale"), rs.getString("GP"), rs.getString("POST_GRP"));
                        int vacancy = rs.getInt("sanction_strength") - meninposition;

                        label = new Label(0, row, "", innercell);
                        sheet.addCell(label);
                        label = new Label(1, row, rs.getString("post"), innercell);
                        sheet.addCell(label);
                        label = new Label(2, row, rs.getString("pay_scale"), innercell);
                        sheet.addCell(label);
                        num = new Number(3, row, rs.getInt("GP"), innercell);
                        sheet.addCell(num);
                        label = new Label(4, row, rs.getString("level_7thpay"), innercell);
                        sheet.addCell(label);
                        num = new Number(5, row, rs.getInt("sanction_strength"), innercell);
                        sheet.addCell(num);
                        num = new Number(6, row, meninposition, innercell);
                        sheet.addCell(num);
                        num = new Number(7, row, vacancy, innercell);
                        sheet.addCell(num);
                        label = new Label(8, row, "", innercell);
                        sheet.addCell(label);

                        counter = counter + 1;

                        sanctionedStrengthTotal = sanctionedStrengthTotal + rs.getInt("sanction_strength");
                        menInPositionTotal = menInPositionTotal + meninposition;
                        vacancyTotal = vacancyTotal + vacancy;
                    }

                    row = row + 1;

                    label = new Label(0, row, "Total", headcell);
                    sheet.addCell(label);
                    label = new Label(1, row, "", innercell);
                    sheet.addCell(label);
                    label = new Label(2, row, "", innercell);
                    sheet.addCell(label);
                    label = new Label(3, row, "", innercell);
                    sheet.addCell(label);
                    label = new Label(4, row, "", innercell);
                    sheet.addCell(label);
                    num = new Number(5, row, sanctionedStrengthTotal, innercell);
                    sheet.addCell(num);
                    num = new Number(6, row, menInPositionTotal, innercell);
                    sheet.addCell(num);
                    num = new Number(7, row, vacancyTotal, innercell);
                    sheet.addCell(num);
                    label = new Label(8, row, "", innercell);
                    sheet.addCell(label);
                }

                /* PART-C*/
                row = row + 3;

                label = new Label(0, row, "PART-C ( Non-Regular Establishment)", headcell);//column,row
                sheet.addCell(label);
                sheet.mergeCells(0, row, 6, row);

                row = row + 1;

                label = new Label(0, row, "Category", headcell);
                sheet.setColumnView(0, 10);
                sheet.addCell(label);
                label = new Label(1, row, "Description of the Posts", headcell);
                sheet.setColumnView(1, 35);
                sheet.addCell(label);
                label = new Label(2, row, "As per 7th Pay", headcell);
                sheet.addCell(label);
                sheet.mergeCells(2, row, 3, row);
                label = new Label(4, row, "Persons-in-Position", headcell);
                sheet.setColumnView(4, 10);
                sheet.addCell(label);
                label = new Label(5, row, "Remarks if any", headcell);
                sheet.setColumnView(5, 11);
                sheet.addCell(label);

                DataBaseFunctions.closeSqlObjects(rs, pst);

                String sql = "SELECT gpc,post, POST_GRP,level_7thpay,pay_scale,sanction_strength, GP FROM("
                        + " SELECT COUNT(*) sanction_strength,GPC,pay_scale,post_grp,level_7thpay, G_SPC.GP FROM bill_section_mapping"
                        + " inner join g_section on bill_section_mapping.section_id=g_section.section_id"
                        + " INNER JOIN SECTION_POST_MAPPING ON BILL_SECTION_MAPPING.SECTION_ID = SECTION_POST_MAPPING.SECTION_ID"
                        + " INNER JOIN G_SPC ON SECTION_POST_MAPPING.SPC = G_SPC.SPC"
                        + " LEFT OUTER JOIN EMP_MAST ON SECTION_POST_MAPPING.SPC = EMP_MAST.CUR_SPC"
                        + " WHERE g_section.off_code in (" + officecodes + ") and g_section.bill_type='CONT6_REG' AND is_sanctioned='Y'"
                        + " and is_terminated <> 'Y' group by gpc, pay_scale, post_grp,level_7thpay,G_SPC.GP) G_SPC"
                        + " INNER JOIN G_POST ON G_SPC.GPC = G_POST.POST_CODE order by post_grp, level_7thpay desc,pay_scale desc, post";
                pst = con.prepareStatement(sql);
                rs = pst.executeQuery();
                while (rs.next()) {

                    row = row + 1;
                    
                    int menInPosition = getMenInPosition(con, "CONT_6", rs.getString("level_7thpay"), "", rs.getString("gpc"), "", "", "");
                    
                    label = new Label(0, row, "", innercell);
                    sheet.setColumnView(0, 10);
                    sheet.addCell(label);
                    label = new Label(1, row, rs.getString("post"), innercell);
                    sheet.setColumnView(1, 35);
                    sheet.addCell(label);
                    label = new Label(2, row, rs.getString("level_7thpay"), innercell);
                    sheet.addCell(label);
                    sheet.mergeCells(2, row, 3, row);
                    num = new Number(4, row, menInPosition, innercell);
                    sheet.setColumnView(4, 10);
                    sheet.addCell(num);
                    label = new Label(5, row, "", innercell);
                    sheet.setColumnView(5, 11);
                    sheet.addCell(label);
                }
            }
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(offrs, offpst);
            DataBaseFunctions.closeSqlObjects(cors, copst);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    private static int getMenInPosition(Connection con, String billtype, String pay_scale_7th, String isAIS, String gpc, String pay_scale, String gp, String postgrp) throws SQLException {

        PreparedStatement pst = null;
        ResultSet rs = null;

        int count = 0;

        String sql = "";
        try {
            if (billtype.equals("REGULAR")) {
                if (pay_scale_7th != null && !pay_scale_7th.equals("")) {
                    if (isAIS.equals("AIS") || isAIS.equals("UGC") || isAIS.equals("OJS")) {
                        sql = "select count(distinct cur_spc) cnt from emp_mast e"
                                + " inner join g_spc g on e.cur_spc=g.spc"
                                + " inner join g_cadre c on e.cur_cadre_code=c.cadre_code"
                                + " inner join section_post_mapping spm on e.cur_spc=spm.spc"
                                + " inner join bill_section_mapping bsm on spm.section_id=bsm.section_id"
                                + " inner join bill_group_master on bsm.BILL_GROUP_ID=bill_group_master.BILL_GROUP_ID"
                                + " where g.gpc=? AND bill_group_master.bill_type = '42' and g.level_7thpay::TEXT=? and g.pay_scale=? and g.gp=? and g.post_grp=?"
                                + " and g.is_sanctioned='Y' and cadre_type=? and is_regular='Y'";
                        pst = con.prepareStatement(sql);
                        pst.setString(1, gpc);
                        pst.setString(2, pay_scale_7th);
                        pst.setString(3, pay_scale);
                        if (gp != null && !gp.equals("")) {
                            pst.setInt(4, Integer.parseInt(gp));
                        } else {
                            pst.setInt(4, 0);
                        }
                        pst.setString(5, postgrp);
                        pst.setString(6, isAIS);
                        rs = pst.executeQuery();
                        if (rs.next()) {
                            count = rs.getInt("cnt");
                        }
                    } else {
                        sql = "select count(distinct cur_spc) cnt from emp_mast e"
                                + " inner join g_spc g on e.cur_spc=g.spc"
                                + " inner join section_post_mapping spm on e.cur_spc=spm.spc"
                                + " inner join bill_section_mapping bsm on spm.section_id=bsm.section_id"
                                + " inner join bill_group_master on bsm.BILL_GROUP_ID=bill_group_master.BILL_GROUP_ID"
                                + " left outer join g_cadre c on e.cur_cadre_code=c.cadre_code"
                                + " where g.gpc=? AND bill_group_master.bill_type = '42' AND g.level_7thpay::TEXT=? and g.pay_scale=? and g.gp=?"
                                + " and g.post_grp=? and g.is_sanctioned='Y' and is_regular='Y'"
                                + " and (((cadre_type<>'AIS' AND cadre_type<>'UGC' AND cadre_type<>'OJS') OR cadre_type IS NULL) OR CUR_CADRE_CODE IS NULL)";
                        pst = con.prepareStatement(sql);
                        pst.setString(1, gpc);
                        pst.setString(2, pay_scale_7th);
                        pst.setString(3, pay_scale);
                        if (gp != null && !gp.equals("")) {
                            pst.setInt(4, Integer.parseInt(gp));
                        } else {
                            pst.setInt(4, 0);
                        }
                        pst.setString(5, postgrp);
                        rs = pst.executeQuery();
                        if (rs.next()) {
                            count = rs.getInt("cnt");
                        }
                    }
                } else {
                    if (isAIS.equals("AIS") || isAIS.equals("UGC") || isAIS.equals("OJS")) {
                        sql = "select count(distinct cur_spc) cnt from emp_mast e"
                                + " inner join g_spc g on e.cur_spc=g.spc"
                                + " inner join g_cadre c on e.cur_cadre_code=c.cadre_code"
                                + " inner join section_post_mapping spm on e.cur_spc=spm.spc"
                                + " inner join bill_section_mapping bsm on spm.section_id=bsm.section_id"
                                + " inner join bill_group_master on bsm.BILL_GROUP_ID=bill_group_master.BILL_GROUP_ID"
                                + " where e.cur_spc=g.spc AND bill_group_master.bill_type = '42' and e.cur_cadre_code=c.cadre_code and g.gpc=? and g.pay_scale=?"
                                + " and g.gp=? and g.post_grp=? and g.is_sanctioned='Y'"
                                + " and cadre_type=? and is_regular='Y'";
                        pst = con.prepareStatement(sql);
                        pst.setString(1, gpc);
                        pst.setString(2, pay_scale);
                        if (gp != null && !gp.equals("")) {
                            pst.setInt(3, Integer.parseInt(gp));
                        } else {
                            pst.setInt(3, 0);
                        }
                        pst.setString(4, postgrp);
                        pst.setString(5, isAIS);
                        rs = pst.executeQuery();
                        if (rs.next()) {
                            count = rs.getInt("cnt");
                        }
                    } else {
                        sql = "select count(distinct cur_spc) cnt from emp_mast e"
                                + " inner join g_spc g on e.cur_spc=g.spc"
                                + " inner join section_post_mapping spm on e.cur_spc=spm.spc"
                                + " inner join bill_section_mapping bsm on spm.section_id=bsm.section_id"
                                + " inner join bill_group_master on bsm.BILL_GROUP_ID=bill_group_master.BILL_GROUP_ID"
                                + " left outer join g_cadre c on e.cur_cadre_code=c.cadre_code"
                                + " where g.gpc=? AND bill_group_master.bill_type = '42' and g.pay_scale=? and g.gp=? and g.post_grp=? and g.is_sanctioned='Y' and is_regular='Y'"
                                + " and (((cadre_type<>'AIS' AND cadre_type<>'UGC' AND cadre_type<>'OJS') OR cadre_type IS NULL) OR CUR_CADRE_CODE IS NULL)";
                        pst = con.prepareStatement(sql);
                        pst.setString(1, gpc);
                        pst.setString(2, pay_scale);
                        if (gp != null && !gp.equals("")) {
                            pst.setInt(3, Integer.parseInt(gp));
                        } else {
                            pst.setInt(3, 0);
                        }
                        pst.setString(4, postgrp);
                        rs = pst.executeQuery();
                        if (rs.next()) {
                            count = rs.getInt("cnt");
                        }
                    }
                }
            } else if (billtype.equals("GIA")) {
                if (pay_scale_7th != null && !pay_scale_7th.equals("")) {
                    if (isAIS.equals("AIS") || isAIS.equals("UGC") || isAIS.equals("OJS")) {
                        sql = "select count(distinct cur_spc) cnt from emp_mast e"
                                + " inner join g_spc g on e.cur_spc=g.spc"
                                + " inner join g_cadre c on e.cur_cadre_code=c.cadre_code"
                                + " inner join section_post_mapping spm on e.cur_spc=spm.spc"
                                + " inner join bill_section_mapping bsm on spm.section_id=bsm.section_id"
                                + " inner join bill_group_master on bsm.BILL_GROUP_ID=bill_group_master.BILL_GROUP_ID"
                                + " where g.gpc=? AND (bill_group_master.bill_type = '21' or bill_group_master.bill_type = '69') and g.level_7thpay::TEXT=? and g.pay_scale=? and g.gp=? and g.post_grp=?"
                                + " and g.is_sanctioned='Y' and cadre_type=? and is_regular='Y'";
                        pst = con.prepareStatement(sql);
                        pst.setString(1, gpc);
                        pst.setString(2, pay_scale_7th);
                        pst.setString(3, pay_scale);
                        if (gp != null && !gp.equals("")) {
                            pst.setInt(4, Integer.parseInt(gp));
                        } else {
                            pst.setInt(4, 0);
                        }
                        pst.setString(5, postgrp);
                        pst.setString(6, isAIS);
                        rs = pst.executeQuery();
                        if (rs.next()) {
                            count = rs.getInt("cnt");
                        }
                    } else {
                        sql = "select count(distinct cur_spc) cnt from emp_mast e"
                                + " inner join g_spc g on e.cur_spc=g.spc"
                                + " inner join section_post_mapping spm on e.cur_spc=spm.spc"
                                + " inner join bill_section_mapping bsm on spm.section_id=bsm.section_id"
                                + " inner join bill_group_master on bsm.BILL_GROUP_ID=bill_group_master.BILL_GROUP_ID"
                                + " left outer join g_cadre c on e.cur_cadre_code=c.cadre_code"
                                + " where g.gpc=? AND (bill_group_master.bill_type = '21' or bill_group_master.bill_type = '69') and g.level_7thpay::TEXT=? and g.pay_scale=? and g.gp=? and g.post_grp=? and g.is_sanctioned='Y' and is_regular='Y'"
                                + " and (((cadre_type<>'AIS' AND cadre_type<>'UGC' AND cadre_type<>'OJS') OR cadre_type IS NULL) OR CUR_CADRE_CODE IS NULL)";
                        pst = con.prepareStatement(sql);
                        pst.setString(1, gpc);
                        pst.setString(2, pay_scale_7th);
                        pst.setString(3, pay_scale);
                        if (gp != null && !gp.equals("")) {
                            pst.setInt(4, Integer.parseInt(gp));
                        } else {
                            pst.setInt(4, 0);
                        }
                        pst.setString(5, postgrp);
                        rs = pst.executeQuery();
                        if (rs.next()) {
                            count = rs.getInt("cnt");
                        }
                    }
                } else {
                    if (isAIS.equals("AIS") || isAIS.equals("UGC") || isAIS.equals("OJS")) {
                        sql = "select count(distinct cur_spc) cnt from emp_mast e"
                                + " inner join g_spc g on e.cur_spc=g.spc"
                                + " inner join g_cadre c on e.cur_cadre_code=c.cadre_code"
                                + " inner join section_post_mapping spm on e.cur_spc=spm.spc"
                                + " inner join bill_section_mapping bsm on spm.section_id=bsm.section_id"
                                + " inner join bill_group_master on bsm.BILL_GROUP_ID=bill_group_master.BILL_GROUP_ID"
                                + " where e.cur_spc=g.spc AND (bill_group_master.bill_type = '21' or bill_group_master.bill_type = '69') and e.cur_cadre_code=c.cadre_code and g.gpc=? and g.pay_scale=? and g.gp=? and g.post_grp=? and g.is_sanctioned='Y'"
                                + " and cadre_type=? and is_regular='Y'";
                        pst = con.prepareStatement(sql);
                        pst.setString(1, gpc);
                        pst.setString(2, pay_scale);
                        if (gp != null && !gp.equals("")) {
                            pst.setInt(3, Integer.parseInt(gp));
                        } else {
                            pst.setInt(3, 0);
                        }
                        pst.setString(4, postgrp);
                        pst.setString(5, isAIS);
                        rs = pst.executeQuery();
                        if (rs.next()) {
                            count = rs.getInt("cnt");
                        }
                    } else {
                        sql = "select count(distinct cur_spc) cnt from emp_mast e"
                                + " inner join g_spc g on e.cur_spc=g.spc"
                                + " inner join section_post_mapping spm on e.cur_spc=spm.spc"
                                + " inner join bill_section_mapping bsm on spm.section_id=bsm.section_id"
                                + " inner join bill_group_master on bsm.BILL_GROUP_ID=bill_group_master.BILL_GROUP_ID"
                                + " left outer join g_cadre c on e.cur_cadre_code=c.cadre_code"
                                + " where g.gpc=? AND (bill_group_master.bill_type = '21' or bill_group_master.bill_type = '69') and g.pay_scale=? and g.gp=? and g.post_grp=? and g.is_sanctioned='Y' and is_regular='Y'"
                                + " and (((cadre_type<>'AIS' AND cadre_type<>'UGC' AND cadre_type<>'OJS') OR cadre_type IS NULL) OR CUR_CADRE_CODE IS NULL)";
                        pst = con.prepareStatement(sql);
                        pst.setString(1, gpc);
                        pst.setString(2, pay_scale);
                        if (gp != null && !gp.equals("")) {
                            pst.setInt(3, Integer.parseInt(gp));
                        } else {
                            pst.setInt(3, 0);
                        }
                        pst.setString(4, postgrp);
                        rs = pst.executeQuery();
                        if (rs.next()) {
                            count = rs.getInt("cnt");
                        }
                    }
                }
            } else if (billtype.equals("CONT_6")) {
                sql = "select count(distinct cur_spc) cnt from emp_mast e"
                        + " inner join g_spc g on e.cur_spc=g.spc"
                        + " inner join section_post_mapping spm on e.cur_spc=spm.spc"
                        + " inner join bill_section_mapping bsm on spm.section_id=bsm.section_id"
                        + " left outer join g_cadre c on e.cur_cadre_code=c.cadre_code"
                        + " where g.gpc=? and g.is_sanctioned='Y' and"
                        + " (((cadre_type<>'AIS' AND cadre_type<>'UGC' AND cadre_type<>'OJS') or cadre_type is null) OR CUR_CADRE_CODE IS NULL)"
                        + " and is_regular='C' and dep_code='02'";
                pst = con.prepareStatement(sql);
                pst.setString(1, gpc);
                rs = pst.executeQuery();
                if (rs.next()) {
                    count = rs.getInt("cnt");
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
        }
        return count;
    }
}
