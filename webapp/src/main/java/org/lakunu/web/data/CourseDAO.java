package org.lakunu.web.data;

import com.google.common.collect.ImmutableList;

import static org.lakunu.web.security.Utils.checkPermissions;

public abstract class CourseDAO {

    public final ImmutableList<Course> getOwnedCourses() {
        checkPermissions("course:listOwned");
        return doGetOwnedCourses();
    }

    protected abstract ImmutableList<Course> doGetOwnedCourses();

}
