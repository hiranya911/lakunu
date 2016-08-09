package org.lakunu.web.api;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import org.lakunu.web.models.Lab;
import org.lakunu.web.models.SubmissionView;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/submission/*")
public class SubmissionController extends LakunuController {

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

        int limit;
        String limitParam = req.getParameter("limit");
        if (Strings.isNullOrEmpty(limitParam)) {
            limit = -1;
        } else {
            limit = Integer.parseInt(limitParam);
        }
        ImmutableList<SubmissionView> ownedSubmissions = submissionService.getOwnedSubmissions(
                lab, limit);
        req.setAttribute("lab", lab);
        req.setAttribute("course", courseService.getCourse(courseId));
        req.setAttribute("submissions", ownedSubmissions);
        req.setAttribute("viewAll", limit < 0 || ownedSubmissions.size() < limit);
        req.getRequestDispatcher("/WEB-INF/jsp/submissions.jsp").forward(req, resp);
    }
}
