<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Human Resources Management System, Government of Odisha</title>

        <link href="css/bootstrap.min.css" rel="stylesheet">
        <link href="font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">

        <script type="text/javascript" src="js/jquery.min.js"></script>  
        <script type="text/javascript" src="js/bootstrap.min.js"></script>
        <script type="text/javascript">
            $(document).ready(function() {
                if($('#groupAData').val() == 0){
                    $('#groupAData').val('');
                }
                if($('#groupBData').val() == 0){
                    $('#groupBData').val('');
                }
                if($('#groupCData').val() == 0){
                    $('#groupCData').val('');
                }
                if($('#groupDData').val() == 0){
                    $('#groupDData').val('');
                }
                $('#financialYear').change(function() {
                    var dataString = 'fy='+$(this).val();
                    $.ajax({
                        type: "GET",
                        url: 'duplicateAERFinancialYear.htm',
                        data: dataString,
                        cache: false,
                        success: function(html) {
                            if(html.indexOf("Y") > -1){
                                alert("Data is present for Selected Financial Year.");
                                $('#btnSave').prop("disabled",true);
                                $('#financialYear').val("");
                            }else{
                                $('#btnSave').prop("disabled",false);
                            }
                        }
                    });
                });
            });
            
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
            
            function validateForm(){
                if($('#financialYear').val() == ''){
                    alert("Please select Financial Year");
                    return false;
                }
                if($('#groupAData').val() == '' || $('#groupBData').val() == '' || $('#groupCData').val() == '' || $('#groupDData').val() == ''){
                    alert("Please enter at least one Sanctioned Post");
                    return false;
                }
                return true;
            }
        </script>
    </head>
    <body>
        <div class="container-fluid">
            <form:form action="saveOfficeWiseSanctionedStrength.htm" commandName="officebean">
                <form:hidden path="aerId"/>
                <div class="panel panel-default">
                    <div class="panel-header">
                        <div align="center" style="font-weight: bold;margin-top:20px;">
                            <c:out value="${offName}"/>
                        </div>
                        <div align="center" style="font-weight: bold;margin-top:20px;">
                            SANCTIONED STRENGTH
                        </div>
                    </div>
                    <div class="panel-body">
                        <table class="table table-striped table-bordered" width="90%">
                            <thead>
                                <tr>
                                    <td colspan="5" align="center">
                                        Financial Year&nbsp;&nbsp;&nbsp;
                                        <form:select path="financialYear" id="financialYear">
                                            <form:option value="" label="--Select--"/>
                                            <form:options items="${fylist}"/>
                                        </form:select>
                                    </td>
                                </tr>
                                <tr>
                                    <th width="15%">Group A</th>
                                    <th width="15%">Group B</th>
                                    <th width="15%">Group C</th>
                                    <th width="15%">Group D</th>
                                    <th width="15%">Grant-in-Aid</th>
                                </tr>
                            </thead>
                            <hr />
                            <tbody>
                                <tr>
                                    <td>
                                        <form:input path="groupAData" id="groupAData" onkeypress="return onlyIntegerRange(event)"/>
                                    </td>
                                    <td>
                                        <form:input path="groupBData" id="groupBData" onkeypress="return onlyIntegerRange(event)"/>
                                    </td>
                                    <td>
                                        <form:input path="groupCData" id="groupCData" onkeypress="return onlyIntegerRange(event)"/>
                                    </td>
                                    <td>
                                        <form:input path="groupDData" id="groupDData" onkeypress="return onlyIntegerRange(event)"/>
                                    </td>
                                    <td>
                                        <form:input path="grantInAid" id="grantInAid" onkeypress="return onlyIntegerRange(event)"/>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                    <div class="panel-footer">
                        <input type="submit" name="action" id="btnSave" value="Save" class="btn btn-primary" onclick="return validateForm();"/>
                        <input type="submit" name="action" id="btnSave" value="Back" class="btn btn-primary"/>
                    </div>
                </div>
            </form:form>
        </div>
    </body>
</html>
