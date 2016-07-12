package org.lakunu.labs.utils;

import com.google.common.base.Strings;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import static com.google.common.base.Preconditions.checkArgument;

public final class FileOutputHandler extends LoggingOutputHandler {

    private final PrintWriter writer;

    public FileOutputHandler(String path) throws IOException {
        checkArgument(!Strings.isNullOrEmpty(path), "File path is required");
        this.writer = new PrintWriter(new FileWriter(path));
    }

    @Override
    public void info(String msg) {
        super.info(msg);
        writer.println("[INFO] " + msg);
    }

    @Override
    public void warn(String msg) {
        super.warn(msg);
        writer.println("[WARN] " + msg);
    }

    @Override
    public void error(String msg) {
        super.error(msg);
        writer.println("[ERROR] " + msg);
    }

    @Override
    public void close() {
        super.close();
        writer.close();
    }
}
