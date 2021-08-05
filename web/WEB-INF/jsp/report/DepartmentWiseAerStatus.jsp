<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        <link href="css/bootstrap.min.css" rel="stylesheet">
        <link href="font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">        
        <link href="css/bootstrap-datetimepicker.css" rel="stylesheet" type="text/css"/>
        <script src="js/moment.js" type="text/javascript"></script>
        <script src="js/jquery.min.js" type="text/javascript"></script>        
        <script src="js/bootstrap.min.js" type="text/javascript"></script>
        <script language="JavaScript" type="text/javascript">
            $(window).load(function () {
                // Fill modal with content from link href
                $("#myModal").on("show.bs.modal", function (e) {
                    var link = $(e.relatedTarget);
                    $(this).find(".modal-body").load(link.attr("href"));
                });
            });
        </script>
    </head>
    <body>
        <form:form class="form-inline" action="aerstatuslist.htm" commandName="aerstatuslist"  method="GET">
            <div align="center">
                <table  id="example"  width="100%" cellspacing="0">

                    <tr>
                        <td width="40%" align="right">
                            Select Financial Year
                        </td>
                        <td width="20%">
                            <form:select id="finYear" path="fy" class="form-control">
                                <form:options items="${financialYearList}"/>
                            </form:select>
                        </td>
                        <td align="left">
                            <input type="submit" value="Ok" name="ok" class="btn btn-primary"/>
                        </td>
                    </tr>
                </table>
            </div>
            <table id="example" class="table table-striped table-bordered" width="100%" cellspacing="0">
                <thead>
                    <tr>
                        <th>Sl No</th>
                        <th>Department Name</th>
                        <th> No Of DDOs</th>
                        <th> No Of Offices <br>AER Submitted</th>
                        <th>No Of Offices<br>AER Approved</th>
                    </tr>
                </thead>

                <tbody>
                    <c:forEach var="aerstatuslist" items="${aerStatusList}" varStatus="theCount">
                        <tr>
                            <td>${theCount.index}</td>
                            <td><c:out value="${aerstatuslist.deptName}"/></td>
                            <td><c:out value="${aerstatuslist.noOfDDO}"/></td>
                            <td>
                                <a href="aerreportsubmittedofflist.htm?deptCode=${aerstatuslist.deptCode}" data-remote="false" data-toggle="modal" data-target="#myModal"><c:out value="${aerstatuslist.noAerSubmitted}"/></a>
                            </td>

                            <td><c:out value="${aerstatuslist.noOfAerAproved}"/></td>
                        </tr>
                    </c:forEach>                    
            </table>
            <div id="myModal" class="modal fade" role="dialog">
                <div class="modal-dialog" style="width:1000px;">
                    <!-- Modal content-->
                    <div class="modal-content">
                        <div class="modal-body">

                        </div>
                        <div class="modal-footer">
                            <span id="msg"></span>                        

                            <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                        </div>
                    </div>

                </div>
            </div>

        </form:form>
    </body>
</html>
