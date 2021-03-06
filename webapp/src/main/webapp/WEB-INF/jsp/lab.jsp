<%--@elvariable id="lab" type="org.lakunu.web.models.Lab"--%>
<%--@elvariable id="course" type="org.lakunu.web.models.Course"--%>
<%--@elvariable id="canEdit" type="java.lang.Boolean"--%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <%@ include file="/WEB-INF/include/header.html" %>
    <shiro:hasPermission name="lab:publish:${course.id}:${lab.id}">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/pickadate.js/3.5.3/themes/default.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/pickadate.js/3.5.3/themes/default.date.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/pickadate.js/3.5.3/themes/default.time.css">
    </shiro:hasPermission>
    <link rel="stylesheet" href="<c:url value="/codemirror/codemirror.css"/>">
    <title>Lakunu: Lab [${lab.name}]</title>
</head>
<body>
<%@ include file="/WEB-INF/include/body_top.html" %>
<div class="container">
    <h3>${lab.name} <small><a href="/course/${lab.courseId}">(Back to ${course.name})</a></small></h3>
    <c:if test="${not lab.published}">
        <div class="row">
            <div class="col-md-12">
                <div class="alert alert-info">
                    This lab is not published. It cannot receive any submissions until it is published.
                </div>
            </div>
        </div>
    </c:if>
    <c:if test="${lab.published}">
        <shiro:hasPermission name="lab:update:${course.id}:${lab.id}">
            <div class="row">
                <div class="col-md-12">
                    <div class="alert alert-info">
                        This lab is published. You cannot make changes to the lab while it's published.
                    </div>
                </div>
            </div>
        </shiro:hasPermission>
        <shiro:lacksPermission name="lab:update:${course.id}:${lab.id}">
            <div class="row">
                <div class="col-md-12">
                    <div class="alert alert-info">
                        This lab is published. It is available for receiving submissions.
                    </div>
                </div>
            </div>
        </shiro:lacksPermission>
    </c:if>

    <shiro:hasPermission name="lab:update:${course.id}:${lab.id}">
        <jsp:include page="lab_config.jsp"/>
        <jsp:include page="lab_details.jsp"/>
    </shiro:hasPermission>

    <shiro:hasPermission name="lab:publish:${course.id}:${lab.id}">
        <jsp:include page="lab_publish.jsp"/>
    </shiro:hasPermission>
</div>
</body>
</html>