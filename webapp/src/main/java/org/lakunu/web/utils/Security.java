package org.lakunu.web.utils;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

public class Security {

    public static void checkPermissions(String... permissions) {
        Subject subject = SecurityUtils.getSubject();
        subject.checkPermissions(permissions);
    }

    public static String getCurrentUser() {
        return SecurityUtils.getSubject().getPrincipal().toString();
    }

}
