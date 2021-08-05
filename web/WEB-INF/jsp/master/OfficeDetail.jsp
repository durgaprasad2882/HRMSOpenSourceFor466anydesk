<%-- 
    Document   : OfficeDetail
    Created on : Nov 22, 2017, 11:09:09 AM
    Author     : manisha
--%>


<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>:: HRMS, Government of Odisha ::</title>
        <link href="css/bootstrap.min.css" rel="stylesheet">
        <link href="font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">        
        <link href="css/sb-admin.css" rel="stylesheet">
        <script src="js/jquery.min.js"></script>        
        <script src="js/bootstrap.min.js"></script>
        <script type="text/javascript">
            function getOfficeList() {
                var url = 'getOfficeListJSON.htm?deptcode=' + $("#deptCode").val();
                $.getJSON(url, function (data) {
                    $('#pOffCode').empty();
                    $.each(data, function (i, obj) {
                        $('#pOffCode').append($('<option>').text(obj.offName).attr('value', obj.offCode));
                    });
                });
            }
            function saveCheck(){
                 var offEn=$('#offEn').val();
                if(offEn==""){
                    alert("Please Enter Office Name");
                    $('#ddoCode').focus();
                     return false;
                }
                var ddoCode=$('#ddoCode').val();
                if(ddoCode==""){
                    alert("Please Enter DDO Code");
                    $('#ddoCode').focus();
                     return false;
                }
                 var deptCode = $('#deptCode').combobox('getValue');
                 if(deptCode==""){
                      alert("Please Select Department");
                       return false;
                 }
              // return true;
            }
        </script>      
        <style type="text/css">
            .control-label {
                padding-top: 7px;
                margin-bottom: 0;
                text-align: left;
            }
            .row{
                margin-bottom: 5px;
            }
        </style>
    </head>
    <body>
        <div id="wrapper">
            <jsp:include page="../tab/hrmsadminmenu.jsp"/>        
            <div id="page-wrapper">
                <form:form action="getOfficeDetail.htm" commandName="officeModel">
                    <div class="container-fluid">
                        <div style="text-align:center;border:1px ">
                            OFFICE ESTABLISHMENT DETAILS

                        </div>
                        <div class="row bg-primary" style="padding: 5px;">Office Information</div>
                        <form action="/action_page.php">
                            <div class="row">
                                <label class="control-label col-sm-3">1&emsp;Office code  </label> 
                                <div class="col-sm-6" >
                                    <form:hidden path="offCode" value="${officeModel.offCode}"/>
                                    ${officeModel.offCode}
                                </div>
                            </div>  
                            <div class="row">
                                <label class="control-label col-sm-3">2&emsp;Office Name  </label> 
                                <div class="col-sm-6" >
                                    <form:input class="form-control" path="offEn" id="offEn"/>

                                </div>
                            </div>
                            <div class="row">     
                                <label class="control-label col-sm-3">3&emsp; Name of the department </label>
                                <p class="form-control-static col-sm-8">
                                    <form:select class="form-control" path="deptCode" id="deptCode" onchange="getOfficeList()">
                                        <form:option value="" label="Select"/>
                                        <form:options items="${departmentList}" itemLabel="deptName" itemValue="deptCode"/>                                        
                                    </form:select>
                                </p>

                            </div>                        

                            <div class="row">
                                <label class="control-label col-sm-3" >4&emsp;establishment type</label>
                                <p class="form-control-static col-sm-8">Field office</p>
                            </div>

                            <div class="row bg-primary" style="padding: 5px;">Office Address</div>

                            <div class="row">                            
                                <label class="control-label col-sm-3">5&emsp; Office Address</label>
                            </div>                        

                            <div class="row">
                                <label class="control-label col-sm-3" >&emsp;&emsp;Address</label>
                                <div class="col-sm-6" >
                                    <form:input class="form-control" path="offAddress"/>
                                </div>
                            </div>
                            <div class="row">
                                <label class="control-label col-sm-3" >&emsp;&emsp;Village/Town/City:</label>
                                <div class="col-sm-8">
                                    ${officeModel.villagename}
                                </div>
                            </div>
                            <div class="row">
                                <label class="control-label col-sm-3" >&emsp;&emsp;Block:</label>                                
                                <div class="col-sm-3">
                                    <form:select class="form-control" path="blockCode">
                                        <form:option value="" label="Select"/>
                                        <form:options items="${blockList}" itemLabel="blockCode" itemValue="blockName"/>                                        
                                    </form:select>
                                </div>
                            </div>
                            <div class="row">
                                <label class="control-label col-sm-3" >&emsp;&emsp;pin Code:</label>
                                <div class="col-sm-6" >
                                    <form:input class="form-control" path="pincode"/>
                                </div>
                            </div>
                            <div class="row">
                                <label class="control-label col-sm-3" >&emsp;&emsp;District:</label>
                                <p class="form-control-static col-sm-3">
                                    <form:select class="form-control" path="distCode">
                                        <form:option value="" label="Select"/>
                                        <form:options items="${districtList}" itemLabel="distName" itemValue="distCode"/>                                        
                                    </form:select>
                                </p>
                                <p class="form-control-static col-sm-3">State:<form:input class="form-control" path="stateName"/></p>

                            </div>
                            <div class="row">
                                <label class="control-label col-sm-3">6&emsp;Office telephone no.</label>
                                <p class="form-control-static col-sm-1">Area/std code</p>
                                <p class="form-control-static col-sm-3"><form:input class="form-control" path="telStd"/></p>
                                <p class="form-control-static col-sm-2">Tel
                                    No :<form:input class="form-control" path="telNo"/></p>
                            </div>
                            <div class="row">

                                <label class="control-label col-sm-3">7&emsp;Office Fax no.</label>
                                <div class="col-sm-3" >
                                    <form:input class="form-control" path="faxNo"/>

                                </div>
                            </div>
                            <div class="row">
                                <label class="control-label col-sm-3" >8&emsp;Office email id</label>
                                <div class="col-sm-6" >
                                    <form:input class="form-control" path="offEmail"/>
                                </div>
                            </div>

                            <div class="row bg-primary" style="padding: 5px;">DDO INFORMATION</div>

                            <div class="row">
                                <label class="control-label col-sm-3">9&emsp;Parent Office</label>
                                <div class="col-sm-9">
                                    <form:select class="form-control" id="pOffCode" path="pOffCode">
                                        <form:option value="" label="Select"/>
                                        <form:options items="${parentOffList}" itemLabel="offName" itemValue="pOffCode"/>                                        
                                    </form:select>
                                </div>
                            </div>   
                            <div class="row">
                                <label class="control-label col-sm-3">10&emsp;Sub Division</label>
                                <div class="col-sm-9">
                                    <form:select class="form-control" path="subDivisionCode">
                                        <form:option value="" label="Select"/>
                                        <form:options items="${subdivisionList}" itemLabel="subDivisionName" itemValue="subDivisionCode"/>                                        
                                    </form:select>
                                </div>   
                            </div> 
                            <div class="row">
                                <label class="control-label col-sm-3">11&emsp;DDO Name</label>
                                <div class="col-sm-9">${officeModel.ddoName}</div>
                            </div>
                            <div class="row">
                                <label class="control-label col-sm-3">12&emsp;Designation of DDO</label>
                                <div class="col-sm-9">
                                    ${officeModel.ddoPost}

                                </div>
                            </div>
                            <div class="row">                            
                                <label class="control-label col-sm-3">13&emsp;DDO Code</label>
                                <div class="col-sm-6" >
                                    ${officeModel.ddoCode}
                                    <form:input class="form-control" id="ddoCode" path="ddoCode"/>
                                </div>

                            </div>
                            <div class="row">
                                <label class="control-label col-sm-3" >14&emsp;DDO current Acc No</label>
                                <div class="col-sm-6"> 
                                    <form:input class="form-control" path="ddoCurAccNo"/>
                                </div>
                            </div>
                            <div class="row">
                                <label class="control-label col-sm-3" >15&emsp;Treasury Sarkar,Name</label>
                                <div class="col-sm-8">
                                    <form:input class="form-control" path="recBy"/>
                                </div>
                            </div>
                            <div class="row">
                                <label class="control-label col-sm-3">16&emsp;Bank Name</label>
                                <div class="col-sm-9">
                                    <form:select class="form-control" path="bankCode">
                                        <form:option value="" label="Select"/>
                                        <form:options items="${bankList}" itemLabel="bankname" itemValue="bankcode"/>                                        
                                    </form:select>
                                </div>
                            </div>
                            <div class="row">
                                <label class="control-label col-sm-3">17&emsp;Branch Name</label>
                                <div class="col-sm-9">
                                    <form:select class="form-control" path="branchCode">
                                        <form:option value="" label="Select"/>
                                        <form:options items="${branchList}" itemLabel="branchname" itemValue="branchcode"/>                                        
                                    </form:select>
                                </div>
                            </div>

                            <div class="row">
                                <label class="control-label col-sm-3">18&emsp;Office Treasury/sub Treasury Name</label>
                                <div class="col-sm-9">
                                    <form:select class="form-control" path="trCode">
                                        <form:option value="" label="Select"/>
                                        <form:options items="${treasuryList}" itemLabel="treasuryName" itemValue="treasuryCode"/>                                        
                                    </form:select>
                                </div>
                            </div>
                            <div class="row">
                                <label class="control-label col-sm-3" >19&emsp;Office Bank Name</label>
                                <div class="col-sm-10">
                                    <p class="form-control-static"></p>
                                </div>
                            </div>
                            <div class="row">
                                <label class="control-label col-sm-3" >20&emsp;Head of Office(HOO) designation</label>
                                <div class="col-sm-10">
                                    <p class="form-control-static"></p>
                                </div>
                            </div>
                            <div class="row">
                                <label class="control-label col-sm-3" >21&emsp;Number of Employees</label>
                                <p class="form-control-static col-sm-3">Group 05</p>
                                <p class="form-control-static col-sm-3">Group 90</p>
                                <p class="form-control-static col-sm-3">Group 95</p>
                            </div>
                            <div class="row">
                                <label class="control-label col-sm-3" >22&emsp;DDO Regd No</label>
                                <div class="col-sm-6">
                                    <form:input class="form-control" path="ddoRegNo"/>
                                </div>
                            </div>
                            <div class="row">                            
                                <label class="control-label col-sm-3">23&emsp; Tan Number</label>
                                <div class="col-sm-6" >
                                    <form:input class="form-control" path="tanNo"/>
                                </div>
                            </div>

                            <div class="row">                            
                                <label class="control-label col-sm-3">24&emsp; DTO Regd No</label>
                                <div class="col-sm-6">
                                    <form:input class="form-control" path="dtoRegNo"/>
                                </div>
                            </div>
                            <div class="row">                            
                                <label class="control-label col-sm-3">25&emsp;Total Nu.of Employees(GRA+GRB+GRC+GRD)</label>
                                <p class="form-control-static col-sm-8"></p>
                            </div>
                            <div class="row">                            
                                <label class="control-label col-sm-3">26&emsp; Lic PA Code</label>
                                <div class="col-sm-6">
                                    ${officeModel.paCode}
                                </div>
                            </div>

                        </form>
                        <div class="row bg-primary" style="padding: 5px;">Verify and Certify Information</div>
                        <div class="panel-footer">
                            <input type="submit" value="Submit" name="save" class="btn btn-default" onclick="saveCheck()"/>
                            <button type="submit" class="btn btn-default">Exit</button>
                        </div>
                    </form:form>
                </div>
            </div>
        </div>
    </body>
</html>
