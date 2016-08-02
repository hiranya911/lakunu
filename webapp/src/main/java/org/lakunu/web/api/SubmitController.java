package org.lakunu.web.api;

import com.google.common.base.Strings;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.lakunu.labs.utils.ThresholdByteArrayOutputStream;
import org.lakunu.web.models.Lab;
import org.lakunu.web.service.submissions.FileUploadSubmission;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

@WebServlet("/submit/*")
public class SubmitController extends LakunuController {

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
        req.setAttribute("lab", lab);
        req.setAttribute("course", courseService.getCourse(lab.getCourseId()));
        req.getRequestDispatcher("/WEB-INF/jsp/submit.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req,
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

        ServletFileUpload fileUpload = new ServletFileUpload();
        try {
            FileItemIterator iterator = fileUpload.getItemIterator(req);
            while (iterator.hasNext()) {
                FileItemStream item = iterator.next();
                if ("submissionFile".equals(item.getFieldName()) && !item.isFormField()) {
                    logger.info("Received file input field: " + item.getName());
                    ThresholdByteArrayOutputStream buffer = new ThresholdByteArrayOutputStream(
                            4 * 1024 * 1024, true);
                    try (InputStream in = item.openStream()) {
                        IOUtils.copy(in, buffer);
                    }
                    submissionService.addSubmission(lab, new FileUploadSubmission(item.getName(),
                            buffer.toByteArray()));
                    break;
                }
            }
            resp.sendRedirect("/submission/" + courseId + "/" + labId);
        } catch (FileUploadException e) {
            throw new ServletException(e);
        }
    }
}
