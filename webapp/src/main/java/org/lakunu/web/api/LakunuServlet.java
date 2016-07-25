package org.lakunu.web.api;

import org.lakunu.web.data.DAOCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class LakunuServlet extends HttpServlet {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected DAOCollection daoCollection;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.daoCollection = (DAOCollection) config.getServletContext().getAttribute(
                DAOCollection.DAO_COLLECTION);
        checkNotNull(this.daoCollection, "DAOCollection is required");
    }
}
