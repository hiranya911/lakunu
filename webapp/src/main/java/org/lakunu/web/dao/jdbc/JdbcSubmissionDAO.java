package org.lakunu.web.dao.jdbc;

import com.google.common.collect.ImmutableList;
import org.lakunu.web.dao.DAOException;
import org.lakunu.web.dao.EvaluationJobQueue;
import org.lakunu.web.dao.SubmissionDAO;
import org.lakunu.web.models.Submission;
import org.lakunu.web.utils.Security;

import javax.sql.DataSource;

import java.sql.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public final class JdbcSubmissionDAO implements SubmissionDAO {

    private final DataSource dataSource;
    private final EvaluationJobQueue jobQueue;

    public JdbcSubmissionDAO(DataSource dataSource, EvaluationJobQueue jobQueue) {
        checkNotNull(dataSource, "DataSource is required");
        checkNotNull(jobQueue, "JobQueue is required");
        this.dataSource = dataSource;
        this.jobQueue = jobQueue;
    }

    @Override
    public String addSubmission(Submission submission) {
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
