<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
    <head>
        <title>Lakunu: Home</title>
    </head>
    <body>
        <h1>Welcome to Lakunu</h1>
        <p>You are logged in as <shiro:principal/> (<a href="/logout">logout</a>)</p>
        <h2>My Courses (Instructor)</h2>
        <table>
            <c:if test="${empty courses}">
                <tr>
                    <td>No courses to display</td>
                </tr>
            </c:if>
            <c:forEach items="${courses}" var="course">
                <tr>
                    <td>${course.name}</td>
                    <td>${course.owner}</td>
                </tr>
            </c:forEach>
        </table>
    </body>
</html>