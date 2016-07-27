package org.lakunu.web.api;

import com.google.common.base.Strings;
import org.lakunu.web.models.Lab;
import org.lakunu.web.service.LabService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.lakunu.web.utils.Security.hasPermission;

@WebServlet("/lab/*")
public class LabController extends LakunuController {

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
        req.getRequestDispatcher("/lab.jsp").forward(req, resp);
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

        if (Boolean.parseBoolean(req.getParameter("updateLab"))) {
            Lab.Update update = lab.update()
                    .setName(req.getParameter("labName"))
                    .setDescription(req.getParameter("labDescription"));
            String config = req.getParameter("labConfig");
            if (!Strings.isNullOrEmpty(config)) {
                update.setConfiguration(config.getBytes());
            } else {
                update.setConfiguration(null);
            }
            labService.updateLab(update);
            logger.info("Updated lab: {}", lab.getId());
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