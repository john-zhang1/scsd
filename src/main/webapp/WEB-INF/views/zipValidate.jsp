<%@ page contentType = "text/html; charset = UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        <title>SAF Page Step 2</title>
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.2.1/css/bootstrap.min.css">
        <style><%@include file="/WEB-INF/css/nav-top-fixed.css"%></style>
    </head>

    <body>
        <%@include file="/WEB-INF/views/navbar.html"%>
        <h2>Photo Name Validation Results:</h2>
        <table>
            <tr>
                <td>Photo List:</td>
                <td>${photoNames.toString()}</td>
            </tr>
            <tr>
                <td>Invalid Photo Names:</td>
                <td>${formatErrors.get("invalidNames").toString()}</td>
            </tr>
            <tr>
                <td>Items don't have required two photos:</td>
                <td>${formatErrors.get("invalidPairs").toString()}</td>
            </tr>
            <tr>
                <c:choose>
                    <c:when test="${next}">
                        <td>Create SAF Package</td>
                        <td><a href="<c:url value='/createSAF' />">Create SAF Package</a></td>
                        <td><a href="<c:url value='/zipUpload' />">Upload a new zip file</a></td>
                    </c:when>
                    <c:otherwise>
                        <td><a href="<c:url value='/zipReturn' />">Correct the photo names and upload zip file again</a></td>
                    </c:otherwise>
                </c:choose>

            </tr>
        </table>

        <script src="https://code.jquery.com/jquery-3.2.1.slim.min.js" integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN" crossorigin="anonymous"></script>
        <script>window.jQuery || document.write('<script src="../../assets/js/vendor/jquery-slim.min.js"><\/script>')</script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.6/umd/popper.min.js"></script>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.2.1/js/bootstrap.min.js"></script>
   </body>

</html>