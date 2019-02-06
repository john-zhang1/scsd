<%@ page contentType = "text/html; charset = UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <title>SAF Page Step 1</title>
    </head>

    <body>

        <h2>CSV Validation Results:</h2>
        <table>
            <tr>
                <td>Headings contain wrong columns:</td>
                <td>${errors.get("invalidHeadings").toString()}</td>
            </tr>
            <tr>
                <td>Samples containing wrong information:</td>
                <td>${errors.get("invalidZipFormat").toString()}</td>
            </tr>
            <tr>
                <td>OriginalFileName :</td>
                <td>${file.originalFilename}</td>
            </tr>
            <tr>
                <td>Type :</td>
                <td>${file.contentType}</td>
            </tr>
            <tr>
                <c:if test="${next}">
                    <td>Upload photo zip file:</td>
                    <td><a href="<c:url value='/zipUpload' />">Upload Zip File</a></td>                        
                </c:if>
            </tr>
        </table>
   </body>

</html>
