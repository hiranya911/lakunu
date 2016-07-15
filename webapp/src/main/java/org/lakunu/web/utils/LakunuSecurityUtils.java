package org.lakunu.web.utils;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

public class LakunuSecurityUtils {

    public static void checkPermissions(String... permissions) {
        Subject subject = SecurityUtils.getSubject();
        subject.checkPermissions(permissions);
    }

}
