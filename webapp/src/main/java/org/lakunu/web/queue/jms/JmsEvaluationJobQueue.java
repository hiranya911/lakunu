package org.lakunu.web.queue.jms;

import org.lakunu.web.dao.DAOException;
import org.lakunu.web.queue.EvaluationJobQueue;
import org.lakunu.web.utils.ConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Collection;
import java.util.Collections;

public final class JmsEvaluationJobQueue implements EvaluationJobQueue {

    private static final Logger logger = LoggerFactory.getLogger(JmsEvaluationJobQueue.class);

    private static final String JOB_QUEUE_CONNECTION_FACTORY = "jobQueue.cf";
    private static final String JOB_QUEUE_NAME = "jobQueue.name";

    private final QueueConnectionFactory connectionFactory;
    private final Queue queue;

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
        try (
                QueueConnection connection = connectionFactory.createQueueConnection();
                Session session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
                MessageProducer producer = session.createProducer(queue)
        ) {
            for (String submissionId : submissionIds) {
                TextMessage message = session.createTextMessage();
                message.setText(submissionId);
                producer.send(message);
            }
        } catch (JMSException e) {
            throw new DAOException("Error during enqueue", e);
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
