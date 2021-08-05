<%@page contentType="text/html" pageEncoding="UTF-8" import="hrms.common.CommonFunctions"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%
    String completedtasklink = "";
%>
<html>
    <head>
		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
        <title>Human Resources Management System, Government of Odisha</title>

        <link rel="stylesheet" type="text/css" href="resources/css/themes/default/easyui.css">
        <link rel="stylesheet" type="text/css" href="resources/css/themes/icon.css">
        <link rel="stylesheet" type="text/css" href="resources/css/colorbox.css"/>
		<link rel="stylesheet" type="text/css" href="css/popupmain.css"/>
        <style type="text/css">
            body{
                font-family:Verdana,sans-serif;
            }
            .alink{
                color:#fff !important;
                text-decoration: none;
                font-family: Verdana,sans-serif;
                font-size: 12px;                
                background-color: #286090;
                border-color: #204d74;
                white-space: nowrap;
                border: 1px solid transparent;
                border-radius: 4px;
                display: inline-block;
                padding: 6px 12px;

            }
        </style>

        <!-- LAYOUT v 1.3.0 -->
        <script type="text/javascript" src="js/jquery.min-1.9.1.js"></script>
        <script type="text/javascript" src="js/jquery.easyui.min.js"></script>
        <script type="text/javascript" src="js/datagrid-detailview.js"></script>
        <script type="text/javascript" src="js/webcam.js"></script>
        <script type="text/javascript"  src="js/jquery.colorbox-min.js"></script>

        <script language="JavaScript" type="text/javascript">
            $(document).ready(function() {
                $("#capturesnap").hide();
                $('#viewcompleted').hide();
                getHolidays();
                $('#win').window({
                    onBeforeClose: function() {
                        $('#dg').datagrid('reload');
                    }
                })

                var parval = $('#parstatus').combobox('getValue');
                var process = $('#sltProcess').combobox('getValue');
                var empname = $('#txtEmpname').val();
                var gpfno = $('#txtGPFNo').val();
                $('#dg').datagrid({
                    url: "taskAction.htm?parstatus=" + parval + "&processId=" + process + "&empName=" + empname + "&gpf=" + gpfno
                            //url: "taskAction.htm"
                });

                $('#parstatus').combobox({
                    onChange: function(rec) {
                        if (rec.value != 9) {
                            $('#viewcompleted').hide();
                        }
                    }
                });

            });
            function getHolidays() {
                $.ajax({
                    url: 'getHolidayList.htm',
                    type: 'get',
                    success: function(retVal) {
                        strHolidays = retVal.toString();
                        arrHolidays = strHolidays.split(',');
                        $('#easyuical').calendar({
                            formatter: function(date) {
                                var d = date.getDate();
                                var opts = $(this).calendar('options');
                                var y = opts.year;
                                var m = opts.month;
                                fd = date.getDay();
                                nw = Math.ceil(d / 7);
                                d1 = d;
                                if (d1 < 10)
                                    d1 = '0' + d1;
                                if (m < 10)
                                    m = '0' + m;
                                for (var i = 0; i < arrHolidays.length; i++)
                                {
                                    arrTemp = arrHolidays[i].split('->');
                                    if ((d1 + '-' + m + '-' + y) == arrTemp[0])
                                    {
                                        if (arrTemp[2] == 'G')
                                            return '<span style="color:#FF0000;font-weight:bold;" title="' + arrTemp[1] + '">' + d + '</span>';
                                        else
                                            return '<span style="color:#008900;font-weight:bold;" title="' + arrTemp[1] + ' (Optional)">' + d + '</span>';
                                    }
                                }
                                if (fd == 6)
                                {
                                    if (nw == 2)
                                        return '<span style="color:#FF0000;font-weight:bold;" title="Second Saturday">' + d + '</span>';
                                    else
                                        return '<span style="color:#000000;">' + d + '</span>';
                                }
                                return d;
                            }
                        });
                    }
                });
            }
            function acceptingAuthsave() {
                var isConfirm = confirm("Are you sure to submit the Remarks?");
                if (isConfirm == true) {
                    $.ajax({
                        type: "POST",
                        url: "AcceptingAuthRemarksSave.htm?parid=" + $('#parid').val() + "&taskid=" + $('#taskid').val() + "&acceptingAuthRemarks=" + $('#sltAcceptingAuthorityRemarks').val(),
                        dataType: "json"
                    }).done(function(serverResponse) {
                        $.messager.alert(serverResponse.msgType, serverResponse.msg);
                        if (serverResponse.msg == "Accepted") {
                            $('#dg').datagrid('reload');
                        }
                    });
                } else {

                }
            }

            function detailsCombo() {
                openWindow('taskDetailView.htm?taskId=' + $('#taskid').val() + '&auth=');
            }


            function openWindow(linkurl) {
                $("#win").window("open");
                $("#win").window("setTitle", "Task List");
                $("#winfram").attr("src", linkurl);
            }

            function callNoImage() {

                var userPhoto = document.getElementById('loginUserPhoto');
                userPhoto.src = "images/NoEmployee.png";

            }
            function UploadImage() {
                var url = 'fileUploadForm.htm';
                $.colorbox({href: url, iframe: true, open: true, width: "70%", height: "50%", overlayClose: false, onClosed: refreshImage});
            }

            function refreshImage() {
                $("#loginUserPhoto").attr('src', 'displayprofilephoto.htm?empid=' +${users.empId} + '&date=' + (new Date()).getTime());
            }

            function changepassword() {
                $('#changpwddlg').dialog('open');
            }
            function saveChangePassword() {
                $.post('ChangePasswordAction.htm', $('#changpwdfm').serialize()).done(function(data) {
                    $('#msgspan').text(data.msg);
                });
            }
            function changemobile() {
                var url = 'ChangeMobile.htm?empId=' + $('#empid').val() + '&newmobile=';
                $.colorbox({href: url, iframe: true, open: true, width: "50%", height: "30%"});
            }
            function changeAadhaar() {
                var url = 'ChangeAadhaar.htm?empId=' + $('#empid').val() + '&newAadhaar=';
                $.colorbox({href: url, iframe: true, open: true, width: "50%", height: "30%"});
            }
            function addTab(title, url) {
                if ($('#tt').tabs('exists', title)) {
                    $('#tt').tabs('select', title);
                } else {
                    var content = '<iframe scrolling="auto" frameborder="0" src="' + url + '" style="width:100%;height:100%;"><\/iframe>';
                    $('#tt').tabs('add', {
                        title: title,
                        content: content,
                        closable: true
                    });
                }
            }
            function doSearch() {

                var parval = $('#parstatus').combobox('getValue');
                var parvalText = $('#parstatus').combobox('getText');
                var process = $('#sltProcess').combobox('getValue');
                var processText = $('#sltProcess').combobox('getText');
                var empname = $('#txtEmpname').val();
                var gpfno = $('#txtGPFNo').val();

                if (parval == parvalText) {
                    alert("Invalid Status Selected");
                    return false;
                }
                if (process == processText) {
                    alert("Invalid Task Selected");
                    return false;
                }
                if (parval == "9") {
                    $('#viewcompleted').show();
                }

                if (empname != '') {
                    //process = "";
                    //parval = "";
                }
                if (gpfno != '') {
                    empname = "";
                    //process = "";
                    //parval = "";
                }

                $('#dg').datagrid('load', {
                    parstatus: parval,
                    processId: process,
                    empName: empname,
                    gpf: gpfno
                });
            }
			//new code added
            $(document).ready(function() {

                
                //if close button is clicked
                $('.window .close').click(function(e) {
                    //Cancel the link behavior
                    e.preventDefault();

                    $('#mask').hide();
                    $('.window').hide();
                });

                //if mask is clicked
                $('#mask').click(function() {
                    $(this).hide();
                    $('.window').hide();
                });

            });
            function showStateLevelDialog()
            {
                var id = '#dialog';

                //Get the screen height and width
                var maskHeight = $(document).height();
                var maskWidth = $(window).width();

                //Set heigth and width to mask to fill up the whole screen
                $('#mask').css({'width': maskWidth, 'height': maskHeight});

                //transition effect		
                $('#mask').fadeIn(500);
                $('#mask').fadeTo("slow", 0.9);

                //Get the window height and width
                var winH = $(window).height() - 100;
                var winW = $(window).width();

                //Set the popup window to center
                $(id).css('top', winH / 2 - $(id).height() / 2);
                $(id).css('left', winW / 2 - $(id).width() / 2);
                if ($('#hasFilled').val() == "true")
                {
                    $('#mask').hide();
                    $('.window').hide();

                }
                else
                {
                    //transition effect
                    $(id).fadeIn(2000);
                }   
            }
            function validateExpertise()
            {
                if (objE.areaOfExpertise.value == '')
                {
                    alert("Please enter Area of Expertise.");
                    objE.areaOfExpertise.focus();
                    return false;
                }
                if (objE.areaOfInterest.value == '')
                {
                    alert("Please enter Area of Interest.");
                    objE.areaOfInterest.focus();
                    return false;
                }
                if (objE.volWillingness.selectedIndex == 0)
                {
                    alert("Please select where you are willing to serve as State Level Observer in addition to my normal official duties? Yes/No");
                    objE.volWillingness.focus();
                    return false;
                }
                var postData = $('#frmExpertise').serialize();
                $.ajax({
                    url: 'saveExpertise.htm',
                    type: 'post',
                    data: postData,
                    success: function(retVal) {
                        if (retVal == 'Success')
                        {
                            alert("Thank you for your Interest. Please Click ok to go to your HRMS Dashboard.");
                            $('#mask').hide();
                            $('.window').hide();
                        }
                        else
                        {
                            alert("There is some error while processing your request. Please try again later.");
                            $('#mask').hide();
                            $('.window').hide();
                        }
                    }
                });
            }
        </script>
        <style type="text/css">
            .icon-calendar {
                background: rgba(0, 0, 0, 0) url("images/icon_calendar.png") no-repeat scroll center center;
            }
            .icon-appl {
                background: rgba(0, 0, 0, 0) url("images/application.png") no-repeat scroll center center;
            }
        </style>
    </head>

    <body style="padding:0px;">
	 <!-- Employees Expertise Popup-->        
        <div id="boxes">
            <div style=" left: 551.5px; display: none;" id="dialog" class="window"> 
                <h1 style="font-size:18pt;color:#0A62AA;font-weight:bold;">Enrollment as Sate Level Observer</h1>
                <form name="frmExpertise" id="frmExpertise" method="post" action="saveExtertise.htm">
                    <input type="hidden" name="hasFilled" id="hasFilled" value="${hasFilled}" />
                    <table width="100%" cellspacing="1" cellpadding="4" border="0" bgcolor="#EAEAEA" style="font-size:9pt;">
                        <tr bgcolor="#FFFFFF">
                            <td width="150">
                                1.Name of the Officer:
                            </td>
                            <td>
                                <c:out value="${userinfo.name}"/>
                                <input type="hidden" name="name" value="${userinfo.name}" />
                            </td>
                        </tr>
                        <tr bgcolor="#FFFFFF">
                            <td>
                                2.Designation:
                            </td>
                            <td>
                                <c:out value="${userinfo.designation}"/>
                                <input type="hidden" name="designation" value="${userinfo.designation}" />
                            </td>
                        </tr>
                        <tr bgcolor="#FFFFFF">
                            <td>
                                3.Grade:
                            </td>
                            <td>
                                <c:if test="${not empty userinfo.grade}"><c:out value="${userinfo.grade}"/>
                                    <input type="hidden" name="grade" value="${userinfo.grade}" />
                                </c:if>
                                <c:if test="${empty userinfo.grade}"><input type="text" class="tb10" name="grade" />
                                </c:if>
                            </td> 
                        </tr>
                        <tr bgcolor="#FFFFFF">
                            <td>
                                4.Parent Department:
                            </td>
                            <td>
                                <c:out value="${userinfo.deptname}"/>
                                <input type="hidden" name="deptname" value="${userinfo.deptname}" />
                            </td>
                        </tr>
                        <tr bgcolor="#FFFFFF">
                            <td>
                                5.Present Place of Posting:
                            </td>

                            <td>
                                <c:out value="${userinfo.postingPlace}"/>
                                <input type="hidden" name="postingPlace" value="${userinfo.postingPlace}" />
                            </td>
                        </tr>
                        <tr bgcolor="#FFFFFF">
                            <td>
                                6.Details of Office in which presently stationed:
                            </td>
                            <td>
                                <c:out value="${userinfo.curofficename}"/>
                                <input type="hidden" name="curofficename" value="${userinfo.curofficename}" />
                            </td>              

                        </tr>
                        <tr bgcolor="#FFFFFF">
                            <td>
                                7.Area of Expertise:
                            </td>
                            <td>
                                <textarea rows="3" cols="50" style="width:100%;" class="tb10" name="areaOfExpertise"></textarea>
                            </td>              

                        </tr>
                        <tr bgcolor="#FFFFFF">
                            <td>
                                8.Area of Interest:
                            </td>
                            <td>
                                <textarea rows="3" cols="50" style="width:100%;" name="areaOfInterest" class="tb10"></textarea>
                            </td>              

                        </tr> 
                        <tr bgcolor="#FFFFFF">
                            <td colspan="2">
                                9.Voluntary willingness to serve as State Level Observer in addition to my normal official duties.
                                <select size="1" class="tb10" style="width:100px;" name="volWillingness">
                                    <option value="">-Select-</option>
                                    <option value="Yes">Yes</option>
                                    <option value="No">No</option>
                                </select>
                            </td>
                        </tr>                     
                        <tr bgcolor="#FFFFFF">
                            <td>
                                10.Mobile Number:
                            </td>
                            <td>
                                <c:if test="${not empty userinfo.mobile}"><c:out value="${userinfo.mobile}"/>
                                </c:if>
                                <c:if test="${empty userinfo.mobile}"><input type="text" class="tb10" name="mobile" />
                                </c:if>                             
                            </td>              
                        </tr>
                        <tr bgcolor="#FFFFFF">
                            <td>
                                11.Landline Number:
                            </td>
                            <td>
                                <input type="text" name="landline" class="tb10" />
                            </td>              
                        </tr>
                        <tr bgcolor="#FFFFFF">
                            <td>
                                12.Office Phone Number:
                            </td>
                            <td>
                                <input type="text" name="officePhone" class="tb10" />
                            </td>              
                        </tr>
                        <tr bgcolor="#FFFFFF">
                            <td>
                                13.Email Address:
                            </td>
                            <td>
                                <c:if test="${not empty userinfo.emailid}"><c:out value="${userinfo.emailid}"/>
                                    <input type="hidden" name="emailid" value="${userinfo.emailid}" />
                                </c:if>
                                <c:if test="${empty userinfo.emailid}"><input type="text" class="tb10" name="emailid" />
                                </c:if>                            
                            </td>              
                        </tr>                    
                    </table>
                    <div id="popupfoot"> <input type="button" id="btnExpertise" class="btn pri" value="Save & Close" onclick="javascript:validateExpertise()" />
                        <a href="#" class="close agree btn pri" style="background:#FF0000;color:#FFFFFF;">Remind me Later</a> 
                    </div>
                </form>
                <script type="text/javascript">
                    var objE = document.frmExpertise;
                </script>
            </div>
            <div style="width: 1478px; font-size: 32pt; color:white; height: 602px; display: none; opacity: 0.8;" id="mask"></div>
        </div>
        <!-- Employees Expertise Popup Ends-->
        <form id= "myform" action="login.htm">
            <jsp:include page="topbanner.jsp"/>
            <input type="hidden" name="empId" value="${empId}" id="empid"/>
			<input type="hidden" name="curip" value="${curip}" id="curip" />
            <div style="padding:10px;">
                <div id="cc_layout" class="easyui-layout" style="width:100%;height:700px;">
                    <div data-options="region:'center',split:true" title="Dash Board">
                        <div id="tt" class="easyui-tabs" border="false" plain="true" fit="true" style="width:100%;height:680px;">
                            <div title="Home">
                                <div style="padding:5px;"></div>
                                <div id="tb" style="padding:3px;text-align:center;">
                                    <label>Select Task </label>
                                    <input class="easyui-combobox" id="sltProcess" name="sltProcess" style="width:20%" data-options="valueField:'value',textField:'label',url:'GetWorkflowProcessJSONData.htm'">
                                    <label> Select Status </label>
                                    <input id="parstatus" name="parstatus" class="easyui-combobox" style="width:40%" data-options="
                                           valueField: 'value',
                                           textField: 'label',
                                           data: [{
                                           label: 'All',
                                           value: ''
                                           },{
                                           label: 'PENDING AS REPORTING AUTHORITY',
                                           value: '6'
                                           },{
                                           label: 'PENDING AS REVIEWING AUTHORITY',
                                           value: '7'
                                           },{
                                           label: 'PENDING AS ACCEPTING AUTHORITY',
                                           value: '8'
                                           },{
                                           label: 'MY COMPLETED TASKS',
                                           value: '9'
                                           }]" />
                                    <a href="javascript:void(0)" class="easyui-linkbutton" onclick="doSearch()" plain="true">Search</a>
                                    <%
                                        completedtasklink = "CompletedTask.htm";
                                    %>
                                    <a href='<%=completedtasklink%>' id="viewcompleted" class="easyui-linkbutton" target="_blank">Print Report</a>
                                    <br /><label>Employee Name</label>
                                    <input type="text" id="txtEmpname" class="easyui-textbox" size="30"/>
                                    <label>GPF No</label>
                                    <input type="text" id="txtGPFNo" class="easyui-textbox"/>
                                </div>
                                <table id="dg" class="easyui-datagrid" style="width:100%;height:360px;" title="My Task"
                                       rownumbers="true" pagination="true" singleSelect="true"
                                       data-options="singleSelect:true,collapsible:true,fitColumns:true" toolbar="#tb">
                                    <thead>
                                        <tr>
                                            <th data-options="field:'taskId'">Task ID</th>
                                            <th data-options="field:'processname'">Task name</th>
                                            <th data-options="field:'applicant'">Initiated By</th>
                                            <th data-options="field:'dateOfInitiationAsString'">Initiated On</th>
                                            <th data-options="field:'status'">Status</th>
                                            <th data-options="field:'istaskcompleted',align:'center',formatter:quickView">Action</th>
                                            <th data-options="field:'auth',hidden:'true'">Authority Type</th>
                                            <th data-options="field:'statusId',hidden:'true'">Status Id</th>
                                        </tr> 
                                    </thead>
                                </table>
                                <script type="text/javascript">
                                    $(function() {
                                        var pager = $('#dg').datagrid('getPager'); // get the pager of datagrid
                                        pager.pagination({
                                            buttons: [{
                                                    iconCls: 'icon-edit',
                                                    handler: function() {
                                                        var row = $('#dg').datagrid('getSelected');

                                                        if (row == null) {
                                                            alert('Please Select row from Task List. ');
                                                            return false;
                                                        } else {
                                                            var auth1 = row.auth;
                                                            var statusid = row.statusId;
                                                            //openWindow('JSP/TaskAction.do?taskId='+row.taskId+'&submit=View&auth='+auth1);
                                                            if (statusid != '21') {
                                                                openWindow('taskDetailView.htm?processid=' + row.processId + '&taskId=' + row.taskId + '&auth=' + auth1);
                                                            } else if (statusid == '21') {
                                                                if ((auth1 != "REPORTING") && (auth1 != "REVIEWING") && (auth1 != "ACCEPTING")) {
                                                                    openWindow('JSP/ParApplyDispAction.do?submit=AdverseReport&taskid=' + row.taskId);
                                                                } else {
                                                                    openWindow('JSP/TaskAction.do?taskId=' + row.taskId + '&submit=View&auth=' + auth1);
                                                                }
                                                            }
                                                        }
                                                    }

                                                }]
                                        });
                                        $('#dg').datagrid({
                                            view: detailview,
                                            detailFormatter: function(index, row) {
                                                return '<div class="ddv" style="padding:5px 0;background:#E5F0C9;"><\/div>';
                                            },
                                            onExpandRow: function(index, row) {
                                                //alert("Status ID is:" +row.statusId);
                                                if (row.statusId == 9) {
                                                    alert("This facility is unavailable.");
                                                    return false;
                                                } else if (row.statusId != 8) {
                                                    alert("This facility is available for Accepting Authority only.");
                                                    return false;
                                                } else {
                                                    var ddv = $(this).datagrid('getRowDetail', index).find('div.ddv');
                                                    ddv.panel({
                                                        height: 60,
                                                        border: false,
                                                        cache: false,
                                                        href: 'AcceptingAuthRemarksPage.htm?taskid=' + row.taskId,
                                                        onLoad: function() {
                                                            $('#dg').datagrid('fixDetailRowHeight', index);
                                                        }
                                                    });
                                                    $('#dg').datagrid('fixDetailRowHeight', index);
                                                }
                                            }
                                        });
                                    })
                                    function quickView(val, row) {
                                        var auth1 = row.auth;
                                        var statusid = row.statusId;
                                        var url = "";
                                        if (statusid != '21') {
                                            url = 'taskDetailView.htm?taskId=' + row.taskId + '&auth=' + auth1;
                                        } else if (statusid == '21') {
                                            if ((auth1 != "REPORTING") && (auth1 != "REVIEWING") && (auth1 != "ACCEPTING")) {
                                                url = 'JSP/ParApplyDispAction.do?submit=AdverseReport&taskid=' + row.taskId;
                                            } else {
                                                url = 'taskRedirectAction.htm?taskId=' + row.taskId + '&auth=' + auth1;
                                            }
                                        }
                                        //var url ='JSP/TaskAction.do?taskId='+row.taskId+'&submit=View&auth='+auth1;
                                        return "<a href='javascript:void(0)' onclick='openWindow(\"" + url + "\")'><img src='images/action.png' width='16' height='16'></a>"
                                    }
                                </script>

                                <div id="win" class="easyui-window" title="My Window" data-options="modal:true,closed:true,iconCls:'icon-window'" closed="true" style="width:1200px;height:500px;padding:5px;">
                                    <iframe id="winfram" frameborder="0" scrolling="yes" marginheight="0" marginwidth="0" height="100%" width="100%"></iframe>
                                </div>



                                <div class="easyui-layout" data-options="fit:true,split:true">
                                    <div data-options="region:'center',split:true,border:true">
                                        <div class="easyui-layout" data-options="fit:true,split:true">
                                            <div title="Calendar" data-options="region:'west',split:true,border:true,collapsible:false" style="width:258">
                                                <div id="easyuical" class="easyui-calendar" style="width:250px;height:250px;"></div>
                                            </div>

                                            <div title="User Privilege" data-options="region:'center',split:true,border:true" style="width:300">
                                                <c:if test="${users.usertype=='G'}">  
												
                                                    <c:if test="${users.hasmyCadreTab=='Y'}">    
                                                        <a href='JSP/welcome.do?rollId=<%=CommonFunctions.encodedTxt("01")%>' class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-cadre'"> My Cadre Interface </a>
                                                    </c:if>
                                                    <c:if test="${users.hasmyDeptTab=='Y'}">   
                                                        <a href='JSP/welcome.do?rollId=<%=CommonFunctions.encodedTxt("02")%>' class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-department'"> My Department Interface </a>
                                                        <a href="GradeWiseReport.do?submit=Search" style="font-size: 14px;font-weight: bold;" target="_blank" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-par'"> Posting Details </a>
                                                    </c:if>
                                                    <c:if test="${users.hasmyDistTab=='Y'}">   
                                                        <a href='JSP/welcome.do?rollId=<%=CommonFunctions.encodedTxt("03")%>' class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-district'"> My District Interface </a>
                                                    </c:if>
                                                    <c:if test="${users.hasmyHodTab=='Y'}">   
                                                        <a href='tabController.htm?rollId=<%=CommonFunctions.encodedTxt("04")%>' class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-hod'"> My Hod Interface </a>
                                                    </c:if>
													<c:if test="${users.hasCommandandAuthPriv=='Y'}">  
                                                        <a href="javascript:void(0)" style="font-size: 14px;font-weight: bold;"  class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-cadre'" onclick="$('#cc_layout').layout('collapse', 'west');
                                                                    addTab('Grievance', 'adminGrievanceList.htm')">Grievance List</a>
                                                    </c:if>  
													<c:if test="${users.hasPayRevisionAuth=='Y'}">   
                                                        <a href="javascript:void(0)" style="font-size: 14px;font-weight: bold;"  class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-par'" onclick="$('#cc_layout').layout('collapse','west');addTab('Third Schedule', 'ThirdScheduleEmpList.htm')">Third Schedule List</a>
                                                    </c:if>
													<c:if test="${users.hascheckingAuth=='Y'}">   
                                                       <a href="javascript:void(0)" style="font-size: 14px;font-weight: bold;"  class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-par'" onclick="$('#cc_layout').layout('collapse', 'west');
                                                                addTab('Third Schedule Checking Auth', 'ThirdScheduleCheckingAuthEmpList.htm')">Third Schedule Checking Auth List</a>

                                                    </c:if>
													 <c:if test="${users.hasverifyingAuth=='Y'}">   
                                                        <a href="javascript:void(0)" style="font-size: 14px;font-weight: bold;"  class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-par'" onclick="$('#cc_layout').layout('collapse', 'west');
                                                                addTab('Third Schedule Verifying Auth', 'ThirdScheduleVerifyingAuthEmpList.htm')">Third Schedule Verifying Auth List</a>

                                                    </c:if>
                                                    <c:if test="${users.haspoliceDGTab=='Y'}">   
                                                        <a href='JSP/PoliceDG.do?submit=View' class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-office'" target="_blank"> Police DG Interface </a>
                                                    </c:if>
													 <c:if test="${users.hasparadminTab=='Y'}">  
                                                        <a href='javascript:void(0)'  onclick="addTab('Par Custdian', 'viewPARAdmin.htm')" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-appl'"> Par Custodian</a>
                                                </c:if>													
                                                    <c:if test="${users.hasmyOfficeTab=='Y'}"> 			
                                                        <a href='tabController.htm?rollId=<%=CommonFunctions.encodedTxt("05")%>' class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-office'"> My Office Interface </a>
                                                        <a href="javascript:void(0)" style="font-size: 14px;font-weight: bold;"  class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-dp'" onclick="addTab('PROCEEDINGS', 'DiscProcedingList.htm')">Disciplinary Proceedings</a>
														<a href="javascript:void(0)" style="font-size: 14px;font-weight: bold;"  class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-par'" onclick="$('#cc_layout').layout('collapse','west');addTab('Office Wise Second Schedule List', 'GetOfficeWiseSecondScheduleEmployeeList.htm')">Office Wise Second Schedule List</a>
														
                                                        
                                                    </c:if>
                                                    
                                                    <a href='http://orissalms.in/SingleSignOn.do?hrmsId=${users.hrmsEncId}' class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-office'" target="_blank"> My Court Cases</a>
                                                    <br><!--<a href='javascript:void(0)'  onclick="addTab('My Training Applications', 'MyTrainingApplication.htm')" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-appl'"> My Training Applications</a><br>-->
                                                    <a href='javascript:void(0)'  onclick="$('#cc_layout').layout('collapse', 'west');addTab('Manage Training', 'ManageTraining.htm')" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-appl'"> Manage Training</a>
                                                </c:if>
                                            </div>
                                            <div title="Request or Submission" data-options="region:'east',split:true,border:true,collapsible:true" style="width:300;">
                                                <c:if test="${users.usertype=='G'}">
                                                    <div style="margin-bottom:10px">
                                                        <a href="javascript:void(0)" style="font-size: 14px;font-weight: bold;"  class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-par'" onclick="$('#cc_layout').layout('collapse', 'west');
                                                                addTab('PAR', 'GetPARList.htm')">Submit Performance Appraisal</a>
														<a href="javascript:void(0)" style="font-size: 14px;font-weight: bold;"  class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-par'" onclick="$('#cc_layout').layout('collapse','west');addTab('Pay Revision Option', 'SecondSchedulePage.htm')">Pay Revision Option</a>
                                                        <!--<a href="javascript:void(0)" style="font-size: 14px;font-weight: bold;"  class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-par'" onclick="$('#cc_layout').layout('collapse', 'west');
                                                                addTab('PAR', 'ParReport.htm?fiscalyear=')">PAR Report</a>-->
                                                        <a href="javascript:void(0)" style="font-size: 14px;font-weight: bold;"  class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-leave'" onclick="$('#cc_layout').layout('collapse', 'west');
                                                                addTab('LEAVE', 'leaveapply.htm')">Apply Leave</a>
                                                        <a href="javascript:void(0)" style="font-size: 14px;font-weight: bold;"  class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-leave'" onclick="$('#cc_layout').layout('collapse', 'west');
                                                                addTab('LOAN', 'loanList.htm')">Apply Loan</a>                                                       
                                                        <a href="javascript:void(0)" style="font-size: 14px;font-weight: bold;"  class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-leave'" onclick="$('#cc_layout').layout('collapse', 'west');
                                                                addTab('PAYSLIP BROWSER', 'PaySlipList.htm')">Pay Slip</a>
                                                        <a href="javascript:void(0)" style="font-size: 14px;font-weight: bold;"  class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-leave'" onclick="$('#cc_layout').layout('collapse', 'west');
                                                                addTab('PROPERTY STATEMENT', 'viewpropertystatementlist.htm')">Property Statement</a>
																
                                                        <!--<a href="javascript:void(0)" style="font-size: 14px;font-weight: bold;"  class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-leave'" onclick="$('#cc_layout').layout('collapse', 'west');
                                                                addTab('LIC REPORT', 'LICReport.htm')">LIC Report</a>-->
                                                        <a href="javascript:void(0)" style="font-size: 14px;font-weight: bold;"  class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-leave'" onclick="$('#cc_layout').layout('collapse', 'west');
                                                                addTab('Apply Quarter', 'http://equarters.nic.in/SSL_pages/Https_Public/USERSignUp.aspx?hrmsid=${users.empId}')">Apply Quarter</a>
														<a href="javascript:void(0)" style="font-size: 14px;font-weight: bold;"  class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-leave'" onclick="$('#cc_layout').layout('collapse', 'west');
                                                            addTab('Grievance', 'employeeGrievanceList.htm')">Grievance</a>
                                                       <!-- <a href="javascript:void(0)" style="font-size: 14px;font-weight: bold;"  class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-par'" onclick="$('#cc_layout').layout('collapse', 'west');
                                                                addTab('Employee Information', 'GetEmployeeInformationPage.htm')">Employee Information</a>-->

                                                        <!-- <a href="javascript:void(0)" style="font-size: 14px;font-weight: bold;"  class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-leave'" onclick="addTab('LOAN SANCTION', 'loansanction.htm')">Loan Sanction</a>-->
                                                        <!--<br /><a href="javascript:void(0)" style="font-size: 14px;font-weight: bold;"  class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-leave'"  onclick="addTab('Training Calendar', 'TrainingController.htm')">Training Calendar</a>-->
                                                        <c:if test = "${isEligible == true}"><br /><a href="javascript:void(0)" style="font-size: 14px;font-weight: bold;color:#008900;"  class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-calendar'"  onclick="$('#cc_layout').layout('collapse', 'west');
                                                                addTab('Apply for NISG Training', 'ApplyNISGTraining.htm')">Apply for NISG Training</a></c:if>
														<br />
                                                        <c:if test="${users.hasmyOfficeTab=='Y'}"> 
                                                            <!--<a href="javascript:void(0)" style="font-size: 14px;font-weight: bold;"  class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-add'" onclick="addTab('Increment Proposal', 'displayProposalListpage.htm')">Increment Proposal</a>-->
                                                        </c:if>
														<a href="javascript:void(0)" style="font-size: 14px;font-weight: bold;"  class="easyui-linkbutton" data-options="href:'profile.html',plain:true,iconCls:'icon-leave'" onclick="$('#cc_layout').layout('collapse', 'west');
                                                                addTab('ONLINE TICKET', 'onlineticketlist.htm')">Online Ticket
                                                        </a>
														<a href="javascript:void(0)" style="font-size: 14px;font-weight: bold;"  class="easyui-linkbutton" data-options="href:'profile.html',plain:true,iconCls:'icon-leave'" onclick="$('#cc_layout').layout('collapse','west');addTab('Service Book', 'servicebook.htm')">Service Book</a>
                                                    </div>
													<div style="margin-bottom:10px">
                                                            <a href="javascript:void(0)" style="font-size: 14px;font-weight: bold;"  class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-leave'" onclick="$('#cc_layout').layout('collapse', 'west');
                                                                addTab('View Loan Account', 'employeeloanaccount.htm')">View Loan Account
															</a><br />
                                                        <a href="javascript: void(0)" onclick="javascript: showStateLevelDialog()" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-leave'" style="color:#FF0000;font-weight:bold;text-decoration:none;">Enrollment as Sate Level Observer</a>
                                                    </div>
                                                </c:if>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div data-options="region:'west',split:true,collapsible:true" title="My Profile" style="width:500px;">
                        <c:if test="${users.usertype=='M'}">
                            <table border="0" cellpadding="1" cellspacing="0" width="99%" style="margin-left:5px;font-size:12px;">                  

                                <tr style="height:40px;">
                                    <td colspan="2" >
                                        <span style="font-size:13px;font-weight:bold;">
                                            <c:out value="${users.fullName}"/>
                                        </span>
                                        <hr style="border:1px solid #a3a183;"/>
                                    </td>
                                    <td rowspan="8" valign="top">
                                        <img id="loginUserPhoto" style="border:1px solid #a3a183;padding:3px;" onerror="callNoImage()"  alt="ProfileImage" src='displayprofilephoto.htm?empid=${users.empId}' width="100" height="100" />
                                        </br>
                                        <a href="javascript:UploadImage(0)" class="atag"> Upload Photo</a><br />
                                        <a href="javascript:changepassword();" class="alink">Change Password</a>
                                    </td>
                                </tr>
                                <tr style="height:40px;">
                                    <td width="20%">&nbsp; </td>
                                    <td width="80%"><span style="font-size:12px;font-weight:bold;"> <c:out value="${users.designation}"/> </span></td>
                                </tr>
                                <tr style="height:40px;">
                                    <td>&nbsp; </td>
                                    <td><span style="font-size:12px;font-weight:bold;"> <c:out value="${users.offName}"/> </span></td>
                                </tr>

                                <tr style="height:40px;">
                                    <td>&nbsp;</td>
                                    <td>
                                        <span style="font-size:12px;font-weight:bold;"> <c:out value="${users.mobile}"/> </span>
                                        <a href="javascript:changemobile();"> Add/Modify Mobile Number</a>
                                    </td>
                                </tr>
                                <tr style="height:40px;">
                                    <td>&nbsp;</td>
                                    <td>&nbsp;</td>
                                </tr>
                                <tr style="height:40px;">
                                    <td>&nbsp;</td>
                                    <td>&nbsp;</td>
                                </tr>                  
                            </table>
                        </c:if>
                        <c:if test="${users.usertype=='G'}">
                            <table border="0" cellpadding="1" cellspacing="0" width="99%" style="margin-left:5px;font-size:12px;">                  

                                <tr style="height:40px;">
                                    <td colspan="2" ><span style="font-size:13px;font-weight:bold;"> <c:out 
                                                value="${users.fullName}"/> </span>
                                        <hr style="border:1px solid #a3a183;"/></td>
                                    <td rowspan="8" valign="top">
                                        <div id="my_camera"></div>
                                        <div id="webcam"></div>
                                        <div id="my_result">
                                            <img id="loginUserPhoto" style="border:1px solid #a3a183;padding:3px;" onerror="callNoImage()"  alt="ProfileImage" src='displayprofilephoto.htm?empid=${users.empId}' width="100" height="100" />
                                        </div>
                                        <a href="javascript:void(take_snap())" id="activewebcam"> Take Snapshot </a><br/>
                                        <a href="javascript:UploadImage(0)" class="atag"> Upload Photo</a><br/>
                                        <a href="javascript:void(captureSnap())" id="capturesnap"> Capture Photo </a><br/>
                                        <a href="javascript:changepassword();" class="alink">Change Password</a>
                                    </td>
                                </tr>
                                <tr style="height:40px;">
                                    <td width="20%">HRMS ID </td>
                                    <td width="80%">
                                        <span style="font-size:12px;font-weight:bold;"> 
                                            <c:out value="${users.empId}"/>
                                        </span>
                                    </td>
                                </tr>
                                <tr style="height:40px;">
                                    <td width="20%">GPF / PRAN </td>
                                    <td width="80%">
                                        <span style="font-size:12px;font-weight:bold;"> 
                                            <span style="color:#008000;font-weight: bold;" >(<c:out value="${users.acctType}"/>) </span><c:out value="${users.gpfno}"/>
                                        </span>
                                    </td>
                                </tr>
                                <tr style="height:40px;">
                                    <td>Date of Birth</td> 
                                    <td><span style="font-size:12px;font-weight:bold;"> <c:out value="${users.formattedDob}"/> 
                                        </span></td>
                                </tr> 
                                <tr style="height:40px;">
                                    <td>Joined On </td>
                                    <td>
                                        <span style="font-size:12px;font-weight:bold;"><c:out value="${users.formattedDoegov}"/> 
                                        </span> 
                                    </td>

                                </tr>
                                <tr style="height:40px;">
                                    <td>Post Group</td>
                                    <td>
                                        <span style="font-size:12px;font-weight:bold;"><c:out value="${users.postgrp}"/> 
                                        </span> 
                                    </td>

                                </tr>
                                <tr style="height:40px;">
                                    <td>Post </td>
                                    <td>
                                        <span style="font-size:12px;font-weight:bold;">
                                            <c:out  value="${users.postname}"/> 
                                        </span>

                                    </td>

                                </tr>
                                <tr style="height:40px;">
                                    <td>Office </td>
                                    <td><span style="font-size:12px;font-weight:bold;"> <c:out 
                                                value="${users.offname}"/></span></td>
                                </tr>

                                <tr style="height:40px;">
                                    <td>Cadre </td>
                                    <td> <span style="font-size:12px;font-weight:bold;"><c:out 
                                                value="${users.cadrename}"/> </span>
                                    </td>
                                </tr>

                                <tr style="height:40px;">
                                    <td>Mobile No</td>
                                    <td>
										<c:if test="${not empty users.mobile}">
											<span style="font-size:12px;font-weight:bold;"><c:out value="${users.mobile}"/></span>
										</c:if>
                                        <c:if test="${empty users.mobile}">    
											<a href="javascript:changemobile();"> Add Mobile Number</a>
										</c:if>
                                    </td>
                                </tr>   
                                <tr style="height:40px;">
                                    <td>Aadhaar No</td>
                                    <td>
										<c:if test="${not empty users.aadharno}">
											<span style="font-size:12px;font-weight:bold;"><c:out value="${users.aadharno}"/></span>
                                        </c:if>
										<c:if test="${empty users.aadharno}">
											<a href="javascript:changeAadhaar();"> Add Aadhaar Number</a>
										</c:if>
                                    </td>
                                </tr>   
                            </table>
                        </c:if>  


                    </div>
                </div>
            </div>
        </form>
        <div id="changpwddlg" class="easyui-dialog" title="Change Password" data-options="iconCls:'icon-save',modal:true,closed: true" style="width:700px;height:350px;padding:10px">
            <form action="ChangePasswordAction.htm" id="changpwdfm" data-toggle="validator" role="form">
                <div align="center" style="color: red;"><span id="msgspan"></span></div>
                <div class="easyui-panel" style="width:100%;max-width:700px;padding:10px 20px;"> 
                    <div style="margin-bottom:20px">
                        <input class="easyui-passwordbox" prompt="Current Password" name="userPassword" id="userPassword" label="Current Password:" labelWidth="140" iconWidth="28" style="width:100%;height:34px;padding:10px">
                    </div>                    
                    <div style="margin-bottom:20px">
                        <input class="easyui-passwordbox" prompt="New Password" name="newpassword" id="newpassword" label="New Password:" labelWidth="140" iconWidth="28" style="width:100%;height:34px;padding:10px">
                    </div>
                    <div style="margin-bottom:20px">
                        <input class="easyui-passwordbox" prompt="Confirm Password" name="confirmpassword" id="confirmpassword" label="Confirm Password:" labelWidth="140" iconWidth="28" style="width:100%;height:34px;padding:10px">
                    </div>
                    <div style="margin-bottom:20px">
                        <a href="javascript:saveChangePassword()" class="easyui-linkbutton" iconCls="icon-ok" style="width:100%;height:32px">Change</a>
                    </div>
                    <div style="margin-bottom:20px">
                        <span class="help-block" style="color: red;">Password policy to match 8 characters with alphabets in combination with numbers and special characters. e.g Welcome@12</span>
                    </div>
                </div>
            </form>
        </div>

        <script language="JavaScript" type="text/javascript">
            function take_snap() {
                Webcam.set({
                    width: 220,
                    height: 140,
                    image_format: 'jpeg',
                    jpeg_quality: 90,
                    dest_width: 100,
                    dest_height: 100,
                    flip_horiz: true
                });

                Webcam.attach('#my_camera');
                $("#capturesnap").show();
                $("#my_camera").show();
                $("#activewebcam").hide();
            }
            function captureSnap() {

                Webcam.snap(function(data_uri) {

                    document.getElementById('my_result').innerHTML = '<img src="' + data_uri + '"/>';

                    var raw_image_data = data_uri.replace(/^data\:image\/\w+\;base64\,/, '');
                    //document.getElementById('mydata').value = raw_image_data;

                    Webcam.on('uploadProgress', function(progress) {
                        // Upload in progress
                        // 'progress' will be between 0.0 and 1.0
                    });

                    Webcam.on('uploadComplete', function(code, text) {
                        // Upload complete!
                        // 'code' will be the HTTP response code from the server, e.g. 200
                        // 'text' will be the raw response content
                    });

                    Webcam.upload(data_uri, 'WebCamUpload', function(code, text) {
                        // Upload complete!
                        // 'code' will be the HTTP response code from the server, e.g. 200
                        // 'text' will be the raw response content
                    });
                });

                $("#my_camera").hide();
                $("#capturesnap").hide();
                $("#activewebcam").show();

            }
        </script>
    </body>
</html>