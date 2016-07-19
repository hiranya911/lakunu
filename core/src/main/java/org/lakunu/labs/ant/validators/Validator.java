package org.lakunu.labs.ant.validators;

import com.google.common.base.Strings;
import org.lakunu.labs.ant.TaskContext;

import static com.google.common.base.Preconditions.checkArgument;

public abstract class Validator {

    protected final String label;
    protected final double score;

    public Validator(String label, double score) {
        checkArgument(!Strings.isNullOrEmpty(label), "label not specified");
        this.label = "lakunu:" + label;
        this.score = score;
    }

    public abstract void validate(TaskContext context);

    protected final void reportFullScore(TaskContext context) {
        context.getProject().setUserProperty(label, score + " / " + score);
    }
}
