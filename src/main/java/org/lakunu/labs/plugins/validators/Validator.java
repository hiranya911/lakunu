package org.lakunu.labs.plugins.validators;

import com.google.common.base.Strings;
import org.lakunu.labs.Score;
import org.lakunu.labs.plugins.Plugin;

import static com.google.common.base.Preconditions.checkArgument;

public abstract class Validator {

    protected final String name;
    protected final double score;

    protected Validator(String name, double score) {
        checkArgument(!Strings.isNullOrEmpty(name), "name is required");
        this.name = name;
        this.score = score;
    }

    public abstract Score validate(Plugin.Context context);

    protected final Score reportScore(boolean condition) {
        if (score >= 0) {
            if (condition) {
                return Score.newPoints(name, score, score);
            } else {
                return Score.newPoints(name, 0, score);
            }
        } else {
            if (condition) {
                return Score.newPenalty(name, score);
            } else {
                return Score.newPenalty(name, 0);
            }
        }
    }

    protected final Score reportScore(double value) {
        if (score >= 0) {
            checkArgument(0 <= value && value <= score, "invalid score: %s", value);
            return Score.newPoints(name, value, score);
        } else {
            checkArgument(value <= 0 && value >= score, "invalid score: %s", value);
            return Score.newPenalty(name, value);
        }
    }

}
