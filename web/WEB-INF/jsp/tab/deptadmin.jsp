<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%
    String contextPath = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + contextPath + "/";

%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>:: HRMS, Government of Odisha ::</title>
        <link href="css/bootstrap.min.css" rel="stylesheet">
        <link href="font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
        <!-- Custom CSS -->
        <link href="css/sb-admin.css" rel="stylesheet">
        <script src="js/jquery.min.js"></script>

        <!-- Bootstrap Core JavaScript -->
        <script src="js/bootstrap.min.js"></script>
        <style>
            .placeholder img{
                display: inline-block;
                border-radius: 50%;                
            }
        </style>    

    </head>
    <body>
        <jsp:include page="deptadminmenu.jsp"/>        
        <div id="page-wrapper">

            <div class="container-fluid" style='min-height: 400px'>
         <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
              <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
    <jsp:useBean id="date" class="java.util.Date" />
    <fmt:formatDate value="${date}" pattern="yyyy" var="currentYear" />
    <fmt:formatDate value="${date}" pattern="MMMM" var="currentMonth" />
          <h1 class="page-header">Department Dashboard Panel</h1>
          <ul style='list-style-type: none'>
              <li> <h4><a href='https://drive.google.com/file/d/1SS1tAGC0okMUsCapcBMQmFK0PCSc3O4H/view' target="_blank" style='color:red'>Download OPSC Advance Calendar</a></h4></li>
               <li> <h4><a href='https://drive.google.com/file/d/1KRUeYP3_ZKzZlkZr9QI8hr8PU1sMyyeM/view' target="_blank" style='color:red'>Download OSSSC Advance Calendar</a></h4></li>
                <li> <h4><a href='https://drive.google.com/file/d/11wpCmDuLvBbbS42md5YXBpyZW-XEbeVt/view' target="_blank" style='color:red'>Download OSSC Advance Calendar</a></h4></li>
              
          </ul>
         
          <div class="row placeholders">
            <div class="col-xs-6 col-sm-3 placeholder">
              <img src="data:image/gif;base64,R0lGODlhAQABAIAAAHd3dwAAACH5BAAAAAAALAAAAAABAAEAAAICRAEAOw==" width="200" height="200" class="img-responsive" alt="Generic placeholder thumbnail">
              <h4> <a href="getDeptvacantpostList.htm?year=${currentYear}&month=${currentMonth}">Base Level Vacant Post</a></h4>
              
            </div>
            <div class="col-xs-6 col-sm-3 placeholder">
              <img src="data:image/gif;base64,R0lGODlhAQABAIAAAHd3dwAAACH5BAAAAAAALAAAAAABAAEAAAICRAEAOw==" width="200" height="200" class="img-responsive" alt="Generic placeholder thumbnail">
               <h4> <a href="getRaschemeList.htm">Rehabilitation Scheme</a></h4>
             
            </div>
            <div class="col-xs-6 col-sm-3 placeholder">
              <img src="data:image/gif;base64,R0lGODlhAQABAIAAAHd3dwAAACH5BAAAAAAALAAAAAABAAEAAAICRAEAOw==" width="200" height="200" class="img-responsive" alt="Generic placeholder thumbnail">
               <h4> <a href="getScstRecruitmentList.htm">Special Recruitment</a></h4>
             
            </div>
            <div class="col-xs-6 col-sm-3 placeholder">
              <img src="data:image/gif;base64,R0lGODlhAQABAIAAAHd3dwAAACH5BAAAAAAALAAAAAABAAEAAAICRAEAOw==" width="200" height="200" class="img-responsive" alt="Generic placeholder thumbnail">
               <h4> <a href="getRecruitmentDriveList.htm">Recruitment Drive</a></h4>             
            </div>
              
               <div class="col-xs-6 col-sm-3 placeholder">
              <img src="data:image/gif;base64,R0lGODlhAQABAIAAAHd3dwAAACH5BAAAAAAALAAAAAABAAEAAAICRAEAOw==" width="200" height="200" class="img-responsive" alt="Generic placeholder thumbnail">
               <h4> <a href="getTrainingList.htm">Training</a></h4>
             
            </div>
            <div class="col-xs-6 col-sm-3 placeholder">
              <img src="data:image/gif;base64,R0lGODlhAQABAIAAAHd3dwAAACH5BAAAAAAALAAAAAABAAEAAAICRAEAOw==" width="200" height="200" class="img-responsive" alt="Generic placeholder thumbnail">
               <h4> <a href="getBleoList.htm">BLEO Recruitment</a></h4>             
            </div>
             <div class="col-xs-6 col-sm-3 placeholder">
              <img src="data:image/gif;base64,R0lGODlhAQABAIAAAHd3dwAAACH5BAAAAAAALAAAAAABAAEAAAICRAEAOw==" width="200" height="200" class="img-responsive" alt="Generic placeholder thumbnail">
               <h4> <a href="changePasswordmis.htm">Change Password</a></h4>             
            </div>
              
              
          </div>

            </div>
            
        </div>
    </body>
</html>