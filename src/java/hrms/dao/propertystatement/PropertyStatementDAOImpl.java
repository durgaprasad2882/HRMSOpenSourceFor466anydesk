/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.dao.propertystatement;

import hrms.common.CommonFunctions;
import hrms.common.DataBaseFunctions;
import hrms.model.propertystatement.PropertyDetail;
import hrms.model.propertystatement.PropertyStatement;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Manas Jena
 */
public class PropertyStatementDAOImpl implements PropertyStatementDAO {

    @Resource(name = "dataSource")
    protected DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public ArrayList getPropertyList(String empid) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet resultset = null;
        ArrayList propertyList = new ArrayList();
        try {
            con = this.dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT YEARLY_PROPERTY_ID,FINANCIAL_YEAR,STATUS_ID,FROM_DATE,TO_DATE,SUBMISSION_TYPE FROM PROPERTY_STATEMENT_LIST WHERE EMP_ID=?");
            pstmt.setString(1, empid);
            resultset = pstmt.executeQuery();
            int slno = 0;
            while (resultset.next()) {
                slno++;
                PropertyStatement propstmt = new PropertyStatement();
                propstmt.setSlNo(slno);
                propstmt.setYearlyPropId(resultset.getBigDecimal("YEARLY_PROPERTY_ID"));
                propstmt.setFiscalyear(resultset.getString("FINANCIAL_YEAR"));
                propstmt.setStatusid(resultset.getInt("STATUS_ID"));
                propstmt.setFromdate(resultset.getDate("FROM_DATE"));
                propstmt.setTodate(resultset.getDate("TO_DATE"));
                propstmt.setSubmissiontype(resultset.getString("SUBMISSION_TYPE"));
                propertyList.add(propstmt);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return propertyList;
    }

    @Override
    public boolean isDuplicatePropertyPeriod(PropertyStatement propertyStatement) {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        boolean isDuplicate = true;
        boolean duplPeriod = false;
        String sql = "";
        String dbf1 = "";
        String dbt1 = "";
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            con = this.dataSource.getConnection();
            if (propertyStatement.getYearlyPropId() != null) {
                sql = "SELECT FROM_DATE,TO_DATE FROM  PROPERTY_STATEMENT_LIST WHERE EMP_ID=? AND FINANCIAL_YEAR=? AND YEARLY_PROPERTY_ID <> ";
                pst = con.prepareStatement(sql);
                pst.setString(1, propertyStatement.getEmpid());
                pst.setString(2, propertyStatement.getFiscalyear());
            } else {
                sql = "SELECT FROM_DATE,TO_DATE FROM  PROPERTY_STATEMENT_LIST WHERE EMP_ID=? AND FINANCIAL_YEAR=?";                
                pst = con.prepareStatement(sql);
                pst.setString(1, propertyStatement.getEmpid());
                pst.setString(2, propertyStatement.getFiscalyear());
            }
            rs = pst.executeQuery();
            int noofrows = 0;
            while (rs.next()) {
                noofrows++;
                dbf1 = CommonFunctions.getFormattedOutputDate1(rs.getDate("FROM_DATE"));
                dbt1 = CommonFunctions.getFormattedOutputDate1(rs.getDate("TO_DATE"));
                Date frs1 = formatter.parse(dbf1);
                Date trs1 = formatter.parse(dbt1);
                if (propertyStatement.getFromdate().compareTo(frs1) > 0 && (propertyStatement.getFromdate().compareTo(trs1) > 0 && propertyStatement.getTodate().compareTo(trs1) > 0)) {
                    duplPeriod = true;
                } else if ((propertyStatement.getFromdate().compareTo(frs1) < 0) && (propertyStatement.getTodate().compareTo(frs1) < 0 && propertyStatement.getTodate().compareTo(trs1) < 0)) {
                    duplPeriod = true;
                } else if (propertyStatement.getFromdate().compareTo(frs1) == 0 && propertyStatement.getTodate().compareTo(trs1) == 0) {
                    duplPeriod = true;
                } else {
                    duplPeriod = false;
                }

                if (duplPeriod == false) {
                    isDuplicate = false;
                }
            }
            if (noofrows == 0) {
                isDuplicate = false;
            }
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return isDuplicate;
    }

    @Override
    public void savePropertyStmt(PropertyStatement psf) {
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = this.dataSource.getConnection();
            int yearlyPropId = CommonFunctions.getMaxCodeInteger("PROPERTY_STATEMENT_LIST", "YEARLY_PROPERTY_ID", con);
            pst = con.prepareStatement("INSERT INTO PROPERTY_STATEMENT_LIST(YEARLY_PROPERTY_ID,EMP_ID,FINANCIAL_YEAR,STATUS_ID,FROM_DATE,TO_DATE,BASIC_SAL,PAY_SCALE,CADRE_CODE,SPC,POST_GROUP,SUBMISSION_TYPE)VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");
            pst.setInt(1, yearlyPropId);
            pst.setString(2, psf.getEmpid());
            pst.setString(3, psf.getFiscalyear());
            pst.setInt(4, 0);
            pst.setDate(5, new java.sql.Date(psf.getFromdate().getTime()));
            pst.setDate(6, new java.sql.Date(psf.getTodate().getTime()));
            pst.setInt(7, psf.getCurbasicsalary());
            pst.setString(8, psf.getPayscale());
            pst.setString(9, psf.getCurcadrecode());
            pst.setString(10, psf.getCurspc());
            pst.setString(11, psf.getPostgroup());
            pst.setString(12, psf.getSubmissiontype());
            pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
    }

    @Override
    public boolean deletePropertyStmt(BigDecimal yearlyPropId, String empId) {
        boolean isDeleted = false;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet resultset = null;
        int statusId = 1;
        try {
            con = this.dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT STATUS_ID FROM PROPERTY_STATEMENT_LIST WHERE YEARLY_PROPERTY_ID=? AND EMP_ID=?");
            pstmt.setBigDecimal(1, yearlyPropId);
            pstmt.setString(2, empId);
            resultset = pstmt.executeQuery();
            if (resultset.next()) {
                statusId = resultset.getInt("STATUS_ID");
            }
            if (statusId == 0) {
                pstmt = con.prepareStatement("DELETE FROM PROPERTY_STATEMENT_LIST WHERE YEARLY_PROPERTY_ID=? AND EMP_ID=?");
                pstmt.setBigDecimal(1, yearlyPropId);
                pstmt.setString(2, empId);
                pstmt.executeUpdate();
                isDeleted = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return isDeleted;
    }

    @Override
    public PropertyStatement getPropertyStmt(BigDecimal yearlyPropId) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet resultset = null;
        PropertyStatement propstmt = new PropertyStatement();
        try {
            con = this.dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT YEARLY_PROPERTY_ID,FINANCIAL_YEAR,STATUS_ID,SUBMISSION_TYPE,FROM_DATE,TO_DATE,INITIALS,F_NAME,M_NAME,L_NAME,SPN,PROPERTY_STATEMENT_LIST.PAY_SCALE,PROPERTY_STATEMENT_LIST.BASIC_SAL,EMP_MAST.GP,SUBMITTED_ON FROM PROPERTY_STATEMENT_LIST "
                    + "INNER JOIN EMP_MAST ON PROPERTY_STATEMENT_LIST.EMP_ID = EMP_MAST.EMP_ID "
                    + "LEFT OUTER JOIN G_SPC ON PROPERTY_STATEMENT_LIST.SPC = G_SPC.SPC "
                    + "WHERE YEARLY_PROPERTY_ID=?");
            pstmt.setBigDecimal(1, yearlyPropId);
            resultset = pstmt.executeQuery();
            if (resultset.next()) {
                propstmt.setFullname(StringUtils.defaultString(resultset.getString("INITIALS")) + " " + resultset.getString("F_NAME") + " " + StringUtils.defaultString(resultset.getString("M_NAME")) + " " + resultset.getString("L_NAME"));
                propstmt.setSpn(resultset.getString("SPN"));
                propstmt.setPayscale(resultset.getString("PAY_SCALE"));
                propstmt.setCurbasicsalary(resultset.getInt("BASIC_SAL"));
                propstmt.setGradepay(resultset.getInt("GP"));
                propstmt.setYearlyPropId(resultset.getBigDecimal("YEARLY_PROPERTY_ID"));
                propstmt.setFiscalyear(resultset.getString("FINANCIAL_YEAR"));
                propstmt.setStatusid(resultset.getInt("STATUS_ID"));
                propstmt.setSubmittedOn(resultset.getDate("SUBMITTED_ON"));
                propstmt.setSubmissiontype(resultset.getString("SUBMISSION_TYPE"));
                propstmt.setFromdate(resultset.getDate("FROM_DATE"));
                propstmt.setTodate(resultset.getDate("TO_DATE"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return propstmt;
    }

    @Override
    public ArrayList getMovablePropertyDetailList(BigDecimal yearlyPropId) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet resultset = null;
        ArrayList propertyDetailList = new ArrayList();
        try {
            con = this.dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT property_details_id,PROPERTY_NAME,property_details.PROPERTY_ID,PROPERTY_NATURE,PROPERTY_LOCATION,VALUE,LOAN,DATE_OF_ACQUISITION,MANNER,REMARKS,PROPERTY_DETAILS.OWNER_TYPE_ID,OWNER_TYPE_NAME,OTHER_PROPERTY_OWNER,DESC_OF_OTH_ITEM,AREAUNIT from property_details "
                    + "INNER JOIN PROPERTY_MASTER ON property_details.PROPERTY_ID = PROPERTY_MASTER.PROPERTY_ID "
                    + "INNER JOIN PROPERTY_TYPE_MASTER ON PROPERTY_TYPE_MASTER.PROPERTY_TYPE_ID=PROPERTY_MASTER.PROPERTY_TYPE_ID "
                    + "INNER JOIN PROPERTY_OWNER ON PROPERTY_DETAILS.OWNER_TYPE_ID = PROPERTY_OWNER.OWNER_TYPE_ID "
                    + "WHERE property_details.yearly_property_id=? AND PROPERTY_TYPE_MASTER.PROPERTY_TYPE_ID=1");
            pstmt.setBigDecimal(1, yearlyPropId);
            resultset = pstmt.executeQuery();
            int slno = 0;
            while (resultset.next()) {
                slno++;
                PropertyDetail propertyDetail = new PropertyDetail();
                propertyDetail.setSlno(slno);
                propertyDetail.setPropertyDtlsId(resultset.getBigDecimal("property_details_id"));
                propertyDetail.setPropertyId(resultset.getInt("PROPERTY_ID"));
                propertyDetail.setPropertyName(resultset.getString("PROPERTY_NAME"));
                propertyDetail.setPropertyNature(resultset.getString("PROPERTY_NATURE"));
                propertyDetail.setPropertyLocation(resultset.getString("PROPERTY_LOCATION"));
                propertyDetail.setPropertyValue(resultset.getBigDecimal("VALUE"));
                propertyDetail.setLoan(resultset.getBigDecimal("LOAN"));
                propertyDetail.setDateOfAcq(resultset.getDate("DATE_OF_ACQUISITION"));
                propertyDetail.setManner(resultset.getString("MANNER"));
                propertyDetail.setRemark(resultset.getString("REMARKS"));
                propertyDetail.setPropertyOwner(resultset.getInt("OWNER_TYPE_ID"));
                propertyDetail.setPropertyOwnerDtl(resultset.getString("OWNER_TYPE_NAME"));
                propertyDetail.setOtherPropertyOwner(resultset.getString("OTHER_PROPERTY_OWNER"));
                propertyDetail.setDescofothitem(resultset.getString("DESC_OF_OTH_ITEM"));
                propertyDetail.setAreaunit(resultset.getString("AREAUNIT"));
                propertyDetailList.add(propertyDetail);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return propertyDetailList;
    }

    @Override
    public ArrayList getImmovablePropertyDetailList(BigDecimal yearlyPropId) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet resultset = null;
        ArrayList propertyDetailList = new ArrayList();
        try {
            con = this.dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT property_details_id,PROPERTY_NAME,property_details.PROPERTY_ID,PROPERTY_NATURE,PROPERTY_LOCATION,VALUE,PROPERTY_AREA,INTEREST1,DATE_OF_ACQUISITION,REMARKS,MANNER,PROPERTY_DETAILS.OWNER_TYPE_ID,OWNER_TYPE_NAME,OTHER_PROPERTY_OWNER,DESC_OF_OTH_ITEM, AREAUNIT from property_details "
                    + "INNER JOIN PROPERTY_MASTER ON property_details.PROPERTY_ID = PROPERTY_MASTER.PROPERTY_ID "
                    + "INNER JOIN PROPERTY_TYPE_MASTER ON PROPERTY_TYPE_MASTER.PROPERTY_TYPE_ID=PROPERTY_MASTER.PROPERTY_TYPE_ID "
                    + "INNER JOIN PROPERTY_OWNER ON PROPERTY_DETAILS.OWNER_TYPE_ID = PROPERTY_OWNER.OWNER_TYPE_ID "
                    + "WHERE property_details.yearly_property_id=? AND PROPERTY_TYPE_MASTER.PROPERTY_TYPE_ID=2");
            pstmt.setBigDecimal(1, yearlyPropId);
            resultset = pstmt.executeQuery();
            int slno = 0;
            while (resultset.next()) {
                slno++;
                PropertyDetail propertyDetail = new PropertyDetail();
                propertyDetail.setSlno(slno);
                propertyDetail.setPropertyDtlsId(resultset.getBigDecimal("property_details_id"));
                propertyDetail.setPropertyId(resultset.getInt("PROPERTY_ID"));
                propertyDetail.setPropertyName(resultset.getString("PROPERTY_NAME"));
                propertyDetail.setPropertyNature(resultset.getString("PROPERTY_NATURE"));
                propertyDetail.setPropertyLocation(resultset.getString("PROPERTY_LOCATION"));
                propertyDetail.setPropertyValue(resultset.getBigDecimal("VALUE"));
                propertyDetail.setPropertyArea(resultset.getBigDecimal("PROPERTY_AREA"));
                propertyDetail.setInterest(resultset.getString("INTEREST1"));
                propertyDetail.setDateOfAcq(resultset.getDate("DATE_OF_ACQUISITION"));
                propertyDetail.setRemark(resultset.getString("REMARKS"));
                propertyDetail.setManner(resultset.getString("MANNER"));
                propertyDetail.setPropertyOwner(resultset.getInt("OWNER_TYPE_ID"));
                propertyDetail.setPropertyOwnerDtl(resultset.getString("OWNER_TYPE_NAME"));
                propertyDetail.setOtherPropertyOwner(resultset.getString("OTHER_PROPERTY_OWNER"));
                propertyDetail.setDescofothitem(resultset.getString("DESC_OF_OTH_ITEM"));
                propertyDetail.setAreaunit(resultset.getString("AREAUNIT"));
                propertyDetailList.add(propertyDetail);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(con);
        }
        return propertyDetailList;
    }

    @Override
    public int saveImmovableProperty(PropertyDetail psf) {
        Connection con = null;
        PreparedStatement pst = null;
        int mcode = 0;
        try {
            con = this.dataSource.getConnection();
            if (psf.getPropertyDtlsId() != null && psf.getPropertyDtlsId() != BigDecimal.ZERO) {
                pst = con.prepareStatement("UPDATE PROPERTY_DETAILS SET PROPERTY_LOCATION=?,PROPERTY_AREA=?,PROPERTY_NATURE=?,INTEREST1=?,VALUE=?,OWNER_TYPE_ID=?,DATE_OF_ACQUISITION=?,REMARKS=?,PROPERTY_ID=?,MANNER=?,DESC_OF_OTH_ITEM=?,AREAUNIT=?,OTHER_PROPERTY_OWNER=? WHERE PROPERTY_DETAILS_ID=?");
                pst.setString(1, psf.getPropertyLocation());
                pst.setBigDecimal(2, psf.getPropertyArea());
                pst.setString(3, psf.getPropertyNature());
                pst.setString(4, psf.getInterest());
                pst.setBigDecimal(5, psf.getPropertyValue());
                pst.setInt(6, psf.getPropertyOwner());
                if (psf.getDateOfAcq() == null) {
                    pst.setDate(7, null);
                } else {
                    pst.setDate(7, new java.sql.Date(psf.getDateOfAcq().getTime()));
                }
                pst.setString(8, psf.getRemark());
                pst.setInt(9, psf.getPropertyTypeId());
                pst.setString(10, psf.getManner());
                pst.setString(11, psf.getDescofothitem());
                pst.setString(12, psf.getAreaunit());
                pst.setString(13, psf.getOtherPropertyOwner());
                pst.setBigDecimal(14, psf.getPropertyDtlsId());
                pst.executeUpdate();
            } else {
                mcode = CommonFunctions.getMaxCodeInteger("PROPERTY_DETAILS", "PROPERTY_DETAILS_ID", con);
                pst = con.prepareStatement("INSERT INTO PROPERTY_DETAILS(PROPERTY_DETAILS_ID,PROPERTY_LOCATION,PROPERTY_AREA,PROPERTY_NATURE,INTEREST1,VALUE,OWNER_TYPE_ID,DATE_OF_ACQUISITION,REMARKS,PROPERTY_ID,YEARLY_PROPERTY_ID,MANNER,DESC_OF_OTH_ITEM,AREAUNIT,OTHER_PROPERTY_OWNER) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                pst.setInt(1, mcode);
                pst.setString(2, psf.getPropertyLocation());
                pst.setBigDecimal(3, psf.getPropertyArea());
                pst.setString(4, psf.getPropertyNature());
                pst.setString(5, psf.getInterest());
                pst.setBigDecimal(6, psf.getPropertyValue());
                pst.setInt(7, psf.getPropertyOwner() + 0);
                if (psf.getDateOfAcq() == null) {
                    pst.setDate(8, null);
                } else {
                    pst.setDate(8, new java.sql.Date(psf.getDateOfAcq().getTime()));
                }
                pst.setString(9, psf.getRemark());
                pst.setInt(10, psf.getPropertyTypeId());
                pst.setBigDecimal(11, psf.getYearlyPropId());
                pst.setString(12, psf.getManner());
                pst.setString(13, psf.getDescofothitem());
                pst.setString(14, psf.getAreaunit());
                pst.setString(15, psf.getOtherPropertyOwner());
                pst.executeUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return mcode;
    }

    @Override
    public int saveMovableProperty(PropertyDetail psf) {
        Connection con = null;
        PreparedStatement pst = null;
        int mcode = 0;
        try {
            con = this.dataSource.getConnection();
          //  System.out.println("Descofothitem is: "+psf.getDescofothitem());
            if (psf.getPropertyDtlsId() != null && psf.getPropertyDtlsId() != BigDecimal.ZERO) {
                pst = con.prepareStatement("UPDATE PROPERTY_DETAILS SET VALUE=?,OWNER_TYPE_ID=?,DATE_OF_ACQUISITION=?,REMARKS=?,PROPERTY_ID=?,LOAN=?,MANNER=?,DESC_OF_OTH_ITEM=?,OTHER_PROPERTY_OWNER=? WHERE PROPERTY_DETAILS_ID=?");
                pst.setBigDecimal(1, psf.getPropertyValue());
                pst.setInt(2, psf.getPropertyOwner());
                if (psf.getDateOfAcq() == null) {
                    pst.setDate(3, null);
                } else {
                    pst.setDate(3, new java.sql.Date(psf.getDateOfAcq().getTime()));
                }
                pst.setString(4, psf.getRemark());
                pst.setInt(5, psf.getPropertyTypeId());
                pst.setBigDecimal(6, psf.getLoan());
                pst.setString(7, psf.getManner());
                pst.setString(8, psf.getDescofothitem());
                pst.setString(9, psf.getOtherPropertyOwner());
                pst.setBigDecimal(10, psf.getPropertyDtlsId());
                pst.executeUpdate();
            } else {
                mcode = CommonFunctions.getMaxCodeInteger("PROPERTY_DETAILS", "PROPERTY_DETAILS_ID", con);
                pst = con.prepareStatement("INSERT INTO PROPERTY_DETAILS(PROPERTY_DETAILS_ID,VALUE,OWNER_TYPE_ID,DATE_OF_ACQUISITION,REMARKS,PROPERTY_ID,YEARLY_PROPERTY_ID,LOAN,MANNER,DESC_OF_OTH_ITEM,OTHER_PROPERTY_OWNER) values(?,?,?,?,?,?,?,?,?,?,?)");
                pst.setInt(1, mcode);
                pst.setBigDecimal(2, psf.getPropertyValue());
                pst.setInt(3, psf.getPropertyOwner());
                if (psf.getDateOfAcq() == null) {
                    pst.setDate(4, null);
                } else {
                    pst.setDate(4, new java.sql.Date(psf.getDateOfAcq().getTime()));
                }
                pst.setString(5, psf.getRemark());
                pst.setInt(6, psf.getPropertyTypeId());
                pst.setBigDecimal(7, psf.getYearlyPropId());
                pst.setBigDecimal(8, psf.getLoan());
                pst.setString(9, psf.getManner());
                pst.setString(10, psf.getDescofothitem());
                pst.setString(11, psf.getOtherPropertyOwner());
                pst.executeUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pst);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return mcode;
    }

    @Override
    public PropertyDetail getImmovableProperty(BigDecimal propertyDtlsId) {
        PropertyDetail propertyDetail = new PropertyDetail();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet resultset = null;
        try {
            con = this.dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT * FROM property_details WHERE PROPERTY_DETAILS_ID=?");
            pstmt.setBigDecimal(1, propertyDtlsId);
            resultset = pstmt.executeQuery();
            if (resultset.next()) {
                propertyDetail.setPropertyDtlsId(resultset.getBigDecimal("PROPERTY_DETAILS_ID"));
                propertyDetail.setPropertyLocation(resultset.getString("PROPERTY_LOCATION"));
                propertyDetail.setPropertyArea(resultset.getBigDecimal("PROPERTY_AREA"));
                propertyDetail.setPropertyNature(resultset.getString("PROPERTY_NATURE"));
                propertyDetail.setInterest(resultset.getString("INTEREST1"));
                propertyDetail.setPropertyValue(resultset.getBigDecimal("VALUE"));
                propertyDetail.setPropertyOwner(resultset.getInt("OWNER_TYPE_ID"));
                propertyDetail.setDateOfAcq(resultset.getDate("DATE_OF_ACQUISITION"));
                propertyDetail.setRemark(resultset.getString("REMARKS"));
                propertyDetail.setPropertyTypeId(resultset.getInt("PROPERTY_ID"));
                propertyDetail.setManner(resultset.getString("MANNER"));
                propertyDetail.setDescofothitem(resultset.getString("DESC_OF_OTH_ITEM"));
                propertyDetail.setAreaunit(resultset.getString("AREAUNIT"));
                propertyDetail.setOtherPropertyOwner(resultset.getString("OTHER_PROPERTY_OWNER"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return propertyDetail;
    }

    @Override
    public PropertyDetail getMovableProperty(BigDecimal propertyDtlsId) {
        PropertyDetail propertyDetail = new PropertyDetail();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet resultset = null;
        try {
            con = this.dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT * FROM property_details WHERE PROPERTY_DETAILS_ID=?");
            pstmt.setBigDecimal(1, propertyDtlsId);
            resultset = pstmt.executeQuery();
            if (resultset.next()) {
                propertyDetail.setPropertyDtlsId(resultset.getBigDecimal("PROPERTY_DETAILS_ID"));
                propertyDetail.setPropertyTypeId(resultset.getInt("PROPERTY_ID"));
                propertyDetail.setPropertyValue(resultset.getBigDecimal("VALUE"));
                propertyDetail.setLoan(resultset.getBigDecimal("LOAN"));
                propertyDetail.setPropertyOwner(resultset.getInt("OWNER_TYPE_ID"));
                propertyDetail.setDateOfAcq(resultset.getDate("DATE_OF_ACQUISITION"));
                propertyDetail.setRemark(resultset.getString("REMARKS"));
                propertyDetail.setManner(resultset.getString("MANNER"));
                propertyDetail.setDescofothitem(resultset.getString("DESC_OF_OTH_ITEM"));
                propertyDetail.setOtherPropertyOwner(resultset.getString("OTHER_PROPERTY_OWNER"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return propertyDetail;
    }

    @Override
    public boolean deleteImmovableProperty(BigDecimal propertyDtlsId, String empId) {
        boolean isDeleted = true;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet resultset = null;
        String tempEmpId = "";
        try {
            con = this.dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT EMP_ID FROM PROPERTY_DETAILS  INNER JOIN PROPERTY_STATEMENT_LIST on PROPERTY_DETAILS.YEARLY_PROPERTY_ID = PROPERTY_STATEMENT_LIST.YEARLY_PROPERTY_ID WHERE PROPERTY_DETAILS_ID=?");
            pstmt.setBigDecimal(1, propertyDtlsId);
            resultset = pstmt.executeQuery();
            if (resultset.next()) {
                tempEmpId = resultset.getString("EMP_ID");
            }
            if (empId.equalsIgnoreCase(tempEmpId)) {
                pstmt = con.prepareStatement("DELETE FROM property_details WHERE PROPERTY_DETAILS_ID=?");
                pstmt.setBigDecimal(1, propertyDtlsId);
                isDeleted = pstmt.execute();
            }
        } catch (SQLException e) {
            isDeleted = false;
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return isDeleted;
    }

    @Override
    public boolean deleteMovableProperty(BigDecimal propertyDtlsId, String empId) {
        boolean isDeleted = true;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet resultset = null;
        String tempEmpId = "";
        try {
            con = this.dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT EMP_ID FROM PROPERTY_DETAILS  INNER JOIN PROPERTY_STATEMENT_LIST on PROPERTY_DETAILS.YEARLY_PROPERTY_ID = PROPERTY_STATEMENT_LIST.YEARLY_PROPERTY_ID WHERE PROPERTY_DETAILS_ID=?");
            pstmt.setBigDecimal(1, propertyDtlsId);
            resultset = pstmt.executeQuery();
            if (resultset.next()) {
                tempEmpId = resultset.getString("EMP_ID");
            }
            if (empId.equalsIgnoreCase(tempEmpId)) {
                pstmt = con.prepareStatement("DELETE FROM property_details WHERE PROPERTY_DETAILS_ID=?");
                pstmt.setBigDecimal(1, propertyDtlsId);
                isDeleted = pstmt.execute();
            }
        } catch (SQLException e) {
            isDeleted = false;
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return isDeleted;
    }

    @Override
    public boolean submitPropertyStatement(BigDecimal yearlyPropId, String empId) {
        boolean isSubmitted = true;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet resultset = null;
        String tempEmpId = "";
        try {
            con = this.dataSource.getConnection();
            pstmt = con.prepareStatement("SELECT EMP_ID FROM PROPERTY_STATEMENT_LIST WHERE YEARLY_PROPERTY_ID=?");
            pstmt.setBigDecimal(1, yearlyPropId);
            resultset = pstmt.executeQuery();
            if (resultset.next()) {
                tempEmpId = resultset.getString("EMP_ID");
            }
            if (empId.equalsIgnoreCase(tempEmpId)) {
                pstmt = con.prepareStatement("UPDATE PROPERTY_STATEMENT_LIST SET STATUS_ID=1 , SUBMITTED_ON=? WHERE YEARLY_PROPERTY_ID=?");
                pstmt.setDate(1, new java.sql.Date(new Date().getTime()));
                pstmt.setBigDecimal(2, yearlyPropId);
                isSubmitted = pstmt.execute();
            }
        } catch (SQLException e) {
            isSubmitted = false;
            e.printStackTrace();
        } finally {
            DataBaseFunctions.closeSqlObjects(pstmt);
            DataBaseFunctions.closeSqlObjects(con);
        }
        return isSubmitted;
    }
}
