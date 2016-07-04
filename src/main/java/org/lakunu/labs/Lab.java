package org.lakunu.labs;

import com.google.common.base.Strings;
import org.apache.commons.io.FileUtils;
import org.lakunu.labs.submit.Submission;
import org.lakunu.labs.utils.LoggingOutputHandler;

import java.io.File;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkArgument;

public final class Lab {

    private final String name;
    private final Lifecycle lifecycle;
    private final String targetDir;

    private Lab(Builder builder) {
        checkArgument(!Strings.isNullOrEmpty(builder.name), "Name is required");
        checkArgument(builder.lifecycle != null, "Lifecycle is required");
        checkArgument(!Strings.isNullOrEmpty(builder.targetDir), "Target directory is required");
        this.name = builder.name;
        this.lifecycle = builder.lifecycle;
        this.targetDir = builder.targetDir;
    }

    public String getName() {
        return name;
    }

    private synchronized File createTargetDirectory() {
        File target = new File(this.targetDir).getAbsoluteFile();
        try {
            FileUtils.forceMkdir(target);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return target;
    }

    public void run(Submission submission, String phase) {
        File target = createTargetDirectory();
        submission.prepare(target);
        LabContext context = LabContext.newBuilder()
                .setSubmissionDir(submission.getDirectory())
                .setOutputHandler(new LoggingOutputHandler())
                .build();
        lifecycle.run(context, phase);
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
        private String targetDir = "target";

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

        public Builder setTargetDir(String targetDir) {
            this.targetDir = targetDir;
            return this;
        }

        public Lab build() {
            return new Lab(this);
        }
    }

}
