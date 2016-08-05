package org.lakunu.web.api;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.lakunu.web.models.Course;
import org.lakunu.web.models.Lab;
import org.lakunu.web.service.LabService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;


@WebServlet("/course/*")
public class CourseController extends LakunuController {

    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp) throws ServletException, IOException {
        UrlPathInfo pathInfo = new UrlPathInfo(req);
        if (pathInfo.isEmpty()) {
            resp.sendError(403);
            return;
        }

        String courseId = pathInfo.get(0);
        Course course = courseService.getCourse(courseId);
        if (course == null) {
            resp.sendError(404, "Course ID does not exist: " + courseId);
            return;
        }
        req.setAttribute("course", course);
        ImmutableList<Lab> labs = courseService.getLabs(course.getId());
        req.setAttribute("courseLabs", labs);
        req.setAttribute("labPermissions", computeLabPermissions(labs));
        req.getRequestDispatcher("/WEB-INF/jsp/course.jsp").forward(req, resp);
    }

    private Map<String,String> computeLabPermissions(List<Lab> labs) {
        ImmutableMap.Builder<String,String> permissions = ImmutableMap.builder();
        Subject subject = SecurityUtils.getSubject();
        labs.forEach(lab -> {
            String permission = "";
            if (subject.isPermitted(LabService.UPDATE_PERMISSION(lab))) {
                permission += "u";
            }
            if (subject.isPermitted(LabService.PUBLISH_PERMISSION(lab))) {
                permission += "p";
            }
            if (subject.isPermitted(LabService.SUBMIT_PERMISSION(lab))) {
                permission += "s";
            }
            if (permission.length() > 0) {
                permissions.put(lab.getId(), permission);
            }
        });
        return permissions.build();
    }

    @Override
    protected void doPost(HttpServletRequest req,
                          HttpServletResponse resp) throws ServletException, IOException {
        String courseId = courseService.addCourse(req.getParameter("courseName"),
                req.getParameter("courseDescription"));
        resp.sendRedirect("/course/" + courseId);
    }

}
