<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--@elvariable id="course" type="org.lakunu.web.data.Course"--%>
<%--@elvariable id="lab" type="org.lakunu.web.data.Lab"--%>
<script>
    function validateForm() {
        var editor = $('.CodeMirror')[0].CodeMirror;
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
<form role="form" id="updateLabForm" onsubmit="return validateForm()" method="POST" action="/lab/${course.id}/${lab.id}">
    <input type="hidden" name="_method" value="PUT">
    <input type="hidden" name="updateLab" value="true">
    <div class="row">
        <div class="col-md-12">
            <div class="panel panel-primary">
                <div class="panel-heading">Configuration</div>
                <div class="panel-body">
                    <textarea name="labConfig" id="labConfig" class="form-control" rows="10"></textarea>
                </div>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col-md-12">
            <div class="panel panel-primary">
                <div class="panel-heading">
                    <div class="panel-title" data-toggle="collapse" data-target="#collapse1" style="cursor: pointer">
                        Details
                        <span class="pull-right">
                            <i class="glyphicon glyphicon-chevron-down"></i>
                        </span>
                    </div>
                </div>
                <div id="collapse1" class="panel-collapse collapse">
                    <div class="panel-body">
                        <div class="form-group">
                            <label>ID: ${lab.id}</label>
                        </div>
                        <div class="form-group">
                            <label for="labName">Name</label>
                            <input id="labName" name="labName" type="text" class="form-control" value="${lab.name}" required>
                        </div>
                        <div class="form-group">
                            <label for="labDescription">Lab Description:</label>
                        <textarea class="form-control" rows="3" name="labDescription"
                                  id="labDescription" maxlength="512" required>${lab.description}</textarea>
                        </div>
                        <div class="form-group">
                            <label>Created At:</label>
                            <p>${lab.createdAt}</p>
                        </div>
                        <div class="form-group">
                            <label>Created By:</label>
                            <p>${lab.createdBy}</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col-md-1">
            <button type="submit" class="btn btn-primary" form="updateLabForm">Save</button>
        </div>
    </div>
</form>
<div id="configErrorModal" class="modal fade" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Invalid Input</h4>
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