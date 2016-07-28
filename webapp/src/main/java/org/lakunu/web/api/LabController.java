package org.lakunu.web.api;

import com.google.common.base.Strings;
import org.lakunu.web.models.Lab;
import org.lakunu.web.service.LabService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.lakunu.web.utils.Security.hasPermission;

@WebServlet("/lab/*")
public class LabController extends LakunuController {

    private static final SimpleDateFormat DEADLINE_DATETIME_FORMAT =
            new SimpleDateFormat("MM/dd/yyyy hh:mm a");
    private static final String DEFAULT_DEADLINE_TIME = "11:30 PM";

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
        String labConfig = "";
        if (lab.getConfiguration() != null) {
            labConfig = new String(lab.getConfiguration());
        }
        req.setAttribute("labConfigString", labConfig);
        req.setAttribute("course", courseService.getCourse(lab.getCourseId()));
        req.setAttribute("canEdit", hasPermission(LabService.UPDATE_PERMISSION(lab)) &&
                !lab.isPublished());
        req.getRequestDispatcher("/WEB-INF/jsp/lab.jsp").forward(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req,
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

        if (Boolean.parseBoolean(req.getParameter("updateLabConfig"))) {
            Lab.Update update = lab.newUpdate();
            String config = req.getParameter("labConfig");
            if (!Strings.isNullOrEmpty(config)) {
                update.setConfiguration(config.getBytes());
            } else {
                update.setConfiguration(null);
            }
            labService.updateLab(update);
            logger.info("Updated lab configuration: {}", lab.getId());
        } else if (Boolean.parseBoolean(req.getParameter("updateLabDetails"))) {
            Lab.Update update = lab.newUpdate()
                    .setName(req.getParameter("labName"))
                    .setDescription(req.getParameter("labDescription"));
            labService.updateLab(update);
            logger.info("Updated lab details: {}", lab.getId());
        } else if (Boolean.parseBoolean(req.getParameter("publishLab"))) {
            Lab.PublishSettings publishSettings = lab.newPublishSettings()
                    .setPublished(true)
                    .setAllowLateSubmissions(Boolean.parseBoolean(req.getParameter("labAllowLate")));
            String deadlineDate = req.getParameter("labDeadline");
            if (!Strings.isNullOrEmpty(deadlineDate)) {
                String deadlineTime = req.getParameter("labDeadlineTime");
                if (Strings.isNullOrEmpty(deadlineTime)) {
                    deadlineTime = DEFAULT_DEADLINE_TIME;
                }
                String dateInput = deadlineDate.trim() + " " + deadlineTime.trim();
                try {
                    publishSettings.setSubmissionDeadline(DEADLINE_DATETIME_FORMAT.parse(dateInput));
                } catch (ParseException e) {
                    throw new ServletException("Invalid date string: " + dateInput, e);
                }
            }
            labService.publishLab(publishSettings);
            logger.info("Published lab: {}", lab.getId());
            resp.sendRedirect("/lab/" + courseId + "/" + labId);
        } else if (Boolean.parseBoolean(req.getParameter("unpublishLab"))) {
            labService.unpublishLab(lab);
            logger.info("Unpublished lab: {}", lab.getId());
            resp.sendRedirect("/lab/" + courseId + "/" + labId);
        } else {
            resp.sendError(400, "Invalid update operation");
        }
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
        String courseId = pathInfo.substring(1);
        String labId = labService.addLab(req.getParameter("labName"),
                req.getParameter("labDescription"), courseId);
        resp.sendRedirect("/lab/" + courseId + "/" + labId);
    }
}
