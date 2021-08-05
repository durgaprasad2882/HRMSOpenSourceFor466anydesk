<%-- 
    Document   : OfficeEmployeeList
    Created on : 16 Feb, 2017, 3:53:03 PM
    Author     : Prashant
--%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<%@ page contentType="text/html;charset=UTF-8"%>

<%
    String contextPath = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + contextPath + "/";
%>
<html>
    <head>
        <base href="<%=basePath%>"></base>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <title>PROCEEDINGS</title>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link rel="stylesheet" type="text/css" href="resources/css/themes/default/easyui.css">
        <link rel="stylesheet" type="text/css" href="resources/css/themes/icon.css">
        <!-- LAYOUT v 1.3.0 -->
        <script type="text/javascript" src="js/jquery.min-1.9.1.js"></script>
        <script type="text/javascript" src="js/jquery.easyui.min.js"></script>
        
        <script type="text/javascript">
            function viewDiscProced(val,row){
                var daId = row.daid;
                var viewDP = 'viewDiscProced.htm?DAID='+daId;
                return "<a href='"+viewDP+"' target='_blank'>View</a>"; 
            }
            
            function forwardDiscProced(daid){
                var url="forwardDiscProced.htm?DAID="+daid; 
                self.location = url;
            }
            
            function forwardDP(val,row){
                var daId= row.daid;
                var dpSts= row.dpStatus;
                if(dpSts == 55){
                    return "<a href='javascript:void(0)' onclick='forwardDiscProced(\""+daId+"\")'>Initiated</a>";
                }else if(dpSts ==56){
                    return "Fordward";
                }else if(dpSts ==57){
                    return "More Information Required";
                }else if(dpSts ==58){
                    return "Inquiring Officer Appointed";
                }else if(dpSts ==59){
                    return "Completed";
                }
            }
            
            
        </script> 
    </head>   
    <body >
        <form:form  action="DiscProcedingList.htm" method="POST" commandName="DiscProceedingBean">
            
            <div style="width:100%;height:85%;" id="tbl-container">
                <table id="empListDg" class="easyui-datagrid" style="width:100%;height:85%;" title="PROCEEDING LIST" rownumbers="true"
                        pagination="true" toolbar="#toolbar" nowrap="false" data-options="singleSelect:true" url="GetDoListJSON.htm">
                    <thead>
                        <tr> 
                            <th data-options="field:'daid',hidden:true"></th>
                            <th data-options="field:'dpStatus',hidden:true"></th>
                            <th data-options="field:'doEmpNameAndDesg',width:270"><span style="font-weight: bold;">EMPLOYEE NAME / DESIGNATION</span></th>
                            <th data-options="field:'disType',width:70" align="center"><span style="font-weight: bold;">TYPE</span></th>
                            <th data-options="field:'underRule',width:90" align="center"><span style="font-weight: bold;">UNDER RULE</span></th>
                            
                            <th data-options="field:'forwardNameAndDegn',width:250"><span style="font-weight: bold;">FORWARD TO</span></th>
                            <th data-options="field:'forwardDate',width:110" align="center"><span style="font-weight: bold;">FORWARD DATE</span></th>
                            
                            <th data-options="field:'appIo',width:80,formatter:viewDiscProced" align="center"><span style="font-weight: bold;">VIEW</span></th>
                            <th data-options="field:'appIo1',width:80,formatter:forwardDP" align="center"><span style="font-weight: bold;">STATUS</span></th>
                        </tr> 
                    </thead>
                </table>
                
                <div id="toolbar">
                    <a href="DiscProceding1.htm"  class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-add'">
                        <span style="font-size: 15px;font-weight:bold;">START NEW PROCEEDING</span></a>
                </div>
            </div>
        </form:form>
    </body>
</html>