package org.lakunu.labs.ant.validators;

import com.google.common.collect.ImmutableList;
import org.lakunu.labs.Score;
import org.lakunu.labs.ant.TaskContext;
import org.lakunu.labs.ant.ValidatorArg;

import java.util.regex.Pattern;

public final class OutputMatchValidator extends LakunuValidator {

    private final Pattern pattern;
    private final boolean error;

    public OutputMatchValidator(String label, double score, ImmutableList<ValidatorArg> args) {
        super(label, score, args);
        this.pattern = Pattern.compile(getRequiredArgument("pattern"));
        this.error = Boolean.parseBoolean(getOptionalArgument("error", "false"));
    }

    @Override
    public Score validate(TaskContext context) {
        String output;
        if (error) {
            output = context.getError();
        } else {
            output = context.getOutput();
        }
        return reportScore(output != null && pattern.matcher(output).matches());
    }
}
