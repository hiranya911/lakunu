package org.lakunu.labs;

import com.google.common.base.Strings;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class Score {

    private final String name;
    private final double value;
    private final double limit;

    private Score(String name, double value, double limit) {
        checkArgument(!Strings.isNullOrEmpty(name), "Name is required");
        this.name = name;
        this.value = value;
        this.limit = limit;
    }

    public String getName() {
        return name;
    }

    public double getValue() {
        return value;
    }

    public double getLimit() {
        return limit;
    }

    public Score add(String name, Score score) {
        checkNotNull(score, "argument must not be null");
        return new Score(name, this.value + score.value, this.limit + score.limit);
    }

    public static Score total(Collection<Score> scores) {
        return scores.stream().reduce(newPoints("total", 0D, 0D), (s1,s2) -> s1.add("total", s2));
    }

    public static Score newPoints(String name, double value, double limit) {
        checkArgument(value >= 0, "value must not be negative");
        checkArgument(limit >= 0, "limit must not be negative");
        checkArgument(value <= limit, "value must not exceed limit");
        return new Score(name, value, limit);
    }

    public static Score newPenalty(String name, double value) {
        checkArgument(value <= 0, "value must not be positive");
        return new Score(name, value, 0D);
    }

    @Override
    public String toString() {
        String str = "" + value;
        if (limit >= 0) {
            str += " / " + limit;
        }
        return str;
    }
}
