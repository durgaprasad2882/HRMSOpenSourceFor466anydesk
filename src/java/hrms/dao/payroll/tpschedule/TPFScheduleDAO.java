package hrms.dao.payroll.tpschedule;

import hrms.model.common.CommonReportParamBean;
import java.util.List;

public interface TPFScheduleDAO {
    
    public List getEmployeeWiseTPFList(String billno);
    
    public List getTPFAbstract(String billno);
    
    public CommonReportParamBean getCommonReportParameter(String billNo);
}
