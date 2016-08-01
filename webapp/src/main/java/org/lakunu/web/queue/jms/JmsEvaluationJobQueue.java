package org.lakunu.web.queue.jms;

import org.lakunu.web.dao.DAOException;
import org.lakunu.web.queue.EvaluationJobQueue;
import org.lakunu.web.service.EvaluationJobWorker;
import org.lakunu.web.utils.ConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class JmsEvaluationJobQueue implements EvaluationJobQueue {

    private static final Logger logger = LoggerFactory.getLogger(JmsEvaluationJobQueue.class);

    private static final String JOB_QUEUE_CONNECTION_FACTORY = "jobQueue.cf";
    private static final String JOB_QUEUE_NAME = "jobQueue.name";

    private final QueueConnectionFactory connectionFactory;
    private final Queue queue;
    private final List<JmsEvaluationJobWorkerWrapper> workers = new ArrayList<>();

    public JmsEvaluationJobQueue(ConfigProperties properties) {
        InitialContext context = null;
        try {
            context = new InitialContext();
            String factoryName = properties.getRequired(JOB_QUEUE_CONNECTION_FACTORY);
            logger.info("Loading JMS connection factory: {}", factoryName);
            connectionFactory = (QueueConnectionFactory) context.lookup(factoryName);
            queue = (Queue) context.lookup(properties.getRequired(JOB_QUEUE_NAME));
        } catch (NamingException e) {
            throw new RuntimeException(e);
        } finally {
            closeContext(context);
        }
    }

    @Override
    public void enqueue(String submissionId) {
        enqueue(Collections.singleton(submissionId));
    }

    @Override
    public void enqueue(Collection<String> submissionIds) {
        if (submissionIds.isEmpty()) {
            return;
        }

        QueueConnection connection = null;
        try {
            connection = connectionFactory.createQueueConnection();
            Session session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(queue);
            for (String submissionId : submissionIds) {
                TextMessage message = session.createTextMessage();
                message.setText(submissionId);
                producer.send(message);
            }
            logger.info("Enqueued {} submissions for evaluation", submissionIds.size());
        } catch (JMSException e) {
            throw new DAOException("Error during enqueue", e);
        } finally {
            closeConnection(connection);
        }
    }

    @Override
    public synchronized void addWorker(EvaluationJobWorker worker) {
        try {
            workers.add(new JmsEvaluationJobWorkerWrapper(connectionFactory, queue, worker));
        } catch (JMSException e) {
            throw new DAOException("Error while adding worker", e);
        }
    }

    @Override
    public synchronized void cleanup() {
        workers.forEach(JmsEvaluationJobWorkerWrapper::cleanup);
    }

    private void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (JMSException ignored) {
            }
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
}
