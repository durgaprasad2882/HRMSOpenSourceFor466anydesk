<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Human Resources Management System, Government of Odisha</title>
        <link href="css/bootstrap.min.css" rel="stylesheet">
        <link href="font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">                
        <script src="js/jquery.min.js" type="text/javascript"></script>        
        <script src="js/bootstrap.min.js" type="text/javascript"></script>
        <script type="text/javascript">
            $(window).load(function() {
                // Fill modal with content from link href
                $("#viewModal").on("show.bs.modal", function(e) {
                    var link = $(e.relatedTarget);
                    $(this).find(".modal-body").load(link.attr("href"));
                });

                $("#editModal").on("show.bs.modal", function(e) {
                    var link = $(e.relatedTarget);
                    $(this).find(".modal-content").load(link.attr("href"));
                });
            })

            function SaveIncomeTax(aqSlNo) {
                var taxAmt = $("#txtAmt"+aqSlNo).val();
                var taxBillNo = $("#billNo").val();
                if (taxAmt == '') {
                    alert("Please Enter Income Tax Amount");
                    return false;
                } else {
                    $.ajax({
                        type: "GET",
                        url: "saveItArrMast.htm",
                        data: {aqslno: aqSlNo, billNo: taxBillNo, taxAmt: taxAmt},
                        success: function(data) {
                            
                        },
                        error: function() {
                            alert('Error Occured');
                        }
                    });
                }
            }

            function SaveCPF(aqSlNo) {
                var cpfVal = $("#txtCpf"+aqSlNo).val();
                var taxBillNo = $("#billNo").val();
                if (cpfVal == '') {
                    alert("Please Enter CPF Contribution ");
                    return false;
                } else {
                    $.ajax({
                        type: "GET",
                        url: "saveCpfArrMast.htm",
                        data: {aqslno: aqSlNo, billNo: taxBillNo, cpfAmt: cpfVal},
                        success: function(data) {
                            
                        },
                        error: function() {
                            alert('Error Occured');
                        }
                    });
                }
            }
            
            function onlyIntegerRange(e) {
                var browser = navigator.appName;
                if (browser == "Netscape") {
                    var keycode = e.which;
                    if ((keycode >= 48 && keycode <= 57) || keycode == 8 || keycode == 0)
                        return true;
                    else
                        return false;
                } else {
                    if ((e.keyCode >= 48 && e.keyCode <= 57) || e.keycode == 8 || e.keycode == 0)
                        e.returnValue = true;
                    else
                        e.returnValue = false;
                }
            }
            
        </script>
                <style type="text/css">
            .table > tbody > tr > td, .table > tbody > tr > th{padding:2px;}

        </style>
    </head>
    <body>

                <div class="panel-body">
                    <table class="table table-bordered" style="font-size:10pt;">
                        <thead>
                            <tr>
                                <th width="6%">Sl No</th>
                                <th width="8%">HRMS ID</th>
                                <th width="23%">Employee Name</th>
                                <th width="23%">Designation</th>
                                <th width="8%">Arrear Pay</th>
                                <th width="8%">Income Tax</th>
                                <th width="8%">CPF</th>
                                <th width="8%">PT</th>
                                <th width="8%">Net Pay</th>
                                <th>View</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${AqArrList}" var="arrAqMast" varStatus="cnt">    
                                <tr>
                                    <td>${cnt.index+1}</td>
                                    <td>${arrAqMast.empCode}</td>
                                    <td>${arrAqMast.empName}</td>
                                    <td>${arrAqMast.curDesg}</td>
                                    <td>${arrAqMast.arrearpay}</td>

                                    <td>${arrAqMast.incomeTaxAmt}</td>
                                    <td>${arrAqMast.cpfHead}</td>
                                    <td>${arrAqMast.pt}</td>
                                    <td>
                                        ${arrAqMast.arrearpay - (arrAqMast.incomeTaxAmt+arrAqMast.cpfHead)}
                                    </td>
                                    <td><input type="button" value="View" onclick="window.open('browseArrAqDataReport.htm?aqslno=${arrAqMast.aqSlNo}&billNo=${billNo}')" class="btn btn-default" style="color:#0000FF" /></td>

                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
                

        <!-- Print Bill Modal -->
        <div id="viewModal" class="modal fade" role="dialog">
            <div class="modal-dialog" style="width:1000px;">

                <!-- Modal content-->
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal">&times;</button>
                        <h4 class="modal-title">View Aquitance Details</h4>
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
        <div id="editModal" class="modal fade" role="dialog">
            <div class="modal-dialog" style="width:1000px;">

                <!-- Modal content-->
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal">&times;</button>
                        <h4 class="modal-title">Edit Aquitance Details</h4>
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
    </body>
</html>
