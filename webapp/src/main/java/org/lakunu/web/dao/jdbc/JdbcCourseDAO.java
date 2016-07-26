package org.lakunu.web.dao.jdbc;

import com.google.common.collect.ImmutableList;
import org.lakunu.web.dao.CourseDAO;
import org.lakunu.web.dao.DAOException;
import org.lakunu.web.models.Lab;
import org.lakunu.web.models.Course;
import org.lakunu.web.utils.Security;

import javax.sql.DataSource;

import java.sql.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class JdbcCourseDAO implements CourseDAO {

    private final DataSource dataSource;

    public JdbcCourseDAO(DataSource dataSource) {
        checkNotNull(dataSource, "DataSource is required");
        this.dataSource = dataSource;
    }

    @Override
    public String addCourse(Course course) {
        try {
            return String.valueOf(AddCourseCommand.execute(dataSource, course));
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
    public ImmutableList<Lab> getLabs(String courseId) {
        try {
            return GetLabsCommand.execute(dataSource, Long.parseLong(courseId));
        } catch (SQLException e) {
            throw new DAOException("Error while retrieving labs", e);
        }
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
                        Course course = new Course();
                        course.setId(String.valueOf(rs.getLong("COURSE_ID")));
                        course.setName(rs.getString("COURSE_NAME"));
                        course.setDescription(rs.getString("COURSE_DESCRIPTION"));
                        course.setOwner(rs.getString("COURSE_OWNER"));
                        course.setCreatedAt(rs.getTimestamp("COURSE_CREATED_AT"));
                        return course;
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
                        Course course = new Course();
                        course.setId(String.valueOf(rs.getLong("COURSE_ID")));
                        course.setName(rs.getString("COURSE_NAME"));
                        course.setDescription(rs.getString("COURSE_DESCRIPTION"));
                        course.setOwner(rs.getString("COURSE_OWNER"));
                        course.setCreatedAt(rs.getTimestamp("COURSE_CREATED_AT"));
                        builder.add(course);
                    }
                    return builder.build();
                }
            }
        }
    }

    private static final class GetLabsCommand extends Command<ImmutableList<Lab>> {

        private static final String GET_LABS_SQL =
                "SELECT LAB_ID, LAB_NAME, LAB_DESCRIPTION, LAB_CREATED_BY, LAB_CREATED_AT, " +
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
                        Lab lab = new Lab();
                        lab.setId(String.valueOf(rs.getLong("LAB_ID")));
                        lab.setName(rs.getString("LAB_NAME"));
                        lab.setDescription(rs.getString("LAB_DESCRIPTION"));
                        lab.setCourseId(String.valueOf(courseId));
                        lab.setCreatedAt(rs.getTimestamp("LAB_CREATED_AT"));
                        lab.setCreatedBy(rs.getString("LAB_CREATED_BY"));
                        lab.setConfiguration(rs.getBytes("LAB_CONFIG"));
                        lab.setPublished(rs.getBoolean("LAB_PUBLISHED"));
                        lab.setSubmissionDeadline(rs.getTimestamp("LAB_SUBMISSION_DEADLINE"));
                        lab.setAllowLateSubmissions(rs.getBoolean("LAB_ALLOW_LATE_SUBMISSIONS"));
                        builder.add(lab);
                    }
                    return builder.build();
                }
            }
        }
    }


}
