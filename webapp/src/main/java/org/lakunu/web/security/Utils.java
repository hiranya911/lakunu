package org.lakunu.web.security;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

public class Utils {

    public static void checkPermissions(String... permissions) {
        Subject subject = SecurityUtils.getSubject();
        subject.checkPermissions(permissions);
    }

}
