package org.lakunu.web.data;

import static com.google.common.base.Preconditions.checkNotNull;

public final class DAOCollection {

    public static final String DAO_COLLECTION = "DAO_COLLECTION";

    private final CourseDAO courseDAO;

    private DAOCollection(Builder builder) {
        checkNotNull(builder.courseDAO, "CourseDAO is required");
        this.courseDAO = builder.courseDAO;
    }

    public CourseDAO getCourseDAO() {
        return courseDAO;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private CourseDAO courseDAO;

        private Builder() {
        }

        public Builder setCourseDAO(CourseDAO courseDAO) {
            this.courseDAO = courseDAO;
            return this;
        }

        public DAOCollection build() {
            return new DAOCollection(this);
        }
    }
}
