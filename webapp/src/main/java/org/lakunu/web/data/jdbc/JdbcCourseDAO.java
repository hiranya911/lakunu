package org.lakunu.web.data.jdbc;

import com.google.common.collect.ImmutableList;
import org.lakunu.web.data.Course;
import org.lakunu.web.data.CourseDAO;
import org.lakunu.web.utils.Security;

import javax.sql.DataSource;
import java.sql.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public final class JdbcCourseDAO extends CourseDAO {

    public static final int ROLE_INSTRUCTOR = 1;

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
        return String.valueOf(AddCourseCommand.execute(dataSource, course));
    }

    @Override
    protected Course doGetCourse(String courseId) throws Exception {
        return GetCourseCommand.execute(dataSource, courseId);
    }

    private static final class AddCourseCommand extends TxCommand<Long> {

        private static final String ADD_COURSE_SQL =
                "INSERT INTO COURSE (COURSE_NAME, COURSE_DESCRIPTION, COURSE_OWNER, COURSE_CREATED_AT) VALUES (?,?,?,?)";
        private static final String ADD_COURSE_OWNER_SQL =
                "INSERT INTO COURSE_USER (COURSE_ID, USER_ID, USER_ROLE) VALUES (?,?,?)";

        private final Course course;

        private AddCourseCommand(DataSource dataSource, Course course) {
            super(dataSource);
            this.course = course;
        }

        private static long execute(DataSource dataSource, Course course) throws SQLException {
            return new AddCourseCommand(dataSource, course).run();
        }

        @Override
        protected Long doTransaction(Connection connection) throws SQLException {
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

            try (PreparedStatement stmt = connection.prepareStatement(ADD_COURSE_OWNER_SQL)) {
                stmt.setLong(1, insertId);
                stmt.setString(2, Security.getCurrentUser());
                stmt.setInt(3, ROLE_INSTRUCTOR);
                stmt.executeUpdate();
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
}
