package org.lakunu.web.api;

import org.lakunu.web.service.DAOFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class LakunuServlet extends HttpServlet {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected DAOFactory daoFactory;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.daoFactory = (DAOFactory) config.getServletContext().getAttribute(
                DAOFactory.DAO_FACTORY);
        checkNotNull(this.daoFactory, "DAOFactory is required");
    }
}
