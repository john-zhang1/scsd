<%@ page contentType = "text/html; charset = UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>SAF Downloads</title>
    </head>
    <body>

        <h2>SAF Package Downloads:</h2>
        <table>
            <tr>
                <td>New Data SAF:</td>
            </tr>
            <tr>
                <td>${file_new.getName()}</td><td>(${file_new.length()})</td>
            </tr>
            <tr>
                <td><a href="<c:url value='/downloads/saf-new' />">Download New SAF</a></td>
            </tr>
            <br />
            <tr>
                <td>Existing Data SAF:</td>
            </tr>
            <tr>
                <td>${file_exist.getName()}</td><td>(${file_exist.length()})</td>
            </tr>
            <tr>
                <td><a href="<c:url value='/downloads/saf-existing' />">Download Existing SAF</a></td>
            </tr>
        </table>
    </body>
</html>
