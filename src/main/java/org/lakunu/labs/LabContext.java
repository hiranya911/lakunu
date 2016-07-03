package org.lakunu.labs;

import org.lakunu.labs.utils.LoggingOutputHandler;

import java.io.File;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class LabContext {

    private final File workingDir;
    private final LabOutputHandler outputHandler;

    private LabContext(Builder builder) {
        checkNotNull(builder.workingDir, "Working directory is required");
        checkArgument(builder.workingDir.isDirectory() && builder.workingDir.exists(),
                "Working directory path is not a directory or does not exist");
        checkNotNull(builder.outputHandler, "Output handler is required");
        this.workingDir = builder.workingDir;
        this.outputHandler = builder.outputHandler;
    }

    public File getWorkingDir() {
        return workingDir;
    }

    public LabOutputHandler getOutputHandler() {
        return outputHandler;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private File workingDir;
        private LabOutputHandler outputHandler = new LoggingOutputHandler();

        private Builder() {
        }

        public Builder setWorkingDir(File workingDir) {
            this.workingDir = workingDir;
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
