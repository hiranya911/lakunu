package org.lakunu.labs.submit;

import org.lakunu.labs.EvaluationContext;

import java.io.File;

import static com.google.common.base.Preconditions.checkState;

public abstract class Submission {

    public abstract File getSubmissionDirectory(EvaluationContext context);

}
