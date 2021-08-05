<%-- 
    Document   : ModuleList
    Created on : Nov 21, 2016, 6:08:30 PM
    Author     : Manas Jena
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

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
        <script type="text/javascript">
            function getOfficeList() {
                var url = 'getOfficeListJSON.htm?deptcode=' + $("#deptCode").val();
                var valText = $("#deptCode option:selected").html();
                $("#hiddenDeptName").val(valText);

                $.getJSON(url, function (data) {
                    $('#offCode').empty();
                    $('#postcode').empty();
                    $('#offCode').append($('<option>').text('Select Post').attr('value', ''));
                    $.each(data, function (i, obj) {
                        $('#offCode').append($('<option>').text(obj.offName).attr('value', obj.offCode));
                    });
                });
            }
            function getPostList() {
                var valText = $("#offCode option:selected").html();
                $("#hiddenOfficeName").val(valText);
            }
        </script>
    </head>
    <body>
        <div id="wrapper">
            <jsp:include page="../tab/hrmsadminmenu.jsp"/>        
            <div id="page-wrapper">
                <div class="container-fluid">
                    <!-- Page Heading -->
                    <div class="row">
                        <div class="col-lg-12">                            
                            <ol class="breadcrumb">
                                <li>
                                    <i class="fa fa-dashboard"></i>  <a href="index.html">Dashboard</a>
                                </li>
                                <li class="active">
                                    <i class="fa fa-file"></i> Privilege List 
                                </li>                                
                            </ol>
                        </div>
                    </div>
                    <form role="form" action="substantivePostDetails.htm" commandName="substantivePostDetails"  method="post">
                        <input type="hidden" name='hiddenDeptName' value='' id='hiddenDeptName'/>
                        <input type="hidden" name='hiddenOfficeName' value='' id='hiddenOfficeName'/>
                        <div class="row">
                            <div class="col-lg-12">
                                <h2>Substantive Post</h2>

                                <div class="form-group">
                                    <label>Department</label>                                    
                                    <select class="form-control"  name="deptCode" id="deptCode" onchange="getOfficeList()" required="1">
                                        <option value=''>Select Department</option>
                                        <c:forEach items="${deptList}" var="dept" >
                                            <option value="${dept.deptCode}">${dept.deptName}</option>
                                        </c:forEach>                                        
                                    </select>                                   
                                </div>

                                <div class="form-group">
                                    <label>Office</label>
                                    <select class="form-control"  name="offCode" id="offCode"   onchange="getPostList()" required="1">                             
                                        <option value=''>Select Post</option>
                                    </select>
                                </div>
                                <input type='submit' name='Submit' value='Submit' class="btn btn-primary" />


                            </div>
                        </div>
                    </form>

                </div>
            </div>
        </div>



        <!-- Modal -->
        <div id="myModal" class="modal fade" role="dialog">
            <div class="modal-dialog">

                <!-- Modal content-->
                <!-- <div class="modal-content">
                     <div class="modal-header">
                         <button type="button" class="close" data-dismiss="modal">&times;</button>
                         <h4 class="modal-title">New Privilege</h4>
                     </div>
                     <div class="modal-body">
                         <div class="form-group">
                             <label>Role</label>
                             <select class="form-control"  name="role" id="role" onchange="showModuleGroup()">                             
                             </select>
                         </div>
                         <div class="form-group">
                             <label>Module Group</label>
                             <select class="form-control"  name="modulegroup" id="modulegroup" onchange="showModule()" >                             
                             </select>
                         </div>
                         <div class="form-group">
                             <label>Module</label>
                             <select class="form-control"  name="module" id="module" >                             
                             </select>
                         </div>
                     </div>
                     <div class="modal-footer">
                         <span id="msg"></span>
                         <button type="button" class="btn btn-default" onclick="assignPrivilege()">Save</button>
                         <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                     </div>
                 </div>-->

            </div>
        </div>
    </body>
</html>
