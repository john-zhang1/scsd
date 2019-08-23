<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        <title>Citizen Science Soil Project Taxonomy</title>
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.2.1/css/bootstrap.min.css">
        <style><%@include file="/WEB-INF/css/nav-top-fixed.css"%></style>
    </head>
    <body>
        <%@include file="/WEB-INF/views/navbar.html"%>
        <h3>Taxonomy File</h3>
        <table>
            <c:choose>
                <c:when test="${generated}">
                    <tr><td>Download Taxonomy SAF:</td></tr>
                    <tr><td>${file.getName()}</td><td>(${file.length()})</td></tr>
                    <tr><td><a href="<c:url value='/downloads/saf-taxonomy' />">saf-taxonomy</a></td></tr>
                </c:when>
                <c:otherwise>
                    <c:choose>
                        <c:when test="${next}">
                            <tr><td>Uploaded file:</td></tr>
                            <tr><td>${file.originalFilename}</td></tr>
                            <tr>
                                <td>Invalid Internal Code</td>
                                <td>${errors.get("invalidInternalCode").toString()}</td>
                            </tr>
                            <tr>
                                <td>Generate Taxonomy SAF Package:</td>
                                <td><a href="<c:url value='/generateTax' />">Generate saf-taxonomy</a></td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:choose>
                                <c:when test="${upload}">
                                    <tr><td>Uploaded file:</td>
                                    <tr><td>${file.originalFilename}</td><tr>
                                    <tr>
                                        <td>Invalid Internal Code</td>
                                        <td>${errors.get("invalidInternalCode").toString()}</td>
                                    </tr>
                                    <tr><td><a href="<c:url value='/taxonomies' />">Correct the errors and upload file again</a></td></tr>
                                </c:when>
                                <c:otherwise>
                                    <tr><td>
                                        <form:form method="POST" action="/scsd/uploadTax" enctype="multipart/form-data">
                                            <table>
                                                <tr>
                                                    <td>Select a taxonomy csv file to upload</td>
                                                    <td><input type="file" name="file" /></td>
                                                </tr>
                                                <tr>
                                                    <td><input type="submit" value="Submit" /></td>
                                                </tr>
                                            </table>
                                        </form:form>
                                    </td></tr>
                                </c:otherwise>
                            </c:choose>
                        </c:otherwise>
                    </c:choose>
                </c:otherwise>
            </c:choose>
        </table>

        <script src="https://code.jquery.com/jquery-3.2.1.slim.min.js" integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN" crossorigin="anonymous"></script>
        <script>window.jQuery || document.write('<script src="../../assets/js/vendor/jquery-slim.min.js"><\/script>')</script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.6/umd/popper.min.js"></script>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.2.1/js/bootstrap.min.js"></script>
    </body>
</html>
