package org.lakunu.web.dao.jdbc;

import org.lakunu.web.dao.DAOException;
import org.lakunu.web.models.Evaluation;
import org.lakunu.web.models.EvaluationRecord;
import org.lakunu.web.models.Lab;

import javax.sql.DataSource;
import java.sql.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public final class JdbcEvaluationDAO {

    private final DataSource dataSource;

    public JdbcEvaluationDAO(DataSource dataSource) {
        checkNotNull(dataSource, "DataSource is required");
        this.dataSource = dataSource;
    }

    public EvaluationRecord startEvaluation(Evaluation evaluation) {
        try {
            return StartEvaluationCommand.execute(dataSource, evaluation);
        } catch (SQLException e) {
            throw new DAOException("Error while opening evaluation record", e);
        }
    }

    private static class StartEvaluationCommand extends TxCommand<EvaluationRecord> {

        private static final String GET_LAB_SQL = "SELECT id, name, description, course_id, " +
                "created_at, created_by, config, published, submission_deadline, " +
                "allow_late_submissions FROM lab WHERE id = (SELECT lab_id from submission where id = ?)";
        private static final String OPEN_EVAL_RECORD_SQL = "INSERT INTO evaluation " +
                "(submission_id, started_at) VALUES (?,?)";

        private final Evaluation evaluation;
        
        private static EvaluationRecord execute(DataSource dataSource, 
                                                Evaluation evaluation) throws SQLException {
            return new StartEvaluationCommand(dataSource, evaluation).run();
        }

        private StartEvaluationCommand(DataSource dataSource, Evaluation evaluation) {
            super(dataSource);
            this.evaluation = evaluation;
        }

        @Override
        protected EvaluationRecord doTransaction(Connection connection) throws SQLException {
            Lab lab;
            try (PreparedStatement stmt = connection.prepareStatement(GET_LAB_SQL)) {
                stmt.setLong(1, Long.parseLong(evaluation.getSubmissionId()));
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
                stmt.setLong(1, Long.parseLong(evaluation.getSubmissionId()));
                stmt.setTimestamp(2, new Timestamp(evaluation.getStartedAt().getTime()));
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

}
