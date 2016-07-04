package org.lakunu.labs;

import org.lakunu.labs.utils.LoggingOutputHandler;

import java.io.File;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class LabContext {

    private final File submissionDir;
    private final LabOutputHandler outputHandler;

    private LabContext(Builder builder) {
        checkNotNull(builder.submissionDir, "Working directory is required");
        checkArgument(builder.submissionDir.isDirectory() && builder.submissionDir.exists(),
                "Working directory path is not a directory or does not exist");
        checkNotNull(builder.outputHandler, "Output handler is required");
        this.submissionDir = builder.submissionDir;
        this.outputHandler = builder.outputHandler;
    }

    public File getSubmissionDir() {
        return submissionDir;
    }

    public LabOutputHandler getOutputHandler() {
        return outputHandler;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private File submissionDir;
        private LabOutputHandler outputHandler = new LoggingOutputHandler();

        private Builder() {
        }

        public Builder setSubmissionDir(File submissionDir) {
            this.submissionDir = submissionDir;
            return this;
        }

        public Builder setOutputHandler(LabOutputHandler outputHandler) {
            this.outputHandler = outputHandler;
            return this;
        }

        public LabContext build() {
            return new LabContext(this);
        }
    }
}
