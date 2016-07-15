package org.lakunu.web.api;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.lakunu.web.data.TestDatabase;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HomePageServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp) throws ServletException, IOException {
        Subject currentUser = SecurityUtils.getSubject();
        System.out.println(currentUser);
        currentUser.checkPermission("course:listOwned");
        req.setAttribute("courses", TestDatabase.getCoursesByOwner(currentUser.getPrincipal().toString()));
        req.getRequestDispatcher("/home.jsp").forward(req, resp);
    }


}
