<%--@elvariable id="lab" type="org.lakunu.web.data.Lab"--%>
<%--@elvariable id="course" type="org.lakunu.web.data.Course"--%>
<%--@elvariable id="canEdit" type="java.lang.Boolean"--%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <%@ include file="include/header.html" %>
    <link rel="stylesheet" href="<c:url value="/codemirror/codemirror.css"/>">
    <script src="<c:url value="/codemirror/codemirror.js"/>"></script>
    <script src="<c:url value="/codemirror/xml/xml.js"/>"></script>
    <title>Lakunu: Lab [${lab.name}]</title>
</head>
<body>
<%@ include file="include/body_top.html" %>
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
<shiro:hasPermission name="lab:publish:${course.id}:${lab.id}">
    <c:if test="${not lab.published}">
        <div id="publishLabModal" class="modal fade" role="dialog">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal">&times;</button>
                        <h4 class="modal-title">Publish Options</h4>
                    </div>
                    <div class="modal-body">
                        <form id="publishLab" class="form" method="POST" role="form">
                            <div class="form-group">
                                <label for="labDeadline">Submission Deadline:</label>
                                <input id="labDeadline" name="labDeadline" type="text" class="form-control" value="${lab.submissionDeadline}" required>
                            </div>
                            <div class="form-group">
                                <label><input type="checkbox" name="labAllowLate" value=""> Allow Late Submissions</label>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button class="btn btn-primary" type="submit" form="publishLab">Publish</button>
                        <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                    </div>
                </div>
            </div>
        </div>
    </c:if>
</shiro:hasPermission>
</body>
</html>