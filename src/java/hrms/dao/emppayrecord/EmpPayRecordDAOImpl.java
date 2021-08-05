package hrms.dao.emppayrecord;

import hrms.common.DataBaseFunctions;
import hrms.model.emppayrecord.EmpPayRecordForm;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import javax.annotation.Resource;
import javax.sql.DataSource;

public class EmpPayRecordDAOImpl implements EmpPayRecordDAO{
    
    @Resource(name = "dataSource")
    protected DataSource dataSource;
    
    private MaxEmpPayRecordIdDAO maxEmpPayRecordIdDAO;
    
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setMaxEmpPayRecordIdDAO(MaxEmpPayRecordIdDAOImpl maxEmpPayRecordIdDAO) {
        this.maxEmpPayRecordIdDAO = maxEmpPayRecordIdDAO;
    }

    @Override
    public void saveEmpPayRecordData(EmpPayRecordForm eprform) {
        
        Connection con = null;
        
        PreparedStatement pst = null;
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        try{
            con = dataSource.getConnection();
            
            String sql = "insert into emp_pay_record (pay_id, not_type, not_id, emp_id, wef, weft, pay_scale, pay, s_pay, p_pay, oth_pay, oth_desc, gp) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pst = con.prepareStatement(sql);
            pst.setString(1, maxEmpPayRecordIdDAO.getMaxEmpPayRecordId());
            pst.setString(2, eprform.getNot_type());
            pst.setString(3, eprform.getNot_id());
            pst.setString(4, eprform.getEmpid());
            if (eprform.getWefDt() != null && !eprform.getWefDt().equals("")) {
                pst.setTimestamp(5, new Timestamp(sdf.parse(eprform.getWefDt()).getTime()));
            } else {
                pst.setTimestamp(5, null);
            }
            if (eprform.getWefTime() != null && !eprform.getWefTime().equals("")) {
                pst.setString(6, eprform.getWefTime());
            } else {
                pst.setString(6, null);
            }
            if (eprform.getPayscale() != null && !eprform.getPayscale().equals("")) {
                pst.setString(7, eprform.getPayscale());
            } else {
                pst.setString(7, null);
            }
            if (eprform.getBasic() == null || eprform.getBasic().equals("")) {
                pst.setDouble(8, 0);
            } else {
                pst.setDouble(8, java.lang.Double.parseDouble(eprform.getBasic()));
            }
            if (eprform.getS_pay() == null || eprform.getS_pay().equalsIgnoreCase("")) {
                pst.setDouble(9, 0);
            } else {
                pst.setDouble(9, java.lang.Double.parseDouble(eprform.getS_pay()));
            }
            if (eprform.getP_pay() == null || eprform.getP_pay().equalsIgnoreCase("")) {
                pst.setDouble(10, 0);
            } else {
                pst.setDouble(10, java.lang.Double.parseDouble(eprform.getP_pay()));
            }
            if (eprform.getOth_pay() == null || eprform.getOth_pay().equalsIgnoreCase("")) {
                pst.setDouble(11, 0);
            } else {
                pst.setDouble(11, java.lang.Double.parseDouble(eprform.getOth_pay()));
            }
            if (eprform.getOth_desc() != null && !eprform.getOth_desc().equals("")) {
                pst.setString(12, eprform.getOth_desc().toUpperCase());
            } else {
                pst.setString(12, null);
            }
            if (eprform.getGp() == null || eprform.getGp().equalsIgnoreCase("")) {
                pst.setDouble(13, 0);
            } else {
                pst.setDouble(13, java.lang.Double.parseDouble(eprform.getGp()));
            }
            pst.executeUpdate();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    @Override
    public void updateEmpPayRecordData(EmpPayRecordForm eprform) {
        
        Connection con = null;
        
        PreparedStatement pst = null;
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        try{
            con = dataSource.getConnection();
            
            String sql = "update emp_pay_record set wef=?, weft=?, pay_scale=?, pay=?, s_pay=?, p_pay=?, oth_pay=?, oth_desc=?, gp=? where pay_id=? and emp_id=?";
            pst = con.prepareStatement(sql);
            if(eprform.getWefDt() != null && !eprform.getWefDt().equals("")){
                pst.setTimestamp(1, new Timestamp(sdf.parse(eprform.getWefDt()).getTime()));
            }else{
                pst.setTimestamp(1, null);
            }
            pst.setString(2,eprform.getWefTime());
            pst.setString(3,eprform.getPayscale());
            if (eprform.getBasic() == null || eprform.getBasic().equals("")) {
                pst.setDouble(4, 0);
            }else{
                pst.setDouble(4, java.lang.Double.parseDouble(eprform.getBasic()));
            }
            if(eprform.getS_pay() == null || eprform.getS_pay().equals("")){
                pst.setDouble(5, 0);
            }else{
                pst.setDouble(5,java.lang.Double.parseDouble(eprform.getS_pay()));
            }
            if(eprform.getP_pay() == null || eprform.getP_pay().equals("")){
                pst.setDouble(6, 0);
            }else{
                pst.setDouble(6,java.lang.Double.parseDouble(eprform.getP_pay()));
            }
            if(eprform.getOth_desc() == null || eprform.getOth_desc().equals("")){
                pst.setDouble(7, 0);
            }else{
                pst.setDouble(7,java.lang.Double.parseDouble(eprform.getOth_pay()));
            }
            pst.setString(8,eprform.getOth_desc());
            if(eprform.getGp() == null || eprform.getGp().equals("")){
                pst.setDouble(9, 0);
            }else{
                pst.setDouble(9,java.lang.Double.parseDouble(eprform.getGp()));
            }
            pst.setString(10,eprform.getPayid());
            pst.setString(11,eprform.getEmpid());
            pst.executeUpdate();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    @Override
    public void deleteEmpPayRecordData(EmpPayRecordForm eprform) {
        
        Connection con = null;
        
        PreparedStatement pst = null;
        
        try{
            con = dataSource.getConnection();
            
            String sql = "DELETE FROM EMP_PAY_RECORD WHERE EMP_ID=? AND PAY_ID=?";
            pst = con.prepareStatement(sql);
            pst.setString(1,eprform.getEmpid());
            pst.setString(2,eprform.getPayid());
            pst.executeUpdate();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            DataBaseFunctions.closeSqlObjects(con);
        }
    }
}
