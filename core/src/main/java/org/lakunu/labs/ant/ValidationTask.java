package org.lakunu.labs.ant;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import org.lakunu.labs.Score;
import org.lakunu.labs.ant.validators.OutputMatchValidator;
import org.lakunu.labs.ant.validators.PropertyValidator;
import org.lakunu.labs.ant.validators.SuccessValidator;
import org.lakunu.labs.ant.validators.LakunuValidator;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

public abstract class ValidationTask {

    protected String label;
    protected double score;
    protected String type;
    protected final List<ValidatorArg> args = new ArrayList<>();

    private ValidationTask() {
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void add(ValidatorArg arg) {
        this.args.add(arg);
    }

    public void execute(TaskContext context) {
        LakunuValidator validator = buildValidator();
        Score score = validator.validate(context);
        context.addScore(score);
    }

    public Score getRubric() {
        LakunuValidator validator = buildValidator();
        return validator.zero();
    }

    private LakunuValidator buildValidator() {
        checkArgument(!Strings.isNullOrEmpty(type), "validator type not specified");
        ImmutableList<ValidatorArg> args = ImmutableList.copyOf(this.args);
        if (type.equals("success")) {
            return new SuccessValidator(label, score, args);
        } else if (type.equals("property")) {
            return new PropertyValidator(label, score, args);
        } else if (type.equals("output-match")) {
            return new OutputMatchValidator(label, score, args);
        }
        throw new IllegalArgumentException("Unknown validator: " + type);
    }

    public static final class PreValidationTask extends ValidationTask {
    }

    public static final class PostValidationTask extends ValidationTask {
    }
}
