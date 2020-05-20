<div class="mt-3">
    <c:choose>
        <c:when test="${errors['photos'].size() gt 0}">
            <div class="mb-3">Fix errors and <a href="<c:url value='/zipReupload' />">click</a> to upload a photo zip file again (may only contain the following photos) </div>
            <div class="row mb-3">
                <c:forEach var="name" items="${errors['photos']}" varStatus="status">
                    <div class="col-sm-1">${name}</div>
                </c:forEach>
            </div>
        </c:when>
        <c:otherwise>
            <div class="mb-3">
                Photos have been uploaded. <a href="<c:url value='/zipReupload' />">Click</a> if you have more photo zip files to upload
            </div>
        </c:otherwise>
    </c:choose>
</div>
<hr/>