package org.lakunu.web.data.test;

import com.google.common.collect.ImmutableList;
import org.apache.shiro.SecurityUtils;
import org.lakunu.web.data.Course;
import org.lakunu.web.data.CourseDAO;

public final class TestCourseDAO extends CourseDAO {

    @Override
    protected ImmutableList<Course> doGetOwnedCourses() {
        String owner = SecurityUtils.getSubject().getPrincipal().toString();
        return ImmutableList.of(
                new Course("cs56", owner),
                new Course("cs270", owner)
        );
    }
}
