package hrms.dao.loansanction;

import hrms.model.loan.Loan;
import java.util.ArrayList;
import java.util.List;

public interface LoanSancDAO {

    public List getLoanSancList(String empId);

    public void saveLoanDetail(Loan loanForm, String notId);

    public Loan editLoanData(String loanId);

    public void removeLoanData(String loanId, String notId);

    public void updateLoanDetail(Loan loanForm);

    public List getPrincipalLoanListForBill(String empId, int payday, String depcode);

    public List getInterestLoanListForBill(String empId, int payday, String depcode);

    public List getLoanDeductionDeailEmpWise(String empId, String loanType);

    public String getFABTId(String demandNumber);
    
    public String getGPFBTId(String gpfType);

}
