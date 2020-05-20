<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>SAF Builder</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.2.1/css/bootstrap.min.css">
    <style><%@include file="/WEB-INF/css/nav-top-fixed.css"%></style>
</head>

  <body>
    <%@include file="/WEB-INF/views/navbar.html"%>
    <%--<%@include file="/WEB-INF/views/images/logo.png"%>--%>

    <main role="main" class="container">
      <div class="jumbotron">
        <h1>SAF Builder</h1>
        <p class="lead">This web application is a helper tool to validate metadata and generate Simple Archive Packages for data ingestion in DSpace collections.</p>
        <!--<a class="btn btn-lg btn-primary" href="../../components/navbar/" role="button">View navbar docs</a>-->
      </div>
    </main>

    <!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="https://code.jquery.com/jquery-3.2.1.slim.min.js"></script>
    <script>window.jQuery || document.write('<script src="../../assets/js/vendor/jquery-slim.min.js"><\/script>')</script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.6/umd/popper.min.js"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.2.1/js/bootstrap.min.js"></script>
</body></html>