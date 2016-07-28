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
    <c:if test="${canEdit}">
        <jsp:include page="lab_edit.jsp"/>
    </c:if>
    <c:if test="${not canEdit}">
        <jsp:include page="lab_view.jsp"/>
    </c:if>
</div>
<script>
    var config, editor;

    config = {
        lineNumbers: true,
        mode: "xml",
        indentWithTabs: false,
        readOnly: ${not canEdit}
    };

    editor = CodeMirror.fromTextArea(document.getElementById("labConfig"), config);
    editor.setSize(900, 300);
</script>
</body>
</html>