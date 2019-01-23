<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
 
<html>
 
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Employee Registration Form</title>
 
<style>
 
    .error {
        color: #ff0000;
    }
</style>
 
</head>
 
<body>
 
    <h2>Registration Form</h2>
  
    <form:form method="POST" modelAttribute="zip">
        <form:input type="hidden" path="id" id="id"/>
        <table>
            <tr>
                <td><label for="name">Zip Code </label> </td>
                <td><form:input path="zip" id="zip"/></td>
                <td><form:errors path="zip" cssClass="error"/></td>
            </tr>

            <tr>
                <td><label for="city">City: </label> </td>
                <td><form:input path="city" id="city"/></td>
                <td><form:errors path="city" cssClass="error"/></td>
            </tr>

            <tr>
                <td><label for="shortState">State: </label> </td>
                <td><form:input path="shortState" id="shortState"/></td>
                <td><form:errors path="shortState" cssClass="error"/></td>
            </tr>

            <tr>
                <td><label for="latitude">Latitude </label> </td>
                <td><form:input path="latitude" id="latitude"/></td>
                <td><form:errors path="latitude" cssClass="error"/></td>
            </tr>

            <tr>
                <td><label for="longitude">Longitude </label> </td>
                <td><form:input path="longitude" id="longitude"/></td>
                <td><form:errors path="longitude" cssClass="error"/></td>
            </tr>

            <tr>
                <td colspan="3">
                    <c:choose>
                        <c:when test="${edit}">
                            <input type="submit" value="Update"/>
                        </c:when>
                        <c:otherwise>
                            <input type="submit" value="Register"/>
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>
        </table>
    </form:form>
    <br/>
    <br/>
    Go back to <a href="<c:url value='/ziplist' />">List of All Zips</a>
</body>
</html>