<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Human Resources Management System, Government of Odisha</title>

        <link rel="stylesheet" type="text/css" href="resources/css/themes/default/easyui.css">
        <link rel="stylesheet" type="text/css" href="resources/css/themes/icon.css">

        <!-- LAYOUT v 1.3.0 -->
        <script type="text/javascript" src="js/jquery.min-1.9.1.js"></script>
        <script type="text/javascript" src="js/jquery.easyui.min.js"></script>

        <script type="text/javascript">
            $(document).ready(function () {
                $('#deptname').combobox({
                    onSelect: function (record) {
                        $('#officename').combobox('clear');
                        $('#post').combobox('clear');

                        var url = 'getOfficeListJSON.htm?deptcode=' + record.deptCode;
                        $('#officename').combobox('reload', url);
                    }
                });

                $('#officename').combobox({
                    onSelect: function (record) {
                        $('#post').combobox('clear');
                        var url = 'getPostListLoanworkflowJSON.htm?offcode=' + record.offCode;
                        $('#post').combobox('reload', url);
                    }
                });
            });

            function getPost() {
                var deptCode = $('#deptname').combobox('getValue');
                var offCode = $('#officename').combobox('getValue');
                var offName = $('#officename').combobox('getText');
                var spc_hrmsid = $('#post').combobox('getValue');
               // alert(spc_hrmsid);
                spc_hrmsid=spc_hrmsid.split("|");
                var spc=spc_hrmsid[0];
                var spc_hrmsid=spc_hrmsid[1];
                
                var spn = $('#post').combobox('getText');

                parent.SelectSpn(offCode, spc, offName, spn,spc_hrmsid);
            }
            
            function formatItem(row) {
                var s = '<span style="font-weight:bold">' + row.label + '</span><br/>' +
                        '<span style="color:#228B22">' + row.desc + '</span>';
                return s;
            }
        </script>
    </head>
    <body>
        <div align="center" style="margin-top:5px;margin-bottom:10px;">
            <div align="center">
                <table border="0" cellspacing="0" cellpadding="0" style="width:100%">
                    <tr style="height:40px">
                        <td width="20%" align="center">
                            Department
                        </td>
                        <td width="70%">
                            <input name="hidDeptCode" id="deptname" class="easyui-combobox" style="width:500px;" data-options="valueField:'deptCode',textField:'deptName',url:'getDeptListJSON.htm'" />
                        </td>
                        <td width="10%">
                            &nbsp;
                        </td>
                    </tr>
                    <tr style="height:40px" align="center">
                        <td>
                            Office
                        </td>
                        <td align="left">
                            <input name="hidOffCode" id="officename" class="easyui-combobox" style="width:500px;" data-options="valueField:'offCode',textField:'offName'"/>
                        </td>
                        <td>
                            &nbsp;
                        </td>
                    </tr>
                </table>
                <div style="padding-left: 36px;margin-bottom:20px">
                    <label class="label-top">Post :</label>
                    <input class="easyui-combobox" id="post" name="post" style="width:50%;" data-options="
                           valueField: 'value',
                           textField: 'label',
                           panelWidth: 300,
                           panelHeight: 'auto',
                           formatter: formatItem">
                </div>&nbsp;<button type="button" onclick="getPost()">Ok</button>
            </div>
        </div>
    </body>
</html>
