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
            <h3>CS Upload</h3>
            <c:choose>
                <c:when test="${next.get('collection') == 'collection' && next.get('csd') == 'csd' && next.get('photos') == 'photos'}">
                    <%@ include file="/WEB-INF/views/cs/collectionValidate.jsp" %>
                    <%@ include file="/WEB-INF/views/cs/csdValidate.jsp" %>
                    <%@ include file="/WEB-INF/views/cs/photosValidate.jsp" %>
                    <a class="btn btn-primary" href="/scsd/createSAF" role="button">Create SAF Package</a>
                </c:when>
                <c:when test="${next.get('collection') == 'collection' && next.get('csd') == 'csd'}">
                    <%@ include file="/WEB-INF/views/cs/collectionValidate.jsp" %>
                    <%@ include file="/WEB-INF/views/cs/csdValidate.jsp" %>
                    <c:choose>
                        <c:when test="${errors['photos'].size() gt 0}">
                            <c:set var="errors" value="${errors}" scope="request" />
                            <%@ include file="/WEB-INF/views/cs/photosValidate.jsp" %>
                        </c:when>
                        <c:otherwise>
                            <%@ include file="/WEB-INF/views/cs/photos.jsp" %>
                        </c:otherwise>
                    </c:choose>
                </c:when>
                <c:when test="${next.get('collection') == 'collection' && next.get('photos') == 'photos'}">
                    <%@ include file="/WEB-INF/views/cs/collectionValidate.jsp" %>
                    <c:choose>
                        <c:when test="${errors['csvErrorFlag'].size() gt 0}">
                            <c:set var="alert" value="${alert}" scope="request" />
                            <c:set var="errors" value="${errors}" scope="request" />
                            <c:set var="zip" value="${zip}" scope="request" />
                            <%@ include file="/WEB-INF/views/cs/csdValidate.jsp" %>
                        </c:when>
                        <c:otherwise>
                            <%@ include file="/WEB-INF/views/cs/csd.jsp" %>
                        </c:otherwise>
                    </c:choose>
                    <%@ include file="/WEB-INF/views/cs/photosValidate.jsp" %>
                </c:when>
                <c:when test="${next.get('csd') == 'csd' && next.get('photos') == 'photos'}">
                    <c:choose>
                        <c:when test="${next.get('collection') == 'collection'}">
                            <%@ include file="/WEB-INF/views/cs/collectionValidate.jsp" %>
                        </c:when>
                        <c:otherwise>
                            <%@ include file="/WEB-INF/views/cs/collection.jsp" %>
                        </c:otherwise>
                    </c:choose>
                    <%@ include file="/WEB-INF/views/cs/csdValidate.jsp" %>
                    <%@ include file="/WEB-INF/views/cs/photosValidate.jsp" %>
                </c:when>
                <c:when test="${next.get('collection') == 'collection'}">
                    <%@ include file="/WEB-INF/views/cs/collectionValidate.jsp" %>
                    <c:choose>
                        <c:when test="${errors['csvErrorFlag'].size() gt 0}">
                            <c:set var="alert" value="${alert}" scope="request" />
                            <c:set var="errors" value="${errors}" scope="request" />
                            <c:set var="zip" value="${zip}" scope="request" />
                            <%@ include file="/WEB-INF/views/cs/csdValidate.jsp" %>
                        </c:when>
                        <c:otherwise>
                            <%@ include file="/WEB-INF/views/cs/csd.jsp" %>
                        </c:otherwise>
                    </c:choose>
                    <c:choose>
                        <c:when test="${errors['photos'].size() gt 0}">
                            <c:set var="errors" value="${errors}" scope="request" />
                            <%@ include file="/WEB-INF/views/cs/photosValidate.jsp" %>
                        </c:when>
                        <c:otherwise>
                            <%@ include file="/WEB-INF/views/cs/photos.jsp" %>
                        </c:otherwise>
                    </c:choose>
                </c:when>
                <c:when test="${next.get('csd') == 'csd'}">
                    <c:choose>
                        <c:when test="${next.get('collection') == 'collection'}">
                            <%@ include file="/WEB-INF/views/cs/collectionValidate.jsp" %>
                        </c:when>
                        <c:otherwise>
                            <%@ include file="/WEB-INF/views/cs/collection.jsp" %>
                        </c:otherwise>
                    </c:choose>
                    <%@ include file="/WEB-INF/views/cs/csdValidate.jsp" %>
                    <c:choose>
                        <c:when test="${errors['photos'].size() gt 0}">
                            <c:set var="errors" value="${errors}" scope="request" />
                            <%@ include file="/WEB-INF/views/cs/photosValidate.jsp" %>
                        </c:when>
                        <c:otherwise>
                            <%@ include file="/WEB-INF/views/cs/photos.jsp" %>
                        </c:otherwise>
                    </c:choose>
                </c:when>
                <c:when test="${next.get('photos') == 'photos'}">
                    <c:choose>
                        <c:when test="${next.get('collection') == 'collection'}">
                            <%@ include file="/WEB-INF/views/cs/collectionValidate.jsp" %>
                        </c:when>
                        <c:otherwise>
                            <%@ include file="/WEB-INF/views/cs/collection.jsp" %>
                        </c:otherwise>
                    </c:choose>
                    <c:choose>
                        <c:when test="${errors['csvErrorFlag'].size() gt 0}">
                            <c:set var="alert" value="${alert}" scope="request" />
                            <c:set var="errors" value="${errors}" scope="request" />
                            <c:set var="zip" value="${zip}" scope="request" />
                            <%@ include file="/WEB-INF/views/cs/csdValidate.jsp" %>
                        </c:when>
                        <c:otherwise>
                            <%@ include file="/WEB-INF/views/cs/csd.jsp" %>
                        </c:otherwise>
                    </c:choose>
                    <%@ include file="/WEB-INF/views/cs/photosValidate.jsp" %>
                </c:when>
                <c:otherwise>
                    <%@ include file="/WEB-INF/views/cs/collection.jsp" %>
                    <c:choose>
                        <c:when test="${ errors['csvErrorFlag'].size() gt 0}">
                            <c:set var="alert" value="${alert}" scope="request" />
                            <c:set var="errors" value="${errors}" scope="request" />
                            <c:set var="zip" value="${zip}" scope="request" />
                            <%@ include file="/WEB-INF/views/cs/csdValidate.jsp" %>
                        </c:when>
                        <c:otherwise>
                            <%@ include file="/WEB-INF/views/cs/csd.jsp" %>
                        </c:otherwise>
                    </c:choose>
                    <c:choose>
                        <c:when test="${errors['photos'].size() gt 0}">
                            <c:set var="errors" value="${errors}" scope="request" />
                            <%@ include file="/WEB-INF/views/cs/photosValidate.jsp" %>
                        </c:when>
                        <c:otherwise>
                            <%@ include file="/WEB-INF/views/cs/photos.jsp" %>
                        </c:otherwise>
                    </c:choose>
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
