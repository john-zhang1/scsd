<%@ page contentType = "text/html; charset = UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        <title>SAF Downloads</title>
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.2.1/css/bootstrap.min.css">
        <style><%@include file="/WEB-INF/css/nav-top-fixed.css"%></style>
    </head>
    <body>
        <%@include file="/WEB-INF/views/navbar.html"%>

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

        <script src="https://code.jquery.com/jquery-3.2.1.slim.min.js" integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN" crossorigin="anonymous"></script>
        <script>window.jQuery || document.write('<script src="../../assets/js/vendor/jquery-slim.min.js"><\/script>')</script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.6/umd/popper.min.js"></script>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.2.1/js/bootstrap.min.js"></script>
    </body>
</html>
