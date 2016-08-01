package org.lakunu.web.dao.jdbc;

import com.google.common.collect.ImmutableList;
import org.lakunu.web.dao.DAOException;
import org.lakunu.web.dao.SubmissionDAO;
import org.lakunu.web.models.Submission;
import org.lakunu.web.queue.EvaluationJobQueue;
import org.lakunu.web.utils.Security;

import javax.sql.DataSource;

import java.sql.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public final class JdbcSubmissionDAO implements SubmissionDAO {

    private final DataSource dataSource;

    public JdbcSubmissionDAO(DataSource dataSource) {
        checkNotNull(dataSource, "DataSource is required");
        this.dataSource = dataSource;
    }

    @Override
    public String addSubmission(Submission submission, EvaluationJobQueue jobQueue) {
        try {
            return AddSubmissionCommand.execute(dataSource, submission, jobQueue);
        } catch (SQLException e) {
            throw new DAOException("Error while submitting lab", e);
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

    @Override
    public Submission getSubmission(String submissionId) {
        try {
            return GetSubmissionCommand.execute(dataSource, submissionId);
        } catch (SQLException e) {
            throw new DAOException("Error while retrieving submission", e);
        }
    }

    private static final class AddSubmissionCommand extends TxCommand<String> {

        private static final String SUBMIT_LAB_SQL = "INSERT INTO submission (user_id, " +
                "lab_id, submitted_at, submission_type, submission_data) VALUES (?,?,?,?,?)";

        private final Submission submission;
        private final EvaluationJobQueue jobQueue;

        private AddSubmissionCommand(DataSource dataSource, Submission submission,
                                     EvaluationJobQueue jobQueue) {
            super(dataSource);
            this.submission = submission;
            this.jobQueue = jobQueue;
        }

        private static String execute(DataSource dataSource, Submission submission,
                                      EvaluationJobQueue jobQueue) throws SQLException {
            return new AddSubmissionCommand(dataSource, submission, jobQueue).run();
        }

        @Override
        protected String doTransaction(Connection connection) throws SQLException {
            try (PreparedStatement stmt = connection.prepareStatement(SUBMIT_LAB_SQL,
                    Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, submission.getUserId());
                stmt.setLong(2, Long.parseLong(submission.getLabId()));
                stmt.setTimestamp(3, new Timestamp(submission.getSubmittedAt().getTime()));
                stmt.setString(4, submission.getType());
                stmt.setBytes(5, submission.getData());
                int rows = stmt.executeUpdate();
                checkState(rows == 1, "Failed to add the lab to database");
                String submissionId;
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        submissionId = String.valueOf(rs.getLong(1));
                    } else {
                        throw new IllegalStateException("Failed to retrieve new lab ID");
                    }
                }

                jobQueue.enqueue(submissionId);
                return submissionId;
            }
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
                        builder.add(createSubmission(rs));
                    }
                    return builder.build();
                }
            }
        }
    }

    private static final class GetSubmissionCommand extends Command<Submission> {

        private static final String GET_SUBMISSION_SQL = "SELECT id, user_id, lab_id, submitted_at, " +
                "submission_type, submission_data FROM submission WHERE id = ?";

        private final long submissionId;

        private GetSubmissionCommand(DataSource dataSource, String submissionId) {
            super(dataSource);
            this.submissionId = Long.parseLong(submissionId);
        }

        private static Submission execute(DataSource dataSource,
                                          String submissionId) throws SQLException {
            return new GetSubmissionCommand(dataSource, submissionId).run();
        }

        @Override
        protected Submission doRun(Connection connection) throws SQLException {
            try (PreparedStatement stmt = connection.prepareStatement(GET_SUBMISSION_SQL)) {
                stmt.setLong(1, submissionId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return createSubmission(rs);
                    } else {
                        return null;
                    }
                }
            }
        }
    }

    private static Submission createSubmission(ResultSet rs) throws SQLException {
        return Submission.newBuilder()
                .setId(String.valueOf(rs.getLong("id")))
                .setUserId(rs.getString("user_id"))
                .setLabId(String.valueOf(rs.getLong("lab_id")))
                .setType(rs.getString("submission_type"))
                .setSubmittedAt(rs.getTimestamp("submitted_at"))
                .setData(rs.getBytes("submission_data"))
                .build();
    }
}
