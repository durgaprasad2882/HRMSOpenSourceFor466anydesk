<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Human Resources Management System, Government of Odisha</title>

        <link rel="stylesheet" type="text/css" href="resources/css/themes/default/easyui.css">
        <link rel="stylesheet" type="text/css" href="resources/css/themes/icon.css">

        <script type="text/javascript" src="js/jquery.min-1.9.1.js"></script>
        <script type="text/javascript" src="js/jquery.easyui.min.js"></script>
        <script type="text/javascript" src="js/common.js"></script>
        <script type="text/javascript">
            $(document).ready(function () {





                $('#deptCode').change(function () {
                    if ($('#deptCode').val() != '') {
                        $.ajax({
                            type: "POST",
                            url: "getOfficeListJSON.htm",
                            data: {deptcode: $('#deptCode').val()},
                            dataType: "json",
                            success: function (data) {
                                var keys = Object.keys(data);
                                $('#hidOffCode').empty();
                                $('#hidSpc').empty();

                                newOption = '<option value="">Select One</option>';
                                $('#hidOffCode').append(newOption);

                                $.each(data, function (i, obj) {

                                    $('#hidOffCode').append('<option value="' + obj.offCode + '">' + obj.offName + '</option>');

                                });

                            },
                            error: function () {
                                alert('Error occured');
                            }
                        });
                    }
                });




                $('#hidOffCode').change(function () {
                    if ($('#hidOffCode').val() != '') {
                        $.ajax({
                            type: "POST",
                            url: "getPostCodeListJSON.htm",
                            data: {offcode: $('#hidOffCode').val()},
                            dataType: "json",
                            success: function (data) {
                                $('#hidSpc').empty();
                                $.each(data, function (i, obj) {
                                    $('#hidSpc').append('<option value="' + obj.value + '">' + obj.label + '</option>');
                                });

                            },
                            error: function () {
                                alert('Error occured');
                            }
                        });
                    }

                });


            });
            function radioFuncDept(val, row) {
                var data = row.deptCode + ":" + row.deptName;
                return "<input type='radio' name='rddeptid' id='rddeptid' value='" + data + "' onclick='SelectDepartment(\"" + data + "\")'/>";
            }

            function SelectDepartment(data) {

                var radspl = data.split(':');
                var deptcode = radspl[0];
                var deptname = radspl[1];
                $('#deptDialog').dialog('close');
                $('#deptname').textbox('setValue', deptname);
                $('#hidDeptCode').val(deptcode);
            }

            function radioFuncOffice(val, row) {
                var data = row.value + ":" + row.label;
                return "<input type='radio' name='rdoffcode' id='rdoffcode' value='" + data + "' onclick='SelectOffice(\"" + data + "\")'/>";
            }

            function SelectOffice(data) {

                var radspl = data.split(':');
                var offcode = radspl[0];
                var offname = radspl[1];
                $('#officeDialog').dialog('close');
                $('#officename').textbox('setValue', offname);
                $('#hidOffCode').val(offcode);
            }

            function radioFuncPost(val, row) {
                var data = row.spc + ":" + row.spn;
                data = data.replace("'", "&rsquo;");
                return "<input type='radio' name='rdopostcode' id='rdopostcode' value='" + data + "' onclick='SelectPost(\"" + data + "\")'/>";
            }

            function SelectPost(data) {

                var radspl = data.split(':');
                var spccode = radspl[0];
                var spn = radspl[1];
                $('#postDialog').dialog('close');
                $('#postname').textbox('setValue', spn);
                $('#hidPost').val(spccode);
            }

            function getPost() {
                var deptCode = $('#hidDeptCode').val();
                var offCode = $('#hidOffCode').val();
                var offName = $('#officename').textbox('getValue');
                var spc = $('#hidPost').val();
                var spn = $('#postname').textbox('getText');
                if (spc == '') {
                    alert('please select Post.');
                } else {
                    //$('#hidDeptCode').val(deptCode);
                    //$('#hidOffCode').val(offCode);
                    $('#sancAuthPostName').val(spn);
                    $('#hidSpc').val(spc);
                    $('#winsubstantivepost').window('close');
                }
            }

            function callWETFunction(val, row) {
                if (row.effTime == 'FN') {
                    return "FORE NOON";
                } else if (row.effTime == 'AN') {
                    return "AFTER NOON";
                }
            }

            function searchOffice() {
                $('#officedg').datagrid('load', {
                    offToSearch: $('#officesearch').val()
                });
            }
            function searchPost() {
                $('#postdg').datagrid('load', {
                    postToSearch: $('#postsearch').val()
                });
            }
            function changepost() {
                $('#winsubstantivepost').window('open');
            }

            function addIncrement() {
                $('#saveBtn').show();
                $('#delBtn').hide();
                $('#dlg').dialog('open');
                $('#incrementDataForm').form('clear');
                //$('#incrementDataForm').form('load', 'addIncrement.htm');
            }

            function saveCheck() {

                var ordno = $('#txtSanctionOrderNo').textbox('getValue');
                var orddt = $('#txtSanctionOrderDt').datebox('getValue');
                var wefdt = $('#txtWEFDt').datebox('getValue');
                var weftime = $('#txtWEFTime').combobox('getValue');
                var basic = $('#txtNewBasic').textbox('getValue');
                var incramt = $('#txtIncrAmt').textbox('getValue');
                var gp = $('#txtGradePay').textbox('getValue');
                $('#saveBtn').hide();
                if (ordno == '') {
                    alert("Enter Order No");
                    $('#saveBtn').show();
                    return false;
                }
                if (orddt == '') {
                    alert("Enter Order Date");
                    $('#saveBtn').show();
                    return false;
                }
                if (wefdt == '') {
                    alert("Enter With Effect Date");
                    $('#saveBtn').show();
                    return false;
                }
                if (weftime == '') {
                    alert("Enter With Effect Time");
                    $('#saveBtn').show();
                    return false;
                }
                if (basic == '') {
                    alert("Enter New Basic");
                    $('#saveBtn').show();
                    return false;
                }
                if (incramt == '') {
                    alert("Enter Increment Amount");
                    $('#saveBtn').show();
                    return false;
                }
                if (gp == '') {
                    alert("Enter Grade Pay");
                    $('#saveBtn').show();
                    return false;
                }
                return true;
            }

            function saveIncrement() {
                $('#incrementDataForm').form('submit', {
                    url: 'saveIncrement.htm',
                    onSubmit: function () {
                        return saveCheck();
                    },
                    success: function (result) {
                        var result = eval('(' + result + ')');
                        if (result.message) {
                            $.messager.show({
                                title: 'Error',
                                msg: result.message
                            });
                        } else {
                            $('#dlg').dialog('close'); // close the dialog
                            $('#incrementdg').datagrid('reload'); // reload the user data
                        }
                    }
                });
            }

            function editIncrement() {
                $('#saveBtn').show();
                var row = $('#incrementdg').datagrid('getSelected');
                if (row) {
                    var notId = row.hnotid;

                    var incrId = row.hidIncrId;
                    $('#dlg').dialog('open');
                    var url = 'editIncrement.htm?notId=' + notId + '&incrId=' + incrId;
                    $('#incrementDataForm').form('load', url);
                    $('#incrementDataForm').form({
                        onLoadSuccess: function (data) {
                            if (data.hidIncrId != '') {
                                $('#delBtn').show();
                                
                            }
                        }
                    });
                } else {
                    alert("Select a Row");
                }
            }

            function delIncrement() {
                var row = $('#incrementdg').datagrid('getSelected');
                if (row) {
                    var result = confirm("Want to delete?");
                    if (result) {
                        var notId = row.hnotid;

                        var incrId = row.hidIncrId;
                        
                        
                        var url = 'deleteIncrement.htm?notId='+notId+'&incrId=' + incrId;
                        $.getJSON(url, function(data) {
                            $('#incrementdg').datagrid('reload');
                            
                            if (data.isDeleted) {
                                alert("Sucessfully Deleted");
                                $('#dlg').dialog('close'); 
                            }
                        });
                        
                        
                        

                        
                    }
                } else {
                    alert("Select a Row");
                }
            }

        </script>
        <style type="text/css">
            table tr{
                font-family:Verdana;
                font-size:13px;
            }
        </style>
    </head>
    <body>
        <table id="incrementdg" class="easyui-datagrid" style="width:100%;height:400px;" title="Increment List"
               rownumbers="true" singleSelect="true" url="GetIncrementSanctionListJSON.htm" pagination="true" collapsible="false"
               data-options="nowrap:false" toolbar="#incrementTool">
            <thead>
                <tr>
                    <th data-options="field:'hidIncrId',hidden:true"></th>
                    <th data-options="field:'hnotid',hidden:true"></th>
                    <th data-options="field:'doe',width:150">Date of Entry</th>
                    <th data-options="field:'effDate',width:150">W.E.F Date</th>
                    <th data-options="field:'effTime',width:150,formatter:callWETFunction">W.E.F Time</th>
                    <th data-options="field:'incrAmt',width:100,align:'right'">Increment<br />Amount(Rs.)</th>
                    <th data-options="field:'newBasic',width:150,align:'right'">New Basic(Rs.)</th>
                    <th data-options="field:'gradePay',width:100,align:'right'">Grade Pay</th>
                </tr> 
            </thead>
        </table>
        <div id="incrementTool" style="padding:3px">                    
            <a href="javascript:addIncrement();" class="easyui-linkbutton" iconCls="icon-add" plain="true">New</a>
            <a href="javascript:editIncrement();" class="easyui-linkbutton" iconCls="icon-edit" plain="true">Edit</a>
        </div>

        <div id="dlg" class="easyui-dialog" title="Increment New/Edit" data-options="iconCls:'icon-save',closed: true,modal: true" style="width:900px;height:600px;padding:10px;top:50px;" buttons="#dlg-buttons">

            <form:form mathod="POST" action="IncrementSanctionList.htm" id="incrementDataForm" commandName="incrementForm">
                <input type="hidden" name="hnotid"/>
                <input type="hidden" name="hidIncrId" id="hidIncrId"/>
                <input type="hidden" name="hidPayId"/>

                <div align="center">
                    <div class="easyui-panel">
                        <% int i = 1;%>
                        <table style="width:100%;height:50px" border="0" cellpadding="0" cellspacing="0">    
                            <tr style="height:40px;">
                                <td width="5%" align="center"><%=i++%>.</td>
                                <td width="20%" align="left">
                                    Sanction Order No:<span style="color: red">*</span>
                                </td>
                                <td width="20%">
                                    <input class="easyui-textbox" name="txtSanctionOrderNo" id="txtSanctionOrderNo" style="width:80%;text-align:left"/>
                                </td>
                                <td width="5%" align="center"><%=i++%>.</td>
                                <td width="20%" align="left">
                                    Sanction Order Date:<span style="color: red">*</span>
                                </td>
                                <td width="20%">
                                    <input class="easyui-datebox" id="txtSanctionOrderDt" name="txtSanctionOrderDt" style="width:75%" data-options="required:true,formatter:myformatter,parser:myparser" editable="false">
                                </td>
                            </tr>

                            <tr style="height:40px;">
                                <td align="center"><%=i++%></td>
                                <td>Department</td>
                                <td colspan="4">

                                    <form:select id="deptCode" path="deptCode" class="form-control" style="width:500px;">
                                        <form:option value=""> ALL </form:option>
                                        <form:options items="${DepartmentList}" itemValue="value" itemLabel="label"/>
                                    </form:select>

                                </td>
                            </tr>
                            </tr>
                            <tr style="height:40px;">
                                <td align="center"><%=i++%></td>
                                <td>Office Name</td>
                                <td colspan="4">
                                    <form:select id="hidOffCode" path="hidOffCode" class="form-control" style="width:500px;">
                                        <form:option value=""> Select One </form:option>

                                    </form:select>

                                </td>
                            </tr>

                            <tr style="height:40px;">
                                <td align="center"><%=i++%></td>
                                <td>Authority Name</td>
                                <td colspan="4">
                                    <form:select id="hidSpc" path="hidSpc" class="form-control" style="width:500px;">
                                        <form:option value=""> Select One </form:option>
                                    </form:select>
                                </td>
                            </tr>




                            <tr style="height:40px;">
                                <td align="center"><%=i++%></td>
                                <td>
                                    With Effect From<span style="color: red">*</span>
                                </td>
                                <td>
                                    <input class="easyui-datebox" id="txtWEFDt" name="txtWEFDt" style="width:75%" data-options="required:true,formatter:myformatter,parser:myparser" editable="false">
                                </td>
                                <td align="center"><%=i++%></td>
                                <td>
                                    Time<span style="color: red">*</span>
                                </td>
                                <td>
                                    <select name="txtWEFTime" id="txtWEFTime" class="easyui-combobox">
                                        <option value="">--Select--</option>
                                        <option value="FN">FORENOON</option>
                                        <option value="AN">AFTERNOON</option>
                                    </select>
                                </td>
                            </tr>
                            <tr style="height:40px;">
                                <td align="center"><%=i++%></td>
                                <td>
                                    Pay Scale/Pay Band(in Rs.)
                                </td>
                                <td colspan="4">
                                    <input name="sltPayScale" id="sltPayScale" class="easyui-combobox" style="width:70%" data-options="valueField:'payscale',textField:'payscale',url:'incrementGetPayscaleListJSON.htm'" />
                                </td>
                            </tr>
                            <tr style="height:40px;">
                                <td align="center"><%=i++%></td>
                                <td>
                                    Increment Amount(in Rs.)<span style="color: red">*</span>
                                </td>
                                <td>
                                    <input class="easyui-numberbox" name="txtIncrAmt" id="txtIncrAmt"/>
                                </td>
                                <td align="center"><%=i++%></td>
                                <td>
                                    Personal Pay(in Rs.)
                                </td>
                                <td>
                                    <input class="easyui-numberbox" name="txtP_pay" id="txtP_pay"/>
                                </td>
                            </tr>
                            <tr style="height:40px;">
                                <td align="center"><%=i++%></td>
                                <td>
                                    Other Pay(in Rs.)
                                </td>
                                <td>
                                    <input class="easyui-numberbox" name="txtOthPay" id="txtOthPay"/>
                                </td>
                                <td align="center"><%=i++%></td>
                                <td>
                                    Special Pay(in Rs.)
                                </td>
                                <td>
                                    <input class="easyui-numberbox" name="txtSPay" id="txtSPay"/>
                                </td>
                            </tr>
                            <tr style="height:40px;">
                                <td align="center"><%=i++%></td>
                                <td>
                                    New Basic(in Rs.)<span style="color: red">*</span>
                                </td>
                                <td>
                                    <input class="easyui-numberbox" name="txtNewBasic" id="txtNewBasic"/>
                                </td>
                                <td align="center"><%=i++%></td>
                                <td>
                                    Desc. of Other Pay
                                </td>
                                <td>
                                    <input class="easyui-textbox" name="txtDescOth" id="txtDescOth"/>
                                </td>
                            </tr>
                            <tr style="height:40px;">
                                <td align="center"><%=i++%></td>
                                <td>
                                    Grade Pay<span style="color: red">*</span> 
                                </td>
                                <td colspan="4">
                                    <input class="easyui-numberbox" name="txtGradePay" id="txtGradePay"/> 7th Pay employee put 0 into this column
                                </td>
                                
                            </tr>
                            <tr style="height:40px;">
                                <td align="center"><%=i++%></td>
                                <td>
                                    Increment Level
                                </td>
                                <td colspan="4">
                                    <select class="easyui-combobox" name="incrementLvl" id="incrementLvl">
                                        <option value="">--Select--</option>
                                        <option value="1">First Increment</option>
                                        <option value="2">Second Increment</option>
                                        <option value="3">Third Increment</option>
                                        <option value="4">Fourth Increment</option>
                                    </select>
                                </td>
                            </tr>
                            <tr style="height:40px;">
                                <td align="center"><%=i++%></td>
                                <td>
                                    Increment Type
                                </td>
                                <td colspan="4">
                                    <select class="easyui-combobox" name="incrementType" id="incrementType">
                                        <option value="">--Select--</option>
                                        <option value="A">Annual Increment</option>
                                        <option value="S">Stagnation Increment</option>
                                        <option value="D">Advance Increment</option>
                                        <option value="T">Antedated</option>
                                        <option value="P">Previous</option>
                                    </select>
                                </td>
                            </tr>
                            <tr style="height:40px;">
                                <td align="center"><%=i++%></td>
                                <td>Note (if Any)</td>
                                <td colspan="4">
                                    <textarea name="txtIncrNote" id="txtIncrNote" style="width: 90%; text-align: left; height: 60px;"></textarea>
                                </td>
                            </tr>
                        </table>
                    </div>
                </div>
            </form:form>
        </div>
        <div id="dlg-buttons">
            <a href="javascript:void(0)" id="saveBtn" class="easyui-linkbutton c6" iconCls="icon-save" onclick="saveIncrement()" style="width:90px">Save</a>
            <a href="javascript:void(0)" id="delBtn" class="easyui-linkbutton c6" iconCls="icon-remove" onclick="delIncrement()" style="width:90px">Delete</a>
        </div>

        <%-- Start - Content for Child Window --%>

        <%-- End - Content for Child Window --%>
    </body>
</html>