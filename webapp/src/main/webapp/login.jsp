<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
    <head>
        <title>Lakunu: Login</title>
    </head>
    <body>
        <shiro:guest>
            <h1>Welcome to Lakunu</h1>
            <p>Please Login</p>
            <form name="loginform" action="" method="POST" accept-charset="UTF-8" role="form">
                <input class="form-control" placeholder="Username or Email" name="username" type="text">
                <input class="form-control" placeholder="Password" name="password" type="password" value="">
                <input name="rememberMe" type="checkbox" value="true"> Remember Me
                <input class="btn btn-lg btn-success btn-block" type="submit" value="Login">
            </form>
            <c:if test="${not empty requestScope.shiroLoginFailure}">
                <c:out value="${requestScope.shiroLoginFailure}"/>
            </c:if>
        </shiro:guest>
        <shiro:user>
            <jsp:forward page="home"/>
        </shiro:user>
    </body>
</html>