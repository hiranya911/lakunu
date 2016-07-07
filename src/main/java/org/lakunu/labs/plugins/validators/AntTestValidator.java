package org.lakunu.labs.plugins.validators;

import org.apache.commons.lang3.SystemUtils;
import org.lakunu.labs.Score;
import org.lakunu.labs.plugins.Plugin;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

public final class AntTestValidator extends Validator {

    private final double scorePerTest;

    public AntTestValidator(String name, double limit, double scorePerTest) {
        super(name, limit);
        checkArgument(limit < 0 == scorePerTest < 0, "scorePerTest must have same sign as limit");
        this.scorePerTest = scorePerTest;
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

        int passed = results.values().stream().mapToInt(r -> r.passed).sum();
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
}
