package org.lakunu.web.api;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import org.lakunu.web.models.Lab;
import org.lakunu.web.models.Submission;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@WebServlet("/eval/*")
public class EvaluationController extends LakunuController {

    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (Strings.isNullOrEmpty(pathInfo)) {
            resp.sendError(404);
            return;
        }

        // TODO: Improve the path parameter parsing
        String courseId = pathInfo.substring(1, pathInfo.indexOf('/', 1));
        String labId = pathInfo.substring(pathInfo.indexOf('/', 1) + 1);
        Lab lab = labService.getLab(courseId, labId);
        if (lab == null) {
            resp.sendError(404, "Lab ID does not exist: " + labId);
            return;
        }

        List<Submission> ownedSubmissions = new ArrayList<>(evaluationService.getOwnedSubmissions(
                courseId, labId));
        ownedSubmissions.sort((o1, o2) -> o2.getSubmittedAt().compareTo(o1.getSubmittedAt()));

        req.setAttribute("lab", lab);
        req.setAttribute("course", courseService.getCourse(courseId));
        req.setAttribute("submissions", ImmutableList.copyOf(ownedSubmissions));
        req.getRequestDispatcher("/WEB-INF/jsp/eval.jsp").forward(req, resp);
    }
}
