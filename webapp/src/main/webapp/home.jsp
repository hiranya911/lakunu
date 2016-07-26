<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
    <head>
        <%@ include file="include/header.html" %>
        <title>Lakunu: Home</title>
    </head>
    <body>
        <%@ include file="include/body_top.html" %>
        <div class="container">
            <div class="row">
                <div class="col-md-12">
                    <div class="panel panel-primary">
                        <div class="panel-heading">My Courses (Instructor)</div>
                        <div class="panel-body">
                            <c:if test="${empty courses}">
                                <p>No courses to display</p>
                            </c:if>
                            <c:if test="${not empty courses}">
                                <table class="table table-striped">
                                    <thead class="thead-inverse">
                                    <tr>
                                        <th>Name</th>
                                        <th>Description</th>
                                        <th>Created</th>
                                        <th/>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach items="${courses}" var="course">
                                        <tr>
                                            <td>${course.name}</td>
                                            <td>${course.description}</td>
                                            <td>${course.createdAt}</td>
                                            <td><a href="/course/${course.id}">View</a></td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </c:if>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>