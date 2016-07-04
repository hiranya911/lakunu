package org.lakunu.labs;

import org.apache.commons.io.FileUtils;
import org.lakunu.labs.submit.Submission;
import org.lakunu.labs.utils.LoggingOutputHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class EvaluationContext {

    private final File workingDirectory;
    private final LabOutputHandler outputHandler;
    private File evaluationDirectory;
    private final File submissionDirectory;

    private EvaluationContext(Builder builder) {
        checkNotNull(builder.submission, "Submission is required");
        checkNotNull(builder.workingDirectory, "Working directory is required");
        checkArgument(builder.workingDirectory.isDirectory() && builder.workingDirectory.exists(),
                "Working directory path is not a directory or does not exist");
        checkNotNull(builder.outputHandler, "Output handler is required");
        this.workingDirectory = builder.workingDirectory;
        this.outputHandler = builder.outputHandler;
        this.submissionDirectory = builder.submission.initSubmissionDirectory(this);
    }

    public synchronized File getEvaluationDirectory() {
        if (evaluationDirectory == null) {
            try {
                Path workingDirPath = Files.createTempDirectory(workingDirectory.toPath(), "lakunu");
                evaluationDirectory = workingDirPath.toFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return evaluationDirectory;
    }

    public File getSubmissionDirectory() {
        return submissionDirectory;
    }

    public LabOutputHandler getOutputHandler() {
        return outputHandler;
    }

    public synchronized void cleanup() {
        FileUtils.deleteQuietly(evaluationDirectory);
        evaluationDirectory = null;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private Submission submission;
        private File workingDirectory;
        private LabOutputHandler outputHandler = new LoggingOutputHandler();

        private Builder() {
        }

        public Builder setSubmission(Submission submission) {
            this.submission = submission;
            return this;
        }

        public Builder setWorkingDirectory(File workingDirectory) {
            this.workingDirectory = workingDirectory;
            return this;
        }

        public Builder setOutputHandler(LabOutputHandler outputHandler) {
            this.outputHandler = outputHandler;
            return this;
        }

        public EvaluationContext build() {
            return new EvaluationContext(this);
        }
    }

}
