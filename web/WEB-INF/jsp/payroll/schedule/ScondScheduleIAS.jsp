<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Second Schedule</title>

        <link rel="stylesheet" type="text/css" href="resources/css/themes/default/easyui.css"/>
        <link rel="stylesheet" type="text/css" href="resources/css/themes/icon.css"/>

        <script type="text/javascript" src="js/jquery.min-1.9.1.js"></script>
        <script type="text/javascript" src="js/jquery.easyui.min.js"></script>
        <script type="text/javascript">
            $(document).ready(function() {
                $('#department').combobox('clear');
                $('#office').combobox('clear');
                $('#post').combobox('clear');

                $('#department').combobox({
                    url: 'getDeptListJSON.htm',
                    onSelect: function(record) {
                        $('#office').combobox('clear');
                        $('#post').combobox('clear');
                        var url = 'getOfficeListJSON.htm?deptcode=' + record.deptCode;
                        $('#office').combobox('reload', url);
                    }
                });
                $('#office').combobox({
                    onSelect: function(record) {
                        $('#post').combobox('clear');
                        var url = 'getPostCodeListJSON.htm?offcode=' + record.offCode;
                        $('#post').combobox('reload', url);
                    }
                });

            });

            function changepost() {
                $('#winsubstantivepost').window('open');
            }

            function getPost() {
                $('#postname2').val($('#post').combobox('getText'));
                $('#hidPostCode2').val($('#post').combobox('getValue'));

                $('#department').combobox('clear');
                $('#office').combobox('clear');
                $('#post').combobox('clear');

                $('#winsubstantivepost').window('close');
            }

            function saveCheck() {
                if ($('input[name=rdForm]:checked').length <= 0) {
                    alert("Please Select one option!");
                    return false;
                }

                if ($('input[name=rdForm]:checked').val() == 1) {
                    
                } else if ($('input[name=rdForm]:checked').val() == 2) {
                    if ($('#postname2').val() == '') {
                        alert("Please Enter Post");
                        return false;
                    }
                    if ($('#payscale2').val() == '') {
                        alert("Please Enter Pay Scale");
                        return false;
                    }
                    if ($('#gp2').val() == '') {
                        alert("Please Enter Grade Pay");
                        return false;
                    }
                }

                if (confirm('Are you sure to submit?')) {
                    return true;
                } else {
                    return false;
                }
            }
        </script>
    </head>
    <body>
        <form action="SecondScheduleForm.htm" method="POST">
            <div align="center" style="font-family: Verdana;font-size: 12px;margin-top:50px;">
                <table border="0" width="100%" cellpadding="0" cellspacing="0">
                    <tr style="height:50px;">
                        <td width="20%">
                            <input type="radio" name="rdForm" value="1"/>
                        </td>
                        <td width="80%">
                            I <u><c:out value="${SecondSchlBean.empname}"/></u> hereby elect the revised pay structure with effect from 1st January,2016. 
                        </td>
                    </tr>
                    <tr style="height:50px;">
                        <td colspan="2">&nbsp;</td>
                    </tr>
                    <tr style="height:50px;">
                        <td>
                            <input type="radio" name="rdForm" value="2"/>
                        </td>
                        <td>
                            I <u><c:out value="${SecondSchlBean.empname}"/></u> hereby elect to continue on Pay Band and Grade Pay on my substantive/officiating post mentioned below unit. 
                        </td>
                    </tr>            

                    <tr style="height:50px;">
                        <td>
                            &emsp;&emsp;*
                        </td>
                        <td>
                            The date of my next increment/the date of my subsequent increment raising my pay to promotion /up gradation to the post of
                            <input type="hidden" name="hidPostCode2" id="hidPostCode2"/>
                            <input type="text" name="postname2" id="postname2" size="50" value="${SecondSchlBean.post}" readonly="true"/>
                            <a href="javascript:void(0)" id="change" onclick="changepost()">
                                <button type="button">Browse</button>
                            </a>
                        </td>
                    </tr>

                    <tr style="height:50px;">
                        <td colspan="2">
                            &emsp;&emsp;Existing Pay Band and Grade Pay
                            <select name="payscale2" id="payscale2" style="width:200px;">
                                <option value="">--Select--</option>
                                <option value="4750-14680" <c:if test="${not empty SecondSchlBean.payscale && SecondSchlBean.payscale == '4750-14680'}"> <c:out value='selected="selected"'/></c:if>>4750-14680</option>
                                <option value="4930-14680" <c:if test="${not empty SecondSchlBean.payscale && SecondSchlBean.payscale == '4930-14680'}"> <c:out value='selected="selected"'/></c:if>>4930-14680</option>
                                <option value="5200-20200" <c:if test="${not empty SecondSchlBean.payscale && SecondSchlBean.payscale == '5200-20200'}"> <c:out value='selected="selected"'/></c:if>>5200-20200</option>
                                <option value="9300-34800" <c:if test="${not empty SecondSchlBean.payscale && SecondSchlBean.payscale == '9300-34800'}"> <c:out value='selected="selected"'/></c:if>>9300-34800</option>
                                <option value="15600-39100" <c:if test="${not empty SecondSchlBean.payscale && SecondSchlBean.payscale == '15600-39100'}"> <c:out value='selected="selected"'/></c:if>>15600-39100</option>
                                <option value="37400-67000" <c:if test="${not empty SecondSchlBean.payscale && SecondSchlBean.payscale == '37400-67000'}"> <c:out value='selected="selected"'/></c:if>>37400-67000</option>
                                <option value="67000-79000" <c:if test="${not empty SecondSchlBean.payscale && SecondSchlBean.payscale == '67000-79000'}"> <c:out value='selected="selected"'/></c:if>>67000-79000</option>
                                <option value="75500-80000" <c:if test="${not empty SecondSchlBean.payscale && SecondSchlBean.payscale == '75500-80000'}"> <c:out value='selected="selected"'/></c:if>>75500-80000</option>
                                <option value="80000" <c:if test="${not empty SecondSchlBean.payscale && SecondSchlBean.payscale == '80000'}"> <c:out value='selected="selected"'/></c:if>>80000</option>
                                <option value="90000" <c:if test="${not empty SecondSchlBean.payscale && SecondSchlBean.payscale == '90000'}"> <c:out value='selected="selected"'/></c:if>>90000</option>
                            </select>

                            <select name="gp2" id="gp2">
                                <option value="">--Select--</option>
                                <option value="0" <c:if test="${not empty SecondSchlBean.gp && SecondSchlBean.gp == '0'}"> <c:out value='selected="selected"'/></c:if>>0</option>
                                <option value="1700" <c:if test="${not empty SecondSchlBean.gp && SecondSchlBean.gp == '1700'}"> <c:out value='selected="selected"'/></c:if>>1700</option>
                                <option value="1775" <c:if test="${not empty SecondSchlBean.gp && SecondSchlBean.gp == '1775'}"> <c:out value='selected="selected"'/></c:if>>1775</option>
                                <option value="1800" <c:if test="${not empty SecondSchlBean.gp && SecondSchlBean.gp == '1800'}"> <c:out value='selected="selected"'/></c:if>>1800</option>
                                <option value="1900" <c:if test="${not empty SecondSchlBean.gp && SecondSchlBean.gp == '1900'}"> <c:out value='selected="selected"'/></c:if>>1900</option>
                                <option value="2000" <c:if test="${not empty SecondSchlBean.gp && SecondSchlBean.gp == '2000'}"> <c:out value='selected="selected"'/></c:if>>2000</option>
                                <option value="2200" <c:if test="${not empty SecondSchlBean.gp && SecondSchlBean.gp == '2200'}"> <c:out value='selected="selected"'/></c:if>>2200</option>
                                <option value="2400" <c:if test="${not empty SecondSchlBean.gp && SecondSchlBean.gp == '2400'}"> <c:out value='selected="selected"'/></c:if>>2400</option>
                                <option value="2800" <c:if test="${not empty SecondSchlBean.gp && SecondSchlBean.gp == '2800'}"> <c:out value='selected="selected"'/></c:if>>2800</option>
                                <option value="4200" <c:if test="${not empty SecondSchlBean.gp && SecondSchlBean.gp == '4200'}"> <c:out value='selected="selected"'/></c:if>>4200</option>
                                <option value="4600" <c:if test="${not empty SecondSchlBean.gp && SecondSchlBean.gp == '4600'}"> <c:out value='selected="selected"'/></c:if>>4600</option>
                                <option value="4800" <c:if test="${not empty SecondSchlBean.gp && SecondSchlBean.gp == '4800'}"> <c:out value='selected="selected"'/></c:if>>4800</option>
                                <option value="5400" <c:if test="${not empty SecondSchlBean.gp && SecondSchlBean.gp == '5400'}"> <c:out value='selected="selected"'/></c:if>>5400</option>
                                <option value="6600" <c:if test="${not empty SecondSchlBean.gp && SecondSchlBean.gp == '6600'}"> <c:out value='selected="selected"'/></c:if>>6600</option>
                                <option value="7600" <c:if test="${not empty SecondSchlBean.gp && SecondSchlBean.gp == '7600'}"> <c:out value='selected="selected"'/></c:if>>7600</option>
                                <option value="8700" <c:if test="${not empty SecondSchlBean.gp && SecondSchlBean.gp == '8700'}"> <c:out value='selected="selected"'/></c:if>>8700</option>
                                <option value="8800" <c:if test="${not empty SecondSchlBean.gp && SecondSchlBean.gp == '8800'}"> <c:out value='selected="selected"'/></c:if>>8800</option>
                                <option value="8900" <c:if test="${not empty SecondSchlBean.gp && SecondSchlBean.gp == '8900'}"> <c:out value='selected="selected"'/></c:if>>8900</option>
                                <option value="9000" <c:if test="${not empty SecondSchlBean.gp && SecondSchlBean.gp == '9000'}"> <c:out value='selected="selected"'/></c:if>>9000</option>
                                <option value="10000" <c:if test="${not empty SecondSchlBean.gp && SecondSchlBean.gp == '10000'}"> <c:out value='selected="selected"'/></c:if>>10000</option>
                            </select>
                        </td>
                    </tr>
                    <tr style="height:50px;">
                        <td>&nbsp;</td>
                        <td align="right">
                            <button type="submit" class="easyui-linkbutton" onclick="return saveCheck()">Submit</button>
                        </td>
                    </tr>
                </table>
            </div>
        </form>

        <%-- Start - Content for Child Window --%>
        <div id="winsubstantivepost" class="easyui-window" title="Search" style="width:700px;height:400px;top:50px;padding:10px 20px" closed="true" buttons="#searchdlg-buttons"
             data-options="iconCls:'icon-search',modal:true">
            <table width="100%" border="0" cellpadding="0" cellspacing="0">
                <tr style="height:40px;">
                    <td width="20%">Department</td>
                    <td width="80%">
                        <input name="department" id="department" class="easyui-combobox" style="width:500px;" data-options="valueField:'deptCode',textField:'deptName',editable:false" />
                    </td>
                </tr>
                </tr>
                <tr style="height:40px;">
                    <td>Office Name</td>
                    <td>
                        <input name="office" id="office" class="easyui-combobox" style="width:500px;" data-options="valueField:'offCode',textField:'offName',editable:false"/>
                    </td>
                </tr>
                <tr style="height:40px;">
                    <td>Post Name</td>
                    <td>
                        <input name="post" id="post" class="easyui-combobox" style="width:400px;" data-options="valueField:'value',textField:'label',editable:false">
                    </td>
                </tr>
                <tr style="height:40px;">
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
