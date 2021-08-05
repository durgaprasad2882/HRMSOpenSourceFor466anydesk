<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%
    int slno = 0;
    String fiscalyear = "";
    String atchid = "";
    String downloadlink = "";
    String pdflink = "";
    String revertlink = "";
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Human Resources Management System, Government of Odisha</title>
        
        <link href="resources/css/colorbox.css" rel="stylesheet">
        <link rel="stylesheet" type="text/css" href="resources/css/themes/default/easyui.css"/>
        <link rel="stylesheet" type="text/css" href="resources/css/themes/icon.css"/>

        <script type="text/javascript" src="js/jquery.min-1.9.1.js"></script>
        <script type="text/javascript" src="js/jquery.colorbox-min.js"></script>
        <script type="text/javascript" src="js/jquery.easyui.min.js"></script>
        <script type="text/javascript" src="js/basicjavascript.js"></script>
        <script type="text/javascript">
            $(document).ready(function(){
                if($('#hidsltGrading').val() > 0){
                    $('#sltGrading').combobox('setValue', $('#hidsltGrading').val());
                }
                if($('#hidsltReviewGrading').val() > 0){
                    $('#sltReviewGrading').combobox('setValue', $('#hidsltReviewGrading').val());
                }
            })
            function onlyIntegerRange(e)
            {
                var browser = navigator.appName;
                if (browser == "Netscape") {
                    var keycode = e.which;
                    if ((keycode >= 48 && keycode <= 53) || keycode == 8 || keycode == 0)
                        return true;
                    else
                        return false;
                } else {
                    if ((e.keyCode >= 48 && e.keyCode <= 53) || e.keycode == 8 || e.keycode == 0)
                        e.returnValue = true;
                    else
                        e.returnValue = false;
                }
            }

            function savecheck() {
                var parstatus = $("#parstatus").val();
                if (parstatus == "6") {
                    if (!BlankNumericFieldValidation("ratingattitude", "Attitude to Work"))
                        return false;
                    if (!BlankNumericFieldValidation("ratingcoordination", "Co-ordination ability"))
                        return false;
                    if (!BlankNumericFieldValidation("ratingresponsibility", "Sense of responsibility"))
                        return false;
                    if (!BlankNumericFieldValidation("teamworkrating", "Ability to work in a team"))
                        return false;
                    if (!BlankNumericFieldValidation("ratingcomskill", "Communication skill"))
                        return false;
                    if (!BlankNumericFieldValidation("ratingitskill", "Knowledge of Rules"))
                        return false;
                    if (!BlankNumericFieldValidation("ratingleadership", "Leadership Qualities"))
                        return false;
                    if (!BlankNumericFieldValidation("ratinginitiative", "Initiative"))
                        return false;
                    if (!BlankNumericFieldValidation("ratingdecisionmaking", "Decision-making ability"))
                        return false;
                    if (!BlankNumericFieldValidation("ratequalityofwork", "Quality of Work"))
                        return false;
                    
                    var grading = $('#sltGrading').combobox('getValue');
                    if (grading == '') {
                        alert("Please select Overall Grading");
                        return false;
                    } else if (grading != '') {
                        var grde = grading;
                        var avg = "Below Average";
                        var outs = "Outstanding";
                        var decs = (grde == 1) ? avg : outs;
                        if ((grde == 1 || grde == 5) && $('#gradingNote').val() == '') {
                            alert("As you have selected Overall Grading as " + decs + " you must enter Justification");
                            $('#gradingNote').focus();
                            return false;
                        }
                    }
                } else if (parstatus == "7") {
                    if ($('#reviewingNote').val() == '') {
                        alert("Please enter Reviewing Note");
                        $('#reviewingNote').focus();
                        return false;
                    }
                    var reviewinggrading = $('#sltReviewGrading').combobox('getValue');
                    if (reviewinggrading == '') {
                        alert("Please select Reviewing Overall Grading");
                        $('#sltReviewGrading').focus();
                        return false;
                    }
                } else if (parstatus == "8") {
                    if ($('#acceptingNote').val() == '') {
                        alert("Please enter Accepting Note");
                        $('#acceptingNote').focus();
                        return false;
                    }
                }
                return true;
            }
			function submitcheck(){
                var isSubmit = savecheck();
                //alert("isSubmit is: "+isSubmit);
                if(isSubmit == true){
                    var isConfirm = confirm("Are you sure to Submit the Remarks?");
                    if(isConfirm == true){
                        return true;
                    }else{
                        return false;
                    }
                }else{
                    return false;
                }
            }
            function revertClick(){
                var url = 'RevertPAR.htm?fiscalyr='+$('#fiscalYear').val()+'&parId='+$('#parid').val()+'&parStatus='+$('#parstatus').val()+'&taskId='+$('#taskId').val()+'&isreportingcompleted='+$('#isreportingcompleted').val();
                $.colorbox({href:url,iframe:true, open:true, width:"80%", height:"100%"});
            }
        </script>
        <style type="text/css">
            body{
                font-family: Arial;
                font-size:13px;
            }
        </style>
    </head>
    <body>
        <div align="center" style="margin-top:5px;margin-bottom:10px;">
            <div align="center">
                <table border="0" width="99%" cellpadding="0" cellspacing="0" style="font-size:12px; font-family:verdana;">
                    <tr>
                        <td style="background-color:#5095ce;color:#FFFFFF;padding:0px;font-weight:bold;" align="center"><h2>Performance Appraisal Report (PAR) for Group 'A' & 'B' officers of Govt. of Odisha</h2></td>
                    </tr>                        
                </table>
            </div>
        </div>
        <div style="width:100%;overflow: auto;margin-top:5px;border:1px">
            <div align="center">
                <div style="width:99%;">                        
                    <div style="width:100%;overflow: auto;margin-top:1px;border:1px solid #5095ce;">
                        <div style="background-color:#5095ce;color:#FFFFFF;padding:5px;font-weight:bold;" align="left">Details of Transmission / Movement of PAR</div>
                        <table border="0" cellpadding="5" cellspacing="0" width="100%" class="tableview">
                            <tr style="height: 40px">                               
                                <td align="center" valign="top" width="10%"> 1. </td>
                                <td width="20%" valign="top">Reporting Authority</td>
                                <td width="70%">
                                    <table border="0" cellpadding="0" cellspacing="0" width="100%">
                                        <c:if test="${not empty pardetail.reportingauth}">
                                            <c:forEach var="rptauth" items="${pardetail.reportingauth}">
                                                <%slno = slno + 1;%>
                                                <tr>
                                                    <td width="5%"><%=slno%>.</td>
                                                    <c:if test="${rptauth.isPendingReportingAuthority == 'Y'}">
                                                        <td width="95%" style="color:red;">
                                                            <c:out value="${rptauth.authorityname}"/> (<c:out value="${rptauth.authorityspn}"/>)<br />
                                                            (<span style="font-size:10.5px;">From:<c:out value="${rptauth.fromdt}"/> To:<c:out value="${rptauth.todt}"/></span>)
                                                            &nbsp;&nbsp;&nbsp;&nbsp;<span style="color:black;">(Pending at this end)</span>      
                                                        </td>
                                                    </c:if>
                                                    <c:if test="${rptauth.isPendingReportingAuthority != 'Y'}">
                                                        <td width="95%">
                                                            <c:out value="${rptauth.authorityname}"/> (<c:out value="${rptauth.authorityspn}"/>)<br />
                                                            (<span style="font-size:10.5px;">From:<c:out value="${rptauth.fromdt}"/> To:<c:out value="${rptauth.todt}"/></span>)
                                                        </td>
                                                    </c:if>
                                                </tr>
                                            </c:forEach>
                                        </c:if>
                                    </table>
                                </td>
                            </tr>
                            <%slno = 0;%>
                            <tr style="height: 40px">                               
                                <td align="center" valign="top" width="10%"> 2. </td>
                                <td width="20%" valign="top">Reviewing Authority</td>
                                <td width="70%">
                                    <table border="0" cellpadding="0" cellspacing="0" width="100%">
                                        <c:if test="${not empty pardetail.reviewingauth}">
                                            <c:forEach var="rptauth" items="${pardetail.reviewingauth}">
                                                <%slno = slno + 1;%>
                                                <tr>
                                                    <td width="5%"><%=slno%>.</td>
                                                    <c:if test="${rptauth.isPendingReviewingAuthority == 'Y'}">
                                                        <td width="95%" style="color:red;">
                                                            <c:out value="${rptauth.authorityname}"/>(<c:out value="${rptauth.authorityspn}"/>)<br />
                                                            (<span style="font-size:10.5px;">From:<c:out value="${rptauth.fromdt}"/> To:<c:out value="${rptauth.todt}"/></span>
                                                            &nbsp;&nbsp;&nbsp;&nbsp;<span style="color:black;">(Pending at this end)</span>
                                                        </td>
                                                    </c:if>
                                                    <c:if test="${rptauth.isPendingReviewingAuthority != 'Y'}">
                                                        <td width="95%">
                                                            <c:out value="${rptauth.authorityname}"/>(<c:out value="${rptauth.authorityspn}"/>)<br />
                                                            (<span style="font-size:10.5px;">From:<c:out value="${rptauth.fromdt}"/> To:<c:out value="${rptauth.todt}"/></span>
                                                        </td>
                                                    </c:if>
                                                </tr>
                                            </c:forEach>
                                        </c:if>
                                    </table>
                                </td>
                            </tr>
                            <%slno = 0;%>
                            <tr style="height: 40px">
                                <td align="center" valign="top" width="10%"> 3. </td>
                                <td width="20%" valign="top">Accepting Authority</td>
                                <td width="70%">
                                    <table border="0" cellpadding="0" cellspacing="0" width="100%">
                                        <c:if test="${not empty pardetail.acceptingauth}">
                                            <c:forEach var="rptauth" items="${pardetail.acceptingauth}">
                                                <%slno = slno + 1;%>
                                                <tr>
                                                    <td width="5%"><%=slno%>.</td>
                                                    <c:if test="${rptauth.isPendingAcceptingAuthority == 'Y'}">
                                                        <td width="95%" style="color:red;">
                                                            <c:out value="${rptauth.authorityname}"/>(<c:out value="${rptauth.authorityspn}"/><br />
                                                            (<span style="font-size:10.5px;">From:<c:out value="${rptauth.fromdt}"/> To:<c:out value="${rptauth.todt}"/></span>)
                                                            &nbsp;&nbsp;&nbsp;&nbsp;<span style="color:black;">(Pending at this end)</span>
                                                        </td>
                                                    </c:if>
                                                    <c:if test="${rptauth.isPendingAcceptingAuthority != 'Y'}">
                                                        <td width="95%">
                                                            <c:out value="${rptauth.authorityname}"/>(<c:out value="${rptauth.authorityspn}"/><br />
                                                            (<span style="font-size:10.5px;">From:<c:out value="${rptauth.fromdt}"/> To:<c:out value="${rptauth.todt}"/></span>)
                                                        </td>
                                                    </c:if>
                                                </tr>
                                            </c:forEach>
                                        </c:if>
                                    </table>    
                                </td>
                            </tr>
                        </table>
                    </div>
                    <div style="width:100%;overflow: auto;margin-top:1px;border:1px solid #5095ce;">                                                        
                        <div style="background-color:#5095ce;color:#FFFFFF;padding:5px;font-weight:bold;" align="left">Personal Information</div>                            
                        <table border="0" cellpadding="5" cellspacing="0" width="100%" class="tableview">
                            <tr style="height: 40px">                               
                                <td align="center" width="10%"> 1. </td>
                                <td width="20%">Applicant</td>
                                <td width="70%">
                                    <span>
                                        <c:out value="${pardetail.applicant}"/>
                                    </span>
                                </td> 
                                <td rowspan="9" valign="top">
                                    <img id="loginUserPhoto" style="border:1px solid #a3a183;padding:3px;" onerror="callNoImage()"  alt="ProfileImage" src='displayprofilephoto.htm?empid=${pardetail.applicantempid}' width="100" height="100" />
                                </td>
                            </tr>
                            <tr style="height: 40px">                               
                                <td align="center" width="10%"> 2. </td>
                                <td width="20%">Fiscal Year</td>
                                <td width="70%">
                                    <span>
                                        <c:out value="${pardetail.fiscalYear}"/>
                                    </span>
                                </td> 
                            </tr>
                            <tr style="height: 40px">                               
                                <td align="center" width="10%"> 3. </td>
                                <td width="20%">Appraisal Period .</td>
                                <td width="70%">
                                    <span>
                                        From : <c:out value="${pardetail.parPeriodFrom}"/> -  To: <c:out value="${pardetail.parPeriodTo}"/>
                                    </span>
                                </td>
                            </tr>
                            <tr style="height: 40px">                               
                                <td align="center" width="10%"> 4. </td>
                                <td width="20%">Date of Birth .</td>
                                <td width="70%"><span><c:out value="${pardetail.dob}"/></span></td>
                            </tr>
                            <tr style="height: 40px">                               
                                <td align="center" width="10%"> 5. </td>
                                <td width="20%">Service to which the officer belongs .</td>
                                <td width="70%"><span><c:out value="${pardetail.empService}"/></span></td>
                            </tr>
                            <tr style="height: 40px">                               
                                <td align="center" width="10%"> 6. </td>
                                <td width="20%">Group to which the officer belongs .</td>
                                <td width="70%"> <span><c:out value="${pardetail.empGroup}"/></span></td>
                            </tr>
                            <tr style="height: 40px">                               
                                <td align="center" width="10%"> 7. </td>
                                <td width="20%">Designation during the period of report .</td>
                                <td width="70%"><span><c:out value="${pardetail.apprisespn}"/></span></td>
                            </tr>
                            <tr style="height: 40px">                               
                                <td align="center" width="10%"> 8. </td>
                                <td width="20%">Office to where posted .</td>
                                <td width="70%"><span><c:out value="${pardetail.empOffice}"/></span></td>                                         
                            </tr>
                            <tr style="height: 40px">                               
                                <td align="center" width="10%"> 9. </td>
                                <td width="20%">Head Quarter(if any) .</td>
                                <td width="70%"><span><c:out value="${pardetail.sltHeadQuarter}"/></span></td>
                            </tr>
                        </table>
                    </div>
                    <div style="width:100%;overflow: auto;margin-top:5px;border:1px solid #5095ce;">
                        <div style="background-color:#5095ce;color:#FFFFFF;padding:5px;font-weight:bold;" align="left">Absentee Statement</div>                        
                        <table border="0" cellpadding="5" cellspacing="0" width="100%" class="tableview">             
                            <tr>
                                <th align="center" width="15%"><b>From Date</b></th>
                                <th align="center" width="15%"><b>To Date</b></th>
                                <th align="center" width="15%"><b>Leave/ Training</b></th>
                                <th align="center" width="15%"><b>Type of Leave</b></th>
                            </tr>  
                        </table>
                        <table border="0" cellpadding="5" cellspacing="0" width="100%" class="tableview">
                            <c:if test="${not empty pardetail.leaveAbsentee}">
                                <c:forEach var="AbsenteeBean" items="${pardetail.leaveAbsentee}">
                                    <tr height="40px">
                                        <td width="15%" align="center"><c:out value="${AbsenteeBean.fromDate}"/></td>
                                        <td width="15%" align="center"><c:out value="${AbsenteeBean.toDate}"/></td>
                                        <c:if test="${AbsenteeBean.absenceCause == 'L'}">
                                            <td width="15%" align="center">Leave</td>
                                        </c:if>
                                        <c:if test="${AbsenteeBean.absenceCause == 'T'}">
                                            <td width="15%" align="center">Training</td>
                                        </c:if>
                                        <td width="15%" align="center"><c:out value="${AbsenteeBean.leaveType}"/></td>
                                    </tr>
                                </c:forEach>
                            </c:if>
                        </table>
                    </div>
                    <div style="width:100%;overflow: auto;margin-top:5px;border:1px solid #5095ce;">
                        <div style="background-color:#5095ce;color:#FFFFFF;padding:5px;font-weight:bold;" align="left">Achievements</div>                            
                        <table border="0" cellpadding="5" cellspacing="0" width="100%" class="tableview">             
                            <tr>
								<th align="center" width="5%"><b>SL No</b></th>
                                <th align="center" width="15%"><b>Task</b></th>
                                <th align="center" width="15%"><b>Target</b></th>
                                <th align="center" width="15%"><b>Achievement</b></th>
                                <th align="center" width="10%"><b>Achievement(%)</b></th>
                                <th align="center" width="15%"><b>Attachment(if any)</b></th>
                            </tr>  
                        </table>
                        <table border="0" cellpadding="5" cellspacing="0" width="100%" class="tableview"> 
                            <c:if test="${not empty pardetail.fiscalYear}">
                                <c:set var="fyear" value="${pardetail.fiscalYear}"/>
                                <%
                                    fiscalyear = (String) pageContext.getAttribute("fyear");
                                %>
                            </c:if>
                            <c:if test="${not empty pardetail.achivementList}">
                                <c:forEach var="AchievementBean" items="${pardetail.achivementList}">
                                    <c:if test="${not empty AchievementBean.attachmentId}">
                                        <c:set var="attid" value="${AchievementBean.attachmentId}"/>
                                        <%
                                            atchid = (String) pageContext.getAttribute("attid");
                                        %>
                                    </c:if>
                                    <%
                                        downloadlink = "DownloadAchievementAttachment.htm?attId=" + atchid + "&fiscalyr=" + fiscalyear;
                                    %>
                                    <tr height="40px">
										<td width="5%" align="center"><c:out value="${AchievementBean.slno}"/></td>
                                        <td width="15%" align="center"><c:out value="${AchievementBean.task}"/></td>
                                        <td width="15%" align="center"><c:out value="${AchievementBean.target}"/></td>
                                        <td width="15%" align="center"><c:out value="${AchievementBean.achievement}"/></td>
                                        <td width="10%" align="center"><c:out value="${AchievementBean.percentAchievement}"/></td>
                                        <td width="15%" align="center">
                                        <a href='<%=downloadlink%>' style="text-decoration:none;">
                                            <c:out value="${AchievementBean.attachmentname}"/>
                                        </a>
                                    </td>
                                    </tr>
                                </c:forEach>
                            </c:if>
                        </table>
                    </div>
                    <div style="width:100%;overflow: auto;margin-top:5px;border:1px solid #5095ce;">
                        <div style="background-color:#5095ce;color:#FFFFFF;padding:5px;font-weight:bold;" align="left">Other Details</div>
                        <table border="0" cellpadding="5" cellspacing="0" width="100%" class="tableview">                                         
                            <tr style="height: 40px">                               
                                <td align="center" width="10%"> 1. </td>
                                <td width="20%">Brief description of duties/tasks entrusted.(in about 100 words)</td>
                                <td width="70%">
                                    <span>
                                        <c:out value="${pardetail.selfappraisal}" escapeXml="false"/>
                                    </span>
                                </td>
                            </tr>
                            <tr style="height: 40px">                               
                                <td align="center" width="10%"> 2. </td>
                                <td width="20%">Significant work, if any, done</td>
                                <td width="70%">
                                    <span>
                                        <c:out value="${pardetail.specialcontribution}" escapeXml="false"/>
                                    </span>
                                </td> 
                            </tr>
                            <tr style="height: 40px">                               
                                <td align="center" width="10%"> 3. </td>
                                <td width="20%">Hindrance</td>
                                <td width="70%">
                                    <span>
                                        <c:out value="${pardetail.reason}" escapeXml="false"/>
                                    </span>
                                </td>
                            </tr>
                            <tr style="height: 40px">                               
                                <td align="center" width="10%"> 4. </td>
                                <td width="20%">Place</td>
                                <td width="70%">
                                    <span>
                                        <c:out value="${pardetail.place}"/>
                                    </span>
                                </td> 
                            </tr>
                        </table>
                    </div>
                    <form action="forwardPAR.htm" method="POST" commandName="parDetail">
                        <input type="hidden" name="parid" id="parid" value="${pardetail.parid}"/>
                        <input type="hidden" name="taskid" id="taskId" value="${pardetail.taskid}"/>
                        <input type="hidden" name="parstatus" id="parstatus" value="${pardetail.parstatus}"/>
                        <input type="hidden" name="apprisespc" id="apprisespc" value="${pardetail.apprisespc}"/>
                        <input type="hidden" name="isreportingcompleted" value="${pardetail.isreportingcompleted}"/>
                        <input type="hidden" name="reportingempid" value="${pardetail.reportingempid}"/>
                        <input type="hidden" name="isreviewingcompleted" value="${pardetail.isreviewingcompleted}"/>
                        <input type="hidden" name="isacceptingcompleted" value="${pardetail.isacceptingcompleted}"/>
                        <input type="hidden" name="fiscalYear" id="fiscalYear" value="${pardetail.fiscalYear}"/>
                        <input type="hidden" name="urlName" value="${pardetail.urlName}"/>
                        <c:if test="${pardetail.ishideremark != null && pardetail.ishideremark == 'N'}">
                            <div style="width:100%;overflow: auto;margin-top:5px;border:1px solid #5F9B24;">
                                <div style="background-color:#5F9B24;color:#FFFFFF;padding:5px;font-weight:bold;" align="left">Remarks of Reporting Authority</div>
                                <table border="0" cellpadding="5" cellspacing="0" width="100%" class="tableview">
                                    <tr style="height: 40px">
                                        <td width="70%">
                                            <c:if test="${not empty pardetail.reportingdata}">
                                                <%slno = 0;%>
                                                <c:forEach var="reportingdt" items="${pardetail.reportingdata}">
                                                    <c:if test="${reportingdt.isreportingcompleted == 'F'}">
                                                        <table>
                                                            <tr>
                                                                <%if (slno == 0) {%>
                                                                <td style="font-weight:bold;text-decoration:underline;">
                                                                    <c:out value="${reportingdt.reportingauthName}"/>
                                                                </td>
                                                                <%} else {%>
                                                                <td style="border-top:1px solid black;font-weight:bold;text-decoration:underline;">
                                                                    <c:out value="${reportingdt.reportingauthName}"/>
                                                                </td>
                                                                <%}%>
                                                            </tr>
                                                            <tr>
                                                                <td>
                                                                    No Remarks Given by the Authority.
                                                                </td>
                                                            </tr>
                                                        </table>
                                                    </c:if>
                                                    <c:if test="${reportingdt.isreportingcompleted == 'Y'}">
                                                        <table>
                                                            <tr>
                                                                <%if (slno == 0) {%>
                                                                <td style="font-weight:bold;text-decoration:underline;">
                                                                    <c:out value="${reportingdt.reportingauthName}"/>
                                                                </td>
                                                                <%} else {%>
                                                                <td style="border-top:1px solid black;font-weight:bold;text-decoration:underline;">
                                                                    <c:out value="${reportingdt.reportingauthName}"/>
                                                                </td>
                                                                <%}%>
                                                            </tr>
                                                            <tr>
                                                                <td><span>1. Assessment of work output, attributes & functional competencies.</span>(This should be on a relative scale of 1-5, with 1 referring to the lowest level & 5   to the highest level. Please indicate your rating for the officer against each item.)</td>
                                                            </tr>
                                                            <tr>
                                                                <td>
                                                                    <table width="60%" style="margin-left:35px;">
                                                                        <tr>
                                                                            <th width="15%">Description</th>
                                                                            <th width="15%">Rating</th>
                                                                            <th width="15%">Description</th>
                                                                            <th width="15%">Rating</th>
                                                                        </tr>

                                                                        <tr>
                                                                            <td>(a)  Attitude to work    :</td>
                                                                            <td><div style="padding:5px;"><c:out value="${reportingdt.ratingattitude}"/></div></td>
                                                                            <td>(f) Co-ordination ability:</td>
                                                                            <td><div style="padding:5px;"><c:out value="${reportingdt.ratingcoordination}"/></div></td>
                                                                        </tr>
                                                                        <tr>
                                                                            <td>(b)  Sense of responsibility:    </td>
                                                                            <td><div style="padding:5px;"><c:out value="${reportingdt.ratingresponsibility}"/></div></td>
                                                                            <td>(g) Ability to work in a team:</td>
                                                                            <td><div style="padding:5px;"><c:out value="${reportingdt.teamworkrating}"/></div></td>
                                                                        </tr>
                                                                        <tr>
                                                                            <td>(c)  Communication skill :  </td>
                                                                            <td><div style="padding:5px;"><c:out value="${reportingdt.ratingcomskill}"/></div></td>
                                                                            <td>(h) Knowledge of Rules/Procedures/ IT  Skills/ Relevant Subject :</td>
                                                                            <td><div style="padding:5px;"><c:out value="${reportingdt.ratingitskill}"/></div></td>
                                                                        </tr>
                                                                        <tr>
                                                                            <td>(d)  Leadership Qualities :  </td>
                                                                            <td><div style="padding:5px;"><c:out value="${reportingdt.ratingleadership}"/></div></td>
                                                                            <td>(i) Initiative :</td>
                                                                            <td><div style="padding:5px;"><c:out value="${reportingdt.ratinginitiative}"/></div></td>
                                                                        </tr>
                                                                        <tr>
                                                                            <td>(e) ) Decision-making ability :  </td>
                                                                            <td><div style="padding:5px;"><c:out value="${reportingdt.ratingdecisionmaking}"/></div></td>
                                                                            <td>(j) ) Quality of Work :</td>
                                                                            <td><div style="padding:5px;"><c:out value="${reportingdt.ratequalityofwork}"/></div></td>
                                                                        </tr>
                                                                    </table>
                                                                </td>
                                                            </tr>
                                                            <tr>
                                                                <td><span>2. General Assessment </span>(Please give an overall assessment of the officer including   his/her   attitude towards  S.T/S.C/Weaker Sections &  relation  with public):</td>
                                                            </tr>
                                                            <tr>
                                                                <td><div style="padding:5px;"><c:out value="${reportingdt.authNote}"/></div></td>
                                                            </tr>
                                                            <tr>
                                                                <td><span>3. Inadequacies, deficiencies or shortcomings, if any (Remarks to be treated as adverse ):</span></td>
                                                            </tr>
                                                            <tr>
                                                                <td><div style="padding:5px;"><c:out value="${reportingdt.inadequaciesNote}"/></div></td>
                                                            </tr>  
                                                            <tr>
                                                                <td><span>4. Integrity (If integrity is doubtful or  adverse please write "Not certified" in the space below and justify your remarks in box 4 above):</span></td>
                                                            </tr>
                                                            <tr>
                                                                <td><div style="padding:5px;"><c:out value="${reportingdt.integrityNote}"/></div></td>
                                                            </tr> 
                                                            <tr>
                                                                <td><span> 5. Overall Grading : </span><span style="padding:5px;"><c:out value="${reportingdt.sltGradingName}"/></span></td>
                                                            </tr>
                                                            <tr>
                                                                <td><span>6. For  Overall Grading  Below Average /  Outstanding  please provide justification in the   space below.:</span></td>
                                                            </tr>
                                                            <tr>
                                                                <td><div style="padding:5px;"><c:out value="${reportingdt.gradingNote}"/></div></td>
                                                            </tr> 
                                                        </table>
                                                    </c:if>
                                                    <c:if test="${reportingdt.isreportingcompleted != 'Y'}">
                                                        <c:if test="${reportingdt.iscurrentreporting == 'Y'}">

                                                            <table>
                                                                <tr>
                                                                    <%if (slno == 0) {%>
                                                                    <td style="font-weight:bold;text-decoration:underline;">
                                                                        <c:out value="${reportingdt.reportingauthName}"/>
                                                                    </td>
                                                                    <%} else {%>
                                                                    <td style="border-top:1px solid black;font-weight:bold;text-decoration:underline;">
                                                                        <c:out value="${reportingdt.reportingauthName}"/>
                                                                    </td>
                                                                    <%}%>
                                                                </tr>
                                                                <tr>
                                                                    <td><span>1. Assessment of work output, attributes & functional competencies.</span>(This should be on a relative scale of 1-5, with 1 referring to the lowest level & 5   to the highest level. Please indicate your rating for the officer against each item.) </td>
                                                                </tr>
                                                                <tr>
                                                                    <td>
                                                                        <table width="60%" style="margin-left:35px;">
                                                                            <tr>
                                                                                <th width="15%">Description</td>
                                                                                <th width="15%">Rating</td>
                                                                                <th width="15%">Description</td>
                                                                                <th width="15%">Rating</td>
                                                                            </tr>
                                                                            <tr>
                                                                                <td>(a)  Attitude to work    :</td>
                                                                                <td><input type="text" name="ratingattitude" id="ratingattitude" value="${reportingdt.ratingattitude}" maxlength="1" onkeypress="return onlyIntegerRange(event)"/></td>
                                                                                <td>(f) Co-ordination ability:</td>
                                                                                <td><input type="text" name="ratingcoordination" id="ratingcoordination" value="${reportingdt.ratingcoordination}" maxlength="1" onkeypress="return onlyIntegerRange(event)"/></td>
                                                                            </tr>
                                                                            <tr>
                                                                                <td>(b)  Sense of responsibility:    </td>
                                                                                <td><input type="text" name="ratingresponsibility" id="ratingresponsibility" value="${reportingdt.ratingresponsibility}" maxlength="1" onkeypress="return onlyIntegerRange(event)"/></td>
                                                                                <td>(g) Ability to work in a team:</td>
                                                                                <td><input type="text" name="teamworkrating" id="teamworkrating" value="${reportingdt.teamworkrating}" maxlength="1" onkeypress="return onlyIntegerRange(event)"/></td>
                                                                            </tr>
                                                                            <tr>
                                                                                <td>(c)  Communication skill :  </td>
                                                                                <td><input type="text" name="ratingcomskill" id="ratingcomskill" value="${reportingdt.ratingcomskill}" maxlength="1" onkeypress="return onlyIntegerRange(event)"/></td>
                                                                                <td>(h) Knowledge of Rules/Procedures/ IT  Skills/ Relevant Subject :</td>
                                                                                <td><input type="text" name="ratingitskill" id="ratingitskill" value="${reportingdt.ratingitskill}" maxlength="1" onkeypress="return onlyIntegerRange(event)"/></td>
                                                                            </tr>
                                                                            <tr>
                                                                                <td>(d)  Leadership Qualities :  </td>
                                                                                <td><input type="text" name="ratingleadership" id="ratingleadership" value="${reportingdt.ratingleadership}" maxlength="1" onkeypress="return onlyIntegerRange(event)"/></td>
                                                                                <td>(i) Initiative :</td>
                                                                                <td><input type="text" name="ratinginitiative" id="ratinginitiative" value="${reportingdt.ratinginitiative}" maxlength="1" onkeypress="return onlyIntegerRange(event)"/></td>
                                                                            </tr>
                                                                            <tr>
                                                                                <td>(e) Decision-making ability :  </td>
                                                                                <td><input type="text" name="ratingdecisionmaking" id="ratingdecisionmaking" value="${reportingdt.ratingdecisionmaking}" maxlength="1" onkeypress="return onlyIntegerRange(event)"/></td>
                                                                                <td>(j) Quality of Work :</td>
                                                                                <td><input type="text" name="ratequalityofwork" id="ratequalityofwork" value="${reportingdt.ratequalityofwork}" maxlength="1" onkeypress="return onlyIntegerRange(event)"/></td>
                                                                            </tr>
                                                                        </table>
                                                                    </td>
                                                                </tr>
                                                                <tr>
                                                                    <td><span>2. General Assessment </span>(Please give an overall assessment of the officer including   his/her   attitude towards  S.T/S.C/Weaker Sections &  relation  with public):</td>
                                                                </tr>
                                                                <tr>
                                                                    <td><textarea name="authNote" class="textareacolor" rows="4" id="authNote" style="width:80%;height:60px;text-align:left;">${reportingdt.authNote}</textarea></td>
                                                                </tr>
                                                                <tr>
                                                                    <td>3. Inadequacies, deficiencies or shortcomings, if any (Remarks to be treated as adverse ):</td>
                                                                </tr>
                                                                <tr>
                                                                    <td><textarea name="inadequaciesNote" class="textareacolor" rows="4" id="inadequaciesNote" style="width:80%;height:60px;text-align:left;">${reportingdt.inadequaciesNote}</textarea></td>
                                                                </tr>  
                                                                <tr>
                                                                    <td>4. Integrity (If integrity is doubtful or  adverse please write “Not certified” in the space below and justify your remarks in box 4 above):</td>
                                                                </tr>
                                                                <tr>
                                                                    <td><textarea name="integrityNote"  class="textareacolor" rows="4" id="inadequaciesNote" style="width:80%;height:60px;text-align:left;">${reportingdt.integrityNote}</textarea></td>
                                                                </tr> 
                                                                <tr>
                                                                    <td> 5. Overall Grading :
                                                                        <input type="hidden" id="hidsltGrading" value="${reportingdt.sltGrading}"/>
                                                                        <input name="sltGrading" id="sltGrading" class="easyui-combobox" style="width:20%" data-options="valueField:'value',textField:'label',url:'GetPARGradeListJSON.htm'"/>
                                                                        <%--<select name="sltGrading">
                                                                            <option value="">Select</option>
                                                                            <c:forEach var="grd" items="${gradelist}">
                                                                                <option value="${grd.value}">${grd.label}</option>
                                                                            </c:forEach>
                                                                        </select>--%>
                                                                    </td>
                                                                </tr>
                                                                <tr>
                                                                    <td>6. For  Overall Grading  “Below Average” /  “Outstanding”  please provide justification in the   space below.:</td>
                                                                </tr>
                                                                <tr>
                                                                    <td><textarea name="gradingNote" class="textareacolor" rows="4" id="gradingNote" style="width:80%;height:60px;text-align:left;">${reportingdt.gradingNote}</textarea></td>
                                                                </tr> 
                                                            </table>
                                                        </c:if>
                                                    </c:if>
                                                    <%slno += 1;%>
                                                </c:forEach>
                                            </c:if>
                                        </td>
                                    </tr>
                                </table>
                            </div>

                            <div style="width:100%;overflow: auto;margin-top:5px;border:1px solid #5095ce;">
                                <div style="background-color:#5F9B24;color:#FFFFFF;padding:5px;font-weight:bold;" align="left">Remarks of Reviewing Authority</div>
                                <table border="0" cellpadding="5" cellspacing="0" width="100%" class="tableview">                                        
                                    <tr style="height: 40px">
                                        <td width="70%">
                                            <c:if test="${not empty pardetail.reviewingdata}">
                                                <%slno = 0;%>
                                                <c:forEach var="reviewingdt" items="${pardetail.reviewingdata}">
                                                    <c:if test="${reviewingdt.isreviewingcompleted == 'F'}">
                                                        <table>
                                                            <tr>
                                                                <%if (slno == 0) {%>
                                                                <td style="font-weight:bold;text-decoration:underline;">
                                                                    <c:out value="${reviewingdt.reviewingauthName}"/>
                                                                </td>
                                                                <%} else {%>
                                                                <td style="border-top:1px solid black;font-weight:bold;text-decoration:underline;">
                                                                    <c:out value="${reviewingdt.reviewingauthName}"/>
                                                                </td>
                                                                <%}%>
                                                            </tr>
                                                            <tr>
                                                                <td>
                                                                    No Remarks Given by the Authority.
                                                                </td>
                                                            </tr>
                                                        </table>
                                                    </c:if>
                                                    <c:if test="${reviewingdt.isreviewingcompleted == 'Y'}">
                                                        <table border="0" cellpadding="5" cellspacing="0" width="100%" class="tableview">
                                                            <tr>
                                                                <%if (slno == 0) {%>
                                                                <td style="font-weight:bold;text-decoration:underline;">
                                                                    <c:out value="${reviewingdt.reviewingauthName}"/>
                                                                </td>
                                                                <%} else {%>
                                                                <td style="border-top:1px solid black;font-weight:bold;text-decoration:underline;">
                                                                    <c:out value="${reviewingdt.reviewingauthName}"/>
                                                                </td>
                                                                <%}%>
                                                            </tr>
                                                            <tr>
                                                                <td><span>1. Please Indicate if you agree with the general assessment/ adverse remarks/ overall grading  made by the   Reporting Authority, and give your assessment.</span></td>
                                                            </tr>
                                                            <tr>
                                                                <td>
                                                                    <c:out value="${reviewingdt.reviewingNote}"/>
                                                                </td>
                                                            </tr>
                                                            <tr>
                                                                <td> <span>2. Overall Grading Given By Reviewing Authority  :</span>
                                                                    <c:out value="${reviewingdt.reviewGrading}"/>
                                                                </td>
                                                            </tr>
                                                        </table>
                                                    </c:if>
                                                    <c:if test="${reviewingdt.isreviewingcompleted != 'Y'}">
                                                        <c:if test="${pardetail.parstatus == '7'}">
                                                            <c:if test="${reviewingdt.iscurrentreviewing == 'Y'}">
                                                                <table border="0" cellpadding="5" cellspacing="0" width="100%" class="tableview">
                                                                    <tr>
                                                                        <%if (slno == 0) {%>
                                                                        <td style="font-weight:bold;text-decoration:underline;">
                                                                            <c:out value="${reviewingdt.reviewingauthName}"/>
                                                                        </td>
                                                                        <%} else {%>
                                                                        <td style="border-top:1px solid black;font-weight:bold;text-decoration:underline;">
                                                                            <c:out value="${reviewingdt.reviewingauthName}"/>
                                                                        </td>
                                                                        <%}%>
                                                                    </tr>
                                                                    <tr>
                                                                        <td><span>1. Please Indicate if you agree with the general assessment/ adverse remarks/ overall grading  made by the   Reporting Authority, and give your assessment.</span></td>
                                                                    </tr>
                                                                    <tr>
                                                                        <td><textarea name="reviewingNote" class="textareacolor" rows="4" id="reviewingNote" style="width:80%;height:60px;text-align:left;">${reviewingdt.reviewingNote}</textarea></td>
                                                                    </tr>
                                                                    <tr>
                                                                        <td> <span>2. Overall Grading Given By Reviewing Authority  :</span>
                                                                            <%--<select name="sltReviewGrading" id="sltReviewGrading">
                                                                                <option value="">Select</option>
                                                                                <c:forEach var="grdarr" items="${pardetail.gradingArr}">
                                                                                    <option value="${grdarr}">${grdarr}</option>
                                                                                </c:forEach>
                                                                            </select>--%>
                                                                            <input type="hidden" id="hidsltReviewGrading" value="${reviewingdt.sltReviewGrading}"/>
                                                                            <input name="sltReviewGrading" id="sltReviewGrading" class="easyui-combobox" style="width:20%" data-options="valueField:'value',textField:'label',url:'GetPARGradeListJSON.htm'"/>
                                                                        </td>
                                                                    </tr>
                                                                </table>
                                                            </c:if>
                                                        </c:if>
                                                    </c:if>
                                                    <%slno += 1;%>
                                                </c:forEach>
                                            </c:if>
                                        </td> 
                                    </tr>
                                </table>
                            </div>

                            <div style="width:100%;overflow: auto;margin-top:5px;border:1px solid #5095ce;">
                                <div style="background-color:#5F9B24;color:#FFFFFF;padding:5px;font-weight:bold;" align="left">Remarks of Accepting Authority</div>
                                <c:if test="${not empty pardetail.acceptingdata}">
                                    <%slno = 0;%>
                                    <c:forEach var="acceptingdt" items="${pardetail.acceptingdata}">
                                        <table border="0" cellpadding="5" cellspacing="0" width="100%" class="tableview">
                                            <tr style="height: 40px">
                                                <td width="70%">
                                                    <span>
                                                        <c:if test="${acceptingdt.isacceptingcompleted == 'Y'}">
                                                            <table width="100%">
                                                                <tr>
                                                                    <%if (slno == 0) {%>
                                                                    <td style="font-weight:bold;text-decoration:underline;">
                                                                        <c:out value="${acceptingdt.acceptingauthName}"/>
                                                                    </td>
                                                                    <%} else {%>
                                                                    <td style="border-top:1px solid black;font-weight:bold;text-decoration:underline;">
                                                                        <c:out value="${acceptingdt.acceptingauthName}"/>
                                                                    </td>
                                                                    <%}%>
                                                                </tr>
                                                            </table>
                                                            <div style="padding:5px;"><c:out value="${acceptingdt.acceptingNote}"/></div>
                                                        </c:if>
                                                        <c:if test="${acceptingdt.isacceptingcompleted == 'F'}">
                                                            <table>
                                                                <tr>
                                                                    <%if (slno == 0) {%>
                                                                    <td style="font-weight:bold;text-decoration:underline;">
                                                                        <c:out value="${acceptingdt.acceptingauthName}"/>
                                                                    </td>
                                                                    <%} else {%>
                                                                    <td style="border-top:1px solid black;font-weight:bold;text-decoration:underline;">
                                                                        <c:out value="${acceptingdt.acceptingauthName}"/>
                                                                    </td>
                                                                    <%}%>
                                                                </tr>
                                                                <tr>
                                                                    <td>
                                                                        No Remarks Given by the Authority.
                                                                    </td>
                                                                </tr>
                                                            </table>
                                                        </c:if>
                                                        <c:if test="${acceptingdt.isacceptingcompleted != 'Y'}">
                                                            <c:if test="${acceptingdt.iscurrentaccepting == 'Y'}">
                                                                <table width="100%">
                                                                    <tr>
                                                                        <%if (slno == 0) {%>
                                                                        <td style="font-weight:bold;text-decoration:underline;">
                                                                            <c:out value="${acceptingdt.acceptingauthName}"/>
                                                                        </td>
                                                                        <%} else {%>
                                                                        <td style="border-top:1px solid black;font-weight:bold;text-decoration:underline;">
                                                                            <c:out value="${acceptingdt.acceptingauthName}"/>
                                                                        </td>
                                                                        <%}%>
                                                                    </tr>
                                                                </table>
                                                                <c:if test="${pardetail.parstatus == '8'}">
                                                                    <textarea name="acceptingNote" class="textareacolor" rows="4" id="acceptingNote" style="width:80%;height:60px;text-align:left;">${acceptingdt.acceptingNote}</textarea>
                                                                </c:if>
                                                            </c:if>
                                                        </c:if>
                                                    </span>
                                                </td>
                                            </tr>
                                        </table>
                                        <%slno += 1;%>
                                    </c:forEach>
                                </c:if>
                            </div>
                    </div>
                </div>
            </div>  
        </c:if>
        <div align="center">
            <div class="controlpanelDiv">	
                <table width="100%" cellpadding="0" cellspacing="0" >
                    <tr style="height:40px">
                        <td align="left" class="skinbutton sb_active">
                            <span style="padding-left:10px;">
                                <%
                                    int parId = 0;
                                    int taskId = 0;
                                %>
                                <c:if test="${not empty pardetail.parid}">
                                    <c:set var="pid" value="${pardetail.parid}"/>

                                    <%
                                        parId = Integer.parseInt(pageContext.getAttribute("pid") + "");
                                    %>
                                </c:if>
                                <c:if test="${not empty pardetail.taskid}">
                                    <c:set var="tid" value="${pardetail.taskid}"/>
                                    <%
                                        taskId = Integer.parseInt(pageContext.getAttribute("tid") + "");
                                    %>
                                </c:if>                                        

                                <%--<html:submit property="submit" value="Cancel" styleClass="btn1" />--%>
                                <%
                                    //pdflink = "viewPAR.htm?parid="+parId+"&taskid="+taskId;
                                    pdflink = "viewPAR.htm?parid=" + parId;
                                %>
                                <%--<html:submit property="submit" value="Download" styleClass="btn1" />--%>
                                <a href='<%=pdflink%>' target="_blank" class="easyui-linkbutton">Download</a>
                                <c:if test="${pardetail.ishideremark == 'N'}">
                                    <c:if test="${pardetail.parstatus != '21' && pardetail.parstatus < 9}">
                                        <c:if test="${pardetail.isClosedFiscalYearAuthority != 'Y'}">

                                            <input type="submit" name="forwardpar" value="Save" class="easyui-linkbutton" onclick="return savecheck()"/> 
                                            <input type="submit" name="forwardpar" value="Submit" class="easyui-linkbutton" onclick="return submitcheck()"/> 

                                            <%--<html:submit property="submit" style="margin-left:100px;color:red;" value="Revert" styleClass="btn1" />--%>
                                            <%
                                                revertlink = "revertPAR.htm?parid=" + parId;
                                            %>
                                            <%--<html:link action='<%=revertlink%>' target="_blank" styleClass="btn1">Revert</html:link>--%>
                                            <c:if test="${pardetail.parstatus == '6'}">
                                                <a href="javascript:revertClick();" class="easyui-linkbutton">Revert</a>
                                            </c:if>
                                            <c:if test="${pardetail.parstatus == '7'}">
                                                <a href="javascript:revertClick();" class="easyui-linkbutton">Revert</a>
                                            </c:if>
                                            <c:if test="${pardetail.parstatus == '8'}">
                                                <a href="javascript:revertClick();" class="easyui-linkbutton">Revert</a>
                                            </c:if>
											<c:if test="${isClosed == 'Y'}">
                                                <span style="color:red;">Remarks Submission for Financial Year <c:out value="${pardetail.fiscalYear}"/> is closed.</span>
                                            </c:if>
                                        </c:if>
                                    </c:if>
                                </c:if>
                            </span>
                        </td>
                        <td width="60%" style="color:black;">
                            EMP ID - <c:out value="${pardetail.applicantempid}"/>,
                            PAR ID - <c:out value="${pardetail.parid}"/>,
							Submitted On - <c:out value="${pardetail.submitted_on}"/>
                        </td>
                    </tr>                        
                </table>                                                
            </div>
        </div>
    </form>
</body>
</html>
