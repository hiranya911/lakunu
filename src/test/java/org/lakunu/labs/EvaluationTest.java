package org.lakunu.labs;

import com.google.common.collect.ImmutableList;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.lakunu.labs.plugins.CustomTestPlugin;
import org.lakunu.labs.submit.Submission;
import org.lakunu.labs.submit.TestSubmission;
import org.lakunu.labs.utils.LabUtils;

import static org.hamcrest.CoreMatchers.is;

public class EvaluationTest {

    @Test
    public void testSinglePluginEvaluation() throws Exception {
        Lab lab = DefaultLabBuilder.newBuilder()
                .setName("test")
                .addPlugin(DefaultLabBuilder.BUILD_PHASE, CustomTestPlugin.newInstance(context -> {
                    context.getOutputHandler().info("Hello world");
                    return true;
                }))
                .build();
        TestOutputHandler outputHandler = new TestOutputHandler();
        Submission submission = new TestSubmission();
        Evaluation eval = Evaluation.newBuilder()
                .setLab(lab)
                .setSubmission(submission)
                .setOutputHandler(outputHandler)
                .setWorkingDirectory(FileUtils.getTempDirectory())
                .setOutputHandler(outputHandler)
                .build();
        eval.run();

        ImmutableList<String> entries = outputHandler.entries().stream()
                .map(e -> e.line)
                .collect(LabUtils.immutableList());
        Assert.assertTrue(entries.size() > 0);
        ImmutableList<String> expected = expected("\n" +
                "------------------------------------------------------------------------\n" +
                "Starting build phase\n" +
                "------------------------------------------------------------------------\n" +
                "\n" +
                "Hello world\n" +
                "------------------------------------------------------------------------");
        Assert.assertThat(entries, is(expected));
    }

    @Test
    public void testMultiplePluginEvaluation() throws Exception {
        Lab lab = DefaultLabBuilder.newBuilder()
                .setName("test")
                .addPlugin(DefaultLabBuilder.BUILD_PHASE, CustomTestPlugin.newInstance(context -> {
                    context.getOutputHandler().info("Hello world");
                    return true;
                }))
                .addPlugin(DefaultLabBuilder.RUN_PHASE, CustomTestPlugin.newInstance(context -> {
                    context.getOutputHandler().info("Bye world");
                    return true;
                }))
                .build();
        TestOutputHandler outputHandler = new TestOutputHandler();
        Submission submission = new TestSubmission();
        Evaluation eval = Evaluation.newBuilder()
                .setLab(lab)
                .setSubmission(submission)
                .setOutputHandler(outputHandler)
                .setWorkingDirectory(FileUtils.getTempDirectory())
                .setOutputHandler(outputHandler)
                .build();
        eval.run();

        ImmutableList<String> entries = outputHandler.entries().stream()
                .map(e -> e.line)
                .collect(LabUtils.immutableList());
        Assert.assertTrue(entries.size() > 0);
        ImmutableList<String> expected = expected("\n" +
                        "------------------------------------------------------------------------\n" +
                        "Starting build phase\n" +
                        "------------------------------------------------------------------------\n" +
                        "\n" +
                        "Hello world\n" +
                        "\n" +
                        "------------------------------------------------------------------------\n" +
                        "Starting run phase\n" +
                        "------------------------------------------------------------------------\n" +
                        "\n" +
                        "Bye world\n" +
                        "------------------------------------------------------------------------");
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
