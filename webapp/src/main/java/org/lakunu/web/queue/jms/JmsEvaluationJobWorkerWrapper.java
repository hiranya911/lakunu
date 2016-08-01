package org.lakunu.web.queue.jms;

import org.lakunu.web.service.EvaluationJobWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

import static com.google.common.base.Preconditions.checkNotNull;

public final class JmsEvaluationJobWorkerWrapper implements MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(JmsEvaluationJobWorkerWrapper.class);

    private final Connection connection;
    private final Session session;
    private final MessageConsumer consumer;
    private final EvaluationJobWorker worker;

    JmsEvaluationJobWorkerWrapper(QueueConnectionFactory connectionFactory, Queue queue,
                                  EvaluationJobWorker worker) throws JMSException {
        checkNotNull(connectionFactory, "ConnectionFactory is required");
        checkNotNull(worker, "Worker is required");
        this.worker = worker;
        connection = connectionFactory.createQueueConnection();
        try {
            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            consumer = session.createConsumer(queue);
            consumer.setMessageListener(this);
            connection.start();
            logger.info("Initialized evaluation job worker");
        } catch (JMSException e) {
            closeSilently(connection);
            throw e;
        }
    }

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                TextMessage text = (TextMessage) message;
                worker.evaluate(text.getText());
            }
        } catch (Exception e) {
            logger.error("Error while processing message", e);
        } finally {
            try {
                message.acknowledge();
            } catch (JMSException e) {
                logger.error("Error while acknowledging receipt of message", e);
            }
        }
    }

    void cleanup() {
        closeSilently(connection);
    }

    private void closeSilently(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception ignored) {
            }
        }
    }
}
