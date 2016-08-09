package org.lakunu.web.dao.jdbc;

import com.google.common.collect.ImmutableList;
import org.lakunu.labs.Score;
import org.lakunu.labs.utils.LabUtils;
import org.lakunu.web.dao.DAOException;
import org.lakunu.web.dao.SubmissionDAO;
import org.lakunu.web.models.*;
import org.lakunu.web.utils.Security;

import javax.sql.DataSource;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public final class JdbcSubmissionDAO implements SubmissionDAO {

    private final DataSource dataSource;

    public JdbcSubmissionDAO(DataSource dataSource) {
        checkNotNull(dataSource, "DataSource is required");
        this.dataSource = dataSource;
    }

    @Override
    public String addSubmission(Submission submission) {
        try {
            return AddSubmissionCommand.execute(dataSource, submission);
        } catch (SQLException e) {
            throw new DAOException("Error while submitting lab", e);
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

    @Override
    public ImmutableList<SubmissionView> getOwnedSubmissions(Lab lab, int limit) {
        try {
            return GetOwnedSubmissionsCommand.execute(dataSource, lab.getId(), limit);
        } catch (SQLException e) {
            throw new DAOException("Error while retrieving submissions", e);
        }
    }

    @Override
    public ImmutableList<SubmissionView> getSubmissionsByUser(Lab lab, String userId, int limit) {
        try {
            return GetSubmissionsByUserCommand.execute(dataSource, userId, lab.getId(), limit);
        } catch (SQLException e) {
            throw new DAOException("Error while retrieving submissions", e);
        }
    }

    @Override
    public ImmutableList<SubmissionView> getAllSubmissions(Lab lab) {
        try {
            return GetAllSubmissionsCommand.execute(dataSource, lab.getId());
        } catch (SQLException e) {
            throw new DAOException("Error while retrieving submissions", e);
        }
    }

    private static final class AddSubmissionCommand extends TxCommand<String> {

        private static final String ADD_SUBMISSION_SQL = "INSERT INTO submission (user_id, " +
                "lab_id, submitted_at, submission_type, submission_data) VALUES (?,?,?,?,?)";
        private static final String ENQUEUE_SUBMISSION_SQL =
                "INSERT INTO job_queue (submission_id) VALUES (?)";

        private final Submission submission;

        private AddSubmissionCommand(DataSource dataSource, Submission submission) {
            super(dataSource);
            this.submission = submission;
        }

        private static String execute(DataSource dataSource,
                                      Submission submission) throws SQLException {
            return new AddSubmissionCommand(dataSource, submission).run();
        }

        @Override
        protected String doTransaction(Connection connection) throws SQLException {
            long submissionId;
            try (PreparedStatement stmt = connection.prepareStatement(ADD_SUBMISSION_SQL,
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
                int rows = stmt.executeUpdate();
                checkState(rows == 1, "Failed to enqueue the submission");
            }
            return String.valueOf(submissionId);
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

    private static String prepareArrayQuery(String sql, int args) {
        String params = IntStream.range(0, args).mapToObj(i -> "?").collect(Collectors.joining(","));
        return String.format(sql, params);
    }

    private static class GetAllSubmissionsCommand extends Command<ImmutableList<SubmissionView>> {

        private static final String GET_SUBMISSIONS_SQL = "SELECT id, user_id, lab_id, submitted_at, " +
                "submission_type FROM submission WHERE lab_id = ? ORDER BY submitted_at DESC";

        private static final String GET_EVALUATIONS_SQL = "SELECT id, submission_id, " +
                "started_at, finished_at, finishing_status, log FROM evaluation WHERE " +
                "submission_id IN (%s) ORDER BY finished_at DESC";

        private static final String GET_GRADES_SQL = "SELECT id, evaluation_id, label, score, " +
                "score_limit FROM grade WHERE evaluation_id IN (%s)";

        protected final long labId;

        protected GetAllSubmissionsCommand(DataSource dataSource, String labId) {
            super(dataSource);
            this.labId = Long.parseLong(labId);
        }

        private static ImmutableList<SubmissionView> execute(DataSource dataSource,
                                                            String labId) throws SQLException {
            return new GetAllSubmissionsCommand(dataSource, labId).run();
        }

        protected PreparedStatement getStatement(Connection connection) throws SQLException {
            PreparedStatement stmt = null;
            try {
                stmt = connection.prepareStatement(GET_SUBMISSIONS_SQL);
                stmt.setLong(1, labId);
                return stmt;
            } catch (SQLException e) {
                if (stmt != null) {
                    stmt.close();
                }
                throw e;
            }
        }

        private Map<Long,SubmissionView.Builder> getSubmissions(Connection connection)
                throws SQLException {
            Map<Long,SubmissionView.Builder> views = new LinkedHashMap<>();

            try (PreparedStatement stmt = getStatement(connection)) {
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        long submissionId = rs.getLong("id");
                        SubmissionView.Builder view = SubmissionView.newBuilder()
                                .setId(String.valueOf(submissionId))
                                .setLabId(String.valueOf(rs.getLong("lab_id")))
                                .setSubmittedAt(rs.getTimestamp("submitted_at"))
                                .setType(rs.getString("submission_type"))
                                .setUserId(rs.getString("user_id"));
                        views.put(submissionId, view);
                    }
                }
            }
            return views;
        }

        private ImmutableList<Evaluation> getEvaluations(Connection connection,
                                                         Long[] submissionIds) throws SQLException {
            Map<Long,Evaluation.Builder> evaluations = new LinkedHashMap<>();
            try (PreparedStatement stmt = connection.prepareStatement(
                    prepareArrayQuery(GET_EVALUATIONS_SQL, submissionIds.length))) {
                for (int i = 0; i < submissionIds.length; i++) {
                    stmt.setLong(i + 1, submissionIds[i]);
                }
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        long id = rs.getLong("id");
                        Evaluation.Builder builder = Evaluation.newBuilder()
                                .setId(String.valueOf(id))
                                .setSubmissionId(String.valueOf(rs.getLong("submission_id")))
                                .setStartedAt(rs.getTimestamp("started_at"))
                                .setFinishedAt(rs.getTimestamp("finished_at"))
                                .setFinishingStatus(EvaluationStatus.fromInt(rs.getInt("finishing_status")))
                                .setLog(rs.getString("log"));
                        evaluations.put(id, builder);
                    }
                }
            }
            if (evaluations.isEmpty()) {
                return ImmutableList.of();
            }

            Long[] evaluationIds = evaluations.keySet().stream().toArray(Long[]::new);
            try (PreparedStatement stmt = connection.prepareStatement(
                    prepareArrayQuery(GET_GRADES_SQL, evaluationIds.length))) {
                for (int i = 0; i < evaluationIds.length; i++) {
                    stmt.setLong(i + 1, evaluationIds[i]);
                }
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Evaluation.Builder builder = evaluations.get(rs.getLong("evaluation_id"));
                        String label = rs.getString("label");
                        double score = rs.getDouble("score");
                        double limit = rs.getDouble("score_limit");
                        builder.addScore(Score.create(label, score, limit));
                    }
                }
            }
            return evaluations.values().stream().map(Evaluation.Builder::build)
                    .collect(LabUtils.immutableList());
        }

        @Override
        protected ImmutableList<SubmissionView> doRun(Connection connection) throws SQLException {
            Map<Long,SubmissionView.Builder> views = getSubmissions(connection);
            if (!views.isEmpty()) {
                ImmutableList<Evaluation> evaluations = getEvaluations(connection, views.keySet()
                        .stream().toArray(Long[]::new));
                evaluations.forEach(e ->
                        views.get(Long.parseLong(e.getSubmissionId())).addEvaluation(e));
            }
            return views.values().stream().map(SubmissionView.Builder::build)
                    .collect(LabUtils.immutableList());
        }
    }

    private static class GetSubmissionsByUserCommand extends GetAllSubmissionsCommand {

        private static final String GET_SUBMISSIONS_BY_USER_SQL = "SELECT id, user_id, lab_id, " +
                "submitted_at, submission_type FROM submission WHERE user_id = ? AND lab_id = ? " +
                "ORDER BY submitted_at DESC";

        private final String userId;
        private final int limit;

        protected GetSubmissionsByUserCommand(DataSource dataSource, String userId, String labId,
                                              int limit) {
            super(dataSource, labId);
            this.userId = userId;
            this.limit = limit;
        }

        private static ImmutableList<SubmissionView> execute(DataSource dataSource, String userId,
                                                      String labId, int limit) throws SQLException {
            return new GetSubmissionsByUserCommand(dataSource, userId, labId, limit).run();
        }

        protected PreparedStatement getStatement(Connection connection) throws SQLException {
            String sql = GET_SUBMISSIONS_BY_USER_SQL;
            if (limit >= 0) {
                sql += (" LIMIT " + limit);
            }

            PreparedStatement stmt = null;
            try {
                stmt = connection.prepareStatement(sql);
                stmt.setString(1, userId);
                stmt.setLong(2, labId);
                return stmt;
            } catch (SQLException e) {
                if (stmt != null) {
                    stmt.close();
                }
                throw e;
            }
        }
    }

    private static class GetOwnedSubmissionsCommand extends GetSubmissionsByUserCommand {

        private GetOwnedSubmissionsCommand(DataSource dataSource, String labId, int limit) {
            super(dataSource, Security.getCurrentUser(), labId, limit);
        }

        private static ImmutableList<SubmissionView> execute(DataSource dataSource, String labId,
                                                             int limit) throws SQLException {
            return new GetOwnedSubmissionsCommand(dataSource, labId, limit).run();
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
