package org.lakunu.labs.utils;

import org.junit.Assert;
import org.junit.Test;

public class BufferingOutputHandlerTest {

    @Test
    public void testBuffering() {
        BufferingOutputHandler outputHandler = new BufferingOutputHandler(64);
        for (int i = 0; i < 2; i++) {
            outputHandler.info("Hello " + i);
        }
        byte[] data = outputHandler.getBufferedOutput();
        Assert.assertEquals("Hello 0\nHello 1\n", new String(data));
    }

    @Test
    public void testTruncation() {
        BufferingOutputHandler outputHandler = new BufferingOutputHandler(64);
        for (int i = 0; i < 10; i++) {
            outputHandler.info("1234567890");
        }
        byte[] data = outputHandler.getBufferedOutput();
        Assert.assertTrue(data.length <= 64);
    }

}
