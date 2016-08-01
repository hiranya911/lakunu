package org.lakunu.web.dao.jdbc;

import org.lakunu.web.dao.DAOException;
import org.lakunu.web.dao.EnqueueWorker;
import org.lakunu.web.queue.EvaluationJobQueue;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class JdbcEnqueueWorker extends EnqueueWorker {

    private final DataSource dataSource;

    public JdbcEnqueueWorker(DataSource dataSource, EvaluationJobQueue jobQueue) {
        super(jobQueue);
        checkNotNull(dataSource, "DataSource is required");
        this.dataSource = dataSource;
    }

    @Override
    protected int enqueue(EvaluationJobQueue jobQueue) {
        try {
            return new EnqueueCommand(dataSource, jobQueue).run();
        } catch (SQLException e) {
            throw new DAOException("Error while enqueuing jobs", e);
        }
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
