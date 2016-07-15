<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<html>
    <head>
        <title>Lakunu: Home</title>
    </head>
    <body>
        <h1>Welcome to Lakunu</h1>
        <shiro:guest>
            <p>Please Login</p>
            <form name="loginform" action="" method="POST" accept-charset="UTF-8" role="form">
                <input class="form-control" placeholder="Username or Email" name="username" type="text">
                <input class="form-control" placeholder="Password" name="password" type="password" value="">
                <input name="rememberMe" type="checkbox" value="true"> Remember Me
                <input class="btn btn-lg btn-success btn-block" type="submit" value="Login">
            </form>
            <%
                Object loginError = request.getAttribute("shiroLoginFailure");
                if (loginError != null) {
            %>
            <p>Error: <%=loginError%></p>
            <%
                }
            %>
        </shiro:guest>
        <shiro:user>
            <p>You are logged in as <shiro:principal/> (<a href="/logout">logout</a>)</p>
        </shiro:user>
    </body>
</html>