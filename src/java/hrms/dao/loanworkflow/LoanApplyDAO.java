/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.loanworkflow;

import com.itextpdf.text.Document;
import hrms.model.loanworkflow.LoanForm;
import hrms.model.loanworkflow.LoanGPFForm;
import hrms.model.loanworkflow.LoanHBAForm;
import hrms.model.loanworkflow.LoanTempGPFForm;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author lenovo
 */
public interface LoanApplyDAO {
    /* Method Declare */

    public LoanForm displayEmpDetails(String hrmsid);

    public void saveLoanData(LoanForm lform, String empid, String filepath);

    public List getLoanList(String empid);

    public String loanStatus(int statusid, int processid);

    public LoanForm getLoanDetails(int taskid, String hrmsid);

    public List getPostList(String offcode);

    public List getprocessList(String processid);

    public void saveApproveLoanData(LoanForm lform, String empid);
    
     public void saveLoansaction(LoanForm lform);
      public void saveLoanGPFsaction(LoanGPFForm lform);

    public LoanForm ReplyLoan(String Option, String hrmsid, int loanid);

    public LoanForm SactionLoanOrder(int taskid, String hrmsid, int loanid);
     public LoanForm PreviewSactionOrder(int taskid, String hrmsid, int loanid);

    public void downloadLoanAttachment(HttpServletResponse response, String filepath, String loanid) throws IOException;

    public void savereapplyLoanData(LoanForm lform, String empid, String filepath);

    public void deleteLoanAttch(int loanid, String filepath);

    public LoanGPFForm GPFEmpDetails(String hrmsid);

    public void savegpfLoanData(LoanGPFForm lform, String empid, String filepath);

    public LoanGPFForm getgpfLoanDetails(int taskid, String hrmsid);

    public void savegpfApproveLoanData(LoanGPFForm lform, String empid);

    public LoanGPFForm ReplygpfLoan(String Option, String hrmsid, int loanid);

    public void savegpfreapplyLoan(LoanGPFForm lform, String empid, String filepath);
    public LoanGPFForm SactionGPFOrder(int taskid, int loanid);
    
    
    public LoanHBAForm HBAEmpDetails(String hrmsid);

    public void saveHBALoanData(LoanHBAForm lform, String empid, String filepath);

    public LoanHBAForm gethbaLoanDetails(int taskid);

    public void savehbaApproveLoanData(LoanHBAForm lform, String empid);

    public void downloadHBALoanAttachment(HttpServletResponse response, String filepath, String loanid, String attchmentId) throws IOException;

    public LoanHBAForm ReplyhbaLoan(String Option, String hrmsid, int loanid);
    public void saveHBAreapplyLoan(LoanHBAForm lform, String empid, String filepath);
    
    public void viewPDFfunc(Document document,int loanid);
    
    
    public LoanTempGPFForm TempGPFEmpDetails(String hrmsid);
    public void savetempgpfLoanData(LoanTempGPFForm lform, String empid, String filepath);
    public LoanTempGPFForm tempgpfLoanDetails(int taskid);
    public void DownloadtempgpfLoanAttch(HttpServletResponse response, String filepath, String loanid) throws IOException;
     public void savetempfpfApproveData(LoanTempGPFForm lform);
     public LoanTempGPFForm Reapplytempgpf(String Option, String hrmsid, int loanid);
      public void savetempgpfreapply(LoanTempGPFForm lform, String empid, String filepath);
}
