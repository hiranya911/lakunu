<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
    <%@ include file="include/header.html" %>
    <title>Lakunu: Course [${course.name}]</title>
</head>
<body>
<%@ include file="include/body_top.html" %>
<div class="container">
    <h3>${course.name}</h3>
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
                    <%--@elvariable id="courseLabs" type="java.util.List<Lab>"--%>
                    <%--@elvariable id="labPermissions" type="java.util.Map<String,String>"--%>
                    <c:if test="${not empty labPermissions}">
                        <table class="table table-striped">
                            <thead class="thead-inverse">
                            <tr>
                                <th>Name</th>
                                <th>Description</th>
                                <th>Created</th>
                                <th></th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${courseLabs}" var="lab">
                                <c:if test="${not empty labPermissions[lab.id]}">
                                    <tr>
                                        <td><c:out value="${lab.name}"/></td>
                                        <td><c:out value="${lab.description}"/></td>
                                        <td><c:out value="${lab.createdAt}"/></td>
                                        <td>
                                            <c:if test="${fn:contains(labPermissions[lab.id], 'v')}">
                                                <a href="/lab/${course.id}/${lab.id}">View/Edit</a>
                                                <span class="tab-space">&nbsp;</span>
                                            </c:if>
                                            <c:if test="${fn:contains(labPermissions[lab.id], 's')}">
                                                Submit
                                            </c:if>
                                        </td>
                                    </tr>
                                </c:if>
                            </c:forEach>
                            </tbody>
                        </table>
                    </c:if>
                    <c:if test="${empty labPermissions}">
                        <p>No labs to display</p>
                    </c:if>
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
                <p>Share form goes here</p>
            </div>
        </div>
    </shiro:hasPermission>
</div>
</body>
</html>