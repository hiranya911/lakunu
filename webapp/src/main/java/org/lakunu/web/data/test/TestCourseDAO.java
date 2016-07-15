package org.lakunu.web.data.test;

import com.google.common.collect.ImmutableList;
import org.apache.shiro.SecurityUtils;
import org.lakunu.web.data.Course;
import org.lakunu.web.data.CourseDAO;

import java.sql.Timestamp;

public final class TestCourseDAO extends CourseDAO {

    @Override
    protected ImmutableList<Course> doGetOwnedCourses() {
        String owner = SecurityUtils.getSubject().getPrincipal().toString();
        return ImmutableList.of(
                Course.newBuilder().setName("cs56").setDescription("test")
                        .setOwner(owner).setCreatedAt(new Timestamp(System.currentTimeMillis()))
                        .build(),
                Course.newBuilder().setName("cs270").setDescription("test")
                        .setOwner(owner).setCreatedAt(new Timestamp(System.currentTimeMillis()))
                        .build()
        );
    }
}
