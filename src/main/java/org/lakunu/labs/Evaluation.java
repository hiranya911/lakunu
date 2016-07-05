package org.lakunu.labs;

import org.apache.commons.io.FileUtils;
import org.lakunu.labs.submit.Submission;
import org.lakunu.labs.utils.LoggingOutputHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class Evaluation {

    private static final Logger logger = LoggerFactory.getLogger(Evaluation.class);

    private final Submission submission;
    private final Lab lab;
    private final File workingDirectory;
    private final LabOutputHandler outputHandler;
    private final boolean cleanUpAfterFinish;

    private Evaluation(Builder builder) {
        checkNotNull(builder.submission, "Submission is required");
        checkNotNull(builder.lab, "Lab is required");
        checkNotNull(builder.workingDirectory, "Working directory is required");
        checkArgument(builder.workingDirectory.isDirectory() && builder.workingDirectory.exists(),
                "Working directory path is not a directory or does not exist");
        checkNotNull(builder.outputHandler, "Output handler is required");
        this.submission = builder.submission;
        this.lab = builder.lab;
        this.workingDirectory = builder.workingDirectory;
        this.cleanUpAfterFinish = builder.cleanUpAfterFinish;
        this.outputHandler = builder.outputHandler;
    }

    public void run(String finalPhase) throws IOException {
        Context context = new Context(this);
        try {
            lab.execute(context, finalPhase);
        } finally {
            if (cleanUpAfterFinish) {
                context.cleanup();
            }
        }
    }

    public void run() throws IOException {
        run(null);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Context newTestContext() {
        return new Context();
    }

    public static final class Context {

        private final File workingDirectory;
        private final LabOutputHandler outputHandler;
        private final File submissionDirectory;
        private final File resourcesDirectory;
        private File evaluationDirectory;

        private Context() {
            this.workingDirectory = null;
            this.outputHandler = null;
            this.submissionDirectory = null;
            this.resourcesDirectory = null;
        }

        private Context(Evaluation eval) throws IOException {
            this.workingDirectory = eval.workingDirectory;
            this.outputHandler = eval.outputHandler;
            this.resourcesDirectory = eval.lab.prepareResources(this);
            this.submissionDirectory = eval.submission.prepare(this);
            checkNotNull(submissionDirectory, "Submission directory is required");
            checkArgument(submissionDirectory.isDirectory() && submissionDirectory.exists(),
                    "Submission directory path is not a directory or does not exist");
        }

        public synchronized File getEvaluationDirectory() throws IOException {
            if (evaluationDirectory == null) {
                Path workingDirPath = Files.createTempDirectory(workingDirectory.toPath(), "lakunu");
                evaluationDirectory = workingDirPath.toFile();
                logger.info("Created evaluation directory: {}", evaluationDirectory.getAbsolutePath());
            }
            return evaluationDirectory;
        }

        public LabOutputHandler getOutputHandler() {
            return outputHandler;
        }

        public File getSubmissionDirectory() {
            return submissionDirectory;
        }

        private synchronized void cleanup() {
            logger.info("Cleaning up...");
            FileUtils.deleteQuietly(evaluationDirectory);
            evaluationDirectory = null;
        }

    }

    public static class Builder {

        private Submission submission;
        private Lab lab;
        private File workingDirectory;
        private LabOutputHandler outputHandler = new LoggingOutputHandler();
        private boolean cleanUpAfterFinish = true;

        private Builder() {
        }

        public Builder setSubmission(Submission submission) {
            this.submission = submission;
            return this;
        }

        public Builder setLab(Lab lab) {
            this.lab = lab;
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

        public Builder setCleanUpAfterFinish(boolean cleanUpAfterFinish) {
            this.cleanUpAfterFinish = cleanUpAfterFinish;
            return this;
        }

        public Evaluation build() {
            return new Evaluation(this);
        }
    }
}
