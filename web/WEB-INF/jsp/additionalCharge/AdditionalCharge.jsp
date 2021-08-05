<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
    <head>
        
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Human Resources Management System, Government of Odisha</title>

        <link rel="stylesheet" type="text/css" href="resources/css/themes/default/easyui.css">
        <link rel="stylesheet" type="text/css" href="resources/css/themes/icon.css">

        <script type="text/javascript" src="js/jquery.min-1.9.1.js"></script>
        <script type="text/javascript" src="js/jquery.easyui.min.js"></script>
        <script type="text/javascript" src="js/common.js"></script>
        <script type='text/javascript'>
             function newDeputation() {
                $('#saveBtn').show();
                $('#dlg').dialog('open');
                $('#deputationDataForm').form('clear');
            }

        </script>    
          </head>
    <body>
         <div id="deputationTool" style="padding:3px">                    
            <a href="javascript:newDeputation();" class="easyui-linkbutton" data-options="iconCls:'icon-add'" plain="true" id="addBtn">Add New</a>
            <a href="javascript:editDeputation();" class="easyui-linkbutton" data-options="iconCls:'icon-edit'" plain="true" id="editBtn">Edit</a>
        </div>
        <div style='clear:both'></div>
        <input type="hidden" id="type"/>
        <table id="deputationdg" class="easyui-datagrid" style="width:100%;height:400px;" title="AdditionalCharge List"
               rownumbers="true" singleSelect="true" url="GetAdditionalChargeListJSON.htm" singleSelect="true" pagination="true" collapsible="false"
               data-options="nowrap:false,fitColumns:false" toolbar="#additionalchargeTool">
            <thead>
                <tr>
                    <th data-options="field:'notId',hidden:true"></th>
                    <th data-options="field:'doe',width:150">Date of Entry</th>
                    <th data-options="field:'notOrdNo',width:150">Notification<br />Order No</th>
                    <th data-options="field:'notOrdDt',width:150">Notification<br />Date</th>
                    <th data-options="field:'notType',width:150">Notification Type</th>
                </tr> 
            </thead>
        </table>
       <div id="dlg" class="easyui-dialog" title="Deputation New/Edit" data-options="iconCls:'icon-save',closed: true,modal: true" style="width:900px;height:600px;padding:10px" buttons="#dlg-buttons">
            <form id="deputationDataForm" method="POST" commandName="additionChargeForm">
                <input type="hidden" name="depId"/>
                <input type="hidden" name="hidNotId"/>
                <input type="hidden" name="hidTransferId"/>
                
                <input type="hidden" name="hidNotifyingDeptCode" id="hidNotifyingDeptCode"/>
                <input type="hidden" name="hidNotifyingOffCode" id="hidNotifyingOffCode"/>
                <input type="hidden" name="hidPostedDeptCode" id="hidPostedDeptCode"/>
                <input type="hidden" name="hidPostedOffCode" id="hidPostedOffCode"/>
                
                <input type="hidden" name="hidSubCadreStatus" id="hidSubCadreStatus"/>
                <input type="hidden" name="hidFieldOffice" id="hidFieldOffice"/>
                <div align="center">
                    <% int i = 1;%>
                    <table style="width:100%;height:50px;" border="0" cellpadding="0" cellspacing="0">
                        <tr style="height:40px;">
                            <td width="5%" align="center"><%=i++%>.</td>
                            <td width="21%">
                                Notification Order No<span style="color: red">*</span>
                            </td>
                            <td width="20%">
                                <input class="easyui-textbox" name="txtNotOrdNo" id="txtNotOrdNo" maxlength="51"/>
                            </td>
                            <td width="5%"><%=i++%>.</td>
                            <td width="19%">
                                Notification Order Date<span style="color: red">*</span>
                            </td>
                            <td width="20%">
                                <input class="easyui-datebox" id="txtNotOrdDt" name="txtNotOrdDt" style="width:70%" data-options="required:true,formatter:myformatter,parser:myparser" editable="false">
                            </td>
                        </tr>
                        <tr style="height:40px;">
                            <td align="center"><%=i++%>.</td>
                            <td>
                                Details of Notifying Authority
                            </td>
                            <td colspan="4">&nbsp;&nbsp;&nbsp;
                                <input type="text" name="notifyingSpn" readonly="true" id="notifyingPostName" size="80"/>
                                <input type="hidden" name="notifyingSpc" id="notifyingSpc"/>
                                <a href="javascript:void(0)" id="change" onclick="changepost('notify')">
                                    <button type="button">Browse</button>
                                </a>
                            </td>
                        </tr>
                        <tr style="height:40px;">
                            <td align="center"><%=i++%>.</td>
                            <td>
                                Details of Posting
                            </td>
                            <td colspan="4">&nbsp;&nbsp;&nbsp;
                                <input type="text" name="postedSpn" readonly="true" id="postedPostName" size="80"/>
                                <input type="hidden" name="postedSpc" id="postedSpc"/>
                                <a href="javascript:void(0)" id="change" onclick="changepost('posted')">
                                    <button type="button">Browse</button>
                                </a>
                            </td>
                        </tr>
                        <tr style="height:40px;">
                            <td>&nbsp;</td>
                            <td>(a) Field Office</td>
                            <td colspan="4">
                                <input name="sltFieldOffice" id="sltFieldOffice" class="easyui-combobox" style="width:60%" data-options="valueField:'value',textField:'label'"/>
                            </td>
                        </tr>
                        <tr style="height:40px;">
                            <td align="center"><%=i++%>.</td>
                            <td>
                                Cadre Status
                            </td>
                            <td colspan="4">
                                <input name="sltCadreStatus" id="sltCadreStatus" class="easyui-combobox" style="width:60%" data-options="valueField:'value',textField:'label',url:'deputationGetCadreStatusListJSON.htm'"/>
                            </td>
                        </tr>
                        <tr style="height:40px;">
                            <td align="center"><%=i++%>.</td>
                            <td>
                                Sub Cadre Status
                            </td>
                            <td colspan="4">
                                <input name="sltSubCadreStatus" id="sltSubCadreStatus" class="easyui-combobox" style="width:60%" data-options="valueField:'value',textField:'label'"/>
                            </td>
                        </tr>
                        <tr style="height:40px;">
                            <td align="center"><%=i++%>.</td>
                            <td>
                                Deputation Period Details
                            </td>
                            <td colspan="4">&nbsp;</td>
                        </tr>
                        <tr style="height:40px;">
                            <td>&nbsp;</td>
                            <td>
                                (a) If Extension of Deputation Period
                            </td>
                            <td colspan="4">
                                <input type="checkbox" name="chkExtnDptnPrd" id="chkExtnDptnPrd" value="Y"/>
                            </td>
                        </tr>
                        <tr style="height:40px;">
                            <td>&nbsp;</td>
                            <td>
                                (b) With Effect From
                            </td>
                            <td>
                                <input class="easyui-datebox" id="txtWEFrmDt" name="txtWEFrmDt" style="width:70%" data-options="required:true,formatter:myformatter,parser:myparser" editable="false">
                            </td>
                            <td>&nbsp;</td>
                            <td>
                                Time
                            </td>
                            <td>
                                <select name="sltWEFrmTime" id="sltWEFrmTime" class="easyui-combobox" style="width:130px;">
                                    <option value="">-Select-</option>
                                    <option value="FN">Fore Noon</option>
                                    <option value="AN">After Noon</option>
                                </select>
                            </td>
                        </tr>
                        <tr style="height:40px;">
                            <td>&nbsp;</td>
                            <td>
                                (b) Till Date
                            </td>
                            <td>
                                <input class="easyui-datebox" id="txtTillDt" name="txtTillDt" style="width:70%" data-options="required:true,formatter:myformatter,parser:myparser" editable="false">
                            </td>
                            <td>&nbsp;</td>
                            <td>
                                Time
                            </td>
                            <td>
                                <select name="sltTillTime" id="sltTillTime" class="easyui-combobox" style="width:130px;">
                                    <option value="">-Select-</option>
                                    <option value="FN">Fore Noon</option>
                                    <option value="AN">After Noon</option>
                                </select>
                            </td>
                        </tr>
                        <tr style="height:40px;">
                            <td align="center"><%=i++%></td>
                            <td>
                                Note(if any)
                            </td>
                            <td colspan="4">
                                <textarea name="note" id="note" style="width: 90%; text-align: left; height: 60px;"></textarea>
                            </td>
                        </tr>
                    </table>
                </div>
            </form>
        </div>
        <div id="dlg-buttons">
            <a href="javascript:void(0)" id="saveBtn" class="easyui-linkbutton c6" iconCls="icon-save" onclick="saveDeputation()" style="width:90px">Save</a>
            <a href="javascript:void(0)" id="delBtn" class="easyui-linkbutton c6" iconCls="icon-remove" onclick="delDeputation()" style="width:90px">Delete</a>
        </div>
        <%-- Start - Content for Child Window --%>
        <div id="winsubstantivepost" class="easyui-window" title="Search" style="width:700px;height:400px;padding:10px 20px" closed="true" buttons="#searchdlg-buttons"
             data-options="iconCls:'icon-search',modal:true">
            <table style="width:100%;">
                <tr>
                    <td width="70%">
                        Select Post
                    </td>
                    <td width="30%">&nbsp;</td>
                </tr>
                <tr>
                    <td>
                        <input class="easyui-combobox" id="sltPost" style="width:600px;" data-options="valueField:'value',textField:'label',url: 'TransferPostListJSON.htm'"/>
                    </td>
                    <td>
                        <button type="button" onclick="SelectPost()">Ok</button>
                    </td>
                </tr>
            </table><br /><br />
            <button type="button" onclick="openAddPostWindow()">Add Post</button>
        </div>
        <div id="winAddPost" class="easyui-window" title="Search" style="width:700px;height:300px;padding:10px 20px" closed="true">
            <table width="100%" cellpadding="0" cellspacing="0">
                <tr style="height: 30px;">
                    <td width="30%">Department:</td>
                    <td width="70%">
                        <input class="easyui-combobox" id="department" name="department" style="width:400px;" data-options="valueField:'deptCode',textField:'deptName',url:'getDeptListJSON.htm'">
                    </td>
                </tr>
                <tr style="height: 30px;">
                    <td>Office</td>
                    <td>
                        <input class="easyui-combobox" id="office" name="office" style="width:400px;" data-options="valueField:'value',textField:'label'">
                    </td>
                </tr>
                <tr style="height: 30px;">
                    <td>Post</td>
                    <td>
                        <input class="easyui-combobox" id="substantivepost" name="substantivepost" style="width:400px;" data-options="valueField:'spc',textField:'postname'">
                    </td>
                </tr>
                <tr style="height: 30px;">
                    <td>&nbsp;</td>
                    <td>
                        <button type="button" onclick="getPost()">Ok</button>
                    </td>
                </tr>
            </table>
        </div>
       
       
        <%-- End - Content for Child Window --%>
    </body>
</html>