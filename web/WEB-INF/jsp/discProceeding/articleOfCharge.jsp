<%-- 
    Document   : articleOfCharge
    Created on : Feb 5, 2018, 4:10:28 PM
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
        <div id="page-wrapper">
            <form:form action="saveDisccharge.htm" commandName="chargebean" method="post" enctype="multipart/formdata">
                <div class="container-fluid">

                    <div class="panel panel-default">


                        <div class="panel-body">                       
                            <div class="form-group">
                                <label class="control-label " >Articles of Charge</label>
                                <div class="col-sm-4"> 
                                    <form:hidden path="daId"/>
                                    <form:hidden path="dacid"/>
                                    <form:input class="form-control" path="charge"/>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="control-label " >Statement Of Imputation</label>
                                <div class="col-sm-4">  
                                    <form:input class="form-control" path="chargeDetails"/>
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="usr">Document</label>
                                <div class="col-sm-4"> 
                                    <input type="file" name="uploadedFile"  class="form-control"/>
                                </div>
                            </div>

                        </div>
                        <div class="panel-footer">
                            <input type="submit" name="action" value="Save" class="btn btn-default"/>
                            <c:if test="${chargebean.dacid > 0}">
                                <input type="submit" name="action" value="Delete" class="btn btn-default"/>
                            </c:if>
                            <input type="submit" name="action" value="Cancel" class="btn btn-default"/>
                        </div>

                    </div>
                </div>
            </form:form>
        </div>
    </body>
</html>