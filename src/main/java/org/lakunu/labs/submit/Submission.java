package org.lakunu.labs.submit;

import org.lakunu.labs.EvaluationContext;

import java.io.File;

public abstract class Submission {

    /**
     * Create and initialize the submission directory for the given EvaluationContext.
     * Returns a File representing the initialized submission directory.
     */
    public abstract File initSubmissionDirectory(EvaluationContext context);

}
