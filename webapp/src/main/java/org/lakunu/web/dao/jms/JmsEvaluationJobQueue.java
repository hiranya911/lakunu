package org.lakunu.web.dao.jms;

import org.lakunu.web.dao.DAOException;
import org.lakunu.web.dao.EvaluationJobQueue;
import org.lakunu.web.utils.ConfigProperties;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public final class JmsEvaluationJobQueue implements EvaluationJobQueue {

    private static final String JOB_QUEUE_CONNECTION_FACTORY = "jobQueue.cf";
    private static final String JOB_QUEUE_NAME = "jobQueue.name";

    private final QueueConnectionFactory connectionFactory;
    private final Queue queue;

    public JmsEvaluationJobQueue(ConfigProperties properties) {
        InitialContext context = null;
        try {
            context = new InitialContext();
            connectionFactory = (QueueConnectionFactory) context.lookup(
                    properties.getRequired(JOB_QUEUE_CONNECTION_FACTORY));
            queue = (Queue) context.lookup(properties.getRequired(JOB_QUEUE_NAME));
        } catch (NamingException e) {
            throw new RuntimeException(e);
        } finally {
            closeContext(context);
        }
    }

    @Override
    public void enqueue(String submissionId) {
        try (
            QueueConnection connection = connectionFactory.createQueueConnection();
            Session session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(queue)
        ) {
            TextMessage message = session.createTextMessage();
            message.setText(submissionId);
            producer.send(message);
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
