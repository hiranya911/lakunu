<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%--@elvariable id="course" type="org.lakunu.web.models.Course"--%>
<%--@elvariable id="courseLabs" type="java.util.List<Lab>"--%>
<%--@elvariable id="labPermissions" type="java.util.Map<String,String>"--%>
<%--@elvariable id="publishOptions" type="java.lang.Boolean"--%>
<c:if test="${not empty labPermissions}">
    <div id="publishResponse"></div>
    <table class="table table-striped">
        <thead class="thead-inverse">
        <tr>
            <th>Name</th>
            <th>Description</th>
            <th>Created</th>
            <th></th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${courseLabs}" var="lab">
            <c:if test="${not empty labPermissions[lab.id]}">
                <tr>
                    <td><c:out value="${lab.name}"/></td>
                    <td><c:out value="${lab.description}"/></td>
                    <td><c:out value="${lab.createdAt}"/></td>
                    <td>
                        <c:if test="${fn:contains(labPermissions[lab.id], 'v') || fn:contains(labPermissions[lab.id], 'e')}">
                            <a href="/lab/${course.id}/${lab.id}">View/Edit</a>
                            <span class="tab-space">&nbsp;</span>
                        </c:if>
                        <c:if test="${fn:contains(labPermissions[lab.id], 'p')}">
                            <span id="publish-${course.id}-${lab.id}" style="display: ${lab.published ? "none":""}">
                                <a class="publish-lab" href="#" data-toggle="modal" data-target="#publishLabModal"
                                   data-lab-id="${lab.id}" data-lab-name="${lab.name}">Publish</a>
                            </span>
                            <span id="unpublish-${course.id}-${lab.id}" style="display: ${lab.published ? "":"none"}">
                                <a class="unpublish-lab" href="#" data-toggle="modal" data-target="#unpublishLabModal"
                                   data-lab-id="${lab.id}" data-lab-name="${lab.name}">Unpublish</a>
                            </span>
                            <span class="tab-space">&nbsp;</span>
                        </c:if>
                        <c:if test="${fn:contains(labPermissions[lab.id], 's')}">
                            <span id="submit-${course.id}-${lab.id}" style="display: ${lab.published ? "":"none"}">
                                <a href="/submit/${course.id}/${lab.id}">Submit</a>
                            </span>
                            <span class="text-muted" id="nosubmit-${course.id}-${lab.id}" style="display: ${lab.published ? "none":""}">
                                Submit
                            </span>
                        </c:if>
                    </td>
                </tr>
            </c:if>
        </c:forEach>
        </tbody>
    </table>
</c:if>
<c:if test="${empty labPermissions}">
    <p>No labs to display</p>
