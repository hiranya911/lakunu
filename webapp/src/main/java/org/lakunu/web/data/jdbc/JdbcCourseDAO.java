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
                        .setName(rs.getString("NAME"))
                        .setDescription(rs.getString("DESCRIPTION"))
                        .setOwner(rs.getString("OWNER"))
                        .setCreatedAt(rs.getTimestamp("CREATED_AT")).build();
                builder.add(course);
            }
            return builder.build();
        }
    }

    private PreparedStatement getOwnedCoursesQuery(Connection connection) throws SQLException {
        final String sql = "SELECT COURSE_ID, NAME, DESCRIPTION, OWNER, CREATED_AT FROM COURSE WHERE OWNER = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, Security.getCurrentUser());
        return stmt;
    }
}
