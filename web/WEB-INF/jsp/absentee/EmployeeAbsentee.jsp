<%-- 
    Document   : EmployeeAbsentee
    Created on : Jan 20, 2017, 5:06:25 PM
    Author     : Manas Jena
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Human Resources Management System, Government of Odisha</title>

        <link rel="stylesheet" type="text/css" href="resources/css/themes/default/easyui.css">
        <link rel="stylesheet" type="text/css" href="resources/css/themes/icon.css">
        <link href="css/bootstrap.min.css" rel="stylesheet">
        <link href="font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
        <!-- LAYOUT v 1.3.0 -->
        <script type="text/javascript" src="js/jquery.min-1.9.1.js"></script>

        <script type="text/javascript" src="js/jquery.easyui.min.js"></script>
        <script type="text/javascript">
            var toolbar = [{
                    text: 'New',
                    iconCls: 'icon-add',
                    handler: function () {
                        $('#fm').form('clear');
                        $('#newabsenteedlg').dialog('open');
                    }
                }, {
                    text: 'Edit',
                    iconCls: 'icon-edit',
                    handler: function () {
                        var row = $('#newlicGrid').datagrid('getSelected');
                        if (row) {
                            //$('#dlg').dialog('refresh', 'payBillReportAction.htm?billNo=' + row.billno);
                            $('#fm').form('clear');
                            $('#fm').form('load', 'editEmployeeLic.htm?elId=' + row.elId);
                            $('#newlicdlg').dialog('open');
                        } else {
                            alert("Select a Row");
                        }
                    }
                }, {
                    text: 'Delete',
                    iconCls: 'icon-remove',
                    handler: function () {
                        var row = $('#newlicGrid').datagrid('getSelected');
                        if (row) {
                            //$('#dlg').dialog('refresh', 'payBillReportAction.htm?billNo=' + row.billno);
                            $('#newlicdlg').dialog('open');
                        } else {
                            alert("Select a Row");
                        }
                    }
                }];
            function showAbsenteeList() {
                var sltyear = $('#sltyear').combobox('getValue');
                var sltmonth = $('#sltmonth').combobox('getValue');
                alert("sltmonth"+sltmonth);
                $('#absenteelistGrid').datagrid({
                    
                    url: "getAbseneteeList.htm?sltyear=" + sltyear + "&sltmonth=" + sltmonth
                });
            }
            
             /*    function saveEmployeeAbsentee(){
                  
                   $('#fm').form('submit', {
                    url: "saveEmployeeAbsentee.htm",                    
                    success: function (result) {
                        alert(result);
                        var result = eval('(' + result + ')');
                        if (result.errorMsg) {
                            $.messager.show({
                                title: 'Error',
                                msg: result.errorMsg
                            });
                        } else {
                            $('#newabsenteedlg').dialog('close'); // close the dialog
                            $('#absenteelistGrid').datagrid('reload'); // reload the user data
                        }
                    }
                });
         }*/
            
            
           function saveEmployeeAbsentee(){
                $.post('saveEmployeeAbsentee.htm', $('#fm').serialize()).done(function (data) {
                    $('#newabsenteedlg').dialog('close'); // close the dialog 
                    $('#absenteelistGrid').datagrid('reload');
                });
            }
        </script>
    </head>
    <body>
        <div align="center" style="margin-top:5px;margin-bottom:7px;">
            <div class="empInfoDiv" align="center" >
                <table border="0" width="90%"  cellspacing="0" class="table">
                    <thead></thead>
                    <tr style="height:30">
                        <td align="center" width="15%"><b>Select Year:</b>&nbsp;<span style="color: red">*</span></td>
                        <td width="15%">
                            <select name="sltyear" id="sltyear" class="easyui-combobox" style="width:60%;" data-options=" url:'getAbseneteeYear.htm',method:'get',valueField:'value',textField:'label',panelHeight:'auto',labelPosition: 'top'">
                                <option value="">Select</option>

                            </select>
                        </td>
                        <td align="center" width="15%"><b>Select Month</b>&nbsp;<span style="color: red">*</span></td>
                        <td width="15%">
                            <select name="sltmonth" id="sltmonth" class="easyui-combobox" style="width:60%;" >
                                <option value="00">Jan</option>
                                <option value="01">Feb</option>
                                <option value="02">Mar</option>
                                <option value="03">Apr</option>
                                <option value="04">May</option>
                                <option value="05">Jun</option>
                                <option value="06">Jul</option>
                                <option value="07">Aug</option>
                                <option value="08">Sep</option>
                                <option value="09">Oct</option>
                                <option value="10">Nov</option>
                                <option value="11">Dec</option>
                            </select>
                        </td>
                        <td width="10%">                                
                            <input type="button" value="Ok" class="sessionstyle" onclick="showAbsenteeList()"/>                        
                        </td>
                    </tr>
                </table>
            </div>
            <div>
                <table class="easyui-datagrid" style="height:400px" width="100%" id="absenteelistGrid"
                       data-options="fitColumns:true,singleSelect:true,toolbar:toolbar">
                    <thead>
                        <tr>
                            <th data-options="field:'frmDate'" width="25%">From Date</th>
                            <th data-options="field:'toDate'" width="25%">To Date</th>
                            <th data-options="field:'totaldts'" width="25%">Total Days</th>                            
                        </tr>
                    </thead>
                </table>
            </div>

        </div>
        <div id="newabsenteedlg" class="easyui-dialog" title="Employee Absentee" style="width:700px;height:400px;padding:10px" data-options="iconCls:'icon-save',closed: true,modal: true" closed="true" style="width:700px;" buttons="#dlg-buttons" >
            <form id="fm" method="post" novalidate style="margin:0;padding:10px 50px">
                <div style="margin-bottom:10px;font-size:14px;border-bottom:1px solid #ccc">Absentee Information</div>

                <div style="margin-bottom:10px">
                    <input name="fromDate" id="fromDate" class="easyui-datebox" required="true" label="From Date: " style="width:80%">
                </div>
                <div style="margin-bottom:10px">
                    <input name="toDate" id="toDate" class="easyui-datebox" required="true" label="To Date: " style="width:80%">
                </div>
            </form>
        </div>
          <div id="dlg-buttons">
            <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok" onclick="saveEmployeeAbsentee()" style="width:90px">Save</a>
            <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:$('#newabsenteedlg').dialog('close')" style="width:90px">Cancel</a>
        </div>
    </body>
</html>
