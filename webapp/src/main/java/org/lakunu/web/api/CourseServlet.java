package org.lakunu.web.api;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.lakunu.web.data.Lab;
import org.lakunu.web.models.Course;
import org.lakunu.web.service.CourseService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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

        // TODO: Improve the path parameter parsing
        String courseId = pathInfo.substring(1);
        CourseService service = CourseService.getInstance(daoFactory);
        Course course = service.getCourse(courseId);
        if (course == null) {
            resp.sendError(404, "Course ID does not exist: " + courseId);
            return;
        }
        req.setAttribute("course", course);
        ImmutableList<Lab> labs = service.getLabs(courseId);
        req.setAttribute("courseLabs", labs);
        req.setAttribute("labPermissions", computeLabPermissions(labs));
        req.getRequestDispatcher("/course.jsp").forward(req, resp);
    }

    private Map<String,String> computeLabPermissions(List<Lab> labs) {
        ImmutableMap.Builder<String,String> permissions = ImmutableMap.builder();
        Subject subject = SecurityUtils.getSubject();
        labs.forEach(lab -> {
            String permission = "";
            if (subject.isPermitted("lab:view:" + lab.getCourseId() + ":" + lab.getId())) {
                permission += "v";
            }
            if (subject.isPermitted("lab:edit:" + lab.getCourseId() + ":" + lab.getId())) {
                permission += "e";
            }
            if (subject.isPermitted("lab:submit:" + lab.getCourseId() + ":" + lab.getId())) {
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
        CourseService service = CourseService.getInstance(daoFactory);
        Course course = service.addCourse(req.getParameter("courseName"),
                req.getParameter("courseDescription"));
        resp.sendRedirect("/course/" + course.getId());
    }

}
