<%--@elvariable id="lab" type="org.lakunu.web.data.Lab"--%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <%@ include file="include/header.html" %>
    <title>Lakunu: Lab [${lab.name}]</title>
</head>
<body>
<%@ include file="include/body_top.html" %>
<div class="container">
    <h3>${lab.name} (${course.name})</h3>
    <div class="row">
        <div class="col-md-12">
            <div class="panel panel-primary">
                <%--@elvariable id="course" type="org.lakunu.web.data.Course"--%>
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
                    <a href="/course/${lab.courseId}">Back to ${course.name}</a>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>