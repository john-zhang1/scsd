<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>SAF Upload Form</title>
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
        <div class="container">
            <h3>Taxonomy Upload</h3>
            <c:choose>
                <c:when test="${taxNext.get('collection') == 'collection' && taxNext.get('link') == 'link'}">
                    <%@ include file="/WEB-INF/views/cs/taxCollectionValidate.jsp" %>
                    <%@ include file="/WEB-INF/views/cs/linkValidate.jsp" %>
                    <a class="btn btn-primary" href="/scsd/createTaxSAF" role="button">Create SAF Package</a>
                </c:when>
                <c:when test="${taxNext.get('collection') == 'collection'}">
                    <%@ include file="/WEB-INF/views/cs/taxCollectionValidate.jsp" %>
                    <c:choose>
                        <c:when test="${taxErrors.size() gt 0}">
                            <c:set var="taxErrors" value="${taxErrors}" scope="request" />
                            <%@ include file="/WEB-INF/views/cs/linkValidate.jsp" %>
                        </c:when>
                        <c:otherwise>
                            <%@ include file="/WEB-INF/views/cs/link.jsp" %>
                        </c:otherwise>
                    </c:choose>
                </c:when>
                <c:when test="${taxNext.get('link') == 'link'}">
                    <%@ include file="/WEB-INF/views/cs/taxCollection.jsp" %>
                    <%@ include file="/WEB-INF/views/cs/linkValidate.jsp" %>
                </c:when>
                <c:otherwise>
                    <%@ include file="/WEB-INF/views/cs/taxCollection.jsp" %>
                </c:otherwise>
            </c:choose>
        </div>

        <script>
        // Add the following code if you want the name of the file appear on select
        $(".custom-file-input").on("change", function() {
          var fileName = $(this).val().split("\\").pop();
          $(this).siblings(".custom-file-label").addClass("selected").html(fileName);
        });
        </script>

    </body>
</html>