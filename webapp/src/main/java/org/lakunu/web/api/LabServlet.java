package org.lakunu.web.api;

import com.google.common.base.Strings;
import org.lakunu.web.data.Lab;
import org.lakunu.web.data.LabDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.lakunu.web.utils.Security.hasPermission;

@WebServlet("/lab/*")
public class LabServlet extends LakunuServlet {

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
        Lab lab = daoCollection.getLabDAO().getLab(courseId, labId);
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
        req.setAttribute("course", daoCollection.getCourseDAO().getCourse(lab.getCourseId()));
        req.setAttribute("canEdit", hasPermission(LabDAO.UPDATE_PERMISSION(lab)) && !lab.isPublished());
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
        Lab lab = daoCollection.getLabDAO().getLab(courseId, labId);
        if (lab == null) {
            resp.sendError(404, "Lab ID does not exist: " + labId);
            return;
        }

        if (Boolean.parseBoolean(req.getParameter("updateLab"))) {
            Lab.Update update = new Lab.Update(lab)
                    .setName(req.getParameter("labName"))
                    .setDescription(req.getParameter("labDescription"));
            String config = req.getParameter("labConfig");
            if (!Strings.isNullOrEmpty(config)) {
                update.setConfiguration(config.getBytes());
            } else {
                update.setConfiguration(null);
            }
            lab.update(update);
            daoCollection.getLabDAO().updateLab(lab);
            logger.info("Updated lab: {}", lab.getId());
        } else if (Boolean.parseBoolean(req.getParameter("publishLab"))) {
            Lab.Publish publish = new Lab.Publish();
            String deadline = req.getParameter("labDeadline");
            if (!Strings.isNullOrEmpty(deadline)) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date deadlineDate = format.parse(deadline);
                    publish.setSubmissionDeadline(new Timestamp(deadlineDate.getTime()));
                } catch (ParseException e) {
                    throw new ServletException(e);
                }
            }
            publish.setAllowLateSubmissions(Boolean.parseBoolean(req.getParameter("labAllowLate")));
            lab.publish(publish);
            daoCollection.getLabDAO().publishLab(lab);
            logger.info("Published lab: {}", lab.getId());
            resp.sendRedirect("/lab/" + lab.getCourseId() + "/" + lab.getId());
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
        Lab lab = Lab.newBuilder()
                .setName(req.getParameter("labName"))
                .setDescription(req.getParameter("labDescription"))
                .setCourseId(courseId)
                .buildForAddition();
        String labId = daoCollection.getLabDAO().addLab(lab);
        resp.sendRedirect("/lab/" + courseId + "/" + labId);
    }
}
