package org.lakunu.web.service;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.lakunu.web.models.Lab;
import org.lakunu.web.models.Course;
import org.lakunu.web.utils.Security;

import java.util.Date;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.lakunu.web.utils.Security.checkPermissions;

public final class CourseService extends AbstractDomainService {

    public static final int ROLE_OWNER = 1;
    public static final int ROLE_INSTRUCTOR = 2;
    public static final int ROLE_STUDENT = 3;

    private static final ImmutableList<Integer> COURSE_ROLES = ImmutableList.of(
            ROLE_OWNER, ROLE_INSTRUCTOR, ROLE_STUDENT
    );

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

    public void shareCourse(Course course, ImmutableSet<String> users, int role) {
        checkNotNull(course, "Course is required");
        checkNotNull(users, "Users list is required");
        checkArgument(COURSE_ROLES.contains(role), "Unsupported role");
        checkPermissions("course:share:" + course.getId());
        daoFactory.getCourseDAO().shareCourse(course, users, role);
    }

    public ImmutableList<Lab> getLabs(String courseId) {
        checkArgument(!Strings.isNullOrEmpty(courseId), "CourseID is required");
        checkPermissions("course:getLabs:" + courseId);
        return daoFactory.getLabDAO().getLabs(courseId);
    }
}
