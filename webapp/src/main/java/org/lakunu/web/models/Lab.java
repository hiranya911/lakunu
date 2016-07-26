package org.lakunu.web.models;

import com.google.common.base.Strings;

import java.io.Serializable;
import java.util.Date;

import static com.google.common.base.Preconditions.checkArgument;

public final class Lab implements Serializable {

    private String id;
    private String name;
    private String description;
    private String createdBy;
    private Date createdAt;
    private String courseId;
    private byte[] configuration;

    private boolean published;
    private Date submissionDeadline;
    private boolean allowLateSubmissions;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public byte[] getConfiguration() {
        return configuration;
    }

    public void setConfiguration(byte[] configuration) {
        this.configuration = configuration;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public Date getSubmissionDeadline() {
        return submissionDeadline;
    }

    public void setSubmissionDeadline(Date submissionDeadline) {
        this.submissionDeadline = submissionDeadline;
    }

    public boolean isAllowLateSubmissions() {
        return allowLateSubmissions;
    }

    public void setAllowLateSubmissions(boolean allowLateSubmissions) {
        this.allowLateSubmissions = allowLateSubmissions;
    }

    public Lab validate() {
        checkArgument(!Strings.isNullOrEmpty(name), "name is required");
        checkArgument(name.length() <= 128, "name is too long");
        checkArgument(!Strings.isNullOrEmpty(description), "description is required");
        checkArgument(description.length() <= 512, "description is too long");
        checkArgument(!Strings.isNullOrEmpty(courseId), "CourseID is required");
        return this;
    }
}
