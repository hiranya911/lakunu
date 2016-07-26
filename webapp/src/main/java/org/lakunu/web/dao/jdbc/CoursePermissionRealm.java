package org.lakunu.web.dao.jdbc;

import com.google.common.collect.ImmutableSet;
import org.apache.shiro.SecurityUtils;
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

import static com.google.common.base.Preconditions.checkNotNull;

public final class CoursePermissionRealm extends AuthorizingRealm {

    public static final int ROLE_OWNER = 1;
    public static final int ROLE_INSTRUCTOR = 2;

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        // This is essentially a configuration error. It's not meaningful to throw an
        // AuthorizationException for this case.
        checkNotNull(dataSource, "DataSource not specified");

        if (principals == null) {
            throw new AuthorizationException("Principals must not be null");
        }

        try {
            Set<CourseRole> courseRoles = GetCourseRolesCommand.execute(dataSource,
                    principals.getPrimaryPrincipal());
            ImmutableSet.Builder<String> permissions = ImmutableSet.builder();
            courseRoles.forEach(role -> {
                switch (role.role) {
                    case ROLE_OWNER:
                        permissions.add("course:*:" + role.courseId);
                        permissions.add("lab:*:" + role.courseId + ":*");
                        break;
                    case ROLE_INSTRUCTOR:
                        permissions.add("course:get,getLabs:" + role.courseId);
                        permissions.add("lab:*:" + role.courseId + ":*");
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

    public void notifyPermissionChange() {
        clearCachedAuthorizationInfo(SecurityUtils.getSubject().getPrincipals());
    }

    @Override
    protected Object getAuthorizationCacheKey(PrincipalCollection principals) {
        return principals.getPrimaryPrincipal();
    }

    private static final class GetCourseRolesCommand extends Command<Set<CourseRole>> {

        private static final String GET_OWNED_COURSES_SQL =
                "SELECT COURSE_ID FROM COURSE WHERE COURSE_OWNER = ?";
        private static final String GET_COURSE_PERMISSIONS_SQL =
                "SELECT COURSE_ID, USER_ROLE FROM COURSE_USER WHERE USER_ID = ?";

        private final String userId;

        private GetCourseRolesCommand(DataSource dataSource, Object userId) {
            super(dataSource);
            checkNotNull(userId, "userId is required");
            this.userId = userId.toString();
        }

        private static Set<CourseRole> execute(DataSource dataSource,
                                               Object userId) throws SQLException {
            return new GetCourseRolesCommand(dataSource, userId).run();
        }

        @Override
        protected Set<CourseRole> doRun(Connection connection) throws SQLException {
            ImmutableSet.Builder<CourseRole> courseRoles = ImmutableSet.builder();
            try (PreparedStatement stmt = connection.prepareStatement(GET_OWNED_COURSES_SQL)) {
                stmt.setString(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        long courseId = rs.getLong("COURSE_ID");
                        courseRoles.add(new CourseRole(courseId, ROLE_OWNER));
                    }
                }
            }

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

        private CourseRole(long courseId, int role) {
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
