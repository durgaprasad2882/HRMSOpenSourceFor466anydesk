<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%
    String contextPath = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + contextPath + "/";

%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>:: HRMS, Government of Odisha ::</title>
        <link href="css/bootstrap.min.css" rel="stylesheet" type="text/css">
        <link href="font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
        <link href="css/sb-admin.css" rel="stylesheet" type="text/css">
        <script src="js/jquery.min.js" type="text/javascript"></script>
        <script src="js/bootstrap.min.js" type="text/javascript"></script>

    </head>
    <body>
        <div id="wrapper">
            <jsp:include page="../tab/deptmismenu.jsp"/>                
            <div id="page-wrapper">

                <div class="container-fluid">
                    <!-- Page Heading -->
                    
                    <div class="row">
                        <div class="col-lg-12">
                            <h2>Monthly Activity Court Case Report</h2>
                            <div class="table-responsive">
                                <table id="account" class="table table-bordered table-striped" >
                                    <thead>
                                        <tr style='background-color:black;color:white;font-size:14px'>
                                           
                                            <th>Department</th>
                                            <th>Post</th>
                                            <th>Court Case <br/>No<br/>And Date</th>
                                            <th>Date Of <br/>Submission of <br/>PWC</th>
                                            <th>Date of <br/>Filling Counter <br/>Affedevit</th>
                                            <th>Date of <br/>Interim<br/> Order</th>
                                            <th>Date of <br/>Final <br/>Judgement</th>     
                                            <th>Stay Order<br/> Passed<br/> By order</th>
                                            <th>Steps Taken <br/>For Vacation<br/> Of Stay</th>
                                            <th>Date Of <br/>Vacation of <br/>Stay</th>
                                             <th>Date Of <br/>Publication Of<br/> Final Result</th>
                                            <th>Date Of <br/>Sponsoring of <br/>Candidates</th>
                                            
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${ccourtcaseList}" var="ccourtcaseList">
                                            <tr scope="row">

                                                                                  
                                                <td>${ccourtcaseList.departmentId}</td>                                                
                                                <td>${ccourtcaseList.postId}</td>
                                                <td>${ccourtcaseList.courtCaseNo}<br/>${ccourtcaseList.courtCaseDate}</td>
                                                <td>${ccourtcaseList.pwcDate}</td>
                                                <td>${ccourtcaseList.affedevitdate}</td>  
                                                 <td>${ccourtcaseList.interimorder}</td>  
                                                  <td>${ccourtcaseList.judgementDate}</td>  
                                                  <td>${ccourtcaseList.orderPassed}</td>  
                                                  <td>${ccourtcaseList.stepsStay}</td> 
                                                   <td>${ccourtcaseList.stayDate}</td>  
                                                  <td>${ccourtcaseList.finalResultDate}</td>  
                                                    <td>${ccourtcaseList.sponsoringDate}</td>  
                                                
                                            </tr>
                                        </c:forEach>
                                    </tbody>	
                                </table>

                            </div>
                        </div>
                    </div>

                </div>
            </div>
        </div>
    </body>
</html>