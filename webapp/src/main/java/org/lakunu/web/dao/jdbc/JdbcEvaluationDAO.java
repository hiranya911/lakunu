package org.lakunu.web.dao.jdbc;

import org.lakunu.labs.Score;
import org.lakunu.web.dao.DAOException;
import org.lakunu.web.dao.EvaluationDAO;
import org.lakunu.web.models.EvaluationRecord;
import org.lakunu.web.models.Lab;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Date;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public final class JdbcEvaluationDAO implements EvaluationDAO {

    private final DataSource dataSource;

    public JdbcEvaluationDAO(DataSource dataSource) {
        checkNotNull(dataSource, "DataSource is required");
        this.dataSource = dataSource;
    }

    public EvaluationRecord startEvaluation(String submissionId, Date startedAt) {
        try {
            return StartEvaluationCommand.execute(dataSource, submissionId, startedAt);
        } catch (SQLException e) {
            throw new DAOException("Error while opening evaluation record", e);
        }
    }

    @Override
    public void finishEvaluation(EvaluationRecord record) {
        try {
            FinishEvaluationCommand.execute(dataSource, record);
        } catch (SQLException e) {
            throw new DAOException("Error while closing evaluation record", e);
        }
    }

    private static class StartEvaluationCommand extends TxCommand<EvaluationRecord> {

        private static final String GET_LAB_SQL = "SELECT id, name, description, course_id, " +
                "created_at, created_by, config, published, submission_deadline, " +
                "allow_late_submissions FROM lab WHERE id = (SELECT lab_id from submission where id = ?)";
        private static final String OPEN_EVAL_RECORD_SQL = "INSERT INTO evaluation " +
                "(submission_id, started_at) VALUES (?,?)";

        private final long submissionId;
        private final Timestamp startedAt;

        private static EvaluationRecord execute(DataSource dataSource, String submissionId,
                                                Date startedAt) throws SQLException {
            return new StartEvaluationCommand(dataSource, submissionId, startedAt).run();
        }

        private StartEvaluationCommand(DataSource dataSource, String submissionId, Date startedAt) {
            super(dataSource);
            this.submissionId = Long.parseLong(submissionId);
            this.startedAt = new Timestamp(startedAt.getTime());
        }

        @Override
        protected EvaluationRecord doTransaction(Connection connection) throws SQLException {
            Lab lab;
            try (PreparedStatement stmt = connection.prepareStatement(GET_LAB_SQL)) {
                stmt.setLong(1, submissionId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        lab = JdbcLabDAO.createLab(rs);
                    } else {
                        throw new DAOException("Failed to find lab for submission");
                    }
                }
            }

            long evalId;
            try (PreparedStatement stmt = connection.prepareStatement(OPEN_EVAL_RECORD_SQL,
                    Statement.RETURN_GENERATED_KEYS)) {
                stmt.setLong(1, submissionId);
                stmt.setTimestamp(2, startedAt);
                int rows = stmt.executeUpdate();
                checkState(rows == 1, "Failed to open evaluation record");
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        evalId = rs.getLong(1);
                    } else {
                        throw new DAOException("Failed to find evaluation ID");
                    }
                }
            }
            return new EvaluationRecord(String.valueOf(evalId), lab);
        }
    }

    private static class FinishEvaluationCommand extends TxCommand<Void> {

        private static final String CLOSE_EVAL_RECORD_SQL = "UPDATE evaluation set " +
                "finished_at = ?, finishing_status = ?, log = ? WHERE id = ?";
        private static final String ADD_GRADE_SQL = "INSERT INTO grade (evaluation_id, label, " +
                "score, score_limit) VALUES (?,?,?,?)";

        private final EvaluationRecord record;

        private FinishEvaluationCommand(DataSource dataSource, EvaluationRecord record) {
            super(dataSource);
            this.record = record;
        }

        public static void execute(DataSource dataSource,
                                   EvaluationRecord record) throws SQLException {
            new FinishEvaluationCommand(dataSource, record).run();
        }

        @Override
        protected Void doTransaction(Connection connection) throws SQLException {
            try (PreparedStatement stmt = connection.prepareStatement(CLOSE_EVAL_RECORD_SQL)) {
                stmt.setTimestamp(1, new Timestamp(record.getFinishedAt().getTime()));
                stmt.setInt(2, record.getFinishingStatus());
                stmt.setString(3, record.getLog());
                stmt.setLong(4, Long.parseLong(record.getId()));
                int rows = stmt.executeUpdate();
                checkState(rows == 1, "Failed to close evaluation record");
            }

            if (!record.getScores().isEmpty()) {
                try (PreparedStatement stmt = connection.prepareStatement(ADD_GRADE_SQL)) {
                    for (Score score : record.getScores()) {
                        stmt.setLong(1, Long.parseLong(record.getId()));
                        stmt.setString(2, score.getName());
                        stmt.setDouble(3, score.getValue());
                        stmt.setDouble(4, score.getLimit());
                        stmt.addBatch();
                    }
                    stmt.executeBatch();
                }
            }
            return null;
        }
    }

}
