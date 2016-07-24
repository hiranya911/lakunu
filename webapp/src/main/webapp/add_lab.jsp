<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<%--@elvariable id="lab" type="org.lakunu.web.data.Course"--%>
<head>
    <%@ include file="include/header.html" %>
    <title>Lakunu: Add Lab [${param["cName"]}]</title>
</head>
<body>
<%@ include file="include/body_top.html" %>
<div class="container">
    <div class="row">
        <div class="col-md-12">
            <div class="panel panel-primary">
                <div class="panel-heading">Create Lab for: ${param["cName"]}</div>
                <div class="panel-body">
                    <form role="form" method="POST" action="/lab/${param["cId"]}">
                        <div class="form-group">
                            <label for="labName">Lab Name:</label>
                            <input type="text" name="labName" class="form-control" id="labName" required>
                        </div>
                        <div class="form-group">
                            <label for="labVersion">Lab Version:</label>
                            <input type="text" name="labVersion" class="form-control" id="labVersion" required>
                        </div>
                        <div class="form-group">
                            <label for="labDescription">Lab Description:</label>
                            <textarea class="form-control" rows="3" name="labDescription"
                                      id="labDescription" maxlength="512" required></textarea>
                        </div>
                        <button type="submit" class="btn btn-primary">Submit</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>