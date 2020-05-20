<c:choose>
    <c:when test="${taxErrors.size() gt 0}">
        <div class="row mb-3"><div class="col">Correct errors in items below and <a href="/scsd/linkReupload">click</a> to upload again</div></div>
        <c:forEach var="error" items="${taxErrors}" varStatus="status">
            <div class="row">
                <c:forEach var="element" items="${error.value}" varStatus="theCount">
                    <div class="col-sm-${theCount.count * 2 - 1}">${element}</div>
                </c:forEach>
            </div>
        </c:forEach>
    </c:when>
    <c:otherwise>
        <div class="mb-3">New data csv file has been uploaded</div>
    </c:otherwise>
</c:choose>