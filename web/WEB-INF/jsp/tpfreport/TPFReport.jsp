<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Human Resources Management System, Government of Odisha</title>

        <link rel="stylesheet" type="text/css" href="resources/css/themes/default/easyui.css">
        <link rel="stylesheet" type="text/css" href="resources/css/themes/icon.css">

        <script type="text/javascript" src="js/jquery.min-1.9.1.js"></script>
        <script type="text/javascript" src="js/jquery.easyui.min.js"></script>
        <link href="css/bootstrap.min.css" rel="stylesheet">
        <link href="font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
        <!-- Custom CSS -->
        <link href="css/sb-admin.css" rel="stylesheet">

        <!-- Bootstrap Core JavaScript -->
        <script src="js/bootstrap.min.js"></script>

        <script type="text/javascript">
            $(document).ready(function() {
                $('#treasury').combobox({
                    onSelect: function(record) {
                        $('#ddocode').combobox('clear');
                        var url = 'treasuryDDOListJSON.htm?trcode=' + record.treasuryCode;
                        $('#ddocode').combobox('reload', url);
                    }
                });
            });

            function getBillAmount() {
                var treasury = $('#treasury').combobox('getValue');
                var ddocode = $('#ddocode').combobox('getValue');
                var year = $('#sltYear').combobox('getValue');
                var month = $('#sltMonth').combobox('getValue');

                if (treasury == '') {
                    alert("Please select Treasury");
                } else if (ddocode == '') {
                    alert("Please select DDO Code");
                } else if (year == '') {
                    alert("Please select Year");
                } else if (month == '') {
                    alert("Please select Month");
                } else {
                    var url = 'treasuryGetBillAmtJSON.htm?ddocode=' + ddocode + '&year=' + year + '&month=' + month;
                    $('#billAmt').combobox('reload', url);
                }
            }

            function openTPFReport() {
                var treasury = $('#treasury').combobox('getValue');
                var ddocode = $('#ddocode').combobox('getValue');
                var year = $('#sltYear').combobox('getValue');
                var month = $('#sltMonth').combobox('getValue');
                var billid = $('#billAmt').combobox('getValue');

                if (treasury == '') {
                    alert("Please select Treasury");
                } else if (ddocode == '') {
                    alert("Please select DDO Code");
                } else if (year == '') {
                    alert("Please select Year");
                } else if (month == '') {
                    alert("Please select Month");
                } else if (billid == '') {
                    alert("Please select Bill Amount");
                } else {
                    var url = "http://par.hrmsodisha.gov.in/TPFScheduleHTML.htm?billNo=" + billid;
                    window.open(url, "_blank");
                }
            }
        </script>
    </head>
    <body>

        <div id="wrapper">

            <jsp:include page="../tab/ServiceConditionAdminMenu.jsp"/>        
            <div id="page-wrapper">

                <div class="container-fluid">


                    <div align="center" style="margin-top:5px;margin-bottom:7px;">
                        <table border="0" cellspacing="0" cellpadding="0" style="font-size:12px; font-family:verdana;">
                            <tr style="height:40px;">
                                <td width="20%" align="center">
                                    TREASURY
                                </td>
                                <td width="30%">
                                    <input class="easyui-combobox" id="treasury" name="treasury" style="width:300px;" data-options="valueField:'treasuryCode',textField:'treasuryName',url:'getTreasuryListJSON.htm'">
                                </td>
                                <td width="20%" align="center">
                                    DDO CODE
                                </td>
                                <td width="30%">
                                    <input class="easyui-combobox" id="ddocode" name="ddocode" style="width:300px;" data-options="valueField:'value',textField:'label'">
                                </td>
                            </tr>

                            <tr style="height:40px;">
                                <td align="center">
                                    YEAR
                                </td>
                                <td>
                                    <input class="easyui-combobox" id="sltYear" name="sltYear" style="width:200px;" data-options="valueField:'value',textField:'label',url:'treasuryGetYearListJSON.htm'">
                                </td>
                                <td align="center">
                                    MONTH
                                </td>
                                <td>
                                    <select class="easyui-combobox" id="sltMonth" name="sltMonth" style="width:200px;">
                                        <option value="0">JANUARY</option>
                                        <option value="1">FEBUARY</option>
                                        <option value="2">MARCH</option>
                                        <option value="3">APRIL</option>
                                        <option value="4">MAY</option>
                                        <option value="5">JUNE</option>
                                        <option value="6">JULY</option>
                                        <option value="7">AUGUST</option>
                                        <option value="8">SEPTEMBER</option>
                                        <option value="9">OCTOBER</option>
                                        <option value="10">NOVEMBER<BER</option>
                                            <option value="11">DECEMBER</option>
                                    </select>

                                    <a href="javascript:getBillAmount()" class="easyui-linkbutton" id="btnGetBill" iconCls="icon-edit"/>GET BILL</a>
                                </td>
                            </tr>

                            <tr style="height:40px;">
                                <td align="center">
                                    Bill Amount
                                </td>
                                <td>
                                    <input class="easyui-combobox" id="billAmt" name="billAmt" style="width:400px;" data-options="valueField:'value',textField:'label'">
                                </td>
                                <td colspan="2">
                                    <a href="javascript:openTPFReport()" class="easyui-linkbutton" id="btnGetReport" iconCls="icon-add">VIEW REPORT</a>&nbsp;
                                    <a href="javascript:openTPFPaymentReport()" class="easyui-linkbutton" iconCls="icon-add">VIEW PAYMENT REPORT</a>
                                </td>
                            </tr>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
