package org.lakunu.web.dao.jdbc;

import com.google.common.collect.ImmutableList;
import org.lakunu.web.dao.DAOException;
import org.lakunu.web.dao.EvaluationDAO;
import org.lakunu.web.models.Submission;
import org.lakunu.web.utils.Security;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.google.common.base.Preconditions.checkNotNull;

public final class JdbcEvaluationDAO implements EvaluationDAO {

    private final DataSource dataSource;

    public JdbcEvaluationDAO(DataSource dataSource) {
        checkNotNull(dataSource, "DataSource is required");
        this.dataSource = dataSource;
    }

    @Override
    public ImmutableList<Submission> getOwnedSubmissions(String courseId, String labId) {
        try {
            return GetOwnedSubmissionsCommand.execute(dataSource, labId);
        } catch (SQLException e) {
            throw new DAOException("Error while retrieving submissions", e);
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
