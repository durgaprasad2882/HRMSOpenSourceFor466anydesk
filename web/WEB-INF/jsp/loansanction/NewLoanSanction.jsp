<%-- 
    Document   : NewLoanSanction
    Created on : Oct 26, 2017, 12:54:11 PM
    Author     : Manas
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <title>Human Resources Management System, Government of Odisha</title>      
        <link href="css/bootstrap.min.css" rel="stylesheet">
        <link href="font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
        <link href="css/bootstrap-datetimepicker.css" rel="stylesheet" type="text/css"/>
        <script src="js/moment.js" type="text/javascript"></script>
        <script src="js/jquery.min.js" type="text/javascript"></script>           
        <script type="text/javascript" src="js/bootstrap.min.js"></script>
        <script type="text/javascript" src="js/bootstrap-datetimepicker.js"></script>
        <script language="javascript" type="text/javascript">
            function getBranchList(me) {
                $('option', $('#branch')).not(':eq(0)').remove();
                $.ajax({
                    type: "POST",
                    url: "getBranchListJSON.htm?bankCode=" + $(me).val(),
                    success: function(data) {
                        $.each(data, function(i, obj)
                        {
                            $('#branch').append($('<option>', {
                                value: obj.branchcode,
                                text: obj.branchname
                            }));

                        });
                    }
                });
            }
        </script>
    </head>
    <body>
        <form:form action="saveloanSanction.htm" method="post" commandName="command">
            <div class="container-fluid">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        Employee Loan Sanction
                    </div>        
                    <div class="panel-body">
                        <div class="row" style="margin-bottom: 7px;">
                            <div class="col-lg-2">
                                <label for="orderno">Order No:</label>
                            </div>
                            <div class="col-lg-2">   
                                <form:hidden path="empid" />
                                <form:hidden path="loanid"/>
                                <form:input path="orderno" class="form-control" id="orderno"/>
                            </div>
                            <div class="col-lg-2">
                                <label for="orderdate">Order Date:</label>
                            </div>
                            <div class="col-lg-2">
                                <div class='input-group date' id='processDate'>
                                    <form:input class="form-control" id="orderdate" path="orderdate" />
                                    <span class="input-group-addon">
                                        <span class="glyphicon glyphicon-time"></span>
                                    </span>
                                </div>                                
                            </div>
                        </div>
                        <div class="row" style="margin-bottom: 7px;">
                            <div class="col-lg-2">
                                <label for="sltloan">Loan Name:</label>
                            </div>
                            <div class="col-lg-2">
                                <form:select path="sltloan" id="sltloan" class="form-control">
                                    <option value="">Select Loan</option>
                                    <form:options items="${loanTypeList}" itemValue="loanType" itemLabel="loanName"/> 
                                </form:select>
                            </div>
                        </div>
                        <div class="row" style="margin-bottom: 7px;">
                            <div class="col-lg-2">
                                <label for="authority">Sanctioning Authority:</label>
                            </div>
                            <div class="col-lg-9">
                                <input type="text" name="authority" class="form-control" id="authority">                           
                            </div>
                            <div class="col-lg-1">
                                <button type="button" class="btn btn-primary" onclick="searchAuthority()">
                                    <span class="glyphicon glyphicon-search"></span> Search
                                </button>
                            </div>
                        </div>
                        <div class="row" style="margin-bottom: 7px;">
                            <div class="col-lg-2">
                                <label for="txtamount">Amount in Rs.:</label>
                            </div>
                            <div class="col-lg-2">                            
                                <form:input path="txtamount" class="form-control" id="txtamount"/>
                            </div>
                            <div class="col-lg-2">
                                <label for="accountNo">Account No :</label>
                            </div>
                            <div class="col-lg-2">
                                <form:input path="accountNo" class="form-control" id="accountNo"/>
                            </div>
                        </div>
                        <div class="row" style="margin-bottom: 7px;">
                            <div class="col-lg-2">
                                <label for="bank">Bank Name:</label>
                            </div>
                            <div class="col-lg-2">
                                <form:select path="bank" id="bank" class="form-control" onchange="getBranchList(this)">
                                    <option value="">Select Bank</option>
                                    <form:options items="${bankList}" itemValue="bankcode" itemLabel="bankname"/> 
                                </form:select>                            
                            </div>
                            <div class="col-lg-2">
                                <label for="branch">Branch Name :</label>
                            </div>
                            <div class="col-lg-2">
                                <form:select path="branch" class="form-control" id="branch">
                                    <option value="">Select Branch</option>
                                </form:select>
                            </div>
                        </div>
                        <div class="row" style="margin-bottom: 7px;">
                            <div class="col-lg-2">
                                <label>Treasury voucher No:<span style="color: red">*</span></label>
                            </div>
                            <div class="col-lg-2">
                                <input class="form-control" id="voucherNo" type="text" name="voucherNo" ></input>
                            </div>
                            <div class="col-lg-2">
                                <label >Date:<span style="color: red">*</span></label>
                            </div>
                            <div class="col-lg-2">
                                <div class='input-group date' id='processDate'>
                                    <form:input class="form-control" id="voucherDate" path="voucherDate" />
                                    <span class="input-group-addon">
                                        <span class="glyphicon glyphicon-time"></span>
                                    </span>
                                </div>
                            </div>                        
                        </div>
                        <div class="row" style="margin-bottom: 7px;">
                            <div class="col-lg-2">
                                <label>Treasury :<span style="color: red">*</span></label>
                            </div>
                            <div class="col-lg-2">
                                <form:select path="treasuryname" class="form-control" id="treasuryname">
                                    <option value="">Select Treasury</option>
                                    <form:options items="${treasuryList}" itemValue="treasuryCode" itemLabel="treasuryName"/>
                                </form:select>
                            </div>                                                
                        </div>
                        <div class="row" style="margin-bottom: 7px;margin-top: 65px;">
                            <div class="col-lg-2">
                                <label>Now Deduct: :<span style="color: red">*</span></label>
                            </div>
                            <div class="col-lg-2">
                                <select name="nowDeduct" class="form-control" id="nowDeduct">
                                    <option value="P">PRINCIPAL</option>
                                    <option value="I">INTEREST</option>
                                </select>
                            </div>                                                
                        </div>
                        <div class="row" style="margin-bottom: 7px;">
                            <div class="col-lg-12">
                                <table width="100%">                                
                                    <tr>                                    
                                        <td><label>Original Amount</label></td>
                                        <td><label>Total No of Instal.</label></td>
                                        <td><label>Instalment Amount</label></td>
                                        <td><label>Paid Instal No</label></td>
                                        <td><label>Monhtly Instl No(if required)</label></td>
                                        <td><label>Cumulative Amount paid</label></td>
                                        <td><label>Completed Recovery</label></td>
                                    </tr>
                                    <tr>                                    
                                        <td><form:input path="originalAmt" class="form-control" id="originalAmt"/></td>
                                        <td><form:input path="totalNoOfInsl" class="form-control" id="totalNoOfInsl"/></td>
                                        <td><form:input path="instalmentAmount" class="form-control" id="instalmentAmount"/></td>
                                        <td><form:input path="lastPaidInstalNo" class="form-control" id="lastPaidInstalNo"/></td>
                                        <td><form:input path="monthlyinstlno" class="form-control" id="monthlyinstlno"/></td>
                                        <td><form:input path="cumulativeAmtPaid" class="form-control" id="cumulativeAmtPaid"/></td>
                                        <td><form:checkbox path="completedRecovery" class="form-control" value="1" id="completedRecovery"/></td>
                                    </tr>                                
                                </table>
                            </div>
                        </div>
                    </div>            
                    <div class="panel-footer">
                        <button type="submit" class="btn btn-default">Save Loan</button>  
                    </div>
                </div>
            </div>
        </form:form>
        <script type="text/javascript">
            $(function() {
                $('#voucherDate').datetimepicker({
                    format: 'D-MMM-YYYY'
                });
                $('#orderdate').datetimepicker({
                    format: 'D-MMM-YYYY'
                });
            });
        </script>
    </body>
</html>
