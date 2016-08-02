package org.lakunu.labs.submit;

import org.lakunu.labs.Evaluation;

import java.io.File;
import java.io.IOException;

public abstract class Submission {

    /**
     * Creates and initializes the submission directory for the given EvaluationContext.
     * Returns a File representing the prepared submission directory.
     */
    public abstract File prepare(Evaluation.Context context) throws IOException;

}
