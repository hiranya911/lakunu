package org.lakunu.web.data;

import com.google.common.base.Strings;
import org.lakunu.web.utils.Security;

import java.sql.Timestamp;
import java.util.Calendar;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class Course {

    public static final String DEFAULT_ID = "_unidentified_";

    private final String id;
    private String name;
    private String description;
    private final String owner;
    private final Timestamp createdAt;

    private Course(Builder builder) {
        checkArgument(!Strings.isNullOrEmpty(builder.id), "ID is required");
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

    public String getId() {
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

        private String id = DEFAULT_ID;
        private String name;
        private String description;
        private String owner;
        private Timestamp createdAt;

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

        public Builder setCreatedAt(Timestamp createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Course build() {
            if (this.owner == null) {
                this.owner = Security.getCurrentUser();
            }
            if (this.createdAt == null) {
                this.createdAt = new Timestamp(Calendar.getInstance().getTime().getTime());
            }
            return new Course(this);
        }
    }

}
