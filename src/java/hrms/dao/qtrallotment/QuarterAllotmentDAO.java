package hrms.dao.qtrallotment;

import hrms.model.qtrallotment.QuarterAllotment;
import java.util.List;

public interface QuarterAllotmentDAO {

    public List QuaterAllotSt(String empId);

    public QuarterAllotment editQuarterAllotment(String qtrAllotId);

    public int saveQuaterAllotmentRecord(QuarterAllotment quarterAllot);

    public int saveQuaterSurrenderRecord(QuarterAllotment quarterAllot);

    public QuarterAllotment getSurrenderEditRecords(String qtrSurId);

    public void deleteQtrAllot(String qtrid, String empid);

    public void deleteQtrSurrendRecords(String qtrid, String surid, String empid);
    

}
