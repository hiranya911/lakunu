package org.lakunu.labs;

import com.google.common.collect.ImmutableList;
import org.apache.commons.io.FileUtils;
import org.lakunu.labs.submit.Submission;
import org.lakunu.labs.utils.LabUtils;
import org.lakunu.labs.utils.LoggingOutputHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class Evaluation {

    private static final Logger logger = LoggerFactory.getLogger(Evaluation.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    private final Submission submission;
    private final Lab lab;
    private final File workingDirectory;
    private final LabOutputHandler outputHandler;
    private final boolean outputSummary;
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
        this.outputSummary = builder.outputSummary;
        this.cleanUpAfterFinish = builder.cleanUpAfterFinish;
        this.outputHandler = builder.outputHandler;
    }

    public void run(String finalPhase) throws IOException {
        long start = System.nanoTime();
        Context context = new EvaluationContext(this);
        boolean exception = false;
        try {
            lab.execute(context, finalPhase);
        } catch (Exception e) {
            exception = true;
            throw e;
        } finally {
            if (outputSummary) {
                dumpSummary(start, exception, context);
            }
            LabUtils.outputBreak(outputHandler);
            if (cleanUpAfterFinish) {
                context.cleanup();
            }
        }
    }

    private void dumpSummary(long start, boolean exception, Context context) {
        Runtime runtime = Runtime.getRuntime();
        long total = runtime.totalMemory();
        long used = total - runtime.freeMemory();
        long end = System.nanoTime();
        LabOutputHandler outputHandler = context.getOutputHandler();
        if (exception) {
            LabUtils.outputTitle("Evaluation terminated due to exception", outputHandler);
        } else {
            LabUtils.outputTitle("Evaluation complete", outputHandler);
        }

        ImmutableList<Score> rubric = lab.getRubric();
        if (!rubric.isEmpty()) {
            int longest = rubric.stream().mapToInt(s -> s.getName().length()).max().getAsInt();
            ImmutableList<Score> graded = context.getScores();
            List<Score> finalGrades = new ArrayList<>();
            for (int i = 0; i < rubric.size(); i++) {
                Score score;
                if (i < graded.size()) {
                    score = graded.get(i);
                    outputHandler.info(String.format("Score: %" + (longest + 1) + "s %20s",
                            score.getName(), score.toString()));
                } else {
                    score = rubric.get(i);
                    outputHandler.info(String.format("Score: %" + (longest + 1) + "s %20s (Skipped)",
                            score.getName(), score.toString()));
                }
                finalGrades.add(score);
            }
            outputHandler.info("");
            outputHandler.info("Total score: " + Score.total(finalGrades).toString());
            outputHandler.info("");
            outputHandler.info("");
        }

        outputHandler.info(String.format("Total time: %.3f s", (end - start)/1e9));
        outputHandler.info(String.format("Finished at: %s", DATE_FORMAT.format(
                new Date(System.currentTimeMillis()))));
        outputHandler.info(String.format("Final memory: %dM/%dM",
                used/FileUtils.ONE_MB, total/FileUtils.ONE_MB));
    }

    public void run() throws IOException {
        run(null);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static abstract class Context {

        private final Queue<Score> scores = new ConcurrentLinkedQueue<>();

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
            checkNotNull(submissionDirectory, "Submission directory is required");
            checkArgument(submissionDirectory.isDirectory() && submissionDirectory.exists(),
                    "Submission directory path is not a directory or does not exist");
        }

        @Override
        public synchronized File getEvaluationDirectory() throws IOException {
            if (evaluationDirectory == null) {
                Path evaluationDirPath = Files.createTempDirectory(workingDirectory.toPath(), "lakunu");
                evaluationDirectory = evaluationDirPath.toFile();
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
        private boolean outputSummary = true;

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

        public Builder setOutputSummary(boolean outputSummary) {
            this.outputSummary = outputSummary;
            return this;
        }

        public Evaluation build() {
            return new Evaluation(this);
        }
    }
}
