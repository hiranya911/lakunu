package org.lakunu.web.api;

import com.google.common.base.Strings;
import org.lakunu.web.data.Lab;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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

        String courseId = pathInfo.substring(1, pathInfo.indexOf('/', 1));
        String labId = pathInfo.substring(pathInfo.indexOf('/', 1) + 1);
        System.out.println("Retrieving lab: " + courseId + ":" + labId);
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
