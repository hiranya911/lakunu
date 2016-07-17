package org.lakunu.web.data.jdbc;

import com.google.common.collect.ImmutableList;
import org.lakunu.web.data.Course;
import org.lakunu.web.data.CourseDAO;
import org.lakunu.web.utils.Security;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.google.common.base.Preconditions.checkNotNull;

public final class JdbcCourseDAO extends CourseDAO {

    private static final String GET_OWNED_COURSES_SQL =
            "SELECT COURSE_ID, COURSE_NAME, COURSE_DESCRIPTION, COURSE_OWNER, COURSE_CREATED_AT FROM COURSE WHERE COURSE_OWNER = ?";

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
}
