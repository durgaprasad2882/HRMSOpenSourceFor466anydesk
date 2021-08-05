<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<% int i = 1;
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <form id="fm" action="address.htm" method="post" name="emp" >
            <table border="0" class="tableview" cellpadding="0" cellspacing="0" width="100%">
                <thead>
                <input type="hidden" name="txtemppermaddrs" id="txtemppermaddrs"/>
                <input type="hidden" name="txtemppermpin" id="txtemppermpin"/>
                <input type="hidden" name="txtempSTD" id="txtempSTD"/>
                <input type="hidden" name="txtemptelno" id="txtemptelno"/>
                
                    <tr>
                        <td align="center"><%=i++%>.</td>
                        <td>Address</td>
                        <td colspan="4" >
                            <select id="sltAddressType" class="easyui-combobox" name="sltAddressType" style="width:40%;">
                                <option value="">--Select One--</option>
                                <option value="PA">Permanent Address</option>
                                <option value="RA">Present Residence Address</option>
                            </select> 
                        </td>
                    </tr>
                    <tr >
                        <td width="5%" align="center"><%=i++%>.</td>
                        <td width="15%">State</td>
                        <td width="30%">
                            <logic:notEmpty name="AddressFormBean" property="viewSltempState">
                                <span><c:out value="${emp.gpfno}" /><bean:write name="AddressFormBean" property="viewSltempState"/></span>
                            </logic:notEmpty>
                            <html:select property="sltempState" styleClass="style12" style="width: 63%;display: none" styleId="sltempState" >
                                <html:option value=""></html:option> 
                                <logic:notEmpty name="AddressFormBean" property="stateArray">
                                    <html:optionsCollection name="AddressFormBean" property="stateArray"/> 
                                </logic:notEmpty>
                            </html:select>
                        </td>
                        <td width="5%" align="center"><%=i++%>.</td>
                        <td width="15%">District</td>
                        <td width="30%">
                            <logic:notEmpty name="AddressFormBean" property="viewSltempDistrict">
                                <span><bean:write name="AddressFormBean" property="viewSltempDistrict"/></span>
                            </logic:notEmpty>
                            <html:select property="sltempDistrict" styleClass="style12" style="width: 68%;display: none" styleId="sltempDistrict">
                                <html:option value=""></html:option> 
                                <logic:notEmpty name="AddressFormBean" property="districtArray">
                                    <html:optionsCollection property="districtArray"/> 
                                </logic:notEmpty>
                            </html:select>
                        </td>
                    </tr>



            </table>
        </form>
    </body>
</html>
