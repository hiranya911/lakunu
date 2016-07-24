package org.lakunu.web.data;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.lakunu.web.utils.Security.checkPermissions;

public abstract class CourseDAO {

    public final ImmutableList<Course> getOwnedCourses() {
        checkPermissions("course:listOwned");
        try {
            return doGetOwnedCourses();
        } catch (Exception e) {
            throw new DAOException("Error while retrieving owned courses", e);
        }
    }

    public final String addCourse(Course course) {
        checkNotNull(course, "Course is required");
        checkPermissions("course:add");
        try {
            return doAddCourse(course);
        } catch (Exception e) {
            throw new DAOException("Error while adding course", e);
        }
    }

    public final Course getCourse(String courseId) {
        checkArgument(!Strings.isNullOrEmpty(courseId), "Course ID is required");
        checkPermissions("course:get:" + courseId);
        try {
            return doGetCourse(courseId);
        } catch (Exception e) {
            throw new DAOException("Error while retrieving course", e);
        }
    }

    public ImmutableList<Lab> getLabs(String courseId) {
        checkArgument(!Strings.isNullOrEmpty(courseId), "Course ID is required");
        checkPermissions("course:listLabs:" + courseId);
        try {
            return doGetLabs(courseId);
        } catch (Exception e) {
            throw new DAOException("Error while retrieving labs", e);
        }
    }

    protected abstract ImmutableList<Course> doGetOwnedCourses() throws Exception;
    protected abstract String doAddCourse(Course course) throws Exception;
    protected abstract Course doGetCourse(String courseId) throws Exception;
    protected abstract ImmutableList<Lab> doGetLabs(String courseId) throws Exception;
}
