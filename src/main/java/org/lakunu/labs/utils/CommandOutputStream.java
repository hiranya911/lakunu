package org.lakunu.labs.utils;

import org.apache.commons.exec.LogOutputStream;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.io.output.NullOutputStream;
import org.apache.commons.io.output.TeeOutputStream;
import org.apache.commons.io.output.ThresholdingOutputStream;
import org.lakunu.labs.LabOutputHandler;

import java.io.IOException;
import java.io.OutputStream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

final class CommandOutputStream extends TeeOutputStream {

    private static final int LEVEL_STDOUT = 0;
    private static final int LEVEL_STDERR = 1;

    private CommandOutputStream(LabOutputHandlerStream out, OutputStream branch) {
        super(out, branch);
    }

    public String getContent() {
        checkState(branch instanceof StringBufferOutputStream, "Buffering not enabled");
        return ((StringBufferOutputStream) branch).buffer.toString();
    }

    public static Factory withoutBuffering(boolean stdout, LabOutputHandler outputHandler) {
        return new Factory(stdout, outputHandler, false, 0);
    }

    public static Factory withBuffering(boolean stdout, LabOutputHandler outputHandler,
                                                    int threshold) {
        return new Factory(stdout, outputHandler, true, threshold);
    }

    static final class Factory {
        private final int level;
        private final LabOutputHandler outputHandler;
        private final boolean buffering;
        private final int bufferLimit;

        private Factory(boolean stdout, LabOutputHandler outputHandler, boolean buffering, int bufferLimit) {
            checkNotNull(outputHandler, "Output handler is required");
            checkArgument(bufferLimit >= 0, "Buffer limit cannot be negative");
            this.level = stdout ? LEVEL_STDOUT : LEVEL_STDERR;
            this.outputHandler = outputHandler;
            this.buffering = buffering;
            this.bufferLimit = bufferLimit;
        }

        CommandOutputStream build() {
            LabOutputHandlerStream primary = new LabOutputHandlerStream(level, outputHandler);
            OutputStream secondary;
            if (buffering) {
                secondary = new StringBufferOutputStream(bufferLimit);
            } else {
                secondary = NullOutputStream.NULL_OUTPUT_STREAM;
            }
            return new CommandOutputStream(primary, secondary);
        }
    }

    private static final class LabOutputHandlerStream extends LogOutputStream {

        private final LabOutputHandler outputHandler;

        private LabOutputHandlerStream(int level, LabOutputHandler outputHandler) {
            super(level);
            checkNotNull(outputHandler, "Output handler is required");
            this.outputHandler = outputHandler;
        }

        @Override
        protected void processLine(String line, int level) {
            if (level == LEVEL_STDOUT) {
                outputHandler.info(line);
            } else {
                outputHandler.error(line);
            }
        }
    }

    private static final class StringBufferOutputStream extends ThresholdingOutputStream {

        private final ByteArrayOutputStream buffer = new ByteArrayOutputStream(256);
        private OutputStream current;

        private StringBufferOutputStream(int threshold) {
            super(threshold);
            this.current = this.buffer;
        }

        @Override
        protected OutputStream getStream() throws IOException {
            return current;
        }

        @Override
        protected void thresholdReached() throws IOException {
            this.current = NullOutputStream.NULL_OUTPUT_STREAM;
        }

    }


}
