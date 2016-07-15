package org.lakunu.web.data;

import com.google.common.base.Strings;

import java.sql.Timestamp;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class Course {

    private final long id;
    private String name;
    private String description;
    private final String owner;
    private final Timestamp createdAt;

    private Course(Builder builder) {
        checkArgument(builder.id >= -1, "ID must be greater than or equal to -1");
        checkArgument(!Strings.isNullOrEmpty(builder.name), "name is required");
        checkArgument(!Strings.isNullOrEmpty(builder.description), "description is required");
        checkArgument(!Strings.isNullOrEmpty(builder.owner), "owner is required");
        checkNotNull(builder.createdAt, "created time is required");
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.owner = builder.owner;
        this.createdAt = builder.createdAt;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Course setName(String name) {
        checkArgument(!Strings.isNullOrEmpty(name), "name is required");
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Course setDescription(String description) {
        checkArgument(!Strings.isNullOrEmpty(description), "description is required");
        this.description = description;
        return this;
    }

    public String getOwner() {
        return owner;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private long id = -1L;
        private String name;
        private String description;
        private String owner;
        private Timestamp createdAt;

        private Builder() {
        }

        public Builder setId(long id) {
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

        public Builder setCreatedAt(Timestamp createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Course build() {
            return new Course(this);
        }
    }

}
