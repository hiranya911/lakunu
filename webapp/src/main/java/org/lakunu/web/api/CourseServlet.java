package org.lakunu.web.api;

import com.google.common.base.Strings;
import org.lakunu.web.data.Course;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/course/*")
public class CourseServlet extends LakunuServlet {

    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (Strings.isNullOrEmpty(pathInfo)) {
            resp.sendError(404);
            return;
        }

        String courseId = pathInfo.substring(1);
        Course course = daoCollection.getCourseDAO().getCourse(courseId);
        if (course == null) {
            resp.sendError(404, "Course ID does not exist: " + courseId);
            return;
        }
        req.setAttribute("course", course);
        req.getRequestDispatcher("/course.jsp").forward(req, resp);
    }

}
