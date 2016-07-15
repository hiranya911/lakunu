package org.lakunu.web.api;

import org.lakunu.web.data.TestDatabase;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HomePageServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("courses", TestDatabase.getCoursesByOwner());
        req.getRequestDispatcher("/home.jsp").forward(req, resp);
    }

}
