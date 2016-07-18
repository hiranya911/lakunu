package org.lakunu.web.data.jdbc;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

public final class CoursePermissionRealm extends AuthorizingRealm {

    public static final int ROLE_INSTRUCTOR = 1;

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        if (principals == null) {
            throw new AuthorizationException("Principals must not be null");
        }
        if (dataSource == null) {
            throw new AuthorizationException("DataSource not specified");
        }

        try {
            Set<CourseRole> courseRoles = GetCourseRolesCommand.execute(dataSource,
                    principals.getPrimaryPrincipal().toString());
            ImmutableSet.Builder<String> permissions = ImmutableSet.builder();
            courseRoles.forEach(role -> {
                switch (role.role) {
                    case ROLE_INSTRUCTOR:
                        permissions.add("course:*:" + role.courseId);
                        break;
                }
            });
            SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
            authorizationInfo.addStringPermissions(permissions.build());
            return authorizationInfo;
        } catch (SQLException e) {
            throw new AuthorizationException(e);
        }
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(
            AuthenticationToken authenticationToken) throws AuthenticationException {
        return null;
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return false;
    }

    private static final class GetCourseRolesCommand extends Command<Set<CourseRole>> {

        private static final String GET_COURSE_PERMISSIONS_SQL =
                "SELECT COURSE_ID, USER_ID, USER_ROLE FROM COURSE_USER WHERE USER_ID = ?";

        private final String userId;

        private GetCourseRolesCommand(DataSource dataSource, String userId) {
            super(dataSource);
            checkArgument(!Strings.isNullOrEmpty(userId), "userId is required");
            this.userId = userId;
        }

        private static Set<CourseRole> execute(DataSource dataSource, String userId) throws SQLException {
            return new GetCourseRolesCommand(dataSource, userId).run();
        }

        @Override
        protected Set<CourseRole> doRun(Connection connection) throws SQLException {
            ImmutableSet.Builder<CourseRole> courseRoles = ImmutableSet.builder();
            try (PreparedStatement stmt = connection.prepareStatement(GET_COURSE_PERMISSIONS_SQL)) {
                stmt.setString(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        long courseId = rs.getLong("COURSE_ID");
                        int role = rs.getShort("USER_ROLE");
                        courseRoles.add(new CourseRole(courseId, role));
                    }
                }
            }
            return courseRoles.build();
        }
    }

    private static final class CourseRole {
        private final long courseId;
        private final int role;

        public CourseRole(long courseId, int role) {
            this.courseId = courseId;
            this.role = role;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof CourseRole) {
                CourseRole other = (CourseRole) obj;
                return this.courseId == other.courseId && this.role == other.role;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return (String.valueOf(courseId) + String.valueOf(role)).hashCode();
        }
    }
}
