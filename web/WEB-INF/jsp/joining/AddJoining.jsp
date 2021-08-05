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
            $(document).ready(function(){
                if($('#hidDeptCode').val() != ''){
                    getDeptWiseOfficeList();
                    //$('#tempType').val("E");
                }
                if($('#hidTempOffCode').val() != ''){
                    getOfficeWiseGPCList($('#hidTempOffCode').val());
                    $('#tempType').val("E");
                }
                if($('#hidTempPostCode').val() != ''){
                    getGPCWiseSPCList($('#hidTempOffCode').val(),$('#hidTempPostCode').val());
                    $('#tempType').val("E");
                }
                
                $(document).ajaxStop(function() {
                    if($('#tempType').val() == "E"){
                        //alert("Inside Ajax Stop");
                        $('#hidOffCode').val($('#hidTempOffCode').val());
                        $('#hidPostCode').val($('#hidTempPostCode').val());
                        $('#spc').val($('#hidTempSpc').val());
                        $('#sltFieldOffice').val($('#hidTempFieldOffCode').val());
                        $('#tempType').val();
                    }
                });
            });
            
            function getDeptWiseOfficeList() {
                $('#hidOffCode').empty();
                var url = 'getOfficeListJSON.htm?deptcode=' + $('#hidDeptCode').val();
                $('#hidOffCode').append('<option value="">--Select Office--</option>');
                $.getJSON(url, function(data) {
                    $.each(data, function(i, obj) {
                        $('#hidOffCode').append('<option value="' + obj.value + '">' + obj.label + '</option>');
                    });
                });
            }
            
            function getOfficeWiseGPCList(offcode) {
                var offCode = "";
                if(offcode != ''){
                    offCode = offcode;
                }else{
                    offCode = $('#hidOffCode').val();
                }
                
                $('#hidPostCode').empty();
                var url = 'getPostCodeListJSON.htm?offcode=' + offCode;
                $('#hidPostCode').append('<option value="">--Select--</option>');
                $.getJSON(url, function(data) {
                    $.each(data, function(i, obj) {
                        $('#hidPostCode').append('<option value="' + obj.value + '">' + obj.label + '</option>');
                    });
                });
                
                $('#sltFieldOffice').empty();
                var url = 'joiningGetFieldOffListJSON.htm?offcode=' + offCode;
                $('#sltFieldOffice').append('<option value="">--Select--</option>');
                $.getJSON(url, function(data) {
                    $.each(data, function(i, obj) {
                        $('#sltFieldOffice').append('<option value="' + obj.value + '">' + obj.label + '</option>');
                    });
                });
            }
            
            function getGPCWiseSPCList(offCode,gpc) {
                var offcode = "";
                var postcode = "";
                if(gpc != ''){
                    offcode = offCode;
                    postcode = gpc;
                }else{
                    offcode = $('#hidOffCode').val();
                    postcode = $('#hidPostCode').val();
                }
                
                $('#spc').empty();
                var url = 'joiningGetGPCWiseSPCListJSON.htm?offcode=' + offcode+'&gpc='+postcode;
                $('#spc').append('<option value="">--Select--</option>');
                $.getJSON(url, function(data) {
                    $.each(data, function(i, obj) {
                        $('#spc').append('<option value="' + obj.value + '">' + obj.label + '</option>');
                    });
                });
            }
            function getPost() {
                $('#spn').val($('#spc option:selected').text());
                $('#joiningModal').modal('toggle');
            }
            
            function toggleTypeVal(){
                $('#tempType').val("");
            }
        </script>
    </head>
    <body>
        <form:form action="saveEmployeeJoining.htm" method="post" commandName="jForm">
            <form:hidden path="jempid"/>
            <form:hidden path="notId"/>
            <form:hidden path="notType"/>
            <form:hidden path="rlvId"/>
            <form:hidden path="joinId"/>
            <form:hidden path="hidLcrId"/>
            <form:hidden path="hidAddition"/>
            
            <form:hidden path="hidTempOffCode" id="hidTempOffCode"/>
            <form:hidden path="hidTempPostCode" id="hidTempPostCode"/>
            <form:hidden path="hidTempSpc" id="hidTempSpc"/>
            <form:hidden path="hidTempFieldOffCode" id="hidTempFieldOffCode"/>
            
            <input type="hidden" id="tempType" value="V"/>
            <input type="hidden" id="entpsc" value="${entpsc}"/>
            <div class="container-fluid">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        Employee Joining
                    </div>
                    <div class="panel-body">
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
                                <form:input class="form-control" path="notType" id="notType" readonly="true"/>
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
                                <form:input class="form-control" path="notOrdNo" id="notOrdNo" readonly="true"/>
                            </div>
                            <div class="col-lg-2">
                                (c) Order Date
                            </div>
                            <div class="col-lg-2">
                                <form:input class="form-control" path="notOrdDt" id="notOrdDt" readonly="true"/>
                            </div>
                        </div>
                            
                        <div class="row" style="margin-bottom: 7px;">
                            <div class="col-lg-2">
                                (d) Department Name
                            </div>
                            <div class="col-lg-10">
                                <form:input class="form-control" path="notiDeptName" id="notiDeptName" readonly="true"/>
                            </div>
                            <div class="col-lg-2">
                                
                            </div>
                        </div>
                            
                        <div class="row" style="margin-bottom: 7px;">
                            <div class="col-lg-2">
                                (e) Office Name
                            </div>
                            <div class="col-lg-10">
                                <form:input class="form-control" path="notiOffName" id="notiOffName" readonly="true"/>
                            </div>
                            <div class="col-lg-2">
                                
                            </div>
                        </div>
                            
                        <div class="row" style="margin-bottom: 7px;">
                            <div class="col-lg-2">
                                (f) Authority
                            </div>
                            <div class="col-lg-10">
                                <form:input class="form-control" path="notiSpn" id="notiSpn" readonly="true"/>
                            </div>
                            <div class="col-lg-2">
                                
                            </div>
                        </div>
                            
                        <div class="row" style="margin-bottom: 7px;">
                            <div class="col-lg-2">
                                (g) Note
                            </div>
                            <div class="col-lg-10">
                                <form:textarea class="form-control" path="notNote" id="notNote" readonly="true"/>
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
                            <div class="col-lg-2">
                                (a) Relieve Report/Letter No.
                            </div>
                            <div class="col-lg-6">
                                <form:input path="rlvOrdNo" id="rlvOrdNo" readonly="true"/>
                            </div>
                            <div class="col-lg-2">
                                (b) Order Date
                            </div>
                            <div class="col-lg-2">
                                <div class="input-group date" id="processDate">
                                    <form:input path="rlvOrdDt" id="rlvOrdDt" readonly="true"/>
                                </div>
                            </div>
                        </div>
                        
                        <div class="row" style="margin-bottom: 7px;">
                            <div class="col-lg-2">
                                (c) Relieved On
                            </div>
                            <div class="col-lg-2">
                                <div class="input-group date" id="processDate">
                                    <form:input path="rlvDt" id="rlvDt" readonly="true"/>
                                </div>
                            </div>
                            <div class="col-lg-2">
                                (d) Relieved Time
                            </div>
                            <div class="col-lg-2">
                                <form:input path="rlvTime" id="rlvTime" readonly="true"/>
                            </div>
                        </div>
                        
                        <div class="row" style="margin-bottom: 7px;">
                            <div class="col-lg-2">
                                (e) Due Date of Joining
                            </div>
                            <div class="col-lg-2">
                                <div class="input-group date" id="processDate">
                                    <form:input path="joiningDueDt" id="joiningDueDt" readonly="true"/>
                                </div>
                            </div>
                            <div class="col-lg-2">
                                (f) Joining Time
                            </div>
                            <div class="col-lg-2">
                                <form:input path="joiningDueTime" id="joiningDueTime" readonly="true"/>
                            </div>
                        </div>
                                
                        <div class="row" style="margin-bottom: 7px;">
                            <div class="col-lg-6">
                                <label>Joining Order Details</label>
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
                                (a) Joining Report/Letter No.<span style="color: red">*</span>
                            </div>
                            <div class="col-lg-6">
                                <form:input path="joiningOrdNo" id="joiningOrdNo"/>
                            </div>
                            <div class="col-lg-2">
                                (b) Order Date<span style="color: red">*</span>
                            </div>
                            <div class="col-lg-2">
                                <div class="input-group date" id="processDate">
                                    <form:input path="joiningOrdDt" id="joiningOrdDt" class="txtDate"/>
                                    <span class="input-group-addon">
                                        <span class="glyphicon glyphicon-time"></span>
                                    </span>
                                </div>
                            </div>
                        </div>
                                    
                        <div class="row" style="margin-bottom: 7px;">
                            <div class="col-lg-2">
                                (c) Joined On<span style="color: red">*</span>
                            </div>
                            <div class="col-lg-2">
                                <div class="input-group date" id="processDate">
                                    <form:input path="joiningDt" id="joiningDt" class="txtDate"/>
                                    <span class="input-group-addon">
                                        <span class="glyphicon glyphicon-time"></span>
                                    </span>
                                </div>
                            </div>
                            <div class="col-lg-2">
                                (d) Joined Time<span style="color: red">*</span>
                            </div>
                            <div class="col-lg-2">
                                <form:select path="sltJoiningTime" id="sltJoiningTime" class="form-control">
                                    <option value="">-Select-</option>
                                    <form:option value="FN">Fore Noon</form:option>
                                    <form:option value="AN">After Noon</form:option>
                                </form:select>
                            </div>
                        </div>
                                    
                        <div class="row" style="margin-bottom: 7px;">
                            <div class="col-lg-2">
                                (e) Unavailed joining Time Granted as EL
                            </div>
                            <div class="col-lg-2">
                                <form:checkbox path="chkujt" id="chkujt" value="Y"/>
                            </div>
                            <div class="col-lg-2">
                                
                            </div>
                            <div class="col-lg-2">
                                
                            </div>
                        </div>
                            
                        <div class="row" style="margin-bottom: 7px;">
                            <div class="col-lg-2">
                                (f) From Date
                            </div>
                            <div class="col-lg-2">
                                <div class="input-group date" id="processDate">
                                    <form:input path="ujtFrmDt" id="ujtFrmDt" class="txtDate"/>
                                    <span class="input-group-addon">
                                        <span class="glyphicon glyphicon-time"></span>
                                    </span>
                                </div>
                            </div>
                            <div class="col-lg-2">
                                (g) To Date
                            </div>
                            <div class="col-lg-2">
                                <div class="input-group date" id="processDate">
                                    <form:input path="ujtToDt" id="ujtToDt" class="txtDate"/>
                                    <span class="input-group-addon">
                                        <span class="glyphicon glyphicon-time"></span>
                                    </span>
                                </div>
                            </div>
                        </div>
                                    
                        <div class="row" style="margin-bottom: 7px;">
                            <div class="col-lg-2">
                                (h) Authority
                            </div>
                            <div class="col-lg-9">
                                <form:input class="form-control" path="spn" id="spn"/>                           
                            </div>
                            <div class="col-lg-1">
                                <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#joiningModal" onclick="toggleTypeVal();">
                                    <span class="glyphicon glyphicon-search"></span> Search
                                </button>
                            </div>
                        </div>
                            
                        <div class="row" style="margin-bottom: 7px;">
                            <div class="col-lg-2">
                                (i) Field Office
                            </div>
                            <div class="col-lg-9">
                                <form:select path="sltFieldOffice" id="sltFieldOffice" class="form-control">
                                    <option value="">--Select Office--</option>
                                    <form:options items="${fieldofflist}" itemValue="value" itemLabel="label"/>
                                </form:select>                        
                            </div>
                            <div class="col-lg-1">
                                
                            </div>
                        </div>
                            
                        <div class="row" style="margin-bottom: 7px;">
                            <div class="col-lg-2">
                                <label for="note">Note(if any)</label>
                            </div>
                            <div class="col-lg-9">
                                <form:textarea class="form-control" path="note" id="note"/>
                            </div>
                            <div class="col-lg-1">
                            </div>
                        </div>
                    </div>
                    <div class="panel-footer">
                        <button type="submit" name="submit" value="Save" class="btn btn-default">Save Joining</button>
                        <c:if test="${not empty jForm.joinId}">
                            <button type="submit" name="submit" value="Delete" class="btn btn-default">Delete Joining</button>
                        </c:if>
                    </div>
                </div>
            </div>
                            
            <div id="joiningModal" class="modal" role="dialog">
                <div class="modal-dialog">
                    <!-- Modal content-->
                    <div class="modal-content">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal">&times;</button>
                            <h4 class="modal-title">Details of Authority</h4>
                        </div>
                        <div class="modal-body">
                            <div class="row" style="margin-bottom: 7px;">
                                <div class="col-lg-2">
                                    <label for="sltDept">Department</label>
                                </div>
                                <div class="col-lg-9">
                                    <form:select path="hidDeptCode" id="hidDeptCode" class="form-control" onchange="getDeptWiseOfficeList();">
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
                                    <form:select path="hidOffCode" id="hidOffCode" class="form-control" onchange="getOfficeWiseGPCList('');">
                                        <option value="">--Select Office--</option>
                                    </form:select>
                                </div>
                                <div class="col-lg-1">
                                </div>
                            </div>
                            <div class="row" style="margin-bottom: 7px;">
                                <div class="col-lg-2">
                                    <label for="note">Generic Post</label>
                                </div>
                                <div class="col-lg-9">
                                    <form:select path="hidPostCode" id="hidPostCode" class="form-control" onchange="getGPCWiseSPCList('','');">
                                        <option value="">--Select Office--</option>
                                    </form:select>
                                </div>
                                <div class="col-lg-1">
                                </div>
                            </div>
                            <div class="row" style="margin-bottom: 7px;">
                                <div class="col-lg-2">
                                    <label for="note">Substantive Post</label>
                                </div>
                                <div class="col-lg-9">
                                    <form:select path="jspc" id="spc" class="form-control" onchange="getPost();">
                                        <option value="">--Select--</option>
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
