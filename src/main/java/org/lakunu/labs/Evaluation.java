package org.lakunu.labs;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.FileUtils;
import org.lakunu.labs.submit.Submission;
import org.lakunu.labs.utils.LoggingOutputHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class Evaluation {

    private static final Logger logger = LoggerFactory.getLogger(Evaluation.class);

    private final Submission submission;
    private final Lab lab;
    private final File workingDirectory;
    private final LabOutputHandler outputHandler;
    private final boolean cleanUpAfterFinish;
    private final ImmutableMap<String,Object> properties;

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
        this.properties = builder.properties.build();
    }

    public void run(String finalPhase) throws IOException {
        Context context = new EvaluationContext(this);
        try {
            lab.execute(context, finalPhase);
        } finally {
            for (Score score : context.getScores()) {
                logger.info("Item score... {}", score.toString());
            }
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

    public static abstract class Context {

        private final Queue<Score> scores = new ConcurrentLinkedQueue<>();
        private final Map<String,Object> properties = new HashMap<>();

        public abstract File getEvaluationDirectory() throws IOException;
        public abstract LabOutputHandler getOutputHandler();
        public abstract File getSubmissionDirectory();
        public abstract File getResourcesDirectory();
        protected abstract void cleanup();

        public final void addScore(Score score) {
            scores.offer(score);
        }

        public ImmutableList<Score> getScores() {
            return ImmutableList.copyOf(scores);
        }

        public final ImmutableMap<String,Object> getProperties() {
            return ImmutableMap.copyOf(properties);
        }

        public final <T> T getProperty(String name, Class<T> clazz) {
            Object value = properties.get(name);
            if (value != null) {
                return clazz.cast(value);
            }
            return null;
        }

        public final void setProperty(String name, Object value) {
            properties.put(name, value);
        }
    }

    public static final class EvaluationContext extends Context {

        private final File workingDirectory;
        private final LabOutputHandler outputHandler;
        private final File submissionDirectory;
        private final File resourcesDirectory;
        private File evaluationDirectory;

        private EvaluationContext(Evaluation eval) throws IOException {
            this.workingDirectory = eval.workingDirectory;
            this.outputHandler = eval.outputHandler;
            this.resourcesDirectory = eval.lab.prepareResources(this);
            this.submissionDirectory = eval.submission.prepare(this);
            eval.properties.forEach(this::setProperty);
            checkNotNull(submissionDirectory, "Submission directory is required");
            checkArgument(submissionDirectory.isDirectory() && submissionDirectory.exists(),
                    "Submission directory path is not a directory or does not exist");
        }

        @Override
        public synchronized File getEvaluationDirectory() throws IOException {
            if (evaluationDirectory == null) {
                Path workingDirPath = Files.createTempDirectory(workingDirectory.toPath(), "lakunu");
                evaluationDirectory = workingDirPath.toFile();
                logger.info("Created evaluation directory: {}", evaluationDirectory.getAbsolutePath());
            }
            return evaluationDirectory;
        }

        @Override
        public LabOutputHandler getOutputHandler() {
            return outputHandler;
        }

        @Override
        public File getSubmissionDirectory() {
            return submissionDirectory;
        }

        @Override
        public File getResourcesDirectory() {
            return resourcesDirectory;
        }

        protected synchronized void cleanup() {
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
        private final ImmutableMap.Builder<String,Object> properties = ImmutableMap.builder();

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

        public Builder addProperty(String key, Object value) {
            this.properties.put(key, value);
            return this;
        }

        public Evaluation build() {
            return new Evaluation(this);
        }
    }
}
