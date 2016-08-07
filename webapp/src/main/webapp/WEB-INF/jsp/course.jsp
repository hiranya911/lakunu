<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
    <%@ include file="/WEB-INF/include/header.html" %>
    <title>Lakunu: Course [${course.name}]</title>
</head>
<body>
<%@ include file="/WEB-INF/include/body_top.html" %>
<div class="container">
    <h3>${course.name}</h3>
    <div class="row">
        <div class="col-md-12">
            <div class="panel panel-primary">
                <%--@elvariable id="course" type="org.lakunu.web.models.Course"--%>
                <div class="panel-heading">Details</div>
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
    <div class="row">
        <div class="col-md-12">
            <div class="panel panel-primary">
                <div class="panel-heading">Labs</div>
                <div class="panel-body">
                    <jsp:include page="list_labs.jsp"/>
                    <shiro:hasPermission name="course:addLab:${course.id}">
                        <div id="addLabModal" class="modal fade" role="dialog">
                            <div class="modal-dialog">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <button type="button" class="close" data-dismiss="modal">&times;</button>
                                        <h4 class="modal-title">Add New Lab</h4>
                                    </div>
                                    <div class="modal-body">
                                        <form id="addLab" role="form" method="POST" action="/lab/${course.id}">
                                            <div class="form-group">
                                                <label for="labName">Lab Name:</label>
                                                <input type="text" name="labName" class="form-control" id="labName" required>
                                            </div>
                                            <div class="form-group">
                                                <label for="labDescription">Lab Description:</label>
                                                <textarea class="form-control" rows="3" name="labDescription"
                                                    id="labDescription" maxlength="512" required></textarea>
                                            </div>
                                        </form>
                                    </div>
                                    <div class="modal-footer">
                                        <button class="btn btn-primary" type="submit" form="addLab">Create</button>
                                        <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <a href="#" data-toggle="modal" data-target="#addLabModal">New Lab</a>
                    </shiro:hasPermission>
                </div>
            </div>
        </div>
    </div>
    <shiro:hasPermission name="course:share:${course.id}">
        <div class="row">
            <div class="col-md-12">
                <div class="panel panel-primary">
                    <div class="panel-heading">Sharing</div>
                    <div class="panel-body">
                        <form role="form" id="shareCourseForm" method="POST" action="">
                            <input type="hidden" name="_method" value="PUT">
                            <input type="hidden" name="shareCourse" value="true">
                            <div class="form-group col-sm-6">
                                <label for="users">Enter user names (one user per line)</label>
                                <textarea name="users" id="users" class="form-control input-sm" rows="5" cols="20" required></textarea>
                            </div>
                            <div class="form-group col-sm-6">
                                <div class="radio">
                                    <label><input type="radio" name="role" value="student" checked>Share as student</label>
                                </div>
                                <div class="radio">
                                    <label><input type="radio" name="role" value="instructor">Share as instructor</label>
                                </div>
                            </div>
                            <div class="form-group col-sm-12">
                                <button type="submit" form="shareCourseForm" class="btn btn-primary">Share</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </shiro:hasPermission>
</div>
</body>
</html>