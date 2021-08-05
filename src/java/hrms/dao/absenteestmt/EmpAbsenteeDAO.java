package hrms.dao.absenteestmt;
import hrms.model.absentee.Absentee;
import java.util.ArrayList;
public interface EmpAbsenteeDAO {
    public ArrayList getAbseneteeList(String empid, String year, int month);
    public boolean saveAbsenteeData(Absentee abform);
    public boolean deleteAbsEmploye(String empId, String absid);
    public ArrayList getAbseneteeYear(String empid);
}
