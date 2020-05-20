<style>
    .form-control::-webkit-input-placeholder {
        color: #bbb;
    }
</style>

<c:choose>
    <c:when test="${errors['csvErrorFlag'].size() gt 0}">
        <ul class="nav nav-tabs mt-3 mb-3 " id="zipTab" role="tablist">
            <li class="nav-item">
                <a class="nav-link active" id="csvupload-tab" data-toggle="tab" href="#csvupload" role="tab" aria-controls="csvupload" aria-selected="true">CSV Upload</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" id="addzipcode-tab" data-toggle="tab" href="#addzipcode" role="tab" aria-controls="addzipcode" aria-selected="false">Zip Code</a>
            </li>
        </ul>
        <div class="tab-content" id="zipTabContent">
            <div class="tab-pane fade show active" id="csvupload" role="tabpanel" aria-labelledby="csvupload-tab">
                <c:choose>
                    <c:when test="${errors['csvErrorFlag'].size() gt 0}">
                        <div class="row mb-3"><div class="col">Correct errors in items below and <a href="/scsd/csdReupload">click</a> to upload again</div></div>
                        <c:forEach var="error" items="${errors}" varStatus="status">
                            <div class="row">
                                <c:if test="${error.key ne 'csvErrorFlag' && error.key ne 'photos'}">
                                    <c:forEach var="element" items="${error.value}">
                                        <div class="col-sm-1">${element}</div>
                                    </c:forEach>
                                </c:if>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <div>New data csv file has been uploaded</div>
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="tab-pane fade" id="addzipcode" role="tabpanel" aria-labelledby="addzipcode-tab">
                <c:set var="zip" value="${zip}" scope="request" />
                <c:set var="alert" value="${alert}" scope="request" />
                <%@ include file="/WEB-INF/views/cs/addZipcode.jsp" %>
            </div>
        </div>
    </c:when>
    <c:otherwise>
        <div class="mt-3">
            New data csv file has been uploaded
        </div>
    </c:otherwise>
</c:choose>
<hr/>

<script>
$(function() {
    $('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
        localStorage.setItem('activeTab', $(e.target).attr('href'));
    });
    var activeTab = localStorage.getItem('activeTab');
    if(activeTab){
        $('.nav-tabs a[href="' + activeTab + '"]').tab('show');
    }
});
</script>