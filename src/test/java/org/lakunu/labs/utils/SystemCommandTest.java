package org.lakunu.labs.utils;

import junit.framework.Assert;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.LogOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.NullOutputStream;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.lakunu.labs.TestOutputHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SystemCommandTest {

    @Before
    public void unixOnly() {
        Assume.assumeTrue(SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_UNIX);
    }

    @Test
    public void testSimpleCommand() throws Exception {
        TestOutputStream out = new TestOutputStream();
        TestOutputStream err = new TestOutputStream();
        SystemCommand cmd = SystemCommand.newBuilder()
                .setCommand("date")
                .build();
        int status = cmd.run(out, err);
        Assert.assertEquals(0, status);
        Assert.assertEquals(1, out.entries.size());
        Assert.assertEquals(0, err.entries.size());

        // check rerun
        status = cmd.run(out, err);
        Assert.assertEquals(0, status);
        Assert.assertEquals(2, out.entries.size());
        Assert.assertEquals(0, err.entries.size());
    }

    @Test
    public void testWorkingDirectory() throws Exception {
        TestOutputStream out = new TestOutputStream();
        TestOutputStream err = new TestOutputStream();
        SystemCommand cmd = SystemCommand.newBuilder()
                .setCommand("pwd")
                .setWorkingDirectory(FileUtils.getUserDirectory())
                .build();
        int status = cmd.run(out, err);
        Assert.assertEquals(0, status);
        Assert.assertEquals(1, out.entries.size());
        String entry = out.entries.get(0);
        Assert.assertEquals(FileUtils.getUserDirectory().getAbsolutePath(), entry);
    }

    @Test(expected = IOException.class)
    public void testNonExistentCommand() throws Exception {
        SystemCommand cmd = SystemCommand.newBuilder()
                .setCommand("bogus_as_hell")
                .build();
        cmd.run(NullOutputStream.NULL_OUTPUT_STREAM, NullOutputStream.NULL_OUTPUT_STREAM);
    }

    @Test
    public void testWrongCommand() throws Exception {
        TestOutputStream out = new TestOutputStream();
        TestOutputStream err = new TestOutputStream();
        SystemCommand cmd = SystemCommand.newBuilder()
                .setCommand("date")
                .addArgument("-k")
                .build();
        try {
            cmd.run(out, err);
        } catch (ExecuteException ignored) {
        }
        Assert.assertTrue(err.entries.size() > 0);
    }

    @Test
    public void testStdoutBuffering() throws Exception {
        CommandOutputStream out = CommandOutputStream.withBuffering(true, 100)
                .build(new TestOutputHandler());
        SystemCommand cmd = SystemCommand.newBuilder()
                .setCommand("date")
                .build();
        int status = cmd.run(out, NullOutputStream.NULL_OUTPUT_STREAM);
        Assert.assertEquals(0, status);
        Assert.assertFalse(out.getContent().isEmpty());
    }

    @Test
    public void testStderrBuffering() throws Exception {
        CommandOutputStream err = CommandOutputStream.withBuffering(false, 100)
                .build(new TestOutputHandler());
        SystemCommand cmd = SystemCommand.newBuilder()
                .setCommand("ls")
                .addArgument("*.bogus")
                .build();
        int status = cmd.run(NullOutputStream.NULL_OUTPUT_STREAM, err);
        Assert.assertTrue(status != 0);
        Assert.assertFalse(err.getContent().isEmpty());
    }

    private static class TestOutputStream extends LogOutputStream {

        private final List<String> entries = new ArrayList<>();

        @Override
        protected void processLine(String s, int i) {
            entries.add(s);
        }
    }

}
