package hrms.model.report.officewisesanctionedstrength;

public class OfficeWiseSanctionedStrengthBean {
    
    private String financialYear;
    private String groupAData;
    private String groupBData;
    private String groupCData;
    private String groupDData;
    private String grantInAid;
    private int total;
    
    private String aerId;

    public String getFinancialYear() {
        return financialYear;
    }

    public void setFinancialYear(String financialYear) {
        this.financialYear = financialYear;
    }

    public String getGroupAData() {
        return groupAData;
    }

    public void setGroupAData(String groupAData) {
        this.groupAData = groupAData;
    }

    public String getGroupBData() {
        return groupBData;
    }

    public void setGroupBData(String groupBData) {
        this.groupBData = groupBData;
    }

    public String getGroupCData() {
        return groupCData;
    }

    public void setGroupCData(String groupCData) {
        this.groupCData = groupCData;
    }

    public String getGroupDData() {
        return groupDData;
    }

    public void setGroupDData(String groupDData) {
        this.groupDData = groupDData;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getAerId() {
        return aerId;
    }

    public void setAerId(String aerId) {
        this.aerId = aerId;
    }

    public String getGrantInAid() {
        return grantInAid;
    }

    public void setGrantInAid(String grantInAid) {
        this.grantInAid = grantInAid;
    }
}
