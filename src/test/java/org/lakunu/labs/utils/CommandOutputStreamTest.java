package org.lakunu.labs.utils;

import com.google.common.collect.ImmutableList;
import junit.framework.Assert;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.lakunu.labs.TestOutputHandler;

import java.io.IOException;

public class CommandOutputStreamTest {

    public static final String[] LINES = new String[]{"Hello world", "line2"};

    @Test
    public void testNoBuffering() throws IOException {
        TestOutputHandler outputHandler = new TestOutputHandler();
        CommandOutputStream outputStream = CommandOutputStream.withoutBuffering(
                true, outputHandler).build();
        writeLines(outputStream);

        ImmutableList<TestOutputHandler.LogEntry> entries = outputHandler.entries();
        Assert.assertEquals(LINES.length, entries.size());
        for (int i = 0; i < LINES.length; i++) {
            Assert.assertEquals(LINES[i], entries.get(i).line);
            Assert.assertEquals(TestOutputHandler.Level.INFO, entries.get(i).level);
        }
        try {
            outputStream.getContent();
            Assert.fail("No error thrown from unbuffered stream");
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    public void testNoBufferingWithErrors() throws IOException {
        TestOutputHandler outputHandler = new TestOutputHandler();
        CommandOutputStream outputStream = CommandOutputStream.withoutBuffering(
                false, outputHandler).build();
        writeLines(outputStream);

        ImmutableList<TestOutputHandler.LogEntry> entries = outputHandler.entries();
        Assert.assertEquals(LINES.length, entries.size());
        for (int i = 0; i < LINES.length; i++) {
            Assert.assertEquals(LINES[i], entries.get(i).line);
            Assert.assertEquals(TestOutputHandler.Level.ERROR, entries.get(i).level);
        }
        try {
            outputStream.getContent();
            Assert.fail("No error thrown from unbuffered stream");
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    public void testBuffering() throws IOException {
        TestOutputHandler outputHandler = new TestOutputHandler();
        CommandOutputStream outputStream = CommandOutputStream.withBuffering(
                true, outputHandler, LINES[0].length()).build();
        writeLines(outputStream);

        ImmutableList<TestOutputHandler.LogEntry> entries = outputHandler.entries();
        Assert.assertEquals(LINES.length, entries.size());
        for (int i = 0; i < LINES.length; i++) {
            Assert.assertEquals(LINES[i], entries.get(i).line);
            Assert.assertEquals(TestOutputHandler.Level.INFO, entries.get(i).level);
        }
        String content = outputStream.getContent();
        Assert.assertEquals("Hello world", content);
    }

    @Test
    public void testBufferingWithErrors() throws IOException {
        TestOutputHandler outputHandler = new TestOutputHandler();
        CommandOutputStream outputStream = CommandOutputStream.withBuffering(
                false, outputHandler, LINES[0].length()).build();
        writeLines(outputStream);

        ImmutableList<TestOutputHandler.LogEntry> entries = outputHandler.entries();
        Assert.assertEquals(LINES.length, entries.size());
        for (int i = 0; i < LINES.length; i++) {
            Assert.assertEquals(LINES[i], entries.get(i).line);
            Assert.assertEquals(TestOutputHandler.Level.ERROR, entries.get(i).level);
        }
        String content = outputStream.getContent();
        Assert.assertEquals("Hello world", content);
    }

    private void writeLines(CommandOutputStream outputStream) throws IOException {
        try {
            for (String line : LINES) {
                outputStream.write(line.getBytes());
                outputStream.write('\n');
            }
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
    }

}
