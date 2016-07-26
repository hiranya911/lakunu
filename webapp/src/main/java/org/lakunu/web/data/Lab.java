package org.lakunu.web.data;

import com.google.common.base.Strings;
import org.lakunu.web.utils.Security;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class Lab {

    private static final String DEFAULT_ID = "_unidentified_";

    private final String id;
    private String name;
    private String description;
    private final String createdBy;
    private final Timestamp createdAt;
    private final String courseId;
    private byte[] configuration;

    private boolean published;
    private Timestamp submissionDeadline;
    private boolean allowLateSubmissions;

    private Lab(Builder builder) {
        checkArgument(!Strings.isNullOrEmpty(builder.id), "ID is required");
        checkArgument(!Strings.isNullOrEmpty(builder.createdBy), "createdBy is required");
        checkNotNull(builder.createdAt, "created time is required");
        checkArgument(!Strings.isNullOrEmpty(builder.courseId), "courseId is required");
        this.id = builder.id;
        this.setName(builder.name);
        this.setDescription(builder.description);
        this.createdBy = builder.createdBy;
        this.createdAt = builder.createdAt;
        this.courseId = builder.courseId;
        this.setConfiguration(builder.configuration);
        this.setPublished(builder.published);
        this.setSubmissionDeadline(builder.submissionDeadline);
        this.setAllowLateSubmissions(builder.allowLateSubmissions);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        checkArgument(!Strings.isNullOrEmpty(name), "name is required");
        checkArgument(name.length() <= 128, "name is too long");
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    private void setDescription(String description) {
        checkArgument(!Strings.isNullOrEmpty(description), "description is required");
        checkArgument(description.length() <= 512, "description is too long");
        this.description = description;
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

    private void setConfiguration(byte[] configuration) {
        this.configuration = configuration;
    }

    public boolean isPublished() {
        return published;
    }

    private void setPublished(boolean published) {
        this.published = published;
    }

    public Timestamp getSubmissionDeadline() {
        return submissionDeadline;
    }

    private void setSubmissionDeadline(Timestamp submissionDeadline) {
        this.submissionDeadline = submissionDeadline;
    }

    public boolean isAllowLateSubmissions() {
        return allowLateSubmissions;
    }

    private void setAllowLateSubmissions(boolean allowLateSubmissions) {
        this.allowLateSubmissions = allowLateSubmissions;
    }

    public boolean isOpenForSubmissions() {
        boolean beforeDeadline = (submissionDeadline == null ||
                submissionDeadline.before(new Timestamp(new Date().getTime())));
        return published && (beforeDeadline || allowLateSubmissions);
    }

    public void update(Update update) {
        setName(update.name);
        setDescription(update.description);
        setConfiguration(update.configuration);
    }

    public void publish(Publish publish) {
        setPublished(true);
        setSubmissionDeadline(publish.submissionDeadline);
        setAllowLateSubmissions(publish.allowLateSubmissions);
    }

    public void unpublish() {
        setPublished(false);
    }

    public static class Update {

        private String name;
        private String description;
        private byte[] configuration;

        public Update(Lab lab) {
            checkNotNull(lab, "Lab is required");
            this.name = lab.name;
            this.description = lab.description;
            this.configuration = lab.configuration;
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
    }

    public static class Publish {

        private Timestamp submissionDeadline;
        private boolean allowLateSubmissions;

        public Publish setSubmissionDeadline(Timestamp submissionDeadline) {
            this.submissionDeadline = submissionDeadline;
            return this;
        }

        public Publish setAllowLateSubmissions(boolean allowLateSubmissions) {
            this.allowLateSubmissions = allowLateSubmissions;
            return this;
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
        private Timestamp submissionDeadline;
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

        public Builder setSubmissionDeadline(Timestamp submissionDeadline) {
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

        public Lab buildForAddition() {
            this.id = DEFAULT_ID;
            this.createdBy = Security.getCurrentUser();
            this.createdAt = new Timestamp(Calendar.getInstance().getTime().getTime());
            this.published = false;
            return new Lab(this);
        }
    }

    public static String permission(String operation, String courseId, String labId) {
        checkArgument(!Strings.isNullOrEmpty(operation), "operation is required");
        checkArgument(!Strings.isNullOrEmpty(courseId), "courseId is required");
        checkArgument(!Strings.isNullOrEmpty(labId), "labId is required");
        return String.format("lab:%s:%s:%s", operation, courseId, labId);
    }

    public static String permission(String operation, Lab lab) {
        return permission(operation, lab.getCourseId(), lab.getId());
    }

}

