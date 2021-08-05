<%-- 
    Document   : SubmitIncrementProposal
    Created on : 11 Jul, 2016, 11:36:43 AM
    Author     : Surendra
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        <link rel="stylesheet" type="text/css" href="resources/css/themes/default/easyui.css">
        <link rel="stylesheet" type="text/css" href="resources/css/themes/icon.css">

        <!-- LAYOUT v 1.3.0 -->
        <script type="text/javascript" src="js/jquery.min-1.9.1.js"></script>
        <script type="text/javascript" src="js/jquery.easyui.min.js"></script>

        <script type="text/javascript">
            $(function () {
                /*Call After Load*/

                $('#dept').combobox('clear');
                $('#office').combobox('clear');
                $('#post').combobox('clear');
                $('#auth').combobox('clear');

                $('#dept').combobox({url: 'getDeptListJSON.htm',
                    onSelect: function(record) {
                        $('#office').combobox('clear');
                        $('#post').combobox('clear');
                        $('#auth').combobox('clear');
                        var url = 'getOfficeListJSON.htm?deptcode=' + record.deptCode;
                        $('#office').combobox('reload', url);
                    }
                });
                $('#office').combobox({
                    onSelect: function(record) {
                        $('#post').combobox('clear');
                        $('#auth').combobox('clear');
                        var url = 'getPostCodeListJSON.htm?offcode=' + record.offCode;
                        $('#post').combobox('reload', url);
                    }
                });
                $('#post').combobox({
                    onSelect: function(record) {
                        $('#auth').combobox('clear');
                        var url = 'getPostCodeWiseEmployeeListSPCJSON.htm?postcode=' + record.value;
                        $('#auth').combobox('reload', url);
                    }
                });
            });
            
            function submitToAuthority(){
                auth=$('#post').combobox('getValue');
                if(auth==''){
                    alert('Please Select Authority.');
                    return false;
                }
            }

            $(document).ready(function () {
                //$('#txtdeptname').combobox('setValues', ['11','GENERAL ADMINISTARTION DEPARTMENT']);
                //var deptcode = $('#dept').combobox('getValue');
            });
        </script>
    </head>
    <body>
        <form action="submitProposalTask.htm" commandName="IncrementProposal" method="post">
            <input type="hidden" name="proposalId" value="${incrementForm.proposalId}"/>
            <div id="p" class="easyui-panel" title="Increment" style="width:100%;height:100%;padding:10px;">

                <div style="margin-bottom:28px">
                    <label class="label-top">Department :</label>
                    <input class="easyui-combobox" id="dept" style="width:50%;height:26px;" data-options="valueField:'deptCode',textField:'deptName',editable:false"/>
                </div>
                <div style="padding-left: 28px;margin-bottom:20px">
                    <label class="label-top">Office :</label>
                    <input class="easyui-combobox" id="office" style="width:50%;height:26px;" data-options="valueField:'offCode',textField:'offName',editable:false"/>
                </div>
                <div style="padding-left: 28px;margin-bottom:20px">
                    <label class="label-top">Post :</label>
                    <input class="easyui-combobox" id="post" style="width:50%;height:26px;" data-options="valueField:'value',textField:'label',editable:false"/>
                </div>
                <div style="padding-left: 28px;margin-bottom:20px">
                    <label class="label-top">Authority :</label>
                    <input class="easyui-combobox" id="auth" name="authspc" style="width:50%;height:26px;" data-options="valueField:'value',textField:'label',editable:false"/>
                </div>
            </div>

            <div id="dlg-buttons">
                <table cellpadding="0" cellspacing="0" style="width:100%">
                    <tr>
                        <td style="text-align:left">
                            <a href="displayProposalListpage.htm" class="easyui-linkbutton" iconCls="icon-cancel">Return</a>
                            <button type="submit" class="easyui-linkbutton" onclick="return submitToAuthority()">Send</button>
                        </td>
                    </tr>
                </table>
            </div>
        </form>     

    </body>
</html>
