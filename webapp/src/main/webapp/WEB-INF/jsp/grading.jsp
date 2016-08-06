<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
    <%@ include file="/WEB-INF/include/header.html" %>
    <title>Lakunu: Grading [${lab.name}]</title>
</head>
<body>
<%@ include file="/WEB-INF/include/body_top.html" %>
<div class="container">
    <h3>${lab.name} Grading <small><a href="/course/${lab.courseId}">(Back to ${course.name})</a></small></h3>
    <div class="row">
        <div class="col-md-12">
            <table class="table table-striped">
                <tr>
                    <th>User</th>
                    <th>Submissions</th>
                    <th>Last Submission</th>
                    <th>Last Score</th>
                </tr>
                <c:forEach items="${submissions}" var="sub">
                    <tr>
                        <td>${sub.key}</td>
                        <td>${fn:length(sub.value)}</td>
                        <td>
                            <c:if test="${not empty sub.value}">
                                ${sub.value[0].submittedAt}
                            </c:if>
                            <c:if test="${empty sub.value}">
                                None
                            </c:if>
                        </td>
                        <td>
                            <c:if test="${not empty sub.value}">
                                ${sub.value[0].finalScore}
                            </c:if>
                            <c:if test="${empty sub.value}">
                                0.0
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
            </table>
        </div>
    </div>
</div>
</body>
</html>