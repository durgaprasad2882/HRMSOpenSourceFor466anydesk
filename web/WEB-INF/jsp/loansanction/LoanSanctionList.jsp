
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<%@ page contentType="text/html;charset=UTF-8"%>

<%
    String contextPath = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + contextPath + "/";
    int i = 0;
%>
<html>
    <base href="<%=basePath%>"></base>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>Human Resources Management System, Government of Odisha</title>      
    <link href="css/bootstrap.min.css" rel="stylesheet">
    <link href="font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">

    <script type="text/javascript" src="js/jquery.min-1.9.1.js"></script>        
    <script type="text/javascript" src="js/bootstrap.min.js"></script>

    <script language="javascript" type="text/javascript">

        function searchAuthority() {
            var url = 'sancauthority.htm';
            $.colorbox({href: url, iframe: true, open: true, width: "70%", height: "450px", top: "10px", overlayClose: false});
        }
        function SelectSpn(spc, empid, post, empname, deptCode, offCode)
        {
            $.colorbox.close();
            $('#authority').textbox('setValue', empname + "," + post);
            $('#hidAuthEmpId').val(empid);
            $('#hidSpcAuthCode').val(spc);
            $('#hidAuthDeptCode').val(deptCode);
            $('#hidAuthOffCode').val(offCode);

        }
        function editLoanDetail() {
            // var href = 'loanData.htm?loanId=' + row.loanId;
            var row = $('#dgloan').datagrid('getSelected');
            $('#hidEmpId').val($('#empid').val());
            if (row) {
                $('#dlg').dialog('open').dialog('center').dialog('setTitle', 'Edit Loan');
                //$('#dlg').maximizable(true);
                $('#fm').form('load', 'loanData.htm?loanId=' + row.loanid + '&notId=' + row.notid);
                $('#dlg').dialog('reload');
            }
        }
        function deleteLoan() {
            // var href = 'loanData.htm?loanId=' + row.loanId;
            var row = $('#dgloan').datagrid('getSelected');
            if (row) {

                // $('#dlgmov').dialog('open').dialog('setTitle', 'Edit Immovable Property');
                $('#fm').form('load', 'deleteLoan.htm?loanId=' + row.loanid + '&notId=' + row.notid);
                $('#dgloan').datagrid('reload');


            }

        }
        function viewDetail(value, row, index) {
            var href = 'loanData1.htm?loanId=' + row.loanId;
        }
        function newLoan() {
            $('#fm').form('clear');
            $('#hidEmpId').val($('#empid').val());
            $('#dlg').dialog('open').dialog('center').dialog('setTitle', 'Edit Loan');
            $('#fm').form('load', 'newloanData.htm');
        }
        function actionEvent() {
            var nowDeduct = $('#txtNowDeduct').combobox('getValue');
            alert(nowDeduct);
        }
        function saveLoanDetails() {
            $('#fm').form('submit', {
                url: 'saveLoanAction.htm',
                success: function(result) {
                    var result = eval('(' + result + ')');
                    if (result.errorMsg) {
                        $.messager.show({
                            title: 'Error',
                            msg: result.errorMsg
                        });
                    } else {
                        $('#dlg').dialog('close'); // close the dialog
                        $('#dgloan').datagrid('reload'); // reload the user data
                    }
                }

            });
        }
    </script>
    <style type="text/css">
        #fm{
            margin:0;
            padding:10px 30px;
        }
        .ftitle{
            font-size:14px;
            font-weight:bold;
            padding:5px 0;
            margin-bottom:10px;
            border-bottom:1px solid #ccc;
        }
        .fitem{
            margin-bottom:5px;
        }
        .fitem label{
            display:inline-block;
            width:20%;
        }
        .fitem1 label{
            display:inline-block;
            width:80%;
        }
    </style>
</head>
<body>
    <form:form action="newloanData.htm" method="post"  commandName="command">
        <div class="container-fluid">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <div class="row">
                        <div class="col-lg-12">

                        </div>
                    </div>
                </div>
                <div class="panel-body">
                    <table class="table table-bordered">
                        <thead>
                            <tr>
                                <th width="5%">Sl No</th>
                                <th width="40%">Loan Name</th>
                                <th width="30%">Amount</th>
                                <th>Is Recovered</th>
                                <th width="15%">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${loanSancList}" var="loanSanc" varStatus="cnt">
                                <tr>
                                    <td>${cnt.index+1}</td>
                                    <td>${loanSanc.sltloan}</td>
                                    <td>${loanSanc.txtamount}</td>
                                    <td>
                                        <c:if test="${loanSanc.completedRecovery == 1}">
                                            <span style="color: #ff0000;">Recovered</span>
                                        </c:if>
                                        <c:if test="${loanSanc.completedRecovery == 0}">
                                            <span style="color: #00cccc;">Continuing</span>
                                        </c:if>
                                    </td>
                                    <td><a href="editloanData.htm?loanid=${loanSanc.loanid}">Edit</a></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
                <div class="panel-footer">

                    <form:hidden class="form-control" id="empid" path="empid" />
                    <button type="submit" class="btn btn-default">New Loan</button>  

                </div>
            </div>
        </div>
    </form:form>
</body>
</html>
