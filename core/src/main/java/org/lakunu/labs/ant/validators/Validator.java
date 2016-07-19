package org.lakunu.labs.ant.validators;

import com.google.common.base.Strings;
import org.lakunu.labs.Score;
import org.lakunu.labs.ant.TaskContext;

import static com.google.common.base.Preconditions.checkArgument;

public abstract class Validator {

    protected final String label;
    protected final double score;

    public Validator(String label, double score) {
        checkArgument(!Strings.isNullOrEmpty(label), "label not specified");
        this.label = label;
        this.score = score;
    }

    public abstract void validate(TaskContext context);

    public final Score zero() {
        return reportScore(false);
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
