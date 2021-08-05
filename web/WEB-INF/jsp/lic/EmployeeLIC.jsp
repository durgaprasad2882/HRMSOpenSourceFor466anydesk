<%-- 
    Document   : EmployeeLIC
    Created on : Jan 18, 2017, 1:21:46 PM
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
                        $('#newlicdlg').dialog('open');
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
            function saveEmployeeLIC(){
                $.post('saveEmployeeLic.htm', $('#fm').serialize()).done(function (data) {
                    $('#newlicdlg').dialog('close'); // close the dialog 
                    $('#newlicGrid').datagrid('reload');
                });
            }
        </script>
    </head>
    <body>
        <div>
            <table class="easyui-datagrid" style="height:400px" width="100%" id="newlicGrid" url="getEmployeeWiseLICList.htm" data-options="fitColumns:true,singleSelect:true,toolbar:toolbar">
                <thead>
                    <tr>
                        <th data-options="field:'policyNo'" width="35%">Life Insurance Policy No</th>
                        <th data-options="field:'subAmount'" width="30%">Monthly Subscription amount</th>
                        <th data-options="field:'wef'" width="35%">With Effect From</th>
                    </tr>
                </thead>
            </table>
        </div>
    </div>
    <div id="newlicdlg" class="easyui-dialog" title="Life Insurance Premium" style="width:700px;height:400px;padding:10px" data-options="iconCls:'icon-save',closed: true,modal: true,buttons: [{
         text:'Save',
         iconCls:'icon-ok',
         handler:function(){
         saveEmployeeLIC();
         }
         },{
         text:'Cancel',
         handler:function(){
         $('#newlicdlg').dialog('close');
         }
         }]" >
        <form id="fm" method="post" novalidate style="margin:0;padding:10px 50px">
            <div style="margin-bottom:10px;font-size:14px;border-bottom:1px solid #ccc">Life Insurance Premium Information</div>
            <div style="margin-bottom:10px">
                <input name="elId" id="elId" type="hidden">
                <input name="policyNo" id="policyNo" class="easyui-textbox" required="true" label="Life Insurance Policy No: " style="width:80%">
            </div>
            <div style="margin-bottom:10px">
                <input name="subAmount" id="subAmount" class="easyui-textbox" required="true" label="Monthly Subscription amount: " style="width:80%">
            </div>
            <div style="margin-bottom:10px">
                <input name="wef" id="wef" class="easyui-datebox" required="true" label="With Effect From: " style="width:80%">
            </div>
            <div style="margin-bottom:10px">
                <select name="insuranceType" id="insuranceType" class="easyui-combobox" label='Insurance Type:' required="true" style="width:80%;">                        
                    <option selected="selected" value="">--Select One--</option>
                    <option value="LIC">LIC</option>
                    <option value="PLI">PLI</option>
                    <option value="TLIC">TLIC</option>
                </select>                               
            </div>
            <div style="margin-bottom:10px">
                <input name="note" id="note" class="easyui-textbox" multiline="true" label="Note: " style="width:80%">
            </div>
        </form>
    </div>
</body>
</html>
