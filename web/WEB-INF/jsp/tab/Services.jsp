<%@ page contentType="text/html;charset=windows-1252"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=windows-1252"/>
        <title>HRMS-Services</title>        



    </head>
    <body>
        <form action="getRollWiseLink.htm" commandName="RollwiseGroupInfoBean">
        <div align="center">
            <div style="width:100%;border:#5095ce solid 1px;background-color:#b1c242;">
                <table border="0" width="100%"  cellspacing="0" style="font-size:12px; font-family:verdana;layout: fixed;color:#000000;font-weight:bold;">
                    <thead> </thead>
                    <tr>
                        <td width="20%" align="right">
                            Employee Name:                    
                        </td>
                        <td width="38%" style="text-transform:uppercase;" align="left">
                            <b> ${SelectedEmpObj.fullName} </b>
                        </td>
                        <td width="16%" align="right">
                            HRMS ID:                    
                        </td>
                        <td width="26%">
                            ${SelectedEmpObj.empId} 
                        </td>
                    </tr>

                    <tr>
                        <td align="right">Current Post: </td>
                        <td >
                            &nbsp; ${SelectedEmpObj.postname} 
                        </td>
                        <td align="right">GPF/ PPAN No:</td>
                        <td><b style="text-transform:uppercase;"> ${SelectedEmpObj.gpfno}     &nbsp;</b></td>
                    </tr>
                    <tr>
                        <td align="right">Current Cadre: </td>
                        <td align="left"><b> ${SelectedEmpObj.cadrename}   &nbsp;</b></td>
                        <td align="right">Current Status:</td>
                        <td><b> ${SelectedEmpObj.depstatus}&nbsp;</b></td>
                    </tr>
                </table>
            </div>
        </div>
        <div style="height:640px;padding: 5px;">
            
                <c:forEach var="grpListArr" items="${RollwiseGroupInfoBean.grpList}"> 
                    <div class="modGrp"> 
                        <span><div align="center"> ${grpListArr.modGrpName} </div></span>
                        
                            <ul class="serviceList">
                                <c:forEach var="mplistarray" items="${grpListArr.moduleListArr}"> 
                                
                                    <li class="modclass">
                                        <a href="javascript:openWindow('${mplistarray.linkurl}','${mplistarray.moduleName}')">${mplistarray.moduleName}</a> 
                                    </li>
                                    
                                </c:forEach>
                            </ul>
                        

                        <div align="right" class="pagingServicList">
                            <ul style="margin:0;padding:0;list-style:none;float:right;">                        										
                                
                            </ul>						
                        </div>
                    </div>                    
                </c:forEach>
           
        </div>
<!--
        <div align="center">
            <div style="height:30px;width:100%; border: #5095ce solid 1px;background-color:#e5f0c9;font-weight:bold;color:#FFFFFF;vertical-align: middle;padding:10px 0px 0px 0px; ">
                <ul id="modgrouplistpaging" style="margin:0;padding:0;list-style:none;float:right;font-size: 18px;margin-right: 30px;">                        										
                    
                </ul>
            </div>
        </div>
-->
        </form>

    </body>
</html>