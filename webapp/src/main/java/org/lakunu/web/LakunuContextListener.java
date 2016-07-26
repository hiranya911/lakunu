package org.lakunu.web;

import org.lakunu.web.dao.jdbc.JdbcDAOFactory;
import org.lakunu.web.data.DAOCollection;
import org.lakunu.web.data.jdbc.JdbcDAOCollection;
import org.lakunu.web.service.DAOFactory;
import org.lakunu.web.utils.ConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import java.io.IOException;
import java.lang.reflect.Constructor;

@WebListener
public final class LakunuContextListener implements ServletContextListener {

    public static final String DAO_COLLECTION = "daoCollection";
    public static final String DAO_FACTORY = "daoFactory";
    public static final String LAKUNU_PROPERTIES = "/WEB-INF/lakunu.properties";

    private static final Logger logger = LoggerFactory.getLogger(LakunuContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();
        try {
            ConfigProperties properties = new ConfigProperties(servletContext, LAKUNU_PROPERTIES);
            servletContext.setAttribute(DAOCollection.DAO_COLLECTION, initDAOCollection(properties));
            servletContext.setAttribute(DAOFactory.DAO_FACTORY, initDAOFactory(properties));
            logger.info("Lakunu webapp initialized");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();
        DAOCollection daoCollection = (DAOCollection) servletContext.getAttribute(
                DAOCollection.DAO_COLLECTION);
        daoCollection.close();
        logger.info("Lakunu webapp terminated");
    }

    private DAOCollection initDAOCollection(ConfigProperties properties) {
        String type = properties.getOptional(DAO_COLLECTION, JdbcDAOCollection.class.getName());
        try {
            Class<? extends DAOCollection> clazz = Class.forName(type).asSubclass(DAOCollection.class);
            Constructor<? extends DAOCollection> constructor = clazz.getConstructor(ConfigProperties.class);
            return constructor.newInstance(properties);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private DAOFactory initDAOFactory(ConfigProperties properties) {
        String type = properties.getOptional(DAO_FACTORY, JdbcDAOFactory.class.getName());
        try {
            Class<? extends DAOFactory> clazz = Class.forName(type).asSubclass(DAOFactory.class);
            Constructor<? extends DAOFactory> constructor = clazz.getConstructor(ConfigProperties.class);
            return constructor.newInstance(properties);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
