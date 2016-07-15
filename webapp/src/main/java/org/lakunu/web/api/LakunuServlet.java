package org.lakunu.web.api;

import org.lakunu.web.data.DAOCollection;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class LakunuServlet extends HttpServlet {

    protected DAOCollection daoCollection;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.daoCollection = (DAOCollection) config.getServletContext().getAttribute(
                DAOCollection.DAO_COLLECTION);
        checkNotNull(this.daoCollection, "DAOCollection is required");
    }
}
