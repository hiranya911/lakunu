package org.lakunu.web.data;

import com.google.common.collect.ImmutableList;

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

    public final Course getCourse(long id) {
        checkPermissions("course:get:" + id);
        return null;
    }

    protected abstract ImmutableList<Course> doGetOwnedCourses() throws Exception;
    protected abstract String doAddCourse(Course course) throws Exception;
}
