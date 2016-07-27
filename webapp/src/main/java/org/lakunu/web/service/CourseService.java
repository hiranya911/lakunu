package org.lakunu.web.service;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import org.lakunu.web.models.Lab;
import org.lakunu.web.models.Course;
import org.lakunu.web.utils.Security;

import java.util.Date;

import static com.google.common.base.Preconditions.checkArgument;
import static org.lakunu.web.utils.Security.checkPermissions;

public final class CourseService extends AbstractDomainService {

    public CourseService(DAOFactory daoFactory) {
        super(daoFactory);
    }

    public String addCourse(String name, String description) {
        checkArgument(!Strings.isNullOrEmpty(name), "name is required");
        checkArgument(name.length() <= 128, "name is too long");
        checkArgument(!Strings.isNullOrEmpty(description), "description is required");
        checkArgument(description.length() <= 512, "description is too long");
        checkPermissions("course:add");

        Course course = Course.newBuilder()
                .setName(name)
                .setDescription(description)
                .setOwner(Security.getCurrentUser())
                .setCreatedAt(new Date())
                .build();
        String courseId = daoFactory.getCourseDAO().addCourse(course);
        Security.clearCoursePermissionCache();
        return courseId;
    }

    public Course getCourse(String courseId) {
        checkArgument(!Strings.isNullOrEmpty(courseId), "CourseID is required");
        checkPermissions("course:get:" + courseId);
        return daoFactory.getCourseDAO().getCourse(courseId);
    }

    public ImmutableList<Course> getOwnedCourses() {
        checkPermissions("course:getOwned");
        return daoFactory.getCourseDAO().getOwnedCourses();
    }

    public ImmutableList<Lab> getLabs(String courseId) {
        checkArgument(!Strings.isNullOrEmpty(courseId), "CourseID is required");
        checkPermissions("course:getLabs:" + courseId);
        return daoFactory.getLabDAO().getLabs(courseId);
    }
}
