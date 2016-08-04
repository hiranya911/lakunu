package org.lakunu.web.models;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import java.io.Serializable;
import java.util.Date;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class SubmissionView implements Serializable {

    private final String id;
    private final String userId;
    private final String labId;
    private final Date submittedAt;
    private final String type;
    private final ImmutableList<Evaluation> evaluations;

    private SubmissionView(Builder builder) {
        checkArgument(!Strings.isNullOrEmpty(builder.id), "id is required");
        checkArgument(!Strings.isNullOrEmpty(builder.userId), "UserId is required");
        checkArgument(!Strings.isNullOrEmpty(builder.labId), "LabId is required");
        checkNotNull(builder.submittedAt, "submittedAt is required");
        checkArgument(!Strings.isNullOrEmpty(builder.type), "Type is required");
        this.id = builder.id;
        this.userId = builder.userId;
        this.labId = builder.labId;
        this.submittedAt = builder.submittedAt;
        this.type = builder.type;
        this.evaluations = builder.evaluations.build();
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getLabId() {
        return labId;
    }

    public Date getSubmittedAt() {
        return submittedAt;
    }

    public String getType() {
        return type;
    }

    public ImmutableList<Evaluation> getEvaluations() {
        return evaluations;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private String id;
        private String userId;
        private String labId;
        private Date submittedAt;
        private String type;
        private final ImmutableList.Builder<Evaluation> evaluations = ImmutableList.builder();

        private Builder() {
        }

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder setLabId(String labId) {
            this.labId = labId;
            return this;
        }

        public Builder setSubmittedAt(Date submittedAt) {
            this.submittedAt = submittedAt;
            return this;
        }

        public Builder setType(String type) {
            this.type = type;
            return this;
        }

        public Builder addEvaluation(Evaluation evaluation) {
            this.evaluations.add(evaluation);
            return this;
        }

        public SubmissionView build() {
            return new SubmissionView(this);
        }
    }
}
