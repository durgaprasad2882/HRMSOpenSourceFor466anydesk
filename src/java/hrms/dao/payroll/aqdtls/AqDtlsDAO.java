package hrms.dao.payroll.aqdtls;

import hrms.model.payroll.aqdtls.AqDtlsModel;
import hrms.model.payroll.billbrowser.AqDtlsDedBean;
import java.util.List;

public interface AqDtlsDAO {

    public int saveAqdtlsData(AqDtlsModel[] dtls, boolean stopSubscription);

    public int saveAqdtls1Data(AqDtlsModel[] dtls, boolean stopSubscription);

    public AqDtlsModel getAqdtlsData(String aqslno);

    public List getAqdtlsList(String aqslno);

    public int deleteAqdtls(String aqslno);

    public void updateAqDtlsData(AqDtlsDedBean aqDtlsDedBean);

    public AqDtlsDedBean getAqDetailsDed(String aqslNo, String dedType, String nowdedn);

    public AqDtlsDedBean getAqDetailsAllowance(String aqslNo, String dedType);

}
