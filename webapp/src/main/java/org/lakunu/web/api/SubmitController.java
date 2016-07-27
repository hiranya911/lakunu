package org.lakunu.web.api;

import com.google.common.base.Strings;
import org.lakunu.web.models.Lab;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
}
