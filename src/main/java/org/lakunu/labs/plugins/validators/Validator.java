package org.lakunu.labs.plugins.validators;

import org.lakunu.labs.Score;

import static com.google.common.base.Preconditions.checkArgument;

public abstract class Validator<T> {

    private final int maxScore;

    protected Validator(int maxScore) {
        checkArgument(maxScore >= 0, "maxScore must not be negative");
        this.maxScore = maxScore;
    }

    public abstract Score validate(T input);

}
