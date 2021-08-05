<%-- 
    Document   : OnlineTicketingData
    Created on : 12 Oct, 2017, 11:34:26 AM
    Author     : lenovo pc
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        <link rel="stylesheet" type="text/css" href="css/bootstrap.min.css">

        <style type="text/css">

        </style>
        
    </head>
    <body>

    <form:form action="onlineticket.htm" commandName="onlineticketing" enctype="multipart/form-data">
        <div class="container">
           
                <div class="row">
                    <div class="col-md-4 col-md-push-4">
                         <div class = "page-header">
                <h1>
                    <small>Online Ticket</small>
                </h1>
            </div>
                        <div class="form-group">
                            <label for="form_name">Ticket By :</label><br>
                            <c:out value="${onlineticketing.username}" />
                            <html:hidden path="userId"/>
                           <!-- <form:input path="userId" class="form-control" id="userId" placeholder="Enter Ticket No"/>-->
                        </div>
                        <div class="form-group">
                            <label for="form_name">Topic</label>
                            <form:select path="topicId" class="form-control">
                                <form:option value="" label="Select"/>
                                <c:forEach items="${topiclist}" var="topic">
                                    <form:option value="${topic.topicId}" label="${topic.topic}"/>
                                </c:forEach>                                 
                            </form:select> 
                        </div>
                        <div class="form-group">
                            <label for="form_name">Message</label>
                            <form:textarea path="message" class="form-control" id="message" rows="3"/>
                            </div>
                        <div class="form-group">
                            <table>
                                <tr>
                                    <td><label for="exampleInputFile">File Upload</label></td>
                            
                                    <td>
                                        <input type="file" name="file" />
                                       <!-- <input type="file" class="form-control-file" name="file" id="file" aria-describedby="fileHelp">-->
                            <small id="fileHelp" class="form-text text-muted"></small></td>
                                    <td></td>
                            <tr>
                            </table>
                        </div>
                        <div class="col-md-8 col-md-push-4">
                        <input type="submit" value="Save" name="save" class="btn btn-primary"/>
                        <input type="submit" value="Cancel" name="cancel" class="btn btn-primary"/>
                        </div>
                    </div>
                </div>
            </div>
    </form:form>

</body>
</html>
