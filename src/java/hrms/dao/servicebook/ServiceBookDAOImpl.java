package hrms.dao.servicebook;

import hrms.common.CommonFunctions;
import hrms.common.DataBaseFunctions;
import hrms.model.notification.EmployeeCadreSt;
import hrms.model.servicebook.EmpServiceBook;
import hrms.model.servicebook.EmpServiceHistory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;
//import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang.StringUtils;

public class ServiceBookDAOImpl implements ServiceBookDAO {

    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setMiscellaneousData(EmpServiceHistory esh) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT HEADER,DESCRIPTION FROM EMP_MISC WHERE MISC_ID=? AND EMP_ID=?");
            pstmt.setString(1, esh.getNoteId()+"");
            pstmt.setString(2, esh.getEmpId());
            resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                if (resultSet.getString("HEADER") != null && !resultSet.getString("HEADER").equals("")) {
                    esh.setCategory(resultSet.getString("HEADER").trim() + ".");
                }
                if (resultSet.getString("DESCRIPTION") != null && !resultSet.getString("DESCRIPTION").equals("")) {
                    String miscllaneousData = resultSet.getString("DESCRIPTION").trim();
                    miscllaneousData = miscllaneousData.replaceAll("[\n\r\t]", "");
                    miscllaneousData = StringUtils.replace(miscllaneousData, "\"", "");
                    esh.setModuleNote(miscllaneousData.trim());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(resultSet, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    public void setRetiredMentData(EmpServiceHistory esh) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        try {
            con = dataSource.getConnection();
            String retType = null;
            pstmt = con.prepareStatement("SELECT RET_TYPE FROM EMP_RET_RES WHERE RET_ID=? AND EMP_ID=?");
            pstmt.setInt(1, esh.getNoteId());
            pstmt.setString(2, esh.getEmpId());
            resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                if (resultSet.getString("RET_TYPE") != null && !resultSet.getString("RET_TYPE").trim().equals("")) {
                    retType = resultSet.getString("RET_TYPE");
                }
            }
            if (retType != null) {
                if (retType.equalsIgnoreCase("VR")) {
                    esh.setCategory("VOLUNTARY RETIREMENT");
                } else if (retType.equalsIgnoreCase("SR")) {
                    esh.setCategory("RETIREMENT ON SUPERANNUATION");
                } else if (retType.equalsIgnoreCase("CR")) {
                    esh.setCategory("COMPULSORY RETIREMENT");
                } else if (retType.equalsIgnoreCase("RG")) {
                    esh.setCategory("RESIGNATION");
                } else if (retType.equalsIgnoreCase("TR")) {
                    esh.setCategory("TERMINATION");
                } else if (retType.equalsIgnoreCase("RT")) {
                    esh.setCategory("RETRENCHMENT");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(resultSet, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    public void setLoanTransactionType(EmpServiceHistory esh) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT TRAN_TYPE FROM EMP_LOAN_TRAN WHERE NOT_ID=? and EMP_ID=?");
            pstmt.setInt(1, esh.getNoteId());
            pstmt.setString(2, esh.getEmpId());
            resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                if (resultSet.getString("TRAN_TYPE") != null && !resultSet.getString("TRAN_TYPE").equals("")) {
                    if (resultSet.getString("TRAN_TYPE").equals("REPAYMENT")) {
                        esh.setCategory("REPAYMENT OF LOAN");
                    } else if (resultSet.getString("TRAN_TYPE").equals("RELEASE")) {
                        esh.setCategory("RELEASE OF LOAN");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(resultSet, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    public void setEmployeeLeaveType(EmpServiceHistory esh) {
        String lsotId = null;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT * FROM EMP_LEAVE WHERE NOT_ID=? and EMP_ID=?");
            pstmt.setInt(1, esh.getNoteId());
            pstmt.setString(2, esh.getEmpId());
            resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                lsotId = resultSet.getString("LSOT_ID");
            }
            pstmt = con.prepareStatement("SELECT * FROM G_LSOT WHERE LSOT_ID=?");
            pstmt.setString(1, lsotId);
            resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                if (lsotId.equalsIgnoreCase("01")) {
                    esh.setCategory("LEAVE SANCTIONED");
                } else if (lsotId.equalsIgnoreCase("02")) {
                    esh.setCategory("LEAVE SURRENDERED");
                } else {
                    esh.setCategory(resultSet.getString("LSOT"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(resultSet, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    public void setEmployeeDeputationData(EmpServiceHistory esh) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        try {
            con = dataSource.getConnection();
            String ifExt = null;
            pstmt = con.prepareStatement("SELECT IF_EXTENSION FROM EMP_DEPUTATION WHERE NOT_TYPE='DEPUTATION' AND NOT_ID=? AND EMP_ID=?");
            pstmt.setInt(1, esh.getNoteId());
            pstmt.setString(2, esh.getEmpId());
            resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                if (resultSet.getString("IF_EXTENSION") != null) {
                    ifExt = resultSet.getString("IF_EXTENSION");
                }
            }
            if (ifExt != null && ifExt.equalsIgnoreCase("Y")) {
                esh.setCategory("EXTENSION OF DEPUTATION");
            } else if (ifExt == null) {
                esh.setCategory(esh.getTabname().toUpperCase());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(resultSet, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    public void setEmployeeAgDeputationData(EmpServiceHistory esh) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT IF_EXTENSION FROM EMP_DEPUTATION WHERE NOT_TYPE='DEPUTATION_AG' AND NOT_ID=? AND EMP_ID=?");
            pstmt.setInt(1, esh.getNoteId());
            pstmt.setString(2, esh.getEmpId());
            resultSet = pstmt.executeQuery();
            String ifExt = null;
            if (resultSet.next()) {
                if (resultSet.getString("IF_EXTENSION") != null) {
                    ifExt = resultSet.getString("IF_EXTENSION");
                }
            }
            if (ifExt != null && ifExt.equalsIgnoreCase("Y")) {
                esh.setCategory("ENDORSEMENT AG ON EXTENSION OF DEPUTATION");
            } else {
                esh.setCategory("ENDORSEMENT OF AG ON DEPUTATION");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(resultSet, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    public void setWefDate(EmpServiceHistory esh) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        try {
            con = dataSource.getConnection();
            String tabName = esh.getTabname();
            if (tabName.equalsIgnoreCase("INCREMENT") || tabName.equalsIgnoreCase("PAYREVISION") || tabName.equalsIgnoreCase("PAYFIXATION") || tabName.equalsIgnoreCase("STEPUP") || tabName.equalsIgnoreCase("PAY_ENTITLEMENT")) {
                pstmt = con.prepareStatement("SELECT WEF FROM EMP_PAY_RECORD WHERE NOT_ID = ?");
                pstmt.setInt(1, esh.getNoteId());
                resultSet = pstmt.executeQuery();
                if (resultSet.next()) {
                    if (resultSet.getDate("WEF") != null && !resultSet.getString("WEF").trim().equals("")) {
                        esh.setWefChange(CommonFunctions.getFormattedOutputDate1(resultSet.getDate("WEF")));
                    }
                }
            } else if (tabName.equalsIgnoreCase("FIRST_APPOINTMENT") || tabName.equalsIgnoreCase("ABSORPTION") || tabName.equalsIgnoreCase("DIRECT")
                    || tabName.equalsIgnoreCase("REDEPLOYMENT") || tabName.equalsIgnoreCase("REHABILITATION")
                    || tabName.equalsIgnoreCase("SELECTION") || tabName.equalsIgnoreCase("VALIDATION") || tabName.equalsIgnoreCase("PROMOTION")
                    || tabName.equalsIgnoreCase("REPATRIATION") || tabName.equalsIgnoreCase("JOIN_CADRE") || tabName.equalsIgnoreCase("RELIEVE_CADRE")
                    || tabName.equalsIgnoreCase("OFFICIATE") || tabName.equalsIgnoreCase("CONFIRMATION") || tabName.equalsIgnoreCase("TRANSFER")
                    || tabName.equalsIgnoreCase("POSTING") || tabName.equalsIgnoreCase("SERVICE_DISPOSAL") || tabName.equalsIgnoreCase("ADDITIONAL_CHARGE")) {
                pstmt = con.prepareStatement("SELECT WEFD FROM EMP_CADRE WHERE NOT_ID = ?");
                pstmt.setInt(1, esh.getNoteId());
                resultSet = pstmt.executeQuery();
                if (resultSet.next()) {
                    if (resultSet.getDate("WEFD") != null && !resultSet.getString("WEFD").trim().equals("")) {
                        esh.setWefChange(CommonFunctions.getFormattedOutputDate1(resultSet.getDate("WEFD")));
                    }
                }
            } else if (tabName.equalsIgnoreCase("wefPiInfo")) {
                pstmt = con.prepareStatement("SELECT WEF FROM EMP_PI_HISTORY WHERE NOT_ID = ?");
                pstmt.setInt(1, esh.getNoteId());
                resultSet = pstmt.executeQuery();
                if (resultSet.next()) {
                    if (resultSet.getDate("WEF") != null && !resultSet.getString("WEF").trim().equals("")) {
                        esh.setWefChange(CommonFunctions.getFormattedOutputDate1(resultSet.getDate("WEF")));
                    }
                }
            } else if (tabName.equalsIgnoreCase("SUSPENSION") || tabName.equalsIgnoreCase("HEAD QUARTER FIXATION")) {
                pstmt = con.prepareStatement("SELECT WEFD FROM EMP_SUSPENSION WHERE SP_ID = ?");
                pstmt.setInt(1, esh.getNoteId());
                resultSet = pstmt.executeQuery();
                if (resultSet.next()) {
                    if (resultSet.getDate("WEFD") != null && !resultSet.getString("WEFD").trim().equals("")) {
                        esh.setWefChange(CommonFunctions.getFormattedOutputDate1(resultSet.getDate("WEFD")));
                    }
                }
            } else if (tabName.equalsIgnoreCase("JOINING")) {
                String jWeft = getWefTime(esh);
                pstmt = con.prepareStatement("SELECT JOIN_DATE FROM EMP_JOIN WHERE NOT_ID = ?");
                pstmt.setInt(1, esh.getNoteId());
                resultSet = pstmt.executeQuery();
                if (resultSet.next()) {
                    if (resultSet.getDate("JOIN_DATE") != null && !resultSet.getString("JOIN_DATE").trim().equals("")) {
                        if (jWeft != null && !jWeft.equalsIgnoreCase("FN")) {
                            esh.setWefChange(CommonFunctions.getFormattedOutputDate1(resultSet.getDate("JOIN_DATE")) + "(" + jWeft + ")");
                        } else {
                            esh.setWefChange(CommonFunctions.getFormattedOutputDate1(resultSet.getDate("JOIN_DATE")));
                        }
                    }
                }
            } else if (tabName.equalsIgnoreCase("RELIEVE")) {
                String jWeft = getWefTime(esh);
                pstmt = con.prepareStatement("SELECT RLV_DATE FROM EMP_RELIEVE WHERE NOT_ID = ?");
                pstmt.setInt(1, esh.getNoteId());
                resultSet = pstmt.executeQuery();
                if (resultSet.next()) {
                    if (resultSet.getDate("RLV_DATE") != null && !resultSet.getString("RLV_DATE").trim().equals("")) {
                        if (jWeft != null && !jWeft.equalsIgnoreCase("FN")) {
                            esh.setWefChange(CommonFunctions.getFormattedOutputDate1(resultSet.getDate("RLV_DATE")) + "(" + jWeft + ")");
                        } else {
                            esh.setWefChange(CommonFunctions.getFormattedOutputDate1(resultSet.getDate("RLV_DATE")));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(resultSet, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    public String getWefTime(EmpServiceHistory esh) throws Exception {
        String wefTime = null;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        try {
            con = dataSource.getConnection();
            if (esh.getTabname().equalsIgnoreCase("RELIEVE")) {
                pstmt = con.prepareStatement("SELECT RLV_TIME FROM EMP_RELIEVE WHERE NOT_ID=?");
                pstmt.setInt(1, esh.getNoteId());
                resultSet = pstmt.executeQuery();
                if (resultSet.getString("RLV_TIME") != null && !resultSet.getString("RLV_TIME").trim().equals("")) {
                    wefTime = resultSet.getString("RLV_TIME").trim();
                }
            } else if (esh.getTabname().equalsIgnoreCase("JOINING")) {
                pstmt = con.prepareStatement("SELECT JOIN_TIME FROM EMP_JOIN WHERE NOT_ID = ??");
                pstmt.setInt(1, esh.getNoteId());
                resultSet = pstmt.executeQuery();
                if (resultSet.getString("JOIN_TIME") != null && !resultSet.getString("JOIN_TIME").trim().equals("")) {
                    wefTime = resultSet.getString("JOIN_TIME").trim();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(resultSet, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return wefTime;
    }

    public EmployeeCadreSt getCadreInfoOfNotification(String notid) {
        EmployeeCadreSt ec = null;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT * FROM EMP_CADRE WHERE NOT_ID=?");
            pstmt.setString(1, notid);
            resultSet = pstmt.executeQuery();
            if(resultSet.next()){
                ec = new EmployeeCadreSt();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(resultSet, pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return ec;
    }
    
    /*Cadre/Post/Pay*/
    public void setCadrePostPay(EmpServiceHistory esh) {
        String tabName = esh.getTabname();
        if (tabName.equalsIgnoreCase("ABSORPTION") || tabName.equalsIgnoreCase("DIRECT") || tabName.equalsIgnoreCase("REDEPLOYMENT") || tabName.equalsIgnoreCase("REHABILITATION") || tabName.equalsIgnoreCase("REGULARIZATION") || tabName.equalsIgnoreCase("SELECTION") || tabName.equalsIgnoreCase("VALIDATION") || tabName.equalsIgnoreCase("CONFIRMATION") || tabName.equalsIgnoreCase("PROMOTION") || tabName.equalsIgnoreCase("REPATRIATION") || tabName.equalsIgnoreCase("JOIN_CADRE") || tabName.equalsIgnoreCase("FIRST_APPOINTMENT") || tabName.equalsIgnoreCase("PAY_ENTITLEMENT") || tabName.equalsIgnoreCase("INCREMENT") || tabName.equalsIgnoreCase("PAYREVISION") || tabName.equalsIgnoreCase("PAYFIXATION") || tabName.equalsIgnoreCase("STEPUP")) {

        } else if (tabName.equalsIgnoreCase("JOINING")) {

        } else {
            esh.setCadre("N");
            esh.setPay("N");
        }
    }

    public void setTabName(EmpServiceHistory esh) {
        String tabName = esh.getTabname();
        if (tabName != null) {
            if (tabName.equalsIgnoreCase("DEPUTATION")) {
                setEmployeeDeputationData(esh);
            } else if (tabName.equalsIgnoreCase("DEPUTATION_AG")) {
                setEmployeeAgDeputationData(esh);
            } else if (tabName.equalsIgnoreCase("FIRST_APPOINTMENT")) {
                esh.setCategory("REGULAR RECRUITMENT DETAILS");
            } else if (tabName.equalsIgnoreCase("GREEN_CARD")) {
                esh.setCategory("GREEN CARD DETAILS");
            } else if (tabName.equalsIgnoreCase("JOIN_CADRE")) {
                esh.setCategory("JOINING IN CADRE");
            } else if (tabName.equalsIgnoreCase("INITIATION")) {
                esh.setCategory("DEPARTMENTAL PROCEEDING");
            } else if (tabName.equalsIgnoreCase("RELIEVE_CADRE")) {
                esh.setCategory("RELIEVE FROM CADRE");
            } else if (tabName.equalsIgnoreCase("PAY_ENTITLEMENT")) {
                esh.setCategory("PAY ENTITLEMENT");
            } else if (tabName.equalsIgnoreCase("TRANSFER")) {
                esh.setCategory("TRANSFER & POSTING");
            } else if (tabName.equalsIgnoreCase("EQ_STAT")) {
                esh.setCategory("EQUIVALENT TO POST");
            } else if (tabName.equalsIgnoreCase("EXAMINATION")) {
                esh.setCategory("DEPARTMENTAL EXAMINATION");
            } else if (tabName.equalsIgnoreCase("PAYREVISION")) {
                esh.setCategory("PAY REVISION");
            } else if (tabName.equalsIgnoreCase("PAYFIXATION")) {
                esh.setCategory("PAY FIXATION");
            } else if (tabName.equalsIgnoreCase("STEPUP")) {
                esh.setCategory("STEPUP OF PAY");
            } else if (tabName.equalsIgnoreCase("ADDITIONAL_CHARGE")) {
                esh.setCategory("ADDITIONAL CHARGE");
            } else if (tabName.equalsIgnoreCase("LT_TRAINING")) {
                esh.setCategory("LONG TERM/ MANDATORY TRAINING OR COURSE");
            } else if (tabName.equalsIgnoreCase("LEAVE")) {
                setEmployeeLeaveType(esh);
            } else if (tabName.equalsIgnoreCase("PI_CHANGE")) {
                esh.setCategory("CHANGE OF NAME/GPF A/C NO.");
            } else if (tabName.equalsIgnoreCase("LOAN_TRAN")) {
                setLoanTransactionType(esh);
            } else if (tabName.equalsIgnoreCase("LOAN_SANC")) {
                esh.setCategory("LOAN SANCTION");
            } else if (tabName.equalsIgnoreCase("RES_CAT")) {
                esh.setCategory("RESERVATION CATEGORY");
            } else if (tabName.equalsIgnoreCase("ALLOT_CADRE")) {
                esh.setCategory("ALLOTMENT TO CADRE");
            } else if (tabName.equalsIgnoreCase("ENROLLMENT")) {
                esh.setCategory("ENROLMENT TO INSURANCE SCHEME");
            } else if (tabName.equalsIgnoreCase("REHABILITATION")) {
                esh.setCategory("REHABILITATION(FIRST APPOINTMENT)");
            } else if (tabName.equalsIgnoreCase("SERVICE_DISPOSAL")) {
                esh.setCategory("PLACEMENT OF SERVICE");
            } else if (tabName.equalsIgnoreCase("BRASS_ALLOT")) {
                esh.setCategory("BRASS NO. ALLOTMENT");
            } else if (tabName.equalsIgnoreCase("REGULARIZATION")) {
                esh.setCategory("REGULARIZATION OF SERVICE");
            } else if (tabName.equalsIgnoreCase("RETIREMENT")) {
                setRetiredMentData(esh);
            } else if (tabName.equalsIgnoreCase("MISC")) {
                setMiscellaneousData(esh);
            } else if (tabName.equalsIgnoreCase("DET_VAC")) {
                esh.setCategory("DETENTION OF VACATION");
            } else if (tabName.equalsIgnoreCase("OFFICIATE")) {
                esh.setCategory("ALLOWED TO OFFICIATE");
            } else {
                esh.setCategory(tabName.toUpperCase());
            }
        }//
    }

    @Override
    public EmpServiceBook getSHReport(String employeeCodes) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List getServiceBookAnnexureAData(String empid) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List getServiceBookAnnexureBData(String empid) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List getServiceBookAnnexureDData(String empid) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List getServiceBookAnnexureEData(String empid) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
}
