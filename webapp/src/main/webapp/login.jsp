<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <%@ include file="/WEB-INF/include/header.html" %>
    <title>Lakunu: Login</title>
</head>
<body>
<shiro:guest>
    <div class="container">
        <div class="row">
            <div class="col-md-12">
                <div class="jumbotron">
                    <h1>Welcome to Lakunu</h1>
                    <p>Automate grading programming assignments</p>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="col-md-4 col-md-offset-4">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <h3 class="panel-title" align="center">Sign in</h3>
                    </div>
                    <div class="panel-body">
                        <form class="form-signin" action="" method="POST" role="form">
                            <div class="form-group">
                                <label for="inputUsername" class="sr-only">Username</label>
                                <input class="form-control" placeholder="Username" name="username"
                                       id="inputUsername" type="text" required autofocus>
                            </div>
                            <div class="form-group">
                                <label for="inputPassword" class="sr-only">Password</label>
                                <input class="form-control" placeholder="Password" name="password"
                                       id="inputPassword" type="password" required>
                            </div>
                            <div class="checkbox">
                                <label>
                                    <input name="rememberMe" type="checkbox" value="true"> Remember
                                    Me
                                </label>
                            </div>
                            <button class="btn btn-lg btn-primary btn-block" type="submit">Sign in
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <!-- /container -->
    <c:if test="${not empty requestScope.shiroLoginFailure}">
        <c:out value="${requestScope.shiroLoginFailure}"/>
    </c:if>
</shiro:guest>
<shiro:user>
    <jsp:forward page="home"/>
</shiro:user>
</body>
</html>