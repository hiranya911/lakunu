<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%--@elvariable id="course" type="org.lakunu.web.data.Course"--%>
<%--@elvariable id="lab" type="org.lakunu.web.data.Lab"--%>

<c:if test="${lab.published}">
    <shiro:hasPermission name="lab:update:${course.id}:${lab.id}">
        <div class="row">
            <div class="col-md-12">
                <div class="alert alert-info">
                    This lab is published. You cannot make changes to the lab while it's published.
                </div>
            </div>
        </div>
    </shiro:hasPermission>
    <shiro:lacksPermission name="lab:update:${course.id}:${lab.id}">
        <div class="row">
            <div class="col-md-12">
                <div class="alert alert-info">
                    This lab is published. It is available for receiving submissions.
                </div>
            </div>
        </div>
    </shiro:lacksPermission>
</c:if>
<c:if test="${not lab.published}">
    <div class="row">
        <div class="col-md-12">
            <div class="alert alert-info">
                This lab is not yet published. It cannot receive any submissions until it is published.
            </div>
        </div>
    </div>
</c:if>
<div class="row">
    <div class="col-md-12">
        <div class="panel panel-primary">
            <div class="panel-heading">Configuration</div>
            <div class="panel-body">
                <textarea name="labConfig" id="labConfig" class="form-control" rows="10" readonly>${labConfigString}</textarea>
            </div>
        </div>
    </div>
</div>
<div class="row">
    <div class="col-md-12">
        <div class="panel panel-primary">
            <div class="panel-heading">Details</div>
            <div class="panel-body">
                <table class="table table-striped">
                    <tbody>
                    <tr>
                        <td>ID</td>
                        <td><c:out value="${lab.id}"/></td>
                    </tr>
                    <tr>
                        <td>Name</td>
                        <td><c:out value="${lab.name}"/></td>
                    </tr>
                    <tr>
                        <td>Description</td>
                        <td><c:out value="${lab.description}"/></td>
                    </tr>
                    <tr>
                        <td>Created</td>
                        <td><c:out value="${lab.createdAt}"/></td>
                    </tr>
                    <tr>
                        <td>Created By</td>
                        <td><c:out value="${lab.createdBy}"/></td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
<div class="row">
    <div class="col-md-3">
        <shiro:hasPermission name="lab:publish:${course.id}:${lab.id}">
            <c:if test="${lab.published}">
                <button type="button" class="btn btn-primary">Unpublish</button>
            </c:if>
        </shiro:hasPermission>
    </div>
</div>