</c:if>
<c:if test="${publishOptions}">
    <div id="publishLabModal" class="modal fade" role="dialog">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                    <h4 class="modal-title">Publish Options for <span id="publishLabTitle"></span></h4>
                </div>
                <div class="modal-body">
                    <form id="publishLabForm" class="form" action="" method="POST" role="form">
                        <input type="hidden" name="_method" value="PUT">
                        <input type="hidden" name="publishLab" value="true">
                        <input type="hidden" name="labId" id="labId" value="">
                        <input type="hidden" name="labName" id="labName" value="">
                        <input type="hidden" name="courseId" value="${course.id}">
                        <div class="form-group">
                            <label for="labDeadline">Submission Deadline:</label>
                            <input id="labDeadline" name="labDeadline" type="text" class="form-control" value="" required>
                        </div>
                        <div class="form-group">
                            <label><input type="checkbox" name="labAllowLate" value=""> Allow Late Submissions</label>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button class="btn btn-primary" type="submit" form="publishLabForm">Publish</button>
                    <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                </div>
            </div>
        </div>
    </div>
    <div id="unpublishLabModal" class="modal fade" role="dialog">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                    <h4 class="modal-title">Unpublish <span id="unpublishLabTitle"></span></h4>
                </div>
                <div class="modal-body">
                    Are you sure?
                    <form id="unpublishLabForm" class="form" action="" method="POST" role="form">
                        <input type="hidden" name="_method" value="PUT">
                        <input type="hidden" name="unpublishLab" value="true">
                        <input type="hidden" name="labId" id="unpublishLabId" value="">
                        <input type="hidden" name="labName" id="unpublishLabName" value="">
                        <input type="hidden" name="courseId" value="${course.id}">
                    </form>
                </div>
                <div class="modal-footer">
                    <button class="btn btn-primary" type="submit" form="unpublishLabForm">Unpublish</button>
                    <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                </div>
            </div>
        </div>
    </div>
    <script>
        $('#publishLabModal').on('show.bs.modal', function(e) {
            var labId = $(e.relatedTarget).data('lab-id');
            var labName = $(e.relatedTarget).data('lab-name');
            $(e.currentTarget).find('#labId').val(labId);
            $(e.currentTarget).find('#labName').val(labName);
            $(e.currentTarget).find('#publishLabTitle').text(labName);
        });

        $('#unpublishLabModal').on('show.bs.modal', function(e) {
            var labId = $(e.relatedTarget).data('lab-id');
            var labName = $(e.relatedTarget).data('lab-name');
            $(e.currentTarget).find('#unpublishLabId').val(labId);
            $(e.currentTarget).find('#unpublishLabName').val(labName);
            $(e.currentTarget).find('#unpublishLabTitle').text(labName);
        });

        $(document).ready(function() {
            $('#publishLabForm').submit(function(e){
                var postData = $(this).serializeArray();
                var labId = $(this).find('#labId').val();
                var labName = $(this).find('#labName').val();
                $.ajax({
                    url: '/lab/${course.id}/' + labId,
                    type: 'POST',
                    data: postData,
                    success: function (data, textStatus, jqXHR) {
                        var updateDiv = $('#publishResponse');
                        updateDiv.text(labName + ' published successfully.');
                        updateDiv.removeClass();
                        updateDiv.addClass('alert');
                        updateDiv.addClass('alert-success');
                        $('#publish-${course.id}-' + labId).hide();
                        $('#unpublish-${course.id}-' + labId).show();
                        $('#submit-${course.id}-' + labId).show();
                        $('#nosubmit-${course.id}-' + labId).hide();
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        var updateDiv = $('#publishResponse');
                        updateDiv.text('Failed to publish ' + labName);
                        updateDiv.removeClass();
                        updateDiv.addClass('alert');
                        updateDiv.addClass('alert-danger');
                    }
                });
                $('#publishLabModal').modal('hide');
                e.preventDefault(); //STOP default action
                e.unbind(); //unbind. to stop multiple form submit.
            });

            $('#unpublishLabForm').submit(function(e){
                var postData = $(this).serializeArray();
                var labId = $(this).find('#unpublishLabId').val();
                var labName = $(this).find('#unpublishLabName').val();
                $.ajax({
                    url: '/lab/${course.id}/' + labId,
                    type: 'POST',
                    data: postData,
                    success: function (data, textStatus, jqXHR) {
                        var updateDiv = $('#publishResponse');
                        updateDiv.text(labName + ' unpublished successfully.');
                        updateDiv.removeClass();
                        updateDiv.addClass('alert');
                        updateDiv.addClass('alert-success');
                        $('#publish-${course.id}-' + labId).show();
                        $('#unpublish-${course.id}-' + labId).hide();
                        $('#submit-${course.id}-' + labId).hide();
                        $('#nosubmit-${course.id}-' + labId).show();
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        var updateDiv = $('#publishResponse');
                        updateDiv.text('Failed to unpublish ' + labName);
                        updateDiv.removeClass();
                        updateDiv.addClass('alert');
                        updateDiv.addClass('alert-danger');
                    }
                });
                $('#unpublishLabModal').modal('hide');
                e.preventDefault(); //STOP default action
                e.unbind(); //unbind. to stop multiple form submit.
            });
        });
    </script>
</c:if>
