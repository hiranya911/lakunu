package org.lakunu.web.service.permissions;

import com.google.common.base.Strings;

import static com.google.common.base.Preconditions.checkArgument;

public final class CoursePermission extends Permission {

    private final String courseId;

    private CoursePermission(String operation, String courseId) {
        super("course", operation);
        checkArgument(!Strings.isNullOrEmpty(courseId), "CourseID is required");
        this.courseId = courseId;
    }

    @Override
    protected String getIdentifier() {
        return courseId;
    }

    public static final CoursePermission ADD = new CoursePermission("add", "*");
    public static final CoursePermission GET_OWNED_COURSES = new CoursePermission("getOwned", "*");
    public static final CoursePermission GET_COURSES_AS_STUDENT = new CoursePermission("getAsStudent", "*");

    public static CoursePermission GET(String courseId) {
        return new CoursePermission("get", courseId);
    }

    public static CoursePermission GET_STUDENTS(String courseId) {
        return new CoursePermission("getStudents", courseId);
    }

    public static CoursePermission GET_LABS(String courseId) {
        return new CoursePermission("getLabs", courseId);
    }

    public static CoursePermission SHARE(String courseId) {
        return new CoursePermission("share", courseId);
    }

}
