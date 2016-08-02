package org.lakunu.labs.utils;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.io.output.NullOutputStream;
import org.apache.commons.io.output.ThresholdingOutputStream;

import java.io.IOException;
import java.io.OutputStream;

public final class ThresholdByteArrayOutputStream extends ThresholdingOutputStream {

    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private final boolean throwExceptionOnThreshold;

    private OutputStream stream = buffer;

    public ThresholdByteArrayOutputStream(int threshold, boolean throwExceptionOnThreshold) {
        super(threshold);
        this.throwExceptionOnThreshold = throwExceptionOnThreshold;
    }

    @Override
    protected OutputStream getStream() throws IOException {
        return stream;
    }

    @Override
    protected void thresholdReached() throws IOException {
        if (throwExceptionOnThreshold) {
            throw new IOException("Content larger than the threshold: " + getThreshold());
        } else {
            stream = new NullOutputStream();
        }
    }

    public byte[] toByteArray() {
        return buffer.toByteArray();
    }

}
