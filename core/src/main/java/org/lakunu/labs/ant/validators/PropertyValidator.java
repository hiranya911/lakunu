package org.lakunu.labs.ant.validators;

import org.lakunu.labs.ant.TaskContext;
import org.lakunu.labs.ant.ValidatorArg;

import java.util.List;
import java.util.Optional;

public final class PropertyValidator extends Validator {

    private final String property;
    private final String value;

    public PropertyValidator(String label, double score, List<ValidatorArg> args) {
        super(label, score);
        this.property = args.stream().filter(a -> a.getName().equals("property"))
                .findFirst().get().getValue();
        Optional<ValidatorArg> value = args.stream().filter(a -> a.getName().equals("value"))
                .findFirst();
        this.value = value.isPresent() ? value.get().getValue() : "true";
    }

    @Override
    public void validate(TaskContext context) {
        String value = context.getProject().getProperty(property);
        context.addScore(reportScore(value != null && value.equals(this.value)));
    }
}
