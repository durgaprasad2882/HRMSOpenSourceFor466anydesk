<%-- 
    Document   : discproced
    Created on : Nov 9, 2017, 11:52:47 AM
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
        <script>
            function openOfficeList() {
                $('#myModal').modal('show');
            }
        </script>
    </head>
    <body>
        <form:form commandName="pbean" method="post" action="rule15Controller.htm">
            <div class="container-fluid">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <div class="row">
                            <div class="col-sm-1 text-right"> <label class="control-label">Office:</label></div>
                            <div class="col-sm-6"><form:input path="offName"  class="form-control" id="office"/><form:hidden path="offCode"/>  </div>
                            <div class="col-sm-2"><button type="submit" class="btn btn-default">Get Employee</button></div>
                            <div class="col-sm-2" text-left><button type="submit" class="btn btn-default" onclick="openOfficeList()">Other Office</button></div>
                        </div>
                    </div>
                    <div class="panel-body table-responsive">
                        <table class="table table-bordered" >
                            <thead>
                                <tr>
                                    <th width="5%">Select</th>
                                    <th width="5%">Sl No</th>
                                    <th width="55%">Employee Name</th>
                                    <th width="45%">Designation</th>
                                </tr>
                            </thead>

                            <tbody>                                        
                                <c:forEach items="${empList}" var="employee" varStatus="cnt">
                                    <tr>
                                        <td><input type="checkbox" name="delinquent" value="${employee.empid}"></td>
                                        <td>${cnt.index+1}</td>
                                        <td>${employee.fullname}</td>
                                        <td>${employee.post}</td>

                                    </tr>
                                </c:forEach>
                            </tbody>

                        </table>
                    </div>
                    <div class="panel-footer">
                        <button type="submit" class="btn btn-default">Disciplinary Rule 15</button>
                        <button type="button" class="btn btn-default">Disciplinary Rule 16</button>
                        <button type="button" class="btn btn-default">Suspension</button>
                    </div>
                </div>
            </div>
        </form:form>
        <!--
        <div >
            <form>
                <input type="text"  value="Home" >
                <input type="text" value="Proceeding" ><br>
                Office:
                <input type="text" name="firstname" >
                <button type="button" class="btn btn-info btn-lg" data-toggle="modal" data-target="#myModal">search</button>
                <button type="button" class="btn btn-info btn-lg" onclick="openOfficeList()">search1</button>
                
        -->


        <div id="myModal" class="modal fade" role="dialog">
            <div class="modal-dialog">

                <!-- Modal content-->
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal">&times;</button>
                        <h4 class="modal-title">Modal Header</h4>
                    </div>
                    <div class="modal-body">
                        <p>Some text in the modal.</p>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                    </div>
                </div>

            </div>
        </div>



    </body>
</html>