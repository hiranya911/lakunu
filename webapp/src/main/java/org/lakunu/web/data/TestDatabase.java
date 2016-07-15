package org.lakunu.web.data;

import com.google.common.collect.ImmutableList;

public class TestDatabase {

    public static ImmutableList<Course> getCoursesByOwner(String owner) {
        return ImmutableList.of(
                new Course("cs56", owner),
                new Course("cs270", owner)
        );
    }

}
