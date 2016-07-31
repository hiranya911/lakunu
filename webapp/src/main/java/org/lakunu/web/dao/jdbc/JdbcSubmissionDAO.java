package org.lakunu.web.dao.jdbc;

import com.google.common.collect.ImmutableList;
import org.lakunu.web.dao.DAOException;
import org.lakunu.web.dao.SubmissionDAO;
import org.lakunu.web.models.JobEntry;
import org.lakunu.web.models.Submission;
import org.lakunu.web.utils.Security;

import javax.sql.DataSource;

import java.sql.*;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public final class JdbcSubmissionDAO implements SubmissionDAO {

    private final DataSource dataSource;

    public JdbcSubmissionDAO(DataSource dataSource) {
        checkNotNull(dataSource, "DataSource is required");
        this.dataSource = dataSource;
    }

    @Override
    public String addAndEnqueueSubmission(Submission submission) {
        try {
            return AddSubmissionCommand.execute(dataSource, submission);
        } catch (SQLException e) {
            throw new DAOException("Error while submitting lab", e);
        }
    }

    @Override
    public JobEntry getNextEntryForProcessing() {
        try {
            return GetNextEntryCommand.execute(dataSource);
        } catch (SQLException e) {
            throw new DAOException("Error while polling job queue", e);
        }
    }

    @Override
    public ImmutableList<Submission> getOwnedSubmissions(String courseId, String labId) {
        try {
            return GetOwnedSubmissionsCommand.execute(dataSource, labId);
        } catch (SQLException e) {
            throw new DAOException("Error while retrieving submissions", e);
        }
    }

    private static final class AddSubmissionCommand extends TxCommand<String> {

        private static final String SUBMIT_LAB_SQL = "INSERT INTO submission (user_id, " +
                "lab_id, submitted_at, submission_type, submission_data) VALUES (?,?,?,?,?)";
        private static final String ENQUEUE_SUBMISSION_SQL = "INSERT INTO job_queue " +
                "(submission_id, status) VALUES (?,?)";

        private final Submission submission;

        private AddSubmissionCommand(DataSource dataSource, Submission submission) {
            super(dataSource);
            this.submission = submission;
        }

        private static String execute(DataSource dataSource, Submission submission) throws SQLException {
            return new AddSubmissionCommand(dataSource, submission).run();
        }

        @Override
        protected String doTransaction(Connection connection) throws SQLException {
            long submissionId;
            try (PreparedStatement stmt = connection.prepareStatement(SUBMIT_LAB_SQL,
                    Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, submission.getUserId());
                stmt.setLong(2, Long.parseLong(submission.getLabId()));
                stmt.setTimestamp(3, new Timestamp(submission.getSubmittedAt().getTime()));
                stmt.setString(4, submission.getType());
                stmt.setBytes(5, submission.getData());
                int rows = stmt.executeUpdate();
                checkState(rows == 1, "Failed to add the lab to database");
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        submissionId = rs.getLong(1);
                    } else {
                        throw new IllegalStateException("Failed to retrieve new lab ID");
                    }
                }

            }

            try (PreparedStatement stmt = connection.prepareStatement(ENQUEUE_SUBMISSION_SQL)) {
                stmt.setLong(1, submissionId);
                stmt.setInt(2, JobEntry.JOB_ENTRY_STATUS_READY);
                int rows = stmt.executeUpdate();
                checkState(rows == 1, "Failed to add the lab to the job queue");
            }
            return String.valueOf(submissionId);
        }
    }

    private static final class GetNextEntryCommand extends TxCommand<JobEntry> {

        private static final String GET_NEXT_ENTRY_SQL = "SELECT id, submission_id, status, " +
                "started_at FROM job_queue WHERE status = ? OR (status = ? AND started_at < ?) LIMIT 1";
        private static final String UPDATE_ENTRY_SQL = "UPDATE job_queue SET status = ?, " +
                "started_at = ? WHERE id = ?";

        private GetNextEntryCommand(DataSource dataSource) {
            super(dataSource, Connection.TRANSACTION_SERIALIZABLE);
        }

        private static JobEntry execute(DataSource dataSource) throws SQLException {
            return new GetNextEntryCommand(dataSource).run();
        }

        @Override
        protected JobEntry doTransaction(Connection connection) throws SQLException {
            Calendar calendar = Calendar.getInstance();
            Timestamp now = new Timestamp(calendar.getTime().getTime());
            calendar.add(Calendar.HOUR_OF_DAY, -1);
            Timestamp oneHourAgo = new Timestamp(calendar.getTime().getTime());

            JobEntry jobEntry = null;
            try (PreparedStatement stmt = connection.prepareStatement(GET_NEXT_ENTRY_SQL)) {
                stmt.setInt(1, JobEntry.JOB_ENTRY_STATUS_READY);
                stmt.setInt(2, JobEntry.JOB_ENTRY_STATUS_PROCESSING);
                stmt.setTimestamp(3, oneHourAgo);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        jobEntry = JobEntry.newBuilder()
                                .setId(String.valueOf(rs.getLong("id")))
                                .setSubmissionId(String.valueOf(rs.getLong("submission_id")))
                                .setStatus(rs.getInt("status"))
                                .setStartedAt(rs.getTimestamp("started_at"))
                                .build();
                    }
                }
            }

            if (jobEntry != null) {
                try (PreparedStatement stmt = connection.prepareStatement(UPDATE_ENTRY_SQL)) {
                    stmt.setInt(1, JobEntry.JOB_ENTRY_STATUS_PROCESSING);
                    stmt.setTimestamp(2, now);
                    stmt.setLong(3, Long.parseLong(jobEntry.getId()));
                    stmt.executeUpdate();
                }
            }

            return jobEntry;
        }
    }

    private static final class GetOwnedSubmissionsCommand extends Command<ImmutableList<Submission>> {

        private static final String GET_OWNED_SUBMISSIONS_SQL = "SELECT id, user_id, lab_id, submitted_at, " +
                "submission_type, submission_data FROM submission WHERE user_id = ? AND lab_id = ?";

        private final long labId;

        private GetOwnedSubmissionsCommand(DataSource dataSource, String labId) {
            super(dataSource);
            this.labId = Long.parseLong(labId);
        }

        private static ImmutableList<Submission> execute(DataSource dataSource,
                                                         String labId) throws SQLException {
            return new GetOwnedSubmissionsCommand(dataSource, labId).run();
        }

        @Override
        protected ImmutableList<Submission> doRun(Connection connection) throws SQLException {
            try (PreparedStatement stmt = connection.prepareStatement(GET_OWNED_SUBMISSIONS_SQL)) {
                stmt.setString(1, Security.getCurrentUser());
                stmt.setLong(2, labId);
                try (ResultSet rs = stmt.executeQuery()) {
                    ImmutableList.Builder<Submission> builder = ImmutableList.builder();
                    while (rs.next()) {
                        Submission submission = Submission.newBuilder()
                                .setLabId(String.valueOf(rs.getLong("id")))
                                .setUserId(rs.getString("user_id"))
                                .setLabId(String.valueOf(rs.getLong("lab_id")))
                                .setType(rs.getString("submission_type"))
                                .setSubmittedAt(rs.getTimestamp("submitted_at"))
                                .setData(rs.getBytes("submission_data"))
                                .build();
                        builder.add(submission);
                    }
                    return builder.build();
                }
            }
        }
    }
}
