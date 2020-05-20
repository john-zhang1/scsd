<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <title>Taxonomy SAF Download</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css">
        <style><%@ include file="/WEB-INF/css/nav-top-fixed.css"%></style>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>
        <script src="https://code.jquery.com/jquery-3.2.1.slim.min.js"></script>
        <script>window.jQuery || document.write('<script src="../../assets/js/vendor/jquery-slim.min.js"><\/script>')</script>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js"></script>
    </head>
    <body>
        <%@include file="/WEB-INF/views/navbar.html"%>

        <div class="container mt-3">
            <h3>Taxonomy SAF Download</h3>
            <jsp:useBean id="cutils" class="org.csscience.cssaf.utils.CommonUtils"/>
            <div class="mt-3">${file.getName()} (${cutils.readableFileSize(file.length())})</div>
            <div class="mt-2"><a href="<c:url value='/downloads/saf-taxonomy' />" class="btn btn-outline-primary">Download ${file.getName()}</a></div>
        </div>
    </body>
</html>