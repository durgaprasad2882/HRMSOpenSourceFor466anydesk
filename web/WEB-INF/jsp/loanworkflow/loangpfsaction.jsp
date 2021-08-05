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
        <link  rel="stylesheet" type="text/css"  href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css"/>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
        <script type="text/javascript">
            $(document).ready(function () {
                $('#loandg').datagrid('load', {
                    empid: $('#empId').val()
                });
            });
            function type_designation(vals){
              //  alert(vals);
                $('#id_from_des').html(vals)
            }
            function submitSanction(){
                
                var letterno=$('#letter_no').val();
                var letter_date=$('#letter_date').val();
                 var letter_form_name=$('#letter_form_name').val();
                 var letter_form_designation=$('#letter_form_designation').val();
                   var letter_to_name=$('#letter_to_name').val();
                    var memo_no=$('#memo_no').val();
                     var memo_date=$('#memo_date').val();
                 if(letterno==""){
                     alert("Please enter letter no");
                     return false;
                 }
                 if(letter_date==""){
                     alert("Please enter letter date");
                     return false;
                 }
                 if(letter_form_name==""){
                     alert("Please enter form name");
                     return false;
                 }
                 if(letter_form_designation==""){
                     alert("Please enter letter designation");
                     return false;
                 }
                 if(letter_to_name==""){
                     alert("Please enter to name");
                     return false;
                 }
                 if(memo_no==""){
                     alert("Please enter memo no");
                     return false;
                 }
                 if(memo_date==""){
                     alert("Please enter memo date");
                     return false;
                 }
            }
            
            function downloadpdf(){
                var url = "DownloadPDF.htm?loanid=${LoanForm.loanId}";
                self.location = url;
            }
        </script>
    </head>
    <body style="margin-left:30px ">
        <p style="text-style:justify" align="center">GOVERNMENT OF ODISHA </p>
        <p style="text-style:justify" align="center">${LoanForm.offaddress} Department</div> </p>  
            
    <form class="form-horizontal"  action="saveLoansaction.htm" method="POST" commandName="Loan Saction"  enctype="multipart/form-data" onsubmit="return submitSanction()">
         <input type="hidden" name="loanId" id="loanid" value="${LoanForm.loanId}"/>
            <input type="hidden" name="taskid" id="taskid" value="${LoanForm.taskid}"/>
        <table style="width:100%">
             <tr>
                <td  valign="top">Letter No</td>
                <td style="width:30%">
                    <c:if test="${ not empty LoanForm.letterNo}">
                        ${LoanForm.letterNo}
                     </c:if> 
                     <c:if test="${ empty LoanForm.letterNo}">
                       <input name="letterNo" class="form-control" id="letter_no"/>
                     </c:if> 
                   
                </td>
                <td>&nbsp;</td>
                <td  valign="top">Date</td>
                <td style="width:30%">
                     <c:if test="${ not empty LoanForm.letterDate}">
                        ${LoanForm.letterDate}
                     </c:if> 
                     <c:if test="${ empty LoanForm.letterDate}">
                        <input name="letterDate" class="form-control" id="letter_date"/>
                     </c:if>
                  
                </td>
               
            </tr>
             <tr>
                <td>&nbsp;</td>
            </tr>
             <tr >
                <td  valign="top">Letter From Name</td>
                <td style="width:30%;"> 
                     <c:if test="${ not empty LoanForm.letterformName}">
                        ${LoanForm.letterformName}
                     </c:if> 
                     <c:if test="${ empty LoanForm.letterformName}">
                        <input name="letterformName" class="form-control" id="letter_form_name"/>
                     </c:if>
                   
                </td>
                <td>&nbsp;</td>
                <td  valign="top">Letter From Designation</td>
                <td style="width:30%">
                     <c:if test="${ not empty LoanForm.letterformdesignation}">
                        ${LoanForm.letterformdesignation}
                     </c:if> 
                     <c:if test="${ empty LoanForm.letterformdesignation}">
                        <input name="letterformdesignation" class="form-control" id="letter_form_designation"/>
                     </c:if>
                   
                </td>
               
            </tr>
             <tr>
                <td>&nbsp;</td>
            </tr>
             <tr >
                <td  valign="top">Letter To</td>
                <td style="width:30%;" colspan="3">
                     <c:if test="${ not empty LoanForm.letterto}">
                        ${LoanForm.letterto}
                     </c:if> 
                     <c:if test="${ empty LoanForm.letterto}">
                         <input name="letterto" class="form-control" id="letterto_name" value="The Account General (A & E), Odisha, Bhubaneswar."/>
                     </c:if>
                   
                </td>
                
               
               
            </tr>
        </table>  
          <div style="height:20px"></div>
        <p style="text-style:justify;padding-left:10px"><strong>Sub:</strong>Sanction of advance for purchase of <strong>${LoanForm.preAdvPur}</strong> in favour of  <strong>${LoanForm.name}</strong> of <strong>${LoanForm.offaddress}</strong> Department</p>
        <div style="height:20px"></div>
    </form>   
    </body>

</html>
