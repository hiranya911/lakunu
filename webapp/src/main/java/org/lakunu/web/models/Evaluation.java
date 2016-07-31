package org.lakunu.web.models;

import com.google.common.base.Strings;

import java.io.Serializable;
import java.util.Date;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class Evaluation implements Serializable {

    private final String id;
    private final String submissionId;
    private final Date startedAt;
    private final Date finishedAt;

    private Evaluation(Builder builder) {
        checkArgument(!Strings.isNullOrEmpty(builder.id), "ID is required");
        checkArgument(!Strings.isNullOrEmpty(builder.submissionId), "SubmissionID is required");
        checkNotNull(builder.startedAt, "Start time is required");
        this.id = builder.id;
        this.submissionId = builder.submissionId;
        this.startedAt = builder.startedAt;
        this.finishedAt = builder.finishedAt;
    }

    public String getId() {
        return id;
    }

    public String getSubmissionId() {
        return submissionId;
    }

    public Date getStartedAt() {
        return startedAt;
    }

    public Date getFinishedAt() {
        return finishedAt;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private String id = "_unidentified_";
        private String submissionId;
        private Date startedAt;
        private Date finishedAt;

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

        public Builder setStartedAt(Date startedAt) {
            this.startedAt = startedAt;
            return this;
        }

        public Builder setFinishedAt(Date finishedAt) {
            this.finishedAt = finishedAt;
            return this;
        }

        public Evaluation build() {
            return new Evaluation(this);
        }
    }
}
