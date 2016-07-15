package org.lakunu.web;

import org.lakunu.web.data.DAOCollection;
import org.lakunu.web.data.jdbc.JDBCDAOCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public final class LakunuContextListener implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(LakunuContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();
        // TODO: Load the DAO collection from a config
        servletContext.setAttribute(DAOCollection.DAO_COLLECTION, new JDBCDAOCollection(servletContext));
        logger.info("Lakunu webapp initialized");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();
        DAOCollection daoCollection = (DAOCollection) servletContext.getAttribute(
                DAOCollection.DAO_COLLECTION);
        daoCollection.close();
        logger.info("Lakunu webapp terminated");
    }
}
