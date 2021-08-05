<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Human Resources Management System, Government of Odisha</title>

        <link rel="stylesheet" type="text/css" href="resources/css/themes/default/easyui.css"/>
        <link rel="stylesheet" type="text/css" href="resources/css/themes/icon.css"/>
        <link rel="stylesheet" type="text/css" href="css/hrmis.css" />
        <link rel="stylesheet" type="text/css" href="css/jquery.datetimepicker.css"/>
        <link href="resources/css/colorbox.css" rel="stylesheet">

        <!-- LAYOUT v 1.3.0 -->
        <script type="text/javascript" src="js/jquery.min-1.9.1.js"></script>
        <script type="text/javascript" src="js/jquery.easyui.min.js"></script>
        <script language="javascript" src="js/jquery.datetimepicker.js" type="text/javascript"></script>
        <script type="text/javascript" src="js/jquery.colorbox-min.js"></script>
        <script language="javascript" src="js/servicehistory.js" type="text/javascript"></script>
        <script language="javascript" src="js/basicjavascript.js" type="text/javascript"></script>
        <script language="javascript" type="text/javascript">
            $(document).ready(function() {

                $('#headqtr').bind('copy paste cut', function(e) {
                    e.preventDefault(); //disable cut,copy,paste.
                    alert('cut,copy & paste options are disabled !!');
                });

                var fisclyear = $('#fiscalyear').val();
                var finyear = fisclyear.split("-");
                var min_year = finyear[0];
                var max_year = parseInt(finyear[0]) + 1;

                //Start - Current Date
                var curdate = new Date();
                var dd = curdate.getDate();
                var mm = curdate.getMonth() + 1; //January is 0!
                var yyyy = curdate.getFullYear();
                //End - Current Date

                var maxDPDate;
                if (max_year <= yyyy) {
                    maxDPDate = new Date(max_year, 2, 31);
                } else if (max_year > yyyy) {
                    maxDPDate = new Date(yyyy, mm, dd);
                }

                $('.txtDate').datetimepicker({
                    timepicker: false,
                    format: 'd-M-Y',
                    minDate: new Date(min_year, 3, 1),
                    //maxDate: new Date(max_year, 2, 31),
                    maxDate: maxDPDate,
                    closeOnDateSelect: true,
                    validateOnBlur: false
                });

                $('#empDepartment').combobox('clear');
                $('#empOffice').combobox('clear');
                $('#empPost').combobox('clear');

                $('#empDepartment').combobox({
                    url: 'getDeptListJSON.htm',
                    onSelect: function(record) {
                        $('#empOffice').combobox('clear');
                        $('#empPost').combobox('clear');
                        var url = 'getOfficeListJSON.htm?deptcode=' + record.deptCode;
                        $('#empOffice').combobox('reload', url);
                    }
                });
                $('#empOffice').combobox({
                    onSelect: function(record) {
                        $('#empPost').combobox('clear');
                        var url = 'getOfficeWithSPCList.htm?offcode=' + record.offCode;
                        $('#empPost').combobox('reload', url);
                    }
                });
                
                $('#cadreDepartment').combobox('clear');
                $('#cadreName').combobox('clear');
                
                $('#cadreDepartment').combobox({
                    url: 'getDeptListJSON.htm',
                    onSelect: function(record) {
                        $('#cadreName').combobox('clear');
                        var url = 'getCadreListJSON.htm?deptcode=' + record.deptCode;
                        $('#cadreName').combobox('reload', url);
                    }
                });
            });

            function changepost() {
                $('#winsubstantivepost').window('open');
            }
            
            function addCadre(){
                $('#wincadre').window('open');
            }
            
            function getPost() {
                $('#hidspc').val($('#empPost').combobox('getValue'));
                $("#post").text($('#empPost').combobox('getText'));
                
                $('#empDepartment').combobox('clear');
                $('#empOffice').combobox('clear');
                $('#empPost').combobox('clear');

                $('#winsubstantivepost').window('close');
            }
            
            function getCadre(){
                $('#cadre').text($('#cadreName').combobox('getText'));
                $('#cadreCode').val($('#cadreName').combobox('getValue'));
                $('#wincadre').window('close');
            }
            
            function SelectSpn(offCode, spc, offName, authName)
            {
                $.colorbox.close();
                $('#hidspc').val(spc);
                $('#hidOffice').val(offCode);
                $("#office").html(offName);
                $("#post").text(authName);
            }

            function saveCheck() {
                if ($('#txtFromDate').val() == '') {
                    alert("Please enter From Date");
                    return false;
                }
                if ($('#txtToDate').val() == '') {
                    alert("Please enter To Date");
                    return false;
                }
                if ($('#hidspc').val() == '') {
                    alert("Designation cannot be blank.");
                    return false;
                }
                if ($('#cadreCode').val() == '') {
                    alert("Service to which the officer belongs cannot be blank.");
                    return false;
                }

                if (($('#txtFromDate').val() != '') && ($('#txtToDate').val() != '')) {
                    var ftemp = $("#txtFromDate").val().split("-");
                    var ttemp = $("#txtToDate").val().split("-");
                    var fdt = new Date(ftemp[2], monthint(ftemp[1].toUpperCase()), ftemp[0]);
                    var tdt = new Date(ttemp[2], monthint(ttemp[1].toUpperCase()), ttemp[0]);
                    if (fdt > tdt) {
                        alert("From Date must be less than To Date");
                        return false;
                    }
                }
                return true;
            }
        </script>
    </head>
    <body style="padding:0px;">
        <table style="font-family:Verdana;" cellpadding="0" cellspacing="0">
            <tr>
                <td style="width:253px;">
                    <div style="border-width:1px;width:251px;font-weight:bold;color:#333;border-color:#d7d7d7;border-style:solid; border-width:1px 1px 0 1px;padding:10px 0px 10px 0px;text-align:center;">
                        Personal Information
                    </div>
                </td>
                <td style="width:253px;">
                    <div style="border-width:1px;width:251px;color:#333;border-color:#d7d7d7;border-style:solid; border-width:1px 1px 1px 0;padding:10px 0px 10px 0px;text-align:center;">
                        Absentee Statement
                    </div>
                </td>
                <td style="width:253px;">
                    <div style="border-width:1px;width:251px;color:#333;border-color:#d7d7d7;border-style:solid; border-width:1px 1px 1px 0;padding:10px 0px 10px 0px;text-align:center;">
                        Achievements
                    </div>
                </td>
                <td style="width:253px;">
                    <div style="border-width:1px;width:251px;color:#333;border-color:#d7d7d7;border-style:solid; border-width:1px 1px 1px 0;padding:10px 0px 10px 0px;text-align:center;">
                        Other Details
                    </div>
                </td>
            </tr>
        </table>
        <form action="addPAR.htm" method="POST" commandName="ParMastForm">
            <input type="hidden" name="fiscalyear" id="fiscalyear" value='${parMastForm.fiscalyear}'/>
            <input type="hidden" name="hidparid" id="hidparid" value='${parMastForm.parid}'/>
            <input type="hidden" name="parstatus" id="parstatus" value='${parMastForm.parstatus}'/>
            <input type="hidden" name="taskid" id="taskid" value='${parMastForm.taskid}'/>

            <div align="center" style="margin-top:5px;margin-bottom:10px;">
                <div align="center" class="easyui-panel" title="Financial Year">
                    <table border="0" width="100%" cellpadding="0" cellspacing="0" style="padding-left:20px;">
                        <tbody>
                            <tr style="height:40px">
                                <td align="left" width="15%">
                                    Financial Year : <b><c:out value="${parMastForm.fiscalyear}"/></b>
                                </td>
                                <td align="center" width="15%">For The Period From:</td>
                                <td align="center" width="15%">
                                    <fmt:formatDate var="frmDate" value="${parMastForm.periodfrom}" pattern="dd-MMM-yyyy"/>
                                    <input type="text" name="periodfrom" id="txtFromDate" readonly="true" class="txtDate" value='${frmDate}'/>
                                </td>
                                <td align="center" width="5%">To:</td>
                                <td align="left" width="50%">
                                    <fmt:formatDate var="toDate" value="${parMastForm.periodto}" pattern="dd-MMM-yyyy"/>
                                    <input type="text" name="periodto" id="txtToDate" readonly="true" class="txtDate" value='${toDate}'/>
                                </td>
                            </tr>
                            <tr>
                                <td colspan="5">
                                    <span style="display:block;text-align:center;color:red;"><c:out value="${parerrmsg}"/></span>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
            <div align="center">
                <div align="center" class="easyui-panel" title="Personal Information">
                    <table border="0" width="100%" cellpadding="0" cellspacing="0" style="padding-left:20px;font-family:Verdana;font-size:14px;" >
                        <tr style="height:40px;">
                            <td width="30%">
                                <b>HRMS ID</b>
                            </td>
                            <td width="20%">&nbsp;</td>
                            <td width="50%">
                                <c:out value="${users.empId}"/>
                                <input type="hidden" name="empid" value='${users.empId}'/>
                            </td>
                        </tr>
                        <tr style="height:40px;">
                            <td>
                                <b>Full name of the officer</b>
                            </td>
                            <td>&nbsp;</td>
                            <td>
                                <c:out value="${users.fullName}"/>
                            </td>
                        </tr>
                        <tr style="height:40px;">
                            <td>
                                <b>Date of birth</b>
                            </td>
                            <td>&nbsp;</td>
                            <td>
                                <fmt:formatDate var="dob" value="${users.dob}" pattern="dd-MMM-yyyy"/>
                                <c:out value="${dob}"/>
                            </td>
                        </tr>
                        <tr style="height:40px;">
                            <td>
                                <b>Service to which the officer belongs</b>
                            </td>
                            <td>&nbsp;</td>
                            <td>
                                <span id="cadre">
                                    <c:out value="${users.cadrename}"/>
                                </span>
                                <input type="hidden" id="cadreCode" name="cadreCode" value='${users.cadrecode}'/>
                                <c:if test="${empty users.cadrecode}">
                                    &nbsp;&nbsp;
                                    <a href="javascript:void(0)" id="change" onclick="javascript:addCadre();">
                                        <button type="button">Add Cadre</button>
                                    </a>
                                </c:if>
                            </td>
                        </tr>
                        <tr style="height:40px;">
                            <td>
                                <b>Group to which the officer belongs</b>
                            </td>
                            <td>&nbsp;</td>
                            <td>
                                <c:out value="${users.postgrp}"/>
                                <input type="hidden" name="postGrp" value='${users.postgrp}'/>
                            </td>
                        </tr>
                        <tr style="height:40px;">
                            <td>
                                <b>Designation during the period of report</b>
                            </td>
                            <td>&nbsp;</td>
                            <td>
                                <c:if test="${not empty parMastForm.spc}">
                                    <span id="post">
                                        <c:out value="${parMastForm.spn}"/>
                                    </span>
                                    <input type="hidden" name="spc" id="hidspc" value='${parMastForm.spc}'/>
                                </c:if>
                                <c:if test="${empty parMastForm.spc}">
                                    <span id="post">
                                        <c:out value="${users.spn}"/>
                                    </span>
                                    <input type="hidden" name="spc" id="hidspc" value='${users.curspc}'/>
                                </c:if>
                                &nbsp;&nbsp;
                                <a href="javascript:void(0)" id="change" onclick="javascript:changepost();">
                                    <button type="button">Change</button>
                                </a>
                            </td>
                        </tr>
                        <tr style="height:40px;">
                            <td><b>Office to where posted</b></td>
                            <td>&nbsp;</td>
                            <td>
                                <c:if test="${empty parMastForm.offCode}">
                                    <span id="office">
                                        <c:out value="${users.offname}"/>
                                    </span>
                                    <input type="hidden" name="offCode" id="hidOffice" value='${users.offcode}'/>
                                </c:if>    
                                <c:if test="${not empty parMastForm.offCode}">
                                    <span id="office">
                                        <c:out value="${parMastForm.offname}"/>
                                    </span>
                                    <input type="hidden" name="offCode" id="hidOffice" value='${parMastForm.offCode}'/>
                                </c:if>
                            </td>
                        </tr>
                        <tr style="height:40px;">
                            <td><b>Head Quarter(if any)</b></td>
                            <td>
                                &nbsp; 
                            </td>
                            <td>
                                <input type="text" name="headqtr" id="headqtr" value='${parMastForm.headqtr}' size="40" onkeypress="return onlyCharacters(event)"/> 
                            </td>
                        </tr>
                    </table>
                </div>
            </div>
            <div align="right">
                <div style="margin-top:10px;margin-bottom:0px;" class="easyui-panel" align="right">
                    <input type="hidden" name="pageno" value="1"/>
                    <table border="0" width="100%">
                        <tr>
                            <td width="50%" align="left" style="padding-left:20px;">
                                <input type="submit" name="newPar" value="Back" class="easyui-linkbutton"/>
                                <c:if test="${parMastForm.parid != 0 && isClosed == 'N'}">
                                    <c:if test="${parMastForm.parstatus == 0}">
                                        &nbsp;&nbsp;&nbsp;
                                        <input type="submit" name="newPar" value="Delete" class="easyui-linkbutton" onclick="return confirm('Are you sure to delete the PAR?')"/>
                                    </c:if>
                                </c:if>
                            </td>
                            <td width="50%" align="right" style="padding-right:20px;">
                                <c:if test="${isClosed != 'Y'}">
                                    <c:if test="${parMastForm.parstatus >= 18}">
                                        <input type="submit" name="newPar" value="Update" class="easyui-linkbutton" id="nextbtn" onclick="return saveCheck();"/>
                                    </c:if>
                                    <c:if test="${parMastForm.parstatus < 18}">
                                        <input type="submit" name="newPar" value="Next" class="easyui-linkbutton" id="nextbtn" onclick="return saveCheck();"/>
                                    </c:if>
                                </c:if>
                                <c:if test="${isClosed == 'Y'}">
                                    <span style="display:block;text-align:center;color:red;">
                                        PAR for Financial year <c:out value="${parMastForm.fiscalyear}"/> is closed.
                                    </span>
                                </c:if>
                            </td>
                        </tr>
                    </table>
                </div>
            </div>

            <%-- Start - Content for Child Window --%>
            <div id="winsubstantivepost" class="easyui-window" title="Search" style="width:700px;height:400px;top:50px;padding:10px 20px" closed="true" buttons="#searchdlg-buttons"
                 data-options="iconCls:'icon-search',modal:true">
                <table width="100%" border="0" cellpadding="0" cellspacing="0">
                    <tr style="height:40px;">
                        <td width="20%">Department</td>
                        <td width="80%">
                            <input name="empDepartment" id="empDepartment" class="easyui-combobox" style="width:500px;" data-options="valueField:'deptCode',textField:'deptName',editable:false" />
                        </td>
                    </tr>
                    <tr style="height:40px;">
                        <td>Office Name</td>
                        <td>
                            <input name="empOffice" id="empOffice" class="easyui-combobox" style="width:500px;" data-options="valueField:'offCode',textField:'offName',editable:false"/>
                        </td>
                    </tr>
                    <tr style="height:40px;">
                        <td>Post</td>
                        <td>
                            <input name="empPost" id="empPost" class="easyui-combobox" style="width:400px;" data-options="valueField:'spc',textField:'spn',editable:false">
                        </td>
                    </tr>

                    <tr style="height:40px;">
                        <td>&nbsp;</td>
                        <td>
                            <button type="button" onclick="getPost()">Ok</button>
                        </td>
                    </tr>
                </table>
            </div>
            
            <div id="wincadre" class="easyui-window" title="Search" style="width:700px;height:400px;top:50px;padding:10px 20px" closed="true"
                 data-options="iconCls:'icon-search',modal:true">
                <table width="100%" border="0" cellpadding="0" cellspacing="0">
                    <tr style="height:40px;">
                        <td width="20%">Department</td>
                        <td width="80%">
                            <input name="cadreDepartment" id="cadreDepartment" class="easyui-combobox" style="width:500px;" data-options="valueField:'deptCode',textField:'deptName',editable:false" />
                        </td>
                    </tr>
                    <tr style="height:40px;">
                        <td>Cadre</td>
                        <td>
                            <input name="cadreName" id="cadreName" class="easyui-combobox" style="width:500px;" data-options="valueField:'value',textField:'label',editable:false"/>
                        </td>
                    </tr>
                    <tr style="height:40px;">
                        <td>&nbsp;</td>
                        <td>
                            <button type="button" onclick="getCadre()">Ok</button>
                        </td>
                    </tr>
                </table>
            </div>
            <%-- End - Content for Child Window --%>
        </form>
    </body>
</html>
