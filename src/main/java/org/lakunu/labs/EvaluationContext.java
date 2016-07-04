package org.lakunu.labs;

import org.apache.commons.io.FileUtils;
import org.lakunu.labs.submit.Submission;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class EvaluationContext {

    private final File workingDirectory;
    private final File submissionDirectory;
    private final LabOutputHandler outputHandler;

    private File evaluationDirectory;

    EvaluationContext(Submission submission, File workingDirectory, LabOutputHandler outputHandler) {
        checkNotNull(submission, "Submission is required");
        checkNotNull(workingDirectory, "Working directory is required");
        checkArgument(workingDirectory.isDirectory() && workingDirectory.exists(),
                "Working directory path is not a directory or does not exist");
        checkNotNull(outputHandler, "Output handler is required");
        this.workingDirectory = workingDirectory;
        this.outputHandler = outputHandler;
        this.submissionDirectory = submission.initDirectory(this);
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

    public File getSubmissionDirectory() {
        return submissionDirectory;
    }

    synchronized void cleanup() {
        FileUtils.deleteQuietly(evaluationDirectory);
        evaluationDirectory = null;
    }
}
