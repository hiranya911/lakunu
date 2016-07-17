<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
    <head>
        <%@ include file="include/header.html" %>
        <title>Lakunu: Home</title>
    </head>
    <body>
        <div class="page-header">
            <h1>Welcome to Lakunu</h1>
            <p>You are logged in as <shiro:principal/> (<a href="/logout">logout</a>)</p>
        </div>
        <div class="container">
            <div class="row">
                <div class="col-md-4">
                    <div class="panel panel-primary">
                        <div class="panel-heading">Create New Course</div>
                        <div class="panel-body">
                            <form class="form" action="/course" method="POST" role="form">
                                <div class="form-group">
                                    <label for="courseName">Name:</label>
                                    <input type="text" class="form-control" name="courseName"
                                           id="courseName" size="128" maxlength="128" required/>
                                </div>
                                <div class="form-group">
                                    <label for="courseDescription">Description:</label>
                                    <textarea class="form-control" rows="3" name="courseDescription" id="courseDescription" required></textarea>
                                </div>
                                <button class="btn btn-primary" type="submit">Create</button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-md-12">
                    <div class="panel panel-primary">
                        <div class="panel-heading">My Courses (Instructor)</div>
                        <div class="panel-body">
                            <%--@elvariable id="DAO_COLLECTION" type="org.lakunu.web.data.DAOCollection"--%>
                            <c:set var="courses" value="${DAO_COLLECTION.courseDAO.ownedCourses}"/>
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