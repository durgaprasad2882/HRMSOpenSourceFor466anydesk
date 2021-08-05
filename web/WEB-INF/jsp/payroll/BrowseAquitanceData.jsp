<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<!DOCTYPE html>
<% int j = 0;%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Human Resources Management System, Government of Odisha</title>
        <link href="css/bootstrap.min.css" rel="stylesheet">
        <link href="font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">        
        <link href="css/bootstrap-datetimepicker.css" rel="stylesheet" type="text/css"/>
        <script src="js/moment.js" type="text/javascript"></script>
        <script src="js/jquery.min.js" type="text/javascript"></script>        
        <script src="js/bootstrap.min.js" type="text/javascript"></script>
        <script type="text/javascript">
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
        <form:form class="form-inline" action="browserAqData.htm" method="POST" commandName="command">
            <div class="container-fluid">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <div class="row">
                            <div class="col-lg-2"><b>HRMS ID:</b></div>
                            <div class="col-lg-4">${aqReportBean.empcode}</div>
                            <div class="col-lg-2"><b>SCALE:</b></div>
                            <div class="col-lg-4">${aqReportBean.payscale}</div>
                        </div>
                        <div class="row">
                            <div class="col-lg-2"><b>EMPLOYEE NAME:</b></div>
                            <div class="col-lg-4">${aqReportBean.empname}</div>
                            <div class="col-lg-2"><b>DDO:</b></div>
                            <div class="col-lg-4">&nbsp;</div>
                        </div>
                        <div class="row">
                            <div class="col-lg-2"><b>OFFICE NAME:</b></div>
                            <div class="col-lg-4">&nbsp;</div>
                            <div class="col-lg-2"><b>PLAN SECTOR:</b></div>
                            <div class="col-lg-4">&nbsp;</div>
                        </div>
                        <div class="row">
                            <div class="col-lg-2"><b>DESIGNATION:</b></div>
                            <div class="col-lg-4">${aqReportBean.curdesg}</div>
                            <div class="col-lg-2"><b>GPF No:</b></div>
                            <div class="col-lg-4">${aqReportBean.gpfaccno}</div>
                        </div>
                        <div class="row">
                            <div class="col-lg-2"><b>BASIC:</b></div>
                            <div class="col-lg-4">${aqReportBean.curbasic}</div>
                            <div class="col-lg-2">&nbsp;</div>
                            <div class="col-lg-4">&nbsp;</div>
                        </div>
                    </div>
                    <div class="panel-body">
                        <div class="row">
                            <div class="col-lg-6">
                                <table class="table table-bordered">
                                    <thead>                   	
                                        <tr height="30px">
                                            <th width="5%" align="center" >SL.</th>
                                            <th width="65%" align="center">ALLOWANCES</th>
                                            <th width="25%" align="center">AMOUNT</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${allowanceObjList}" var="allowanceObj" varStatus="cnt">
                                            <tr>
                                                <td>${cnt.index+1}</td>
                                                <td>${allowanceObj.allowance}</td>
                                                <td>${allowanceObj.amount}</td>
                                                <td>
                                                    <a href="getAqDtlsAllAction.htm?aqslNo=${aqSlNo}&adCode=${allowanceObj.allowance}&billNo=${billNo}&adType=A" data-remote="false" data-toggle="modal" data-target="#myModal" class="btn btn-default">Edit</a>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                        <tr>
                                            <td  align="right" style="font-size:12px;font-family:verdana;border-top:1px solid black;border-bottom:1px solid black;">&nbsp;</td>
                                            <td  align="right" style="font-size:12px;font-family:verdana;border-top:1px solid black;border-bottom:1px solid black;">Total Allowances:</td>
                                            <td  align="right" style="font-size:12px;font-family:verdana;border-top:1px solid black;border-bottom:1px solid black;"><span id="totalAllDisplay">${totAll}</span></td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                            <div class="col-lg-6">
                                <table class="table table-bordered">
                                    <thead>
                                        <tr>
                                            <th width="5%" align="center" >SL.</th>
                                            <th width="15%" align="center" >DEDUCTIONS</th>
                                            <th width="25%" align="center">ACCOUNT NO.</th>
                                            <th width="20%" align="center" >DEDUCTION FOR</th>
                                            <th width="20%" align="center" >INSTALMENT No.</th>
                                            <th width="10%" align="center">AMOUNT</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${deductionobjList}" var="deductionobj" varStatus="cnt">
                                            <tr>
                                                <td>${cnt.index+1}</td>
                                                <td>${deductionobj.deduction} <c:if test="${not empty deductionobj.nowdedn}">(${deductionobj.nowdedn})</c:if></td>
                                                <td>${deductionobj.accNo}</td>
                                                <td>${deductionobj.deductionFor}</td>
                                                <td>${deductionobj.noofInstal}</td>
                                                <td>${deductionobj.amount}</td>
                                                <td>
                                                    <!--<a href="getAqDtlsDedAction.htm?aqslNo=${aqSlNo}&dednType=${deductionobj.deduction}&amount=${deductionobj.amount}&instlNo=${deductionobj.noofInstal}&billNo=${billNo}" data-remote="false" data-toggle="modal" data-target="#myModal" class="btn btn-default">Edit</a>-->
                                                    <a href="getAqDtlsDedAction.htm?aqslNo=${aqSlNo}&adCode=${deductionobj.deduction}&billNo=${billNo}&nowDedn=${deductionobj.nowdedn}" data-remote="false" data-toggle="modal" data-target="#myModal" class="btn btn-default">Edit</a>
                                                </td>
                                            </tr>

                                        </c:forEach>
                                        <tr>
                                            <td  align="right" style="font-size:12px;font-family:verdana;border-top:1px solid black;border-bottom:1px solid black;">&nbsp;</td>
                                            <td  colspan="4" align="right" style="font-size:12px;font-family:verdana;border-top:1px solid black;border-bottom:1px solid black;">Total Deduction:</td>
                                            <td  align="right" style="font-size:12px;font-family:verdana;border-top:1px solid black;border-bottom:1px solid black;"><span id="totalAllDisplay">${totDed}</span></td>
                                        </tr>
                                    </tbody>
                                </table>


                            </div>
                        </div>
                    </div>
                    <table class="table table-bordered">     
                        <tr>
                            <td align="center">Gross Rs.${gross}</td>
                            <td  align="center">Net Rs.${net}</td>
                           
                        </tr>
                    </table>
                </div>

            </div>


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


        </div>
    </form:form>
</body>
</html>
