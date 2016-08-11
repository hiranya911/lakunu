package org.lakunu.web.api;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedMap;
import org.apache.commons.io.IOUtils;
import org.lakunu.web.models.Course;
import org.lakunu.web.models.Lab;
import org.lakunu.web.models.SubmissionView;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@WebServlet("/grading/*")
public class GradingController extends LakunuController {

    private static final SimpleDateFormat EXPORT_DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");

    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp) throws ServletException, IOException {

        UrlPathInfo pathInfo = new UrlPathInfo(req);
        if (pathInfo.isEmpty()) {
            resp.sendError(403);
            return;
        }

        String courseId = pathInfo.get(0);
        String labId = pathInfo.get(1);
        Lab lab = labService.getLab(courseId, labId);
        if (lab == null) {
            resp.sendError(404, "Lab ID does not exist: " + labId);
            return;
        }

        Course course = courseService.getCourse(lab.getCourseId());
        if (Boolean.parseBoolean(req.getParameter("export"))) {
            exportGrades(course, lab, resp);
            return;
        }

        req.setAttribute("course", course);
        req.setAttribute("lab", lab);
        String userId = req.getParameter("user");
        if (Strings.isNullOrEmpty(userId)) {
            ImmutableList<String> students = courseService.getStudents(course);
            Map<String, List<SubmissionView>> submissions = submissionService
                    .getSubmissionsForGrading(lab);

            ImmutableSortedMap.Builder<String, List<SubmissionView>> builder = ImmutableSortedMap.naturalOrder();
            submissions.forEach(builder::put);
            students.stream().filter(s -> !submissions.containsKey(s))
                    .forEach(s -> builder.put(s, ImmutableList.of()));

            req.setAttribute("submissions", builder.build());
            req.getRequestDispatcher("/WEB-INF/jsp/grading.jsp").forward(req, resp);
        } else {
            int limit;
            String limitParam = req.getParameter("limit");
            if (Strings.isNullOrEmpty(limitParam)) {
                limit = -1;
            } else {
                limit = Integer.parseInt(limitParam);
            }
            ImmutableList<SubmissionView> submissions = submissionService.getSubmissionsForGrading(
                    lab, userId, limit);
            req.setAttribute("getByUser", userId);
            req.setAttribute("submissions", submissions);
            req.setAttribute("viewAll", limit < 0 || submissions.size() < limit);
            req.getRequestDispatcher("/WEB-INF/jsp/user_grading.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req,
                         HttpServletResponse resp) throws ServletException, IOException {
        if (Boolean.parseBoolean(req.getParameter("enqueue"))) {
            String courseId = req.getParameter("courseId");
            String labId = req.getParameter("labId");
            Lab lab = labService.getLab(courseId, labId);
            if (lab == null) {
                resp.sendError(404, "Invalid lab or course");
                return;
            }
            String submissionId = req.getParameter("submission");
            submissionService.enqueueSubmission(lab, submissionId);
        } else {
            resp.sendError(400, "Invalid update operation");
        }
    }

    private void exportGrades(Course course, Lab lab, HttpServletResponse resp) throws IOException {
        ImmutableList<String> students = courseService.getStudents(course);
        ImmutableSortedMap<String,Double> grades = submissionService.exportGrades(lab, students);
        resp.setContentType("text/csv");
        String fileName = String.format("lakunu_lab%s_%s.csv", lab.getId(),
                EXPORT_DATE_FORMAT.format(new Date()));
        resp.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"",
                fileName));
        OutputStream outputStream = resp.getOutputStream();
        for (Map.Entry<String,Double> entry : grades.entrySet()) {
            IOUtils.write(String.format("%s, %.2f\n", entry.getKey(), entry.getValue()),
                    outputStream);
        }
        outputStream.flush();
        outputStream.close();
    }
}
