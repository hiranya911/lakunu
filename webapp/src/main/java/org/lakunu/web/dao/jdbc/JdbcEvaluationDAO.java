package org.lakunu.web.dao.jdbc;

import org.lakunu.labs.Score;
import org.lakunu.web.dao.DAOException;
import org.lakunu.web.dao.EvaluationDAO;
import org.lakunu.web.models.Evaluation;
import org.lakunu.web.models.Lab;
import org.lakunu.web.models.Submission;

import javax.sql.DataSource;
import java.sql.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public final class JdbcEvaluationDAO implements EvaluationDAO {

    private final DataSource dataSource;

    public JdbcEvaluationDAO(DataSource dataSource) {
        checkNotNull(dataSource, "DataSource is required");
        this.dataSource = dataSource;
    }

    @Override
    public Lab getLabForEvaluation(Submission submission) {
        try {
            return GetLabForEvaluationCommand.execute(dataSource, submission);
        } catch (SQLException e) {
            throw new DAOException("Error while retrieving lab", e);
        }
    }

    @Override
    public boolean addEvaluation(Evaluation evaluation, Lab lab) {
        try {
            return AddEvaluationCommand.execute(dataSource, evaluation, lab);
        } catch (SQLException e) {
            throw new DAOException("Error while adding evaluation", e);
        }
    }

    private static class GetLabForEvaluationCommand extends Command<Lab> {

        private static final String GET_LAB_SQL = "SELECT id, name, description, course_id, " +
                "created_at, created_by, config, published, submission_deadline, " +
                "allow_late_submissions FROM lab WHERE id = ?";

        private final long labId;

        private static Lab execute(DataSource dataSource, Submission submission) throws SQLException {
            return new GetLabForEvaluationCommand(dataSource, submission).run();
        }

        private GetLabForEvaluationCommand(DataSource dataSource, Submission submission) {
            super(dataSource);
            this.labId = Long.parseLong(submission.getLabId());
        }

        @Override
        protected Lab doRun(Connection connection) throws SQLException {
            try (PreparedStatement stmt = connection.prepareStatement(GET_LAB_SQL)) {
                stmt.setLong(1, labId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return JdbcLabDAO.createLab(rs);
                    } else {
                        throw new DAOException("Failed to find lab for submission");
                    }
                }
            }
        }
    }

    private static class AddEvaluationCommand extends TxCommand<Boolean> {

        private static final String GET_LAB_SQL = "SELECT id, name, description, course_id, " +
                "created_at, created_by, config, published, submission_deadline, " +
                "allow_late_submissions FROM lab WHERE id = (SELECT lab_id from submission " +
                "where id = ?) FOR UPDATE";
        private static final String ADD_EVAL_SQL = "INSERT INTO evaluation (submission_id, " +
                "started_at, finished_at, finishing_status, log) VALUES (?,?,?,?,?)";
        private static final String ADD_GRADE_SQL = "INSERT INTO grade (evaluation_id, label, " +
                "score, score_limit) VALUES (?,?,?,?)";

        private final Evaluation evaluation;
        private final Lab lab;

        public AddEvaluationCommand(DataSource dataSource, Evaluation evaluation, Lab lab) {
            super(dataSource);
            this.evaluation = evaluation;
            this.lab = lab;
        }

        public static boolean execute(DataSource dataSource, Evaluation evaluation,
                                      Lab lab) throws SQLException {
            return new AddEvaluationCommand(dataSource, evaluation, lab).run();
        }

        @Override
        protected Boolean doTransaction(Connection connection) throws SQLException {
            try (PreparedStatement stmt = connection.prepareStatement(GET_LAB_SQL)) {
                stmt.setLong(1, Long.parseLong(evaluation.getSubmissionId()));
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Lab currentLab = JdbcLabDAO.createLab(rs);
                        if (currentLab.getHash() != lab.getHash()) {
                            return false;
                        }
                    } else {
                        throw new DAOException("Failed to find lab for submission");
                    }
                }
            }

            long evaluationId;
            try (PreparedStatement stmt = connection.prepareStatement(ADD_EVAL_SQL,
                    Statement.RETURN_GENERATED_KEYS)) {
                stmt.setLong(1, Long.parseLong(evaluation.getSubmissionId()));
                stmt.setTimestamp(2, new Timestamp(evaluation.getStartedAt().getTime()));
                stmt.setTimestamp(3, new Timestamp(evaluation.getFinishedAt().getTime()));
                stmt.setInt(4, evaluation.getFinishingStatus().getStatus());
                stmt.setString(5, evaluation.getLog());
                int rows = stmt.executeUpdate();
                checkState(rows == 1, "Failed to add evaluation record");
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        evaluationId = rs.getLong(1);
                    } else {
                        throw new IllegalStateException("Failed to get evaluation ID");
                    }
                }
            }

            if (!evaluation.getScores().isEmpty()) {
                try (PreparedStatement stmt = connection.prepareStatement(ADD_GRADE_SQL)) {
                    for (Score score : evaluation.getScores()) {
                        stmt.setLong(1, evaluationId);
                        stmt.setString(2, score.getName());
                        stmt.setDouble(3, score.getValue());
                        stmt.setDouble(4, score.getLimit());
                        stmt.addBatch();
                    }
                    stmt.executeBatch();
                }
            }
            return true;
        }
    }

}
