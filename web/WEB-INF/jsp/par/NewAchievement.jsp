<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%
    String downloadlink = "";
    int attid = 0;
    String fiscalyear = "";
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Human Resources Management System, Government of Odisha</title>

        <link rel="stylesheet" type="text/css" href="css/hrmis.css" />
        <link rel="stylesheet" type="text/css" href="resources/css/themes/default/easyui.css"/>
        <link rel="stylesheet" type="text/css" href="resources/css/themes/icon.css"/>

        <script type="text/javascript" src="js/jquery.min-1.9.1.js"></script>
        <script type="text/javascript" src="js/jquery.easyui.min.js"></script>
        <script type="text/javascript">
            $(document).ready(function(){
               if($('#slno').val() == 0){
                   //alert("0 is present");
                   $('#slno').val('');
                   //alert("slno value is: "+$('#slno').val());
               }
               
               if ($('#attachid').val() > 0) {
                    $('#filelabel').show();
                    $('#upload').hide();
                }else{
                    $('#filelabel').hide();
                    $('#upload').show();
                }
               
            });
            function delAttach(obj) {
                if (confirm("Are you sure to Delete?")) {
                    var attachmentid = obj.id;
                    var dataString = 'achId=' + $('#hidpacid').val() + '&attachmentid=' + attachmentid + '&fiscalyr=' + $('#fiscalyear').val();
                    //alert("dataString is: " + dataString);
                    $.ajax({
                        type: "POST",
                        url: 'DeleteAchievementAttachment.htm',
                        data: dataString,
                        dataType: "json"
                    }).done(function(serverResponse) {
                        $.messager.alert(serverResponse.msgType, serverResponse.msg);
                        $('#filelabel').hide();
                        $('#upload').show();
                    });
                }
            }
            function applySave(){
                var num = /^[0-9]+$/;
                var slno = $("#slno");
                var task = $("#task");
                var target = $("#target");
                var achievement = $("#achievement");
                var percentageofAchievement = $("#achievementpercent");
                
                if (slno.val() == "")
                {
                    alert("Please Enter Sl No.");
                    slno.focus();
                    return false;
                }else{
                    if(!slno.val().match(num)){
                        alert("Enter only numbers for Sl No");
                        slno.focus();
                        return false;
                    }
                }
                if (task.val() == "")
                {
                    alert("Please Enter Task to be Completed.");
                    task.focus();
                    return false;
                }
                if (target.val() == "")
                {
                    alert("Please Enter Target.");
                    target.focus();
                    return false;
                }
                if (achievement.val() == "")
                {
                    alert("Please Enter Achievement.");
                    achievement.focus();
                    return false;
                }
                /*if (percentageofAchievement.value == "")
                {
                    alert("Please Enter Percentage of Achievement.");
                    percentageofAchievement.focus();
                    return false;
                }else{*/
                if(percentageofAchievement.val() != ""){
                    if(!percentageofAchievement.val().match(num)){
                        alert("Enter only numbers for Percentage");
                        percentageofAchievement.focus();
                        return false;
                    }
                }
            }
        </script>
    </head>
    <body>
        <form action="addPAR.htm" method="POST" commandName="parAchievement" enctype="multipart/form-data">
            <input type="hidden" name="hidparid" value="${parMastForm.parid}"/>
            <input type="hidden" name="hidpacid" id="hidpacid" value="${parAchievement.pacid}"/>
            <input type="hidden" id="attachid" value="${parAchievement.hidattchid}"/>
            <input type="hidden" name="fiscalyear" id="fiscalyear" value='${parMastForm.fiscalyear}'/>
            <input type="hidden" name="hidparfrmdt" id="hidparfrmdt" value='${parfrmdt}'/>
            <input type="hidden" name="hidpartodt" id="hidpartodt" value='${partodt}'/>
            <div align="center" style="margin-top:5px;margin-bottom:10px;">
                <div class="easyui-panel" align="center">
                    <table border="0" width="90%" cellspacing="0" style="font-size:14px; font-family:Verdana;">
                        <tbody>
                            <tr style="height:40px">
                                <td width="90%" align="left">
                                    Achievement Details
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
            <div align="center">
                <div class="easyui-panel" title="Achievement">
                    <table style="width:100%;height:50px;" border="0" cellpadding="0" cellspacing="0">     
                        <tr style="height:40px">
                            <td width="5%" align="center">1.</td>
                            <td width="15%" align="left">Set Sl No</td>
                            <td width="80%" align="left">
                                <input type="text" name="slno" id="slno" class="easyui-textbox" style="width:40%;border:1px solid #000000;" value="${parAchievement.slno}"/>
                            </td>
                        </tr>
                        <tr style="height:40px">
                            <td align="center">2.</td>
                            <td align="left">Task</td>
                            <td align="left">
                                <input type="text" name="task" id="task" class="easyui-textbox" style="width:40%;border:1px solid #000000;" value="${parAchievement.task}"/>
                            </td>
                        </tr>
                        <tr style="height:40px">
                            <td align="center">3.</td>
                            <td align="left">Target</td>
                            <td align="left">
                                <input type="text" name="target" id="target" class="easyui-textbox" style="width:40%;border:1px solid #000000;" value="${parAchievement.target}"/>
                            </td>
                        </tr>
                        <tr style="height:40px">
                            <td align="center">4.</td>
                            <td align="left">Achievement</td>
                            <td align="left">
                                <input type="text" name="achievement" id="achievement" class="easyui-textbox" style="width:40%;border:1px solid #000000;" value="${parAchievement.achievement}"/>
                            </td>
                        </tr>
                        <tr style="height:60px">
                            <td align="center">5.</td>
                            <td align="left">Percentage(%) of Achievement</td>
                            <td align="left">
                                <input type="text" name="achievementpercent" id="achievementpercent" class="easyui-textbox" style="width:40%;border:1px solid #000000;" value="${parAchievement.achievementpercent}"/>
                            </td>
                        </tr>
                        <tr style="height:60px">
                            <td align="center">6.</td>
                            <td align="left">Percentage(Qualitative) of Achievement</td>
                            <td align="left">
                                <input type="text" name="achievementpersqualitative" id="achievementpersqualitative" class="easyui-textbox" style="width:40%;border:1px solid #000000;" value="${parAchievement.achievementpersqualitative}"/>        
                            </td>
                        </tr>
                        <tr style="height:60px">
                            <td align="center">6.</td>
                            <td align="left">Upload Necessary Document</td>
                            <td align="left">
                                <span id="filelabel">
                                    <%--<c:if test="${parAchievement.hidattchid != null && parAchievement.hidattchid != ''}">--%>
                                    <c:if test="${not empty parMastForm.fiscalyear}">
                                        <c:set var="fsyr" value="${parMastForm.fiscalyear}"/>
                                        <%
                                            fiscalyear = (String)pageContext.getAttribute("fsyr");
                                        %>
                                    </c:if>
                                    <c:if test="${not empty parAchievement.hidattchid}">
                                        <c:set var="attid" value="${parAchievement.hidattchid}"/>
                                        <%
                                            attid = Integer.parseInt(pageContext.getAttribute("attid") + "");
                                        %>
                                    </c:if>
                                    <%
                                        downloadlink = "DownloadAchievementAttachment.htm?attId="+attid+"&fiscalyr="+fiscalyear;
                                    %>
                                    <a href='<%=downloadlink%>' style="text-decoration:none;">
                                        <c:out value="${parAchievement.hidattchfilename}"/>
                                    </a>&nbsp;&nbsp;&nbsp;&nbsp;
                                    <a href="javascript:void(0)" style="text-decoration:none;">
                                        <img src="images/Delete-icon.png" id='<c:out value="${parAchievement.hidattchid}"/>' onclick="delAttach(this)"/>
                                    </a>
                                    <%--</c:if>--%>
                                </span>
                                <span id="upload">
                                    <%--<c:if test="${parAchievement.hidattchid == null || parAchievement.hidattchid == ''}">--%>
                                    <input type="file" name="attchfile" id="attchfile" style="width:40%;border:1px solid #000000;"/>
                                    <%--</c:if>--%>
                                </span>
                            </td>
                        </tr>
                    </table>
                </div>
            </div>
            <div align="center">
                <div class="easyui-panel">	
                    <table width="100%" cellpadding="0" cellspacing="0">
                        <tr style="height: 40px">
                            <td width="100%" align="left">
                                <span style="padding-left:10px;">
                                    <input type="hidden" name="mode" value="achievement"/>
                                    <input type="submit" name="newPar" value="Cancel" class="easyui-linkbutton"/>
                                    <input type="submit" name="newPar" value="Save" class="easyui-linkbutton" onClick="return applySave();"/>  
                                </span>
                            </td>
                        </tr>
                    </table>
                </div>
            </div>
        </form>
    </body>
</html>
