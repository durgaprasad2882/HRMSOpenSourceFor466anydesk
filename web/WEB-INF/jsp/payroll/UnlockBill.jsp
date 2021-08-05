<%-- 
    Document   : UnlockBill
    Created on : Mar 26, 2018, 2:37:15 PM
    Author     : Madhusmita
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>:: HRMS, Government of Odisha ::</title>
        <link href="css/bootstrap.min.css" rel="stylesheet">
        <link href="font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">        
        <link href="css/sb-admin.css" rel="stylesheet">
        <script src="js/jquery.min.js"></script>        
        <script src="js/bootstrap.min.js"></script>
        <title>Unlock Bill</title>        
        <script type="text/javascript">
            function addOfficeData(obj)
            {
                $('#offname').empty();
                var dist_code = obj.value;
                //alert(dist_code);
                if (dist_code != '') {
                    var url = 'getoffice.htm?dist_code=' + dist_code;
                    $('#offname').append('<option value="">--Select Office--</option>');
                    $.getJSON(url, function(data) {
                        $.each(data, function(i, obj) {
                            $('#offname').append('<option value="' + obj.value + '">' + obj.label + '</option>');
                        });
                    });
                } else {
                    $('#offname').children().remove().end().append('<option selected value="">--Select Office--<\/option>');
                }
            }
            function unlockalert(billid)
            {
                if (confirm("Are You sure to unlock ?"))
                {
                    self.location = "unlockbilltoResubmit.htm?billid=" + billid;

                } else {
                    return false;
                }

            }
            function objectBillAlert()
            {
                var checkBoxlength = $("input[name=objectbill]:checked").length;
                if (checkBoxlength == 0) {
                    alert("Please select bill.");
                    return false;
                }
                else
                {
                    alert("Are you Sure to Object Bill ?")
                    return true;
                }
            }

        </script>

    </head>
    <body>
        <div id="wrapper">
            <jsp:include page="../tab/hrmsadminmenu.jsp"/>        
            <div id="page-wrapper">     

                <div class="container-fluid">

       
                    <div class="row">
                        <div class="col-lg-12">                            
                            <ol class="breadcrumb">
                                <li>
                                    <i class="fa fa-dashboard"></i>  <a href="index.html">Dashboard</a>
                                </li>
                                <li class="active">
                                    <i class="fa fa-file"></i> Unlock BIll 
                                </li>                                
                            </ol>
                        </div>
                    </div>              
                    <div style="text-align:center;">
                        <form:form action="unlockbill.htm" commandName="billDetail" method="POST">
                            <table border="0" width="60%"  cellspacing="0" style="font-size:12px; font-family:verdana;">
                                <tr style="height: 30px">
                                    <td style="text-align:center;">
                                        <form:label path="offcode">OFFICE CODE</form:label>
                                        <form:input path="offcode" class="form-control"/>
                                    </td> 
                                    <td style="text-align:center;">
                                        <form:label path="billnumber">BILL ID</form:label>
                                        <form:input path="billnumber" class="form-control"/>
                                    </td>
                                    <td>
                                        <input type="submit" class="form-control" value="Ok"/>                                        
                                    </td>
                                </tr>                
                            </table>
                       
                    </div>
                    <div class="row">
                        <div class="col-lg-12">
                            <h3>Bill List</h3>
                            <div class="table-responsive">
                                <table class="table table-bordered table-hover table-striped">
                                    <thead>
                                        <tr>
                                            <th width="5%">Select Bill</th>
                                            <th width="10%">Bill Id</th>
                                            <th width="10%">Bill Name</th>
                                            <th width="10%">Bill Date</th>
                                            <th width="15%">Bill Status</th>
                                            <th width="15%">Bill Group Name</th>
                                            <th width="10%">Month/Year</th>
                                            <th width="20%">Token No/Date</th>
                                            <th width="30%">Previous Token No/Date</th>
                                            <th width="20%">Office</th>
                                            <th width="10%">Unlock Bill</th>
                                            <th width="10%">Unlock To Resubmit</th>
                                        </tr>
                                    </thead>
                                    <c:if test="${not empty data}">
                                        <c:forEach var="list" items="${data}">
                                            <tbody>
                                            <tr>
                                                <td class="form-group"> 
                                                    <c:if test="${list.billStatusId ==5}">
                                                        <div class="form-group">
                                                            <input type="checkbox" name="objectbill" value="${list.billnumber}"/>
                                                        </div>
                                                    </c:if>                                       
                                                </td>
                                                <td class="form-group">
                                                    <div class="form-group">
                                                        <c:out value="${list.billnumber}"/>
                                                    </div>
                                                </td>
                                                <td class="form-group">
                                                    <c:out value="${list.billdesc}"/>
                                                </td>
                                                <td class="form-group">
                                                    <c:out value="${list.billDate}"/>
                                                </td>
                                                <td class="form-group">
                                                    <c:out value="${list.billStatus}"/>
                                                </td>
                                                <td class="form-group">
                                                    <c:out value="${list.billgrpname}"/>
                                                </td>
                                                <td class="form-group">                                         
                                                    <c:out value="${list.aq_month}"/> /
                                                    <c:out value="${list.aq_year}"/>
                                                </td>
                                                <td class="form-group">
                                                    <c:out value="${list.tokenNumber}"/> /
                                                    <c:out value="${list.tokendate}"/>
                                                </td>
                                                <td class="form-group">
                                                    <c:out value="${list.prevTokenNumber}"/> /
                                                    <c:out value="${list.prevTokendate}"/>
                                                </td>
                                                <td class="form-group">
                                                    <c:out value="${list.offcode}"/>
                                                </td>
                                                <td class="form-group"> 
                                                    <c:if test="${list.billStatusId ==3 || list.billStatusId ==4 || list.billStatusId ==8 }">
                                                        <a href="unlockbilldata.htm?billnumber=<c:out value="${list.billnumber}"/>" onclick="return unlockalert();">Unlock</a> 
                                                    </c:if>
                                                </td>                                            
                                                <td class="form-group"> 
                                                    <c:if test="${list.billStatusId ==2}">
                                                        <%--<a href=" javascript:unlockalert('<c:out value="${list.billnumber}"/>');">Unlock</a> --%>
                                                        <a href="unlockbilltoResubmit.htm?billnumber=<c:out value="${list.billnumber} "/>" onclick="return unlockalert();">Unlock</a> 
                                                    </c:if>                                    
                                                </td>
                                            </tr>
                                            </tbody>
                                        </c:forEach>                                                                             
                                    </c:if>
                                </table>
                            </div>
                            <filedset>
                                <div align="left">                        
                                    <input type="submit" value="ObjectBill" name="action" style="width:100px;" onclick="return objectBillAlert()" class="btn btn-default"/>                      
                                </div>
                            </filedset>
                             </form:form>
                        </div>
                    </div>                  
                </div>
            </div>
        </div>
    </body>
</html>
