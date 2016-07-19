package org.lakunu.labs.ant.validators;

import com.google.common.collect.ImmutableList;
import org.lakunu.labs.Score;
import org.lakunu.labs.ant.TaskContext;
import org.lakunu.labs.ant.ValidatorArg;

public final class PropertyValidator extends LakunuValidator {

    private final String property;
    private final String value;
    private final boolean checkNotExists;

    public PropertyValidator(String label, double score, ImmutableList<ValidatorArg> args) {
        super(label, score, args);
        this.property = getRequiredArgument("property");
        this.value = getOptionalArgument("value", "true");
        this.checkNotExists = Boolean.parseBoolean(getOptionalArgument("checkNotExists", "false"));
    }

    @Override
    public Score validate(TaskContext context) {
        String value = context.getProject().getProperty(property);
        if (checkNotExists) {
            return reportScore(value == null);
        } else {
            return reportScore(value != null && value.equals(this.value));
        }
    }
}
