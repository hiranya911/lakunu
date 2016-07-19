package org.lakunu.labs.ant.validators;

import org.lakunu.labs.ant.TaskContext;

public final class SuccessValidator extends Validator {

    public SuccessValidator(String label, double score) {
        super(label, score);
    }

    @Override
    public void validate(TaskContext context) {
        context.addScore(reportScore(context.isSuccess()));
    }
}
