package org.lakunu.web.data;

import com.google.common.collect.ImmutableList;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import static org.lakunu.web.utils.LakunuSecurityUtils.checkPermissions;

public class TestDatabase {

    public static ImmutableList<Course> getCoursesByOwner() {
        checkPermissions("course:listOwned");
        Subject owner = SecurityUtils.getSubject();
        return ImmutableList.of(
                new Course("cs56", owner.getPrincipal().toString()),
                new Course("cs270", owner.getPrincipal().toString())
        );
    }

}
