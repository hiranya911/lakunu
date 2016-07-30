package org.lakunu.web.models;

import com.google.common.base.Strings;

import java.io.Serializable;
import java.util.Date;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class Submission implements Serializable {

    private final String id;
    private final String userId;
    private final String labId;
    private final Date submittedAt;
    private final String type;
    private final byte[] data;

    private Submission(Builder builder) {
        checkArgument(!Strings.isNullOrEmpty(builder.id), "id is required");
        checkArgument(!Strings.isNullOrEmpty(builder.userId), "UserId is required");
        checkArgument(!Strings.isNullOrEmpty(builder.labId), "LabId is required");
        checkNotNull(builder.submittedAt, "submittedAt is required");
        checkArgument(!Strings.isNullOrEmpty(builder.type), "Type is required");
        checkArgument(builder.data != null && builder.data.length > 0, "Data is required");
        this.id = builder.id;
        this.userId = builder.userId;
        this.labId = builder.labId;
        this.submittedAt = builder.submittedAt;
        this.type = builder.type;
        this.data = builder.data;
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

    public byte[] getData() {
        return data;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private String id = "_unidentified_";
        private String userId;
        private String labId;
        private Date submittedAt;
        private String type;
        private byte[] data;

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

        public Builder setData(byte[] data) {
            this.data = data;
            return this;
        }

        public Submission build() {
            return new Submission(this);
        }
    }

}
