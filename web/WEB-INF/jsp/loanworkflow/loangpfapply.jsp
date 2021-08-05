<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    String downloadlink = "";
    String deleteAttach = "";
    String loanid = "";
%>
<html>
    <head>
        <link rel="stylesheet" type="text/css" href="resources/css/themes/default/easyui.css">
        <link rel="stylesheet" type="text/css" href="resources/css/themes/icon.css">
        <link rel="stylesheet" type="text/css" href="resources/css/colorbox.css">

        <script type="text/javascript" src="js/jquery.min-1.9.1.js"></script>
        <script type="text/javascript" src="js/jquery.easyui.min.js"></script>
        <script type="text/javascript" src="js/jquery.colorbox-min.js"></script>
        <script language="javascript" src="js/jquery.datetimepicker.js" type="text/javascript"></script>
        <link href="css/jquery.datetimepicker.css" rel="stylesheet" type="text/css" />
        <script type="text/javascript">
            function applyloan()
            {
               var fup = document.getElementById('file_att');
                var fileName = fup.value;
                var ext = fileName.substring(fileName.lastIndexOf('.') + 1);
                var ext=ext.toLowerCase();
                
                if( (ext != "pdf" &&  ext != "zip") ){
                    alert("Upload pdf/zip files only");
                    fup.focus();
                    return false;
                }  
               var forwardto=$("#forwardto").val();
                 if ( forwardto == "") {
                    alert("Please select your loan authority ");
                    document.getElementById("forwardto").focus();
                    return false;
                }      
                var confirmloan = confirm("Do you want to apply GPF Long term advance ");
                if (!confirmloan) {
                    return false;
                }


            }


            function changepost() {
                var url = 'ChangePostLoanController.htm';
                $.colorbox({href: url, iframe: true, open: true, width: "80%", height: "50%"});
            }

            function SelectSpn(offCode, spc, offName, authName, spc_hrmsid)
            {
                $.colorbox.close();
                $('#hidSPC').val(spc);
                $('#hidOffCode').val(offCode);
                $("#hidOffName").val(offName);
                $("#forwardto").val(authName);
                $("#forwardtoHrmsid").val(spc_hrmsid);
            }

            $(document).ready(function () {
                $('.txtDate').datetimepicker({
                    timepicker: false,
                    format: 'd-M-Y',
                    closeOnDateSelect: true,
                    validateOnBlur: false
                });




            });


        </script>    
    </head>

    <body>       
        <form action="savegpfLoan.htm" method="POST" commandName="LoanForm" onsubmit="return applyloan()" enctype="multipart/form-data">
            <input type="hidden" name="hidOffCode" id="hidOffCode"/>
            <input type="hidden" name="hidOffName" id="hidOffName"/>
            <input type="hidden" name="hidSPC" id="hidSPC"/>
            <input type="hidden" name="forwardtoHrmsid" id="forwardtoHrmsid"/>
            <input type="hidden" name="gpftype" id="${LoanGPFForm.gpftype}"/>
            <input type="hidden" name="creditForm" id="creditForm" value="3/${LoanGPFForm.previousYear}"/>
            <input type="hidden" name="creditTo" id="creditTo" value="${LoanGPFForm.cmonth}/${LoanGPFForm.cyear}"/>
            <input type="hidden" name="withdrawfrom" id="withdrawfrom" value="3/${LoanGPFForm.previousYear}"/>
            <input type="hidden" name="withdrawto" id="withdrawto" value="${LoanGPFForm.cmonth}/${LoanGPFForm.cyear}"/>
            <input type="hidden" name="accountOfficer" id="accountOfficer" value="${LoanGPFForm.ddocode}"/>
            <input type="hidden" name="empSPC" id="empSPC" value="${LoanGPFForm.empSPC}"/>
            <div id="tbl-container" class="easyui-panel" title="Reapply Loan"  style="width:100%;overflow: auto;">

                <div align="left" style="padding-left:10px">
                    <h2 style="text-transform: uppercase">Proforma for application of ${LoanGPFForm.gpftype} withdrawal From GPF</h2>
                    <table style="width:100%">  
                        <tr>
                            <td style="width:10%">1.</td>
                            <td style="width:30%">Name of the subscriber</td>
                            <td style="width:60%"> 
                                ${LoanGPFForm.empName}
                            </td>
                        </tr>
                        <tr>
                            <td style="width:10%">2.</td>
                            <td style="width:30%">Account Number</td>
                            <td style="width:60%"> 
                                ${LoanGPFForm.gpfno}
                            </td>
                        </tr>
                        <tr>
                            <td style="width:10%">3.</td>
                            <td style="width:30%">Post</td>
                            <td style="width:60%"> 
                                ${LoanGPFForm.designation}
                            </td>
                        </tr>
                        <tr>
                            <td style="width:10%">4.</td>
                            <td style="width:30%">Pay</td>
                            <td style="width:60%"> 
                                ${LoanGPFForm.pay}
                            </td>
                        </tr>
                        <tr>
                            <td style="width:10%">5(i).</td>
                            <td style="width:30%">Date of Joining Service</td>
                            <td style="width:60%"> 
                                ${LoanGPFForm.doj}
                            </td>
                        </tr>
                        <tr>
                            <td style="width:10%">5(ii).</td>
                            <td style="width:30%">Date of Superannuation </td>
                            <td style="width:60%"> 
                                ${LoanGPFForm.supperannuation}
                            </td>
                        </tr>
                        <tr>
                            <td style="width:10%">6.</td>
                            <td style="width:30%">GPF Type </td>
                            <td style="width:60%"> 
                                <c:set var="gpftypestatus" value="${LoanGPFForm.gpftype}"/>
                                <input type='hidden' name='gpftype' value="REFUNDABLE"/>
                              <!--  <input type="radio" class="easyui-radio" name="gpftype"  value="NON-REFUNDABLE" <c:if test="${gpftypestatus == 'NON-REFUNDABLE'}"> checked='checked'</c:if> <c:if test="${gpftypestatus == 'REFUNDABLE'}"> disabled='disabled'</c:if>  />NON-REFUNDABLE<br/>
                                <input type="radio" class="easyui-radio" name="gpftype"  value="REFUNDABLE"  <c:if test="${gpftypestatus == 'REFUNDABLE'}"> checked='checked'</c:if> />REFUNDABLE<br/>-->
                                REFUNDABLE
                                </td>
                            </tr>
                            <tr>
                                <td style="width:10%">7.</td>
                                <td style="width:30%">Balance at the credit of the subscriber on the date of application </td>
                                <td style="width:60%"> 
                                    <input class="easyui-textbox" id="balanceCredit" type="number" name="balanceCredit" style="width:300px;height:25px" required  /> 
                                </td>
                            </tr>
                            <tr>
                                <td style="width:10%">i).</td>
                                <td style="width:30%">Closing balance as per statement for the Year <strong> ${LoanGPFForm.cyear} </strong> </td>
                            <td style="width:60%"> 
                                <input class="easyui-textbox" id="closingbalance" type="number" name="closingbalance" style="width:300px;height:25px"  required/> 
                            </td>
                        </tr>
                        <tr>
                            <td style="width:10%">ii).</td>
                            <td style="width:30%">Credit from <strong>4/${LoanGPFForm.previousYear}</strong> to <strong>${LoanGPFForm.cmonth}/${LoanGPFForm.cyear} </strong> </td>
                            <td style="width:60%"> 
                                <input class="easyui-textbox" id="creditAmount" type="number" name="creditAmount" style="width:300px;height:25px"  value="${LoanGPFForm.creditAmount}" /> 
                            </td>
                        </tr>
                        <tr>
                            <td style="width:10%">iii).</td>
                            <td style="width:30%">Refunds made to the fund after the closing balance </td>
                            <td style="width:60%"> 
                                <input class="easyui-textbox" id="refund" type="number" name="refund" style="width:300px;height:25px"  required/> 
                            </td>
                        </tr>

                        <tr>
                            <td style="width:10%">iv).</td>
                            <td style="width:30%">Withdrawal during the period from  <strong>4/${LoanGPFForm.previousYear}</strong> to <strong>${LoanGPFForm.cmonth}/${LoanGPFForm.cyear} </strong> </td>
                            <td style="width:60%"> 
                                <input class="easyui-textbox" id="withdrawalAmount" type="number" name="withdrawalAmount" style="width:300px;height:25px" required /> 
                            </td>
                        </tr>
                        <tr>
                            <td style="width:10%">v).</td>
                            <td style="width:30%"> Net Balance at credit on date of application. </td>
                            <td style="width:60%"> 
                                <input class="easyui-textbox" id="netbalance" type="number" name="netbalance" style="width:300px;height:25px"  required/> 
                            </td>
                        </tr>
                        <tr>
                            <td style="width:10%">8.</td>
                            <td style="width:30%">Amount of withdrawal required. </td>
                            <td style="width:60%"> 
                                <input class="easyui-textbox" id="withdrawalreq" type="number" name="withdrawalreq" style="width:300px;height:25px"  required/> 
                            </td>
                        </tr>
                        <tr>
                            <td style="width:10%">9(a).</td>
                            <td style="width:30%"> Purpose for which the withdrawal is required </td>
                            <td style="width:60%"> 
                                <input class="easyui-textbox" id="purpose" type="text" name="purpose" style="width:300px;height:25px"  required/> 
                            </td>
                        </tr>
                        <tr>
                            <td style="width:10%">9(b).</td>
                            <td style="width:30%"> Rule under which the request is covered </td>
                            <td style="width:60%"> 
                                <input class="easyui-textbox" id="requestcovered" type="text" name="requestcovered" style="width:300px;height:25px"  /> 
                            </td>
                        </tr>
                        <tr>
                            <td style="width:10%">10. </td>
                            <td style="width:30%">Weather any withdrawal was taken for the same purpose earlier if so, indicate the amount and the year </td>
                            <td style="width:60%"> 
                                <input class="easyui-textbox" id="withdrawaltaken" type="text" name="withdrawaltaken" style="width:300px;height:25px"  /> 
                            </td>
                        </tr>
                        <tr>
                            <td style="width:10%">11. </td>
                            <td style="width:30%">Attachment </td>
                            <td style="width:60%"> 
                                <input id="file_att" type="file" name="file_att" style="width:300px;height:25px"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width:10%">12. </td>
                            <td style="width:30%">Name of the account officer maintaining the provident Fund account </td>
                            <td style="width:60%"> 
                                ${LoanGPFForm.accountOfficer}
                            </td>
                        </tr>



                        <tr>
                            <td style="width:10%">13. </td>
                            <td style="width:30%">forward To</td>
                            <td>
                                <input id="forwardto" type="text" name="forwardto" style="width:300px;height:25px" readonly="true"   ></input>
                                <a href="javascript:void(0)" id="change" onclick="changepost()">
                                    <button type="button">Search</button>
                                </a>
                            </td>   
                        </tr>



                    </table>
                    <div style="text-align:center;padding:5px">
                        <input class="easyui-linkbutton" type="submit" name="Save" value="Submit" />
                        <input class="easyui-linkbutton" type="button" name="Back" value="Back" onclick="self.location = 'loanList.htm'"/>

                    </div>      
                </div>
            </div>
        </form>
    </body>
</html>