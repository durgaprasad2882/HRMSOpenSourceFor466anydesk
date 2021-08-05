<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<% int i = 1;
%>
<%
    String contextPath = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + contextPath + "/";

    System.out.println(basePath);
%>
<html>
    <head>
        <base href="<%=basePath%>"></base>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Employee Profile</title>
        <link rel="stylesheet" type="text/css" href="resources/css/themes/default/easyui.css">
        <link rel="stylesheet" type="text/css" href="resources/css/themes/icon.css">
        <link rel="stylesheet" type="text/css" href="resources/css/demo.css">
        <script type="text/javascript" src="js/common.js"></script>
        <script type="text/javascript" src="js/jquery.min-1.9.1.js"></script>
        <script type="text/javascript" src="js/jquery.min.js"></script>
        <script type="text/javascript" src="js/jquery.easyui.min.js"></script>
        <script type="text/javascript" src="js/jquery.datetimepicker.js"></script>

        <script language="javascript" type="text/javascript">
            function saveProfileDetails() {
                alert("satya1");
                $('#fm').form('submit', {
                    url: 'saveProfileAction.htm',
                    success: function (result) {
                        var result = eval('(' + result + ')');
                        alert(result);
                        if (result.errorMsg) {
                            $.messager.show({
                                title: 'Error',
                                msg: result.errorMsg
                            });
                        } 
                    }

                });

            }
            /* function getSupDate(year12,supanndate,dob)
             {      
             alert(dob);
             var empid=document.getElementById("txtEid").value;
             var url="";	
             url="<bean:message key="ajaxPath"/>/getsuperannuation";
             var param = "radyear="+year12+"&empid="+empid;
             var loader = dhtmlxAjax.getSync(url+"?"+param);
             document.getElementById(supanndate).value = loader.xmlDoc.responseText;
             }*/

        </script>
    </head>

    <body>
         <form:form name="myForm"  action="saveProfileAction.htm" method="POST" commandName="emp">
        <div style="margin:5px 0;"></div>
        <div class="easyui-tabs" data-options="tools:'#tab-tools'" style="width:auto;height:600px">
            <div title="Personal Info" data-options="href:'personalinfo.htm'" style="padding:10px;">
                 

            </div>
            <div title="Address" data-options="href:'address.htm'" style="padding:10px;">

            </div>

            <div title="Identity" data-options="href:'identity.htm'"  style="padding:10px;">

            </div>
            <div title="Education" data-options="href:'education.htm'" style="padding:10px;">

            </div>
            <div title="Family" data-options="href:'family.htm'" style="padding:10px;">

            </div>
            <div title="Language" data-options="href:'language.htm'" style="padding:10px;">

            </div>

        </div>
        <div>


        </div>
         </form:form>
    </body>
</html>
