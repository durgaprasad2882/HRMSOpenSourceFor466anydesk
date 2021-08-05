<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <form:form class="form-inline" action="aerstatuslist.htm" commandName="aerstatuslist"  method="GET">
             <div class="modal-header">
                    <h4 class="modal-title">Office List(AER Submitted)</h4>
                </div>
         <table id="example" class="table table-striped table-bordered" width="100%" cellspacing="0">
                <thead>
                    <tr>
                        <th>Sl No</th>
                        <th>Office Name </th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="aersubmittedofflist" items="${offList}" varStatus="theCount">
                        <tr>
                            <td>${theCount.index}</td>
                            <td>${aersubmittedofflist.offName}</td>
                       </tr>
                    </c:forEach>
                </table>
        </form:form>
    </body>
</html>
