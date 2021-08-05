<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Human Resources Management System, Government of Odisha</title>
        <link href="css/bootstrap.min.css" rel="stylesheet">
        <link href="font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">        
        <link href="css/bootstrap-datetimepicker.css" rel="stylesheet" type="text/css"/>
        <script src="js/moment.js" type="text/javascript"></script>
        <script src="js/jquery.min.js" type="text/javascript"></script>        
        <script src="js/bootstrap.min.js" type="text/javascript"></script>
        <script src="js/bootstrap-datetimepicker.js" type="text/javascript"></script>
        <script type="text/javascript">

        </script>
    </head>
    <body>
        <div class="container-fluid">
            <form:form class="form-horizontal" action="saveAllowanceAndDeduction.htm" method="POST" commandName="command">
                <div class="panel panel-default">
                    <div class="panel-heading">

                    </div>
                    <div class="panel-body">
                        <div class="row">
                            <div class="col-lg-6">
                                <div class="form-group">
                                    <label class="control-label col-sm-2" for="addesc">Description:</label>
                                    <div class="col-sm-10">
                                        <form:hidden path="adcode"/>
                                        <form:hidden path="adtype"/>
                                        <form:hidden path="whereupdated"/>
                                        <form:hidden path="updationRefCode"/>
                                        <form:input path="addesc" id="addesc" class="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label class="control-label col-sm-2" for="advalue">Fixed or Formula:</label>
                                    <div class="col-sm-10">
                                        <form:select path="adamttype" id="adamttype" class="form-control">
                                            <form:option value="">--Select One--</form:option>
                                            <form:option value="1">FIXED</form:option>
                                            <form:option value="0">FORMULA</form:option>
                                        </form:select>                                        
                                    </div>
                                </div>                                    
                                <div class="form-group">
                                    <label class="control-label col-sm-2" for="advalue">Amount:</label>
                                    <div class="col-sm-10">                                        
                                        <form:input path="advalue" id="advalue" class="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label class="control-label col-sm-2" for="formula">Formula:</label>
                                    <div class="col-sm-10">                                        
                                        <form:select path="formula" id="formula" class="form-control">
                                            <form:option value="">--Select One--</form:option>
                                        </form:select>
                                    </div>
                                </div>                                
                            </div>
                        </div>
                    </div>
                    <div class="panel-footer">
                        <button type="submit" class="btn btn-default" name="Save">Save</button>
                        <button type="submit" class="btn btn-default" name="Cancel">Cancel</button>
                    </div>
                </div>
            </form:form>
        </div>
    </body>
</html>
