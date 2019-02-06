<%@ page contentType = "text/html; charset = UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
    <head>
        <title>File Upload Example</title>
    </head>
   <body>
        <h2>Submitted File (Single)</h2>
        <table>
            <tr>
                <td>OriginalFileName :</td>
                <td>${file.originalFilename}</td>
            </tr>
            <tr>
                <td>Type :</td>
                <td>${file.contentType}</td>
            </tr>
        </table>
        <br />

        <h2>Submitted Files (Multiple)</h2>
        <table>
            <c:forEach items="${files}" var="file">	
                <tr>
                    <td>OriginalFileName :</td>
                    <td>${file.originalFilename}</td>
                </tr>
                <tr>
                    <td>Type :</td>
                    <td>${file.contentType}</td>
                    </tr>
            </c:forEach>
        </table>
        <br />

        <h2>Submitted File with Data (<code>@RequestParam</code>)</h2>
        <table>
            <tr>
                <td>Name :</td>
                <td>${name}</td>
            </tr>
            <tr>
                <td>Email :</td>
                <td>${email}</td>
            </tr>
            <tr>
                <td>OriginalFileName :</td>
                <td>${file.originalFilename}</td>
            </tr>
            <tr>
                <td>Type :</td>
                <td>${file.contentType}</td>
            </tr>
        </table>

        <br />

        <h2>Submitted File with Data (<code>@ModelAttribute</code>)</h2>
        <table>
            <tr>
                <td>Name :</td>
                <td>${formDataWithFile.name}</td>
            </tr>
            <tr>
                <td>Email :</td>
                <td>${formDataWithFile.email}</td>
            </tr>
            <tr>
                <td>OriginalFileName :</td>
                <td>${formDataWithFile.file.originalFilename}</td>
            </tr>
            <tr>
                <td>Type :</td>
                <td>${formDataWithFile.file.contentType}</td>
            </tr>
        </table>

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
        </table>

        <h2>ZIP file upload:</h2>
        <table>
            <tr>
                <td>OriginalFileName :</td>
                <td>${file.originalFilename}</td>
            </tr>
            <tr>
                <td>Type :</td>
                <td>${file.contentType}</td>
            </tr>
        </table>


        <h2>Photo Name Validation Results:</h2>
        <table>
            <tr>
                <td>OriginalFileName :</td>
                <td>${file.originalFilename}</td>
            </tr>
            <tr>
                <td>Type :</td>
                <td>${file.contentType}</td>
            </tr>
            <tr>
                <td>Photo List:</td>
                <td>${photoNames.toString()}</td>
            </tr>
            <tr>
                <td>Invalid Photo Names:</td>
                <td>${formatErrors.get("invalidNames").toString()}</td>
            </tr>
            <tr>
                <td>Items don't have required two photos:</td>
                <td>${formatErrors.get("invalidPairs").toString()}</td>
            </tr>
            <tr>
                <td>Download zip file:</td>
                <td><a href="<c:url value='/downloads/zip/${targetFile}' />">Download SAF</a></td>
            </tr>
        </table>

   </body>

</html>