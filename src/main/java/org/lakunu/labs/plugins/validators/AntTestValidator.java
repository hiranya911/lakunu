package org.lakunu.labs.plugins.validators;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.SystemUtils;
import org.lakunu.labs.Score;
import org.lakunu.labs.plugins.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

public final class AntTestValidator extends Validator {

    private final double scorePerTest;
    private final ImmutableList<String> testSuites;

    public AntTestValidator(Builder builder) {
        super(builder);
        checkArgument(builder.score < 0 == builder.scorePerTest < 0,
                "scorePerTest must have same sign as score");
        this.scorePerTest = builder.scorePerTest;
        this.testSuites = ImmutableList.copyOf(builder.testSuites);
    }

    @Override
    public Score validate(Plugin.Context context) {
        String output = context.getOutput();
        Map<String,TestResult> results = new HashMap<>();
        String header = null;
        for (String line : output.split(SystemUtils.LINE_SEPARATOR)) {
            line = line.trim();
            if (header == null && line.startsWith("[junit] Testsuite: ")) {
                header = line.split(" ")[2];
            } else if (header != null) {
                if (line.startsWith("[junit] Tests run: ")) {
                    results.put(header, new TestResult(line));
                } else {
                    throw new IllegalArgumentException("unexpected line: " + line);
                }
                header = null;
            }
        }

        int passed = results.keySet().stream()
                .filter(k -> testSuites.isEmpty() || testSuites.contains(k))
                .mapToInt(k -> results.get(k).passed)
                .sum();
        if (score >= 0) {
            return reportScore(Math.min(score, scorePerTest * passed));
        } else {
            return reportScore(Math.max(score, scorePerTest * passed));
        }
    }

    private static final class TestResult {
        private final int passed;
        private final int total;

        private TestResult(String line) {
            String[] segments = line.replaceAll(",", "").split(" ");
            this.total = Integer.parseInt(segments[3]);
            int failed = Integer.parseInt(segments[5]) + Integer.parseInt(segments[7]);
            this.passed = total - failed;
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder extends Validator.Builder<AntTestValidator,Builder> {

        private double scorePerTest;
        private List<String> testSuites = new ArrayList<>();

        private Builder() {
        }

        public Builder setScorePerTest(double scorePerTest) {
            this.scorePerTest = scorePerTest;
            return this;
        }

        public Builder addTestSuite(String testsuite) {
            this.testSuites.add(testsuite);
            return this;
        }

        @Override
        protected Builder getThisObj() {
            return this;
        }

        @Override
        public AntTestValidator build() {
            return new AntTestValidator(this);
        }
    }
}
