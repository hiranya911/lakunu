package org.lakunu.labs.utils;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import junit.framework.Assert;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.io.FileUtils;
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
                .build();
        SystemCommand.Output output = cmd.run(handler);
        Assert.assertEquals(0, output.getStatus());
        ImmutableList<TestOutputHandler.LogEntry> entries = handler.entries();
        Assert.assertEquals(1, entries.size());
        TestOutputHandler.LogEntry entry = entries.get(0);
        Assert.assertEquals(TestOutputHandler.Level.INFO, entry.level);
        Assert.assertTrue(!Strings.isNullOrEmpty(entry.line));

        // check rerun
        output = cmd.run(handler);
        Assert.assertEquals(0, output.getStatus());
        entries = handler.entries();
        Assert.assertEquals(2, entries.size());
        entry = entries.get(1);
        Assert.assertEquals(TestOutputHandler.Level.INFO, entry.level);
        Assert.assertTrue(!Strings.isNullOrEmpty(entry.line));
    }

    @Test
    public void testWorkingDirectory() throws Exception {
        TestOutputHandler handler = new TestOutputHandler();
        SystemCommand cmd = SystemCommand.newBuilder()
                .setCommand("pwd")
                .build();
        SystemCommand.Output output = cmd.run(FileUtils.getUserDirectory(), handler);
        Assert.assertEquals(0, output.getStatus());
        ImmutableList<TestOutputHandler.LogEntry> entries = handler.entries();
        Assert.assertEquals(1, entries.size());
        TestOutputHandler.LogEntry entry = entries.get(0);
        Assert.assertEquals(TestOutputHandler.Level.INFO, entry.level);
        Assert.assertEquals(FileUtils.getUserDirectory().getAbsolutePath(), entry.line);
    }

    @Test(expected = IOException.class)
    public void testNonExistentCommand() throws Exception {
        TestOutputHandler handler = new TestOutputHandler();
        SystemCommand cmd = SystemCommand.newBuilder()
                .setCommand("bogus_as_hell")
                .build();
        cmd.run(handler);
    }

    @Test
    public void testWrongCommand() throws Exception {
        TestOutputHandler handler = new TestOutputHandler();
        SystemCommand cmd = SystemCommand.newBuilder()
                .setCommand("date")
                .addArgument("-k")
                .build();
        try {
            cmd.run(handler);
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
                .setBufferStdout(true)
                .build();
        SystemCommand.Output output = cmd.run(new TestOutputHandler());
        Assert.assertEquals(0, output.getStatus());
        Assert.assertFalse(output.getStdout().isEmpty());

        cmd = SystemCommand.newBuilder()
                .setCommand("date")
                .build();
        try {
            cmd.run(new TestOutputHandler()).getStdout();
            Assert.fail("No error thrown for invalid state");
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    public void testStderrBuffering() throws Exception {
        SystemCommand cmd = SystemCommand.newBuilder()
                .setCommand("ls")
                .addArgument("*.bogus")
                .setBufferStderr(true)
                .build();
        SystemCommand.Output output = cmd.run(new TestOutputHandler());
        Assert.assertTrue(output.getStatus() != 0);
        Assert.assertFalse(output.getStderr().isEmpty());

        cmd = SystemCommand.newBuilder()
                .setCommand("ls")
                .addArgument("*.bogus")
                .build();
        output = cmd.run(new TestOutputHandler());
        Assert.assertTrue(output.getStatus() != 0);
        try {
            output.getStderr();
            Assert.fail("No error thrown for invalid state");
        } catch (IllegalStateException ignored) {
        }
    }

}
