package org.lakunu.web.api;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/courses")
public class CoursesServlet extends LakunuServlet {

    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("courses", daoCollection.getCourseDAO().getOwnedCourses());
        req.getRequestDispatcher("/courses.jsp").forward(req, resp);
    }

}
