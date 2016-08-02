package org.lakunu.labs.utils;

import org.lakunu.labs.LabOutputHandler;

import java.io.PrintWriter;

public final class BufferingOutputHandler implements LabOutputHandler {

    private final PrintWriter writer;
    private final ThresholdByteArrayOutputStream buffer;

    public BufferingOutputHandler(int limit) {
        this.buffer = new ThresholdByteArrayOutputStream(limit, false);
        this.writer = new PrintWriter(this.buffer, true);
    }

    @Override
    public synchronized void info(String msg) {
        writer.println(msg);
    }

    @Override
    public synchronized void warn(String msg) {
        writer.println(msg);
    }

    @Override
    public synchronized void error(String msg) {
        writer.println(msg);
    }

    public synchronized byte[] getBufferedOutput() {
        return buffer.toByteArray();
    }
}
