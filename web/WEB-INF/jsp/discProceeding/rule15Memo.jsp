
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>:HRMS:</title>
        <link rel="stylesheet" type="text/css" href="resources/css/themes/default/easyui.css"/>
        <link rel="stylesheet" type="text/css" href="resources/css/themes/icon.css"/>
        <!-- LAYOUT v 1.3.0 -->
        <script type="text/javascript" src="js/servicehistory.js"></script>
        <script type="text/javascript" src="js/jquery.min-1.9.1.js"></script>
        <script type="text/javascript" src="js/jquery.easyui.min.js"></script>
        <script type="text/javascript" src="js/common.js"></script>
        
        <style type="text/css">
            .headr{
                font-weight: bold;
                font-family: Verdana, Arial, Helvetica, sans-serif;
                font-size: 16px;
            }
        </style>
        
        <script language="javascript" type="text/javascript">
            $(document).ready(function(){
                $('#rule15MemoDate').datebox().datebox('calendar').calendar({
                    validator: function(date){
                            var now = new Date();
                            var d1 = new Date('1950', '01', '01');
                            var d2 = new Date(now.getFullYear(), now.getMonth(), now.getDate());
                            return d1<=date && date<=d2;
                    }
                });
            });
            function saveMemo(){
                var memoNo=$('#rule15MemoNo').val();
                if(memoNo == ''){
                    alert("Please enter Memorandum No");
                    return false;
                }
                if(memoNo.length >=18){
                    alert("Please Enter a Memorandum No between 1 and 18");
                    return false;
                }
                var ordrDate= $('#rule15MemoDate').datebox('getValue');// will get the date value
                if( ordrDate == ''){
                    alert("Please enter Date");
                    return false;
                }
                var charge=$('#annex1Charge').val();
                if(charge.length >=495){
                    alert("Please Enter value between 0 and 495");
                    return false;
                }
                
            }
        </script>
    </head>
    <body style="padding:0px;">
        <table style="font-family:Verdana;margin-top: 5px;" width="99%" cellpadding="0" cellspacing="0">
            <tr>
                <td style="width:24%;">
                    <div style="border-width:1px;width:99%;font-weight:bold;color:#333;border-color:#d7d7d7;border-style:solid; border-width:1px 1px 0 1px;text-align:center;">
                        IRREGULARITIES
                    </div>
                </td>
                <td style="width:24%;">
                    <div style="border-width:1px;width:99%;color:#333;border-color:#d7d7d7;border-style:solid; border-width:1px 1px 1px 0;text-align:center;">
                        CHARGES AND WITNESS
                   </div>
                </td>
            </tr>
        </table>
        
        <form:form action="saveRule15Memo.htm" method="POST" commandName="Rule15ChargeBean">
            
            <input type="hidden" name="hidDoHrmsId" id="hidDoHrmsId" value='${Rule15Memo.doHrmsId}'/>
            <input type="hidden" name="hidOffCode" id="hidOffCode" value='${Rule15Memo.hidOffCode}'/>
            
            <div align="center" width="99%" style="margin-top:5px;margin-bottom:10px;height:55%;">
                <div align="center" class="easyui-panel" title="IRREGULARITIES" width="99%">
                    <table border="0" width="99%">
                        <tr>
                            <td colspan="2" align="center" class="headr">Government of Odisha
                                <br/>[<c:out value="${Rule15Memo.deptName}"/> DEPARTMENT]
                            </td>
                        </tr>
                        <tr>
                            <td>&nbsp;</td>
                        </tr>
                        <tr>
                            <td width="50%" align="left">Memorandum No: <input name="rule15MemoNo" id="rule15MemoNo" class="easyui-textbox" 
                                        data-options="required:true,validType:'length[1,18]'" value="${Rule15Memo.rule15MemoNo}"
                                        style="width:60%;border:1px solid #000000;border-radius: 0.3em;"/>
                            </td>
                            <td width="50%" align="center">Date: <input name="rule15MemoDate" id="rule15MemoDate" class="easyui-datebox" editable="false"
                                        value="${Rule15Memo.rule15MemoDate}" style="width:60%" data-options="required:true,formatter:myformatter,parser:myparser"/>
                            </td>
                        </tr> 
                        <tr>
                            <td>&nbsp;</td>
                        </tr>
                    </table>
                    
                    <table width="100%" border="0" style="font-family: Verdana, Arial, Helvetica, sans-serif;font-size: 13px;">
                        <tr>
                            <td><p style="text-align: justify;">&nbsp;&nbsp;&nbsp;
                                    Sri / Smt <span style="font-weight: bold;"><c:out value="${Rule15Memo.doEmpName}"/></span> is hereby informed 
                                    that it is proposed to hold an inquiry against him/her under Rule 15 of the Orissa Civil Services 
                                    (Classification , Control and Appeal) Rules, 1962. 
                                </p>
                                <p style="text-align: justify;">&nbsp;&nbsp;Sri / Smt <span style="font-weight: bold;"><c:out value="${Rule15Memo.doEmpName}"/></span> has committed following irregularities.</p>
                            </td>
                        </tr>
                        <tr>
                            <td style="padding-left: 20px;">
                                <input class="easyui-textbox" name="annex1Charge" id="annex1Charge" 
                                        style="width:97%;height:98px;" data-options="validType:'length[0,495]',multiline:true">
                            </td>
                        </tr>    
                        <tr>
                            <td>&nbsp;</td>
                        </tr>
                    </table>
                </div>
            </div>
            
            <div align="center" width="99%" style="margin-top:2px;margin-bottom:10px;border:1px solid #95B8E7;">
                <table border="0" width="100%">
                    <tr>
                        <td width="50%" align="left" style="padding-right:20px;">
                            <input type="submit" name="btnAnnexture1" value="Previous" class="easyui-linkbutton" data-options="iconCls:'icon-remove'"/>
                        </td>
                        <td width="50%" align="right" style="padding-left:20px;">
                            <input type="submit" name="btnAnnexture1" value="CHARGES And WITNESS" class="easyui-linkbutton" 
                                   onclick="return saveMemo()" style="width:60%;border: 1px solid #2A7CBE;"/>
                        </td>
                    </tr>
                </table>
            </div>
            
        </form:form>
    </body>
</html>
