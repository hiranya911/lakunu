package org.lakunu.web.dao.jdbc;

import com.google.common.collect.ImmutableList;
import org.lakunu.web.dao.LabDAO;
import org.lakunu.web.dao.DAOException;
import org.lakunu.web.models.Lab;

import javax.sql.DataSource;

import java.sql.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public final class JdbcLabDAO implements LabDAO {

    private final DataSource dataSource;

    JdbcLabDAO(DataSource dataSource) {
        checkNotNull(dataSource, "DataSource is required");
        this.dataSource = dataSource;
    }

    @Override
    public String addLab(Lab lab) {
        try {
            return AddLabCommand.execute(dataSource, lab);
        } catch (SQLException e) {
            throw new DAOException("Error while adding lab", e);
        }
    }

    @Override
    public Lab getLab(String courseId, String labId) {
        try {
            return GetLabCommand.execute(dataSource, courseId, labId);
        } catch (SQLException e) {
            throw new DAOException("Error while retrieving lab", e);
        }
    }

    @Override
    public ImmutableList<Lab> getLabs(String courseId) {
        try {
            return GetLabsCommand.execute(dataSource, courseId);
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

    private static final class AddLabCommand extends Command<String> {

        private static final String ADD_LAB_SQL =  "INSERT INTO lab (name, description, " +
                "course_id, created_at, created_by) VALUES (?,?,?,?,?)";

        private final Lab lab;

        private AddLabCommand(DataSource dataSource, Lab lab) {
            super(dataSource);
            this.lab = lab;
        }

        private static String execute(DataSource dataSource, Lab lab) throws SQLException {
            return new AddLabCommand(dataSource, lab).run();
        }

        @Override
        protected String doRun(Connection connection) throws SQLException {
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
                        return String.valueOf(rs.getLong(1));
                    } else {
                        throw new IllegalStateException("Failed to retrieve new lab ID");
                    }
                }
            }
        }
    }

    private static final class GetLabCommand extends Command<Lab> {

        private static final String GET_LAB_SQL = "SELECT id, name, description, course_id, " +
                "created_at, created_by, config, published, submission_deadline, " +
                "allow_late_submissions FROM lab WHERE id = ? AND course_id = ?";

        private final long courseId;
        private final long labId;

        public static Lab execute(DataSource dataSource, String courseId,
                                  String labId) throws SQLException {
            return new GetLabCommand(dataSource, courseId, labId).run();
        }

        private GetLabCommand(DataSource dataSource, String courseId, String labId) {
            super(dataSource);
            this.courseId = Long.parseLong(courseId);
            this.labId = Long.parseLong(labId);
        }

        @Override
        protected Lab doRun(Connection connection) throws SQLException {
            try (PreparedStatement stmt = connection.prepareStatement(GET_LAB_SQL)) {
                stmt.setLong(1, labId);
                stmt.setLong(2, courseId);
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

        private static final String UPDATE_LAB_SQL =  "UPDATE lab SET name = ?, description = ?, " +
                "config = ?, published = ?, submission_deadline = ?, allow_late_submissions = ? " +
                "WHERE id = ? AND course_id = ?";

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
                stmt.setBoolean(4, lab.isPublished());
                if (lab.getSubmissionDeadline() != null) {
                    stmt.setTimestamp(5, new Timestamp(lab.getSubmissionDeadline().getTime()));
                } else {
                    stmt.setTimestamp(5, null);
                }
                stmt.setBoolean(6, lab.isAllowLateSubmissions());
                stmt.setLong(7, Long.parseLong(lab.getId()));
                stmt.setLong(8, Long.parseLong(lab.getCourseId()));
                if (stmt.executeUpdate() == 1) {
                    return null;
                } else {
                    throw new DAOException("Failed to update lab: " + lab.getId());
                }
            }
        }
    }

    private static final class GetLabsCommand extends Command<ImmutableList<Lab>> {

        private static final String GET_LABS_SQL = "SELECT id, name, description, course_id, " +
                "created_at, created_by, config, published, submission_deadline, " +
                "allow_late_submissions FROM lab WHERE course_id = ?";

        private final long courseId;

        private GetLabsCommand(DataSource dataSource, String courseId) {
            super(dataSource);
            this.courseId = Long.parseLong(courseId);
        }

        private static ImmutableList<Lab> execute(DataSource dataSource,
                                                  String courseId) throws SQLException {
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

    public static Lab createLab(ResultSet rs) throws SQLException {
        return Lab.newBuilder()
                .setId(String.valueOf(rs.getLong("id")))
                .setName(rs.getString("name"))
                .setDescription(rs.getString("description"))
                .setCourseId(String.valueOf(rs.getLong("course_id")))
                .setCreatedAt(rs.getTimestamp("created_at"))
                .setCreatedBy(rs.getString("created_by"))
                .setConfiguration(rs.getBytes("config"))
                .setPublished(rs.getBoolean("published"))
                .setSubmissionDeadline(rs.getTimestamp("submission_deadline"))
                .setAllowLateSubmissions(rs.getBoolean("allow_late_submissions"))
                .build();
    }
}
