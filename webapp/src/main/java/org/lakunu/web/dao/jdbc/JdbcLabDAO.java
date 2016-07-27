package org.lakunu.web.dao.jdbc;


import com.google.common.collect.ImmutableList;
import org.lakunu.web.dao.LabDAO;
import org.lakunu.web.dao.DAOException;
import org.lakunu.web.models.Lab;

import javax.sql.DataSource;

import java.sql.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class JdbcLabDAO implements LabDAO {

    private final DataSource dataSource;

    public JdbcLabDAO(DataSource dataSource) {
        checkNotNull(dataSource, "DataSource is required");
        this.dataSource = dataSource;
    }

    @Override
    public String addLab(Lab lab) {
        try {
            return String.valueOf(AddLabCommand.execute(dataSource, lab));
        } catch (SQLException e) {
            throw new DAOException("Error while adding lab", e);
        }
    }

    @Override
    public Lab getLab(String labId) {
        try {
            return GetLabCommand.execute(dataSource, labId);
        } catch (SQLException e) {
            throw new DAOException("Error while retrieving lab", e);
        }
    }

    @Override
    public ImmutableList<Lab> getLabs(String courseId) {
        try {
            return GetLabsCommand.execute(dataSource, Long.parseLong(courseId));
        } catch (SQLException e) {
            throw new DAOException("Error while retrieving labs", e);
        }
    }

    @Override
    public void updateLab(Lab lab) {
        try {
            UpdateLabCommand.execute(dataSource, lab);
        } catch (SQLException e) {
            throw new DAOException("Error while updating lab", e);
        }
    }

    private static final class AddLabCommand extends Command<Long> {

        private static final String ADD_LAB_SQL =  "INSERT INTO LAB " +
                "(LAB_NAME, LAB_DESCRIPTION, LAB_COURSE_ID, LAB_CREATED_AT, LAB_CREATED_BY) " +
                "VALUES (?,?,?,?,?)";

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
                stmt.setTimestamp(4, new Timestamp(lab.getCreatedAt().getTime()));
                stmt.setString(5, lab.getCreatedBy());
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
                        "LAB_CREATED_BY, LAB_CONFIG, LAB_PUBLISHED, LAB_SUBMISSION_DEADLINE, " +
                        "LAB_ALLOW_LATE_SUBMISSIONS FROM LAB WHERE LAB_ID = ?";

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
                        return createLab(rs);
                    } else {
                        return null;
                    }
                }
            }
        }
    }

    private static final class UpdateLabCommand extends Command<Void> {

        private static final String UPDATE_LAB_SQL =
                "UPDATE LAB SET LAB_NAME = ?, LAB_DESCRIPTION = ?, LAB_CONFIG = ? WHERE LAB_ID = ?";

        private final Lab lab;

        public static void execute(DataSource dataSource, Lab lab) throws SQLException {
            new UpdateLabCommand(dataSource, lab).run();
        }

        private UpdateLabCommand(DataSource dataSource, Lab lab) {
            super(dataSource);
            this.lab = lab;
        }

        @Override
        protected Void doRun(Connection connection) throws SQLException {
            try (PreparedStatement stmt = connection.prepareStatement(UPDATE_LAB_SQL)) {
                stmt.setString(1, lab.getName());
                stmt.setString(2, lab.getDescription());
                stmt.setBytes(3, lab.getConfiguration());
                stmt.setLong(4, Long.parseLong(lab.getId()));
                if (stmt.executeUpdate() == 1) {
                    return null;
                } else {
                    throw new DAOException("Failed to update lab: " + lab.getId());
                }
            }
        }
    }

    private static final class GetLabsCommand extends Command<ImmutableList<Lab>> {

        private static final String GET_LABS_SQL =
                "SELECT LAB_ID, LAB_NAME, LAB_DESCRIPTION, LAB_CREATED_BY, LAB_CREATED_AT, LAB_COURSE_ID, " +
                        "LAB_CONFIG, LAB_PUBLISHED, LAB_SUBMISSION_DEADLINE, LAB_ALLOW_LATE_SUBMISSIONS FROM LAB WHERE LAB_COURSE_ID = ?";

        private final long courseId;

        private GetLabsCommand(DataSource dataSource, long courseId) {
            super(dataSource);
            this.courseId = courseId;
        }

        private static ImmutableList<Lab> execute(DataSource dataSource, long courseId) throws SQLException {
            return new GetLabsCommand(dataSource, courseId).run();
        }

        @Override
        protected ImmutableList<Lab> doRun(Connection connection) throws SQLException {
            try (PreparedStatement stmt = connection.prepareStatement(GET_LABS_SQL)) {
                stmt.setLong(1, courseId);
                try (ResultSet rs = stmt.executeQuery()) {
                    ImmutableList.Builder<Lab> builder = ImmutableList.builder();
                    while (rs.next()) {
                        builder.add(createLab(rs));
                    }
                    return builder.build();
                }
            }
        }
    }

    private static Lab createLab(ResultSet rs) throws SQLException {
        return Lab.newBuilder()
                .setId(String.valueOf(rs.getLong("LAB_ID")))
                .setName(rs.getString("LAB_NAME"))
                .setDescription(rs.getString("LAB_DESCRIPTION"))
                .setCourseId(String.valueOf(rs.getLong("LAB_COURSE_ID")))
                .setCreatedAt(rs.getTimestamp("LAB_CREATED_AT"))
                .setCreatedBy(rs.getString("LAB_CREATED_BY"))
                .setConfiguration(rs.getBytes("LAB_CONFIG"))
                .setPublished(rs.getBoolean("LAB_PUBLISHED"))
                .setSubmissionDeadline(rs.getTimestamp("LAB_SUBMISSION_DEADLINE"))
                .setAllowLateSubmissions(rs.getBoolean("LAB_ALLOW_LATE_SUBMISSIONS"))
                .build();
    }
}
