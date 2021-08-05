<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<!DOCTYPE html>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>PaySlip List Page</title>

        <link rel="stylesheet" type="text/css" href="resources/css/themes/default/easyui.css">
        <link rel="stylesheet" type="text/css" href="resources/css/themes/icon.css">

        <!-- LAYOUT v 1.3.0 -->
        <script type="text/javascript" src="js/jquery.min-1.9.1.js"></script>
        <script type="text/javascript" src="js/jquery.easyui.min.js"></script>
        <script type="text/javascript">
            function showPaylist(){
                var year = $('#sltYear').combobox('getValue');
		var tyear = $('#sltYear').combobox('getText');
                var month = $('#sltMonth').combobox('getValue');
		var tmonth = $('#sltMonth').combobox('getText');
                
                if(year == ""){
                    alert("Please select Year");
                    return false;
                }else if(year == tyear){
                    alert("Invalid Year");
                    return false;
                }
				
                if(month == ""){
                    alert("Please select Month");
                    return false;
                }else if(month == tmonth){
                    alert("Invalid Month");
                    return false;
                }
                
                $('#payslipdg').datagrid({
                    url : "GetPaySlip.htm?empid="+$('#empid').val()+"&sltYear="+year+"&sltMonth="+month
                });
            }
            
            function viewPaySlipDetail(val,row){
                var aqslno = row.aqslno;
                var year = $('#sltYear').combobox('getValue');
                var month = $('#sltMonth').combobox('getValue');
                return "<a href='PaySlipDetail.htm?aqlsno="+aqslno+"&sltYear="+year+"&sltMonth="+month+"&empid="+$('#empid').val()+"' target='_blank'>View</a>";
            }
        </script>
    </head>
    <body>
        <div align="center">
            <input type="hidden" name="empid" id="empid" value="${empid}"/>
            <table border="0" width="100%" cellspacing="0" cellpadding="0">
                <tr>
                    <td width="25%" align="center">
                        Select Year:
                    </td>
                    <td width="20%">
                        <select name="sltYear" id="sltYear" class="easyui-combobox">
                            <option value="">--Select--</option>
                            <option value="2012">2012 </option>
                            <option value="2013">2013 </option>
                            <option value="2014">2014 </option>
                            <option value="2015">2015 </option>
                            <option value="2016">2016 </option>
							<option value="2017">2017 </option>
                        </select>
                    </td>
                    <td width="25%" align="center">
                        Select Month:
                    </td>
                    <td width="30%">
                        <select name="sltMonth" id="sltMonth" class="easyui-combobox">
                            <option value="">--Select--</option>
                            <option value="0">Jan</option>
                            <option value="1">Feb</option>
                            <option value="2">Mar</option>
                            <option value="3">Apr</option>
                            <option value="4">May</option>
                            <option value="5">Jun</option>
                            <option value="6">Jul</option>
                            <option value="7">Aug</option>
                            <option value="8">Sep</option>
                            <option value="9">Oct</option>
                            <option value="10">Nov</option>
                            <option value="11">Dec</option>
                        </select>
                        &nbsp;&nbsp;&nbsp;&nbsp;
                        <button type="submit" class="easyui-linkbutton" onclick="showPaylist()">Ok</button>
                    </td>
                </tr>
            </table>
            <table id="payslipdg" class="easyui-datagrid" style="width:100%;height:400px;" title="My Pay Slip"
                   rownumbers="true" singleSelect="true" singleSelect="true" pagination="true" collapsible="false"
                   data-options="nowrap:false">
                <thead>
                    <tr>
                        <th data-options="field:'aqslno',width:20,hidden:true">AQSLNo</th>
                        <th data-options="field:'month_year',width:80">Month-Year</th>
                        <th data-options="field:'basic',width:150">Basic Pay</th>
                        <th data-options="field:'totallowance',width:150">Total<br>Allowances</th>
                        <th data-options="field:'gross',width:200">Gross Pay</th>
                        <th data-options="field:'totdeduction',width:150">Total<br>Deductions</th>
                        <th data-options="field:'netpay',width:100">Net Pay</th>
                        <th data-options="field:'temp',width:100,formatter:viewPaySlipDetail">View</th>
                    </tr> 
                </thead>
            </table>
        </div>
	<div align="center">
            <span style="color:red">Pay Slips of only Token Generated Bills are shown in the list.</span>
        </div>
    </body>
</html>

