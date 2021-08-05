<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Performance Appraisal</title>

        <link href="resources/css/colorbox.css" rel="stylesheet">
        <link rel="stylesheet" type="text/css" href="resources/css/themes/default/easyui.css">
        <link rel="stylesheet" type="text/css" href="resources/css/themes/icon.css">

        <!-- LAYOUT v 1.3.0 -->
        <script type="text/javascript" src="js/jquery.min-1.9.1.js"></script>
        <script type="text/javascript" src="js/jquery.easyui.min.js"></script>
        <script type="text/javascript" src="js/jquery.colorbox-min.js"></script>
        <script type="text/javascript">

            $(document).ready(function() {
                $('#fiscalyear').combobox('setValue', $('#defaultfiscalyear').val());
            });

            function doSearch() {
                var fiscalyear = $('#fiscalyear').combobox('getValue');
                $('#dg').datagrid('load', {
                    fiscalyear: fiscalyear
                });
            }

            function editPAR(val, row) {
                var parid = row.parid;
                var parstatus = row.parstatus;
                //alert("PAR Status is: "+ parstatus);
                if (parstatus < 6 || parstatus == 16 || parstatus == 18 || parstatus == 19) {
                    var editdata = 'editPAR.htm?parid=' + parid;
                    var viewdata = 'PARDetailView.htm?parid=' + parid + '&taskid=0&auth=';
                    //return "<a href='"+editdata+"'>Edit</a>/<a href='"+viewdata+"' target='_blank'>View</a>";
                    if (parstatus != 16 && parstatus != 18 && parstatus != 19) {
                        return "<a href='" + editdata + "'>Edit</a>/<a href='" + viewdata + "' target='_blank'>View</a>";
                    } else if (parstatus == 16 || parstatus == 18 || parstatus == 19) {
                        return "<a href='" + editdata + "'>Edit</a>/<a href='" + viewdata + "' target='_blank'>View</a>&nbsp;&nbsp;&nbsp&nbsp;<a href='javascript:viewRevertReason(" + parid + ")'><img src='./images/revert.png' width='25' height='25' alt='Reverted' title='Reverted'/></a>";
                    }
                } else if (parstatus >= 6 && (parstatus != 17 && parstatus != 16 && parstatus != 18 && parstatus != 19)) {
                    //var data = 'viewPAR.htm?parid='+parid;
                    var viewdata = 'PARDetailView.htm?parid=' + parid + '&taskid=0&auth=';
                    return "<a href='" + viewdata + "' target='_blank'>View</a>";
                } else if (parstatus == 17) {
                    var editnrc = 'NRCDetailView.htm?parid=' + parid;
                    return "<a href='" + editnrc + "'>View</a>";
                }
            }

            function submitPAR(val, row) {
                var parid = row.parid;
                var parstatus = row.parstatus;
                var isclosed = row.isClosed;
                var authRemarksClosed = row.authRemarksClosed;
                //alert("PAR Status is: "+ parstatus);
                if (isclosed == "N" || authRemarksClosed == "N") {
                    if (parstatus < 6 || parstatus == 16 || parstatus == 18 || parstatus == 19) {
                        return "<a href='submitPAR.htm?parid=" + parid + "'>Submit</a>";
                    } else if (parstatus >= 6) {
                        if (parstatus == 6) {
                            return "Submitted to Reporting Authority";
                        } else if (parstatus == 7) {
                            return "Submitted to Reviewing Authority";
                        } else if (parstatus == 8) {
                            return "Submitted to Accepting Authority";
                        } else if (parstatus == 9) {
                            return "Completed";
                        } else if (parstatus == 17) {
                            return "Requested for NRC";
                        }
                    }
                } else if (isclosed == "Y" && authRemarksClosed == "Y") {
                    if (parstatus != 9) {
                        if (parstatus == 17) {
                            return "<span style='color:red'>NRC is Completed</span>";
                        } else {
                            return "<span style='color:red'>PAR is Closed</span>";
                        }
                    } else {
                        return "Completed";
                    }
                }
            }

            function viewRevertReason(parid) {
                var url = 'RevertReason.htm?parid=' + parid;
                $.colorbox({href: url, iframe: true, open: true, width: "60%", height: "50%"});
            }
			
			function createCheck(){
                var fiscalyear = $('#fiscalyear').combobox('getValue');
                if(fiscalyear == ''){
                    alert("Please select Financial Year");
                    return false;
                }
            }
        </script>
        <style type="text/css">
            body{
                font-family: Verdana;
                font-size:16px;
            }
        </style>
    </head>
    <body>
        <form action="addPAR.htm" method="POST" commandName="parMastForm">
            <input type="hidden" id="defaultfiscalyear" value="${defaultfiscalyear}"/>
            <div align="center" style="margin-top:5px;margin-bottom:10px;">
                <div class="empInfoDiv" align="center">
                    <table border="0" width="100%" cellspacing="0" style="font-size:12px; font-family:verdana;">
                        <tr>
                            <td align="center">
                                <input name="fiscalyear" id="fiscalyear" class="easyui-combobox" style="width:20%" data-options="valueField:'fy',textField:'fy',url:'GetFiscalYearListJSON.htm'" />
                                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                <button class="btn btn-lg btn-primary btn-block easyui-linkbutton" type="button" onclick="doSearch();">Ok</button>
                            </td>
                            <td align="center">
                                &nbsp;
                            </td>
                        </tr>
                    </table>
                </div>
            </div>
            <table id="dg" class="easyui-datagrid" style="width:100%;height:400px;" title="My PAR"
                   rownumbers="true" singleSelect="true" url="GetPARListJSON.htm" singleSelect="true" pagination="true" collapsible="false"
                   data-options="nowrap:false">
                <thead>
                    <tr>
                        <th data-options="field:'taskid',width:10,hidden:true"></th>
                        <th data-options="field:'parstatus',width:10,hidden:true"></th>
                        <th data-options="field:'isClosed',width:10,hidden:true"></th>
                        <th data-options="field:'authRemarksClosed',width:10,hidden:true"></th>
                        <th data-options="field:'parid',width:80">PAR No</th>
                        <th data-options="field:'periodfrom',width:150">Period From</th>
                        <th data-options="field:'periodto',width:150">Period To</th>
                        <th data-options="field:'designation',width:300">Designation during the Period of Report</th>
                        <th data-options="field:'isauthSet',width:80,formatter:editPAR">Edit</th>
                        <th data-options="field:'refid',width:100,formatter:submitPAR">Submit</th>
                    </tr> 
                </thead>
            </table>
            <table style="width:100%;height: 90px;">
                <tr>
                    <td width="30%" align="left">
                        <input type="hidden" name="pageno" value="0"/>
                        <%--<button type="submit">Create PAR</button>--%>
                        <span id="parbtn">
                            <input type="submit" name="newPar" value="Create PAR" class="easyui-linkbutton" onclick="return createCheck();"/>
                            &nbsp;&nbsp;&nbsp;&nbsp;
                            <input type="submit" name="newPar" value="Request For NRC" class="easyui-linkbutton" onclick="return createCheck();"/>
                        </span>
                    </td>
                    <td width="70%" align="left">&nbsp;</td>
                </tr>
            </table>
        </form>
    </body>
</html>
