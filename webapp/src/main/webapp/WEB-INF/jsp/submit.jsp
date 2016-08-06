<%--@elvariable id="lab" type="org.lakunu.web.models.Lab"--%>
<%--@elvariable id="course" type="org.lakunu.web.models.Course"--%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <%@ include file="/WEB-INF/include/header.html" %>
    <title>Lakunu: Submit Lab [${lab.name}]</title>
</head>
<body>
<%@ include file="/WEB-INF/include/body_top.html" %>
<div class="container">
    <h3>${lab.name} <small><a href="/course/${lab.courseId}">(Back to ${course.name})</a></small></h3>
    <c:if test="${lab.openForSubmissions}">
        <div class="alert alert-info">
            This lab is open for submissions.
        </div>
    </c:if>
    <c:if test="${not lab.openForSubmissions}">
        <div class="alert alert-danger">
            This lab is closed for submissions.
        </div>
    </c:if>
    <div class="row">
        <div class="col-md-12">
            <div class="panel panel-primary">
                <div class="panel-heading">Details</div>
                <div class="panel-body">
                    <table class="table table-striped">
                        <tbody>
                        <tr>
                            <td class="col-md-3">Name</td>
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
                        <tr>
                            <td>Submission Deadline</td>
                            <td><c:out value="${lab.submissionDeadline}"/></td>
                        </tr>
                        <tr>
                            <td>Allow Late</td>
                            <td><c:out value="${lab.allowLateSubmissions}"/></td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
    <shiro:hasPermission name="lab:submit:${course.id}:${lab.id}">
        <c:if test="${lab.openForSubmissions}">
            <div class="row">
                <div class="col-md-12">
                    <div class="panel panel-primary">
                        <div class="panel-heading">Upload Submission</div>
                        <div class="panel-body">
                            <form id="submitLabForm" method="POST" enctype="multipart/form-data" action="/submit/${course.id}/${lab.id}">
                                <div class="form-group">
                                    <input id="submissionFile" name="submissionFile" type="file">
                                </div>
                                <button type="submit" form="submitLabForm" class="btn btn-primary">Upload</button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </c:if>
    </shiro:hasPermission>
</div>
</body>
</html>