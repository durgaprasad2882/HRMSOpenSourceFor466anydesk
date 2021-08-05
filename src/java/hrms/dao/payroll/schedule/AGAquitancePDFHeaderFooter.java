package hrms.dao.payroll.schedule;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import hrms.model.common.CommonReportParamBean;
import hrms.model.payroll.aqreport.AqreportBean;
import hrms.model.payroll.billbrowser.BillConfigObj;
import java.io.IOException;
import org.apache.commons.lang.StringUtils;

public class AGAquitancePDFHeaderFooter extends PdfPageEventHelper {

    CommonReportParamBean crb = null;
    AqreportBean aqreportFormBean = null;
    BillConfigObj billConfig = null;

    public AGAquitancePDFHeaderFooter(CommonReportParamBean crb, AqreportBean aqreportFormBean, BillConfigObj billConfig) throws IOException {
        this.crb = crb;
        this.aqreportFormBean = aqreportFormBean;
        this.billConfig = billConfig;
    }

    @Override
    public void onStartPage(PdfWriter writer, Document document) {
        //ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, new Phrase(this.principalHeader, new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK)), 10, 820, 0);
        //ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, new Phrase(this.interestHeader, new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK)), 350, 820, 0);

        try {
            String col7Desc[] = null;
            String col6Desc[] = null;

            if (billConfig != null && !billConfig.equals("")) {
                col6Desc = billConfig.getCol6List();
                col7Desc = billConfig.getCol7List();
            }

            Font textFont = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);

            PdfPTable table = new PdfPTable(5);
            table.setWidths(new float[]{10, 10, 60, 10, 10});
            table.setWidthPercentage(100);

            PdfPCell cell = null;

            cell = new PdfPCell(new Phrase("STATE-" + StringUtils.defaultString(aqreportFormBean.getState()) + "\n" + "VCH NO: " + crb.getVchNo(), textFont));
            cell.setBorder(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("DIST-" + StringUtils.defaultString(aqreportFormBean.getDistrict()) + "\n" + crb.getVchDate(), textFont));
            cell.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("SCHEDULE-A STATE HEAD QUATERS FORM NO-58\n"
                    + "PAY BILL FOR " + aqreportFormBean.getOffen()
                    + "\n"
                    + "MONTHLY PAY BILL FOR " + aqreportFormBean.getMonth() + "-" + aqreportFormBean.getYear(), textFont));
            cell.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("BILL NO:" + StringUtils.defaultString(aqreportFormBean.getBilldesc()) + "\nBILL DT:" + StringUtils.defaultString(aqreportFormBean.getBilldate()), textFont));
            cell.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(String.format("PAGE: %d ", writer.getPageNumber()), textFont));
            cell.setBorder(Rectangle.TOP | Rectangle.RIGHT | Rectangle.BOTTOM);
            table.addCell(cell);

            document.add(table);

            PdfPTable table1 = new PdfPTable(21);
            //table1.setWidths(new float[]{3, 11, 4, 4, 4, 4, 4, 5, 5, 6, 4, 4, 4, 8, 5, 5, 5, 5, 5, 4, 7});
            table1.setWidths(new float[]{5, 20, 10, 8, 8, 8, 10, 10, 9, 15, 8, 8, 15, 15, 15, 15, 15, 11, 10, 12, 20});
            table1.setWidthPercentage(100);

            cell = new PdfPCell(new Phrase("SL NO", textFont));
            cell.setBorder(Rectangle.LEFT | Rectangle.BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.addCell(cell);

            cell = new PdfPCell(new Phrase("NAME/\nDESG/\nPAY SCALE", textFont));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.addCell(cell);

            cell = new PdfPCell(new Phrase("BASIC\nSPL PAY\nGP\nIR", textFont));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.addCell(cell);

            cell = new PdfPCell(new Phrase("DP\nP.PAY", textFont));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.addCell(cell);

            String column6 = "";
            String column7 = "";

            if (col7Desc != null && col6Desc != null) {
                column6 = StringUtils.defaultString(col6Desc[0]) + "\n" + StringUtils.defaultString(col6Desc[1]) + "\n" + StringUtils.defaultString(col6Desc[2]) + "\n" + StringUtils.defaultString(col6Desc[3]) + "\n" + StringUtils.defaultString(col6Desc[4]) + "\n" + StringUtils.defaultString(col6Desc[5]);
                column7 = StringUtils.defaultString(col7Desc[0]) + "\n" + StringUtils.defaultString(col7Desc[1]) + "\n" + StringUtils.defaultString(col7Desc[2]) + "\n" + StringUtils.defaultString(col7Desc[3]) + "\n" + StringUtils.defaultString(col7Desc[4]) + "\n" + StringUtils.defaultString(col7Desc[5]);
            } else if (col7Desc != null) {
                column6 = "HRA";
                column7 = StringUtils.defaultString(col7Desc[0]) + "\n" + StringUtils.defaultString(col7Desc[1]) + "\n" + StringUtils.defaultString(col7Desc[2]) + "\n" + StringUtils.defaultString(col7Desc[3]) + "\n" + StringUtils.defaultString(col7Desc[4]) + "\n" + StringUtils.defaultString(col7Desc[5]);
            } else if (col6Desc != null) {
                column6 = StringUtils.defaultString(col6Desc[0]) + "\n" + StringUtils.defaultString(col6Desc[1]) + "\n" + StringUtils.defaultString(col6Desc[2]) + "\n" + StringUtils.defaultString(col6Desc[3]) + "\n" + StringUtils.defaultString(col6Desc[4]) + "\n" + StringUtils.defaultString(col6Desc[5]);
                column7 = "IR" + "\n" + "WA" + "\n" + "CA" + "\n" + "OTA" + "\n" + "DEP.AL";
            } else {
                column6 = "HRA";
                column7 = "OTHER\nALLOWANCE";
            }

            cell = new PdfPCell(new Phrase("DA", textFont));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.addCell(cell);
            cell = new PdfPCell(new Phrase(column6, textFont));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.addCell(cell);
            cell = new PdfPCell(new Phrase(column7, textFont));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.addCell(cell);
            cell = new PdfPCell(new Phrase("GROSS\nPAY", textFont));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.addCell(cell);
            cell = new PdfPCell(new Phrase("LIC/\nPLI", textFont));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.addCell(cell);
            cell = new PdfPCell(new Phrase("GPF/CPF/TPF\nDA-GPF\nRECOVERY", textFont));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.addCell(cell);
            cell = new PdfPCell(new Phrase("P.TAX\nI.TAX", textFont));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.addCell(cell);
            cell = new PdfPCell(new Phrase("HRR\nWATER TAX\nSWG\nHIRE CHG", textFont));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.addCell(cell);
            cell = new PdfPCell(new Phrase("HB\n INT HB\nSPL HB\nINT SPL HB", textFont));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.addCell(cell);
            cell = new PdfPCell(new Phrase("MC\nINT MC\nMC/MOP ADV\nINT MC/MOPED", textFont));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.addCell(cell);
            cell = new PdfPCell(new Phrase("CAR ADV\nINT CAR\nBI-CYCLE\nINT CYCL", textFont));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.addCell(cell);
            cell = new PdfPCell(new Phrase("PAY ADV\nMED ADV\nTRADE ADV\nOVDL", textFont));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.addCell(cell);
            cell = new PdfPCell(new Phrase("FEST\nNPS ARR.\nEX. PAY\nRTI\nAUDR", textFont));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.addCell(cell);
            cell = new PdfPCell(new Phrase("OTHER\nRECOVERY\nGIS ADV\nAIS GIS\nCOMP ADV", textFont));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.addCell(cell);
            cell = new PdfPCell(new Phrase("TOTAL\nDEDN", textFont));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.addCell(cell);
            cell = new PdfPCell(new Phrase("NET PAY", textFont));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.addCell(cell);
            cell = new PdfPCell(new Phrase("REMARKS\n A/C NO", textFont));
            cell.setBorder(Rectangle.RIGHT | Rectangle.BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.addCell(cell);

            cell = new PdfPCell(new Phrase("(1)", textFont));
            cell.setBorder(Rectangle.BOTTOM | Rectangle.LEFT);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.addCell(cell);
            cell = new PdfPCell(new Phrase("(2)", textFont));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.addCell(cell);
            cell = new PdfPCell(new Phrase("(3)", textFont));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.addCell(cell);
            cell = new PdfPCell(new Phrase("(4)", textFont));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.addCell(cell);
            cell = new PdfPCell(new Phrase("(5)", textFont));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.addCell(cell);
            cell = new PdfPCell(new Phrase("(6)", textFont));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.addCell(cell);
            cell = new PdfPCell(new Phrase("(7)", textFont));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.addCell(cell);
            cell = new PdfPCell(new Phrase("(8)", textFont));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.addCell(cell);
            cell = new PdfPCell(new Phrase("(9)", textFont));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.addCell(cell);
            cell = new PdfPCell(new Phrase("(10)", textFont));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.addCell(cell);
            cell = new PdfPCell(new Phrase("(11)", textFont));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.addCell(cell);
            cell = new PdfPCell(new Phrase("(12)", textFont));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.addCell(cell);
            cell = new PdfPCell(new Phrase("(13)", textFont));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.addCell(cell);
            cell = new PdfPCell(new Phrase("(14)", textFont));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.addCell(cell);
            cell = new PdfPCell(new Phrase("(15)", textFont));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.addCell(cell);
            cell = new PdfPCell(new Phrase("(16)", textFont));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.addCell(cell);
            cell = new PdfPCell(new Phrase("(17)", textFont));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.addCell(cell);
            cell = new PdfPCell(new Phrase("(18)", textFont));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.addCell(cell);
            cell = new PdfPCell(new Phrase("(19)", textFont));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.addCell(cell);
            cell = new PdfPCell(new Phrase("(20)", textFont));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.addCell(cell);
            cell = new PdfPCell(new Phrase("(21)", textFont));
            cell.setBorder(Rectangle.BOTTOM | Rectangle.RIGHT);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.addCell(cell);

            document.add(table1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        try {
            ColumnText ct = new ColumnText(writer.getDirectContent());

            ct.setSimpleColumn(new Rectangle(36, 10, 559, 32));
            ct.addText(new Phrase("N:B:- The document is digitally signed and electronically generated and therefore needs no ink signed signature as per IT Act 2000. http://hrmsorissa.gov.in/", new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK)));
            ct.setAlignment(Element.ALIGN_LEFT);
            ct.go();
        } catch (DocumentException de) {
            throw new ExceptionConverter(de);
        }
    }
}
