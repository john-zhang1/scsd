<%@ page contentType = "text/html; charset = UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <title>SAF Page Step 2</title>
    </head>

    <body>
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
                <c:if test="${next}">
                    <td>Create SAF Package</td>
                    <td><a href="<c:url value='/createSAF' />">Create SAF Package</a></td>
                </c:if>
            </tr>
        </table>

   </body>

</html>