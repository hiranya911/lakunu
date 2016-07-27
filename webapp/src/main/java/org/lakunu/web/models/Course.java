package org.lakunu.web.models;

import com.google.common.base.Strings;

import java.io.Serializable;
import java.util.Date;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class Course implements Serializable {

    private final String id;
    private final String name;
    private final String description;
    private final String owner;
    private final Date createdAt;

    private Course(Builder builder) {
        checkArgument(!Strings.isNullOrEmpty(builder.id), "id is required");
        checkArgument(!Strings.isNullOrEmpty(builder.name), "name is required");
        checkArgument(builder.name.length() <= 128, "name is too long");
        checkArgument(!Strings.isNullOrEmpty(builder.description), "description is required");
        checkArgument(builder.description.length() <= 64 * 1024, "description is too long");
        checkArgument(!Strings.isNullOrEmpty(builder.owner), "owner is required");
        checkNotNull(builder.createdAt, "createdAt is required");
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.owner = builder.owner;
        this.createdAt = builder.createdAt;
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

    public String getOwner() {
        return owner;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private String id = "_unidentified_";
        private String name;
        private String description;
        private String owner;
        private Date createdAt;

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

        public Builder setOwner(String owner) {
            this.owner = owner;
            return this;
        }

        public Builder setCreatedAt(Date createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Course build() {
            return new Course(this);
        }
    }
}
