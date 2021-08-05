<%-- 
    Document   : SectionDefination
    Created on : Nov 21, 2016, 3:12:08 PM
    Author     : Manas Jena
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Human Resources Management System, Government of Odisha</title>      
        <link href="css/bootstrap.min.css" rel="stylesheet">
        <link href="font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
        <!-- LAYOUT v 1.3.0 -->
        <script type="text/javascript" src="js/jquery.min-1.9.1.js"></script>        
        <script type="text/javascript" src="js/bootstrap.min.js"></script>
        <script type='text/javascript'>
            function delete_group(ids) {
                var con = confirm("Do you want to delete this Group information");
                if (con) {
                    window.location = "deleteGroupData.htm?groupId=" + ids;

                }
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
                                <th  width="10%">SL NO</th>
                                <th  width="40%">DESCRIPTION</th>
                                <th  width="40%">CHART OF ACCOUNT</th>
                                <th  width="10%">&nbsp;</th>                          
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${groupList}" var="group" varStatus="cnt">
                                <tr>
                                    <td>${cnt.index+1}</td>
                                    <td>${group.billgroupdesc}</td>
                                    <td>${group.chartofaccount}</td>

                                    <td><a href="editGroupList.htm?groupId=${group.billgroupid}">Edit</a></td>

                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
                <div class="panel-footer">                    
                    <button type="submit" class="btn btn-default" onclick="javascript: self.location = 'addGroupList.htm'">New Group</button>                    
                </div>
            </div>
        </div>
    </body>
</html>
