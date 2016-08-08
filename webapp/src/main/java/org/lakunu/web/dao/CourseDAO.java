package org.lakunu.web.dao;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.lakunu.web.models.Course;

public interface CourseDAO {

    String addCourse(Course course);
    Course getCourse(String courseId);
    ImmutableList<Course> getOwnedCourses();
    ImmutableList<Course> getCoursesAsStudent();
    void shareCourse(Course course, ImmutableSet<String> users, int role);

}
