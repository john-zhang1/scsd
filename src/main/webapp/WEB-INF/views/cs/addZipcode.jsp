<div class="container mt-3">
    <c:choose>
        <c:when test="${not empty alert['warning']}">
            <div class="mb-3 alert alert-warning">${alert['warning']}</div>
        </c:when>
        <c:when test="${not empty alert['success']}">
            <div class="mb-3 alert alert-success">${alert['success']}</div>
        </c:when>
        <c:otherwise>
        </c:otherwise>
    </c:choose>
    <form:form method="POST" action="/scsd/addZipCode" modelAttribute="zip">
        <form:input type="hidden" path="id" id="id"/>
        <div class="form-group">
            <label for="name">Zip Code</label>
            <form:input class="form-control" id="zip" path="zip" placeholder="73019"/>
            <form:errors path="zip" cssClass="text-danger"/>
        </div>
        <div class="form-group">
            <label for="name">City</label>
            <form:input class="form-control" id="city" path="city" placeholder="Norman"/>
            <form:errors path="city" cssClass="text-danger"/>
        </div>
        <div class="form-group">
            <label for="name">State</label>
            <form:input class="form-control" id="shortState" path="shortState" placeholder="OK"/>
            <form:errors path="shortState" cssClass="text-danger"/>
        </div>
        <div class="form-group">
            <label for="name">Latitude</label>
            <form:input class="form-control" id="latitude" path="latitude" placeholder="35.208566"/>
            <form:errors path="latitude" cssClass="text-danger"/>
        </div>
        <div class="form-group">
            <label for="name">Longitude</label>
            <form:input class="form-control" id="longitude" path="longitude" placeholder="-97.444510"/>
            <form:errors path="longitude" cssClass="text-danger"/>
        </div>
        <c:choose>
            <c:when test="${not empty zip.id}">
                <button type="submit" class="btn btn-primary">Update</button>
                <a class="btn btn-primary" href="/scsd/newZipCode" role="button">New</a>
            </c:when>
            <c:otherwise>
                <button type="submit" class="btn btn-primary">Add</button>
            </c:otherwise>
        </c:choose>
    </form:form>
</div>