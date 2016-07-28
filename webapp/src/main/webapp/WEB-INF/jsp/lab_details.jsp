<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="row">
    <div class="col-md-12">
        <div class="panel panel-primary">
            <div class="panel-heading">Details</div>
            <div class="panel-body">
                <c:if test="${canEdit}">
                    <script>
                        $(document).ready(function () {
                            $('#updateLabDetailsForm').submit(function (e) {
                                var postData = $(this).serializeArray();
                                $.ajax(
                                        {
                                            url: '/lab/${course.id}/${lab.id}',
                                            type: 'POST',
                                            data: postData,
                                            success: function (data, textStatus, jqXHR) {
                                                var updateDiv = $('#updateDetailsResponse');
                                                updateDiv.text('${lab.name} updated successfully.');
                                                updateDiv.removeClass();
                                                updateDiv.addClass('alert');
                                                updateDiv.addClass('alert-success');
                                            },
                                            error: function (jqXHR, textStatus, errorThrown) {
                                                var updateDiv = $('#updateDetailsResponse');
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
                    <div id="updateDetailsResponse"></div>
                    <form role="form" id="updateLabDetailsForm" method="POST" action="">
                        <input type="hidden" name="_method" value="PUT">
                        <input type="hidden" name="updateLabDetails" value="true">
                        <div class="form-group">
                            <label style="float:left;margin-right:5px;">ID:</label>
                            <p>${lab.id}</p>
                        </div>
                        <div class="form-group">
                            <label for="labName">Name:</label>
                            <input id="labName" name="labName" type="text" class="form-control" value="${lab.name}" required>
                        </div>
                        <div class="form-group">
                            <label for="labDescription">Lab Description:</label>
                            <textarea class="form-control" rows="3" name="labDescription"
                                  id="labDescription" maxlength="512" required>${lab.description}</textarea>
                        </div>
                        <div class="form-group">
                            <label style="float:left;margin-right:5px;">Created At:</label>
                            <p>${lab.createdAt}</p>
                        </div>
                        <div class="form-group">
                            <label style="float:left;margin-right:5px;">Created By:</label>
                            <p>${lab.createdBy}</p>
                        </div>
                        <button class="btn btn-primary" type="submit" form="updateLabDetailsForm">Save</button>
                    </form>
                </c:if>
                <c:if test="${not canEdit}">
                        <table class="table table-striped">
                            <tbody>
                            <tr>
                                <td class="col-md-3">ID</td>
                                <td>${lab.id}</td>
                            </tr>
                            <tr>
                                <td>Name</td>
                                <td>${lab.name}</td>
                            </tr>
                            <tr>
                                <td>Description</td>
                                <td>${lab.description}</td>
                            </tr>
                            <tr>
                                <td>Created</td>
                                <td>${lab.createdAt}</td>
                            </tr>
                            <tr>
                                <td>Created By</td>
                                <td>${lab.createdBy}</td>
                            </tr>
                            </tbody>
                        </table>
                </c:if>
            </div>
        </div>
    </div>
</div>