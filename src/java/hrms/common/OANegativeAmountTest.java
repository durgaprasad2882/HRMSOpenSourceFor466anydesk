package hrms.common;

import hrms.model.billvouchingTreasury.ObjectBreakup;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class OANegativeAmountTest {

    public static void main(String args[]) {

        Connection con = null;

        PreparedStatement pst = null;
        ResultSet rs = null;
        
        ArrayList objlist = new ArrayList();
        try {
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection("jdbc:postgresql://172.16.1.16/hrmis", "hrmis2", "cmgi");
            //con = DriverManager.getConnection("jdbc:postgresql://192.168.1.19/hrmis", "hrmis2", "cmgi");

            pst = con.prepareStatement("SELECT * FROM(SELECT BT_ID,sum(to_be_paid-already_paid) adamt FROM ARR_DTLS INNER JOIN ARR_MAST ON  ARR_DTLS.AQSL_NO = ARR_MAST.AQSL_NO WHERE BILL_NO = ? group by bt_id) T1 WHERE adamt != 0 order by adamt");
            pst.setInt(1, 52968722);
            rs = pst.executeQuery();
            ObjectBreakup object = null;
            int tAmt = 0;
            while (rs.next()) {
                object = new ObjectBreakup();
                object.setHrmsgeneratedRefno(52968722);
                object.setObjectHead(rs.getString("BT_ID"));
                
                if (rs.getInt("adamt") > 0) {

                    if (object.getObjectHead() != null && object.getObjectHead().equals("136")) {
                        if (rs.getInt("adamt") > tAmt) {
                            object.setObjectHeadwiseAmount((rs.getInt("adamt") + tAmt));
                            tAmt = 0;
                        }else{
                            object.setObjectHeadwiseAmount(rs.getInt("adamt"));
                        }
                    } else if (object.getObjectHead() != null && object.getObjectHead().equals("156")) {
                        if (rs.getInt("adamt") > tAmt) {
                            object.setObjectHeadwiseAmount((rs.getInt("adamt") + tAmt));
                            tAmt = 0;
                        } else {
                            object.setObjectHeadwiseAmount(rs.getInt("adamt"));
                        }
                    } else {
                        object.setObjectHeadwiseAmount(rs.getInt("adamt"));
                    }
                    if (object.getObjectHeadwiseAmount() < 0) {
                        tAmt = (int)object.getObjectHeadwiseAmount();
                    } else {
                        objlist.add(object);
                    }
                } else {
                    tAmt = rs.getInt("adamt") + tAmt;
                }
            }
            
            if(objlist != null && objlist.size() > 0){
                object = null;
                for(int i = 0; i < objlist.size(); i++){
                    object = (ObjectBreakup)objlist.get(i);
                    System.out.println("Object Head is: "+object.getObjectHead()+" and amount is: "+object.getObjectHeadwiseAmount());
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
