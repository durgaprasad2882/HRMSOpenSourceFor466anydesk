<%-- 
    Document   : articleOfChargeList
    Created on : Feb 5, 2018, 4:10:42 PM
    Author     : manisha
--%>

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<%@ page contentType="text/html;charset=UTF-8"%>

<%
    String contextPath = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + contextPath + "/";
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Human Resources Management System, Government of Odisha</title>
        <link href="css/bootstrap.min.css" rel="stylesheet">
        <script src="js/jquery.min.js" type="text/javascript"></script>        
        <script src="js/bootstrap.min.js" type="text/javascript"></script>
        <style>
            table, th, td {
                border: 1px solid black;
                border-collapse: collapse;
            }
            th, td {
                padding: 5px;
                text-align: left;    
            }
            .table-responsive {
                max-height:450px;
                font-size: 10px;
            }
            .table-bordered{
                font-size: 12px;
            }
        </style>
    </head>
    <body>
        <div class="table-responsive">
            <form:form action="addNewDisccharge.htm" method="post" commandName="chargebean">
                <table class="table table-bordered table-hover table-striped">
                    <thead>
                        <tr>
                            <th>Article of Charge</th>
                            <th>Charge Details</th>
                            <th>Action</th>

                        </tr>
                    </thead>
                    <tbody>                                        
                        <c:forEach items="${articleOfChargeList}" var="articleOfCharge">
                            <tr>
                                <td>${articleOfCharge.charge}</td>
                                <td>${articleOfCharge.chargeDetails}</td>
                                <td>
                                    <a href="editNewDisccharge.htm?dacid=${articleOfCharge.dacid}&daId=${chargebean.daId}" class="btn btn-default" >Edit </a>
                                    <a  href="employeeWitnessList.htm?action=witnessList&dacid=${articleOfCharge.dacid}&daId=${chargebean.daId}" class="btn btn-default" >Witness</a>
                                </td>                            
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                <div class="panel-footer">
                    <form:hidden path="daId"/>
                    <input type="submit" name="action" value="Add New" class="btn btn-default"/>
                    <input type="submit" name="action" value="Back" class="btn btn-default"/>                

                </div>
            </form:form>
        </div>
    </body>
</html>

