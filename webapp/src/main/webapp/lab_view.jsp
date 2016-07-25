<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--@elvariable id="lab" type="org.lakunu.web.data.Lab"--%>
<div class="row">
    <div class="col-md-12">
        <div class="panel panel-primary">
            <div class="panel-heading">Configuration</div>
            <div class="panel-body">
                <textarea name="labConfig" id="labConfig" class="form-control" rows="10" readonly></textarea>
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