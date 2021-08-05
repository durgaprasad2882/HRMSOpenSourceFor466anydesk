<%-- 
    Document   : PrepareArrearBill
    Created on : 17 Mar, 2018, 11:23:04 AM
    Author     : Surendra
--%>



<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<!DOCTYPE html>
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
            function validate() {
                var checkBoxlength = $("input[name=billgroupId]:checked").length;
                if (checkBoxlength == 0) {
                    alert("Please select bill.");
                    return false;
                }
                if ($("#processDate1").val() == "") {
                    alert("Bill Date Cannot Be Blank");
                    return false;
                }
            }
            function validateForm() {
                if ($("#processDate1").val() == "") {
                    alert("Bill Date Cannot Be Blank");
                    return false;
                }
            }
        </script>
    </head>
    <body>
        <form:form action="prepareNewArrearBillform.htm" method="post" commandName="BillBrowserbean" >
            <div class="container-fluid">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <div class="row">                            
                            <div class="col-lg-2">
                                <div class="form-group">
                                    <label for="billType">Bill Type:</label>
                                    <form:select path="txtbilltype" id="txtbilltype" class="form-control">
                                        <option value="ARREAR">Arrear Bill</option>
                                    </form:select>                
                                </div>
                            </div>
                            <div class="col-lg-2">
                                <div class="form-group">
                                    <label for="processDate">Process Date:</label>
                                    <div class='input-group date' id='processDate'>
                                        <form:input class="form-control" id="processDate1" path="processDate" />
                                        <span class="input-group-addon">
                                            <span class="glyphicon glyphicon-time"></span>
                                        </span>
                                    </div>
                                </div>
                            </div>
                        </div>
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
                                <input type="submit" name="action" value="Ok" class="btn btn-default" onclick="return validateForm()"/>



                            </div>
                        </div>               
                    </div>
                    <div class="panel-body">
                        <table class="table table-bordered">
                            <thead>
                                <tr>
                                    <th width="5%">Select Bill</th>
                                    <th width="30%">Bill Name</th>                                
                                    <th width="10%">Chart of Account</th>                                
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${billGroupList}" var="billattr">
                                    <tr>
                                        <td><input type="checkbox" name="billgroupId" value="${billattr.billgroupId}"/></td>
                                        <td>${billattr.billDesc}</td>                                
                                        <td>${billattr.chartofAcc}</td>                                
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                    <div class="panel-footer">
                        <input type="submit" name="action" value="Back" class="btn btn-default"/>
                        <c:if test="${not empty billGroupList}">
                            <input type="submit" class="btn btn-default" name="action" value="Process" onclick="return validate()"/>
                        </c:if>
                    </div>
                </div>
            </div>
        </form:form> 
        <!-- Modal -->
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
        <script type="text/javascript">
            $(function () {
                $('#processDate').datetimepicker({
                    format: 'D-MMM-YYYY'
                });
            });
        </script>
    </body>
</html>

