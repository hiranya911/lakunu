package org.lakunu.labs.utils;

import com.google.common.collect.ImmutableList;
import junit.framework.Assert;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.lakunu.labs.TestOutputHandler;

import java.io.IOException;

public class CommandOutputStreamTest {

    @Test
    public void testNoBuffering() throws IOException {
        String[] lines = { "Hello world", "line2" };
        TestOutputHandler outputHandler = new TestOutputHandler();
        CommandOutputStream outputStream = CommandOutputStream.withoutBuffering(true, outputHandler);
        try {
            for (String line : lines) {
                outputStream.write(line.getBytes());
                outputStream.write('\n');
            }
        } finally {
            IOUtils.closeQuietly(outputStream);
        }

        ImmutableList<TestOutputHandler.LogEntry> entries = outputHandler.entries();
        Assert.assertEquals(lines.length, entries.size());
        for (int i = 0; i < lines.length; i++) {
            Assert.assertEquals(lines[i], entries.get(i).line);
        }
        try {
            outputStream.getContent();
            Assert.fail("No error thrown from unbuffered stream");
        } catch (IllegalStateException ignored) {
        }
    }

}
