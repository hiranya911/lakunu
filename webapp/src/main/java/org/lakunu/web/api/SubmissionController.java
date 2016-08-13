package org.lakunu.web.api;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import org.apache.commons.io.IOUtils;
import org.lakunu.web.models.Lab;
import org.lakunu.web.models.Submission;
import org.lakunu.web.models.SubmissionView;
import org.lakunu.web.service.submissions.UserSubmissionFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@WebServlet("/submission/*")
public class SubmissionController extends LakunuController {

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

        if (Boolean.parseBoolean(req.getParameter("download"))) {
            downloadSubmission(lab, pathInfo.get(2), resp);
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

    private void downloadSubmission(Lab lab, String submissionId,
                                    HttpServletResponse resp) throws IOException {
        Submission submission = submissionService.getSubmission(lab, submissionId);
        if (submission == null) {
            resp.sendError(404, "Submission ID does not exist: " + submissionId);
            return;
        }

        resp.setContentType(UserSubmissionFactory.getMimeType(submission.getType()));
        String fileName = UserSubmissionFactory.getFileName("submission_" + submission.getId(),
                submission.getType());
        resp.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"",
                fileName));
        OutputStream outputStream = resp.getOutputStream();
        IOUtils.write(submission.getData(), outputStream);
        outputStream.flush();
        outputStream.close();
    }

}
