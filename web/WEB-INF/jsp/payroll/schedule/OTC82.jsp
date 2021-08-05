<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>:: OTC 82 ::</title>
        <style type="text/css">
            .pgHeader{
                font-size:12px;
                font-family:verdana;
                font-weight: bold;
            }
            .tblHeader{
                font-size:12px;
                text-align:center;
                font-family:verdana;
                font-weight: bold;
                border-top:1px solid black;
                border-bottom:1px solid black;
                border-left:1px solid black;
                border-right:1px solid black;
            }
        </style>
    </head>
    <body>
    <div style="width:90%;margin: 0 auto;">
        <table width="100%" border="0">
            <tr>
                <td style="text-align:center;font-size:18px;font-weight:bold;"><b><u>O.T.C - 82</u></b></td>
            </tr>
            <tr>
                <td style="text-align:center;font-size:15px;">
                    For the month of &nbsp;<c:out value="${Otc82Header.month}"/> - <c:out value="${Otc82Header.year}"/>
                </td>
            </tr>
        </table>
    </div>
           
    <div style="width:98%;margin: 0 auto;font-size:13px; font-family:verdana;">
        <table border="0" width="100%"  cellspacing="0" cellpadding="0" style="font-size:14px; font-family:verdana;">
            <thead></thead>
            <tr>
                <td width="5%">&nbsp;</td>
                <td height="10px" width="80%">To,</td>
                <td width="5%">&nbsp;</td>
            </tr>
            <tr>
                <td>&nbsp;</td>
                <td>&nbsp;&nbsp; The Treasury Officer,</td>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td>&nbsp;</td>
                <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td>&nbsp;</td>
                <td>&nbsp;&nbsp;&nbsp;MANAGER RBI PAD AND CePC </br>&nbsp;&nbsp;&nbsp;BHUBANESWAR</td>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td height="10px" colspan="3">&nbsp;</td>
            </tr>
        </table>    
                
        <table border="0" width="100%"  cellspacing="0" cellpadding="0" style="font-size:14px; font-family:verdana;">
            <thead></thead>
            <tr>
                <td width="5%">&nbsp;</td>
                <td height="10px" width="80%">Sir,</td>
                <td width="5%">&nbsp;</td>
            </tr>
            <tr>
                <td>&nbsp;</td>
                <td class="printData" style="text-align:left;" >
                    <p style="line-height: 25px;text-align: justify;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        Please pay Bill No &nbsp; <b><c:out value="${Otc82Header.billDesc}"/></b>  &nbsp;- <c:out value="${Otc82Header.year}"/> as per statement enclosed.
                    </p>
                </td>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td>&nbsp;</td>
                <td height="10px">&nbsp;
                    <p style="padding-left:5px;"> <b>A.</b> Please Pay <b><c:out value="${Otc82Header.netPay}"/></b> by transfer credit to the saving bank 
                        Accounts of &nbsp; <c:out value="${Otc82Header.noofEmp}"/> no of employees. </p>
                </td>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td>&nbsp;</td>
                <td height="10px">&nbsp;
                    <p style="padding-left:5px;"> <b>B</b> Please Pay ................... by transfer credit to the Drawing and Disbursing Officer's 
                        current Account, acc no    &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
                        &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
                        towards payment of cheque(s)  no. of employees having no account in the bank.</p>
                </td>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td>&nbsp;</td>
                <td height="10px">&nbsp;
                    <p style="padding-left:5px;"> <b>C</b> Please Pay <b><c:out value="${Otc82Header.toDdoAccount}"/></b> by transfer credit to the 
                        Drawing and Disbursing Officer's current Account, acc no    &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 
                        &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 
                        towards recovery of loan from Bank and financial Institutions in respect of &nbsp; &nbsp; &nbsp; no. of employees.</p>
                </td>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td height="15px" colspan="3">&nbsp;</td>
            </tr>
            <tr>
                <td>&nbsp;</td>
                <td height="10px">&nbsp;
                    I authorise  <span style="padding-left:400px;"> Name</p>
                </td>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td>&nbsp;</td>
                <td height="10px">&nbsp;
                    District Office
                </td>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td>&nbsp;</td>
                <td height="10px">&nbsp;
                    to collect above statement and instruments duly signed by you.
                </td>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td height="10px" colspan="3">&nbsp;</td>
            </tr> 
        </table>    
                
        <table border="0" width="100%"  cellspacing="0" cellpadding="0" style="font-size:14px; font-family:verdana;">
            <tr style="height:60px;">
                <td width="5%">&nbsp;</td>
                <td style="text-align:left;">Signature Attested</td>
                <td>&nbsp; </td>
                <td style="text-align:right;">Yours Faithfully,</td>
                <td>&nbsp;</td>
                <td width="5%">&nbsp;</td>
            </tr>
            <tr style="height:60px;">
                <td>&nbsp; </td>
                <td style="text-align:left;">Signature of</td>
                <td>&nbsp; </td>
                <td style="text-align:right;">Drawing and Disbursment Officer</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
            </tr>
            <tr style="height:60px;">
                <td>&nbsp; </td>
                <td style="text-align:left;">Drawing and Disbursment Officer</td>
                <td>&nbsp; </td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
            </tr>
        </table>
                
    </div>  
</body>
</html>
