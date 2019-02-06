<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>File Upload</title>
    </head>
    <body>
        <h3>Enter The File to Upload (Single file)</h3>

        <form:form method="POST" action="/scsd/fileUpload" enctype="multipart/form-data">

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

        <br /> 

        <h3>Enter The Files to Upload (Multiple files)</h3>

        <form:form method="POST" action="/scsd/uploadMultiFile" enctype="multipart/form-data">

            <table>
                <tr>
                    <td>Select a file to upload</td>
                    <td><input type="file" name="files" /></td>
                </tr>
                <tr>
                    <td>Select a file to upload</td>
                    <td><input type="file" name="files" /></td>
                </tr>
                <tr>
                    <td>Select a file to upload</td>
                    <td><input type="file" name="files" /></td>
                </tr>
                <tr>
                    <td><input type="submit" value="Submit" /></td>
                </tr>
            </table>

        </form:form>
        <br />

        <h3>Fill the Form and Select a File (<code>@RequestParam</code>)</h3>

        <form:form method="POST" action="/scsd/uploadFileWithAddtionalData" enctype="multipart/form-data">

            <table>
                <tr>
                    <td>Name</td>
                    <td><input type="text" name="name" /></td>
                </tr>
                <tr>
                    <td>Email</td>
                    <td><input type="text" name="email" /></td>
                </tr>
                <tr>
                    <td>Select a file to upload</td>
                    <td><input type="file" name="file" /></td>
                </tr>
                <tr>
                    <td><input type="submit" value="Submit" /></td>
                </tr>
            </table>

        </form:form>


        <h3>Fill the Form and Select a File (<code>@ModelAttribute</code>)</h3>

        <form:form method="POST" action="/scsd/uploadFileModelAttribute" enctype="multipart/form-data">

            <table>
                <tr>
                    <td>Name</td>
                    <td><input type="text" name="name" /></td>
                </tr>
                <tr>
                    <td>Email</td>
                    <td><input type="text" name="email" /></td>
                </tr>
                <tr>
                    <td>Select a file to upload</td>
                    <td><input type="file" name="file" /></td>
                </tr>
                <tr>
                    <td><input type="submit" value="Submit" /></td>
                </tr>
            </table>

        </form:form>

        <form:form method="POST" action="/scsd/csvFileUpload" enctype="multipart/form-data">

            <table>
                <tr>
                    <td>Select a CSV file to upload</td>
                    <td><input type="file" name="file" /></td>
                </tr>
                <tr>
                    <td><input type="submit" value="Submit" /></td>
                </tr>
            </table>

        </form:form>
	
        <form:form method="POST" action="/scsd/zipFileUpload" enctype="multipart/form-data">

            <table>
                <tr>
                    <td>Select a ZIP file to upload</td>
                    <td><input type="file" name="file" /></td>
                </tr>
                <tr>
                    <td><input type="submit" value="Submit" /></td>
                </tr>
            </table>

        </form:form>
	
    </body>
</html>
