package org.lakunu.web.utils;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.subject.Subject;
import org.lakunu.web.dao.jdbc.CoursePermissionRealm;
import org.lakunu.web.service.permissions.Permission;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Security {

    public static void checkPermissions(String... permissions) {
        Subject subject = SecurityUtils.getSubject();
        subject.checkPermissions(permissions);
    }

    public static void checkPermissions(Permission... permissions) {
        Subject subject = SecurityUtils.getSubject();
        for (Permission p : permissions) {
            subject.checkPermission(p.toString());
        }
    }

    public static boolean hasPermission(String permission) {
        Subject subject = SecurityUtils.getSubject();
        return subject.isPermitted(permission);
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

    public static void clearCoursePermissionCache(Collection<String> users) {
        RealmSecurityManager securityManager = (RealmSecurityManager) SecurityUtils.getSecurityManager();
        List<CoursePermissionRealm> realms = securityManager.getRealms().stream()
                .filter(r -> r instanceof CoursePermissionRealm)
                .map(r -> (CoursePermissionRealm) r)
                .collect(Collectors.toList());
        users.forEach(user -> realms.forEach(realm -> realm.notifyPermissionChange(user)));
    }

}
