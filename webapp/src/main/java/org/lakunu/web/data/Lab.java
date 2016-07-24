package org.lakunu.web.data;

import com.google.common.base.Strings;
import org.lakunu.web.utils.Security;

import java.sql.Timestamp;
import java.util.Calendar;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class Lab {

    private static final String DEFAULT_ID = "_unidentified_";

    private final String id;
    private String name;
    private String version;
    private String description;
    private final String createdBy;
    private final Timestamp createdAt;
    private final String courseId;

    private Lab(Builder builder) {
        checkArgument(!Strings.isNullOrEmpty(builder.id), "ID is required");
        checkArgument(!Strings.isNullOrEmpty(builder.name), "name is required");
        checkArgument(builder.name.length() <= 128, "name is too long");
        checkArgument(!Strings.isNullOrEmpty(builder.version), "version is required");
        checkArgument(builder.version.length() <= 128, "version is too long");
        checkArgument(!Strings.isNullOrEmpty(builder.description), "description is required");
        checkArgument(builder.description.length() <= 512, "description is too long");
        checkArgument(!Strings.isNullOrEmpty(builder.createdBy), "createdBy is required");
        checkNotNull(builder.createdAt, "created time is required");
        checkArgument(!Strings.isNullOrEmpty(builder.courseId), "courseId is required");
        this.id = builder.id;
        this.name = builder.name;
        this.version = builder.version;
        this.description = builder.description;
        this.createdBy = builder.createdBy;
        this.createdAt = builder.createdAt;
        this.courseId = builder.courseId;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Lab setName(String name) {
        this.name = name;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public Lab setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Lab setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public String getCourseId() {
        return courseId;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private String id;
        private String name;
        private String version;
        private String description;
        private String createdBy;
        private Timestamp createdAt;
        private String courseId;

        private Builder() {
        }

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setVersion(String version) {
            this.version = version;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setCreatedBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Builder setCreatedAt(Timestamp createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder setCourseId(String courseId) {
            this.courseId = courseId;
            return this;
        }

        public Lab build() {
            return new Lab(this);
        }

        public Lab buildForAddition() {
            this.id = DEFAULT_ID;
            this.createdBy = Security.getCurrentUser();
            this.createdAt = new Timestamp(Calendar.getInstance().getTime().getTime());
            return new Lab(this);
        }
    }
}

