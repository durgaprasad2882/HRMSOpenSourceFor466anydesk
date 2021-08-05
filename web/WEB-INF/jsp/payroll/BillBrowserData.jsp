<%-- 
    Document   : BillBrowserData
    Created on : Oct 30, 2017, 11:42:46 PM
    Author     : Manas

--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Human Resources Management System, Government of Odisha</title>
        <link href="css/bootstrap.min.css" rel="stylesheet">
        <link href="font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">        
        <link href="css/bootstrap-datetimepicker.css" rel="stylesheet" type="text/css"/>
        <script src="js/moment.js" type="text/javascript"></script>
        <script src="js/jquery.min.js" type="text/javascript"></script>        
        <script src="js/bootstrap.min.js" type="text/javascript"></script>
        <script src="js/bootstrap-datetimepicker.js" type="text/javascript"></script>
        <script type="text/javascript">
            function validateForm() {

                if ($("#billdesc").val() == '') {
                    alert('Please enter Bill number.');
                    $("#billdesc").focus();
                    return false;
                }
                if ($("#billDate").val() == '') {
                    alert('Please enter Bill Date.');
                    $("#billDate").focus();
                    return false;
                }else{
                    var obj=$("#billDate").val();
                    ret=isDate($("#billDate").val(), 'Incorrect date');
                    if(ret==false){
                        $("#billDate").focus();
                        return false;
                    }
                }

                if ($("#treasury").val() == '') {
                    alert('Please Select Treasury .');
                    $("#treasury").focus();
                    return false;
                }

            }


            function isDate(date, msg) {
                // dd-mmm-yyyy format
                
                if (date.length == 0) {
                    return true; // Ignore null value
                }
                if (date.length == 10) {
                    date = "0" + date; // Add a leading zero
                }
                if (date.length != 11) {
                    alert(msg);
                    return false;
                }
                day = date.substring(0, 2);
                month = date.substring(3, 6).toUpperCase();
                year = date.substring(7, 11);
                if (isNaN(day) || (day < 0) || isNaN(year) || (year < 1)) {
                    alert(msg);
                    return false;
                }

                // Ensure valid month and set maximum days for that month...
                if ((month == "JAN") || (month == "jan") || (month == "MAR") || (month == "mar") || (month == "MAY") || (month == "may") ||
                        (month == "JUL") || (month == "jul") || (month == "AUG") || (month == "aug") || (month == "OCT") || (month == "oct") ||
                        (month == "DEC")) {
                    monthdays = 31
                }
                else if ((month == "APR") || (month == "apr") || (month == "JUN") || (month == "jun") || (month == "sep") || (month == "SEP") ||
                        (month == "NOV") || (month == "nov")) {
                    monthdays = 30
                }
                else if ((month == "FEB") || (month == "feb") ) {
                    monthdays = ((year % 4) == 0) ? 29 : 28;
                }
                else {
                    alert(msg);
                    return false;
                }
                if (day > monthdays) {
                    alert(msg);
                    return false;
                }
                return true;
            }


        </script>
    </head>
    <body>
        <div class="container-fluid">
            <form:form class="form-inline" action="saveBill.htm" method="POST" commandName="command">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        Chart of Account: ${command.chartofAcct}                  
                    </div>
                    <div class="panel-body">
                        <div class="row">
                            <div class="col-lg-12">
                                <table class="table table-bordered">
                                    <tr>
                                        <td width="15%" align="left">&nbsp;&nbsp;Bill ID
                                            <form:hidden path="billNo"/>
                                            <form:hidden path="txtbilltype"/>
                                            <form:hidden path="sltYear"/>
                                            <form:hidden path="sltMonth"/>
                                        </td>
                                        <td width="15%" >
                                            ${command.billNo}                      
                                        </td>

                                        <td width="15%" align="left">Treasury</td>
                                        <td width="45%" align="left">
                                            <form:select path="treasury" id="treasury" class="form-control">
                                        <option value=""> Select Treasury </option>
                                        <form:options items="${treasuryList}" itemValue="treasuryCode" itemLabel="treasuryName"/>
                                    </form:select>
                                    </td>                                
                                    </tr>
                                    <tr>
                                        <td  align="left">&nbsp;&nbsp;Bill No</td>
                                        <td>
                                            
                                                <form:input path="billdesc" id="billdesc" class="form-control"/>
                                          
                                        </td>
                                        <td align="left">DDO Name and Designation</td>
                                        <td align="left"></td>
                                    </tr>
                                    <tr>
                                        <td align="left">&nbsp;&nbsp;Bill Date (dd-MMM-yyyy) ex. 01-JAN-2018</td>
                                        <td>
                                            
                                            <form:input path="billDate" id="billDate" class="form-control"/> </td>
                                       
                                        <td align="left">TV No & Date</td>
                                        <td align="left">

                                        </td>
                                    </tr>
                                    <tr>
                                        <td align="left">&nbsp;&nbsp;Bill Type</td>
                                        <td>${billDtls.billType}</td>                       
                                        <td  align="left">Received Rs.</td>
                                        <td  align="left">
                                            &nbsp;
                                        </td>
                                    </tr>
                                    <tr>
                                        <td  align="left">&nbsp;&nbsp;Description</td>
                                        <td>${command.billdesc}</td>
                                        <td align="left">Instrument No</td>
                                        <td align="left">&nbsp;</td>
                                    </tr>
                                    <tr>
                                        <td align="left">&nbsp; Benificiary/ Reference &nbsp;&nbsp;Number(iOTMS)</td>
                                        <td><form:input path="benificiaryNumber" class="form-control"/> </td>
                                        <td align="left">Received By</td>
                                        <td align="left">&nbsp;</td>
                                </table>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-lg-4">
                                <div align="center"><b>ALLOWANCE</b></div>
                                <table class="table table-bordered">
                                    <thead>
                                        <tr height="30px">
                                            <th width="10%" align="center" >SL NO.</th>
                                            <th width="45%" align="center">ALLOWANCES</th>
                                            <th width="20%" align="center">OBJECT HEAD</th>
                                            <th width="25%" align="right" >AMOUNT</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${allowanceList}" var="allowance" varStatus="cnt">
                                            <tr>
                                                <td>${cnt.index+1}</td>
                                                <td>${allowance.adname}</td>
                                                <td>${allowance.objecthead}</td>
                                                <td>${allowance.adamount}</td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                            <div class="col-lg-4">
                                <div align="center"><b>DEDUCTION</b></div>
                                <table class="table table-bordered">
                                    <tr height="30px">
                                        <th width="10%" align="center">SL No.</th>
                                        <th width="40%" align="center" >DEDUCTIONS</th>
                                        <th width="20%" align="center">BY TRANSFER</th>
                                        <th width="30%" align="right">AMOUNT</th>
                                    </tr>
                                    <tbody>
                                        <c:forEach items="${deductionList}" var="deduction" varStatus="cnt">
                                            <tr>
                                                <td>${cnt.index+1}</td>
                                                <td>${deduction.adname}</td>
                                                <td>${deduction.objecthead}</td>
                                                <td>${deduction.adamount}</td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                            <div class="col-lg-4">
                                <div align="center"><b>PRIVATE LOAN</b></div>
                                <table class="table table-bordered">
                                    <thead>
                                        <tr height="30px">
                                            <th width="10%" align="center">SL No.</th>
                                            <th width="60%" align="center" >LOAN</th>
                                            <th width="30%" align="right">AMOUNT</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${pvtloanList}" var="pvtloan" varStatus="cnt">
                                            <tr>
                                                <td>${cnt.index+1}</td>
                                                <td>${pvtloan.adname}</td>
                                                <td>${pvtloan.objecthead}</td>
                                                <td>${pvtloan.adamount}</td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-lg-12">
                                <table class="table table-bordered">
                                    <tr style="background-color:#E9E9EA;">
                                        <th width="87" scope="col"><div align="center" class="style9">HEAD</div></th>
                                    <th width="102" scope="col"><div align="center" class="style9">GPF/GPF ADV.(P) </div></th>
                                    <th width="56" scope="col"><div align="center" class="style9">AISGPF</div></th>
                                    <th width="67" scope="col"><div align="center" class="style9">CPF/NPS</div></th>
                                    <th width="52" scope="col"><div align="center" class="style9">AISGIS</div></th>

                                    <th width="94" scope="col"><div align="center" class="style9">TPF/TPF ADV(P) </div></th>
                                    <th width="32" scope="col"><div align="center" class="style9">LIC</div></th>
                                    <th width="53" scope="col"><div align="center" class="style9">SHBA(P)</div></th>
                                    <th width="53" scope="col"><div align="center" class="style9">HBA(P)</div></th>
                                    <th width="112" scope="col"><div align="center" class="style9">MC/MOPED/CAR(P)</div></th>
                                    <th width="77" scope="col"><div align="center" class="style9">BICY ADV(P) </div></th>
                                    <th width="77" scope="col"><div align="center" class="style9">GIS ADV(P) </div></th>

                                    <th width="77" scope="col"><div align="center" class="style9">CMPTR ADV(P) </div></th>
                                    <th width="77" scope="col"><div align="center" class="style9">PLI </div></th>
                                    <th width="77" scope="col"><div align="center" class="style9">IT </div></th>
                                    <th width="77" scope="col"><div align="center" class="style9">DAO/ALLIED GPF </div></th>
                                    </tr>
                                    <tr style="background-color:#E2F4FA;">
                                        <th scope="row"><span class="style7">Previous BT ID </span></th>
                                        <td><div align="center"><span class="style3">8690</span></div></td>
                                        <td><div align="center"><span class="style3">8692</span></div></td>
                                        <td><div align="center"><span class="style3">30594</span></div></td>
                                        <td><div align="center"><span class="style3">8693</span></div></td>

                                        <td><div align="center"><span class="style3">7058</span></div></td>
                                        <td><div align="center"><span class="style3">7100</span></div></td>
                                        <td><div align="center"><span class="style3">7049</span></div></td>
                                        <td><div align="center"><span class="style3">8678</span></div></td>
                                        <td><div align="center"><span class="style3">8679</span></div></td>
                                        <td><div align="center"><span class="style3">30013</span></div></td>
                                        <td><div align="center"><span class="style3">8680</span></div></td>

                                        <td><div align="center"><span class="style3">30015</span></div></td>
                                        <td><div align="center"><span class="style3">7108</span></div></td>
                                        <td><div align="center"><span class="style3">7112</span></div></td>
                                        <td><div align="center"><span class="style3">30020</span></div></td>
                                    </tr>
                                    <tr>
                                        <th scope="row"><span class="style7">New BT ID </span></th>
                                        <td><div align="center"><span class="style3">55545</span></div></td>
                                        <td><div align="center"><span class="style3">57649</span></div></td>
                                        <td><div align="center"><span class="style3">57740</span></div></td>
                                        <td><div align="center"><span class="style3">58829</span></div></td>

                                        <td><div align="center"><span class="style3">55550</span></div></td>
                                        <td><div align="center"><span class="style3">55832</span></div></td>
                                        <td><div align="center"><span class="style3">55522</span></div></td>
                                        <td><div align="center"><span class="style3">55521</span></div></td>
                                        <td><div align="center"><span class="style3">55525</span></div></td>
                                        <td><div align="center"><span class="style3">57633</span></div></td>
                                        <td><div align="center"><span class="style3">57639</span></div></td>

                                        <td><div align="center"><span class="style3">57635</span></div></td>
                                        <td><div align="center"><span class="style3">58600</span></div></td>
                                        <td><div align="center"><span class="style3">58816</span></div></td>
                                        <td><div align="center"><span class="style3">58239</span></div></td>

                                    </tr>
                                </table>
                            </div>
                        </div>
                    </div>                
                    <div class="panel-footer">
                        <c:if test="${command.status lt 2}">
                            <input type="submit" name="action" value="Reprocess" class="btn btn-default"/>
                            <input type="submit" name="action" value="Save" class="btn btn-default" onclick="return validateForm()"/>
                            <input type="submit" name="action" value="ChangeChartofAccount" class="btn btn-default"/>
                        </c:if>
                        <c:if test="${command.status == 4}">
                            <input type="submit" name="action" value="Reprocess" class="btn btn-default"/>
                            <input type="submit" name="action" value="Save" class="btn btn-default" onclick="return validateForm()"/>
                            <input type="submit" name="action" value="Back" class="btn btn-default"/>
                        </c:if>
                        <c:if test="${command.status == 8}">
                            <input type="submit" name="action" value="Reprocess" class="btn btn-default"/>
                            <input type="submit" name="action" value="Save" class="btn btn-default" onclick="return validateForm()"/>
                            <input type="submit" name="action" value="ChangeChartofAccount" class="btn btn-default"/>
                        </c:if>
                        <input type="submit" name="action" value="Cancel" class="btn btn-default"/>
                        
                    </div>
                </div>
            </form:form>
        </div>
    </body>
</html>
