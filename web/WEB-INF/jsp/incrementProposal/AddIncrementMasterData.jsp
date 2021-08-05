<%-- 
    Document   : AddIncrementMasterData
    Created on : 28 Jun, 2016, 3:46:09 PM
    Author     : Surendra
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        <link rel="stylesheet" type="text/css" href="resources/css/themes/default/easyui.css">
        <link rel="stylesheet" type="text/css" href="resources/css/themes/icon.css">

        <!-- LAYOUT v 1.3.0 -->
        <script type="text/javascript" src="js/jquery.min-1.9.1.js"></script>
        <script type="text/javascript" src="js/jquery.easyui.min.js"></script>

        <script type="text/javascript">
            $(document).ready(function () {
                $('#dlg').dialog('close');
                propmastId = $("#proposalId").val();
                $('#dg').datagrid({
                    url: "proposalListAction.htm?proposalId=" + propmastId
                });

            });

            function addEmployeeintoList() {
                month = $("#proposalMonth").val();
                year = $("#proposalYear").val();
                propmastId = $("#proposalId").val();
                emplist = $("#dgAddEmp").datagrid("getChecked");
                var ids = '';
                for (var i = 0; i < emplist.length; i++) {
                    if (ids != '') {
                        ids = ids + ',' + emplist[i].empId;
                    } else {
                        ids = emplist[i].empId;
                    }

                }
                if (emplist.length > 0) {
                    $.ajax({
                        type: 'POST',
                        data: {emplist: ids, month: month, year: year, proposlMastId: propmastId},
                        url: 'addProposedEmployee.htm',
                        success: function (response) {
                            $('#dlg').dialog('close');
                            $('#dg').datagrid('reload');
                        }
                    });
                } else {
                    alert('Nothing to Add');
                }
            }

            function saveproposal() {
                $(".easyui-linkbutton").attr('disabled', true);
                data = $("#dg").datagrid("getRows");
                var ids = '';
                for (var i = 0; i < data.length; i++) {
                    if (ids != '') {
                        ids = ids + ',' + data[i].empId;
                    } else {
                        ids = data[i].empId;
                    }

                }
                proposalId = $("#proposalId").val();
                month = $("#proposalMonth").val();
                year = $("#proposalYear").val();
                if (data.length > 0) {
                    $.ajax({
                        type: 'POST',
                        data: {proposalId: proposalId, emplist: ids, month: month, year: year},
                        url: 'saveProposalList.htm',
                        success: function (response) {
                            alert('Proposal Created Successfully');
                            window.location.href = "displayProposalListpage.htm";
                        }
                    });
                } else {
                    alert('Nothing to Save');
                }
            }

            function removeProposal(data) {
                var result = confirm("Are you sure want to remove from Proposal List?");
                if (result) {
                    $.ajax({
                        type: 'POST',
                        url: 'removeProposalList.htm?' + data,
                        success: function (response) {
                            $('#dg').datagrid('reload');
                        }
                    });
                } else {
                    return false;
                }

            }

            function returnveify(){
                data = $("#dg").datagrid("getRows");
                if(data.length>$('#rowcount').val()){
                    if(confirm("Your proposal List not save. Are you want to save?"))
                    {
                        return false;
                    }
                }
            }


        </script>
    </head>
    <body>
        <form action="newProposalMaster.htm" commandName="incrementForm" method="post">
            <div id="p" class="easyui-panel" title="Increment " style="width:100%;height:575px;padding:10px;">
                <input type="hidden" id="proposalId" name="proposalId" value="${incrementForm.proposalId}"/>
                <input type="hidden" id="proposalMonth" name="proposalMonth" value="${incrementForm.proposalMonth}"/>
                <input type="hidden" id="proposalYear" name="proposalYear" value="${incrementForm.proposalYear}"/>
                <input type="hidden" id="rowcount" name="rowcount" value=""/>
                <div style="text-align:center;">
                    <label for="Month">INCREMENT PROPOSAL LIST FOR THE MONTH OF</label> : ${incrementForm.monthasString}
                </div>

                <table id="dg" class="easyui-datagrid" style="width:100%;height:480px;" title="Increment Proposal List" 
                       data-options="rownumbers:true,fitColumns:true,singleSelect:true,collapsible:true,method:'get'" toolbar="#toolbar">
                    <thead>
                        <tr>
                            <th data-options="field:'empId'">HRMS ID</th>
                            <th data-options="field:'gpfno'">GPF NO</th>
                            <th data-options="field:'empname'">Employee Name</th>
                            <th data-options="field:'post'">Substantive/ <br> Officiating</th>
                            <th data-options="field:'payscale'">Scale of Pay of Posts</th>
                            <th data-options="field:'presentpay'">Present pay</th>
                            <th data-options="field:'presentpaydate'">Date from which </br> present pay is drawn</th>
                            <th data-options="field:'nextincr'">Date of present </br> increment</th>
                            <th data-options="field:'futurepay'">Future pay</th>
                            <th data-options="field:'proposaldetailId',formatter:removeProposalDetails">Remove</th>
                        </tr> 
                    </thead>
                </table>
                <div id="toolbar">
                    <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-add" plain="true" onclick="addEmployee()">Add Employee</a>
                    <a href="javascript:saveproposal()" class="easyui-linkbutton" iconCls="icon-save" plain="true">Save Proposal</a>
                </div>
                <script type="text/javascript">
                    function removeProposalDetails(val, row) {
                        var data = "proposaldetailId=" + row.proposaldetailId + "&empId=" + row.empId;
                        return "<a href='javascript:void(0)' onclick='removeProposal(\"" + data + "\")'><img src='images/Delete-icon.png' width='15' height='15'></a>";

                    }

                    function addEmployee() {
                        $('#dlg').dialog('open');
                        $('#dgAddEmp').datagrid('reload');
                        data = $("#dg").datagrid("getRows");
                        
                        rowsize=data.length;
                        $('#rowcount').val(rowsize);
                        
                    }
                </script>

                <div id="dlg" class="easyui-dialog" title="Add Employee" style="width:80%;height:400px;max-width:800px;padding:10px" data-options="
                     iconCls:'icon-save',
                     modal:true,
                     onResize:function(){
                     $(this).dialog('center');
                     }">

                    <table id="dgAddEmp" class="easyui-datagrid" title="Select employee from list" style="width:100%;height:250px"
                           data-options="rownumbers:true,singleSelect:false,fitColumns:true,url:'addProposalEmployeeListAction.htm',method:'get'" toolbar="#toolbar2">
                        <thead>
                            <tr>
                                <th data-options="field:'ck',checkbox:true"></th>
                                <th data-options="field:'empId'">Employee Id</th>
                                <th data-options="field:'gpfno'">GPF/PRAN</th>
                                <th data-options="field:'empname'">Employee Name</th>
                                <th data-options="field:'nextincr'">Next Increment</th>
                                <th data-options="field:'post'">Designation</th>

                            </tr>
                        </thead>
                    </table>
                </div>
                <div id="toolbar2">
                    <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-add" plain="true" onclick="addEmployeeintoList()">Add</a>
                </div>

                <div id="dlg-buttons">
                    <table cellpadding="0" cellspacing="0" style="width:100%">
                        <tr>
                            <td style="text-align:left">
                                <a href="displayProposalListpage.htm" class="easyui-linkbutton" >Return</a>
                            </td>
                        </tr>
                    </table>
                </div>
            </div>
        </form>
    </body>
</html>
