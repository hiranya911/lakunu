package org.lakunu.labs.ant;

import org.apache.commons.io.output.NullOutputStream;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.lakunu.labs.LabOutputHandler;

import java.io.PrintStream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class AntLabOutputHandler extends DefaultLogger {

    private final LabOutputHandler outputHandler;

    public AntLabOutputHandler(LabOutputHandler outputHandler, int level) {
        checkNotNull(outputHandler, "output handler is required");
        checkArgument(level >= Project.MSG_ERR && level <= Project.MSG_INFO, "Unsupported log level");
        this.setOutputPrintStream(new PrintStream(NullOutputStream.NULL_OUTPUT_STREAM));
        this.setErrorPrintStream(new PrintStream(NullOutputStream.NULL_OUTPUT_STREAM));
        this.outputHandler = outputHandler;
        this.msgOutputLevel = level;
    }

    @Override
    protected void printMessage(String msg, PrintStream stream, int priority) {
        switch (priority) {
            case Project.MSG_INFO:
            case Project.MSG_VERBOSE:
                outputHandler.info(msg);
                break;
            case Project.MSG_WARN:
                outputHandler.warn(msg);
                break;
            case Project.MSG_ERR:
                outputHandler.error(msg);
                break;
        }
    }
}
