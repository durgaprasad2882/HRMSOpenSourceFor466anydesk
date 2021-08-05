package hrms.dao.fiscalyear;

import hrms.model.fiscalyear.FiscalYear;
import java.util.List;

public interface FiscalYearDAO {
    
    public String getDefaultFiscalYear();
    
    public List getFiscalYearList();
    
     public List getPFiscalYearList();
}
