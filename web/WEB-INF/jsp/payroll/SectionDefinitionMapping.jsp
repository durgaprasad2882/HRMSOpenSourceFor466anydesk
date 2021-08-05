<%-- 
    Document   : SectionDefinitionMapping
    Created on : Oct 25, 2017, 4:25:39 AM
    Author     : Manas
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Human Resources Management System, Government of Odisha</title>      
        <link href="css/bootstrap.min.css" rel="stylesheet">
        <link href="font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
        <!-- LAYOUT v 1.3.0 -->
        <script type="text/javascript" src="js/jquery.min-1.9.1.js"></script>        
        <script type="text/javascript" src="js/bootstrap.min.js"></script>
        <script type="text/javascript">
            function addDivdata() {
                var checkBoxName = "chkAvail";
                var checkBoxes = $("input[name=" + checkBoxName + "]");
                var checkBoxlength = $("input[name=" + checkBoxName + "]:checked").length;
                var checkBoxName2 = "chkAssaign";
                var chkAssaign = "'chkAssaign'";
                var checkBoxes2 = $("input[name=" + checkBoxName2 + "]");
                var len = checkBoxes2.length;
                var remove = "'remove'";

                var checkboxarray = new Array();
                var i = 0;
                $("input:checked").each(function()
                {
                    var textboxobj = $(this).next();
                    var txtboxId = textboxobj.attr("id");
                    var va = document.getElementById(txtboxId).value;

                    checkboxarray[i] = va;
                    i++;
                });
                checkboxarray.sort(numOrdA);
                for (i = 0; i < checkboxarray.length; i++) {
                    $("input:checked").each(function() {
                        var textboxobj = $(this).next();
                        var txtboxId = textboxobj.attr("id");
                        var va = document.getElementById(txtboxId).value;
                        if (va == checkboxarray[i]) {
                            len = parseInt(len) + 1;
                            var chkId = "chk" + len;
                            $('#assigned').append('<div id="' + $(this).val() + '" style="padding:5px;"> <input type="checkbox" name="' + checkBoxName2 + '" value="' + $(this).val() + '" id="' + len + '" onclick="restrict2SelectCheckbox(' + chkAssaign + ',' + remove + ',' + len + ')"/>' + $("#" + $(this).val()).text() + '<a href="javascript:showSPCOrder(\'' + $(this).val() + '\')">Update OrderDate<\/a><\/div>');
                            var dataString = 'spc=' + $(this).val() + '&sectionId=' + $("#hidsecId").val();
                            $.ajax({
                                type: "POST",
                                url: 'assignPostAction.htm',
                                data: dataString,
                                async: false,
                                cache: false,
                                success: function(html) {

                                }
                            });
                            $("#" + $(this).val()).remove();

                        }
                    });
                }

                $('input[name=chkAvail]').attr('checked', false);
                len = 1;
                $('input[type=checkbox]').each(function() {
                    len++;
                });
            }

            function removeDivdata() {
                var checkBoxName = "chkAssaign";
                var checkBoxes = $("input[name=" + checkBoxName + "]");

                var checkBoxName2 = "chkAvail";
                var checkBoxes2 = $("input[name=" + checkBoxName2 + "]");
                var add = "'add'";
                var chkAvail = "'chkAvail'";
                var len = checkBoxes2.length;
                $.each(checkBoxes, function() {                    
                    if ($(this).prop('checked')==true) {                        
                        len = parseInt(len) + 1;
                        var divid = $(this).val();
                        $("#" + $(this).val() + " a").remove();
                        var divText = $("#" + $(this).val()).text();
                        var dataString = 'spc=' + $(this).val();                        
                        var tempid = "temp" + len;
                        $.ajax({
                            type: "POST",
                            url: 'removePostAction.htm',
                            data: dataString,
                            async: false,
                            cache: false,
                            success: function(html) {

                                $("#" + divid).remove();
                                $('#availemp').append('<div id="' + divid + '" style="padding:5px;" > <input type="checkbox" name="' + checkBoxName2 + '" value="' + divid + '" id="' + len + '" onclick="restrict2SelectCheckbox(' + chkAvail + ',' + add + ',' + len + ');getCheckOrder(this)"/>' + divText + ' <input type="hidden" name="temp" id="' + tempid + '" value=""/><\/div>');

                            }
                        });

                    }

                });

                $('input[name=chkAssaign]').attr('checked', false);

                len = 0;
                $('input[type=text]').each(function() {
                    len++;
                    $(this).val(len);
                });
            }

            function restrict2SelectCheckbox(name, value, id) {
                var checkBoxlength = $("input[name=" + name + "]:checked").length;

                if (checkBoxlength > 20) {
                    var str = "You cannot " + value + " more than 20 post";
                    alert(str);
                    document.getElementById(id).checked = false;
                }
            }
            function numOrdA(a, b) {
                return (a - b);
            }
            function numOrdD(a, b) {
                return (b - a);
            }
            function getCheckOrder(obj) {
                if (obj.checked == true) {
                    var objCurr = obj.value;
                    var divId = $("#" + objCurr + " input:first").next();
                    var txtboxid = divId.attr("id");

                    var tempVal = $("#hidPageno").val();
                    document.getElementById(txtboxid).value = tempVal;
                    tempVal = parseInt(tempVal) + 1;
                    $("#hidPageno").val(tempVal);
                }
            }
        </script>
    </head>
    <body>
        <div class="container-fluid">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <div class="row">
                        <div class="col-lg-12">
                            Section Name : ${sectionName}
                            <input type="hidden" name="hidPageno" value="0" id="hidPageno">
                            <input type="hidden" name="hidsecId" id="hidsecId" value="${sectionId}"/>
                        </div>
                    </div>
                </div>
                <div class="panel-body">
                    <div> <b>  Available Post</b> </div>
                    <div id="availemp" style="border:1px solid #5095CE;height:235px;overflow:auto;text-align:left;padding:10px;">
                        <c:forEach items="${availableEmpList}" var="availableEmp" varStatus="listCount">
                            <div style="padding:5px;" id="${availableEmp.value}">
                                <input type="checkbox" value="${availableEmp.value}" id="${listCount.index}" name="chkAvail" onclick="restrict2SelectCheckbox('chkAvail', 'add', '${listCount.index}');
                                        getCheckOrder(this)"/> ${availableEmp.label}
                                <input type="hidden" name="temp" id="temp${listCount.index}" value=""/>
                            </div>
                        </c:forEach>
                    </div>
                    <div style="padding-top:5px">                        
                        <span style="padding-left:20px">
                            <button type="button" class="btn btn-default" onclick="removeDivdata()">Up</button>
                            <button type="button" class="btn btn-default" onclick="addDivdata()">Down</button>                                                    
                        </span>
                    </div> 
                    <div> <b>  Assigned Post </b> </div>
                    <div id="assigned" style="border:1px solid #5095CE;height:235px;overflow:auto;text-align:left;padding:10px;" class="upDownDiv">
                        <c:forEach items="${assignEmpList}" var="assignEmp" varStatus="listCount">
                            <div style="padding:5px;" id="${assignEmp.spc}">
                                <input type="checkbox" value="${assignEmp.spc}" id="${listCount.index}" name="chkAssaign" onclick="restrict2SelectCheckbox('chkAssaign','remove','${listCount.index}')"/> ${assignEmp.spn}
                            </div>
                        </c:forEach>
                    </div>
                </div>
                <form action="billSectionAction.htm" method="post">
                    <div class="panel-footer">                    
                        <button type="submit" class="btn btn-default">Back</button>                    
                    </div>
                </form>
            </div>
        </div>
    </body>
</html>
