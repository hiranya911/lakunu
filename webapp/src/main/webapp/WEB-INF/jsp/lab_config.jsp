<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="row">
    <div class="col-md-12">
        <div class="panel panel-primary">
            <div class="panel-heading">Configuration</div>
            <div class="panel-body">
                <c:if test="${canEdit}">
                    <script>
                        $(document).ready(function () {
                            $('#updateLabConfigForm').submit(function (e) {
                                var postData = $(this).serializeArray();
                                var configInValid = null;
                                $.each(postData, function (i, input) {
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
                                            url: '/lab/${course.id}/${lab.id}',
                                            type: 'POST',
                                            data: postData,
                                            success: function (data, textStatus, jqXHR) {
                                                var updateDiv = $('#updateResponse');
                                                updateDiv.text('${lab.name} updated successfully.');
                                                updateDiv.removeClass();
                                                updateDiv.addClass('alert');
                                                updateDiv.addClass('alert-success');
                                            },
                                            error: function (jqXHR, textStatus, errorThrown) {
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
                    <div id="updateResponse"></div>
                    <form role="form" id="updateLabConfigForm" method="POST" action="">
                        <input type="hidden" name="_method" value="PUT">
                        <input type="hidden" name="updateLabConfig" value="true">
                        <div class="form-group">
                            <textarea name="labConfig" id="labConfig" class="form-control" rows="10">${labConfigString}</textarea>
                        </div>
                        <button type="submit" form="updateLabConfigForm" class="btn btn-primary">Save</button>
                    </form>
                </c:if>
                <c:if test="${not canEdit}">
                    <textarea id="labConfig" class="form-control" rows="10">${labConfigString}</textarea>
                </c:if>
            </div>
        </div>
    </div>
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