/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.payroll.arrear;

import hrms.model.payroll.arrear.ArrAqDtlsModel;
import hrms.model.payroll.arrear.ArrAqMastModel;
import hrms.model.payroll.arrear.PayRevisionOption;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import jxl.write.WritableWorkbook;

/**
 *
 * @author Manas
 */
public interface ArrmastDAO {

    public String saveArrmastdata(ArrAqMastModel arrAqMastModel);

    public String addEmployeeToBill(ArrAqMastModel arrAqMastModel);
    
    public int getCalcUniqueNo(String aqslno);

    public void saveArrdtlsdata(ArrAqDtlsModel[] arrAqDtlsModels, int calcUniqueNo);
    
    public void deleteArrdtlsdata(String aqslno);

    public List getArrearBillDtls(int billNo, String aqMonth, String aqYear);

    public void reprocessArrAqMast(String aqslno);

    public List getArrearAcquaintance(int billNo);

    public List getArrearAqHeaderData(int billNo);

    public ArrAqMastModel getArrearAcquaintanceData(String aqSlNo);

    public List getArrearAcquaintanceDtls(String aqSlNo);

    public ArrAqDtlsModel getArrearAcquaintanceDataList(String aqSlNo, int aqMonth, int aqYear);

    public int updateArrAqMastItData(int billNo, String aqSlNo, int incTax);

    public int updateArrMastItData(int billNo, String aqSlNo, int incTax);

    public int updateArrMastCpfData(int billNo, String aqSlNo, int cpfAmt);

    public int updateArrDtlsData(ArrAqDtlsModel arrAqDtlsModel);

    public int deleteArrDtlsData(ArrAqDtlsModel arrDtlsBean);

    public PayRevisionOption searchEmployee(String searchemp);

    public int updateArrMastPtData(int billNo, String aqSlNo, int ptAmt);

    public List getArrearAcqEmpDet(String billNo);

    public List getArrearAcqEmpPayDetails(String aqSlNo, int month, int year);

    public void downloadArrearAcqEmpExcel(OutputStream out, String offcode, WritableWorkbook workbook, String billNo) throws Exception;

    public int deleteArrMastData(String aqSlNo, int billNo);

    public ArrAqDtlsModel[] getAqDtlsModelFromAllowanceList(PayRevisionOption po, int month, int year, String empCode, double dapercentage, String aqslno);
    
    public ArrAqDtlsModel[] getAqDtlsModelFromAllowanceListForContractual(Date choiceDate, int month, int year, String empCode, int dapercentage, String aqslno, int[] contractualMatrix, int gp);

    public PayRevisionOption getChoiceDate(String empCode);        

    public int reCalculateArrMast(int billNo);
    
    public void insertIntoPayRevisionOption(String inputChoiceDate, int payrevisionbasic,String empid);
    
    public int[] getPayMatrix(int gp);
}
