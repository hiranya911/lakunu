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
    private final String name;
    private final String description;
    private final String createdBy;
    private final Timestamp createdAt;
    private final String courseId;
    private final byte[] configuration;
    private final boolean published;

    private Lab(Builder builder) {
        checkArgument(!Strings.isNullOrEmpty(builder.id), "ID is required");
        checkArgument(!Strings.isNullOrEmpty(builder.name), "name is required");
        checkArgument(builder.name.length() <= 128, "name is too long");
        checkArgument(!Strings.isNullOrEmpty(builder.description), "description is required");
        checkArgument(builder.description.length() <= 512, "description is too long");
        checkArgument(!Strings.isNullOrEmpty(builder.createdBy), "createdBy is required");
        checkNotNull(builder.createdAt, "created time is required");
        checkArgument(!Strings.isNullOrEmpty(builder.courseId), "courseId is required");
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.createdBy = builder.createdBy;
        this.createdAt = builder.createdAt;
        this.courseId = builder.courseId;
        this.configuration = builder.configuration;
        this.published = builder.published;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
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

    public byte[] getConfiguration() {
        return configuration;
    }

    public boolean isPublished() {
        return published;
    }

    public Updater getUpdater() {
        return new Updater(this);
    }

    public static class Updater {

        private final Builder builder;

        private Updater(Lab lab) {
            this.builder = newBuilder().setId(lab.id)
                    .setName(lab.name)
                    .setDescription(lab.description)
                    .setCourseId(lab.courseId)
                    .setCreatedAt(lab.createdAt)
                    .setCreatedBy(lab.createdBy)
                    .setConfiguration(lab.configuration)
                    .setPublished(lab.published);
        }

        public Updater setName(String name) {
            builder.setName(name);
            return this;
        }

        public Updater setDescription(String description) {
            builder.setDescription(description);
            return this;
        }

        public Updater setConfiguration(byte[] configuration) {
            builder.setConfiguration(configuration);
            return this;
        }

        public Lab update() {
            return builder.build();
        }

    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private String id;
        private String name;
        private String description;
        private String createdBy;
        private Timestamp createdAt;
        private String courseId;
        private byte[] configuration;
        private boolean published;

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

        public Builder setConfiguration(byte[] configuration) {
            this.configuration = configuration;
            return this;
        }

        public Builder setPublished(boolean published) {
            this.published = published;
            return this;
        }

        public Lab build() {
            return new Lab(this);
        }

        public Lab buildForAddition() {
            this.id = DEFAULT_ID;
            this.createdBy = Security.getCurrentUser();
            this.createdAt = new Timestamp(Calendar.getInstance().getTime().getTime());
            this.published = false;
            return new Lab(this);
        }
    }
}

