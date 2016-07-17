package org.lakunu.web.data.jdbc;

import com.google.common.collect.ImmutableList;
import org.lakunu.web.data.Course;
import org.lakunu.web.data.CourseDAO;
import org.lakunu.web.utils.Security;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Calendar;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public final class JdbcCourseDAO extends CourseDAO {

    private static final String GET_OWNED_COURSES_SQL =
            "SELECT COURSE_ID, COURSE_NAME, COURSE_DESCRIPTION, COURSE_OWNER, COURSE_CREATED_AT FROM COURSE WHERE COURSE_OWNER = ?";
    private static final String ADD_COURSE_SQL =
            "INSERT INTO COURSE (COURSE_NAME, COURSE_DESCRIPTION, COURSE_OWNER, COURSE_CREATED_AT) VALUES (?,?,?,?)";

    private final DataSource dataSource;

    JdbcCourseDAO(DataSource dataSource) {
        checkNotNull(dataSource, "datasource is required");
        this.dataSource = dataSource;
    }

    @Override
    protected ImmutableList<Course> doGetOwnedCourses() throws Exception {
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = getOwnedCoursesQuery(connection);
                ResultSet rs = stmt.executeQuery()
        ) {
            ImmutableList.Builder<Course> builder = ImmutableList.builder();
            while (rs.next()) {
                Course course = Course.newBuilder().setId(String.valueOf(rs.getLong("COURSE_ID")))
                        .setName(rs.getString("COURSE_NAME"))
                        .setDescription(rs.getString("COURSE_DESCRIPTION"))
                        .setOwner(rs.getString("COURSE_OWNER"))
                        .setCreatedAt(rs.getTimestamp("COURSE_CREATED_AT")).build();
                builder.add(course);
            }
            return builder.build();
        }
    }

    private PreparedStatement getOwnedCoursesQuery(Connection connection) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(GET_OWNED_COURSES_SQL);
        stmt.setString(1, Security.getCurrentUser());
        return stmt;
    }

    @Override
    protected String doAddCourse(Course course) throws Exception {
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = addCourseQuery(connection, course)
        ) {
            int rows = stmt.executeUpdate();
            checkState(rows == 1, "Failed to add the course to database");
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return String.valueOf(rs.getLong(1));
                } else {
                    throw new IllegalStateException("Failed to retrieve new course ID");
                }
            }
        }
    }

    private PreparedStatement addCourseQuery(Connection connection, Course course) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(ADD_COURSE_SQL);
        stmt.setString(1, course.getName());
        stmt.setString(2, course.getDescription());
        stmt.setString(3, Security.getCurrentUser());
        stmt.setTimestamp(4, new Timestamp(Calendar.getInstance().getTime().getTime()));
        return stmt;
    }
}
