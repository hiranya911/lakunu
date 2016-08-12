package org.lakunu.web.dao.jdbc;

import com.google.common.collect.ImmutableList;
import org.lakunu.web.dao.DAOException;
import org.lakunu.web.dao.JobQueueBrowserDAO;
import org.lakunu.web.queue.EvaluationJobQueue;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.google.common.base.Preconditions.checkNotNull;

public class JdbcJobQueueBrowserDAO extends JobQueueBrowserDAO {

    private final DataSource dataSource;

    JdbcJobQueueBrowserDAO(DataSource dataSource, EvaluationJobQueue jobQueue) {
        super(jobQueue);
        checkNotNull(dataSource, "DataSource is required");
        this.dataSource = dataSource;
    }

    @Override
    public ImmutableList<String> getPendingSubmissions() {
        try {
            return BrowseCommand.execute(dataSource, jobQueue);
        } catch (SQLException e) {
            throw new DAOException("Error while retrieving pending submissions", e);
        }
    }

    public static class BrowseCommand extends Command<ImmutableList<String>> {

        private final EvaluationJobQueue jobQueue;

        private BrowseCommand(DataSource dataSource, EvaluationJobQueue jobQueue) {
            super(dataSource);
            this.jobQueue = jobQueue;
        }

        private static ImmutableList<String> execute(
                DataSource dataSource, EvaluationJobQueue jobQueue) throws SQLException {
            return new BrowseCommand(dataSource, jobQueue).run();
        }

        @Override
        protected ImmutableList<String> doRun(Connection connection) throws SQLException {
            ImmutableList.Builder<String> submissions = ImmutableList.builder();
            try (
                    PreparedStatement stmt = connection.prepareStatement(
                            JdbcEnqueueWorker.EnqueueCommand.SELECT_JOBS_SQL);
                    ResultSet rs = stmt.executeQuery()

            ) {
                while (rs.next()) {
                    submissions.add(String.valueOf(rs.getLong("submission_id")));
                }
            }

            submissions.addAll(jobQueue.getPendingSubmissions());
            return submissions.build();
        }
    }
}
