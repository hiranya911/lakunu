package org.lakunu.labs;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import org.lakunu.labs.submit.Submission;
import org.lakunu.labs.utils.LoggingOutputHandler;

import static com.google.common.base.Preconditions.checkArgument;

public final class Lab {

    private final String name;
    private final ImmutableSet<Resource> resources;
    private final Lifecycle lifecycle;

    private Lab(Builder builder) {
        checkArgument(!Strings.isNullOrEmpty(builder.name), "Name is required");
        checkArgument(builder.lifecycle != null, "Lifecycle is required");
        this.name = builder.name;
        this.resources = builder.resources.build();
        this.lifecycle = builder.lifecycle;
    }

    public String getName() {
        return name;
    }

    public void run(Submission submission) {
        run(submission, null);
    }

    public void run(Submission submission, String phase) {
        submission.prepare();
        LabContext context = LabContext.newBuilder()
                .setWorkingDir(submission.getDirectory())
                .setOutputHandler(new LoggingOutputHandler())
                .build();
        lifecycle.run(context, phase);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private ImmutableSet.Builder<Resource> resources = ImmutableSet.builder();
        private Lifecycle lifecycle;

        private Builder() {
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder addResource(Resource resource) {
            this.resources.add(resource);
            return this;
        }

        public Builder setLifecycle(Lifecycle lifecycle) {
            this.lifecycle = lifecycle;
            return this;
        }

        public Lab build() {
            return new Lab(this);
        }
    }

}
