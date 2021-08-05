<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        <link rel="stylesheet" type="text/css" href="css/bootstrap.css"/>
        <link rel="stylesheet" type="text/css" href="css/dataTables.bootstrap4.min.css"/>
        <script type="text/javascript"  src="js/jquery-1.12.4.js"></script>
        <script type="text/javascript"  src="js/jquery.dataTables.min.js"></script>
        <script type="text/javascript"  src="js/dataTables.bootstrap4.min.js"></script>
        <script language="javascript" type="text/javascript" >
            $(document).ready(function () {
                $('#example').DataTable();
            });
        </script>
    </head>
    <body>
        <form:form action="onlineticketlist.htm" commandName="onlineticketing"  method="GET">
            <div class="main-content">
                <div >
                    <div class="float-left">
                        <input type="submit" value="New Ticket" name="newticket" class="btn btn-primary"/>
                         
                    </div>
                </div>
            </div>
            <table id="example" class="table table-striped table-bordered" width="100%" cellspacing="0">
                <thead>
                    <tr>
                        <th>Sl No</th>
                        <th>Category</th>
                        <th>Message</th>
                        <th>Office Name</th>
                        <th>Status</th>
                        <th>Action</th>
                    </tr>
                </thead>

                <tbody>
                    <c:forEach var="ticketlist" items="${onlineticketlist}" varStatus="theCount">
                        <tr>
                            <td>${theCount.index}</td>
                            <td><c:out value="${ticketlist.topicName}"/></td>
                            <td><c:out value="${ticketlist.message}"/></td>
                            <td><c:out value="${ticketlist.offname}"/></td>
                            <td><c:out value="${ticketlist.status}"/></td>
                            <td>&nbsp;</td>
                        </tr>
                    </c:forEach>                    
            </table>

        </form:form>
    </body>
</html>
