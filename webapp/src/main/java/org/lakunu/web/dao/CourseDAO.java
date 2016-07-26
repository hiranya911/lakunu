package org.lakunu.web.dao;

import com.google.common.collect.ImmutableList;
import org.lakunu.web.models.Lab;
import org.lakunu.web.models.Course;

public interface CourseDAO {

    String addCourse(Course course);
    Course getCourse(String courseId);
    ImmutableList<Course> getOwnedCourses();
    ImmutableList<Lab> getLabs(String courseId);

}
