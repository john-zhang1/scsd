<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>SAF Upload Form</title>
    </head>
    <body>
        <h3>Upload CSV file to Validate</h3>

        <form:form method="POST" action="/scsd/csvFileUpload" enctype="multipart/form-data">
            <table>
                <tr>
                    <td>Select a file to upload</td>
                    <td><input type="file" name="file" /></td>
                </tr>
                <tr>
                    <td><input type="submit" value="Submit" /></td>
                </tr>
            </table>
        </form:form>

    </body>
</html>
