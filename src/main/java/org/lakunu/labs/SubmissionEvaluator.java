package org.lakunu.labs;

import org.lakunu.labs.submit.Submission;
import org.lakunu.labs.utils.LoggingOutputHandler;

import java.io.File;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;

public final class SubmissionEvaluator {

    private final Submission submission;
    private final Lab lab;

    public SubmissionEvaluator(Submission submission, Lab lab) {
        checkNotNull(submission, "Submission is required");
        checkNotNull(lab, "Lab is required");
        this.submission = submission;
        this.lab = lab;
    }

    public void evaluate(File workingDirectory, String finalPhase) throws IOException {
        EvaluationContext context = new EvaluationContext(workingDirectory,
                new LoggingOutputHandler());
        try {
            File submissionDirectory = submission.prepare(context);
            context.setSubmissionDirectory(submissionDirectory);
            lab.execute(context, finalPhase);
        } finally {
            context.cleanup();
        }
    }

    public void evaluate(File workingDirectory) throws IOException {
        evaluate(workingDirectory, null);
    }

}
