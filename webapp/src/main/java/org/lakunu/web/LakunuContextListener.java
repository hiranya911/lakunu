package org.lakunu.web;

import com.google.common.base.Strings;
import org.lakunu.web.data.DAOCollection;
import org.lakunu.web.data.jdbc.JdbcDAOCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import java.lang.reflect.Constructor;

@WebListener
public final class LakunuContextListener implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(LakunuContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();
        servletContext.setAttribute(DAOCollection.DAO_COLLECTION, initDAOCollection(servletContext));
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

    private DAOCollection initDAOCollection(ServletContext servletContext) {
        String type = servletContext.getInitParameter("daoCollection");
        if (Strings.isNullOrEmpty(type)) {
            logger.info("DAOCollection type not configured. Using default.");
            type = JdbcDAOCollection.class.getName();
        }
        try {
            Class<? extends DAOCollection> clazz = Class.forName(type).asSubclass(DAOCollection.class);
            Constructor<? extends DAOCollection> constructor = clazz.getConstructor(ServletContext.class);
            return constructor.newInstance(servletContext);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
