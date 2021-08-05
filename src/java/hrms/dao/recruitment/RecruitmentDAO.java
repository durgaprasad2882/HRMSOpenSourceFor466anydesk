
package hrms.dao.recruitment;

import hrms.model.recruitment.RecruitmentModel;
import java.util.List;


public interface RecruitmentDAO {
    
    public int insertRecruitmentData(RecruitmentModel recruit);
    
    public int updateRecruitmentData(RecruitmentModel recruit);
    
    public int deleteRecruitment(String recruitId);
    
    public RecruitmentModel editRecruitment(String recruitId);
    
    public List findAllRecruitment(String empId);
    
}
