<%--@elvariable id="lab" type="org.lakunu.web.data.Lab"--%>
<%--@elvariable id="course" type="org.lakunu.web.data.Course"--%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <%@ include file="/WEB-INF/include/header.html" %>
    <title>Lakunu: Evaluations [${lab.name}]</title>
</head>
<body>
<%@ include file="/WEB-INF/include/body_top.html" %>
<div class="container">
    <h3>${lab.name} Submissions <small><a href="/course/${lab.courseId}">(Back to ${course.name})</a></small></h3>
    <c:forEach items="${submissions}" var="sub">
        <div class="row">
            <div class="col-md-12">
                <div class="panel panel-primary">
                    <div class="panel-heading">Submission at: ${sub.submittedAt}</div>
                    <div class="panel-body">
                        <c:if test="${empty sub.evaluations}">
                            <p class="bg-info">Pending evaluation</p>
                        </c:if>
                        <c:forEach items="${sub.evaluations}" var="eval">
                            <p>${eval.id} - ${eval.totalScore}</p>
                        </c:forEach>
                    </div>
                </div>
            </div>
        </div>
    </c:forEach>
    <c:if test="${not viewAll}">
        <a href="/submission/${course.id}/${lab.id}">View All</a>
    </c:if>
</div>
</body>
</html>