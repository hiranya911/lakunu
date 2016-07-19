package org.lakunu.labs.ant.validators;

import com.google.common.collect.ImmutableList;
import org.lakunu.labs.Score;
import org.lakunu.labs.ant.TaskContext;
import org.lakunu.labs.ant.ValidatorArg;

public final class SuccessValidator extends LakunuValidator {

    public SuccessValidator(String label, double score, ImmutableList<ValidatorArg> args) {
        super(label, score, args);
    }

    @Override
    public Score validate(TaskContext context) {
        return reportScore(context.isSuccess());
    }
}
