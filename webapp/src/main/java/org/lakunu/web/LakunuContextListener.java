package org.lakunu.web;

import org.lakunu.web.data.DAOCollection;
import org.lakunu.web.data.test.TestCourseDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public final class LakunuContextListener implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(LakunuContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        DAOCollection collection = DAOCollection.newBuilder()
                .setCourseDAO(new TestCourseDAO())
                .build();
        servletContextEvent.getServletContext().setAttribute(DAOCollection.DAO_COLLECTION, collection);
        logger.info("Lakunu webapp initialized");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        logger.info("Lakunu webapp terminated");
    }
}
