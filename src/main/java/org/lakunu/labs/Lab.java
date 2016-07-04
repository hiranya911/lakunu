package org.lakunu.labs;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.io.FileUtils;
import org.lakunu.labs.resources.Resource;
import org.lakunu.labs.submit.Submission;
import org.lakunu.labs.utils.LoggingOutputHandler;


import java.io.File;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class Lab {

    private final String name;
    private final Lifecycle lifecycle;
    private final File workingDirectory;
    private final ImmutableSet<Resource> resources;

    private Lab(Builder builder) {
        checkArgument(!Strings.isNullOrEmpty(builder.name), "Name is required");
        checkNotNull(builder.lifecycle, "Lifecycle is required");
        checkNotNull(builder.workingDirectory, "Working directory is required");
        checkArgument(builder.workingDirectory.isDirectory() && builder.workingDirectory.exists(),
                "Working directory path is not a directory or does not exist");
        this.name = builder.name;
        this.lifecycle = builder.lifecycle;
        this.workingDirectory = builder.workingDirectory;
        this.resources = builder.resources.build();
    }

    public String getName() {
        return name;
    }

    public void run(Submission submission, String phase) {
        EvaluationContext context = EvaluationContext.newBuilder()
                .setSubmission(submission)
                .setWorkingDirectory(workingDirectory)
                .setOutputHandler(new LoggingOutputHandler())
                .build();
        try {
            lifecycle.run(context, phase);
        } finally {
            context.cleanup();
        }
    }

    public void run(Submission submission) {
        run(submission, null);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private String name;
        private Lifecycle lifecycle;
        private File workingDirectory = FileUtils.getTempDirectory();
        private ImmutableSet.Builder<Resource> resources = ImmutableSet.builder();

        private Builder() {
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setLifecycle(Lifecycle lifecycle) {
            this.lifecycle = lifecycle;
            return this;
        }

        public Builder setWorkingDirectory(File workingDirectory) {
            this.workingDirectory = workingDirectory;
            return this;
        }

        public Builder addResource(Resource resource) {
            this.resources.add(resource);
            return this;
        }

        public Lab build() {
            return new Lab(this);
        }
    }

}
