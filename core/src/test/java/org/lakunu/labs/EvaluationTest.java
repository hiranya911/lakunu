package org.lakunu.labs;

import com.google.common.collect.ImmutableList;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.lakunu.labs.submit.Submission;
import org.lakunu.labs.submit.TestSubmission;
import org.lakunu.labs.utils.LabUtils;

import static org.hamcrest.CoreMatchers.is;

public class EvaluationTest {

    @Test
    public void testSingleStepEvaluation() throws Exception {
        TestEvaluationPlan plan = new TestEvaluationPlan(ImmutableList.of(
                c -> c.getOutputHandler().info("Step 1")
        ));
        Lab lab = Lab.newBuilder()
                .setName("test")
                .setEvaluationPlan(plan)
                .build();
        TestOutputHandler outputHandler = new TestOutputHandler();
        Submission submission = new TestSubmission();
        Evaluation eval = Evaluation.newBuilder()
                .setLab(lab)
                .setSubmission(submission)
                .setOutputHandler(outputHandler)
                .setWorkingDirectory(FileUtils.getTempDirectory())
                .setOutputHandler(outputHandler)
                .setOutputSummary(false)
                .build();
        eval.run();

        ImmutableList<String> entries = outputHandler.entries().stream()
                .map(e -> e.line)
                .collect(LabUtils.immutableList());
        Assert.assertTrue(entries.size() > 0);
        ImmutableList<String> expected = expected("Step 1\n" +
                "------------------------------------------------------------------------\n");
        Assert.assertThat(entries, is(expected));
    }

    @Test
    public void testMultiplePluginEvaluation() throws Exception {
        TestEvaluationPlan plan = new TestEvaluationPlan(ImmutableList.of(
                c -> c.getOutputHandler().info("Step 1"),
                c -> c.getOutputHandler().info("Step 2")
        ));
        Lab lab = Lab.newBuilder()
                .setName("test")
                .setEvaluationPlan(plan)
                .build();
        TestOutputHandler outputHandler = new TestOutputHandler();
        Submission submission = new TestSubmission();
        Evaluation eval = Evaluation.newBuilder()
                .setLab(lab)
                .setSubmission(submission)
                .setOutputHandler(outputHandler)
                .setWorkingDirectory(FileUtils.getTempDirectory())
                .setOutputHandler(outputHandler)
                .setOutputSummary(false)
                .build();
        eval.run();

        ImmutableList<String> entries = outputHandler.entries().stream()
                .map(e -> e.line)
                .collect(LabUtils.immutableList());
        Assert.assertTrue(entries.size() > 0);
        ImmutableList<String> expected = expected("Step 1\n" +
                "Step 2\n" +
                "------------------------------------------------------------------------\n");
        Assert.assertThat(entries, is(expected));
    }

    private ImmutableList<String> expected(String output) {
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        for (String line : output.split("\n")) {
            builder.add(line);
        }
        return builder.build();
    }
}
