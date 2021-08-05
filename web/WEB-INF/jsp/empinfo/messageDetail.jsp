<%-- 
    Document   : messageDetail
    Created on : Dec 30, 2017, 12:13:38 PM
    Author     : manisha
--%>


<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
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
        <style type="text/css">
            .control-label {
                padding-top: 7px;
                margin-bottom: 0;
                text-align: left;
            }
            .row{
                margin-bottom: 5px;
            }
        </style>
    </head>
    <body>
        <div id="wrapper">
            <jsp:include page="../tab/hrmsadminmenu.jsp"/>        
            <div id="page-wrapper">
                <form:form action="saveEmployeeMessage.htm" commandName="employeeMessage" method="post" enctype="multipart/form-data">

                    <div class="form-group">
                        <label for="usr">Message</label>
                        <form:hidden path="empid"/>
                        <form:input path="message" class="form-control"/>
                    </div>
                    <div class="form-group">
                        <label for="usr">File</label>
                        <input type="file" name="uploadedFile"  class="form-control"/>
                    </div>
                    <div class="panel-footer">
                        <button type="submit" class="btn btn-default">Submit</button>
                    </div>
                </form:form>
            </div>
        </div>
    </body>
</html>
