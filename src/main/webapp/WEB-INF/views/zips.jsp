<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>University Enrollments</title>
 
    <style>
        tr:first-child{
            font-weight: bold;
            background-color: #C6C9C4;
        }
    </style>
 
</head>
 
 
<body>
    <h2>List of Zips</h2>  
    <table>
        <tr>
            <td>ID</td><td>Zip</td><td>City</td><td>State</td><td>Latitude</td><td>Longitude</td>
        </tr>
        <c:forEach items="${zips}" var="zip">
            <tr>
            <td>${zip.id}</td>
            <td>${zip.zip}</td>
            <td>${zip.city}</td>
            <td>${zip.shortState}</td>
            <td>${zip.latitude}</td>
            <td>${zip.longitude}</td>
            <td><a href="<c:url value='/edit-${zip.zip}-zip' />">${zip.zip}</a></td>
            <td><a href="<c:url value='/delete-${zip.zip}-zip' />">delete</a></td>
            </tr>
        </c:forEach>
    </table>
    <br/>
    <a href="<c:url value='/newzip' />">Add New Zip</a>
</body>
</html>