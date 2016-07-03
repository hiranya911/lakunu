package org.lakunu.labs.utils;

import com.google.common.base.Strings;
import junit.framework.Assert;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.lakunu.labs.LabOutputHandler;

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
        TestOutputHandler handler = new TestOutputHandler();
        SystemCommand cmd = SystemCommand.newBuilder()
                .setCommand("date")
                .setOutputHandler(handler)
                .build();
        Assert.assertEquals(0, cmd.run());
        Assert.assertEquals(1, handler.entries.size());
        LogEntry entry = handler.entries.get(0);
        Assert.assertEquals(Level.INFO, entry.level);
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
        Assert.assertTrue(handler.entries.size() > 0);
        handler.entries.forEach(e -> Assert.assertEquals(Level.ERROR, e.level));
    }

    static class LogEntry {
        private final String line;
        private final Level level;

        LogEntry(String line, Level level) {
            this.line = line;
            this.level = level;
        }
    }

    static class TestOutputHandler implements LabOutputHandler {

        private final List<LogEntry> entries = new ArrayList<>();

        public void info(String line) {
            entries.add(new LogEntry(line, Level.INFO));
        }

        public void warn(String line) {
            entries.add(new LogEntry(line, Level.WARN));
        }

        public void error(String line) {
            entries.add(new LogEntry(line, Level.ERROR));
        }
    }

    enum Level {
        INFO,
        WARN,
        ERROR
    }

}
