<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--@elvariable id="labConfigString" type="java.lang.String"--%>
<%--@elvariable id="course" type="org.lakunu.web.data.Course"--%>
<%--@elvariable id="lab" type="org.lakunu.web.data.Lab"--%>
<script>
    $(document).ready(function() {
        $('#updateLabForm').submit(function(e){
            var postData = $(this).serializeArray();
            var configInValid = null;
            $.each(postData, function(i, input) {
               if (input.name == 'labConfig') {
                   if (input.value != null && input.value != '') {
                       var parser = new DOMParser();
                       var dom = parser.parseFromString(input.value, "text/xml");
                       if (dom.getElementsByTagName("parsererror").length > 0) {
                           configInValid = 'Invalid XML configuration';
                       }
                   }
               }
            });
            if (configInValid != null) {
                var updateDiv = $('#updateResponse');
                updateDiv.text(configInValid);
                updateDiv.removeClass();
                updateDiv.addClass('alert');
                updateDiv.addClass('alert-warning');
                return false;
            }
            $.ajax(
                    {
                        url : '/lab/${course.id}/${lab.id}',
                        type: 'POST',
                        data : postData,
                        success:function(data, textStatus, jqXHR) {
                            var updateDiv = $('#updateResponse');
                            updateDiv.text('${lab.name} updated successfully.');
                            updateDiv.removeClass();
                            updateDiv.addClass('alert');
                            updateDiv.addClass('alert-success');
                        },
                        error: function(jqXHR, textStatus, errorThrown) {
                            var updateDiv = $('#updateResponse');
                            updateDiv.text('Failed to update ${lab.name}.');
                            updateDiv.removeClass();
                            updateDiv.addClass('alert');
                            updateDiv.addClass('alert-danger');
                        }
                    });
            e.preventDefault(); //STOP default action
            e.unbind(); //unbind. to stop multiple form submit.
        });
    });
</script>
<form role="form" id="updateLabForm" method="POST" action="">
    <input type="hidden" name="_method" value="PUT">
    <input type="hidden" name="updateLab" value="true">
    <div class="row">
        <div class="col-md-12">
            <div id="updateResponse"></div>
            <div class="panel panel-primary">
                <div class="panel-heading">Configuration</div>
                <div class="panel-body">
                    <textarea name="labConfig" id="labConfig" class="form-control" rows="10">${labConfigString}</textarea>
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
            <button type="submit" class="btn btn-primary">Save</button>
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