<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="row">
    <div class="col-md-12">
        <div class="panel panel-primary">
            <div class="panel-heading">Publishing</div>
            <div class="panel-body">
                <c:if test="${lab.published}">
                    <table class="table table-striped">
                        <tbody>
                        <tr>
                            <td class="col-md-3">Published</td>
                            <td class="text-success">Yes</td>
                        </tr>
                        <tr>
                            <td>Submission Deadline</td>
                            <td>${lab.submissionDeadline}</td>
                        </tr>
                        <tr>
                            <td>Late Submissions</td>
                            <td>${lab.allowLateSubmissions}</td>
                        </tr>
                        </tbody>
                    </table>
                    <button class="btn btn-primary" type="button" data-toggle="modal"
                            data-target="#unpublishLabModal">Unpublish</button>

                    <div id="unpublishLabModal" class="modal fade" role="dialog">
                        <div class="modal-dialog">
                            <form id="unpublishLabForm" method="POST" action="/lab/${course.id}/${lab.id}">
                                <input type="hidden" name="_method" value="PUT">
                                <input type="hidden" name="unpublishLab" value="true">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <button type="button" class="close" data-dismiss="modal">&times;</button>
                                        <h4 class="modal-title">Unpublish ${lab.name}</h4>
                                    </div>
                                    <div class="modal-body">
                                        <p>Are you sure you want to unpublish ${lab.name}?</p>
                                    </div>
                                    <div class="modal-footer">
                                        <button class="btn btn-primary" type="submit" form="unpublishLabForm">Unpublish</button>
                                        <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                </c:if>
                <c:if test="${not lab.published}">
                    <script src="//cdnjs.cloudflare.com/ajax/libs/pickadate.js/3.5.3/picker.js"></script>
                    <script src="//cdnjs.cloudflare.com/ajax/libs/pickadate.js/3.5.3/picker.date.js"></script>
                    <script src="//cdnjs.cloudflare.com/ajax/libs/pickadate.js/3.5.3/picker.time.js"></script>
                    <script src="//cdnjs.cloudflare.com/ajax/libs/pickadate.js/3.5.3/legacy.js"></script>
                    <form role="form" id="publishLabForm" method="POST" action="/lab/${course.id}/${lab.id}">
                        <input type="hidden" name="_method" value="PUT">
                        <input type="hidden" name="publishLab" value="true">
                        <div class="form-group form-inline">
                            <label for="labDeadline">Submission Deadline (Date):</label>
                            <input id="labDeadline" name="labDeadline" type="text" class="form-control">
                        </div>
                        <div class="form-group form-inline">
                            <label for="labDeadlineTime">Submission Deadline (Time):</label>
                            <input id="labDeadlineTime" name="labDeadlineTime" type="text" class="form-control" value="11:30 PM">
                        </div>
                        <div class="form-group">
                            <div class="checkbox">
                                <label>
                                    <input name="labAllowLate" type="checkbox" value=""> Allow Late Submissions
                                </label>
                            </div>
                        </div>
                        <button class="btn btn-primary" type="submit" form="publishLabForm">Publish</button>
                    </form>
                    <script type="text/javascript">
                        $(document).ready(function() {
                            $('#labDeadline').pickadate({
                                format: 'mm/dd/yyyy',
                                formatSubmit: 'mm/dd/yyyy',
                                min: new Date()
                            });
                            $('#labDeadlineTime').pickatime({
                                disable: [[0,0]]
                            });
                        });
                    </script>
                </c:if>
            </div>
        </div>
    </div>
</div>