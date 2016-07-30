package org.lakunu.web.api;

import com.google.common.base.Strings;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.io.output.ThresholdingOutputStream;
import org.lakunu.web.models.Lab;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
                    FileUploadBuffer buffer = new FileUploadBuffer(1024 * 1024);
                    try (InputStream in = item.openStream()) {
                        IOUtils.copy(in, buffer);
                    }
                    labService.submitLab(lab, "FileUpload", buffer.getData());
                    break;
                }
            }
            resp.sendRedirect("/lab/" + courseId + "/" + labId);
        } catch (FileUploadException e) {
            throw new ServletException(e);
        }
    }

    private static class FileUploadBuffer extends ThresholdingOutputStream {

        private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        public FileUploadBuffer(int threshold) {
            super(threshold);
        }

        @Override
        protected OutputStream getStream() throws IOException {
            return buffer;
        }

        @Override
        protected void thresholdReached() throws IOException {
            throw new IOException("Upload larger than threshold: " + getThreshold());
        }

        protected  byte[] getData() {
            return buffer.toByteArray();
        }
    }
}
