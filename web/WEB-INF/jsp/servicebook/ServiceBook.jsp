<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<% String url = "";
    String myempId = "";
    String attachId = "";
    String contextPath = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + contextPath + "/";
    int i = 0;
%>
<%!int mypage = 0;
    boolean payrecordprinted = false;
    int numofrowsleft = 0;
%>
<html>
    <head>
        <base href="<%=basePath%>"></base>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Service Book</title>
        <link rel="stylesheet" type="text/css" href="css/PrintDetail.css">
        <script type="text/javascript">
            function callNoImage() {
                var userPhoto = document.getElementById('sbUserPhoto');
                userPhoto.src = "images/NoEmployee.png";

            }
        </script>
    </head>
    <body>
        <form:form action="employeeProfile.htm" commandName="employeeProfile"  method="GET" target="_blank">

            <div align="center" style="overflow-x:hidden">

                <div  style="width: 100%">

                    <div>
                        <CENTER>
                            <table width="1040" height="50" border="0" cellpadding="0" cellspacing="0">
                                <tr>
                                    <td width="940" >&nbsp;</td>
                                </tr>
                            </table>
                            <table width="1040" border="1" cellpadding="0" cellspacing="0" bordercolor="#000099">

                                <tr>
                                    <td width="1040" height="1600">
                                        <table width="1040" height="1013" border="0" cellpadding="0" cellspacing="0">

                                            <tr>
                                                <td colspan="2" height="70" align="center" valign="middle">&nbsp;</td>
                                            </tr>
                                            <tr>
                                                <td  colspan="2" height="154" align="center" valign="middle">&nbsp;<img src='<%=basePath%>/images/odgovt.gif' height="144" width="165"/></td>
                                            </tr>
                                            <tr>
                                                <td colspan="2" height="67" align="center" valign="bottom" style="font-family:Arial, Helvetica, sans-serif;font-size:40px;color:#000099">
                                                    <strong>HUMAN RESOURCES MANAGEMENT SYSTEM</strong>    	</td>
                                            </tr>
                                            <tr>
                                                <td  height="51" colspan="2" align="center" valign="top" style="font-family:Arial, Helvetica, sans-serif;font-size:40px;color:#000099"><strong>Government of Odisha</strong></td>
                                            </tr>

                                            <tr> 
                                                <td  colspan="2" height="30" align="center">
                                                    &nbsp;
                                                </td>
                                            </tr>

                                            <tr>
                                                <td height="180" colspan="2" align="center" style="font-family:Arial, Helvetica, sans-serif;font-size:55px;float:right;font-style:normal;color:#000099">&nbsp;</td>
                                            </tr>
                                            <tr>
                                                <td  colspan="2" height="30" align="center">
                                                    &nbsp;
                                                </td>
                                            </tr>
                                            <tr>
                                                <td  colspan="2" height="30" align="center">
                                                    &nbsp;
                                                </td>
                                            </tr>
                                            <tr>
                                                <td  colspan="2" height="30" align="center">
                                                    &nbsp;
                                                </td>
                                            </tr>
                                            <tr>
                                                <td height="40" colspan="2" align="center" style="font-family:Arial, Helvetica, sans-serif;font-size:55px;font-style:normal;color:#000099"><strong>SERVICE BOOK </strong></td>
                                            </tr>
                                            <tr>
                                                <td colspan="2" height="40" align="center" style="font-family:Arial, Helvetica, sans-serif;font-size:55px;color:#000099"><strong>OF</strong></td>
                                            </tr>
                                            <tr>
                                                <td colspan="2" height="150" align="center">
                                                    <img src="displayemployeeprofilephoto.htm" id="sbUserPhoto" onerror="callNoImage()" height="150px" width="140px" border="2"/>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td colspan="2" height="40" align="center" style="font-family:Arial, Helvetica, sans-serif;font-size:45px;font-style:normal;color:#000099">
                                                    <strong><c:out value="${employeeProfile.empName}"/></strong>
                                                </td>	
                                            </tr>
                                            <tr>
                                                <td colspan="2" height="30" align="center" style="font-family:Arial, Helvetica, sans-serif;font-size:25px;font-style:normal;color:#000099">
                                                    <strong>
                                                        <c:out value="${employeeProfile.empCadre}"/>&nbsp;
                                                        <c:out value="${employeeProfile.empAllotmentYear}"/>&nbsp;
                                                    </strong>

                                                </td>
                                            </tr>
                                            <c:if test="${not empty cadreId}">
                                                <tr>
                                                    <td width="515" height="30" align="right" style="font-family:Arial, Helvetica, sans-serif;font-size:25px;font-style:normal;color:#000099">
                                                        <strong>
                                                            CADRE ID :
                                                        </strong>&nbsp;
                                                    </td>
                                                    <td width="515" height="30" align="left" style="font-family:Arial, Helvetica, sans-serif;font-size:25px;font-style:normal;color:#000099">
                                                        &nbsp;<c:out value="${employeeProfile.cadreId}"/>
                                                    </td>
                                                </tr>
                                            </c:if>
                                            <tr height="35px">
                                                <td width="515" height="31" align="right" style="font-family:Arial, Helvetica, sans-serif;font-size:25px;font-style:normal;color:#000099">

                                                    <strong>
                                                        HRMS ID :
                                                    </strong>

                                                </td>
                                                <td width="515" height="31" align="left" style="font-family:Arial, Helvetica, sans-serif;font-size:25px;font-style:normal;color:#000099">

                                                    &nbsp;<c:out value="${employeeProfile.empid}"/>
                                                    &nbsp;
                                                </td>
                                            </tr>

                                            <tr height="35px">
                                                <td width="515" height="31" align="right" style="font-family:Arial, Helvetica, sans-serif;font-size:25px;font-style:normal;color:#000099">

                                                    <strong>
                                                        GPF NO :
                                                    </strong>

                                                </td>
                                                <td width="515" height="31" align="left" style="font-family:Arial, Helvetica, sans-serif;font-size:25px;font-style:normal;color:#000099">

                                                    &nbsp;<c:out value="${employeeProfile.gpfno}"/>
                                                    &nbsp;
                                                </td>
                                            </tr>
                                            <tr height="35px">
                                                <td width="515" height="31" align="right" style="font-family:Arial, Helvetica, sans-serif;font-size:25px;font-style:normal;color:#000099">
                                                    <c:if test="${not empty gistype}">
                                                        <strong>
                                                            <c:out value="${employeeProfile.gistype}"/> NO. :
                                                        </strong>
                                                    </c:if>&nbsp;	
                                                </td>
                                                <td width="515" height="31" align="left" style="font-family:Arial, Helvetica, sans-serif;font-size:25px;font-style:normal;color:#000099">
                                                    <c:if test="${not empty gis}">   
                                                        &nbsp; <c:out value="${employeeProfile.gis}"/>
                                                    </c:if>&nbsp;
                                                </td>
                                            </tr>
                                            <c:if test="${empty empPhotoPath}">
                                                <tr>
                                                    <td colspan="2" height="100" align="center">
                                                        &nbsp;
                                                    </td>
                                                </tr>
                                            </c:if>
                                            <c:if test="${empty empCadre}">

                                                <tr>
                                                    <td colspan="2" height="50" align="center" style="font-family:Arial, Helvetica, sans-serif;font-size:25px;font-style:normal;color:#000099">
                                                        &nbsp;
                                                    </td>
                                                </tr>
                                            </c:if>
                                            <c:if test="${empty cadreId}">

                                                <tr>
                                                    <td colspan="2" height="30" align="center" style="font-family:Arial, Helvetica, sans-serif;font-size:25px;font-style:normal;color:#000099">
                                                        &nbsp;
                                                    </td>
                                                </tr>
                                            </c:if>

                                            <tr>
                                                <td  colspan="2" height="30" align="center">
                                                    &nbsp;
                                                </td>
                                            </tr>
                                            <tr>
                                                <td  colspan="2" height="30" align="center">
                                                    &nbsp;
                                                </td>
                                            </tr>
                                            <tr>
                                                <td  colspan="2" height="30" align="center">
                                                    &nbsp;
                                                </td>
                                            </tr>
                                            <tr>
                                                <td  colspan="2" height="30" align="center">
                                                    &nbsp;
                                                </td>
                                            </tr>
                                            <tr>
                                                <td  colspan="2" height="30" align="center">
                                                    &nbsp;
                                                </td>
                                            </tr>
                                            <tr>
                                                <td  colspan="2" height="30" align="center">
                                                    &nbsp;
                                                </td>
                                            </tr>
                                            <tr>
                                                <td  colspan="2" height="30" align="center">
                                                    &nbsp;
                                                </td>
                                            </tr>
                                            <tr>
                                                <td  colspan="2" height="30" align="center">
                                                    &nbsp;
                                                </td>
                                            </tr>
                                            <tr>
                                                <td  colspan="2" height="30" align="center">
                                                    &nbsp;
                                                </td>
                                            </tr>
                                            <tr>
                                                <td  colspan="2" height="30" align="center">
                                                    &nbsp;
                                                </td>
                                            </tr>
                                            <tr>
                                                <td  colspan="2" height="30" align="center">
                                                    &nbsp;
                                                </td>
                                            </tr>
                                            <tr>
                                                <td  colspan="2" height="25" align="center">
                                                    &nbsp;
                                                </td>
                                            </tr>
                                            <tr>
                                                <td  colspan="2" height="90" align="center" style="font-family:Arial, Helvetica, sans-serif;font-size:30px;font-style:italic;color:#000099">
                                                    <img src='<%=basePath%>/images/file.JPG' height="80" width="90"/>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td colspan="2" height="25" align="center" style="font-family:Arial, Helvetica, sans-serif;font-size:15px;color:#000099">Developed By</td>
                                            </tr>
                                            <tr>
                                                <td colspan="2" height="25"  align="center" style="font-family:Arial, Helvetica, sans-serif;font-size:15px;color:#000099"> Centre for Modernizing Government Initiative(CMGI)</td>
                                            </tr>
                                            <tr>
                                                <td  colspan="2" height="25" align="center" style="font-family:Arial, Helvetica, sans-serif;font-size:8px;color:#000099;text-transform: uppercase;">
                                                    DATE OF PRINTING: <c:out value="${employeeProfile.serverDate}"/>
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>
                            </table>
                            <input type="button" name="pagebreak1" style="page-break-before: always;width: 0;height: 0"/>
                            <table width="1040"  style="left: 18px;">
                                <tr>
                                    <td class="printData" style="text-align:left">
                                        GOVERNMENT OF ODISHA
                                    </td>
                                    <td class="printFooter" style="text-align:center;text-transform: uppercase">&nbsp;

                                    </td>
                                    <td colspan="2" class="printData" style="text-align:right;">	
                                        SERVICE BOOK OF <c:out value="${employeeProfile.empName}"/>
                                        <c:if test="${not empty gpfno}">
                                            <c:if test="${not empty empCadre}">
                                                (<c:out value="${employeeProfile.gpfno}"/>),
                                            </c:if>
                                            <c:if test="${not empty empCadre}">
                                                (<c:out value="${employeeProfile.gpfno}"/>)
                                            </c:if>
                                        </c:if>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="4">
                                        <hr/>
                                    </td>
                                </tr>
                            </table>
                            <table border="1" cellpadding="0" cellspacing="0" width="1040" height="1600" style="left: 18px;">
                                <tr>
                                    <td colspan="3" valign="middle" align="center">

                                        <img border="2" id="imgid" src='<%=basePath%>/images/SB1stPage.png' width="750px" height="1200px"/>



                                    </td>
                                </tr>        
                            </table>
                            <%mypage = mypage + 1;%>
                            <table width="1040" style="left: 18px;">
                                <tr>
                                    <td colspan="4" height="5px">
                                        <hr/>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="printData" >&nbsp;

                                    </td>
                                    <td class="printFooter" style="text-align:center">&nbsp;</td>
                                    <td class="printData" style="text-align:right; text-transform: uppercase;">Page:<%=mypage%></td>
                                </tr>
                            </table>   
                            <input type="button" name="pagebreak1" style="page-break-before: always;width: 0;height: 0"/>
                            <table width="1040"  style="left: 18px;">
                                <tr>
                                    <td class="printData" style="text-align:left">
                                        GOVERNMENT OF ODISHA
                                    </td>
                                    <td class="printFooter" style="text-align:center;text-transform: uppercase">&nbsp;

                                    </td>
                                    <td colspan="2" class="printData" style="text-align:right;">	
                                        SERVICE BOOK OF <c:out value="${employeeProfile.empName}"/>
                                        <c:if test="${not empty gpfno}">
                                            <c:if test="${not empty empCadre}">
                                                (<c:out value="${employeeProfile.gpfno}"/>),
                                            </c:if>
                                            <c:if test="${not empty empCadre}">
                                                (<c:out value="${employeeProfile.gpfno}"/>)
                                            </c:if>
                                        </c:if>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="4">
                                        <hr/>
                                    </td>
                                </tr>
                            </table>
                            <table width="1040" height="100" border="1" cellpadding="0" cellspacing="0">
                                <tr>
                                    <td class="printLabel" style="text-align:left;text-transform:uppercase;font-size:18px">NAME: </td>
                                    <td colspan="3" class="printData" style="text-align:left;text-transform:uppercase;font-size:18px">&nbsp;<c:out value="${employeeProfile.empName}"/></td>
                                </tr>
                                <tr>
                                    <td width="250" class="printLabel" style="text-align:left;text-transform:uppercase;font-size:18px">
                                        <c:if test="${employeeProfile.ifpPan == 'GPF'}">

                                            GPF NO. :&nbsp;
                                        </c:if>
                                        <c:if test="${employeeProfile.ifpPan == 'Y'}">

                                            PPAN NO. :&nbsp;
                                        </c:if>	
                                    </td>
                                    <td width="250" class="printData" style="text-align:left;text-transform:uppercase;font-size:18px">&nbsp;<c:out value="${employeeProfile.gpfno}"/></td>
                                    <td width="250" class="printLabel" style="text-align:left; text-transform:uppercase;font-size:18px">HRMS ID:</td>
                                    <td width="250" class="printData" style="text-align:left;text-transform:uppercase;font-size:18px">&nbsp;<c:out value="${employeeProfile.empid}"/></td>
                                </tr>
                            </table>
                            <table width="1040" height="150" border="1" cellpadding="0" cellspacing="0">
                                <tr>
                                    <td width="250" class="printLabel" style="text-align:left;text-transform:uppercase;font-size:18px">Present Address:</br>(Residence)</td>
                                    <td width="750" class="printData" style="text-align:left;text-transform:uppercase;font-size:18px">&nbsp;

                                        <c:if test="${not empty employeeProfile.residenceAdd}">
                                            <c:out value="${employeeProfile.residenceAdd}"/>
                                        </c:if>	
                                    </td>
                                </tr>
                            </table>
                            <table width="1040" height="100" border="1" cellpadding="0" cellspacing="0">
                                <tr>
                                    <td width="340" class="printLabel" style="text-align:left;text-transform:uppercase;font-size:18px">DATE OF BIRTH <br>BY CHRISTIAN ERA AS <br>NEARLY AS CAN <br>BE ASCERTAINED : </td>
                                    <td width="700" class="printData" style="text-align:left;text-transform:uppercase;font-size:18px">&nbsp;

                                        <c:if test="${not empty employeeProfile.dob}">
                                            <c:out value="${employeeProfile.dob}"/>
                                        </c:if>
                                        <c:if test="${not empty employeeProfile.dobText}">
                                            (<c:out value="${employeeProfile.dobText}"/>)
                                        </c:if>

                                    </td>
                                </tr>
                            </table>
                            <table width="1040" height="50" border="1" cellpadding="0" cellspacing="0">
                                <tr class="alternateTD">
                                    <td  class="printLabel" style="text-align:left;text-transform:uppercase;font-size:20px">Employee Educational Details: </td>
                                </tr>
                            </table>
                            <c:forEach items="${educations}" var="education">
                                <table width="1040" height="50" border="1" cellpadding="0" cellspacing="0">
                                    <thead> </thead>
                                    <tr>
                                        <td class="printLabel" style="text-align:center;text-transform:uppercase;font-size:20px ">&nbsp;
                                            ${education.qualification}		
                                        </td>	
                                    </tr>
                                </table>


                                <table width="1040" height="50" border="1" cellpadding="0" cellspacing="0">
                                    <thead> </thead>
                                    <tr>
                                        <td width="300" class="printLabel" style="text-align:left;text-transform:uppercase;font-size:18px ">Year of Passing</td>
                                        <td width="100" class="printData" style="text-align:left;text-transform:uppercase;font-size:18px">
                                            &nbsp;
                                            <c:if test="${not empty education.yearofpass}">
                                                ${education.yearofpass}
                                            </c:if>	
                                        </td>
                                        <td width="160" class="printLabel" style="text-align:left;text-transform:uppercase;font-size:18px">Faculty</td>
                                        <td width="170" class="printData" style="text-align:left;text-transform:uppercase;font-size:18px">&nbsp;
                                            ${education.faculty}

                                        </td>
                                        <td width="200" class="printLabel" style="text-align:left;text-transform:uppercase;font-size:18px">Degree/<br>Certificate</td>
                                        <td width="100" class="printData" style="text-align:left;text-transform:uppercase;font-size:18px">&nbsp;
                                            ${education.degree}
                                        </td>
                                    </tr>
                                </table>

                                <table width="1040" height="50" border="1" cellpadding="0" cellspacing="0">	
                                    <thead> </thead>
                                    <tr>
                                        <td width="300" class="printLabel"  style="text-align:left;text-transform:uppercase;font-size:18px">Subject</td>
                                        <td width="740" class="printData"  style="text-align:left;text-transform:uppercase;font-size:18px">&nbsp;
                                            <c:if test="${not empty education.subject}">
                                                ${education.subject}
                                            </c:if>	
                                        </td>
                                    </tr>	 
                                </table>

                                <table width="1040" height="50" border="1" cellpadding="0" cellspacing="0">
                                    <thead> </thead>
                                    <tr>
                                        <td width="300" class="printLabel"  style="text-align:left;text-transform:uppercase;font-size:18px">Board/University</td>
                                        <td width="740" class="printData"  style="text-align:left;text-transform:uppercase;font-size:18px">&nbsp;
                                            ${education.board}
                                        </td>
                                    </tr>	
                                </table>
                            </c:forEach>
                            <table width="1040" height="50" border="1" cellpadding="0" cellspacing="0">
                                <thead> </thead>
                                <tr>
                                    <td width="300" class="printLabel" style="text-align:left;font-size:18px">HEIGHT(in cm):</td>
                                    <td width="740" class="printData" style="text-align:left;text-transform:uppercase;font-size:18px">&nbsp;
                                        <c:if test="${employeeProfile.height gt 0}">
                                            ${employeeProfile.height}
                                        </c:if>
                                    </td>
                                </tr>
                            </table>
                            <table width="1040" height="100" border="1" cellpadding="0" cellspacing="0">
                                <thead> </thead>
                                <tr>
                                    <td width="300" class="printLabel" style="text-align:left;text-transform:uppercase;font-size:18px">Personal <br>Identification Mark : </td>
                                    <td width="740" class="printData" style="text-align:left;text-transform:uppercase;font-size:18px">&nbsp;
                                        ${employeeProfile.idmark}
                                    </td>
                                </tr>
                            </table> 

                            <table width="1040" height="130" border="1" cellpadding="0" cellspacing="0">
                                <thead> </thead>
                                <tr>
                                    <td width="300" class="printLabel" style="text-align:left;text-transform:uppercase;font-size:18px">Father's name <br>and residence : </td>
                                    <td width="740" class="printData" style="text-align:left;text-transform:uppercase;font-size:18px">
                                        <c:forEach items="${familyRel}" var="familyRelation">
                                            <b>${familyRelation.fatherName}</b><br><br>
                                        </c:forEach>
                                        <c:forEach items="${address}" var="premanentAddr">
                                            ${premanentAddr.address}
                                        </c:forEach>&nbsp;
                                    </td>
                                </tr>
                            </table>

                            <table width="1040" height="50" border="1" cellpadding="0" cellspacing="0">		
                                <thead> </thead>
                                <tr height="50" >
                                    <td width="300" class="printLabel" style="text-align:left; font-size:18px">DATE OF ENTRY IN <BR> GOVERMENT SERVICE :</td>
                                    <td width="740" class="printData" style="text-align:left;text-transform:uppercase;font-size:18px">&nbsp;

                                        ${employeeProfile.doeGov}
                                        <c:if test="${not empty employeeProfile.entryGovDateText}">
                                            (${employeeProfile.entryGovDateText})
                                        </c:if>
                                    </td>
                                </tr>
                            </table>
                            <table width="1040" height="80" border="1" cellpadding="0" cellspacing="0">		
                                <thead> </thead>
                                <tr height="80" >
                                    <td width="300" class="printLabel" style="text-align:left; font-size:18px">DATE OF ENTRY IN THE<BR>SERVICE FOR WHICH<BR>SERVICE BOOK CREATED:</td>
                                    <td width="740" class="printData" style="text-align:left;text-transform:uppercase;font-size:18px">&nbsp;
                                        ${employeeProfile.joindategoo}
                                        (${employeeProfile.joinDateText})
                                    </td>
                                </tr>
                            </table>
                            <table width="1040" height="50" border="1" cellpadding="0" cellspacing="0">		
                                <thead> </thead>
                                <tr height="50" >
                                    <td width="520" class="printLabel" style="text-align:left; font-size:18px">DECLARATION OF HOME TOWN:</td>
                                    <td width="520" class="printData" style="text-align:left;text-transform:uppercase;font-size:18px">&nbsp;
                                        ${employeeProfile.homeTown}
                                    </td>
                                </tr>
                            </table>
                            <input type="button" name="pagebreak1" style="page-break-before: always;width: 0;height: 0"/>
                            <table width="1040"  style="left: 18px;">
                                <tr>
                                    <td class="printData" style="text-align:left">
                                        GOVERNMENT OF ODISHA
                                    </td>
                                    <td class="printFooter" style="text-align:center;text-transform: uppercase">&nbsp;

                                    </td>
                                    <td colspan="2" class="printData" style="text-align:right;">	
                                        SERVICE BOOK OF <c:out value="${employeeProfile.empName}"/>
                                        <c:if test="${not empty gpfno}">
                                            <c:if test="${not empty empCadre}">
                                                (<c:out value="${employeeProfile.gpfno}"/>),
                                            </c:if>
                                            <c:if test="${not empty empCadre}">
                                                (<c:out value="${employeeProfile.gpfno}"/>)
                                            </c:if>
                                        </c:if>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="4">
                                        <hr/>
                                    </td>
                                </tr>
                            </table>
                            <table width="1040" height="50" border="1" cellpadding="0" cellspacing="0">
                                <thead> </thead>
                                <tr>
                                    <td width="500" class="printLabel" style="text-align:left;text-transform:uppercase;font-size:18px">DATE OF SUPERANNUATION: </td>
                                    <td width="500" class="printData" style="text-align:left;text-transform:uppercase;font-size:18px">&nbsp;${employeeProfile.dor}</td>
                                </tr>
                            </table>
                            <table width="1040" border="1" cellpadding="0" cellspacing="0">
                                <thead> </thead>
                                <tr height="50" >
                                    <td width="250" class="printLabel" style="text-align:left; text-transform:uppercase;font-size:18px">Category:</td>
                                    <td width="250" class="printData" style="text-align:left;text-transform:uppercase;font-size:18px">&nbsp;
                                        <c:if test="${not empty employeeProfile.category}">
                                            ${employeeProfile.category}
                                        </c:if>
                                    </td>
                                    <td width="250" class="printLabel" style="text-align:left;text-transform:uppercase;font-size:18px">Marital Status:</td>
                                    <td width="250" class="printData" style="text-align:left;text-transform:uppercase;font-size:18px">&nbsp;
                                        ${employeeProfile.maritalStatus}
                                    </td>
                                </tr>
                            </table>
                            <table width="1040" height="50" border="1" cellpadding="0" cellspacing="0">
                                <thead> </thead>
                                <tr>
                                    <td width="280" class="printLabel" style="text-align:left;text-transform:uppercase;font-size:18px">Cell Phone:</td>
                                    <td width="760" class="printData" style="text-align:left;text-transform:uppercase;font-size:18px">&nbsp; ${employeeProfile.mobile}</td>
                                </tr>
                            </table>
                            <table width="1040" height="50" border="1" cellpadding="0" cellspacing="0">
                                <thead> </thead>
                                <tr class="alternateTD">
                                    <td  class="printLabel" style="text-align:left;text-transform:uppercase;font-size:20px">Identity of the Employee:</td>
                                </tr>
                            </table>
                            <table width="1040" height="50" border="1" cellpadding="0" cellspacing="0">
                                <thead> </thead>
                                <tr>
                                    <td width="220" class="printLabel" style="text-align:center;text-transform:uppercase;font-size:18px">Type of Identification</td>
                                    <td width="240" class="printLabel" style="text-align:center;text-transform:uppercase;font-size:18px">No.</td>
                                    <td width="280" class="printLabel" style="text-align:center;text-transform:uppercase;font-size:18px">Place of Issue</td>
                                    <td width="150" class="printLabel" style="text-align:center;text-transform:uppercase;font-size:18px">Date of Issue</td>
                                    <td width="150" class="printLabel" style="text-align:center;text-transform:uppercase;font-size:18px">Date of Expiry</td>
                                </tr>
                                <c:forEach items="${identity}" var="identity">
                                    <tr>
                                        <td width="220" class="printData" style="text-align:center;text-transform:uppercase;font-size:18px">&nbsp;${identity.identityDesc}</td>
                                        <td width="240" class="printData" style="text-align:center;text-transform:uppercase;font-size:18px">&nbsp;${identity.identityNo}</td>
                                        <td width="280" class="printData" style="text-align:center;text-transform:uppercase;font-size:18px">&nbsp;${identity.placeOfIssue}</td>
                                        <td width="150" class="printData" style="text-align:center;text-transform:uppercase;font-size:18px">&nbsp;${identity.issueDate}</td>
                                        <td width="150" class="printData" style="text-align:center;text-transform:uppercase;font-size:18px">&nbsp;${identity.expiryDate}</td>
                                    </tr>
                                </c:forEach>
                            </table>
                            <table width="1040" height="50" border="1" cellpadding="0" cellspacing="0">
                                <thead> </thead>
                                <tr class="alternateTD">
                                    <td  class="printLabel" style="text-align:left;text-transform:uppercase;font-size:20px">Family of the Employee:</td>
                                </tr>
                            </table>
                            <table width="1040" height="50" border="1" cellpadding="0" cellspacing="0">
                                <thead> </thead>
                                <tr >
                                    <td width="250" class="printLabel" style="text-align:center;text-transform:uppercase;font-size:18px">Relation Type</td>
                                    <td width="350" class="printLabel" style="text-align:center;text-transform:uppercase;font-size:18px">Name</td>
                                    <td width="250" class="printLabel" style="text-align:center;text-transform:uppercase;font-size:18px">Is Alive?</td>
                                </tr>
                                <c:forEach items="${familyRel}" var="family">
                                    <tr>
                                        <td width="250" class="printData" style="text-align:center;text-transform:uppercase;font-size:18px">&nbsp;${family.relation}</td>
                                        <td width="350" class="printData" style="text-align:center;text-transform:uppercase;font-size:18px">&nbsp;${family.initials} ${family.fname} ${family.mname} ${family.lname}</td>
                                        <td width="250" class="printData" style="text-align:center;text-transform:uppercase;font-size:18px">&nbsp;${family.ifalive}</td>
                                    </tr>
                                </c:forEach>
                            </table>
                            <table width="1040" height="50" border="1" cellpadding="0" cellspacing="0">                                
                                <tr class="alternateTD">
                                    <td  class="printLabel" style="text-align:left;text-transform:uppercase;font-size:20px">Employee Reservation Category:</td>
                                </tr>
                            </table>   
                            <table width="1040" height="50" border="1" cellpadding="0" cellspacing="0">                                
                                <tr>
                                    <td width="740" class="printLabel" style="text-align:left;text-transform:uppercase;font-size:18px">Is Employeed under any Reservation Category?</td>
                                    <td width="300" class="printData" style="text-align:center;text-transform:uppercase;font-size:18px">&nbsp; ${employeeProfile.ifReservation}</td>
                                </tr>
                            </table> 
                            <table width="1040" height="50" border="1" cellpadding="0" cellspacing="0">                                
                                <tr>
                                    <td width="740" class="printLabel" style="text-align:left;text-transform:uppercase;font-size:18px">Reservation Category Under Which Employed </td>
                                    <td width="300" class="printData" style="text-align:center;text-transform:uppercase;font-size:18px">&nbsp;
                                        <c:if test="${not empty employeeProfile.category}">
                                            ${employeeProfile.category}
                                        </c:if>	
                                    </td>	
                                </tr>
                            </table>
                            <table width="1040" height="50" border="1" cellpadding="0" cellspacing="0">                                
                                <tr>
                                    <td width="740" class="printLabel" style="text-align:left;text-transform:uppercase;font-size:18px">Is Employeed under Rehabilation Assistance Scheme?</td>
                                    <td width="300" class="printData" style="text-align:center;text-transform:uppercase;font-size:18px">&nbsp; ${employeeProfile.ifRehabiltation}</td>
                                </tr>
                            </table>
                            <%
                                mypage++;
                            %>
                            <table width="1040"   style="left: 18px;">
                                <thead> </thead>
                                <tr>
                                    <td colspan="4" height="5px">
                                        <hr/>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="printData" >
                                        &nbsp;
                                    </td>
                                    <td class="printFooter" style="text-align:center">&nbsp;</td>
                                    <td class="printData" style="text-align:right; text-transform: uppercase;">Page:<%=mypage%></td>
                                </tr>
                            </table>
                        </center>
                    </div>                   
                    <div style="page-break-before: always;">
                        <table width="1040" cellspacing="0" cellpadding="0" border="1">
                            <thead>
                                <tr class="alternateTD">
                                    <th width="300" style="text-align:center;font-size:16px;" class="printLabelHeader">Post/Cadre/Scale of Pay</th>
                                    <th width="120" style="text-align:center;font-size:16px;" class="printLabelHeader">Pay</th>
                                    <th width="90" style="text-align:center;font-size:16px;" class="printLabelHeader">WEF</th>
                                    <th width="450" style="text-align:center;font-size:16px;border-right:1px solid #666666;" colspan="2" class="printLabelHeader">Entry in the Service Book </th>
                                </tr>
                            </thead>
                            <c:forEach items="${esb.empsbrecord}" var="servicehistory" varStatus="cnt">
                                <tr height="355px">
                                    <td width="300" height="355" align="left" style="font-size:18px;padding-left:5px;" class="printDataInner">
                                        <div style="font-family:Arial,Helvetica,sans-serif;font-size:10px;">${cnt.index + 1}</div>
                                        <div style="height:340px;display: table;">
                                            <span style="vertical-align:middle;display: table-cell;">

                                            </span>
                                        </div>		
                                    </td>
                                    <td width="120" height="355" align="left" style="font-size:18px;" class="printDataInner">

                                    </td>
                                    <td width="90" height="355" align="left" style="font-size:18px;" class="printDataInner">                                    
                                        ${servicehistory.wefChange}
                                    </td>
                                    <td width="450" align="left" style="font-size:18px;text-transform: none;border-right:1px solid #666666;" id="serviceHistory2" class="printDataInner">
                                        <div style="word-wrap: break-word;width: 450px;padding: 5px;">
                                            <b><u> ${servicehistory.category}</u></b>  <br/>
                                                    ${servicehistory.sbdescription}
                                            &nbsp;	

                                            <c:if test="${not empty servicehistory.moduleNote}">
                                                <br/>
                                                <p style="font-style: italic;">
                                                    <b>Note:</b> ${servicehistory.moduleNote}
                                                </p>

                                            </c:if>
                                        </div>
                                    </td>
                                </tr>
                                <tr height="10px">
                                    <td align="right" style="font-size:10px;text-transform: uppercase;" colspan="4">
                                        &nbsp;ENTRY TAKEN BY: ${servicehistory.entauth} ON ${servicehistory.doe}
                                    </td>
                                </tr>
                            </c:forEach>
                        </table>
                    </div>
                </div>
            </div>
        </form:form>
    </body>
</html>
