package org.lakunu.labs.submit;

import org.lakunu.labs.EvaluationContext;
import org.lakunu.labs.Lab;
import org.lakunu.labs.utils.LoggingOutputHandler;

import java.io.File;

public abstract class Submission {

    /**
     * Create and initialize the submission directory for the given EvaluationContext.
     * Returns a File representing the initialized submission directory.
     */
    public abstract File initSubmissionDirectory(EvaluationContext context);

    public final void evaluate(Lab lab, String finalPhase) {
        EvaluationContext context = EvaluationContext.newBuilder()
                .setSubmission(this)
                .setWorkingDirectory(lab.getWorkingDirectory())
                .setOutputHandler(new LoggingOutputHandler())
                .build();
        try {
            lab.execute(context, finalPhase);
        } finally {
            context.cleanup();
        }
    }

    public void evaluate(Lab lab) {
        evaluate(lab, null);
    }

}
