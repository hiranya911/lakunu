<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<%--@elvariable id="course" type="org.lakunu.web.data.Course"--%>
<head>
    <%@ include file="include/header.html" %>
    <title>Lakunu: Course [${course.name}]</title>
</head>
<body>
<%@ include file="include/body_top.html" %>
<div class="container">
    <div class="row">
        <div class="col-md-12">
            <div class="panel panel-primary">
                <div class="panel-heading">Course Info: ${course.name}</div>
                <div class="panel-body">
                    <table class="table table-striped">
                        <tbody>
                            <tr>
                                <td>ID</td>
                                <td><c:out value="${course.id}"/></td>
                            </tr>
                            <tr>
                                <td>Name</td>
                                <td><c:out value="${course.name}"/></td>
                            </tr>
                            <tr>
                                <td>Description</td>
                                <td><c:out value="${course.description}"/></td>
                            </tr>
                            <tr>
                                <td>Created</td>
                                <td><c:out value="${course.createdAt}"/></td>
                            </tr>
                            <tr>
                                <td>Owner</td>
                                <td><c:out value="${course.owner}"/></td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
    <shiro:hasPermission name="course:share:${course.id}">
        <div class="row">
            <div class="col-md-12">
                <p>Share form goes here</p>
            </div>
        </div>
    </shiro:hasPermission>
</div>
</body>
</html>