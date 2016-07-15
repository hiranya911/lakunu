package org.lakunu.web.api;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HomePageServlet extends LakunuServlet {

    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("courses", daoCollection.getCourseDAO().getOwnedCourses());
        req.getRequestDispatcher("/home.jsp").forward(req, resp);
    }

}
