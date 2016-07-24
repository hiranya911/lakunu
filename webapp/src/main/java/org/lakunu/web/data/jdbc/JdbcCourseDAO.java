package org.lakunu.web.data.jdbc;

import com.google.common.collect.ImmutableList;
import org.lakunu.web.data.Course;
import org.lakunu.web.data.CourseDAO;
import org.lakunu.web.data.Lab;
import org.lakunu.web.utils.Security;

import javax.sql.DataSource;
import java.sql.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public final class JdbcCourseDAO extends CourseDAO {

    private final DataSource dataSource;

    JdbcCourseDAO(DataSource dataSource) {
        checkNotNull(dataSource, "datasource is required");
        this.dataSource = dataSource;
    }

    @Override
    protected ImmutableList<Course> doGetOwnedCourses() throws Exception {
        return GetOwnedCoursesCommand.execute(dataSource);
    }

    @Override
    protected String doAddCourse(Course course) throws Exception {
        long courseId = AddCourseCommand.execute(dataSource, course);
        Security.clearCoursePermissionCache();
        return String.valueOf(courseId);
    }

    @Override
    protected Course doGetCourse(String courseId) throws Exception {
        return GetCourseCommand.execute(dataSource, courseId);
    }

    @Override
    protected ImmutableList<Lab> doGetLabs(String courseId) throws Exception {
        return GetLabsCommand.execute(dataSource, courseId);
    }

    private static final class AddCourseCommand extends Command<Long> {

        private static final String ADD_COURSE_SQL =
                "INSERT INTO COURSE (COURSE_NAME, COURSE_DESCRIPTION, COURSE_OWNER, COURSE_CREATED_AT) VALUES (?,?,?,?)";

        private final Course course;

        private AddCourseCommand(DataSource dataSource, Course course) {
            super(dataSource);
            this.course = course;
        }

        private static long execute(DataSource dataSource, Course course) throws SQLException {
            return new AddCourseCommand(dataSource, course).run();
        }

        @Override
        protected Long doRun(Connection connection) throws SQLException {
            long insertId;
            try (PreparedStatement stmt = connection.prepareStatement(ADD_COURSE_SQL,
                    Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, course.getName());
                stmt.setString(2, course.getDescription());
                stmt.setString(3, course.getOwner());
                stmt.setTimestamp(4, course.getCreatedAt());
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
            return insertId;
        }
    }

    private static final class GetCourseCommand extends Command<Course> {

        private static final String GET_COURSE_SQL =
                "SELECT COURSE_ID, COURSE_NAME, COURSE_DESCRIPTION, COURSE_OWNER, COURSE_CREATED_AT FROM COURSE WHERE COURSE_ID = ?";

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
                        return Course.newBuilder().setId(String.valueOf(rs.getLong("COURSE_ID")))
                                .setName(rs.getString("COURSE_NAME"))
                                .setDescription(rs.getString("COURSE_DESCRIPTION"))
                                .setOwner(rs.getString("COURSE_OWNER"))
                                .setCreatedAt(rs.getTimestamp("COURSE_CREATED_AT")).build();
                    } else {
                        return null;
                    }
                }
            }
        }
    }

    private static final class GetOwnedCoursesCommand extends Command<ImmutableList<Course>> {

        private static final String GET_OWNED_COURSES_SQL =
                "SELECT COURSE_ID, COURSE_NAME, COURSE_DESCRIPTION, COURSE_OWNER, COURSE_CREATED_AT FROM COURSE WHERE COURSE_OWNER = ?";

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
                        Course course = Course.newBuilder()
                                .setId(String.valueOf(rs.getLong("COURSE_ID")))
                                .setName(rs.getString("COURSE_NAME"))
                                .setDescription(rs.getString("COURSE_DESCRIPTION"))
                                .setOwner(rs.getString("COURSE_OWNER"))
                                .setCreatedAt(rs.getTimestamp("COURSE_CREATED_AT")).build();
                        builder.add(course);
                    }
                    return builder.build();
                }
            }
        }
    }

    private static final class GetLabsCommand extends Command<ImmutableList<Lab>> {

        private static final String GET_LABS_SQL =
                "SELECT LAB_ID, LAB_NAME, LAB_DESCRIPTION, LAB_CREATED_BY, LAB_CREATED_AT FROM LAB WHERE LAB_COURSE_ID = ?";

        private final String courseId;

        private GetLabsCommand(DataSource dataSource, String courseId) {
            super(dataSource);
            this.courseId = courseId;
        }

        private static ImmutableList<Lab> execute(DataSource dataSource, String courseId) throws SQLException {
            return new GetLabsCommand(dataSource, courseId).run();
        }

        @Override
        protected ImmutableList<Lab> doRun(Connection connection) throws SQLException {
            try (PreparedStatement stmt = connection.prepareStatement(GET_LABS_SQL)) {
                stmt.setString(1, courseId);
                try (ResultSet rs = stmt.executeQuery()) {
                    ImmutableList.Builder<Lab> builder = ImmutableList.builder();
                    while (rs.next()) {
                        Lab lab = Lab.newBuilder()
                                .setId(String.valueOf(rs.getLong("LAB_ID")))
                                .setName(rs.getString("LAB_NAME"))
                                .setDescription(rs.getString("LAB_DESCRIPTION"))
                                .setCourseId(courseId)
                                .setCreatedBy(rs.getString("LAB_CREATED_BY"))
                                .setCreatedAt(rs.getTimestamp("LAB_CREATED_AT")).build();
                        builder.add(lab);
                    }
                    return builder.build();
                }
            }
        }
    }
}
