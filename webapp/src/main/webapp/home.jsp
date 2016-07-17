<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
    <head>
        <%@ include file="include/header.html" %>
        <title>Lakunu: Home</title>
    </head>
    <body>
        <nav class="navbar navbar-inverse navbar-fixed-top">
            <div class="navbar-inner">
                <div class="container">
                    <div class="navbar-header">
                        <a class="navbar-brand" href="/">Lakunu</a>
                    </div>
                    <ul class="nav navbar-nav">
                        <li class="active"><a href="home.jsp">Home</a></li>
                        <li><a href="#" data-toggle="modal" data-target="#addCourseModal">New Course</a></li>
                    </ul>
                    <ul class="nav pull-right">
                        <li class="dropdown">
                            <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                                <span class="glyphicon glyphicon-user"></span>
                                <shiro:principal/>
                            </a>
                            <ul class="dropdown-menu">
                                <li><a href="logout">Logout</a></li>
                            </ul>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>
        <div id="fix-for-navbar-fixed-top-spacing" style="height: 60px;">&nbsp;</div>
        <div class="container">
            <div id="addCourseModal" class="modal fade" role="dialog">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal">&times;</button>
                            <h4 class="modal-title">Add New Course</h4>
                        </div>
                        <div class="modal-body">
                            <form id="addCourse" class="form" action="/course" method="POST" role="form">
                                <div class="form-group">
                                    <label for="courseName">Name:</label>
                                    <input type="text" class="form-control" name="courseName"
                                           id="courseName" maxlength="128" placeholder="e.g. CS101" required/>
                                </div>
                                <div class="form-group">
                                    <label for="courseDescription">Description:</label>
                                    <textarea class="form-control" rows="3" name="courseDescription"
                                              id="courseDescription" maxlength="512" required></textarea>
                                </div>
                            </form>
                        </div>
                        <div class="modal-footer">
                            <button class="btn btn-primary" type="submit" form="addCourse">Create</button>
                            <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
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