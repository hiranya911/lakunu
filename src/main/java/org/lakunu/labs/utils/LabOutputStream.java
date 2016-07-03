package org.lakunu.labs.utils;

import org.apache.commons.exec.LogOutputStream;
import org.lakunu.labs.LabOutputHandler;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Converts process output into line-oriented text, and passes along to
 * a LabOutputHandler for further processing.
 */
public final class LabOutputStream extends LogOutputStream {

    public static final int INFO = 0;
    public static final int ERROR = 1;

    private final LabOutputHandler outputHandler;

    private LabOutputStream(LabOutputHandler outputHandler, boolean stdout) {
        super(stdout ? INFO : ERROR);
        checkNotNull(outputHandler, "Output handler is required");
        this.outputHandler = outputHandler;
    }

    static LabOutputStream captureStdout(LabOutputHandler outputHandler) {
        return new LabOutputStream(outputHandler, true);
    }

    static LabOutputStream captureStderr(LabOutputHandler outputHandler) {
        return new LabOutputStream(outputHandler, false);
    }

    @Override
    protected void processLine(String line, int level) {
        if (level == INFO) {
            outputHandler.info(line);
        } else {
            outputHandler.error(line);
        }
    }
}
