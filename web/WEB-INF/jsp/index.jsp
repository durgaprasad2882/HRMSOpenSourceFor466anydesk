<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%
    StringBuffer sb = new StringBuffer();
    for (int i = 1; i <= 3; i++) {
        //sb.append((char) (int) (Math.random() * 79 + 23 + 7));
        int x = (int) (Math.random() * 20) + 5;
        //System.out.println("Random String is: "+x);
        //sb.append((int)(Math.random() * 20) + 5);
        sb.append(x);
    }
    String cap = new String(sb);
%>
<html>
    <head>
        <title>:: Welcome to HRMS Odisha ::</title>

        <!-- Bootstrap core CSS -->
        <link href="resources/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" type="text/css" href="resources/css/themes/default/easyui.css">
        <link rel="stylesheet" type="text/css" href="resources/css/themes/icon.css">

        <script type="text/javascript" src="js/jquery-1.8.0.min.js"></script>
        <script type="text/javascript" src="js/jquery.easyui.min.js"></script>
        <script type="text/javascript" src="js/common.js"></script>
        <style>
            body, html {
                height: 100%;
                background-repeat: no-repeat;
                /*background-image: linear-gradient(rgb(62,134,196), rgb(60,134,194));
                background: url(images/HRMS_PAYROLL_BANNER.PNG) no-repeat center center fixed; 
                background-color: #F7F7F7;*/
                -webkit-background-size: cover;
                -moz-background-size: cover;
                -o-background-size: cover;
                background-size: contain;
                background: url('images/bg.png');
            }

            .card-container.card {
                max-width: 350px;
                padding: 40px 40px;
            }

            .btn {
                font-weight: 700;
                height: 36px;
                -moz-user-select: none;
                -webkit-user-select: none;
                user-select: none;
                cursor: default;
            }

            /*
             * Card component
             */
            .card {
                background-color: #F7F7F7;
                /* just in case there no content*/
                padding: 20px 25px 30px;
                margin: 0 auto 25px;
                margin-top: 20px;
                /* shadows and rounded borders */
                -moz-border-radius: 2px;
                -webkit-border-radius: 2px;
                border-radius: 2px;
                -moz-box-shadow: 0px 2px 2px rgba(0, 0, 0, 0.3);
                -webkit-box-shadow: 0px 2px 2px rgba(0, 0, 0, 0.3);
                box-shadow: 0px 2px 2px rgba(0, 0, 0, 0.3);
            }

            .profile-img-card {
                width: 96px;
                height: 96px;
                margin: 0 auto 10px;
                display: block;
                -moz-border-radius: 50%;
                -webkit-border-radius: 50%;
                border-radius: 50%;
            }

            /*
             * Form styles
             */
            .profile-name-card {
                font-size: 16px;
                font-weight: bold;
                text-align: center;
                margin: 10px 0 0;
                min-height: 1em;
            }

            .reauth-email {
                display: block;
                color: #404040;
                line-height: 2;
                margin-bottom: 10px;
                font-size: 14px;
                text-align: center;
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
                -moz-box-sizing: border-box;
                -webkit-box-sizing: border-box;
                box-sizing: border-box;
            }

            .form-signin #inputEmail,
            .form-signin #inputPassword {
                direction: ltr;
                height: 44px;
                font-size: 16px;
            }

            .form-signin input[type=email],
            .form-signin input[type=password],
            .form-signin input[type=text],
            .form-signin button {
                width: 100%;
                display: block;
                margin-bottom: 10px;
                z-index: 1;
                position: relative;
                -moz-box-sizing: border-box;
                -webkit-box-sizing: border-box;
                box-sizing: border-box;
            }

            .form-signin .form-control:focus {
                border-color: rgb(104, 145, 162);
                outline: 0;
                -webkit-box-shadow: inset 0 1px 1px rgba(0,0,0,.075),0 0 8px rgb(104, 145, 162);
                box-shadow: inset 0 1px 1px rgba(0,0,0,.075),0 0 8px rgb(104, 145, 162);
            }

            .btn.btn-signin {
                /*background-color: #4d90fe; */
                background-color: #0F487E;
                /* background-color: linear-gradient(rgb(104, 145, 162), rgb(12, 97, 33));*/
                padding: 0px;
                font-weight: 700;
                font-size: 14px;
                height: 36px;
                -moz-border-radius: 3px;
                -webkit-border-radius: 3px;
                border-radius: 3px;
                border: none;
                -o-transition: all 0.218s;
                -moz-transition: all 0.218s;
                -webkit-transition: all 0.218s;
                transition: all 0.218s;
                cursor:pointer;
            }

            .btn.btn-signin:hover,
            .btn.btn-signin:active,
            .btn.btn-signin:focus {
                background-color: #5283D6;
            }

            .forgot-password {
                color: #498BF4;
            }

            .forgot-password:hover,
            .forgot-password:active,
            .forgot-password:focus{
                text-decoration:underline;
            }
            #hrms_footer{width:100%;height:30px;background:#EAEAEA;border-top:1px solid #DADADA;color:#555555;line-height:30px;font-size:9pt;text-align:center;}
            #error_msg{display: block;
                       color: #FF0000;font-weight:bold;
                       line-height: 2;
                       margin-bottom: 10px;
                       font-size: 14px;
                       text-align: center;
                       overflow: hidden;
                       text-overflow: ellipsis;
                       white-space: nowrap;
                       -moz-box-sizing: border-box;
                       -webkit-box-sizing: border-box;
                       box-sizing: border-box;}
            </style>

            <script type="text/javascript">
                if (top.frames.length != 0) {
                    if (window.location.href.replace)
                        top.location.replace(self.location.href);
                    else
                        top.location.href = self.document.href;
                }
                $(document).ready(function() {
                    $("#userName").attr('placeholder', 'User Id')
                    $("#password").attr('placeholder', 'password')
                    $("#cap1").attr('placeholder', 'Captcha')

                    $('#dd').dialog({
                        title: 'Forgot Password',
                        width: 500,
                        height: 400,
                        closed: false,
                        cache: false,
                        modal: true,
                        onClose: function() {
                            $('#dd').dialog('refresh');
                            $('#mobile').val('');

                        }
                    });
                    $('#dd').dialog('close');

                    $('input#cap1').bind('copy paste', function(e) {
                        e.preventDefault();
                    });
                });


                function sendRequest() {
                    $("#mobile").val();

                    var mob = /^[1-9]{1}[0-9]{9}$/;
                    var dateExp = /^(([0-9])|([0-2][0-9])|([3][0-1]))\-(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec|JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)\-\d{4}$/;
                    var dateofbirth = $('#dateofbirth');

                    if ($("#mobile").val() == '') {
                        alert('Mobile Number cannot be blank');
                        $("#mobile").focus();
                        return false;
                    } else if (dateofbirth.val() == '') {
                        alert('Date of Birth cannot be blank');
                        return false;
                    } else if (mob.test($.trim($("#mobile").val())) == false) {
                        alert('Please enter a valid mobile number.');
                        return false;
                    } else if (dateExp.test($.trim(dateofbirth.val())) == false) {
                        alert('Please enter a valid Date.');
                        return false;
                    } else if ($('#cap1').val() == '') {
                        alert('Please enter the shown Characters in the Captcha field');
                        $('#cap1').focus();
                        return false;
                    } else {
                        $("#msgid").html('');
                        $("#msgid").css("text-align", "center");
                        $("#msgid").html('<span><img src="images/ajax-loader.gif" width="20" height="20"/>&nbsp;&nbsp;Please Wait...<\/span>');
                        $.ajax({
                            type: 'POST',
                            url: 'ForgotPassword.htm?mobile=' + $("#mobile").val() + '&dob=' + dateofbirth.val() + '&cap1=' + $('#cap1').val() + '&cap2=' + $('#cap2').val(),
                            success: function(msg) {
                                var str = '';
                                //alert("Msg is: "+msg);
                                setTimeout(function() {
                                    $("#msgid").html('');
                                    if (msg == 1) {
                                        str = 'User Id and Password has been sent to your mobile.';
                                        $("#msgid").text(str);
                                    } else if (msg == "2") {
                                        str = '<h3>Your mobile number is not registered in HRMS.<br\/> ';
                                        str = str + 'Note: To register your mobile no, Please Contact<br\/>';
                                        str = str + 'your billing section or DDO.<br\/>';

                                        str = str + '<a href="https://drive.google.com/file/d/0B6DGe7Iwnj3vMGNPZExkTWNkY2c/view?usp=sharing">Click Here<\/a> how billing section or DDO can register<br\/>';
                                        str = str + 'the Mobile No<\/h3>';
                                        $("#msgid").append(str);
                                    } else if (msg == 3) {
                                        str = '<span style="color:red;"><br \/>Request limit is 3 times per day.</\span>';
                                        $("#msgid").append(str);
                                    } else if (msg == "4") {
                                        str = '<span style="color:red;"><br \/>Combination of Mobile and Date of Birth does not match!</\span>';
                                        $("#msgid").append(str);
                                    } else if (msg == "5") {
                                        str = '<span style="color:red;"><br \/>Invalid Captcha Code!</\span>';
                                        $("#msgid").append(str);
                                    } else if (msg == 6) {
                                        str = '<span style="color:red;"><br \/>Wait for 5 mins for Next Request!</\span>';
                                        $("#msgid").append(str);
                                    }
                                }, 3000);
                            }
                        });


                    }

                }
                function ssoLogin() {
                    var myWindow = window.open("ssosamlrequestcreator", "ePraman", "width=500,height=500");
                }
            </script>
        </head>

        <body>
            <div style="text-align:center;padding:5px;background:#FAFAFA;border-bottom:1px solid #DADADA;">
            <a href="http://hrmsodisha.gov.in"><img src="images/temp_logo.png" alt="Human Resource Management System" /></a>
        </div>
        <h1>
            <div align="center">
				<h2 style='color:red'>Submit your Grievance to your Authority</h2><br/>
                <!--<a target="_blank" href="http://apps.hrmsorissa.gov.in/portal/page/portal/HRMS/Services" style='color:red'>For preparing Salary Bill please click here</a>-->
            </div>
        </h1>
        <div class="container">

            <div class="card card-container">
                <img id="profile-img" class="profile-img-card" src="images/logo3.png" />
                <p id="profile-name" class="profile-name-card">Login to your Account</p>
                <form class="form-signin" action="login.htm" commandName="loginForm"  method="post" autocomplete="off">
                    <span id="error_msg" class="error_msg"> ${errorMsg} </span>
                    <input type="text" name="userName" readonly id="userName" class="form-control" autocomplete="off" placeholder="User Id" onfocus="if (this.hasAttribute('readonly')) {
                                this.removeAttribute('readonly');
                                // fix for mobile safari to show virtual keyboard
                                this.blur();
                                this.focus();
                            }" required autofocus>
                    <input type="password" readonly name="userPassword" id="userPassword" class="form-control" autocomplete="off" placeholder="Password" onfocus="if (this.hasAttribute('readonly')) {
                                this.removeAttribute('readonly');
                                // fix for mobile safari to show virtual keyboard
                                this.blur();
                                this.focus();
                            }" required>
                    <div style="margin-bottom:10px;"><img id="captcha_id" name="imgCaptcha" src="captcha.jpg">
                        <span style="color:#888888;font-size:9pt;font-style:italic;">Can&rsquo;t read?</span> <a style="font-size:9pt;" href="javascript:void(0)" onclick="document.getElementById('captcha_id').src = 'captcha.jpg?' + Math.random();
                                return false">Click here</a></div>
                    <input type="text" name="captcha" id="captcha" class="form-control" placeholder="Security Code" required>
                    <button class="btn btn-lg btn-primary btn-block btn-signin" type="submit">Login</button>
                    <a href="javascript:ssoLogin()" >Login using e-Pramaan</a>
                </form>

                <a class="forgot-password" href="javascript:void(0)" onclick="$('#dd').dialog('open')"> 
                    Forgot your password?
                </a>

            </div>
        </div>
        <div id="hrms_footer">
            Copyright © CMGI - All Rights Reserved - General Administration(AR) Department, Government of Odisha
        </div>
        <div id="dd" style="padding:20px;">
            <%--<h1>
                <span style="color:red;">Forgot Password facility is temporarily unavailable.</span>
            </h1>--%>
            Enter your 10 digit Registered (HRMS) Mobile Number
            <div style="padding:10px;">
                <input id="mobile" class="easyui-validatebox" data-options="prompt:'Enter mobile number',required:true,validType:'mobile'" style="width:100%;height:32px">
            </div>
            Enter your Date of Birth (dd-MMM-yyyy)
            <div style="padding:10px;">
                <%--<input type="text" name="dateofbirth" id="dateofbirth" style="width:200px;" class="easyui-validatebox" data-options="prompt:'Enter Date of Birth',required:true,validType:'dateofbirth'"> For Eg. 01-JAN-2001--%>
                <input class="easyui-datebox" id="dateofbirth" name="dateofbirth" style="width:45%" data-options="required:true,formatter:myformatter,parser:myparser" editable="false">
            </div>
            Enter the below characters in text box.
            <div style="width:100%;padding:10px;">
                <table border="0">  
                    <tbody>  
                        <tr>  
                            <td width="40%" align="middle">  
                                <span style="background-color: aqua;font-size:16px;"><%=cap%></span>  
                            </td>

                            <td width="40%"><input type="text" name="cap1" value="" id="cap1"/></td>  
                            <td width="20%"><input type="hidden" name="cap2" value='<%=cap%>' id="cap2" readonly="readonly"/></td>  
                        </tr>  
                    </tbody>  
                </table>
            </div>
            <div align="center" style="width:50%;padding:10px;">
                <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-ok" style="width:100%;height:32px;" onclick="sendRequest()">Send Request</a>
            </div>

            <div id="msgid" style="color:#008000;font-weigth:bold 14 px solid;">

            </div>
        </div>


    </body>
</html>