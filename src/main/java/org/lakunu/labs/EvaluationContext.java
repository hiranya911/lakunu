package org.lakunu.labs;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public final class EvaluationContext {

    private final File workingDirectory;
    private final LabOutputHandler outputHandler;

    private File evaluationDirectory;
    private File submissionDirectory;

    EvaluationContext(File workingDirectory, LabOutputHandler outputHandler) {
        checkNotNull(workingDirectory, "Working directory is required");
        checkArgument(workingDirectory.isDirectory() && workingDirectory.exists(),
                "Working directory path is not a directory or does not exist");
        checkNotNull(outputHandler, "Output handler is required");
        this.workingDirectory = workingDirectory;
        this.outputHandler = outputHandler;
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

    public LabOutputHandler getOutputHandler() {
        return outputHandler;
    }

    public synchronized File getSubmissionDirectory() {
        checkState(submissionDirectory != null, "Submission directory not set");
        return submissionDirectory;
    }

    synchronized void setSubmissionDirectory(File submissionDirectory) {
        checkState(this.submissionDirectory == null, "Cannot overwrite submission directory");
        checkArgument(submissionDirectory.exists() && submissionDirectory.isDirectory(),
                "Submission directory does not exist or is not a directory");
        this.submissionDirectory = submissionDirectory;
    }

    synchronized void cleanup() {
        FileUtils.deleteQuietly(evaluationDirectory);
        evaluationDirectory = null;
    }
}
