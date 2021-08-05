<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
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
        <script src="js/bootstrap-datetimepicker.js" type="text/javascript"></script>
        <script type="text/javascript">
            $(window).load(function () {
                // Fill modal with content from link href
                $("#myModal").on("show.bs.modal", function (e) {
                    var link = $(e.relatedTarget);
                    
                    $(this).find(".modal-body").load(link.attr("href"));

                });
                
                $("#submitToIFMS").on("show.bs.modal", function (e) {
                    var link = $(e.relatedTarget);
                    $(this).find(".modal-body").load(link.attr("href"));
                });

                $('#submitToIFMS').on('hidden.bs.modal', function () {
                    location.reload();// submitToIFMS
                });

                $("#billStatusModal").on("show.bs.modal", function (e) {
                    var link = $(e.relatedTarget);
                    $(this).find(".modal-body").load(link.attr("href"));
                });

                $("#reprocessModal").on("show.bs.modal", function (e) {
                    var link = $(e.relatedTarget);
                    $(this).find(".modal-content").load(link.attr("href"));
                    $('#processDate').datetimepicker({
                        format: 'D-MMM-YYYY'
                    });
                });
            })



            function showMonth(me) {
                $.ajax({
                    type: "POST",
                    url: "getBillMonthYearWise.htm?sltYear=" + $(me).val(),
                    success: function (data) {
                        $('#sltMonth').empty();
                        if (data == '') {
                            $('#sltMonth').append($('<option>', {
                                value: '',
                                text: 'Select One'
                            }));
                        }

                        $.each(data, function (i, obj)
                        {
                            $('#sltMonth').append($('<option>', {
                                value: obj.value,
                                text: obj.label
                            }));

                        });
                    }
                });
            }
            function validate() {
                var today = new Date();
                var processDt = document.getElementById('txtprocessdt');
                if (processDt.value == '') {
                    alert('Please enter Process Date.');
                    processDt.focus();
                    return false;
                } else {
                    var prdt = new Date(processDt.value);
                    if (prdt > today) {
                        alert("Process Date should not be greater than Today's Date");
                        processDt.focus();
                        processDt.select();
                        return false;
                    }
                }
            }
        </script>
        <style>
            @media (min-width: 800px) {
                .modal-dialog {
                    width: 600;
                    margin: 30px auto;
                }
                .modal-content {
                    -webkit-box-shadow: 0 5px 15px rgba(0, 0, 0, .5);
                    box-shadow: 0 5px 15px rgba(0, 0, 0, .5);
                }
                .modal-sm {
                    width: 300px;
                }
            }
        </style>
    </head>
    <body>
        <div class="container-fluid">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <form:form class="form-inline" action="getPayBillList.htm" method="POST" commandName="command">
                        <div class="row">
                            <div class="col-lg-12">

                                <div class="form-group">
                                    <label for="billType">Bill Type:</label>
                                    <form:select path="txtbilltype" id="txtbilltype" class="form-control">
                                        <form:option value="PAY">Pay Bill</form:option>
                                        <form:option value="ARREAR">Arrear Bill</form:option>
                                    </form:select>                
                                </div>
                                <div class="form-group">
                                    <label for="sltYear">Year:</label>
                                    <form:select path="sltYear" id="sltYear" class="form-control" onchange="showMonth(this)">
                                        <option value="">Select</option>
                                        <form:options items="${billYears}" itemValue="value" itemLabel="label"/>                                        
                                    </form:select>
                                </div>
                                <div class="form-group">
                                    <label for="sltMonth">Month:</label>
                                    <form:select path="sltMonth" id="sltMonth" class="form-control" style="width:60%;">
                                        <option value="">Select</option>
                                        <form:options items="${billMonths}" itemValue="value" itemLabel="label"/>                                        
                                    </form:select>
                                </div>
                                <button type="submit" class="btn btn-default">Submit</button>

                            </div>
                        </div>
                        <c:if test="${command.txtbilltype == 'ARREAR'}">
                            <div class="row">
                                <div class="col-lg-2">


                                    <div class="form-group">
                                        <label for="sltYear">From Year:</label>
                                        <form:select path="sltFromYear" class="form-control">
                                            <form:option value="2016"> 2016 </form:option>
                                            <form:option value="2017"> 2017 </form:option>
                                        </form:select>
                                    </div>
                                </div>
                                <div class="col-lg-2">
                                    <div class="form-group">
                                        <label for="sltMonth">From Month:</label>

                                        <form:select path="sltFromMonth" class="form-control">
                                            <form:option value="1">JANUARY</form:option>
                                            <form:option value="2">FEBRUARY</form:option>
                                            <form:option value="3">MARCH</form:option>
                                            <form:option value="4">APRIL</form:option>
                                            <form:option value="5">MAY</form:option>
                                            <form:option value="6">JUNE</form:option>
                                            <form:option value="7">JULY</form:option>
                                            <form:option value="8">AUGUST</form:option>
                                            <form:option value="9">SEPTEMBER</form:option>
                                            <form:option value="10">OCTOBER</form:option>
                                            <form:option value="11">NOVEMBER</form:option>
                                            <form:option value="12">DECEMBER</form:option>
                                        </form:select>
                                    </div>



                                </div>
                            </div>
                            <div class="clearfix"> </div>                     
                            <div class="clearfix"> </div>                
                            <div class="row">
                                <div class="col-lg-2">


                                    <div class="form-group">
                                        <label for="sltYear">To Year:</label>
                                        <form:select path="sltToYear" id="sltToYear" class="form-control">
                                            <form:option value="2016"> 2016 </form:option>
                                            <form:option value="2017"> 2017 </form:option>
                                        </form:select>
                                    </div>
                                </div>
                                <div class="col-lg-2">
                                    <div class="form-group">
                                        <label for="sltMonth">To Month:</label>

                                        <form:select path="sltToMonth" id="sltToMonth" class="form-control">
                                            <form:option value="1">JANUARY</form:option>
                                            <form:option value="2">FEBRUARY</form:option>
                                            <form:option value="3">MARCH</form:option>
                                            <form:option value="4">APRIL</form:option>
                                            <form:option value="5">MAY</form:option>
                                            <form:option value="6">JUNE</form:option>
                                            <form:option value="7">JULY</form:option>
                                            <form:option value="8">AUGUST</form:option>
                                            <form:option value="9">SEPTEMBER</form:option>
                                            <form:option value="10">OCTOBER</form:option>
                                            <form:option value="11">NOVEMBER</form:option>
                                            <form:option value="12">DECEMBER</form:option>
                                        </form:select>
                                    </div>




                                </div>
                            </div>
                        </c:if>
                    </form:form> 
                    <div class="panel-body">
                        <table class="table table-bordered">
                            <thead>
                                <tr>
                                    <th width="10%">Bill No</th>
                                    <th width="20%">Bill Description</th>
                                    <th width="15%">Bill Type</th>
                                    <th width="15%">Acquaintance Roll</th>
                                    <th width="5%">Print Bill</th>
                                    <th width="5%">Edit</th>
                                    <th width="10%">Submit To i-OTMS</th>
                                    <th width="15%">Status</th>
                                    <th width="10%">Lock Bill</th>

                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${billList}" var="bill">
                                    <tr>
                                        <td>${bill.billdesc}</td>
                                        <td>${bill.billGroupDesc}</td>
                                        <td>${bill.billtype}</td>
                                        <td>
                                            <c:if test="${bill.isbillPrepared == 'Y'}">
                                                <c:if test="${command.txtbilltype == 'PAY'}">
                                                    <a href="browseAquitance.htm?billNo=${bill.billno}">Browse Aquitance</a>
                                                </c:if>
                                                <c:if test="${command.txtbilltype == 'ARREAR'}">
                                                    <a href="browseAquitanceArr.htm?billNo=${bill.billno}">Browse Arrear</a>
                                                </c:if>

                                            </c:if>
                                        </td>
                                        <td>
                                            <c:if test="${bill.isbillPrepared == 'Y'}">
                                                <a href="payBillReportAction.htm?billNo=${bill.billno}&txtbilltype=${command.txtbilltype}" data-remote="false" data-toggle="modal" data-target="#myModal" class="btn btn-default">Print</a>
                                            </c:if>
                                        </td>
                                        <td>
                                            <c:if test="${bill.isbillPrepared == 'Y'}">
                                                <c:if test="${command.txtbilltype == 'PAY'}">
                                                    <a href="editBill.htm?billNo=${bill.billno}">Edit</a>
                                                </c:if>
                                                <c:if test="${command.txtbilltype == 'ARREAR'}">
                                                    <a href="editBillArrear.htm?billNo=${bill.billno}">Edit Arrear Bill</a>
                                                </c:if>
                                            </c:if>



                                        </td>
                                        <td>
                                            <c:if test="${bill.onlinebillapproved == 'Y'}">
                                                <c:if test="${not empty bill.billno}">
                                                    <c:if test="${bill.showLink == 'Y'}">

                                                        <a href="submitToIFMS.htm?billNo=${bill.billno}" data-remote="false" data-toggle="modal" title="View Status" data-target="#submitToIFMS" class="btn btn-default">Submit to IFMS</a>
                                                    </c:if>
                                                    <c:if test="${bill.showLink == 'N'}">
                                                        <c:if test="${bill.lockBill == '5'}">
                                                            Token Generated
                                                        </c:if>
                                                        <c:if test="${bill.lockBill == '7'}">
                                                            Vouchered
                                                        </c:if>
                                                        <c:if test="${bill.lockBill == '3'}">
                                                            SUBMITTED
                                                        </c:if>
                                                    </c:if>
                                                </c:if>
                                            </c:if>
                                        </td>
                                        <td>
                                            <c:if test="${bill.isbillPrepared == 'Y'}">
                                                <c:if test="${bill.lockBill == '4'}">
                                                    <a href="showUploadBillStatus.htm?billNo=${bill.billno}" data-remote="false" data-toggle="modal" title="View Status" data-target="#billStatusModal" class="btn btn-default">Error</a>

                                                </c:if>
                                                <c:if test="${bill.lockBill == '8'}">
                                                    <a href="showUploadBillStatus.htm?billNo=${bill.billno}" data-remote="false" data-toggle="modal" title="View Status" data-target="#billStatusModal" class="btn btn-default">Objected</a>
                                                </c:if>
                                                <c:if test="${bill.lockBill != '4' and bill.lockBill != '8'}">
                                                    <a href="showUploadBillStatus.htm?billNo=${bill.billno}" data-remote="false" data-toggle="modal" title="View Status" data-target="#billStatusModal" class="btn btn-default">Status</a>
                                                </c:if>
                                            </c:if>
                                            <c:if test="${empty bill.billno}">
                                                <c:if test="${command.txtbilltype == 'PAY'}">
                                                    <a href="processIndividualBill.htm?billNo=${bill.billno}&billgroupId=${bill.billgroupId}&sltMonth=${command.sltMonth}&sltYear=${command.sltYear}&txtbilltype=PAY" data-remote="false" data-toggle="modal" data-target="#reprocessModal" class="btn btn-default">Process Bill</a>
                                                </c:if>
                                                <c:if test="${command.txtbilltype == 'ARREAR'}">
                                                    <a href="processArrearIndividualBill.htm?billNo=${bill.billno}&billgroupId=${bill.billgroupId}&sltMonth=${command.sltMonth}&sltYear=${command.sltYear}&txtbilltype=ARREAR" data-remote="false" data-toggle="modal" data-target="#reprocessModal" class="btn btn-default">Process Arrear Bill</a>
                                                </c:if>
                                            </c:if>
                                            <c:if test="${bill.isbillPrepared == 'N'}">
                                                <c:if test="${not empty bill.billno}">
                                                    Under Process
                                                </c:if>
                                            </c:if>
                                        </td>
                                        <td>
                                            <c:if test="${bill.isbillPrepared == 'Y'}">
                                                <c:if test="${bill.lockBill lt 2}">
                                                    <a href="lockBill.htm?billNo=${bill.billno}&sltMonth=${command.sltMonth}&sltYear=${command.sltYear}&txtbilltype=PAY" onclick="return confirm('Are you sure you want to lock this bill? \\n After Lock Bill can not edit ..... ')">Lock Bill</a>
                                                </c:if>
                                                <c:if test="${bill.lockBill ge 2}">
                                                    Locked
                                                </c:if>
                                            </c:if>                                        
                                        </td>

                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                    <div class="panel-footer">
                        <div class="row">
                            <div class="col-sm-1">
                                <form:form action="newBill.htm" method="post">
                                    <button type="submit" class="btn btn-default">New Bill</button>
                                </form:form>
                            </div>
                            <div class="col-sm-1">
                                <form:form action="newArrearBill.htm" method="post">
                                    <button type="submit" class="btn btn-default">New Arrear Bill</button>
                                </form:form>
                            </div>
                        </div>
                    </div>

                </div>
            </div>
            
            
            <!-- Submit to IFMS Verification Modal -->
            <div id="submitToIFMS" class="modal fade" role="dialog">
                <div class="modal-dialog" style="width:1000px;">

                    <!-- Modal content-->
                    <div class="modal-content">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal">&times;</button>
                            <h4 class="modal-title">Bill Upload</h4>
                        </div>
                        <div class="modal-body">

                        </div>
                        <div class="modal-footer">
                            <span id="msg"></span>                        
                            <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                        </div>
                    </div>

                </div>
            </div>

            <!-- Bill Status Modal -->
            <div id="billStatusModal" class="modal fade" role="dialog">
                <div class="modal-dialog" style="width:1000px;">

                    <!-- Modal content-->
                    <div class="modal-content">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal">&times;</button>
                            <h4 class="modal-title">Bill Status</h4>
                        </div>
                        <div class="modal-body">

                        </div>
                        <div class="modal-footer">
                            <span id="msg"></span>                        
                            <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                        </div>
                    </div>

                </div>
            </div>

            <!-- Print Bill Modal -->
            <div id="myModal" class="modal fade" role="dialog">
                <div class="modal-dialog" style="width:1000px;">

                    <!-- Modal content-->
                    <div class="modal-content">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal">&times;</button>
                            <h4 class="modal-title">Bill Print Details</h4>
                        </div>
                        <div class="modal-body">

                        </div>
                        <div class="modal-footer">
                            <span id="msg"></span>                        
                            <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                        </div>
                    </div>

                </div>
            </div>
            <!-- Reprocess Bill Modal -->
            <div id="reprocessModal" class="modal fade" role="dialog">
                <div class="modal-dialog" style="width:1000px;">

                    <!-- Modal content-->

                    <div class="modal-content">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal">&times;</button>
                            <h4 class="modal-title">Reprocess Bill</h4>
                        </div>
                        <div class="modal-body">

                        </div>
                        <div class="modal-footer">
                            <span id="msg"></span>
                            <button type="submit" class="btn btn-default" name="Process">Process</button>
                            <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                        </div>
                    </div>

                </div>
            </div>
    </body>
</html>
