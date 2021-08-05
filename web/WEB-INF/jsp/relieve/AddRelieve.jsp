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
        <script type="text/javascript" src="js/bootstrap.min.js"></script>
        <script type="text/javascript" src="js/bootstrap-datetimepicker.js"></script>
        <script type="text/javascript">
            $(document).ready(function() {

            });
            
            function saveCheck() {
                if ($('#txtRlvOrdNo').val() == '') {
                    alert("Please Enter Relieve No");
                    $('#txtRlvOrdNo').focus();
                    return false;
                }
                if ($('#txtRlvOrdDt').val() == '') {
                    alert("Please Enter Relieve Order Date");
                    return false;
                }
                if ($('#txtRlvDt').val() == '') {
                    alert("Please Enter Relieve Date");
                    return false;
                }
                if ($('#txtJoinDt').val() == '') {
                    alert("Please Enter Joining Date");
                    return false;
                }
                if($('#sltRlvPost').val() == ''){
                    alert("Please select Post from which employee will be relieved");
                    return false;
                }
                return true;
            }
        </script>
    </head>
    <body>
        <form:form action="saveRelieve.htm" method="post" commandName="rlvForm">
            <div class="container-fluid">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        Employee Relieve
                    </div>
                    <div class="panel-body">
                        <form:hidden path="empid" id="empid"/>
                        <form:hidden path="hidNotId" id="hidNotId"/>
                        <form:hidden path="doe" id="doe"/>
                        <form:hidden path="rlvId" id="rlvId"/>

                        <%--<input type="hidden" id="hidDeptCode" value="${RelieveForm.sltDept}"/>
                        <input type="hidden" id="hidOffCode" value="${RelieveForm.sltOffice}"/>

                        <input type="hidden" id="hidRlvTime" value="${RelieveForm.sltRlvTime}"/>
                        <input type="hidden" id="hidJoinTime" value="${RelieveForm.sltJoinTime}"/>
                        <input type="hidden" id="hidRlvPost" value="${RelieveForm.sltRlvPost}"/>--%>

                        <div class="row" style="margin-bottom: 7px;">
                            <div class="col-lg-6">
                                <label>Notification Order Details</label>
                            </div>
                            <div class="col-lg-2">   

                            </div>
                            <div class="col-lg-2">

                            </div>
                            <div class="col-lg-2">

                            </div>
                        </div>

                        <div class="row" style="margin-bottom: 7px;">
                            <div class="col-lg-2">
                                (a) Type
                            </div>
                            <div class="col-lg-6">
                                <form:input class="form-control" path="hidNotType" id="hidNotType" readonly="true"/>
                            </div>
                            <div class="col-lg-2">

                            </div>
                            <div class="col-lg-2">

                            </div>
                        </div>

                        <div class="row" style="margin-bottom: 7px;">
                            <div class="col-lg-2">
                                (b) Order No
                            </div>
                            <div class="col-lg-2">
                                <form:input class="form-control" path="ordNo" id="ordNo" readonly="true"/>
                            </div>
                            <div class="col-lg-2">
                                (c) Order Date
                            </div>
                            <div class="col-lg-2">
                                <form:input class="form-control" path="ordDt" id="ordDt" readonly="true"/>
                            </div>
                        </div>

                        <div class="row" style="margin-bottom: 7px;">
                            <div class="col-lg-2">
                                (d) Department Name
                            </div>
                            <div class="col-lg-10">
                                <form:input class="form-control" path="deptname" id="deptname" readonly="true"/>
                            </div>
                        </div>

                        <div class="row" style="margin-bottom: 7px;">
                            <div class="col-lg-2">
                                (e) Office Name
                            </div>
                            <div class="col-lg-10">
                                <form:input class="form-control" path="offname" id="offname" readonly="true"/>
                            </div>
                        </div>

                        <div class="row" style="margin-bottom: 7px;">
                            <div class="col-lg-2">
                                (f) Authority
                            </div>
                            <div class="col-lg-10">
                                <form:input class="form-control" path="postname" id="postname" readonly="true"/>
                            </div>
                        </div>

                        <div class="row" style="margin-bottom: 7px;">
                            <div class="col-lg-2">
                                (g) Note
                            </div>
                            <div class="col-lg-10">
                                <form:textarea class="form-control" path="note" id="note" readonly="true"/>
                            </div>
                        </div>

                        <div class="row" style="margin-bottom: 7px;">
                            <div class="col-lg-6">
                                <label>Relieve Order Details</label>
                            </div>
                            <div class="col-lg-2">   

                            </div>
                            <div class="col-lg-2">

                            </div>
                            <div class="col-lg-2">

                            </div>
                        </div>    

                        <div class="row" style="margin-bottom: 7px;">
                            <div class="col-lg-3">
                                (a) Relieve Report/Letter No.<span style="color: red">*</span>
                            </div>
                            <div class="col-lg-2">
                                <form:input path="txtRlvOrdNo" id="txtRlvOrdNo"/>
                            </div>
                            <div class="col-lg-2">
                                (b) Date<span style="color: red">*</span>
                            </div>
                            <div class="col-lg-2">
                                <div class="input-group date" id="processDate">
                                    <form:input path="txtRlvOrdDt" id="txtRlvOrdDt" class="txtDate" readonly="true"/>
                                    <span class="input-group-addon">
                                        <span class="glyphicon glyphicon-time"></span>
                                    </span>
                                </div>
                            </div>
                        </div>

                        <div class="row" style="margin-bottom: 7px;">
                            <div class="col-lg-3">
                                (c) Relieved On<span style="color: red">*</span>
                            </div>
                            <div class="col-lg-2">
                                <div class="input-group date" id="processDate">
                                    <form:input path="txtRlvDt" id="txtRlvDt" class="txtDate" readonly="true"/>
                                    <span class="input-group-addon">
                                        <span class="glyphicon glyphicon-time"></span>
                                    </span>
                                </div>
                            </div>
                            <div class="col-lg-2">
                                (d) Time<span style="color: red">*</span>
                            </div>
                            <div class="col-lg-2">
                                <form:select path="sltRlvTime" id="sltRlvTime" class="form-control">
                                    <option value="">-Select-</option>
                                    <form:option value="FN">Fore Noon</form:option>
                                    <form:option value="AN">After Noon</form:option>
                                </form:select>
                            </div>
                        </div>

                        <div class="row" style="margin-bottom: 7px;">
                            <div class="col-lg-3">
                                (e) Due Date of Joining<span style="color: red">*</span>
                            </div>
                            <div class="col-lg-2">
                                <div class="input-group date" id="processDate">
                                    <form:input path="txtJoinDt" id="txtJoinDt" class="txtDate" readonly="true"/>
                                    <span class="input-group-addon">
                                        <span class="glyphicon glyphicon-time"></span>
                                    </span>
                                </div>
                            </div>
                            <div class="col-lg-2">
                                (f) Time<span style="color: red">*</span>
                            </div>
                            <div class="col-lg-2">
                                <form:select path="sltJoinTime" id="sltJoinTime" class="form-control">
                                    <option value="">-Select-</option>
                                    <form:option value="FN">Fore Noon</form:option>
                                    <form:option value="AN">After Noon</form:option>
                                </form:select>
                            </div>
                        </div>

                        <div class="row" style="margin-bottom: 7px;">
                            <div class="col-lg-2">

                            </div>
                            <div class="col-lg-2">
                                <form:radiobutton path="rdRqRl" value="Y"/> Relinquished
                            </div>
                            <div class="col-lg-2">

                            </div>
                            <div class="col-lg-2">
                                <form:radiobutton path="rdRqRl" value="N"/> Relieved
                            </div>
                        </div>

                        <div class="row" style="margin-bottom: 7px;">
                            <div class="col-lg-2">
                                (g) Relieved From<span style="color: red">*</span>
                            </div>
                            <div class="col-lg-8">
                                <form:select path="sltRlvPost" id="sltRlvPost" class="form-control">
                                    <form:options items="${relievedPostlist}" itemValue="value" itemLabel="label"/>
                                </form:select>
                            </div>
                            <div class="col-lg-2">

                            </div>
                        </div>

                        <%--<div class="row" style="margin-bottom: 7px;">
                            <div class="col-lg-2">
                                (h) Relieved From Additional Charges
                            </div>
                            <div class="col-lg-8">
                                <form:select path="sltAddlCharge" id="sltAddlCharge" class="form-control">
                                    <form:options items="${addlChargeList}" itemValue="value" itemLabel="label"/>
                                </form:select>
                            </div>
                            <div class="col-lg-2">
                                
                            </div>
                        </div>--%>

                        <div class="row" style="margin-bottom: 7px;">
                            <div class="col-lg-2">
                                Authority:
                            </div>
                            <div class="col-lg-9">
                                <form:input class="form-control" path="authPostName" id="authPostName"/>                           
                            </div>
                            <div class="col-lg-1">
                                <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#releiveAuthorityModal">
                                    <span class="glyphicon glyphicon-search"></span> Search
                                </button>
                            </div>
                        </div>

                        <div class="row" style="margin-bottom: 7px;">
                            <div class="col-lg-2">
                                Note(if any)
                            </div>
                            <div class="col-lg-9">
                                <form:textarea class="form-control" path="txtRelieveNote" id="txtRelieveNote"/>
                            </div>
                            <div class="col-lg-1">
                            </div>
                        </div>
                    </div>
                    <div class="panel-footer">
                        <button type="submit" name="submit" value="Save" class="btn btn-default">Save Relieve</button>
                        <c:if test="${not empty rlvForm.rlvId}">
                            <button type="submit" name="submit" value="Delete" class="btn btn-default">Delete</button>
                        </c:if>
                    </div>
                </div>
            </div>

            <div id="releiveAuthorityModal" class="modal" role="dialog">
                <div class="modal-dialog">
                    <!-- Modal content-->
                    <div class="modal-content">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal">&times;</button>
                            <h4 class="modal-title">Authority</h4>
                        </div>
                        <div class="modal-body">
                            <div class="row" style="margin-bottom: 7px;">
                                <div class="col-lg-2">
                                    <label for="sltDept">Department</label>
                                </div>
                                <div class="col-lg-9">
                                    <form:select path="authHidDeptCode" id="authHidDeptCode" class="form-control" onchange="getDeptWiseOfficeList('A');">
                                        <option value="">--Select Department--</option>
                                        <form:options items="${deptlist}" itemValue="deptCode" itemLabel="deptName"/>
                                    </form:select>
                                </div>
                                <div class="col-lg-1">
                                </div>
                            </div>
                            <div class="row" style="margin-bottom: 7px;">
                                <div class="col-lg-2">
                                    <label for="note">Office</label>
                                </div>
                                <div class="col-lg-9">
                                    <form:select path="authHidOffCode" id="authHidOffCode" class="form-control" onchange="getOfficeWisePostList('A');">
                                        <option value="">--Select Office--</option>
                                    </form:select>
                                </div>
                                <div class="col-lg-1">
                                </div>
                            </div>
                            <div class="row" style="margin-bottom: 7px;">
                                <div class="col-lg-2">
                                    <label for="note">Post</label>
                                </div>
                                <div class="col-lg-9">
                                    <form:select path="authSpc" id="authSpc" class="form-control" onchange="getPost('A');">
                                        <option value="">--Select Post--</option>
                                    </form:select>
                                </div>
                                <div class="col-lg-1">
                                </div>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                        </div>
                    </div>
                </div>
            </div>
        </form:form>
    </body>
    <script type="text/javascript">
        $(function() {
            $('.txtDate').datetimepicker({
                format: 'D-MMM-YYYY',
                useCurrent: false,
                ignoreReadonly: true
            });
        });
    </script>
</html>
