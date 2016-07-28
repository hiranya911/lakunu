<%--@elvariable id="lab" type="org.lakunu.web.data.Lab"--%>
<%--@elvariable id="course" type="org.lakunu.web.data.Course"--%>
<%--@elvariable id="canEdit" type="java.lang.Boolean"--%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <%@ include file="/WEB-INF/include/header.html" %>
    <link rel="stylesheet" href="<c:url value="/codemirror/codemirror.css"/>">
    <script src="<c:url value="/codemirror/codemirror.js"/>"></script>
    <script src="<c:url value="/codemirror/xml/xml.js"/>"></script>
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
                    This lab is not yet published. It cannot receive any submissions until it is published.
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

    <shiro:hasPermission name="lab:view:${course.id}:${lab.id}">
        <jsp:include page="lab_config.jsp"/>
    </shiro:hasPermission>
    <jsp:include page="lab_details.jsp"/>
</div>
</body>
</html>