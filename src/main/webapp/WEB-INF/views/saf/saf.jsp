<form:form method="POST" action="/scsd/zipSafUpload" enctype="multipart/form-data">
    <div class="custom-file mt-4">
      <input type="file" class="custom-file-input" name="file">
      <label class="custom-file-label" for="file">Choose a zip file containing metadata csv and files</label>
      <input type="hidden" name="uploadFileName" value="safData.zip" />
      <input type="hidden" name="sessionid" value=<% out.print( session.getId()); %> />
    </div>

    <div class="mt-3">
      <button type="submit" class="btn btn-primary">Submit</button>
    </div>
</form:form>