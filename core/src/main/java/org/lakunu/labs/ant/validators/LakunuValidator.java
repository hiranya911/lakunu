package org.lakunu.labs.ant.validators;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import org.lakunu.labs.Score;
import org.lakunu.labs.ant.TaskContext;
import org.lakunu.labs.ant.ValidatorArg;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public abstract class LakunuValidator {

    private final String label;
    private final double score;
    private final ImmutableList<ValidatorArg> args;

    public LakunuValidator(String label, double score, ImmutableList<ValidatorArg> args) {
        checkArgument(!Strings.isNullOrEmpty(label), "label not specified");
        checkNotNull(args, "Args must not be null");
        this.label = label;
        this.score = score;
        this.args = args;
    }

    public abstract Score validate(TaskContext context);

    public final Score zero() {
        return reportScore(false);
    }

    protected final String getRequiredArgument(String name) {
        Optional<ValidatorArg> result = args.stream()
                .filter(arg -> arg.getName().equals(name)).findFirst();
        checkArgument(result.isPresent(), "Argument %s is required", name);
        String value = result.get().getValue();
        checkArgument(!Strings.isNullOrEmpty(value), "Argument %s is required", name);
        return value;
    }

    protected final String getOptionalArgument(String name, String def) {
        Optional<ValidatorArg> result = args.stream()
                .filter(arg -> arg.getName().equals(name)).findFirst();
        if (result.isPresent()) {
            return result.get().getValue();
        } else {
            return def;
        }
    }

    protected final Score reportScore(boolean condition) {
        if (score >= 0) {
            if (condition) {
                return Score.newPoints(label, score, score);
            } else {
                return Score.newPoints(label, 0, score);
            }
        } else {
            if (condition) {
                return Score.newPenalty(label, score);
            } else {
                return Score.newPenalty(label, 0);
            }
        }
    }

    protected final Score reportScore(double value) {
        if (score >= 0) {
            checkArgument(0 <= value && value <= score, "invalid score: %s", value);
            return Score.newPoints(label, value, score);
        } else {
            checkArgument(value <= 0 && value >= score, "invalid score: %s", value);
            return Score.newPenalty(label, value);
        }
    }

    protected final Score reportScoreWithLimit(double value) {
        if (score >= 0) {
            return reportScore(Math.min(score, value));
        } else {
            return reportScore(Math.max(score, value));
        }
    }
}
