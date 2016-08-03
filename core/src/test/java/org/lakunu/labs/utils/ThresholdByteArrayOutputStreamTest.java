package org.lakunu.labs.utils;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class ThresholdByteArrayOutputStreamTest {

    @Test
    public void testTruncation() throws Exception {
        ThresholdByteArrayOutputStream out = new ThresholdByteArrayOutputStream(16, false);
        for (int i = 0; i < 20; i++) {
            out.write('a');
        }
        byte[] data = out.toByteArray();
        Assert.assertEquals(16, data.length);
    }

    @Test
    public void testException() throws Exception {
        ThresholdByteArrayOutputStream out = new ThresholdByteArrayOutputStream(16, true);
        for (int i = 0; i < 16; i++) {
            out.write('a');
        }

        try {
            out.write('a');
            Assert.fail("No exception thrown for exceeding threshold");
        } catch (IOException ignored) {
        }
        byte[] data = out.toByteArray();
        Assert.assertEquals(16, data.length);
    }

}
