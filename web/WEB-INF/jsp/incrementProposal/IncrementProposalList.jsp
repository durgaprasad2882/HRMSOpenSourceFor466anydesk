<%-- 
    Document   : Increment Proposal List
    Created on : 20 Jun, 2016, 12:14:12 PM
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
        <!-- Include Bootstrap Datepicker -->

        <link rel="stylesheet" type="text/css" href="resources/css/themes/default/easyui.css">
        <link rel="stylesheet" type="text/css" href="resources/css/themes/icon.css">
        <link rel="stylesheet" type="text/css" href="resources/css/demo.css">
        <!-- LAYOUT v 1.3.0 -->
        <script type="text/javascript" src="js/jquery.min-1.9.1.js"></script> 
        <script type="text/javascript" src="js/jquery.easyui.min.js"></script>




        <script type="text/javascript">
            var monthNames = ["JAN", "FEB", "MAR", "APR", "MAY", "JUN",
                "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"
            ];


            $(document).ready(function() {
                $('#dd').dialog('close');
            });

            function openInputForm(proposalId) {
                $('#dd').dialog('open');
                $('#eventForm').form('clear');
                $('#proposalId').val(proposalId);
            }

            function myformatter(date) {
                var y = date.getFullYear();
                var m = date.getMonth();
                var d = date.getDate();
                //alert(date);
                //(d < 10 ? ('0' + d) : d) + '-' + (m < 10 ? ('0' + m) : m) + '-' + y;
                return (d < 10 ? ('0' + d) : d) + '-' + monthNames[m] + '-' + y;
            }
            function myparser(s) {                
                if (!s)
                    return new Date();
                var ss = (s.split('-'));
                var found = $.inArray(ss[1], monthNames);                
                var y = parseInt(ss[0], 10);
                var m = parseInt(found+1, 10);
                var d = parseInt(ss[2], 10);
                if (!isNaN(y) && !isNaN(m) && !isNaN(d)) {
                    return new Date(d, m - 1, y); //d + '-' + monthNames[m - 1] + '-' + y;
                } else {
                    return new Date();
                }
            }




            function openInputForm(proposalId) {
                $('#dd').dialog('open');
                $('#eventForm').form('clear');
                $('#proposalId').val(proposalId);
            }





        </script>
    </head>
    <body>

        <table id="dg" class="easyui-datagrid" style="width:100%;height:575px;" title="Increment Proposal List"
               rownumbers="true" pagination="true" url="proposalMasterListAction.htm"
               data-options="singleSelect:true,collapsible:false,fitColumns:true" toolbar="#tb">
            <thead>
                <tr>
                    <th data-options="field:'lastUpdated'"> Creation Date</th>
                    <th data-options="field:'propMonth'">Month</th>
                    <th data-options="field:'propYear'">Year</th>
                    <th data-options="field:'ordno'">Order No</th>
                    <th data-options="field:'orddate'">Order Date</th>
                    <th data-options="field:'proposalId',formatter:editProposalDetails">Edit</th>
                    <th data-options="field:'submitproposalId',formatter:submitProposalDetails">Submit</th>
                    <th data-options="field:'exportproposalId',formatter:export2Pdf">Export</th>
                    <th data-options="field:'status',formatter:getOrderNoInsertPage">Status</th>
                    <th data-options="field:'taskid',hidden:true"></th>
                </tr> 
            </thead>
        </table>

        <div id="dd" class="easyui-dialog" title="Update Order No and Order Date" style="width:400px;height:200px;"
             data-options="iconCls:'icon-save',resizable:true,modal:true">
            <input id="proposalId" type="hidden" name="proposalId">
            <div class="easyui-panel">
                <form id="eventForm" method="post">




                    <div style="margin-bottom:20px">
                        <input class="easyui-textbox" id="orderno" name="orderno" style="width:70%" data-options="label:'Order No:',required:true">
                    </div>
                    <div style="margin-bottom:20px">
                        <input class="easyui-datebox" id="orderDate" name="orderDate" style="width:70%" data-options="label:'Order Date :',required:true,formatter:myformatter,parser:myparser" editable="false">
                    </div>


                    <div class="form-group">
                        <div >
                            <div style="margin:20px 0;">
                                <a href="javascript:void(0)" class="easyui-linkbutton" onclick="updateOrderInfo()" style="width:80px">Save</a>
                                <a href="javascript:void(0)" class="easyui-linkbutton" onclick="clearForm()" style="width:80px">Clear</a>
                            </div>

                        </div>
                    </div>
                </form>
            </div>
        </div>




        <script>
            function updateOrderInfo() {
                $('#eventForm').form('submit', {
                    onSubmit: function() {
                        if ($(this).form('enableValidation').form('validate')) {

                            proposalId = $('#proposalId').val();
                            ordno = $('#orderno').val();
                            orderDate = $('#orderDate').val();
                            
                            $.ajax({
                                type: 'POST',
                                data: {propmastId: proposalId, ordno: ordno, ordDate: orderDate},
                                url: 'updateorderInfo.htm',
                                success: function(response) {
                                    $('#dd').dialog('close');
                                    $('#dg').datagrid('reload');
                                }
                            });
                            return false;
                        } else {
                            return false;
                        }
                    }
                });
            }

            function clearForm() {
                $('#eventForm').form('clear');
            }

            function editProposalDetails(val, row) {
                if (row.taskid < 1) {
                    var url = "newProposalMaster.htm?proposalId=" + row.proposalId;
                    return '<a href="' + url + '">Edit</a>';
                }
            }
            function submitProposalDetails(val, row) {
                if (row.taskid < 1) {
                    var url = "submitProposal.htm?proposalId=" + row.proposalId;
                    return '<a href="' + url + '">Submit</a>';
                }
            }



            function export2Pdf(val, row) {
                var url = "generatepdf.htm?proposalId=" + row.exportproposalId;
                return "<a href='" + url + "'><img src='resources/css/themes/icons/pdf_icon.gif' width='15' height='15'></a>";
            }

            function getOrderNoInsertPage(val, row, index) {
                if (row.status == 'APPROVED' && row.orddate == '') {
                    return "<a href='javascript:void(0)' onclick='openInputForm(\"" + row.proposalId + "\")'>APPROVED</a>";
                } else if (row.status == 'APPROVED' && row.orddate != '') {
                    return 'APPROVED';
                } else if (row.status == 'DECLINE') {
                    return 'DECLINE';
                } else if (row.status == 'PENDING') {
                    return 'PENDING';
                }
            }
        </script>
        <div style="padding:5px 0;">
            <a href="newProposalMaster.htm?proposalId=0" class="easyui-linkbutton" data-options="iconCls:'icon-add'" style="width:15%">Create Increment Proposal</a>
        </div>
    </body>
</html>
