package org.lakunu.labs;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.lakunu.labs.plugins.CustomTestPlugin;
import org.lakunu.labs.submit.Submission;
import org.lakunu.labs.submit.TestSubmission;
import org.lakunu.labs.utils.LabUtils;

import java.io.File;
import java.io.IOException;

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
    public void testEvaluationProperties() throws Exception {
        Lab lab = DefaultLabBuilder.newBuilder()
                .setName("test")
                .addPlugin(DefaultLabBuilder.BUILD_PHASE, CustomTestPlugin.newInstance(context -> {
                    context.getOutputHandler().info(context.replaceProperties("Hello world ${foo}"));
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
                .addProperty("foo", "abc")
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
                "Hello world abc\n" +
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

    public static final class TestContext extends Evaluation.Context {

        private final File evaluationDirectory;
        private final LabOutputHandler outputHandler;
        private final File submissionDirectory;
        private final File resourceDirectory;

        private TestContext(TestContextBuilder builder) {
            this.evaluationDirectory = builder.evaluationDirectory;
            this.outputHandler = builder.outputHandler;
            this.submissionDirectory = builder.submissionDirectory;
            this.resourceDirectory = builder.resourceDirectory;
            builder.properties.build().forEach(this::setProperty);
        }

        @Override
        public File getEvaluationDirectory() throws IOException {
            return evaluationDirectory;
        }

        @Override
        public LabOutputHandler getOutputHandler() {
            return outputHandler;
        }

        @Override
        public File getSubmissionDirectory() {
            return submissionDirectory;
        }

        @Override
        public File getResourcesDirectory() {
            return resourceDirectory;
        }

        @Override
        protected void cleanup() {

        }
    }

    public static class TestContextBuilder {
        private File evaluationDirectory;
        private LabOutputHandler outputHandler;
        private File submissionDirectory;
        private File resourceDirectory;
        private final ImmutableMap.Builder<String,Object> properties = ImmutableMap.builder();

        private TestContextBuilder() {
        }

        public TestContextBuilder setEvaluationDirectory(File evaluationDirectory) {
            this.evaluationDirectory = evaluationDirectory;
            return this;
        }

        public TestContextBuilder setOutputHandler(LabOutputHandler outputHandler) {
            this.outputHandler = outputHandler;
            return this;
        }

        public TestContextBuilder setSubmissionDirectory(File submissionDirectory) {
            this.submissionDirectory = submissionDirectory;
            return this;
        }

        public TestContextBuilder setResourceDirectory(File resourceDirectory) {
            this.resourceDirectory = resourceDirectory;
            return this;
        }

        public TestContextBuilder addProperty(String key, Object value) {
            this.properties.put(key, value);
            return this;
        }

        public TestContext build() {
            return new TestContext(this);
        }
    }

    public static TestContextBuilder testContextBuilder() {
        return new TestContextBuilder();
    }


}
