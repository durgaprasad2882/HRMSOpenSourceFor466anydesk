/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hrms.dao.discProceeding;

import hrms.model.discProceeding.DiscProceedingBean;
import hrms.model.discProceeding.DpViewBean;
import hrms.model.discProceeding.Rule15ChargeBean;
import java.util.ArrayList;
import java.util.List;

public interface DiscProceedingDAO {
    
    public List getDiscProcedingList(String empId, int page, int rows);
    public int getDiscProceedTotalCount(String empId);
    public List getDeptWiseEmpList(String deptCode);
    public List getOffWiseEmpList(String offCode);
    public List getPostWithEmpList(String offCode);
    
    public List getOffWiseEmpWitnessList(String offCode, String hrmsId, int dacId, String mode);
    public int saveRule15MemoDetails(DiscProceedingBean dpBean);
    public int getMaxDaId();
    public DiscProceedingBean getRule15MemoDetails(String offCode, String empHrmsId);
    public ArrayList getEmpComboDtls(String offCode, String hrmsid);
    public int saveRule15AddCharges(Rule15ChargeBean chargeBean, String filePath);   
    public List getRule15ChargeList(int doDaId, int page, int rows);
    public int getRule15ChargeTotalCount(int doDaId);
    public DpViewBean viewRule15DiscProceeding(String offCode, String daId);
    public int saveRule15AddWitness(Rule15ChargeBean chargeBean); 
    public int deleteChargeAndWitness(int dacId);
    public int deleteWitnessOnly(int dacId);
    public Rule15ChargeBean EditRule15ChargeData(Rule15ChargeBean chargeBean, int dacId);
    public int updateRule15Charges(Rule15ChargeBean chargeBean);
    public Rule15ChargeBean EditRule15WitnessData(Rule15ChargeBean chargeBean, int dacId);
    public int rule15UpdateWitness(Rule15ChargeBean chargeBean);
    public String getChargeDetails(int daid);
    
    public List getDPForwadrEmpList(String offCode);
    public List getPostWiseEmpList(String postCode);
    public int saveRule15ForwardDP(String authEmpId, String authEmpSpc, Rule15ChargeBean chargeBean);
    
    
    
}
