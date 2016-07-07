package org.lakunu.labs.utils;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import junit.framework.Assert;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.lakunu.labs.TestOutputHandler;

import java.io.IOException;

public class SystemCommandTest {

    @Before
    public void unixOnly() {
        Assume.assumeTrue(SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_UNIX);
    }

    @Test
    public void testSimpleCommand() throws Exception {
        TestOutputHandler handler = new TestOutputHandler();
        SystemCommand cmd = SystemCommand.newBuilder()
                .setCommand("date")
                .setOutputHandler(handler)
                .build();
        SystemCommand.Output output = cmd.run();
        Assert.assertEquals(0, output.getStatus());
        ImmutableList<TestOutputHandler.LogEntry> entries = handler.entries();
        Assert.assertEquals(1, entries.size());
        TestOutputHandler.LogEntry entry = entries.get(0);
        Assert.assertEquals(TestOutputHandler.Level.INFO, entry.level);
        Assert.assertTrue(!Strings.isNullOrEmpty(entry.line));
    }

    @Test(expected = IOException.class)
    public void testNonExistentCommand() throws Exception {
        TestOutputHandler handler = new TestOutputHandler();
        SystemCommand cmd = SystemCommand.newBuilder()
                .setCommand("bogus_as_hell")
                .setOutputHandler(handler)
                .build();
        cmd.run();
    }

    @Test
    public void testWrongCommand() throws Exception {
        TestOutputHandler handler = new TestOutputHandler();
        SystemCommand cmd = SystemCommand.newBuilder()
                .setCommand("date")
                .addArgument("-k")
                .setOutputHandler(handler)
                .build();
        try {
            cmd.run();
        } catch (ExecuteException ignored) {
        }
        ImmutableList<TestOutputHandler.LogEntry> entries = handler.entries();
        Assert.assertTrue(entries.size() > 0);
        entries.forEach(e -> Assert.assertEquals(TestOutputHandler.Level.ERROR, e.level));
    }

    @Test
    public void testStdoutBuffering() throws Exception {
        SystemCommand cmd = SystemCommand.newBuilder()
                .setCommand("date")
                .setOutputHandler(new TestOutputHandler())
                .setBufferStdout(true)
                .build();
        SystemCommand.Output output = cmd.run();
        Assert.assertEquals(0, output.getStatus());
        Assert.assertFalse(output.getStdout().isEmpty());

        cmd = SystemCommand.newBuilder()
                .setCommand("date")
                .setOutputHandler(new TestOutputHandler())
                .build();
        try {
            cmd.run().getStdout();
            Assert.fail("No error thrown for invalid state");
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    public void testStderrBuffering() throws Exception {
        SystemCommand cmd = SystemCommand.newBuilder()
                .setCommand("ls")
                .addArgument("*.bogus")
                .setOutputHandler(new TestOutputHandler())
                .setBufferStderr(true)
                .build();
        SystemCommand.Output output = cmd.run();
        Assert.assertTrue(output.getStatus() != 0);
        Assert.assertFalse(output.getStderr().isEmpty());

        cmd = SystemCommand.newBuilder()
                .setCommand("ls")
                .addArgument("*.bogus")
                .setOutputHandler(new TestOutputHandler())
                .build();
        output = cmd.run();
        Assert.assertTrue(output.getStatus() != 0);
        try {
            output.getStderr();
            Assert.fail("No error thrown for invalid state");
        } catch (IllegalStateException ignored) {
        }
    }

}
