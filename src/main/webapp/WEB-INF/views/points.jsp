<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        <title>Points Json</title>
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.2.1/css/bootstrap.min.css">
        <style><%@include file="/WEB-INF/css/nav-top-fixed.css"%></style>
    </head>
    <body>
        <%@include file="/WEB-INF/views/navbar.html"%>
        <h3>Points Json File</h3>
        <table>
            <tr>
                <c:choose>
                    <c:when test="${next}">
                        <td>Download Json File:</td>
                        <td><a href="<c:url value='/downloads/points' />">points.json</a></td>
                    </c:when>
                    <c:otherwise>
                        <form:form method="POST" action="/scsd/generatePoints" enctype="multipart/form-data">
                            <table>
                                <tr>
                                    <td><input type="submit" value="Generate" /></td>
                                </tr>
                            </table>
                        </form:form>
                    </c:otherwise>
                </c:choose>
            </tr>
        </table>

        <script src="https://code.jquery.com/jquery-3.2.1.slim.min.js"></script>
        <script>window.jQuery || document.write('<script src="../../assets/js/vendor/jquery-slim.min.js"><\/script>')</script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.6/umd/popper.min.js"></script>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.2.1/js/bootstrap.min.js"></script>
    </body>
</html>
