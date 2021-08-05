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
                alert("satya");
                
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
            <table border="0" class="tableview" cellpadding="0" cellspacing="0" width="100%">
                <tr>
                <input type="text" name="empid" id="empid" value="${emp.empid}"/>
                <td width="5%">&nbsp;</td>
                <td width="25%">&nbsp;</td>
                <td width="30%" align="center">&nbsp;</td>
                <td width="15%" align="center">&nbsp;</td>
                <td width="5%" align="center">&nbsp;</td>
                <td width="5%" align="center">&nbsp;</td>
                <td width="5%">&nbsp;</td>

            </tr>
            <tr height="40px">
                <td align="center"><%=i++%>.</td>
                <td>Employee's Full Name:</td>
                <td colspan="3" width="">
                    <c:out value="${emp.empName}" />

                </td>
            </tr>

            <tr height="40px">
                <td align="center"><%=i++%>.</td>
                <td>Employee's GPF No:</td>
                <td colspan="3">
                    <span><c:out value="${emp.gpfno}" /></span>
                </td>
                <td>&nbsp; </td>
            </tr>

            <tr height="40px">
                <td align="center"><%=i++%>.</td>
                <td>GIS Type:</td>
                <td>
                    <input class="easyui-combobox"  id="gisType" name="gisType" data-options="valueField:'schemeId',textField:'schemeName',url:'getGisTypeJSON.htm',width:'60%',onLoadSuccess : function(){$(this).combobox('setValue',${emp.gisType});}" >

                </td>
                <td>&nbsp; </td>
            </tr>
            <tr height="40px">
                <td align="center"><%=i++%>.</td>
                <td>GIS No:</td>
                <td>
                    <input class="easyui-textbox" id="gisNo" type="text"  data-options="width:'60%'" name="gisNo" value="${emp.gisNo}" ></input>

                </td>
                <td>&nbsp; </td>
            </tr>
            <tr height="40px">
                <td align="center"><%=i++%>.</td>
                <td>
                    Date of Birth(dd-MMM-yyyy):
                </td>
                <td>

                    <span><c:out value="${emp.dob}" /></span>
                    <input type="hidden" name="dob" id="dob"/>
                </td>

                <td align="left" colspan="1" rowspan="4" valign="top">
                    <fieldset style="border:  1px solid black;" width="35%">
                        <legend align="left"><span style="color: black">Employee Photo</span></legend>


                        <!--<iframe scrolling="no" src="' + node.attributes.url + '" frameborder="0" style="border:0;width:100%;height:100%;"></iframe>-->
                    </fieldset>
                </td>	
                <td>&nbsp; </td>
            </tr>
            <tr height="40px">
                <td align="center"><%=i++%>.</td>
                <td>
                    Select Age of Superannuation (in Years)
                </td>
                <td>
                    <input type="radio" name="radyear1" id="radyear1" value="58"  onclick="getSupDate('58', 'txtDos', 'dob')"/>58&nbsp;
                    <input type="radio" name="radyear1" id="radyear2" value="60"  onclick="getSupDate('60', 'txtDos', 'dob')"/>60&nbsp;
                    <input type="radio" name="radyear1" id="radyear3" value="62"  onclick="getSupDate('62', 'txtDos', 'dob')"/>62&nbsp;
                </td>
                <td>&nbsp;</td>
            </tr>
            <tr height="40px">
                <td align="center" ><%=i++%>.</td>
                <td>Date of Superannuation(dd-MMM-yyyy): &nbsp;<span style="color: red">*</span></td>
                <td>
                    <input class="easyui-textbox" id="txtDos" type="text" data-options="width:'30%',required:true,formatter:myformatter,parser:myparser" name="txtDos" editable="false"></input>

                </td>
                <td>&nbsp; </td>
            </tr>

            <tr height="40px">
                <td align="center" ><%=i++%>.</td>
                <td>Date from which in continuous service with GoO(dd-MMM-yyyy):</td>
                <td>
                    <input class="easyui-textbox" id="joindategoo" value="${emp.joindategoo}" type="text" data-options="width:'30%',formatter:myformatter,parser:myparser" name="joindategoo" editable="false"></input>

                </td>
                <td>&nbsp; </td>
            </tr>
            <tr height="40px">
                <td align="center" ><%=i++%>.</td>
                <td>Time</td>
                <td>
                    <select id="txtwefTime" class="easyui-combobox" name="txtwefTime" style="width:60%;">
                        <option value="">--Select One--</option>
                        <option value="FN">FORENOON</option>
                        <option value="AN">AFTERNOON</option>
                    </select> 
                </td>
                <td>&nbsp; </td>
            </tr>
            <tr height="40px">
                <td align="center" ><%=i++%>.</td>
                <td>Date of entry into Govt. service(dd-MMM-yyyy):</td>
                <td>
                    <input class="easyui-textbox" id="doeGov" value="${emp.doeGov}" type="text" data-options="width:'30%',formatter:myformatter,parser:myparser" name="doeGov" editable="false"></input>
                    <input type="hidden" name="txtDoeGov" id="txtDoeGov"/>
                </td>
                <td>&nbsp; </td>
            </tr>
            <tr height="40px">
                <td align="center" ><%=i++%>.</td>
                <td>Time</td>
                <td>
                    <select id="timeOfEntryGoo" class="easyui-combobox" name="timeOfEntryGoo" style="width:60%;">
                        <option value="">--Select One--</option>
                        <option value="FN">FORENOON</option>
                        <option value="AN">AFTERNOON</option>
                    </select> 
                </td>
                <td>&nbsp; </td>
            </tr>

            <tr height="40px">
                <td align="center"><%=i++%>.</td>
                <td>Gender:</td>
                <td >
                    <c:if test="${emp.gender=='M'}">
                        <input type="radio" name="gender" value="M" checked/>
                        Male&nbsp;&nbsp;&nbsp;&nbsp;
                        <input type="radio"  name="gender" value="F" />Female
                    </c:if>
                    <c:if test="${emp.gender=='F'}">
                        <input type="radio" name="gender" value="M" />
                        Male&nbsp;&nbsp;&nbsp;&nbsp;
                        <input type="radio"  name="gender" value="F" checked />Female
                    </c:if>

                    <c:if test="${emp.gender==''}">
                        <input type="radio" name="gender" value="M"/>
                        Male&nbsp;&nbsp;&nbsp;&nbsp;
                        <input type="radio"  name="gender" value="F" />Female
                    </c:if>
                </td>
                <td>&nbsp; </td>
            </tr>
            <tr height="40px">
                <td align="center"><%=i++%>.</td>
                <td>Marital Status:</td>
                <td>
                    <c:out value="${emp.marital}" />
                    <input class="easyui-combobox"  id="marital" name="marital" data-options="valueField:'maritalId',textField:'maritalStatus',url:'getMaritalStatusJSON.htm',width:'60%',onLoadSuccess : function(){$(this).combobox('setValue','${emp.marital}');}" >

                    <input type="hidden" name="hidsltempmarstatus" id="hidsltempmarstatus"/>
                </td>
                <td>&nbsp; </td>
            </tr>


            <tr height="40px">
                <td align="center"><%=i++%>.</td>
                <td>Category:</td>
                <td>
                    <input class="easyui-combobox"  id="category" name="category" data-options="valueField:'categoryid',textField:'categoryName',url:'getCategoryJSON.htm',width:'60%',onLoadSuccess : function(){$(this).combobox('setValue','${emp.category}');}">

                </td>
                <td>&nbsp; </td>
            </tr>
            <tr height="40px">
                <td align="center"><%=i++%>.</td>
                <td>Height(in cm):</td>
                <td>
                    <input class="easyui-textbox" id="txtemphieght" type="text"  data-options="width:'60%'" name="txtemphieght" ></input>
                    <input type="hidden" name="txtemphieght" id="txtemphieght"/>
                </td>
                <td>&nbsp; </td>
            </tr>
            <tr height="40px">
                <td align="center"><%=i++%>.</td>
                <td>Blood Group:</td>
                <td>
                    <input class="easyui-combobox"  id="bloodgrp" name="bloodgrp" data-options="valueField:'bloodgrpId',textField:'bloodgrp',url:'getBloodGrpJSON.htm',width:'60%',onLoadSuccess : function(){$(this).combobox('setValue','${emp.bloodgrp}');}" ></input>
                    <input type="hidden" name="bloodgrp" id="bloodgrp"/>
                </td>
                <td>&nbsp; </td>
            </tr>

            <!--************ CHANGED BY PKM STARTS *********** -->     
            <tr height="40px">
                <td align="center"><%=i++%>.</td>
                <td>Declaration of Home Town:</td>
                <td>
                    <input class="easyui-textbox" id="homeTown" type="text"  data-options="width:'60%'" name="homeTown" ></input>
                </td>
                <td>&nbsp; </td>
            </tr>
            <!--************ CHANGED BY PKM ENDS *********** -->     
            <tr height="40px">
                <td align="center"><%=i++%>.</td>
                <td>Religion:</td>
                <td>
                    <input class="easyui-combobox"  id="religion" name="religion" data-options="valueField:'religionId',textField:'religion',url:'getReligionJSON.htm',width:'60%',onLoadSuccess : function(){$(this).combobox('setValue','${emp.religion}');}" ></input>

                </td>
                <td>&nbsp; </td>
            </tr>
            <tr height="40px">
                <td align="center"><%=i++%>.</td>
                <td>Domicile:</td>
                <td>
                    <input class="easyui-textbox" id="domicil" type="text"  data-options="width:'60%'" name="domicil" ></input>

                </td>
                <td>&nbsp; </td>
            </tr>
            <tr height="40px">
                <td align="center"><%=i++%>.</td>
                <td>Personal Identification Mark:</td>
                <td colspan="5">
                    <input class="easyui-textbox" id="txtemppim" type="text"  data-options="multiline:true ,width:'60%'" style="height:60px" name="txtemppim" ></input>
                    <input type="hidden" name="hidtxtemppim" id="hidtxtemppim"/>

                </td>
            </tr>
            <tr height="40px">
                <td align="center"><%=i++%>.</td>
                <td>Bank Name:</td>
                <td colspan="5">

                    <input class="easyui-combobox"  id="sltBank"  name="sltBank" data-options="width:'50%',valueField:'bankcode',textField:'bankname',
                           onSelect: function (record) {
                           $('#branch').combobox('clear');
                           var url = 'getBranchListJSON.htm?bankCode=' + record.bankcode;
                           $('#branch').combobox('reload', url);

                           }" ></input>
                </td>
            </tr>
            <tr height="40px">
                <td align="center"><%=i++%>.</td>
                <td>Branch Name:</td>
                <td colspan="5">
                    <input class="easyui-combobox"  id="sltBranch"  name="sltBranch" data-options="valueField:'branchcode',textField:'branchname',width:'50%'" ></input>

                </td>
            </tr>
            <tr height="40px">
                <td align="center"><%=i++%>.</td>
                <td>Bank Account No:</td>
                <td>
                    <input class="easyui-textbox" id="txtbankaccno" type="text"  data-options="width:'60%'" name="txtbankaccno" ></input>

                </td>
                <td>&nbsp; </td>
            </tr>
            <tr height="40px">
                <td align="center"><%=i++%>.</td>
                <td>Mobile Number:</td>
                <td>
                    <input class="easyui-textbox" id="mobile"   data-options="width:'60%'" value="${emp.mobile}" name="txtmobno" ></input>

                </td>
                <td>&nbsp; </td>
            </tr>


        </table>
        <div>
           <input type="submit" value="Save"/>
            <a href="avascript:void(0)" class="easyui-linkbutton" onclick="saveProfileDetails()" data-options="iconCls:'icon-save'" >Save</a>
        </div>

    </form:form>
</body>
</html>
