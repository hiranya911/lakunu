package org.lakunu.web.dao.jdbc;

import org.lakunu.web.dao.CourseDAO;
import org.lakunu.web.dao.EvaluationDAO;
import org.lakunu.web.dao.SubmissionDAO;
import org.lakunu.web.dao.LabDAO;
import org.lakunu.web.queue.EvaluationJobQueue;
import org.lakunu.web.service.DAOFactory;
import org.lakunu.web.utils.ConfigProperties;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public final class JdbcDAOFactory extends DAOFactory {

    private static final String DAO_FACTORY_DATA_SOURCE = "daoFactory.dataSource";
    private static final String DAO_FACTORY_ENQUEUE_WORKER = "daoFactory.enqueueWorker";

    private final DataSource dataSource;
    private final JdbcEnqueueWorker enqueueWorker;

    public JdbcDAOFactory(ConfigProperties properties, EvaluationJobQueue jobQueue) {
        InitialContext context = null;
        try {
            context = new InitialContext();
            String dsName = properties.getRequired(DAO_FACTORY_DATA_SOURCE);
            logger.info("Loading JDBC datasource: {}", dsName);
            dataSource = (DataSource) context.lookup(dsName);
            if (Boolean.parseBoolean(properties.getOptional(DAO_FACTORY_ENQUEUE_WORKER, "true"))) {
                enqueueWorker = new JdbcEnqueueWorker(dataSource, jobQueue);
            } else {
                enqueueWorker = null;
            }
        } catch (NamingException e) {
            throw new RuntimeException(e);
        } finally {
            closeContext(context);
        }
    }

    private void closeContext(InitialContext context) {
        if (context != null) {
            try {
                context.close();
            } catch (NamingException ignored) {
            }
        }
    }

    @Override
    protected CourseDAO getCourseDAO() {
        return new JdbcCourseDAO(dataSource);
    }

    @Override
    protected LabDAO getLabDAO() {
        return new JdbcLabDAO(dataSource);
    }

    @Override
    protected SubmissionDAO getSubmissionDAO() {
        return new JdbcSubmissionDAO(dataSource);
    }

    @Override
    protected EvaluationDAO getEvaluationDAO() {
        return new JdbcEvaluationDAO(dataSource);
    }

    @Override
    public void cleanup() {
        if (enqueueWorker != null) {
            enqueueWorker.cleanup();
        }
    }
}
