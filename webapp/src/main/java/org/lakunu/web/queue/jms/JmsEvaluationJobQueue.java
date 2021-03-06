package org.lakunu.web.queue.jms;

import com.google.common.collect.ImmutableList;
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
import java.util.Enumeration;
import java.util.List;

public final class JmsEvaluationJobQueue implements EvaluationJobQueue {

    private static final Logger logger = LoggerFactory.getLogger(JmsEvaluationJobQueue.class);

    private static final String JOB_QUEUE_CONNECTION_FACTORY = "jobQueue.cf";
    private static final String JOB_QUEUE_NAME = "jobQueue.name";

    private final QueueConnectionFactory connectionFactory;
    private final Queue queue;
    private final List<JmsConsumerClient> workers = new ArrayList<>();

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
    public void enqueue(Collection<String> submissionIds) {
        if (submissionIds.isEmpty()) {
            return;
        }

        QueueConnection connection = null;
        Session session = null;
        try {
            connection = connectionFactory.createQueueConnection();
            session = connection.createQueueSession(true, Session.SESSION_TRANSACTED);
            MessageProducer producer = session.createProducer(queue);
            for (String submissionId : submissionIds) {
                TextMessage message = session.createTextMessage();
                message.setText(submissionId);
                producer.send(message);
            }
            logger.info("Enqueued {} submissions for evaluation", submissionIds.size());
            session.commit();
        } catch (JMSException e) {
            rollback(session);
            throw new RuntimeException("Error during enqueue", e);
        } finally {
            closeConnection(connection);
        }
    }

    @Override
    public ImmutableList<String> getPendingSubmissions() {
        QueueConnection connection = null;
        Session session;
        try {
            connection = connectionFactory.createQueueConnection();
            session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            QueueBrowser browser = session.createBrowser(queue);
            ImmutableList.Builder<String> submissions = ImmutableList.builder();
            Enumeration messages = browser.getEnumeration();
            while (messages.hasMoreElements()) {
                Object message = messages.nextElement();
                if (message instanceof TextMessage) {
                    submissions.add(((TextMessage) message).getText());
                }
            }
            return submissions.build();
        } catch (JMSException e) {
            throw new RuntimeException("Error during query", e);
        } finally {
            closeConnection(connection);
        }
    }

    private void rollback(Session session) {
        if (session != null) {
            try {
                session.rollback();
            } catch (JMSException e) {
                logger.error("Error while rolling back session", e);
            }
        }
    }

    @Override
    public synchronized void addWorker(EvaluationJobWorker worker) {
        workers.add(new JmsConsumerClient(connectionFactory, queue, worker));
    }

    @Override
    public synchronized void cleanup() {
        workers.forEach(JmsConsumerClient::cleanup);
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
