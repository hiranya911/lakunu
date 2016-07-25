package org.lakunu.web.api;

import com.google.common.base.Strings;
import org.apache.shiro.SecurityUtils;
import org.lakunu.web.data.Lab;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkArgument;

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
        req.setAttribute("lab", lab);
        req.setAttribute("course", daoCollection.getCourseDAO().getCourse(lab.getCourseId()));

        String editPermission = "lab:edit:" + lab.getCourseId() + ":" + lab.getId();
        req.setAttribute("canEdit", SecurityUtils.getSubject().isPermitted(editPermission));
        req.getRequestDispatcher("/lab.jsp").forward(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req,
                         HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("[" + req.getParameter("labConfig") + "]");
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
