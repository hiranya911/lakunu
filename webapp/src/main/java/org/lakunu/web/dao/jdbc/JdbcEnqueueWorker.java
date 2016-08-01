package org.lakunu.web.dao.jdbc;

import org.lakunu.web.queue.EvaluationJobQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.google.common.base.Preconditions.checkNotNull;

final class JdbcEnqueueWorker implements Runnable {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final DataSource dataSource;
    private final EvaluationJobQueue jobQueue;
    private final ExecutorService executor;
    private final Future<?> future;

    private boolean proceed;

    public JdbcEnqueueWorker(DataSource dataSource, EvaluationJobQueue jobQueue) {
        this.dataSource = dataSource;
        checkNotNull(jobQueue, "JobQueue is required");
        this.jobQueue = jobQueue;
        this.executor = Executors.newSingleThreadExecutor();
        this.proceed = true;
        this.future = this.executor.submit(this);
    }

    @Override
    public final void run() {
        logger.info("Initializing enqueue worker");
        while (proceed) {
            try {
                int count = new EnqueueCommand(dataSource, jobQueue).run();
                if (count == 0) {
                    try {
                        Thread.sleep(15000);
                    } catch (InterruptedException ignored) {
                    }
                }
            } catch (Exception e) {
                logger.error("Error while executing enqueue operation", e);
            }
        }
    }

    public void cleanup() {
        this.proceed = false;
        this.future.cancel(true);
        this.executor.shutdownNow();
        logger.info("Enqueue worker terminated");
    }

    private static class EnqueueCommand extends TxCommand<Integer> {

        private static final String LOCK_SQL = "SELECT * FROM job_queue_lock FOR UPDATE";
        private static final String SELECT_JOBS_SQL = "SELECT id, submission_id from job_queue";
        private static final String DELETE_JOBS_SQL = "DELETE FROM job_queue WHERE id = ?";

        private final EvaluationJobQueue jobQueue;

        private EnqueueCommand(DataSource dataSource, EvaluationJobQueue jobQueue) {
            super(dataSource);
            this.jobQueue = jobQueue;
        }

        @Override
        protected Integer doTransaction(Connection connection) throws SQLException {
            try (PreparedStatement stmt = connection.prepareStatement(LOCK_SQL)) {
                stmt.executeQuery();
            }

            List<Long> idList = new ArrayList<>();
            List<String> submissions = new ArrayList<>();
            try (
                    PreparedStatement stmt = connection.prepareStatement(SELECT_JOBS_SQL);
                    ResultSet rs = stmt.executeQuery()

            ) {
                while (rs.next()) {
                    idList.add(rs.getLong("id"));
                    submissions.add(String.valueOf(rs.getLong("submission_id")));
                }
            }

            if (!submissions.isEmpty()) {
                jobQueue.enqueue(submissions);
                try (PreparedStatement stmt = connection.prepareStatement(DELETE_JOBS_SQL)) {
                    for (long id : idList) {
                        stmt.setLong(1, id);
                        stmt.addBatch();
                    }
                    stmt.executeBatch();
                }
            }

            return submissions.size();
        }
    }

}
