<%--@elvariable id="lab" type="org.lakunu.web.data.Lab"--%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <%@ include file="include/header.html" %>
    <link rel="stylesheet" href="/codemirror/codemirror.css">
    <script src="/codemirror/codemirror.js"></script>
    <script src="/codemirror/xml/xml.js"></script>
    <title>Lakunu: Lab [${lab.name}]</title>
</head>
<body>
<%@ include file="include/body_top.html" %>
<div class="container">
    <h3>${lab.name} <small><a href="/course/${lab.courseId}">(Back to ${course.name})</a></small></h3>
    <div class="row">
        <div class="col-md-12">
            <div class="panel panel-primary">
                <%--@elvariable id="course" type="org.lakunu.web.data.Course"--%>
                <div class="panel-heading">Configuration</div>
                <div class="panel-body">
                    <form onsubmit="return checkConfig()" role="form" id="updateConfig" method="POST" action="/lab/${course.id}/${lab.id}">
                        <input name="updateConfig" type="hidden" value="true">
                        <div class="form-group">
                            <textarea name="labConfig" id="labConfig" class="form-control"
                                      rows="10"></textarea>
                        </div>
                        <button type="submit" class="btn btn-primary" form="updateConfig">Save</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col-md-12">
            <div class="panel panel-primary">
                <%--@elvariable id="course" type="org.lakunu.web.data.Course"--%>
                <div class="panel-heading">Details</div>
                <div class="panel-body">
                    <table class="table table-striped">
                        <tbody>
                        <tr>
                            <td>ID</td>
                            <td><c:out value="${lab.id}"/></td>
                        </tr>
                        <tr>
                            <td>Name</td>
                            <td><c:out value="${lab.name}"/></td>
                        </tr>
                        <tr>
                            <td>Description</td>
                            <td><c:out value="${lab.description}"/></td>
                        </tr>
                        <tr>
                            <td>Created</td>
                            <td><c:out value="${lab.createdAt}"/></td>
                        </tr>
                        <tr>
                            <td>Created By</td>
                            <td><c:out value="${lab.createdBy}"/></td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
    <div id="configErrorModal" class="modal fade" role="dialog">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                    <h4 class="modal-title">Invalid Configuration</h4>
                </div>
                <div class="modal-body">
                    <p id="configErrorMessage"></p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>
</div>
<script>
    var config, editor;

    config = {
        lineNumbers: true,
        mode: "xml",
        indentWithTabs: false
    };

    editor = CodeMirror.fromTextArea(document.getElementById("labConfig"), config);

    function checkConfig() {
        var code = editor.getValue();
        if (code == null || code == '') {
            document.getElementById('configErrorMessage').innerHTML = 'No content provided';
            $('#configErrorModal').modal('show');
            return false;
        } else {
            var parser = new DOMParser();
            var dom = parser.parseFromString(code, "text/xml");
            if (dom.getElementsByTagName("parsererror").length > 0) {
                document.getElementById('configErrorMessage').innerHTML = 'Invalid XML configuration';
                $('#configErrorModal').modal('show');
                return false;
            }
        }
        return true;
    }
</script>
</body>
</html>