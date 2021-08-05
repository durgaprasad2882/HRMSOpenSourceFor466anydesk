<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
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
               
                var radios = document.getElementsByName("loanapplyfor");
                var formValid = false;

                var i = 0;
                while (!formValid && i < radios.length) {
                    if (radios[i].checked)
                        formValid = true;
                    i++;
                }
                if (!formValid) {
                    alert("Please choose Loan Apply For");
                    return false;
                }

                var antprice = $("#txtantprice").val();
                if (isNaN(antprice) || antprice == "") {
                    alert("Invalid Anticipated Price");
                    // $("#txtantprice").textbox('focus');
                    return false;
                }
                var purtype = $('#purtype').val();
                if ($("#purtype:checked").length == 0) {
                    alert("Please select Purchase Type");
                    return false;
                }
                var amountadv = $("#amountadv").val();
                if (isNaN(amountadv) || amountadv == "") {
                    alert("Invalid Amount of advance");
                    document.getElementById("amountadv").focus();
                    return false;
                }
                if (parseInt(antprice) < parseInt(amountadv)) {
                    alert("Amount of advance should not  be more than Anticipated Price");
                    return false;

                }
                var instalments = $("#instalments").val();
                if (isNaN(instalments) || instalments == "") {
                    alert("Invalid No of instalments");
                    document.getElementById("instalments").focus();
                    return false;
                }

                if (instalments > 50 || instalments < 1) {
                    alert("No of instalments should not be more than 50 ");
                    document.getElementById("instalments").focus();
                    return false;
                }
                var forwardto = $("#forwardto").val();
                if (forwardto == "") {
                    alert("Please select your loan authority ");
                    document.getElementById("forwardto").focus();
                    return false;
                }
                var fup = document.getElementById('file_att');
                var fileName = fup.value;
                var ext = fileName.substring(fileName.lastIndexOf('.') + 1);
                var ext=ext.toLowerCase();
                if(ext && ext != "pdf"){
                    alert("Upload pdf files only");
                    fup.focus();
                    return false;
                }
                var confirmloan = confirm("Do you want to apply Loan Again ");
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
            function purpose_advance(vals) {
                if (vals == "No") {
                    $('#PreAdvPur').textbox('clear');
                    $('#amounpretadv').textbox('clear');
                    $('#dateofdrawal').textbox('clear');
                    $('#13a').hide();
                    $('#13b').hide();
                    $('#13c').hide();
                } else {
                    $('#13a').show();
                    $('#13b').show();
                    $('#13c').show();


                }
            }
            function interest_paid(vals) {
                if (vals == "No") {
                    $('#13e').show();

                } else {
                    $('#13e').hide();
                    $('#amountstanding').textbox('clear');
                }
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
        <form action="saveLoan.htm" method="POST" commandName="LoanForm" onsubmit="return applyloan()" enctype="multipart/form-data">
            <input type="hidden" name="empName" id="empName" value="${LoanForm.empName}"/>
            <input type="hidden" name="designation" id="designation" value="${LoanForm.designation}"/>
            <input type="hidden" name="basicsalary" id="basicsalary" value="${LoanForm.basicsalary}"/>
            <input type="hidden" name="netsalary" id="netsalary" value="${LoanForm.netsalary}"/>
            <input type="hidden" name="empSPC" id="empSPC" value="${LoanForm.empSPC}"/>


            <input type="hidden" name="hidOffCode" id="hidOffCode"/>
            <input type="hidden" name="hidOffName" id="hidOffName"/>
            <input type="hidden" name="hidSPC" id="hidSPC"/>
            <input type="hidden" name="forwardtoHrmsid" id="forwardtoHrmsid"/>

            <div id="tbl-container" class="easyui-panel" title="Apply Loan"  style="width:100%;overflow: auto;">
                <div align="left" style="padding-left:10px">
                    <h2 style="text-transform: uppercase">Application form for Advance for the purpose of Motor car/motor cycle/Moped/personal computer</h2>
                    <table style="width:100%">
                        <tr>
                            <td style="width:30%">1. Name</td>
                            <td style="width:70%"> 
                                ${LoanForm.empName}
                            </td>
                        </tr>
                        <tr>
                            <td>2. Designation</td>
                            <td> 
                                ${LoanForm.designation}
                            </td>
                        </tr>
                        <tr>
                            <td>3. Official Address</td>
                            <td> 
                                ${LoanForm.offaddress}
                            </td>
                        </tr>
                        <tr>
                            <td>4. Job Type</td>
                            <td> 
                                ${LoanForm.jobType}
                            </td>
                        </tr>
                        <tr>
                            <td>5. Basic  salary</td>
                            <td> 
                                ${LoanForm.basicsalary}
                            </td>
                        </tr>
                        <tr>
                            <td>6. Net  salary</td>
                            <td> 
                                ${LoanForm.netsalary}
                            </td>
                        </tr>
                        <tr>
                            <td>7.DOB</td>
                            <td> 
                                ${LoanForm.empdob}
                            </td>
                        </tr>
                        <tr>
                            <td>8.Date of Superannuation</td>
                            <td> 
                                ${LoanForm.superannuation}
                            </td>
                        </tr>
                        <tr>
                            <td valign='top'>9.Loan Apply For</td>
                            <td> 
                                <input type="radio" class="easyui-radio" name="loanapplyfor"  value="MOTOR CAR"  />MOTOR CAR<br/>
                                <input type="radio" class="easyui-radio" name="loanapplyfor"  value="MOTOR CYCLE"  />MOTOR CYCLE<br/>
                                <input type="radio" class="easyui-radio" name="loanapplyfor"  value="MOPED"  />MOPED<br/>
                                <input type="radio" class="easyui-radio" name="loanapplyfor"  value="PERSONAL COMPUTER"  />PERSONAL COMPUTER<br/>

                            </td>
                        </tr>

                        <tr>
                            <td>10.Anticipated Price</td>
                            <td> 
                                <input class="easyui-textbox" id="txtantprice" type="text" name="antprice" style="width:300px;height:25px"   ></input>
                            </td>
                        </tr>
                        <tr>
                            <td>11.Purchase Type</td>
                            <td> 
                                <input type="radio" class="easyui-radio" name="purtype" id='purtype' value="New"  />New &nbsp;<input type="radio" class="easyui-radio" name="purtype" value="Second Hand" id='purtype' />Second Hand
                            </td>
                        </tr>
                        <tr>
                            <td>12.Amount of advance required</td>
                            <td> 
                                <input class="easyui-textbox" id="amountadv" type="text" name="amountadv" style="width:300px;height:25px"   ></input>
                            </td>
                        </tr>
                        <tr>
                            <td>13.No of instalments</td>
                            <td> 
                                <input class="easyui-textbox" id="instalments" type="text" name="instalments" style="width:300px;height:25px"   ></input>
                            </td>
                        </tr>
                        <tr>
                            <td>14.Weather advance for similar purpose was availed previously?</td>
                            <td>
                                <input type="radio" class="easyui-radio" name="previousAvail"  value="Yes"  onclick="purpose_advance(this.value)" />Yes &nbsp;<input type="radio" class="easyui-radio" name="previousAvail" value="No"  onclick="purpose_advance(this.value)" />No
                            </td>   
                        </tr>
                        <tr id="13a" >
                            <td>15.Whether for Motor Car/Cycle/Moped/Personal Computer</td>
                            <td>
                                <input class="easyui-textbox" id="PreAdvPur" type="text" name="PreAdvPur" style="width:300px;height:25px"   ></input>
                            </td>   
                        </tr>
                        <tr  id="13b">
                            <td>16.Amount of  advance</td>
                            <td>
                                <input class="easyui-textbox" id="amounpretadv" type="text" name="amounpretadv" style="width:300px;height:25px"   ></input>
                            </td>   
                        </tr>
                        <tr id="13c">
                            <td>17.Date of drawal advance</td>
                            <td>
                                <input class="txtDate" id="dateofdrawal" type="text" name="dateofdrawal" style="width:300px;height:25px;"   ></input>
                            </td>   
                        </tr>
                        <tr>
                            <td>18.Principal along with Interest paid in Full? </td>
                            <td>
                                <input type="radio" class="easyui-radio" name="intpaidfull" value="Yes"  onclick="interest_paid(this.value)" />Yes &nbsp;<input type="radio" class="easyui-radio" name="intpaidfull" value="No"  onclick="interest_paid(this.value)"/>No
                            </td>   
                        </tr>
                        <tr id="13e" >
                            <td>19.Amount of principal/interest standing</td>
                            <td>
                                <input class="easyui-textbox" id="amountstanding" type="text" name="amountstanding" style="width:300px;height:25px"   ></input>
                            </td>   
                        </tr>
                        <tr>
                            <td>20.Weather the officer is on leave or is about to proceed?</td>
                            <td>
                                <input type="radio" class="easyui-radio" name="officerleave" value="Yes"  />Yes &nbsp;<input type="radio" class="easyui-radio" name="officerleave" value="No"  />No
                            </td>   
                        </tr>
                        <tr>
                            <td>21.Date of commencement leave</td>
                            <td>
                                <input class="txtDate" id="datecommleave" type="text" name="datecommleave" style="width:300px;height:25px"   ></input>
                            </td>   
                        </tr>
                        <tr>
                            <td>22.Date of expire leave</td>
                            <td>
                                <input class="txtDate" id="dateexpireleave" type="text" name="dateexpireleave" style="width:300px;height:25px"   ></input>
                            </td>   
                        </tr>
                        <tr>
                            <td>23.Attachment</td>
                            <td>
                                <input id="file_att" type="file" name="file_att" style="width:300px;height:25px"></input>
                            </td>   
                        </tr>
                        <tr>
                            <td>24.Forward to</td>
                            <td>
                                <input id="forwardto" type="text" name="forwardto" style="width:300px;height:25px" readonly="true"   ></input>
                                <a href="javascript:void(0)" id="change" onclick="changepost()">
                                    <button type="button">Search</button>
                                </a>
                            </td>   
                        </tr>



                    </table>
                    <div style="text-align:center;padding:5px">
                        <input class="easyui-linkbutton" type="submit" name="Save" value="Apply" />
                        <input class="easyui-linkbutton" type="button" name="Back" value="Back" onclick="self.location = 'loanList.htm'"/>

                    </div>      
                </div>
            </div>
        </form>
    </body>
</html>