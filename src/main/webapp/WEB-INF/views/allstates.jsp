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
    <h2>List of Employees</h2>  
    <table>
        <tr>
            <td>ID</td><td>Short Name</td><td>Long Name</td></td>
        </tr>
        <c:forEach items="${states}" var="state">
            <tr>
            <td>${state.stateId}</td>
            <td>${state.shortName}</td>
            <td>${state.longName}</td>
            <td><a href="<c:url value='/edit-${state.stateId}-state' />">${state.longName}</a></td>
            <td><a href="<c:url value='/delete-${state.shortName}-state' />">delete</a></td>
            </tr>
        </c:forEach>
    </table>
    <br/>
    <a href="<c:url value='/new' />">Add New State</a>
    <a href="<c:url value='/csvFileUpload' />">Validate CSV</a>
</body>
</html>