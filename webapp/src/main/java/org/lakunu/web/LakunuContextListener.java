package org.lakunu.web;

import org.lakunu.web.queue.jms.JmsEvaluationJobQueue;
import org.lakunu.web.queue.EvaluationJobQueue;
import org.lakunu.web.dao.jdbc.JdbcDAOFactory;
import org.lakunu.web.service.DAOFactory;
import org.lakunu.web.utils.ConfigProperties;
import org.lakunu.web.workers.SimpleWorker;
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

    public static final String DAO_FACTORY = "daoFactory";
    private static final String JOB_QUEUE = "jobQueue";
    public static final String LAKUNU_PROPERTIES = "/WEB-INF/lakunu.properties";

    private static final Logger logger = LoggerFactory.getLogger(LakunuContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();
        ConfigProperties properties;
        try {
            properties = new ConfigProperties(servletContext, LAKUNU_PROPERTIES);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        EvaluationJobQueue jobQueue = initJobQueue(properties);
        servletContext.setAttribute(EvaluationJobQueue.JOB_QUEUE, jobQueue);

        DAOFactory daoFactory = initDAOFactory(properties, jobQueue);
        servletContext.setAttribute(DAOFactory.DAO_FACTORY, daoFactory);

        // TODO: Make this configurable
        jobQueue.addWorker(new SimpleWorker(daoFactory));

        logger.info("Lakunu webapp initialized");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();

        DAOFactory daoFactory = (DAOFactory) servletContext.getAttribute(DAOFactory.DAO_FACTORY);
        daoFactory.cleanup();

        EvaluationJobQueue jobQueue = (EvaluationJobQueue) servletContext.getAttribute(
                EvaluationJobQueue.JOB_QUEUE);
        jobQueue.cleanup();
        logger.info("Lakunu webapp terminated");
    }

    private DAOFactory initDAOFactory(ConfigProperties properties, EvaluationJobQueue jobQueue) {
        logger.info("Initializing DAO factory");
        String type = properties.getOptional(DAO_FACTORY, JdbcDAOFactory.class.getName());
        try {
            Class<? extends DAOFactory> clazz = Class.forName(type).asSubclass(DAOFactory.class);
            Constructor<? extends DAOFactory> constructor = clazz.getConstructor(
                    ConfigProperties.class, EvaluationJobQueue.class);
            return constructor.newInstance(properties, jobQueue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private EvaluationJobQueue initJobQueue(ConfigProperties properties) {
        logger.info("Initializing evaluation job queue");
        String queueImpl = properties.getOptional(JOB_QUEUE, JmsEvaluationJobQueue.class.getName());
        try {
            Class<? extends EvaluationJobQueue> clazz = Class.forName(queueImpl)
                    .asSubclass(EvaluationJobQueue.class);
            Constructor<? extends EvaluationJobQueue> constructor = clazz.getConstructor(
                    ConfigProperties.class);
            return constructor.newInstance(properties);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
