package org.lakunu.labs.ant.validators;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.SystemUtils;
import org.lakunu.labs.Score;
import org.lakunu.labs.ant.TaskContext;
import org.lakunu.labs.ant.ValidatorArg;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;

public final class AntTestValidator extends LakunuValidator {

    private static final String ANT_TEST_RESULTS = "_ANT_TEST_RESULTS_";

    private final double scorePerTest;
    private final ImmutableList<String> testSuites;

    public AntTestValidator(String label, double score, ImmutableList<ValidatorArg> args) {
        super(label, score, args);
        this.scorePerTest = Double.parseDouble(getRequiredArgument("scorePerTest"));
        if (score < 0) {
            checkArgument(scorePerTest <= 0,
                    "scorePerTest must be 0 or have the same sign as score");
        } else if (score > 0) {
            checkArgument(scorePerTest >= 0,
                    "scorePerTest must be 0 or have the same sign as score");
        } else {
            checkArgument(scorePerTest == 0D, "scorePerTest must be 0 when score is 0");
        }
        String suites = getOptionalArgument("suites", "");
        this.testSuites = ImmutableList.copyOf(Arrays.asList(suites.split(",")));
    }

    private synchronized TestResultMap getTestResults(TaskContext context) {
        TestResultMap results = context.getProperty(ANT_TEST_RESULTS, TestResultMap.class);
        if (results == null) {
            Map<String,TestResult> resultsMap = new HashMap<>();
            String output = context.getOutput();
            String header = null;
            for (String line : output.split(SystemUtils.LINE_SEPARATOR)) {
                line = line.trim();
                if (header == null && line.startsWith("Testsuite: ")) {
                    header = line.split(" ")[1];
                } else if (header != null) {
                    if (line.startsWith("Tests run: ")) {
                        resultsMap.put(header, new TestResult(line));
                    } else {
                        throw new IllegalArgumentException("unexpected line: " + line);
                    }
                    header = null;
                }
            }
            results = new TestResultMap(resultsMap);
            context.setProperty(ANT_TEST_RESULTS, results);
        }
        return results;
    }

    @Override
    public Score validate(TaskContext context) {
        TestResultMap results = getTestResults(context);
        int passed = results.suites()
                .filter(k -> testSuites.isEmpty() || testSuites.contains(k))
                .mapToInt(k -> results.get(k).passed)
                .sum();
        if (scorePerTest == 0D) {
            int total = results.suites()
                    .filter(k -> testSuites.isEmpty() || testSuites.contains(k))
                    .mapToInt(k -> results.get(k).total)
                    .sum();
            return reportScore(passed == total);
        } else {
            return reportScoreWithLimit(scorePerTest * passed);
        }
    }

    public static final class TestResultMap {
        private final ImmutableMap<String,TestResult> testResults;

        private TestResultMap(Map<String, TestResult> testResults) {
            this.testResults = ImmutableMap.copyOf(testResults);
        }

        public TestResult get(String suite) {
            return testResults.get(suite);
        }

        public Stream<String> suites() {
            return testResults.keySet().stream();
        }
    }

    private static final class TestResult {
        private final int passed;
        private final int total;

        private TestResult(String line) {
            String[] segments = line.replaceAll(",", "").split(" ");
            this.total = Integer.parseInt(segments[2]);
            int failed = Integer.parseInt(segments[4]) + Integer.parseInt(segments[6]);
            this.passed = total - failed;
        }
    }

}
