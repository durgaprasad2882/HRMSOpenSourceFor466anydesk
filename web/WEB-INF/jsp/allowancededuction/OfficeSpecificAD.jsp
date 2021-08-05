<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
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
        <script type="text/javascript" src="js/bootstrap.min.js"></script>
        <script type="text/javascript">
            var toolbar = [{
                    text: 'Edit',
                    iconCls: 'icon-edit',
                    handler: function () {
                        var row = $('#adlistGrid').datagrid('getSelected');
                        if (row) {

                            var adcode = "";
                            var whereupdated = "G";

                            if (row.adcode) {
                                adcode = row.adcode;
                                whereupdated = "O";
                            } else {
                                adcode = row.refadcode;
                                whereupdated = "G";
                            }
                            $("#whereupdated").val(whereupdated);
                            $('#dlg').dialog('open').dialog('center');
                            $('#fm').form('load', 'editAllowanceDeductionAction.htm?adcode=' + adcode + '&whereupdated=' + whereupdated);
                            //alert(adcode);
                            //$('#dlg').dialog('refresh', 'editAllowanceDeductionAction.htm?adcode=' + adcode);
                            //$('#dlg').dialog('open');
                        } else {
                            alert("Select a Row");
                        }
                    }
                }, {
                    text: 'Delete',
                    iconCls: 'icon-remove',
                    handler: function () {
                        var row = $('#adlistGrid').datagrid('getSelected');
                        if (row) {
                            alert(row.billno);
                            $('#dlg').dialog('refresh', 'payBillReportAction.htm?billNo=' + row.billno);
                            $('#dlg').dialog('open');
                        } else {
                            alert("Select a Row");
                        }
                    }
                }];
            function showADList() {
                var txtadtype = $('#txtadtype').combobox('getValue');
                $('#adlistGrid').datagrid({
                    url: "getOfficeWiseADList.htm?adtype=" + txtadtype
                });
            }
            function changeBtnStatus() {
                $('#head').textbox('readonly', false);
            }
            function saveUpdatedAdDetails() {
                $('#fm').form('submit', {
                    url: "saveAllowanceDeductionAction.htm",                    
                    success: function (result) {
                        alert(result);
                        var result = eval('(' + result + ')');
                        if (result.errorMsg) {
                            $.messager.show({
                                title: 'Error',
                                msg: result.errorMsg
                            });
                        } else {
                            $('#dlg').dialog('close'); // close the dialog
                            $('#dg').datagrid('reload'); // reload the user data
                        }
                    }
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
                        <td align="center" width="15%"><b>Allowance /Deduction :</b>&nbsp;<span style="color: red">*</span></td>
                        <td width="15%" c>
                            <select name="txtadtype" id="txtadtype" class="easyui-combobox" style="width:60%;">
                                <option value="">Select</option>
                                <option value="A">Allowance</option>
                                <option value="D">Deduction</option>
                                <option value="P">Pvt. Deduction</option>
                            </select>
                        </td>
                        <td width="10%">                                
                            <input type="button" value="Ok" class="sessionstyle" onclick="return showADList()"/>                        
                        </td>
                    </tr>
                </table>
            </div>
            <div>
                <table class="easyui-datagrid" style="height:400px" width="100%" id="adlistGrid"
                       data-options="fitColumns:true,singleSelect:true,toolbar:toolbar">
                    <thead>
                        <tr>
                            <th data-options="field:'addesc'" width="25%">NAME OF THE HEAD</th>
                            <th data-options="field:'adcodename'" width="15%">CODE</th>
                            <th data-options="field:'head'" width="15%">BT/OBJECT HEAD</th>
                            <th data-options="field:'formula'" width="25%">FORMULA</th>
                            <th data-options="field:'advalue'" width="5%">FIXED VALUE</th>
                        </tr>
                    </thead>
                </table>
            </div>
        </div>
        <div id="dlg" class="easyui-dialog" title="Edit Allowance Deduction" data-options="iconCls:'icon-save',closed: true,modal: true" closed="true" style="width:700px;" buttons="#dlg-buttons">
            <form id="fm" method="post" novalidate style="margin:0;padding:20px 50px">
                <div style="margin-bottom:20px;font-size:14px;border-bottom:1px solid #ccc">Allowance Deduction Information</div>
                <div style="margin-bottom:10px">
                    <input name="addesc" class="easyui-textbox" required="true" label="Code:" style="width:80%">
                    <input type="hidden" name="adcode" id="adcode" />
                    <input type="hidden" name="whereupdated" id="whereupdated"/>
                </div>
                <div style="margin-bottom:10px">
                    <input name="adcodename" class="easyui-textbox" required="true" label="Adcode Name:" style="width:80%">
                </div>
                <div style="margin-bottom:10px">
                    <select name="isfixed" id="sltAdamttype" class="easyui-combobox" required="true" label="Fixed or Formula:" style="width:80%;">
                        <option value="1">FIXED</option>
                        <option value="0">FORMULA</option>
                    </select>                        
                </div>
                <div style="margin-bottom:10px">
                    <input name="advalue" class="easyui-textbox" label="Amount:" style="width:80%">
                </div> 
                <div style="margin-bottom:10px">
                    <input name="formula" class="easyui-textbox" label="Formula:" style="width:80%">
                </div>
                <div style="margin-bottom:10px">
                    <input name="head" id="head" class="easyui-textbox" required="true" label="Object Head:" style="width:70%" readonly="true"> 
                    <input href="javascript:void(0)" class="easyui-linkbutton" style="width:90px" id="btnChange" name="btnChange" value="Change" onclick="changeBtnStatus()"/>
                </div>
            </form>
        </div>
        <div id="dlg-buttons">
            <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok" onclick="saveUpdatedAdDetails()" style="width:90px">Save</a>
            <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:$('#dlg').dialog('close')" style="width:90px">Cancel</a>
        </div>
    </body>
</html>
