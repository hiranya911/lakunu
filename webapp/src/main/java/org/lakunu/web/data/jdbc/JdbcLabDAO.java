package org.lakunu.web.data.jdbc;

import org.lakunu.web.data.Lab;
import org.lakunu.web.data.LabDAO;

import javax.sql.DataSource;

import java.sql.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public final class JdbcLabDAO extends LabDAO {

    private final DataSource dataSource;

    JdbcLabDAO(DataSource dataSource) {
        checkNotNull(dataSource, "datasource is required");
        this.dataSource = dataSource;
    }

    @Override
    protected String doAddLab(Lab lab) throws Exception {
        return String.valueOf(AddLabCommand.execute(dataSource, lab));
    }

    @Override
    protected Lab doGetLab(String courseId, String labId) throws Exception {
        return GetLabCommand.execute(dataSource, labId);
    }

    private static final class AddLabCommand extends Command<Long> {

        private static final String ADD_LAB_SQL =  "INSERT INTO LAB " +
                "(LAB_NAME, LAB_DESCRIPTION, LAB_COURSE_ID, LAB_CREATED_AT, LAB_CREATED_BY, LAB_CONFIG, LAB_PUBLISHED) " +
                "VALUES (?,?,?,?,?,?,?)";

        private final Lab lab;

        private AddLabCommand(DataSource dataSource, Lab lab) {
            super(dataSource);
            this.lab = lab;
        }

        private static long execute(DataSource dataSource, Lab lab) throws SQLException {
            return new AddLabCommand(dataSource, lab).run();
        }

        @Override
        protected Long doRun(Connection connection) throws SQLException {
            long insertId;
            try (PreparedStatement stmt = connection.prepareStatement(ADD_LAB_SQL,
                    Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, lab.getName());
                stmt.setString(2, lab.getDescription());
                stmt.setLong(3, Long.parseLong(lab.getCourseId()));
                stmt.setTimestamp(4, lab.getCreatedAt());
                stmt.setString(5, lab.getCreatedBy());
                stmt.setBytes(6, lab.getConfiguration());
                stmt.setBoolean(7, lab.isPublished());
                int rows = stmt.executeUpdate();
                checkState(rows == 1, "Failed to add the lab to database");
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        insertId = rs.getLong(1);
                    } else {
                        throw new IllegalStateException("Failed to retrieve new lab ID");
                    }
                }
            }
            return insertId;
        }
    }

    private static final class GetLabCommand extends Command<Lab> {

        private static final String GET_LAB_SQL =
                "SELECT LAB_ID, LAB_NAME, LAB_DESCRIPTION, LAB_COURSE_ID, LAB_CREATED_AT, " +
                        "LAB_CREATED_BY, LAB_CONFIG, LAB_PUBLISHED FROM LAB WHERE LAB_ID = ?";

        private final long labId;

        public static Lab execute(DataSource dataSource, String labId) throws SQLException {
            return new GetLabCommand(dataSource, Long.parseLong(labId)).run();
        }

        private GetLabCommand(DataSource dataSource, long labId) {
            super(dataSource);
            this.labId = labId;
        }

        @Override
        protected Lab doRun(Connection connection) throws SQLException {
            try (PreparedStatement stmt = connection.prepareStatement(GET_LAB_SQL)) {
                stmt.setLong(1, labId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return Lab.newBuilder().setId(String.valueOf(rs.getLong("LAB_ID")))
                                .setName(rs.getString("LAB_NAME"))
                                .setDescription(rs.getString("LAB_DESCRIPTION"))
                                .setCourseId(String.valueOf(rs.getLong("LAB_COURSE_ID")))
                                .setCreatedAt(rs.getTimestamp("LAB_CREATED_AT"))
                                .setCreatedBy(rs.getString("LAB_CREATED_BY"))
                                .setConfiguration(rs.getBytes("LAB_CONFIG"))
                                .setPublished(rs.getBoolean("LAB_PUBLISHED"))
                                .build();
                    } else {
                        return null;
                    }
                }
            }
        }
    }
}
