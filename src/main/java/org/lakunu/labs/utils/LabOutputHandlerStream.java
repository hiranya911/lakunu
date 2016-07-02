package org.lakunu.labs.utils;

import org.apache.commons.exec.LogOutputStream;
import org.lakunu.labs.LabOutputHandler;

import static com.google.common.base.Preconditions.checkNotNull;

public final class LabOutputHandlerStream extends LogOutputStream {

    public static final int INFO = 0;
    public static final int ERROR = 1;

    private final LabOutputHandler outputHandler;

    private LabOutputHandlerStream(LabOutputHandler outputHandler, boolean stdout) {
        super(stdout ? INFO : ERROR);
        checkNotNull(outputHandler, "Output handler is required");
        this.outputHandler = outputHandler;
    }

    public static LabOutputHandlerStream infoLogger(LabOutputHandler outputHandler) {
        return new LabOutputHandlerStream(outputHandler, true);
    }

    public static LabOutputHandlerStream errorLogger(LabOutputHandler outputHandler) {
        return new LabOutputHandlerStream(outputHandler, false);
    }

    @Override
    protected void processLine(String line, int level) {
        LabOutputHandler.Level outputLevel = level == INFO ?
                LabOutputHandler.Level.INFO : LabOutputHandler.Level.ERROR;
        outputHandler.processLine(line, outputLevel);
    }
}
