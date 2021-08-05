/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

/**
 *
 * @author cmgi
 */
public class ASIRevert {

    public static void main(String args[]) {
        Connection con = null;

        PreparedStatement pst = null;
        int nominationMasterId = 1292;
        try{
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection("jdbc:postgresql://172.16.1.14/hrmis", "hrmis2", "cmgi");
            
            String sql = "update police_nomination_master set submitted_to_office=null, submitted_on=null, is_submitted=null,submitted_to_dg_office=null where nomination_master_id=?";
            pst = con.prepareStatement(sql);
            pst.setInt(1,nominationMasterId);
            pst.executeUpdate();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }
}
