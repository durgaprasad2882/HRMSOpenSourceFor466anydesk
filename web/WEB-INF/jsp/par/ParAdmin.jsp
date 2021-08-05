<%-- 
    Document   : ParAdmin
    Created on : Jun 28, 2017, 2:55:47 PM
    Author     : DurgaPrasad
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" type="text/css" href="resources/css/themes/default/easyui.css">
        <script type="text/javascript" src="js/jquery.min-1.9.1.js"></script>        
        <script type="text/javascript" src="js/jquery.easyui.min.js"></script>
    </head>
    <body>
        <div id="parpanel" class="easyui-panel" title="PAR" style="width:100%;height:700px;padding:5px;">
            <table id="tt" class="easyui-datagrid" style="width:100%;height:365px"
                   url="getSearchPARList.htm?fiscalyear=2015-16" iconCls="icon-search" toolbar="#tb" singleSelect="true"
                   title="Performance Appraisal Data" rownumbers="true" pagination="true">
                <thead>
                    <tr>
                        <th data-options="field:'has_seen',hidden:true"></th>
                        <th field="empId" width="8%">Employee Id</th> 
                        <th field="gpfno" width="8%">GPF No</th> 
                        <th field="empName" width="15%">Employee Name</th>                    
                        <th field="postName" width="20%">Designation</th>
                        <th field="dob" width="8%">Date of Birth</th>
                        <th field="groupName" width="5%" align="center">Group</th>
                        <th field="cadreName" width="10%">Cadre</th>
                        <th field="currentoffice" width="10%">Current Office</th>
                        <th field="mobile" width="10%">Mobile</th>
                        <th field="parstatus" width="20%">Status</th>
                        <th data-options="field:'spc',hidden:true">Status</th>
                    </tr>
                </thead>
            </table>
            <div id="p" class="easyui-panel" title="PAR Detail" style="width:100%;height:300px;background:#fafafa;">
                <p>Par Detail panel content.</p>            
            </div>
        </div>
    </body>
</html>
