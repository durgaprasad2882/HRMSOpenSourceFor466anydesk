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
                $(".txtNum").keypress(function(e) {
                    //if the letter is not digit then display error and don't type anything
                    if (e.which != 8 && e.which != 0 && (e.which < 48 || e.which > 57)) {
                        //display error message
                        $("#errmsg").html("Digits Only").show().fadeOut("slow");
                        return false;
                    }
                });
            });
        </script>
        <style type="text/css">
            body{
                font-family: Verdana;
                font-size:16px;
            }
        </style>
    </head>
    <body>
        <form:form action="newTransfer.htm" method="post" commandName="transferForm">
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
                                    <th width="20%">Date of Entry</th>
                                    <th width="30%">Notification Order No</th>
                                    <th width="25%">Notification Order Date</th>
                                    <th width="20%">Notification Type</th>
                                    <th colspan="2" width="15%" align="center">Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${transferlist}" var="tlist">
                                    <tr>
                                        <td>${tlist.doe}</td>
                                        <td>${tlist.ordno}</td>
                                        <td>${tlist.ordt}</td>
                                        <td>${tlist.notType}</td>
                                        <td><a href="editTransfer.htm?transferId=${tlist.transferId}&notId=${tlist.hnotid}">Edit</a></td>
                                        <c:if test="${not empty tlist.hrlvid}">
                                            <td>Relieved</td>
                                        </c:if>
                                        <c:if test="${empty tlist.hrlvid}">
                                            <td><a href="entryRelieve.htm?notId=${tlist.hnotid}&rlvId=${tlist.hrlvid}">Relieve</a></td>
                                        </c:if>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                    <div class="panel-footer">
                        <form:hidden class="form-control" path="empid" id="empid"/>
                        <button type="submit" class="btn btn-default">New Transfer</button>  
                    </div>
                </div>
            </div>
        </form:form>
    </body>
</html>
