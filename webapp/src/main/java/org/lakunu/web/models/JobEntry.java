package org.lakunu.web.models;

import com.google.common.base.Strings;

import java.io.Serializable;
import java.util.Date;

import static com.google.common.base.Preconditions.checkArgument;

public final class JobEntry implements Serializable {

    public static final int JOB_ENTRY_STATUS_READY = 0;
    public static final int JOB_ENTRY_STATUS_PROCESSING = 1;

    private final String id;
    private final String submissionId;
    private final int status;
    private final Date startedAt;

    private JobEntry(Builder builder) {
        checkArgument(!Strings.isNullOrEmpty(builder.id), "ID is required");
        checkArgument(!Strings.isNullOrEmpty(builder.submissionId), "SubmissionID is required");
        this.id = builder.id;
        this.submissionId = builder.submissionId;
        this.status = builder.status;
        this.startedAt = builder.startedAt;
    }

    public String getId() {
        return id;
    }

    public String getSubmissionId() {
        return submissionId;
    }

    public int getStatus() {
        return status;
    }

    public Date getStartedAt() {
        return startedAt;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private String id;
        private String submissionId;
        private int status;
        private Date startedAt;

        private Builder() {
        }

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setSubmissionId(String submissionId) {
            this.submissionId = submissionId;
            return this;
        }

        public Builder setStatus(int status) {
            this.status = status;
            return this;
        }

        public Builder setStartedAt(Date startedAt) {
            this.startedAt = startedAt;
            return this;
        }

        public JobEntry build() {
            return new JobEntry(this);
        }
    }

}
