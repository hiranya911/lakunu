package org.lakunu.labs.plugins.utils;

import com.google.common.collect.ImmutableList;
import junit.framework.Assert;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.lakunu.labs.Evaluation;
import org.lakunu.labs.EvaluationTest;
import org.lakunu.labs.TestOutputHandler;
import org.lakunu.labs.plugins.PluginTest;

import java.io.IOException;

public class SystemCommandTest {

    @Before
    public void unixOnly() {
        Assume.assumeTrue(SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_UNIX);
    }

    @Test
    public void testSimpleCommand() throws Exception {
        TestOutputHandler outputHandler = new TestOutputHandler();
        Evaluation.Context evalContext = EvaluationTest.testContextBuilder()
                .setOutputHandler(outputHandler)
                .setSubmissionDirectory(FileUtils.getTempDirectory())
                .build();
        SystemCommand cmd = SystemCommand.newBuilder()
                .setCommand("date")
                .build();
        SystemCommand.Output output = cmd.run(PluginTest.pluginContext(evalContext));
        Assert.assertEquals(0, output.getStatus());
        ImmutableList<TestOutputHandler.LogEntry> entries = outputHandler.entries();
        Assert.assertEquals(1, entries.size());
        Assert.assertEquals(TestOutputHandler.Level.INFO, entries.get(0).level);

        // check rerun
        output = cmd.run(PluginTest.pluginContext(evalContext));
        Assert.assertEquals(0, output.getStatus());
        entries = outputHandler.entries();
        Assert.assertEquals(2, entries.size());
        Assert.assertEquals(TestOutputHandler.Level.INFO, entries.get(1).level);
    }

    @Test
    public void testWorkingDirectory() throws Exception {
        TestOutputHandler outputHandler = new TestOutputHandler();
        Evaluation.Context evalContext = EvaluationTest.testContextBuilder()
                .setOutputHandler(outputHandler)
                .setSubmissionDirectory(FileUtils.getUserDirectory())
                .build();
        SystemCommand cmd = SystemCommand.newBuilder()
                .setCommand("pwd")
                .build();
        SystemCommand.Output output = cmd.run(PluginTest.pluginContext(evalContext));
        Assert.assertEquals(0, output.getStatus());
        Assert.assertEquals(1, outputHandler.entries().size());
        String entry = outputHandler.entries().get(0).line;
        Assert.assertEquals(FileUtils.getUserDirectory().getAbsolutePath(), entry);
    }

    @Test(expected = IOException.class)
    public void testNonExistentCommand() throws Exception {
        TestOutputHandler outputHandler = new TestOutputHandler();
        Evaluation.Context evalContext = EvaluationTest.testContextBuilder()
                .setOutputHandler(outputHandler)
                .setSubmissionDirectory(FileUtils.getTempDirectory())
                .build();
        SystemCommand cmd = SystemCommand.newBuilder()
                .setCommand("bogus_as_hell")
                .build();
        cmd.run(PluginTest.pluginContext(evalContext));
    }

    @Test
    public void testWrongCommand() throws Exception {
        TestOutputHandler outputHandler = new TestOutputHandler();
        Evaluation.Context evalContext = EvaluationTest.testContextBuilder()
                .setOutputHandler(outputHandler)
                .setSubmissionDirectory(FileUtils.getUserDirectory())
                .build();
        SystemCommand cmd = SystemCommand.newBuilder()
                .setCommand("date")
                .addArg("-k")
                .build();
        SystemCommand.Output output = cmd.run(PluginTest.pluginContext(evalContext));
        Assert.assertTrue(output.getStatus() != 0);
        ImmutableList<TestOutputHandler.LogEntry> entries = outputHandler.entries();
        Assert.assertTrue(entries.size() > 0);
        Assert.assertEquals(TestOutputHandler.Level.ERROR, entries.get(0).level);
    }

    @Test
    public void testStdoutBuffering() throws Exception {
        Evaluation.Context evalContext = EvaluationTest.testContextBuilder()
                .setOutputHandler(new TestOutputHandler())
                .setSubmissionDirectory(FileUtils.getTempDirectory())
                .build();
        SystemCommand cmd = SystemCommand.newBuilder()
                .setCommand("date")
                .setBufferStdout(true)
                .build();
        SystemCommand.Output output = cmd.run(PluginTest.pluginContext(evalContext));
        Assert.assertEquals(0, output.getStatus());
        Assert.assertTrue(!output.getStdout().isEmpty());

        cmd = SystemCommand.newBuilder()
                .setCommand("date")
                .build();
        output = cmd.run(PluginTest.pluginContext(evalContext));
        Assert.assertEquals(0, output.getStatus());
        try {
            output.getStdout();
            Assert.fail("No error thrown for disabled buffering");
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    public void testStderrBuffering() throws Exception {
        Evaluation.Context evalContext = EvaluationTest.testContextBuilder()
                .setOutputHandler(new TestOutputHandler())
                .setSubmissionDirectory(FileUtils.getTempDirectory())
                .build();
        SystemCommand cmd = SystemCommand.newBuilder()
                .setCommand("date")
                .addArg("-k")
                .setBufferStderr(true)
                .build();
        SystemCommand.Output output = cmd.run(PluginTest.pluginContext(evalContext));
        Assert.assertTrue(output.getStatus() != 0);
        Assert.assertTrue(!output.getStderr().isEmpty());

        cmd = SystemCommand.newBuilder()
                .setCommand("date")
                .addArg("-k")
                .build();
        output = cmd.run(PluginTest.pluginContext(evalContext));
        Assert.assertTrue(output.getStatus() != 0);
        try {
            output.getStderr();
            Assert.fail("No error thrown for disabled buffering");
        } catch (IllegalStateException ignored) {
        }
    }

}
