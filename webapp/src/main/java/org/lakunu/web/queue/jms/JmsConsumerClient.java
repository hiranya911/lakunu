package org.lakunu.web.queue.jms;

import org.lakunu.web.service.EvaluationJobWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;


public final class JmsConsumerClient implements MessageListener, ExceptionListener {

    private enum State {
        READY,
        CONNECTING,
        CONNECTED,
        FINISHED
    }

    private static final Logger logger = LoggerFactory.getLogger(JmsConsumerClient.class);

    private final QueueConnectionFactory connectionFactory;
    private final Queue queue;
    private final EvaluationJobWorker worker;
    private final AtomicReference<State> state = new AtomicReference<>(State.READY);
    private final ExecutorService exec = Executors.newSingleThreadExecutor();

    private Future<?> currentConnector;
    private JmsConnection connection;

    public JmsConsumerClient(QueueConnectionFactory connectionFactory, Queue queue,
                             EvaluationJobWorker worker) {
        this.connectionFactory = connectionFactory;
        this.queue = queue;
        this.worker = worker;
        attemptConnection();
    }

    private void attemptConnection() {
        synchronized (state) {
            if (state.compareAndSet(State.READY, State.CONNECTING)) {
                currentConnector = exec.submit(new JmsConnector());
            }
        }
    }

    @Override
    public void onException(JMSException e) {
        logger.warn("Error in JMS connection", e);
        synchronized (state) {
            if (state.compareAndSet(State.CONNECTED, State.READY)) {
                logger.info("Tearing down existing JMS connection");
                connection.close();
                connection = null;
            }
        }
        attemptConnection();
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
        }
    }

    void cleanup() {
        synchronized (state) {
            state.set(State.FINISHED);
            state.notify();
            if (connection != null) {
                connection.close();
                connection = null;
            }
            if (currentConnector != null) {
                currentConnector.cancel(true);
                currentConnector = null;
            }
            exec.shutdownNow();
        }
    }

    private class JmsConnector implements Runnable {

        private static final long MAX_DELAY = 60000L;

        private long delay = 1000L;

        @Override
        public void run() {
            synchronized (state) {
                logger.info("Attempting to connect to JMS broker");
                JmsConnection newConnection = connectLoop();
                if (newConnection != null) {
                    if (state.compareAndSet(State.CONNECTING, State.CONNECTED)) {
                        connection = newConnection;
                        logger.info("Established JMS connection");
                    } else {
                        newConnection.close();
                    }
                }
                currentConnector = null;
            }
        }

        private JmsConnection connectLoop() {
            while (state.get() == State.CONNECTING) {
                JmsConnection newConnection = null;
                try {
                    newConnection = new JmsConnection(connectionFactory, queue);
                    newConnection.start(JmsConsumerClient.this);
                    return newConnection;
                } catch (JMSException e) {
                    logger.error("Failed to establish JMS connection - Retrying", e);
                    if (newConnection != null) {
                        newConnection.close();
                    }
                }

                try {
                    state.wait(delay);
                } catch (InterruptedException ignored) {
                }
                delay = delay * 2 < MAX_DELAY ? delay * 2 : MAX_DELAY;
            }
            return null;
        }
    }

    private static class JmsConnection {

        private final Connection connection;
        private final Session session;
        private final MessageConsumer consumer;

        private JmsConnection(QueueConnectionFactory connectionFactory, Queue queue) throws JMSException {
            connection = connectionFactory.createQueueConnection();
            try {
                session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                consumer = session.createConsumer(queue);
            } catch (JMSException e) {
                closeSilently(connection);
                throw e;
            }
        }

        private void start(JmsConsumerClient client) throws JMSException {
            consumer.setMessageListener(client);
            connection.setExceptionListener(client);
            connection.start();
        }

        private void close() {
            closeSilently(connection);
        }

    }

    private static void closeSilently(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception ignored) {
            }
        }
    }
}
