<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
 
<html>
 
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>State Registration Form</title>
 
<style>
 
    .error {
        color: #ff0000;
    }
</style>
 
</head>
 
<body>
 
    <h2>Registration Form</h2>
  
    <form:form method="POST" modelAttribute="state">
        <form:input type="hidden" path="stateId" id="stateId"/>
        <table>
            <tr>
                <td><label for="shortName">Short Name: </label> </td>
                <td><form:input path="shortName" id="shortName"/></td>
                <td><form:errors path="shortName" cssClass="error"/></td>
            </tr>
         
            <tr>
                <td><label for="longName">Full Name: </label> </td>
                <td><form:input path="longName" id="longName"/></td>
                <td><form:errors path="longName" cssClass="error"/></td>
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
    Go back to <a href="<c:url value='/statelist' />">List of All States</a>
</body>
</html>