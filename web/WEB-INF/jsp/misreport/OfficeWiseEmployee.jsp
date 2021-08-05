<%-- 
    Document   : OfficeWiseEmployee
    Created on : Feb 9, 2017, 4:42:58 PM
    Author     : Manas Jena
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Human Resources Management System, Government of Odisha</title>
        <link href="css/bootstrap.min.css" rel="stylesheet">
        <link href="font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">                
        <script src="js/jquery.min.js"></script>        
        <script src="js/bootstrap.min.js"></script>
    </head>
    <body>

        <div id="wrapper">
            <div class="row">
                <div class="col-lg-12">
                    <div class="panel panel-default">
                        <div class="panel-heading" align="center" style="background-color: #868686;color: #ffffff;font-size: xx-large;">EMPLOYEE LIST</div>
                        <div class="panel-body">
                            <div class="table-responsive">
                                <table class="table table-bordered table-hover table-striped">
                                    <thead>
                                        <tr style="background-color: #0071c5;color: #ffffff;">
                                            <th>Sl No</th>
                                            <th>EMPLOYEE<br/>GROUP<br/>CADRE<br/>GRADE </th>
                                            <th>GPF NO</th>
                                            <th>POST</th>
                                            <th>DOB<br/>DOS</th>
                                            <th>JOINING DATE<br/>HOME DIST</th>
                                            <th>BASIC<br/>GR.PAY</th>
                                            <th>AADHAR NO</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${employees}" var="employee" varStatus="count">
                                            <tr>
                                                <td>${count.index + 1}</td>
                                                <td>${employee.fname} ${employee.mname} ${employee.lname}<br/>${employee.cadreCode}<br/>${employee.cadreGrade}</td>
                                                <td>${employee.gpfno}</td>
                                                <td>${employee.post}</td>
                                                <td>${employee.dob}<br/>${employee.dor}</td>
                                                <td>${employee.joindategoo}<br/>${employee.permanentdist}</td>
                                                <td>${employee.basic}<br/>${employee.gp}</td>
                                                <td>${employee.aadhaarno}</td>
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
