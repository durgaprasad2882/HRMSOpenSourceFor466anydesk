<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Human Resources Management System, Government of Odisha</title>

        <link rel="stylesheet" type="text/css" href="resources/css/themes/default/easyui.css">
        <link rel="stylesheet" type="text/css" href="resources/css/themes/icon.css">

        <script type="text/javascript" src="js/jquery.min-1.9.1.js"></script>
        <script type="text/javascript" src="js/jquery.easyui.min.js"></script>
        <script type="text/javascript">
            $(document).ready(function() {
                $('#win').window({
                    onBeforeClose: function() {
                        $('#loandg').datagrid('reload');
                    }
                })
            });


            function openWindow(linkurl, modname) {
                $("#winfram").attr("src", linkurl);
                $("#win").window("open");
                $("#win").window("setTitle", modname);

            }
        </script>
    </head>
    <body>


        <table id="loandg" class="easyui-datagrid" style="width:100%;height:400px;" title="Loan List"
               rownumbers="true" singleSelect="true" url="GetLoanListJSON.htm" singleSelect="true" pagination="true" collapsible="false"
               data-options="nowrap:false" toolbar="#toolbar">
            <thead>
                <tr>
                    <th data-options="field:'loanid',width:80,formatter:loanId">Loan Id</th>
                    <th data-options="field:'loanType',width:200">Loan Type</th>
                    <th data-options="field:'loanDate',width:200">Loan Date</th>

                    <th data-options="field:'loanStatus',width:200,formatter:loanstatus">Loan Status</th>
                    <th data-options="field:'reapply',width:'10%',formatter:reapplyloan" align='center'>&nbsp;</th>  
                </tr> `
            </thead>
        </table>
        <div id="toolbar">
            <a href="loanapplyController.htm" class="easyui-linkbutton" iconCls="icon-add" plain="true">Apply Motor Car/Moped/computer Loan</a>
            <a href="loangpfapplyController.htm" class="easyui-linkbutton" iconCls="icon-add" plain="true">GPF(REFUNDABLE)</a>
            <a href="loangpfapplyController.htm" class="easyui-linkbutton" iconCls="icon-add" plain="true">GPF(NON-REFUNDABLE)</a>
            <a href="loanhbaapplyController.htm" class="easyui-linkbutton" iconCls="icon-add" plain="true">House Building Advance</a>


        </div>

        <div  id="win" class="easyui-window" title="My Window" data-options="modal:true,closed:true,iconCls:'icon-window'" closed="true" style="width:100%;height:90%;padding:5px;">
            <iframe id="winfram"  frameborder="0" scrolling="yes" marginheight="0" marginwidth="0" height="100%" width="100%"></iframe>
        </div>
        <script type='text/javascript'>
            function reapplyloan(val, row)
            {
                if (row.loanstatusid == 28) {
                    var str1 = '<a href="javascript:void(0)" onclick="openWindow(\'ReapplyLoanAction.htm?opt=reapply&loanid=' + row.loanid + '\', \'Reapply Loan\')" title="Reapply Loan " ><img src="images/revert.png" alt="Reapply Loan" height="20px" width="20px" /></a>';
                    return str1;
                } else if (row.loanstatusid == 38) {
                    var str1 = '<a href="javascript:void(0)" onclick="openWindow(\'ReapplygpfLoan.htm?opt=reapply&loanid=' + row.loanid + '\', \'Reapply GPF LTA\')" title="Reapply GPF LTA " ><img src="images/revert.png" alt="Reapply GPF LTA" height="20px" width="20px" /></a>';
                    return str1;
                } else if (row.loanstatusid == 46) {
                    var str1 = '<a href="javascript:void(0)" onclick="openWindow(\'ReapplyhbaLoan.htm?opt=reapply&loanid=' + row.loanid + '\', \'Reapply HBA\')" title="Reapply HBA" ><img src="images/revert.png" alt="Reapply HBA" height="20px" width="20px" /></a>';
                    return str1;
                } else if (row.loanstatusid == 26) {
                    var str1 = '<a href="javascript:void(0)" onclick="openWindow(\'SactionOrder.htm?taskid=' + row.taskid + '&loanid=' + row.loanid + '\', \'Saction Order\')" title="Saction Order "><img src="images/action.png" alt="Saction Order" height="20px" width="20px" /></a>';
                    return str1;

                }
            }
            function loanstatus(val, row)
            {
                if (row.loanstatusid == 28) {
                    var str1 = '<span style="color:red;font-weight:bold">' + row.loanStatus + '</span>';
                    return str1;
                } else if (row.loanstatusid == 26 || row.loanstatusid == 27 || row.loanstatusid == 36 || row.loanstatusid == 37 || row.loanstatusid == 44 || row.loanstatusid == 45) {
                    var str1 = '<span style="color:#468EC9;font-weight:bold">' + row.loanStatus + '</span>';
                    return str1;

                } else {
                    return row.loanStatus;
                }
            }
            function loanId(val, row)
            {
                if (row.loanstatusid == 28 || row.loanstatusid == 38 || row.loanstatusid == 46) {
                    var str1 = '<span style="color:red;font-weight:bold">' + row.loanid + '</span>';
                    return str1;
                } else if (row.loanstatusid == 26 || row.loanstatusid == 27 || row.loanstatusid == 36 || row.loanstatusid == 37 || row.loanstatusid == 44 || row.loanstatusid == 45) {
                    var str1 = '<span style="color:#468EC9;font-weight:bold">' + row.loanid + '</span>';
                    return str1;

                } else {
                    return row.loanid;
                }
            }

        </script>   
    </body>

</html>
