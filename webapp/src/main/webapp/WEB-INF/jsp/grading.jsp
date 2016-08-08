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
                    <th>Final Grade</th>
                    <th/>
                </tr>
                <c:forEach items="${submissions}" var="sub">
                    <tr>
                        <td>${sub.key}</td>
                        <td>
                            <ul>
                                <c:if test="${empty sub.value}">
                                    <li>No submissions</li>
                                </c:if>
                                <c:if test="${fn:length(sub.value) > 0}">
                                    <li>
                                        ${sub.value[0].submittedAt}
                                        <c:if test="${empty sub.value[0].evaluations}">
                                            <span class="label label-warning">Pending</span>
                                        </c:if>
                                        <c:if test="${not empty sub.value[0].evaluations}">
                                            <span class="label label-success">${sub.value[0].finalScore}</span>
                                        </c:if>
                                    </li>
                                </c:if>
                                <c:if test="${fn:length(sub.value) > 1}">
                                    <li>
                                        ${sub.value[1].submittedAt}
                                        <c:if test="${empty sub.value[1].evaluations}">
                                            <span class="label label-warning">Pending</span>
                                        </c:if>
                                        <c:if test="${not empty sub.value[1].evaluations}">
                                            <span class="label label-success">${sub.value[1].finalScore}</span>
                                        </c:if>
                                    </li>
                                </c:if>
                                <c:if test="${fn:length(sub.value) > 2}">
                                    <li>
                                        ${sub.value[2].submittedAt}
                                        <c:if test="${empty sub.value[2].evaluations}">
                                            <span class="label label-warning">Pending</span>
                                        </c:if>
                                        <c:if test="${not empty sub.value[2].evaluations}">
                                            <span class="label label-success">${sub.value[2].finalScore}</span>
                                        </c:if>
                                    </li>
                                </c:if>
                                <c:if test="${fn:length(sub.value) > 3}">
                                    <li>${fn:length(sub.value) - 3} more</li>
                                </c:if>
                            </ul>
                        </td>
                        <td>
                            <c:if test="${not empty sub.value}">
                                ${sub.value[0].finalScore}
                            </c:if>
                            <c:if test="${empty sub.value}">
                                0.0
                            </c:if>
                        </td>
                        <td>
                            <c:if test="${not empty sub.value}">
                                <a href="?user=${sub.key}&limit=3">View</a>
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