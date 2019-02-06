<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix = "form" uri = "http://www.springframework.org/tags/form"%>

<!DOCTYPE html>
<html>
   <head>
      <title>File Upload Example</title>
   </head>

   <body>
      <form:form method = "POST" modelAttribute = "file" enctype = "multipart/form-data">
         Please select a file to upload : 
         <input type = "file" name = "file" />
         <input type = "submit" value = "upload" />
      </form:form>
   </body>
</html>

