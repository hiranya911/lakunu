package org.lakunu.web.api;

import com.google.common.collect.ImmutableListMultimap;
import org.lakunu.web.models.Lab;
import org.lakunu.web.models.SubmissionView;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/grading/*")
public class GradingController extends LakunuController {

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

        Map<String,List<SubmissionView>> submissions = submissionService
                .getAllSubmissions(lab);
        req.setAttribute("course", courseService.getCourse(lab.getCourseId()));
        req.setAttribute("lab", lab);
        req.setAttribute("submissions", submissions);
        req.getRequestDispatcher("/WEB-INF/jsp/grading.jsp").forward(req, resp);
    }
}
