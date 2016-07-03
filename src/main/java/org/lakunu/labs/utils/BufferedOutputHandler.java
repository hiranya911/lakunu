package org.lakunu.labs.utils;

import com.google.common.collect.ImmutableList;
import org.lakunu.labs.LabOutputHandler;

import static com.google.common.base.Preconditions.checkNotNull;

public final class BufferedOutputHandler implements LabOutputHandler {

    private final LabOutputHandler outputHandler;
    private final ImmutableList.Builder<String> lines = ImmutableList.builder();

    public BufferedOutputHandler(LabOutputHandler outputHandler) {
        checkNotNull(outputHandler, "Output handler is required");
        this.outputHandler = outputHandler;
    }

    @Override
    public void info(String msg) {
        lines.add(msg);
        outputHandler.info(msg);
    }

    @Override
    public void warn(String msg) {
        lines.add(msg);
        outputHandler.warn(msg);
    }

    @Override
    public void error(String msg) {
        lines.add(msg);
        outputHandler.error(msg);
    }

    public ImmutableList<String> lines() {
        return lines.build();
    }
}
