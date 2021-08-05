package hrms.common;

import hrms.model.payroll.billbrowser.BillBean;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ChallanNoMissingDataAQ_DTLS {

    public static void main(String args[]) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;

        PreparedStatement insertpst = null;

        ArrayList billList = new ArrayList();
        BillBean bb = null;

        String retrieveMonth = "JAN";
        try {
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection("jdbc:postgresql://172.16.1.16/hrmis", "hrmis2", "cmgi");
            //con = DriverManager.getConnection("jdbc:postgresql://192.168.1.19/hrmis", "hrmis2", "cmgi");

            String insertsql = "insert into challan_no_aqdtls_no_data(bill_no,ddo_code,challan_no,challan_date,aq_year,aq_month,retrieve_month,type_of_bill) values(?,?,?,?,?,?,?,?)";
            insertpst = con.prepareStatement(insertsql);

            String sql = "select bill_no,off_code,aq_year,aq_month,challan_no,challan_date,type_of_bill from bill_mast"
                    + " where extract (year from challan_date)=2020 AND extract (month from challan_date)=1";
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                bb = new BillBean();
                bb.setBillno(rs.getString("bill_no"));
                bb.setBilltype(rs.getString("type_of_bill"));
                bb.setDdocode(rs.getString("off_code"));
                bb.setBillYear(rs.getInt("aq_year"));
                bb.setBillMonth(rs.getInt("aq_month"));
                bb.setVoucherno(rs.getString("challan_no"));
                if (rs.getDate("challan_date") != null && !rs.getDate("challan_date").equals("")) {
                    bb.setVoucherdate(rs.getString("challan_date"));
                }
                billList.add(bb);
            }

            DataBaseFunctions.closeSqlObjects(rs, pst);

            bb = null;
            if (billList != null && billList.size() > 0) {
                for (int i = 0; i < billList.size(); i++) {
                    bb = (BillBean) billList.get(i);

                    int billno = Integer.parseInt(bb.getBillno());
                    int aqyear = bb.getBillYear();
                    int aqmonth = bb.getBillMonth();

                    if (bb.getBilltype() != null && bb.getBilltype().equals("PAY")) {
                        String aqdtlstbl = AqFunctionalities.getAQBillDtlsTable(aqmonth, aqyear);

                        String checksql = "select count(aq_dtls.aqsl_no) cnt from aq_mast"
                                + " inner join " + aqdtlstbl + " aq_dtls on aq_mast.aqsl_no=aq_dtls.aqsl_no"
                                + " where bill_no=?";
                        pst = con.prepareStatement(checksql);
                        pst.setInt(1, billno);
                        rs = pst.executeQuery();
                        if (rs.next()) {
                            if (rs.getString("cnt") != null && !rs.getString("cnt").equals("")) {
                                if (rs.getInt("cnt") == 0) {
                                    System.out.println(i + " and PAY Bill and Inside 0 and bill no is: " + billno);
                                    insertpst.setInt(1, billno);
                                    insertpst.setString(2, bb.getDdocode());
                                    insertpst.setString(3, bb.getVoucherno());
                                    insertpst.setTimestamp(4, new Timestamp(new SimpleDateFormat("yyyy-MM-dd").parse(bb.getVoucherdate()).getTime()));
                                    insertpst.setInt(5, aqyear);
                                    insertpst.setInt(6, aqmonth);
                                    insertpst.setString(7, retrieveMonth);
                                    insertpst.setString(8, bb.getBilltype());
                                    insertpst.executeUpdate();
                                }
                            }
                        }
                    }else if(bb.getBilltype() != null && bb.getBilltype().contains("ARREAR")){
                        String checksql = "select count(aq_dtls.aqsl_no) cnt from arr_mast"
                                + " inner join arr_dtls aq_dtls on arr_mast.aqsl_no=aq_dtls.aqsl_no"
                                + " where bill_no=?";
                        pst = con.prepareStatement(checksql);
                        pst.setInt(1, billno);
                        rs = pst.executeQuery();
                        if (rs.next()) {
                            if (rs.getString("cnt") != null && !rs.getString("cnt").equals("")) {
                                if (rs.getInt("cnt") == 0) {
                                    System.out.println(i + " and Arrear Bill and Inside 0 and bill no is: " + billno);
                                    insertpst.setInt(1, billno);
                                    insertpst.setString(2, bb.getDdocode());
                                    insertpst.setString(3, bb.getVoucherno());
                                    insertpst.setTimestamp(4, new Timestamp(new SimpleDateFormat("yyyy-MM-dd").parse(bb.getVoucherdate()).getTime()));
                                    insertpst.setInt(5, aqyear);
                                    insertpst.setInt(6, aqmonth);
                                    insertpst.setString(7, retrieveMonth);
                                    insertpst.setString(8, bb.getBilltype());
                                    insertpst.executeUpdate();
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(rs, pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }
}
