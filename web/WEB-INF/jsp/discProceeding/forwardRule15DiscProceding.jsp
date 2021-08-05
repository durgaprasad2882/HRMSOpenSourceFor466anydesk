
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%
    String contextPath = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + contextPath + "/";
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>:HRMS:</title>
        <link rel="stylesheet" type="text/css" href="css/hrmis.css"/>
        <link rel="stylesheet" type="text/css" href="resources/css/themes/default/easyui.css"/>
        <link rel="stylesheet" type="text/css" href="resources/css/themes/icon.css"/>
        <script type="text/javascript" src="js/jquery.min-1.9.1.js"></script>
        <script type="text/javascript" src="js/jquery.easyui.min.js"></script>
        <script type="text/javascript" src="js/ckeditor/ckeditor.js"></script>

        <style type="text/css">
            .star{
                color:#FF0000;
                font-size:15px;
            }
        </style>
        <script type="text/javascript">
            $(document).ready(function() {

                $('#deptDialog').dialog('close');
                $('#officeDialog').dialog('close');
                $('#postDialog').dialog('close');
                $('#deptname').textbox({
                    onClickButton: function() {
                        $('#deptDialog').dialog('open');
                    }
                });

                $('#officename').textbox({
                    onClickButton: function() {
                        deptcode = $('#hidDeptCode').val();
                        if (deptcode == '') {
                            alert('Please select Department');
                        } else {
                            $('#officeDialog').dialog('open');
                            $('#officedg').datagrid({
                                url: "getOfficeListJSON.htm?deptcode=" + deptcode
                            });
                        }
                    }
                });

                $('#postname').textbox({
                    onClickButton: function() {
                        offcode = $('#hidOffCode').val();
                        if (offcode == '') {
                            alert('Please select Office');
                        } else {
                            $('#postDialog').dialog('open');
                            $('#postdg').datagrid({
                                url: "getRule15PostListJSON.htm?offcode=" + offcode
                            });
                        }
                    }
                });
            });

            function getPost() {
                var deptCode = $('#hidDeptCode').val();
                var offCode = $('#hidOffCode').val();
                var offName = $('#officename').textbox('getValue');
                var spc = $('#hidPostCode').val();

                var spn = $('#postname').textbox('getValue');
                if (spc == '') {
                    alert('please select Post.');
                } else {
                    window.opener.document.getElementById('office').innerHTML = offName;
                    window.opener.document.getElementById('hidOffice').value = offCode;
                    window.opener.document.getElementById('post').innerHTML = spn;
                    window.opener.document.getElementById('hidspc').value = spc;
                    window.close();
                }
            }

            function radioFuncDept(val, row) {
                var data = row.deptCode + ":" + row.deptName;
                return "<input type='radio' name='rddeptid' id='rddeptid' value='" + data + "' onclick='SelectDepartment(\"" + data + "\")'/>";
            }

            function radioFuncOffice(val, row) {
                var data = row.value + ":" + row.label;
                return "<input type='radio' name='rdoffcode' id='rdoffcode' value='" + data + "' onclick='SelectOffice(\"" + data + "\")'/>";
            }

            function radioFuncPost(val, row) {
                var data = row.value + ":" + row.label;
                data = data.replace("'", "&rsquo;");
                return "<input type='radio' name='rdopostcode' id='rdopostcode' value='" + data + "' onclick='SelectPost(\"" + data + "\")'/>";
            }

            function SelectDepartment(data) {

                var radspl = data.split(':');
                var deptcode = radspl[0];
                var deptname = radspl[1];
                $('#deptDialog').dialog('close');
                $('#deptname').textbox('setValue', deptname);
                $('#hidDeptCode').val(deptcode);
            }

            function SelectOffice(data) {

                var radspl = data.split(':');
                var offcode = radspl[0];
                var offname = radspl[1];
                $('#officeDialog').dialog('close');
                $('#officename').textbox('setValue', offname);
                $('#hidOffCode').val(offcode);
            }

            function SelectPost(data) {

                var radspl = data.split(':');
                var spccode = radspl[0];
                var spn = radspl[1];
                $('#postDialog').dialog('close');
                $('#postname').textbox('setValue', spn);
                $('#hidPostCode').val(spccode);
            }

            function getEmployee() {

                var deptName = $('#deptname').val();
                var offName = $('#officename').val();
                var postName = $('#postname').val();
                if (deptName == '') {
                    alert("Please select a Department");
                    return false;
                }
                if (offName == '') {
                    alert("Please select an Office");
                    return false;
                }
                if (postName == '') {
                    alert("Please select a Post");
                    return false;
                }

                var postCode = $('#hidPostCode').val();
                if (postCode != '') {
                    $('#empListDg').datagrid('load', {
                        deptCode: $('#hidDeptCode').val(),
                        offCode: $('#hidOffCode').val(),
                        pCode: $('#hidPostCode').val()
                    });
                } else {
                    alert("Select a Post");
                    return false;
                }



            }
            function forwardDP() {
                var row = $('#empListDg').datagrid('getSelected');
                if (row) {
                    $('#hidForwardHrmsId').val(row.doHrmsId);
                } else {
                    alert("Select an Employee");
                    return false;
                }
            }

            function radioFuncEmp(val, row) {
                var data = row.doHrmsId;
                return "<input type='radio' name='rdEmpid' id='rdEmpid' value='" + data + "'/>";
            }

            

        </script>
    </head>
    <body>
        <form action="forwardDiscProced.htm" method="POST" commandName="Rule15ChargeBean">
            <input type="hidden" name="hidFowardDaId" id="hidFowardDaId" value='${hidDaid}'/>
            <input type="hidden" name="hidForwardHrmsId" id="hidForwardHrmsId"/>

            <div id="deptDialog" class="easyui-dialog" title="Select Department" style="width:80%;" data-options="iconCls:'icon-save',modal:true">
                <table id="deptdg" class="easyui-datagrid" title="..." style="width:500px;"
                       data-options="rownumbers:true,singleSelect:true,url:'getDeptListJSON.htm',method:'post'">
                    <thead>
                        <tr>
                            <th data-options="field:'deptCode',formatter:radioFuncDept"></th>
                            <th data-options="field:'deptName',width:420">Department Name</th>
                        </tr>
                    </thead>
                </table>
            </div>

            <div id="officeDialog" class="easyui-dialog" title="Select Office" style="width:80%;" data-options="iconCls:'icon-save',modal:true">
                <table id="officedg" class="easyui-datagrid" nowrap="false" style="width:600px;"
                       data-options="rownumbers:true,singleSelect:true,method:'post'">
                    <thead>
                        <tr>
                            <th data-options="field:'value',formatter:radioFuncOffice"></th>
                            <th data-options="field:'label',width:500">Office Name</th>
                        </tr>
                    </thead>
                </table>
            </div>

            <div id="postDialog" class="easyui-dialog" title="Select Post" style="width:80%;" data-options="iconCls:'icon-save',modal:true">
                <table id="postdg" class="easyui-datagrid" nowrap="false" style="width:600px;"
                       data-options="rownumbers:true,singleSelect:true,method:'post'">
                    <thead>
                        <tr>
                            <th data-options="field:'value',formatter:radioFuncPost"></th>
                            <th data-options="field:'label',width:500">Post</th>
                        </tr>
                    </thead>
                </table>    
            </div>

            <div align="center" width="99%" style="margin-top:5px;margin-bottom:10px;">
                <div align="center" class="easyui-panel" title="Forward Departmental Proceeding" width="99%">
                    <table border="0" cellspacing="0" cellpadding="0" style="width:100%">
                        <tr style="height:40px">
                            <td width="20%" align="center">Department</td>
                            <td width="70%">
                                <input name="hidDeptCode" id="hidDeptCode" type="hidden"/> 
                                <input id="deptname" class="easyui-textbox" data-options="editable:false,buttonText:'Search',buttonAlign:'right',buttonIcon:'icon-search',required:true,prompt:'Click on search icon to select Department...'" style="width:100%;height:32px;">
                            </td>
                            <td width="10%">&nbsp;</td>
                        </tr>
                        <tr style="height:40px" align="center">
                            <td>Office</td>
                            <td align="left">
                                <input name="hidOffCode" id="hidOffCode" type="hidden"/>
                                <input id="officename" class="easyui-textbox" data-options="editable:false,buttonText:'Search',buttonAlign:'right',buttonIcon:'icon-search',required:true,prompt:'Click on search icon to select Office...'" style="width:100%;height:32px;">
                            </td>
                            <td>&nbsp;</td>
                        </tr>
                        <tr style="height:40px" align="center">
                            <td>Post</td>
                            <td align="left">
                                <input type="hidden" name="hidPostCode" id="hidPostCode"/> 
                                <input id="postname" class="easyui-textbox" data-options="editable:false,buttonText:'Search',buttonAlign:'right',buttonIcon:'icon-search',required:true,prompt:'Click on search icon to select Post...'" style="width:100%;height:32px;">
                                
                            </td>
                            <td>
                                <button type="button" onClick="getEmployee()">Ok</button>
                            </td>
                        </tr>
                    </table>
                </div>
            </div>

            <div style="width:99%;margin: 1px auto;"  id="tbl-container">
                <table id="empListDg" class="easyui-datagrid" style="width:99%;height:250px;" title="AUTHORITY LIST" rownumbers="true" 
                       url="GetDpForwardEmpJSON.htm" singleSelect="true" collapsible="false" toolbar="#toolbar">
                    <thead>
                        <tr> 
                            <th data-options="field:'doHrmsId',formatter:radioFuncEmp"></th>
                            <th data-options="field:'doEmpName',width:230"><span style="font-weight: bold;">AUTHORITY NAME</span></th>
                        </tr> 
                    </thead>
                </table>
            </div>

            <div align="center" width="99%" style="margin-top:5px;margin-bottom:10px;">
                <div align="center" class="easyui-panel" width="99%">
                    <table border="0" width="100%">
                        <tr>
                            <td width="50%" align="left" style="padding-left:20px;">
                                <input type="submit" name="rule15forwardDp" value="Back" class="easyui-linkbutton" data-options="iconCls:'icon-remove'"/>
                            </td>
                            <td width="50%" align="right" style="padding-right:20px;">
                                <input type="submit" name="rule15forwardDp" value="Forward" onclick="return forwardDP()"
                                       class="easyui-linkbutton" data-options="iconCls:'icon-add'"/>
                            </td>
                        </tr>
                    </table>
                </div>
            </div>

        </form>
    </body>
</html>
