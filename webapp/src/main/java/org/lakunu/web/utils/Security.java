package org.lakunu.web.utils;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.subject.Subject;
import org.lakunu.web.data.jdbc.CoursePermissionRealm;

public class Security {

    public static void checkPermissions(String... permissions) {
        Subject subject = SecurityUtils.getSubject();
        subject.checkPermissions(permissions);
    }

    public static String getCurrentUser() {
        return SecurityUtils.getSubject().getPrincipal().toString();
    }

    public static void clearCoursePermissionCache() {
        RealmSecurityManager securityManager = (RealmSecurityManager) SecurityUtils.getSecurityManager();
        securityManager.getRealms().stream()
                .filter(r -> r instanceof CoursePermissionRealm)
                .map(r -> (CoursePermissionRealm) r)
                .forEach(CoursePermissionRealm::notifyPermissionChange);
    }

}
