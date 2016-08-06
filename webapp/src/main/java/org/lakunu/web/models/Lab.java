package org.lakunu.web.models;

import com.google.common.base.Strings;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class Lab implements Serializable {

    private final String id;
    private final String name;
    private final String description;
    private final String createdBy;
    private final Date createdAt;
    private final String courseId;
    private final byte[] configuration;

    private final boolean published;
    private final Date submissionDeadline;
    private final boolean allowLateSubmissions;

    private Lab(Builder builder) {
        checkArgument(!Strings.isNullOrEmpty(builder.id), "id is required");
        checkArgument(!Strings.isNullOrEmpty(builder.name), "name is required");
        checkArgument(builder.name.length() <= 128, "name is too long");
        checkArgument(!Strings.isNullOrEmpty(builder.description), "description is required");
        checkArgument(builder.description.length() <= 64 * 1024, "description is too long");
        checkArgument(!Strings.isNullOrEmpty(builder.courseId), "CourseID is required");
        checkArgument(!Strings.isNullOrEmpty(builder.createdBy), "createdBy is required");
        checkNotNull(builder.createdAt, "createdAt is required");
        if (builder.published) {
            checkArgument(builder.configuration != null && builder.configuration.length > 0,
                    "Configuration is required when published");
        }
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.createdBy = builder.createdBy;
        this.createdAt = builder.createdAt;
        this.courseId = builder.courseId;
        this.configuration = builder.configuration;
        this.published = builder.published;
        this.submissionDeadline = builder.submissionDeadline;
        this.allowLateSubmissions = builder.allowLateSubmissions;
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

    public Date getCreatedAt() {
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

    public Date getSubmissionDeadline() {
        return submissionDeadline;
    }

    public boolean isAllowLateSubmissions() {
        return allowLateSubmissions;
    }

    public boolean isOpenForSubmissions() {
        return isOpenForSubmissions(new Date());
    }

    public boolean isOpenForSubmissions(Date submittedAt) {
        if (published) {
            if (submissionDeadline != null) {
                return (submissionDeadline.after(submittedAt) || allowLateSubmissions);
            } else {
                return true;
            }
        }
        return false;
    }

    public int getHash() {
        if (configuration != null) {
            return Arrays.hashCode(configuration);
        } else {
            return 0;
        }
    }

    public Update newUpdate() {
        return new Update(this);
    }

    public PublishSettings newPublishSettings() {
        return new PublishSettings(this);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Update extends Mutation<Lab> {

        private String name;
        private String description;
        private byte[] configuration;

        private Update(Lab original) {
            super(original);
            this.name = original.name;
            this.description = original.description;
            this.configuration = original.configuration;
        }

        public Update setName(String name) {
            this.name = name;
            return this;
        }

        public Update setDescription(String description) {
            this.description = description;
            return this;
        }

        public Update setConfiguration(byte[] configuration) {
            this.configuration = configuration;
            return this;
        }

        @Override
        public Lab apply() {
            return newBuilder().setId(original.id)
                    .setName(name)
                    .setDescription(description)
                    .setConfiguration(configuration)
                    .setCreatedAt(original.createdAt)
                    .setCreatedBy(original.createdBy)
                    .setPublished(original.published)
                    .setCourseId(original.courseId)
                    .setSubmissionDeadline(original.submissionDeadline)
                    .setAllowLateSubmissions(original.allowLateSubmissions)
                    .build();
        }
    }

    public static class PublishSettings extends Mutation<Lab> {

        private boolean published;
        private Date submissionDeadline;
        private boolean allowLateSubmissions;

        private PublishSettings(Lab original) {
            super(original);
            this.published = original.published;
            this.submissionDeadline = original.submissionDeadline;
            this.allowLateSubmissions = original.allowLateSubmissions;
        }

        public PublishSettings setPublished(boolean published) {
            this.published = published;
            return this;
        }

        public PublishSettings setSubmissionDeadline(Date submissionDeadline) {
            this.submissionDeadline = submissionDeadline;
            return this;
        }

        public PublishSettings setAllowLateSubmissions(boolean allowLateSubmissions) {
            this.allowLateSubmissions = allowLateSubmissions;
            return this;
        }

        @Override
        public Lab apply() {
            return newBuilder().setId(original.id)
                    .setName(original.name)
                    .setDescription(original.description)
                    .setConfiguration(original.configuration)
                    .setCreatedAt(original.createdAt)
                    .setCreatedBy(original.createdBy)
                    .setPublished(published)
                    .setCourseId(original.courseId)
                    .setSubmissionDeadline(submissionDeadline)
                    .setAllowLateSubmissions(allowLateSubmissions)
                    .build();
        }
    }

    public static class Builder {
        private String id = "_unidentified_";
        private String name;
        private String description;
        private String createdBy;
        private Date createdAt;
        private String courseId;
        private byte[] configuration;

        private boolean published;
        private Date submissionDeadline;
        private boolean allowLateSubmissions;

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

        public Builder setCreatedAt(Date createdAt) {
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

        public Builder setSubmissionDeadline(Date submissionDeadline) {
            this.submissionDeadline = submissionDeadline;
            return this;
        }

        public Builder setAllowLateSubmissions(boolean allowLateSubmissions) {
            this.allowLateSubmissions = allowLateSubmissions;
            return this;
        }

        public Lab build() {
            return new Lab(this);
        }
    }
}
