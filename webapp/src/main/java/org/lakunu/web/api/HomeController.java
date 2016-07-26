package org.lakunu.web.api;

import org.lakunu.web.service.CourseService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/home")
public class HomeController extends LakunuServlet {

    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp) throws ServletException, IOException {
        CourseService service = CourseService.getInstance(daoFactory);
        req.setAttribute("courses", service.getOwnedCourses());
        req.getRequestDispatcher("home.jsp").forward(req, resp);
    }
}
