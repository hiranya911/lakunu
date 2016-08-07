package org.lakunu.web.dao.jdbc;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.lakunu.web.dao.CourseDAO;
import org.lakunu.web.dao.DAOException;
import org.lakunu.web.models.Course;
import org.lakunu.web.utils.Security;

import javax.sql.DataSource;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public final class JdbcCourseDAO implements CourseDAO {

    private final DataSource dataSource;

    public JdbcCourseDAO(DataSource dataSource) {
        checkNotNull(dataSource, "DataSource is required");
        this.dataSource = dataSource;
    }

    @Override
    public String addCourse(Course course) {
        try {
            return AddCourseCommand.execute(dataSource, course);
        } catch (SQLException e) {
            throw new DAOException("Error while adding course", e);
        }
    }

    @Override
    public Course getCourse(String courseId) {
        try {
            return GetCourseCommand.execute(dataSource, courseId);
        } catch (SQLException e) {
            throw new DAOException("Error while retrieving course", e);
        }
    }

    @Override
    public ImmutableList<Course> getOwnedCourses() {
        try {
            return GetOwnedCoursesCommand.execute(dataSource);
        } catch (SQLException e) {
            throw new DAOException("Error while retrieving courses", e);
        }
    }

    @Override
    public void shareCourse(Course course, ImmutableSet<String> users, int role) {
        try {
            ShareCourseCommand.execute(dataSource, course.getId(), users, role);
        } catch (SQLException e) {
            throw new DAOException("Error while updating course roles", e);
        }
    }

    private static final class AddCourseCommand extends Command<String> {

        private static final String ADD_COURSE_SQL = "INSERT INTO course (name, description, " +
                "owner, created_at) VALUES (?,?,?,?)";

        private final Course course;

        private AddCourseCommand(DataSource dataSource, Course course) {
            super(dataSource);
            this.course = course;
        }

        private static String execute(DataSource dataSource, Course course) throws SQLException {
            return new AddCourseCommand(dataSource, course).run();
        }

        @Override
        protected String doRun(Connection connection) throws SQLException {
            long insertId;
            try (PreparedStatement stmt = connection.prepareStatement(ADD_COURSE_SQL,
                    Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, course.getName());
                stmt.setString(2, course.getDescription());
                stmt.setString(3, course.getOwner());
                stmt.setTimestamp(4, new Timestamp(course.getCreatedAt().getTime()));
                int rows = stmt.executeUpdate();
                checkState(rows == 1, "Failed to add the course to database");
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        insertId = rs.getLong(1);
                    } else {
                        throw new IllegalStateException("Failed to retrieve new course ID");
                    }
                }
            }
            return String.valueOf(insertId);
        }
    }

    private static final class GetCourseCommand extends Command<Course> {

        private static final String GET_COURSE_SQL = "SELECT id, name, description, owner, " +
                "created_at FROM course WHERE id = ?";

        private final long courseId;

        private GetCourseCommand(DataSource dataSource, long courseId) {
            super(dataSource);
            this.courseId = courseId;
        }

        private static Course execute(DataSource dataSource, String courseId) throws SQLException {
            return new GetCourseCommand(dataSource, Long.parseLong(courseId)).run();
        }

        @Override
        protected Course doRun(Connection connection) throws SQLException {
            try (PreparedStatement stmt = connection.prepareStatement(GET_COURSE_SQL)) {
                stmt.setLong(1, courseId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return createCourse(rs);
                    } else {
                        return null;
                    }
                }
            }
        }
    }

    private static final class GetOwnedCoursesCommand extends Command<ImmutableList<Course>> {

        private static final String GET_OWNED_COURSES_SQL = "SELECT id, name, description, " +
                "owner, created_at FROM course WHERE owner = ?";

        private GetOwnedCoursesCommand(DataSource dataSource) {
            super(dataSource);
        }

        private static ImmutableList<Course> execute(DataSource dataSource) throws SQLException {
            return new GetOwnedCoursesCommand(dataSource).run();
        }

        @Override
        protected ImmutableList<Course> doRun(Connection connection) throws SQLException {
            try (PreparedStatement stmt = connection.prepareStatement(GET_OWNED_COURSES_SQL)) {
                stmt.setString(1, Security.getCurrentUser());
                try (ResultSet rs = stmt.executeQuery()) {
                    ImmutableList.Builder<Course> builder = ImmutableList.builder();
                    while (rs.next()) {
                        builder.add(createCourse(rs));
                    }
                    return builder.build();
                }
            }
        }
    }

    private static final class ShareCourseCommand extends TxCommand<Void> {

        private static final String GET_CURRENT_USERS_SQL = "SELECT user_id FROM course_user " +
                "WHERE course_id = ? AND role = ?";
        private static final String ADD_COURSE_ROLE_SQL = "INSERT INTO course_user (course_id, " +
                "user_id, role) VALUES (?,?,?)";

        private final long courseId;
        private final ImmutableSet<String> users;
        private final int role;

        private ShareCourseCommand(DataSource dataSource, String courseId,
                                   ImmutableSet<String> users, int role) {
            super(dataSource);
            this.courseId = Long.parseLong(courseId);
            this.users = users;
            this.role = role;
        }

        private static void execute(DataSource dataSource, String courseId,
                                    ImmutableSet<String> users, int role) throws SQLException {
            new ShareCourseCommand(dataSource, courseId, users, role).run();
        }

        @Override
        protected Void doTransaction(Connection connection) throws SQLException {
            Set<String> currentUsers = new HashSet<>();
            try (PreparedStatement stmt = connection.prepareStatement(GET_CURRENT_USERS_SQL)) {
                stmt.setLong(1, courseId);
                stmt.setInt(2, role);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        currentUsers.add(rs.getString("user_id"));
                    }
                }
            }

            Sets.SetView<String> newUsers = Sets.difference(users, currentUsers);
            if (!newUsers.isEmpty()) {
                try (PreparedStatement stmt = connection.prepareStatement(ADD_COURSE_ROLE_SQL)) {
                    for (String user : newUsers) {
                        stmt.setLong(1, courseId);
                        stmt.setString(2, user);
                        stmt.setInt(3, role);
                        stmt.addBatch();
                    }
                    stmt.executeUpdate();
                }
            }
            return null;
        }
    }

    private static Course createCourse(ResultSet rs) throws SQLException {
        return Course.newBuilder()
                .setId(String.valueOf(rs.getLong("id")))
                .setName(rs.getString("name"))
                .setDescription(rs.getString("description"))
                .setOwner(rs.getString("owner"))
                .setCreatedAt(rs.getTimestamp("created_at"))
                .build();
    }
}
