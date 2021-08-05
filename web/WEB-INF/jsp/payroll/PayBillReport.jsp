<%-- 
    Document   : PayBillReport
    Created on : Aug 19, 2016, 1:37:11 PM
    Author     : Manas Jena
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>:: HRMS ::</title>
        <style type="text/css">
            .reportHeader{
                background-color:#B1C242;
                font-family:Arial,Helvetica;
            }
        </style>
    </head>
    <body>

        <table class="table table-hover">
            <thead>	
                <tr>
                    <th  width="5%">Sl No.</th>
                    <th width="60%">Report List</th>
                    <th width="10%">Action</th>
                    <th width="10%">PDF LINK</th>   
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${reportList}" var="report">
                    <tr>
                        <td>${report.slNo}</td>
                        <td>${report.reportName}</td>
                        <td><a target="_blank" href="${report.actionPath}"><img border="0" alt="HTML" src="http://apps.hrmsorissa.gov.in/ohs_images/images/IMAGES/html_icon.gif"></a></td> 
                        <c:if test="${not empty report.pdfLink}">
                            <td><a target="_blank" href="${report.pdfLink}"><img border="0" alt="PDF" src="images/pdf.png" height="20"></a></td>
                        </c:if>
                        <c:if test="${empty report.pdfLink}">
                            <td>&nbsp;</td>
                        </c:if>  
                    </tr>
                </c:forEach>
               
            </tbody>
        </table>
    </body>
</html>