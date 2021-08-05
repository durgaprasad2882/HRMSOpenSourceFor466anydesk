<%-- 
    Document   : OfficeList
    Created on : Nov 18, 2017, 1:02:16 PM
    Author     : manisha
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
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
    </head>
    <body>
        <div id="wrapper">
            <jsp:include page="../tab/hrmsadminmenu.jsp"/>        
            <div id="page-wrapper">
                <div class="container-fluid">
                    <!-- Page Heading -->
                    <div class="row">
                        <div class="col-lg-12">                            
                            <ol class="breadcrumb">
                                <li>
                                    <i class="fa fa-dashboard"></i>  <a href="index.html">Dashboard</a>
                                </li>
                                <li class="active">
                                    <i class="fa fa-file"></i> Office List 
                                </li>
                                <li class="active">
                                    <i class="fa fa-file"></i> <a href="getOfficeDetail.htm">New Office</a>
                                </li>
                            </ol>
                        </div>
                    </div>
                    <div class="row">
                        <form:form action="getDeptWiseOfficeList.htm" commandName="Office" method="post">
                        <div class="col-lg-2">Department Name</div>
                        <div class="col-lg-8">
                            <form:select path="deptCode" class="form-control">
                                <form:option value="">Select</form:option>
                                <form:options items="${departmentList}" itemValue="deptCode" itemLabel="deptName"/>                                
                            </form:select>                            
                        </div>
                        <div class="col-lg-2"><button type="submit" class="form-control">Search</button> </div>
                        </form:form>
                    </div>
                    <div class="row">
                        <div class="col-lg-12">
                            <h2>Office List</h2>
                            <div class="table-responsive">
                                <table class="table table-bordered table-hover table-striped">
                                    <thead>
                                        <tr>
                                            <th>Sl No</th>
                                            <th>Office Name</th>
                                            <th>DDO Code</th>
                                            <th>Action</th>                                            
                                        </tr>
                                    </thead>
                                    <tbody>                                        
                                        <c:forEach items="${officeList}" var="office" varStatus="count">
                                            <tr>
                                                <td>${count.index + 1}</td>
                                                <td>${office.offName}</td>
                                                <td>${office.ddoCode}</td>
                                                <td><a href="getOfficeDetail.htm?offCode=${office.offCode}">Edit</td>
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


