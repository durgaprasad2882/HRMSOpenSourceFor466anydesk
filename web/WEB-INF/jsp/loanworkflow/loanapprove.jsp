<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
          function closeIframeWindow(){
             // alert('hi');
              window.parent.closeIframe();
            //  alert('rashmi');
          }
          $(document).ready(function () {
                $('#loan_status').combobox({
                    onSelect: function(record) {
                       //alert(record.value);
                       if(record.value==29 || record.value==27){
                           $("#id_forward_div").show();
                       } else {
                          $("#id_forward_div").hide();  
                       }
                    }
                });
            });
         

            function SelectSpn(offCode, spc, offName, authName,spc_hrmsid)
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
            function laonsaction(){
                 var loan_status = $("#loan_status").val();
                 if(loan_status==""){
                      alert("Please select Loan status");
                       document.getElementById("loan_status").focus();
                       return false;
                 }
                 if(loan_status==29 || loan_status==27){
                    var forwardto=$("#forwardto").val();
                    if ( forwardto == "") {
                       alert("Please select next loan authority details ");
                       document.getElementById("forwardto").focus();
                       return false;
                   }
                 }
                 
                 if(loan_status!=26){
                    var loancomments=$("#loancomments").val();
                    if(loancomments=="" || loancomments.length<5){
                        
                        alert("Please write your comments");
                        return false;
                    }
                 }
                
                
               
            }
             function changepost() {
                var url = 'ChangePostLoanController.htm';
                $.colorbox({href: url, iframe: true, open: true, width: "80%", height: "50%"});
            }
             function openWindow(linkurl, modname) {
               // $("#winfram").attr("src", linkurl);
              //  $("#win").window("open");
              //  $("#win").window("setTitle", modname);
              alert(linkurl);

            }
        </script>
    </head>

    <body>       
        <form action="saveApproveLoan.htm" method="POST" commandName="LoanForm" onsubmit="return laonsaction()">
             <input type="hidden" name="loanId" id="loanid" value="${LoanForm.loanId}"/>
            <input type="hidden" name="taskid" id="taskid" value="${LoanForm.taskid}"/>
            <input type="hidden" name="empID" id="empID" value="${LoanForm.empID}"/>
             <input type="hidden" name="statusID" id="statusID" value="${LoanForm.statusId}"/>
             <input type="hidden" name="approvedBy" id="approvedBy" value="${LoanForm.approvedBy}"/>
              <input type="hidden" name="approvedSpc" id="approvedSpc" value="${LoanForm.approvedSpc}"/>
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
                            <td>9.Loan Apply For</td>
                            <td> 
                                 ${LoanForm.loanapplyfor}
                            </td>
                        </tr>
                        <tr>
                            <td>10.Anticipated Price</td>
                            <td> 
                                 ${LoanForm.antprice}
                            </td>
                        </tr>
                        <tr>
                            <td>11.Purchase Type</td>
                            <td> 
                                ${LoanForm.purtype}
                            </td>
                        </tr>
                        <tr>
                            <td>12.Amount of advance required</td>
                            <td> 
                               ${LoanForm.amountadv}
                            </td>
                        </tr>
                        <tr>
                            <td>13.No of instalments</td>
                            <td> 
                                  ${LoanForm.instalments}
                            </td>
                        </tr>
                        <tr>
                            <td>14.Weather advance for similar purpose was availed previously?</td>
                            <td>
                                ${LoanForm.previousAvail}
                            </td>   
                        </tr>
                        <tr id="13a" >
                            <td>15.Whether for Motor Car/Cycle/Moped</td>
                            <td>
                             ${LoanForm.preAdvPur}
                            </td>   
                        </tr>
                        <tr  id="13b">
                            <td>16.Amount of  advance</td>
                            <td>
                               ${LoanForm.amounpretadv}
                            </td>   
                        </tr>
                        <tr id="13c">
                            <td>17.Date of drawal advance</td>
                            <td>
                               ${LoanForm.dateofdrawal}
                            </td>   
                        </tr>
                        <tr>
                            <td>18.Principal along with Interest paid in Full? </td>
                            <td>
                                ${LoanForm.intpaidfull}
                            </td>   
                        </tr>
                        <tr id="13e" >
                            <td>19.Amount of principal/interest standing</td>
                            <td>
                                ${LoanForm.amountstanding}
                            </td>   
                        </tr>
                        <tr>
                            <td>20.Weather the officer is on leave or is about to proceed?</td>
                            <td>
                                ${LoanForm.officerleave}
                            </td>   
                        </tr>
                        <tr>
                            <td>21.Date of commencement leave</td>
                            <td>
                                ${LoanForm.datecommleave}
                            </td>   
                        </tr>
                        <tr>
                            <td>22.Date of expire leave</td>
                            <td>
                                ${LoanForm.dateexpireleave}
                            </td>   
                        </tr>
                         <tr>
                            <td>23.Attachment</td>
                            <td>
                                <!--<input class="easyui-textbox" id="file_att" type="file" name="file" style="width:300px;height:25px"   ></input>-->
                                <c:if test="${ not empty LoanForm.diskFileName}">
                                    <c:set var="lid" value="${LoanForm.loanId}"/>
                                    <a href="DownloadLoanAttch.htm?lid=${LoanForm.loanId}" target="_blank" >Download</a>
                                </c:if>
                               <c:if test="${empty LoanForm.diskFileName}">
                                    <strong> No File Attached</strong>
                                  </c:if>   
                         
                            </td>   
                        </tr>
                        <tr>
                            <td>24.Forwarded to</td>
                            <td>
                                ${LoanForm.forwardtoHrmsid}
                            </td>   
                        </tr>
                         <tr>
                            <td>25.Notes</td>
                            <td>
                                ${LoanForm.notes}
                            </td>   
                        </tr>
                        <tr>
                               
                                <td >26.Action Taken:</td>
                                <td colspan="4"><input class="easyui-combobox"  id="loan_status" name="loan_status" data-options="valueField:'value',textField:'label',url:'getprocessList.htm?processid=6'" style="width:300px;height:auto"  onchange="display_forward(this.value)"></td>
                         </tr>
                           <tr style="display:none" id="id_forward_div">
                            <td>27.Forward to</td>
                            <td>
                                <input id="forwardto" type="text" name="forwardto" style="width:300px;height:25px" readonly="true"   />
                                <a href="javascript:void(0)" id="change" onclick="changepost()">
                                    <button type="button">Search</button>
                                </a>
                            </td>   
                        </tr>
                         <tr>
                               
                                <td >28.Comments:</td>
                                <td colspan="4">
                                    <!--<input class="easyui-textarea" type="textarea" name="loancomments"  styleId="txtcomments" style="width:40%;height:60px;;" styleClass="textareacolor" required/> -->
                                    <textarea rows="4" cols="40" class="easyui-textarea" name="loancomments"  id="loancomments" styleId="txtcomments" style="width:40%;height:60px;;" styleClass="textareacolor" ></textarea>
                                </td>
                         </tr>
                            

                    </table>
                    <div style="text-align:center;padding:5px">
                        <input class="easyui-linkbutton" type="submit" name="Save" value="Submit" />
                        <c:if test="${ LoanForm.statusId  == 27}">
                            <a href="javascript:void(0)" onclick="window.open('SactionOrder.htm?taskid=${LoanForm.taskid}&loanid=${LoanForm.loanId}', 'Saction Order','width=600,height=600')" title="Saction Order "> <input class="easyui-linkbutton" type="button" name="gorder" value="Generate Order"/></a>
                       </c:if>

                    </div>      
                </div>
            </div>
        </form>
    </body>
</html>