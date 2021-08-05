/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.payroll.billbrowser;

import hrms.model.payroll.billbrowser.SectionDefinition;
import hrms.model.payroll.billbrowser.SectionDtlSPCWiseEmp;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 *
 * @author Manas Jena
 */
public interface SectionDefinationDAO {
    public ArrayList getSectionList(String offcode);
    public ArrayList getSPCList(String sectionId);
    public ArrayList getBillGroupWiseSectionList(BigDecimal billgroupid);
    public ArrayList getSPCWiseEmpInSection(int sectionid);
    public ArrayList getSPCWiseEmpInSection(int sectionid, String offcode, int pyear, int pmonth);
    public SectionDtlSPCWiseEmp getSPCEmpSection(String empId);
    public ArrayList getSPCWiseContEmpInSection(int sectionid);
    public ArrayList getSPCWiseCont6RegularEmpInSection(int sectionid);
    public ArrayList getSPCWiseEmpOnlyInSection(int sectionid);
    public String getSectionName(int sectionid);
    public String getBillType(int sectionid);
    public ArrayList getTotalAvailableEmp(String offcode);
    public ArrayList getTotalAssignEmp(String offcode,int sectionid);
    public ArrayList getTotalAvailableContractEmp(String offcode);
    public ArrayList getTotalAssignContractEmp(String offcode, int sectionid);
    public void mapPost(int sectionid, String spc);
    public void removePost(String spc);
    public void saveBillSection(String offCode, SectionDefinition SD);
    public SectionDefinition getBillSection(int billSectionId);
    public void updateBillSection(SectionDefinition SD);
    public SectionDtlSPCWiseEmp getEmpoyeeDataInTransit(SectionDtlSPCWiseEmp sdswe, int year, int month);
}
