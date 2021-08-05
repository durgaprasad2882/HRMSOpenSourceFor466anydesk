<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Human Resources Management System, Government of Odisha</title>

        <link href="css/bootstrap.min.css" rel="stylesheet">
        <link href="font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">

        <script type="text/javascript" src="js/jquery.min-1.9.1.js"></script>        
        <script type="text/javascript" src="js/bootstrap.min.js"></script>
        <script type="text/javascript">
            $(document).ready(function() {
                
            });
            function getMonth(monthStr) {
                var d = Date.parse(monthStr + "1, 2012");
                if (!isNaN(d)) {
                    return new Date(d).getMonth();
                }
                return -1;
            }
        </script>
    </head>
    <body>

        <div class="container-fluid">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <div class="row">
                        <div class="col-lg-12">

                        </div>
                    </div>
                </div>
                <div class="panel-body">
                    <table class="table table-bordered">
                        <thead>
                            <tr>
                                <th width="15%">Notification Type</th>
                                <th width="10%">Date of Entry</th>
                                <th width="10%">Notification Order No</th>
                                <th width="10%">Notification Order Date</th>
                                <th width="7%">Relieve Order No</th>
                                <th width="10%">Relieve Order Date</th>
                                <th width="10%">Relieved on<br />Date</th>
                                <th width="10%">Relieved on Time</th>
                                <th width="10%">Due Date of<br />Joining</th>
                                <th width="10%">Due Time of<br />Joining</th>
                                <th width="20%">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${relievelist}" var="rlist">
                                <tr>
                                    <td>${rlist.notType}</td>
                                    <td>${rlist.notdoe}</td>
                                    <td>${rlist.notordno}</td>
                                    <td>${rlist.notorddt}</td>
                                    <td>${rlist.rlvordno}</td>
                                    <td>${rlist.rlvorddt}</td>
                                    <td>${rlist.rlvondt}</td>
                                    <c:if test="${not empty rlist.rlvontime && rlist.rlvontime == 'FN'}">
                                        <td>FORE NOON</td>
                                    </c:if>
                                    <c:if test="${not empty rlist.rlvontime && rlist.rlvontime == 'AN'}">
                                        <td>AFTER NOON</td>
                                    </c:if>
                                    <c:if test="${empty rlist.rlvontime}">
                                        <td>&nbsp;</td>
                                    </c:if>
                                    <td>${rlist.rlvduedt}</td>
                                    <c:if test="${not empty rlist.rlvduetime && rlist.rlvduetime == 'FN'}">
                                        <td>FORE NOON</td>
                                    </c:if>
                                    <c:if test="${not empty rlist.rlvduetime && rlist.rlvduetime == 'AN'}">
                                        <td>AFTER NOON</td>
                                    </c:if>
                                    <c:if test="${empty rlist.rlvduetime}">
                                        <td>&nbsp;</td>
                                    </c:if>
                                    <td><a href="entryRelieve.htm?notId=${rlist.notid}&rlvId=${rlist.rlvid}">Relieve from Post</a></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
                <div class="panel-footer">

                </div>
            </div>
        </div>
    </body>
</html>
